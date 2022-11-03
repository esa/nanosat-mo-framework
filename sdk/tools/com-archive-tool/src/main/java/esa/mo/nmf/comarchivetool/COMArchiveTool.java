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
package esa.mo.nmf.comarchivetool;

import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.comarchivetool.commands.archive.ArchiveCommandsDefinitions;
import esa.mo.nmf.comarchivetool.commands.logs.LogsCommandsDefinitions;
import esa.mo.nmf.comarchivetool.commands.parameters.ParametersCommandsDefinitions;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Main entry point to start the COMArchiveTool tool.
 *
 * @author Tanguy Soto
 * @author Marcel Mikołajko
 */
@Command(name = "COMArchiveTool",
         subcommands = {LogsCommandsDefinitions.Logs.class,
                        ParametersCommandsDefinitions.Parameter.class,
                        ArchiveCommandsDefinitions.DumpRawArchive.class,
                        ArchiveCommandsDefinitions.DumpFormattedArchive.class,
                        ArchiveCommandsDefinitions.ListArchiveProviders.class},
         description = "Browses a COM archive to retrieve it's contents.")
public class COMArchiveTool {

  public static final String APP_NAME = "com-archive-tool";

  @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
  private boolean helpRequested;

  /**
   * Main command line entry point.
   *
   * @param args the command line arguments
   */
  public static void main(final String[] args) {
    NMFConsumer.initHelpers();
    final COMArchiveTool browser = new COMArchiveTool();
    final CommandLine cmd = new CommandLine(browser);
    cmd.setUsageHelpAutoWidth(true);
    cmd.setUsageHelpLongOptionsMaxWidth(30);

    System.exit(cmd.execute(args));
  }
}
