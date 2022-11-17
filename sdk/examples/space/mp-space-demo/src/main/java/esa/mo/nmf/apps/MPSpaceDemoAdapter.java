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
package esa.mo.nmf.apps;

import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.callback.MPServiceOperation;
import esa.mo.mp.impl.callback.MPServiceOperationCallback;
import esa.mo.mp.impl.util.MPFactory;
import esa.mo.mp.impl.callback.MPServiceOperationArguments;
import esa.mo.nmf.MPRegistration;
import esa.mo.nmf.MissionPlanningNMFAdapter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.EventInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.ccsds.moims.mo.mp.structures.PlanStatus;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlannedActivity;
import org.ccsds.moims.mo.mp.structures.PlannedEvent;
import org.ccsds.moims.mo.mp.structures.RequestStatus;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import esa.mo.nmf.NMFInterface;

/**
 * The adapter for the NMF App
 * Demonstrates how to callback MP provider operations
 * and how to interact with COM Archive using MPArchiveManager
 */
public class MPSpaceDemoAdapter extends MissionPlanningNMFAdapter {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MPSpaceDemoAdapter.class.getName());

    private NMFInterface connector;

    private final MissionPlanningNMFAdapter adapter = this;

    private ObjectId planIdentityId = null;

    public void setNMF(NMFInterface connector) {
        this.connector = connector;
    }

    public ObjectId getPlanIdentityId() {
        return this.planIdentityId;
    }

    @Override
    public void initialRegistrations(MPRegistration registration) {
        registration.setMode(MPRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        LOGGER.info("Checking for plan...");
        Identifier planName = new Identifier("Test NMF plan");
        this.planIdentityId = getArchiveManager().PLAN.getIdentityId(planName);
        if (planIdentityId == null) {
            try {
                LOGGER.info("Creating blank plan...");
                MALInteraction interaction = new AppInteraction();
                PlanVersionDetails planVersion = MPFactory.createPlanVersion();
                planVersion.getInformation().setDescription("NMF App Plan");
                ObjectIdPair pair = getArchiveManager().PLAN.addInstance(planName, planVersion, null, interaction);
                this.planIdentityId = pair.getIdentityId();
                ObjectId planVersionId = pair.getObjectId();
                PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(PlanStatus.DRAFT);
                getArchiveManager().PLAN.addStatus(planVersionId, planUpdate, null, interaction);
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        } else {
            LOGGER.info("Plan already created");
        }

        LOGGER.info("Registering to MP operations");
        registration.registerOperation(MPServiceOperation.SUBMIT_REQUEST, new SubmitRequestVersionCallback());
        registration.registerOperation(MPServiceOperation.UPDATE_REQUEST, new UpdateRequestVersionCallback());
        registration.registerOperation(MPServiceOperation.CANCEL_REQUEST, new CancelRequestVersionCallback());
        registration.registerOperation(MPServiceOperation.INSERT_ACTIVITY, new InsertActivityCallback());
        registration.registerOperation(MPServiceOperation.INSERT_EVENT, new InsertEventCallback());
    }

    class SubmitRequestVersionCallback extends MPServiceOperationCallback {
        @Override
        public void validate(RequestVersionDetails requestVersion) {
            // Request Version validation here
            LOGGER.info("Submitted Request version validation");
        }

        @Override
        public void onCallback(List<MPServiceOperationArguments> arguments) throws MALException,
            MALInteractionException {
            // Arguments contains Request Identity Id, Request Version Id and Request Status Update Id
            MPServiceOperationArguments requestArgument = arguments.get(0);

            // Receive submitted Planning Request
            ObjectId requestVersionId = requestArgument.getInstanceId();
            RequestVersionDetails requestVersion = getArchiveManager().REQUEST_VERSION.getInstance(requestVersionId);
            LOGGER.info("Received SUBMIT_REQUEST operation with request " + requestVersion.toString());

            // Get current plan
            PlanVersionDetails currentPlan = getArchiveManager().PLAN.getInstanceByIdentityId(getPlanIdentityId());

            /* Insert some Mission Planning System here */

            // Save modified Plan Version
            PlanVersionDetails updatedPlan = currentPlan;
            ObjectId planVersionId = getArchiveManager().PLAN.updateInstanceByIdentityId(getPlanIdentityId(),
                updatedPlan, null, requestArgument.getInteraction());
            PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(PlanStatus.DRAFT);
            getArchiveManager().PLAN.addStatus(planVersionId, planUpdate, null, requestArgument.getInteraction());

            // Save updated Request Status
            RequestUpdateDetails newStatus = new RequestUpdateDetails();
            newStatus.setRequestId(requestArgument.getInstanceId());
            newStatus.setStatus(RequestStatus.PLANNED);
            newStatus.setTimestamp(HelperTime.getTimestampMillis());
            newStatus.setPlanRef(planVersionId);
            ObjectId statusId = getArchiveManager().REQUEST_VERSION.updateStatus(requestArgument.getInstanceId(),
                newStatus, null, requestArgument.getInteraction());
        }
    }

    static class UpdateRequestVersionCallback extends MPServiceOperationCallback {
        @Override
        public void validate(RequestVersionDetails requestVersion) {
            // Request Version validation here
            LOGGER.info("Updated Request version validation");
        }

        @Override
        public void onCallback(List<MPServiceOperationArguments> arguments) throws MALException,
            MALInteractionException {
            LOGGER.info("Received UPDATE_REQUEST operation");
            // Override here
        }
    }

    static class CancelRequestVersionCallback extends MPServiceOperationCallback {
        @Override
        public void onCallback(List<MPServiceOperationArguments> arguments) throws MALException,
            MALInteractionException {
            LOGGER.info("Received CANCEL_REQUEST operation");
            // Override here
        }
    }

    class InsertActivityCallback extends MPServiceOperationCallback {

        @Override
        public void validate(PlanVersionDetails planVersion, ActivityInstanceDetails activityInstance) {
            LOGGER.info("Validating inserted activity");
        }

        @Override
        public void onCallback(List<MPServiceOperationArguments> arguments) throws MALException,
            MALInteractionException {
            LOGGER.info("Received INSERT_ACTIVITY operation");
            MPServiceOperationArguments planArgument = arguments.get(0);
            MPServiceOperationArguments activityArgument = arguments.get(1);

            ObjectId planVersionId = planArgument.getInstanceId();
            ObjectId activityDefId = activityArgument.getDefinitionId();
            ObjectId activityId = activityArgument.getInstanceId();

            // Get plan and activity
            PlanVersionDetails planVersion = getArchiveManager().PLAN.getInstance(planVersionId);
            ActivityInstanceDetails activityInstance = getArchiveManager().ACTIVITY.getInstance(activityId);

            // Add event on planned items
            PlannedActivity plannedActivity = new PlannedActivity();
            plannedActivity.setInstance(activityInstance);
            planVersion.getItems().getPlannedActivities().add(plannedActivity);

            // Save modified Plan Version
            ObjectId planIdentityId = getArchiveManager().PLAN.getIdentityIdByInstanceId(planVersionId);
            getArchiveManager().PLAN.updateInstanceByIdentityId(planIdentityId, planVersion, null, planArgument
                .getInteraction());
        }
    }

    class InsertEventCallback extends MPServiceOperationCallback {

        @Override
        public void validate(PlanVersionDetails planVersion, EventInstanceDetails eventInstance) {
            LOGGER.info("Validating inserted event");
        }

        @Override
        public void onCallback(List<MPServiceOperationArguments> arguments) throws MALException,
            MALInteractionException {
            LOGGER.info("Received INSERT_EVENT operation");
            MPServiceOperationArguments planArgument = arguments.get(0);
            MPServiceOperationArguments eventArgument = arguments.get(1);

            ObjectId planVersionId = planArgument.getInstanceId();
            ObjectId eventDefId = eventArgument.getDefinitionId();
            ObjectId eventId = eventArgument.getInstanceId();

            // Get plan and event
            PlanVersionDetails planVersion = getArchiveManager().PLAN.getInstance(planVersionId);
            EventInstanceDetails eventInstance = getArchiveManager().EVENT.getInstance(eventId);

            // Add event on planned items
            PlannedEvent plannedEvent = new PlannedEvent();
            plannedEvent.setInstance(eventInstance);
            planVersion.getItems().getPlannedEvents().add(plannedEvent);

            // Save modified Plan Version
            ObjectId planIdentityId = getArchiveManager().PLAN.getIdentityIdByInstanceId(planVersionId);
            getArchiveManager().PLAN.updateInstanceByIdentityId(planIdentityId, planVersion, null, planArgument
                .getInteraction());
        }
    }
}
