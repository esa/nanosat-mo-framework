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
package esa.mo.nmf.testbed.e2e;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Manages the lifecycle of an NMF Supervisor process for end-to-end tests.
 *
 * <p>
 * The filesystem used is generated fresh during the build
 * (generate-test-resources phase) and its root path is read from the
 * {@code nmf.e2e.filesystem} system property injected by the Surefire
 * configuration in the testbed pom.
 *
 * <p>
 * The Supervisor is launched via {@code start_supervisor.sh} so that any
 * breakage in the script is caught by the tests. Because the script pipes
 * output through {@code tee}, {@link Process#destroy()} only kills the shell;
 * {@link #tearDown()} therefore uses {@link ProcessHandle#descendants()} to
 * terminate the full process tree and avoid orphans holding the MAL port.
 *
 * <p>
 * Readiness is determined by scanning the Supervisor's stdout for the
 * {@code URI: maltcp://...-Directory} line that the Supervisor emits once the
 * Directory service is fully initialised. This line also carries the URI used
 * by the tests to connect to the Directory service.
 *
 * @author Cesar Coelho
 */
public class SupervisorHarness {

    public static final String PROP_FILESYSTEM = "nmf.e2e.filesystem";
    private static final int STARTUP_TIMEOUT_SECONDS = 10;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 10;

    private static final Logger LOGGER = Logger.getLogger(SupervisorHarness.class.getName());

    private File nmfDir;
    private Process process;
    private Thread logDrainer;
    private final List<String> logLines = new ArrayList<>();
    private volatile String directoryURI;

    /**
     * Starts the Supervisor from the generated NMF filesystem and blocks until
     * it is ready.
     *
     * @throws IOException if the filesystem property is missing, the directory
     * does not exist, or the Supervisor does not become ready within the
     * timeout.
     */
    public void setUp() throws IOException {
        String path = System.getProperty(PROP_FILESYSTEM);
        if (path == null) {
            throw new IOException("System property '" + PROP_FILESYSTEM + "' is not set. "
                    + "Run via Maven (mvn test) so the filesystem is generated first.");
        }
        setUp(new File(path));
    }

    /**
     * Starts the Supervisor from a given NMF filesystem and blocks until it is
     * ready. Used by the tests that work on a filesystem of their own rather
     * than on the one that the property points at.
     *
     * @param filesystem The root of the NMF filesystem to start from.
     * @throws IOException if the directory does not exist, or the Supervisor
     * does not become ready within the timeout.
     */
    public void setUp(final File filesystem) throws IOException {
        nmfDir = filesystem;
        if (!nmfDir.exists()) {
            throw new IOException("NMF filesystem directory not found: " + nmfDir.getAbsolutePath());
        }

        ProcessBuilder pb = buildSupervisorProcess(nmfDir);
        process = pb.start();

        // Drain stdout/stderr in the background so the internal pipe never blocks.
        // waitForReadiness() also scans these lines for the Directory URI.
        logDrainer = new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    synchronized (logLines) {
                        logLines.add(line);
                    }
                }
            } catch (IOException ignored) {
                // Process ended; normal exit path.
            }
        }, "supervisor-log-drainer");
        logDrainer.setDaemon(true);
        logDrainer.start();

        this.waitForReadiness();
        LOGGER.info("Supervisor is ready. Directory URI: " + directoryURI);
    }

    /**
     * Stops the Supervisor process and waits for it to exit.
     *
     * @throws IOException if the process could not be stopped.
     */
    public void tearDown() throws IOException {
        if (process != null) {
            // TERM the bootloader script first: its trap kills the supervisor
            // JVM and the log duplication (this also exercises the trap path).
            process.destroy();
            try {
                if (!process.waitFor(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    process.descendants().forEach(ProcessHandle::destroyForcibly);
                    process.destroyForcibly();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // Fallback: no descendants may survive the script
            process.descendants().forEach(ProcessHandle::destroyForcibly);
            process = null;
        }
        if (logDrainer != null) {
            logDrainer.interrupt();
            logDrainer = null;
        }
    }

    /**
     * Returns a snapshot of all lines written to the Supervisor's
     * stdout/stderr.
     *
     * @return unmodifiable list of log lines captured so far.
     */
    public List<String> getProviderLog() {
        synchronized (logLines) {
            return Collections.unmodifiableList(new ArrayList<>(logLines));
        }
    }

    /**
     * Returns true if any captured log line starts with {@code WARNING:}.
     *
     * @return true if warnings were logged.
     */
    public boolean hasWarnings() {
        synchronized (logLines) {
            for (String line : logLines) {
                if (line.startsWith("WARNING:")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if any captured log line starts with {@code SEVERE:}.
     *
     * @return true if errors were logged.
     */
    public boolean hasErrors() {
        synchronized (logLines) {
            for (String line : logLines) {
                if (line.startsWith("SEVERE:")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns the NMF root directory of the generated filesystem.
     *
     * @return the NMF root directory.
     */
    public File getNmfDir() {
        return nmfDir;
    }

    /**
     * Returns the Directory service URI extracted from the Supervisor's stdout
     * during startup.
     *
     * @return the Directory service URI string.
     * @throws IOException if the URI has not been captured yet.
     */
    public String getDirectoryURI() throws IOException {
        if (directoryURI == null) {
            throw new IOException("Directory URI not yet captured — supervisor may not have started.");
        }
        return directoryURI;
    }

    /**
     * Builds a {@link ProcessBuilder} that runs {@code start_supervisor.sh}.
     */
    private static ProcessBuilder buildSupervisorProcess(File nmfDir) throws IOException {
        File script = new File(nmfDir, "start_supervisor.sh");
        if (!script.exists()) {
            throw new IOException("start_supervisor.sh not found: " + script.getAbsolutePath());
        }
        LOGGER.info("Launching supervisor via: " + script.getAbsolutePath());
        ProcessBuilder pb = new ProcessBuilder("bash", script.getAbsolutePath());
        pb.directory(nmfDir);
        pb.redirectErrorStream(true);
        return pb;
    }

    private void waitForReadiness() throws IOException {
        long deadline = System.currentTimeMillis() + STARTUP_TIMEOUT_SECONDS * 1000L;
        while (System.currentTimeMillis() < deadline) {
            if (!process.isAlive()) {
                throw new IOException("Supervisor process exited unexpectedly during startup. "
                        + "Log:\n" + String.join("\n", getProviderLog()));
            }

            // Scan for the Directory URI (usually the last message when Supervisor is ready!
            String uri = scanForDirectoryURI();
            if (uri != null) {
                directoryURI = uri;
                return;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Interrupted while waiting for Supervisor to start.");
            }
        }
        if (!process.isAlive()) {
            throw new IOException("Supervisor process exited before becoming ready. "
                    + "Log:\n" + String.join("\n", getProviderLog()));
        }
        throw new IOException("Supervisor did not become ready within "
                + STARTUP_TIMEOUT_SECONDS + " seconds. "
                + "Log:\n" + String.join("\n", getProviderLog()));
    }

    /**
     * Scans captured log lines for the Directory URI emitted at the end of
     * startup.
     */
    private String scanForDirectoryURI() {
        synchronized (logLines) {
            for (String line : logLines) {
                String uri = LogScanner.extractDirectoryURI(line);
                if (uri != null) {
                    return uri;
                }
            }
        }
        return null;
    }

}
