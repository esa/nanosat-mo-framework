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
import esa.mo.nmf.testbed.e2e.PackageManagementHarness;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.sm.structures.PackageInstalled;
import org.ccsds.moims.mo.sm.structures.PackageUninstalled;
import org.ccsds.moims.mo.sm.structures.PackageUpgraded;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

/**
 * End-to-end tests for the Package Management lifecycle traceability: the
 * install, uninstall, and upgrade operations must store the corresponding
 * PackageInstalled, PackageUninstalled, and PackageUpgraded COM objects in
 * the archive, and must not store them when the operation is rejected.
 *
 * <p>
 * The tests mutate shared package state and therefore run in a fixed order
 * (uninstall, then reinstall, then upgrade), leaving the benchmark package
 * installed at the end.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PackageLifecycleTest extends NMFTest {

    private static final String UPGRADE_VERSION = "5.1";
    private static final String PACKAGE_PREFIX = "benchmark-";
    private static final String STORE_WARNING = "Could not store Package";

    private static final SupervisorHarness supervisorHarness = new SupervisorHarness();
    private static final PackageManagementHarness pm = new PackageManagementHarness(supervisorHarness);

    private static String benchmarkPackage;
    private static String initialVersion;
    private static String upgradedPackage;

    // Archive object ids present before the tests ran, so assertions are
    // immune to leftovers from previous runs on the same filesystem.
    private static Set<Long> installedBaseline;
    private static Set<Long> uninstalledBaseline;
    private static Set<Long> upgradedBaseline;

    @BeforeClass
    public static void startSupervisor() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        supervisorHarness.setUp();

        // Remove a stale upgrade fixture from a previous run on this filesystem
        File stale = new File(new File(supervisorHarness.getNmfDir(), "packages"),
                PACKAGE_PREFIX + UPGRADE_VERSION + ".nmfpack");
        if (stale.exists() && !stale.delete()) {
            throw new IOException("Could not delete stale fixture: " + stale.getAbsolutePath());
        }

        pm.connect();
        benchmarkPackage = pm.findPackageByPrefix(PACKAGE_PREFIX);
        // benchmark-5.0-SNAPSHOT.nmfpack -> 5.0-SNAPSHOT
        initialVersion = benchmarkPackage.substring(PACKAGE_PREFIX.length(),
                benchmarkPackage.lastIndexOf('.'));
        LOGGER.info("Testing with package '" + benchmarkPackage
                + "' (version " + initialVersion + ")");

        installedBaseline = ids(pm.queryPackageInstalled());
        uninstalledBaseline = ids(pm.queryPackageUninstalled());
        upgradedBaseline = ids(pm.queryPackageUpgraded());
    }

    @AfterClass
    public static void stopSupervisor() throws IOException {
        supervisorHarness.tearDown();
    }

    // -------------------------------------------------------------------------
    // Test 1 — Uninstall stores a PackageUninstalled object
    // -------------------------------------------------------------------------

    @Test
    public void test1_UninstallStoresPackageUninstalled() throws Exception {
        LOGGER.info(SEP + "\nRunning: test1_UninstallStoresPackageUninstalled()\n" + SEP);

        MOErrorException error = pm.uninstall(benchmarkPackage, true);
        Assert.assertNull("uninstall must succeed but returned: " + error, error);

        List<ArchivePersistenceObject> records = newRecords(
                pm.queryPackageUninstalled(), uninstalledBaseline);
        Assert.assertEquals("Exactly one PackageUninstalled record must be stored",
                1, records.size());

        PackageUninstalled body = (PackageUninstalled) records.get(0).getObject();
        Assert.assertEquals("packageName", benchmarkPackage, body.getPackageName().getValue());
        Assert.assertEquals("version must be the version before uninstalling",
                initialVersion, body.getVersion());
        Assert.assertEquals("keptConfigurations", Boolean.TRUE, body.getKeptConfigurations());
        Assert.assertNotNull("triggeredBy must identify the requesting consumer",
                body.getTriggeredBy());
    }

    // -------------------------------------------------------------------------
    // Test 2 — Install stores a PackageInstalled object
    // -------------------------------------------------------------------------

    @Test
    public void test2_InstallStoresPackageInstalled() throws Exception {
        LOGGER.info(SEP + "\nRunning: test2_InstallStoresPackageInstalled()\n" + SEP);

        MOErrorException error = pm.install(benchmarkPackage);
        Assert.assertNull("install must succeed but returned: " + error, error);

        List<ArchivePersistenceObject> records = newRecords(
                pm.queryPackageInstalled(), installedBaseline);
        Assert.assertEquals("Exactly one PackageInstalled record must be stored",
                1, records.size());

        PackageInstalled body = (PackageInstalled) records.get(0).getObject();
        Assert.assertEquals("packageName", benchmarkPackage, body.getPackageName().getValue());
        Assert.assertEquals("version", initialVersion, body.getVersion());
        Assert.assertNotNull("triggeredBy must identify the requesting consumer",
                body.getTriggeredBy());
    }

    // -------------------------------------------------------------------------
    // Test 3 — Upgrade stores a PackageUpgraded object with both versions
    // -------------------------------------------------------------------------

    @Test
    public void test3_UpgradeStoresPackageUpgraded() throws Exception {
        LOGGER.info(SEP + "\nRunning: test3_UpgradeStoresPackageUpgraded()\n" + SEP);

        upgradedPackage = pm.createUpgradedPackage(benchmarkPackage, UPGRADE_VERSION);

        MOErrorException error = pm.upgrade(upgradedPackage);
        Assert.assertNull("upgrade must succeed but returned: " + error, error);

        List<ArchivePersistenceObject> records = newRecords(
                pm.queryPackageUpgraded(), upgradedBaseline);
        Assert.assertEquals("Exactly one PackageUpgraded record must be stored",
                1, records.size());

        PackageUpgraded body = (PackageUpgraded) records.get(0).getObject();
        Assert.assertEquals("packageName", upgradedPackage, body.getPackageName().getValue());
        Assert.assertEquals("fromVersion must be the version before the upgrade",
                initialVersion, body.getFromVersion());
        Assert.assertEquals("toVersion must be the version after the upgrade",
                UPGRADE_VERSION, body.getToVersion());
        Assert.assertNotNull("triggeredBy must identify the requesting consumer",
                body.getTriggeredBy());
    }

    // -------------------------------------------------------------------------
    // Test 4 — Installing an already-installed package stores nothing
    // -------------------------------------------------------------------------

    @Test
    public void test4_InstallAlreadyInstalledStoresNothing() throws Exception {
        LOGGER.info(SEP + "\nRunning: test4_InstallAlreadyInstalledStoresNothing()\n" + SEP);

        int recordsBefore = pm.queryPackageInstalled().size();

        // The package installed right now is the upgraded one (test3)
        MOErrorException error = pm.install(upgradedPackage);
        Assert.assertNotNull("Installing an already-installed package must return an error", error);

        Assert.assertEquals("No additional PackageInstalled record may be stored",
                recordsBefore, pm.queryPackageInstalled().size());
    }

    // -------------------------------------------------------------------------
    // Test 5 — Uninstalling an unknown package stores nothing
    // -------------------------------------------------------------------------

    @Test
    public void test5_UninstallUnknownPackageStoresNothing() throws Exception {
        LOGGER.info(SEP + "\nRunning: test5_UninstallUnknownPackageStoresNothing()\n" + SEP);

        int recordsBefore = pm.queryPackageUninstalled().size();

        MOErrorException error = pm.uninstall("nonexistent-9.9.nmfpack", false);
        Assert.assertNotNull("Uninstalling an unknown package must return an error", error);

        Assert.assertEquals("No additional PackageUninstalled record may be stored",
                recordsBefore, pm.queryPackageUninstalled().size());
    }

    // -------------------------------------------------------------------------
    // Test 6 — No archive-store warnings in the Supervisor log
    // -------------------------------------------------------------------------

    @Test
    public void test6_NoArchiveStoreWarningsInSupervisorLog() {
        LOGGER.info(SEP + "\nRunning: test6_NoArchiveStoreWarningsInSupervisorLog()\n" + SEP);

        List<String> hits = supervisorHarness.getProviderLog().stream()
                .filter(line -> line.contains(STORE_WARNING))
                .collect(Collectors.toList());

        Assert.assertTrue("Supervisor log must not contain archive-store warnings, found: "
                + hits, hits.isEmpty());
    }

    private static Set<Long> ids(List<ArchivePersistenceObject> records) {
        Set<Long> ids = new HashSet<>();
        for (ArchivePersistenceObject record : records) {
            ids.add(record.getArchiveDetails().getId());
        }
        return ids;
    }

    private static List<ArchivePersistenceObject> newRecords(
            List<ArchivePersistenceObject> records, Set<Long> baseline) {
        return records.stream()
                .filter(r -> !baseline.contains(r.getArchiveDetails().getId()))
                .collect(Collectors.toList());
    }
}
