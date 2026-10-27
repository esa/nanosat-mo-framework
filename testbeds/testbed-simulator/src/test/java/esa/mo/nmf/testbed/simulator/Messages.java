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

import esa.mo.nmf.mission.orekit.simulator.util.CommandDescriptor;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorDeviceData;
import java.util.LinkedList;

/** What the simulator sends, told apart as its GUI tells them apart. */
final class Messages {

    private Messages() {
    }

    /** The list of commands and devices that the simulator sends when a client asks for it. */
    static boolean isCommandList(LinkedList<?> list) {
        return !list.isEmpty() && list.stream().anyMatch(CommandDescriptor.class::isInstance);
    }

    /** The values of the devices, which the simulator sends periodically. */
    static boolean isDeviceData(LinkedList<?> list) {
        return !list.isEmpty() && list.stream().allMatch(SimulatorDeviceData.class::isInstance);
    }

    /** The command of the list that asks the GPS for its last known position, which takes no arguments. */
    static CommandDescriptor theLastKnownPositionCommand(LinkedList<?> list) {
        return list.stream()
                .filter(CommandDescriptor.class::isInstance)
                .map(CommandDescriptor.class::cast)
                .filter(c -> "GPS".equals(c.getIntF()))
                .filter(c -> "String getLastKnownPosition()".equals(c.getMethodBody()))
                .findFirst()
                .orElse(null);
    }
}
