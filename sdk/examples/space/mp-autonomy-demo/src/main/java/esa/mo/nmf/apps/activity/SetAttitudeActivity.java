package esa.mo.nmf.apps.activity;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecution;
import org.ccsds.moims.mo.com.structures.ObjectId;
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
 * SetAttitudeActivity contains methods for creating and executing SetAttitude Activity
 */
public class SetAttitudeActivity implements ExecutableActivity {

    public static final String LATITUDE_ARGUMENT_NAME = "latitude";
    public static final String LONGITUDE_ARGUMENT_NAME = "longitude";

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(SetAttitudeActivity.class.getName());

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

        ActivityDefinitionDetails setAttitudeActivityDef = MPFactory.createActivityDefinition();

        setAttitudeActivityDef.setDescription("Set satellite attitude using ADCS (SetAttitudeAction). This activity is inserted by planner.");
        setAttitudeActivityDef.setVersion("1 - 03.04.2020");

        List<ArgDef> setAttitudeArgDefs = new ArrayList<>();
        setAttitudeArgDefs.add(latitudeDef);
        setAttitudeArgDefs.add(longitudeDef);
        setAttitudeActivityDef.setArgDefs(MPPolyFix.encodeArgDefs(setAttitudeArgDefs));

        setAttitudeActivityDef.setExecDef(new Identifier("SetAttitudeAction"));

        definitionId = insertActivityDef("SetAttitude", setAttitudeActivityDef);

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

    public static PlannedActivity create(ObjectId requestVersionId, TimelineItem timelineItem) throws MALException, MALInteractionException, NMFException {
        // Extract data
        long startTime = timelineItem.getLowerBound();
        long endTime = timelineItem.getUpperBound();
        String latitude = CoordinateConverter.fromPlannerCoordinate(timelineItem.getArguments()[0]);
        String longitude = CoordinateConverter.fromPlannerCoordinate(timelineItem.getArguments()[1]);

        // Create Instance
        ActivityInstanceDetails setAttitudeActivity = MPFactory.createActivityInstance();
        ObjectId setAttitudeActivityId = createActivityDetails(definitionId, setAttitudeActivity, requestVersionId);

        // Create Update
        ActivityUpdateDetails setAttitudeActivityStatus = MPFactory.createActivityUpdate(ActivityStatus.UNPLANNED);

        Time setAttitudeActivityStart = new Time(startTime);
        Time setAttitudeActivityEnd = new Time(endTime);
        TimeTrigger setAttitudeStartTrigger = new TimeTrigger(setAttitudeActivityStart, setAttitudeActivityStart);
        TimeTrigger setAttitudeEndTrigger = new TimeTrigger(setAttitudeActivityEnd, setAttitudeActivityEnd);
        setAttitudeActivityStatus.setStart(MPPolyFix.encode(setAttitudeStartTrigger));
        setAttitudeActivityStatus.setEnd(MPPolyFix.encode(setAttitudeEndTrigger));

        Argument latitudeArgument = createArgument(LATITUDE_ARGUMENT_NAME, latitude);
        Argument longitudeArgument = createArgument(LONGITUDE_ARGUMENT_NAME, longitude);
        ArgumentList setAttitudeActivityArguments = new ArgumentList();
        setAttitudeActivityArguments.add(latitudeArgument);
        setAttitudeActivityArguments.add(longitudeArgument);
        setAttitudeActivityStatus.setArguments(setAttitudeActivityArguments);

        createActivityUpdate(setAttitudeActivityId, setAttitudeActivityStatus);

        // Create PlannedActivity
        PlannedActivity setAttitudePlannedActivity = new PlannedActivity();
        setAttitudePlannedActivity.setInstance(setAttitudeActivity);
        setAttitudePlannedActivity.setInstanceId(setAttitudeActivityId.getKey());
        setAttitudePlannedActivity.setUpdate(setAttitudeActivityStatus);

        return setAttitudePlannedActivity;
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
        Argument latitudeArgument = argumentList.get(0);
        Argument longitudeArgument = argumentList.get(1);
        String latitudeValue = latitudeArgument.getArgValue().get(0);
        String longitudeValue = longitudeArgument.getArgValue().get(0);

        // Transform Activity into Action
        Long actionInstanceId = System.currentTimeMillis();
        ActionInstanceDetails actionDetails = new ActionInstanceDetails();
        Long actionDefinitionInstanceId = 1L; // TODO activityDefinition.getExecDef().getValue();
        actionDetails.setDefInstId(actionDefinitionInstanceId);
        actionDetails.setStageStartedRequired(true);
        actionDetails.setStageProgressRequired(true);
        actionDetails.setStageCompletedRequired(true);
        AttributeValueList argumentValueList = new AttributeValueList();
        argumentValueList.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(latitudeValue)));
        argumentValueList.add(new AttributeValue((Attribute) HelperAttributes.javaType2Attribute(longitudeValue)));
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

        String latitude = CoordinateConverter.toPlannerCoordinate(updateArguments.get(0).getArgValue().get(0));
        String longitude = CoordinateConverter.toPlannerCoordinate(updateArguments.get(1).getArgValue().get(0));

        return new String[] { latitude, longitude };
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

    private static ObjectId createActivityUpdate(ObjectId activityInstanceId, ActivityUpdateDetails update) throws MALException, MALInteractionException, NMFException {
        ObjectId source = null;
        update.setErrCode(null);
        update.setErrInfo(null);
        update.setTimestamp(SystemClock.getTime());
        return archiveManager.ACTIVITY.addStatus(activityInstanceId, update, source, interaction);
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