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

import esa.mo.platform.impl.provider.gen.MagnetometerAdapterInterface;
import esa.mo.platform.impl.util.HelperIADCS100;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.platform.magnetometer.structures.MagneticFieldInstance;
import org.ccsds.moims.mo.platform.structures.Vector3D;

/** 
 *
 * @author Cesar Coelho
 */
public class MagnetometerSoftSimAdapter implements MagnetometerAdapterInterface {
    
    private final ESASimulator instrumentsSimulator;
    
    public MagnetometerSoftSimAdapter(ESASimulator instrumentsSimulator){
        this.instrumentsSimulator = instrumentsSimulator;
    }

    @Override
    public MagneticFieldInstance getMagneticField() {
        
        byte[] telemetry = instrumentsSimulator.getpFineADCS().GetSensorTelemetry();
        Vector3D vector = HelperIADCS100.getMagneticFieldFromSensorTM(telemetry);

        MagneticFieldInstance magField = new MagneticFieldInstance();
        magField.setX(vector.getX() / 1000);
        magField.setY(vector.getY() / 1000);
        magField.setZ(vector.getZ() / 1000);

        return magField;
    }

    
}
