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
package esa.mo.nmf.composites;

import org.junit.Assert;
import org.junit.Test;

/**
 * A Supervisor that ground cannot command must not confirm its boot, so that
 * the bootloader falls back to the previous baseline. Holds when ground can
 * and cannot command it.
 */
public class NotCommandableBootTest {

    @Test
    public void aBootloaderOlderThan60MakesItNotCommandable() {
        String reason = NanoSatMOSupervisor.whyGroundCannotCommand(null, false);
        Assert.assertNotNull("Without the access control mode, ground cannot command the Supervisor", reason);
        Assert.assertTrue(reason, reason.contains("older than 6.0"));
        Assert.assertNotNull("A credential of ground placed by hand does not bring the other settings",
                NanoSatMOSupervisor.whyGroundCannotCommand(null, true));
    }

    @Test
    public void enforceWithoutACredentialOfGroundIsNotCommandable() {
        String reason = NanoSatMOSupervisor.whyGroundCannotCommand("enforce", false);
        Assert.assertNotNull("Enforced without a credential of ground, no call from ground is accepted", reason);
        Assert.assertTrue(reason, reason.contains("no credential of ground"));
    }

    @Test
    public void groundCanCommandItOtherwise() {
        Assert.assertNull("Enforced with the credential of ground",
                NanoSatMOSupervisor.whyGroundCannotCommand("enforce", true));
        Assert.assertNull("In audit mode, the calls of ground go through",
                NanoSatMOSupervisor.whyGroundCannotCommand("audit", false));
        Assert.assertNull("With access control off, every call goes through",
                NanoSatMOSupervisor.whyGroundCannotCommand("off", false));
    }
}
