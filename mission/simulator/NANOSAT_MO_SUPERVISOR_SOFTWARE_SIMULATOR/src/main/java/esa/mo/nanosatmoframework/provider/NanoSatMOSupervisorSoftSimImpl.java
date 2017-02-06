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
package esa.mo.nanosatmoframework.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.nanosatmosupervisor.NanoSatMOSupervisor;
import esa.mo.nmf.packager.PackageManagementBackendNMFPackage;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesProviderSoftSim;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * A Provider of MO services composed by COM, MC and Platform services. Selects
 * the transport layer based on the selected values of the properties file and
 * initializes all services automatically. Provides configuration persistence,
 * therefore the last state of the configuration of the MO services will be kept
 * upon restart. Additionally, the NanoSat MO Framework implements an
 * abstraction layer over the Back-End of some MO services to facilitate the
 * monitoring of the business logic of the app using the NanoSat MO Framework.
 *
 * @author Cesar Coelho
 */
public class NanoSatMOSupervisorSoftSimImpl extends NanoSatMOSupervisor {

    private PlatformServicesProviderSoftSim platformServicesSim;

    /**
     * NanoSat MO Supervisor for the Software Simulator
     *
     */
    public NanoSatMOSupervisorSoftSimImpl() {
        super(new MCSoftwareSimulatorAdapter(),
                new PlatformServicesConsumer(),
                new PackageManagementBackendNMFPackage());
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        NanoSatMOSupervisorSoftSimImpl supervisor = new NanoSatMOSupervisorSoftSimImpl();
    }

    @Override
    public void initPlatformServices(COMServicesProvider comServices) {
        try {
            platformServicesSim = new PlatformServicesProviderSoftSim();
            platformServicesSim.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOSupervisorSoftSimImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        ConnectionConsumer connectionConsumer = new ConnectionConsumer();

        try {
            connectionConsumer.loadURIs();
            super.getPlatformServices().init(connectionConsumer, null);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NanoSatMOSupervisorSoftSimImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
