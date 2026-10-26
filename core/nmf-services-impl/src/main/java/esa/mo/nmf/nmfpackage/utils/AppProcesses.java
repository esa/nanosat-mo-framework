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
package esa.mo.nmf.nmfpackage.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Finds the operating system processes that are running an App.
 *
 * The Apps Launcher only knows the processes that it started itself, so an App
 * started in any other way, for example by running its start script by hand,
 * is not running for it. The files of an App must not be removed while any
 * process still runs from them, wherever that process came from, so this class
 * asks the operating system instead.
 *
 * A process runs the App when it is owned by the App's own user (under the
 * linux-userspace isolation), or when its command line names the App's
 * directory: the start script gives java the full path of the App's jar, and
 * the commands of the Apps Launcher name the directory in every isolation
 * mode. Its working directory is not enough, as a shell that is only in the
 * App's directory would then keep the App from being removed.
 */
public class AppProcesses {

    private AppProcesses() {
    }

    /**
     * Returns the live processes that run the App, leaving out this process.
     *
     * @param appDir The installation directory of the App.
     * @param username The user that the App runs as, or null if the App does
     * not have a user of its own.
     * @return The processes found, empty if there are none.
     * @throws IOException if the directory of the App cannot be resolved.
     */
    public static List<ProcessHandle> find(final File appDir, final String username)
            throws IOException {
        final Path dir = appDir.getCanonicalFile().toPath();
        // The directory must end where the path does, so that the App "foo" is
        // not taken for the App "foobar"
        final Pattern named = Pattern.compile(Pattern.quote(dir.toString())
                + "(?=$|[/\\\\'\"\\s:;])");
        final long self = ProcessHandle.current().pid();

        return ProcessHandle.allProcesses()
                .filter(p -> p.pid() != self)
                .filter(p -> runsTheApp(p, named, username))
                .collect(Collectors.toList());
    }

    /**
     * Gives the processes time to exit, as an App that was just stopped may
     * still be closing, and returns those that are still running the App.
     *
     * @param appDir The installation directory of the App.
     * @param username The user that the App runs as, or null if the App does
     * not have a user of its own.
     * @param timeoutMs How long to wait for the processes to exit.
     * @return The processes still running the App, empty if there are none.
     * @throws IOException if the directory of the App cannot be resolved.
     */
    public static List<ProcessHandle> awaitExit(final File appDir, final String username,
            final long timeoutMs) throws IOException {
        final long deadline = System.currentTimeMillis() + timeoutMs;

        for (ProcessHandle process : find(appDir, username)) {
            long left = deadline - System.currentTimeMillis();
            if (left <= 0) {
                break;
            }
            try {
                process.onExit().get(left, TimeUnit.MILLISECONDS);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                // Still running when the time ran out: found again below
            }
        }

        return find(appDir, username);
    }

    private static boolean runsTheApp(final ProcessHandle process,
            final Pattern named, final String username) {
        if (!process.isAlive()) {
            return false;
        }

        ProcessHandle.Info info = process.info();

        if (username != null && username.equals(info.user().orElse(null))) {
            return true;
        }

        return info.commandLine().map(line -> named.matcher(line).find()).orElse(false);
    }
}
