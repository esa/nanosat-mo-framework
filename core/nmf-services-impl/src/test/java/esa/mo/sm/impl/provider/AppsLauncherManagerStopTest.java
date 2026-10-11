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

import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.sm.structures.AppDetails;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for stopping an app whose process is no longer tracked by the
 * Supervisor. stopApp checks that an app is running before it stops it, but a
 * concurrent killApp, or the force-kill of an overlapping stopApp of the same
 * app, can remove the app's process in between. Stopping must then treat the
 * app as already stopped instead of failing.
 */
public class AppsLauncherManagerStopTest {

    private static final Long APP_ID = 1L;

    // The manager needs no COM archive to stop an app it no longer tracks.
    private final AppsLauncherManagerLinux manager = new AppsLauncherManagerLinux(null);

    /** Registers an app that the Supervisor knows about but has no process for. */
    private void registerAppWithoutProcess() {
        manager.addDef(APP_ID, new AppDetails(new Identifier("my-app"), "-", "1.0",
                new Identifier("NMF_App"), false, true, "-", "-", null));
    }

    @Test
    public void stoppingAnAppWhoseProcessIsGoneDoesNotFail() throws Exception {
        registerAppWithoutProcess();

        manager.stopNMFAppGracefully(APP_ID, new Duration(1), null,
                appId -> Assert.fail("an app that is already gone must not be force-killed"));
    }

    @Test
    public void stoppingAnAppWhoseProcessIsGoneWithNoTimeoutDoesNotFail() throws Exception {
        registerAppWithoutProcess();

        manager.stopNMFAppGracefully(APP_ID, null, null,
                appId -> Assert.fail("an app that is already gone must not be force-killed"));
    }
}
