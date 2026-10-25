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
package esa.mo.nmf.security.accesscontrol;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

/**
 * App permissions, end to end: the real Supervisor installs the permissions
 * probe App, acknowledging the one permission it declares, and runs it under
 * linux-userspace isolation. The probe then calls services of the Supervisor
 * and reports, for each call, whether it was let through.
 * <p>
 * In {@code enforce} mode, the App must reach the baseline that every App has
 * and the service it declared, and be refused everything else.
 * <p>
 * Prerequisites (otherwise the test is skipped, not failed):
 * <ul>
 * <li>the barebone filesystem generated with linux-userspace
 * ({@code mvn -pl nmf-mission-barebone/barebone-space-filesystem package
 * -Pgenerate-filesystem -Dgenerate-filesystem.appsIsolation=linux-userspace});</li>
 * <li>the permissions probe {@code .nmfpack} (build {@code app-permissions-probe});</li>
 * <li>the cli-tool fat jar ({@code mvn -pl sdk/cli-tool -Passembly-with-dependencies install}).</li>
 * </ul>
 */
public class AppPermissionsTest {

    private static final String ALLOWED = "ALLOWED";
    private static final String REFUSED = "REFUSED";

    /** The one permission the probe declares in its pom.xml. */
    private static final String DECLARED = "Supervisor.MC.Parameter";

    private static final File BAREBONE_DIR = new File(System.getProperty("nmf.security.barebone.dir",
            "../../nmf-mission-barebone/barebone-space-filesystem"));
    private static final File FS_ROOT = new File(BAREBONE_DIR, "target/space-filesystem/nanosat-mo-framework");
    private static final File SETUP_SCRIPT = new File(FS_ROOT, "setup_linux_userspace.sh");
    private static final File PROBE_TARGET = new File("../app-permissions-probe/target");
    private static final File CLI_TARGET = new File("../../sdk/cli-tool/target");

    @Test
    public void enforceLetsAnAppReachOnlyTheBaselineAndWhatItDeclared() throws Exception {
        // Each call the probe makes, and its outcome under enforce
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("Supervisor.COM.Directory.lookup", ALLOWED);              // baseline
        expected.put("Supervisor.SM.Heartbeat.getPeriod", ALLOWED);            // baseline
        expected.put("Supervisor.SM.AppsLauncher.monitorEventsRegister", ALLOWED); // baseline
        expected.put("Supervisor.MC.Parameter.listDefinition", ALLOWED);       // declared
        expected.put("Supervisor.MC.Action.listDefinition", REFUSED);
        expected.put("Supervisor.MC.Aggregation.listDefinition", REFUSED);
        expected.put("Supervisor.SM.AppsLauncher.listApp", REFUSED);
        expected.put("Supervisor.SM.PackageManagement.findPackage", REFUSED);
        expected.put("Supervisor.SM.CommandExecutor.runCommand", REFUSED);
        expected.put("OtherHost.Supervisor.SM.CommandExecutor.runCommand", REFUSED); // its host by another name
        expected.put("Supervisor.COM.Archive.count", REFUSED);
        expected.put("Publish.Supervisor.SM.AppsLauncher.monitorEvents", REFUSED); // only the Supervisor publishes
        expected.put("Publish.Supervisor.SM.AppsLauncher.monitorEvents.delivered", "NO"); // and nobody receives it
        expected.put("Forged.Supervisor.SM.PackageManagement.findPackage", REFUSED); // made-up credential
        expected.put("Supervisor.GroundCredential.read", REFUSED);              // the ground's secret

        Run run = run("enforce", DECLARED);
        Assert.assertTrue("The probe must be installed when its permission is acknowledged.\n" + run.stdout,
                run.installed);
        String report = run.report;
        Assert.assertFalse("The probe produced no report.\n" + run.stdout,
                report.isEmpty() || report.contains("NO_REPORT"));

        System.out.println("The Supervisor installed the permissions probe, acknowledging " + DECLARED
                + ", and ran it in enforce mode. The probe reported:");
        List<String> mismatches = new ArrayList<>();
        for (Map.Entry<String, String> call : expected.entrySet()) {
            String actual = valueOf(report, call.getKey());
            boolean ok = actual.startsWith(call.getValue());
            if (!ok && call.getKey().startsWith("Publish.")) {
                // MO 15.0 does not return the broker's refusal to the publisher, which then gets
                // no answer (fixed in MO 15.1, mo-services-java d9288f17). Until the NMF moves to
                // 15.1, no answer counts as refused, provided the event was not delivered.
                // TODO: remove this once the parent is MO 15.1.
                ok = actual.contains("no answer within")
                        && "NO".equals(valueOf(report, call.getKey() + ".delivered"));
            }
            // A refused permission tells the App's developer what is missing and where to declare it
            if (ok && REFUSED.equals(call.getValue()) && call.getKey().startsWith("Supervisor.")
                    && !call.getKey().startsWith("Supervisor.GroundCredential")) {
                String permission = call.getKey().substring(0, call.getKey().lastIndexOf('.'));
                ok = actual.contains("Missing permission: " + permission) && actual.contains("pom.xml");
            }
            System.out.printf("  %-50s expected %-8s got %s%n", call.getKey(), call.getValue(),
                    ok ? actual : actual + "   <-- MISMATCH");
            if (!ok) {
                mismatches.add(call.getKey() + ": expected " + call.getValue() + ", got " + actual);
            }
        }
        System.out.println("Supervisor log, access control lines:");
        System.out.println(run.supervisorLog.isEmpty() ? "  (none)" : run.supervisorLog);

        Assert.assertTrue("In enforce mode, an App must reach only the baseline and what it declared:\n  - "
                + String.join("\n  - ", mismatches) + "\n" + report, mismatches.isEmpty());
    }

    @Test
    public void auditLetsEverythingThroughButWarnsTheAppAndTheSupervisor() throws Exception {
        Run run = run("audit", DECLARED);
        // The calls of the probe to services it was not granted
        String[] notGranted = {"Supervisor.MC.Action.listDefinition", "Supervisor.MC.Aggregation.listDefinition",
            "Supervisor.SM.AppsLauncher.listApp", "Supervisor.SM.PackageManagement.findPackage",
            "Supervisor.SM.CommandExecutor.runCommand", "Supervisor.COM.Archive.count"};

        Assert.assertFalse("The probe produced no report.\n" + run.stdout,
                run.report.isEmpty() || run.report.contains("NO_REPORT"));
        System.out.println("In audit mode, the probe reported:\n" + run.report);
        System.out.println("App log, access control lines:\n" + run.appLog);
        System.out.println("Supervisor log, access control lines:\n" + run.supervisorLog);
        List<String> problems = new ArrayList<>();
        for (String call : notGranted) {
            String permission = call.substring(0, call.lastIndexOf('.'));
            if (!valueOf(run.report, call).startsWith(ALLOWED)) {
                problems.add("the call to " + permission + " must go through in audit mode");
            }
            if (!run.appLog.contains("Missing permission: " + permission)) {
                problems.add("the App's log must warn that it lacks " + permission);
            }
            if (!run.supervisorLog.contains("Audit: App 'permissions-probe' called " + permission)) {
                problems.add("the Supervisor's log must record the call to " + permission);
            }
        }
        if (run.appLog.contains("Missing permission: " + DECLARED)) {
            problems.add("the App must not be warned about " + DECLARED + ", which it was granted");
        }
        Assert.assertTrue("In audit mode:\n  - " + String.join("\n  - ", problems), problems.isEmpty());
    }

    @Test
    public void enforceRefusesACallFromGroundWithoutTheGroundCredential() throws Exception {
        Run run = run("enforce", DECLARED);

        System.out.println("In enforce mode, a call from ground:\n" + run.ground);
        Assert.assertTrue("Without the ground credential, a call from ground must be refused.\n" + run.ground,
                run.ground.contains("WITHOUT_CREDENTIAL=REFUSED"));
        Assert.assertTrue("With the ground credential, a call from ground must go through.\n" + run.ground,
                run.ground.contains("WITH_CREDENTIAL=ALLOWED"));
        Assert.assertTrue("With the ground credential file given as a system property, as the playground's "
                + "tools do, a call from ground must go through.\n" + run.ground,
                run.ground.contains("WITH_GROUND_PROPERTY=ALLOWED"));
    }

    @Test
    public void enforceRefusesAnInstallThatDoesNotAcknowledgeTheDeclaredPermissions() throws Exception {
        Run run = run("enforce", "-");

        System.out.println("In enforce mode, the probe, which declares " + DECLARED
                + ", was installed without acknowledging it: installed = " + run.installed);
        Assert.assertFalse("In enforce mode, a package whose declared permissions are not all "
                + "acknowledged must not be installed.\n" + run.stdout, run.installed);
    }

    @Test
    public void auditInstallsButLogsAnInstallThatDoesNotAcknowledgeTheDeclaredPermissions() throws Exception {
        Run run = run("audit", "-");

        System.out.println("In audit mode, the probe was installed without acknowledging " + DECLARED
                + ": installed = " + run.installed);
        System.out.println("Supervisor log, access control lines:");
        System.out.println(run.supervisorLog.isEmpty() ? "  (none)" : run.supervisorLog);
        Assert.assertTrue("In audit mode, the install must go through.\n" + run.stdout, run.installed);
        Assert.assertTrue("In audit mode, the Supervisor must log the permission that was not acknowledged.\n"
                + run.supervisorLog, run.supervisorLog.contains(DECLARED)
                && run.supervisorLog.toLowerCase().contains("acknowledged"));
    }

    @Test
    public void theAcknowledgedPermissionsAreRecordedInTheArchive() throws Exception {
        Run run = run("enforce", DECLARED);

        List<String> installed = objectsOfClass(run.archive, "org.ccsds.moims.mo.sm.structures.PackageInstalled");
        System.out.println("PackageInstalled objects in the Supervisor's Archive:");
        installed.forEach(o -> System.out.println("  " + o.replaceAll("\\s+", " ")));
        Assert.assertTrue("The PackageInstalled object of the probe must record the permissions acknowledged "
                + "at installation.\n" + installed, installed.stream()
                .anyMatch(o -> o.contains("permissions-probe") && o.contains(DECLARED)));
    }

    @Test
    public void theCallsMadeWithoutPermissionAreRecordedInTheArchive() throws Exception {
        String unauthorised = "org.ccsds.moims.mo.sm.structures.UnauthorisedCall";

        List<String> enforced = objectsOfClass(run("enforce", DECLARED).archive, unauthorised);
        System.out.println("UnauthorisedCall objects after an enforce run:");
        enforced.forEach(o -> System.out.println("  " + o.replaceAll("\\s+", " ")));
        Assert.assertTrue("In enforce mode, the refused call of the App to CommandExecutor must be recorded",
                enforced.stream().anyMatch(o -> o.contains("permissions-probe")
                && o.contains("Supervisor.SM.CommandExecutor") && o.contains("\"refused\": true")));
        Assert.assertTrue("In enforce mode, the refused call from ground without the credential must be recorded",
                enforced.stream().anyMatch(o -> !o.contains("\"app\"")
                && o.contains("Supervisor.SM.PackageManagement") && o.contains("\"refused\": true")));

        List<String> audited = objectsOfClass(run("audit", DECLARED).archive, unauthorised);
        System.out.println("UnauthorisedCall objects after an audit run:");
        audited.forEach(o -> System.out.println("  " + o.replaceAll("\\s+", " ")));
        Assert.assertTrue("In audit mode, the call of the App to CommandExecutor must be recorded as not refused",
                audited.stream().anyMatch(o -> o.contains("permissions-probe")
                && o.contains("Supervisor.SM.CommandExecutor") && o.contains("\"refused\": false")));
    }

    /** What one run of the driver printed. */
    private static final class Run {
        String stdout;
        boolean installed;
        String report;
        String supervisorLog;
        String archive;
        String ground;
        String appLog;
    }

    /**
     * Returns the text of each object of the given class in an Archive dump:
     * from its class name to the next object of any class.
     */
    private static List<String> objectsOfClass(String dump, String className) {
        List<String> objects = new ArrayList<>();
        String key = "\"" + className + "\"";
        int at = dump.indexOf(key);
        while (at >= 0) {
            int next = dump.indexOf("\"org.ccsds.moims.mo.", at + key.length());
            objects.add(next < 0 ? dump.substring(at) : dump.substring(at, next));
            at = dump.indexOf(key, at + key.length());
        }
        return objects;
    }

    /**
     * Starts a fresh container, runs the driver in the given mode, acknowledging
     * the given permissions, and returns what it printed.
     */
    private static Run run(String mode, String grants) throws Exception {
        Assume.assumeTrue("Skipping: generate the barebone filesystem with linux-userspace first. "
                + "Expected at: " + SETUP_SCRIPT.getAbsolutePath(), SETUP_SCRIPT.isFile());
        File probePackage = findFile(PROBE_TARGET, ".nmfpack");
        Assume.assumeTrue("Skipping: build the permissions probe app (produces its .nmfpack) first.",
                probePackage != null);
        File cliJar = findFile(CLI_TARGET, "-jar-with-dependencies.jar");
        Assume.assumeTrue("Skipping: build the cli-tool fat jar (assembly-with-dependencies) first.",
                cliJar != null);

        // Stage the probe package and the cli-tool into the filesystem tree, so that
        // the barebone Dockerfile, which copies that tree, bakes them into the image.
        stage(probePackage, new File(FS_ROOT, "packages"), probePackage.getName());
        stage(cliJar, new File(FS_ROOT, "security-tools"), "cli-tool.jar");

        ImageFromDockerfile image = new ImageFromDockerfile()
                .withFileFromPath(".", Paths.get(BAREBONE_DIR.getAbsolutePath()));

        try (GenericContainer<?> supervisor = new GenericContainer<>(image)) {
            supervisor
                    .withCreateContainerCmdModifier(cmd -> cmd.withUser("root")
                            .withEntrypoint("sleep").withCmd("infinity"))
                    .withStartupTimeout(Duration.ofMinutes(3))
                    .withCopyFileToContainer(
                            MountableFile.forClasspathResource("access-control/permissions-probe-e2e.sh", 0755),
                            "/tmp/e2e.sh");
            supervisor.start();
            Container.ExecResult result = supervisor.execInContainer("sh", "/tmp/e2e.sh", mode, grants);
            String install = section(result.getStdout(), "---INSTALL---");
            if (install.isEmpty()) {
                Assert.fail("The '" + mode + "' run did not get as far as the install.\nstdout:\n"
                        + result.getStdout() + "\nstderr:\n" + result.getStderr());
            }
            Run run = new Run();
            run.stdout = result.getStdout();
            run.installed = install.contains("INSTALLED=yes");
            run.report = section(result.getStdout(), "---REPORT---");
            run.supervisorLog = section(result.getStdout(), "---SUPERVISOR-LOG---");
            run.archive = section(result.getStdout(), "---ARCHIVE---");
            run.ground = section(result.getStdout(), "---GROUND---");
            run.appLog = section(result.getStdout(), "---APP-LOG---");
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

    /** Returns the value of a {@code KEY=value} line in the report, or {@code "?"} if absent. */
    private static String valueOf(String report, String key) {
        for (String line : report.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith(key + "=")) {
                return trimmed.substring(key.length() + 1).trim();
            }
        }
        return "?";
    }

    private static void stage(File src, File destDir, String destName) throws Exception {
        Files.createDirectories(destDir.toPath());
        Path dest = new File(destDir, destName).toPath();
        Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
    }

    private static File findFile(File dir, String suffix) {
        File[] files = dir.listFiles((d, n) -> n.endsWith(suffix));
        return (files != null && files.length > 0) ? files[0] : null;
    }
}
