package esa.nmf.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest2 {

    public static boolean debug = false;

    @Test
    public void test1001() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1001");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-1));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1002() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1002");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(
            (int) (short) 19456);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1003() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1003");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1004() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1004");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(0L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1005() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1005");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((int) ' ');
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1006() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1006");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(18);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1007() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1007");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1008() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1008");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        int int9 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray5, 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D10 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
    }

    @Test
    public void test1009() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1009");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("2");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test1010() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1010");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) ' ');
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1011() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1011");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1012() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1012");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 256);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMagneticFieldFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1013() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1013");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[0.0, -1.0]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test1014() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1014");
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
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray94);
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
    public void test1015() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1015");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (byte) 0);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test1016() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1016");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray(0.0f, (int) (short) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test1017() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1017");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 15);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1018() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1018");
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
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed36 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray34);
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
    public void test1019() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1019");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (byte) 1);
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
    public void test1020() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1020");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(44);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1021() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1021");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray((-13));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1022() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1022");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) ' ');
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test1023() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1023");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[[100, 1, -1, 10], hi!, ]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test1024() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1024");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 2);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1025() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1025");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[10, 1, -1, 10]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test1026() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1026");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 24);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1027() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1027");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 6);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1,
            (int) (byte) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 0 + "'", short3 == (short) 0);
    }

    @Test
    public void test1028() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1028");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1029() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1029");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((-7));
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1030() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1030");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(66);
        int int3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getIntFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 66 + "'", int3 == 66);
    }

    @Test
    public void test1032() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1032");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray((int) (short) 256);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1033() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1033");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(65536);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1034() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1034");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            2.81475010265073E14d);
        double double3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + double3 + "' != '" + 2.81475010265073E14d + "'", double3 ==
            2.81475010265073E14d);
    }

    @Test
    public void test1035() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1035");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        long long3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion4 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 4625759767262920704L + "'", long3 == 4625759767262920704L);
    }

    @Test
    public void test1036() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1036");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        double double3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + double3 + "' != '" + 2.81475010265078E14d + "'", double3 ==
            2.81475010265078E14d);
    }

    @Test
    public void test1037() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1037");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265070L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1038() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1038");
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
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream57 = uRIList1.stream();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream58 = uRIList1.stream();
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
        org.junit.Assert.assertNotNull(uRIStream57);
        org.junit.Assert.assertNotNull(uRIStream58);
    }

    @Test
    public void test1039() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1039");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(6);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1040() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1040");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265078L);
        double double3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getDoubleFromByteArray(byteArray1,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + double3 + "' != '" + 2.81475010265078E14d + "'", double3 ==
            2.81475010265078E14d);
    }

    @Test
    public void test1041() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1041");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 56);
        short short3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getInt16FromByteArray(byteArray1, 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + short3 + "' != '" + (short) 19456 + "'", short3 == (short) 19456);
    }

    @Test
    public void test1042() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1042");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 281475010265077L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1043() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1043");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1044() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1044");
        }
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = octetList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = octetList0.getAreaVersion();
        java.lang.Integer int3 = uOctet2.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = uOctet2.getServiceNumber();
        byte[] byteArray6 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        boolean boolean7 = uOctet2.equals((java.lang.Object) byteArray6);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion8 = esa.mo.platform.impl.util.HelperIADCS100
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
    }

    @Test
    public void test1045() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1045");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(28);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getTargetQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1046() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1046");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281475010265073L);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putIntInByteArray(9, 4, byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test1047() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1047");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 7);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1048() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1048");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(16L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1049() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1049");
        }
        try {
            org.ccsds.moims.mo.platform.gps.structures.Position position1 = esa.mo.platform.impl.util.HelperGPS
                .gpggalong2Position("[1, 281475010265070, 10]");
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: 9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
    }

    @Test
    public void test1050() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1050");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 6);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1051() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1051");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 66);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1052() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1052");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) (-15));
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray((float) (byte) -1, (int) (byte) 1,
            byteArray3);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed5 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test1053() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1053");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487878L);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1054() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1054");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(60);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1055() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1055");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 1);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1056() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1056");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(3.0d);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1057() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1057");
        }
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 28);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putLongInByteArray((long) 5, (int) (byte) 0, byteArray5);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putDoubleInByteArray((double) 64, (int) (short) 0,
            byteArray5);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed8 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray5);
    }

    @Test
    public void test1058() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1058");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 9);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularMomentumFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1059() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1059");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1060() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1060");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) (-2));
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1061() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1061");
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
        java.lang.Long long84 = longList1.getShortForm();
        java.util.Spliterator<java.lang.Long> longSpliterator85 = longList1.spliterator();
        java.lang.Integer int86 = longList1.getTypeShortForm();
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
        org.junit.Assert.assertTrue("'" + long84 + "' != '" + 281475010265075L + "'", long84.equals(281475010265075L));
        org.junit.Assert.assertNotNull(longSpliterator85);
        org.junit.Assert.assertTrue("'" + int86 + "' != '" + (-13) + "'", int86.equals((-13)));
    }

    @Test
    public void test1062() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1062");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 14L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1063() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1063");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray(100.0f);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1064() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1064");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 11);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAttitudeFromSensorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1065() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1065");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("[100]");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test1066() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1066");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(
            (double) 4625759767262920704L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed2 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromSunPointingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1067() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1067");
        }
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        shortList0.trimToSize();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = shortList0.getAreaVersion();
        java.lang.String str3 = uOctet2.toString();
        byte[] byteArray5 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(4);
        boolean boolean6 = uOctet2.equals((java.lang.Object) byteArray5);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed7 = esa.mo.platform.impl.util.HelperIADCS100
                .getWheelSpeedFromFixWGS84TargetTrackingStatus(byteArray5);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertTrue("'" + str3 + "' != '" + "1" + "'", str3.equals("1"));
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + false + "'", !boolean6);
    }

    @Test
    public void test1068() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1068");
        }
        try {
            float float1 = esa.mo.platform.impl.util.HelperGPS.degMinutes2Degrees("15");
            org.junit.Assert.fail("Expected exception of type java.io.IOException; message: null");
        } catch (java.io.IOException e) {
        }
    }

    @Test
    public void test1069() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1069");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 18);
        long long3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getLongFromByteArray(byteArray1, 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.WheelsSpeed wheelsSpeed4 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentWheelSpeedFromActuatorTM(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 4625759767262920704L + "'", long3 == 4625759767262920704L);
    }

    @Test
    public void test1070() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1070");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) 4);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getMTQFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1072() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1072");
        }
        byte[] byteArray3 = new byte[]{(byte) 0, (byte) 100, (byte) 100};
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D4 = esa.mo.platform.impl.util.HelperIADCS100
                .getSunVectorFromSpinModeStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test1073() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1073");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray(281474993487883L);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1074() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1074");
        }
        byte[] byteArray6 = new byte[]{(byte) 10, (byte) 0, (byte) 0, (byte) 1, (byte) -1, (byte) 10};
        float float8 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.getFloatFromByteArray(byteArray6,
            (int) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D9 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromNadirTargetTrackingStatus(byteArray6);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray6);
        org.junit.Assert.assertTrue("'" + float8 + "' != '" + 6.1629766E-33f + "'", float8 == 6.1629766E-33f);
    }

    @Test
    public void test1075() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1075");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray((double) 4);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getQuaternionsFromSpinModeStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1077() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1077");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.long2ByteArray((long) (byte) 0);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1078() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1078");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int16_2ByteArray(
            (int) (short) 19456);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getAngularVelocityFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1079() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1079");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(28);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D2 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1080() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1080");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.double2ByteArray(6371.0d);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromNadirTargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }

    @Test
    public void test1081() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1081");
        }
        byte[] byteArray3 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.int2ByteArray(52);
        esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.putFloatInByteArray(0.0f, (int) (short) 0, byteArray3);
        try {
            org.ccsds.moims.mo.platform.structures.VectorF3D vector3D5 = esa.mo.platform.impl.util.HelperIADCS100
                .getPositionFromFixWGS84TargetTrackingStatus(byteArray3);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray3);
    }

    @Test
    public void test1082() throws Throwable {
        if (debug) {
            System.out.format("%n%s%n", "RegressionTest2.test1082");
        }
        byte[] byteArray1 = esa.mo.platform.impl.util.HelperIADCS100.FWRefFineADCS.float2ByteArray((float) 11);
        try {
            org.ccsds.moims.mo.platform.autonomousadcs.structures.Quaternion quaternion2 = esa.mo.platform.impl.util.HelperIADCS100
                .getCurrentQuaternionsFromFixWGS84TargetTrackingStatus(byteArray1);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray1);
    }
}
