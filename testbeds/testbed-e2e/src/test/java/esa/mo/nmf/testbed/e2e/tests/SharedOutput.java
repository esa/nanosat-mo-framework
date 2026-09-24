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

import java.util.logging.Logger;

/**
 * What the end-to-end tests write their output with.
 * <p>
 * These were held by a base class that every test extended, which gave each of
 * them a parent that did nothing: the class had no behaviour to inherit, only
 * these four. They are imported statically now, so that a test is free to
 * extend something that has something to give it.
 * <p>
 * The name avoids Test at either end on purpose. Surefire takes a class called
 * Test* or *Test for a test class and runs it, and this one has nothing to run.
 */
public final class SharedOutput {

    public static final Logger LOGGER = Logger.getLogger(SharedOutput.class.getName());
    public static final String SEP = "-----------------------------------------------";
    public static final String SETUP_CLASS_SEP = "-----------------------------------------------------------------------";
    public static final String SETUP_CLASS_MSG = "Entered: setUpClass() - The Supervisor will be started here!";

    private SharedOutput() {
    }
}
