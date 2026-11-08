/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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

import esa.mo.nmf.NMFProvider;
import esa.mo.nmf.nmfpackage.metadata.MetadataApp;
import java.io.File;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * The Apps Launcher does not start an App built against another major version
 * of the NMF, as its receipt records it.
 */
public class AppNotRunnableTest {

    private static final String RECEIPTS_PROPERTY = "esa.mo.nmf.nmfpackage.receipts";

    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    private File receipts;
    private String previousReceipts;

    @Before
    public void useATemporaryReceiptsFolder() throws Exception {
        receipts = tmp.newFolder("receipts");
        previousReceipts = System.setProperty(RECEIPTS_PROPERTY, receipts.getAbsolutePath());
    }

    @After
    public void restoreTheReceiptsFolder() {
        if (previousReceipts == null) {
            System.clearProperty(RECEIPTS_PROPERTY);
        } else {
            System.setProperty(RECEIPTS_PROPERTY, previousReceipts);
        }
    }

    @Test
    public void anAppOfAnotherMajorVersionIsNotStarted() throws Exception {
        int major = NMFProvider.getMajorVersionNMF();
        receipt("current-app", major + ".0");
        receipt("old-app", (major - 1) + ".1");

        Assert.assertNull("An App of the major version that runs is started",
                AppsLauncherManager.whyNotRunnable("current-app"));
        String reason = AppsLauncherManager.whyNotRunnable("old-app");
        Assert.assertNotNull("An App of another major version is not started", reason);
        Assert.assertTrue(reason, reason.contains("not the major version"));
        Assert.assertNull("An App that was not installed from a package has nothing to check",
                AppsLauncherManager.whyNotRunnable("not-installed"));
    }

    private void receipt(String name, String nmfVersion) throws Exception {
        new MetadataApp(name, "1.0", "esa.mo.nmf.apps.Probe", name + ".jar", null, null,
                new ArrayList<>(), nmfVersion).store(new File(receipts, name + ".receipt"));
    }
}
