/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.mission.orekit.simulator.threading;

import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * What the simulator makes of the orbit a header file gives it.
 * <p>
 * The one that matters is the orbit of zeros: it is what an image carries until
 * a spacecraft is told where to fly, and flying it gave a position of NaN and a
 * spacecraft that never moved.
 */
public class OrbitOfTest {

    private static final double[] DEFAULT = {SimulatorNode.DEFAULT_OPS_SAT_A,
        SimulatorNode.DEFAULT_OPS_SAT_E, SimulatorNode.DEFAULT_OPS_SAT_ORBIT_I,
        SimulatorNode.DEFAULT_OPS_SAT_RAAN, SimulatorNode.DEFAULT_OPS_SAT_ARG_PER,
        SimulatorNode.DEFAULT_OPS_SAT_TRUE_ANOMALY};

    private static final Logger QUIET = quiet();

    private static Logger quiet() {
        Logger logger = Logger.getLogger(OrbitOfTest.class.getName());
        logger.setLevel(Level.OFF);
        return logger;
    }

    @Test
    public void anOrbitIsFlownAsItIsWritten() {
        assertArrayEquals(new double[]{7021.0, 0.0, 98.05, 340.0, 0.0, 15.0},
                SimulatorNode.orbitOf("7021.0;0.0;98.05;340.0;0.0;15.0", QUIET), 1e-9);
    }

    @Test
    public void spaceAroundTheElementsIsNotPartOfThem() {
        assertArrayEquals(new double[]{7021.0, 0.0, 98.05, 340.0, 0.0, 0.0},
                SimulatorNode.orbitOf(" 7021.0 ; 0.0 ;98.05;340.0;0.0;0.0", QUIET), 1e-9);
    }

    @Test
    public void theOrbitOfZerosIsNotFlown() {
        assertArrayEquals(DEFAULT, SimulatorNode.orbitOf("0.0;0.0;0.0;0.0;0.0;0.0", QUIET), 1e-9);
    }

    @Test
    public void anOrbitWithinTheEarthIsNotFlown() {
        assertArrayEquals(DEFAULT, SimulatorNode.orbitOf("100.0;0.0;98.05;340.0;0.0;0.0", QUIET), 1e-9);
        assertArrayEquals(DEFAULT,
                SimulatorNode.orbitOf(SimulatorNode.EARTH_RADIUS + ";0.0;98.05;340.0;0.0;0.0", QUIET), 1e-9);
    }

    @Test
    public void anOrbitTheSpacecraftWouldLeaveIsNotFlown() {
        assertArrayEquals(DEFAULT, SimulatorNode.orbitOf("7021.0;1.0;98.05;340.0;0.0;0.0", QUIET), 1e-9);
        assertArrayEquals(DEFAULT, SimulatorNode.orbitOf("7021.0;-0.1;98.05;340.0;0.0;0.0", QUIET), 1e-9);
    }

    @Test
    public void elementsThatAreNotSixAreNotFlown() {
        assertArrayEquals(DEFAULT, SimulatorNode.orbitOf("7021.0;0.0;98.05", QUIET), 1e-9);
        assertArrayEquals(DEFAULT, SimulatorNode.orbitOf("7021.0;0.0;98.05;340.0;0.0;0.0;0.0", QUIET), 1e-9);
    }

    @Test
    public void elementsThatAreNotNumbersAreNotFlown() {
        assertArrayEquals(DEFAULT, SimulatorNode.orbitOf("7021.0;0.0;polar;340.0;0.0;0.0", QUIET), 1e-9);
    }

    @Test
    public void aHeaderWithNoOrbitLeavesTheDefaultOne() {
        assertArrayEquals(DEFAULT, SimulatorNode.orbitOf(null, QUIET), 1e-9);
    }

    @Test
    public void theDefaultOrbitIsOneAndIsAboveTheEarth() {
        assertEquals(6, DEFAULT.length);
        org.junit.Assert.assertTrue("The default orbit has to be one a spacecraft can fly",
                SimulatorNode.DEFAULT_OPS_SAT_A > SimulatorNode.EARTH_RADIUS);
    }
}
