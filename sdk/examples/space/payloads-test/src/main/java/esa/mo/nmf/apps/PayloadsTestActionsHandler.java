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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Duration;
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

  public PayloadsTestActionsHandler(PayloadsTestMCAdapter payloadsTestMCAdapter)
  {
    this.payloadsTestMCAdapter = payloadsTestMCAdapter;
  }

  public UInteger executeAdcsModeAction(Duration duration,
      AttitudeMode attitudeMode, PayloadsTestMCAdapter payloadsTestMCAdapter)
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
    } catch (MALInteractionException | MALException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(3);
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(4);
    }
    return null; // Success
  }

  public UInteger takePicture(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction,
      PictureFormat format)
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
    } catch (MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }

  public UInteger takeAutoExposedPicture(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction,
      PictureFormat format)
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
    } catch (MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }

  public UInteger setDeviceState(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction,
      UInteger deviceType,
      boolean setOn)
  {
    try {
      DeviceList deviceList = new DeviceList();
      deviceList.add(new Device(setOn, null, null, DeviceType.fromNumericValue(deviceType)));
      payloadsTestMCAdapter.nmf.getPlatformServices().getPowerControlService().enableDevices(
          deviceList);
      return null; // Success!
    } catch (MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }

  public UInteger recordSDRData(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    try {
      if (!sdrRegistered) {
        payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().
            streamRadioRegister(ConnectionConsumer.subscriptionWildcardRandom(),
                new PayloadsTestSDRDataHandler());
        sdrRegistered = true;
      }
      SDRConfiguration config = new SDRConfiguration(SDR_CENTER_FREQUENCY, SDR_RX_GAIN,
          SDR_LPF_BW, SDR_SAMPLING_FREQUENCY);
      payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().enableSDR(
          true, config, SDR_REPORTING_INTERVAL);
      Timer timer = new Timer();
      timer.schedule(new TimerTask()
      {
        @Override
        public void run()
        {
          try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().enableSDR(
                false, config, SDR_REPORTING_INTERVAL);
          } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, "Failed to stop the SDR", ex);
          }
        }
      }, SDR_RECORDING_DURATION);
      return null; // Success!
    } catch (MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }
}
