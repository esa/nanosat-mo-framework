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
package esa.mo.sm.impl.provider;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * Unit tests for the bubblewrap isolation command assembly (NMF-2026-003).
 * They need no bwrap binary: they only check the argv the Supervisor would
 * execute to sandbox an app.
 */
public class AppsLauncherManagerBubblewrapTest {

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    // The manager needs no COM archive for command assembly.
    private final AppsLauncherManagerBubblewrap manager = new AppsLauncherManagerBubblewrap(null);

    private String originalHome;

    @Before
    public void redirectHome() {
        // assembleCommand creates the app's storage directory under user.home; keep that in
        // a temporary directory so the test does not touch the real home.
        originalHome = System.getProperty("user.home");
        System.setProperty("user.home", tmp.getRoot().getAbsolutePath());
    }

    @After
    public void restoreHome() {
        if (originalHome != null) {
            System.setProperty("user.home", originalHome);
        }
    }

    @Test
    public void sandboxUnsharesNamespacesAndDiesWithTheSupervisor() {
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", null, new String[0]));

        Assert.assertEquals("/usr/bin/bwrap", cmd.get(0));
        Assert.assertTrue("all namespaces must be unshared", cmd.contains("--unshare-all"));
        Assert.assertTrue("the sandbox must die with the Supervisor", cmd.contains("--die-with-parent"));
        Assert.assertTrue("the inherited environment must be cleared", cmd.contains("--clearenv"));
    }

    @Test
    public void filesystemIsReadOnlyExceptTheAppDirectory() {
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", null, new String[0]));

        // The whole filesystem is bound read-only: '--ro-bind / /'.
        int roIdx = cmd.indexOf("--ro-bind");
        Assert.assertTrue("the filesystem must be mounted read-only", roIdx >= 0);
        Assert.assertEquals("/", cmd.get(roIdx + 1));
        Assert.assertEquals("/", cmd.get(roIdx + 2));

        // The app's own working directory is bound read-write: '--bind /w /w'. It is the
        // first --bind, before the log and storage directories.
        int bindIdx = cmd.indexOf("--bind");
        Assert.assertTrue("the app directory must be writable", bindIdx >= 0);
        Assert.assertEquals("/w", cmd.get(bindIdx + 1));
        Assert.assertEquals("/w", cmd.get(bindIdx + 2));
    }
}
