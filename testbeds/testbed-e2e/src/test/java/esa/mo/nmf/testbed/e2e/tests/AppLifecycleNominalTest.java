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
package esa.mo.nmf.testbed.e2e.tests;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.nmf.testbed.e2e.AppHarness;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.IOException;
import java.util.List;
import org.ccsds.moims.mo.sm.structures.AppEventType;
import org.ccsds.moims.mo.sm.structures.AppStopped;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * End-to-end tests for nominal NMF App lifecycle transitions.
 *
 * Each test manages its own start/stop cycle independently. The Supervisor is
 * started once for the whole class and torn down at the end.
 */
public class AppLifecycleNominalTest extends NMFTest {

    private static final long STOP_TIMEOUT_MS = 20_000;
    private static final long EVENT_TIMEOUT_MS = 20_000;

    private static final SupervisorHarness supervisorHarness = new SupervisorHarness();

    private final AppHarness app = new AppHarness("all-mc-services", supervisorHarness);

    @BeforeClass
    public static void startSupervisor() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        supervisorHarness.setUp();
    }

    @AfterClass
    public static void stopSupervisor() throws IOException {
        supervisorHarness.tearDown();
    }

    @After
    public void ensureAppStopped() {
        // Safety net: if a test fails mid-lifecycle, force-stop the app so the
        // next test can start cleanly.
        try {
            if (app.isRunning()) {
                app.kill();
                Thread.sleep(2000);
            }
        } catch (Exception ignored) {
        }
    }

    // -------------------------------------------------------------------------
    // Test 1 — Start
    // -------------------------------------------------------------------------

    @Test
    public void testStartApp() throws Exception {
        LOGGER.info(SEP + "\nRunning: testStartApp()\n" + SEP);
        app.start();

        Assert.assertTrue("App must be running after runApp", app.isRunning());
    }

    // -------------------------------------------------------------------------
    // Test 2 — Stop
    // -------------------------------------------------------------------------

    @Test
    public void testStopApp() throws Exception {
        LOGGER.info(SEP + "\nRunning: testStopApp()\n" + SEP);
        app.start();

        boolean cleanStop = app.stop(STOP_TIMEOUT_MS);

        Assert.assertTrue("stopApp must complete without an update error", cleanStop);
        Assert.assertFalse("App must not be running after stopApp", app.isRunning());
        Assert.assertTrue("OS process must be gone after stopApp", app.isProcessGone());
    }

    // -------------------------------------------------------------------------
    // Test 3 — Kill
    // -------------------------------------------------------------------------

    @Test
    public void testKillApp() throws Exception {
        LOGGER.info(SEP + "\nRunning: testKillApp()\n" + SEP);
        app.start();

        // killApp is SUBMIT (fire and forget); wait for the KILLED monitorEvent
        // as the definitive signal that the process exited.
        List<AppEventType> events = collectEventsAroundAction(
                () -> app.kill(), EVENT_TIMEOUT_MS, 1);

        Assert.assertTrue("KILLED event must be received after killApp",
                events.contains(AppEventType.KILLED));
        Assert.assertFalse("App must not be running after killApp", app.isRunning());
        Assert.assertTrue("OS process must be gone after killApp", app.waitProcessGone(10_000));
    }

    // -------------------------------------------------------------------------
    // Test 4 — monitorEvents on start
    // -------------------------------------------------------------------------

    @Test
    public void testMonitorEventsOnStart() throws Exception {
        LOGGER.info(SEP + "\nRunning: testMonitorEventsOnStart()\n" + SEP);

        // Connect to the Supervisor first (without starting the app) so we can
        // subscribe to monitorEvents before runApp triggers START_REQUESTED/STARTED.
        app.connect();
        CollectorThread collector = new CollectorThread(app, EVENT_TIMEOUT_MS, 2);
        collector.start();
        Thread.sleep(200); // give subscription time to register

        app.runApp();
        List<AppEventType> events = collector.join();

        Assert.assertTrue("START_REQUESTED must be received",
                events.contains(AppEventType.START_REQUESTED));
        Assert.assertTrue("STARTED must be received",
                events.contains(AppEventType.STARTED));
    }

    // -------------------------------------------------------------------------
    // Test 5 — monitorEvents on stop
    // -------------------------------------------------------------------------

    @Test
    public void testMonitorEventsOnStop() throws Exception {
        LOGGER.info(SEP + "\nRunning: testMonitorEventsOnStop()\n" + SEP);
        app.start();

        List<AppEventType> events = collectEventsAroundAction(
                () -> app.stop(STOP_TIMEOUT_MS), EVENT_TIMEOUT_MS, 2);

        Assert.assertTrue("STOP_REQUESTED must be received",
                events.contains(AppEventType.STOP_REQUESTED));
        Assert.assertTrue("STOPPED must be received",
                events.contains(AppEventType.STOPPED));
        Assert.assertTrue("OS process must be gone after stop", app.waitProcessGone(10_000));
    }

    // -------------------------------------------------------------------------
    // Test 6 — monitorEvents on kill
    // -------------------------------------------------------------------------

    @Test
    public void testMonitorEventsOnKill() throws Exception {
        LOGGER.info(SEP + "\nRunning: testMonitorEventsOnKill()\n" + SEP);
        app.start();

        List<AppEventType> events = collectEventsAroundAction(
                () -> app.kill(), EVENT_TIMEOUT_MS, 1);

        Assert.assertTrue("KILLED must be received", events.contains(AppEventType.KILLED));
        Assert.assertTrue("OS process must be gone after kill", app.waitProcessGone(10_000));
    }

    // -------------------------------------------------------------------------
    // Test 7 — AppStarted archive record
    // -------------------------------------------------------------------------

    @Test
    public void testAppStartedInArchive() throws Exception {
        LOGGER.info(SEP + "\nRunning: testAppStartedInArchive()\n" + SEP);
        app.start();

        List<ArchivePersistenceObject> records = app.queryAppStarted();

        Assert.assertFalse("At least one AppStarted record must exist in archive"
                + (records.isEmpty() ? app.getDiagnostics() : ""),
                records.isEmpty());
    }

    // -------------------------------------------------------------------------
    // Test 8 — AppStopped archive record
    // -------------------------------------------------------------------------

    @Test
    public void testAppStoppedInArchive() throws Exception {
        LOGGER.info(SEP + "\nRunning: testAppStoppedInArchive()\n" + SEP);
        app.start();
        app.stop(STOP_TIMEOUT_MS);

        List<ArchivePersistenceObject> records = app.queryAppStopped();

        Assert.assertFalse("At least one AppStopped record must exist in archive"
                + (records.isEmpty() ? app.getDiagnostics() : ""),
                records.isEmpty());

        AppStopped body = (AppStopped) records.get(0).getObject();
        Assert.assertEquals("AppStopped.stopReason must be STOPPED",
                AppEventType.STOPPED, body.getStopReason());
    }

    // -------------------------------------------------------------------------
    // Test 9 — Self-exit with code 0 is classified as EXITED (control)
    // -------------------------------------------------------------------------

    @Test
    public void testSelfExitZeroIsExited() throws Exception {
        LOGGER.info(SEP + "\nRunning: testSelfExitZeroIsExited()\n" + SEP);
        AppHarness benchmark = new AppHarness("benchmark", supervisorHarness);
        benchmark.start();

        benchmark.launchAppAction("shutdown.system.exit.0");

        Assert.assertTrue("OS process must be gone after self-exit" + benchmark.getDiagnostics(),
                benchmark.waitProcessGone(10_000));

        List<ArchivePersistenceObject> records = benchmark.queryAppStopped();
        boolean foundExited0 = records.stream().anyMatch(r -> {
            AppStopped b = (AppStopped) r.getObject();
            return AppEventType.EXITED.equals(b.getStopReason())
                    && Integer.valueOf(0).equals(b.getExitCode());
        });
        Assert.assertTrue("An AppStopped record with EXITED and exitCode 0 must exist"
                + (!foundExited0 ? benchmark.getDiagnostics() : ""), foundExited0);
    }

    // -------------------------------------------------------------------------
    // Test 10 — Self-exit with a non-zero code is classified as CRASHED and the
    // exit code is propagated. Captures the bug where the exit code arrives as 0.
    // -------------------------------------------------------------------------

    @Test
    public void testSelfExitNonZeroIsCrashed() throws Exception {
        LOGGER.info(SEP + "\nRunning: testSelfExitNonZeroIsCrashed()\n" + SEP);
        AppHarness benchmark = new AppHarness("benchmark", supervisorHarness);
        benchmark.start();

        benchmark.launchAppAction("shutdown.system.exit.x", 18);

        Assert.assertTrue("OS process must be gone after self-exit" + benchmark.getDiagnostics(),
                benchmark.waitProcessGone(10_000));

        List<ArchivePersistenceObject> records = benchmark.queryAppStopped();
        Assert.assertFalse("An AppStopped record must exist"
                + (records.isEmpty() ? benchmark.getDiagnostics() : ""), records.isEmpty());

        boolean foundCrashed18 = records.stream().anyMatch(r -> {
            AppStopped b = (AppStopped) r.getObject();
            return AppEventType.CRASHED.equals(b.getStopReason())
                    && Integer.valueOf(18).equals(b.getExitCode());
        });
        Assert.assertTrue("Self-exit with code 18 must be recorded as CRASHED with exitCode 18 "
                + "(the Apps Launcher currently receives exit code 0)"
                + (!foundCrashed18 ? benchmark.getDiagnostics() : ""), foundCrashed18);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    @FunctionalInterface
    private interface Action {
        void run() throws Exception;
    }

    /**
     * Subscribes to monitorEvents for this app, executes the action, then
     * waits for up to {@code timeoutMs} to collect {@code count} events.
     */
    private List<AppEventType> collectEventsAroundAction(
            Action action, long timeoutMs, int count) throws Exception {
        CollectorThread collector = new CollectorThread(app, timeoutMs, count);
        collector.start();
        Thread.sleep(200);
        action.run();
        return collector.join();
    }

    /**
     * Runs waitForMonitorEvents on a background thread so the subscription is
     * active before the triggering action is called.
     */
    private static class CollectorThread {

        private final AppHarness app;
        private final long timeoutMs;
        private final int count;
        private Thread thread;
        private List<AppEventType> result;
        private Exception error;

        CollectorThread(AppHarness app, long timeoutMs, int count) {
            this.app = app;
            this.timeoutMs = timeoutMs;
            this.count = count;
        }

        void start() {
            thread = new Thread(() -> {
                try {
                    result = app.waitForMonitorEvents(timeoutMs, count);
                } catch (Exception e) {
                    error = e;
                }
            });
            thread.setDaemon(true);
            thread.start();
        }

        List<AppEventType> join() throws Exception {
            thread.join(timeoutMs + 2000);
            if (error != null) {
                throw error;
            }
            return result != null ? result : List.of();
        }
    }
}
