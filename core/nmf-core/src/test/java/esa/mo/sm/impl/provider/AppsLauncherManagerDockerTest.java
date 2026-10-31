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

import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.environment.Deployment;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the docker-containers isolation command assembly. These need
 * no Docker daemon: they only check the {@code docker run} argv that the
 * Supervisor would execute.
 */
public class AppsLauncherManagerDockerTest {

    // The manager needs no COM archive for command assembly.
    private final AppsLauncherManagerDocker manager = new AppsLauncherManagerDocker(null);

    @org.junit.Rule
    public org.junit.rules.TemporaryFolder tmp = new org.junit.rules.TemporaryFolder(
            new java.io.File("target").getAbsoluteFile());

    private String originalHome;

    @org.junit.Before
    public void redirectHome() {
        // assembleCommand creates the app's storage under user.home; keep that in target/
        originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tmp.getRoot().getAbsolutePath());
    }

    @After
    public void clearProperties() {
        if (originalHome != null) {
            System.setProperty("user.home", originalHome);
        }
        System.clearProperty(AppsLauncherManagerDocker.IMAGE_PROPERTY);
        System.clearProperty(AppsLauncherManagerDocker.NETWORK_PROPERTY);
    }

    @Test
    public void testEnvironmentCarriesTheDirectoryURI() {
        String uri = "maltcp://supervisor:1024/nanosat-mo-supervisor-Directory";
        Map<String, String> env = manager.assembleAppLauncherEnvironment(uri);
        Assert.assertEquals("-D" + Const.CENTRAL_DIRECTORY_URI_PROPERTY + "=" + uri,
                env.get("JAVA_OPTS"));
    }

    @Test
    public void testAssembleStartCommand() {
        String nmfHome = Deployment.getNMFRootDir().getAbsolutePath();
        String workDir = nmfHome + "/apps/MyApp";
        String[] env = {"JAVA_OPTS=-Dfoo=bar"};

        List<String> cmd = Arrays.asList(
                manager.assembleCommand(workDir, "MyApp", null, env));

        Assert.assertEquals(Arrays.asList("docker", "run", "--rm", "--name", "nmf-app-MyApp",
                "--network", "host"), cmd.subList(0, 7));
        Assert.assertEquals("the app's command must end with its start script",
                Arrays.asList("eclipse-temurin:21-jre", "/bin/sh", "-c", "./start_app.sh"),
                cmd.subList(cmd.size() - 4, cmd.size()));

        // NMF-2026-015: never root, no capabilities, resource caps
        Assert.assertTrue("the container must run as the Supervisor's user, not root",
                cmd.contains("--user") && !cmd.get(cmd.indexOf("--user") + 1).startsWith("0:"));
        Assert.assertEquals("ALL", cmd.get(cmd.indexOf("--cap-drop") + 1));
        Assert.assertTrue(cmd.contains("--security-opt")
                && cmd.get(cmd.indexOf("--security-opt") + 1).equals("no-new-privileges"));
        Assert.assertTrue("the number of processes must be capped", cmd.contains("--pids-limit"));

        // The whole NMF root must not be mounted, only the working directory of the app
        Assert.assertFalse("the whole NMF root must not be mounted",
                cmd.contains(nmfHome + ":" + nmfHome));
        Assert.assertTrue("the app directory must be mounted read-write",
                cmd.contains(workDir + ":" + workDir));
        Assert.assertEquals("JAVA_OPTS=-Dfoo=bar", cmd.get(cmd.lastIndexOf("-e") + 1));
    }

    @Test
    public void testContainerNameIsSanitised() {
        // Spaces and slashes are not valid in a Docker container name.
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "space app/1", null, new String[0]));
        int nameIdx = cmd.indexOf("--name");
        Assert.assertTrue("--name flag must be present", nameIdx >= 0);
        Assert.assertEquals("nmf-app-space-app-1", cmd.get(nameIdx + 1));
    }

    @Test
    public void testImageAndNetworkOverrides() {
        System.setProperty(AppsLauncherManagerDocker.IMAGE_PROPERTY, "myrepo/nmf-app:1.2");
        System.setProperty(AppsLauncherManagerDocker.NETWORK_PROPERTY, "mo-bridge");

        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", null, new String[0]));

        int netIdx = cmd.indexOf("--network");
        Assert.assertEquals("mo-bridge", cmd.get(netIdx + 1));
        // The image is the token right before the in-container shell invocation.
        Assert.assertEquals("myrepo/nmf-app:1.2", cmd.get(cmd.indexOf("/bin/sh") - 1));
    }

    @Test
    public void testAppIsNotRunPrivileged() {
        // The container must not be given --privileged (NMF-2026-003): that would
        // defeat the isolation and hand the app full access to the host.
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", null, new String[0]));
        Assert.assertFalse("the container must not run --privileged", cmd.contains("--privileged"));
    }

}
