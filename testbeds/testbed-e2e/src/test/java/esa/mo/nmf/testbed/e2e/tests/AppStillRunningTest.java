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
import esa.mo.nmf.nmfpackage.NMFPackageManager;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import esa.mo.nmf.nmfpackage.utils.AppProcesses;
import esa.mo.nmf.nmfpackage.utils.AuxFilesGenerator;
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
import java.util.List;
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
 * The files of an App are not removed while the App still runs.
 *
 * The Apps Launcher only knows the processes that it started, so an App whose
 * start script was run by hand is not running for it. Uninstalling the App
 * used to remove its jar and receipt and leave its process running from files
 * that were gone. These tests run the generated start script by hand, as the
 * report did, and hold that uninstall and update refuse to go on until the
 * process has stopped.
 */
public class AppStillRunningTest {

    private static final byte[] CONTENT = "not really a jar".getBytes(StandardCharsets.UTF_8);

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File root;
    private File previousRoot;
    private final List<Process> started = new ArrayList<>();

    @Before
    public void useATemporaryNMFRoot() throws Exception {
        Assume.assumeTrue("A POSIX shell is needed", new File("/bin/sh").canExecute());
        root = tmp.newFolder(Deployment.DIR_NMF);
        previousRoot = setNMFRoot(root);
    }

    @After
    public void stopTheProcessesAndRestoreTheNMFRoot() throws Exception {
        started.forEach(AppStillRunningTest::stop);
        if (root != null) {
            setNMFRoot(previousRoot);
        }
    }

    @Test
    public void uninstallIsRefusedWhileTheAppRunsOutsideTheAppsLauncher() throws Exception {
        File pack = packageOf("probe", "1.0");
        new NMFPackageManager(null).install(pack.getAbsolutePath(), root);
        Process app = runTheStartScriptByHand("probe");

        try {
            new NMFPackageManager(null).uninstall(metadataOf(pack), false);
            Assert.fail("An App that still runs must not be uninstalled");
        } catch (IOException refused) {
            Assert.assertTrue(refused.getMessage(), refused.getMessage().contains("still running"));
        }

        Assert.assertTrue("The jar of a running App must be kept",
                new File(root, "apps/probe/probe.jar").isFile());
        Assert.assertTrue("The receipt of a running App must be kept",
                new File(Deployment.getInstallationsTrackerDir(), "probe.receipt").isFile());

        stop(app);
        new NMFPackageManager(null).uninstall(metadataOf(pack), false);

        Assert.assertFalse("Once the App has stopped, it is uninstalled",
                new File(root, "apps/probe/probe.jar").exists());
    }

    @Test
    public void updateIsRefusedWhileTheAppRunsOutsideTheAppsLauncher() throws Exception {
        new NMFPackageManager(null).install(packageOf("probe", "1.0").getAbsolutePath(), root);
        runTheStartScriptByHand("probe");

        try {
            new NMFPackageManager(null).update(packageOf("probe", "2.0").getAbsolutePath(), root);
            Assert.fail("An App that still runs must not be updated");
        } catch (IOException refused) {
            Assert.assertTrue(refused.getMessage(), refused.getMessage().contains("still running"));
        }

        Assert.assertEquals("A running App keeps the version that it runs", "1.0",
                Metadata.load(new File(Deployment.getInstallationsTrackerDir(), "probe.receipt"))
                        .getPackageVersion());
    }

    @Test
    public void anAppWithALongerNameIsNotTakenForTheApp() throws Exception {
        File other = new File(root, "apps/probe-other");
        Assert.assertTrue(other.mkdirs());
        started.add(new ProcessBuilder("/bin/sh", "-c", "sleep 60; true",
                new File(other, "probe-other.jar").getAbsolutePath()).start());

        Assert.assertTrue("Only the processes of the App itself are found",
                AppProcesses.find(new File(root, "apps/probe"), null).isEmpty());
    }

    @Test
    public void aShellInTheAppsDirectoryIsNotTakenForTheApp() throws Exception {
        File appDir = new File(root, "apps/probe");
        Assert.assertTrue(appDir.mkdirs());
        started.add(new ProcessBuilder("/bin/sh", "-c", "sleep 60; true").directory(appDir).start());

        Assert.assertTrue("Being in the App's directory is not running the App",
                AppProcesses.find(appDir, null).isEmpty());
    }

    /**
     * Writes the start script of the App with a stand-in for java that keeps
     * running, and runs it as anyone on board could, outside the Apps Launcher.
     */
    private Process runTheStartScriptByHand(String name) throws Exception {
        File java = new File(tmp.getRoot(), "java");
        Files.write(java.toPath(), "#!/bin/sh\nsleep 60\n".getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(java.setExecutable(true));

        File appDir = new File(root, "apps/" + name);
        File script = new File(appDir, "start_app.sh");
        Files.write(script.toPath(), AuxFilesGenerator.generateLinuxStartAppScript(
                java.getAbsolutePath(), app(name, "1.0")).getBytes(StandardCharsets.UTF_8));

        ProcessBuilder pb = new ProcessBuilder("/bin/sh", script.getAbsolutePath());
        pb.environment().remove("JAVA_OPTS");
        pb.redirectErrorStream(true);
        pb.redirectOutput(new File(tmp.getRoot(), "script-output"));
        Process process = pb.start();
        started.add(process);

        long deadline = System.currentTimeMillis() + 10_000;
        while (AppProcesses.find(appDir, null).isEmpty()) {
            Assert.assertTrue("The App must have been started", System.currentTimeMillis() < deadline);
            Thread.sleep(50);
        }
        return process;
    }

    private static void stop(Process process) {
        process.descendants().forEach(ProcessHandle::destroyForcibly);
        process.destroyForcibly();
        try {
            process.waitFor();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    private static MetadataApp app(String name, String version) {
        return new MetadataApp(name, version, "esa.mo.nmf.apps.Probe", name + ".jar",
                null, null, new ArrayList<>(), "6.0");
    }

    private static Metadata metadataOf(File pack) throws IOException {
        try (ZipFile zip = new ZipFile(pack)) {
            return Metadata.parseZipFile(zip);
        }
    }

    private File packageOf(String name, String version) throws IOException {
        Metadata metadata = app(name, version);
        String path = "apps/" + name + "/" + name + ".jar";
        metadata.addProperty(Metadata.PACKAGE_METADATA_VERSION, Metadata.METADATA_VERSION_LATEST);
        metadata.addProperty(Metadata.FILE_COUNT, "1");
        metadata.addProperty(Metadata.FILE_PATH + ".0", path);
        metadata.addProperty(Metadata.FILE_SHA256 + ".0",
                ChecksumGenerator.sha256Hex(new ByteArrayInputStream(CONTENT)));

        ByteArrayOutputStream properties = new ByteArrayOutputStream();
        metadata.store(properties);

        File pack = new File(tmp.newFolder(), name + "-" + version + ".nmfpack");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(pack))) {
            zip.putNextEntry(new ZipEntry(Metadata.FILENAME));
            zip.write(properties.toByteArray());
            zip.closeEntry();
            zip.putNextEntry(new ZipEntry(path));
            zip.write(CONTENT);
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
