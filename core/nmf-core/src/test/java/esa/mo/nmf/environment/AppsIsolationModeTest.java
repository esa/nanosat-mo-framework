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
package esa.mo.nmf.environment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Holds which isolation modes are valid. An unknown mode must not be taken for
 * no isolation (NMF-2026-013): the Supervisor then runs no App.
 */
public class AppsIsolationModeTest {

    private String previous;

    @Before
    public void keepTheMode() {
        previous = System.getProperty(AppsIsolationMode.PROPERTY);
    }

    @After
    public void restoreTheMode() {
        if (previous == null) {
            System.clearProperty(AppsIsolationMode.PROPERTY);
        } else {
            System.setProperty(AppsIsolationMode.PROPERTY, previous);
        }
    }

    @Test
    public void eachOfTheFourModesIsValid() {
        for (String mode : AppsIsolationMode.MODES) {
            System.setProperty(AppsIsolationMode.PROPERTY, mode);
            Assert.assertNull("'" + mode + "' must be valid", AppsIsolationMode.invalidModeReason());
        }
    }

    @Test
    public void noSettingAtAllMeansNoIsolation() {
        // As when the Supervisor is started from an IDE, without the boot script
        System.clearProperty(AppsIsolationMode.PROPERTY);
        Assert.assertNull(AppsIsolationMode.invalidModeReason());
        Assert.assertTrue(AppsIsolationMode.isNone());
    }

    @Test
    public void anyOtherValueIsNotValid() {
        for (String value : new String[]{"", " ", "linux_userspace", "Linux-Userspace", "none ", "bwrap", "docker"}) {
            System.setProperty(AppsIsolationMode.PROPERTY, value);
            String reason = AppsIsolationMode.invalidModeReason();
            Assert.assertNotNull("'" + value + "' must not be valid", reason);
            Assert.assertTrue("The reason must name the value: " + reason, reason.contains("'" + value + "'"));
        }
    }
}
