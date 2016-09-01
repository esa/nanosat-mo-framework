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
package esa.mo.platform.impl.provider.gen;

import java.io.IOException;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeDefinition;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeInstance;

/**
 *
 * @author Cesar Coelho
 */
public interface AutonomousADCSAdapterInterface {
    
    /**
     * The isADCSAvailable operation checks if the ADCS unit.
     *
     * @return
     */
    public boolean isUnitAvailable();

    /**
     * The setAttitudeDefinition operation shall set a certain attitude based
     * on the AttitudeDefinition 
     *
     * @param attitude
     * @throws IOException
     */
    public void setDesiredAttitude(AttitudeDefinition attitude) throws IOException;

    /**
     * The unset operation shall set a certain attitude based
     * on the AttitudeDefinition 
     *
     * @throws IOException
     */
    public void unset() throws IOException;
    
    /**
     * The getAttitudeTM returns an object representing the information
     * usually provided by an ADCS unit
     *
     * @return The Attitude Telemetry from the ADCS
     * @throws IOException
     */
    public AttitudeInstance getAttitudeInstance() throws IOException;
    
    
}
