package esa.nmf.test;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegressionTest0 {

    public static boolean debug = false;

    @Test
    public void test001() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test001");
        esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter mCSoftwareSimulatorAdapter0 = new esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter();
        org.ccsds.moims.mo.mal.structures.Duration duration2 = new org.ccsds.moims.mo.mal.structures.Duration(1.0d);
        java.lang.Long[] longArray6 = new java.lang.Long[]{10L, 100L, 100L};
        java.util.ArrayList<java.lang.Long> longList7 = new java.util.ArrayList<java.lang.Long>();
        boolean boolean8 = java.util.Collections.addAll((java.util.Collection<java.lang.Long>) longList7, longArray6);
        int int9 = longList7.size();
        java.util.stream.Stream<java.lang.Long> longStream10 = longList7.parallelStream();
        boolean boolean11 = duration2.equals((java.lang.Object) longStream10);
        org.ccsds.moims.mo.mal.structures.UShort uShort12 = duration2.getServiceNumber();
        org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails parameterDefinitionDetails13 = null;
        try {
            org.ccsds.moims.mo.mc.parameter.structures.ParameterValue parameterValue14 = mCSoftwareSimulatorAdapter0
                .getValueWithCustomValidityState((org.ccsds.moims.mo.mal.structures.Attribute) uShort12,
                    parameterDefinitionDetails13);
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter.randoop_classUsedFlag from class esa.mo.nmf.MonitorAndControlNMFAdapter");
        } catch (java.lang.IllegalAccessError e) {
        }
        org.junit.Assert.assertNotNull(longArray6);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + true + "'", boolean8);
        org.junit.Assert.assertTrue("'" + int9 + "' != '" + 3 + "'", int9 == 3);
        org.junit.Assert.assertNotNull(longStream10);
        org.junit.Assert.assertTrue("'" + boolean11 + "' != '" + false + "'", !boolean11);
        org.junit.Assert.assertNotNull(uShort12);
    }

    @Test
    public void test002() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test002");
        esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter mCSoftwareSimulatorAdapter0 = new esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter();
        org.ccsds.moims.mo.mal.structures.Identifier identifier2 = new org.ccsds.moims.mo.mal.structures.Identifier(
            "[0, -1, -1, 10, 0]");
        try {
            boolean boolean3 = mCSoftwareSimulatorAdapter0.isReadOnly(identifier2);
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter.randoop_classUsedFlag from class esa.mo.nmf.MonitorAndControlNMFAdapter");
        } catch (java.lang.IllegalAccessError e) {
        }
    }

    @Test
    public void test003() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test003");
        esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter mCSoftwareSimulatorAdapter0 = new esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter();
        org.ccsds.moims.mo.mal.structures.Identifier identifier2 = new org.ccsds.moims.mo.mal.structures.Identifier(
            "1");
        java.lang.Integer int3 = identifier2.getTypeShortForm();
        java.lang.Integer[] intArray11 = new java.lang.Integer[]{0, (-1), (-1), 10, 0};
        java.util.ArrayList<java.lang.Integer> intList12 = new java.util.ArrayList<java.lang.Integer>();
        boolean boolean13 = java.util.Collections.addAll((java.util.Collection<java.lang.Integer>) intList12,
            intArray11);
        boolean boolean15 = intList12.contains((java.lang.Object) 0);
        intList12.ensureCapacity((int) (byte) 0);
        intList12.clear();
        int int19 = intList12.size();
        org.ccsds.moims.mo.mal.structures.FloatList floatList21 = new org.ccsds.moims.mo.mal.structures.FloatList(0);
        org.ccsds.moims.mo.mal.structures.Element element22 = floatList21.createElement();
        boolean boolean23 = intList12.contains((java.lang.Object) floatList21);
        java.lang.Object obj24 = intList12.clone();
        boolean boolean25 = identifier2.equals((java.lang.Object) intList12);
        try {
            java.lang.Integer int27 = intList12.remove((-9));
            org.junit.Assert.fail("Expected exception of type java.lang.IndexOutOfBoundsException; message: -9");
        } catch (java.lang.IndexOutOfBoundsException e) {
        }
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 6 + "'", int3.equals(6));
        org.junit.Assert.assertNotNull(intArray11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + true + "'", boolean13);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + true + "'", boolean15);
        org.junit.Assert.assertTrue("'" + int19 + "' != '" + 0 + "'", int19 == 0);
        org.junit.Assert.assertNotNull(element22);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", !boolean23);
        org.junit.Assert.assertNotNull(obj24);
        org.junit.Assert.assertTrue("'" + boolean25 + "' != '" + false + "'", !boolean25);
    }

    @Test
    public void test005() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test005");
        esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter mCSoftwareSimulatorAdapter0 = new esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter();
        org.ccsds.moims.mo.mal.structures.Identifier identifier2 = new org.ccsds.moims.mo.mal.structures.Identifier(
            "1");
        java.lang.Integer int3 = identifier2.getTypeShortForm();
        try {
            boolean boolean4 = mCSoftwareSimulatorAdapter0.isReadOnly(identifier2);
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter.randoop_classUsedFlag from class esa.mo.nmf.MonitorAndControlNMFAdapter");
        } catch (java.lang.IllegalAccessError e) {
        }
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 6 + "'", int3.equals(6));
    }

    @Test
    public void test008() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test008");
        esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter mCSoftwareSimulatorAdapter0 = new esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter();
        org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails actionDefinitionDetails1 = null;
        org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails actionInstanceDetails2 = null;
        org.ccsds.moims.mo.mal.structures.UIntegerList uIntegerList3 = null;
        try {
            boolean boolean4 = mCSoftwareSimulatorAdapter0.preCheck(actionDefinitionDetails1, actionInstanceDetails2,
                uIntegerList3);
            // flaky:             org.junit.Assert.fail("Expected exception of type java.lang.IllegalAccessError; message: tried to access field esa.mo.nmf.nanosatmosupervisor.MCSupervisorBasicAdapter.randoop_classUsedFlag from class esa.mo.nmf.MonitorAndControlNMFAdapter");
        } catch (java.lang.IllegalAccessError e) {
        }
    }

}
