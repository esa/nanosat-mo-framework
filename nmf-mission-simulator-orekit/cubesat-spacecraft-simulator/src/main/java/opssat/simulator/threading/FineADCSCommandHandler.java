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

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import opssat.simulator.orekit.OrekitCore;
import opssat.simulator.peripherals.PFineADCS.FWRefFineADCS;
import opssat.simulator.tcp.TCPServerReceiveOnly;
import opssat.simulator.util.CommandResult;
import opssat.simulator.util.SimulatorSpacecraftState;
import static opssat.simulator.threading.SimulatorNode.DevDatPBind;

/**
 * Handles FineADCS commands (internal IDs 1001–1204) on behalf of SimulatorNode.
 */
class FineADCSCommandHandler {

    private final SimulatorNode node;

    FineADCSCommandHandler(SimulatorNode node) {
        this.node = node;
    }

    Object handle(int commandId, ArrayList<Object> argObject, CommandResult commandResult) throws Exception {
        Object globalResult = null;
        switch (commandId) {

            case 1001: {// Origin [IFineADCS] Method [byte[] runRawCommand(int cmdID,byte[] data,int
                // iAD);//1001//Low level command to interact with FineADCS]
                int cmdID = (Integer) argObject.get(0);
                byte[] data = (byte[]) argObject.get(1);
                int iAD = (Integer) argObject.get(2);
                globalResult = new byte[0];
                break;
            }
            case 1002: {// Origin [IFineADCS] Method [byte[] Identify();//1002//High level command to
                // interact with FineADCS]
                globalResult = new byte[8];
                break;
            }
            case 1003: {// Origin [IFineADCS] Method [void SoftwareReset();//1003//High level command to
                // interact with FineADCS]
                break;
            }
            case 1004: {// Origin [IFineADCS] Method [void I2CReset();//1004//High level command to
                // interact with FineADCS]
                break;
            }
            case 1005: {// Origin [IFineADCS] Method [void SetDateTime(long seconds,int
                // subseconds);//1005//High level command to interact with FineADCS]
                long seconds = (Long) argObject.get(0);
                int subseconds = (Integer) argObject.get(1);
                break;
            }
            case 1006: {// Origin [IFineADCS] Method [byte[] GetDateTime();//1006//High level command to
                // interact with FineADCS]
                globalResult = new byte[6];
                break;
            }
            case 1007: {// Origin [IFineADCS] Method [void iADCSPowerCycle(byte onoff,byte
                // register);//1007//High level command to interact with FineADCS]
                byte onoff = (Byte) argObject.get(0);
                byte register = (Byte) argObject.get(1);
                break;
            }
            case 1008: {// Origin [IFineADCS] Method [void SetOperationMode(byte opmode);//1008//High
                // level command to interact with FineADCS]
                byte opmode = (Byte) argObject.get(0);
                break;
            }
            case 1009: {// Origin [IFineADCS] Method [void SetPowerUpdateInterval(long
                // miliseconds);//1009//High level command to interact with FineADCS]
                long miliseconds = (Long) argObject.get(0);
                break;
            }
            case 1010: {// Origin [IFineADCS] Method [void SetTemperatureUpdateInterval(long
                // miliseconds);//1010//High level command to interact with FineADCS]
                long miliseconds = (Long) argObject.get(0);
                break;
            }
            case 1011: {// Origin [IFineADCS] Method [byte[] GetStandardTelemetry();//1011//High level
                // command to interact with FineADCS]
                globalResult = new byte[60];
                break;
            }
            case 1012: {// Origin [IFineADCS] Method [byte[] GetExtendedTelemetry();//1012//High level
                // command to interact with FineADCS]
                globalResult = new byte[183];
                break;
            }
            case 1013: {// Origin [IFineADCS] Method [byte[] GetPowerStatus();//1013//High level command
                // to interact with FineADCS]
                globalResult = new byte[24];
                break;
            }
            case 1014: {// Origin [IFineADCS] Method [byte[] GetInfoTelemetry();//1014//High level
                // command to interact with FineADCS]
                globalResult = new byte[158];
                break;
            }
            case 1015: {// Origin [IFineADCS] Method [byte[] GetSensorTelemetry();//1015//High level
                // command to interact with FineADCS]
                byte[] result = new byte[74];
                SimulatorSpacecraftState spacecraftState = node.getSpacecraftState();
                float[] magneticField = spacecraftState.getMagnetometer();
                FWRefFineADCS.putFloatInByteArray(magneticField[0], FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_X, result);
                FWRefFineADCS.putFloatInByteArray(magneticField[1], FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Y, result);
                FWRefFineADCS.putFloatInByteArray(magneticField[2], FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Z, result);

                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Accelerometer)
                        .getTypeAsFloatByIndex(0), FWRefFineADCS.SENSORTM_IDX.ACCELEROMETER_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Accelerometer)
                        .getTypeAsFloatByIndex(1), FWRefFineADCS.SENSORTM_IDX.ACCELEROMETER_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Accelerometer)
                        .getTypeAsFloatByIndex(2), FWRefFineADCS.SENSORTM_IDX.ACCELEROMETER_Z, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Gyro1)
                        .getTypeAsFloatByIndex(0), FWRefFineADCS.SENSORTM_IDX.GYRO1_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Gyro1)
                        .getTypeAsFloatByIndex(1), FWRefFineADCS.SENSORTM_IDX.GYRO1_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Gyro1)
                        .getTypeAsFloatByIndex(2), FWRefFineADCS.SENSORTM_IDX.GYRO1_Z, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Gyro2)
                        .getTypeAsFloatByIndex(0), FWRefFineADCS.SENSORTM_IDX.GYRO2_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Gyro2)
                        .getTypeAsFloatByIndex(1), FWRefFineADCS.SENSORTM_IDX.GYRO2_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Gyro2)
                        .getTypeAsFloatByIndex(2), FWRefFineADCS.SENSORTM_IDX.GYRO2_Z, result);
                float[] quaternions = spacecraftState.getQ();
                FWRefFineADCS.putFloatInByteArray(quaternions[0], FWRefFineADCS.SENSORTM_IDX.QUATERNION1, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[1], FWRefFineADCS.SENSORTM_IDX.QUATERNION2, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[2], FWRefFineADCS.SENSORTM_IDX.QUATERNION3, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[3], FWRefFineADCS.SENSORTM_IDX.QUATERNION4, result);
                globalResult = result;
                break;
            }
            case 1016: {// Origin [IFineADCS] Method [byte[] GetActuatorTelemetry();//1016//High level
                // command to interact with FineADCS]
                byte[] result = new byte[21];
                FWRefFineADCS.putInt16InByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(0), FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_X, result);
                FWRefFineADCS.putInt16InByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(1), FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_Y, result);
                FWRefFineADCS.putInt16InByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(2), FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_Z, result);
                FWRefFineADCS.putInt16InByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer)
                        .getTypeAsIntByIndex(0), FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_X, result);
                FWRefFineADCS.putInt16InByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer)
                        .getTypeAsIntByIndex(1), FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_Y, result);
                FWRefFineADCS.putInt16InByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer)
                        .getTypeAsIntByIndex(2), FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_Z, result);
                globalResult = result;
                break;
            }
            case 1017: {// Origin [IFineADCS] Method [byte[] GetAttitudeTelemetry();//1017//High level
                // command to interact with FineADCS]
                byte[] result = new byte[28];
                byte pointingLoopState = node.orekitCore.getStateTarget();
                FWRefFineADCS.putByteInByteArray(pointingLoopState,
                        FWRefFineADCS.POINTING_LOOP_IDX.POINTING_LOOP_STATE, result);
                globalResult = result;
                break;
            }
            case 1018: {// Origin [IFineADCS] Method [byte[] GetExtendedSensorTelemetry();//1018//High
                // level command to interact with FineADCS]
                globalResult = new byte[154];
                break;
            }
            case 1019: {// Origin [IFineADCS] Method [byte[] GetExtendedActuatorTelemetry();//1019//High
                // level command to interact with FineADCS]
                globalResult = new byte[53];
                break;
            }
            case 1020: {// Origin [IFineADCS] Method [byte[] GetExtendedAttitudeTelemetry();//1020//High
                // level command to interact with FineADCS]
                globalResult = new byte[36];
                break;
            }
            case 1021: {// Origin [IFineADCS] Method [byte[] GetMagneticTelemetry();//1021//High level
                // command to interact with FineADCS]
                globalResult = new byte[32];
                break;
            }
            case 1022: {// Origin [IFineADCS] Method [byte[] GetSunTelemetry();//1022//High level
                // command to interact with FineADCS]
                globalResult = new byte[13];
                break;
            }
            case 1023: {// Origin [IFineADCS] Method [void SetThresholdValueForMagEmulation(int
                // value);//1023//High level command to interact with FineADCS]
                int value = (Integer) argObject.get(0);
                break;
            }
            case 1024: {// Origin [IFineADCS] Method [byte[]
                // GetThresholdValueForMagEmulation();//1024//High level command to interact
                // with FineADCS]
                globalResult = new byte[4];
                break;
            }
            case 1025: {// Origin [IFineADCS] Method [void ClearErrorRegister();//1025//High level
                // command to interact with FineADCS]
                break;
            }
            case 1026: {// Origin [IFineADCS] Method [byte[] GetSystemRegisters(byte
                // register);//1026//High level command to interact with FineADCS]
                byte register = (Byte) argObject.get(0);
                globalResult = new byte[4];
                break;
            }
            case 1027: {// Origin [IFineADCS] Method [byte[] GetControlRegisters(byte
                // register);//1027//High level command to interact with FineADCS]
                byte register = (Byte) argObject.get(0);
                globalResult = new byte[4];
                break;
            }
            case 1028: {// Origin [IFineADCS] Method [void SetSystemRegister(byte[] data);//1028//High
                // level command to interact with FineADCS]
                byte[] data = (byte[]) argObject.get(0);
                break;
            }
            case 1029: {// Origin [IFineADCS] Method [void ResetSystemRegister(byte[] data);//1029//High
                // level command to interact with FineADCS]
                byte[] data = (byte[]) argObject.get(0);
                break;
            }
            case 1030: {// Origin [IFineADCS] Method [void SetMemberUpdateInterval(byte memberID,long
                // interval);//1030//High level command to interact with FineADCS]
                byte memberID = (Byte) argObject.get(0);
                long interval = (Long) argObject.get(1);
                break;
            }
            case 1031: {// Origin [IFineADCS] Method [byte[] GetMemberUpdateInterval(byte
                // memberID);//1031//High level command to interact with FineADCS]
                byte memberID = (Byte) argObject.get(0);
                globalResult = new byte[8];
                break;
            }
            case 1032: {// Origin [IFineADCS] Method [void SetHILStatus(byte[]
                // HILStatusRegister);//1032//High level command to interact with FineADCS]
                byte[] HILStatusRegister = (byte[]) argObject.get(0);
                break;
            }
            case 1033: {// Origin [IFineADCS] Method [byte[] GetHILStatus();//1033//High level command
                // to interact with FineADCS]
                globalResult = new byte[4];
                break;
            }
            case 1034: {// Origin [IFineADCS] Method [void SetUpdateInterval(int interval);//1034//High
                // level command to interact with FineADCS]
                int interval = (Integer) argObject.get(0);
                break;
            }
            case 1035: {// Origin [IFineADCS] Method [void SetValuesToAllSensors(int[]
                // values);//1035//High level command to interact with FineADCS]
                int[] values = (int[]) argObject.get(0);
                break;
            }
            case 1036: {// Origin [IFineADCS] Method [byte[] GetValuesAllSensors();//1036//High level
                // command to interact with FineADCS]
                globalResult = new byte[96];
                break;
            }
            case 1037: {// Origin [IFineADCS] Method [void SetCalibrationParametersAllSensors(int[]
                // values);//1037//High level command to interact with FineADCS]
                int[] values = (int[]) argObject.get(0);
                break;
            }
            case 1038: {// Origin [IFineADCS] Method [byte[]
                // GetCalibrationParametersAllSensors();//1038//High level command to interact
                // with FineADCS]
                globalResult = new byte[48];
                break;
            }
            case 1039: {// Origin [IFineADCS] Method [void EnableCalibrationAllSensors();//1039//High
                // level command to interact with FineADCS]
                break;
            }
            case 1040: {// Origin [IFineADCS] Method [void DisableCalibrationAllSensors();//1040//High
                // level command to interact with FineADCS]
                break;
            }
            case 1041: {// Origin [IFineADCS] Method [void SetUpdateIntervalRW(int
                // interval);//1041//High level command to interact with FineADCS]
                int interval = (Integer) argObject.get(0);
                break;
            }
            case 1042: {// Origin [IFineADCS] Method [void SetSpeedToAllRWs(int[] values);//1042//High
                // level command to interact with FineADCS]
                int[] values = (int[]) argObject.get(0);
                break;
            }
            case 1043: {// Origin [IFineADCS] Method [byte[] GetSpeedAllRWs();//1043//High level command
                // to interact with FineADCS]
                globalResult = new byte[6];
                break;
            }
            case 1044: {// Origin [IFineADCS] Method [byte[] SetAccAllRWs(int[] values);//1044//High
                // level command to interact with FineADCS]
                int[] values = (int[]) argObject.get(0);
                globalResult = new byte[6];
                break;
            }
            case 1045: {// Origin [IFineADCS] Method [void SetSleepAllRWs(byte sleepMode);//1045//High
                // level command to interact with FineADCS]
                byte sleepMode = (Byte) argObject.get(0);
                break;
            }
            case 1046: {// Origin [IFineADCS] Method [void SetDipoleMomentAllMTQs(int[]
                // values);//1046//High level command to interact with FineADCS]
                int[] values = (int[]) argObject.get(0);
                break;
            }
            case 1047: {// Origin [IFineADCS] Method [byte[] GetDipoleMomentAllMTQs();//1047//High level
                // command to interact with FineADCS]
                globalResult = new byte[6];
                break;
            }
            case 1048: {// Origin [IFineADCS] Method [void SuspendAllMTQs();//1048//High level command
                // to interact with FineADCS]
                break;
            }
            case 1049: {// Origin [IFineADCS] Method [void ResumeAllMTQs();//1049//High level command to
                // interact with FineADCS]
                break;
            }
            case 1050: {// Origin [IFineADCS] Method [void ResetAllMTQs();//1050//High level command to
                // interact with FineADCS]
                break;
            }
            case 1051: {// Origin [IFineADCS] Method [void RunSelftTestAllMTQs();//1051//High level
                // command to interact with FineADCS]
                break;
            }
            case 1052: {// Origin [IFineADCS] Method [void SetMTQRelaxTime(int relaxtime);//1052//High
                // level command to interact with FineADCS]
                int relaxtime = (Integer) argObject.get(0);
                break;
            }
            case 1053: {// Origin [IFineADCS] Method [void StopAllMTQ();//1053//High level command to
                // interact with FineADCS]
                break;
            }
            case 1054: {// Origin [IFineADCS] Method [void MTQXSetDipoleMoment(int
                // dipoleValue);//1054//High level command to interact with FineADCS]
                int dipoleValue = (Integer) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).setIntTypeByIndex(dipoleValue, 0);
                break;
            }
            case 1055: {// Origin [IFineADCS] Method [byte[] MTQXGetDipoleMoment();//1055//High level
                // command to interact with FineADCS]
                byte[] result = new byte[2];
                result = FWRefFineADCS.int16_2ByteArray(
                        node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).getTypeAsIntByIndex(0));
                globalResult = result;
                break;
            }
            case 1056: {// Origin [IFineADCS] Method [void MTQXSuspend();//1056//High level command to
                // interact with FineADCS]
                break;
            }
            case 1057: {// Origin [IFineADCS] Method [void MTQXResume();//1057//High level command to
                // interact with FineADCS]
                break;
            }
            case 1058: {// Origin [IFineADCS] Method [void MTQXRunSelfTest();//1058//High level command
                // to interact with FineADCS]
                break;
            }
            case 1059: {// Origin [IFineADCS] Method [void MTQXReset();//1059//High level command to
                // interact with FineADCS]
                break;
            }
            case 1060: {// Origin [IFineADCS] Method [void MTQXStop();//1060//High level command to
                // interact with FineADCS]
                break;
            }
            case 1061: {// Origin [IFineADCS] Method [void MTQYSetDipoleMoment(int
                // dipoleValue);//1061//High level command to interact with FineADCS]
                int dipoleValue = (Integer) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).setIntTypeByIndex(dipoleValue, 1);
                break;
            }
            case 1062: {// Origin [IFineADCS] Method [byte[] MTQYGetDipoleMoment();//1062//High level
                // command to interact with FineADCS]
                globalResult = new byte[2];
                break;
            }
            case 1063: {// Origin [IFineADCS] Method [void MTQYSuspend();//1063//High level command to
                // interact with FineADCS]
                break;
            }
            case 1064: {// Origin [IFineADCS] Method [void MTQYResume();//1064//High level command to
                // interact with FineADCS]
                break;
            }
            case 1065: {// Origin [IFineADCS] Method [void MTQYRunSelfTest();//1065//High level command
                // to interact with FineADCS]
                break;
            }
            case 1066: {// Origin [IFineADCS] Method [void MTQYReset();//1066//High level command to
                // interact with FineADCS]
                break;
            }
            case 1067: {// Origin [IFineADCS] Method [void MTQYStop();//1067//High level command to
                // interact with FineADCS]
                break;
            }
            case 1068: {// Origin [IFineADCS] Method [void MTQZSetDipoleMoment(int
                // dipoleValue);//1068//High level command to interact with FineADCS]
                int dipoleValue = (Integer) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer).setIntTypeByIndex(dipoleValue, 2);
                break;
            }
            case 1069: {// Origin [IFineADCS] Method [byte[] MTQZGetDipoleMoment();//1069//High level
                // command to interact with FineADCS]
                globalResult = new byte[2];
                break;
            }
            case 1070: {// Origin [IFineADCS] Method [void MTQZSuspend();//1070//High level command to
                // interact with FineADCS]
                break;
            }
            case 1071: {// Origin [IFineADCS] Method [void MTQZResume();//1071//High level command to
                // interact with FineADCS]
                break;
            }
            case 1072: {// Origin [IFineADCS] Method [void MTQZRunSelfTest();//1072//High level command
                // to interact with FineADCS]
                break;
            }
            case 1073: {// Origin [IFineADCS] Method [void MTQZReset();//1073//High level command to
                // interact with FineADCS]
                break;
            }
            case 1074: {// Origin [IFineADCS] Method [void MTQZStop();//1074//High level command to
                // interact with FineADCS]
                break;
            }
            case 1075: {// Origin [IFineADCS] Method [void SetRWXSpeed(int speedValue);//1075//High
                // level command to interact with FineADCS]
                int speedValue = (Integer) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).setIntTypeByIndex(speedValue, 0);
                break;
            }
            case 1076: {// Origin [IFineADCS] Method [byte[] GetRWXSpeed();//1076//High level command to
                // interact with FineADCS]
                globalResult = new byte[2];
                break;
            }
            case 1077: {// Origin [IFineADCS] Method [void SetRWXAcceleration(int
                // accelerationValue);//1077//High level command to interact with FineADCS]
                int accelerationValue = (Integer) argObject.get(0);
                break;
            }
            case 1078: {// Origin [IFineADCS] Method [void SetRWXSleep(byte sleepMode);//1078//High
                // level command to interact with FineADCS]
                byte sleepMode = (Byte) argObject.get(0);
                break;
            }
            case 1079: {// Origin [IFineADCS] Method [byte[] GetRWXID();//1079//High level command to
                // interact with FineADCS]
                globalResult = new byte[4];
                break;
            }
            case 1080: {// Origin [IFineADCS] Method [void SetRWYSpeed(int speedValue);//1080//High
                // level command to interact with FineADCS]
                int speedValue = (Integer) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).setIntTypeByIndex(speedValue, 1);
                break;
            }
            case 1081: {// Origin [IFineADCS] Method [byte[] GetRWYSpeed();//1081//High level command to
                // interact with FineADCS]
                globalResult = new byte[2];
                break;
            }
            case 1082: {// Origin [IFineADCS] Method [void SetRWYAcceleration(int
                // accelerationValue);//1082//High level command to interact with FineADCS]
                int accelerationValue = (Integer) argObject.get(0);
                break;
            }
            case 1083: {// Origin [IFineADCS] Method [void SetRWYSleep(byte sleepMode);//1083//High
                // level command to interact with FineADCS]
                byte sleepMode = (Byte) argObject.get(0);
                break;
            }
            case 1084: {// Origin [IFineADCS] Method [byte[] GetRWYID();//1084//High level command to
                // interact with FineADCS]
                globalResult = new byte[4];
                break;
            }
            case 1085: {// Origin [IFineADCS] Method [void SetRWZSpeed(int speedValue);//1085//High
                // level command to interact with FineADCS]
                int speedValue = (Integer) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels).setIntTypeByIndex(speedValue, 2);
                break;
            }
            case 1086: {// Origin [IFineADCS] Method [byte[] GetRWZSpeed();//1086//High level command to
                // interact with FineADCS]
                globalResult = new byte[2];
                break;
            }
            case 1087: {// Origin [IFineADCS] Method [void SetRWZAcceleration(int
                // accelerationValue);//1087//High level command to interact with FineADCS]
                int accelerationValue = (Integer) argObject.get(0);
                break;
            }
            case 1088: {// Origin [IFineADCS] Method [void SetRWZSleep(byte sleepMode);//1088//High
                // level command to interact with FineADCS]
                byte sleepMode = (Byte) argObject.get(0);
                break;
            }
            case 1089: {// Origin [IFineADCS] Method [byte[] GetRWZID();//1089//High level command to
                // interact with FineADCS]
                globalResult = new byte[4];
                break;
            }
            case 1090: {// Origin [IFineADCS] Method [void ST200SetQuaternion(int[] values);//1090//High
                // level command to interact with FineADCS]
                int[] values = (int[]) argObject.get(0);
                break;
            }
            case 1091: {// Origin [IFineADCS] Method [void ST200UpdateInterval(long
                // interval);//1091//High level command to interact with FineADCS]
                long interval = (Long) argObject.get(0);
                break;
            }
            case 1092: {// Origin [IFineADCS] Method [void SunSensor1SetValue(int[] values);//1092//High
                // level command to interact with FineADCS]
                int[] values = (int[]) argObject.get(0);
                break;
            }
            case 1093: {// Origin [IFineADCS] Method [byte[] SunSensor1GetValue();//1093//High level
                // command to interact with FineADCS]
                globalResult = new byte[16];
                break;
            }
            case 1094: {// Origin [IFineADCS] Method [void SunSensor1SetValueQuaternion(int[]
                // values);//1094//High level command to interact with FineADCS]
                int[] values = (int[]) argObject.get(0);
                break;
            }
            case 1095: {// Origin [IFineADCS] Method [byte[] SunSensor1GetValueQuaternion();//1095//High
                // level command to interact with FineADCS]
                globalResult = new byte[16];
                break;
            }
            case 1096: {// Origin [IFineADCS] Method [void Gyro1SetRate(float[] values);//1096//High
                // level command to interact with FineADCS]
                float[] values = (float[]) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_Gyro1).setType(values);
                node.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum).setType(values);
                break;
            }
            case 1097: {// Origin [IFineADCS] Method [byte[] Gyro1GetRate();//1097//High level command
                // to interact with FineADCS]
                globalResult = new byte[20];
                break;
            }
            case 1098: {// Origin [IFineADCS] Method [void Gyro1SetUpdateInterval(int
                // updateRate);//1098//High level command to interact with FineADCS]
                int updateRate = (Integer) argObject.get(0);
                break;
            }
            case 1099: {// Origin [IFineADCS] Method [void Gyro1RemoveBias();//1099//High level command
                // to interact with FineADCS]
                break;
            }
            case 1100: {// Origin [IFineADCS] Method [byte[] Gyro1GetBias();//1100//High level command
                // to interact with FineADCS]
                globalResult = new byte[4];
                break;
            }
            case 1101: {// Origin [IFineADCS] Method [void Gyro1SetFilter1(byte updateRate,int
                // allowedDeviation);//1101//High level command to interact with FineADCS]
                byte updateRate = (Byte) argObject.get(0);
                int allowedDeviation = (Integer) argObject.get(1);
                break;
            }
            case 1102: {// Origin [IFineADCS] Method [void Gyro1SetCalibrationParameters(int[]
                // calibrationValues);//1102//High level command to interact with FineADCS]
                int[] calibrationValues = (int[]) argObject.get(0);
                break;
            }
            case 1103: {// Origin [IFineADCS] Method [byte[]
                // Gyro1GetCalibrationParameters();//1103//High level command to interact with
                // FineADCS]
                globalResult = new byte[48];
                break;
            }
            case 1104: {// Origin [IFineADCS] Method [void Gyro1EnableCalibration();//1104//High level
                // command to interact with FineADCS]
                break;
            }
            case 1105: {// Origin [IFineADCS] Method [void Gyro1DisableCalibration();//1105//High level
                // command to interact with FineADCS]
                break;
            }
            case 1106: {// Origin [IFineADCS] Method [void Gyro1SetQuaternionFromSunSensor(float[]
                // quaternionValues);//1106//High level command to interact with FineADCS]
                float[] quaternionValues = (float[]) argObject.get(0);
                break;
            }
            case 1107: {// Origin [IFineADCS] Method [byte[]
                // Gyro1GetQuaternionFromSunSensor();//1107//High level command to interact with
                // FineADCS]
                globalResult = new byte[16];
                break;
            }
            case 1108: {// Origin [IFineADCS] Method [void accelerometerSetValues(float[]
                // values);//1108//High level command to interact with FineADCS]
                float[] values = (float[]) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_Accelerometer).setType(values);
                break;
            }
            case 1109: {// Origin [IFineADCS] Method [byte[] accelerometerGetValues();//1109//High level
                // command to interact with FineADCS]
                globalResult = new byte[12];
                break;
            }
            case 1110: {// Origin [IFineADCS] Method [void accelerometerReadInterval(int
                // interval);//1110//High level command to interact with FineADCS]
                int interval = (Integer) argObject.get(0);
                break;
            }
            case 1111: {// Origin [IFineADCS] Method [void magnetometerSetMagneticField(float[]
                // values);//1111//High level command to interact with FineADCS]
                float[] values = (float[]) argObject.get(0);
                break;
            }
            case 1112: {// Origin [IFineADCS] Method [byte[] magnetometerGetMagneticField();//1112//High
                // level command to interact with FineADCS]
                globalResult = new byte[12];
                break;
            }
            case 1113: {// Origin [IFineADCS] Method [void magnetometerSetUpdateInterval(int
                // interval);//1113//High level command to interact with FineADCS]
                int interval = (Integer) argObject.get(0);
                break;
            }
            case 1114: {// Origin [IFineADCS] Method [void accelerometerSetCalibrationParams(float[]
                // values);//1114//High level command to interact with FineADCS]
                float[] values = (float[]) argObject.get(0);
                break;
            }
            case 1115: {// Origin [IFineADCS] Method [byte[]
                // accelerometerGetCalibrationParams();//1115//High level command to interact
                // with FineADCS]
                globalResult = new byte[48];
                break;
            }
            case 1116: {// Origin [IFineADCS] Method [void accelerometerEnableCalibration();//1116//High
                // level command to interact with FineADCS]
                break;
            }
            case 1117: {// Origin [IFineADCS] Method [void
                // accelerometerDisableCalibration();//1117//High level command to interact with
                // FineADCS]
                break;
            }
            case 1118: {// Origin [IFineADCS] Method [void
                // accelerometerSetQuaternionFromSunSensor(float[]
                // quaternionValues);//1118//High level command to interact with FineADCS]
                float[] quaternionValues = (float[]) argObject.get(0);
                break;
            }
            case 1119: {// Origin [IFineADCS] Method [byte[]
                // accelerometerGetQuaternionFromSunSensor();//1119//High level command to
                // interact with FineADCS]
                globalResult = new byte[16];
                break;
            }
            case 1120: {// Origin [IFineADCS] Method [byte[] kalman2FilterGetTelemetry(int
                // requestRegister);//1120//High level command to interact with FineADCS]
                int requestRegister = (Integer) argObject.get(0);
                globalResult = new byte[68];
                break;
            }
            case 1121: {// Origin [IFineADCS] Method [void kalman2FilterSelectGyro(byte
                // selectGyro);//1121//High level command to interact with FineADCS]
                byte selectGyro = (Byte) argObject.get(0);
                break;
            }
            case 1122: {// Origin [IFineADCS] Method [void kalman2FilterStart();//1122//High level
                // command to interact with FineADCS]
                break;
            }
            case 1123: {// Origin [IFineADCS] Method [void kalman2FilterStop();//1123//High level
                // command to interact with FineADCS]
                break;
            }
            case 1124: {// Origin [IFineADCS] Method [byte[] kalman4FilterGetTelemetry(int
                // requestRegister);//1124//High level command to interact with FineADCS]
                int requestRegister = (Integer) argObject.get(0);
                globalResult = new byte[68];
                break;
            }
            case 1125: {// Origin [IFineADCS] Method [void kalman4FilterSelectGyro(byte
                // selectGyro);//1125//High level command to interact with FineADCS]
                byte selectGyro = (Byte) argObject.get(0);
                break;
            }
            case 1126: {// Origin [IFineADCS] Method [void kalman4FilterStart();//1126//High level
                // command to interact with FineADCS]
                break;
            }
            case 1127: {// Origin [IFineADCS] Method [void kalman4FilterStop();//1127//High level
                // command to interact with FineADCS]
                break;
            }
            case 1128: {// Origin [IFineADCS] Method [void controlLoopsSetUpdateInterval(int
                // interval);//1128//High level command to interact with FineADCS]
                int interval = (Integer) argObject.get(0);
                break;
            }
            case 1129: {// Origin [IFineADCS] Method [byte[] controlLoopsGetTargetRWSpeed();//1129//High
                // level command to interact with FineADCS]
                globalResult = new byte[6];
                break;
            }
            case 1130: {// Origin [IFineADCS] Method [byte[]
                // controlLoopsGetTargetMTWDipoleMoment3D();//1130//High level command to
                // interact with FineADCS]
                globalResult = new byte[6];
                break;
            }
            case 1131: {// Origin [IFineADCS] Method [byte[] controlLoopsGetStatus();//1131//High level
                // command to interact with FineADCS]
                globalResult = new byte[24];
                break;
            }
            case 1132: {// Origin [IFineADCS] Method [void controlLoopsSetAntiWindup(byte axis,int
                // controlRegister,float[] values);//1132//High level command to interact with
                // FineADCS]
                byte axis = (Byte) argObject.get(0);
                int controlRegister = (Integer) argObject.get(1);
                float[] values = (float[]) argObject.get(2);
                break;
            }
            case 1133: {// Origin [IFineADCS] Method [byte[] controlLoopsGetAntiWindup(byte axis,int
                // controlRegister);//1133//High level command to interact with FineADCS]
                byte axis = (Byte) argObject.get(0);
                int controlRegister = (Integer) argObject.get(1);
                globalResult = new byte[16];
                break;
            }
            case 1134: {// Origin [IFineADCS] Method [void singleAxisStartControlLoop(byte axis,int
                // controlRegister,float[] targetAngle);//1134//High level command to interact
                // with FineADCS]
                byte axis = (Byte) argObject.get(0);
                int controlRegister = (Integer) argObject.get(1);
                float[] targetAngle = (float[]) argObject.get(2);
                break;
            }
            case 1135: {// Origin [IFineADCS] Method [void singleAxisStopControlLoop(byte
                // axis);//1135//High level command to interact with FineADCS]
                byte axis = (Byte) argObject.get(0);
                break;
            }
            case 1136: {// Origin [IFineADCS] Method [void singleAxisSetParameter(byte axis,int
                // controlRegister,float[] values);//1136//High level command to interact with
                // FineADCS]
                byte axis = (Byte) argObject.get(0);
                int controlRegister = (Integer) argObject.get(1);
                float[] values = (float[]) argObject.get(2);
                break;
            }
            case 1137: {// Origin [IFineADCS] Method [byte[] singleAxisGetParameter(byte axis,int
                // controlRegister);//1137//High level command to interact with FineADCS]
                byte axis = (Byte) argObject.get(0);
                int controlRegister = (Integer) argObject.get(1);
                globalResult = new byte[28];
                break;
            }
            case 1138: {// Origin [IFineADCS] Method [void singleAxisResetParameter(byte axis,int
                // controlRegister);//1138//High level command to interact with FineADCS]
                byte axis = (Byte) argObject.get(0);
                int controlRegister = (Integer) argObject.get(1);
                break;
            }
            case 1139: {// Origin [IFineADCS] Method [void sunPointingStartControlLoop(float[]
                // targetSunVector);//1139//High level command to interact with FineADCS]
                float[] targetSunVector = (float[]) argObject.get(0);
                break;
            }
            case 1140: {// Origin [IFineADCS] Method [void sunPointingStopControlLoop();//1140//High
                // level command to interact with FineADCS]
                break;
            }
            case 1141: {// Origin [IFineADCS] Method [void sunPointingSetParameter(float[]
                // values);//1141//High level command to interact with FineADCS]
                float[] values = (float[]) argObject.get(0);
                break;
            }
            case 1142: {// Origin [IFineADCS] Method [byte[] sunPointingGetParameter();//1142//High
                // level command to interact with FineADCS]
                globalResult = new byte[40];
                break;
            }
            case 1143: {// Origin [IFineADCS] Method [void sunPointingResetParameter();//1143//High
                // level command to interact with FineADCS]
                break;
            }
            case 1144: {// Origin [IFineADCS] Method [void bdotStartControlLoop(byte
                // controller);//1144//High level command to interact with FineADCS]
                byte controller = (Byte) argObject.get(0);
                break;
            }
            case 1145: {// Origin [IFineADCS] Method [void bdotStopControlLoop();//1145//High level
                // command to interact with FineADCS]
                break;
            }
            case 1146: {// Origin [IFineADCS] Method [void bdotSetParameter(float gain);//1146//High
                // level command to interact with FineADCS]
                float gain = (Float) argObject.get(0);
                break;
            }
            case 1147: {// Origin [IFineADCS] Method [byte[] bdotGetParameter();//1147//High level
                // command to interact with FineADCS]
                globalResult = new byte[4];
                break;
            }
            case 1148: {// Origin [IFineADCS] Method [void bdotResetParameter();//1148//High level
                // command to interact with FineADCS]
                break;
            }
            case 1149: {// Origin [IFineADCS] Method [void singleSpinningStartControlLoop(float[]
                // targetBodyAxis,float targetAngularVelocityMagnitude,float[]
                // inertialTargetVector);//1149//High level command to interact with FineADCS]
                float[] targetBodyAxis = (float[]) argObject.get(0);
                float targetAngularVelocityMagnitude = (Float) argObject.get(1);
                float[] inertialTargetVector = (float[]) argObject.get(2);
                break;
            }
            case 1150: {// Origin [IFineADCS] Method [void singleSpinningStopControlLoop();//1150//High
                // level command to interact with FineADCS]
                break;
            }
            case 1151: {// Origin [IFineADCS] Method [void singleSpinningSetParameter(float[]
                // values);//1151//High level command to interact with FineADCS]
                float[] values = (float[]) argObject.get(0);
                break;
            }
            case 1152: {// Origin [IFineADCS] Method [byte[] singleSpinningGetParameter();//1152//High
                // level command to interact with FineADCS]
                globalResult = new byte[52];
                break;
            }
            case 1153: {// Origin [IFineADCS] Method [void targetTrackingStartModeConstantVel(byte
                // modeType,float[] values,long[] times);//1153//High level command to interact
                // with FineADCS]
                byte modeType = (Byte) argObject.get(0);
                float[] values = (float[]) argObject.get(1);
                long[] times = (long[]) argObject.get(2);
                break;
            }
            case 1154: {// Origin [IFineADCS] Method [void targetTrackingStartModeGeneral(byte
                // modeType,float[] values,long[] times);//1154//High level command to interact
                // with FineADCS]
                byte modeType = (Byte) argObject.get(0);
                float[] values = (float[]) argObject.get(1);
                long[] times = (long[]) argObject.get(2);
                break;
            }
            case 1155: {// Origin [IFineADCS] Method [void targetTrackingStartModeWGS84(byte
                // modeType,float[] values,long[] times);//1155//High level command to interact
                // with FineADCS]
                byte modeType = (Byte) argObject.get(0);
                float[] values = (float[]) argObject.get(1);
                long[] times = (long[]) argObject.get(2);
                break;
            }
            case 1156: {// Origin [IFineADCS] Method [void targetTrackingStopMode();//1156//High level
                // command to interact with FineADCS]
                break;
            }
            case 1157: {// Origin [IFineADCS] Method [void targetTrackingSetParameters(float[]
                // values);//1157//High level command to interact with FineADCS]
                float[] values = (float[]) argObject.get(0);
                break;
            }
            case 1158: {// Origin [IFineADCS] Method [byte[] targetTrackingGetParameters();//1158//High
                // level command to interact with FineADCS]
                globalResult = new byte[73];
                break;
            }
            case 1159: {// Origin [IFineADCS] Method [void targetTrackingResetParameters();//1159//High
                // level command to interact with FineADCS]
                break;
            }
            case 1160: {// Origin [IFineADCS] Method [void orbitSetRV();//1160//High level command to
                // interact with FineADCS]
                break;
            }
            case 1161: {// Origin [IFineADCS] Method [byte[] orbitGetRV();//1161//High level command to
                // interact with FineADCS]
                globalResult = new byte[32];
                break;
            }
            case 1162: {// Origin [IFineADCS] Method [void orbitSetTLE(byte[] tleData);//1162//High
                // level command to interact with FineADCS]
                byte[] tleData = (byte[]) argObject.get(0);
                StringBuilder newTLE1 = new StringBuilder(), newTLE2 = new StringBuilder();
                boolean result = OrekitCore.parseTLEFromBytes(tleData, newTLE1, newTLE2);
                node.logger.log(Level.FINE, "Response ok to new TLEs is [" + result + "]");
                if (result) {
                    node.simulatorHeader.setOrekitTLE1(newTLE1.toString());
                    node.simulatorHeader.setOrekitTLE2(newTLE2.toString());
                    node.orekitCore.setNewTLEs(node.simulatorHeader);
                } else {
                    commandResult.setCommandFailed(true);
                }
                break;
            }
            case 1163: {// Origin [IFineADCS] Method [byte[] orbitSetUpdateInterval(int
                // updateInterval);//1163//High level command to interact with FineADCS]
                int updateInterval = (Integer) argObject.get(0);
                globalResult = new byte[32];
                break;
            }
            case 1164: {// Origin [IFineADCS] Method [void opModeIdle();//1164//High level command to
                // interact with FineADCS]
                break;
            }
            case 1165: {// Origin [IFineADCS] Method [void opModeSafe();//1165//High level command to
                // interact with FineADCS]
                node.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.SUN_POINTING);
                break;
            }
            case 1166: {// Origin [IFineADCS] Method [void opModeMeasure();//1166//High level command to
                // interact with FineADCS]
                node.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.NADIR_POINTING);
                break;
            }
            case 1167: {// Origin [IFineADCS] Method [void opModeDetumble(byte start,long[]
                // times);//1167//High level command to interact with FineADCS]
                byte start = (Byte) argObject.get(0);
                long[] times = (long[]) argObject.get(1);
                node.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.BDOT_DETUMBLE);
                break;
            }
            case 1168: {// Origin [IFineADCS] Method [void opModeSunPointing(byte[] mode,long[]
                // times,float[] targetSunVector);//1168//High level command to interact with
                // FineADCS]
                byte[] mode = (byte[]) argObject.get(0);
                long[] times = (long[]) argObject.get(1);
                float[] targetSunVector = (float[]) argObject.get(2);
                node.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.SUN_POINTING);
                break;
            }
            case 1169: {// Origin [IFineADCS] Method [byte[] opModeGetSunPointingStatus();//1169//High
                // level command to interact with FineADCS]
                byte[] result = new byte[25];
                SimulatorSpacecraftState spacecraftState = node.getSpacecraftState();
                double[] sunVector = spacecraftState.getSunVector();
                FWRefFineADCS.putFloatInByteArray((float) sunVector[0],
                        FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_X, result);
                FWRefFineADCS.putFloatInByteArray((float) sunVector[1],
                        FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_Y, result);
                FWRefFineADCS.putFloatInByteArray((float) sunVector[2],
                        FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_Z, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(0), FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(1), FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(2), FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Z, result);
                globalResult = result;
                break;
            }
            case 1170: {// Origin [IFineADCS] Method [void opModeSetModeSpin(byte mode,long[]
                // times,float[] targetVector);//1170//High level command to interact with
                // FineADCS]
                byte mode = (Byte) argObject.get(0);
                long[] times = (long[]) argObject.get(1);
                float[] targetVector = (float[]) argObject.get(2);
                node.orekitCore.changeAttitudeLof(targetVector[0],
                        targetVector[1], targetVector[2], targetVector[6]);
                break;
            }
            case 1171: {// Origin [IFineADCS] Method [byte[] opModeGetSpinModeStatus();//1171//High
                // level command to interact with FineADCS]
                byte[] result = new byte[64];
                SimulatorSpacecraftState spacecraftState = node.getSpacecraftState();
                double[] sunVector = spacecraftState.getSunVector();
                float[] magneticField = spacecraftState.getMagnetometer();
                float[] quaternions = spacecraftState.getQ();
                FWRefFineADCS.putFloatInByteArray((float) sunVector[0],
                        FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_X, result);
                FWRefFineADCS.putFloatInByteArray((float) sunVector[1],
                        FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_Y, result);
                FWRefFineADCS.putFloatInByteArray((float) sunVector[2],
                        FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_Z, result);
                FWRefFineADCS.putFloatInByteArray(magneticField[0],
                        FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_X, result);
                FWRefFineADCS.putFloatInByteArray(magneticField[1],
                        FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_Y, result);
                FWRefFineADCS.putFloatInByteArray(magneticField[2],
                        FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_Z, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[0], FWRefFineADCS.SPINMODESTAT_IDX.Q1, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[1], FWRefFineADCS.SPINMODESTAT_IDX.Q2, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[2], FWRefFineADCS.SPINMODESTAT_IDX.Q3, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[3], FWRefFineADCS.SPINMODESTAT_IDX.Q4, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum)
                        .getTypeAsFloatByIndex(0), FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum)
                        .getTypeAsFloatByIndex(1), FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum)
                        .getTypeAsFloatByIndex(2), FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_Z, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer)
                        .getTypeAsIntByIndex(0), FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer)
                        .getTypeAsIntByIndex(1), FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_Magnetorquer)
                        .getTypeAsIntByIndex(2), FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_Z, result);
                globalResult = result;
                break;
            }
            case 1172: {// Origin [IFineADCS] Method [void opModeSetTargetTrackingCVelocity(byte
                // mode,long[] times,float[] targetVector);//1172//High level command to
                // interact with FineADCS]
                byte mode = (Byte) argObject.get(0);
                long[] times = (long[]) argObject.get(1);
                float[] targetVector = (float[]) argObject.get(2);
                break;
            }
            case 1173: {// Origin [IFineADCS] Method [byte[]
                // opModeGetTargetTrackingCVelocityStatus();//1173//High level command to
                // interact with FineADCS]
                globalResult = new byte[56];
                break;
            }
            case 1174: {// Origin [IFineADCS] Method [void opModeSetNadirTargetTracking(byte mode,long[]
                // times);//1174//High level command to interact with FineADCS]
                byte mode = (Byte) argObject.get(0);
                long[] times = (long[]) argObject.get(1);
                node.orekitCore.changeAttitude(OrekitCore.ATTITUDE_MODE.NADIR_POINTING);
                break;
            }
            case 1175: {// Origin [IFineADCS] Method [byte[]
                // opModeGetNadirTargetTrackingStatus();//1175//High level command to interact
                // with FineADCS]
                byte[] result = new byte[68];
                SimulatorSpacecraftState spacecraftState = node.getSpacecraftState();
                float[] positionVector = spacecraftState.getRv();
                float[] quaternions = spacecraftState.getQ();
                FWRefFineADCS.putFloatInByteArray(positionVector[0],
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_X, result);
                FWRefFineADCS.putFloatInByteArray(positionVector[1],
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_Y, result);
                FWRefFineADCS.putFloatInByteArray(positionVector[2],
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_Z, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity)
                        .getTypeAsFloatByIndex(0), FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity)
                        .getTypeAsFloatByIndex(1), FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity)
                        .getTypeAsFloatByIndex(2), FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_Z, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[0], FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q1, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[1], FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q2, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[2], FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q3, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[3], FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q4, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[0],
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q1, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[1],
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q2, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[2],
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q3, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[3],
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q4, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(0),
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(1),
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(2),
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_Z, result);
                globalResult = result;
                break;
            }
            case 1176: {// Origin [IFineADCS] Method [void opModeSetStandardTargetTracking(byte
                // mode,long[] times,float[] quaternionCoefficients);//1176//High level command
                // to interact with FineADCS]
                byte mode = (Byte) argObject.get(0);
                long[] times = (long[]) argObject.get(1);
                float[] quaternionCoefficients = (float[]) argObject.get(2);
                break;
            }
            case 1177: {// Origin [IFineADCS] Method [byte[]
                // opModeGetStandardTargetTrackingStatus();//1177//High level command to
                // interact with FineADCS]
                globalResult = new byte[56];
                break;
            }
            case 1178: {// Origin [IFineADCS] Method [void opModeSetFixWGS84TargetTracking(byte
                // mode,long[] times,float[] latitudeLongitude);//1178//High level command to
                // interact with FineADCS]
                byte mode = (Byte) argObject.get(0);
                long[] times = (long[]) argObject.get(1);
                float[] latitudeLongitude = (float[]) argObject.get(2);
                node.orekitCore.changeAttitudeTarget(latitudeLongitude[0], latitudeLongitude[1], 0);
                break;
            }
            case 1179: {// Origin [IFineADCS] Method [byte[]
                // opModeGetFixWGS84TargetTracking();//1179//High level command to interact with
                // FineADCS]
                byte[] result = new byte[68];
                SimulatorSpacecraftState spacecraftState = node.getSpacecraftState();
                float[] positionVector = spacecraftState.getRv();
                float[] quaternions = spacecraftState.getQ();
                FWRefFineADCS.putFloatInByteArray(positionVector[0],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_X, result);
                FWRefFineADCS.putFloatInByteArray(positionVector[1],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_Y, result);
                FWRefFineADCS.putFloatInByteArray(positionVector[2],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_Z, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity)
                        .getTypeAsFloatByIndex(0), FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity)
                        .getTypeAsFloatByIndex(1), FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_AngularVelocity)
                        .getTypeAsFloatByIndex(2), FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_Z, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[0],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q1, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[1],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q2, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[2],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q3, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[3],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q4, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[0],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q1, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[1],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q2, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[2],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q3, result);
                FWRefFineADCS.putFloatInByteArray(quaternions[3],
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q4, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(0),
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_X, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(1),
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_Y, result);
                FWRefFineADCS.putFloatInByteArray(node.hMapSDData.get(DevDatPBind.FineADCS_ReactionWheels)
                        .getTypeAsIntByIndex(2),
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_Z, result);
                globalResult = result;
                break;
            }
            case 1180: {// Origin [IFineADCS] Method [void opModeSetTargetCapture1(byte mode,long
                // startTime,float[] data);//1180//High level command to interact with FineADCS]
                byte mode = (Byte) argObject.get(0);
                long startTime = (Long) argObject.get(1);
                float[] data = (float[]) argObject.get(2);
                break;
            }
            case 1181: {// Origin [IFineADCS] Method [byte[] opModeGetTargetCapture1();//1181//High
                // level command to interact with FineADCS]
                globalResult = new byte[22];
                break;
            }
            case 1182: {// Origin [IFineADCS] Method [byte[] simGetOrbitTLEBytesFromString(String
                // tleLine1,String tleLine2);//1182//High level command to interact with
                // FineADCS]
                String tleLine1 = (String) argObject.get(0);
                String tleLine2 = (String) argObject.get(1);
                StringBuilder exception = new StringBuilder();
                byte[] result = new byte[140];
                if (!OrekitCore.parseTLEFromStrings(result, tleLine1, tleLine2, exception)) {
                    commandResult.setCommandFailed(true);
                    throw new Exception(exception.toString());
                }
                globalResult = result;
                break;
            }
            case 1183: {// Origin [IFineADCS] Method [float simGetFloatFromByteArray(byte[] data,int
                // byteOffset);//1183//Test command for the helper libraries]
                byte[] data = (byte[]) argObject.get(0);
                int byteOffset = (Integer) argObject.get(1);
                globalResult = FWRefFineADCS.getFloatFromByteArray(data, byteOffset);
                break;
            }
            case 1184: {// Origin [IFineADCS] Method [byte[] simGetByteArrayFromFloat(float
                // data);//1184//Test command for the helper libraries]
                float data = (Float) argObject.get(0);
                byte[] result = new byte[4];
                FWRefFineADCS.putFloatInByteArray(data, 0, result);
                globalResult = result;
                break;
            }
            case 1185: {// Origin [IFineADCS] Method [double simGetDoubleFromByteArray(byte[] data,int
                // byteOffset);//1185//Test command for the helper libraries]
                byte[] data = (byte[]) argObject.get(0);
                int byteOffset = (Integer) argObject.get(1);
                globalResult = FWRefFineADCS.getDoubleFromByteArray(data, byteOffset);
                break;
            }
            case 1186: {// Origin [IFineADCS] Method [byte[] simGetByteArrayFromDouble(double
                // data);//1186//Test command for the helper libraries]
                double data = (Double) argObject.get(0);
                byte[] result = new byte[8];
                FWRefFineADCS.putDoubleInByteArray(data, 0, result);
                globalResult = result;
                break;
            }
            case 1187: {// Origin [IFineADCS] Method [int simGetIntFromByteArray(byte[] data,int
                // byteOffset);//1187//Test command for the helper libraries]
                byte[] data = (byte[]) argObject.get(0);
                int byteOffset = (Integer) argObject.get(1);
                globalResult = FWRefFineADCS.getIntFromByteArray(data, byteOffset);
                break;
            }
            case 1188: {// Origin [IFineADCS] Method [byte[] simGetByteArrayFromInt(int
                // data);//1188//Test command for the helper libraries]
                int data = (Integer) argObject.get(0);
                byte[] result = new byte[4];
                FWRefFineADCS.putIntInByteArray(data, 0, result);
                globalResult = result;
                break;
            }
            case 1189: {// Origin [IFineADCS] Method [long simGetLongFromByteArray(byte[] data,long
                // byteOffset);//1189//Test command for the helper libraries]
                byte[] data = (byte[]) argObject.get(0);
                int byteOffset = (Integer) argObject.get(1);
                globalResult = FWRefFineADCS.getLongFromByteArray(data, byteOffset);
                break;
            }
            case 1190: {// Origin [IFineADCS] Method [byte[] simGetByteArrayFromLong(long
                // data);//1190//Test command for the helper libraries]
                long data = (Long) argObject.get(0);
                byte[] result = new byte[8];
                FWRefFineADCS.putLongInByteArray(data, 0, result);
                globalResult = result;
                break;
            }
            case 1191: {// Origin [IFineADCS] Method [void Gyro2SetRate(float[] values);//1191//High
                // level command to interact with FineADCS]
                float[] values = (float[]) argObject.get(0);
                node.hMapSDData.get(DevDatPBind.FineADCS_Gyro2).setType(values);
                node.hMapSDData.get(DevDatPBind.FineADCS_AngularMomentum).setType(values);
                break;
            }
            case 1192: {// Origin [IFineADCS] Method [byte[] Gyro2GetRate();//1192//High level command
                // to interact with FineADCS]
                globalResult = new byte[20];
                break;
            }
            case 1193: {// Origin [IFineADCS] Method [void Gyro2SetUpdateInterval(int
                // updateRate);//1193//High level command to interact with FineADCS]
                int updateRate = (Integer) argObject.get(0);
                break;
            }
            case 1194: {// Origin [IFineADCS] Method [void Gyro2RemoveBias();//1194//High level command
                // to interact with FineADCS]
                break;
            }
            case 1195: {// Origin [IFineADCS] Method [byte[] Gyro2GetBias();//1195//High level command
                // to interact with FineADCS]
                globalResult = new byte[4];
                break;
            }
            case 1196: {// Origin [IFineADCS] Method [void Gyro2SetFilter1(byte updateRate,int
                // allowedDeviation);//1196//High level command to interact with FineADCS]
                byte updateRate = (Byte) argObject.get(0);
                int allowedDeviation = (Integer) argObject.get(1);
                break;
            }
            case 1197: {// Origin [IFineADCS] Method [void Gyro2SetCalibrationParameters(int[]
                // calibrationValues);//1197//High level command to interact with FineADCS]
                int[] calibrationValues = (int[]) argObject.get(0);
                break;
            }
            case 1198: {// Origin [IFineADCS] Method [byte[]
                // Gyro2GetCalibrationParameters();//1198//High level command to interact with
                // FineADCS]
                globalResult = new byte[48];
                break;
            }
            case 1199: {// Origin [IFineADCS] Method [void Gyro2EnableCalibration();//1199//High level
                // command to interact with FineADCS]
                break;
            }
            case 1200: {// Origin [IFineADCS] Method [void Gyro2DisableCalibration();//1200//High level
                // command to interact with FineADCS]
                break;
            }
            case 1201: {// Origin [IFineADCS] Method [void Gyro2SetQuaternionFromSunSensor(float[]
                // quaternionValues);//1201//High level command to interact with FineADCS]
                float[] quaternionValues = (float[]) argObject.get(0);
                break;
            }
            case 1202: {// Origin [IFineADCS] Method [byte[]
                // Gyro2GetQuaternionFromSunSensor();//1202//High level command to interact with
                // FineADCS]
                globalResult = new byte[16];
                break;
            }
            case 1203: {// Origin [IFineADCS] Method [int simGetInt16FromByteArray(byte[] data,int
                // byteOffset);//1203//Test command for the helper libraries]
                byte[] data = (byte[]) argObject.get(0);
                int byteOffset = (Integer) argObject.get(1);
                globalResult = FWRefFineADCS.getInt16FromByteArray(data, byteOffset);
                break;
            }
            case 1204: {// Origin [IFineADCS] Method [void simRunDeviceCommand(String
                // data);//1204//Generic for loading restricted subsystems]
                String data = (String) argObject.get(0);
                if (data != null) {
                    String[] words = data.split(":");
                    if (words.length > 0) {
                        if ("quaternionServer".equals(words[0])) {
                            if (node.quaternionTcpServer != null) {
                                node.quaternionTcpServer.setShouldClose(true);
                            }
                            node.quaternionTcpServer = new TCPServerReceiveOnly(Integer.parseInt(words[1]),
                                    node.logger);
                            node.quaternionTcpServer.start();
                        } else if ("cameraScript".equals(words[0])) {
                            node.cameraScriptPath = words[1];
                        } else {
                            throw new IOException("Command not recognised [" + data + "]");
                        }
                    }
                }
                break;
            }

            default:
                break;
        }
        return globalResult;
    }
}
