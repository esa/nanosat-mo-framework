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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
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
    protected String getScriptExtension() {
        return ".sh";
    }

    @Override
    protected String[] assembleCommand(final String workDir, final String appName,
            final String runAs, final String prefix, final String[] env) {
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
        // --bind requires the source to exist, but AppStorage only creates it lazily.
        // Binds only this app's subdirectory, not the shared .nmf-apps parent, so
        // other tenants' storage stays hidden.
        String appHomeDir = appStorageDirOf(workDir);
        if (appHomeDir != null) {
            mkDirAndSetPermissions(new File(appHomeDir));
            ret.add("--bind");
            ret.add(appHomeDir);
            ret.add(appHomeDir);
        }

        ret.add("--tmpfs");
        ret.add("/tmp");

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

        ret.add("./" + prefix + "app.sh");

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
     * @return the directory to bind, or null if it cannot be worked out, in which case
     * nothing is bound rather than a path that does not exist.
     */
    private static String appStorageDirOf(final String workDir) {
        try {
            return System.getProperty("user.home") + File.separator + ".nmf-apps"
                    + File.separator + new File(workDir).getCanonicalFile().getName();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Could not resolve the App directory: " + workDir, ex);
            return null;
        }
    }

    // Mirrors AppStorage.mkDirAndSetPermissions (esa.mo.nmf, not visible here):
    // requests rwxrwx---, though umask may narrow it (same as AppStorage's own lazy
    // creation would). Not an isolation concern for bubblewrap - every App runs as
    // the same OS user, so isolation comes from which dirs get bound, not these bits.
    private static void mkDirAndSetPermissions(File dir) {
        if (dir.exists()) {
            return;
        }

        mkDirAndSetPermissions(dir.getParentFile());

        Set<PosixFilePermission> posix = PosixFilePermissions.fromString("rwxrwx---");
        FileAttribute<?> permissions = PosixFilePermissions.asFileAttribute(posix);
        try {
            Files.createDirectory(dir.toPath(), permissions);
        } catch (UnsupportedOperationException ex1) {
            // Non-POSIX filesystem: create normally, then narrow to owner-only rwx
            // (mirrors AppStorage's own fallback).
            dir.mkdirs();
            dir.setExecutable(false, false);
            dir.setExecutable(true, true);
            dir.setReadable(false, false);
            dir.setReadable(true, true);
            dir.setWritable(false, false);
            dir.setWritable(true, true);
        } catch (IOException ex2) {
            LOGGER.log(Level.SEVERE, "Failed to create App storage directory: " + dir, ex2);
        }
    }

}
