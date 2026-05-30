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
import esa.mo.nmf.testbed.performance.cli.CLIResult;
import java.io.File;
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
 * {@link #tearDownClass()} and uploaded as a CI artifact together with one
 * {@code .log} file per run.
 *
 * <p>
 * Tiers reflect the depth of network interaction:
 * <ul>
 *   <li>Tier 1 — no network (pure JVM + picocli startup cost)
 *   <li>Tier 2 — one MAL round-trip after connecting to the Supervisor
 *   <li>Tier 3 — two MAL round-trips (e.g. archive query + file write)
 * </ul>
 */
public class CLIPerformanceTest {

    private static final SupervisorHarness supervisor = new SupervisorHarness();
    private static CLIHarness cli;
    private static final PerformanceResults results = new PerformanceResults();
    private static File paramDumpFile;

    @BeforeClass
    public static void setUpClass() throws IOException {
        supervisor.setUp();
        cli = new CLIHarness();
        paramDumpFile = File.createTempFile("nmf-perf-param-dump-", ".csv");
        paramDumpFile.deleteOnExit();
    }

    @AfterClass
    public static void tearDownClass() throws IOException {
        results.save();
        supervisor.tearDown();
    }

    @Test
    public void testTier1Help() throws IOException, InterruptedException {
        CLIResult r1 = cli.run("--help");
        CLIResult r2 = cli.run("--help");
        CLIResult r3 = cli.run("--help");
        results.record("tier-1-help", r1, r2, r3);
    }

    @Test
    public void testTier2ParameterList() throws IOException, InterruptedException {
        String uri = supervisor.getDirectoryURI();
        CLIResult r1 = cli.run("parameter", "list", "-r", uri);
        CLIResult r2 = cli.run("parameter", "list", "-r", uri);
        CLIResult r3 = cli.run("parameter", "list", "-r", uri);
        results.record("tier-2-parameter-list", r1, r2, r3);
    }

    @Test
    public void testTier2FindPackages() throws IOException, InterruptedException {
        String uri = supervisor.getDirectoryURI();
        CLIResult r1 = cli.run("software-management", "findPackage", "*", "-r", uri);
        CLIResult r2 = cli.run("software-management", "findPackage", "*", "-r", uri);
        CLIResult r3 = cli.run("software-management", "findPackage", "*", "-r", uri);
        results.record("tier-2-find-packages", r1, r2, r3);
    }

    @Test
    public void testTier3ParameterGet() throws IOException, InterruptedException {
        String uri = supervisor.getDirectoryURI();
        String dumpPath = paramDumpFile.getAbsolutePath();
        CLIResult r1 = cli.run("parameter", "get", dumpPath, "-r", uri);
        CLIResult r2 = cli.run("parameter", "get", dumpPath, "-r", uri);
        CLIResult r3 = cli.run("parameter", "get", dumpPath, "-r", uri);
        results.record("tier-3-parameter-get", r1, r2, r3);
    }

}
