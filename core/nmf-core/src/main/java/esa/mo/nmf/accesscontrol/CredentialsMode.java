/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.accesscontrol;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * How a caller proves its credential to the Supervisor, chosen by the mission
 * when it generates its filesystem and passed to the Supervisor by the
 * bootloader.
 * <p>
 * The credential identifies the caller. Under {@link #PLAIN} the caller sends
 * it as it is, so whoever reads one request can send it again as that caller.
 * Under {@link #SIGNED}, the default, the caller never sends the credential: it
 * signs each request with it, over a counter that the Supervisor refuses to
 * accept twice, so that a request read off the wire cannot be sent again.
 */
public enum CredentialsMode {

    /**
     * The caller signs each request with its credential, which never leaves it.
     * The default.
     */
    SIGNED,

    /**
     * The caller sends its credential as it is. Meant for a Supervisor that can
     * be reached only through the loopback, where reading its traffic needs the
     * rights that already read the credential files.
     */
    PLAIN;

    private static final Logger LOGGER = Logger.getLogger(CredentialsMode.class.getName());

    /**
     * The system property, set by the bootloader from
     * {@code access-control-credentials} in its configuration and by the
     * Supervisor for each App it starts, that holds the mode: signed or plain.
     * A ground tool reads it too; those of a plain mission must be given it.
     */
    public static final String PROPERTY = "esa.mo.nmf.accesscontrol.credentials";

    /**
     * Returns the mode in force. A missing or unknown value is {@link #SIGNED},
     * so that a Supervisor started without the bootloader is not left sending
     * credentials in the clear.
     *
     * @return the mode
     */
    public static CredentialsMode current() {
        return parse(System.getProperty(PROPERTY));
    }

    /**
     * Returns the mode that the given text names, ignoring case. A missing or
     * unknown value is {@link #SIGNED}.
     *
     * @param value the text: signed or plain
     * @return the mode
     */
    public static CredentialsMode parse(final String value) {
        if (value != null) {
            for (CredentialsMode mode : values()) {
                if (mode.name().equals(value.trim().toUpperCase(Locale.ROOT))) {
                    return mode;
                }
            }
            LOGGER.warning("Unknown access control credentials mode '" + value + "': credentials are signed");
        }
        return SIGNED;
    }

    /**
     * Returns the name of the mode as it is written in the configuration.
     *
     * @return signed or plain
     */
    public String configName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
