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
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.MPHelper;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionHelper;
import org.ccsds.moims.mo.mp.plandistribution.body.GetPlanResponse;
import org.ccsds.moims.mo.mp.plandistribution.body.GetPlanStatusResponse;
import org.ccsds.moims.mo.mp.plandistribution.body.ListPlansResponse;
import org.ccsds.moims.mo.mp.plandistribution.provider.MonitorPlanPublisher;
import org.ccsds.moims.mo.mp.plandistribution.provider.MonitorPlanStatusPublisher;
import org.ccsds.moims.mo.mp.plandistribution.provider.PlanDistributionInheritanceSkeleton;
import org.ccsds.moims.mo.mp.plandistribution.provider.QueryPlanInteraction;
import org.ccsds.moims.mo.mp.structures.PlanFilter;
import org.ccsds.moims.mo.mp.structures.PlanInformationList;
import org.ccsds.moims.mo.mp.structures.PlanStatusList;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetailsList;
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.callback.MPServiceOperationManager;

/**
 * Plan Distribution (PDS) Service provider implementation
 * Overridden methods contain the operation implementation.
 */
public class PlanDistributionProviderServiceImpl extends PlanDistributionInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(PlanDistributionProviderServiceImpl.class.getName());

    private MALProvider provider;
    private boolean initialised = false;
    private MonitorPlanPublisher planPublisher;
    private boolean isPlanPublisherRegistered = false;
    private boolean isPlanStatusPublisherRegistered = false;
    private MonitorPlanStatusPublisher planStatusPublisher;
    private final ConnectionProvider connection = new ConnectionProvider();

    private MPArchiveManager archiveManager;

    /**
     * creates the MAL objects
     *
     * @param comServices
     * @throws MALException On initialisation error.
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
                PlanDistributionHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        // Shut down old service transport
        if (this.provider != null) {
            this.connection.closeAll();
        }

        this.provider = this.connection.startService(PlanDistributionHelper.PLANDISTRIBUTION_SERVICE_NAME.toString(),
            PlanDistributionHelper.PLANDISTRIBUTION_SERVICE, true, this);

        planPublisher = createMonitorPlanPublisher(ConfigurationProviderSingleton.getDomain(),
            ConfigurationProviderSingleton.getNetwork(), SessionType.LIVE, ConfigurationProviderSingleton
                .getSourceSessionName(), QoSLevel.BESTEFFORT, null, new UInteger(0));

        // Currently not used
        planStatusPublisher = createMonitorPlanStatusPublisher(ConfigurationProviderSingleton.getDomain(),
            ConfigurationProviderSingleton.getNetwork(), SessionType.LIVE, ConfigurationProviderSingleton
                .getSourceSessionName(), QoSLevel.BESTEFFORT, null, new UInteger(0));

        this.archiveManager = archiveManager;

        // Listen Event Service for Plan Version updates in COM Archive
        try {
            EventConsumerServiceImpl consumer = new EventConsumerServiceImpl(comServices.getEventService()
                .getConnectionProvider().getConnectionDetails());
            Subscription subcription = HelperCOM.generateCOMEventSubscriptionBySourceType("PlanUpdates",
                PlanDistributionHelper.PLANVERSION_OBJECT_TYPE);
            consumer.addEventReceivedListener(subcription, new EventReceivedListener() {
                @Override
                public void onDataReceived(EventCOMObject eventCOMObject) {
                    ObjectId planVersionId = eventCOMObject.getSource();
                    ObjectId planIdentityId = archiveManager.PLAN.getIdentityIdByInstanceId(planVersionId);
                    Identifier planIdentity = archiveManager.PLAN.getIdentity(planIdentityId);
                    PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);
                    try {
                        publishPlan(planIdentity, planIdentityId, planVersionId, planVersion, null);
                    } catch (IllegalArgumentException | MALInteractionException | MALException e) {
                        LOGGER.warning("Error publishing plan update");
                    }
                }
            });
            LOGGER.info("Subscribed to PlanVersion updates");
        } catch (MALInteractionException | MalformedURLException e) {
            throw new MALException(e.getMessage());
        }

        // Listen Event Service for Plan Version status updates in COM Archive
        try {
            EventConsumerServiceImpl consumer = new EventConsumerServiceImpl(comServices.getEventService()
                .getConnectionProvider().getConnectionDetails());
            Subscription subcription = HelperCOM.generateCOMEventSubscriptionBySourceType("PlanStatusUpdates",
                PlanDistributionHelper.PLANUPDATE_OBJECT_TYPE);
            consumer.addEventReceivedListener(subcription, new EventReceivedListener() {
                @Override
                public void onDataReceived(EventCOMObject eventCOMObject) {
                    ObjectId planStatusId = eventCOMObject.getSource();
                    ObjectId planVersionId = archiveManager.PLAN.getInstanceIdByStatusId(planStatusId);
                    ObjectId planIdentityId = archiveManager.PLAN.getIdentityIdByInstanceId(planVersionId);
                    Identifier planIdentity = archiveManager.PLAN.getIdentity(planIdentityId);
                    PlanUpdateDetails planUpdate = archiveManager.PLAN.getStatus(planStatusId);
                    try {
                        publishPlanStatus(planIdentity, planIdentityId, planVersionId, planUpdate);
                    } catch (IllegalArgumentException | MALInteractionException | MALException e) {
                        LOGGER.warning("Error publishing plan status update");
                    }
                }
            });
            LOGGER.info("Subscribed to PlanVersion status updates");
        } catch (MALInteractionException | MalformedURLException e) {
            throw new MALException(e.getMessage());
        }

        this.initialised = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Plan Distribution service: READY! (" + timestamp + " ms)");
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
    public ListPlansResponse listPlans(PlanFilter planFilter, MALInteraction interaction)
        throws MALInteractionException, MALException {
        LongList identityIdList = new LongList();
        LongList versionIdList = new LongList();
        PlanInformationList informationList = new PlanInformationList();
        PlanStatusList statusList = new PlanStatusList();

        // List all instanceIds
        ObjectIdList versionIds = archiveManager.PLAN.listAllInstanceIds();
        ObjectIdList identityIds = archiveManager.PLAN.getIdentityIdsByInstanceIds(versionIds);
        ObjectIdList statusIds = archiveManager.PLAN.getStatusIdsByInstanceIds(versionIds);
        PlanVersionDetailsList planVersions = archiveManager.PLAN.getInstances(versionIds);
        PlanUpdateDetailsList planUpdates = archiveManager.PLAN.getStatuses(statusIds);
        for (int index = 0; index < versionIds.size(); index++) {
            ObjectId identityId = identityIds.get(index);
            ObjectId versionId = versionIds.get(index);
            PlanVersionDetails planVersion = planVersions.get(index);
            PlanUpdateDetails planUpdate = planUpdates.get(index);
            if (!checkPlanFilter(planFilter, planVersion, planUpdate)) {
                // Skip all plans that do not match the filter
                continue;
            }
            identityIdList.add(COMObjectIdHelper.getInstanceId(identityId));
            versionIdList.add(COMObjectIdHelper.getInstanceId(versionId));
            informationList.add(planVersion.getInformation());
            statusList.add(planUpdate.getStatus());
        }

        // Send response to consumer
        ListPlansResponse response = new ListPlansResponse(identityIdList, versionIdList, informationList, statusList);
        return response;
    }

    @Override
    public GetPlanResponse getPlan(LongList planIdentityIds, MALInteraction interaction) throws MALInteractionException,
        MALException {
        LongList versionIdList = new LongList();
        PlanVersionDetailsList versionList = new PlanVersionDetailsList();

        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(planIdentityIds,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectIdList versionIds = archiveManager.PLAN.getInstanceIdsByIdentityIds(identityIds);

        for (ObjectId versionId : versionIds) {
            Long versionInstanceId = COMObjectIdHelper.getInstanceId(versionId);
            PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(versionId);

            versionIdList.add(versionInstanceId);
            versionList.add(planVersion);
        }

        // Send response to consumer
        GetPlanResponse response = new GetPlanResponse(versionIdList, versionList);
        return response;
    }

    @Override
    public GetPlanStatusResponse getPlanStatus(LongList planIdentityIds, MALInteraction interaction)
        throws MALInteractionException, MALException {
        LongList versionIdList = new LongList();
        PlanStatusList statusList = new PlanStatusList();

        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(planIdentityIds,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectIdList versionIds = archiveManager.PLAN.getInstanceIdsByIdentityIds(identityIds);

        for (ObjectId versionId : versionIds) {
            Long versionInstanceId = COMObjectIdHelper.getInstanceId(versionId);
            PlanUpdateDetails planUpdate = archiveManager.PLAN.getStatusByInstanceId(versionId);

            versionIdList.add(versionInstanceId);
            statusList.add(planUpdate.getStatus());
        }

        // Send response to consumer
        GetPlanStatusResponse response = new GetPlanStatusResponse(versionIdList, statusList);
        return response;
    }

    @Override
    public void queryPlan(PlanFilter planFilter, QueryPlanInteraction interaction) throws MALInteractionException,
        MALException {
        LongList versionIdList = new LongList();
        PlanVersionDetailsList instanceList = new PlanVersionDetailsList();

        ObjectIdList statusIds = archiveManager.PLAN.listAllStatusIds();
        ObjectIdList versionIds = archiveManager.PLAN.getInstanceIdsByStatusIds(statusIds);
        PlanVersionDetailsList planVersions = archiveManager.PLAN.getInstances(versionIds);
        PlanUpdateDetailsList planUpdates = archiveManager.PLAN.getStatuses(statusIds);
        for (int index = 0; index < versionIds.size(); index++) {
            ObjectId versionId = versionIds.get(index);
            PlanVersionDetails planVersion = planVersions.get(index);
            PlanUpdateDetails planUpdate = planUpdates.get(index);
            if (!checkPlanFilter(planFilter, planVersion, planUpdate)) {
                // Skip all planVersions that do not match the filter
                continue;
            }
            versionIdList.add(COMObjectIdHelper.getInstanceId(versionId));
            instanceList.add(planVersion);
        }

        // Send response to consumer
        Short numberOfUpdates = 0;
        interaction.sendAcknowledgement(numberOfUpdates);

        interaction.sendResponse(versionIdList, instanceList);
    }

    private boolean checkPlanFilter(PlanFilter planFilter, PlanVersionDetails planVersion,
        PlanUpdateDetails planUpdate) {
        if (planFilter.getReturnAll() != null && planFilter.getReturnAll()) {
            return true;
        }
        boolean match = false;
        // TODO: Check domain
        // TODO: Check precursor
        // Check status
        if (planFilter.getStatus() != null && planFilter.getStatus().contains(planUpdate.getStatus())) {
            match = true;
        }
        return match;
    }

    private void publishPlan(Identifier planIdentity, ObjectId planIdentityId, ObjectId planVersionId,
        PlanVersionDetails planVersion, PlanUpdateDetails planUpdate) throws MALInteractionException, MALException {
        if (!this.isPlanPublisherRegistered) {
            final EntityKeyList entityKeys = new EntityKeyList();
            entityKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
            planPublisher.register(entityKeys, new PublishInteractionListener());
            this.isPlanPublisherRegistered = true;
        }

        UpdateHeaderList headerList = new UpdateHeaderList();

        Identifier firstSubKey = planIdentity;
        Long secondSubKey = COMObjectIdHelper.getInstanceId(planIdentityId);
        Long thirdSubKey = COMObjectIdHelper.getInstanceId(planVersionId);
        Long fourthSubKey = planUpdate != null ? Long.valueOf(planUpdate.getStatus().getNumericValue().getValue()) : 0L;

        EntityKey entityKey = new EntityKey(firstSubKey, secondSubKey, thirdSubKey, fourthSubKey);
        headerList.add(new UpdateHeader(HelperTime.getTimestampMillis(), connection.getConnectionDetails()
            .getProviderURI(), UpdateType.CREATION, entityKey));
        PlanVersionDetailsList planVersionList = new PlanVersionDetailsList();
        planVersionList.add(planVersion);

        planPublisher.publish(headerList, planVersionList);
    }

    private void publishPlanStatus(Identifier planIdentity, ObjectId planIdentityId, ObjectId planVersionId,
        PlanUpdateDetails planUpdate) throws MALInteractionException, MALException {
        if (!this.isPlanStatusPublisherRegistered) {
            final EntityKeyList entityKeys = new EntityKeyList();
            entityKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
            planStatusPublisher.register(entityKeys, new PublishInteractionListener());
            this.isPlanStatusPublisherRegistered = true;
        }

        UpdateHeaderList headerList = new UpdateHeaderList();

        Identifier firstSubKey = planIdentity;
        Long secondSubKey = COMObjectIdHelper.getInstanceId(planIdentityId);
        Long thirdSubKey = COMObjectIdHelper.getInstanceId(planVersionId);
        Long fourthSubKey = Long.valueOf(planUpdate.getStatus().getNumericValue().getValue());

        EntityKey entityKey = new EntityKey(firstSubKey, secondSubKey, thirdSubKey, fourthSubKey);
        headerList.add(new UpdateHeader(HelperTime.getTimestampMillis(), connection.getConnectionDetails()
            .getProviderURI(), UpdateType.CREATION, entityKey));

        ObjectIdList planVersionIdList = new ObjectIdList();
        planVersionIdList.add(planVersionId);

        PlanUpdateDetailsList planUpdateList = new PlanUpdateDetailsList();
        planUpdateList.add(planUpdate);

        planStatusPublisher.publish(headerList, planVersionIdList, planUpdateList);
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
