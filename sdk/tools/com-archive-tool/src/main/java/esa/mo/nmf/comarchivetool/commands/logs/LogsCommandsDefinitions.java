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
package esa.mo.nmf.comarchivetool.commands.logs;

import esa.mo.nmf.comarchivetool.ArchiveBrowserHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ArgGroup;

/**
 * Definitions for the log commands
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
public class LogsCommandsDefinitions {

    @Command(name = "log", subcommands = {ListLogs.class, GetLogs.class},
            description = "Gets or lists NMF app logs using the content of a local or remote COM archive.")
    public static class Logs {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;
    }

    @Command(name = "list",
            description = "Lists NMF apps having logs in the content of a local or remote COM archive.")
    public static class ListLogs implements Runnable {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;

        @ArgGroup(multiplicity = "1")
        ArchiveBrowserHelper.LocalOrRemote localOrRemote;

        @Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
                description = "Restricts the list to NMF apps in a specific domain\n"
                              + "  - default: search for app in all domains\n" + "  - format: key1.key2.[...].keyN.\n"
                              + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domain;

        @Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
                description = "Restricts the list to NMF apps having logs logged after the given time\n"
                              + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
                              + "  - example: \"2021-03-04 08:37:58.482\"")
        String startTime;

        @Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
                description = "Restricts the list to NMF apps having logs logged before the given time. "
                              + "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n"
                              + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
                              + "  - example: \"2021-03-05 12:05:45.271\"")
        String endTime;

        /** {@inheritDoc} */
        @Override
        public void run() {
            LogsCommandsImplementations.listLogs(localOrRemote.databaseFile, localOrRemote.providerURI, domain,
                                                 startTime, endTime);
        }
    }

    @Command(name = "get",
            description = "Dumps to a LOG file an NMF app logs using the content of a local or remote COM archive.")
    public static class GetLogs implements Runnable {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;

        @ArgGroup(multiplicity = "1")
        ArchiveBrowserHelper.LocalOrRemote localOrRemote;

        @Parameters(arity = "1", paramLabel = "<appName>",
                description = "Name of the NMF app we want the logs for")
        String appName;

        @Parameters(arity = "1", paramLabel = "<logFile>", description = "target LOG file")
        String logFile;

        @Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
                description = "Domain of the NMF app we want the logs for\n"
                              + "  - default: search for app in all domains\n" + "  - format: key1.key2.[...].keyN.\n"
                              + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
        String domain;

        @Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
                description = "Restricts the dump to logs logged after the given time\n"
                              + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
                              + "  - example: \"2021-03-04 08:37:58.482\"")
        String startTime;

        @Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
                description = "Restricts the dump to logs logged before the given time. "
                              + "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n"
                              + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
                              + "  - example: \"2021-03-05 12:05:45.271\"")
        String endTime;

        @Option(names = {"-t", "--timestamped"}, paramLabel = "<addTimestamps>",
                description = "If specified additional timestamp will be added to each line")
        boolean addTimestamps;

        @Override
        public void run() {
            LogsCommandsImplementations.getLogs(localOrRemote.databaseFile, localOrRemote.providerURI, appName,
                                                domain, startTime, endTime, logFile, addTimestamps);
        }
    }
}
