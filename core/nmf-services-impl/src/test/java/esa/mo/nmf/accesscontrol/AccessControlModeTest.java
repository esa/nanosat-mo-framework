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
package esa.mo.nmf.accesscontrol;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Holds that a Supervisor enforces access control unless it is told
 * otherwise, also when it is started without the bootloader, such as from an
 * IDE, or with a mode it does not know.
 */
public class AccessControlModeTest {

    @After
    public void clearMode() {
        System.clearProperty(AccessControlMode.PROPERTY);
    }

    @Test
    public void withoutAModeAccessControlIsEnforced() {
        System.clearProperty(AccessControlMode.PROPERTY);
        Assert.assertEquals(AccessControlMode.ENFORCE, AccessControlMode.current());
    }

    @Test
    public void anUnknownModeIsEnforced() {
        for (String value : new String[]{"", "of", "disabled", "none"}) {
            Assert.assertEquals("The mode '" + value + "'", AccessControlMode.ENFORCE, AccessControlMode.parse(value));
        }
    }

    @Test
    public void aMissionCanChooseAnotherMode() {
        Assert.assertEquals(AccessControlMode.OFF, AccessControlMode.parse("off"));
        Assert.assertEquals(AccessControlMode.AUDIT, AccessControlMode.parse(" Audit "));
        System.setProperty(AccessControlMode.PROPERTY, "audit");
        Assert.assertEquals(AccessControlMode.AUDIT, AccessControlMode.current());
    }
}
