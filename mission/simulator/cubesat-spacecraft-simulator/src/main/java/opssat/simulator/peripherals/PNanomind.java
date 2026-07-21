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
import opssat.simulator.interfaces.INanomind;
import opssat.simulator.interfaces.InternalData;
import opssat.simulator.threading.SimulatorNode;

/**
 *
 * @author Cezar Suteu
 */
public class PNanomind extends GenericPeripheral implements INanomind {
    public PNanomind(SimulatorNode simulatorNode, String name) {
        super(simulatorNode, name);
    }

    @Override
    @InternalData(internalID = 4001, commandIDs = {"", ""}, argNames = {"cmdID", "data"})
    public byte[] runRawCommand(int cmdID, byte[] data) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(cmdID);
        argObject.add(data);
        return (byte[]) super.getSimulatorNode().runGenericMethod(4001, argObject);
    }

    @Override
    @InternalData(internalID = 4002, commandIDs = {"", "0x01"}, argNames = {"device"})
    public void SetPowerState(byte device) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(device);
        super.getSimulatorNode().runGenericMethod(4002, argObject);
    }

    @Override
    @InternalData(internalID = 4003, commandIDs = {"", "0x06"}, argNames = {""})
    public byte GetPowerState() {
        ArrayList<Object> argObject = null;
        return (Byte) super.getSimulatorNode().runGenericMethod(4003, argObject);
    }

    @Override
    @InternalData(internalID = 4004, commandIDs = {"", "0x10"}, argNames = {"opmode"})
    public void SetOperationMode(byte opmode) {
        ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(opmode);
        super.getSimulatorNode().runGenericMethod(4004, argObject);
    }
}
