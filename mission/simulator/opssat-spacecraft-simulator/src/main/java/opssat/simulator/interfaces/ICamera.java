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
public interface ICamera {

    /**
     * <pre>
     * High level command: file written to filesystem to request camera take a picture
     * Input parameters:int width,int height
     * Size of returned parameters: 7962624
     * </pre>
     *
     * @param width The width input parameters are in pixels.
     * @param height The height input parameters are in pixels.
     * @return An array containing the picture
     */
    byte[] takePicture(int width, int height);//3001

    /**
     * <pre>
     * Simulator helper command: preload into memory a raw camera picture
     * Input parameters:String fileName
     * Return parameters:void
     * Size of returned parameters: 0
     * </pre>
     *
     * @param fileName The filename of the raw picture. It is expected to be
     * found in $HOME/.ops-sat-simulator/data
     */
    void simPreloadPicture(String fileName);//3002

}
