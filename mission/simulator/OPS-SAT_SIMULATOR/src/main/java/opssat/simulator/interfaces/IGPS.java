/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2016      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under the European Space Agency Public License, Version 2.0
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
public interface IGPS {
//# String latitude
//# String longitude
//# String altitude

    /**
     * <pre>
     * Obtain a NMEA response for a given NMEA sentence
     * Input parameters:String inputSentence
     * Return parameters:String
     * Size of returned parameters: 0
     *
     * </pre>
     */
    String getNMEASentence(String inputSentence);//2001

    /**
     * <pre>
     * Obtain the last known position of the s/c
     * Input parameters:
     * Return parameters:String
     * Size of returned parameters: 0
     *
     * </pre>
     */
    String getLastKnownPosition();//2002
}
