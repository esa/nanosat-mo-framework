/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.security.probe;

import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.SimpleMonitorAndControlAdapter;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A benign NMF App used to prove the NMF-2026-003 privilege-separation fix.
 * <p>
 * It is a real NMF App: it is packaged as a {@code .nmfpackage}, installed by
 * the Supervisor's PackageManagement service, and started by its AppsLauncher
 * service like any other app. The only thing it does is observe and report, from
 * inside the launched process, the privileges the Supervisor handed it:
 * <ul>
 * <li>the operating-system user the process runs as;</li>
 * <li>whether it can read a file that only the Supervisor's user can; and</li>
 * <li>whether it can read a file that only {@code root} can.</li>
 * </ul>
 * It writes those findings to a report file and then idles as a well-behaved
 * app. The behavioural test reads the report and checks that, under isolation,
 * the app ran as its own unprivileged user and could read neither file.
 * <p>
 * The probe never attempts to escalate or to defeat anything: it only reports
 * what the environment already allows, so it is safe to ship and to run.
 */
public class PrivilegeProbe {

    /** Where to write the report; overridable so the test can place it on a shared path. */
    public static final String REPORT_PROPERTY = "esa.mo.nmf.security.probe.report";
    private static final String DEFAULT_REPORT = "/tmp/privilege-probe-report";

    /** A file only the Supervisor's user can read; its readability shows app-vs-supervisor isolation. */
    public static final String SUPERVISOR_SECRET_PROPERTY = "esa.mo.nmf.security.probe.supervisorSecret";
    private static final String DEFAULT_SUPERVISOR_SECRET = "/tmp/sup_secret";

    /** A file only root can read; its readability shows whether the app has root's reach. */
    public static final String ROOT_SECRET_PROPERTY = "esa.mo.nmf.security.probe.rootSecret";
    private static final String DEFAULT_ROOT_SECRET = "/root/secret";

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();

    /**
     * Registers nothing; the app exists only to be launched by the Supervisor.
     */
    public PrivilegeProbe() {
        connector.init(new SimpleMonitorAndControlAdapter() {
            @Override
            public void initialRegistrations(MCRegistration registration) {
                // The probe exposes no parameters or actions.
            }

            @Override
            public Serializable onGetValueSimple(String name) {
                return null;
            }

            @Override
            public boolean onSetValueSimple(String name, Serializable value) {
                return false;
            }

            @Override
            public boolean actionArrivedSimple(String name, Serializable[] values, Long executionId) {
                return false;
            }
        });
    }

    /**
     * Writes the privilege report, then connects to the Supervisor as a normal app.
     *
     * @param args unused
     */
    public static void main(final String[] args) {
        // Report first, so the finding survives even if the connector cannot reach the Supervisor.
        writeReport();

        try {
            new PrivilegeProbe();
        } catch (Throwable t) {
            // Being a connected app is not what this probe proves; the report is already written.
            System.out.println("PRIVILEGE-PROBE: connector did not start (" + t + "); report already written.");
        }
    }

    private static void writeReport() {
        String report = "USER=" + System.getProperty("user.name", "?") + "\n"
                + "SUPERVISOR_SECRET_READABLE=" + yesNo(canRead(property(SUPERVISOR_SECRET_PROPERTY,
                        DEFAULT_SUPERVISOR_SECRET))) + "\n"
                + "ROOT_SECRET_READABLE=" + yesNo(canRead(property(ROOT_SECRET_PROPERTY,
                        DEFAULT_ROOT_SECRET))) + "\n";

        Path path = Paths.get(property(REPORT_PROPERTY, DEFAULT_REPORT));
        try {
            Files.write(path, report.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            // Fall back to stdout so the finding is not lost if the report path is not writable.
            System.out.println("PRIVILEGE-PROBE: could not write " + path + " (" + e + ")");
        }
        System.out.print("PRIVILEGE-PROBE REPORT:\n" + report);
    }

    private static String property(String key, String fallback) {
        String value = System.getProperty(key);
        return (value != null) ? value : fallback;
    }

    private static boolean canRead(String file) {
        try {
            byte[] ignored = Files.readAllBytes(Paths.get(file));
            return ignored != null;
        } catch (IOException | RuntimeException e) {
            return false;
        }
    }

    private static String yesNo(boolean value) {
        return value ? "yes" : "no";
    }
}
