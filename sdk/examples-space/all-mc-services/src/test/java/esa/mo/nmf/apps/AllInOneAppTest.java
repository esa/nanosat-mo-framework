/* ----------------------------------------------------------------------------
 * Copyright (C) 2025      European Space Agency
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
package esa.mo.nmf.apps;

import java.io.IOException;
import org.junit.Test;

/**
 * Basic Test to check if the App can be started without crashing.
 */
public class AllInOneAppTest {

    @Test
    public void test0() throws IOException {
        try {
            AllInOne.main(null);
        } catch (Exception ex) {
            org.junit.Assert.fail("The App failed to start!");
        }
    }
}
