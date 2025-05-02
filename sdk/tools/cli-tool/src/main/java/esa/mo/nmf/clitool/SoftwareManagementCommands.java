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
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;
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
    static Logger LOGGER = Logger.getLogger(SoftwareManagementCommands.class.getName());

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
                return;
            }

            if (consumer.getSMServices().getHeartbeatService() == null) {
                System.out.println("Heartbeat service is not available for this provider!");
                return;
            }

            try {
                HeartbeatStub heartbeat = consumer.getSMServices().getHeartbeatService().getHeartbeatStub();

                Identifier subscriptionId = new Identifier("CLI-Consumer-HeartbeatSubscription");
                Subscription subscription = new Subscription(subscriptionId, null, null, null);
                heartbeatSubscription = subscriptionId;

                heartbeat.beatRegister(subscription, new HeartbeatAdapter() {
                    @Override
                    public void beatNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
                        UpdateHeader updateHeader, Map qosProperties) {
                        //long timestamp = updateHeader.getTimestamp().getValue();
                        System.out.println("Heartbeat received");
                    }
                });

                final Object lock = new Object();

                synchronized (lock) {
                    lock.wait();
                }
            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during heartbeat register!", e);
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
                return;
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Apps launcher service is not available for this provider!");
                return;
            }

            final Object lock = new Object();

            try {
                AppsLauncherStub appsLauncher = consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub();

                Identifier subscriptionId = new Identifier("CLI-Consumer-AppsLauncherSubscription");
                Subscription subscription;
                if (appNames == null || appNames.isEmpty()) {
                    subscription = new Subscription(subscriptionId, null, null, null);
                } else {
                    ArchiveStub archive = consumer.getCOMServices().getArchiveService().getArchiveStub();
                    Map<String, ProviderAppDetails> providerNameToDetails = getProvidersDetails(archive);

                    SubscriptionFilterList filters = new SubscriptionFilterList();
                    //EntityKeyList entityKeys = new EntityKeyList();
                    for (String app : appNames) {
                        Long id = providerNameToDetails.get(app).id;
                        if (id != null) {
                            filters.add(new SubscriptionFilter(new Identifier("app.name"), new AttributeList(app)));
                            //EntityKey entitykey = new EntityKey(new Identifier(app), id, 0L, 0L);
                            //entityKeys.add(entitykey);
                        } else {
                            System.out.println("Provider " + app + " not found!");
                        }
                    }

                    if (filters.isEmpty()) {
                        System.out.println("Could not find any providers matching provided names!");
                        System.out.println("Available providers:");
                        for (Map.Entry<String, ProviderAppDetails> entry : providerNameToDetails.entrySet()) {
                            System.out.println(entry.getKey() + " - Running: " + entry.getValue().appDetails
                                .getRunning());
                        }
                        return;
                    }

                    //EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);
                    //EntityRequestList entities = new EntityRequestList();
                    //entities.add(entity);
                    //subscription = new Subscription(subscriptionId, entities);
                    subscription = new Subscription(subscriptionId, null, null, filters);
                }
                outputSubscription = subscriptionId;

                appsLauncher.monitorExecutionRegister(subscription, new AppsLauncherAdapter() {
                    @Override
                    public void monitorExecutionNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
                        UpdateHeader updateHeader, String strings, Map qosProperties) {
                        String providerName = updateHeader.getKeyValues().get(0).getValue().toString();
                        String[] lines = strings.split("\n");
                        for (String line : lines) {
                            System.out.println("[" + providerName + "]: " + line);
                        }
                    }
                });

                synchronized (lock) {
                    lock.wait();
                }
            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during heartbeat register!", e);
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
                return;
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Apps launcher service is not available for this provider!");
                return;
            }

            try {
                AppsLauncherStub appsLauncher = consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub();

                ArchiveStub archive = consumer.getCOMServices().getArchiveService().getArchiveStub();
                Map<String, ProviderAppDetails> providerNameToDetails = getProvidersDetails(archive);
                if (!checkProvider(providerNameToDetails, appName)) {
                    return;
                }

                LongList ids = new LongList();
                ids.add(providerNameToDetails.get(appName).id);
                appsLauncher.runApp(ids);

            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during runApp!", e);
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
                return;
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Apps launcher service is not available for this provider!");
                return;
            }

            try {
                AppsLauncherStub appsLauncher = consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub();

                ArchiveStub archive = consumer.getCOMServices().getArchiveService().getArchiveStub();
                Map<String, ProviderAppDetails> providerNameToDetails = getProvidersDetails(archive);

                if (!checkProvider(providerNameToDetails, appName)) {
                    return;
                }

                final Object lock = new Object();

                LongList ids = new LongList();
                ids.add(providerNameToDetails.get(appName).id);
                appsLauncher.stopApp(ids, new AppsLauncherAdapter() {
                    @Override
                    public void stopAppUpdateReceived(MALMessageHeader msgHeader, Long appClosing, Map qosProperties) {
                        for (ProviderAppDetails details : providerNameToDetails.values()) {
                            if (appClosing.equals(details.id)) {
                                System.out.println(details.appDetails.getName() + " closing in progress...");
                                break;
                            }
                        }
                    }

                    @Override
                    public void stopAppResponseReceived(MALMessageHeader msgHeader, Map qosProperties) {
                        System.out.println("App closed!");

                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void stopAppUpdateErrorReceived(MALMessageHeader msgHeader, MOErrorException error,
                        Map qosProperties) {
                        LOGGER.log(Level.SEVERE, "Error during stopApp!", error);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void stopAppResponseErrorReceived(MALMessageHeader msgHeader, MOErrorException error,
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
                return;
            }

            if (consumer.getSMServices().getAppsLauncherService() == null) {
                System.out.println("Apps launcher service is not available for this provider!");
                return;
            }

            try {
                AppsLauncherStub appsLauncher = consumer.getSMServices().getAppsLauncherService().getAppsLauncherStub();

                ArchiveStub archive = consumer.getCOMServices().getArchiveService().getArchiveStub();
                Map<String, ProviderAppDetails> providerNameToDetails = getProvidersDetails(archive);

                if (!checkProvider(providerNameToDetails, appName)) {
                    return;
                }

                LongList ids = new LongList();
                ids.add(providerNameToDetails.get(appName).id);
                appsLauncher.killApp(ids);
            } catch (MALInteractionException | MALException | InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Error during killApp!", e);
            }
        }
    }

    private static boolean checkProvider(Map<String, ProviderAppDetails> providers, String provider) {
        if (providers.containsKey(provider)) {
            return true;
        }

        System.out.println("Could not find any apps matching provided name!");
        System.out.println("Available apps:");
        for (Map.Entry<String, ProviderAppDetails> entry : providers.entrySet()) {
            System.out.println(entry.getKey() + " - Running: " + entry.getValue().appDetails.getRunning());
        }
        return false;
    }

    private static Map<String, ProviderAppDetails> getProvidersDetails(ArchiveStub archive)
        throws MALInteractionException, MALException, InterruptedException {
        final Object lock = new Object();

        ArchiveQueryList queries = new ArchiveQueryList();
        queries.add(new ArchiveQuery(BaseCommand.domain, null, null,
                0L, null, null, null, null, null));

        Map<String, ProviderAppDetails> result = new HashMap<>();
        ObjectType appType = new ObjectType(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NUMBER,
            AppsLauncherServiceInfo.APPSLAUNCHER_SERVICE_NUMBER, new UOctet((short) 0),
            AppsLauncherServiceInfo.APP_OBJECT_NUMBER);
        archive.query(true, appType, queries, null, new ArchiveAdapter() {
            @Override
            public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
                for (int i = 0; i < objDetails.size(); ++i) {
                    AppDetails details = (AppDetails) objBodies.get(i);
                    result.put(details.getName().getValue(), new ProviderAppDetails(objDetails.get(i).getInstId(),
                        details));
                }
            }

            @Override
            public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                ArchiveDetailsList objDetails, HeterogeneousList objBodies, Map qosProperties) {
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
            public void queryUpdateErrorReceived(MALMessageHeader msgHeader,
                    MOErrorException error, Map qosProperties) {
                LOGGER.log(Level.SEVERE, "Error during archive query!", error);
                synchronized (lock) {
                    lock.notifyAll();
                }
            }

            @Override
            public void queryResponseErrorReceived(MALMessageHeader msgHeader,
                    MOErrorException error, Map qosProperties) {
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
