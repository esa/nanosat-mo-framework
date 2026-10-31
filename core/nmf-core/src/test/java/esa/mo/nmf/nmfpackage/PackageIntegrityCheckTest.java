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
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Holds that the integrity of a package is really checked: by the backend that
 * the Package Management service asks, and by update before it removes the
 * installed version.
 */
public class PackageIntegrityCheckTest {

    private static final String JAR = "apps/probe/probe.jar";
    private static final byte[] V1 = "version one".getBytes(StandardCharsets.UTF_8);
    private static final byte[] V2 = "version two".getBytes(StandardCharsets.UTF_8);
    private static final byte[] TAMPERED = "not the listed file".getBytes(StandardCharsets.UTF_8);

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File root;
    private File previousRoot;

    @Before
    public void useATemporaryNMFRoot() throws Exception {
        root = tmp.newFolder(Deployment.DIR_NMF);
        previousRoot = setNMFRoot(root);
    }

    @After
    public void restoreTheNMFRoot() throws Exception {
        setNMFRoot(previousRoot);
    }

    @Test
    public void anUpdateWithABadFileLeavesTheInstalledVersionAsItIs() throws IOException {
        NMFPackageManager manager = new NMFPackageManager(null);
        manager.install(appPackage("1.0", V1, V1).getAbsolutePath(), root);
        File jar = new File(root, JAR);
        Assert.assertArrayEquals(V1, Files.readAllBytes(jar.toPath()));

        // Version 2 lists the checksum of one file but carries another
        File bad = appPackage("2.0", V2, TAMPERED);
        try {
            manager.update(bad.getAbsolutePath(), root);
            Assert.fail("An update whose file does not match its checksum must be refused");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains("SHA-256 does not match"));
        }

        Assert.assertArrayEquals("The installed version must be left as it is",
                V1, Files.readAllBytes(jar.toPath()));
        Assert.assertTrue("The installed version must still be recorded as installed",
                new File(Deployment.getInstallationsTrackerDir(), "probe.receipt").isFile());
    }

    @Test
    public void theBackendReportsTheRealIntegrityAndTheChecksumOfThePackageFile() throws IOException {
        File packages = tmp.newFolder("packages");
        File good = appPackage("1.0", V1, V1);
        File bad = appPackage("1.0", V1, TAMPERED);
        Files.copy(good.toPath(), new File(packages, "good.nmfpack").toPath());
        Files.copy(bad.toPath(), new File(packages, "bad.nmfpack").toPath());

        NMFPackagePMBackend backend = new NMFPackagePMBackend(packages.getAbsolutePath(), null);

        Assert.assertTrue("A package whose files match their checksums is valid",
                backend.checkPackageIntegrity("good.nmfpack"));
        Assert.assertFalse("A package with a file that does not match its checksum is not valid",
                backend.checkPackageIntegrity("bad.nmfpack"));
        Assert.assertEquals("The checksum is the SHA-256 of the package file as stored",
                ChecksumGenerator.sha256Hex(good), backend.getPackageChecksum("good.nmfpack"));
    }

    /**
     * Writes an App package by hand, as anyone could, that lists the SHA-256 of
     * {@code listed} for its jar but carries {@code carried}.
     */
    private File appPackage(String version, byte[] listed, byte[] carried) throws IOException {
        Metadata metadata = new MetadataApp("probe", version, "esa.mo.nmf.apps.Probe", "probe.jar",
                null, null, new ArrayList<>(), "6.0");
        metadata.addProperty(Metadata.FILE_COUNT, "1");
        metadata.addProperty(Metadata.FILE_PATH + ".0", JAR);
        metadata.addProperty(Metadata.FILE_SHA256 + ".0",
                ChecksumGenerator.sha256Hex(new ByteArrayInputStream(listed)));

        ByteArrayOutputStream properties = new ByteArrayOutputStream();
        metadata.store(properties);

        File pack = new File(tmp.newFolder(), "probe-" + version + ".nmfpack");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(pack))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            zip.write(properties.toByteArray());
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry(JAR));
            zip.write(carried);
            zip.closeEntry();
        }
        return pack;
    }

    /** Points the framework at the given NMF root, answering the one it used before. */
    private static File setNMFRoot(File dir) throws Exception {
        Field field = Deployment.class.getDeclaredField("pathNMF");
        field.setAccessible(true);
        File previous = (File) field.get(null);
        field.set(null, dir);
        return previous;
    }
}
