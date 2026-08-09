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
package opssat.simulator.util;

import java.io.File;

/**
 * The names of the files and directories the simulator keeps outside itself.
 *
 * They were spelled out wherever they were needed, in six places across two
 * classes, which is a poor arrangement for names that have to agree: a
 * simulator that writes one file and reads another looks like it has lost the
 * settings rather than like it has a typo. They are gathered here so that
 * there is one place to change them.
 *
 * The leading underscore is deliberate. These land in whichever directory the
 * simulator was started from, alongside whatever else is there, and the
 * underscore keeps them together at the top of the listing.
 *
 * @author Cesar Coelho
 */
public final class SimulatorFiles {

    /** Prefix shared by every configuration file written beside the simulator. */
    private static final String PREFIX = "_OPS-SAT-SIMULATOR-";

    /** General configuration: time factor, orbit, Celestia, logging levels. */
    public static final String HEADER = PREFIX + "header.txt";

    /** The commands the simulator is to run, and when. */
    public static final String SCHEDULER = PREFIX + "scheduler.txt";

    /** Command templates offered by the user interface. */
    public static final String TEMPLATES = PREFIX + "templates.txt";

    /** Which commands the user interface shows. */
    public static final String COMMANDS_FILTER = PREFIX + "filter.txt";

    /** Directory under the user's home holding the logs and the data folder. */
    public static final String DATA_DIRECTORY = File.separator + ".ops-sat-simulator" + File.separator;

    /** Resources within {@link #DATA_DIRECTORY}. */
    public static final String RESOURCES_DIRECTORY = DATA_DIRECTORY + "resources" + File.separator;

    private SimulatorFiles() {
    }

    /**
     * The name for a copy of the scheduler file kept before it is overwritten.
     *
     * @param timestamp When the copy is being taken, already formatted.
     * @return The file name.
     */
    public static String schedulerBackup(String timestamp) {
        return PREFIX + "scheduler_backup_" + timestamp + ".txt";
    }
}
