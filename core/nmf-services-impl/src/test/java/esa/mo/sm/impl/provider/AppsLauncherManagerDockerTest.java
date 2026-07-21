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

    @After
    public void clearProperties() {
        System.clearProperty(AppsLauncherManagerDocker.IMAGE_PROPERTY);
        System.clearProperty(AppsLauncherManagerDocker.NETWORK_PROPERTY);
    }

    @Test
    public void testScriptExtension() {
        Assert.assertEquals(".sh", manager.getScriptExtension());
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

        List<String> actual = Arrays.asList(
                manager.assembleCommand(workDir, "MyApp", null, "start_", env));

        List<String> expected = Arrays.asList(
                "docker", "run", "--rm",
                "--name", "nmf-app-MyApp",
                "--network", "host",
                "-v", nmfHome + ":" + nmfHome,
                "-w", workDir,
                "-e", "JAVA_OPTS=-Dfoo=bar",
                "eclipse-temurin:21-jre",
                "/bin/sh", "-c", "./start_app.sh");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testContainerNameIsSanitised() {
        // Spaces and slashes are not valid in a Docker container name.
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "space app/1", null, "start_", new String[0]));
        int nameIdx = cmd.indexOf("--name");
        Assert.assertTrue("--name flag must be present", nameIdx >= 0);
        Assert.assertEquals("nmf-app-space-app-1", cmd.get(nameIdx + 1));
    }

    @Test
    public void testImageAndNetworkOverrides() {
        System.setProperty(AppsLauncherManagerDocker.IMAGE_PROPERTY, "myrepo/nmf-app:1.2");
        System.setProperty(AppsLauncherManagerDocker.NETWORK_PROPERTY, "mo-bridge");

        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", null, "start_", new String[0]));

        int netIdx = cmd.indexOf("--network");
        Assert.assertEquals("mo-bridge", cmd.get(netIdx + 1));
        // The image is the token right before the in-container shell invocation.
        Assert.assertEquals("myrepo/nmf-app:1.2", cmd.get(cmd.indexOf("/bin/sh") - 1));
    }

    @Test
    public void testStopPrefixSelectsStopScript() {
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", null, "stop_", new String[0]));
        Assert.assertEquals("./stop_app.sh", cmd.get(cmd.size() - 1));
    }

}
