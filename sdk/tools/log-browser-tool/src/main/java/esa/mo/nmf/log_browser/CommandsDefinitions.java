// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser;

import esa.mo.helpertools.misc.Const;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the CLI commands available. We have one main command that is composed of sub-commands.
 *
 * @author Tanguy Soto
 */

@Command(name = "LogBrowser", subcommands = {CommandLine.HelpCommand.class},
    description = "Browses a COM archive to retrieve logs and more from it.")
public class CommandsDefinitions {

  /**
   * Object holding the implementations of the commands.
   */
  private CommandsImplementations cmdImpl;

  /**
   * Creates a new instance of CommandsDefinitions.
   * 
   * @param cmdImpl Object holding the implementations of the commands.
   */
  public CommandsDefinitions(CommandsImplementations cmdImpl) {
    this.cmdImpl = cmdImpl;
  }

  @Command(name = "dump_raw_archive",
      description = "Dumps to a JSON file the raw tables content of a SQLite COM archive")
  private void dumpRawArchive(
      @Parameters(arity = "1", paramLabel = "<databaseFile>",
          description = "source SQLite database file") String databaseFile,
      @Parameters(arity = "1", paramLabel = "<jsonFile>",
          description = "target JSON file") String jsonFile) {
    cmdImpl.dumpRawArchiveTables(databaseFile, jsonFile);
  }

  @Command(sortOptions = false, name = "dump_archive",
      description = "Dumps to a JSON file the formatted content of a COM archive provider")
  private void dumpFormattedArchive(
      @Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
          description = "URI of the central directory to use") String centralDirectoryURI,
      @Option(order = 1, names = {"-p", "--provider"}, paramLabel = "<providerName>",
          description = "Name of the COM archive provider to query\n" + "  - default: "
              + Const.NANOSAT_MO_SUPERVISOR_NAME) String providerName,
      @Option(order = 2, names = {"-d", "--domain"}, paramLabel = "<domainId>",
          description = "Restricts the dump to objects in a specific domain\n"
              + "  - format: key1.key2.[...].keyN.\n"
              + "  - example: esa.NMF_SDK.nanosat-mo-supervisor") String domain,
      @Option(order = 3, names = {"-t", "--type"}, paramLabel = "<comType>",
          description = "Restricts the dump to objects that are instances of <comType>\n"
              + "  - format: areaNumber.serviceNumber.areaVersion.objectNumber.\n"
              + "  - examples (0=wildcard): 4.2.1.1, 4.2.1.0 ") String comType,
      @Option(order = 4, names = {"-s", "--start"}, paramLabel = "<startTime>",
          description = "Restricts the dump to objects created after the given time\n"
              + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
              + "  - example: \"2021-03-04 08:37:58.482\"") String startTime,
      @Option(order = 5, names = {"-e", "--end"}, paramLabel = "<endTime>",
          description = "Restricts the dump to objects created before the given time. "
              + "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n"
              + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
              + "  - example: \"2021-03-05 12:05:45.271\"") String endTime,
      @Parameters(arity = "1", paramLabel = "<jsonFile>",
          description = "target JSON file") String jsonFile) {
    if (providerName == null) {
      providerName = Const.NANOSAT_MO_SUPERVISOR_NAME;
    }
    cmdImpl.dumpFormattedArchive(centralDirectoryURI, providerName, domain, comType, startTime,
        endTime, jsonFile);
  }

  @Command(name = "list_archive_providers",
      description = "Lists the COM archive providers names found in the central directory")
  private void listArchiveProviders(@Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
      description = "URI of the central directory to use") String centralDirectoryURI) {
    cmdImpl.listArchiveProviders(centralDirectoryURI);
  }
}
