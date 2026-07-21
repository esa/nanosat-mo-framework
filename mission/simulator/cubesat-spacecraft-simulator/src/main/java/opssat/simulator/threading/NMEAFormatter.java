/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
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
package opssat.simulator.threading;

import java.util.LinkedList;
import java.util.Locale;
import opssat.simulator.orekit.GPSSatInView;
import opssat.simulator.peripherals.PGPS;
import opssat.simulator.util.CommandResult;
import opssat.simulator.util.SimulatorSpacecraftState;

/**
 * Formats NMEA sentences for the GPS simulator command (internal ID 2001).
 */
class NMEAFormatter {

    private final SimulatorNode node;

    NMEAFormatter(SimulatorNode node) {
        this.node = node;
    }

    String format(String inputSentence, CommandResult commandResult) {
        String trimmed = inputSentence.trim();
        StringBuilder result = new StringBuilder();
        String separator = ",";

        if (trimmed.endsWith("GLMLA")) {
            String separatorNewLine = "\r\n";
            LinkedList<GPSSatInView> tempResult = node.getSatsInView();
            int numberInSet = tempResult.size();
            for (int iSat = 1; iSat <= numberInSet; iSat++) {
                for (int i = PGPS.FirmwareReferenceOEM16.GLMLA_COL.HEADER;
                        i <= PGPS.FirmwareReferenceOEM16.GLMLA_COL.CHECKSUM; i++) {
                    if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.HEADER) {
                        result.append("$GLMLA").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.NUMBER_IN_SET) {
                        result.append(numberInSet).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.NUMBER_CURRENT) {
                        result.append(iSat).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.SLOT) {
                        result.append(tempResult.get(iSat - 1).getName()).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CALDAY_LEAP_YEAR) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.HEALTH_FREQ) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.ECC) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.DELTAT_DOT) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.ARG_PER) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CLK_OFFSET) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.DELTA_T_DRACONIAN) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.GLONASSTIME_ASC_NODE_EQ) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.LONG_ASC_NODE_CROSS) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.DELTA_NOMINAL_I) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CLK_OFFSET_LSB12) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CLK_SHIFT_COARSE) {
                        result.append("0");
                    } else if (i == PGPS.FirmwareReferenceOEM16.GLMLA_COL.CHECKSUM) {
                        result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                    }
                }
                result.append(separatorNewLine);
            }

        } else if (trimmed.endsWith("GPGRS")) {
            for (int i = PGPS.FirmwareReferenceOEM16.GPGRS_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPGRS_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.HEADER) {
                    result.append("$GPGRS").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.UTC) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.MODE) {
                    result.append("0").append(separator);
                } else if (i >= PGPS.FirmwareReferenceOEM16.GPGRS_COL.RES1
                        && i <= PGPS.FirmwareReferenceOEM16.GPGRS_COL.RES12) {
                    result.append("0");
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGRS_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPGST")) {
            for (int i = PGPS.FirmwareReferenceOEM16.GPGST_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPGST_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.HEADER) {
                    result.append("$GPGST").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.UTC) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.RMS_STD) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.SMJR_STD) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.SMNR_STD) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.ORIENT_SMJR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.LAT_STD) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.LON_STD) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.ALT_STD) {
                    result.append("0");
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGST_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPGSV")) {
            LinkedList<GPSSatInView> tempResult = node.getSatsInView();
            int numberInSet = tempResult.size();
            int numberMessages = (int) Math.ceil((float) numberInSet / 4);
            int k = 0;
            for (int iSat = 1; iSat <= numberMessages; iSat++) {
                int[] tempPRN = new int[4];
                int[] tempElevation = new int[4];
                int[] tempAzimuth = new int[4];
                for (int j = 0; j < 4; j++) {
                    if (k < numberInSet) {
                        tempPRN[j] = tempResult.get(k).getPrn();
                        tempElevation[j] = (int) tempResult.get(k).getElevation();
                        tempAzimuth[j] = (int) tempResult.get(k++).getAzimuth();
                    }
                }
                for (int i = PGPS.FirmwareReferenceOEM16.GPGSV_COL.HEADER;
                        i <= PGPS.FirmwareReferenceOEM16.GPGSV_COL.CHECKSUM; i++) {
                    if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.HEADER) {
                        result.append("$GPGSV").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.NUMBER_MSGS) {
                        result.append(numberMessages).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.CURRENT_MSG) {
                        result.append(iSat).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.NUMBER_SATS) {
                        result.append(numberInSet).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT1_PRN) {
                        result.append(tempPRN[0]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT1_ELEV) {
                        result.append(tempElevation[0]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT1_AZ) {
                        result.append(tempAzimuth[0]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT1_SNR) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT2_PRN) {
                        result.append(tempPRN[1]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT2_ELEV) {
                        result.append(tempElevation[1]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT2_AZ) {
                        result.append(tempAzimuth[1]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT2_SNR) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT3_PRN) {
                        result.append(tempPRN[2]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT3_ELEV) {
                        result.append(tempElevation[2]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT3_AZ) {
                        result.append(tempAzimuth[2]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT3_SNR) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT4_PRN) {
                        result.append(tempPRN[3]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT4_ELEV) {
                        result.append(tempElevation[3]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT4_AZ) {
                        result.append(tempAzimuth[3]).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.SAT4_SNR) {
                        result.append("0");
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPGSV_COL.CHECKSUM) {
                        result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                    }
                }
                result.append("\n");
            }

        } else if (trimmed.endsWith("GPHDT")) {
            for (int i = PGPS.FirmwareReferenceOEM16.GPHDT_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPHDT_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPHDT_COL.HEADER) {
                    result.append("$GPHDT").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPHDT_COL.HEADING) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPHDT_COL.DEGREES_TRUE) {
                    result.append("T");
                } else if (i == PGPS.FirmwareReferenceOEM16.GPHDT_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPRMB")) {
            for (int i = PGPS.FirmwareReferenceOEM16.GPRMB_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPRMB_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.HEADER) {
                    result.append("$GPRMB").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DATA_STATUS) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.XTRACK_ERR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DIR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.ORIGIN_ID) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DEST_ID) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DEST_LAT) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.LAT_DIR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.DEST_LON) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.LON_DIR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.RANGE) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.BEARING) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.VEL) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.ARR_STATUS) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.MODE_IND) {
                    result.append("0");
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMB_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPRMC")) {
            for (int i = PGPS.FirmwareReferenceOEM16.GPRMC_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPRMC_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.HEADER) {
                    result.append("$GPRMC").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.UTC) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.POS_STATUS) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.LAT) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.LAT_DIR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.LON) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.LON_DIR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.SPEED_KN) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.TRACK_TRUE) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.DATE) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.MAG_VAR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.VAR_DIR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.MODE_IND) {
                    result.append("0");
                } else if (i == PGPS.FirmwareReferenceOEM16.GPRMC_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPVTG")) {
            for (int i = PGPS.FirmwareReferenceOEM16.GPVTG_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPVTG_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.HEADER) {
                    result.append("$GPVTG").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.TRACK_TRUE) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.T_INDICATOR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.TRACK_GOOD_DEG_MAGNETIC) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.MAGNETIC_TRACK) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.SPEED_KN) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.NAUTICAL_SPEED_IND) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.SPEED_KMH) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.SPEED_INDICATOR) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.POS_MODE) {
                    result.append("0");
                } else if (i == PGPS.FirmwareReferenceOEM16.GPVTG_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPZDA")) {
            for (int i = PGPS.FirmwareReferenceOEM16.GPZDA_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPZDA_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.HEADER) {
                    result.append("$GPZDA").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.UTC) {
                    result.append(node.simulatorData.getUTCCurrentTime()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.DAY) {
                    result.append(node.simulatorData.getCurrentDay()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.MONTH) {
                    result.append(node.simulatorData.getCurrentMonth()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.YEAR) {
                    result.append(node.simulatorData.getCurrentYear()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.NULL1) {
                    result.append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.NULL2) {
                    // empty field
                } else if (i == PGPS.FirmwareReferenceOEM16.GPZDA_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPALM")) {
            String separatorNewLine = "\r\n";
            LinkedList<GPSSatInView> tempResult = node.getSatsInView();
            int numberInSet = tempResult.size();
            for (int iSat = 1; iSat <= numberInSet; iSat++) {
                for (int i = PGPS.FirmwareReferenceOEM16.GPALM_COL.HEADER;
                        i <= PGPS.FirmwareReferenceOEM16.GPALM_COL.CHECKSUM; i++) {
                    if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.HEADER) {
                        result.append("$GPALM").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.NUMBER_MSG_LOG) {
                        result.append(numberInSet).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.NUMBER_CURRENT) {
                        result.append(iSat).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.PRN) {
                        result.append(tempResult.get(iSat - 1).getPrn()).append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.REF_WEEK_NO) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.SV_HEALTH) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.ECC) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.ALM_REF_TIME) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.INC_ANGLE) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.OMEGA_DOT) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.RT_AXIS) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.OMEGA) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.LONG_ASC_NODE) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.MO_MEAN_ANOMALY) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.AF0_CLK_PAR) {
                        result.append("0").append(separator);
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.AF1_CLK_PAR) {
                        result.append("0");
                    } else if (i == PGPS.FirmwareReferenceOEM16.GPALM_COL.CHECKSUM) {
                        result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                    }
                }
                result.append(separatorNewLine);
            }

        } else if (trimmed.endsWith("GPGGA")) {
            SimulatorSpacecraftState state = node.getSpacecraftState();
            for (int i = PGPS.FirmwareReferenceOEM16.GPGGA_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPGGA_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.HEADER) {
                    result.append("$GPGGA").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.UTC) {
                    result.append(node.simulatorData.getUTCCurrentTime()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.LAT) {
                    result.append(PGPS.FirmwareReferenceOEM16.degrees2DDMMpMMMM(
                            Math.abs(state.getLatitude()))).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.LAT_DIR) {
                    result.append(state.getLatitude() >= 0 ? "N" : "S").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.LONG) {
                    result.append(PGPS.FirmwareReferenceOEM16.degrees2DDDMMpMMMM(
                            Math.abs(state.getLongitude()))).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.LONG_DIR) {
                    result.append(state.getLongitude() >= 0 ? "E" : "W").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.QUAL) {
                    result.append("1").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.SATS_IN_USE) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.HDOP) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.ALTITUDE) {
                    result.append(String.format(Locale.ROOT, "%.2f", state.getAltitude())).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.ALTITUDE_UNITS) {
                    result.append("M").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.UNDULATION) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.UNDULATION_UNITS) {
                    result.append("M").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.AGE_CORR_DATA) {
                    result.append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.DIFF_BASESID) {
                    // empty field
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGA_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPGGALONG")) {
            SimulatorSpacecraftState state = node.getSpacecraftState();
            for (int i = PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.HEADER) {
                    result.append("$GPGGALONG").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.UTC) {
                    result.append(node.simulatorData.getUTCCurrentTime()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.LAT) {
                    result.append(PGPS.FirmwareReferenceOEM16.degrees2DDMMpMMMMMMM(
                            Math.abs(state.getLatitude()))).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.LAT_DIR) {
                    result.append(state.getLatitude() >= 0 ? "N" : "S").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.LONG) {
                    result.append(PGPS.FirmwareReferenceOEM16.degrees2DDDMMpMMMMMMM(
                            Math.abs(state.getLongitude()))).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.LONG_DIR) {
                    result.append(state.getLongitude() >= 0 ? "E" : "W").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.QUAL) {
                    result.append("1").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.SATS_IN_USE) {
                    result.append(state.getSatsInView()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.HDOP) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.ALTITUDE) {
                    result.append(String.format(Locale.ROOT, "%.3f", state.getAltitude())).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.ALTITUDE_UNITS) {
                    result.append("M").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.UNDULATION) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.UNDULATION_UNITS) {
                    result.append("M").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.AGE_CORR_DATA) {
                    result.append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.DIFF_BASESID) {
                    // empty field
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGALONG_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else if (trimmed.endsWith("GPGGARTK")) {
            SimulatorSpacecraftState state = node.getSpacecraftState();
            for (int i = PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.HEADER;
                    i <= PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.CHECKSUM; i++) {
                if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.HEADER) {
                    result.append("$GPGGARTK").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.UTC) {
                    result.append(node.simulatorData.getUTCCurrentTime()).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.LAT) {
                    result.append(PGPS.FirmwareReferenceOEM16.degrees2DDMMpMMMMMMM(
                            Math.abs(state.getLatitude()))).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.LAT_DIR) {
                    result.append(state.getLatitude() >= 0 ? "N" : "S").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.LONG) {
                    result.append(PGPS.FirmwareReferenceOEM16.degrees2DDDMMpMMMMMMM(
                            Math.abs(state.getLongitude()))).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.LONG_DIR) {
                    result.append(state.getLongitude() >= 0 ? "E" : "W").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.QUAL) {
                    result.append("1").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.SATS_IN_USE) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.HDOP) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.ALTITUDE) {
                    result.append(String.format(Locale.ROOT, "%.3f", state.getAltitude())).append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.ALTITUDE_UNITS) {
                    result.append("M").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.NULL1) {
                    result.append("0").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.NULL2) {
                    result.append("M").append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.AGE_DIFF_DATA) {
                    result.append(separator);
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.DIFF_BASESID) {
                    // empty field
                } else if (i == PGPS.FirmwareReferenceOEM16.GPGGARTK_COL.CHECKSUM) {
                    result.append(SimulatorNode.calcNMEAChecksum(result.toString()));
                }
            }

        } else {
            commandResult.setCommandFailed(true);
            return "Sentence identifier [" + trimmed + "] unknown";
        }

        return result.toString();
    }
}
