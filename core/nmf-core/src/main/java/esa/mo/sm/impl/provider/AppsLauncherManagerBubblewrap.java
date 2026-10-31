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
 * Each app is launched inside a bubblewrap sandbox: the full host filesystem
 * is mounted read-only, then the app directory and its log directory are
 * bind-mounted read-write. The sandbox environment is cleared and only
 * {@code JAVA_OPTS} (carrying the central directory URI) and a minimal
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

        // Full filesystem read-only, then selectively open writable paths
        ret.add("--ro-bind");
        ret.add("/");
        ret.add("/");

        // Hide what an App must not read: the storage of every App, where their
        // credentials are (its own is bound back below), and the bootloader
        // directory, which holds the credential of ground
        ret.add("--tmpfs");
        ret.add(AppStorage.getStorageRootDir().getAbsolutePath());
        ret.add("--tmpfs");
        ret.add(Deployment.getBootloaderDir().getAbsolutePath());

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
