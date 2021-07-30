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

package esa.mo.sm.impl.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cesar Coelho
 */
public class ShellCommander {

    private final OSValidator osValidator = new OSValidator();
    private final static int DEATH_TIMEOUT = 2000;
    
    public ShellCommander(){
    }

    public Process runCommand(String cmd) {
        return this.runCommand(cmd, null);
    }

    public String runCommandAndGetOutputMessage(String cmd) {
        try {
            Process proc = this.runCommand(cmd, null);
            StreamWrapper error = new StreamWrapper(proc.getErrorStream(), "ERROR");
            StreamWrapper output = new StreamWrapper(proc.getInputStream(), "OUTPUT");
            error.start();
            output.start();
            
            error.join(DEATH_TIMEOUT);
            output.join(DEATH_TIMEOUT);
            proc.destroyForcibly();
            
            return output.getMessage();
        } catch (InterruptedException ex) {
            Logger.getLogger(ShellCommander.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return "";
    }
   
    public String runCommandAndGetOutputMessageAndError(String cmd) {
        try {
            Process proc = this.runCommand(cmd, null);
            StreamWrapper error = new StreamWrapper(proc.getErrorStream(), "ERROR");
            StreamWrapper output = new StreamWrapper(proc.getInputStream(), "OUTPUT");
            error.start();
            output.start();
            
            error.join(DEATH_TIMEOUT);
            output.join(DEATH_TIMEOUT);
            proc.destroyForcibly();
            
            return output.getMessage() + error.getMessage();
        } catch (InterruptedException ex) {
            Logger.getLogger(ShellCommander.class.getName()).log(Level.SEVERE, 
                    "The command could not be executed!", ex);
        }
        
        return "";
    }
   
    public Process runCommand(String cmd, File dirPath) {
        try {
            Process proc;

            if (osValidator.isUnix()) {
                proc = Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd}, null, dirPath);
            } else if (osValidator.isWindows()) {
                proc = Runtime.getRuntime().exec(new String[]{"cmd", "/c", cmd}, null, dirPath);
            } else {
                Logger.getLogger(ShellCommander.class.getName()).log(Level.SEVERE, 
                        "The command could not executed due to an Unknown OS!");
                return null;
            }

            return proc;
        } catch (IOException ex) {
            Logger.getLogger(ShellCommander.class.getName()).log(Level.SEVERE, 
                    "The command could not be executed!", ex);
        }

        return null;
    }

    private class StreamWrapper extends Thread {

        private InputStream is = null;
        private String type = null;
        private String message = "<Nothing>";

        public String getMessage() {
            return message;
        }

        StreamWrapper(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        @Override
        public void run() {
            this.setName("ShellCommander_StreamWrapper");
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder buffer = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\n");
                }
                message = buffer.toString();
            } catch (IOException ioe) {
                Logger.getLogger(ShellCommander.class.getName()).log(Level.INFO, 
                        "Error: ", ioe);
            }
        }
    }

}
