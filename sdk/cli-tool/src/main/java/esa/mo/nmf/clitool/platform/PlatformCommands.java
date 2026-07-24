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
package esa.mo.nmf.clitool.platform;

import esa.mo.nmf.clitool.Args;
import esa.mo.nmf.clitool.BaseCommand;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.body.GetStatusResponse;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.consumer.CameraStub;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.consumer.GPSStub;
import org.ccsds.moims.mo.platform.structures.*;

/**
 * Container for the platform CLI subcommands (gps, adcs, camera).
 *
 * @author marcel.mikolajko
 */
public class PlatformCommands {

    static Logger LOGGER = Logger.getLogger(PlatformCommands.class.getName());

    private PlatformCommands() {
    }

    /**
     * Implements the {@code camera take-picture} CLI command.
     */
    public static class TakePicture extends BaseCommand {
        /**
         * Default constructor.
         */
        public TakePicture() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            String resolution = args.option("-res", "--resolution");
            String format = args.option("-fmt", "--format");
            String exposure = args.option("-exp", "--exposure");
            String gainRed = args.option("-gr", "--gain-red");
            String gainGreen = args.option("-gg", "--gain-green");
            String gainBlue = args.option("-gb", "--gain-blue");
            String filename = args.option("-o", "--output");

            if (resolution == null) {
                System.out.println("Missing required option: -res/--resolution");
                return;
            }
            if (format == null) {
                format = "PNG";
            }
            if (exposure == null) {
                exposure = "0.1";
            }
            if (gainRed == null) {
                gainRed = "1.0";
            }
            if (gainGreen == null) {
                gainGreen = "1.0";
            }
            if (gainBlue == null) {
                gainBlue = "1.0";
            }
            if (filename == null) {
                filename = "picture";
            }

            if (!super.initRemoteConsumer()) {
                return;
            }

            CameraStub camera;
            try {
                camera = consumer.getPlatformServices().getCameraService();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Camera service is not available for this provider!", e);
                return;
            }

            String[] res = resolution.split("x");
            PixelResolution pixelResolution = new PixelResolution(
                    new UInteger(Integer.parseInt(res[0])),
                    new UInteger(Integer.parseInt(res[1])));

            final String finalFormat = format;
            final String finalFilename = filename;
            CameraSettings settings = new CameraSettings(
                    pixelResolution,
                    PictureFormat.fromString(format.toUpperCase()),
                    new Duration(Double.parseDouble(exposure)),
                    Float.parseFloat(gainRed),
                    Float.parseFloat(gainGreen),
                    Float.parseFloat(gainBlue),
                    null
            );

            final Object lock = new Object();
            try {
                camera.takePicture(settings, new CameraAdapter() {
                    @Override
                    public void takePictureResponseReceived(MALMessageHeader msgHeader,
                            Picture picture, Map qosProperties) {
                        System.out.println("Picture received: " + picture);
                        try {
                            String outFile = finalFilename + "." + finalFormat.toLowerCase();
                            Files.write(Paths.get(outFile), picture.getContent().getValue());
                            System.out.println("File " + outFile + " saved!");
                        } catch (IOException e) {
                            LOGGER.log(Level.SEVERE, "Error during picture saving!", e);
                        }

                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void takePictureResponseErrorReceived(MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE, "Error during takePicture!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock) {
                    lock.wait();
                }
            } catch (MALInteractionException e) {
                MOErrorException error = e.getStandardError();
                if (error.getErrorNumber().equals(COMHelper.INVALID_ARGUMENT_ERROR_NUMBER)) {
                    if (error.getExtraInformation() instanceof PixelResolutionList) {
                        System.out.println("Provided resolution is not supported!");
                        System.out.println("Supported resolutions: " + error.getExtraInformation());
                    } else {
                        System.out.println("Provided format is not supported!");
                        System.out.println("Supported formats: " + error.getExtraInformation());
                    }
                } else if (error.getErrorNumber().equals(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER)) {
                    System.out.println("Camera is currently unavailable");
                } else if (error.getErrorNumber().equals(PlatformHelper.DEVICE_IN_USE_ERROR_NUMBER)) {
                    System.out.println("Camera is currently in use");
                } else {
                    LOGGER.log(Level.SEVERE, "Error during takePicture!", e);
                }
            } catch (MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during takePicture!", e);
            }
        }
    }

    /**
     * Implements the {@code adcs get-status} CLI command.
     */
    public static class GetStatus extends BaseCommand {
        /**
         * Default constructor.
         */
        public GetStatus() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);

            if (!super.initRemoteConsumer()) {
                return;
            }

            AutonomousADCSStub adcs;
            try {
                adcs = consumer.getPlatformServices().getAutonomousADCSService();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Autonomous ADCS service is not available for this provider!", e);
                return;
            }

            try {
                GetStatusResponse response = adcs.getStatus();
                AttitudeTelemetry telemetry = response.getAttitudeTelemetry();
                System.out.println("Attitude telemetry:");
                System.out.println("  attitude: " + telemetry.getAttitude());
                System.out.println("  angular velocity: " + telemetry.getAngularVelocity());
                System.out.println("  sun vector: " + telemetry.getSunVector());
                System.out.println("  magnetic field: " + telemetry.getMagneticField());
                System.out.println("  state target: " + telemetry.getStateTarget());

                ActuatorsTelemetry actuatorsTelemetry = response.getActuatorsTelemetry();
                System.out.println("Actuators telemetry:");
                System.out.println("  target wheel speed: " + actuatorsTelemetry.getTargetWheelSpeed());
                System.out.println("  current wheel speed: " + actuatorsTelemetry.getCurrentWheelSpeed());
                System.out.println("  mtq dipole moment: " + actuatorsTelemetry.getMtqDipoleMoment());
                System.out.println("  mtq state: " + actuatorsTelemetry.getMtqState());
                System.out.println("Control duration: " + response.getControlDuration());
                System.out.println("Generation enabled: " + response.getGenerationEnabled());
                System.out.println("Monitoring interval: " + response.getMonitoringInterval());
                System.out.println("Active attitude mode: " + response.getActiveAttitudeMode());
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during getStatus!", e);
            }
        }
    }

    /**
     * Implements the {@code gps get-nmea-sentence} CLI command.
     */
    public static class GetNMEASentence extends BaseCommand {
        /**
         * Default constructor.
         */
        public GetNMEASentence() {
        }


        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <sentenceIdentifier>");
                return;
            }
            String sentenceId = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                return;
            }

            GPSStub gps = null;
            try {
                gps = consumer.getPlatformServices().getGPSService();
            } catch (IOException e) {
                System.out.println("GPS service is not available for this provider!");
                return;
            }

            final Object lock = new Object();

            try {
                gps.getNMEASentence(sentenceId, new GPSAdapter() {
                    @Override
                    public void getNMEASentenceResponseReceived(MALMessageHeader msgHeader,
                            String sentence, Map qosProperties) {
                        System.out.println("Sentence received: " + sentence);

                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void getNMEASentenceResponseErrorReceived(MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE, "Error during getNMEASentence!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock) {
                    lock.wait();
                }
            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during getNMEASentence!", e);
            }
        }
    }
}
