// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the CLI commands available. We have one main command with no name that is composed of
 * sub-commands.
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

  @Command(name = "dump", description = "Dumps a COM archive to a JSON file")
  private void dump(
      @Option(names = {"-r", "--raw"},
          description = "dumps the raw SQLite tables content") boolean rawDump,
      @Parameters(arity = "1", paramLabel = "<databaseFile>",
          description = "source SQLite database file") String databaseFile,
      @Parameters(arity = "1", paramLabel = "<jsonFile>",
          description = "target JSON file") String jsonFile) {
    if (rawDump) {
      cmdImpl.dumpRawArchiveTables(databaseFile, jsonFile);
    }
    else {
      cmdImpl.dumpFormattedArchive();
    }
  }
}
