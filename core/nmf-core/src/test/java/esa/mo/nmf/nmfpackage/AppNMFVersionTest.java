/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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

import esa.mo.helpertools.misc.OSValidator;
import esa.mo.nmf.NMFProvider;
import esa.mo.nmf.environment.AppsIsolationMode;
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import esa.mo.nmf.nmfpackage.utils.AuxFilesGenerator;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * An App runs only on the major version of the NMF that it was built against,
 * with the Java and the NMF jars of the baseline that runs, and a failed
 * update leaves a receipt that lists the files on disk.
 */
public class AppNMFVersionTest {

    private static final String RECEIPTS_PROPERTY = "esa.mo.nmf.nmfpackage.receipts";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File root;
    private File previousRoot;
    private String previousReceipts;
    private String previousIsolation;
    private String previousJavaHome;
    private final int major = NMFProvider.getMajorVersionNMF();

    @Before
    public void useATemporaryNMFRoot() throws Exception {
        root = tmp.newFolder(Deployment.DIR_NMF);
        previousRoot = setNMFRoot(root);
        previousReceipts = System.clearProperty(RECEIPTS_PROPERTY);
        previousIsolation = System.clearProperty(AppsIsolationMode.PROPERTY);
        previousJavaHome = System.getProperty("java.home");
    }

    @After
    public void restoreTheNMFRoot() throws Exception {
        setNMFRoot(previousRoot);
        restore(RECEIPTS_PROPERTY, previousReceipts);
        restore(AppsIsolationMode.PROPERTY, previousIsolation);
        System.setProperty("java.home", previousJavaHome);
    }

    @Test
    public void anAppRunsOnlyOnTheMajorVersionItWasBuiltAgainst() {
        Assert.assertNull("Same major version", app("probe", "1.0", major + ".0").whyNotRunnableOn(major));
        Assert.assertNull("Another minor version", app("probe", "1.0", major + ".3-SNAPSHOT").whyNotRunnableOn(major));

        String other = app("probe", "1.0", (major - 1) + ".1").whyNotRunnableOn(major);
        Assert.assertNotNull("Another major version", other);
        Assert.assertTrue(other, other.contains("not the major version"));

        String none = app("probe", "1.0", null).whyNotRunnableOn(major);
        Assert.assertNotNull("A package that records no NMF version cannot be checked", none);
        Assert.assertTrue(none, none.contains("records no NMF version"));

        Assert.assertNotNull("An NMF version that is not valid", app("probe", "1.0", "six").whyNotRunnableOn(major));
    }

    @Test
    public void anAppOfAnotherMajorVersionIsNotInstalled() throws Exception {
        File pack = packageOf(app("old-app", "1.0", (major - 1) + ".1"));
        try {
            new NMFPackageManager(null).install(pack.getAbsolutePath(), root);
            Assert.fail("An App built against another major version of the NMF must be refused");
        } catch (IOException expected) {
            Assert.assertTrue(expected.getMessage(), expected.getMessage().contains("not the major version"));
        }
        Assert.assertFalse("Nothing of the App is written", new File(root, "apps/old-app").exists());
    }

    @Test
    public void theStartScriptRunsTheJavaOfTheBaseline() throws Exception {
        Assert.assertEquals("A Java outside the NMF java directory is the java of the system",
                "java", Deployment.getAppJavaCommand());

        File runtime = new File(new File(root, Deployment.DIR_JAVA), "jre-21");
        File java = new File(runtime, "bin/java");
        java.getParentFile().mkdirs();
        Files.write(java.toPath(), "java".getBytes(StandardCharsets.UTF_8));
        System.setProperty("java.home", runtime.getAbsolutePath());
        Assert.assertEquals("A Java runtime of the NMF is named by its path",
                java.getCanonicalPath(), Deployment.getAppJavaCommand());
    }

    @Test
    public void theStartScriptsAreRewrittenForTheNMFThatRuns() throws Exception {
        Assume.assumeTrue("Start scripts are shell scripts", new OSValidator().isUnix());
        new NMFPackageManager(null).install(packageOf(app("probe", "1.0", major + ".0")).getAbsolutePath(), root);
        File script = new File(root, "apps/probe/start_app.sh");
        Files.write(script.toPath(), "the script of the previous NMF version".getBytes(StandardCharsets.UTF_8));

        // An App of another major version, installed by an older NMF
        MetadataApp old = app("old-app", "1.0", (major - 1) + ".1");
        old.store(new File(Deployment.getInstallationsTrackerDir(), "old-app.receipt"));
        File oldScript = new File(root, "apps/old-app/start_app.sh");
        oldScript.getParentFile().mkdirs();
        Files.write(oldScript.toPath(), "the script of the old App".getBytes(StandardCharsets.UTF_8));

        NMFPackageManager.refreshStartScripts();

        MetadataApp receipt = Metadata.load(
                new File(Deployment.getInstallationsTrackerDir(), "probe.receipt")).castToApp();
        Assert.assertEquals("The script of an App that can run is rewritten for the NMF that runs",
                AuxFilesGenerator.generateLinuxStartAppScript(Deployment.getAppJavaCommand(), receipt),
                read(script));
        Assert.assertEquals("The script of an App that cannot run is left as it is",
                "the script of the old App", read(oldScript));
    }

    @Test
    public void aFailedUpdateLeavesAReceiptOfTheFilesOnDisk() throws Exception {
        Assume.assumeTrue("App accounts exist on Unix only", new OSValidator().isUnix());
        Assume.assumeFalse("The update must fail to give the App its account",
                new File("/usr/local/sbin/nmf-app-account").exists());
        NMFPackageManager manager = new NMFPackageManager(null);
        manager.install(packageOf(app("probe", "1.0", major + ".0")).getAbsolutePath(), root);

        // The App accounts cannot be managed, as on a spacecraft whose setup
        // script was not run: the update fails after the new files are in place
        System.setProperty(AppsIsolationMode.PROPERTY, AppsIsolationMode.LINUX_USERSPACE);
        try {
            manager.update(packageOf(app("probe", "1.1", major + ".0")).getAbsolutePath(), root);
            Assert.fail("The update must fail when the App cannot be given to its account");
        } catch (IOException expected) {
            // As on the spacecraft
        }

        File receipt = new File(Deployment.getInstallationsTrackerDir(), "probe.receipt");
        Assert.assertTrue("The App must keep a receipt, so that the update can be run again", receipt.isFile());
        Assert.assertEquals("The receipt lists the files of the new version, which are on disk",
                "1.1", Metadata.load(receipt).getPackageVersion());
    }

    private static MetadataApp app(String name, String version, String nmfVersion) {
        return new MetadataApp(name, version, "esa.mo.nmf.apps.Probe", name + ".jar",
                null, null, new ArrayList<>(), nmfVersion);
    }

    private File packageOf(MetadataApp metadata) throws IOException {
        File jar = new File(tmp.newFolder(), metadata.getPackageName() + ".jar");
        Files.write(jar.toPath(), metadata.getPackageVersion().getBytes(StandardCharsets.UTF_8));
        NMFPackageBuilder builder = new NMFPackageBuilder(metadata);
        builder.addFileOrDirectory(jar);
        return builder.createPackage(tmp.newFolder());
    }

    private static String read(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private static void restore(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
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
