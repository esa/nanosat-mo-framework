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
package esa.mo.nmf.comarchivetool.commands.archive;

import esa.mo.nmf.comarchivetool.ArchiveBrowserHelper;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Definitions for the archive commands
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
public class ArchiveCommandsDefinitions {

    @Command(name = "dump_raw", description = "Dumps to a JSON file the raw tables content of a local COM archive")
    public static class DumpRawArchive implements Runnable {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;

        @Parameters(arity = "1", paramLabel = "<databaseFile>", description = "Local SQLite database file\n" +
            "  - example: ../nanosat-mo-supervisor-sim/comArchive.db")
        String databaseFile;

        @Parameters(arity = "1", paramLabel = "<jsonFile>", description = "target JSON file")
        String jsonFile;

        @Override
        public void run() {
            ArchiveCommandsImplementations.dumpRawArchiveTables(databaseFile, jsonFile);
        }
    }

    @Command(name = "backup_and_clean", description = "Backups the data for a specific provider")
    public static class BackupProvider implements Runnable {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;

        @ArgGroup(multiplicity = "1")
        ArchiveBrowserHelper.LocalOrRemote localOrRemote;

        @Option(names = {"-o", "--output"}, paramLabel = "<filename>", description = "target file name")
        String filename;

        @Parameters(arity = "1", index = "0", paramLabel = "<domainId>",
                    description = "Restricts the dump to objects in a specific domain\n" +
                        "  - format: key1.key2.[...].keyN.\n" + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domain;

        @Option(names = {"-p", "--provider"}, paramLabel = "<appName>",
                description = "Name of the NMF app we want to backup")
        String appName;

        @Override
        public void run() {
            ArchiveCommandsImplementations.backupProvider(localOrRemote.databaseFile, localOrRemote.providerURI, domain,
                appName, filename);
        }
    }

    @Command(name = "dump", description = "Dumps to a JSON file the formatted content of a local or remote COM archive")
    public static class DumpFormattedArchive implements Runnable {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;

        @ArgGroup(multiplicity = "1")
        ArchiveBrowserHelper.LocalOrRemote localOrRemote;

        @Parameters(arity = "1", paramLabel = "<jsonFile>", description = "target JSON file")
        String jsonFile;

        @Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
                description = "Restricts the dump to objects in a specific domain\n" +
                    "  - format: key1.key2.[...].keyN.\n" + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domain;

        @Option(names = {"-t", "--type"}, paramLabel = "<comType>",
                description = "Restricts the dump to objects that are instances of <comType>\n" +
                    "  - format: areaNumber.serviceNumber.areaVersion.objectNumber.\n" +
                    "  - examples (0=wildcard): 4.2.1.1, 4.2.1.0 ")
        String comType;

        @Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
                description = "Restricts the dump to objects created after the given time\n" +
                    "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-04 08:37:58.482\"")
        String startTime;

        @Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
                description = "Restricts the dump to objects created before the given time. " +
                    "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n" +
                    "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n" + "  - example: \"2021-03-05 12:05:45.271\"")
        String endTime;

        @Override
        public void run() {
            ArchiveCommandsImplementations.dumpFormattedArchive(localOrRemote.databaseFile, localOrRemote.providerURI,
                domain, comType, startTime, endTime, jsonFile);
        }
    }

    @Command(name = "list", description = "Lists the COM archive providers URIs found in a central directory")
    public static class ListArchiveProviders implements Runnable {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;

        @Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
                    description = "URI of the central directory to use")
        String centralDirectoryURI;

        @Override
        public void run() {
            ArchiveCommandsImplementations.listArchiveProviders(centralDirectoryURI);
        }
    }
}
