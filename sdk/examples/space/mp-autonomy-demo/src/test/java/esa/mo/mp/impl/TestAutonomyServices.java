package esa.mo.mp.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyString;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mp.structures.ActivityDetails;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ActivityNode;
import org.ccsds.moims.mo.mp.structures.ActivityStatus;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.ArgSpec;
import org.ccsds.moims.mo.mp.structures.ArgSpecList;
import org.ccsds.moims.mo.mp.structures.ArgType;
import org.ccsds.moims.mo.mp.structures.Argument;
import org.ccsds.moims.mo.mp.structures.ArgumentList;
import org.ccsds.moims.mo.mp.structures.Constraint;
import org.ccsds.moims.mo.mp.structures.ConstraintNode;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.ccsds.moims.mo.mp.structures.PlanStatus;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlannedActivity;
import org.ccsds.moims.mo.mp.structures.PlannedActivityList;
import org.ccsds.moims.mo.mp.structures.PlannedItems;
import org.ccsds.moims.mo.mp.structures.PositionExpression;
import org.ccsds.moims.mo.mp.structures.RequestStatus;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.SimpleActivityDetails;
import org.ccsds.moims.mo.mp.structures.TimeExpression;
import org.ccsds.moims.mo.mp.structures.TimeTrigger;
import org.ccsds.moims.mo.mp.structures.TimeWindow;
import org.ccsds.moims.mo.mp.structures.TimeWindowConstraint;
import org.ccsds.moims.mo.mp.structures.TimeWindowList;
import org.ccsds.moims.mo.mp.structures.c_ActivityDetailsList;
import org.ccsds.moims.mo.mp.structures.c_Expression;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSStub;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.consumer.CameraStub;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.Picture;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.consumer.GPSStub;
import org.ccsds.moims.mo.platform.gps.structures.TwoLineElementSet;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.orekit.propagation.analytical.tle.TLE;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import esa.mo.apps.autonomy.classify.CloudyClassification;
import esa.mo.apps.autonomy.classify.ImageClassifier;
import esa.mo.apps.autonomy.classify.InterestClassification;
import esa.mo.apps.autonomy.planner.converter.tle.TLEConverter;
import esa.mo.apps.autonomy.util.FileUtils;
import esa.mo.apps.autonomy.vw.VisibilityWindow;
import esa.mo.apps.autonomy.vw.VisibilityWindowItem;
import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.provider.EventProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.clock.SystemClock;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.mc.impl.consumer.ActionConsumerServiceImpl;
import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.consumer.PlanDistributionConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanEditConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanExecutionControlConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanInformationManagementConsumerServiceImpl;
import esa.mo.mp.impl.consumer.PlanningRequestConsumerServiceImpl;
import esa.mo.mp.impl.provider.PlanDistributionProviderServiceImpl;
import esa.mo.mp.impl.provider.PlanEditProviderServiceImpl;
import esa.mo.mp.impl.provider.PlanExecutionControlProviderServiceImpl;
import esa.mo.mp.impl.provider.PlanInformationManagementProviderServiceImpl;
import esa.mo.mp.impl.provider.PlanningRequestProviderServiceImpl;
import esa.mo.mp.impl.util.MOFactory;
import esa.mo.mp.impl.util.MPFactory;
import esa.mo.mp.impl.util.MPPolyFix;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.apps.AppInteraction;
import esa.mo.nmf.apps.MCAutonomyDemoAdapter;
import esa.mo.nmf.apps.MPAutonomyDemoAdapter;
import esa.mo.nmf.apps.activity.SetAttitudeActivity;
import esa.mo.nmf.apps.activity.TakeImageActivity;
import esa.mo.nmf.apps.controller.MPCameraController;
import esa.mo.nmf.apps.controller.MPScoutController;
import esa.mo.nmf.apps.monitoring.MPExperimentLogger;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import esa.mo.platform.impl.util.PlatformServicesConsumer;

/**
 * Test class to test MP Services that use on-board autonomy.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ VisibilityWindow.class, ImageClassifier.class })
public class TestAutonomyServices {

    private static final Logger LOGGER = Logger.getLogger(TestAutonomyServices.class.getName());

    private static final long TEST_MONITOR_TIMEOUT = 5000; // in ms

    private static MCAutonomyDemoAdapter mcAdapter;
    private static MPAutonomyDemoAdapter mpAdapter;

    private static NanoSatMOConnectorImpl connector;

    private static EventConsumerServiceImpl eventServiceConsumer;
    private static ActionConsumerServiceImpl actionServiceConsumer;
    private static PlanInformationManagementConsumerServiceImpl pimServiceConsumer;
    private static PlanningRequestConsumerServiceImpl planningRequestServiceConsumer;
    private static PlanDistributionConsumerServiceImpl planDistributionServiceConsumer;
    private static PlanEditConsumerServiceImpl planEditServiceConsumer;
    private static PlanExecutionControlConsumerServiceImpl planExecutionControlServiceConsumer;

    @BeforeClass
    public static void setup() throws MalformedURLException, MALException, MALInteractionException, NMFException, IOException {
        System.setProperty("provider.properties",
            "src/test/resources/testProvider.properties");
        System.setProperty("esa.mo.nanosatmoframework.provider.settings",
            "src/test/resources/testProvider.properties");
        System.setProperty("transport.properties",
            "src/test/resources/testTransport.properties");

        FileUtils.loadApplicationProperties("src/test/resources/testApplication.properties");

        // Provider setup
        connector = Mockito.spy(new NanoSatMOConnectorImpl());

        mcAdapter = new MCAutonomyDemoAdapter();
        mcAdapter.setNMF(connector);

        mpAdapter = new MPAutonomyDemoAdapter();
        mpAdapter.setNMF(connector);

        // Mock Platform Services
        PlatformServicesConsumer platformServicesConsumer = Mockito.mock(PlatformServicesConsumer.class);
        GPSStub gpsService = Mockito.mock(GPSStub.class);
        AutonomousADCSStub adcsService = Mockito.mock(AutonomousADCSStub.class);
        CameraStub cameraService = Mockito.mock(CameraStub.class);

        Mockito.doAnswer(invocation -> {
            Object[] arguments = invocation.getArguments();
            GPSAdapter adapter = (GPSAdapter)arguments[0];

            TwoLineElementSet twoLineElement = TLEConverter.convert(
                "1 44878U 19092F   20159.72929773  .00000725  00000-0  41750-4 0  9990",
                "2 44878  97.4685 343.1680 0015119  36.0805 324.1445 15.15469997 26069"
            );

            adapter.getTLEResponseReceived(null, twoLineElement, null);
            return null;
        }).when(gpsService).getTLE(any(GPSAdapter.class));

        Mockito.doAnswer(invocation -> {
            Object[] arguments = invocation.getArguments();
            CameraSettings cameraSettings = (CameraSettings)arguments[0];
            CameraAdapter adapter = (CameraAdapter)arguments[1];
            Picture picture = new Picture();
            picture.setSettings(cameraSettings);
            picture.setTimestamp(SystemClock.getTime());
            picture.setContent(new Blob(new byte[0]));
            adapter.takePictureResponseReceived(null, picture, null);
            return null;
        }).when(cameraService).takePicture(any(CameraSettings.class), any(CameraAdapter.class));

        Mockito.when(platformServicesConsumer.getGPSService()).thenReturn(gpsService);
        Mockito.when(platformServicesConsumer.getAutonomousADCSService()).thenReturn(adcsService);
        Mockito.when(platformServicesConsumer.getCameraService()).thenReturn(cameraService);
        Mockito.doReturn(platformServicesConsumer).when(connector).getPlatformServices();

        connector.init(mcAdapter, mpAdapter);

        // Logging setup
        MPExperimentLogger.init(connector);

        // Controller setup
        MPCameraController.init(connector);
        MPScoutController.init(connector);

        // COM Services
        EventProviderServiceImpl eventServiceProvider = connector.getCOMServices()
            .getEventService();

        // MC Services
        ActionProviderServiceImpl actionServiceProvider = connector.getMCServices()
            .getActionService();

        // MP Services
        PlanInformationManagementProviderServiceImpl pimServiceProvider = connector.getMPServices()
            .getPlanInformationManagementService();
        PlanningRequestProviderServiceImpl planningRequestServiceProvider = connector.getMPServices()
            .getPlanningRequestService();
        PlanDistributionProviderServiceImpl planDistributionServiceProvider = connector.getMPServices()
            .getPlanDistributionService();
        PlanEditProviderServiceImpl planEditServiceProvider = connector.getMPServices()
            .getPlanEditService();
        PlanExecutionControlProviderServiceImpl planExecutionControlServiceProvider = connector.getMPServices()
            .getPlanExecutionControlService();

        // Consumer setup
        COMServicesConsumer comServicesConsumer = new COMServicesConsumer();

        SingleConnectionDetails eventConnection = eventServiceProvider.getConnectionProvider().getConnectionDetails();
        eventServiceConsumer = new EventConsumerServiceImpl(eventConnection);
        comServicesConsumer.init(eventServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails actionConnection = actionServiceProvider.getConnectionProvider().getConnectionDetails();
        actionServiceConsumer = new ActionConsumerServiceImpl(actionConnection, comServicesConsumer);
        comServicesConsumer.init(actionServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails pimConnection = pimServiceProvider.getConnectionProvider().getConnectionDetails();
        pimServiceConsumer = new PlanInformationManagementConsumerServiceImpl(pimConnection, comServicesConsumer);
        comServicesConsumer.init(pimServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails prsConnection = planningRequestServiceProvider.getConnectionProvider().getConnectionDetails();
        planningRequestServiceConsumer = new PlanningRequestConsumerServiceImpl(prsConnection, comServicesConsumer);
        comServicesConsumer.init(planningRequestServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails pdsConnection = planDistributionServiceProvider.getConnectionProvider().getConnectionDetails();
        planDistributionServiceConsumer = new PlanDistributionConsumerServiceImpl(pdsConnection, comServicesConsumer);
        comServicesConsumer.init(planDistributionServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails pedConnection = planEditServiceProvider.getConnectionProvider().getConnectionDetails();
        planEditServiceConsumer = new PlanEditConsumerServiceImpl(pedConnection, comServicesConsumer);
        comServicesConsumer.init(planEditServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails pecConnection = planExecutionControlServiceProvider.getConnectionProvider().getConnectionDetails();
        planExecutionControlServiceConsumer = new PlanExecutionControlConsumerServiceImpl(pecConnection, comServicesConsumer);
        comServicesConsumer.init(planExecutionControlServiceConsumer.getConnectionConsumer());

        try {
            Thread.sleep(1000); // Wait for DB init
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        LOGGER.info("Setup finished");
    }

    @Test(timeout=30000)
    public void testPECSubmitPlan() throws MALInteractionException, MALException, NMFException {
        // Monitor Activity updates
        ActivitiesMonitor activityMonitor = new ActivitiesMonitor();
        Subscription activitySubscription = MOFactory.createSubscription();

        planExecutionControlServiceConsumer.getPlanExecutionControlStub().monitorActivitiesRegister(activitySubscription, activityMonitor);

        // Create Plan
        PlanVersionDetails planVersion = createTakeImagePlan();

        Identifier identity = new Identifier("Test plan");
        MALInteraction interaction = new AppInteraction();
        ObjectIdPair pair = mpAdapter.getArchiveManager().PLAN.addInstance(identity, planVersion, null, interaction);
        ObjectId planVersionId = pair.getObjectId();

        // Submit Plan
        PlanStatus planStatus = planExecutionControlServiceConsumer.getPlanExecutionControlStub()
            .submitPlan(planVersionId, planVersion);

        assertNotNull(planStatus);
        assertEquals(PlanStatus.SUBMITTED, planStatus);

        // Wait for Activity updates
        synchronized(activityMonitor) {
            try {
                while (activityMonitor.getReceivedUpdateList().size() < 6) {
                    activityMonitor.wait(TEST_MONITOR_TIMEOUT);
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, null, e);
                fail("Exception thrown");
            }
        }

        ActivityUpdateDetailsList monitorResponse = activityMonitor.getReceivedUpdateList();
        assertNotNull(monitorResponse);
        assertFalse(monitorResponse.isEmpty());
        assertEquals(6, monitorResponse.size());
        assertEquals(ActivityStatus.PLANNED, monitorResponse.get(0).getStatus());
        assertEquals(ActivityStatus.PLANNED, monitorResponse.get(1).getStatus());
        assertEquals(ActivityStatus.EXECUTING, monitorResponse.get(2).getStatus());
        assertEquals(ActivityStatus.COMPLETED, monitorResponse.get(3).getStatus());
        assertEquals(ActivityStatus.EXECUTING, monitorResponse.get(4).getStatus());
        assertEquals(ActivityStatus.COMPLETED, monitorResponse.get(5).getStatus());
        LOGGER.info("Received expected Activity Updates");

        // Deregister subscriptions
        IdentifierList activitySubscriptions = new IdentifierList();
        activitySubscriptions.add(activitySubscription.getSubscriptionId());
        planExecutionControlServiceConsumer.getPlanExecutionControlStub().monitorActivitiesDeregister(activitySubscriptions);
    }

    @Test(timeout=90000)
    public void testTakeImageAutonomy() throws MALInteractionException, MALException {
        // Create Planning Request
        Identifier requestIdentity = new Identifier("Test Image");
        RequestVersionDetails requestVersion = createTakeImageRequest();

        PowerMockito.mockStatic(VisibilityWindow.class, ImageClassifier.class);

        // Setup visibility window mock
        Mockito.when(VisibilityWindow.getWindows(any(TLE.class), anyDouble(), anyDouble(), anyDouble(), any(Time.class), any(Time.class)))
            .then(invocation -> {
                Object[] arguments = invocation.getArguments();
                Double latitude = (Double)arguments[1];
                Double longitude = (Double)arguments[2];
                Time startTime = (Time)arguments[4];
                Time endTime = (Time)arguments[5];
                assertEquals(10d, latitude, 0);
                assertEquals(51d, longitude, 0);

                long visibilityStart = startTime.getValue() + 1000;
                long visibilityEnd = Math.min(startTime.getValue() + 20000, endTime.getValue());
                List<VisibilityWindowItem> visibilityWindows = new ArrayList<>();
                visibilityWindows.add(new VisibilityWindowItem(latitude, longitude, visibilityStart, visibilityEnd));
                return visibilityWindows;
            });

        // Setup image classification mock
        final int[] imagesTaken = new int[1];
        Mockito.when(ImageClassifier.classifyClouds(anyString()))
            .then(invocation -> {
                imagesTaken[0] += 1;

                Object[] arguments = invocation.getArguments();
                String imagePath = (String)arguments[0];
                String expectedImageRegex = String.format("./images/Request_[0-9-]+_[0-9:]+_TakeImage_10,51_activityInstanceId[1-9][0-9]*_iter%d.jpg", imagesTaken[0]);
                assertTrue(String.format("%s does not match expected pattern", imagePath), imagePath.matches(expectedImageRegex));
                File image = new File(imagePath);
                assertTrue(image.exists());

                // First image cloudy, then all clear
                boolean clear = imagesTaken[0] > 1;
                return clear ? CloudyClassification.CLEAR : CloudyClassification.CLOUDY;
            });

        // Monitor Planning Request updates
        PlanningRequestsMonitor requestMonitor = new PlanningRequestsMonitor();
        Subscription requestSubscription = MOFactory.createSubscription(requestIdentity);

        planningRequestServiceConsumer.getPlanningRequestStub()
            .monitorRequestsRegister(requestSubscription, requestMonitor);

        // Monitor Activity updates
        ActivitiesMonitor activityMonitor = new ActivitiesMonitor();
        Subscription activitySubscription = MOFactory.createSubscription();

        planExecutionControlServiceConsumer.getPlanExecutionControlStub()
            .monitorActivitiesRegister(activitySubscription, activityMonitor);

        // Submit Planning Request
        planningRequestServiceConsumer.getPlanningRequestStub().submitRequest(requestIdentity, requestVersion);

        // Wait for Planning Request updates
        synchronized(requestMonitor) {
            try {
                while (requestMonitor.getReceivedUpdateList().size() < 6) {
                    requestMonitor.wait(TEST_MONITOR_TIMEOUT);
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, null, e);
                fail("Exception thrown");
            }
        }

        RequestUpdateDetailsList monitorResponse = requestMonitor.getReceivedUpdateList();
        assertNotNull(monitorResponse);
        assertFalse(monitorResponse.isEmpty());
        assertEquals(6, monitorResponse.size());
        assertEquals(RequestStatus.REQUESTED, monitorResponse.get(0).getStatus());
        assertEquals(RequestStatus.ACCEPTED, monitorResponse.get(1).getStatus());
        assertEquals(RequestStatus.PLANNED, monitorResponse.get(2).getStatus());
        assertEquals(RequestStatus.SCHEDULED, monitorResponse.get(3).getStatus());
        assertEquals(RequestStatus.EXECUTING, monitorResponse.get(4).getStatus());
        assertEquals(RequestStatus.COMPLETED, monitorResponse.get(5).getStatus());

        LOGGER.info("Received expected Request Updates");

        LOGGER.info(requestMonitor.toString());

        LOGGER.info(activityMonitor.toString());

        // Deregister subscriptions
        IdentifierList requestSubscriptionList = new IdentifierList();
        requestSubscriptionList.add(requestSubscription.getSubscriptionId());
        planningRequestServiceConsumer.getPlanningRequestStub().monitorRequestsDeregister(requestSubscriptionList);

        IdentifierList activitySubscriptionList = new IdentifierList();
        activitySubscriptionList.add(activitySubscription.getSubscriptionId());
        planExecutionControlServiceConsumer.getPlanExecutionControlStub().monitorActivitiesDeregister(activitySubscriptionList);
    }

    @Test(timeout=90000)
    public void testScoutAutonomy() throws MALInteractionException, MALException {
        PowerMockito.mockStatic(VisibilityWindow.class, ImageClassifier.class);

        // Setup visibility window mock
        Mockito.when(VisibilityWindow.getWindows(any(TLE.class), anyDouble(), anyDouble(), anyDouble(), any(Time.class), any(Time.class)))
            .then(invocation -> {
                Object[] arguments = invocation.getArguments();
                Double latitude = (Double)arguments[1];
                Double longitude = (Double)arguments[2];
                Time startTime = (Time)arguments[4];
                Time endTime = (Time)arguments[5];
                long visibilityStart = startTime.getValue() + 1000;
                long visibilityEnd = Math.min(startTime.getValue() + 20000, endTime.getValue());
                List<VisibilityWindowItem> visibilityWindows = new ArrayList<>();
                visibilityWindows.add(new VisibilityWindowItem(latitude, longitude, visibilityStart, visibilityEnd));
                return visibilityWindows;
            });

        // Setup image classification mock
        Mockito.when(ImageClassifier.classifyClouds(anyString()))
            .then(invocation -> {
                Object[] arguments = invocation.getArguments();
                String imagePath = (String)arguments[0];
                String expectedImageRegex = "./images/Request_[0-9-]+_[0-9:]+_Scout_10,51_activityInstanceId[1-9][0-9]*_iter1.jpg";
                assertTrue(String.format("%s does not match expected pattern", imagePath), imagePath.matches(expectedImageRegex));
                File image = new File(imagePath);
                assertTrue(image.exists());

                return CloudyClassification.CLEAR;
            });

        Mockito.when(ImageClassifier.classifyInterest(anyString()))
            .then(invocation -> {
                Object[] arguments = invocation.getArguments();
                String imagePath = (String)arguments[0];
                String expectedImageRegex = "./images/Request_[0-9-]+_[0-9:]+_Scout_10,51_activityInstanceId[1-9][0-9]*_iter1.jpg";
                assertTrue(String.format("%s does not match expected pattern", imagePath), imagePath.matches(expectedImageRegex));
                File image = new File(imagePath);
                assertTrue(image.exists());

                List<InterestClassification> interests = new ArrayList<>();
                interests.add(InterestClassification.VOLCANO);
                return interests;
            });

        // Create Planning Request
        Identifier requestIdentity = new Identifier("Test Scout");
        RequestVersionDetails requestVersion = createScoutRequest();

        // Monitor Planning Request updates
        PlanningRequestsMonitor requestMonitor = new PlanningRequestsMonitor();
        Subscription subscription = MOFactory.createSubscription(requestIdentity);

        planningRequestServiceConsumer.getPlanningRequestStub()
            .monitorRequestsRegister(subscription, requestMonitor);

        // Submit Planning Request
        planningRequestServiceConsumer.getPlanningRequestStub().submitRequest(requestIdentity, requestVersion);

        // Wait for Planning Request updates
        synchronized(requestMonitor) {
            try {
                while (requestMonitor.getReceivedUpdateList().size() < 6) {
                    requestMonitor.wait(TEST_MONITOR_TIMEOUT);
                }
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, null, e);
                fail("Exception thrown");
            }
        }

        RequestUpdateDetailsList monitorResponse = requestMonitor.getReceivedUpdateList();
        assertNotNull(monitorResponse);
        assertFalse(monitorResponse.isEmpty());
        assertEquals(6, monitorResponse.size());
        assertEquals(RequestStatus.REQUESTED, monitorResponse.get(0).getStatus());
        assertEquals(RequestStatus.ACCEPTED, monitorResponse.get(1).getStatus());
        assertEquals(RequestStatus.PLANNED, monitorResponse.get(2).getStatus());
        assertEquals(RequestStatus.SCHEDULED, monitorResponse.get(3).getStatus());
        assertEquals(RequestStatus.EXECUTING, monitorResponse.get(4).getStatus());
        assertEquals(RequestStatus.COMPLETED, monitorResponse.get(5).getStatus());

        LOGGER.info("Received expected Request Updates");

        LOGGER.info(requestMonitor.toString());

        // Deregister subscription
        IdentifierList subscriptionList = new IdentifierList();
        subscriptionList.add(subscription.getSubscriptionId());
        planningRequestServiceConsumer.getPlanningRequestStub().monitorRequestsDeregister(subscriptionList);
    }

    private PlanVersionDetails createTakeImagePlan() throws MALException, MALInteractionException {
        long timeNow = Instant.now().toEpochMilli();

        PlanVersionDetails planVersion = MPFactory.createPlanVersion();

        // Get Activity Definition Ids
        ObjectId setAttitudeDefinitionId = SetAttitudeActivity.getDefinitionId();
        ObjectId takeImageDefinitionId = TakeImageActivity.getDefinitionId();

        // Add Activity Instances
        ActivityInstanceDetails setAttitudeActivity = MPFactory.createActivityInstance();
        setAttitudeActivity.setComments("SetAttitude activity");

        Time setAttitudeActivityStart = new Time(timeNow + 2500);
        Time setAttitudeActivityEnd = new Time(timeNow + 4000);
        TimeWindowConstraint setAttitudeTimeWindow = createTimeWindowConstraint(setAttitudeActivityStart, setAttitudeActivityEnd);

        List<Constraint> setAttitudeActivityConstraints = new ArrayList<Constraint>();
        setAttitudeActivityConstraints.add(setAttitudeTimeWindow);

        ConstraintNode setAttitudeActivityConstraintNode = new ConstraintNode();
        setAttitudeActivityConstraintNode.setConstraints(MPPolyFix.encodeConstraints(setAttitudeActivityConstraints));

        setAttitudeActivity.setConstraints(setAttitudeActivityConstraintNode);

        ObjectId setAttitudeActivityId = addActivityDetails(setAttitudeDefinitionId, setAttitudeActivity);


        ActivityInstanceDetails takeImageActivity = MPFactory.createActivityInstance();
        takeImageActivity.setComments("Take Image activity");

        Time takeImageActivityStart = new Time(timeNow + 9000);
        Time takeImageActivityEnd = new Time(timeNow + 10500);
        TimeWindowConstraint takeImageTimeWindow = createTimeWindowConstraint(takeImageActivityStart, takeImageActivityEnd);

        List<Constraint> takeImageActivityConstraints = new ArrayList<Constraint>();
        takeImageActivityConstraints.add(takeImageTimeWindow);

        ConstraintNode takeImageActivityConstraintNode = new ConstraintNode();
        takeImageActivityConstraintNode.setConstraints(MPPolyFix.encodeConstraints(takeImageActivityConstraints));

        takeImageActivity.setConstraints(takeImageActivityConstraintNode);

        ObjectId takeImageActivityId = addActivityDetails(takeImageDefinitionId, takeImageActivity);

        // Add Activity Updates
        StringList latitudeValue = new StringList();
        latitudeValue.add("10");
        Argument latitudeArgument = new Argument(new Identifier("latitude"), latitudeValue, 1);

        StringList longitudeValue = new StringList();
        longitudeValue.add("51");
        Argument longitudeArgument = new Argument(new Identifier("longitude"), longitudeValue, 1);

        StringList fileIdValue = new StringList();
        String fileId = String.format("test_prefix_%d", COMObjectIdHelper.getInstanceId(takeImageActivityId));
        fileIdValue.add(fileId);
        Argument fileIdArgument = new Argument(new Identifier("fileId"), fileIdValue, 1);

        ActivityUpdateDetails setAttitudeActivityStatus = MPFactory.createActivityUpdate(ActivityStatus.PLANNED);

        ArgumentList setAttitudeActivityArguments = new ArgumentList();
        setAttitudeActivityArguments.add(latitudeArgument);
        setAttitudeActivityArguments.add(longitudeArgument);
        setAttitudeActivityStatus.setArguments(setAttitudeActivityArguments);

        TimeTrigger setAttitudeStartTrigger = new TimeTrigger(setAttitudeActivityStart, setAttitudeActivityStart);
        TimeTrigger setAttitudeEndTrigger = new TimeTrigger(setAttitudeActivityEnd, setAttitudeActivityEnd);
        setAttitudeActivityStatus.setStart(MPPolyFix.encode(setAttitudeStartTrigger));
        setAttitudeActivityStatus.setEnd(MPPolyFix.encode(setAttitudeEndTrigger));

        addActivityUpdate(setAttitudeActivityId, setAttitudeActivityStatus);


        ActivityUpdateDetails takeImageActivityStatus = MPFactory.createActivityUpdate(ActivityStatus.PLANNED);

        ArgumentList takeImageActivityArguments = new ArgumentList();
        takeImageActivityArguments.add(latitudeArgument);
        takeImageActivityArguments.add(longitudeArgument);
        takeImageActivityArguments.add(fileIdArgument);
        takeImageActivityStatus.setArguments(takeImageActivityArguments);

        TimeTrigger takeImageStartTrigger = new TimeTrigger(takeImageActivityStart, takeImageActivityStart);
        TimeTrigger takeImageEndTrigger = new TimeTrigger(takeImageActivityEnd, takeImageActivityEnd);
        takeImageActivityStatus.setStart(MPPolyFix.encode(takeImageStartTrigger));
        takeImageActivityStatus.setEnd(MPPolyFix.encode(takeImageEndTrigger));

        addActivityUpdate(takeImageActivityId, takeImageActivityStatus);

        // Create plan
        PlannedActivityList plannedActivities = new PlannedActivityList();

        PlannedActivity setAttitudePlannedActivity = new PlannedActivity();
        setAttitudePlannedActivity.setInstance(setAttitudeActivity);
        setAttitudePlannedActivity.setInstanceId(setAttitudeActivityId.getKey());
        setAttitudePlannedActivity.setUpdate(setAttitudeActivityStatus);
        plannedActivities.add(setAttitudePlannedActivity);

        PlannedActivity takeImagePlannedActivity = new PlannedActivity();
        takeImagePlannedActivity.setInstance(takeImageActivity);
        takeImagePlannedActivity.setInstanceId(takeImageActivityId.getKey());
        takeImagePlannedActivity.setUpdate(takeImageActivityStatus);
        plannedActivities.add(takeImagePlannedActivity);

        PlannedItems plannedItems = new PlannedItems();
        plannedItems.setPlannedActivities(plannedActivities);

        planVersion.setItems(plannedItems);

        return planVersion;
    }

    private RequestVersionDetails createTakeImageRequest() {
        long timeNow = Instant.now().toEpochMilli();

        RequestVersionDetails requestVersion = MPFactory.createRequestVersion();

        requestVersion.setTemplate(MPCameraController.getImageRequestTemplateId());
        requestVersion.setDescription("Take picture at 10,51");

        TimeExpression windowStart = new TimeExpression("==", null, ArgType.TIME, new Time(timeNow + 15000));
        TimeExpression windowEnd = new TimeExpression("==", null, ArgType.TIME, new Time(timeNow + 75000));
        TimeWindow timeWindow = new TimeWindow(windowStart, windowEnd);

        TimeWindowList timeWindows = new TimeWindowList();
        timeWindows.add(timeWindow);
        requestVersion.setValidityTime(timeWindows);

        SimpleActivityDetails simpleActivity = new SimpleActivityDetails();
        ObjectId definitionId = TakeImageActivity.getDefinitionId();
        simpleActivity.setActivityDef(COMObjectIdHelper.getInstanceId(definitionId));

        PositionExpression positionExpression = new PositionExpression("==", null, ArgType.POSITION, new Union("10,51"));
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

    private RequestVersionDetails createScoutRequest() {
        long timeNow = Instant.now().toEpochMilli();

        RequestVersionDetails requestVersion = MPFactory.createRequestVersion();

        requestVersion.setTemplate(MPScoutController.getScoutRequestTemplateId());
        requestVersion.setDescription("Scout at 10,51");

        TimeExpression windowStart = new TimeExpression("==", null, ArgType.TIME, new Time(timeNow + 15000));
        TimeExpression windowEnd = new TimeExpression("==", null, ArgType.TIME, new Time(timeNow + 80000));
        TimeWindow timeWindow = new TimeWindow(windowStart, windowEnd);

        TimeWindowList timeWindows = new TimeWindowList();
        timeWindows.add(timeWindow);
        requestVersion.setValidityTime(timeWindows);

        Argument positionArgument = new Argument();
        positionArgument.setArgName(new Identifier("positions"));
        StringList positionValue = new StringList();
        positionValue.add("10,51");
        positionArgument.setArgValue(positionValue);
        positionArgument.setCount(1);

        Argument interestArgument = new Argument();
        interestArgument.setArgName(new Identifier("interests"));
        StringList interestValues = new StringList();
        interestValues.add("Volcano");
        interestArgument.setArgValue(interestValues);
        positionArgument.setCount(interestValues.size());

        ArgumentList argumentList = new ArgumentList();
        argumentList.add(positionArgument);
        argumentList.add(interestArgument);
        requestVersion.setArguments(argumentList);

        return requestVersion;
    }

    private ObjectId addActivityDetails(ObjectId definitionId, ActivityInstanceDetails activity) throws MALException, MALInteractionException {
        ObjectId source = null;
        MALInteraction interaction = new AppInteraction();
        return mpAdapter.getArchiveManager().ACTIVITY.addInstance(definitionId, activity, source, interaction);
    }

    private ObjectId addActivityUpdate(ObjectId instanceId, ActivityUpdateDetails status) throws MALException, MALInteractionException {
        ObjectId source = null;
        MALInteraction interaction = new AppInteraction();
        return mpAdapter.getArchiveManager().ACTIVITY.addStatus(instanceId, status, source, interaction);
    }

    private TimeWindowConstraint createTimeWindowConstraint(Time startTime, Time endTime) {
        TimeExpression windowStart = new TimeExpression("==", null, ArgType.TIME, startTime);
        TimeExpression windowEnd = new TimeExpression("==", null, ArgType.TIME, endTime);

        TimeWindow timeWindow = new TimeWindow(windowStart, windowEnd);

        TimeWindowList timeWindows = new TimeWindowList();
        timeWindows.add(timeWindow);
        TimeWindowConstraint timeWindowConstraint = new TimeWindowConstraint();
        timeWindowConstraint.setTimeWindows(timeWindows);
        return timeWindowConstraint;
    }
}
