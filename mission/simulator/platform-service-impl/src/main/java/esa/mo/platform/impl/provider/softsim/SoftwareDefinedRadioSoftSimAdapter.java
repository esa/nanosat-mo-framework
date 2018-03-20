/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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

import esa.mo.platform.impl.provider.gen.SoftwareDefinedRadioAdapterInterface;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.structures.DoubleList;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponents;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponentsList;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.SDRConfiguration;

/** 
 *
 * @author Cesar Coelho
 */
public class SoftwareDefinedRadioSoftSimAdapter implements SoftwareDefinedRadioAdapterInterface {
    
    private final ESASimulator instrumentsSimulator;
    
    public SoftwareDefinedRadioSoftSimAdapter(ESASimulator instrumentsSimulator){
        this.instrumentsSimulator = instrumentsSimulator;
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
    public IQComponentsList getIQComponents() {

        int nSamples = 10000;
        double[] data = instrumentsSimulator.getpSDR().readFromBuffer(nSamples);
        
        DoubleList inPhase = new DoubleList();
        DoubleList quadrature = new DoubleList();
        
        for(int i = 0; i < nSamples; i++){
            inPhase.add(data[2*i]);
            quadrature.add(data[2*i+1]);
        }
 
        IQComponents iqComponents = new IQComponents();
        iqComponents.setInPhase(inPhase);
        iqComponents.setQuadrature(quadrature);
        
        IQComponentsList outList = new IQComponentsList();
        outList.add(iqComponents);
        return outList;
    }

    
}
