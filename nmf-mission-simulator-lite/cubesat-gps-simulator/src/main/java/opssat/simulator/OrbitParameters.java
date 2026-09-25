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

    private final Vector positionEarthFixed;

    private final Vector velocityEarthFixed;
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
        this(latitude, longitude, a, velocity, time, null, null);
    }

    /**
     * Constructor that also carries the position and the velocity in the frame
     * that turns with the Earth, which is the frame a receiver reports in.
     *
     * @param latitude The latitude, in degrees.
     * @param longitude The longitude, in degrees.
     * @param a The distance to the centre of the Earth, in kilometres.
     * @param velocity The velocity in the inertial frame, in kilometres per
     * second.
     * @param time The time this position is for.
     * @param positionEarthFixed The position in the Earth-fixed frame, in
     * kilometres.
     * @param velocityEarthFixed The velocity in the Earth-fixed frame, in
     * kilometres per second.
     */
    public OrbitParameters(double latitude, double longitude, double a,
            Vector velocity, Date time, Vector positionEarthFixed, Vector velocityEarthFixed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.a = a;
        this.velocity = velocity;
        this.time = time;
        this.positionEarthFixed = positionEarthFixed;
        this.velocityEarthFixed = velocityEarthFixed;
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

    /**
     * @return The position in the frame that turns with the Earth, in
     * kilometres, or null when this position was not worked out in it.
     */
    public Vector getPositionEarthFixed() {
        return positionEarthFixed;
    }

    /**
     * @return The velocity in the frame that turns with the Earth, in
     * kilometres per second, or null when this position was not worked out in
     * it. It differs from the inertial velocity by the turning of the Earth,
     * which at this altitude is a few percent of the speed.
     */
    public Vector getVelocityEarthFixed() {
        return velocityEarthFixed;
    }

    public Vector getVelocity() {
        return velocity;
    }
}
