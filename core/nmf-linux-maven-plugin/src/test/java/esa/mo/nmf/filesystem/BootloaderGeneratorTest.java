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

import esa.mo.nmf.environment.Deployment;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for how {@link BootloaderGenerator} applies the apps isolation mode
 * chosen at mission-development time (NMF-2026-003): the mode is recorded in the
 * bootloader config, and the linux-userspace provisioning script is generated
 * only for that mode.
 */
public class BootloaderGeneratorTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private static final String NMF_VERSION = "6.0";
    private static final String MISSION_VERSION = "1.0";
    private static final String MAIN_CLASS = "esa.mo.nmf.Supervisor";

    private File nmfRoot;

    @Before
    public void createBaselineDirs() {
        nmfRoot = tmp.getRoot();
        // The generator writes checksum manifests of the baseline jar directories,
        // so those directories have to exist.
        new File(new File(nmfRoot, Deployment.DIR_JARS_NMF), NMF_VERSION).mkdirs();
        new File(new File(nmfRoot, Deployment.DIR_JARS_MISSION), MISSION_VERSION).mkdirs();
    }

    private void generate(String isolationMode) throws Exception {
        new BootloaderGenerator(nmfRoot).generate(NMF_VERSION, MISSION_VERSION, MAIN_CLASS, isolationMode);
    }

    private String config() throws Exception {
        File cfg = new File(new File(nmfRoot, Deployment.DIR_BOOTLOADER), Deployment.FILE_BOOTLOADER_CONFIG);
        return new String(Files.readAllBytes(cfg.toPath()), StandardCharsets.UTF_8);
    }

    private File setupScript() {
        return new File(nmfRoot, BootloaderGenerator.SETUP_LINUX_USERSPACE_SCRIPT);
    }

    @Test
    public void linuxUserspaceRecordsTheModeAndShipsTheSetupScript() throws Exception {
        generate("linux-userspace");

        Assert.assertTrue("the config must record the isolation mode",
                config().contains("apps-isolation=linux-userspace"));
        Assert.assertTrue("the setup script must be generated for linux-userspace",
                setupScript().isFile());

        // The generated script is the one that creates the privilege-separation users.
        String setup = new String(Files.readAllBytes(setupScript().toPath()), StandardCharsets.UTF_8);
        Assert.assertTrue("the setup script must create the nmf-admin user", setup.contains("nmf-admin"));
        Assert.assertTrue("the setup script must create the nmf-apps group", setup.contains("nmf-apps"));

        // The nmf-admin account must not get a password, nor sudo rights to set one.
        Assert.assertFalse("the setup script must not set any password", setup.contains("chpasswd"));
        Assert.assertTrue("the setup script must lock the nmf-admin account",
                setup.contains("passwd -l $user_nmf_admin"));
        // The Supervisor and the Apps need a home they can write to, not /nonexistent.
        Assert.assertTrue("nmf-admin must get a home directory",
                setup.contains("adduser --system --home \"$dir_home$1\""));
        Assert.assertTrue("the account of an App must get a home directory",
                setup.contains("adduser --system --home \"/home/$user\""));

        // Its sudo rights must be the App account helper and su to an App user only.
        Assert.assertTrue("the setup script must install the App account helper",
                setup.contains("helper_path=\"/usr/local/sbin/nmf-app-account\""));
        Assert.assertTrue("nmf-admin must be allowed the App account helper",
                setup.contains("NOPASSWD: $helper_path\n"));
        for (String generic : new String[]{"NOPASSWD:/usr/sbin/adduser", "NOPASSWD:/usr/sbin/deluser",
            "NOPASSWD:/bin/chmod", "NOPASSWD:/bin/chgrp", "$rule_"}) {
            Assert.assertFalse("nmf-admin must not be given a generic command: " + generic,
                    setup.contains(generic));
        }
    }

    @Test
    public void aChecksumIsNotRequiredUnlessTheMissionAsksForIt() throws Exception {
        generate("none");
        Assert.assertTrue("By default, install and update do not require a checksum",
                config().contains("require-package-checksum=false"));

        new BootloaderGenerator(nmfRoot).generate(NMF_VERSION, MISSION_VERSION, MAIN_CLASS, "none", true);
        Assert.assertTrue("A mission can require a checksum for install and update",
                config().contains("require-package-checksum=true"));
    }

    @Test
    public void accessControlIsEnforcedUnlessTheMissionChoosesAnotherMode() throws Exception {
        generate("none");
        Assert.assertTrue("By default, access control is enforced",
                config().contains("access-control=enforce"));

        new BootloaderGenerator(nmfRoot).generate(NMF_VERSION, MISSION_VERSION, MAIN_CLASS, "none", false,
                "audit");
        Assert.assertTrue("A mission can choose the access control mode",
                config().contains("access-control=audit"));
    }

    @Test
    public void aGroundCredentialIsGeneratedWithTheFilesystem() throws Exception {
        generate("none");
        File credential = new File(new File(nmfRoot, Deployment.DIR_BOOTLOADER), Deployment.FILE_GROUND_CREDENTIAL);
        Assert.assertTrue("The filesystem must hold the credential of ground", credential.isFile());
        String first = new String(Files.readAllBytes(credential.toPath()), StandardCharsets.US_ASCII).trim();
        Assert.assertTrue("The credential must be 32 random bytes, as 64 hexadecimal digits: " + first,
                first.matches("[0-9a-f]{64}"));

        generate("none");
        String second = new String(Files.readAllBytes(credential.toPath()), StandardCharsets.US_ASCII).trim();
        Assert.assertNotEquals("Each filesystem must get its own credential", first, second);
    }

    @Test
    public void noneRecordsTheModeAndShipsNoSetupScript() throws Exception {
        generate("none");

        Assert.assertTrue("the config must record the isolation mode",
                config().contains("apps-isolation=none"));
        Assert.assertFalse("the setup script must not be generated for none",
                setupScript().exists());
    }

    @Test
    public void bubblewrapRecordsTheModeAndShipsNoSetupScript() throws Exception {
        generate("bubblewrap");

        Assert.assertTrue("the config must record the isolation mode",
                config().contains("apps-isolation=bubblewrap"));
        Assert.assertFalse("the setup script is only for linux-userspace",
                setupScript().exists());
    }
}
