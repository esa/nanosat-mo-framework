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
import java.util.ArrayList;
import java.util.HashMap;
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

}
