package esa.mo.nmf.apps.activity;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecution;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ActivityStatus;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ArgDef;
import org.ccsds.moims.mo.mp.structures.ArgType;
import org.ccsds.moims.mo.mp.structures.Argument;
import org.ccsds.moims.mo.mp.structures.ArgumentList;
import org.ccsds.moims.mo.mp.structures.NumericArgDef;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mp.structures.PlannedActivity;
import org.ccsds.moims.mo.mp.structures.StringArgDef;
import org.ccsds.moims.mo.mp.structures.TimeTrigger;
import org.ccsds.moims.mo.mp.structures.Trigger;
import esa.mo.apps.autonomy.planner.converter.mp.TimelineItem;
import esa.mo.apps.autonomy.util.CoordinateConverter;
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.clock.SystemClock;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.exec.activity.ExecutableActivity;
import esa.mo.mp.impl.util.MOFactory;
import esa.mo.mp.impl.util.MPFactory;
import esa.mo.mp.impl.util.MPPolyFix;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.apps.AppInteraction;

/**
 * TakeImageActivity contains methods for creating and executing TakeImage Activity
 */
public class TakeImageActivity implements ExecutableActivity {

    public static final String LATITUDE_ARGUMENT_NAME = "latitude";
    public static final String LONGITUDE_ARGUMENT_NAME = "longitude";
    public static final String FILE_ID_ARGUMENT_NAME = "fileId";
    public static final String PLANNING_CYCLE_ARGUMENT_NAME = "planningCycle";

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(TakeImageActivity.class.getName());

    private static NMFInterface connector = null;

    private static MPArchiveManager archiveManager = null;

    private static Subscription actionSubscription = null;
    private static ActionsMonitor actionsMonitor = null;

    private static MALInteraction interaction = new AppInteraction();

    private static ObjectId definitionId = null;

    public static void init(NMFInterface nmfConnector) throws MALInteractionException, MALException, NMFException {
        connector = nmfConnector;
        archiveManager = nmfConnector.getMPServices().getArchiveManager();

        // Identity & Definition
        NumericArgDef latitudeDef = new NumericArgDef(new Identifier(LATITUDE_ARGUMENT_NAME), ArgType.DOUBLE, "degree", "Latitude", false, new Union(-90), new Union(90), 10);
        NumericArgDef longitudeDef = new NumericArgDef(new Identifier(LONGITUDE_ARGUMENT_NAME), ArgType.DOUBLE, "degree", "Longitude", false, new Union(-180), new Union(180), 10);
        StringArgDef fileIdDef = new StringArgDef(new Identifier(FILE_ID_ARGUMENT_NAME), ArgType.STRING, null, "Image file ID", false, 128, null);
        NumericArgDef planningCycleDef = new NumericArgDef(new Identifier(PLANNING_CYCLE_ARGUMENT_NAME), ArgType.INTEGER, "", "Planning cycle number", false, new Union(1), null, 0);

        ActivityDefinitionDetails takeImageActivityDef = MPFactory.createActivityDefinition();

        takeImageActivityDef.setDescription("Takes an image of the specified coordinates (TakeImageAction). If the image is classified as cloudy, submits a new Request1 to repeat the image capture (Action3). This activity does not change the satellite's attitude.");
        takeImageActivityDef.setVersion("1 - 03.04.2020");

        List<ArgDef> takeImageArgDefs = new ArrayList<>();
        takeImageArgDefs.add(latitudeDef);
        takeImageArgDefs.add(longitudeDef);
        takeImageArgDefs.add(fileIdDef);
        takeImageArgDefs.add(planningCycleDef);
        takeImageActivityDef.setArgDefs(MPPolyFix.encodeArgDefs(takeImageArgDefs));
        takeImageActivityDef.setExecDef(new Identifier("TakeImageAction"));

        definitionId = insertActivityDef("TakeImage", takeImageActivityDef);

        LOGGER.info("Listening for Action progress");
        try {
            SingleConnectionDetails connectionDetails = connector.getCOMServices().getEventService().getConnectionProvider().getConnectionDetails();
            EventConsumerServiceImpl eventServiceConsumer = new EventConsumerServiceImpl(connectionDetails);
            actionSubscription = MOFactory.createSubscription();
            actionsMonitor = new ActionsMonitor();
            eventServiceConsumer.getEventStub().monitorEventRegister(actionSubscription, actionsMonitor);
        } catch (NMFException | MalformedURLException | MALException | MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public static ObjectId create(ObjectId requestVersionId, String comments) throws MALException, MALInteractionException, NMFException {
        // Create Instance
        ActivityInstanceDetails takeImageActivity = MPFactory.createActivityInstance();
        takeImageActivity.setComments(comments);
        ObjectId takeImageActivityId = createActivityDetails(definitionId, takeImageActivity, requestVersionId);
        return takeImageActivityId;
    }

    public static PlannedActivity create(ObjectId requestVersionId, TimelineItem timelineItem, String filePrefix) throws MALException, MALInteractionException, NMFException {
        // Extract data
        long startTime = timelineItem.getLowerBound();
        long endTime = timelineItem.getUpperBound();
        String latitude = CoordinateConverter.fromPlannerCoordinate(timelineItem.getArguments()[0]);
        String longitude = CoordinateConverter.fromPlannerCoordinate(timelineItem.getArguments()[1]);
        Long instanceId = Long.parseLong(timelineItem.getArguments()[2]);

        // Get Instance
        ObjectId takeImageActivityId = COMObjectIdHelper.getObjectId(instanceId, PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);
        ActivityInstanceDetails takeImageActivity = archiveManager.ACTIVITY.getInstance(takeImageActivityId);

        // Create Update
        ActivityUpdateDetails takeImageActivityStatus = MPFactory.createActivityUpdate(ActivityStatus.UNPLANNED);

        Time takeImageActivityStart = new Time(startTime);
        Time takeImageActivityEnd = new Time(endTime);
        TimeTrigger takeImageStartTrigger = new TimeTrigger(takeImageActivityStart, takeImageActivityStart);
        TimeTrigger takeImageEndTrigger = new TimeTrigger(takeImageActivityEnd, takeImageActivityEnd);
        takeImageActivityStatus.setStart(MPPolyFix.encode(takeImageStartTrigger));
        takeImageActivityStatus.setEnd(MPPolyFix.encode(takeImageEndTrigger));

        Argument latitudeArgument = createArgument(LATITUDE_ARGUMENT_NAME, latitude);
        Argument longitudeArgument = createArgument(LONGITUDE_ARGUMENT_NAME, longitude);

        int planningCycle = 1;
        ActivityUpdateDetails currentUpdate = archiveManager.ACTIVITY.getStatusByInstanceId(takeImageActivityId);
        if (currentUpdate != null) {
            ArgumentList currentArguments = currentUpdate.getArguments();
            Argument currentPlanningCycleArgument = currentArguments.get(3);
            String currentPlanningCycleValue = currentPlanningCycleArgument.getArgValue().get(0);
            planningCycle = Integer.parseInt(currentPlanningCycleValue) + 1;
        }

        String fileId = String.format("%s_%s,%s_activityInstanceId%s_iter%d", filePrefix, latitude, longitude, instanceId, planningCycle);
        Argument fileIdArgument = createArgument(FILE_ID_ARGUMENT_NAME, fileId);
        Argument planningCycleArgument = createArgument(PLANNING_CYCLE_ARGUMENT_NAME, Integer.toString(planningCycle));

        ArgumentList takeImageActivityArguments = new ArgumentList();
        takeImageActivityArguments.add(latitudeArgument);
        takeImageActivityArguments.add(longitudeArgument);
        takeImageActivityArguments.add(fileIdArgument);
        takeImageActivityArguments.add(planningCycleArgument);
        takeImageActivityStatus.setArguments(takeImageActivityArguments);

        if (currentUpdate == null) {
            createActivityUpdate(takeImageActivityId, takeImageActivityStatus);
        } else {
            updateActivityStatus(takeImageActivityId, takeImageActivityStatus, ActivityStatus.UNPLANNED);
        }

        // Create PlannedActivity
        PlannedActivity takeImagePlannedActivity = new PlannedActivity();
        takeImagePlannedActivity.setInstance(takeImageActivity);
        takeImagePlannedActivity.setInstanceId(takeImageActivityId.getKey());
        takeImagePlannedActivity.setUpdate(takeImageActivityStatus);

        return takeImagePlannedActivity;
    }

    public void missed(ObjectId activityInstanceId) throws MALException, MALInteractionException {
        ActivityUpdateDetails currentStatus = archiveManager.ACTIVITY.getStatusByInstanceId(activityInstanceId);
        updateActivityStatus(activityInstanceId, currentStatus, ActivityStatus.FAILED, "Missed");
    }

    public void execute(ObjectId activityInstanceId) throws MALException, MALInteractionException {
        ObjectId activityDefinitionId = archiveManager.ACTIVITY.getDefinitionIdByInstanceId(activityInstanceId);
        ObjectId activityIdentityId = archiveManager.ACTIVITY.getIdentityIdByDefinitionId(activityDefinitionId);
        Identifier identity = archiveManager.ACTIVITY.getIdentity(activityIdentityId);
        ActivityDefinitionDetails activityDefinition = archiveManager.ACTIVITY.getDefinition(activityDefinitionId);
        ActivityUpdateDetails currentStatus = archiveManager.ACTIVITY.getStatusByInstanceId(activityInstanceId);

        // Update actual time
        Trigger trigger = MPPolyFix.decode(currentStatus.getStart());
        trigger.setTime(SystemClock.getTime());

        // Set Activity status to EXECUTING
        Long instanceId = COMObjectIdHelper.getInstanceId(activityInstanceId);
        LOGGER.info("Updating Activity " + identity + "(" + instanceId + ") status to EXECUTING");
        updateActivityStatus(activityInstanceId, currentStatus, ActivityStatus.EXECUTING);

        // Extract arguments from Activity
        ArgumentList argumentList = currentStatus.getArguments();
        Argument fileIdArgument = argumentList.get(2);
        String fileIdValue = fileIdArgument.getArgValue().get(0);

        // Transform Activity into Action
        Long actionInstanceId = System.currentTimeMillis();
        ActionInstanceDetails actionDetails = new ActionInstanceDetails();
        Long actionDefinitionInstanceId = 2L; // TODO activityDefinition.getExecDef().getValue();
        actionDetails.setDefInstId(actionDefinitionInstanceId);
        actionDetails.setStageStartedRequired(true);
        actionDetails.setStageProgressRequired(true);
        actionDetails.setStageCompletedRequired(true);
        AttributeValueList argumentValueList = new AttributeValueList();
        argumentValueList.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(fileIdValue)));
        actionDetails.setArgumentValues(argumentValueList);
        actionDetails.setArgumentIds(null);
        actionDetails.setIsRawValue(null);

        // Store Action to Archive
        addActionInstance(actionDetails);

        // Listen for Action progress and update Activity Status based on that
        actionsMonitor.register(actionInstanceId, new ActionProgressCallback(){
            @Override
            public void onCallback(Long actionInstanceId, ActivityExecution activityExecution) {
                LOGGER.info("Action id: " + actionInstanceId + " " + activityExecution.toString());
                UInteger lastProgressStage = new UInteger(activityExecution.getStageCount().getValue() - 1);
                boolean progressComplete = Objects.equals(activityExecution.getExecutionStage(), lastProgressStage);
                if (!activityExecution.getSuccess()) {
                    // Action stage failed
                    LOGGER.log(Level.SEVERE, activityExecution.toString());
                } else if (activityExecution.getSuccess() && progressComplete) {
                    // Action executed, set Activity status to COMPLETED
                    try {
                        LOGGER.info("Updating Activity " + identity + "(" + instanceId + ") status to COMPLETED");
                        updateActivityStatus(activityInstanceId, currentStatus, ActivityStatus.COMPLETED);
                    } catch (MALException | MALInteractionException e) {
                        LOGGER.log(Level.SEVERE, null, e);
                    }
                }
            }
        });

        // Submit the Action
        try {
            connector.getMCServices().getActionService().submitAction(actionInstanceId, actionDetails, interaction);
        } catch (NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    public static ObjectId getDefinitionId() {
        return definitionId;
    }

    public static String[] getTimelineArguments(PlannedActivity plannedActivity) {
        ActivityUpdateDetails update = plannedActivity.getUpdate();
        ArgumentList updateArguments = update.getArguments();
        ObjectKey instanceIdKey = plannedActivity.getInstanceId();

        String latitude = CoordinateConverter.toPlannerCoordinate(updateArguments.get(0).getArgValue().get(0));
        String longitude = CoordinateConverter.toPlannerCoordinate(updateArguments.get(1).getArgValue().get(0));
        String instanceId = Long.toString(instanceIdKey.getInstId());

        return new String[] { latitude, longitude, instanceId };
    }

    private static ObjectId insertActivityDef(String identity, ActivityDefinitionDetails activityDefinition) throws MALInteractionException, MALException, NMFException {
        ObjectId identityId = archiveManager.ACTIVITY.getIdentityId(new Identifier(identity));
        ObjectId definitionId = archiveManager.ACTIVITY.getDefinitionIdByIdentityId(identityId);
        if (definitionId == null) {
            IdentifierList identities = new IdentifierList();
            identities.add(new Identifier(identity));
            ActivityDefinitionDetailsList definitions = new ActivityDefinitionDetailsList();
            definitions.add(activityDefinition);
            ObjectInstancePairList pairs = connector.getMPServices().getPlanInformationManagementService().addActivityDef(identities, definitions, interaction);
            definitionId = COMObjectIdHelper.getObjectId(pairs.get(0).getObjectInstanceId(), PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE);
        }

        return definitionId;
    }

    private static Argument createArgument(String name, String value) {
        StringList values = new StringList();
        values.add(value);
        return new Argument(new Identifier(name), values, 1);
    }

    private static ObjectId createActivityDetails(ObjectId definitionId, ActivityInstanceDetails activity, ObjectId source) throws MALException, MALInteractionException, NMFException {
        return archiveManager.ACTIVITY.addInstance(definitionId, activity, source, interaction);
    }

    private static ObjectId createActivityUpdate(ObjectId instanceId, ActivityUpdateDetails update) throws MALException, MALInteractionException, NMFException {
        ObjectId source = null;
        update.setErrCode(null);
        update.setErrInfo(null);
        update.setTimestamp(SystemClock.getTime());
        return archiveManager.ACTIVITY.addStatus(instanceId, update, source, interaction);
    }

    private static ObjectId updateActivityStatus(ObjectId activityInstanceId, ActivityUpdateDetails update, ActivityStatus status) throws MALException, MALInteractionException {
        return updateActivityStatus(activityInstanceId, update, status, null);
    }

    private static ObjectId updateActivityStatus(ObjectId activityInstanceId, ActivityUpdateDetails update, ActivityStatus status, String errInfo) throws MALException, MALInteractionException {
        ObjectId source = null;
        update.setErrCode(null);
        update.setErrInfo(errInfo);
        update.setStatus(status);
        update.setTimestamp(SystemClock.getTime());
        return archiveManager.ACTIVITY.updateStatus(activityInstanceId, update, source, interaction);
    }

    private static void addActionInstance(ActionInstanceDetails actionInstance) {
        Long definitionInstanceId = COMObjectIdHelper.getInstanceId(getDefinitionId());
        ActionInstanceDetailsList actionInstanceList = new ActionInstanceDetailsList();
        actionInstanceList.add(actionInstance);
        try {
            connector.getCOMServices().getArchiveService().store(
                false,
                ActionHelper.ACTIONINSTANCE_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                HelperArchive.generateArchiveDetailsList(definitionInstanceId, null, interaction),
                actionInstanceList,
                interaction
            );
        } catch (MALException | MALInteractionException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }
}
