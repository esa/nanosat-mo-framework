package org.ccsds.moims.mo.test.com;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.ccsds.moims.mo.com.COMObject;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.junit.Before;
import org.junit.Test;

public class TestCOMService {

    private COMService uut;

    @Before
    public void setup() {
        uut = new COMService(new UShort(42), new Identifier("Testservice"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddCOMObject1() {
        uut.addCOMObject(null);
    }

    @Test
    public void testAddCOMObject2() {
        COMObject input = new COMObject(new ObjectType(new UShort(1), new UShort(2), new UOctet((byte) 3), new UShort(
            4)), new Identifier("Test2"), new Integer(42), false, null, false, null, false);
        uut.addCOMObject(input);
        assertEquals(1, uut.getObjects().length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCOMObjectByName1() {
        uut.getObjectByName(null);
    }

    @Test
    public void testGetCOMObjectByName2() {
        COMObject res = uut.getObjectByName(new Identifier(null));
        assertNull(res);
    }

    @Test
    public void testGetCOMObjectByName3() {
        COMObject input = new COMObject(new ObjectType(new UShort(1), new UShort(2), new UOctet((byte) 3), new UShort(
            4)), new Identifier("Test3"), new Integer(42), false, null, false, null, false);
        uut.addCOMObject(input);
        COMObject res = uut.getObjectByName(new Identifier("Test3"));
        assertEquals("Test3", res.getObjectName().getValue());
        assertEquals(1, res.getObjectType().getArea().getValue());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetObjectByNumber1() {
        uut.getObjectByNumber(null);
    }

    @Test
    public void testGetObjectByNumber2() {
        COMObject input = new COMObject(new ObjectType(new UShort(1), new UShort(2), new UOctet((byte) 3), new UShort(
            4)), new Identifier("Test2"), new Integer(1337), false, null, false, null, false);
        uut.addCOMObject(input);
        COMObject res = uut.getObjectByNumber(new UShort(4));
        assertEquals(1337, res.getBodyShortForm());
    }

    @Test
    public void testGetObjects() {
        COMObject input = new COMObject(new ObjectType(new UShort(1), new UShort(2), new UOctet((byte) 3), new UShort(
            1)), new Identifier("Test1"), new Integer(42), false, null, false, null, false);
        uut.addCOMObject(input);
        COMObject input2 = new COMObject(new ObjectType(new UShort(1), new UShort(2), new UOctet((byte) 3), new UShort(
            2)), new Identifier("Test2"), new Integer(42), false, null, false, null, false);
        uut.addCOMObject(input2);
        COMObject[] res = uut.getObjects();
        assertEquals(2, res.length);
    }
}
