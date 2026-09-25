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
import esa.mo.platform.impl.provider.gen.GPSProviderServiceImpl;
import esa.mo.platform.impl.provider.lite.GPSLiteAdapter;
import esa.mo.platform.impl.provider.lite.OrbitFromEnvironment;
import opssat.simulator.InstrumentsSimulator;
import opssat.simulator.Orbit;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.platform.artificialintelligence.provider.ArtificialIntelligenceInheritanceSkeleton;
import org.ccsds.moims.mo.platform.autonomousadcs.provider.AutonomousADCSInheritanceSkeleton;
import org.ccsds.moims.mo.platform.camera.provider.CameraInheritanceSkeleton;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwaredefinedradio.provider.SoftwareDefinedRadioInheritanceSkeleton;

/**
 * The Platform services of the lite simulator, which are the GPS service alone.
 * <p>
 * The simulator works the orbit out analytically rather than propagating it, so
 * it knows where the spacecraft is and nothing else: there is no attitude, no
 * magnetic field and no sun vector to answer with. The services that would need
 * them report that they are not supported, rather than answering with a
 * simulation that is not there.
 */
public class PlatformServicesProviderLite implements PlatformServicesProviderInterface {

    private final InstrumentsSimulator simulator = newSimulator();

    /**
     * @return The simulator, flying the orbit the environment describes, or the
     * one the simulator carries when it describes none.
     */
    private static InstrumentsSimulator newSimulator() {
        Orbit orbit = OrbitFromEnvironment.read();
        return (orbit != null) ? new InstrumentsSimulator(orbit) : new InstrumentsSimulator();
    }

    private final GPSProviderServiceImpl gpsService = new GPSProviderServiceImpl();

    @Override
    public void init(COMServicesProvider comServices) throws MALException {
        gpsService.init(comServices, new GPSLiteAdapter(simulator));
    }

    @Override
    public GPSProviderServiceImpl getGPSService() {
        return this.gpsService;
    }

    @Override
    public CameraInheritanceSkeleton getCameraService() {
        throw new UnsupportedOperationException(unsupported("Camera"));
    }

    @Override
    public AutonomousADCSInheritanceSkeleton getAutonomousADCSService() {
        throw new UnsupportedOperationException(unsupported("Autonomous ADCS"));
    }

    @Override
    public OpticalDataReceiverInheritanceSkeleton getOpticalDataReceiverService() {
        throw new UnsupportedOperationException(unsupported("Optical Data Receiver"));
    }

    @Override
    public SoftwareDefinedRadioInheritanceSkeleton getSoftwareDefinedRadioService() {
        throw new UnsupportedOperationException(unsupported("Software Defined Radio"));
    }

    @Override
    public ArtificialIntelligenceInheritanceSkeleton getAIService() {
        throw new UnsupportedOperationException(unsupported("Artificial Intelligence"));
    }

    private static String unsupported(String service) {
        return "The lite simulator has no " + service + " service. It reports "
                + "the position of the spacecraft; use "
                + "nmf-mission-simulator-orekit for the rest.";
    }
}
