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
public interface IOpticalReceiver {
    //# byte[] operatingBuffer
    //# Integer successRate
    /**
    <pre>
    Low level command to interact with OpticalReceiver.
    Input parameters:int cmdID,byte[] data
    Return parameters:byte[]
    Size of returned parameters: 0
    This commands accepts generic structures for OpticalReceiver.
    </pre>
    */
    byte[] runRawCommand(int cmdID, byte[] data);//7001

    /**
    <pre>
    Simulator method only: sets the operating buffer of the optical receiver.
    Input parameters:byte[] buffer
    Return parameters:void
    Size of returned parameters: 0
    The operating buffer is used as a source of incoming data. It is retrieved with readFromMessage and when it has reached end it is wrapped around. 
    </pre>
    */
    void simSetMessageBuffer(byte[] buffer);//7002

    /**
    <pre>
    Simulator method only: sets the chance a bit from the operating buffer will be flipped upon read.
    Input parameters:int successRate
    Return parameters:void
    Size of returned parameters: 0
    Calculation is applied to each bit: maximum value 10000 is 100% message consistency(no bit flip). Minimum value 5000 is 50% chance of bit flip. 
    </pre>
    */
    void simSetSuccessRate(int successRate);//7003

    /**
    <pre>
    Read bytesNo from operating buffer
    Input parameters:int bytesNo
    Return parameters:byte[]
    Size of returned parameters: 0
    
    </pre>
    */
    byte[] readFromMessageBuffer(int bytesNo);//7004

    /**
    <pre>
    Simulator helper command: preload into memory a raw data file
    Input parameters:String fileName
    Return parameters:void
    Size of returned parameters: 0
    The filename of the data raw file. It is expected to be found in $HOME/.ops-sat-simulator/data
    </pre>
    */
    void simPreloadFile(String fileName);//7005

}
