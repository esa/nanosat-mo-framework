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
 * Manages the lifecycle of an NMF Supervisor instance for end-to-end tests.
 * Call {@link #setUp} before tests and {@link #tearDown} after.
 *
 * @author Cesar Coelho
 */
public class SupervisorHarness {

    private static final Logger LOGGER = Logger.getLogger(SupervisorHarness.class.getName());

    public void setUp() throws IOException {
        // TODO: start the NMF Supervisor (in-process or as an external process)
    }

    public void tearDown() throws IOException {
        // TODO: stop the NMF Supervisor and collect logs
    }

}
