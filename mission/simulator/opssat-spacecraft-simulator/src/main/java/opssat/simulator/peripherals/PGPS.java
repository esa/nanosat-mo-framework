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
package opssat.simulator.peripherals;

import java.util.ArrayList;

import opssat.simulator.interfaces.IGPS;
import opssat.simulator.interfaces.ISimulatorDeviceData;
import opssat.simulator.interfaces.InternalData;
import opssat.simulator.threading.SimulatorNode;

/**
 *
 * @author Cezar Suteu
 */
@ISimulatorDeviceData(descriptors = { "double:latitude [deg]", "double:longitude [deg]",
    "double:altitude [deg]", "double:groundStationESOC_Elevation [deg]",
    "double:groundStationESOC_Azimuth [deg]", "String:satsInView" })
public class PGPS extends GenericPeripheral implements IGPS {

  public static class FirmwareReferenceOEM16 {

    public static class GPGGA_COL {
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

    public static class GPGGALONG_COL {
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

    public static class GPGGARTK_COL {
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
      public final static int NULL1 = 11;
      public final static int NULL2 = 12;
      public final static int AGE_DIFF_DATA = 13;
      public final static int DIFF_BASESID = 14;
      public final static int CHECKSUM = 15;
    }

    public static class GLMLA_COL {
      public final static int HEADER = 0;
      public final static int NUMBER_IN_SET = 1;
      public final static int NUMBER_CURRENT = 2;
      public final static int SLOT = 3;
      public final static int CALDAY_LEAP_YEAR = 4;
      public final static int HEALTH_FREQ = 5;
      public final static int ECC = 6;
      public final static int DELTAT_DOT = 7;
      public final static int ARG_PER = 8;
      public final static int CLK_OFFSET = 9;
      public final static int DELTA_T_DRACONIAN = 10;
      public final static int GLONASSTIME_ASC_NODE_EQ = 11;
      public final static int LONG_ASC_NODE_CROSS = 12;
      public final static int DELTA_NOMINAL_I = 13;
      public final static int CLK_OFFSET_LSB12 = 14;
      public final static int CLK_SHIFT_COARSE = 15;
      public final static int CHECKSUM = 16;
    }

    public static class GPALM_COL {
      public final static int HEADER = 0;
      public final static int NUMBER_MSG_LOG = 1;
      public final static int NUMBER_CURRENT = 2;
      public final static int PRN = 3;
      public final static int REF_WEEK_NO = 4;
      public final static int SV_HEALTH = 5;
      public final static int ECC = 6;
      public final static int ALM_REF_TIME = 7;
      public final static int INC_ANGLE = 8;
      public final static int OMEGA_DOT = 9;
      public final static int RT_AXIS = 10;
      public final static int OMEGA = 11;
      public final static int LONG_ASC_NODE = 12;
      public final static int MO_MEAN_ANOMALY = 13;
      public final static int AF0_CLK_PAR = 14;
      public final static int AF1_CLK_PAR = 15;
      public final static int CHECKSUM = 16;
    }

    public static class GPGRS_COL {
      public final static int HEADER = 0;
      public final static int UTC = 1;
      public final static int MODE = 2;
      public final static int RES1 = 3;
      public final static int RES2 = 4;
      public final static int RES3 = 5;
      public final static int RES4 = 6;
      public final static int RES5 = 7;
      public final static int RES6 = 8;
      public final static int RES7 = 9;
      public final static int RES8 = 10;
      public final static int RES9 = 11;
      public final static int RES10 = 12;
      public final static int RES11 = 13;
      public final static int RES12 = 14;
      public final static int CHECKSUM = 15;
    }

    public static class GPGST_COL {
      public final static int HEADER = 0;
      public final static int UTC = 1;
      public final static int RMS_STD = 2;
      public final static int SMJR_STD = 3;
      public final static int SMNR_STD = 4;
      public final static int ORIENT_SMJR = 5;
      public final static int LAT_STD = 6;
      public final static int LON_STD = 7;
      public final static int ALT_STD = 8;
      public final static int CHECKSUM = 9;
    }

    public static class GPGSV_COL {
      public final static int HEADER = 0;
      public final static int NUMBER_MSGS = 1;
      public final static int CURRENT_MSG = 2;
      public final static int NUMBER_SATS = 3;
      public final static int SAT1_PRN = 4;
      public final static int SAT1_ELEV = 5;
      public final static int SAT1_AZ = 6;
      public final static int SAT1_SNR = 7;
      public final static int SAT2_PRN = 8;
      public final static int SAT2_ELEV = 9;
      public final static int SAT2_AZ = 10;
      public final static int SAT2_SNR = 11;
      public final static int SAT3_PRN = 12;
      public final static int SAT3_ELEV = 13;
      public final static int SAT3_AZ = 14;
      public final static int SAT3_SNR = 15;
      public final static int SAT4_PRN = 16;
      public final static int SAT4_ELEV = 17;
      public final static int SAT4_AZ = 18;
      public final static int SAT4_SNR = 19;
      public final static int CHECKSUM = 20;
    }

    public static class GPRMB_COL {
      public final static int HEADER = 0;
      public final static int DATA_STATUS = 1;
      public final static int XTRACK_ERR = 2;
      public final static int DIR = 3;
      public final static int ORIGIN_ID = 4;
      public final static int DEST_ID = 5;
      public final static int DEST_LAT = 6;
      public final static int LAT_DIR = 7;
      public final static int DEST_LON = 8;
      public final static int LON_DIR = 9;
      public final static int RANGE = 10;
      public final static int BEARING = 11;
      public final static int VEL = 12;
      public final static int ARR_STATUS = 13;
      public final static int MODE_IND = 14;
      public final static int CHECKSUM = 15;
    }

    public static class GPHDT_COL {
      public final static int HEADER = 0;
      public final static int HEADING = 1;
      public final static int DEGREES_TRUE = 2;
      public final static int CHECKSUM = 3;
    }

    public static class GPRMC_COL {
      public final static int HEADER = 0;
      public final static int UTC = 1;
      public final static int POS_STATUS = 2;
      public final static int LAT = 3;
      public final static int LAT_DIR = 4;
      public final static int LON = 5;
      public final static int LON_DIR = 6;
      public final static int SPEED_KN = 7;
      public final static int TRACK_TRUE = 8;
      public final static int DATE = 9;
      public final static int MAG_VAR = 10;
      public final static int VAR_DIR = 11;
      public final static int MODE_IND = 12;
      public final static int CHECKSUM = 13;
    }

    public static class GPVTG_COL {
      public final static int HEADER = 0;
      public final static int TRACK_TRUE = 1;
      public final static int T_INDICATOR = 2;
      public final static int TRACK_GOOD_DEG_MAGNETIC = 3;
      public final static int MAGNETIC_TRACK = 4;
      public final static int SPEED_KN = 5;
      public final static int NAUTICAL_SPEED_IND = 6;
      public final static int SPEED_KMH = 7;
      public final static int SPEED_INDICATOR = 8;
      public final static int POS_MODE = 9;
      public final static int CHECKSUM = 10;
    }

    public static class GPZDA_COL {
      public final static int HEADER = 0;
      public final static int UTC = 1;
      public final static int DAY = 2;
      public final static int MONTH = 3;
      public final static int YEAR = 4;
      public final static int NULL1 = 5;
      public final static int NULL2 = 6;
      public final static int CHECKSUM = 7;
    }

    public static double DDMMpMMMM2degrees(String DDMMpMMMM) {
      if (DDMMpMMMM.length() == 9) {
        return Double.parseDouble(DDMMpMMMM.substring(0, 2))
            + (Double.parseDouble(DDMMpMMMM.substring(2, 4))
                + Double.parseDouble(DDMMpMMMM.substring(5, 9)) / 1000) / 60;
      } else {
        return 0;
      }
    }

    public static double DDDMMpMMMM2degrees(String DDMMpMMMM) {
      if (DDMMpMMMM.length() == 10) {
        // DDDMMpMMMM sentence
        return Double.parseDouble(DDMMpMMMM.substring(0, 3))
            + (Double.parseDouble(DDMMpMMMM.substring(3, 5))
                + Double.parseDouble(DDMMpMMMM.substring(6, 10)) / 1000) / 60;
      } else {
        return 0;
      }
    }

    public static double DDMMpMMMMMMM2degrees(String DDMMpMMMMMMM) {
      if (DDMMpMMMMMMM.length() == 12) {
        // System.out.println(DDMMpMMMMMMM.substring(0, 2));
        // System.out.println(DDMMpMMMMMMM.substring(2, 4));
        // System.out.println(DDMMpMMMMMMM.substring(5, 12));
        // System.out.println(Double.parseDouble(DDMMpMMMMMMM.substring(5, 12)) / 1000);
        return Double.parseDouble(DDMMpMMMMMMM.substring(0, 2))
            + (Double.parseDouble(DDMMpMMMMMMM.substring(2, 4))
                + Double.parseDouble(DDMMpMMMMMMM.substring(5, 12)) / 1000000) / 60;
      } else {
        return 0;
      }
    }

    public static double DDDMMpMMMMMMM2degrees(String DDDMMpMMMMMMM) {
      if (DDDMMpMMMMMMM.length() == 13) {
        return Double.parseDouble(DDDMMpMMMMMMM.substring(0, 3))
            + (Double.parseDouble(DDDMMpMMMMMMM.substring(3, 5))
                + Double.parseDouble(DDDMMpMMMMMMM.substring(6, 13)) / 1000000) / 60;
      } else {
        return 0;
      }
    }

    public static String degrees2DDMMpMMMM(double decimal_degrees) {
      int degrees = (int) Math.floor(decimal_degrees);
      double decimal_minutes = 60 * (decimal_degrees - degrees);
      int minutes = (int) Math.floor(decimal_minutes);
      int minutes_remainder = (int) Math.floor((decimal_minutes - minutes) * 1000);
      return String.format("%02d%02d.%04d", degrees, minutes, minutes_remainder);
    }

    public static String degrees2DDDMMpMMMM(double decimal_degrees) {
      int degrees = (int) Math.floor(decimal_degrees);
      double decimal_minutes = 60 * (decimal_degrees - degrees);
      int minutes = (int) Math.floor(decimal_minutes);
      int minutes_remainder = (int) Math.floor((decimal_minutes - minutes) * 1000);
      return String.format("%03d%02d.%04d", degrees, minutes, minutes_remainder);
    }

    public static String degrees2DDMMpMMMMMMM(double decimal_degrees) {
      // System.out.println("degrees2DDMMpMMMMMMM input ["+decimal_degrees+"]");
      int degrees = (int) Math.floor(decimal_degrees);
      double decimal_minutes = 60 * (decimal_degrees - degrees);
      int minutes = (int) Math.floor(decimal_minutes);
      long minutes_remainder = (long) Math.floor((decimal_minutes - minutes) * 1000000);
      return String.format("%02d%02d.%07d", degrees, minutes, minutes_remainder);
    }

    public static String degrees2DDDMMpMMMMMMM(double decimal_degrees) {
      int degrees = (int) Math.floor(decimal_degrees);
      double decimal_minutes = 60 * (decimal_degrees - degrees);
      int minutes = (int) Math.floor(decimal_minutes);
      long minutes_remainder = (long) Math.floor((decimal_minutes - minutes) * 1000000);
      return String.format("%03d%02d.%07d", degrees, minutes, minutes_remainder);
    }

  }

  public PGPS(SimulatorNode simulatorNode, String name) {
    super(simulatorNode, name);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @InternalData(internalID = 2001, commandIDs = { "", "" }, argNames = { "inputSentence" })
  public String getNMEASentence(String inputSentence) {
    ArrayList<Object> argObject = new ArrayList<>();
    argObject.add(inputSentence);
    return (String) super.getSimulatorNode().runGenericMethod(2001, argObject);
  }

  @Override
  @InternalData(internalID = 2002, commandIDs = { "", "" }, argNames = { "" })
  public String getLastKnownPosition() {
    ArrayList<Object> argObject = null;
    return (String) super.getSimulatorNode().runGenericMethod(2002, argObject);
  }

  @Override
  @InternalData(internalID = 2003, commandIDs = { "", "" }, argNames = { "" })
  public String getBestXYZSentence() {
    ArrayList<Object> argObject = null;
    return (String) super.getSimulatorNode().runGenericMethod(2003, argObject);
  }

  @Override
  @InternalData(internalID = 2004, commandIDs = { "", "" }, argNames = { "" })
  public String getTIMEASentence() {
    ArrayList<Object> argObject = null;
    return (String) super.getSimulatorNode().runGenericMethod(2004, argObject);
  }
}
