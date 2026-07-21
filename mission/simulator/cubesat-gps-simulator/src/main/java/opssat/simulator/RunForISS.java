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
package opssat.simulator;

import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Cesar Coelho
 */
public class RunForISS {

    private static final String EPOCH_DEFAULT = "2025:06:17 11:16:37 UTC";

    private final Orbit orbit = new Orbit(
            6787,
            51.6391 * (Math.PI / 180),
            (305.6704) * (Math.PI / 180),
            (257.2132) * (Math.PI / 180),
            0.0001085,
            0,
            EPOCH_DEFAULT);

    public final InstrumentsSimulator app = new InstrumentsSimulator(orbit);

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        RunForISS demo = new RunForISS();
    }

    public RunForISS() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GPS gps = app.getGPS();
                OrbitParameters position = gps.getPositionNow();
                System.out.printf("\nLatitude: %f\nLongitude: %f\nAltitude: %f\nTime: %s\n",
                        position.getLatitude(), position.getLongitude(),
                        position.getAltitude(), position.getTime().toString());
            }
        }, 0, 3000);

    }

}
