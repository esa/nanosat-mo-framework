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
package esa.mo.nmf.clitool.sm;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.clitool.BaseCommand;
import esa.mo.nmf.clitool.ExitCodes;
import static esa.mo.nmf.clitool.BaseCommand.consumer;
import esa.mo.nmf.clitool.sm.AppsLauncherCommands.KillApp;
import esa.mo.nmf.clitool.sm.AppsLauncherCommands.MonitorExecution;
import esa.mo.nmf.clitool.sm.AppsLauncherCommands.RunApp;
import esa.mo.nmf.clitool.sm.AppsLauncherCommands.StopApp;
import esa.mo.nmf.clitool.sm.PackageManagementCommands.FindPackage;
import esa.mo.nmf.clitool.sm.PackageManagementCommands.Install;
import esa.mo.nmf.clitool.sm.PackageManagementCommands.Uninstall;
import esa.mo.nmf.clitool.sm.PackageManagementCommands.Upgrade;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatAdapter;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatStub;
import org.ccsds.moims.mo.softwaremanagement.packagemanagement.consumer.PackageManagementStub;
import picocli.CommandLine.Command;

/**
 * @author marcel.mikolajko
 */
public class SoftwareManagementCommands {

    private static final Logger LOGGER = Logger.getLogger(SoftwareManagementCommands.class.getName());

    public static Identifier heartbeatSubscription;
    public static Identifier outputSubscription;

    @Command(name = "software-management", subcommands = {FindPackage.class, Install.class, Uninstall.class, Upgrade.class})
    public static class SoftwareManagement {
    }

    @Command(name = "heartbeat", subcommands = {Beat.class})
    public static class Heartbeat {
    }

    @Command(name = "apps-launcher", subcommands = {MonitorExecution.class, RunApp.class, StopApp.class, KillApp.class})
    public static class AppsLauncher {
    }

    @Command(name = "subscribe", description = "Subscribes to provider's heartbeat")
    public static class Beat extends BaseCommand implements Runnable {

        @Override
        public void run() {
            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getHeartbeatService() == null) {
                System.out.println("Heartbeat service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                HeartbeatStub heartbeat = consumer.getSMServices().getHeartbeatService().getHeartbeatStub();

                Identifier subscriptionId = new Identifier("CLI-Consumer-HeartbeatSubscription");
                Subscription subscription = ConnectionConsumer.subscriptionWildcard(subscriptionId);
                heartbeatSubscription = subscriptionId;

                heartbeat.beatRegister(subscription, new HeartbeatAdapter() {
                    @Override
                    public void beatNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
                            UpdateHeaderList updateHeaderList, Map qosProperties) {
                        long timestamp = updateHeaderList.get(0).getTimestamp().getValue();
                        System.out.println("[" + timestamp + "] - Heartbeat received");
                    }
                });

                final Object lock = new Object();

                synchronized (lock) {
                    lock.wait();
                }
            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during heartbeat register!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    public static PackageManagementStub getPackageManagement() {
        return consumer.getSMServices().getPackageManagementService().getPackageManagementStub();
    }
}
