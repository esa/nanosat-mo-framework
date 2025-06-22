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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/**
 * The GPS class represents a GPS that can return a position for a selected
 * orbit.
 *
 * @author Cesar Coelho
 */
public class GPS {

    private final Orbit orbit;
    private OrbitParameters position;

    /**
     * Constructor for the GPS class.
     *
     * @param selectedOrbit The orbit to be used for generation of the gps data.
     */
    public GPS(Orbit selectedOrbit) {
        this.orbit = selectedOrbit;
        position = orbit.getParametersForLatestDate();
    }

    /**
     * Returns the latest position of the satellite.
     *
     * @return the latest position of the satellite.
     */
    public OrbitParameters getPositionNow() {
        return this.getPosition(new Date());
    }

    /**
     * Returns the position of the satellite for a given time.
     *
     * @param time The time to be used for the calculation of the position.
     * @return the position of the satellite for a given time.
     */
    public synchronized OrbitParameters getPosition(Date time) {
        this.position = orbit.getParametersForDate(time);

        // The next line shouldn't be here, because if I request the Position from the GPS faster than
        // the Samplefrequency of the GPS, then I shall get the same value
        //this.Position = this.orbit.getParameters();
        double latitude = truncateDecimal(this.position.getLatitude(), 6).doubleValue();
        double longitude = truncateDecimal(this.position.getLongitude(), 6).doubleValue();

        latitude = fixBoundaries(latitude, -90, 90);
        longitude = fixBoundaries(longitude, -180, 180);

        // No errors for the velocity vector were included
        BigDecimal truncated = truncateDecimal(this.position.getA(), 1);
        Vector velocity = this.position.getVelocity();
        OrbitParameters newPosition = new OrbitParameters(latitude, longitude,
                truncated.doubleValue(), velocity, this.position.getTime());

        return newPosition;
    }

    /**
     * Truncates a double value to the defined number of decimals.
     *
     * @param x The double value to be truncated.
     * @param numberofDecimals The number of decimals to truncate.
     * @return The truncated number.
     */
    public static BigDecimal truncateDecimal(double x, int numberofDecimals) {
        // From: http://stackoverflow.com/questions/7747469/how-can-i-truncate-a-double-to-only-two-decimal-places-in-java

        RoundingMode rMode = (x > 0) ? RoundingMode.FLOOR : RoundingMode.CEILING;
        return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, rMode);
    }

    private static double fixBoundaries(double input, double low_limit, double top_limit) {
        if (input < low_limit) {
            return low_limit;
        }

        if (input > top_limit) {
            return top_limit;
        }

        return input;  // nothing to be fixed
    }
}
