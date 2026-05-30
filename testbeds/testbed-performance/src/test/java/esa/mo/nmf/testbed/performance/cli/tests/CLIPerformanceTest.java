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
package esa.mo.nmf.testbed.performance.cli.tests;

import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import esa.mo.nmf.testbed.performance.PerformanceResults;
import esa.mo.nmf.testbed.performance.cli.CLIHarness;
import java.io.IOException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Performance tests for the NMF CLI Tool.
 *
 * <p>
 * Each test method measures a specific CLI command by running it three times
 * against a live in-process Supervisor, then records the results via
 * {@link PerformanceResults}. The results file is written in
 * {@link #tearDownClass()} and uploaded as a CI artifact.
 *
 * <p>
 * Test methods are added in step 3 of the CLI performance plan.
 */
public class CLIPerformanceTest {

    private static final SupervisorHarness supervisor = new SupervisorHarness();
    private static CLIHarness cli;
    private static final PerformanceResults results = new PerformanceResults();

    @BeforeClass
    public static void setUpClass() throws IOException {
        supervisor.setUp();
        cli = new CLIHarness();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        results.save();
        supervisor.tearDown();
    }

    @Test
    public void placeholder() {
        // TODO: performance test methods to be added in step 3
    }

}
