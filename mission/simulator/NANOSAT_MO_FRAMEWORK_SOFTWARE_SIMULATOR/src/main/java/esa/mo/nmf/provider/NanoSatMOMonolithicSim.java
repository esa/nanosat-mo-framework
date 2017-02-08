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
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
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

    private PlatformServicesProviderSoftSim provider;
    
    /**
     * To initialize the NanoSat MO Monolithic with this method, it is necessary
     * to implement the ActionInvocationListener interface and the
     * ParameterStatusListener interface.
     *
     * @param actionAdapter The adapter to connect the actions to the
     * corresponding method of a specific entity.
     * @param parameterAdapter The adapter to connect the parameters to the
     * corresponding variable of a specific entity.
     */
    /*
    public NanoSatMOMonolithicSim(ActionInvocationListener actionAdapter, ParameterStatusListener parameterAdapter) {
        super(actionAdapter, parameterAdapter, new PlatformServicesConsumer());
    }
    */

    /**
     * To initialize the NanoSat MO Monolithic with this method, it is necessary
     * to extend the MonitorAndControlAdapter adapter class. The
     * SimpleMonitorAndControlAdapter class contains a simpler interface which
     * allows sending directly parameters of the most common java types and it
     * also allows the possibility to send serializable objects.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the
     * corresponding methods and variables of a specific entity.
     */
    public NanoSatMOMonolithicSim(MonitorAndControlNMFAdapter mcAdapter) {
        super(mcAdapter, new PlatformServicesConsumer());
    }

    @Override
    public void initPlatformServices(COMServicesProvider comServices) {
        // We need to do a bybass here because it a Monolithic implementation
        // of the Software Simulator. Basically, we initialize the Platform 
        // service providers and then we initialize the consumers' connections
        // to them.
        
        try {
            provider = new PlatformServicesProviderSoftSim();
            provider.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOMonolithicSim.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        ConnectionConsumer connectionConsumer = new ConnectionConsumer();

        try {
            connectionConsumer.loadURIs();
            super.getPlatformServices().init(connectionConsumer, null);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NanoSatMOMonolithicSim.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
