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

import esa.mo.com.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.Quota;
import esa.mo.helpertools.misc.Const;
import esa.mo.helpertools.misc.OSValidator;
import esa.mo.nmf.environment.AppsIsolationMode;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;
import esa.mo.reconfigurable.service.ReconfigurableService;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.InternalException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.sm.appslauncher.provider.AppsLauncherInheritanceSkeleton;
import org.ccsds.moims.mo.sm.appslauncher.provider.MonitorEventsPublisher;
import org.ccsds.moims.mo.sm.appslauncher.provider.MonitorExecutionPublisher;
import org.ccsds.moims.mo.sm.appslauncher.provider.StopAppInteraction;
import org.ccsds.moims.mo.sm.structures.AppDetails;
import org.ccsds.moims.mo.sm.structures.AppEventType;
import org.ccsds.moims.mo.sm.structures.AppStarted;
import org.ccsds.moims.mo.sm.structures.AppStopped;

/**
 * Apps Launcher service Provider.
 */
public class AppsLauncherProviderServiceImpl extends AppsLauncherInheritanceSkeleton implements ReconfigurableService {

    private static final Logger LOGGER = Logger.getLogger(
            AppsLauncherProviderServiceImpl.class.getName());
    // Maximum length of a stderr/stdout chunk to be persisted - allows downlinking it via SPP without issues
    private static final int MAX_SEGMENT_SIZE = UShort.MAX_VALUE - 256;
    private MALProvider appsLauncherServiceProvider;
    private MonitorExecutionPublisher publisher;
    private MonitorEventsPublisher eventsPublisher;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private boolean isEventsRegistered = false;
    private final Object lock = new Object();
    private final Set<Long> killPendingApps = Collections.synchronizedSet(new HashSet<>());
    private final Set<Long> stopPendingApps = Collections.synchronizedSet(new HashSet<>());
    private AppsLauncherManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private COMServicesProvider comServices;
    private DirectoryProviderServiceImpl directoryService;
    private ConfigurationChangeListener configurationAdapter;
    private int stdLimit; // Limit of stdout/stderr to allow in the archive.
    /**
     * Object used to track archive usage by STD output of each app
     */
    private Quota stdQuota = new Quota();

    /**
     * Initializes the Apps Launcher service provider.
     *
     * @param comServices The COM services.
     * @param directoryService The central Directory service.
     * @throws MALException If the service could not be initialized.
     */
    public synchronized void init(final COMServicesProvider comServices,
            final DirectoryProviderServiceImpl directoryService) throws MALException {
        long timestamp = System.currentTimeMillis();

        int kbyte = Integer.parseInt(System.getProperty(Const.APPSLAUNCHER_STD_LIMIT_PROPERTY,
                Const.APPSLAUNCHER_STD_LIMIT_DEFAULT));
        stdLimit = kbyte * 1024; // init limit with value of property
        publisher = createMonitorExecutionPublisher(ConfigurationProviderSingleton.getDomain(),
                null,
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));
        eventsPublisher = createMonitorEventsPublisher(ConfigurationProviderSingleton.getDomain(),
                null,
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
        OSValidator osValidator = new OSValidator();
        if (osValidator.isWindows()) {
            manager = new AppsLauncherManagerWindows(comServices);
        } else if (AppsIsolationMode.isBubblewrap()) {
            manager = new AppsLauncherManagerBubblewrap(comServices);
        } else {
            manager = new AppsLauncherManagerLinux(comServices);
        }
        appsLauncherServiceProvider = connection.startService(
                AppsLauncherHelper.APPSLAUNCHER_SERVICE, true, this);
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

    private void storeAppStarted(Long appId, MALInteraction interaction) {
        if (this.manager.getCOMServices().getArchiveService() == null) {
            return;
        }
        try {
            URI triggeredBy = (interaction != null)
                    ? interaction.getMessageHeader().getFromURI() : null;
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new AppStarted(triggeredBy));
            ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(
                    appId, null, connection.getPrimaryConnectionDetails().getProviderURI());
            this.manager.getCOMServices().getArchiveService().store(
                    true, AppsLauncherServiceInfo.APPSTARTED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(), archDetails, bodies, null);
        } catch (org.ccsds.moims.mo.com.DuplicateException | org.ccsds.moims.mo.com.InvalidArgumentException | MALException | MALInteractionException ex) {
            LOGGER.log(Level.WARNING, "Could not store AppStarted in archive", ex);
        }
    }

    private void storeAppStopped(Long appId, AppEventType stopReason, int exitCode) {
        if (this.manager.getCOMServices().getArchiveService() == null) {
            return;
        }
        try {
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(new AppStopped(stopReason, exitCode, null));
            ArchiveDetailsList archDetails = HelperArchive.generateArchiveDetailsList(
                    appId, null, connection.getPrimaryConnectionDetails().getProviderURI());
            this.manager.getCOMServices().getArchiveService().store(
                    true, AppsLauncherServiceInfo.APPSTOPPED_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(), archDetails, bodies, null);
        } catch (org.ccsds.moims.mo.com.DuplicateException | org.ccsds.moims.mo.com.InvalidArgumentException | MALException | MALInteractionException ex) {
            LOGGER.log(Level.WARNING, "Could not store AppStopped in archive", ex);
        }
    }

    private void publishAppEvent(String appName, Long appId, AppEventType eventType, Integer exitCode, String extraInfo) {
        try {
            synchronized (lock) {
                if (!isEventsRegistered) {
                    eventsPublisher.registerWithDefaultKeys(new PublishInteractionListener());
                    isEventsRegistered = true;
                }
            }
            AttributeList keyValues = new AttributeList();
            keyValues.add(new Identifier(appName));
            keyValues.add(new Union(appId));
            UpdateHeader updateHeader = new UpdateHeader(
                    new Identifier(connection.getConnectionDetails().getProviderURI().getValue()),
                    connection.getConnectionDetails().getDomain(),
                    keyValues.getAsNullableAttributeList());
            eventsPublisher.publish(updateHeader, eventType, exitCode, extraInfo);
        } catch (IllegalArgumentException | MALException | MALInteractionException ex) {
            LOGGER.log(Level.WARNING, "Exception publishing app event", ex);
        }
    }

    private void publishExecutionMonitoring(final Long appObjId, final String outputText) {
        try {
            synchronized (lock) {
                if (!isRegistered) {
                    publisher.registerWithDefaultKeys(new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            String appName = manager.get(appObjId).getName().toString();

            LOGGER.log(Level.FINER,
                    "Generating update for the App: {0} (Identifier: {1})",
                    new Object[]{appObjId, new Identifier(appName)});

            String outputList = new String();

            AttributeList keyValues = new AttributeList();
            keyValues.add(new Identifier(appName));
            keyValues.add(new Union(appObjId));

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            URI sourceURI = connection.getConnectionDetails().getProviderURI();
            UpdateHeader updateHeader = new UpdateHeader(new Identifier(sourceURI.getValue()),
                    connection.getConnectionDetails().getDomain(), keyValues.getAsNullableAttributeList());

            int length = outputText.length();
            for (int i = 0; i < length; i += MAX_SEGMENT_SIZE) {
                int end = Math.min(length, i + MAX_SEGMENT_SIZE);
                String segment = outputText.substring(i, end);
                outputList = outputList + segment;
                boolean storeInArchive = Boolean.valueOf(System.getProperty(Const.APPSLAUNCHER_STD_STORE_PROPERTY,
                        Const.APPSLAUNCHER_STD_STORE_DEFAULT));

                if (storeInArchive) {
                    // Store in COM archive if the option is enabled and below limit
                    int currentStd = stdQuota.retrieve(appObjId);

                    if (currentStd + segment.length() <= stdLimit) {
                        Element eventBody = new Union(segment);
                        stdQuota.increase(appObjId, segment.length());
                    } else {
                        String errorString
                                = "Your logging is too verbose and reached the limit.\nPlease reduce verbosity.";
                        outputList = outputList + errorString;
                    }
                }
            }

            publisher.publish(updateHeader, outputList);
        } catch (IllegalArgumentException | MALException | MALInteractionException ex) {
            LOGGER.log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public void runApp(LongList appInstIds, MALInteraction interaction) throws UnknownException, InvalidArgumentException, InternalException, MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (appInstIds == null) { // Is the input null?
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
            throw new InvalidArgumentException(invIndexList);
        }

        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        // Run the apps!
        for (int i = 0; i < appInstIds.size(); i++) {
            try {
                SingleConnectionDetails details;
                ConnectionProvider con = directoryService.getConnection();

                if (con.getSecondaryConnectionDetails() != null) {
                    // For applications in space, the primary URI is MALSPP, and secondary a MALTCP
                    details = con.getSecondaryConnectionDetails();
                } else {
                    details = con.getConnectionDetails();
                }
                String directoryServiceURI = details.getProviderURI().toString();
                Long appId = appInstIds.get(i);
                AppDetails app = this.manager.get(appId);
                publishAppEvent(app.getName().toString(), appId, AppEventType.START_REQUESTED, null, null);
                CallbacksImpl calback = new CallbacksImpl();
                ProcessExecutionHandler pHandler = new ProcessExecutionHandler(calback, appId);
                manager.startAppProcess(pHandler, interaction, directoryServiceURI);
                publishAppEvent(app.getName().toString(), appId, AppEventType.STARTED, null, null);
                storeAppStarted(appId, interaction);
            } catch (IOException ex) {
                UIntegerList intIndexList = new UIntegerList();
                intIndexList.add(new UInteger(i));
                Logger.getLogger(AppsLauncherManager.class.getName()).log(Level.INFO,
                        "Not able to start the application process...", ex);
                throw new InternalException(intIndexList);
            }
        }
    }

    @Override
    public void killApp(LongList appInstIds, MALInteraction interaction)
            throws UnknownException, InvalidArgumentException, MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (appInstIds == null) { // Is the input null?
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
            throw new InvalidArgumentException(invIndexList);
        }

        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        // Kill the apps!
        for (int i = 0; i < appInstIds.size(); i++) {
            killPendingApps.add(appInstIds.get(i));
            manager.killAppProcess(appInstIds.get(i), interaction);
        }
    }

    @Override
    public void stopApp(final LongList appInstIds, final Duration timeout, final StopAppInteraction interaction)
            throws UnknownException, InvalidArgumentException, MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (appInstIds == null) { // Is the input null?
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

        for (int i = 0; i < appInstIds.size(); i++) {
            Long appId = appInstIds.get(i);
            AppDetails app = this.manager.get(appId);

            if (app == null) {
                unkIndexList.add(new UInteger(i));
                LOGGER.log(Level.WARNING, "App with id {0} unknown", new Object[]{appId});
            } else if (!manager.isAppRunning(appId)) {
                invIndexList.add(new UInteger(i));
                LOGGER.log(Level.WARNING, "App with id {0} not running", new Object[]{appId});
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        if (!invIndexList.isEmpty()) {
            throw new InvalidArgumentException(invIndexList);
        }

        if (interaction != null) {
            interaction.sendAcknowledgement();
        }

        for (int i = 0; i < appInstIds.size(); i++) {
            Long appId = appInstIds.get(i);
            String name = this.manager.get(appId).getName().toString();
            stopPendingApps.add(appId);
            LOGGER.log(Level.INFO,
                    "Publishing STOP_REQUESTED monitorEvents notification for app ''{0}'' (id={1}).",
                    new Object[]{name, appId});
            publishAppEvent(name, appId, AppEventType.STOP_REQUESTED, null, null);
        }

        final MALInteraction malInt = (interaction != null) ? interaction.getInteraction() : null;
        manager.stopApps(appInstIds, timeout, interaction, appId -> forceKillAfterTimeout(appId, malInt));

        if (interaction != null) {
            interaction.sendResponse();
        }
    }

    /**
     * Force-kills an app whose stopApp grace period expired. Moves it from the
     * stop-pending set to the kill-pending set so that, when the process exits,
     * {@code processStopped} classifies it as KILLED rather than STOPPED.
     *
     * @param appId the app instance id.
     * @param interaction the originating interaction, may be null.
     */
    private void forceKillAfterTimeout(final Long appId, final MALInteraction interaction) {
        stopPendingApps.remove(appId);
        killPendingApps.add(appId);
        manager.killAppProcess(appId, interaction);
    }

    @Override
    public ListAppResponse listApp(final IdentifierList appNames, final Identifier category,
            final MALInteraction interaction) throws UnknownException, MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();

        if (appNames == null) { // Is the input null?
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
            } else {
                matchedIds.add(appId);
            }
        }

        if (!unkIndexList.isEmpty()) {
            throw new UnknownException(unkIndexList);
        }

        for (Long id : matchedIds) { // Is the app running?
            runningApps.add(manager.isAppRunning(id));
        }

        return new ListAppResponse(matchedIds, runningApps);
    }

    @Override
    public void setOnConfigurationChangeListener(final ConfigurationChangeListener configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override
    public Boolean reloadConfiguration(ObjectKeysList configurationObjectDetails) {
        // Validate the configuration...
        if (configurationObjectDetails == null) {
            return false;
        }

        if (configurationObjectDetails == null) {
            return false;
        }

        // Is the size 1?
        // 1 because we just have Apps as configuration objects in this service
        if (configurationObjectDetails.size() != 1) {
            return false;
        }

        ObjectKeys confSet = configurationObjectDetails.get(0);

        // Confirm the objType
        if (!confSet.getObjType().equals(AppsLauncherServiceInfo.APPDETAILS_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if (confSet.getIds().isEmpty()) {
            manager.reconfigureDefinitions(new LongList(), new HeterogeneousList());   // Reconfigures the Manager
            return true;
        }

        // ok, we're good to go...
        // Load the App Details from this configuration...
        HeterogeneousList pDefs = (HeterogeneousList) HelperArchive.getObjectBodyListFromArchive(manager.getArchiveService(),
                AppsLauncherServiceInfo.APPDETAILS_OBJECT_TYPE, ConfigurationProviderSingleton.getDomain(), confSet.getIds());

        if (manager.reconfigureDefinitions(confSet.getIds(), pDefs)) {
            for (Long id : confSet.getIds()) { // Set all running state to false
                manager.setRunning(id, false, null);
            }
        } else {
            LOGGER.log(Level.WARNING,
                    "Failed to reconfigure definitions. Ids: {0} pDefs: {1}",
                    new Object[]{confSet.getIds(), pDefs});
        }
        return true;
    }

    @Override
    public ObjectKeysList getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        final HashMap<Long, Element> defObjs = manager.getCurrentDefinitionsConfiguration();

        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());

        final ObjectKeys objsSet = new ObjectKeys(AppsLauncherServiceInfo.APPDETAILS_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), currentObjIds);

        final ObjectKeysList list = new ObjectKeysList();
        list.add(objsSet);

        // Needs the Common API here!
        return new ObjectKeysList(list);
    }

    @Override
    public COMService getCOMService() {
        return AppsLauncherHelper.APPSLAUNCHER_SERVICE;
    }

    public void refresh() {
        manager.refreshAvailableAppsList(new URI(""));
    }

    public void addFolderWithApps(java.io.File folder) {
        manager.addFolderWithApps(folder);
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            LOGGER.fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            LOGGER.fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    private class CallbacksImpl implements ProcessExecutionHandler.Callbacks {

        @Override
        public void flushStdout(Long objId, String data) {
            publishExecutionMonitoring(objId, data);
        }

        @Override
        public void flushStderr(Long objId, String data) {
            publishExecutionMonitoring(objId, data);
        }

        @Override
        public void processStopped(Long objId, int exitCode) {
            LOGGER.log(Level.INFO, "The process exited with code {0} and objId: {1}",
                    new Object[]{exitCode, objId});
            String appName = manager.get(objId).getName().toString();
            AppEventType stopReason;
            if (killPendingApps.remove(objId)) {
                stopReason = AppEventType.KILLED;
            } else if (stopPendingApps.remove(objId)) {
                stopReason = AppEventType.STOPPED;
            } else if (exitCode == 0) {
                stopReason = AppEventType.EXITED;
            } else {
                stopReason = AppEventType.CRASHED;
            }
            LOGGER.log(Level.INFO,
                    "App ''{0}'' (id={1}) process exited with code {2}, classified as {3}.",
                    new Object[]{appName, objId, exitCode, stopReason});
            publishAppEvent(appName, objId, stopReason, exitCode, null);
            storeAppStopped(objId, stopReason, exitCode);
            manager.setRunning(objId, false, null);
        }
    }

}
