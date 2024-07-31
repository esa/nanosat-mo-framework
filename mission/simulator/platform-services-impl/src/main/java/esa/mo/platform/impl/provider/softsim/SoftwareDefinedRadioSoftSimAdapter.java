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

import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import esa.mo.platform.impl.provider.gen.SoftwareDefinedRadioAdapterInterface;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.structures.FloatList;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponents;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.SDRConfiguration;

/**
 *
 * @author Cesar Coelho
 */
public class SoftwareDefinedRadioSoftSimAdapter implements SoftwareDefinedRadioAdapterInterface, SimulatorAdapter {

    private final ESASimulator instrumentsSimulator;
    private PowerControlAdapterInterface pcAdapter;

    public SoftwareDefinedRadioSoftSimAdapter(ESASimulator instrumentsSimulator,
        PowerControlAdapterInterface pcAdapter) {
        this.instrumentsSimulator = instrumentsSimulator;
        this.pcAdapter = pcAdapter;
    }

    @Override
    public boolean isUnitAvailable() {
        return pcAdapter.isDeviceEnabled(DeviceType.SDR);
    }

    @Override
    public boolean setConfiguration(SDRConfiguration configuration) {
        return false;
    }

    @Override
    public boolean enableSDR(Boolean enable) {
        return true;
    }

    @Override
    public IQComponents getIQComponents() {

        int nSamples = 10000;
        double[] data = instrumentsSimulator.getpSDR().readFromBuffer(nSamples);

        FloatList inPhase = new FloatList();
        FloatList quadrature = new FloatList();

        // Trim the accuracy to match NMF interface
        for (int i = 0; i < nSamples; i++) {
            inPhase.add((float) data[2 * i]);
            quadrature.add((float) data[2 * i + 1]);
        }

        IQComponents iqComponents = new IQComponents();
        iqComponents.setInPhase(inPhase);
        iqComponents.setQuadrature(quadrature);

        return iqComponents;
    }

}
