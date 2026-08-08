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
import esa.mo.nmf.testbed.e2e.FilesystemHarness;
import esa.mo.nmf.testbed.e2e.PackageManagementHarness;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * Installs an App, takes it back to a previously released version, and brings
 * it forward again.
 *
 * <p>
 * The Package Management service has no rollback operation. Going back to an
 * older version is the {@code upgrade} operation pointed at an older package:
 * it compares no versions and simply replaces what is installed with what the
 * package holds, so it moves in either direction. The name is the only thing
 * that suggests otherwise.
 *
 * <p>
 * What this test pins down is that the way back really works, and that the App
 * it leaves behind starts. The second half is the part worth having, because
 * the operation deletes the files of the installed version before it writes the
 * older ones, so a rollback that only half worked would still leave a receipt
 * claiming success.
 *
 * <p>
 * The version rolled back to is the released package taken from the repository,
 * so the test rolls back to the very bytes that were published rather than to a
 * rebuild of them. It is a release of the same major version as the framework
 * under test, which is the only case the NMF supports: an App is compiled
 * against one version of the framework and linked against whichever one the
 * Bootloader selected, so the major versions have to match.
 *
 * <p>
 * The test works on a copy of the filesystem, because it leaves the App on
 * whichever version it finished with and the other tests are written for the
 * one the filesystem was generated with.
 *
 * @author Cesar Coelho
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppRollbackTest extends NMFTest {

    /** Name of the App taken back and forth between versions. */
    private static final String APP = "benchmark";
    private static final String PACKAGE_PREFIX = APP + "-";
    private static final String PACKAGE_ENDING = ".nmfpack";
    private static final String RECEIPT_ENDING = ".receipt";
    private static final String KEY_VERSION = "info.version";

    /** Points at the copy of the filesystem that this test works on. */
    private static final String PROP_FILESYSTEM_ROLLBACK = "nmf.e2e.filesystem.rollback";

    /** Points at the directory the released App package is staged in. */
    private static final String PROP_BASELINE_APPS = "nmf.e2e.baseline.apps";

    /** Names the version of the App under development. */
    private static final String PROP_CURRENT_VERSION = "nmf.e2e.current.version";

    private static final SupervisorHarness supervisorHarness = new SupervisorHarness();
    private static final PackageManagementHarness pm = new PackageManagementHarness(supervisorHarness);
    private static FilesystemHarness filesystem;
    private static AppHarness app;

    /** The version under development, installed first and returned to last. */
    private static String currentPackage;
    private static String currentVersion;

    /** The released version, the one rolled back to. */
    private static String releasedPackage;
    private static String releasedVersion;

    private static Set<Long> installedBaseline;
    private static Set<Long> upgradedBaseline;

    @BeforeClass
    public static void startSupervisor() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);

        String destination = System.getProperty(PROP_FILESYSTEM_ROLLBACK);
        if (destination == null) {
            throw new IOException("System property '" + PROP_FILESYSTEM_ROLLBACK + "' is not set. "
                    + "Run via Maven (mvn test) so the filesystem is generated first.");
        }
        filesystem = new FilesystemHarness(new File(destination));

        // The version under development is named by Maven rather than read from
        // whatever the copy happens to have installed: the tests that run
        // before this one leave the App upgraded to versions of their own,
        // which this one would otherwise inherit through the copy.
        currentVersion = System.getProperty(PROP_CURRENT_VERSION);
        if (currentVersion == null) {
            throw new IOException("System property '" + PROP_CURRENT_VERSION + "' is not set. "
                    + "Run via Maven (mvn test) so the version under development is named.");
        }
        currentPackage = PACKAGE_PREFIX + currentVersion + PACKAGE_ENDING;
        releasedPackage = stageReleasedPackage();
        releasedVersion = versionOf(releasedPackage);

        LOGGER.info("Installing '" + APP + "' " + currentVersion + ", rolling it back to "
                + releasedVersion + ", and forward again");

        supervisorHarness.setUp(filesystem.getNmfDir());
        pm.connect();

        installedBaseline = ids(pm.queryPackageInstalled());
        upgradedBaseline = ids(pm.queryPackageUpgraded());
        app = new AppHarness(APP, supervisorHarness);
    }

    @AfterClass
    public static void stopSupervisor() throws IOException {
        supervisorHarness.tearDown();
    }

    // -------------------------------------------------------------------------
    // Test 1 — The two versions are different, so the test can prove something
    // -------------------------------------------------------------------------
    @Test
    public void test1_TheTwoVersionsDiffer() {
        LOGGER.info(SEP + "\nRunning: test1_TheTwoVersionsDiffer()\n" + SEP);

        Assert.assertNotEquals("The version rolled back to must differ from the one "
                + "installed, otherwise the test proves nothing",
                currentVersion, releasedVersion);
    }

    // -------------------------------------------------------------------------
    // Test 2 — Uninstalling and installing the version under development
    // -------------------------------------------------------------------------
    @Test
    public void test2_InstallCurrentVersion() throws Exception {
        LOGGER.info(SEP + "\nRunning: test2_InstallCurrentVersion()\n" + SEP);

        // Whatever version the copy came with is removed, and not the one about
        // to be installed: uninstalling wants a receipt of the same name and
        // version, and the tests that ran before this one may have left the App
        // on a version of their own.
        String installedNow = installedVersion();
        MOErrorException error = pm.uninstall(
                PACKAGE_PREFIX + installedNow + PACKAGE_ENDING, false);
        Assert.assertNull("Uninstalling the installed version " + installedNow
                + " must succeed but returned: " + error, error);
        Assert.assertNull("Nothing must be installed after uninstalling", installedVersion());

        error = pm.install(currentPackage);
        Assert.assertNull("Installing '" + currentPackage
                + "' must succeed but returned: " + error, error);

        Set<Long> after = ids(pm.queryPackageInstalled());
        after.removeAll(installedBaseline);
        Assert.assertFalse("Installing must store a PackageInstalled object", after.isEmpty());
        Assert.assertEquals("The receipt must name the version installed",
                currentVersion, installedVersion());
    }

    // -------------------------------------------------------------------------
    // Test 3 — The freshly installed App runs
    // -------------------------------------------------------------------------
    @Test
    public void test3_InstalledAppRuns() throws Exception {
        LOGGER.info(SEP + "\nRunning: test3_InstalledAppRuns()\n" + SEP);

        assertRuns(currentVersion);
    }

    // -------------------------------------------------------------------------
    // Test 4 — Rolling back to the released version
    // -------------------------------------------------------------------------
    @Test
    public void test4_RollbackToReleasedVersion() throws Exception {
        LOGGER.info(SEP + "\nRunning: test4_RollbackToReleasedVersion()\n" + SEP);

        MOErrorException error = pm.upgrade(releasedPackage);
        Assert.assertNull("Rolling back to '" + releasedPackage
                + "' must succeed but returned: " + error, error);

        Set<Long> after = ids(pm.queryPackageUpgraded());
        after.removeAll(upgradedBaseline);
        Assert.assertFalse("Rolling back must store a PackageUpgraded object", after.isEmpty());

        Assert.assertEquals("The receipt must name the version rolled back to",
                releasedVersion, installedVersion());
        assertOnlyJarInstalled(releasedVersion, currentVersion);
    }

    // -------------------------------------------------------------------------
    // Test 5 — The rolled back App runs
    // -------------------------------------------------------------------------
    @Test
    public void test5_RolledBackAppRuns() throws Exception {
        LOGGER.info(SEP + "\nRunning: test5_RolledBackAppRuns()\n" + SEP);

        assertRuns(releasedVersion);
    }

    // -------------------------------------------------------------------------
    // Test 6 — Rolling forward again to the version under development
    // -------------------------------------------------------------------------
    @Test
    public void test6_RollForwardToCurrentVersion() throws Exception {
        LOGGER.info(SEP + "\nRunning: test6_RollForwardToCurrentVersion()\n" + SEP);

        MOErrorException error = pm.upgrade(currentPackage);
        Assert.assertNull("Rolling forward to '" + currentPackage
                + "' must succeed but returned: " + error, error);
        Assert.assertEquals("The receipt must name the version rolled forward to",
                currentVersion, installedVersion());
        assertOnlyJarInstalled(currentVersion, releasedVersion);

        assertRuns(currentVersion);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    /**
     * Starts the App and asserts that it runs, having first asserted which
     * version is installed. Without that assertion the App would run either
     * way, and the test would pass while proving nothing about the version it
     * claims to have started.
     */
    private static void assertRuns(final String version) throws IOException {
        Assert.assertEquals("The App of version " + version + " must be the one started here",
                version, installedVersion());

        app.connect();
        app.runApp();
        Assert.assertTrue("The App must run at version " + version, app.isRunning());
        app.stop(30000);
    }

    /**
     * Asserts that the Jar of one version is installed and that of the other is
     * gone. The half-done rollback this guards against is the new files being
     * written while the old ones are left in place, which leaves two versions
     * of the App in one directory.
     */
    private static void assertOnlyJarInstalled(final String present, final String absent) {
        File appDir = new File(new File(filesystem.getNmfDir(), "apps"), APP);
        Assert.assertTrue("The Jar of version " + present + " must be installed",
                new File(appDir, APP + "-" + present + ".jar").exists());
        Assert.assertFalse("The Jar of version " + absent + " must be gone",
                new File(appDir, APP + "-" + absent + ".jar").exists());
    }

    /**
     * Copies the released App package staged by Maven into the packages folder
     * of the copied filesystem, where the Package Management service looks for
     * it.
     *
     * @return The file name of the staged package.
     */
    private static String stageReleasedPackage() throws IOException {
        String staging = System.getProperty(PROP_BASELINE_APPS);
        if (staging == null) {
            throw new IOException("System property '" + PROP_BASELINE_APPS + "' is not set. "
                    + "Run via Maven (mvn test) so the released package is staged first.");
        }

        File[] staged = new File(staging).listFiles(
                (dir, name) -> name.startsWith(PACKAGE_PREFIX) && name.endsWith(PACKAGE_ENDING));
        if (staged == null || staged.length != 1) {
            throw new IOException("Exactly one staged '" + PACKAGE_PREFIX + "' package was "
                    + "expected in: " + staging);
        }

        File packages = new File(filesystem.getNmfDir(), "packages");
        File target = new File(packages, staged[0].getName());
        Files.copy(staged[0].toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return target.getName();
    }

    /**
     * Returns the version that the installation receipt of the App names, which
     * is what the Package Manager considers installed.
     *
     * @return The installed version, or {@code null} if there is no receipt.
     */
    private static String installedVersion() {
        File receipt = new File(new File(new File(filesystem.getNmfDir(), "etc"),
                "installations-tracker"), APP + RECEIPT_ENDING);
        if (!receipt.exists()) {
            return null;
        }

        Properties props = new Properties();
        try (InputStream in = new FileInputStream(receipt)) {
            props.load(in);
        } catch (IOException ex) {
            throw new IllegalStateException("The receipt could not be read: " + receipt, ex);
        }
        return props.getProperty(KEY_VERSION);
    }

    /**
     * Takes the version out of a package file name, as in
     * {@code benchmark-5.0.nmfpack} to {@code 5.0}.
     */
    private static String versionOf(final String packageFileName) {
        return packageFileName.substring(PACKAGE_PREFIX.length(),
                packageFileName.length() - PACKAGE_ENDING.length());
    }

    private static Set<Long> ids(List<ArchivePersistenceObject> records) {
        Set<Long> ids = new HashSet<>();
        for (ArchivePersistenceObject record : records) {
            ids.add(record.getArchiveDetails().getId());
        }
        return ids;
    }
}
