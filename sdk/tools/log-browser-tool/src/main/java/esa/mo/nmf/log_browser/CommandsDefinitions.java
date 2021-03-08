// (C) 2021 European Space Agency
// European Space Operations Centre
// Darmstadt, Germany

package esa.mo.nmf.log_browser;

import esa.mo.helpertools.misc.Const;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the CLI commands available.
 *
 * @author Tanguy Soto
 */

@Command(name = "LogBrowser",
    subcommands = {DumpRawArchive.class, DumpFormattedArchive.class, GetLogs.class,
        ListArchiveProviders.class},
    description = "Browses a COM archive to retrieve logs and more from it.")
public class CommandsDefinitions {

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  private boolean helpRequested;
}


@Command(name = "dump_raw",
    description = "Dumps to a JSON file the raw tables content of a SQLite COM archive")
class DumpRawArchive implements Runnable {
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  boolean helpRequested;

  @Parameters(arity = "1", paramLabel = "<databaseFile>",
      description = "source SQLite database file")
  String databaseFile;

  @Parameters(arity = "1", paramLabel = "<jsonFile>", description = "target JSON file")
  String jsonFile;

  @Override
  public void run() {
    CommandsImplementations.dumpRawArchiveTables(databaseFile, jsonFile);
  }
}


@Command(sortOptions = false, name = "dump",
    description = "Dumps to a JSON file the formatted content of a COM archive provider")
class DumpFormattedArchive implements Runnable {
  @Option(order = 1, names = {"-h", "--help"}, usageHelp = true,
      description = "display a help message")
  boolean helpRequested;

  @Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
      description = "URI of the central directory to use")
  String centralDirectoryURI;

  @Option(order = 2, names = {"-p", "--provider"}, paramLabel = "<providerName>",
      description = "Name of the COM archive provider to query\n" + "  - default: "
          + Const.NANOSAT_MO_SUPERVISOR_NAME)
  String providerName;

  @Option(order = 3, names = {"-d", "--domain"}, paramLabel = "<domainId>",
      description = "Restricts the dump to objects in a specific domain\n"
          + "  - format: key1.key2.[...].keyN.\n"
          + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
  String domain;

  @Option(order = 4, names = {"-t", "--type"}, paramLabel = "<comType>",
      description = "Restricts the dump to objects that are instances of <comType>\n"
          + "  - format: areaNumber.serviceNumber.areaVersion.objectNumber.\n"
          + "  - examples (0=wildcard): 4.2.1.1, 4.2.1.0 ")
  String comType;

  @Option(order = 5, names = {"-s", "--start"}, paramLabel = "<startTime>",
      description = "Restricts the dump to objects created after the given time\n"
          + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
          + "  - example: \"2021-03-04 08:37:58.482\"")
  String startTime;

  @Option(order = 6, names = {"-e", "--end"}, paramLabel = "<endTime>",
      description = "Restricts the dump to objects created before the given time. "
          + "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n"
          + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
          + "  - example: \"2021-03-05 12:05:45.271\"")
  String endTime;

  @Parameters(arity = "1", paramLabel = "<jsonFile>", description = "target JSON file")
  String jsonFile;

  @Override
  public void run() {
    if (providerName == null) {
      providerName = Const.NANOSAT_MO_SUPERVISOR_NAME;
    }
    CommandsImplementations.dumpFormattedArchive(centralDirectoryURI, providerName, domain, comType,
        startTime, endTime, jsonFile);
  }
}


@Command(sortOptions = false, name = "get_logs",
    description = "Dumps to a LOG file the logs of an NMF app using the content of a COM archive provider")
class GetLogs implements Runnable {
  @Option(order = 1, names = {"-h", "--help"}, usageHelp = true,
      description = "display a help message")
  boolean helpRequested;

  @Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
      description = "URI of the central directory to use")
  String centralDirectoryURI;

  @Parameters(arity = "1", paramLabel = "<appName>",
      description = "Name of the NMF app we want the logs for")
  String appName;

  @Option(order = 2, names = {"-p", "--provider"}, paramLabel = "<providerName>",
      description = "Name of the COM archive provider to query\n" + "  - default: "
          + Const.NANOSAT_MO_SUPERVISOR_NAME)
  String providerName;

  @Option(order = 3, names = {"-d", "--domain"}, paramLabel = "<domainId>",
      description = "Domain of the NMF app we want the logs for\n"
          + "  - default: search for app in all domains\n" + "  - format: key1.key2.[...].keyN.\n"
          + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
  String domain;

  @Option(order = 4, names = {"-s", "--start"}, paramLabel = "<startTime>",
      description = "Restricts the dump to logs logged after the given time\n"
          + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
          + "  - example: \"2021-03-04 08:37:58.482\"")
  String startTime;

  @Option(order = 5, names = {"-e", "--end"}, paramLabel = "<endTime>",
      description = "Restricts the dump to logs logged before the given time. "
          + "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n"
          + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
          + "  - example: \"2021-03-05 12:05:45.271\"")
  String endTime;

  @Parameters(arity = "1", paramLabel = "<logFile>", description = "target LOG file")
  String logFile;

  @Override
  public void run() {
    if (providerName == null) {
      providerName = Const.NANOSAT_MO_SUPERVISOR_NAME;
    }
    CommandsImplementations.getLogs(centralDirectoryURI, appName, providerName, domain, startTime,
        endTime, logFile);
  }
}


@Command(name = "list",
    description = "Lists the COM archive providers names found in a central directory")
class ListArchiveProviders implements Runnable {
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  boolean helpRequested;

  @Parameters(arity = "1", paramLabel = "<centralDirectoryURI>",
      description = "URI of the central directory to use")
  String centralDirectoryURI;

  @Override
  public void run() {
    CommandsImplementations.listArchiveProviders(centralDirectoryURI);
  }
}
