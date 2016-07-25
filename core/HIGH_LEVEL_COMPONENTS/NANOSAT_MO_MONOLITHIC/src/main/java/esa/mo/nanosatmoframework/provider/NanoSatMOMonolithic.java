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
package esa.mo.nanosatmoframework.provider;

import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.nanosatmoframework.adapters.MonitorAndControlAdapter;
import esa.mo.platform.impl.util.PlatformServicesProviderInterface;
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
     * to implement the ActionInvocationListener interface and the
     * ParameterStatusListener interface.
     *
     * @param actionAdapter The adapter to connect the actions to the
     * corresponding method of a specific entity.
     * @param parameterAdapter The adapter to connect the parameters to the
     * corresponding variable of a specific entity.
     * @param platformServices
     */
    public NanoSatMOMonolithic(ActionInvocationListener actionAdapter,
            ParameterStatusListener parameterAdapter,
            PlatformServicesProviderInterface platformServices) {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties

        this.platformServices = platformServices;
        this.providerName = System.getProperty(ConfigurationProvider.MO_APP_NAME) + PROVIDER_SUFFIX_NAME;

        try {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.FINE, "Initializing services...");
            this.servicesInit(actionAdapter, parameterAdapter);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. Perhaps there's something wrong with the selected Transport Layer.", ex);
            return;
        }

        // Populate the Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "Populating Directory service...");
        directoryService.autoLoadURIsFile(this.providerName);

        // Are the dynamic changes enabled?
        if ("true".equals(System.getProperty(DYNAMIC_CHANGES_PROPERTY))) {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "Loading previous configurations...");
            this.loadConfigurations();
        }

        final String uri = directoryService.getConnection().getConnectionDetails().getProviderURI().toString();
        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "NanoSat MO Monolithic initialized! URI: " + uri + "\n");

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
     * @param platformServices Platform Services
     */
    public NanoSatMOMonolithic(MonitorAndControlAdapter mcAdapter,
            PlatformServicesProviderInterface platformServices) {
        this(mcAdapter, mcAdapter, platformServices);
    }

    private void servicesInit(ActionInvocationListener actionAdapter,
            ParameterStatusListener parameterAdapter) throws MALException {
        comServices.init();

        /*
            mcServices.init(
                    comServices,
                    actionAdapter,
                    parameterAdapter,
                    null
            );
         */
        
        if (actionAdapter == null && parameterAdapter == null) {
            parameterManager = new ParameterManager(comServices, parameterAdapter);
            mcServices.getParameterService().init(parameterManager);

            mcServices.getActionService().init(comServices, actionAdapter);
            mcServices.getAlertService().init(comServices);
            mcServices.getAggregationService().init(comServices, parameterManager);

        }

        this.initPlatformServices();
        directoryService.init(comServices);

    }

}
