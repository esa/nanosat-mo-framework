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
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.nanosatmomonolithic.NanoSatMOMonolithic;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesProviderSoftSim;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

/**
 * NanoSat MO Monolithic OPS-SAT Software Simulator
 *
 * @author Cesar Coelho
 */
public class NanoSatMOMonolithicSim extends NanoSatMOMonolithic {

    private static final Logger LOGGER = Logger.getLogger(NanoSatMOMonolithicSim.class.getName());

    private PlatformServicesProviderSoftSim provider;

    /**
     * To initialize the NanoSat MO Monolithic with this method, it is necessary to extend the
     * MonitorAndControlAdapter adapter class. The SimpleMonitorAndControlAdapter class contains a
     * simpler interface which allows sending directly parameters of the most common java types and it
     * also allows the possibility to send serializable objects.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the corresponding methods
     *                  and variables of a specific entity.
     */
    @Override
    public void init(MonitorAndControlNMFAdapter mcAdapter) {
        super.init(mcAdapter, new PlatformServicesConsumer());
    }

    @Override
    public void initPlatformServices(COMServicesProvider comServices) {
        // We need to do a bypass here because it's a Monolithic implementation
        // of the Software Simulator. Basically, we initialize the Platform
        // service providers and then we initialize the consumers' connections
        // to them.

        try {
            provider = new PlatformServicesProviderSoftSim();
            provider.init(comServices);
        } catch (MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        ConnectionConsumer connectionConsumer = new ConnectionConsumer();

        try {
            connectionConsumer.loadURIs();
            super.getPlatformServices().init(connectionConsumer, null);
        } catch (MalformedURLException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
