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
package esa.mo.sm.impl.provider;

import esa.mo.nmf.environment.Deployment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Holds that an App started in a sandbox can read its own credential, which
 * the Supervisor gives it for the run, and neither the credential of another
 * App nor the credential of ground, and that it has the usual devices, such as
 * {@code /dev/null}.
 * <p>
 * Each test starts a real sandbox through the launcher, with a start script
 * that reports which of the three files it can read. It is skipped where the
 * sandbox (bubblewrap or Docker) is not available, unless the
 * {@value #REQUIRE_SANDBOXES_PROPERTY} system property is true, as in CI.
 */
public class AppSandboxSecretsTest {

    private static final String APP = "probe";

    /** When true, a missing sandbox fails the test instead of skipping it. */
    public static final String REQUIRE_SANDBOXES_PROPERTY = "esa.mo.nmf.test.requireSandboxes";

    // Not under /tmp: the bubblewrap sandbox mounts a fresh /tmp over it
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder(new File("target").getAbsoluteFile());

    private String originalHome;
    private File previousRoot;
    private File root;
    private File workDir;

    @Before
    public void setUpAnNMFRootAndAHome() throws Exception {
        File home = tmp.newFolder("home");
        originalHome = System.getProperty("user.home");
        System.setProperty("user.home", home.getAbsolutePath());

        root = tmp.newFolder(Deployment.DIR_NMF);
        previousRoot = setNMFRoot(root);
        workDir = new File(new File(root, Deployment.DIR_APPS), APP);
        Assert.assertTrue(workDir.mkdirs());
        Assert.assertTrue(Deployment.getLogsDirForApp(APP).mkdirs());

        // The three secrets: this App's credential, another App's, and ground's
        write(new File(home, ".nmf-apps/" + APP + "/credential"), "own");
        write(new File(home, ".nmf-apps/other/credential"), "other");
        write(new File(new File(root, Deployment.DIR_BOOTLOADER), Deployment.FILE_GROUND_CREDENTIAL), "ground");

        // The App: a start script that reports which of them it can read
        File script = new File(workDir, "start_app.sh");
        write(script, "#!/bin/sh\n"
                + "check() { if cat \"$2\" >/tmp/check 2>&1; then echo \"$1=yes\"; else echo \"$1=no\"; fi; }\n"
                + "check OWN '" + new File(home, ".nmf-apps/" + APP + "/credential") + "'\n"
                + "check OTHER '" + new File(home, ".nmf-apps/other/credential") + "'\n"
                + "check GROUND '" + new File(new File(root, Deployment.DIR_BOOTLOADER),
                        Deployment.FILE_GROUND_CREDENTIAL) + "'\n"
                + "if echo x >/dev/null 2>/tmp/check; then echo DEVNULL=yes; else echo DEVNULL=no; fi\n");
        Assert.assertTrue(script.setExecutable(true, false));
    }

    @After
    public void restore() throws Exception {
        System.setProperty("user.home", originalHome);
        setNMFRoot(previousRoot);
    }

    @Test
    public void inBubblewrapAnAppReadsOnlyItsOwnCredential() throws Exception {
        requireSandbox("bubblewrap is not available", runs("bwrap", "--ro-bind", "/", "/", "true"));
        AppsLauncherManagerBubblewrap manager = new AppsLauncherManagerBubblewrap(null);

        String report = run(manager.assembleCommand(workDir.getAbsolutePath(), APP, null, new String[0]));

        assertOnlyItsOwn(report);
    }

    @Test
    public void inDockerAnAppReadsOnlyItsOwnCredential() throws Exception {
        String image = System.getProperty(AppsLauncherManagerDocker.IMAGE_PROPERTY, "eclipse-temurin:21-jre");
        requireSandbox("Docker or the image " + image + " is not available",
                runs("docker", "image", "inspect", image));
        AppsLauncherManagerDocker manager = new AppsLauncherManagerDocker(null);

        String report = run(manager.assembleCommand(workDir.getAbsolutePath(), APP, null, new String[0]));

        assertOnlyItsOwn(report);
    }

    private static void requireSandbox(String missing, boolean available) {
        if (Boolean.getBoolean(REQUIRE_SANDBOXES_PROPERTY)) {
            Assert.assertTrue(missing, available);
        }
        Assume.assumeTrue(missing, available);
    }

    private static void assertOnlyItsOwn(String report) {
        System.out.println("The App reported:\n" + report);
        Assert.assertTrue("The App must be able to write to /dev/null\n" + report,
                report.contains("DEVNULL=yes"));
        Assert.assertTrue("The App must be able to read its own credential\n" + report,
                report.contains("OWN=yes"));
        Assert.assertTrue("The App must not be able to read the credential of another App\n" + report,
                report.contains("OTHER=no"));
        Assert.assertTrue("The App must not be able to read the credential of ground\n" + report,
                report.contains("GROUND=no"));
    }

    private String run(String[] command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(workDir);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        Assert.assertTrue("The sandbox must finish", p.waitFor(120, TimeUnit.SECONDS));
        return new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static boolean runs(String... command) {
        try {
            Process p = new ProcessBuilder(command).redirectErrorStream(true).start();
            p.getInputStream().readAllBytes();
            return p.waitFor(60, TimeUnit.SECONDS) && p.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    private static void write(File file, String content) throws IOException {
        Assert.assertTrue(file.getParentFile().isDirectory() || file.getParentFile().mkdirs());
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
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
