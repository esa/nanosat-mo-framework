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
import esa.mo.nmf.environment.SoftwareBaseline;
import esa.mo.nmf.mcadapters.SupervisorInfoMCAdapter;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import esa.mo.nmf.testbed.e2e.UpgradeFilesystemHarness;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.structures.ParameterValueDetailsList;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * End-to-end test of an upgrade of the NMF that does not come up: the primary
 * baseline is moved to the version under development and that version is made
 * unable to start, so that the Bootloader has to find its own way back to the
 * released version that the secondary baseline still holds.
 *
 * This is the rollback that matters most, because it is the one that happens
 * with nobody there to command it. The ladder is driven by running the
 * Bootloader once per invocation, exactly as a service manager would.
 *
 * The version that is fallen back to is held to the {@code nmf.version} that
 * the Supervisor answers with, which it reads out of the manifest of the Jar it
 * was loaded from, so the fallback has to have reached the released Jars and
 * not merely the released baseline.
 *
 * The filesystem is a copy of the generated one, so that the other tests are
 * left with the single version they are written for.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NMFUpgradeFallbackTest extends NMFTest {


    /**
     * A class that is nowhere on the classpath, so that the JVM gives up at
     * once and the boot is never confirmed.
     */
    private static final String UNSTARTABLE = "esa.mo.nmf.upgrade.that.cannot.Start";

    private static final int BOOTLOADER_TIMEOUT_SECONDS = 60;

    private static UpgradeFilesystemHarness filesystem;
    private static SupervisorHarness harness;

    @BeforeClass
    public static void setUpClass() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        filesystem = new UpgradeFilesystemHarness(
                new File(System.getProperty(UpgradeFilesystemHarness.PROP_FILESYSTEM_UPGRADE)));

        // The upgrade is on the primary and cannot start; the release that is
        // upgraded from stays on the secondary, to be fallen back to
        filesystem.setBaselineVersion(Deployment.ROLE_PRIMARY, filesystem.getDevelopmentVersion());
        filesystem.setBaselineMainClass(Deployment.ROLE_PRIMARY, UNSTARTABLE);
        filesystem.setBaselineVersion(Deployment.ROLE_SECONDARY, filesystem.getBaselineVersion());
        filesystem.clearBootState();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        if (harness != null) {
            harness.tearDown();
            harness = null;
        }
    }

    // Test 1 — An upgrade that cannot start advances the fallback ladder
    // instead of being retried for ever

    @Test
    public void test1_FailedUpgradeAdvancesTheLadder() throws Exception {
        LOGGER.info(SEP + "\nRunning: test1_FailedUpgradeAdvancesTheLadder()\n" + SEP);
        int maxAttempts = readMaxAttempts();

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            Assert.assertNotEquals("An upgrade that cannot start must fail its boot",
                    0, runBootloaderOnce());
        }

        Assert.assertEquals("After " + maxAttempts + " failed attempts the ladder must have"
                + " moved off the upgrade", Deployment.ROLE_SECONDARY,
                loadState().getProperty("rung"));
        Assert.assertFalse("An upgrade that cannot start must never be confirmed",
                filesystem.isBootConfirmed());
    }

    // Test 2 — The next boot falls back to the released version, and it is the
    // released Jars that are running

    @Test
    public void test2_FallbackRunsTheReleasedVersion() throws Exception {
        LOGGER.info(SEP + "\nRunning: test2_FallbackRunsTheReleasedVersion()\n" + SEP);
        String released = filesystem.getBaselineVersion();

        harness = new SupervisorHarness();
        harness.setUp(filesystem.getNmfDir());

        assertBootloaderSelected(Deployment.ROLE_SECONDARY);
        assertBootloaderVerified(Deployment.DIR_JARS_NMF, released);
        assertBootloaderVerified(Deployment.DIR_JARS_MISSION, released);
        Assert.assertTrue("The fallback must report a successful boot",
                filesystem.isBootConfirmed());

        GroundMOAdapterImpl adapter = connect();
        try {
            ParameterStub stub = adapter.getMCServices().getParameterService().getParameterStub();
            IdentifierList names = new IdentifierList();
            names.add(new Identifier(SupervisorInfoMCAdapter.PARAM_NMF_VERSION));
            LongList ids = stub.listDefinition(names);
            Assert.assertEquals("Parameter must be defined: " + SupervisorInfoMCAdapter.PARAM_NMF_VERSION, 1, ids.size());
            ParameterValueDetailsList values = stub.getValue(ids);

            Assert.assertEquals("The Supervisor that came up must be the released version",
                    released, values.get(0).getValue().getRawValue().toString());
        } finally {
            adapter.closeConnections();
        }
    }

    /**
     * Runs one Bootloader invocation to completion, like the service manager
     * would, and returns its exit code.
     *
     * The Bootloader leaves the Supervisor behind when it manages to start it.
     * This test expects it never to, but a Supervisor that outlives the call
     * would hold the port and the single-instance lock and so bring down every
     * test that runs afterwards, which is a poor way to be told. Whatever is
     * left is therefore taken down before returning.
     */
    private static int runBootloaderOnce() throws IOException, InterruptedException {
        File nmfDir = filesystem.getNmfDir();
        ProcessBuilder pb = new ProcessBuilder("bash",
                new File(nmfDir, "start_supervisor.sh").getAbsolutePath());
        pb.directory(nmfDir);
        pb.redirectErrorStream(true);
        pb.redirectOutput(ProcessBuilder.Redirect.DISCARD);
        Process p = pb.start();
        Assert.assertTrue("The bootloader invocation did not terminate",
                p.waitFor(BOOTLOADER_TIMEOUT_SECONDS, TimeUnit.SECONDS));
        killLeftoverSupervisor();
        return p.exitValue();
    }

    /**
     * Takes down a Supervisor that a Bootloader invocation left running.
     */
    private static void killLeftoverSupervisor() {
        ProcessHandle.allProcesses()
                .filter(handle -> handle.info().commandLine()
                        .map(line -> line.contains(filesystem.getNmfDir().getAbsolutePath())
                                && line.contains("java"))
                        .orElse(false))
                .forEach(handle -> {
                    LOGGER.log(java.util.logging.Level.WARNING,
                            "A Bootloader invocation left a Supervisor running: {0}", handle.pid());
                    handle.destroyForcibly();
                });
    }

    private static int readMaxAttempts() throws IOException {
        Properties config = new Properties();
        File file = new File(new File(filesystem.getNmfDir(), Deployment.DIR_BOOTLOADER),
                Deployment.FILE_BOOTLOADER_CONFIG);
        try (FileInputStream in = new FileInputStream(file)) {
            config.load(in);
        }
        return Integer.parseInt(config.getProperty("boot-max-attempts", "2"));
    }

    private static Properties loadState() throws IOException {
        Properties state = new Properties();
        File file = new File(new File(filesystem.getNmfDir(), Deployment.DIR_BOOTLOADER),
                Deployment.FILE_BOOTLOADER_STATE);
        try (FileInputStream in = new FileInputStream(file)) {
            state.load(in);
        }
        return state;
    }

    private static void assertBootloaderSelected(final String role) {
        String expected = "BASELINE-SELECTION selected: " + role;
        for (String line : harness.getProviderLog()) {
            if (line.contains(expected)) {
                return;
            }
        }
        Assert.fail("The Bootloader must have selected the " + role + " baseline,"
                + " but its log does not say so.");
    }

    private static void assertBootloaderVerified(final String jars, final String version) {
        String expected = "INTEGRITY-TEST OK " + jars + "/" + version;
        for (String line : harness.getProviderLog()) {
            if (line.contains(expected)) {
                return;
            }
        }
        Assert.fail("The Bootloader must have verified " + jars + "/" + version
                + " before starting the Supervisor, but its log does not say so.");
    }

    private static GroundMOAdapterImpl connect() throws Exception {
        ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                new URI(harness.getDirectoryURI()));
        Assert.assertFalse("Directory must return a provider", providers.isEmpty());
        return new GroundMOAdapterImpl(providers.get(0));
    }
}
