package esa.mo.helpertools.test.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.Test;
import esa.mo.helpertools.misc.FileDataSerialization;

public class TestFileDataSerialization {

    /* Tests for serializeDataOut */

    @Test(expected = IllegalArgumentException.class)
    public void testSerializeDataOut1() {
        try {
            FileDataSerialization.serializeDataOut(null, new String("I am an object."));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void testSerializeDataOut2() {
        try {
            FileDataSerialization.serializeDataOut("testfile", null);
            File f = new File("testfile");
            assertTrue(f.length() > 0);
            byte[] data = new byte[(int) f.length()];
            FileInputStream fis = new FileInputStream(f);
            fis.read(data);
            fis.close();
            f.delete();
            assertEquals(data[4], 112);
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
    }

    @Test
    public void testSerializeDataOut3() {
        try {
            String ref = "I am an object.";
            FileDataSerialization.serializeDataOut("testfile", ref);
            File f = new File("testfile");
            assertTrue(f.length() > 0);
            byte[] data = new byte[(int) f.length()];
            FileInputStream fis = new FileInputStream(f);
            fis.read(data);
            fis.close();
            byte[] chars = new byte[data.length - 7];
            System.arraycopy(data, 7, chars, 0, data.length - 7);
            String result = new String(chars);
            f.delete();
            assertEquals("I am an object.", result);
        } catch (IOException e) {
            e.printStackTrace();
            fail("IOException");
        }
    }

    /* Tests for serializeDataIn */

    @Test(expected = IllegalArgumentException.class)
    public void testSerializeDataIn1() {
        try {
            FileDataSerialization.serializeDataIn(null);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            fail("Exception " + e.getClass().getName());
        }
    }

    @Test
    public void testSerializeDataIn2() {
        String filename = "testfile";
        try {
            FileDataSerialization.serializeDataOut(filename, "I am a valid string.");
            String result;
            try {
                result = (String) FileDataSerialization.serializeDataIn(filename);
                assertEquals("I am a valid string.", result);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                fail("Class not found exception");
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not write test file.");
        }
    }
}
