/* ----------------------------------------------------------------------------
 * Copyright (C) 2023      European Space Agency
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
package esa.mo.nmf.clitool;

/**
 * Exit codes from: /usr/include/sysexits.h
 *
 * @author Cesar Coelho
 */
public class ExitCodes {

    private ExitCodes() {
    }

    /** Successful termination. */
    public static final int OK = 0;
    /** Generic, unspecified error. */
    public static final int GENERIC_ERROR = 1;
    /** Base value for the {@code sysexits.h} error codes. */
    public static final int BASE = 64;
    /** Command line usage error. */
    public static final int USAGE = 64;
    /** Data format error. */
    public static final int NO_DATA = 65;
    /** Cannot open the input. */
    public static final int NO_INPUT = 66;
    /** The specified user does not exist. */
    public static final int NO_USER = 67;
    /** The specified host does not exist. */
    public static final int NO_HOST = 68;
    /** A required service is unavailable. */
    public static final int UNAVAILABLE = 69;
    /** Internal software error. */
    public static final int SOFTWARE = 70;
    /** Operating system error. */
    public static final int OS_ERROR = 71;
    /** A critical operating system file is missing. */
    public static final int OS_FILE = 72;
    /** Cannot create an output file. */
    public static final int CAN_NOT_CREATE = 73;
    /** Input/output error. */
    public static final int IO_ERROR = 74;
    /** Temporary failure; the operation may be retried. */
    public static final int TEMP_FAIL = 75;
    /** Remote error in the protocol. */
    public static final int PROTOCOL = 76;
    /** Permission denied. */
    public static final int NO_PERMISSION = 77;
    /** Configuration error. */
    public static final int CONFIG = 78;
}
