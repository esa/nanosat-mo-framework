package esa.mo.mp.impl.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.ccsds.moims.mo.mp.structures.ArgDef;
import org.ccsds.moims.mo.mp.structures.ArgumentConstraint;
import org.ccsds.moims.mo.mp.structures.ComplexConstraint;
import org.ccsds.moims.mo.mp.structures.Constraint;
import org.ccsds.moims.mo.mp.structures.DurationConstraint;
import org.ccsds.moims.mo.mp.structures.StringArgDef;
import org.ccsds.moims.mo.mp.structures.c_ArgDef;
import org.ccsds.moims.mo.mp.structures.c_ArgDefList;
import org.ccsds.moims.mo.mp.structures.c_Constraint;
import org.ccsds.moims.mo.mp.structures.c_ConstraintList;
import org.junit.Test;

/**
 * TestMPPolyFix contains unit tests for TestMPPoly public worker methods
 */
public class TestMPPolyFix {

    @Test
    public void testEncodeConstraint() {
        ComplexConstraint complexConstraint = new ComplexConstraint();
        c_Constraint constraint = MPPolyFix.encodeConstraint(complexConstraint);
        assertNotNull(constraint);
        assertNull(constraint.getConstraintNode());
        assertNotNull(constraint.getConditionalConstraint());
        assertNull(constraint.getEffect());
        assertNotNull(constraint.getConditionalConstraint().getComplexConstraint());
    }

    @Test
    public void testEncodeConstraints() {
        List<Constraint> constraints = new ArrayList<>();
        constraints.add(new DurationConstraint());
        constraints.add(new ArgumentConstraint());
        c_ConstraintList constraintList = MPPolyFix.encodeConstraints(constraints);
        assertNotNull(constraintList);
        assertEquals(2, constraintList.size());
        assertNotNull(constraintList.get(0).getConditionalConstraint());
        assertNotNull(constraintList.get(1).getConditionalConstraint());
    }

    @Test
    public void testEncode() {
        c_ArgDef argDef = (c_ArgDef) MPPolyFix.encode(new StringArgDef(), c_ArgDef.class);
        assertNotNull(argDef);
        assertNull(argDef.getNumericArgDef());
        assertNotNull(argDef.getStringArgDef());
        assertNull(argDef.getStatusArgDef());
    }

    @Test
    public void testListEncode() {
        List<ArgDef> list = new ArrayList<>();
        list.add(new StringArgDef());

        c_ArgDefList argDefs = (c_ArgDefList) MPPolyFix.encode(list, c_ArgDefList.class);

        assertNotNull(argDefs);
        assertEquals(1, argDefs.size());
        c_ArgDef argDef = argDefs.get(0);
        assertNull(argDef.getNumericArgDef());
        assertNotNull(argDef.getStringArgDef());
        assertNull(argDef.getStatusArgDef());
    }

    @Test
    public void testDecode() {
        c_ArgDef argDef = new c_ArgDef();
        argDef.setStringArgDef(new StringArgDef());

        StringArgDef stringDef = (StringArgDef) MPPolyFix.decode(argDef);
        assertNotNull(stringDef);
        assertEquals(new StringArgDef(), stringDef);
    }

    @Test
    public void testListDecode() {
        c_ArgDef argDef = new c_ArgDef();
        argDef.setStringArgDef(new StringArgDef());
        c_ArgDefList list = new c_ArgDefList();
        list.add(argDef);

        List<ArgDef> argDefs = (List<ArgDef>) MPPolyFix.decode(list);

        assertNotNull(argDefs);
        assertEquals(1, argDefs.size());
        assertEquals(new StringArgDef(), argDefs.get(0));
    }

    @Test
    public void testListDecodeEmpty() {
        c_ArgDefList list = new c_ArgDefList();
        List<ArgDef> argDefs = (List<ArgDef>) MPPolyFix.decode(list);

        assertNotNull(argDefs);
        assertTrue(argDefs.isEmpty());
    }
}
