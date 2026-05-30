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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
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
    private final Map<String, long[]> measurements = new LinkedHashMap<>();

    /**
     * Records three timing samples for the given command label.
     *
     * @param command display name used as the section header in the output file.
     * @param run1    elapsed milliseconds for the first run.
     * @param run2    elapsed milliseconds for the second run.
     * @param run3    elapsed milliseconds for the third run.
     */
    public void record(String command, long run1, long run2, long run3) {
        measurements.put(command, new long[]{run1, run2, run3});
    }

    /**
     * Writes all recorded measurements to the results file and logs its path.
     *
     * @throws IOException if the results path property is missing or the file
     *                     cannot be written.
     */
    public void save() throws IOException {
        String path = System.getProperty(PROP_RESULTS);
        if (path == null) {
            throw new IOException("System property '" + PROP_RESULTS + "' is not set.");
        }
        File file = new File(path);
        file.getParentFile().mkdirs();

        try (PrintWriter pw = new PrintWriter(file)) {
            pw.println("# NMF CLI Tool - Performance Results");
            pw.println("# Timestamp: " + Instant.now());
            pw.println("# Java: " + System.getProperty("java.version")
                    + " " + System.getProperty("java.vm.name"));
            pw.println();

            for (Map.Entry<String, long[]> entry : measurements.entrySet()) {
                long[] runs = entry.getValue();
                long average = (runs[0] + runs[1] + runs[2]) / 3;
                pw.println("[" + entry.getKey() + "]");
                pw.println("run_1_ms=" + runs[0]);
                pw.println("run_2_ms=" + runs[1]);
                pw.println("run_3_ms=" + runs[2]);
                pw.println("average_ms=" + average);
                pw.println();
            }
        }
        LOGGER.info("Performance results written to: " + file.getAbsolutePath());
    }

}
