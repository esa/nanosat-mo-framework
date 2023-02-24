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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.ccsds.moims.mo.mal.structures.FloatList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed;
import org.ccsds.moims.mo.platform.structures.VectorF3D;

/**
 *
 * @author Cesar Coelho
 */
public class HelperIADCS100 {

    public static class FWRefFineADCS {

        public static class SENSORTM_IDX {
            //Byte offset

            public final static int MAG_FIELD_X = 0 * 4;
            public final static int MAG_FIELD_Y = 1 * 4;
            public final static int MAG_FIELD_Z = 2 * 4;
            public final static int ACCELEROMETER_X = 3 * 4;
            public final static int ACCELEROMETER_Y = 4 * 4;
            public final static int ACCELEROMETER_Z = 5 * 4;
            public final static int GYRO1_X = 6 * 4;
            public final static int GYRO1_Y = 7 * 4;
            public final static int GYRO1_Z = 8 * 4;
            public final static int GYRO2_X = 9 * 4;
            public final static int GYRO2_Y = 10 * 4;
            public final static int GYRO2_Z = 11 * 4;
            public final static int ST200_TIME = 12 * 4;//UI64
            public final static int ST200_TIME_MSEC = 14 * 4;//UI16
            public final static int QUATERNION1 = 14 * 4 + 2;
            public final static int QUATERNION2 = 15 * 4 + 2;
            public final static int QUATERNION3 = 16 * 4 + 2;
            public final static int QUATERNION4 = 17 * 4 + 2;
        }

        public static class ACTUATORTM_IDX {
            //Byte offset

            public final static int RW_CURRENT_SPEED_X = 0 * 2;
            public final static int RW_LAST_TARGET_X = 1 * 2;
            public final static int RW_TARGET_MODE_X = 2 * 2;
            public final static int RW_CURRENT_SPEED_Y = 2 * 2 + 1;
            public final static int RW_LAST_TARGET_Y = 3 * 2 + 1;
            public final static int RW_TARGET_MODE_Y = 4 * 2 + 1;
            public final static int RW_CURRENT_SPEED_Z = 5 * 2;
            public final static int RW_LAST_TARGET_Z = 6 * 2;
            public final static int RW_TARGET_MODE_Z = 7 * 2;
            public final static int MTQ_TARGET_X = 7 * 2 + 1;
            public final static int MTQ_TARGET_Y = 8 * 2 + 1;
            public final static int MTQ_TARGET_Z = 9 * 2 + 1;
        }

        public static class SPINMODESTAT_IDX {
            //Byte offset

            public final static int SUN_VECTOR_X = 0 * 4;
            public final static int SUN_VECTOR_Y = 1 * 4;
            public final static int SUN_VECTOR_Z = 2 * 4;
            public final static int MAGNETOMETER_X = 3 * 4;
            public final static int MAGNETOMETER_Y = 4 * 4;
            public final static int MAGNETOMETER_Z = 5 * 4;
            public final static int Q1 = 6 * 4;
            public final static int Q2 = 7 * 4;
            public final static int Q3 = 8 * 4;
            public final static int Q4 = 9 * 4;
            public final static int ANG_MOM_X = 10 * 4;
            public final static int ANG_MOM_Y = 11 * 4;
            public final static int ANG_MOM_Z = 12 * 4;
            public final static int MTQ_DIP_MOMENT_X = 13 * 4;
            public final static int MTQ_DIP_MOMENT_Y = 14 * 4;
            public final static int MTQ_DIP_MOMENT_Z = 15 * 4;
        }

        public static class SUNPOINTSTAT_IDX {
            //Byte offset

            public final static int SUN_VECTOR_X = 0 * 4;
            public final static int SUN_VECTOR_Y = 1 * 4;
            public final static int SUN_VECTOR_Z = 2 * 4;
            public final static int SUN_VECTOR_VALID = 3 * 4;
            public final static int ACTUATOR_X = 3 * 4 + 1;
            public final static int ACTUATOR_Y = 4 * 4 + 1;
            public final static int ACTUATOR_Z = 5 * 4 + 1;
        }

        public static class FIXWGS84_TGTTRACKSTAT_IDX {
            //Byte offset

            public final static int POSITION_VECTOR_X = 0 * 4;
            public final static int POSITION_VECTOR_Y = 1 * 4;
            public final static int POSITION_VECTOR_Z = 2 * 4;
            public final static int ANG_VEL_X = 3 * 4;
            public final static int ANG_VEL_Y = 4 * 4;
            public final static int ANG_VEL_Z = 5 * 4;
            public final static int Q1 = 6 * 4;
            public final static int Q2 = 7 * 4;
            public final static int Q3 = 8 * 4;
            public final static int Q4 = 9 * 4;
            public final static int TGT_Q1 = 10 * 4;
            public final static int TGT_Q2 = 11 * 4;
            public final static int TGT_Q3 = 12 * 4;
            public final static int TGT_Q4 = 13 * 4;
            public final static int RW_SPEED_X = 14 * 4;
            public final static int RW_SPEED_Y = 15 * 4;
            public final static int RW_SPEED_Z = 16 * 4;
        }

        public static class NADIR_TGTTRACKSTAT_IDX {
            //Byte offset

            public final static int POSITION_VECTOR_X = 0 * 4;
            public final static int POSITION_VECTOR_Y = 1 * 4;
            public final static int POSITION_VECTOR_Z = 2 * 4;
            public final static int ANG_VEL_X = 3 * 4;
            public final static int ANG_VEL_Y = 4 * 4;
            public final static int ANG_VEL_Z = 5 * 4;
            public final static int Q1 = 6 * 4;
            public final static int Q2 = 7 * 4;
            public final static int Q3 = 8 * 4;
            public final static int Q4 = 9 * 4;
            public final static int TGT_Q1 = 10 * 4;
            public final static int TGT_Q2 = 11 * 4;
            public final static int TGT_Q3 = 12 * 4;
            public final static int TGT_Q4 = 13 * 4;
            public final static int RW_SPEED_X = 14 * 4;
            public final static int RW_SPEED_Y = 15 * 4;
            public final static int RW_SPEED_Z = 16 * 4;
        }

        public static class POINTING_LOOP_IDX {
            //Byte offset

            public final static int POINTING_LOOP_STATE = 0 * 4;
        }

        public static byte[] long2ByteArray(long value) {
            return ByteBuffer.allocate(8).putLong(value).array();
        }

        public static byte[] int2ByteArray(int value) {
            return ByteBuffer.allocate(4).putInt(value).array();
        }

        public static byte[] int16_2ByteArray(int value) {
            byte[] temp = ByteBuffer.allocate(4).putInt(value).array();
            byte[] result = new byte[2];
            result[0] = temp[2];
            result[1] = temp[3];
            return result;
        }

        public static byte[] float2ByteArray(float value) {
            return ByteBuffer.allocate(4).putFloat(value).array();
        }

        public static byte[] double2ByteArray(double value) {
            return ByteBuffer.allocate(8).putDouble(value).array();
        }

        public static void putFloatInByteArray(float value, int byteOffset, byte[] target) {
            byte[] tempByte = float2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, 4 - 1 + 1);
        }

        public static void putByteInByteArray(byte value, int byteOffset, byte[] target) {
            target[byteOffset] = value;
        }

        public static byte getByteFromByteArray(byte[] source, int byteOffset) {
            return source[byteOffset];
        }

        public static float getFloatFromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        public static void putDoubleInByteArray(double value, int byteOffset, byte[] target) {
            byte[] tempByte = double2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, 8 - 1 + 1);
        }

        public static double getDoubleFromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 8).order(ByteOrder.BIG_ENDIAN).getDouble();
        }

        public static void putIntInByteArray(int value, int byteOffset, byte[] target) {
            byte[] tempByte = int2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, 4 - 1 + 1);
        }

        public static int getIntFromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        }

        public static short getInt16FromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 2).order(ByteOrder.BIG_ENDIAN).getShort();
        }

        public static void putLongInByteArray(long value, int byteOffset, byte[] target) {
            byte[] tempByte = long2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, 8 - 1 + 1);
        }

        public static long getLongFromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 8).order(ByteOrder.BIG_ENDIAN).getLong();
        }

    }

    public static VectorF3D getAngularVelocityFromSensorTM(byte[] sensorTM) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.GYRO1_X),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.GYRO1_Y),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.GYRO1_Z));
    }

    public static Quaternion getAttitudeFromSensorTM(byte[] sensorTM) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.QUATERNION1),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.QUATERNION2),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.QUATERNION3),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.QUATERNION4));
    }

    public static VectorF3D getMagneticFieldFromSensorTM(byte[] sensorTM) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_X),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Y),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Z));
    }

    public static VectorF3D getMTQFromActuatorTM(byte[] actuatorTM) {
        return new VectorF3D(
                (float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                        FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_X),
                (float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                        FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_Y),
                (float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                        FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_Z));
    }

    public static WheelsSpeed getCurrentWheelSpeedFromActuatorTM(byte[] actuatorTM) {
        FloatList velocity = new FloatList();
        velocity.add((float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_X));
        velocity.add((float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_Y));
        velocity.add((float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                FWRefFineADCS.ACTUATORTM_IDX.RW_CURRENT_SPEED_Z));
        return new WheelsSpeed(velocity);
    }

    public static WheelsSpeed getTargetWheelSpeedFromActuatorTM(byte[] actuatorTM) {
        FloatList velocity = new FloatList();
        velocity.add((float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                FWRefFineADCS.ACTUATORTM_IDX.RW_LAST_TARGET_X));
        velocity.add((float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                FWRefFineADCS.ACTUATORTM_IDX.RW_LAST_TARGET_Y));
        velocity.add((float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                FWRefFineADCS.ACTUATORTM_IDX.RW_LAST_TARGET_Z));
        return new WheelsSpeed(velocity);
    }

    public static VectorF3D getSunVectorFromSpinModeStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_Z));
    }

    public static VectorF3D getMagneticFieldFromSpinModeStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_Z));
    }

    public static Quaternion getQuaternionsFromSpinModeStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.Q4));
    }

    public static VectorF3D getAngularMomentumFromSpinModeStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_Z));
    }

    public static VectorF3D getMTQFromSpinModeStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_Z));
    }

    public static VectorF3D getSunVectorFromSunPointingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_Z));
    }

    public static VectorF3D getMTQFromSunPointingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Z));
    }

    public static WheelsSpeed getWheelSpeedFromSunPointingStatus(byte[] status) {
        FloatList velocity = new FloatList();
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_X));
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Y));
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Z));
        return new WheelsSpeed(velocity);
    }

    public static VectorF3D getPositionFromFixWGS84TargetTrackingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_X),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_Z));
    }

    public static VectorF3D getAngularVelocityFromFixWGS84TargetTrackingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_X),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_Y),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_Z));
    }

    public static WheelsSpeed getWheelSpeedFromFixWGS84TargetTrackingStatus(byte[] status) {
        FloatList velocity = new FloatList();
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_X));
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_Y));
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.RW_SPEED_Z));
        return new WheelsSpeed(velocity);
    }

    public static Quaternion getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q4));
    }

    public static Quaternion getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q4));
    }

    public static VectorF3D getPositionFromNadirTargetTrackingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_X),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_Z));
    }

    public static VectorF3D getAngularVelocityFromNadirTargetTrackingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_Z));
    }

    public static Quaternion getCurrentQuaternionsFromNadirTargetTrackingStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q4));
    }

    public static Quaternion getTargetQuaternionsFromNadirTargetTrackingStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q4));
    }

    public static WheelsSpeed getWheelSpeedFromNadirTargetTrackingStatus(byte[] status) {
        FloatList velocity = new FloatList();
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_X));
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_Y));
        velocity.add(FWRefFineADCS.getFloatFromByteArray(status,
                FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.RW_SPEED_Z));
        return new WheelsSpeed(velocity);
    }

    public static byte getPointingLoopStateTarget(byte[] status) {
        byte stateTarget;
        stateTarget = FWRefFineADCS.getByteFromByteArray(status,
                FWRefFineADCS.POINTING_LOOP_IDX.POINTING_LOOP_STATE);
        return stateTarget;
    }

}
