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
package esa.mo.helpertools.misc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * A Helper class to serialize java objects from and to a file.
 *
 */
public class FileDataSerialization {

    /**
     * Serializes data to a file
     *
     * @param filename Name of the file
     * @param ish      the object to be wrapped
     * @throws java.net.MalformedURLException
     * @throws java.lang.IllegalArgumentException If filename == null
     */
    public static void serializeDataOut(String filename, Object ish) throws IOException, IllegalArgumentException {
        if (filename == null) {
            throw new IllegalArgumentException("Filename must not be null for serialization.");
        }
        FileOutputStream fos = new FileOutputStream(filename);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(ish);
        oos.flush();
        oos.close();
    }

    /**
     * Deserializes data from a file
     *
     * @param filename Name of the file
     * @return Generated Object
     * @throws java.io.FileNotFoundException
     * @throws java.net.MalformedURLException
     * @throws java.lang.ClassNotFoundException
     * @throws java.lang.IllegalArgumentException If filename == null
     */
    public static Object serializeDataIn(String filename) throws IOException, ClassNotFoundException,
        IllegalArgumentException {
        if (filename == null) {
            throw new IllegalArgumentException("Filename must not be null for deserialization.");
        }
        try {
            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);

            Object iHandler = ois.readObject();
            ois.close();
            return iHandler;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException e) {
            throw new IOException();
        }
    }

}
