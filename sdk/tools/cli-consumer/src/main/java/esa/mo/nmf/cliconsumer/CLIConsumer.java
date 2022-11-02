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
package esa.mo.nmf.cliconsumer;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * @author marcel.mikolajko
 */
@Command(name = "esa.mo.nmf.cliconsumer.CLIConsumer",
         subcommands = {MCCommands.Parameter.class,
                        MCCommands.Aggregation.class,
                        SoftwareManagementCommands.AppsLauncher.class,
                        SoftwareManagementCommands.Heartbeat.class,
                        PlatformCommands.GPS.class,
                        PlatformCommands.ADCS.class,
                        PlatformCommands.Camera.class},
         description = "Provides a way to use provider's services from the command line.")
public class CLIConsumer
{
    /*
        PubSub:
          - event - monitorEvent
          - statistic - monitorStatistics
          - camera - picturesStream
          - gps - nearbyPosition
          - adcs - monitorAttitude
          - Radio - streamRadio
          - clock - monitorTimeFactor
          -x parameter - monitorValue
          -x aggregation - monitorValue
          -x heartbeat - beat
          -x appslauncher - monitorExecution
     */
    public static final String APP_NAME = "cli-consumer";

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
    private boolean helpRequested;

    public static void main(String[] args)
    {
        Runtime.getRuntime().addShutdownHook(new Thread(BaseCommand::closeConsumer));
        CLIConsumer cliConsumer = new CLIConsumer();
        CommandLine cmd = new CommandLine(cliConsumer);
        cmd.setUsageHelpAutoWidth(true);
        cmd.setUsageHelpLongOptionsMaxWidth(30);

        System.exit(cmd.execute(args));
    }
}
//------------------------------------------------------------------------------