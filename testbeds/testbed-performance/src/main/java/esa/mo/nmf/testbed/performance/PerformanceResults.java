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
package esa.mo.nmf.testbed.performance;

import esa.mo.nmf.testbed.performance.cli.CLIResult;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * Accumulates per-command timing measurements and writes them to the
 * performance results file at the end of the test run.
 *
 * <p>
 * The output file path is read from the {@code nmf.perf.results} system
 * property injected by the Surefire configuration in the testbed pom.
 * The file is uploaded as a CI artifact so results can be compared across runs.
 *
 * <p>
 * File format (one section per measured command):
 * <pre>
 * # NMF CLI Tool - Performance Results
 * # Timestamp: 2026-05-30T10:00:00Z
 * # Java: 11.0.22 OpenJDK 64-Bit Server VM
 *
 * [list-apps]
 * run_1_ms=142
 * run_2_ms=138
 * run_3_ms=140
 * average_ms=140
 * </pre>
 */
public class PerformanceResults {

    public static final String PROP_RESULTS = "nmf.perf.results";
    private static final Logger LOGGER = Logger.getLogger(PerformanceResults.class.getName());

    /** command name -> [run1, run2, run3] */
    private final Map<String, CLIResult[]> measurements = new TreeMap<>();

    /**
     * Records three CLI invocation results for the given command label.
     *
     * @param command display name used as the section header in the output file;
     *                should include the tier prefix, e.g. {@code "tier-1-help"}.
     * @param run1    result of the first invocation.
     * @param run2    result of the second invocation.
     * @param run3    result of the third invocation.
     */
    public void record(String command, CLIResult run1, CLIResult run2, CLIResult run3) {
        measurements.put(command, new CLIResult[]{run1, run2, run3});
    }

    /**
     * Writes all recorded measurements to the results file and one log file per
     * run (named {@code <command>-runN.log}) in the same directory, then logs
     * the results file path.
     *
     * @throws IOException if the results path property is missing or any file
     *                     cannot be written.
     */
    public void save() throws IOException {
        String path = System.getProperty(PROP_RESULTS);
        if (path == null) {
            throw new IOException("System property '" + PROP_RESULTS + "' is not set.");
        }
        File file = new File(path);
        file.getParentFile().mkdirs();

        long maxMemoryMb = ManagementFactory.getMemoryMXBean()
                .getHeapMemoryUsage().getMax() / (1024 * 1024);

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("# NMF CLI Tool - Performance Results");
            pw.println("# Timestamp: " + Instant.now());
            pw.println("# Java: " + System.getProperty("java.version")
                    + " " + System.getProperty("java.vm.name"));
            pw.println("# OS: " + System.getProperty("os.name")
                    + " " + System.getProperty("os.version")
                    + " (" + System.getProperty("os.arch") + ")");
            pw.println("# CPU cores: " + Runtime.getRuntime().availableProcessors());
            pw.println("# JVM heap max: " + maxMemoryMb + " MB");
            pw.println();

            for (Map.Entry<String, CLIResult[]> entry : measurements.entrySet()) {
                CLIResult[] runs = entry.getValue();
                long average = (runs[0].elapsedMs + runs[1].elapsedMs + runs[2].elapsedMs) / 3;
                pw.println("[" + entry.getKey() + "]");
                pw.println("run_1_ms=" + runs[0].elapsedMs + "  exit=" + runs[0].exitCode);
                pw.println("run_2_ms=" + runs[1].elapsedMs + "  exit=" + runs[1].exitCode);
                pw.println("run_3_ms=" + runs[2].elapsedMs + "  exit=" + runs[2].exitCode);
                pw.println("average_ms=" + average);
                pw.println();

                writeRunLog(file.getParentFile(), entry.getKey(), runs);
            }
        }
        LOGGER.info("Performance results written to: " + file.getAbsolutePath());
    }

    private static void writeRunLog(File dir, String command, CLIResult[] runs) throws IOException {
        for (int i = 0; i < runs.length; i++) {
            File logFile = new File(dir, command + "-run" + (i + 1) + ".log");
            try (PrintWriter pw = new PrintWriter(logFile)) {
                pw.println("# command: " + command + "  run: " + (i + 1));
                pw.println("# elapsed_ms: " + runs[i].elapsedMs);
                pw.println("# exit_code: " + runs[i].exitCode);
                pw.println();
                pw.print(runs[i].output);
            }
        }
    }

}
