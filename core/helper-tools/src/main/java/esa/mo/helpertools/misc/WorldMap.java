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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * A class that holds information for a World Map view. This code is originally
 * from Cesar Coelho Master Thesis demo. It is no longer used and should be
 * considered to be removed in the future.
 */
@Deprecated
public class WorldMap {

    private final String earthPictureFilename = "earth.jpg";  // Map variables
    private BufferedImage imageLoaded;
    private int final_width;
    private float ratio;
    private int final_height;

    public WorldMap(int width) {

        try {
            imageLoaded = ImageIO.read(new File(earthPictureFilename));

            final_width = width;
            ratio = ((float) final_width) / ((float) imageLoaded.getWidth());
            final_height = (int) (ratio * ((float) imageLoaded.getHeight()));

        } catch (IOException ex) {
            Logger.getLogger(WorldMap.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addPixel(double x_double, double y_double) {
        int x_top = (int) floor(x_double);
        int y_top = (int) floor(y_double);

        int x_bot = (int) ceil(x_double);
        int y_bot = (int) ceil(y_double);

        // Print 4 pixels
        imageLoaded.setRGB(x_top, y_top, Color.RED.getRGB());
        imageLoaded.setRGB(x_top, y_bot, Color.RED.getRGB());
        imageLoaded.setRGB(x_bot, y_top, Color.RED.getRGB());
        imageLoaded.setRGB(x_bot, y_bot, Color.RED.getRGB());
    }

    public ImageIcon addCoordinate(double latitude, double longitude) {
        // Convert to pixel:
        // Hint: (0,0) is top left
        //       (final_width, final_height) is bottom right
        // latitude: [-90, 90]        180
        // longitude: [-180, 180]     360

        final double width = imageLoaded.getWidth() - 1;
        final double height = imageLoaded.getHeight() - 1;

        // longitude to x position:
        double x = (longitude * width / 360 + width / 2);

        // latitude to y position:
        double y = (-latitude * height / 180 + height / 2);

        // Add the pixel on the map:
        this.addPixel(x, y);

        return new ImageIcon(imageLoaded.getScaledInstance(final_width, final_height, 1));
    }

}
