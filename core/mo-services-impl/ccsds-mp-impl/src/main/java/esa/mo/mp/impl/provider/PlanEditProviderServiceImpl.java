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

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mp.MPHelper;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionHelper;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planedit.provider.PlanEditInheritanceSkeleton;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.EventInstanceDetails;
import org.ccsds.moims.mo.mp.structures.PlanStatus;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlannedActivityList;
import org.ccsds.moims.mo.mp.structures.PlannedEventList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.callback.MPServiceOperation;
import esa.mo.mp.impl.callback.MPServiceOperationArguments;
import esa.mo.mp.impl.callback.MPServiceOperationHelper;
import esa.mo.mp.impl.callback.MPServiceOperationManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.util.MPFactory;

/**
 * Plan Edit (PED) Service provider implementation
 * Overridden methods contain the operation implementation.
 */
public class PlanEditProviderServiceImpl extends PlanEditInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(PlanEditProviderServiceImpl.class.getName());

    private MALProvider provider;
    private boolean initialised = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private MPServiceOperationManager operationCallbackManager;

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
                PlanEditHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        // Shut down old service transport
        if (this.provider != null) {
            this.connection.closeAll();
        }

        this.provider = this.connection.startService(PlanEditHelper.PLANEDIT_SERVICE_NAME.toString(),
            PlanEditHelper.PLANEDIT_SERVICE, true, this);

        this.archiveManager = archiveManager;
        this.operationCallbackManager = registration;

        this.initialised = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Plan Edit service: READY! (" + timestamp + " ms)");
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
    public void insertActivity(Long planIdentityInstanceId, Long activityDefInstanceId,
        ActivityInstanceDetails activityInstance, MALInteraction interaction) throws MALInteractionException,
        MALException {
        ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        ObjectId activityDefId = COMObjectIdHelper.getObjectId(activityDefInstanceId,
            PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE);

        PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        if (existsActivity(planVersion, activityInstance)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, new Union(
                "Given Activity already exists in the plan")));
        }

        // Validation callback
        operationCallbackManager.notifyActivityValidation(MPServiceOperation.INSERT_ACTIVITY, planVersion,
            activityInstance);

        // Add Activity
        ObjectId activityId = archiveManager.ACTIVITY.addInstance(activityDefId, activityInstance, null, interaction);

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.INSERT_ACTIVITY, Arrays.asList(
            new MPServiceOperationArguments(planIdentityId,        // Identity
                null,                  // Definition
                planVersionId,         // Instance
                null,                  // Status
                interaction, null                   // Source
            ), new MPServiceOperationArguments(null,                  // Identity
                activityDefId,         // Definition
                activityId,            // Instance
                null,                  // Status
                interaction, null                   // Source
            )));
    }

    @Override
    public void updateActivity(Long planIdentityInstanceId, Long activityInstanceId,
        ActivityInstanceDetails activityInstance, MALInteraction interaction) throws MALInteractionException,
        MALException {
        ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        ObjectId activityId = COMObjectIdHelper.getObjectId(activityInstanceId,
            PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);

        PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check validity
        ActivityInstanceDetails activity = archiveManager.ACTIVITY.getInstance(activityId);
        if (activity == null || !existsActivity(planVersion, activity)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union(
                "Given Activity does not exist in the plan")));
        }

        // Validation callback
        operationCallbackManager.notifyActivityValidation(MPServiceOperation.INSERT_ACTIVITY, planVersion,
            activityInstance);

        // Update Activity
        ObjectId activityDefinitionId = archiveManager.ACTIVITY.getDefinitionIdByInstanceId(activityId);
        ObjectId updatedActivityId = archiveManager.ACTIVITY.updateInstance(activityDefinitionId, activityInstance,
            null, interaction);

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.UPDATE_ACTIVITY, Arrays.asList(
            new MPServiceOperationArguments(planIdentityId,        // Identity
                null,                  // Definition
                planVersionId,         // Instance
                null,                  // Status
                interaction, null                   // Source
            ), new MPServiceOperationArguments(null,                  // Identity
                activityDefinitionId,  // Definition
                updatedActivityId,     // Instance
                null,                  // Status
                interaction, null                   // Source
            )));
    }

    @Override
    public void deleteActivity(Long planIdentityInstanceId, Long activityInstanceId, MALInteraction interaction)
        throws MALInteractionException, MALException {
        ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        ObjectId activityId = COMObjectIdHelper.getObjectId(activityInstanceId,
            PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);

        PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check validity
        ActivityInstanceDetails activity = archiveManager.ACTIVITY.getInstance(activityId);
        if (activity == null || !existsActivity(planVersion, activity)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union(
                "Given Activity does not exist in the plan")));
        }

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.DELETE_ACTIVITY, Arrays.asList(
            new MPServiceOperationArguments(planIdentityId,        // Identity
                null,                  // Definition
                planVersionId,         // Instance
                null,                  // Status
                interaction, null                   // Source
            ), new MPServiceOperationArguments(null,                  // Identity
                null,                  // Definition
                activityId,            // Instance
                null,                  // Status
                interaction, null                   // Source
            )));
    }

    @Override
    public void insertEvent(Long planIdentityInstanceId, Long eventDefInstanceId, EventInstanceDetails eventInstance,
        MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        ObjectId eventDefId = COMObjectIdHelper.getObjectId(eventDefInstanceId,
            PlanInformationManagementHelper.EVENTDEFINITION_OBJECT_TYPE);

        PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check for duplicate
        if (existsEvent(planVersion, eventInstance)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, new Union(
                "Given Event already exists in the plan")));
        }

        // Validation callback
        operationCallbackManager.notifyEventValidation(MPServiceOperation.INSERT_EVENT, planVersion, eventInstance);

        // Add Event
        ObjectId eventId = archiveManager.EVENT.addInstance(eventDefId, eventInstance, null, interaction);

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.INSERT_EVENT, Arrays.asList(new MPServiceOperationArguments(
            planIdentityId,        // Identity
            null,                  // Definition
            planVersionId,         // Instance
            null,                  // Status
            interaction, null                   // Source
        ), new MPServiceOperationArguments(null,                  // Identity
            eventDefId,            // Definition
            eventId,               // Instance
            null,                  // Status
            interaction, null                   // Source
        )));
    }

    @Override
    public void updateEvent(Long planIdentityInstanceId, Long eventInstanceId, EventInstanceDetails eventInstance,
        MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        ObjectId eventId = COMObjectIdHelper.getObjectId(eventInstanceId, PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE);

        PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check validity
        EventInstanceDetails event = archiveManager.EVENT.getInstance(eventId);
        if (event == null || !existsEvent(planVersion, event)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union(
                "Given Event does not exist in the plan")));
        }

        // Validation callback
        operationCallbackManager.notifyEventValidation(MPServiceOperation.UPDATE_EVENT, planVersion, eventInstance);

        // Update Event
        ObjectId eventDefinitionId = archiveManager.EVENT.getDefinitionIdByInstanceId(eventId);
        ObjectId updatedEventId = archiveManager.EVENT.updateInstance(eventDefinitionId, eventInstance, null,
            interaction);

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.UPDATE_EVENT, Arrays.asList(new MPServiceOperationArguments(
            planIdentityId,        // Identity
            null,                  // Definition
            planVersionId,         // Instance
            null,                  // Status
            interaction, null                   // Source
        ), new MPServiceOperationArguments(null,                  // Identity
            eventDefinitionId,     // Definition
            updatedEventId,        // Instance
            null,                  // Status
            interaction, null                   // Source
        )));
    }

    @Override
    public void deleteEvent(Long planIdentityInstanceId, Long eventInstanceId, MALInteraction interaction)
        throws MALInteractionException, MALException {
        ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        ObjectId eventId = COMObjectIdHelper.getObjectId(eventInstanceId, PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE);

        PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check validity
        EventInstanceDetails event = archiveManager.EVENT.getInstance(eventId);
        if (event == null || !existsEvent(planVersion, event)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union(
                "Given Event does not exist in the plan")));
        }

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.DELETE_EVENT, Arrays.asList(new MPServiceOperationArguments(
            planIdentityId,        // Identity
            null,                  // Definition
            planVersionId,         // Instance
            null,                  // Status
            interaction, null                   // Source
        ), new MPServiceOperationArguments(null,                  // Identity
            null,                  // Definition
            eventId,               // Instance
            null,                  // Status
            interaction, null                   // Source
        )));
    }

    @Override
    public void updatePlanStatus(Long planIdentityInstanceId, PlanStatus planStatus, MALInteraction interaction)
        throws MALInteractionException, MALException {
        ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId,
            PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);

        // Update plan
        PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(planStatus);
        ObjectId planUpdateId = archiveManager.PLAN.updateStatus(planVersionId, planUpdate, null, interaction);

        // Operation callback
        operationCallbackManager.notify(MPServiceOperation.UPDATE_PLAN_STATUS, MPServiceOperationHelper.asArgumentsList(
            planIdentityId,        // Identity
            null,                  // Definition
            planVersionId,         // Instance
            planUpdateId,          // Status
            interaction, null                   // Source
        ));
    }

    private ObjectId getLatestPlanVersion(ObjectId planIdentityId) throws MALInteractionException {
        ObjectId latestPlanVersionId = archiveManager.PLAN.getInstanceIdByIdentityId(planIdentityId);

        if (latestPlanVersionId == null) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, new Union(
                "Unknown Plan Identity Id")));
        }

        return latestPlanVersionId;
    }

    private boolean existsActivity(PlanVersionDetails planVersion, ActivityInstanceDetails activity) {
        int index = getActivityIndex(planVersion, activity);
        return index != -1;
    }

    private int getActivityIndex(PlanVersionDetails planVersion, ActivityInstanceDetails activity) {
        if (planVersion == null || activity == null)
            return -1;
        if (planVersion.getItems() == null || planVersion.getItems().getPlannedActivities() == null)
            return -1;
        PlannedActivityList plannedActivities = planVersion.getItems().getPlannedActivities();
        return IntStream.range(0, plannedActivities.size()).filter(index -> Objects.equals(plannedActivities.get(index)
            .getInstance(), activity)).findFirst().orElse(-1);
    }

    private boolean existsEvent(PlanVersionDetails planVersion, EventInstanceDetails event) {
        int index = getEventIndex(planVersion, event);
        return index != -1;
    }

    private int getEventIndex(PlanVersionDetails planVersion, EventInstanceDetails event) {
        if (planVersion == null || event == null)
            return -1;
        if (planVersion.getItems() == null || planVersion.getItems().getPlannedEvents() == null)
            return -1;
        PlannedEventList plannedEvents = planVersion.getItems().getPlannedEvents();
        return IntStream.range(0, plannedEvents.size()).filter(index -> Objects.equals(plannedEvents.get(index)
            .getInstance(), event)).findFirst().orElse(-1);
    }
}
