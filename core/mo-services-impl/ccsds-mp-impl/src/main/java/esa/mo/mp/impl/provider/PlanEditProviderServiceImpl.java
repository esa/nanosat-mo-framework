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
    public synchronized void init(final COMServicesProvider comServices, final MPArchiveManager archiveManager, final MPServiceOperationManager registration) throws MALException {
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
            } catch (final MALException ex) {
                // nothing to be done..
            }
        }

        // Shut down old service transport
        if (this.provider != null) {
            this.connection.closeAll();
        }

        this.provider = this.connection.startService(PlanEditHelper.PLANEDIT_SERVICE_NAME.toString(), PlanEditHelper.PLANEDIT_SERVICE, true, this);

        this.archiveManager = archiveManager;
        this.operationCallbackManager = registration;

        this.initialised = true;
        LOGGER.info("Plan Edit service READY");
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
    public void insertActivity(final Long planIdentityInstanceId, final Long activityDefInstanceId, final ActivityInstanceDetails activityInstance, final MALInteraction interaction) throws MALInteractionException, MALException {
        final ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        final ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        final ObjectId activityDefId = COMObjectIdHelper.getObjectId(activityDefInstanceId, PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE);

        final PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        if (existsActivity(planVersion, activityInstance)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, new Union("Given Activity already exists in the plan")));
        }

        // Validation callback
        operationCallbackManager.notifyActivityValidation(MPServiceOperation.INSERT_ACTIVITY, planVersion, activityInstance);

        // Add Activity
        final ObjectId activityId = archiveManager.ACTIVITY.addInstance(activityDefId, activityInstance, null, interaction);

        // Operation callback
        operationCallbackManager.notify(
            MPServiceOperation.INSERT_ACTIVITY,
            Arrays.asList(
                new MPServiceOperationArguments(
                    planIdentityId,        // Identity
                    null,                  // Definition
                    planVersionId,         // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                ),
                new MPServiceOperationArguments(
                    null,                  // Identity
                    activityDefId,         // Definition
                    activityId,            // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                )
            )
        );
    }

    @Override
    public void updateActivity(final Long planIdentityInstanceId, final Long activityInstanceId, final ActivityInstanceDetails activityInstance, final MALInteraction interaction) throws MALInteractionException, MALException {
        final ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        final ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        final ObjectId activityId = COMObjectIdHelper.getObjectId(activityInstanceId, PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);

        final PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check validity
        final ActivityInstanceDetails activity = archiveManager.ACTIVITY.getInstance(activityId);
        if (activity == null || !existsActivity(planVersion, activity)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union("Given Activity does not exist in the plan")));
        }

        // Validation callback
        operationCallbackManager.notifyActivityValidation(MPServiceOperation.INSERT_ACTIVITY, planVersion, activityInstance);

        // Update Activity
        final ObjectId activityDefinitionId = archiveManager.ACTIVITY.getDefinitionIdByInstanceId(activityId);
        final ObjectId updatedActivityId = archiveManager.ACTIVITY.updateInstance(activityDefinitionId, activityInstance, null, interaction);

        // Operation callback
        operationCallbackManager.notify(
            MPServiceOperation.UPDATE_ACTIVITY,
            Arrays.asList(
                new MPServiceOperationArguments(
                    planIdentityId,        // Identity
                    null,                  // Definition
                    planVersionId,         // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                ),
                new MPServiceOperationArguments(
                    null,                  // Identity
                    activityDefinitionId,  // Definition
                    updatedActivityId,     // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                )
            )
        );
    }

    @Override
    public void deleteActivity(final Long planIdentityInstanceId, final Long activityInstanceId, final MALInteraction interaction) throws MALInteractionException, MALException {
        final ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        final ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        final ObjectId activityId = COMObjectIdHelper.getObjectId(activityInstanceId, PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);

        final PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check validity
        final ActivityInstanceDetails activity = archiveManager.ACTIVITY.getInstance(activityId);
        if (activity == null || !existsActivity(planVersion, activity)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union("Given Activity does not exist in the plan")));
        }

        // Operation callback
        operationCallbackManager.notify(
            MPServiceOperation.DELETE_ACTIVITY,
            Arrays.asList(
                new MPServiceOperationArguments(
                    planIdentityId,        // Identity
                    null,                  // Definition
                    planVersionId,         // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                ),
                new MPServiceOperationArguments(
                    null,                  // Identity
                    null,                  // Definition
                    activityId,            // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                )
            )
        );
    }

    @Override
    public void insertEvent(final Long planIdentityInstanceId, final Long eventDefInstanceId, final EventInstanceDetails eventInstance, final MALInteraction interaction) throws MALInteractionException, MALException {
        final ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        final ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        final ObjectId eventDefId = COMObjectIdHelper.getObjectId(eventDefInstanceId, PlanInformationManagementHelper.EVENTDEFINITION_OBJECT_TYPE);

        final PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check for duplicate
        if (existsEvent(planVersion, eventInstance)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, new Union("Given Event already exists in the plan")));
        }

        // Validation callback
        operationCallbackManager.notifyEventValidation(MPServiceOperation.INSERT_EVENT, planVersion, eventInstance);

        // Add Event
        final ObjectId eventId = archiveManager.EVENT.addInstance(eventDefId, eventInstance, null, interaction);

        // Operation callback
        operationCallbackManager.notify(
            MPServiceOperation.INSERT_EVENT,
            Arrays.asList(
                new MPServiceOperationArguments(
                    planIdentityId,        // Identity
                    null,                  // Definition
                    planVersionId,         // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                ),
                new MPServiceOperationArguments(
                    null,                  // Identity
                    eventDefId,            // Definition
                    eventId,               // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                )
            )
        );
    }

    @Override
    public void updateEvent(final Long planIdentityInstanceId, final Long eventInstanceId, final EventInstanceDetails eventInstance, final MALInteraction interaction) throws MALInteractionException, MALException {
        final ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        final ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        final ObjectId eventId = COMObjectIdHelper.getObjectId(eventInstanceId, PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE);

        final PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check validity
        final EventInstanceDetails event = archiveManager.EVENT.getInstance(eventId);
        if (event == null || !existsEvent(planVersion, event)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union("Given Event does not exist in the plan")));
        }

        // Validation callback
        operationCallbackManager.notifyEventValidation(MPServiceOperation.UPDATE_EVENT, planVersion, eventInstance);

        // Update Event
        final ObjectId eventDefinitionId = archiveManager.EVENT.getDefinitionIdByInstanceId(eventId);
        final ObjectId updatedEventId = archiveManager.EVENT.updateInstance(eventDefinitionId, eventInstance, null, interaction);

        // Operation callback
        operationCallbackManager.notify(
            MPServiceOperation.UPDATE_EVENT,
            Arrays.asList(
                new MPServiceOperationArguments(
                    planIdentityId,        // Identity
                    null,                  // Definition
                    planVersionId,         // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                ),
                new MPServiceOperationArguments(
                    null,                  // Identity
                    eventDefinitionId,     // Definition
                    updatedEventId,        // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                )
            )
        );
    }

    @Override
    public void deleteEvent(final Long planIdentityInstanceId, final Long eventInstanceId, final MALInteraction interaction) throws MALInteractionException, MALException {
        final ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        final ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);
        final ObjectId eventId = COMObjectIdHelper.getObjectId(eventInstanceId, PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE);

        final PlanVersionDetails planVersion = archiveManager.PLAN.getInstance(planVersionId);

        // Check validity
        final EventInstanceDetails event = archiveManager.EVENT.getInstance(eventId);
        if (event == null || !existsEvent(planVersion, event)) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new Union("Given Event does not exist in the plan")));
        }

        // Operation callback
        operationCallbackManager.notify(
            MPServiceOperation.DELETE_EVENT,
            Arrays.asList(
                new MPServiceOperationArguments(
                    planIdentityId,        // Identity
                    null,                  // Definition
                    planVersionId,         // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                ),
                new MPServiceOperationArguments(
                    null,                  // Identity
                    null,                  // Definition
                    eventId,               // Instance
                    null,                  // Status
                    interaction,
                    null                   // Source
                )
            )
        );
    }

    @Override
    public void updatePlanStatus(final Long planIdentityInstanceId, final PlanStatus planStatus, final MALInteraction interaction) throws MALInteractionException, MALException {
        final ObjectId planIdentityId = COMObjectIdHelper.getObjectId(planIdentityInstanceId, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
        final ObjectId planVersionId = this.getLatestPlanVersion(planIdentityId);

        // Update plan
        final PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(planStatus);
        final ObjectId planUpdateId = archiveManager.PLAN.updateStatus(planVersionId, planUpdate, null, interaction);

        // Operation callback
        operationCallbackManager.notify(
            MPServiceOperation.UPDATE_PLAN_STATUS,
            MPServiceOperationHelper.asArgumentsList(
                planIdentityId,        // Identity
                null,                  // Definition
                planVersionId,         // Instance
                planUpdateId,          // Status
                interaction,
                null                   // Source
            )
        );
    }

    private ObjectId getLatestPlanVersion(final ObjectId planIdentityId) throws MALInteractionException {
        final ObjectId latestPlanVersionId = archiveManager.PLAN.getInstanceIdByIdentityId(planIdentityId);

        if (latestPlanVersionId == null) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, new Union("Unknown Plan Identity Id")));
        }

        return latestPlanVersionId;
    }

    private boolean existsActivity(final PlanVersionDetails planVersion, final ActivityInstanceDetails activity) {
        final int index = getActivityIndex(planVersion, activity);
        return index != -1;
    }

    private int getActivityIndex(final PlanVersionDetails planVersion, final ActivityInstanceDetails activity) {
        if (planVersion == null || activity == null) return -1;
        if (planVersion.getItems() == null || planVersion.getItems().getPlannedActivities() == null) return -1;
        final PlannedActivityList plannedActivities = planVersion.getItems().getPlannedActivities();
        return IntStream.range(0, plannedActivities.size())
            .filter(index -> Objects.equals(plannedActivities.get(index).getInstance(), activity))
            .findFirst().orElse(-1);
    }

    private boolean existsEvent(final PlanVersionDetails planVersion, final EventInstanceDetails event) {
        final int index = getEventIndex(planVersion, event);
        return index != -1;
    }

    private int getEventIndex(final PlanVersionDetails planVersion, final EventInstanceDetails event) {
        if (planVersion == null || event == null) return -1;
        if (planVersion.getItems() == null || planVersion.getItems().getPlannedEvents() == null) return -1;
        final PlannedEventList plannedEvents = planVersion.getItems().getPlannedEvents();
        return IntStream.range(0, plannedEvents.size())
            .filter(index -> Objects.equals(plannedEvents.get(index).getInstance(), event))
            .findFirst().orElse(-1);
    }
}
