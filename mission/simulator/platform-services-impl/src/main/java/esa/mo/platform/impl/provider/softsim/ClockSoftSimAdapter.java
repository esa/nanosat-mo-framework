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
package esa.mo.platform.impl.provider.softsim;

import org.ccsds.moims.mo.mal.structures.Time;
import esa.mo.platform.impl.provider.gen.ClockAdapterInterface;
import opssat.simulator.main.ESASimulator;

public class ClockSoftSimAdapter implements ClockAdapterInterface, SimulatorAdapter {

    private final ESASimulator instrumentsSimulator;

    public ClockSoftSimAdapter(ESASimulator instrumentsSimulator) {
        this.instrumentsSimulator = instrumentsSimulator;
    }

    public Time getTime() {
        return new Time(this.instrumentsSimulator.getSimulatorNode().getSimulatedTime());
    }

    public int getTimeFactor() {
        return this.instrumentsSimulator.getSimulatorNode().getTimeFactor();
    }
}
