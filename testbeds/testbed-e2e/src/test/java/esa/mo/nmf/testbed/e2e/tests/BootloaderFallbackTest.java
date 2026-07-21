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

import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * End-to-end tests for the fallback ladder of the NMF Bootloader (REC.01-05):
 * a primary baseline whose Supervisor cannot start leads, after
 * boot-max-attempts failed invocations, to the secondary baseline; a
 * confirmed boot resets the fallback state. The bootloader performs one boot
 * attempt per invocation, so the ladder is driven by running the script
 * repeatedly, exactly like a service manager would.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BootloaderFallbackTest extends NMFTest {

    private static final SupervisorHarness harness = new SupervisorHarness();

    private static File nmfDir;
    private static File primaryFile;
    private static byte[] primaryBackup;
    private static File stateFile;

    @BeforeClass
    public static void setUpClass() throws IOException {
        String path = System.getProperty(SupervisorHarness.PROP_FILESYSTEM);
        Assert.assertNotNull("System property '" + SupervisorHarness.PROP_FILESYSTEM
                + "' is not set.", path);
        nmfDir = new File(path);

        File bootloaderDir = new File(nmfDir, Deployment.DIR_BOOTLOADER);
        primaryFile = new File(bootloaderDir, Deployment.FILE_BASELINE_PRIMARY);
        stateFile = new File(bootloaderDir, Deployment.FILE_BOOTLOADER_STATE);
        primaryBackup = Files.readAllBytes(primaryFile.toPath());

        // Break the primary baseline: a main class that cannot exist makes
        // the JVM exit immediately, before any confirmation
        String broken = new String(primaryBackup, StandardCharsets.UTF_8)
                .replaceAll("main-class=.*", "main-class=esa.mo.nmf.does.not.Exist");
        Files.write(primaryFile.toPath(), broken.getBytes(StandardCharsets.UTF_8));
        Files.deleteIfExists(stateFile.toPath());
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
        // Restore the primary baseline and a clean fallback state for the
        // other test classes of this run
        Files.write(primaryFile.toPath(), primaryBackup);
        Files.deleteIfExists(stateFile.toPath());
    }

    // Test 1 — Two failed invocations of the broken primary advance the
    // fallback ladder to the secondary rung

    @Test
    public void test1_FailedAttemptsAdvanceTheLadder() throws Exception {
        LOGGER.info(SEP + "\nRunning: test1_FailedAttemptsAdvanceTheLadder()\n" + SEP);

        Assert.assertNotEquals("First attempt must fail", 0, runBootloaderOnce());
        Properties state = loadState();
        Assert.assertEquals("primary", state.getProperty("rung"));
        Assert.assertEquals("1", state.getProperty("failed-attempts"));

        Assert.assertNotEquals("Second attempt must fail", 0, runBootloaderOnce());
        state = loadState();
        Assert.assertEquals("The rung must advance after boot-max-attempts failures",
                "secondary", state.getProperty("rung"));
        Assert.assertEquals("0", state.getProperty("failed-attempts"));
    }

    // Test 2 — The next start boots the secondary baseline, the Supervisor
    // confirms, and the fallback state resets to primary (self-healing)

    @Test
    public void test2_SecondaryBootsAndConfirmationResetsState() throws Exception {
        LOGGER.info(SEP + "\nRunning: test2_SecondaryBootsAndConfirmationResetsState()\n" + SEP);

        harness.setUp(); // Boots via the bootloader; waits for readiness
        Assert.assertNotNull("The Supervisor must be reachable on the secondary baseline",
                harness.getDirectoryURI());

        // The confirmation is written at the same readiness point the harness
        // waits for; allow a moment for the state reset to land
        Properties state = null;
        for (int i = 0; i < 20; i++) {
            state = loadState();
            if ("primary".equals(state.getProperty("rung"))) {
                break;
            }
            Thread.sleep(500);
        }
        Assert.assertEquals("A confirmed boot must reset the rung to primary",
                "primary", state.getProperty("rung"));
        Assert.assertEquals("0", state.getProperty("failed-attempts"));
    }

    /**
     * Runs one bootloader invocation to completion, like the service manager
     * would, and returns its exit code.
     */
    private static int runBootloaderOnce() throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder("bash",
                new File(nmfDir, "start_supervisor.sh").getAbsolutePath());
        pb.directory(nmfDir);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        Process p = pb.start();
        Assert.assertTrue("The bootloader invocation did not terminate",
                p.waitFor(60, TimeUnit.SECONDS));
        return p.exitValue();
    }

    private static Properties loadState() throws IOException {
        Properties props = new Properties();
        try (java.io.FileInputStream in = new java.io.FileInputStream(stateFile)) {
            props.load(in);
        }
        return props;
    }
}
