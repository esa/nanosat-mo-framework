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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the Linux command that starts an app. They need no root and no
 * sudo. They check that the app is confined to its own unprivileged user
 * (NMF-2026-003), and that the app directory and the environment values reach
 * the shell as single quoted words, so that no character in them is acted on
 * (NMF-2026-004).
 */
public class AppsLauncherManagerLinuxTest {

    // The manager needs no COM archive for command assembly.
    private final AppsLauncherManagerLinux manager = new AppsLauncherManagerLinux(null);

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void appIsLaunchedAsItsOwnUnprivilegedUser() {
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", "app-MyApp", new String[0]));

        // The app runs through 'su - app-MyApp -c ...', so under its own user and not
        // the Supervisor's. (A leading 'sudo' is added only when sudo is available, so
        // it is not asserted here.)
        int suIdx = cmd.indexOf("su");
        Assert.assertTrue("the command must switch user with su", suIdx >= 0);
        Assert.assertEquals("-", cmd.get(suIdx + 1));
        Assert.assertEquals("app-MyApp", cmd.get(suIdx + 2));
        Assert.assertEquals("-c", cmd.get(suIdx + 3));
        Assert.assertTrue("the app script must run inside the su -c argument",
                cmd.get(suIdx + 4).contains("./start_app.sh"));
    }

    @Test
    public void withoutAUserTheAppRunsInAPlainShell() {
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", null, new String[0]));

        Assert.assertEquals("/bin/sh", cmd.get(0));
        Assert.assertEquals("-c", cmd.get(1));
        Assert.assertFalse("there must be no user switch when no user is given", cmd.contains("su"));
    }

    @Test
    public void theDirectoryAndTheEnvironmentValuesAreQuoted() {
        String[] env = {"JAVA_OPTS=-Dname=it's here"};
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/apps/my app;x", "MyApp", null, env));

        // Each value is one single-quoted word; a quote inside a value is closed,
        // escaped and reopened. The script only runs if the cd succeeded.
        Assert.assertEquals("cd '/apps/my app;x' && JAVA_OPTS='-Dname=it'\\''s here' ./start_app.sh",
                cmd.get(cmd.size() - 1));
    }

    @Test
    public void aDirectoryWithShellCharactersIsEnteredAsItIs() throws Exception {
        Assume.assumeTrue("needs a POSIX shell", new File("/bin/sh").canExecute());

        // A directory whose name holds characters that a shell would otherwise act on.
        File appDir = tmp.newFolder("app dir;x&y|z$HOME'q\"r");
        File script = new File(appDir, "start_app.sh");
        Files.write(script.toPath(), "#!/bin/sh\npwd > ran\n".getBytes(StandardCharsets.UTF_8));
        Assert.assertTrue(script.setExecutable(true));

        String[] env = {"MARKER=it's $HOME;x"};
        Process process = new ProcessBuilder(
                manager.assembleCommand(appDir.getAbsolutePath(), "MyApp", null, env))
                .redirectErrorStream(true).start();
        Assert.assertTrue("the start script must finish", process.waitFor(10, TimeUnit.SECONDS));
        Assert.assertEquals("the start script must succeed", 0, process.exitValue());

        // The script ran inside that very directory, so the name was read literally.
        File ran = new File(appDir, "ran");
        Assert.assertTrue("the start script must have run in the App directory", ran.isFile());
        Assert.assertEquals(appDir.getAbsolutePath(),
                new String(Files.readAllBytes(ran.toPath()), StandardCharsets.UTF_8).trim());
    }
}
