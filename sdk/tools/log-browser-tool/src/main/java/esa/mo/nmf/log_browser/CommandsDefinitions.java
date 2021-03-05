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
      description = "Dumps to a JSON file the raw SQLite tables content of a COM archive")
  private void dumpRawArchive(
      @Parameters(arity = "1", paramLabel = "<databaseFile>",
          description = "source SQLite database file") String databaseFile,
      @Parameters(arity = "1", paramLabel = "<jsonFile>",
          description = "target JSON file") String jsonFile) {
    cmdImpl.dumpRawArchiveTables(databaseFile, jsonFile);
  }

  @Command(name = "dump_archive",
      description = "Dumps to a JSON file the formatted content of a COM archive provider")
  private void dumpFormattedArchive(
      @Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
          description = "URI of the central directory to use") String centralDirectoryURI,
      @Option(names = {"-p", "--provider"}, paramLabel = "provider_name",
          description = "Name of the COM archive provider, defaults to "
              + Const.NANOSAT_MO_SUPERVISOR_NAME) String providerName,
      @Parameters(arity = "1", paramLabel = "<jsonFile>",
          description = "target JSON file") String jsonFile) {
    if (providerName == null) {
      providerName = Const.NANOSAT_MO_SUPERVISOR_NAME;
    }
    cmdImpl.dumpFormattedArchive(centralDirectoryURI, providerName, jsonFile);
  }

  @Command(name = "list_archive_providers",
      description = "Lists the COM archive providers names found in the central directory")
  private void listArchiveProviders(@Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
      description = "URI of the central directory to use") String centralDirectoryURI) {
    cmdImpl.listArchiveProviders(centralDirectoryURI);
  }
}
