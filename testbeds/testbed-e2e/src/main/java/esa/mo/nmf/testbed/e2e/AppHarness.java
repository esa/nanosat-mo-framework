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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.structures.NMFProviderType;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.NullableAttributeList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherStub;
import org.ccsds.moims.mo.sm.structures.AppEventType;

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
    private static final int STARTUP_TIMEOUT_SECONDS = 2;
    private static final String WILDCARD = "*";

    private final String appName;
    private final SupervisorHarness supervisorHarness;

    private GroundMOAdapterImpl adapter;
    private AppsLauncherStub stub;
    private Long appId;

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
        try {
            LongList ids = new LongList();
            ids.add(appId);
            stub.runApp(ids);
            LOGGER.info("runApp('" + appName + "') submitted.");

            waitUntilRunning();
            LOGGER.info("App '" + appName + "' is running.");
        } catch (MALException | MALInteractionException e) {
            throw new IOException("Failed to start app '" + appName + "': " + e.getMessage(), e);
        }
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
                    + "\nApp log (may include AppShutdownGuard thread dump):"
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
        try {
            ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                    new URI(supervisorHarness.getDirectoryURI()));
            Provider appProvider = null;
            for (Provider p : providers) {
                if (NMFProviderType.APP.equals(p.getProviderType())
                        && p.getProviderName() != null
                        && appName.equals(p.getProviderName().getValue())) {
                    appProvider = p;
                    break;
                }
            }
            if (appProvider == null) {
                throw new IOException("App provider '" + appName + "' not found in Directory.");
            }
            GroundMOAdapterImpl appAdapter = new GroundMOAdapterImpl(appProvider);
            appAdapter.launchAction(actionName, args);
            LOGGER.info("launchAction('" + actionName + "') on app '" + appName + "' submitted.");
        } catch (MALException | MALInteractionException | java.net.MalformedURLException e) {
            throw new IOException("Failed to invoke action on app '" + appName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Sends stopApp and blocks until the PROGRESS RESPONSE is received or the
     * timeout expires.
     *
     * @param timeoutMs maximum time to wait in milliseconds.
     * @return true if the app stopped cleanly (UPDATE received, not an error);
     * false if an update error was received or the timeout expired.
     * @throws IOException if the call itself fails.
     */
    public boolean stop(long timeoutMs) throws IOException {
        if (stub == null || appId == null) {
            return false;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicBoolean updateError = new AtomicBoolean(false);
        LongList ids = new LongList();
        ids.add(appId);
        try {
            stub.stopApp(ids, new AppsLauncherAdapter() {
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
        Subscription sub = org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer
                .subscriptionWildcardRandom();
        try {
            stub.monitorEventsRegister(sub, new AppsLauncherAdapter() {
                @Override
                public void monitorEventsNotifyReceived(MALMessageHeader msgHeader,
                        Identifier subscriptionId, UpdateHeader updateHeader,
                        AppEventType eventType, Integer exitCode, String extraInfo,
                        java.util.Map qosProperties) {
                    NullableAttributeList keyValues = updateHeader.getKeyValues();
                    if (keyValues == null || keyValues.isEmpty()) {
                        return;
                    }
                    Identifier name = (Identifier) keyValues.get(0).getValue();
                    if (appName.equals(name.getValue())) {
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
        return queryByRelated(AppsLauncherServiceInfo.APPSTARTED_OBJECT_TYPE);
    }

    /**
     * Queries the archive for AppStopped COM objects whose related link points
     * to this app's AppDetails object.
     *
     * @return list of matching archive objects, never null.
     * @throws IOException if the query fails.
     */
    public List<ArchivePersistenceObject> queryAppStopped() throws IOException {
        return queryByRelated(AppsLauncherServiceInfo.APPSTOPPED_OBJECT_TYPE);
    }

    private List<ArchivePersistenceObject> queryByRelated(ObjectType objType) throws IOException {
        List<ArchivePersistenceObject> results = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch latch = new CountDownLatch(1);

        try {
            adapter.getCOMServices().getArchiveService().getArchiveStub().query(
                    Boolean.TRUE, objType, new ArchiveQuery(appId), null,
                    new ArchiveAdapter() {
                        @Override
                        public void queryUpdateReceived(MALMessageHeader msgHeader,
                                ObjectType receivedObjType, IdentifierList domain,
                                ArchiveDetailsList details,
                                org.ccsds.moims.mo.mal.structures.HeterogeneousList bodies,
                                java.util.Map qosProperties) {
                            if (details != null) {
                                for (int i = 0; i < details.size(); i++) {
                                    org.ccsds.moims.mo.mal.structures.Element body =
                                            (bodies != null && i < bodies.size())
                                            ? (org.ccsds.moims.mo.mal.structures.Element) bodies.get(i)
                                            : null;
                                    results.add(new ArchivePersistenceObject(
                                            receivedObjType, domain,
                                            details.get(i).getId(), details.get(i), body));
                                }
                            }
                        }

                        @Override
                        public void queryResponseReceived(MALMessageHeader msgHeader,
                                java.util.Map qosProperties) {
                            latch.countDown();
                        }

                        @Override
                        public void queryAckErrorReceived(MALMessageHeader msgHeader,
                                MOErrorException error, java.util.Map qosProperties) {
                            latch.countDown();
                        }
                    });
        } catch (MALException | MALInteractionException e) {
            throw new IOException("Archive query failed: " + e.getMessage(), e);
        }
        try {
            latch.await(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new ArrayList<>(results);
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
                stub.stopApp(ids, new AppsLauncherAdapter() {
                });
                LOGGER.info("stopApp('" + appName + "') submitted.");
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.WARNING, "Error stopping app '" + appName + "': " + e.getMessage(), e);
            }
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

    /**
     * Polls the Supervisor's Directory service until the app's own provider
     * entry appears, which only happens after {@code NanoSatMOConnectorImpl.init()}
     * completes. This is the definitive "app is fully initialised" signal:
     * the supervisor's running flag and the STARTED monitorEvent both fire
     * when the process is merely spawned, before the JVM has loaded any
     * classes, so checking either of those can race with the first action or
     * stop request.
     *
     * <p>Uses the already-open Directory stub from {@link #adapter} to avoid
     * opening a new MAL connection on every poll.
     */
    private void waitUntilRunning() throws IOException {
        IdentifierList domainWildcard = new IdentifierList();
        domainWildcard.add(new Identifier("*"));
        org.ccsds.moims.mo.com.structures.ServiceFilter wildcardFilter =
                new org.ccsds.moims.mo.com.structures.ServiceFilter(
                        new Identifier("*"),
                        domainWildcard,
                        new org.ccsds.moims.mo.mal.structures.ServiceId(
                                new org.ccsds.moims.mo.mal.structures.UShort((short) 0),
                                new org.ccsds.moims.mo.mal.structures.UShort((short) 0),
                                new org.ccsds.moims.mo.mal.structures.UOctet((short) 0)),
                        null);

        long deadline = System.currentTimeMillis() + STARTUP_TIMEOUT_SECONDS * 1000L;
        while (System.currentTimeMillis() < deadline) {
            try {
                ProviderList providers = adapter.getCOMServices()
                        .getDirectoryService().getDirectoryStub().lookup(wildcardFilter);
                for (Provider p : providers) {
                    if (NMFProviderType.APP.equals(p.getProviderType())
                            && p.getProviderName() != null
                            && appName.equals(p.getProviderName().getValue())) {
                        // App registered in Directory — give the archive (H2) a moment
                        // to finish initialising before returning. Without this buffer a
                        // System.exit() triggered immediately after registration can catch
                        // H2 mid-recovery on a reused database, making its shutdown hook
                        // take many seconds and causing waitProcessGone to time out.
                        Thread.sleep(500);
                        return;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for app '" + appName + "' to start.");
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for app '" + appName + "' to start.");
            }
        }
        // Timeout: check whether the process at least started (crash detection)
        if (!isRunning()) {
            throw new IOException("App '" + appName + "' is not running after runApp() — "
                    + "it likely crashed on startup."
                    + "\n---------------------------"
                    + "\nApp log:"
                    + "\n" + readAppLog()
                    + "\n---------------------------");
        }
        throw new IOException("App '" + appName + "' did not register in Directory within "
                + STARTUP_TIMEOUT_SECONDS + "s — startup too slow or init failed silently.");
    }

}
