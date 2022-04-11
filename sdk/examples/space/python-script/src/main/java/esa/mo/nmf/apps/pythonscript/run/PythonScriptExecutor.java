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
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

public class PythonScriptExecutor {

    private static final Logger LOG = Logger.getLogger(PythonScriptExecutor.class.getName());
    private static final String ENV_PROCESS_DURATION = "ENV_PROCESS_DURATION";
    private static final String LOG_PATH = "output_logs";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
            .ofPattern("yyyyMMdd_HHmmss_SSSS").withZone(ZoneId.systemDefault());

    private final DefaultExecutor executor = new DefaultExecutor();
    private final long minDurationSeconds;
    private final long maxDurationMillis;
    private final MCAdapter mcAdapter;
    private final Long id;

    public PythonScriptExecutor(MCAdapter mcAdapter, Long id,
            Integer minDurationSeconds, Integer maxDurationSeconds) {
        this.minDurationSeconds = toMinDuration(minDurationSeconds);
        this.maxDurationMillis = toWatchdogTimeout(maxDurationSeconds);
        this.mcAdapter = mcAdapter;
        this.id = id;
        this.executor.setWatchdog(new ExecuteWatchdog(maxDurationMillis));
    }

    public boolean runPythonScript(String argument0) {
        LOG.info("Process " + id + " is starting:"
                + "\n  >> Min duration: " + minDurationSeconds + " seconds"
                + "\n  >> Max duration: " + maxDurationMillis + " ms");

        OutputStream outputStream = initLogStream(generateLogFilename());
        if (outputStream == null) {
            return false;
        }

        CommandLine commandLine = new CommandLine("python3")
                .addArgument("scripts/myScript.py")
                .addArgument(argument0);

        executor.setStreamHandler(new PumpStreamHandler(outputStream));

        Map<String, String> environment = new HashMap<>();
        environment.put(ENV_PROCESS_DURATION, String.valueOf(minDurationSeconds));

        try {
            executor.execute(commandLine, environment,
                    new LoggingExecuteResultHandler(mcAdapter, id, outputStream)
            );
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "The script could not be executed", e);
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

    private static OutputStream initLogStream(String fileName) {
        String path = AppStorage.getAppUserdataDir() + File.separator + LOG_PATH;
        Path logDir = FileUtils.createDirectoriesIfNotExist(Paths.get(path));
        Path logFileName = Paths.get(logDir.toString(), fileName);
        return FileUtils.newOutpuStreamSafe(logFileName);
    }

    private static String generateLogFilename() {
        return String.format("%s.log", DATE_FORMATTER.format(Instant.now()));
    }

}
