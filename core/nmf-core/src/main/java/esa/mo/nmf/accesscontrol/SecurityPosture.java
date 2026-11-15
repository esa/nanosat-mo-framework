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

import java.util.Properties;

/**
 * The security posture of a deployment: the access control mode, the
 * credentials mode, and whether a package checksum is required. It is recorded
 * at filesystem generation as the {@code security-baseline.properties}
 * reference and compared, at boot, against the posture that is actually in
 * force, so that a change from what was deployed can be warned about.
 *
 * <p>The comparison is by strength, not by value, so that a change can be told
 * apart as a strengthening or a weakening: a weakening reduces on-board
 * protection and may indicate tampering, a strengthening does not.
 *
 * @author Cesar Coelho
 */
public final class SecurityPosture {

    /** Property key for the access control mode. */
    public static final String KEY_ACCESS_CONTROL = "access-control";
    /** Property key for the credentials mode. */
    public static final String KEY_CREDENTIALS = "access-control-credentials";
    /** Property key for whether a package checksum is required. */
    public static final String KEY_REQUIRE_CHECKSUM = "require-package-checksum";

    /** The direction of a change in security posture. */
    public enum Drift {
        /** The posture is the same as the one it is compared against. */
        SAME,
        /** At least one field is stronger and none is weaker. */
        STRENGTHENED,
        /** At least one field is weaker, which reduces on-board protection. */
        WEAKENED
    }

    private final AccessControlMode mode;
    private final CredentialsMode credentials;
    private final boolean requireChecksum;

    /**
     * Constructor.
     *
     * @param mode the access control mode
     * @param credentials the credentials mode
     * @param requireChecksum whether a package checksum is required
     */
    public SecurityPosture(final AccessControlMode mode, final CredentialsMode credentials,
            final boolean requireChecksum) {
        this.mode = mode;
        this.credentials = credentials;
        this.requireChecksum = requireChecksum;
    }

    /**
     * Reads a security posture from properties, defaulting a missing or unknown
     * value to its strongest setting.
     *
     * @param p the properties to read, with the {@code KEY_*} keys
     * @return the security posture
     */
    public static SecurityPosture fromProperties(final Properties p) {
        return new SecurityPosture(
                AccessControlMode.parse(p.getProperty(KEY_ACCESS_CONTROL)),
                CredentialsMode.parse(p.getProperty(KEY_CREDENTIALS)),
                !"false".equalsIgnoreCase(p.getProperty(KEY_REQUIRE_CHECKSUM, "true")));
    }

    // Strength of each field: a higher number is a stronger (safer) setting.
    private static int strength(final AccessControlMode m) {
        switch (m) {
            case OFF:
                return 0;
            case AUDIT:
                return 1;
            default:
                return 2; // ENFORCE
        }
    }

    private static int strength(final CredentialsMode c) {
        // SIGNED is stronger than PLAIN; the enum's own order is not by strength
        return (c == CredentialsMode.SIGNED) ? 1 : 0;
    }

    private static int strength(final boolean requireChecksum) {
        return requireChecksum ? 1 : 0;
    }

    /**
     * Returns how this posture differs, in strength, from a deployed one. A
     * weakening of any field wins over a strengthening of another, because the
     * weakening is what reduces protection.
     *
     * @param deployed the posture that was deployed
     * @return the direction of the change
     */
    public Drift driftFrom(final SecurityPosture deployed) {
        boolean weaker = strength(mode) < strength(deployed.mode)
                || strength(credentials) < strength(deployed.credentials)
                || strength(requireChecksum) < strength(deployed.requireChecksum);
        boolean stronger = strength(mode) > strength(deployed.mode)
                || strength(credentials) > strength(deployed.credentials)
                || strength(requireChecksum) > strength(deployed.requireChecksum);
        if (weaker) {
            return Drift.WEAKENED;
        }
        if (stronger) {
            return Drift.STRENGTHENED;
        }
        return Drift.SAME;
    }

    /**
     * Returns a readable description of the fields that differ from a deployed
     * posture, each as {@code key: deployed -> current}.
     *
     * @param deployed the posture that was deployed
     * @return the description, empty if nothing differs
     */
    public String describeChangeFrom(final SecurityPosture deployed) {
        StringBuilder str = new StringBuilder();
        if (mode != deployed.mode) {
            append(str, KEY_ACCESS_CONTROL, deployed.mode.configName(), mode.configName());
        }
        if (credentials != deployed.credentials) {
            append(str, KEY_CREDENTIALS, deployed.credentials.configName(), credentials.configName());
        }
        if (requireChecksum != deployed.requireChecksum) {
            append(str, KEY_REQUIRE_CHECKSUM, String.valueOf(deployed.requireChecksum),
                    String.valueOf(requireChecksum));
        }
        return str.toString();
    }

    private static void append(final StringBuilder str, final String key, final String from, final String to) {
        if (str.length() > 0) {
            str.append("; ");
        }
        str.append(key).append(": ").append(from).append(" -> ").append(to);
    }
}
