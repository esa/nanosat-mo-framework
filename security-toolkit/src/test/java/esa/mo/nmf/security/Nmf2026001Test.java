/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.security;

import java.io.File;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

/**
 * NMF-2026-001: unsafe deserialization and wildcard bind in the spacecraft
 * simulator's TCP transport.
 * <p>
 * The test runs the current simulator inside a container and drives it with
 * {@link esa.mo.nmf.security.probe.SecurityProbe}. It asserts that the current
 * code binds to the configured loopback address only and rejects a class that
 * is not on its deserialization allow-list. The differential test against the
 * vulnerable 4.0 release lives in a separate test.
 */
public class Nmf2026001Test {

    private static final String BASE_IMAGE = "eclipse-temurin:21-jre";

    private static final String MAIN_SERVER = "esa.mo.nmf.mission.orekit.simulator.main.MainServer";

    /** The simulator fat jar, built by the assembly-with-dependencies profile. */
    private static final File SIM_JAR = new File(System.getProperty("nmf.security.sim.jar",
            "../nmf-mission-simulator-orekit/cubesat-spacecraft-simulator/target/"
                    + "cubesat-spacecraft-simulator-jar-with-dependencies.jar"));

    /** The compiled probe classes, copied into the image next to the simulator. */
    private static final File PROBE_CLASSES = new File(System.getProperty("nmf.security.probe.classes",
            "target/classes"));

    @Test
    public void currentCodeBindsToLoopbackAndRejectsUnlistedClasses() {
        Assert.assertTrue("Build the simulator fat jar first (mvn -Passembly-with-dependencies install): "
                + SIM_JAR.getAbsolutePath(), SIM_JAR.isFile());
        Assert.assertTrue("Compile the probe first (mvn test-compile): " + PROBE_CLASSES.getAbsolutePath(),
                PROBE_CLASSES.isDirectory());

        StringBuilder logs = new StringBuilder();
        try (GenericContainer<?> simulator = new GenericContainer<>(BASE_IMAGE)) {
            simulator
                    .withCopyFileToContainer(MountableFile.forHostPath(SIM_JAR.getAbsolutePath()), "/app/sim.jar")
                    .withCopyFileToContainer(MountableFile.forHostPath(PROBE_CLASSES.getAbsolutePath()), "/app/probe")
                    .withCommand("java", "-cp", "/app/sim.jar:/app/probe",
                            "esa.mo.nmf.security.probe.SecurityProbe", MAIN_SERVER)
                    .withLogConsumer(frame -> logs.append(frame.getUtf8String()))
                    .waitingFor(Wait.forLogMessage(".*RESULT PROBE_DONE.*", 1)
                            .withStartupTimeout(Duration.ofMinutes(3)));
            simulator.start();
        }

        String probeReport = report(logs.toString());
        System.out.println();
        System.out.println("===== NMF-2026-001: spacecraft simulator TCP transport =====");
        System.out.println("simulator version under test: " + versionFrom(logs.toString()));
        System.out.println(probeReport);
        System.out.println("This version is expected to bind to loopback only and reject the canary.");
        System.out.println("============================================================");

        Assert.assertTrue("The bind check did not run. Probe report:\n" + probeReport,
                probeReport.contains("RESULT BIND="));
        Assert.assertTrue("The deserialization check did not run. Probe report:\n" + probeReport,
                probeReport.contains("RESULT DESER="));
        Assert.assertTrue("The current server must bind to the loopback address only, but the probe "
                + "reached it off loopback. Probe report:\n" + probeReport,
                probeReport.contains("RESULT BIND=LOOPBACK_ONLY"));
        Assert.assertTrue("The current server must reject a class that is not on its allow-list, but the "
                + "probe's canary was instantiated. Probe report:\n" + probeReport,
                probeReport.contains("RESULT DESER=REJECTED"));
    }

    /**
     * Keeps only the probe's own lines from the container output, dropping the
     * simulator's startup logging, and strips the {@code SECPROBE} marker.
     */
    private static String report(String containerOutput) {
        StringBuilder sb = new StringBuilder();
        for (String line : containerOutput.split("\n")) {
            int marker = line.indexOf("SECPROBE ");
            if (marker >= 0) {
                sb.append("  ").append(line.substring(marker + "SECPROBE ".length()).trim()).append('\n');
            }
        }
        return sb.toString();
    }

    /** Pulls the version the probe reported from the container output. */
    private static String versionFrom(String containerOutput) {
        for (String line : containerOutput.split("\n")) {
            int marker = line.indexOf("RESULT VERSION=");
            if (marker >= 0) {
                return line.substring(marker + "RESULT VERSION=".length()).trim();
            }
        }
        return "unknown";
    }
}
