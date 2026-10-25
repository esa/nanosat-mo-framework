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
package esa.mo.nmf.testbed.e2e.tests;

import esa.mo.nmf.testbed.e2e.PackageManagementHarness;
import esa.mo.nmf.testbed.e2e.SupervisorHarness;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static esa.mo.nmf.testbed.e2e.tests.SharedOutput.LOGGER;
import static esa.mo.nmf.testbed.e2e.tests.SharedOutput.SEP;
import static esa.mo.nmf.testbed.e2e.tests.SharedOutput.SETUP_CLASS_MSG;
import static esa.mo.nmf.testbed.e2e.tests.SharedOutput.SETUP_CLASS_SEP;

/**
 * The testbed runs in audit mode, like the playground, and its calls from
 * ground present the ground credential that the filesystem was generated with,
 * so that audit mode records only what an App does without permission.
 */
public class AccessControlAuditTest {

    private static final SupervisorHarness supervisorHarness = new SupervisorHarness();
    private static final PackageManagementHarness pm = new PackageManagementHarness(supervisorHarness);

    @BeforeClass
    public static void startSupervisor() throws IOException {
        LOGGER.info(SETUP_CLASS_SEP + "\n" + SETUP_CLASS_MSG + "\n" + SETUP_CLASS_SEP);
        supervisorHarness.setUp();
        pm.connect();
    }

    @AfterClass
    public static void stopSupervisor() throws IOException {
        supervisorHarness.tearDown();
    }

    @Test
    public void theTestbedRunsInAuditMode() throws IOException {
        LOGGER.info(SEP + "\nRunning: theTestbedRunsInAuditMode()\n" + SEP);
        File config = new File(new File(supervisorHarness.getNmfDir(), "bootloader"), "config.properties");
        String text = new String(Files.readAllBytes(config.toPath()), StandardCharsets.UTF_8);
        Assert.assertTrue("The testbed filesystem must be generated in audit mode:\n" + text,
                text.contains("access-control=audit"));
    }

    @Test
    public void theCallsOfTheTestbedPresentTheGroundCredential() throws Exception {
        LOGGER.info(SEP + "\nRunning: theCallsOfTheTestbedPresentTheGroundCredential()\n" + SEP);
        // A call that needs a permission: without the ground credential, audit mode would flag it
        pm.findPackageByPrefix("benchmark-");
        Thread.sleep(500);

        List<String> flagged = supervisorHarness.getProviderLog().stream()
                .filter(line -> line.contains("carries no valid credentials"))
                .collect(Collectors.toList());
        Assert.assertTrue("The calls of the testbed must present the ground credential, but these were flagged: "
                + flagged, flagged.isEmpty());
    }
}
