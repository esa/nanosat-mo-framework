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
package esa.mo.nmf.nmfpackage;

/**
 * Defines the isolation mode applied to NMF Apps managed by a Supervisor.
 * The active mode is set by the mission designer via the
 * {@code nmf-linux-maven-plugin} and passed to the Supervisor JVM as the
 * system property {@value #PROPERTY}.
 *
 * @author Cesar Coelho
 */
public class AppsIsolationMode {

    public static final String PROPERTY = "esa.mo.nmf.packagemanager.appsIsolation";

    /** Apps run as the user that launched the Supervisor. */
    public static final String NONE = "none";

    /** Each app runs under a dedicated Linux user account created at install time. */
    public static final String LINUX_USERSPACE = "linux-userspace";

    /** Each app runs inside a dedicated Docker container (not yet implemented). */
    public static final String DOCKER_CONTAINERS = "docker-containers";

    /** Each app runs inside a bubblewrap sandbox (not yet implemented). */
    public static final String BUBBLEWRAP = "bubblewrap";

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
     * Returns true if the active isolation mode is {@value #LINUX_USERSPACE}.
     *
     * @return Whether linux-userspace isolation is active.
     */
    public static boolean isLinuxUserspace() {
        return LINUX_USERSPACE.equals(getCurrent());
    }

}
