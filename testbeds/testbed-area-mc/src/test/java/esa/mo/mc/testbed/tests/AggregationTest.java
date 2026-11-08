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
import esa.mo.mc.testbed.backends.MutableParameterBackend;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationAdapter;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationStub;
import org.ccsds.moims.mo.mc.aggregation.consumer.MonitorValueSubscriptionKeys;
import org.ccsds.moims.mo.mc.structures.AggregationCategory;
import org.ccsds.moims.mo.mc.structures.AggregationDefinition;
import org.ccsds.moims.mo.mc.structures.AggregationDefinitionList;
import org.ccsds.moims.mo.mc.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.structures.AggregationValue;
import org.ccsds.moims.mo.mc.structures.GenerationMode;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterDefinitionList;
import org.ccsds.moims.mo.mc.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.structures.ThresholdFilter;
import org.ccsds.moims.mo.mc.structures.ThresholdType;
import org.ccsds.moims.mo.mc.structures.ValidityState;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the reports of the MC Aggregation service, received through the
 * monitorValue PUB-SUB operation.
 * <p>
 * Each test uses its own aggregation, with a report interval of one second,
 * and removes it at the end. Note: when an aggregation is added, the provider
 * publishes a first report at once, and its stored values are then reset. So
 * the first two reports never compare their values with a previous report.
 */
public class AggregationTest {

    private static final Logger LOGGER = Logger.getLogger(AggregationTest.class.getName());
    private static final SetUpProvidersAndConsumers harness = new SetUpProvidersAndConsumers();

    private static final MutableParameterBackend BACKEND = new MutableParameterBackend();

    // The reports received, per aggregation name
    private static final Map<String, BlockingQueue<AggregationValue>> REPORTS = new ConcurrentHashMap<>();
    private static Subscription subscription;

    @BeforeClass
    public static void setUpClass() throws Exception {
        harness.setUp(false, false, true, true, BACKEND);

        subscription = ConnectionConsumer.subscriptionWildcardRandom();
        stub().monitorValueRegister(subscription, new AggregationAdapter() {
            @Override
            public void monitorValueNotifyReceived(MALMessageHeader msgHeader,
                    Identifier subscriptionId, UpdateHeader updateHeader,
                    MonitorValueSubscriptionKeys keys, AggregationValue newValue,
                    Map qosProperties) {
                reports(keys.getAggregationName().getValue()).add(newValue);
            }
        });
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        IdentifierList ids = new IdentifierList();
        ids.add(subscription.getSubscriptionId());
        stub().monitorValueDeregister(ids);
        harness.tearDown();
    }

    // Test — Each value of a report carries its definitionId and sampleTime

    @Test
    public void testReportValuesCarryDefinitionIdAndSampleTime() throws Exception {
        LOGGER.info("Running: testReportValuesCarryDefinitionIdAndSampleTime()");
        BACKEND.counter("Ids_Counter");
        LongList paramIds = new LongList();
        paramIds.add(addParameter("Ids_Fixed", 0));
        paramIds.add(addParameter("Ids_Counter", 0));

        Time start = Time.now();
        Long aggId = addAggregation("Ids", true, false, 0, null, paramIds);
        try {
            for (int i = 0; i < 3; i++) {
                AggregationValue report = nextReport("Ids", 3);
                Time received = Time.now();
                ParameterValueList values = values(report);

                Assert.assertEquals("The report must have one value per parameter", 2, values.size());
                for (int k = 0; k < values.size(); k++) {
                    ParameterValue value = values.get(k);
                    Assert.assertEquals("The values must be in the order of the parameter set",
                            paramIds.get(k), value.getDefinitionId());
                    long sampled = value.getSampleTime().getValue();
                    Assert.assertTrue("The sampleTime must be between the start and the reception",
                            sampled >= start.getValue() && sampled <= received.getValue());
                }
            }
        } finally {
            removeAggregation(aggId);
        }
    }

    // Test — With sendUnchanged FALSE, an unchanged value is left out of the report

    @Test
    public void testSendUnchangedFalseLeavesOutUnchangedValues() throws Exception {
        LOGGER.info("Running: testSendUnchangedFalseLeavesOutUnchangedValues()");
        BACKEND.counter("Unchanged_Counter");
        LongList paramIds = new LongList();
        paramIds.add(addParameter("Unchanged_Fixed", 0));
        paramIds.add(addParameter("Unchanged_Counter", 0));

        Long aggId = addAggregation("Unchanged", false, false, 0, null, paramIds);
        try {
            List<AggregationValue> reports = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                reports.add(nextReport("Unchanged", 3));
            }

            Assert.assertEquals("The first report must send every value", 2, values(reports.get(0)).size());
            // The reports after the first two compare with a previous report
            for (AggregationValue report : reports.subList(2, 4)) {
                ParameterValueList values = values(report);
                Assert.assertEquals("An unchanged value must be left out", 1, values.size());
                Assert.assertEquals("The changed value must be sent",
                        paramIds.get(1), values.get(0).getDefinitionId());
            }
        } finally {
            removeAggregation(aggId);
        }
    }

    // Test — A periodic parameter is VALID, changed or not, as every sample reads it

    @Test
    public void testPeriodicParametersAreNotExpired() throws Exception {
        LOGGER.info("Running: testPeriodicParametersAreNotExpired()");
        BACKEND.counter("Expired_Counter");
        LongList paramIds = new LongList();
        paramIds.add(addParameter("Expired_Fixed", 1));
        paramIds.add(addParameter("Expired_Counter", 1));

        Long aggId = addAggregation("Expired", true, false, 0, null, paramIds);
        try {
            for (int i = 0; i < 4; i++) {
                ParameterValueList values = values(nextReport("Expired", 3));

                Assert.assertEquals("The report must keep one value per parameter", 2, values.size());
                Assert.assertEquals("The values must be in the order of the parameter set",
                        paramIds.get(0), values.get(0).getDefinitionId());
                Assert.assertEquals("The values must be in the order of the parameter set",
                        paramIds.get(1), values.get(1).getDefinitionId());
                Assert.assertEquals("An unchanged periodic parameter must be VALID",
                        ValidityState.VALID, values.get(0).getValidityState());
                Assert.assertEquals("A changing periodic parameter must be VALID",
                        ValidityState.VALID, values.get(1).getValidityState());
            }
        } finally {
            removeAggregation(aggId);
        }
    }

    // Test — enableFilter changes the filter, and leaves the reporting as it is

    @Test
    public void testEnableFilterChangesOnlyTheFilter() throws Exception {
        LOGGER.info("Running: testEnableFilterChangesOnlyTheFilter()");
        BACKEND.set("Filter_Fixed", 10);
        LongList paramIds = new LongList();
        paramIds.add(addParameter("Filter_Fixed", 0));
        ThresholdFilter filter = new ThresholdFilter(ThresholdType.DELTA, new Union(5));

        Long aggId = addAggregation("Filter", true, false, 0, filter, paramIds);
        LongList aggIds = new LongList();
        aggIds.add(aggId);
        try {
            nextReport("Filter", 3);

            // With the filter on, a value that does not change is not reported
            stub().enableFilter(true, aggIds);
            Thread.sleep(2500); // The reports of the first two samples
            reports("Filter").clear();
            Assert.assertEquals("A filtered aggregation must not report an unchanged value",
                    0, collect("Filter", 3).size());

            // With the filter off again, the periodic reports must come back
            stub().enableFilter(false, aggIds);
            Assert.assertTrue("An aggregation without a filter must report every period",
                    collect("Filter", 3.5).size() >= 2);
        } finally {
            removeAggregation(aggId);
        }
    }

    // Test — A filtered aggregation reports on its timeout, and when its filter is crossed

    @Test
    public void testFilteredAggregationReportsOnTimeoutAndOnChange() throws Exception {
        LOGGER.info("Running: testFilteredAggregationReportsOnTimeoutAndOnChange()");
        BACKEND.set("Timeout_Fixed", 10);
        LongList paramIds = new LongList();
        paramIds.add(addParameter("Timeout_Fixed", 0));
        ThresholdFilter filter = new ThresholdFilter(ThresholdType.DELTA, new Union(5));

        Long aggId = addAggregation("Timeout", true, true, 2, filter, paramIds);
        try {
            // Wait for a timeout report, so that the next period comes before the next timeout
            AggregationValue report;
            do {
                report = nextReport("Timeout", 4);
            } while (report.getGenerationMode() != GenerationMode.FILTERED_TIMEOUT);

            BACKEND.set("Timeout_Fixed", 20); // A change of 10 crosses the threshold of 5
            report = nextReport("Timeout", 1.8);
            Assert.assertEquals("A crossed filter must trigger a periodic report",
                    GenerationMode.PERIODIC, report.getGenerationMode());
            Assert.assertTrue("The report of a crossed filter must say it was filtered", report.getFiltered());
            Assert.assertEquals("The report must carry the new value",
                    Integer.valueOf(20), ((Union) values(report).get(0).getRawValue()).getIntegerValue());

            // The value no longer changes: only the timeout reports it
            reports("Timeout").clear();
            List<AggregationValue> reports = collect("Timeout", 4.5);
            Assert.assertFalse("The timeout must report an unchanged value", reports.isEmpty());
            for (AggregationValue r : reports) {
                Assert.assertEquals("An unchanged value must be reported only on the timeout",
                        GenerationMode.FILTERED_TIMEOUT, r.getGenerationMode());
            }
        } finally {
            removeAggregation(aggId);
        }
    }

    private static AggregationStub stub() {
        return harness.getAggregationConsumerStub().getAggregationStub();
    }

    private static BlockingQueue<AggregationValue> reports(String aggregationName) {
        return REPORTS.computeIfAbsent(aggregationName, k -> new LinkedBlockingQueue<>());
    }

    /**
     * Returns the values of the single parameter set of a report.
     */
    private static ParameterValueList values(AggregationValue report) {
        Assert.assertEquals("The report must have one parameter set", 1, report.getParameterSetValues().size());
        return report.getParameterSetValues().get(0).getValues();
    }

    private static AggregationValue nextReport(String aggregationName, double timeoutSeconds) throws Exception {
        AggregationValue report = reports(aggregationName).poll((long) (timeoutSeconds * 1000), TimeUnit.MILLISECONDS);
        Assert.assertNotNull("A report of " + aggregationName + " must arrive within "
                + timeoutSeconds + " seconds", report);
        return report;
    }

    /**
     * Returns the reports received during the given time.
     */
    private static List<AggregationValue> collect(String aggregationName, double seconds) throws Exception {
        Thread.sleep((long) (seconds * 1000));
        List<AggregationValue> out = new ArrayList<>();
        reports(aggregationName).drainTo(out);
        return out;
    }

    /**
     * Registers an integer parameter and returns its id.
     */
    private static Long addParameter(String name, double reportIntervalSeconds) throws Exception {
        ParameterDefinition def = new ParameterDefinition(new Identifier(name),
                "A test parameter", AttributeType.INTEGER, true,
                new Duration(reportIntervalSeconds), true);
        ParameterDefinitionList defs = new ParameterDefinitionList();
        defs.add(def);
        LongList ids = harness.getParameterProvider().addParameters(defs, null);
        Assert.assertEquals("One id must be returned", 1, ids.size());
        return ids.get(0);
    }

    /**
     * Adds a periodic aggregation with a report interval of one second and a
     * single parameter set, and returns its id.
     */
    private static Long addAggregation(String name, boolean sendUnchanged, boolean filterEnabled,
            double filteredTimeoutSeconds, ThresholdFilter filter, LongList paramIds) throws Exception {
        AggregationParameterSetList sets = new AggregationParameterSetList();
        sets.add(new AggregationParameterSet(null, paramIds, new Duration(0), filter));
        AggregationDefinition def = new AggregationDefinition(new Identifier(name),
                "A test aggregation", AggregationCategory.GENERAL, new Duration(1),
                sendUnchanged, filterEnabled, new Duration(filteredTimeoutSeconds), true, sets);
        AggregationDefinitionList defs = new AggregationDefinitionList();
        defs.add(def);
        LongList ids = harness.getAggregationProvider().addAggregation(defs, null);
        Assert.assertEquals("One id must be returned", 1, ids.size());
        return ids.get(0);
    }

    private static void removeAggregation(Long id) throws Exception {
        LongList ids = new LongList();
        ids.add(id);
        harness.getAggregationProvider().removeAggregation(ids, null);
    }

}
