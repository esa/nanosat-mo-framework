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

package esa.mo.nmf.log_browser;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Defines the CLI commands available.
 *
 * @author Tanguy Soto
 */

@Command(name = "COMArchiveBrowser",
    subcommands = {DumpRawArchive.class, DumpFormattedArchive.class, Logs.class,
        ListArchiveProviders.class},
    description = "Browses a COM archive to retrieve logs and more from it.")
public class CommandsDefinitions {

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  private boolean helpRequested;
}


@Command(name = "dump_raw",
    description = "Dumps to a JSON file the raw tables content of a local COM archive")
class DumpRawArchive implements Runnable {
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  boolean helpRequested;

  @Parameters(arity = "1", paramLabel = "<databaseFile>",
      description = "Local SQLite database file\n"
          + "  - example: ../nanosat-mo-supervisor-sim/comArchive.db")
  String databaseFile;

  @Parameters(arity = "1", paramLabel = "<jsonFile>", description = "target JSON file")
  String jsonFile;

  @Override
  public void run() {
    CommandsImplementations.dumpRawArchiveTables(databaseFile, jsonFile);
  }
}


@Command(name = "dump",
    description = "Dumps to a JSON file the formatted content of a local or remote COM archive")
class DumpFormattedArchive implements Runnable {
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  boolean helpRequested;

  @ArgGroup(exclusive = true, multiplicity = "1")
  LocalOrRemote localOrRemote;

  static class LocalOrRemote {
    @Option(names = {"-l", "--local"}, paramLabel = "<databaseFile>",
        description = "Local SQLite database file\n"
            + "  - example: ../nanosat-mo-supervisor-sim/comArchive.db")
    String databaseFile;

    @Option(names = {"-r", "--remote"}, paramLabel = "<providerURI>",
        description = "Remote COM archive provider URI\n"
            + "  - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Archive")
    String providerURI;
  }

  @Parameters(arity = "1", paramLabel = "<jsonFile>", description = "target JSON file")
  String jsonFile;

  @Option(names = {"-d", "--domain"}, paramLabel = "<domainId>",
      description = "Restricts the dump to objects in a specific domain\n"
          + "  - format: key1.key2.[...].keyN.\n"
          + "  - example: esa.NMF_SDK.nanosat-mo-supervisor")
  String domain;

  @Option(names = {"-t", "--type"}, paramLabel = "<comType>",
      description = "Restricts the dump to objects that are instances of <comType>\n"
          + "  - format: areaNumber.serviceNumber.areaVersion.objectNumber.\n"
          + "  - examples (0=wildcard): 4.2.1.1, 4.2.1.0 ")
  String comType;

  @Option(names = {"-s", "--start"}, paramLabel = "<startTime>",
      description = "Restricts the dump to objects created after the given time\n"
          + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
          + "  - example: \"2021-03-04 08:37:58.482\"")
  String startTime;

  @Option(names = {"-e", "--end"}, paramLabel = "<endTime>",
      description = "Restricts the dump to objects created before the given time. "
          + "If this option is provided without the -s option, returns the single object that has the closest timestamp to, but not greater than <endTime>\n"
          + "  - format: \"yyyy-MM-dd HH:mm:ss.SSS\"\n"
          + "  - example: \"2021-03-05 12:05:45.271\"")
  String endTime;

  @Override
  public void run() {
    CommandsImplementations.dumpFormattedArchive(localOrRemote.databaseFile,
        localOrRemote.providerURI, domain, comType, startTime, endTime, jsonFile);
  }
}


@Command(name = "log", subcommands = {ListLogs.class, GetLogs.class},
    description = "Gets or lists NMF app logs using the content of a local or remote COM archive.")
class Logs {
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  boolean helpRequested;
}


@Command(name = "list",
    description = "Lists NMF apps having logs in the content of a local or remote COM archive.")
class ListLogs implements Runnable {
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  boolean helpRequested;

  @ArgGroup(exclusive = true, multiplicity = "1")
  LocalOrRemote localOrRemote;

  static class LocalOrRemote {
    @Option(names = {"-l", "--local"}, paramLabel = "<databaseFile>",
        description = "Local SQLite database file\n"
            + "  - example: ../nanosat-mo-supervisor-sim/comArchive.db")
    String databaseFile;

    @Option(names = {"-r", "--remote"}, paramLabel = "<providerURI>",
        description = "Remote COM archive provider URI\n"
            + "  - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Archive")
    String providerURI;
  }

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
    CommandsImplementations.listLogs(localOrRemote.databaseFile, localOrRemote.providerURI, domain,
        startTime, endTime);
  }
}


@Command(name = "get",
    description = "Dumps to a LOG file an NMF app logs using the content of a local or remote COM archive.")
class GetLogs implements Runnable {
  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  boolean helpRequested;

  @ArgGroup(exclusive = true, multiplicity = "1")
  LocalOrRemote localOrRemote;

  static class LocalOrRemote {
    @Option(names = {"-l", "--local"}, paramLabel = "<databaseFile>",
        description = "Local SQLite database file\n"
            + "  - example: ../nanosat-mo-supervisor-sim/comArchive.db")
    String databaseFile;

    @Option(names = {"-r", "--remote"}, paramLabel = "<providerURI>",
        description = "Remote COM archive provider URI\n"
            + "  - example: maltcp://10.0.2.15:1024/nanosat-mo-supervisor-Archive")
    String providerURI;
  }

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

  @Override
  public void run() {
    CommandsImplementations.getLogs(localOrRemote.databaseFile, localOrRemote.providerURI, appName,
        domain, startTime, endTime, logFile);
  }
}


@Command(name = "list",
    description = "Lists the COM archive providers URIs found in a central directory")
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
