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
package esa.mo.common.impl.proxy;

import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.nmf.NMFConsumer;
import java.net.MalformedURLException;
import java.util.Collection;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 */
public class DirectoryProxyServiceImpl extends DirectoryProviderServiceImpl {

    public void syncLocalDirectoryServiceWithCentral(final URI centralDirectoryServiceURI,
            final URI routedURI) throws MALException, MalformedURLException, MALInteractionException {
        ProviderSummaryList providers = NMFConsumer.retrieveProvidersFromDirectory(true, centralDirectoryServiceURI);
        addProxyPrefix(providers, routedURI.getValue());

        // Clean the current list of provider that are available
        // on the Local Directory service
        this.withdrawAllProviders();

        for (ProviderSummary provider : providers) {
            PublishDetails pub = new PublishDetails();
            pub.setDomain(provider.getProviderKey().getDomain());
            pub.setNetwork(new Identifier("not_available"));
            pub.setProviderDetails(provider.getProviderDetails());
            pub.setProviderName(provider.getProviderName());
            pub.setServiceXML(null);
            pub.setSessionType(SessionType.LIVE);
            pub.setSourceSessionName(null);
            this.publishProvider(pub, null);
        }
    }

    /**
     * Adds the protocol bridge as a prefix to the serviceURI and brokerURI.
     *
     * @param providers List of providers
     * @param proxyURI The URI of the protocol bridge
     * @throws IllegalArgumentException if the providers object is null
     */
    public static void addProxyPrefix(final ProviderSummaryList providers,
            final String proxyURI) throws IllegalArgumentException {
        if (providers == null) {
            throw new IllegalArgumentException("The provider object cannot be null.");
        }

        for (ProviderSummary provider : providers) {
            final ServiceCapabilityList capabilities = provider.getProviderDetails().getServiceCapabilities();

            for (ServiceCapability capability : capabilities) {
                for (AddressDetails dets : capability.getServiceAddresses()) {
                    String serviceURI = proxyURI + "@" + dets.getServiceURI().getValue();
//                    String serviceURI = dets.getServiceURI().getValue() + "@" + proxyURI;
                    dets.setServiceURI(new URI(serviceURI));

                    if (dets.getBrokerURI() != null) {
                        String brokerURI = proxyURI + "@" + dets.getBrokerURI().getValue();
//                        String brokerURI = dets.getBrokerURI().getValue() + "@" + proxyURI;
                        dets.setBrokerURI(new URI(brokerURI));
                    }
                }
            }
        }
    }

    public void rerouteCOMArchiveURI(IdentifierList providerDomain, URI to) {
        synchronized (MUTEX) {
            Collection<PublishDetails> providers = providersAvailable.values();

            for (PublishDetails provider : providers) {
                if (providerDomain.equals(provider.getDomain())) {
                    ServiceCapabilityList capabilities = provider.getProviderDetails().getServiceCapabilities();

                    // Find the Archive service
                    for (ServiceCapability capability : capabilities) {
                        ServiceKey key = capability.getServiceKey();

                        if (COMHelper._COM_AREA_NUMBER == key.getArea().getValue()
                                && ArchiveHelper._ARCHIVE_SERVICE_NUMBER == key.getService().getValue()
                                && COMHelper._COM_AREA_VERSION == key.getVersion().getValue()) {
                            AddressDetails details = capability.getServiceAddresses().get(0);
                            details.setServiceURI(to);
                        }
                    }
                }
            }
        }
    }
}
