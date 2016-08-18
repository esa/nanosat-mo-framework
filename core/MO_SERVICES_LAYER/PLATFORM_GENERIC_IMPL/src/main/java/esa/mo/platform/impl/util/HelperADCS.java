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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.ccsds.moims.mo.platform.structures.Quaternions;
import org.ccsds.moims.mo.platform.structures.Vector3D;
import org.ccsds.moims.mo.platform.structures.WheelSpeed;


/**
 *
 * @author Cesar Coelho
 */
public class HelperADCS {
    public static class FWRefFineADCS {
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
        public static byte [] long2ByteArray (long value)
        {
            return ByteBuffer.allocate(Long.BYTES).putLong(value).array();
        }
        public static byte [] int2ByteArray (int value)
        {  
             return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
        }
        public static byte [] int16_2ByteArray (int value)
        {  
             byte[] temp=ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
             byte[] result=new byte[2];
             result[0]=temp[2];
             result[1]=temp[3];
             return result;
        }
        public static byte [] float2ByteArray (float value)
        {  
             return ByteBuffer.allocate(Float.BYTES).putFloat(value).array();
        }
        public static byte [] double2ByteArray (double value)
        {  
             return ByteBuffer.allocate(Double.BYTES).putDouble(value).array();
        }
        
        public static void putFloatInByteArray(float value, int byteOffset, byte[] target)
        {
            byte [] tempByte=float2ByteArray(value);
            for (int i=0;i<=Float.BYTES-1;i++)
            {
                target[byteOffset+i]=tempByte[i];
            }
        }
        public static float getFloatFromByteArray(byte[] source, int byteOffset)
        {
            return ByteBuffer.wrap(source,byteOffset,Float.BYTES).order(ByteOrder.BIG_ENDIAN).getFloat();
        }
        public static void putDoubleInByteArray(double value, int byteOffset, byte[] target)
        {
            byte [] tempByte=double2ByteArray(value);
            for (int i=0;i<=Double.BYTES-1;i++)
            {
                target[byteOffset+i]=tempByte[i];
            }
        }
        public static double getDoubleFromByteArray(byte[] source, int byteOffset)
        {
            return ByteBuffer.wrap(source,byteOffset,Double.BYTES).order(ByteOrder.BIG_ENDIAN).getDouble();
        }
        public static void putIntInByteArray(int value, int byteOffset, byte[] target)
        {
            byte [] tempByte=int2ByteArray(value);
            for (int i=0;i<=Integer.BYTES-1;i++)
            {
                target[byteOffset+i]=tempByte[i];
            }
        }
        public static int getIntFromByteArray(byte[] source, int byteOffset)
        {
            return ByteBuffer.wrap(source,byteOffset,Integer.BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
        }
        public static int getInt16FromByteArray(byte[] source, int byteOffset)
        {
            byte[] temp=new byte[Integer.BYTES];
            temp[2]=source[byteOffset];
            temp[3]=source[byteOffset+1];
            return ByteBuffer.wrap(temp,byteOffset,Integer.BYTES).order(ByteOrder.BIG_ENDIAN).getInt();
        }
        public static void putLongInByteArray(long value, int byteOffset, byte[] target)
        {
            byte [] tempByte=long2ByteArray(value);
            for (int i=0;i<=Long.BYTES-1;i++)
            {
                target[byteOffset+i]=tempByte[i];
            }
        }
        public static long getLongFromByteArray(byte[] source, int byteOffset)
        {
            return ByteBuffer.wrap(source,byteOffset,Long.BYTES).order(ByteOrder.BIG_ENDIAN).getLong();
        }
    
    }
    public static Vector3D getMagneticFieldFromSensorTM(byte[] sensorTM) {
        return new Vector3D(
            FWRefFineADCS.getDoubleFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_X),
            FWRefFineADCS.getDoubleFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Y),
            FWRefFineADCS.getDoubleFromByteArray(sensorTM, FWRefFineADCS.SENSORTM_IDX.MAG_FIELD_Z));        
    }

    public static Vector3D getMTQFromActuatorTM(byte[] actuatorTM) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static WheelSpeed getWheelSpeedFromActuatorTM(byte[] actuatorTM) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Vector3D getSunVectorFromSpinModeStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Vector3D getMagneticFieldFromSpinModeStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Quaternions getQuaternionsFromSpinModeStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Vector3D getAngularMomentumFromSpinModeStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Vector3D getMTQFromSpinModeStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Vector3D getSunVectorFromSunPointingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Vector3D getMTQFromSunPointingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static WheelSpeed getWheelSpeedFromSunPointingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Vector3D getMagneticFieldFromFixWGS84TargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Double getMTQFromFixWGS84TargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static WheelSpeed getWheelSpeedFromFixWGS84TargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Quaternions getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Quaternions getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Vector3D getPositionFromNadirTargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Double getAngularVelocityFromNadirTargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Quaternions getCurrentQuaternionsFromNadirTargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Quaternions getTargetQuaternionsFromNadirTargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static WheelSpeed getWheelSpeedFromNadirTargetTrackingStatus(byte[] status) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }



}
