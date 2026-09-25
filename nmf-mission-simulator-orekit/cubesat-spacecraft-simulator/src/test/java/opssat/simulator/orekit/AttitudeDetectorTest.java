/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package opssat.simulator.orekit;

import org.hipparchus.ode.events.Action;
import org.junit.Assert;
import org.junit.Test;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;

/**
 * Tests that an AttitudeDetector survives being copied.
 *
 * The attitude of the spacecraft is changed by events. An AttitudesSequence is
 * given a detector for each attitude mode and puts its own handler on each of
 * them; that handler is what switches the attitude provider when the detector
 * fires. Orekit never changes a detector in place, so asking for one with a
 * handler returns a copy, and the copy is what the propagator ends up using.
 *
 * A copy that forgets which mode it watches, or forgets the handler it was
 * given, leaves the sequence with detectors that cannot switch anything. The
 * attitude then stays as it is and every command to change it is accepted and
 * has no effect, which is what these tests are here to prevent.
 *
 * @author Cesar Coelho
 */
public class AttitudeDetectorTest {

    /** The name of the additional state the detectors read. */
    private static final String ATTITUDE = "attitude";

    /** How many attitude modes the state carries. */
    private static final int MODES = 8;

    /** Stands in for the handler an AttitudesSequence puts on a detector. */
    private static class MarkerHandler implements EventHandler<AttitudeDetector> {

        @Override
        public Action eventOccurred(SpacecraftState s, AttitudeDetector detector, boolean increasing) {
            return Action.CONTINUE;
        }

        @Override
        public SpacecraftState resetState(AttitudeDetector detector, SpacecraftState oldState) {
            return oldState;
        }
    }

    // -------------------------------------------------------------------------
    // Test 1 — a copy keeps the handler
    // -------------------------------------------------------------------------
    @Test
    public void copyKeepsTheHandlerItWasGiven() {
        MarkerHandler handler = new MarkerHandler();
        AttitudeDetector copy = new AttitudeDetector(3).withHandler(handler);

        Assert.assertSame("The copy must carry the handler it was given: that handler "
                + "is what performs the switch between attitude providers",
                handler, copy.getHandler());
    }

    // -------------------------------------------------------------------------
    // Test 2 — a copy watches the same attitude mode
    // -------------------------------------------------------------------------
    @Test
    public void copyKeepsWatchingTheSameMode() {
        for (int mode = 0; mode < MODES; mode++) {
            AttitudeDetector copy = new AttitudeDetector(mode).withHandler(new MarkerHandler());

            Assert.assertEquals("A copy of the detector for mode " + mode
                    + " must still watch mode " + mode + ", or every detector ends up "
                    + "watching the same one",
                    1.0, copy.g(stateWithOnly(mode)), 0.0);
        }
    }

    // -------------------------------------------------------------------------
    // Test 3 — copies made for other reasons keep the mode too
    // -------------------------------------------------------------------------
    @Test
    public void copyForOtherReasonsAlsoKeepsTheMode() {
        AttitudeDetector copy = new AttitudeDetector(5).withMaxCheck(120.0);

        Assert.assertEquals("The check interval must be the one asked for",
                120.0, copy.getMaxCheckInterval(), 0.0);
        Assert.assertEquals("Changing the check interval must not change which attitude "
                + "mode the detector watches",
                1.0, copy.g(stateWithOnly(5)), 0.0);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------
    /**
     * A state whose attitude additional state is one at the given mode and zero
     * everywhere else, so that what a detector reports says which mode it reads.
     */
    private static SpacecraftState stateWithOnly(int mode) {
        double[] attitude = new double[MODES];
        attitude[mode] = 1.0;

        Orbit orbit = new KeplerianOrbit(7000000.0, 0.001, 1.71, 0.0, 0.0, 0.0,
                PositionAngle.TRUE, FramesFactory.getEME2000(), AbsoluteDate.J2000_EPOCH,
                Constants.WGS84_EARTH_MU);

        return new SpacecraftState(orbit).addAdditionalState(ATTITUDE, attitude);
    }
}
