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
import esa.mo.nmf.environment.SoftwareBaseline;
import esa.mo.nmf.nmfpackage.NMFPackageBuilder;
import esa.mo.nmf.nmfpackage.metadata.MetadataMission;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import esa.mo.nmf.testbed.e2e.PackageManagementHarness;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import static esa.mo.nmf.testbed.e2e.tests.SharedOutput.LOGGER;
import static esa.mo.nmf.testbed.e2e.tests.SharedOutput.SEP;
import static esa.mo.nmf.testbed.e2e.tests.SharedOutput.SETUP_CLASS_SEP;
import static esa.mo.nmf.testbed.e2e.tests.SharedOutput.SETUP_CLASS_MSG;

/**
 * Installs a mission software baseline and holds where it lands.
 *
 * A mission baseline is one of the two halves of what the bootloader starts.
 * It is carried in a package of type mission, and installing one has to put
 * its Jars in jars-mission under a directory named after its own version,
 * beside the version that is running rather than over it: the running one has
 * to stay bootable, because a baseline is activated later and deliberately,
 * with the bootloader.setPrimaryBaseline action, and has to be fallen back to
 * if it does not come up.
 *
 * The package is built here rather than taken from a mission. Nothing is
 * released to upgrade from, this being the first version of the framework to
 * carry mission baselines at all, and the thing under test is where the files
 * go and what is written about them, not what the Jars contain.
 *
 * The version installed is not the version running, so this also holds the
 * half of it that matters for a mission: that a mission version can arrive
 * without the framework version moving, the two being read separately by the
 * bootloader and joined only on the classpath.
 */
public class MissionBaselineInstallTest {

    /** The version to install, which is not the one the filesystem runs. */
    private static final String NEW_MISSION_VERSION = "9.9.9-test";

    private static final String PACKAGE_NAME = "a-mission-baseline";

    private static final SupervisorHarness supervisorHarness = new SupervisorHarness();
    private static final PackageManagementHarness pm = new PackageManagementHarness(supervisorHarness);

    private static File nmfDir;
    private static String runningMissionVersion;
    private static String runningNmfVersion;

    @BeforeClass
    public static void startSupervisor() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        supervisorHarness.setUp();
        nmfDir = supervisorHarness.getNmfDir();

        Properties primary = new Properties();
        try (java.io.InputStream in = Files.newInputStream(new File(new File(nmfDir,
                Deployment.DIR_BOOTLOADER), "baseline-primary.properties").toPath())) {
            primary.load(in);
        }
        runningMissionVersion = primary.getProperty(SoftwareBaseline.KEY_MISSION_VERSION);
        runningNmfVersion = primary.getProperty(SoftwareBaseline.KEY_NMF_VERSION);

        Assert.assertNotNull("The filesystem has to name the mission version it runs",
                runningMissionVersion);
        Assert.assertNotEquals("The version installed has to differ from the one running, or "
                + "nothing is proved about landing beside it", runningMissionVersion,
                NEW_MISSION_VERSION);

        stageMissionPackage();
        pm.connect();
    }

    @AfterClass
    public static void stopSupervisor() throws IOException {
        supervisorHarness.tearDown();
        removeWhatWasInstalled();
    }

    /**
     * Puts the filesystem back as it was found.
     * <p>
     * The tests of this module share one generated filesystem, and one of them
     * walks every version directory of jars-mission and holds that each carries
     * a checksum manifest. A baseline left behind here would be walked by it,
     * so what this test installed is taken away again.
     */
    private static void removeWhatWasInstalled() throws IOException {
        File installed = new File(new File(nmfDir, Deployment.DIR_JARS_MISSION), NEW_MISSION_VERSION);
        deleteTree(installed);

        File packages = new File(nmfDir, Deployment.DIR_PACKAGES);
        File[] staged = packages.listFiles((dir, name) -> name.startsWith(PACKAGE_NAME));

        if (staged != null) {
            for (File file : staged) {
                Files.deleteIfExists(file.toPath());
            }
        }

        LOGGER.info("The mission baseline this test installed was removed again");
    }

    private static void deleteTree(File file) throws IOException {
        if (!file.exists()) {
            return;
        }

        File[] children = file.listFiles();

        if (children != null) {
            for (File child : children) {
                deleteTree(child);
            }
        }
        Files.deleteIfExists(file.toPath());
    }

    /**
     * Builds a mission package of a version of its own and puts it where the
     * Supervisor looks for packages to install.
     */
    private static void stageMissionPackage() throws IOException {
        File work = Files.createTempDirectory("mission-baseline-fixture").toFile();
        File jar = new File(work, "a-mission-" + NEW_MISSION_VERSION + ".jar");
        Files.write(jar.toPath(), "a mission baseline".getBytes(StandardCharsets.UTF_8));

        NMFPackageBuilder builder = new NMFPackageBuilder(
                new MetadataMission(PACKAGE_NAME, NEW_MISSION_VERSION));
        builder.addFileOrDirectory(jar);
        File built = builder.createPackage(work);

        Assert.assertNotNull("The fixture package has to be built", built);

        File packages = new File(nmfDir, Deployment.DIR_PACKAGES);
        Files.createDirectories(packages.toPath());
        Files.copy(built.toPath(), new File(packages, built.getName()).toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);

        LOGGER.info("Staged the mission package: " + built.getName());
    }

    @Test
    public void aMissionBaselineLandsInItsOwnVersionedDirectory() throws Exception {
        LOGGER.info(SEP + "\nRunning: aMissionBaselineLandsInItsOwnVersionedDirectory()\n" + SEP);

        String packageFile = pm.findPackageByPrefix(PACKAGE_NAME);
        MOErrorException error = pm.install(packageFile);
        Assert.assertNull("Installing a mission baseline must succeed, but returned: " + error, error);

        File missionJars = new File(new File(nmfDir, Deployment.DIR_JARS_MISSION), NEW_MISSION_VERSION);
        Assert.assertTrue("The baseline must be laid down in jars-mission/" + NEW_MISSION_VERSION
                + ", which is where the bootloader looks for it", missionJars.isDirectory());
        Assert.assertTrue("The Jar of the baseline must be in it",
                new File(missionJars, "a-mission-" + NEW_MISSION_VERSION + ".jar").isFile());
    }

    @Test
    public void theRunningBaselineIsLeftWhereItIs() {
        LOGGER.info(SEP + "\nRunning: theRunningBaselineIsLeftWhereItIs()\n" + SEP);

        File running = new File(new File(nmfDir, Deployment.DIR_JARS_MISSION), runningMissionVersion);
        Assert.assertTrue("The mission version that is running must still be there to be "
                + "booted again, installing another beside it", running.isDirectory());

        File frameworkRunning = new File(new File(nmfDir, Deployment.DIR_JARS_NMF), runningNmfVersion);
        Assert.assertTrue("And the framework underneath it must not have moved: a mission "
                + "baseline is versioned on its own", frameworkRunning.isDirectory());
    }

    @Test
    public void theBaselineIsGivenItsChecksums() {
        LOGGER.info(SEP + "\nRunning: theBaselineIsGivenItsChecksums()\n" + SEP);

        File missionJars = new File(new File(nmfDir, Deployment.DIR_JARS_MISSION), NEW_MISSION_VERSION);
        File checksums = new File(missionJars, ChecksumGenerator.CHECKSUMS_FILENAME);

        Assert.assertTrue("A baseline directory must carry the manifest the bootloader verifies "
                + "it against; the Package Manager writes it on install rather than trusting one "
                + "shipped in the package", checksums.isFile());
    }
}
