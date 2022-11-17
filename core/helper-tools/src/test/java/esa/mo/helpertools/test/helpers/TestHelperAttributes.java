package esa.mo.helpertools.test.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigInteger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.DoubleList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.FloatList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.OctetList;
import org.ccsds.moims.mo.mal.structures.ShortList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.junit.Test;

import esa.mo.helpertools.helpers.HelperAttributes;

public class TestHelperAttributes {

    /* Tests for attribute2double */

    @Test
    public void testAttribute2Double1() {
        Union attribute = new Union(false);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double2() {
        Union attribute = new Union(true);
        assertEquals(1.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double3() {
        Union attribute = new Union(0f);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double4() {
        Union attribute = new Union(42.0f);
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double5() {
        Union attribute = new Union(Float.MAX_VALUE);
        assertEquals((double) Float.MAX_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double6() {
        Union attribute = new Union(Float.MIN_VALUE);
        assertEquals((double) Float.MIN_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double7() {
        Union attribute = new Union(0);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double8() {
        Union attribute = new Union(42);
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double9() {
        Union attribute = new Union(Integer.MAX_VALUE);
        assertEquals((double) Integer.MAX_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double10() {
        Union attribute = new Union(Integer.MIN_VALUE);
        assertEquals((double) Integer.MIN_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double11() {
        Union attribute = new Union(0.0);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double12() {
        Union attribute = new Union(42.0);
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double13() {
        Union attribute = new Union(Double.MAX_VALUE);
        assertEquals(Double.MAX_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double14() {
        Union attribute = new Union(Double.MIN_VALUE);
        assertEquals(Double.MIN_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double15() {
        Union attribute = new Union(0L);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double16() {
        Union attribute = new Union(42L);
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double17() {
        Union attribute = new Union(Long.MAX_VALUE);
        assertEquals((double) Long.MAX_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double18() {
        Union attribute = new Union(Long.MIN_VALUE);
        assertEquals((double) Long.MIN_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double19() {
        Union attribute = new Union((short) 0);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double20() {
        Union attribute = new Union((short) 42);
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double21() {
        Union attribute = new Union(Short.MAX_VALUE);
        assertEquals((double) Short.MAX_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double22() {
        Union attribute = new Union(Short.MIN_VALUE);
        assertEquals((double) Short.MIN_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double23() {
        Union attribute = new Union((byte) 0);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double24() {
        Union attribute = new Union((byte) 42.0);
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double25() {
        Union attribute = new Union(Byte.MAX_VALUE);
        assertEquals((double) Byte.MAX_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double26() {
        Union attribute = new Union(Byte.MIN_VALUE);
        assertEquals((double) Byte.MIN_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double27() {
        Union attribute = new Union("ABCD");
        assertEquals(null, HelperAttributes.attribute2double(attribute));
    }

    @Test
    public void testAttribute2Double28() {
        Union attribute = new Union("123456789.01234567");
        assertEquals(123456789.01234567, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double29() {
        Union attribute = new Union("42.0");
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double31() {
        Duration attribute = new Duration(0);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double32() {
        Duration attribute = new Duration(42.0);
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double33() {
        Identifier attribute = new Identifier("0");
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double34() {
        Identifier attribute = new Identifier("ABCDEFG");
        assertEquals(null, HelperAttributes.attribute2double(attribute));
    }

    @Test
    public void testAttribute2Double35() {
        Identifier attribute = new Identifier("1337");
        assertEquals(1337.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double36() {
        Identifier attribute = new Identifier("1337.42");
        assertEquals(1337.42, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double37() {
        UOctet attribute = new UOctet((short) 0);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double38() {
        UOctet attribute = new UOctet((short) 255);
        assertEquals(255.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double39() {
        UOctet attribute = new UOctet((short) 42);
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double41() {
        UShort attribute = new UShort(0);
        assertEquals(0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double42() {
        UShort attribute = new UShort(UShort.MAX_VALUE);
        assertEquals(65535.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double43() {
        UShort attribute = new UShort(12345);
        assertEquals(12345.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double44() {
        UInteger attribute = new UInteger(0);
        assertEquals(0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double45() {
        UInteger attribute = new UInteger(UInteger.MAX_VALUE);
        assertEquals(4294967295.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double46() {
        UInteger attribute = new UInteger(4563183);
        assertEquals(4563183.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double47() {
        ULong attribute = new ULong(new BigInteger("0"));
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double48() {
        ULong attribute = new ULong(ULong.MAX_VALUE);
        assertEquals(18446744073709551615.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double49() {
        ULong attribute = new ULong(new BigInteger("42"));
        assertEquals(42.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double50() {
        Time attribute = new Time(0L);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double51() {
        Time attribute = new Time(Long.MAX_VALUE);
        assertEquals((double) Long.MAX_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double52() {
        FineTime attribute = new FineTime(0L);
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double53() {
        FineTime attribute = new FineTime(Long.MAX_VALUE);
        assertEquals((double) Long.MAX_VALUE, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double54() {
        URI attribute = new URI("0");
        assertEquals(0.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double55() {
        URI attribute = new URI("1234567893548");
        assertEquals(1234567893548.0, (double) HelperAttributes.attribute2double(attribute), 0.0000001);
    }

    @Test
    public void testAttribute2Double56() {
        URI attribute = new URI("XZYWERFD");
        assertEquals(null, HelperAttributes.attribute2double(attribute));
    }

    /* Tests for attribute2string */

    @Test
    public void testAttribute2String1() {
        assertEquals("null", HelperAttributes.attribute2string(null));
    }

    @Test
    public void testAttribute2String2() {
        assertEquals("", HelperAttributes.attribute2string(new Object()));
    }

    @Test
    public void testAttribute2String3() {
        Union attribute = new Union(42.0);
        assertEquals("42.0", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String4() {
        Union attribute = new Union((Double) null);
        assertEquals("", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String5() {
        Union attribute = new Union(true);
        assertEquals("true", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String6() {
        Union attribute = new Union((Boolean) null);
        assertEquals("", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String7() {
        Union attribute = new Union(false);
        assertEquals("false", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String8() {
        Union attribute = new Union(42.0f);
        assertEquals("42.0", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String9() {
        Union attribute = new Union((Float) null);
        assertEquals("", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String10() {
        Union attribute = new Union(42);
        assertEquals("42", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String11() {
        Union attribute = new Union((Integer) null);
        assertEquals("", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String12() {
        Union attribute = new Union(Long.MAX_VALUE);
        assertEquals("9223372036854775807", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String13() {
        Union attribute = new Union((Long) null);
        assertEquals("", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String14() {
        Union attribute = new Union((short) 1337);
        assertEquals("1337", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String15() {
        Union attribute = new Union((Short) null);
        assertEquals("", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String16() {
        Union attribute = new Union((byte) 123);
        assertEquals("123", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String17() {
        Union attribute = new Union((Byte) null);
        assertEquals("", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String18() {
        Union attribute = new Union("I am a string!");
        assertEquals("I am a string!", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String19() {
        Union attribute = new Union((String) null);
        assertEquals("", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String20() {
        Duration attribute = new Duration(0.0);
        assertEquals("0.0", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String21() {
        UOctet attribute = new UOctet((short) 123);
        assertEquals("123", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String22() {
        UShort attribute = new UShort(12345);
        assertEquals("12345", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String23() {
        UInteger attribute = new UInteger(1234567890);
        assertEquals("1234567890", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String24() {
        Blob attribute = new Blob();
        assertEquals("null", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String25() {
        Blob attribute = new Blob("Teststring".getBytes());
        assertEquals("[84, 101, 115, 116, 115, 116, 114, 105, 110, 103]", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String26() {
        ULong attribute = new ULong(new BigInteger("12345678909876"));
        assertEquals("12345678909876", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String27() {
        Time attribute = new Time(12345678L);
        assertEquals("12345678", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String28() {
        Identifier attribute = new Identifier("TheName");
        assertEquals("TheName", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String29() {
        FineTime attribute = new FineTime(1234567890876543223L);
        assertEquals("1234567890876543223", HelperAttributes.attribute2string(attribute));
    }

    @Test
    public void testAttribute2String30() {
        URI attribute = new URI("http://www.esa.int/");
        assertEquals("http://www.esa.int/", HelperAttributes.attribute2string(attribute));
    }

    /* Tests for attributeName2object */

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeName2Object1() {
        HelperAttributes.attributeName2object(null);
    }

    @Test
    public void testAttributeName2Object2() {
        Object o = HelperAttributes.attributeName2object("Blob");
        assertNotEquals(null, o);
        assertTrue(o instanceof Blob);
    }

    @Test
    public void testAttributeName2Object3() {
        Object o = HelperAttributes.attributeName2object("Boolean");
        assertNotEquals(null, o);
        assertTrue(o instanceof Boolean);
    }

    @Test
    public void testAttributeName2Object4() {
        Object o = HelperAttributes.attributeName2object("Duration");
        assertNotEquals(null, o);
        assertTrue(o instanceof Duration);
    }

    @Test
    public void testAttributeName2Object5() {
        Object o = HelperAttributes.attributeName2object("Float");
        assertNotEquals(null, o);
        assertTrue(o instanceof Float);
    }

    @Test
    public void testAttributeName2Object6() {
        Object o = HelperAttributes.attributeName2object("Double");
        assertNotEquals(null, o);
        assertTrue(o instanceof Double);
    }

    @Test
    public void testAttributeName2Object7() {
        Object o = HelperAttributes.attributeName2object("Identifier");
        assertNotEquals(null, o);
        assertTrue(o instanceof Identifier);
    }

    @Test
    public void testAttributeName2Object8() {
        Object o = HelperAttributes.attributeName2object("Octet");
        assertNotEquals(null, o);
        assertTrue(o instanceof Byte);
    }

    @Test
    public void testAttributeName2Object9() {
        Object o = HelperAttributes.attributeName2object("UOctet");
        assertNotEquals(null, o);
        assertTrue(o instanceof UOctet);
    }

    @Test
    public void testAttributeName2Object10() {
        Object o = HelperAttributes.attributeName2object("Short");
        assertNotEquals(null, o);
        assertTrue(o instanceof Short);
    }

    @Test
    public void testAttributeName2Object11() {
        Object o = HelperAttributes.attributeName2object("UShort");
        assertNotEquals(null, o);
        assertTrue(o instanceof UShort);
    }

    @Test
    public void testAttributeName2Object12() {
        Object o = HelperAttributes.attributeName2object("Integer");
        assertNotEquals(null, o);
        assertTrue(o instanceof Integer);
    }

    @Test
    public void testAttributeName2Object13() {
        Object o = HelperAttributes.attributeName2object("UInteger");
        assertNotEquals(null, o);
        assertTrue(o instanceof UInteger);
    }

    @Test
    public void testAttributeName2Object14() {
        Object o = HelperAttributes.attributeName2object("Long");
        assertNotEquals(null, o);
        assertTrue(o instanceof Long);
    }

    @Test
    public void testAttributeName2Object15() {
        Object o = HelperAttributes.attributeName2object("ULong");
        assertNotEquals(null, o);
        assertTrue(o instanceof ULong);
    }

    @Test
    public void testAttributeName2Object16() {
        Object o = HelperAttributes.attributeName2object("String");
        assertNotEquals(null, o);
        assertTrue(o instanceof String);
    }

    @Test
    public void testAttributeName2Object17() {
        Object o = HelperAttributes.attributeName2object("Time");
        assertNotEquals(null, o);
        assertTrue(o instanceof Time);
    }

    @Test
    public void testAttributeName2Object18() {
        Object o = HelperAttributes.attributeName2object("FineTime");
        assertNotEquals(null, o);
        assertTrue(o instanceof FineTime);
    }

    @Test
    public void testAttributeName2Object19() {
        Object o = HelperAttributes.attributeName2object("URI");
        assertNotEquals(null, o);
        assertTrue(o instanceof URI);
    }

    @Test
    public void testAttributeName2Object20() {
        Object o = HelperAttributes.attributeName2object("SerializedObject");
        assertNotEquals(null, o);
        assertTrue(o instanceof Blob);
    }

    @Test
    public void testAttributeName2Object21() {
        Object o = HelperAttributes.attributeName2object("FooBar");
        assertEquals(null, o);
    }

    /* Tests for string2attribute */

    @Test(expected = IllegalArgumentException.class)
    public void testString2Attribute2() {
        HelperAttributes.string2attribute(new Union(42), null);
    }

    @Test
    public void testString2Attribute3() {
        Union attribute = new Union(42.0);
        Union result = (Union) HelperAttributes.string2attribute(attribute, "1337.0");
        assertEquals(Union._DOUBLE_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(1337.0, result.getDoubleValue(), 0.000001);
    }

    @Test
    public void testString2Attribute4() {
        Union attribute = new Union(true);
        Union result = (Union) HelperAttributes.string2attribute(attribute, "false");
        assertEquals(Union._BOOLEAN_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(false, result.getBooleanValue());
    }

    @Test
    public void testString2Attribute5() {
        Union attribute = new Union(false);
        Union result = (Union) HelperAttributes.string2attribute(attribute, "true");
        assertEquals(Union._BOOLEAN_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(true, result.getBooleanValue());
    }

    @Test
    public void testString2Attribute6() {
        Union attribute = new Union(0f);
        Union result = (Union) HelperAttributes.string2attribute(attribute, "42.0f");
        assertEquals(Union._FLOAT_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(42.0f, (float) result.getFloatValue(), 0.000001f);
    }

    @Test
    public void testString2Attribute7() {
        Union attribute = new Union(0);
        Union result = (Union) HelperAttributes.string2attribute(attribute, "1337");
        assertEquals(Union._INTEGER_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(1337, (int) result.getIntegerValue());
    }

    @Test
    public void testString2Attribute8() {
        Union attribute = new Union(0L);
        Union result = (Union) HelperAttributes.string2attribute(attribute, "123456789");
        assertEquals(Union._LONG_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(123456789L, (long) result.getLongValue());
    }

    @Test
    public void testString2Attribute9() {
        Union attribute = new Union((byte) 123);
        Union result = (Union) HelperAttributes.string2attribute(attribute, "42");
        assertEquals(Union._OCTET_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(42, (byte) result.getOctetValue());
    }

    @Test
    public void testString2Attribute10() {
        Union attribute = new Union((short) 4321);
        Union result = (Union) HelperAttributes.string2attribute(attribute, "9876");
        assertEquals(Union._SHORT_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(9876, (short) result.getShortValue());
    }

    @Test
    public void testString2Attribute11() {
        Duration attribute = new Duration();
        Duration result = (Duration) HelperAttributes.string2attribute(attribute, "321.123");
        assertTrue(result instanceof Duration);
        assertEquals(321.123, result.getValue(), 0.000001);
    }

    @Test
    public void testString2Attribute12() {
        UOctet attribute = new UOctet();
        UOctet result = (UOctet) HelperAttributes.string2attribute(attribute, "255");
        assertTrue(result instanceof UOctet);
        assertEquals(255, result.getValue());
    }

    @Test
    public void testString2Attribute13() {
        UShort attribute = new UShort();
        UShort result = (UShort) HelperAttributes.string2attribute(attribute, "12345");
        assertTrue(result instanceof UShort);
        assertEquals(12345, result.getValue());
    }

    @Test
    public void testString2Attribute14() {
        UInteger attribute = new UInteger();
        UInteger result = (UInteger) HelperAttributes.string2attribute(attribute, "41234562");
        assertTrue(result instanceof UInteger);
        assertEquals(41234562, result.getValue());
    }

    @Test
    public void testString2Attribute15() {
        ULong attribute = new ULong();
        ULong result = (ULong) HelperAttributes.string2attribute(attribute, "12345678987654323456");
        assertTrue(result instanceof ULong);
        assertEquals(new BigInteger("12345678987654323456"), result.getValue());
    }

    @Test
    public void testString2Attribute16() {
        Time attribute = new Time();
        Time result = (Time) HelperAttributes.string2attribute(attribute, "12345643");
        assertTrue(result instanceof Time);
        assertEquals(12345643L, result.getValue());
    }

    @Test
    public void testString2Attribute17() {
        FineTime attribute = new FineTime();
        FineTime result = (FineTime) HelperAttributes.string2attribute(attribute, "1234567876");
        assertTrue(result instanceof FineTime);
        assertEquals(1234567876L, result.getValue());
    }

    @Test
    public void testString2Attribute18() {
        Identifier attribute = new Identifier();
        Identifier result = (Identifier) HelperAttributes.string2attribute(attribute, "I am root");
        assertTrue(result instanceof Identifier);
        assertEquals("I am root", result.getValue());
    }

    @Test
    public void testString2Attribute19() {
        URI attribute = new URI();
        URI result = (URI) HelperAttributes.string2attribute(attribute, "http://www.esa.int/");
        assertTrue(result instanceof URI);
        assertEquals("http://www.esa.int/", result.getValue());
    }

    @Test
    public void testString2Attribute20() {
        Long attribute = 0L;
        Long result = (Long) HelperAttributes.string2attribute(attribute, "133742");
        assertTrue(result instanceof Long);
        assertEquals(133742L, (long) result);
    }

    @Test
    public void testString2Attribute21() {
        Boolean b = Boolean.FALSE;
        Boolean result = (Boolean) HelperAttributes.string2attribute(b, "true");
        assertTrue(result instanceof Boolean);
        assertEquals(true, result);
    }

    @Test
    public void testString2Attribute22() {
        assertEquals(null, HelperAttributes.string2attribute(new UShort(1235), ""));
    }

    /* Tests for javaType2Attribute */

    @Test
    public void testJavaType2Attribute1() {
        assertEquals(null, HelperAttributes.javaType2Attribute(null));
    }

    @Test
    public void testJavaType2Attribute2() {
        Object res = HelperAttributes.javaType2Attribute(true);
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._BOOLEAN_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(true, result.getBooleanValue());
    }

    @Test
    public void testJavaType2Attribute3() {
        Object res = HelperAttributes.javaType2Attribute(false);
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._BOOLEAN_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(false, result.getBooleanValue());
    }

    @Test
    public void testJavaType2Attribute4() {
        Object res = HelperAttributes.javaType2Attribute(42);
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._INTEGER_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(42, (int) result.getIntegerValue());
    }

    @Test
    public void testJavaType2Attribute5() {
        Object res = HelperAttributes.javaType2Attribute(1337429874L);
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._LONG_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(1337429874L, (long) result.getLongValue());
    }

    @Test
    public void testJavaType2Attribute6() {
        Object res = HelperAttributes.javaType2Attribute("I am a test.");
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._STRING_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals("I am a test.", result.getStringValue());
    }

    @Test
    public void testJavaType2Attribute7() {
        Object res = HelperAttributes.javaType2Attribute(42.1337);
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._DOUBLE_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(42.1337, result.getDoubleValue(), 0.000001);
    }

    @Test
    public void testJavaType2Attribute8() {
        Object res = HelperAttributes.javaType2Attribute(1337.42f);
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._FLOAT_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(1337.42f, result.getFloatValue(), 0.000001);
    }

    @Test
    public void testJavaType2Attribute9() {
        Object res = HelperAttributes.javaType2Attribute((byte) 123);
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._OCTET_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(123, (byte) result.getOctetValue());
    }

    @Test
    public void testJavaType2Attribute10() {
        Object res = HelperAttributes.javaType2Attribute((short) 12345);
        assertTrue(res instanceof Union);
        Union result = (Union) res;
        assertEquals(Union._SHORT_TYPE_SHORT_FORM, (int) result.getTypeShortForm());
        assertEquals(12345, (short) result.getShortValue());
    }

    /* Tests for attribute2JavaType */

    @Test
    public void testAttribute2JavaType1() {
        assertEquals(null, HelperAttributes.attribute2JavaType(null));
    }

    @Test
    public void testAttribute2JavaType2() {
        Union attribute = new Union(true);
        assertEquals(true, HelperAttributes.attribute2JavaType(attribute));
    }

    @Test
    public void testAttribute2JavaType3() {
        Union attribute = new Union(false);
        assertEquals(false, HelperAttributes.attribute2JavaType(attribute));
    }

    @Test
    public void testAttribute2JavaType4() {
        Union attribute = new Union(42);
        assertEquals(42, HelperAttributes.attribute2JavaType(attribute));
    }

    @Test
    public void testAttribute2JavaType5() {
        Union attribute = new Union(133742247331L);
        assertEquals(133742247331L, HelperAttributes.attribute2JavaType(attribute));
    }

    @Test
    public void testAttribute2JavaType6() {
        Union attribute = new Union((short) 42);
        assertEquals((short) 42, HelperAttributes.attribute2JavaType(attribute));
    }

    @Test
    public void testAttribute2JavaType7() {
        Union attribute = new Union((byte) 42);
        assertEquals((byte) 42, HelperAttributes.attribute2JavaType(attribute));
    }

    @Test
    public void testAttribute2JavaType8() {
        Union attribute = new Union(42.0f);
        assertEquals(42.0f, HelperAttributes.attribute2JavaType(attribute));
    }

    @Test
    public void testAttribute2JavaType9() {
        Union attribute = new Union(42.0);
        assertEquals(42.0, HelperAttributes.attribute2JavaType(attribute));
    }

    @Test
    public void testAttribute2JavaType10() {
        Union attribute = new Union("I am a string now.");
        assertEquals("I am a string now.", HelperAttributes.attribute2JavaType(attribute));
    }

    /* Tests for generateElementListFromJavaType */

    @Test
    public void testGenerateElementListFromJavaType1() {
        assertEquals(null, HelperAttributes.generateElementListFromJavaType(null));
    }

    @Test
    public void testGenerateElementListFromJavaType2() {
        assertEquals(null, HelperAttributes.generateElementListFromJavaType(new Time()));
    }

    @Test
    public void testGenerateElementListFromJavaType3() {
        assertTrue(HelperAttributes.generateElementListFromJavaType(true) instanceof BooleanList);
    }

    @Test
    public void testGenerateElementListFromJavaType4() {
        assertTrue(HelperAttributes.generateElementListFromJavaType(42) instanceof IntegerList);
    }

    @Test
    public void testGenerateElementListFromJavaType5() {
        assertTrue(HelperAttributes.generateElementListFromJavaType((short) 1337) instanceof ShortList);
    }

    @Test
    public void testGenerateElementListFromJavaType6() {
        assertTrue(HelperAttributes.generateElementListFromJavaType((byte) 127) instanceof OctetList);
    }

    @Test
    public void testGenerateElementListFromJavaType7() {
        assertTrue(HelperAttributes.generateElementListFromJavaType(127.0f) instanceof FloatList);
    }

    @Test
    public void testGenerateElementListFromJavaType8() {
        assertTrue(HelperAttributes.generateElementListFromJavaType(12742.0) instanceof DoubleList);
    }

    @Test
    public void testGenerateElementListFromJavaType9() {
        assertTrue(HelperAttributes.generateElementListFromJavaType(12345678903L) instanceof LongList);
    }

    @Test
    public void testGenerateElementListFromJavaType10() {
        assertTrue(HelperAttributes.generateElementListFromJavaType("You know what I am!") instanceof StringList);
    }

    /* Tests for typeShortForm2attributeName */

    @Test
    public void testTypeShortForm2attributeName1() {
        assertEquals("", HelperAttributes.typeShortForm2attributeName(42));
    }

    @Test
    public void testTypeShortForm2attributeName2() {
        assertEquals("Blob", HelperAttributes.typeShortForm2attributeName(1));
    }

    @Test
    public void testTypeShortForm2attributeName3() {
        assertEquals("Boolean", HelperAttributes.typeShortForm2attributeName(2));
    }

    @Test
    public void testTypeShortForm2attributeName4() {
        assertEquals("Duration", HelperAttributes.typeShortForm2attributeName(3));
    }

    @Test
    public void testTypeShortForm2attributeName5() {
        assertEquals("Float", HelperAttributes.typeShortForm2attributeName(4));
    }

    @Test
    public void testTypeShortForm2attributeName6() {
        assertEquals("Double", HelperAttributes.typeShortForm2attributeName(5));
    }

    @Test
    public void testTypeShortForm2attributeName7() {
        assertEquals("Identifier", HelperAttributes.typeShortForm2attributeName(6));
    }

    @Test
    public void testTypeShortForm2attributeName8() {
        assertEquals("Octet", HelperAttributes.typeShortForm2attributeName(7));
    }

    @Test
    public void testTypeShortForm2attributeName9() {
        assertEquals("UOctet", HelperAttributes.typeShortForm2attributeName(8));
    }

    @Test
    public void testTypeShortForm2attributeName10() {
        assertEquals("Short", HelperAttributes.typeShortForm2attributeName(9));
    }

    @Test
    public void testTypeShortForm2attributeName11() {
        assertEquals("UShort", HelperAttributes.typeShortForm2attributeName(10));
    }

    @Test
    public void testTypeShortForm2attributeName12() {
        assertEquals("Integer", HelperAttributes.typeShortForm2attributeName(11));
    }

    @Test
    public void testTypeShortForm2attributeName13() {
        assertEquals("UInteger", HelperAttributes.typeShortForm2attributeName(12));
    }

    @Test
    public void testTypeShortForm2attributeName14() {
        assertEquals("Long", HelperAttributes.typeShortForm2attributeName(13));
    }

    @Test
    public void testTypeShortForm2attributeName15() {
        assertEquals("ULong", HelperAttributes.typeShortForm2attributeName(14));
    }

    @Test
    public void testTypeShortForm2attributeName16() {
        assertEquals("String", HelperAttributes.typeShortForm2attributeName(15));
    }

    @Test
    public void testTypeShortForm2attributeName17() {
        assertEquals("Time", HelperAttributes.typeShortForm2attributeName(16));
    }

    @Test
    public void testTypeShortForm2attributeName18() {
        assertEquals("FineTime", HelperAttributes.typeShortForm2attributeName(17));
    }

    @Test
    public void testTypeShortForm2attributeName19() {
        assertEquals("URI", HelperAttributes.typeShortForm2attributeName(18));
    }

    @Test
    public void testTypeShortForm2attributeName20() {
        assertEquals("SerializedObject", HelperAttributes.typeShortForm2attributeName(127));
    }

    /* Tests for attributeName2typeShortForm */

    @Test(expected = IllegalArgumentException.class)
    public void testAttributeName2TypeShortForm1() {
        HelperAttributes.attributeName2typeShortForm(null);
    }

    @Test
    public void testAttributeName2TypeShortForm2() {
        assertEquals(null, HelperAttributes.attributeName2typeShortForm(""));
    }

    @Test
    public void testAttributeName2TypeShortForm3() {
        assertEquals(null, HelperAttributes.attributeName2typeShortForm("Invalid attributename"));
    }

    @Test
    public void testAttributeName2TypeShortForm4() {
        assertEquals(1, (int) HelperAttributes.attributeName2typeShortForm("Blob"));
    }

    @Test
    public void testAttributeName2TypeShortForm5() {
        assertEquals(2, (int) HelperAttributes.attributeName2typeShortForm("Boolean"));
    }

    @Test
    public void testAttributeName2TypeShortForm6() {
        assertEquals(3, (int) HelperAttributes.attributeName2typeShortForm("Duration"));
    }

    @Test
    public void testAttributeName2TypeShortForm7() {
        assertEquals(4, (int) HelperAttributes.attributeName2typeShortForm("Float"));
    }

    @Test
    public void testAttributeName2TypeShortForm8() {
        assertEquals(5, (int) HelperAttributes.attributeName2typeShortForm("Double"));
    }

    @Test
    public void testAttributeName2TypeShortForm9() {
        assertEquals(6, (int) HelperAttributes.attributeName2typeShortForm("Identifier"));
    }

    @Test
    public void testAttributeName2TypeShortForm10() {
        assertEquals(7, (int) HelperAttributes.attributeName2typeShortForm("Octet"));
    }

    @Test
    public void testAttributeName2TypeShortForm11() {
        assertEquals(8, (int) HelperAttributes.attributeName2typeShortForm("UOctet"));
    }

    @Test
    public void testAttributeName2TypeShortForm12() {
        assertEquals(9, (int) HelperAttributes.attributeName2typeShortForm("Short"));
    }

    @Test
    public void testAttributeName2TypeShortForm13() {
        assertEquals(10, (int) HelperAttributes.attributeName2typeShortForm("UShort"));
    }

    @Test
    public void testAttributeName2TypeShortForm14() {
        assertEquals(11, (int) HelperAttributes.attributeName2typeShortForm("Integer"));
    }

    @Test
    public void testAttributeName2TypeShortForm15() {
        assertEquals(12, (int) HelperAttributes.attributeName2typeShortForm("UInteger"));
    }

    @Test
    public void testAttributeName2TypeShortForm16() {
        assertEquals(13, (int) HelperAttributes.attributeName2typeShortForm("Long"));
    }

    @Test
    public void testAttributeName2TypeShortForm17() {
        assertEquals(14, (int) HelperAttributes.attributeName2typeShortForm("ULong"));
    }

    @Test
    public void testAttributeName2TypeShortForm18() {
        assertEquals(15, (int) HelperAttributes.attributeName2typeShortForm("String"));
    }

    @Test
    public void testAttributeName2TypeShortForm19() {
        assertEquals(16, (int) HelperAttributes.attributeName2typeShortForm("Time"));
    }

    @Test
    public void testAttributeName2TypeShortForm20() {
        assertEquals(17, (int) HelperAttributes.attributeName2typeShortForm("FineTime"));
    }

    @Test
    public void testAttributeName2TypeShortForm21() {
        assertEquals(18, (int) HelperAttributes.attributeName2typeShortForm("URI"));
    }

    /* Tests for serialObject2blobAttribute */

    @Test
    public void testSerialObject2BlobAttribute1() {
        try {
            Blob b = HelperAttributes.serialObject2blobAttribute(null);
            byte[] bs = b.getValue();
            assertEquals(112, bs[4]);
        } catch (IOException | MALException e) {
            e.printStackTrace();
            fail("IOException!");
        }
    }

    @Test
    public void testSerialObject2BlobAttribute2() {
        try {
            String ref = "Serializable String";
            byte[] refArr = ref.getBytes();
            Blob b = HelperAttributes.serialObject2blobAttribute("Serializable String");
            byte[] bs = b.getValue();
            for (int i = 0; i < refArr.length; i++) {
                assertEquals(refArr[i], bs[i + 7]);
            }
        } catch (IOException | MALException e) {
            e.printStackTrace();
            fail("IOException!");
        }
    }

    /* Tests for blobAttribute2serialObject */

    @Test(expected = IllegalArgumentException.class)
    public void testBlobAttribute2SerialObject1() {
        try {
            HelperAttributes.blobAttribute2serialObject(null);
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException!");
        }
    }

    @Test
    public void testBlobAttribute2serialObject2() {
        // serialization and deserialization should be inverse of each other
        String ref = "I am the string of the legends.";
        try {
            assertEquals(ref, HelperAttributes.blobAttribute2serialObject(HelperAttributes.serialObject2blobAttribute(
                ref)));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException!");
        }
    }
}
