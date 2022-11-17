package opssat.simulator.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

//import esa.opssat.camera.processing.OPSSATCameraDebayering;

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
     * Scales a BufferedImage to the wanted target resolution. This can be up-scaled
     * or down-scaled.
     * 
     * @param targetWidth  The final width for the output image.
     * @param targetHeight The final height for the output image.
     * @param input        The image to rescale.
     * @return BufferedImage with the resolution targetWidth x targetHeight
     * @throws IllegalArgumentException Iff targetWidth <= 0 || targetHeight <= 0 ||
     *                                  input == null.
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
     *         pattern RGGB.
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
     * @throws IOException              Iff something unexpected occurs while
     *                                  reading the file.
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

    public static byte getRed(int pixel) {
        return (byte) ((pixel & 0x00FF0000) >> 16);
    }

    public static byte getGreen(int pixel) {
        return (byte) ((pixel & 0x0000FF00) >> 8);
    }

    public static byte getBlue(int pixel) {
        return (byte) (pixel & 0x000000FF);
    }

    /*public static void main(String[] args) {
    String path = "/home/yannick/repositories/eclipsePlayground/nanosat-mo-framework/mission/simulator/opssat-spacecraft-simulator/src/main/resources/earth.jpg";
    try {
      byte[] data = loadNonRawImage(path);
      BufferedImage bi = OPSSATCameraDebayering.getDebayeredImage(data);
      ImageIO.write(bi, "jpg", new File("/home/yannick/loadertest.jpg"));
    } catch (IllegalArgumentException | IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    }*/
}
