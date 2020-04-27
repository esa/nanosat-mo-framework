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
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.provider.QueryInteraction;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeTargetTracking;
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
  public static final int PHOTOGRAPH_LOCATION_STAGES =
      3 + CameraAcquisitorSystemCameraHandler.PHOTOGRAPH_NOW_STAGES;
  private static final int STAGE_WAIT_FOR_PASS = 1;
  private static final int STAGE_ATTITUDE_CORECTION = 2;
  private static final int STAGE_WAIT_FOR_OPTIMAL_PASS = 3;

  private CameraAcquisitorSystemMCAdapter casMCAdapter;

  private static final Logger LOGGER = Logger.getLogger(
      CameraAcquisitorSystemCameraTargetHandler.class.getName());

  // time to keep track of all sheduled photographs
  private final java.util.Timer timer = new java.util.Timer();

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

    ArgumentDefinitionDetailsList argumentsPhotographLocation =
        new ArgumentDefinitionDetailsList();
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
    }
    {
      byte rawType = Attribute._STRING_TYPE_SHORT_FORM;
      String rawUnit = null;
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;
      argumentsPhotographLocation.add(new ArgumentDefinitionDetails(
          new Identifier("timeStemp"), null, rawType, rawUnit,
          conditionalConversions, convertedType, convertedUnit));
    }

    ActionDefinitionDetails actionDefTakePhotograp = new ActionDefinitionDetails(
        "queues a new photograph target at the Specified Timestemp",
        new UOctet((short) 0), new UShort(PHOTOGRAPH_LOCATION_STAGES),
        argumentsPhotographLocation);

    actionDefs.add(actionDefTakePhotograp);
    actionNames.add(new Identifier(ACTION_PHOTOGRAPH_LOCATION));

    registration.registerActions(actionNames, actionDefs);

  }

  UInteger photographLocation(AttributeValueList attributeValues, Long actionInstanceObjId,
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
              STAGE_WAIT_FOR_PASS,
              PHOTOGRAPH_LOCATION_STAGES,
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
              STAGE_ATTITUDE_CORECTION,
              PHOTOGRAPH_LOCATION_STAGES,
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
              STAGE_WAIT_FOR_OPTIMAL_PASS,
              PHOTOGRAPH_LOCATION_STAGES,
              actionInstanceObjId);
          LOGGER.log(Level.INFO, "Finished waiting for Pass");

          // trigger photograph
          LOGGER.log(Level.INFO, "Taking Photograph now");
          casMCAdapter.getCameraHandler().takePhotograph(actionInstanceObjId,
              STAGE_WAIT_FOR_OPTIMAL_PASS, PHOTOGRAPH_LOCATION_STAGES, "");

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

  /**
   * recovers all scheduled photographs in case of a crash or a reboot of the system.
   */
  void recoverLastState()
  {
    // get previous requests
    ArchiveQueryList archiveQueryList = new ArchiveQueryList();
    ArchiveQuery archiveQuery = new ArchiveQuery();

    archiveQuery.setDomain(null);
    archiveQuery.setNetwork(null);
    archiveQuery.setProvider(null);
    archiveQuery.setRelated(new Long(0));
    archiveQuery.setSource(null);
    archiveQuery.setStartTime(null);
    archiveQuery.setEndTime(null);
    archiveQuery.setSortFieldName(null);

    archiveQueryList.add(archiveQuery);

    SchedulerArchiveAdapter archiveAdapter = new SchedulerArchiveAdapter();

    // query all necessary information from archive service
    try {
      if (casMCAdapter.getConnector().getCOMServices().getArchiveService() != null) {
        Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
            Level.INFO,
            "Archive Service found.");

        casMCAdapter.getConnector().getCOMServices().getArchiveService()
            .query(
                true,
                new ObjectType(
                    //  new UShort(4), new UShort(1), new UOctet((short) 1), new UShort(3)),
                    new UShort(0), new UShort(0), new UOctet((short) 0), new UShort(0)),
                archiveQueryList, null, archiveAdapter);
      } else {
        Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
            Level.INFO,
            "NO Archive Service found!");

      }
    } catch (NMFException | MALException | MALInteractionException ex) {
      Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(Level.SEVERE,
          null, ex);
    }
  }

  /**
   * Class for handling responses from Archive service query
   */
  private class SchedulerArchiveAdapter extends QueryInteraction
  {

    public SchedulerArchiveAdapter()
    {
      super(null);
    }

    @Override
    public MALMessage sendResponse(ObjectType objType, IdentifierList domain,
        ArchiveDetailsList objDetails, ElementList objBodies) throws MALInteractionException,
        MALException
    {
      if (objBodies != null) {
        int i = 0;
        for (Object objBody : objBodies) {
          if (objBody instanceof ActionInstanceDetails) {
            ActionInstanceDetails instance = ((ActionInstanceDetails) objBody);
            if (instance.getArgumentValues().size() == 3) {
              String timeStemp = instance.getArgumentValues().get(2).getValue().toString();
              try {
                AbsoluteDate targetDate = new AbsoluteDate(timeStemp, TimeScalesFactory.getUTC());

                if (targetDate.compareTo(CameraAcquisitorSystemMCAdapter.getNow()) > 0) {
                  photographLocation(instance.getArgumentValues(), objDetails.get(
                      i).getInstId(), true,
                      null);
                  Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                      Level.INFO, "recovered action: {0} longitude:{1} latitude:{2}", new Object[]{
                        timeStemp,
                        instance.getArgumentValues().get(0).getValue().toString(),
                        instance.getArgumentValues().get(
                            2).getValue().toString()});
                }
              } catch (Exception e) {
                Logger.getLogger(CameraAcquisitorSystemCameraTargetHandler.class.getName()).log(
                    Level.WARNING,
                    "recover action failed: {0}", e.getMessage());
              }
            }
          }
        }
      }
      return null;
    }

    @Override
    public MALMessage sendAcknowledgement() throws MALInteractionException, MALException
    {
      return null;
    }

    @Override
    public MALMessage sendUpdate(ObjectType objType, IdentifierList domain,
        ArchiveDetailsList objDetails, ElementList objBodies) throws MALInteractionException,
        MALException
    {
      sendResponse(objType, domain, objDetails, objBodies);
      return null;
    }
  }
}
