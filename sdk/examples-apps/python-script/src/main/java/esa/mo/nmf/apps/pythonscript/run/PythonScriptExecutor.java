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
package esa.mo.nmf.apps.pythonscript.run;

import esa.mo.nmf.AppStorage;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Python Script Executor.
 */
public class PythonScriptExecutor {

    private static final Logger LOG = Logger.getLogger(PythonScriptExecutor.class.getName());
    private static final String ENV_PROCESS_DURATION = "ENV_PROCESS_DURATION";

    /**
     * Disables the buffering Python applies to its output when that output is not a
     * terminal.
     */
    private static final String ENV_PYTHON_UNBUFFERED = "PYTHONUNBUFFERED";
    private static final String LOG_PATH = "output_logs";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMdd_HHmmss_SSSS").withZone(ZoneId.systemDefault());

    /**
     * Value of {@code maxDurationSeconds} denoting that no maximum duration is
     * imposed on the script.
     */
    private static final long NO_TIMEOUT = -1L;

    private final long minDurationSeconds;
    private final long maxDurationMillis;
    private final MCAdapter mcAdapter;
    private final Long id;

    /**
     * The script currently being executed, retained so that it may be terminated
     * externally. Written by the thread that starts the process and read by the
     * thread requesting termination, and therefore declared volatile.
     */
    private volatile Process process;

    /**
     * Creates a new {@code PythonScriptExecutor}.
     *
     * @param mcAdapter the mc adapter
     * @param id the id
     * @param minDurationSeconds the min duration seconds
     * @param maxDurationSeconds the max duration seconds
     */
    public PythonScriptExecutor(MCAdapter mcAdapter, Long id,
            Integer minDurationSeconds, Integer maxDurationSeconds) {
        this.minDurationSeconds = toMinDuration(minDurationSeconds);
        this.maxDurationMillis = toTimeout(maxDurationSeconds);
        this.mcAdapter = mcAdapter;
        this.id = id;
    }

    /**
     * Run python script.
     *
     * @param argument0 the argument0
     * @return the run python script
     */
    public boolean runPythonScript(String argument0) {
        File logFile = initLogFile(generateLogFilename());

        // The output of the script is not reproduced here, so the location of the file
        // holding it is reported: an exit value on its own does not say why a script
        // failed, and the reason is in that file.
        LOG.info("Process " + id + " is starting:"
                + "\n  >> Min duration: " + minDurationSeconds + " seconds"
                + "\n  >> Max duration: " + describeMaximum()
                + "\n  >> Output: " + logFile.getAbsolutePath());

        ProcessBuilder builder = new ProcessBuilder("python3", "scripts/myScript.py", argument0);

        // Standard output and standard error are both redirected to the log file.
        // Redirection also avoids the requirement to consume the process output from
        // this process: a child process whose output is not consumed blocks once the
        // pipe buffer is full.
        builder.redirectErrorStream(true);
        builder.redirectOutput(Redirect.to(logFile));

        // The environment is cleared before the single required variable is set, so
        // that nothing is inherited from this process. This reproduces the behaviour of
        // the previous implementation, which supplied the environment to Runtime.exec,
        // where a non-null environment replaces the inherited one rather than
        // supplementing it.
        builder.environment().clear();
        builder.environment().put(ENV_PROCESS_DURATION, String.valueOf(minDurationSeconds));

        // Python buffers its output in blocks when that output is not a terminal, and
        // here it is a file. A script terminated for exceeding its maximum duration is
        // killed outright, so anything still held in that buffer is lost, and the log is
        // empty in precisely the case where it is most needed. Unbuffered output is
        // written as it is produced.
        builder.environment().put(ENV_PYTHON_UNBUFFERED, "1");

        final LoggingExecuteResultHandler handler = new LoggingExecuteResultHandler(mcAdapter, id);
        try {
            process = builder.start();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "The script could not be executed", e);
            return false;
        }

        watch(process, handler);
        return true;
    }

    /**
     * Registers completion handling for the script, and enforces the maximum
     * duration if one is configured.
     * <p>
     * Two independent actions are registered on process termination. The first
     * reports the exit value, whether termination was self-initiated or forced. The
     * second applies the maximum duration: on expiry the process is terminated
     * forcibly, which causes it to exit and the first action to report accordingly.
     */
    private void watch(final Process started, final LoggingExecuteResultHandler handler) {
        started.onExit().whenComplete(
                (exited, failure) -> handler.onProcessEnded(exited.exitValue()));

        if (maxDurationMillis > 0) {
            started.onExit()
                    .orTimeout(maxDurationMillis, TimeUnit.MILLISECONDS)
                    .exceptionally(overrun -> {
                        LOG.info("Script " + id + " exceeded its maximum duration of "
                                + maxDurationMillis + " ms and is being terminated");
                        started.destroyForcibly();
                        return null;
                    });
        }
    }

    /**
     * Destroy process.
     */
    public void destroyProcess() {
        final Process running = process;
        if (running != null) {
            running.destroyForcibly();
        }
    }

    /**
     * Describes the maximum duration in the unit the action declares it in, which is
     * seconds. It is held in milliseconds because that is the unit the timeout is
     * applied in, but reporting it that way alongside a minimum given in seconds invites
     * the two to be read as though they were different quantities.
     *
     * @return The maximum duration in seconds, or a statement that none is imposed.
     */
    private String describeMaximum() {
        if (maxDurationMillis <= 0) {
            return "unlimited";
        }
        return (maxDurationMillis / 1000) + " seconds";
    }

    private static long toTimeout(Integer timeoutSeconds) {
        if (timeoutSeconds == null || timeoutSeconds <= 0) {
            return NO_TIMEOUT;
        }
        return timeoutSeconds * 1000L;
    }

    private static int toMinDuration(Integer minDurationSeconds) {
        if (minDurationSeconds == null || minDurationSeconds < 0) {
            return 0;
        }
        return minDurationSeconds;
    }

    /**
     * @return The file to which the script output is redirected. A file is used in
     * preference to a stream because the process writes to it directly, so that no
     * output need be transferred or closed by this process.
     */
    private static File initLogFile(String fileName) {
        String path = AppStorage.getAppUserdataDir() + File.separator + LOG_PATH;
        Path logDir = FileUtils.createDirectoriesIfNotExist(Paths.get(path));
        return Paths.get(logDir.toString(), fileName).toFile();
    }

    private static String generateLogFilename() {
        return String.format("%s.log", DATE_FORMATTER.format(Instant.now()));
    }

}
