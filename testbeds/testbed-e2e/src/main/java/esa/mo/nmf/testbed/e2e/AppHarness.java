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
package esa.mo.nmf.testbed.e2e;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * Manages the lifecycle of an NMF App instance for end-to-end tests.
 * Requires a running Supervisor; use together with {@link SupervisorHarness}.
 * Call {@link #setUp} before tests and {@link #tearDown} after.
 *
 * @author Cesar Coelho
 */
public class AppHarness {

    private static final Logger LOGGER = Logger.getLogger(AppHarness.class.getName());

    private final String appName;

    public AppHarness(String appName) {
        this.appName = appName;
    }

    public void setUp() throws IOException {
        // TODO: request the Supervisor to start the app and wait until it is running
    }

    public void tearDown() throws IOException {
        // TODO: request the Supervisor to stop the app and collect logs
    }

    public String getAppName() {
        return appName;
    }

}
