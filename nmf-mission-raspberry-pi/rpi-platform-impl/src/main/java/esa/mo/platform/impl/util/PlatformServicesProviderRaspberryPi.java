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
import esa.mo.platform.impl.provider.gen.ArtificialIntelligenceProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.PowerControlProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.CameraProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.GPSProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.AutonomousADCSProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.OpticalDataReceiverProviderServiceImpl;
import esa.mo.platform.impl.provider.gen.SoftwareDefinedRadioProviderServiceImpl;
import esa.mo.platform.impl.provider.raspberrypi.CameraSingleImageAdapter;
import esa.mo.platform.impl.provider.raspberrypi.GPSProviderServiceWithTLEImpl;
import esa.mo.platform.impl.provider.raspberrypi.GPSSoftSimAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.platform.artificialintelligence.provider.ArtificialIntelligenceInheritanceSkeleton;

/**
 *
 *
 */
public class PlatformServicesProviderRaspberryPi implements PlatformServicesProviderInterface {

    private final static Logger LOGGER = Logger.getLogger(PlatformServicesProviderRaspberryPi.class.getName());

    private final ArtificialIntelligenceProviderServiceImpl aiService
            = new ArtificialIntelligenceProviderServiceImpl();

    private final AutonomousADCSProviderServiceImpl adcsService
            = new AutonomousADCSProviderServiceImpl();

    private final CameraProviderServiceImpl cameraService
            = new CameraProviderServiceImpl();

    private final GPSProviderServiceWithTLEImpl gpsService
            = new GPSProviderServiceWithTLEImpl();

    private final OpticalDataReceiverProviderServiceImpl optrxService
            = new OpticalDataReceiverProviderServiceImpl();

    private final PowerControlProviderServiceImpl powerService
            = new PowerControlProviderServiceImpl();

    private final SoftwareDefinedRadioProviderServiceImpl sdrService
            = new SoftwareDefinedRadioProviderServiceImpl();

    @Override
    public void init(COMServicesProvider comServices) throws MALException {
        try {
            GPSSoftSimAdapter gpsAdapter = new GPSSoftSimAdapter();
            gpsService.init(comServices, gpsAdapter);
            cameraService.init(comServices, new CameraSingleImageAdapter());
        } catch (UnsatisfiedLinkError | NoClassDefFoundError | NoSuchMethodError error) {
            LOGGER.log(Level.SEVERE, "Could not load platform adapters "
                    + "(check for missing JARs and libraries)", error);
        }
    }

    @Override
    public GPSProviderServiceImpl getGPSService() {
        return this.gpsService;
    }

    @Override
    public CameraProviderServiceImpl getCameraService() {
        return this.cameraService;
    }

    @Override
    public AutonomousADCSProviderServiceImpl getAutonomousADCSService() {
        return this.adcsService;
    }

    @Override
    public OpticalDataReceiverProviderServiceImpl getOpticalDataReceiverService() {
        return this.optrxService;
    }

    @Override
    public SoftwareDefinedRadioProviderServiceImpl getSoftwareDefinedRadioService() {
        return this.sdrService;
    }

    @Override
    public ArtificialIntelligenceInheritanceSkeleton getAIService() {
        //return this.aiService;
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
