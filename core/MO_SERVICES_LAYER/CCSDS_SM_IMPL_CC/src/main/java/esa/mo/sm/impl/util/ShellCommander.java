/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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
    private final static int DEATH_TIMEOUT = 3000;
    
    public ShellCommander(){
    }

    public Process runCommand(String cmd) {
        return this.runCommand(cmd, null);
    }
    
    public Process runCommand(String cmd, File dirPath) {

        try {
            Process proc;

            if (osValidator.isUnix()) {
                proc = Runtime.getRuntime().exec(new String[]{"bash", "-c", cmd}, null, dirPath);
            } else if (osValidator.isWindows()) {
                proc = Runtime.getRuntime().exec(new String[]{"cmd", "/c", cmd}, null, dirPath);
            } else {
                Logger.getLogger(ShellCommander.class.getName()).log(Level.SEVERE, "Unknown OS");
                return null;
            }

            StreamWrapper error = new StreamWrapper(proc.getErrorStream(), "ERROR");
            StreamWrapper output = new StreamWrapper(proc.getInputStream(), "OUTPUT");
            int exitVal = 0;

            error.start();
            output.start();

            System.out.println("Output:\n" + output.getMessage() + "\nError:\n" + error.getMessage());

            error.join(DEATH_TIMEOUT);
            output.join(DEATH_TIMEOUT);
            proc.destroy();
//            exitVal = proc.waitFor();

            System.out.println("Output:\n" + output.getMessage() + "\nError:\n" + error.getMessage());
            
            return proc;
        } catch (IOException ex) {
            Logger.getLogger(ShellCommander.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ShellCommander.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    private class StreamWrapper extends Thread {

        private InputStream is = null;
        private String type = null;
        private String message = null;

        public String getMessage() {
            return message;
        }

        StreamWrapper(InputStream is, String type) {
            this.is = is;
            this.type = type;
        }

        private String getOutput() {
            return message;
        }

        @Override
        public void run() {
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
                Logger.getLogger(ShellCommander.class.getName()).log(Level.INFO, "Error: " + ioe);
//                ioe.printStackTrace();
            }
        }
    }

}
