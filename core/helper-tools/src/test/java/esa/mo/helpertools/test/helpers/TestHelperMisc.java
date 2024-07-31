package esa.mo.helpertools.test.helpers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.BlobList;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.DoubleList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.DurationList;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTimeList;
import org.ccsds.moims.mo.mal.structures.FloatList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.OctetList;
import org.ccsds.moims.mo.mal.structures.ShortList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.TimeList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.ULongList;
import org.ccsds.moims.mo.mal.structures.UOctetList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.URIList;
import org.ccsds.moims.mo.mal.structures.UShortList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import esa.mo.helpertools.helpers.HelperMisc;

public class TestHelperMisc {

    private static final Logger LOGGER = Logger.getLogger(TestHelperMisc.class.getName());

    Properties props;

    @Before
    public void saveProps() {
        props = (Properties) System.getProperties().clone();
    }

    @After
    public void restoreProps() {
        System.setProperties(props);
    }

    @Before
    public void setup() {
        try {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            Logger.getLogger(TestHelperMisc.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /* Tests for loadProperties */

    /*
     * Different signatures:
     * 
     * public static Properties loadProperties(final java.net.URL url, final String
     * chainProperty)
     * 
     * public static Properties loadProperties(final String path) throws IOException
     * 
     */

    @Test(expected = IllegalArgumentException.class)
    public void testLoadPropertiesPath1() {
        try {
            HelperMisc.loadProperties(null);
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
    }

    @Test
    public void testLoadPropertiesPath2() {
        try {
            String path = getClass().getClassLoader().getResource("testProp1.properties").getPath();
            Properties res = HelperMisc.loadProperties(path);
            assertEquals(2, res.size());
            assertEquals("Foo", res.get("helpertools.test.TestProp1"));
            assertEquals("Bar", res.get("helpertools.test.TestProp2"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadPropertiesURL1() {
        Properties res = HelperMisc.loadProperties(null, null);
        assertNotNull(res);
        assertEquals(0, res.size());
    }

    @Test
    public void testLoadPropertiesURL2() {
        String path = getClass().getClassLoader().getResource("testProp2.properties").getPath();
        URL url;
        try {
            url = new File(path).toURI().toURL();
            Properties res = HelperMisc.loadProperties(url, "");
            assertEquals(3, res.size());
            assertEquals("nmf", res.get("helpertools.test.TestProp3"));
            assertEquals("ops-sat", res.get("helpertools.test.TestProp4"));
            assertEquals("tle", res.get("helpertools.test.TestProp5"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            fail("MalformedURLException!");
        }
    }

    /**
     * The m.u.t. has to check the value of the second parameter for null, otherwise
     * the methodcall will result in a NullPointerException.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLoadPropertiesURL3() {
        String path = getClass().getClassLoader().getResource("testProp2.properties").getPath();
        URL url;
        try {
            url = new File(path).toURI().toURL();
            HelperMisc.loadProperties(url, null);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            fail("MalformedURLException!");
        }
    }

    /* Tests for loadConsumerProperties */

    @Test
    public void testLoadConsumerProperties1() {
        Properties systemProps = System.getProperties();
        systemProps.put("consumer.properties", getClass().getClassLoader().getResource("testProp3.properties")
            .getPath());
        System.setProperties(systemProps);
        try {
            HelperMisc.loadConsumerProperties();
            Properties resProps = System.getProperties();
            assertTrue(resProps.containsKey("helpertools.test.TestProp1"));
            assertTrue(resProps.containsKey("helpertools.test.TestProp2"));
            assertTrue(resProps.containsKey("helpertools.test.TestProp3"));
            assertTrue(resProps.containsKey("helpertools.test.TestProp4"));
            assertTrue(resProps.containsKey("helpertools.test.TestProp5"));
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException!");
        }
    }

    @Test(expected = IOException.class)
    public void testLoadConsumerProperties2() throws IOException {
        Properties systemProps = System.getProperties();
        systemProps.put("consumer.properties", "fileThatDoesntExist.properties");
        System.setProperties(systemProps);
        HelperMisc.loadConsumerProperties();
    }

    /* Tests for loadThisPropertiesFile */

    @Test(expected = IllegalArgumentException.class)
    public void testLoadThisPropertiesFile1() {
        HelperMisc.loadThisPropertiesFile(null);
    }

    @Test
    public void testLoadThisPropertiesFile2() {
        int oldSize = System.getProperties().size();
        HelperMisc.loadThisPropertiesFile(getClass().getClassLoader().getResource("testProp2.properties").getPath());

        Properties sys = System.getProperties();
        assertEquals(3, sys.size() - oldSize);
        assertTrue(sys.containsKey("helpertools.test.TestProp3"));
        assertTrue(sys.containsKey("helpertools.test.TestProp4"));
        assertTrue(sys.containsKey("helpertools.test.TestProp5"));
    }

    @Test
    public void testLoadThisPropertiesFile3() {
        int oldSize = System.getProperties().size();
        HelperMisc.loadThisPropertiesFile("foobar.properties");

        Properties sys = System.getProperties();
        assertEquals(0, sys.size() - oldSize);
    }

    /* Tests for isStringAttribute */

    @Test(expected = IllegalArgumentException.class)
    public void testIsStringAttribute1() {
        HelperMisc.isStringAttribute(null);
    }

    @Test
    public void testIsStringAttribute2() {
        assertEquals(true, HelperMisc.isStringAttribute(new Identifier("Foo")));
    }

    @Test
    public void testIsStringAttribute3() {
        assertEquals(true, HelperMisc.isStringAttribute(new Union("Bar")));
    }

    @Test
    public void testIsStringAttribute4() {
        assertEquals(true, HelperMisc.isStringAttribute(new URI("www.esa.int")));
    }

    @Test
    public void testIsStringAttribute5() {
        assertEquals(false, HelperMisc.isStringAttribute(new Duration(123.42)));
    }

    /* Tests for element2elementList */

    @Test
    public void testElement2ElementList1() {
        try {
            ElementList res = HelperMisc.element2elementList(null);
            assertEquals(null, res);
        } catch (MALException e) {
            e.printStackTrace();
            fail("Wrong exception");
        }
    }

    @Test
    public void testElement2ElementList2() {
        Long foo = 12345678900987L;
        try {
            ElementList result = HelperMisc.element2elementList(foo);
            assertNotNull(result);
            assertTrue(result instanceof LongList);
        } catch (IllegalArgumentException | MALException e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElement2ElementList3() {
        Duration foo = new Duration(1234.0);
        try {
            ElementList result = HelperMisc.element2elementList(foo);
            assertNotNull(result);
            assertTrue(result instanceof DurationList);
        } catch (IllegalArgumentException | MALException e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElement2ElementList4() {
        String foo = "ASDFqwerty";
        try {
            ElementList result = HelperMisc.element2elementList(foo);
            assertNotNull(result);
            assertTrue(result instanceof StringList);
        } catch (IllegalArgumentException | MALException e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    /* Tests for elementList2element */

    @Test
    public void testElementList2Element1() {
        try {
            assertNull(HelperMisc.elementList2element(null));
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element2() {
        ElementList input = new BlobList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(1, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element3() {
        ElementList input = new BooleanList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(2, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element4() {
        ElementList input = new DurationList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(3, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element5() {
        ElementList input = new FloatList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(4, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element6() {
        ElementList input = new DoubleList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(5, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element7() {
        ElementList input = new IdentifierList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(6, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element8() {
        ElementList input = new OctetList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(7, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element9() {
        ElementList input = new UOctetList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(8, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element10() {
        ElementList input = new ShortList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(9, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element11() {
        ElementList input = new UShortList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(10, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element12() {
        ElementList input = new IntegerList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(11, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element13() {
        ElementList input = new UIntegerList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(12, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element14() {
        ElementList input = new LongList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(13, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element15() {
        ElementList input = new ULongList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(14, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element16() {
        ElementList input = new StringList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(15, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element17() {
        ElementList input = new TimeList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(16, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element18() {
        ElementList input = new FineTimeList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(17, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    @Test
    public void testElementList2Element19() {
        ElementList input = new URIList();
        Element result;
        try {
            result = HelperMisc.elementList2element(input);
            assertNotNull(result);
            assertEquals(18, (int) result.getTypeShortForm());
        } catch (Exception e) {
            e.printStackTrace();
            fail("This test should not cause any exceptions.");
        }
    }

    /* Tests for domainId2domain */

    @Test
    public void testDomainId2Domain1() {
        IdentifierList result = HelperMisc.domainId2domain(null);
        assertNotNull(result);
        assertTrue(result instanceof IdentifierList);
        assertEquals(0, result.size());
    }

    @Test
    public void testDomainId2Domain2() {
        IdentifierList result = HelperMisc.domainId2domain("");
        assertNotNull(result);
        assertTrue(result instanceof IdentifierList);
        assertEquals("An empty domain string should result in an empty IdentifierList; Size ", 0, result.size());
    }

    @Test
    public void testDomainId2Domain3() {
        IdentifierList result = HelperMisc.domainId2domain("www.github.com");
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(new Identifier("www"), result.get(0));
        assertEquals(new Identifier("github"), result.get(1));
        assertEquals(new Identifier("com"), result.get(2));
    }

    /* Tests for domain2domainId */

    @Test
    public void testDomain2DomainId1() {
        assertEquals(null, HelperMisc.domain2domainId(null));
    }

    @Test
    public void testDomain2DomainId2() {
        IdentifierList input = new IdentifierList();
        assertEquals("", HelperMisc.domain2domainId(input));
    }

    @Test
    public void testDomain2DomainId3() {
        IdentifierList input = new IdentifierList();
        input.add(new Identifier("www"));
        input.add(new Identifier("esa"));
        input.add(new Identifier("int"));
        assertEquals("www.esa.int", HelperMisc.domain2domainId(input));
    }

    /* Tests for loadPropertiesFile */

    @Test
    public void testLoadPropertiesFile() {
        System.setProperty("provider.properties", getClass().getClassLoader().getResource("testProp7.properties")
            .getPath());
        HelperMisc.loadPropertiesFile();
        Properties props = System.getProperties();
        assertEquals("nanosat-mo-test", props.get("helpertools.configurations.MOappName"));
    }
}
