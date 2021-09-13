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
package esa.mo.nmf.nmfpackage;

import esa.mo.sm.impl.util.ShellCommander;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code LinuxUsersGroups} class allows the management of Linux users and
 * groups in a filesystem.
 *
 * @author Cesar Coelho
 */
public class LinuxUsersGroups {

    private static final String NOT_FOUND = "command not found";

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
    public static void useradd(String username, String password,
            boolean withGroup, String extraGroups) throws IOException {
        ShellCommander shell = new ShellCommander();

        // First, we need to check if the "useradd" exist
        String cmd1 = "useradd -h";
        String out1 = shell.runCommandAndGetOutputMessageAndError(cmd1);

        if (out1.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd1 + "\n" + out1;
            throw new IOException(msg);
        }

        // Second, we need to check if we have permissions to run the commands
        StringBuilder useradd = new StringBuilder();
        useradd.append("sudo ");
        useradd.append("useradd ").append(username)
                .append(" --create-home")
                .append(" --shell ").append(DEFAULT_SHELL)
                .append(withGroup ? " --user-group" : "")
                .append(" --groups ").append(extraGroups);
        //String cmd = "useradd $user_nmf_admin -m -s /bin/bash --user-group";
        //String cmd = "useradd $user_nmf_admin --create-home --shell /bin/bash --user-group";

        String cmd3 = useradd.toString();
        String out3 = shell.runCommandAndGetOutputMessageAndError(cmd3);

        if (out3.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd3 + "\n" + out3;
            throw new IOException(msg);
        }

        // Does the user account have a respective password?
        if (password != null) {
            LinuxUsersGroups.chpasswd(username, password);
        }

        // Change permissions on the home directories
        LinuxUsersGroups.printCommandAndOutput(cmd3, out3);
    }

    /**
     * Adds a new Linux group.
     *
     * @param groupName The name of the group to be created.
     * @param isSystemGroup Creates a system group.
     * @throws IOException if the group could not be created.
     */
    public static void addgroup(String groupName, boolean isSystemGroup) throws IOException {
        ShellCommander shell = new ShellCommander();

        // First, we need to check if the "useradd" exist
        String cmd1 = "sudo addgroup -h";
        String out1 = shell.runCommandAndGetOutputMessageAndError(cmd1);

        if (out1.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd1 + "\n" + out1;
            throw new IOException(msg);
        }

        StringBuilder addgroup = new StringBuilder();
        addgroup.append("sudo ");
        addgroup.append("addgroup ")
                .append("-S ")
                .append(groupName);

        String cmd2 = addgroup.toString();
        String out2 = shell.runCommandAndGetOutputMessageAndError(cmd2);

        if (out2.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd2 + "\n" + out2;
            throw new IOException(msg);
        }

        // Print the command output:
        LinuxUsersGroups.printCommandAndOutput(cmd2, out2);
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
        ShellCommander shell = new ShellCommander();

        // First, we need to check if the "useradd" exist
        String cmd1 = "sudo adduser -h";
        String out1 = shell.runCommandAndGetOutputMessageAndError(cmd1);

        if (out1.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd1 + "\n" + out1;
            throw new IOException(msg);
        }

        // Different Linux Systems have different syntaxes for the same command
        // So we need to check if we are using the adduser from "BusyBox" or not
        boolean isBusyBox = out1.contains("BusyBox");

        // Second, we need to check if we have permissions to run the commands
        StringBuilder useradd = new StringBuilder();
        useradd.append("sudo ");

        if (isBusyBox) {
            if (withGroup) {
                LinuxUsersGroups.addgroup(username, true);
            }
            useradd.append("adduser ")
                    .append("-s ").append(DEFAULT_SHELL)
                    .append(withGroup ? " -G " : "")
                    .append(withGroup ? username : "")
                    .append(" -S ")
                    .append(username);
        } else {
            useradd.append("adduser --system ")
                    .append("--shell ").append(DEFAULT_SHELL)
                    .append(withGroup ? " --group " : " ")
                    .append(username);
        }
        //String cmd = "useradd $user_nmf_admin -m -s /bin/bash --user-group";
        //String cmd = "useradd $user_nmf_admin --create-home --shell /bin/bash --user-group";

        String cmd3 = useradd.toString();
        String out3 = shell.runCommandAndGetOutputMessageAndError(cmd3);

        if (out3.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd3 + "\n" + out3;
            throw new IOException(msg);
        }

        // Does the user account have a respective password?
        if (password != null) {
            LinuxUsersGroups.chpasswd(username, password);
        }

        // Change permissions on the home directories
        LinuxUsersGroups.printCommandAndOutput(cmd3, out3);
    }

    public static void addUserToGroup(String username, String extraGroup) throws IOException {
        ShellCommander shell = new ShellCommander();

        // First, we need to check if the "useradd" exist
        String cmd1 = "sudo adduser -h";
        String out1 = shell.runCommandAndGetOutputMessageAndError(cmd1);

        if (out1.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd1 + "\n" + out1;
            throw new IOException(msg);
        }

        StringBuilder useradd = new StringBuilder();
        useradd.append("sudo ");
        useradd.append("adduser ")
                .append(username)
                .append(" ")
                .append(extraGroup);

        String cmd3 = useradd.toString();
        String out3 = shell.runCommandAndGetOutputMessageAndError(cmd3);

        if (out3.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd3 + "\n" + out3;
            throw new IOException(msg);
        }
    }

    public static void chpasswd(String username, String password) throws IOException {
        ShellCommander shell = new ShellCommander();
        String cmd2 = "chpasswd -h";
        String out2 = shell.runCommandAndGetOutputMessageAndError(cmd2);

        if (out2.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd2 + "\n" + out2;
            throw new IOException(msg);
        }

        StringBuilder chpasswd = new StringBuilder();
        chpasswd.append("echo ")
                .append(username).append(":").append(password)
                .append(" | ")
                .append("chpasswd");
        // echo $user_nmf_admin:$user_nmf_admin_password | chpasswd

        String cmd4 = chpasswd.toString();
        String out4 = shell.runCommandAndGetOutputMessageAndError(cmd4);

        if (out4.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd4 + "\n" + out4;
            throw new IOException(msg);
        }
    }

    /**
     * Deletes an existing Linux user account.
     *
     * @param username The username of the user account.
     * @param removeHome Sets if the user's home directory will be removed.
     * @throws IOException if the user could not be deleted.
     */
    public static void userdel(String username, boolean removeHome) throws IOException {
        StringBuilder cmd = new StringBuilder();
        cmd.append("sudo ");
        cmd.append("userdel --force ");
        cmd.append(removeHome ? "--remove " : "");
        cmd.append(username);

        ShellCommander shell = new ShellCommander();
        String out = shell.runCommandAndGetOutputMessageAndError(cmd.toString());

        if (out.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        if (out.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        LinuxUsersGroups.printCommandAndOutput(cmd.toString(), out);
    }

    /**
     * Deletes an existing Linux user account.
     *
     * @param username The username of the user account.
     * @param removeHome Sets if the user's home directory will be removed.
     * @throws IOException if the user could not be deleted.
     */
    public static void deluser(String username, boolean removeHome) throws IOException {
        StringBuilder cmd = new StringBuilder();
        cmd.append("sudo ");
        cmd.append("deluser ");
        cmd.append(removeHome ? "--remove-home " : "");
        cmd.append(username);

        ShellCommander shell = new ShellCommander();
        String out = shell.runCommandAndGetOutputMessageAndError(cmd.toString());

        if (out.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        if (out.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        LinuxUsersGroups.printCommandAndOutput(cmd.toString(), out);
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
        StringBuilder cmd = new StringBuilder();
        cmd.append(sudo ? "sudo " : "");
        cmd.append("chmod ");
        cmd.append(recursive ? "--recursive " : "");
        cmd.append(mode);
        cmd.append(" ");
        cmd.append(path);

        ShellCommander shell = new ShellCommander();
        String out = shell.runCommandAndGetOutputMessageAndError(cmd.toString());

        if (out.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        if (out.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        LinuxUsersGroups.printCommandAndOutput(cmd.toString(), out);
    }

    public static void chgrp(boolean recursive, String newGroup, String path) throws IOException {
        StringBuilder cmd = new StringBuilder();
        cmd.append("sudo ");
        cmd.append("chgrp ");
        cmd.append(recursive ? "--recursive " : "");
        cmd.append(newGroup);
        cmd.append(" ");
        cmd.append(path);

        ShellCommander shell = new ShellCommander();
        String out = shell.runCommandAndGetOutputMessageAndError(cmd.toString());

        if (out.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        if (out.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        LinuxUsersGroups.printCommandAndOutput(cmd.toString(), out);
    }

    public static String findHomeDir(String username) throws IOException {
        StringBuilder cmd = new StringBuilder();
        cmd.append("cat /etc/passwd | grep '");
        cmd.append(username);
        cmd.append(":' | cut -d ':' -f6");

        ShellCommander shell = new ShellCommander();
        String out = shell.runCommandAndGetOutputMessageAndError(cmd.toString());

        if (out.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        if (out.contains(PERMISSION_DENIED)) {
            String msg = "Permission denied! For command:\n >> " + cmd + "\n" + out;
            throw new IOException(msg);
        }

        LinuxUsersGroups.printCommandAndOutput(cmd.toString(), out);
        return out;
    }

    public static void printCommandAndOutput(String cmd, String out) {
        Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.INFO,
                "Executed command: " + cmd + "\n" + out);
    }

}
