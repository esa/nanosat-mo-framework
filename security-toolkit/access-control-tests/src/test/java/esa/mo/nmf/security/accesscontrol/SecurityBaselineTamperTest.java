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
package esa.mo.nmf.security.accesscontrol;

import java.io.File;
import java.nio.file.Paths;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

/**
 * Security baseline tamper detection, end to end: the real barebone Supervisor
 * boots with an access-control value in {@code config.properties} that differs
 * from the one the filesystem was generated with (recorded in the root-locked
 * {@code security-baseline.properties}), and both the bootloader and the
 * Supervisor must warn about the change.
 *
 * <p>The barebone filesystem is generated with {@code enforce}, so writing
 * {@code off} into {@code config.properties} is a downgrade, which must be
 * warned about; writing {@code enforce} matches the baseline and must not.
 *
 * <p>Prerequisite (otherwise the test is skipped, not failed): the barebone
 * filesystem generated with linux-userspace
 * ({@code mvn -pl nmf-mission-barebone/barebone-space-filesystem package
 * -Pgenerate-filesystem -Dgenerate-filesystem.appsIsolation=linux-userspace}).
 */
public class SecurityBaselineTamperTest {

    private static final File BAREBONE_DIR = new File(System.getProperty("nmf.security.barebone.dir",
            "../../nmf-mission-barebone/barebone-space-filesystem"));
    private static final File FS_ROOT = new File(BAREBONE_DIR, "target/space-filesystem/nanosat-mo-framework");
    private static final File SETUP_SCRIPT = new File(FS_ROOT, "setup_linux_userspace.sh");

    @Test
    public void aDowngradeIsWarnedAboutInBothTheBootReportAndTheSupervisorLog() throws Exception {
        Run run = run("off");
        Assert.assertTrue("The security baseline must record the deployed enforce posture.\n" + run.baseline,
                run.baseline.contains("access-control=enforce"));
        Assert.assertTrue("The Boot Report must warn that the posture was downgraded.\n" + run.bootReport,
                run.bootReport.toLowerCase().contains("security posture downgraded"));
        Assert.assertTrue("The Supervisor log must warn that the posture was downgraded.\n" + run.supervisorLog,
                run.supervisorLog.toLowerCase().contains("security posture downgraded"));
    }

    @Test
    public void aConfigurationThatMatchesTheBaselineRaisesNoDowngradeWarning() throws Exception {
        Run run = run("enforce");
        Assert.assertFalse("A configuration that matches the baseline must not warn of a downgrade.\n"
                + run.bootReport, run.bootReport.toLowerCase().contains("downgraded"));
        Assert.assertFalse("A configuration that matches the baseline must not warn of a downgrade.\n"
                + run.supervisorLog, run.supervisorLog.toLowerCase().contains("downgraded"));
    }

    private static final class Run {
        String stdout;
        String baseline;
        String bootReport;
        String supervisorLog;
    }

    private static Run run(String accessControl) throws Exception {
        Assume.assumeTrue("Skipping: generate the barebone filesystem with linux-userspace first. "
                + "Expected at: " + SETUP_SCRIPT.getAbsolutePath(), SETUP_SCRIPT.isFile());

        ImageFromDockerfile image = new ImageFromDockerfile()
                .withFileFromPath(".", Paths.get(BAREBONE_DIR.getAbsolutePath()));

        try (GenericContainer<?> supervisor = new GenericContainer<>(image)) {
            supervisor
                    .withCreateContainerCmdModifier(cmd -> cmd.withUser("root")
                            .withEntrypoint("sleep").withCmd("infinity"))
                    .withStartupTimeout(Duration.ofMinutes(3))
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("access-control/security-baseline-tamper-e2e.sh", 0755),
                            "/tmp/e2e.sh");
            supervisor.start();
            Container.ExecResult result = supervisor.execInContainer("sh", "/tmp/e2e.sh", accessControl);
            Run run = new Run();
            run.stdout = result.getStdout();
            run.baseline = section(result.getStdout(), "---BASELINE---");
            run.bootReport = section(result.getStdout(), "---BOOT-REPORT---");
            run.supervisorLog = section(result.getStdout(), "---SUPERVISOR-LOG---");
            String boot = section(result.getStdout(), "---BOOT---");
            if (!boot.contains("CONFIRMED")) {
                Assert.fail("The Supervisor did not confirm its boot.\nstdout:\n" + result.getStdout()
                        + "\nstderr:\n" + result.getStderr());
            }
            return run;
        }
    }

    private static String section(String stdout, String marker) {
        int a = stdout.indexOf(marker);
        int b = stdout.indexOf("---END---", a < 0 ? 0 : a);
        if (a < 0 || b < 0) {
            return "";
        }
        return stdout.substring(a + marker.length(), b).trim();
    }
}
