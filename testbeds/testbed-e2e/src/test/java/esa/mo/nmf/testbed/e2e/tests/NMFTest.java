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
package esa.mo.nmf.testbed.e2e.tests;

/**
 * Base class for NMF end-to-end tests. Provides shared constants for
 * test output formatting.
 */
public abstract class NMFTest {

    protected static final String SEP = "-----------------------------------------------";
    protected static final String SETUP_CLASS_SEP = "-----------------------------------------------------------------------";
    protected static final String SETUP_CLASS_MSG = "Entered: setUpClass() - The Supervisor will be started here!";
}
