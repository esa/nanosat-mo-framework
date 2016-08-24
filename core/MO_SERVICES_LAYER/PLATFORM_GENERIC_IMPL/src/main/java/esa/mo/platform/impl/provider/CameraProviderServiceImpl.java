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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.camera.CameraHelper;
import org.ccsds.moims.mo.platform.camera.body.GetPropertiesResponse;
import org.ccsds.moims.mo.platform.camera.provider.CameraInheritanceSkeleton;
import org.ccsds.moims.mo.platform.camera.provider.TakePictureInteraction;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;

/**
 *
 */
public class CameraProviderServiceImpl extends CameraInheritanceSkeleton {

    private MALProvider cameraServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private CameraAdapterInterface adapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param adapter The Camera adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, CameraAdapterInterface adapter) throws MALException {
        if (!initialiased) {

            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME, PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                CameraHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
            }
        }

        // Shut down old service transport
        if (null != cameraServiceProvider) {
            connection.close();
        }

        this.adapter = adapter;
        cameraServiceProvider = connection.startService(CameraHelper.CAMERA_SERVICE_NAME.toString(), CameraHelper.CAMERA_SERVICE, this);

        running = true;
        initialiased = true;
        Logger.getLogger(CameraProviderServiceImpl.class.getName()).info("Action service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != cameraServiceProvider) {
                cameraServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public void setStreaming(Duration streamingRate, PixelResolution dimension, PictureFormat format, Identifier firstEntityKey, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unsetStreaming(MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Picture previewPicture(MALInteraction interaction) throws MALInteractionException, MALException {

        // Get some picture from the adapter...
        Picture aaaa = adapter.getPicturePreview();

        byte[] data = null;

        ImageIcon image = new ImageIcon(data);
        PixelResolution dimension = new PixelResolution();

        dimension.setHeight(new UInteger(image.getIconHeight()));
        dimension.setWidth(new UInteger(image.getIconWidth()));
        
        Picture picture = new Picture();
        picture.setCreationDate(HelperTime.getTimestampMillis());
        picture.setBitDepth(null);
        picture.setContent(new Blob (data));
        picture.setDimension(dimension);

        picture.setDimension(null);
        picture.setFormat(PictureFormat.RAW);
        
        return picture;
    }

    @Override
    public void takePicture(PixelResolution dimension, PictureFormat format, Duration exposureTime, TakePictureInteraction interaction) throws MALInteractionException, MALException {

        interaction.sendAcknowledgement();
        
        
        try {
            Picture data = adapter.takePicture(dimension, format);

//            ImageIcon image = new ImageIcon(data);
    //        BufferedImage bi = data;
//            Picture picture = new Picture();
        
//            picture.setBitDepth(null);

        } catch (IOException ex) {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        
        
        throw new MALInteractionException(new MALStandardError(MALHelper.UNSUPPORTED_OPERATION_ERROR_NUMBER, null));

    }

    @Override
    public GetPropertiesResponse getProperties(MALInteraction interaction) throws MALInteractionException, MALException {

        PictureFormatList availableFormats = new PictureFormatList();
        // The list of formats supported by the service
        availableFormats.add(PictureFormat.RAW);
        // Add the rest of the formats..
        
        PixelResolutionList availableResolutions = adapter.getAvailableResolutions();
        String extraInfo = adapter.getExtraInfo();
        
        return new GetPropertiesResponse(availableResolutions, availableFormats, extraInfo);
    }

}
