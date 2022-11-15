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
public interface ISDR {
    /**
    <pre>
    Low level command to interact with SDR.
    Input parameters:int cmdID,byte[] data
    Return parameters:byte[]
    Size of returned parameters: 0
    This commands accepts generic structures for SDR.
    </pre>
    */
    byte[] runRawCommand(int cmdID, byte[] data);//6001

    /**
    <pre>
    Simulator helper command: preload into memory a raw data file
    Input parameters:String fileName
    Return parameters:void
    Size of returned parameters: 0
    The filename of the data raw file. It is expected to be found in $HOME/.ops-sat-simulator/data
    </pre>
    */
    void simPreloadFile(String fileName);//6002

    /**
    <pre>
    Read samples from operating buffer
    Input parameters:int numberSamples
    Return parameters:double[]
    Size of returned parameters: 0
    
    </pre>
    */
    double[] readFromBuffer(int numberSamples);//6003

}
