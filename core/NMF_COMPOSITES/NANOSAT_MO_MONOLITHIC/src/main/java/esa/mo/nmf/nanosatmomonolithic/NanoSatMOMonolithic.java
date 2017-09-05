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
package esa.mo.nmf.nanosatmomonolithic;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.nmf.NMFProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.reconfigurable.provider.PersistProviderConfiguration;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;

/**
 * The implementation of the NanoSat MO Monolithic that can be extended by
 * particular implementations.
 *
 * @author Cesar Coelho
 */
public abstract class NanoSatMOMonolithic extends NMFProvider {

    private final static String PROVIDER_SUFFIX_NAME = " over NanoSat MO Monolithic";

    /**
     * Initializes the NanoSat MO Monolithic. The MonitorAndControlAdapter
     * adapter class can be extended for remote monitoring and control with the
     * CCSDS Monitor and Control services. One can also extend the
     * SimpleMonitorAndControlAdapter class which contains a simpler interface.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the
     * corresponding methods and variables of a specific entity.
     * @param platformServices Platform Services
     */
    public void init(final MonitorAndControlNMFAdapter mcAdapter,
            final PlatformServicesConsumer platformServices) {
        super.startTime = System.currentTimeMillis();
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.setInputProcessorsProperty();

        // Create provider name to be registerd on the Directory service...
        super.providerName = System.getProperty(HelperMisc.PROP_MO_APP_NAME) + PROVIDER_SUFFIX_NAME;

        super.platformServices = platformServices;

        try {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.FINE,
                    "Initializing services...");

            comServices.init();
            comServices.initArchiveSync();

            heartbeatService.init();
            super.startMCServices(mcAdapter);
            this.initPlatformServices(comServices);
            directoryService.init(comServices);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. Perhaps there's "
                    + "something wrong with the selected Transport Layer.", ex);
            return;
        }

        // Populate the Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO,
                "Populating Directory service...");
        directoryService.loadURIs(this.providerName);

        // Are the dynamic changes enabled?
        if ("true".equals(System.getProperty(DYNAMIC_CHANGES_PROPERTY))) {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO,
                    "Loading previous configurations...");

            // Activate the previous configuration
            final ObjectId confId = new ObjectId(
                    ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE,
                    new ObjectKey(
                            ConfigurationProviderSingleton.getDomain(),
                            DEFAULT_PROVIDER_CONFIGURATION_OBJID
                    )
            );

            super.providerConfiguration = new PersistProviderConfiguration(
                    this,
                    confId,
                    comServices.getArchiveService()
            );

            try {
                super.providerConfiguration.loadPreviousConfigurations();
            } catch (IOException ex) {
                Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (mcAdapter != null) {
            MCRegistration registration = new MCRegistration(comServices, mcServices.getParameterService(),
                    mcServices.getAggregationService(), mcServices.getAlertService(), mcServices.getActionService());
            mcAdapter.initialRegistrations(registration);
        }

        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO,
                "NanoSat MO Monolithic initialized in "
                + (((float) (System.currentTimeMillis() - super.startTime)) / 1000)
                + " seconds!");
        final String uri = directoryService.getConnection().getConnectionDetails().getProviderURI().toString();
        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO,
                "URI: {0}\n", uri);
    }

    /**
     * It closes the App gracefully.
     *
     * @param source The source of the triggering. Can be null
     */
    @Override
    public final void closeGracefully(final ObjectId source) {
        try {
            long time = System.currentTimeMillis();

            // Acknowledge the reception of the request to close (Closing...)
            Long eventId = this.getCOMServices().getEventService().generateAndStoreEvent(
                    AppsLauncherHelper.STOPPING_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    null,
                    null,
                    source,
                    null);

            final URI uri = this.getCOMServices().getEventService().getConnectionProvider().getConnectionDetails().getProviderURI();

            try {
                this.getCOMServices().getEventService().publishEvent(uri, eventId,
                        AppsLauncherHelper.STOPPING_OBJECT_TYPE, null, source, null);
            } catch (IOException ex) {
                Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Close the app...
            // Make a call on the app layer to close nicely...
            if (this.closeAppAdapter != null) {
                Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO,
                        "Triggering the closeAppAdapter of the app business logic...");
                this.closeAppAdapter.onClose(); // Time to sleep, boy!
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
                Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Should close them safely as well...
//        provider.getMCServices().closeServices();
//        provider.getCOMServices().closeServices();
            this.getCOMServices().closeAll();

            // Exit the Java application
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO,
                    "Success! The currently running Java Virtual Machine will now terminate. "
                    + "(App closed in: " + (System.currentTimeMillis() - time) + " ms)\n");
        } catch (NMFException ex) {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.exit(0);
    }

    public abstract void initPlatformServices(COMServicesProvider comServices);

}
