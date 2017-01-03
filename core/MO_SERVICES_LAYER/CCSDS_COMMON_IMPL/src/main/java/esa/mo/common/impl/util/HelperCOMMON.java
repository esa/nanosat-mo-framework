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
package esa.mo.common.impl.util;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ServicesConnectionDetails;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALService;

/**
 *
 * @author Cesar Coelho
 */
public class HelperCOMMON {

    /**
     * Generates the ConnectionConsumer from the ProviderSummary
     * 
     * @param provider The ProviderSummary object
     * @return ConnectionConsumer The ConnectionConsumer object
     */
    public static ConnectionConsumer providerSummaryToConnectionConsumer(ProviderSummary provider){
        
        ConnectionConsumer connection = new ConnectionConsumer();
        
        ServicesConnectionDetails serviceDetails = new ServicesConnectionDetails();
        HashMap<String, SingleConnectionDetails> services = new HashMap<String, SingleConnectionDetails>();

        // Cycle all the services in the provider and put them in the serviceDetails object
        for (ServiceCapability serviceInfo : provider.getProviderDetails().getServiceCapabilities()) {  // Add all the tabs!

            ServiceKey key = serviceInfo.getServiceKey();
            AddressDetails addressDetails;

            if (!serviceInfo.getServiceAddresses().isEmpty()) {  // If there are no address info we cannot connect...
                addressDetails = serviceInfo.getServiceAddresses().get(0);
            } else {
                continue;
            }

            SingleConnectionDetails details = new SingleConnectionDetails();
            details.setBrokerURI(addressDetails.getBrokerURI());
            details.setProviderURI(addressDetails.getServiceURI());
            details.setDomain(provider.getProviderKey().getDomain());

            MALService malService = MALContextFactory.lookupArea(key.getArea(), key.getVersion()).getServiceByNumber(key.getService());
            
            if(malService == null){
                Logger.getLogger(HelperCOMMON.class.getName()).log(Level.WARNING, "The service could not be found in the MAL factory. Maybe the Helper for that service was not initialized. The service key is: " + key.toString());
                continue;
            }
            
            services.put(malService.getName().toString(), details);

        }

        serviceDetails.setServices(services);
        connection.setServicesDetails(serviceDetails);
        
        return connection;
    }

}
