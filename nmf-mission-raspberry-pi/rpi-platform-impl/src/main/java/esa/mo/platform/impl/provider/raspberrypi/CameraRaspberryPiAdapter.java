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
package esa.mo.platform.impl.provider.raspberrypi;

import esa.mo.platform.impl.provider.gen.CameraAdapterInterface;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.structures.CameraSettings;
import org.ccsds.moims.mo.platform.structures.Picture;
import org.ccsds.moims.mo.platform.structures.PictureFormat;
import org.ccsds.moims.mo.platform.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.structures.PixelResolution;
import org.ccsds.moims.mo.platform.structures.PixelResolutionList;

/**
 *
 * @author Cesar Coelho
 */
public class CameraRaspberryPiAdapter implements CameraAdapterInterface {

    private static final Duration PREVIEW_EXPOSURE_TIME = new Duration(0.100); // 100ms
    private static final Duration MINIMUM_PERIOD = new Duration(1); // 1 second for now...
    private final PictureFormatList supportedFormats = new PictureFormatList();
    private int nativeImageLength;
    private int nativeImageWidth;
    private boolean unitAvailable = false;

    private static final Logger LOGGER = Logger.getLogger(CameraRaspberryPiAdapter.class.getName());

    public CameraRaspberryPiAdapter() {
        supportedFormats.add(PictureFormat.RAW);
        supportedFormats.add(PictureFormat.RGB24);
        supportedFormats.add(PictureFormat.BMP);
        supportedFormats.add(PictureFormat.PNG);
        supportedFormats.add(PictureFormat.JPG);
        LOGGER.log(Level.INFO, "Initialisation");

        unitAvailable = true;
    }

    @Override
    public boolean isUnitAvailable() {
        return unitAvailable;
    }

    @Override
    public String getExtraInfo() {
        return "";
    }

    @Override
    public PixelResolutionList getAvailableResolutions() {
        PixelResolutionList availableResolutions = new PixelResolutionList();

        return availableResolutions;
    }

    @Override
    public synchronized Picture getPicturePreview() {
        final PixelResolution resolution = new PixelResolution(new UInteger(nativeImageWidth),
                new UInteger(nativeImageLength));
        return null;
    }

    @Override
    public Duration getMinimumPeriod() {
        return MINIMUM_PERIOD;
    }

    @Override
    public PictureFormatList getAvailableFormats() {
        return supportedFormats;
    }

    @Override
    public Picture takePicture(CameraSettings settings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Picture takeAutoExposedPicture(CameraSettings settings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasFixedResolutions() {
        // TODO Auto-generated method stub
        return false;
    }


}
