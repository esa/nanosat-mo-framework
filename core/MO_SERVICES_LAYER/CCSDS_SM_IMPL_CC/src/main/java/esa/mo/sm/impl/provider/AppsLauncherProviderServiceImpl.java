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
package esa.mo.sm.impl.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.sm.impl.util.OSValidator;
import esa.mo.sm.impl.util.ShellCommander;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.provider.AppsLauncherInheritanceSkeleton;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetailsList;

/**
 *
 */
public class AppsLauncherProviderServiceImpl extends AppsLauncherInheritanceSkeleton {

    private MALProvider appsLauncherServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private AppsLauncherManager manager;
    private final ConfigurationProvider configuration = new ConfigurationProvider();
    private final ConnectionProvider connection = new ConnectionProvider();
    private COMServicesProvider comServices;

    /**
     * Initializes the Event service provider
     *
     * @param comServices
     * @throws MALException On initialization error.
     */
    public synchronized void init(COMServicesProvider comServices) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                AppsLauncherHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        this.comServices = comServices;
        manager = new AppsLauncherManager(comServices);
        appsLauncherServiceProvider = connection.startService(AppsLauncherHelper.APPSLAUNCHER_SERVICE_NAME.toString(), AppsLauncherHelper.APPSLAUNCHER_SERVICE, this);
        running = true;
        initialiased = true;
        Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.INFO, "Applications Manager service: READY");
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public void stopApp(LongList appInstIds, MALInteraction interaction) throws MALInteractionException, MALException {

        // Is the app still running?
        
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void killApp(LongList appInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ListAppResponse listApp(IdentifierList appNames, Identifier category, MALInteraction interaction) throws MALInteractionException, MALException {

        ListAppResponse outList = new ListAppResponse();

        if (null == appNames) { // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        // Refresh the list of available Apps
        manager.refreshAvailableAppsList();
        LongList ids = new LongList();
        BooleanList runningApps = new BooleanList();

        for (Identifier appName : appNames) {
            if ("*".equals(appName.getValue())) {
                ids.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                ids.addAll(manager.listAll());
                
                for (Long id : ids){
                    runningApps.add(manager.isAppRunning(id));
                }
                
                break;
            }

            Long id = manager.list(appName);

            if (id != null) {
                runningApps.add(manager.isAppRunning(id));
            } else {
                runningApps.add(false);

                // Throw an error
            }
        }
        
        outList.setBodyElement0(ids);
        outList.setBodyElement1(runningApps);

        return outList;
    }

    @Override
    public AppDetailsList getAppDetails(LongList appInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        
        AppDetailsList outList = new AppDetailsList();
        
        // Refresh the list of available Apps
        this.manager.refreshAvailableAppsList();

        for (Long appInstId : appInstIds) {
            AppDetails app = (AppDetails) this.manager.getDefs().get(appInstId); // get it from the list of available apps

            // The app id could not be identified?
            if (app == null) {

            }
            
            outList.add(app);

        }
        
        return outList;
    }

    @Override
    public void runApp(LongList appInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        // Refresh the list of available Apps
        this.manager.refreshAvailableAppsList();

        for (Long appInstId : appInstIds) {
            AppDetails app = (AppDetails) this.manager.getDefs().get(appInstId); // get it from the list of available apps

            // The app id could not be identified?
            if (app == null) {

            }

            // Is the app already running?
            if (manager.isAppRunning(appInstId)){
                // Throw an error!
                // To be done...
                
                continue;
            }
            
            // Run the app!
            manager.runApp(appInstId);
        }

    }
    
    
    

}
