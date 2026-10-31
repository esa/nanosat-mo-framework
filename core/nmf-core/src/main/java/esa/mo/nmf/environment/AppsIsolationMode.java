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
package esa.mo.nmf.environment;

import java.util.List;

/**
 * Defines the isolation mode applied to NMF Apps managed by a Supervisor.
 * The active mode is set by the mission designer via the
 * {@code nmf-linux-maven-plugin} and passed to the Supervisor JVM as the
 * system property {@value #PROPERTY}.
 *
 * @author Cesar Coelho
 */
public class AppsIsolationMode {
    private AppsIsolationMode() {
    }


    /** The property. */
    public static final String PROPERTY = "esa.mo.nmf.packagemanager.appsIsolation";

    /** Apps run as the user that launched the Supervisor. */
    public static final String NONE = "none";

    /** Each app runs under a dedicated Linux user account created at install time. */
    public static final String LINUX_USERSPACE = "linux-userspace";

    /** Each app runs inside a dedicated Docker container. */
    public static final String DOCKER_CONTAINERS = "docker-containers";

    /** Each app runs inside a bubblewrap sandbox. */
    public static final String BUBBLEWRAP = "bubblewrap";

    /** The isolation modes. */
    public static final List<String> MODES = List.of(NONE, LINUX_USERSPACE, BUBBLEWRAP, DOCKER_CONTAINERS);

    /**
     * Returns why the isolation mode is not valid, or null if it is. The mode is
     * valid if it is one of the {@link #MODES}, or if the property is not set at
     * all, as when the Supervisor is started from an IDE, which means
     * {@value #NONE}. Any other value, an empty one included, is not valid: the
     * isolation that the mission chose cannot be known, so no App may be run,
     * rather than run without isolation (NMF-2026-013).
     *
     * @return The reason, or null if the mode is valid.
     */
    public static String invalidModeReason() {
        String value = System.getProperty(PROPERTY);
        if (value == null || MODES.contains(value)) {
            return null;
        }
        return "The isolation mode of the Apps '" + value + "' is not one of " + String.join(", ", MODES)
                + ": no App is run until apps-isolation in bootloader/config.properties is one of them";
    }

    /**
     * Returns the active isolation mode read from the system property,
     * defaulting to {@value #NONE} if the property is not set.
     *
     * @return The active isolation mode string.
     */
    public static String getCurrent() {
        String value = System.getProperty(PROPERTY);
        return (value != null) ? value : NONE;
    }

    /**
     * Returns true if the active isolation mode is {@value #NONE}.
     *
     * @return Whether apps run without isolation.
     */
    public static boolean isNone() {
        return NONE.equals(getCurrent());
    }

    /**
     * Returns true if the active isolation mode is {@value #LINUX_USERSPACE}.
     *
     * @return Whether linux-userspace isolation is active.
     */
    public static boolean isLinuxUserspace() {
        return LINUX_USERSPACE.equals(getCurrent());
    }

    /**
     * Returns true if the active isolation mode is {@value #DOCKER_CONTAINERS}.
     *
     * @return Whether docker-containers isolation is active.
     */
    public static boolean isDockerContainers() {
        return DOCKER_CONTAINERS.equals(getCurrent());
    }

    /**
     * Returns true if the active isolation mode is {@value #BUBBLEWRAP}.
     *
     * @return Whether bubblewrap isolation is active.
     */
    public static boolean isBubblewrap() {
        return BUBBLEWRAP.equals(getCurrent());
    }

}
