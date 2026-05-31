/* ----------------------------------------------------------------------------
 * Copyright (C) 2023      European Space Agency
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

import static esa.mo.nmf.clitool.BaseCommand.consumer;
import static esa.mo.nmf.clitool.sm.SoftwareManagementCommands.outputSubscription;
import esa.mo.nmf.clitool.Args;
import esa.mo.nmf.clitool.BaseCommand;
import esa.mo.nmf.clitool.ExitCodes;
import esa.mo.nmf.clitool.Helper;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherStub;

/**
 * The AppsLauncherCommands class contains the static classes for the Apps
 * Launcher service.
 *
 * @author Cesar Coelho
 */
public class AppsLauncherCommands {

    private static final Logger LOGGER = Logger.getLogger(AppsLauncherCommands.class.getName());

    public static class MonitorExecution extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> appNames = args.positionals();

            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Apps launcher service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                AppsLauncherStub appsLauncher = getAppsLauncher();
                Long timestamp = System.currentTimeMillis();
                Identifier subscriptionId = new Identifier("CLI-Consumer-AppsLauncherSubscription_" + timestamp);
                Subscription subscription = new Subscription(subscriptionId, null, null, null);

                if (!appNames.isEmpty()) {
                    AttributeList acceptableNames = new AttributeList();
                    for (String app : appNames) {
                        acceptableNames.add(new Identifier(app));
                    }
                    SubscriptionFilterList filters = new SubscriptionFilterList();
                    filters.add(new SubscriptionFilter(new Identifier("appName"), acceptableNames));
                    subscription = new Subscription(subscriptionId, null, null, filters);
                }
                outputSubscription = subscriptionId;

                appsLauncher.monitorExecutionRegister(subscription, new AppsLauncherAdapter() {
                    @Override
                    public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader,
                            org.ccsds.moims.mo.mal.structures.Identifier subscriptionId,
                            org.ccsds.moims.mo.mal.structures.UpdateHeader updateHeader,
                            String outputStream,
                            java.util.Map qosProperties) {
                        String providerName = ((Identifier) updateHeader.getKeyValues().get(0).getValue()).getValue();
                        String[] lines = outputStream.split("\n");
                        for (String line : lines) {
                            System.out.println("[" + providerName + "]: " + line);
                        }
                    }
                });

                final Object lock = new Object();

                synchronized (lock) {
                    lock.wait();
                }
            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during monitorExecution register!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    public static class RunApp extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <appName>");
                return;
            }
            String appName = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Apps launcher service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                AppsLauncherStub appsLauncher = getAppsLauncher();
                IdentifierList appsToSearch = new IdentifierList();
                appsToSearch.add(new Identifier(appName));
                ListAppResponse response = appsLauncher.listApp(appsToSearch, null);
                LongList appIds = response.getAppIds();

                if (!Helper.checkProvider(appIds)) {
                    System.exit(ExitCodes.GENERIC_ERROR);
                }

                appsLauncher.runApp(appIds);
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during runApp!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    public static class StopApp extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <appName>");
                return;
            }
            String appName = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Apps launcher service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                AppsLauncherStub appsLauncher = getAppsLauncher();
                IdentifierList appsToSearch = new IdentifierList();
                appsToSearch.add(new Identifier(appName));
                ListAppResponse response = appsLauncher.listApp(appsToSearch, null);
                LongList appIds = response.getAppIds();

                if (!Helper.checkProvider(appIds)) {
                    System.exit(ExitCodes.GENERIC_ERROR);
                }

                final Object lock = new Object();

                appsLauncher.stopApp(appIds, new AppsLauncherAdapter() {
                    @Override
                    public void stopAppUpdateReceived(MALMessageHeader msgHeader,
                            Long appClosing, Map qosProperties) {
                        System.out.println("Stopping App with id: " + appClosing);
                    }

                    @Override
                    public void stopAppResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
                        System.out.println("App stopped!");

                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void stopAppUpdateErrorReceived(MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE, "Error during stopApp!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void stopAppResponseErrorReceived(MALMessageHeader msgHeader,
                            MOErrorException error, Map qosProperties) {
                        LOGGER.log(Level.SEVERE, "Error during stopApp!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock) {
                    lock.wait(4_000);
                }

            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during stopApp!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    public static class KillApp extends BaseCommand {

        @Override
        public void run(Args args) {
            parseBaseOptions(args);
            List<String> positionals = args.positionals();
            if (positionals.isEmpty()) {
                System.out.println("Missing required argument: <appName>");
                return;
            }
            String appName = positionals.get(0);

            if (!super.initRemoteConsumer()) {
                System.exit(ExitCodes.NO_HOST);
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Apps launcher service is not available for this provider!");
                System.exit(ExitCodes.UNAVAILABLE);
            }

            try {
                AppsLauncherStub appsLauncher = getAppsLauncher();
                IdentifierList appsToSearch = new IdentifierList();
                appsToSearch.add(new Identifier(appName));
                ListAppResponse response = appsLauncher.listApp(appsToSearch, null);
                LongList appIds = response.getAppIds();

                if (!Helper.checkProvider(appIds)) {
                    System.exit(ExitCodes.GENERIC_ERROR);
                }

                appsLauncher.killApp(appIds);
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during killApp!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    public static AppsLauncherStub getAppsLauncher() {
        return consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub();
    }
}
