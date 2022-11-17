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

package esa.mo.helpertools.misc;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class to ensure the application terminates even if one of the handlers takes time
 */
public class AppShutdownGuard {

    private static final Logger LOGGER = Logger.getLogger(AppShutdownGuard.class.getName());

    private AppShutdownGuard() {
    }

    public static void start() {
        (new Thread("ShutdownGuardThread") {
            @Override
            public void run() {
                try {
                    sleep(Const.APP_SHUTDOWN_GUARD_MS);
                } catch (InterruptedException e) {
                    // The thread was interrupted by the system exit
                    return;
                }
                LOGGER.log(Level.WARNING,
                    "The application failed to exit gracefully within predefined {0} ms. Performing a thread dump...",
                    Const.APP_SHUTDOWN_GUARD_MS);
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
