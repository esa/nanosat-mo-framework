package esa.nmf.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest1 {

    public static boolean debug = false;

    @Test
    public void test0501() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0501");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((-5.0d));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0502() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0502");
        }
        byte[] byteArray5 = new byte[]{(byte) -1, (byte) -1, (byte) 0, (byte) 100, (byte) 100};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0503() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0503");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0504() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0504");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0505() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0505");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) 'a');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0506() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0506");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0507() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0507");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0508() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0508");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        long long3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 4625759767262920704L + "'", long3 == 4625759767262920704L);
    }

    @Test
    public void test0509() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0509");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487876L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0510() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0510");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList1 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int2 = floatList1.getTypeShortForm();
        java.lang.Integer int3 = floatList1.getTypeShortForm();
        java.lang.Integer int4 = floatList1.getTypeShortForm();
        boolean boolean6 = floatList1.remove((java.lang.Object) 36);
        byte[] byteArray8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int9 = floatList1.lastIndexOf((java.lang.Object) byteArray8);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion10 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray8);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-4) + "'", int2.equals((-4)));
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-4) + "'", int3.equals((-4)));
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-4) + "'", int4.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + false + "'", !boolean6);
        org.junit.Assert.assertNotNull(byteArray8);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-1) + "'", int9 == (-1));
    }

    @Test
    public void test0511() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0511");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[281474993487875]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0512() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0512");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0513() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0513");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0514() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0514");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-15), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0515() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0515");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) -1 + "'", short3 == (short) -1);
    }

    @Test
    public void test0516() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0516");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0517() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0517");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList1 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int2 = floatList1.getTypeShortForm();
        java.lang.Integer int3 = floatList1.getTypeShortForm();
        java.lang.Integer int4 = floatList1.getTypeShortForm();
        boolean boolean6 = floatList1.remove((java.lang.Object) 36);
        byte[] byteArray8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int9 = floatList1.lastIndexOf((java.lang.Object) byteArray8);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion10 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray8);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-4) + "'", int2.equals((-4)));
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-4) + "'", int3.equals((-4)));
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-4) + "'", int4.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + false + "'", !boolean6);
        org.junit.Assert.assertNotNull(byteArray8);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-1) + "'", int9 == (-1));
    }

    @Test
    public void test0518() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0518");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0520() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0520");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (short) 255);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0521() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0521");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0522() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0522");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(10L, (int) (short) 0, byteArray5);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed8 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0523() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0523");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) -1 + "'", short3 == (short) -1);
    }

    @Test
    public void test0525() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0525");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(32);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0526() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0526");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0527() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0527");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0528() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0528");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(
            (int) (short) 19456);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0529() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0529");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0530() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0530");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("2.81474993487887E14");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0531() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0531");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("10");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0532() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0532");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0533() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0533");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-7));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0534() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0534");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0535() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0535");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0536() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0536");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0537() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0537");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0538() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0538");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265070L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0539() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0539");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0541() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0541");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0542() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0542");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0543() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0543");
        }
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = octetList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = octetList0.getAreaVersion();
        java.lang.Integer int3 = uOctet2.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = uOctet2.getServiceNumber();
        byte[] byteArray6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        boolean boolean7 = uOctet2.equals((java.lang.Object) byteArray6);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed8 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 8 + "'", int3.equals(8));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    }

    @Test
    public void test0544() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0544");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(20);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0545() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0545");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((-5.0d));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0546() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0546");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 1L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0548() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0548");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0549() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0549");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0550() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0550");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees(
                "[17, 15, 0, 6, 0, -1, 12, 14, 60, 4, 9, 14, 17, 5, -1, -10, -9, 17, 15, 17, 19, 2, 44, 65535, 3, 70, 36, 100, 10]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0551() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0551");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        float float6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getFloatFromByteArray(byteArray3,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D7 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + float6 + "' != '" + 2.8147501E14f + "'", float6 == 2.8147501E14f);
    }

    @Test
    public void test0552() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0552");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0553() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0553");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        long long7 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray4,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed8 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertTrue("'" + long7 + "' != '" + 4823355201182236832L + "'", long7 == 4823355201182236832L);
    }

    @Test
    public void test0554() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0554");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0555() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0555");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0556() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0556");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 15 + "'", short3 == (short) 15);
    }

    @Test
    public void test0557() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0557");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 255);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0558() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0558");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 24);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0559() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0559");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-18));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0560() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0560");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0561() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0561");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0562() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0562");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0563() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0563");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0564() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0564");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0566() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0566");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0567() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0567");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0568() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0568");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0569() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0569");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-13));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0570() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0570");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0571() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0571");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 65536 + "'", int3 == 65536);
    }

    @Test
    public void test0572() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0572");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 65536);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0573() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0573");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[false, false]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0574() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0574");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 100L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0576() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0576");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 6);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0577() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0577");
        }
        byte[] byteArray6 = new byte[]{(byte) 1, (byte) 100, (byte) -1, (byte) -1, (byte) 100, (byte) 1};
        int int8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray6, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed9 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + 1694498660 + "'", int8 == 1694498660);
    }

    @Test
    public void test0578() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0578");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-2));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0579() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0579");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0580() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0580");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 65536 + "'", int3 == 65536);
    }

    @Test
    public void test0581() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0581");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0582() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0582");
        }
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = octetList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = octetList0.getAreaVersion();
        java.lang.Integer int3 = uOctet2.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = uOctet2.getServiceNumber();
        byte[] byteArray6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        boolean boolean7 = uOctet2.equals((java.lang.Object) byteArray6);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 8 + "'", int3.equals(8));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    }

    @Test
    public void test0583() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0583");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487876L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0584() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0584");
        }
        java.lang.Integer[] intArray4 = new java.lang.Integer[]{(-7), 8, (-4), 4};
        java.util.ArrayList<java.lang.Integer> intList5 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList5, intArray4);
        boolean boolean7 = intList5.isEmpty();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray8 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList9 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean10 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList9, uRIArray8);
        java.lang.Double[] doubleArray14 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList15 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList15,
            doubleArray14);
        java.lang.Byte[] byteArray21 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList22 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean23 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList22,
            byteArray21);
        java.lang.String str24 = byteList22.toString();
        boolean boolean25 = doubleList15.retainAll((java.util.Collection<java.lang.Byte>) byteList22);
        boolean boolean26 = uRIList9.containsAll((java.util.Collection<java.lang.Byte>) byteList22);
        java.lang.Integer[] intArray39 = new java.lang.Integer[]{6, 6, 28, 2, 4, 0, 62, 64, 19, 20, 11, 62};
        java.util.ArrayList<java.lang.Integer> intList40 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean41 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList40,
            intArray39);
        java.lang.Byte[] byteArray45 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList46 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean47 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList46,
            byteArray45);
        boolean boolean49 = byteList46.add((java.lang.Byte) (byte) 0);
        java.util.stream.Stream<java.lang.Byte> byteStream50 = byteList46.parallelStream();
        int int52 = byteList46.indexOf((java.lang.Object) 5);
        boolean boolean53 = intList40.contains((java.lang.Object) byteList46);
        boolean boolean54 = uRIList9.retainAll((java.util.Collection<java.lang.Byte>) byteList46);
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.URI> uRISpliterator55 = uRIList9.spliterator();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream56 = uRIList9.stream();
        esa.mo.platform.impl.util.HelperGPS helperGPS57 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS58 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS59 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS60 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS61 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS62 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray63 = new esa.mo.platform.impl.util.HelperGPS[]{helperGPS57,
                                                                                                           helperGPS58,
                                                                                                           helperGPS59,
                                                                                                           helperGPS60,
                                                                                                           helperGPS61,
                                                                                                           helperGPS62};
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray64 = uRIList9.toArray(helperGPSArray63);
        try {
            esa.mo.platform.impl.util.HelperGPS[] helperGPSArray65 = intList5.toArray(helperGPSArray63);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayStoreException; message: null");
        } catch (java.lang.ArrayStoreException e) {
        }
        org.junit.Assert.assertNotNull(intArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(uRIArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(doubleArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertNotNull(byteArray21);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
        org.junit.Assert.assertTrue("'" + str24 + "' != '" + "[100, 1, -1, 10]" + "'", str24.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(intArray39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertNotNull(byteArray45);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
        org.junit.Assert.assertNotNull(byteStream50);
        org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-1) + "'", int52 == (-1));
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertNotNull(uRISpliterator55);
        org.junit.Assert.assertNotNull(uRIStream56);
        org.junit.Assert.assertNotNull(helperGPSArray63);
        org.junit.Assert.assertNotNull(helperGPSArray64);
    }

    @Test
    public void test0585() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0585");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        int int6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray3, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D7 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 16672 + "'", int6 == 16672);
    }

    @Test
    public void test0586() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0586");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0587() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0587");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0588() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0588");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 19);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0589() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0589");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4294967295L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0590() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0590");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-15), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0591() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0591");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265070L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0592() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0592");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("15.0");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0593() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0593");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) (-15));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0594() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0594");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 65536);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0595() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0595");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0596() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0596");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(12);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0597() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0597");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(9);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0598() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0598");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 31);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0599() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0599");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) (short) 100);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray(18, 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0600() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0600");
        }
        org.ccsds.moims.mo.mal.structures.UShortList uShortList1 = new org.ccsds.moims.mo.mal.structures.UShortList(48);
        java.lang.Long long2 = uShortList1.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element3 = uShortList1.createElement();
        org.ccsds.moims.mo.mal.structures.Element element4 = uShortList1.createElement();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator5 = uShortList1.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort6 = uShortList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort7 = uShortList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort8 = org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
        org.ccsds.moims.mo.mal.structures.UShort[] uShortArray9 = new org.ccsds.moims.mo.mal.structures.UShort[]{uShort8};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList10 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
        boolean boolean11 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList10, uShortArray9);
        int int12 = uShortList10.size();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator13 = uShortList10
            .spliterator();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream14 = uShortList10
            .parallelStream();
        boolean boolean15 = uShortList1.equals((java.lang.Object) uShortList10);
        org.ccsds.moims.mo.mal.structures.FloatList floatList17 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int18 = floatList17.getTypeShortForm();
        java.lang.Integer int19 = floatList17.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort20 = floatList17.getAreaNumber();
        java.util.stream.Stream<java.lang.Float> floatStream21 = floatList17.parallelStream();
        int int22 = uShortList10.indexOf((java.lang.Object) floatList17);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList24 = new org.ccsds.moims.mo.mal.structures.BooleanList(
            100);
        int int25 = booleanList24.size();
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList26 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj27 = doubleList26.clone();
        java.lang.String[] strArray32 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList33 = new java.util.ArrayList<java.lang.String>();
        boolean boolean34 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList33,
            strArray32);
        strList33.add((int) (short) 0, "[100, 1, -1, 10]");
        int int38 = doubleList26.lastIndexOf((java.lang.Object) strList33);
        boolean boolean39 = doubleList26.isEmpty();
        java.lang.Object obj40 = doubleList26.clone();
        boolean boolean42 = doubleList26.add((java.lang.Double) (-1.0d));
        java.lang.Double[] doubleArray46 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList47 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList47,
            doubleArray46);
        java.lang.Byte[] byteArray53 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList54 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean55 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList54,
            byteArray53);
        java.lang.String str56 = byteList54.toString();
        boolean boolean57 = doubleList47.retainAll((java.util.Collection<java.lang.Byte>) byteList54);
        java.lang.Byte[] byteArray62 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList63 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean64 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList63,
            byteArray62);
        java.lang.String str65 = byteList63.toString();
        java.lang.Byte byte68 = byteList63.set(0, (java.lang.Byte) (byte) 10);
        boolean boolean69 = byteList54.containsAll((java.util.Collection<java.lang.Byte>) byteList63);
        boolean boolean70 = doubleList26.retainAll((java.util.Collection<java.lang.Byte>) byteList54);
        boolean boolean71 = booleanList24.containsAll((java.util.Collection<java.lang.Byte>) byteList54);
        boolean boolean72 = floatList17.removeAll((java.util.Collection<java.lang.Byte>) byteList54);
        org.ccsds.moims.mo.mal.structures.Element element73 = floatList17.createElement();
        byte[] byteArray75 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        int int76 = floatList17.lastIndexOf((java.lang.Object) byteArray75);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed77 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray75);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265078L + "'", long2.equals(281475010265078L));
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(element4);
        org.junit.Assert.assertNotNull(uShortSpliterator5);
        org.junit.Assert.assertNotNull(uShort6);
        org.junit.Assert.assertNotNull(uShort7);
        org.junit.Assert.assertNotNull(uShort8);
        org.junit.Assert.assertNotNull(uShortArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + int12 + "' != '" + 1 + "'", int12 == 1);
        org.junit.Assert.assertNotNull(uShortSpliterator13);
        org.junit.Assert.assertNotNull(uShortStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-4) + "'", int18.equals((-4)));
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + (-4) + "'", int19.equals((-4)));
        org.junit.Assert.assertNotNull(uShort20);
        org.junit.Assert.assertNotNull(floatStream21);
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-1) + "'", int22 == (-1));
        org.junit.Assert.assertTrue("'" + int25 + "' != '" + 0 + "'", int25 == 0);
        org.junit.Assert.assertNotNull(obj27);
        org.junit.Assert.assertNotNull(strArray32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertNotNull(obj40);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
        org.junit.Assert.assertNotNull(doubleArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(byteArray53);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
        org.junit.Assert.assertTrue("'" + str56 + "' != '" + "[100, 1, -1, 10]" + "'", str56.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertNotNull(byteArray62);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
        org.junit.Assert.assertTrue("'" + str65 + "' != '" + "[100, 1, -1, 10]" + "'", str65.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + byte68 + "' != '" + (byte) 100 + "'", byte68.equals((byte) 100));
        org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + true + "'", boolean69);
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
        org.junit.Assert.assertNotNull(element73);
        org.junit.Assert.assertNotNull(byteArray75);
        org.junit.Assert.assertTrue("'" + int76 + "' != '" + (-1) + "'", int76 == (-1));
    }

    @Test
    public void test0601() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0601");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("5");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0603() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0603");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 24);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0604() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0604");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        double double3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + double3 + "' != '" + 2.81475010265078E14d + "'", double3 ==
            2.81475010265078E14d);
    }

    @Test
    public void test0605() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0605");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0606() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0606");
        }
        java.lang.Long[] longArray26 = new java.lang.Long[]{281474993487884L, (-1L), 281475010265083L, 281475010265070L,
                                                            281474993487883L, 281474993487885L, 281474993487872L,
                                                            281475010265070L, 281474993487885L, 281474993487889L, (-1L),
                                                            281475010265086L, 281475010265078L, 4294967295L,
                                                            281474993487874L, 281474993487885L, 281474993487886L, 1L,
                                                            281475010265086L, 281475010265075L, 281474993487876L,
                                                            281474993487878L, 281474993487889L, (-1L), 100L,
                                                            281474993487875L};
        java.util.ArrayList<java.lang.Long> longList27 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean28 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList27,
            longArray26);
        java.lang.Long long30 = longList27.remove(0);
        byte[] byteArray32 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        boolean boolean33 = longList27.equals((java.lang.Object) byteArray32);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D34 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray32);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(longArray26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertTrue("'" + long30 + "' != '" + 281474993487884L + "'", long30.equals(281474993487884L));
        org.junit.Assert.assertNotNull(byteArray32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
    }

    @Test
    public void test0607() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0607");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (short) 255);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0608() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0608");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0609() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0609");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0610() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0610");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0611() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0611");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        int int9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray5, 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion10 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    }

    @Test
    public void test0612() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0612");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0613() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0613");
        }
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray0 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList1 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean2 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList1, uRIArray0);
        java.lang.Double[] doubleArray6 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList7 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList7,
            doubleArray6);
        java.lang.Byte[] byteArray13 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList14 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList14,
            byteArray13);
        java.lang.String str16 = byteList14.toString();
        boolean boolean17 = doubleList7.retainAll((java.util.Collection<java.lang.Byte>) byteList14);
        boolean boolean18 = uRIList1.containsAll((java.util.Collection<java.lang.Byte>) byteList14);
        java.lang.Integer[] intArray31 = new java.lang.Integer[]{6, 6, 28, 2, 4, 0, 62, 64, 19, 20, 11, 62};
        java.util.ArrayList<java.lang.Integer> intList32 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean33 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList32,
            intArray31);
        java.lang.Byte[] byteArray37 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList38 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean39 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList38,
            byteArray37);
        boolean boolean41 = byteList38.add((java.lang.Byte) (byte) 0);
        java.util.stream.Stream<java.lang.Byte> byteStream42 = byteList38.parallelStream();
        int int44 = byteList38.indexOf((java.lang.Object) 5);
        boolean boolean45 = intList32.contains((java.lang.Object) byteList38);
        boolean boolean46 = uRIList1.retainAll((java.util.Collection<java.lang.Byte>) byteList38);
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.URI> uRISpliterator47 = uRIList1.spliterator();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream48 = uRIList1.stream();
        esa.mo.platform.impl.util.HelperGPS helperGPS49 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS50 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS51 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS52 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS53 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS54 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray55 = new esa.mo.platform.impl.util.HelperGPS[]{helperGPS49,
                                                                                                           helperGPS50,
                                                                                                           helperGPS51,
                                                                                                           helperGPS52,
                                                                                                           helperGPS53,
                                                                                                           helperGPS54};
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray56 = uRIList1.toArray(helperGPSArray55);
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor57 = uRIList1.iterator();
        org.junit.Assert.assertNotNull(uRIArray0);
        org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + false + "'", !boolean2);
        org.junit.Assert.assertNotNull(doubleArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(byteArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + str16 + "' != '" + "[100, 1, -1, 10]" + "'", str16.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
        org.junit.Assert.assertNotNull(intArray31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertNotNull(byteArray37);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertNotNull(byteStream42);
        org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-1) + "'", int44 == (-1));
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + false + "'", !boolean45);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + false + "'", !boolean46);
        org.junit.Assert.assertNotNull(uRISpliterator47);
        org.junit.Assert.assertNotNull(uRIStream48);
        org.junit.Assert.assertNotNull(helperGPSArray55);
        org.junit.Assert.assertNotNull(helperGPSArray56);
        org.junit.Assert.assertNotNull(uRIItor57);
    }

    @Test
    public void test0614() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0614");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0615() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0615");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList5 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int6 = floatList5.getTypeShortForm();
        java.lang.Integer int7 = floatList5.getTypeShortForm();
        java.lang.Integer int8 = floatList5.getTypeShortForm();
        boolean boolean10 = floatList5.remove((java.lang.Object) 36);
        byte[] byteArray12 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int13 = floatList5.lastIndexOf((java.lang.Object) byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 40, 0, byteArray12);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed16 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray12);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-4) + "'", int7.equals((-4)));
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-4) + "'", int8.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(byteArray12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
    }

    @Test
    public void test0616() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0616");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 66);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0617() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0617");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[100, 1, 1, -1, 10]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0618() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0618");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0619() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0619");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[1]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0620() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0620");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0621() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0621");
        }
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = octetList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = octetList0.getAreaVersion();
        java.lang.Integer int3 = uOctet2.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = uOctet2.getServiceNumber();
        byte[] byteArray6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        boolean boolean7 = uOctet2.equals((java.lang.Object) byteArray6);
        short short9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray6,
            (int) (short) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion10 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 8 + "'", int3.equals(8));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertTrue("'" + short9 + "' != '" + (short) 256 + "'", short9 == (short) 256);
    }

    @Test
    public void test0622() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0622");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81475010265073E14d);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0623() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0623");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0624() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0624");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0625() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0625");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0626() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0626");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) -1 + "'", short3 == (short) -1);
    }

    @Test
    public void test0627() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0627");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0628() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0628");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0629() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0629");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0631() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0631");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 1);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0632() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0632");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0633() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0633");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) '4');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0634() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0634");
        }
        org.ccsds.moims.mo.mal.structures.UShortList uShortList1 = new org.ccsds.moims.mo.mal.structures.UShortList(48);
        java.lang.Long long2 = uShortList1.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element3 = uShortList1.createElement();
        org.ccsds.moims.mo.mal.structures.Element element4 = uShortList1.createElement();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator5 = uShortList1.spliterator();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor6 = uShortList1.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort7 = uShortList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray8 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList9 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean10 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList9, uRIArray8);
        java.lang.Double[] doubleArray14 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList15 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList15,
            doubleArray14);
        java.lang.Byte[] byteArray21 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList22 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean23 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList22,
            byteArray21);
        java.lang.String str24 = byteList22.toString();
        boolean boolean25 = doubleList15.retainAll((java.util.Collection<java.lang.Byte>) byteList22);
        boolean boolean26 = uRIList9.containsAll((java.util.Collection<java.lang.Byte>) byteList22);
        uRIList9.ensureCapacity((int) (short) 1);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList29 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj30 = doubleList29.clone();
        java.lang.String[] strArray35 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList36 = new java.util.ArrayList<java.lang.String>();
        boolean boolean37 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList36,
            strArray35);
        strList36.add((int) (short) 0, "[100, 1, -1, 10]");
        int int41 = doubleList29.lastIndexOf((java.lang.Object) strList36);
        java.lang.Byte[] byteArray45 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList46 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean47 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList46,
            byteArray45);
        boolean boolean49 = byteList46.add((java.lang.Byte) (byte) 0);
        java.lang.Byte[] byteArray55 = new java.lang.Byte[]{(byte) 10, (byte) 1, (byte) -1, (byte) 10, (byte) 0};
        java.util.ArrayList<java.lang.Byte> byteList56 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean57 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList56,
            byteArray55);
        boolean boolean58 = byteList46.removeAll((java.util.Collection<java.lang.Byte>) byteList56);
        boolean boolean59 = doubleList29.removeAll((java.util.Collection<java.lang.Byte>) byteList46);
        org.ccsds.moims.mo.mal.structures.OctetList octetList60 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet61 = octetList60.getAreaVersion();
        boolean boolean62 = doubleList29.retainAll((java.util.Collection<java.lang.Byte>) octetList60);
        boolean boolean63 = uRIList9.equals((java.lang.Object) octetList60);
        org.ccsds.moims.mo.mal.structures.Element element64 = octetList60.createElement();
        java.lang.Long long65 = octetList60.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort66 = octetList60.getServiceNumber();
        java.util.stream.Stream<java.lang.Byte> byteStream67 = octetList60.stream();
        boolean boolean68 = uShortList1.retainAll((java.util.Collection<java.lang.Byte>) octetList60);
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor70 = uShortList1.listIterator(
            (int) (byte) 0);
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator71 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator72 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator73 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator74 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator75 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator76 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray77 = new esa.mo.platform.impl.util.PositionsCalculator[]{positionsCalculator71,
                                                                                                                                         positionsCalculator72,
                                                                                                                                         positionsCalculator73,
                                                                                                                                         positionsCalculator74,
                                                                                                                                         positionsCalculator75,
                                                                                                                                         positionsCalculator76};
        esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray78 = uShortList1.toArray(
            positionsCalculatorArray77);
        org.ccsds.moims.mo.mal.structures.StringList stringList80 = new org.ccsds.moims.mo.mal.structures.StringList(
            17);
        org.ccsds.moims.mo.mal.structures.UShort uShort81 = stringList80.getServiceNumber();
        boolean boolean82 = uShortList1.add(uShort81);
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265078L + "'", long2.equals(281475010265078L));
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(element4);
        org.junit.Assert.assertNotNull(uShortSpliterator5);
        org.junit.Assert.assertNotNull(uShortItor6);
        org.junit.Assert.assertNotNull(uShort7);
        org.junit.Assert.assertNotNull(uRIArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(doubleArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertNotNull(byteArray21);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
        org.junit.Assert.assertTrue("'" + str24 + "' != '" + "[100, 1, -1, 10]" + "'", str24.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(obj30);
        org.junit.Assert.assertNotNull(strArray35);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
        org.junit.Assert.assertTrue("'" + int41 + "' != '" + (-1) + "'", int41 == (-1));
        org.junit.Assert.assertNotNull(byteArray45);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
        org.junit.Assert.assertNotNull(byteArray55);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + true + "'", boolean58);
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + false + "'", !boolean59);
        org.junit.Assert.assertNotNull(uOctet61);
        org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + false + "'", !boolean62);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertNotNull(element64);
        org.junit.Assert.assertTrue("'" + long65 + "' != '" + 281475010265081L + "'", long65.equals(281475010265081L));
        org.junit.Assert.assertNotNull(uShort66);
        org.junit.Assert.assertNotNull(byteStream67);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + false + "'", !boolean68);
        org.junit.Assert.assertNotNull(uShortItor70);
        org.junit.Assert.assertNotNull(positionsCalculatorArray77);
        org.junit.Assert.assertNotNull(positionsCalculatorArray78);
        org.junit.Assert.assertNotNull(uShort81);
        org.junit.Assert.assertTrue("'" + boolean82 + "' != '" + true + "'", boolean82);
    }

    @Test
    public void test0635() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0635");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81475010265073E14d);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0636() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0636");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0637() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0637");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(44);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0638() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0638");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 1,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0639() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0639");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) (-15));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0640() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0640");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        int int6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray3, 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion7 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 0 + "'", int6 == 0);
    }

    @Test
    public void test0641() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0641");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0642() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0642");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        int int9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray5, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D10 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    }

    @Test
    public void test0643() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0643");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0644() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0644");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0646() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0646");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((-5.0d));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0647() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0647");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(58);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0648() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0648");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 65536);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0649() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0649");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81475010265073E14d);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0650() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0650");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) '#');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0651() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0651");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("false");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0652() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0652");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0653() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0653");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0654() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0654");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        long long7 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray4,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertTrue("'" + long7 + "' != '" + 4823355201182236832L + "'", long7 == 4823355201182236832L);
    }

    @Test
    public void test0655() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0655");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4625759767262920704L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0656() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0656");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0657() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0657");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0658() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0658");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("0");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0659() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0659");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-4));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0661() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0661");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 31);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0663() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0663");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(70);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0664() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0664");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (short) 255);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0665() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0665");
        }
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = octetList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = octetList0.getAreaVersion();
        java.lang.Integer int3 = uOctet2.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = uOctet2.getServiceNumber();
        byte[] byteArray6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        boolean boolean7 = uOctet2.equals((java.lang.Object) byteArray6);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed8 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 8 + "'", int3.equals(8));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
    }

    @Test
    public void test0666() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0666");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((-5.0d));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0667() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0667");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 15 + "'", short3 == (short) 15);
    }

    @Test
    public void test0669() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0669");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) ' ');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0670() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0670");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 1,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0671() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0671");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-2), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0672() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0672");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        float float6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getFloatFromByteArray(byteArray3,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed7 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + float6 + "' != '" + 2.8147501E14f + "'", float6 == 2.8147501E14f);
    }

    @Test
    public void test0673() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0673");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        int int9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray5, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D10 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    }

    @Test
    public void test0675() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0675");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 15 + "'", short3 == (short) 15);
    }

    @Test
    public void test0676() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0676");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[true]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0677() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0677");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0678() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0678");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 65536 + "'", int3 == 65536);
    }

    @Test
    public void test0679() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0679");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0680() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0680");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(44);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0681() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0681");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[100, 1, -1, 10, 1]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0682() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0682");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 15 + "'", short3 == (short) 15);
    }

    @Test
    public void test0683() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0683");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-4));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0684() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0684");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(6371.0d);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0687() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0687");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0688() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0688");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[52]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0689() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0689");
        }
        org.ccsds.moims.mo.mal.structures.UShortList uShortList1 = new org.ccsds.moims.mo.mal.structures.UShortList(48);
        java.lang.Long long2 = uShortList1.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element3 = uShortList1.createElement();
        org.ccsds.moims.mo.mal.structures.Element element4 = uShortList1.createElement();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator5 = uShortList1.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort6 = uShortList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort7 = uShortList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort8 = org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
        org.ccsds.moims.mo.mal.structures.UShort[] uShortArray9 = new org.ccsds.moims.mo.mal.structures.UShort[]{uShort8};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList10 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
        boolean boolean11 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList10, uShortArray9);
        int int12 = uShortList10.size();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator13 = uShortList10
            .spliterator();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream14 = uShortList10
            .parallelStream();
        boolean boolean15 = uShortList1.equals((java.lang.Object) uShortList10);
        org.ccsds.moims.mo.mal.structures.FloatList floatList17 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int18 = floatList17.getTypeShortForm();
        java.lang.Integer int19 = floatList17.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort20 = floatList17.getAreaNumber();
        java.util.stream.Stream<java.lang.Float> floatStream21 = floatList17.parallelStream();
        int int22 = uShortList10.indexOf((java.lang.Object) floatList17);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList24 = new org.ccsds.moims.mo.mal.structures.BooleanList(
            100);
        int int25 = booleanList24.size();
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList26 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj27 = doubleList26.clone();
        java.lang.String[] strArray32 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList33 = new java.util.ArrayList<java.lang.String>();
        boolean boolean34 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList33,
            strArray32);
        strList33.add((int) (short) 0, "[100, 1, -1, 10]");
        int int38 = doubleList26.lastIndexOf((java.lang.Object) strList33);
        boolean boolean39 = doubleList26.isEmpty();
        java.lang.Object obj40 = doubleList26.clone();
        boolean boolean42 = doubleList26.add((java.lang.Double) (-1.0d));
        java.lang.Double[] doubleArray46 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList47 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList47,
            doubleArray46);
        java.lang.Byte[] byteArray53 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList54 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean55 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList54,
            byteArray53);
        java.lang.String str56 = byteList54.toString();
        boolean boolean57 = doubleList47.retainAll((java.util.Collection<java.lang.Byte>) byteList54);
        java.lang.Byte[] byteArray62 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList63 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean64 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList63,
            byteArray62);
        java.lang.String str65 = byteList63.toString();
        java.lang.Byte byte68 = byteList63.set(0, (java.lang.Byte) (byte) 10);
        boolean boolean69 = byteList54.containsAll((java.util.Collection<java.lang.Byte>) byteList63);
        boolean boolean70 = doubleList26.retainAll((java.util.Collection<java.lang.Byte>) byteList54);
        boolean boolean71 = booleanList24.containsAll((java.util.Collection<java.lang.Byte>) byteList54);
        boolean boolean72 = floatList17.removeAll((java.util.Collection<java.lang.Byte>) byteList54);
        org.ccsds.moims.mo.mal.structures.Element element73 = floatList17.createElement();
        byte[] byteArray75 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        int int76 = floatList17.lastIndexOf((java.lang.Object) byteArray75);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion77 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray75);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265078L + "'", long2.equals(281475010265078L));
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(element4);
        org.junit.Assert.assertNotNull(uShortSpliterator5);
        org.junit.Assert.assertNotNull(uShort6);
        org.junit.Assert.assertNotNull(uShort7);
        org.junit.Assert.assertNotNull(uShort8);
        org.junit.Assert.assertNotNull(uShortArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + int12 + "' != '" + 1 + "'", int12 == 1);
        org.junit.Assert.assertNotNull(uShortSpliterator13);
        org.junit.Assert.assertNotNull(uShortStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-4) + "'", int18.equals((-4)));
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + (-4) + "'", int19.equals((-4)));
        org.junit.Assert.assertNotNull(uShort20);
        org.junit.Assert.assertNotNull(floatStream21);
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-1) + "'", int22 == (-1));
        org.junit.Assert.assertTrue("'" + int25 + "' != '" + 0 + "'", int25 == 0);
        org.junit.Assert.assertNotNull(obj27);
        org.junit.Assert.assertNotNull(strArray32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertNotNull(obj40);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
        org.junit.Assert.assertNotNull(doubleArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(byteArray53);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
        org.junit.Assert.assertTrue("'" + str56 + "' != '" + "[100, 1, -1, 10]" + "'", str56.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertNotNull(byteArray62);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
        org.junit.Assert.assertTrue("'" + str65 + "' != '" + "[100, 1, -1, 10]" + "'", str65.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + byte68 + "' != '" + (byte) 100 + "'", byte68.equals((byte) 100));
        org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + true + "'", boolean69);
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
        org.junit.Assert.assertNotNull(element73);
        org.junit.Assert.assertNotNull(byteArray75);
        org.junit.Assert.assertTrue("'" + int76 + "' != '" + (-1) + "'", int76 == (-1));
    }

    @Test
    public void test0690() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0690");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 65536 + "'", int3 == 65536);
    }

    @Test
    public void test0691() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0691");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487885L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((int) (short) 0, 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0692() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0692");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0693() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0693");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0694() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0694");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265077L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0696() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0696");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0697() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0697");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 24);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0699() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0699");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0700() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0700");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0701() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0701");
        }
        byte[] byteArray6 = new byte[]{(byte) 1, (byte) 100, (byte) -1, (byte) -1, (byte) 100, (byte) 1};
        int int8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray6, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D9 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + 1694498660 + "'", int8 == 1694498660);
    }

    @Test
    public void test0702() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0702");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4625759767262920704L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0703() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0703");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-2));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0704() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0704");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487880L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0705() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0705");
        }
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = octetList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = octetList0.getAreaVersion();
        java.lang.Integer int3 = uOctet2.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = uOctet2.getServiceNumber();
        byte[] byteArray6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        boolean boolean7 = uOctet2.equals((java.lang.Object) byteArray6);
        short short9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray6,
            (int) (short) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D10 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 8 + "'", int3.equals(8));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertTrue("'" + short9 + "' != '" + (short) 256 + "'", short9 == (short) 256);
    }

    @Test
    public void test0706() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0706");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0708() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0708");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(70);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0709() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0709");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0710() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0710");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0711() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0711");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0712() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0712");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0713() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0713");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0714() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0714");
        }
        byte[] byteArray6 = new byte[]{(byte) 10, (byte) 0, (byte) 0, (byte) 1, (byte) -1, (byte) 10};
        float float8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getFloatFromByteArray(byteArray6,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion9 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + float8 + "' != '" + 6.1629766E-33f + "'", float8 == 6.1629766E-33f);
    }

    @Test
    public void test0715() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0715");
        }
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = shortList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = shortList0.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.Element element3 = shortList0.createElement();
        java.util.Spliterator<java.lang.Short> shortSpliterator4 = shortList0.spliterator();
        java.util.stream.Stream<java.lang.Short> shortStream5 = shortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element6 = shortList0.createElement();
        java.lang.Integer int7 = shortList0.getTypeShortForm();
        byte[] byteArray9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 58);
        int int10 = shortList0.lastIndexOf((java.lang.Object) byteArray9);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion11 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray9);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(shortSpliterator4);
        org.junit.Assert.assertNotNull(shortStream5);
        org.junit.Assert.assertNotNull(element6);
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-9) + "'", int7.equals((-9)));
        org.junit.Assert.assertNotNull(byteArray9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-1) + "'", int10 == (-1));
    }

    @Test
    public void test0716() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0716");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4294967295L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0717() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0717");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 62);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0718() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0718");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList3 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int4 = floatList3.getTypeShortForm();
        java.lang.Integer int5 = floatList3.getTypeShortForm();
        java.lang.Integer int6 = floatList3.getTypeShortForm();
        boolean boolean8 = floatList3.remove((java.lang.Object) 36);
        byte[] byteArray10 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int11 = floatList3.lastIndexOf((java.lang.Object) byteArray10);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D13 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray10);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-4) + "'", int4.equals((-4)));
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-4) + "'", int5.equals((-4)));
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertNotNull(byteArray10);
        org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-1) + "'", int11 == (-1));
    }

    @Test
    public void test0719() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0719");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0720() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0720");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0722() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0722");
        }
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = shortList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = shortList0.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.Element element3 = shortList0.createElement();
        java.util.Spliterator<java.lang.Short> shortSpliterator4 = shortList0.spliterator();
        java.util.stream.Stream<java.lang.Short> shortStream5 = shortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element6 = shortList0.createElement();
        java.lang.Integer int7 = shortList0.getTypeShortForm();
        byte[] byteArray9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 58);
        int int10 = shortList0.lastIndexOf((java.lang.Object) byteArray9);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion11 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray9);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(shortSpliterator4);
        org.junit.Assert.assertNotNull(shortStream5);
        org.junit.Assert.assertNotNull(element6);
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-9) + "'", int7.equals((-9)));
        org.junit.Assert.assertNotNull(byteArray9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-1) + "'", int10 == (-1));
    }

    @Test
    public void test0723() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0723");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("100");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0724() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0724");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0725() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0725");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0726() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0726");
        }
        byte[] byteArray4 = new byte[]{(byte) 100, (byte) 100, (byte) 0, (byte) 10};
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray4);
    }

    @Test
    public void test0727() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0727");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0728() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0728");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        float float6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getFloatFromByteArray(byteArray3,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D7 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + float6 + "' != '" + 2.8147501E14f + "'", float6 == 2.8147501E14f);
    }

    @Test
    public void test0729() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0729");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(44);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0730() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0730");
        }
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = octetList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = octetList0.getAreaVersion();
        java.lang.Integer int3 = uOctet2.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = uOctet2.getServiceNumber();
        byte[] byteArray6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        boolean boolean7 = uOctet2.equals((java.lang.Object) byteArray6);
        short short9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray6,
            (int) (short) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D10 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 8 + "'", int3.equals(8));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertTrue("'" + short9 + "' != '" + (short) 256 + "'", short9 == (short) 256);
    }

    @Test
    public void test0731() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0731");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(60);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0732() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0732");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList5 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int6 = floatList5.getTypeShortForm();
        java.lang.Integer int7 = floatList5.getTypeShortForm();
        java.lang.Integer int8 = floatList5.getTypeShortForm();
        boolean boolean10 = floatList5.remove((java.lang.Object) 36);
        byte[] byteArray12 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int13 = floatList5.lastIndexOf((java.lang.Object) byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 40, 0, byteArray12);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D16 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray12);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-4) + "'", int7.equals((-4)));
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-4) + "'", int8.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(byteArray12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
    }

    @Test
    public void test0733() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0733");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-11));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0734() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0734");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 19);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0735() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0735");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        int int6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray3, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed7 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 16672 + "'", int6 == 16672);
    }

    @Test
    public void test0736() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0736");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) '4');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0737() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0737");
        }
        org.ccsds.moims.mo.mal.structures.LongList longList1 = new org.ccsds.moims.mo.mal.structures.LongList(64);
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = longList1.getServiceNumber();
        longList1.trimToSize();
        org.ccsds.moims.mo.mal.structures.UShortList uShortList5 = new org.ccsds.moims.mo.mal.structures.UShortList(48);
        java.lang.Long long6 = uShortList5.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element7 = uShortList5.createElement();
        org.ccsds.moims.mo.mal.structures.Element element8 = uShortList5.createElement();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator9 = uShortList5.spliterator();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor10 = uShortList5.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort11 = uShortList5.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray12 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList13 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean14 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList13, uRIArray12);
        java.lang.Double[] doubleArray18 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList19 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean20 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList19,
            doubleArray18);
        java.lang.Byte[] byteArray25 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList26 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList26,
            byteArray25);
        java.lang.String str28 = byteList26.toString();
        boolean boolean29 = doubleList19.retainAll((java.util.Collection<java.lang.Byte>) byteList26);
        boolean boolean30 = uRIList13.containsAll((java.util.Collection<java.lang.Byte>) byteList26);
        uRIList13.ensureCapacity((int) (short) 1);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList33 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj34 = doubleList33.clone();
        java.lang.String[] strArray39 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList40 = new java.util.ArrayList<java.lang.String>();
        boolean boolean41 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList40,
            strArray39);
        strList40.add((int) (short) 0, "[100, 1, -1, 10]");
        int int45 = doubleList33.lastIndexOf((java.lang.Object) strList40);
        java.lang.Byte[] byteArray49 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList50 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean51 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList50,
            byteArray49);
        boolean boolean53 = byteList50.add((java.lang.Byte) (byte) 0);
        java.lang.Byte[] byteArray59 = new java.lang.Byte[]{(byte) 10, (byte) 1, (byte) -1, (byte) 10, (byte) 0};
        java.util.ArrayList<java.lang.Byte> byteList60 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean61 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList60,
            byteArray59);
        boolean boolean62 = byteList50.removeAll((java.util.Collection<java.lang.Byte>) byteList60);
        boolean boolean63 = doubleList33.removeAll((java.util.Collection<java.lang.Byte>) byteList50);
        org.ccsds.moims.mo.mal.structures.OctetList octetList64 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet65 = octetList64.getAreaVersion();
        boolean boolean66 = doubleList33.retainAll((java.util.Collection<java.lang.Byte>) octetList64);
        boolean boolean67 = uRIList13.equals((java.lang.Object) octetList64);
        org.ccsds.moims.mo.mal.structures.Element element68 = octetList64.createElement();
        java.lang.Long long69 = octetList64.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort70 = octetList64.getServiceNumber();
        java.util.stream.Stream<java.lang.Byte> byteStream71 = octetList64.stream();
        boolean boolean72 = uShortList5.retainAll((java.util.Collection<java.lang.Byte>) octetList64);
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor74 = uShortList5.listIterator(
            (int) (byte) 0);
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator75 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator76 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator77 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator78 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator79 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator80 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray81 = new esa.mo.platform.impl.util.PositionsCalculator[]{positionsCalculator75,
                                                                                                                                         positionsCalculator76,
                                                                                                                                         positionsCalculator77,
                                                                                                                                         positionsCalculator78,
                                                                                                                                         positionsCalculator79,
                                                                                                                                         positionsCalculator80};
        esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray82 = uShortList5.toArray(
            positionsCalculatorArray81);
        esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray83 = longList1.toArray(
            positionsCalculatorArray82);
        org.ccsds.moims.mo.mal.structures.UShort uShort84 = longList1.getAreaNumber();
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + long6 + "' != '" + 281475010265078L + "'", long6.equals(281475010265078L));
        org.junit.Assert.assertNotNull(element7);
        org.junit.Assert.assertNotNull(element8);
        org.junit.Assert.assertNotNull(uShortSpliterator9);
        org.junit.Assert.assertNotNull(uShortItor10);
        org.junit.Assert.assertNotNull(uShort11);
        org.junit.Assert.assertNotNull(uRIArray12);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertNotNull(doubleArray18);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
        org.junit.Assert.assertNotNull(byteArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertTrue("'" + str28 + "' != '" + "[100, 1, -1, 10]" + "'", str28.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertNotNull(obj34);
        org.junit.Assert.assertNotNull(strArray39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertTrue("'" + int45 + "' != '" + (-1) + "'", int45 == (-1));
        org.junit.Assert.assertNotNull(byteArray49);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
        org.junit.Assert.assertNotNull(byteArray59);
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + true + "'", boolean61);
        org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + true + "'", boolean62);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + false + "'", !boolean63);
        org.junit.Assert.assertNotNull(uOctet65);
        org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + false + "'", !boolean66);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
        org.junit.Assert.assertNotNull(element68);
        org.junit.Assert.assertTrue("'" + long69 + "' != '" + 281475010265081L + "'", long69.equals(281475010265081L));
        org.junit.Assert.assertNotNull(uShort70);
        org.junit.Assert.assertNotNull(byteStream71);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
        org.junit.Assert.assertNotNull(uShortItor74);
        org.junit.Assert.assertNotNull(positionsCalculatorArray81);
        org.junit.Assert.assertNotNull(positionsCalculatorArray82);
        org.junit.Assert.assertNotNull(positionsCalculatorArray83);
        org.junit.Assert.assertNotNull(uShort84);
    }

    @Test
    public void test0739() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0739");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(58);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0740() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0740");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65536);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0741() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0741");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0742() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0742");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0743() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0743");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        int int9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray5, 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed10 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    }

    @Test
    public void test0744() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0744");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487885L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((int) (short) 0, 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0745() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0745");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0746() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0746");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0747() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0747");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0748() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0748");
        }
        org.ccsds.moims.mo.mal.structures.LongList longList1 = new org.ccsds.moims.mo.mal.structures.LongList(64);
        java.lang.Integer int2 = longList1.getTypeShortForm();
        java.lang.Long long3 = longList1.getShortForm();
        java.lang.Byte[] byteArray8 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList9 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList9, byteArray8);
        java.lang.String str11 = byteList9.toString();
        byteList9.add(2, (java.lang.Byte) (byte) 10);
        boolean boolean15 = longList1.removeAll((java.util.Collection<java.lang.Byte>) byteList9);
        esa.mo.platform.impl.util.HelperIADCS100 helperIADCS100_16 = new esa.mo.platform.impl.util.HelperIADCS100();
        esa.mo.platform.impl.util.HelperIADCS100[] helperIADCS100Array17 = new esa.mo.platform.impl.util.HelperIADCS100[]{helperIADCS100_16};
        esa.mo.platform.impl.util.HelperIADCS100[] helperIADCS100Array18 = longList1.toArray(helperIADCS100Array17);
        org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-13) + "'", int2.equals((-13)));
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 281475010265075L + "'", long3.equals(281475010265075L));
        org.junit.Assert.assertNotNull(byteArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + str11 + "' != '" + "[100, 1, -1, 10]" + "'", str11.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertNotNull(helperIADCS100Array17);
        org.junit.Assert.assertNotNull(helperIADCS100Array18);
    }

    @Test
    public void test0749() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0749");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 19456 + "'", short3 == (short) 19456);
    }

    @Test
    public void test0750() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0750");
        }
        java.lang.Long[] longArray26 = new java.lang.Long[]{281474993487884L, (-1L), 281475010265083L, 281475010265070L,
                                                            281474993487883L, 281474993487885L, 281474993487872L,
                                                            281475010265070L, 281474993487885L, 281474993487889L, (-1L),
                                                            281475010265086L, 281475010265078L, 4294967295L,
                                                            281474993487874L, 281474993487885L, 281474993487886L, 1L,
                                                            281475010265086L, 281475010265075L, 281474993487876L,
                                                            281474993487878L, 281474993487889L, (-1L), 100L,
                                                            281474993487875L};
        java.util.ArrayList<java.lang.Long> longList27 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean28 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList27,
            longArray26);
        java.lang.Long long30 = longList27.remove(0);
        byte[] byteArray32 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        boolean boolean33 = longList27.equals((java.lang.Object) byteArray32);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D34 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray32);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(longArray26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertTrue("'" + long30 + "' != '" + 281474993487884L + "'", long30.equals(281474993487884L));
        org.junit.Assert.assertNotNull(byteArray32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
    }

    @Test
    public void test0751() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0751");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0752() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0752");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 64);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0753() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0753");
        }
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Integer int1 = doubleList0.getTypeShortForm();
        java.lang.Long long2 = doubleList0.getShortForm();
        int int3 = doubleList0.size();
        org.ccsds.moims.mo.mal.structures.OctetList octetList4 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet5 = octetList4.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet6 = octetList4.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray7 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList8 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean9 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList8, uRIArray7);
        java.lang.Double[] doubleArray13 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList14 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList14,
            doubleArray13);
        java.lang.Byte[] byteArray20 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList21 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList21,
            byteArray20);
        java.lang.String str23 = byteList21.toString();
        boolean boolean24 = doubleList14.retainAll((java.util.Collection<java.lang.Byte>) byteList21);
        boolean boolean25 = uRIList8.containsAll((java.util.Collection<java.lang.Byte>) byteList21);
        uRIList8.ensureCapacity((int) (short) 1);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList28 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj29 = doubleList28.clone();
        java.lang.String[] strArray34 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList35 = new java.util.ArrayList<java.lang.String>();
        boolean boolean36 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList35,
            strArray34);
        strList35.add((int) (short) 0, "[100, 1, -1, 10]");
        int int40 = doubleList28.lastIndexOf((java.lang.Object) strList35);
        java.lang.Byte[] byteArray44 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList45 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean46 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList45,
            byteArray44);
        boolean boolean48 = byteList45.add((java.lang.Byte) (byte) 0);
        java.lang.Byte[] byteArray54 = new java.lang.Byte[]{(byte) 10, (byte) 1, (byte) -1, (byte) 10, (byte) 0};
        java.util.ArrayList<java.lang.Byte> byteList55 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean56 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList55,
            byteArray54);
        boolean boolean57 = byteList45.removeAll((java.util.Collection<java.lang.Byte>) byteList55);
        boolean boolean58 = doubleList28.removeAll((java.util.Collection<java.lang.Byte>) byteList45);
        org.ccsds.moims.mo.mal.structures.OctetList octetList59 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet60 = octetList59.getAreaVersion();
        boolean boolean61 = doubleList28.retainAll((java.util.Collection<java.lang.Byte>) octetList59);
        boolean boolean62 = uRIList8.equals((java.lang.Object) octetList59);
        boolean boolean63 = octetList4.containsAll((java.util.Collection<java.lang.Byte>) octetList59);
        boolean boolean64 = doubleList0.containsAll((java.util.Collection<java.lang.Byte>) octetList4);
        java.lang.Integer[] intArray77 = new java.lang.Integer[]{6, 6, 28, 2, 4, 0, 62, 64, 19, 20, 11, 62};
        java.util.ArrayList<java.lang.Integer> intList78 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean79 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList78,
            intArray77);
        java.lang.Byte[] byteArray83 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList84 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean85 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList84,
            byteArray83);
        boolean boolean87 = byteList84.add((java.lang.Byte) (byte) 0);
        java.util.stream.Stream<java.lang.Byte> byteStream88 = byteList84.parallelStream();
        int int90 = byteList84.indexOf((java.lang.Object) 5);
        boolean boolean91 = intList78.contains((java.lang.Object) byteList84);
        boolean boolean92 = doubleList0.containsAll((java.util.Collection<java.lang.Byte>) byteList84);
        byte[] byteArray94 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        int int95 = doubleList0.lastIndexOf((java.lang.Object) byteArray94);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D96 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray94);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-5) + "'", int1.equals((-5)));
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265083L + "'", long2.equals(281475010265083L));
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 0 + "'", int3 == 0);
        org.junit.Assert.assertNotNull(uOctet5);
        org.junit.Assert.assertNotNull(uOctet6);
        org.junit.Assert.assertNotNull(uRIArray7);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
        org.junit.Assert.assertNotNull(doubleArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertNotNull(byteArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + str23 + "' != '" + "[100, 1, -1, 10]" + "'", str23.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
        org.junit.Assert.assertNotNull(obj29);
        org.junit.Assert.assertNotNull(strArray34);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
        org.junit.Assert.assertTrue("'" + int40 + "' != '" + (-1) + "'", int40 == (-1));
        org.junit.Assert.assertNotNull(byteArray44);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(byteArray54);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
        org.junit.Assert.assertNotNull(uOctet60);
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + false + "'", !boolean61);
        org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + true + "'", boolean62);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
        org.junit.Assert.assertNotNull(intArray77);
        org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + true + "'", boolean79);
        org.junit.Assert.assertNotNull(byteArray83);
        org.junit.Assert.assertTrue("'" + boolean85 + "' != '" + true + "'", boolean85);
        org.junit.Assert.assertTrue("'" + boolean87 + "' != '" + true + "'", boolean87);
        org.junit.Assert.assertNotNull(byteStream88);
        org.junit.Assert.assertTrue("'" + int90 + "' != '" + (-1) + "'", int90 == (-1));
        org.junit.Assert.assertTrue("'" + boolean91 + "' != '" + false + "'", !boolean91);
        org.junit.Assert.assertTrue("'" + boolean92 + "' != '" + false + "'", !boolean92);
        org.junit.Assert.assertNotNull(byteArray94);
        org.junit.Assert.assertTrue("'" + int95 + "' != '" + (-1) + "'", int95 == (-1));
    }

    @Test
    public void test0754() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0754");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) 'a');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0755() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0755");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0756() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0756");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) (short) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0757() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0757");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList3 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int4 = floatList3.getTypeShortForm();
        java.lang.Integer int5 = floatList3.getTypeShortForm();
        java.lang.Integer int6 = floatList3.getTypeShortForm();
        boolean boolean8 = floatList3.remove((java.lang.Object) 36);
        byte[] byteArray10 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int11 = floatList3.lastIndexOf((java.lang.Object) byteArray10);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed13 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray10);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-4) + "'", int4.equals((-4)));
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-4) + "'", int5.equals((-4)));
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertNotNull(byteArray10);
        org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-1) + "'", int11 == (-1));
    }

    @Test
    public void test0758() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0758");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 24);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0759() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0759");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 64);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0760() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0760");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0761() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0761");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Integer int1 = floatList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element2 = floatList0.createElement();
        org.ccsds.moims.mo.mal.structures.Element element3 = floatList0.createElement();
        java.util.ListIterator<java.lang.Float> floatItor4 = floatList0.listIterator();
        java.lang.Short[] shortArray10 = new java.lang.Short[]{(short) 100, (short) 1, (short) 1, (short) -1,
                                                               (short) 10};
        java.util.ArrayList<java.lang.Short> shortList11 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList11,
            shortArray10);
        java.util.ListIterator<java.lang.Short> shortItor13 = shortList11.listIterator();
        java.lang.Byte[] byteArray17 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList18 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean19 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList18,
            byteArray17);
        boolean boolean21 = byteList18.add((java.lang.Byte) (byte) 0);
        boolean boolean22 = shortList11.containsAll((java.util.Collection<java.lang.Byte>) byteList18);
        java.lang.Byte[] byteArray27 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList28 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList28,
            byteArray27);
        java.lang.String str30 = byteList28.toString();
        boolean boolean31 = shortList11.retainAll((java.util.Collection<java.lang.Byte>) byteList28);
        org.ccsds.moims.mo.mal.structures.URI uRI33 = new org.ccsds.moims.mo.mal.structures.URI("hi!");
        java.lang.Long long34 = uRI33.getShortForm();
        int int35 = byteList28.lastIndexOf((java.lang.Object) long34);
        boolean boolean36 = floatList0.remove((java.lang.Object) int35);
        org.ccsds.moims.mo.mal.structures.URI uRI38 = new org.ccsds.moims.mo.mal.structures.URI("hi!");
        org.ccsds.moims.mo.mal.structures.UShort uShort39 = uRI38.getAreaNumber();
        java.lang.Long long40 = uRI38.getShortForm();
        org.ccsds.moims.mo.mal.structures.URI uRI42 = new org.ccsds.moims.mo.mal.structures.URI("hi!");
        boolean boolean44 = uRI42.equals((java.lang.Object) 281474993487881L);
        org.ccsds.moims.mo.mal.structures.URI uRI46 = new org.ccsds.moims.mo.mal.structures.URI("hi!");
        boolean boolean48 = uRI46.equals((java.lang.Object) 281474993487881L);
        java.lang.Integer int49 = uRI46.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.URI uRI51 = new org.ccsds.moims.mo.mal.structures.URI("hi!");
        org.ccsds.moims.mo.mal.structures.UShort uShort52 = uRI51.getAreaNumber();
        java.lang.Integer int53 = uRI51.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.URI uRI55 = new org.ccsds.moims.mo.mal.structures.URI("hi!");
        org.ccsds.moims.mo.mal.structures.UShort uShort56 = uRI55.getAreaNumber();
        java.lang.Long long57 = uRI55.getShortForm();
        java.lang.String str58 = uRI55.getValue();
        org.ccsds.moims.mo.mal.structures.URI uRI60 = new org.ccsds.moims.mo.mal.structures.URI("hi!");
        org.ccsds.moims.mo.mal.structures.UShort uShort61 = uRI60.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray62 = new org.ccsds.moims.mo.mal.structures.URI[]{uRI38, uRI42,
                                                                                                         uRI46, uRI51,
                                                                                                         uRI55, uRI60};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList63 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean64 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList63, uRIArray62);
        java.lang.Byte[] byteArray69 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList70 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean71 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList70,
            byteArray69);
        java.lang.String str72 = byteList70.toString();
        java.lang.Byte[] byteArray76 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList77 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean78 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList77,
            byteArray76);
        boolean boolean80 = byteList77.add((java.lang.Byte) (byte) 0);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet81 = org.ccsds.moims.mo.mal.structures.UShortList.AREA_VERSION;
        org.ccsds.moims.mo.mal.structures.UShort uShort82 = uOctet81.getAreaNumber();
        int int83 = byteList77.lastIndexOf((java.lang.Object) uShort82);
        boolean boolean84 = byteList70.equals((java.lang.Object) uShort82);
        boolean boolean85 = uRIList63.removeAll((java.util.Collection<java.lang.Byte>) byteList70);
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray86 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList87 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean88 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList87, uRIArray86);
        uRIList87.trimToSize();
        int int90 = byteList70.indexOf((java.lang.Object) uRIList87);
        int int91 = floatList0.lastIndexOf((java.lang.Object) uRIList87);
        byte[] byteArray93 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        boolean boolean94 = floatList0.contains((java.lang.Object) byteArray93);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion95 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray93);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-4) + "'", int1.equals((-4)));
        org.junit.Assert.assertNotNull(element2);
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(floatItor4);
        org.junit.Assert.assertNotNull(shortArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(shortItor13);
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertNotNull(byteArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertTrue("'" + str30 + "' != '" + "[100, 1, -1, 10]" + "'", str30.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertTrue("'" + long34 + "' != '" + 281474993487890L + "'", long34.equals(281474993487890L));
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-1) + "'", int35 == (-1));
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertNotNull(uShort39);
        org.junit.Assert.assertTrue("'" + long40 + "' != '" + 281474993487890L + "'", long40.equals(281474993487890L));
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertTrue("'" + int49 + "' != '" + 18 + "'", int49.equals(18));
        org.junit.Assert.assertNotNull(uShort52);
        org.junit.Assert.assertTrue("'" + int53 + "' != '" + 18 + "'", int53.equals(18));
        org.junit.Assert.assertNotNull(uShort56);
        org.junit.Assert.assertTrue("'" + long57 + "' != '" + 281474993487890L + "'", long57.equals(281474993487890L));
        org.junit.Assert.assertTrue("'" + str58 + "' != '" + "hi!" + "'", str58.equals("hi!"));
        org.junit.Assert.assertNotNull(uShort61);
        org.junit.Assert.assertNotNull(uRIArray62);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
        org.junit.Assert.assertNotNull(byteArray69);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
        org.junit.Assert.assertTrue("'" + str72 + "' != '" + "[100, 1, -1, 10]" + "'", str72.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertNotNull(byteArray76);
        org.junit.Assert.assertTrue("'" + boolean78 + "' != '" + true + "'", boolean78);
        org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + true + "'", boolean80);
        org.junit.Assert.assertNotNull(uOctet81);
        org.junit.Assert.assertNotNull(uShort82);
        org.junit.Assert.assertTrue("'" + int83 + "' != '" + (-1) + "'", int83 == (-1));
        org.junit.Assert.assertTrue("'" + boolean84 + "' != '" + false + "'", !boolean84);
        org.junit.Assert.assertTrue("'" + boolean85 + "' != '" + false + "'", !boolean85);
        org.junit.Assert.assertNotNull(uRIArray86);
        org.junit.Assert.assertTrue("'" + boolean88 + "' != '" + false + "'", !boolean88);
        org.junit.Assert.assertTrue("'" + int90 + "' != '" + (-1) + "'", int90 == (-1));
        org.junit.Assert.assertTrue("'" + int91 + "' != '" + (-1) + "'", int91 == (-1));
        org.junit.Assert.assertNotNull(byteArray93);
        org.junit.Assert.assertTrue("'" + boolean94 + "' != '" + false + "'", !boolean94);
    }

    @Test
    public void test0762() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0762");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0763() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0763");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(44);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0764() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0764");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0765() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0765");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 40);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0767() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0767");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) (short) 100);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray(18, 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0768() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0768");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(100.0f);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0769() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0769");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0770() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0770");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        int int9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray5, 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion10 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    }

    @Test
    public void test0772() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0772");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487880L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0773() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0773");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0774() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0774");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(10L, (int) (short) 0, byteArray5);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0775() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0775");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (short) 15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0776() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0776");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-2));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0777() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0777");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0778() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0778");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("0");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0779() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0779");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(9);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0780() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0780");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0781() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0781");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487885L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0782() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0782");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 15 + "'", short3 == (short) 15);
    }

    @Test
    public void test0783() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0783");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) ' ');
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0785() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0785");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("36.0");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0786() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0786");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0787() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0787");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((-5.0d));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0788() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0788");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0790() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0790");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(9);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0791() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0791");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0792() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0792");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(12);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0793() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0793");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0794() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0794");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0795() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0795");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0796() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0796");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[1, 0]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0797() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0797");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0799() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0799");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0800() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0800");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(0L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0801() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0801");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 24);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0802() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0802");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 1L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0803() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0803");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0804() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0804");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 64);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0805() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0805");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(20);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0806() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0806");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0807() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0807");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[10, 1, -1, 10]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0808() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0808");
        }
        byte[] byteArray2 = new byte[]{(byte) 100, (byte) -1};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D3 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray2);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray2);
    }

    @Test
    public void test0809() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0809");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0810() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0810");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487885L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((int) (short) 0, 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0811() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0811");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487885L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0812() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0812");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0813() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0813");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) -1 + "'", short3 == (short) -1);
    }

    @Test
    public void test0814() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0814");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) (short) 100);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray(18, 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0815() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0815");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        int int6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray3, 0);
        float float8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getFloatFromByteArray(byteArray3, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed9 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 16672 + "'", int6 == 16672);
        org.junit.Assert.assertTrue("'" + float8 + "' != '" + 2.3362E-41f + "'", float8 == 2.3362E-41f);
    }

    @Test
    public void test0816() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0816");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 1);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        double double5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1, 0);
        short short7 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
        org.junit.Assert.assertTrue("'" + double5 + "' != '" + 4.9E-324d + "'", double5 == 4.9E-324d);
        org.junit.Assert.assertTrue("'" + short7 + "' != '" + (short) 0 + "'", short7 == (short) 0);
    }

    @Test
    public void test0817() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0817");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList5 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int6 = floatList5.getTypeShortForm();
        java.lang.Integer int7 = floatList5.getTypeShortForm();
        java.lang.Integer int8 = floatList5.getTypeShortForm();
        boolean boolean10 = floatList5.remove((java.lang.Object) 36);
        byte[] byteArray12 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int13 = floatList5.lastIndexOf((java.lang.Object) byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 40, 0, byteArray12);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed16 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray12);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-4) + "'", int7.equals((-4)));
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-4) + "'", int8.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(byteArray12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
    }

    @Test
    public void test0818() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0818");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81475010265073E14d);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0819() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0819");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(32);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0820() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0820");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0821() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0821");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-15), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0823() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0823");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0824() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0824");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray(70, 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0825() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0825");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265077L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0826() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0826");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0828() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0828");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0829() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0829");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0830() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0830");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0831() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0831");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0832() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0832");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        int int9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray5, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D10 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    }

    @Test
    public void test0833() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0833");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        int int9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray5, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D10 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    }

    @Test
    public void test0834() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0834");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487879L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0836() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0836");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[0]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0837() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0837");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) '4');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0838() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0838");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487881L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0839() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0839");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(44);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0840() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0840");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0841() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0841");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0842() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0842");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) '4');
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0843() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0843");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(13);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0844() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0844");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList3 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int4 = floatList3.getTypeShortForm();
        java.lang.Integer int5 = floatList3.getTypeShortForm();
        java.lang.Integer int6 = floatList3.getTypeShortForm();
        boolean boolean8 = floatList3.remove((java.lang.Object) 36);
        byte[] byteArray10 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int11 = floatList3.lastIndexOf((java.lang.Object) byteArray10);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D13 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray10);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-4) + "'", int4.equals((-4)));
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-4) + "'", int5.equals((-4)));
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertNotNull(byteArray10);
        org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-1) + "'", int11 == (-1));
    }

    @Test
    public void test0845() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0845");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[100, 1, -1, 10]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0846() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0846");
        }
        byte[] byteArray6 = new byte[]{(byte) 1, (byte) 100, (byte) -1, (byte) -1, (byte) 100, (byte) 1};
        int int8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray6, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D9 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + 1694498660 + "'", int8 == 1694498660);
    }

    @Test
    public void test0847() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0847");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4625759767262920704L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0848() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0848");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0849() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0849");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray(0.0f, (int) (short) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0850() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0850");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[-1.0]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0851() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0851");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0852() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0852");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        long long7 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray4,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertTrue("'" + long7 + "' != '" + 4823355201182236832L + "'", long7 == 4823355201182236832L);
    }

    @Test
    public void test0853() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0853");
        }
        byte[] byteArray6 = new byte[]{(byte) 1, (byte) 100, (byte) -1, (byte) -1, (byte) 100, (byte) 1};
        int int8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray6, 1);
        short short10 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray6, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D11 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + 1694498660 + "'", int8 == 1694498660);
        org.junit.Assert.assertTrue("'" + short10 + "' != '" + (short) 356 + "'", short10 == (short) 356);
    }

    @Test
    public void test0856() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0856");
        }
        java.lang.Short[] shortArray5 = new java.lang.Short[]{(short) 100, (short) 1, (short) 1, (short) -1,
                                                              (short) 10};
        java.util.ArrayList<java.lang.Short> shortList6 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean7 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList6,
            shortArray5);
        java.util.ListIterator<java.lang.Short> shortItor8 = shortList6.listIterator();
        java.lang.Byte[] byteArray12 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList13 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean14 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList13,
            byteArray12);
        boolean boolean16 = byteList13.add((java.lang.Byte) (byte) 0);
        boolean boolean17 = shortList6.containsAll((java.util.Collection<java.lang.Byte>) byteList13);
        java.lang.Byte[] byteArray22 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList23 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean24 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList23,
            byteArray22);
        java.lang.String str25 = byteList23.toString();
        boolean boolean26 = shortList6.retainAll((java.util.Collection<java.lang.Byte>) byteList23);
        java.util.Spliterator<java.lang.Short> shortSpliterator27 = shortList6.spliterator();
        org.ccsds.moims.mo.mal.structures.OctetList octetList28 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet29 = octetList28.getAreaVersion();
        java.util.Spliterator<java.lang.Byte> byteSpliterator30 = octetList28.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort31 = octetList28.getAreaNumber();
        boolean boolean32 = shortList6.retainAll((java.util.Collection<java.lang.Byte>) octetList28);
        byte[] byteArray34 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        int int35 = octetList28.indexOf((java.lang.Object) byteArray34);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D36 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray34);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(shortArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
        org.junit.Assert.assertNotNull(shortItor8);
        org.junit.Assert.assertNotNull(byteArray12);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
        org.junit.Assert.assertNotNull(byteArray22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertTrue("'" + str25 + "' != '" + "[100, 1, -1, 10]" + "'", str25.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertNotNull(shortSpliterator27);
        org.junit.Assert.assertNotNull(uOctet29);
        org.junit.Assert.assertNotNull(byteSpliterator30);
        org.junit.Assert.assertNotNull(uShort31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + false + "'", !boolean32);
        org.junit.Assert.assertNotNull(byteArray34);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-1) + "'", int35 == (-1));
    }

    @Test
    public void test0857() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0857");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0858() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0858");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) ' ');
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0859() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0859");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0860() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0860");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (short) 255);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0861() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0861");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0862() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0862");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0863() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0863");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("1.0");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0864() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0864");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 255);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0865() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0865");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 1L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0866() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0866");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList5 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int6 = floatList5.getTypeShortForm();
        java.lang.Integer int7 = floatList5.getTypeShortForm();
        java.lang.Integer int8 = floatList5.getTypeShortForm();
        boolean boolean10 = floatList5.remove((java.lang.Object) 36);
        byte[] byteArray12 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int13 = floatList5.lastIndexOf((java.lang.Object) byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 40, 0, byteArray12);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed16 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray12);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-4) + "'", int7.equals((-4)));
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-4) + "'", int8.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(byteArray12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
    }

    @Test
    public void test0867() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0867");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0868() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0868");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(12);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0870() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0870");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0871() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0871");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0872() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0872");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[0, 0]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0873() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0873");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 58);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0874() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0874");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 100.0f);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0875() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0875");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList5 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int6 = floatList5.getTypeShortForm();
        java.lang.Integer int7 = floatList5.getTypeShortForm();
        java.lang.Integer int8 = floatList5.getTypeShortForm();
        boolean boolean10 = floatList5.remove((java.lang.Object) 36);
        byte[] byteArray12 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int13 = floatList5.lastIndexOf((java.lang.Object) byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 40, 0, byteArray12);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D16 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray12);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-4) + "'", int7.equals((-4)));
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-4) + "'", int8.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(byteArray12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
    }

    @Test
    public void test0876() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0876");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) 'a');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0877() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0877");
        }
        java.lang.Long[] longArray26 = new java.lang.Long[]{281474993487884L, (-1L), 281475010265083L, 281475010265070L,
                                                            281474993487883L, 281474993487885L, 281474993487872L,
                                                            281475010265070L, 281474993487885L, 281474993487889L, (-1L),
                                                            281475010265086L, 281475010265078L, 4294967295L,
                                                            281474993487874L, 281474993487885L, 281474993487886L, 1L,
                                                            281475010265086L, 281475010265075L, 281474993487876L,
                                                            281474993487878L, 281474993487889L, (-1L), 100L,
                                                            281474993487875L};
        java.util.ArrayList<java.lang.Long> longList27 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean28 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList27,
            longArray26);
        java.lang.Long long30 = longList27.remove(0);
        byte[] byteArray32 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        boolean boolean33 = longList27.equals((java.lang.Object) byteArray32);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D34 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray32);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(longArray26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertTrue("'" + long30 + "' != '" + 281474993487884L + "'", long30.equals(281474993487884L));
        org.junit.Assert.assertNotNull(byteArray32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
    }

    @Test
    public void test0878() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0878");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) (-15));
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) (byte) -1, (int) (byte) 1,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0879() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0879");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487885L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0880() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0880");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0881() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0881");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray(9, 4, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0882() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0882");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0883() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0883");
        }
        java.lang.Short[] shortArray2 = new java.lang.Short[]{(short) 0, (short) -1};
        java.util.ArrayList<java.lang.Short> shortList3 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList3,
            shortArray2);
        shortList3.clear();
        java.lang.Object obj6 = null;
        boolean boolean7 = shortList3.remove(obj6);
        java.util.stream.Stream<java.lang.Short> shortStream8 = shortList3.stream();
        org.ccsds.moims.mo.mal.structures.FloatList floatList9 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Integer int10 = floatList9.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element11 = floatList9.createElement();
        org.ccsds.moims.mo.mal.structures.Element element12 = floatList9.createElement();
        java.lang.String str13 = floatList9.toString();
        boolean boolean15 = floatList9.add((java.lang.Float) 0.0f);
        java.lang.String str16 = floatList9.toString();
        boolean boolean18 = floatList9.add((java.lang.Float) (-1.0f));
        java.lang.Boolean[] booleanArray20 = new java.lang.Boolean[]{false};
        java.util.ArrayList<java.lang.Boolean> booleanList21 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList21,
            booleanArray20);
        java.util.stream.Stream<java.lang.Boolean> booleanStream23 = booleanList21.parallelStream();
        java.util.stream.Stream<java.lang.Boolean> booleanStream24 = booleanList21.stream();
        java.lang.Byte[] byteArray29 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList30 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean31 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList30,
            byteArray29);
        java.lang.String str32 = byteList30.toString();
        boolean boolean33 = booleanList21.containsAll((java.util.Collection<java.lang.Byte>) byteList30);
        boolean boolean34 = floatList9.removeAll((java.util.Collection<java.lang.Byte>) byteList30);
        int int35 = shortList3.indexOf((java.lang.Object) floatList9);
        int int36 = shortList3.size();
        boolean boolean38 = shortList3.add((java.lang.Short) (short) 15);
        java.util.Spliterator<java.lang.Short> shortSpliterator39 = shortList3.spliterator();
        byte[] byteArray41 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        boolean boolean42 = shortList3.remove((java.lang.Object) byteArray41);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D43 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray41);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(shortArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(shortStream8);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
        org.junit.Assert.assertNotNull(element11);
        org.junit.Assert.assertNotNull(element12);
        org.junit.Assert.assertTrue("'" + str13 + "' != '" + "[]" + "'", str13.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + str16 + "' != '" + "[0.0]" + "'", str16.equals("[0.0]"));
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertNotNull(booleanArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(booleanStream23);
        org.junit.Assert.assertNotNull(booleanStream24);
        org.junit.Assert.assertNotNull(byteArray29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertTrue("'" + str32 + "' != '" + "[100, 1, -1, 10]" + "'", str32.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-1) + "'", int35 == (-1));
        org.junit.Assert.assertTrue("'" + int36 + "' != '" + 0 + "'", int36 == 0);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertNotNull(shortSpliterator39);
        org.junit.Assert.assertNotNull(byteArray41);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + false + "'", !boolean42);
    }

    @Test
    public void test0884() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0884");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) (-15));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0885() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0885");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487885L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0886() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0886");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-15));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0887() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0887");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265070L);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 1 + "'", short3 == (short) 1);
    }

    @Test
    public void test0888() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0888");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 4823355201182236832L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0889() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0889");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0890() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0890");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0891() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0891");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 1);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0892() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0892");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        long long3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 4625759767262920704L + "'", long3 == 4625759767262920704L);
    }

    @Test
    public void test0893() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0893");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-7));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0894() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0894");
        }
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList0 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        java.lang.String[] strArray4 = new java.lang.String[]{"[100, 1, -1, 10]", "hi!", ""};
        java.util.ArrayList<java.lang.String> strList5 = new java.util.ArrayList<java.lang.String>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList5, strArray4);
        boolean boolean7 = strList5.isEmpty();
        int int8 = booleanList0.indexOf((java.lang.Object) strList5);
        java.lang.Object obj9 = null;
        int int10 = booleanList0.indexOf(obj9);
        org.ccsds.moims.mo.mal.structures.UShort uShort11 = booleanList0.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort12 = booleanList0.getServiceNumber();
        boolean boolean13 = booleanList0.isEmpty();
        java.lang.String str14 = booleanList0.toString();
        java.util.ListIterator<java.lang.Boolean> booleanItor15 = booleanList0.listIterator();
        java.lang.Integer int16 = booleanList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShortList uShortList18 = new org.ccsds.moims.mo.mal.structures.UShortList(
            48);
        java.lang.Long long19 = uShortList18.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element20 = uShortList18.createElement();
        org.ccsds.moims.mo.mal.structures.Element element21 = uShortList18.createElement();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator22 = uShortList18
            .spliterator();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor23 = uShortList18.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort24 = uShortList18.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray25 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList26 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean27 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList26, uRIArray25);
        java.lang.Double[] doubleArray31 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList32 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean33 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList32,
            doubleArray31);
        java.lang.Byte[] byteArray38 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList39 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean40 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList39,
            byteArray38);
        java.lang.String str41 = byteList39.toString();
        boolean boolean42 = doubleList32.retainAll((java.util.Collection<java.lang.Byte>) byteList39);
        boolean boolean43 = uRIList26.containsAll((java.util.Collection<java.lang.Byte>) byteList39);
        uRIList26.ensureCapacity((int) (short) 1);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList46 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj47 = doubleList46.clone();
        java.lang.String[] strArray52 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList53 = new java.util.ArrayList<java.lang.String>();
        boolean boolean54 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList53,
            strArray52);
        strList53.add((int) (short) 0, "[100, 1, -1, 10]");
        int int58 = doubleList46.lastIndexOf((java.lang.Object) strList53);
        java.lang.Byte[] byteArray62 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList63 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean64 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList63,
            byteArray62);
        boolean boolean66 = byteList63.add((java.lang.Byte) (byte) 0);
        java.lang.Byte[] byteArray72 = new java.lang.Byte[]{(byte) 10, (byte) 1, (byte) -1, (byte) 10, (byte) 0};
        java.util.ArrayList<java.lang.Byte> byteList73 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean74 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList73,
            byteArray72);
        boolean boolean75 = byteList63.removeAll((java.util.Collection<java.lang.Byte>) byteList73);
        boolean boolean76 = doubleList46.removeAll((java.util.Collection<java.lang.Byte>) byteList63);
        org.ccsds.moims.mo.mal.structures.OctetList octetList77 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet78 = octetList77.getAreaVersion();
        boolean boolean79 = doubleList46.retainAll((java.util.Collection<java.lang.Byte>) octetList77);
        boolean boolean80 = uRIList26.equals((java.lang.Object) octetList77);
        org.ccsds.moims.mo.mal.structures.Element element81 = octetList77.createElement();
        java.lang.Long long82 = octetList77.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort83 = octetList77.getServiceNumber();
        java.util.stream.Stream<java.lang.Byte> byteStream84 = octetList77.stream();
        boolean boolean85 = uShortList18.retainAll((java.util.Collection<java.lang.Byte>) octetList77);
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor87 = uShortList18.listIterator(
            (int) (byte) 0);
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator88 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator89 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator90 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator91 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator92 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator93 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray94 = new esa.mo.platform.impl.util.PositionsCalculator[]{positionsCalculator88,
                                                                                                                                         positionsCalculator89,
                                                                                                                                         positionsCalculator90,
                                                                                                                                         positionsCalculator91,
                                                                                                                                         positionsCalculator92,
                                                                                                                                         positionsCalculator93};
        esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray95 = uShortList18.toArray(
            positionsCalculatorArray94);
        boolean boolean96 = booleanList0.remove((java.lang.Object) positionsCalculatorArray95);
        boolean boolean98 = booleanList0.add((java.lang.Boolean) false);
        org.junit.Assert.assertNotNull(strArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-1) + "'", int8 == (-1));
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-1) + "'", int10 == (-1));
        org.junit.Assert.assertNotNull(uShort11);
        org.junit.Assert.assertNotNull(uShort12);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertTrue("'" + str14 + "' != '" + "[]" + "'", str14.equals("[]"));
        org.junit.Assert.assertNotNull(booleanItor15);
        org.junit.Assert.assertTrue("'" + int16 + "' != '" + (-2) + "'", int16.equals((-2)));
        org.junit.Assert.assertTrue("'" + long19 + "' != '" + 281475010265078L + "'", long19.equals(281475010265078L));
        org.junit.Assert.assertNotNull(element20);
        org.junit.Assert.assertNotNull(element21);
        org.junit.Assert.assertNotNull(uShortSpliterator22);
        org.junit.Assert.assertNotNull(uShortItor23);
        org.junit.Assert.assertNotNull(uShort24);
        org.junit.Assert.assertNotNull(uRIArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
        org.junit.Assert.assertNotNull(doubleArray31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertNotNull(byteArray38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
        org.junit.Assert.assertTrue("'" + str41 + "' != '" + "[100, 1, -1, 10]" + "'", str41.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + false + "'", !boolean43);
        org.junit.Assert.assertNotNull(obj47);
        org.junit.Assert.assertNotNull(strArray52);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
        org.junit.Assert.assertTrue("'" + int58 + "' != '" + (-1) + "'", int58 == (-1));
        org.junit.Assert.assertNotNull(byteArray62);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
        org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + true + "'", boolean66);
        org.junit.Assert.assertNotNull(byteArray72);
        org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + true + "'", boolean74);
        org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + true + "'", boolean75);
        org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
        org.junit.Assert.assertNotNull(uOctet78);
        org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + false + "'", !boolean79);
        org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + true + "'", boolean80);
        org.junit.Assert.assertNotNull(element81);
        org.junit.Assert.assertTrue("'" + long82 + "' != '" + 281475010265081L + "'", long82.equals(281475010265081L));
        org.junit.Assert.assertNotNull(uShort83);
        org.junit.Assert.assertNotNull(byteStream84);
        org.junit.Assert.assertTrue("'" + boolean85 + "' != '" + false + "'", !boolean85);
        org.junit.Assert.assertNotNull(uShortItor87);
        org.junit.Assert.assertNotNull(positionsCalculatorArray94);
        org.junit.Assert.assertNotNull(positionsCalculatorArray95);
        org.junit.Assert.assertTrue("'" + boolean96 + "' != '" + false + "'", !boolean96);
        org.junit.Assert.assertTrue("'" + boolean98 + "' != '" + true + "'", boolean98);
    }

    @Test
    public void test0895() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0895");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("2");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0896() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0896");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("29.0");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0897() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0897");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4625759767262920704L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0898() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0898");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0899() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0899");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) ' ');
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0900() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0900");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (short) 15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0901() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0901");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487876L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0902() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0902");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0903() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0903");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0904() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0904");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0905() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0905");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0906() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0906");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(20);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0907() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0907");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487879L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0908() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0908");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-13));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0909() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0909");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0910() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0910");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0911() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0911");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("281475010265070");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0912() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0912");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(12);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0913() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0913");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487879L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0915() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0915");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList7 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int8 = floatList7.getTypeShortForm();
        java.lang.Integer int9 = floatList7.getTypeShortForm();
        java.lang.Integer int10 = floatList7.getTypeShortForm();
        boolean boolean12 = floatList7.remove((java.lang.Object) 36);
        byte[] byteArray14 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int15 = floatList7.lastIndexOf((java.lang.Object) byteArray14);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray14);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 40, 0, byteArray14);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray(0.0d, (int) (short) 0, byteArray14);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D19 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray14);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-4) + "'", int8.equals((-4)));
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-4) + "'", int9.equals((-4)));
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
        org.junit.Assert.assertNotNull(byteArray14);
        org.junit.Assert.assertTrue("'" + int15 + "' != '" + (-1) + "'", int15 == (-1));
    }

    @Test
    public void test0916() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0916");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 14L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0917() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0917");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0919() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0919");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray(0.0f, (int) (short) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0920() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0920");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0921() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0921");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 66 + "'", int3 == 66);
    }

    @Test
    public void test0922() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0922");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0924() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0924");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        float float6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getFloatFromByteArray(byteArray3,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion7 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + float6 + "' != '" + 2.8147501E14f + "'", float6 == 2.8147501E14f);
    }

    @Test
    public void test0925() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0925");
        }
        org.ccsds.moims.mo.mal.structures.Union union1 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Short) (short) 10);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = union1.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList3 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj4 = doubleList3.clone();
        java.lang.String[] strArray9 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList10 = new java.util.ArrayList<java.lang.String>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList10, strArray9);
        strList10.add((int) (short) 0, "[100, 1, -1, 10]");
        int int15 = doubleList3.lastIndexOf((java.lang.Object) strList10);
        boolean boolean16 = union1.equals((java.lang.Object) doubleList3);
        org.ccsds.moims.mo.mal.structures.Element element17 = doubleList3.createElement();
        java.lang.Long[] longArray44 = new java.lang.Long[]{281474993487884L, (-1L), 281475010265083L, 281475010265070L,
                                                            281474993487883L, 281474993487885L, 281474993487872L,
                                                            281475010265070L, 281474993487885L, 281474993487889L, (-1L),
                                                            281475010265086L, 281475010265078L, 4294967295L,
                                                            281474993487874L, 281474993487885L, 281474993487886L, 1L,
                                                            281475010265086L, 281475010265075L, 281474993487876L,
                                                            281474993487878L, 281474993487889L, (-1L), 100L,
                                                            281474993487875L};
        java.util.ArrayList<java.lang.Long> longList45 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean46 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList45,
            longArray44);
        java.lang.Long long48 = longList45.remove(0);
        longList45.ensureCapacity(8);
        java.util.Iterator<java.lang.Long> longItor51 = longList45.iterator();
        int int52 = doubleList3.indexOf((java.lang.Object) longList45);
        java.util.Spliterator<java.lang.Double> doubleSpliterator53 = doubleList3.spliterator();
        byte[] byteArray55 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(1);
        boolean boolean56 = doubleList3.equals((java.lang.Object) byteArray55);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed57 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray55);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(obj4);
        org.junit.Assert.assertNotNull(strArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + int15 + "' != '" + (-1) + "'", int15 == (-1));
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(element17);
        org.junit.Assert.assertNotNull(longArray44);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertTrue("'" + long48 + "' != '" + 281474993487884L + "'", long48.equals(281474993487884L));
        org.junit.Assert.assertNotNull(longItor51);
        org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-1) + "'", int52 == (-1));
        org.junit.Assert.assertNotNull(doubleSpliterator53);
        org.junit.Assert.assertNotNull(byteArray55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
    }

    @Test
    public void test0927() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0927");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 58);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0929() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0929");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList3 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int4 = floatList3.getTypeShortForm();
        java.lang.Integer int5 = floatList3.getTypeShortForm();
        java.lang.Integer int6 = floatList3.getTypeShortForm();
        boolean boolean8 = floatList3.remove((java.lang.Object) 36);
        byte[] byteArray10 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int11 = floatList3.lastIndexOf((java.lang.Object) byteArray10);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed13 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray10);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-4) + "'", int4.equals((-4)));
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-4) + "'", int5.equals((-4)));
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertNotNull(byteArray10);
        org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-1) + "'", int11 == (-1));
    }

    @Test
    public void test0930() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0930");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 24);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0932() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0932");
        }
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray0 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList1 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean2 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList1, uRIArray0);
        java.lang.Double[] doubleArray6 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList7 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList7,
            doubleArray6);
        java.lang.Byte[] byteArray13 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList14 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList14,
            byteArray13);
        java.lang.String str16 = byteList14.toString();
        boolean boolean17 = doubleList7.retainAll((java.util.Collection<java.lang.Byte>) byteList14);
        boolean boolean18 = uRIList1.containsAll((java.util.Collection<java.lang.Byte>) byteList14);
        uRIList1.ensureCapacity((int) (short) 1);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList21 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj22 = doubleList21.clone();
        java.lang.String[] strArray27 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList28 = new java.util.ArrayList<java.lang.String>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList28,
            strArray27);
        strList28.add((int) (short) 0, "[100, 1, -1, 10]");
        int int33 = doubleList21.lastIndexOf((java.lang.Object) strList28);
        java.lang.Byte[] byteArray37 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList38 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean39 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList38,
            byteArray37);
        boolean boolean41 = byteList38.add((java.lang.Byte) (byte) 0);
        java.lang.Byte[] byteArray47 = new java.lang.Byte[]{(byte) 10, (byte) 1, (byte) -1, (byte) 10, (byte) 0};
        java.util.ArrayList<java.lang.Byte> byteList48 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean49 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList48,
            byteArray47);
        boolean boolean50 = byteList38.removeAll((java.util.Collection<java.lang.Byte>) byteList48);
        boolean boolean51 = doubleList21.removeAll((java.util.Collection<java.lang.Byte>) byteList38);
        org.ccsds.moims.mo.mal.structures.OctetList octetList52 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet53 = octetList52.getAreaVersion();
        boolean boolean54 = doubleList21.retainAll((java.util.Collection<java.lang.Byte>) octetList52);
        boolean boolean55 = uRIList1.equals((java.lang.Object) octetList52);
        java.lang.Object obj56 = null;
        int int57 = uRIList1.lastIndexOf(obj56);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList58 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj59 = doubleList58.clone();
        java.lang.String[] strArray64 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList65 = new java.util.ArrayList<java.lang.String>();
        boolean boolean66 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList65,
            strArray64);
        strList65.add((int) (short) 0, "[100, 1, -1, 10]");
        int int70 = doubleList58.lastIndexOf((java.lang.Object) strList65);
        java.util.ListIterator<java.lang.String> strItor71 = strList65.listIterator();
        boolean boolean73 = strList65.add("hi!");
        java.lang.Double[] doubleArray77 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList78 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean79 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList78,
            doubleArray77);
        java.lang.Byte[] byteArray84 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList85 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean86 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList85,
            byteArray84);
        java.lang.String str87 = byteList85.toString();
        boolean boolean88 = doubleList78.retainAll((java.util.Collection<java.lang.Byte>) byteList85);
        boolean boolean89 = strList65.removeAll((java.util.Collection<java.lang.Byte>) byteList85);
        boolean boolean90 = uRIList1.containsAll((java.util.Collection<java.lang.Byte>) byteList85);
        java.lang.Object[] objArray91 = uRIList1.toArray();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor92 = uRIList1.listIterator();
        byte[] byteArray94 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 2);
        boolean boolean95 = uRIList1.equals((java.lang.Object) byteArray94);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D96 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray94);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uRIArray0);
        org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + false + "'", !boolean2);
        org.junit.Assert.assertNotNull(doubleArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(byteArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + str16 + "' != '" + "[100, 1, -1, 10]" + "'", str16.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
        org.junit.Assert.assertNotNull(obj22);
        org.junit.Assert.assertNotNull(strArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertTrue("'" + int33 + "' != '" + (-1) + "'", int33 == (-1));
        org.junit.Assert.assertNotNull(byteArray37);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertNotNull(byteArray47);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + true + "'", boolean50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + false + "'", !boolean51);
        org.junit.Assert.assertNotNull(uOctet53);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
        org.junit.Assert.assertTrue("'" + int57 + "' != '" + (-1) + "'", int57 == (-1));
        org.junit.Assert.assertNotNull(obj59);
        org.junit.Assert.assertNotNull(strArray64);
        org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + true + "'", boolean66);
        org.junit.Assert.assertTrue("'" + int70 + "' != '" + (-1) + "'", int70 == (-1));
        org.junit.Assert.assertNotNull(strItor71);
        org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + true + "'", boolean73);
        org.junit.Assert.assertNotNull(doubleArray77);
        org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + true + "'", boolean79);
        org.junit.Assert.assertNotNull(byteArray84);
        org.junit.Assert.assertTrue("'" + boolean86 + "' != '" + true + "'", boolean86);
        org.junit.Assert.assertTrue("'" + str87 + "' != '" + "[100, 1, -1, 10]" + "'", str87.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean88 + "' != '" + true + "'", boolean88);
        org.junit.Assert.assertTrue("'" + boolean89 + "' != '" + false + "'", !boolean89);
        org.junit.Assert.assertTrue("'" + boolean90 + "' != '" + false + "'", !boolean90);
        org.junit.Assert.assertNotNull(objArray91);
        org.junit.Assert.assertNotNull(uRIItor92);
        org.junit.Assert.assertNotNull(byteArray94);
        org.junit.Assert.assertTrue("'" + boolean95 + "' != '" + false + "'", !boolean95);
    }

    @Test
    public void test0933() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0933");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0934() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0934");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65536);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0935() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0935");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position(
                    "[17, 15, 0, 6, 0, -1, 12, 14, 60, 4, 9, 14, 17, 5, -1, -10, -9, 17, 15, 17, 19, 2, 44, 65535, 3, 70, 36, 100, 10]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0936() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0936");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0937() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0937");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(13);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0938() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0938");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-1));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0939() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0939");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        int int6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray3, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D7 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 16672 + "'", int6 == 16672);
    }

    @Test
    public void test0940() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0940");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList5 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int6 = floatList5.getTypeShortForm();
        java.lang.Integer int7 = floatList5.getTypeShortForm();
        java.lang.Integer int8 = floatList5.getTypeShortForm();
        boolean boolean10 = floatList5.remove((java.lang.Object) 36);
        byte[] byteArray12 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int13 = floatList5.lastIndexOf((java.lang.Object) byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray(100L, (int) (short) 0, byteArray12);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 40, 0, byteArray12);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D16 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray12);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-4) + "'", int6.equals((-4)));
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-4) + "'", int7.equals((-4)));
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-4) + "'", int8.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(byteArray12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
    }

    @Test
    public void test0941() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0941");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0942() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0942");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("35.0");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0943() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0943");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) (short) 19456);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0944() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0944");
        }
        java.lang.Short[] shortArray2 = new java.lang.Short[]{(short) 0, (short) -1};
        java.util.ArrayList<java.lang.Short> shortList3 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList3,
            shortArray2);
        shortList3.clear();
        java.lang.Object obj6 = null;
        boolean boolean7 = shortList3.remove(obj6);
        java.util.stream.Stream<java.lang.Short> shortStream8 = shortList3.stream();
        org.ccsds.moims.mo.mal.structures.FloatList floatList9 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Integer int10 = floatList9.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element11 = floatList9.createElement();
        org.ccsds.moims.mo.mal.structures.Element element12 = floatList9.createElement();
        java.lang.String str13 = floatList9.toString();
        boolean boolean15 = floatList9.add((java.lang.Float) 0.0f);
        java.lang.String str16 = floatList9.toString();
        boolean boolean18 = floatList9.add((java.lang.Float) (-1.0f));
        java.lang.Boolean[] booleanArray20 = new java.lang.Boolean[]{false};
        java.util.ArrayList<java.lang.Boolean> booleanList21 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList21,
            booleanArray20);
        java.util.stream.Stream<java.lang.Boolean> booleanStream23 = booleanList21.parallelStream();
        java.util.stream.Stream<java.lang.Boolean> booleanStream24 = booleanList21.stream();
        java.lang.Byte[] byteArray29 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList30 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean31 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList30,
            byteArray29);
        java.lang.String str32 = byteList30.toString();
        boolean boolean33 = booleanList21.containsAll((java.util.Collection<java.lang.Byte>) byteList30);
        boolean boolean34 = floatList9.removeAll((java.util.Collection<java.lang.Byte>) byteList30);
        int int35 = shortList3.indexOf((java.lang.Object) floatList9);
        int int36 = shortList3.size();
        boolean boolean38 = shortList3.add((java.lang.Short) (short) 15);
        java.util.Spliterator<java.lang.Short> shortSpliterator39 = shortList3.spliterator();
        byte[] byteArray41 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        boolean boolean42 = shortList3.remove((java.lang.Object) byteArray41);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed43 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray41);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(shortArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(shortStream8);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
        org.junit.Assert.assertNotNull(element11);
        org.junit.Assert.assertNotNull(element12);
        org.junit.Assert.assertTrue("'" + str13 + "' != '" + "[]" + "'", str13.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + str16 + "' != '" + "[0.0]" + "'", str16.equals("[0.0]"));
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertNotNull(booleanArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(booleanStream23);
        org.junit.Assert.assertNotNull(booleanStream24);
        org.junit.Assert.assertNotNull(byteArray29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertTrue("'" + str32 + "' != '" + "[100, 1, -1, 10]" + "'", str32.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-1) + "'", int35 == (-1));
        org.junit.Assert.assertTrue("'" + int36 + "' != '" + 0 + "'", int36 == 0);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertNotNull(shortSpliterator39);
        org.junit.Assert.assertNotNull(byteArray41);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + false + "'", !boolean42);
    }

    @Test
    public void test0945() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0945");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0946() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0946");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0947() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0947");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0948() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0948");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) (short) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0949() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0949");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487881L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0950() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0950");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0951() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0951");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(6);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0952() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0952");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 1);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        double double5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1, 0);
        short short7 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
        org.junit.Assert.assertTrue("'" + double5 + "' != '" + 4.9E-324d + "'", double5 == 4.9E-324d);
        org.junit.Assert.assertTrue("'" + short7 + "' != '" + (short) 0 + "'", short7 == (short) 0);
    }

    @Test
    public void test0953() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0953");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0954() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0954");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0955() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0955");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) -1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0956() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0956");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0957() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0957");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0958() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0958");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) (-5));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0959() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0959");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0960() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0960");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        long long7 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray4,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertTrue("'" + long7 + "' != '" + 4823355201182236832L + "'", long7 == 4823355201182236832L);
    }

    @Test
    public void test0961() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0961");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487889L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0963() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0963");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 64);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0964() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0964");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 28);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0965() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0965");
        }
        org.ccsds.moims.mo.mal.structures.FloatList floatList1 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int2 = floatList1.getTypeShortForm();
        java.lang.Integer int3 = floatList1.getTypeShortForm();
        java.lang.Integer int4 = floatList1.getTypeShortForm();
        boolean boolean6 = floatList1.remove((java.lang.Object) 36);
        byte[] byteArray8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        int int9 = floatList1.lastIndexOf((java.lang.Object) byteArray8);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion10 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray8);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-4) + "'", int2.equals((-4)));
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-4) + "'", int3.equals((-4)));
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-4) + "'", int4.equals((-4)));
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + false + "'", !boolean6);
        org.junit.Assert.assertNotNull(byteArray8);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-1) + "'", int9 == (-1));
    }

    @Test
    public void test0967() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0967");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-4));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0968() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0968");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0969() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0969");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0970() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0970");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0971() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0971");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("29.0");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0972() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0972");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0973() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0973");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0974() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0974");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4294967295L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0975() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0975");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0976() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0976");
        }
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray0 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList1 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean2 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList1, uRIArray0);
        java.lang.Double[] doubleArray6 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList7 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList7,
            doubleArray6);
        java.lang.Byte[] byteArray13 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList14 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList14,
            byteArray13);
        java.lang.String str16 = byteList14.toString();
        boolean boolean17 = doubleList7.retainAll((java.util.Collection<java.lang.Byte>) byteList14);
        boolean boolean18 = uRIList1.containsAll((java.util.Collection<java.lang.Byte>) byteList14);
        uRIList1.ensureCapacity((int) (short) 1);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList21 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj22 = doubleList21.clone();
        java.lang.String[] strArray27 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList28 = new java.util.ArrayList<java.lang.String>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList28,
            strArray27);
        strList28.add((int) (short) 0, "[100, 1, -1, 10]");
        int int33 = doubleList21.lastIndexOf((java.lang.Object) strList28);
        java.lang.Byte[] byteArray37 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList38 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean39 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList38,
            byteArray37);
        boolean boolean41 = byteList38.add((java.lang.Byte) (byte) 0);
        java.lang.Byte[] byteArray47 = new java.lang.Byte[]{(byte) 10, (byte) 1, (byte) -1, (byte) 10, (byte) 0};
        java.util.ArrayList<java.lang.Byte> byteList48 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean49 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList48,
            byteArray47);
        boolean boolean50 = byteList38.removeAll((java.util.Collection<java.lang.Byte>) byteList48);
        boolean boolean51 = doubleList21.removeAll((java.util.Collection<java.lang.Byte>) byteList38);
        org.ccsds.moims.mo.mal.structures.OctetList octetList52 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet53 = octetList52.getAreaVersion();
        boolean boolean54 = doubleList21.retainAll((java.util.Collection<java.lang.Byte>) octetList52);
        boolean boolean55 = uRIList1.equals((java.lang.Object) octetList52);
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor56 = uRIList1.iterator();
        java.lang.Object obj57 = uRIList1.clone();
        esa.mo.platform.impl.util.HelperGPS helperGPS58 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS59 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS60 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray61 = new esa.mo.platform.impl.util.HelperGPS[]{helperGPS58,
                                                                                                           helperGPS59,
                                                                                                           helperGPS60};
        esa.mo.platform.impl.util.HelperGPS helperGPS62 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS63 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS64 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray65 = new esa.mo.platform.impl.util.HelperGPS[]{helperGPS62,
                                                                                                           helperGPS63,
                                                                                                           helperGPS64};
        esa.mo.platform.impl.util.HelperGPS helperGPS66 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS67 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS68 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray69 = new esa.mo.platform.impl.util.HelperGPS[]{helperGPS66,
                                                                                                           helperGPS67,
                                                                                                           helperGPS68};
        esa.mo.platform.impl.util.HelperGPS helperGPS70 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS71 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS72 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray73 = new esa.mo.platform.impl.util.HelperGPS[]{helperGPS70,
                                                                                                           helperGPS71,
                                                                                                           helperGPS72};
        esa.mo.platform.impl.util.HelperGPS helperGPS74 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS75 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS76 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray77 = new esa.mo.platform.impl.util.HelperGPS[]{helperGPS74,
                                                                                                           helperGPS75,
                                                                                                           helperGPS76};
        esa.mo.platform.impl.util.HelperGPS[][] helperGPSArray78 = new esa.mo.platform.impl.util.HelperGPS[][]{helperGPSArray61,
                                                                                                               helperGPSArray65,
                                                                                                               helperGPSArray69,
                                                                                                               helperGPSArray73,
                                                                                                               helperGPSArray77};
        esa.mo.platform.impl.util.HelperGPS[][][] helperGPSArray79 = new esa.mo.platform.impl.util.HelperGPS[][][]{helperGPSArray78};
        esa.mo.platform.impl.util.HelperGPS[][][] helperGPSArray80 = uRIList1.toArray(helperGPSArray79);
        org.junit.Assert.assertNotNull(uRIArray0);
        org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + false + "'", !boolean2);
        org.junit.Assert.assertNotNull(doubleArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(byteArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + str16 + "' != '" + "[100, 1, -1, 10]" + "'", str16.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
        org.junit.Assert.assertNotNull(obj22);
        org.junit.Assert.assertNotNull(strArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertTrue("'" + int33 + "' != '" + (-1) + "'", int33 == (-1));
        org.junit.Assert.assertNotNull(byteArray37);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertNotNull(byteArray47);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + true + "'", boolean50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + false + "'", !boolean51);
        org.junit.Assert.assertNotNull(uOctet53);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
        org.junit.Assert.assertNotNull(uRIItor56);
        org.junit.Assert.assertNotNull(obj57);
        org.junit.Assert.assertNotNull(helperGPSArray61);
        org.junit.Assert.assertNotNull(helperGPSArray65);
        org.junit.Assert.assertNotNull(helperGPSArray69);
        org.junit.Assert.assertNotNull(helperGPSArray73);
        org.junit.Assert.assertNotNull(helperGPSArray77);
        org.junit.Assert.assertNotNull(helperGPSArray78);
        org.junit.Assert.assertNotNull(helperGPSArray79);
        org.junit.Assert.assertNotNull(helperGPSArray80);
    }

    @Test
    public void test0977() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0977");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(0L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0978() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0978");
        }
        java.lang.Short[] shortArray2 = new java.lang.Short[]{(short) 0, (short) -1};
        java.util.ArrayList<java.lang.Short> shortList3 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList3,
            shortArray2);
        shortList3.clear();
        java.lang.Object obj6 = null;
        boolean boolean7 = shortList3.remove(obj6);
        java.util.stream.Stream<java.lang.Short> shortStream8 = shortList3.stream();
        org.ccsds.moims.mo.mal.structures.FloatList floatList9 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Integer int10 = floatList9.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element11 = floatList9.createElement();
        org.ccsds.moims.mo.mal.structures.Element element12 = floatList9.createElement();
        java.lang.String str13 = floatList9.toString();
        boolean boolean15 = floatList9.add((java.lang.Float) 0.0f);
        java.lang.String str16 = floatList9.toString();
        boolean boolean18 = floatList9.add((java.lang.Float) (-1.0f));
        java.lang.Boolean[] booleanArray20 = new java.lang.Boolean[]{false};
        java.util.ArrayList<java.lang.Boolean> booleanList21 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList21,
            booleanArray20);
        java.util.stream.Stream<java.lang.Boolean> booleanStream23 = booleanList21.parallelStream();
        java.util.stream.Stream<java.lang.Boolean> booleanStream24 = booleanList21.stream();
        java.lang.Byte[] byteArray29 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList30 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean31 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList30,
            byteArray29);
        java.lang.String str32 = byteList30.toString();
        boolean boolean33 = booleanList21.containsAll((java.util.Collection<java.lang.Byte>) byteList30);
        boolean boolean34 = floatList9.removeAll((java.util.Collection<java.lang.Byte>) byteList30);
        int int35 = shortList3.indexOf((java.lang.Object) floatList9);
        int int36 = shortList3.size();
        boolean boolean38 = shortList3.add((java.lang.Short) (short) 15);
        java.util.Spliterator<java.lang.Short> shortSpliterator39 = shortList3.spliterator();
        byte[] byteArray41 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        boolean boolean42 = shortList3.remove((java.lang.Object) byteArray41);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion43 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray41);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(shortArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(shortStream8);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-4) + "'", int10.equals((-4)));
        org.junit.Assert.assertNotNull(element11);
        org.junit.Assert.assertNotNull(element12);
        org.junit.Assert.assertTrue("'" + str13 + "' != '" + "[]" + "'", str13.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + str16 + "' != '" + "[0.0]" + "'", str16.equals("[0.0]"));
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertNotNull(booleanArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(booleanStream23);
        org.junit.Assert.assertNotNull(booleanStream24);
        org.junit.Assert.assertNotNull(byteArray29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertTrue("'" + str32 + "' != '" + "[100, 1, -1, 10]" + "'", str32.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-1) + "'", int35 == (-1));
        org.junit.Assert.assertTrue("'" + int36 + "' != '" + 0 + "'", int36 == 0);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertNotNull(shortSpliterator39);
        org.junit.Assert.assertNotNull(byteArray41);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + false + "'", !boolean42);
    }

    @Test
    public void test0979() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0979");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 66 + "'", int3 == 66);
    }

    @Test
    public void test0980() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0980");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0981() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0981");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0982() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0982");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0983() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0983");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0984() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0984");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0985() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0985");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0986() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0986");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 6);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0987() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0987");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(3.0d);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0988() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0988");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-15));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0989() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0989");
        }
        org.ccsds.moims.mo.mal.structures.UShortList uShortList1 = new org.ccsds.moims.mo.mal.structures.UShortList(48);
        java.lang.Long long2 = uShortList1.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element3 = uShortList1.createElement();
        org.ccsds.moims.mo.mal.structures.Element element4 = uShortList1.createElement();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator5 = uShortList1.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort6 = uShortList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort7 = uShortList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort8 = org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
        org.ccsds.moims.mo.mal.structures.UShort[] uShortArray9 = new org.ccsds.moims.mo.mal.structures.UShort[]{uShort8};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList10 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
        boolean boolean11 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList10, uShortArray9);
        int int12 = uShortList10.size();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator13 = uShortList10
            .spliterator();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream14 = uShortList10
            .parallelStream();
        boolean boolean15 = uShortList1.equals((java.lang.Object) uShortList10);
        org.ccsds.moims.mo.mal.structures.FloatList floatList17 = new org.ccsds.moims.mo.mal.structures.FloatList(100);
        java.lang.Integer int18 = floatList17.getTypeShortForm();
        java.lang.Integer int19 = floatList17.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort20 = floatList17.getAreaNumber();
        java.util.stream.Stream<java.lang.Float> floatStream21 = floatList17.parallelStream();
        int int22 = uShortList10.indexOf((java.lang.Object) floatList17);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList24 = new org.ccsds.moims.mo.mal.structures.BooleanList(
            100);
        int int25 = booleanList24.size();
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList26 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj27 = doubleList26.clone();
        java.lang.String[] strArray32 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList33 = new java.util.ArrayList<java.lang.String>();
        boolean boolean34 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList33,
            strArray32);
        strList33.add((int) (short) 0, "[100, 1, -1, 10]");
        int int38 = doubleList26.lastIndexOf((java.lang.Object) strList33);
        boolean boolean39 = doubleList26.isEmpty();
        java.lang.Object obj40 = doubleList26.clone();
        boolean boolean42 = doubleList26.add((java.lang.Double) (-1.0d));
        java.lang.Double[] doubleArray46 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList47 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList47,
            doubleArray46);
        java.lang.Byte[] byteArray53 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList54 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean55 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList54,
            byteArray53);
        java.lang.String str56 = byteList54.toString();
        boolean boolean57 = doubleList47.retainAll((java.util.Collection<java.lang.Byte>) byteList54);
        java.lang.Byte[] byteArray62 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList63 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean64 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList63,
            byteArray62);
        java.lang.String str65 = byteList63.toString();
        java.lang.Byte byte68 = byteList63.set(0, (java.lang.Byte) (byte) 10);
        boolean boolean69 = byteList54.containsAll((java.util.Collection<java.lang.Byte>) byteList63);
        boolean boolean70 = doubleList26.retainAll((java.util.Collection<java.lang.Byte>) byteList54);
        boolean boolean71 = booleanList24.containsAll((java.util.Collection<java.lang.Byte>) byteList54);
        boolean boolean72 = floatList17.removeAll((java.util.Collection<java.lang.Byte>) byteList54);
        org.ccsds.moims.mo.mal.structures.Element element73 = floatList17.createElement();
        byte[] byteArray75 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        int int76 = floatList17.lastIndexOf((java.lang.Object) byteArray75);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D77 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray75);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265078L + "'", long2.equals(281475010265078L));
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(element4);
        org.junit.Assert.assertNotNull(uShortSpliterator5);
        org.junit.Assert.assertNotNull(uShort6);
        org.junit.Assert.assertNotNull(uShort7);
        org.junit.Assert.assertNotNull(uShort8);
        org.junit.Assert.assertNotNull(uShortArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + int12 + "' != '" + 1 + "'", int12 == 1);
        org.junit.Assert.assertNotNull(uShortSpliterator13);
        org.junit.Assert.assertNotNull(uShortStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-4) + "'", int18.equals((-4)));
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + (-4) + "'", int19.equals((-4)));
        org.junit.Assert.assertNotNull(uShort20);
        org.junit.Assert.assertNotNull(floatStream21);
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-1) + "'", int22 == (-1));
        org.junit.Assert.assertTrue("'" + int25 + "' != '" + 0 + "'", int25 == 0);
        org.junit.Assert.assertNotNull(obj27);
        org.junit.Assert.assertNotNull(strArray32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertNotNull(obj40);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
        org.junit.Assert.assertNotNull(doubleArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(byteArray53);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
        org.junit.Assert.assertTrue("'" + str56 + "' != '" + "[100, 1, -1, 10]" + "'", str56.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertNotNull(byteArray62);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
        org.junit.Assert.assertTrue("'" + str65 + "' != '" + "[100, 1, -1, 10]" + "'", str65.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + byte68 + "' != '" + (byte) 100 + "'", byte68.equals((byte) 100));
        org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + true + "'", boolean69);
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
        org.junit.Assert.assertNotNull(element73);
        org.junit.Assert.assertNotNull(byteArray75);
        org.junit.Assert.assertTrue("'" + int76 + "' != '" + (-1) + "'", int76 == (-1));
    }

    @Test
    public void test0990() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0990");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0991() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0991");
        }
        java.lang.Float[] floatArray1 = new java.lang.Float[]{10.0f};
        java.util.ArrayList<java.lang.Float> floatList2 = new java.util.ArrayList<java.lang.Float>();
        boolean boolean3 = java.util.Collections.addAll((java.util.Collection<java.lang.Float>) floatList2,
            floatArray1);
        java.lang.Object obj4 = null;
        int int5 = floatList2.lastIndexOf(obj4);
        java.util.ListIterator<java.lang.Float> floatItor6 = floatList2.listIterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort7 = org.ccsds.moims.mo.mal.structures.FloatList.SERVICE_SHORT_FORM;
        org.ccsds.moims.mo.mal.structures.UShort[] uShortArray8 = new org.ccsds.moims.mo.mal.structures.UShort[]{uShort7};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList9 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
        boolean boolean10 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList9, uShortArray8);
        int int11 = uShortList9.size();
        org.ccsds.moims.mo.mal.structures.UShort uShort12 = org.ccsds.moims.mo.mal.structures.UShort.ATTRIBUTE_SERVICE_NUMBER;
        boolean boolean13 = uShortList9.add(uShort12);
        boolean boolean14 = floatList2.equals((java.lang.Object) uShort12);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList15 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj16 = doubleList15.clone();
        java.lang.String[] strArray21 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList22 = new java.util.ArrayList<java.lang.String>();
        boolean boolean23 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList22,
            strArray21);
        strList22.add((int) (short) 0, "[100, 1, -1, 10]");
        int int27 = doubleList15.lastIndexOf((java.lang.Object) strList22);
        boolean boolean28 = doubleList15.isEmpty();
        java.lang.Object obj29 = doubleList15.clone();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray30 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList31 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean32 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList31, uRIArray30);
        java.lang.Object obj33 = uRIList31.clone();
        java.lang.Double[] doubleArray37 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList38 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean39 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList38,
            doubleArray37);
        java.lang.Byte[] byteArray44 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList45 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean46 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList45,
            byteArray44);
        java.lang.String str47 = byteList45.toString();
        boolean boolean48 = doubleList38.retainAll((java.util.Collection<java.lang.Byte>) byteList45);
        java.util.ListIterator<java.lang.Byte> byteItor49 = byteList45.listIterator();
        boolean boolean50 = byteList45.isEmpty();
        boolean boolean51 = uRIList31.containsAll((java.util.Collection<java.lang.Byte>) byteList45);
        boolean boolean52 = doubleList15.removeAll((java.util.Collection<java.lang.Byte>) byteList45);
        java.lang.Boolean[] booleanArray55 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList56 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean57 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList56,
            booleanArray55);
        java.util.stream.Stream<java.lang.Boolean> booleanStream58 = booleanList56.stream();
        java.util.Spliterator<java.lang.Boolean> booleanSpliterator59 = booleanList56.spliterator();
        int int60 = byteList45.indexOf((java.lang.Object) booleanSpliterator59);
        boolean boolean61 = floatList2.containsAll((java.util.Collection<java.lang.Byte>) byteList45);
        java.lang.Long[] longArray69 = new java.lang.Long[]{4294967295L, (-1L), 4294967295L, 0L, (-1L), 0L, 100L};
        java.util.ArrayList<java.lang.Long> longList70 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean71 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList70,
            longArray69);
        longList70.add((int) (byte) 1, (java.lang.Long) 281475010265070L);
        java.lang.Byte[] byteArray79 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList80 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean81 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList80,
            byteArray79);
        java.lang.String str82 = byteList80.toString();
        java.lang.Byte byte85 = byteList80.set(0, (java.lang.Byte) (byte) 10);
        boolean boolean86 = longList70.containsAll((java.util.Collection<java.lang.Byte>) byteList80);
        java.lang.Object obj87 = null;
        boolean boolean88 = byteList80.equals(obj87);
        boolean boolean89 = byteList45.removeAll((java.util.Collection<java.lang.Byte>) byteList80);
        byte[] byteArray91 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265077L);
        boolean boolean92 = byteList80.equals((java.lang.Object) byteArray91);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D93 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray91);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(floatArray1);
        org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3);
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + (-1) + "'", int5 == (-1));
        org.junit.Assert.assertNotNull(floatItor6);
        org.junit.Assert.assertNotNull(uShort7);
        org.junit.Assert.assertNotNull(uShortArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + int11 + "' != '" + 1 + "'", int11 == 1);
        org.junit.Assert.assertNotNull(uShort12);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertNotNull(obj16);
        org.junit.Assert.assertNotNull(strArray21);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
        org.junit.Assert.assertTrue("'" + int27 + "' != '" + (-1) + "'", int27 == (-1));
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertNotNull(obj29);
        org.junit.Assert.assertNotNull(uRIArray30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + false + "'", !boolean32);
        org.junit.Assert.assertNotNull(obj33);
        org.junit.Assert.assertNotNull(doubleArray37);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertNotNull(byteArray44);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertTrue("'" + str47 + "' != '" + "[100, 1, -1, 10]" + "'", str47.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(byteItor49);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + false + "'", !boolean50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + false + "'", !boolean51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
        org.junit.Assert.assertNotNull(booleanArray55);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertNotNull(booleanStream58);
        org.junit.Assert.assertNotNull(booleanSpliterator59);
        org.junit.Assert.assertTrue("'" + int60 + "' != '" + (-1) + "'", int60 == (-1));
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + false + "'", !boolean61);
        org.junit.Assert.assertNotNull(longArray69);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
        org.junit.Assert.assertNotNull(byteArray79);
        org.junit.Assert.assertTrue("'" + boolean81 + "' != '" + true + "'", boolean81);
        org.junit.Assert.assertTrue("'" + str82 + "' != '" + "[100, 1, -1, 10]" + "'", str82.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + byte85 + "' != '" + (byte) 100 + "'", byte85.equals((byte) 100));
        org.junit.Assert.assertTrue("'" + boolean86 + "' != '" + false + "'", !boolean86);
        org.junit.Assert.assertTrue("'" + boolean88 + "' != '" + false + "'", !boolean88);
        org.junit.Assert.assertTrue("'" + boolean89 + "' != '" + true + "'", boolean89);
        org.junit.Assert.assertNotNull(byteArray91);
        org.junit.Assert.assertTrue("'" + boolean92 + "' != '" + false + "'", !boolean92);
    }

    @Test
    public void test0992() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0992");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 14L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0993() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0993");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) -1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0994() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0994");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 1);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        double double5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
        org.junit.Assert.assertTrue("'" + double5 + "' != '" + 4.9E-324d + "'", double5 == 4.9E-324d);
    }

    @Test
    public void test0996() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0996");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) ' ');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0997() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0997");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-1));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0998() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0998");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0999() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test0999");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 2 + "'", short3 == (short) 2);
    }

    @Test
    public void test1000() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest1.test1000");
        }
        org.ccsds.moims.mo.mal.structures.Union union1 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Short) (short) 10);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = union1.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList3 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj4 = doubleList3.clone();
        java.lang.String[] strArray9 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList10 = new java.util.ArrayList<java.lang.String>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList10, strArray9);
        strList10.add((int) (short) 0, "[100, 1, -1, 10]");
        int int15 = doubleList3.lastIndexOf((java.lang.Object) strList10);
        boolean boolean16 = union1.equals((java.lang.Object) doubleList3);
        org.ccsds.moims.mo.mal.structures.Element element17 = doubleList3.createElement();
        java.lang.Long[] longArray44 = new java.lang.Long[]{281474993487884L, (-1L), 281475010265083L, 281475010265070L,
                                                            281474993487883L, 281474993487885L, 281474993487872L,
                                                            281475010265070L, 281474993487885L, 281474993487889L, (-1L),
                                                            281475010265086L, 281475010265078L, 4294967295L,
                                                            281474993487874L, 281474993487885L, 281474993487886L, 1L,
                                                            281475010265086L, 281475010265075L, 281474993487876L,
                                                            281474993487878L, 281474993487889L, (-1L), 100L,
                                                            281474993487875L};
        java.util.ArrayList<java.lang.Long> longList45 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean46 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList45,
            longArray44);
        java.lang.Long long48 = longList45.remove(0);
        longList45.ensureCapacity(8);
        java.util.Iterator<java.lang.Long> longItor51 = longList45.iterator();
        int int52 = doubleList3.indexOf((java.lang.Object) longList45);
        java.util.Spliterator<java.lang.Double> doubleSpliterator53 = doubleList3.spliterator();
        byte[] byteArray55 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(1);
        boolean boolean56 = doubleList3.equals((java.lang.Object) byteArray55);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D57 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray55);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(obj4);
        org.junit.Assert.assertNotNull(strArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + int15 + "' != '" + (-1) + "'", int15 == (-1));
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(element17);
        org.junit.Assert.assertNotNull(longArray44);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertTrue("'" + long48 + "' != '" + 281474993487884L + "'", long48.equals(281474993487884L));
        org.junit.Assert.assertNotNull(longItor51);
        org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-1) + "'", int52 == (-1));
        org.junit.Assert.assertNotNull(doubleSpliterator53);
        org.junit.Assert.assertNotNull(byteArray55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
    }
}
