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

import esa.mo.platform.impl.provider.gen.OpticalDataReceiverAdapterInterface;
import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;

/**
 *
 * @author Cesar Coelho
 */
public class OpticalDataReceiverSoftSimAdapter implements OpticalDataReceiverAdapterInterface, SimulatorAdapter {

    private final ESASimulator instrumentsSimulator;
    private PowerControlAdapterInterface pcAdapter;

    public OpticalDataReceiverSoftSimAdapter(ESASimulator instrumentsSimulator,
        PowerControlAdapterInterface pcAdapter) {
        this.instrumentsSimulator = instrumentsSimulator;
        this.pcAdapter = pcAdapter;
    }

    @Override
    public boolean isUnitAvailable() {
        return pcAdapter.isDeviceEnabled(DeviceType.OPTRX);
    }

    @Override
    public byte[] recordOpticalReceiverData(Duration recordingLength) {
        int nSamples = (int) (recordingLength.getValue() * 1000); // Assume 1kHz sample rate
        return instrumentsSimulator.getpOpticalReceiver().readFromMessageBuffer(nSamples);
    }

}
