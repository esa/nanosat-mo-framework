/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
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
package esa.mo.nmf.apps;

import at.tugraz.ihf.opssat.iadcs.IADCS_100_Control_Error_Register;
import at.tugraz.ihf.opssat.iadcs.IADCS_100_Control_Status_Register;
import at.tugraz.ihf.opssat.iadcs.IADCS_100_System_Error_Register;
import at.tugraz.ihf.opssat.iadcs.IADCS_100_System_Livelyhood_Register;
import at.tugraz.ihf.opssat.iadcs.IADCS_100_System_Status_Register;
import at.tugraz.ihf.opssat.iadcs.SEPP_IADCS_API;
import at.tugraz.ihf.opssat.iadcs.SEPP_IADCS_API_INFO_TELEMETRY;
import at.tugraz.ihf.opssat.iadcs.SEPP_IADCS_API_POWER_STATUS_TELEMETRY;
import at.tugraz.ihf.opssat.iadcs.SEPP_IADCS_API_STANDARD_TELEMETRY;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 *
 */
public class HKTelemetry {

    private static final Logger LOGGER = Logger.getLogger(HKTelemetry.class.getName());

    public static void dumpHKTelemetry(final SEPP_IADCS_API adcsApi) {
        LOGGER.log(Level.INFO, "Dumping HK Telemetry...");

        if (adcsApi == null) {
            LOGGER.log(Level.WARNING, "The adcsApi object is null!");
            return;
        }

        System.out.println("---Stage 0---");

        try {
            adcsApi.Print_Info();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "(01) Error!");
            return;
        }

        System.out.printf("---Stage 1---");

        SEPP_IADCS_API_STANDARD_TELEMETRY stdTM;
        SEPP_IADCS_API_POWER_STATUS_TELEMETRY powerTM;
        SEPP_IADCS_API_INFO_TELEMETRY infoTM;

        try {
            stdTM = adcsApi.Get_Standard_Telemetry();
            powerTM = adcsApi.Get_Power_Status_Telemetry();
            infoTM = adcsApi.Get_Info_Telemetry();
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "(02) Error!");
            return;
        }

        System.out.println("---Stage 2---");

        IADCS_100_System_Status_Register status_register = stdTM.getSYSTEM_STATUS_REGISTER();

        if (status_register != null) {
            System.out.println("->status_register");
            System.out.println("status_register.getDETUMBLING_MODE() : " + HKTelemetry.boolean2string(status_register
                .getDETUMBLING_MODE()));
            System.out.println("status_register.getIDLE_MODE() : " + HKTelemetry.boolean2string(status_register
                .getIDLE_MODE()));
            System.out.println("status_register.getMEASUREMENT_MODE() : " + HKTelemetry.boolean2string(status_register
                .getMEASUREMENT_MODE()));
            System.out.println("status_register.getSAFE_MODE() : " + HKTelemetry.boolean2string(status_register
                .getSAFE_MODE()));
            System.out.println("status_register.getSINGLE_SPINNING_MODE() : " + HKTelemetry.boolean2string(
                status_register.getSINGLE_SPINNING_MODE()));
            System.out.println("status_register.getSUN_POINTING_MODE() : " + HKTelemetry.boolean2string(status_register
                .getSUN_POINTING_MODE()));
            System.out.println("status_register.getTARGET_POINTING_MODE() : " + HKTelemetry.boolean2string(
                status_register.getTARGET_POINTING_MODE()));
            System.out.println("status_register.getUNDEFINED_MODE() : " + HKTelemetry.boolean2string(status_register
                .getUNDEFINED_MODE()));
        } else {
            System.out.println("->status_register is null   x(");
        }

        IADCS_100_System_Error_Register error_register = stdTM.getSYSTEM_ERROR_REGISTER();

        if (error_register != null) {
            System.out.println("->error_register");
            System.out.println("error_register.getACS_UPDATE_ERROR() : " + HKTelemetry.boolean2string(error_register
                .getACS_UPDATE_ERROR()));
            System.out.println("error_register.getACTUATOR_INIT_ERROR() : " + HKTelemetry.boolean2string(error_register
                .getACTUATOR_INIT_ERROR()));
            System.out.println("error_register.getCONTROL_MODULE_ERROR() : " + HKTelemetry.boolean2string(error_register
                .getCONTROL_MODULE_ERROR()));
            System.out.println("error_register.getCRC_FAIL() : " + HKTelemetry.boolean2string(error_register
                .getCRC_FAIL()));
            System.out.println("error_register.getKALMAN_FILTER_MODULE_ERROR() : " + HKTelemetry.boolean2string(
                error_register.getKALMAN_FILTER_MODULE_ERROR()));
            System.out.println("error_register.getMAGNETOMETER_ERROR() : " + HKTelemetry.boolean2string(error_register
                .getMAGNETOMETER_ERROR()));
            System.out.println("error_register.getOPERATION_MODE_ERROR() : " + HKTelemetry.boolean2string(error_register
                .getOPERATION_MODE_ERROR()));
            System.out.println("error_register.getORBIT_PROPAGATION_ERROR() : " + HKTelemetry.boolean2string(
                error_register.getORBIT_PROPAGATION_ERROR()));
            System.out.println("error_register.getPOWER_READING_ERROR() : " + HKTelemetry.boolean2string(error_register
                .getPOWER_READING_ERROR()));
            System.out.println("error_register.getSENSOR_INIT_ERROR() : " + HKTelemetry.boolean2string(error_register
                .getSENSOR_INIT_ERROR()));
            System.out.println("error_register.getSTR_COMM_FAIL() : " + HKTelemetry.boolean2string(error_register
                .getSTR_COMM_FAIL()));
            System.out.println("error_register.getSTR_FAIL() : " + HKTelemetry.boolean2string(error_register
                .getSTR_FAIL()));
            System.out.println("error_register.getTIME_INIT_ERROR() : " + HKTelemetry.boolean2string(error_register
                .getTIME_INIT_ERROR()));
            System.out.println("error_register.getUNKNOWN_MASTER_COMMAND() : " + HKTelemetry.boolean2string(
                error_register.getUNKNOWN_MASTER_COMMAND()));
        } else {
            System.out.println("->error_register is null   x(");
        }

        IADCS_100_Control_Status_Register csr = stdTM.getCONTROL_MODULE_REGISTERS().getCSR();

        if (csr != null) {
            System.out.println("->csr");
            System.out.println("csr.getALL_AXIS() : " + HKTelemetry.boolean2string(csr.getALL_AXIS()));
            System.out.println("csr.getCONTROL_MODULE_ERROR() : " + HKTelemetry.boolean2string(csr
                .getCONTROL_MODULE_ERROR()));
            System.out.println("csr.getIDLE() : " + HKTelemetry.boolean2string(csr.getIDLE()));
            System.out.println("csr.getSINGLE_AXIS() : " + HKTelemetry.boolean2string(csr.getSINGLE_AXIS()));
        } else {
            System.out.println("->csr is null   x(");
        }

        IADCS_100_Control_Error_Register cer = stdTM.getCONTROL_MODULE_REGISTERS().getCER();

        if (cer != null) {
            System.out.println("->cer");
            System.out.println("cer.getAAC_BDOT_MAX_ERROR() : " + HKTelemetry.boolean2string(cer
                .getAAC_BDOT_MAX_ERROR()));
            System.out.println("cer.getAAC_BDOT_PROP_ERROR() : " + HKTelemetry.boolean2string(cer
                .getAAC_BDOT_PROP_ERROR()));
            System.out.println("cer.getAAC_SINGLE_SPINNING_MODE_ERROR() : " + HKTelemetry.boolean2string(cer
                .getAAC_SINGLE_SPINNING_MODE_ERROR()));
            System.out.println("cer.getAAC_SUN_POINTING_ERROR() : " + HKTelemetry.boolean2string(cer
                .getAAC_SUN_POINTING_ERROR()));
            // The rest we did not put here... ;)
        } else {
            System.out.println("->cer is null   x(");
        }

        IADCS_100_System_Livelyhood_Register livelyhood_register = stdTM.getLIVELYHOOD_REGISTER();

        if (cer != null) {
            System.out.println("->livelyhood_register");
            System.out.println("livelyhood_register.getADDITIONAL_POWER_SENSOR_ONE_ALIVE() : " + HKTelemetry
                .boolean2string(livelyhood_register.getADDITIONAL_POWER_SENSOR_ONE_ALIVE()));
            System.out.println("livelyhood_register.getADDITIONAL_POWER_SENSOR_TWO_ALIVE() : " + HKTelemetry
                .boolean2string(livelyhood_register.getADDITIONAL_POWER_SENSOR_TWO_ALIVE()));
            System.out.println("livelyhood_register.getCONTROL_PROCESSOR_ALIVE() : " + HKTelemetry.boolean2string(
                livelyhood_register.getCONTROL_PROCESSOR_ALIVE()));
            System.out.println("livelyhood_register.getEXTERNAL_MAGNETOMETER_ALIVE() : " + HKTelemetry.boolean2string(
                livelyhood_register.getEXTERNAL_MAGNETOMETER_ALIVE()));
            System.out.println("livelyhood_register.getINTERNAL_RW_CURRENT_SENSOR_ALIVE() : " + HKTelemetry
                .boolean2string(livelyhood_register.getINTERNAL_RW_CURRENT_SENSOR_ALIVE()));
            System.out.println("livelyhood_register.getMAIN_BOARD_CURRENT_SENSOR_ALIVE() : " + HKTelemetry
                .boolean2string(livelyhood_register.getMAIN_BOARD_CURRENT_SENSOR_ALIVE()));
            System.out.println("livelyhood_register.getMEMS_MAGNETOMETER_ALIVE() : " + HKTelemetry.boolean2string(
                livelyhood_register.getMEMS_MAGNETOMETER_ALIVE()));
            System.out.println("livelyhood_register.getMTQ_CURRENT_SENSOR_ALIVE() : " + HKTelemetry.boolean2string(
                livelyhood_register.getMTQ_CURRENT_SENSOR_ALIVE()));
            System.out.println("livelyhood_register.getSTAR_TRACKER_ALIVE() : " + HKTelemetry.boolean2string(
                livelyhood_register.getSTAR_TRACKER_ALIVE()));
            System.out.println("livelyhood_register.getSTAR_TRACKER_CURRENT_SENSOR_ALIVE() : " + HKTelemetry
                .boolean2string(livelyhood_register.getSTAR_TRACKER_CURRENT_SENSOR_ALIVE()));
        } else {
            System.out.println("->livelyhood_register is null   x(");
        }

        long time = stdTM.getEPOCH_TIME_MSEC();
        System.out.println("getEPOCH_TIME_MSEC() : " + time);
        /*        
            stdTM.getLIVELYHOOD_REGISTER(),
            stdTM.getEPOCH_TIME_MSEC())
         */

        LOGGER.log(Level.INFO, String.format("Standard TM:\n" + "IADCS_STATUS_REGISTER = %d\n" +
            "IADCS_ERROR_REGISTER = %d\n" + "CONTROL_STATUS_REGISTER = %d\n" + "CONTROL_ERROR_REGISTER = %d\n" +
            "LIVELYHOOD_REGISTER = %d\n" + "ELAPSED_SUBSECONDS_SINCE_EPOCH_MSEC = %d\n", stdTM
                .getSYSTEM_STATUS_REGISTER(), stdTM.getSYSTEM_ERROR_REGISTER(), stdTM.getCONTROL_MODULE_REGISTERS()
                    .getCSR(), stdTM.getCONTROL_MODULE_REGISTERS().getCER(), stdTM.getLIVELYHOOD_REGISTER(), stdTM
                        .getEPOCH_TIME_MSEC()));

        /* OLD:
        LOGGER.log(Level.INFO,
        String.format("Power TM:\n"
            + " MAGNETTORQUER_POWER_CONSUMPTION_W = %.3f\n"
            + " MAGNETTORQUER_SUPPLY_VOLTAGE_V = %.3f\n"
            + " STARTRACKER_CURRENT_CONSUMPTION_A = %.3f\n"
            + " STARTRACKER_POWER_CONSUMPTION_W = %.3f\n"
            + " STARTRACKER_SUPPLY_VOLTAGE_V = %.3f\n"
            + " IADCS_CURRENT_CONSUMPTION_A = %.3f\n"
            + " IADCS_POWER_CONSUMPTION_W = %.3f\n"
            + " IADCS_SUPPLY_VOLTAGE_V = %.3f\n"
            + " REACTIONWHEEL_CURRENT_CONSUMPTION_A = %.3f\n"
            + " REACTIONWHEEL_POWER_CONSUMPTION_W = %.3f\n"
            + " REACTIONWHEEL_SUPPLY_VOLTAGE_V = %.3f\n",
            powerTM.getMAGNETTORQUER_POWER_CONSUMPTION_W(),
            powerTM.getMAGNETTORQUER_SUPPLY_VOLTAGE_V(),
            powerTM.getSTARTRACKER_CURRENT_CONSUMPTION_A(),
            powerTM.getSTARTRACKER_POWER_CONSUMPTION_W(),
            powerTM.getSTARTRACKER_SUPPLY_VOLTAGE_V(),
            powerTM.getIADCS_CURRENT_CONSUMPTION_A(),
            powerTM.getIADCS_POWER_CONSUMPTION_W(),
            powerTM.getIADCS_SUPPLY_VOLTAGE_V(),
            powerTM.getREACTIONWHEEL_CURRENT_CONSUMPTION_A(),
            powerTM.getREACTIONWHEEL_POWER_CONSUMPTION_W(),
            powerTM.getREACTIONWHEEL_SUPPLY_VOLTAGE_V()));
         */
        LOGGER.log(Level.INFO, String.format("Power TM:\n" + " MAGNETTORQUER_POWER_CONSUMPTION_W = %.3f\n" +
            " MAGNETTORQUER_SUPPLY_VOLTAGE_V = %.3f\n" + " STARTRACKER_CURRENT_CONSUMPTION_A = %.3f\n" +
            " STARTRACKER_POWER_CONSUMPTION_W = %.3f\n" + " STARTRACKER_SUPPLY_VOLTAGE_V = %.3f\n" +
            " IADCS_CURRENT_CONSUMPTION_A = %.3f\n" + " IADCS_POWER_CONSUMPTION_W = %.3f\n" +
            " IADCS_SUPPLY_VOLTAGE_V = %.3f\n" + " REACTIONWHEEL_CURRENT_CONSUMPTION_A = %.3f\n" +
            " REACTIONWHEEL_POWER_CONSUMPTION_W = %.3f\n" + " REACTIONWHEEL_SUPPLY_VOLTAGE_V = %.3f\n", powerTM
                .getMAGNETTORQUER().getPOWER_CONSUMPTION(), powerTM.getMAGNETTORQUER().getVOLTAGE(), powerTM
                    .getSTARTRACKER().getCURRENT(), powerTM.getSTARTRACKER().getPOWER_CONSUMPTION(), powerTM
                        .getSTARTRACKER().getVOLTAGE(), powerTM.getIADCS().getCURRENT(), powerTM.getIADCS()
                            .getPOWER_CONSUMPTION(), powerTM.getIADCS().getVOLTAGE(), powerTM.getREACTIONWHEEL()
                                .getCURRENT(), powerTM.getREACTIONWHEEL().getPOWER_CONSUMPTION(), powerTM
                                    .getREACTIONWHEEL().getVOLTAGE()));

        /* OLD
        Logger.getLogger(AutonomousADCSOPSSATAdapter.class.getName()).log(Level.INFO,
        String.format("Info TM:\n"
            + "PRIMARY_TARGET_TYPE = %d\n"
            + "SECONDARY_TARGET_TYPE = %d\n"
            + "DEVICE_NAME = %s\n"
            + "DEVICE_MODEL_NAME = %s\n"
            + "SERIAL_NUMBER = %d\n"
            + "COMPILE_TIME = %s\n"
            + "SOFTWARE_VERSION = %s\n"
            + "DEBUG_LEVEL = %d\n"
            + "GIT_COMMIT_ID = %s\n"
            + "COMPILER = %s\n"
            + "COMPILER_VERSION = %s\n",
            infoTM.getPRIMARY_TARGET_TYPE(),
            infoTM.getSECONDARY_TARGET_TYPE(),
            infoTM.getDEVICE_NAME(),z2
            infoTM.getDEVICE_MODEL_NAME(),
            infoTM.getSERIAL_NUMBER(),
            infoTM.getCOMPILE_TIME(),
            infoTM.getSOFTWARE_VERSION(),
            infoTM.getDEBUG_LEVEL(),
            infoTM.getGIT_COMMIT_ID(),
            infoTM.getCOMPILER(),
            infoTM.getCOMPILER_VERSION()));
         */
        Logger.getLogger(HKTelemetry.class.getName()).log(Level.INFO, String.format("Info TM:\n" +
            "DEVICE_NAME = %s\n" + "SERIAL_NUMBER = %d\n" + "COMPILE_TIME = %s\n" + "SOFTWARE_VERSION = %s\n" +
            "DEBUG_LEVEL = %d\n" + "GIT_COMMIT_ID = %s\n" + "COMPILER = %s\n" + "COMPILER_VERSION = %s\n", infoTM
                .getDEVICE_NAME(), infoTM.getDEVICE_SERIAL_NUMBER(), infoTM.getBUILD_TIMESTAMP(), infoTM
                    .getSW_VERSION(), infoTM.getDEBUG_LEVEL(), infoTM.getSW_COMMIT_ID(), infoTM.getCOMPILER_NAME(),
            infoTM.getCOMPILER_VERSION()));

    }

    private static String boolean2string(boolean detumbling_mode) {
        return detumbling_mode ? "true" : "false";
    }

}
