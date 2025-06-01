/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.helpertools.test.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.ccsds.moims.mo.mal.helpertools.misc.FileDataSerialization;

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
