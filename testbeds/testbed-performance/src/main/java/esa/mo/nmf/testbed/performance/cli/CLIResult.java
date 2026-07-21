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
package esa.mo.nmf.testbed.performance.cli;

/**
 * Holds the outcome of a single CLI Tool invocation: elapsed wall-clock time,
 * the captured stdout/stderr output, and the process exit code.
 */
public class CLIResult {

    public final long elapsedMs;
    public final String output;
    public final int exitCode;

    public CLIResult(long elapsedMs, String output, int exitCode) {
        this.elapsedMs = elapsedMs;
        this.output = output;
        this.exitCode = exitCode;
    }
}
