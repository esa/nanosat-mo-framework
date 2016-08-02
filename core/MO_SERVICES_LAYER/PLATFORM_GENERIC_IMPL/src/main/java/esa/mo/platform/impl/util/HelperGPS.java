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

import java.io.IOException;
import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.PositionExtraDetails;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfo;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;

/**
 *
 * @author Cesar Coelho
 */
public class HelperGPS {

    public static class GPGGA_GEN_COL {

        public final static int HEADER = 0;
        public final static int UTC = 1;
        public final static int LAT = 2;
        public final static int LAT_DIR = 3;
        public final static int LONG = 4;
        public final static int LONG_DIR = 5;
        public final static int QUAL = 6;
        public final static int SATS_IN_USE = 7;
        public final static int HDOP = 8;
        public final static int ALTITUDE = 9;
        public final static int ALTITUDE_UNITS = 10;
        public final static int UNDULATION = 11;
        public final static int UNDULATION_UNITS = 12;
        public final static int AGE_CORR_DATA = 13;
        public final static int DIFF_BASESID = 14;
        public final static int CHECKSUM = 15;
    }

    /**
     * Converts a GPGGA NMEA sentence into a Position object.
     *
     * @param gpgga GPGGA NMEA sentence
     * @return Position object
     * @throws java.io.IOException
     */
    public static Position gpggalong2Position(String gpgga) throws IOException {
        Position pos = new Position();
        String[] items = gpgga.split(",");
        pos.setAltitude(Double.parseDouble(items[GPGGA_GEN_COL.ALTITUDE]));
        pos.setLatitude(DDMMpMMMMMMM2degrees(items[GPGGA_GEN_COL.LAT]) * ((items[GPGGA_GEN_COL.LAT_DIR]).equals("S") ? -1 : 1));
        pos.setLongitude(DDDMMpMMMMMMM2degrees(items[GPGGA_GEN_COL.LONG]) * ((items[GPGGA_GEN_COL.LONG_DIR]).equals("W") ? -1 : 1));

        PositionExtraDetails posExtraDetails = new PositionExtraDetails();
        posExtraDetails.setFixQuality(Integer.parseInt(items[GPGGA_GEN_COL.QUAL]));
        posExtraDetails.setHdop(Float.parseFloat(items[GPGGA_GEN_COL.HDOP]));
        posExtraDetails.setNumberOfSatellites(Integer.parseInt(items[GPGGA_GEN_COL.SATS_IN_USE]));
        posExtraDetails.setUndulation(Float.parseFloat(items[GPGGA_GEN_COL.UNDULATION]));
        posExtraDetails.setUtc(null);
        pos.setExtraDetails(posExtraDetails);

        return pos;
    }

    /**
     * Converts a GPALM NMEA sentence into a SatelliteInfoList object.
     *
     * @param gpalm GPALM NMEA sentence
     * @return SatelliteInfoList object
     * @throws java.io.IOException
     */
    public static SatelliteInfoList gpalm2SatelliteInfoList(final String gpalm) throws IOException {

        SatelliteInfoList sats = new SatelliteInfoList();
        sats.add(new SatelliteInfo());
        sats.add(new SatelliteInfo());
        sats.add(new SatelliteInfo());
        
        return sats;
    }
    
    public static double DDMMpMMMMMMM2degrees(String DDMMpMMMMMMM) throws IOException {
        if (DDMMpMMMMMMM.length() == 12) {
            return Double.parseDouble(DDMMpMMMMMMM.substring(0, 2))
                    + (Double.parseDouble(DDMMpMMMMMMM.substring(2, 4)) + Double.parseDouble(DDMMpMMMMMMM.substring(5, 12)) / 1000000) / 60;
        } else {
            throw new IOException();
        }
    }

    public static double DDDMMpMMMMMMM2degrees(String DDDMMpMMMMMMM) throws IOException {
        if (DDDMMpMMMMMMM.length() == 13) {
            return Double.parseDouble(DDDMMpMMMMMMM.substring(0, 3))
                    + (Double.parseDouble(DDDMMpMMMMMMM.substring(3, 5)) + Double.parseDouble(DDDMMpMMMMMMM.substring(6, 13)) / 1000000) / 60;
        } else {
            throw new IOException();
        }
    }

}
