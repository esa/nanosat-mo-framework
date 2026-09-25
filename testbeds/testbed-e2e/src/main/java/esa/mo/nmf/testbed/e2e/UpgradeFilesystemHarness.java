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
package esa.mo.nmf.testbed.e2e;

import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.environment.SoftwareBaseline;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

/**
 * A filesystem holding two versions of the NMF, so that an upgrade from one to
 * the other, and the way back, can be exercised.
 *
 * The tests that boot a second version have to change the baseline files and
 * to leave a Supervisor of another version behind, which the other tests are
 * not written for. This harness therefore works on a copy of the generated
 * filesystem, taken by {@link FilesystemHarness}, and adds the second version
 * to that copy.
 *
 * Note that the second version is added under {@code jars-nmf/<version>}, which
 * is what an upgrade does, but which also puts two versions of the framework
 * where an App start script looks for one. A test that is not about the NMF
 * baseline should therefore take a plain {@link FilesystemHarness} instead of
 * this one.
 *
 * @author Cesar Coelho
 */
public class UpgradeFilesystemHarness extends FilesystemHarness {

    /**
     * Points at the filesystem that the upgrade tests work on, the copy that
     * holds both versions.
     */
    public static final String PROP_FILESYSTEM_UPGRADE = "nmf.e2e.filesystem.upgrade";

    /**
     * Points at the directory holding the Jars of the version to upgrade from.
     */
    public static final String PROP_BASELINE_JARS = "nmf.e2e.baseline.jars";

    /**
     * The version to upgrade from, the one those Jars carry.
     */
    public static final String PROP_BASELINE_VERSION = "nmf.e2e.baseline.version";

    /**
     * The Jar of the mission, which goes to jars-mission rather than jars-nmf.
     */
    private static final String MISSION_JAR_PREFIX = "barebone-nanosat-mo-supervisor";

    private final String baselineVersion;
    private final String developmentVersion;

    /**
     * Takes a copy of the generated filesystem and adds the version to upgrade
     * from to it, so that the copy holds both versions.
     *
     * @param destination Where to place the copy.
     * @throws IOException if the filesystem cannot be copied, the Jars of the
     * version to upgrade from are not staged, or the copy cannot be written.
     */
    public UpgradeFilesystemHarness(final File destination) throws IOException {
        super(destination);

        String jars = System.getProperty(PROP_BASELINE_JARS);
        if (jars == null) {
            throw new IOException("System property '" + PROP_BASELINE_JARS + "' is not set. "
                    + "Run via Maven (mvn test) so the Jars to upgrade from are staged first.");
        }

        this.baselineVersion = System.getProperty(PROP_BASELINE_VERSION);
        if (baselineVersion == null) {
            throw new IOException("System property '" + PROP_BASELINE_VERSION + "' is not set.");
        }

        // The version that the filesystem was generated with is the one to
        // upgrade to, and it is already named by the baseline it carries.
        this.developmentVersion = readBaseline(Deployment.ROLE_PRIMARY).getProperty(SoftwareBaseline.KEY_NMF_VERSION);
        if (developmentVersion == null) {
            throw new IOException("The copied filesystem has no nmf-version in its primary baseline.");
        }

        addBaselineVersion(new File(jars));
    }

    /**
     * Lays the Jars of the version to upgrade from into their own version
     * directories, with the checksums that the Bootloader verifies at boot.
     */
    private void addBaselineVersion(final File jars) throws IOException {
        File[] files = jars.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0) {
            throw new IOException("No Jars to upgrade from were found in: " + jars.getAbsolutePath());
        }

        File nmfJars = new File(new File(nmfDir, Deployment.DIR_JARS_NMF), baselineVersion);
        File missionJars = new File(new File(nmfDir, Deployment.DIR_JARS_MISSION), baselineVersion);
        Files.createDirectories(nmfJars.toPath());
        Files.createDirectories(missionJars.toPath());

        for (File jar : files) {
            File target = jar.getName().startsWith(MISSION_JAR_PREFIX) ? missionJars : nmfJars;
            Files.copy(jar.toPath(), new File(target, jar.getName()).toPath(),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        // Without these the Bootloader fails the integrity test and never boots
        ChecksumGenerator.writeChecksumsFile(nmfJars);
        ChecksumGenerator.writeChecksumsFile(missionJars);
    }

    /**
     * Returns the version that is upgraded from, the one taken from a release.
     *
     * @return The version to upgrade from.
     */
    public String getBaselineVersion() {
        return baselineVersion;
    }

    /**
     * Returns the version that is upgraded to, the one under development that
     * the filesystem was generated with.
     *
     * @return The version to upgrade to.
     */
    public String getDevelopmentVersion() {
        return developmentVersion;
    }

    /**
     * Reads one of the baseline files.
     *
     * @param role The role of the baseline: primary, secondary or factory.
     * @return The baseline that the file holds.
     * @throws IOException if the file cannot be read.
     */
    public Properties readBaseline(final String role) throws IOException {
        Properties props = new Properties();
        try (FileInputStream in = new FileInputStream(baselineFile(role))) {
            props.load(in);
        }
        return props;
    }

    /**
     * Points a baseline at a version, leaving the rest of it as it is.
     *
     * @param role The role of the baseline: primary, secondary or factory.
     * @param version The version to point it at.
     * @throws IOException if the file cannot be read or written.
     */
    public void setBaselineVersion(final String role, final String version) throws IOException {
        Properties baseline = readBaseline(role);
        baseline.setProperty(SoftwareBaseline.KEY_NMF_VERSION, version);
        baseline.setProperty(SoftwareBaseline.KEY_MISSION_VERSION, version);
        store(role, baseline);
    }

    /**
     * Sets the class that a baseline starts, used to point one at a class that
     * cannot be found so that the boot fails.
     *
     * @param role The role of the baseline: primary, secondary or factory.
     * @param mainClass The class to start.
     * @throws IOException if the file cannot be read or written.
     */
    public void setBaselineMainClass(final String role, final String mainClass) throws IOException {
        Properties baseline = readBaseline(role);
        baseline.setProperty("main-class", mainClass);
        store(role, baseline);
    }

    /**
     * Forgets which rung of the fallback ladder was reached and whether the
     * last boot was confirmed, so that the next boot starts from the primary.
     *
     * @throws IOException if the files cannot be deleted.
     */
    public void clearBootState() throws IOException {
        File bootloaderDir = new File(nmfDir, Deployment.DIR_BOOTLOADER);
        Files.deleteIfExists(new File(bootloaderDir, Deployment.FILE_BOOTLOADER_STATE).toPath());
        Files.deleteIfExists(new File(bootloaderDir, Deployment.FILE_BOOT_CONFIRMED).toPath());
    }

    /**
     * Returns whether the last boot reported itself as successful.
     *
     * @return {@code true} if the boot confirmation marker is there.
     */
    public boolean isBootConfirmed() {
        return new File(new File(nmfDir, Deployment.DIR_BOOTLOADER),
                Deployment.FILE_BOOT_CONFIRMED).isFile();
    }

    private void store(final String role, final Properties baseline) throws IOException {
        try (FileOutputStream out = new FileOutputStream(baselineFile(role))) {
            baseline.store(out, "NMF Software Baseline");
        }
    }

    private File baselineFile(final String role) {
        return new File(new File(nmfDir, Deployment.DIR_BOOTLOADER),
                Deployment.baselineFileName(role));
    }

}
