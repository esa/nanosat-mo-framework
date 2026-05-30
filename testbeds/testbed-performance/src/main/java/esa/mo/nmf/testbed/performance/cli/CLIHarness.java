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
package esa.mo.nmf.testbed.performance.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Manages the lifecycle of CLI Tool invocations for performance tests.
 *
 * <p>
 * Each call to {@link #run(String...)} spawns a fresh JVM process running the
 * CLI fat jar, measures wall-clock time from process start to exit, and
 * returns the elapsed milliseconds. Spawning a fresh JVM per invocation
 * is intentional: it captures the full startup cost that end users experience.
 *
 * <p>
 * The path to the CLI fat jar is read from the {@code nmf.perf.cli.jar}
 * system property injected by the Surefire configuration in the testbed pom.
 */
public class CLIHarness {

    public static final String PROP_CLI_JAR = "nmf.perf.cli.jar";
    private static final int COMMAND_TIMEOUT_SECONDS = 30;
    private static final Logger LOGGER = Logger.getLogger(CLIHarness.class.getName());

    private final File cliJar;

    public CLIHarness() throws IOException {
        String jarPath = System.getProperty(PROP_CLI_JAR);
        if (jarPath == null) {
            throw new IOException("System property '" + PROP_CLI_JAR + "' is not set. "
                    + "Run via Maven (mvn test) so the CLI jar path is injected.");
        }
        this.cliJar = new File(jarPath);
        if (!cliJar.exists()) {
            throw new IOException("CLI jar not found: " + cliJar.getAbsolutePath());
        }
    }

    /**
     * Spawns the CLI Tool in a fresh JVM, waits for it to exit, and returns
     * the wall-clock elapsed time in milliseconds.
     *
     * @param args command-line arguments forwarded to the CLI tool.
     * @return elapsed time in milliseconds from process start to exit.
     * @throws IOException          if the process could not be started or timed out.
     * @throws InterruptedException if the calling thread is interrupted while waiting.
     */
    public long run(String... args) throws IOException, InterruptedException {
        List<String> cmd = new ArrayList<>();
        cmd.add("java");
        cmd.add("-jar");
        cmd.add(cliJar.getAbsolutePath());
        cmd.addAll(Arrays.asList(args));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);

        long start = System.currentTimeMillis();
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                LOGGER.fine("CLI output: " + line);
            }
        }

        if (!process.waitFor(COMMAND_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new IOException("CLI command timed out after " + COMMAND_TIMEOUT_SECONDS
                    + "s: " + cmd);
        }

        long elapsed = System.currentTimeMillis() - start;
        LOGGER.info("CLI " + Arrays.toString(args) + " -> " + elapsed + " ms (exit "
                + process.exitValue() + ")");
        return elapsed;
    }

}
