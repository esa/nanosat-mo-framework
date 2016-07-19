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

import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.nanosatmoframework.adapters.MonitorAndControlAdapter;
import esa.mo.nanosatmoframework.provider.NanoSatMOFrameworkProvider;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

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

    private final static String PROVIDER_PREFIX_NAME = "App: ";
    public final static String NANOSAT_MO_SUPERVISOR_FOLDER_NAME = "NanoSat_MO_Supervisor";

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

        this.providerName = PROVIDER_PREFIX_NAME + System.getProperty(ConfigurationProvider.MO_APP_NAME);

        // Connect to the Central Directory service
        // Lookup for the Platform services
        // Connect to them...
        platformServices = null;

        try {
            comServices.init();

            mcServices.init(
                    comServices,
                    actionAdapter,
                    parameterAdapter,
                    null
            );

            directoryService.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. Perhaps there's something wrong with the Transport Layer.", ex);
            return;
        }

        // Populate the Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Populating local Directory service...");
        PublishDetails publishDetails = directoryService.autoLoadURIsFile(this.providerName);

        // Are the dynamic changes enabled?
        if ("true".equals(System.getProperty(DYNAMIC_CHANGES_PROPERTY))) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Loading previous configurations...");
            this.loadConfigurations();
        }

        try {
            // Connect to the Central Directory service...
            URI centralDirectoryURI = this.readCentralDirectoryServiceURI();
            DirectoryConsumerServiceImpl directoryServiceConsumer = new DirectoryConsumerServiceImpl(centralDirectoryURI);

            // Register the services in the Central Directory service...
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.INFO, "Populating Central Directory service...");
            directoryServiceConsumer.getDirectoryStub().publishProvider(publishDetails);

        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
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

    public final URI readCentralDirectoryServiceURI() {
        // Read from the file
        String path = ".." + File.separator + NANOSAT_MO_SUPERVISOR_FOLDER_NAME + File.separator + FILENAME_CENTRAL_DIRECTORY_SERVICE;
        File file = new File(path);

        // Get the text out of it...
        String line;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8"));
            BufferedReader br = new BufferedReader(isr);

            while ((line = br.readLine()) != null) {
                return new URI(line);
            }

        } catch (IOException ex) {
            Logger.getLogger(NanoSatMOConnectorImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
