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

    /**
     * Creates a new Linux user account and sets its respective password. The
     * account can be created without a password by passing null into the
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
    public static void createUser(String username, String password,
            boolean withGroup, String extraGroups) throws IOException {
        ShellCommander shell = new ShellCommander();
        final String defaultShell = "/bin/bash";

        //--------------------------------------------------------------------
        // First, we need to check if the "useradd" exist
        String cmd1 = "useradd -h";
        String out1 = shell.runCommandAndGetOutputMessageAndError(cmd1);

        if (out1.contains(NOT_FOUND)) {
            String msg = MSG_NOT_FOUND + "\n >> " + cmd1 + "\n" + out1;
            throw new IOException(msg);
        }

        //--------------------------------------------------------------------
        // Second, we need to check if we have permissions to run the commands
        StringBuilder useradd = new StringBuilder();
        useradd.append("sudo ");
        useradd.append("useradd ").append(username)
                .append(" --create-home")
                .append(" --shell ").append(defaultShell)
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

        // Change permissions on the home directories
        Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.INFO,
                " Running command: " + cmd3 + "\n" + out3);
    }

    /**
     * Deletes an existing Linux user account.
     *
     * @param username The username of the user account.
     * @param removeHome Sets if the user's home directory will be removed.
     * @throws IOException if the user could not be deleted.
     */
    public static void deleteUser(String username, boolean removeHome) throws IOException {
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

        Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.INFO,
                " Running command: " + cmd + "\n" + out);
    }

    /**
     * Changes the access permissions of file system objects (files and
     * directories).
     *
     * @param path The path of the file or directory.
     * @param mode The mode to be set.
     * @param recursive Sets if the command is recursive.
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

        Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.INFO,
                " Running command: " + cmd + "\n" + out);
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

        Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.INFO,
                " Running command: " + cmd + "\n" + out);
    }

    static String findHomeDir(String username) throws IOException {
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

        Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.INFO,
                " Running command: " + cmd + "\n" + out);
        
        return out;
    }
    
}
