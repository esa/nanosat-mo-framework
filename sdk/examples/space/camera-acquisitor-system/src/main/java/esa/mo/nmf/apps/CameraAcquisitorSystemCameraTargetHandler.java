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
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeTargetTrackingLinear;
import org.orekit.orbits.Orbit;

/**
 * Class handling acquisition of targets and the corresponding actions
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class CameraAcquisitorSystemCameraTargetHandler
{

  //TODO set corect number as soon as stages are defined
  private static final int PHOTOGRAPH_LOCATION_STAGES = 1;

  private static final int STAGE_CALCULATE_CURRENT_ORBIT = 1;
  private static final int STAGE_PREDICT_PASS = 2;
  private static final int STAGE_WAIT_FOR_BEGIN_PASS = 3;
  private static final int STAGE_ATTITUDE_CORECTION = 4;
  private static final int STAGE_WAIT_FOR_OPTIMAL_PASS = 5;
  private static final int STAGE_TAKE_PHOTOGRAPH = 6;

  public static final String ACTION_PHOTOGRAPH_LOCATION = "photographLocation";

  private CameraAcquisitorSystemMCAdapter casMCAdapter;

  private static final Logger LOGGER = Logger.getLogger(
      CameraAcquisitorSystemCameraTargetHandler.class.getName());

  //queue to save all target locations
  PriorityQueue<CameraAcquisitorSystemTargetLocation> locationQueue =
      new PriorityQueue<CameraAcquisitorSystemTargetLocation>();

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

    registration.registerActions(actionNames, actionDefs);

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
    Orbit currentOrbit = this.casMCAdapter.getGpsHandler().getCurrentOrbit();

    try {
      this.casMCAdapter.getConnector().reportActionExecutionProgress(true, 0,
          STAGE_CALCULATE_CURRENT_ORBIT,
          PHOTOGRAPH_LOCATION_STAGES,
          actionInstanceObjId);
      LOGGER.log(Level.INFO, "Current Orbit: {0}", currentOrbit.toString());
    } catch (NMFException ex) {
      Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(Level.SEVERE,
          ex.getMessage());
    }

    try {
      CameraAcquisitorSystemTargetLocation location =
          new CameraAcquisitorSystemTargetLocation(
              longitude, latitude, maxAngle, timeType, currentOrbit, this.casMCAdapter);
      locationQueue.add(location);

      if (location.getOptimalTime() == null) {
        LOGGER.log(Level.SEVERE, "Target will not be Reached in maximal calculation time frame!");
        this.casMCAdapter.getConnector().reportActionExecutionProgress(false, 0, STAGE_PREDICT_PASS,
            PHOTOGRAPH_LOCATION_STAGES,
            actionInstanceObjId);
      } else {
        this.casMCAdapter.getConnector().reportActionExecutionProgress(true, 0,
            STAGE_PREDICT_PASS,
            PHOTOGRAPH_LOCATION_STAGES,
            actionInstanceObjId);
      }

      double seconds = location.getOptimalTime().durationFrom(
          CameraAcquisitorSystemMCAdapter.getNow());

      LOGGER.log(Level.INFO, "Seconds till photograph: {0}", seconds);

      new java.util.Timer().schedule(
          new java.util.TimerTask()
      {
        @Override
        public void run()
        {
          String error_information = "";
          try {
            // re-evaluate nearest Overpass for more accuracy
            location.calculateTimeFrame(casMCAdapter.getGpsHandler().getCurrentOrbit(),
                casMCAdapter);
          } catch (Exception ex) {
            Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                Level.SEVERE,
                ex.getMessage());
            error_information += "__PRECICION-CALCULATION-FAIL";
          }
          // calculate Attitude
          AttitudeMode desiredAttitude = new AttitudeModeTargetTrackingLinear(Float.MIN_NORMAL,
              Float.NaN, Float.NaN, Float.NaN, Long.MAX_VALUE, Long.MIN_VALUE);

          Duration timeTillPhotograph = new Duration(location.getOptimalTime().durationFrom(
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
      },
          ((long) seconds) * 1000 - this.casMCAdapter.getWorstCaseRotationTimeMS() * 2 //conversion to milliseconds //Worst time * 2 just to be save.
      );

    } catch (Exception e) {
      LOGGER.log(Level.SEVERE, e.getMessage());
      return new UInteger(0); //TODO check posible error codes
    }

    return new UInteger(1);
  }
}
