/* ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Utility class to load different image types and convert them to the IMS-100
 * bayer pattern (RGGB) for use in the simulator. This class also takes care of
 * re-scaling the loaded image.
 *
 * @author Yannick Lavan
 *
 */
public class ImageLoader {

    public static final int RESOLUTION_WIDTH = 2048;
    public static final int RESOLUTION_HEIGHT = 1944;

    /**
     * Scales a BufferedImage to the wanted target resolution. This can be
     * up-scaled or down-scaled.
     *
     * @param targetWidth The final width for the output image.
     * @param targetHeight The final height for the output image.
     * @param input The image to rescale.
     * @return BufferedImage with the resolution targetWidth x targetHeight
     * @throws IllegalArgumentException Iff targetWidth &lt;= 0 || targetHeight
     * &lt;= 0 || input == null.
     */
    public static BufferedImage rescale(int targetWidth, int targetHeight, BufferedImage input)
            throws IllegalArgumentException {
        if (targetWidth <= 0 || targetHeight <= 0) {
            throw new IllegalArgumentException("The target dimensions must be positive.");
        }
        if (input == null) {
            throw new IllegalArgumentException("The provided image must not be null");
        }
        BufferedImage out = new BufferedImage(targetWidth, targetHeight, input.getType());

        double x_ratio = (double) input.getWidth() / (double) targetWidth;
        double y_ratio = (double) input.getHeight() / (double) targetHeight;
        double posX = 0.0;
        double posY = 0.0;
        for (int i = 0; i < targetHeight; i++) {
            for (int j = 0; j < targetWidth; j++) {
                posX = Math.floor(j * x_ratio);
                posY = Math.floor(i * y_ratio);
                out.setRGB(j, i, input.getRGB((int) posX, (int) posY));
            }
        }

        return out;
    }

    /**
     * Converts a buffered image to the IMS-100 Bayer pattern.
     *
     * @param input The RGB image to transform.
     * @return The byte array containing the pattern with 12 bits per pixel and
     * pattern RGGB.
     * @throws IllegalArgumentException Iff input == null.
     */
    public static byte[] convertToBayerPattern(BufferedImage input) throws IllegalArgumentException {
        if (input == null) {
            throw new IllegalArgumentException("Input must not be null!");
        }

        int width = input.getWidth();
        int height = input.getHeight();
        int numBytes = width * height * 2;
        byte[] output = new byte[numBytes];
        int k = 0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = input.getRGB(j, i);
                byte col;
                if (i % 2 == 0) { // G R G R G R
                    if (j % 2 == 0) {
                        col = ImageLoader.getGreen(pixel);
                    } else {
                        col = ImageLoader.getRed(pixel);
                    }
                } else { // B G B G B G
                    if (j % 2 == 0) {
                        col = ImageLoader.getBlue(pixel);
                    } else {
                        col = ImageLoader.getGreen(pixel);
                    }
                }
                output[k++] = 0; // The channel value 0xAB is mapped to the bytes
                // 0x00 0xAB (little-endian)
                output[k++] = col;
            }
        }
        // Swap even/odd pixels to fit format of IMS-100
        for (int l = 0; l < output.length - 3; l += 4) {
            byte temp = output[l];
            output[l] = output[l + 2];
            output[l + 2] = temp;
            temp = output[l + 1];
            output[l + 1] = output[l + 3];
            output[l + 3] = temp;
        }

        return output;
    }

    /**
     * Loads images with another extension than .raw, re-scales them to fit the
     * IMS-100 resolution of 2048 x 1944 pixels and transforms them into the raw
     * Bayer pattern.
     *
     * @param path Absolute path to the image.
     * @return Byte array containing the Bayer pattern data of the image.
     * @throws IllegalArgumentException Iff path == null.
     * @throws IOException Iff something unexpected occurs while reading the
     * file.
     */
    public static byte[] loadNonRawImage(String path) throws IllegalArgumentException, IOException {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null.");
        }
        File f = new File(path);
        BufferedImage temp = ImageIO.read(f);
        if (temp.getWidth() != RESOLUTION_WIDTH || temp.getHeight() != RESOLUTION_HEIGHT) {
            temp = ImageLoader.rescale(RESOLUTION_WIDTH, RESOLUTION_HEIGHT, temp);
        }
        return ImageLoader.convertToBayerPattern(temp);
    }

    /**
     * Turns an IMS-100 bayer frame back into a picture, undoing what
     * {@link #convertToBayerPattern} does.
     *
     * The camera reports one colour per pixel, in the RGGB arrangement: red and
     * green alternating along the even rows, green and blue along the odd ones.
     * Each two by two square therefore carries one red, one blue and two green
     * readings between its four pixels, and a whole colour has to be made from
     * them. The simplest way is taken here: the square is given one colour,
     * from its own four readings, and all four pixels are painted with it. It
     * is the "replication" of the literature, and it is what the simulator has
     * always used.
     *
     * @param data The bayer frame, two bytes per pixel, little endian.
     * @param width Width of the frame in pixels.
     * @param height Height of the frame in pixels.
     * @return The picture.
     * @throws IllegalArgumentException If the frame is not the size the width
     * and height call for.
     */
    public static BufferedImage debayer(byte[] data, int width, int height)
            throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Input must not be null!");
        }
        if (data.length < width * height * 2) {
            throw new IllegalArgumentException("Bayer frame holds " + data.length
                    + " bytes, which is short of the " + (width * height * 2)
                    + " that " + width + "x" + height + " needs.");
        }

        // Two bytes to a reading, little endian, and each neighbouring pair is
        // exchanged to suit the IMS-100, so the pair is read back to front to
        // put the readings in the order of the picture.
        //
        // How many of those sixteen bits carry anything depends on where the
        // frame came from. A camera gives twelve, low in the word. A frame made
        // out of an ordinary picture by convertToBayerPattern gives eight, high
        // in the word, with the lower byte left at zero. Both arrive here, so
        // the brightest reading in the frame decides how far the readings have
        // to come down to be eight bit.
        int[] sample = new int[width * height];
        int brightest = 0;
        for (int i = 0; i < sample.length; i++) {
            int pair = i ^ 1; // the neighbour it was swapped with
            int reading = ((data[pair * 2 + 1] & 0xFF) << 8) | (data[pair * 2] & 0xFF);
            sample[i] = reading;
            if (reading > brightest) {
                brightest = reading;
            }
        }

        int shift = 0;
        while ((brightest >> shift) > 0xFF) {
            shift++;
        }
        for (int i = 0; i < sample.length; i++) {
            sample[i] >>= shift;
        }

        BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < height - 1; i += 2) {
            for (int j = 0; j < width - 1; j += 2) {
                int red = sample[i * width + j + 1];
                int blue = sample[(i + 1) * width + j];
                // Two greens are read per square, on opposite corners.
                int green = (sample[i * width + j] + sample[(i + 1) * width + j + 1]) / 2;

                int rgb = (red << 16) | (green << 8) | blue;
                output.setRGB(j, i, rgb);
                output.setRGB(j + 1, i, rgb);
                output.setRGB(j, i + 1, rgb);
                output.setRGB(j + 1, i + 1, rgb);
            }
        }
        return output;
    }

    public static byte getRed(int pixel) {
        return (byte) ((pixel & 0x00FF0000) >> 16);
    }

    public static byte getGreen(int pixel) {
        return (byte) ((pixel & 0x0000FF00) >> 8);
    }

    public static byte getBlue(int pixel) {
        return (byte) (pixel & 0x000000FF);
    }

}
