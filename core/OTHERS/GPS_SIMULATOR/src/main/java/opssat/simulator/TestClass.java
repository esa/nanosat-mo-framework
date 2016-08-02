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
package opssat.simulator;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Cesar Coelho
 */
public class TestClass {

    public final InstrumentsSimulator app = new InstrumentsSimulator();

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     */
    public static void main(final String args[]) {
        TestClass demo = new TestClass();
    }

    public TestClass() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.printf("\nLatitude, Longitude: %f, %f\nAltitude: %f\nTime: %s\n",
                        app.getGPSlatitude(), app.getGPSlongitude(), app.getGPSaltitude(), app.getGPStime().toString());

            }
        }, 0, 3000);

//        byte[] dfdsfdf = demo.app.getPicture();
    }

}
