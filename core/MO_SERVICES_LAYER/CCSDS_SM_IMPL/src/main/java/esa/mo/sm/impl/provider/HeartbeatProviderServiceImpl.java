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
package esa.mo.sm.impl.provider;

import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
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
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.HeartbeatHelper;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.provider.BeatPublisher;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.provider.HeartbeatInheritanceSkeleton;

/**
 *
 */
public class HeartbeatProviderServiceImpl extends HeartbeatInheritanceSkeleton {

    private MALProvider heartbeatServiceProvider;
    private BeatPublisher publisher;
    private boolean initialiased = false;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final Timer timer = new Timer("HeartbeatThread");
    protected long period = 10000; // 10 seconds

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @throws MALException On initialisation error.
     */
    public synchronized void init() throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME,
                    SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) == null) {
                SoftwareManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                HeartbeatHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
            }
        }

        publisher = createBeatPublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != heartbeatServiceProvider) {
            connection.closeAll();
        }

        heartbeatServiceProvider = connection.startService(HeartbeatHelper.HEARTBEAT_SERVICE_NAME.toString(),
                HeartbeatHelper.HEARTBEAT_SERVICE, true, this);

        running = true;
        initialiased = true;
        Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).info("Heartbeat service READY");

        // Start the timer to publish the heartbeat
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (running) {
                    publishHeartbeat();
                }
            }
        }, period, period);
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != heartbeatServiceProvider) {
                heartbeatServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }
    }

    private void publishHeartbeat() {
        try {
            synchronized (lock) {
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            final UpdateHeaderList hdrlst = new UpdateHeaderList(1);
            hdrlst.add(
                    new UpdateHeader(
                            HelperTime.getTimestampMillis(),
                            connection.getConnectionDetails().getProviderURI(),
                            UpdateType.UPDATE,
                            new EntityKey(null, null, null, null)
                    )
            );

            publisher.publish(hdrlst);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (0)");
        } catch (MALException ex) {
            Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (1)");
        } catch (MALInteractionException ex) {
            Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (2)");
        }
    }

    @Override
    public Duration getPeriod(MALInteraction interaction) throws MALInteractionException, MALException {
        // Convert to seconds and return the value
        return new Duration(period / 1000);
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties)
                throws MALException {
            Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).log(Level.INFO, "Registration Ack: {0}", header.toString());
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(HeartbeatProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

}
