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
package opssat.simulator.interfaces;

/**
 *
 * @author Cezar Suteu
 */
public interface IFineADCS {
    //# Boolean enabled1
    //# Integer valueInteger1
    //# Float valueFloat1
    //# String textField1

    /**
     * /
     **
     * <pre>
     * Low level command to interact with FineADCS
     * Input parameters:int cmdID,byte[] data,int iAD
     * Return parameters:byte[]
     * Size of returned parameters: 0
     * This commands accepts generic structures for the ADCS.
     * </pre>
     */
    byte[] runRawCommand(int cmdID, byte[] data, int iAD);//1001

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 8
     * 0..7: iACDS100
     * </pre>
     */
    byte[] Identify();//1002

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     *
     * </pre>
     */
    void SoftwareReset();//1003

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     *
     * </pre>
     */
    void I2CReset();//1004

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:long seconds,int subseconds
     * Return parameters:void
     * Size of returned parameters: 0
     * Epoch: 01.01.1970 0:00:00
     * UTC
     * UI32: Seconds
     * UI16: Sub-seconds [msec]
     * </pre>
     */
    void SetDateTime(long seconds, int subseconds);//1005

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 6
     * Epoch: 01.01.1970 0:00:00
     * UTC
     * UI32: Seconds
     * UI16: Sub-seconds [msec]
     * </pre>
     */
    byte[] GetDateTime();//1006

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte onoff,byte register
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: ON/OFF
     * ON: 0x55
     * OFF: 0xAA
     * UI8: Register of selected
     * devices
     * 0.bit: Reaction wheels
     * 1.bit: ST200
     * 2.bit: Sunsensors
     * 3.bit: iACDS
     * </pre>
     */
    void iADCSPowerCycle(byte onoff, byte register);//1007

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte opmode
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: Mode
     * 0 - Idle Mode
     * 1 - Save Mode (= Idle Mode)
     * </pre>
     */
    void SetOperationMode(byte opmode);//1008

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:long miliseconds
     * Return parameters:void
     * Size of returned parameters: 0
     * UI32: Interval time in msec
     * </pre>
     */
    void SetPowerUpdateInterval(long miliseconds);//1009

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:long miliseconds
     * Return parameters:void
     * Size of returned parameters: 0
     * UI32: Interval time in msec
     * </pre>
     */
    void SetTemperatureUpdateInterval(long miliseconds);//1010

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 60
     * Byte No. Type Description
     * 0-3 UI32 iACDS Status Register (see Chapter 5.5.27.1)
     * 4-7 UI32 iACDS Error Register (see Chapter 5.5.27.2)
     * 8-11 UI32 Control Status Register (see Chapter 5.5.27.5)
     * 12-15 UI32 Control Error Register
     * 16-19 UI32 Livelyhood Register (see Chapter 5.5.27.3)
     * 20-23 UI32 Elapsed seconds since epoch [sec]
     * 24-25 UI16 Elapsed sub-seconds [msec]
     * 26 I8 Gyro 1 Temperature [deg Celsius]
     * 27 I8 Gyro 2 Temperature [deg Celsius]
     * 28 I8 MTQ-X Temperature [deg Celsius]
     * 29 I8 MTQ-Y Temperature [deg Celsius]
     * 30 I8 MTQ-Z Temperature [deg Celsius]
     * 31 I8 Ctrl Processor Temperature
     * 32 I8 RW-X Temperature [deg Celsius]
     * 33 I8 RW-Y Temperature [deg Celsius]
     * 34 I8 RW-Z Temperature [deg Celsius]
     * 35 I8 ST200 Temperature [deg Celsius]
     * 36-59 Power Status Telemetry
     * </pre>
     */
    byte[] GetStandardTelemetry();//1011

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 183
     * Byte No. Type Description
     * 0-59 Standard Telemetry
     * 60-133 Sensor Telemetry
     * 134-169 Actuator Telemetry
     * 170-197 Attitude Telemetry
     * </pre>
     */
    byte[] GetExtendedTelemetry();//1012

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 24
     * Byte No. Type Description
     * 0-1 UI16 MTQ Current Consumption [mA]
     * 2-3 UI16 MTQ Power Consumption [mW]
     * 4-5 UI16 MTQ Supply Voltage [mV]
     * 6-7 UI16 ST200 Current Consumption [mA]
     * 8-9 UI16 ST200 Power Consumption [mW]
     * 10-11 UI16 ST200 Supply Voltage [mV]
     * 12-13 UI16 iACDS 3.3V Current Consumption [mA]
     * 14-15 UI16 iACDS 3.3V Power Consumption [mW]
     * 16-17 UI16 iACDS 3.3V Supply Voltage [mV]
     * 18-19 UI16 RW Current Consumption [mA]
     * 20-21 UI16 RW Power Consumption [mW]
     * 22-23 UI16 RW Supply Voltage [mV]
     * </pre>
     */
    byte[] GetPowerStatus();//1013

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 158
     * 0 UI8 Primary Target Type: should be 0x02
     * 1 UI8 Secondary Target Type
     * 2 - internal use
     * 3..12 String Device Name [including string terminating character]
     * 13..15 String Device Model Name [including string terminating character]
     * 16..19 UI32 Serial Number
     * 20..52 String Compile Time [including string terminating character]
     * 53..58 String Software Version [including string terminating character]
     * 59 UI8 Debug Level
     * 60..70 String Git Commit ID [including string terminating character]
     * 71..91 String Compiler [including string terminating character]
     * 92..102 String Compiler Version
     * 103..157 - internal use
     * </pre>
     */
    byte[] GetInfoTelemetry();//1014

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 74
     * Byte No. Type Description
     * 0-3 Float Magnetic Field X [uT]
     * 4-7 Float Magnetic Field Y [uT]
     * 8-11 Float Magnetic Field Z [uT]
     * 12-15 Float Accelerometer X [m/s^2]
     * 16-19 Float Accelerometer Y [m/s^2]
     * 20-23 Float Accelerometer Z [m/s^2]
     * 24-27 Float Gyro 1 X [mdps]
     * 28-31 Float Gyro 1 Y [mdps]
     * 32-35 Float Gyro 1 Z [mdps]
     * 36-39 Float Gyro 2 X [mdps]
     * 40-43 Float Gyro 2 Y [mdps]
     * 44-47 Float Gyro 2 Z [mdps]
     * 48-55 UI64 ST200 Time [sec]
     * 56-57 UI16 ST200 Time [m sec]
     * 58-61 Float Quaternion 1
     * 62-65 Float Quaternion 2
     * 66-69 Float Quaternion 3
     * 70-73 Float Quaternion 4
     * </pre>
     */
    byte[] GetSensorTelemetry();//1015

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 21
     * Byte No. Type Description
     * 0-1 I16 Reaction Wheel Current Speed X [rpm]
     * 2-3 I16 Reaction Wheel Last Target Value X []
     * 4 UI8 Reaction Wheel Target Mode X [Values- rpm, TBD,
     * rpm/sec]
     * 5-6 I16 Reaction Wheel Current Speed Y [rpm]
     * 7-8 I16 Reaction Wheel Last Target Value Y []
     * 9 UI8 Reaction Wheel Target Mode Y [Values- rpm, TBD,
     * rpm/sec]
     * 10-11 I16 Reaction Wheel Current Speed Z [rpm]
     * 12-13 I16 Reaction Wheel Last Target Value Z []
     * 14 UI8 Reaction Wheel Target Mode Z [Values- rpm, TBD,
     * rpm/sec]
     * 15-16 I16 Magnetorquer Target X [mA m^2]
     * 17-18 I16 Magnetorquer Target Y [mA m^2]
     * 19-20 I16 Magnetorquer Target Z [mA m^2]
     * </pre>
     */
    byte[] GetActuatorTelemetry();//1016

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 28
     * Byte No. Type Description
     * 0-3 Float Quaternion Attitude 1
     * 4-7 Float Quaternion Attitude 2
     * 8-11 Float Quaternion Attitude 3
     * 12-15 Float Quaternion Attitude 4
     * 16-19 Float Angular rate X [rad/sec]
     * 20-23 Float Angular rate Y [rad/sec]
     * 24-27 Float Angular rate Z [rad/sec]
     * </pre>
     */
    byte[] GetAttitudeTelemetry();//1017

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 154
     * Byte No. Type Description
     * 0-73 Sensor Telemetry
     * 74-81 UI64 Magnetometer Timestamp [msec]
     * 82-89 UI64 Accelerometer Timestamp [msec]
     * 90-97 UI64 Gyro 1 Timestamp [msec]
     * 98-105 UI64 Gyro 2 Timestamp [msec]
     * 106-113 UI64 Sun Sensor 1 Timestamp [msec]
     * 114-121 UI64 Sun Sensor 2 Timestamp [msec]
     * 122-129 UI64 Sun Sensor 3 Timestamp [msec]
     * 130-137 UI64 Sun Sensor 4 Timestamp [msec]
     * 138-145 UI64 Sun Sensor 5 Timestamp [msec]
     * 146-153 UI64 Sun Sensor 6 Timestamp [msec]
     * </pre>
     */
    byte[] GetExtendedSensorTelemetry();//1018

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 53
     * Byte No. Type Description
     * 0-20 Actuator Telemetry
     * 21-28 UI64 Reaction Wheel Speed Timestamp [msec]
     * 29-36 UI64 Magnetorquer X Target Timestamp [msec]
     * 37-44 UI64 Magnetorquer Y Target Timestamp [msec]
     * 45-52 UI64 Magnetorquer Z Target Timestamp [msec]
     * </pre>
     */
    byte[] GetExtendedActuatorTelemetry();//1019

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 36
     * Byte No. Type Description
     * 0-27 Attitude Telemetry
     * 28-35 UI64 Star Tracker Timestamp [msec]
     * </pre>
     */
    byte[] GetExtendedAttitudeTelemetry();//1020

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 32
     * Data:
     * 3xF32: Magnetic field
     * measured by sensor
     * 3xF32: Final magnetic field
     * in b-frame, which is a
     * emulated one, if SC is
     * rotating too fast.
     * 1xUI64: Time stamp
     * </pre>
     */
    byte[] GetMagneticTelemetry();//1021

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 13
     * Data:
     * 3xF32: Final Sunsensor after
     * selection in b-frame
     * UI8: valid flag
     * </pre>
     */
    byte[] GetSunTelemetry();//1022

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int value
     * Return parameters:void
     * Size of returned parameters: 0
     * Data:
     * 3xF32: Final Sunsensor after
     * selection in b-frame
     * UI8: valid flag
     * </pre>
     */
    void SetThresholdValueForMagEmulation(int value);//1023

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * Data:
     * 1xF32: Threshold value, from
     * which the emulated magnetic
     * values are used by SC.
     * </pre>
     */
    byte[] GetThresholdValueForMagEmulation();//1024

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Clear saved errors on iADCS
     * general status
     * </pre>
     */
    void ClearErrorRegister();//1025

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte register
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * UI8: Type
     * 0 - Scheduler Register;
     * 1 - Error Register;
     * 2 - Status Register;
     * 4 - HL Main Register
     * 5 - HL Scheduler Register
     * Data:
     * UI32: Register
     * </pre>
     */
    byte[] GetSystemRegisters(byte register);//1026

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte register
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * UI8: Type
     * UI8: Type = 3
     * Data:
     * UI32: Control Main;
     * UI32: Control Error
     * Register;
     * UI32: Control All Axis
     * Register;
     * 3x UI32: Control Single Axis
     * Register
     * </pre>
     */
    byte[] GetControlRegisters(byte register);//1027

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte[] data
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: Type
     * 0 - Scheduler Register;
     * 4 - HL Main Register;
     * 5 - HL Scheduler Register;
     * UI32: Value
     * </pre>
     */
    void SetSystemRegister(byte[] data);//1028

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte[] data
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: Type
     * 0 - Scheduler Register;
     * 1 - Error Register
     * </pre>
     */
    void ResetSystemRegister(byte[] data);//1029

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte memberID,long interval
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: Member ID
     * 0 - Gyro 1
     * 1 - Gyro 2
     * 2 - Magmeter
     * 3 - Accelerometer
     * 4 - ST200
     * 5 - Sunsensor
     * 6 - Temperature Sensor
     * 7 - Power Sensor
     * 8 - RWs
     * 9 - MTQ-Relax Time
     * 10 - Orbit
     * 11 - ACS
     * 12 - Control
     * 13 - HL-Mode / Target Capture
     * 1
     * UI64: Update Interval
     * </pre>
     */
    void SetMemberUpdateInterval(byte memberID, long interval);//1030

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte memberID
     * Return parameters:byte[]
     * Size of returned parameters: 8
     * UI8: Member ID
     * 0 - Gyro 1
     * 1 - Gyro 2
     * 2 - Magmeter
     * 3 - Accelerometer
     * 4 - ST200
     * 5 - Sunsensor
     * 6 - Temperature Sensor
     * 7 - Power Sensor
     * 8 - RWs
     * 9 - MTQ-Relax Time
     * 10 - Orbit
     * 11 - ACS
     * 12 - Control
     * 13 - HL-Mode / Target Capture
     * 1
     * Data:
     * UI64: Update Interval
     * </pre>
     */
    byte[] GetMemberUpdateInterval(byte memberID);//1031

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte[] HILStatusRegister
     * Return parameters:void
     * Size of returned parameters: 0
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * Valid:
     * Bit = 1 : Yes
     * Bit = 0 : No
     * 4 BIT_MASK_COMPASS HIL Mode Status of Magnetometer
     * 5 BIT_MASK_ACC HIL Mode Status of Accelerometer
     * 6 BIT_MASK_GYRO1 HIL Mode Status of Gyro 1
     * 7 BIT_MASK_GYRO2 HIL Mode Status of Gyro 2
     * 8 BIT_MASK_SS_1 HIL Mode Status of Sun Sensor 1
     * 9 BIT_MASK_SS_2 HIL Mode Status of Sun Sensor 2
     * 10 BIT_MASK_SS_3 HIL Mode Status of Sun Sensor 3
     * 11 BIT_MASK_SS_4 HIL Mode Status of Sun Sensor 4
     * 12 BIT_MASK_SS_5 HIL Mode Status of Sun Sensor 5
     * 13 BIT_MASK_SS_6 HIL Mode Status of Sun Sensor 6
     * 14 BIT_MASK_MTQ_X HIL Mode Status of MTQ X
     * 15 BIT_MASK_MTQ_Y HIL Mode Status of MTQ Y
     * 16 BIT_MASK_MTQ_Z HIL Mode Status of MTQ Z
     * 17 BIT_MASK_RW_X HIL Mode Status of RW X
     * 18 BIT_MASK_RW_Y HIL Mode Status of RW Y
     * 19 BIT_MASK_RW_Z HIL Mode Status of RW Z
     * 20 BIT_MASK_ST HIL Mode Status of Startracker
     * </pre>
     */
    void SetHILStatus(byte[] HILStatusRegister);//1032

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * Valid:
     * Bit = 1 : Yes
     * Bit = 0 : No
     * 4 BIT_MASK_COMPASS HIL Mode Status of Magnetometer
     * 5 BIT_MASK_ACC HIL Mode Status of Accelerometer
     * 6 BIT_MASK_GYRO1 HIL Mode Status of Gyro 1
     * 7 BIT_MASK_GYRO2 HIL Mode Status of Gyro 2
     * 8 BIT_MASK_SS_1 HIL Mode Status of Sun Sensor 1
     * 9 BIT_MASK_SS_2 HIL Mode Status of Sun Sensor 2
     * 10 BIT_MASK_SS_3 HIL Mode Status of Sun Sensor 3
     * 11 BIT_MASK_SS_4 HIL Mode Status of Sun Sensor 4
     * 12 BIT_MASK_SS_5 HIL Mode Status of Sun Sensor 5
     * 13 BIT_MASK_SS_6 HIL Mode Status of Sun Sensor 6
     * 14 BIT_MASK_MTQ_X HIL Mode Status of MTQ X
     * 15 BIT_MASK_MTQ_Y HIL Mode Status of MTQ Y
     * 16 BIT_MASK_MTQ_Z HIL Mode Status of MTQ Z
     * 17 BIT_MASK_RW_X HIL Mode Status of RW X
     * 18 BIT_MASK_RW_Y HIL Mode Status of RW Y
     * 19 BIT_MASK_RW_Z HIL Mode Status of RW Z
     * 20 BIT_MASK_ST HIL Mode Status of Startracker
     * </pre>
     */
    byte[] GetHILStatus();//1033

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int interval
     * Return parameters:void
     * Size of returned parameters: 0
     * UI32: Interval time in [
     * msec]
     * </pre>
     */
    void SetUpdateInterval(int interval);//1034

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Only in HIL Mode
     * 6x [3xF32 + 1xF32] -
     * Sunvector + Intensity
     * </pre>
     */
    void SetValuesToAllSensors(int[] values);//1035

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 96
     * Get sunsensor values
     * 6x [3xF32 + 1xF32] -
     * Sunvector + Intensity
     * </pre>
     */
    byte[] GetValuesAllSensors();//1036

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Calibration Parameters
     * 9x F32 - Calibration matrix
     * 3x F32 - Calibration Offset
     * </pre>
     */
    void SetCalibrationParametersAllSensors(int[] values);//1037

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 48
     * Get Calibration Parameters
     * 9x F32 - Calibration matrix
     * 3x F32 - Calibration Offset
     * </pre>
     */
    byte[] GetCalibrationParametersAllSensors();//1038

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable Calibration
     * </pre>
     */
    void EnableCalibrationAllSensors();//1039

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Disable Calibration
     * </pre>
     */
    void DisableCalibrationAllSensors();//1040

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int interval
     * Return parameters:void
     * Size of returned parameters: 0
     * UI32: Interval time in [
     * msec]
     * </pre>
     */
    void SetUpdateIntervalRW(int interval);//1041

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * 3x I16 : Desired speeds for
     * 3 axis in [rpm]
     * </pre>
     */
    void SetSpeedToAllRWs(int[] values);//1042

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 6
     * 3x I16: Speeds of 3 axis in
     * [rpm ]
     * </pre>
     */
    byte[] GetSpeedAllRWs();//1043

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:byte[]
     * Size of returned parameters: 6
     * 3x I16: Acceleration in
     * [rpm/sec]
     * </pre>
     */
    byte[] SetAccAllRWs(int[] values);//1044

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte sleepMode
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: Sleep mode
     * 0 - Sleep Mode 1: Coast
     * 1 - Sleep Mode 2: Passive
     * Brake
     * </pre>
     */
    void SetSleepAllRWs(byte sleepMode);//1045

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * 3x I16 : Values for 3 axis
     * in [mAm^2]
     * </pre>
     */
    void SetDipoleMomentAllMTQs(int[] values);//1046

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 6
     * 3x I16 : Values for 3 axis
     * in [mAm^2]
     * </pre>
     */
    byte[] GetDipoleMomentAllMTQs();//1047

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Set MTQs in sleep mode
     * </pre>
     */
    void SuspendAllMTQs();//1048

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Resume MTQs with previous
     * settings
     * </pre>
     */
    void ResumeAllMTQs();//1049

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Reset MTQs Microcontrollers
     * </pre>
     */
    void ResetAllMTQs();//1050

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Degaus operation to remove
     * resisual magnetic field in
     * the mtqs. Restart of mtqs
     * needed after this operation
     * </pre>
     */
    void RunSelftTestAllMTQs();//1051

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int relaxtime
     * Return parameters:void
     * Size of returned parameters: 0
     * Set relax time for all MTQs.
     * Relax time: time MTQ needs
     * to build up or destroy the
     * desired field
     * I16: relax time in [msec ]
     * </pre>
     */
    void SetMTQRelaxTime(int relaxtime);//1052

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Set all MTQs target to zero.
     * </pre>
     */
    void StopAllMTQ();//1053

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int dipoleValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set I16: Dipole moment value
     * in [mAm^2]
     * </pre>
     */
    void MTQXSetDipoleMoment(int dipoleValue);//1054

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 2
     * Get I16: Dipole moment value
     * in [mAm^2]
     * </pre>
     */
    byte[] MTQXGetDipoleMoment();//1055

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Set mtq in sleep mode.
     * </pre>
     */
    void MTQXSuspend();//1056

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Restart mtq with previous
     * settings
     * </pre>
     */
    void MTQXResume();//1057

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Degaus operation to remove
     * resisual magnetic field in
     * the mtq. Restart of mtq
     * needed after this operation
     * </pre>
     */
    void MTQXRunSelfTest();//1058

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Reset mtq microcontroller
     * </pre>
     */
    void MTQXReset();//1059

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Stop mtq microcontroller
     * </pre>
     */
    void MTQXStop();//1060

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int dipoleValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set I16: Dipole moment value
     * in [mAm^2]
     * </pre>
     */
    void MTQYSetDipoleMoment(int dipoleValue);//1061

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 2
     * Get I16: Dipole moment value
     * in [mAm^2]
     * </pre>
     */
    byte[] MTQYGetDipoleMoment();//1062

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Set mtq in sleep mode.
     * </pre>
     */
    void MTQYSuspend();//1063

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Restart mtq with previous
     * settings
     * </pre>
     */
    void MTQYResume();//1064

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Degaus operation to remove
     * resisual magnetic field in
     * the mtq. Restart of mtq
     * needed after this operation
     * </pre>
     */
    void MTQYRunSelfTest();//1065

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Reset mtq microcontroller
     * </pre>
     */
    void MTQYReset();//1066

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Stop mtq microcontroller
     * </pre>
     */
    void MTQYStop();//1067

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int dipoleValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set I16: Dipole moment value
     * in [mAm^2]
     * </pre>
     */
    void MTQZSetDipoleMoment(int dipoleValue);//1068

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 2
     * Get I16: Dipole moment value
     * in [mAm^2]
     * </pre>
     */
    byte[] MTQZGetDipoleMoment();//1069

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Set mtq in sleep mode.
     * </pre>
     */
    void MTQZSuspend();//1070

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Restart mtq with previous
     * settings
     * </pre>
     */
    void MTQZResume();//1071

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Degaus operation to remove
     * resisual magnetic field in
     * the mtq. Restart of mtq
     * needed after this operation
     * </pre>
     */
    void MTQZRunSelfTest();//1072

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Reset mtq microcontroller
     * </pre>
     */
    void MTQZReset();//1073

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Stop mtq microcontroller
     * </pre>
     */
    void MTQZStop();//1074

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int speedValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set wheel speed in [rpm]
     * I16: speed
     * </pre>
     */
    void SetRWXSpeed(int speedValue);//1075

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 2
     * Get wheel speed in [rpm]
     * I16: speed
     * </pre>
     */
    byte[] GetRWXSpeed();//1076

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int accelerationValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set desired acceleration of the wheel in [rpm/sec]
     * I16: Acceleration
     * </pre>
     */
    void SetRWXAcceleration(int accelerationValue);//1077

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte sleepMode
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: Sleep mode
     * 0 - Sleep Mode 1: Coast
     * 1 - Sleep Mode 2: Passive Brake
     * </pre>
     */
    void SetRWXSleep(byte sleepMode);//1078

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * Get RW ID on selected Axis
     * UI32: Wheel ID
     * </pre>
     */
    byte[] GetRWXID();//1079

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int speedValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set wheel speed in [rpm]
     * I16: speed
     * </pre>
     */
    void SetRWYSpeed(int speedValue);//1080

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 2
     * Get wheel speed in [rpm]
     * I16: speed
     * </pre>
     */
    byte[] GetRWYSpeed();//1081

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int accelerationValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set desired acceleration of the wheel in [rpm/sec]
     * I16: Acceleration
     * </pre>
     */
    void SetRWYAcceleration(int accelerationValue);//1082

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte sleepMode
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: Sleep mode
     * 0 - Sleep Mode 1: Coast
     * 1 - Sleep Mode 2: Passive Brake
     * </pre>
     */
    void SetRWYSleep(byte sleepMode);//1083

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * Get RW ID on selected Axis
     * UI32: Wheel ID
     * </pre>
     */
    byte[] GetRWYID();//1084

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int speedValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set wheel speed in [rpm]
     * I16: speed
     * </pre>
     */
    void SetRWZSpeed(int speedValue);//1085

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 2
     * Get wheel speed in [rpm]
     * I16: speed
     * </pre>
     */
    byte[] GetRWZSpeed();//1086

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int accelerationValue
     * Return parameters:void
     * Size of returned parameters: 0
     * Set desired acceleration of the wheel in [rpm/sec]
     * I16: Acceleration
     * </pre>
     */
    void SetRWZAcceleration(int accelerationValue);//1087

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte sleepMode
     * Return parameters:void
     * Size of returned parameters: 0
     * UI8: Sleep mode
     * 0 - Sleep Mode 1: Coast
     * 1 - Sleep Mode 2: Passive Brake
     * </pre>
     */
    void SetRWZSleep(byte sleepMode);//1088

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * Get RW ID on selected Axis
     * UI32: Wheel ID
     * </pre>
     */
    byte[] GetRWZID();//1089

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set quaternion q [4] in HIL
     * mode
     * 4x F32:
     * </pre>
     */
    void ST200SetQuaternion(int[] values);//1090

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:long interval
     * Return parameters:void
     * Size of returned parameters: 0
     * Update HIL Interval
     * UI64 [ms]
     * </pre>
     */
    void ST200UpdateInterval(long interval);//1091

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Value of the selected Sun
     * Sensor
     * 3x F32: Sun Sensor vector
     * 1x F32: Sun Intensity
     * </pre>
     */
    void SunSensor1SetValue(int[] values);//1092

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 16
     * Get Value of the selected Sun
     * Sensor
     * 3x F32: Sun sensor vector
     * 1x F32: Intensity
     * </pre>
     */
    byte[] SunSensor1GetValue();//1093

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Value of the selected Sun
     * Sensor
     * 3x F32: Sun Sensor vector
     * 1x F32: Sun Intensity
     * </pre>
     */
    void SunSensor1SetValueQuaternion(int[] values);//1094

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 16
     * Get Value of the selected Sun
     * Sensor
     * 3x F32: Sun sensor vector
     * 1x F32: Intensity
     * </pre>
     */
    byte[] SunSensor1GetValueQuaternion();//1095

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set gyro rate on 3 axis [in
     * HIL mode]
     * 3x F32: rates on 3 axis in
     * [rad/s]
     * </pre>
     */
    void Gyro1SetRate(float[] values);//1096

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 20
     * Get gyro rate on 3 axis
     * 3x F32 : rate on 3 axis in [rad/s]
     * 1xUI64: timestamp of gyro reading
     * </pre>
     */
    byte[] Gyro1GetRate();//1097

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int updateRate
     * Return parameters:void
     * Size of returned parameters: 0
     * Set update interval of gyro
     * UI32: interval in [msec]
     * </pre>
     */
    void Gyro1SetUpdateInterval(int updateRate);//1098

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable/ Disable Bias
     * Removement
     * I8:
     * 0 - Off
     * 1 - On
     * </pre>
     */
    void Gyro1RemoveBias();//1099

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * Enable Bias calculation on iADCS (averaging)
     * I32: number of values to average
     * </pre>
     */
    byte[] Gyro1GetBias();//1100

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte updateRate,int allowedDeviation
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable/ Disable Gyro Filter 1
     * (averaging filter)
     * UI8:
     * 0 - Filter Off
     * 1 - Filter On
     * F32: allowed deviation in [rad/s] between 2 gyro values to avoid value-jump
     * </pre>
     */
    void Gyro1SetFilter1(byte updateRate, int allowedDeviation);//1101

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] calibrationValues
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Calibration Parameters
     * 9x F32 - Calibration matrix
     * 3x F32 - Calibration Offset
     * </pre>
     */
    void Gyro1SetCalibrationParameters(int[] calibrationValues);//1102

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 48
     * Get Calibration Parameters
     * 9x F32 - Calibration matrix
     * 3x F32 - Calibration Offset
     * </pre>
     */
    byte[] Gyro1GetCalibrationParameters();//1103

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable Calibration
     * </pre>
     */
    void Gyro1EnableCalibration();//1104

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Disable Calibration
     * </pre>
     */
    void Gyro1DisableCalibration();//1105

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] quaternionValues
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Quaternion from Sun Sensor for Gyro
     * </pre>
     */
    void Gyro1SetQuaternionFromSunSensor(float[] quaternionValues);//1106

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 16
     * Get Quaternion from Sun Sensor for Gyro
     * </pre>
     */
    byte[] Gyro1GetQuaternionFromSunSensor();//1107

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Values of Accelerometer [in HIL Mode]
     * 3x F32: values for 3 axis in [m/s^2]
     * </pre>
     */
    void accelerometerSetValues(float[] values);//1108

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 12
     * Get Values of Accelerometer
     * 3x F32: values of 3 axis in [m/s^2]
     * </pre>
     */
    byte[] accelerometerGetValues();//1109

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int interval
     * Return parameters:void
     * Size of returned parameters: 0
     * Set update interval
     * UI32: interval in [msec]
     * </pre>
     */
    void accelerometerReadInterval(int interval);//1110

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Value on Magnetometer [in HIL Mode]
     * 3x F32: value on 3 axis in [T]
     * </pre>
     */
    void magnetometerSetMagneticField(float[] values);//1111

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 12
     * Get Value on Magnetometer
     * 3x F32: value of 3 axis in [T]
     * </pre>
     */
    byte[] magnetometerGetMagneticField();//1112

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int interval
     * Return parameters:void
     * Size of returned parameters: 0
     * Set update interval of Magnetometer
     * UI32: interval in [msec]
     * </pre>
     */
    void magnetometerSetUpdateInterval(int interval);//1113

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Calibration Parameters
     * 9x F32 - Calibration matrix
     * 3x F32 - Calibration Offset
     * </pre>
     */
    void accelerometerSetCalibrationParams(float[] values);//1114

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 48
     * Get Calibration Parameters
     * 9x F32 - Calibration matrix
     * 3x F32 - Calibration Offset
     * </pre>
     */
    byte[] accelerometerGetCalibrationParams();//1115

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable Calibration
     * </pre>
     */
    void accelerometerEnableCalibration();//1116

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Disable Calibration
     * </pre>
     */
    void accelerometerDisableCalibration();//1117

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] quaternionValues
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Quaternion from Sun Sensor for Compass
     * </pre>
     */
    void accelerometerSetQuaternionFromSunSensor(float[] quaternionValues);//1118

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 16
     * Get Quaternion from Sun Sensor for Compass
     * </pre>
     */
    byte[] accelerometerGetQuaternionFromSunSensor();//1119

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int requestRegister
     * Return parameters:byte[]
     * Size of returned parameters: 68
     * Get Kalman 2 Telemetry:
     * Unselected items return zeros
     * UI32: Type
     * 0 - Measured Gyro;
     * Data:
     * 3xUI32 Gyro Telemetry
     * 2 - Estimated Bias;
     * Data:
     * 3x UI32 : zeros;
     * 3x UI32 : Estimated Bias
     * 4 - Angle Velocity;
     * Data:
     * 6x UI32: zeros;
     * 3x UI32 Angle Velocity;
     * 8 - ST Quaternion;
     * Data
     * 9x UI32 : zeros;
     * 4x UI32 ST Telemetry
     * 10 - Kalman Quaternion
     * Data:
     * 13x UI32 zeros;
     * 4xUI32 Kalman Quaternion
     * </pre>
     */
    byte[] kalman2FilterGetTelemetry(int requestRegister);//1120

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte selectGyro
     * Return parameters:void
     * Size of returned parameters: 0
     * Select gyro to be used by
     * Kalman 2 Filter
     * UI8:
     * 0 - Gyro 1
     * 1 - Gyro 2
     * </pre>
     */
    void kalman2FilterSelectGyro(byte selectGyro);//1121

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable Kalman 2 Filtering
     * </pre>
     */
    void kalman2FilterStart();//1122

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Disable Kalman 2 Filtering
     * </pre>
     */
    void kalman2FilterStop();//1123

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int requestRegister
     * Return parameters:byte[]
     * Size of returned parameters: 68
     * Get Kalman 4 Telemetry:
     * N_data = 68
     * Unselected items return
     * zeroes.
     * See: 5.5.27.9
     * UI32: register of selected
     * values
     * see: 5.5.27.9
     * </pre>
     */
    byte[] kalman4FilterGetTelemetry(int requestRegister);//1124

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte selectGyro
     * Return parameters:void
     * Size of returned parameters: 0
     * Select gyro to be used by
     * Kalman 4 Filter
     * UI8:
     * 0 - Gyro 1
     * 1 - Gyro 2
     * </pre>
     */
    void kalman4FilterSelectGyro(byte selectGyro);//1125

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable Kalman 2 Filtering
     * </pre>
     */
    void kalman4FilterStart();//1126

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Disable Kalman 2 Filtering
     * </pre>
     */
    void kalman4FilterStop();//1127

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int interval
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Quaternion from Sun Sensor for Compass
     * </pre>
     */
    void controlLoopsSetUpdateInterval(int interval);//1128

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 6
     * Get target rw speed commanding by controller
     * 3x I16: Target Speeds
     * </pre>
     */
    byte[] controlLoopsGetTargetRWSpeed();//1129

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 6
     * Get target mtq values commanding by controller
     * 3x I16: Target dipole moment
     * </pre>
     */
    byte[] controlLoopsGetTargetMTWDipoleMoment3D();//1130

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 24
     * Get Status of control group
     * 1x UI32: General status
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * Error:
     * Bit = 1 : Yes
     * Bit = 0 : No
     * 0 CTRL_FLAG_CS_IDLE Idle Status Of Control Module
     * 1 CTRL_FLAG_CS_SAC_MODE Single Axis Mode Status
     * 2 CTRL_FLAG_CS_AAC_MODE All Axis Mode Status
     * 31 CTRL_ERR_FLAG_GENERAL Error of Control Module
     * 1x UI32: Error Status;
     * 1x UI32: All Axis Control
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * 0 MODE_BDOT_1 Bdot control using algorithm 1
     * 1 MODE_BDOT_2 Bdot control using algorithm 2
     * 2 MODE_SUN_POINTING Sun pointing mode
     * 3 MODE_SSM Single spinning mode
     * 3x UI32: Single Axis Control
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * 0 MODE_ANGLE_PI Angle Control Using PI Controller, TBD
     * 1 MODE_ANGLE_DCT Angle Control Using Discrete Time Controller
     * 2 MODE_ANGLE_CASD_IN Angle Control Using Cascading Controller
     * [Inner Loop]
     * 3 MODE_ANGLE_CASD_OUT Angle Control Using Cascading Controller
     * [Outer Loop]
     * 10 MODE_ANGVEL_PI Angular Velocity Control Using PI Controller
     * 11 MODE_ANGVEL_DCT Angular Velocity Control Using Discrete Time
     * Controller
     * 31 USE_GYRO_2 Use Gyro 2 for Controlling: Default Setting
     * </pre>
     */
    byte[] controlLoopsGetStatus();//1131

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte axis,int controlRegister,float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Anti Windup Angle
     * Parameters
     * 1x UI8: Axis;
     * 1x UI32: Control Register;
     * 1x F32: Threshold value;
     * 1x F32 Multiplication factor
     * </pre>
     */
    void controlLoopsSetAntiWindup(byte axis, int controlRegister, float[] values);//1132

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte axis,int controlRegister
     * Return parameters:byte[]
     * Size of returned parameters: 16
     * Get Anti Windup Angle
     * Parameters
     * Send :
     * 1x UI8: Axis;
     * 1x UI32: Control Register;
     * Receive:
     * PID, Angle Velocity and Angle
     * DCT controllers:
     * 1x F32: Threshold value;
     * 1x F32: Multiplication
     * factor
     * Cascade Control:
     * 1x F32: In loop Threshold;
     * 1x F32: In loop
     * Multiplication factor;
     * 1x F32: Out loop Threshold;
     * 1x F32: Out loop
     * Multiplication factor
     * </pre>
     */
    byte[] controlLoopsGetAntiWindup(byte axis, int controlRegister);//1133

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte axis,int controlRegister,float[] targetAngle
     * Return parameters:void
     * Size of returned parameters: 0
     * Start Control Loop
     * UI8: Axis;
     * UI32: Control Register
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * 0 MODE_ANGLE_PI Angle Control Using PI Controller, TBD
     * 1 MODE_ANGLE_DCT Angle Control Using Discrete Time Controller
     * 2 MODE_ANGLE_CASD_IN Angle Control Using Cascading Controller
     * [Inner Loop]
     * 3 MODE_ANGLE_CASD_OUT Angle Control Using Cascading Controller
     * [Outer Loop]
     * 10 MODE_ANGVEL_PI Angular Velocity Control Using PI Controller
     * 11 MODE_ANGVEL_DCT Angular Velocity Control Using Discrete Time
     * Controller
     * 31 USE_GYRO_2 Use Gyro 2 for Controlling: Default Setting
     * F32: Target Angle [deg]
     * </pre>
     */
    void singleAxisStartControlLoop(byte axis, int controlRegister, float[] targetAngle);//1134

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte axis
     * Return parameters:void
     * Size of returned parameters: 0
     * Stop Control Loop
     * UI8: Axis
     * </pre>
     */
    void singleAxisStopControlLoop(byte axis);//1135

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte axis,int controlRegister,float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Control Parameters:
     * DCT Control:
     * UI8: Axis;
     * UI32: Control Register
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * 0 MODE_ANGLE_PI Angle Control Using PI Controller, TBD
     * 1 MODE_ANGLE_DCT Angle Control Using Discrete Time Controller
     * 2 MODE_ANGLE_CASD_IN Angle Control Using Cascading Controller
     * [Inner Loop]
     * 3 MODE_ANGLE_CASD_OUT Angle Control Using Cascading Controller
     * [Outer Loop]
     * 10 MODE_ANGVEL_PI Angular Velocity Control Using PI Controller
     * 11 MODE_ANGVEL_DCT Angular Velocity Control Using Discrete Time
     * Controller
     * 31 USE_GYRO_2 Use Gyro 2 for Controlling: Default Setting
     * F32: Target Angle;
     * 3x F32: Applied Parameters
     * Cascade Control:
     * UI8: Axis;
     * UI32: Control Register
     * F32: Target Angle;
     * 6x F32: Applied Parameters
     * </pre>
     */
    void singleAxisSetParameter(byte axis, int controlRegister, float[] values);//1136

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte axis,int controlRegister
     * Return parameters:byte[]
     * Size of returned parameters: 28
     * Get control parameters
     * UI8: Axis;
     * UI32: Control Register
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * 0 MODE_ANGLE_PI Angle Control Using PI Controller, TBD
     * 1 MODE_ANGLE_DCT Angle Control Using Discrete Time Controller
     * 2 MODE_ANGLE_CASD_IN Angle Control Using Cascading Controller
     * [Inner Loop]
     * 3 MODE_ANGLE_CASD_OUT Angle Control Using Cascading Controller
     * [Outer Loop]
     * 10 MODE_ANGVEL_PI Angular Velocity Control Using PI Controller
     * 11 MODE_ANGVEL_DCT Angular Velocity Control Using Discrete Time
     * Controller
     * 31 USE_GYRO_2 Use Gyro 2 for Controlling: Default Setting
     * Data:
     * DCT Control:
     * F32: Target Angle;
     * 3x F32: Applied Parameters
     * Cascade Control:
     * F32: Target Angle;
     * 6x F32: Applied Parameters
     * </pre>
     */
    byte[] singleAxisGetParameter(byte axis, int controlRegister);//1137

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte axis,int controlRegister
     * Return parameters:void
     * Size of returned parameters: 0
     * Reset control parameters to
     * default
     * UI8: Axis;
     * UI32: Control Register
     * Bit
     * No
     * Description Comments
     * Status:
     * Bit = 1 : Enable
     * Bit = 0 : Disable
     * 0 MODE_ANGLE_PI Angle Control Using PI Controller, TBD
     * 1 MODE_ANGLE_DCT Angle Control Using Discrete Time Controller
     * 2 MODE_ANGLE_CASD_IN Angle Control Using Cascading Controller
     * [Inner Loop]
     * 3 MODE_ANGLE_CASD_OUT Angle Control Using Cascading Controller
     * [Outer Loop]
     * 10 MODE_ANGVEL_PI Angular Velocity Control Using PI Controller
     * 11 MODE_ANGVEL_DCT Angular Velocity Control Using Discrete Time
     * Controller
     * 31 USE_GYRO_2 Use Gyro 2 for Controlling: Default Setting
     * Data:
     * F32: Target Angle;
     * 3x F32: Resetted Paramerters of Selected Controller
     * </pre>
     */
    void singleAxisResetParameter(byte axis, int controlRegister);//1138

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] targetSunVector
     * Return parameters:void
     * Size of returned parameters: 0
     * Start Control Loop with given target sun vector
     * F32: X - value;
     * F32: Y - value;
     * F32: Z - value;
     * </pre>
     */
    void sunPointingStartControlLoop(float[] targetSunVector);//1139

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Stop Control Loop
     * </pre>
     */
    void sunPointingStopControlLoop();//1140

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Control Parameters
     * F32: Gain Kp;
     * F32: Gain Kv[1][1];
     * F32: Gain Kv[1][2];
     * F32: Gain Kv[1][3];
     * F32: Gain Kv[2][2];
     * F32: Gain Kv[2][3];
     * F32: Gain Kv[0][3];
     * Kv is a symmetric 3x3 matrix
     * </pre>
     */
    void sunPointingSetParameter(float[] values);//1141

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 40
     * Get controller parameters
     * 3xF32: Target sun vector;
     * 1xF32: Controller gain Kp;
     * 6xF32: Controller gain Kv,
     * Kv is a symmetric 3x3 matrix
     * </pre>
     */
    byte[] sunPointingGetParameter();//1142

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Reset control parameters to default
     * </pre>
     */
    void sunPointingResetParameter();//1143

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte controller
     * Return parameters:void
     * Size of returned parameters: 0
     * Start Control Loop
     * UI8:
     * 0 - Bdot Controller 1
     * 1 - Bdot Controller 2
     * </pre>
     */
    void bdotStartControlLoop(byte controller);//1144

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Stop Control Loop
     * </pre>
     */
    void bdotStopControlLoop();//1145

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float gain
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Control Parameters [only for Bdot Controller 1]
     * F32: Controller gain
     * </pre>
     */
    void bdotSetParameter(float gain);//1146

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * Get controller gain of Bdot
     * Controller 1
     * F32: Controller gain
     * </pre>
     */
    byte[] bdotGetParameter();//1147

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Reset control parameters to default
     * </pre>
     */
    void bdotResetParameter();//1148

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] targetBodyAxis,float targetAngularVelocityMagnitude,float[] inertialTargetVector
     * Return parameters:void
     * Size of returned parameters: 0
     * Start single spinning mode with given targets
     * - Target body axis
     * F32: X - value;
     * F32: Y - value;
     * F32: Z - value;
     * - Target angular velocity magnitude;
     * F32: Target a. velocity magnitude;
     * - Inertial target vector
     * F32: X - value;
     * F32: Y - value;
     * </pre>
     */
    void singleSpinningStartControlLoop(float[] targetBodyAxis, float targetAngularVelocityMagnitude,
        float[] inertialTargetVector);//1149

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Stop Control Loop
     * </pre>
     */
    void singleSpinningStopControlLoop();//1150

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Control Parameters
     * F32: Gain K;
     * F32: Gain K1;
     * F32: Gain K2;
     * F32: Gain K3;
     * F32: Angle Velocity Margin;
     * F32: Angle b Target - Margin;
     * F32: Angle i Target - Margin
     * </pre>
     */
    void singleSpinningSetParameter(float[] values);//1151

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 52
     * Get controller parameters
     * 3x F32: Target body axis;
     * 1x F32: Target angular velocity magnitude;
     * 3x F32: Inertial Target Axis;
     * 3x F32: Controller gains;
     * 3x F32: Controller Margins
     * </pre>
     */
    byte[] singleSpinningGetParameter();//1152

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte modeType,float[] values,long[] times
     * Return parameters:void
     * Size of returned parameters: 0
     * Start target tracking mode:
     * UI8 Type:
     * 0 - Constant Velocity:
     * 4x F32: Start Quaternion;
     * 4x F32: End Quaternion;
     * 1x UI64: Start Time
     * 1x UI64: End Time
     * </pre>
     */
    void targetTrackingStartModeConstantVel(byte modeType, float[] values, long[] times);//1153

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte modeType,float[] values,long[] times
     * Return parameters:void
     * Size of returned parameters: 0
     * Start target tracking mode:
     * UI8 Type:
     * 2 - General
     * 12x F32 Poynomials;
     * 1x UI64: Start Time;
     * 1x UI64: End Time
     * </pre>
     */
    void targetTrackingStartModeGeneral(byte modeType, float[] values, long[] times);//1154

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte modeType,float[] values,long[] times
     * Return parameters:void
     * Size of returned parameters: 0
     * Start target tracking mode:
     * UI8 Type:
     * 3 - Fix WGS84;
     * 1x F32 : Latitude;
     * 1x F32 : Longitude;
     * 1x F32 : Altitude;
     * 1x UI64: Start Time;
     * 1x UI64: End Time
     * </pre>
     */
    void targetTrackingStartModeWGS84(byte modeType, float[] values, long[] times);//1155

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Stop target tracking mode
     * </pre>
     */
    void targetTrackingStopMode();//1156

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set target tracking parameters:
     * 1x F32: Gain K
     * 1x F32: Gain K1
     * </pre>
     */
    void targetTrackingSetParameters(float[] values);//1157

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 73
     * Get target tracking
     * parameters:
     * UI8 Type:
     * 0 - Constante Velocity
     * Data:
     * 1x F32: Gain K;
     * 1x F32: Gain K1;
     * 1x U8: Track Mode;
     * 4x F32: Start Quaternion ;
     * 4x F32: End Quaternion;
     * 1x UI64: Start Time;
     * 1x UI64: End Time
     * 2 - General
     * Data
     * 1x F32: Gain K;
     * 1x F32: Gain K1;
     * 1x U8: Track Mode;
     * 12x F32: Poynomials History;
     * 1x UI64: Start Time;
     * 1x UI64: End Time
     * Get target tracking
     * parameters:
     * UI8 Type:
     * 3 - Fix WGS84;
     * Data
     * 1x F32: Gain K;
     * 1x F32: Gain K1;
     * 1x U8: Track Mode;
     * 1x F32: Latitude;
     * 1x F32: Longitude;
     * 1x F32: Altitude;
     * 1x UI64: Start Time;
     * 1x UI64: End Time
     * </pre>
     */
    byte[] targetTrackingGetParameters();//1158

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Target to initial parameters
     * </pre>
     */
    void targetTrackingResetParameters();//1159

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Set orbital r,v + epoch time
     * </pre>
     */
    void orbitSetRV();//1160

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 32
     * Get orbital r, v , JDUT
     * 3x F32 : r [m/s]
     * 3x F32 : v [km/s]
     * 1x F64 : JDUT [days]
     * </pre>
     */
    byte[] orbitGetRV();//1161

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte[] tleData
     * Return parameters:void
     * Size of returned parameters: 0
     * TLE Data
     * 69x UI8 : Line 1
     * UI8 : Empty Character '\0'
     * 69x UI8 : Line 2
     * UI8 : Empty Character '\0'
     * </pre>
     */
    void orbitSetTLE(byte[] tleData);//1162

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int updateInterval
     * Return parameters:byte[]
     * Size of returned parameters: 32
     * UI32: Update Interval in [msec]
     * </pre>
     */
    byte[] orbitSetUpdateInterval(int updateInterval);//1163

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Same as cmd 'Set Mode: Safe' [0x02]
     * </pre>
     */
    void opModeIdle();//1164

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'Safe Mode'. All sensor polling off. All actuators off. No controlling.
     * </pre>
     */
    void opModeSafe();//1165

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'Measurement Mode'. All sensor polling ON. All actuators off. No controlling.
     * </pre>
     */
    void opModeMeasure();//1166

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte start,long[] times
     * Return parameters:void
     * Size of returned parameters: 0
     * Set the 'Detumbling Mode'. Enables 'B-dot Max Control Loop' based on magnetometer and magnetorquers.
     * Turns off all other unnecessary activities.
     * Parameters:
     * 1x UI8 Start (=1) or Stop (=0)
     * 2x UI64 Start and Stop Time [msec]
     * </pre>
     */
    void opModeDetumble(byte start, long[] times);//1167

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte[] mode,long[] times,float[] targetSunVector
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'Sun Pointing Mode'. Enables 'Sun Pointing Control Loop' based on RW and sun sensors.
     * Turns off all other unnecessary activities.
     * Parameters:
     * 1x UI8 Start (=1) or Stop (=0)
     * 2x UI64 Start and Stop Time [msec]
     * 1x UI8 Actuator Mode (only 0=RW)
     * 3x F32 Target Sun Vector [body]
     * </pre>
     */
    void opModeSunPointing(byte[] mode, long[] times, float[] targetSunVector);//1168

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 25
     * Get parameters for sun
     * pointing:
     * 3x F32 Sun Vector [body frame]
     * 1x UI8 Sun Vector Valid Flag
     * 3x F32 Actuator Values [body frame]
     * (either RW Speed [rpm] or MTQ Dipole Moment [Am^2])
     * </pre>
     */
    byte[] opModeGetSunPointingStatus();//1169

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte mode,long[] times,float[] targetVector
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'Single Spinner Mode'.
     * Enables 'SSMC Control Loop' based on MTQ, HP-gyro, magnetometer and sun sensors.
     * Turns off all other unnecessary activities.
     * Parameters:
     * 1x UI8 Start (=1) or Stop (=0)
     * 2x UI64 Start and Stop Time [msec]
     * 3x F32 Tgt. Ang. Mom. Vec. [body]
     * 3x F32 Tgt. Ang. Mom. Vec. [inertial]
     * 1x F32 Tgt. Ang. Vel. [rad/sec]
     * </pre>
     */
    void opModeSetModeSpin(byte mode, long[] times, float[] targetVector);//1170

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 64
     * Get parameters for spin mode:
     * 3x F32 Sun Vector [body frame]
     * 3x F32 Mag. Field [body frame]
     * 4x F32 Quaternion
     * 3x F32 Ang. Momentum [body]
     * 3x F32 MTQ Dipole Moment [Am^2]
     * </pre>
     */
    byte[] opModeGetSpinModeStatus();//1171

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte mode,long[] times,float[] targetVector
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'Contant Velocity Target Tracking Mode'.
     * Enables 'Control Loop' based on RW, HP-gyro, ST, Kalman Filter.
     * Turns off all other unnecessary activities.
     * Parameters:
     * 1x UI8 Start (=1) or Stop (=0)
     * 2x UI64 Start and Stop Time [msec]
     * 4x F32 Start Quaternion
     * 4x F32 Stop Quaternion
     * Note: Used for Nadir Pointing
     * </pre>
     */
    void opModeSetTargetTrackingCVelocity(byte mode, long[] times, float[] targetVector);//1172

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 56
     * Get parameters for tgt. mode:
     * 3x F32 Ang. Vel. [body frame]
     * 4x F32 Quaternion
     * 4x F32 Target Quaternion
     * 3x F32 RW Speed [rpm]
     * </pre>
     */
    byte[] opModeGetTargetTrackingCVelocityStatus();//1173

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte mode,long[] times
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'Nadir Dynamic Target Tracking Mode'.
     * Enables 'Control Loop' based on RW, HP-gyro, ST, Kalman Filter.
     * Turns off all other unnecessary activities.
     * Parameters:
     * 1x UI8 Start (=1) or Stop (=0)
     * 2x UI64 Start and Stop Time [msec]
     * Note: Computational Extensive – Proper Operation TBD
     * </pre>
     */
    void opModeSetNadirTargetTracking(byte mode, long[] times);//1174

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 68
     * Get parameters for tgt. mode:
     * 3x F32 Position Vector [WGS84]
     * 3x F32 Ang. Vel. [body frame]
     * 4x F32 Quaternion
     * 4x F32 Target Quaternion
     * 3x F32 RW Speed [rpm]
     * </pre>
     */
    byte[] opModeGetNadirTargetTrackingStatus();//1175

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte mode,long[] times,float[] quaternionCoefficients
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'Std. Target Tracking Mode'.
     * Enables 'Control Loop' based on RW, HP-gyro, ST, Kalman Filter.
     * Turns off all other unnecessary activities.
     * Parameters:
     * 1x UI8 Start (=1) or Stop (=0)
     * 2x UI64 Start and Stop Time [msec]
     * 12x F32 Quaternion Coeffcients
     * </pre>
     */
    void opModeSetStandardTargetTracking(byte mode, long[] times, float[] quaternionCoefficients);//1176

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 56
     * Get parameters for tgt. mode:
     * 3x F32 Ang. Vel. [body frame]
     * 4x F32 Quaternion
     * 4x F32 Target Quaternion
     * 3x F32 RW Speed [rpm]
     * </pre>
     */
    byte[] opModeGetStandardTargetTrackingStatus();//1177

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte mode,long[] times,float[] latitudeLongitude
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'Fix WGS84 Target Tracking Mode'.
     * Enables 'Control Loop' based on RW, HP-gyro, ST, Kalman Filter.
     * Turns off all other unnecessary activities.
     * Parameters:
     * 1x UI8 Start (=1) or Stop (=0)
     * 2x UI64 Start and Stop Time [msec]
     * 1x F32 Latitude [rad]
     * 1x F32 Longitude [rad]
     * Note: Computational Extensive – Proper Operation TBD
     * </pre>
     */
    void opModeSetFixWGS84TargetTracking(byte mode, long[] times, float[] latitudeLongitude);//1178

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 68
     * Get parameters for tgt. mode:
     * 3x F32 Position Vector [WGS84]
     * 3x F32 Ang. Vel. [body frame]
     * 4x F32 Quaternion
     * 4x F32 Target Quaternion
     * 3x F32 RW Speed [rpm]
     * </pre>
     */
    byte[] opModeGetFixWGS84TargetTracking();//1179

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte mode,long startTime,float[] data
     * Return parameters:void
     * Size of returned parameters: 0
     * Sets the 'HL Target Capture 1 Mode'.
     * Enables 'Control Loop' based on RW, HP-gyro, Kalman Filter and ST or S/B-Sensors. Uses simple euler rotations for tracking.
     * Turns off all other unnecessary activities.
     * Parameter:
     * UI8 Mode
     * 1x F32 Latitude [rad]
     * 1x F32 Longitude [rad]
     * 1x UI64 Start Time
     * 4x F32 Inertial Target
     * Quaternion
     * </pre>
     */
    void opModeSetTargetCapture1(byte mode, long startTime, float[] data);//1180

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 22
     * Get parameters for tgt. mode:
     * UI32 Status Register
     * UI32 Single Axis Control Reg. X
     * UI32 Single Axis Control Reg. Y
     * UI32 Single Axis Control Reg. Z
     * UI32 All Axis Control Register
     * UI8 State Counter
     * UI8 State Target
     * </pre>
     */
    byte[] opModeGetTargetCapture1();//1181

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:String tleLine1,String tleLine2
     * Return parameters:byte[]
     * Size of returned parameters: 140
     * Input parameters:
     * tleLine1
     * tleLine2
     * Return: byte array of TLE data
     * </pre>
     */
    byte[] simGetOrbitTLEBytesFromString(String tleLine1, String tleLine2);//1182

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:byte[] data,int byteOffset
     * Return parameters:float
     * Size of returned parameters: 1
     * Input parameters:
     * data - the byte array, should be at least 4 bytes long
     * byteOffset - zero-based index, should be a multiple of four
     * Return: float value of data inside byte array, at the given byte offset
     * </pre>
     */
    float simGetFloatFromByteArray(byte[] data, int byteOffset);//1183

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:float data
     * Return parameters:byte[]
     * Size of returned parameters: 1
     * Input parameters:
     * data - the float value
     * Return: byte array corresponding to the float value
     * </pre>
     */
    byte[] simGetByteArrayFromFloat(float data);//1184

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:byte[] data,int byteOffset
     * Return parameters:double
     * Size of returned parameters: 1
     * Input parameters:
     * data - the byte array, should be at least 8 bytes long
     * byteOffset - zero-based index, should be a multiple of eight
     * Return: float value of data inside byte array, at the given byte offset
     * </pre>
     */
    double simGetDoubleFromByteArray(byte[] data, int byteOffset);//1185

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:double data
     * Return parameters:byte[]
     * Size of returned parameters: 8
     * Input parameters:
     * data - the double value
     * Return: byte array corresponding to the double value
     * </pre>
     */
    byte[] simGetByteArrayFromDouble(double data);//1186

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:byte[] data,int byteOffset
     * Return parameters:int
     * Size of returned parameters: 1
     * Input parameters:
     * data - the byte array, should be at least 4 bytes long
     * byteOffset - zero-based index, should be a multiple of four
     * Return: int value of data inside byte array, at the given byte offset
     * </pre>
     */
    int simGetIntFromByteArray(byte[] data, int byteOffset);//1187

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:int data
     * Return parameters:byte[]
     * Size of returned parameters: 8
     * Input parameters:
     * data - the int value
     * Return: byte array corresponding to the int value
     * </pre>
     */
    byte[] simGetByteArrayFromInt(int data);//1188

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:byte[] data,long byteOffset
     * Return parameters:long
     * Size of returned parameters: 1
     * Input parameters:
     * data - the byte array, should be at least 8 bytes long
     * byteOffset - zero-based index, should be a multiple of eight
     * Return: long value of data inside byte array, at the given byte offset
     * </pre>
     */
    long simGetLongFromByteArray(byte[] data, int byteOffset);//1189

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:long data
     * Return parameters:byte[]
     * Size of returned parameters: 8
     * Input parameters:
     * data - the long value
     * Return: byte array corresponding to the long value
     * </pre>
     */
    byte[] simGetByteArrayFromLong(long data);//1190

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] values
     * Return parameters:void
     * Size of returned parameters: 0
     * Set gyro rate on 3 axis [in
     * HIL mode]
     * 3x F32: rates on 3 axis in
     * [rad/s]
     * </pre>
     */
    void Gyro2SetRate(float[] values);//1191

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 20
     * Get gyro rate on 3 axis
     * 3x F32 : rate on 3 axis in [rad/s]
     * 1xUI64: timestamp of gyro reading
     * </pre>
     */
    byte[] Gyro2GetRate();//1192

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int updateRate
     * Return parameters:void
     * Size of returned parameters: 0
     * Set update interval of gyro
     * UI32: interval in [msec]
     * </pre>
     */
    void Gyro2SetUpdateInterval(int updateRate);//1193

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable/ Disable Bias
     * Removement
     * I8:
     * 0 - Off
     * 1 - On
     * </pre>
     */
    void Gyro2RemoveBias();//1194

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 4
     * Enable Bias calculation on iADCS (averaging)
     * I32: number of values to average
     * </pre>
     */
    byte[] Gyro2GetBias();//1195

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:byte updateRate,int allowedDeviation
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable/ Disable Gyro Filter 1
     * (averaging filter)
     * UI8:
     * 0 - Filter Off
     * 1 - Filter On
     * F32: allowed deviation in [rad/s] between 2 gyro values to avoid value-jump
     * </pre>
     */
    void Gyro2SetFilter1(byte updateRate, int allowedDeviation);//1196

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:int[] calibrationValues
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Calibration Parameters
     * 9x F32 - Calibration matrix
     * 3x F32 - Calibration Offset
     * </pre>
     */
    void Gyro2SetCalibrationParameters(int[] calibrationValues);//1197

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 48
     * Get Calibration Parameters
     * 9x F32 - Calibration matrix
     * 3x F32 - Calibration Offset
     * </pre>
     */
    byte[] Gyro2GetCalibrationParameters();//1198

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Enable Calibration
     * </pre>
     */
    void Gyro2EnableCalibration();//1199

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:void
     * Size of returned parameters: 0
     * Disable Calibration
     * </pre>
     */
    void Gyro2DisableCalibration();//1200

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:float[] quaternionValues
     * Return parameters:void
     * Size of returned parameters: 0
     * Set Quaternion from Sun Sensor for Gyro
     * </pre>
     */
    void Gyro2SetQuaternionFromSunSensor(float[] quaternionValues);//1201

    /**
     * <pre>
     * High level command to interact with FineADCS
     * Input parameters:
     * Return parameters:byte[]
     * Size of returned parameters: 16
     * Get Quaternion from Sun Sensor for Gyro
     * </pre>
     */
    byte[] Gyro2GetQuaternionFromSunSensor();//1202

    /**
     * <pre>
     * Test command for the helper libraries
     * Input parameters:byte[] data,int byteOffset
     * Return parameters:int
     * Size of returned parameters: 1
     * Input parameters:
     * data - the byte array, should be at least 2 bytes long
     * byteOffset - zero-based index, should be a multiple of two
     * Return: int value of data inside byte array, at the given byte offset
     * </pre>
     */
    int simGetInt16FromByteArray(byte[] data, int byteOffset);//1203

    /**
     * <pre>
     * Generic for loading restricted subsystems
     * Input parameters:String data
     * Return parameters:void
     * Size of returned parameters: 0
     * Input parameters:
     * data - the string which contains the name of the command with optional arguments
     * </pre>
     */
    void simRunDeviceCommand(String data);//1204

}
