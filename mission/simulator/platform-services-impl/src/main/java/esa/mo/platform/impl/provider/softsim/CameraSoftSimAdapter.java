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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.platform.impl.provider.gen.CameraAdapterInterface;
import esa.opssat.camera.processing.OPSSATCameraDebayering;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.MALException;

/**
 *
 * @author Cesar Coelho
 */
public class CameraSoftSimAdapter implements CameraAdapterInterface
{

  private final static Duration MINIMUM_DURATION = new Duration(10); // 10 seconds for now...
  private final static int IMAGE_WIDTH = 2048;
  private final static int IMAGE_HEIGHT = 1944;
  private final static int PREVIEW_WIDTH = 600;
  private final static int PREVIEW_HEIGHT = 600;
  private final ESASimulator instrumentsSimulator;
  private final PictureFormatList supportedFormats = new PictureFormatList();

  public CameraSoftSimAdapter(ESASimulator instrumentsSimulator)
  {
    supportedFormats.add(PictureFormat.RAW);
    supportedFormats.add(PictureFormat.RGB24);
    supportedFormats.add(PictureFormat.BMP);
    supportedFormats.add(PictureFormat.PNG);
    supportedFormats.add(PictureFormat.JPG);
    this.instrumentsSimulator = instrumentsSimulator;
  }

  @Override
  public String getExtraInfo()
  {
    return "NMF Satellite Simulator - Camera Adapter";
  }

  @Override
  public PixelResolutionList getAvailableResolutions()
  {
    PixelResolutionList availableResolutions = new PixelResolutionList();
    // Only one:
    availableResolutions.add(new PixelResolution(new UInteger(IMAGE_WIDTH), new UInteger(
        IMAGE_HEIGHT)));

    // Insert the Available Resolutions
    return availableResolutions;
  }

  @Override
  public synchronized Picture getPicturePreview()
  {
    final PixelResolution resolution = new PixelResolution(new UInteger(PREVIEW_WIDTH),
        new UInteger(PREVIEW_HEIGHT));
    final Duration exposureTime = new Duration(0.1);
    final Time timestamp = HelperTime.getTimestampMillis();
    final byte[] data = instrumentsSimulator.getpCamera().takePicture(
        (int) resolution.getWidth().getValue(), (int) resolution.getHeight().getValue());

    CameraSettings pictureSettings = new CameraSettings();
    pictureSettings.setResolution(resolution);
    pictureSettings.setFormat(PictureFormat.RAW);
    pictureSettings.setExposureTime(exposureTime);
    Picture picture = new Picture(timestamp, pictureSettings, new Blob(data));

    return picture;
  }

  @Override
  public synchronized Picture takePicture(final CameraSettings settings) throws IOException
  {
    // Get a picture from the simulator...
    final Time timestamp = HelperTime.getTimestampMillis();
    byte[] data = instrumentsSimulator.getpCamera().takePicture(
        (int) settings.getResolution().getWidth().getValue(), (int) settings.getResolution().getHeight().getValue());

    if (settings.getFormat() != settings.getFormat().RAW) {
      data = convertImage(data, settings.getFormat());
    }

    CameraSettings pictureSettings = new CameraSettings();
    pictureSettings.setResolution(settings.getResolution());
    pictureSettings.setFormat(settings.getFormat());
    pictureSettings.setExposureTime(settings.getExposureTime());
    pictureSettings.setGainRed(settings.getGainRed());
    pictureSettings.setGainGreen(settings.getGainGreen());
    pictureSettings.setGainBlue(settings.getGainBlue());
    Picture picture = new Picture(timestamp, pictureSettings, new Blob(data));
    return picture;
  }

  @Override
  public Picture takeAutoExposedPicture(CameraSettings settings) throws IOException, MALException
  {
    return takePicture(settings);
  }


  @Override
  public Duration getMinimumPeriod()
  {
    return MINIMUM_DURATION;
  }

  private byte[] convertImage(byte[] rawImage, final PictureFormat targetFormat) throws
      IOException
  {
    BufferedImage image = OPSSATCameraDebayering.getDebayeredImage(rawImage);
    byte[] ret = null;

    ByteArrayOutputStream stream = new ByteArrayOutputStream();

    if (targetFormat.equals(PictureFormat.RGB24)) {
      int w = image.getWidth();
      int h = image.getHeight();
      int[] rgba = image.getRGB(0, 0, w, h, null, 0, w);
      ret = new byte[rgba.length * 3];
      for (int i = 0; i < rgba.length; ++i) {
        final int pixelval = rgba[i];
        ret[i * 3 + 0] = (byte) ((pixelval >> 16) & 0xFF); // R
        ret[i * 3 + 1] = (byte) ((pixelval >> 8) & 0xFF);  // G
        ret[i * 3 + 2] = (byte) ((pixelval) & 0xFF);       // B
        // Ignore Alpha channel
      }
    } else if (targetFormat.equals(PictureFormat.BMP)) {
      ImageIO.write(image, "BMP", stream);
      ret = stream.toByteArray();
      stream.close();
    } else if (targetFormat.equals(PictureFormat.PNG)) {
      ImageIO.write(image, "PNG", stream);
      ret = stream.toByteArray();
      stream.close();
    } else if (targetFormat.equals(PictureFormat.JPG)) {
      ImageIO.write(image, "JPEG", stream);
      ret = stream.toByteArray();
      stream.close();
    } else {
      throw new IOException(
          "Something went wrong! The Image could not be converted into the selected format.");
    }
    return ret;
  }

  @Override
  public PictureFormatList getAvailableFormats()
  {
    return supportedFormats;
  }

  @Override
  public boolean isUnitAvailable()
  {
    return true;
  }

}
