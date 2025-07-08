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
package esa.mo.common.impl.provider;

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
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.common.directory.DirectoryHelper;
import org.ccsds.moims.mo.common.directory.DirectoryServiceInfo;
import org.ccsds.moims.mo.common.directory.body.PublishProviderResponse;
import org.ccsds.moims.mo.common.directory.provider.DirectoryInheritanceSkeleton;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.AddressDetailsList;
import org.ccsds.moims.mo.common.directory.structures.ProviderDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
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

    public static final String CHAR_S2G = "s2g";
    private static final Logger LOGGER
            = Logger.getLogger(DirectoryProviderServiceImpl.class.getName());

    private MALProvider directoryServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    protected final Map<Long, PublishDetails> providersAvailable
            = new ConcurrentHashMap<>();
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
        if (null != directoryServiceProvider) {
            connection.closeAll();
        }

        directoryServiceProvider = connection.startService(
                DirectoryServiceInfo.DIRECTORY_SERVICE_NAME.toString(),
                DirectoryHelper.DIRECTORY_SERVICE, false, this);

        running = true;
        initialiased = true;
        LOGGER.info("Directory service READY");
    }

    private static AddressDetails getServiceAddressDetails(final SingleConnectionDetails conn) {
        QoSLevelList qos = new QoSLevelList();
        qos.add(QoSLevel.ASSURED);
        NamedValueList qosProperties = new NamedValueList();  // Nothing here for now...

        AddressDetails serviceAddress = new AddressDetails(qos, qosProperties,
                new UInteger(1), conn.getProviderURI(), conn.getBrokerURI());

        return serviceAddress;
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
    public ProviderSummaryList lookupProvider(final ServiceFilter filter,
            final MALInteraction interaction) throws MALInteractionException, MALException {
        if (null == filter) { // Is the input null?
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

        final HashMap<Long, PublishDetails> list;

        synchronized (MUTEX) {
            list = new HashMap<>(providersAvailable);
        }

        LongList keys = new LongList();
        keys.addAll(list.keySet());

        // Initialize the final Provider Summary List
        ProviderSummaryList outputList = new ProviderSummaryList();

        // Filter...
        for (int i = 0; i < keys.size(); i++) { // Filter through all providers
            PublishDetails provider = list.get(keys.get(i));

            //Check service provider name
            if (!filter.getServiceProviderId().toString().equals("*")) { // If not a wildcard...
                if (!provider.getProviderId().toString().equals(filter.getServiceProviderId().toString())) {
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

            // Check session name
            if (!filter.getSessionName().toString().equals("*")) {
                if (!CHAR_S2G.equals(filter.getSessionName().toString())) {
                    if (provider.getSourceSessionName() != null
                            && !provider.getSourceSessionName().toString().equals(
                                    filter.getSessionName().toString())) {
                        continue;
                    }
                }
            }

            // Set the Provider Details structure
            ServiceCapabilityList outCap = new ServiceCapabilityList();
            ProviderDetails pDetails = provider.getProviderDetails();

            // Go through all the services and check each service
            for (ServiceCapability serviceCapability : pDetails.getServiceCapabilities()) {
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

                // Check service capabilities
                if (!filter.getRequiredCapabilitySets().isEmpty()) { // Not empty...
                    boolean capExists = false;

                    for (UShort cap : filter.getRequiredCapabilitySets()) {
                        // cycle all the ones available in the provider
                        for (UShort proCap : filter.getRequiredCapabilitySets()) {
                            if (cap.equals(proCap)) {
                                capExists = true;
                                break;
                            }
                        }
                    }

                    if (!capExists) { // If the capability we want does not exist, then get out...
                        continue;
                    }
                }

                ServiceCapability newServiceCapability = new ServiceCapability(
                        serviceCapability.getServiceId(),
                        serviceCapability.getSupportedCapabilitySets(),
                        serviceCapability.getServiceProperties(),
                        new AddressDetailsList()
                );

                // This is a workaround to save bandwidth on the downlink! It is not part of the standard
                if (CHAR_S2G.equals(filter.getSessionName().toString())) {
                    // We assume that we use malspp on the downlink
                    for (int k = 0; k < serviceCapability.getServiceAddresses().size(); k++) {
                        AddressDetails address = serviceCapability.getServiceAddresses().get(k);

                        if (address.getServiceURI().toString().startsWith("malspp")) {
                            newServiceCapability.getServiceAddresses().add(address);
                        }
                    }
                } else {
                    newServiceCapability.getServiceAddresses().addAll(serviceCapability.getServiceAddresses());
                }

                // Add the service to the list of matching services
                outCap.add(newServiceCapability);
            }

            // It passed all the tests!
            ProviderDetails outProvDetails = new ProviderDetails(outCap, pDetails.getProviderAddresses());
            outputList.add(new ProviderSummary(provider.getDomain(),
                    keys.get(i), provider.getProviderId(), outProvDetails));
        }

        // Errors
        // The operation does not return any errors.
        return outputList;  // requirement: 3.4.9.2.d
    }

    @Override
    public PublishProviderResponse publishProvider(final PublishDetails newProviderDetails,
            final MALInteraction interaction) throws MALInteractionException, MALException {
        Identifier serviceProviderName = newProviderDetails.getProviderId();
        HeterogeneousList objBodies = new HeterogeneousList();
        objBodies.add(serviceProviderName);

        PublishProviderResponse response = new PublishProviderResponse();

        synchronized (MUTEX) {
            final HashMap<Long, PublishDetails> list = new HashMap<>(providersAvailable);

            // Do we already have this provider in the Directory service?
            for (Long key : list.keySet()) {
                PublishDetails provider = this.providersAvailable.get(key);

                if (serviceProviderName.getValue().equals(provider.getProviderId().getValue())) {
                    // It is repeated!!
                    LOGGER.warning("There was already a provider with the same name in the "
                            + "Directory service. Removing the old one and adding the new one...");
                    withdrawProvider(key, null);
                }
            }

            ArchiveDetailsList archDetails = (interaction == null)
                    ? HelperArchive.generateArchiveDetailsList(null, null,
                            connection.getPrimaryConnectionDetails().getProviderURI())
                    : HelperArchive.generateArchiveDetailsList((Long) null, null, interaction);

            // Check if there are comServices...
            if (comServices == null) {
                throw new MALInteractionException(new InvalidException(null));
            }

            // Check if the archive is available...
            if (comServices.getArchiveService() == null) {
                throw new MALInteractionException(new InvalidException(null));
            }

            // Store in the Archive the ServiceProvider COM object and get an object instance identifier
            final LongList returnedServProvObjIds = comServices.getArchiveService().store(true,
                    DirectoryServiceInfo.SERVICEPROVIDER_OBJECT_TYPE, ConfigurationProviderSingleton.getDomain(), archDetails,
                    objBodies, null);

            Long servProvObjId;

            if (!returnedServProvObjIds.isEmpty()) {
                servProvObjId = returnedServProvObjIds.get(0);
            } else {  // Nothing was returned...
                throw new MALInteractionException(new InvalidException(null));
            }

            // related contains the objId of the ServiceProvider object
            final ArchiveDetailsList archDetails1 = (interaction == null)
                    ? HelperArchive.generateArchiveDetailsList(servProvObjId, null,
                            connection.getPrimaryConnectionDetails().getProviderURI())
                    : HelperArchive.generateArchiveDetailsList(servProvObjId, null, interaction);

            HeterogeneousList capabilities = new HeterogeneousList();
            capabilities.add(newProviderDetails.getProviderDetails());

            // Store in the Archive the ProviderCapabilities COM object
            comServices.getArchiveService().store(false, DirectoryServiceInfo.PROVIDERCAPABILITIES_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(), archDetails1, capabilities, null);

            this.providersAvailable.put(servProvObjId, newProviderDetails);
            response = new PublishProviderResponse(servProvObjId, null);
        }

        return response;
    }

    @Override
    public void withdrawProvider(Long providerObjectKey,
            MALInteraction interaction) throws MALInteractionException, MALException {
        synchronized (MUTEX) {
            if (!this.providersAvailable.containsKey(providerObjectKey)) { // The requested provider does not exist
                throw new MALInteractionException(new UnknownException(null));
            }

            ArchiveManager manager = comServices.getArchiveService().getArchiveManager();
            IdentifierList domain = ConfigurationProviderSingleton.getDomain();
            ArchiveQuery query = new ArchiveQuery(domain, null, null, providerObjectKey, null, null, null, null, null);
            List<ArchivePersistenceObject> result = manager.query(DirectoryServiceInfo.PROVIDERCAPABILITIES_OBJECT_TYPE,
                    query, null);
            Long capabilityId = result.get(0).getArchiveDetails().getInstId(); // there should be only one object in the query result
            LongList providerIds = new LongList();
            providerIds.add(providerObjectKey);
            manager.removeEntries(DirectoryServiceInfo.SERVICEPROVIDER_OBJECT_TYPE, domain, providerIds, null);
            LongList capabilityIds = new LongList();
            capabilityIds.add(capabilityId);
            manager.removeEntries(DirectoryServiceInfo.PROVIDERCAPABILITIES_OBJECT_TYPE, domain, capabilityIds, null);

            this.providersAvailable.remove(providerObjectKey); // Remove the provider...
        }
    }

    public void withdrawAllProviders() throws MALInteractionException, MALException {
        synchronized (MUTEX) {
            for (Long key : providersAvailable.keySet()) {
                withdrawProvider(key, null);
            }
        }
    }

    public PublishDetails loadURIs(final String providerName) {
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
            // "If NULL then all capabilities supported."
            ServiceCapability capability = new ServiceCapability(key, null, new NamedValueList(), serviceAddresses);
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
                    // "If NULL then all capabilities supported."
                    capability = new ServiceCapability(key2, null, new NamedValueList(), serviceAddresses);
                    capabilities.add(capability);
                }
                serviceAddresses.add(serviceAddress);
            }
        }

        ProviderDetails serviceDetails = new ProviderDetails(capabilities, new AddressDetailsList());

        PublishDetails newProviderDetails = new PublishDetails(new Identifier(providerName),
                ConfigurationProviderSingleton.getDomain(), null,
                ConfigurationProviderSingleton.getNetwork(), serviceDetails);

        try {
            this.publishProvider(newProviderDetails, null);
            return newProviderDetails;
        } catch (MALInteractionException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public FileList getAreaXML(String filename, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
