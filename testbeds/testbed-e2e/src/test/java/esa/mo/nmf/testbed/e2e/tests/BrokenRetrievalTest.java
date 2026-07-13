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
package esa.mo.nmf.testbed.e2e.tests;

import esa.mo.nmf.testbed.e2e.AppHarness;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.IOException;
import java.util.List;
import org.ccsds.moims.mo.mc.structures.AggregationValueDetailsList;
import org.ccsds.moims.mo.mc.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ParameterValueDetailsList;
import org.ccsds.moims.mo.mc.structures.ValidityState;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * End-to-end tests that exercise retrieval of deliberately broken MC items from
 * the {@code broken-app-for-testbeds} app: a parameter, an action and an
 * aggregation that all fail on retrieval, each checked against a healthy
 * counterpart used as a control.
 */
public class BrokenRetrievalTest extends NMFTest {

    private static final long ACTION_TIMEOUT_MS = 15_000;

    // Item names as registered by BrokenMCAdapter. Referenced as literals
    // because the app is an nmfpack dependency, not on the test classpath.
    private static final String PARAM_HEALTHY = "Healthy_Parameter";
    private static final String PARAM_BROKEN = "Broken_Parameter";
    private static final String AGG_HEALTHY = "Healthy_Aggregation";
    private static final String AGG_BROKEN = "Broken_Aggregation";
    private static final String ACTION_HEALTHY = "Healthy_Action";
    private static final String ACTION_BROKEN = "Broken_Action";

    private static final SupervisorHarness supervisorHarness = new SupervisorHarness();

    private final AppHarness app = new AppHarness("broken-app-for-testbeds", supervisorHarness);

    @BeforeClass
    public static void startSupervisor() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        supervisorHarness.setUp();
    }

    @AfterClass
    public static void stopSupervisor() throws IOException {
        supervisorHarness.tearDown();
    }

    @After
    public void ensureAppStopped() {
        try {
            if (app.isRunning()) {
                app.kill();
                Thread.sleep(2000);
            }
        } catch (Exception ignored) {
        }
    }

    // -------------------------------------------------------------------------
    // Parameter
    // -------------------------------------------------------------------------

    @Test
    public void testBrokenParameterIsInvalid() throws Exception {
        LOGGER.info(SEP + "\nRunning: testBrokenParameterIsInvalid()\n" + SEP);
        app.start();

        ValidityState broken = firstValidity(app.getParameterValues(List.of(PARAM_BROKEN)));
        Assert.assertEquals("A parameter whose read throws must come back as INVALID_RAW"
                + app.getDiagnostics(), ValidityState.INVALID_RAW, broken);

        ValidityState healthy = firstValidity(app.getParameterValues(List.of(PARAM_HEALTHY)));
        Assert.assertEquals("The healthy parameter must read as VALID (control)",
                ValidityState.VALID, healthy);
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    @Test
    public void testBrokenActionReportsFailure() throws Exception {
        LOGGER.info(SEP + "\nRunning: testBrokenActionReportsFailure()\n" + SEP);
        app.start();

        Boolean brokenOutcome = app.awaitActionOutcome(ACTION_BROKEN, ACTION_TIMEOUT_MS);
        Assert.assertNotNull("An END stage must be reported for the broken action"
                + app.getDiagnostics(), brokenOutcome);
        Assert.assertFalse("The broken action must report execution failure (success=false)",
                brokenOutcome);

        Boolean healthyOutcome = app.awaitActionOutcome(ACTION_HEALTHY, ACTION_TIMEOUT_MS);
        Assert.assertNotNull("An END stage must be reported for the healthy action"
                + app.getDiagnostics(), healthyOutcome);
        Assert.assertTrue("The healthy action must report success (control)", healthyOutcome);
    }

    // -------------------------------------------------------------------------
    // Aggregation
    // -------------------------------------------------------------------------

    @Test
    public void testBrokenAggregationSamplesInvalid() throws Exception {
        LOGGER.info(SEP + "\nRunning: testBrokenAggregationSamplesInvalid()\n" + SEP);
        app.start();

        ValidityState broken = firstAggregatedValidity(app.getAggregationValues(List.of(AGG_BROKEN)));
        Assert.assertEquals("An aggregation over a broken parameter must sample it as INVALID_RAW"
                + app.getDiagnostics(), ValidityState.INVALID_RAW, broken);

        ValidityState healthy = firstAggregatedValidity(app.getAggregationValues(List.of(AGG_HEALTHY)));
        Assert.assertEquals("The healthy aggregation must sample its parameter as VALID (control)",
                ValidityState.VALID, healthy);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static ValidityState firstValidity(ParameterValueDetailsList details) {
        Assert.assertFalse("No parameter value was returned", details.isEmpty());
        return details.get(0).getValue().getValidityState();
    }

    private static ValidityState firstAggregatedValidity(AggregationValueDetailsList details) {
        Assert.assertFalse("No aggregation value was returned", details.isEmpty());
        ParameterValue value = details.get(0).getValue()
                .getParameterSetValues().get(0)
                .getValues().get(0)
                .getValue();
        return value.getValidityState();
    }

}
