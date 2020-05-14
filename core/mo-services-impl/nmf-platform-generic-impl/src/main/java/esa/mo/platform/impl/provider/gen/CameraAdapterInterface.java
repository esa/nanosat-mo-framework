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
package esa.mo.platform.impl.provider.gen;

import java.io.IOException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;

/**
 *
 * @author Cesar Coelho
 */
public interface CameraAdapterInterface
{

  /**
   * Checks if the device is present and accessible.
   *
   * @return true if the device is present and available for operations.
   */
  boolean isUnitAvailable();

  /**
   * @return The resolutions supported by the Camera Adapter
   */
  PixelResolutionList getAvailableResolutions();

  /**
   * @return The formats supported by the Camera Adapter
   */
  PictureFormatList getAvailableFormats();

  Picture getPicturePreview() throws IOException;

  Picture takePicture(final CameraSettings settings)
      throws IOException;

  Picture takeAutoExposedPicture(final CameraSettings settings)
      throws IOException, MALException;

  /**
   * The getMinimumPeriod method shall return the duration of taking a picture. This value is
   * intended to be used to limit the number of consecutive calls for the takePicture method and
   * therefore the minimum period of the stream shall be the duration of taking a picture. This
   * method should be a static method however Java 6 does not allow static methods in interfaces.
   *
   * @return The minimum period of the stream. Or the duration of taking a picture.
   */
  Duration getMinimumPeriod();

  String getExtraInfo();

}
