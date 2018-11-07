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

import esa.mo.helpertools.connections.ConnectionProvider;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.opticaldatareceiver.OpticalDataReceiverHelper;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.RecordSamplesInteraction;

/**
 * Optical Data Receiver service Provider.
 */
public class OpticalDataReceiverProviderServiceImpl extends OpticalDataReceiverInheritanceSkeleton
{

  private MALProvider opticalDataReceiverServiceProvider;
  private boolean initialiased = false;
  private final Object lock = new Object();
  private boolean isRegistered = false;
  private final ConnectionProvider connection = new ConnectionProvider();
  private Timer publishTimer = new Timer();
  private final AtomicLong uniqueObjId = new AtomicLong(System.currentTimeMillis());
  private OpticalDataReceiverAdapterInterface adapter;

  /**
   * Initializes the Software-defined Radio service.
   *
   * @param adapter The Camera adapter
   * @throws MALException On initialisation error.
   */
  public synchronized void init(OpticalDataReceiverAdapterInterface adapter) throws MALException
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
        OpticalDataReceiverHelper.init(MALContextFactory.getElementFactoryRegistry());
      } catch (MALException ex) { // nothing to be done..
      }
    }

    // Shut down old service transport
    if (null != opticalDataReceiverServiceProvider) {
      connection.closeAll();
    }

    this.adapter = adapter;
    opticalDataReceiverServiceProvider = connection.startService(
        OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE_NAME.toString(),
        OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE, this);

    initialiased = true;
    Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).info(
        "Optical Data Receiver service READY");
  }

  /**
   * Closes all running threads and releases the MAL resources.
   */
  public void close()
  {
    try {
      if (null != opticalDataReceiverServiceProvider) {
        opticalDataReceiverServiceProvider.close();
      }
      connection.closeAll();
    } catch (MALException ex) {
      Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).log(Level.WARNING,
          "Exception during close down of the provider {0}", ex);
    }
  }

  @Override
  public void recordSamples(Duration recordingDuration, RecordSamplesInteraction interaction) throws
      MALInteractionException, MALException
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
