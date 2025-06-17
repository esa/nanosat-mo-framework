/* ----------------------------------------------------------------------------
 * Copyright (C) 2025      European Space Agency
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

import java.util.Date;

/**
 *
 * @author Cesar.Coelho
 */
public class OrbitParameters {

    private final double longitude;
    private final double latitude;
    private final double a;
    private final Date time;
    private final Vector velocity;

    /**
     * Constructor.
     *
     * @param latitude The latitude of the orbit.
     * @param longitude The longitude of the orbit.
     * @param a Semi-major axis in Km.
     * @param velocity The velocity.
     * @param time The time.
     */
    public OrbitParameters(double latitude, double longitude, double a, Vector velocity, Date time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.a = a;
        this.velocity = velocity;
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getA() {
        return a;
    }

    public double getAltitude() {
        return a - Orbit.EARTH_RADIUS;
    }

    public Date getTime() {
        return time;
    }

    public Vector getVelocity() {
        return velocity;
    }
}
