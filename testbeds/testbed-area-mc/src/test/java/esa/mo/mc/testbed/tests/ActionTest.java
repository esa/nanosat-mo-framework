/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.mc.testbed.tests;

import esa.mo.mc.testbed.SetUpProvidersAndConsumers;
import esa.mo.mc.testbed.backends.Backend;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.action.consumer.ActionAdapter;
import org.ccsds.moims.mo.mc.action.consumer.ActionStub;
import org.ccsds.moims.mo.mc.action.consumer.MonitorExecutionSubscriptionKeys;
import org.ccsds.moims.mo.mc.structures.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Nominal tests for the MC Action service: a consumer triggers an action, the
 * provider forwards it to the backend's {@code actionArrived} handler, and the
 * execution is reported as successful over monitorExecution. The failure path
 * is covered by {@link BrokenRetrievalTest}. Run in-process (no Supervisor);
 * the full end-to-end equivalent lives in testbed-e2e.
 */
public class ActionTest {

    private static final Logger LOGGER = Logger.getLogger(ActionTest.class.getName());
    private static final SetUpProvidersAndConsumers harness = new SetUpProvidersAndConsumers();

    private static final RecordingActionBackend BACKEND = new RecordingActionBackend();

    // An action that reports its progress steps
    private static final String PROGRESS_ACTION = "Progress_Action";
    private static final int PROGRESS_STEPS = 2;

    @BeforeClass
    public static void setUpClass() throws IOException {
        // Action only, backed by a handler that always succeeds.
        harness.setUp(true, false, false, false, BACKEND);
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
    }

    // Test — Triggering an action runs the backend handler and reports success

    @Test
    public void testExecuteActionSucceeds() throws Exception {
        LOGGER.info("Running: testExecuteActionSucceeds()");
        ActionDefinition def = new ActionDefinition(new Identifier("Nominal_Action"),
                "An action that always succeeds.", new UShort(0), new ArgumentDefinitionList());
        ActionDefinitionList defs = new ActionDefinitionList();
        defs.add(def);
        Long actionId = harness.getActionProvider().addAction(defs, null).get(0);

        int before = BACKEND.invocations.get();
        Boolean outcome = awaitActionOutcome(actionId, 10_000);

        Assert.assertNotNull("An END stage must be reported for the action", outcome);
        Assert.assertTrue("A nominal action must report execution success (success=true)", outcome);
        Assert.assertEquals("The backend actionArrived handler must have been invoked once",
                before + 1, BACKEND.invocations.get());
    }

    // Test — An argument of the wrong type is rejected, also without the argumentIds

    @Test
    public void testWrongArgumentTypeIsRejected() throws Exception {
        LOGGER.info("Running: testWrongArgumentTypeIsRejected()");
        ArgumentDefinitionList args = new ArgumentDefinitionList();
        args.add(new ArgumentDefinition(new Identifier("count"), AttributeType.INTEGER));
        ActionDefinition def = new ActionDefinition(new Identifier("Typed_Action"),
                "An action with one Integer argument.", new UShort(0), args);
        ActionDefinitionList defs = new ActionDefinitionList();
        defs.add(def);
        Long actionId = harness.getActionProvider().addAction(defs, null).get(0);

        AttributeValueList values = new AttributeValueList();
        values.add(new AttributeValue(new Union("not an Integer")));
        ActionStub stub = harness.getActionConsumerStub().getActionStub();

        int before = BACKEND.invocations.get();
        try {
            stub.executeAction(new ExecutionRequest(actionId, values, null));
            Assert.fail("An argument of the wrong type must be rejected");
        } catch (InvalidArgumentException ex) {
            UIntegerList indexes = (UIntegerList) ex.getExtraInformation();
            Assert.assertEquals("The error must name the wrong argument", 1, indexes.size());
            Assert.assertEquals("The error must name the wrong argument", 0, indexes.get(0).getValue());
        }
        Assert.assertEquals("A rejected action must not reach the backend",
                before, BACKEND.invocations.get());
    }

    // Test — listExecutions returns the executions with their stages

    @Test
    public void testListExecutionsReturnsTheStages() throws Exception {
        LOGGER.info("Running: testListExecutionsReturnsTheStages()");
        Long actionId = addAction("History_Action");
        ActionStub stub = harness.getActionConsumerStub().getActionStub();
        Time before = Time.now();
        Long first = stub.executeAction(new ExecutionRequest(actionId, new AttributeValueList(), null));
        Long second = stub.executeAction(new ExecutionRequest(actionId, new AttributeValueList(), null));

        ActionExecutionList executions = awaitEnded(actionId, 2);
        Assert.assertEquals("Both executions must be listed", 2, executions.size());
        Assert.assertEquals("The oldest execution comes first", first, executions.get(0).getExecutionId());
        Assert.assertEquals("The most recent execution comes last", second, executions.get(1).getExecutionId());
        for (ActionExecution execution : executions) {
            Assert.assertEquals("The request must name the definition",
                    actionId, execution.getRequest().getDefinitionId());
            Assert.assertTrue("The request time must be the time of the request",
                    execution.getRequestTime().getValue() >= before.getValue());
            ExecutionStageList stages = execution.getStages();
            Assert.assertEquals("An action without progress reaches START and END", 2, stages.size());
            Assert.assertEquals("The first stage must be START",
                    ExecutionStageType.START, stages.get(0).getStatus().getStageType());
            Assert.assertEquals("The last stage must be END",
                    ExecutionStageType.END, stages.get(1).getStatus().getStageType());
            Assert.assertTrue("The action must have succeeded", stages.get(1).getStatus().getSuccess());
            Assert.assertTrue("The stages must be in the order in which they were reached",
                    stages.get(0).getTime().getValue() <= stages.get(1).getTime().getValue());
            Assert.assertTrue("A stage cannot come before the request",
                    stages.get(0).getTime().getValue() >= execution.getRequestTime().getValue());
        }

        // maxCount keeps the most recent
        UInteger one = new UInteger(1);
        ActionExecutionList latest = stub.listExecutions(ids(actionId), null, null, one);
        Assert.assertEquals("maxCount must limit the executions", 1, latest.size());
        Assert.assertEquals("maxCount must keep the most recent", second, latest.get(0).getExecutionId());

        // The wildcard lists the executions of every action
        ActionExecutionList all = stub.listExecutions(ids(0L), null, null, null);
        Assert.assertTrue("The wildcard must list these executions",
                all.stream().filter(e -> actionId.equals(e.getRequest().getDefinitionId())).count() == 2);

        // A window after both requests matches none
        Time after = new Time(Time.now().getValue() + 60_000);
        Assert.assertTrue("A window after the requests must match none",
                stub.listExecutions(ids(actionId), after, null, null).isEmpty());
    }

    // Test — listExecutions returns the PROGRESS stages between START and END

    @Test
    public void testListExecutionsReturnsTheProgressStages() throws Exception {
        LOGGER.info("Running: testListExecutionsReturnsTheProgressStages()");
        ActionDefinition def = new ActionDefinition(new Identifier(PROGRESS_ACTION),
                "An action that reports its progress.", new UShort(PROGRESS_STEPS), new ArgumentDefinitionList());
        ActionDefinitionList defs = new ActionDefinitionList();
        defs.add(def);
        Long actionId = harness.getActionProvider().addAction(defs, null).get(0);
        harness.getActionConsumerStub().getActionStub()
                .executeAction(new ExecutionRequest(actionId, new AttributeValueList(), null));

        ExecutionStageList stages = awaitEnded(actionId, 1).get(0).getStages();
        List<ExecutionStageType> types = new ArrayList<>();
        for (ExecutionStage stage : stages) {
            types.add(stage.getStatus().getStageType());
        }
        Assert.assertEquals("The stages must be START, each PROGRESS step and END, in that order",
                Arrays.asList(ExecutionStageType.START, ExecutionStageType.PROGRESS,
                        ExecutionStageType.PROGRESS, ExecutionStageType.END), types);
        Assert.assertEquals("The first PROGRESS stage must be step 1", 1, stages.get(1).getStatus().getStep().getValue());
        Assert.assertEquals("The second PROGRESS stage must be step 2", 2, stages.get(2).getStatus().getStep().getValue());
        for (int i = 1; i < stages.size(); i++) {
            Assert.assertTrue("The stages must be in the order in which they were reached",
                    stages.get(i - 1).getTime().getValue() <= stages.get(i).getTime().getValue());
        }
    }

    // Test — listExecutions refuses an unknown definition, and a reversed window

    @Test
    public void testListExecutionsRefusesBadRequests() throws Exception {
        LOGGER.info("Running: testListExecutionsRefusesBadRequests()");
        ActionStub stub = harness.getActionConsumerStub().getActionStub();
        try {
            stub.listExecutions(ids(987654L), null, null, null);
            Assert.fail("An unknown definition must be refused");
        } catch (UnknownException expected) {
            // Refused
        }
        Time now = Time.now();
        try {
            stub.listExecutions(ids(0L), new Time(now.getValue() + 1000), now, null);
            Assert.fail("A startTime after the endTime must be refused");
        } catch (InvalidArgumentException expected) {
            // Refused
        }
    }

    // -------------------------------------------------------------------------
    // Helpers

    private static Long addAction(String name) throws Exception {
        ActionDefinition def = new ActionDefinition(new Identifier(name),
                "An action that always succeeds.", new UShort(0), new ArgumentDefinitionList());
        ActionDefinitionList defs = new ActionDefinitionList();
        defs.add(def);
        return harness.getActionProvider().addAction(defs, null).get(0);
    }

    private static LongList ids(Long id) {
        LongList ids = new LongList();
        ids.add(id);
        return ids;
    }

    /**
     * Waits until the given number of executions of an action have reached
     * their END stage, and returns them.
     */
    private static ActionExecutionList awaitEnded(Long actionId, int count) throws Exception {
        ActionStub stub = harness.getActionConsumerStub().getActionStub();
        long deadline = System.currentTimeMillis() + 10_000;
        ActionExecutionList executions;
        do {
            executions = stub.listExecutions(ids(actionId), null, null, null);
            long ended = executions.stream().filter(e -> !e.getStages().isEmpty()
                    && ExecutionStageType.END.equals(
                            e.getStages().get(e.getStages().size() - 1).getStatus().getStageType())).count();
            if (ended == count) {
                return executions;
            }
            Thread.sleep(100);
        } while (System.currentTimeMillis() < deadline);
        return executions;
    }
    // -------------------------------------------------------------------------

    /**
     * Executes the action and returns the success flag of its monitorExecution
     * END stage, or {@code null} if none was received within the timeout.
     */
    private static Boolean awaitActionOutcome(Long actionDefId, long timeoutMs) throws Exception {
        ActionStub stub = harness.getActionConsumerStub().getActionStub();
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Boolean> outcome = new AtomicReference<>();
        Subscription sub = ConnectionConsumer.subscriptionWildcardRandom();
        stub.monitorExecutionRegister(sub, new ActionAdapter() {
            @Override
            public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader,
                    Identifier subscriptionId, UpdateHeader updateHeader,
                    MonitorExecutionSubscriptionKeys keys, Boolean success,
                    UShort step, String comment, java.util.Map qosProperties) {
                if (actionDefId.equals(keys.getDefinitionId())
                        && ExecutionStageType.END.equals(keys.getStageType())) {
                    outcome.set(success);
                    latch.countDown();
                }
            }
        });
        try {
            // Let the subscription register before the near-instant 0-stage action.
            Thread.sleep(300);
            stub.executeAction(new ExecutionRequest(actionDefId, new AttributeValueList(), null));
            latch.await(timeoutMs, TimeUnit.MILLISECONDS);
            return outcome.get();
        } finally {
            IdentifierList subIds = new IdentifierList();
            subIds.add(sub.getSubscriptionId());
            stub.monitorExecutionDeregister(subIds);
        }
    }

    /**
     * A backend whose action handler always succeeds and counts its invocations,
     * so the test can assert the provider actually forwarded the execution.
     */
    private static final class RecordingActionBackend extends Backend {

        private final AtomicInteger invocations = new AtomicInteger(0);

        @Override
        public void actionArrived(Identifier identifier, AttributeValueList attributeValues,
                Long executionId, MALInteraction interaction) throws ExecutionFailedException {
            invocations.incrementAndGet();
            if (PROGRESS_ACTION.equals(identifier.getValue())) {
                try {
                    for (int step = 1; step <= PROGRESS_STEPS; step++) {
                        harness.getActionProvider().reportExecutionProgress(true, null, step,
                                PROGRESS_STEPS, executionId);
                    }
                } catch (IOException ex) {
                    throw new ExecutionFailedException(ex.getMessage());
                }
            }
        }

        @Override
        public Attribute onGetValue(Identifier identifier) {
            return new Union(0);
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            return true;
        }
    }

}
