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

import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.List;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end test for the generic {@code bootloader.restart} action: invoking
 * it makes the Supervisor exit with the restart code, which the bootloader
 * ({@code start_supervisor.sh}) recognises and re-executes, booting the current
 * primary baseline again.
 */
public class BootloaderRestartTest extends NMFTest {

    private static final SupervisorHarness harness = new SupervisorHarness();
    private static final String ACTION_RESTART = "bootloader.restart";

    private static GroundMOAdapterImpl adapter;

    @BeforeClass
    public static void setUpClass() throws Exception {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        harness.setUp();

        ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                new URI(harness.getDirectoryURI()));
        Assert.assertFalse("Directory must return a provider", providers.isEmpty());
        Provider supervisor = providers.get(0);
        adapter = new GroundMOAdapterImpl(supervisor);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        if (adapter != null) {
            adapter.closeConnections();
        }
        harness.tearDown();
    }

    // Test — The restart action makes the bootloader re-execute and boot again

    @Test
    public void testRestartActionReExecsBootloader() throws Exception {
        LOGGER.info(SEP + "\nRunning: testRestartActionReExecsBootloader()\n" + SEP);

        int bootsBefore = countBootStarts();

        Long actionId = adapter.launchAction(ACTION_RESTART, new Serializable[]{});
        Assert.assertNotNull("The restart action must be submitted", actionId);
        Assert.assertNotEquals("The restart action must be found", Long.valueOf(-1), actionId);

        // The Supervisor exits with the restart code after a short grace period;
        // the bootloader then re-executes and boots again. Wait for both the
        // restart notice and a fresh boot in the report.
        boolean reExeced = false;
        for (int i = 0; i < 60; i++) {
            List<String> report = readReport();
            if (contains(report, "restart requested (exit 90)") && countBootStarts() > bootsBefore) {
                reExeced = true;
                break;
            }
            Thread.sleep(500);
        }
        Assert.assertTrue("The bootloader must re-execute and boot again after the restart action",
                reExeced);
    }

    private static int countBootStarts() throws IOException {
        int count = 0;
        for (String line : readReport()) {
            if (line.contains("Boot started at:")) {
                count++;
            }
        }
        return count;
    }

    private static List<String> readReport() throws IOException {
        File reportFile = new File(new File(harness.getNmfDir(), Deployment.DIR_LOGS),
                Deployment.DIR_BOOTLOADER + File.separator
                + "bootloader_" + LocalDate.now() + ".log");
        if (!reportFile.isFile()) {
            return List.of();
        }
        return Files.readAllLines(reportFile.toPath(), StandardCharsets.UTF_8);
    }

    private static boolean contains(List<String> lines, String token) {
        for (String line : lines) {
            if (line.contains(token)) {
                return true;
            }
        }
        return false;
    }
}
