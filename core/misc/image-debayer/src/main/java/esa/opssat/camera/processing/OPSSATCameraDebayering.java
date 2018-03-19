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

import ij.Debayer_Image;
import ij.ImagePlus;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.process.ShortProcessor;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 *
 * @author Cesar Coelho
 */
public class OPSSATCameraDebayering {

    /**
     * The method converts a byte array coming from OPS-SAT's Camera using the
     * "Replication" algorithm and returns a BufferedImage.
     * 
     * @param data
     * @return
     */
    public static BufferedImage getBufferedImageFromBytes(byte[] data) {
        return OPSSATCameraDebayering.getBufferedImageFromBytes(data, 0);
    }

    /**
     * The method converts a byte array coming from OPS-SAT's Camera using the
     * selected algorithm and returns a BufferedImage.
     * 
     * The available algorithms are:
     * 0: "Replication"
     * 1: "Bilinear" 
     * 2: "Smooth Hue"
     * 3: "Adaptive Smooth Hue"
     * 
     * @param data
     * @param algorithm
     * @return
     */
    public static BufferedImage getBufferedImageFromBytes(byte[] data, int algorithm) {
        FileInfo fi = new FileInfo();
        fi.fileFormat = FileInfo.RAW;
        fi.width = 2048;
        fi.height = 1944;

        fi.offset = (int) 0;
        fi.nImages = 1;
        fi.gapBetweenImages = 0;
        fi.intelByteOrder = true; // Little-endian byte order
        fi.whiteIsZero = false;

        fi.fileType = FileInfo.GRAY16_UNSIGNED;

        FileOpener fo = new FileOpener(fi);
        ColorModel cm = fo.createColorModel(fi);

        // Convert the byte array to a shorts array
        short[] shorts = new short[data.length / 2];
        ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);

        ShortProcessor processor = new ShortProcessor(fi.width, fi.height, shorts, cm);
        ImagePlus imp = new ImagePlus("", processor);

        ImagePlus outImage = OPSSATCameraDebayering.processImage(imp, algorithm);
//        outImage.show();

        return outImage.getBufferedImage();
    }
    
    private static ImagePlus processImage(ImagePlus imp, int algorithm) {
        Debayer_Image debayeringFilter = new Debayer_Image();
        debayeringFilter.setup(String.valueOf(algorithm), imp);
        debayeringFilter.run(null);
        return debayeringFilter.getImage();
    }

}
