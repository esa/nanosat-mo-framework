/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.testbed.e2e;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherStub;
import org.ccsds.moims.mo.sm.appslauncher.consumer.MonitorEventsSubscriptionKeys;
import org.ccsds.moims.mo.sm.appslauncher.consumer.MonitorExecutionSubscriptionKeys;
import org.ccsds.moims.mo.sm.structures.AppEventType;
import org.ccsds.moims.mo.mc.action.consumer.ActionAdapter;
import org.ccsds.moims.mo.mc.action.consumer.ActionStub;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationStub;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.structures.AggregationValueDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ExecutionRequest;
import org.ccsds.moims.mo.mc.structures.ExecutionStageType;
import org.ccsds.moims.mo.mc.structures.ParameterValueDetailsList;

/**
 * Manages the lifecycle of a named NMF App for end-to-end tests.
 *
 * <p>
 * Requires a running Supervisor; construct with the same
 * {@link SupervisorHarness} instance used by the test class and call
 * {@link SupervisorHarness#setUp()} before calling {@link #setUp()} here.
 *
 * @author Cesar Coelho
 */
public class AppHarness {

    private static final Logger LOGGER = Logger.getLogger(AppHarness.class.getName());
    private static final int STARTUP_TIMEOUT_SECONDS = 10;
    private static final String WILDCARD = "*";

    private final String appName;
    private final SupervisorHarness supervisorHarness;

    private GroundMOAdapterImpl adapter;
    private AppsLauncherStub stub;
    private Long appId;
    private GroundMOAdapterImpl appMcAdapter;

    /**
     * Creates a harness for the named app.
     *
     * @param appName the NMF app name as known to the AppsLauncher.
     * @param supervisorHarness the already-constructed supervisor harness.
     */
    public AppHarness(String appName, SupervisorHarness supervisorHarness) {
        this.appName = appName;
        this.supervisorHarness = supervisorHarness;
    }

    /**
     * Requests the Supervisor to start the app and waits until it has
     * registered itself in the Directory service.
     *
     * <p>
     * Checking the Directory (rather than the AppsLauncher running flag) is the
     * definitive proof of a successful start: the supervisor sets the running
     * flag as soon as it spawns the process, before the JVM loads any classes.
     * Registration with the Directory only happens inside
     * {@code NanoSatMOConnectorImpl.init()}, so a JVM that crashes on startup
     * (e.g. {@code NoClassDefFoundError}) will never appear there.
     *
     * @throws IOException if the app could not be started or did not register
     * within the timeout.
     */
    public void setUp() throws IOException {
        connect();
        runApp();
    }

    /**
     * Connects to the Supervisor's AppsLauncher service and resolves this app's
     * COM object id, without starting the app. Safe to call before
     * {@link #runApp()} so a consumer can subscribe to monitorEvents in time to
     * observe the START_REQUESTED / STARTED notifications.
     *
     * @throws IOException if the Supervisor could not be reached or the app is
     * not registered.
     */
    public void connect() throws IOException {
        String directoryURIStr = supervisorHarness.getDirectoryURI();
        LOGGER.info("Connecting to Directory service at: " + directoryURIStr);

        try {
            ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                    new URI(directoryURIStr));

            Provider supervisorProvider = findSupervisorProvider(providers);
            if (supervisorProvider == null) {
                throw new IOException("No Supervisor provider found at " + directoryURIStr);
            }

            adapter = new GroundMOAdapterImpl(supervisorProvider);
            stub = adapter.getSMServices().getAppsLauncherService().getAppsLauncherStub();

            appId = resolveAppId();
            LOGGER.info("Found app '" + appName + "' with id=" + appId);
        } catch (MALException | MALInteractionException | java.net.MalformedURLException e) {
            throw new IOException("Failed to connect for app '" + appName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Requests the Supervisor to start the app and waits until it has
     * registered itself in the Directory service. Connects first if not
     * already connected.
     *
     * <p>
     * Checking the Directory (rather than the AppsLauncher running flag) is the
     * definitive proof of a successful start: the supervisor sets the running
     * flag as soon as it spawns the process, before the JVM loads any classes.
     * Registration with the Directory only happens inside
     * {@code NanoSatMOConnectorImpl.init()}, so a JVM that crashes on startup
     * (e.g. {@code NoClassDefFoundError}) will never appear there.
     *
     * @throws IOException if the app could not be started or did not register
     * within the timeout.
     */
    public void runApp() throws IOException {
        if (stub == null || appId == null) {
            connect();
        }

        CountDownLatch uriLatch = new CountDownLatch(1);
        AtomicReference<String> foundURI = new AtomicReference<>();
        Subscription sub = ConnectionConsumer.subscriptionWildcardRandom();

        try {
            // Subscribe to monitorExecution BEFORE launching. The Supervisor's
            // ProcessExecutionHandler reads the app's stdout and re-publishes it
            // here every second, so this is the direct equivalent of reading
            // process.getInputStream() — immune to stale Directory entries left
            // by previously killed instances.
            stub.monitorExecutionRegister(sub, new AppsLauncherAdapter() {
                @Override
                public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader,
                        Identifier subscriptionId, UpdateHeader updateHeader,
                        MonitorExecutionSubscriptionKeys keys,
                        String outputStream, java.util.Map qosProperties) {
                    Identifier receivedAppName = keys.getAppName();
                    if (receivedAppName == null) {
                        return;
                    }
                    if (!appName.equals(receivedAppName.getValue())) {
                        return;
                    }
                    for (String line : outputStream.split("\\R", -1)) {
                        String uri = LogScanner.extractDirectoryURI(line);
                        if (uri != null) {
                            foundURI.set(uri);
                            uriLatch.countDown();
                            return;
                        }
                    }
                }
            });

            LongList ids = new LongList();
            ids.add(appId);
            stub.runApp(ids);
            LOGGER.info("runApp('" + appName + "') submitted.");
        } catch (MALException | MALInteractionException e) {
            throw new IOException("Failed to start app '" + appName + "': " + e.getMessage(), e);
        }

        waitUntilRunning(uriLatch, foundURI, sub);
        LOGGER.info("App '" + appName + "' is running.");
    }

    /**
     * Requests the Supervisor to start the app, same as setUp but usable
     * within individual test methods.
     *
     * @throws IOException if the app could not be started.
     */
    public void start() throws IOException {
        setUp();
    }

    /**
     * Polls {@link #isProcessGone()} until it returns true or the timeout
     * expires. After a kill, the parent shell exits (and the KILLED event
     * fires) slightly before the JVM descendant finishes its forced shutdown,
     * so an instantaneous check can race; this gives the process tree time to
     * be fully reaped.
     *
     * @param timeoutMs maximum time to wait in milliseconds.
     * @return true if the process became gone within the timeout.
     */
    public boolean waitProcessGone(long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            if (isProcessGone()) {
                return true;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        if (!isProcessGone()) {
            LOGGER.warning("waitProcessGone timed out for app '" + appName
                    + "' — process still alive after " + timeoutMs + " ms."
                    + "\nApp log:"
                    + "\n" + readAppLog());
        }
        return isProcessGone();
    }

    /**
     * Connects directly to this app's own provider in the Directory and invokes
     * one of its MC actions. Used to drive app-initiated behaviour such as
     * self-termination. The app must already be running and registered.
     *
     * @param actionName the action name as registered by the app.
     * @param args the action arguments.
     * @throws IOException if the app provider could not be found or reached.
     */
    public void launchAppAction(String actionName, java.io.Serializable... args) throws IOException {
        ActionStub actionStub = appMcAdapter().getMCServices().getActionService().getActionStub();
        try {
            // Resolve the definition id directly against the app's Action
            // service, tolerating the registration window (see
            // resolveActionDefId). Routing through GroundMOAdapterImpl.launchAction
            // instead would swallow a failed resolution and return silently, so a
            // never-dispatched action only surfaced 10 s later as an unrelated
            // "process still alive" timeout. Here it fails fast with the real cause.
            Long defId = resolveActionDefId(actionStub, actionName, 10_000);

            AttributeValueList argValues = new AttributeValueList();
            for (java.io.Serializable arg : args) {
                argValues.add(new AttributeValue((Attribute) Attribute.javaType2Attribute(arg)));
            }

            actionStub.executeAction(new ExecutionRequest(defId, argValues, null));
            LOGGER.info("launchAction('" + actionName + "') on app '" + appName + "' submitted.");
        } catch (MALException | MALInteractionException e) {
            throw new IOException("Failed to invoke action '" + actionName + "' on app '"
                    + appName + "': " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while resolving action '" + actionName + "'", e);
        }
    }

    /**
     * Reads the latest values of the named parameters directly from this app's
     * own Parameter service. Used to observe retrieval that fails (a broken
     * parameter comes back with validity INVALID_RAW rather than an error).
     *
     * @param names the parameter names as registered by the app.
     * @return the returned ParameterValueDetails, in the same order as the ids.
     * @throws IOException if the app provider could not be reached.
     */
    public ParameterValueDetailsList getParameterValues(List<String> names) throws IOException {
        try {
            ParameterStub parameterStub = appMcAdapter().getMCServices()
                    .getParameterService().getParameterStub();
            IdentifierList paramNames = new IdentifierList();
            names.forEach(n -> paramNames.add(new Identifier(n)));
            LongList ids = parameterStub.listDefinition(paramNames);
            return parameterStub.getValue(ids);
        } catch (MALException | MALInteractionException e) {
            throw new IOException("getValue (parameters) failed for app '" + appName + "': "
                    + e.getMessage(), e);
        }
    }

    /**
     * Reads the current values of the named aggregations directly from this
     * app's own Aggregation service. A broken aggregation samples fresh, so the
     * broken parameter it contains comes back with validity INVALID_RAW.
     *
     * @param names the aggregation names as registered by the app.
     * @return the returned AggregationValueDetails, in the same order as the ids.
     * @throws IOException if the app provider could not be reached.
     */
    public AggregationValueDetailsList getAggregationValues(List<String> names) throws IOException {
        try {
            AggregationStub aggregationStub = appMcAdapter().getMCServices()
                    .getAggregationService().getAggregationStub();
            IdentifierList aggNames = new IdentifierList();
            names.forEach(n -> aggNames.add(new Identifier(n)));
            LongList ids = aggregationStub.listDefinition(aggNames);
            return aggregationStub.getValue(ids);
        } catch (MALException | MALInteractionException e) {
            throw new IOException("getValue (aggregations) failed for app '" + appName + "': "
                    + e.getMessage(), e);
        }
    }

    /**
     * Executes one of the app's actions and waits for the END stage of its
     * monitorExecution report, returning the reported success flag. A broken
     * action reports the END stage with success {@code false}.
     *
     * @param actionName the action name as registered by the app.
     * @param timeoutMs maximum time to wait for the END stage in milliseconds.
     * @return the success flag of the END stage, or {@code null} if no END stage
     * was received within the timeout.
     * @throws IOException if the app provider could not be reached or the action
     * is unknown.
     */
    public Boolean awaitActionOutcome(String actionName, long timeoutMs) throws IOException {
        ActionStub actionStub = appMcAdapter().getMCServices().getActionService().getActionStub();

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> outcome = new AtomicReference<>();
        Subscription sub = ConnectionConsumer.subscriptionWildcardRandom();
        try {
            final Long defId = resolveActionDefId(actionStub, actionName, timeoutMs);

            actionStub.monitorExecutionRegister(sub, new ActionAdapter() {
                @Override
                public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader,
                        Identifier subscriptionId, UpdateHeader updateHeader,
                        org.ccsds.moims.mo.mc.action.consumer.MonitorExecutionSubscriptionKeys keys,
                        Boolean success, UShort step, String comment, java.util.Map qosProperties) {
                    if (defId.equals(keys.getDefinitionId())
                            && ExecutionStageType.END.equals(keys.getStageType())) {
                        outcome.set(success);
                        latch.countDown();
                    }
                }
            });

            // Give the subscription time to register before triggering the
            // action; a 0-stage action publishes START/END almost immediately,
            // so firing too early would miss the END notification.
            Thread.sleep(300);

            actionStub.executeAction(new ExecutionRequest(defId, new AttributeValueList(), null));

            latch.await(timeoutMs, TimeUnit.MILLISECONDS);
            return outcome.get();
        } catch (MALException | MALInteractionException e) {
            throw new IOException("executeAction/monitorExecution failed for app '" + appName + "': "
                    + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for the action outcome", e);
        } finally {
            IdentifierList subIds = new IdentifierList();
            subIds.add(sub.getSubscriptionId());
            try {
                actionStub.monitorExecutionDeregister(subIds);
            } catch (MALException | MALInteractionException ignored) {
            }
        }
    }

    /**
     * Resolves an action's definition id, tolerating the window in which the app
     * is already registered in the Directory but has not yet completed its
     * {@code initialRegistrations()}. During that window {@code listDefinition}
     * reports the name as UNKNOWN; this polls until the definition appears or
     * {@code timeoutMs} elapses. Any non-UNKNOWN interaction error is a genuine
     * failure and is rethrown immediately.
     */
    private Long resolveActionDefId(ActionStub actionStub, String actionName, long timeoutMs)
            throws MALException, MALInteractionException, InterruptedException, IOException {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(actionName));
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (true) {
            try {
                LongList ids = actionStub.listDefinition(names);
                if (ids != null && !ids.isEmpty() && ids.get(0) != null && ids.get(0) != 0L) {
                    return ids.get(0);
                }
                // Empty/zero result: definition not registered yet, keep polling.
            } catch (MALInteractionException e) {
                if (!MALHelper.UNKNOWN_ERROR_NUMBER.equals(e.getStandardError().getErrorNumber())) {
                    throw e;
                }
                // UNKNOWN: the action is not registered yet, keep polling.
            }
            if (System.currentTimeMillis() >= deadline) {
                throw new IOException("Action '" + actionName + "' not registered on app '"
                        + appName + "' within " + timeoutMs + " ms.");
            }
            Thread.sleep(200);
        }
    }

    /**
     * Lazily connects to this app's own provider in the Directory and caches a
     * {@link GroundMOAdapterImpl} for consuming its MC services. Reset by
     * {@link #tearDown()}.
     */
    private GroundMOAdapterImpl appMcAdapter() throws IOException {
        if (appMcAdapter == null) {
            appMcAdapter = new GroundMOAdapterImpl(findAppProvider());
        }
        return appMcAdapter;
    }

    /**
     * Resolves this app's own provider entry in the Central Directory.
     */
    private Provider findAppProvider() throws IOException {
        try {
            ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                    new URI(supervisorHarness.getDirectoryURI()));
            for (Provider p : providers) {
                if (NMFProviderType.APP.equals(p.getProviderType())
                        && p.getProviderName() != null
                        && appName.equals(p.getProviderName().getValue())) {
                    return p;
                }
            }
            throw new IOException("App provider '" + appName + "' not found in Directory.");
        } catch (MALException | MALInteractionException | java.net.MalformedURLException e) {
            throw new IOException("Failed to resolve provider for app '" + appName + "': "
                    + e.getMessage(), e);
        }
    }

    /**
     * Sends a gentle stopApp (no force-kill) and blocks until the PROGRESS
     * RESPONSE is received or the timeout expires.
     *
     * @param timeoutMs maximum time to wait in milliseconds.
     * @return true if the app stopped cleanly (UPDATE received, not an error);
     * false if an update error was received or the timeout expired.
     * @throws IOException if the call itself fails.
     */
    public boolean stop(long timeoutMs) throws IOException {
        return stop(timeoutMs, null);
    }

    /**
     * Sends stopApp with an explicit grace period and blocks until the PROGRESS
     * RESPONSE is received or the timeout expires.
     *
     * @param timeoutMs maximum time to wait for the consumer-side UPDATE.
     * @param graceTimeout the grace period the provider allows before forcibly
     * killing the app, or {@code null} to never force-kill.
     * @return true if the app stopped cleanly (UPDATE received, not an error);
     * false if an update error was received or the timeout expired.
     * @throws IOException if the call itself fails.
     */
    public boolean stop(long timeoutMs, Duration graceTimeout) throws IOException {
        if (stub == null || appId == null) {
            return false;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean updateError = new AtomicBoolean(false);
        LongList ids = new LongList();
        ids.add(appId);
        try {
            stub.stopApp(ids, graceTimeout, new AppsLauncherAdapter() {
                @Override
                public void stopAppUpdateReceived(MALMessageHeader msgHeader,
                        Long appClosing, java.util.Map qosProperties) {
                    latch.countDown();
                }

                @Override
                public void stopAppAckErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    updateError.set(true);
                    latch.countDown();
                }

                @Override
                public void stopAppUpdateErrorReceived(MALMessageHeader msgHeader,
                        MOErrorException error, java.util.Map qosProperties) {
                    updateError.set(true);
                    latch.countDown();
                }
            });
        } catch (MALException | MALInteractionException e) {
            throw new IOException("stopApp call failed: " + e.getMessage(), e);
        }
        try {
            latch.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for stopApp", e);
        }
        return !updateError.get() && latch.getCount() == 0;
    }

    /**
     * Sends killApp (SUBMIT — fire and forget with ack).
     *
     * @throws IOException if the call fails.
     */
    public void kill() throws IOException {
        if (stub == null || appId == null) {
            return;
        }
        try {
            LongList ids = new LongList();
            ids.add(appId);
            stub.killApp(ids);
        } catch (MALException | MALInteractionException e) {
            throw new IOException("killApp call failed: " + e.getMessage(), e);
        }
    }

    /**
     * Subscribes to monitorEvents and collects notifications for this app
     * until {@code count} events have been received or {@code timeoutMs}
     * elapses.
     *
     * @param timeoutMs maximum time to wait in milliseconds.
     * @param count number of events to collect before returning early.
     * @return the collected AppEventType values (may be fewer than count on
     * timeout).
     * @throws IOException if the subscription fails.
     */
    public List<AppEventType> waitForMonitorEvents(long timeoutMs, int count) throws IOException {
        List<AppEventType> collected = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(count);
        Subscription sub = ConnectionConsumer.subscriptionWildcardRandom();
        try {
            stub.monitorEventsRegister(sub, new AppsLauncherAdapter() {
                @Override
                public void monitorEventsNotifyReceived(MALMessageHeader msgHeader,
                        Identifier subscriptionId, UpdateHeader updateHeader,
                        MonitorEventsSubscriptionKeys keys,
                        AppEventType eventType, Integer exitCode, String extraInfo,
                        java.util.Map qosProperties) {
                    Identifier receivedAppName = keys.getAppName();
                    if (receivedAppName == null) {
                        return;
                    }
                    if (appName.equals(receivedAppName.getValue())) {
                        collected.add(eventType);
                        latch.countDown();
                    }
                }
            });
        } catch (MALException | MALInteractionException e) {
            throw new IOException("monitorEventsRegister failed: " + e.getMessage(), e);
        }
        try {
            latch.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            IdentifierList subIds = new IdentifierList();
            subIds.add(sub.getSubscriptionId());
            try {
                stub.monitorEventsDeregister(subIds);
            } catch (MALException | MALInteractionException ignored) {
            }
        }
        return new ArrayList<>(collected);
    }

    /**
     * Returns true if no OS process with this app's name is detectable via
     * ProcessHandle.
     *
     * @return true if the process is gone.
     */
    public boolean isProcessGone() {
        return ProcessHandle.allProcesses()
                .noneMatch(ph -> ph.info().commandLine()
                .map(cmd -> cmd.contains(appName))
                .orElse(false));
    }

    /**
     * Queries the archive for AppStarted COM objects whose related link points
     * to this app's AppDetails object.
     *
     * @return list of matching archive objects, never null.
     * @throws IOException if the query fails.
     */
    public List<ArchivePersistenceObject> queryAppStarted() throws IOException {
        return new AppArchiveQueries(adapter, appId).queryAppStarted();
    }

    /**
     * Queries the archive for AppStopped COM objects whose related link points
     * to this app's AppDetails object.
     *
     * @return list of matching archive objects, never null.
     * @throws IOException if the query fails.
     */
    public List<ArchivePersistenceObject> queryAppStopped() throws IOException {
        return new AppArchiveQueries(adapter, appId).queryAppStopped();
    }

    /**
     * Requests the Supervisor to stop the app.
     *
     * @throws IOException if the app could not be stopped.
     */
    public void tearDown() throws IOException {
        if (stub != null && appId != null) {
            try {
                LongList ids = new LongList();
                ids.add(appId);
                stub.stopApp(ids, null, new AppsLauncherAdapter() {
                });
                LOGGER.info("stopApp('" + appName + "') submitted.");
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.WARNING, "Error stopping app '" + appName + "': " + e.getMessage(), e);
            }
        }
        if (appMcAdapter != null) {
            appMcAdapter.closeConnections();
            appMcAdapter = null;
        }
        if (adapter != null) {
            adapter.closeConnections();
            adapter = null;
        }
        stub = null;
        appId = null;
    }

    /**
     * Returns the app name as registered with the Supervisor.
     *
     * @return the app name.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Returns the COM object instance ID of the app, available after
     * {@link #setUp()}.
     *
     * @return the app's object instance ID, or {@code null} if setUp has not
     * been called.
     */
    public Long getAppId() {
        return appId;
    }

    /**
     * Returns {@code true} if the app is currently reported as running by the
     * AppsLauncher.
     *
     * @return true if running.
     * @throws IOException if the query fails.
     */
    public boolean isRunning() throws IOException {
        if (stub == null || appId == null) {
            return false;
        }
        try {
            IdentifierList names = new IdentifierList();
            names.add(new Identifier(appName));
            ListAppResponse response = stub.listApp(names, new Identifier(WILDCARD));
            if (response.getRunning() != null && !response.getRunning().isEmpty()) {
                return Boolean.TRUE.equals(response.getRunning().get(0));
            }
            return false;
        } catch (MALException | MALInteractionException e) {
            throw new IOException("Failed to query app running state: " + e.getMessage(), e);
        }
    }

    /**
     * Returns a diagnostic string containing the app log and the supervisor
     * log, suitable for embedding directly in a JUnit assertion failure message
     * so the full context is visible in CI output without having to fetch
     * separate artefacts.
     */
    public String getDiagnostics() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== App log (").append(appName).append(") ===\n");
        sb.append(readAppLog());
        sb.append("\n=== Supervisor log (last 100 lines) ===\n");
        List<String> supervisorLines = supervisorHarness.getProviderLog();
        int from = Math.max(0, supervisorLines.size() - 100);
        supervisorLines.subList(from, supervisorLines.size()).forEach(l -> sb.append(l).append('\n'));
        return sb.toString();
    }

    /**
     * Reads the most recent log file written by the app's start script, or
     * returns a fallback message.
     */
    private String readAppLog() {
        java.io.File logDir = new java.io.File(supervisorHarness.getNmfDir(),
                "logs/app_" + appName);
        if (!logDir.isDirectory()) {
            return "(log directory not found: " + logDir + ")";
        }
        java.io.File[] logs = logDir.listFiles(f -> f.getName().endsWith(".log"));
        if (logs == null || logs.length == 0) {
            return "(no log files found in " + logDir + ")";
        }
        Arrays.sort(logs, Comparator.comparingLong(java.io.File::lastModified).reversed());
        try {
            return new String(Files.readAllBytes(logs[0].toPath()));
        } catch (IOException e) {
            return "(could not read " + logs[0] + ": " + e.getMessage() + ")";
        }
    }

    private Provider findSupervisorProvider(ProviderList providers) {
        // Select the provider classified as the Supervisor
        for (Provider p : providers) {
            if (NMFProviderType.SUPERVISOR.equals(p.getProviderType())) {
                return p;
            }
        }
        return providers.isEmpty() ? null : providers.get(0);
    }

    private Long resolveAppId() throws MALException, MALInteractionException, IOException {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(appName));
        ListAppResponse response = stub.listApp(names, new Identifier(WILDCARD));
        LongList ids = response.getAppIds();
        if (ids == null || ids.isEmpty()) {
            throw new IOException("App '" + appName + "' not found in AppsLauncher.");
        }
        return ids.get(0);
    }

    private void waitUntilRunning(CountDownLatch uriLatch, AtomicReference<String> foundURI,
            Subscription sub) throws IOException {
        try {
            boolean found = uriLatch.await(STARTUP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!found) {
                if (!isRunning()) {
                    throw new IOException("App '" + appName + "' is not running after runApp() — "
                            + "it likely crashed on startup."
                            + "\n---------------------------"
                            + "\nApp log:"
                            + "\n" + readAppLog()
                            + "\n---------------------------");
                }
                throw new IOException("App '" + appName + "' did not output its Directory URI within "
                        + STARTUP_TIMEOUT_SECONDS + "s — startup too slow or init failed silently.");
            }
            LOGGER.info("App '" + appName + "' registered with URI: " + foundURI.get());
            // Give the archive (H2) a moment to finish initialising before
            // returning. Without this buffer a System.exit() triggered
            // immediately after registration can catch H2 mid-recovery on a
            // reused database, making its shutdown hook take many seconds
            // and causing waitProcessGone to time out.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Interrupted while waiting for app '" + appName + "' to start.");
        } finally {
            IdentifierList subIds = new IdentifierList();
            subIds.add(sub.getSubscriptionId());
            try {
                stub.monitorExecutionDeregister(subIds);
            } catch (MALException | MALInteractionException ignored) {
            }
        }
    }

}
