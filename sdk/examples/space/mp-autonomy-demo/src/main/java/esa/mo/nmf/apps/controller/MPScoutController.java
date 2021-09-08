package esa.mo.nmf.apps.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.planningrequest.body.SubmitRequestResponse;
import org.ccsds.moims.mo.mp.structures.ActivityDetails;
import org.ccsds.moims.mo.mp.structures.ActivityNode;
import org.ccsds.moims.mo.mp.structures.ActivityStatus;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ArgSpec;
import org.ccsds.moims.mo.mp.structures.ArgSpecList;
import org.ccsds.moims.mo.mp.structures.ArgType;
import org.ccsds.moims.mo.mp.structures.Argument;
import org.ccsds.moims.mo.mp.structures.ArgumentList;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mp.structures.PositionExpression;
import org.ccsds.moims.mo.mp.structures.RequestStatus;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.SimpleActivityDetails;
import org.ccsds.moims.mo.mp.structures.TimeExpression;
import org.ccsds.moims.mo.mp.structures.TimeWindow;
import org.ccsds.moims.mo.mp.structures.TimeWindowList;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetailsList;
import org.ccsds.moims.mo.mp.structures.c_Expression;
import esa.mo.apps.autonomy.classify.ImageClassifier;
import esa.mo.apps.autonomy.classify.InterestClassification;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.clock.SystemClock;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.consumer.PlanExecutionControlConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanInformationManagementConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanningRequestConsumerServiceImpl;
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

/**
 * MPScoutController listens for Scout Requests and Activity updates.
 * When a Scout request is received, it is processed and starts sending Image Requests.
 * When a TakeImage Activity with clear image is completed, classification on the image is done for interests.
 * If the image is not interesting, the scouting phase continues and another Image Request is sent.
 * If the taken image is interesting, the scouting phase is completed and surveying phase starts.
 * In surveying phase another Image Request is sent, the phase is completed when clear image is received.
 */
public class MPScoutController {

    private static final Logger LOGGER = Logger.getLogger(MPScoutController.class.getName());

    private static final String SCOUT_REQUEST_IDENTITY = "Scout";

    private static final String SCOUT_REQUEST_COMMENTS = "Scout";
    private static final String SURVEY_REQUEST_COMMENTS = "Survey";

    private static final String TAKE_IMAGE_ACTIVITY = "TakeImage";

    private static final String CLASSIFICATION_INFO_CLEAR = "Clear";
    private static final String CLASSIFICATION_INFO_INTERESTING = "Interesting";
    private static final String CLASSIFICATION_INFO_NOT_INTERESTING = "Not interesting";
    private static final String CLASSIFICATION_INFO_ERROR = "Interest classification error";

    private static final String EXCEPTION_ERROR_INFO = "Exception - check logs";

    // Margin between current time and the start of sent Image Request validity time
    private static final long TAKE_IMAGE_REQUEST_START_MARGIN = 15000; // In ms

    private static String imagesPath = "images";

    private static NMFInterface connector = null;
    private static MPArchiveManager archiveManager;

    private static MALInteraction interaction = new AppInteraction();
    private static ObjectId scoutRequestTemplateId = null;

    private static int requestIteration = 1;

    private static Subscription activitySubscription = null;
    private static ActivitiesMonitor activitiesMonitor = null;

    private static PlanInformationManagementConsumerServiceImpl pimServiceConsumer;
    private static PlanningRequestConsumerServiceImpl planningRequestServiceConsumer;
    private static PlanExecutionControlConsumerServiceImpl planExecutionControlServiceConsumer;

    private MPScoutController() {}

    public static void init(NMFInterface nmfConnector) {
        connector = nmfConnector;

        imagesPath = System.getProperty("autonomy.app.images", imagesPath);

        try {
            archiveManager = connector.getMPServices().getArchiveManager();
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

        addRequestTemplate();

        LOGGER.info("Listening for Activity updates");
        try {
            activitySubscription = MOFactory.createSubscription();
            activitiesMonitor = new ActivitiesMonitor(archiveManager);
            planExecutionControlServiceConsumer.getPlanExecutionControlStub()
                .monitorActivitiesRegister(activitySubscription, activitiesMonitor);
        } catch (MALException | MALInteractionException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        LOGGER.info("Listening for Scout requests");
        Subscription imageRequestSubscription = MOFactory.createSubscription();
        RequestStatusMonitor imageRequestMonitor = new RequestStatusMonitor(new Identifier(SCOUT_REQUEST_IDENTITY), archiveManager,
            new RequestStatusCallback(){
                @Override
                public void requested(RequestUpdateDetails update) {
                    ObjectId requestVersionId = update.getRequestId();
                    try {
                        scoutRequested(requestVersionId);
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

        LOGGER.info("MPScoutController initialized!");
    }

    public static ObjectId getScoutRequestTemplateId() {
        return scoutRequestTemplateId;
    }

    private static void addRequestTemplate() {
        LOGGER.info("Checking for Scout Request template...");
        ObjectId identityId = archiveManager.REQUEST_TEMPLATE.getIdentityId(new Identifier(SCOUT_REQUEST_IDENTITY));
        scoutRequestTemplateId = archiveManager.REQUEST_TEMPLATE.getDefinitionIdByIdentityId(identityId);
        if (scoutRequestTemplateId == null) {
            LOGGER.info("Adding Scout Request template");
            IdentifierList identities = new IdentifierList();
            identities.add(new Identifier(SCOUT_REQUEST_IDENTITY));
            RequestTemplateDetailsList definitions = new RequestTemplateDetailsList();
            definitions.add(new RequestTemplateDetails());
            try {
                ObjectInstancePairList pairs = pimServiceConsumer.getPlanInformationManagementStub().addRequestDef(identities, definitions);
                scoutRequestTemplateId = COMObjectIdHelper.getObjectId(pairs.get(0).getObjectInstanceId(), PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        } else {
            LOGGER.info("Scout Request template already exists");
        }
    }

    private static void scoutRequested(ObjectId scoutRequestId) {
        LOGGER.info("Scout Request received...");
        RequestVersionDetails scoutRequest = archiveManager.REQUEST_VERSION.getInstance(scoutRequestId);
        ArgumentList argumentList = scoutRequest.getArguments();
        Argument positionsArgument = argumentList.get(0);

        Argument interestsArgument = argumentList.get(1);

        if (interestsArgument.getArgValue().isEmpty()) {
            LOGGER.warning("No interests specified in the scouting request");
        }

        List<InterestClassification> interests = new ArrayList<>();
        for (String interest : interestsArgument.getArgValue()) {
            InterestClassification classification = InterestClassification.fromString(interest);
            if (classification == null) {
                LOGGER.warning("Unknown interest " + interest);
            } else {
                interests.add(classification);
            }
        }

        try {
            for (String position : positionsArgument.getArgValue()) {
                scout(scoutRequestId, position, interests);
            }
            updateRequestStatus(scoutRequestId, null, RequestStatus.ACCEPTED);
            updateRequestStatus(scoutRequestId, null, RequestStatus.PLANNED);
            updateRequestStatus(scoutRequestId, null, RequestStatus.SCHEDULED);
            updateRequestStatus(scoutRequestId, null, RequestStatus.EXECUTING);
        } catch (PlanningException e) {
            LOGGER.info(e.getMessage());
            updateRequestStatus(scoutRequestId, null, RequestStatus.REJECTED, e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            updateRequestStatus(scoutRequestId, null, RequestStatus.REJECTED, EXCEPTION_ERROR_INFO);
        }
    }

    private static void scout(ObjectId scoutRequestId, String position, List<InterestClassification> interests) throws Exception {
        LOGGER.info(String.format("Starting scouting (%s)...", position));
        RequestVersionDetails scoutRequest = archiveManager.REQUEST_VERSION.getInstance(scoutRequestId);

        Identifier imageRequestIdentity = new Identifier(String.format("Scout(%s) - (id: %s)", position, requestIteration++));
        while (archiveManager.REQUEST_VERSION.getIdentityId(imageRequestIdentity) != null) {
            imageRequestIdentity = new Identifier(String.format("Scout(%s) - (id: %s)", position, requestIteration++));
        }

        TimeWindow validityWindow = scoutRequest.getValidityTime().get(0);
        Time validityStart = getUpdatedValidityStart(validityWindow);
        Time validityEnd = (Time)validityWindow.getEnd().getValue();

        RequestVersionDetails imageRequest = createTakeImageRequest(validityStart, validityEnd, position);
        imageRequest.setComments(SCOUT_REQUEST_COMMENTS);

        // Send Image Requests
        SubmitRequestResponse response = planningRequestServiceConsumer.getPlanningRequestStub()
            .submitRequest(imageRequestIdentity, imageRequest);

        ObjectId takeImageRequestId = COMObjectIdHelper.getObjectId(response.getBodyElement1(), PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE);

        // Monitor activities
        activitiesMonitor.register(takeImageRequestId, new ActivityUpdateCallback(){
            @Override
            public void onCallback(Identifier activityIdentity, ObjectId activityInstanceId, ActivityUpdateDetails updateDetails) {
                processScoutingActivityUpdate(activityIdentity, activityInstanceId, updateDetails, scoutRequestId, scoutRequest, position, interests);
            }
        });
    }

    private static void processScoutingActivityUpdate(Identifier activityIdentity, ObjectId activityInstanceId, ActivityUpdateDetails updateDetails, ObjectId scoutRequestId, RequestVersionDetails scoutRequest, String position, List<InterestClassification> interests) {
        ActivityStatus activityStatus = updateDetails.getStatus();
        String info = updateDetails.getErrInfo();
        if (info == null) return;

        if (activityStatus == ActivityStatus.COMPLETED && Objects.equals(activityIdentity.getValue(), TAKE_IMAGE_ACTIVITY)) {
            if (Objects.equals(info, CLASSIFICATION_INFO_CLEAR)) {
                // Clear image taken, classify it
                classifyImage(activityInstanceId, updateDetails, interests);
            } else if (info.contains(CLASSIFICATION_INFO_NOT_INTERESTING)) {
                try {
                    // Taken image not interesting, plan another scout
                    scout(scoutRequestId, position, interests);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, null, e);
                }
            } else if (info.contains(CLASSIFICATION_INFO_INTERESTING)) {
                updateRequestStatus(scoutRequestId, null, RequestStatus.COMPLETED);
                try {
                    // Image interesting, start surverying
                    survey(scoutRequest, position);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, null, e);
                }
            }
        }
    }

    private static void survey(RequestVersionDetails scoutRequest, String position) throws Exception {
        LOGGER.info(String.format("Starting surveying (%s)...", position));

        Identifier imageRequestIdentity = new Identifier(String.format("Survey(%s) - (id: %s)", position, requestIteration++));
        while (archiveManager.REQUEST_VERSION.getIdentityId(imageRequestIdentity) != null) {
            imageRequestIdentity = new Identifier(String.format("Survey(%s) - (id: %s)", position, requestIteration++));
        }

        TimeWindow validityWindow = scoutRequest.getValidityTime().get(0);
        Time validityStart = getUpdatedValidityStart(validityWindow);
        Time validityEnd = (Time)validityWindow.getEnd().getValue();

        RequestVersionDetails imageRequest = createTakeImageRequest(validityStart, validityEnd, position);
        imageRequest.setComments(SURVEY_REQUEST_COMMENTS);

        // Send Image Requests
        SubmitRequestResponse response = planningRequestServiceConsumer.getPlanningRequestStub()
            .submitRequest(imageRequestIdentity, imageRequest);

        ObjectId takeImageRequestId = COMObjectIdHelper.getObjectId(response.getBodyElement1(), PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE);

        // Monitor activities
        activitiesMonitor.register(takeImageRequestId, new ActivityUpdateCallback(){
            @Override
            public void onCallback(Identifier activityIdentity, ObjectId activityInstanceId, ActivityUpdateDetails updateDetails) {
                processSurveyingActivityUpdate(activityIdentity, updateDetails);
            }
        });
    }

    private static void processSurveyingActivityUpdate(Identifier activityIdentity, ActivityUpdateDetails updateDetails) {
        ActivityStatus activityStatus = updateDetails.getStatus();
        String info = updateDetails.getErrInfo();

        if (activityStatus == ActivityStatus.COMPLETED && Objects.equals(activityIdentity.getValue(), TAKE_IMAGE_ACTIVITY)) {
            if (Objects.equals(info, CLASSIFICATION_INFO_CLEAR)) {
                // Clear image taken, stop survey
                LOGGER.info("Clear image received, surveying completed");
            }
        }
    }

    private static Time getUpdatedValidityStart(TimeWindow validityWindow) throws PlanningException {
        Time validityStart = (Time)validityWindow.getStart().getValue();
        Time validityEnd = (Time)validityWindow.getEnd().getValue();

        long timeNow = SystemClock.getTime().getValue();
        Time updatedValidityStart = new Time(Math.max(validityStart.getValue(), timeNow + TAKE_IMAGE_REQUEST_START_MARGIN));

        if (updatedValidityStart.getValue() >= validityEnd.getValue()) {
            throw new PlanningException("Validity window expired");
        }

        return updatedValidityStart;
    }

    private static void classifyImage(ObjectId activityInstanceId, ActivityUpdateDetails updateDetails, List<InterestClassification> requestInterests) {
        LOGGER.info("Classifying image...");
        Argument fileIdArgument = updateDetails.getArguments().get(2);
        String fileId = fileIdArgument.getArgValue().get(0);
        List<InterestClassification> classification = ImageClassifier.classifyInterest(imagesPath + File.separator + fileId + ".jpg");

        if (classification.contains(InterestClassification.ERROR)) {
            LOGGER.warning("Interest classification failed");
            updateActivityStatus(activityInstanceId, ActivityStatus.COMPLETED, CLASSIFICATION_INFO_ERROR);
            return;
        }

        // Image is interesting when there is a overlap between classified and requested interests
        HashSet<InterestClassification> overlap = new HashSet<>(classification);
        overlap.retainAll(requestInterests);

        boolean interesting = !overlap.isEmpty();
        String formattedClassification = classification.toString();
        if (interesting) {
            LOGGER.info("Image interesting " + formattedClassification);
            String info = String.format("%s %s", CLASSIFICATION_INFO_INTERESTING, formattedClassification);
            updateActivityStatus(activityInstanceId, ActivityStatus.COMPLETED, info);
        } else {
            LOGGER.info("Image not interesting " + formattedClassification);
            String info =  String.format("%s %s", CLASSIFICATION_INFO_NOT_INTERESTING, formattedClassification);
            updateActivityStatus(activityInstanceId, ActivityStatus.COMPLETED, info);
        }
    }

    private static RequestVersionDetails createTakeImageRequest(Time startTime, Time endTime, String position) {
        RequestVersionDetails requestVersion = MPFactory.createRequestVersion();

        requestVersion.setTemplate(MPCameraController.getImageRequestTemplateId());
        requestVersion.setDescription(String.format("Take picture at (%s)", position));

        TimeExpression windowStart = new TimeExpression("==", null, ArgType.TIME, startTime);
        TimeExpression windowEnd = new TimeExpression("==", null, ArgType.TIME, endTime);
        TimeWindow timeWindow = new TimeWindow(windowStart, windowEnd);

        TimeWindowList timeWindows = new TimeWindowList();
        timeWindows.add(timeWindow);
        requestVersion.setValidityTime(timeWindows);

        SimpleActivityDetails simpleActivity = new SimpleActivityDetails();
        ObjectId definitionId = TakeImageActivity.getDefinitionId();
        simpleActivity.setActivityDef(COMObjectIdHelper.getInstanceId(definitionId));

        PositionExpression positionExpression = new PositionExpression("==", null, ArgType.POSITION, new Union(position));
        c_Expression expression = MPPolyFix.encode(positionExpression);
        ArgSpec positionArg = new ArgSpec(new Identifier("position"), expression, null);

        ArgSpecList argSpecList = new ArgSpecList();
        argSpecList.add(positionArg);
        simpleActivity.setArgSpecs(argSpecList);

        List<ActivityDetails> activityDetailsList = new ArrayList<>();
        activityDetailsList.add(simpleActivity);

        c_ActivityDetailsList activities = MPPolyFix.encodeActivityDetails(activityDetailsList);

        ActivityNode activityNode = new ActivityNode();
        activityNode.setComments("Take picture activity");

        activityNode.setActivities(activities);
        requestVersion.setActivities(activityNode);

        return requestVersion;
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

    private static ObjectId updateActivityStatus(ObjectId activityInstanceId, ActivityStatus status, String info) {
        ActivityUpdateDetails currentUpdate = archiveManager.ACTIVITY.getStatusByInstanceId(activityInstanceId);
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
