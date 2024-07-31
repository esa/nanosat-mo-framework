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
import esa.mo.sm.impl.consumer.PackageManagementConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.sm.impl.consumer.CommandExecutorConsumerServiceImpl;
import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.HeartbeatHelper;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.PackageManagementHelper;

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
     * Initializes the Package Management service and the Applications Manager
     * service
     *
     * @param connectionConsumer Connection details
     * @param comServices COM services
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices) {
        init(connectionConsumer, comServices, null, null);
    }

    /**
     * Initializes the Package Management service and the Applications Manager
     * service
     *
     * @param connectionConsumer Connection details
     * @param comServices COM services
     * @param authenticationId authenticationId of the logged in user
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices,
            Blob authenticationId, String localNamePrefix) {
        SingleConnectionDetails details;

        try {
            // Initialize the Apps Launcher service
            details = connectionConsumer.getServicesDetails().get(
                    AppsLauncherHelper.APPSLAUNCHER_SERVICE_NAME);
            if (details != null) {
                appsLauncherService = new AppsLauncherConsumerServiceImpl(
                        details, comServices, authenticationId, localNamePrefix);
            }

            // Initialize the Command Executor Service service
            details = connectionConsumer.getServicesDetails().get(
                    CommandExecutorHelper.COMMANDEXECUTOR_SERVICE_NAME);
            if (details != null) {
                commandExecutorService = new CommandExecutorConsumerServiceImpl(
                        details, comServices, authenticationId, localNamePrefix);
            }

            // Initialize the Package Management service
            details = connectionConsumer.getServicesDetails().get(
                    PackageManagementHelper.PACKAGEMANAGEMENT_SERVICE_NAME);
            if (details != null) {
                packageManagementService = new PackageManagementConsumerServiceImpl(
                        details, comServices, authenticationId, localNamePrefix);
            }

            // Initialize the Heartbeat service
            details = connectionConsumer.getServicesDetails().get(HeartbeatHelper.HEARTBEAT_SERVICE_NAME);
            if (details != null) {
                heartbeatService = new HeartbeatConsumerServiceImpl(details, 
                        comServices, authenticationId, localNamePrefix);
            }
        } catch (MALException | MalformedURLException | MALInteractionException ex) {
            Logger.getLogger(SMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PackageManagementConsumerServiceImpl getPackageManagementService() {
        return this.packageManagementService;
    }

    public AppsLauncherConsumerServiceImpl getAppsLauncherService() {
        return this.appsLauncherService;
    }

    public CommandExecutorConsumerServiceImpl getCommandExecutorService() {
        return this.commandExecutorService;
    }

    public HeartbeatConsumerServiceImpl getHeartbeatService() {
        return this.heartbeatService;
    }

    /**
     * Sets manually all the COM consumer services
     *
     * @param packageManagementService Package Management service consumer
     * @param appsLauncherService Applications Manager service consumer
     * @param heartbeatService Heartbeat service consumer
     * @param commandExecutorService Command Executor Service consumer
     */
    public void setServices(PackageManagementConsumerServiceImpl packageManagementService,
            AppsLauncherConsumerServiceImpl appsLauncherService,
            HeartbeatConsumerServiceImpl heartbeatService,
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
            this.packageManagementService.close();
        }

        if (this.appsLauncherService != null) {
            this.appsLauncherService.close();
        }

        if (this.commandExecutorService != null) {
            this.commandExecutorService.close();
        }

        if (this.heartbeatService != null) {
            this.heartbeatService.close();
        }
    }

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
