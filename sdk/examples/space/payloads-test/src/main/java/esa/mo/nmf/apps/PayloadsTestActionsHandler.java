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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.powercontrol.structures.Device;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceList;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.SDRConfiguration;

/**
 *
 * @author dmars
 */
public class PayloadsTestActionsHandler
{

  private static final Logger LOGGER = Logger.getLogger(
      PayloadsTestActionsHandler.class.getName());

  private static final float SDR_SAMPLING_FREQUENCY = (float) 1.5;
  private static final float SDR_LPF_BW = (float) 0.75;
  private static final int SDR_RX_GAIN = 10;
  private static final float SDR_CENTER_FREQUENCY = (float) 443.0;
  private static final Duration SDR_REPORTING_INTERVAL = new Duration(0.2);
  private static final int SDR_RECORDING_DURATION = 2000;
  static final int TOTAL_STAGES = 3;

  private boolean sdrRegistered = false;

  private final PayloadsTestMCAdapter payloadsTestMCAdapter;

  public PayloadsTestActionsHandler(final PayloadsTestMCAdapter payloadsTestMCAdapter)
  {
    this.payloadsTestMCAdapter = payloadsTestMCAdapter;
  }

  public UInteger executeAdcsModeAction(Duration duration,
                                        final AttitudeMode attitudeMode, final PayloadsTestMCAdapter payloadsTestMCAdapter)
  {
    if (duration != null) {
      // Negative Durations are not allowed!
      if (duration.getValue() < 0) {
        return new UInteger(1);
      }
      if (duration.getValue() == 0) {
        // Adhere to the ADCS Service interface
        duration = null;
      }
    }
    try {
      payloadsTestMCAdapter.nmf.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(
          duration, attitudeMode);
    } catch (final MALInteractionException | MALException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(3);
    } catch (final IOException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(4);
    }
    return null; // Success
  }
  
  public UInteger scheduleTakePicture(
          final Long actionInstanceObjId,
          final boolean reportProgress,
          final MALInteraction interaction,
          final Duration scheduleDelay,
          final PictureFormat format,
          final boolean autoExposed)
  {
    final Timer timer = new Timer();
    long delay = (long)(scheduleDelay.getValue() * 1000L);
    if (delay < 0) {
      delay = 0;
    }
    LOGGER.log(Level.INFO, "Scheduling takePicture action in {0} ms", delay);
    timer.schedule(new TimerTask(){
      @Override
      public void run() {
        final String actionName;
        switch(format.getOrdinal()) {
          case PictureFormat._BMP_INDEX:
            if(autoExposed)
              actionName = "takeAutoExposedPicture_BMP";
            else
              actionName = "takePicture_BMP";
            break;
          case PictureFormat._RAW_INDEX:
            if(autoExposed)
              actionName = "takeAutoExposedPicture_RAW";
            else
              actionName = "takePicture_RAW";
            break;
          case PictureFormat._PNG_INDEX:
          case PictureFormat._JPG_INDEX:
          default:
            if(autoExposed)
              actionName = "takeAutoExposedPicture_JPG";
            else
              actionName = "takePicture_JPG";
            break;
        }
        payloadsTestMCAdapter.simpleCommandingInterface.launchAction(actionName, new Serializable[]{});
      }
    }, delay);
    return null; // Success!
  }

  public UInteger takePicture(
          final Long actionInstanceObjId,
          final boolean reportProgress,
          final MALInteraction interaction,
          final PictureFormat format)
  {
    try {
      payloadsTestMCAdapter.nmf.getPlatformServices().getCameraService().takePicture(
          new CameraSettings(
              payloadsTestMCAdapter.defaultCameraResolution,
              format,
              new Duration(payloadsTestMCAdapter.cameraExposureTime),
              payloadsTestMCAdapter.cameraGainR,
              payloadsTestMCAdapter.cameraGainG,
              payloadsTestMCAdapter.cameraGainB),
          new PayloadsTestCameraDataHandler(actionInstanceObjId, payloadsTestMCAdapter));
      return null; // Success!
    } catch (final MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }

  public UInteger takeAutoExposedPicture(
          final Long actionInstanceObjId,
          final boolean reportProgress,
          final MALInteraction interaction,
          final PictureFormat format)
  {
    try {
      payloadsTestMCAdapter.nmf.getPlatformServices().getCameraService().takeAutoExposedPicture(
          new CameraSettings(
              payloadsTestMCAdapter.defaultCameraResolution,
              format,
              new Duration(payloadsTestMCAdapter.cameraExposureTime),
              payloadsTestMCAdapter.cameraGainR,
              payloadsTestMCAdapter.cameraGainG,
              payloadsTestMCAdapter.cameraGainB),
          new PayloadsTestCameraDataHandler(actionInstanceObjId, payloadsTestMCAdapter));
      return null; // Success!
    } catch (final MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }

  public UInteger setDeviceState(
          final Long actionInstanceObjId,
          final boolean reportProgress,
          final MALInteraction interaction,
          final UInteger deviceType,
          final boolean setOn)
  {
    try {
      final DeviceList deviceList = new DeviceList();
      deviceList.add(new Device(setOn, null, null, DeviceType.fromNumericValue(deviceType)));
      payloadsTestMCAdapter.nmf.getPlatformServices().getPowerControlService().enableDevices(
          deviceList);
      return null; // Success!
    } catch (final MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }

  public UInteger recordSDRData(
          final Long actionInstanceObjId,
          final boolean reportProgress,
          final MALInteraction interaction)
  {
    try {
      if (!sdrRegistered) {
        payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().
            streamRadioRegister(ConnectionConsumer.subscriptionWildcardRandom(),
                new PayloadsTestSDRDataHandler());
        sdrRegistered = true;
      }
      final SDRConfiguration config = new SDRConfiguration(SDR_CENTER_FREQUENCY, SDR_RX_GAIN,
          SDR_LPF_BW, SDR_SAMPLING_FREQUENCY);
      payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().enableSDR(
          true, config, SDR_REPORTING_INTERVAL);
      final Timer timer = new Timer();
      timer.schedule(new TimerTask()
      {
        @Override
        public void run()
        {
          try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().enableSDR(
                false, config, SDR_REPORTING_INTERVAL);
          } catch (final MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, "Failed to stop the SDR", ex);
          }
        }
      }, SDR_RECORDING_DURATION);
      return null; // Success!
    } catch (final MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }
}
