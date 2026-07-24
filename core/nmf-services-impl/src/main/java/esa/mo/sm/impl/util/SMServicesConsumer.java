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
package esa.mo.sm.impl.util;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.sm.impl.consumer.AppsLauncherConsumerServiceImpl;
import esa.mo.sm.impl.consumer.CommandExecutorConsumerServiceImpl;
import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import esa.mo.sm.impl.consumer.PackageManagementConsumerServiceImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.commandexecutor.CommandExecutorServiceInfo;
import org.ccsds.moims.mo.sm.heartbeat.HeartbeatServiceInfo;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementServiceInfo;

/**
 * Class holding all the COM services consumers. The services can all be
 * initialized automatically or can be set manually.
 */
public class SMServicesConsumer {

    private PackageManagementConsumerServiceImpl packageManagementService;
    private AppsLauncherConsumerServiceImpl appsLauncherService;
    private CommandExecutorConsumerServiceImpl commandExecutorService;
    private HeartbeatConsumerServiceImpl heartbeatService;

    /**
     * Default constructor.
     */
    public SMServicesConsumer() {
    }

    /**
     * Initializes the Software Management services.
     *
     * @param connectionConsumer The connection details.
     * @param comServices The COM services.
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {
        init(connectionConsumer, comServices, null, null);
    }

    /**
     * Initializes the Software Management services.
     *
     * @param connectionConsumer The connection details.
     * @param comServices The COM services.
     * @param authenticationId The authenticationId of the logged in user.
     * @param localNamePrefix The local name prefix.
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices,
            Blob authenticationId, String localNamePrefix) {
        SingleConnectionDetails details;

        try {
            // Initialize the Apps Launcher service
            details = connectionConsumer.getServicesDetails().get(
                    AppsLauncherServiceInfo.APPSLAUNCHER_SERVICE_NAME);
            if (details != null) {
                appsLauncherService = new AppsLauncherConsumerServiceImpl(details, comServices);
            }

            // Initialize the Command Executor Service service
            details = connectionConsumer.getServicesDetails().get(
                    CommandExecutorServiceInfo.COMMANDEXECUTOR_SERVICE_NAME);
            if (details != null) {
                commandExecutorService = new CommandExecutorConsumerServiceImpl(details, comServices);
            }

            // Initialize the Package Management service
            details = connectionConsumer.getServicesDetails().get(
                    PackageManagementServiceInfo.PACKAGEMANAGEMENT_SERVICE_NAME);
            if (details != null) {
                packageManagementService = new PackageManagementConsumerServiceImpl(details, comServices);
            }

            // Initialize the Heartbeat service
            details = connectionConsumer.getServicesDetails().get(HeartbeatServiceInfo.HEARTBEAT_SERVICE_NAME);
            if (details != null) {
                heartbeatService = new HeartbeatConsumerServiceImpl(details, comServices);
            }
        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(SMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the PackageManagement service consumer.
     *
     * @return the PackageManagement service consumer, or {@code null} if not initialized
     */
    public PackageManagementConsumerServiceImpl getPackageManagementService() {
        return this.packageManagementService;
    }

    /**
     * Returns the AppsLauncher service consumer.
     *
     * @return the AppsLauncher service consumer, or {@code null} if not initialized
     */
    public AppsLauncherConsumerServiceImpl getAppsLauncherService() {
        return this.appsLauncherService;
    }

    /**
     * Returns the CommandExecutor service consumer.
     *
     * @return the CommandExecutor service consumer, or {@code null} if not initialized
     */
    public CommandExecutorConsumerServiceImpl getCommandExecutorService() {
        return this.commandExecutorService;
    }

    /**
     * Returns the Heartbeat service consumer.
     *
     * @return the Heartbeat service consumer, or {@code null} if not initialized
     */
    public HeartbeatConsumerServiceImpl getHeartbeatService() {
        return this.heartbeatService;
    }

    /**
     * Sets manually all the COM consumer services.
     *
     * @param packageManagementService Package Management service consumer.
     * @param appsLauncherService Applications Manager service consumer.
     * @param heartbeatService Heartbeat service consumer.
     * @param commandExecutorService Command Executor Service consumer.
     */
    public void setServices(PackageManagementConsumerServiceImpl packageManagementService,
            AppsLauncherConsumerServiceImpl appsLauncherService, HeartbeatConsumerServiceImpl heartbeatService,
            CommandExecutorConsumerServiceImpl commandExecutorService) {
        this.packageManagementService = packageManagementService;
        this.appsLauncherService = appsLauncherService;
        this.heartbeatService = heartbeatService;
        this.commandExecutorService = commandExecutorService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if (this.packageManagementService != null) {
            this.packageManagementService.closeConnection();
        }

        if (this.appsLauncherService != null) {
            this.appsLauncherService.closeConnection();
        }

        if (this.commandExecutorService != null) {
            this.commandExecutorService.closeConnection();
        }

        if (this.heartbeatService != null) {
            this.heartbeatService.closeConnection();
        }
    }

    /**
     * Propagates the authentication id to all the initialized SM service consumers.
     *
     * @param authenticationId the authentication id of the logged in user
     */
    public void setAuthenticationId(Blob authenticationId) {
        if (this.packageManagementService != null) {
            this.packageManagementService.setAuthenticationId(authenticationId);
        }

        if (this.appsLauncherService != null) {
            this.appsLauncherService.setAuthenticationId(authenticationId);
        }

        if (this.commandExecutorService != null) {
            this.commandExecutorService.setAuthenticationId(authenticationId);
        }

        if (this.heartbeatService != null) {
            this.heartbeatService.setAuthenticationId(authenticationId);
        }
    }

}
