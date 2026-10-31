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

import esa.mo.nmf.nmfpackage.metadata.Metadata;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The permissions that an App can declare, and the form they take.
 * <p>
 * A permission names whose service it is, then the area and the service
 * exactly as in the MO service definitions:
 * <ul>
 * <li>{@code Supervisor.<Area>.<Service>} for a service of the Supervisor,
 * for example {@code Supervisor.Platform.Camera};</li>
 * <li>{@code App.<name>.*} for all the services of another App;</li>
 * <li>{@code App.*} for all the services of every App.</li>
 * </ul>
 */
public final class Permissions {

    /** The prefix of a permission to a service of the Supervisor. */
    public static final String SUPERVISOR = "Supervisor.";

    /** The prefix of a permission to the services of other Apps. */
    public static final String APP = "App.";

    /** The suffix that stands for all the services of an App. */
    public static final String ALL_SERVICES = ".*";

    /** The services of the MO service definitions, as {@code <Area>.<Service>}. */
    private static final Set<String> SERVICES = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(
            "COM.Archive", "COM.ArchiveSync", "COM.Directory", "COM.Login",
            "MC.Action", "MC.Parameter", "MC.Alert", "MC.Aggregation", "MC.Conversion",
            "SM.PackageManagement", "SM.Heartbeat", "SM.CommandExecutor", "SM.AppsLauncher",
            "Platform.Camera", "Platform.GPS", "Platform.AutonomousADCS",
            "Platform.SoftwareDefinedRadio", "Platform.OpticalDataReceiver", "Platform.FPGA",
            "Platform.PowerControl", "Platform.ArtificialIntelligence", "Platform.SoftwareImages")));

    private Permissions() {
    }

    /**
     * Returns whether the given text is a valid permission.
     *
     * @param permission the text to check
     * @return true if it is a valid permission
     */
    public static boolean isValid(final String permission) {
        if (permission == null) {
            return false;
        }
        if (permission.startsWith(SUPERVISOR)) {
            return SERVICES.contains(permission.substring(SUPERVISOR.length()));
        }
        if (permission.equals(APP + "*")) {
            return true;
        }
        if (permission.startsWith(APP) && permission.endsWith(ALL_SERVICES)) {
            String app = permission.substring(APP.length(), permission.length() - ALL_SERVICES.length());
            return Metadata.isValidPackageName(app);
        }
        return false;
    }

    /**
     * Returns the services that a permission of the Supervisor can name, as
     * {@code <Area>.<Service>}.
     *
     * @return the services
     */
    public static Set<String> services() {
        return SERVICES;
    }
}
