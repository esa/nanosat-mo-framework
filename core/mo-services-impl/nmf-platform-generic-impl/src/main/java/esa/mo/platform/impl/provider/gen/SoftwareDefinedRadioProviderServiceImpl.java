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

import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import org.ccsds.moims.mo.platform.softwaredefinedradio.SoftwareDefinedRadioHelper;
import org.ccsds.moims.mo.platform.softwaredefinedradio.provider.SoftwareDefinedRadioInheritanceSkeleton;
import org.ccsds.moims.mo.platform.softwaredefinedradio.provider.StreamRadioPublisher;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponents;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.IQComponentsList;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.SDRConfiguration;

/**
 * Software-defined Radio service Provider.
 */
public class SoftwareDefinedRadioProviderServiceImpl extends SoftwareDefinedRadioInheritanceSkeleton
{

  private MALProvider softwareDefinedRadioServiceProvider;
  private boolean initialiased = false;
  private boolean running = false;
  private boolean sdrInUse;
  private StreamRadioPublisher publisher;
  private final Object lock = new Object();
  private boolean isRegistered = false;
  private final ConnectionProvider connection = new ConnectionProvider();
  private Timer publishTimer = new Timer();
  private final AtomicLong uniqueObjId = new AtomicLong(System.currentTimeMillis());
  private SoftwareDefinedRadioAdapterInterface adapter;

  /**
   * Initializes the Software-defined Radio service.
   *
   * @param adapter The SDR adapter
   * @throws MALException On initialisation error.
   */
  public synchronized void init(SoftwareDefinedRadioAdapterInterface adapter) throws MALException
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
        SoftwareDefinedRadioHelper.init(MALContextFactory.getElementFactoryRegistry());
      } catch (MALException ex) { // nothing to be done..
      }
    }

    publisher = createStreamRadioPublisher(ConfigurationProviderSingleton.getDomain(),
        ConfigurationProviderSingleton.getNetwork(),
        SessionType.LIVE,
        ConfigurationProviderSingleton.getSourceSessionName(),
        QoSLevel.BESTEFFORT,
        null,
        new UInteger(0));

    // Shut down old service transport
    if (null != softwareDefinedRadioServiceProvider) {
      connection.closeAll();
    }

    this.adapter = adapter;
    softwareDefinedRadioServiceProvider = connection.startService(
        SoftwareDefinedRadioHelper.SOFTWAREDEFINEDRADIO_SERVICE_NAME.toString(),
        SoftwareDefinedRadioHelper.SOFTWAREDEFINEDRADIO_SERVICE, this);

    running = true;
    initialiased = true;
    Logger.getLogger(SoftwareDefinedRadioProviderServiceImpl.class.getName()).info(
        "Software-defined Radio service READY");
  }

  /**
   * Closes all running threads and releases the MAL resources.
   */
  public void close()
  {
    try {
      if (null != softwareDefinedRadioServiceProvider) {
        softwareDefinedRadioServiceProvider.close();
      }

      connection.closeAll();
      running = false;
    } catch (MALException ex) {
      Logger.getLogger(SoftwareDefinedRadioProviderServiceImpl.class.getName()).log(Level.WARNING,
          "Exception during close down of the provider {0}", ex);
    }
  }

  private void streamRadioUpdate()
  {
    try {
      final long objId;
      final IQComponentsList iqComponentsList = new IQComponentsList(1);

      synchronized (lock) {
        if (!isRegistered) {
          final EntityKeyList lst = new EntityKeyList();
          lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
          publisher.register(lst, new PublishInteractionListener());
          isRegistered = true;
        }
        final IQComponents iqComponents = adapter.getIQComponents();
        if (iqComponents == null) {
          return;
        }
        iqComponentsList.add(iqComponents);
        objId = uniqueObjId.incrementAndGet();
      }

      Logger.getLogger(SoftwareDefinedRadioProviderServiceImpl.class.getName()).log(Level.FINER,
          "Generating streaming Radio update with objId: " + objId);

      final EntityKey ekey = new EntityKey(null, null, null, null);
      final UpdateHeaderList hdrlst = new UpdateHeaderList();
      hdrlst.add(new UpdateHeader(HelperTime.getTimestampMillis(),
          connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));

      publisher.publish(hdrlst, iqComponentsList);

    } catch (IllegalArgumentException | MALInteractionException | MALException ex) {
      Logger.getLogger(SoftwareDefinedRadioProviderServiceImpl.class.getName()).log(Level.WARNING,
          "Exception during publishing process on the provider {0}", ex);
    }
  }

  @Override
  public synchronized void enableSDR(final Boolean enable,
      final SDRConfiguration initialConfiguration, final Duration publishingPeriod,
      final MALInteraction interaction) throws MALInteractionException, MALException
  {
    publishTimer.cancel();

    if (!enable) {
      sdrInUse = false;
    } else {
      if (!adapter.isUnitAvailable()) {
        throw new MALInteractionException(new MALStandardError(
            PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
      }
      if (!adapter.setConfiguration(initialConfiguration)) {
        throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER,
            null));
      }
      sdrInUse = true;
      int period = (int) (publishingPeriod.getValue() * 1000); // In milliseconds

      publishTimer = new Timer();
      publishTimer.schedule(new TimerTask()
      {
        @Override
        public void run()
        {
          if (running) {
            if (sdrInUse) {
              streamRadioUpdate();
            }
          }
        }
      }, period, period);
    }
    if (!adapter.enableSDR(enable)) {
      throw new MALInteractionException(
          new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, null));
    }
  }

  @Override
  public synchronized void updateConfiguration(final SDRConfiguration sdrConfiguration,
      final MALInteraction interaction) throws MALInteractionException, MALException
  {
    if (!adapter.isUnitAvailable()) {
      throw new MALInteractionException(new MALStandardError(
          PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
    }
    if (!adapter.setConfiguration(sdrConfiguration)) {
      throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
    }
  }

  public static final class PublishInteractionListener implements MALPublishInteractionListener
  {

    @Override
    public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
        throws MALException
    {
      Logger.getLogger(SoftwareDefinedRadioProviderServiceImpl.class.getName()).fine(
          "PublishInteractionListener::publishDeregisterAckReceived");
    }

    @Override
    public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body,
        final Map qosProperties)
        throws MALException
    {
      Logger.getLogger(SoftwareDefinedRadioProviderServiceImpl.class.getName()).warning(
          "PublishInteractionListener::publishErrorReceived");
    }

    @Override
    public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
        throws MALException
    {
      Logger.getLogger(SoftwareDefinedRadioProviderServiceImpl.class.getName()).log(Level.INFO,
          "Registration Ack: {0}", header.toString());
    }

    @Override
    public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body,
        final Map qosProperties) throws MALException
    {
      Logger.getLogger(SoftwareDefinedRadioProviderServiceImpl.class.getName()).warning(
          "PublishInteractionListener::publishRegisterErrorReceived");
    }
  }

}
