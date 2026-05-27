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
package esa.mo.com.impl.proxy;

import esa.mo.com.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.NMFConsumer;
import java.net.MalformedURLException;
import java.util.Collection;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveServiceInfo;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.ServiceId;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.action.ActionServiceInfo;

/**
 * The DirectoryProxyServiceImpl class extends the DirectoryProviderServiceImpl
 * class in order to allow the routing of the communications to other ground
 * systems.
 */
public class DirectoryProxyServiceImpl extends DirectoryProviderServiceImpl {

    /**
     * Synchronizes the current list of providers existing in the remote Central
     * Directory service with the local one. Returns the list of remote
     * providers.
     *
     * @param centralDirectoryServiceURI The URI of the central Directory
     * service.
     * @param routedURI The routed URI.
     * @return The list of the providers from the remote Central Directory
     * service
     * @throws org.ccsds.moims.mo.mal.MALException if there is a MAL exception.
     * @throws java.net.MalformedURLException if the URI is incorrect.
     * @throws org.ccsds.moims.mo.mal.MALInteractionException if it could not
     * reach the Directory service.
     */
    public ProviderList syncLocalDirectoryServiceWithCentral(final URI centralDirectoryServiceURI,
            final URI routedURI) throws MALException, MalformedURLException, MALInteractionException {
        IdentifierList schemeFilter = new IdentifierList();
        schemeFilter.add(new Identifier("malspp"));
        ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                centralDirectoryServiceURI, schemeFilter);
        ProviderList updatedProviders = addProxyPrefix(providers, routedURI.getValue());

        // Clean the current list of provider that are available
        // on the Local Directory service
        this.withdrawAllProviders();

        for (Provider provider : updatedProviders) {
            PublishDetails pub = new PublishDetails(
                    provider.getProviderName(),
                    provider.getDomain(),
                    new Identifier("not_available"),
                    provider.getProviderDetails());
            this.add(pub, null);
        }

        // Make the Ground MO Proxy (itself) also available in the list of providers
        this.loadURIs(Const.NANOSAT_MO_GROUND_PROXY_NAME);
        return updatedProviders;
    }

    /**
     * Adds the protocol bridge as a prefix to the serviceURI and brokerURI.
     *
     * @param providers List of providers
     * @param proxyURI The URI of the protocol bridge
     * @return The updated providers list with the proxy prefix.
     * @throws IllegalArgumentException if the providers object is null
     */
    public static ProviderList addProxyPrefix(final ProviderList providers,
            final String proxyURI) throws IllegalArgumentException {
        if (providers == null) {
            throw new IllegalArgumentException("The provider object cannot be null.");
        }

        ProviderList updatedProviders = new ProviderList();

        for (Provider in : providers) {
            ProviderDetails oldDetails = in.getProviderDetails();
            final ServiceCapabilityList capabilities = oldDetails.getServiceCapabilities();
            final ServiceCapabilityList newCapabilities = new ServiceCapabilityList();

            for (ServiceCapability capability : capabilities) {
                AddressDetailsList newDets = new AddressDetailsList();
                for (AddressDetails dets : capability.getServiceAddresses()) {
                    String serviceURI = proxyURI + "@" + dets.getServiceURI().getValue();
                    URI brokerURI = null;

                    if (dets.getBrokerURI() != null) {
                        brokerURI = new URI(proxyURI + "@" + dets.getBrokerURI().getValue());
                    }

                    AddressDetails addressDetails = new AddressDetails(
                            new URI(serviceURI),
                            brokerURI);

                    newDets.add(addressDetails);
                }

                newCapabilities.add(new ServiceCapability(
                        capability.getServiceId(),
                        capability.getSupportedCapabilitySets(),
                        capability.getServiceProperties(),
                        newDets));
            }
            ProviderDetails newDetails = new ProviderDetails(
                    newCapabilities,
                    oldDetails.getProviderAddresses()
            );

            updatedProviders.add(new Provider(in.getDomain(), in.getId(), in.getProviderName(), newDetails));
        }

        return updatedProviders;
    }

    /**
     * Reroutes the Archive service URI of a specific provider to a different
     * URI.
     *
     * @param providerDomain The domain of the provider to be routed.
     * @param to The new URI.
     */
    public void rerouteArchiveServiceURI(IdentifierList providerDomain, URI to) {
        synchronized (MUTEX) {
            Collection<PublishDetails> providers = providersAvailable.values();

            for (PublishDetails provider : providers) {
                if (providerDomain.equals(provider.getDomain())) {
                    ServiceCapabilityList capabilities = provider.getProviderDetails().getServiceCapabilities();

                    // Find the Archive service
                    for (ServiceCapability capability : capabilities) {
                        ServiceId key = capability.getServiceId();

                        if (COMHelper._COM_AREA_NUMBER == key.getKeyArea().getValue()
                                && ArchiveServiceInfo._ARCHIVE_SERVICE_NUMBER == key.getKeyService().getValue()
                                && COMHelper._COM_AREA_VERSION == key.getKeyAreaVersion().getValue()) {
                            //AddressDetails details = capability.getServiceAddresses().get(0);
                            //details.setServiceURI(to);
                            throw new RuntimeException("This feature was disabled. Please update the code here!");
                        }
                    }
                }
            }
        }
    }

    /**
     * Reroutes the Action service URI of a specific provider to a different
     * URI.
     *
     * @param providerDomain The domain of the provider to be routed.
     * @param to The new URI.
     */
    public void rerouteActionServiceURI(IdentifierList providerDomain, URI to) {
        synchronized (MUTEX) {
            Collection<PublishDetails> providers = providersAvailable.values();

            for (PublishDetails provider : providers) {
                if (providerDomain.equals(provider.getDomain())) {
                    ServiceCapabilityList capabilities = provider.getProviderDetails().getServiceCapabilities();

                    // Find the Archive service
                    for (ServiceCapability capability : capabilities) {
                        ServiceId key = capability.getServiceId();

                        if (MCHelper._MC_AREA_NUMBER == key.getKeyArea().getValue()
                                && ActionServiceInfo._ACTION_SERVICE_NUMBER == key.getKeyService().getValue()
                                && MCHelper._MC_AREA_VERSION == key.getKeyAreaVersion().getValue()) {
                            //AddressDetails details = capability.getServiceAddresses().get(0);
                            //details.setServiceURI(to);
                            throw new RuntimeException("This feature was disabled. Please update the code here!");
                        }
                    }
                }
            }
        }
    }

}
