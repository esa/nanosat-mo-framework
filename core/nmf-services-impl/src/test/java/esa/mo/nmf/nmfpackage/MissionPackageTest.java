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
package esa.mo.nmf.nmfpackage;

import esa.mo.nmf.environment.Deployment;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataMission;
import esa.mo.nmf.nmfpackage.metadata.MetadataNMF;
import org.junit.Assert;
import org.junit.Test;

/**
 * Holds where a mission package puts what it carries.
 *
 * A mission baseline goes to jars-mission, under a directory named after its
 * own version, so that the version being replaced stays where it is and can be
 * booted again. That directory is also what tells the rest of the framework it
 * is looking at a baseline: the Package Manager decides what to refresh and
 * what to refuse by the path of the files, not by the type of the package.
 *
 * The version is the mission's own. The bootloader keeps a version for the
 * mission and one for the framework and builds the classpath from both, so a
 * mission can be updated while the framework underneath it stays.
 *
 * Before this, a package of this type had no path of its own and its jars were
 * carried at the root, which on installation would have strewn them across the
 * top of the filesystem.
 */
public class MissionPackageTest {

    private static final String VERSION = "2.7.1";

    @Test
    public void aMissionPackageIsOfTypeMission() {
        Metadata metadata = new MetadataMission("a-mission", VERSION);

        Assert.assertTrue("A mission package has to say that it is one", metadata.isMission());
        Assert.assertEquals(Metadata.TYPE_UPDATE_MISSION, metadata.getPackageType());
        Assert.assertFalse("It is not an App", metadata.isApp());
        Assert.assertFalse("Nor the framework", metadata.isNMF());
    }

    @Test
    public void aMissionPackageIsABaselineComponent() {
        Assert.assertTrue("A mission baseline is a component of the software baseline, as the "
                + "framework and the runtime are, so installing one refreshes the checksums and "
                + "rotates the baseline", new MetadataMission("a-mission", VERSION).isBaselineComponent());
    }

    @Test
    public void theFilesGoUnderJarsMissionAndTheVersion() throws IOException {
        Assert.assertEquals("A mission baseline belongs in jars-mission, under its own version",
                Deployment.DIR_JARS_MISSION + "/" + VERSION + "/a-jar.jar",
                whereItPuts(new MetadataMission("a-mission", VERSION)));
    }

    @Test
    public void theMissionVersionIsItsOwnAndNotTheFrameworksOne() throws IOException {
        String mission = whereItPuts(new MetadataMission("a-mission", VERSION));
        String framework = whereItPuts(new MetadataNMF("nmf", "9.9.9"));

        Assert.assertNotEquals("The two baselines are versioned apart, so that one can be "
                + "updated without the other moving", mission, framework);
        Assert.assertTrue("The mission one is under jars-mission",
                mission.startsWith(Deployment.DIR_JARS_MISSION + "/"));
        Assert.assertTrue("The framework one is under jars-nmf",
                framework.startsWith(Deployment.DIR_JARS_NMF + "/"));
    }

    /**
     * Builds a package holding one file and answers where inside it that file
     * was put. It is asked of the package rather than of the builder, because
     * where the files land is the thing that matters and the only thing the
     * Package Manager reads.
     *
     * @param metadata The metadata of the package to build.
     * @return The path the file was given inside the package.
     */
    private static String whereItPuts(Metadata metadata) throws IOException {
        Path work = Files.createTempDirectory("nmf-mission-package-test");
        work.toFile().deleteOnExit();

        File jar = new File(work.toFile(), "a-jar.jar");
        Files.write(jar.toPath(), "not really a jar".getBytes(StandardCharsets.UTF_8));

        NMFPackageBuilder builder = new NMFPackageBuilder(metadata);
        builder.addFileOrDirectory(jar);
        File built = builder.createPackage(work.toFile());

        Assert.assertNotNull("The package has to be built to be looked into", built);

        try (ZipFile zip = new ZipFile(built)) {
            return zip.stream()
                    .map(ZipEntry::getName)
                    .filter(name -> name.endsWith("a-jar.jar"))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("The file is not in the package"))
                    .replace('\\', '/');
        }
    }

    @Test
    public void metadataOfAMissionCastsBackToAMission() {
        Metadata metadata = new MetadataMission("a-mission", VERSION);

        Assert.assertNotNull("A mission package casts to the mission metadata", metadata.castToMission());
        Assert.assertNull("And an App does not", new MetadataNMF("nmf", VERSION).castToMission());
    }
}
