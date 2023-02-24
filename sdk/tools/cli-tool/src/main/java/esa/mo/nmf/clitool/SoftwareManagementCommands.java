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

import esa.mo.helpertools.connections.ConnectionConsumer;
import static esa.mo.nmf.clitool.BaseCommand.consumer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.body.ListAppResponse;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.consumer.AppsLauncherStub;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.structures.AppDetails;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatAdapter;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatStub;
import picocli.CommandLine.*;

/**
 * @author marcel.mikolajko
 */
public class SoftwareManagementCommands {

    private static final Logger LOGGER = Logger.getLogger(SoftwareManagementCommands.class.getName());

    public static Identifier heartbeatSubscription;
    public static Identifier outputSubscription;

    @Command(name = "apps-launcher", subcommands = {MonitorExecution.class, RunApp.class, StopApp.class, KillApp.class})
    public static class AppsLauncher {
    }

    @Command(name = "heartbeat", subcommands = {Beat.class})
    public static class Heartbeat {
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

    @Command(name = "subscribe", description = "Subscribes to app's stdout")
    public static class MonitorExecution extends BaseCommand implements Runnable {

        @Parameters(arity = "0..*", paramLabel = "<appNames>", index = "0",
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

    @Command(name = "run", description = "Runs the specified provider app")
    public static class RunApp extends BaseCommand implements Runnable {

        @Parameters(arity = "1", paramLabel = "<appName>", index = "0", description = "Name of the app to run.")
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
                // ArchiveStub archive = consumer.getCOMServices().getArchiveService().getArchiveStub();
                // Map<String, ProviderAppDetails> providerNameToDetails = getProvidersDetails(archive);
                IdentifierList appsToSearch = new IdentifierList();
                appsToSearch.add(new Identifier(appName));
                ListAppResponse response = appsLauncher.listApp(appsToSearch, null);
                LongList appIds = response.getBodyElement0();

                if (!checkProvider(appIds)) {
                    System.exit(ExitCodes.GENERIC_ERROR);
                }

                appsLauncher.runApp(appIds);
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during runApp!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    @Command(name = "stop", description = "Stops the specified provider app")
    public static class StopApp extends BaseCommand implements Runnable {

        @Parameters(arity = "1", paramLabel = "<appName>", index = "0", description = "Name of the app to stop.")
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

                if (!checkProvider(appIds)) {
                    System.exit(ExitCodes.GENERIC_ERROR);
                }

                final Object lock = new Object();

                appsLauncher.stopApp(appIds, new AppsLauncherAdapter() {
                    @Override
                    public void stopAppUpdateReceived(MALMessageHeader msgHeader, Long appClosing, Map qosProperties) {
                        System.out.println("Closing App with id: " + appClosing);
                    }

                    @Override
                    public void stopAppResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
                        System.out.println("App closed!");

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

    @Command(name = "kill", description = "Kills the specified provider app")
    public static class KillApp extends BaseCommand implements Runnable {

        @Parameters(arity = "1", paramLabel = "<appName>", index = "0", description = "Name of the app to kill.")
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

                if (!checkProvider(appIds)) {
                    System.exit(ExitCodes.GENERIC_ERROR);
                }

                appsLauncher.killApp(appIds);
            } catch (MALInteractionException | MALException e) {
                LOGGER.log(Level.SEVERE, "Error during killApp!", e);
                System.exit(ExitCodes.GENERIC_ERROR);
            }
        }
    }

    private static boolean checkProvider(LongList matchingApps) {
        if (matchingApps.size() != 1) {
            System.out.println("Could not find any apps matching provided name!");

            ArchiveStub archive = consumer.getCOMServices().getArchiveService().getArchiveStub();
            try {
                Map<String, ProviderAppDetails> providers = getProvidersDetails(archive);
                System.out.println("Available apps:");
                for (Map.Entry<String, ProviderAppDetails> entry : providers.entrySet()) {
                    System.out.println(entry.getKey() + " - Running: " + entry.getValue().appDetails.getRunning());
                }
            } catch (MALInteractionException | MALException | InterruptedException ex) {
                LOGGER.log(Level.SEVERE, "Error while retrieving the available Apps!", ex);
            }

            return false;
        }

        return true;
    }

    public static AppsLauncherStub getAppsLauncher() {
        return consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub();
    }

    private static Map<String, ProviderAppDetails> getProvidersDetails(ArchiveStub archive)
            throws MALInteractionException, MALException, InterruptedException {
        final Object lock = new Object();

        ArchiveQueryList queries = new ArchiveQueryList();
        queries.add(new ArchiveQuery(BaseCommand.domain, null, null, 0L, null, null, null, null, null));

        Map<String, ProviderAppDetails> result = new HashMap<>();
        ObjectType appType = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
                AppsLauncherHelper.APPSLAUNCHER_SERVICE_NUMBER, new UOctet((short) 0),
                AppsLauncherHelper.APP_OBJECT_NUMBER);
        archive.query(true, appType, queries, null, new ArchiveAdapter() {
            @Override
            public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                    ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
                for (int i = 0; i < objDetails.size(); ++i) {
                    AppDetails details = (AppDetails) objBodies.get(i);
                    result.put(details.getName().getValue(), new ProviderAppDetails(objDetails.get(i).getInstId(),
                            details));
                }
            }

            @Override
            public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                    ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
                if (objDetails != null) {
                    for (int i = 0; i < objDetails.size(); ++i) {
                        AppDetails details = (AppDetails) objBodies.get(i);
                        result.put(details.getName().getValue(), new ProviderAppDetails(objDetails.get(i).getInstId(),
                                details));
                    }
                }

                synchronized (lock) {
                    lock.notifyAll();
                }
            }

            @Override
            public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                    Map qosProperties) {
                LOGGER.log(Level.SEVERE, "Error during archive query!", error);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }

            @Override
            public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
                    Map qosProperties) {
                LOGGER.log(Level.SEVERE, "Error during archive query!", error);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }
        });

        synchronized (lock) {
            lock.wait();
        }

        return result;
    }

    private static class ProviderAppDetails {

        Long id;
        AppDetails appDetails;

        public ProviderAppDetails(Long id, AppDetails appDetails) {
            this.appDetails = appDetails;
            this.id = id;
        }
    }
}
