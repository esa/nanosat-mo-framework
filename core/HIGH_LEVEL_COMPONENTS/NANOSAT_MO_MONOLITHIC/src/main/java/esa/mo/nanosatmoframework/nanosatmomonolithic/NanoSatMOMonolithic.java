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
package esa.mo.nanosatmoframework.nanosatmomonolithic;

import esa.mo.nanosatmoframework.NanoSatMOFrameworkProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nanosatmoframework.MCRegistration;
import esa.mo.nanosatmoframework.MonitorAndControlNMFAdapter;
import esa.mo.nanosatmoframework.NMFException;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;

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
public abstract class NanoSatMOMonolithic extends NanoSatMOFrameworkProvider {

    private final static String PROVIDER_SUFFIX_NAME = " over NanoSat MO Monolithic";

    /**
     * To initialize the NanoSat MO Framework with this method, it is necessary
     * to extend the MonitorAndControlAdapter adapter class. The
     * SimpleMonitorAndControlAdapter class contains a simpler interface which
     * allows sending directly parameters of the most common java types and it
     * also allows the possibility to send serializable objects.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the
     * corresponding methods and variables of a specific entity.
     * @param platformServices Platform Services
     */
    public NanoSatMOMonolithic(MonitorAndControlNMFAdapter mcAdapter,
            PlatformServicesConsumer platformServices) {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
        HelperMisc.setInputProcessorsProperty();

        // Create provider name to be registerd on the Directory service...
        this.providerName = System.getProperty(HelperMisc.MO_APP_NAME) + PROVIDER_SUFFIX_NAME;

        this.platformServices = platformServices;

        try {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.FINE, "Initializing services...");

            comServices.init();
            heartbeatService.init();
            this.startMCServices(mcAdapter);
            this.initPlatformServices(comServices);
            directoryService.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. Perhaps there's something wrong with the selected Transport Layer.", ex);
            return;
        }

        // Populate the Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "Populating Directory service...");
        directoryService.autoLoadURIsFile(this.providerName);

        if (mcAdapter != null) {
            // Are the dynamic changes enabled?
            if ("true".equals(System.getProperty(DYNAMIC_CHANGES_PROPERTY))) {
                Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "Loading previous configurations...");
                try {
                    this.loadConfigurations();
                } catch (NMFException ex) {
                    Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            MCRegistration registration = new MCRegistration(comServices, mcServices.getParameterService(), 
                    mcServices.getAggregationService(), mcServices.getAlertService(), mcServices.getActionService());
            mcAdapter.initialRegistrations(registration);
        }
        
        final String uri = directoryService.getConnection().getConnectionDetails().getProviderURI().toString();
        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "NanoSat MO Monolithic initialized! URI: " + uri + "\n");
    }

}
