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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.clitool.BaseCommand;
import static esa.mo.nmf.clitool.BaseCommand.consumer;
import esa.mo.nmf.clitool.ExitCodes;
import esa.mo.nmf.clitool.Helper;
import static esa.mo.nmf.clitool.sm.SoftwareManagementCommands.outputSubscription;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherStub;
import picocli.CommandLine;

/**
 * The AppsLauncherCommands class contains the static classes for the Apps
 * Launcher service.
 *
 * @author Cesar Coelho
 */
public class AppsLauncherCommands {

    private static final Logger LOGGER = Logger.getLogger(AppsLauncherCommands.class.getName());

    @CommandLine.Command(name = "subscribe", description = "Subscribes to app's stdout")
    public static class MonitorExecution extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "0..*", paramLabel = "<appNames>", index = "0",
                description = "Names of the apps to subscribe to. If non are specified subscribe to all.")
        List<String> appNames;

        @Override
        public void run() {
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
                Subscription subscription = ConnectionConsumer.subscriptionWildcard(subscriptionId);
                EntityKeyList entityKeys = new EntityKeyList();

                if (appNames != null && !appNames.isEmpty()) {
                    for (String app : appNames) {
                        IdentifierList appsToSearch = new IdentifierList();
                        appsToSearch.add(new Identifier(app));
                        ListAppResponse response = appsLauncher.listApp(appsToSearch, null);
                        Long id = response.getBodyElement0().get(0);

                        if (id != null) {
                            EntityKey entitykey = new EntityKey(new Identifier(app), id, 0L, 0L);
                            entityKeys.add(entitykey);
                        } else {
                            System.out.println("Provider " + app + " not found!");
                        }
                    }

                    if (entityKeys.isEmpty()) {
                        System.out.println("Could not find any providers matching provided names!");
                        System.exit(ExitCodes.GENERIC_ERROR);
                    }

                    EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);
                    EntityRequestList entities = new EntityRequestList();
                    entities.add(entity);

                    subscription = new Subscription(subscriptionId, entities);
                }
                outputSubscription = subscriptionId;

                appsLauncher.monitorExecutionRegister(subscription, new AppsLauncherAdapter() {
                    @Override
                    public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
                            UpdateHeaderList updateHeaderList, StringList strings, Map qosProperties) {
                        for (int i = 0; i < updateHeaderList.size(); i++) {
                            String providerName = updateHeaderList.get(i).getKey().getFirstSubKey().getValue();
                            String[] lines = strings.get(i).split("\n");
                            for (String line : lines) {
                                System.out.println("[" + providerName + "]: " + line);
                            }
                        }
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

    @CommandLine.Command(name = "run", description = "Runs the specified provider app")
    public static class RunApp extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<appName>", index = "0", description = "Name of the app to run.")
        String appName;

        @Override
        public void run() {
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
                LongList appIds = response.getBodyElement0();

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

    @CommandLine.Command(name = "stop", description = "Stops the specified provider app")
    public static class StopApp extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<appName>", index = "0", description = "Name of the app to stop.")
        String appName;

        @Override
        public void run() {
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
                LongList appIds = response.getBodyElement0();

                if (!Helper.checkProvider(appIds)) {
                    System.exit(ExitCodes.GENERIC_ERROR);
                }

                final Object lock = new Object();

                appsLauncher.stopApp(appIds, new AppsLauncherAdapter() {
                    @Override
                    public void stopAppUpdateReceived(MALMessageHeader msgHeader, Long appClosing, Map qosProperties) {
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
                    public void stopAppUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                            Map qosProperties) {
                        LOGGER.log(Level.SEVERE, "Error during stopApp!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void stopAppResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                            Map qosProperties) {
                        LOGGER.log(Level.SEVERE, "Error during stopApp!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }
                });

                synchronized (lock) {
                    lock.wait();
                }

            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during stopApp!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    @CommandLine.Command(name = "kill", description = "Kills the specified provider app")
    public static class KillApp extends BaseCommand implements Runnable {

        @CommandLine.Parameters(arity = "1", paramLabel = "<appName>", index = "0", description = "Name of the app to kill.")
        String appName;

        @Override
        public void run() {
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
                LongList appIds = response.getBodyElement0();

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
