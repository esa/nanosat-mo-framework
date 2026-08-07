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
import java.io.IOException;
import java.util.Properties;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
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
 * End-to-end test of an upgrade of the NMF and of the way back from it: the
 * Supervisor is booted on the version of the last release, upgraded to the
 * version under development, and then rolled back to the release again.
 *
 * Both versions are laid down side by side, and only the primary baseline is
 * moved between them, which is what an upgrade amounts to on a spacecraft.
 *
 * Every step is held to what is actually running rather than to what was asked
 * for. The baseline file only says which version was chosen, so it is read back
 * along with three things that the running Supervisor itself answers with:
 * <ul>
 * <li>the version directories that the Bootloader verified before starting it,
 * <li>the {@code nmf.version} parameter, which the Supervisor reads out of the
 * manifest of the Jar it was loaded from, so it cannot agree with the baseline
 * unless the right Jars were really used,
 * <li>the {@code nmf.uptime} parameter, which is only small when the process
 * was replaced rather than left running.
 * </ul>
 *
 * The filesystem is a copy of the generated one, so that the other tests are
 * left with the single version they are written for.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NMFUpgradeRollbackTest extends NMFTest {


    /**
     * A Supervisor that has just been started cannot have been up for longer
     * than this. Generous, because a loaded build machine is still a restart.
     */
    private static final double MAX_UPTIME_AFTER_RESTART_SECONDS = 60.0;

    private static UpgradeFilesystemHarness filesystem;
    private static SupervisorHarness harness;

    @BeforeClass
    public static void setUpClass() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        filesystem = new UpgradeFilesystemHarness(
                new File(System.getProperty(UpgradeFilesystemHarness.PROP_FILESYSTEM_UPGRADE)));
        LOGGER.log(java.util.logging.Level.INFO, "Upgrading from {0} to {1}",
                new Object[]{filesystem.getBaselineVersion(), filesystem.getDevelopmentVersion()});
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        stopSupervisor();
    }

    // Test 1 — The Supervisor boots on the version of the last release

    @Test
    public void test1_BaselineRunsTheReleasedVersion() throws Exception {
        LOGGER.info(SEP + "\nRunning: test1_BaselineRunsTheReleasedVersion()\n" + SEP);
        bootOn(filesystem.getBaselineVersion());
        assertRunning(filesystem.getBaselineVersion());
    }

    // Test 2 — Moving the primary baseline upgrades it to the version under
    // development

    @Test
    public void test2_UpgradeRunsTheDevelopmentVersion() throws Exception {
        LOGGER.info(SEP + "\nRunning: test2_UpgradeRunsTheDevelopmentVersion()\n" + SEP);
        bootOn(filesystem.getDevelopmentVersion());
        assertRunning(filesystem.getDevelopmentVersion());
    }

    // Test 3 — Moving it back rolls the upgrade back to the released version

    @Test
    public void test3_RollbackReturnsToTheReleasedVersion() throws Exception {
        LOGGER.info(SEP + "\nRunning: test3_RollbackReturnsToTheReleasedVersion()\n" + SEP);
        bootOn(filesystem.getBaselineVersion());
        assertRunning(filesystem.getBaselineVersion());
    }

    // Test 4 — The version that is rolled back to is still the one running,
    // and the other version is still there to be upgraded to again

    @Test
    public void test4_BothVersionsRemainInstalled() {
        LOGGER.info(SEP + "\nRunning: test4_BothVersionsRemainInstalled()\n" + SEP);
        for (String version : new String[]{filesystem.getBaselineVersion(),
            filesystem.getDevelopmentVersion()}) {
            for (String jars : new String[]{Deployment.DIR_JARS_NMF, Deployment.DIR_JARS_MISSION}) {
                File dir = new File(new File(filesystem.getNmfDir(), jars), version);
                Assert.assertTrue("A rollback must leave both versions installed, missing: "
                        + dir.getAbsolutePath(), dir.isDirectory());
            }
        }
    }

    /**
     * Points the primary baseline at a version and boots the Supervisor on it,
     * from a state that carries nothing over from the boot before.
     */
    private static void bootOn(final String version) throws IOException {
        stopSupervisor();
        filesystem.setBaselineVersion(Deployment.ROLE_PRIMARY, version);
        filesystem.clearBootState();

        harness = new SupervisorHarness();
        harness.setUp(filesystem.getNmfDir());
    }

    private static void stopSupervisor() throws IOException {
        if (harness != null) {
            harness.tearDown();
            harness = null;
        }
    }

    /**
     * Holds the running Supervisor to a version, from the baseline that chose
     * it through to what the Supervisor itself answers with.
     */
    private static void assertRunning(final String version) throws Exception {
        Properties baseline = filesystem.readBaseline(Deployment.ROLE_PRIMARY);
        Assert.assertEquals("The primary baseline must name the version",
                version, baseline.getProperty(SoftwareBaseline.KEY_NMF_VERSION));
        Assert.assertEquals("The primary baseline must name the mission version",
                version, baseline.getProperty(SoftwareBaseline.KEY_MISSION_VERSION));

        // What the Bootloader verified before it started the Supervisor
        assertBootloaderVerified(Deployment.DIR_JARS_NMF, version);
        assertBootloaderVerified(Deployment.DIR_JARS_MISSION, version);

        Assert.assertTrue("The Supervisor must report a successful boot",
                filesystem.isBootConfirmed());

        // What the Supervisor answers with, over MO
        GroundMOAdapterImpl adapter = connect();
        try {
            ParameterStub stub = adapter.getMCServices().getParameterService().getParameterStub();

            Assert.assertEquals("The running Supervisor must report the version of the Jars"
                    + " it was loaded from", version, readParameter(stub, SupervisorInfoMCAdapter.PARAM_NMF_VERSION).toString());

            double uptime = Double.parseDouble(readParameter(stub, SupervisorInfoMCAdapter.PARAM_NMF_UPTIME).toString());
            Assert.assertTrue("The Supervisor must have been restarted, but it has been up for "
                    + uptime + " seconds", uptime < MAX_UPTIME_AFTER_RESTART_SECONDS);
        } finally {
            adapter.closeConnections();
        }
    }

    /**
     * Holds the Bootloader to having verified the version directory that the
     * baseline named, which is where the Jars it started come from.
     */
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

    private static GroundMOAdapterImpl connect()
            throws IOException, MALInteractionException, MALException {
        ProviderList providers = NMFConsumer.retrieveProvidersFromDirectory(
                new URI(harness.getDirectoryURI()));
        Assert.assertFalse("Directory must return a provider", providers.isEmpty());
        return new GroundMOAdapterImpl(providers.get(0));
    }

    private static Object readParameter(final ParameterStub stub, final String name)
            throws MALInteractionException, MALException {
        IdentifierList names = new IdentifierList();
        names.add(new Identifier(name));
        LongList ids = stub.listDefinition(names);
        Assert.assertEquals("Parameter must be defined: " + name, 1, ids.size());
        ParameterValueDetailsList values = stub.getValue(ids);
        Assert.assertEquals("Parameter must have a value: " + name, 1, values.size());
        return values.get(0).getValue().getRawValue();
    }
}
