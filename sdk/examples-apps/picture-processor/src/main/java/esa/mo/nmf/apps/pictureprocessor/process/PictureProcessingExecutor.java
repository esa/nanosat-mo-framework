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
package esa.mo.nmf.apps.pictureprocessor.process;

import static esa.mo.nmf.apps.pictureprocessor.utils.FileUtils.createDirectoriesIfNotExist;
import static esa.mo.nmf.apps.pictureprocessor.utils.FileUtils.stripFileNameExtension;
import esa.mo.nmf.AppStorage;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Picture Processing Executor.
 */
public class PictureProcessingExecutor {

    private static final Logger LOG = Logger.getLogger(PictureProcessingExecutor.class.getName());
    private static final String ENV_PROCESS_DURATION = "ENV_PROCESS_DURATION";

    /**
     * Disables the buffering Python applies to its output when that output is not a
     * terminal.
     */
    private static final String ENV_PYTHON_UNBUFFERED = "PYTHONUNBUFFERED";
    private static final String LOG_PATH = "logs";

    /**
     * Value of {@code maxProcessDurationSeconds} denoting that no maximum duration
     * is imposed on the process.
     */
    private static final long NO_TIMEOUT = -1L;

    private final long minDurationSeconds;
    private final ProcessEventListener processEventListener;
    private final Long maxDurationMillis;
    private final Long processRequestId;

    /**
     * The process currently being executed, retained so that it may be terminated
     * externally. Written by the thread that starts the process and read by the
     * thread requesting termination, and therefore declared volatile.
     */
    private volatile Process process;

    /**
     * Creates a new {@code PictureProcessingExecutor}.
     *
     * @param processEventListener the process event listener
     * @param processRequestId the process request id
     * @param minProcessDurationSeconds the min process duration seconds
     * @param maxProcessDurationSeconds the max process duration seconds
     */
    public PictureProcessingExecutor(ProcessEventListener processEventListener, 
            Long processRequestId, Integer minProcessDurationSeconds, Integer maxProcessDurationSeconds) {
        this.maxDurationMillis = toTimeout(maxProcessDurationSeconds);
        this.minDurationSeconds = toMinDuration(minProcessDurationSeconds);
        this.processEventListener = processEventListener;
        this.processRequestId = processRequestId;
    }

    /**
     * Process picture.
     *
     * @param picture the picture
     * @return the process picture
     */
    public boolean processPicture(Path picture) {
        File logFile = initLogFile(picture.getFileName());

        // The output of the process is not reproduced here, so the location of the file
        // holding it is reported: an exit value on its own does not say why a process
        // failed, and the reason is in that file.
        LOG.info("Process " + processRequestId + " is starting. It will last at least "
                + minDurationSeconds + " seconds and at most " + describeMaximum() + "."
                + " Output: " + logFile.getAbsolutePath());

        ProcessBuilder builder = new ProcessBuilder("python", "imageEditor.py",
                picture.toAbsolutePath().toString());

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
        // here it is a file. A process terminated for exceeding its maximum duration is
        // killed outright, so anything still held in that buffer is lost, and the log is
        // empty in precisely the case where it is most needed. Unbuffered output is
        // written as it is produced.
        builder.environment().put(ENV_PYTHON_UNBUFFERED, "1");

        final LoggingExecuteResultHandler handler
                = new LoggingExecuteResultHandler(processEventListener, processRequestId);
        try {
            process = builder.start();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Picture could not be processed", e);
            return false;
        }

        watch(process, handler);
        return true;
    }

    /**
     * Registers completion handling for the process, and enforces the maximum
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
                        LOG.info("Process " + processRequestId + " exceeded its maximum"
                                + " duration of " + maxDurationMillis
                                + " ms and is being terminated");
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
     * @return The file to which the process output is redirected. A file is used in
     * preference to a stream because the process writes to it directly, so that no
     * output need be transferred or closed by this process.
     */
    private static File initLogFile(Path fileName) {
        String path = AppStorage.getAppUserdataDir() + File.separator + LOG_PATH;
        return createDirectoriesIfNotExist(Paths.get(path))
                .resolve(logFileName(stripFileNameExtension(fileName))).toFile();
    }

    private static Path logFileName(Path processInputFile) {
        String name = "picture-processor-" + processInputFile.toString() + ".log";
        return processInputFile.resolveSibling(name);
    }

}
