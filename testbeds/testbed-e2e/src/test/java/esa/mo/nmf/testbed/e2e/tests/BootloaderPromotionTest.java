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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end test for the bootloader's baseline promotion: on a confirmed boot
 * the bootloader must set the secondary baseline to the baseline it just booted
 * (the last known-good), so the fallback ladder always keeps a good version.
 *
 * <p>
 * This is a promotion of the running baseline, not a rotation of the previous
 * primary: it is what lets the {@code setPrimaryBaseline} action re-point the
 * primary any number of times before the next reboot without ever pushing an
 * un-booted version into the secondary.
 */
public class BootloaderPromotionTest extends NMFTest {

    private static final SupervisorHarness harness = new SupervisorHarness();

    /** A distinct main-class written into the secondary before the boot. */
    private static final String MARKER = "test.PromotionMarker";

    private static byte[] secondaryBackup;
    private static byte[] configBackup;
    private static File nmfDir;

    @BeforeClass
    public static void setUpClass() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);

        // The nmf root is resolved before the boot (the harness only sets its
        // own copy inside setUp), so the baseline files can be prepared first.
        nmfDir = new File(System.getProperty(SupervisorHarness.PROP_FILESYSTEM));

        // Force a fresh fallback state so the boot selects the primary baseline.
        Files.deleteIfExists(new File(bootloaderDir(), Deployment.FILE_BOOTLOADER_STATE).toPath());

        // Shorten the promotion soak (default 60s) so the promotion is
        // observable quickly; snapshot the config to restore it afterwards.
        File configFile = new File(bootloaderDir(), Deployment.FILE_BOOTLOADER_CONFIG);
        configBackup = Files.readAllBytes(configFile.toPath());
        Properties config = new Properties();
        try (InputStream in = new FileInputStream(configFile)) {
            config.load(in);
        }
        config.setProperty("promotion-soak-s", "3");
        try (OutputStream out = new FileOutputStream(configFile)) {
            config.store(out, "short promotion soak for the promotion test");
        }

        // Snapshot the secondary, then make it distinct from the primary (a
        // marker main-class) so the promotion overwriting it is observable.
        File secondaryFile = baselineFile(Deployment.ROLE_SECONDARY);
        secondaryBackup = Files.readAllBytes(secondaryFile.toPath());

        Properties distinct = readBaselineFile(Deployment.ROLE_PRIMARY);
        distinct.setProperty("main-class", MARKER);
        try (OutputStream out = new FileOutputStream(secondaryFile)) {
            distinct.store(out, "distinct secondary for the promotion test");
        }

        // Boots the primary baseline; the bootloader promotes it to secondary
        // when the boot confirms. setUp returns at readiness, up to one poll
        // cycle before the bootloader records the promotion, so wait for the
        // effect directly (the marker being overwritten) rather than a shared
        // "confirmed" report line that a previous test class may have written.
        harness.setUp();
        for (int i = 0; i < 80; i++) {
            if (!MARKER.equals(readBaselineFile(Deployment.ROLE_SECONDARY).getProperty("main-class"))) {
                break;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
        if (secondaryBackup != null) {
            Files.write(baselineFile(Deployment.ROLE_SECONDARY).toPath(), secondaryBackup);
        }
        if (configBackup != null) {
            Files.write(new File(bootloaderDir(), Deployment.FILE_BOOTLOADER_CONFIG).toPath(), configBackup);
        }
    }

    // Test — A confirmed boot promotes the running (primary) baseline to the
    // secondary, overwriting the distinct marker.

    @Test
    public void testConfirmedBootPromotesRunningBaselineToSecondary() throws IOException {
        LOGGER.info(SEP + "\nRunning: testConfirmedBootPromotesRunningBaselineToSecondary()\n" + SEP);

        Properties primary = readBaselineFile(Deployment.ROLE_PRIMARY);
        Properties secondary = readBaselineFile(Deployment.ROLE_SECONDARY);

        Assert.assertNotEquals("The distinct marker must have been overwritten by the promotion",
                MARKER, secondary.getProperty("main-class"));
        Assert.assertEquals("The secondary must be promoted to the running (primary) baseline",
                primary.getProperty("main-class"), secondary.getProperty("main-class"));
        Assert.assertEquals("nmf-version must match the running baseline",
                primary.getProperty("nmf-version"), secondary.getProperty("nmf-version"));
        Assert.assertEquals("mission-version must match the running baseline",
                primary.getProperty("mission-version"), secondary.getProperty("mission-version"));
    }

    private static File bootloaderDir() {
        return new File(nmfDir, Deployment.DIR_BOOTLOADER);
    }

    private static File baselineFile(String role) {
        return new File(bootloaderDir(), Deployment.baselineFileName(role));
    }

    private static Properties readBaselineFile(String role) throws IOException {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(baselineFile(role))) {
            props.load(in);
        }
        return props;
    }
}
