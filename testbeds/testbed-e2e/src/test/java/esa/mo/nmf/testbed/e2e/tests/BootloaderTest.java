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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end tests for the NMF Bootloader: boots the Supervisor through the
 * bootloader script and verifies the Boot Report and the runtime state
 * against the NMF Bootloader Specification.
 */
public class BootloaderTest extends NMFTest {

    private static final SupervisorHarness harness = new SupervisorHarness();

    /**
     * True if a bootloader state file existed before this class booted the
     * Supervisor: in that case this is not the first boot attempt of the
     * current OS boot, and the restart type must be warm instead of cold.
     */
    private static boolean stateExistedBeforeBoot;

    @BeforeClass
    public static void setUpClass() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        String path = System.getProperty(SupervisorHarness.PROP_FILESYSTEM);
        if (path != null) {
            stateExistedBeforeBoot = new File(new File(path, Deployment.DIR_BOOTLOADER),
                    Deployment.FILE_BOOTLOADER_STATE).isFile();
        }
        harness.setUp();

        // The harness returns at the readiness message, up to one poll cycle
        // before the bootloader records the confirmation: wait for that
        // confirmation entry
        try {
            for (int i = 0; i < 20; i++) {
                if (contains(readTodaysReport(), "CONFIRMATION confirmed")) {
                    return;
                }
                Thread.sleep(500);
            }
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
    }

    // Test — A Boot Report was written for this boot, complete with the
    // start marker, the six sequence steps, and the confirmation entry

    @Test
    public void testBootReportIsComplete() throws IOException {
        LOGGER.info(SEP + "\nRunning: testBootReportIsComplete()\n" + SEP);
        List<String> report = readTodaysReport();

        Assert.assertTrue("Missing report start marker",
                contains(report, "Boot started at:"));
        for (String step : new String[]{"INITIALISATION", "SELF-TESTS",
            "BASELINE-SELECTION", "INTEGRITY-TEST", "EXECUTION", "CONFIRMATION"}) {
            Assert.assertTrue("Missing step entry: " + step, contains(report, step));
        }
        Assert.assertTrue("The boot must be confirmed by the Supervisor",
                contains(report, "CONFIRMATION confirmed"));
    }

    // Test — The report records a successful nominal boot: primary baseline
    // selected, all self-tests and integrity tests OK

    @Test
    public void testNominalBootSelectsPrimaryWithAllTestsPassing() throws IOException {
        LOGGER.info(SEP + "\nRunning: testNominalBootSelectsPrimaryWithAllTestsPassing()\n" + SEP);
        List<String> report = readTodaysReport();

        Assert.assertTrue("The primary baseline must be selected on a nominal boot",
                contains(report, "BASELINE-SELECTION selected: primary"));
        List<String> failures = report.stream()
                .filter(line -> line.contains(": FAIL"))
                .collect(Collectors.toList());
        Assert.assertTrue("No step may fail on a nominal boot, but found: " + failures,
                failures.isEmpty());
    }

    // Test — The restart type matches the pre-boot state: cold on the first
    // boot of this OS boot, warm afterwards

    @Test
    public void testRestartTypeIndication() throws IOException {
        LOGGER.info(SEP + "\nRunning: testRestartTypeIndication()\n" + SEP);
        List<String> report = readTodaysReport();
        String expected = stateExistedBeforeBoot ? "restart-type: warm" : "restart-type: cold";
        Assert.assertTrue("Expected '" + expected + "' in the Boot Report",
                contains(report, expected));
    }

    // Test — The bootloader persisted its runtime state with the current
    // kernel boot identifier

    @Test
    public void testStateFileHoldsCurrentBootId() throws IOException {
        LOGGER.info(SEP + "\nRunning: testStateFileHoldsCurrentBootId()\n" + SEP);
        File stateFile = new File(new File(harness.getNmfDir(), Deployment.DIR_BOOTLOADER),
                Deployment.FILE_BOOTLOADER_STATE);
        Assert.assertTrue("Missing state file: " + stateFile.getAbsolutePath(),
                stateFile.isFile());

        String state = new String(Files.readAllBytes(stateFile.toPath()), StandardCharsets.UTF_8);
        String bootId = new String(Files.readAllBytes(
                new File("/proc/sys/kernel/random/boot_id").toPath()),
                StandardCharsets.UTF_8).trim();
        Assert.assertTrue("State file must record the current kernel boot id",
                state.contains("boot-id=" + bootId));
    }

    /**
     * Returns the last Boot Report section of today's daily file: the one
     * written by the boot performed in {@link #setUpClass()}. Earlier
     * sections of the same day (from other test classes or manual boots)
     * must not leak into the assertions.
     */
    private static List<String> readTodaysReport() throws IOException {
        File reportFile = new File(new File(harness.getNmfDir(), Deployment.DIR_LOGS),
                Deployment.DIR_BOOTLOADER + File.separator
                + "bootloader_" + LocalDate.now() + ".log");
        Assert.assertTrue("Missing Boot Report file: " + reportFile.getAbsolutePath(),
                reportFile.isFile());

        List<String> lines = Files.readAllLines(reportFile.toPath(), StandardCharsets.UTF_8);
        int lastStart = 0;
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).contains("Boot started at:")) {
                lastStart = i;
            }
        }
        return lines.subList(lastStart, lines.size());
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
