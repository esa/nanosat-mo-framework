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
package esa.mo.mc.impl.proxy;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.consumer.ActionConsumerServiceImpl;
import esa.mo.mc.impl.provider.ActionManager;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.consumer.ActionAdapter;
import org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequestList;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 *
 */
public class ActionProxyServiceImpl extends ActionInheritanceSkeleton {

    private MALProvider actionServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private ActionManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private ActionConsumerServiceImpl actionConsumer;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param localCOMServices
     * @param actionConsumer
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider localCOMServices, ActionConsumerServiceImpl actionConsumer)
        throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
                CommonHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION)
                .getServiceByName(ConfigurationHelper.CONFIGURATION_SERVICE_NAME) == null) {
                ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION).getServiceByName(
                ActionHelper.ACTION_SERVICE_NAME) == null) {
                ActionHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
        }

        // Shut down old service transport
        if (null != actionServiceProvider) {
            connection.closeAll();
        }

        Random random = new Random();
        String name = ActionHelper.ACTION_SERVICE_NAME.toString() + "_" + random.nextInt();
        actionServiceProvider = connection.startService(name, ActionHelper.ACTION_SERVICE, false, this);

        running = true;
        this.manager = new ActionManager(localCOMServices, null);
        this.actionConsumer = actionConsumer;

        initialiased = true;
        Logger.getLogger(ActionProxyServiceImpl.class.getName()).info("Action service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != actionServiceProvider) {
                actionServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ActionProxyServiceImpl.class.getName()).log(Level.WARNING,
                "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public void submitAction(Long actionInstId, ActionInstanceDetails actionDetails, MALInteraction interaction)
        throws MALInteractionException, MALException {
        // Publish Activity Tracking event: Reception Event
        manager.getCOMServices().getActivityTrackingService().publishReceptionEvent(interaction, true, new Duration(0),
            actionConsumer.getConnectionDetails().getProviderURI(), null);

        actionConsumer.getActionStub().asyncSubmitAction(actionInstId, actionDetails, new ActionAdapter() {
            @Override
            public void submitActionAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
                // Expected!
            }

            @Override
            public void submitActionErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                Map qosProperties) {
                Logger.getLogger(ActionProxyServiceImpl.class.getName()).log(Level.WARNING,
                    "The Action could not be submitted to the provider. {0}", error);
            }
        });

        // Publish Activity Tracking event: Forward Event
        manager.getCOMServices().getActivityTrackingService().publishForwardEvent(interaction, true, new Duration(0),
            actionConsumer.getConnectionDetails().getProviderURI(), null);
    }

    @Override
    public Boolean preCheckAction(ActionInstanceDetails actionDetails, MALInteraction interaction)
        throws MALInteractionException, MALException {
        UIntegerList invIndexList = new UIntegerList();

        // 3.2.10.3.2
        if (!manager.existsDef(actionDetails.getDefInstId())) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        // 3.2.10.2.c
        boolean accepted = manager.checkActionInstanceDetails(actionDetails, invIndexList);

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.2.9.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        return accepted;
    }

    @Override
    public ObjectInstancePairList listDefinition(IdentifierList il, MALInteraction mali) throws MALInteractionException,
        MALException {
        return actionConsumer.getActionStub().listDefinition(il);
    }

    @Override
    public ObjectInstancePairList addAction(ActionCreationRequestList acrl, MALInteraction mali)
        throws MALInteractionException, MALException {
        return actionConsumer.getActionStub().addAction(acrl);
    }

    @Override
    public LongList updateDefinition(LongList ll, ActionDefinitionDetailsList addl, MALInteraction mali)
        throws MALInteractionException, MALException {
        return actionConsumer.getActionStub().updateDefinition(ll, addl);
    }

    @Override
    public void removeAction(LongList ll, MALInteraction mali) throws MALInteractionException, MALException {
        actionConsumer.getActionStub().removeAction(ll);
    }

}
