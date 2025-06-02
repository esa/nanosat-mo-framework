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
package esa.mo.nmf.clitool;

import esa.mo.nmf.clitool.mc.MCCommands;
import esa.mo.nmf.clitool.platform.PlatformCommands;
import esa.mo.nmf.clitool.sm.SoftwareManagementCommands;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * @author marcel.mikolajko
 */
@Command(name = "esa.mo.nmf.clitool.CLITool",
        subcommands = {
            MCCommands.Aggregation.class,
            MCCommands.Parameter.class,
            MCCommands.Action.class,
            SoftwareManagementCommands.AppsLauncher.class,
            SoftwareManagementCommands.SoftwareManagement.class,
            SoftwareManagementCommands.Heartbeat.class,
            PlatformCommands.GPS.class,
            PlatformCommands.ADCS.class,
            PlatformCommands.Camera.class,
            ArchiveCommands.class,
            LogsCommands.class},
        description = "Provides a way to use provider's services from the command line.")
public class CLITool {

    public static final String APP_NAME = "cli-consumer";

    @Option(names = {"-h", "--help"}, usageHelp = true, description = "display a help message")
    private boolean helpRequested;

    public static void main(String[] args) {
        // The startup time can be improved with the property below,
        // according to section "Picocli 4.7.0" in the Release Notes.
        // Link: https://github.com/MadFoal/picocli/blob/master/RELEASE-NOTES.md
        System.setProperty("picocli.disable.closures", "true");
        // The property above, does not seem to improve the startup time...

        long startTime = System.currentTimeMillis();
        System.out.println("(1) The CLI Tool is starting...");
        Runtime.getRuntime().addShutdownHook(new Thread(BaseCommand::closeConsumer));
        System.out.println("(2) The CLI Tool is starting...");
        CLITool cliConsumer = new CLITool();
        System.out.println("(3) The CLI Tool is starting...");
        CommandLine cmd = new CommandLine(cliConsumer);
        System.out.println("(4) The CLI Tool is starting...");
        cmd.setUsageHelpAutoWidth(true);
        cmd.setUsageHelpLongOptionsMaxWidth(30);
        long startupTime = System.currentTimeMillis() - startTime;
        System.out.println("(5) The CLI Tool was started in: " + startupTime + " ms");

        System.exit(cmd.execute(args));
    }
}
