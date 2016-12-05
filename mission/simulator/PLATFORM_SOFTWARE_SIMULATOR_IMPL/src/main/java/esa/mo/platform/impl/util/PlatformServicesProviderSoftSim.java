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
package esa.mo.platform.impl.util;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.platform.impl.provider.gen.AutonomousADCSProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.CameraProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.GPSProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.MagnetometerProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.OpticalDataReceiverProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.SoftwareDefinedRadioProviderServiceImpl;
import esa.mo.platform.impl.provider.softsim.AutonomousADCSSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.CameraSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.GPSSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.MagnetometerSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.OpticalDataReceiverSoftSimAdapter;
import esa.mo.platform.impl.provider.softsim.SoftwareDefinedRadioSoftSimAdapter;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.platform.autonomousadcs.provider.AutonomousADCSInheritanceSkeleton;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwaredefinedradio.provider.SoftwareDefinedRadioInheritanceSkeleton;

/**
 *
 *
 */
public class PlatformServicesProviderSoftSim implements PlatformServicesProviderInterface {

    // Simulator
    private final ESASimulator instrumentsSimulator = new ESASimulator("127.0.0.1");

    // Services
    private final AutonomousADCSProviderServiceImpl autonomousADCSService = new AutonomousADCSProviderServiceImpl();
    private final CameraProviderServiceImpl cameraService = new CameraProviderServiceImpl();
    private final GPSProviderServiceImpl gpsService = new GPSProviderServiceImpl();
    private final MagnetometerProviderServiceImpl magnetometerService = new MagnetometerProviderServiceImpl();
    private final OpticalDataReceiverProviderServiceImpl opticalDataReceiverService = new OpticalDataReceiverProviderServiceImpl();
    private final SoftwareDefinedRadioProviderServiceImpl sdrService = new SoftwareDefinedRadioProviderServiceImpl();

    public void init(COMServicesProvider comServices) throws MALException {
        autonomousADCSService.init(comServices, new AutonomousADCSSoftSimAdapter(instrumentsSimulator));
        cameraService.init(comServices, new CameraSoftSimAdapter(instrumentsSimulator));
        gpsService.init(comServices, new GPSSoftSimAdapter(instrumentsSimulator));
        magnetometerService.init(new MagnetometerSoftSimAdapter(instrumentsSimulator));
        opticalDataReceiverService.init(new OpticalDataReceiverSoftSimAdapter(instrumentsSimulator));
        sdrService.init(new SoftwareDefinedRadioSoftSimAdapter(instrumentsSimulator));
    }

    @Override
    public AutonomousADCSInheritanceSkeleton getAutonomousADCSService() {
        return this.autonomousADCSService;
    }

    @Override
    public CameraProviderServiceImpl getCameraService() {
        return this.cameraService;
    }

    @Override
    public GPSProviderServiceImpl getGPSService() {
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
    
}
