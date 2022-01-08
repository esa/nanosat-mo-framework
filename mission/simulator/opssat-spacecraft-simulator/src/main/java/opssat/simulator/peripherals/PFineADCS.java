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


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import opssat.simulator.threading.SimulatorNode;
import opssat.simulator.interfaces.IFineADCS;
import opssat.simulator.interfaces.ISimulatorDeviceData;
import opssat.simulator.interfaces.InternalData;

/**
 *
 * @author Cezar Suteu
 */
@ISimulatorDeviceData(
    descriptors={
        "String:modeOperation",
        "float[3]:positionInertial [km]",
        "float[3]:velocityInertial [km/s]",
        "double:q1",
        "double:q2",
        "double:q3",
        "double:q4",
        "String:magneticField",
        "String:rotation",
        "String:magnetometer",
        "String:sunVector",
        "int[3]:reactionWheels",
        "float[3]:accelerometer",
        "float[3]:gyro1",
        "float[3]:gyro2",
        "int[3]:magnetorquer",
        "float[3]:angularMomentum",
        "float[3]:angularVelocity"
    })
public class PFineADCS extends GenericPeripheral implements IFineADCS {

    public static final int DOUBLE_BYTES = Double.SIZE / Byte.SIZE;
    public static final int FLOAT_BYTES = Float.SIZE / Byte.SIZE;
    public static final int INTEGER_BYTES = Integer.SIZE / Byte.SIZE;
    public static final int LONG_BYTES = Long.SIZE / Byte.SIZE;

    public static class FWRefFineADCS {
        //Below are indexes to the fields in the resulting byte arrays
        public static class SENSORTM_IDX{
            //Byte offset
            public final static int MAG_FIELD_X                      = 0*4;
            public final static int MAG_FIELD_Y                      = 1*4;
            public final static int MAG_FIELD_Z                      = 2*4;
            public final static int ACCELEROMETER_X                  = 3*4;
            public final static int ACCELEROMETER_Y                  = 4*4;
            public final static int ACCELEROMETER_Z                  = 5*4;
            public final static int GYRO1_X                          = 6*4;
            public final static int GYRO1_Y                          = 7*4;
            public final static int GYRO1_Z                          = 8*4;
            public final static int GYRO2_X                          = 9*4;
            public final static int GYRO2_Y                          = 10*4;
            public final static int GYRO2_Z                          = 11*4;
            public final static int ST200_TIME                       = 12*4;//UI64
            public final static int ST200_TIME_MSEC                  = 14*4;//UI16
            public final static int QUATERNION1                      = 14*4+2;
            public final static int QUATERNION2                      = 15*4+2;
            public final static int QUATERNION3                      = 16*4+2;
            public final static int QUATERNION4                      = 17*4+2;
        }
        public static class ACTUATORTM_IDX{
            //Byte offset
            public final static int RW_CURRENT_SPEED_X               = 0*2;
            public final static int RW_LAST_TARGET_X                 = 1*2;
            public final static int RW_TARGET_MODE_X                 = 2*2;
            public final static int RW_CURRENT_SPEED_Y               = 2*2+1;
            public final static int RW_LAST_TARGET_Y                 = 3*2+1;
            public final static int RW_TARGET_MODE_Y                 = 4*2+1;
            public final static int RW_CURRENT_SPEED_Z               = 5*2;
            public final static int RW_LAST_TARGET_Z                 = 6*2;
            public final static int RW_TARGET_MODE_Z                 = 7*2;
            public final static int MTQ_TARGET_X                     = 7*2+1;
            public final static int MTQ_TARGET_Y                     = 8*2+1;
            public final static int MTQ_TARGET_Z                     = 9*2+1;
        }
        public static class SPINMODESTAT_IDX{
            //Byte offset
            public final static int SUN_VECTOR_X                     = 0*4;
            public final static int SUN_VECTOR_Y                     = 1*4;
            public final static int SUN_VECTOR_Z                     = 2*4;
            public final static int MAGNETOMETER_X                   = 3*4;
            public final static int MAGNETOMETER_Y                   = 4*4;
            public final static int MAGNETOMETER_Z                   = 5*4;
            public final static int Q1                               = 6*4;
            public final static int Q2                               = 7*4;
            public final static int Q3                               = 8*4;
            public final static int Q4                               = 9*4;
            public final static int ANG_MOM_X                        = 10*4;
            public final static int ANG_MOM_Y                        = 11*4;
            public final static int ANG_MOM_Z                        = 12*4;
            public final static int MTQ_DIP_MOMENT_X                 = 13*4;
            public final static int MTQ_DIP_MOMENT_Y                 = 14*4;
            public final static int MTQ_DIP_MOMENT_Z                 = 15*4;
        }
        public static class SUNPOINTSTAT_IDX{
            //Byte offset
            public final static int SUN_VECTOR_X                     = 0*4;
            public final static int SUN_VECTOR_Y                     = 1*4;
            public final static int SUN_VECTOR_Z                     = 2*4;
            public final static int SUN_VECTOR_VALID                 = 3*4;
            public final static int ACTUATOR_X                       = 3*4+1;
            public final static int ACTUATOR_Y                       = 4*4+1;
            public final static int ACTUATOR_Z                       = 5*4+1;
        }
        public static class FIXWGS84_TGTTRACKSTAT_IDX{
            //Byte offset
            public final static int POSITION_VECTOR_X                = 0*4;
            public final static int POSITION_VECTOR_Y                = 1*4;
            public final static int POSITION_VECTOR_Z                = 2*4;
            public final static int ANG_VEL_X                        = 3*4;
            public final static int ANG_VEL_Y                        = 4*4;
            public final static int ANG_VEL_Z                        = 5*4;
            public final static int Q1                               = 6*4;
            public final static int Q2                               = 7*4;
            public final static int Q3                               = 8*4;
            public final static int Q4                               = 9*4;
            public final static int TGT_Q1                           = 10*4;
            public final static int TGT_Q2                           = 11*4;
            public final static int TGT_Q3                           = 12*4;
            public final static int TGT_Q4                           = 13*4;
            public final static int RW_SPEED_X                       = 14*4;
            public final static int RW_SPEED_Y                       = 15*4;
            public final static int RW_SPEED_Z                       = 16*4;
        }
        public static class NADIR_TGTTRACKSTAT_IDX{
            //Byte offset
            public final static int POSITION_VECTOR_X                = 0*4;
            public final static int POSITION_VECTOR_Y                = 1*4;
            public final static int POSITION_VECTOR_Z                = 2*4;
            public final static int ANG_VEL_X                        = 3*4;
            public final static int ANG_VEL_Y                        = 4*4;
            public final static int ANG_VEL_Z                        = 5*4;
            public final static int Q1                               = 6*4;
            public final static int Q2                               = 7*4;
            public final static int Q3                               = 8*4;
            public final static int Q4                               = 9*4;
            public final static int TGT_Q1                           = 10*4;
            public final static int TGT_Q2                           = 11*4;
            public final static int TGT_Q3                           = 12*4;
            public final static int TGT_Q4                           = 13*4;
            public final static int RW_SPEED_X                       = 14*4;
            public final static int RW_SPEED_Y                       = 15*4;
            public final static int RW_SPEED_Z                       = 16*4;
        }
        public static class POINTING_LOOP_IDX{
            //Byte offset
            public final static int POINTING_LOOP_STATE              = 0;
        }

        public static byte [] long2ByteArray (final long value)
        {
            return ByteBuffer.allocate(LONG_BYTES).putLong(value).array();
        }
        public static byte [] int2ByteArray (final int value)
        {
             return ByteBuffer.allocate(INTEGER_BYTES).putInt(value).array();
        }
        public static byte [] int16_2ByteArray (final int value)
        {
             final byte[] temp=ByteBuffer.allocate(INTEGER_BYTES).putInt(value).array();
             final byte[] result=new byte[2];
             result[0]=temp[2];
             result[1]=temp[3];
             return result;
        }
        public static byte [] float2ByteArray (final float value)
        {
             return ByteBuffer.allocate(FLOAT_BYTES).putFloat(value).array();
        }
        public static byte [] double2ByteArray (final double value)
        {
             return ByteBuffer.allocate(DOUBLE_BYTES).putDouble(value).array();
        }

        public static void putByteInByteArray(final byte value, final int byteOffset, final byte[] target)
        {
            target[byteOffset]=value;
        }
        public static byte getByteFromByteArray(final byte[] source, final int byteOffset)
        {
            return source[byteOffset];
        }
        public static void putFloatInByteArray(final float value, final int byteOffset, final byte[] target)
        {
            final byte [] tempByte=float2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, FLOAT_BYTES - 1 + 1);
        }
        public static float getFloatFromByteArray(final byte[] source, final int byteOffset)
        {
            return ByteBuffer.wrap(source,byteOffset,FLOAT_BYTES).order(ByteOrder.BIG_ENDIAN).getFloat();
        }
        public static void putDoubleInByteArray(final double value, final int byteOffset, final byte[] target)
        {
            final byte [] tempByte=double2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, DOUBLE_BYTES - 1 + 1);
        }
        public static double getDoubleFromByteArray(final byte[] source, final int byteOffset)
        {
            return ByteBuffer.wrap(source,byteOffset,DOUBLE_BYTES).order(ByteOrder.BIG_ENDIAN).getDouble();
        }
        public static void putIntInByteArray(final int value, final int byteOffset, final byte[] target)
        {
            final byte [] tempByte=int2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, INTEGER_BYTES - 1 + 1);
        }
        /**
        Inserts a 16-bit integer into a byte array
         * @param value integer value to be added
         * @param byteOffset the destination offset in the byte array
         * @param target the resulting byte array to contain the integer value
        */
        public static void putInt16InByteArray(final int value, final int byteOffset, final byte[] target)
        {
            final byte [] tempByte=int2ByteArray(value);
            System.arraycopy(tempByte, 2, target, byteOffset + 0, INTEGER_BYTES - 1 - 2 + 1);
        }
        public static int getIntFromByteArray(final byte[] source, final int byteOffset)
        {
            return ByteBuffer.wrap(source,byteOffset,INTEGER_BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
        }
        public static int getInt16FromByteArray(final byte[] source, final int byteOffset)
        {
            final byte[] temp=new byte[INTEGER_BYTES];
            temp[2]=source[byteOffset];
            temp[3]=source[byteOffset+1];
            return ByteBuffer.wrap(temp,byteOffset,INTEGER_BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
        }
        public static void putLongInByteArray(final long value, final int byteOffset, final byte[] target)
        {
            final byte [] tempByte=long2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, LONG_BYTES - 1 + 1);
        }
        public static long getLongFromByteArray(final byte[] source, final int byteOffset)
        {
            return ByteBuffer.wrap(source,byteOffset,LONG_BYTES).order(ByteOrder.BIG_ENDIAN).getLong();
        }

    }
    public PFineADCS(final SimulatorNode simulatorNode, final String name) {
        super(simulatorNode,name);
    }
    /**
      * {@inheritDoc}
      */
    @Override
    @InternalData (internalID=1001,commandIDs={"",""},argNames={"cmdID","data","iAD"})
    public byte[] runRawCommand(final int cmdID, final byte[] data, final int iAD) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(cmdID);
        argObject.add(data);
        argObject.add(iAD);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1001,argObject);
    }

    @Override
    @InternalData (internalID=1002,commandIDs={"0xAA","0x01"},argNames={""})
    public byte[] Identify() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1002,argObject);
    }

    @Override
    @InternalData (internalID=1003,commandIDs={"0xAA","0x02"},argNames={""})
    public void SoftwareReset() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1003,argObject);
    }

    @Override
    @InternalData (internalID=1004,commandIDs={"0xAA","0x03"},argNames={""})
    public void I2CReset() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1004,argObject);
    }

    @Override
    @InternalData (internalID=1005,commandIDs={"0xAA","0x05"},argNames={"seconds","subseconds"})
    public void SetDateTime(final long seconds, final int subseconds) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(seconds);
        argObject.add(subseconds);
        super.getSimulatorNode().runGenericMethod(1005,argObject);
    }

    @Override
    @InternalData (internalID=1006,commandIDs={"0xAA","0x06"},argNames={""})
    public byte[] GetDateTime() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1006,argObject);
    }

    @Override
    @InternalData (internalID=1007,commandIDs={"0xAA","0x07"},argNames={"onoff","register"})
    public void iADCSPowerCycle(final byte onoff, final byte register) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(onoff);
        argObject.add(register);
        super.getSimulatorNode().runGenericMethod(1007,argObject);
    }

    @Override
    @InternalData (internalID=1008,commandIDs={"0xAA","0x10"},argNames={"opmode"})
    public void SetOperationMode(final byte opmode) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(opmode);
        super.getSimulatorNode().runGenericMethod(1008,argObject);
    }

    @Override
    @InternalData (internalID=1009,commandIDs={"0xAA","0x20"},argNames={"miliseconds"})
    public void SetPowerUpdateInterval(final long miliseconds) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(miliseconds);
        super.getSimulatorNode().runGenericMethod(1009,argObject);
    }

    @Override
    @InternalData (internalID=1010,commandIDs={"0xAA","0x21"},argNames={"miliseconds"})
    public void SetTemperatureUpdateInterval(final long miliseconds) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(miliseconds);
        super.getSimulatorNode().runGenericMethod(1010,argObject);
    }

    @Override
    @InternalData (internalID=1011,commandIDs={"0xAA","0x31"},argNames={""})
    public byte[] GetStandardTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1011,argObject);
    }

    @Override
    @InternalData (internalID=1012,commandIDs={"0xAA","0x32"},argNames={""})
    public byte[] GetExtendedTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1012,argObject);
    }

    @Override
    @InternalData (internalID=1013,commandIDs={"0xAA","0x33"},argNames={""})
    public byte[] GetPowerStatus() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1013,argObject);
    }

    @Override
    @InternalData (internalID=1014,commandIDs={"0xAA","0x34"},argNames={""})
    public byte[] GetInfoTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1014,argObject);
    }

    @Override
    @InternalData (internalID=1015,commandIDs={"0xAA","0x35"},argNames={""})
    public byte[] GetSensorTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1015,argObject);
    }

    @Override
    @InternalData (internalID=1016,commandIDs={"0xAA","0x36"},argNames={""})
    public byte[] GetActuatorTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1016,argObject);
    }

    @Override
    @InternalData (internalID=1017,commandIDs={"0xAA","0x37"},argNames={""})
    public byte[] GetAttitudeTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1017,argObject);
    }

    @Override
    @InternalData (internalID=1018,commandIDs={"0xAA","0x38"},argNames={""})
    public byte[] GetExtendedSensorTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1018,argObject);
    }

    @Override
    @InternalData (internalID=1019,commandIDs={"0xAA","0x39"},argNames={""})
    public byte[] GetExtendedActuatorTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1019,argObject);
    }

    @Override
    @InternalData (internalID=1020,commandIDs={"0xAA","0x3A"},argNames={""})
    public byte[] GetExtendedAttitudeTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1020,argObject);
    }

    @Override
    @InternalData (internalID=1021,commandIDs={"0xAA","0x3B"},argNames={""})
    public byte[] GetMagneticTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1021,argObject);
    }

    @Override
    @InternalData (internalID=1022,commandIDs={"0xAA","0x3C"},argNames={""})
    public byte[] GetSunTelemetry() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1022,argObject);
    }

    @Override
    @InternalData (internalID=1023,commandIDs={"0xAA","0x3D"},argNames={"value"})
    public void SetThresholdValueForMagEmulation(final int value) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(value);
        super.getSimulatorNode().runGenericMethod(1023,argObject);
    }

    @Override
    @InternalData (internalID=1024,commandIDs={"0xAA","0x3E"},argNames={""})
    public byte[] GetThresholdValueForMagEmulation() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1024,argObject);
    }

    @Override
    @InternalData (internalID=1025,commandIDs={"0xAA","0x41"},argNames={""})
    public void ClearErrorRegister() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1025,argObject);
    }

    @Override
    @InternalData (internalID=1026,commandIDs={"0xAA","0x42"},argNames={"register"})
    public byte[] GetSystemRegisters(final byte register) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(register);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1026,argObject);
    }

    @Override
    @InternalData (internalID=1027,commandIDs={"0xAA","0x42"},argNames={"register"})
    public byte[] GetControlRegisters(final byte register) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(register);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1027,argObject);
    }

    @Override
    @InternalData (internalID=1028,commandIDs={"0xAA","0x43"},argNames={"data"})
    public void SetSystemRegister(final byte[] data) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        super.getSimulatorNode().runGenericMethod(1028,argObject);
    }

    @Override
    @InternalData (internalID=1029,commandIDs={"0xAA","0x44"},argNames={"data"})
    public void ResetSystemRegister(final byte[] data) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        super.getSimulatorNode().runGenericMethod(1029,argObject);
    }

    @Override
    @InternalData (internalID=1030,commandIDs={"0xAA","0x45"},argNames={"memberID","interval"})
    public void SetMemberUpdateInterval(final byte memberID, final long interval) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(memberID);
        argObject.add(interval);
        super.getSimulatorNode().runGenericMethod(1030,argObject);
    }

    @Override
    @InternalData (internalID=1031,commandIDs={"0xAA","0x46"},argNames={"memberID"})
    public byte[] GetMemberUpdateInterval(final byte memberID) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(memberID);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1031,argObject);
    }

    @Override
    @InternalData (internalID=1032,commandIDs={"0xAA","0x50"},argNames={"HILStatusRegister"})
    public void SetHILStatus(final byte[] HILStatusRegister) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(HILStatusRegister);
        super.getSimulatorNode().runGenericMethod(1032,argObject);
    }

    @Override
    @InternalData (internalID=1033,commandIDs={"0xAA","0x51"},argNames={""})
    public byte[] GetHILStatus() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1033,argObject);
    }

    @Override
    @InternalData (internalID=1034,commandIDs={"0xAA","0x80"},argNames={"interval"})
    public void SetUpdateInterval(final int interval) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(interval);
        super.getSimulatorNode().runGenericMethod(1034,argObject);
    }

    @Override
    @InternalData (internalID=1035,commandIDs={"0xAA","0x81"},argNames={"values"})
    public void SetValuesToAllSensors(final int[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1035,argObject);
    }

    @Override
    @InternalData (internalID=1036,commandIDs={"0xAA","0x82"},argNames={""})
    public byte[] GetValuesAllSensors() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1036,argObject);
    }

    @Override
    @InternalData (internalID=1037,commandIDs={"0xAA","0x83"},argNames={"values"})
    public void SetCalibrationParametersAllSensors(final int[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1037,argObject);
    }

    @Override
    @InternalData (internalID=1038,commandIDs={"0xAA","0x84"},argNames={""})
    public byte[] GetCalibrationParametersAllSensors() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1038,argObject);
    }

    @Override
    @InternalData (internalID=1039,commandIDs={"0xAA","0x85"},argNames={""})
    public void EnableCalibrationAllSensors() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1039,argObject);
    }

    @Override
    @InternalData (internalID=1040,commandIDs={"0xAA","0x86"},argNames={""})
    public void DisableCalibrationAllSensors() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1040,argObject);
    }

    @Override
    @InternalData (internalID=1041,commandIDs={"0xAA","0x88"},argNames={"interval"})
    public void SetUpdateIntervalRW(final int interval) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(interval);
        super.getSimulatorNode().runGenericMethod(1041,argObject);
    }

    @Override
    @InternalData (internalID=1042,commandIDs={"0xAA","0x89"},argNames={"values"})
    public void SetSpeedToAllRWs(final int[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1042,argObject);
    }

    @Override
    @InternalData (internalID=1043,commandIDs={"0xAA","0x8A"},argNames={""})
    public byte[] GetSpeedAllRWs() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1043,argObject);
    }

    @Override
    @InternalData (internalID=1044,commandIDs={"0xAA","0x8C"},argNames={"values"})
    public byte[] SetAccAllRWs(final int[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1044,argObject);
    }

    @Override
    @InternalData (internalID=1045,commandIDs={"0xAA","0x8D"},argNames={"sleepMode"})
    public void SetSleepAllRWs(final byte sleepMode) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(sleepMode);
        super.getSimulatorNode().runGenericMethod(1045,argObject);
    }

    @Override
    @InternalData (internalID=1046,commandIDs={"0xAA","0x90"},argNames={"values"})
    public void SetDipoleMomentAllMTQs(final int[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1046,argObject);
    }

    @Override
    @InternalData (internalID=1047,commandIDs={"0xAA","0x91"},argNames={""})
    public byte[] GetDipoleMomentAllMTQs() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1047,argObject);
    }

    @Override
    @InternalData (internalID=1048,commandIDs={"0xAA","0x92"},argNames={""})
    public void SuspendAllMTQs() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1048,argObject);
    }

    @Override
    @InternalData (internalID=1049,commandIDs={"0xAA","0x93"},argNames={""})
    public void ResumeAllMTQs() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1049,argObject);
    }

    @Override
    @InternalData (internalID=1050,commandIDs={"0xAA","0x94"},argNames={""})
    public void ResetAllMTQs() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1050,argObject);
    }

    @Override
    @InternalData (internalID=1051,commandIDs={"0xAA","0x95"},argNames={""})
    public void RunSelftTestAllMTQs() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1051,argObject);
    }

    @Override
    @InternalData (internalID=1052,commandIDs={"0xAA","0x96"},argNames={"relaxtime"})
    public void SetMTQRelaxTime(final int relaxtime) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(relaxtime);
        super.getSimulatorNode().runGenericMethod(1052,argObject);
    }

    @Override
    @InternalData (internalID=1053,commandIDs={"0xAA","0x97"},argNames={""})
    public void StopAllMTQ() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1053,argObject);
    }

    @Override
    @InternalData (internalID=1054,commandIDs={"0xA0","0x01"},argNames={"dipoleValue"})
    public void MTQXSetDipoleMoment(final int dipoleValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(dipoleValue);
        super.getSimulatorNode().runGenericMethod(1054,argObject);
    }

    @Override
    @InternalData (internalID=1055,commandIDs={"0xA0","0x02"},argNames={""})
    public byte[] MTQXGetDipoleMoment() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1055,argObject);
    }

    @Override
    @InternalData (internalID=1056,commandIDs={"0xA0","0x05"},argNames={""})
    public void MTQXSuspend() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1056,argObject);
    }

    @Override
    @InternalData (internalID=1057,commandIDs={"0xA0","0x06"},argNames={""})
    public void MTQXResume() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1057,argObject);
    }

    @Override
    @InternalData (internalID=1058,commandIDs={"0xA0","0x07"},argNames={""})
    public void MTQXRunSelfTest() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1058,argObject);
    }

    @Override
    @InternalData (internalID=1059,commandIDs={"0xA0","0x08"},argNames={""})
    public void MTQXReset() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1059,argObject);
    }

    @Override
    @InternalData (internalID=1060,commandIDs={"0xA0","0x09"},argNames={""})
    public void MTQXStop() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1060,argObject);
    }

    @Override
    @InternalData (internalID=1061,commandIDs={"0xA1","0x01"},argNames={"dipoleValue"})
    public void MTQYSetDipoleMoment(final int dipoleValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(dipoleValue);
        super.getSimulatorNode().runGenericMethod(1061,argObject);
    }

    @Override
    @InternalData (internalID=1062,commandIDs={"0xA1","0x02"},argNames={""})
    public byte[] MTQYGetDipoleMoment() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1062,argObject);
    }

    @Override
    @InternalData (internalID=1063,commandIDs={"0xA1","0x05"},argNames={""})
    public void MTQYSuspend() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1063,argObject);
    }

    @Override
    @InternalData (internalID=1064,commandIDs={"0xA1","0x06"},argNames={""})
    public void MTQYResume() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1064,argObject);
    }

    @Override
    @InternalData (internalID=1065,commandIDs={"0xA1","0x07"},argNames={""})
    public void MTQYRunSelfTest() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1065,argObject);
    }

    @Override
    @InternalData (internalID=1066,commandIDs={"0xA1","0x08"},argNames={""})
    public void MTQYReset() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1066,argObject);
    }

    @Override
    @InternalData (internalID=1067,commandIDs={"0xA1","0x09"},argNames={""})
    public void MTQYStop() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1067,argObject);
    }

    @Override
    @InternalData (internalID=1068,commandIDs={"0xA2","0x01"},argNames={"dipoleValue"})
    public void MTQZSetDipoleMoment(final int dipoleValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(dipoleValue);
        super.getSimulatorNode().runGenericMethod(1068,argObject);
    }

    @Override
    @InternalData (internalID=1069,commandIDs={"0xA2","0x02"},argNames={""})
    public byte[] MTQZGetDipoleMoment() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1069,argObject);
    }

    @Override
    @InternalData (internalID=1070,commandIDs={"0xA2","0x05"},argNames={""})
    public void MTQZSuspend() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1070,argObject);
    }

    @Override
    @InternalData (internalID=1071,commandIDs={"0xA2","0x06"},argNames={""})
    public void MTQZResume() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1071,argObject);
    }

    @Override
    @InternalData (internalID=1072,commandIDs={"0xA2","0x07"},argNames={""})
    public void MTQZRunSelfTest() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1072,argObject);
    }

    @Override
    @InternalData (internalID=1073,commandIDs={"0xA2","0x08"},argNames={""})
    public void MTQZReset() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1073,argObject);
    }

    @Override
    @InternalData (internalID=1074,commandIDs={"0xA2","0x09"},argNames={""})
    public void MTQZStop() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1074,argObject);
    }

    @Override
    @InternalData (internalID=1075,commandIDs={"0xA3","0x01"},argNames={"speedValue"})
    public void SetRWXSpeed(final int speedValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(speedValue);
        super.getSimulatorNode().runGenericMethod(1075,argObject);
    }

    @Override
    @InternalData (internalID=1076,commandIDs={"0xA3","0x02"},argNames={""})
    public byte[] GetRWXSpeed() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1076,argObject);
    }

    @Override
    @InternalData (internalID=1077,commandIDs={"0xA3","0x03"},argNames={"accelerationValue"})
    public void SetRWXAcceleration(final int accelerationValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(accelerationValue);
        super.getSimulatorNode().runGenericMethod(1077,argObject);
    }

    @Override
    @InternalData (internalID=1078,commandIDs={"0xA3","0x08"},argNames={"sleepMode"})
    public void SetRWXSleep(final byte sleepMode) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(sleepMode);
        super.getSimulatorNode().runGenericMethod(1078,argObject);
    }

    @Override
    @InternalData (internalID=1079,commandIDs={"0xA3","0x20"},argNames={""})
    public byte[] GetRWXID() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1079,argObject);
    }

    @Override
    @InternalData (internalID=1080,commandIDs={"0xA4","0x01"},argNames={"speedValue"})
    public void SetRWYSpeed(final int speedValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(speedValue);
        super.getSimulatorNode().runGenericMethod(1080,argObject);
    }

    @Override
    @InternalData (internalID=1081,commandIDs={"0xA4","0x02"},argNames={""})
    public byte[] GetRWYSpeed() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1081,argObject);
    }

    @Override
    @InternalData (internalID=1082,commandIDs={"0xA4","0x03"},argNames={"accelerationValue"})
    public void SetRWYAcceleration(final int accelerationValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(accelerationValue);
        super.getSimulatorNode().runGenericMethod(1082,argObject);
    }

    @Override
    @InternalData (internalID=1083,commandIDs={"0xA4","0x08"},argNames={"sleepMode"})
    public void SetRWYSleep(final byte sleepMode) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(sleepMode);
        super.getSimulatorNode().runGenericMethod(1083,argObject);
    }

    @Override
    @InternalData (internalID=1084,commandIDs={"0xA4","0x20"},argNames={""})
    public byte[] GetRWYID() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1084,argObject);
    }

    @Override
    @InternalData (internalID=1085,commandIDs={"0xA4","0x01"},argNames={"speedValue"})
    public void SetRWZSpeed(final int speedValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(speedValue);
        super.getSimulatorNode().runGenericMethod(1085,argObject);
    }

    @Override
    @InternalData (internalID=1086,commandIDs={"0xA4","0x02"},argNames={""})
    public byte[] GetRWZSpeed() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1086,argObject);
    }

    @Override
    @InternalData (internalID=1087,commandIDs={"0xA4","0x03"},argNames={"accelerationValue"})
    public void SetRWZAcceleration(final int accelerationValue) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(accelerationValue);
        super.getSimulatorNode().runGenericMethod(1087,argObject);
    }

    @Override
    @InternalData (internalID=1088,commandIDs={"0xA4","0x08"},argNames={"sleepMode"})
    public void SetRWZSleep(final byte sleepMode) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(sleepMode);
        super.getSimulatorNode().runGenericMethod(1088,argObject);
    }

    @Override
    @InternalData (internalID=1089,commandIDs={"0xA4","0x20"},argNames={""})
    public byte[] GetRWZID() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1089,argObject);
    }

    @Override
    @InternalData (internalID=1090,commandIDs={"0xAB","0x01"},argNames={"values"})
    public void ST200SetQuaternion(final int[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1090,argObject);
    }

    @Override
    @InternalData (internalID=1091,commandIDs={"0xAB","0x10"},argNames={"interval"})
    public void ST200UpdateInterval(final long interval) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(interval);
        super.getSimulatorNode().runGenericMethod(1091,argObject);
    }

    @Override
    @InternalData (internalID=1092,commandIDs={"0xB0","0x01"},argNames={"values"})
    public void SunSensor1SetValue(final int[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1092,argObject);
    }

    @Override
    @InternalData (internalID=1093,commandIDs={"0xB0","0x02"},argNames={""})
    public byte[] SunSensor1GetValue() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1093,argObject);
    }

    @Override
    @InternalData (internalID=1094,commandIDs={"0xB0","0x11"},argNames={"values"})
    public void SunSensor1SetValueQuaternion(final int[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1094,argObject);
    }

    @Override
    @InternalData (internalID=1095,commandIDs={"0xB0","0x12"},argNames={""})
    public byte[] SunSensor1GetValueQuaternion() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1095,argObject);
    }

    @Override
    @InternalData (internalID=1096,commandIDs={"0xC0","0x01"},argNames={"values"})
    public void Gyro1SetRate(final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1096,argObject);
    }

    @Override
    @InternalData (internalID=1097,commandIDs={"0xC0","0x02"},argNames={""})
    public byte[] Gyro1GetRate() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1097,argObject);
    }

    @Override
    @InternalData (internalID=1098,commandIDs={"0xC0","0x10"},argNames={"updateRate"})
    public void Gyro1SetUpdateInterval(final int updateRate) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(updateRate);
        super.getSimulatorNode().runGenericMethod(1098,argObject);
    }

    @Override
    @InternalData (internalID=1099,commandIDs={"0xC0","0x20"},argNames={""})
    public void Gyro1RemoveBias() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1099,argObject);
    }

    @Override
    @InternalData (internalID=1100,commandIDs={"0xC0","0x21"},argNames={""})
    public byte[] Gyro1GetBias() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1100,argObject);
    }

    @Override
    @InternalData (internalID=1101,commandIDs={"0xC0","0x22"},argNames={"updateRate","allowedDeviation"})
    public void Gyro1SetFilter1(final byte updateRate, final int allowedDeviation) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(updateRate);
        argObject.add(allowedDeviation);
        super.getSimulatorNode().runGenericMethod(1101,argObject);
    }

    @Override
    @InternalData (internalID=1102,commandIDs={"0xC0","0x23"},argNames={"calibrationValues"})
    public void Gyro1SetCalibrationParameters(final int[] calibrationValues) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(calibrationValues);
        super.getSimulatorNode().runGenericMethod(1102,argObject);
    }

    @Override
    @InternalData (internalID=1103,commandIDs={"0xC0","0x24"},argNames={""})
    public byte[] Gyro1GetCalibrationParameters() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1103,argObject);
    }

    @Override
    @InternalData (internalID=1104,commandIDs={"0xC0","0x25"},argNames={""})
    public void Gyro1EnableCalibration() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1104,argObject);
    }

    @Override
    @InternalData (internalID=1105,commandIDs={"0xC0","0x26"},argNames={""})
    public void Gyro1DisableCalibration() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1105,argObject);
    }

    @Override
    @InternalData (internalID=1106,commandIDs={"0xC0","0x27"},argNames={"quaternionValues"})
    public void Gyro1SetQuaternionFromSunSensor(final float[] quaternionValues) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(quaternionValues);
        super.getSimulatorNode().runGenericMethod(1106,argObject);
    }

    @Override
    @InternalData (internalID=1107,commandIDs={"0xC0","0x28"},argNames={""})
    public byte[] Gyro1GetQuaternionFromSunSensor() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1107,argObject);
    }

    @Override
    @InternalData (internalID=1108,commandIDs={"0xC3","0x01"},argNames={"values"})
    public void accelerometerSetValues(final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1108,argObject);
    }

    @Override
    @InternalData (internalID=1109,commandIDs={"0xC3","0x02"},argNames={""})
    public byte[] accelerometerGetValues() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1109,argObject);
    }

    @Override
    @InternalData (internalID=1110,commandIDs={"0xC3","0x10"},argNames={"interval"})
    public void accelerometerReadInterval(final int interval) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(interval);
        super.getSimulatorNode().runGenericMethod(1110,argObject);
    }

    @Override
    @InternalData (internalID=1111,commandIDs={"0xC6","0x01"},argNames={"values"})
    public void magnetometerSetMagneticField(final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1111,argObject);
    }

    @Override
    @InternalData (internalID=1112,commandIDs={"0xC6","0x02"},argNames={""})
    public byte[] magnetometerGetMagneticField() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1112,argObject);
    }

    @Override
    @InternalData (internalID=1113,commandIDs={"0xC6","0x10"},argNames={"interval"})
    public void magnetometerSetUpdateInterval(final int interval) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(interval);
        super.getSimulatorNode().runGenericMethod(1113,argObject);
    }

    @Override
    @InternalData (internalID=1114,commandIDs={"0xC6","0x23"},argNames={"values"})
    public void accelerometerSetCalibrationParams(final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1114,argObject);
    }

    @Override
    @InternalData (internalID=1115,commandIDs={"0xC6","0x24"},argNames={""})
    public byte[] accelerometerGetCalibrationParams() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1115,argObject);
    }

    @Override
    @InternalData (internalID=1116,commandIDs={"0xC6","0x25"},argNames={""})
    public void accelerometerEnableCalibration() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1116,argObject);
    }

    @Override
    @InternalData (internalID=1117,commandIDs={"0xC6","0x26"},argNames={""})
    public void accelerometerDisableCalibration() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1117,argObject);
    }

    @Override
    @InternalData (internalID=1118,commandIDs={"0xC6","0x27"},argNames={"quaternionValues"})
    public void accelerometerSetQuaternionFromSunSensor(final float[] quaternionValues) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(quaternionValues);
        super.getSimulatorNode().runGenericMethod(1118,argObject);
    }

    @Override
    @InternalData (internalID=1119,commandIDs={"0xC6","0x28"},argNames={""})
    public byte[] accelerometerGetQuaternionFromSunSensor() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1119,argObject);
    }

    @Override
    @InternalData (internalID=1120,commandIDs={"0x30","0x02"},argNames={"requestRegister"})
    public byte[] kalman2FilterGetTelemetry(final int requestRegister) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(requestRegister);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1120,argObject);
    }

    @Override
    @InternalData (internalID=1121,commandIDs={"0x30","0x03"},argNames={"selectGyro"})
    public void kalman2FilterSelectGyro(final byte selectGyro) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(selectGyro);
        super.getSimulatorNode().runGenericMethod(1121,argObject);
    }

    @Override
    @InternalData (internalID=1122,commandIDs={"0x30","0x11"},argNames={""})
    public void kalman2FilterStart() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1122,argObject);
    }

    @Override
    @InternalData (internalID=1123,commandIDs={"0x30","0x12"},argNames={""})
    public void kalman2FilterStop() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1123,argObject);
    }

    @Override
    @InternalData (internalID=1124,commandIDs={"0x30","0x22"},argNames={"requestRegister"})
    public byte[] kalman4FilterGetTelemetry(final int requestRegister) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(requestRegister);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1124,argObject);
    }

    @Override
    @InternalData (internalID=1125,commandIDs={"0x30","0x23"},argNames={"selectGyro"})
    public void kalman4FilterSelectGyro(final byte selectGyro) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(selectGyro);
        super.getSimulatorNode().runGenericMethod(1125,argObject);
    }

    @Override
    @InternalData (internalID=1126,commandIDs={"0x30","0x2A"},argNames={""})
    public void kalman4FilterStart() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1126,argObject);
    }

    @Override
    @InternalData (internalID=1127,commandIDs={"0x30","0x2B"},argNames={""})
    public void kalman4FilterStop() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1127,argObject);
    }

    @Override
    @InternalData (internalID=1128,commandIDs={"0x31","0x10"},argNames={"interval"})
    public void controlLoopsSetUpdateInterval(final int interval) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(interval);
        super.getSimulatorNode().runGenericMethod(1128,argObject);
    }

    @Override
    @InternalData (internalID=1129,commandIDs={"0x31","0x14"},argNames={""})
    public byte[] controlLoopsGetTargetRWSpeed() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1129,argObject);
    }

    @Override
    @InternalData (internalID=1130,commandIDs={"0x31","0x15"},argNames={""},
        description=
            "MyCustom\n"
        + ""
        + "Again")
    public byte[] controlLoopsGetTargetMTWDipoleMoment3D() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1130,argObject);
    }

    @Override
    @InternalData (internalID=1131,commandIDs={"0x31","0x15"},argNames={""})
    public byte[] controlLoopsGetStatus() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1131,argObject);
    }

    @Override
    @InternalData (internalID=1132,commandIDs={"0x31","0x20"},argNames={"axis","controlRegister","values"})
    public void controlLoopsSetAntiWindup(final byte axis, final int controlRegister, final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(axis);
        argObject.add(controlRegister);
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1132,argObject);
    }

    @Override
    @InternalData (internalID=1133,commandIDs={"0x31","0x21"},argNames={"axis","controlRegister"})
    public byte[] controlLoopsGetAntiWindup(final byte axis, final int controlRegister) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(axis);
        argObject.add(controlRegister);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1133,argObject);
    }

    @Override
    @InternalData (internalID=1134,commandIDs={"0x31","0x60"},argNames={"axis","controlRegister","targetAngle"})
    public void singleAxisStartControlLoop(final byte axis, final int controlRegister, final float[] targetAngle) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(axis);
        argObject.add(controlRegister);
        argObject.add(targetAngle);
        super.getSimulatorNode().runGenericMethod(1134,argObject);
    }

    @Override
    @InternalData (internalID=1135,commandIDs={"0x31","0x61"},argNames={"axis"})
    public void singleAxisStopControlLoop(final byte axis) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(axis);
        super.getSimulatorNode().runGenericMethod(1135,argObject);
    }

    @Override
    @InternalData (internalID=1136,commandIDs={"0x31","0x62"},argNames={"axis","controlRegister","values"})
    public void singleAxisSetParameter(final byte axis, final int controlRegister, final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(axis);
        argObject.add(controlRegister);
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1136,argObject);
    }

    @Override
    @InternalData (internalID=1137,commandIDs={"0x31","0x63"},argNames={"axis","controlRegister"})
    public byte[] singleAxisGetParameter(final byte axis, final int controlRegister) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(axis);
        argObject.add(controlRegister);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1137,argObject);
    }

    @Override
    @InternalData (internalID=1138,commandIDs={"0x31","0x64"},argNames={"axis","controlRegister"})
    public void singleAxisResetParameter(final byte axis, final int controlRegister) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(axis);
        argObject.add(controlRegister);
        super.getSimulatorNode().runGenericMethod(1138,argObject);
    }

    @Override
    @InternalData (internalID=1139,commandIDs={"0x31","0x70"},argNames={"targetSunVector"})
    public void sunPointingStartControlLoop(final float[] targetSunVector) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(targetSunVector);
        super.getSimulatorNode().runGenericMethod(1139,argObject);
    }

    @Override
    @InternalData (internalID=1140,commandIDs={"0x31","0x71"},argNames={""})
    public void sunPointingStopControlLoop() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1140,argObject);
    }

    @Override
    @InternalData (internalID=1141,commandIDs={"0x31","0x72"},argNames={"values"})
    public void sunPointingSetParameter(final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1141,argObject);
    }

    @Override
    @InternalData (internalID=1142,commandIDs={"0x31","0x73"},argNames={""})
    public byte[] sunPointingGetParameter() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1142,argObject);
    }

    @Override
    @InternalData (internalID=1143,commandIDs={"0x31","0x74"},argNames={""})
    public void sunPointingResetParameter() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1143,argObject);
    }

    @Override
    @InternalData (internalID=1144,commandIDs={"0x31","0x80"},argNames={"controller"})
    public void bdotStartControlLoop(final byte controller) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(controller);
        super.getSimulatorNode().runGenericMethod(1144,argObject);
    }

    @Override
    @InternalData (internalID=1145,commandIDs={"0x31","0x81"},argNames={""})
    public void bdotStopControlLoop() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1145,argObject);
    }

    @Override
    @InternalData (internalID=1146,commandIDs={"0x31","0x82"},argNames={"gain"})
    public void bdotSetParameter(final float gain) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(gain);
        super.getSimulatorNode().runGenericMethod(1146,argObject);
    }

    @Override
    @InternalData (internalID=1147,commandIDs={"0x31","0x83"},argNames={""})
    public byte[] bdotGetParameter() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1147,argObject);
    }

    @Override
    @InternalData (internalID=1148,commandIDs={"0x31","0x84"},argNames={""})
    public void bdotResetParameter() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1148,argObject);
    }

    @Override
    @InternalData (internalID=1149,commandIDs={"0x31","0x90"},argNames={"targetBodyAxis","targetAngularVelocityMagnitude","inertialTargetVector"})
    public void singleSpinningStartControlLoop(final float[] targetBodyAxis, final float targetAngularVelocityMagnitude, final float[] inertialTargetVector) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(targetBodyAxis);
        argObject.add(targetAngularVelocityMagnitude);
        argObject.add(inertialTargetVector);
        super.getSimulatorNode().runGenericMethod(1149,argObject);
    }

    @Override
    @InternalData (internalID=1150,commandIDs={"0x31","0x91"},argNames={""})
    public void singleSpinningStopControlLoop() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1150,argObject);
    }

    @Override
    @InternalData (internalID=1151,commandIDs={"0x31","0x92"},argNames={"values"})
    public void singleSpinningSetParameter(final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1151,argObject);
    }

    @Override
    @InternalData (internalID=1152,commandIDs={"0x31","0x93"},argNames={""})
    public byte[] singleSpinningGetParameter() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1152,argObject);
    }

    @Override
    @InternalData (internalID=1153,commandIDs={"0x31","0xA0"},argNames={"modeType","values","times"})
    public void targetTrackingStartModeConstantVel(final byte modeType, final float[] values, final long[] times) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(modeType);
        argObject.add(values);
        argObject.add(times);
        super.getSimulatorNode().runGenericMethod(1153,argObject);
    }

    @Override
    @InternalData (internalID=1154,commandIDs={"0x31","0xA0"},argNames={"modeType","values","times"})
    public void targetTrackingStartModeGeneral(final byte modeType, final float[] values, final long[] times) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(modeType);
        argObject.add(values);
        argObject.add(times);
        super.getSimulatorNode().runGenericMethod(1154,argObject);
    }

    @Override
    @InternalData (internalID=1155,commandIDs={"0x31","0xA0"},argNames={"modeType","values","times"})
    public void targetTrackingStartModeWGS84(final byte modeType, final float[] values, final long[] times) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(modeType);
        argObject.add(values);
        argObject.add(times);
        super.getSimulatorNode().runGenericMethod(1155,argObject);
    }

    @Override
    @InternalData (internalID=1156,commandIDs={"0x31","0xA1"},argNames={""})
    public void targetTrackingStopMode() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1156,argObject);
    }

    @Override
    @InternalData (internalID=1157,commandIDs={"0x31","0xA2"},argNames={"values"})
    public void targetTrackingSetParameters(final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1157,argObject);
    }

    @Override
    @InternalData (internalID=1158,commandIDs={"0x31","0xA3"},argNames={""})
    public byte[] targetTrackingGetParameters() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1158,argObject);
    }

    @Override
    @InternalData (internalID=1159,commandIDs={"0x31","0xA4"},argNames={""})
    public void targetTrackingResetParameters() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1159,argObject);
    }

    @Override
    @InternalData (internalID=1160,commandIDs={"0x32","0x01"},argNames={""})
    public void orbitSetRV() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1160,argObject);
    }

    @Override
    @InternalData (internalID=1161,commandIDs={"0x32","0x02"},argNames={""})
    public byte[] orbitGetRV() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1161,argObject);
    }

    @Override
    @InternalData (internalID=1162,commandIDs={"0x32","0x03"},argNames={"tleData"})
    public void orbitSetTLE(final byte[] tleData) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(tleData);
        super.getSimulatorNode().runGenericMethod(1162,argObject);
    }

    @Override
    @InternalData (internalID=1163,commandIDs={"0x32","0x10"},argNames={"updateInterval"})
    public byte[] orbitSetUpdateInterval(final int updateInterval) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(updateInterval);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1163,argObject);
    }

    @Override
    @InternalData (internalID=1164,commandIDs={"0x36","0x01"},argNames={""})
    public void opModeIdle() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1164,argObject);
    }

    @Override
    @InternalData (internalID=1165,commandIDs={"0x36","0x02"},argNames={""})
    public void opModeSafe() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1165,argObject);
    }

    @Override
    @InternalData (internalID=1166,commandIDs={"0x36","0x03"},argNames={""})
    public void opModeMeasure() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1166,argObject);
    }

    @Override
    @InternalData (internalID=1167,commandIDs={"0x36","0x04"},argNames={"start","times"})
    public void opModeDetumble(final byte start, final long[] times) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(start);
        argObject.add(times);
        super.getSimulatorNode().runGenericMethod(1167,argObject);
    }

    @Override
    @InternalData (internalID=1168,commandIDs={"0x36","0x05"},argNames={"mode","times","targetSunVector"})
    public void opModeSunPointing(final byte[] mode, final long[] times, final float[] targetSunVector) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(mode);
        argObject.add(times);
        argObject.add(targetSunVector);
        super.getSimulatorNode().runGenericMethod(1168,argObject);
    }

    @Override
    @InternalData (internalID=1169,commandIDs={"0x36","0x06"},argNames={""})
    public byte[] opModeGetSunPointingStatus() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1169,argObject);
    }

    @Override
    @InternalData (internalID=1170,commandIDs={"0x36","0x07"},argNames={"mode","times","targetVector"})
    public void opModeSetModeSpin(final byte mode, final long[] times, final float[] targetVector) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(mode);
        argObject.add(times);
        argObject.add(targetVector);
        super.getSimulatorNode().runGenericMethod(1170,argObject);
    }

    @Override
    @InternalData (internalID=1171,commandIDs={"0x36","0x08"},argNames={""})
    public byte[] opModeGetSpinModeStatus() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1171,argObject);
    }

    @Override
    @InternalData (internalID=1172,commandIDs={"0x36","0x09"},argNames={"mode","times","targetVector"})
    public void opModeSetTargetTrackingCVelocity(final byte mode, final long[] times, final float[] targetVector) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(mode);
        argObject.add(times);
        argObject.add(targetVector);
        super.getSimulatorNode().runGenericMethod(1172,argObject);
    }

    @Override
    @InternalData (internalID=1173,commandIDs={"0x36","0x0A"},argNames={""})
    public byte[] opModeGetTargetTrackingCVelocityStatus() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1173,argObject);
    }

    @Override
    @InternalData (internalID=1174,commandIDs={"0x36","0x0B"},argNames={"mode","times"})
    public void opModeSetNadirTargetTracking(final byte mode, final long[] times) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(mode);
        argObject.add(times);
        super.getSimulatorNode().runGenericMethod(1174,argObject);
    }

    @Override
    @InternalData (internalID=1175,commandIDs={"0x36","0x0C"},argNames={""})
    public byte[] opModeGetNadirTargetTrackingStatus() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1175,argObject);
    }

    @Override
    @InternalData (internalID=1176,commandIDs={"0x36","0x0D"},argNames={"mode","times","quaternionCoefficients"})
    public void opModeSetStandardTargetTracking(final byte mode, final long[] times, final float[] quaternionCoefficients) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(mode);
        argObject.add(times);
        argObject.add(quaternionCoefficients);
        super.getSimulatorNode().runGenericMethod(1176,argObject);
    }

    @Override
    @InternalData (internalID=1177,commandIDs={"0x36","0x0E"},argNames={""})
    public byte[] opModeGetStandardTargetTrackingStatus() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1177,argObject);
    }

    @Override
    @InternalData (internalID=1178,commandIDs={"0x36","0x0F"},argNames={"mode","times","latitudeLongitude"})
    public void opModeSetFixWGS84TargetTracking(final byte mode, final long[] times, final float[] latitudeLongitude) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(mode);
        argObject.add(times);
        argObject.add(latitudeLongitude);
        super.getSimulatorNode().runGenericMethod(1178,argObject);
    }

    @Override
    @InternalData (internalID=1179,commandIDs={"0x36","0x10"},argNames={""})
    public byte[] opModeGetFixWGS84TargetTracking() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1179,argObject);
    }

    @Override
    @InternalData (internalID=1180,commandIDs={"0x36","0x11"},argNames={"mode","startTime","data"})
    public void opModeSetTargetCapture1(final byte mode, final long startTime, final float[] data) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(mode);
        argObject.add(startTime);
        argObject.add(data);
        super.getSimulatorNode().runGenericMethod(1180,argObject);
    }

    @Override
    @InternalData (internalID=1181,commandIDs={"0x36","0x12"},argNames={""})
    public byte[] opModeGetTargetCapture1() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1181,argObject);
    }

    @Override
    @InternalData (internalID=1182,commandIDs={"",""},argNames={"tleLine1","tleLine2"})
    public byte[] simGetOrbitTLEBytesFromString(final String tleLine1, final String tleLine2) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(tleLine1);
        argObject.add(tleLine2);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1182,argObject);
    }

    @Override
    @InternalData (internalID=1183,commandIDs={"",""},argNames={"data","byteOffset"})
    public float simGetFloatFromByteArray(final byte[] data, final int byteOffset) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        argObject.add(byteOffset);
        return (Float) super.getSimulatorNode().runGenericMethod(1183,argObject);
    }

    @Override
    @InternalData (internalID=1184,commandIDs={"",""},argNames={"data"})
    public byte[] simGetByteArrayFromFloat(final float data) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1184,argObject);
    }

    @Override
    @InternalData (internalID=1185,commandIDs={"",""},argNames={"data","byteOffset"})
    public double simGetDoubleFromByteArray(final byte[] data, final int byteOffset) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        argObject.add(byteOffset);
        return (Double) super.getSimulatorNode().runGenericMethod(1185,argObject);
    }

    @Override
    @InternalData (internalID=1186,commandIDs={"",""},argNames={"data"})
    public byte[] simGetByteArrayFromDouble(final double data) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1186,argObject);
    }

    @Override
    @InternalData (internalID=1187,commandIDs={"",""},argNames={"data","byteOffset"})
    public int simGetIntFromByteArray(final byte[] data, final int byteOffset) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        argObject.add(byteOffset);
        return (Integer) super.getSimulatorNode().runGenericMethod(1187,argObject);
    }

    @Override
    @InternalData (internalID=1188,commandIDs={"",""},argNames={"data"})
    public byte[] simGetByteArrayFromInt(final int data) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1188,argObject);
    }

    @Override
    @InternalData (internalID=1189,commandIDs={"",""},argNames={"data","byteOffset"})
    public long simGetLongFromByteArray(final byte[] data, final int byteOffset) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        argObject.add(byteOffset);
        return (Long) super.getSimulatorNode().runGenericMethod(1189,argObject);
    }

    @Override
    @InternalData (internalID=1190,commandIDs={"",""},argNames={"data"})
    public byte[] simGetByteArrayFromLong(final long data) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        return (byte[]) super.getSimulatorNode().runGenericMethod(1190,argObject);
    }

    @Override
    @InternalData (internalID=1191,commandIDs={"0xC0","0x01"},argNames={"values"})
    public void Gyro2SetRate(final float[] values) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(values);
        super.getSimulatorNode().runGenericMethod(1191,argObject);
    }

    @Override
    @InternalData (internalID=1192,commandIDs={"0xC0","0x02"},argNames={""})
    public byte[] Gyro2GetRate() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1192,argObject);
    }

    @Override
    @InternalData (internalID=1193,commandIDs={"0xC0","0x10"},argNames={"updateRate"})
    public void Gyro2SetUpdateInterval(final int updateRate) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(updateRate);
        super.getSimulatorNode().runGenericMethod(1193,argObject);
    }

    @Override
    @InternalData (internalID=1194,commandIDs={"0xC0","0x20"},argNames={""})
    public void Gyro2RemoveBias() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1194,argObject);
    }

    @Override
    @InternalData (internalID=1195,commandIDs={"0xC0","0x21"},argNames={""})
    public byte[] Gyro2GetBias() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1195,argObject);
    }

    @Override
    @InternalData (internalID=1196,commandIDs={"0xC0","0x22"},argNames={"updateRate","allowedDeviation"})
    public void Gyro2SetFilter1(final byte updateRate, final int allowedDeviation) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(updateRate);
        argObject.add(allowedDeviation);
        super.getSimulatorNode().runGenericMethod(1196,argObject);
    }

    @Override
    @InternalData (internalID=1197,commandIDs={"0xC0","0x23"},argNames={"calibrationValues"})
    public void Gyro2SetCalibrationParameters(final int[] calibrationValues) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(calibrationValues);
        super.getSimulatorNode().runGenericMethod(1197,argObject);
    }

    @Override
    @InternalData (internalID=1198,commandIDs={"0xC0","0x24"},argNames={""})
    public byte[] Gyro2GetCalibrationParameters() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1198,argObject);
    }

    @Override
    @InternalData (internalID=1199,commandIDs={"0xC0","0x25"},argNames={""})
    public void Gyro2EnableCalibration() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1199,argObject);
    }

    @Override
    @InternalData (internalID=1200,commandIDs={"0xC0","0x26"},argNames={""})
    public void Gyro2DisableCalibration() {
        final ArrayList<Object> argObject=null;
        super.getSimulatorNode().runGenericMethod(1200,argObject);
    }

    @Override
    @InternalData (internalID=1201,commandIDs={"0xC0","0x27"},argNames={"quaternionValues"})
    public void Gyro2SetQuaternionFromSunSensor(final float[] quaternionValues) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(quaternionValues);
        super.getSimulatorNode().runGenericMethod(1201,argObject);
    }

    @Override
    @InternalData (internalID=1202,commandIDs={"0xC0","0x28"},argNames={""})
    public byte[] Gyro2GetQuaternionFromSunSensor() {
        final ArrayList<Object> argObject=null;
        return (byte[]) super.getSimulatorNode().runGenericMethod(1202,argObject);
    }

    @Override
    @InternalData (internalID=1203,commandIDs={"",""},argNames={"data","byteOffset"})
    public int simGetInt16FromByteArray(final byte[] data, final int byteOffset) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        argObject.add(byteOffset);
        return (Integer) super.getSimulatorNode().runGenericMethod(1203,argObject);
    }

    @Override
    @InternalData (internalID=1204,commandIDs={"",""},argNames={"data"})
    public void simRunDeviceCommand(final String data) {
        final ArrayList<Object> argObject = new ArrayList<>();
        argObject.add(data);
        super.getSimulatorNode().runGenericMethod(1204,argObject);
    }

}
