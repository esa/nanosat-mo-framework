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
import opssat.simulator.interfaces.ISDR;
import opssat.simulator.interfaces.ISimulatorDeviceData;
import opssat.simulator.interfaces.InternalData;
import opssat.simulator.threading.SimulatorNode;

/**
 *
 * @author Cezar Suteu
 */
@ISimulatorDeviceData(descriptors = {"String:operatingBuffer", "int:operatingBufferIndex"})
public class PSDR extends GenericPeripheral implements ISDR {
    public PSDR(SimulatorNode simulatorNode, String name) {
        super(simulatorNode, name);
    }

    @Override
    @InternalData(internalID = 6001, commandIDs = {"", ""}, argNames = {"cmdID", "data"})
    public byte[] runRawCommand(int cmdID, byte[] data) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(cmdID);
        argObject.add(data);
        return (byte[]) super.getSimulatorNode().runGenericMethod(6001, argObject);
    }

    @Override
    @InternalData(internalID = 6002, commandIDs = {"", ""}, argNames = {"fileName"})
    public void simPreloadFile(String fileName) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(fileName);
        super.getSimulatorNode().runGenericMethod(6002, argObject);
    }

    @Override
    @InternalData(internalID = 6003, commandIDs = {"", ""}, argNames = {"numberSamples"})
    public double[] readFromBuffer(int numberSamples) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(numberSamples);
        return (double[]) super.getSimulatorNode().runGenericMethod(6003, argObject);
    }

}
