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
package esa.mo.platform.impl.util;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.platform.impl.provider.adapters.AIMovidiusAdapter;
import esa.mo.platform.impl.provider.gen.*;
import esa.mo.platform.impl.provider.softsim.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.platform.artificialintelligence.provider.ArtificialIntelligenceInheritanceSkeleton;
import org.ccsds.moims.mo.platform.fpga.provider.FPGAInheritanceSkeleton;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwaredefinedradio.provider.SoftwareDefinedRadioInheritanceSkeleton;

/**
 *
 *
 */
public class PlatformServicesProviderSoftSim implements PlatformServicesProviderInterface {

    private static final Logger LOGGER = Logger.getLogger(PlatformServicesProviderSoftSim.class.getName());

    // Simulator
    private final ESASimulator sim = new ESASimulator("127.0.0.1");

    // Services
    private final ArtificialIntelligenceProviderServiceImpl aiService =
            new ArtificialIntelligenceProviderServiceImpl();
    private final AutonomousADCSProviderServiceImpl autonomousADCSService =
            new AutonomousADCSProviderServiceImpl();
    private final CameraProviderServiceImpl cameraService =
            new CameraProviderServiceImpl();
    private final GPSProviderServiceWithTLEImpl gpsService =
            new GPSProviderServiceWithTLEImpl();
    private final OpticalDataReceiverProviderServiceImpl opticalDataReceiverService =
            new OpticalDataReceiverProviderServiceImpl();
    private final SoftwareDefinedRadioProviderServiceImpl sdrService =
            new SoftwareDefinedRadioProviderServiceImpl();
    private final PowerControlProviderServiceImpl powerService =
            new PowerControlProviderServiceImpl();
    private PowerControlAdapterInterface pcAdapter;
    private final FPGAProviderServiceImpl fpgaService =
            new FPGAProviderServiceImpl();

    @Override
    public void init(COMServicesProvider comServices) throws MALException {
        // Check if hybrid setup is used
        CameraAdapterInterface camAdapter;
        AIMovidiusAdapter aiAdapter;
        AutonomousADCSAdapterInterface adcsAdapter;
        GPSAdapterInterface gpsAdapter;
        OpticalDataReceiverAdapterInterface optRxAdapter;
        SoftwareDefinedRadioAdapterInterface sdrAdapter;

        Properties platformProperties = new Properties();
        try {
            platformProperties.load(new FileInputStream("platformsim.properties"));
            if (platformProperties.getProperty("platform.mode").equals("hybrid")) {
                String pcAdapterName = platformProperties.getProperty("pc.adapter");
                String camAdapterName = platformProperties.getProperty("camera.adapter");
                String adcsAdapterName = platformProperties.getProperty("adcs.adapter");
                String gpsAdapterName = platformProperties.getProperty("gps.adapter");
                String optRxAdapterName = platformProperties.getProperty("optrx.adapter");
                String sdrAdapterName = platformProperties.getProperty("sdr.adapter");

                // PowerControl adapter
                try {
                    pcAdapter = (PowerControlAdapterInterface) Class.forName(pcAdapterName).newInstance();
                } catch (InstantiationException | IllegalAccessException
                        | ClassNotFoundException e) {
                    LOGGER.log(Level.WARNING,
                            "Failed to instantiate the power control adapter. "
                            + "Falling back to default PowerControlSoftSimAdapter.",
                            e);
                    pcAdapter = new PowerControlSoftSimAdapter();
                }

                // Camera adapter
                try {
                    boolean isSim = Arrays.asList(Class.forName(camAdapterName).getInterfaces())
                            .contains(SimulatorAdapter.class);

                    if (isSim) {
                        camAdapter = new CameraSoftSimAdapter(sim, pcAdapter);
                    } else {
                        Constructor constructor = Class.forName(camAdapterName)
                                .getConstructor(PowerControlAdapterInterface.class);
                        camAdapter = (CameraAdapterInterface) constructor.newInstance(pcAdapter);
                    }
                } catch (InstantiationException | IllegalAccessException
                        | ClassNotFoundException | NoSuchMethodException
                        | InvocationTargetException e) {
                    LOGGER.log(Level.WARNING,
                            "Failed to instantiate the camera adapter. "
                            + "Falling back to default CameraSoftSimAdapter.", e);
                    camAdapter = new CameraSoftSimAdapter(
                            sim, pcAdapter);
                }

                // ADCS adapter
                try {
                    boolean isSim = Arrays.asList(Class.forName(adcsAdapterName).getInterfaces())
                            .contains(SimulatorAdapter.class);
                    if (isSim) {
                        adcsAdapter = new AutonomousADCSSoftSimAdapter(sim, pcAdapter);
                    } else {
                        Constructor constructor = Class.forName(adcsAdapterName)
                                .getConstructor(PowerControlAdapterInterface.class);
                        adcsAdapter = (AutonomousADCSAdapterInterface) constructor.newInstance(pcAdapter);
                    }
                } catch (InstantiationException | IllegalAccessException
                        | ClassNotFoundException | NoSuchMethodException
                        | InvocationTargetException e) {
                    LOGGER.log(Level.WARNING,
                            "Failed to instantiate the iADCS adapter. "
                            + "Falling back to default AutonomousADCSSoftSimAdapter.",
                            e);
                    adcsAdapter = new AutonomousADCSSoftSimAdapter(sim, pcAdapter);
                }

                // GPS adapter
                try {
                    boolean isSim = Arrays.asList(Class.forName(gpsAdapterName).getInterfaces())
                            .contains(SimulatorAdapter.class);
                    if (isSim) {
                        gpsAdapter = new GPSSoftSimAdapter(sim, pcAdapter);
                    } else {
                        Constructor constructor = Class.forName(gpsAdapterName)
                                .getConstructor(PowerControlAdapterInterface.class);
                        gpsAdapter = (GPSAdapterInterface) constructor.newInstance(pcAdapter);
                    }
                } catch (InstantiationException | IllegalAccessException
                        | ClassNotFoundException | NoSuchMethodException
                        | InvocationTargetException e) {
                    LOGGER.log(Level.WARNING,
                            "Failed to instantiate the GPS adapter. "
                            + "Falling back to default GPSSoftSimAdapter.", e);
                    gpsAdapter = new GPSSoftSimAdapter(sim, pcAdapter);
                }

                // Optical Data Receiver adapter
                try {
                    boolean isSim = Arrays.asList(Class.forName(optRxAdapterName).getInterfaces())
                            .contains(SimulatorAdapter.class);

                    if (isSim) {
                        optRxAdapter = new OpticalDataReceiverSoftSimAdapter(sim, pcAdapter);
                    } else {
                        Constructor constructor = Class.forName(optRxAdapterName)
                                .getConstructor(PowerControlAdapterInterface.class);
                        optRxAdapter = (OpticalDataReceiverAdapterInterface) constructor.newInstance(pcAdapter);
                    }
                } catch (InstantiationException | IllegalAccessException
                        | ClassNotFoundException | NoSuchMethodException
                        | InvocationTargetException e) {
                    LOGGER.log(Level.WARNING,
                            "Failed to instantiate the optRX adapter. "
                            + "Falling back to default "
                            + "OpticalDataReceiverSoftSimAdapter.", e);
                    optRxAdapter = new OpticalDataReceiverSoftSimAdapter(sim, pcAdapter);
                }

                // Radio adapter
                try {
                    boolean isSim = Arrays.asList(Class.forName(sdrAdapterName).getInterfaces())
                            .contains(SimulatorAdapter.class);
                    if (isSim) {
                        sdrAdapter = new SoftwareDefinedRadioSoftSimAdapter(sim, pcAdapter);
                    } else {
                        Constructor constructor = Class.forName(sdrAdapterName)
                                .getConstructor(PowerControlAdapterInterface.class);
                        sdrAdapter = (SoftwareDefinedRadioAdapterInterface) constructor.newInstance(pcAdapter);
                    }
                } catch (InstantiationException | IllegalAccessException
                        | ClassNotFoundException | NoSuchMethodException
                        | InvocationTargetException e) {
                    LOGGER.log(Level.WARNING,
                            "Failed to instantiate the SDR adapter. "
                            + "Falling back to default "
                            + "SoftwareDefinedRadioSoftSimAdapter.", e);
                    sdrAdapter = new SoftwareDefinedRadioSoftSimAdapter(
                            sim, pcAdapter);
                }

                aiAdapter = null;
            } else {
                pcAdapter = new PowerControlSoftSimAdapter();
                camAdapter = new CameraSoftSimAdapter(sim, pcAdapter);
                aiAdapter = new AIMovidiusAdapter();
                adcsAdapter = new AutonomousADCSSoftSimAdapter(sim, pcAdapter);
                gpsAdapter = new GPSSoftSimAdapter(sim, pcAdapter);
                optRxAdapter = new OpticalDataReceiverSoftSimAdapter(sim, pcAdapter);
                sdrAdapter = new SoftwareDefinedRadioSoftSimAdapter(sim, pcAdapter);
            }
        } catch (IOException e) {
            // Assume simulated environment by default
            LOGGER.log(Level.WARNING,
                    "Platform config file not found. Using simulated environment.");
            pcAdapter = new PowerControlSoftSimAdapter();
            camAdapter = new CameraSoftSimAdapter(sim, pcAdapter);
            try {
                aiAdapter = new AIMovidiusAdapter();
            } catch (IOException ex) {
                LOGGER.log(Level.INFO, "The AI adapter could not be started!", ex);
                aiAdapter = null;
            }
            adcsAdapter = new AutonomousADCSSoftSimAdapter(sim, pcAdapter);
            gpsAdapter = new GPSSoftSimAdapter(sim, pcAdapter);
            optRxAdapter = new OpticalDataReceiverSoftSimAdapter(sim, pcAdapter);
            sdrAdapter = new SoftwareDefinedRadioSoftSimAdapter(sim, pcAdapter);
        }

        autonomousADCSService.init(comServices, adcsAdapter);
        if (aiAdapter != null) {
            aiService.init(aiAdapter);
        }
        cameraService.init(comServices, camAdapter);
        gpsService.init(comServices, gpsAdapter);
        opticalDataReceiverService.init(optRxAdapter);
        sdrService.init(sdrAdapter);
        powerService.init(pcAdapter);
        fpgaService.init(comServices, new FPGASoftSimAdapter());
    }

    public void startStatusTracking(ConnectionConsumer connection) {
        pcAdapter.startStatusTracking(connection);
    }

    /**
     * Returns the time factor at which the simulated time advances relative
     * to real time.
     *
     * @return The simulation time factor.
     */
    public int getTimeFactor() {
        return sim.getSimulatorNode().getTimeFactor();
    }

    @Override
    public AutonomousADCSProviderServiceImpl getAutonomousADCSService() {
        return this.autonomousADCSService;
    }

    @Override
    public CameraProviderServiceImpl getCameraService() {
        return this.cameraService;
    }

    @Override
    public GPSProviderServiceImpl getGPSService() {
        System.out.println("platform");
        return this.gpsService;
    }

    @Override
    public OpticalDataReceiverInheritanceSkeleton getOpticalDataReceiverService() {
        return this.opticalDataReceiverService;
    }

    @Override
    public SoftwareDefinedRadioInheritanceSkeleton getSoftwareDefinedRadioService() {
        return this.sdrService;
    }

    @Override
    public ArtificialIntelligenceInheritanceSkeleton getAIService() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public FPGAInheritanceSkeleton getFPGAService() {
        return this.fpgaService;
    }
}
