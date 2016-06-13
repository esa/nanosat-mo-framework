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

    public final static int GPGGA_HEADER_COL            =0;
    public final static int GPGGA_UTC_COL               =1;
    public final static int GPGGA_LAT_COL               =2;
    public final static int GPGGA_LAT_DIR_COL           =3;
    public final static int GPGGA_LONG_COL              =4;
    public final static int GPGGA_LONG_DIR_COL          =5;
    public final static int GPGGA_QUAL_COL              =6;
    public final static int GPGGA_SATS_IN_USE_COL       =7;
    public final static int GPGGA_HDOP_COL              =8;
    public final static int GPGGA_ALTITUDE_COL          =9;
    public final static int GPGGA_ALTITUDE_UNITS_COL    =10;
    public final static int GPGGA_UNDULATION_COL        =11;
    public final static int GPGGA_UNDULATION_UNITS_COL  =12;
    public final static int GPGGA_AGE_CORR_DATA_COL     =13;
    public final static int GPGGA_DIFF_BASESID_COL      =14;
    public final static int GPGGA_CHECKSUM_COL          =15;
    
    public static double DDMMpMMMM2degrees(String DDMMpMMMM)
    { 
        if (DDMMpMMMM.length()==9)
        {
            //System.out.println("DDMMpMMMM["+DDMMpMMMM+"]");
            //System.out.println(Double.parseDouble(DDMMpMMMM.substring(5, 9))/3600);
            return Double.parseDouble(DDMMpMMMM.substring(0, 2))+(Double.parseDouble(DDMMpMMMM.substring(2, 4))+Double.parseDouble(DDMMpMMMM.substring(5, 9))/1000)/60;
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
        pos.setAltitude(Double.parseDouble(items[GPGGA_ALTITUDE_COL]));
        pos.setLatitude(DDMMpMMMM2degrees(items[GPGGA_LAT_COL])*((items[GPGGA_LAT_DIR_COL]).equals("S")?-1:1));
        pos.setLongitude(DDMMpMMMM2degrees(items[GPGGA_LONG_COL])*((items[GPGGA_LONG_DIR_COL]).equals("W")?-1:1));
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
