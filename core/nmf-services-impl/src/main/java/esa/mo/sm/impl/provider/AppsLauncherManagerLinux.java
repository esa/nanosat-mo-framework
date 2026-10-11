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
    protected String[] assembleCommand(final String workDir, final String appName,
            final String runAs, final String[] env) {
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

        // The command is run by a shell, so every value in it is quoted and stays one
        // word whatever characters it holds. The script only runs if the cd succeeded:
        // under 'su -' the shell starts in the user's home directory.
        StringBuilder command = new StringBuilder("cd ").append(shellQuote(workDir)).append(" && ");
        for (String envVar : env) {
            int eq = envVar.indexOf('=');
            if (eq <= 0) {
                continue; // Not a NAME=value pair
            }
            // Only the value is quoted: a quoted NAME=value would no longer be an assignment.
            command.append(envVar, 0, eq + 1).append(shellQuote(envVar.substring(eq + 1))).append(' ');
        }
        command.append("./start_app.sh");
        ret.add(command.toString());

        return ret.toArray(new String[0]);
    }

    /**
     * Quotes a value for a POSIX shell so that it is read as a single word with
     * no special characters, by wrapping it in single quotes.
     *
     * @param value the value to quote
     * @return the quoted value
     */
    static String shellQuote(final String value) {
        return "'" + value.replace("'", "'\\''") + "'";
    }

    @Override
    protected HashMap<String, String> assembleAppLauncherEnvironment(final String directoryServiceURI) {
        final HashMap<String, String> targetEnv = new HashMap<>();
        final Map<String, String> parentEnv = ProcessEnvironment.ofThisProcess();
        if (parentEnv.containsKey("NMF_LIB")) {
            targetEnv.put("NMF_LIB", parentEnv.get("NMF_LIB"));
        }
        if (parentEnv.containsKey("NMF_HOME")) {
            targetEnv.put("NMF_HOME", parentEnv.get("NMF_HOME"));
        }
        if (parentEnv.containsKey("PATH")) {
            targetEnv.put("PATH", parentEnv.get("PATH"));
        }
        targetEnv.put("JAVA_OPTS",
                "-D" + Const.CENTRAL_DIRECTORY_URI_PROPERTY + "=" + directoryServiceURI);
        return targetEnv;
    }
}
