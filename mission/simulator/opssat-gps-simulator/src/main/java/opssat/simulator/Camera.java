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
package opssat.simulator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * The Camera class simulates a picture taken by a real camera by providing a
 * byte array of data. The picture is not realistic.
 * 
 * @author Cesar Coelho
 */
public class Camera {

    public byte[] takePicture(float duration) {

        try {
            Thread.sleep((long) (duration * 1000));
        } catch (InterruptedException ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
        }

        BufferedImage imgIn;
        byte[] imgOut = null;

/*
        try {
            imgIn = ImageIO.read(new File("earth.jpg"));
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            ImageIO.write(imgIn, "jpg", byteArray);
            byteArray.flush();
            imgOut = byteArray.toByteArray();
        } catch (IOException ex) {
            Logger.getLogger(Camera.class.getName()).log(Level.SEVERE, null, ex);
        }
*/


        return imgOut;

    }

}
