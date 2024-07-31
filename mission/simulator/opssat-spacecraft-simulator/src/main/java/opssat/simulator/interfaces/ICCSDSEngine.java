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
public interface ICCSDSEngine {

    /**
     * <pre>
     * Low level command to interact with CCSDSEngine.
     * Input parameters:int cmdID,byte[] data
     * Return parameters:byte[]
     * Size of returned parameters: 0
     * This commands accepts generic structures for CCSDSEngine.
     * </pre>
     *
     * @param cmdID
     * @param data
     * @return
     */
    byte[] runRawCommand(int cmdID, byte[] data);//8001

}
