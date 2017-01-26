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
package esa.mo.platform.impl.provider.softsim;

import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.platform.impl.provider.gen.CameraAdapterInterface;
import esa.opssat.camera.processing.OPSSATCameraDebayering;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;

/**
 *
 * @author Cesar Coelho
 */
public class CameraSoftSimAdapter implements CameraAdapterInterface {

    private final ESASimulator instrumentsSimulator;
    private final static Duration MINIMUM_DURATION = new Duration(10); // 10 seconds for now...
    private final static int IMAGE_LENGTH = 2048;
    private final static int IMAGE_WIDTH = 1944;
    
    public CameraSoftSimAdapter(ESASimulator instrumentsSimulator) {
        this.instrumentsSimulator = instrumentsSimulator;
    }

    @Override
    public String getExtraInfo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PixelResolutionList getAvailableResolutions() {
        PixelResolutionList availableResolutions = new PixelResolutionList();
        // Only one:
        availableResolutions.add(new PixelResolution(new UInteger(IMAGE_LENGTH), new UInteger(IMAGE_WIDTH)));
        
        // Insert the Available Resolutions

        return availableResolutions;
    }

    @Override
    public PictureFormatList getAvailableFormats() {
        PictureFormatList availableFormats = new PictureFormatList();
        availableFormats.add(PictureFormat.RAW);

        return availableFormats;
    }

    @Override
    public synchronized Picture getPicturePreview() {
        /*
        // Some data neds to go here:
        byte[] data = null;

        ImageIcon image = new ImageIcon(data);
        PixelResolution dimension = new PixelResolution();

        dimension.setHeight(new UInteger(image.getIconHeight()));
        dimension.setWidth(new UInteger(image.getIconWidth()));
         */

        PixelResolution resolution = new PixelResolution(new UInteger(800), new UInteger(600));
        byte[] data = instrumentsSimulator.getpCamera().takePicture((int) resolution.getWidth().getValue(), (int) resolution.getHeight().getValue());

        Picture picture = new Picture();
        picture.setCreationDate(HelperTime.getTimestampMillis());
        picture.setContent(new Blob(data));
        picture.setDimension(resolution);
        picture.setBitDepth(null);
        picture.setFormat(PictureFormat.RAW);

        return picture;
    }

    @Override
    public synchronized Picture takePicture(PixelResolution dimensions, PictureFormat format) throws IOException {

        // Get some picture from the simulator...
        byte[] data = instrumentsSimulator.getpCamera().takePicture((int) dimensions.getWidth().getValue(), (int) dimensions.getHeight().getValue());

        Picture picture = new Picture();
        picture.setCreationDate(HelperTime.getTimestampMillis());
        picture.setContent(new Blob(data));
        picture.setDimension(dimensions);
        picture.setBitDepth(null);
        picture.setFormat(PictureFormat.RAW);

        return picture;

    }

    @Override
    public Picture convertPicture(Picture picture, PictureFormat format) throws IOException {
        if (picture.getFormat().equals(format)) {
            return picture;
        }

        Picture newPicture = new Picture();
        newPicture.setCreationDate(picture.getCreationDate());
        newPicture.setBitDepth(null);
        newPicture.setDimension(picture.getDimension());

        if (picture.getFormat().equals(PictureFormat.RAW)) {
            // Call Manuels decode algorithm
//            byte[] data = manuelAwesomeAlgorithm.convertRAWToDebayered(picture.getContent().getValue());

            if (format.equals(PictureFormat.RAW_DEBAYERED)) {
                newPicture.setFormat(PictureFormat.RAW_DEBAYERED);
                newPicture.setBitDepth(null);
//                newPicture.setContent(new Blob(data));

                return newPicture;
            }

            if (format.equals(PictureFormat.PNG)) {
                try {
                    BufferedImage img = OPSSATCameraDebayering.getBufferedImageFromBytes(picture.getContent().getValue());
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ImageIO.write(img, "png", stream);
                    newPicture.setContent(new Blob(stream.toByteArray()));
                    newPicture.setFormat(PictureFormat.PNG);
                    return newPicture;
                } catch (MALException ex) {
                    Logger.getLogger(CameraSoftSimAdapter.class.getName()).log(Level.SEVERE, "The picture could not be converted!", ex);
                }

            }

        }

        return picture;
    }

    @Override
    public Duration getMinimumPeriod() {
        return MINIMUM_DURATION;
    }

}
