package esa.mo.mp.impl;

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
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mp.impl.callback.MPServiceOperation;
import esa.mo.mp.impl.callback.MPServiceOperationArguments;
import esa.mo.mp.impl.callback.MPServiceOperationCallback;
import esa.mo.mp.impl.util.MPFactory;
import esa.mo.nmf.MPRegistration;
import esa.mo.nmf.MissionPlanningNMFAdapter;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.apps.AppInteraction;

/**
 * The adapter for the TestMPServices tests
 */
public class MPServicesTestAdapter extends MissionPlanningNMFAdapter {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MPServicesTestAdapter.class.getName());

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
            ObjectId planVersionId = getArchiveManager().PLAN.updateInstanceByIdentityId(getPlanIdentityId(),
                currentPlan, null, requestArgument.getInteraction());
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

    class InsertActivityCallback extends MPServiceOperationCallback {
        @Override
        public void onCallback(List<MPServiceOperationArguments> arguments) throws MALException,
            MALInteractionException {
            MPServiceOperationArguments activityArgument = arguments.get(1);

            PlanVersionDetails currentPlan = getArchiveManager().PLAN.getInstanceByIdentityId(getPlanIdentityId());

            // Receive inserted activity
            ObjectId activityId = activityArgument.getInstanceId();
            ActivityInstanceDetails activity = getArchiveManager().ACTIVITY.getInstance(activityId);

            // Insert the activity into planned activities
            PlannedActivity plannedActivity = new PlannedActivity();
            plannedActivity.setInstance(activity);
            currentPlan.getItems().getPlannedActivities().add(plannedActivity);

            // Save modified Plan Version
            ObjectId planVersionId = getArchiveManager().PLAN.updateInstanceByIdentityId(getPlanIdentityId(),
                currentPlan, null, activityArgument.getInteraction());
            PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(PlanStatus.DRAFT);
            getArchiveManager().PLAN.addStatus(planVersionId, planUpdate, null, activityArgument.getInteraction());
        }
    }

    class InsertEventCallback extends MPServiceOperationCallback {
        @Override
        public void onCallback(List<MPServiceOperationArguments> arguments) throws MALException,
            MALInteractionException {
            MPServiceOperationArguments eventArgument = arguments.get(1);

            PlanVersionDetails currentPlan = getArchiveManager().PLAN.getInstanceByIdentityId(getPlanIdentityId());

            // Receive inserted event
            ObjectId eventId = eventArgument.getInstanceId();
            EventInstanceDetails event = getArchiveManager().EVENT.getInstance(eventId);

            // Insert the event into planned events
            PlannedEvent plannedEvent = new PlannedEvent();
            plannedEvent.setInstance(event);
            currentPlan.getItems().getPlannedEvents().add(plannedEvent);

            // Save modified Plan Version
            ObjectId planVersionId = getArchiveManager().PLAN.updateInstanceByIdentityId(getPlanIdentityId(),
                currentPlan, null, eventArgument.getInteraction());
            PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(PlanStatus.DRAFT);
            getArchiveManager().PLAN.addStatus(planVersionId, planUpdate, null, eventArgument.getInteraction());
        }
    }
}
