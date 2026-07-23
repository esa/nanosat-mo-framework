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

import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.platform.structures.*;

/**
 *
 * @author dmars
 */
public class PayloadsTestActionsHandler {

    private static final Logger LOGGER = Logger.getLogger(PayloadsTestActionsHandler.class.getName());

    private static final float SDR_SAMPLING_FREQUENCY = (float) 1.5;
    private static final float SDR_LPF_BW = (float) 0.75;
    private static final int SDR_RX_GAIN = 10;
    private static final float SDR_CENTER_FREQUENCY = (float) 443.0;
    private static final Duration SDR_REPORTING_INTERVAL = new Duration(0.2);
    private static final int SDR_RECORDING_DURATION = 2000;
    static final int TOTAL_STAGES = 3;

    private boolean sdrRegistered = false;

    private final PayloadsTestMCAdapter payloadsTestMCAdapter;

    public PayloadsTestActionsHandler(PayloadsTestMCAdapter payloadsTestMCAdapter) {
        this.payloadsTestMCAdapter = payloadsTestMCAdapter;
    }

    public void executeAdcsModeAction(Duration duration,
            AttitudeMode attitudeMode, PayloadsTestMCAdapter payloadsTestMCAdapter) throws ExecutionFailedException {
        if (duration != null) {
            // Negative Durations are not allowed!
            if (duration.getInSeconds() < 0) {
                throw new ExecutionFailedException("Hold duration must be non-negative");
            }
            if (duration.getInSeconds() == 0) {
                // Adhere to the ADCS Service interface
                duration = null;
            }
        }
        try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(duration, attitudeMode);
        } catch (MALInteractionException | MALException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new ExecutionFailedException("Failed to set desired attitude: " + ex.getMessage());
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new ExecutionFailedException("IO error setting desired attitude: " + ex.getMessage());
        }
    }

    public void scheduleTakePicture(Long executionId,
            MALInteraction interaction, Duration scheduleDelay, PictureFormat format, boolean autoExposed) {
        Timer timer = new Timer();
        long delay = (long) (scheduleDelay.getInSeconds() * 1000L);
        if (delay < 0) {
            delay = 0;
        }
        LOGGER.log(Level.INFO, "Scheduling takePicture action in {0} ms", delay);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String actionName;
                switch (format.getValue()) {
                    case PictureFormat.BMP_VALUE:
                        if (autoExposed) {
                            actionName = "camera.take-auto-exposed-picture.bmp";
                        } else {
                            actionName = "camera.take-picture.bmp";
                        }
                        break;
                    case PictureFormat.RAW_VALUE:
                        if (autoExposed) {
                            actionName = "camera.take-auto-exposed-picture.raw";
                        } else {
                            actionName = "camera.take-picture.raw";
                        }
                        break;
                    case PictureFormat.PNG_VALUE:
                    case PictureFormat.JPG_VALUE:
                    default:
                        if (autoExposed) {
                            actionName = "camera.take-auto-exposed-picture.jpg";
                        } else {
                            actionName = "camera.take-picture.jpg";
                        }
                        break;
                }
                payloadsTestMCAdapter.simpleCommandingInterface.launchAction(actionName, new Serializable[]{});
            }
        }, delay);
    }

    public void takePicture(Long executionId,
            MALInteraction interaction, PictureFormat format) throws ExecutionFailedException {
        try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getCameraService().takePicture(
                    new CameraSettings(
                            payloadsTestMCAdapter.defaultCameraResolution,
                            format,
                            new Duration(payloadsTestMCAdapter.cameraExposureTime),
                            payloadsTestMCAdapter.cameraGainR,
                            payloadsTestMCAdapter.cameraGainG,
                            payloadsTestMCAdapter.cameraGainB,
                            null),
                    new PayloadsTestCameraDataHandler(executionId, payloadsTestMCAdapter));
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new ExecutionFailedException("Failed to take picture: " + ex.getMessage());
        }
    }

    public void takeAutoExposedPicture(Long executionId,
            MALInteraction interaction, PictureFormat format) throws ExecutionFailedException {
        try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getCameraService().takeAutoExposedPicture(
                    new CameraSettings(
                            payloadsTestMCAdapter.defaultCameraResolution,
                            format,
                            new Duration(payloadsTestMCAdapter.cameraExposureTime),
                            payloadsTestMCAdapter.cameraGainR,
                            payloadsTestMCAdapter.cameraGainG,
                            payloadsTestMCAdapter.cameraGainB,
                            null),
                    new PayloadsTestCameraDataHandler(executionId, payloadsTestMCAdapter));
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new ExecutionFailedException("Failed to take auto-exposed picture: " + ex.getMessage());
        }
    }

    public void setDeviceState(Long executionId,
            MALInteraction interaction, UInteger deviceType, boolean setOn) throws ExecutionFailedException {
        try {
            DeviceList deviceList = new DeviceList();
            DeviceType d = new DeviceType((int) deviceType.getValue());
            deviceList.add(new Device(setOn, null, null, d));
            payloadsTestMCAdapter.nmf.getPlatformServices().getPowerControlService().enableDevices(deviceList);
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new ExecutionFailedException("Failed to set device state: " + ex.getMessage());
        }
    }

    public void recordSDRData(Long executionId, MALInteraction interaction) throws ExecutionFailedException {
        try {
            if (!sdrRegistered) {
                payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().streamRadioRegister(
                        ConnectionConsumer.subscriptionWildcardRandom(), new PayloadsTestSDRDataHandler());
                sdrRegistered = true;
            }
            SDRConfiguration config = new SDRConfiguration(SDR_CENTER_FREQUENCY,
                    SDR_RX_GAIN, SDR_LPF_BW, SDR_SAMPLING_FREQUENCY);
            payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().enableSDR(
                    true, config, SDR_REPORTING_INTERVAL);
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().enableSDR(
                                false, config, SDR_REPORTING_INTERVAL);
                    } catch (MALInteractionException | MALException | IOException | NMFException ex) {
                        LOGGER.log(Level.SEVERE, "Failed to stop the SDR", ex);
                    }
                }
            }, SDR_RECORDING_DURATION);
        } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new ExecutionFailedException("Failed to record SDR data: " + ex.getMessage());
        }
    }
}
