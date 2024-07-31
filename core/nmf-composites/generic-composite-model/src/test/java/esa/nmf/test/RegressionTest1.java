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
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl2 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl3 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl4 = mCServicesProviderNMF0
            .getParameterService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl2);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl3);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl4);
    }

    @Test
    public void test502() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test502");
        esa.mo.nmf.NMFException nMFException1 = new esa.mo.nmf.NMFException("-1");
        esa.mo.nmf.NMFException nMFException4 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray5 = nMFException4.getSuppressed();
        java.lang.Throwable[] throwableArray6 = nMFException4.getSuppressed();
        esa.mo.nmf.NMFException nMFException7 = new esa.mo.nmf.NMFException("[hi!, hi!]",
            (java.lang.Throwable) nMFException4);
        java.lang.String str8 = nMFException4.toString();
        nMFException1.addSuppressed((java.lang.Throwable) nMFException4);
        esa.mo.nmf.NMFException nMFException11 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray12 = nMFException11.getSuppressed();
        java.lang.Throwable[] throwableArray13 = nMFException11.getSuppressed();
        nMFException1.addSuppressed((java.lang.Throwable) nMFException11);
        java.lang.String str15 = nMFException1.toString();
        java.lang.String str16 = nMFException1.toString();
        org.junit.Assert.assertNotNull(throwableArray5);
        org.junit.Assert.assertNotNull(throwableArray6);
        org.junit.Assert.assertTrue("'" + str8 + "' != '" + "esa.mo.nmf.NMFException: 0" + "'", str8.equals(
            "esa.mo.nmf.NMFException: 0"));
        org.junit.Assert.assertNotNull(throwableArray12);
        org.junit.Assert.assertNotNull(throwableArray13);
        org.junit.Assert.assertTrue("'" + str15 + "' != '" + "esa.mo.nmf.NMFException: -1" + "'", str15.equals(
            "esa.mo.nmf.NMFException: -1"));
        org.junit.Assert.assertTrue("'" + str16 + "' != '" + "esa.mo.nmf.NMFException: -1" + "'", str16.equals(
            "esa.mo.nmf.NMFException: -1"));
    }

    @Test
    public void test503() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test503");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj1 = shortList0.clone();
        java.lang.Long long2 = shortList0.getShortForm();
        java.lang.Object[] objArray3 = shortList0.toArray();
        shortList0.trimToSize();
        boolean boolean5 = shortList0.isEmpty();
        java.util.stream.Stream<java.lang.Short> shortStream6 = shortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.ShortList shortList7 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray9 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList10 = new java.util.ArrayList<java.lang.String>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList10, strArray9);
        int int12 = strList10.size();
        java.lang.Boolean[] booleanArray15 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList16 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean17 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList16,
            booleanArray15);
        boolean boolean19 = booleanList16.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream20 = booleanList16.stream();
        boolean boolean21 = strList10.containsAll((java.util.Collection<java.lang.Boolean>) booleanList16);
        boolean boolean22 = shortList7.containsAll((java.util.Collection<java.lang.Boolean>) booleanList16);
        java.util.stream.Stream<java.lang.Short> shortStream23 = shortList7.parallelStream();
        org.ccsds.moims.mo.mal.structures.Time time25 = new org.ccsds.moims.mo.mal.structures.Time((long) (byte) -1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet26 = time25.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.Time[] timeArray27 = new org.ccsds.moims.mo.mal.structures.Time[]{time25};
        org.ccsds.moims.mo.mal.structures.Time[] timeArray28 = shortList7.toArray(timeArray27);
        org.ccsds.moims.mo.mal.structures.Time[] timeArray29 = shortList0.toArray(timeArray27);
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider30 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF31 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF31
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl33 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl34 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl35 = mCServicesProviderNMF31
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF36 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl37 = mCServicesProviderNMF36
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl38 = mCServicesProviderNMF36
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl39 = mCServicesProviderNMF36
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl40 = mCServicesProviderNMF36
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF41 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF41
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl43 = mCServicesProviderNMF41
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF44 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl45 = mCServicesProviderNMF44
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl46 = mCServicesProviderNMF44
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl47 = mCServicesProviderNMF44
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl48 = mCServicesProviderNMF44
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration49 = new esa.mo.nmf.MCRegistration(cOMServicesProvider30,
            parameterProviderServiceImpl35, aggregationProviderServiceImpl40, alertProviderServiceImpl43,
            actionProviderServiceImpl48);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl50 = mCRegistration49.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl51 = mCRegistration49.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl52 = mCRegistration49.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl53 = mCRegistration49.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl54 = mCRegistration49.aggregationService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl55 = mCRegistration49.aggregationService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl56 = mCRegistration49.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl57 = mCRegistration49.aggregationService;
        boolean boolean58 = shortList0.remove((java.lang.Object) aggregationProviderServiceImpl57);
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265079L + "'", long2.equals(281475010265079L));
        org.junit.Assert.assertNotNull(objArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertNotNull(shortStream6);
        org.junit.Assert.assertNotNull(strArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + int12 + "' != '" + 1 + "'", int12 == 1);
        org.junit.Assert.assertNotNull(booleanArray15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
        org.junit.Assert.assertNotNull(booleanStream20);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertNotNull(shortStream23);
        org.junit.Assert.assertNotNull(uOctet26);
        org.junit.Assert.assertNotNull(timeArray27);
        org.junit.Assert.assertNotNull(timeArray28);
        org.junit.Assert.assertNotNull(timeArray29);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl33);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl34);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl35);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl37);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl38);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl39);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl40);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl43);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl45);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl46);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl47);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl48);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl50);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl51);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl52);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl53);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl54);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl55);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl56);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl57);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
    }

    @Test
    public void test504() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test504");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        org.ccsds.moims.mo.mal.structures.UShort uShort1 = shortList0.getServiceNumber();
        java.lang.String[] strArray6 = new java.lang.String[]{"1", "hi!", "", "1"};
        java.util.ArrayList<java.lang.String> strList7 = new java.util.ArrayList<java.lang.String>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList7, strArray6);
        java.util.ListIterator<java.lang.String> strItor9 = strList7.listIterator();
        java.lang.String[] strArray11 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList12 = new java.util.ArrayList<java.lang.String>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList12,
            strArray11);
        boolean boolean15 = strList12.add("hi!");
        java.lang.Object obj16 = null;
        boolean boolean17 = strList12.equals(obj16);
        java.util.ListIterator<java.lang.String> strItor18 = strList12.listIterator();
        java.lang.String[] strArray20 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList21 = new java.util.ArrayList<java.lang.String>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList21,
            strArray20);
        boolean boolean24 = strList21.add("hi!");
        java.lang.Object obj25 = null;
        boolean boolean26 = strList21.equals(obj25);
        java.util.ListIterator<java.lang.String> strItor27 = strList21.listIterator();
        java.lang.String[] strArray32 = new java.lang.String[]{"1", "hi!", "", "1"};
        java.util.ArrayList<java.lang.String> strList33 = new java.util.ArrayList<java.lang.String>();
        boolean boolean34 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList33,
            strArray32);
        java.util.ListIterator<java.lang.String> strItor35 = strList33.listIterator();
        java.util.ListIterator[] listIteratorArray37 = new java.util.ListIterator[4];
        @SuppressWarnings("unchecked")
        java.util.ListIterator<java.lang.String>[] strItorArray38 = (java.util.ListIterator<java.lang.String>[]) listIteratorArray37;
        strItorArray38[0] = strItor9;
        strItorArray38[1] = strItor18;
        strItorArray38[2] = strItor27;
        strItorArray38[3] = strItor35;
        java.util.ListIterator<java.lang.String>[] strItorArray47 = shortList0.toArray(strItorArray38);
        byte[] byteArray51 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob52 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray51);
        int int53 = blob52.getLength();
        int int54 = blob52.getLength();
        int int55 = blob52.getOffset();
        boolean boolean56 = shortList0.remove((java.lang.Object) blob52);
        java.util.stream.Stream<java.lang.Short> shortStream57 = shortList0.parallelStream();
        esa.mo.nmf.NMFException nMFException59 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray60 = nMFException59.getSuppressed();
        java.lang.Throwable[] throwableArray61 = nMFException59.getSuppressed();
        esa.mo.nmf.NMFException nMFException63 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray64 = nMFException63.getSuppressed();
        nMFException59.addSuppressed((java.lang.Throwable) nMFException63);
        java.lang.Throwable[] throwableArray66 = nMFException63.getSuppressed();
        java.lang.Throwable[] throwableArray67 = nMFException63.getSuppressed();
        esa.mo.nmf.NMFException nMFException68 = new esa.mo.nmf.NMFException();
        nMFException63.addSuppressed((java.lang.Throwable) nMFException68);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList71 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int72 = uShortList71.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor73 = uShortList71.iterator();
        java.lang.Object[] objArray74 = uShortList71.toArray();
        boolean boolean76 = uShortList71.equals((java.lang.Object) 1);
        esa.mo.nmf.NMFException nMFException79 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray80 = nMFException79.getSuppressed();
        java.lang.Throwable[] throwableArray81 = nMFException79.getSuppressed();
        esa.mo.nmf.NMFException nMFException83 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray84 = nMFException83.getSuppressed();
        nMFException79.addSuppressed((java.lang.Throwable) nMFException83);
        java.lang.Throwable[] throwableArray86 = nMFException83.getSuppressed();
        java.lang.Throwable[] throwableArray87 = nMFException83.getSuppressed();
        esa.mo.nmf.NMFException nMFException88 = new esa.mo.nmf.NMFException("[]",
            (java.lang.Throwable) nMFException83);
        int int89 = uShortList71.indexOf((java.lang.Object) nMFException88);
        java.lang.String str90 = nMFException88.toString();
        esa.mo.nmf.NMFException nMFException91 = new esa.mo.nmf.NMFException("[100]",
            (java.lang.Throwable) nMFException88);
        esa.mo.nmf.NMFException[] nMFExceptionArray92 = new esa.mo.nmf.NMFException[]{nMFException68, nMFException91};
        esa.mo.nmf.NMFException[] nMFExceptionArray93 = shortList0.toArray(nMFExceptionArray92);
        java.lang.Integer int94 = shortList0.getTypeShortForm();
        org.junit.Assert.assertNotNull(uShort1);
        org.junit.Assert.assertNotNull(strArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(strItor9);
        org.junit.Assert.assertNotNull(strArray11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
        org.junit.Assert.assertNotNull(strItor18);
        org.junit.Assert.assertNotNull(strArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(strItor27);
        org.junit.Assert.assertNotNull(strArray32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertNotNull(strItor35);
        org.junit.Assert.assertNotNull(listIteratorArray37);
        org.junit.Assert.assertNotNull(strItorArray38);
        org.junit.Assert.assertNotNull(strItorArray47);
        org.junit.Assert.assertNotNull(byteArray51);
        org.junit.Assert.assertTrue("'" + int53 + "' != '" + 3 + "'", int53 == 3);
        org.junit.Assert.assertTrue("'" + int54 + "' != '" + 3 + "'", int54 == 3);
        org.junit.Assert.assertTrue("'" + int55 + "' != '" + 0 + "'", int55 == 0);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertNotNull(shortStream57);
        org.junit.Assert.assertNotNull(throwableArray60);
        org.junit.Assert.assertNotNull(throwableArray61);
        org.junit.Assert.assertNotNull(throwableArray64);
        org.junit.Assert.assertNotNull(throwableArray66);
        org.junit.Assert.assertNotNull(throwableArray67);
        org.junit.Assert.assertTrue("'" + int72 + "' != '" + (-10) + "'", int72.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor73);
        org.junit.Assert.assertNotNull(objArray74);
        org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
        org.junit.Assert.assertNotNull(throwableArray80);
        org.junit.Assert.assertNotNull(throwableArray81);
        org.junit.Assert.assertNotNull(throwableArray84);
        org.junit.Assert.assertNotNull(throwableArray86);
        org.junit.Assert.assertNotNull(throwableArray87);
        org.junit.Assert.assertTrue("'" + int89 + "' != '" + (-1) + "'", int89 == (-1));
        org.junit.Assert.assertTrue("'" + str90 + "' != '" + "esa.mo.nmf.NMFException: []" + "'", str90.equals(
            "esa.mo.nmf.NMFException: []"));
        org.junit.Assert.assertNotNull(nMFExceptionArray92);
        org.junit.Assert.assertNotNull(nMFExceptionArray93);
        org.junit.Assert.assertTrue("'" + int94 + "' != '" + (-9) + "'", int94.equals((-9)));
    }

    @Test
    public void test505() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test505");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl3 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl4 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF0
            .getParameterService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl3);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
    }

    @Test
    public void test506() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test506");
        org.ccsds.moims.mo.mal.structures.StringList stringList0 = new org.ccsds.moims.mo.mal.structures.StringList();
        org.ccsds.moims.mo.mal.structures.Element element1 = stringList0.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = stringList0.getServiceNumber();
        java.lang.Integer int3 = stringList0.getTypeShortForm();
        stringList0.ensureCapacity(13);
        java.util.stream.Stream<java.lang.String> strStream6 = stringList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.StringList stringList8 = new org.ccsds.moims.mo.mal.structures.StringList(
            65535);
        int int9 = stringList0.indexOf((java.lang.Object) stringList8);
        boolean boolean10 = stringList0.isEmpty();
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList11 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Integer int12 = doubleList11.getTypeShortForm();
        java.util.stream.Stream<java.lang.Double> doubleStream13 = doubleList11.stream();
        org.ccsds.moims.mo.mal.structures.FloatList floatList14 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long15 = floatList14.getShortForm();
        java.lang.String[] strArray17 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList18 = new java.util.ArrayList<java.lang.String>();
        boolean boolean19 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList18,
            strArray17);
        int int20 = strList18.size();
        java.lang.Boolean[] booleanArray23 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList24 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean25 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList24,
            booleanArray23);
        boolean boolean27 = booleanList24.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream28 = booleanList24.stream();
        boolean boolean29 = strList18.containsAll((java.util.Collection<java.lang.Boolean>) booleanList24);
        boolean boolean30 = floatList14.containsAll((java.util.Collection<java.lang.Boolean>) booleanList24);
        boolean boolean31 = doubleList11.remove((java.lang.Object) booleanList24);
        boolean boolean32 = stringList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList24);
        org.ccsds.moims.mo.mal.structures.FloatList floatList33 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long34 = floatList33.getShortForm();
        java.lang.String[] strArray36 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList37 = new java.util.ArrayList<java.lang.String>();
        boolean boolean38 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList37,
            strArray36);
        int int39 = strList37.size();
        java.lang.Boolean[] booleanArray42 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList43 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean44 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList43,
            booleanArray42);
        boolean boolean46 = booleanList43.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream47 = booleanList43.stream();
        boolean boolean48 = strList37.containsAll((java.util.Collection<java.lang.Boolean>) booleanList43);
        boolean boolean49 = floatList33.containsAll((java.util.Collection<java.lang.Boolean>) booleanList43);
        java.util.Spliterator<java.lang.Float> floatSpliterator50 = floatList33.spliterator();
        esa.mo.nmf.NMFException nMFException52 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray53 = nMFException52.getSuppressed();
        int int54 = floatList33.lastIndexOf((java.lang.Object) throwableArray53);
        java.lang.Integer int55 = floatList33.getTypeShortForm();
        java.util.Spliterator<java.lang.Float> floatSpliterator56 = floatList33.spliterator();
        byte[] byteArray60 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob61 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray60);
        int int62 = blob61.getLength();
        int int63 = blob61.getLength();
        int int64 = blob61.getOffset();
        boolean boolean65 = blob61.isURLBased();
        java.lang.Long long66 = blob61.getShortForm();
        byte[] byteArray67 = blob61.getValue();
        org.ccsds.moims.mo.mal.structures.Blob blob70 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray67,
            (int) (short) 0, 3);
        boolean boolean71 = floatList33.remove((java.lang.Object) blob70);
        boolean boolean72 = stringList0.contains((java.lang.Object) blob70);
        org.ccsds.moims.mo.mal.structures.Element element73 = stringList0.createElement();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList75 = new org.ccsds.moims.mo.mal.structures.BooleanList(
            1);
        boolean boolean76 = stringList0.equals((java.lang.Object) 1);
        stringList0.ensureCapacity((int) (short) -1);
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-15) + "'", int3.equals((-15)));
        org.junit.Assert.assertNotNull(strStream6);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + (-1) + "'", int9 == (-1));
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + int12 + "' != '" + (-5) + "'", int12.equals((-5)));
        org.junit.Assert.assertNotNull(doubleStream13);
        org.junit.Assert.assertTrue("'" + long15 + "' != '" + 281475010265084L + "'", long15.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray17);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
        org.junit.Assert.assertTrue("'" + int20 + "' != '" + 1 + "'", int20 == 1);
        org.junit.Assert.assertNotNull(booleanArray23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(booleanStream28);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + false + "'", !boolean32);
        org.junit.Assert.assertTrue("'" + long34 + "' != '" + 281475010265084L + "'", long34.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray36);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertTrue("'" + int39 + "' != '" + 1 + "'", int39 == 1);
        org.junit.Assert.assertNotNull(booleanArray42);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertNotNull(booleanStream47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertNotNull(floatSpliterator50);
        org.junit.Assert.assertNotNull(throwableArray53);
        org.junit.Assert.assertTrue("'" + int54 + "' != '" + (-1) + "'", int54 == (-1));
        org.junit.Assert.assertTrue("'" + int55 + "' != '" + (-4) + "'", int55.equals((-4)));
        org.junit.Assert.assertNotNull(floatSpliterator56);
        org.junit.Assert.assertNotNull(byteArray60);
        org.junit.Assert.assertTrue("'" + int62 + "' != '" + 3 + "'", int62 == 3);
        org.junit.Assert.assertTrue("'" + int63 + "' != '" + 3 + "'", int63 == 3);
        org.junit.Assert.assertTrue("'" + int64 + "' != '" + 0 + "'", int64 == 0);
        org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
        org.junit.Assert.assertTrue("'" + long66 + "' != '" + 281474993487873L + "'", long66.equals(281474993487873L));
        org.junit.Assert.assertNotNull(byteArray67);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
        org.junit.Assert.assertNotNull(element73);
        org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
    }

    @Test
    public void test507() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test507");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        org.ccsds.moims.mo.mal.structures.UShort uShort24 = longList22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort25 = longList22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.StringList stringList26 = new org.ccsds.moims.mo.mal.structures.StringList();
        org.ccsds.moims.mo.mal.structures.Element element27 = stringList26.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort28 = stringList26.getServiceNumber();
        java.lang.Integer int29 = stringList26.getTypeShortForm();
        stringList26.ensureCapacity(13);
        int int32 = longList22.lastIndexOf((java.lang.Object) stringList26);
        org.ccsds.moims.mo.mal.structures.Element element33 = stringList26.createElement();
        java.lang.Short[] shortArray36 = new java.lang.Short[]{(short) 100, (short) -1};
        java.util.ArrayList<java.lang.Short> shortList37 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean38 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList37,
            shortArray36);
        java.lang.Object obj39 = null;
        boolean boolean40 = shortList37.equals(obj39);
        shortList37.clear();
        java.util.stream.Stream<java.lang.Short> shortStream42 = shortList37.parallelStream();
        org.ccsds.moims.mo.mal.structures.ShortList shortList43 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj44 = shortList43.clone();
        java.util.stream.Stream<java.lang.Short> shortStream45 = shortList43.parallelStream();
        shortList43.trimToSize();
        shortList43.ensureCapacity((int) ' ');
        java.lang.Integer[] intArray65 = new java.lang.Integer[]{18, 0, 18, 65535, (-1), 3, (-1), (-1), 13, 0, 3, (-5),
                                                                 12, 0, 100, 14};
        java.util.ArrayList<java.lang.Integer> intList66 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean67 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList66,
            intArray65);
        java.lang.Boolean[] booleanArray70 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList71 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean72 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList71,
            booleanArray70);
        java.util.Iterator<java.lang.Boolean> booleanItor73 = booleanList71.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream74 = booleanList71.stream();
        boolean boolean75 = intList66.retainAll((java.util.Collection<java.lang.Boolean>) booleanList71);
        boolean boolean76 = shortList43.containsAll((java.util.Collection<java.lang.Boolean>) booleanList71);
        org.ccsds.moims.mo.mal.structures.LongList longList78 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UShort uShort79 = longList78.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort80 = longList78.getServiceNumber();
        boolean boolean81 = booleanList71.remove((java.lang.Object) uShort80);
        boolean boolean82 = shortList37.removeAll((java.util.Collection<java.lang.Boolean>) booleanList71);
        int int83 = booleanList71.size();
        boolean boolean84 = stringList26.retainAll((java.util.Collection<java.lang.Boolean>) booleanList71);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(uShort24);
        org.junit.Assert.assertNotNull(uShort25);
        org.junit.Assert.assertNotNull(element27);
        org.junit.Assert.assertNotNull(uShort28);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-15) + "'", int29.equals((-15)));
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
        org.junit.Assert.assertNotNull(element33);
        org.junit.Assert.assertNotNull(shortArray36);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertNotNull(shortStream42);
        org.junit.Assert.assertNotNull(obj44);
        org.junit.Assert.assertNotNull(shortStream45);
        org.junit.Assert.assertNotNull(intArray65);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
        org.junit.Assert.assertNotNull(booleanArray70);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + true + "'", boolean72);
        org.junit.Assert.assertNotNull(booleanItor73);
        org.junit.Assert.assertNotNull(booleanStream74);
        org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + true + "'", boolean75);
        org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
        org.junit.Assert.assertNotNull(uShort79);
        org.junit.Assert.assertNotNull(uShort80);
        org.junit.Assert.assertTrue("'" + boolean81 + "' != '" + false + "'", !boolean81);
        org.junit.Assert.assertTrue("'" + boolean82 + "' != '" + false + "'", !boolean82);
        org.junit.Assert.assertTrue("'" + int83 + "' != '" + 2 + "'", int83 == 2);
        org.junit.Assert.assertTrue("'" + boolean84 + "' != '" + false + "'", !boolean84);
    }

    @Test
    public void test508() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test508");
        org.ccsds.moims.mo.mal.structures.StringList stringList1 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = stringList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList4 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.lang.Long long5 = integerList4.getShortForm();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList7 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        org.ccsds.moims.mo.mal.structures.IntegerList integerList9 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.lang.Long long10 = integerList9.getShortForm();
        org.ccsds.moims.mo.mal.structures.IntegerList[] integerListArray11 = new org.ccsds.moims.mo.mal.structures.IntegerList[]{integerList4,
                                                                                                                                 integerList7,
                                                                                                                                 integerList9};
        org.ccsds.moims.mo.mal.structures.IntegerList[] integerListArray12 = stringList1.toArray(integerListArray11);
        java.lang.Boolean[] booleanArray14 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList15 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList15,
            booleanArray14);
        java.util.ListIterator<java.lang.Boolean> booleanItor18 = booleanList15.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream19 = booleanList15.parallelStream();
        boolean boolean21 = booleanList15.add((java.lang.Boolean) false);
        java.util.ListIterator<java.lang.Boolean> booleanItor23 = booleanList15.listIterator(0);
        boolean boolean24 = stringList1.containsAll((java.util.Collection<java.lang.Boolean>) booleanList15);
        java.lang.Byte[] byteArray27 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList28 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList28,
            byteArray27);
        boolean boolean30 = byteList28.isEmpty();
        int int32 = byteList28.indexOf((java.lang.Object) 'a');
        int int34 = byteList28.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray36 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList37 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean38 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList37,
            booleanArray36);
        java.util.ListIterator<java.lang.Boolean> booleanItor40 = booleanList37.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream41 = booleanList37.parallelStream();
        boolean boolean43 = booleanList37.add((java.lang.Boolean) false);
        boolean boolean44 = byteList28.retainAll((java.util.Collection<java.lang.Boolean>) booleanList37);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList45 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj46 = uShortList45.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream47 = uShortList45
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor48 = uShortList45.iterator();
        boolean boolean49 = byteList28.equals((java.lang.Object) uShortItor48);
        org.ccsds.moims.mo.mal.structures.ShortList shortList50 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj51 = shortList50.clone();
        boolean boolean52 = byteList28.equals((java.lang.Object) shortList50);
        shortList50.clear();
        boolean boolean54 = stringList1.equals((java.lang.Object) shortList50);
        java.util.ListIterator<java.lang.String> strItor55 = stringList1.listIterator();
        org.ccsds.moims.mo.mal.structures.Time time57 = new org.ccsds.moims.mo.mal.structures.Time((long) (byte) -1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet58 = time57.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort59 = time57.getServiceNumber();
        java.lang.Integer int60 = time57.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element61 = time57.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort62 = time57.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.FloatList floatList63 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long64 = floatList63.getShortForm();
        java.lang.String[] strArray66 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList67 = new java.util.ArrayList<java.lang.String>();
        boolean boolean68 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList67,
            strArray66);
        int int69 = strList67.size();
        java.lang.Boolean[] booleanArray72 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList73 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean74 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList73,
            booleanArray72);
        boolean boolean76 = booleanList73.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream77 = booleanList73.stream();
        boolean boolean78 = strList67.containsAll((java.util.Collection<java.lang.Boolean>) booleanList73);
        boolean boolean79 = floatList63.containsAll((java.util.Collection<java.lang.Boolean>) booleanList73);
        java.util.Spliterator<java.lang.Float> floatSpliterator80 = floatList63.spliterator();
        esa.mo.nmf.NMFException nMFException82 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray83 = nMFException82.getSuppressed();
        int int84 = floatList63.lastIndexOf((java.lang.Object) throwableArray83);
        java.lang.Integer int85 = floatList63.getTypeShortForm();
        java.util.ListIterator<java.lang.Float> floatItor86 = floatList63.listIterator();
        boolean boolean87 = time57.equals((java.lang.Object) floatList63);
        java.lang.String str88 = floatList63.toString();
        int int89 = floatList63.size();
        int int90 = stringList1.lastIndexOf((java.lang.Object) floatList63);
        java.lang.Object obj91 = floatList63.clone();
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 281475010265077L + "'", long5.equals(281475010265077L));
        org.junit.Assert.assertTrue("'" + long10 + "' != '" + 281475010265077L + "'", long10.equals(281475010265077L));
        org.junit.Assert.assertNotNull(integerListArray11);
        org.junit.Assert.assertNotNull(integerListArray12);
        org.junit.Assert.assertNotNull(booleanArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertNotNull(booleanItor18);
        org.junit.Assert.assertNotNull(booleanStream19);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
        org.junit.Assert.assertNotNull(booleanItor23);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(byteArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
        org.junit.Assert.assertTrue("'" + int34 + "' != '" + (-1) + "'", int34 == (-1));
        org.junit.Assert.assertNotNull(booleanArray36);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertNotNull(booleanItor40);
        org.junit.Assert.assertNotNull(booleanStream41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
        org.junit.Assert.assertNotNull(obj46);
        org.junit.Assert.assertNotNull(uShortStream47);
        org.junit.Assert.assertNotNull(uShortItor48);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertNotNull(obj51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
        org.junit.Assert.assertNotNull(strItor55);
        org.junit.Assert.assertNotNull(uOctet58);
        org.junit.Assert.assertNotNull(uShort59);
        org.junit.Assert.assertTrue("'" + int60 + "' != '" + 16 + "'", int60.equals(16));
        org.junit.Assert.assertNotNull(element61);
        org.junit.Assert.assertNotNull(uShort62);
        org.junit.Assert.assertTrue("'" + long64 + "' != '" + 281475010265084L + "'", long64.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray66);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + true + "'", boolean68);
        org.junit.Assert.assertTrue("'" + int69 + "' != '" + 1 + "'", int69 == 1);
        org.junit.Assert.assertNotNull(booleanArray72);
        org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + true + "'", boolean74);
        org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + true + "'", boolean76);
        org.junit.Assert.assertNotNull(booleanStream77);
        org.junit.Assert.assertTrue("'" + boolean78 + "' != '" + false + "'", !boolean78);
        org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + false + "'", !boolean79);
        org.junit.Assert.assertNotNull(floatSpliterator80);
        org.junit.Assert.assertNotNull(throwableArray83);
        org.junit.Assert.assertTrue("'" + int84 + "' != '" + (-1) + "'", int84 == (-1));
        org.junit.Assert.assertTrue("'" + int85 + "' != '" + (-4) + "'", int85.equals((-4)));
        org.junit.Assert.assertNotNull(floatItor86);
        org.junit.Assert.assertTrue("'" + boolean87 + "' != '" + false + "'", !boolean87);
        org.junit.Assert.assertTrue("'" + str88 + "' != '" + "[]" + "'", str88.equals("[]"));
        org.junit.Assert.assertTrue("'" + int89 + "' != '" + 0 + "'", int89 == 0);
        org.junit.Assert.assertTrue("'" + int90 + "' != '" + (-1) + "'", int90 == (-1));
        org.junit.Assert.assertNotNull(obj91);
    }

    @Test
    public void test509() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test509");
        java.lang.String[] strArray1 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList2 = new java.util.ArrayList<java.lang.String>();
        boolean boolean3 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList2, strArray1);
        boolean boolean5 = strList2.add("hi!");
        java.lang.Object obj6 = null;
        boolean boolean7 = strList2.equals(obj6);
        java.util.ListIterator<java.lang.String> strItor8 = strList2.listIterator();
        java.lang.Object obj9 = null;
        boolean boolean10 = strList2.remove(obj9);
        java.util.Iterator<java.lang.String> strItor11 = strList2.iterator();
        java.util.Spliterator<java.lang.String> strSpliterator12 = strList2.spliterator();
        java.lang.String str13 = strList2.toString();
        java.util.Spliterator<java.lang.String> strSpliterator14 = strList2.spliterator();
        strList2.ensureCapacity((int) (short) 100);
        java.util.Iterator<java.lang.String> strItor17 = strList2.iterator();
        java.util.ListIterator<java.lang.String> strItor18 = strList2.listIterator();
        java.lang.Byte[] byteArray23 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList24 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean25 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList24,
            byteArray23);
        boolean boolean27 = byteList24.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj28 = byteList24.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream29 = byteList24.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList31 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int32 = byteList24.indexOf((java.lang.Object) stringList31);
        byte[] byteArray36 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob37 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray36);
        int int38 = blob37.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob39 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean40 = blob37.equals((java.lang.Object) blob39);
        boolean boolean41 = stringList31.remove((java.lang.Object) blob39);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF42 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean43 = stringList31.equals((java.lang.Object) mCServicesProviderNMF42);
        java.util.stream.Stream<java.lang.String> strStream44 = stringList31.parallelStream();
        java.lang.Long[] longArray49 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList50 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean51 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList50,
            longArray49);
        java.lang.Object obj52 = null;
        boolean boolean53 = longList50.contains(obj52);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger55 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean56 = longList50.contains((java.lang.Object) (byte) 100);
        java.util.Iterator<java.lang.Long> longItor57 = longList50.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort58 = org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
        boolean boolean59 = longList50.remove((java.lang.Object) uShort58);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList60 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int61 = uShortList60.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor62 = uShortList60.iterator();
        java.lang.Object[] objArray63 = uShortList60.toArray();
        uShortList60.clear();
        uShortList60.clear();
        boolean boolean66 = uShortList60.isEmpty();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor67 = uShortList60.iterator();
        boolean boolean68 = longList50.contains((java.lang.Object) uShortList60);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList69 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.UShort uShort70 = booleanList69.getAreaNumber();
        boolean boolean71 = longList50.retainAll((java.util.Collection<java.lang.Boolean>) booleanList69);
        java.lang.Integer int72 = booleanList69.getTypeShortForm();
        boolean boolean74 = booleanList69.add((java.lang.Boolean) true);
        boolean boolean75 = stringList31.retainAll((java.util.Collection<java.lang.Boolean>) booleanList69);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet76 = booleanList69.getAreaVersion();
        boolean boolean77 = strList2.containsAll((java.util.Collection<java.lang.Boolean>) booleanList69);
        org.junit.Assert.assertNotNull(strArray1);
        org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(strItor8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(strItor11);
        org.junit.Assert.assertNotNull(strSpliterator12);
        org.junit.Assert.assertTrue("'" + str13 + "' != '" + "[hi!, hi!]" + "'", str13.equals("[hi!, hi!]"));
        org.junit.Assert.assertNotNull(strSpliterator14);
        org.junit.Assert.assertNotNull(strItor17);
        org.junit.Assert.assertNotNull(strItor18);
        org.junit.Assert.assertNotNull(byteArray23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(obj28);
        org.junit.Assert.assertNotNull(byteStream29);
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
        org.junit.Assert.assertNotNull(byteArray36);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + 3 + "'", int38 == 3);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + false + "'", !boolean41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + false + "'", !boolean43);
        org.junit.Assert.assertNotNull(strStream44);
        org.junit.Assert.assertNotNull(longArray49);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertNotNull(longItor57);
        org.junit.Assert.assertNotNull(uShort58);
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + false + "'", !boolean59);
        org.junit.Assert.assertTrue("'" + int61 + "' != '" + (-10) + "'", int61.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor62);
        org.junit.Assert.assertNotNull(objArray63);
        org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + true + "'", boolean66);
        org.junit.Assert.assertNotNull(uShortItor67);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + false + "'", !boolean68);
        org.junit.Assert.assertNotNull(uShort70);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
        org.junit.Assert.assertTrue("'" + int72 + "' != '" + (-2) + "'", int72.equals((-2)));
        org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + true + "'", boolean74);
        org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + false + "'", !boolean75);
        org.junit.Assert.assertNotNull(uOctet76);
        org.junit.Assert.assertTrue("'" + boolean77 + "' != '" + false + "'", !boolean77);
    }

    @Test
    public void test510() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test510");
        esa.mo.nmf.NMFException nMFException2 = new esa.mo.nmf.NMFException("[-1, -1, 1, 1, 10]");
        esa.mo.nmf.NMFException nMFException3 = new esa.mo.nmf.NMFException("[1]", (java.lang.Throwable) nMFException2);
        java.lang.Throwable[] throwableArray4 = nMFException3.getSuppressed();
        org.junit.Assert.assertNotNull(throwableArray4);
    }

    @Test
    public void test511() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test511");
        esa.mo.nmf.NMFException nMFException2 = new esa.mo.nmf.NMFException("0");
        esa.mo.nmf.NMFException nMFException3 = new esa.mo.nmf.NMFException("hi!", (java.lang.Throwable) nMFException2);
        java.lang.String str4 = nMFException3.toString();
        esa.mo.nmf.NMFException nMFException6 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray7 = nMFException6.getSuppressed();
        java.lang.Throwable[] throwableArray8 = nMFException6.getSuppressed();
        esa.mo.nmf.NMFException nMFException10 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray11 = nMFException10.getSuppressed();
        nMFException6.addSuppressed((java.lang.Throwable) nMFException10);
        java.lang.Throwable[] throwableArray13 = nMFException10.getSuppressed();
        java.lang.Throwable[] throwableArray14 = nMFException10.getSuppressed();
        nMFException3.addSuppressed((java.lang.Throwable) nMFException10);
        java.lang.Throwable[] throwableArray16 = nMFException3.getSuppressed();
        org.junit.Assert.assertTrue("'" + str4 + "' != '" + "esa.mo.nmf.NMFException: hi!" + "'", str4.equals(
            "esa.mo.nmf.NMFException: hi!"));
        org.junit.Assert.assertNotNull(throwableArray7);
        org.junit.Assert.assertNotNull(throwableArray8);
        org.junit.Assert.assertNotNull(throwableArray11);
        org.junit.Assert.assertNotNull(throwableArray13);
        org.junit.Assert.assertNotNull(throwableArray14);
        org.junit.Assert.assertNotNull(throwableArray16);
    }

    @Test
    public void test512() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test512");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl21 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl23 = mCRegistration19.parameterService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl23);
    }

    @Test
    public void test513() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test513");
        esa.mo.nmf.NMFException nMFException3 = new esa.mo.nmf.NMFException("[-1.0]");
        esa.mo.nmf.NMFException nMFException4 = new esa.mo.nmf.NMFException("[hi!, [1]]",
            (java.lang.Throwable) nMFException3);
        esa.mo.nmf.NMFException nMFException5 = new esa.mo.nmf.NMFException("[true, true]",
            (java.lang.Throwable) nMFException4);
    }

    @Test
    public void test514() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test514");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl24 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl25 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl26 = mCRegistration19.actionService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl24);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl25);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl26);
    }

    @Test
    public void test515() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test515");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        org.ccsds.moims.mo.mal.structures.UShort uShort23 = stringList12.getAreaNumber();
        esa.mo.nmf.NMFException nMFException27 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray28 = nMFException27.getSuppressed();
        java.lang.Throwable[] throwableArray29 = nMFException27.getSuppressed();
        esa.mo.nmf.NMFException nMFException31 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray32 = nMFException31.getSuppressed();
        nMFException27.addSuppressed((java.lang.Throwable) nMFException31);
        java.lang.Throwable[] throwableArray34 = nMFException31.getSuppressed();
        java.lang.Throwable[] throwableArray35 = nMFException31.getSuppressed();
        esa.mo.nmf.NMFException nMFException36 = new esa.mo.nmf.NMFException("[]",
            (java.lang.Throwable) nMFException31);
        esa.mo.nmf.NMFException nMFException39 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray40 = nMFException39.getSuppressed();
        java.lang.Throwable[] throwableArray41 = nMFException39.getSuppressed();
        esa.mo.nmf.NMFException nMFException42 = new esa.mo.nmf.NMFException("[hi!, hi!]",
            (java.lang.Throwable) nMFException39);
        nMFException36.addSuppressed((java.lang.Throwable) nMFException39);
        esa.mo.nmf.NMFException nMFException44 = new esa.mo.nmf.NMFException("esa.mo.nmf.NMFException: 0",
            (java.lang.Throwable) nMFException39);
        boolean boolean45 = stringList12.remove((java.lang.Object) nMFException39);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertNotNull(uShort23);
        org.junit.Assert.assertNotNull(throwableArray28);
        org.junit.Assert.assertNotNull(throwableArray29);
        org.junit.Assert.assertNotNull(throwableArray32);
        org.junit.Assert.assertNotNull(throwableArray34);
        org.junit.Assert.assertNotNull(throwableArray35);
        org.junit.Assert.assertNotNull(throwableArray40);
        org.junit.Assert.assertNotNull(throwableArray41);
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + false + "'", !boolean45);
    }

    @Test
    public void test516() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test516");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream2 = uShortList0.parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor3 = uShortList0.iterator();
        org.ccsds.moims.mo.mal.structures.FloatList floatList4 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long5 = floatList4.getShortForm();
        java.lang.String[] strArray7 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList8 = new java.util.ArrayList<java.lang.String>();
        boolean boolean9 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList8, strArray7);
        int int10 = strList8.size();
        java.lang.Boolean[] booleanArray13 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList14 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList14,
            booleanArray13);
        boolean boolean17 = booleanList14.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream18 = booleanList14.stream();
        boolean boolean19 = strList8.containsAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        boolean boolean20 = floatList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        java.util.Spliterator<java.lang.Float> floatSpliterator21 = floatList4.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort22 = floatList4.getAreaNumber();
        java.lang.Object obj23 = null;
        boolean boolean24 = floatList4.equals(obj23);
        int int25 = uShortList0.indexOf(obj23);
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream26 = uShortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.FloatList floatList27 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long28 = floatList27.getShortForm();
        floatList27.trimToSize();
        java.lang.String str30 = floatList27.toString();
        java.util.Spliterator<java.lang.Float> floatSpliterator31 = floatList27.spliterator();
        java.lang.Byte[] byteArray34 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList35 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean36 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList35,
            byteArray34);
        boolean boolean37 = byteList35.isEmpty();
        int int39 = byteList35.indexOf((java.lang.Object) 'a');
        int int41 = byteList35.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray43 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList44 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean45 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList44,
            booleanArray43);
        java.util.ListIterator<java.lang.Boolean> booleanItor47 = booleanList44.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream48 = booleanList44.parallelStream();
        boolean boolean50 = booleanList44.add((java.lang.Boolean) false);
        boolean boolean51 = byteList35.retainAll((java.util.Collection<java.lang.Boolean>) booleanList44);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList52 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj53 = uShortList52.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream54 = uShortList52
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor55 = uShortList52.iterator();
        boolean boolean56 = byteList35.equals((java.lang.Object) uShortItor55);
        org.ccsds.moims.mo.mal.structures.ShortList shortList57 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj58 = shortList57.clone();
        boolean boolean59 = byteList35.equals((java.lang.Object) shortList57);
        esa.mo.nmf.NMFException nMFException61 = new esa.mo.nmf.NMFException("-1");
        boolean boolean62 = byteList35.equals((java.lang.Object) nMFException61);
        byteList35.trimToSize();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList64 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element65 = booleanList64.createElement();
        java.lang.Long long66 = booleanList64.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort67 = booleanList64.getAreaNumber();
        java.lang.Integer int68 = booleanList64.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray69 = new org.ccsds.moims.mo.mal.structures.BooleanList[]{booleanList64};
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray70 = byteList35.toArray(booleanListArray69);
        boolean boolean71 = floatList27.remove((java.lang.Object) booleanListArray69);
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray72 = uShortList0.toArray(booleanListArray69);
        org.ccsds.moims.mo.mal.structures.UShort uShort73 = uShortList0.getServiceNumber();
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(uShortStream2);
        org.junit.Assert.assertNotNull(uShortItor3);
        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 281475010265084L + "'", long5.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray7);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + 1 + "'", int10 == 1);
        org.junit.Assert.assertNotNull(booleanArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertNotNull(booleanStream18);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertNotNull(floatSpliterator21);
        org.junit.Assert.assertNotNull(uShort22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertTrue("'" + int25 + "' != '" + (-1) + "'", int25 == (-1));
        org.junit.Assert.assertNotNull(uShortStream26);
        org.junit.Assert.assertTrue("'" + long28 + "' != '" + 281475010265084L + "'", long28.equals(281475010265084L));
        org.junit.Assert.assertTrue("'" + str30 + "' != '" + "[]" + "'", str30.equals("[]"));
        org.junit.Assert.assertNotNull(floatSpliterator31);
        org.junit.Assert.assertNotNull(byteArray34);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertTrue("'" + int39 + "' != '" + (-1) + "'", int39 == (-1));
        org.junit.Assert.assertTrue("'" + int41 + "' != '" + (-1) + "'", int41 == (-1));
        org.junit.Assert.assertNotNull(booleanArray43);
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
        org.junit.Assert.assertNotNull(booleanItor47);
        org.junit.Assert.assertNotNull(booleanStream48);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + true + "'", boolean50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
        org.junit.Assert.assertNotNull(obj53);
        org.junit.Assert.assertNotNull(uShortStream54);
        org.junit.Assert.assertNotNull(uShortItor55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertNotNull(obj58);
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + true + "'", boolean59);
        org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + false + "'", !boolean62);
        org.junit.Assert.assertNotNull(element65);
        org.junit.Assert.assertTrue("'" + long66 + "' != '" + 281475010265086L + "'", long66.equals(281475010265086L));
        org.junit.Assert.assertNotNull(uShort67);
        org.junit.Assert.assertTrue("'" + int68 + "' != '" + (-2) + "'", int68.equals((-2)));
        org.junit.Assert.assertNotNull(booleanListArray69);
        org.junit.Assert.assertNotNull(booleanListArray70);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
        org.junit.Assert.assertNotNull(booleanListArray72);
        org.junit.Assert.assertNotNull(uShort73);
    }

    @Test
    public void test517() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test517");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj1 = shortList0.clone();
        java.util.stream.Stream<java.lang.Short> shortStream2 = shortList0.parallelStream();
        shortList0.trimToSize();
        shortList0.ensureCapacity((int) ' ');
        java.lang.Integer[] intArray22 = new java.lang.Integer[]{18, 0, 18, 65535, (-1), 3, (-1), (-1), 13, 0, 3, (-5),
                                                                 12, 0, 100, 14};
        java.util.ArrayList<java.lang.Integer> intList23 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean24 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList23,
            intArray22);
        java.lang.Boolean[] booleanArray27 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList28 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList28,
            booleanArray27);
        java.util.Iterator<java.lang.Boolean> booleanItor30 = booleanList28.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream31 = booleanList28.stream();
        boolean boolean32 = intList23.retainAll((java.util.Collection<java.lang.Boolean>) booleanList28);
        boolean boolean33 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList28);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet34 = shortList0.getAreaVersion();
        java.lang.Long long35 = shortList0.getShortForm();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF36 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl37 = mCServicesProviderNMF36
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl38 = mCServicesProviderNMF36
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl39 = mCServicesProviderNMF36
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl40 = mCServicesProviderNMF36
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl41 = mCServicesProviderNMF36
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF36
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl43 = mCServicesProviderNMF36
            .getAggregationService();
        boolean boolean44 = shortList0.remove((java.lang.Object) mCServicesProviderNMF36);
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl45 = mCServicesProviderNMF36
            .getAggregationService();
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(shortStream2);
        org.junit.Assert.assertNotNull(intArray22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertNotNull(booleanArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertNotNull(booleanItor30);
        org.junit.Assert.assertNotNull(booleanStream31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertNotNull(uOctet34);
        org.junit.Assert.assertTrue("'" + long35 + "' != '" + 281475010265079L + "'", long35.equals(281475010265079L));
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl37);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl38);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl39);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl40);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl45);
    }

    @Test
    public void test518() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test518");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl25 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl26 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl27 = mCServicesProviderNMF23
            .getActionService();
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl25);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl26);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl27);
    }

    @Test
    public void test519() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test519");
        org.ccsds.moims.mo.mal.structures.IntegerList integerList1 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.lang.String str2 = integerList1.toString();
        boolean boolean4 = integerList1.add((java.lang.Integer) 8);
        integerList1.trimToSize();
        esa.mo.nmf.NMFException nMFException8 = new esa.mo.nmf.NMFException("[100, 0]");
        esa.mo.nmf.NMFException nMFException9 = new esa.mo.nmf.NMFException("[100, -1]",
            (java.lang.Throwable) nMFException8);
        int int10 = integerList1.lastIndexOf((java.lang.Object) nMFException8);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "[]" + "'", str2.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-1) + "'", int10 == (-1));
    }

    @Test
    public void test520() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test520");
        org.ccsds.moims.mo.mal.structures.LongList longList1 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = longList1.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort3 = longList1.getAreaNumber();
        java.util.Spliterator<java.lang.Long> longSpliterator4 = longList1.spliterator();
        java.lang.String[] strArray8 = new java.lang.String[]{"[-1, -1, 1, 1, 10]", "100", "100"};
        java.util.ArrayList<java.lang.String> strList9 = new java.util.ArrayList<java.lang.String>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList9, strArray8);
        java.lang.Boolean[] booleanArray13 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList14 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList14,
            booleanArray13);
        boolean boolean17 = booleanList14.add((java.lang.Boolean) true);
        boolean boolean18 = strList9.removeAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        java.lang.String[] strArray20 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList21 = new java.util.ArrayList<java.lang.String>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList21,
            strArray20);
        int int23 = strList21.size();
        java.lang.Boolean[] booleanArray26 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList27 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean28 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList27,
            booleanArray26);
        boolean boolean30 = booleanList27.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream31 = booleanList27.stream();
        boolean boolean32 = strList21.containsAll((java.util.Collection<java.lang.Boolean>) booleanList27);
        int int33 = booleanList14.indexOf((java.lang.Object) strList21);
        java.util.Iterator<java.lang.String> strItor34 = strList21.iterator();
        java.util.stream.Stream<java.lang.String> strStream35 = strList21.parallelStream();
        strList21.clear();
        java.util.ListIterator<java.lang.String> strItor37 = strList21.listIterator();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF38 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl40 = mCServicesProviderNMF38
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl41 = mCServicesProviderNMF38
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF42 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCServicesProviderNMF42
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl44 = mCServicesProviderNMF42
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl45 = mCServicesProviderNMF42
            .getAlertService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl46 = mCServicesProviderNMF42
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF47 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl48 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl49 = mCServicesProviderNMF47
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl50 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl51 = mCServicesProviderNMF47
            .getAggregationService();
        org.ccsds.moims.mo.mc.aggregation.provider.AggregationSkeleton[] aggregationSkeletonArray52 = new org.ccsds.moims.mo.mc.aggregation.provider.AggregationSkeleton[]{aggregationProviderServiceImpl41,
                                                                                                                                                                           aggregationProviderServiceImpl46,
                                                                                                                                                                           aggregationProviderServiceImpl51};
        org.ccsds.moims.mo.mc.aggregation.provider.AggregationSkeleton[] aggregationSkeletonArray53 = strList21.toArray(
            aggregationSkeletonArray52);
        org.ccsds.moims.mo.mc.aggregation.provider.AggregationSkeleton[] aggregationSkeletonArray54 = longList1.toArray(
            aggregationSkeletonArray52);
        org.ccsds.moims.mo.mal.structures.UShort uShort55 = longList1.getServiceNumber();
        longList1.ensureCapacity((-13));
        org.ccsds.moims.mo.mal.structures.UShort uShort58 = longList1.getServiceNumber();
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(uShort3);
        org.junit.Assert.assertNotNull(longSpliterator4);
        org.junit.Assert.assertNotNull(strArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertNotNull(booleanArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
        org.junit.Assert.assertNotNull(strArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + 1 + "'", int23 == 1);
        org.junit.Assert.assertNotNull(booleanArray26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertNotNull(booleanStream31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + false + "'", !boolean32);
        org.junit.Assert.assertTrue("'" + int33 + "' != '" + (-1) + "'", int33 == (-1));
        org.junit.Assert.assertNotNull(strItor34);
        org.junit.Assert.assertNotNull(strStream35);
        org.junit.Assert.assertNotNull(strItor37);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl40);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl44);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl45);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl46);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl48);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl49);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl50);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl51);
        org.junit.Assert.assertNotNull(aggregationSkeletonArray52);
        org.junit.Assert.assertNotNull(aggregationSkeletonArray53);
        org.junit.Assert.assertNotNull(aggregationSkeletonArray54);
        org.junit.Assert.assertNotNull(uShort55);
        org.junit.Assert.assertNotNull(uShort58);
    }

    @Test
    public void test521() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test521");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream2 = uShortList0.parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor3 = uShortList0.iterator();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor4 = uShortList0.listIterator();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider5 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl13 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl14 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl15 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF16 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl17 = mCServicesProviderNMF16
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl18 = mCServicesProviderNMF16
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF19 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCServicesProviderNMF19
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCServicesProviderNMF19
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl22 = mCServicesProviderNMF19
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCServicesProviderNMF19
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration24 = new esa.mo.nmf.MCRegistration(cOMServicesProvider5,
            parameterProviderServiceImpl10, aggregationProviderServiceImpl15, alertProviderServiceImpl18,
            actionProviderServiceImpl23);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl25 = mCRegistration24.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl26 = mCRegistration24.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider27 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF28 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl29 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl30 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF33 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl34 = mCServicesProviderNMF33
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl35 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl36 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl37 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF38 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl40 = mCServicesProviderNMF38
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF41 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF41
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl43 = mCServicesProviderNMF41
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCServicesProviderNMF41
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl45 = mCServicesProviderNMF41
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration46 = new esa.mo.nmf.MCRegistration(cOMServicesProvider27,
            parameterProviderServiceImpl32, aggregationProviderServiceImpl37, alertProviderServiceImpl40,
            actionProviderServiceImpl45);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF47 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl48 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl49 = mCServicesProviderNMF47
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl50 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl51 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl52 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl53 = mCServicesProviderNMF47
            .getActionService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF54 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl55 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl56 = mCServicesProviderNMF54
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl57 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl58 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl59 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl60 = mCServicesProviderNMF54
            .getActionService();
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray61 = new org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[]{actionProviderServiceImpl26,
                                                                                                                                                                                   actionProviderServiceImpl45,
                                                                                                                                                                                   actionProviderServiceImpl53,
                                                                                                                                                                                   actionProviderServiceImpl60};
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray62 = uShortList0
            .toArray(actionInheritanceSkeletonArray61);
        uShortList0.ensureCapacity(18);
        org.ccsds.moims.mo.mal.structures.FineTime fineTime66 = new org.ccsds.moims.mo.mal.structures.FineTime(
            281474993487884L);
        org.ccsds.moims.mo.mal.structures.UShort uShort67 = fineTime66.getAreaNumber();
        boolean boolean68 = uShortList0.equals((java.lang.Object) fineTime66);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList69 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.UShort uShort70 = booleanList69.getAreaNumber();
        java.util.ListIterator<java.lang.Boolean> booleanItor71 = booleanList69.listIterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort72 = booleanList69.getAreaNumber();
        boolean boolean73 = uShortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList69);
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(uShortStream2);
        org.junit.Assert.assertNotNull(uShortItor3);
        org.junit.Assert.assertNotNull(uShortItor4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl13);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl14);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl15);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl17);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl25);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl26);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl29);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl34);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl35);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl36);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl37);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl40);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl45);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl48);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl49);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl50);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl51);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl52);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl53);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl55);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl56);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl57);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl58);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl59);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl60);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray61);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray62);
        org.junit.Assert.assertNotNull(uShort67);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + false + "'", !boolean68);
        org.junit.Assert.assertNotNull(uShort70);
        org.junit.Assert.assertNotNull(booleanItor71);
        org.junit.Assert.assertNotNull(uShort72);
        org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + true + "'", boolean73);
    }

    @Test
    public void test522() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test522");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int1 = uShortList0.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor2 = uShortList0.iterator();
        java.lang.Long long3 = uShortList0.getShortForm();
        uShortList0.clear();
        org.ccsds.moims.mo.mal.structures.OctetList octetList5 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.Element element6 = octetList5.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort7 = octetList5.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet8 = octetList5.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort9 = octetList5.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort10 = octetList5.getServiceNumber();
        java.lang.Long long11 = octetList5.getShortForm();
        org.ccsds.moims.mo.mal.structures.ShortList shortList12 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj13 = shortList12.clone();
        java.util.stream.Stream<java.lang.Short> shortStream14 = shortList12.parallelStream();
        shortList12.trimToSize();
        shortList12.ensureCapacity((int) ' ');
        java.lang.Integer[] intArray34 = new java.lang.Integer[]{18, 0, 18, 65535, (-1), 3, (-1), (-1), 13, 0, 3, (-5),
                                                                 12, 0, 100, 14};
        java.util.ArrayList<java.lang.Integer> intList35 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean36 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList35,
            intArray34);
        java.lang.Boolean[] booleanArray39 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList40 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean41 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList40,
            booleanArray39);
        java.util.Iterator<java.lang.Boolean> booleanItor42 = booleanList40.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream43 = booleanList40.stream();
        boolean boolean44 = intList35.retainAll((java.util.Collection<java.lang.Boolean>) booleanList40);
        boolean boolean45 = shortList12.containsAll((java.util.Collection<java.lang.Boolean>) booleanList40);
        java.lang.Boolean[] booleanArray47 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList48 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean49 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList48,
            booleanArray47);
        java.util.ListIterator<java.lang.Boolean> booleanItor51 = booleanList48.listIterator((int) (short) 1);
        boolean boolean52 = shortList12.containsAll((java.util.Collection<java.lang.Boolean>) booleanList48);
        java.util.stream.Stream<java.lang.Boolean> booleanStream53 = booleanList48.stream();
        boolean boolean54 = booleanList48.isEmpty();
        org.ccsds.moims.mo.mal.structures.FloatList floatList55 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long56 = floatList55.getShortForm();
        java.lang.String[] strArray58 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList59 = new java.util.ArrayList<java.lang.String>();
        boolean boolean60 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList59,
            strArray58);
        int int61 = strList59.size();
        java.lang.Boolean[] booleanArray64 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList65 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean66 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList65,
            booleanArray64);
        boolean boolean68 = booleanList65.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream69 = booleanList65.stream();
        boolean boolean70 = strList59.containsAll((java.util.Collection<java.lang.Boolean>) booleanList65);
        boolean boolean71 = floatList55.containsAll((java.util.Collection<java.lang.Boolean>) booleanList65);
        java.util.Spliterator<java.lang.Float> floatSpliterator72 = floatList55.spliterator();
        esa.mo.nmf.NMFException nMFException74 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray75 = nMFException74.getSuppressed();
        int int76 = floatList55.lastIndexOf((java.lang.Object) throwableArray75);
        org.ccsds.moims.mo.mal.structures.Element element77 = floatList55.createElement();
        int int78 = booleanList48.lastIndexOf((java.lang.Object) element77);
        booleanList48.clear();
        boolean boolean80 = octetList5.removeAll((java.util.Collection<java.lang.Boolean>) booleanList48);
        java.util.stream.Stream<java.lang.Boolean> booleanStream81 = booleanList48.parallelStream();
        boolean boolean82 = uShortList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList48);
        org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-10) + "'", int1.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor2);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 281475010265078L + "'", long3.equals(281475010265078L));
        org.junit.Assert.assertNotNull(element6);
        org.junit.Assert.assertNotNull(uShort7);
        org.junit.Assert.assertNotNull(uOctet8);
        org.junit.Assert.assertNotNull(uShort9);
        org.junit.Assert.assertNotNull(uShort10);
        org.junit.Assert.assertTrue("'" + long11 + "' != '" + 281475010265081L + "'", long11.equals(281475010265081L));
        org.junit.Assert.assertNotNull(obj13);
        org.junit.Assert.assertNotNull(shortStream14);
        org.junit.Assert.assertNotNull(intArray34);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
        org.junit.Assert.assertNotNull(booleanArray39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertNotNull(booleanItor42);
        org.junit.Assert.assertNotNull(booleanStream43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + false + "'", !boolean45);
        org.junit.Assert.assertNotNull(booleanArray47);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
        org.junit.Assert.assertNotNull(booleanItor51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
        org.junit.Assert.assertNotNull(booleanStream53);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertTrue("'" + long56 + "' != '" + 281475010265084L + "'", long56.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray58);
        org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + true + "'", boolean60);
        org.junit.Assert.assertTrue("'" + int61 + "' != '" + 1 + "'", int61 == 1);
        org.junit.Assert.assertNotNull(booleanArray64);
        org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + true + "'", boolean66);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + true + "'", boolean68);
        org.junit.Assert.assertNotNull(booleanStream69);
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + false + "'", !boolean70);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + false + "'", !boolean71);
        org.junit.Assert.assertNotNull(floatSpliterator72);
        org.junit.Assert.assertNotNull(throwableArray75);
        org.junit.Assert.assertTrue("'" + int76 + "' != '" + (-1) + "'", int76 == (-1));
        org.junit.Assert.assertNotNull(element77);
        org.junit.Assert.assertTrue("'" + int78 + "' != '" + (-1) + "'", int78 == (-1));
        org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + false + "'", !boolean80);
        org.junit.Assert.assertNotNull(booleanStream81);
        org.junit.Assert.assertTrue("'" + boolean82 + "' != '" + false + "'", !boolean82);
    }

    @Test
    public void test523() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test523");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl3 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl4 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl5 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl6 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl7 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl8 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl9 = mCServicesProviderNMF0
            .getActionService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl3);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl4);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl6);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl7);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl8);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl9);
    }

    @Test
    public void test524() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test524");
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.UShort uShort1 = doubleList0.getServiceNumber();
        boolean boolean3 = doubleList0.add((java.lang.Double) 1.0d);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet4 = doubleList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList5 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element6 = booleanList5.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort7 = booleanList5.getAreaNumber();
        java.lang.Long long8 = booleanList5.getShortForm();
        java.util.stream.Stream<java.lang.Boolean> booleanStream9 = booleanList5.parallelStream();
        java.lang.Integer int10 = booleanList5.getTypeShortForm();
        java.lang.String str11 = booleanList5.toString();
        boolean boolean12 = doubleList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList5);
        org.ccsds.moims.mo.mal.structures.UShort uShort13 = booleanList5.getServiceNumber();
        java.lang.Byte[] byteArray18 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList19 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean20 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList19,
            byteArray18);
        boolean boolean22 = byteList19.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj23 = byteList19.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream24 = byteList19.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList26 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int27 = byteList19.indexOf((java.lang.Object) stringList26);
        byte[] byteArray31 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob32 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray31);
        int int33 = blob32.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob34 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean35 = blob32.equals((java.lang.Object) blob34);
        boolean boolean36 = stringList26.remove((java.lang.Object) blob34);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF37 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean38 = stringList26.equals((java.lang.Object) mCServicesProviderNMF37);
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl39 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl40 = mCServicesProviderNMF37
            .getParameterService();
        boolean boolean41 = booleanList5.remove((java.lang.Object) mCServicesProviderNMF37);
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl42 = mCServicesProviderNMF37
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl43 = mCServicesProviderNMF37
            .getActionService();
        org.junit.Assert.assertNotNull(uShort1);
        org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3);
        org.junit.Assert.assertNotNull(uOctet4);
        org.junit.Assert.assertNotNull(element6);
        org.junit.Assert.assertNotNull(uShort7);
        org.junit.Assert.assertTrue("'" + long8 + "' != '" + 281475010265086L + "'", long8.equals(281475010265086L));
        org.junit.Assert.assertNotNull(booleanStream9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-2) + "'", int10.equals((-2)));
        org.junit.Assert.assertTrue("'" + str11 + "' != '" + "[]" + "'", str11.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(uShort13);
        org.junit.Assert.assertNotNull(byteArray18);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(obj23);
        org.junit.Assert.assertNotNull(byteStream24);
        org.junit.Assert.assertTrue("'" + int27 + "' != '" + (-1) + "'", int27 == (-1));
        org.junit.Assert.assertNotNull(byteArray31);
        org.junit.Assert.assertTrue("'" + int33 + "' != '" + 3 + "'", int33 == 3);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl39);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl40);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + false + "'", !boolean41);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl42);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl43);
    }

    @Test
    public void test525() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test525");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        java.util.Iterator<java.lang.Long> longItor24 = longList22.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort25 = longList22.getServiceNumber();
        java.lang.Long long26 = longList22.getShortForm();
        boolean boolean27 = longList22.isEmpty();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(longItor24);
        org.junit.Assert.assertNotNull(uShort25);
        org.junit.Assert.assertTrue("'" + long26 + "' != '" + 281475010265075L + "'", long26.equals(281475010265075L));
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
    }

    @Test
    public void test526() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test526");
        org.ccsds.moims.mo.mal.structures.LongList longList1 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = longList1.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort3 = longList1.getAreaNumber();
        java.util.Spliterator<java.lang.Long> longSpliterator4 = longList1.spliterator();
        java.lang.String[] strArray8 = new java.lang.String[]{"[-1, -1, 1, 1, 10]", "100", "100"};
        java.util.ArrayList<java.lang.String> strList9 = new java.util.ArrayList<java.lang.String>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList9, strArray8);
        java.lang.Boolean[] booleanArray13 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList14 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList14,
            booleanArray13);
        boolean boolean17 = booleanList14.add((java.lang.Boolean) true);
        boolean boolean18 = strList9.removeAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        java.lang.String[] strArray20 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList21 = new java.util.ArrayList<java.lang.String>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList21,
            strArray20);
        int int23 = strList21.size();
        java.lang.Boolean[] booleanArray26 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList27 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean28 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList27,
            booleanArray26);
        boolean boolean30 = booleanList27.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream31 = booleanList27.stream();
        boolean boolean32 = strList21.containsAll((java.util.Collection<java.lang.Boolean>) booleanList27);
        int int33 = booleanList14.indexOf((java.lang.Object) strList21);
        java.util.Iterator<java.lang.String> strItor34 = strList21.iterator();
        java.util.stream.Stream<java.lang.String> strStream35 = strList21.parallelStream();
        strList21.clear();
        java.util.ListIterator<java.lang.String> strItor37 = strList21.listIterator();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF38 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl40 = mCServicesProviderNMF38
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl41 = mCServicesProviderNMF38
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF42 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCServicesProviderNMF42
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl44 = mCServicesProviderNMF42
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl45 = mCServicesProviderNMF42
            .getAlertService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl46 = mCServicesProviderNMF42
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF47 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl48 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl49 = mCServicesProviderNMF47
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl50 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl51 = mCServicesProviderNMF47
            .getAggregationService();
        org.ccsds.moims.mo.mc.aggregation.provider.AggregationSkeleton[] aggregationSkeletonArray52 = new org.ccsds.moims.mo.mc.aggregation.provider.AggregationSkeleton[]{aggregationProviderServiceImpl41,
                                                                                                                                                                           aggregationProviderServiceImpl46,
                                                                                                                                                                           aggregationProviderServiceImpl51};
        org.ccsds.moims.mo.mc.aggregation.provider.AggregationSkeleton[] aggregationSkeletonArray53 = strList21.toArray(
            aggregationSkeletonArray52);
        org.ccsds.moims.mo.mc.aggregation.provider.AggregationSkeleton[] aggregationSkeletonArray54 = longList1.toArray(
            aggregationSkeletonArray52);
        org.ccsds.moims.mo.mal.structures.UShort uShort55 = longList1.getServiceNumber();
        java.util.Spliterator<java.lang.Long> longSpliterator56 = longList1.spliterator();
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(uShort3);
        org.junit.Assert.assertNotNull(longSpliterator4);
        org.junit.Assert.assertNotNull(strArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertNotNull(booleanArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
        org.junit.Assert.assertNotNull(strArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + 1 + "'", int23 == 1);
        org.junit.Assert.assertNotNull(booleanArray26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertNotNull(booleanStream31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + false + "'", !boolean32);
        org.junit.Assert.assertTrue("'" + int33 + "' != '" + (-1) + "'", int33 == (-1));
        org.junit.Assert.assertNotNull(strItor34);
        org.junit.Assert.assertNotNull(strStream35);
        org.junit.Assert.assertNotNull(strItor37);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl40);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl44);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl45);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl46);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl48);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl49);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl50);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl51);
        org.junit.Assert.assertNotNull(aggregationSkeletonArray52);
        org.junit.Assert.assertNotNull(aggregationSkeletonArray53);
        org.junit.Assert.assertNotNull(aggregationSkeletonArray54);
        org.junit.Assert.assertNotNull(uShort55);
        org.junit.Assert.assertNotNull(longSpliterator56);
    }

    @Test
    public void test527() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test527");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        java.lang.Integer int22 = floatList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort23 = floatList0.getServiceNumber();
        boolean boolean25 = floatList0.add((java.lang.Float) 0.0f);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-4) + "'", int22.equals((-4)));
        org.junit.Assert.assertNotNull(uShort23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
    }

    @Test
    public void test528() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test528");
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList0 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element1 = booleanList0.createElement();
        java.lang.Long long2 = booleanList0.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element3 = booleanList0.createElement();
        esa.mo.nmf.NMFException nMFException5 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray6 = nMFException5.getSuppressed();
        java.lang.Throwable[] throwableArray7 = nMFException5.getSuppressed();
        java.lang.Throwable[] throwableArray8 = nMFException5.getSuppressed();
        boolean boolean9 = booleanList0.equals((java.lang.Object) throwableArray8);
        org.ccsds.moims.mo.mal.structures.UShort uShort10 = booleanList0.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.Element element11 = booleanList0.createElement();
        java.lang.Integer int12 = booleanList0.getTypeShortForm();
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265086L + "'", long2.equals(281475010265086L));
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(throwableArray6);
        org.junit.Assert.assertNotNull(throwableArray7);
        org.junit.Assert.assertNotNull(throwableArray8);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
        org.junit.Assert.assertNotNull(uShort10);
        org.junit.Assert.assertNotNull(element11);
        org.junit.Assert.assertTrue("'" + int12 + "' != '" + (-2) + "'", int12.equals((-2)));
    }

    @Test
    public void test529() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test529");
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList0 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element1 = booleanList0.createElement();
        java.lang.Long long2 = booleanList0.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element3 = booleanList0.createElement();
        esa.mo.nmf.NMFException nMFException5 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray6 = nMFException5.getSuppressed();
        java.lang.Throwable[] throwableArray7 = nMFException5.getSuppressed();
        java.lang.Throwable[] throwableArray8 = nMFException5.getSuppressed();
        boolean boolean9 = booleanList0.equals((java.lang.Object) throwableArray8);
        org.ccsds.moims.mo.mal.structures.UShort uShort10 = booleanList0.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.Element element11 = booleanList0.createElement();
        java.lang.Long long12 = booleanList0.getShortForm();
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265086L + "'", long2.equals(281475010265086L));
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(throwableArray6);
        org.junit.Assert.assertNotNull(throwableArray7);
        org.junit.Assert.assertNotNull(throwableArray8);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
        org.junit.Assert.assertNotNull(uShort10);
        org.junit.Assert.assertNotNull(element11);
        org.junit.Assert.assertTrue("'" + long12 + "' != '" + 281475010265086L + "'", long12.equals(281475010265086L));
    }

    @Test
    public void test530() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test530");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray2 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList3 = new java.util.ArrayList<java.lang.String>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList3, strArray2);
        int int5 = strList3.size();
        java.lang.Boolean[] booleanArray8 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList9 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList9,
            booleanArray8);
        boolean boolean12 = booleanList9.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream13 = booleanList9.stream();
        boolean boolean14 = strList3.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        boolean boolean15 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        java.util.stream.Stream<java.lang.Short> shortStream16 = shortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element17 = shortList0.createElement();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("false");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = shortList0.indexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList22 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.String str23 = doubleList22.toString();
        java.lang.Boolean[] booleanArray25 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList26 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList26,
            booleanArray25);
        java.util.ListIterator<java.lang.Boolean> booleanItor29 = booleanList26.listIterator((int) (short) 1);
        boolean boolean30 = doubleList22.containsAll((java.util.Collection<java.lang.Boolean>) booleanList26);
        java.lang.Byte[] byteArray33 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList34 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList34,
            byteArray33);
        boolean boolean36 = byteList34.isEmpty();
        int int37 = byteList34.size();
        int int38 = booleanList26.indexOf((java.lang.Object) byteList34);
        java.lang.Boolean[] booleanArray41 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList42 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList42,
            booleanArray41);
        java.util.Iterator<java.lang.Boolean> booleanItor44 = booleanList42.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream45 = booleanList42.stream();
        java.util.Iterator<java.lang.Boolean> booleanItor46 = booleanList42.iterator();
        boolean boolean47 = byteList34.removeAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        boolean boolean48 = shortList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        org.ccsds.moims.mo.mal.structures.Element element49 = shortList0.createElement();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList50 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        java.lang.Integer int51 = booleanList50.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element52 = booleanList50.createElement();
        boolean boolean53 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        org.ccsds.moims.mo.mal.structures.OctetList octetList54 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UShort uShort55 = octetList54.getServiceNumber();
        boolean boolean56 = octetList54.isEmpty();
        org.ccsds.moims.mo.mal.structures.UShort uShort57 = octetList54.getServiceNumber();
        java.lang.Long long58 = octetList54.getShortForm();
        boolean boolean59 = shortList0.contains((java.lang.Object) octetList54);
        try {
            java.lang.Short short61 = shortList0.remove((int) (short) 0);
            org.junit.Assert.fail(
                "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 0, Size: 0");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(strArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + 1 + "'", int5 == 1);
        org.junit.Assert.assertNotNull(booleanArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(booleanStream13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertNotNull(shortStream16);
        org.junit.Assert.assertNotNull(element17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + str23 + "' != '" + "[]" + "'", str23.equals("[]"));
        org.junit.Assert.assertNotNull(booleanArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(booleanItor29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertNotNull(byteArray33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertTrue("'" + int37 + "' != '" + 2 + "'", int37 == 2);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertNotNull(booleanArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertNotNull(booleanItor44);
        org.junit.Assert.assertNotNull(booleanStream45);
        org.junit.Assert.assertNotNull(booleanItor46);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertNotNull(element49);
        org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-2) + "'", int51.equals((-2)));
        org.junit.Assert.assertNotNull(element52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
        org.junit.Assert.assertNotNull(uShort55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
        org.junit.Assert.assertNotNull(uShort57);
        org.junit.Assert.assertTrue("'" + long58 + "' != '" + 281475010265081L + "'", long58.equals(281475010265081L));
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + false + "'", !boolean59);
    }

    @Test
    public void test531() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test531");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        java.lang.Integer int22 = floatList0.getTypeShortForm();
        java.lang.Integer[] intArray25 = new java.lang.Integer[]{(-5), (-5)};
        java.util.ArrayList<java.lang.Integer> intList26 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList26,
            intArray25);
        java.util.Spliterator<java.lang.Integer> intSpliterator28 = intList26.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort29 = org.ccsds.moims.mo.mal.structures.ShortList.SERVICE_SHORT_FORM;
        boolean boolean30 = intList26.remove((java.lang.Object) uShort29);
        boolean boolean31 = intList26.isEmpty();
        java.lang.Object obj32 = intList26.clone();
        boolean boolean33 = floatList0.contains((java.lang.Object) intList26);
        java.util.Spliterator<java.lang.Integer> intSpliterator34 = intList26.spliterator();
        try {
            java.lang.Integer int36 = intList26.get((int) 'a');
            org.junit.Assert.fail(
                "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 97, Size: 2");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-4) + "'", int22.equals((-4)));
        org.junit.Assert.assertNotNull(intArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(intSpliterator28);
        org.junit.Assert.assertNotNull(uShort29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
        org.junit.Assert.assertNotNull(obj32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertNotNull(intSpliterator34);
    }

    @Test
    public void test532() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test532");
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Integer int1 = doubleList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = doubleList0.getServiceNumber();
        java.lang.Byte[] byteArray5 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList6 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean7 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList6, byteArray5);
        boolean boolean8 = byteList6.isEmpty();
        int int10 = byteList6.indexOf((java.lang.Object) 'a');
        int int12 = byteList6.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray14 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList15 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList15,
            booleanArray14);
        java.util.ListIterator<java.lang.Boolean> booleanItor18 = booleanList15.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream19 = booleanList15.parallelStream();
        boolean boolean21 = booleanList15.add((java.lang.Boolean) false);
        boolean boolean22 = byteList6.retainAll((java.util.Collection<java.lang.Boolean>) booleanList15);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList23 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj24 = uShortList23.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream25 = uShortList23
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor26 = uShortList23.iterator();
        boolean boolean27 = byteList6.equals((java.lang.Object) uShortItor26);
        org.ccsds.moims.mo.mal.structures.ShortList shortList28 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj29 = shortList28.clone();
        boolean boolean30 = byteList6.equals((java.lang.Object) shortList28);
        esa.mo.nmf.NMFException nMFException32 = new esa.mo.nmf.NMFException("-1");
        boolean boolean33 = byteList6.equals((java.lang.Object) nMFException32);
        byteList6.trimToSize();
        int int35 = doubleList0.indexOf((java.lang.Object) byteList6);
        java.util.Spliterator<java.lang.Double> doubleSpliterator36 = doubleList0.spliterator();
        try {
            java.util.ListIterator<java.lang.Double> doubleItor38 = doubleList0.listIterator(65535);
            org.junit.Assert.fail(
                "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 65535");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-5) + "'", int1.equals((-5)));
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-1) + "'", int10 == (-1));
        org.junit.Assert.assertTrue("'" + int12 + "' != '" + (-1) + "'", int12 == (-1));
        org.junit.Assert.assertNotNull(booleanArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertNotNull(booleanItor18);
        org.junit.Assert.assertNotNull(booleanStream19);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(obj24);
        org.junit.Assert.assertNotNull(uShortStream25);
        org.junit.Assert.assertNotNull(uShortItor26);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
        org.junit.Assert.assertNotNull(obj29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-1) + "'", int35 == (-1));
        org.junit.Assert.assertNotNull(doubleSpliterator36);
    }

    @Test
    public void test533() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test533");
        esa.mo.nmf.NMFException nMFException2 = new esa.mo.nmf.NMFException("false");
        java.lang.Throwable[] throwableArray3 = nMFException2.getSuppressed();
        esa.mo.nmf.NMFException nMFException4 = new esa.mo.nmf.NMFException("[0.0, 100.0]",
            (java.lang.Throwable) nMFException2);
        org.junit.Assert.assertNotNull(throwableArray3);
    }

    @Test
    public void test534() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test534");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        org.ccsds.moims.mo.mal.structures.UShort uShort24 = longList22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort25 = longList22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.StringList stringList26 = new org.ccsds.moims.mo.mal.structures.StringList();
        org.ccsds.moims.mo.mal.structures.Element element27 = stringList26.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort28 = stringList26.getServiceNumber();
        java.lang.Integer int29 = stringList26.getTypeShortForm();
        stringList26.ensureCapacity(13);
        int int32 = longList22.lastIndexOf((java.lang.Object) stringList26);
        longList22.ensureCapacity((-5));
        try {
            longList22.add(10, (java.lang.Long) 281474993487880L);
            org.junit.Assert.fail(
                "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 10, Size: 0");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(uShort24);
        org.junit.Assert.assertNotNull(uShort25);
        org.junit.Assert.assertNotNull(element27);
        org.junit.Assert.assertNotNull(uShort28);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-15) + "'", int29.equals((-15)));
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
    }

    @Test
    public void test535() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test535");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl24 = mCRegistration19.aggregationService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl25 = mCRegistration19.aggregationService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl26 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl27 = mCRegistration19.aggregationService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl28 = mCRegistration19.aggregationService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl24);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl25);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl26);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl27);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl28);
    }

    @Test
    public void test536() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test536");
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.util.ListIterator<java.lang.Double> doubleItor1 = doubleList0.listIterator();
        java.util.ListIterator<java.lang.Double> doubleItor2 = doubleList0.listIterator();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = doubleList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.FloatList floatList4 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long5 = floatList4.getShortForm();
        floatList4.trimToSize();
        java.lang.String str7 = floatList4.toString();
        java.util.Spliterator<java.lang.Float> floatSpliterator8 = floatList4.spliterator();
        java.lang.Byte[] byteArray11 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList12 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList12,
            byteArray11);
        boolean boolean14 = byteList12.isEmpty();
        int int16 = byteList12.indexOf((java.lang.Object) 'a');
        int int18 = byteList12.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray20 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList21 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList21,
            booleanArray20);
        java.util.ListIterator<java.lang.Boolean> booleanItor24 = booleanList21.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream25 = booleanList21.parallelStream();
        boolean boolean27 = booleanList21.add((java.lang.Boolean) false);
        boolean boolean28 = byteList12.retainAll((java.util.Collection<java.lang.Boolean>) booleanList21);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList29 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj30 = uShortList29.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream31 = uShortList29
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor32 = uShortList29.iterator();
        boolean boolean33 = byteList12.equals((java.lang.Object) uShortItor32);
        org.ccsds.moims.mo.mal.structures.ShortList shortList34 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj35 = shortList34.clone();
        boolean boolean36 = byteList12.equals((java.lang.Object) shortList34);
        esa.mo.nmf.NMFException nMFException38 = new esa.mo.nmf.NMFException("-1");
        boolean boolean39 = byteList12.equals((java.lang.Object) nMFException38);
        byteList12.trimToSize();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList41 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element42 = booleanList41.createElement();
        java.lang.Long long43 = booleanList41.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort44 = booleanList41.getAreaNumber();
        java.lang.Integer int45 = booleanList41.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray46 = new org.ccsds.moims.mo.mal.structures.BooleanList[]{booleanList41};
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray47 = byteList12.toArray(booleanListArray46);
        boolean boolean48 = floatList4.remove((java.lang.Object) booleanListArray46);
        boolean boolean49 = doubleList0.remove((java.lang.Object) booleanListArray46);
        java.util.Spliterator<java.lang.Double> doubleSpliterator50 = doubleList0.spliterator();
        boolean boolean51 = doubleList0.isEmpty();
        org.junit.Assert.assertNotNull(doubleItor1);
        org.junit.Assert.assertNotNull(doubleItor2);
        org.junit.Assert.assertNotNull(uOctet3);
        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 281475010265084L + "'", long5.equals(281475010265084L));
        org.junit.Assert.assertTrue("'" + str7 + "' != '" + "[]" + "'", str7.equals("[]"));
        org.junit.Assert.assertNotNull(floatSpliterator8);
        org.junit.Assert.assertNotNull(byteArray11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertTrue("'" + int16 + "' != '" + (-1) + "'", int16 == (-1));
        org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-1) + "'", int18 == (-1));
        org.junit.Assert.assertNotNull(booleanArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(booleanItor24);
        org.junit.Assert.assertNotNull(booleanStream25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertNotNull(obj30);
        org.junit.Assert.assertNotNull(uShortStream31);
        org.junit.Assert.assertNotNull(uShortItor32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertNotNull(obj35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
        org.junit.Assert.assertNotNull(element42);
        org.junit.Assert.assertTrue("'" + long43 + "' != '" + 281475010265086L + "'", long43.equals(281475010265086L));
        org.junit.Assert.assertNotNull(uShort44);
        org.junit.Assert.assertTrue("'" + int45 + "' != '" + (-2) + "'", int45.equals((-2)));
        org.junit.Assert.assertNotNull(booleanListArray46);
        org.junit.Assert.assertNotNull(booleanListArray47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertNotNull(doubleSpliterator50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
    }

    @Test
    public void test537() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test537");
        org.ccsds.moims.mo.mal.structures.ULong uLong0 = new org.ccsds.moims.mo.mal.structures.ULong();
        org.ccsds.moims.mo.mal.structures.UShort uShort1 = uLong0.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = uLong0.getServiceNumber();
        java.lang.Long long3 = uLong0.getShortForm();
        org.ccsds.moims.mo.mal.structures.FloatList floatList4 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long5 = floatList4.getShortForm();
        java.lang.String[] strArray7 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList8 = new java.util.ArrayList<java.lang.String>();
        boolean boolean9 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList8, strArray7);
        int int10 = strList8.size();
        java.lang.Boolean[] booleanArray13 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList14 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList14,
            booleanArray13);
        boolean boolean17 = booleanList14.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream18 = booleanList14.stream();
        boolean boolean19 = strList8.containsAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        boolean boolean20 = floatList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        java.util.ListIterator<java.lang.Float> floatItor21 = floatList4.listIterator();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet22 = floatList4.getAreaVersion();
        boolean boolean23 = uLong0.equals((java.lang.Object) uOctet22);
        java.lang.Long long24 = uLong0.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element25 = uLong0.createElement();
        java.math.BigInteger bigInteger26 = uLong0.getValue();
        org.ccsds.moims.mo.mal.structures.ULong uLong27 = new org.ccsds.moims.mo.mal.structures.ULong(bigInteger26);
        org.ccsds.moims.mo.mal.structures.FloatList floatList28 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long29 = floatList28.getShortForm();
        java.lang.String[] strArray31 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList32 = new java.util.ArrayList<java.lang.String>();
        boolean boolean33 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList32,
            strArray31);
        int int34 = strList32.size();
        java.lang.Boolean[] booleanArray37 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList38 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean39 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList38,
            booleanArray37);
        boolean boolean41 = booleanList38.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream42 = booleanList38.stream();
        boolean boolean43 = strList32.containsAll((java.util.Collection<java.lang.Boolean>) booleanList38);
        boolean boolean44 = floatList28.containsAll((java.util.Collection<java.lang.Boolean>) booleanList38);
        java.util.Spliterator<java.lang.Float> floatSpliterator45 = floatList28.spliterator();
        esa.mo.nmf.NMFException nMFException47 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray48 = nMFException47.getSuppressed();
        int int49 = floatList28.lastIndexOf((java.lang.Object) throwableArray48);
        org.ccsds.moims.mo.mal.structures.LongList longList50 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int51 = floatList28.lastIndexOf((java.lang.Object) longList50);
        org.ccsds.moims.mo.mal.structures.UShort uShort52 = longList50.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort53 = longList50.getServiceNumber();
        boolean boolean54 = uLong27.equals((java.lang.Object) uShort53);
        java.lang.Integer int55 = uLong27.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet56 = uLong27.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort57 = uLong27.getServiceNumber();
        org.junit.Assert.assertNotNull(uShort1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 281474993487886L + "'", long3.equals(281474993487886L));
        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 281475010265084L + "'", long5.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray7);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + 1 + "'", int10 == 1);
        org.junit.Assert.assertNotNull(booleanArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertNotNull(booleanStream18);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertNotNull(floatItor21);
        org.junit.Assert.assertNotNull(uOctet22);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
        org.junit.Assert.assertTrue("'" + long24 + "' != '" + 281474993487886L + "'", long24.equals(281474993487886L));
        org.junit.Assert.assertNotNull(element25);
        org.junit.Assert.assertNotNull(bigInteger26);
        org.junit.Assert.assertTrue("'" + long29 + "' != '" + 281475010265084L + "'", long29.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertTrue("'" + int34 + "' != '" + 1 + "'", int34 == 1);
        org.junit.Assert.assertNotNull(booleanArray37);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertNotNull(booleanStream42);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + false + "'", !boolean43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
        org.junit.Assert.assertNotNull(floatSpliterator45);
        org.junit.Assert.assertNotNull(throwableArray48);
        org.junit.Assert.assertTrue("'" + int49 + "' != '" + (-1) + "'", int49 == (-1));
        org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-1) + "'", int51 == (-1));
        org.junit.Assert.assertNotNull(uShort52);
        org.junit.Assert.assertNotNull(uShort53);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertTrue("'" + int55 + "' != '" + 14 + "'", int55.equals(14));
        org.junit.Assert.assertNotNull(uOctet56);
        org.junit.Assert.assertNotNull(uShort57);
    }

    @Test
    public void test538() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test538");
        org.ccsds.moims.mo.mal.structures.Identifier identifier0 = new org.ccsds.moims.mo.mal.structures.Identifier();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = identifier0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray2 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList3 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean4 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList3, uRIArray2);
        java.lang.Boolean[] booleanArray7 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList8 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean9 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList8,
            booleanArray7);
        java.util.Iterator<java.lang.Boolean> booleanItor10 = booleanList8.iterator();
        boolean boolean11 = uRIList3.retainAll((java.util.Collection<java.lang.Boolean>) booleanList8);
        java.lang.Boolean[] booleanArray14 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList15 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList15,
            booleanArray14);
        boolean boolean18 = booleanList15.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream19 = booleanList15.stream();
        boolean boolean20 = uRIList3.retainAll((java.util.Collection<java.lang.Boolean>) booleanList15);
        uRIList3.ensureCapacity(5);
        boolean boolean23 = identifier0.equals((java.lang.Object) uRIList3);
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor24 = uRIList3.listIterator();
        java.lang.String str25 = uRIList3.toString();
        org.ccsds.moims.mo.mal.structures.Union union27 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Double) 100.0d);
        org.ccsds.moims.mo.mal.structures.UShort uShort28 = union27.getAreaNumber();
        boolean boolean29 = uRIList3.equals((java.lang.Object) union27);
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream30 = uRIList3.parallelStream();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider31 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF32 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl33 = mCServicesProviderNMF32
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl34 = mCServicesProviderNMF32
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl35 = mCServicesProviderNMF32
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl36 = mCServicesProviderNMF32
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF37 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl38 = mCServicesProviderNMF37
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl39 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl40 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl41 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF42 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCServicesProviderNMF42
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCServicesProviderNMF42
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF45 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl46 = mCServicesProviderNMF45
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl47 = mCServicesProviderNMF45
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl48 = mCServicesProviderNMF45
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl49 = mCServicesProviderNMF45
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration50 = new esa.mo.nmf.MCRegistration(cOMServicesProvider31,
            parameterProviderServiceImpl36, aggregationProviderServiceImpl41, alertProviderServiceImpl44,
            actionProviderServiceImpl49);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl51 = mCRegistration50.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl52 = mCRegistration50.aggregationService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl53 = mCRegistration50.actionService;
        boolean boolean54 = uRIList3.remove((java.lang.Object) mCRegistration50);
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider55 = mCRegistration50.comServices;
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uRIArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + false + "'", !boolean4);
        org.junit.Assert.assertNotNull(booleanArray7);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
        org.junit.Assert.assertNotNull(booleanItor10);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertNotNull(booleanArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertNotNull(booleanStream19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
        org.junit.Assert.assertNotNull(uRIItor24);
        org.junit.Assert.assertTrue("'" + str25 + "' != '" + "[]" + "'", str25.equals("[]"));
        org.junit.Assert.assertNotNull(uShort28);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
        org.junit.Assert.assertNotNull(uRIStream30);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl33);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl34);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl35);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl36);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl38);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl39);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl40);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl46);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl47);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl48);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl49);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl51);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl52);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl53);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertNull(cOMServicesProvider55);
    }

    @Test
    public void test539() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test539");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray2 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList3 = new java.util.ArrayList<java.lang.String>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList3, strArray2);
        int int5 = strList3.size();
        java.lang.Boolean[] booleanArray8 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList9 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList9,
            booleanArray8);
        boolean boolean12 = booleanList9.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream13 = booleanList9.stream();
        boolean boolean14 = strList3.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        boolean boolean15 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        java.util.stream.Stream<java.lang.Short> shortStream16 = shortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element17 = shortList0.createElement();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("false");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = shortList0.indexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList22 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.String str23 = doubleList22.toString();
        java.lang.Boolean[] booleanArray25 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList26 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList26,
            booleanArray25);
        java.util.ListIterator<java.lang.Boolean> booleanItor29 = booleanList26.listIterator((int) (short) 1);
        boolean boolean30 = doubleList22.containsAll((java.util.Collection<java.lang.Boolean>) booleanList26);
        java.lang.Byte[] byteArray33 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList34 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList34,
            byteArray33);
        boolean boolean36 = byteList34.isEmpty();
        int int37 = byteList34.size();
        int int38 = booleanList26.indexOf((java.lang.Object) byteList34);
        java.lang.Boolean[] booleanArray41 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList42 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList42,
            booleanArray41);
        java.util.Iterator<java.lang.Boolean> booleanItor44 = booleanList42.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream45 = booleanList42.stream();
        java.util.Iterator<java.lang.Boolean> booleanItor46 = booleanList42.iterator();
        boolean boolean47 = byteList34.removeAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        boolean boolean48 = shortList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        org.ccsds.moims.mo.mal.structures.Element element49 = shortList0.createElement();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList50 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        java.lang.Integer int51 = booleanList50.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element52 = booleanList50.createElement();
        boolean boolean53 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        org.ccsds.moims.mo.mal.structures.OctetList octetList54 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.UShort uShort55 = octetList54.getServiceNumber();
        boolean boolean56 = octetList54.isEmpty();
        org.ccsds.moims.mo.mal.structures.UShort uShort57 = octetList54.getServiceNumber();
        java.lang.Long long58 = octetList54.getShortForm();
        boolean boolean59 = shortList0.contains((java.lang.Object) octetList54);
        org.ccsds.moims.mo.mal.structures.Duration duration60 = new org.ccsds.moims.mo.mal.structures.Duration();
        java.lang.Long long61 = duration60.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element62 = duration60.createElement();
        int int63 = octetList54.indexOf((java.lang.Object) duration60);
        java.lang.Long long64 = duration60.getShortForm();
        org.junit.Assert.assertNotNull(strArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + 1 + "'", int5 == 1);
        org.junit.Assert.assertNotNull(booleanArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(booleanStream13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertNotNull(shortStream16);
        org.junit.Assert.assertNotNull(element17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + str23 + "' != '" + "[]" + "'", str23.equals("[]"));
        org.junit.Assert.assertNotNull(booleanArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(booleanItor29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertNotNull(byteArray33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertTrue("'" + int37 + "' != '" + 2 + "'", int37 == 2);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertNotNull(booleanArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertNotNull(booleanItor44);
        org.junit.Assert.assertNotNull(booleanStream45);
        org.junit.Assert.assertNotNull(booleanItor46);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertNotNull(element49);
        org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-2) + "'", int51.equals((-2)));
        org.junit.Assert.assertNotNull(element52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
        org.junit.Assert.assertNotNull(uShort55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
        org.junit.Assert.assertNotNull(uShort57);
        org.junit.Assert.assertTrue("'" + long58 + "' != '" + 281475010265081L + "'", long58.equals(281475010265081L));
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + false + "'", !boolean59);
        org.junit.Assert.assertTrue("'" + long61 + "' != '" + 281474993487875L + "'", long61.equals(281474993487875L));
        org.junit.Assert.assertNotNull(element62);
        org.junit.Assert.assertTrue("'" + int63 + "' != '" + (-1) + "'", int63 == (-1));
        org.junit.Assert.assertTrue("'" + long64 + "' != '" + 281474993487875L + "'", long64.equals(281474993487875L));
    }

    @Test
    public void test540() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test540");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl25 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl26 = mCServicesProviderNMF23
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl27 = mCServicesProviderNMF23
            .getAggregationService();
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl25);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl26);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl27);
    }

    @Test
    public void test541() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test541");
        org.ccsds.moims.mo.mal.structures.StringList stringList1 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = stringList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList4 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.lang.Long long5 = integerList4.getShortForm();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList7 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        org.ccsds.moims.mo.mal.structures.IntegerList integerList9 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.lang.Long long10 = integerList9.getShortForm();
        org.ccsds.moims.mo.mal.structures.IntegerList[] integerListArray11 = new org.ccsds.moims.mo.mal.structures.IntegerList[]{integerList4,
                                                                                                                                 integerList7,
                                                                                                                                 integerList9};
        org.ccsds.moims.mo.mal.structures.IntegerList[] integerListArray12 = stringList1.toArray(integerListArray11);
        java.lang.Boolean[] booleanArray14 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList15 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList15,
            booleanArray14);
        java.util.ListIterator<java.lang.Boolean> booleanItor18 = booleanList15.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream19 = booleanList15.parallelStream();
        boolean boolean21 = booleanList15.add((java.lang.Boolean) false);
        java.util.ListIterator<java.lang.Boolean> booleanItor23 = booleanList15.listIterator(0);
        boolean boolean24 = stringList1.containsAll((java.util.Collection<java.lang.Boolean>) booleanList15);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList25 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj26 = uShortList25.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream27 = uShortList25
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor28 = uShortList25.iterator();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor29 = uShortList25.listIterator();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider30 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF31 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF31
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl33 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl34 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl35 = mCServicesProviderNMF31
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF36 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl37 = mCServicesProviderNMF36
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl38 = mCServicesProviderNMF36
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl39 = mCServicesProviderNMF36
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl40 = mCServicesProviderNMF36
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF41 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF41
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl43 = mCServicesProviderNMF41
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF44 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl45 = mCServicesProviderNMF44
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl46 = mCServicesProviderNMF44
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl47 = mCServicesProviderNMF44
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl48 = mCServicesProviderNMF44
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration49 = new esa.mo.nmf.MCRegistration(cOMServicesProvider30,
            parameterProviderServiceImpl35, aggregationProviderServiceImpl40, alertProviderServiceImpl43,
            actionProviderServiceImpl48);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl50 = mCRegistration49.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl51 = mCRegistration49.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider52 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF53 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl54 = mCServicesProviderNMF53
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl55 = mCServicesProviderNMF53
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl56 = mCServicesProviderNMF53
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl57 = mCServicesProviderNMF53
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF58 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl59 = mCServicesProviderNMF58
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl60 = mCServicesProviderNMF58
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl61 = mCServicesProviderNMF58
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl62 = mCServicesProviderNMF58
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF63 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl64 = mCServicesProviderNMF63
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl65 = mCServicesProviderNMF63
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF66 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl67 = mCServicesProviderNMF66
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl68 = mCServicesProviderNMF66
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl69 = mCServicesProviderNMF66
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl70 = mCServicesProviderNMF66
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration71 = new esa.mo.nmf.MCRegistration(cOMServicesProvider52,
            parameterProviderServiceImpl57, aggregationProviderServiceImpl62, alertProviderServiceImpl65,
            actionProviderServiceImpl70);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF72 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl73 = mCServicesProviderNMF72
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl74 = mCServicesProviderNMF72
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl75 = mCServicesProviderNMF72
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl76 = mCServicesProviderNMF72
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl77 = mCServicesProviderNMF72
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl78 = mCServicesProviderNMF72
            .getActionService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF79 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl80 = mCServicesProviderNMF79
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl81 = mCServicesProviderNMF79
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl82 = mCServicesProviderNMF79
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl83 = mCServicesProviderNMF79
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl84 = mCServicesProviderNMF79
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl85 = mCServicesProviderNMF79
            .getActionService();
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray86 = new org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[]{actionProviderServiceImpl51,
                                                                                                                                                                                   actionProviderServiceImpl70,
                                                                                                                                                                                   actionProviderServiceImpl78,
                                                                                                                                                                                   actionProviderServiceImpl85};
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray87 = uShortList25
            .toArray(actionInheritanceSkeletonArray86);
        org.ccsds.moims.mo.mc.action.provider.ActionHandler[] actionHandlerArray88 = stringList1.toArray(
            (org.ccsds.moims.mo.mc.action.provider.ActionHandler[]) actionInheritanceSkeletonArray86);
        java.util.Iterator<java.lang.String> strItor89 = stringList1.iterator();
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 281475010265077L + "'", long5.equals(281475010265077L));
        org.junit.Assert.assertTrue("'" + long10 + "' != '" + 281475010265077L + "'", long10.equals(281475010265077L));
        org.junit.Assert.assertNotNull(integerListArray11);
        org.junit.Assert.assertNotNull(integerListArray12);
        org.junit.Assert.assertNotNull(booleanArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertNotNull(booleanItor18);
        org.junit.Assert.assertNotNull(booleanStream19);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
        org.junit.Assert.assertNotNull(booleanItor23);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(obj26);
        org.junit.Assert.assertNotNull(uShortStream27);
        org.junit.Assert.assertNotNull(uShortItor28);
        org.junit.Assert.assertNotNull(uShortItor29);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl33);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl34);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl35);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl37);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl38);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl39);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl40);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl43);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl45);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl46);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl47);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl48);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl50);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl51);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl54);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl55);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl56);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl57);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl59);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl60);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl61);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl62);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl64);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl65);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl67);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl68);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl69);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl70);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl73);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl74);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl75);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl76);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl77);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl78);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl80);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl81);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl82);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl83);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl84);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl85);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray86);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray87);
        org.junit.Assert.assertNotNull(actionHandlerArray88);
        org.junit.Assert.assertNotNull(strItor89);
    }

    @Test
    public void test542() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test542");
        esa.mo.nmf.NMFException nMFException1 = new esa.mo.nmf.NMFException("18.0");
        java.lang.Throwable[] throwableArray2 = nMFException1.getSuppressed();
        esa.mo.nmf.NMFException nMFException5 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray6 = nMFException5.getSuppressed();
        java.lang.Throwable[] throwableArray7 = nMFException5.getSuppressed();
        esa.mo.nmf.NMFException nMFException9 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray10 = nMFException9.getSuppressed();
        nMFException5.addSuppressed((java.lang.Throwable) nMFException9);
        esa.mo.nmf.NMFException nMFException12 = new esa.mo.nmf.NMFException("[true]",
            (java.lang.Throwable) nMFException9);
        nMFException1.addSuppressed((java.lang.Throwable) nMFException9);
        org.junit.Assert.assertNotNull(throwableArray2);
        org.junit.Assert.assertNotNull(throwableArray6);
        org.junit.Assert.assertNotNull(throwableArray7);
        org.junit.Assert.assertNotNull(throwableArray10);
    }

    @Test
    public void test543() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test543");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        org.ccsds.moims.mo.mal.structures.Time time24 = new org.ccsds.moims.mo.mal.structures.Time();
        org.ccsds.moims.mo.mal.structures.Element element25 = time24.createElement();
        boolean boolean26 = longList22.equals((java.lang.Object) time24);
        org.ccsds.moims.mo.mal.structures.UShort uShort27 = time24.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.Element element28 = time24.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort29 = time24.getAreaNumber();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(element25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(uShort27);
        org.junit.Assert.assertNotNull(element28);
        org.junit.Assert.assertNotNull(uShort29);
    }

    @Test
    public void test544() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test544");
        org.ccsds.moims.mo.mal.structures.UInteger uInteger1 = new org.ccsds.moims.mo.mal.structures.UInteger(1L);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = uInteger1.getAreaVersion();
        java.lang.Long long3 = uInteger1.getShortForm();
        java.lang.String str4 = uInteger1.toString();
        long long5 = uInteger1.getValue();
        org.ccsds.moims.mo.mal.structures.UShortList uShortList6 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int7 = uShortList6.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor8 = uShortList6.iterator();
        java.lang.Object[] objArray9 = uShortList6.toArray();
        boolean boolean11 = uShortList6.equals((java.lang.Object) 1);
        java.lang.Byte[] byteArray16 = new java.lang.Byte[]{(byte) 0, (byte) 0, (byte) -1, (byte) 0};
        java.util.ArrayList<java.lang.Byte> byteList17 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean18 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList17,
            byteArray16);
        java.util.Iterator<java.lang.Byte> byteItor19 = byteList17.iterator();
        org.ccsds.moims.mo.mal.structures.Duration duration21 = new org.ccsds.moims.mo.mal.structures.Duration(
            (double) 14);
        java.lang.Long long22 = duration21.getShortForm();
        int int23 = byteList17.indexOf((java.lang.Object) long22);
        int int24 = uShortList6.lastIndexOf((java.lang.Object) byteList17);
        java.lang.Object[] objArray25 = byteList17.toArray();
        java.util.stream.Stream<java.lang.Byte> byteStream26 = byteList17.parallelStream();
        java.lang.Object obj27 = byteList17.clone();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider28 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF29 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl30 = mCServicesProviderNMF29
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl32 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl33 = mCServicesProviderNMF29
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF34 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl35 = mCServicesProviderNMF34
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl36 = mCServicesProviderNMF34
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl37 = mCServicesProviderNMF34
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl38 = mCServicesProviderNMF34
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF39 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl40 = mCServicesProviderNMF39
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl41 = mCServicesProviderNMF39
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF42 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCServicesProviderNMF42
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl44 = mCServicesProviderNMF42
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl45 = mCServicesProviderNMF42
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl46 = mCServicesProviderNMF42
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration47 = new esa.mo.nmf.MCRegistration(cOMServicesProvider28,
            parameterProviderServiceImpl33, aggregationProviderServiceImpl38, alertProviderServiceImpl41,
            actionProviderServiceImpl46);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl48 = mCRegistration47.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl49 = mCRegistration47.actionService;
        int int50 = byteList17.indexOf((java.lang.Object) actionProviderServiceImpl49);
        boolean boolean51 = uInteger1.equals((java.lang.Object) int50);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet52 = uInteger1.getAreaVersion();
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 281474993487884L + "'", long3.equals(281474993487884L));
        org.junit.Assert.assertTrue("'" + str4 + "' != '" + "1" + "'", str4.equals("1"));
        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 1L + "'", long5 == 1L);
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-10) + "'", int7.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor8);
        org.junit.Assert.assertNotNull(objArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertNotNull(byteArray16);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertNotNull(byteItor19);
        org.junit.Assert.assertTrue("'" + long22 + "' != '" + 281474993487875L + "'", long22.equals(281474993487875L));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertTrue("'" + int24 + "' != '" + (-1) + "'", int24 == (-1));
        org.junit.Assert.assertNotNull(objArray25);
        org.junit.Assert.assertNotNull(byteStream26);
        org.junit.Assert.assertNotNull(obj27);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl32);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl33);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl35);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl36);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl37);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl38);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl40);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl44);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl45);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl46);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl48);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl49);
        org.junit.Assert.assertTrue("'" + int50 + "' != '" + (-1) + "'", int50 == (-1));
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + false + "'", !boolean51);
        org.junit.Assert.assertNotNull(uOctet52);
    }

    @Test
    public void test545() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test545");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        java.lang.Integer int22 = floatList0.getTypeShortForm();
        java.util.ListIterator<java.lang.Float> floatItor23 = floatList0.listIterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort24 = floatList0.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList25 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.UShort uShort26 = doubleList25.getServiceNumber();
        boolean boolean28 = doubleList25.add((java.lang.Double) 1.0d);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet29 = doubleList25.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList30 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element31 = booleanList30.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort32 = booleanList30.getAreaNumber();
        java.lang.Long long33 = booleanList30.getShortForm();
        java.util.stream.Stream<java.lang.Boolean> booleanStream34 = booleanList30.parallelStream();
        java.lang.Integer int35 = booleanList30.getTypeShortForm();
        java.lang.String str36 = booleanList30.toString();
        boolean boolean37 = doubleList25.containsAll((java.util.Collection<java.lang.Boolean>) booleanList30);
        boolean boolean38 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList30);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-4) + "'", int22.equals((-4)));
        org.junit.Assert.assertNotNull(floatItor23);
        org.junit.Assert.assertNotNull(uShort24);
        org.junit.Assert.assertNotNull(uShort26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertNotNull(uOctet29);
        org.junit.Assert.assertNotNull(element31);
        org.junit.Assert.assertNotNull(uShort32);
        org.junit.Assert.assertTrue("'" + long33 + "' != '" + 281475010265086L + "'", long33.equals(281475010265086L));
        org.junit.Assert.assertNotNull(booleanStream34);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-2) + "'", int35.equals((-2)));
        org.junit.Assert.assertTrue("'" + str36 + "' != '" + "[]" + "'", str36.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
    }

    @Test
    public void test546() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test546");
        esa.mo.nmf.NMFException nMFException1 = new esa.mo.nmf.NMFException("[-5, -5]");
    }

    @Test
    public void test547() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test547");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl3 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl6 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl7 = mCServicesProviderNMF0
            .getActionService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl6);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl7);
    }

    @Test
    public void test548() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test548");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCRegistration19.parameterService;
        esa.mo.nmf.MCRegistration.RegistrationMode registrationMode21 = esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS;
        mCRegistration19.setMode(registrationMode21);
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider23 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF24 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl25 = mCServicesProviderNMF24
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl26 = mCServicesProviderNMF24
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl27 = mCServicesProviderNMF24
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl28 = mCServicesProviderNMF24
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF29 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl30 = mCServicesProviderNMF29
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl32 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl33 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF34 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl35 = mCServicesProviderNMF34
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl36 = mCServicesProviderNMF34
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF37 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl38 = mCServicesProviderNMF37
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl39 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl40 = mCServicesProviderNMF37
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl41 = mCServicesProviderNMF37
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration42 = new esa.mo.nmf.MCRegistration(cOMServicesProvider23,
            parameterProviderServiceImpl28, aggregationProviderServiceImpl33, alertProviderServiceImpl36,
            actionProviderServiceImpl41);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCRegistration42.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl44 = mCRegistration42.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl45 = mCRegistration42.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl46 = mCRegistration42.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl47 = mCRegistration42.actionService;
        esa.mo.nmf.MCRegistration.RegistrationMode registrationMode48 = esa.mo.nmf.MCRegistration.RegistrationMode.UPDATE_IF_EXISTS;
        mCRegistration42.setMode(registrationMode48);
        mCRegistration19.setMode(registrationMode48);
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl51 = mCRegistration19.aggregationService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertTrue("'" + registrationMode21 + "' != '" +
            esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS + "'", registrationMode21.equals(
                esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS));
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl25);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl26);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl27);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl28);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl32);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl33);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl35);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl36);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl38);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl39);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl40);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl44);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl45);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl46);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl47);
        org.junit.Assert.assertTrue("'" + registrationMode48 + "' != '" +
            esa.mo.nmf.MCRegistration.RegistrationMode.UPDATE_IF_EXISTS + "'", registrationMode48.equals(
                esa.mo.nmf.MCRegistration.RegistrationMode.UPDATE_IF_EXISTS));
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl51);
    }

    @Test
    public void test549() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test549");
        esa.mo.nmf.NMFException nMFException0 = new esa.mo.nmf.NMFException();
        esa.mo.nmf.NMFException nMFException2 = new esa.mo.nmf.NMFException("[10, -1, -1, 10]");
        nMFException0.addSuppressed((java.lang.Throwable) nMFException2);
        esa.mo.nmf.NMFException nMFException5 = new esa.mo.nmf.NMFException("[10, -1, -1, 10]");
        java.lang.Throwable[] throwableArray6 = nMFException5.getSuppressed();
        nMFException0.addSuppressed((java.lang.Throwable) nMFException5);
        org.junit.Assert.assertNotNull(throwableArray6);
    }

    @Test
    public void test550() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test550");
        org.ccsds.moims.mo.mal.structures.URIList uRIList1 = new org.ccsds.moims.mo.mal.structures.URIList(17);
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = uRIList1.getAreaNumber();
        java.lang.Long long3 = uRIList1.getShortForm();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor4 = uRIList1.listIterator();
        java.lang.Boolean[] booleanArray6 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList7 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList7,
            booleanArray6);
        java.util.ListIterator<java.lang.Boolean> booleanItor10 = booleanList7.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream11 = booleanList7.parallelStream();
        boolean boolean13 = booleanList7.add((java.lang.Boolean) false);
        java.lang.Boolean boolean16 = booleanList7.set(0, (java.lang.Boolean) false);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList17 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.Element element18 = doubleList17.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort19 = doubleList17.getServiceNumber();
        doubleList17.ensureCapacity(3);
        doubleList17.trimToSize();
        java.lang.Boolean[] booleanArray25 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList26 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList26,
            booleanArray25);
        boolean boolean29 = booleanList26.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream30 = booleanList26.stream();
        java.lang.Double[] doubleArray32 = new java.lang.Double[]{0.0d};
        java.util.ArrayList<java.lang.Double> doubleList33 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean34 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList33,
            doubleArray32);
        java.util.Spliterator<java.lang.Double> doubleSpliterator35 = doubleList33.spliterator();
        java.util.Iterator<java.lang.Double> doubleItor36 = doubleList33.iterator();
        boolean boolean37 = booleanList26.contains((java.lang.Object) doubleItor36);
        boolean boolean38 = doubleList17.removeAll((java.util.Collection<java.lang.Boolean>) booleanList26);
        booleanList26.trimToSize();
        boolean boolean40 = booleanList7.containsAll((java.util.Collection<java.lang.Boolean>) booleanList26);
        java.lang.Integer[] intArray43 = new java.lang.Integer[]{(-5), (-5)};
        java.util.ArrayList<java.lang.Integer> intList44 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean45 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList44,
            intArray43);
        java.util.Spliterator<java.lang.Integer> intSpliterator46 = intList44.spliterator();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray47 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList48 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean49 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList48, uRIArray47);
        java.lang.Boolean[] booleanArray52 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList53 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean54 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList53,
            booleanArray52);
        java.util.Iterator<java.lang.Boolean> booleanItor55 = booleanList53.iterator();
        boolean boolean56 = uRIList48.retainAll((java.util.Collection<java.lang.Boolean>) booleanList53);
        java.lang.Boolean[] booleanArray59 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList60 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean61 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList60,
            booleanArray59);
        boolean boolean63 = booleanList60.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream64 = booleanList60.stream();
        boolean boolean65 = uRIList48.retainAll((java.util.Collection<java.lang.Boolean>) booleanList60);
        boolean boolean66 = intList44.retainAll((java.util.Collection<java.lang.Boolean>) booleanList60);
        boolean boolean67 = booleanList26.contains((java.lang.Object) booleanList60);
        boolean boolean68 = uRIList1.removeAll((java.util.Collection<java.lang.Boolean>) booleanList26);
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor69 = uRIList1.iterator();
        java.lang.String str70 = uRIList1.toString();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList71 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element72 = booleanList71.createElement();
        java.lang.Long long73 = booleanList71.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element74 = booleanList71.createElement();
        esa.mo.nmf.NMFException nMFException76 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray77 = nMFException76.getSuppressed();
        java.lang.Throwable[] throwableArray78 = nMFException76.getSuppressed();
        java.lang.Throwable[] throwableArray79 = nMFException76.getSuppressed();
        boolean boolean80 = booleanList71.equals((java.lang.Object) throwableArray79);
        boolean boolean81 = uRIList1.retainAll((java.util.Collection<java.lang.Boolean>) booleanList71);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 281475010265070L + "'", long3.equals(281475010265070L));
        org.junit.Assert.assertNotNull(uRIItor4);
        org.junit.Assert.assertNotNull(booleanArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(booleanItor10);
        org.junit.Assert.assertNotNull(booleanStream11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16.equals(true));
        org.junit.Assert.assertNotNull(element18);
        org.junit.Assert.assertNotNull(uShort19);
        org.junit.Assert.assertNotNull(booleanArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertNotNull(booleanStream30);
        org.junit.Assert.assertNotNull(doubleArray32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertNotNull(doubleSpliterator35);
        org.junit.Assert.assertNotNull(doubleItor36);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertNotNull(intArray43);
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
        org.junit.Assert.assertNotNull(intSpliterator46);
        org.junit.Assert.assertNotNull(uRIArray47);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertNotNull(booleanArray52);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
        org.junit.Assert.assertNotNull(booleanItor55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertNotNull(booleanArray59);
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + true + "'", boolean61);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertNotNull(booleanStream64);
        org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
        org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + true + "'", boolean66);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + false + "'", !boolean67);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + false + "'", !boolean68);
        org.junit.Assert.assertNotNull(uRIItor69);
        org.junit.Assert.assertTrue("'" + str70 + "' != '" + "[]" + "'", str70.equals("[]"));
        org.junit.Assert.assertNotNull(element72);
        org.junit.Assert.assertTrue("'" + long73 + "' != '" + 281475010265086L + "'", long73.equals(281475010265086L));
        org.junit.Assert.assertNotNull(element74);
        org.junit.Assert.assertNotNull(throwableArray77);
        org.junit.Assert.assertNotNull(throwableArray78);
        org.junit.Assert.assertNotNull(throwableArray79);
        org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + false + "'", !boolean80);
        org.junit.Assert.assertTrue("'" + boolean81 + "' != '" + false + "'", !boolean81);
    }

    @Test
    public void test551() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test551");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl23 = mCRegistration19.aggregationService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl23);
    }

    @Test
    public void test552() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test552");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray2 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList3 = new java.util.ArrayList<java.lang.String>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList3, strArray2);
        int int5 = strList3.size();
        java.lang.Boolean[] booleanArray8 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList9 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList9,
            booleanArray8);
        boolean boolean12 = booleanList9.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream13 = booleanList9.stream();
        boolean boolean14 = strList3.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        boolean boolean15 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        java.util.ListIterator<java.lang.Short> shortItor16 = shortList0.listIterator();
        java.lang.Integer int17 = shortList0.getTypeShortForm();
        java.lang.Object obj18 = shortList0.clone();
        org.ccsds.moims.mo.mal.structures.UShort uShort19 = shortList0.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.LongList longList21 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet22 = longList21.getAreaVersion();
        java.util.Iterator<java.lang.Long> longItor23 = longList21.iterator();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF24 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl25 = mCServicesProviderNMF24
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl26 = mCServicesProviderNMF24
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl27 = mCServicesProviderNMF24
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl28 = mCServicesProviderNMF24
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl29 = mCServicesProviderNMF24
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl30 = mCServicesProviderNMF24
            .getParameterService();
        int int31 = longList21.indexOf((java.lang.Object) mCServicesProviderNMF24);
        longList21.trimToSize();
        java.lang.String str33 = longList21.toString();
        longList21.trimToSize();
        boolean boolean35 = shortList0.contains((java.lang.Object) longList21);
        org.junit.Assert.assertNotNull(strArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + 1 + "'", int5 == 1);
        org.junit.Assert.assertNotNull(booleanArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(booleanStream13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertNotNull(shortItor16);
        org.junit.Assert.assertTrue("'" + int17 + "' != '" + (-9) + "'", int17.equals((-9)));
        org.junit.Assert.assertNotNull(obj18);
        org.junit.Assert.assertNotNull(uShort19);
        org.junit.Assert.assertNotNull(uOctet22);
        org.junit.Assert.assertNotNull(longItor23);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl25);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl26);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl27);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl28);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl29);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl30);
        org.junit.Assert.assertTrue("'" + int31 + "' != '" + (-1) + "'", int31 == (-1));
        org.junit.Assert.assertTrue("'" + str33 + "' != '" + "[]" + "'", str33.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
    }

    @Test
    public void test553() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test553");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        org.ccsds.moims.mo.mal.structures.LongList longList25 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        java.util.stream.Stream<java.lang.Long> longStream26 = longList25.parallelStream();
        org.ccsds.moims.mo.mal.structures.UShort uShort27 = longList25.getServiceNumber();
        int int28 = floatList0.indexOf((java.lang.Object) longList25);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet29 = floatList0.getAreaVersion();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(longStream26);
        org.junit.Assert.assertNotNull(uShort27);
        org.junit.Assert.assertTrue("'" + int28 + "' != '" + (-1) + "'", int28 == (-1));
        org.junit.Assert.assertNotNull(uOctet29);
    }

    @Test
    public void test554() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test554");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        java.lang.Integer int22 = floatList0.getTypeShortForm();
        java.util.Spliterator<java.lang.Float> floatSpliterator23 = floatList0.spliterator();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet24 = floatList0.getAreaVersion();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-4) + "'", int22.equals((-4)));
        org.junit.Assert.assertNotNull(floatSpliterator23);
        org.junit.Assert.assertNotNull(uOctet24);
    }

    @Test
    public void test555() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test555");
        org.ccsds.moims.mo.mal.structures.Time time1 = new org.ccsds.moims.mo.mal.structures.Time((long) (byte) -1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = time1.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort3 = time1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.Duration duration5 = new org.ccsds.moims.mo.mal.structures.Duration(
            (double) 14);
        boolean boolean6 = time1.equals((java.lang.Object) duration5);
        java.lang.Integer int7 = duration5.getTypeShortForm();
        java.lang.Integer int8 = duration5.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.ShortList shortList9 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj10 = shortList9.clone();
        java.lang.Long long11 = shortList9.getShortForm();
        java.util.stream.Stream<java.lang.Short> shortStream12 = shortList9.stream();
        boolean boolean13 = duration5.equals((java.lang.Object) shortList9);
        shortList9.ensureCapacity(9);
        java.lang.Integer int16 = shortList9.getTypeShortForm();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider17 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF18 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl19 = mCServicesProviderNMF18
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl20 = mCServicesProviderNMF18
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCServicesProviderNMF18
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCServicesProviderNMF18
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl24 = mCServicesProviderNMF23
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl25 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl26 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl27 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF28 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl29 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl30 = mCServicesProviderNMF28
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF31 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF31
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl33 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl34 = mCServicesProviderNMF31
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl35 = mCServicesProviderNMF31
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration36 = new esa.mo.nmf.MCRegistration(cOMServicesProvider17,
            parameterProviderServiceImpl22, aggregationProviderServiceImpl27, alertProviderServiceImpl30,
            actionProviderServiceImpl35);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl37 = mCRegistration36.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl38 = mCRegistration36.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCRegistration36.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl40 = mCRegistration36.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl41 = mCRegistration36.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCRegistration36.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCRegistration36.parameterService;
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCRegistration36.alertService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl45 = mCRegistration36.aggregationService;
        int int46 = shortList9.lastIndexOf((java.lang.Object) aggregationProviderServiceImpl45);
        try {
            java.util.ListIterator<java.lang.Short> shortItor48 = shortList9.listIterator(12);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 12");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(uShort3);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + false + "'", !boolean6);
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + 3 + "'", int7.equals(3));
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + 3 + "'", int8.equals(3));
        org.junit.Assert.assertNotNull(obj10);
        org.junit.Assert.assertTrue("'" + long11 + "' != '" + 281475010265079L + "'", long11.equals(281475010265079L));
        org.junit.Assert.assertNotNull(shortStream12);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
        org.junit.Assert.assertTrue("'" + int16 + "' != '" + (-9) + "'", int16.equals((-9)));
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl19);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl24);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl25);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl26);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl27);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl29);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl30);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl33);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl34);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl35);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl37);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl38);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl40);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl45);
        org.junit.Assert.assertTrue("'" + int46 + "' != '" + (-1) + "'", int46 == (-1));
    }

    @Test
    public void test556() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test556");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl21 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl22 = mCRegistration19.aggregationService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl21);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl22);
    }

    @Test
    public void test557() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test557");
        esa.mo.nmf.NMFException nMFException1 = new esa.mo.nmf.NMFException("14");
    }

    @Test
    public void test558() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test558");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.Element element22 = floatList0.createElement();
        floatList0.clear();
        java.lang.Object obj24 = floatList0.clone();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertNotNull(element22);
        org.junit.Assert.assertNotNull(obj24);
    }

    @Test
    public void test559() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test559");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int1 = uShortList0.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor2 = uShortList0.iterator();
        java.lang.Object[] objArray3 = uShortList0.toArray();
        boolean boolean5 = uShortList0.equals((java.lang.Object) 1);
        java.lang.Byte[] byteArray10 = new java.lang.Byte[]{(byte) 0, (byte) 0, (byte) -1, (byte) 0};
        java.util.ArrayList<java.lang.Byte> byteList11 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList11,
            byteArray10);
        java.util.Iterator<java.lang.Byte> byteItor13 = byteList11.iterator();
        org.ccsds.moims.mo.mal.structures.Duration duration15 = new org.ccsds.moims.mo.mal.structures.Duration(
            (double) 14);
        java.lang.Long long16 = duration15.getShortForm();
        int int17 = byteList11.indexOf((java.lang.Object) long16);
        int int18 = uShortList0.lastIndexOf((java.lang.Object) byteList11);
        java.lang.Byte[] byteArray23 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList24 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean25 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList24,
            byteArray23);
        boolean boolean27 = byteList24.add((java.lang.Byte) (byte) 10);
        java.lang.String str28 = byteList24.toString();
        java.util.stream.Stream<java.lang.Byte> byteStream29 = byteList24.parallelStream();
        int int30 = byteList24.size();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray31 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList32 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean33 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList32, uRIArray31);
        java.lang.Boolean[] booleanArray36 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList37 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean38 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList37,
            booleanArray36);
        java.util.Iterator<java.lang.Boolean> booleanItor39 = booleanList37.iterator();
        boolean boolean40 = uRIList32.retainAll((java.util.Collection<java.lang.Boolean>) booleanList37);
        booleanList37.clear();
        boolean boolean42 = byteList24.removeAll((java.util.Collection<java.lang.Boolean>) booleanList37);
        java.util.stream.Stream<java.lang.Boolean> booleanStream43 = booleanList37.stream();
        int int44 = uShortList0.lastIndexOf((java.lang.Object) booleanList37);
        java.util.ListIterator<java.lang.Boolean> booleanItor45 = booleanList37.listIterator();
        org.ccsds.moims.mo.mal.structures.LongList longList47 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet48 = longList47.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort49 = longList47.getAreaNumber();
        java.util.Spliterator<java.lang.Long> longSpliterator50 = longList47.spliterator();
        int int51 = booleanList37.lastIndexOf((java.lang.Object) longList47);
        org.ccsds.moims.mo.mal.structures.URIList uRIList53 = new org.ccsds.moims.mo.mal.structures.URIList(17);
        org.ccsds.moims.mo.mal.structures.Element element54 = uRIList53.createElement();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor55 = uRIList53.iterator();
        int int56 = longList47.lastIndexOf((java.lang.Object) uRIList53);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet57 = uRIList53.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.FloatList floatList58 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long59 = floatList58.getShortForm();
        java.lang.String[] strArray61 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList62 = new java.util.ArrayList<java.lang.String>();
        boolean boolean63 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList62,
            strArray61);
        int int64 = strList62.size();
        java.lang.Boolean[] booleanArray67 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList68 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean69 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList68,
            booleanArray67);
        boolean boolean71 = booleanList68.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream72 = booleanList68.stream();
        boolean boolean73 = strList62.containsAll((java.util.Collection<java.lang.Boolean>) booleanList68);
        boolean boolean74 = floatList58.containsAll((java.util.Collection<java.lang.Boolean>) booleanList68);
        java.util.Spliterator<java.lang.Float> floatSpliterator75 = floatList58.spliterator();
        esa.mo.nmf.NMFException nMFException77 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray78 = nMFException77.getSuppressed();
        int int79 = floatList58.lastIndexOf((java.lang.Object) throwableArray78);
        org.ccsds.moims.mo.mal.structures.LongList longList80 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int81 = floatList58.lastIndexOf((java.lang.Object) longList80);
        org.ccsds.moims.mo.mal.structures.UShort uShort82 = longList80.getServiceNumber();
        boolean boolean83 = uRIList53.remove((java.lang.Object) uShort82);
        java.lang.Long long84 = uRIList53.getShortForm();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet85 = uRIList53.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.Identifier identifier87 = new org.ccsds.moims.mo.mal.structures.Identifier(
            "[100, -1]");
        boolean boolean88 = uRIList53.contains((java.lang.Object) identifier87);
        org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-10) + "'", int1.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor2);
        org.junit.Assert.assertNotNull(objArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertNotNull(byteArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(byteItor13);
        org.junit.Assert.assertTrue("'" + long16 + "' != '" + 281474993487875L + "'", long16.equals(281474993487875L));
        org.junit.Assert.assertTrue("'" + int17 + "' != '" + (-1) + "'", int17 == (-1));
        org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-1) + "'", int18 == (-1));
        org.junit.Assert.assertNotNull(byteArray23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertTrue("'" + str28 + "' != '" + "[-1, -1, 1, 1, 10]" + "'", str28.equals(
            "[-1, -1, 1, 1, 10]"));
        org.junit.Assert.assertNotNull(byteStream29);
        org.junit.Assert.assertTrue("'" + int30 + "' != '" + 5 + "'", int30 == 5);
        org.junit.Assert.assertNotNull(uRIArray31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertNotNull(booleanArray36);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertNotNull(booleanItor39);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + false + "'", !boolean42);
        org.junit.Assert.assertNotNull(booleanStream43);
        org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-1) + "'", int44 == (-1));
        org.junit.Assert.assertNotNull(booleanItor45);
        org.junit.Assert.assertNotNull(uOctet48);
        org.junit.Assert.assertNotNull(uShort49);
        org.junit.Assert.assertNotNull(longSpliterator50);
        org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-1) + "'", int51 == (-1));
        org.junit.Assert.assertNotNull(element54);
        org.junit.Assert.assertNotNull(uRIItor55);
        org.junit.Assert.assertTrue("'" + int56 + "' != '" + (-1) + "'", int56 == (-1));
        org.junit.Assert.assertNotNull(uOctet57);
        org.junit.Assert.assertTrue("'" + long59 + "' != '" + 281475010265084L + "'", long59.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray61);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertTrue("'" + int64 + "' != '" + 1 + "'", int64 == 1);
        org.junit.Assert.assertNotNull(booleanArray67);
        org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + true + "'", boolean69);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
        org.junit.Assert.assertNotNull(booleanStream72);
        org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + false + "'", !boolean73);
        org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + false + "'", !boolean74);
        org.junit.Assert.assertNotNull(floatSpliterator75);
        org.junit.Assert.assertNotNull(throwableArray78);
        org.junit.Assert.assertTrue("'" + int79 + "' != '" + (-1) + "'", int79 == (-1));
        org.junit.Assert.assertTrue("'" + int81 + "' != '" + (-1) + "'", int81 == (-1));
        org.junit.Assert.assertNotNull(uShort82);
        org.junit.Assert.assertTrue("'" + boolean83 + "' != '" + false + "'", !boolean83);
        org.junit.Assert.assertTrue("'" + long84 + "' != '" + 281475010265070L + "'", long84.equals(281475010265070L));
        org.junit.Assert.assertNotNull(uOctet85);
        org.junit.Assert.assertTrue("'" + boolean88 + "' != '" + false + "'", !boolean88);
    }

    @Test
    public void test560() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test560");
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.Element element1 = octetList0.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = org.ccsds.moims.mo.mal.structures.DoubleList.SERVICE_SHORT_FORM;
        int int3 = octetList0.lastIndexOf((java.lang.Object) uShort2);
        java.util.Iterator<java.lang.Byte> byteItor4 = octetList0.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort5 = octetList0.getServiceNumber();
        java.lang.Object[] objArray6 = octetList0.toArray();
        boolean boolean8 = octetList0.add((java.lang.Byte) (byte) 10);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF9 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF9
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl11 = mCServicesProviderNMF9
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl12 = mCServicesProviderNMF9
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl13 = mCServicesProviderNMF9
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl14 = mCServicesProviderNMF9
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF9
            .getParameterService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl16 = mCServicesProviderNMF9
            .getParameterService();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider17 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF18 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl19 = mCServicesProviderNMF18
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl20 = mCServicesProviderNMF18
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCServicesProviderNMF18
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCServicesProviderNMF18
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl24 = mCServicesProviderNMF23
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl25 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl26 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl27 = mCServicesProviderNMF23
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF28 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl29 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl30 = mCServicesProviderNMF28
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF31 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF31
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl33 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl34 = mCServicesProviderNMF31
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl35 = mCServicesProviderNMF31
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration36 = new esa.mo.nmf.MCRegistration(cOMServicesProvider17,
            parameterProviderServiceImpl22, aggregationProviderServiceImpl27, alertProviderServiceImpl30,
            actionProviderServiceImpl35);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl37 = mCRegistration36.parameterService;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF38 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl40 = mCServicesProviderNMF38
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl41 = mCServicesProviderNMF38
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl43 = mCServicesProviderNMF38
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl44 = mCServicesProviderNMF38
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl45 = mCServicesProviderNMF38
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl46 = mCServicesProviderNMF38
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl47 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider48 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF49 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl50 = mCServicesProviderNMF49
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl51 = mCServicesProviderNMF49
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl52 = mCServicesProviderNMF49
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl53 = mCServicesProviderNMF49
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF54 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl55 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl56 = mCServicesProviderNMF54
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl57 = mCServicesProviderNMF54
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl58 = mCServicesProviderNMF54
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF59 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl60 = mCServicesProviderNMF59
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl61 = mCServicesProviderNMF59
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF62 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl63 = mCServicesProviderNMF62
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl64 = mCServicesProviderNMF62
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl65 = mCServicesProviderNMF62
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl66 = mCServicesProviderNMF62
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration67 = new esa.mo.nmf.MCRegistration(cOMServicesProvider48,
            parameterProviderServiceImpl53, aggregationProviderServiceImpl58, alertProviderServiceImpl61,
            actionProviderServiceImpl66);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl68 = mCRegistration67.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl69 = mCRegistration67.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl70 = mCRegistration67.parameterService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider71 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF72 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl73 = mCServicesProviderNMF72
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl74 = mCServicesProviderNMF72
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl75 = mCServicesProviderNMF72
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl76 = mCServicesProviderNMF72
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF77 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl78 = mCServicesProviderNMF77
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl79 = mCServicesProviderNMF77
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl80 = mCServicesProviderNMF77
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl81 = mCServicesProviderNMF77
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF82 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl83 = mCServicesProviderNMF82
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl84 = mCServicesProviderNMF82
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF85 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl86 = mCServicesProviderNMF85
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl87 = mCServicesProviderNMF85
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl88 = mCServicesProviderNMF85
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl89 = mCServicesProviderNMF85
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration90 = new esa.mo.nmf.MCRegistration(cOMServicesProvider71,
            parameterProviderServiceImpl76, aggregationProviderServiceImpl81, alertProviderServiceImpl84,
            actionProviderServiceImpl89);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl91 = mCRegistration90.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl92 = mCRegistration90.aggregationService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl93 = mCRegistration90.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl[] parameterProviderServiceImplArray94 = new esa.mo.mc.impl.provider.ParameterProviderServiceImpl[]{parameterProviderServiceImpl16,
                                                                                                                                                                parameterProviderServiceImpl37,
                                                                                                                                                                parameterProviderServiceImpl47,
                                                                                                                                                                parameterProviderServiceImpl70,
                                                                                                                                                                parameterProviderServiceImpl93};
        try {
            esa.mo.mc.impl.provider.ParameterProviderServiceImpl[] parameterProviderServiceImplArray95 = octetList0
                .toArray(parameterProviderServiceImplArray94);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayStoreException; message: null");
        } catch (java.lang.ArrayStoreException e) {
        }
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-1) + "'", int3 == (-1));
        org.junit.Assert.assertNotNull(byteItor4);
        org.junit.Assert.assertNotNull(uShort5);
        org.junit.Assert.assertNotNull(objArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl11);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl12);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl13);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl14);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl16);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl19);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl24);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl25);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl26);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl27);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl29);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl30);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl33);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl34);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl35);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl37);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl40);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl43);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl44);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl45);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl46);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl47);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl50);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl51);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl52);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl53);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl55);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl56);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl57);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl58);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl60);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl61);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl63);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl64);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl65);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl66);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl68);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl69);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl70);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl73);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl74);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl75);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl76);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl78);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl79);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl80);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl81);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl83);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl84);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl86);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl87);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl88);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl89);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl91);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl92);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl93);
        org.junit.Assert.assertNotNull(parameterProviderServiceImplArray94);
    }

    @Test
    public void test561() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test561");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        org.ccsds.moims.mo.mal.structures.UShort uShort17 = org.ccsds.moims.mo.mal.structures.LongList.AREA_SHORT_FORM;
        java.lang.Long long18 = uShort17.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort19 = uShort17.getServiceNumber();
        boolean boolean20 = floatList0.remove((java.lang.Object) uShort17);
        java.util.Spliterator<java.lang.Float> floatSpliterator21 = floatList0.spliterator();
        org.ccsds.moims.mo.mal.structures.FloatList floatList22 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long23 = floatList22.getShortForm();
        java.lang.String[] strArray25 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList26 = new java.util.ArrayList<java.lang.String>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList26,
            strArray25);
        int int28 = strList26.size();
        java.lang.Boolean[] booleanArray31 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList32 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean33 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList32,
            booleanArray31);
        boolean boolean35 = booleanList32.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream36 = booleanList32.stream();
        boolean boolean37 = strList26.containsAll((java.util.Collection<java.lang.Boolean>) booleanList32);
        boolean boolean38 = floatList22.containsAll((java.util.Collection<java.lang.Boolean>) booleanList32);
        java.util.ListIterator<java.lang.Float> floatItor39 = floatList22.listIterator();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet40 = floatList22.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort41 = floatList22.getServiceNumber();
        floatList22.trimToSize();
        boolean boolean43 = floatList0.contains((java.lang.Object) floatList22);
        java.lang.Byte[] byteArray48 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList49 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean50 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList49,
            byteArray48);
        boolean boolean52 = byteList49.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj53 = byteList49.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream54 = byteList49.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList56 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int57 = byteList49.indexOf((java.lang.Object) stringList56);
        byte[] byteArray61 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob62 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray61);
        int int63 = blob62.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob64 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean65 = blob62.equals((java.lang.Object) blob64);
        boolean boolean66 = stringList56.remove((java.lang.Object) blob64);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF67 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean68 = stringList56.equals((java.lang.Object) mCServicesProviderNMF67);
        java.util.ListIterator<java.lang.String> strItor69 = stringList56.listIterator();
        java.util.stream.Stream<java.lang.String> strStream70 = stringList56.stream();
        boolean boolean71 = stringList56.isEmpty();
        java.util.stream.Stream<java.lang.String> strStream72 = stringList56.parallelStream();
        int int73 = floatList0.lastIndexOf((java.lang.Object) strStream72);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(uShort17);
        org.junit.Assert.assertTrue("'" + long18 + "' != '" + 281474993487882L + "'", long18.equals(281474993487882L));
        org.junit.Assert.assertNotNull(uShort19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertNotNull(floatSpliterator21);
        org.junit.Assert.assertTrue("'" + long23 + "' != '" + 281475010265084L + "'", long23.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertTrue("'" + int28 + "' != '" + 1 + "'", int28 == 1);
        org.junit.Assert.assertNotNull(booleanArray31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertNotNull(booleanStream36);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertNotNull(floatItor39);
        org.junit.Assert.assertNotNull(uOctet40);
        org.junit.Assert.assertNotNull(uShort41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + false + "'", !boolean43);
        org.junit.Assert.assertNotNull(byteArray48);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + true + "'", boolean50);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
        org.junit.Assert.assertNotNull(obj53);
        org.junit.Assert.assertNotNull(byteStream54);
        org.junit.Assert.assertTrue("'" + int57 + "' != '" + (-1) + "'", int57 == (-1));
        org.junit.Assert.assertNotNull(byteArray61);
        org.junit.Assert.assertTrue("'" + int63 + "' != '" + 3 + "'", int63 == 3);
        org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
        org.junit.Assert.assertTrue("'" + boolean66 + "' != '" + false + "'", !boolean66);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + false + "'", !boolean68);
        org.junit.Assert.assertNotNull(strItor69);
        org.junit.Assert.assertNotNull(strStream70);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
        org.junit.Assert.assertNotNull(strStream72);
        org.junit.Assert.assertTrue("'" + int73 + "' != '" + (-1) + "'", int73 == (-1));
    }

    @Test
    public void test562() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test562");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl3 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl6 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl7 = mCServicesProviderNMF0
            .getAggregationService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl6);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl7);
    }

    @Test
    public void test563() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test563");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        org.ccsds.moims.mo.mal.structures.URI uRI17 = new org.ccsds.moims.mo.mal.structures.URI();
        int int18 = floatList0.lastIndexOf((java.lang.Object) uRI17);
        org.ccsds.moims.mo.mal.structures.UShort uShort19 = uRI17.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.Element element20 = uRI17.createElement();
        java.lang.Integer int21 = uRI17.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Blob blob22 = new org.ccsds.moims.mo.mal.structures.Blob();
        java.lang.Long long23 = blob22.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort24 = blob22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort25 = blob22.getServiceNumber();
        boolean boolean26 = uRI17.equals((java.lang.Object) uShort25);
        java.lang.String[] strArray28 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList29 = new java.util.ArrayList<java.lang.String>();
        boolean boolean30 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList29,
            strArray28);
        boolean boolean32 = strList29.add("hi!");
        org.ccsds.moims.mo.mal.structures.FloatList floatList33 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long34 = floatList33.getShortForm();
        java.lang.String[] strArray36 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList37 = new java.util.ArrayList<java.lang.String>();
        boolean boolean38 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList37,
            strArray36);
        int int39 = strList37.size();
        java.lang.Boolean[] booleanArray42 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList43 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean44 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList43,
            booleanArray42);
        boolean boolean46 = booleanList43.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream47 = booleanList43.stream();
        boolean boolean48 = strList37.containsAll((java.util.Collection<java.lang.Boolean>) booleanList43);
        boolean boolean49 = floatList33.containsAll((java.util.Collection<java.lang.Boolean>) booleanList43);
        org.ccsds.moims.mo.mal.structures.URI uRI50 = new org.ccsds.moims.mo.mal.structures.URI();
        int int51 = floatList33.lastIndexOf((java.lang.Object) uRI50);
        org.ccsds.moims.mo.mal.structures.UShort uShort52 = uRI50.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet53 = uRI50.getAreaVersion();
        boolean boolean54 = strList29.equals((java.lang.Object) uOctet53);
        java.util.ListIterator<java.lang.String> strItor55 = strList29.listIterator();
        boolean boolean57 = strList29.add("281474993487883");
        boolean boolean58 = uRI17.equals((java.lang.Object) boolean57);
        java.lang.Byte[] byteArray61 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList62 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean63 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList62,
            byteArray61);
        boolean boolean64 = byteList62.isEmpty();
        int int66 = byteList62.indexOf((java.lang.Object) 'a');
        int int68 = byteList62.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray70 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList71 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean72 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList71,
            booleanArray70);
        java.util.ListIterator<java.lang.Boolean> booleanItor74 = booleanList71.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream75 = booleanList71.parallelStream();
        boolean boolean77 = booleanList71.add((java.lang.Boolean) false);
        boolean boolean78 = byteList62.retainAll((java.util.Collection<java.lang.Boolean>) booleanList71);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList79 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj80 = uShortList79.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream81 = uShortList79
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor82 = uShortList79.iterator();
        boolean boolean83 = byteList62.equals((java.lang.Object) uShortItor82);
        org.ccsds.moims.mo.mal.structures.ShortList shortList84 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj85 = shortList84.clone();
        boolean boolean86 = byteList62.equals((java.lang.Object) shortList84);
        esa.mo.nmf.NMFException nMFException88 = new esa.mo.nmf.NMFException("-1");
        boolean boolean89 = byteList62.equals((java.lang.Object) nMFException88);
        byteList62.trimToSize();
        boolean boolean91 = uRI17.equals((java.lang.Object) byteList62);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-1) + "'", int18 == (-1));
        org.junit.Assert.assertNotNull(uShort19);
        org.junit.Assert.assertNotNull(element20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + 18 + "'", int21.equals(18));
        org.junit.Assert.assertTrue("'" + long23 + "' != '" + 281474993487873L + "'", long23.equals(281474993487873L));
        org.junit.Assert.assertNotNull(uShort24);
        org.junit.Assert.assertNotNull(uShort25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(strArray28);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + long34 + "' != '" + 281475010265084L + "'", long34.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray36);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertTrue("'" + int39 + "' != '" + 1 + "'", int39 == 1);
        org.junit.Assert.assertNotNull(booleanArray42);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertNotNull(booleanStream47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-1) + "'", int51 == (-1));
        org.junit.Assert.assertNotNull(uShort52);
        org.junit.Assert.assertNotNull(uOctet53);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertNotNull(strItor55);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
        org.junit.Assert.assertNotNull(byteArray61);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + false + "'", !boolean64);
        org.junit.Assert.assertTrue("'" + int66 + "' != '" + (-1) + "'", int66 == (-1));
        org.junit.Assert.assertTrue("'" + int68 + "' != '" + (-1) + "'", int68 == (-1));
        org.junit.Assert.assertNotNull(booleanArray70);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + true + "'", boolean72);
        org.junit.Assert.assertNotNull(booleanItor74);
        org.junit.Assert.assertNotNull(booleanStream75);
        org.junit.Assert.assertTrue("'" + boolean77 + "' != '" + true + "'", boolean77);
        org.junit.Assert.assertTrue("'" + boolean78 + "' != '" + true + "'", boolean78);
        org.junit.Assert.assertNotNull(obj80);
        org.junit.Assert.assertNotNull(uShortStream81);
        org.junit.Assert.assertNotNull(uShortItor82);
        org.junit.Assert.assertTrue("'" + boolean83 + "' != '" + false + "'", !boolean83);
        org.junit.Assert.assertNotNull(obj85);
        org.junit.Assert.assertTrue("'" + boolean86 + "' != '" + true + "'", boolean86);
        org.junit.Assert.assertTrue("'" + boolean89 + "' != '" + false + "'", !boolean89);
        org.junit.Assert.assertTrue("'" + boolean91 + "' != '" + false + "'", !boolean91);
    }

    @Test
    public void test564() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test564");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        java.util.stream.Stream<java.lang.String> strStream25 = stringList12.parallelStream();
        java.lang.Long[] longArray30 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList31 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean32 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList31,
            longArray30);
        java.lang.Object obj33 = null;
        boolean boolean34 = longList31.contains(obj33);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger36 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean37 = longList31.contains((java.lang.Object) (byte) 100);
        java.util.Iterator<java.lang.Long> longItor38 = longList31.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort39 = org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
        boolean boolean40 = longList31.remove((java.lang.Object) uShort39);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList41 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int42 = uShortList41.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor43 = uShortList41.iterator();
        java.lang.Object[] objArray44 = uShortList41.toArray();
        uShortList41.clear();
        uShortList41.clear();
        boolean boolean47 = uShortList41.isEmpty();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor48 = uShortList41.iterator();
        boolean boolean49 = longList31.contains((java.lang.Object) uShortList41);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList50 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.UShort uShort51 = booleanList50.getAreaNumber();
        boolean boolean52 = longList31.retainAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        java.lang.Integer int53 = booleanList50.getTypeShortForm();
        boolean boolean55 = booleanList50.add((java.lang.Boolean) true);
        boolean boolean56 = stringList12.retainAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        org.ccsds.moims.mo.mal.structures.UShort uShort57 = booleanList50.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet58 = booleanList50.getAreaVersion();
        java.util.ListIterator<java.lang.Boolean> booleanItor60 = booleanList50.listIterator(0);
        java.lang.Integer int61 = booleanList50.getTypeShortForm();
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(strStream25);
        org.junit.Assert.assertNotNull(longArray30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertNotNull(longItor38);
        org.junit.Assert.assertNotNull(uShort39);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertTrue("'" + int42 + "' != '" + (-10) + "'", int42.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor43);
        org.junit.Assert.assertNotNull(objArray44);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
        org.junit.Assert.assertNotNull(uShortItor48);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertNotNull(uShort51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
        org.junit.Assert.assertTrue("'" + int53 + "' != '" + (-2) + "'", int53.equals((-2)));
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertNotNull(uShort57);
        org.junit.Assert.assertNotNull(uOctet58);
        org.junit.Assert.assertNotNull(booleanItor60);
        org.junit.Assert.assertTrue("'" + int61 + "' != '" + (-2) + "'", int61.equals((-2)));
    }

    @Test
    public void test565() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test565");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        java.util.ListIterator<java.lang.String> strItor25 = stringList12.listIterator();
        java.util.stream.Stream<java.lang.String> strStream26 = stringList12.stream();
        java.util.Iterator<java.lang.String> strItor27 = stringList12.iterator();
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(strItor25);
        org.junit.Assert.assertNotNull(strStream26);
        org.junit.Assert.assertNotNull(strItor27);
    }

    @Test
    public void test566() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test566");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCRegistration19.parameterService;
        esa.mo.nmf.MCRegistration.RegistrationMode registrationMode21 = esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS;
        mCRegistration19.setMode(registrationMode21);
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider23 = mCRegistration19.comServices;
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl24 = mCRegistration19.alertService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl25 = mCRegistration19.aggregationService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertTrue("'" + registrationMode21 + "' != '" +
            esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS + "'", registrationMode21.equals(
                esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS));
        org.junit.Assert.assertNull(cOMServicesProvider23);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl24);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl25);
    }

    @Test
    public void test567() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test567");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream2 = uShortList0.parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor3 = uShortList0.iterator();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor4 = uShortList0.listIterator();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider5 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl13 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl14 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl15 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF16 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl17 = mCServicesProviderNMF16
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl18 = mCServicesProviderNMF16
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF19 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCServicesProviderNMF19
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCServicesProviderNMF19
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl22 = mCServicesProviderNMF19
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCServicesProviderNMF19
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration24 = new esa.mo.nmf.MCRegistration(cOMServicesProvider5,
            parameterProviderServiceImpl10, aggregationProviderServiceImpl15, alertProviderServiceImpl18,
            actionProviderServiceImpl23);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl25 = mCRegistration24.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl26 = mCRegistration24.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider27 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF28 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl29 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl30 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF33 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl34 = mCServicesProviderNMF33
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl35 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl36 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl37 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF38 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl40 = mCServicesProviderNMF38
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF41 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF41
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl43 = mCServicesProviderNMF41
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCServicesProviderNMF41
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl45 = mCServicesProviderNMF41
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration46 = new esa.mo.nmf.MCRegistration(cOMServicesProvider27,
            parameterProviderServiceImpl32, aggregationProviderServiceImpl37, alertProviderServiceImpl40,
            actionProviderServiceImpl45);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF47 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl48 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl49 = mCServicesProviderNMF47
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl50 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl51 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl52 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl53 = mCServicesProviderNMF47
            .getActionService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF54 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl55 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl56 = mCServicesProviderNMF54
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl57 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl58 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl59 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl60 = mCServicesProviderNMF54
            .getActionService();
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray61 = new org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[]{actionProviderServiceImpl26,
                                                                                                                                                                                   actionProviderServiceImpl45,
                                                                                                                                                                                   actionProviderServiceImpl53,
                                                                                                                                                                                   actionProviderServiceImpl60};
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray62 = uShortList0
            .toArray(actionInheritanceSkeletonArray61);
        uShortList0.ensureCapacity(18);
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator65 = uShortList0.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort66 = uShortList0.getAreaNumber();
        java.lang.Long long67 = uShort66.getShortForm();
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(uShortStream2);
        org.junit.Assert.assertNotNull(uShortItor3);
        org.junit.Assert.assertNotNull(uShortItor4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl13);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl14);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl15);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl17);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl25);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl26);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl29);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl34);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl35);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl36);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl37);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl40);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl45);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl48);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl49);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl50);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl51);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl52);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl53);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl55);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl56);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl57);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl58);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl59);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl60);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray61);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray62);
        org.junit.Assert.assertNotNull(uShortSpliterator65);
        org.junit.Assert.assertNotNull(uShort66);
        org.junit.Assert.assertTrue("'" + long67 + "' != '" + 281474993487882L + "'", long67.equals(281474993487882L));
    }

    @Test
    public void test568() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test568");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl3 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl4 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl5 = mCServicesProviderNMF0
            .getActionService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl3);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl4);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl5);
    }

    @Test
    public void test569() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test569");
        org.ccsds.moims.mo.mal.structures.IntegerList integerList1 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        integerList1.add(0, (java.lang.Integer) 4);
        org.ccsds.moims.mo.mal.structures.UShort uShort5 = integerList1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet6 = integerList1.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShortList uShortList7 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int8 = uShortList7.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor9 = uShortList7.iterator();
        int int10 = integerList1.indexOf((java.lang.Object) uShortList7);
        uShortList7.trimToSize();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream12 = uShortList7.stream();
        java.lang.Byte[] byteArray15 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList16 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean17 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList16,
            byteArray15);
        boolean boolean18 = byteList16.isEmpty();
        int int20 = byteList16.indexOf((java.lang.Object) 'a');
        int int22 = byteList16.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray24 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList25 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean26 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList25,
            booleanArray24);
        java.util.ListIterator<java.lang.Boolean> booleanItor28 = booleanList25.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream29 = booleanList25.parallelStream();
        boolean boolean31 = booleanList25.add((java.lang.Boolean) false);
        boolean boolean32 = byteList16.retainAll((java.util.Collection<java.lang.Boolean>) booleanList25);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList33 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj34 = uShortList33.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream35 = uShortList33
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor36 = uShortList33.iterator();
        boolean boolean37 = byteList16.equals((java.lang.Object) uShortItor36);
        org.ccsds.moims.mo.mal.structures.ShortList shortList38 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj39 = shortList38.clone();
        boolean boolean40 = byteList16.equals((java.lang.Object) shortList38);
        esa.mo.nmf.NMFException nMFException42 = new esa.mo.nmf.NMFException("-1");
        boolean boolean43 = byteList16.equals((java.lang.Object) nMFException42);
        byteList16.trimToSize();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList45 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element46 = booleanList45.createElement();
        java.lang.Long long47 = booleanList45.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort48 = booleanList45.getAreaNumber();
        java.lang.Integer int49 = booleanList45.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray50 = new org.ccsds.moims.mo.mal.structures.BooleanList[]{booleanList45};
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray51 = byteList16.toArray(booleanListArray50);
        java.util.AbstractList<java.lang.Boolean>[] booleanListArray52 = uShortList7.toArray(
            (java.util.AbstractList<java.lang.Boolean>[]) booleanListArray50);
        java.lang.Long long53 = uShortList7.getShortForm();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator54 = uShortList7.spliterator();
        org.junit.Assert.assertNotNull(uShort5);
        org.junit.Assert.assertNotNull(uOctet6);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-10) + "'", int8.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + (-1) + "'", int10 == (-1));
        org.junit.Assert.assertNotNull(uShortStream12);
        org.junit.Assert.assertNotNull(byteArray15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + false + "'", !boolean18);
        org.junit.Assert.assertTrue("'" + int20 + "' != '" + (-1) + "'", int20 == (-1));
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-1) + "'", int22 == (-1));
        org.junit.Assert.assertNotNull(booleanArray24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertNotNull(booleanItor28);
        org.junit.Assert.assertNotNull(booleanStream29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertNotNull(obj34);
        org.junit.Assert.assertNotNull(uShortStream35);
        org.junit.Assert.assertNotNull(uShortItor36);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertNotNull(obj39);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + false + "'", !boolean43);
        org.junit.Assert.assertNotNull(element46);
        org.junit.Assert.assertTrue("'" + long47 + "' != '" + 281475010265086L + "'", long47.equals(281475010265086L));
        org.junit.Assert.assertNotNull(uShort48);
        org.junit.Assert.assertTrue("'" + int49 + "' != '" + (-2) + "'", int49.equals((-2)));
        org.junit.Assert.assertNotNull(booleanListArray50);
        org.junit.Assert.assertNotNull(booleanListArray51);
        org.junit.Assert.assertNotNull(booleanListArray52);
        org.junit.Assert.assertTrue("'" + long53 + "' != '" + 281475010265078L + "'", long53.equals(281475010265078L));
        org.junit.Assert.assertNotNull(uShortSpliterator54);
    }

    @Test
    public void test570() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test570");
        esa.mo.nmf.NMFException nMFException3 = new esa.mo.nmf.NMFException("0");
        esa.mo.nmf.NMFException nMFException4 = new esa.mo.nmf.NMFException("hi!", (java.lang.Throwable) nMFException3);
        esa.mo.nmf.NMFException nMFException5 = new esa.mo.nmf.NMFException("[1.0, 0.0, 10.0]",
            (java.lang.Throwable) nMFException4);
    }

    @Test
    public void test571() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test571");
        org.ccsds.moims.mo.mal.structures.Identifier identifier0 = new org.ccsds.moims.mo.mal.structures.Identifier();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet1 = identifier0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray2 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList3 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean4 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList3, uRIArray2);
        java.lang.Boolean[] booleanArray7 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList8 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean9 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList8,
            booleanArray7);
        java.util.Iterator<java.lang.Boolean> booleanItor10 = booleanList8.iterator();
        boolean boolean11 = uRIList3.retainAll((java.util.Collection<java.lang.Boolean>) booleanList8);
        java.lang.Boolean[] booleanArray14 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList15 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList15,
            booleanArray14);
        boolean boolean18 = booleanList15.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream19 = booleanList15.stream();
        boolean boolean20 = uRIList3.retainAll((java.util.Collection<java.lang.Boolean>) booleanList15);
        uRIList3.ensureCapacity(5);
        boolean boolean23 = identifier0.equals((java.lang.Object) uRIList3);
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor24 = uRIList3.listIterator();
        java.lang.String str25 = uRIList3.toString();
        org.ccsds.moims.mo.mal.structures.Union union27 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Double) 100.0d);
        org.ccsds.moims.mo.mal.structures.UShort uShort28 = union27.getAreaNumber();
        boolean boolean29 = uRIList3.equals((java.lang.Object) union27);
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.URI> uRIStream30 = uRIList3.parallelStream();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider31 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF32 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl33 = mCServicesProviderNMF32
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl34 = mCServicesProviderNMF32
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl35 = mCServicesProviderNMF32
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl36 = mCServicesProviderNMF32
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF37 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl38 = mCServicesProviderNMF37
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl39 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl40 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl41 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF42 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCServicesProviderNMF42
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCServicesProviderNMF42
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF45 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl46 = mCServicesProviderNMF45
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl47 = mCServicesProviderNMF45
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl48 = mCServicesProviderNMF45
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl49 = mCServicesProviderNMF45
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration50 = new esa.mo.nmf.MCRegistration(cOMServicesProvider31,
            parameterProviderServiceImpl36, aggregationProviderServiceImpl41, alertProviderServiceImpl44,
            actionProviderServiceImpl49);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl51 = mCRegistration50.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl52 = mCRegistration50.aggregationService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl53 = mCRegistration50.actionService;
        boolean boolean54 = uRIList3.remove((java.lang.Object) mCRegistration50);
        java.lang.String str55 = uRIList3.toString();
        try {
            org.ccsds.moims.mo.mal.structures.URI uRI57 = uRIList3.remove((-4));
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: null");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(uOctet1);
        org.junit.Assert.assertNotNull(uRIArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + false + "'", !boolean4);
        org.junit.Assert.assertNotNull(booleanArray7);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
        org.junit.Assert.assertNotNull(booleanItor10);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertNotNull(booleanArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertNotNull(booleanStream19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
        org.junit.Assert.assertNotNull(uRIItor24);
        org.junit.Assert.assertTrue("'" + str25 + "' != '" + "[]" + "'", str25.equals("[]"));
        org.junit.Assert.assertNotNull(uShort28);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
        org.junit.Assert.assertNotNull(uRIStream30);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl33);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl34);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl35);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl36);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl38);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl39);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl40);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl46);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl47);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl48);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl49);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl51);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl52);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl53);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertTrue("'" + str55 + "' != '" + "[]" + "'", str55.equals("[]"));
    }

    @Test
    public void test572() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test572");
        java.lang.String[] strArray1 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList2 = new java.util.ArrayList<java.lang.String>();
        boolean boolean3 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList2, strArray1);
        boolean boolean5 = strList2.add("hi!");
        java.lang.Object obj6 = null;
        boolean boolean7 = strList2.equals(obj6);
        java.util.ListIterator<java.lang.String> strItor8 = strList2.listIterator();
        java.lang.Object obj9 = null;
        boolean boolean10 = strList2.remove(obj9);
        java.util.Iterator<java.lang.String> strItor11 = strList2.iterator();
        java.util.Spliterator<java.lang.String> strSpliterator12 = strList2.spliterator();
        int int13 = strList2.size();
        boolean boolean14 = strList2.isEmpty();
        java.lang.Boolean[] booleanArray17 = new java.lang.Boolean[]{false, false};
        java.util.ArrayList<java.lang.Boolean> booleanList18 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean19 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList18,
            booleanArray17);
        boolean boolean20 = strList2.removeAll((java.util.Collection<java.lang.Boolean>) booleanList18);
        org.ccsds.moims.mo.mal.structures.IntegerList integerList22 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        integerList22.add(0, (java.lang.Integer) 4);
        org.ccsds.moims.mo.mal.structures.UShort uShort26 = integerList22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet27 = integerList22.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShortList uShortList28 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int29 = uShortList28.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor30 = uShortList28.iterator();
        int int31 = integerList22.indexOf((java.lang.Object) uShortList28);
        uShortList28.trimToSize();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream33 = uShortList28.stream();
        java.lang.Byte[] byteArray36 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList37 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean38 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList37,
            byteArray36);
        boolean boolean39 = byteList37.isEmpty();
        int int41 = byteList37.indexOf((java.lang.Object) 'a');
        int int43 = byteList37.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray45 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList46 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean47 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList46,
            booleanArray45);
        java.util.ListIterator<java.lang.Boolean> booleanItor49 = booleanList46.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream50 = booleanList46.parallelStream();
        boolean boolean52 = booleanList46.add((java.lang.Boolean) false);
        boolean boolean53 = byteList37.retainAll((java.util.Collection<java.lang.Boolean>) booleanList46);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList54 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj55 = uShortList54.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream56 = uShortList54
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor57 = uShortList54.iterator();
        boolean boolean58 = byteList37.equals((java.lang.Object) uShortItor57);
        org.ccsds.moims.mo.mal.structures.ShortList shortList59 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj60 = shortList59.clone();
        boolean boolean61 = byteList37.equals((java.lang.Object) shortList59);
        esa.mo.nmf.NMFException nMFException63 = new esa.mo.nmf.NMFException("-1");
        boolean boolean64 = byteList37.equals((java.lang.Object) nMFException63);
        byteList37.trimToSize();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList66 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element67 = booleanList66.createElement();
        java.lang.Long long68 = booleanList66.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort69 = booleanList66.getAreaNumber();
        java.lang.Integer int70 = booleanList66.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray71 = new org.ccsds.moims.mo.mal.structures.BooleanList[]{booleanList66};
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray72 = byteList37.toArray(booleanListArray71);
        java.util.AbstractList<java.lang.Boolean>[] booleanListArray73 = uShortList28.toArray(
            (java.util.AbstractList<java.lang.Boolean>[]) booleanListArray71);
        try {
            java.util.Collection<java.lang.Boolean>[] booleanCollectionArray74 = strList2.toArray(
                (java.util.Collection<java.lang.Boolean>[]) booleanListArray73);
            org.junit.Assert.fail("Expected exception of type java.lang.ArrayStoreException; message: null");
        } catch (java.lang.ArrayStoreException e) {
        }
        org.junit.Assert.assertNotNull(strArray1);
        org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(strItor8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(strItor11);
        org.junit.Assert.assertNotNull(strSpliterator12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + 2 + "'", int13 == 2);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertNotNull(booleanArray17);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertNotNull(uShort26);
        org.junit.Assert.assertNotNull(uOctet27);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-10) + "'", int29.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor30);
        org.junit.Assert.assertTrue("'" + int31 + "' != '" + (-1) + "'", int31 == (-1));
        org.junit.Assert.assertNotNull(uShortStream33);
        org.junit.Assert.assertNotNull(byteArray36);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + true + "'", boolean38);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
        org.junit.Assert.assertTrue("'" + int41 + "' != '" + (-1) + "'", int41 == (-1));
        org.junit.Assert.assertTrue("'" + int43 + "' != '" + (-1) + "'", int43 == (-1));
        org.junit.Assert.assertNotNull(booleanArray45);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
        org.junit.Assert.assertNotNull(booleanItor49);
        org.junit.Assert.assertNotNull(booleanStream50);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
        org.junit.Assert.assertNotNull(obj55);
        org.junit.Assert.assertNotNull(uShortStream56);
        org.junit.Assert.assertNotNull(uShortItor57);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
        org.junit.Assert.assertNotNull(obj60);
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + true + "'", boolean61);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + false + "'", !boolean64);
        org.junit.Assert.assertNotNull(element67);
        org.junit.Assert.assertTrue("'" + long68 + "' != '" + 281475010265086L + "'", long68.equals(281475010265086L));
        org.junit.Assert.assertNotNull(uShort69);
        org.junit.Assert.assertTrue("'" + int70 + "' != '" + (-2) + "'", int70.equals((-2)));
        org.junit.Assert.assertNotNull(booleanListArray71);
        org.junit.Assert.assertNotNull(booleanListArray72);
        org.junit.Assert.assertNotNull(booleanListArray73);
    }

    @Test
    public void test573() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test573");
        java.lang.Long[] longArray4 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList5 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList5, longArray4);
        java.lang.Object obj7 = null;
        boolean boolean8 = longList5.contains(obj7);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger10 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean11 = longList5.contains((java.lang.Object) (byte) 100);
        java.lang.Boolean[] booleanArray14 = new java.lang.Boolean[]{false, false};
        java.util.ArrayList<java.lang.Boolean> booleanList15 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList15,
            booleanArray14);
        boolean boolean17 = longList5.containsAll((java.util.Collection<java.lang.Boolean>) booleanList15);
        java.lang.Long long19 = longList5.get(1);
        java.util.stream.Stream<java.lang.Long> longStream20 = longList5.stream();
        java.lang.Long long22 = longList5.remove(0);
        java.lang.Object obj23 = longList5.clone();
        org.ccsds.moims.mo.mal.structures.LongList longList25 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet26 = longList25.getAreaVersion();
        java.util.Iterator<java.lang.Long> longItor27 = longList25.iterator();
        longList25.ensureCapacity((-15));
        java.util.Iterator<java.lang.Long> longItor30 = longList25.iterator();
        esa.mo.nmf.NMFException nMFException32 = new esa.mo.nmf.NMFException("1");
        esa.mo.nmf.NMFException nMFException35 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray36 = nMFException35.getSuppressed();
        java.lang.Throwable[] throwableArray37 = nMFException35.getSuppressed();
        esa.mo.nmf.NMFException nMFException39 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray40 = nMFException39.getSuppressed();
        nMFException35.addSuppressed((java.lang.Throwable) nMFException39);
        esa.mo.nmf.NMFException nMFException42 = new esa.mo.nmf.NMFException("", (java.lang.Throwable) nMFException39);
        java.lang.Throwable[] throwableArray43 = nMFException39.getSuppressed();
        esa.mo.nmf.NMFException[] nMFExceptionArray44 = new esa.mo.nmf.NMFException[]{nMFException32, nMFException39};
        esa.mo.nmf.NMFException[] nMFExceptionArray45 = longList25.toArray(nMFExceptionArray44);
        int int46 = longList5.indexOf((java.lang.Object) nMFExceptionArray45);
        org.ccsds.moims.mo.mal.structures.ShortList shortList47 = new org.ccsds.moims.mo.mal.structures.ShortList();
        org.ccsds.moims.mo.mal.structures.UShort uShort48 = shortList47.getServiceNumber();
        java.util.stream.Stream<java.lang.Short> shortStream49 = shortList47.stream();
        int int50 = shortList47.size();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList52 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.lang.Long long53 = integerList52.getShortForm();
        java.util.stream.Stream<java.lang.Integer> intStream54 = integerList52.parallelStream();
        int int55 = integerList52.size();
        integerList52.ensureCapacity(2);
        java.util.stream.Stream<java.lang.Integer> intStream58 = integerList52.parallelStream();
        boolean boolean59 = shortList47.equals((java.lang.Object) integerList52);
        org.ccsds.moims.mo.mal.structures.BooleanList[][] booleanListArray60 = new org.ccsds.moims.mo.mal.structures.BooleanList[][]{};
        org.ccsds.moims.mo.mal.structures.BooleanList[][] booleanListArray61 = shortList47.toArray(booleanListArray60);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet62 = shortList47.getAreaVersion();
        java.lang.String str63 = uOctet62.toString();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet64 = uOctet62.getAreaVersion();
        int int65 = longList5.indexOf((java.lang.Object) uOctet62);
        org.junit.Assert.assertNotNull(longArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertNotNull(booleanArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
        org.junit.Assert.assertTrue("'" + long19 + "' != '" + (-1L) + "'", long19.equals((-1L)));
        org.junit.Assert.assertNotNull(longStream20);
        org.junit.Assert.assertTrue("'" + long22 + "' != '" + 10L + "'", long22.equals(10L));
        org.junit.Assert.assertNotNull(obj23);
        org.junit.Assert.assertNotNull(uOctet26);
        org.junit.Assert.assertNotNull(longItor27);
        org.junit.Assert.assertNotNull(longItor30);
        org.junit.Assert.assertNotNull(throwableArray36);
        org.junit.Assert.assertNotNull(throwableArray37);
        org.junit.Assert.assertNotNull(throwableArray40);
        org.junit.Assert.assertNotNull(throwableArray43);
        org.junit.Assert.assertNotNull(nMFExceptionArray44);
        org.junit.Assert.assertNotNull(nMFExceptionArray45);
        org.junit.Assert.assertTrue("'" + int46 + "' != '" + (-1) + "'", int46 == (-1));
        org.junit.Assert.assertNotNull(uShort48);
        org.junit.Assert.assertNotNull(shortStream49);
        org.junit.Assert.assertTrue("'" + int50 + "' != '" + 0 + "'", int50 == 0);
        org.junit.Assert.assertTrue("'" + long53 + "' != '" + 281475010265077L + "'", long53.equals(281475010265077L));
        org.junit.Assert.assertNotNull(intStream54);
        org.junit.Assert.assertTrue("'" + int55 + "' != '" + 0 + "'", int55 == 0);
        org.junit.Assert.assertNotNull(intStream58);
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + true + "'", boolean59);
        org.junit.Assert.assertNotNull(booleanListArray60);
        org.junit.Assert.assertNotNull(booleanListArray61);
        org.junit.Assert.assertNotNull(uOctet62);
        org.junit.Assert.assertTrue("'" + str63 + "' != '" + "1" + "'", str63.equals("1"));
        org.junit.Assert.assertNotNull(uOctet64);
        org.junit.Assert.assertTrue("'" + int65 + "' != '" + (-1) + "'", int65 == (-1));
    }

    @Test
    public void test574() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test574");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl22 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl24 = mCRegistration19.actionService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl24);
    }

    @Test
    public void test575() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test575");
        java.lang.Short[] shortArray2 = new java.lang.Short[]{(short) 100, (short) -1};
        java.util.ArrayList<java.lang.Short> shortList3 = new java.util.ArrayList<java.lang.Short>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Short>) shortList3,
            shortArray2);
        java.lang.Object obj5 = null;
        boolean boolean6 = shortList3.equals(obj5);
        java.util.Iterator<java.lang.Short> shortItor7 = shortList3.iterator();
        java.lang.Object[] objArray8 = shortList3.toArray();
        java.lang.String[] strArray12 = new java.lang.String[]{"[-1, -1, 1, 1, 10]", "100", "100"};
        java.util.ArrayList<java.lang.String> strList13 = new java.util.ArrayList<java.lang.String>();
        boolean boolean14 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList13,
            strArray12);
        java.lang.Boolean[] booleanArray17 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList18 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean19 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList18,
            booleanArray17);
        boolean boolean21 = booleanList18.add((java.lang.Boolean) true);
        boolean boolean22 = strList13.removeAll((java.util.Collection<java.lang.Boolean>) booleanList18);
        java.lang.String[] strArray24 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList25 = new java.util.ArrayList<java.lang.String>();
        boolean boolean26 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList25,
            strArray24);
        int int27 = strList25.size();
        java.lang.Boolean[] booleanArray30 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList31 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean32 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList31,
            booleanArray30);
        boolean boolean34 = booleanList31.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream35 = booleanList31.stream();
        boolean boolean36 = strList25.containsAll((java.util.Collection<java.lang.Boolean>) booleanList31);
        int int37 = booleanList18.indexOf((java.lang.Object) strList25);
        boolean boolean38 = booleanList18.isEmpty();
        boolean boolean39 = shortList3.containsAll((java.util.Collection<java.lang.Boolean>) booleanList18);
        shortList3.ensureCapacity(3);
        java.util.Iterator<java.lang.Short> shortItor42 = shortList3.iterator();
        java.util.stream.Stream<java.lang.Short> shortStream43 = shortList3.stream();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF44 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl45 = mCServicesProviderNMF44
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl46 = mCServicesProviderNMF44
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl47 = mCServicesProviderNMF44
            .getAlertService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl48 = mCServicesProviderNMF44
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl49 = mCServicesProviderNMF44
            .getParameterService();
        int int50 = shortList3.lastIndexOf((java.lang.Object) parameterProviderServiceImpl49);
        org.junit.Assert.assertNotNull(shortArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + false + "'", !boolean6);
        org.junit.Assert.assertNotNull(shortItor7);
        org.junit.Assert.assertNotNull(objArray8);
        org.junit.Assert.assertNotNull(strArray12);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14);
        org.junit.Assert.assertNotNull(booleanArray17);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + true + "'", boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertNotNull(strArray24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertTrue("'" + int27 + "' != '" + 1 + "'", int27 == 1);
        org.junit.Assert.assertNotNull(booleanArray30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertNotNull(booleanStream35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertTrue("'" + int37 + "' != '" + (-1) + "'", int37 == (-1));
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
        org.junit.Assert.assertNotNull(shortItor42);
        org.junit.Assert.assertNotNull(shortStream43);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl45);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl46);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl47);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl48);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl49);
        org.junit.Assert.assertTrue("'" + int50 + "' != '" + (-1) + "'", int50 == (-1));
    }

    @Test
    public void test576() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test576");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        floatList0.clear();
        java.lang.Long[] longArray29 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList30 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean31 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList30,
            longArray29);
        java.lang.Object obj32 = null;
        boolean boolean33 = longList30.contains(obj32);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger35 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean36 = longList30.contains((java.lang.Object) (byte) 100);
        java.util.Iterator<java.lang.Long> longItor37 = longList30.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort38 = org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
        boolean boolean39 = longList30.remove((java.lang.Object) uShort38);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList40 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int41 = uShortList40.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor42 = uShortList40.iterator();
        java.lang.Object[] objArray43 = uShortList40.toArray();
        uShortList40.clear();
        uShortList40.clear();
        boolean boolean46 = uShortList40.isEmpty();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor47 = uShortList40.iterator();
        boolean boolean48 = longList30.contains((java.lang.Object) uShortList40);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList49 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.UShort uShort50 = booleanList49.getAreaNumber();
        boolean boolean51 = longList30.retainAll((java.util.Collection<java.lang.Boolean>) booleanList49);
        java.lang.Integer int52 = booleanList49.getTypeShortForm();
        boolean boolean54 = booleanList49.add((java.lang.Boolean) true);
        java.util.Iterator<java.lang.Boolean> booleanItor55 = booleanList49.iterator();
        boolean boolean56 = floatList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList49);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(longArray29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertNotNull(longItor37);
        org.junit.Assert.assertNotNull(uShort38);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
        org.junit.Assert.assertTrue("'" + int41 + "' != '" + (-10) + "'", int41.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor42);
        org.junit.Assert.assertNotNull(objArray43);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertNotNull(uShortItor47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertNotNull(uShort50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
        org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-2) + "'", int52.equals((-2)));
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
        org.junit.Assert.assertNotNull(booleanItor55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
    }

    @Test
    public void test577() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test577");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl3 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl4 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl5 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl6 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl7 = mCServicesProviderNMF0
            .getActionService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl3);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl4);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl5);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl6);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl7);
    }

    @Test
    public void test578() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test578");
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList0 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element1 = booleanList0.createElement();
        java.lang.Long long2 = booleanList0.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element3 = booleanList0.createElement();
        esa.mo.nmf.NMFException nMFException5 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray6 = nMFException5.getSuppressed();
        java.lang.Throwable[] throwableArray7 = nMFException5.getSuppressed();
        java.lang.Throwable[] throwableArray8 = nMFException5.getSuppressed();
        boolean boolean9 = booleanList0.equals((java.lang.Object) throwableArray8);
        boolean boolean11 = booleanList0.add((java.lang.Boolean) true);
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265086L + "'", long2.equals(281475010265086L));
        org.junit.Assert.assertNotNull(element3);
        org.junit.Assert.assertNotNull(throwableArray6);
        org.junit.Assert.assertNotNull(throwableArray7);
        org.junit.Assert.assertNotNull(throwableArray8);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + false + "'", !boolean9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
    }

    @Test
    public void test579() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test579");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl24 = mCRegistration19.aggregationService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl25 = mCRegistration19.aggregationService;
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList26 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.Element element27 = doubleList26.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort28 = doubleList26.getServiceNumber();
        doubleList26.ensureCapacity(3);
        doubleList26.trimToSize();
        org.ccsds.moims.mo.mal.structures.UShort uShort32 = doubleList26.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort33 = doubleList26.getServiceNumber();
        java.util.Spliterator<java.lang.Double> doubleSpliterator34 = doubleList26.spliterator();
        org.ccsds.moims.mo.mal.structures.Element element35 = doubleList26.createElement();
        java.lang.Integer int36 = doubleList26.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.OctetList octetList37 = new org.ccsds.moims.mo.mal.structures.OctetList();
        octetList37.clear();
        java.util.Iterator<java.lang.Byte> byteItor39 = octetList37.iterator();
        org.ccsds.moims.mo.mal.structures.Element element40 = octetList37.createElement();
        java.lang.Boolean[] booleanArray42 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList43 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean44 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList43,
            booleanArray42);
        java.util.ListIterator<java.lang.Boolean> booleanItor46 = booleanList43.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream47 = booleanList43.parallelStream();
        boolean boolean49 = booleanList43.add((java.lang.Boolean) false);
        java.util.Spliterator<java.lang.Boolean> booleanSpliterator50 = booleanList43.spliterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream51 = booleanList43.stream();
        boolean boolean52 = octetList37.removeAll((java.util.Collection<java.lang.Boolean>) booleanList43);
        boolean boolean53 = doubleList26.containsAll((java.util.Collection<java.lang.Boolean>) booleanList43);
        try {
            org.ccsds.moims.mo.com.structures.ObjectIdList objectIdList54 = mCRegistration19.registerConversions(
                (org.ccsds.moims.mo.mal.structures.ElementList) doubleList26);
            org.junit.Assert.fail(
                "Expected exception of type esa.mo.nmf.NMFException; message: The conversion object didn't match any type of Conversion.");
        } catch (esa.mo.nmf.NMFException e) {
        }
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl24);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl25);
        org.junit.Assert.assertNotNull(element27);
        org.junit.Assert.assertNotNull(uShort28);
        org.junit.Assert.assertNotNull(uShort32);
        org.junit.Assert.assertNotNull(uShort33);
        org.junit.Assert.assertNotNull(doubleSpliterator34);
        org.junit.Assert.assertNotNull(element35);
        org.junit.Assert.assertTrue("'" + int36 + "' != '" + (-5) + "'", int36.equals((-5)));
        org.junit.Assert.assertNotNull(byteItor39);
        org.junit.Assert.assertNotNull(element40);
        org.junit.Assert.assertNotNull(booleanArray42);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + true + "'", boolean44);
        org.junit.Assert.assertNotNull(booleanItor46);
        org.junit.Assert.assertNotNull(booleanStream47);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
        org.junit.Assert.assertNotNull(booleanSpliterator50);
        org.junit.Assert.assertNotNull(booleanStream51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
    }

    @Test
    public void test580() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test580");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl5 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl6 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl7 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl8 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl9 = mCServicesProviderNMF0
            .getAlertService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl5);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl6);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl7);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl8);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl9);
    }

    @Test
    public void test581() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test581");
        esa.mo.nmf.NMFException nMFException2 = new esa.mo.nmf.NMFException("[-1.0]");
        esa.mo.nmf.NMFException nMFException3 = new esa.mo.nmf.NMFException("[hi!, [1]]",
            (java.lang.Throwable) nMFException2);
        esa.mo.nmf.NMFException nMFException6 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray7 = nMFException6.getSuppressed();
        java.lang.Throwable[] throwableArray8 = nMFException6.getSuppressed();
        esa.mo.nmf.NMFException nMFException10 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray11 = nMFException10.getSuppressed();
        nMFException6.addSuppressed((java.lang.Throwable) nMFException10);
        esa.mo.nmf.NMFException nMFException13 = new esa.mo.nmf.NMFException(
            "[281475010265077, 281474993487884, 281475010265073, 281475010265083, 281475010265084, -1, 281474993487884, 281474993487874, 281475010265070, 281474993487888, 281474993487887, 281474993487886, 100, 281474993487885]",
            (java.lang.Throwable) nMFException6);
        nMFException3.addSuppressed((java.lang.Throwable) nMFException6);
        org.junit.Assert.assertNotNull(throwableArray7);
        org.junit.Assert.assertNotNull(throwableArray8);
        org.junit.Assert.assertNotNull(throwableArray11);
    }

    @Test
    public void test582() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test582");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream2 = uShortList0.parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor3 = uShortList0.iterator();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor4 = uShortList0.listIterator();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider5 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl13 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl14 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl15 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF16 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl17 = mCServicesProviderNMF16
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl18 = mCServicesProviderNMF16
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF19 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCServicesProviderNMF19
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCServicesProviderNMF19
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl22 = mCServicesProviderNMF19
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCServicesProviderNMF19
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration24 = new esa.mo.nmf.MCRegistration(cOMServicesProvider5,
            parameterProviderServiceImpl10, aggregationProviderServiceImpl15, alertProviderServiceImpl18,
            actionProviderServiceImpl23);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl25 = mCRegistration24.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl26 = mCRegistration24.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider27 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF28 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl29 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl30 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF33 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl34 = mCServicesProviderNMF33
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl35 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl36 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl37 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF38 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl40 = mCServicesProviderNMF38
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF41 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF41
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl43 = mCServicesProviderNMF41
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCServicesProviderNMF41
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl45 = mCServicesProviderNMF41
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration46 = new esa.mo.nmf.MCRegistration(cOMServicesProvider27,
            parameterProviderServiceImpl32, aggregationProviderServiceImpl37, alertProviderServiceImpl40,
            actionProviderServiceImpl45);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF47 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl48 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl49 = mCServicesProviderNMF47
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl50 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl51 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl52 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl53 = mCServicesProviderNMF47
            .getActionService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF54 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl55 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl56 = mCServicesProviderNMF54
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl57 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl58 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl59 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl60 = mCServicesProviderNMF54
            .getActionService();
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray61 = new org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[]{actionProviderServiceImpl26,
                                                                                                                                                                                   actionProviderServiceImpl45,
                                                                                                                                                                                   actionProviderServiceImpl53,
                                                                                                                                                                                   actionProviderServiceImpl60};
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray62 = uShortList0
            .toArray(actionInheritanceSkeletonArray61);
        uShortList0.ensureCapacity(18);
        org.ccsds.moims.mo.mal.structures.URIList uRIList66 = new org.ccsds.moims.mo.mal.structures.URIList(17);
        org.ccsds.moims.mo.mal.structures.UShort uShort67 = uRIList66.getAreaNumber();
        java.lang.Long long68 = uRIList66.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element69 = uRIList66.createElement();
        boolean boolean70 = uShortList0.remove((java.lang.Object) uRIList66);
        java.lang.Object[] objArray71 = uShortList0.toArray();
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(uShortStream2);
        org.junit.Assert.assertNotNull(uShortItor3);
        org.junit.Assert.assertNotNull(uShortItor4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl13);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl14);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl15);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl17);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl25);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl26);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl29);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl34);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl35);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl36);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl37);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl40);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl45);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl48);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl49);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl50);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl51);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl52);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl53);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl55);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl56);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl57);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl58);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl59);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl60);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray61);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray62);
        org.junit.Assert.assertNotNull(uShort67);
        org.junit.Assert.assertTrue("'" + long68 + "' != '" + 281475010265070L + "'", long68.equals(281475010265070L));
        org.junit.Assert.assertNotNull(element69);
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + false + "'", !boolean70);
        org.junit.Assert.assertNotNull(objArray71);
    }

    @Test
    public void test583() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test583");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.ListIterator<java.lang.Float> floatItor17 = floatList0.listIterator();
        java.lang.String str18 = floatList0.toString();
        java.lang.Boolean[] booleanArray20 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList21 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList21,
            booleanArray20);
        java.util.ListIterator<java.lang.Boolean> booleanItor24 = booleanList21.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream25 = booleanList21.parallelStream();
        boolean boolean27 = booleanList21.add((java.lang.Boolean) false);
        java.lang.Boolean boolean30 = booleanList21.set(0, (java.lang.Boolean) false);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList31 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.Element element32 = doubleList31.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort33 = doubleList31.getServiceNumber();
        doubleList31.ensureCapacity(3);
        doubleList31.trimToSize();
        java.lang.Boolean[] booleanArray39 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList40 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean41 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList40,
            booleanArray39);
        boolean boolean43 = booleanList40.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream44 = booleanList40.stream();
        java.lang.Double[] doubleArray46 = new java.lang.Double[]{0.0d};
        java.util.ArrayList<java.lang.Double> doubleList47 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList47,
            doubleArray46);
        java.util.Spliterator<java.lang.Double> doubleSpliterator49 = doubleList47.spliterator();
        java.util.Iterator<java.lang.Double> doubleItor50 = doubleList47.iterator();
        boolean boolean51 = booleanList40.contains((java.lang.Object) doubleItor50);
        boolean boolean52 = doubleList31.removeAll((java.util.Collection<java.lang.Boolean>) booleanList40);
        booleanList40.trimToSize();
        boolean boolean54 = booleanList21.containsAll((java.util.Collection<java.lang.Boolean>) booleanList40);
        boolean boolean55 = floatList0.retainAll((java.util.Collection<java.lang.Boolean>) booleanList21);
        int int57 = floatList0.lastIndexOf((java.lang.Object) (-7));
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList58 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element59 = booleanList58.createElement();
        java.lang.Long long60 = booleanList58.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element61 = booleanList58.createElement();
        esa.mo.nmf.NMFException nMFException63 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray64 = nMFException63.getSuppressed();
        java.lang.Throwable[] throwableArray65 = nMFException63.getSuppressed();
        java.lang.Throwable[] throwableArray66 = nMFException63.getSuppressed();
        boolean boolean67 = booleanList58.equals((java.lang.Object) throwableArray66);
        org.ccsds.moims.mo.mal.structures.Element element68 = booleanList58.createElement();
        org.ccsds.moims.mo.mal.structures.Element element69 = booleanList58.createElement();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet70 = booleanList58.getAreaVersion();
        int int71 = booleanList58.size();
        boolean boolean72 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList58);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatItor17);
        org.junit.Assert.assertTrue("'" + str18 + "' != '" + "[]" + "'", str18.equals("[]"));
        org.junit.Assert.assertNotNull(booleanArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(booleanItor24);
        org.junit.Assert.assertNotNull(booleanStream25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30.equals(true));
        org.junit.Assert.assertNotNull(element32);
        org.junit.Assert.assertNotNull(uShort33);
        org.junit.Assert.assertNotNull(booleanArray39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertNotNull(booleanStream44);
        org.junit.Assert.assertNotNull(doubleArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(doubleSpliterator49);
        org.junit.Assert.assertNotNull(doubleItor50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + false + "'", !boolean51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + false + "'", !boolean54);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + false + "'", !boolean55);
        org.junit.Assert.assertTrue("'" + int57 + "' != '" + (-1) + "'", int57 == (-1));
        org.junit.Assert.assertNotNull(element59);
        org.junit.Assert.assertTrue("'" + long60 + "' != '" + 281475010265086L + "'", long60.equals(281475010265086L));
        org.junit.Assert.assertNotNull(element61);
        org.junit.Assert.assertNotNull(throwableArray64);
        org.junit.Assert.assertNotNull(throwableArray65);
        org.junit.Assert.assertNotNull(throwableArray66);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + false + "'", !boolean67);
        org.junit.Assert.assertNotNull(element68);
        org.junit.Assert.assertNotNull(element69);
        org.junit.Assert.assertNotNull(uOctet70);
        org.junit.Assert.assertTrue("'" + int71 + "' != '" + 0 + "'", int71 == 0);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + true + "'", boolean72);
    }

    @Test
    public void test584() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test584");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        java.lang.Integer int22 = floatList0.getTypeShortForm();
        java.util.ListIterator<java.lang.Float> floatItor23 = floatList0.listIterator();
        org.ccsds.moims.mo.mal.structures.Identifier identifier24 = new org.ccsds.moims.mo.mal.structures.Identifier();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet25 = identifier24.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray26 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList27 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean28 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList27, uRIArray26);
        java.lang.Boolean[] booleanArray31 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList32 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean33 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList32,
            booleanArray31);
        java.util.Iterator<java.lang.Boolean> booleanItor34 = booleanList32.iterator();
        boolean boolean35 = uRIList27.retainAll((java.util.Collection<java.lang.Boolean>) booleanList32);
        java.lang.Boolean[] booleanArray38 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList39 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean40 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList39,
            booleanArray38);
        boolean boolean42 = booleanList39.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream43 = booleanList39.stream();
        boolean boolean44 = uRIList27.retainAll((java.util.Collection<java.lang.Boolean>) booleanList39);
        uRIList27.ensureCapacity(5);
        boolean boolean47 = identifier24.equals((java.lang.Object) uRIList27);
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor48 = uRIList27.listIterator();
        java.lang.String str49 = uRIList27.toString();
        org.ccsds.moims.mo.mal.structures.Union union51 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Double) 100.0d);
        org.ccsds.moims.mo.mal.structures.UShort uShort52 = union51.getAreaNumber();
        boolean boolean53 = uRIList27.equals((java.lang.Object) union51);
        int int54 = floatList0.lastIndexOf((java.lang.Object) union51);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-4) + "'", int22.equals((-4)));
        org.junit.Assert.assertNotNull(floatItor23);
        org.junit.Assert.assertNotNull(uOctet25);
        org.junit.Assert.assertNotNull(uRIArray26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", !boolean28);
        org.junit.Assert.assertNotNull(booleanArray31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertNotNull(booleanItor34);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
        org.junit.Assert.assertNotNull(booleanArray38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
        org.junit.Assert.assertNotNull(booleanStream43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertNotNull(uRIItor48);
        org.junit.Assert.assertTrue("'" + str49 + "' != '" + "[]" + "'", str49.equals("[]"));
        org.junit.Assert.assertNotNull(uShort52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
        org.junit.Assert.assertTrue("'" + int54 + "' != '" + (-1) + "'", int54 == (-1));
    }

    @Test
    public void test585() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test585");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl24 = mCRegistration19.aggregationService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider25 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF26 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl27 = mCServicesProviderNMF26
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl28 = mCServicesProviderNMF26
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl29 = mCServicesProviderNMF26
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl30 = mCServicesProviderNMF26
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF31 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF31
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl33 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl34 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl35 = mCServicesProviderNMF31
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF36 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl37 = mCServicesProviderNMF36
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl38 = mCServicesProviderNMF36
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF39 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl40 = mCServicesProviderNMF39
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl41 = mCServicesProviderNMF39
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl42 = mCServicesProviderNMF39
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl43 = mCServicesProviderNMF39
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration44 = new esa.mo.nmf.MCRegistration(cOMServicesProvider25,
            parameterProviderServiceImpl30, aggregationProviderServiceImpl35, alertProviderServiceImpl38,
            actionProviderServiceImpl43);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl45 = mCRegistration44.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl46 = mCRegistration44.aggregationService;
        esa.mo.nmf.MCRegistration.RegistrationMode registrationMode47 = esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS;
        mCRegistration44.setMode(registrationMode47);
        mCRegistration19.setMode(registrationMode47);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl24);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl27);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl28);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl29);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl30);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl33);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl34);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl35);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl37);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl38);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl40);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl41);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl42);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl43);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl45);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl46);
        org.junit.Assert.assertTrue("'" + registrationMode47 + "' != '" +
            esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS + "'", registrationMode47.equals(
                esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS));
    }

    @Test
    public void test586() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test586");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        org.ccsds.moims.mo.mal.structures.Time time24 = new org.ccsds.moims.mo.mal.structures.Time();
        org.ccsds.moims.mo.mal.structures.Element element25 = time24.createElement();
        boolean boolean26 = longList22.equals((java.lang.Object) time24);
        java.lang.String[] strArray28 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList29 = new java.util.ArrayList<java.lang.String>();
        boolean boolean30 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList29,
            strArray28);
        boolean boolean32 = strList29.add("hi!");
        java.lang.Object obj33 = null;
        boolean boolean34 = strList29.equals(obj33);
        java.util.ListIterator<java.lang.String> strItor35 = strList29.listIterator();
        int int36 = longList22.indexOf((java.lang.Object) strList29);
        java.lang.Class<?> wildcardClass37 = strList29.getClass();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(element25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(strArray28);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertNotNull(strItor35);
        org.junit.Assert.assertTrue("'" + int36 + "' != '" + (-1) + "'", int36 == (-1));
        org.junit.Assert.assertNotNull(wildcardClass37);
    }

    @Test
    public void test587() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test587");
        org.ccsds.moims.mo.mal.structures.OctetList octetList0 = new org.ccsds.moims.mo.mal.structures.OctetList();
        org.ccsds.moims.mo.mal.structures.Element element1 = octetList0.createElement();
        java.lang.Integer int2 = octetList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort3 = octetList0.getAreaNumber();
        java.util.ListIterator<java.lang.Byte> byteItor4 = octetList0.listIterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort5 = octetList0.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.LongList longList7 = new org.ccsds.moims.mo.mal.structures.LongList(0);
        java.lang.Integer int8 = longList7.getTypeShortForm();
        int int9 = longList7.size();
        java.util.ListIterator<java.lang.Long> longItor10 = longList7.listIterator();
        org.ccsds.moims.mo.mal.structures.ShortList shortList11 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj12 = shortList11.clone();
        java.util.stream.Stream<java.lang.Short> shortStream13 = shortList11.parallelStream();
        shortList11.trimToSize();
        shortList11.ensureCapacity((int) ' ');
        java.lang.Integer[] intArray33 = new java.lang.Integer[]{18, 0, 18, 65535, (-1), 3, (-1), (-1), 13, 0, 3, (-5),
                                                                 12, 0, 100, 14};
        java.util.ArrayList<java.lang.Integer> intList34 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList34,
            intArray33);
        java.lang.Boolean[] booleanArray38 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList39 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean40 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList39,
            booleanArray38);
        java.util.Iterator<java.lang.Boolean> booleanItor41 = booleanList39.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream42 = booleanList39.stream();
        boolean boolean43 = intList34.retainAll((java.util.Collection<java.lang.Boolean>) booleanList39);
        boolean boolean44 = shortList11.containsAll((java.util.Collection<java.lang.Boolean>) booleanList39);
        java.lang.Boolean[] booleanArray46 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList47 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList47,
            booleanArray46);
        java.util.ListIterator<java.lang.Boolean> booleanItor50 = booleanList47.listIterator((int) (short) 1);
        boolean boolean51 = shortList11.containsAll((java.util.Collection<java.lang.Boolean>) booleanList47);
        java.util.stream.Stream<java.lang.Boolean> booleanStream52 = booleanList47.stream();
        boolean boolean53 = booleanList47.isEmpty();
        org.ccsds.moims.mo.mal.structures.FloatList floatList54 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long55 = floatList54.getShortForm();
        java.lang.String[] strArray57 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList58 = new java.util.ArrayList<java.lang.String>();
        boolean boolean59 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList58,
            strArray57);
        int int60 = strList58.size();
        java.lang.Boolean[] booleanArray63 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList64 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean65 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList64,
            booleanArray63);
        boolean boolean67 = booleanList64.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream68 = booleanList64.stream();
        boolean boolean69 = strList58.containsAll((java.util.Collection<java.lang.Boolean>) booleanList64);
        boolean boolean70 = floatList54.containsAll((java.util.Collection<java.lang.Boolean>) booleanList64);
        java.util.Spliterator<java.lang.Float> floatSpliterator71 = floatList54.spliterator();
        esa.mo.nmf.NMFException nMFException73 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray74 = nMFException73.getSuppressed();
        int int75 = floatList54.lastIndexOf((java.lang.Object) throwableArray74);
        org.ccsds.moims.mo.mal.structures.Element element76 = floatList54.createElement();
        int int77 = booleanList47.lastIndexOf((java.lang.Object) element76);
        java.lang.Object obj78 = booleanList47.clone();
        boolean boolean79 = longList7.containsAll((java.util.Collection<java.lang.Boolean>) booleanList47);
        boolean boolean80 = octetList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList47);
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertTrue("'" + int2 + "' != '" + (-7) + "'", int2.equals((-7)));
        org.junit.Assert.assertNotNull(uShort3);
        org.junit.Assert.assertNotNull(byteItor4);
        org.junit.Assert.assertNotNull(uShort5);
        org.junit.Assert.assertTrue("'" + int8 + "' != '" + (-13) + "'", int8.equals((-13)));
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 0 + "'", int9 == 0);
        org.junit.Assert.assertNotNull(longItor10);
        org.junit.Assert.assertNotNull(obj12);
        org.junit.Assert.assertNotNull(shortStream13);
        org.junit.Assert.assertNotNull(intArray33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertNotNull(booleanArray38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
        org.junit.Assert.assertNotNull(booleanItor41);
        org.junit.Assert.assertNotNull(booleanStream42);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
        org.junit.Assert.assertNotNull(booleanArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(booleanItor50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + false + "'", !boolean51);
        org.junit.Assert.assertNotNull(booleanStream52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
        org.junit.Assert.assertTrue("'" + long55 + "' != '" + 281475010265084L + "'", long55.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray57);
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + true + "'", boolean59);
        org.junit.Assert.assertTrue("'" + int60 + "' != '" + 1 + "'", int60 == 1);
        org.junit.Assert.assertNotNull(booleanArray63);
        org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + true + "'", boolean65);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
        org.junit.Assert.assertNotNull(booleanStream68);
        org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + false + "'", !boolean69);
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + false + "'", !boolean70);
        org.junit.Assert.assertNotNull(floatSpliterator71);
        org.junit.Assert.assertNotNull(throwableArray74);
        org.junit.Assert.assertTrue("'" + int75 + "' != '" + (-1) + "'", int75 == (-1));
        org.junit.Assert.assertNotNull(element76);
        org.junit.Assert.assertTrue("'" + int77 + "' != '" + (-1) + "'", int77 == (-1));
        org.junit.Assert.assertNotNull(obj78);
        org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + false + "'", !boolean79);
        org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + false + "'", !boolean80);
    }

    @Test
    public void test588() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test588");
        org.ccsds.moims.mo.mal.structures.URI uRI0 = new org.ccsds.moims.mo.mal.structures.URI();
        org.ccsds.moims.mo.mal.structures.UShort uShort1 = uRI0.getServiceNumber();
        java.lang.String str2 = uRI0.getValue();
        org.ccsds.moims.mo.mal.structures.UShort uShort3 = uRI0.getAreaNumber();
        java.lang.String str4 = uRI0.getValue();
        org.ccsds.moims.mo.mal.structures.ShortList shortList5 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray7 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList8 = new java.util.ArrayList<java.lang.String>();
        boolean boolean9 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList8, strArray7);
        int int10 = strList8.size();
        java.lang.Boolean[] booleanArray13 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList14 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList14,
            booleanArray13);
        boolean boolean17 = booleanList14.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream18 = booleanList14.stream();
        boolean boolean19 = strList8.containsAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        boolean boolean20 = shortList5.containsAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        java.util.stream.Stream<java.lang.Short> shortStream21 = shortList5.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element22 = shortList5.createElement();
        esa.mo.nmf.NMFException nMFException24 = new esa.mo.nmf.NMFException("false");
        java.lang.Throwable[] throwableArray25 = nMFException24.getSuppressed();
        int int26 = shortList5.indexOf((java.lang.Object) throwableArray25);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList27 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.String str28 = doubleList27.toString();
        java.lang.Boolean[] booleanArray30 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList31 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean32 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList31,
            booleanArray30);
        java.util.ListIterator<java.lang.Boolean> booleanItor34 = booleanList31.listIterator((int) (short) 1);
        boolean boolean35 = doubleList27.containsAll((java.util.Collection<java.lang.Boolean>) booleanList31);
        java.lang.Byte[] byteArray38 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList39 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean40 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList39,
            byteArray38);
        boolean boolean41 = byteList39.isEmpty();
        int int42 = byteList39.size();
        int int43 = booleanList31.indexOf((java.lang.Object) byteList39);
        java.lang.Boolean[] booleanArray46 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList47 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList47,
            booleanArray46);
        java.util.Iterator<java.lang.Boolean> booleanItor49 = booleanList47.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream50 = booleanList47.stream();
        java.util.Iterator<java.lang.Boolean> booleanItor51 = booleanList47.iterator();
        boolean boolean52 = byteList39.removeAll((java.util.Collection<java.lang.Boolean>) booleanList47);
        boolean boolean53 = shortList5.removeAll((java.util.Collection<java.lang.Boolean>) booleanList47);
        org.ccsds.moims.mo.mal.structures.Element element54 = shortList5.createElement();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList55 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        java.lang.Integer int56 = booleanList55.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element57 = booleanList55.createElement();
        boolean boolean58 = shortList5.containsAll((java.util.Collection<java.lang.Boolean>) booleanList55);
        boolean boolean59 = uRI0.equals((java.lang.Object) boolean58);
        org.junit.Assert.assertNotNull(uShort1);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "" + "'", str2.equals(""));
        org.junit.Assert.assertNotNull(uShort3);
        org.junit.Assert.assertTrue("'" + str4 + "' != '" + "" + "'", str4.equals(""));
        org.junit.Assert.assertNotNull(strArray7);
        org.junit.Assert.assertTrue("'" + boolean9 + "' != '" + true + "'", boolean9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + 1 + "'", int10 == 1);
        org.junit.Assert.assertNotNull(booleanArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertNotNull(booleanStream18);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertNotNull(shortStream21);
        org.junit.Assert.assertNotNull(element22);
        org.junit.Assert.assertNotNull(throwableArray25);
        org.junit.Assert.assertTrue("'" + int26 + "' != '" + (-1) + "'", int26 == (-1));
        org.junit.Assert.assertTrue("'" + str28 + "' != '" + "[]" + "'", str28.equals("[]"));
        org.junit.Assert.assertNotNull(booleanArray30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertNotNull(booleanItor34);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
        org.junit.Assert.assertNotNull(byteArray38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + false + "'", !boolean41);
        org.junit.Assert.assertTrue("'" + int42 + "' != '" + 2 + "'", int42 == 2);
        org.junit.Assert.assertTrue("'" + int43 + "' != '" + (-1) + "'", int43 == (-1));
        org.junit.Assert.assertNotNull(booleanArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertNotNull(booleanItor49);
        org.junit.Assert.assertNotNull(booleanStream50);
        org.junit.Assert.assertNotNull(booleanItor51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
        org.junit.Assert.assertNotNull(element54);
        org.junit.Assert.assertTrue("'" + int56 + "' != '" + (-2) + "'", int56.equals((-2)));
        org.junit.Assert.assertNotNull(element57);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + true + "'", boolean58);
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + false + "'", !boolean59);
    }

    @Test
    public void test589() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test589");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl21 = mCRegistration19.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider22 = mCRegistration19.comServices;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl24 = mCRegistration19.alertService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider25 = mCRegistration19.comServices;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl26 = mCRegistration19.actionService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl21);
        org.junit.Assert.assertNull(cOMServicesProvider22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl24);
        org.junit.Assert.assertNull(cOMServicesProvider25);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl26);
    }

    @Test
    public void test590() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test590");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        java.util.stream.Stream<java.lang.String> strStream25 = stringList12.parallelStream();
        java.lang.Long[] longArray30 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList31 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean32 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList31,
            longArray30);
        java.lang.Object obj33 = null;
        boolean boolean34 = longList31.contains(obj33);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger36 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean37 = longList31.contains((java.lang.Object) (byte) 100);
        java.util.Iterator<java.lang.Long> longItor38 = longList31.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort39 = org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
        boolean boolean40 = longList31.remove((java.lang.Object) uShort39);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList41 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int42 = uShortList41.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor43 = uShortList41.iterator();
        java.lang.Object[] objArray44 = uShortList41.toArray();
        uShortList41.clear();
        uShortList41.clear();
        boolean boolean47 = uShortList41.isEmpty();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor48 = uShortList41.iterator();
        boolean boolean49 = longList31.contains((java.lang.Object) uShortList41);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList50 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.UShort uShort51 = booleanList50.getAreaNumber();
        boolean boolean52 = longList31.retainAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        java.lang.Integer int53 = booleanList50.getTypeShortForm();
        boolean boolean55 = booleanList50.add((java.lang.Boolean) true);
        boolean boolean56 = stringList12.retainAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        java.lang.Integer int57 = booleanList50.getTypeShortForm();
        java.lang.Object obj58 = booleanList50.clone();
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(strStream25);
        org.junit.Assert.assertNotNull(longArray30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertNotNull(longItor38);
        org.junit.Assert.assertNotNull(uShort39);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertTrue("'" + int42 + "' != '" + (-10) + "'", int42.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor43);
        org.junit.Assert.assertNotNull(objArray44);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
        org.junit.Assert.assertNotNull(uShortItor48);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertNotNull(uShort51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
        org.junit.Assert.assertTrue("'" + int53 + "' != '" + (-2) + "'", int53.equals((-2)));
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertTrue("'" + int57 + "' != '" + (-2) + "'", int57.equals((-2)));
        org.junit.Assert.assertNotNull(obj58);
    }

    @Test
    public void test591() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test591");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl2 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl3 = mCServicesProviderNMF0
            .getActionService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl2);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl3);
    }

    @Test
    public void test592() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test592");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray2 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList3 = new java.util.ArrayList<java.lang.String>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList3, strArray2);
        int int5 = strList3.size();
        java.lang.Boolean[] booleanArray8 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList9 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList9,
            booleanArray8);
        boolean boolean12 = booleanList9.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream13 = booleanList9.stream();
        boolean boolean14 = strList3.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        boolean boolean15 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        java.util.stream.Stream<java.lang.Short> shortStream16 = shortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element17 = shortList0.createElement();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("false");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = shortList0.indexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList22 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.String str23 = doubleList22.toString();
        java.lang.Boolean[] booleanArray25 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList26 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList26,
            booleanArray25);
        java.util.ListIterator<java.lang.Boolean> booleanItor29 = booleanList26.listIterator((int) (short) 1);
        boolean boolean30 = doubleList22.containsAll((java.util.Collection<java.lang.Boolean>) booleanList26);
        java.lang.Byte[] byteArray33 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList34 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList34,
            byteArray33);
        boolean boolean36 = byteList34.isEmpty();
        int int37 = byteList34.size();
        int int38 = booleanList26.indexOf((java.lang.Object) byteList34);
        java.lang.Boolean[] booleanArray41 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList42 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList42,
            booleanArray41);
        java.util.Iterator<java.lang.Boolean> booleanItor44 = booleanList42.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream45 = booleanList42.stream();
        java.util.Iterator<java.lang.Boolean> booleanItor46 = booleanList42.iterator();
        boolean boolean47 = byteList34.removeAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        boolean boolean48 = shortList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        org.ccsds.moims.mo.mal.structures.Element element49 = shortList0.createElement();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList50 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        java.lang.Integer int51 = booleanList50.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element52 = booleanList50.createElement();
        boolean boolean53 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        java.lang.Class<?> wildcardClass54 = shortList0.getClass();
        org.junit.Assert.assertNotNull(strArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + 1 + "'", int5 == 1);
        org.junit.Assert.assertNotNull(booleanArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(booleanStream13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertNotNull(shortStream16);
        org.junit.Assert.assertNotNull(element17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + str23 + "' != '" + "[]" + "'", str23.equals("[]"));
        org.junit.Assert.assertNotNull(booleanArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(booleanItor29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertNotNull(byteArray33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertTrue("'" + int37 + "' != '" + 2 + "'", int37 == 2);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertNotNull(booleanArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertNotNull(booleanItor44);
        org.junit.Assert.assertNotNull(booleanStream45);
        org.junit.Assert.assertNotNull(booleanItor46);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertNotNull(element49);
        org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-2) + "'", int51.equals((-2)));
        org.junit.Assert.assertNotNull(element52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
        org.junit.Assert.assertNotNull(wildcardClass54);
    }

    @Test
    public void test593() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test593");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        org.ccsds.moims.mo.mal.structures.UShort uShort17 = org.ccsds.moims.mo.mal.structures.LongList.AREA_SHORT_FORM;
        java.lang.Long long18 = uShort17.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort19 = uShort17.getServiceNumber();
        boolean boolean20 = floatList0.remove((java.lang.Object) uShort17);
        java.util.Spliterator<java.lang.Float> floatSpliterator21 = floatList0.spliterator();
        java.lang.Integer int22 = floatList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort23 = floatList0.getAreaNumber();
        boolean boolean25 = floatList0.add((java.lang.Float) 0.0f);
        esa.mo.nmf.NMFException nMFException28 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray29 = nMFException28.getSuppressed();
        java.lang.Throwable[] throwableArray30 = nMFException28.getSuppressed();
        esa.mo.nmf.NMFException nMFException33 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray34 = nMFException33.getSuppressed();
        java.lang.Throwable[] throwableArray35 = nMFException33.getSuppressed();
        esa.mo.nmf.NMFException nMFException37 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray38 = nMFException37.getSuppressed();
        nMFException33.addSuppressed((java.lang.Throwable) nMFException37);
        esa.mo.nmf.NMFException nMFException40 = new esa.mo.nmf.NMFException("", (java.lang.Throwable) nMFException37);
        nMFException28.addSuppressed((java.lang.Throwable) nMFException40);
        esa.mo.nmf.NMFException nMFException42 = new esa.mo.nmf.NMFException("[10, -1, -1, 10]",
            (java.lang.Throwable) nMFException40);
        int int43 = floatList0.lastIndexOf((java.lang.Object) nMFException42);
        java.lang.Integer int44 = floatList0.getTypeShortForm();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(uShort17);
        org.junit.Assert.assertTrue("'" + long18 + "' != '" + 281474993487882L + "'", long18.equals(281474993487882L));
        org.junit.Assert.assertNotNull(uShort19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertNotNull(floatSpliterator21);
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-4) + "'", int22.equals((-4)));
        org.junit.Assert.assertNotNull(uShort23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertNotNull(throwableArray29);
        org.junit.Assert.assertNotNull(throwableArray30);
        org.junit.Assert.assertNotNull(throwableArray34);
        org.junit.Assert.assertNotNull(throwableArray35);
        org.junit.Assert.assertNotNull(throwableArray38);
        org.junit.Assert.assertTrue("'" + int43 + "' != '" + (-1) + "'", int43 == (-1));
        org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-4) + "'", int44.equals((-4)));
    }

    @Test
    public void test594() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test594");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray2 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList3 = new java.util.ArrayList<java.lang.String>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList3, strArray2);
        int int5 = strList3.size();
        java.lang.Boolean[] booleanArray8 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList9 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList9,
            booleanArray8);
        boolean boolean12 = booleanList9.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream13 = booleanList9.stream();
        boolean boolean14 = strList3.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        boolean boolean15 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList9);
        java.util.stream.Stream<java.lang.Short> shortStream16 = shortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element17 = shortList0.createElement();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("false");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = shortList0.indexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList22 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.String str23 = doubleList22.toString();
        java.lang.Boolean[] booleanArray25 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList26 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean27 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList26,
            booleanArray25);
        java.util.ListIterator<java.lang.Boolean> booleanItor29 = booleanList26.listIterator((int) (short) 1);
        boolean boolean30 = doubleList22.containsAll((java.util.Collection<java.lang.Boolean>) booleanList26);
        java.lang.Byte[] byteArray33 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList34 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList34,
            byteArray33);
        boolean boolean36 = byteList34.isEmpty();
        int int37 = byteList34.size();
        int int38 = booleanList26.indexOf((java.lang.Object) byteList34);
        java.lang.Boolean[] booleanArray41 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList42 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList42,
            booleanArray41);
        java.util.Iterator<java.lang.Boolean> booleanItor44 = booleanList42.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream45 = booleanList42.stream();
        java.util.Iterator<java.lang.Boolean> booleanItor46 = booleanList42.iterator();
        boolean boolean47 = byteList34.removeAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        boolean boolean48 = shortList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        org.ccsds.moims.mo.mal.structures.Element element49 = shortList0.createElement();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList50 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        java.lang.Integer int51 = booleanList50.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element52 = booleanList50.createElement();
        boolean boolean53 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        org.ccsds.moims.mo.mal.structures.UShort uShort54 = booleanList50.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort55 = booleanList50.getServiceNumber();
        org.junit.Assert.assertNotNull(strArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + int5 + "' != '" + 1 + "'", int5 == 1);
        org.junit.Assert.assertNotNull(booleanArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(booleanStream13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertNotNull(shortStream16);
        org.junit.Assert.assertNotNull(element17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + str23 + "' != '" + "[]" + "'", str23.equals("[]"));
        org.junit.Assert.assertNotNull(booleanArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(booleanItor29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertNotNull(byteArray33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertTrue("'" + int37 + "' != '" + 2 + "'", int37 == 2);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertNotNull(booleanArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertNotNull(booleanItor44);
        org.junit.Assert.assertNotNull(booleanStream45);
        org.junit.Assert.assertNotNull(booleanItor46);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertNotNull(element49);
        org.junit.Assert.assertTrue("'" + int51 + "' != '" + (-2) + "'", int51.equals((-2)));
        org.junit.Assert.assertNotNull(element52);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
        org.junit.Assert.assertNotNull(uShort54);
        org.junit.Assert.assertNotNull(uShort55);
    }

    @Test
    public void test595() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test595");
        java.lang.Byte[] byteArray2 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList3 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList3, byteArray2);
        boolean boolean5 = byteList3.isEmpty();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList7 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.util.stream.Stream<java.lang.Integer> intStream8 = integerList7.parallelStream();
        java.util.stream.Stream<java.lang.Integer> intStream9 = integerList7.stream();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray10 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList11 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean12 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList11, uRIArray10);
        java.lang.Boolean[] booleanArray15 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList16 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean17 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList16,
            booleanArray15);
        java.util.Iterator<java.lang.Boolean> booleanItor18 = booleanList16.iterator();
        boolean boolean19 = uRIList11.retainAll((java.util.Collection<java.lang.Boolean>) booleanList16);
        java.lang.Boolean[] booleanArray22 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList23 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean24 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList23,
            booleanArray22);
        boolean boolean26 = booleanList23.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream27 = booleanList23.stream();
        boolean boolean28 = uRIList11.retainAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        boolean boolean29 = integerList7.removeAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        boolean boolean30 = byteList3.retainAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        java.util.Iterator<java.lang.Byte> byteItor31 = byteList3.iterator();
        org.ccsds.moims.mo.mal.structures.FloatList floatList32 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long33 = floatList32.getShortForm();
        java.lang.String[] strArray35 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList36 = new java.util.ArrayList<java.lang.String>();
        boolean boolean37 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList36,
            strArray35);
        int int38 = strList36.size();
        java.lang.Boolean[] booleanArray41 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList42 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList42,
            booleanArray41);
        boolean boolean45 = booleanList42.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream46 = booleanList42.stream();
        boolean boolean47 = strList36.containsAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        boolean boolean48 = floatList32.containsAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        java.util.Spliterator<java.lang.Float> floatSpliterator49 = floatList32.spliterator();
        esa.mo.nmf.NMFException nMFException51 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray52 = nMFException51.getSuppressed();
        int int53 = floatList32.lastIndexOf((java.lang.Object) throwableArray52);
        org.ccsds.moims.mo.mal.structures.LongList longList54 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int55 = floatList32.lastIndexOf((java.lang.Object) longList54);
        java.util.Iterator<java.lang.Long> longItor56 = longList54.iterator();
        boolean boolean57 = byteList3.equals((java.lang.Object) longList54);
        org.ccsds.moims.mo.mal.structures.Union union59 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Float) 100.0f);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet60 = union59.getAreaVersion();
        java.lang.Integer int61 = union59.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.FloatList floatList62 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long63 = floatList62.getShortForm();
        java.lang.String[] strArray65 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList66 = new java.util.ArrayList<java.lang.String>();
        boolean boolean67 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList66,
            strArray65);
        int int68 = strList66.size();
        java.lang.Boolean[] booleanArray71 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList72 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean73 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList72,
            booleanArray71);
        boolean boolean75 = booleanList72.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream76 = booleanList72.stream();
        boolean boolean77 = strList66.containsAll((java.util.Collection<java.lang.Boolean>) booleanList72);
        boolean boolean78 = floatList62.containsAll((java.util.Collection<java.lang.Boolean>) booleanList72);
        org.ccsds.moims.mo.mal.structures.URI uRI79 = new org.ccsds.moims.mo.mal.structures.URI();
        int int80 = floatList62.lastIndexOf((java.lang.Object) uRI79);
        org.ccsds.moims.mo.mal.structures.UShort uShort81 = uRI79.getServiceNumber();
        boolean boolean82 = union59.equals((java.lang.Object) uShort81);
        boolean boolean83 = longList54.remove((java.lang.Object) uShort81);
        java.util.Spliterator<java.lang.Long> longSpliterator84 = longList54.spliterator();
        org.junit.Assert.assertNotNull(byteArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertNotNull(intStream8);
        org.junit.Assert.assertNotNull(intStream9);
        org.junit.Assert.assertNotNull(uRIArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
        org.junit.Assert.assertNotNull(booleanArray15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertNotNull(booleanItor18);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
        org.junit.Assert.assertNotNull(booleanArray22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertNotNull(booleanStream27);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", !boolean28);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertNotNull(byteItor31);
        org.junit.Assert.assertTrue("'" + long33 + "' != '" + 281475010265084L + "'", long33.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray35);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + 1 + "'", int38 == 1);
        org.junit.Assert.assertNotNull(booleanArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
        org.junit.Assert.assertNotNull(booleanStream46);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertNotNull(floatSpliterator49);
        org.junit.Assert.assertNotNull(throwableArray52);
        org.junit.Assert.assertTrue("'" + int53 + "' != '" + (-1) + "'", int53 == (-1));
        org.junit.Assert.assertTrue("'" + int55 + "' != '" + (-1) + "'", int55 == (-1));
        org.junit.Assert.assertNotNull(longItor56);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
        org.junit.Assert.assertNotNull(uOctet60);
        org.junit.Assert.assertTrue("'" + int61 + "' != '" + 4 + "'", int61.equals(4));
        org.junit.Assert.assertTrue("'" + long63 + "' != '" + 281475010265084L + "'", long63.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray65);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + true + "'", boolean67);
        org.junit.Assert.assertTrue("'" + int68 + "' != '" + 1 + "'", int68 == 1);
        org.junit.Assert.assertNotNull(booleanArray71);
        org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + true + "'", boolean73);
        org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + true + "'", boolean75);
        org.junit.Assert.assertNotNull(booleanStream76);
        org.junit.Assert.assertTrue("'" + boolean77 + "' != '" + false + "'", !boolean77);
        org.junit.Assert.assertTrue("'" + boolean78 + "' != '" + false + "'", !boolean78);
        org.junit.Assert.assertTrue("'" + int80 + "' != '" + (-1) + "'", int80 == (-1));
        org.junit.Assert.assertNotNull(uShort81);
        org.junit.Assert.assertTrue("'" + boolean82 + "' != '" + false + "'", !boolean82);
        org.junit.Assert.assertTrue("'" + boolean83 + "' != '" + false + "'", !boolean83);
        org.junit.Assert.assertNotNull(longSpliterator84);
    }

    @Test
    public void test596() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test596");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int1 = uShortList0.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor2 = uShortList0.iterator();
        org.ccsds.moims.mo.mal.structures.ULong uLong4 = new org.ccsds.moims.mo.mal.structures.ULong();
        org.ccsds.moims.mo.mal.structures.UShort uShort5 = uLong4.getAreaNumber();
        java.lang.String str6 = uShort5.toString();
        uShortList0.add(0, uShort5);
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray8 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList9 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean10 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList9, uRIArray8);
        java.lang.Boolean[] booleanArray13 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList14 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean15 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList14,
            booleanArray13);
        java.util.Iterator<java.lang.Boolean> booleanItor16 = booleanList14.iterator();
        boolean boolean17 = uRIList9.retainAll((java.util.Collection<java.lang.Boolean>) booleanList14);
        java.lang.Boolean[] booleanArray20 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList21 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList21,
            booleanArray20);
        boolean boolean24 = booleanList21.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream25 = booleanList21.stream();
        boolean boolean26 = uRIList9.retainAll((java.util.Collection<java.lang.Boolean>) booleanList21);
        java.lang.Boolean[] booleanArray29 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList30 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean31 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList30,
            booleanArray29);
        java.util.Iterator<java.lang.Boolean> booleanItor32 = booleanList30.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream33 = booleanList30.stream();
        boolean boolean34 = uRIList9.containsAll((java.util.Collection<java.lang.Boolean>) booleanList30);
        int int35 = uShortList0.lastIndexOf((java.lang.Object) booleanList30);
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream36 = uShortList0.parallelStream();
        uShortList0.trimToSize();
        java.lang.Integer int38 = uShortList0.getTypeShortForm();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF39 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl40 = mCServicesProviderNMF39
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl41 = mCServicesProviderNMF39
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl42 = mCServicesProviderNMF39
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCServicesProviderNMF39
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCServicesProviderNMF39
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl45 = mCServicesProviderNMF39
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl46 = mCServicesProviderNMF39
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl47 = mCServicesProviderNMF39
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl48 = mCServicesProviderNMF39
            .getAggregationService();
        boolean boolean49 = uShortList0.remove((java.lang.Object) aggregationProviderServiceImpl48);
        java.lang.Integer int50 = uShortList0.getTypeShortForm();
        org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-10) + "'", int1.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor2);
        org.junit.Assert.assertNotNull(uShort5);
        org.junit.Assert.assertTrue("'" + str6 + "' != '" + "1" + "'", str6.equals("1"));
        org.junit.Assert.assertNotNull(uRIArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", !boolean10);
        org.junit.Assert.assertNotNull(booleanArray13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertNotNull(booleanItor16);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
        org.junit.Assert.assertNotNull(booleanArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertNotNull(booleanStream25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(booleanArray29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertNotNull(booleanItor32);
        org.junit.Assert.assertNotNull(booleanStream33);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + (-1) + "'", int35 == (-1));
        org.junit.Assert.assertNotNull(uShortStream36);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-10) + "'", int38.equals((-10)));
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl40);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl41);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl42);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl45);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl46);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl47);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl48);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertTrue("'" + int50 + "' != '" + (-10) + "'", int50.equals((-10)));
    }

    @Test
    public void test597() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test597");
        esa.mo.nmf.NMFException nMFException2 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray3 = nMFException2.getSuppressed();
        java.lang.Throwable[] throwableArray4 = nMFException2.getSuppressed();
        java.lang.Throwable[] throwableArray5 = nMFException2.getSuppressed();
        esa.mo.nmf.NMFException nMFException6 = new esa.mo.nmf.NMFException("[100, 0]",
            (java.lang.Throwable) nMFException2);
        org.ccsds.moims.mo.mal.structures.Duration duration8 = new org.ccsds.moims.mo.mal.structures.Duration(
            (double) 10.0f);
        esa.mo.nmf.NMFException nMFException10 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray11 = nMFException10.getSuppressed();
        java.lang.Throwable[] throwableArray12 = nMFException10.getSuppressed();
        boolean boolean13 = duration8.equals((java.lang.Object) nMFException10);
        nMFException6.addSuppressed((java.lang.Throwable) nMFException10);
        org.junit.Assert.assertNotNull(throwableArray3);
        org.junit.Assert.assertNotNull(throwableArray4);
        org.junit.Assert.assertNotNull(throwableArray5);
        org.junit.Assert.assertNotNull(throwableArray11);
        org.junit.Assert.assertNotNull(throwableArray12);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
    }

    @Test
    public void test598() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test598");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl5 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl6 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF0
            .getParameterService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl5);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl6);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
    }

    @Test
    public void test599() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test599");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCRegistration19.aggregationService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider22 = mCRegistration19.comServices;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNull(cOMServicesProvider22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
    }

    @Test
    public void test600() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test600");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl2 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl3 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl4 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl5 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl6 = mCServicesProviderNMF0
            .getAlertService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl2);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl3);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl4);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl5);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl6);
    }

    @Test
    public void test601() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test601");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl21 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl24 = mCRegistration19.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider25 = mCRegistration19.comServices;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl24);
        org.junit.Assert.assertNull(cOMServicesProvider25);
    }

    @Test
    public void test602() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test602");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        org.ccsds.moims.mo.mal.structures.UShort uShort24 = longList22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort25 = longList22.getServiceNumber();
        java.lang.Long long26 = longList22.getShortForm();
        try {
            java.util.ListIterator<java.lang.Long> longItor28 = longList22.listIterator(11);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 11");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(uShort24);
        org.junit.Assert.assertNotNull(uShort25);
        org.junit.Assert.assertTrue("'" + long26 + "' != '" + 281475010265075L + "'", long26.equals(281475010265075L));
    }

    @Test
    public void test603() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test603");
        esa.mo.nmf.NMFException nMFException0 = new esa.mo.nmf.NMFException();
        java.lang.String str1 = nMFException0.toString();
        java.lang.Throwable[] throwableArray2 = nMFException0.getSuppressed();
        org.junit.Assert.assertTrue("'" + str1 + "' != '" + "esa.mo.nmf.NMFException" + "'", str1.equals(
            "esa.mo.nmf.NMFException"));
        org.junit.Assert.assertNotNull(throwableArray2);
    }

    @Test
    public void test604() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test604");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl22 = mCRegistration19.alertService;
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl23 = mCRegistration19.alertService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl22);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl23);
    }

    @Test
    public void test605() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test605");
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList0 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element1 = booleanList0.createElement();
        java.lang.Long long2 = booleanList0.getShortForm();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = booleanList0.getAreaVersion();
        java.util.stream.Stream<java.lang.Boolean> booleanStream4 = booleanList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray5 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList6 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean7 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList6, uRIArray5);
        java.lang.Boolean[] booleanArray10 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList11 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList11,
            booleanArray10);
        java.util.Iterator<java.lang.Boolean> booleanItor13 = booleanList11.iterator();
        boolean boolean14 = uRIList6.retainAll((java.util.Collection<java.lang.Boolean>) booleanList11);
        booleanList11.clear();
        java.lang.Byte[] byteArray20 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList21 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList21,
            byteArray20);
        boolean boolean24 = byteList21.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj25 = byteList21.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream26 = byteList21.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList28 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int29 = byteList21.indexOf((java.lang.Object) stringList28);
        byte[] byteArray33 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob34 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray33);
        int int35 = blob34.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob36 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean37 = blob34.equals((java.lang.Object) blob36);
        boolean boolean38 = stringList28.remove((java.lang.Object) blob36);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF39 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean40 = stringList28.equals((java.lang.Object) mCServicesProviderNMF39);
        java.util.stream.Stream<java.lang.String> strStream41 = stringList28.parallelStream();
        java.lang.Long[] longArray46 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList47 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList47,
            longArray46);
        java.lang.Object obj49 = null;
        boolean boolean50 = longList47.contains(obj49);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger52 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean53 = longList47.contains((java.lang.Object) (byte) 100);
        java.util.Iterator<java.lang.Long> longItor54 = longList47.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort55 = org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
        boolean boolean56 = longList47.remove((java.lang.Object) uShort55);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList57 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int58 = uShortList57.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor59 = uShortList57.iterator();
        java.lang.Object[] objArray60 = uShortList57.toArray();
        uShortList57.clear();
        uShortList57.clear();
        boolean boolean63 = uShortList57.isEmpty();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor64 = uShortList57.iterator();
        boolean boolean65 = longList47.contains((java.lang.Object) uShortList57);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList66 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.UShort uShort67 = booleanList66.getAreaNumber();
        boolean boolean68 = longList47.retainAll((java.util.Collection<java.lang.Boolean>) booleanList66);
        java.lang.Integer int69 = booleanList66.getTypeShortForm();
        boolean boolean71 = booleanList66.add((java.lang.Boolean) true);
        boolean boolean72 = stringList28.retainAll((java.util.Collection<java.lang.Boolean>) booleanList66);
        org.ccsds.moims.mo.mal.structures.UShort uShort73 = booleanList66.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet74 = booleanList66.getAreaVersion();
        boolean boolean75 = booleanList11.containsAll((java.util.Collection<java.lang.Boolean>) booleanList66);
        boolean boolean76 = booleanList0.addAll((java.util.Collection<java.lang.Boolean>) booleanList66);
        boolean boolean78 = booleanList0.add((java.lang.Boolean) false);
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265086L + "'", long2.equals(281475010265086L));
        org.junit.Assert.assertNotNull(uOctet3);
        org.junit.Assert.assertNotNull(booleanStream4);
        org.junit.Assert.assertNotNull(uRIArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(booleanArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(booleanItor13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertNotNull(byteArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertNotNull(obj25);
        org.junit.Assert.assertNotNull(byteStream26);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-1) + "'", int29 == (-1));
        org.junit.Assert.assertNotNull(byteArray33);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + 3 + "'", int35 == 3);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertNotNull(strStream41);
        org.junit.Assert.assertNotNull(longArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + false + "'", !boolean50);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
        org.junit.Assert.assertNotNull(longItor54);
        org.junit.Assert.assertNotNull(uShort55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertTrue("'" + int58 + "' != '" + (-10) + "'", int58.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor59);
        org.junit.Assert.assertNotNull(objArray60);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertNotNull(uShortItor64);
        org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
        org.junit.Assert.assertNotNull(uShort67);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + true + "'", boolean68);
        org.junit.Assert.assertTrue("'" + int69 + "' != '" + (-2) + "'", int69.equals((-2)));
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
        org.junit.Assert.assertNotNull(uShort73);
        org.junit.Assert.assertNotNull(uOctet74);
        org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + false + "'", !boolean75);
        org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + true + "'", boolean76);
        org.junit.Assert.assertTrue("'" + boolean78 + "' != '" + true + "'", boolean78);
    }

    @Test
    public void test606() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test606");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int1 = uShortList0.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor2 = uShortList0.iterator();
        java.lang.Long long3 = uShortList0.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = uShortList0.getAreaNumber();
        esa.mo.nmf.NMFException nMFException6 = new esa.mo.nmf.NMFException("esa.mo.nmf.NMFException");
        java.lang.Byte[] byteArray9 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList10 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList10, byteArray9);
        boolean boolean12 = byteList10.isEmpty();
        int int14 = byteList10.indexOf((java.lang.Object) 'a');
        int int16 = byteList10.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray18 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList19 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean20 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList19,
            booleanArray18);
        java.util.ListIterator<java.lang.Boolean> booleanItor22 = booleanList19.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream23 = booleanList19.parallelStream();
        boolean boolean25 = booleanList19.add((java.lang.Boolean) false);
        boolean boolean26 = byteList10.retainAll((java.util.Collection<java.lang.Boolean>) booleanList19);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList27 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj28 = uShortList27.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream29 = uShortList27
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor30 = uShortList27.iterator();
        boolean boolean31 = byteList10.equals((java.lang.Object) uShortItor30);
        org.ccsds.moims.mo.mal.structures.ShortList shortList32 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj33 = shortList32.clone();
        boolean boolean34 = byteList10.equals((java.lang.Object) shortList32);
        esa.mo.nmf.NMFException nMFException36 = new esa.mo.nmf.NMFException("-1");
        boolean boolean37 = byteList10.equals((java.lang.Object) nMFException36);
        esa.mo.nmf.NMFException nMFException39 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray40 = nMFException39.getSuppressed();
        java.lang.Throwable[] throwableArray41 = nMFException39.getSuppressed();
        esa.mo.nmf.NMFException nMFException43 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray44 = nMFException43.getSuppressed();
        nMFException39.addSuppressed((java.lang.Throwable) nMFException43);
        java.lang.String str46 = nMFException43.toString();
        esa.mo.nmf.NMFException nMFException48 = new esa.mo.nmf.NMFException("1");
        esa.mo.nmf.NMFException[] nMFExceptionArray49 = new esa.mo.nmf.NMFException[]{nMFException6, nMFException36,
                                                                                      nMFException43, nMFException48};
        esa.mo.nmf.NMFException[] nMFExceptionArray50 = uShortList0.toArray(nMFExceptionArray49);
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator51 = uShortList0.spliterator();
        org.junit.Assert.assertTrue("'" + int1 + "' != '" + (-10) + "'", int1.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor2);
        org.junit.Assert.assertTrue("'" + long3 + "' != '" + 281475010265078L + "'", long3.equals(281475010265078L));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertNotNull(byteArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
        org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
        org.junit.Assert.assertTrue("'" + int16 + "' != '" + (-1) + "'", int16 == (-1));
        org.junit.Assert.assertNotNull(booleanArray18);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
        org.junit.Assert.assertNotNull(booleanItor22);
        org.junit.Assert.assertNotNull(booleanStream23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertNotNull(obj28);
        org.junit.Assert.assertNotNull(uShortStream29);
        org.junit.Assert.assertNotNull(uShortItor30);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
        org.junit.Assert.assertNotNull(obj33);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertNotNull(throwableArray40);
        org.junit.Assert.assertNotNull(throwableArray41);
        org.junit.Assert.assertNotNull(throwableArray44);
        org.junit.Assert.assertTrue("'" + str46 + "' != '" + "esa.mo.nmf.NMFException: 0" + "'", str46.equals(
            "esa.mo.nmf.NMFException: 0"));
        org.junit.Assert.assertNotNull(nMFExceptionArray49);
        org.junit.Assert.assertNotNull(nMFExceptionArray50);
        org.junit.Assert.assertNotNull(uShortSpliterator51);
    }

    @Test
    public void test607() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test607");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        boolean boolean2 = uShortList0.isEmpty();
        org.ccsds.moims.mo.mal.structures.StringList stringList4 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        java.lang.Double[] doubleArray6 = new java.lang.Double[]{0.0d};
        java.util.ArrayList<java.lang.Double> doubleList7 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList7,
            doubleArray6);
        java.util.Spliterator<java.lang.Double> doubleSpliterator9 = doubleList7.spliterator();
        java.util.Iterator<java.lang.Double> doubleItor10 = doubleList7.iterator();
        boolean boolean11 = stringList4.equals((java.lang.Object) doubleItor10);
        java.lang.Long long12 = stringList4.getShortForm();
        java.lang.Long[] longArray17 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList18 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean19 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList18,
            longArray17);
        java.lang.Object obj20 = null;
        boolean boolean21 = longList18.contains(obj20);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger23 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean24 = longList18.contains((java.lang.Object) (byte) 100);
        java.lang.Boolean[] booleanArray27 = new java.lang.Boolean[]{false, false};
        java.util.ArrayList<java.lang.Boolean> booleanList28 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList28,
            booleanArray27);
        boolean boolean30 = longList18.containsAll((java.util.Collection<java.lang.Boolean>) booleanList28);
        java.lang.Boolean[] booleanArray33 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList34 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList34,
            booleanArray33);
        java.util.Iterator<java.lang.Boolean> booleanItor36 = booleanList34.iterator();
        int int37 = longList18.indexOf((java.lang.Object) booleanList34);
        longList18.trimToSize();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray39 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList40 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean41 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList40, uRIArray39);
        java.lang.Boolean[] booleanArray44 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList45 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean46 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList45,
            booleanArray44);
        java.util.Iterator<java.lang.Boolean> booleanItor47 = booleanList45.iterator();
        boolean boolean48 = uRIList40.retainAll((java.util.Collection<java.lang.Boolean>) booleanList45);
        booleanList45.clear();
        boolean boolean50 = longList18.retainAll((java.util.Collection<java.lang.Boolean>) booleanList45);
        boolean boolean51 = stringList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList45);
        boolean boolean52 = uShortList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList45);
        byte[] byteArray56 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob57 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray56);
        byte[] byteArray58 = blob57.getValue();
        org.ccsds.moims.mo.mal.structures.Blob blob61 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray58, 6,
            (-2));
        org.ccsds.moims.mo.mal.structures.Blob blob64 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray58, 65535,
            (int) '#');
        boolean boolean65 = uShortList0.equals((java.lang.Object) 65535);
        int int66 = uShortList0.size();
        uShortList0.trimToSize();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream68 = uShortList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet69 = uShortList0.getAreaVersion();
        esa.mo.nmf.NMFException nMFException72 = new esa.mo.nmf.NMFException("false");
        esa.mo.nmf.NMFException nMFException73 = new esa.mo.nmf.NMFException("esa.mo.nmf.NMFException: -1",
            (java.lang.Throwable) nMFException72);
        boolean boolean74 = uOctet69.equals((java.lang.Object) "esa.mo.nmf.NMFException: -1");
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertTrue("'" + boolean2 + "' != '" + true + "'", boolean2);
        org.junit.Assert.assertNotNull(doubleArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(doubleSpliterator9);
        org.junit.Assert.assertNotNull(doubleItor10);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertTrue("'" + long12 + "' != '" + 281475010265073L + "'", long12.equals(281475010265073L));
        org.junit.Assert.assertNotNull(longArray17);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + true + "'", boolean19);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(booleanArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + false + "'", !boolean30);
        org.junit.Assert.assertNotNull(booleanArray33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertNotNull(booleanItor36);
        org.junit.Assert.assertTrue("'" + int37 + "' != '" + (-1) + "'", int37 == (-1));
        org.junit.Assert.assertNotNull(uRIArray39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + false + "'", !boolean41);
        org.junit.Assert.assertNotNull(booleanArray44);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertNotNull(booleanItor47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + true + "'", boolean50);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + false + "'", !boolean52);
        org.junit.Assert.assertNotNull(byteArray56);
        org.junit.Assert.assertNotNull(byteArray58);
        org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
        org.junit.Assert.assertTrue("'" + int66 + "' != '" + 0 + "'", int66 == 0);
        org.junit.Assert.assertNotNull(uShortStream68);
        org.junit.Assert.assertNotNull(uOctet69);
        org.junit.Assert.assertTrue("'" + boolean74 + "' != '" + false + "'", !boolean74);
    }

    @Test
    public void test608() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test608");
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.Element element1 = doubleList0.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = doubleList0.getServiceNumber();
        doubleList0.ensureCapacity(3);
        doubleList0.trimToSize();
        org.ccsds.moims.mo.mal.structures.UShort uShort6 = doubleList0.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort7 = doubleList0.getServiceNumber();
        java.util.Spliterator<java.lang.Double> doubleSpliterator8 = doubleList0.spliterator();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF9 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF9
            .getParameterService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl11 = mCServicesProviderNMF9
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl12 = mCServicesProviderNMF9
            .getAlertService();
        boolean boolean13 = doubleList0.remove((java.lang.Object) alertProviderServiceImpl12);
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertNotNull(uShort6);
        org.junit.Assert.assertNotNull(uShort7);
        org.junit.Assert.assertNotNull(doubleSpliterator8);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl11);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl12);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", !boolean13);
    }

    @Test
    public void test609() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test609");
        java.lang.Byte[] byteArray2 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList3 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList3, byteArray2);
        boolean boolean5 = byteList3.isEmpty();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList7 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.util.stream.Stream<java.lang.Integer> intStream8 = integerList7.parallelStream();
        java.util.stream.Stream<java.lang.Integer> intStream9 = integerList7.stream();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray10 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList11 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean12 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList11, uRIArray10);
        java.lang.Boolean[] booleanArray15 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList16 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean17 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList16,
            booleanArray15);
        java.util.Iterator<java.lang.Boolean> booleanItor18 = booleanList16.iterator();
        boolean boolean19 = uRIList11.retainAll((java.util.Collection<java.lang.Boolean>) booleanList16);
        java.lang.Boolean[] booleanArray22 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList23 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean24 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList23,
            booleanArray22);
        boolean boolean26 = booleanList23.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream27 = booleanList23.stream();
        boolean boolean28 = uRIList11.retainAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        boolean boolean29 = integerList7.removeAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        boolean boolean30 = byteList3.retainAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        java.util.Spliterator<java.lang.Byte> byteSpliterator31 = byteList3.spliterator();
        int int32 = byteList3.size();
        org.ccsds.moims.mo.mal.structures.UShortList uShortList33 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int34 = uShortList33.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor35 = uShortList33.iterator();
        java.lang.Object[] objArray36 = uShortList33.toArray();
        uShortList33.clear();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream38 = uShortList33.stream();
        java.lang.String str39 = uShortList33.toString();
        org.ccsds.moims.mo.mal.structures.FloatList floatList40 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long41 = floatList40.getShortForm();
        floatList40.trimToSize();
        java.lang.String str43 = floatList40.toString();
        java.util.Spliterator<java.lang.Float> floatSpliterator44 = floatList40.spliterator();
        java.lang.Byte[] byteArray47 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList48 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean49 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList48,
            byteArray47);
        boolean boolean50 = byteList48.isEmpty();
        int int52 = byteList48.indexOf((java.lang.Object) 'a');
        int int54 = byteList48.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray56 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList57 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean58 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList57,
            booleanArray56);
        java.util.ListIterator<java.lang.Boolean> booleanItor60 = booleanList57.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream61 = booleanList57.parallelStream();
        boolean boolean63 = booleanList57.add((java.lang.Boolean) false);
        boolean boolean64 = byteList48.retainAll((java.util.Collection<java.lang.Boolean>) booleanList57);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList65 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj66 = uShortList65.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream67 = uShortList65
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor68 = uShortList65.iterator();
        boolean boolean69 = byteList48.equals((java.lang.Object) uShortItor68);
        org.ccsds.moims.mo.mal.structures.ShortList shortList70 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj71 = shortList70.clone();
        boolean boolean72 = byteList48.equals((java.lang.Object) shortList70);
        esa.mo.nmf.NMFException nMFException74 = new esa.mo.nmf.NMFException("-1");
        boolean boolean75 = byteList48.equals((java.lang.Object) nMFException74);
        byteList48.trimToSize();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList77 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element78 = booleanList77.createElement();
        java.lang.Long long79 = booleanList77.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort80 = booleanList77.getAreaNumber();
        java.lang.Integer int81 = booleanList77.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray82 = new org.ccsds.moims.mo.mal.structures.BooleanList[]{booleanList77};
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray83 = byteList48.toArray(booleanListArray82);
        boolean boolean84 = floatList40.remove((java.lang.Object) booleanListArray82);
        java.util.ArrayList<java.lang.Boolean>[] booleanListArray85 = uShortList33.toArray(
            (java.util.ArrayList<java.lang.Boolean>[]) booleanListArray82);
        java.lang.Iterable<java.lang.Boolean>[] booleanIterableArray86 = byteList3.toArray(
            (java.lang.Iterable<java.lang.Boolean>[]) booleanListArray82);
        org.junit.Assert.assertNotNull(byteArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertNotNull(intStream8);
        org.junit.Assert.assertNotNull(intStream9);
        org.junit.Assert.assertNotNull(uRIArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
        org.junit.Assert.assertNotNull(booleanArray15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertNotNull(booleanItor18);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
        org.junit.Assert.assertNotNull(booleanArray22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertNotNull(booleanStream27);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", !boolean28);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertNotNull(byteSpliterator31);
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + 0 + "'", int32 == 0);
        org.junit.Assert.assertTrue("'" + int34 + "' != '" + (-10) + "'", int34.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor35);
        org.junit.Assert.assertNotNull(objArray36);
        org.junit.Assert.assertNotNull(uShortStream38);
        org.junit.Assert.assertTrue("'" + str39 + "' != '" + "[]" + "'", str39.equals("[]"));
        org.junit.Assert.assertTrue("'" + long41 + "' != '" + 281475010265084L + "'", long41.equals(281475010265084L));
        org.junit.Assert.assertTrue("'" + str43 + "' != '" + "[]" + "'", str43.equals("[]"));
        org.junit.Assert.assertNotNull(floatSpliterator44);
        org.junit.Assert.assertNotNull(byteArray47);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + true + "'", boolean49);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + false + "'", !boolean50);
        org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-1) + "'", int52 == (-1));
        org.junit.Assert.assertTrue("'" + int54 + "' != '" + (-1) + "'", int54 == (-1));
        org.junit.Assert.assertNotNull(booleanArray56);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + true + "'", boolean58);
        org.junit.Assert.assertNotNull(booleanItor60);
        org.junit.Assert.assertNotNull(booleanStream61);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertTrue("'" + boolean64 + "' != '" + true + "'", boolean64);
        org.junit.Assert.assertNotNull(obj66);
        org.junit.Assert.assertNotNull(uShortStream67);
        org.junit.Assert.assertNotNull(uShortItor68);
        org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + false + "'", !boolean69);
        org.junit.Assert.assertNotNull(obj71);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + true + "'", boolean72);
        org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + false + "'", !boolean75);
        org.junit.Assert.assertNotNull(element78);
        org.junit.Assert.assertTrue("'" + long79 + "' != '" + 281475010265086L + "'", long79.equals(281475010265086L));
        org.junit.Assert.assertNotNull(uShort80);
        org.junit.Assert.assertTrue("'" + int81 + "' != '" + (-2) + "'", int81.equals((-2)));
        org.junit.Assert.assertNotNull(booleanListArray82);
        org.junit.Assert.assertNotNull(booleanListArray83);
        org.junit.Assert.assertTrue("'" + boolean84 + "' != '" + false + "'", !boolean84);
        org.junit.Assert.assertNotNull(booleanListArray85);
        org.junit.Assert.assertNotNull(booleanIterableArray86);
    }

    @Test
    public void test610() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test610");
        esa.mo.nmf.NMFException nMFException2 = new esa.mo.nmf.NMFException("-1");
        esa.mo.nmf.NMFException nMFException5 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray6 = nMFException5.getSuppressed();
        java.lang.Throwable[] throwableArray7 = nMFException5.getSuppressed();
        esa.mo.nmf.NMFException nMFException8 = new esa.mo.nmf.NMFException("[hi!, hi!]",
            (java.lang.Throwable) nMFException5);
        java.lang.String str9 = nMFException5.toString();
        nMFException2.addSuppressed((java.lang.Throwable) nMFException5);
        esa.mo.nmf.NMFException nMFException11 = new esa.mo.nmf.NMFException("", (java.lang.Throwable) nMFException2);
        org.junit.Assert.assertNotNull(throwableArray6);
        org.junit.Assert.assertNotNull(throwableArray7);
        org.junit.Assert.assertTrue("'" + str9 + "' != '" + "esa.mo.nmf.NMFException: 0" + "'", str9.equals(
            "esa.mo.nmf.NMFException: 0"));
    }

    @Test
    public void test611() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test611");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.Element element22 = floatList0.createElement();
        java.util.Iterator<java.lang.Float> floatItor23 = floatList0.iterator();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertNotNull(element22);
        org.junit.Assert.assertNotNull(floatItor23);
    }

    @Test
    public void test612() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test612");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        boolean boolean22 = floatList0.isEmpty();
        boolean boolean24 = floatList0.add((java.lang.Float) 100.0f);
        java.lang.Object obj25 = floatList0.clone();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertNotNull(obj25);
    }

    @Test
    public void test613() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test613");
        org.ccsds.moims.mo.mal.structures.Time time1 = new org.ccsds.moims.mo.mal.structures.Time((long) (byte) -1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = time1.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort3 = time1.getServiceNumber();
        java.lang.Integer int4 = time1.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element5 = time1.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort6 = time1.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.FloatList floatList7 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long8 = floatList7.getShortForm();
        java.lang.String[] strArray10 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList11 = new java.util.ArrayList<java.lang.String>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList11,
            strArray10);
        int int13 = strList11.size();
        java.lang.Boolean[] booleanArray16 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList17 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean18 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList17,
            booleanArray16);
        boolean boolean20 = booleanList17.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream21 = booleanList17.stream();
        boolean boolean22 = strList11.containsAll((java.util.Collection<java.lang.Boolean>) booleanList17);
        boolean boolean23 = floatList7.containsAll((java.util.Collection<java.lang.Boolean>) booleanList17);
        java.util.Spliterator<java.lang.Float> floatSpliterator24 = floatList7.spliterator();
        esa.mo.nmf.NMFException nMFException26 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray27 = nMFException26.getSuppressed();
        int int28 = floatList7.lastIndexOf((java.lang.Object) throwableArray27);
        java.lang.Integer int29 = floatList7.getTypeShortForm();
        java.util.ListIterator<java.lang.Float> floatItor30 = floatList7.listIterator();
        boolean boolean31 = time1.equals((java.lang.Object) floatList7);
        java.lang.String str32 = floatList7.toString();
        floatList7.ensureCapacity((int) '#');
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(uShort3);
        org.junit.Assert.assertTrue("'" + int4 + "' != '" + 16 + "'", int4.equals(16));
        org.junit.Assert.assertNotNull(element5);
        org.junit.Assert.assertNotNull(uShort6);
        org.junit.Assert.assertTrue("'" + long8 + "' != '" + 281475010265084L + "'", long8.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + 1 + "'", int13 == 1);
        org.junit.Assert.assertNotNull(booleanArray16);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
        org.junit.Assert.assertNotNull(booleanStream21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
        org.junit.Assert.assertNotNull(floatSpliterator24);
        org.junit.Assert.assertNotNull(throwableArray27);
        org.junit.Assert.assertTrue("'" + int28 + "' != '" + (-1) + "'", int28 == (-1));
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-4) + "'", int29.equals((-4)));
        org.junit.Assert.assertNotNull(floatItor30);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + false + "'", !boolean31);
        org.junit.Assert.assertTrue("'" + str32 + "' != '" + "[]" + "'", str32.equals("[]"));
    }

    @Test
    public void test614() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test614");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCRegistration19.aggregationService;
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl22 = mCRegistration19.alertService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider23 = mCRegistration19.comServices;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl22);
        org.junit.Assert.assertNull(cOMServicesProvider23);
    }

    @Test
    public void test615() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test615");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl1 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF0
            .getParameterService();
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl1);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
    }

    @Test
    public void test616() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test616");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        boolean boolean22 = floatList0.isEmpty();
        org.ccsds.moims.mo.mal.structures.Element element23 = floatList0.createElement();
        floatList0.trimToSize();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(element23);
    }

    @Test
    public void test617() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test617");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        java.util.Iterator<java.lang.Long> longItor24 = longList22.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort25 = longList22.getServiceNumber();
        java.lang.Object[] objArray26 = longList22.toArray();
        org.ccsds.moims.mo.mal.structures.UShort uShort27 = longList22.getServiceNumber();
        java.util.Spliterator<java.lang.Long> longSpliterator28 = longList22.spliterator();
        java.lang.Object obj29 = longList22.clone();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(longItor24);
        org.junit.Assert.assertNotNull(uShort25);
        org.junit.Assert.assertNotNull(objArray26);
        org.junit.Assert.assertNotNull(uShort27);
        org.junit.Assert.assertNotNull(longSpliterator28);
        org.junit.Assert.assertNotNull(obj29);
    }

    @Test
    public void test618() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test618");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl5 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl6 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF0
            .getParameterService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl5);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl6);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
    }

    @Test
    public void test619() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test619");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl22 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl24 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl25 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl26 = mCRegistration19.aggregationService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl24);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl25);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl26);
    }

    @Test
    public void test620() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test620");
        java.lang.Long[] longArray4 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList5 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList5, longArray4);
        java.lang.Object obj7 = null;
        boolean boolean8 = longList5.contains(obj7);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger10 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean11 = longList5.contains((java.lang.Object) (byte) 100);
        java.lang.Boolean[] booleanArray14 = new java.lang.Boolean[]{false, false};
        java.util.ArrayList<java.lang.Boolean> booleanList15 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean16 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList15,
            booleanArray14);
        boolean boolean17 = longList5.containsAll((java.util.Collection<java.lang.Boolean>) booleanList15);
        java.lang.Long long19 = longList5.get(1);
        java.util.stream.Stream<java.lang.Long> longStream20 = longList5.stream();
        org.ccsds.moims.mo.mal.structures.FloatList floatList21 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long22 = floatList21.getShortForm();
        java.lang.String[] strArray24 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList25 = new java.util.ArrayList<java.lang.String>();
        boolean boolean26 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList25,
            strArray24);
        int int27 = strList25.size();
        java.lang.Boolean[] booleanArray30 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList31 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean32 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList31,
            booleanArray30);
        boolean boolean34 = booleanList31.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream35 = booleanList31.stream();
        boolean boolean36 = strList25.containsAll((java.util.Collection<java.lang.Boolean>) booleanList31);
        boolean boolean37 = floatList21.containsAll((java.util.Collection<java.lang.Boolean>) booleanList31);
        java.util.Spliterator<java.lang.Float> floatSpliterator38 = floatList21.spliterator();
        esa.mo.nmf.NMFException nMFException40 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray41 = nMFException40.getSuppressed();
        int int42 = floatList21.lastIndexOf((java.lang.Object) throwableArray41);
        org.ccsds.moims.mo.mal.structures.LongList longList43 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int44 = floatList21.lastIndexOf((java.lang.Object) longList43);
        java.util.Iterator<java.lang.Long> longItor45 = longList43.iterator();
        boolean boolean46 = longList5.remove((java.lang.Object) longList43);
        java.util.Iterator<java.lang.Long> longItor47 = longList43.iterator();
        org.junit.Assert.assertNotNull(longArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertNotNull(booleanArray14);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
        org.junit.Assert.assertTrue("'" + long19 + "' != '" + (-1L) + "'", long19.equals((-1L)));
        org.junit.Assert.assertNotNull(longStream20);
        org.junit.Assert.assertTrue("'" + long22 + "' != '" + 281475010265084L + "'", long22.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertTrue("'" + int27 + "' != '" + 1 + "'", int27 == 1);
        org.junit.Assert.assertNotNull(booleanArray30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + true + "'", boolean34);
        org.junit.Assert.assertNotNull(booleanStream35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertNotNull(floatSpliterator38);
        org.junit.Assert.assertNotNull(throwableArray41);
        org.junit.Assert.assertTrue("'" + int42 + "' != '" + (-1) + "'", int42 == (-1));
        org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-1) + "'", int44 == (-1));
        org.junit.Assert.assertNotNull(longItor45);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + false + "'", !boolean46);
        org.junit.Assert.assertNotNull(longItor47);
    }

    @Test
    public void test621() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test621");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        java.util.ListIterator<java.lang.String> strItor25 = stringList12.listIterator();
        stringList12.ensureCapacity((int) (short) -1);
        java.lang.Integer[] intArray41 = new java.lang.Integer[]{4, 12, 1, 4, 10, 65535, 5, 0, 5, 0, 14, 13, (-1)};
        java.util.ArrayList<java.lang.Integer> intList42 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList42,
            intArray41);
        org.ccsds.moims.mo.mal.structures.Identifier identifier44 = new org.ccsds.moims.mo.mal.structures.Identifier();
        boolean boolean45 = intList42.contains((java.lang.Object) identifier44);
        boolean boolean47 = intList42.remove((java.lang.Object) 281475010265075L);
        intList42.ensureCapacity(6);
        int int50 = intList42.size();
        java.lang.Double[] doubleArray54 = new java.lang.Double[]{1.0d, 0.0d, 10.0d};
        java.util.ArrayList<java.lang.Double> doubleList55 = new java.util.ArrayList<java.lang.Double>();
        boolean boolean56 = java.util.Collections.addAll((java.util.Collection<java.lang.Double>) doubleList55,
            doubleArray54);
        java.lang.Boolean[] booleanArray59 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList60 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean61 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList60,
            booleanArray59);
        boolean boolean63 = booleanList60.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream64 = booleanList60.stream();
        int int65 = doubleList55.lastIndexOf((java.lang.Object) booleanStream64);
        int int67 = doubleList55.lastIndexOf((java.lang.Object) 10.0d);
        java.lang.String str68 = doubleList55.toString();
        boolean boolean69 = intList42.remove((java.lang.Object) str68);
        org.ccsds.moims.mo.mal.structures.ShortList shortList70 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj71 = shortList70.clone();
        java.util.stream.Stream<java.lang.Short> shortStream72 = shortList70.parallelStream();
        java.lang.String str73 = shortList70.toString();
        java.util.stream.Stream<java.lang.Short> shortStream74 = shortList70.parallelStream();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList76 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        org.ccsds.moims.mo.mal.structures.UShort uShort77 = integerList76.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.URIList uRIList79 = new org.ccsds.moims.mo.mal.structures.URIList(17);
        org.ccsds.moims.mo.mal.structures.UShort uShort80 = uRIList79.getAreaNumber();
        java.lang.Integer int81 = uRIList79.getTypeShortForm();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.URI> uRISpliterator82 = uRIList79.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort83 = uRIList79.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.Element element84 = uRIList79.createElement();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList85 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element86 = booleanList85.createElement();
        boolean boolean87 = uRIList79.retainAll((java.util.Collection<java.lang.Boolean>) booleanList85);
        boolean boolean88 = integerList76.containsAll((java.util.Collection<java.lang.Boolean>) booleanList85);
        boolean boolean89 = shortList70.removeAll((java.util.Collection<java.lang.Boolean>) booleanList85);
        java.lang.Integer int90 = booleanList85.getTypeShortForm();
        java.lang.String str91 = booleanList85.toString();
        boolean boolean92 = intList42.containsAll((java.util.Collection<java.lang.Boolean>) booleanList85);
        java.util.Iterator<java.lang.Integer> intItor93 = intList42.iterator();
        int int94 = stringList12.indexOf((java.lang.Object) intItor93);
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(strItor25);
        org.junit.Assert.assertNotNull(intArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + false + "'", !boolean45);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertTrue("'" + int50 + "' != '" + 13 + "'", int50 == 13);
        org.junit.Assert.assertNotNull(doubleArray54);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
        org.junit.Assert.assertNotNull(booleanArray59);
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + true + "'", boolean61);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertNotNull(booleanStream64);
        org.junit.Assert.assertTrue("'" + int65 + "' != '" + (-1) + "'", int65 == (-1));
        org.junit.Assert.assertTrue("'" + int67 + "' != '" + 2 + "'", int67 == 2);
        org.junit.Assert.assertTrue("'" + str68 + "' != '" + "[1.0, 0.0, 10.0]" + "'", str68.equals(
            "[1.0, 0.0, 10.0]"));
        org.junit.Assert.assertTrue("'" + boolean69 + "' != '" + false + "'", !boolean69);
        org.junit.Assert.assertNotNull(obj71);
        org.junit.Assert.assertNotNull(shortStream72);
        org.junit.Assert.assertTrue("'" + str73 + "' != '" + "[]" + "'", str73.equals("[]"));
        org.junit.Assert.assertNotNull(shortStream74);
        org.junit.Assert.assertNotNull(uShort77);
        org.junit.Assert.assertNotNull(uShort80);
        org.junit.Assert.assertTrue("'" + int81 + "' != '" + (-18) + "'", int81.equals((-18)));
        org.junit.Assert.assertNotNull(uRISpliterator82);
        org.junit.Assert.assertNotNull(uShort83);
        org.junit.Assert.assertNotNull(element84);
        org.junit.Assert.assertNotNull(element86);
        org.junit.Assert.assertTrue("'" + boolean87 + "' != '" + false + "'", !boolean87);
        org.junit.Assert.assertTrue("'" + boolean88 + "' != '" + true + "'", boolean88);
        org.junit.Assert.assertTrue("'" + boolean89 + "' != '" + false + "'", !boolean89);
        org.junit.Assert.assertTrue("'" + int90 + "' != '" + (-2) + "'", int90.equals((-2)));
        org.junit.Assert.assertTrue("'" + str91 + "' != '" + "[]" + "'", str91.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean92 + "' != '" + true + "'", boolean92);
        org.junit.Assert.assertNotNull(intItor93);
        org.junit.Assert.assertTrue("'" + int94 + "' != '" + (-1) + "'", int94 == (-1));
    }

    @Test
    public void test622() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test622");
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.Element element1 = doubleList0.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = doubleList0.getServiceNumber();
        java.lang.Integer int3 = doubleList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = doubleList0.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.Union union6 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Float) 100.0f);
        int int7 = doubleList0.indexOf((java.lang.Object) 100.0f);
        org.ccsds.moims.mo.mal.structures.ShortList shortList8 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray10 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList11 = new java.util.ArrayList<java.lang.String>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList11,
            strArray10);
        int int13 = strList11.size();
        java.lang.Boolean[] booleanArray16 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList17 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean18 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList17,
            booleanArray16);
        boolean boolean20 = booleanList17.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream21 = booleanList17.stream();
        boolean boolean22 = strList11.containsAll((java.util.Collection<java.lang.Boolean>) booleanList17);
        boolean boolean23 = shortList8.containsAll((java.util.Collection<java.lang.Boolean>) booleanList17);
        java.util.stream.Stream<java.lang.Short> shortStream24 = shortList8.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element25 = shortList8.createElement();
        esa.mo.nmf.NMFException nMFException27 = new esa.mo.nmf.NMFException("false");
        java.lang.Throwable[] throwableArray28 = nMFException27.getSuppressed();
        int int29 = shortList8.indexOf((java.lang.Object) throwableArray28);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList30 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.String str31 = doubleList30.toString();
        java.lang.Boolean[] booleanArray33 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList34 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList34,
            booleanArray33);
        java.util.ListIterator<java.lang.Boolean> booleanItor37 = booleanList34.listIterator((int) (short) 1);
        boolean boolean38 = doubleList30.containsAll((java.util.Collection<java.lang.Boolean>) booleanList34);
        java.lang.Byte[] byteArray41 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList42 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList42,
            byteArray41);
        boolean boolean44 = byteList42.isEmpty();
        int int45 = byteList42.size();
        int int46 = booleanList34.indexOf((java.lang.Object) byteList42);
        java.lang.Boolean[] booleanArray49 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList50 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean51 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList50,
            booleanArray49);
        java.util.Iterator<java.lang.Boolean> booleanItor52 = booleanList50.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream53 = booleanList50.stream();
        java.util.Iterator<java.lang.Boolean> booleanItor54 = booleanList50.iterator();
        boolean boolean55 = byteList42.removeAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        boolean boolean56 = shortList8.removeAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        boolean boolean57 = booleanList50.isEmpty();
        boolean boolean58 = doubleList0.remove((java.lang.Object) booleanList50);
        java.util.ListIterator<java.lang.Double> doubleItor59 = doubleList0.listIterator();
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-5) + "'", int3.equals((-5)));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-1) + "'", int7 == (-1));
        org.junit.Assert.assertNotNull(strArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + 1 + "'", int13 == 1);
        org.junit.Assert.assertNotNull(booleanArray16);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
        org.junit.Assert.assertNotNull(booleanStream21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
        org.junit.Assert.assertNotNull(shortStream24);
        org.junit.Assert.assertNotNull(element25);
        org.junit.Assert.assertNotNull(throwableArray28);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-1) + "'", int29 == (-1));
        org.junit.Assert.assertTrue("'" + str31 + "' != '" + "[]" + "'", str31.equals("[]"));
        org.junit.Assert.assertNotNull(booleanArray33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertNotNull(booleanItor37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertNotNull(byteArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
        org.junit.Assert.assertTrue("'" + int45 + "' != '" + 2 + "'", int45 == 2);
        org.junit.Assert.assertTrue("'" + int46 + "' != '" + (-1) + "'", int46 == (-1));
        org.junit.Assert.assertNotNull(booleanArray49);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
        org.junit.Assert.assertNotNull(booleanItor52);
        org.junit.Assert.assertNotNull(booleanStream53);
        org.junit.Assert.assertNotNull(booleanItor54);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + false + "'", !boolean55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + false + "'", !boolean57);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
        org.junit.Assert.assertNotNull(doubleItor59);
    }

    @Test
    public void test623() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test623");
        java.lang.Long[] longArray8 = new java.lang.Long[]{4294967295L, 281475010265084L, 100L, 281474993487886L, 0L,
                                                           281474993487876L, 100L, 281474993487890L};
        java.util.ArrayList<java.lang.Long> longList9 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList9, longArray8);
        java.util.ListIterator<java.lang.Long> longItor11 = longList9.listIterator();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList();
        org.ccsds.moims.mo.mal.structures.Element element13 = stringList12.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort14 = stringList12.getServiceNumber();
        boolean boolean15 = longList9.equals((java.lang.Object) stringList12);
        boolean boolean17 = longList9.add((java.lang.Long) 281474993487874L);
        longList9.ensureCapacity((-4));
        org.ccsds.moims.mo.mal.structures.UShortList uShortList20 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int21 = uShortList20.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor22 = uShortList20.iterator();
        java.lang.Object[] objArray23 = uShortList20.toArray();
        boolean boolean25 = uShortList20.equals((java.lang.Object) 1);
        esa.mo.nmf.NMFException nMFException28 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray29 = nMFException28.getSuppressed();
        java.lang.Throwable[] throwableArray30 = nMFException28.getSuppressed();
        esa.mo.nmf.NMFException nMFException32 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray33 = nMFException32.getSuppressed();
        nMFException28.addSuppressed((java.lang.Throwable) nMFException32);
        java.lang.Throwable[] throwableArray35 = nMFException32.getSuppressed();
        java.lang.Throwable[] throwableArray36 = nMFException32.getSuppressed();
        esa.mo.nmf.NMFException nMFException37 = new esa.mo.nmf.NMFException("[]",
            (java.lang.Throwable) nMFException32);
        int int38 = uShortList20.indexOf((java.lang.Object) nMFException37);
        org.ccsds.moims.mo.mal.structures.Element element39 = uShortList20.createElement();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet40 = uShortList20.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet41 = uOctet40.getAreaVersion();
        boolean boolean42 = longList9.equals((java.lang.Object) uOctet41);
        org.junit.Assert.assertNotNull(longArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertNotNull(longItor11);
        org.junit.Assert.assertNotNull(element13);
        org.junit.Assert.assertNotNull(uShort14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-10) + "'", int21.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor22);
        org.junit.Assert.assertNotNull(objArray23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
        org.junit.Assert.assertNotNull(throwableArray29);
        org.junit.Assert.assertNotNull(throwableArray30);
        org.junit.Assert.assertNotNull(throwableArray33);
        org.junit.Assert.assertNotNull(throwableArray35);
        org.junit.Assert.assertNotNull(throwableArray36);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertNotNull(element39);
        org.junit.Assert.assertNotNull(uOctet40);
        org.junit.Assert.assertNotNull(uOctet41);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + false + "'", !boolean42);
    }

    @Test
    public void test624() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test624");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream2 = uShortList0.parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor3 = uShortList0.iterator();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor4 = uShortList0.listIterator();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider5 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl13 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl14 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl15 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF16 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl17 = mCServicesProviderNMF16
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl18 = mCServicesProviderNMF16
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF19 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCServicesProviderNMF19
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCServicesProviderNMF19
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl22 = mCServicesProviderNMF19
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCServicesProviderNMF19
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration24 = new esa.mo.nmf.MCRegistration(cOMServicesProvider5,
            parameterProviderServiceImpl10, aggregationProviderServiceImpl15, alertProviderServiceImpl18,
            actionProviderServiceImpl23);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl25 = mCRegistration24.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl26 = mCRegistration24.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider27 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF28 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl29 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl30 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF33 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl34 = mCServicesProviderNMF33
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl35 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl36 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl37 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF38 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl40 = mCServicesProviderNMF38
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF41 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF41
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl43 = mCServicesProviderNMF41
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCServicesProviderNMF41
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl45 = mCServicesProviderNMF41
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration46 = new esa.mo.nmf.MCRegistration(cOMServicesProvider27,
            parameterProviderServiceImpl32, aggregationProviderServiceImpl37, alertProviderServiceImpl40,
            actionProviderServiceImpl45);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF47 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl48 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl49 = mCServicesProviderNMF47
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl50 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl51 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl52 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl53 = mCServicesProviderNMF47
            .getActionService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF54 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl55 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl56 = mCServicesProviderNMF54
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl57 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl58 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl59 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl60 = mCServicesProviderNMF54
            .getActionService();
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray61 = new org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[]{actionProviderServiceImpl26,
                                                                                                                                                                                   actionProviderServiceImpl45,
                                                                                                                                                                                   actionProviderServiceImpl53,
                                                                                                                                                                                   actionProviderServiceImpl60};
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray62 = uShortList0
            .toArray(actionInheritanceSkeletonArray61);
        uShortList0.ensureCapacity(18);
        org.ccsds.moims.mo.mal.structures.URIList uRIList66 = new org.ccsds.moims.mo.mal.structures.URIList(17);
        org.ccsds.moims.mo.mal.structures.UShort uShort67 = uRIList66.getAreaNumber();
        java.lang.Long long68 = uRIList66.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element69 = uRIList66.createElement();
        boolean boolean70 = uShortList0.remove((java.lang.Object) uRIList66);
        int int71 = uShortList0.size();
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(uShortStream2);
        org.junit.Assert.assertNotNull(uShortItor3);
        org.junit.Assert.assertNotNull(uShortItor4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl13);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl14);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl15);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl17);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl25);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl26);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl29);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl34);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl35);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl36);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl37);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl40);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl45);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl48);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl49);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl50);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl51);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl52);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl53);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl55);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl56);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl57);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl58);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl59);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl60);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray61);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray62);
        org.junit.Assert.assertNotNull(uShort67);
        org.junit.Assert.assertTrue("'" + long68 + "' != '" + 281475010265070L + "'", long68.equals(281475010265070L));
        org.junit.Assert.assertNotNull(element69);
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + false + "'", !boolean70);
        org.junit.Assert.assertTrue("'" + int71 + "' != '" + 0 + "'", int71 == 0);
    }

    @Test
    public void test625() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test625");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj1 = shortList0.clone();
        java.lang.Long long2 = shortList0.getShortForm();
        java.lang.Object[] objArray3 = shortList0.toArray();
        java.lang.Byte[] byteArray8 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList9 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean10 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList9, byteArray8);
        boolean boolean12 = byteList9.add((java.lang.Byte) (byte) 10);
        java.lang.String str13 = byteList9.toString();
        int int14 = shortList0.lastIndexOf((java.lang.Object) byteList9);
        boolean boolean16 = byteList9.add((java.lang.Byte) (byte) 0);
        org.ccsds.moims.mo.mal.structures.FloatList floatList17 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long18 = floatList17.getShortForm();
        java.lang.String[] strArray20 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList21 = new java.util.ArrayList<java.lang.String>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList21,
            strArray20);
        int int23 = strList21.size();
        java.lang.Boolean[] booleanArray26 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList27 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean28 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList27,
            booleanArray26);
        boolean boolean30 = booleanList27.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream31 = booleanList27.stream();
        boolean boolean32 = strList21.containsAll((java.util.Collection<java.lang.Boolean>) booleanList27);
        boolean boolean33 = floatList17.containsAll((java.util.Collection<java.lang.Boolean>) booleanList27);
        java.util.Spliterator<java.lang.Float> floatSpliterator34 = floatList17.spliterator();
        esa.mo.nmf.NMFException nMFException36 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray37 = nMFException36.getSuppressed();
        int int38 = floatList17.lastIndexOf((java.lang.Object) throwableArray37);
        org.ccsds.moims.mo.mal.structures.LongList longList39 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int40 = floatList17.lastIndexOf((java.lang.Object) longList39);
        longList39.trimToSize();
        java.lang.Object[] objArray42 = longList39.toArray();
        org.ccsds.moims.mo.mal.structures.Element element43 = longList39.createElement();
        boolean boolean44 = byteList9.equals((java.lang.Object) element43);
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertTrue("'" + long2 + "' != '" + 281475010265079L + "'", long2.equals(281475010265079L));
        org.junit.Assert.assertNotNull(objArray3);
        org.junit.Assert.assertNotNull(byteArray8);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + true + "'", boolean10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertTrue("'" + str13 + "' != '" + "[-1, -1, 1, 1, 10]" + "'", str13.equals(
            "[-1, -1, 1, 1, 10]"));
        org.junit.Assert.assertTrue("'" + int14 + "' != '" + (-1) + "'", int14 == (-1));
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + true + "'", boolean16);
        org.junit.Assert.assertTrue("'" + long18 + "' != '" + 281475010265084L + "'", long18.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + 1 + "'", int23 == 1);
        org.junit.Assert.assertNotNull(booleanArray26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertNotNull(booleanStream31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + false + "'", !boolean32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertNotNull(floatSpliterator34);
        org.junit.Assert.assertNotNull(throwableArray37);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + (-1) + "'", int38 == (-1));
        org.junit.Assert.assertTrue("'" + int40 + "' != '" + (-1) + "'", int40 == (-1));
        org.junit.Assert.assertNotNull(objArray42);
        org.junit.Assert.assertNotNull(element43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
    }

    @Test
    public void test626() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test626");
        org.ccsds.moims.mo.mal.structures.IntegerList integerList1 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        int int2 = integerList1.size();
        java.lang.Integer int3 = integerList1.getTypeShortForm();
        java.util.stream.Stream<java.lang.Integer> intStream4 = integerList1.stream();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray5 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList6 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean7 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList6, uRIArray5);
        java.lang.Boolean[] booleanArray10 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList11 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList11,
            booleanArray10);
        java.util.Iterator<java.lang.Boolean> booleanItor13 = booleanList11.iterator();
        boolean boolean14 = uRIList6.retainAll((java.util.Collection<java.lang.Boolean>) booleanList11);
        booleanList11.clear();
        java.lang.Byte[] byteArray20 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList21 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList21,
            byteArray20);
        boolean boolean24 = byteList21.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj25 = byteList21.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream26 = byteList21.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList28 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int29 = byteList21.indexOf((java.lang.Object) stringList28);
        byte[] byteArray33 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob34 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray33);
        int int35 = blob34.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob36 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean37 = blob34.equals((java.lang.Object) blob36);
        boolean boolean38 = stringList28.remove((java.lang.Object) blob36);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF39 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean40 = stringList28.equals((java.lang.Object) mCServicesProviderNMF39);
        java.util.stream.Stream<java.lang.String> strStream41 = stringList28.parallelStream();
        java.lang.Long[] longArray46 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList47 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean48 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList47,
            longArray46);
        java.lang.Object obj49 = null;
        boolean boolean50 = longList47.contains(obj49);
        org.ccsds.moims.mo.mal.structures.UInteger uInteger52 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        boolean boolean53 = longList47.contains((java.lang.Object) (byte) 100);
        java.util.Iterator<java.lang.Long> longItor54 = longList47.iterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort55 = org.ccsds.moims.mo.mal.structures.OctetList.AREA_SHORT_FORM;
        boolean boolean56 = longList47.remove((java.lang.Object) uShort55);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList57 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int58 = uShortList57.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor59 = uShortList57.iterator();
        java.lang.Object[] objArray60 = uShortList57.toArray();
        uShortList57.clear();
        uShortList57.clear();
        boolean boolean63 = uShortList57.isEmpty();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor64 = uShortList57.iterator();
        boolean boolean65 = longList47.contains((java.lang.Object) uShortList57);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList66 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.UShort uShort67 = booleanList66.getAreaNumber();
        boolean boolean68 = longList47.retainAll((java.util.Collection<java.lang.Boolean>) booleanList66);
        java.lang.Integer int69 = booleanList66.getTypeShortForm();
        boolean boolean71 = booleanList66.add((java.lang.Boolean) true);
        boolean boolean72 = stringList28.retainAll((java.util.Collection<java.lang.Boolean>) booleanList66);
        org.ccsds.moims.mo.mal.structures.UShort uShort73 = booleanList66.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet74 = booleanList66.getAreaVersion();
        boolean boolean75 = booleanList11.containsAll((java.util.Collection<java.lang.Boolean>) booleanList66);
        int int76 = integerList1.indexOf((java.lang.Object) booleanList11);
        org.junit.Assert.assertTrue("'" + int2 + "' != '" + 0 + "'", int2 == 0);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-11) + "'", int3.equals((-11)));
        org.junit.Assert.assertNotNull(intStream4);
        org.junit.Assert.assertNotNull(uRIArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + false + "'", !boolean7);
        org.junit.Assert.assertNotNull(booleanArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertNotNull(booleanItor13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertNotNull(byteArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertNotNull(obj25);
        org.junit.Assert.assertNotNull(byteStream26);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-1) + "'", int29 == (-1));
        org.junit.Assert.assertNotNull(byteArray33);
        org.junit.Assert.assertTrue("'" + int35 + "' != '" + 3 + "'", int35 == 3);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertNotNull(strStream41);
        org.junit.Assert.assertNotNull(longArray46);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + true + "'", boolean48);
        org.junit.Assert.assertTrue("'" + boolean50 + "' != '" + false + "'", !boolean50);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
        org.junit.Assert.assertNotNull(longItor54);
        org.junit.Assert.assertNotNull(uShort55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertTrue("'" + int58 + "' != '" + (-10) + "'", int58.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor59);
        org.junit.Assert.assertNotNull(objArray60);
        org.junit.Assert.assertTrue("'" + boolean63 + "' != '" + true + "'", boolean63);
        org.junit.Assert.assertNotNull(uShortItor64);
        org.junit.Assert.assertTrue("'" + boolean65 + "' != '" + false + "'", !boolean65);
        org.junit.Assert.assertNotNull(uShort67);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + true + "'", boolean68);
        org.junit.Assert.assertTrue("'" + int69 + "' != '" + (-2) + "'", int69.equals((-2)));
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
        org.junit.Assert.assertTrue("'" + boolean72 + "' != '" + false + "'", !boolean72);
        org.junit.Assert.assertNotNull(uShort73);
        org.junit.Assert.assertNotNull(uOctet74);
        org.junit.Assert.assertTrue("'" + boolean75 + "' != '" + false + "'", !boolean75);
        org.junit.Assert.assertTrue("'" + int76 + "' != '" + (-1) + "'", int76 == (-1));
    }

    @Test
    public void test627() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test627");
        org.ccsds.moims.mo.mal.structures.Union union1 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Integer) 15);
        esa.mo.nmf.NMFException nMFException3 = new esa.mo.nmf.NMFException("-1");
        esa.mo.nmf.NMFException nMFException6 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray7 = nMFException6.getSuppressed();
        java.lang.Throwable[] throwableArray8 = nMFException6.getSuppressed();
        esa.mo.nmf.NMFException nMFException9 = new esa.mo.nmf.NMFException("[hi!, hi!]",
            (java.lang.Throwable) nMFException6);
        java.lang.String str10 = nMFException6.toString();
        nMFException3.addSuppressed((java.lang.Throwable) nMFException6);
        esa.mo.nmf.NMFException nMFException13 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray14 = nMFException13.getSuppressed();
        java.lang.Throwable[] throwableArray15 = nMFException13.getSuppressed();
        nMFException3.addSuppressed((java.lang.Throwable) nMFException13);
        boolean boolean17 = union1.equals((java.lang.Object) nMFException13);
        org.junit.Assert.assertNotNull(throwableArray7);
        org.junit.Assert.assertNotNull(throwableArray8);
        org.junit.Assert.assertTrue("'" + str10 + "' != '" + "esa.mo.nmf.NMFException: 0" + "'", str10.equals(
            "esa.mo.nmf.NMFException: 0"));
        org.junit.Assert.assertNotNull(throwableArray14);
        org.junit.Assert.assertNotNull(throwableArray15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
    }

    @Test
    public void test628() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test628");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        org.ccsds.moims.mo.mal.structures.UShort uShort25 = stringList12.getServiceNumber();
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(uShort25);
    }

    @Test
    public void test629() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test629");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        java.lang.Integer int22 = floatList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.Element element23 = floatList0.createElement();
        org.ccsds.moims.mo.mal.structures.Identifier identifier24 = new org.ccsds.moims.mo.mal.structures.Identifier();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet25 = identifier24.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray26 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList27 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean28 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList27, uRIArray26);
        java.lang.Boolean[] booleanArray31 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList32 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean33 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList32,
            booleanArray31);
        java.util.Iterator<java.lang.Boolean> booleanItor34 = booleanList32.iterator();
        boolean boolean35 = uRIList27.retainAll((java.util.Collection<java.lang.Boolean>) booleanList32);
        java.lang.Boolean[] booleanArray38 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList39 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean40 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList39,
            booleanArray38);
        boolean boolean42 = booleanList39.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream43 = booleanList39.stream();
        boolean boolean44 = uRIList27.retainAll((java.util.Collection<java.lang.Boolean>) booleanList39);
        uRIList27.ensureCapacity(5);
        boolean boolean47 = identifier24.equals((java.lang.Object) uRIList27);
        int int48 = uRIList27.size();
        java.lang.Boolean[] booleanArray51 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList52 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean53 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList52,
            booleanArray51);
        boolean boolean55 = booleanList52.add((java.lang.Boolean) true);
        boolean boolean56 = uRIList27.equals((java.lang.Object) booleanList52);
        boolean boolean57 = floatList0.removeAll((java.util.Collection<java.lang.Boolean>) booleanList52);
        java.lang.Object obj58 = floatList0.clone();
        org.ccsds.moims.mo.mal.structures.ShortList shortList59 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj60 = shortList59.clone();
        java.util.stream.Stream<java.lang.Short> shortStream61 = shortList59.parallelStream();
        java.lang.String str62 = shortList59.toString();
        java.util.stream.Stream<java.lang.Short> shortStream63 = shortList59.parallelStream();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList65 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        org.ccsds.moims.mo.mal.structures.UShort uShort66 = integerList65.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.URIList uRIList68 = new org.ccsds.moims.mo.mal.structures.URIList(17);
        org.ccsds.moims.mo.mal.structures.UShort uShort69 = uRIList68.getAreaNumber();
        java.lang.Integer int70 = uRIList68.getTypeShortForm();
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.URI> uRISpliterator71 = uRIList68.spliterator();
        org.ccsds.moims.mo.mal.structures.UShort uShort72 = uRIList68.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.Element element73 = uRIList68.createElement();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList74 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element75 = booleanList74.createElement();
        boolean boolean76 = uRIList68.retainAll((java.util.Collection<java.lang.Boolean>) booleanList74);
        boolean boolean77 = integerList65.containsAll((java.util.Collection<java.lang.Boolean>) booleanList74);
        boolean boolean78 = shortList59.removeAll((java.util.Collection<java.lang.Boolean>) booleanList74);
        java.lang.Long long79 = booleanList74.getShortForm();
        boolean boolean80 = floatList0.retainAll((java.util.Collection<java.lang.Boolean>) booleanList74);
        try {
            floatList0.add(17, (java.lang.Float) 0.0f);
            org.junit.Assert.fail(
                "Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 17, Size: 0");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int22 + "' != '" + (-4) + "'", int22.equals((-4)));
        org.junit.Assert.assertNotNull(element23);
        org.junit.Assert.assertNotNull(uOctet25);
        org.junit.Assert.assertNotNull(uRIArray26);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", !boolean28);
        org.junit.Assert.assertNotNull(booleanArray31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertNotNull(booleanItor34);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
        org.junit.Assert.assertNotNull(booleanArray38);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + true + "'", boolean40);
        org.junit.Assert.assertTrue("'" + boolean42 + "' != '" + true + "'", boolean42);
        org.junit.Assert.assertNotNull(booleanStream43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertTrue("'" + int48 + "' != '" + 0 + "'", int48 == 0);
        org.junit.Assert.assertNotNull(booleanArray51);
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + true + "'", boolean53);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + true + "'", boolean55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + false + "'", !boolean57);
        org.junit.Assert.assertNotNull(obj58);
        org.junit.Assert.assertNotNull(obj60);
        org.junit.Assert.assertNotNull(shortStream61);
        org.junit.Assert.assertTrue("'" + str62 + "' != '" + "[]" + "'", str62.equals("[]"));
        org.junit.Assert.assertNotNull(shortStream63);
        org.junit.Assert.assertNotNull(uShort66);
        org.junit.Assert.assertNotNull(uShort69);
        org.junit.Assert.assertTrue("'" + int70 + "' != '" + (-18) + "'", int70.equals((-18)));
        org.junit.Assert.assertNotNull(uRISpliterator71);
        org.junit.Assert.assertNotNull(uShort72);
        org.junit.Assert.assertNotNull(element73);
        org.junit.Assert.assertNotNull(element75);
        org.junit.Assert.assertTrue("'" + boolean76 + "' != '" + false + "'", !boolean76);
        org.junit.Assert.assertTrue("'" + boolean77 + "' != '" + true + "'", boolean77);
        org.junit.Assert.assertTrue("'" + boolean78 + "' != '" + false + "'", !boolean78);
        org.junit.Assert.assertTrue("'" + long79 + "' != '" + 281475010265086L + "'", long79.equals(281475010265086L));
        org.junit.Assert.assertTrue("'" + boolean80 + "' != '" + false + "'", !boolean80);
    }

    @Test
    public void test630() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test630");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider22 = mCRegistration19.comServices;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider23 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF24 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl25 = mCServicesProviderNMF24
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl26 = mCServicesProviderNMF24
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl27 = mCServicesProviderNMF24
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl28 = mCServicesProviderNMF24
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF29 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl30 = mCServicesProviderNMF29
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl32 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl33 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF34 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl35 = mCServicesProviderNMF34
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl36 = mCServicesProviderNMF34
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF37 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl38 = mCServicesProviderNMF37
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl39 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl40 = mCServicesProviderNMF37
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl41 = mCServicesProviderNMF37
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration42 = new esa.mo.nmf.MCRegistration(cOMServicesProvider23,
            parameterProviderServiceImpl28, aggregationProviderServiceImpl33, alertProviderServiceImpl36,
            actionProviderServiceImpl41);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl43 = mCRegistration42.parameterService;
        esa.mo.nmf.MCRegistration.RegistrationMode registrationMode44 = esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS;
        mCRegistration42.setMode(registrationMode44);
        mCRegistration19.setMode(registrationMode44);
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider47 = mCRegistration19.comServices;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl48 = mCRegistration19.aggregationService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNull(cOMServicesProvider22);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl25);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl26);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl27);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl28);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl32);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl33);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl35);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl36);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl38);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl39);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl40);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl41);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl43);
        org.junit.Assert.assertTrue("'" + registrationMode44 + "' != '" +
            esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS + "'", registrationMode44.equals(
                esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS));
        org.junit.Assert.assertNull(cOMServicesProvider47);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl48);
    }

    @Test
    public void test631() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test631");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        java.util.ListIterator<java.lang.String> strItor25 = stringList12.listIterator();
        stringList12.ensureCapacity((int) (short) -1);
        org.ccsds.moims.mo.mal.structures.UShort uShort28 = stringList12.getServiceNumber();
        stringList12.trimToSize();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet30 = stringList12.getAreaVersion();
        try {
            java.util.ListIterator<java.lang.String> strItor32 = stringList12.listIterator((int) (short) 10);
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: Index: 10");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(strItor25);
        org.junit.Assert.assertNotNull(uShort28);
        org.junit.Assert.assertNotNull(uOctet30);
    }

    @Test
    public void test632() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test632");
        org.ccsds.moims.mo.mal.structures.ShortList shortList0 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj1 = shortList0.clone();
        java.util.stream.Stream<java.lang.Short> shortStream2 = shortList0.parallelStream();
        shortList0.trimToSize();
        shortList0.ensureCapacity((int) ' ');
        java.lang.Integer[] intArray22 = new java.lang.Integer[]{18, 0, 18, 65535, (-1), 3, (-1), (-1), 13, 0, 3, (-5),
                                                                 12, 0, 100, 14};
        java.util.ArrayList<java.lang.Integer> intList23 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean24 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList23,
            intArray22);
        java.lang.Boolean[] booleanArray27 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList28 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean29 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList28,
            booleanArray27);
        java.util.Iterator<java.lang.Boolean> booleanItor30 = booleanList28.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream31 = booleanList28.stream();
        boolean boolean32 = intList23.retainAll((java.util.Collection<java.lang.Boolean>) booleanList28);
        boolean boolean33 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList28);
        java.lang.Boolean[] booleanArray35 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList36 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean37 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList36,
            booleanArray35);
        java.util.ListIterator<java.lang.Boolean> booleanItor39 = booleanList36.listIterator((int) (short) 1);
        boolean boolean40 = shortList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList36);
        org.ccsds.moims.mo.mal.structures.FloatList floatList41 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long42 = floatList41.getShortForm();
        java.lang.String[] strArray44 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList45 = new java.util.ArrayList<java.lang.String>();
        boolean boolean46 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList45,
            strArray44);
        int int47 = strList45.size();
        java.lang.Boolean[] booleanArray50 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList51 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean52 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList51,
            booleanArray50);
        boolean boolean54 = booleanList51.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream55 = booleanList51.stream();
        boolean boolean56 = strList45.containsAll((java.util.Collection<java.lang.Boolean>) booleanList51);
        boolean boolean57 = floatList41.containsAll((java.util.Collection<java.lang.Boolean>) booleanList51);
        java.util.Spliterator<java.lang.Float> floatSpliterator58 = floatList41.spliterator();
        esa.mo.nmf.NMFException nMFException60 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray61 = nMFException60.getSuppressed();
        int int62 = floatList41.lastIndexOf((java.lang.Object) throwableArray61);
        org.ccsds.moims.mo.mal.structures.LongList longList63 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int64 = floatList41.lastIndexOf((java.lang.Object) longList63);
        org.ccsds.moims.mo.mal.structures.Time time65 = new org.ccsds.moims.mo.mal.structures.Time();
        org.ccsds.moims.mo.mal.structures.Element element66 = time65.createElement();
        boolean boolean67 = longList63.equals((java.lang.Object) time65);
        boolean boolean68 = shortList0.remove((java.lang.Object) time65);
        java.lang.Integer int69 = time65.getTypeShortForm();
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(shortStream2);
        org.junit.Assert.assertNotNull(intArray22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertNotNull(booleanArray27);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + true + "'", boolean29);
        org.junit.Assert.assertNotNull(booleanItor30);
        org.junit.Assert.assertNotNull(booleanStream31);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertNotNull(booleanArray35);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
        org.junit.Assert.assertNotNull(booleanItor39);
        org.junit.Assert.assertTrue("'" + boolean40 + "' != '" + false + "'", !boolean40);
        org.junit.Assert.assertTrue("'" + long42 + "' != '" + 281475010265084L + "'", long42.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray44);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + true + "'", boolean46);
        org.junit.Assert.assertTrue("'" + int47 + "' != '" + 1 + "'", int47 == 1);
        org.junit.Assert.assertNotNull(booleanArray50);
        org.junit.Assert.assertTrue("'" + boolean52 + "' != '" + true + "'", boolean52);
        org.junit.Assert.assertTrue("'" + boolean54 + "' != '" + true + "'", boolean54);
        org.junit.Assert.assertNotNull(booleanStream55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + false + "'", !boolean57);
        org.junit.Assert.assertNotNull(floatSpliterator58);
        org.junit.Assert.assertNotNull(throwableArray61);
        org.junit.Assert.assertTrue("'" + int62 + "' != '" + (-1) + "'", int62 == (-1));
        org.junit.Assert.assertTrue("'" + int64 + "' != '" + (-1) + "'", int64 == (-1));
        org.junit.Assert.assertNotNull(element66);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + false + "'", !boolean67);
        org.junit.Assert.assertTrue("'" + boolean68 + "' != '" + false + "'", !boolean68);
        org.junit.Assert.assertTrue("'" + int69 + "' != '" + 16 + "'", int69.equals(16));
    }

    @Test
    public void test633() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test633");
        org.ccsds.moims.mo.mal.structures.IntegerList integerList1 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.lang.String str2 = integerList1.toString();
        boolean boolean4 = integerList1.add((java.lang.Integer) 8);
        boolean boolean5 = integerList1.isEmpty();
        java.lang.String str6 = integerList1.toString();
        java.util.Iterator<java.lang.Integer> intItor7 = integerList1.iterator();
        integerList1.clear();
        java.lang.Long long9 = integerList1.getShortForm();
        integerList1.ensureCapacity((-10));
        org.ccsds.moims.mo.mal.structures.UInteger uInteger13 = new org.ccsds.moims.mo.mal.structures.UInteger(
            (long) (byte) 100);
        java.lang.String str14 = uInteger13.toString();
        java.lang.Long long15 = uInteger13.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort16 = uInteger13.getServiceNumber();
        int int17 = integerList1.lastIndexOf((java.lang.Object) uInteger13);
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList18 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        java.lang.Long long19 = booleanList18.getShortForm();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList20 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element21 = booleanList20.createElement();
        java.lang.Long long22 = booleanList20.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element23 = booleanList20.createElement();
        esa.mo.nmf.NMFException nMFException25 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray26 = nMFException25.getSuppressed();
        java.lang.Throwable[] throwableArray27 = nMFException25.getSuppressed();
        java.lang.Throwable[] throwableArray28 = nMFException25.getSuppressed();
        boolean boolean29 = booleanList20.equals((java.lang.Object) throwableArray28);
        org.ccsds.moims.mo.mal.structures.UShort uShort30 = booleanList20.getAreaNumber();
        java.util.stream.Stream<java.lang.Boolean> booleanStream31 = booleanList20.stream();
        int int32 = booleanList18.lastIndexOf((java.lang.Object) booleanStream31);
        boolean boolean33 = integerList1.containsAll((java.util.Collection<java.lang.Boolean>) booleanList18);
        byte[] byteArray35 = new byte[]{(byte) 10};
        org.ccsds.moims.mo.mal.structures.Blob blob38 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray35, 2,
            (int) ' ');
        org.ccsds.moims.mo.mal.structures.Blob blob39 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray35);
        org.ccsds.moims.mo.mal.structures.Blob blob40 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray35);
        byte[] byteArray41 = blob40.getValue();
        org.ccsds.moims.mo.mal.structures.Blob blob42 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray41);
        org.ccsds.moims.mo.mal.structures.UShort uShort43 = blob42.getAreaNumber();
        int int44 = integerList1.indexOf((java.lang.Object) uShort43);
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "[]" + "'", str2.equals("[]"));
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertTrue("'" + str6 + "' != '" + "[8]" + "'", str6.equals("[8]"));
        org.junit.Assert.assertNotNull(intItor7);
        org.junit.Assert.assertTrue("'" + long9 + "' != '" + 281475010265077L + "'", long9.equals(281475010265077L));
        org.junit.Assert.assertTrue("'" + str14 + "' != '" + "100" + "'", str14.equals("100"));
        org.junit.Assert.assertTrue("'" + long15 + "' != '" + 281474993487884L + "'", long15.equals(281474993487884L));
        org.junit.Assert.assertNotNull(uShort16);
        org.junit.Assert.assertTrue("'" + int17 + "' != '" + (-1) + "'", int17 == (-1));
        org.junit.Assert.assertTrue("'" + long19 + "' != '" + 281475010265086L + "'", long19.equals(281475010265086L));
        org.junit.Assert.assertNotNull(element21);
        org.junit.Assert.assertTrue("'" + long22 + "' != '" + 281475010265086L + "'", long22.equals(281475010265086L));
        org.junit.Assert.assertNotNull(element23);
        org.junit.Assert.assertNotNull(throwableArray26);
        org.junit.Assert.assertNotNull(throwableArray27);
        org.junit.Assert.assertNotNull(throwableArray28);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
        org.junit.Assert.assertNotNull(uShort30);
        org.junit.Assert.assertNotNull(booleanStream31);
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertNotNull(byteArray35);
        org.junit.Assert.assertNotNull(byteArray41);
        org.junit.Assert.assertNotNull(uShort43);
        org.junit.Assert.assertTrue("'" + int44 + "' != '" + (-1) + "'", int44 == (-1));
    }

    @Test
    public void test634() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test634");
        java.lang.Byte[] byteArray2 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList3 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean4 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList3, byteArray2);
        boolean boolean5 = byteList3.isEmpty();
        org.ccsds.moims.mo.mal.structures.IntegerList integerList7 = new org.ccsds.moims.mo.mal.structures.IntegerList(
            (int) '4');
        java.util.stream.Stream<java.lang.Integer> intStream8 = integerList7.parallelStream();
        java.util.stream.Stream<java.lang.Integer> intStream9 = integerList7.stream();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray10 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList11 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean12 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList11, uRIArray10);
        java.lang.Boolean[] booleanArray15 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList16 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean17 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList16,
            booleanArray15);
        java.util.Iterator<java.lang.Boolean> booleanItor18 = booleanList16.iterator();
        boolean boolean19 = uRIList11.retainAll((java.util.Collection<java.lang.Boolean>) booleanList16);
        java.lang.Boolean[] booleanArray22 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList23 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean24 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList23,
            booleanArray22);
        boolean boolean26 = booleanList23.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream27 = booleanList23.stream();
        boolean boolean28 = uRIList11.retainAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        boolean boolean29 = integerList7.removeAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        boolean boolean30 = byteList3.retainAll((java.util.Collection<java.lang.Boolean>) booleanList23);
        java.util.Iterator<java.lang.Byte> byteItor31 = byteList3.iterator();
        org.ccsds.moims.mo.mal.structures.FloatList floatList32 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long33 = floatList32.getShortForm();
        java.lang.String[] strArray35 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList36 = new java.util.ArrayList<java.lang.String>();
        boolean boolean37 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList36,
            strArray35);
        int int38 = strList36.size();
        java.lang.Boolean[] booleanArray41 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList42 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList42,
            booleanArray41);
        boolean boolean45 = booleanList42.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream46 = booleanList42.stream();
        boolean boolean47 = strList36.containsAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        boolean boolean48 = floatList32.containsAll((java.util.Collection<java.lang.Boolean>) booleanList42);
        java.util.Spliterator<java.lang.Float> floatSpliterator49 = floatList32.spliterator();
        esa.mo.nmf.NMFException nMFException51 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray52 = nMFException51.getSuppressed();
        int int53 = floatList32.lastIndexOf((java.lang.Object) throwableArray52);
        org.ccsds.moims.mo.mal.structures.LongList longList54 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int55 = floatList32.lastIndexOf((java.lang.Object) longList54);
        java.util.Iterator<java.lang.Long> longItor56 = longList54.iterator();
        boolean boolean57 = byteList3.equals((java.lang.Object) longList54);
        byteList3.trimToSize();
        org.junit.Assert.assertNotNull(byteArray2);
        org.junit.Assert.assertTrue("'" + boolean4 + "' != '" + true + "'", boolean4);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", !boolean5);
        org.junit.Assert.assertNotNull(intStream8);
        org.junit.Assert.assertNotNull(intStream9);
        org.junit.Assert.assertNotNull(uRIArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + false + "'", !boolean12);
        org.junit.Assert.assertNotNull(booleanArray15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + true + "'", boolean17);
        org.junit.Assert.assertNotNull(booleanItor18);
        org.junit.Assert.assertTrue("'" + boolean19 + "' != '" + false + "'", !boolean19);
        org.junit.Assert.assertNotNull(booleanArray22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + true + "'", boolean24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + true + "'", boolean26);
        org.junit.Assert.assertNotNull(booleanStream27);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + false + "'", !boolean28);
        org.junit.Assert.assertTrue("'" + boolean29 + "' != '" + false + "'", !boolean29);
        org.junit.Assert.assertTrue("'" + boolean30 + "' != '" + true + "'", boolean30);
        org.junit.Assert.assertNotNull(byteItor31);
        org.junit.Assert.assertTrue("'" + long33 + "' != '" + 281475010265084L + "'", long33.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray35);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + true + "'", boolean37);
        org.junit.Assert.assertTrue("'" + int38 + "' != '" + 1 + "'", int38 == 1);
        org.junit.Assert.assertNotNull(booleanArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertTrue("'" + boolean45 + "' != '" + true + "'", boolean45);
        org.junit.Assert.assertNotNull(booleanStream46);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + false + "'", !boolean47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertNotNull(floatSpliterator49);
        org.junit.Assert.assertNotNull(throwableArray52);
        org.junit.Assert.assertTrue("'" + int53 + "' != '" + (-1) + "'", int53 == (-1));
        org.junit.Assert.assertTrue("'" + int55 + "' != '" + (-1) + "'", int55 == (-1));
        org.junit.Assert.assertNotNull(longItor56);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + true + "'", boolean57);
    }

    @Test
    public void test635() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test635");
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.Element element1 = doubleList0.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = doubleList0.getServiceNumber();
        java.lang.Integer int3 = doubleList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort4 = doubleList0.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.Union union6 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Float) 100.0f);
        int int7 = doubleList0.indexOf((java.lang.Object) 100.0f);
        org.ccsds.moims.mo.mal.structures.ShortList shortList8 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.String[] strArray10 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList11 = new java.util.ArrayList<java.lang.String>();
        boolean boolean12 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList11,
            strArray10);
        int int13 = strList11.size();
        java.lang.Boolean[] booleanArray16 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList17 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean18 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList17,
            booleanArray16);
        boolean boolean20 = booleanList17.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream21 = booleanList17.stream();
        boolean boolean22 = strList11.containsAll((java.util.Collection<java.lang.Boolean>) booleanList17);
        boolean boolean23 = shortList8.containsAll((java.util.Collection<java.lang.Boolean>) booleanList17);
        java.util.stream.Stream<java.lang.Short> shortStream24 = shortList8.parallelStream();
        org.ccsds.moims.mo.mal.structures.Element element25 = shortList8.createElement();
        esa.mo.nmf.NMFException nMFException27 = new esa.mo.nmf.NMFException("false");
        java.lang.Throwable[] throwableArray28 = nMFException27.getSuppressed();
        int int29 = shortList8.indexOf((java.lang.Object) throwableArray28);
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList30 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.String str31 = doubleList30.toString();
        java.lang.Boolean[] booleanArray33 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList34 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean35 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList34,
            booleanArray33);
        java.util.ListIterator<java.lang.Boolean> booleanItor37 = booleanList34.listIterator((int) (short) 1);
        boolean boolean38 = doubleList30.containsAll((java.util.Collection<java.lang.Boolean>) booleanList34);
        java.lang.Byte[] byteArray41 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList42 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean43 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList42,
            byteArray41);
        boolean boolean44 = byteList42.isEmpty();
        int int45 = byteList42.size();
        int int46 = booleanList34.indexOf((java.lang.Object) byteList42);
        java.lang.Boolean[] booleanArray49 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList50 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean51 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList50,
            booleanArray49);
        java.util.Iterator<java.lang.Boolean> booleanItor52 = booleanList50.iterator();
        java.util.stream.Stream<java.lang.Boolean> booleanStream53 = booleanList50.stream();
        java.util.Iterator<java.lang.Boolean> booleanItor54 = booleanList50.iterator();
        boolean boolean55 = byteList42.removeAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        boolean boolean56 = shortList8.removeAll((java.util.Collection<java.lang.Boolean>) booleanList50);
        boolean boolean57 = booleanList50.isEmpty();
        boolean boolean58 = doubleList0.remove((java.lang.Object) booleanList50);
        doubleList0.trimToSize();
        java.util.ListIterator<java.lang.Double> doubleItor60 = doubleList0.listIterator();
        org.junit.Assert.assertNotNull(element1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-5) + "'", int3.equals((-5)));
        org.junit.Assert.assertNotNull(uShort4);
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-1) + "'", int7 == (-1));
        org.junit.Assert.assertNotNull(strArray10);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + 1 + "'", int13 == 1);
        org.junit.Assert.assertNotNull(booleanArray16);
        org.junit.Assert.assertTrue("'" + boolean18 + "' != '" + true + "'", boolean18);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + true + "'", boolean20);
        org.junit.Assert.assertNotNull(booleanStream21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
        org.junit.Assert.assertNotNull(shortStream24);
        org.junit.Assert.assertNotNull(element25);
        org.junit.Assert.assertNotNull(throwableArray28);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-1) + "'", int29 == (-1));
        org.junit.Assert.assertTrue("'" + str31 + "' != '" + "[]" + "'", str31.equals("[]"));
        org.junit.Assert.assertNotNull(booleanArray33);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + true + "'", boolean35);
        org.junit.Assert.assertNotNull(booleanItor37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertNotNull(byteArray41);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + true + "'", boolean43);
        org.junit.Assert.assertTrue("'" + boolean44 + "' != '" + false + "'", !boolean44);
        org.junit.Assert.assertTrue("'" + int45 + "' != '" + 2 + "'", int45 == 2);
        org.junit.Assert.assertTrue("'" + int46 + "' != '" + (-1) + "'", int46 == (-1));
        org.junit.Assert.assertNotNull(booleanArray49);
        org.junit.Assert.assertTrue("'" + boolean51 + "' != '" + true + "'", boolean51);
        org.junit.Assert.assertNotNull(booleanItor52);
        org.junit.Assert.assertNotNull(booleanStream53);
        org.junit.Assert.assertNotNull(booleanItor54);
        org.junit.Assert.assertTrue("'" + boolean55 + "' != '" + false + "'", !boolean55);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + false + "'", !boolean56);
        org.junit.Assert.assertTrue("'" + boolean57 + "' != '" + false + "'", !boolean57);
        org.junit.Assert.assertTrue("'" + boolean58 + "' != '" + false + "'", !boolean58);
        org.junit.Assert.assertNotNull(doubleItor60);
    }

    @Test
    public void test636() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test636");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl3 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl4 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl5 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl6 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl7 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl8 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl9 = mCServicesProviderNMF0
            .getAlertService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl3);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl4);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl5);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl6);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl7);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl8);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl9);
    }

    @Test
    public void test637() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test637");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl3 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl4 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl5 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl6 = mCServicesProviderNMF0
            .getParameterService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl3);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl4);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl6);
    }

    @Test
    public void test638() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test638");
        org.ccsds.moims.mo.mal.structures.LongList longList1 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = longList1.getAreaVersion();
        java.util.Iterator<java.lang.Long> longItor3 = longList1.iterator();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF4 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF4
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl6 = mCServicesProviderNMF4
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl7 = mCServicesProviderNMF4
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl8 = mCServicesProviderNMF4
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl9 = mCServicesProviderNMF4
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF4
            .getParameterService();
        int int11 = longList1.indexOf((java.lang.Object) mCServicesProviderNMF4);
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl12 = mCServicesProviderNMF4
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl13 = mCServicesProviderNMF4
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl14 = mCServicesProviderNMF4
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl15 = mCServicesProviderNMF4
            .getAlertService();
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(longItor3);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl6);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl7);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl8);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl9);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-1) + "'", int11 == (-1));
        org.junit.Assert.assertNotNull(alertProviderServiceImpl12);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl13);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl14);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl15);
    }

    @Test
    public void test639() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test639");
        java.lang.Long[] longArray4 = new java.lang.Long[]{10L, (-1L), (-1L), 10L};
        java.util.ArrayList<java.lang.Long> longList5 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList5, longArray4);
        java.lang.Object obj7 = null;
        boolean boolean8 = longList5.contains(obj7);
        java.lang.String str9 = longList5.toString();
        boolean boolean11 = longList5.add((java.lang.Long) 281475010265081L);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList12 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Integer int13 = uShortList12.getTypeShortForm();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor14 = uShortList12.iterator();
        java.lang.Object[] objArray15 = uShortList12.toArray();
        boolean boolean17 = uShortList12.equals((java.lang.Object) 1);
        esa.mo.nmf.NMFException nMFException20 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray21 = nMFException20.getSuppressed();
        java.lang.Throwable[] throwableArray22 = nMFException20.getSuppressed();
        esa.mo.nmf.NMFException nMFException24 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray25 = nMFException24.getSuppressed();
        nMFException20.addSuppressed((java.lang.Throwable) nMFException24);
        java.lang.Throwable[] throwableArray27 = nMFException24.getSuppressed();
        java.lang.Throwable[] throwableArray28 = nMFException24.getSuppressed();
        esa.mo.nmf.NMFException nMFException29 = new esa.mo.nmf.NMFException("[]",
            (java.lang.Throwable) nMFException24);
        int int30 = uShortList12.indexOf((java.lang.Object) nMFException29);
        java.lang.String str31 = nMFException29.toString();
        int int32 = longList5.lastIndexOf((java.lang.Object) str31);
        org.junit.Assert.assertNotNull(longArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertTrue("'" + str9 + "' != '" + "[10, -1, -1, 10]" + "'", str9.equals("[10, -1, -1, 10]"));
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-10) + "'", int13.equals((-10)));
        org.junit.Assert.assertNotNull(uShortItor14);
        org.junit.Assert.assertNotNull(objArray15);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
        org.junit.Assert.assertNotNull(throwableArray21);
        org.junit.Assert.assertNotNull(throwableArray22);
        org.junit.Assert.assertNotNull(throwableArray25);
        org.junit.Assert.assertNotNull(throwableArray27);
        org.junit.Assert.assertNotNull(throwableArray28);
        org.junit.Assert.assertTrue("'" + int30 + "' != '" + (-1) + "'", int30 == (-1));
        org.junit.Assert.assertTrue("'" + str31 + "' != '" + "esa.mo.nmf.NMFException: []" + "'", str31.equals(
            "esa.mo.nmf.NMFException: []"));
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
    }

    @Test
    public void test640() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test640");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        org.ccsds.moims.mo.mal.structures.UShort uShort17 = org.ccsds.moims.mo.mal.structures.LongList.AREA_SHORT_FORM;
        java.lang.Long long18 = uShort17.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort19 = uShort17.getServiceNumber();
        boolean boolean20 = floatList0.remove((java.lang.Object) uShort17);
        floatList0.trimToSize();
        java.util.Iterator<java.lang.Float> floatItor22 = floatList0.iterator();
        floatList0.ensureCapacity(1);
        java.util.stream.Stream<java.lang.Float> floatStream25 = floatList0.stream();
        java.util.Iterator<java.lang.Float> floatItor26 = floatList0.iterator();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList27 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element28 = booleanList27.createElement();
        java.lang.Long long29 = booleanList27.getShortForm();
        org.ccsds.moims.mo.mal.structures.Element element30 = booleanList27.createElement();
        esa.mo.nmf.NMFException nMFException32 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray33 = nMFException32.getSuppressed();
        java.lang.Throwable[] throwableArray34 = nMFException32.getSuppressed();
        java.lang.Throwable[] throwableArray35 = nMFException32.getSuppressed();
        boolean boolean36 = booleanList27.equals((java.lang.Object) throwableArray35);
        org.ccsds.moims.mo.mal.structures.Element element37 = booleanList27.createElement();
        org.ccsds.moims.mo.mal.structures.Element element38 = booleanList27.createElement();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet39 = booleanList27.getAreaVersion();
        int int40 = floatList0.lastIndexOf((java.lang.Object) booleanList27);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(uShort17);
        org.junit.Assert.assertTrue("'" + long18 + "' != '" + 281474993487882L + "'", long18.equals(281474993487882L));
        org.junit.Assert.assertNotNull(uShort19);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertNotNull(floatItor22);
        org.junit.Assert.assertNotNull(floatStream25);
        org.junit.Assert.assertNotNull(floatItor26);
        org.junit.Assert.assertNotNull(element28);
        org.junit.Assert.assertTrue("'" + long29 + "' != '" + 281475010265086L + "'", long29.equals(281475010265086L));
        org.junit.Assert.assertNotNull(element30);
        org.junit.Assert.assertNotNull(throwableArray33);
        org.junit.Assert.assertNotNull(throwableArray34);
        org.junit.Assert.assertNotNull(throwableArray35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + false + "'", !boolean36);
        org.junit.Assert.assertNotNull(element37);
        org.junit.Assert.assertNotNull(element38);
        org.junit.Assert.assertNotNull(uOctet39);
        org.junit.Assert.assertTrue("'" + int40 + "' != '" + (-1) + "'", int40 == (-1));
    }

    @Test
    public void test641() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test641");
        org.ccsds.moims.mo.mal.structures.OctetList octetList1 = new org.ccsds.moims.mo.mal.structures.OctetList(15);
        java.lang.String str2 = octetList1.toString();
        java.lang.Byte[] byteArray5 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList6 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean7 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList6, byteArray5);
        boolean boolean8 = byteList6.isEmpty();
        java.lang.Boolean[] booleanArray11 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList12 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList12,
            booleanArray11);
        boolean boolean15 = booleanList12.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream16 = booleanList12.stream();
        boolean boolean17 = byteList6.removeAll((java.util.Collection<java.lang.Boolean>) booleanList12);
        org.ccsds.moims.mo.mal.structures.Union union19 = new org.ccsds.moims.mo.mal.structures.Union(
            (java.lang.Long) 0L);
        boolean boolean20 = byteList6.equals((java.lang.Object) 0L);
        java.lang.Byte[] byteArray23 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList24 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean25 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList24,
            byteArray23);
        boolean boolean26 = byteList24.isEmpty();
        java.lang.Boolean[] booleanArray29 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList30 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean31 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList30,
            booleanArray29);
        boolean boolean33 = booleanList30.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream34 = booleanList30.stream();
        boolean boolean35 = byteList24.removeAll((java.util.Collection<java.lang.Boolean>) booleanList30);
        booleanList30.trimToSize();
        boolean boolean37 = byteList6.contains((java.lang.Object) booleanList30);
        boolean boolean38 = octetList1.removeAll((java.util.Collection<java.lang.Boolean>) booleanList30);
        org.ccsds.moims.mo.mal.structures.ShortList shortList39 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj40 = shortList39.clone();
        java.util.stream.Stream<java.lang.Short> shortStream41 = shortList39.parallelStream();
        org.ccsds.moims.mo.mal.structures.UShort uShort42 = shortList39.getServiceNumber();
        java.lang.Byte[] byteArray45 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList46 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean47 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList46,
            byteArray45);
        boolean boolean48 = byteList46.isEmpty();
        int int50 = byteList46.indexOf((java.lang.Object) 'a');
        int int52 = byteList46.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray54 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList55 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean56 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList55,
            booleanArray54);
        java.util.ListIterator<java.lang.Boolean> booleanItor58 = booleanList55.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream59 = booleanList55.parallelStream();
        boolean boolean61 = booleanList55.add((java.lang.Boolean) false);
        boolean boolean62 = byteList46.retainAll((java.util.Collection<java.lang.Boolean>) booleanList55);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList63 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj64 = uShortList63.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream65 = uShortList63
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor66 = uShortList63.iterator();
        boolean boolean67 = byteList46.equals((java.lang.Object) uShortItor66);
        org.ccsds.moims.mo.mal.structures.ShortList shortList68 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj69 = shortList68.clone();
        boolean boolean70 = byteList46.equals((java.lang.Object) shortList68);
        esa.mo.nmf.NMFException nMFException72 = new esa.mo.nmf.NMFException("-1");
        boolean boolean73 = byteList46.equals((java.lang.Object) nMFException72);
        byteList46.trimToSize();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList75 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element76 = booleanList75.createElement();
        java.lang.Long long77 = booleanList75.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort78 = booleanList75.getAreaNumber();
        java.lang.Integer int79 = booleanList75.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray80 = new org.ccsds.moims.mo.mal.structures.BooleanList[]{booleanList75};
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray81 = byteList46.toArray(booleanListArray80);
        java.lang.Iterable<java.lang.Boolean>[] booleanIterableArray82 = shortList39.toArray(
            (java.lang.Iterable<java.lang.Boolean>[]) booleanListArray81);
        java.util.AbstractCollection<java.lang.Boolean>[] booleanCollectionArray83 = octetList1.toArray(
            (java.util.AbstractCollection<java.lang.Boolean>[]) booleanListArray81);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet84 = octetList1.getAreaVersion();
        org.junit.Assert.assertTrue("'" + str2 + "' != '" + "[]" + "'", str2.equals("[]"));
        org.junit.Assert.assertNotNull(byteArray5);
        org.junit.Assert.assertTrue("'" + boolean7 + "' != '" + true + "'", boolean7);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", !boolean8);
        org.junit.Assert.assertNotNull(booleanArray11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertNotNull(booleanStream16);
        org.junit.Assert.assertTrue("'" + boolean17 + "' != '" + false + "'", !boolean17);
        org.junit.Assert.assertTrue("'" + boolean20 + "' != '" + false + "'", !boolean20);
        org.junit.Assert.assertNotNull(byteArray23);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + true + "'", boolean25);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", !boolean26);
        org.junit.Assert.assertNotNull(booleanArray29);
        org.junit.Assert.assertTrue("'" + boolean31 + "' != '" + true + "'", boolean31);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + true + "'", boolean33);
        org.junit.Assert.assertNotNull(booleanStream34);
        org.junit.Assert.assertTrue("'" + boolean35 + "' != '" + false + "'", !boolean35);
        org.junit.Assert.assertTrue("'" + boolean37 + "' != '" + false + "'", !boolean37);
        org.junit.Assert.assertTrue("'" + boolean38 + "' != '" + false + "'", !boolean38);
        org.junit.Assert.assertNotNull(obj40);
        org.junit.Assert.assertNotNull(shortStream41);
        org.junit.Assert.assertNotNull(uShort42);
        org.junit.Assert.assertNotNull(byteArray45);
        org.junit.Assert.assertTrue("'" + boolean47 + "' != '" + true + "'", boolean47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertTrue("'" + int50 + "' != '" + (-1) + "'", int50 == (-1));
        org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-1) + "'", int52 == (-1));
        org.junit.Assert.assertNotNull(booleanArray54);
        org.junit.Assert.assertTrue("'" + boolean56 + "' != '" + true + "'", boolean56);
        org.junit.Assert.assertNotNull(booleanItor58);
        org.junit.Assert.assertNotNull(booleanStream59);
        org.junit.Assert.assertTrue("'" + boolean61 + "' != '" + true + "'", boolean61);
        org.junit.Assert.assertTrue("'" + boolean62 + "' != '" + true + "'", boolean62);
        org.junit.Assert.assertNotNull(obj64);
        org.junit.Assert.assertNotNull(uShortStream65);
        org.junit.Assert.assertNotNull(uShortItor66);
        org.junit.Assert.assertTrue("'" + boolean67 + "' != '" + false + "'", !boolean67);
        org.junit.Assert.assertNotNull(obj69);
        org.junit.Assert.assertTrue("'" + boolean70 + "' != '" + true + "'", boolean70);
        org.junit.Assert.assertTrue("'" + boolean73 + "' != '" + false + "'", !boolean73);
        org.junit.Assert.assertNotNull(element76);
        org.junit.Assert.assertTrue("'" + long77 + "' != '" + 281475010265086L + "'", long77.equals(281475010265086L));
        org.junit.Assert.assertNotNull(uShort78);
        org.junit.Assert.assertTrue("'" + int79 + "' != '" + (-2) + "'", int79.equals((-2)));
        org.junit.Assert.assertNotNull(booleanListArray80);
        org.junit.Assert.assertNotNull(booleanListArray81);
        org.junit.Assert.assertNotNull(booleanIterableArray82);
        org.junit.Assert.assertNotNull(booleanCollectionArray83);
        org.junit.Assert.assertNotNull(uOctet84);
    }

    @Test
    public void test642() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test642");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream2 = uShortList0.parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor3 = uShortList0.iterator();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor4 = uShortList0.listIterator();
        java.lang.String str5 = uShortList0.toString();
        java.lang.Object[] objArray6 = uShortList0.toArray();
        org.ccsds.moims.mo.mal.structures.LongList longList8 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet9 = longList8.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.UShort uShort10 = longList8.getAreaNumber();
        longList8.clear();
        java.util.Iterator<java.lang.Long> longItor12 = longList8.iterator();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet13 = longList8.getAreaVersion();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl18 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl19 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl21 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl22 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl23 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl24 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF25 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl26 = mCServicesProviderNMF25
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl27 = mCServicesProviderNMF25
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl28 = mCServicesProviderNMF25
            .getAlertService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl29 = mCServicesProviderNMF25
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl30 = mCServicesProviderNMF25
            .getParameterService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl31 = mCServicesProviderNMF25
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF32 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl33 = mCServicesProviderNMF32
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl34 = mCServicesProviderNMF32
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl35 = mCServicesProviderNMF32
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl36 = mCServicesProviderNMF32
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl37 = mCServicesProviderNMF32
            .getParameterService();
        org.ccsds.moims.mo.mc.parameter.provider.ParameterInheritanceSkeleton[] parameterInheritanceSkeletonArray38 = new org.ccsds.moims.mo.mc.parameter.provider.ParameterInheritanceSkeleton[]{parameterProviderServiceImpl24,
                                                                                                                                                                                                  parameterProviderServiceImpl31,
                                                                                                                                                                                                  parameterProviderServiceImpl37};
        org.ccsds.moims.mo.mc.parameter.provider.ParameterInheritanceSkeleton[] parameterInheritanceSkeletonArray39 = longList8
            .toArray(parameterInheritanceSkeletonArray38);
        org.ccsds.moims.mo.mal.provider.MALInteractionHandler[] mALInteractionHandlerArray40 = uShortList0.toArray(
            (org.ccsds.moims.mo.mal.provider.MALInteractionHandler[]) parameterInheritanceSkeletonArray39);
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(uShortStream2);
        org.junit.Assert.assertNotNull(uShortItor3);
        org.junit.Assert.assertNotNull(uShortItor4);
        org.junit.Assert.assertTrue("'" + str5 + "' != '" + "[]" + "'", str5.equals("[]"));
        org.junit.Assert.assertNotNull(objArray6);
        org.junit.Assert.assertNotNull(uOctet9);
        org.junit.Assert.assertNotNull(uShort10);
        org.junit.Assert.assertNotNull(longItor12);
        org.junit.Assert.assertNotNull(uOctet13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl18);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl19);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl21);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl22);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl23);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl24);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl26);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl27);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl28);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl29);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl30);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl31);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl33);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl34);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl35);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl36);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl37);
        org.junit.Assert.assertNotNull(parameterInheritanceSkeletonArray38);
        org.junit.Assert.assertNotNull(parameterInheritanceSkeletonArray39);
        org.junit.Assert.assertNotNull(mALInteractionHandlerArray40);
    }

    @Test
    public void test643() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test643");
        org.ccsds.moims.mo.mal.structures.LongList longList1 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = longList1.getAreaVersion();
        java.util.Iterator<java.lang.Long> longItor3 = longList1.iterator();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF4 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF4
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl6 = mCServicesProviderNMF4
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl7 = mCServicesProviderNMF4
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl8 = mCServicesProviderNMF4
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl9 = mCServicesProviderNMF4
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF4
            .getParameterService();
        int int11 = longList1.indexOf((java.lang.Object) mCServicesProviderNMF4);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF4
            .getParameterService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl13 = mCServicesProviderNMF4
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl14 = mCServicesProviderNMF4
            .getAggregationService();
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(longItor3);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl6);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl7);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl8);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl9);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-1) + "'", int11 == (-1));
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl13);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl14);
    }

    @Test
    public void test644() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test644");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        java.util.Spliterator<java.lang.Float> floatSpliterator17 = floatList0.spliterator();
        esa.mo.nmf.NMFException nMFException19 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray20 = nMFException19.getSuppressed();
        int int21 = floatList0.lastIndexOf((java.lang.Object) throwableArray20);
        org.ccsds.moims.mo.mal.structures.LongList longList22 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int23 = floatList0.lastIndexOf((java.lang.Object) longList22);
        org.ccsds.moims.mo.mal.structures.UShort uShort24 = longList22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort25 = longList22.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.StringList stringList26 = new org.ccsds.moims.mo.mal.structures.StringList();
        org.ccsds.moims.mo.mal.structures.Element element27 = stringList26.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort28 = stringList26.getServiceNumber();
        java.lang.Integer int29 = stringList26.getTypeShortForm();
        stringList26.ensureCapacity(13);
        int int32 = longList22.lastIndexOf((java.lang.Object) stringList26);
        java.util.stream.Stream<java.lang.Long> longStream33 = longList22.parallelStream();
        org.ccsds.moims.mo.mal.structures.UShort uShort34 = longList22.getAreaNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort35 = longList22.getServiceNumber();
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertNotNull(floatSpliterator17);
        org.junit.Assert.assertNotNull(throwableArray20);
        org.junit.Assert.assertTrue("'" + int21 + "' != '" + (-1) + "'", int21 == (-1));
        org.junit.Assert.assertTrue("'" + int23 + "' != '" + (-1) + "'", int23 == (-1));
        org.junit.Assert.assertNotNull(uShort24);
        org.junit.Assert.assertNotNull(uShort25);
        org.junit.Assert.assertNotNull(element27);
        org.junit.Assert.assertNotNull(uShort28);
        org.junit.Assert.assertTrue("'" + int29 + "' != '" + (-15) + "'", int29.equals((-15)));
        org.junit.Assert.assertTrue("'" + int32 + "' != '" + (-1) + "'", int32 == (-1));
        org.junit.Assert.assertNotNull(longStream33);
        org.junit.Assert.assertNotNull(uShort34);
        org.junit.Assert.assertNotNull(uShort35);
    }

    @Test
    public void test645() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test645");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream2 = uShortList0.parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor3 = uShortList0.iterator();
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor4 = uShortList0.listIterator();
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider5 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl13 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl14 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl15 = mCServicesProviderNMF11
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF16 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl17 = mCServicesProviderNMF16
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl18 = mCServicesProviderNMF16
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF19 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCServicesProviderNMF19
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl21 = mCServicesProviderNMF19
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl22 = mCServicesProviderNMF19
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl23 = mCServicesProviderNMF19
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration24 = new esa.mo.nmf.MCRegistration(cOMServicesProvider5,
            parameterProviderServiceImpl10, aggregationProviderServiceImpl15, alertProviderServiceImpl18,
            actionProviderServiceImpl23);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl25 = mCRegistration24.actionService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl26 = mCRegistration24.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider27 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF28 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl29 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl30 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF28
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl32 = mCServicesProviderNMF28
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF33 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl34 = mCServicesProviderNMF33
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl35 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl36 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl37 = mCServicesProviderNMF33
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF38 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl39 = mCServicesProviderNMF38
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl40 = mCServicesProviderNMF38
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF41 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl42 = mCServicesProviderNMF41
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl43 = mCServicesProviderNMF41
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl44 = mCServicesProviderNMF41
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl45 = mCServicesProviderNMF41
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration46 = new esa.mo.nmf.MCRegistration(cOMServicesProvider27,
            parameterProviderServiceImpl32, aggregationProviderServiceImpl37, alertProviderServiceImpl40,
            actionProviderServiceImpl45);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF47 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl48 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl49 = mCServicesProviderNMF47
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl50 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl51 = mCServicesProviderNMF47
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl52 = mCServicesProviderNMF47
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl53 = mCServicesProviderNMF47
            .getActionService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF54 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl55 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl56 = mCServicesProviderNMF54
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl57 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl58 = mCServicesProviderNMF54
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl59 = mCServicesProviderNMF54
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl60 = mCServicesProviderNMF54
            .getActionService();
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray61 = new org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[]{actionProviderServiceImpl26,
                                                                                                                                                                                   actionProviderServiceImpl45,
                                                                                                                                                                                   actionProviderServiceImpl53,
                                                                                                                                                                                   actionProviderServiceImpl60};
        org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton[] actionInheritanceSkeletonArray62 = uShortList0
            .toArray(actionInheritanceSkeletonArray61);
        uShortList0.ensureCapacity(18);
        java.util.Spliterator<org.ccsds.moims.mo.mal.structures.UShort> uShortSpliterator65 = uShortList0.spliterator();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList66 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.UShort uShort67 = booleanList66.getAreaNumber();
        java.util.ListIterator<java.lang.Boolean> booleanItor68 = booleanList66.listIterator();
        int int69 = uShortList0.lastIndexOf((java.lang.Object) booleanList66);
        try {
            java.util.List<org.ccsds.moims.mo.mal.structures.UShort> uShortList72 = uShortList0.subList(5, 14);
            org.junit.Assert.fail(
                "Expected exception of type java.lang.IndexOutOfBoundsException; message: toIndex = 14");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(uShortStream2);
        org.junit.Assert.assertNotNull(uShortItor3);
        org.junit.Assert.assertNotNull(uShortItor4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl13);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl14);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl15);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl17);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl21);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl22);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl25);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl26);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl29);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl32);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl34);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl35);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl36);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl37);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl39);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl40);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl42);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl43);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl44);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl45);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl48);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl49);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl50);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl51);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl52);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl53);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl55);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl56);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl57);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl58);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl59);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl60);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray61);
        org.junit.Assert.assertNotNull(actionInheritanceSkeletonArray62);
        org.junit.Assert.assertNotNull(uShortSpliterator65);
        org.junit.Assert.assertNotNull(uShort67);
        org.junit.Assert.assertNotNull(booleanItor68);
        org.junit.Assert.assertTrue("'" + int69 + "' != '" + (-1) + "'", int69 == (-1));
    }

    @Test
    public void test646() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test646");
        org.ccsds.moims.mo.mal.structures.LongList longList1 = new org.ccsds.moims.mo.mal.structures.LongList(
            (int) (byte) 1);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet2 = longList1.getAreaVersion();
        java.util.Iterator<java.lang.Long> longItor3 = longList1.iterator();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF4 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF4
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl6 = mCServicesProviderNMF4
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl7 = mCServicesProviderNMF4
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl8 = mCServicesProviderNMF4
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl9 = mCServicesProviderNMF4
            .getAlertService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl10 = mCServicesProviderNMF4
            .getParameterService();
        int int11 = longList1.indexOf((java.lang.Object) mCServicesProviderNMF4);
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl12 = mCServicesProviderNMF4
            .getAggregationService();
        org.junit.Assert.assertNotNull(uOctet2);
        org.junit.Assert.assertNotNull(longItor3);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl6);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl7);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl8);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl9);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl10);
        org.junit.Assert.assertTrue("'" + int11 + "' != '" + (-1) + "'", int11 == (-1));
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl12);
    }

    @Test
    public void test647() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test647");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl20 = mCRegistration19.actionService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl21 = mCRegistration19.parameterService;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl22 = mCRegistration19.actionService;
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider23 = mCRegistration19.comServices;
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl24 = mCRegistration19.actionService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl20);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl21);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl22);
        org.junit.Assert.assertNull(cOMServicesProvider23);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl24);
    }

    @Test
    public void test648() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test648");
        java.lang.Byte[] byteArray4 = new java.lang.Byte[]{(byte) -1, (byte) -1, (byte) 1, (byte) 1};
        java.util.ArrayList<java.lang.Byte> byteList5 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean6 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList5, byteArray4);
        boolean boolean8 = byteList5.add((java.lang.Byte) (byte) 10);
        java.lang.Object obj9 = byteList5.clone();
        java.util.stream.Stream<java.lang.Byte> byteStream10 = byteList5.stream();
        org.ccsds.moims.mo.mal.structures.StringList stringList12 = new org.ccsds.moims.mo.mal.structures.StringList(
            100);
        int int13 = byteList5.indexOf((java.lang.Object) stringList12);
        byte[] byteArray17 = new byte[]{(byte) 0, (byte) 0, (byte) 0};
        org.ccsds.moims.mo.mal.structures.Blob blob18 = new org.ccsds.moims.mo.mal.structures.Blob(byteArray17);
        int int19 = blob18.getLength();
        org.ccsds.moims.mo.mal.structures.Blob blob20 = new org.ccsds.moims.mo.mal.structures.Blob();
        boolean boolean21 = blob18.equals((java.lang.Object) blob20);
        boolean boolean22 = stringList12.remove((java.lang.Object) blob20);
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF23 = new esa.mo.nmf.MCServicesProviderNMF();
        boolean boolean24 = stringList12.equals((java.lang.Object) mCServicesProviderNMF23);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet25 = stringList12.getAreaVersion();
        boolean boolean27 = stringList12.add("[0.0]");
        java.lang.Object[] objArray28 = stringList12.toArray();
        org.junit.Assert.assertNotNull(byteArray4);
        org.junit.Assert.assertTrue("'" + boolean6 + "' != '" + true + "'", boolean6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertNotNull(obj9);
        org.junit.Assert.assertNotNull(byteStream10);
        org.junit.Assert.assertTrue("'" + int13 + "' != '" + (-1) + "'", int13 == (-1));
        org.junit.Assert.assertNotNull(byteArray17);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 3 + "'", int19 == 3);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + false + "'", !boolean22);
        org.junit.Assert.assertTrue("'" + boolean24 + "' != '" + false + "'", !boolean24);
        org.junit.Assert.assertNotNull(uOctet25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertNotNull(objArray28);
    }

    @Test
    public void test649() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test649");
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider0 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF1 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl2 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF1
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl5 = mCServicesProviderNMF1
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF6 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl7 = mCServicesProviderNMF6
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl8 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl9 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl10 = mCServicesProviderNMF6
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF11 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl12 = mCServicesProviderNMF11
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl13 = mCServicesProviderNMF11
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF14 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl15 = mCServicesProviderNMF14
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl16 = mCServicesProviderNMF14
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl17 = mCServicesProviderNMF14
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl18 = mCServicesProviderNMF14
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration19 = new esa.mo.nmf.MCRegistration(cOMServicesProvider0,
            parameterProviderServiceImpl5, aggregationProviderServiceImpl10, alertProviderServiceImpl13,
            actionProviderServiceImpl18);
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl20 = mCRegistration19.parameterService;
        esa.mo.nmf.MCRegistration.RegistrationMode registrationMode21 = esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS;
        mCRegistration19.setMode(registrationMode21);
        esa.mo.com.impl.util.COMServicesProvider cOMServicesProvider23 = null;
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF24 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl25 = mCServicesProviderNMF24
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl26 = mCServicesProviderNMF24
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl27 = mCServicesProviderNMF24
            .getAggregationService();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl28 = mCServicesProviderNMF24
            .getParameterService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF29 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl30 = mCServicesProviderNMF29
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl31 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl32 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl33 = mCServicesProviderNMF29
            .getAggregationService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF34 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl35 = mCServicesProviderNMF34
            .getParameterService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl36 = mCServicesProviderNMF34
            .getAlertService();
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF37 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl38 = mCServicesProviderNMF37
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl39 = mCServicesProviderNMF37
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl40 = mCServicesProviderNMF37
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl41 = mCServicesProviderNMF37
            .getActionService();
        esa.mo.nmf.MCRegistration mCRegistration42 = new esa.mo.nmf.MCRegistration(cOMServicesProvider23,
            parameterProviderServiceImpl28, aggregationProviderServiceImpl33, alertProviderServiceImpl36,
            actionProviderServiceImpl41);
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl43 = mCRegistration42.actionService;
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl44 = mCRegistration42.aggregationService;
        esa.mo.nmf.MCRegistration.RegistrationMode registrationMode45 = esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS;
        mCRegistration42.setMode(registrationMode45);
        mCRegistration19.setMode(registrationMode45);
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl48 = mCRegistration19.aggregationService;
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl49 = mCRegistration19.parameterService;
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl5);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl7);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl8);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl9);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl10);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl12);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl13);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl15);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl16);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl17);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl18);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl20);
        org.junit.Assert.assertTrue("'" + registrationMode21 + "' != '" +
            esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS + "'", registrationMode21.equals(
                esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS));
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl25);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl26);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl27);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl28);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl30);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl31);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl32);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl33);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl35);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl36);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl38);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl39);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl40);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl41);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl43);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl44);
        org.junit.Assert.assertTrue("'" + registrationMode45 + "' != '" +
            esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS + "'", registrationMode45.equals(
                esa.mo.nmf.MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS));
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl48);
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl49);
    }

    @Test
    public void test650() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test650");
        java.lang.Throwable throwable1 = null;
        esa.mo.nmf.NMFException nMFException2 = new esa.mo.nmf.NMFException("[hi!]", throwable1);
    }

    @Test
    public void test651() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test651");
        org.ccsds.moims.mo.mal.structures.FloatList floatList0 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long1 = floatList0.getShortForm();
        java.lang.String[] strArray3 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList4 = new java.util.ArrayList<java.lang.String>();
        boolean boolean5 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList4, strArray3);
        int int6 = strList4.size();
        java.lang.Boolean[] booleanArray9 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList10 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean11 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList10,
            booleanArray9);
        boolean boolean13 = booleanList10.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream14 = booleanList10.stream();
        boolean boolean15 = strList4.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        boolean boolean16 = floatList0.containsAll((java.util.Collection<java.lang.Boolean>) booleanList10);
        org.ccsds.moims.mo.mal.structures.URI uRI17 = new org.ccsds.moims.mo.mal.structures.URI();
        int int18 = floatList0.lastIndexOf((java.lang.Object) uRI17);
        floatList0.trimToSize();
        boolean boolean21 = floatList0.remove((java.lang.Object) (short) 10);
        java.util.stream.Stream<java.lang.Float> floatStream22 = floatList0.parallelStream();
        org.ccsds.moims.mo.mal.structures.Identifier identifier23 = new org.ccsds.moims.mo.mal.structures.Identifier();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet24 = identifier23.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.URI[] uRIArray25 = new org.ccsds.moims.mo.mal.structures.URI[]{};
        java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI> uRIList26 = new java.util.ArrayList<org.ccsds.moims.mo.mal.structures.URI>();
        boolean boolean27 = java.util.Collections.addAll(
            (java.util.Collection<org.ccsds.moims.mo.mal.structures.URI>) uRIList26, uRIArray25);
        java.lang.Boolean[] booleanArray30 = new java.lang.Boolean[]{false, true};
        java.util.ArrayList<java.lang.Boolean> booleanList31 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean32 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList31,
            booleanArray30);
        java.util.Iterator<java.lang.Boolean> booleanItor33 = booleanList31.iterator();
        boolean boolean34 = uRIList26.retainAll((java.util.Collection<java.lang.Boolean>) booleanList31);
        java.lang.Boolean[] booleanArray37 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList38 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean39 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList38,
            booleanArray37);
        boolean boolean41 = booleanList38.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream42 = booleanList38.stream();
        boolean boolean43 = uRIList26.retainAll((java.util.Collection<java.lang.Boolean>) booleanList38);
        uRIList26.ensureCapacity(5);
        boolean boolean46 = identifier23.equals((java.lang.Object) uRIList26);
        java.util.ListIterator<org.ccsds.moims.mo.mal.structures.URI> uRIItor47 = uRIList26.listIterator();
        org.ccsds.moims.mo.mal.structures.URI uRI50 = new org.ccsds.moims.mo.mal.structures.URI("[10, -1, -1, 10]");
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList51 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.lang.Integer int52 = doubleList51.getTypeShortForm();
        boolean boolean53 = uRI50.equals((java.lang.Object) doubleList51);
        uRIList26.add(0, uRI50);
        org.ccsds.moims.mo.mal.structures.URI uRI56 = new org.ccsds.moims.mo.mal.structures.URI("100.0");
        java.lang.String str57 = uRI56.toString();
        java.lang.String str58 = uRI56.toString();
        boolean boolean59 = uRIList26.add(uRI56);
        java.lang.Long long60 = uRI56.getShortForm();
        java.lang.Integer int61 = uRI56.getTypeShortForm();
        int int62 = floatList0.lastIndexOf((java.lang.Object) uRI56);
        java.lang.String str63 = uRI56.toString();
        java.lang.Integer int64 = uRI56.getTypeShortForm();
        java.lang.Long long65 = uRI56.getShortForm();
        org.ccsds.moims.mo.mal.structures.FloatList floatList66 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long67 = floatList66.getShortForm();
        java.lang.String[] strArray69 = new java.lang.String[]{"hi!"};
        java.util.ArrayList<java.lang.String> strList70 = new java.util.ArrayList<java.lang.String>();
        boolean boolean71 = java.util.Collections.addAll((java.util.Collection<java.lang.String>) strList70,
            strArray69);
        int int72 = strList70.size();
        java.lang.Boolean[] booleanArray75 = new java.lang.Boolean[]{true, false};
        java.util.ArrayList<java.lang.Boolean> booleanList76 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean77 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList76,
            booleanArray75);
        boolean boolean79 = booleanList76.add((java.lang.Boolean) true);
        java.util.stream.Stream<java.lang.Boolean> booleanStream80 = booleanList76.stream();
        boolean boolean81 = strList70.containsAll((java.util.Collection<java.lang.Boolean>) booleanList76);
        boolean boolean82 = floatList66.containsAll((java.util.Collection<java.lang.Boolean>) booleanList76);
        java.util.Spliterator<java.lang.Float> floatSpliterator83 = floatList66.spliterator();
        esa.mo.nmf.NMFException nMFException85 = new esa.mo.nmf.NMFException("0");
        java.lang.Throwable[] throwableArray86 = nMFException85.getSuppressed();
        int int87 = floatList66.lastIndexOf((java.lang.Object) throwableArray86);
        org.ccsds.moims.mo.mal.structures.LongList longList88 = new org.ccsds.moims.mo.mal.structures.LongList();
        int int89 = floatList66.lastIndexOf((java.lang.Object) longList88);
        longList88.trimToSize();
        java.lang.Object[] objArray91 = longList88.toArray();
        boolean boolean92 = uRI56.equals((java.lang.Object) objArray91);
        org.junit.Assert.assertTrue("'" + long1 + "' != '" + 281475010265084L + "'", long1.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray3);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + 1 + "'", int6 == 1);
        org.junit.Assert.assertNotNull(booleanArray9);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + true + "'", boolean11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertNotNull(booleanStream14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", !boolean15);
        org.junit.Assert.assertTrue("'" + boolean16 + "' != '" + false + "'", !boolean16);
        org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-1) + "'", int18 == (-1));
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", !boolean21);
        org.junit.Assert.assertNotNull(floatStream22);
        org.junit.Assert.assertNotNull(uOctet24);
        org.junit.Assert.assertNotNull(uRIArray25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + false + "'", !boolean27);
        org.junit.Assert.assertNotNull(booleanArray30);
        org.junit.Assert.assertTrue("'" + boolean32 + "' != '" + true + "'", boolean32);
        org.junit.Assert.assertNotNull(booleanItor33);
        org.junit.Assert.assertTrue("'" + boolean34 + "' != '" + false + "'", !boolean34);
        org.junit.Assert.assertNotNull(booleanArray37);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + true + "'", boolean39);
        org.junit.Assert.assertTrue("'" + boolean41 + "' != '" + true + "'", boolean41);
        org.junit.Assert.assertNotNull(booleanStream42);
        org.junit.Assert.assertTrue("'" + boolean43 + "' != '" + false + "'", !boolean43);
        org.junit.Assert.assertTrue("'" + boolean46 + "' != '" + false + "'", !boolean46);
        org.junit.Assert.assertNotNull(uRIItor47);
        org.junit.Assert.assertTrue("'" + int52 + "' != '" + (-5) + "'", int52.equals((-5)));
        org.junit.Assert.assertTrue("'" + boolean53 + "' != '" + false + "'", !boolean53);
        org.junit.Assert.assertTrue("'" + str57 + "' != '" + "100.0" + "'", str57.equals("100.0"));
        org.junit.Assert.assertTrue("'" + str58 + "' != '" + "100.0" + "'", str58.equals("100.0"));
        org.junit.Assert.assertTrue("'" + boolean59 + "' != '" + true + "'", boolean59);
        org.junit.Assert.assertTrue("'" + long60 + "' != '" + 281474993487890L + "'", long60.equals(281474993487890L));
        org.junit.Assert.assertTrue("'" + int61 + "' != '" + 18 + "'", int61.equals(18));
        org.junit.Assert.assertTrue("'" + int62 + "' != '" + (-1) + "'", int62 == (-1));
        org.junit.Assert.assertTrue("'" + str63 + "' != '" + "100.0" + "'", str63.equals("100.0"));
        org.junit.Assert.assertTrue("'" + int64 + "' != '" + 18 + "'", int64.equals(18));
        org.junit.Assert.assertTrue("'" + long65 + "' != '" + 281474993487890L + "'", long65.equals(281474993487890L));
        org.junit.Assert.assertTrue("'" + long67 + "' != '" + 281475010265084L + "'", long67.equals(281475010265084L));
        org.junit.Assert.assertNotNull(strArray69);
        org.junit.Assert.assertTrue("'" + boolean71 + "' != '" + true + "'", boolean71);
        org.junit.Assert.assertTrue("'" + int72 + "' != '" + 1 + "'", int72 == 1);
        org.junit.Assert.assertNotNull(booleanArray75);
        org.junit.Assert.assertTrue("'" + boolean77 + "' != '" + true + "'", boolean77);
        org.junit.Assert.assertTrue("'" + boolean79 + "' != '" + true + "'", boolean79);
        org.junit.Assert.assertNotNull(booleanStream80);
        org.junit.Assert.assertTrue("'" + boolean81 + "' != '" + false + "'", !boolean81);
        org.junit.Assert.assertTrue("'" + boolean82 + "' != '" + false + "'", !boolean82);
        org.junit.Assert.assertNotNull(floatSpliterator83);
        org.junit.Assert.assertNotNull(throwableArray86);
        org.junit.Assert.assertTrue("'" + int87 + "' != '" + (-1) + "'", int87 == (-1));
        org.junit.Assert.assertTrue("'" + int89 + "' != '" + (-1) + "'", int89 == (-1));
        org.junit.Assert.assertNotNull(objArray91);
        org.junit.Assert.assertTrue("'" + boolean92 + "' != '" + false + "'", !boolean92);
    }

    @Test
    public void test652() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test652");
        esa.mo.nmf.MCServicesProviderNMF mCServicesProviderNMF0 = new esa.mo.nmf.MCServicesProviderNMF();
        esa.mo.mc.impl.provider.ParameterProviderServiceImpl parameterProviderServiceImpl1 = mCServicesProviderNMF0
            .getParameterService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl2 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl3 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl4 = mCServicesProviderNMF0
            .getAggregationService();
        esa.mo.mc.impl.provider.AlertProviderServiceImpl alertProviderServiceImpl5 = mCServicesProviderNMF0
            .getAlertService();
        esa.mo.mc.impl.provider.ActionProviderServiceImpl actionProviderServiceImpl6 = mCServicesProviderNMF0
            .getActionService();
        esa.mo.mc.impl.provider.AggregationProviderServiceImpl aggregationProviderServiceImpl7 = mCServicesProviderNMF0
            .getAggregationService();
        org.junit.Assert.assertNotNull(parameterProviderServiceImpl1);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl2);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl3);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl4);
        org.junit.Assert.assertNotNull(alertProviderServiceImpl5);
        org.junit.Assert.assertNotNull(actionProviderServiceImpl6);
        org.junit.Assert.assertNotNull(aggregationProviderServiceImpl7);
    }

    @Test
    public void test653() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test653");
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList0 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        java.util.ListIterator<java.lang.Double> doubleItor1 = doubleList0.listIterator();
        java.util.ListIterator<java.lang.Double> doubleItor2 = doubleList0.listIterator();
        org.ccsds.moims.mo.mal.structures.UOctet uOctet3 = doubleList0.getAreaVersion();
        org.ccsds.moims.mo.mal.structures.FloatList floatList4 = new org.ccsds.moims.mo.mal.structures.FloatList();
        java.lang.Long long5 = floatList4.getShortForm();
        floatList4.trimToSize();
        java.lang.String str7 = floatList4.toString();
        java.util.Spliterator<java.lang.Float> floatSpliterator8 = floatList4.spliterator();
        java.lang.Byte[] byteArray11 = new java.lang.Byte[]{(byte) 10, (byte) 100};
        java.util.ArrayList<java.lang.Byte> byteList12 = new java.util.ArrayList<java.lang.Byte>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<java.lang.Byte>) byteList12,
            byteArray11);
        boolean boolean14 = byteList12.isEmpty();
        int int16 = byteList12.indexOf((java.lang.Object) 'a');
        int int18 = byteList12.lastIndexOf((java.lang.Object) "hi!");
        java.lang.Boolean[] booleanArray20 = new java.lang.Boolean[]{true};
        java.util.ArrayList<java.lang.Boolean> booleanList21 = new java.util.ArrayList<java.lang.Boolean>();
        boolean boolean22 = java.util.Collections.addAll((java.util.Collection<java.lang.Boolean>) booleanList21,
            booleanArray20);
        java.util.ListIterator<java.lang.Boolean> booleanItor24 = booleanList21.listIterator((int) (short) 1);
        java.util.stream.Stream<java.lang.Boolean> booleanStream25 = booleanList21.parallelStream();
        boolean boolean27 = booleanList21.add((java.lang.Boolean) false);
        boolean boolean28 = byteList12.retainAll((java.util.Collection<java.lang.Boolean>) booleanList21);
        org.ccsds.moims.mo.mal.structures.UShortList uShortList29 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj30 = uShortList29.clone();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream31 = uShortList29
            .parallelStream();
        java.util.Iterator<org.ccsds.moims.mo.mal.structures.UShort> uShortItor32 = uShortList29.iterator();
        boolean boolean33 = byteList12.equals((java.lang.Object) uShortItor32);
        org.ccsds.moims.mo.mal.structures.ShortList shortList34 = new org.ccsds.moims.mo.mal.structures.ShortList();
        java.lang.Object obj35 = shortList34.clone();
        boolean boolean36 = byteList12.equals((java.lang.Object) shortList34);
        esa.mo.nmf.NMFException nMFException38 = new esa.mo.nmf.NMFException("-1");
        boolean boolean39 = byteList12.equals((java.lang.Object) nMFException38);
        byteList12.trimToSize();
        org.ccsds.moims.mo.mal.structures.BooleanList booleanList41 = new org.ccsds.moims.mo.mal.structures.BooleanList();
        org.ccsds.moims.mo.mal.structures.Element element42 = booleanList41.createElement();
        java.lang.Long long43 = booleanList41.getShortForm();
        org.ccsds.moims.mo.mal.structures.UShort uShort44 = booleanList41.getAreaNumber();
        java.lang.Integer int45 = booleanList41.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray46 = new org.ccsds.moims.mo.mal.structures.BooleanList[]{booleanList41};
        org.ccsds.moims.mo.mal.structures.BooleanList[] booleanListArray47 = byteList12.toArray(booleanListArray46);
        boolean boolean48 = floatList4.remove((java.lang.Object) booleanListArray46);
        boolean boolean49 = doubleList0.remove((java.lang.Object) booleanListArray46);
        java.util.Spliterator<java.lang.Double> doubleSpliterator50 = doubleList0.spliterator();
        org.ccsds.moims.mo.mal.structures.DoubleList doubleList51 = new org.ccsds.moims.mo.mal.structures.DoubleList();
        org.ccsds.moims.mo.mal.structures.Element element52 = doubleList51.createElement();
        org.ccsds.moims.mo.mal.structures.UShort uShort53 = doubleList51.getServiceNumber();
        doubleList51.ensureCapacity(3);
        doubleList51.trimToSize();
        org.ccsds.moims.mo.mal.structures.UShort uShort57 = doubleList51.getServiceNumber();
        org.ccsds.moims.mo.mal.structures.UShort uShort58 = doubleList51.getServiceNumber();
        java.util.Spliterator<java.lang.Double> doubleSpliterator59 = doubleList51.spliterator();
        boolean boolean60 = doubleList0.contains((java.lang.Object) doubleList51);
        org.junit.Assert.assertNotNull(doubleItor1);
        org.junit.Assert.assertNotNull(doubleItor2);
        org.junit.Assert.assertNotNull(uOctet3);
        org.junit.Assert.assertTrue("'" + long5 + "' != '" + 281475010265084L + "'", long5.equals(281475010265084L));
        org.junit.Assert.assertTrue("'" + str7 + "' != '" + "[]" + "'", str7.equals("[]"));
        org.junit.Assert.assertNotNull(floatSpliterator8);
        org.junit.Assert.assertNotNull(byteArray11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + false + "'", !boolean14);
        org.junit.Assert.assertTrue("'" + int16 + "' != '" + (-1) + "'", int16 == (-1));
        org.junit.Assert.assertTrue("'" + int18 + "' != '" + (-1) + "'", int18 == (-1));
        org.junit.Assert.assertNotNull(booleanArray20);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22);
        org.junit.Assert.assertNotNull(booleanItor24);
        org.junit.Assert.assertNotNull(booleanStream25);
        org.junit.Assert.assertTrue("'" + boolean27 + "' != '" + true + "'", boolean27);
        org.junit.Assert.assertTrue("'" + boolean28 + "' != '" + true + "'", boolean28);
        org.junit.Assert.assertNotNull(obj30);
        org.junit.Assert.assertNotNull(uShortStream31);
        org.junit.Assert.assertNotNull(uShortItor32);
        org.junit.Assert.assertTrue("'" + boolean33 + "' != '" + false + "'", !boolean33);
        org.junit.Assert.assertNotNull(obj35);
        org.junit.Assert.assertTrue("'" + boolean36 + "' != '" + true + "'", boolean36);
        org.junit.Assert.assertTrue("'" + boolean39 + "' != '" + false + "'", !boolean39);
        org.junit.Assert.assertNotNull(element42);
        org.junit.Assert.assertTrue("'" + long43 + "' != '" + 281475010265086L + "'", long43.equals(281475010265086L));
        org.junit.Assert.assertNotNull(uShort44);
        org.junit.Assert.assertTrue("'" + int45 + "' != '" + (-2) + "'", int45.equals((-2)));
        org.junit.Assert.assertNotNull(booleanListArray46);
        org.junit.Assert.assertNotNull(booleanListArray47);
        org.junit.Assert.assertTrue("'" + boolean48 + "' != '" + false + "'", !boolean48);
        org.junit.Assert.assertTrue("'" + boolean49 + "' != '" + false + "'", !boolean49);
        org.junit.Assert.assertNotNull(doubleSpliterator50);
        org.junit.Assert.assertNotNull(element52);
        org.junit.Assert.assertNotNull(uShort53);
        org.junit.Assert.assertNotNull(uShort57);
        org.junit.Assert.assertNotNull(uShort58);
        org.junit.Assert.assertNotNull(doubleSpliterator59);
        org.junit.Assert.assertTrue("'" + boolean60 + "' != '" + false + "'", !boolean60);
    }

    @Test
    public void test654() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest1.test654");
        org.ccsds.moims.mo.mal.structures.UShortList uShortList0 = new org.ccsds.moims.mo.mal.structures.UShortList();
        java.lang.Object obj1 = uShortList0.clone();
        org.ccsds.moims.mo.mal.structures.UShort uShort2 = uShortList0.getServiceNumber();
        java.lang.Integer int3 = uShortList0.getTypeShortForm();
        org.ccsds.moims.mo.mal.structures.UShortList uShortList4 = new org.ccsds.moims.mo.mal.structures.UShortList();
        org.ccsds.moims.mo.mal.structures.UShort uShort5 = uShortList4.getServiceNumber();
        java.lang.Integer int6 = uShortList4.getTypeShortForm();
        int int7 = uShortList0.lastIndexOf((java.lang.Object) uShortList4);
        org.ccsds.moims.mo.mal.structures.UOctet uOctet8 = uShortList0.getAreaVersion();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream9 = uShortList0.stream();
        java.util.stream.Stream<org.ccsds.moims.mo.mal.structures.UShort> uShortStream10 = uShortList0.stream();
        esa.mo.nmf.NMFException nMFException11 = new esa.mo.nmf.NMFException();
        esa.mo.nmf.NMFException nMFException12 = new esa.mo.nmf.NMFException();
        nMFException11.addSuppressed((java.lang.Throwable) nMFException12);
        java.lang.String str14 = nMFException11.toString();
        int int15 = uShortList0.indexOf((java.lang.Object) nMFException11);
        org.junit.Assert.assertNotNull(obj1);
        org.junit.Assert.assertNotNull(uShort2);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + (-10) + "'", int3.equals((-10)));
        org.junit.Assert.assertNotNull(uShort5);
        org.junit.Assert.assertTrue("'" + int6 + "' != '" + (-10) + "'", int6.equals((-10)));
        org.junit.Assert.assertTrue("'" + int7 + "' != '" + (-1) + "'", int7 == (-1));
        org.junit.Assert.assertNotNull(uOctet8);
        org.junit.Assert.assertNotNull(uShortStream9);
        org.junit.Assert.assertNotNull(uShortStream10);
        org.junit.Assert.assertTrue("'" + str14 + "' != '" + "esa.mo.nmf.NMFException" + "'", str14.equals(
            "esa.mo.nmf.NMFException"));
        org.junit.Assert.assertTrue("'" + int15 + "' != '" + (-1) + "'", int15 == (-1));
    }
}
