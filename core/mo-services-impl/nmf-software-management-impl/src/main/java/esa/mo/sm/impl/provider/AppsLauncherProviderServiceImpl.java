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
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;
import esa.mo.reconfigurable.service.ReconfigurableService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSet;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSetList;
import org.ccsds.moims.mo.common.directory.provider.DirectoryInheritanceSkeleton;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Element;
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
import org.ccsds.moims.mo.mal.structures.UIntegerList;
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
import org.ccsds.moims.mo.softwaremanagement.appslauncher.provider.StopAppInteraction;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetailsList;

/**
 * Apps Launcher service Provider.
 */
public class AppsLauncherProviderServiceImpl extends AppsLauncherInheritanceSkeleton implements ReconfigurableService {

    public final static String PROVIDER_PREFIX_NAME = "App: ";
    private MALProvider appsLauncherServiceProvider;
    private MonitorExecutionPublisher publisher;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private AppsLauncherManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private COMServicesProvider comServices;
    private DirectoryInheritanceSkeleton directoryService;
    private ConfigurationChangeListener configurationAdapter;

    /**
     * Initializes the Event service provider
     *
     * @param comServices
     * @param directoryService
     * @throws MALException On initialization error.
     */
    public synchronized void init(final COMServicesProvider comServices,
            final DirectoryInheritanceSkeleton directoryService) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME,
                    SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) == null) {
                SoftwareManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                AppsLauncherHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        publisher = createMonitorExecutionPublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != appsLauncherServiceProvider) {
            connection.closeAll();
        }

        this.comServices = comServices;
        this.directoryService = directoryService;
        manager = new AppsLauncherManager(comServices);
        appsLauncherServiceProvider = connection.startService(AppsLauncherHelper.APPSLAUNCHER_SERVICE_NAME.toString(),
                AppsLauncherHelper.APPSLAUNCHER_SERVICE, this);
        running = true;
        initialiased = true;

        Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.INFO, "Apps Launcher service: READY");
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    private void publishExecutionMonitoring(final Long appObjId, final String outputText) {
        try {
            synchronized (lock) {
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating update for the App: {0} (Identifier: {1})",
                    new Object[]{
                        appObjId, new Identifier(manager.get(appObjId).getName().toString())
                    });

            final StringList outputList = new StringList();

            // Should not be store in the Archive... it's too much stuff
            final EntityKey ekey = new EntityKey(new Identifier(manager.get(appObjId).getName().toString()), appObjId, null, null);
            final Time timestamp = HelperTime.getTimestampMillis();

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));

            outputList.add(outputText);
            publisher.publish(hdrlst, outputList);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public void runApp(LongList appInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == appInstIds) { // Is the input null?
            throw new IllegalArgumentException("appInstIds argument must not be null");
        }

        // Refresh the list of available Apps
        boolean anyChanges = this.manager.refreshAvailableAppsList(connection.getPrimaryConnectionDetails().getProviderURI());

        if (anyChanges) {
            // Update the Configuration available on the COM Archive
            if (this.configurationAdapter != null) {
                this.configurationAdapter.onConfigurationChanged(this);
            }
        }

        for (int index = 0; index < appInstIds.size(); index++) {
            AppDetails app = (AppDetails) this.manager.get(appInstIds.get(index)); // get it from the list of available apps

            // The app id could not be identified?
            if (app == null) {
                unkIndexList.add(new UInteger(index)); // Throw an UNKNOWN error
                continue;
            }

            // Is the app already running?
            if (manager.isAppRunning(appInstIds.get(index))) {
                invIndexList.add(new UInteger(index)); // Throw an INVALID error
                continue;
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // Run the apps!
        for (int i = 0; i < appInstIds.size(); i++) {
            try {
                manager.startAppProcess(new ProcessExecutionHandler(appInstIds.get(i)), interaction);
            } catch (IOException ex) {
                UIntegerList intIndexList = new UIntegerList();
                intIndexList.add(new UInteger(i));
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
                        "Not able to start the application process...", ex);
                throw new MALInteractionException(new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, intIndexList));
            }
        }
    }

    @Override
    public void killApp(LongList appInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == appInstIds) { // Is the input null?
            throw new IllegalArgumentException("appInstIds argument must not be null");
        }

        // Refresh the list of available Apps
        boolean anyChanges = this.manager.refreshAvailableAppsList(connection.getPrimaryConnectionDetails().getProviderURI());

        if (anyChanges) {
            // Update the Configuration available on the COM Archive
            if (this.configurationAdapter != null) {
                this.configurationAdapter.onConfigurationChanged(this);
            }
        }

        for (int index = 0; index < appInstIds.size(); index++) {
            // Get it from the list of available apps
            AppDetails app = (AppDetails) this.manager.get(appInstIds.get(index));

            // The app id could not be identified?
            if (app == null) {
                unkIndexList.add(new UInteger(index)); // Throw an UNKNOWN error
                continue;
            }

            // Is the app the app not running?
            if (!manager.isAppRunning(appInstIds.get(index))) {
                invIndexList.add(new UInteger(index)); // Throw an INVALID error
                continue;
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // Kill the apps!
        for (int i = 0; i < appInstIds.size(); i++) {
            manager.killAppProcess(appInstIds.get(i), interaction);
        }
    }

    @Override
    public void stopApp(final LongList appInstIds, final StopAppInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList intIndexList = new UIntegerList();
        ArrayList<SingleConnectionDetails> appConnections = new ArrayList<SingleConnectionDetails>();

        if (null == appInstIds) { // Is the input null?
            throw new IllegalArgumentException("appInstIds argument must not be null");
        }

        // Refresh the list of available Apps
        boolean anyChanges = this.manager.refreshAvailableAppsList(connection.getPrimaryConnectionDetails().getProviderURI());

        if (anyChanges) {
            // Update the Configuration available on the COM Archive
            if (this.configurationAdapter != null) {
                this.configurationAdapter.onConfigurationChanged(this);
            }
        }

        IdentifierList appDirectoryNames = new IdentifierList();

        for (int i = 0; i < appInstIds.size(); i++) {
            // Get it from the list of available apps
            AppDetails app = (AppDetails) this.manager.get(appInstIds.get(i));

            // The app id could not be identified?
            if (app == null) {
                unkIndexList.add(new UInteger(i)); // Throw an UNKNOWN error
                continue;
            }

            // Is the app the app not running?
            if (!manager.isAppRunning(appInstIds.get(i))) {
                invIndexList.add(new UInteger(i)); // Throw an INVALID error
                continue;
            }

            // Define the filter in order to get the Event service URI of the app
            final Identifier serviceProviderName = new Identifier(PROVIDER_PREFIX_NAME + app.getName());
            final IdentifierList domain = new IdentifierList();
            domain.add(new Identifier("*"));
            final COMService eventCOM = EventHelper.EVENT_SERVICE;
            ServiceKey serviceKey = new ServiceKey(eventCOM.getArea().getNumber(),
                    eventCOM.getNumber(), eventCOM.getArea().getVersion());
            ServiceFilter sf = new ServiceFilter(serviceProviderName, domain, new Identifier("*"),
                    null, new Identifier("*"), serviceKey, new UIntegerList());

            // Do a lookup on the Central Drectory service for the app that we want
            ProviderSummaryList providersList = this.directoryService.lookupProvider(sf, interaction.getInteraction());
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.FINER,
                    "providersList object: " + providersList.toString());

            try {
                // Add here the filtering for the best IPC!!!

                final SingleConnectionDetails connectionDetails
                        = AppsLauncherManager.getSingleConnectionDetailsFromProviderSummaryList(providersList);
                appConnections.add(connectionDetails);

                // Add to the list of Directory service Obj Ids
                if (!providersList.isEmpty()) {
                    appDirectoryNames.add(providersList.get(0).getProviderName());
                } else {
                    appDirectoryNames.add(null);
                }
            } catch (IOException ex) {
                intIndexList.add(new UInteger(i)); // Throw an INTERNAL error
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!intIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, intIndexList));
        }

        interaction.sendAcknowledgement();

        manager.stopApps(appInstIds, appDirectoryNames, appConnections, interaction);
    }

    @Override
    public ListAppResponse listApp(final IdentifierList appNames, final Identifier category,
            final MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        ListAppResponse outList = new ListAppResponse();

        if (null == appNames) { // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        // Refresh the list of available Apps
        boolean anyChanges = manager.refreshAvailableAppsList(connection.getPrimaryConnectionDetails().getProviderURI());

        if (anyChanges) {
            // Update the Configuration available on the COM Archive
            if (this.configurationAdapter != null) {
                this.configurationAdapter.onConfigurationChanged(this);
            }
        }

        LongList ids = new LongList();
        BooleanList runningApps = new BooleanList();

        for (int index = 0; index < appNames.size(); index++) {
            if ("*".equals(appNames.get(index).getValue())) {
                ids.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                ids.addAll(manager.listAll());
                break;
            }

            if (manager.list(appNames.get(index)) == null) {
                // The app does not exist...
                unkIndexList.add(new UInteger(index)); // Throw an UNKNOWN error
            }
        }

        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        for (Long id : ids) { // Is the app running?
            runningApps.add(manager.isAppRunning(id));
        }

        outList.setBodyElement0(ids);
        outList.setBodyElement1(runningApps);

        return outList;
    }

    @Override
    public void setOnConfigurationChangeListener(final ConfigurationChangeListener configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override
    public Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails) {
        // Validate the configuration...
        if (configurationObjectDetails == null) {
            return false;
        }

        if (configurationObjectDetails.getConfigObjects() == null) {
            return false;
        }

        // Is the size 1?
        if (configurationObjectDetails.getConfigObjects().size() != 1) {  // 1 because we just have Apps as configuration objects in this service
            return false;
        }

        ConfigurationObjectSet confSet = configurationObjectDetails.getConfigObjects().get(0);

        // Confirm the objType
        if (!confSet.getObjType().equals(AppsLauncherHelper.APP_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if (confSet.getObjInstIds().isEmpty()) {
            manager.reconfigureDefinitions(new LongList(), new AppDetailsList());   // Reconfigures the Manager
            return true;
        }

        // ok, we're good to go...
        // Load the App Details from this configuration...
        AppDetailsList pDefs = (AppDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                AppsLauncherHelper.APP_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSet.getObjInstIds());

        manager.reconfigureDefinitions(confSet.getObjInstIds(), pDefs);   // Reconfigures the Manager

        for (Long id : confSet.getObjInstIds()) { // Set all running state to false
            manager.setRunning(id, false, null);
        }

        return true;
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        final HashMap<Long, Element> defObjs = manager.getCurrentDefinitionsConfiguration();

        final ConfigurationObjectSet objsSet = new ConfigurationObjectSet();
        objsSet.setDomain(ConfigurationProviderSingleton.getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(AppsLauncherHelper.APP_OBJECT_TYPE);

        final ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSet);

        // Needs the Common API here!
        final ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(list);

        return set;
    }

    @Override
    public COMService getCOMService() {
        return AppsLauncherHelper.APPSLAUNCHER_SERVICE;
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
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterAckReceived");
//            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.INFO, "Registration Ack: {0}", header.toString());
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    public class ProcessExecutionHandler {

        private final Timer timer = new Timer();
        private final static int PERIOD_PUB = 2 * 1000; // Publish every 2 seconds
        private final Long appObjId;
        private Thread collectStream1;
        private Thread collectStream2;
        private Process process = null;

        public ProcessExecutionHandler(final Long appId) {
            this.appObjId = appId;
        }

        public Long getAppInstId() {
            return appObjId;
        }

        public Process getProcess() {
            return process;
        }

        public void close() {
            timer.cancel();
            process.destroy();
        }

        public void startPublishing(final Process process) {
            this.process = process;

            final StringBuilder buffer = new StringBuilder();

            // Every PERIOD_PUB seconds, publish the String data
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Change the buffer position
                    int size = buffer.length();
                    if (size != 0) {
                        String output = buffer.substring(0, size);
                        buffer.delete(0, size);

                        // Publish what's in the buffer every PERIOD_PUB
                        // Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(Level.INFO, output);
                        publishExecutionMonitoring(appObjId, output);
                    }
                }
            }, 0, PERIOD_PUB);

            collectStream1 = createThreadReader(buffer, new BufferedReader(new InputStreamReader(process.getInputStream())));
            collectStream2 = createThreadReader(buffer, new BufferedReader(new InputStreamReader(process.getErrorStream())));

            collectStream1.start();
            collectStream2.start();
        }

        private Thread createThreadReader(final StringBuilder buf, final BufferedReader br) {
            return new Thread() {
                @Override
                public void run() {
                    this.setName("AppsLauncher_ThreadReader");
                    try {
                        String line;

                        while ((line = br.readLine()) != null) {
                            buf.append(line);
                            buf.append("\n");
                        }
                    } catch (IOException ex) {
                        // The App stream is closed!
                        AppDetails details = manager.get(appObjId);
                        Logger.getLogger(AppsLauncherProviderServiceImpl.class.getName()).log(
                                Level.INFO, "The stream of the App " + appObjId
                                + " (name: " + details.getName().toString()
                                + ") has been closed.");

                        close();
                    }
                }
            };
        }
    }

}
