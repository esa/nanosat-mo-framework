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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Time;
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
public class CameraSingleImageAdapter implements CameraAdapterInterface {

    private final static Duration MINIMUM_DURATION = new Duration(10); // 10 seconds for now...
    private final static UInteger IMAGE_LENGTH = new UInteger(2048);
    private final static UInteger IMAGE_WIDTH = new UInteger(1944);

    public CameraSingleImageAdapter() {
    }

    @Override
    public String getExtraInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public PixelResolutionList getAvailableResolutions() {
        PixelResolutionList availableResolutions = new PixelResolutionList();
        // Only one:
        availableResolutions.add(new PixelResolution(IMAGE_LENGTH, IMAGE_WIDTH));

        // Insert the Available Resolutions
        return availableResolutions;
    }

    @Override
    public synchronized Picture getPicturePreview() {
        return null;
    }

    @Override
    public Picture takePicture(CameraSettings settings) throws IOException {
        // Eiffel Tower (example)
        Blob content = null;
        try {
            String fileName = "picture_demo.jpg";
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream in = classLoader.getResourceAsStream(fileName);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            content = new Blob(response);
        } catch (java.net.UnknownHostException ex) {
            Logger.getLogger(CameraSingleImageAdapter.class.getName()).log(
                    Level.SEVERE, "Maybe there is no internet?", ex);
            throw new IOException(ex); // Wrap into an IOException
        } catch (MalformedURLException ex) {
            Logger.getLogger(CameraSingleImageAdapter.class.getName()).log(
                    Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CameraSingleImageAdapter.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return new Picture(Time.now(), settings, content);
    }

    @Override
    public Duration getMinimumPeriod() {
        return MINIMUM_DURATION;
    }

    @Override
    public boolean isUnitAvailable() {
        return true;
    }

    @Override
    public PictureFormatList getAvailableFormats() {
        PictureFormatList list = new PictureFormatList();
        list.add(PictureFormat.RAW);
        list.add(PictureFormat.JPG);
        list.add(PictureFormat.PNG);
        return list;
    }

    @Override
    public Picture takeAutoExposedPicture(CameraSettings settings) throws IOException, MALException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean hasFixedResolutions() {
        // TODO Auto-generated method stub
        return false;
    }

}
