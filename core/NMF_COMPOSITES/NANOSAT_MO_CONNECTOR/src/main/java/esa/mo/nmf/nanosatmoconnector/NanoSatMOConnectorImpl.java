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
package esa.mo.nmf.nanosatmoconnector;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.common.impl.util.HelperCommon;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFProvider;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.reconfigurable.provider.PersistProviderConfiguration;
import esa.mo.sm.impl.provider.AppsLauncherManager;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.directory.body.PublishProviderResponse;
import org.ccsds.moims.mo.common.directory.structures.AddressDetailsList;
import org.ccsds.moims.mo.common.directory.structures.ProviderDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;

/**
 * The implementation of the NanoSat MO Connector.
 *
 * @author Cesar Coelho
 */
public class NanoSatMOConnectorImpl extends NMFProvider {

    private Long appDirectoryServiceId;
    private EventConsumerServiceImpl serviceCOMEvent;
    private Subscription subscription;

    /**
     * Initializes the NanoSat MO Connector. The MonitorAndControlAdapter
     * adapter class can be extended for remote monitoring and control with the
     * CCSDS Monitor and Control services. One can also extend the
     * SimpleMonitorAndControlAdapter class which contains a simpler interface.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the
     * corresponding methods and variables of a specific entity.
     */
    @Override
    public void init(final MonitorAndControlNMFAdapter mcAdapter) {
        super.startTime = System.currentTimeMillis();
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.setInputProcessorsProperty();

        // Create provider name to be registerd on the Directory service...
        String appName = "Unknown";
        try { // Use the folder name
            appName = (new File((new File("")).getCanonicalPath())).getName();
            System.setProperty(HelperMisc.MO_APP_NAME, appName);
        } catch (IOException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                    "The NMF App name could not be established.");
        }

        this.providerName = AppsLauncherProviderServiceImpl.PROVIDER_PREFIX_NAME + appName;

        try {
            comServices.init();
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. "
                    + "Perhaps there's something wrong with the Transport Layer.", ex);
            return;
        }

        URI centralDirectoryURI = this.readCentralDirectoryServiceURI();

        if (centralDirectoryURI != null && centralDirectoryURI.getValue().startsWith("malspp")) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                    "The Central Directory service URI read is selecting 'malspp' as transport. The URI will be discarded."
                    + " To enable a better IPC communication, please enable the secondary transport protocol flag: "
                    + HelperMisc.SECONDARY_PROTOCOL
            );

            centralDirectoryURI = null;
        }

        DirectoryConsumerServiceImpl centralDirectory = null;

        // Connect to the Central Directory service
        if (centralDirectoryURI != null) {
            try {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                        "Attempting to connect to Central Directory service at: {0}",
                        centralDirectoryURI.toString());

                // Connect to the Central Directory service...
                centralDirectory = new DirectoryConsumerServiceImpl(centralDirectoryURI);

                IdentifierList domain = new IdentifierList();
                domain.add(new Identifier("*"));
                COMService eventCOM = EventHelper.EVENT_SERVICE; // Filter for the Event service of the Supervisor
                final ServiceKey serviceKey = new ServiceKey(eventCOM.getArea().getNumber(),
                        eventCOM.getNumber(), eventCOM.getArea().getVersion());
                final ServiceFilter sf = new ServiceFilter(
                        new Identifier(NMFProvider.NANOSAT_MO_SUPERVISOR_NAME),
                        domain, new Identifier("*"), null, new Identifier("*"),
                        serviceKey, new UIntegerList());
                final ProviderSummaryList supervisorEventServiceConnectionDetails = centralDirectory.getDirectoryStub().lookupProvider(sf);

                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                        "The Central Directory service is operational!");

                // Register for CloseApp Events...
                try {
                    // Convert provider to connectionDetails...
                    final SingleConnectionDetails connectionDetails = AppsLauncherManager.getSingleConnectionDetailsFromProviderSummaryList(supervisorEventServiceConnectionDetails);
                    serviceCOMEvent = new EventConsumerServiceImpl(connectionDetails);
                } catch (IOException ex) {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                            "Something went wrong...");
                }

                // Subscribe to all Events
                // Select all object numbers from the Apps Launcher service Events
                final Long secondEntityKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(AppsLauncherHelper.APP_OBJECT_TYPE);
                final Random random = new Random();
                subscription = ConnectionConsumer.subscriptionKeys(
                        new Identifier("CloseAppEventListener" + random.nextInt()),
                        new Identifier("*"), secondEntityKey, new Long(0), new Long(0));

                // Register with the subscription key provided
                serviceCOMEvent.addEventReceivedListener(subscription, new CloseAppEventListener(this));

                // Lookup for the Platform services on the NanoSat MO Supervisor
                final ServiceKey sk = new ServiceKey(PlatformHelper.PLATFORM_AREA_NUMBER,
                        new UShort(0), new UOctet((short) 0));
                final ServiceFilter sf2 = new ServiceFilter(new Identifier(NMFProvider.NANOSAT_MO_SUPERVISOR_NAME),
                        domain, new Identifier("*"), null, new Identifier("*"), sk, new UIntegerList());
                final ProviderSummaryList supervisorConnections = centralDirectory.getDirectoryStub().lookupProvider(sf2);

                if (supervisorConnections.size() == 1) { // Platform services found!
                    // Load all the Platform services' APIs
                    if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME, PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                        PlatformHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
                    }

                    // Select the best transport for IPC and convert to a ConnectionConsumer object
                    final ProviderSummary filteredConnections = NanoSatMOConnectorImpl.selectBestIPCTransport(supervisorConnections.get(0));
                    final ConnectionConsumer supervisorCCPlat = HelperCommon.providerSummaryToConnectionConsumer(filteredConnections);

                    // Connect to them...
                    platformServices = new PlatformServicesConsumer();
                    COMServicesConsumer comServicesConsumer = new COMServicesConsumer();
                    comServicesConsumer.init(supervisorCCPlat);
                    platformServices.init(supervisorCCPlat, comServicesConsumer);
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                            "Successfully connected to Platform services on: {0}",
                            supervisorConnections.get(0).getProviderName());
                } else {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                            "The NanoSat MO Connector was expecting a single NanoSat MO Supervisor provider!"
                            + " Instead it found {0}.", supervisorConnections.size());
                }
            } catch (MALException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                        "Could not connect to the Central Directory service! Maybe it is down...");
            }
        }

        // Initialize the rest of the services
        try {
            this.startMCServices(mcAdapter);
            directoryService.init(comServices);
            heartbeatService.init();
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. "
                    + "Perhaps there's something wrong with the Transport Layer.", ex);
            return;
        }

        this.initAdditionalServices();

        // Populate the local Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                "Populating local Directory service...");
        PublishDetails publishDetails = directoryService.loadURIs(this.providerName);

        // Populate the provider list of services in the Central Directory service
        if (centralDirectoryURI != null) {
            try {
                if (centralDirectory != null) {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                            "Populating Central Directory service on URI: {0}", centralDirectoryURI.getValue());

                    final PublishProviderResponse response = centralDirectory.getDirectoryStub().publishProvider(publishDetails);
                    this.appDirectoryServiceId = response.getBodyElement0();
                    centralDirectory.closeConnection(); // Close the connection to the Directory service
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                            "Populated! And the connection to the Directory service has been successfully closed!");
                }
            } catch (MALException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                        "Could not connect to the Central Directory service! Maybe it is down...");
            }
        }

        // Are the dynamic changes enabled?
        if ("true".equals(System.getProperty(DYNAMIC_CHANGES_PROPERTY))) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                    "Loading previous configurations...");

            // Activate the previous configuration
            final ObjectId confId = new ObjectId(ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE,
                    new ObjectKey(ConfigurationProviderSingleton.getDomain(), DEFAULT_PROVIDER_CONFIGURATION_OBJID));

            super.providerConfiguration = new PersistProviderConfiguration(this, confId, comServices.getArchiveService());

            try {
                super.providerConfiguration.loadPreviousConfigurations();
            } catch (IOException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (mcAdapter != null) {
            MCRegistration registration = new MCRegistration(comServices, mcServices.getParameterService(),
                    mcServices.getAggregationService(), mcServices.getAlertService(), mcServices.getActionService());
            mcAdapter.initialRegistrations(registration);
        }

        Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                "NanoSat MO Connector initialized in "
                + (((float) (System.currentTimeMillis() - super.startTime)) / 1000)
                + " seconds!");
        
        final String uri = directoryService.getConnection().getPrimaryConnectionDetails().getProviderURI().toString();
        Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                "URI: {0}\n", uri);

        // We just loaded everything, it is a good time to 
        // hint the garbage collector and clean up some memory
        // NMFProvider.hintGC();
    }

    /**
     * Retrieves the object instance identifier of the application that uniquely
     * identifies the App in the Central Directory service.
     *
     * @return The object Instance Identifier of the App in the Central
     * Directory service.
     */
    public final Long getAppDirectoryId() {
        return this.appDirectoryServiceId;
    }

    private static ProviderSummary selectBestIPCTransport(final ProviderSummary provider) {
        final ProviderSummary newSummary = new ProviderSummary();
        newSummary.setProviderKey(provider.getProviderKey());
        newSummary.setProviderName(provider.getProviderName());

        final ProviderDetails details = new ProviderDetails();
        newSummary.setProviderDetails(details);
        details.setProviderAddresses(provider.getProviderDetails().getProviderAddresses());

        final ServiceCapabilityList oldCapabilities = provider.getProviderDetails().getServiceCapabilities();
        final ServiceCapabilityList newCapabilities = new ServiceCapabilityList();

        for (int i = 0; i < oldCapabilities.size(); i++) {
            AddressDetailsList addresses = oldCapabilities.get(i).getServiceAddresses();
            ServiceCapability cap = new ServiceCapability();
            cap.setServiceKey(oldCapabilities.get(i).getServiceKey());
            cap.setServiceProperties(oldCapabilities.get(i).getServiceProperties());
            cap.setSupportedCapabilities(oldCapabilities.get(i).getSupportedCapabilities());

            try {
                final int bestIndex = AppsLauncherManager.getBestIPCServiceAddressIndex(addresses);

                // Select only the best address for IPC
                AddressDetailsList newAddresses = new AddressDetailsList();
                newAddresses.add(addresses.get(bestIndex));
                cap.setServiceAddresses(newAddresses);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.SEVERE,
                        "The best IPC service address index could not be determined!", ex);
            }

            newCapabilities.add(cap);
        }

        details.setServiceCapabilities(newCapabilities);

        return newSummary;
    }

    /**
     * It closes the application gracefully.
     *
     * @param source The source of the triggering. Can be null.
     */
    @Override
    public final void closeGracefully(final ObjectId source) {
        try {
            long time = System.currentTimeMillis();

            // We can close the connection to the Supervisor
            this.serviceCOMEvent.closeConnection();

            // Acknowledge the reception of the request to close (Closing...)
            Long eventId = this.getCOMServices().getEventService().generateAndStoreEvent(
                    AppsLauncherHelper.STOPPING_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    null,
                    null,
                    source,
                    null);

            final URI uri = this.getCOMServices().getEventService().getConnectionProvider().getIPCConnectionDetails().getProviderURI();

            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                    "Publishing event to uri: {0}", uri);

            try {
                this.getCOMServices().getEventService().publishEvent(uri, eventId,
                        AppsLauncherHelper.STOPPING_OBJECT_TYPE, null, source, null);
            } catch (IOException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Close the app...
            // Make a call on the app layer to close nicely...
            if (this.closeAppAdapter != null) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                        "Triggering the closeAppAdapter of the app business logic...");
                this.closeAppAdapter.onClose(); // Time to sleep, boy!
            }

            // Unregister the provider from the Central Directory service...
            final URI centralDirectoryURI = this.readCentralDirectoryServiceURI();

            if (centralDirectoryURI != null) {
                try {
                    DirectoryConsumerServiceImpl directoryServiceConsumer = new DirectoryConsumerServiceImpl(centralDirectoryURI);
                    directoryServiceConsumer.getDirectoryStub().withdrawProvider(this.getAppDirectoryId());
                    directoryServiceConsumer.closeConnection();
                } catch (MALException ex) {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MalformedURLException ex) {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MALInteractionException ex) {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                            "There was a problem while connecting to the Central Directory service on URI: {0}"
                            + "\nException: {1}",
                            new Object[]{centralDirectoryURI.getValue(), ex});
                }
            }

            Long eventId2 = this.getCOMServices().getEventService().generateAndStoreEvent(
                    AppsLauncherHelper.STOPPED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    null,
                    null,
                    source,
                    null);

            try {
                this.getCOMServices().getEventService().publishEvent(uri, eventId2,
                        AppsLauncherHelper.STOPPED_OBJECT_TYPE, null, source, null);
            } catch (IOException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Should close them safely as well...
//        provider.getMCServices().closeServices();
//        provider.getCOMServices().closeServices();
            this.getCOMServices().closeAll();

            // Exit the Java application
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO,
                    "Success! The currently running Java Virtual Machine will now terminate. "
                    + "(App closed in: {0} ms)\n", System.currentTimeMillis() - time);
        } catch (NMFException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.exit(0);
    }

    /**
     * This class can be overridden in order to initialize additional services
     * by the NanoSat MO Connector.
     */
    public void initAdditionalServices() {
        // To be overridden
    }

}
