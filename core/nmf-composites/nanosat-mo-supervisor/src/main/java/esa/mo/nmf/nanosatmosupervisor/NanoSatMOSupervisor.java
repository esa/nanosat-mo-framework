/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.Quota;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.nmf.NMFProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.misc.AppShutdownGuard;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.CloseAppListener;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.OneInstanceLock;
import esa.mo.nmf.nmfpackage.Deployment;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.reconfigurable.provider.PersistProviderConfiguration;
import esa.mo.sm.impl.provider.AppsLauncherProviderServiceImpl;
import esa.mo.sm.impl.provider.CommandExecutorProviderServiceImpl;
import esa.mo.sm.impl.util.PMBackend;
import esa.mo.sm.impl.provider.PackageManagementProviderServiceImpl;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;

/**
 * The implementation of the NanoSat MO Supervisor that can be extended by
 * particular implementations.
 *
 * @author Cesar Coelho
 */
public abstract class NanoSatMOSupervisor extends NMFProvider {

    private static final Logger LOGGER = Logger.getLogger(NanoSatMOSupervisor.class.getName());

    private final PackageManagementProviderServiceImpl packageManagementService
            = new PackageManagementProviderServiceImpl();
    private final AppsLauncherProviderServiceImpl appsLauncherService
            = new AppsLauncherProviderServiceImpl();
    private final CommandExecutorProviderServiceImpl commandExecutorService
            = new CommandExecutorProviderServiceImpl();

    /**
     * Initializes the NanoSat MO Supervisor. The MonitorAndControlAdapter
     * adapter class can be extended for remote monitoring and control with the
     * CCSDS Monitor and Control services. One can also extend the
     * SimpleMonitorAndControlAdapter class which contains a simpler interface.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the
     * corresponding methods and variables of a specific entity.
     * @param platformServices The Platform services consumer stubs
     * @param packageManagementBackend The Package Management services backend.
     */
    public void init(MonitorAndControlNMFAdapter mcAdapter,
            PlatformServicesConsumer platformServices,
            PMBackend packageManagementBackend) {
        super.startTime = System.currentTimeMillis();
        this.generateStartBanner();
        OneInstanceLock lock = new OneInstanceLock();
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
        ConnectionProvider.resetURILinks();

        // Check if we are running as root when we have the NMF in Mode 2
        String user = System.getProperties().getProperty("user.name", "?");
        String mode = System.getProperties().getProperty(HelperMisc.PROP_WORK_DIR_STORAGE_MODE, "?");

        if ("root".equals(user) && mode.equals("2")) {
            throw new RuntimeException("Do not run the NanoSat MO Supervisor as root!");
        }

        // Enforce the App Name property to be Const.NANOSAT_MO_SUPERVISOR_NAME
        System.setProperty(HelperMisc.PROP_MO_APP_NAME, Const.NANOSAT_MO_SUPERVISOR_NAME);

        // Provider name to be used on the Directory service...
        this.providerName = System.getProperty(HelperMisc.PROP_MO_APP_NAME);

        this.platformServices = platformServices;

        try {
            Quota stdQuota = new Quota();
            this.comServices.init();
            this.heartbeatService.init();
            this.directoryService.init(comServices);
            this.appsLauncherService.init(comServices, directoryService);
            this.commandExecutorService.init(comServices);
            this.packageManagementService.init(comServices, packageManagementBackend);
            this.comServices.initArchiveSync();
            super.reconfigurableServices.add(this.appsLauncherService);
            this.appsLauncherService.setStdQuotaPerApp(stdQuota);
            this.appsLauncherService.addFolderWithApps(Deployment.getAppsDir());
            this.comServices.getArchiveSyncService().setStdQuota(stdQuota);
            this.startMCServices(mcAdapter);
            this.initPlatformServices(comServices);
        } catch (MALException ex) {
            LOGGER.log(Level.SEVERE,
                    "The services could not be initialized. "
                    + "Perhaps there's something wrong with the Transport Layer.", ex);
            return;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                //Your 'pos' shutdown code goes here...
                Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(
                        Level.INFO, "Shutting down Supervisor...");

                // Retrieve all apps and then filter for the ones that are running...
                try {
                    IdentifierList allApps = new IdentifierList();
                    allApps.add(new Identifier("*"));
                    ListAppResponse response = appsLauncherService.listApp(allApps, new Identifier("*"), null);
                    LongList runningApps = new LongList();

                    for (int i = 0; i < response.getBodyElement0().size(); i++) {
                        Long appId = response.getBodyElement0().get(i);
                        if (response.getBodyElement1().get(i)) {
                            runningApps.add(appId);
                        }
                    }

                    Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(
                            Level.SEVERE, "Stopping {0} App(s)!", runningApps.size());

                    appsLauncherService.stopApp(runningApps, null);
                } catch (MALInteractionException ex) {
                    Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(
                            Level.SEVERE, "(1) Something went wrong...", ex);
                } catch (MALException ex) {
                    Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(
                            Level.SEVERE, "(2) Something went wrong...", ex);
                }

                Logger.getLogger(NanoSatMOSupervisor.class.getName()).log(
                        Level.INFO, "Done!");
            }
        });

        // Are the dynamic changes enabled?
        if ("true".equals(System.getProperty(Const.DYNAMIC_CHANGES_PROPERTY))) {
            LOGGER.log(Level.INFO, "Loading previous configurations...");

            // Activate the previous configuration
            final ObjectId confId = new ObjectId(ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE,
                    new ObjectKey(ConfigurationProviderSingleton.getDomain(),
                            DEFAULT_PROVIDER_CONFIGURATION_OBJID));

            super.providerConfiguration = new PersistProviderConfiguration(this, confId,
                    comServices.getArchiveService());

            try {
                super.providerConfiguration.loadPreviousConfigurations();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        if (mcAdapter != null) {
            MCRegistration registration
                    = new MCRegistration(comServices, mcServices.getParameterService(),
                            mcServices.getAggregationService(), mcServices.getAlertService(),
                            mcServices.getActionService());
            mcAdapter.initialRegistrations(registration);
        }

        // Populate the Directory service with the entries from the URIs File
        LOGGER.log(Level.INFO, "Populating Directory service...");
        directoryService.loadURIs(Const.NANOSAT_MO_SUPERVISOR_NAME);

        String primaryURI
                = directoryService.getConnection().getPrimaryConnectionDetails().getProviderURI().toString();

        SingleConnectionDetails det
                = directoryService.getConnection().getSecondaryConnectionDetails();
        String secondaryURI = (det != null) ? det.getProviderURI().toString() : null;
        writeCentralDirectoryServiceURI(primaryURI, secondaryURI);

        LOGGER.log(Level.INFO, "NanoSat MO Supervisor initialized in "
                + (((float) (System.currentTimeMillis() - super.startTime)) / 1000)
                + " seconds!");
        LOGGER.log(Level.INFO, "URI: {0}\n", primaryURI);
    }

    public AppsLauncherProviderServiceImpl getAppsLauncherService() {
        return appsLauncherService;
    }

    @Override
    public void setCloseAppListener(final CloseAppListener closeAppAdapter) {
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
            AppShutdownGuard.start();
            long timestamp = System.currentTimeMillis();

            // Acknowledge the reception of the request to close (Closing...)
            Long eventId = this.getCOMServices().getEventService().generateAndStoreEvent(
                    AppsLauncherHelper.STOPPING_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    null,
                    null,
                    source,
                    null);

            final URI uri
                    = this.getCOMServices().getEventService().getConnectionProvider().getConnectionDetails().getProviderURI();

            try {
                this.getCOMServices().getEventService().publishEvent(uri, eventId,
                        AppsLauncherHelper.STOPPING_OBJECT_TYPE, null, source, null);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

            // Close the app...
            // Make a call on the app layer to close nicely...
            if (this.closeAppAdapter != null) {
                LOGGER.log(Level.INFO,
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
                LOGGER.log(Level.SEVERE, null, ex);
            }

            // Should close them safely as well...
//        provider.getMCServices().closeServices();
//        provider.getCOMServices().closeServices();
            this.getCOMServices().closeAll();

            // Exit the Java application
            long duration = System.currentTimeMillis() - timestamp;
            LOGGER.log(Level.INFO,
                    "Success! The currently running Java Virtual Machine will now terminate. "
                    + "(NanoSat MO Supervisor closed in: " + duration + " ms)\n");
        } catch (NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        System.exit(0);
    }

    public abstract void initPlatformServices(COMServicesProvider comServices);

}
