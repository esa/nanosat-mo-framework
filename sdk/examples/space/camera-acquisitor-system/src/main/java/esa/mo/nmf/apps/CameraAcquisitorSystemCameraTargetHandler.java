/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.nmf.apps;

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.util.PriorityQueue;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeTargetTracking;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeTargetTrackingLinear;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

/**
 * Class handling acquisition of targets and the corresponding actions
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class CameraAcquisitorSystemCameraTargetHandler
{

  public static final String ACTION_PHOTOGRAPH_LOCATION = "photographLocation";
  public static final int PHOTOGRAPH_LOCATION_STAGES = 6;
  private static final int STAGE_CALCULATE_CURRENT_ORBIT = 1;
  private static final int STAGE_PREDICT_PASS = 2;
  private static final int STAGE_WAIT_FOR_BEGIN_PASS = 3;
  private static final int STAGE_ATTITUDE_CORECTION = 4;
  private static final int STAGE_WAIT_FOR_OPTIMAL_PASS = 5;
  private static final int STAGE_TAKE_PHOTOGRAPH = 6;

  public static final String ACTION_PHOTOGRAPH_LOCATION_MANUAL = "photographLocationManual";
  public static final int PHOTOGRAPH_LOCATION_MANUAL_STAGES =
      3 + CameraAcquisitorSystemCameraHandler.PHOTOGRAPH_NOW_STAGES;
  private static final int STAGE_MANUAL_WAIT_FOR_PASS = 1;
  private static final int STAGE_MANUAL_ATTITUDE_CORECTION = 2;
  private static final int STAGE_MANUAL_WAIT_FOR_OPTIMAL_PASS = 3;

  private CameraAcquisitorSystemMCAdapter casMCAdapter;

  private static final Logger LOGGER = Logger.getLogger(
      CameraAcquisitorSystemCameraTargetHandler.class.getName());

  // time to keep track of all sheduled photographs
  private final java.util.Timer timer = new java.util.Timer();

  //queue to save all target locations
  PriorityQueue<CameraAcquisitorSystemTargetLocation> locationQueue =
      new PriorityQueue<>();

  public CameraAcquisitorSystemCameraTargetHandler(CameraAcquisitorSystemMCAdapter casMCAdapter)
  {
    this.casMCAdapter = casMCAdapter;
  }

  /**
   * Registers all target related actions
   *
   * @param registration
   */
  static void registerActions(MCRegistration registration)
  {
    ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
    IdentifierList actionNames = new IdentifierList();

    ArgumentDefinitionDetailsList argumentsPhotographLocation = new ArgumentDefinitionDetailsList();
    {
      byte rawType = Attribute._DOUBLE_TYPE_SHORT_FORM;
      String rawUnit = "degree";
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;
      argumentsPhotographLocation.add(new ArgumentDefinitionDetails(
          new Identifier("targetLongitude"), null, rawType, rawUnit,
          conditionalConversions, convertedType, convertedUnit));
      argumentsPhotographLocation.add(new ArgumentDefinitionDetails(
          new Identifier("targetLatitude"), null, rawType, rawUnit,
          conditionalConversions, convertedType, convertedUnit));
      argumentsPhotographLocation.add(new ArgumentDefinitionDetails(
          new Identifier("maximumAngleToTarget"), null, rawType, rawUnit,
          conditionalConversions, convertedType, convertedUnit));
    }
    {
      byte rawType = Attribute._UINTEGER_TYPE_SHORT_FORM;
      String rawUnit = null;
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;
      argumentsPhotographLocation.add(new ArgumentDefinitionDetails(
          new Identifier("timeOfPhotograph"), null, rawType, rawUnit,
          conditionalConversions, convertedType, convertedUnit));
    }

    ActionDefinitionDetails actionDefTakePhotograpOfLocation = new ActionDefinitionDetails(
        "queues a new photograph target",
        new UOctet((short) 0), new UShort(PHOTOGRAPH_LOCATION_STAGES), argumentsPhotographLocation);

    actionDefs.add(actionDefTakePhotograpOfLocation);
    actionNames.add(new Identifier(ACTION_PHOTOGRAPH_LOCATION));

    //
    ArgumentDefinitionDetailsList argumentsPhotographLocationManual =
        new ArgumentDefinitionDetailsList();
    {
      byte rawType = Attribute._DOUBLE_TYPE_SHORT_FORM;
      String rawUnit = "degree";
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;
      argumentsPhotographLocationManual.add(new ArgumentDefinitionDetails(
          new Identifier("targetLongitude"), null, rawType, rawUnit,
          conditionalConversions, convertedType, convertedUnit));
      argumentsPhotographLocationManual.add(new ArgumentDefinitionDetails(
          new Identifier("targetLatitude"), null, rawType, rawUnit,
          conditionalConversions, convertedType, convertedUnit));
    }
    {
      byte rawType = Attribute._STRING_TYPE_SHORT_FORM;
      String rawUnit = null;
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;
      argumentsPhotographLocationManual.add(new ArgumentDefinitionDetails(
          new Identifier("timeStemp"), null, rawType, rawUnit,
          conditionalConversions, convertedType, convertedUnit));
    }

    ActionDefinitionDetails actionDefTakePhotograpManual = new ActionDefinitionDetails(
        "queues a new photograph target at the Specified Timestemp",
        new UOctet((short) 0), new UShort(PHOTOGRAPH_LOCATION_MANUAL_STAGES),
        argumentsPhotographLocationManual);

    actionDefs.add(actionDefTakePhotograpManual);
    actionNames.add(new Identifier(ACTION_PHOTOGRAPH_LOCATION_MANUAL));

    registration.registerActions(actionNames, actionDefs);

  }

  UInteger photographLocationManual(AttributeValueList attributeValues, Long actionInstanceObjId,
      boolean reportProgress, MALInteraction interaction)
  {
    // get parameters
    Double longitude = HelperAttributes.attribute2double(attributeValues.get(0).getValue());
    Double latitude = HelperAttributes.attribute2double(attributeValues.get(1).getValue());
    String timeStemp =
        HelperAttributes.attribute2JavaType(attributeValues.get(2).getValue()).toString();
    //

    AbsoluteDate targetDate = new AbsoluteDate(timeStemp, TimeScalesFactory.getUTC());

    double seconds = targetDate.durationFrom(CameraAcquisitorSystemMCAdapter.getNow());

    // create TimerTask to shedule attitude correction shortly before targetDate
    TimerTask task = new java.util.TimerTask()
    {
      @Override
      public void run()
      {
        try {
          casMCAdapter.getConnector().reportActionExecutionProgress(true, 0,
              STAGE_MANUAL_WAIT_FOR_PASS,
              PHOTOGRAPH_LOCATION_MANUAL_STAGES,
              actionInstanceObjId);
        } catch (NMFException ex) {
          Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
              Level.SEVERE,
              null, ex);
        }

        Duration timeTillPhotograph = new Duration(
            targetDate.durationFrom(CameraAcquisitorSystemMCAdapter.getNow()));

        // set desired attitude using target latitude and longitude
        AttitudeMode desiredAttitude = new AttitudeModeTargetTracking(
            longitude.floatValue(),
            latitude.floatValue());

        try {
          casMCAdapter.getConnector().getPlatformServices().getAutonomousADCSService()
              .setDesiredAttitude(
                  new Duration(
                      timeTillPhotograph.getValue() + casMCAdapter.getAttitudeSaftyMarginSeconds()),
                  desiredAttitude);

          casMCAdapter.getConnector().reportActionExecutionProgress(true, 0,
              STAGE_MANUAL_ATTITUDE_CORECTION,
              PHOTOGRAPH_LOCATION_MANUAL_STAGES,
              actionInstanceObjId);
          LOGGER.log(Level.INFO, "Attitude Correction Running");
        } catch (NMFException | IOException | MALInteractionException | MALException ex) {
          Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
              Level.SEVERE,
              null, ex);
        }

        // wait again till optimal moment for photograph
        try {
          double fractSeconds = timeTillPhotograph.getValue();
          long seconds = (long) fractSeconds;
          long milliSeconds = (long) ((fractSeconds - seconds) * 1000);

          TimeUnit.SECONDS.sleep(seconds);
          TimeUnit.MILLISECONDS.sleep(milliSeconds);
        } catch (InterruptedException ex) {
          Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
              Level.SEVERE,
              ex.getMessage());
        }

        try {
          casMCAdapter.getConnector().reportActionExecutionProgress(true, 0,
              STAGE_MANUAL_WAIT_FOR_OPTIMAL_PASS,
              PHOTOGRAPH_LOCATION_MANUAL_STAGES,
              actionInstanceObjId);
          LOGGER.log(Level.INFO, "Finished waiting for Pass");

          // trigger photograph
          LOGGER.log(Level.INFO, "Taking Photograph now");
          casMCAdapter.getCameraHandler().takePhotograph(actionInstanceObjId,
              STAGE_MANUAL_WAIT_FOR_OPTIMAL_PASS, PHOTOGRAPH_LOCATION_MANUAL_STAGES, "");

        } catch (NMFException | IOException | MALInteractionException | MALException ex) {
          Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
              Level.SEVERE,
              ex.getMessage());
        }
      }
    };

    this.timer.schedule(
        task,
        ((long) seconds) * 1000 - this.casMCAdapter.getWorstCaseRotationTimeMS() //conversion to milliseconds
    );
    LOGGER.log(Level.INFO, "Starting Timer for Photograph, Number of Seconds: {0}", seconds);

    return new UInteger(0);
  }

  UInteger photographLocation(AttributeValueList attributeValues, Long actionInstanceObjId,
      boolean reportProgress, MALInteraction interaction)
  {
    //get parameters
    Double longitude = HelperAttributes.attribute2double(attributeValues.get(0).getValue());
    Double latitude = HelperAttributes.attribute2double(attributeValues.get(1).getValue());
    Double maxAngle = HelperAttributes.attribute2double(attributeValues.get(2).getValue());

    CameraAcquisitorSystemTargetLocation.TimeModeEnum timeType =
        CameraAcquisitorSystemTargetLocation.TimeModeEnum.ANY;

    try {
      //parse TimeModeEnum
      int value = (int) ((UInteger) attributeValues.get(3).getValue()).getValue();
      timeType = CameraAcquisitorSystemTargetLocation.TimeModeEnum.values()[value];
    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, "Invalid timeOfPhotograph! \n {0}", e);
    }
    LOGGER.log(Level.INFO, "{0}:", ACTION_PHOTOGRAPH_LOCATION);
    LOGGER.log(Level.INFO, "  Longitude: {0}", longitude);
    LOGGER.log(Level.INFO, "   Latitude: {0}", latitude);
    LOGGER.log(Level.INFO, "  max Angle: {0}", maxAngle);
    LOGGER.log(Level.INFO, "  time type: {0}", timeType.name());

    // ------------------ add Location to queue ------------------
    TLE tle = this.casMCAdapter.getGpsHandler().getTLE();

    try {
      this.casMCAdapter.getConnector().reportActionExecutionProgress(true, 0,
          STAGE_CALCULATE_CURRENT_ORBIT,
          PHOTOGRAPH_LOCATION_STAGES,
          actionInstanceObjId);
      LOGGER.log(Level.INFO, "Current Orbit: {0}", tle.toString());
    } catch (NMFException ex) {
      Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(Level.SEVERE,
          ex.getMessage());
    }

    try {
      CameraAcquisitorSystemTargetLocation location =
          new CameraAcquisitorSystemTargetLocation(
              longitude, latitude, maxAngle, timeType, tle, this.casMCAdapter);

      Stream<CameraAcquisitorSystemTargetLocation> sceduleCollisions = locationQueue.stream();
      for (int i = 0; i < casMCAdapter.getMaxRetrys(); i++) {
        //count collisions
        final CameraAcquisitorSystemTargetLocation tmpLocation = location;
        sceduleCollisions = locationQueue.stream().filter(
            (otherLocation) -> (Math.abs(otherLocation.getOptimalTime().durationFrom(
                tmpLocation.getOptimalTime())) < this.casMCAdapter.getWorstCaseRotationTimeSeconds()));
        if (sceduleCollisions.count() == 0) {
          //if collisions == 0 stop serching for new pass
          break;
        } else {
          location =
              new CameraAcquisitorSystemTargetLocation(
                  longitude, latitude, maxAngle, timeType, tle, this.casMCAdapter);
        }
      }

      // if we could not scedule after maxRetrys, stop the scedule process.
      if (sceduleCollisions.count() != 0) {
        this.casMCAdapter.getConnector().reportActionExecutionProgress(false, 0,
            STAGE_CALCULATE_CURRENT_ORBIT,
            PHOTOGRAPH_LOCATION_STAGES,
            actionInstanceObjId);
        return new UInteger(0);
      }

      locationQueue.add(location);

      if (location.getOptimalTime() == null) {
        LOGGER.log(Level.SEVERE, "Target will not be Reached in maximal calculation time frame!");
        this.casMCAdapter.getConnector().reportActionExecutionProgress(false, 0,
            STAGE_PREDICT_PASS,
            PHOTOGRAPH_LOCATION_STAGES,
            actionInstanceObjId);
        return new UInteger(0);
      } else {
        this.casMCAdapter.getConnector().reportActionExecutionProgress(true, 0,
            STAGE_PREDICT_PASS,
            PHOTOGRAPH_LOCATION_STAGES,
            actionInstanceObjId);
      }

      double seconds = location.getOptimalTime().durationFrom(
          CameraAcquisitorSystemMCAdapter.getNow());

      LOGGER.log(Level.INFO, "Seconds till photograph: {0}", seconds);
      final CameraAcquisitorSystemTargetLocation sceduledLocation = location;
      TimerTask task = new java.util.TimerTask()
      {
        @Override
        public void run()
        {
          String error_information = "";
          try {
            // re-evaluate nearest Overpass for more accuracy
            sceduledLocation.calculateTimeFrame(casMCAdapter.getGpsHandler().getTLE(), casMCAdapter);
          } catch (Exception ex) {
            Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                Level.SEVERE,
                ex.getMessage());
            error_information += "__PRECICION-CALCULATION-FAIL";
          }
          // calculate Attitude
          AttitudeMode desiredAttitude = new AttitudeModeTargetTrackingLinear(Float.MIN_NORMAL,
              Float.NaN, Float.NaN, Float.NaN, Long.MAX_VALUE, Long.MIN_VALUE);

          Duration timeTillPhotograph = new Duration(sceduledLocation.getOptimalTime().durationFrom(
              CameraAcquisitorSystemMCAdapter.getNow()));

          try {
            //set Attitude
            casMCAdapter.getConnector().getPlatformServices().getAutonomousADCSService().setDesiredAttitude(
                new Duration(
                    timeTillPhotograph.getValue() + casMCAdapter.getAttitudeSaftyMarginSeconds()),
                desiredAttitude);
          } catch (MALException | NMFException | IOException | MALInteractionException ex) {
            Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                Level.SEVERE,
                ex.getMessage());
            error_information += "__ATTITUDE-CORECTION-FAIL";
          }

          // wait again
          try {
            double fractSeconds = timeTillPhotograph.getValue();
            long seconds = (long) fractSeconds;
            long milliSeconds = (long) ((fractSeconds - seconds) * 1000);
            TimeUnit.SECONDS.sleep(seconds);
            TimeUnit.MILLISECONDS.sleep(milliSeconds);
          } catch (InterruptedException ex) {
            Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                Level.SEVERE,
                ex.getMessage());
            error_information += "__SLEEP-FAIL";
          }

          try {
            // trigger photograph
            casMCAdapter.getCameraHandler().takePhotograph(actionInstanceObjId,
                PHOTOGRAPH_LOCATION_STAGES, PHOTOGRAPH_LOCATION_STAGES, error_information);
          } catch (NMFException ex) {
            Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                Level.SEVERE,
                ex.getMessage());
          } catch (IOException ex) {
            Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                Level.SEVERE,
                ex.getMessage());
          } catch (MALInteractionException ex) {
            Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                Level.SEVERE,
                ex.getMessage());
          } catch (MALException ex) {
            Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                Level.SEVERE,
                ex.getMessage());
          }

          this.cancel();
        }
      };

      location.setTask(task);

      this.timer.schedule(
          task,
          ((long) seconds) * 1000 - this.casMCAdapter.getWorstCaseRotationTimeMS() * 2 //conversion to milliseconds //Worst time * 2 just to be save.
      );

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
      return new UInteger(0); //TODO check posible error codes
    }

    return new UInteger(1);
  }
}
