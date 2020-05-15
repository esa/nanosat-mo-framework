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
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.misc.TaskScheduler;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
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
import org.ccsds.moims.mo.platform.camera.provider.PicturesStreamPublisher;
import org.ccsds.moims.mo.platform.camera.provider.TakeAutoExposedPictureInteraction;
import org.ccsds.moims.mo.platform.camera.provider.TakePictureInteraction;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormatList;
import org.ccsds.moims.mo.platform.camera.structures.PictureList;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolutionList;

/**
 * Camera service Provider.
 */
public class CameraProviderServiceImpl extends CameraInheritanceSkeleton
{

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
   * creates the MAL objects, the publisher used to create updates and starts the publishing thread
   *
   * @param comServices
   * @param adapter     The Camera adapter
   * @throws MALException On initialisation error.
   */
  public synchronized void init(COMServicesProvider comServices, CameraAdapterInterface adapter)
      throws MALException
  {
    if (!initialiased) {
      if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
        MALHelper.init(MALContextFactory.getElementFactoryRegistry());
      }

      if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME,
          PlatformHelper.PLATFORM_AREA_VERSION) == null) {
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
    cameraServiceProvider = connection.startService(CameraHelper.CAMERA_SERVICE_NAME.toString(),
        CameraHelper.CAMERA_SERVICE, this);

    availableFormats = adapter.getAvailableFormats();
    running = true;
    initialiased = true;
    LOGGER.info("Camera service READY");
  }

  /**
   * Closes all running threads and releases the MAL resources.
   */
  public void close()
  {
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
      final CameraSettings settings)
  {
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
          picture = adapter.takePicture(settings);
        } catch (IOException ex) {
          LOGGER.log(Level.SEVERE, null, ex);
        }
      }

      LOGGER.log(Level.FINER, "Generating streaming Picture update with objId: {0}", objId);

      final EntityKey ekey = new EntityKey(firstEntityKey, objId,
          settings.getResolution().getWidth().getValue(),
          settings.getResolution().getHeight().getValue());

      final UpdateHeaderList hdrlst = new UpdateHeaderList();
      hdrlst.add(new UpdateHeader(HelperTime.getTimestampMillis(),
          connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));

      PictureList picList = new PictureList();
      picList.add(picture);
      publisher.publish(hdrlst, picList);
    } catch (IllegalArgumentException | MALException | MALInteractionException ex) {
      LOGGER.log(Level.WARNING,
          "Exception during publishing process on the provider {0}", ex);
    }
  }

  private void isCapturePossible(final CameraSettings settings) throws MALInteractionException
  {
    if (!adapter.isUnitAvailable()) {
      throw new MALInteractionException(new MALStandardError(
          PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
    }
    if (cameraInUse) { // Is the Camera unit in use?
      throw new MALInteractionException(new MALStandardError(
          PlatformHelper.DEVICE_IN_USE_ERROR_NUMBER, null));
    }
    final PixelResolutionList availableResolutions = adapter.getAvailableResolutions();
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
      throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER,
          availableResolutions));
    }

    boolean isFormatsAvailable = false;
    // Do we have the resolution requested?
    for (PictureFormat availableFormat : availableFormats) {
      if (settings.getFormat().getNumericValue().getValue() == availableFormat.getNumericValue().getValue()) {
        isFormatsAvailable = true;
      }
    }

    // If not, then send the available formats to the consumer so they can pick...
    if (!isFormatsAvailable) {
      throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER,
          availableFormats));
    }
  }

  @Override
  public void enableStream(Boolean enable, final Duration streamingRate,
      final Identifier firstEntityKey,
      final CameraSettings settings, MALInteraction interaction) throws MALInteractionException,
      MALException
  {
    if (enable == false) {
      cameraInUse = false;
      publishTimer.stopLast();
    } else {
      if (null == firstEntityKey) { // Is the input null?
        throw new IllegalArgumentException("firstEntityKey argument must not be null");
      }

      // Is the requested streaming rate less than the minimum period?
      if (streamingRate.getValue() < minimumPeriod.getValue()) {
        throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER,
            minimumPeriod));
      }

      // Is the requested streaming rate less than the service lowest minimum period?
      if (streamingRate.getValue() < serviceLowestMinimumPeriod.getValue()) {
        // This is a protection to avoid having crazy implementations with super low streaming rates!
        throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER,
            serviceLowestMinimumPeriod));
      }

      isCapturePossible(settings);

      if (firstEntityKey.getValue() == null
          || "*".equals(firstEntityKey.getValue())
          || "".equals(firstEntityKey.getValue())) {
        throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
      }

      cameraInUse = true;
      publishTimer.stopLast();
      int period = (int) (streamingRate.getValue() * 1000); // In milliseconds

      //publishTimer = new TaskScheduler(1);
      publishTimer.scheduleTask(new Thread()
      {
        @Override
        public void run()
        {
          if (running) {
            if (cameraInUse) {
              streamPicturesUpdate(firstEntityKey, settings);
            }
          }
        }
      }, period, period, TimeUnit.MILLISECONDS, true);
    }
  }

  @Override
  public Picture previewPicture(MALInteraction interaction) throws MALInteractionException,
      MALException
  {
    if (!adapter.isUnitAvailable()) {
      throw new MALInteractionException(new MALStandardError(
          PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
    }
    // Get some preview Picture from the camera...
    synchronized (lock) {
      try {
        return adapter.getPicturePreview();
      } catch (IOException ex) {
        throw new MALInteractionException(new MALStandardError(
            PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
      }
    }
  }

  @Override
  public void takePicture(final CameraSettings settings, TakePictureInteraction interaction) throws
      MALInteractionException, MALException
  {
    isCapturePossible(settings);
    interaction.sendAcknowledgement();

    synchronized (lock) {
      try {
        Picture picture = adapter.takePicture(settings);

        LOGGER.log(Level.INFO,
            "The picture has been taken!");

        interaction.sendResponse(picture);
        LOGGER.log(Level.INFO,
            "The picture has been sent!");

      } catch (IOException ex) {
        interaction.sendError(new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER,
            null));
      }
    }
  }

  @Override
  public void takeAutoExposedPicture(CameraSettings settings,
      TakeAutoExposedPictureInteraction interaction) throws MALInteractionException, MALException
  {
    isCapturePossible(settings);
    interaction.sendAcknowledgement();
    synchronized (lock) {
      try {
        Picture picture = adapter.takeAutoExposedPicture(settings);

        LOGGER.log(Level.INFO,
            "The picture has been taken!");

        interaction.sendResponse(picture);
        LOGGER.log(Level.INFO,
            "The picture has been sent!");

      } catch (IOException ex) {
        interaction.sendError(new MALStandardError(PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER,
            null));
      }
    }
  }

  @Override
  public GetPropertiesResponse getProperties(MALInteraction interaction) throws
      MALInteractionException, MALException
  {
    final PixelResolutionList availableResolutions = adapter.getAvailableResolutions();
    String extraInfo = adapter.getExtraInfo();

    return new GetPropertiesResponse(availableResolutions, availableFormats, extraInfo);
  }

  public static final class PublishInteractionListener implements MALPublishInteractionListener
  {

    @Override
    public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
        throws MALException
    {
      LOGGER.fine(
          "PublishInteractionListener::publishDeregisterAckReceived");
    }

    @Override
    public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body,
        final Map qosProperties)
        throws MALException
    {
      LOGGER.warning(
          "PublishInteractionListener::publishErrorReceived");
    }

    @Override
    public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
        throws MALException
    {
      LOGGER.log(Level.INFO,
          "Registration Ack: {0}", header.toString());
    }

    @Override
    public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body,
        final Map qosProperties) throws MALException
    {
      LOGGER.warning(
          "PublishInteractionListener::publishRegisterErrorReceived");
    }
  }
}
