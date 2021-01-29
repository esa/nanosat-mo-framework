/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALService;

/**
 *
 * @author Cesar Coelho
 */
public class HelperCommon {

    /**
     * Generates the ConnectionConsumer from the ProviderSummary. It will select
     * the first URI available on the Service Addresses list, so the one with
     * index 0.
     *
     * @param provider The ProviderSummary object
     * @return ConnectionConsumer The ConnectionConsumer object
     */
    public static ConnectionConsumer providerSummaryToConnectionConsumer(ProviderSummary provider) {
        final ConnectionConsumer connection = new ConnectionConsumer();

        final ServicesConnectionDetails serviceDetails = new ServicesConnectionDetails();
        final HashMap<String, SingleConnectionDetails> services = new HashMap<String, SingleConnectionDetails>();

        // Cycle all the services in the provider and put them in the serviceDetails object
        for (ServiceCapability serviceInfo : provider.getProviderDetails().getServiceCapabilities()) {
            ServiceKey key = serviceInfo.getServiceKey();
            AddressDetails addressDetails;

            // If there are no address info we cannot connect...
            if (!serviceInfo.getServiceAddresses().isEmpty()) {
                // Select the first one (index: 0)
                addressDetails = serviceInfo.getServiceAddresses().get(0);

                if (serviceInfo.getServiceAddresses().size() != 1) {
                    Logger.getLogger(HelperCommon.class.getName()).log(Level.WARNING,
                            "There are more than just one service address in the ServiceCapability.");
                }
            } else {
                continue;
            }

            SingleConnectionDetails details = new SingleConnectionDetails();
            details.setBrokerURI(addressDetails.getBrokerURI());
            details.setProviderURI(addressDetails.getServiceURI());
            details.setDomain(provider.getProviderKey().getDomain());

            final MALArea malArea = MALContextFactory.lookupArea(key.getArea(), key.getVersion());

            if (malArea == null) {
                Logger.getLogger(HelperCommon.class.getName()).log(Level.WARNING,
                        "The service could not be found in the MAL factory. "
                        + "Maybe the Helper for that service was not initialized. "
                        + "The service key is: " + key.toString());
                continue;
            }

            final MALService malService = malArea.getServiceByNumber(key.getService());

            if (malService == null) {
                Logger.getLogger(HelperCommon.class.getName()).log(Level.WARNING,
                        "The service could not be found in the MAL factory. "
                        + "Maybe the Helper for that service was not initialized. "
                        + "The service key is: " + key.toString());
                continue;
            }

            services.put(malService.getName().toString(), details);
        }

        serviceDetails.setServices(services);
        connection.setServicesDetails(serviceDetails);

        return connection;
    }

}
