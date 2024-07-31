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
package esa.mo.mp.impl.provider;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.MPHelper;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.planningrequest.body.GetRequestStatusResponse;
import org.ccsds.moims.mo.mp.planningrequest.body.SubmitRequestResponse;
import org.ccsds.moims.mo.mp.planningrequest.provider.GetRequestInteraction;
import org.ccsds.moims.mo.mp.planningrequest.provider.MonitorRequestsPublisher;
import org.ccsds.moims.mo.mp.planningrequest.provider.PlanningRequestInheritanceSkeleton;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.ccsds.moims.mo.mp.structures.RequestFilter;
import org.ccsds.moims.mo.mp.structures.RequestStatus;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetailsList;
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.callback.MPServiceOperation;
import esa.mo.mp.impl.callback.MPServiceOperationHelper;
import esa.mo.mp.impl.callback.MPServiceOperationManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;

/**
 * Planning Request (PRS) Service provider implementation.
 * Overridden methods contain the operation implementation.
 */
public class PlanningRequestProviderServiceImpl extends PlanningRequestInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(PlanningRequestProviderServiceImpl.class.getName());

    private MALProvider provider;
    private boolean initialised = false;
    private MonitorRequestsPublisher requestPublisher;
    private boolean isPublisherRegistered = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private MPServiceOperationManager operationCallbackManager;

    private MPArchiveManager archiveManager;

    /**
     * creates the MAL objects
     *
     * @param comServices
     * @throws MALException            On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, MPArchiveManager archiveManager, MPServiceOperationManager registration) throws MALException {
        long timestamp = System.currentTimeMillis();
        
        if (!this.initialised) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MPHelper.MP_AREA_NAME, MPHelper.MP_AREA_VERSION) == null) {
                MPHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                PlanningRequestHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        // Shut down old service transport
        if (this.provider != null) {
            this.connection.closeAll();
        }

        this.provider = this.connection.startService(PlanningRequestHelper.PLANNINGREQUEST_SERVICE_NAME.toString(),
            PlanningRequestHelper.PLANNINGREQUEST_SERVICE, true, this);

        requestPublisher = createMonitorRequestsPublisher(ConfigurationProviderSingleton.getDomain(),
            ConfigurationProviderSingleton.getNetwork(), SessionType.LIVE, ConfigurationProviderSingleton
                .getSourceSessionName(), QoSLevel.BESTEFFORT, null, new UInteger(0));

        this.archiveManager = archiveManager;
        this.operationCallbackManager = registration;

        // Listen Event Service for Request Version updates in COM Archive
        try {
            EventConsumerServiceImpl consumer = new EventConsumerServiceImpl(comServices.getEventService()
                .getConnectionProvider().getConnectionDetails());
            Subscription subcription = HelperCOM.generateCOMEventSubscriptionBySourceType("RequestStatusUpdates",
                PlanningRequestHelper.REQUESTSTATUSUPDATE_OBJECT_TYPE);
            consumer.addEventReceivedListener(subcription, new EventReceivedListener() {
                @Override
                public void onDataReceived(EventCOMObject eventCOMObject) {
                    ObjectId statusId = eventCOMObject.getSource();
                    ObjectId requestVersionId = archiveManager.REQUEST_VERSION.getInstanceIdByStatusId(statusId);
                    ObjectId requestIdentityId = archiveManager.REQUEST_VERSION.getIdentityIdByInstanceId(
                        requestVersionId);
                    Identifier requestIdentity = archiveManager.REQUEST_VERSION.getIdentity(requestIdentityId);
                    RequestUpdateDetails status = archiveManager.REQUEST_VERSION.getStatus(statusId);
                    try {
                        publishRequestUpdate(requestIdentity, requestIdentityId, requestVersionId, status);
                    } catch (IllegalArgumentException | MALInteractionException | MALException e) {
                        LOGGER.warning("Error publishing request update");
                    }
                }
            });
            LOGGER.info("Subscribed to RequestVersion updates");
        } catch (MALInteractionException | MalformedURLException e) {
            throw new MALException(e.getMessage());
        }

        this.initialised = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Planning Request service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (this.provider != null) {
                this.provider.close();
            }

            this.connection.closeAll();
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public SubmitRequestResponse submitRequest(Identifier identity, RequestVersionDetails requestVersion,
        MALInteraction interaction) throws MALInteractionException, MALException {
        // Validate request template
        validateRequestTemplate(requestVersion);

        // Validation callback
        operationCallbackManager.notifyRequestValidation(MPServiceOperation.SUBMIT_REQUEST, requestVersion);

        // Store Request Identity and Request Version to COM Archive
        ObjectIdPair pair = this.archiveManager.REQUEST_VERSION.addInstance(identity, requestVersion, null,
            interaction);
        ObjectId requestIdentityId = pair.getIdentityId();
        ObjectId requestVersionId = pair.getObjectId();

        // Store Request Update to COM Archive
        RequestUpdateDetails status = new RequestUpdateDetails();
        status.setRequestId(requestVersionId);
        status.setStatus(RequestStatus.REQUESTED);
        status.setTimestamp(HelperTime.getTimestampMillis());
        ObjectId statusId = this.archiveManager.REQUEST_VERSION.addStatus(requestVersionId, status, null, interaction);

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.SUBMIT_REQUEST, MPServiceOperationHelper.asArgumentsList(pair
            .getIdentityId(), // Identity
            null,                 // Definition
            pair.getObjectId(),   // Instance
            statusId,             // Status
            interaction, null                  // Source
        ));

        // Send response to consumer
        SubmitRequestResponse response = new SubmitRequestResponse();

        Long identityInstanceId = COMObjectIdHelper.getInstanceId(requestIdentityId);
        Long versionInstanceId = COMObjectIdHelper.getInstanceId(requestVersionId);

        response.setBodyElement0(identityInstanceId);
        response.setBodyElement1(versionInstanceId);
        return response;
    }

    @Override
    public Long updateRequest(Long requestIdentityId, RequestVersionDetails requestVersion, MALInteraction interaction)
        throws MALInteractionException, MALException {
        // Validate request template
        validateRequestTemplate(requestVersion);

        // Validation callback
        operationCallbackManager.notifyRequestValidation(MPServiceOperation.UPDATE_REQUEST, requestVersion);

        // Store updated request version to COM Archive
        ObjectId identityId = COMObjectIdHelper.getObjectId(requestIdentityId,
            PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
        ObjectId requestVersionId = this.archiveManager.REQUEST_VERSION.updateInstance(identityId, requestVersion, null,
            interaction);

        // Store updated request version update to COM Archive
        RequestUpdateDetails status = new RequestUpdateDetails();
        status.setRequestId(requestVersionId);
        status.setStatus(RequestStatus.REQUESTED);
        status.setTimestamp(HelperTime.getTimestampMillis());
        ObjectId statusId = this.archiveManager.REQUEST_VERSION.addStatus(requestVersionId, status, null, interaction);

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.UPDATE_REQUEST, MPServiceOperationHelper.asArgumentsList(
            identityId,       // Identity
            null,             // Definition
            requestVersionId, // Instance
            statusId,         // Status
            interaction, null              // Source
        ));

        // Send response to consumer
        Long updatedInstanceId = COMObjectIdHelper.getInstanceId(requestVersionId);
        return updatedInstanceId;
    }

    @Override
    public void cancelRequest(Long requestIdentityId, MALInteraction interaction) throws MALInteractionException,
        MALException {
        // Find request version
        ObjectId identityId = COMObjectIdHelper.getObjectId(requestIdentityId,
            PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
        ObjectId requestVersionId = archiveManager.REQUEST_VERSION.getInstanceIdByIdentityId(identityId);

        // Store updated request version update to COM Archive
        RequestUpdateDetails status = new RequestUpdateDetails();
        status.setRequestId(requestVersionId);
        status.setStatus(RequestStatus.CANCELLED);
        status.setTimestamp(HelperTime.getTimestampMillis());
        ObjectId statusId = this.archiveManager.REQUEST_VERSION.updateStatus(requestVersionId, status, null,
            interaction);

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.CANCEL_REQUEST, MPServiceOperationHelper.asArgumentsList(
            identityId,       // Identity
            null,             // Definition
            requestVersionId, // Instance
            statusId,         // Status
            interaction, null              // Source
        ));
    }

    @Override
    public GetRequestStatusResponse getRequestStatus(RequestFilter requestFilter, MALInteraction interaction)
        throws MALInteractionException, MALException {
        LongList identityIdList = new LongList();
        LongList instanceIdList = new LongList();
        RequestUpdateDetailsList statusList = new RequestUpdateDetailsList();

        // List all request versions and find
        ObjectIdList identityIds = archiveManager.REQUEST_VERSION.listAllIdentityIds();
        ObjectIdList versionIds = archiveManager.REQUEST_VERSION.getInstanceIdsByIdentityIds(identityIds);
        ObjectIdList statusIds = archiveManager.REQUEST_VERSION.getStatusIdsByInstanceIds(versionIds);
        for (int index = 0; index < statusIds.size(); index++) {
            ObjectId identityId = identityIds.get(index);
            ObjectId versionId = versionIds.get(index);
            ObjectId statusId = statusIds.get(index);
            if (!checkRequestFilter(requestFilter, identityId)) {
                // Skip all identities that do not match the filter
                continue;
            }
            RequestUpdateDetails status = archiveManager.REQUEST_VERSION.getStatus(statusId);
            if (versionId == null || status == null) {
                // There is no instance or no status
                continue;
            }
            identityIdList.add(COMObjectIdHelper.getInstanceId(identityId));
            instanceIdList.add(COMObjectIdHelper.getInstanceId(versionId));
            statusList.add(status);
        }

        // Send response to consumer
        GetRequestStatusResponse response = new GetRequestStatusResponse();
        response.setBodyElement0(identityIdList);
        response.setBodyElement1(instanceIdList);
        response.setBodyElement2(statusList);
        return response;
    }

    @Override
    public void getRequest(RequestFilter requestFilter, GetRequestInteraction interaction)
        throws MALInteractionException, MALException {
        LongList identityIdList = new LongList();
        LongList versionIdList = new LongList();
        RequestVersionDetailsList instanceList = new RequestVersionDetailsList();

        ObjectIdList identityIds = archiveManager.REQUEST_VERSION.listAllIdentityIds();
        ObjectIdList versionIds = archiveManager.REQUEST_VERSION.getInstanceIdsByIdentityIds(identityIds);
        for (int index = 0; index < versionIds.size(); index++) {
            ObjectId identityId = identityIds.get(index);
            ObjectId versionId = versionIds.get(index);
            if (!checkRequestFilter(requestFilter, identityId)) {
                // Skip all requests that do not match the filter
                continue;
            }

            RequestVersionDetails instance = archiveManager.REQUEST_VERSION.getInstance(versionId);
            if (versionId == null || instance == null) {
                // There is no instance or no status
                continue;
            }
            identityIdList.add(COMObjectIdHelper.getInstanceId(identityId));
            versionIdList.add(COMObjectIdHelper.getInstanceId(versionId));
            instanceList.add(instance);
        }

        // Send response to consumer
        Short numberOfUpdates = 0;
        interaction.sendAcknowledgement(numberOfUpdates);
        interaction.sendResponse(identityIdList, versionIdList, instanceList);
    }

    private void validateRequestTemplate(RequestVersionDetails requestVersion) throws MALInteractionException {
        if (requestVersion == null || requestVersion.getTemplate() == null)
            return;
        ObjectId requestTemplateId = requestVersion.getTemplate();
        Long requestTemplateInstanceId = COMObjectIdHelper.getInstanceId(requestTemplateId);
        RequestTemplateDetails requestTemplate = archiveManager.REQUEST_TEMPLATE.getDefinition(requestTemplateId);
        if (requestTemplateInstanceId == 0L || requestTemplate == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union(
                "Invalid Request Template Id")));
        }
    }

    private void publishRequestUpdate(Identifier identity, ObjectId identityId, ObjectId versionId,
        RequestUpdateDetails update) throws IllegalArgumentException, MALInteractionException, MALException {
        if (!this.isPublisherRegistered) {
            final EntityKeyList entityKeys = new EntityKeyList();
            entityKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
            requestPublisher.register(entityKeys, new PublishInteractionListener());
            this.isPublisherRegistered = true;
        }

        UpdateHeaderList headerList = new UpdateHeaderList();

        Identifier firstSubKey = identity;
        Long secondSubKey = COMObjectIdHelper.getInstanceId(identityId);
        Long thirdSubKey = COMObjectIdHelper.getInstanceId(versionId);
        Long fourthSubKey = Long.valueOf(update.getStatus().getNumericValue().getValue());

        EntityKey entityKey = new EntityKey(firstSubKey, secondSubKey, thirdSubKey, fourthSubKey);
        headerList.add(new UpdateHeader(update.getTimestamp(), connection.getConnectionDetails().getProviderURI(),
            UpdateType.CREATION, entityKey));
        RequestUpdateDetailsList requestStatusList = new RequestUpdateDetailsList();
        requestStatusList.add(update);

        requestPublisher.publish(headerList, requestStatusList);
    }

    private boolean checkRequestFilter(RequestFilter requestFilter, ObjectId identityId) {
        if (requestFilter.getReturnAll() != null && requestFilter.getReturnAll()) {
            return true;
        }
        boolean match = false;
        // TODO: Check domain
        // TODO: Check source
        // Check identities
        Long identityInstanceId = COMObjectIdHelper.getInstanceId(identityId);
        if (requestFilter.getRequestIdentityId() != null && requestFilter.getRequestIdentityId().contains(
            identityInstanceId)) {
            match = true;
        }
        return match;
    }

    private static final class PublishInteractionListener implements MALPublishInteractionListener {
        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
            throws MALException {
            LOGGER.fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body,
            final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
            throws MALException {
            LOGGER.fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body,
            final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }
}
