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
package esa.mo.nmf.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.nanosatmosupervisor.NanoSatMOSupervisor;
import esa.mo.nmf.nmfpackage.NMFPackagePMBackend;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesProviderSoftSim;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * The implementation of the NanoSat MO Supervisor for the Software Simulator
 * mission.
 *
 * @author Cesar Coelho
 */
public class NanoSatMOSupervisorSoftSimImpl extends NanoSatMOSupervisor {

    private PlatformServicesProviderSoftSim platformServicesSim;

    @Override
    public void init(MonitorAndControlNMFAdapter mcAdapter) {
        super.init(mcAdapter, new PlatformServicesConsumer(), new NMFPackagePMBackend());
    }

    @Override
    public void initPlatformServices(COMServicesProvider comServices) {
        try {
            platformServicesSim = new PlatformServicesProviderSoftSim();
            platformServicesSim.init(comServices);
            this.reconfigurableServices.add(platformServicesSim.getAutonomousADCSService());
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOSupervisorSoftSimImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ConnectionConsumer connectionConsumer = new ConnectionConsumer();
            connectionConsumer.loadURIs();
            super.getPlatformServices().init(connectionConsumer, null);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NanoSatMOSupervisorSoftSimImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NMFException ex) {
            Logger.getLogger(NanoSatMOSupervisorSoftSimImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        NanoSatMOSupervisorSoftSimImpl supervisor = new NanoSatMOSupervisorSoftSimImpl();
        supervisor.init(new MCSoftwareSimulatorAdapter());
    }

}
