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
import java.io.IOException;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
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
        throw new UnsupportedOperationException("Not supported yet.");
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
        picture.setFormat(PictureFormat.RAW);

        return picture;
    }

    @Override
    public synchronized Picture takePicture(final PixelResolution dimensions, 
            final PictureFormat format, final Duration exposureTime) throws IOException {
        // Get a picture from the simulator...
        byte[] data = instrumentsSimulator.getpCamera().takePicture((int) dimensions.getWidth().getValue(), (int) dimensions.getHeight().getValue());

        Picture picture = new Picture();
        picture.setCreationDate(HelperTime.getTimestampMillis());
        picture.setContent(new Blob(data));
        picture.setDimension(dimensions);
        picture.setFormat(PictureFormat.RAW);

        return picture;

    }

    @Override
    public BufferedImage getBufferedImageFromRaw(byte[] rawImage){
        return OPSSATCameraDebayering.getBufferedImageFromBytes(rawImage);
    }
    
    @Override
    public Duration getMinimumPeriod() {
        return MINIMUM_DURATION;
    }

}
