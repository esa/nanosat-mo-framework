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

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import opssat.simulator.threading.SimulatorNode;
import opssat.simulator.util.SimulatorHeader;
import org.junit.Assert;
import org.junit.Test;

/**
 * Commands the attitude to change, over and over, and checks that it does.
 *
 * The failure this is written against is a spacecraft that changes attitude for
 * the first command and then stops. The mode being asked for kept changing and
 * was reported faithfully by everything downstream, but the attitude settled on
 * no rotation at all and stayed there, so the third command onwards was
 * accepted and quietly ignored.
 *
 * Commanding is alternated between two modes that point in clearly different
 * directions, and every command is expected to arrive at the attitude of the
 * mode asked for. Coming back to a mode that has been flown before has to give
 * the same attitude as before, give or take the movement of the orbit, and it
 * is that returning which used to fail.
 *
 * @author Cesar Coelho
 */
public class AttitudeSwitchingTest {

    /** Seconds of propagation after each command, well past the slew. */
    private static final int SECONDS_PER_COMMAND = 75;

    /** How far apart two attitudes have to be to count as different. */
    private static final double DIFFERENT = 0.05;

    /**
     * Sun and nadir pointing, five times over. Every command has to take
     * effect, not just the first.
     */
    @Test
    public void everyCommandChangesTheAttitude() throws Exception {
        SimulatorNode node = new SimulatorNode(new ConcurrentLinkedQueue<>(),
                new ConcurrentLinkedQueue<>(), "attitude-switching-test", 100,
                Level.SEVERE, Level.SEVERE);

        SimulatorHeader header = new SimulatorHeader();
        header.setUpdateInternet(false);

        OrekitCore core = new OrekitCore(6886 * 1000, 0, 98.05, 0, 340, 0,
                header, node.getLogObject(), node);

        OrekitCore.ATTITUDE_MODE[] commands = {
            OrekitCore.ATTITUDE_MODE.SUN_POINTING,
            OrekitCore.ATTITUDE_MODE.NADIR_POINTING,
            OrekitCore.ATTITUDE_MODE.SUN_POINTING,
            OrekitCore.ATTITUDE_MODE.NADIR_POINTING,
            OrekitCore.ATTITUDE_MODE.SUN_POINTING};

        double[] reached = new double[commands.length];

        for (int command = 0; command < commands.length; command++) {
            core.changeAttitude(commands[command]);
            for (int second = 0; second < SECONDS_PER_COMMAND; second++) {
                core.processPropagateStep(1.0);
            }
            reached[command] = firstQuaternionComponent(core);
        }

        // Neighbouring commands ask for different things, so they must arrive
        // somewhere different.
        for (int command = 1; command < commands.length; command++) {
            Assert.assertNotEquals("Command " + (command + 1) + " asked for "
                    + commands[command] + " after " + commands[command - 1]
                    + ", so the attitude should have changed. Reached "
                    + reached[command - 1] + " then " + reached[command],
                    reached[command - 1], reached[command], DIFFERENT);
        }

        // Sun pointing is sun pointing, whenever it is asked for. This is what
        // stopped being true: the later ones used to arrive at no rotation.
        Assert.assertEquals("Sun pointing asked for the third time must reach the same "
                + "attitude as the first time it was asked for",
                reached[0], reached[2], DIFFERENT);
        Assert.assertEquals("Sun pointing asked for the fifth time must reach the same "
                + "attitude as the first time it was asked for",
                reached[0], reached[4], DIFFERENT);
        Assert.assertEquals("Nadir pointing asked for again must reach much the same "
                + "attitude as before, the orbit having moved on a little",
                reached[1], reached[3], 0.15);
    }

    /** The scalar part of the attitude, enough to tell these modes apart. */
    private static double firstQuaternionComponent(OrekitCore core) {
        float[] q = new float[4];
        core.putQuaternionsInVector(q);
        return q[0];
    }
}
