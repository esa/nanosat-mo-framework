/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.provider.ArchiveManager;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.InvalidException;
import org.ccsds.moims.mo.com.directory.DirectoryHelper;
import org.ccsds.moims.mo.com.directory.DirectoryServiceInfo;
import org.ccsds.moims.mo.com.directory.provider.DirectoryInheritanceSkeleton;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.connections.ServicesConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * Directory service Provider.
 */
public class DirectoryProviderServiceImpl extends DirectoryInheritanceSkeleton {

    private static final Logger LOGGER
            = Logger.getLogger(DirectoryProviderServiceImpl.class.getName());

    private MALProvider directoryServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    protected final Map<Long, Provider> providersAvailable = new ConcurrentHashMap<>();
    protected final Object MUTEX = new Object();
    private COMServicesProvider comServices;

    /**
     * Creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices The COM services.
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices) throws MALException {
        long timestamp = System.currentTimeMillis();
        this.comServices = comServices;

        // shut down old service transport
        if (directoryServiceProvider != null) {
            connection.closeAll();
        }

        directoryServiceProvider = connection.startService(
                DirectoryHelper.DIRECTORY_SERVICE, false, this);

        running = true;
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Directory service: READY! (" + timestamp + " ms)");
    }

    private static AddressDetails getServiceAddressDetails(final SingleConnectionDetails conn) {
        return new AddressDetails(conn.getProviderURI(), conn.getBrokerURI());
    }

    private static AddressDetailsList findAddressDetailsListOfService(final ServiceId key,
            final ServiceCapabilityList capabilities) {
        if (key == null) {
            return null;
        }

        // Iterate all capabilities until you find the serviceName
        for (ServiceCapability capability : capabilities) {
            if (capability != null) {
                if (key.equals(capability.getServiceId())) {
                    return capability.getServiceAddresses();
                }
            }
        }

        return null; // Not found!
    }

    public static ServiceId generateServiceKey(final IntegerList keys) {
        return new ServiceId(new UShort(keys.get(0)), new UShort(keys.get(1)), new UOctet(keys.get(2).shortValue()));
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != directoryServiceProvider) {
                directoryServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public ConnectionProvider getConnection() {
        return this.connection;
    }

    @Override
    public ProviderList lookup(final ServiceFilter filter,
            final MALInteraction interaction) throws MALInteractionException, MALException {
        if (filter == null) { // Is the input null?
            throw new IllegalArgumentException("filter argument must not be null");
        }

        final IdentifierList inputDomain = filter.getDomain();

        // Check if the domain contains any wildcard that is not in the end, if so, throw error
        for (int i = 0; i < inputDomain.size(); i++) {
            Identifier domainPart = inputDomain.get(i);

            if (domainPart.toString().equals("*") && i != (inputDomain.size() - 1)) {
                throw new MALInteractionException(new InvalidException(null));
            }
        }

        final HashMap<Long, Provider> list;

        synchronized (MUTEX) {
            list = new HashMap<>(providersAvailable);
        }

        LongList keys = new LongList();
        keys.addAll(list.keySet());

        // Initialize the final Provider Summary List
        ProviderList outputList = new ProviderList();

        // Filter...
        for (int i = 0; i < keys.size(); i++) { // Filter through all providers
            Provider provider = list.get(keys.get(i));

            //Check service provider name
            if (!filter.getProviderName().toString().equals("*")) { // If not a wildcard...
                if (!provider.getProviderName().toString().equals(filter.getProviderName().toString())) {
                    continue;
                }
            }

            if (HelperCOM.domainContainsWildcard(filter.getDomain())) {  // Does it contain a wildcard in the filter?
                // Compare each object one by one...

                if (!HelperCOM.domainMatchesWildcardDomain(provider.getDomain(), inputDomain)) {
                    continue;
                }

            } else if (!inputDomain.equals(provider.getDomain())) {
                continue;
            }

            // Set the Provider Details structure
            ServiceCapabilityList outCap = new ServiceCapabilityList();

            // Go through all the services and check each service
            for (ServiceCapability serviceCapability : provider.getServiceCapabilities()) {
                // Check service key - area field
                if (filter.getServiceId().getKeyArea().getValue() != 0) {
                    if (!serviceCapability.getServiceId().getKeyArea().equals(filter.getServiceId().getKeyArea())) {
                        continue;
                    }
                }

                // Check service key - service field
                if (filter.getServiceId().getKeyService().getValue() != 0) {
                    if (!serviceCapability.getServiceId().getKeyService().equals(
                            filter.getServiceId().getKeyService())) {
                        continue;
                    }
                }

                // Check service key - version field
                if (filter.getServiceId().getKeyAreaVersion().getValue() != 0) {
                    if (!serviceCapability.getServiceId().getKeyAreaVersion().equals(
                            filter.getServiceId().getKeyAreaVersion())) {
                        continue;
                    }
                }

                ServiceCapability newServiceCapability = new ServiceCapability(
                        serviceCapability.getServiceId(),
                        serviceCapability.getServiceProperties(),
                        new AddressDetailsList()
                );

                IdentifierList schemeFilter = filter.getAddressSchemeFilter();
                if (schemeFilter != null && !schemeFilter.isEmpty()) {
                    for (AddressDetails addr : serviceCapability.getServiceAddresses()) {
                        String uri = addr.getServiceURI().toString();
                        for (Identifier scheme : schemeFilter) {
                            if (uri.startsWith(scheme.getValue())) {
                                newServiceCapability.getServiceAddresses().add(addr);
                                break;
                            }
                        }
                    }
                } else {
                    newServiceCapability.getServiceAddresses().addAll(serviceCapability.getServiceAddresses());
                }

                // Add the service to the list of matching services
                outCap.add(newServiceCapability);
            }

            // It passed all the tests!
            outputList.add(new Provider(keys.get(i),
                    provider.getProviderName(), provider.getDomain(), outCap,
                    provider.getProviderAddresses(), provider.getProviderType()));
        }

        // Errors
        // The operation does not return any errors.
        return outputList;  // requirement: 3.4.9.2.d
    }

    @Override
    public Long add(final Provider newProviderDetails,
            final MALInteraction interaction) throws MALInteractionException, MALException {
        Identifier serviceProviderName = newProviderDetails.getProviderName();

        synchronized (MUTEX) {
            final HashMap<Long, Provider> list = new HashMap<>(providersAvailable);

            // Do we already have this provider in the Directory service?
            for (Long key : list.keySet()) {
                Provider provider = this.providersAvailable.get(key);

                if (serviceProviderName.getValue().equals(provider.getProviderName().getValue())) {
                    // It is repeated!!
                    LOGGER.warning("There was already a provider with the same name in the "
                            + "Directory service. Removing the old one and adding the new one...");
                    remove(key, null);
                }
            }

            ArchiveDetailsList archDetails = (interaction == null)
                    ? HelperArchive.generateArchiveDetailsList(null, null,
                            connection.getPrimaryConnectionDetails().getProviderURI())
                    : HelperArchive.generateArchiveDetailsList((Long) null, null, interaction.getMessageHeader().getFromURI());

            // Check if there are comServices...
            if (comServices == null) {
                throw new MALInteractionException(new InvalidException(null));
            }

            // Check if the archive is available...
            if (comServices.getArchiveService() == null) {
                throw new MALInteractionException(new InvalidException(null));
            }

            HeterogeneousList body = new HeterogeneousList();
            body.add(newProviderDetails);

            // Store the Provider COM object in the Archive and get an object instance identifier
            final LongList returnedProvObjIds = comServices.getArchiveService().store(true,
                    DirectoryServiceInfo.PROVIDER_OBJECT_TYPE, ConfigurationProviderSingleton.getDomain(),
                    archDetails, body, null);

            if (returnedProvObjIds.isEmpty()) {
                throw new MALInteractionException(new InvalidException(null));
            }

            Long provObjId = returnedProvObjIds.get(0);
            this.providersAvailable.put(provObjId, newProviderDetails);
            return provObjId;
        }
    }

    @Override
    public void remove(Long providerObjectKey,
            MALInteraction interaction) throws MALInteractionException, MALException {
        synchronized (MUTEX) {
            if (!this.providersAvailable.containsKey(providerObjectKey)) { // The requested provider does not exist
                throw new MALInteractionException(new UnknownException(null));
            }

            ArchiveManager manager = comServices.getArchiveService().getArchiveManager();
            IdentifierList domain = ConfigurationProviderSingleton.getDomain();
            LongList providerIds = new LongList();
            providerIds.add(providerObjectKey);
            manager.removeEntries(DirectoryServiceInfo.PROVIDER_OBJECT_TYPE, domain, providerIds, null);

            this.providersAvailable.remove(providerObjectKey); // Remove the provider...
        }
    }

    public void withdrawAllProviders() throws MALInteractionException, MALException {
        synchronized (MUTEX) {
            for (Long key : providersAvailable.keySet()) {
                remove(key, null);
            }
        }
    }

    public Provider loadURIs(final String providerName) {
        ServicesConnectionDetails primaryConnectionDetails = ConnectionProvider.getGlobalProvidersDetailsPrimary();
        ServicesConnectionDetails secondaryAddresses = ConnectionProvider.getGlobalProvidersDetailsSecondary();

        // Services' connections
        HashMap<String, SingleConnectionDetails> connsMap = primaryConnectionDetails.getServices();
        Object[] serviceNames = connsMap.keySet().toArray();

        final ServiceCapabilityList capabilities = new ServiceCapabilityList();

        // Iterate all the services and make them available...
        for (Object serviceName : serviceNames) {
            SingleConnectionDetails conn = connsMap.get((String) serviceName);
            AddressDetails serviceAddress = DirectoryProviderServiceImpl.getServiceAddressDetails(conn);
            AddressDetailsList serviceAddresses = new AddressDetailsList();
            serviceAddresses.add(serviceAddress);
            ServiceId key = DirectoryProviderServiceImpl.generateServiceKey(conn.getServiceKey());
            ServiceCapability capability = new ServiceCapability(key, new NamedValueList(), serviceAddresses);
            capabilities.add(capability);
        }

        // Second iteration needed here for the secondaryAddresses
        if (secondaryAddresses != null) {
            connsMap = secondaryAddresses.getServices();
            serviceNames = connsMap.keySet().toArray();

            for (Object serviceName : serviceNames) {
                SingleConnectionDetails conn2 = connsMap.get((String) serviceName);
                AddressDetails serviceAddress = DirectoryProviderServiceImpl.getServiceAddressDetails(conn2);
                ServiceId key2 = DirectoryProviderServiceImpl.generateServiceKey(conn2.getServiceKey());
                AddressDetailsList serviceAddresses
                        = DirectoryProviderServiceImpl.findAddressDetailsListOfService(key2, capabilities);
                ServiceCapability capability;

                if (serviceAddresses == null) { // If not found
                    serviceAddresses = new AddressDetailsList();

                    // Then create a new capability object
                    capability = new ServiceCapability(key2, new NamedValueList(), serviceAddresses);
                    capabilities.add(capability);
                }
                serviceAddresses.add(serviceAddress);
            }
        }

        Provider newProviderDetails = new Provider(null,
                new Identifier(providerName),
                ConfigurationProviderSingleton.getDomain(),
                capabilities,
                new AddressDetailsList(),
                null);

        try {
            this.add(newProviderDetails, null);
            return newProviderDetails;
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public Provider loadURIs(final String providerName, final NMFProviderType providerType) {
        Provider provider = loadURIs(providerName);
        if (provider != null) {
            provider.setProviderType(providerType);
        }
        return provider;
    }

    @Override
    public FileList getAreaXML(String filename, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
