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
package esa.mo.nmf.environment;

import java.io.File;

/**
 * Extends {@link Deployment} with operations that are only appropriate for
 * build tooling (e.g. Maven Mojos). Not intended for use in runtime app code.
 */
public class DeploymentTooling extends Deployment {

    /**
     * Initialises the NMF root directory. Intended for tooling (e.g. Maven
     * Mojos) that operates on a generated filesystem at a known path rather
     * than deriving the root from the JVM working directory.
     *
     * @param dir The directory to use as the NMF root.
     */
    public synchronized static void initialize(File dir) {
        pathNMF = dir;
    }

    /**
     * Resets the NMF root directory so that {@link #initialize} may be called
     * again with a different path. Only intended for build tooling that
     * processes multiple modules in the same JVM.
     */
    public synchronized static void reset() {
        pathNMF = null;
    }
}
