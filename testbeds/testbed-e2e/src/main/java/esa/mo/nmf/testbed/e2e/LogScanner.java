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
package esa.mo.nmf.testbed.e2e;

/**
 * Utility methods for scanning NMF log output during test harness startup.
 */
class LogScanner {

    private LogScanner() {
    }

    /**
     * Extracts the Directory service URI from a single log line.
     * Both the Supervisor and NMF apps emit a line of the form
     * {@code ... URI: maltcp://host:port/name-Directory} once the Directory
     * service is fully initialised.
     *
     * @param line a single line of log output.
     * @return the URI string if found, or {@code null}.
     */
    static String extractDirectoryURI(String line) {
        int idx = line.indexOf("URI: ");
        if (idx >= 0) {
            String candidate = line.substring(idx + 5).trim();
            if (candidate.contains("-Directory")) {
                return candidate;
            }
        }
        return null;
    }
}
