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
package esa.mo.sm.impl.provider;

import esa.mo.com.impl.provider.EventProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.Quota;
import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.misc.Const;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;
import esa.mo.reconfigurable.service.ReconfigurableService;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSet;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSetList;
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
import org.ccsds.moims.mo.mal.structures.*;
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
import org.ccsds.moims.mo.softwaremanagement.commandexecutor.CommandExecutorHelper;

/**
 * Apps Launcher service Provider.
 */
public class AppsLauncherProviderServiceImpl extends AppsLauncherInheritanceSkeleton implements ReconfigurableService {

    public final static String PROVIDER_PREFIX_NAME = "App: ";
    private static final Logger LOGGER = Logger.getLogger(
            AppsLauncherProviderServiceImpl.class.getName());
    // Maximum length of a stderr/stdout chunk to be persisted - allows downlinking it via SPP without issues
    private static final int MAX_SEGMENT_SIZE = UShort.MAX_VALUE - 256;
    private MALProvider appsLauncherServiceProvider;
    private MonitorExecutionPublisher publisher;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private AppsLauncherManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private COMServicesProvider comServices;
    private DirectoryProviderServiceImpl directoryService;
    private ConfigurationChangeListener configurationAdapter;
    private int stdLimit; // Limit of stdout/stderr to allow in the archive.
    // Set of app ids for which the warning about verbose logging was sent
    private final Set<Long> verboseLoggingWarningSent = new HashSet<>();
    /**
     * Object used to track archive usage by STD output of each app
     */
    private Quota stdQuota = new Quota();

    /**
     * Initializes the Event service provider
     *
     * @param comServices
     * @param directoryService
     * @throws MALException On initialization error.
     */
    public synchronized void init(final COMServicesProvider comServices,
            final DirectoryProviderServiceImpl directoryService) throws MALException {
        long timestamp = System.currentTimeMillis();
        
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

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME,
                    SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION)
                    .getServiceByName(AppsLauncherHelper.APPSLAUNCHER_SERVICE_NAME) == null) {
                AppsLauncherHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
        }
        int kbyte = Integer.parseInt(System.getProperty(Const.APPSLAUNCHER_STD_LIMIT_PROPERTY,
                Const.APPSLAUNCHER_STD_LIMIT_DEFAULT));
        stdLimit = kbyte * 1024; // init limit with value of property
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
        appsLauncherServiceProvider = connection.startService(
                AppsLauncherHelper.APPSLAUNCHER_SERVICE_NAME.toString(),
                AppsLauncherHelper.APPSLAUNCHER_SERVICE, this);
        running = true;
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Apps Launcher service: READY! (" + timestamp + " ms)");
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    /**
     * Set the common quota object used by ArchiveSync service and
     * AppsLauncherService. The quota gets freed by ArchiveSync after
     * synchronizing STDOUT/STDERR entries.
     *
     * @param q The same Quota object passed to ArchiveSyncProviderServiceImpl.
     */
    public void setStdQuotaPerApp(Quota q) {
        this.stdQuota = q;
    }

    private void publishExecutionMonitoring(final Long appObjId, final String outputText,
            ObjectType objType) {
        try {
            synchronized (lock) {
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            LOGGER.log(Level.FINER,
                    "Generating update for the App: {0} (Identifier: {1})",
                    new Object[]{
                        appObjId, new Identifier(manager.get(appObjId).getName().toString())
                    });

            final StringList outputList = new StringList();

            // Should not be store in the Archive... it's too much stuff
            final EntityKey ekey = new EntityKey(
                    new Identifier(manager.get(appObjId).getName().toString()), appObjId, null, null);
            final Time timestamp = HelperTime.getTimestampMillis();

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(),
                    UpdateType.UPDATE, ekey));
            EventProviderServiceImpl eventService = this.comServices.getEventService();

            int length = outputText.length();
            for (int i = 0; i < length; i += MAX_SEGMENT_SIZE) {
                int end = Math.min(length, i + MAX_SEGMENT_SIZE);
                String segment = outputText.substring(i, end);
                outputList.add(segment);
                if (Boolean.parseBoolean(System.getProperty(Const.APPSLAUNCHER_STD_STORE_PROPERTY,
                        Const.APPSLAUNCHER_STD_STORE_DEFAULT))) {
                    // Store in COM archive if the option is enabled and below limit
                    int currentStd = stdQuota.retrieve(appObjId);
                    if (currentStd + segment.length() <= stdLimit) {
                        Element eventBody = new Union(segment);
                        stdQuota.increase(appObjId, segment.length());
                        IdentifierList domain = connection.getPrimaryConnectionDetails().getDomain();
                        ObjectId source = new ObjectId(AppsLauncherHelper.APP_OBJECT_TYPE, new ObjectKey(domain,
                                appObjId));
                        eventService.generateAndStoreEvent(
                                objType,
                                domain, eventBody, appObjId, source, null);
                    } else if (!verboseLoggingWarningSent.contains(appObjId)) {
                        String errorString
                                = "Your logging is too verbose and reached the limit.\nPlease reduce verbosity.";
                        Element eventBody = new Union(errorString);
                        outputList.add(errorString);
                        IdentifierList domain = connection.getPrimaryConnectionDetails().getDomain();
                        ObjectId source = new ObjectId(AppsLauncherHelper.APP_OBJECT_TYPE, new ObjectKey(domain,
                                appObjId));
                        eventService.generateAndStoreEvent(
                                objType,
                                domain, eventBody, appObjId, source, null);
                        verboseLoggingWarningSent.add(appObjId);
                    }
                }
            }

            publisher.publish(hdrlst, outputList);
        } catch (IllegalArgumentException | MALException | MALInteractionException ex) {
            LOGGER.log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public void runApp(LongList appInstIds, MALInteraction interaction) 
            throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == appInstIds) { // Is the input null?
            throw new IllegalArgumentException("appInstIds argument must not be null");
        }
        LOGGER.log(Level.INFO, "runApp received with arguments: {0}", appInstIds);

        // Refresh the list of available Apps
        boolean anyChanges = this.manager.refreshAvailableAppsList(
                connection.getPrimaryConnectionDetails().getProviderURI());

        if (anyChanges) {
            // Update the Configuration available on the COM Archive
            if (this.configurationAdapter != null) {
                this.configurationAdapter.onConfigurationChanged(this);
            }
        }

        for (int index = 0; index < appInstIds.size(); index++) {
            // Get it from the list of available apps:
            AppDetails app = this.manager.get(appInstIds.get(index));

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
            throw new MALInteractionException(
                    new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList)
            );
        }

        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(
                    new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList)
            );
        }

        // Run the apps!
        for (int i = 0; i < appInstIds.size(); i++) {
            try {
                SingleConnectionDetails details;

                if (directoryService.getConnection().getSecondaryConnectionDetails() != null) {
                    // For applications in space, the primary URI is MALSPP, and secondary a MALTCP
                    details = directoryService.getConnection().getSecondaryConnectionDetails();
                } else {
                    details = directoryService.getConnection().getConnectionDetails();
                }
                String directoryServiceURI = details.getProviderURI().toString();
                AppDetails app = this.manager.get(appInstIds.get(i));
                ObjectType objType = AppsLauncherHelper.STARTAPP_OBJECT_TYPE;
                ObjectId eventSource = this.manager.getCOMServices().getActivityTrackingService()
                        .storeCOMOperationActivity(interaction, null);

                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
                        "Generating StartApp event for app: {0} (Name: ''{1}'')", 
                        new Object[]{appInstIds.get(i), app.getName()});
                this.manager.getCOMServices().getEventService().generateAndStoreEvent(
                        objType, ConfigurationProviderSingleton.getDomain(), app.getName(),
                                appInstIds.get(i), eventSource, interaction);

                manager.startAppProcess(new ProcessExecutionHandler(new CallbacksImpl(), 
                        appInstIds.get(i)), interaction, directoryServiceURI);
            } catch (IOException ex) {
                UIntegerList intIndexList = new UIntegerList();
                intIndexList.add(new UInteger(i));
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
                        "Not able to start the application process...", ex);
                throw new MALInteractionException(new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER,
                        intIndexList));
            }
        }
    }

    @Override
    public void killApp(LongList appInstIds, MALInteraction interaction) 
            throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == appInstIds) { // Is the input null?
            throw new IllegalArgumentException("appInstIds argument must not be null");
        }
        LOGGER.log(Level.INFO, "killApp received with arguments: {0}", appInstIds);

        // Refresh the list of available Apps
        boolean anyChanges = this.manager.refreshAvailableAppsList(
                connection.getPrimaryConnectionDetails().getProviderURI());

        if (anyChanges) {
            // Update the Configuration available on the COM Archive
            if (this.configurationAdapter != null) {
                this.configurationAdapter.onConfigurationChanged(this);
            }
        }

        for (int index = 0; index < appInstIds.size(); index++) {
            // Get it from the list of available apps
            AppDetails app = this.manager.get(appInstIds.get(index));

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
            throw new MALInteractionException(
                    new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList)
            );
        }

        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(
                    new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList)
            );
        }

        // Kill the apps!
        for (int i = 0; i < appInstIds.size(); i++) {
            manager.killAppProcess(appInstIds.get(i), interaction);
        }
    }

    @Override
    public void stopApp(final LongList appInstIds, final StopAppInteraction interaction) 
            throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList intIndexList = new UIntegerList();
        ArrayList<SingleConnectionDetails> appConnections = new ArrayList<>();
        if (null == appInstIds) { // Is the input null?
            throw new IllegalArgumentException("appInstIds argument must not be null");
        }
        LOGGER.log(Level.INFO, "stopApp received with arguments: {0}", appInstIds);

        // Refresh the list of available Apps
        boolean anyChanges = this.manager.refreshAvailableAppsList(
                connection.getPrimaryConnectionDetails().getProviderURI());

        if (anyChanges) {
            // Update the Configuration available on the COM Archive
            if (this.configurationAdapter != null) {
                this.configurationAdapter.onConfigurationChanged(this);
            }
        }

        IdentifierList appDirectoryServiceNames = new IdentifierList();

        prepareStopApp(appInstIds, interaction, unkIndexList, invIndexList, 
                intIndexList, appConnections, appDirectoryServiceNames);

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(
                    MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(
                    COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!intIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(
                    MALHelper.INTERNAL_ERROR_NUMBER, intIndexList));
        }

        if(interaction != null) {
            interaction.sendAcknowledgement();
        }
        
        manager.stopApps(appInstIds, appDirectoryServiceNames, appConnections, interaction);
        
        if(interaction != null) {
            interaction.sendResponse();
        }
    }

    private void prepareStopApp(final LongList appInstIds, final StopAppInteraction interaction, UIntegerList unkIndexList,
            UIntegerList invIndexList, UIntegerList intIndexList, ArrayList<SingleConnectionDetails> appConnections,
            IdentifierList appDirectoryServiceNames) throws MALInteractionException, MALException {
        for (int i = 0; i < appInstIds.size(); i++) {
            // Get it from the list of available apps
            Long appId = appInstIds.get(i);
            AppDetails app = this.manager.get(appId);

            if (app == null) {
                // The app id could not be identified
                unkIndexList.add(new UInteger(i)); // Throw an UNKNOWN error
                LOGGER.log(Level.WARNING, "App with id {0} unknown", new Object[]{appId});
                continue;
            } else if (!manager.isAppRunning(appId)) {
                // The app is not running
                invIndexList.add(new UInteger(i)); // Throw an INVALID error
                LOGGER.log(Level.WARNING, "App with id {0} not running", new Object[]{appId});
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
                    null, new Identifier("*"), serviceKey, new UShortList());
            if (app.getCategory().getValue().equalsIgnoreCase("NMF_App")) {
                // Do a lookup on the Central Drectory service for the app that we want
                MALInteraction malInt = (interaction != null) ? interaction.getInteraction() : null;
                ProviderSummaryList providersList = this.directoryService.lookupProvider(sf, malInt);
                LOGGER.log(Level.FINER, "providersList object: {0}", providersList);

                try {
                    // Add here the filtering for the best IPC!!!
                    final SingleConnectionDetails connectionDetails
                            = AppsLauncherManager.getSingleConnectionDetailsFromProviderSummaryList(providersList);
                    appConnections.add(connectionDetails);

                    // Add to the list of Directory service Obj Ids
                    if (!providersList.isEmpty()) {
                        appDirectoryServiceNames.add(providersList.get(0).getProviderId());
                    } else {
                        appDirectoryServiceNames.add(null);
                    }
                } catch (IOException ex) {
                    LOGGER.log(Level.WARNING, "Exception while obtaining connection details", ex);
                    appConnections.add(null);
                    // Ensure the indices of the lists are in-line
                    appDirectoryServiceNames.add(null);
                }
            } else {
                // Ensure the indices of the lists are in-line
                appConnections.add(null);
                appDirectoryServiceNames.add(null);
            }
        }
    }

    @Override
    public ListAppResponse listApp(final IdentifierList appNames, final Identifier category,
            final MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();

        if (null == appNames) { // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        // Refresh the list of available Apps
        boolean anyChanges = manager.refreshAvailableAppsList(
                connection.getPrimaryConnectionDetails().getProviderURI());

        if (anyChanges) {
            // Update the Configuration available on the COM Archive
            if (this.configurationAdapter != null) {
                this.configurationAdapter.onConfigurationChanged(this);
            }
        }

        LongList matchedIds = new LongList();
        BooleanList runningApps = new BooleanList();

        for (int index = 0; index < appNames.size(); index++) {
            if ("*".equals(appNames.get(index).getValue())) {
                // if the wildcard is in one of the entries of the input list, 
                // then we clear the output list and...
                matchedIds.clear();
                matchedIds.addAll(manager.listAll());
                break;
            }

            Long appId = manager.list(appNames.get(index));
            
            if (appId == null) { // The app does not exist...
                unkIndexList.add(new UInteger(index)); // Throw an UNKNOWN error
            }else{
                matchedIds.add(appId);
            }
        }

        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(
                    new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList)
            );
        }

        for (Long id : matchedIds) { // Is the app running?
            runningApps.add(manager.isAppRunning(id));
        }

        return new ListAppResponse(matchedIds, runningApps);
    }

    @Override
    public void setOnConfigurationChangeListener(
            final ConfigurationChangeListener configurationAdapter) {
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

        if (manager.reconfigureDefinitions(confSet.getObjInstIds(), pDefs)) {
            for (Long id : confSet.getObjInstIds()) { // Set all running state to false
                manager.setRunning(id, false, null);
            }
        } else {
            LOGGER.log(Level.WARNING, "Failed to reconfigure definitions. Ids: {0} pDefs: {1}", new Object[]{confSet.getObjInstIds(), pDefs});
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

    public void refresh() {
        manager.refreshAvailableAppsList(new URI(""));
    }
    
    public void addFolderWithApps(java.io.File folder){
        manager.addFolderWithApps(folder);
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            LOGGER.fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body,
                final Map qosProperties)
                throws MALException {
            LOGGER.warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            LOGGER.fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body,
                final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    private class CallbacksImpl implements ProcessExecutionHandler.Callbacks {

        @Override
        public void flushStdout(Long objId, String data) {
            publishExecutionMonitoring(objId, data, CommandExecutorHelper.STANDARDOUTPUT_OBJECT_TYPE);
        }

        @Override
        public void flushStderr(Long objId, String data) {
            publishExecutionMonitoring(objId, data, CommandExecutorHelper.STANDARDERROR_OBJECT_TYPE);
        }

        @Override
        public void processStopped(Long objId, int exitCode) {
            manager.setRunning(objId, false, null);
        }
    }

}
