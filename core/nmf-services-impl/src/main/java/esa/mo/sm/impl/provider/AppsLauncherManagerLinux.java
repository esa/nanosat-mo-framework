/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.exec.environment.EnvironmentUtils;

/**
 * Linux-specific implementation of the Apps Launcher manager.
 */
public class AppsLauncherManagerLinux extends AppsLauncherManager {

    private static final Logger LOGGER = Logger.getLogger(AppsLauncherManagerLinux.class.getName());

    private boolean sudoAvailable = false;

    /**
     * Creates a new {@code AppsLauncherManagerLinux}.
     *
     * @param comServices the COM services
     */
    public AppsLauncherManagerLinux(COMServicesProvider comServices) {
        super(comServices);
        try {
            String[] params = new String[]{"sh", "-c", "sudo --help"};
            Process p = Runtime.getRuntime().exec(params, null, null);
            try {
                boolean terminated = p.waitFor(1, TimeUnit.SECONDS);
                if (terminated) {
                    sudoAvailable = (p.exitValue() != 127);
                }
            } catch (InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "The process did not finish yet...", ex);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "The process could not be executed!", ex);
        }
    }

    @Override
    protected String getScriptExtension() {
        return ".sh";
    }

    @Override
    protected String[] assembleCommand(final String workDir, final String appName,
            final String runAs, final String prefix, final String[] env) {
        ArrayList<String> ret = new ArrayList<>();

        if (runAs != null) {
            if (sudoAvailable) {
                ret.add("sudo");
            }
            ret.add("su");
            ret.add("-");
            ret.add(runAs);
            ret.add("-c");
        } else {
            ret.add("/bin/sh");
            ret.add("-c");
        }

        StringBuilder envString = new StringBuilder();
        for (String envVar : env) {
            envString.append(envVar).append(" ");
        }
        String script = prefix + "app.sh";
        ret.add("cd " + workDir + ";" + envString.toString() + "./" + script);

        return ret.toArray(new String[0]);
    }

    @Override
    protected HashMap<String, String> assembleAppLauncherEnvironment(final String directoryServiceURI) {
        final HashMap<String, String> targetEnv = new HashMap<>();
        try {
            Map<String, String> parentEnv = EnvironmentUtils.getProcEnvironment();
            if (parentEnv.containsKey("NMF_LIB")) {
                targetEnv.put("NMF_LIB", parentEnv.get("NMF_LIB"));
            }
            if (parentEnv.containsKey("NMF_HOME")) {
                targetEnv.put("NMF_HOME", parentEnv.get("NMF_HOME"));
            }
            if (parentEnv.containsKey("PATH")) {
                targetEnv.put("PATH", parentEnv.get("PATH"));
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "getProcEnvironment failed!", ex);
        }
        targetEnv.put("JAVA_OPTS",
                "-D" + Const.CENTRAL_DIRECTORY_URI_PROPERTY + "=" + directoryServiceURI);
        return targetEnv;
    }
}
