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
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.sm.structures.AppEventType;
import org.ccsds.moims.mo.sm.structures.AppStopped;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end tests for non-nominal NMF App shutdown, using the
 * {@code broken-app-for-testbeds} app. Pairs with {@link AppLifecycleNominalTest}.
 *
 * <p>
 * These exercise the stopApp grace-period contract: a slow-but-graceful app is
 * allowed to finish (there is no app-side shutdown guard any more), while a
 * genuinely hung app is force-killed by the Supervisor once a non-NULL timeout
 * elapses.
 */
public class AppLifecycleNonNominalTest extends NMFTest {

    private static final long ACTION_TIMEOUT_MS = 15_000;

    private static final SupervisorHarness supervisorHarness = new SupervisorHarness();

    private final AppHarness app = new AppHarness("broken-app-for-testbeds", supervisorHarness);

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
        try {
            if (app.isRunning()) {
                app.kill();
                app.waitProcessGone(20_000);
            }
        } catch (Exception ignored) {
        }
    }

    /**
     * An app whose onClose takes several seconds must be allowed to finish: with
     * a NULL timeout (never force-kill) it stops cleanly, recorded as STOPPED
     * with exit code 0. This is the case the old 5 s AppShutdownGuard broke.
     */
    @Test
    public void testSlowShutdownCompletesGracefully() throws Exception {
        LOGGER.info(SEP + "\nRunning: testSlowShutdownCompletesGracefully()\n" + SEP);
        app.start();

        Boolean armed = app.awaitActionOutcome("shutdown.delay", ACTION_TIMEOUT_MS);
        Assert.assertTrue("The shutdown.delay action must complete to arm the slow close"
                + app.getDiagnostics(), Boolean.TRUE.equals(armed));

        // NULL grace period = never force-kill; wait long enough for the ~8 s close.
        boolean cleanStop = app.stop(25_000, null);
        Assert.assertTrue("A slow but graceful app must stop cleanly (no force-kill)"
                + app.getDiagnostics(), cleanStop);
        Assert.assertTrue("The process must be gone after a graceful stop", app.waitProcessGone(10_000));

        List<ArchivePersistenceObject> records = app.queryAppStopped();
        boolean gracefulExit = records.stream().anyMatch(r -> {
            AppStopped body = (AppStopped) r.getObject();
            return AppEventType.STOPPED.equals(body.getStopReason())
                    && Integer.valueOf(0).equals(body.getExitCode());
        });
        Assert.assertTrue("A slow graceful shutdown must be recorded as STOPPED with exit code 0"
                + (!gracefulExit ? app.getDiagnostics() : ""), gracefulExit);
    }

    /**
     * A genuinely hung app must be force-killed by the Supervisor once the
     * supplied grace period elapses, recorded as KILLED.
     */
    @Test
    public void testHungShutdownIsKilledAfterTimeout() throws Exception {
        LOGGER.info(SEP + "\nRunning: testHungShutdownIsKilledAfterTimeout()\n" + SEP);
        app.start();

        Boolean armed = app.awaitActionOutcome("shutdown.hang", ACTION_TIMEOUT_MS);
        Assert.assertTrue("The shutdown.hang action must complete to arm the hang"
                + app.getDiagnostics(), Boolean.TRUE.equals(armed));

        // 3 s grace period: the app hangs, so the Supervisor must force-kill it.
        app.stop(30_000, new Duration(3.0));
        Assert.assertTrue("The Supervisor must force the hung process down after the grace period"
                + app.getDiagnostics(), app.waitProcessGone(15_000));

        List<ArchivePersistenceObject> records = app.queryAppStopped();
        boolean killed = records.stream().anyMatch(r ->
                AppEventType.KILLED.equals(((AppStopped) r.getObject()).getStopReason()));
        Assert.assertTrue("A hung app must be force-killed by the Supervisor and recorded as KILLED"
                + (!killed ? app.getDiagnostics() : ""), killed);
    }

}
