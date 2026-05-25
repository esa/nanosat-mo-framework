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

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Manages the lifecycle of an NMF Supervisor instance for end-to-end tests.
 * The filesystem used is generated fresh by the build (generate-test-resources
 * phase) and its location is passed via the {@code nmf.e2e.filesystem} system
 * property. Call {@link #setUp} before tests and {@link #tearDown} after.
 *
 * @author Cesar Coelho
 */
public class SupervisorHarness {

    public static final String PROP_FILESYSTEM = "nmf.e2e.filesystem";

    private static final Logger LOGGER = Logger.getLogger(SupervisorHarness.class.getName());

    private File nmfDir;

    public void setUp() throws IOException {
        String path = System.getProperty(PROP_FILESYSTEM);
        if (path == null) {
            throw new IOException("System property '" + PROP_FILESYSTEM + "' is not set. "
                    + "Run via Maven (mvn test) so the filesystem is generated first.");
        }
        nmfDir = new File(path);
        if (!nmfDir.exists()) {
            throw new IOException("NMF filesystem directory not found: " + nmfDir.getAbsolutePath());
        }

        // TODO: start the NMF Supervisor from nmfDir (in-process or as an external process)
    }

    public void tearDown() throws IOException {
        // TODO: stop the NMF Supervisor and collect logs
    }

    public File getNmfDir() {
        return nmfDir;
    }

}
