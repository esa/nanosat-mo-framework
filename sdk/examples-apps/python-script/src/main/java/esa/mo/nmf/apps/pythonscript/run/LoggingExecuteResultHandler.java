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

import java.util.logging.Logger;

/**
 * Logging Execute Result Handler.
 * <p>
 * Notified on termination of the process, and in turn notifies the listener that
 * requested its execution. This class previously extended
 * {@code DefaultExecuteResultHandler} of Apache Commons Exec, which reported
 * termination through two distinct methods: one for normal completion and one for
 * failure, the latter carrying an exception. A process terminated for exceeding its
 * maximum duration does not constitute a distinct form of termination, only a
 * different exit value, and a single method is therefore declared.
 */
public class LoggingExecuteResultHandler {

    private static final Logger LOG = Logger.getLogger(LoggingExecuteResultHandler.class.getName());

    private final MCAdapter processEventListener;
    private final Long processRequestId;

    /**
     * Creates a new {@code LoggingExecuteResultHandler}.
     *
     * @param processEventListener the process event listener
     * @param processRequestId the process request id
     * @param processOutputStream the process output stream
     */
    public LoggingExecuteResultHandler(MCAdapter processEventListener,
            Long processRequestId) {
        this.processEventListener = processEventListener;
        this.processRequestId = processRequestId;
    }

    /**
     * Invoked exactly once, on termination of the process for any reason.
     *
     * @param exitValue The exit value of the process. Non-zero if the process
     * failed, and also if it was terminated for exceeding its maximum duration.
     */
    public void onProcessEnded(final int exitValue) {
        LOG.info("Script execution terminated. ExitValue: " + exitValue);
        processEventListener.onProcessCompleted(processRequestId, exitValue);
    }
}
