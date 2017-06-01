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
package esa.mo.sm.impl.consumer;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.misc.ConsumerServiceImpl;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.consumer.MALConsumer;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.HeartbeatHelper;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatAdapter;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatStub;

/**
 *
 * @author Cesar Coelho
 */
public class HeartbeatConsumerServiceImpl extends ConsumerServiceImpl {

    private HeartbeatStub heartbeatService = null;
    private COMServicesConsumer comServices;
    private Subscription heartbeatSubscription = null;

    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    @Override
    public Object getStub() {
        return this.getHeartbeatStub();
    }

    public HeartbeatStub getHeartbeatStub() {
        return this.heartbeatService;
    }

    @Override
    public Object generateServiceStub(MALConsumer tmConsumer) {
        return new HeartbeatStub(tmConsumer);
    }

    public HeartbeatConsumerServiceImpl(SingleConnectionDetails connectionDetails, COMServicesConsumer comServices) throws MALException, MalformedURLException, MALInteractionException {

        if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
            COMHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME, SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) == null) {
            SoftwareManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
        }

        try {
            HeartbeatHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // nothing to be done..
        }

        this.connectionDetails = connectionDetails;
        this.comServices = comServices;

        // Close old connection
        if (tmConsumer != null) {
            try {
                tmConsumer.close();
            } catch (MALException ex) {
                Logger.getLogger(HeartbeatConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        tmConsumer = connection.startService(
                this.connectionDetails.getProviderURI(),
                this.connectionDetails.getBrokerURI(),
                this.connectionDetails.getDomain(),
                HeartbeatHelper.HEARTBEAT_SERVICE);

        this.heartbeatService = new HeartbeatStub(tmConsumer);
    }

    /**
     * Quick method to start listening for a heartbeat coming from the provider.
     * The registration to the service is handled by this class. For more
     * registrations, the developer is advised to use its own registration
     * mechanisms to the provider.
     *
     * @param adapter
     */
    public void startListening(final HeartbeatAdapter adapter) {
        if (heartbeatSubscription == null) {
            heartbeatSubscription = ConnectionConsumer.subscriptionWildcardRandom();
            try {
                heartbeatService.beatRegister(heartbeatSubscription, adapter);
            } catch (MALInteractionException ex) {
                Logger.getLogger(HeartbeatConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(HeartbeatConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Quick method to stop listening for a heartbeat coming from the provider.
     * The registration to the service is handled by this class. For more
     * registrations, the developer is advised to use its own registration
     * mechanisms to the provider.
     *
     */
    public void stopListening() {
        if (heartbeatSubscription != null) {
            try {
                IdentifierList ids = new IdentifierList();
                ids.add(heartbeatSubscription.getSubscriptionId());
                heartbeatService.beatDeregister(ids);
            } catch (MALInteractionException ex) {
                Logger.getLogger(HeartbeatConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(HeartbeatConsumerServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
