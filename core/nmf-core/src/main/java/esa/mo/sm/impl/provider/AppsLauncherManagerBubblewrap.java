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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bubblewrap-based implementation of the Apps Launcher manager.
 *
 * Each app is launched inside a bubblewrap sandbox that mounts only what the
 * app needs: the system directories, the Java runtime and the NMF jars
 * read-only, and the app's own directory, logs and storage read-write. Every
 * app runs as the Supervisor's user, so nothing else of the host or of the NMF
 * is mounted: not the other apps, not the logs and not the packages. The
 * sandbox has its own {@code /proc}, {@code /dev} and {@code /tmp}, has no
 * capabilities and cannot gain any. The sandbox environment is cleared and
 * only {@code JAVA_OPTS} (carrying the central directory URI) and a minimal
 * {@code PATH} are set explicitly.
 *
 * @author Cesar Coelho
 */
public class AppsLauncherManagerBubblewrap extends AppsLauncherManager {

    private static final Logger LOGGER = Logger.getLogger(
            AppsLauncherManagerBubblewrap.class.getName());

    private static final String BWRAP_CMD = "/usr/bin/bwrap";
    private static final String DEFAULT_PATH = "/usr/local/bin:/usr/bin:/bin";

    /**
     * The system directories, mounted read-only where they exist. On a system
     * with a merged /usr, the top-level ones are links into it.
     */
    private static final String[] SYSTEM_DIRS = {"/usr", "/etc", "/bin", "/sbin", "/lib", "/lib32", "/lib64", "/libx32"};

    /**
     * Creates a new {@code AppsLauncherManagerBubblewrap}.
     *
     * @param comServices the COM services
     */
    public AppsLauncherManagerBubblewrap(COMServicesProvider comServices) {
        super(comServices);
    }

    @Override
    protected String[] assembleCommand(final String workDir, final String appName,
            final String runAs, final String[] env) {
        ArrayList<String> ret = new ArrayList<>();
        String trimmedAppName = appName.replaceAll("space-app-", "");

        ret.add(BWRAP_CMD);

        // Isolate all namespaces except network
        ret.add("--unshare-all");
        ret.add("--share-net");

        // Only what the App needs is mounted: every App runs as the Supervisor's
        // user, so the App could read anything else that is (NMF-2026-014). The
        // system and the Java runtime of the Supervisor, which can be outside of
        // it, read-only
        for (String dir : SYSTEM_DIRS) {
            roBindIfExists(ret, dir);
        }
        roBindIfExists(ret, System.getProperty("java.home"));

        // The NMF jars, the shared libraries and the Java runtimes of the NMF,
        // which the start script uses, read-only
        roBindIfExists(ret, Deployment.getJarsNMFDir().getAbsolutePath());
        roBindIfExists(ret, Deployment.getJarsSharedDir().getAbsolutePath());
        roBindIfExists(ret, Deployment.getJavaDir().getAbsolutePath());

        // App directory read-write (state files, config written by the app)
        ret.add("--bind");
        ret.add(workDir);
        ret.add(workDir);

        // Log directory read-write
        String logDir = Deployment.getLogsDirForApp(trimmedAppName).getAbsolutePath();
        ret.add("--bind");
        ret.add(logDir);
        ret.add(logDir);

        // App's own storage ($HOME/.nmf-apps/<app>/) - AppStorage/OneInstanceLock need
        // it writable (NanoSatMOConnectorImpl.init()). Created here because bwrap
        // --bind requires the source to exist, but the App only creates it lazily.
        // Binds only this app's subdirectory, not the shared .nmf-apps parent, so
        // other tenants' storage stays hidden.
        String appHomeDir = appStorageDirOf(workDir);
        if (appHomeDir != null) {
            ret.add("--bind");
            ret.add(appHomeDir);
            ret.add(appHomeDir);
        }

        ret.add("--tmpfs");
        ret.add("/tmp");

        // A /proc of the sandbox's own process namespace, so that the App sees
        // its own processes only, not those of the host
        ret.add("--proc");
        ret.add("/proc");

        // A new /dev with only the standard devices (null, zero, random, urandom,
        // tty, ...): the read-only host /dev cannot even be written to at /dev/null
        ret.add("--dev");
        ret.add("/dev");

        // Kill the sandbox if the Supervisor dies
        ret.add("--die-with-parent");

        ret.add("--chdir");
        ret.add(workDir);

        // Clear the inherited environment and pass only what the app needs
        ret.add("--clearenv");

        for (String envVar : env) {
            int idx = envVar.indexOf('=');
            if (idx > 0) {
                ret.add("--setenv");
                ret.add(envVar.substring(0, idx));
                ret.add(envVar.substring(idx + 1));
            }
        }

        ret.add("--setenv");
        ret.add("PATH");
        ret.add(DEFAULT_PATH);

        ret.add("./start_app.sh");

        return ret.toArray(new String[0]);
    }

    /** Mounts a directory read-only at the same path, if it exists. */
    private static void roBindIfExists(final ArrayList<String> command, final String dir) {
        if (dir == null) {
            return;
        }
        command.add("--ro-bind-try");
        command.add(dir);
        command.add(dir);
    }

    @Override
    protected HashMap<String, String> assembleAppLauncherEnvironment(
            final String directoryServiceURI) {
        final HashMap<String, String> targetEnv = new HashMap<>();
        targetEnv.put("JAVA_OPTS",
                "-D" + Const.CENTRAL_DIRECTORY_URI_PROPERTY + "=" + directoryServiceURI);
        return targetEnv;
    }

    /**
     * The storage directory the App will write to, which is what has to be bound.
     * <p>
     * The name has to be worked out the same way the App works it out, or the directory
     * bound here is not the one it uses and it fails exactly as it did before anything was
     * bound at all. The App takes the <em>canonical</em> name of its working directory -
     * {@code NanoSatMOConnectorImpl.init()} does
     * {@code new File(new File("").getCanonicalPath()).getName()}, having been chdir'd
     * here - puts it in {@code HelperMisc.PROP_MO_APP_NAME}, and {@code AppStorage} builds
     * the path from that. So this canonicalises too: the two agree for an ordinary
     * directory, and would part company the moment an App folder is a symbolic link to a
     * differently named one.
     *
     * @param workDir The App's working directory.
     * @return the directory to bind, created if it did not exist, or null if it cannot
     * be worked out, in which case nothing is bound rather than a path that does not exist.
     */
    private static String appStorageDirOf(final String workDir) {
        try {
            String appName = new File(workDir).getCanonicalFile().getName();
            return AppStorage.getAppDir(appName).getAbsolutePath();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not resolve the App directory: " + workDir, ex);
            return null;
        }
    }

}
