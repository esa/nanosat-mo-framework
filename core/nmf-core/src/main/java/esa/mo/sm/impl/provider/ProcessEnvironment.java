/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.sm.impl.provider;

import java.util.Map;

/**
 * Process Environment.
 * <p>
 * Provides the environment of a process in the two representations required by
 * this package. Both operations were previously obtained from Apache Commons
 * Exec ({@code EnvironmentUtils}) and are available from the JDK, and that
 * dependency has therefore been removed.
 */
final class ProcessEnvironment {

    private ProcessEnvironment() {
    }

    /**
     * Formats an environment as the array of {@code name=value} entries
     * expected by {@link ProcessBuilder} and by the shell.
     *
     * @param environment The environment variables, may be null.
     * @return One entry per variable, in unspecified order. Empty if the
     * environment is null or contains no variables; never null.
     */
    static String[] toStrings(final Map<String, String> environment) {
        if (environment == null) {
            return new String[0];
        }
        final String[] formatted = new String[environment.size()];
        int i = 0;
        for (final Map.Entry<String, String> variable : environment.entrySet()) {
            // A null value is formatted as "name=", denoting a variable that is set
            // but empty. This is distinct from omitting the variable entirely.
            formatted[i++] = variable.getKey() + "="
                    + (variable.getValue() == null ? "" : variable.getValue());
        }
        return formatted;
    }

    /**
     * Returns the environment with which the current process was started.
     * <p>
     * The map returned by {@link System#getenv()} is propagated directly rather
     * than copied. On Windows the JDK constructs that map with a
     * case-insensitive comparator, environment variable names being
     * case-insensitive on that platform. Copying it into a {@code HashMap}
     * would discard that property, so that a lookup of {@code "PATH"} would no
     * longer resolve a variable named {@code "Path"}.
     *
     * @return The environment variables, unmodifiable, with the lookup
     * semantics of the host platform.
     */
    static Map<String, String> ofThisProcess() {
        return System.getenv();
    }
}
