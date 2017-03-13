/* ----------------------------------------------------------------------------
 * Copyright (C) 2017      European Space Agency
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
package esa.opssat.camera.processing;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author César Coelho
 */
public class TestProcessing {

    public static void main(String[] args) {
        // Just a simple test...
        String fileName = "20161221_074003_myPicture.raw";
        String directory = "C:\\Users\\Cesar Coelho\\Desktop\\";
//        Path path = Paths.get(directory + fileName);

        try {
            RandomAccessFile f = new RandomAccessFile(directory+fileName, "r");
            if (f.length() > Integer.MAX_VALUE) {
                throw new IOException("File is too large");
            }
            byte[] data = new byte[(int) f.length()];
            f.readFully(data);

//            byte[] data = Files.readAllBytes(path);
            OPSSATCameraDebayering.getBufferedImageFromBytes(data);
        } catch (IOException ex) {
            Logger.getLogger(TestProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
