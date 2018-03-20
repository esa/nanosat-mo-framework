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

/**
 * The FineADCS class includes the simulation of a Magnetometer on a selected
 * orbit.
 *
 */
public class FineADCS {

    private final Orbit orbit;
    private final Magnetometer magnetometer;

    /**
     * The FineADCS class constructor
     *
     * @param orbit
     */
    public FineADCS(Orbit orbit) {
        this.orbit = orbit;
        magnetometer = new Magnetometer(orbit);
    }

    /**
     * A getter for the Magnetometer class
     *
     * @return
     */
    public Magnetometer getMagnetometer() {
        return this.magnetometer;
    }

}
