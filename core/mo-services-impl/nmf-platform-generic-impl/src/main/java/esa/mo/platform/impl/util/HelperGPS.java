/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
import java.util.Calendar;
import java.util.TimeZone;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.platform.gps.structures.*;

/**
 * A Helper class with some conversions for GPS NMEA sentences
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

    public static class GPGSV_COL {

        public final static int HEADER = 0;
        public final static int NUMBER_MSGS = 1;
        public final static int CURRENT_MSG = 2;
        public final static int NUMBER_SATS = 3;
        public final static int SAT_PRN = 4;
        public final static int SAT_ELEV = 5;
        public final static int SAT_AZ = 6;
        public final static int SAT_SNR = 7;
        public final static int MIN_CHECKSUM = 8;
    }

    public static class BESTXYZ_FIELD {

        public final static int PSOL_STATUS = 0;
        public final static int POS_TYPE = 1;
        public final static int PX = 2;
        public final static int PY = 3;
        public final static int PZ = 4;
        public final static int PX_DEVIATION = 5;
        public final static int PY_DEVIATION = 6;
        public final static int PZ_DEVIATION = 7;
        public final static int VSOL_STATUS = 8;
        public final static int VEL_TYPE = 9;
        public final static int VX = 10;
        public final static int VY = 11;
        public final static int VZ = 12;
        public final static int VX_DEVIATION = 13;
        public final static int VY_DEVIATION = 14;
        public final static int VZ_DEVIATION = 15;
        public final static int STN_ID = 16;
        public final static int V_LATENCY = 17;
        public final static int DIFF_AGE = 18;
        public final static int SOL_AGE = 19;
        public final static int NUM_SV = 20;
        public final static int NUM_SOLN_SV = 21;
        public final static int NUM_GGL1 = 22;
        public final static int NUM_SOLN_MULTI_SV = 23;
        // number 24 is reserved
        public final static int EXT_SOL_STAT = 25;
        public final static int GALILEO_AND_BEIDOU_SIG_MASK = 26;
        public final static int GPS_AND_GLONASS_SIG_MASK = 27;
    }

    /**
     * Converts a GPGGA NMEA sentence into a Position object.
     *
     * @param gpgga GPGGA NMEA sentence
     * @return Position object
     * @throws java.io.IOException
     */
    public static Position gpggalong2Position(String gpgga) throws IOException {
        return gpgga2Position(gpgga);
    }

    /**
     * Converts a GPGGA NMEA sentence into a Position object.
     *
     * @param gpgga GPGGA NMEA sentence
     * @return Position object
     * @throws java.io.IOException
     */
    public static Position gpgga2Position(String gpgga) throws IOException {
        Position pos = new Position();
        String[] items = gpgga.split(",");
        try {
            pos.setAltitude(Float.parseFloat(items[GPGGA_GEN_COL.ALTITUDE]));
            pos.setLatitude(
                    degMinutes2Degrees(items[GPGGA_GEN_COL.LAT]) * ((items[GPGGA_GEN_COL.LAT_DIR]).equals("S")
                    ? -1 : 1));
            pos.setLongitude(
                    degMinutes2Degrees(items[GPGGA_GEN_COL.LONG]) * ((items[GPGGA_GEN_COL.LONG_DIR]).equals(
                    "W") ? -1 : 1));

            PositionExtraDetails posExtraDetails = new PositionExtraDetails();
            posExtraDetails.setPositionSource(PositionSourceType.GNSS);
            if (items[GPGGA_GEN_COL.QUAL].length() != 0) {
                posExtraDetails.setFixQuality(Integer.parseInt(items[GPGGA_GEN_COL.QUAL]));
            } else {
                posExtraDetails.setFixQuality(null);
            }
            if (items[GPGGA_GEN_COL.HDOP].length() != 0) {
                posExtraDetails.setHdop(Float.parseFloat(items[GPGGA_GEN_COL.HDOP]));
            } else {
                posExtraDetails.setHdop(null);
            }
            if (items[GPGGA_GEN_COL.SATS_IN_USE].length() != 0) {
                posExtraDetails.setNumberOfSatellites(Integer.parseInt(items[GPGGA_GEN_COL.SATS_IN_USE]));
            } else {
                posExtraDetails.setNumberOfSatellites(null);
            }
            if (items[GPGGA_GEN_COL.UNDULATION].length() != 0) {
                posExtraDetails.setUndulation(Float.parseFloat(items[GPGGA_GEN_COL.UNDULATION]));
            } else {
                posExtraDetails.setUndulation(null);
            }

            /*
      * Time needs to be calculated, because GGA message only contains
      * Hours, minutes and seconds but not day and year
      * Format: hhmmss.ss
      * with:
      * hh = hour of day (24h format)
      * mm = minute of hour
      * ss.ss = second in Minute (with fractional second)
             */
            String time = items[GPGGA_GEN_COL.UTC];
            int hours = Integer.parseInt(time.substring(0, 2));
            int minutes = Integer.parseInt(time.substring(2, 4));
            int seconds = Integer.parseInt(time.substring(4, 6));
            // The GGALONG sentence also contains the fractions of second witch is not contained in the GGA sentence
            int miliSeconds = (int) (Double.valueOf(time.substring(6, 9)) * 1000); // convert fractional seconds to milliseconds

            // Get current time
            Calendar cal = (Calendar) Calendar.getInstance().clone();
            Calendar cal2 = (Calendar) cal.clone();

            // Set Timezone to utc
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal2.setTimeZone(TimeZone.getTimeZone("UTC"));

            // Set time values from GGA NMEA sentence
            cal.set(Calendar.HOUR_OF_DAY, hours);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, seconds);
            cal.set(Calendar.MILLISECOND, miliSeconds);

            // In case, the current time is shortly after midnight and the message was received before midnight
            if (cal.after(cal2)) {
                // Subtract one day, so that the timestamp isn't 24h off
                cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) - 1);
            }

            posExtraDetails.setUtc(new Time(cal.toInstant().toEpochMilli()));

            pos.setExtraDetails(posExtraDetails);
            return pos;
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }
    }

    /**
     * Converts a GPGSV NMEA sentence into a SatelliteInfoList object.
     *
     * @param gpgsv GPGSV NMEA sentence
     * @return SatelliteInfoList object
     * @throws java.io.IOException
     */
    public static SatelliteInfoList gpgsv2SatelliteInfoList(final String gpgsv) throws IOException {
        try {
            SatelliteInfoList sats = new SatelliteInfoList();
            String[] sentences = gpgsv.split("\n");
            for (String sentence : sentences) {
                String[] words = sentence.split(",|\\*");
                int count = words.length;
                int sentenceSatCount = (count - 5) / 4;
                if (sentenceSatCount <= 0 || sentenceSatCount > 4) {
                    throw new IOException("Sentence [" + sentence + "] has calculated [" + sentenceSatCount + "] sat count");
                }
                int expectedSize = sentenceSatCount * 4 + 5;
                if (count != expectedSize) {
                    throw new IOException("Sentence [" + sentence + "] has wrong GPS sentence size [" + count + "], expected minimum [" + expectedSize + "]");

                }
                if (!"$GPGSV".equals(words[GPGSV_COL.HEADER])) {
                    throw new IOException("Sentence [" + sentence + "] has wrong header ["
                            + words[GPGSV_COL.HEADER] + "], expected [$GPGSV]");
                }
                for (int satOffset = 0, i = 0; i < sentenceSatCount; i++, satOffset += 4) {
                    float azimuth = 0, elevation = 0;
                    Float snr;
                    int prn;
                    prn = Integer.parseInt(words[satOffset + GPGSV_COL.SAT_PRN]);
                    elevation = Float.parseFloat(words[satOffset + GPGSV_COL.SAT_ELEV]);
                    azimuth = Float.parseFloat(words[satOffset + GPGSV_COL.SAT_AZ]);
                    try {
                        snr = Float.parseFloat(words[satOffset + GPGSV_COL.SAT_SNR]);
                    } catch (NumberFormatException e) {
                        snr = null; // Workaround for OEM615 not sending SNR of some sats
                    }
                    if (prn > 0) {
                        sats.add(new SatelliteInfo(azimuth, elevation, prn, 
                                null, null, null, null, null, snr, null, null));
                    }
                }
            }
            return sats;
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }
    }

    public static float degMinutes2Degrees(String in) throws IOException {
        int len = in.length();
        if (len < 4 || len > 13) {
            throw new IOException("Wrong string length for '" + in + "'");
        }
        int decimalAt = in.indexOf('.');
        if (decimalAt == -1) {
            throw new IOException("Did not find decimal in " + in);
        }
        try {
            return Float.parseFloat(in.substring(0, decimalAt - 2))
                    + Float.parseFloat(in.substring(decimalAt - 2, len)) / 60;
        } catch (NumberFormatException e) {
            throw new IOException("Failed to parse degrees and minutes.", e);
        }
    }

    /**
     *
     * Extracts the fields of an BestXYZ message
     *
     * @param bestXYZ BestXYZ message String
     * @return a String Array containing the fields of the given BestXYZ Message
     */
    public static String[] getDataFieldsFromBestXYZ(String bestXYZ) {
        String tmp = bestXYZ.split(";")[1]; //cut of header wich is seperated by a ';'
        tmp = tmp.split("\\*")[0]; //cut of footer wich is seperated by a '*'
        return tmp.split(",");
    }

    /**
     * Extracts the Header of an BestXYZ message
     *
     * @param bestXYZ BestXYZ message String
     * @return String containing the Header of the BestXYZ message
     */
    public static String getHeaderFromBestXYZ(String bestXYZ) {
        return bestXYZ.split(";")[0];
    }

    /**
     * Removes details from the NMEA log like <OK [COM1]
     *
     * @param log NMEA log string
     * @return sanitized NMEA log string
     */
    public static String sanitizeNMEALog(String log) {
        log = log.replace("<OK", "").replace("[COM1]", "");
        if (log.contains("$")) {
            log = log.substring(log.indexOf("$"));
        }
        return log.trim(); // Remove whitespaces
    }

}
