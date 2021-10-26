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
package esa.mo.nmf.comarchivetool.commands.parameters;

import esa.mo.nmf.comarchivetool.ArchiveBrowserHelper;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ArgGroup;

import java.util.List;

/**
 * Definitions for the parameter commands
 *
 * @author Marcel Mikołajko
 */
public class ParametersCommandsDefinitions {

    @Command(name = "parameter", subcommands = {ListParameters.class, GetParameters.class},
            description = "Gets or lists MO parameters using the content of a local or remote COM archive.")
    public static class Parameter {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;
    }

    @Command(name = "list",
            description = "Lists available parameters in a COM archive.")
    public static class ListParameters implements Runnable {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;

        @ArgGroup(multiplicity = "1")
        ArchiveBrowserHelper.LocalOrRemote localOrRemote;

        /** {@inheritDoc} */
        @Override
        public void run() {
            ParametersCommandsImplementations.listParameters(localOrRemote.databaseFile, localOrRemote.providerURI);
        }
    }

    @Command(name = "get",
            description = "Dumps to a file MO parameters samples from COM archive.")
    public static class GetParameters implements Runnable {
        @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
        boolean helpRequested;

        @ArgGroup(multiplicity = "1")
        ArchiveBrowserHelper.LocalOrRemote localOrRemote;

        @Parameters(arity = "1", paramLabel = "<filename>", index = "0",
                    description = "Target file for the parameters samples")
        String parametersFile;

        @Parameters(arity = "0..*", paramLabel = "<parameterNames>", index = "1",
                    description = "Names of the parameters to retrieve\n"
                                  + " - examples: param1 or param1 param2")
        List<String> parameterNames;

        @Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
                description = "Restricts the dump to parameters generated after the given time\n"
                              + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
                              + "  - example: \"2021-03-04 08:37:58.482\"")
        String startTime;

        @Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
                description = "Restricts the dump to parameters generated before the given time. "
                              + "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n"
                              + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
                              + "  - example: \"2021-03-05 12:05:45.271\"")
        String endTime;

        @Option(names = {"-j", "--json"}, paramLabel = "<json>",
                description = "If specified output will be in JSON format")
        boolean json;

        @Override
        public void run() {
            ParametersCommandsImplementations.getParameters(localOrRemote.databaseFile, localOrRemote.providerURI,
                                                            startTime, endTime, parametersFile, parameterNames,
                                                            json);
        }
    }

}
