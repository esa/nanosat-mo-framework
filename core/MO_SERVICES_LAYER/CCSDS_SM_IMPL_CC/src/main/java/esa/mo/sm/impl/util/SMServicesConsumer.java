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

package esa.mo.sm.impl.util;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.sm.impl.consumer.AppsLauncherConsumerServiceImpl;
import esa.mo.sm.impl.consumer.PackageManagementConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.PackageManagementHelper;

/**
 * Class holding all the COM services consumers. The services can all be
 * initialized automatically or can be set manually.
 */
public class SMServicesConsumer {

    private PackageManagementConsumerServiceImpl packageManagementService;
    private AppsLauncherConsumerServiceImpl appsLauncherService;

    /**
     * Initializes the Package Management service and the Applications Manager
     * service
     *
     * @param connectionConsumer Connection details
     * @param comServices COM services
     */
    public void init(ConnectionConsumer connectionConsumer, COMServicesConsumer comServices){

        SingleConnectionDetails details;

        try {
            // Initialize the Apps Launcher service
            details = connectionConsumer.getServicesDetails().get(AppsLauncherHelper.APPSLAUNCHER_SERVICE_NAME);
            if(details != null){
                appsLauncherService = new AppsLauncherConsumerServiceImpl(details, comServices);                
            }

            // Initialize the Package Management service
            details = connectionConsumer.getServicesDetails().get(PackageManagementHelper.PACKAGEMANAGEMENT_SERVICE_NAME);
            if(details != null){
                packageManagementService = new PackageManagementConsumerServiceImpl(details, comServices);
            }
        } catch (MALException ex) {
            Logger.getLogger(SMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(SMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(SMServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public PackageManagementConsumerServiceImpl getPackageManagementService() {
        return this.packageManagementService;
    }

    public AppsLauncherConsumerServiceImpl getAppsLauncherService() {
        return this.appsLauncherService;
    }

    /**
     * Sets manually all the COM consumer services
     *
     * @param packageManagementService Package Management service consumer
     * @param appsLauncherService Applications Manager service consumer
     */
    public void setServices(PackageManagementConsumerServiceImpl packageManagementService, AppsLauncherConsumerServiceImpl appsLauncherService) {
        this.packageManagementService = packageManagementService;
        this.appsLauncherService = appsLauncherService;
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if(this.packageManagementService != null){
            this.packageManagementService.closeConnection();
        }
        
        if(this.appsLauncherService != null){
            this.appsLauncherService.closeConnection();
        }
    }
    
}
