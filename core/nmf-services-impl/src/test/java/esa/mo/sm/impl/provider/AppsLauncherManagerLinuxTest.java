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
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for the linux-userspace isolation command assembly (NMF-2026-003).
 * They need no root and no sudo: they only check the argv the Supervisor would
 * execute to confine an app to its own unprivileged user.
 */
public class AppsLauncherManagerLinuxTest {

    // The manager needs no COM archive for command assembly.
    private final AppsLauncherManagerLinux manager = new AppsLauncherManagerLinux(null);

    @Test
    public void appIsLaunchedAsItsOwnUnprivilegedUser() {
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", "app-MyApp", "start_", new String[0]));

        // The app runs through 'su - app-MyApp -c ...', so under its own user and not
        // the Supervisor's. (A leading 'sudo' is added only when sudo is available, so
        // it is not asserted here.)
        int suIdx = cmd.indexOf("su");
        Assert.assertTrue("the command must switch user with su", suIdx >= 0);
        Assert.assertEquals("-", cmd.get(suIdx + 1));
        Assert.assertEquals("app-MyApp", cmd.get(suIdx + 2));
        Assert.assertEquals("-c", cmd.get(suIdx + 3));
        Assert.assertTrue("the app script must run inside the su -c argument",
                cmd.get(suIdx + 4).contains("./start_app.sh"));
    }

    @Test
    public void withoutAUserTheAppRunsInAPlainShell() {
        List<String> cmd = Arrays.asList(
                manager.assembleCommand("/w", "MyApp", null, "start_", new String[0]));

        Assert.assertEquals("/bin/sh", cmd.get(0));
        Assert.assertEquals("-c", cmd.get(1));
        Assert.assertFalse("there must be no user switch when no user is given", cmd.contains("su"));
    }
}
