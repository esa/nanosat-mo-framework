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
import esa.mo.nmf.AppStorage;
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.nmfpackage.metadata.Metadata;
import java.io.File;
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
 * The container runs as the Supervisor's own user and mounts only what the app
 * needs, so the app cannot change the software of the Supervisor or of the
 * other apps (NMF-2026-015): the NMF jars, the shared libraries and the NMF
 * Java runtimes are mounted read-only, and the app's own directory, logs and
 * storage read-write. It drops every Linux capability and cannot gain any, and
 * its processes, memory and processor time are capped.
 *
 * <p>
 * The container image, network and resource caps are configurable:
 * <ul>
 * <li>{@code esa.mo.nmf.packagemanager.docker.image} (default
 * {@code eclipse-temurin:21-jre})</li>
 * <li>{@code esa.mo.nmf.packagemanager.docker.network} (default {@code host},
 * so the app reaches the Supervisor's Directory as it would on the host)</li>
 * <li>{@code esa.mo.nmf.packagemanager.docker.pidsLimit} (default {@code 512};
 * {@code 0} turns the cap off)</li>
 * <li>{@code esa.mo.nmf.packagemanager.docker.memory} (a Docker memory value
 * such as {@code 512m}; by default the app's maximum heap plus 256 MB; empty
 * turns the cap off)</li>
 * <li>{@code esa.mo.nmf.packagemanager.docker.cpus} (a Docker CPU value such as
 * {@code 1.5}; off by default)</li>
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

    /** System property overriding the limit on the number of processes; 0 turns it off. */
    public static final String PIDS_LIMIT_PROPERTY = "esa.mo.nmf.packagemanager.docker.pidsLimit";
    private static final String DEFAULT_PIDS_LIMIT = "512";

    /** System property overriding the memory cap; empty turns it off. Default: the app's max heap plus this. */
    public static final String MEMORY_PROPERTY = "esa.mo.nmf.packagemanager.docker.memory";
    private static final int MEMORY_OVERHEAD_MB = 256;

    /** System property setting the processor cap (a Docker --cpus value); off by default. */
    public static final String CPUS_PROPERTY = "esa.mo.nmf.packagemanager.docker.cpus";

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

    /**
     * The container name for an app, sanitised to the characters Docker allows
     * in a container name.
     */
    private static String containerName(final String appName) {
        return "nmf-app-" + appName.replaceAll("[^a-zA-Z0-9_.-]", "-");
    }

    @Override
    protected String[] assembleCommand(final String workDir, final String appName,
            final String runAs, final String[] env) {
        final String script = "start_app.sh";

        ArrayList<String> ret = new ArrayList<>();
        ret.add(DOCKER_CMD);
        ret.add("run");
        ret.add("--rm"); // remove the container once it exits
        ret.add("--name");
        ret.add(containerName(appName));
        ret.add("--network");
        ret.add(network()); // reach the Supervisor's Directory service

        // The container runs as the Supervisor's user and never as root, so a
        // file the app writes belongs to that user, as under the other modes
        ret.add("--user");
        ret.add(supervisorUser());

        // No capabilities, and none can be gained: an app needs neither
        ret.add("--cap-drop");
        ret.add("ALL");
        ret.add("--security-opt");
        ret.add("no-new-privileges");

        // Resource caps, so that one app cannot starve the others (NMF-2026-015)
        addResourceLimits(ret, appName);

        // Only what the app needs is mounted, at the same absolute path so the
        // start script resolves it: the NMF jars, the shared libraries and the
        // NMF Java runtimes read-only. The whole NMF root is not mounted, so the
        // app cannot change the Supervisor's software or the other apps.
        roMount(ret, Deployment.getJarsNMFDir());
        roMount(ret, Deployment.getJarsSharedDir());
        roMount(ret, Deployment.getJavaDir());

        // The app's own directory, its logs and its storage read-write
        rwMount(ret, new File(workDir));
        rwMount(ret, Deployment.getLogsDirForApp(appName.replaceAll("space-app-", "")));
        rwMount(ret, AppStorage.getAppDir(appName));

        // The app's own credential for this run, read-only, at the path it is told.
        // There is none when access control is not in use; Docker would create a
        // directory in place of a missing file, so nothing is mounted then.
        File credential = storageCredentialFileOf(appName);
        if (credential.isFile()) {
            ret.add("-v");
            ret.add(credential.getAbsolutePath() + ":" + credential.getAbsolutePath() + ":ro");
        }
        // The app's storage is the Supervisor's home plus .nmf-apps/<app>, so the
        // home must be the Supervisor's for AppStorage to resolve the same path
        ret.add("-e");
        ret.add("HOME=" + System.getProperty("user.home"));
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

    /** The uid:gid of the user that runs the Supervisor. */
    private static String supervisorUser() {
        return idValue("u") + ":" + idValue("g");
    }

    private static String idValue(final String flag) {
        try {
            Process p = new ProcessBuilder("id", "-" + flag).redirectErrorStream(true).start();
            String out = new String(p.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8).trim();
            p.waitFor(5, TimeUnit.SECONDS);
            return out;
        } catch (IOException ex) {
            throw new IllegalStateException("The id of the Supervisor's user could not be read", ex);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while reading the id of the Supervisor's user", ex);
        }
    }

    /** Mounts a directory read-only at the same path, if it exists. */
    private static void roMount(final ArrayList<String> command, final File dir) {
        if (dir.isDirectory()) {
            command.add("-v");
            command.add(dir.getAbsolutePath() + ":" + dir.getAbsolutePath() + ":ro");
        }
    }

    /** Mounts a directory read-write at the same path, creating it if needed. */
    private static void rwMount(final ArrayList<String> command, final File dir) {
        dir.mkdirs();
        command.add("-v");
        command.add(dir.getAbsolutePath() + ":" + dir.getAbsolutePath());
    }

    /** Adds the --pids-limit, --memory and --cpus caps, as the properties set them. */
    private void addResourceLimits(final ArrayList<String> command, final String appName) {
        String pids = System.getProperty(PIDS_LIMIT_PROPERTY, DEFAULT_PIDS_LIMIT);
        if (!"0".equals(pids.trim())) {
            command.add("--pids-limit");
            command.add(pids.trim());
        }
        String memory = System.getProperty(MEMORY_PROPERTY, defaultMemory(appName));
        if (memory != null && !memory.trim().isEmpty()) {
            command.add("--memory");
            command.add(memory.trim());
        }
        String cpus = System.getProperty(CPUS_PROPERTY);
        if (cpus != null && !cpus.trim().isEmpty()) {
            command.add("--cpus");
            command.add(cpus.trim());
        }
    }

    /**
     * The app's maximum heap, read from its receipt, plus an overhead for the
     * rest of the JVM, or null if the heap is not known, so that no cap is set
     * rather than one that could be below what the app asked for.
     */
    private String defaultMemory(final String appName) {
        int heapMB = maxHeapMB(appName);
        return (heapMB > 0) ? (heapMB + MEMORY_OVERHEAD_MB) + "m" : null;
    }

    private int maxHeapMB(final String appName) {
        File receipt = new File(Deployment.getInstallationsTrackerDir(), appName + ".receipt");
        if (!receipt.isFile()) {
            return 0;
        }
        try {
            Metadata metadata = Metadata.load(receipt);
            if (!metadata.isApp()) {
                return 0;
            }
            return heapToMB(metadata.castToApp().getAppMaxHeap());
        } catch (IOException ex) {
            LOGGER.log(Level.FINE, "The max heap of " + appName + " could not be read from its receipt", ex);
            return 0;
        }
    }

    /** Converts a Java heap size such as {@code 128m} or {@code 2g} to whole MB, or 0 if it cannot be read. */
    private static int heapToMB(final String heap) {
        if (heap == null || heap.isEmpty()) {
            return 0;
        }
        String value = heap.trim().toLowerCase();
        char unit = value.charAt(value.length() - 1);
        try {
            if (unit == 'g') {
                return Integer.parseInt(value.substring(0, value.length() - 1)) * 1024;
            }
            if (unit == 'm') {
                return Integer.parseInt(value.substring(0, value.length() - 1));
            }
            if (unit == 'k') {
                return Integer.parseInt(value.substring(0, value.length() - 1)) / 1024;
            }
            return Integer.parseInt(value) / (1024 * 1024);
        } catch (NumberFormatException ex) {
            return 0;
        }
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
