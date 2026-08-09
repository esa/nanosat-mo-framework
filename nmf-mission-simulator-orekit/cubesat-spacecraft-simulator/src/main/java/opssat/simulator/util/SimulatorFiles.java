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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The names of the files and directories the simulator keeps outside itself.
 *
 * They were once spelled out wherever they were needed, six times across two
 * classes. Names that have to agree belong in one place: a simulator that
 * writes one file and reads another does not look like it has a typo, it looks
 * like it has lost the settings.
 *
 * The leading underscore is deliberate. These land in whichever directory the
 * simulator was started from, alongside whatever else is there, and the
 * underscore keeps them together at the top of the listing.
 *
 * @author Cesar Coelho
 */
public final class SimulatorFiles {

    /** Prefix shared by every configuration file written beside the simulator. */
    private static final String PREFIX = "_SIMULATOR-";

    /** What that prefix was while the simulator was only ever OPS-SAT's. */
    private static final String LEGACY_PREFIX = "_OPS-SAT-SIMULATOR-";

    private static final String HEADER_SUFFIX = "header.txt";
    private static final String SCHEDULER_SUFFIX = "scheduler.txt";
    private static final String TEMPLATES_SUFFIX = "templates.txt";
    private static final String COMMANDS_FILTER_SUFFIX = "filter.txt";

    /** General configuration: time factor, orbit, Celestia, logging levels. */
    public static final String HEADER = PREFIX + HEADER_SUFFIX;

    /** The commands the simulator is to run, and when. */
    public static final String SCHEDULER = PREFIX + SCHEDULER_SUFFIX;

    /** Command templates offered by the user interface. */
    public static final String TEMPLATES = PREFIX + TEMPLATES_SUFFIX;

    /** Which commands the user interface shows. */
    public static final String COMMANDS_FILTER = PREFIX + COMMANDS_FILTER_SUFFIX;

    private static final String DATA_DIRECTORY_NAME = ".nmf-simulator";
    private static final String LEGACY_DATA_DIRECTORY_NAME = ".ops-sat-simulator";

    /** Directory under the user's home holding the logs and the data folder. */
    public static final String DATA_DIRECTORY = File.separator + DATA_DIRECTORY_NAME + File.separator;

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

    /**
     * Renames anything left under the old OPS-SAT names.
     *
     * These files hold settings somebody chose: how fast the simulation runs,
     * whether Celestia is served, the whole schedule of commands. Renaming them
     * without moving what is already on disk would leave the simulator finding
     * nothing, writing fresh defaults over the top, and saying nothing about
     * it, so what is there is carried across and the move is reported.
     *
     * Doing this twice is harmless: once the old names are gone there is
     * nothing left to find. Timestamped scheduler backups are left where they
     * are, being historical copies rather than anything the simulator reads.
     *
     * @param workingDir The directory the simulator was started from.
     * @param logger Where to report the move, or null for a logger of its own.
     */
    public static void migrateLegacyNames(String workingDir, Logger logger) {
        Logger log = (logger != null) ? logger : Logger.getLogger(SimulatorFiles.class.getName());

        String[] suffixes = {HEADER_SUFFIX, SCHEDULER_SUFFIX, TEMPLATES_SUFFIX, COMMANDS_FILTER_SUFFIX};
        for (String suffix : suffixes) {
            moveIfLeftBehind(new File(workingDir, LEGACY_PREFIX + suffix),
                    new File(workingDir, PREFIX + suffix), log);
        }

        File home = new File(System.getProperty("user.home"));
        moveIfLeftBehind(new File(home, LEGACY_DATA_DIRECTORY_NAME),
                new File(home, DATA_DIRECTORY_NAME), log);
    }

    /**
     * Moves one to the other, if the first is there and the second is not.
     *
     * An existing destination is never touched: if both names are present then
     * somebody has already been here, and the one in use is the newer name.
     */
    private static void moveIfLeftBehind(File legacy, File current, Logger log) {
        if (!legacy.exists() || current.exists()) {
            return;
        }
        if (legacy.renameTo(current)) {
            log.log(Level.INFO, "Renamed {0} to {1}, the simulator no longer being OPS-SAT''s alone",
                    new Object[]{legacy.getName(), current.getName()});
        } else {
            log.log(Level.WARNING, "Could not rename {0} to {1}. The simulator will start from "
                    + "defaults instead of what is in the older file.",
                    new Object[]{legacy.getName(), current.getName()});
        }
    }
}
