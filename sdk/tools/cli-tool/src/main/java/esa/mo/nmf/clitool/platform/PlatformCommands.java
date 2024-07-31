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

import esa.mo.nmf.clitool.BaseCommand;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.autonomousadcs.body.GetStatusResponse;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetry;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetry;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.consumer.CameraStub;
import org.ccsds.moims.mo.platform.camera.structures.*;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.consumer.GPSStub;
import picocli.CommandLine.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author marcel.mikolajko
 */
public class PlatformCommands {

    static Logger LOGGER = Logger.getLogger(PlatformCommands.class.getName());

    @Command(name = "gps", subcommands = {GetNMEASentence.class})
    public static class GPS {
    }

    @Command(name = "adcs", subcommands = {GetStatus.class})
    public static class ADCS {
    }

    @Command(name = "camera", subcommands = {TakePicture.class})
    public static class Camera {
    }

    @Command(name = "take-picture", description = "Take a picture from the camera")
    public static class TakePicture extends BaseCommand implements Runnable {

        @Option(names = {"-res", "--resolution"}, paramLabel = "<resolution>", required = true,
                description = "Resolution of the image in format widthxheigh. For example 1920x1080")
        String resolution;

        @Option(names = {"-fmt", "--format"}, paramLabel = "<format>", defaultValue = "PNG",
                description = "Format of the image")
        String format;

        @Option(names = {"-exp", "--exposure"}, paramLabel = "<exposureTime>", defaultValue = "0.1",
                description = "Exposure time of the picture")
        String exposure;

        @Option(names = {"-gr", "--gain-red"}, paramLabel = "<gainRed>", defaultValue = "1.0",
                description = "Gain of the red channel")
        String gainRed;

        @Option(names = {"-gg", "--gain-green"}, paramLabel = "<gainGreen>", defaultValue = "1.0",
                description = "Gain of the green channel")
        String gainGreen;

        @Option(names = {"-gb", "--gain-blue"}, paramLabel = "<gainBlue>", defaultValue = "1.0",
                description = "Gain of the blue channel")
        String gainBlue;

        @Option(names = {"-o", "--output"}, paramLabel = "<outputFile>", defaultValue = "picture",
                description = "Name of the output file without the extension.")
        String filename;

        @Override
        public void run() {
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
            CameraSettings settings = new CameraSettings(
                    new PixelResolution(new UInteger(Integer.parseInt(res[0])), new UInteger(Integer.parseInt(res[1]))),
                    PictureFormat.fromString(format.toUpperCase()),
                    new Duration(Double.parseDouble(exposure)),
                    Float.parseFloat(gainRed), Float.parseFloat(gainGreen), Float.parseFloat(gainBlue),
                    null
            );

            final Object lock = new Object();
            try {
                camera.takePicture(settings, new CameraAdapter() {
                    @Override
                    public void takePictureResponseReceived(MALMessageHeader msgHeader, Picture picture,
                            Map qosProperties) {
                        System.out.println("Picture received: " + picture);
                        try {
                            filename = filename + "." + format.toLowerCase();
                            Files.write(Paths.get(filename), picture.getContent().getValue());
                            System.out.println("File " + filename + " saved!");
                        } catch (IOException | MALException e) {
                            LOGGER.log(Level.SEVERE, "Error during picture saving!", e);
                        }

                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void takePictureResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                            Map qosProperties) {
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
                MALStandardError error = e.getStandardError();
                if (error.getErrorNumber().equals(COMHelper.INVALID_ERROR_NUMBER)) {
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

    @Command(name = "get-status", description = "Gets the provider status")
    public static class GetStatus extends BaseCommand implements Runnable {

        @Override
        public void run() {
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
                AttitudeTelemetry telemetry = response.getBodyElement0();
                System.out.println("Attitude telemetry:");
                System.out.println("  attitude: " + telemetry.getAttitude());
                System.out.println("  angular velocity: " + telemetry.getAngularVelocity());
                System.out.println("  sun vector: " + telemetry.getSunVector());
                System.out.println("  magnetic field: " + telemetry.getMagneticField());
                System.out.println("  state target: " + telemetry.getStateTarget());

                ActuatorsTelemetry actuatorsTelemetry = response.getBodyElement1();
                System.out.println("Actuators telemetry:");
                System.out.println("  target wheel speed: " + actuatorsTelemetry.getTargetWheelSpeed());
                System.out.println("  current wheel speed: " + actuatorsTelemetry.getCurrentWheelSpeed());
                System.out.println("  mtq dipole moment: " + actuatorsTelemetry.getMtqDipoleMoment());
                System.out.println("  mtq state: " + actuatorsTelemetry.getMtqState());
                System.out.println("Control duration: " + response.getBodyElement2());
                System.out.println("Generation enabled: " + response.getBodyElement3());
                System.out.println("Monitoring interval: " + response.getBodyElement4());
                System.out.println("Active attitude mode: " + response.getBodyElement5());
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during getStatus!", e);
            }

        }
    }

    @Command(name = "get-nmea-sentence", description = "Gets the NMEA sentence")
    public static class GetNMEASentence extends BaseCommand implements Runnable {

        @Parameters(arity = "1", paramLabel = "<sentenceIdentifier>", index = "0",
                description = "Identifier of the sentence")
        String sentenceId;

        @Override
        public void run() {
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
                    public void getNMEASentenceResponseReceived(MALMessageHeader msgHeader, String sentence,
                            Map qosProperties) {
                        System.out.println("Sentence received: " + sentence);

                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void getNMEASentenceResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                            Map qosProperties) {
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
