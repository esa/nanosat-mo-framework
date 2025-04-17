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

import esa.mo.com.impl.util.COMServicesProvider;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.misc.TaskScheduler;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.AttributeList;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.camera.CameraHelper;
import org.ccsds.moims.mo.platform.camera.CameraServiceInfo;
import org.ccsds.moims.mo.platform.camera.body.GetPropertiesResponse;
import org.ccsds.moims.mo.platform.camera.provider.CameraInheritanceSkeleton;
import org.ccsds.moims.mo.platform.camera.provider.PicturesStreamPublisher;
import org.ccsds.moims.mo.platform.camera.provider.PreprocessPictureInteraction;
import org.ccsds.moims.mo.platform.camera.provider.TakeAutoExposedPictureInteraction;
import org.ccsds.moims.mo.platform.camera.provider.TakePictureInteraction;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;

/**
 * Camera service Provider.
 */
public class CameraProviderServiceImpl extends CameraInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(CameraProviderServiceImpl.class.getName());
    private Duration minimumPeriod;
    private final Duration serviceLowestMinimumPeriod = new Duration(0.01); // 10 millisecods
    private MALProvider cameraServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private PicturesStreamPublisher publisher;
    private boolean cameraInUse;
    private final Object lock = new Object();
    private boolean isRegistered = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private TaskScheduler publishTimer = new TaskScheduler(1);
    private final AtomicLong uniqueObjId = new AtomicLong(System.currentTimeMillis());
    private CameraAdapterInterface adapter;
    private PictureFormatList availableFormats;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param adapter The Camera adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, CameraAdapterInterface adapter) throws MALException {
        long timestamp = System.currentTimeMillis();
        publisher = createPicturesStreamPublisher(ConfigurationProviderSingleton.getDomain(),
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
        cameraServiceProvider = connection.startService(CameraServiceInfo.CAMERA_SERVICE_NAME.toString(),
            CameraHelper.CAMERA_SERVICE, this);

        availableFormats = adapter.getAvailableFormats();
        running = true;
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Camera service: READY! (" + timestamp + " ms)");
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
            LOGGER.log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }
    }

    private void streamPicturesUpdate(final Identifier firstEntityKey,
            final CameraSettings settings) {
        try {
            final Long objId;
            Picture picture = null;

            synchronized (lock) {
                if (!isRegistered) {
                    IdentifierList keys = new IdentifierList();
                    keys.add(new Identifier("firstEntityKey"));
                    keys.add(new Identifier("objId"));
                    keys.add(new Identifier("width"));
                    keys.add(new Identifier("height"));
                    AttributeTypeList keyTypes = new AttributeTypeList();
                    keyTypes.add(AttributeType.IDENTIFIER);
                    keyTypes.add(AttributeType.LONG);
                    keyTypes.add(AttributeType.LONG);
                    keyTypes.add(AttributeType.LONG);
                    publisher.register(keys, keyTypes, new PublishInteractionListener());
                    isRegistered = true;
                }

                objId = uniqueObjId.incrementAndGet();
                try {
                    picture = adapter.takePicture(settings);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }

            if (picture == null) {
                LOGGER.log(Level.FINE, "Could not retrieve a picture. Skipping publishing.");
            } else {
                LOGGER.log(Level.FINER, "Generating streaming Picture update with objId: {0}", objId);

                AttributeList keys = new AttributeList(); 
                keys.add(firstEntityKey);
                keys.addAsJavaType(objId);
                keys.addAsJavaType(settings.getResolution().getWidth().getValue());
                keys.addAsJavaType(settings.getResolution().getHeight().getValue());

                URI source = connection.getConnectionDetails().getProviderURI();
                UpdateHeader updateHeader = new UpdateHeader(new Identifier(source.getValue()), 
                        connection.getConnectionDetails().getDomain(), keys.getAsNullableAttributeList());

                publisher.publish(updateHeader, picture);
            }
        } catch (IllegalArgumentException | MALException | MALInteractionException ex) {
            LOGGER.log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

    private void isCapturePossible(final CameraSettings settings) throws MALInteractionException {
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MOErrorException(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER,
                null));
        }
        if (cameraInUse) { // Is the Camera unit in use?
            throw new MALInteractionException(new MOErrorException(PlatformHelper.DEVICE_IN_USE_ERROR_NUMBER, null));
        }
        final PixelResolutionList availableResolutions = adapter.getAvailableResolutions();

        if (adapter.hasFixedResolutions()) {
            boolean isResolutionAvailable = false;

            // Do we have the resolution requested?
            for (PixelResolution availableResolution : availableResolutions) {
                if (settings.getResolution().equals(availableResolution)) {
                    isResolutionAvailable = true;
                    break;
                }
            }

            // If not, then send the available resolutions to the consumer so they can pick...
            if (!isResolutionAvailable) {
                throw new MALInteractionException(new MOErrorException(COMHelper.INVALID_ERROR_NUMBER,
                    availableResolutions));
            }
        }

        boolean isFormatsAvailable = false;
        // Do we have the resolution requested?
        for (PictureFormat availableFormat : availableFormats) {
            if (settings.getFormat().getValue() == availableFormat.getValue()) {
                isFormatsAvailable = true;
            }
        }

        // If not, then send the available formats to the consumer so they can pick...
        if (!isFormatsAvailable) {
            throw new MALInteractionException(new MOErrorException(COMHelper.INVALID_ERROR_NUMBER, availableFormats));
        }
    }

    @Override
    public void enableStream(Boolean enable, final Duration streamingRate,
            final Identifier firstEntityKey, final CameraSettings settings,
            MALInteraction interaction) throws MALInteractionException, MALException {
        if (!enable) {
            cameraInUse = false;
            publishTimer.stopLast();
        } else {
            if (null == firstEntityKey) { // Is the input null?
                throw new IllegalArgumentException("firstEntityKey argument must not be null");
            }

            // Is the requested streaming rate less than the minimum period?
            if (streamingRate.getValue() < minimumPeriod.getValue()) {
                throw new MALInteractionException(new MOErrorException(COMHelper.INVALID_ERROR_NUMBER, minimumPeriod));
            }

            // Is the requested streaming rate less than the service lowest minimum period?
            if (streamingRate.getValue() < serviceLowestMinimumPeriod.getValue()) {
                // This is a protection to avoid having crazy implementations with super low streaming rates!
                throw new MALInteractionException(new MOErrorException(COMHelper.INVALID_ERROR_NUMBER,
                    serviceLowestMinimumPeriod));
            }

            isCapturePossible(settings);

            if (firstEntityKey.getValue() == null || "*".equals(firstEntityKey.getValue()) || "".equals(firstEntityKey
                .getValue())) {
                throw new MALInteractionException(new MOErrorException(COMHelper.INVALID_ERROR_NUMBER, null));
            }

            cameraInUse = true;
            publishTimer.stopLast();
            int period = (int) (streamingRate.getValue() * 1000); // In milliseconds

            //publishTimer = new TaskScheduler(1);
            publishTimer.scheduleTask(new Thread(() -> {
                if (running) {
                    if (cameraInUse) {
                        streamPicturesUpdate(firstEntityKey, settings);
                    }
                }
            }), period, period, TimeUnit.MILLISECONDS, true);
        }
    }

    @Override
    public Picture previewPicture(MALInteraction interaction)
            throws MALInteractionException, MALException {
        if (!adapter.isUnitAvailable()) {
            throw new MALInteractionException(new MOErrorException(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER,
                null));
        }
        // Get some preview Picture from the camera...
        synchronized (lock) {
            try {
                return adapter.getPicturePreview();
            } catch (IOException ex) {
                throw new MALInteractionException(new MOErrorException(
                        PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
            }
        }
    }

    @Override
    public void takePicture(final CameraSettings settings, TakePictureInteraction interaction)
            throws MALInteractionException, MALException {
        isCapturePossible(settings);
        interaction.sendAcknowledgement();

        synchronized (lock) {
            try {
                Picture picture = adapter.takePicture(settings);
                LOGGER.log(Level.INFO, "The picture has been taken!");
                interaction.sendResponse(picture);
                LOGGER.log(Level.INFO, "The picture has been sent!");
            } catch (IOException ex) {
                interaction.sendError(new MOErrorException(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
            }
        }
    }

    @Override
    public void takeAutoExposedPicture(CameraSettings settings,
            TakeAutoExposedPictureInteraction interaction) throws MALInteractionException, MALException {
        isCapturePossible(settings);
        interaction.sendAcknowledgement();
        synchronized (lock) {
            try {
                Picture picture = adapter.takeAutoExposedPicture(settings);
                LOGGER.log(Level.INFO, "The picture has been taken!");
                interaction.sendResponse(picture);
                LOGGER.log(Level.INFO, "The picture has been sent!");
            } catch (IOException ex) {
                interaction.sendError(new MOErrorException(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
            }
        }
    }

    @Override
    public GetPropertiesResponse getProperties(MALInteraction interaction)
            throws MALInteractionException, MALException {
        final PixelResolutionList availableResolutions = adapter.getAvailableResolutions();
        String extraInfo = adapter.getExtraInfo();
        return new GetPropertiesResponse(availableResolutions, availableFormats, extraInfo);
    }

    @Override
    public void preprocessPicture(Picture inputPicture, CameraSettings settings,
            PreprocessPictureInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            LOGGER.fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            LOGGER.log(Level.INFO, "Registration Ack: {0}", header.toString());
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }
}
