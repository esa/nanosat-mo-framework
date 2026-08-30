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
package esa.mo.nmf.mission.lite;

import esa.mo.com.impl.util.COMServicesProvider;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.mcadapters.MCSupervisorBasicAdapter;
import esa.mo.nmf.nanosatmosupervisor.NanoSatMOSupervisor;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesProviderLite;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * The NanoSat MO Supervisor of the lite simulator mission.
 * <p>
 * The mission is the simulator without the orbital mechanics library: the orbit
 * is worked out analytically, so the spacecraft knows where it is and nothing
 * else. The Platform services are the GPS service alone, which is what makes
 * this Supervisor lighter than the one of the Orekit mission rather than merely
 * different from it.
 */
public class LiteSupervisorImpl extends NanoSatMOSupervisor {

    private static final Logger LOGGER = Logger.getLogger(LiteSupervisorImpl.class.getName());

    private final PlatformServicesProviderLite platformServicesProvider
            = new PlatformServicesProviderLite();

    /**
     * Default constructor.
     */
    public LiteSupervisorImpl() {
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        LiteSupervisorImpl supervisor = new LiteSupervisorImpl();
        MCSupervisorBasicAdapter adapter = new MCSupervisorBasicAdapter(supervisor);
        supervisor.init(adapter);
    }

    @Override
    public void init(MonitorAndControlNMFAdapter mcAdapter) {
        init(mcAdapter, new PlatformServicesConsumer());
    }

    @Override
    public void initPlatformServices(COMServicesProvider comServices) {
        try {
            platformServicesProvider.init(comServices);
        } catch (MALException ex) {
            LOGGER.log(Level.SEVERE, "The Platform services could not be started!", ex);
        }

        // The Supervisor consumes the services it provides, over the loopback.
        try {
            ConnectionConsumer connection = new ConnectionConsumer();
            connection.setServicesDetails(ConnectionProvider.getGlobalProvidersDetailsPrimary());
            super.getPlatformServices().init(connection, null);
        } catch (NMFException ex) {
            LOGGER.log(Level.SEVERE, "The Platform services consumer could not be started!", ex);
        }
    }
}
