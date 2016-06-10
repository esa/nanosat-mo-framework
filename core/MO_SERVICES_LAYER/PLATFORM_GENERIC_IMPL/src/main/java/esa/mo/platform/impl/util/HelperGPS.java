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
import org.ccsds.moims.mo.platform.gps.structures.PositionExtraDetails;


/**
 *
 * @author Cesar Coelho
 */
public class HelperGPS {

    
    public static double DDMMpMMMM2degrees(String DDMMpMMMM)
    {
        System.out.println("DDMMpMMMM2degrees ["+DDMMpMMMM+"]" );
        if (DDMMpMMMM.length()==9)
        {
            return Double.parseDouble(DDMMpMMMM.substring(0, 1))+Double.parseDouble(DDMMpMMMM.substring(2, 3))/60+Double.parseDouble(DDMMpMMMM.substring(5, 8))/3600;
        }
        else
        {
            return 0;
        }
    }
    /**
     * Converts a GPGGA NMEA sentence into a partial Position object.
     *
     * @param gpgga GPGGA NMEA sentence
     * @return Position object
     */
    public static Position gpgga2partialPosition(String gpgga){
        Position pos = new Position();
        String [] items = gpgga.split(",");
        pos.setAltitude(Double.parseDouble(items[8]));
        pos.setLatitude(DDMMpMMMM2degrees(items[1]));
        pos.setLongitude(DDMMpMMMM2degrees(items[3]));
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

        Position pos = gpgga2partialPosition(gpgga);
        PositionExtraDetails posExtraDetails=new PositionExtraDetails();
        pos.setExtraDetails(posExtraDetails);
        return pos;
    }
    
    
       
}
