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
package esa.mo.common.impl.util;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.AddressDetailsList;
import org.ccsds.moims.mo.common.directory.structures.ProviderDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.ServiceInfo;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.ServicesConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.StringList;

/**
 *
 * @author Cesar Coelho
 */
public class HelperCommon {

    private static final Logger LOGGER = Logger.getLogger(HelperCommon.class.getName());

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
        final HashMap<String, SingleConnectionDetails> services = new HashMap<>();

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

            final MALArea malArea = MALContextFactory.lookupArea(key.getKeyArea(), key.getKeyAreaVersion());

            if (malArea == null) {
                Logger.getLogger(HelperCommon.class.getName()).log(Level.WARNING,
                    "The service could not be found in the MAL factory. " +
                        "Maybe the Helper for that service was not initialized. " + "The service key is: " + key
                            .toString());
                continue;
            }

            final ServiceInfo malService = malArea.getServiceByNumber(key.getKeyService());

            if (malService == null) {
                Logger.getLogger(HelperCommon.class.getName()).log(Level.WARNING,
                    "The service could not be found in the MAL factory. " +
                        "Maybe the Helper for that service was not initialized. " + "The service key is: " + key
                            .toString());
                continue;
            }

            services.put(malService.getName().toString(), details);
        }

        serviceDetails.setServices(services);
        connection.setServicesDetails(serviceDetails);

        return connection;
    }

    /**
     * Filters services addresses of a given ProviderSummary. If a service exposes multiple
     * addresses (for multiple IPC transport), then only keep the best one. Best meaning
     * picking, if available, in the following order: tcpip, rmi, other, malspp.
     *
     * @param provider The ProviderSummary to filter
     * @return The filtered ProviderSummary
     */
    public static ProviderSummary selectBestIPCTransport(final ProviderSummary provider) {
        //newSummary.setProviderKey(provider.getProviderKey());
        //newSummary.setProviderId(provider.getProviderId());
        //newSummary.setProviderDetails(details);

        final ServiceCapabilityList oldCapabilities = provider.getProviderDetails().getServiceCapabilities();
        final ServiceCapabilityList newCapabilities = new ServiceCapabilityList();
        final ProviderDetails details = new ProviderDetails(newCapabilities,
            provider.getProviderDetails().getProviderAddresses());
        final ProviderSummary newSummary = new ProviderSummary(provider.getProviderKey(),
            provider.getProviderId(), details);

        for (int i = 0; i < oldCapabilities.size(); i++) {
            AddressDetailsList addresses = oldCapabilities.get(i).getServiceAddresses();
            AddressDetailsList serviceAddresses = null;

            try {
                final int bestIndex = getBestIPCServiceAddressIndex(addresses);

                // Select only the best address for IPC
                serviceAddresses = new AddressDetailsList();
                serviceAddresses.add(addresses.get(bestIndex));
            } catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE,
                        "The best IPC service address index could not be determined!", ex);
            }
            ServiceCapability cap = new ServiceCapability(
                oldCapabilities.get(i).getServiceKey(),
                oldCapabilities.get(i).getSupportedCapabilitySets(),
                oldCapabilities.get(i).getServiceProperties(),
                serviceAddresses);
            /*
            cap.setServiceKey(oldCapabilities.get(i).getServiceKey());
            cap.setSupportedCapabilitySets(oldCapabilities.get(i).getSupportedCapabilitySets());
            cap.setServiceProperties(oldCapabilities.get(i).getServiceProperties());
            cap.setServiceAddresses(newAddresses);
            */
            newCapabilities.add(cap);
        }

        //details.setServiceCapabilities(newCapabilities);
        //details.setProviderAddresses(provider.getProviderDetails().getProviderAddresses());

        return newSummary;
    }

    /**
     * Select the address with the best IPC transport from a given list of addresses.
     * Best meaning picking, if available, in the following order: tcpip, rmi, other, malspp.
     *
     * @param addresses The list of addresses
     * @return Index of the address in the list with the best IPC transport
     * @throws IllegalArgumentException If addresses is empty
     */
    public static int getBestIPCServiceAddressIndex(AddressDetailsList addresses) throws
        IllegalArgumentException
    {
      if (addresses.isEmpty()) {
        throw new IllegalArgumentException("The addresses argument cannot be empty.");
      }

      if (addresses.size() == 1) { // Well, there is only one...
        return 0;
      }

      // Well, if there are more than one, then it means we can pick...
      // My preference would be, in order: tcp/ip, rmi, other, spp
      // SPP is in last because usually this is the transport supposed
      // to be used on the ground-to-space link and not internally.
      StringList availableTransports = getAvailableTransports(addresses);

      int index = getTransportIndex(availableTransports, "tcpip");
      if (index != -1) {
        return index;
      }

      index = getTransportIndex(availableTransports, "rmi");
      if (index != -1) {
        return index;
      }

      index = getTransportIndex(availableTransports, "malspp");

      // If could not be found nor it is not the first one
      if (index != 0) { // Then let's pick the first one
        return 0;
      } else {
        // It was found and it is the first one (0)
        // Then let's select the second (index == 1) transport available...
        return 1;
      }
    }

    private static StringList getAvailableTransports(AddressDetailsList addresses)
    {
      StringList transports = new StringList(); // List of transport names

      for (AddressDetails address : addresses) {
        // The name of the transport is always before ":"
        String[] parts = address.getServiceURI().toString().split(":");
        transports.add(parts[0]);
      }

      return transports;
    }

    private static int getTransportIndex(StringList transports, String findString)
    {
      for (int i = 0; i < transports.size(); i++) {
        if (findString.equals(transports.get(i))) {
          return i;  // match
        }
      }
      return -1;
    }
}
