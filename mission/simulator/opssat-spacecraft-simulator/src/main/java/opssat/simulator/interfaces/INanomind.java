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
package opssat.simulator.interfaces;

/**
 *
 * @author Cezar Suteu
 */
public interface INanomind {
    /**
    <pre>
    Low level command to interact with Nanomind.
    Input parameters:int cmdID,byte[] data
    Return parameters:byte[]
    Size of returned parameters: 0
    This commands accepts generic structures for Nanomind.
    </pre>
    */
    byte[] runRawCommand(int cmdID, byte[] data);//4001

    /**
    <pre>
    Switch on power for a peripheral
    Input parameters:byte device
    Return parameters:void
    Size of returned parameters: 0
    UI8 : Register of selected device
    0.bit: FineADCS
    1.bit: Camera
    2.bit: GPS
    3.bit: SDR
    4.bit: Optical Receiver
    </pre>
    */
    void SetPowerState(byte device);//4002

    /**
    <pre>
    Get power status for a peripheral
    Input parameters:
    Return parameters:byte
    Size of returned parameters: 1
    Return UI8 : Power status of selected device
    0.bit: FineADCS
    1.bit: Camera
    2.bit: GPS
    3.bit: SDR
    4.bit: Optical Receiver
    </pre>
    */
    byte GetPowerState();//4003

    /**
    <pre>
    High level command to interact with Nanomind.
    Input parameters:byte opmode
    Return parameters:void
    Size of returned parameters: 0
    UI8: Mode
    0 - Nominal Mode
    1 - Experimental Mode
    </pre>
    */
    void SetOperationMode(byte opmode);//4004

}
