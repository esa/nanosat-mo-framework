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
package esa.mo.nanosatmoframework.groundmoproxy;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nanosatmoframework.groundmoadapter.GroundMOAdapter;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.mal.MALException;


/**
 * A Consumer of MO services composed by COM, M&C and Platform services.
 * Implements the SimpleCommandingInterface that permits an external software
 * entity to send data (parameters or serialized objects) to the provider, add
 * a DataReceivedListener to receive data and send actions to the provider.
 * It extends the MOServicesConsumer class in order make available the pure MO 
 * interfaces.
 * 
 * @author Cesar Coelho
 */
public class GroundMOProxy  {

    // Have a list of providers
    private COMServicesProvider comServicesProxy = new COMServicesProvider();
    private HashMap<String, GroundMOAdapter> myProviders = new HashMap<String, GroundMOAdapter>();
    
    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     */
    public GroundMOProxy(ConnectionConsumer connection) {

        try {
            comServicesProxy.init();
        } catch (MALException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        

    }

    /**
     * The constructor of this class
     *
     * @param providerDetails The Provider details. This object can be obtained 
     * from the Directory service
     */
    public GroundMOProxy(ProviderSummary providerDetails) {

    }
    
    public void connectToProvider(ProviderSummary providerDetails){
        
    }


}
