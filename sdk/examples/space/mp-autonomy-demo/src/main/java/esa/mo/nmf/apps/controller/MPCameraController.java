package esa.mo.nmf.apps.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.ActivityDetails;
import org.ccsds.moims.mo.mp.structures.ActivityNode;
import org.ccsds.moims.mo.mp.structures.ActivityStatus;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ArgSpec;
import org.ccsds.moims.mo.mp.structures.ArgSpecList;
import org.ccsds.moims.mo.mp.structures.Argument;
import org.ccsds.moims.mo.mp.structures.ArgumentList;
import org.ccsds.moims.mo.mp.structures.Expression;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mp.structures.PlanStatus;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlannedActivity;
import org.ccsds.moims.mo.mp.structures.PlannedActivityList;
import org.ccsds.moims.mo.mp.structures.PlannedEventList;
import org.ccsds.moims.mo.mp.structures.PlannedItems;
import org.ccsds.moims.mo.mp.structures.PositionExpression;
import org.ccsds.moims.mo.mp.structures.RequestStatus;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.SimpleActivityDetails;
import org.ccsds.moims.mo.mp.structures.TimeTrigger;
import org.ccsds.moims.mo.mp.structures.TimeWindow;
import org.ccsds.moims.mo.platform.gps.structures.TwoLineElementSet;
import org.orekit.propagation.analytical.tle.TLE;
import esa.esoc.hso.osa.apsi.application.planner.Planner;
import esa.esoc.hso.osa.apsi.application.planner.PlanningProblem;
import esa.esoc.hso.osa.apsi.trf.kernel.application.ApplicationSolution;
import esa.esoc.hso.osa.apsi.trf.kernel.application.ApplicationSolvingParameters;
import esa.esoc.hso.osa.apsi.trf.kernel.application.ApplicationSolvingTypes;
import esa.esoc.hso.osa.apsi.trf.kernel.domain.DomainTimeline;
import esa.mo.apps.autonomy.classify.CloudyClassification;
import esa.mo.apps.autonomy.classify.ImageClassifier;
import esa.mo.apps.autonomy.planner.converter.mp.GoalItem;
import esa.mo.apps.autonomy.planner.converter.mp.MPConverter;
import esa.mo.apps.autonomy.planner.converter.mp.TimelineItem;
import esa.mo.apps.autonomy.planner.converter.tle.TLEConverter;
import esa.mo.apps.autonomy.util.CoordinateConverter;
import esa.mo.apps.autonomy.vw.VisibilityWindow;
import esa.mo.apps.autonomy.vw.VisibilityWindowItem;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.clock.SystemClock;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.consumer.PlanExecutionControlConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanInformationManagementConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanningRequestConsumerServiceImpl;
import esa.mo.mp.impl.exec.activity.ActivityExecutionEngine;
import esa.mo.mp.impl.provider.PlanExecutionControlProviderServiceImpl;
import esa.mo.mp.impl.provider.PlanInformationManagementProviderServiceImpl;
import esa.mo.mp.impl.provider.PlanningRequestProviderServiceImpl;
import esa.mo.mp.impl.util.MOFactory;
import esa.mo.mp.impl.util.MPFactory;
import esa.mo.mp.impl.util.MPPolyFix;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.apps.AppInteraction;
import esa.mo.nmf.apps.activity.TakeImageActivity;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.nmf.apps.activity.SetAttitudeActivity;

/**
 * MPCameraController creates an empty plan, starts listening for Image Requests and Activity updates
 * When an Image request is received, it is processed and planning is initiated.
 * Planned activities are merged to current plan and the plan submitted for execution.
 * When TakeImage Activity is completed, classification on the image is done for cloudiness.
 * If the image is clear, then request is completed, when it is cloudy, replanning is done.
 * Replanning is repeated until Request validity time permits.
 */
public class MPCameraController {

    private static final Logger LOGGER = Logger.getLogger(MPCameraController.class.getName());

    private static final String IMAGE_REQUEST_IDENTITY = "Image";
    private static final String TAKE_PICTURE_GOAL = "TakePicture";
    private static final String SET_ATTITUDE_ACTIVITY = "SetAttitude";
    private static final String TAKE_IMAGE_ACTIVITY = "TakeImage";

    private static final String CLASSIFICATION_INFO_CLEAR = "Clear";
    private static final String CLASSIFICATION_INFO_CLOUDY = "Cloudy";
    private static final String CLASSIFICATION_INFO_ERROR = "Cloudy classification error";

    private static final String EXCEPTION_ERROR_INFO = "Exception - check logs";

    private static final int PLANNER_MAX_NUMBER_OF_SOLUTION = 1;

    private static final long TLE_MONITOR_TIMEOUT = 5000;

    private static final String TLE_LINE_1 = "1 44878U 19092F   20159.72929773  .00000725  00000-0  41750-4 0  9990";
    private static final String TLE_LINE_2 = "2 44878  97.4685 343.1680 0015119  36.0805 324.1445 15.15469997 26069";

    // Margin between current time and the start of planning
    private static final long PLANNING_START_MARGIN = 15000; // In ms

    private static String imagesPath = "images";
    private static boolean resumePlan = false;
    private static int maxVWNumberPerCoordinate = 10;
    private static Double maxAngle = 15d;
    private static String planStart = "2020-01-01T00:00:00.000Z";
    private static String planEnd = "2020-12-31T23:59:59.000Z";

    private static NMFInterface connector = null;
    private static MPArchiveManager archiveManager;

    private static MALInteraction interaction = new AppInteraction();
    private static ObjectId planIdentityId = null;
    private static ObjectId planVersionId = null;
    private static ObjectId imageRequestTemplateId = null;

    private static TLE tle = null;

    private static Subscription activitySubscription = null;
    private static ActivitiesMonitor activitiesMonitor = null;

    private static HashMap<Long, RequestActivitiesStatus> requestActivities = new HashMap<>();

    private static PlatformServicesConsumer platformServicesConsumer;
    private static PlanInformationManagementConsumerServiceImpl pimServiceConsumer;
    private static PlanningRequestConsumerServiceImpl planningRequestServiceConsumer;
    private static PlanExecutionControlConsumerServiceImpl planExecutionControlServiceConsumer;

    private MPCameraController() {}

    public static void init(NMFInterface nmfConnector) {
        connector = nmfConnector;

        imagesPath = System.getProperty("autonomy.app.images", imagesPath);
        resumePlan = Boolean.parseBoolean(System.getProperty("autonomy.app.plan.resume", Boolean.toString(resumePlan)));
        maxVWNumberPerCoordinate = Integer.parseInt(System.getProperty("vw.planning.max.number.per.coordinate", Integer.toString(maxVWNumberPerCoordinate)));
        maxAngle = Double.parseDouble(System.getProperty("camera.maxangle", Double.toString(maxAngle)));
        planStart = System.getProperty("apsi.temporal.start", planStart);
        planEnd = System.getProperty("apsi.temporal.end", planEnd);

        TLEConverter.init();
        MPConverter.init();
        ImageClassifier.init();

        tle = new TLE(TLE_LINE_1, TLE_LINE_2);

        try {
            archiveManager = connector.getMPServices().getArchiveManager();
        } catch (NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        try {
            platformServicesConsumer = connector.getPlatformServices();
        } catch (NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        try {
            // MP Services
            PlanInformationManagementProviderServiceImpl pimServiceProvider = connector.getMPServices()
                .getPlanInformationManagementService();
            PlanningRequestProviderServiceImpl planningRequestServiceProvider = connector.getMPServices()
                .getPlanningRequestService();
            PlanExecutionControlProviderServiceImpl planExecutionControlServiceProvider = connector.getMPServices()
                .getPlanExecutionControlService();

            // Consumer setup
            COMServicesConsumer comServicesConsumer = new COMServicesConsumer();

            SingleConnectionDetails pimConnection = pimServiceProvider.getConnectionProvider().getConnectionDetails();
            pimServiceConsumer = new PlanInformationManagementConsumerServiceImpl(pimConnection, comServicesConsumer);
            comServicesConsumer.init(pimServiceConsumer.getConnectionConsumer());

            SingleConnectionDetails prsConnection = planningRequestServiceProvider.getConnectionProvider().getConnectionDetails();
            planningRequestServiceConsumer = new PlanningRequestConsumerServiceImpl(prsConnection, comServicesConsumer);
            comServicesConsumer.init(planningRequestServiceConsumer.getConnectionConsumer());

            SingleConnectionDetails pecConnection = planExecutionControlServiceProvider.getConnectionProvider().getConnectionDetails();
            planExecutionControlServiceConsumer = new PlanExecutionControlConsumerServiceImpl(pecConnection, comServicesConsumer);
            comServicesConsumer.init(planExecutionControlServiceConsumer.getConnectionConsumer());
        } catch (NMFException | MALException | MalformedURLException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        try {
            SetAttitudeActivity.init(connector);
            TakeImageActivity.init(connector);
            ActivityExecutionEngine activityExecutionEngine = connector.getMPServices().getActivityExecutionEngine();
            activityExecutionEngine.register("SetAttitudeAction", new SetAttitudeActivity());
            activityExecutionEngine.register("TakeImageAction", new TakeImageActivity());
        } catch (MALInteractionException | MALException | NMFException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        addRequestTemplate();

        initPlanVersion();

        LOGGER.info("Listening for Activity updates");
        try {
            activitySubscription = MOFactory.createSubscription();
            activitiesMonitor = new ActivitiesMonitor(archiveManager);
            planExecutionControlServiceConsumer.getPlanExecutionControlStub()
                .monitorActivitiesRegister(activitySubscription, activitiesMonitor);
        } catch (MALException | MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        LOGGER.info("Listening for Image requests");
        Subscription imageRequestSubscription = MOFactory.createSubscription();
        RequestStatusMonitor imageRequestMonitor = new RequestStatusMonitor(new Identifier(IMAGE_REQUEST_IDENTITY), archiveManager,
            new RequestStatusCallback(){
                @Override
                public void requested(RequestUpdateDetails update) {
                    ObjectId requestVersionId = update.getRequestId();
                    try {
                        imageRequested(requestVersionId);
                    } catch (Throwable t) {
                        LOGGER.log(Level.SEVERE, null, t);
                    }
                }
            }
        );
        try {
            planningRequestServiceConsumer.getPlanningRequestStub()
                .monitorRequestsRegister(imageRequestSubscription, imageRequestMonitor);
        } catch (MALInteractionException | MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        VisibilityWindow.init();

        LOGGER.info("MPCameraController initialized!");
    }

    public static ObjectId getImageRequestTemplateId() {
        return imageRequestTemplateId;
    }

    private static void initPlanVersion() {
        LOGGER.info("Checking for plan...");
        Identifier planName = new Identifier("NMF autonomy plan");
        planIdentityId = archiveManager.PLAN.getIdentityId(planName);
        if (planIdentityId == null) {
            try {
                LOGGER.info("Creating blank plan...");
                PlanVersionDetails planVersion = MPFactory.createPlanVersion();
                Time validityStart = MPConverter.getISO8601Time(planStart);
                Time validityEnd = MPConverter.getISO8601Time(planEnd);
                planVersion.getInformation().setValidityStart(validityStart);
                planVersion.getInformation().setValidityEnd(validityEnd);
                planVersion.getInformation().setDescription("NMF App Plan");
                planVersion.getInformation().setComments("Initial Plan");
                ObjectIdPair pair = archiveManager.PLAN.addInstance(planName, planVersion, null, interaction);
                planIdentityId = pair.getIdentityId();
                ObjectId planVersionId = pair.getObjectId();
                PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(PlanStatus.DRAFT);
                archiveManager.PLAN.addStatus(planVersionId, planUpdate, null, interaction);
            } catch (MALException | MALInteractionException | ParseException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        } else {
            LOGGER.info("Plan already created");
            ObjectId existingPlanVersion = archiveManager.PLAN.getInstanceIdByIdentityId(planIdentityId);
            PlanVersionDetails currentPlan = archiveManager.PLAN.getInstance(existingPlanVersion);
            try {
                if (resumePlan) {
                    connector.getMPServices().getPlanExecutionControlService()
                        .submitPlan(existingPlanVersion, currentPlan, interaction);
                    LOGGER.info("Plan resumed, submitted to execution");
                } else {
                    currentPlan.getItems().setPlannedActivities(new PlannedActivityList());
                    currentPlan.getItems().setPlannedEvents(new PlannedEventList());
                    currentPlan.getInformation().setComments("Restored plan");
                    existingPlanVersion = archiveManager.PLAN.updateInstanceByIdentityId(planIdentityId, currentPlan, null, interaction);
                    PlanUpdateDetails planUpdate = MPFactory.createPlanUpdate(PlanStatus.DRAFT);
                    archiveManager.PLAN.addStatus(existingPlanVersion, planUpdate, null, interaction);
                    LOGGER.info("Existing plan not resumed, resetted plan to initial");
                }
            } catch (MALInteractionException | MALException | NMFException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        }
        planVersionId = archiveManager.PLAN.getInstanceIdByIdentityId(planIdentityId);
    }

    private static void addRequestTemplate() {
        LOGGER.info("Checking for Image Request template...");
        ObjectId identityId = archiveManager.REQUEST_TEMPLATE.getIdentityId(new Identifier(IMAGE_REQUEST_IDENTITY));
        imageRequestTemplateId = archiveManager.REQUEST_TEMPLATE.getDefinitionIdByIdentityId(identityId);
        if (imageRequestTemplateId == null) {
            LOGGER.info("Adding Image Request template");
            IdentifierList identities = new IdentifierList();
            identities.add(new Identifier(IMAGE_REQUEST_IDENTITY));
            RequestTemplateDetailsList definitions = new RequestTemplateDetailsList();
            definitions.add(new RequestTemplateDetails());
            try {
                ObjectInstancePairList pairs = pimServiceConsumer.getPlanInformationManagementStub().addRequestDef(identities, definitions);
                imageRequestTemplateId = COMObjectIdHelper.getObjectId(pairs.get(0).getObjectInstanceId(), PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        } else {
            LOGGER.info("Image Request template already exists");
        }
    }

    private static synchronized void imageRequested(ObjectId requestVersionId) {
        LOGGER.info("Image Request received...");
        RequestVersionDetails requestVersion = archiveManager.REQUEST_VERSION.getInstance(requestVersionId);
        long requestTimestamp = SystemClock.getTime().getValue();
        String filePrefix = createFilePrefix(requestVersion, requestTimestamp);

        // Monitor activities
        activitiesMonitor.register(requestVersionId, new ActivityUpdateCallback(){
            @Override
            public void onCallback(Identifier activityIdentity, ObjectId activityInstanceId, ActivityUpdateDetails updateDetails) {
                processActivityUpdate(activityIdentity, activityInstanceId, updateDetails, requestVersionId, requestVersion, filePrefix);
            }
        });

        PlanVersionDetails currentPlan = archiveManager.PLAN.getInstance(planVersionId);

        List<TimelineItem> plannedTimeline = getPlannedTimeline(currentPlan.getItems().getPlannedActivities());

        List<GoalItem> goals = extractGoals(requestVersionId, requestVersion);

        try {
            TimeWindow validityWindow = requestVersion.getValidityTime().get(0);
            Time validityStart = getUpdatedValidityStart(validityWindow);
            Time validityEnd = (Time)validityWindow.getEnd().getValue();

            Time planStart = currentPlan.getInformation().getValidityStart();
            Time planEnd = currentPlan.getInformation().getValidityEnd();

            List<TimelineItem> newTimeline = solveGoals(plannedTimeline, planStart, planEnd, goals, validityStart, validityEnd, filePrefix);

            List<TimelineItem> newItems = updatePlannedActivities(plannedTimeline, newTimeline);

            updateRequestStatus(requestVersionId, null, RequestStatus.ACCEPTED);

            Long requestVersionInstanceId = COMObjectIdHelper.getInstanceId(requestVersionId);
            requestActivities.put(requestVersionInstanceId, new RequestActivitiesStatus());
            PlannedActivityList plannedActivities = planActivities(requestVersionId, requestVersion, newItems, filePrefix);

            PlannedActivityList mergedActivities = mergePlannedActivities(currentPlan.getItems().getPlannedActivities(), plannedActivities);
            PlannedItems plannedItems = new PlannedItems();
            plannedItems.setPlannedActivities(mergedActivities);

            // Save modified Plan Version
            updatePlannedItems(currentPlan, plannedItems, "Planning of Request id " + requestVersionInstanceId);

            PlanUpdateDetails planDraftUpdate = MPFactory.createPlanUpdate(PlanStatus.DRAFT);
            archiveManager.PLAN.addStatus(planVersionId, planDraftUpdate, null, interaction);

            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.PLANNED);

            // Update Activity's Plan Version reference
            for (PlannedActivity plannedActivity : plannedActivities) {
                ObjectId plannedActivityInstanceId = new ObjectId(PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE, plannedActivity.getInstanceId());
                updateActivityStatus(plannedActivityInstanceId, planVersionId, ActivityStatus.PLANNED);
            }

            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.SCHEDULED);

            // TODO: PEC.monitorPlan?
            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.EXECUTING);

            PlanUpdateDetails planReleasedUpdate = MPFactory.createPlanUpdate(PlanStatus.RELEASED);
            archiveManager.PLAN.addStatus(planVersionId, planReleasedUpdate, null, interaction);

            // Submit plan to Plan Execution Control
            connector.getMPServices().getPlanExecutionControlService()
                .submitPlan(planVersionId, currentPlan, interaction);
            LOGGER.info("Plan submitted for execution");
        } catch (PlanningException e) {
            LOGGER.info(e.getMessage());
            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.REJECTED, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.REJECTED, EXCEPTION_ERROR_INFO);
        }
    }

    private static synchronized void processActivityUpdate(Identifier activityIdentity, ObjectId activityInstanceId, ActivityUpdateDetails updateDetails, ObjectId requestVersionId, RequestVersionDetails requestVersion, String filePrefix) {
        ActivityStatus activityStatus = updateDetails.getStatus();
        String info = updateDetails.getErrInfo();
        Long requestVersionInstanceId = COMObjectIdHelper.getInstanceId(requestVersionId);
        requestActivities.get(requestVersionInstanceId).setActivityStatus(activityInstanceId, activityStatus);

        if (activityStatus == ActivityStatus.COMPLETED && Objects.equals(activityIdentity.getValue(), TAKE_IMAGE_ACTIVITY)) {
            if (info == null) {
                // Image taken, classify it
                classifyImage(activityInstanceId, updateDetails);
                return;
            } else if (Objects.equals(info, CLASSIFICATION_INFO_CLOUDY) || Objects.equals(info, CLASSIFICATION_INFO_ERROR)) {
                // Taken image cloudy or classification failed, replan activity
                replanActivity(requestVersionId, requestVersion, activityInstanceId, updateDetails, filePrefix);
                return;
            } else if (Objects.equals(info, CLASSIFICATION_INFO_CLEAR)) {
                // Image clear
            }
        } else if (activityStatus == ActivityStatus.FAILED && Objects.equals(activityIdentity.getValue(), TAKE_IMAGE_ACTIVITY)) {
            if (Objects.equals(info, "Missed")) {
                // Missed, replan activity
                replanActivity(requestVersionId, requestVersion, activityInstanceId, updateDetails, filePrefix);
                return;
            }
        }

        RequestStatus currentStatus = requestActivities.get(requestVersionInstanceId).getRequestStatus();
        if (currentStatus == RequestStatus.COMPLETED) {
            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.COMPLETED);
        } else if (currentStatus == RequestStatus.FAILED) {
            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.FAILED);
        }
    }

    private static List<GoalItem> extractGoals(ObjectId requestVersionId, RequestVersionDetails requestVersion) {
        List<GoalItem> goals = new ArrayList<>();

        ActivityNode activityNode = requestVersion.getActivities();
        List<ActivityDetails> activityList = MPPolyFix.decode(activityNode.getActivities());

        for (ActivityDetails activityDetails : activityList) {
            if (activityDetails instanceof SimpleActivityDetails) {
                SimpleActivityDetails simpleActivity = (SimpleActivityDetails) activityDetails;
                // TODO: Activity definition id
                Long activityDefinitionInstanceId = simpleActivity.getActivityDef();

                ArgSpecList argSpecs = simpleActivity.getArgSpecs();
                ArgSpec argSpec = argSpecs.get(0);

                try {
                    Expression expression = MPPolyFix.decode(argSpec.getArgSpec());
                    PositionExpression positionExpression = (PositionExpression) expression;
                    String positionString =  ((Union)positionExpression.getValue()).getStringValue();
                    String[] position = positionString.split(",");
                    String latitude = CoordinateConverter.toPlannerCoordinate(position[0]);
                    String longitude = CoordinateConverter.toPlannerCoordinate(position[1]);
                    String comments = createTakeImageComments(requestVersion.getComments(), positionString);
                    ObjectId activityInstanceId = TakeImageActivity.create(requestVersionId, comments);
                    Long instanceId = COMObjectIdHelper.getInstanceId(activityInstanceId);
                    String[] arguments = { latitude, longitude, Long.toString(instanceId), "T", "T" };
                    goals.add(new GoalItem(TAKE_PICTURE_GOAL, arguments));
                } catch (MALException | MALInteractionException | NMFException e) {
                    LOGGER.log(Level.SEVERE, null, e);
                }
            }
        }

        return goals;
    }

    private static String createFilePrefix(RequestVersionDetails requestVersion, long requestTimestamp) {
        String comments = TAKE_IMAGE_ACTIVITY;
        if (requestVersion.getComments() != null && !Objects.equals(requestVersion.getComments(), "")) {
            comments = requestVersion.getComments();
        }
        Date date = new Date(requestTimestamp);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return String.format("Request_%s_%s", format.format(date), comments);
    }

    private static String createTakeImageComments(String requestComments, String position) {
        String prefix = TAKE_IMAGE_ACTIVITY;
        if (requestComments != null && !requestComments.equals("")) {
            prefix = requestComments;
        }
        return String.format("%s(%s)", prefix, position);
    }

    private static List<TimelineItem> solveGoals(List<TimelineItem> plannedTimeline, Time planStart, Time planEnd, List<GoalItem> goals, Time goalStart, Time goalEnd, String filePrefix) throws Exception {
        // Update TLE before getting visibility windows
        if (platformServicesConsumer != null) {
            updateTLE();
        }

        List<VisibilityWindowItem> visibilityWindows = getVisibilityWindows(goals, goalStart, goalEnd);
        if (visibilityWindows.isEmpty()) {
            throw new PlanningException("No visibility windows found");
        }

        PlanningProblem planningProblem = MPConverter.convert(planStart, planEnd, goals, visibilityWindows, plannedTimeline, filePrefix);

        ApplicationSolvingParameters solvingParameters = new ApplicationSolvingParameters();
        solvingParameters.solving_type = ApplicationSolvingTypes.ALL;
        solvingParameters.max_number_of_solutions = PLANNER_MAX_NUMBER_OF_SOLUTION;
        solvingParameters.garbage_collection = false;

        Planner planner = new Planner(planningProblem);

        Instant start = Instant.now();
        List<ApplicationSolution> solutions = planner.solve(solvingParameters);
        Instant finish = Instant.now();
        long solveTime = Duration.between(start, finish).toMillis();

        if (solutions.isEmpty()) {
            throw new PlanningException("No planning solutions found");
        } else {
            LOGGER.info(String.format("Planner found %s solution(s) in %sms", solutions.size(), solveTime));
        }

        ApplicationSolution solution = solutions.get(0);
        planner.chooseSolution(solution);

        DomainTimeline domainTimeline = planningProblem.domain.getDefaultDomainTimeline();

        return MPConverter.convert(domainTimeline, filePrefix);
    }

    private static List<TimelineItem> getPlannedTimeline(PlannedActivityList plannedActivities) {
        List<TimelineItem> plannedItems = new ArrayList<>();
        for (int index = 0; index < plannedActivities.size(); index++) {
            PlannedActivity plannedActivity = plannedActivities.get(index);
            ObjectKey activityInstanceIdKey = plannedActivity.getInstanceId();
            ObjectId activityInstanceId = new ObjectId(PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE, activityInstanceIdKey);

            ObjectId activityDefinitionId = archiveManager.ACTIVITY.getDefinitionIdByInstanceId(activityInstanceId);
            ObjectId activityIdentityId = archiveManager.ACTIVITY.getIdentityIdByDefinitionId(activityDefinitionId);
            Identifier identity = archiveManager.ACTIVITY.getIdentity(activityIdentityId);
            ActivityUpdateDetails status = plannedActivity.getUpdate();

            String activityIdentity = identity.getValue();
            Time activityStart = status.getStart().getTimeTrigger().getTriggerTime();
            Time activityEnd = status.getEnd().getTimeTrigger().getTriggerTime();

            String id = Long.toString(activityInstanceIdKey.getInstId());
            String name = null;
            long lowerBound = activityStart.getValue();
            long upperBound = activityEnd.getValue();
            String[] arguments = new String[] {};

            switch (activityIdentity) {
                case SET_ATTITUDE_ACTIVITY: {
                    name = "Locking";
                    arguments = SetAttitudeActivity.getTimelineArguments(plannedActivity);
                    break;
                }
                case TAKE_IMAGE_ACTIVITY: {
                    name = "MakePic";
                    arguments = TakeImageActivity.getTimelineArguments(plannedActivity);
                    break;
                }
                default:
                    break;
            }

            if (name == null) {
                LOGGER.log(Level.SEVERE, "Unknown activity identity " + activityIdentity);
            }

            plannedItems.add(new TimelineItem(id, name, lowerBound, upperBound, arguments));
        }
        return plannedItems;
    }

    private static PlannedActivityList planActivities(ObjectId requestVersionId, RequestVersionDetails requestVersion, List<TimelineItem> timelineItems, String filePrefix) throws Exception {
        PlannedActivityList plannedActivities = new PlannedActivityList();

        for (TimelineItem timelineItem : timelineItems) {
            PlannedActivity plannedActivity = null;
            switch (timelineItem.getName()) {
                case "Locking": {
                    plannedActivity = SetAttitudeActivity.create(requestVersionId, timelineItem);
                    break;
                }
                case "MakePic": {
                    plannedActivity = TakeImageActivity.create(requestVersionId, timelineItem, filePrefix);
                    break;
                }
                default:
                    break;
            }
            if (plannedActivity != null) {
                plannedActivities.add(plannedActivity);
            }
        }

        // Store Activities statuses for monitoring
        Long requestVersionInstanceId = COMObjectIdHelper.getInstanceId(requestVersionId);
        RequestActivitiesStatus requestActivitiesStatus = requestActivities.get(requestVersionInstanceId);
        for (PlannedActivity plannedActivity : plannedActivities) {
            Long activityInstanceId = plannedActivity.getInstanceId().getInstId();
            ActivityStatus activityStatus = plannedActivity.getUpdate().getStatus();
            requestActivitiesStatus.setActivityStatus(activityInstanceId, activityStatus);
        }

        return plannedActivities;
    }

    private static void updateTLE() {
        LOGGER.info("Getting TLE from GPS service...");
        try {
            TLEMonitor tleMonitor = new TLEMonitor();
            platformServicesConsumer.getGPSService().getTLE(tleMonitor);
            synchronized(tleMonitor) {
                long start = Instant.now().toEpochMilli();
                while (Instant.now().toEpochMilli() < start + TLE_MONITOR_TIMEOUT && tleMonitor.getTLE() == null) {
                    tleMonitor.wait(TLE_MONITOR_TIMEOUT);
                }
                TwoLineElementSet twoLineElement = tleMonitor.getTLE();
                if (twoLineElement != null) {
                    TLE newTLE = TLEConverter.convert(twoLineElement);
                    if (!Objects.equals(tle.getLine1(), newTLE.getLine1()) || !Objects.equals(tle.getLine2(), newTLE.getLine2())) {
                        tle = newTLE;
                        LOGGER.info(String.format("TLE has been updated to:%n%s", newTLE.toString()));
                    } else {
                        LOGGER.info("No updates for TLE, using previous version (or default)");
                    }
                } else {
                    LOGGER.warning(String.format("Exceeded timeout of %sms, using previous version of TLE (or default)", TLE_MONITOR_TIMEOUT));
                }
            }
        } catch (MALInteractionException | MALException | IOException | InterruptedException e) {
            LOGGER.log(Level.SEVERE, "Exception during updating TLE, using previous version of TLE (or default)", e);
        }
    }

    private static List<VisibilityWindowItem> getVisibilityWindows(List<GoalItem> goals, Time start, Time end) {
        List<VisibilityWindowItem> visibilityWindows = new ArrayList<>();

        for (GoalItem goal : goals) {
            if (Objects.equals(goal.getName(), TAKE_PICTURE_GOAL)) {
                Double latitude = Double.parseDouble(CoordinateConverter.fromPlannerCoordinate(goal.getArguments()[0]));
                Double longitude = Double.parseDouble(CoordinateConverter.fromPlannerCoordinate(goal.getArguments()[1]));

                List<VisibilityWindowItem> foundWindows = VisibilityWindow.getWindows(tle, latitude, longitude, maxAngle, start, end);

                // Limit the number of visibility windows for planning
                int toIndex = Math.min(maxVWNumberPerCoordinate, foundWindows.size());
                List<VisibilityWindowItem> planningWindows = foundWindows.subList(0, toIndex);
                visibilityWindows.addAll(planningWindows);
            }
        }

        return visibilityWindows;
    }

    private static void classifyImage(ObjectId activityInstanceId, ActivityUpdateDetails updateDetails) {
        LOGGER.info("Classifying image...");
        Argument fileIdArgument = updateDetails.getArguments().get(2);
        String fileId = fileIdArgument.getArgValue().get(0);
        CloudyClassification classification = ImageClassifier.classifyClouds(imagesPath + File.separator + fileId + ".jpg");
        if (classification == CloudyClassification.CLEAR) {
            LOGGER.info("Image clear");
            updateActivityStatus(activityInstanceId, planVersionId, ActivityStatus.COMPLETED, CLASSIFICATION_INFO_CLEAR);
        } else if (classification == CloudyClassification.CLOUDY) {
            LOGGER.info("Image cloudy");
            updateActivityStatus(activityInstanceId, planVersionId, ActivityStatus.COMPLETED, CLASSIFICATION_INFO_CLOUDY);
        } else {
            LOGGER.warning("Cloudy classification failed");
            updateActivityStatus(activityInstanceId, planVersionId, ActivityStatus.COMPLETED, CLASSIFICATION_INFO_ERROR);
        }
    }

    private static void replanActivity(ObjectId requestVersionId, RequestVersionDetails requestVersion, ObjectId activityInstanceId, ActivityUpdateDetails updateDetails, String filePrefix) {
        LOGGER.info("Replanning TakeImage activity..");
        PlanVersionDetails currentPlan = archiveManager.PLAN.getInstance(planVersionId);

        List<TimelineItem> plannedTimeline = getPlannedTimeline(currentPlan.getItems().getPlannedActivities());

        ArgumentList argumentList = updateDetails.getArguments();
        Argument latitudeArgument = argumentList.get(0);
        Argument longitudeArgument = argumentList.get(1);

        String latitude = CoordinateConverter.toPlannerCoordinate(latitudeArgument.getArgValue().get(0));
        String longitude = CoordinateConverter.toPlannerCoordinate(longitudeArgument.getArgValue().get(0));
        Long instanceId = COMObjectIdHelper.getInstanceId(activityInstanceId);

        List<GoalItem> goals = new ArrayList<>();
        String[] arguments = { latitude, longitude, Long.toString(instanceId), "T", "T" };
        goals.add(new GoalItem(TAKE_PICTURE_GOAL, arguments));

        try {
            TimeWindow validityWindow = requestVersion.getValidityTime().get(0);
            Time validityStart = getUpdatedValidityStart(validityWindow);
            Time validityEnd = (Time)validityWindow.getEnd().getValue();

            Time planStart = currentPlan.getInformation().getValidityStart();
            Time planEnd = currentPlan.getInformation().getValidityEnd();

            List<TimelineItem> newTimeline = solveGoals(plannedTimeline, planStart, planEnd, goals, validityStart, validityEnd, filePrefix);

            List<TimelineItem> newItems = updatePlannedActivities(plannedTimeline, newTimeline);

            PlannedActivityList plannedActivities = planActivities(requestVersionId, requestVersion, newItems, filePrefix);

            PlannedActivityList mergedActivities = mergePlannedActivities(currentPlan.getItems().getPlannedActivities(), plannedActivities);
            PlannedItems plannedItems = new PlannedItems();
            plannedItems.setPlannedActivities(mergedActivities);

            // Save modified Plan Version
            updatePlannedItems(currentPlan, plannedItems, "Replanning of Activity id " + instanceId);

            PlanUpdateDetails planDraftUpdate = MPFactory.createPlanUpdate(PlanStatus.DRAFT);
            archiveManager.PLAN.addStatus(planVersionId, planDraftUpdate, null, interaction);

            // Update Activity's Plan Version reference
            for (PlannedActivity plannedActivity : plannedActivities) {
                ObjectId plannedActivityInstanceId = new ObjectId(PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE, plannedActivity.getInstanceId());
                updateActivityStatus(plannedActivityInstanceId, planVersionId, ActivityStatus.PLANNED);
            }

            PlanUpdateDetails planReleasedUpdate = MPFactory.createPlanUpdate(PlanStatus.RELEASED);
            archiveManager.PLAN.addStatus(planVersionId, planReleasedUpdate, null, interaction);

            // Submit plan to Plan Execution Control
            connector.getMPServices().getPlanExecutionControlService()
                .submitPlan(planVersionId, currentPlan, interaction);
            LOGGER.info("Plan submitted for execution");
        } catch (PlanningException e) {
            LOGGER.info(e.getMessage());
            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.FAILED, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            updateRequestStatus(requestVersionId, planVersionId, RequestStatus.FAILED, EXCEPTION_ERROR_INFO);
        }
    }

    private static Time getUpdatedValidityStart(TimeWindow validityWindow) throws PlanningException {
        Time validityStart = (Time)validityWindow.getStart().getValue();
        Time validityEnd = (Time)validityWindow.getEnd().getValue();

        long timeNow = SystemClock.getTime().getValue();
        Time updatedValidityStart = new Time(Math.max(validityStart.getValue(), timeNow + PLANNING_START_MARGIN));

        if (updatedValidityStart.getValue() >= validityEnd.getValue()) {
            throw new PlanningException("Validity window expired");
        }

        return updatedValidityStart;
    }

    private static ObjectId updatePlannedItems(PlanVersionDetails planVersion, PlannedItems plannedItems, String comment) {
        planVersion.setItems(plannedItems);
        planVersion.getInformation().setProductionDate(SystemClock.getTime());
        planVersion.getInformation().setComments(comment);

        // Save modified Plan Version
        try {
            planVersionId = archiveManager.PLAN.updateInstanceByIdentityId(planIdentityId, planVersion, null, interaction);
            return planVersionId;
        } catch (MALException | MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return null;
        }
    }

    private static List<TimelineItem> updatePlannedActivities(List<TimelineItem> plannedTimeline, List<TimelineItem> newTimeline) {
        List<TimelineItem> newItems = new ArrayList<>(newTimeline);
        for (TimelineItem plannedItem : plannedTimeline) {
            if (newItems.contains(plannedItem)) {
                // New timeline contains exact planned activity - nothing to update
                newItems.remove(plannedItem);
                continue;
            }

            TimelineItem changedItem = null;
            for (TimelineItem newItem : newItems) {
                if (plannedItem.contains(newItem)) {
                    changedItem = newItem;
                    break;
                }
            }

            if (changedItem != null) {
                // Changed activity found in the new timeline - needs to be updated
                Long instanceId = Long.parseLong(plannedItem.getId());
                ObjectId activityInstanceId = COMObjectIdHelper.getObjectId(instanceId, PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);
                ActivityUpdateDetails activityUpdate = archiveManager.ACTIVITY.getStatusByInstanceId(activityInstanceId);

                if (activityUpdate.getStatus() == ActivityStatus.PLANNED) {
                    // Update start and end times only for activities with planned status
                    Time activityStart = new Time(changedItem.getLowerBound());
                    Time activityEnd = new Time(changedItem.getUpperBound());
                    TimeTrigger startTrigger = new TimeTrigger(activityStart, activityStart);
                    TimeTrigger endTrigger = new TimeTrigger(activityEnd, activityEnd);
                    activityUpdate.setStart(MPPolyFix.encode(startTrigger));
                    activityUpdate.setEnd(MPPolyFix.encode(endTrigger));
                    activityUpdate.setTimestamp(SystemClock.getTime());

                    try {
                        archiveManager.ACTIVITY.updateStatus(activityInstanceId, activityUpdate, null, interaction);
                    } catch (MALException | MALInteractionException e) {
                        LOGGER.log(Level.SEVERE, null, e);
                    }
                }
                newItems.remove(changedItem);
            } else {
                LOGGER.warning("Planned activity not found in new timeline");
            }

        }
        // Return only new items
        return newItems;
    }

    private static PlannedActivityList mergePlannedActivities(PlannedActivityList planActivities, PlannedActivityList newActivities) {
        PlannedActivityList mergedActivities = new PlannedActivityList();
        mergedActivities.addAll(planActivities);
        for (PlannedActivity newActivity : newActivities) {
            boolean alreadyPlanned = false;
            for (PlannedActivity planActivity : planActivities) {
                if (Objects.equals(newActivity.getInstanceId(), planActivity.getInstanceId())) {
                    alreadyPlanned = true;
                }
            }
            if (!alreadyPlanned) {
                mergedActivities.add(newActivity);
            }
        }
        return mergedActivities;
    }

    private static ObjectId updateRequestStatus(ObjectId requestVersionId, ObjectId planVersionId, RequestStatus requestStatus) {
        return updateRequestStatus(requestVersionId, planVersionId, requestStatus, null);
    }

    private static ObjectId updateRequestStatus(ObjectId requestVersionId, ObjectId planVersionId, RequestStatus requestStatus, String errInfo) {
        RequestUpdateDetails newStatus = new RequestUpdateDetails();
        newStatus.setRequestId(requestVersionId);
        newStatus.setStatus(requestStatus);
        newStatus.setTimestamp(SystemClock.getTime());
        newStatus.setPlanRef(planVersionId);
        newStatus.setErrInfo(errInfo);
        try {
            return archiveManager.REQUEST_VERSION.updateStatus(requestVersionId, newStatus, null, interaction);
        } catch (MALException | MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return null;
        }
    }

    private static ObjectId updateActivityStatus(ObjectId activityInstanceId, ObjectId planVersionId, ActivityStatus status) {
        return updateActivityStatus(activityInstanceId, planVersionId, status, null);
    }

    private static ObjectId updateActivityStatus(ObjectId activityInstanceId, ObjectId planVersionId, ActivityStatus status, String info) {
        ActivityUpdateDetails currentUpdate = archiveManager.ACTIVITY.getStatusByInstanceId(activityInstanceId);
        currentUpdate.setPlanVersionId(planVersionId);
        currentUpdate.setErrInfo(info);
        currentUpdate.setStatus(status);
        currentUpdate.setTimestamp(SystemClock.getTime());
        try {
            return archiveManager.ACTIVITY.updateStatus(activityInstanceId, currentUpdate, null, interaction);
        } catch (MALException | MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
            return null;
        }
    }
}
