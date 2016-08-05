/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
 *
 *
 */
public class FileDataSerialization {

    /**
     * Serializes data to a file
     *
     * @param filename Name of the file
     * @param ish the object to be wrapped
     * @throws java.net.MalformedURLException
     */
    public static void serializeDataOut(String filename, Object ish) throws IOException {
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
     */
    public static Object serializeDataIn(String filename) throws FileNotFoundException, IOException, ClassNotFoundException {
        try {
            FileInputStream fin = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fin);

            Object iHandler = (Object) ois.readObject();
            ois.close();
            return iHandler;
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException();
        } catch (IOException e) {
            throw new IOException();
        }
    }

}
