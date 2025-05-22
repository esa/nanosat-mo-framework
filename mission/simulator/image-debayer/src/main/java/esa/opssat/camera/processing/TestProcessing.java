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
package esa.opssat.camera.processing;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

/**
 *
 * @author César Coelho
 */
public class TestProcessing {

    public static void main(String[] args) {
        try {
            testDebayer();
        } catch (IOException ex) {
            Logger.getLogger(TestProcessing.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static final String RAW_PATH = "../../../payloads-test/toGround/img_msec_1633341865066_50.ims_rgb";

    public static void testDebayer() throws IOException {
        byte[] inBytes = Files.readAllBytes(Paths.get(RAW_PATH));
        BufferedImage outBuf = OPSSATCameraDebayering.getDebayeredImage(inBytes);

        FileOutputStream stream = new FileOutputStream(RAW_PATH + ".png");

        ImageIO.write(outBuf, "PNG", stream);
        stream.close();
    }

}
