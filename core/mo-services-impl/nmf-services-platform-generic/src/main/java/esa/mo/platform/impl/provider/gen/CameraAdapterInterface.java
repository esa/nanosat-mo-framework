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
package esa.mo.platform.impl.provider.gen;

import java.io.IOException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;

/**
 * The CameraAdapterInterface is an interface to create adapters for the Camera
 * service.
 */
public interface CameraAdapterInterface {

    /**
     * Checks if the device is present and accessible.
     *
     * @return true if the device is present and available for operations.
     */
    boolean isUnitAvailable();

    /**
     * Returns true of the camera as a fixed set of resolutions.
     *
     * @return true if the camera has only a fixed set of resolutions. They can
     * then be retrieved with the getAvailableResolutions() method. The default
     * is true to be backwards compatible with earlier implementations.
     */
    default boolean hasFixedResolutions() {
        return true;
    }

    /**
     * Returns the available resolutions for the camera.
     *
     * @return The resolutions supported by the Camera Adapter
     */
    PixelResolutionList getAvailableResolutions();

    /**
     * Returns the supported formats for the camera.
     *
     * @return The formats supported by the Camera Adapter.
     */
    PictureFormatList getAvailableFormats();

    /**
     * Captures a preview of the picture.
     *
     * @return A preview of the picture.
     * @throws java.io.IOException if something went wrong.
     */
    Picture getPicturePreview() throws IOException;

    /**
     * Captures a picture.
     *
     * @param settings The settings for capturing the picture.
     * @return The picture.
     * @throws IOException if the picture could not be taken.
     */
    Picture takePicture(final CameraSettings settings) throws IOException;

    /**
     * Captures a picture using auto exposure.
     *
     * @param settings The settings for capturing the picture.
     * @return The picture.
     * @throws IOException if the picture could not be taken.
     * @throws MALException if something else went wrong.
     */
    Picture takeAutoExposedPicture(final CameraSettings settings) throws IOException, MALException;

    /**
     * The getMinimumPeriod method shall return the duration of taking a
     * picture. This value is intended to be used to limit the number of
     * consecutive calls for the takePicture method and therefore the minimum
     * period of the stream shall be the duration of taking a picture. This
     * method should be a static method however Java 6 does not allow static
     * methods in interfaces.
     *
     * @return The minimum period of the stream. Or the duration of taking a
     * picture.
     */
    Duration getMinimumPeriod();

    String getExtraInfo();

}
