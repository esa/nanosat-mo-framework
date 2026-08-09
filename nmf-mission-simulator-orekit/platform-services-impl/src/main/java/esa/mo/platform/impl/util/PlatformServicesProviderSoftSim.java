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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.platform.artificialintelligence.provider.ArtificialIntelligenceInheritanceSkeleton;
import org.ccsds.moims.mo.platform.fpga.provider.FPGAInheritanceSkeleton;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwaredefinedradio.provider.SoftwareDefinedRadioInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwareimages.provider.SoftwareImagesInheritanceSkeleton;

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
    private final SoftwareImagesProviderServiceImpl softwareImagesService =
            new SoftwareImagesProviderServiceImpl();

    @Override
    public void init(COMServicesProvider comServices) throws MALException {
        // Every platform service is answered by the simulator.
        //
        // There was a "hybrid" mode here until 2026, where platformsim.properties
        // named a class per adapter and each was brought up by name. It was for
        // running this simulator on the spacecraft itself and on the flatsat,
        // where some of the hardware is really there and the rest has to be
        // pretended: the camera answered by the camera, everything else by the
        // simulation. OPS-SAT flew that way. The adapters it named belonged to
        // the mission and were never in this repository, which is why nothing
        // here refers to them.
        //
        // It is gone because that is no longer the route. A mission that wants
        // its own hardware behind the platform services writes its own provider,
        // which is a plain dependency the compiler can see, rather than a class
        // name in a file that has to be found and loaded at startup.
        AIMovidiusAdapter aiAdapter;

        pcAdapter = new PowerControlSoftSimAdapter();
        CameraAdapterInterface camAdapter = new CameraSoftSimAdapter(sim, pcAdapter);
        AutonomousADCSAdapterInterface adcsAdapter = new AutonomousADCSSoftSimAdapter(sim, pcAdapter);
        GPSAdapterInterface gpsAdapter = new GPSSoftSimAdapter(sim, pcAdapter);
        OpticalDataReceiverAdapterInterface optRxAdapter = new OpticalDataReceiverSoftSimAdapter(sim, pcAdapter);
        SoftwareDefinedRadioAdapterInterface sdrAdapter = new SoftwareDefinedRadioSoftSimAdapter(sim, pcAdapter);

        try {
            aiAdapter = new AIMovidiusAdapter();
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "The AI adapter could not be started!", ex);
            aiAdapter = null;
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
        softwareImagesService.init(comServices, new SoftwareImagesSoftSimAdapter());
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

    @Override
    public SoftwareImagesInheritanceSkeleton getSoftwareImagesService() {
        return this.softwareImagesService;
    }
}
