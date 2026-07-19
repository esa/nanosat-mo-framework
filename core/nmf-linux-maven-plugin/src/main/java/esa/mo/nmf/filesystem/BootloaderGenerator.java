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
package esa.mo.nmf.filesystem;

import esa.mo.nmf.environment.AppsIsolationMode;
import esa.mo.nmf.environment.Deployment;
import esa.mo.nmf.nmfpackage.utils.ChecksumGenerator;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Generates everything the NMF Bootloader consumes: the bootloader script
 * itself, the software baseline files, the bootloader configuration, the
 * checksum manifests of the baseline directories, and the Boot Report
 * directory. See the NMF Bootloader Specification in the documentation.
 *
 * <p>
 * The bootloader script is a static resource copied verbatim: all
 * variability lives in the generated {@code bootloader/} properties files.
 *
 * @author Cesar Coelho
 */
public class BootloaderGenerator {

    /**
     * The script is the single entry point for starting the Supervisor. The
     * historical name is kept so mission tooling and procedures stay valid.
     */
    public static final String BOOTLOADER_SCRIPT = "start_supervisor.sh";

    /**
     * The provisioning and hardening script for the linux-userspace apps
     * isolation mode. Only generated when that mode is selected.
     */
    public static final String SETUP_LINUX_USERSPACE_SCRIPT = "setup_linux_userspace.sh";

    /**
     * The version of the script-to-baseline-files interface contract.
     */
    public static final String SCHEMA_VERSION = "1";

    private static final String DEFAULT_MAX_REPORT_FILE_SIZE_KB = "100";
    private static final String DEFAULT_MIN_FREE_DISK_KB = "10240";
    private static final String DEFAULT_BOOT_CONFIRM_TIMEOUT_S = "60";
    private static final String DEFAULT_BOOT_MAX_ATTEMPTS = "2";
    private static final String DEFAULT_PROMOTION_SOAK_S = "60";

    private final File nmfRootDir;

    /**
     * The Constructor.
     *
     * @param nmfRootDir The NMF root directory of the generated filesystem.
     */
    public BootloaderGenerator(File nmfRootDir) {
        this.nmfRootDir = nmfRootDir;
    }

    /**
     * Generates the bootloader script and its domain. Must be called after
     * the baseline jar directories have been populated, because the checksum
     * manifests cover their final contents.
     *
     * @param nmfVersion The NMF framework version of the deployed baseline.
     * @param missionVersion The mission software version of the deployed
     * baseline.
     * @param supervisorMainClass The main class of the Supervisor.
     * @param appsIsolation The apps isolation mode.
     * @throws IOException if a file could not be generated.
     */
    public void generate(String nmfVersion, String missionVersion,
            String supervisorMainClass, String appsIsolation) throws IOException {
        // The bootloader script: a static resource, copied verbatim
        copyResource(BOOTLOADER_SCRIPT);

        // The bootloader domain: the three baseline files and the config
        File bootloaderDir = new File(nmfRootDir, Deployment.DIR_BOOTLOADER);
        bootloaderDir.mkdirs();

        // Right after deployment the three roles point at the same baseline
        // and the fallback ladder degenerates gracefully
        String baseline = baselineContent(nmfVersion, missionVersion, supervisorMainClass);
        writeFile(new File(bootloaderDir, Deployment.FILE_BASELINE_PRIMARY), baseline);
        writeFile(new File(bootloaderDir, Deployment.FILE_BASELINE_SECONDARY), baseline);
        writeFile(new File(bootloaderDir, Deployment.FILE_BASELINE_FACTORY), baseline);

        String config = "# NMF Bootloader configuration\n"
                + "apps-isolation=" + appsIsolation + "\n"
                + "max-report-file-size-kb=" + DEFAULT_MAX_REPORT_FILE_SIZE_KB + "\n"
                + "min-free-disk-kb=" + DEFAULT_MIN_FREE_DISK_KB + "\n"
                + "boot-confirm-timeout-s=" + DEFAULT_BOOT_CONFIRM_TIMEOUT_S + "\n"
                + "boot-max-attempts=" + DEFAULT_BOOT_MAX_ATTEMPTS + "\n"
                + "promotion-soak-s=" + DEFAULT_PROMOTION_SOAK_S + "\n";
        writeFile(new File(bootloaderDir, Deployment.FILE_BOOTLOADER_CONFIG), config);

        // The Boot Report directory
        new File(new File(nmfRootDir, Deployment.DIR_LOGS), Deployment.DIR_BOOTLOADER).mkdirs();

        // The checksum manifests of the baseline jar directories
        ChecksumGenerator.writeChecksumsFile(
                new File(new File(nmfRootDir, Deployment.DIR_JARS_NMF), nmfVersion));
        ChecksumGenerator.writeChecksumsFile(
                new File(new File(nmfRootDir, Deployment.DIR_JARS_MISSION), missionVersion));

        // The provisioning/hardening script, only for linux-userspace mode
        if (AppsIsolationMode.LINUX_USERSPACE.equals(appsIsolation)) {
            copyResource(SETUP_LINUX_USERSPACE_SCRIPT);
        }
    }

    private static String baselineContent(String nmfVersion, String missionVersion,
            String supervisorMainClass) {
        return "# NMF Software Baseline\n"
                + "schema-version=" + SCHEMA_VERSION + "\n"
                + "nmf-version=" + nmfVersion + "\n"
                + "mission-version=" + missionVersion + "\n"
                + "java=system\n"
                + "main-class=" + supervisorMainClass + "\n";
    }

    private void copyResource(String filename) throws IOException {
        File destination = new File(nmfRootDir, filename);

        try (java.io.InputStream inputStream
                = BootloaderGenerator.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + filename);
            }
            Files.copy(inputStream, destination.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        }
        destination.setExecutable(true, false);
    }

    private static void writeFile(File file, String content) throws IOException {
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }
}
