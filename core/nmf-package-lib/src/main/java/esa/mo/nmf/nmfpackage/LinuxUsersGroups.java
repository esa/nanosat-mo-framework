/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code LinuxUsersGroups} class allows the management of Linux users and
 * groups in a filesystem.
 *
 * @author Cesar Coelho
 */
public class LinuxUsersGroups {

    public static boolean setGroup(File file, String groupName) {
        // Set the Group
        UserPrincipalLookupService service = FileSystems.getDefault().getUserPrincipalLookupService();

        try {
            GroupPrincipal group = service.lookupPrincipalByGroupName(groupName);
            Files.getFileAttributeView(file.toPath(),
                    PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).setGroup(group);
        } catch (IOException ex) {
            Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.SEVERE,
                    "The group " + groupName + " does not exist!");
            return false;
        }
        
        return true;

        /* Example: Retrieve Group
            File originalFile = new File("original.jpg"); // just as an example
            GroupPrincipal group = Files.readAttributes(originalFile.toPath(), 
        PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).group();
         */
        // We need also to create the user+group for this app
        // To be done...
    }

    public static void createUser(String username, String password, boolean withGroup) {
        // First, we need to check if the "useradd" and "chpasswd" commands exist
        
        // Second, we need to check if we have permissions to run the commands
        
        StringBuilder useradd = new StringBuilder();
        useradd.append("useradd ").append(username);
        useradd.append(" -m -s /bin/bash"); // -m CreatesHome dir || -s Sets the shell
        useradd.append(withGroup ? " --user-group" : "");
        //String cmd = "useradd $user_nmf_admin -m -s /bin/bash --user-group";

        StringBuilder chpasswd = new StringBuilder();
        chpasswd.append("echo ").append(username).append(":").append(password);
        chpasswd.append(" | ");
        chpasswd.append("chpasswd");
        // echo $user_nmf_admin:$user_nmf_admin_password | chpasswd

        String cmd = useradd.toString() + " ; " + chpasswd.toString();
        ShellCommander shell = new ShellCommander();
        String output = shell.runCommandAndGetOutputMessage(cmd);

        Logger.getLogger(LinuxUsersGroups.class.getName()).log(Level.INFO,
                "The output is:\n" + output);
    }

}
