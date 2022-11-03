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
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
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
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.MPHelper;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planexecutioncontrol.PlanExecutionControlHelper;
import org.ccsds.moims.mo.mp.planexecutioncontrol.provider.MonitorActivitiesPublisher;
import org.ccsds.moims.mo.mp.planexecutioncontrol.provider.PlanExecutionControlInheritanceSkeleton;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityStatus;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.PlanStatus;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlannedActivity;
import org.ccsds.moims.mo.mp.structures.PlannedItems;
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.EventCOMObject;
import esa.mo.com.impl.util.EventReceivedListener;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.callback.MPServiceOperationManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.exec.ItemCallback;
import esa.mo.mp.impl.exec.TimelineExecutionCallback;
import esa.mo.mp.impl.exec.TimelineItem;
import esa.mo.mp.impl.exec.TimelineExecutionEngine;
import esa.mo.mp.impl.exec.activity.ActivityExecutionEngine;
import esa.mo.mp.impl.util.MPFactory;

/**
 * Plan Execution Control (PEC) Service provider implementation.
 * Overridden methods contain the operation implementation.
 */
public class PlanExecutionControlProviderServiceImpl extends PlanExecutionControlInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(PlanExecutionControlProviderServiceImpl.class.getName());

    private MALProvider provider;
    private boolean initialised = false;
    private MonitorActivitiesPublisher activityPublisher;
    private boolean isPublisherRegistered = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private MPServiceOperationManager operationCallbackManager;
    private ActivityExecutionEngine activityExecutionEngine;

    private MPArchiveManager archiveManager;

    private TimelineExecutionEngine executor = new TimelineExecutionEngine();

    /**
     * creates the MAL objects
     *
     * @param comServices
     * @throws MALException            On initialisation error.
     */
    public synchronized void init(final COMServicesProvider comServices, final MPArchiveManager archiveManager, final MPServiceOperationManager registration, final ActivityExecutionEngine activityExecutionEngine) throws MALException {
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
                PlanExecutionControlHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (final MALException ex) {
                // nothing to be done..
            }
        }

        // Shut down old service transport
        if (this.provider != null) {
            this.connection.closeAll();
        }

        this.provider = this.connection.startService(
            PlanExecutionControlHelper.PLANEXECUTIONCONTROL_SERVICE_NAME.toString(),
            PlanExecutionControlHelper.PLANEXECUTIONCONTROL_SERVICE, true, this
        );

        activityPublisher = createMonitorActivitiesPublisher(
            ConfigurationProviderSingleton.getDomain(),
            ConfigurationProviderSingleton.getNetwork(),
            SessionType.LIVE,
            ConfigurationProviderSingleton.getSourceSessionName(),
            QoSLevel.BESTEFFORT,
            null,
            new UInteger(0)
        );

        this.archiveManager = archiveManager;
        this.operationCallbackManager = registration;
        this.activityExecutionEngine = activityExecutionEngine;

        // Listen Event Service for Activity updates in COM Archive
        try {
            final EventConsumerServiceImpl consumer = new EventConsumerServiceImpl(comServices.getEventService().getConnectionProvider().getConnectionDetails());
            final Subscription subcription = HelperCOM.generateCOMEventSubscriptionBySourceType("ActivityStatusUpdates", PlanEditHelper.ACTIVITYUPDATE_OBJECT_TYPE);
            consumer.addEventReceivedListener(subcription, new EventReceivedListener(){
                @Override
                public void onDataReceived(final EventCOMObject eventCOMObject) {
                    final ObjectId statusId = eventCOMObject.getSource();
                    final ObjectId activityInstanceId = archiveManager.ACTIVITY.getInstanceIdByStatusId(statusId);
                    final ObjectId activityDefinitionId = archiveManager.ACTIVITY.getDefinitionIdByInstanceId(activityInstanceId);
                    final ObjectId activityIdentityId = archiveManager.ACTIVITY.getIdentityIdByDefinitionId(activityDefinitionId);
                    final Identifier activityIdentity = archiveManager.ACTIVITY.getIdentity(activityIdentityId);
                    final ActivityUpdateDetails status = archiveManager.ACTIVITY.getStatus(statusId);
                    try {
                        publishActivityUpdate(activityIdentity, activityIdentityId, activityInstanceId, status);
                    } catch (final IllegalArgumentException | MALInteractionException | MALException e) {
                        LOGGER.warning("Error publishing activity update");
                    }
                }
            });
            LOGGER.info("Subscribed to Activity updates");
        } catch (final MALInteractionException | MalformedURLException e) {
            throw new MALException(e.getMessage());
        }

        executor.setTickInterval(1000);

        this.initialised = true;
        LOGGER.info("Plan Execution Control service READY");
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
        } catch (final MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public PlanStatus submitPlan(final ObjectId planVersionId, final PlanVersionDetails planVersion, final MALInteraction interaction) throws MALInteractionException, MALException {
        final Long planVersionInstanceId = COMObjectIdHelper.getInstanceId(planVersionId);
        LOGGER.info(String.format("Submitted plan version (id: %s) received...", planVersionInstanceId));

        // Update plan status
        this.updatePlanStatus(planVersionId, PlanStatus.SUBMITTED, interaction);

        final ArrayList<TimelineItem> timeline = getTimeline(planVersion.getItems());

        executor.stop();

        final TimelineExecutionCallback executionCallback = getExecutionCallback(planVersionId, interaction);

        executor.setCallback(executionCallback);
        executor.submitTimeline(timeline);

        executor.start();

        return PlanStatus.SUBMITTED;
    }

    private ArrayList<TimelineItem> getTimeline(final PlannedItems plannedItems) {
        // Create Timeline based on PlannedActivities
        final ArrayList<TimelineItem> timeline = new ArrayList<>();
        for (final PlannedActivity plannedActivity : plannedItems.getPlannedActivities()) {
            final ObjectKey activityInstanceIdKey = plannedActivity.getInstanceId();
            final ObjectId activityInstanceId = new ObjectId(PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE, activityInstanceIdKey);

            final ObjectId activityDefinitionId = archiveManager.ACTIVITY.getDefinitionIdByInstanceId(activityInstanceId);
            final ObjectId activityIdentityId = archiveManager.ACTIVITY.getIdentityIdByDefinitionId(activityDefinitionId);
            final ActivityDefinitionDetails activityDefinition = archiveManager.ACTIVITY.getDefinition(activityDefinitionId);
            final Identifier identity = archiveManager.ACTIVITY.getIdentity(activityIdentityId);
            final ActivityUpdateDetails status = archiveManager.ACTIVITY.getStatusByInstanceId(activityInstanceId);

            if (status.getStatus() != ActivityStatus.PLANNED) continue;

            final long earliestStartTime = status.getStart().getTimeTrigger().getTriggerTime().getValue();
            final long latestStartTime = status.getEnd().getTimeTrigger().getTriggerTime().getValue();
            final String itemId = String.format("%s(%s)", identity.getValue(), activityInstanceIdKey.getInstId());

            timeline.add(new TimelineItem(earliestStartTime, latestStartTime, itemId, new ItemCallback(){
                @Override
                public void execute() {
                    activityExecutionEngine.executeActivity(activityDefinition.getExecDef().getValue(), activityInstanceId);
                }
                @Override
                public void missed() {
                    activityExecutionEngine.missedActivity(activityDefinition.getExecDef().getValue(), activityInstanceId);
                }
            }));
        }
        return timeline;
    }

    private TimelineExecutionCallback getExecutionCallback(final ObjectId planVersionId, final MALInteraction interaction) {
        return new TimelineExecutionCallback(){
            @Override
            public void onStart() {
                updatePlanStatus(planVersionId, PlanStatus.ACTIVATED, interaction);
            }
            @Override
            public void onStop() {
                updatePlanStatus(planVersionId, PlanStatus.TERMINATED, "Superceded", interaction);
            }
            @Override
            public void onFinish() {
                updatePlanStatus(planVersionId, PlanStatus.TERMINATED, "Completed (nominal)", interaction);
            }
        };
    }

    private void updatePlanStatus(final ObjectId planVersionId, final PlanStatus status, final MALInteraction interaction) {
        this.updatePlanStatus(planVersionId, status, null, interaction);
    }

    private void updatePlanStatus(final ObjectId planVersionId, final PlanStatus status, final String terminationInfo, final MALInteraction interaction) {
        try {
            final PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(status);
            planUpdate.setTerminationInfo(terminationInfo);
            archiveManager.PLAN.addStatus(planVersionId, planUpdate, null, interaction);
        } catch (final MALException | MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    private void publishActivityUpdate(final Identifier identity, final ObjectId identityId, final ObjectId instanceId, final ActivityUpdateDetails update) throws IllegalArgumentException, MALInteractionException, MALException {
        if (!this.isPublisherRegistered) {
            final EntityKeyList entityKeys = new EntityKeyList();
            entityKeys.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
            activityPublisher.register(entityKeys, new PublishInteractionListener());
            this.isPublisherRegistered = true;
        }

        final UpdateHeaderList headerList = new UpdateHeaderList();

        final Identifier firstSubKey = identity;
        final Long secondSubKey = COMObjectIdHelper.getInstanceId(identityId);
        final Long thirdSubKey = COMObjectIdHelper.getInstanceId(instanceId);
        final Long fourthSubKey = Long.valueOf(update.getStatus().getNumericValue().getValue());

        final EntityKey entityKey = new EntityKey(firstSubKey, secondSubKey, thirdSubKey, fourthSubKey);
        headerList.add(new UpdateHeader(
            update.getTimestamp(),
            connection.getConnectionDetails().getProviderURI(),
            UpdateType.CREATION,
            entityKey
        ));
        final ActivityUpdateDetailsList activityStatusList = new ActivityUpdateDetailsList();
        activityStatusList.add(update);

        activityPublisher.publish(headerList, activityStatusList);
    }

    private static final class PublishInteractionListener implements MALPublishInteractionListener {
        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            LOGGER.fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            LOGGER.fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            LOGGER.warning("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }
}
