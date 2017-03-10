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
package esa.mo.nmf.nanosatmosupervisor;

import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.nmf.nanosatmomonolithic.NanoSatMOMonolithic;
import esa.mo.nmf.NanoSatMOFrameworkProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.misc.HelperNMF;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.CloseAppListener;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFException;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import esa.mo.sm.impl.util.PackageManagementBackendInterface;
import esa.mo.sm.impl.provider.PackageManagementProviderServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
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
public abstract class NanoSatMOSupervisor extends NanoSatMOFrameworkProvider {

    private final PackageManagementProviderServiceImpl packageManagementService = new PackageManagementProviderServiceImpl();
    private final AppsLauncherProviderServiceImpl applicationsManagerService = new AppsLauncherProviderServiceImpl();

    /**
     * To initialize the NanoSat MO Framework with this method, it is necessary
     * to extend the MonitorAndControlAdapter adapter class. The
     * SimpleMonitorAndControlAdapter class contains a simpler interface which
     * allows sending directly parameters of the most common java types and it
     * also allows the possibility to send serializable objects.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the
     * corresponding methods and variables of a specific entity.
     * @param platformServices The Platform services consumer stubs
     * @param packageManagementBackend
     */
    public NanoSatMOSupervisor(MonitorAndControlNMFAdapter mcAdapter,
            PlatformServicesConsumer platformServices,
            PackageManagementBackendInterface packageManagementBackend) {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
        HelperMisc.setInputProcessorsProperty();

        // Enforce the App Name property to be HelperMisc.NMF_NMS_NAME
        System.setProperty(HelperMisc.MO_APP_NAME, HelperNMF.NMF_NMS_NAME);

        // Create provider name to be registerd on the Directory service...
        this.providerName = System.getProperty(HelperMisc.MO_APP_NAME);

        this.platformServices = platformServices;

        try {
            this.comServices.init();
            this.heartbeatService.init();

            // Change transport to start on both RMI and SPP
            this.directoryService.init(comServices);
            this.applicationsManagerService.init(comServices, directoryService);
            this.packageManagementService.init(comServices, packageManagementBackend);
            this.startMCServices(mcAdapter);
            this.initPlatformServices(comServices);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. Perhaps there's something wrong with the Transport Layer.", ex);
            return;
        }

        // Populate the Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO, "Populating Directory service...");
        this.directoryService.loadURIs(NanoSatMOFrameworkProvider.NANOSAT_MO_SUPERVISOR_NAME);

        if (mcAdapter != null) {
            // Are the dynamic changes enabled?
            if ("true".equals(System.getProperty(NanoSatMOFrameworkProvider.DYNAMIC_CHANGES_PROPERTY))) {
                Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO,
                        "Loading previous configurations...");

                try {
                    this.loadConfigurations();
                } catch (NMFException ex) {
                    Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.SEVERE,
                            "The configurations could not be loaded!", ex);
                }
            }

            final MCRegistration registration = new MCRegistration(comServices, mcServices.getParameterService(),
                    mcServices.getAggregationService(), mcServices.getAlertService(), mcServices.getActionService());
            mcAdapter.initialRegistrations(registration);
        }

        final String primaryURI = this.directoryService.getConnection().getPrimaryConnectionDetails().getProviderURI().toString();

        final SingleConnectionDetails det = this.directoryService.getConnection().getSecondaryConnectionDetails();
        final String secondaryURI = (det != null) ? det.getProviderURI().toString() : null;
        this.writeCentralDirectoryServiceURI(primaryURI, secondaryURI);
        Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
                "NanoSat MO Supervisor initialized! URI: " + primaryURI + "\n");

        // We just loaded everything, it is a good time to 
        // hint the garbage collector and clean up some memory
        // NanoSatMOFrameworkProvider.hintGC();
    }

    @Override
    public void addCloseAppListener(final CloseAppListener closeAppAdapter) {
        this.closeAppAdapter = closeAppAdapter;
    }

    /**
     * It closes the App gracefully.
     *
     * @param source The source of the triggering. Can be null
     */
    @Override
    public final void closeGracefully(final ObjectId source) {
        try {
            long startTime = System.currentTimeMillis();

            // Acknowledge the reception of the request to close (Closing...)
            Long eventId = this.getCOMServices().getEventService().generateAndStoreEvent(
                    AppsLauncherHelper.STOPPING_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    null,
                    null,
                    source,
                    null);

            final URI uri = this.getCOMServices().getEventService().getConnectionProvider().getConnectionDetails().getProviderURI();
            this.getCOMServices().getEventService().publishEvent(uri, eventId,
                    AppsLauncherHelper.STOPPING_OBJECT_TYPE, null, source, null);

            // Close the app...
            // Make a call on the app layer to close nicely...
            if (this.closeAppAdapter != null) {
                Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
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

            this.getCOMServices().getEventService().publishEvent(uri, eventId2,
                    AppsLauncherHelper.STOPPED_OBJECT_TYPE, null, source, null);

            // Should close them safely as well...
//        provider.getMCServices().closeServices();
//        provider.getCOMServices().closeServices();
            this.getCOMServices().closeAll();

            // Exit the Java application
            Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.INFO,
                    "Success! The currently running Java Virtual Machine will now terminate. "
                    + "(NanoSat MO Supervisor closed in: " + (System.currentTimeMillis() - startTime) + " ms)\n");

        } catch (NMFException ex) {
            Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.exit(0);
    }

}
