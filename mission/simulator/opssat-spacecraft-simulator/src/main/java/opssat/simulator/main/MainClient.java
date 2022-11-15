/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.main;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import opssat.simulator.gui.GuiApp;

/**
 *
 * @author Cezar Suteu
 */
public class MainClient {

    private static GuiApp guiApp;

    public static void main(String[] args) {

        LookAndFeelInfo[] info = UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < info.length; i++) {
            System.out.println(info[i].toString());
        }

        if (args.length == 2) {
            guiApp = new GuiApp(args[0], Integer.parseInt(args[1]));
        } else {
            guiApp = new GuiApp("127.0.0.1", 11111);
        }
    }

}
