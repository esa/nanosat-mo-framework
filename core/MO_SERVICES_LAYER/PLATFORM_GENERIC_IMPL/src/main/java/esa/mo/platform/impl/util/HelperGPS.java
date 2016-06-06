/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 * 
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 * ----------------------------------------------------------------------------
 */
package esa.mo.platform.impl.util;

import org.ccsds.moims.mo.platform.gps.structures.Position;


/**
 *
 * @author Cesar Coelho
 */
public class HelperGPS {

    /**
     * Converts a GPGGA NMEA sentence into a partial Position object.
     *
     * @param gpgga GPGGA NMEA sentence
     * @return Position object
     */
    public static Position gpgga2partialPosition(String gpgga){

        Position pos = new Position();
        pos.setAltitude(Double.MIN_VALUE);
        pos.setLatitude(Double.MIN_VALUE);
        // To be done

        return pos;
    }

    /**
     * Converts a GPGGA NMEA sentence and SMTH into a full Position object.
     *
     * @param gpgga GPGGA NMEA sentence
     * @param smth
     * @return Position object
     */
    public static Position gpgga2fullPosition(String gpgga, String smth){

        Position pos = new Position();
        pos.setAltitude(Double.MIN_VALUE);
        pos.setLatitude(Double.MIN_VALUE);
        

        return pos;
    }
    
    
       
}
