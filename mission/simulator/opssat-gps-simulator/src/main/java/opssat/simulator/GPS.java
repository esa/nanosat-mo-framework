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

import opssat.simulator.Orbit.OrbitParameters;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.Timer;

/**
 *
 * @author Cesar Coelho
 */
public class GPS {

    private final Orbit orbit;
    private Orbit.OrbitParameters Position;
    private final static int NUMERICAL_ERROR = 2;  // 2 meters
    private final static int POSITION_ERROR = 10;  // 10 meters

    // GPS characteristics
    int SampleFrequency = 1 * 1000; //milliseconds (1 update per second)

    // Errors
    Orbit.OrbitParameters numericalError;
    Orbit.OrbitParameters positionError;

    // Timer
    Timer timer;
    private final Object MUTEX = new Object();

    public GPS(Orbit selectedOrbit) {
        this.orbit = selectedOrbit;
        this.timer = new Timer();

        Position = orbit.getParameters();

        // Generate a POSITION_ERROR position error
        positionError = GPS.this.generateError(POSITION_ERROR, Position);

        // Generate a NUMERICAL_ERROR numerical error
        numericalError = GPS.this.generateError(NUMERICAL_ERROR, Position);

    }

    public OrbitParameters getPosition() {
        return this.getPosition(this.Position.gettime());
    }

    public OrbitParameters getPosition(Date time) {

        OrbitParameters PositionWithErrors;

        synchronized (MUTEX) {
            this.Position = orbit.getParameters(time);

            // Change the numericalError every: this.SampleFrequency
            numericalError = GPS.this.generateError(NUMERICAL_ERROR, Position);

            // The next line shouldn't be here, because if I request the Position from the GPS faster than
            // the Samplefrequency of the GPS, then I shall get the same value
            //this.Position = this.orbit.getParameters();
            double latitude = truncateDecimal(this.Position.getlatitude() + this.positionError.getlatitude() + this.numericalError.getlatitude(), 6).doubleValue();
            double longitude = truncateDecimal(this.Position.getlongitude() + this.positionError.getlongitude() + this.numericalError.getlongitude(), 6).doubleValue();

            latitude = fixBoundaries(latitude, -90, 90);
            longitude = fixBoundaries(longitude, -180, 180);

            // No errors for the velocity vector were included
            PositionWithErrors = new OrbitParameters(
                    latitude,
                    longitude,
                    truncateDecimal(this.Position.geta() + this.positionError.geta() + this.numericalError.geta(), 1).doubleValue(),
                    new Vector(this.Position.getvelocity().x(), this.Position.getvelocity().y(), this.Position.getvelocity().z()),
                    this.Position.gettime());

        }

        return PositionWithErrors;
    }

    public static BigDecimal truncateDecimal(double x, int numberofDecimals) {
        // From: http://stackoverflow.com/questions/7747469/how-can-i-truncate-a-double-to-only-two-decimal-places-in-java
        if (x > 0) {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_FLOOR);
        } else {
            return new BigDecimal(String.valueOf(x)).setScale(numberofDecimals, BigDecimal.ROUND_CEILING);
        }
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

    // k is the constant and it's the error in meters
    private OrbitParameters generateError(double k, OrbitParameters param) {
        // Generate errors
//    System.out.printf("Time: %s\n", RealPosition.time.toString());
        Random randomno = new Random();

        // Factor to convert the k from meters to degrees
        double factor = 360 / (2 * Math.PI * param.geta());

        // The values are divided by 3 to represent a 3 sigma confidence interval
        // The meters need to be converted to kilometers ("/ 1000")

        return new OrbitParameters(
                factor * k / 3 * randomno.nextGaussian(),
                factor * k / 3 * randomno.nextGaussian(),
                k / 1000 / 3 * randomno.nextGaussian(),
                new Vector(0, 0, 0),
                this.Position.gettime());
    }
}
