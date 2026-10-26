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
package esa.mo.sm.impl.provider;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.sm.structures.AppDetails;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for starting an app. The Supervisor checks that an app is not
 * running before it starts it, and the check and the start must be one step:
 * otherwise requests that arrive together all pass the check, each starts a
 * process, and all of them are told that the app was started.
 */
public class AppsLauncherManagerRunTest {

    private static final Long APP_ID = 1L;
    private static final int REQUESTS = 16;

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /** The command that the app is started with, in place of its start script. */
    private volatile String[] command = {"sleep", "30"};
    private final AtomicInteger processesStarted = new AtomicInteger();
    private final List<ProcessExecutionHandler> handlers = new CopyOnWriteArrayList<>();

    // The manager needs no COM archive to start an app.
    private final AppsLauncherManagerLinux manager = new AppsLauncherManagerLinux(null) {
        @Override
        protected String[] assembleCommand(String workDir, String appName, String runAs, String[] env) {
            processesStarted.incrementAndGet();
            return command;
        }
    };

    /** Records, as the Supervisor does, that the app stopped when its process ends. */
    private final ProcessExecutionHandler.Callbacks callbacks = new ProcessExecutionHandler.Callbacks() {
        @Override
        public void flushStdout(Long objId, String data) {
        }

        @Override
        public void flushStderr(Long objId, String data) {
        }

        @Override
        public void processStopped(Long objId, int exitCode) {
            manager.setRunning(objId, false, null);
        }
    };

    @Before
    public void registerAnAppThatIsNotRunning() throws Exception {
        File apps = tmp.newFolder("apps");
        Assert.assertTrue(new File(apps, "my-app").mkdir());
        manager.addFolderWithApps(apps);
        manager.addDef(APP_ID, new AppDetails(new Identifier("my-app"), "-", "1.0",
                new Identifier("NMF_App"), false, false, "-", "-", null));
    }

    @After
    public void stopTheProcesses() {
        for (ProcessExecutionHandler handler : handlers) {
            if (handler.getProcess() != null) {
                handler.getProcess().destroyForcibly();
            }
        }
    }

    @Test
    public void requestsThatArriveTogetherStartTheAppOnce() throws Exception {
        CountDownLatch go = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(REQUESTS);
        AtomicInteger told = new AtomicInteger();

        for (int i = 0; i < REQUESTS; i++) {
            new Thread(() -> {
                try {
                    go.await();
                    if (start()) {
                        told.incrementAndGet();
                    }
                } catch (Exception ex) {
                    // A refused start is not counted as started
                } finally {
                    done.countDown();
                }
            }).start();
        }
        go.countDown();
        Assert.assertTrue(done.await(30, TimeUnit.SECONDS));

        Assert.assertEquals("Only one process may be started for the app", 1, processesStarted.get());
        Assert.assertEquals("Only one request may be told that the app was started", 1, told.get());
        Assert.assertTrue(manager.isAppRunning(APP_ID));
    }

    @Test
    public void anAppCanBeStartedAgainOnceItsProcessHasEnded() throws Exception {
        command = new String[]{"true"};
        Assert.assertTrue(start());

        long deadline = System.currentTimeMillis() + 10_000;
        while (manager.isAppRunning(APP_ID) && System.currentTimeMillis() < deadline) {
            Thread.sleep(20);
        }
        Assert.assertFalse("The app must be seen as stopped once its process has ended", manager.isAppRunning(APP_ID));

        Assert.assertTrue("A stopped app must start again", start());
        Assert.assertEquals(2, processesStarted.get());
    }

    /** Starts the app as a runApp request does, and returns whether it was started. */
    private boolean start() throws Exception {
        ProcessExecutionHandler handler = new ProcessExecutionHandler(callbacks, APP_ID);
        handlers.add(handler);
        return manager.startAppProcess(handler, null, "maltcp://localhost:1024/Directory");
    }
}
