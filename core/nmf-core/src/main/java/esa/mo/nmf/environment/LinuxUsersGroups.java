/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft â€“ v2.4
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
package esa.mo.nmf.environment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code LinuxUsersGroups} class allows the management of Linux users and
 * groups in a filesystem.
 *
 * @author Cesar Coelho
 */
public class LinuxUsersGroups {
    private LinuxUsersGroups() {
    }


    private static final String PERMISSION_DENIED = "Permission denied";

    private static final String MSG_NOT_FOUND = "The command was not found.\n";

    /**
     * The root-owned helper, installed by setup_linux_userspace.sh, that runs
     * the only operations the Supervisor may run as root: it accepts nothing
     * but the account of an App and that App's own directories.
     */
    public static final String APP_ACCOUNT_HELPER = "/usr/local/sbin/nmf-app-account";

    /**
     * Adds the Linux system account of an App, with a group of its own. The
     * account has no password, so nobody can log in as it: the Supervisor
     * switches to it with su.
     *
     * @param username The username of the App, which must be app-&lt;name&gt;.
     * @throws IOException if the user could not be created.
     */
    public static void addAppUser(String username) throws IOException {
        runAppAccountHelper("create", username);
    }

    /**
     * Deletes the Linux account of an App, together with its home directory.
     *
     * @param username The username of the App, which must be app-&lt;name&gt;.
     * @throws IOException if the user could not be deleted.
     */
    public static void deleteAppUser(String username) throws IOException {
        runAppAccountHelper("delete", username);
    }

    /**
     * Gives the files of one of an App's own directories (its installation or
     * its logs directory) to the App's group.
     *
     * @param username The username of the App, which must be app-&lt;name&gt;.
     * @param path The App's directory.
     * @throws IOException if the group could not be changed.
     */
    public static void giveToAppGroup(String username, String path) throws IOException {
        runAppAccountHelper("own", username, path);
    }

    private static void runAppAccountHelper(String... args) throws IOException {
        String[] cmd = new String[args.length + 3];
        cmd[0] = "sudo";
        cmd[1] = "-n";
        cmd[2] = APP_ACCOUNT_HELPER;
        System.arraycopy(args, 0, cmd, 3, args.length);
        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

    /**
     * Changes the access permissions of file system objects (files and
     * directories) that the Supervisor owns.
     *
     * @param recursive Sets if the command is recursive.
     * @param mode The mode to be set.
     * @param path The path of the file or directory.
     * @throws IOException if the permissions could not be changed.
     */
    public static void chmod(boolean recursive, String mode, String path) throws IOException {
        // Note: The flag has to be "-R" and not "--recursive", because of BusyBox
        String[] cmd = recursive
                ? new String[]{"chmod", "-R", mode, path}
                : new String[]{"chmod", mode, path};

        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

    /**
     * Returns the home directory of the given user.
     *
     * @param username the username
     * @return the find home dir
     * @throws IOException if the operation fails
     */
    public static String findHomeDir(String username) throws IOException {
        // Get the list of users and respective folders
        String[] cmd = {"cat", "/etc/passwd"};
        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        String[] lines = out.split("\\R");

        for (String line : lines) {
            String[] entries = line.split(":");

            if (entries.length >= 5) {
                if (entries[0].equals(username)) {
                    return line.split(":")[5];
                }
            }
        }

        throw new IOException("The HomeDir was not found for user: " + username);
    }

    /**
     * Logs a command and its output.
     *
     * @param cmd the cmd
     * @param out the out
     */
    public static void printCommandAndOutput(String[] cmd, String out) {
        Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.INFO,
                "Executed command: " + String.join(" ", cmd) + "\n" + out);
    }

    private static void checkIfPermissionDenied(String[] cmd, String out) throws IOException {
        if (out.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> "
                    + String.join(" ", cmd) + "\n" + out;
            throw new IOException(msg);
        }
    }

    private static String runCommand(String[] cmd) throws IOException {
        try {
            Process p = Runtime.getRuntime().exec(cmd, null, null);
            boolean terminated = p.waitFor(10, TimeUnit.SECONDS);

            if (!terminated) {
                Logger.getLogger(LinuxUsersGroups.class.getName()).log(
                        Level.SEVERE,
                        "Timeout reached: The process is stuck... "
                        + "therefore the process was killed!");

                p.destroyForcibly();
            }

            // Each stream is read once, to its end, and closed
            String stdout = extractString(p.getInputStream());
            String error = extractString(p.getErrorStream());
            String out = stdout + error;
            int exitValue = p.exitValue();

            if (exitValue == 127) { // Command not found!
                throw new IOException(MSG_NOT_FOUND
                        + "\n >> " + String.join(" ", cmd) + "\n" + out);
            }

            if (exitValue != 0) { // Error!
                throw new IOException("There was an error with code " + exitValue
                        + ". For command:\n >> " + String.join(" ", cmd) + "\n"
                        + stdout + "\nError:\n" + error + "\n----");
            }

            return out;
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
    }

    private static String extractString(InputStream in) throws IOException {
        StringBuilder buffer = new StringBuilder();
        // The stream of a process, which is read to its end and then closed
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
            String line;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
                buffer.append("\n");
            }
        }

        return buffer.toString();
    }

}
