/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package opssat.simulator.util;

import java.awt.image.BufferedImage;
import org.junit.Assert;
import org.junit.Test;

/**
 * Checks that a picture survives the trip through the camera.
 *
 * The simulated camera does not hand out pictures. It hands out what the real
 * IMS-100 hands out: one colour reading per pixel, in the RGGB arrangement,
 * which has to be made back into a picture at the other end. Whoever asks for
 * anything other than RAW gets that reconstruction, so this checks the two
 * halves against each other.
 *
 * The colours matter as much as the shape. A red and blue that come back the
 * wrong way round would still be a picture, and would still pass any test that
 * only looked at how far the numbers had moved, so they are checked by name.
 *
 * @author Cesar Coelho
 */
public class ImageLoaderTest {

    /** Small enough to be quick, even in whole squares of the pattern. */
    private static final int WIDTH = 64;
    private static final int HEIGHT = 48;

    /**
     * A flat colour has nothing for the reconstruction to guess at, so it has
     * to come back exactly, and on the right channels.
     */
    @Test
    public void flatColourSurvivesExactly() {
        int[][] colours = {{200, 100, 50}, {0, 0, 0}, {255, 255, 255}, {12, 240, 7}};

        for (int[] c : colours) {
            int rgb = (c[0] << 16) | (c[1] << 8) | c[2];
            BufferedImage input = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    input.setRGB(x, y, rgb);
                }
            }

            byte[] bayer = ImageLoader.convertToBayerPattern(input);
            BufferedImage output = ImageLoader.debayer(bayer, WIDTH, HEIGHT);

            int got = output.getRGB(WIDTH / 2, HEIGHT / 2);
            Assert.assertEquals("red channel", c[0], (got >> 16) & 0xFF);
            Assert.assertEquals("green channel", c[1], (got >> 8) & 0xFF);
            Assert.assertEquals("blue channel", c[2], got & 0xFF);
        }
    }

    /**
     * A picture that varies has to come back close to what went in. It cannot
     * come back exactly: four pixels share one red and one blue reading between
     * them, and that is what a colour filter array costs.
     */
    @Test
    public void aVaryingPictureComesBackClose() {
        BufferedImage input = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                input.setRGB(x, y, ((x * 4 % 256) << 16) | ((y * 5 % 256) << 8) | ((x + y) % 256));
            }
        }

        BufferedImage output = ImageLoader.debayer(
                ImageLoader.convertToBayerPattern(input), WIDTH, HEIGHT);

        long total = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int a = input.getRGB(x, y), b = output.getRGB(x, y);
                total += Math.abs(((a >> 16) & 0xFF) - ((b >> 16) & 0xFF))
                        + Math.abs(((a >> 8) & 0xFF) - ((b >> 8) & 0xFF))
                        + Math.abs((a & 0xFF) - (b & 0xFF));
            }
        }
        double perChannel = (double) total / (WIDTH * HEIGHT * 3);
        Assert.assertTrue("the picture came back too far from what went in: " + perChannel,
                perChannel < 6.0);
    }

    /**
     * A frame off a camera, rather than one made out of a picture.
     *
     * The camera reports twelve bits, low in the sixteen bit word. A frame made
     * by {@link ImageLoader#convertToBayerPattern} carries eight, high in the
     * word. Reading only the upper byte suits the second and ruins the first,
     * which comes back very nearly black, and that is what used to happen.
     */
    @Test
    public void aTwelveBitFrameOffTheCameraIsNotLeftBlack() {
        byte[] frame = new byte[WIDTH * HEIGHT * 2];
        for (int i = 0; i < WIDTH * HEIGHT; i++) {
            int reading = 0x0800 + (i % 0x0400); // twelve bit, so under 4096
            frame[i * 2] = (byte) (reading & 0xFF); // little endian
            frame[i * 2 + 1] = (byte) (reading >> 8);
        }

        BufferedImage output = ImageLoader.debayer(frame, WIDTH, HEIGHT);

        long total = 0;
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                int p = output.getRGB(x, y);
                total += ((p >> 16) & 0xFF) + ((p >> 8) & 0xFF) + (p & 0xFF);
            }
        }
        double mean = (double) total / (WIDTH * HEIGHT * 3);
        Assert.assertTrue("a twelve bit frame came back at a mean of " + mean
                + ", which is the picture being thrown away rather than read",
                mean > 64.0);
    }

    /** The size is the caller's claim about the frame, so it is checked. */
    @Test(expected = IllegalArgumentException.class)
    public void aShortFrameIsRefused() {
        ImageLoader.debayer(new byte[WIDTH * HEIGHT], WIDTH, HEIGHT);
    }

    /** Nothing is not a frame. */
    @Test(expected = IllegalArgumentException.class)
    public void nullIsRefused() {
        ImageLoader.debayer(null, WIDTH, HEIGHT);
    }
}
