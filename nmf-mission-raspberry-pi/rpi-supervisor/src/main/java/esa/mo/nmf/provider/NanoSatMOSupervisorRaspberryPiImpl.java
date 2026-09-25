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
package esa.mo.nmf.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.nanosatmosupervisor.NanoSatMOSupervisor;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesProviderInterface;
import esa.mo.platform.impl.util.PlatformServicesProviderRaspberryPi;
import esa.mo.platform.impl.util.PlatformServicesProviderSoftSim;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * The implementation of the NanoSat MO Supervisor for a Raspberry Pi.
 *
 * @author Cesar Coelho
 */
public final class NanoSatMOSupervisorRaspberryPiImpl extends NanoSatMOSupervisor {

    //  private PlatformServicesProviderSoftSim platformServicesProviderSoftSim;
    private PlatformServicesProviderInterface platformServicesRaspberryPi;

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception if there is an error
     */
    public static void main(final String args[]) throws Exception {
        NanoSatMOSupervisorRaspberryPiImpl supervisor = new NanoSatMOSupervisorRaspberryPiImpl();
        supervisor.init(new MCRaspberryPiAdapter(supervisor));
    }

    @Override
    public void init(final MonitorAndControlNMFAdapter mcAdapter) {
        super.init(mcAdapter,
                new PlatformServicesConsumer(),
                null
        );
    }

    @Override
    public void initPlatformServices(COMServicesProvider comServices) {
        try {
            // TODO: choose based on system properties
            if (true) {
                platformServicesRaspberryPi = new PlatformServicesProviderRaspberryPi();
            } else {
                platformServicesRaspberryPi = new PlatformServicesProviderSoftSim();
            }
            platformServicesRaspberryPi.init(comServices);

        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOSupervisorRaspberryPiImpl.class.getName()).log(
                    Level.SEVERE, "Something went wrong!", ex);
        }
    }

}
