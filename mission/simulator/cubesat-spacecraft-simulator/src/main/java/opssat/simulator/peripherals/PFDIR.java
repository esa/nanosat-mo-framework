/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.peripherals;

import java.util.ArrayList;
import opssat.simulator.interfaces.IFDIR;
import opssat.simulator.interfaces.InternalData;
import opssat.simulator.threading.SimulatorNode;

/**
 *
 * @author Cezar Suteu
 */
public class PFDIR extends GenericPeripheral implements IFDIR {

    public PFDIR(SimulatorNode simulatorNode, String name) {
        super(simulatorNode, name);
    }

    @Override
    @InternalData(internalID = 5001, commandIDs = {"", ""}, argNames = {"cmdID", "data"})
    public byte[] runRawCommand(int cmdID, byte[] data) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(cmdID);
        argObject.add(data);
        return (byte[]) super.getSimulatorNode().runGenericMethod(5001, argObject);
    }

}
