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
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.BlobList;
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
import org.ccsds.moims.mo.platform.opticaldatareceiver.OpticalDataReceiverHelper;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.OpticalDataReceiverInheritanceSkeleton;
import org.ccsds.moims.mo.platform.opticaldatareceiver.provider.StreamDataPublisher;

/**
 * Optical Data Receiver service Provider.
 */
public class OpticalDataReceiverProviderServiceImpl extends OpticalDataReceiverInheritanceSkeleton {

    private MALProvider opticalDataReceiverServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private StreamDataPublisher publisher;
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
    public synchronized void init(OpticalDataReceiverAdapterInterface adapter) throws MALException {
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
                OpticalDataReceiverHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
            }
        }

        publisher = createStreamDataPublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != opticalDataReceiverServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        opticalDataReceiverServiceProvider = connection.startService(OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE_NAME.toString(), OpticalDataReceiverHelper.OPTICALDATARECEIVER_SERVICE, this);

        running = true;
        initialiased = true;
        Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).info("Optical Data Receiver service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != opticalDataReceiverServiceProvider) {
                opticalDataReceiverServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    private void streamDataUpdate() {
        try {
            final Long objId;
            final byte[] data;

            synchronized (lock) {
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }

                objId = uniqueObjId.incrementAndGet();
                data = adapter.getOpticalReceiverData();
            }

            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating streaming Radio update with objId: " + objId);

            final EntityKey ekey = new EntityKey(null, null, null, null);
            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            hdrlst.add(new UpdateHeader(HelperTime.getTimestampMillis(), connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));

            BlobList list = new BlobList();
            list.add(new Blob(data));
            publisher.publish(hdrlst, list);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public synchronized void setPublishingFrequency(Duration publishingPeriod, MALInteraction interaction) throws MALInteractionException, MALException {
        publishTimer.cancel();

        if (publishingPeriod.getValue() != 0) {
            int period = (int) (publishingPeriod.getValue() * 1000); // In milliseconds

            publishTimer = new Timer();
            publishTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (running) {
                        streamDataUpdate();
                    }
                }
            }, period, period);
        }
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {
        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties)
                throws MALException {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).log(Level.INFO, "Registration Ack: {0}", header.toString());
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(OpticalDataReceiverProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

}
