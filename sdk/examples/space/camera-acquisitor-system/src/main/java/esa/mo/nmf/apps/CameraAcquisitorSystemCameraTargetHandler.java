/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeTargetTracking;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;

/**
 * Class handling acquisition of targets and the corresponding actions
 *
 * @author Kevin Otto
 */
public class CameraAcquisitorSystemCameraTargetHandler
{

  public static final String ACTION_PHOTOGRAPH_LOCATION = "photographLocation";
  public static final int PHOTOGRAPH_LOCATION_STAGES =
      3 + CameraAcquisitorSystemCameraHandler.PHOTOGRAPH_NOW_STAGES;
  private static final int STAGE_WAIT_FOR_PASS = 1;
  private static final int STAGE_ATTITUDE_CORECTION = 2;
  private static final int STAGE_WAIT_FOR_OPTIMAL_PASS = 3;

  private final CameraAcquisitorSystemMCAdapter casMCAdapter;

  private static final Logger LOGGER = Logger.getLogger(
      CameraAcquisitorSystemCameraTargetHandler.class.getName());

  // time to keep track of all scheduled photographs
  private final java.util.Timer timer = new java.util.Timer();

  public CameraAcquisitorSystemCameraTargetHandler(final CameraAcquisitorSystemMCAdapter casMCAdapter)
  {
    this.casMCAdapter = casMCAdapter;
  }

  UInteger photographLocation(
          final AttributeValueList attributeValues,
          final Long actionInstanceObjId,
          final boolean reportProgress, final MALInteraction interaction)
  {
    // get parameters
    final Double latitude = HelperAttributes.attribute2double(attributeValues.get(0).getValue());
    final Double longitude = HelperAttributes.attribute2double(attributeValues.get(1).getValue());
    final String timeStamp =
        HelperAttributes.attribute2JavaType(attributeValues.get(2).getValue()).toString();
    //
    return photographLocation(
        latitude, longitude, timeStamp, actionInstanceObjId, reportProgress, interaction);
  }


  UInteger photographLocation(final double latitude, final double longitude, final String timeStamp,
                              final Long actionInstanceObjId,
                              final boolean reportProgress, final MALInteraction interaction)
  {
    final AbsoluteDate targetDate = new AbsoluteDate(timeStamp, TimeScalesFactory.getUTC());

    final double seconds = targetDate.durationFrom(CameraAcquisitorSystemMCAdapter.getNow());

    // create TimerTask to schedule attitude correction shortly before targetDate
    final TimerTask task = new java.util.TimerTask()
    {
      @Override
      public void run()
      {
        try {
          casMCAdapter.getConnector().reportActionExecutionProgress(true, 0,
              STAGE_WAIT_FOR_PASS,
              PHOTOGRAPH_LOCATION_STAGES,
              actionInstanceObjId);
        } catch (final NMFException ex) {
          LOGGER.log(
              Level.SEVERE,
              null, ex);
        }

        final Duration timeTillPhotograph = new Duration(
            targetDate.durationFrom(CameraAcquisitorSystemMCAdapter.getNow()));

        // set desired attitude using target latitude and longitude
        final AttitudeMode desiredAttitude = new AttitudeModeTargetTracking(
            (float) latitude,
            (float) longitude);

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
        } catch (final NMFException | IOException | MALInteractionException | MALException ex) {
          LOGGER.log(
              Level.SEVERE,
              null, ex);
        }

        // wait again till optimal moment for photograph
        try {
          final double fractSeconds = timeTillPhotograph.getValue();
          final long seconds = (long) fractSeconds;
          final long milliSeconds = (long) ((fractSeconds - seconds) * 1000);

          TimeUnit.SECONDS.sleep(seconds);
          TimeUnit.MILLISECONDS.sleep(milliSeconds);
        } catch (final InterruptedException ex) {
          LOGGER.log(
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

        } catch (final NMFException | IOException | MALInteractionException | MALException ex) {
          LOGGER.log(
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

    return null; //Null (action successful) is returned before the action is performed
  }

  /**
   * recovers all scheduled photographs in case of a crash or a reboot of the system.
   */
  void recoverLastState()
  {
    // get previous requests
    final ArchiveQueryList archiveQueryList = new ArchiveQueryList();
    final ArchiveQuery archiveQuery = new ArchiveQuery();

    archiveQuery.setDomain(null);
    archiveQuery.setNetwork(null);
    archiveQuery.setProvider(null);
    archiveQuery.setRelated(0L);
    archiveQuery.setSource(null);
    archiveQuery.setStartTime(null);
    archiveQuery.setEndTime(null);
    archiveQuery.setSortFieldName(null);

    archiveQueryList.add(archiveQuery);

    final SchedulerArchiveAdapter archiveAdapter = new SchedulerArchiveAdapter();

    // query all necessary information from archive service
    try {
      if (casMCAdapter.getConnector().getCOMServices().getArchiveService() != null) {
        LOGGER.log(
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
        LOGGER.log(
            Level.INFO,
            "NO Archive Service found!");

      }
    } catch (final NMFException | MALException | MALInteractionException ex) {
      LOGGER.log(Level.SEVERE,
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
    public MALMessage sendResponse(final ObjectType objType, final IdentifierList domain,
                                   final ArchiveDetailsList objDetails, final ElementList objBodies) throws MALInteractionException,
        MALException
    {
      if (objBodies != null) {
        final int i = 0;
        for (final Object objBody : objBodies) {
          if (objBody instanceof ActionInstanceDetails) {
            final ActionInstanceDetails instance = ((ActionInstanceDetails) objBody);
            if (instance.getArgumentValues().size() == 3) {
              final String timeStamp = instance.getArgumentValues().get(2).getValue().toString();
              try {
                final AbsoluteDate targetDate = new AbsoluteDate(timeStamp, TimeScalesFactory.getUTC());

                if (targetDate.compareTo(CameraAcquisitorSystemMCAdapter.getNow()) > 0) {
                  photographLocation(instance.getArgumentValues(), objDetails.get(
                      i).getInstId(), true,
                      null);
                      LOGGER.log(
                      Level.INFO, "recovered action: {0} latitude:{1} longitude:{2}", new Object[]{
                        timeStamp,
                        instance.getArgumentValues().get(0).getValue().toString(),
                        instance.getArgumentValues().get(
                            1).getValue().toString()});
                }
              } catch (final Exception e) {
                LOGGER.log(
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
    public MALMessage sendUpdate(final ObjectType objType, final IdentifierList domain,
                                 final ArchiveDetailsList objDetails, final ElementList objBodies) throws MALInteractionException,
        MALException
    {
      sendResponse(objType, domain, objDetails, objBodies);
      return null;
    }
  }
}
