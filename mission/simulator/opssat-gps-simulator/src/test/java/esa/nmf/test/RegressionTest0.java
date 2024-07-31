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
        java.math.BigDecimal bigDecimal2 = opssat.simulator.GPS.truncateDecimal((double) ' ', (int) ' ');
        org.junit.Assert.assertNotNull(bigDecimal2);
    }

    @Test
    public void test2() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test2");
        opssat.simulator.Vector vector3 = new opssat.simulator.Vector((double) (byte) 10, 0.0d, (double) (byte) 0);
        double double4 = vector3.z();
        double double5 = vector3.x();
        double double6 = vector3.x();
        double double7 = vector3.x();
        org.junit.Assert.assertTrue("'" + double4 + "' != '" + 0.0d + "'", double4 == 0.0d);
        org.junit.Assert.assertTrue("'" + double5 + "' != '" + 10.0d + "'", double5 == 10.0d);
        org.junit.Assert.assertTrue("'" + double6 + "' != '" + 10.0d + "'", double6 == 10.0d);
        org.junit.Assert.assertTrue("'" + double7 + "' != '" + 10.0d + "'", double7 == 10.0d);
    }

    @Test
    public void test3() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test3");
        opssat.simulator.InstrumentsSimulator instrumentsSimulator0 = new opssat.simulator.InstrumentsSimulator();
        instrumentsSimulator0.printRealPosition();
        int int2 = instrumentsSimulator0.getMode();
        org.junit.Assert.assertTrue("'" + int2 + "' != '" + 0 + "'", int2 == 0);
    }

    @Test
    public void test5() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test5");
        opssat.simulator.InstrumentsSimulator instrumentsSimulator0 = new opssat.simulator.InstrumentsSimulator();
        instrumentsSimulator0.printRealPosition();
        double double2 = instrumentsSimulator0.getFineADCSmagnetometerBtheta();
        // flaky:         org.junit.Assert.assertTrue("'" + double2 + "' != '" + 1.1814262328124097E-5d + "'", double2 == 1.1814262328124097E-5d);
    }

    @Test
    public void test6() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test6");
        opssat.simulator.Vector vector6 = new opssat.simulator.Vector((double) (byte) 10, 0.0d, (double) (byte) 0);
        double double7 = vector6.x();
        java.util.Date date8 = null;
        opssat.simulator.Orbit.OrbitParameters orbitParameters9 = new opssat.simulator.Orbit.OrbitParameters(
            (double) 'a', (double) 7, (double) 6, vector6, date8);
        org.junit.Assert.assertTrue("'" + double7 + "' != '" + 10.0d + "'", double7 == 10.0d);
    }
}
