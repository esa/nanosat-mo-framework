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
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.provider.AppsLauncherInheritanceSkeleton;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.provider.MonitorExecutionPublisher;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;

/**
 *
 */
public class AppsLauncherProviderServiceImpl extends AppsLauncherInheritanceSkeleton {

    private MALProvider appsLauncherServiceProvider;
    private MonitorExecutionPublisher publisher;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
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

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME, SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) == null) {
                SoftwareManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
            
            try {
                AppsLauncherHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        publisher = createMonitorExecutionPublisher(configuration.getDomain(),
                configuration.getNetwork(),
                SessionType.LIVE,
                new Identifier("LIVE"),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != appsLauncherServiceProvider) {
            connection.close();
        }
        
        this.comServices = comServices;
        manager = new AppsLauncherManager(comServices);
        appsLauncherServiceProvider = connection.startService(AppsLauncherHelper.APPSLAUNCHER_SERVICE_NAME.toString(), AppsLauncherHelper.APPSLAUNCHER_SERVICE, this);
        running = true;
        initialiased = true;
        Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.INFO, "Apps Launcher service: READY");
    }
    
    private void publishExecutionMonitoring(final Long appObjId, final String outputText) {
        try {
            if (!isRegistered) {
                final EntityKeyList lst = new EntityKeyList();
                lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                publisher.register(lst, new PublishInteractionListener());

                isRegistered = true;
            }

            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating update for the App: {0} (Identifier: {1})",
                    new Object[]{
                        appObjId, new Identifier(manager.get(appObjId).getName().toString())
                    });

//            final ParameterValue parameterValue = manager.getParameterValue(objId);
            final StringList outputList = new StringList();
            
            // Should not be store in the Archive... it's too much stuff
//            final Long pValObjId = manager.storeAndGeneratePValobjId(parameterValue, appObjId, connection.getConnectionDetails());

            final EntityKey ekey = new EntityKey(new Identifier(manager.get(appObjId).getName().toString()), appObjId, null, null);
            final Time timestamp = HelperTime.getTimestampMillis();

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
//            final ParameterValueList pVallst = new ParameterValueList();

            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
//            pVallst.add(parameterValue); // requirement: 3.3.5.2.8
            
            outputList.add(outputText);
            publisher.publish(hdrlst, outputList);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
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
        manager.refreshAvailableAppsList(connection.getConnectionDetails());
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
    public void runApp(LongList appInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        // Refresh the list of available Apps
        this.manager.refreshAvailableAppsList(connection.getConnectionDetails());

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
    
    
    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties)
                throws MALException {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
//            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterAckReceived");
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.INFO, "Registration Ack: {0}", header.toString());
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header,
                final MALErrorBody body,
                final Map qosProperties)
                throws MALException {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }
    

}
