package esa.nmf.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest0 {

    public static boolean debug = false;

    @Test
    public void test0001() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0001");
        }
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator0 = new esa.mo.platform.impl.util.PositionsCalculator();
    }

    @Test
    public void test0002() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0002");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0003() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0003");
        }
        byte[] byteArray3 = new byte[]{(byte) 1, (byte) 10, (byte) 0};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0004() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0004");
        }
        esa.mo.platform.impl.util.HelperGPS helperGPS0 = new esa.mo.platform.impl.util.HelperGPS();
    }

    @Test
    public void test0005() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0005");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0006() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0006");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0007() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0007");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0008() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0008");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0009() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0009");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0010() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0010");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0011() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0011");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0012() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0012");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0013() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0013");
        }
        esa.mo.platform.impl.util.HelperIADCS100 helperIADCS100_0 = new esa.mo.platform.impl.util.HelperIADCS100();
    }

    @Test
    public void test0014() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0014");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0015() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0015");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0016() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0016");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[100, 1, 1, -1, 10]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0017() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0017");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0018() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0018");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0019() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0019");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0020() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0020");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0021() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0021");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0022() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0022");
        }
        org.ccsds.moims.mo.platform.gps.structures.Position position0 = null;
        org.ccsds.moims.mo.platform.gps.structures.Position position1 = null;
        try {
            double double2 = esa.mo.platform.impl.util.PositionsCalculator.deltaDistanceFrom2Points(position0,
                position1);
            org.junit.Assert.fail(
                "Expected exception of type java.io.IOException; message: Not a valid position. Neither p1 nor p2 can be null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0023() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0023");
        }
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        try {
            esa.mo.platform.impl.provider.gen.GPSManager gPSManager1 = new esa.mo.platform.impl.provider.gen.GPSManager(
                cOMServicesProvider0);
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.platform.impl.provider.gen.GPSManager.randoop_classUsedFlag from class esa.mo.com.impl.util.DefinitionsManager");
        } catch (java.lang.IllegalAccessError e) {
        }
    }

    @Test
    public void test0024() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0024");
        }
        byte[] byteArray6 = new byte[]{(byte) 1, (byte) 10, (byte) 1, (byte) 100, (byte) 0, (byte) 0};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D7 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
    }

    @Test
    public void test0025() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0025");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0026() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0026");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0027() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0027");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0028() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0028");
        }
        java.lang.Boolean[] booleanArray1 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList2 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean3 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList2,
            booleanArray1);
        java.lang.String str4 = booleanList2.toString();
        java.lang.String str5 = booleanList2.toString();
        java.util.stream.Stream<java.lang.Boolean> booleanStream6 = booleanList2.stream();
        esa.mo.platform.impl.util.HelperIADCS100 helperIADCS100_7 = new esa.mo.platform.impl.util.HelperIADCS100();
        esa.mo.platform.impl.util.HelperIADCS100[] helperIADCS100Array8 = new esa.mo.platform.impl.util.HelperIADCS100[]{helperIADCS100_7};
        try {
            esa.mo.platform.impl.util.HelperIADCS100[] helperIADCS100Array9 = booleanList2.toArray(
                helperIADCS100Array8);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayStoreException; message: null");
        } catch (java.lang.ArrayStoreException e) {
        }
        org.junit.Assert.assertNotNull(booleanArray1);
        org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3);
        org.junit.Assert.assertTrue("'" + str4 + "' != '" + "[true]" + "'", str4.equals("[true]"));
        org.junit.Assert.assertTrue("'" + str5 + "' != '" + "[true]" + "'", str5.equals("[true]"));
        org.junit.Assert.assertNotNull(booleanStream6);
        org.junit.Assert.assertNotNull(helperIADCS100Array8);
    }

    @Test
    public void test0029() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0029");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0030() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0030");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0031() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0031");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0032() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0032");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0033() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0033");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-11));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0034() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0034");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0035() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0035");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0036() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0036");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-11));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0037() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0037");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0038() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0038");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0039() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0039");
        }
        java.lang.Long[] longArray2 = new java.lang.Long[]{1L, 281475010265070L};
        java.util.ArrayList<java.lang.Long> longList3 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList3, longArray2);
        java.util.stream.Stream<java.lang.Long> longStream5 = longList3.stream();
        boolean boolean7 = longList3.remove((java.lang.Object) (-4));
        java.lang.String str8 = longList3.toString();
        int int9 = longList3.size();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator10 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator positionsCalculator11 = new esa.mo.platform.impl.util.PositionsCalculator();
        esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray12 = new esa.mo.platform.impl.util.PositionsCalculator[]{positionsCalculator10,
                                                                                                                                         positionsCalculator11};
        try {
            esa.mo.platform.impl.util.PositionsCalculator[] positionsCalculatorArray13 = longList3.toArray(
                positionsCalculatorArray12);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayStoreException; message: null");
        } catch (java.lang.ArrayStoreException e) {
        }
        org.junit.Assert.assertNotNull(longArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertNotNull(longStream5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertTrue("'" + str8 + "' != '" + "[1, 281475010265070]" + "'", str8.equals(
            "[1, 281475010265070]"));
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 2 + "'", int9 == 2);
        org.junit.Assert.assertNotNull(positionsCalculatorArray12);
    }

    @Test
    public void test0040() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0040");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0041() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0041");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0042() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0042");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0043() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0043");
        }
        byte[] byteArray1 = new byte[]{(byte) 100};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0044() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0044");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0045() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0045");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0046() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0046");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
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
    public void test0047() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0047");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0048() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0048");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0049() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0049");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0051() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0051");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0052() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0052");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0053() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0053");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0054() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0054");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0055() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0055");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0056() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0056");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0057() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0057");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0058() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0058");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0059() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0059");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0060() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0060");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0061() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0061");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0062() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0062");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0063() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0063");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0064() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0064");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("14");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0065() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0065");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0066() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0066");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0067() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0067");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0068() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0068");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0069() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0069");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0070() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0070");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0071() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0071");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("hi!");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0072() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0072");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0073() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0073");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0074() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0074");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-2), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0075() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0075");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0076() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0076");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
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
    public void test0077() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0077");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0078() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0078");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("true");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0079() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0079");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0080() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0080");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0082() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0082");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0083() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0083");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0084() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0084");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0085() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0085");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(58);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0086() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0086");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("14");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0087() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0087");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[1]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0088() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0088");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0089() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0089");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0090() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0090");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0091() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0091");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0092() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0092");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0093() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0093");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[1, 10, -1, 1]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0094() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0094");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0095() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0095");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0096() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0096");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0097() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0097");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[0]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0098() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0098");
        }
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Object obj1 = doubleList0.clone();
        java.lang.String[] strArray6 = new java.lang.String[]{"", "", "[100, 1, -1, 10]", ""};
        java.util.ArrayList<java.lang.String> strList7 = new java.util.ArrayList<java.lang.String>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList7, strArray6);
        strList7.add((int) (short) 0, "[100, 1, -1, 10]");
        int int12 = doubleList0.lastIndexOf((java.lang.Object) strList7);
        boolean boolean14 = doubleList0.add((java.lang.Double) (-1.0d));
        int int15 = doubleList0.size();
        org.ccsds.moims.mo.mal.structures.UShort uShort16 = doubleList0.getServiceNumber();
        doubleList0.trimToSize();
        esa.mo.platform.impl.util.HelperIADCS100 helperIADCS100_18 = new esa.mo.platform.impl.util.HelperIADCS100();
        esa.mo.platform.impl.util.HelperIADCS100[] helperIADCS100Array19 = new esa.mo.platform.impl.util.HelperIADCS100[]{helperIADCS100_18};
        try {
            esa.mo.platform.impl.util.HelperIADCS100[] helperIADCS100Array20 = doubleList0.toArray(
                helperIADCS100Array19);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayStoreException; message: null");
        } catch (java.lang.ArrayStoreException e) {
        }
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(strArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertTrue("'" + int12 + "' != '" + (-1) + "'", int12 == (-1));
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
        org.junit.Assert.assertTrue("'" + int15 + "' != '" + 1 + "'", int15 == 1);
        org.junit.Assert.assertNotNull(uShort16);
        org.junit.Assert.assertNotNull(helperIADCS100Array19);
    }

    @Test
    public void test0099() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0099");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[0.0]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0100() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0100");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0101() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0101");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0102() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0102");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0103() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0103");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[false]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0104() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0104");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[1, 281475010265070]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0105() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0105");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0106() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0106");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 19456 + "'", short3 == (short) 19456);
    }

    @Test
    public void test0107() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0107");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0108() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0108");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(58);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0109() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0109");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0110() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0110");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0111() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0111");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0112() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0112");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0113() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0113");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed6 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0114() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0114");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[false, false]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0115() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0115");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0116() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0116");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0117() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0117");
        }
        byte[] byteArray5 = new byte[]{(byte) 1, (byte) 100, (byte) 10, (byte) 100, (byte) 10};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0118() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0118");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0119() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0119");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487885L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0120() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0120");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-4));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0122() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0122");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0123() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0123");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0124() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0124");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[10.0]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0125() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0125");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0126() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0126");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0127() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0127");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0128() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0128");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0129() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0129");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 19456 + "'", short3 == (short) 19456);
    }

    @Test
    public void test0130() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0130");
        }
        byte[] byteArray4 = new byte[]{(byte) 100, (byte) 1, (byte) 0, (byte) -1};
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray4);
    }

    @Test
    public void test0131() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0131");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0132() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0132");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[[100, 1, -1, 10], , , [100, 1, -1, 10], ]");
            org.junit.Assert.fail(
                "Expected exception of type java.lang.NumberFormatException wrapped in IOException; message: For input string: \"10]\"");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0133() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0133");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0134() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0134");
        }
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray0 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList1 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean2 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList1, uRIArray0);
        java.lang.Object obj3 = uRIList1.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream4 = uRIList1.parallelStream();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray5 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList6 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean7 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList6, uRIArray5);
        java.lang.Double[] doubleArray11 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList12 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList12,
            doubleArray11);
        java.lang.Byte[] byteArray18 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList19 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean20 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList19,
            byteArray18);
        java.lang.String str21 = byteList19.toString();
        boolean boolean22 = doubleList12.retainAll((java.util.Collection<java.lang.Byte>) byteList19);
        boolean boolean23 = uRIList6.containsAll((java.util.Collection<java.lang.Byte>) byteList19);
        java.lang.Object obj24 = byteList19.clone();
        boolean boolean25 = uRIList1.retainAll((java.util.Collection<java.lang.Byte>) byteList19);
        esa.mo.platform.impl.util.HelperIADCS100 helperIADCS100_26 = new esa.mo.platform.impl.util.HelperIADCS100();
        esa.mo.platform.impl.util.HelperIADCS100 helperIADCS100_27 = new esa.mo.platform.impl.util.HelperIADCS100();
        esa.mo.platform.impl.util.HelperIADCS100[] helperIADCS100Array28 = new esa.mo.platform.impl.util.HelperIADCS100[]{helperIADCS100_26,
                                                                                                                          helperIADCS100_27};
        try {
            esa.mo.platform.impl.util.HelperIADCS100[] helperIADCS100Array29 = byteList19.toArray(
                helperIADCS100Array28);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayStoreException; message: null");
        } catch (java.lang.ArrayStoreException e) {
        }
        org.junit.Assert.assertNotNull(uRIArray0);
        org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + false + "'", !boolean2);
        org.junit.Assert.assertNotNull(obj3);
        org.junit.Assert.assertNotNull(uRIStream4);
        org.junit.Assert.assertNotNull(uRIArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(doubleArray11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(byteArray18);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
        org.junit.Assert.assertTrue("'" + str21 + "' != '" + "[100, 1, -1, 10]" + "'", str21.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
        org.junit.Assert.assertNotNull(obj24);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
        org.junit.Assert.assertNotNull(helperIADCS100Array28);
    }

    @Test
    public void test0135() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0135");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0136() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0136");
        }
        byte[] byteArray2 = new byte[]{(byte) 100, (byte) -1};
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion3 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray2);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray2);
    }

    @Test
    public void test0137() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0137");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0138() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0138");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("15.0");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0139() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0139");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0140() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0140");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-4));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0141() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0141");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("16");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0142() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0142");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0143() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0143");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[1, 281475010265070]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0144() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0144");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0145() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0145");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0146() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0146");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("281475010265070");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0147() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0147");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4294967295L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0148() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0148");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0149() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0149");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0150() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0150");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 36);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0151() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0151");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0152() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0152");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 2 + "'", short3 == (short) 2);
    }

    @Test
    public void test0153() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0153");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0154() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0154");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0155() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0155");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0156() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0156");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("36.0");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0157() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0157");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0158() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0158");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) -1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0159() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0159");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4294967295L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0160() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0160");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0161() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0161");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0162() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0162");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-4));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0163() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0163");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0164() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0164");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0165() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0165");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0166() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0166");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0167() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0167");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0168() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0168");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("1");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0169() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0169");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-7));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0170() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0170");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(58);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0171() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0171");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0173() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0173");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(58);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0174() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0174");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) -1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0175() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0175");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 2 + "'", short3 == (short) 2);
    }

    @Test
    public void test0176() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0176");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0177() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0177");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10.0f);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0178() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0178");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0179() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0179");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-11));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0180() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0180");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[[100, 1, -1, 10], hi!, ]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0181() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0181");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 66);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0182() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0182");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-4));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0183() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0183");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0184() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0184");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0185() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0185");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 19456 + "'", short3 == (short) 19456);
    }

    @Test
    public void test0186() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0186");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-2), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0187() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0187");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0188() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0188");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0189() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0189");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487880L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0190() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0190");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0191() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0191");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0192() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0192");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0193() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0193");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0194() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0194");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0195() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0195");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[1, 100, 1, 0]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0196() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0196");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0197() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0197");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0198() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0198");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-11));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0199() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0199");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0200() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0200");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0202() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0202");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0203() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0203");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0204() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0204");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0205() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0205");
        }
        byte[] byteArray5 = new byte[]{(byte) 1, (byte) -1, (byte) 100, (byte) 10, (byte) 0};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D6 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0206() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0206");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0207() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0207");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("10.0");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0208() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0208");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-15), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0209() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0209");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0210() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0210");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0211() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0211");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("true");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0212() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0212");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0213() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0213");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0214() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0214");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0215() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0215");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-15), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0216() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0216");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0217() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0217");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0218() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0218");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-11));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0219() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0219");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0220() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0220");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0221() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0221");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0222() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0222");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0223() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0223");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 19456 + "'", short3 == (short) 19456);
    }

    @Test
    public void test0224() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0224");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 64);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0225() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0225");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0226() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0226");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion6 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
    }

    @Test
    public void test0227() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0227");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0228() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0228");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-7));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0229() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0229");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0230() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0230");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("8");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0231() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0231");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0232() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0232");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0234() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0234");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0235() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0235");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0236() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0236");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0237() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0237");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0238() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0238");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) -1 + "'", short3 == (short) -1);
    }

    @Test
    public void test0239() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0239");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0240() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0240");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees(
                "[-1, 281475010265083, 281475010265070, 281474993487883, 281474993487885, 281474993487872, 281475010265070, 281474993487885, 281474993487889, -1, 281475010265086, 281475010265078, 4294967295, 281474993487874, 281474993487885, 281474993487886, 1, 281475010265086, 281475010265075, 281474993487876, 281474993487878, 281474993487889, -1, 100, 281474993487875]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0241() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0241");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[true]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0242() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0242");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0243() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0243");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0244() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0244");
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
                .getSunVectorFromSunPointingStatus(byteArray94);
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
    public void test0245() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0245");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0246() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0246");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 62);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0247() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0247");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0248() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0248");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 65536 + "'", int3 == 65536);
    }

    @Test
    public void test0249() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0249");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0250() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0250");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0251() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0251");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 19);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0252() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0252");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0253() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0253");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0254() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0254");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[10.0, -1.0, -1.0, 10.0]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0255() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0255");
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
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D95 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray93);
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
    public void test0256() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0256");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0257() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0257");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0259() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0259");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0260() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0260");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 66);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0261() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0261");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0262() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0262");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487876L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0263() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0263");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-4));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0264() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0264");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0266() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0266");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) -1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0267() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0267");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0268() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0268");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0269() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0269");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0270() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0270");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 62);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0271() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0271");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 15 + "'", short3 == (short) 15);
    }

    @Test
    public void test0272() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0272");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 19);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0273() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0273");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        long long3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 4625759767262920704L + "'", long3 == 4625759767262920704L);
    }

    @Test
    public void test0274() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0274");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0275() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0275");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0276() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0276");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0277() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0277");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0278() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0278");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0279() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0279");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0280() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0280");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 58);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0281() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0281");
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
                .getMTQFromSunPointingStatus(byteArray34);
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
    public void test0282() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0282");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0283() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0283");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0284() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0284");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion8 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0285() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0285");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 58);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0286() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0286");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0287() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0287");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4294967295L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0288() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0288");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487885L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0289() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0289");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0290() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0290");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487876L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0291() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0291");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion8 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0292() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0292");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0293() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0293");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0294() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0294");
        }
        byte[] byteArray4 = new byte[]{(byte) -1, (byte) 1, (byte) -1, (byte) -1};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray4);
    }

    @Test
    public void test0295() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0295");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0296() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0296");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0297() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0297");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-18));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0298() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0298");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0299() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0299");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 66 + "'", int3 == 66);
    }

    @Test
    public void test0300() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0300");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0301() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0301");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0302() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0302");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0303() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0303");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[1, 281475010265070, 10]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0304() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0304");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0305() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0305");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0306() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0306");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4294967295L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0307() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0307");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-15), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0308() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0308");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487884L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0310() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0310");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0311() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0311");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0312() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0312");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0314() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0314");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487890L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0315() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0315");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[1, 100, 1, 0]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0316() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0316");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0317() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0317");
        }
        byte[] byteArray6 = new byte[]{(byte) 1, (byte) 1, (byte) 1, (byte) 10, (byte) 0, (byte) 1};
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed7 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
    }

    @Test
    public void test0318() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0318");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0319() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0319");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
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
    public void test0320() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0320");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0321() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0321");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0323() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0323");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) 'a');
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0324() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0324");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0325() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0325");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0326() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0326");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0327() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0327");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[true, false]");
            org.junit.Assert.fail(
                "Expected exception of type java.io.IOException; message: Did not find decimal in [true, false]");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0328() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0328");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (-11));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0329() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0329");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0330() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0330");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487880L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0331() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0331");
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
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed11 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray9);
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
    public void test0332() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0332");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0333() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0333");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0334() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0334");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487885L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0335() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0335");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("15.0");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0336() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0336");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees(
                "[[100, 1, -1, 10], , , [100, 1, -1, 10], ]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0337() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0337");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0338() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0338");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0339() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0339");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0340() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0340");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-13));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0341() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0341");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(0.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0342() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0342");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0343() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0343");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0344() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0344");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4294967295L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0345() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0345");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 62);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0346() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0346");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(0.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0348() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0348");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-18));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0349() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0349");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0350() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0350");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) -1 + "'", short3 == (short) -1);
    }

    @Test
    public void test0351() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0351");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-13));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0352() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0352");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0353() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0353");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0354() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0354");
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
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed95 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray93);
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
    public void test0355() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0355");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-2), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0356() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0356");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0357() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0357");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 24);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0358() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0358");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[1]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0359() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0359");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0360() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0360");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("0.0");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0361() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0361");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D8 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test0362() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0362");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0363() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0363");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) ' ');
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0364() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0364");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487876L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0365() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0365");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0366() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0366");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) (short) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0367() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0367");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-7));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0368() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0368");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 19);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0369() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0369");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487885L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0370() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0370");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 36);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0371() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0371");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(100.0f);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0372() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0372");
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
                .getMTQFromSpinModeStatus(byteArray32);
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
    public void test0373() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0373");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265070L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0374() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0374");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487876L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0375() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0375");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("5");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0376() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0376");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0377() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0377");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 29);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0378() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0378");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0379() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0379");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0380() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0380");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0381() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0381");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265070L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0382() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0382");
        }
        java.lang.Long[] longArray2 = new java.lang.Long[]{1L, 281475010265070L};
        java.util.ArrayList<java.lang.Long> longList3 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList3, longArray2);
        java.util.stream.Stream<java.lang.Long> longStream5 = longList3.stream();
        boolean boolean6 = longList3.isEmpty();
        int int7 = longList3.size();
        java.util.stream.Stream<java.lang.Long> longStream8 = longList3.stream();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray9 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList10 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean11 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList10, uRIArray9);
        java.lang.Double[] doubleArray15 = new java.lang.Double[]{100.0d, 100.0d, 1.0d};
        java.util.ArrayList<java.lang.Double> doubleList16 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean17 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList16,
            doubleArray15);
        java.lang.Byte[] byteArray22 = new java.lang.Byte[]{(byte) 100, (byte) 1, (byte) -1, (byte) 10};
        java.util.ArrayList<java.lang.Byte> byteList23 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean24 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList23,
            byteArray22);
        java.lang.String str25 = byteList23.toString();
        boolean boolean26 = doubleList16.retainAll((java.util.Collection<java.lang.Byte>) byteList23);
        boolean boolean27 = uRIList10.containsAll((java.util.Collection<java.lang.Byte>) byteList23);
        java.lang.Integer[] intArray40 = new java.lang.Integer[]{6, 6, 28, 2, 4, 0, 62, 64, 19, 20, 11, 62};
        java.util.ArrayList<java.lang.Integer> intList41 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean42 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList41,
            intArray40);
        java.lang.Byte[] byteArray46 = new java.lang.Byte[]{(byte) 1, (byte) 100, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList47 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList47,
            byteArray46);
        boolean boolean50 = byteList47.add((java.lang.Byte) (byte) 0);
        java.util.stream.Stream<java.lang.Byte> byteStream51 = byteList47.parallelStream();
        int int53 = byteList47.indexOf((java.lang.Object) 5);
        boolean boolean54 = intList41.contains((java.lang.Object) byteList47);
        boolean boolean55 = uRIList10.retainAll((java.util.Collection<java.lang.Byte>) byteList47);
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.URI> uRISpliterator56 = uRIList10.spliterator();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream57 = uRIList10.stream();
        esa.mo.platform.impl.util.HelperGPS helperGPS58 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS59 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS60 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS61 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS62 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS helperGPS63 = new esa.mo.platform.impl.util.HelperGPS();
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray64 = new esa.mo.platform.impl.util.HelperGPS[]{helperGPS58,
                                                                                                           helperGPS59,
                                                                                                           helperGPS60,
                                                                                                           helperGPS61,
                                                                                                           helperGPS62,
                                                                                                           helperGPS63};
        esa.mo.platform.impl.util.HelperGPS[] helperGPSArray65 = uRIList10.toArray(helperGPSArray64);
        try {
            esa.mo.platform.impl.util.HelperGPS[] helperGPSArray66 = longList3.toArray(helperGPSArray65);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayStoreException; message: null");
        } catch (java.lang.ArrayStoreException e) {
        }
        org.junit.Assert.assertNotNull(longArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertNotNull(longStream5);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + false + "'", !boolean6);
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + 2 + "'", int7 == 2);
        org.junit.Assert.assertNotNull(longStream8);
        org.junit.Assert.assertNotNull(uRIArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertNotNull(doubleArray15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertNotNull(byteArray22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertTrue("'" + str25 + "' != '" + "[100, 1, -1, 10]" + "'", str25.equals(
            "[100, 1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
        org.junit.Assert.assertNotNull(intArray40);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
        org.junit.Assert.assertNotNull(byteArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + true + "'", boolean50);
        org.junit.Assert.assertNotNull(byteStream51);
        org.junit.Assert.assertTrue("'" + int53 + "' != '" + (-1) + "'", int53 == (-1));
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + false + "'", !boolean55);
        org.junit.Assert.assertNotNull(uRISpliterator56);
        org.junit.Assert.assertNotNull(uRIStream57);
        org.junit.Assert.assertNotNull(helperGPSArray64);
        org.junit.Assert.assertNotNull(helperGPSArray65);
    }

    @Test
    public void test0383() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0383");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-7));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0384() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0384");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(9);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0385() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0385");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0386() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0386");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-10));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0387() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0387");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0388() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0388");
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
                .getCurrentWheelSpeedFromActuatorTM(byteArray6);
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
    public void test0389() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0389");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0390() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0390");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0391() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0391");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65536);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0392() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0392");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0393() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0393");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0394() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0394");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281474993487885L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0395() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0395");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0396() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0396");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65536);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0397() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0397");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0398() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0398");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265070L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0399() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0399");
        }
        byte[] byteArray6 = new byte[]{(byte) 1, (byte) 100, (byte) -1, (byte) -1, (byte) 100, (byte) 1};
        int int8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray6, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D9 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + 1694498660 + "'", int8 == 1694498660);
    }

    @Test
    public void test0400() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0400");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0401() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0401");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0402() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0402");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray((-15), (int) (byte) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0403() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0403");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 100);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0404() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0404");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-13));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0405() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0405");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0406() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0406");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) (short) 100);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray(18, 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion5 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test0407() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0407");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0408() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0408");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(58);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0409() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0409");
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
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray75);
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
    public void test0410() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0410");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0411() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0411");
        }
        org.ccsds.moims.mo.mal.structures.FineTime fineTime1 = new org.ccsds.moims.mo.mal.structures.FineTime((long) 8);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = fineTime1.getAreaVersion();
        byte[] byteArray4 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        boolean boolean5 = fineTime1.equals((java.lang.Object) byteArray4);
        long long7 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray4,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion8 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray4);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertTrue("'" + long7 + "' != '" + 4823355201182236832L + "'", long7 == 4823355201182236832L);
    }

    @Test
    public void test0412() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0412");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[true]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0413() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0413");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0414() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0414");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(16L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0415() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0415");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0416() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0416");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0418() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0418");
        }
        byte[] byteArray2 = new byte[]{(byte) -1, (byte) -1};
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion3 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray2);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray2);
    }

    @Test
    public void test0419() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0419");
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
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion34 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray32);
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
    public void test0421() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0421");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[255]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0422() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0422");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test0423() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0423");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0424() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0424");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(16L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0425() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0425");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0426() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0426");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0427() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0427");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 19);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0428() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0428");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 13);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0429() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0429");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        double double3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + double3 + "' != '" + 2.81475010265078E14d + "'", double3 ==
            2.81475010265078E14d);
    }

    @Test
    public void test0430() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0430");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 65536);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0431() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0431");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0432() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0432");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(0.0f);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0433() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0433");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0434() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0434");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (byte) 1);
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
    public void test0435() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0435");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0436() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0436");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 65536);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0437() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0437");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[52]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0438() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0438");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0439() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0439");
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
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed95 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray93);
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
    public void test0440() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0440");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487876L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0441() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0441");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(15);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 15 + "'", short3 == (short) 15);
    }

    @Test
    public void test0442() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0442");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487876L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0443() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0443");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0444() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0444");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(
            (int) (short) 19456);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0445() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0445");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(16L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0446() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0446");
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
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D95 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray93);
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
    public void test0447() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0447");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-7));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0448() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0448");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[1, 10, -1, 1]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0449() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0449");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 19);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0450() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0450");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0451() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0451");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0452() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0452");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0453() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0453");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(65535);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0454() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0454");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0456() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0456");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 2);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0457() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0457");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 36);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0458() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0458");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(0.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0459() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0459");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0460() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0460");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 100);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0461() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0461");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(
            (int) (short) 19456);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0462() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0462");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(9);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0463() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0463");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-13));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0464() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0464");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (-11));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0465() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0465");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[[1, 281475010265070], hi!, ]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0466() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0466");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0467() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0467");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 62);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0468() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0468");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0469() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0469");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81474993487882E14d);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 281474993487882L, 0,
            byteArray3);
        float float6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getFloatFromByteArray(byteArray3,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion7 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + float6 + "' != '" + 2.8147501E14f + "'", float6 == 2.8147501E14f);
    }

    @Test
    public void test0470() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0470");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[52]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0471() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0471");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0472() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0472");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0473() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0473");
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
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D10 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray8);
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
    public void test0474() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0474");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0475() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0475");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487880L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0476() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0476");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0477() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0477");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-5));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0478() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0478");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 10);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0479() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0479");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 36);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0480() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0480");
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
                .getMTQFromActuatorTM(byteArray94);
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
    public void test0481() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0481");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0482() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0482");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0483() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0483");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(
            (float) 281474993487875L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0484() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0484");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(14);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0485() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0485");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) ' ');
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0486() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0486");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        double double3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + double3 + "' != '" + 2.81475010265078E14d + "'", double3 ==
            2.81475010265078E14d);
    }

    @Test
    public void test0487() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0487");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("36");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test0488() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0488");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (-11));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0489() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0489");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) 10, 2, byteArray3);
        int int6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray3, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion7 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 16672 + "'", int6 == 16672);
    }

    @Test
    public void test0490() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0490");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[255]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test0491() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0491");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4625759767262920704L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0492() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0492");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-2));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0493() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0493");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0494() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0494");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(32);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0495() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0495");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 19456 + "'", short3 == (short) 19456);
    }

    @Test
    public void test0496() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0496");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0497() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0497");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 10L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0498() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0498");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(16L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0499() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0499");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (short) 10);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test0500() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest0.test0500");
        }
        byte[] byteArray6 = new byte[]{(byte) 1, (byte) 100, (byte) -1, (byte) -1, (byte) 100, (byte) 1};
        int int8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray6, 1);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed9 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + 1694498660 + "'", int8 == 1694498660);
    }
}
