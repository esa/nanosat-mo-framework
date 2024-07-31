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

import esa.mo.nmf.AppStorage;
import static esa.mo.nmf.apps.pictureprocessor.utils.FileUtils.createDirectoriesIfNotExist;
import static esa.mo.nmf.apps.pictureprocessor.utils.FileUtils.newOutpuStreamSafe;
import static esa.mo.nmf.apps.pictureprocessor.utils.FileUtils.stripFileNameExtension;
import java.io.File;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class PictureProcessingExecutor {

    private static final Logger LOG = Logger.getLogger(PictureProcessingExecutor.class.getName());
    private static final String ENV_PROCESS_DURATION = "ENV_PROCESS_DURATION";
    private static final String LOG_PATH = "logs";

    private final long minDurationSeconds;
    private final DefaultExecutor executor;
    private final ProcessEventListener processEventListener;
    private final Long maxDurationMillis;
    private final Long processRequestId;

    public PictureProcessingExecutor(ProcessEventListener processEventListener, 
            Long processRequestId, Integer minProcessDurationSeconds, Integer maxProcessDurationSeconds) {
        this.maxDurationMillis = toWatchdogTimeout(maxProcessDurationSeconds);
        this.minDurationSeconds = toMinDuration(minProcessDurationSeconds);
        this.executor = new DefaultExecutor();
        this.processEventListener = processEventListener;
        this.processRequestId = processRequestId;
        this.executor.setWatchdog(new ExecuteWatchdog(maxDurationMillis));
    }

    public boolean processPicture(Path picture) {
        LOG.info("Process " + processRequestId + " is starting. It will last at least " 
                + minDurationSeconds + "s and at most " + maxDurationMillis + "ms");

        OutputStream outputStream = initLogStream(picture.getFileName());
        if (outputStream == null) {
            return false;
        }

        CommandLine commandLine = new CommandLine("python").addArgument("imageEditor.py").addArgument(picture
            .toAbsolutePath().toString());

        executor.setStreamHandler(new PumpStreamHandler(outputStream));

        Map<String, String> environment = new HashMap<>();
        environment.put(ENV_PROCESS_DURATION, String.valueOf(minDurationSeconds));

        try {
            executor.execute(commandLine, environment, 
                    new LoggingExecuteResultHandler(processEventListener, processRequestId, outputStream)
            );
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Picture could not be processed", e);
            return false;
        }

        return true;
    }

    public void destroyProcess() {
        executor.getWatchdog().destroyProcess();
    }

    private static long toWatchdogTimeout(Integer timeoutSeconds) {
        if (timeoutSeconds == null || timeoutSeconds <= 0) {
            return ExecuteWatchdog.INFINITE_TIMEOUT;
        }
        return timeoutSeconds * 1000L;
    }

    private static int toMinDuration(Integer minDurationMillis) {
        if (minDurationMillis == null || minDurationMillis < 0) {
            return 0;
        }
        return minDurationMillis;
    }

    private static OutputStream initLogStream(Path fileName) {
        String path = AppStorage.getAppUserdataDir() + File.separator + LOG_PATH;
        Path logFileName = createDirectoriesIfNotExist(Paths.get(path))
                .resolve(logFileName(stripFileNameExtension(fileName)));
        return newOutpuStreamSafe(logFileName);
    }

    private static Path logFileName(Path processInputFile) {
        String name = "picture-processor-" + processInputFile.toString() + ".log";
        return processInputFile.resolveSibling(name);
    }

}
