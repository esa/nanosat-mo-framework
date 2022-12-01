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
package esa.mo.nmf.nmfpackage.utils;

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

    private static final String PERMISSION_DENIED = "Permission denied";

    private static final String MSG_NOT_FOUND = "The command was not found.\n";

    private static final String DEFAULT_SHELL = "/bin/bash";

    /**
     * Adds a new Linux user account and sets its respective password. The
     * account can be created without a password by passing a null into the
     * password field argument.
     *
     * @param username The username of the user account.
     * @param password The Password of the user account. Can be null if no
     * password is to be defined.
     * @param withGroup Defines if a group with the same name must also be
     * created during the user account creation.
     * @param extraGroups The list of supplementary groups which the user will
     * also be a member of. Each group is separated from the next by a comma,
     * with no intervening whitespace.
     * @throws IOException if the user could not be created.
     */
    @Deprecated
    public static void useradd(String username, String password,
            boolean withGroup, String extraGroups) throws IOException {
        // Second, we need to check if we have permissions to run the commands
        //String cmd = "useradd $user_nmf_admin -m -s /bin/bash --user-group";
        //String cmd = "useradd $user_nmf_admin --create-home --shell /bin/bash --user-group";
        String group = withGroup ? "--user-group" : "";
        String[] cmd = {"sudo", "useradd", username, "--create-home",
            "--shell", DEFAULT_SHELL, group, "--groups", extraGroups};
        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);

        // Does the user account have a respective password?
        if (password != null) {
            LinuxUsersGroups.chpasswd(username, password);
        }

        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

    /**
     * Adds a new Linux group.
     *
     * @param groupName The name of the group to be created.
     * @param isSystemGroup Creates a system group.
     * @throws IOException if the group could not be created.
     */
    public static void addgroup(String groupName, boolean isSystemGroup) throws IOException {
        String[] cmd = {"sudo", "addgroup", "-S", groupName};
        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

    /**
     * Adds a new Linux user account and sets its respective password. The
     * account can be created without a password by passing a null into the
     * password field argument.
     *
     * @param username The username of the user account.
     * @param password The Password of the user account. Can be null if no
     * password is to be defined.
     * @param withGroup Defines if a group with the same name must also be
     * created during the user account creation.
     * @throws IOException if the user could not be created.
     */
    public static void adduser(String username, String password,
            boolean withGroup) throws IOException {
        // First, we need to check if the "useradd" is from BusyBox or not
        // Different Linux Systems have different syntaxes for the same command
        // So we need to check if we are using the adduser from "BusyBox" or not
        String[] cmd1 = {"sudo", "adduser", "--help"};
        String out1 = runCommand(cmd1);
        boolean isBusyBox = out1.contains("BusyBox");

        // Second, we execute the adduser command
        String[] cmd2;

        if (isBusyBox) {
            if (withGroup) {
                LinuxUsersGroups.addgroup(username, true);

                cmd2 = new String[]{"sudo", "adduser", "-s", DEFAULT_SHELL,
                    "-G", username, "-S", username};
            } else {
                cmd2 = new String[]{"sudo", "adduser", "-s", DEFAULT_SHELL,
                    "-S", username};
            }
        } else {
            // It is NOT BusyBox:
            cmd2 = new String[]{"sudo", "adduser", "--system",
                "--shell", DEFAULT_SHELL, withGroup ? "--group" : "",
                username};
        }

        String out2 = runCommand(cmd2);
        checkIfPermissionDenied(cmd2, out2);

        // Does the user account have a respective password?
        if (password != null) {
            LinuxUsersGroups.chpasswd(username, password);
        }

        LinuxUsersGroups.printCommandAndOutput(cmd2, out2);
    }

    public static void addUserToGroup(String username, String extraGroup) throws IOException {
        // Note: usermod does not exist in some trimmed Linux versions
        // Therefore we will be using the adduser equivalent functionality
        String[] cmd = {"sudo", "adduser", username, extraGroup};
        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

    public static void chpasswd(String username, String password) throws IOException {
        String[] cmd1 = {"chpasswd", "-h"};
        String out1 = runCommand(cmd1);

        // echo $user_nmf_admin:$user_nmf_admin_password | chpasswd
        String userWithPass = username + ":" + password;
        String[] cmd2 = {"echo", userWithPass, "|", "chpasswd"};
        String out2 = runCommand(cmd2);
        checkIfPermissionDenied(cmd2, out2);
    }

    /**
     * Deletes an existing Linux user account.
     *
     * @param username The username of the user account.
     * @param removeHome Sets if the user's home directory will be removed.
     * @throws IOException if the user could not be deleted.
     */
    public static void userdel(String username, boolean removeHome) throws IOException {
        String strRemove = removeHome ? "--remove" : "";
        String[] cmd = {"sudo", "userdel", "--force", strRemove, username};
        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

    /**
     * Deletes an existing Linux user account.
     *
     * @param username The username of the user account.
     * @param removeHome Sets if the user's home directory will be removed.
     * @throws IOException if the user could not be deleted.
     */
    public static void deluser(String username, boolean removeHome) throws IOException {
        String strRemove = removeHome ? "--remove-home" : "";
        String[] cmd = {"sudo", "deluser", strRemove, username};
        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

    /**
     * Changes the access permissions of file system objects (files and
     * directories).
     *
     * @param sudo Sets if the command needs sudo.
     * @param recursive Sets if the command is recursive.
     * @param mode The mode to be set.
     * @param path The path of the file or directory.
     * @throws IOException if the permissions could not be changed.
     */
    public static void chmod(boolean sudo, boolean recursive, String mode, String path) throws IOException {
        String strRecursive = recursive ? "--recursive" : "";
        //String[] cmd = {sudo ? "sudo" : "", "chmod", strRecursive, mode, path};
        String[] cmd = sudo
                ? new String[]{"sudo", "chmod", strRecursive, mode, path}
                : new String[]{"chmod", strRecursive, mode, path};

        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

    public static void chgrp(boolean recursive, String newGroup, String path) throws IOException {
        String[] cmd = {"sudo", "chgrp", recursive ? "--recursive" : "", newGroup, path};
        String out = runCommand(cmd);
        checkIfPermissionDenied(cmd, out);
        LinuxUsersGroups.printCommandAndOutput(cmd, out);
    }

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

            String out = extractString(p.getInputStream());
            out += extractString(p.getErrorStream());
            int exitValue = p.exitValue();

            if (exitValue == 127) { // Command not found!
                throw new IOException(MSG_NOT_FOUND
                        + "\n >> " + String.join(" ", cmd) + "\n" + out);
            }

            if (exitValue != 0) { // Error!
                String error = extractString(p.getErrorStream());
                throw new IOException("There was an error with code " + exitValue
                        + ". For command:\n >> " + String.join(" ", cmd) + "\n"
                        + out + "\nError:\n" + error + "\n----");
            }

            return out;
        } catch (InterruptedException ex) {
            throw new IOException(ex);
        }
    }

    private static String extractString(InputStream in) throws IOException {
        InputStreamReader isr = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            buffer.append(line);
            buffer.append("\n");
        }

        return buffer.toString();
    }

}
