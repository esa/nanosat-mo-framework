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

import java.io.OutputStream;
import java.util.logging.Logger;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.ExecuteException;

public class LoggingExecuteResultHandler extends DefaultExecuteResultHandler {

    private static final Logger LOG = Logger.getLogger(LoggingExecuteResultHandler.class.getName());

    private final OutputStream processOutputStream;
    private final MCAdapter processEventListener;
    private final Long processRequestId;

    public LoggingExecuteResultHandler(MCAdapter processEventListener,
            Long processRequestId, OutputStream processOutputStream) {
        this.processOutputStream = processOutputStream;
        this.processEventListener = processEventListener;
        this.processRequestId = processRequestId;
    }

    @Override
    public void onProcessComplete(final int exitValue) {
        super.onProcessComplete(exitValue);
        LOG.info("Process execution completed. ExitValue: " + exitValue);
        handleProcessTermination(exitValue);
    }

    @Override
    public void onProcessFailed(final ExecuteException e) {
        super.onProcessFailed(e);
        LOG.info("Process execution failed: " + e);
        handleProcessTermination(e.getExitValue());
    }

    private void handleProcessTermination(int exitValue) {
        FileUtils.closeSafe(processOutputStream);
        processEventListener.onProcessCompleted(processRequestId, exitValue);
    }
}
