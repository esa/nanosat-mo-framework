package esa.nmf.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest1 {

    public static boolean debug = false;

    @Test
    public void test501() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test501");
        java.lang.Float[] floatArray3 = new java.lang.Float[]{10.0f, 100.0f, (-1.0f)};
        java.util.ArrayList<java.lang.Float> floatList4 = new java.util.ArrayList<java.lang.Float>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.Float>) floatList4,
            floatArray3);
        boolean boolean7 = floatList4.add((java.lang.Float) 10.0f);
        floatList4.trimToSize();
        boolean boolean9 = floatList4.isEmpty();
        java.util.stream.Stream<java.lang.Float> floatStream10 = floatList4.parallelStream();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl11 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long12 = nanoSatMOConnectorImpl11.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener13 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl11);
        nanoSatMOConnectorImpl11.initAdditionalServices();
        java.lang.Long long15 = nanoSatMOConnectorImpl11.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener16 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl11);
        boolean boolean17 = floatList4.equals((java.lang.Object) nanoSatMOConnectorImpl11);
        try {
            esa.mo.platform.impl.util.PlatformServicesConsumer platformServicesConsumer18 = null; // flaky: nanoSatMOConnectorImpl11.getPlatformServices();
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl.randoop_classUsedFlag from class esa.mo.nmf.NMFProvider");
        } catch (java.lang.IllegalAccessError e) {
        }
        org.junit.Assert.assertNotNull(floatArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
        org.junit.Assert.assertNotNull(floatStream10);
        org.junit.Assert.assertNull(long12);
        org.junit.Assert.assertNull(long15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
    }

    @Test
    public void test502() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test502");
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl0 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long1 = nanoSatMOConnectorImpl0.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener2 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl0);
        nanoSatMOConnectorImpl0.initAdditionalServices();
        nanoSatMOConnectorImpl0.initAdditionalServices();
        java.lang.Long long5 = nanoSatMOConnectorImpl0.getAppDirectoryId();
        try {
            // flaky:             nanoSatMOConnectorImpl0.reportActionExecutionProgress(false, 0, (int) (byte) 1, (int) 'a', 281475010265079L);
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl.randoop_classUsedFlag from class esa.mo.nmf.NMFProvider");
        } catch (java.lang.IllegalAccessError e) {
        }
        org.junit.Assert.assertNull(long1);
        org.junit.Assert.assertNull(long5);
    }

    @Test
    public void test503() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test503");
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl0 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener1 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl0);
        nanoSatMOConnectorImpl0.initAdditionalServices();
        org.ccsds.moims.mo.mal.structures.LongList longList4 = new org.ccsds.moims.mo.mal.structures.LongList();
        org.ccsds.moims.mo.mal.structures.UShort uShort5 = longList4.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet6 = longList4.getAreaVersion();
        longList4.clear();
        org.ccsds.moims.mo.mal.structures.UShort uShort8 = longList4.getServiceNumber();
        java.lang.Integer[] intArray13 = new java.lang.Integer[]{16, 100, 10, 1};
        java.util.ArrayList<java.lang.Integer> intList14 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList14,
            intArray13);
        int int17 = intList14.indexOf((java.lang.Object) 0);
        java.lang.Byte[] byteArray20 = new java.lang.Byte[]{(byte) 100, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList21 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList21,
            byteArray20);
        java.util.ListIterator<java.lang.Byte> byteItor24 = byteList21.listIterator((int) (short) 1);
        int int25 = byteList21.size();
        int int26 = intList14.lastIndexOf((java.lang.Object) int25);
        org.ccsds.moims.mo.mal.structures.URI uRI27 = new org.ccsds.moims.mo.mal.structures.URI();
        java.lang.Long long28 = uRI27.getShortForm();
        java.lang.Long long29 = uRI27.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element30 = uRI27.createElement();
        int int31 = intList14.lastIndexOf((java.lang.Object) element30);
        int int32 = longList4.indexOf((java.lang.Object) int31);
        java.util.Iterator<java.lang.Long> longItor33 = longList4.iterator();
        org.ccsds.moims.mo.mal.structures.ShortList shortList35 = new org.ccsds.moims.mo.mal.structures.ShortList(9);
        java.lang.Long long36 = shortList35.getShortForm();
        org.ccsds.moims.mo.mal.structures.ShortList[] shortListArray37 = new org.ccsds.moims.mo.mal.structures.ShortList[]{shortList35};
        org.ccsds.moims.mo.mal.structures.ShortList[] shortListArray38 = longList4.toArray(shortListArray37);
        try {
            java.lang.Boolean boolean39 = null; // flaky: nanoSatMOConnectorImpl0.pushParameterValue("[-1.0, 10.0, 10.0, 1.0]", (java.io.Serializable) longList4);
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl.randoop_classUsedFlag from class esa.mo.nmf.NMFProvider");
        } catch (java.lang.IllegalAccessError e) {
        }
        org.junit.Assert.assertNotNull(uShort5);
        org.junit.Assert.assertNotNull(uOctet6);
        org.junit.Assert.assertNotNull(uShort8);
        org.junit.Assert.assertNotNull(intArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + int17 + "' != '" + (-1) + "'", int17 == (-1));
        org.junit.Assert.assertNotNull(byteArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(byteItor24);
        org.junit.Assert.assertTrue("'" + int25 + "' != '" + 2 + "'", int25 == 2);
        org.junit.Assert.assertTrue("'" + int26 + "' != '" + (-1) + "'", int26 == (-1));
        org.junit.Assert.assertTrue("'" + long28 + "' != '" + 281474993487890L + "'", long28.equals(281474993487890L));
        org.junit.Assert.assertTrue("'" + long29 + "' != '" + 281474993487890L + "'", long29.equals(281474993487890L));
        org.junit.Assert.assertNotNull(element30);
        org.junit.Assert.assertTrue("'" + int31 + "' != '" + (-1) + "'", int31 == (-1));
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
        org.junit.Assert.assertNotNull(longItor33);
        org.junit.Assert.assertTrue("'" + long36 + "' != '" + 281475010265079L + "'", long36.equals(281475010265079L));
        org.junit.Assert.assertNotNull(shortListArray37);
        org.junit.Assert.assertNotNull(shortListArray38);
    }

    @Test
    public void test504() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test504");
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl0 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener1 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl0);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener2 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl0);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener3 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl0);
        java.lang.Long long4 = nanoSatMOConnectorImpl0.getAppDirectoryId();
        try {
            // flaky:             nanoSatMOConnectorImpl0.reportActionExecutionProgress(false, (int) (short) 0, 17, 100, (long) 4);
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl.randoop_classUsedFlag from class esa.mo.nmf.NMFProvider");
        } catch (java.lang.IllegalAccessError e) {
        }
        org.junit.Assert.assertNull(long4);
    }

    @Test
    public void test505() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test505");
        java.lang.Long[] longArray5 = new java.lang.Long[]{0L, 1L, 10L, 10L, 100L};
        java.util.ArrayList<java.lang.Long> longList6 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean7 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList6, longArray5);
        longList6.trimToSize();
        int int9 = longList6.size();
        longList6.add(0, (java.lang.Long) (-1L));
        int int14 = longList6.indexOf((java.lang.Object) 10);
        int int15 = longList6.size();
        org.ccsds.moims.mo.mal.structures.Union union17 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Byte) (byte) 100);
        java.lang.Long long18 = union17.getShortForm();
        java.lang.Integer int19 = union17.getTypeShortForm();
        java.lang.Byte byte20 = union17.getOctetValue();
        org.ccsds.moims.mo.mal.structures.UShort uShort21 = union17.getServiceNumber();
        java.lang.Byte[] byteArray24 = new java.lang.Byte[]{(byte) 100, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList25 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean26 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList25,
            byteArray24);
        java.util.ListIterator<java.lang.Byte> byteItor28 = byteList25.listIterator((int) (short) 1);
        int int29 = byteList25.size();
        int int31 = byteList25.indexOf((java.lang.Object) (short) 100);
        org.ccsds.moims.mo.mal.structures.LongList longList32 = new org.ccsds.moims.mo.mal.structures.LongList();
        org.ccsds.moims.mo.mal.structures.UShort uShort33 = longList32.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort34 = longList32.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort35 = longList32.getAreaNumber();
        longList32.ensureCapacity(14);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet38 = longList32.getAreaVersion();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl39 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long40 = nanoSatMOConnectorImpl39.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener41 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl39);
        nanoSatMOConnectorImpl39.initAdditionalServices();
        java.lang.Long long43 = nanoSatMOConnectorImpl39.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener44 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl39);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl45 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener46 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl45);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl47 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long48 = nanoSatMOConnectorImpl47.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener49 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl47);
        nanoSatMOConnectorImpl47.initAdditionalServices();
        nanoSatMOConnectorImpl47.initAdditionalServices();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl52 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener53 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl52);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl54 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener55 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl54);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener56 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl54);
        java.lang.Long long57 = nanoSatMOConnectorImpl54.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[] nanoSatMOConnectorImplArray58 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[]{nanoSatMOConnectorImpl39,
                                                                                                                                                          nanoSatMOConnectorImpl45,
                                                                                                                                                          nanoSatMOConnectorImpl47,
                                                                                                                                                          nanoSatMOConnectorImpl52,
                                                                                                                                                          nanoSatMOConnectorImpl54};
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[] nanoSatMOConnectorImplArray59 = longList32.toArray(
            nanoSatMOConnectorImplArray58);
        int int60 = longList32.size();
        boolean boolean61 = byteList25.remove((java.lang.Object) longList32);
        org.ccsds.moims.mo.mal.structures.UShort uShort62 = longList32.getServiceNumber();
        boolean boolean63 = union17.equals((java.lang.Object) uShort62);
        int int64 = longList6.indexOf((java.lang.Object) union17);
        org.junit.Assert.assertNotNull(longArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 5 + "'", int9 == 5);
        org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
        org.junit.Assert.assertTrue("'" + int15 + "' != '" + 6 + "'", int15 == 6);
        org.junit.Assert.assertTrue("'" + long18 + "' != '" + 281474993487879L + "'", long18.equals(281474993487879L));
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 7 + "'", int19.equals(7));
        org.junit.Assert.assertTrue("'" + byte20 + "' != '" + (byte) 100 + "'", byte20.equals((byte) 100));
        org.junit.Assert.assertNotNull(uShort21);
        org.junit.Assert.assertNotNull(byteArray24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertNotNull(byteItor28);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + 2 + "'", int29 == 2);
        org.junit.Assert.assertTrue("'" + int31 + "' != '" + (-1) + "'", int31 == (-1));
        org.junit.Assert.assertNotNull(uShort33);
        org.junit.Assert.assertNotNull(uShort34);
        org.junit.Assert.assertNotNull(uShort35);
        org.junit.Assert.assertNotNull(uOctet38);
        org.junit.Assert.assertNull(long40);
        org.junit.Assert.assertNull(long43);
        org.junit.Assert.assertNull(long48);
        org.junit.Assert.assertNull(long57);
        org.junit.Assert.assertNotNull(nanoSatMOConnectorImplArray58);
        org.junit.Assert.assertNotNull(nanoSatMOConnectorImplArray59);
        org.junit.Assert.assertTrue("'" + int60 + "' != '" + 0 + "'", int60 == 0);
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + false + "'", !boolean61);
        org.junit.Assert.assertNotNull(uShort62);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + false + "'", !boolean63);
        org.junit.Assert.assertTrue("'" + int64 + "' != '" + (-1) + "'", int64 == (-1));
    }

    @Test
    public void test506() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test506");
        java.lang.Long[] longArray5 = new java.lang.Long[]{0L, 1L, 10L, 10L, 100L};
        java.util.ArrayList<java.lang.Long> longList6 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean7 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList6, longArray5);
        longList6.trimToSize();
        int int9 = longList6.size();
        longList6.ensureCapacity((int) (short) 255);
        java.lang.Float[] floatArray16 = new java.lang.Float[]{(-1.0f), 10.0f, 10.0f, 1.0f};
        java.util.ArrayList<java.lang.Float> floatList17 = new java.util.ArrayList<java.lang.Float>();
        boolean boolean18 = java.util.Collections.addAll((java.util.Collection<java.lang.Float>) floatList17,
            floatArray16);
        int int20 = floatList17.lastIndexOf((java.lang.Object) (-1L));
        java.util.Iterator<java.lang.Float> floatItor21 = floatList17.iterator();
        org.ccsds.moims.mo.mal.structures.Identifier identifier22 = new org.ccsds.moims.mo.mal.structures.Identifier();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet23 = identifier22.getAreaVersion();
        boolean boolean25 = identifier22.equals((java.lang.Object) 0);
        boolean boolean26 = floatList17.contains((java.lang.Object) identifier22);
        org.ccsds.moims.mo.mal.structures.UShort[] uShortArray27 = new org.ccsds.moims.mo.mal.structures.UShort[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList28 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
        boolean boolean29 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList28, uShortArray27);
        int int31 = uShortList28.lastIndexOf((java.lang.Object) 'a');
        boolean boolean32 = uShortList28.isEmpty();
        boolean boolean33 = floatList17.retainAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList28);
        boolean boolean34 = longList6.containsAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList28);
        uShortList28.trimToSize();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl36 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener37 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl36);
        nanoSatMOConnectorImpl36.initAdditionalServices();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener39 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl36);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener40 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl36);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl41 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener42 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl41);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener43 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl41);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener44 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl41);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener45 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl41);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl46 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long47 = nanoSatMOConnectorImpl46.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener48 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl46);
        nanoSatMOConnectorImpl46.initAdditionalServices();
        nanoSatMOConnectorImpl46.initAdditionalServices();
        java.lang.Long long51 = nanoSatMOConnectorImpl46.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener52 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl46);
        esa.mo.com.impl.util.EventReceivedListener[] eventReceivedListenerArray53 = new esa.mo.com.impl.util.EventReceivedListener[]{closeAppEventListener40,
                                                                                                                                     closeAppEventListener45,
                                                                                                                                     closeAppEventListener52};
        esa.mo.com.impl.util.EventReceivedListener[] eventReceivedListenerArray54 = uShortList28.toArray(
            eventReceivedListenerArray53);
        org.junit.Assert.assertNotNull(longArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 5 + "'", int9 == 5);
        org.junit.Assert.assertNotNull(floatArray16);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertTrue("'" + int20 + "' != '" + (-1) + "'", int20 == (-1));
        org.junit.Assert.assertNotNull(floatItor21);
        org.junit.Assert.assertNotNull(uOctet23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(uShortArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
        org.junit.Assert.assertTrue("'" + int31 + "' != '" + (-1) + "'", int31 == (-1));
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertNull(long47);
        org.junit.Assert.assertNull(long51);
        org.junit.Assert.assertNotNull(eventReceivedListenerArray53);
        org.junit.Assert.assertNotNull(eventReceivedListenerArray54);
    }

    @Test
    public void test507() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test507");
        org.ccsds.moims.mo.mal.structures.StringList stringList0 = new org.ccsds.moims.mo.mal.structures.StringList();
        java.lang.Long long1 = stringList0.getShortForm();
        java.lang.Long long2 = stringList0.getShortForm();
        java.lang.Long[] longArray8 = new java.lang.Long[]{0L, 1L, 10L, 10L, 100L};
        java.util.ArrayList<java.lang.Long> longList9 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList9, longArray8);
        longList9.trimToSize();
        java.util.stream.Stream<java.lang.Long> longStream12 = longList9.stream();
        boolean boolean13 = stringList0.equals((java.lang.Object) longList9);
        java.lang.String str14 = longList9.toString();
        java.util.ListIterator<java.lang.Long> longItor15 = longList9.listIterator();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl16 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener17 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl16);
        java.lang.Long long18 = nanoSatMOConnectorImpl16.getAppDirectoryId();
        boolean boolean19 = longList9.remove((java.lang.Object) nanoSatMOConnectorImpl16);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265073L + "'", long1.equals(281475010265073L));
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265073L + "'", long2.equals(281475010265073L));
        org.junit.Assert.assertNotNull(longArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertNotNull(longStream12);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
        org.junit.Assert.assertTrue("'" + str14 + "' != '" + "[0, 1, 10, 10, 100]" + "'", str14.equals(
            "[0, 1, 10, 10, 100]"));
        org.junit.Assert.assertNotNull(longItor15);
        org.junit.Assert.assertNull(long18);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
    }

    @Test
    public void test508() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test508");
        java.lang.Short[] shortArray3 = new java.lang.Short[]{(short) 1, (short) 100, (short) 100};
        java.util.ArrayList<java.lang.Short> shortList4 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList4,
            shortArray3);
        boolean boolean7 = shortList4.equals((java.lang.Object) "hi!");
        java.lang.String[] strArray9 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList10 = new java.util.ArrayList<java.lang.String>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList10, strArray9);
        org.ccsds.moims.mo.mal.structures.UShort[] uShortArray12 = new org.ccsds.moims.mo.mal.structures.UShort[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList13 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
        boolean boolean14 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList13, uShortArray12);
        int int16 = uShortList13.indexOf((java.lang.Object) 1.0f);
        boolean boolean17 = strList10.containsAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList13);
        boolean boolean18 = shortList4.containsAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList13);
        shortList4.clear();
        boolean boolean21 = shortList4.add((java.lang.Short) (short) 0);
        int int22 = shortList4.size();
        int int23 = shortList4.size();
        java.lang.Object[] objArray24 = shortList4.toArray();
        java.lang.String[] strArray46 = new java.lang.String[]{"[-1, 281475010265070]", "hi!",
                                                               "[-1, 0, 1, 10, 10, 100]", "[, ]",
                                                               "[10.0, 0.0, -1.0, -1.0]", "-1", "0.0",
                                                               "18446744073709551615", "[, ]", "[-1, 10, 1]", "65535",
                                                               "[-1.0, 10.0, 10.0, 1.0]", "hi!", "[, ]", "65535", "",
                                                               "[0.0, 0.0, 0.0, 10.0]", "[, ]", "[]", "[0, 0]",
                                                               "[-1, 281475010265070, 281474993487873]"};
        java.util.ArrayList<java.lang.String> strList47 = new java.util.ArrayList<java.lang.String>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList47,
            strArray46);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList50 = new org.ccsds.moims.mo.mal.structures.UShortList(
            (int) ' ');
        org.ccsds.moims.mo.mal.structures.UShort uShort51 = uShortList50.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet52 = uShortList50.getAreaVersion();
        java.lang.Long long53 = uShortList50.getShortForm();
        boolean boolean54 = strList47.retainAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList50);
        org.ccsds.moims.mo.mal.structures.UShort uShort55 = uShortList50.getServiceNumber();
        boolean boolean56 = shortList4.retainAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList50);
        java.lang.Byte[] byteArray59 = new java.lang.Byte[]{(byte) 100, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList60 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean61 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList60,
            byteArray59);
        java.util.ListIterator<java.lang.Byte> byteItor63 = byteList60.listIterator((int) (short) 1);
        int int64 = byteList60.size();
        java.util.Iterator<java.lang.Byte> byteItor65 = byteList60.iterator();
        java.util.Iterator<java.lang.Byte> byteItor66 = byteList60.iterator();
        java.lang.Byte byte68 = byteList60.get(1);
        java.util.ListIterator<java.lang.Byte> byteItor69 = byteList60.listIterator();
        org.ccsds.moims.mo.mal.structures.LongList longList70 = new org.ccsds.moims.mo.mal.structures.LongList();
        org.ccsds.moims.mo.mal.structures.UShort uShort71 = longList70.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort72 = longList70.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort73 = longList70.getAreaNumber();
        longList70.ensureCapacity(14);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet76 = longList70.getAreaVersion();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl77 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long78 = nanoSatMOConnectorImpl77.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener79 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl77);
        nanoSatMOConnectorImpl77.initAdditionalServices();
        java.lang.Long long81 = nanoSatMOConnectorImpl77.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener82 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl77);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl83 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener84 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl83);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl85 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long86 = nanoSatMOConnectorImpl85.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener87 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl85);
        nanoSatMOConnectorImpl85.initAdditionalServices();
        nanoSatMOConnectorImpl85.initAdditionalServices();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl90 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener91 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl90);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl92 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener93 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl92);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener94 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl92);
        java.lang.Long long95 = nanoSatMOConnectorImpl92.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[] nanoSatMOConnectorImplArray96 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[]{nanoSatMOConnectorImpl77,
                                                                                                                                                          nanoSatMOConnectorImpl83,
                                                                                                                                                          nanoSatMOConnectorImpl85,
                                                                                                                                                          nanoSatMOConnectorImpl90,
                                                                                                                                                          nanoSatMOConnectorImpl92};
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[] nanoSatMOConnectorImplArray97 = longList70.toArray(
            nanoSatMOConnectorImplArray96);
        int int98 = byteList60.lastIndexOf((java.lang.Object) nanoSatMOConnectorImplArray96);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[] nanoSatMOConnectorImplArray99 = uShortList50.toArray(
            nanoSatMOConnectorImplArray96);
        org.junit.Assert.assertNotNull(shortArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(strArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertNotNull(uShortArray12);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertTrue("'" + int16 + "' != '" + (-1) + "'", int16 == (-1));
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + 1 + "'", int22 == 1);
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + 1 + "'", int23 == 1);
        org.junit.Assert.assertNotNull(objArray24);
        org.junit.Assert.assertNotNull(strArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(uShort51);
        org.junit.Assert.assertNotNull(uOctet52);
        org.junit.Assert.assertTrue("'" + long53 + "' != '" + 281475010265078L + "'", long53.equals(281475010265078L));
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
        org.junit.Assert.assertNotNull(uShort55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
        org.junit.Assert.assertNotNull(byteArray59);
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + true + "'", boolean61);
        org.junit.Assert.assertNotNull(byteItor63);
        org.junit.Assert.assertTrue("'" + int64 + "' != '" + 2 + "'", int64 == 2);
        org.junit.Assert.assertNotNull(byteItor65);
        org.junit.Assert.assertNotNull(byteItor66);
        org.junit.Assert.assertTrue("'" + byte68 + "' != '" + (byte) 100 + "'", byte68.equals((byte) 100));
        org.junit.Assert.assertNotNull(byteItor69);
        org.junit.Assert.assertNotNull(uShort71);
        org.junit.Assert.assertNotNull(uShort72);
        org.junit.Assert.assertNotNull(uShort73);
        org.junit.Assert.assertNotNull(uOctet76);
        org.junit.Assert.assertNull(long78);
        org.junit.Assert.assertNull(long81);
        org.junit.Assert.assertNull(long86);
        org.junit.Assert.assertNull(long95);
        org.junit.Assert.assertNotNull(nanoSatMOConnectorImplArray96);
        org.junit.Assert.assertNotNull(nanoSatMOConnectorImplArray97);
        org.junit.Assert.assertTrue("'" + int98 + "' != '" + (-1) + "'", int98 == (-1));
        org.junit.Assert.assertNotNull(nanoSatMOConnectorImplArray99);
    }

    @Test
    public void test509() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test509");
        java.lang.Short[] shortArray3 = new java.lang.Short[]{(short) 1, (short) 100, (short) 100};
        java.util.ArrayList<java.lang.Short> shortList4 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList4,
            shortArray3);
        boolean boolean7 = shortList4.equals((java.lang.Object) "hi!");
        java.lang.Long[] longArray13 = new java.lang.Long[]{0L, 1L, 10L, 10L, 100L};
        java.util.ArrayList<java.lang.Long> longList14 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList14,
            longArray13);
        longList14.trimToSize();
        java.lang.Object[] objArray17 = longList14.toArray();
        org.ccsds.moims.mo.mal.structures.UShort[] uShortArray18 = new org.ccsds.moims.mo.mal.structures.UShort[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort> uShortList19 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.UShort>();
        boolean boolean20 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList19, uShortArray18);
        int int22 = uShortList19.lastIndexOf((java.lang.Object) 'a');
        boolean boolean23 = uShortList19.isEmpty();
        uShortList19.clear();
        uShortList19.trimToSize();
        boolean boolean26 = longList14.remove((java.lang.Object) uShortList19);
        boolean boolean27 = shortList4.retainAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.UShort>) uShortList19);
        org.ccsds.moims.mo.mal.structures.LongList longList28 = new org.ccsds.moims.mo.mal.structures.LongList();
        org.ccsds.moims.mo.mal.structures.UShort uShort29 = longList28.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort30 = longList28.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort31 = longList28.getAreaNumber();
        longList28.ensureCapacity(14);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet34 = longList28.getAreaVersion();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl35 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long36 = nanoSatMOConnectorImpl35.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener37 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl35);
        nanoSatMOConnectorImpl35.initAdditionalServices();
        java.lang.Long long39 = nanoSatMOConnectorImpl35.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener40 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl35);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl41 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener42 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl41);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl43 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        java.lang.Long long44 = nanoSatMOConnectorImpl43.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener45 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl43);
        nanoSatMOConnectorImpl43.initAdditionalServices();
        nanoSatMOConnectorImpl43.initAdditionalServices();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl48 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener49 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl48);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl nanoSatMOConnectorImpl50 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl();
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener51 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl50);
        esa.mo.nmf.nanosatmoconnector.CloseAppEventListener closeAppEventListener52 = new esa.mo.nmf.nanosatmoconnector.CloseAppEventListener(
            nanoSatMOConnectorImpl50);
        java.lang.Long long53 = nanoSatMOConnectorImpl50.getAppDirectoryId();
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[] nanoSatMOConnectorImplArray54 = new esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[]{nanoSatMOConnectorImpl35,
                                                                                                                                                          nanoSatMOConnectorImpl41,
                                                                                                                                                          nanoSatMOConnectorImpl43,
                                                                                                                                                          nanoSatMOConnectorImpl48,
                                                                                                                                                          nanoSatMOConnectorImpl50};
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[] nanoSatMOConnectorImplArray55 = longList28.toArray(
            nanoSatMOConnectorImplArray54);
        esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl[] nanoSatMOConnectorImplArray56 = shortList4.toArray(
            nanoSatMOConnectorImplArray54);
        org.junit.Assert.assertNotNull(shortArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(longArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertNotNull(objArray17);
        org.junit.Assert.assertNotNull(uShortArray18);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-1) + "'", int22 == (-1));
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(uShort29);
        org.junit.Assert.assertNotNull(uShort30);
        org.junit.Assert.assertNotNull(uShort31);
        org.junit.Assert.assertNotNull(uOctet34);
        org.junit.Assert.assertNull(long36);
        org.junit.Assert.assertNull(long39);
        org.junit.Assert.assertNull(long44);
        org.junit.Assert.assertNull(long53);
        org.junit.Assert.assertNotNull(nanoSatMOConnectorImplArray54);
        org.junit.Assert.assertNotNull(nanoSatMOConnectorImplArray55);
        org.junit.Assert.assertNotNull(nanoSatMOConnectorImplArray56);
    }
}
