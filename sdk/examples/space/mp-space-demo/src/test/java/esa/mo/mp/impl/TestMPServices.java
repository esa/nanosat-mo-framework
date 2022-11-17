package esa.mo.mp.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mp.plandistribution.body.GetPlanResponse;
import org.ccsds.moims.mo.mp.plandistribution.body.GetPlanStatusResponse;
import org.ccsds.moims.mo.mp.plandistribution.body.ListPlansResponse;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planningrequest.body.GetRequestStatusResponse;
import org.ccsds.moims.mo.mp.planningrequest.body.SubmitRequestResponse;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.EventInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mp.structures.PlanFilter;
import org.ccsds.moims.mo.mp.structures.PlanStatus;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestFilter;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetailsList;
import org.junit.BeforeClass;
import org.junit.Test;
import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
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
import esa.mo.nmf.NMFException;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;

/**
 * Test class to test Mission Planning Services.
 */
public class TestMPServices {

    private static final long TEST_MONITOR_TIMEOUT = 5000; // in ms

    private static final Logger LOGGER = Logger.getLogger(TestMPServices.class.getName());

    private static MPServicesTestAdapter adapter;
    private static NanoSatMOConnectorImpl connector;

    private static PlanInformationManagementConsumerServiceImpl pimServiceConsumer;
    private static PlanningRequestConsumerServiceImpl planningRequestServiceConsumer;
    private static PlanDistributionConsumerServiceImpl planDistributionServiceConsumer;
    private static PlanEditConsumerServiceImpl planEditServiceConsumer;
    private static PlanExecutionControlConsumerServiceImpl planExecutionControlServiceConsumer;

    @BeforeClass
    public static void setup() throws MalformedURLException, MALException, MALInteractionException, NMFException {
        System.setProperty("provider.properties", "src/test/resources/testProvider.properties");
        System.setProperty("esa.mo.nanosatmoframework.provider.settings", "src/test/resources/testProvider.properties");
        System.setProperty("transport.properties", "src/test/resources/testTransport.properties");

        // Provider setup
        adapter = new MPServicesTestAdapter();
        connector = new NanoSatMOConnectorImpl();
        adapter.setNMF(connector);
        connector.init(adapter);
        connector.getCOMServices().getArchiveService().wipe();

        PlanInformationManagementProviderServiceImpl pimServiceProvider = connector.getMPServices()
            .getPlanInformationManagementService();
        PlanningRequestProviderServiceImpl planningRequestServiceProvider = connector.getMPServices()
            .getPlanningRequestService();
        PlanDistributionProviderServiceImpl planDistributionServiceProvider = connector.getMPServices()
            .getPlanDistributionService();
        PlanEditProviderServiceImpl planEditServiceProvider = connector.getMPServices().getPlanEditService();
        PlanExecutionControlProviderServiceImpl planExecutionControlServiceProvider = connector.getMPServices()
            .getPlanExecutionControlService();

        // Consumer setup
        COMServicesConsumer comServicesConsumer = new COMServicesConsumer();

        SingleConnectionDetails pimConnection = pimServiceProvider.getConnectionProvider().getConnectionDetails();
        pimServiceConsumer = new PlanInformationManagementConsumerServiceImpl(pimConnection, comServicesConsumer);
        comServicesConsumer.init(pimServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails prsConnection = planningRequestServiceProvider.getConnectionProvider()
            .getConnectionDetails();
        planningRequestServiceConsumer = new PlanningRequestConsumerServiceImpl(prsConnection, comServicesConsumer);
        comServicesConsumer.init(planningRequestServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails pdsConnection = planDistributionServiceProvider.getConnectionProvider()
            .getConnectionDetails();
        planDistributionServiceConsumer = new PlanDistributionConsumerServiceImpl(pdsConnection, comServicesConsumer);
        comServicesConsumer.init(planDistributionServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails pedConnection = planEditServiceProvider.getConnectionProvider().getConnectionDetails();
        planEditServiceConsumer = new PlanEditConsumerServiceImpl(pedConnection, comServicesConsumer);
        comServicesConsumer.init(planEditServiceConsumer.getConnectionConsumer());

        SingleConnectionDetails pecConnection = planExecutionControlServiceProvider.getConnectionProvider()
            .getConnectionDetails();
        planExecutionControlServiceConsumer = new PlanExecutionControlConsumerServiceImpl(pecConnection,
            comServicesConsumer);
        comServicesConsumer.init(planExecutionControlServiceConsumer.getConnectionConsumer());

        try {
            Thread.sleep(1000); // Wait for DB init
        } catch (InterruptedException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
        LOGGER.info("Setup finished");
    }

    @Test
    public void testPIMAddRequestDef() throws MALInteractionException, MALException {
        // Setup
        String identity = "Add Test";
        ObjectInstancePairList response = addTestRequestDef(identity);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.size());

        // Teardown
        removeTestRequestDef(response);
    }

    @Test
    public void testPIMAddInvalidRequestDef() throws MALInteractionException, MALException {
        try {
            // Setup
            String invalidIdentity = "*";
            addTestRequestDef(invalidIdentity);

            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertEquals(COMHelper.INVALID_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("[0]", e.getStandardError().getExtraInformation().toString());
        }
    }

    @Test
    public void testPIMAddDuplicateRequestDef() throws MALInteractionException, MALException {
        ObjectInstancePairList initialResponse = null;
        try {
            // Setup
            String identity = "Duplicate Add Test";
            initialResponse = addTestRequestDef(identity);
            addTestRequestDef(identity);

            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertNotNull(initialResponse);
            assertEquals(COMHelper.DUPLICATE_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("[0]", e.getStandardError().getExtraInformation().toString());
        } finally {
            // Teardown
            removeTestRequestDef(initialResponse);
        }
    }

    @Test
    public void testPIMUpdateRequestDef() throws MALInteractionException, MALException {
        // Setup
        String identity = "Update Test";
        ObjectInstancePairList addResponse = addTestRequestDef(identity);

        RequestTemplateDetails updatedRequestDef = new RequestTemplateDetails();

        LongList identityIdList = new LongList();
        identityIdList.add(addResponse.get(0).getObjectIdentityInstanceId());
        RequestTemplateDetailsList updatedRequestDefList = new RequestTemplateDetailsList();
        updatedRequestDefList.add(updatedRequestDef);

        LongList updateResponse = pimServiceConsumer.getPlanInformationManagementStub().updateRequestDef(identityIdList,
            updatedRequestDefList);

        // Assert
        assertNotNull(updateResponse);
        assertEquals(1, updateResponse.size());
        assertNotNull(updateResponse.get(0));

        // Teardown
        removeTestRequestDef(addResponse);
    }

    @Test
    public void testPIMUpdateInvalidRequestDef() throws MALInteractionException, MALException {
        try {
            // Setup
            LongList identityIdList = new LongList();
            identityIdList.add(0L);
            RequestTemplateDetails updatedRequestDef = new RequestTemplateDetails();

            RequestTemplateDetailsList updatedRequestDefList = new RequestTemplateDetailsList();
            updatedRequestDefList.add(updatedRequestDef);
            pimServiceConsumer.getPlanInformationManagementStub().updateRequestDef(identityIdList,
                updatedRequestDefList);

            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertEquals(COMHelper.INVALID_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("[0]", e.getStandardError().getExtraInformation().toString());
        }
    }

    @Test
    public void testPIMUpdateUnknownRequestDef() throws MALInteractionException, MALException {
        try {
            // Setup
            LongList identityIdList = new LongList();
            identityIdList.add(100L);
            RequestTemplateDetails updatedRequestDef = new RequestTemplateDetails();

            RequestTemplateDetailsList updatedRequestDefList = new RequestTemplateDetailsList();
            updatedRequestDefList.add(updatedRequestDef);
            pimServiceConsumer.getPlanInformationManagementStub().updateRequestDef(identityIdList,
                updatedRequestDefList);

            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertEquals(MALHelper.UNKNOWN_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("[0]", e.getStandardError().getExtraInformation().toString());
        }
    }

    @Test
    public void testPIMListRequestDefs() throws MALInteractionException, MALException {
        String identity = "List Test";
        ObjectInstancePairList addResponse = addTestRequestDef(identity);

        IdentifierList identityList = new IdentifierList();
        identityList.add(new Identifier(identity));
        ObjectInstancePairList listResponse = pimServiceConsumer.getPlanInformationManagementStub().listRequestDefs(
            identityList);

        // Assert
        assertNotNull(listResponse);
        assertEquals(1, listResponse.size());
        assertNotNull(listResponse.get(0));
        assertEquals(addResponse, listResponse);

        // Teardown
        removeTestRequestDef(addResponse);
    }

    @Test
    public void testPIMGetRequestDef() throws MALInteractionException, MALException {
        // Setup
        String identity = "Get Test";
        ObjectInstancePairList addResponse = addTestRequestDef(identity);

        LongList identityIdList = new LongList();
        identityIdList.add(addResponse.get(0).getObjectIdentityInstanceId());
        RequestTemplateDetailsList getResponse = pimServiceConsumer.getPlanInformationManagementStub().getRequestDef(
            identityIdList);

        // Assert
        assertNotNull(getResponse);
        assertEquals(1, getResponse.size());
        assertNotNull(getResponse.get(0));

        // Teardown
        removeTestRequestDef(addResponse);
    }

    @Test
    public void testPIMRemoveRequestDef() throws MALInteractionException, MALException {
        // Setup
        String identity = "Remove Test";
        ObjectInstancePairList addResponse = addTestRequestDef(identity);

        LongList identityIdList = new LongList();
        identityIdList.add(addResponse.get(0).getObjectIdentityInstanceId());
        pimServiceConsumer.getPlanInformationManagementStub().removeRequestDef(identityIdList);
        // No exceptions thrown
    }

    @Test
    public void testPRSSubmitRequest() throws MALInteractionException, MALException {

        SubmitRequestResponse response = submitTestPlanningRequest();

        assertNotNull(response);
        LOGGER.info("Received submitted Request Version Id: " + response.getBodyElement1());
    }

    @Test
    public void testPRSSubmitRequestInvalidTemplate() throws MALInteractionException, MALException {
        try {
            // Setup
            Identifier requestIdentity = new Identifier("Test Request " + new Random().nextInt());
            RequestVersionDetails requestVersion = new RequestVersionDetails();
            ObjectId invalidTemplate = COMObjectIdHelper.getObjectId(0L,
                PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);
            requestVersion.setTemplate(invalidTemplate);
            planningRequestServiceConsumer.getPlanningRequestStub().submitRequest(requestIdentity, requestVersion);
            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertEquals(COMHelper.INVALID_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("Invalid Request Template Id", e.getStandardError().getExtraInformation().toString());
        }
    }

    @Test
    public void testPRSUpdateRequest() throws MALInteractionException, MALException {

        SubmitRequestResponse response = submitTestPlanningRequest();

        RequestVersionDetails requestVersion = new RequestVersionDetails();

        Long requestIdentityId = response.getBodyElement0();
        Long updatedRequestVersionId = planningRequestServiceConsumer.getPlanningRequestStub().updateRequest(
            requestIdentityId, requestVersion);

        assertNotNull(updatedRequestVersionId);
        LOGGER.info("Received updated Request Version Id: " + updatedRequestVersionId);
    }

    @Test
    public void testPRSCancelRequest() throws MALInteractionException, MALException {

        SubmitRequestResponse response = submitTestPlanningRequest();

        Long requestIdentityId = response.getBodyElement0();
        planningRequestServiceConsumer.getPlanningRequestStub().cancelRequest(requestIdentityId);
        LOGGER.info("Request Version cancelled with no exceptions");
    }

    @Test
    public void testPRSGetRequestStatus() throws MALInteractionException, MALException {

        SubmitRequestResponse submitResponse = submitTestPlanningRequest();

        RequestFilter requestFilter = new RequestFilter();
        LongList ids = new LongList();
        ids.add(submitResponse.getBodyElement0());
        requestFilter.setRequestIdentityId(ids);

        GetRequestStatusResponse response = planningRequestServiceConsumer.getPlanningRequestStub().getRequestStatus(
            requestFilter);
        assertNotNull(response);
        assertEquals(1, response.getBodyElement0().size());
        assertNotNull(response.getBodyElement0().get(0));
        assertEquals(1, response.getBodyElement1().size());
        assertNotNull(response.getBodyElement1().get(0));
        assertEquals(1, response.getBodyElement2().size());
        assertNotNull(response.getBodyElement2().get(0));
        LOGGER.info("Received Request Update Status: " + response.getBodyElement2());
    }

    @Test
    public void testPRSGetRequest() throws MALInteractionException, MALException {

        SubmitRequestResponse submitResponse = submitTestPlanningRequest();

        RequestFilter requestFilter = new RequestFilter();
        LongList ids = new LongList();
        ids.add(submitResponse.getBodyElement0());
        requestFilter.setRequestIdentityId(ids);

        GetRequestMonitor monitor = new GetRequestMonitor();
        Short numberOfUpdates = planningRequestServiceConsumer.getPlanningRequestStub().getRequest(requestFilter,
            monitor);

        // Wait for updates
        synchronized (monitor) {
            try {
                monitor.wait(TEST_MONITOR_TIMEOUT);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, null, e);
                fail("Exception thrown");
            }
        }

        Short expectedNumberOfUpdates = 0;
        assertEquals(expectedNumberOfUpdates, numberOfUpdates);

        RequestVersionDetailsList response = monitor.getReceivedRequestVersions();
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    public void testPRSMonitorRequests() throws MALInteractionException, MALException {
        PlanningRequestsMonitor monitor = new PlanningRequestsMonitor();
        Subscription subscription = MOFactory.createSubscription();

        // Register subscription
        planningRequestServiceConsumer.getPlanningRequestStub().monitorRequestsRegister(subscription, monitor);

        submitTestPlanningRequest();

        // Wait for updates
        synchronized (monitor) {
            try {
                monitor.wait(TEST_MONITOR_TIMEOUT);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, null, e);
                fail("Exception thrown");
            }
        }

        RequestUpdateDetailsList response = monitor.getReceivedUpdateList();
        assertNotNull(response);
        assertFalse(response.isEmpty());
        LOGGER.info("Received Request Updates: " + response);

        // Deregister subscription
        IdentifierList subscriptionList = new IdentifierList();
        subscriptionList.add(subscription.getSubscriptionId());
        planningRequestServiceConsumer.getPlanningRequestStub().monitorRequestsDeregister(subscriptionList);
    }

    @Test
    public void testPDSListPlans() throws MALInteractionException, MALException {
        PlanFilter planFilter = new PlanFilter();
        planFilter.setReturnAll(true);

        ListPlansResponse response = planDistributionServiceConsumer.getPlanDistributionStub().listPlans(planFilter);

        assertNotNull(response);
        assertFalse(response.getBodyElement3().isEmpty());
    }

    @Test
    public void testPDSGetPlan() throws MALInteractionException, MALException {
        LongList ids = new LongList();
        ids.add(COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId()));

        GetPlanResponse response = planDistributionServiceConsumer.getPlanDistributionStub().getPlan(ids);

        assertNotNull(response);
        assertFalse(response.getBodyElement1().isEmpty());
    }

    @Test
    public void testPDSGetPlanStatus() throws MALInteractionException, MALException {
        LongList ids = new LongList();
        ids.add(COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId()));

        GetPlanStatusResponse response = planDistributionServiceConsumer.getPlanDistributionStub().getPlanStatus(ids);

        assertNotNull(response);
        assertFalse(response.getBodyElement1().isEmpty());
    }

    @Test
    public void testPDSQueryPlan() throws MALInteractionException, MALException {
        QueryPlanMonitor monitor = new QueryPlanMonitor();

        PlanFilter planFilter = new PlanFilter();
        planFilter.setReturnAll(true);

        Short numberOfUpdates = planDistributionServiceConsumer.getPlanDistributionStub().queryPlan(planFilter,
            monitor);

        // Wait for updates
        synchronized (monitor) {
            try {
                monitor.wait(TEST_MONITOR_TIMEOUT);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, null, e);
                fail("Exception thrown");
            }
        }

        Short expectedNumberOfUpdates = 0;
        assertEquals(expectedNumberOfUpdates, numberOfUpdates);

        PlanVersionDetailsList response = monitor.getReceivedPlanVersions();
        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    public void testPDSMonitorPlan() throws MALInteractionException, MALException {
        PlanMonitor monitor = new PlanMonitor();
        Subscription subscription = MOFactory.createSubscription();

        // Register subscription
        planDistributionServiceConsumer.getPlanDistributionStub().monitorPlanRegister(subscription, monitor);

        submitTestPlanningRequest();

        // Wait for updates
        synchronized (monitor) {
            try {
                monitor.wait(TEST_MONITOR_TIMEOUT);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, null, e);
                fail("Exception thrown");
            }
        }

        PlanVersionDetailsList response = monitor.getReceivedPlanVersions();
        assertNotNull(response);
        assertFalse(response.isEmpty());
        LOGGER.info("Received Plan Versions: " + response);

        // Deregister subscription
        IdentifierList subscriptionList = new IdentifierList();
        subscriptionList.add(subscription.getSubscriptionId());
        planDistributionServiceConsumer.getPlanDistributionStub().monitorPlanDeregister(subscriptionList);
    }

    @Test
    public void testPDSMonitorPlanStatus() throws MALInteractionException, MALException {
        PlanStatusMonitor monitor = new PlanStatusMonitor();
        Subscription subscription = MOFactory.createSubscription();

        // Register subscription
        planDistributionServiceConsumer.getPlanDistributionStub().monitorPlanStatusRegister(subscription, monitor);

        submitTestPlanningRequest();

        // Wait for updates
        synchronized (monitor) {
            try {
                monitor.wait(TEST_MONITOR_TIMEOUT);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, null, e);
                fail("Exception thrown");
            }
        }

        PlanUpdateDetailsList response = monitor.getReceivedPlanStatuses();
        assertNotNull(response);
        assertFalse(response.isEmpty());
        LOGGER.info("Received Plan Statuses: " + response);

        // Deregister subscription
        IdentifierList subscriptionList = new IdentifierList();
        subscriptionList.add(subscription.getSubscriptionId());
        planDistributionServiceConsumer.getPlanDistributionStub().monitorPlanStatusDeregister(subscriptionList);
    }

    @Test
    public void testPEDInsertActivity() throws MALInteractionException, MALException {
        // Setup
        Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
        Long activityDefId = insertActivityDef("Insert activity test");
        ActivityInstanceDetails activity = MPFactory.createActivityInstance();
        activity.setComments("Insert activity test");

        planEditServiceConsumer.getPlanEditStub().insertActivity(planIdentityId, activityDefId, activity);
    }

    @Test
    public void testPEDInsertActivityInvalidPlan() throws MALInteractionException, MALException {
        try {
            // Setup
            Long invalidPlanIdentityId = 0L;
            Long activityDefId = insertActivityDef("Insert activity invalid plan test");
            ActivityInstanceDetails activity = MPFactory.createActivityInstance();
            activity.setComments("Insert activity invalid plan test");

            planEditServiceConsumer.getPlanEditStub().insertActivity(invalidPlanIdentityId, activityDefId, activity);

            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertEquals(MALHelper.UNKNOWN_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("Unknown Plan Identity Id", e.getStandardError().getExtraInformation().toString());
        }
    }

    @Test
    public void testPEDInsertActivityDuplicate() throws MALInteractionException, MALException {
        try {
            // Setup
            Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
            Long activityDefId = insertActivityDef("Insert duplicate activity test");
            ActivityInstanceDetails activity = MPFactory.createActivityInstance();
            activity.setComments("Insert duplicate activity test");

            planEditServiceConsumer.getPlanEditStub().insertActivity(planIdentityId, activityDefId, activity);
            planEditServiceConsumer.getPlanEditStub().insertActivity(planIdentityId, activityDefId, activity);

            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertEquals(COMHelper.DUPLICATE_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("Given Activity already exists in the plan", e.getStandardError().getExtraInformation()
                .toString());
        }
    }

    @Test
    public void testPEDUpdateActivity() throws MALInteractionException, MALException {
        // Setup
        Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
        Long activityDefId = insertActivityDef("Update activity test");
        ActivityInstanceDetails initialActivity = MPFactory.createActivityInstance();
        initialActivity.setComments("Update activity test");

        planEditServiceConsumer.getPlanEditStub().insertActivity(planIdentityId, activityDefId, initialActivity);

        Long activityInstanceId = 1L; // TODO
        ActivityInstanceDetails updatedActivity = MPFactory.createActivityInstance();
        updatedActivity.setComments("Updated activity");

        planEditServiceConsumer.getPlanEditStub().updateActivity(planIdentityId, activityInstanceId, updatedActivity);
    }

    @Test
    public void testPEDUpdateActivityInvalid() throws MALInteractionException, MALException {
        try {
            Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
            Long invalidActivityInstanceId = -1L; // TODO
            ActivityInstanceDetails updatedActivity = MPFactory.createActivityInstance();

            planEditServiceConsumer.getPlanEditStub().updateActivity(planIdentityId, invalidActivityInstanceId,
                updatedActivity);

            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertEquals(COMHelper.INVALID_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("Given Activity does not exist in the plan", e.getStandardError().getExtraInformation()
                .toString());
        }
    }

    @Test
    public void testPEDDeleteActivity() throws MALInteractionException, MALException {
        // Setup
        Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
        Long activityDefId = insertActivityDef("Delete activity test");
        ActivityInstanceDetails activity = MPFactory.createActivityInstance();
        activity.setComments("Delete activity test");

        planEditServiceConsumer.getPlanEditStub().insertActivity(planIdentityId, activityDefId, activity);

        Long activityInstanceId = 1L; // TODO

        planEditServiceConsumer.getPlanEditStub().deleteActivity(planIdentityId, activityInstanceId);
    }

    @Test
    public void testPEDInsertEvent() throws MALInteractionException, MALException {
        Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
        Long eventDefId = insertEventDef("Insert event test");
        EventInstanceDetails event = MPFactory.createEventInstance(1L);

        planEditServiceConsumer.getPlanEditStub().insertEvent(planIdentityId, eventDefId, event);
    }

    @Test
    public void testPEDInsertEventDuplicate() throws MALInteractionException, MALException {
        try {
            // Setup
            Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
            Long eventDefId = insertEventDef("Insert duplicate event test");
            EventInstanceDetails event = MPFactory.createEventInstance(2L);

            planEditServiceConsumer.getPlanEditStub().insertEvent(planIdentityId, eventDefId, event);
            planEditServiceConsumer.getPlanEditStub().insertEvent(planIdentityId, eventDefId, event);

            fail("No exception thrown");
        } catch (MALInteractionException e) {
            // Assert
            assertEquals(COMHelper.DUPLICATE_ERROR_NUMBER, e.getStandardError().getErrorNumber());
            assertEquals("Given Event already exists in the plan", e.getStandardError().getExtraInformation()
                .toString());
        }
    }

    @Test
    public void testPEDUpdateEvent() throws MALInteractionException, MALException {
        // Setup
        Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
        Long eventDefId = insertEventDef("Update event test");
        EventInstanceDetails initialEvent = MPFactory.createEventInstance(3L);

        planEditServiceConsumer.getPlanEditStub().insertEvent(planIdentityId, eventDefId, initialEvent);

        Long eventInstanceId = 1L; // TODO
        EventInstanceDetails updatedEvent = MPFactory.createEventInstance(4L);

        planEditServiceConsumer.getPlanEditStub().updateEvent(planIdentityId, eventInstanceId, updatedEvent);
    }

    @Test
    public void testPEDDeleteEvent() throws MALInteractionException, MALException {
        // Setup
        Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
        Long eventDefId = insertEventDef("Delete event test");
        EventInstanceDetails event = MPFactory.createEventInstance(5L);

        planEditServiceConsumer.getPlanEditStub().insertEvent(planIdentityId, eventDefId, event);

        Long eventInstanceId = 1L; // TODO

        planEditServiceConsumer.getPlanEditStub().deleteEvent(planIdentityId, eventInstanceId);
    }

    @Test
    public void testPEDUpdatePlanStatus() throws MALInteractionException, MALException {
        Long planIdentityId = COMObjectIdHelper.getInstanceId(adapter.getPlanIdentityId());
        PlanStatus planStatus = PlanStatus.RELEASED;

        planEditServiceConsumer.getPlanEditStub().updatePlanStatus(planIdentityId, planStatus);
    }

    private ObjectInstancePairList addTestRequestDef(String identity) throws MALInteractionException, MALException {
        RequestTemplateDetails requestTemplate = new RequestTemplateDetails();
        requestTemplate.setDescription("Test description");
        IdentifierList identities = new IdentifierList();
        identities.add(new Identifier(identity));
        RequestTemplateDetailsList definitions = new RequestTemplateDetailsList();
        definitions.add(requestTemplate);

        return pimServiceConsumer.getPlanInformationManagementStub().addRequestDef(identities, definitions);
    }

    private void removeTestRequestDef(ObjectInstancePairList list) throws MALInteractionException, MALException {
        List<Long> ids = list.stream().map(pair -> pair.getObjectIdentityInstanceId()).collect(Collectors.toList());

        LongList identityIds = new LongList();
        identityIds.addAll(ids);
        pimServiceConsumer.getPlanInformationManagementStub().removeRequestDef(identityIds);
    }

    private SubmitRequestResponse submitTestPlanningRequest() throws MALInteractionException, MALException {
        Identifier requestIdentity = new Identifier("Test Request " + new Random().nextInt());
        RequestVersionDetails requestVersion = new RequestVersionDetails();

        return planningRequestServiceConsumer.getPlanningRequestStub().submitRequest(requestIdentity, requestVersion);
    }

    private Long insertActivityDef(String identity) throws MALInteractionException, MALException {
        IdentifierList identities = new IdentifierList();
        identities.add(new Identifier(identity));
        ActivityDefinitionDetailsList definitions = new ActivityDefinitionDetailsList();
        definitions.add(new ActivityDefinitionDetails());
        ObjectInstancePairList pairs = pimServiceConsumer.getPlanInformationManagementStub().addActivityDef(identities,
            definitions);
        return pairs.get(0).getObjectInstanceId();
    }

    private Long insertEventDef(String identity) throws MALInteractionException, MALException {
        IdentifierList identities = new IdentifierList();
        identities.add(new Identifier(identity));
        EventDefinitionDetailsList definitions = new EventDefinitionDetailsList();
        definitions.add(new EventDefinitionDetails());
        ObjectInstancePairList pairs = pimServiceConsumer.getPlanInformationManagementStub().addEventDef(identities,
            definitions);
        return pairs.get(0).getObjectInstanceId();
    }
}
