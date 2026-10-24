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
import esa.mo.nmf.nmfpackage.metadata.MetadataNMF;
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
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Holds where a package may put, and remove, what it carries.
 *
 * A package lists its files with paths relative to the NMF root, and a package
 * can be built by anyone. An App runs as its own unprivileged user when apps
 * are isolated, but its package is installed by the Supervisor, so a package
 * must not be able to touch files that belong elsewhere, above all the
 * Supervisor's own software. Each type of package therefore has an area of its
 * own: an App its folder, a framework baseline its versioned directory, and so
 * on. These tests hold that a package stays inside its area, that a package
 * which does not is refused as a whole, and that ordinary packages still
 * install where they belong.
 */
public class AppPackageConfinementTest {

    private static final byte[] CONTENT = "not really a jar".getBytes(StandardCharsets.UTF_8);

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
    public void anAppPackageIsInstalledIntoItsOwnFolder() throws Exception {
        File pack = packageCarrying(app("probe"), "apps/probe/probe.jar");

        new NMFPackageManager(null).install(pack.getAbsolutePath(), root);

        Assert.assertTrue("The App's own file must be installed",
                new File(root, "apps/probe/probe.jar").isFile());
    }

    @Test
    public void anAppPackageCannotWriteIntoTheFrameworkBaseline() throws Exception {
        File pack = packageCarrying(app("probe"), "apps/probe/probe.jar", "jars-nmf/9.9.9/extra.jar");

        installExpectingRefusal(pack);

        Assert.assertFalse("An App package must not put a file into the framework's baseline",
                new File(root, "jars-nmf/9.9.9/extra.jar").exists());
        Assert.assertFalse("An App package must not have the baseline's checksums rewritten",
                new File(root, "jars-nmf/9.9.9/" + ChecksumGenerator.CHECKSUMS_FILENAME).exists());
        Assert.assertFalse("A refused package must write nothing, not even its own files",
                new File(root, "apps/probe/probe.jar").exists());
    }

    @Test
    public void anAppPackageCannotWriteIntoAnotherAppsFolder() throws Exception {
        File pack = packageCarrying(app("probe"), "apps/probe/probe.jar", "apps/other/other.jar");

        installExpectingRefusal(pack);

        Assert.assertFalse("An App package must not put a file into another App's folder",
                new File(root, "apps/other/other.jar").exists());
    }

    @Test
    public void anAppPackageCannotDeleteOutsideItsFolder() throws Exception {
        File frameworkJar = new File(root, "jars-nmf/9.9.9/core.jar");
        Assert.assertTrue(frameworkJar.getParentFile().mkdirs());
        Files.write(frameworkJar.toPath(), CONTENT);

        File pack = packageCarrying(app("probe"), "apps/probe/probe.jar", "jars-nmf/9.9.9/core.jar");
        Metadata metadata;
        try (ZipFile zip = new ZipFile(pack)) {
            metadata = Metadata.parseZipFile(zip);
        }

        try {
            new NMFPackageManager(null).uninstall(metadata, false);
            Assert.fail("Uninstalling a package that lists a file outside its area must be refused");
        } catch (IOException refused) {
            // Expected
        }

        Assert.assertTrue("An App package must not delete a file of the framework's baseline",
                frameworkJar.exists());
    }

    @Test
    public void aDeltaPackageIsRefused() throws Exception {
        Properties properties = new Properties();
        properties.put(Metadata.PACKAGE_NAME, "a-delta");
        properties.put(Metadata.PACKAGE_VERSION, "1.0");
        properties.put(Metadata.PACKAGE_TYPE, Metadata.TYPE_DELTA);
        File pack = packageCarrying(new Metadata(properties), "apps/a-delta/a.jar");

        try {
            new NMFPackageManager(null).install(pack.getAbsolutePath(), root);
            Assert.fail("A delta package has no area of its own, so it must be refused");
        } catch (IOException refused) {
            Assert.assertTrue(refused.getMessage(), refused.getMessage().contains("no area of their own"));
        }

        Assert.assertFalse("A refused package must write nothing",
                new File(root, "apps/a-delta/a.jar").exists());
    }

    @Test
    public void aPackageCannotWriteThroughASymbolicLinkInItsArea() throws Exception {
        File outside = tmp.newFolder("outside");
        File link = new File(root, "apps/probe/link");
        Assert.assertTrue(link.getParentFile().mkdirs());
        try {
            Files.createSymbolicLink(link.toPath(), outside.toPath());
        } catch (UnsupportedOperationException | IOException noLinks) {
            Assume.assumeNoException("This file system cannot make symbolic links", noLinks);
        }

        File pack = packageCarrying(app("probe"), "apps/probe/probe.jar", "apps/probe/link/escaped.jar");

        installExpectingRefusal(pack);

        Assert.assertFalse("A link inside the App's folder must not send a file elsewhere",
                new File(outside, "escaped.jar").exists());
    }

    @Test
    public void aFrameworkPackageIsInstalledIntoItsVersionAndRefreshesItsChecksums() throws Exception {
        File pack = packageCarrying(new MetadataNMF("nmf", "9.9.9"), "jars-nmf/9.9.9/core.jar");

        new NMFPackageManager(null).install(pack.getAbsolutePath(), root);

        Assert.assertTrue("The framework baseline must be installed into its own version",
                new File(root, "jars-nmf/9.9.9/core.jar").isFile());
        Assert.assertTrue("A framework baseline refreshes the checksums of its version",
                new File(root, "jars-nmf/9.9.9/" + ChecksumGenerator.CHECKSUMS_FILENAME).isFile());
    }

    private void installExpectingRefusal(File pack) {
        try {
            new NMFPackageManager(null).install(pack.getAbsolutePath(), root);
            Assert.fail("A package that lists a file outside its own area must be refused");
        } catch (IOException refused) {
            // Expected
        }
    }

    private static Metadata app(String name) {
        return new MetadataApp(name, "1.0", "esa.mo.nmf.apps.Probe", name + ".jar",
                null, null, new ArrayList<>(), "6.0");
    }

    /**
     * Writes a package by hand, listing the given paths, as anyone could: the
     * package builder only ever puts a package's files inside its own area.
     */
    private File packageCarrying(Metadata metadata, String... paths) throws IOException {
        metadata.addProperty(Metadata.PACKAGE_METADATA_VERSION, Metadata.METADATA_VERSION_LATEST);
        metadata.addProperty(Metadata.FILE_COUNT, String.valueOf(paths.length));

        String sha256 = ChecksumGenerator.sha256Hex(new ByteArrayInputStream(CONTENT));
        for (int i = 0; i < paths.length; i++) {
            metadata.addProperty(Metadata.FILE_PATH + "." + i, paths[i]);
            metadata.addProperty(Metadata.FILE_SHA256 + "." + i, sha256);
        }

        ByteArrayOutputStream properties = new ByteArrayOutputStream();
        metadata.store(properties);

        File pack = new File(tmp.newFolder(), metadata.getPackageName() + "-1.0.nmfpack");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(pack))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            zip.write(properties.toByteArray());
            zip.closeEntry();
            for (String path : paths) {
                zip.putNextEntry(new ZipEntry(path));
                zip.write(CONTENT);
                zip.closeEntry();
            }
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
