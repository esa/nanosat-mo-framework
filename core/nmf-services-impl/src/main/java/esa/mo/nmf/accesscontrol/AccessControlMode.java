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

import java.util.Locale;
import java.util.logging.Logger;

/**
 * The access control mode, chosen by the mission when it generates its
 * filesystem and passed to the Supervisor by the bootloader.
 */
public enum AccessControlMode {

    /** No checks. */
    OFF,
    /** Nothing is refused, but what {@link #ENFORCE} would refuse is logged. */
    AUDIT,
    /** What an App was not granted is refused. The default. */
    ENFORCE;

    private static final Logger LOGGER = Logger.getLogger(AccessControlMode.class.getName());

    /**
     * The system property, set by the bootloader from {@code access-control} in
     * its configuration, that holds the mode: off, audit or enforce.
     */
    public static final String PROPERTY = "esa.mo.nmf.accesscontrol.mode";

    /**
     * Returns the mode in force. A missing or unknown value is {@link #ENFORCE},
     * so that a Supervisor started without the bootloader, such as from an IDE,
     * is not left open.
     *
     * @return the mode
     */
    public static AccessControlMode current() {
        return parse(System.getProperty(PROPERTY));
    }

    /**
     * Returns the mode that the given text names, ignoring case. A missing or
     * unknown value is {@link #ENFORCE}.
     *
     * @param value the text: off, audit or enforce
     * @return the mode
     */
    public static AccessControlMode parse(final String value) {
        if (value != null) {
            for (AccessControlMode mode : values()) {
                if (mode.name().equals(value.trim().toUpperCase(Locale.ROOT))) {
                    return mode;
                }
            }
            LOGGER.warning("Unknown access control mode '" + value + "': access control is enforced");
        }
        return ENFORCE;
    }

    /**
     * Returns the name of the mode as it is written in the configuration.
     *
     * @return off, audit or enforce
     */
    public String configName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
