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
package esa.mo.nanosatmoframework.connector;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.mc.impl.util.MCServicesProvider;
import esa.mo.nanosatmoframework.adapters.MonitorAndControlAdapter;
import esa.mo.nanosatmoframework.provider.NanoSatMOFrameworkProvider;
import esa.mo.nanosatmoframework.provider.NanoSatMOMonolithic;
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
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.directory.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;
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

//    private Long appObjInstId = new Long(0);
    private Long appDirectoryServiceId;
    private EventConsumerServiceImpl serviceCOMEvent;
    private Subscription subscription;

    /**
     * To initialize the NanoSat MO Framework with this method, it is necessary
     * to implement the ActionInvocationListener interface and the
     * ParameterStatusListener interface.
     *
     * @param actionAdapter The adapter to connect the actions to the
     * corresponding method of a specific entity.
     * @param parameterAdapter The adapter to connect the parameters to the
     * corresponding variable of a specific entity.
     */
    public NanoSatMOConnectorImpl(ActionInvocationListener actionAdapter,
            ParameterStatusListener parameterAdapter) {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
        HelperMisc.setInputProcessorsProperty();

        // Create provider name to be registerd on the Directory service...
        this.providerName = AppsLauncherProviderServiceImpl.PROVIDER_PREFIX_NAME + System.getProperty(ConfigurationProvider.MO_APP_NAME);
        
        URI centralDirectoryURI;
        try {
            centralDirectoryURI = this.readCentralDirectoryServiceURI();
        } catch (FileNotFoundException ex) {
            centralDirectoryURI = null;
        }
        
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
                ProviderSummaryList supervisorConnectionDetails = directoryServiceConsumer.getDirectoryStub().lookupProvider(sf);

                // Register for CloseApp Events...
                try {
                    // To do, convert provider to connectionDetails...
                    SingleConnectionDetails connectionDetails = AppsLauncherManager.getSingleConnectionDetailsFromProviderSummaryList(supervisorConnectionDetails);
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

                // Lookup for the Platform services
                // Connect to them...
                platformServices = null;

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
            this.startMCServices(actionAdapter, parameterAdapter);
            directoryService.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. Perhaps there's something wrong with the Transport Layer.", ex);
            return;
        }

        // Populate the Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Populating Local Directory service...");
        PublishDetails publishDetails = directoryService.autoLoadURIsFile(this.providerName);

        // Are the dynamic changes enabled?
        if ("true".equals(System.getProperty(DYNAMIC_CHANGES_PROPERTY))) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Loading previous configurations...");
            if (actionAdapter != null || parameterAdapter != null) {
                this.loadConfigurations();
            }
        }

        if (centralDirectoryURI != null) {
            try {
                // Register the services in the Central Directory service...
                Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Populating Central Directory service on URI: " + centralDirectoryURI.getValue());

                if (directoryServiceConsumer != null) {
                    this.appDirectoryServiceId = directoryServiceConsumer.getDirectoryStub().publishProvider(publishDetails);
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
    public NanoSatMOConnectorImpl(MonitorAndControlAdapter mcAdapter) {
        this(mcAdapter, mcAdapter);
    }

    @Override
    public void initPlatformServices() {
        // Connect to the Platform services...

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    public final URI readCentralDirectoryServiceURI() throws FileNotFoundException {
        String path = ".." + File.separator
                + NanoSatMOFrameworkProvider.NANOSAT_MO_SUPERVISOR_NAME + File.separator
                + FILENAME_CENTRAL_DIRECTORY_SERVICE;

        File file = new File(path); // Select the file that we want to read from

        String line;
        // Get the text out of that file...
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
        BufferedReader br = new BufferedReader(isr);

        try {
            while ((line = br.readLine()) != null) {
                return new URI(line);
            }
        } catch (IOException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public final Long getAppDirectoryId() {
        return this.appDirectoryServiceId;
    }

}
