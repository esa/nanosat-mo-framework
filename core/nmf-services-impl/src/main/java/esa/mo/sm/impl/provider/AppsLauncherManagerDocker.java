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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.environment.Deployment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.sm.structures.AppDetails;

/**
 * Docker-based implementation of the Apps Launcher manager.
 *
 * Each app is launched inside its own Docker container, while the Supervisor
 * keeps managing the app lifecycle (start, stop, kill) as for any other
 * isolation mode. The app's own {@code start_app.sh} runs unchanged inside the
 * container: the whole NMF home is bind-mounted at the same absolute path, so
 * the absolute references the script carries (shared jars, log directory)
 * resolve, and the central directory URI is passed through {@code JAVA_OPTS}
 * exactly as for the bubblewrap mode.
 *
 * <p>
 * The container image and network are configurable:
 * <ul>
 * <li>{@code esa.mo.nmf.packagemanager.docker.image} (default
 * {@code eclipse-temurin:21-jre})</li>
 * <li>{@code esa.mo.nmf.packagemanager.docker.network} (default {@code host},
 * so the app reaches the Supervisor's Directory as it would on the host)</li>
 * </ul>
 *
 * <p>
 * A graceful stop needs no special handling: a STOP_REQUESTED reaches the app,
 * its connector closes, the JVM exits, the {@code docker run --rm} client exits
 * and the process is reaped as usual. Only the forced kill is overridden,
 * because killing the {@code docker run} client alone does not reliably stop the
 * container.
 *
 * @author Cesar Coelho
 */
public class AppsLauncherManagerDocker extends AppsLauncherManager {

    private static final Logger LOGGER = Logger.getLogger(
            AppsLauncherManagerDocker.class.getName());

    private static final String DOCKER_CMD = "docker";

    /** System property overriding the app container base image. */
    public static final String IMAGE_PROPERTY = "esa.mo.nmf.packagemanager.docker.image";
    private static final String DEFAULT_IMAGE = "eclipse-temurin:21-jre";

    /** System property overriding the app container network. */
    public static final String NETWORK_PROPERTY = "esa.mo.nmf.packagemanager.docker.network";
    private static final String DEFAULT_NETWORK = "host";

    private static final long DOCKER_RM_TIMEOUT_S = 10;

    private static String image() {
        return System.getProperty(IMAGE_PROPERTY, DEFAULT_IMAGE);
    }

    private static String network() {
        return System.getProperty(NETWORK_PROPERTY, DEFAULT_NETWORK);
    }

    /**
     * Creates a new {@code AppsLauncherManagerDocker}.
     *
     * @param comServices the COM services
     */
    public AppsLauncherManagerDocker(COMServicesProvider comServices) {
        super(comServices);
    }

    @Override
    protected String getScriptExtension() {
        return ".sh";
    }

    /**
     * The container name for an app, sanitised to the characters Docker allows
     * in a container name.
     */
    private static String containerName(final String appName) {
        return "nmf-app-" + appName.replaceAll("[^a-zA-Z0-9_.-]", "-");
    }

    @Override
    protected String[] assembleCommand(final String workDir, final String appName,
            final String runAs, final String prefix, final String[] env) {
        final String script = prefix + "app.sh"; // start_app.sh / stop_app.sh
        final String nmfHome = Deployment.getNMFRootDir().getAbsolutePath();

        ArrayList<String> ret = new ArrayList<>();
        ret.add(DOCKER_CMD);
        ret.add("run");
        ret.add("--rm"); // remove the container once it exits
        ret.add("--name");
        ret.add(containerName(appName));
        ret.add("--network");
        ret.add(network()); // reach the Supervisor's Directory service
        // Mount the NMF home at the same absolute path so the app's start script
        // finds the shared jars and its log directory.
        ret.add("-v");
        ret.add(nmfHome + ":" + nmfHome);
        ret.add("-w");
        ret.add(workDir);
        for (String envVar : env) {
            ret.add("-e");
            ret.add(envVar);
        }
        ret.add(image());
        ret.add("/bin/sh");
        ret.add("-c");
        ret.add("./" + script);

        return ret.toArray(new String[0]);
    }

    @Override
    protected HashMap<String, String> assembleAppLauncherEnvironment(
            final String directoryServiceURI) {
        final HashMap<String, String> targetEnv = new HashMap<>();
        targetEnv.put("JAVA_OPTS",
                "-D" + Const.CENTRAL_DIRECTORY_URI_PROPERTY + "=" + directoryServiceURI);
        return targetEnv;
    }

    @Override
    protected boolean killAppProcess(final Long appInstId, final MALInteraction interaction) {
        final AppDetails app = (AppDetails) this.getDef(appInstId);
        if (app != null) {
            // Killing the 'docker run' client alone (what the base class does)
            // does not reliably stop the container, so force-remove it by name.
            final String name = containerName(app.getName().getValue());
            try {
                Process p = new ProcessBuilder(DOCKER_CMD, "rm", "-f", name)
                        .redirectErrorStream(true).start();
                if (!p.waitFor(DOCKER_RM_TIMEOUT_S, TimeUnit.SECONDS)) {
                    p.destroyForcibly();
                }
            } catch (IOException ex) {
                LOGGER.log(Level.WARNING, "Failed to run 'docker rm -f " + name + "'", ex);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return super.killAppProcess(appInstId, interaction);
    }

}
