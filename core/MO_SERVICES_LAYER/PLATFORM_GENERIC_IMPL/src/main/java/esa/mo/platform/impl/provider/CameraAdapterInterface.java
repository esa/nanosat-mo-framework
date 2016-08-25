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
package esa.mo.platform.impl.provider;

import java.io.IOException;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;


/**
 *
 * @author Cesar Coelho
 */
public interface CameraAdapterInterface {

    public String getExtraInfo();

    /**
     * This method should be a static method! Java 6 does not allow static
     * methods in interfaces.
     * 
     * @return The available resolutions
     */
    public PixelResolutionList getAvailableResolutions();

    /**
     * This method should be a static method! Java 6 does not allow static
     * methods in interfaces.
     * 
     * @return The available formats
     */
    public PictureFormatList getAvailableFormats();
    
    public Picture getPicturePreview();

    public Picture takePicture(PixelResolution resolution, PictureFormat format) throws IOException;    

    public Picture processPicture(Picture picture, PictureFormat format)  throws IOException;

    public Duration getMinimumPeriod();
    
}
