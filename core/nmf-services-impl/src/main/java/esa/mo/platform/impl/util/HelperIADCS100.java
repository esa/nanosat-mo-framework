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
import org.ccsds.moims.mo.platform.structures.*;

/**
 * The HelperIADCS100 is an helper class to facilitate the computations related
 * with the ADCS.
 */
public class HelperIADCS100 {
    private HelperIADCS100() {
    }


    /**
     * FW Ref Fine ADCS.
     */
    public static class FWRefFineADCS {
        private FWRefFineADCS() {
        }


        /**
         * SENSORTM IDX.
         */
        public static class SENSORTM_IDX {
            private SENSORTM_IDX() {
            }

            //Byte offset

            /** The mag field x. */
            public final static int MAG_FIELD_X = 0 * 4;
            /** The mag field y. */
            public final static int MAG_FIELD_Y = 1 * 4;
            /** The mag field z. */
            public final static int MAG_FIELD_Z = 2 * 4;
            /** The accelerometer x. */
            public final static int ACCELEROMETER_X = 3 * 4;
            /** The accelerometer y. */
            public final static int ACCELEROMETER_Y = 4 * 4;
            /** The accelerometer z. */
            public final static int ACCELEROMETER_Z = 5 * 4;
            /** The gyro1 x. */
            public final static int GYRO1_X = 6 * 4;
            /** The gyro1 y. */
            public final static int GYRO1_Y = 7 * 4;
            /** The gyro1 z. */
            public final static int GYRO1_Z = 8 * 4;
            /** The gyro2 x. */
            public final static int GYRO2_X = 9 * 4;
            /** The gyro2 y. */
            public final static int GYRO2_Y = 10 * 4;
            /** The gyro2 z. */
            public final static int GYRO2_Z = 11 * 4;
            /** The st200 time. */
            public final static int ST200_TIME = 12 * 4;//UI64
            /** The st200 time msec. */
            public final static int ST200_TIME_MSEC = 14 * 4;//UI16
            /** The quaternion1. */
            public final static int QUATERNION1 = 14 * 4 + 2;
            /** The quaternion2. */
            public final static int QUATERNION2 = 15 * 4 + 2;
            /** The quaternion3. */
            public final static int QUATERNION3 = 16 * 4 + 2;
            /** The quaternion4. */
            public final static int QUATERNION4 = 17 * 4 + 2;
        }

        /**
         * ACTUATORTM IDX.
         */
        public static class ACTUATORTM_IDX {
            private ACTUATORTM_IDX() {
            }

            //Byte offset

            /** The rw current speed x. */
            public final static int RW_CURRENT_SPEED_X = 0 * 2;
            /** The rw last target x. */
            public final static int RW_LAST_TARGET_X = 1 * 2;
            /** The rw target mode x. */
            public final static int RW_TARGET_MODE_X = 2 * 2;
            /** The rw current speed y. */
            public final static int RW_CURRENT_SPEED_Y = 2 * 2 + 1;
            /** The rw last target y. */
            public final static int RW_LAST_TARGET_Y = 3 * 2 + 1;
            /** The rw target mode y. */
            public final static int RW_TARGET_MODE_Y = 4 * 2 + 1;
            /** The rw current speed z. */
            public final static int RW_CURRENT_SPEED_Z = 5 * 2;
            /** The rw last target z. */
            public final static int RW_LAST_TARGET_Z = 6 * 2;
            /** The rw target mode z. */
            public final static int RW_TARGET_MODE_Z = 7 * 2;
            /** The mtq target x. */
            public final static int MTQ_TARGET_X = 7 * 2 + 1;
            /** The mtq target y. */
            public final static int MTQ_TARGET_Y = 8 * 2 + 1;
            /** The mtq target z. */
            public final static int MTQ_TARGET_Z = 9 * 2 + 1;
        }

        /**
         * SPINMODESTAT IDX.
         */
        public static class SPINMODESTAT_IDX {
            private SPINMODESTAT_IDX() {
            }

            //Byte offset

            /** The sun vector x. */
            public final static int SUN_VECTOR_X = 0 * 4;
            /** The sun vector y. */
            public final static int SUN_VECTOR_Y = 1 * 4;
            /** The sun vector z. */
            public final static int SUN_VECTOR_Z = 2 * 4;
            /** The magnetometer x. */
            public final static int MAGNETOMETER_X = 3 * 4;
            /** The magnetometer y. */
            public final static int MAGNETOMETER_Y = 4 * 4;
            /** The magnetometer z. */
            public final static int MAGNETOMETER_Z = 5 * 4;
            /** The q1. */
            public final static int Q1 = 6 * 4;
            /** The q2. */
            public final static int Q2 = 7 * 4;
            /** The q3. */
            public final static int Q3 = 8 * 4;
            /** The q4. */
            public final static int Q4 = 9 * 4;
            /** The ang mom x. */
            public final static int ANG_MOM_X = 10 * 4;
            /** The ang mom y. */
            public final static int ANG_MOM_Y = 11 * 4;
            /** The ang mom z. */
            public final static int ANG_MOM_Z = 12 * 4;
            /** The mtq dip moment x. */
            public final static int MTQ_DIP_MOMENT_X = 13 * 4;
            /** The mtq dip moment y. */
            public final static int MTQ_DIP_MOMENT_Y = 14 * 4;
            /** The mtq dip moment z. */
            public final static int MTQ_DIP_MOMENT_Z = 15 * 4;
        }

        /**
         * SUNPOINTSTAT IDX.
         */
        public static class SUNPOINTSTAT_IDX {
            private SUNPOINTSTAT_IDX() {
            }

            //Byte offset

            /** The sun vector x. */
            public final static int SUN_VECTOR_X = 0 * 4;
            /** The sun vector y. */
            public final static int SUN_VECTOR_Y = 1 * 4;
            /** The sun vector z. */
            public final static int SUN_VECTOR_Z = 2 * 4;
            /** The sun vector valid. */
            public final static int SUN_VECTOR_VALID = 3 * 4;
            /** The actuator x. */
            public final static int ACTUATOR_X = 3 * 4 + 1;
            /** The actuator y. */
            public final static int ACTUATOR_Y = 4 * 4 + 1;
            /** The actuator z. */
            public final static int ACTUATOR_Z = 5 * 4 + 1;
        }

        /**
         * FIXWGS84 TGTTRACKSTAT IDX.
         */
        public static class FIXWGS84_TGTTRACKSTAT_IDX {
            private FIXWGS84_TGTTRACKSTAT_IDX() {
            }

            //Byte offset

            /** The position vector x. */
            public final static int POSITION_VECTOR_X = 0 * 4;
            /** The position vector y. */
            public final static int POSITION_VECTOR_Y = 1 * 4;
            /** The position vector z. */
            public final static int POSITION_VECTOR_Z = 2 * 4;
            /** The ang vel x. */
            public final static int ANG_VEL_X = 3 * 4;
            /** The ang vel y. */
            public final static int ANG_VEL_Y = 4 * 4;
            /** The ang vel z. */
            public final static int ANG_VEL_Z = 5 * 4;
            /** The q1. */
            public final static int Q1 = 6 * 4;
            /** The q2. */
            public final static int Q2 = 7 * 4;
            /** The q3. */
            public final static int Q3 = 8 * 4;
            /** The q4. */
            public final static int Q4 = 9 * 4;
            /** The tgt q1. */
            public final static int TGT_Q1 = 10 * 4;
            /** The tgt q2. */
            public final static int TGT_Q2 = 11 * 4;
            /** The tgt q3. */
            public final static int TGT_Q3 = 12 * 4;
            /** The tgt q4. */
            public final static int TGT_Q4 = 13 * 4;
            /** The rw speed x. */
            public final static int RW_SPEED_X = 14 * 4;
            /** The rw speed y. */
            public final static int RW_SPEED_Y = 15 * 4;
            /** The rw speed z. */
            public final static int RW_SPEED_Z = 16 * 4;
        }

        /**
         * NADIR TGTTRACKSTAT IDX.
         */
        public static class NADIR_TGTTRACKSTAT_IDX {
            private NADIR_TGTTRACKSTAT_IDX() {
            }

            //Byte offset

            /** The position vector x. */
            public final static int POSITION_VECTOR_X = 0 * 4;
            /** The position vector y. */
            public final static int POSITION_VECTOR_Y = 1 * 4;
            /** The position vector z. */
            public final static int POSITION_VECTOR_Z = 2 * 4;
            /** The ang vel x. */
            public final static int ANG_VEL_X = 3 * 4;
            /** The ang vel y. */
            public final static int ANG_VEL_Y = 4 * 4;
            /** The ang vel z. */
            public final static int ANG_VEL_Z = 5 * 4;
            /** The q1. */
            public final static int Q1 = 6 * 4;
            /** The q2. */
            public final static int Q2 = 7 * 4;
            /** The q3. */
            public final static int Q3 = 8 * 4;
            /** The q4. */
            public final static int Q4 = 9 * 4;
            /** The tgt q1. */
            public final static int TGT_Q1 = 10 * 4;
            /** The tgt q2. */
            public final static int TGT_Q2 = 11 * 4;
            /** The tgt q3. */
            public final static int TGT_Q3 = 12 * 4;
            /** The tgt q4. */
            public final static int TGT_Q4 = 13 * 4;
            /** The rw speed x. */
            public final static int RW_SPEED_X = 14 * 4;
            /** The rw speed y. */
            public final static int RW_SPEED_Y = 15 * 4;
            /** The rw speed z. */
            public final static int RW_SPEED_Z = 16 * 4;
        }

        /**
         * POINTING LOOP IDX.
         */
        public static class POINTING_LOOP_IDX {
            private POINTING_LOOP_IDX() {
            }

            //Byte offset

            /** The pointing loop state. */
            public final static int POINTING_LOOP_STATE = 0 * 4;
        }

        /**
         * Converts a {@code long} to its byte-array representation.
         *
         * @param value the value
         * @return the long2 byte array
         */
        public static byte[] long2ByteArray(long value) {
            return ByteBuffer.allocate(8).putLong(value).array();
        }

        /**
         * Converts an {@code int} to its byte-array representation.
         *
         * @param value the value
         * @return the int2 byte array
         */
        public static byte[] int2ByteArray(int value) {
            return ByteBuffer.allocate(4).putInt(value).array();
        }

        /**
         * Converts a 16-bit integer to its byte-array representation.
         *
         * @param value the value
         * @return the int16 2 byte array
         */
        public static byte[] int16_2ByteArray(int value) {
            byte[] temp = ByteBuffer.allocate(4).putInt(value).array();
            byte[] result = new byte[2];
            result[0] = temp[2];
            result[1] = temp[3];
            return result;
        }

        /**
         * Converts a {@code float} to its byte-array representation.
         *
         * @param value the value
         * @return the float2 byte array
         */
        public static byte[] float2ByteArray(float value) {
            return ByteBuffer.allocate(4).putFloat(value).array();
        }

        /**
         * Converts a {@code double} to its byte-array representation.
         *
         * @param value the value
         * @return the double2 byte array
         */
        public static byte[] double2ByteArray(double value) {
            return ByteBuffer.allocate(8).putDouble(value).array();
        }

        /**
         * Writes a {@code float} at the given offset of the target byte array.
         *
         * @param value the value
         * @param byteOffset the byte offset
         * @param target the target
         */
        public static void putFloatInByteArray(float value, int byteOffset, byte[] target) {
            byte[] tempByte = float2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, 4 - 1 + 1);
        }

        /**
         * Writes a {@code byte} at the given offset of the target byte array.
         *
         * @param value the value
         * @param byteOffset the byte offset
         * @param target the target
         */
        public static void putByteInByteArray(byte value, int byteOffset, byte[] target) {
            target[byteOffset] = value;
        }

        /**
         * Returns the byte from byte array.
         *
         * @param source the source
         * @param byteOffset the byte offset
         * @return the byte from byte array
         */
        public static byte getByteFromByteArray(byte[] source, int byteOffset) {
            return source[byteOffset];
        }

        /**
         * Returns the float from byte array.
         *
         * @param source the source
         * @param byteOffset the byte offset
         * @return the float from byte array
         */
        public static float getFloatFromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 4).order(ByteOrder.BIG_ENDIAN).getFloat();
        }

        /**
         * Writes a {@code double} at the given offset of the target byte array.
         *
         * @param value the value
         * @param byteOffset the byte offset
         * @param target the target
         */
        public static void putDoubleInByteArray(double value, int byteOffset, byte[] target) {
            byte[] tempByte = double2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, 8 - 1 + 1);
        }

        /**
         * Returns the double from byte array.
         *
         * @param source the source
         * @param byteOffset the byte offset
         * @return the double from byte array
         */
        public static double getDoubleFromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 8).order(ByteOrder.BIG_ENDIAN).getDouble();
        }

        /**
         * Writes an {@code int} at the given offset of the target byte array.
         *
         * @param value the value
         * @param byteOffset the byte offset
         * @param target the target
         */
        public static void putIntInByteArray(int value, int byteOffset, byte[] target) {
            byte[] tempByte = int2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, 4 - 1 + 1);
        }

        /**
         * Returns the int from byte array.
         *
         * @param source the source
         * @param byteOffset the byte offset
         * @return the int from byte array
         */
        public static int getIntFromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
        }

        /**
         * Returns the int16 from byte array.
         *
         * @param source the source
         * @param byteOffset the byte offset
         * @return the int16 from byte array
         */
        public static short getInt16FromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 2).order(ByteOrder.BIG_ENDIAN).getShort();
        }

        /**
         * Writes a {@code long} at the given offset of the target byte array.
         *
         * @param value the value
         * @param byteOffset the byte offset
         * @param target the target
         */
        public static void putLongInByteArray(long value, int byteOffset, byte[] target) {
            byte[] tempByte = long2ByteArray(value);
            System.arraycopy(tempByte, 0, target, byteOffset + 0, 8 - 1 + 1);
        }

        /**
         * Returns the long from byte array.
         *
         * @param source the source
         * @param byteOffset the byte offset
         * @return the long from byte array
         */
        public static long getLongFromByteArray(byte[] source, int byteOffset) {
            return ByteBuffer.wrap(source, byteOffset, 8).order(ByteOrder.BIG_ENDIAN).getLong();
        }

    }

    /**
     * Returns the angular velocity from sensor tm.
     *
     * @param sensorTM the sensor tm
     * @return the angular velocity from sensor tm
     */
    public static VectorF3D getAngularVelocityFromSensorTM(byte[] sensorTM) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.GYRO1_X),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.GYRO1_Y),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.GYRO1_Z));
    }

    /**
     * Returns the attitude from sensor tm.
     *
     * @param sensorTM the sensor tm
     * @return the attitude from sensor tm
     */
    public static Quaternion getAttitudeFromSensorTM(byte[] sensorTM) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.QUATERNION1),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.QUATERNION2),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.QUATERNION3),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.QUATERNION4));
    }

    /**
     * Returns the magnetic field from sensor tm.
     *
     * @param sensorTM the sensor tm
     * @return the magnetic field from sensor tm
     */
    public static VectorF3D getMagneticFieldFromSensorTM(byte[] sensorTM) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_X),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Y),
                FWRefFineADCS.getFloatFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Z));
    }

    /**
     * Returns the mtq from actuator tm.
     *
     * @param actuatorTM the actuator tm
     * @return the mtq from actuator tm
     */
    public static VectorF3D getMTQFromActuatorTM(byte[] actuatorTM) {
        return new VectorF3D(
                (float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                        FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_X),
                (float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                        FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_Y),
                (float) FWRefFineADCS.getInt16FromByteArray(actuatorTM,
                        FWRefFineADCS.ACTUATORTM_IDX.MTQ_TARGET_Z));
    }

    /**
     * Returns the current wheel speed from actuator tm.
     *
     * @param actuatorTM the actuator tm
     * @return the current wheel speed from actuator tm
     */
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

    /**
     * Returns the target wheel speed from actuator tm.
     *
     * @param actuatorTM the actuator tm
     * @return the target wheel speed from actuator tm
     */
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

    /**
     * Returns the sun vector from spin mode status.
     *
     * @param status the status
     * @return the sun vector from spin mode status
     */
    public static VectorF3D getSunVectorFromSpinModeStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.SUN_VECTOR_Z));
    }

    /**
     * Returns the magnetic field from spin mode status.
     *
     * @param status the status
     * @return the magnetic field from spin mode status
     */
    public static VectorF3D getMagneticFieldFromSpinModeStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MAGNETOMETER_Z));
    }

    /**
     * Returns the quaternions from spin mode status.
     *
     * @param status the status
     * @return the quaternions from spin mode status
     */
    public static Quaternion getQuaternionsFromSpinModeStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.Q4));
    }

    /**
     * Returns the angular momentum from spin mode status.
     *
     * @param status the status
     * @return the angular momentum from spin mode status
     */
    public static VectorF3D getAngularMomentumFromSpinModeStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.ANG_MOM_Z));
    }

    /**
     * Returns the mtq from spin mode status.
     *
     * @param status the status
     * @return the mtq from spin mode status
     */
    public static VectorF3D getMTQFromSpinModeStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SPINMODESTAT_IDX.MTQ_DIP_MOMENT_Z));
    }

    /**
     * Returns the sun vector from sun pointing status.
     *
     * @param status the status
     * @return the sun vector from sun pointing status
     */
    public static VectorF3D getSunVectorFromSunPointingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.SUN_VECTOR_Z));
    }

    /**
     * Returns the mtq from sun pointing status.
     *
     * @param status the status
     * @return the mtq from sun pointing status
     */
    public static VectorF3D getMTQFromSunPointingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.SUNPOINTSTAT_IDX.ACTUATOR_Z));
    }

    /**
     * Returns the wheel speed from sun pointing status.
     *
     * @param status the status
     * @return the wheel speed from sun pointing status
     */
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

    /**
     * Returns the position from fix wgs84 target tracking status.
     *
     * @param status the status
     * @return the position from fix wgs84 target tracking status
     */
    public static VectorF3D getPositionFromFixWGS84TargetTrackingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_X),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.POSITION_VECTOR_Z));
    }

    /**
     * Returns the angular velocity from fix wgs84 target tracking status.
     *
     * @param status the status
     * @return the angular velocity from fix wgs84 target tracking status
     */
    public static VectorF3D getAngularVelocityFromFixWGS84TargetTrackingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_X),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_Y),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.ANG_VEL_Z));
    }

    /**
     * Returns the wheel speed from fix wgs84 target tracking status.
     *
     * @param status the status
     * @return the wheel speed from fix wgs84 target tracking status
     */
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

    /**
     * Returns the current quaternions from fix wgs84 target tracking status.
     *
     * @param status the status
     * @return the current quaternions from fix wgs84 target tracking status
     */
    public static Quaternion getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.Q4));
    }

    /**
     * Returns the target quaternions from fix wgs84 target tracking status.
     *
     * @param status the status
     * @return the target quaternions from fix wgs84 target tracking status
     */
    public static Quaternion getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.FIXWGS84_TGTTRACKSTAT_IDX.TGT_Q4));
    }

    /**
     * Returns the position from nadir target tracking status.
     *
     * @param status the status
     * @return the position from nadir target tracking status
     */
    public static VectorF3D getPositionFromNadirTargetTrackingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_X),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_Y),
                FWRefFineADCS.getFloatFromByteArray(status,
                        FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.POSITION_VECTOR_Z));
    }

    /**
     * Returns the angular velocity from nadir target tracking status.
     *
     * @param status the status
     * @return the angular velocity from nadir target tracking status
     */
    public static VectorF3D getAngularVelocityFromNadirTargetTrackingStatus(byte[] status) {
        return new VectorF3D(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_X),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_Y),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.ANG_VEL_Z));
    }

    /**
     * Returns the current quaternions from nadir target tracking status.
     *
     * @param status the status
     * @return the current quaternions from nadir target tracking status
     */
    public static Quaternion getCurrentQuaternionsFromNadirTargetTrackingStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.Q4));
    }

    /**
     * Returns the target quaternions from nadir target tracking status.
     *
     * @param status the status
     * @return the target quaternions from nadir target tracking status
     */
    public static Quaternion getTargetQuaternionsFromNadirTargetTrackingStatus(byte[] status) {
        return new Quaternion(
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q1),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q2),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q3),
                FWRefFineADCS.getFloatFromByteArray(status, FWRefFineADCS.NADIR_TGTTRACKSTAT_IDX.TGT_Q4));
    }

    /**
     * Returns the wheel speed from nadir target tracking status.
     *
     * @param status the status
     * @return the wheel speed from nadir target tracking status
     */
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

    /**
     * Returns the pointing loop state target.
     *
     * @param status the status
     * @return the pointing loop state target
     */
    public static byte getPointingLoopStateTarget(byte[] status) {
        byte stateTarget;
        stateTarget = FWRefFineADCS.getByteFromByteArray(status,
                FWRefFineADCS.POINTING_LOOP_IDX.POINTING_LOOP_STATE);
        return stateTarget;
    }

}
