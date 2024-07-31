package esa.nmf.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest0 {

    public static boolean debug = false;

    @Test
    public void test1() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test1");
        esa.mo.common.impl.util.HelperCommon helperCommon0 = new esa.mo.common.impl.util.HelperCommon();
    }

    @Test
    public void test2() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test2");
        java.lang.Float[] floatArray5 = new java.lang.Float[]{100.0f, 1.0f, 0.0f, 0.0f, 10.0f};
        java.util.ArrayList<java.lang.Float> floatList6 = new java.util.ArrayList<java.lang.Float>();
        boolean boolean7 = java.util.Collections.addAll((java.util.Collection<java.lang.Float>) floatList6,
            floatArray5);
        java.util.ListIterator<java.lang.Float> floatItor8 = floatList6.listIterator();
        java.util.Spliterator<java.lang.Float> floatSpliterator9 = floatList6.spliterator();
        floatList6.clear();
        java.lang.Object[] objArray11 = floatList6.toArray();
        java.lang.Object[] objArray12 = floatList6.toArray();
        java.util.Spliterator<java.lang.Float> floatSpliterator13 = floatList6.spliterator();
        java.lang.Short[] shortArray16 = new java.lang.Short[]{(short) -1, (short) 1};
        java.util.ArrayList<java.lang.Short> shortList17 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean18 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList17,
            shortArray16);
        java.lang.String[] strArray21 = new java.lang.String[]{"hi!", "hi!"};
        java.util.ArrayList<java.lang.String> strList22 = new java.util.ArrayList<java.lang.String>();
        boolean boolean23 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList22,
            strArray21);
        boolean boolean24 = shortList17.removeAll((java.util.Collection<java.lang.String>) strList22);
        java.lang.String[] strArray27 = new java.lang.String[]{"", "hi!"};
        java.util.ArrayList<java.lang.String> strList28 = new java.util.ArrayList<java.lang.String>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList28,
            strArray27);
        boolean boolean31 = strList28.add("hi!");
        java.lang.String[] strArray35 = new java.lang.String[]{"", "hi!"};
        java.util.ArrayList<java.lang.String> strList36 = new java.util.ArrayList<java.lang.String>();
        boolean boolean37 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList36,
            strArray35);
        boolean boolean38 = strList28.addAll(0, (java.util.Collection<java.lang.String>) strList36);
        java.lang.String[] strArray41 = new java.lang.String[]{"", "hi!"};
        java.util.ArrayList<java.lang.String> strList42 = new java.util.ArrayList<java.lang.String>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList42,
            strArray41);
        boolean boolean45 = strList42.add("hi!");
        java.util.stream.Stream<java.lang.String> strStream46 = strList42.parallelStream();
        boolean boolean47 = strList28.addAll((java.util.Collection<java.lang.String>) strList42);
        boolean boolean48 = shortList17.containsAll((java.util.Collection<java.lang.String>) strList28);
        int int49 = shortList17.size();
        java.lang.Byte[] byteArray55 = new java.lang.Byte[]{(byte) 10, (byte) 0, (byte) 0, (byte) 10, (byte) -1};
        java.util.ArrayList<java.lang.Byte> byteList56 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean57 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList56,
            byteArray55);
        int int59 = byteList56.lastIndexOf((java.lang.Object) (-1.0f));
        java.util.stream.Stream<java.lang.Byte> byteStream60 = byteList56.stream();
        java.lang.String[] strArray63 = new java.lang.String[]{"", "hi!"};
        java.util.ArrayList<java.lang.String> strList64 = new java.util.ArrayList<java.lang.String>();
        boolean boolean65 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList64,
            strArray63);
        boolean boolean67 = strList64.add("hi!");
        boolean boolean68 = byteList56.retainAll((java.util.Collection<java.lang.String>) strList64);
        java.lang.String str69 = byteList56.toString();
        boolean boolean70 = shortList17.equals((java.lang.Object) byteList56);
        java.lang.Object obj71 = byteList56.clone();
        java.lang.Object[] objArray72 = byteList56.toArray();
        byteList56.ensureCapacity(11);
        esa.mo.common.impl.util.HelperCommon helperCommon75 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon76 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon77 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon78 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray79 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon75,
                                                                                                                helperCommon76,
                                                                                                                helperCommon77,
                                                                                                                helperCommon78};
        esa.mo.common.impl.util.HelperCommon helperCommon80 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon81 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon82 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon83 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray84 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon80,
                                                                                                                helperCommon81,
                                                                                                                helperCommon82,
                                                                                                                helperCommon83};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray85 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray79,
                                                                                                                    helperCommonArray84};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray86 = byteList56.toArray(helperCommonArray85);
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray87 = floatList6.toArray(helperCommonArray86);
        org.junit.Assert.assertNotNull(floatArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
        org.junit.Assert.assertNotNull(floatItor8);
        org.junit.Assert.assertNotNull(floatSpliterator9);
        org.junit.Assert.assertNotNull(objArray11);
        org.junit.Assert.assertNotNull(objArray12);
        org.junit.Assert.assertNotNull(floatSpliterator13);
        org.junit.Assert.assertNotNull(shortArray16);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertNotNull(strArray21);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(strArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertNotNull(strArray35);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertNotNull(strArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
        org.junit.Assert.assertNotNull(strStream46);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertTrue("'" + int49 + "' != '" + 2 + "'", int49 == 2);
        org.junit.Assert.assertNotNull(byteArray55);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertTrue("'" + int59 + "' != '" + (-1) + "'", int59 == (-1));
        org.junit.Assert.assertNotNull(byteStream60);
        org.junit.Assert.assertNotNull(strArray63);
        org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + true + "'", boolean65);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + true + "'", boolean68);
        org.junit.Assert.assertTrue("'" + str69 + "' != '" + "[]" + "'", str69.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + false + "'", !boolean70);
        org.junit.Assert.assertNotNull(obj71);
        org.junit.Assert.assertNotNull(objArray72);
        org.junit.Assert.assertNotNull(helperCommonArray79);
        org.junit.Assert.assertNotNull(helperCommonArray84);
        org.junit.Assert.assertNotNull(helperCommonArray85);
        org.junit.Assert.assertNotNull(helperCommonArray86);
        org.junit.Assert.assertNotNull(helperCommonArray87);
    }

    @Test
    public void test3() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test3");
        org.ccsds.moims.mo.mal.structures.IntegerList integerList1 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) (byte) 0);
        java.lang.String str2 = integerList1.toString();
        esa.mo.common.impl.util.HelperCommon helperCommon3 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon4 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon5 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon6 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray7 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon3,
                                                                                                               helperCommon4,
                                                                                                               helperCommon5,
                                                                                                               helperCommon6};
        esa.mo.common.impl.util.HelperCommon helperCommon8 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon9 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon10 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon11 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray12 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon8,
                                                                                                                helperCommon9,
                                                                                                                helperCommon10,
                                                                                                                helperCommon11};
        esa.mo.common.impl.util.HelperCommon helperCommon13 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon14 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon15 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon16 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray17 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon13,
                                                                                                                helperCommon14,
                                                                                                                helperCommon15,
                                                                                                                helperCommon16};
        esa.mo.common.impl.util.HelperCommon helperCommon18 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon19 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon20 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon21 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray22 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon18,
                                                                                                                helperCommon19,
                                                                                                                helperCommon20,
                                                                                                                helperCommon21};
        esa.mo.common.impl.util.HelperCommon helperCommon23 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon24 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon25 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon26 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray27 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon23,
                                                                                                                helperCommon24,
                                                                                                                helperCommon25,
                                                                                                                helperCommon26};
        esa.mo.common.impl.util.HelperCommon helperCommon28 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon29 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon30 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon31 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray32 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon28,
                                                                                                                helperCommon29,
                                                                                                                helperCommon30,
                                                                                                                helperCommon31};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray33 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray7,
                                                                                                                    helperCommonArray12,
                                                                                                                    helperCommonArray17,
                                                                                                                    helperCommonArray22,
                                                                                                                    helperCommonArray27,
                                                                                                                    helperCommonArray32};
        esa.mo.common.impl.util.HelperCommon helperCommon34 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon35 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon36 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon37 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray38 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon34,
                                                                                                                helperCommon35,
                                                                                                                helperCommon36,
                                                                                                                helperCommon37};
        esa.mo.common.impl.util.HelperCommon helperCommon39 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon40 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon41 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon42 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray43 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon39,
                                                                                                                helperCommon40,
                                                                                                                helperCommon41,
                                                                                                                helperCommon42};
        esa.mo.common.impl.util.HelperCommon helperCommon44 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon45 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon46 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon47 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray48 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon44,
                                                                                                                helperCommon45,
                                                                                                                helperCommon46,
                                                                                                                helperCommon47};
        esa.mo.common.impl.util.HelperCommon helperCommon49 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon50 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon51 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon52 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray53 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon49,
                                                                                                                helperCommon50,
                                                                                                                helperCommon51,
                                                                                                                helperCommon52};
        esa.mo.common.impl.util.HelperCommon helperCommon54 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon55 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon56 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon57 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray58 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon54,
                                                                                                                helperCommon55,
                                                                                                                helperCommon56,
                                                                                                                helperCommon57};
        esa.mo.common.impl.util.HelperCommon helperCommon59 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon60 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon61 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon62 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray63 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon59,
                                                                                                                helperCommon60,
                                                                                                                helperCommon61,
                                                                                                                helperCommon62};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray64 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray38,
                                                                                                                    helperCommonArray43,
                                                                                                                    helperCommonArray48,
                                                                                                                    helperCommonArray53,
                                                                                                                    helperCommonArray58,
                                                                                                                    helperCommonArray63};
        esa.mo.common.impl.util.HelperCommon[][][] helperCommonArray65 = new esa.mo.common.impl.util.HelperCommon[][][]{helperCommonArray33,
                                                                                                                        helperCommonArray64};
        esa.mo.common.impl.util.HelperCommon[][][] helperCommonArray66 = integerList1.toArray(helperCommonArray65);
        boolean boolean68 = integerList1.add((java.lang.Integer) (-5));
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "[]" + "'", str2.equals("[]"));
        org.junit.Assert.assertNotNull(helperCommonArray7);
        org.junit.Assert.assertNotNull(helperCommonArray12);
        org.junit.Assert.assertNotNull(helperCommonArray17);
        org.junit.Assert.assertNotNull(helperCommonArray22);
        org.junit.Assert.assertNotNull(helperCommonArray27);
        org.junit.Assert.assertNotNull(helperCommonArray32);
        org.junit.Assert.assertNotNull(helperCommonArray33);
        org.junit.Assert.assertNotNull(helperCommonArray38);
        org.junit.Assert.assertNotNull(helperCommonArray43);
        org.junit.Assert.assertNotNull(helperCommonArray48);
        org.junit.Assert.assertNotNull(helperCommonArray53);
        org.junit.Assert.assertNotNull(helperCommonArray58);
        org.junit.Assert.assertNotNull(helperCommonArray63);
        org.junit.Assert.assertNotNull(helperCommonArray64);
        org.junit.Assert.assertNotNull(helperCommonArray65);
        org.junit.Assert.assertNotNull(helperCommonArray66);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + true + "'", boolean68);
    }

    @Test
    public void test4() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test4");
        java.lang.Byte[] byteArray5 = new java.lang.Byte[]{(byte) 10, (byte) 0, (byte) 0, (byte) 10, (byte) -1};
        java.util.ArrayList<java.lang.Byte> byteList6 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean7 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList6, byteArray5);
        int int9 = byteList6.lastIndexOf((java.lang.Object) (-1.0f));
        java.lang.String[] strArray12 = new java.lang.String[]{"", "hi!"};
        java.util.ArrayList<java.lang.String> strList13 = new java.util.ArrayList<java.lang.String>();
        boolean boolean14 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList13,
            strArray12);
        boolean boolean16 = strList13.add("hi!");
        java.lang.String[] strArray20 = new java.lang.String[]{"", "hi!"};
        java.util.ArrayList<java.lang.String> strList21 = new java.util.ArrayList<java.lang.String>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList21,
            strArray20);
        boolean boolean23 = strList13.addAll(0, (java.util.Collection<java.lang.String>) strList21);
        boolean boolean24 = strList13.isEmpty();
        boolean boolean25 = byteList6.removeAll((java.util.Collection<java.lang.String>) strList13);
        byteList6.clear();
        java.lang.Object obj27 = byteList6.clone();
        java.util.ListIterator<java.lang.Byte> byteItor28 = byteList6.listIterator();
        esa.mo.common.impl.util.HelperCommon helperCommon29 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray30 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon29};
        esa.mo.common.impl.util.HelperCommon helperCommon31 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray32 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon31};
        esa.mo.common.impl.util.HelperCommon helperCommon33 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray34 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon33};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray35 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray30,
                                                                                                                    helperCommonArray32,
                                                                                                                    helperCommonArray34};
        esa.mo.common.impl.util.HelperCommon[][][] helperCommonArray36 = new esa.mo.common.impl.util.HelperCommon[][][]{helperCommonArray35};
        esa.mo.common.impl.util.HelperCommon helperCommon37 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray38 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon37};
        esa.mo.common.impl.util.HelperCommon helperCommon39 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray40 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon39};
        esa.mo.common.impl.util.HelperCommon helperCommon41 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray42 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon41};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray43 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray38,
                                                                                                                    helperCommonArray40,
                                                                                                                    helperCommonArray42};
        esa.mo.common.impl.util.HelperCommon[][][] helperCommonArray44 = new esa.mo.common.impl.util.HelperCommon[][][]{helperCommonArray43};
        esa.mo.common.impl.util.HelperCommon[][][][] helperCommonArray45 = new esa.mo.common.impl.util.HelperCommon[][][][]{helperCommonArray36,
                                                                                                                            helperCommonArray44};
        esa.mo.common.impl.util.HelperCommon[][][][] helperCommonArray46 = byteList6.toArray(helperCommonArray45);
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-1) + "'", int9 == (-1));
        org.junit.Assert.assertNotNull(strArray12);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertNotNull(strArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
        org.junit.Assert.assertNotNull(obj27);
        org.junit.Assert.assertNotNull(byteItor28);
        org.junit.Assert.assertNotNull(helperCommonArray30);
        org.junit.Assert.assertNotNull(helperCommonArray32);
        org.junit.Assert.assertNotNull(helperCommonArray34);
        org.junit.Assert.assertNotNull(helperCommonArray35);
        org.junit.Assert.assertNotNull(helperCommonArray36);
        org.junit.Assert.assertNotNull(helperCommonArray38);
        org.junit.Assert.assertNotNull(helperCommonArray40);
        org.junit.Assert.assertNotNull(helperCommonArray42);
        org.junit.Assert.assertNotNull(helperCommonArray43);
        org.junit.Assert.assertNotNull(helperCommonArray44);
        org.junit.Assert.assertNotNull(helperCommonArray45);
        org.junit.Assert.assertNotNull(helperCommonArray46);
    }

    @Test
    public void test5() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test5");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        floatList0.trimToSize();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = org.ccsds.moims.mo.mal.structures.FloatList.AREA_SHORT_FORM;
        java.lang.Integer int3 = uShort2.getTypeShortForm();
        int int4 = floatList0.lastIndexOf((java.lang.Object) uShort2);
        org.ccsds.moims.mo.mal.structures.UShort uShort5 = floatList0.getServiceNumber();
        floatList0.trimToSize();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet7 = floatList0.getAreaVersion();
        java.util.ListIterator<java.lang.Float> floatItor8 = floatList0.listIterator();
        org.ccsds.moims.mo.mal.structures.ULong uLong9 = new org.ccsds.moims.mo.mal.structures.ULong();
        org.ccsds.moims.mo.mal.structures.ShortList shortList10 = new org.ccsds.moims.mo.mal.structures.ShortList();
        boolean boolean11 = uLong9.equals((java.lang.Object) shortList10);
        org.ccsds.moims.mo.mal.structures.UShort uShort12 = shortList10.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort13 = shortList10.getServiceNumber();
        java.util.Iterator<java.lang.Short> shortItor14 = shortList10.iterator();
        java.util.stream.Stream<java.lang.Short> shortStream15 = shortList10.stream();
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray16 = new org.ccsds.moims.mo.mal.structures.BooleanList[]{};
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray17 = shortList10.toArray(booleanListArray16);
        java.util.Spliterator<java.lang.Short> shortSpliterator18 = shortList10.spliterator();
        java.lang.String[] strArray21 = new java.lang.String[]{"", "hi!"};
        java.util.ArrayList<java.lang.String> strList22 = new java.util.ArrayList<java.lang.String>();
        boolean boolean23 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList22,
            strArray21);
        boolean boolean25 = strList22.add("hi!");
        java.lang.String[] strArray29 = new java.lang.String[]{"", "hi!"};
        java.util.ArrayList<java.lang.String> strList30 = new java.util.ArrayList<java.lang.String>();
        boolean boolean31 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList30,
            strArray29);
        boolean boolean32 = strList22.addAll(0, (java.util.Collection<java.lang.String>) strList30);
        java.util.Iterator<java.lang.String> strItor33 = strList22.iterator();
        boolean boolean34 = shortList10.removeAll((java.util.Collection<java.lang.String>) strList22);
        esa.mo.common.impl.util.HelperCommon helperCommon35 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon36 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon37 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon38 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray39 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon35,
                                                                                                                helperCommon36,
                                                                                                                helperCommon37,
                                                                                                                helperCommon38};
        esa.mo.common.impl.util.HelperCommon helperCommon40 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon41 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon42 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon43 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray44 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon40,
                                                                                                                helperCommon41,
                                                                                                                helperCommon42,
                                                                                                                helperCommon43};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray45 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray39,
                                                                                                                    helperCommonArray44};
        esa.mo.common.impl.util.HelperCommon helperCommon46 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon47 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon48 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon49 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray50 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon46,
                                                                                                                helperCommon47,
                                                                                                                helperCommon48,
                                                                                                                helperCommon49};
        esa.mo.common.impl.util.HelperCommon helperCommon51 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon52 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon53 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon54 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray55 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon51,
                                                                                                                helperCommon52,
                                                                                                                helperCommon53,
                                                                                                                helperCommon54};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray56 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray50,
                                                                                                                    helperCommonArray55};
        esa.mo.common.impl.util.HelperCommon helperCommon57 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon58 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon59 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon60 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray61 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon57,
                                                                                                                helperCommon58,
                                                                                                                helperCommon59,
                                                                                                                helperCommon60};
        esa.mo.common.impl.util.HelperCommon helperCommon62 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon63 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon64 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon65 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray66 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon62,
                                                                                                                helperCommon63,
                                                                                                                helperCommon64,
                                                                                                                helperCommon65};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray67 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray61,
                                                                                                                    helperCommonArray66};
        esa.mo.common.impl.util.HelperCommon helperCommon68 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon69 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon70 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon71 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray72 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon68,
                                                                                                                helperCommon69,
                                                                                                                helperCommon70,
                                                                                                                helperCommon71};
        esa.mo.common.impl.util.HelperCommon helperCommon73 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon74 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon75 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon76 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray77 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon73,
                                                                                                                helperCommon74,
                                                                                                                helperCommon75,
                                                                                                                helperCommon76};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray78 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray72,
                                                                                                                    helperCommonArray77};
        esa.mo.common.impl.util.HelperCommon helperCommon79 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon80 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon81 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon82 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray83 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon79,
                                                                                                                helperCommon80,
                                                                                                                helperCommon81,
                                                                                                                helperCommon82};
        esa.mo.common.impl.util.HelperCommon helperCommon84 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon85 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon86 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon helperCommon87 = new esa.mo.common.impl.util.HelperCommon();
        esa.mo.common.impl.util.HelperCommon[] helperCommonArray88 = new esa.mo.common.impl.util.HelperCommon[]{helperCommon84,
                                                                                                                helperCommon85,
                                                                                                                helperCommon86,
                                                                                                                helperCommon87};
        esa.mo.common.impl.util.HelperCommon[][] helperCommonArray89 = new esa.mo.common.impl.util.HelperCommon[][]{helperCommonArray83,
                                                                                                                    helperCommonArray88};
        esa.mo.common.impl.util.HelperCommon[][][] helperCommonArray90 = new esa.mo.common.impl.util.HelperCommon[][][]{helperCommonArray45,
                                                                                                                        helperCommonArray56,
                                                                                                                        helperCommonArray67,
                                                                                                                        helperCommonArray78,
                                                                                                                        helperCommonArray89};
        esa.mo.common.impl.util.HelperCommon[][][] helperCommonArray91 = shortList10.toArray(helperCommonArray90);
        esa.mo.common.impl.util.HelperCommon[][][] helperCommonArray92 = floatList0.toArray(helperCommonArray90);
        floatList0.ensureCapacity((int) (short) 10);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 10 + "'", int3.equals(10));
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + (-1) + "'", int4 == (-1));
        org.junit.Assert.assertNotNull(uShort5);
        org.junit.Assert.assertNotNull(uOctet7);
        org.junit.Assert.assertNotNull(floatItor8);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertNotNull(uShort12);
        org.junit.Assert.assertNotNull(uShort13);
        org.junit.Assert.assertNotNull(shortItor14);
        org.junit.Assert.assertNotNull(shortStream15);
        org.junit.Assert.assertNotNull(booleanListArray16);
        org.junit.Assert.assertNotNull(booleanListArray17);
        org.junit.Assert.assertNotNull(shortSpliterator18);
        org.junit.Assert.assertNotNull(strArray21);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + true + "'", boolean23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertNotNull(strArray29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertNotNull(strItor33);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertNotNull(helperCommonArray39);
        org.junit.Assert.assertNotNull(helperCommonArray44);
        org.junit.Assert.assertNotNull(helperCommonArray45);
        org.junit.Assert.assertNotNull(helperCommonArray50);
        org.junit.Assert.assertNotNull(helperCommonArray55);
        org.junit.Assert.assertNotNull(helperCommonArray56);
        org.junit.Assert.assertNotNull(helperCommonArray61);
        org.junit.Assert.assertNotNull(helperCommonArray66);
        org.junit.Assert.assertNotNull(helperCommonArray67);
        org.junit.Assert.assertNotNull(helperCommonArray72);
        org.junit.Assert.assertNotNull(helperCommonArray77);
        org.junit.Assert.assertNotNull(helperCommonArray78);
        org.junit.Assert.assertNotNull(helperCommonArray83);
        org.junit.Assert.assertNotNull(helperCommonArray88);
        org.junit.Assert.assertNotNull(helperCommonArray89);
        org.junit.Assert.assertNotNull(helperCommonArray90);
        org.junit.Assert.assertNotNull(helperCommonArray91);
        org.junit.Assert.assertNotNull(helperCommonArray92);
    }
}
