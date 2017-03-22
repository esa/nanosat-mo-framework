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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.camera.CameraHelper;
import org.ccsds.moims.mo.platform.camera.body.GetPropertiesResponse;
import org.ccsds.moims.mo.platform.camera.provider.CameraInheritanceSkeleton;
import org.ccsds.moims.mo.platform.camera.provider.StreamPicturesPublisher;
import org.ccsds.moims.mo.platform.camera.provider.TakePictureInteraction;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PictureList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;

/**
 *
 */
public class CameraProviderServiceImpl extends CameraInheritanceSkeleton {

    private Duration minimumPeriod;
    private final Duration serviceLowestMinimumPeriod = new Duration(0.01); // 10 millisecods
    private MALProvider cameraServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private StreamPicturesPublisher publisher;
    private boolean cameraInUse;
    private final Object lock = new Object();
    private boolean isRegistered = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private Timer publishTimer = new Timer();
    private final AtomicLong uniqueObjId = new AtomicLong(System.currentTimeMillis());
    private CameraAdapterInterface adapter;
    PictureFormatList availableFormats;

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

        publisher = createStreamPicturesPublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != cameraServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        minimumPeriod = this.adapter.getMinimumPeriod();
        cameraServiceProvider = connection.startService(CameraHelper.CAMERA_SERVICE_NAME.toString(), CameraHelper.CAMERA_SERVICE, this);

        availableFormats = new PictureFormatList();
        availableFormats.add(PictureFormat.RAW);
        availableFormats.add(PictureFormat.PNG);
        availableFormats.add(PictureFormat.BMP);
        availableFormats.add(PictureFormat.JPG);        
        
        running = true;
        initialiased = true;
        Logger.getLogger(CameraProviderServiceImpl.class.getName()).info("Camera service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != cameraServiceProvider) {
                cameraServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during close down of the provider {0}", ex);
        }
    }
    
    private void streamPicturesUpdate(final Identifier firstEntityKey, 
            final PixelResolution resolution, final PictureFormat format, final Duration exposureTime) {
        try {
            final Long objId;
            Picture picture = null;

            synchronized (lock) {
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }

                objId = uniqueObjId.incrementAndGet();
                try {
                    picture = adapter.takePicture(resolution, format, exposureTime);

                    if(!PictureFormat.RAW.equals(format)){
                        BufferedImage image = adapter.getBufferedImageFromRaw(picture.getContent().getValue());
                        picture = this.convertImage(image, format);
                    }
                } catch (IOException ex) {
                    Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating streaming Picture update with objId: " + objId);

            final EntityKey ekey = new EntityKey(firstEntityKey, objId, resolution.getWidth().getValue(), resolution.getHeight().getValue());

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            hdrlst.add(new UpdateHeader(HelperTime.getTimestampMillis(), connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));

            PictureList picList = new PictureList();
            picList.add(picture);
            publisher.publish(hdrlst, picList);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public synchronized void setStreaming(final Duration streamingRate, 
            final PixelResolution resolution, final PictureFormat format, 
            final Identifier firstEntityKey, final Duration exposureTime, 
            MALInteraction interaction) throws MALInteractionException, MALException {
        if (null == firstEntityKey) { // Is the input null?
            throw new IllegalArgumentException("firstEntityKey argument must not be null");
        }

        // Is the requested streaming rate less than the minimum period?
        if (streamingRate.getValue() < minimumPeriod.getValue()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, minimumPeriod));
        }

        // Is the requested streaming rate less than the service lowest minimum period?
        if (streamingRate.getValue() < serviceLowestMinimumPeriod.getValue()) {
            // This is a protection to avoid having crazy implementations with super low streaming rates!
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, serviceLowestMinimumPeriod));
        }

        final PixelResolutionList availableResolutions = adapter.getAvailableResolutions();
        boolean isResolutionAvailable = false;

        // Do we have the resolution requested?
        for (PixelResolution availableResolution : availableResolutions) {
            if (resolution.getHeight().getValue() == availableResolution.getHeight().getValue()
                    && resolution.getWidth().getValue() == availableResolution.getWidth().getValue()) {
                isResolutionAvailable = true;
            }
        }

        // If not, then send the available resolutions to the consumer so they can pick...
        if (!isResolutionAvailable) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, availableResolutions));
        }

        boolean isFormatsAvailable = false;

        // Do we have the resolution requested?
        for (PictureFormat availableFormat : availableFormats) {
            if (format.getNumericValue().getValue() == availableFormat.getNumericValue().getValue()) {
                isFormatsAvailable = true;
            }
        }

        // If not, then send the available formats to the consumer so they can pick...
        if (!isFormatsAvailable) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, availableFormats));
        }

        if (firstEntityKey.getValue() == null
                || "*".equals(firstEntityKey.getValue())
                || "".equals(firstEntityKey.getValue())) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        if (cameraInUse) { // Is the Camera unit in use?
            throw new MALInteractionException(new MALStandardError(PlatformHelper.DEVICE_IN_USE_ERROR_NUMBER, null));
        }

        cameraInUse = true;
        publishTimer.cancel();
        int period = (int) (streamingRate.getValue() * 1000); // In milliseconds

        publishTimer = new Timer();
        publishTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (running) {
                    if (cameraInUse) {
                        streamPicturesUpdate(firstEntityKey, resolution, format, exposureTime);
                    }
                }
            }
        }, period, period);
    }

    @Override
    public synchronized void unsetStreaming(MALInteraction interaction) throws MALInteractionException, MALException {
        cameraInUse = false;
        publishTimer.cancel();
    }

    @Override
    public Picture previewPicture(MALInteraction interaction) throws MALInteractionException, MALException {
        if (cameraInUse) { // Is the Camera unit in use?
            throw new MALInteractionException(new MALStandardError(PlatformHelper.DEVICE_IN_USE_ERROR_NUMBER, null));
        }

        // Get some preview Picture from the camera...
        synchronized (lock) {
            try{
                return adapter.getPicturePreview();
            }catch(IOException ex){
                throw new MALInteractionException(new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
            }
        }
    }

    @Override
    public void takePicture(PixelResolution resolution, PictureFormat format, Duration exposureTime, TakePictureInteraction interaction) throws MALInteractionException, MALException {
        interaction.sendAcknowledgement();

        if (cameraInUse) { // Is the Camera unit in use?
            throw new MALInteractionException(new MALStandardError(PlatformHelper.DEVICE_IN_USE_ERROR_NUMBER, null));
        }

        final PixelResolutionList availableResolutions = adapter.getAvailableResolutions();
        boolean isResolutionAvailable = false;

        // Do we have the resolution requested?
        for (PixelResolution availableResolution : availableResolutions) {
            if (resolution.getHeight().getValue() == availableResolution.getHeight().getValue()
                    && resolution.getWidth().getValue() == availableResolution.getWidth().getValue()) {
                isResolutionAvailable = true;
            }
        }

        // If not, then send the available resolutions to the consumer so they can pick...
        if (!isResolutionAvailable) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, availableResolutions));
        }

        boolean isFormatsAvailable = false;

        // Do we have the resolution requested?
        for (PictureFormat availableFormat : availableFormats) {
            if (format.getNumericValue().getValue() == availableFormat.getNumericValue().getValue()) {
                isFormatsAvailable = true;
            }
        }

        // If not, then send the available formats to the consumer so they can pick...
        if (!isFormatsAvailable) {
            interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, availableFormats));
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, availableFormats));
        }

        synchronized (lock) {
            try {
                Picture picture = adapter.takePicture(resolution, format, exposureTime);

                Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.INFO, "1. The picture has been taken!");
                
                if(!PictureFormat.RAW.equals(format)){
                    final BufferedImage image = adapter.getBufferedImageFromRaw(picture.getContent().getValue());
                    Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.INFO, "2. We now have the BufferedImage!");
                    picture = this.convertImage(image, format);
                    Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.INFO, "3. The picture has been converted!");
                }
                
                interaction.sendResponse(picture);
                Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.INFO, "4. The picture has been sent!");
                
                picture = null;
            } catch (IOException ex) {
                interaction.sendError(new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
            }
        }
    }

    @Override
    public GetPropertiesResponse getProperties(MALInteraction interaction) throws MALInteractionException, MALException {
        final PixelResolutionList availableResolutions = adapter.getAvailableResolutions();
        String extraInfo = adapter.getExtraInfo();

        return new GetPropertiesResponse(availableResolutions, availableFormats, extraInfo);
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties)
                throws MALException {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).log(Level.INFO, "Registration Ack: {0}", header.toString());
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(CameraProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    private Picture convertImage(final BufferedImage image, final PictureFormat format) throws IOException {
        Picture newPicture = new Picture();
        newPicture.setCreationDate(HelperTime.getTimestampMillis());
        newPicture.setDimension(new PixelResolution(new UInteger(image.getWidth()), new UInteger(image.getHeight())));

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        
        if (format.equals(PictureFormat.PNG)) {
            ImageIO.write(image, "PNG", stream);
            newPicture.setContent(new Blob(stream.toByteArray()));
            stream.close();
            stream = null;
            newPicture.setFormat(PictureFormat.PNG);
            return newPicture;
        }

        if (format.equals(PictureFormat.BMP)) {
            ImageIO.write(image, "BMP", stream);
            newPicture.setContent(new Blob(stream.toByteArray()));
            stream.close();
            stream = null;
            newPicture.setFormat(PictureFormat.BMP);
            return newPicture;
        }

        if (format.equals(PictureFormat.JPG)) {
            ImageIO.write(image, "JPEG", stream);
            newPicture.setContent(new Blob(stream.toByteArray()));
            stream.close();
            stream = null;
            newPicture.setFormat(PictureFormat.JPG);
            return newPicture;
        }

        throw new IOException("Something went wrong! The Image could not be converted into the selected format.");
    }

}
