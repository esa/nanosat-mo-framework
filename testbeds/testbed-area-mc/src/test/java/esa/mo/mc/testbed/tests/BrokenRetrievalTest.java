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
import esa.mo.mc.testbed.backends.BrokenBackend;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.consumer.ActionAdapter;
import org.ccsds.moims.mo.mc.action.consumer.ActionStub;
import org.ccsds.moims.mo.mc.action.consumer.MonitorExecutionSubscriptionKeys;
import org.ccsds.moims.mo.mc.structures.*;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests that the MC services correctly surface a failing backend on retrieval:
 * a parameter and an aggregation come back with an INVALID_RAW validity state,
 * and an action execution is reported as failed. The healthy path is covered by
 * {@link ParameterTest}. Run in-process (no Supervisor); the full end-to-end
 * equivalent lives in testbed-e2e.
 */
public class BrokenRetrievalTest {

    private static final Logger LOGGER = Logger.getLogger(BrokenRetrievalTest.class.getName());
    private static final SetUpProvidersAndConsumers harness = new SetUpProvidersAndConsumers();

    @BeforeClass
    public static void setUpClass() throws IOException {
        // Action + Parameter + Aggregation, all backed by the always-failing backend.
        harness.setUp(true, false, true, true, new BrokenBackend());
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        harness.tearDown();
    }

    @Test
    public void testBrokenParameterIsInvalid() throws Exception {
        LOGGER.info("Running: testBrokenParameterIsInvalid()");
        Long id = addParameter("Broken_Parameter");

        LongList ids = new LongList();
        ids.add(id);
        ParameterValueDetailsList result = harness.getParameterConsumerStub()
                .getParameterStub().getValue(ids);

        Assert.assertEquals("One value must be returned", 1, result.size());
        Assert.assertEquals("A parameter whose read throws must come back as INVALID_RAW",
                ValidityState.INVALID_RAW, result.get(0).getValue().getValidityState());
    }

    @Test
    public void testBrokenAggregationSamplesInvalid() throws Exception {
        LOGGER.info("Running: testBrokenAggregationSamplesInvalid()");
        Long paramId = addParameter("Broken_Agg_Parameter");

        AggregationParameterSetList sets = new AggregationParameterSetList();
        LongList paramIds = new LongList();
        paramIds.add(paramId);
        sets.add(new AggregationParameterSet(null, paramIds, new Duration(0), null));
        AggregationDefinition aggDef = new AggregationDefinition(new Identifier("Broken_Aggregation"),
                "Aggregates the broken parameter.", AggregationCategory.GENERAL,
                new Duration(0), true, false, false, new Duration(0), false, sets);
        AggregationDefinitionList aggDefs = new AggregationDefinitionList();
        aggDefs.add(aggDef);
        LongList aggIds = harness.getAggregationProvider().addAggregation(aggDefs, null);

        AggregationValueDetailsList result = harness.getAggregationConsumerStub()
                .getAggregationStub().getValue(aggIds);

        Assert.assertEquals("One aggregation value must be returned", 1, result.size());
        ParameterValue sampled = result.get(0).getValue()
                .getParameterSetValues().get(0)
                .getValues().get(0)
                .getValue();
        Assert.assertEquals("An aggregation over a broken parameter must sample it as INVALID_RAW",
                ValidityState.INVALID_RAW, sampled.getValidityState());
    }

    @Test
    public void testBrokenActionReportsFailure() throws Exception {
        LOGGER.info("Running: testBrokenActionReportsFailure()");
        ActionDefinition def = new ActionDefinition(new Identifier("Broken_Action"),
                "An action that always fails.", new UShort(0), new ArgumentDefinitionList());
        ActionDefinitionList defs = new ActionDefinitionList();
        defs.add(def);
        Long actionId = harness.getActionProvider().addAction(defs, null).get(0);

        Boolean outcome = awaitActionOutcome(actionId, 10_000);
        Assert.assertNotNull("An END stage must be reported for the broken action", outcome);
        Assert.assertFalse("The broken action must report execution failure (success=false)", outcome);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static Long addParameter(String name) throws Exception {
        ParameterDefinition def = new ParameterDefinition(new Identifier(name),
                "A parameter whose read always fails", AttributeType.INTEGER,
                false, new Duration(0), false);
        ParameterDefinitionList defs = new ParameterDefinitionList();
        defs.add(def);
        return harness.getParameterProvider().addParameters(defs, null).get(0);
    }

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

}
