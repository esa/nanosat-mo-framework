/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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

import esa.mo.nmf.clitool.mc.ActionCommands;
import esa.mo.nmf.clitool.mc.AggregationCommands;
import esa.mo.nmf.clitool.mc.ParameterCommands;
import esa.mo.nmf.clitool.platform.PlatformCommands;
import esa.mo.nmf.clitool.sm.AppsLauncherCommands;
import esa.mo.nmf.clitool.sm.PackageManagementCommands;
import esa.mo.nmf.clitool.sm.SoftwareManagementCommands;

/**
 * Routes command-line arguments to the appropriate command implementation.
 *
 * <p>
 * The first token is the group name and the second is the subcommand name.
 * All remaining tokens are forwarded to the command's {@link BaseCommand#run(Args)}
 * method as an {@link Args} instance.
 */
public class Dispatcher {

    private static final String HELP =
            "Usage: cli-tool <group> <command> [options...]\n"
            + "\n"
            + "Groups and commands:\n"
            + "  parameter      subscribe | enable | disable | get | list | set\n"
            + "  aggregation    subscribe | enable | disable\n"
            + "  action         trigger | list\n"
            + "  apps-launcher  subscribe | run | stop | kill\n"
            + "  software-management  findPackage | install | uninstall | upgrade\n"
            + "  heartbeat      subscribe\n"
            + "  gps            get-nmea-sentence\n"
            + "  adcs           get-status\n"
            + "  camera         take-picture\n"
            + "  archive        dump_raw | dump | list | backup_and_clean\n"
            + "  log            list | get\n"
            + "\n"
            + "Global options (accepted by every command):\n"
            + "  -r, --remote <uri>      Provider directory URI\n"
            + "  -l, --local <file>      Local SQLite database file\n"
            + "  -p, --provider <name>   Provider name (when directory has multiple)\n";

    public static int dispatch(String[] rawArgs) {
        if (rawArgs.length == 0 || isHelp(rawArgs[0])) {
            System.out.print(HELP);
            return 0;
        }

        String group = rawArgs[0];
        if (rawArgs.length < 2) {
            System.out.println("Missing subcommand for group '" + group + "'.");
            System.out.print(HELP);
            return 1;
        }

        String sub = rawArgs[1];
        // Tokens after group + subcommand are passed to the command
        String[] rest = new String[rawArgs.length - 2];
        System.arraycopy(rawArgs, 2, rest, 0, rest.length);
        Args args = new Args(rest);

        BaseCommand cmd = resolve(group, sub);
        if (cmd == null) {
            System.out.println("Unknown command: " + group + " " + sub);
            System.out.print(HELP);
            return 1;
        }

        cmd.run(args);
        return 0;
    }

    private static boolean isHelp(String token) {
        return "-h".equals(token) || "--help".equals(token);
    }

    private static BaseCommand resolve(String group, String sub) {
        switch (group) {
            case "parameter":
                switch (sub) {
                    case "subscribe": return new ParameterCommands.ParameterMonitorValue();
                    case "enable":    return new ParameterCommands.ParameterEnableGeneration();
                    case "disable":   return new ParameterCommands.ParameterDisableGeneration();
                    case "get":       return new ParameterCommands.GetParameters();
                    case "list":      return new ParameterCommands.ListParameters();
                    case "set":       return new ParameterCommands.SetParameter();
                }
                break;
            case "aggregation":
                switch (sub) {
                    case "subscribe": return new AggregationCommands.AggregationMonitorValue();
                    case "enable":    return new AggregationCommands.AggregationEnableGeneration();
                    case "disable":   return new AggregationCommands.AggregationDisableGeneration();
                }
                break;
            case "action":
                switch (sub) {
                    case "trigger": return new ActionCommands.SubmitAction();
                    case "list":    return new ActionCommands.ListActions();
                }
                break;
            case "apps-launcher":
                switch (sub) {
                    case "subscribe": return new AppsLauncherCommands.MonitorExecution();
                    case "run":       return new AppsLauncherCommands.RunApp();
                    case "stop":      return new AppsLauncherCommands.StopApp();
                    case "kill":      return new AppsLauncherCommands.KillApp();
                }
                break;
            case "software-management":
                switch (sub) {
                    case "findPackage": return new PackageManagementCommands.FindPackage();
                    case "install":     return new PackageManagementCommands.Install();
                    case "uninstall":   return new PackageManagementCommands.Uninstall();
                    case "upgrade":     return new PackageManagementCommands.Upgrade();
                }
                break;
            case "heartbeat":
                if ("subscribe".equals(sub)) {
                    return new SoftwareManagementCommands.Beat();
                }
                break;
            case "gps":
                if ("get-nmea-sentence".equals(sub)) {
                    return new PlatformCommands.GetNMEASentence();
                }
                break;
            case "adcs":
                if ("get-status".equals(sub)) {
                    return new PlatformCommands.GetStatus();
                }
                break;
            case "camera":
                if ("take-picture".equals(sub)) {
                    return new PlatformCommands.TakePicture();
                }
                break;
            case "archive":
                switch (sub) {
                    case "dump_raw":        return new ArchiveCommands.DumpRawArchive();
                    case "dump":            return new ArchiveCommands.DumpFormattedArchive();
                    case "list":            return new ArchiveCommands.ListArchiveProviders();
                    case "backup_and_clean": return new ArchiveCommands.BackupProvider();
                }
                break;
            case "log":
                switch (sub) {
                    case "list": return new LogsCommands.ListLogs();
                    case "get":  return new LogsCommands.GetLogs();
                }
                break;
        }
        return null;
    }
}
