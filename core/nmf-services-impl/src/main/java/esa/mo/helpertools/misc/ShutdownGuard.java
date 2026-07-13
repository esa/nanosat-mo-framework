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
package esa.mo.helpertools.misc;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A watchdog that guarantees a top-level NMF process terminates even if its
 * shutdown sequence deadlocks (for example, a transport teardown that never
 * returns).
 *
 * <p>
 * It is intended for processes that have no parent to force-kill them — the
 * Supervisor and the self-contained Monolithic provider. It must NOT be used by
 * an App: an App is managed by the Supervisor, which owns the escalation to a
 * forced kill (via the stopApp timeout), so an App is allowed to take as long
 * as it needs to shut down gracefully.
 *
 * <p>
 * The timeout defaults to 5000 ms and can be overridden with the
 * {@code nmf.shutdown.guard.ms} system property.
 */
public class ShutdownGuard {

    private static final Logger LOGGER = Logger.getLogger(ShutdownGuard.class.getName());
    private static final String TIMEOUT_PROPERTY = "nmf.shutdown.guard.ms";
    private static final long DEFAULT_TIMEOUT_MS = 5000;

    private ShutdownGuard() {
    }

    /**
     * Starts the watchdog thread. If the JVM has not terminated within the
     * timeout, a thread dump is logged and the process is forcibly exited.
     */
    public static void start() {
        final long timeoutMs = Long.getLong(TIMEOUT_PROPERTY, DEFAULT_TIMEOUT_MS);
        (new Thread("ShutdownGuardThread") {
            @Override
            public void run() {
                try {
                    sleep(timeoutMs);
                } catch (InterruptedException e) {
                    // The thread was interrupted by the system exit
                    return;
                }
                LOGGER.log(Level.WARNING,
                        "The process failed to exit gracefully within the predefined {0} ms. "
                        + "Performing a thread dump...", timeoutMs);
                LOGGER.log(Level.WARNING, threadDump(true, true));
                LOGGER.log(Level.WARNING, "Forcing exit with code -1");
                System.exit(-1);
            }
        }).start();
    }

    private static String threadDump(boolean lockedMonitors, boolean lockedSynchronizers) {
        StringBuilder threadDump = new StringBuilder(System.lineSeparator());
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        for (ThreadInfo threadInfo : threadMXBean.dumpAllThreads(lockedMonitors, lockedSynchronizers)) {
            threadDump.append(threadInfo.toString());
        }
        return threadDump.toString();
    }
}
