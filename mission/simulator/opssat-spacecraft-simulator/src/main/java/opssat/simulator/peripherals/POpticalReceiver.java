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
import opssat.simulator.interfaces.IOpticalReceiver;
import opssat.simulator.interfaces.ISimulatorDeviceData;
import opssat.simulator.interfaces.InternalData;
import opssat.simulator.threading.SimulatorNode;

/**
 *
 * @author Cezar Suteu
 */
@ISimulatorDeviceData(descriptors = {"String:operatingBuffer", "int:operatingBufferIndex", "int:successRate"})
public class POpticalReceiver extends GenericPeripheral implements IOpticalReceiver {

    public POpticalReceiver(SimulatorNode simulatorNode, String name) {
        super(simulatorNode, name);
    }

    @Override
    @InternalData(internalID = 7001, commandIDs = {"", ""}, argNames = {"cmdID", "data"})
    public byte[] runRawCommand(int cmdID, byte[] data) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(cmdID);
        argObject.add(data);
        return (byte[]) super.getSimulatorNode().runGenericMethod(7001, argObject);
    }

    @Override
    @InternalData(internalID = 7002, commandIDs = {"", ""}, argNames = {"buffer"})
    public void simSetMessageBuffer(byte[] buffer) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(buffer);
        super.getSimulatorNode().runGenericMethod(7002, argObject);
    }

    @Override
    @InternalData(internalID = 7003, commandIDs = {"", ""}, argNames = {"successRate"})
    public void simSetSuccessRate(int successRate) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(successRate);
        super.getSimulatorNode().runGenericMethod(7003, argObject);
    }

    @Override
    @InternalData(internalID = 7004, commandIDs = {"", ""}, argNames = {"bytesNo"})
    public byte[] readFromMessageBuffer(int bytesNo) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(bytesNo);
        return (byte[]) super.getSimulatorNode().runGenericMethod(7004, argObject);
    }

    @Override
    @InternalData(internalID = 7005, commandIDs = {"", ""}, argNames = {"fileName"})
    public void simPreloadFile(String fileName) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(fileName);
        super.getSimulatorNode().runGenericMethod(7005, argObject);
    }

}
