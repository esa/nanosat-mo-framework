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
package esa.mo.platform.impl.provider.lite;

import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.Orbit;

/**
 * The orbit the lite simulator flies, taken from the environment.
 * <p>
 * A constellation is a set of containers of one image, so the orbit cannot be
 * built into it: each container is told which orbit it flies through the
 * environment, under the same six names the Orekit simulator is told through.
 * That simulator reads a configuration file, which a script writes before it
 * starts; this one has no such file, so the elements are read here instead.
 * <p>
 * The elements are in the units a reader would expect of them, which is to say
 * kilometres and degrees, and are converted to what {@link Orbit} takes.
 */
public final class OrbitFromEnvironment {

    private static final Logger LOGGER = Logger.getLogger(OrbitFromEnvironment.class.getName());

    /** Semi-major axis, in kilometres. */
    public static final String KEPLER_A = "KEPLER_A";

    /** Eccentricity, which has no unit. */
    public static final String KEPLER_E = "KEPLER_E";

    /** Inclination, in degrees. */
    public static final String KEPLER_I = "KEPLER_I";

    /** Right ascension of the ascending node, in degrees. */
    public static final String KEPLER_RAAN = "KEPLER_RAAN";

    /** Argument of perigee, in degrees. */
    public static final String KEPLER_ARG_PER = "KEPLER_ARG_PER";

    /** True anomaly, in degrees. */
    public static final String KEPLER_TRUE_A = "KEPLER_TRUE_A";

    private OrbitFromEnvironment() {
    }

    /**
     * Reads the orbit of this spacecraft.
     *
     * @return The orbit the environment describes, or null when it describes
     * none, so that the caller keeps the orbit it would have used.
     */
    public static Orbit read() {
        if (System.getenv(KEPLER_A) == null) {
            return null; // Nothing was asked for: the default orbit stands.
        }

        try {
            double a = parse(KEPLER_A);
            double e = parse(KEPLER_E);
            double i = parse(KEPLER_I);
            double raan = parse(KEPLER_RAAN);
            double argPer = parse(KEPLER_ARG_PER);
            double trueAnomaly = parse(KEPLER_TRUE_A);

            LOGGER.log(Level.INFO, "The orbit is taken from the environment: "
                    + "a={0} km, e={1}, i={2} deg, RAAN={3} deg, "
                    + "arg. of perigee={4} deg, true anomaly={5} deg",
                    new Object[]{a, e, i, raan, argPer, trueAnomaly});

            // Orbit takes its angles in radians, and its elements in the order
            // in which it declares them.
            return new Orbit(a, Math.toRadians(i), Math.toRadians(raan),
                    Math.toRadians(argPer), e, Math.toRadians(trueAnomaly));
        } catch (NumberFormatException ex) {
            LOGGER.log(Level.SEVERE, "The orbit in the environment could not be "
                    + "read, so the default orbit is flown instead.", ex);
            return null;
        }
    }

    /**
     * @param name The name of the element.
     * @return The value of that element, or zero where it is not given.
     */
    private static double parse(String name) {
        String value = System.getenv(name);
        return (value == null || value.trim().isEmpty()) ? 0 : Double.parseDouble(value.trim());
    }
}
