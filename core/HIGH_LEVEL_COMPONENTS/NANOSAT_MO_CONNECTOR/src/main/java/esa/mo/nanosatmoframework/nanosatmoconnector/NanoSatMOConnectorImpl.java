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
package esa.mo.nanosatmoframework.nanosatmoconnector;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.common.impl.util.HelperCommon;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nanosatmoframework.MCRegistration;
import esa.mo.nanosatmoframework.MonitorAndControlNMFAdapter;
import esa.mo.nanosatmoframework.NanoSatMOFrameworkProvider;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.sm.impl.provider.AppsLauncherManager;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.common.directory.body.PublishProviderResponse;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
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
 * A Provider of MO services composed by COM, M&C and Platform services. Selects
 * the transport layer based on the selected values of the properties file and
 * initializes all services automatically. Provides configuration persistence,
 * therefore the last state of the configuration of the MO services will be kept
 * upon restart. Additionally, the NanoSat MO Framework implements an
 * abstraction layer over the Back-End of some MO services to facilitate the
 * monitoring of the business logic of the app using the NanoSat MO Framework.
 *
 * @author Cesar Coelho
 */
public final class NanoSatMOConnectorImpl extends NanoSatMOFrameworkProvider {

    private Long appDirectoryServiceId;
    private EventConsumerServiceImpl serviceCOMEvent;
    private Subscription subscription;

    /**
     * To initialize the NanoSat MO Framework with this method, it is necessary
     * to extend the MonitorAndControlAdapter adapter class. The
     * SimpleMonitorAndControlAdapter class contains a simpler interface which
     * allows sending directly parameters of the most common java types and it
     * also allows the possibility to send serializable objects.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the
     * corresponding methods and variables of a specific entity.
     */
    public NanoSatMOConnectorImpl(MonitorAndControlNMFAdapter mcAdapter) {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
        HelperMisc.setInputProcessorsProperty();

        // Create provider name to be registerd on the Directory service...
        this.providerName = AppsLauncherProviderServiceImpl.PROVIDER_PREFIX_NAME + System.getProperty(HelperMisc.MO_APP_NAME);

        URI centralDirectoryURI = this.readCentralDirectoryServiceURI();
        DirectoryConsumerServiceImpl directoryServiceConsumer = null;

        if (centralDirectoryURI != null) {
            try {
                // Connect to the Central Directory service...
                directoryServiceConsumer = new DirectoryConsumerServiceImpl(centralDirectoryURI);

                IdentifierList domain = new IdentifierList();
                domain.add(new Identifier("*"));
                COMService eventCOM = EventHelper.EVENT_SERVICE; // Filter for the Event service of the Supervisor
                ServiceKey serviceKey = new ServiceKey(eventCOM.getArea().getNumber(), eventCOM.getNumber(), eventCOM.getArea().getVersion());
                ServiceFilter sf = new ServiceFilter(new Identifier(NanoSatMOFrameworkProvider.NANOSAT_MO_SUPERVISOR_NAME), domain, new Identifier("*"), null, new Identifier("*"), serviceKey, new UIntegerList());
                ProviderSummaryList supervisorEventServiceConnectionDetails = directoryServiceConsumer.getDirectoryStub().lookupProvider(sf);

                // Register for CloseApp Events...
                try {
                    // To do, convert provider to connectionDetails...
                    SingleConnectionDetails connectionDetails = AppsLauncherManager.getSingleConnectionDetailsFromProviderSummaryList(supervisorEventServiceConnectionDetails);
                    serviceCOMEvent = new EventConsumerServiceImpl(connectionDetails);
                } catch (IOException ex) {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, "Something went wrong...");
                }

                // Subscribe to all Events
                // Select all object numbers from the Apps Launcher service Events
                final Long secondEntityKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(AppsLauncherHelper.APP_OBJECT_TYPE);
                final Random random = new Random();
                subscription = ConnectionConsumer.subscriptionKeys(new Identifier("CloseAppEventListener" + random.nextInt()), new Identifier("*"), secondEntityKey, new Long(0), new Long(0));

                // Register with the subscription key provided
                serviceCOMEvent.addEventReceivedListener(subscription, new CloseAppEventListener(this));

                // Lookup for the Platform services on the NanoSat MO Supervisor
                ServiceKey sk = new ServiceKey(PlatformHelper.PLATFORM_AREA_NUMBER, new UShort(0), new UOctet((short) 0));
                ServiceFilter sf2 = new ServiceFilter(new Identifier(NanoSatMOFrameworkProvider.NANOSAT_MO_SUPERVISOR_NAME), domain, new Identifier("*"), null, new Identifier("*"), sk, new UIntegerList());
                ProviderSummaryList supervisorConnections = directoryServiceConsumer.getDirectoryStub().lookupProvider(sf2);

                if (supervisorConnections.size() == 1) { // Platform services found!
                    // Load the all the Platform services' APIs
                    if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME, PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                        PlatformHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
                    }

                    ConnectionConsumer supervisorCD = HelperCommon.providerSummaryToConnectionConsumer(supervisorConnections.get(0));

                    // Connect to them...
                    platformServices = new PlatformServicesConsumer();
                    COMServicesConsumer comServicesConsumer = new COMServicesConsumer();
                    comServicesConsumer.init(supervisorCD);
                    platformServices.init(supervisorCD, comServicesConsumer);
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Successfully connected to: " + supervisorCD.getConsumerURI());
                } else {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, "The Connector was expecting a single NanoSat MO Supervisor provider! Instead it found " + supervisorConnections.size() + ".");
                }
            } catch (MALException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MalformedURLException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, "Could not connect to the Central Directory service! Maybe it is down...");
            }
        }

        try {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.FINE, "Initializing services...");

            comServices.init();
            heartbeatService.init();
            this.startMCServices(mcAdapter);
            directoryService.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. Perhaps there's something wrong with the Transport Layer.", ex);
            return;
        }
        
        // Populate the Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Populating Local Directory service...");
        PublishDetails publishDetails = directoryService.autoLoadURIsFile(this.providerName);

        if (mcAdapter != null) {
            // Are the dynamic changes enabled?
            if ("true".equals(System.getProperty(DYNAMIC_CHANGES_PROPERTY))) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Loading previous configurations...");
                try {
                    this.loadConfigurations();
                } catch (IOException ex) {
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            MCRegistration registration = new MCRegistration(comServices, mcServices.getParameterService(), 
                    mcServices.getAggregationService(), mcServices.getAlertService(), mcServices.getActionService());
            mcAdapter.initialRegistrations(registration);
        }
        
        if (centralDirectoryURI != null) {
            try {
                // Register the services in the Central Directory service...
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Populating Central Directory service on URI: " + centralDirectoryURI.getValue());

                if (directoryServiceConsumer != null) {
                    PublishProviderResponse response = directoryServiceConsumer.getDirectoryStub().publishProvider(publishDetails);
                    this.appDirectoryServiceId = response.getBodyElement0();
                    directoryServiceConsumer.closeConnection(); // Close the connection to the Directory service
                    Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Populated! And the connection to the Directory service has been successfully closed!");
                }
            } catch (MALException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, "Could not connect to the Central Directory service! Maybe it is down...");
            }
        }

        final String uri = directoryService.getConnection().getConnectionDetails().getProviderURI().toString();
        Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "NanoSat MO Connector initialized! URI: " + uri + "\n");
    }

    @Override
    public void initPlatformServices(COMServicesProvider comServices) {
        // It is supposed to be empty!
    }

    public final URI readCentralDirectoryServiceURI() {
        String path = ".." 
                + File.separator
                + NanoSatMOFrameworkProvider.NANOSAT_MO_SUPERVISOR_NAME 
                + File.separator
                + FILENAME_CENTRAL_DIRECTORY_SERVICE;

        File file = new File(path); // Select the file that we want to read from

        try {
            // Get the text out of that file...
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);

            try {
                String line;
                while ((line = br.readLine()) != null) {
                    return new URI(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.WARNING, "The File " + file.getPath() + " could not be found!");
            return null;
        }

        return null;
    }

    public final Long getAppDirectoryId() {
        return this.appDirectoryServiceId;
    }

}
