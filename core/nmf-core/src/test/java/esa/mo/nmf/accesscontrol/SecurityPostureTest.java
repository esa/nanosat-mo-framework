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

import esa.mo.nmf.accesscontrol.SecurityPosture.Drift;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the strength comparison of a {@link SecurityPosture}, including the
 * credentials mode, whose strength order (SIGNED stronger than PLAIN) is not
 * the enum's own order.
 */
public class SecurityPostureTest {

    private static SecurityPosture posture(AccessControlMode mode, CredentialsMode credentials,
            boolean requireChecksum) {
        return new SecurityPosture(mode, credentials, requireChecksum);
    }

    private static final SecurityPosture HARDENED
            = posture(AccessControlMode.ENFORCE, CredentialsMode.SIGNED, true);

    @Test
    public void anIdenticalPostureIsNotADrift() {
        Assert.assertEquals(Drift.SAME, HARDENED.driftFrom(HARDENED));
    }

    @Test
    public void turningAccessControlOffIsAWeakening() {
        SecurityPosture current = posture(AccessControlMode.OFF, CredentialsMode.SIGNED, true);
        Assert.assertEquals(Drift.WEAKENED, current.driftFrom(HARDENED));
    }

    @Test
    public void turningAccessControlOnIsAStrengthening() {
        SecurityPosture deployed = posture(AccessControlMode.OFF, CredentialsMode.SIGNED, true);
        Assert.assertEquals(Drift.STRENGTHENED, HARDENED.driftFrom(deployed));
    }

    @Test
    public void goingFromSignedToPlainIsAWeakening() {
        SecurityPosture current = posture(AccessControlMode.ENFORCE, CredentialsMode.PLAIN, true);
        Assert.assertEquals(Drift.WEAKENED, current.driftFrom(HARDENED));
    }

    @Test
    public void droppingThePackageChecksumIsAWeakening() {
        SecurityPosture current = posture(AccessControlMode.ENFORCE, CredentialsMode.SIGNED, false);
        Assert.assertEquals(Drift.WEAKENED, current.driftFrom(HARDENED));
    }

    @Test
    public void aWeakeningOfOneFieldWinsOverAStrengtheningOfAnother() {
        SecurityPosture deployed = posture(AccessControlMode.AUDIT, CredentialsMode.SIGNED, true);
        // access-control audit -> enforce (stronger), but credentials signed -> plain (weaker)
        SecurityPosture current = posture(AccessControlMode.ENFORCE, CredentialsMode.PLAIN, true);
        Assert.assertEquals(Drift.WEAKENED, current.driftFrom(deployed));
    }

    @Test
    public void theChangeDescriptionNamesEachChangedField() {
        SecurityPosture current = posture(AccessControlMode.OFF, CredentialsMode.SIGNED, true);
        Assert.assertEquals("access-control: enforce -> off", current.describeChangeFrom(HARDENED));
    }
}
