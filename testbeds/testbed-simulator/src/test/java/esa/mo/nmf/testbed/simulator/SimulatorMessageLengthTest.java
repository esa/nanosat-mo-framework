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
package esa.mo.nmf.testbed.simulator;

import esa.mo.nmf.mission.orekit.simulator.tcp.SimulatorMessages;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

/**
 * A line longer than the simulator accepts is refused, so that a client cannot
 * make the simulator hold an endless message in memory. It reads more than
 * 64 MiB, which is why it runs here and not in the build of the simulator.
 */
public class SimulatorMessageLengthTest {

    @Test
    public void aLineThatIsTooLongIsRefused() {
        byte[] endless = new byte[SimulatorMessages.MAX_LINE_BYTES + 1];
        Arrays.fill(endless, (byte) 'a');
        try {
            SimulatorMessages.read(new ByteArrayInputStream(endless));
            Assert.fail("A line longer than " + SimulatorMessages.MAX_LINE_BYTES + " bytes must be refused");
        } catch (IOException expected) {
            // Refused
        }
    }
}
