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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
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

    // -------------------------------------------------------------------------
    // Helpers
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
        }

        @Override
        public Attribute onGetValue(Long parameterID) {
            return new Union(0);
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            return true;
        }
    }

}
