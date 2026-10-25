/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package esa.mo.nmf.security.probe;

import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.SimpleMonitorAndControlAdapter;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.structures.Provider;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.mal.AuthenticationFailedException;
import org.ccsds.moims.mo.mal.AuthorisationFailException;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALInteractionHandler;
import org.ccsds.moims.mo.mal.provider.MALInvoke;
import org.ccsds.moims.mo.mal.provider.MALProgress;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.provider.MALPublisher;
import org.ccsds.moims.mo.mal.provider.MALRequest;
import org.ccsds.moims.mo.mal.provider.MALSubmit;
import org.ccsds.moims.mo.mal.structures.AttributeList;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.sm.appslauncher.AppsLauncherServiceInfo;
import org.ccsds.moims.mo.sm.appslauncher.consumer.AppsLauncherAdapter;
import org.ccsds.moims.mo.sm.packagemanagement.PackageManagementServiceInfo;
import org.ccsds.moims.mo.sm.structures.AppEventType;
import org.ccsds.moims.mo.sm.structures.Command;

/**
 * A benign NMF App for the access control tests. It declares a single
 * permission, {@code Supervisor.MC.Parameter}, and then calls services of the
 * Supervisor:
 * <ul>
 * <li>the baseline that every App has without declaring it;</li>
 * <li>the one service it declares;</li>
 * <li>services it does not declare.</li>
 * </ul>
 * For each call it reports {@code ALLOWED} if the call reached the service,
 * even if the service answered with an ordinary error such as Unknown, and
 * {@code REFUSED} if it was refused for authorisation or authentication.
 * Every call only reads, except CommandExecutor, which runs {@code id}, and
 * the publishing of an Apps Launcher event, for an App that does not exist.
 */
public class PermissionsProbe {

    /** Where to write the report; overridable so the test can place it on a shared path. */
    public static final String REPORT_PROPERTY = "esa.mo.nmf.security.probe.permissionsReport";
    private static final String DEFAULT_REPORT = "/tmp/permissions-probe-report";

    /** The ground credential file; overridable for a filesystem in another place. */
    public static final String GROUND_CREDENTIAL_PROPERTY = "esa.mo.nmf.security.probe.groundCredential";
    private static final String DEFAULT_GROUND_CREDENTIAL = "/nanosat-mo-framework/bootloader/ground.credential";

    private static final long TIMEOUT_S = 10;

    /** The extra information of the event that the probe publishes, by which it recognises it. */
    private static final String PUBLISHED_EVENT_INFO = "permissions probe";

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private final StringBuilder report = new StringBuilder();

    /** A call made by the probe; it throws whatever the service answers. */
    private interface Attempt {
        void run() throws Exception;
    }

    /**
     * Starts as a normal App, with no parameters or actions of its own.
     */
    public PermissionsProbe() {
        connector.init(new SimpleMonitorAndControlAdapter() {
            @Override
            public void initialRegistrations(MCRegistration registration) {
                // The probe exposes no parameters or actions.
            }

            @Override
            public Serializable onGetValueSimple(String name) {
                return null;
            }

            @Override
            public boolean onSetValueSimple(String name, Serializable value) {
                return false;
            }

            @Override
            public boolean actionArrivedSimple(String name, Serializable[] values, Long executionId) {
                return false;
            }
        });
    }

    /**
     * Runs the probe and writes its report.
     *
     * @param args unused
     */
    public static void main(final String[] args) {
        PermissionsProbe probe = new PermissionsProbe();
        try {
            probe.probe();
        } catch (Throwable t) {
            probe.report.append("PROBE_ERROR=").append(t).append('\n');
        }
        probe.writeReport();
    }

    private void probe() throws Exception {
        URI directoryURI = connector.readCentralDirectoryServiceURI();
        if (directoryURI == null) {
            report.append("PROBE_ERROR=no central Directory\n");
            return;
        }

        // Baseline: looking up the Supervisor's services
        AtomicReference<ProviderList> found = new AtomicReference<>();
        attempt("Supervisor.COM.Directory", "lookup",
                () -> found.set(NMFConsumer.retrieveProvidersFromDirectory(directoryURI)));
        Provider supervisorProvider = null;
        if (found.get() != null) {
            for (Provider p : found.get()) {
                if (Const.NANOSAT_MO_SUPERVISOR_NAME.equals(p.getProviderName().getValue())) {
                    supervisorProvider = p;
                }
            }
        }
        if (supervisorProvider == null) {
            report.append("PROBE_ERROR=the Supervisor was not found in the Directory\n");
            return;
        }

        NMFConsumer supervisor = new NMFConsumer(supervisorProvider);
        supervisor.init();
        IdentifierList all = new IdentifierList();
        all.add(new Identifier("*"));

        // Baseline: the Heartbeat, and the AppsLauncher events that carry the shutdown signal
        attempt("Supervisor.SM.Heartbeat", "getPeriod", () -> supervisor.getSMServices()
                .getHeartbeatService().getHeartbeatStub().getPeriod());
        attempt("Supervisor.SM.AppsLauncher", "monitorEventsRegister", () -> {
            Subscription subscription = ConnectionConsumer.subscriptionWildcardRandom();
            supervisor.getSMServices().getAppsLauncherService().getAppsLauncherStub()
                    .monitorEventsRegister(subscription, new AppsLauncherAdapter() {
                    });
            IdentifierList ids = new IdentifierList();
            ids.add(subscription.getSubscriptionId());
            supervisor.getSMServices().getAppsLauncherService().getAppsLauncherStub()
                    .monitorEventsDeregister(ids);
        });

        // Subscribed to the Apps Launcher events before anything else, so that the report says
        // whether the event that the probe publishes below was delivered, whatever answer the
        // publisher gets. It ends with the probe; the later calls close shared connections.
        java.util.concurrent.atomic.AtomicBoolean delivered = new java.util.concurrent.atomic.AtomicBoolean();
        Subscription events = ConnectionConsumer.subscriptionWildcardRandom();
        boolean subscribed = false;
        try {
            supervisor.getSMServices().getAppsLauncherService().getAppsLauncherStub()
                    .monitorEventsRegister(events, new AppsLauncherAdapter() {
                        @Override
                        public void monitorEventsNotifyReceived(MALMessageHeader msgHeader,
                                Identifier subscriptionId, UpdateHeader updateHeader,
                                org.ccsds.moims.mo.sm.appslauncher.consumer.MonitorEventsSubscriptionKeys keys,
                                AppEventType eventType, Integer exitCode, String extraInfo, Map qosProperties) {
                            if (PUBLISHED_EVENT_INFO.equals(extraInfo)) {
                                delivered.set(true);
                            }
                        }
                    });
            subscribed = true;
        } catch (Exception e) {
            report.append("PROBE_ERROR=could not subscribe to the Apps Launcher events: ").append(e).append('\n');
        }
        // Declared: the one permission in the probe's pom.xml
        attempt("Supervisor.MC.Parameter", "listDefinition", () -> supervisor.getMCServices()
                .getParameterService().getParameterStub().listDefinition(all));

        // Not declared
        attempt("Supervisor.MC.Action", "listDefinition", () -> supervisor.getMCServices()
                .getActionService().getActionStub().listDefinition(all));
        attempt("Supervisor.MC.Aggregation", "listDefinition", () -> supervisor.getMCServices()
                .getAggregationService().getAggregationStub().listDefinition(all));
        attempt("Supervisor.SM.AppsLauncher", "listApp", () -> supervisor.getSMServices()
                .getAppsLauncherService().getAppsLauncherStub().listApp(all, new Identifier("*")));
        attempt("Supervisor.SM.PackageManagement", "findPackage", () -> supervisor.getSMServices()
                .getPackageManagementService().getPackageManagementStub().findPackage(all));
        attempt("Supervisor.SM.CommandExecutor", "runCommand", () -> supervisor.getSMServices()
                .getCommandExecutorService().getCommandExecutorStub()
                .runCommand(new Command("id", null, null)));
        IdentifierList domain = supervisorProvider.getDomain();
        attempt("Supervisor.COM.Archive", "count", () -> countInArchive(supervisor, domain));

        // Publishing on a service of the Supervisor, which only the Supervisor itself does: an
        // event for an App that does not exist, so that no App acts on it if it goes through
        URI eventsBroker = supervisor.getSMServices().getAppsLauncherService().getConnectionDetails()
                .getBrokerURI();
        attempt("Publish.Supervisor.SM.AppsLauncher", "monitorEvents",
                () -> withTimeout(() -> publishAppEvent(eventsBroker, domain)));
        if (subscribed) {
            Thread.sleep(2000);
            report.append("Publish.Supervisor.SM.AppsLauncher.monitorEvents.delivered=")
                    .append(delivered.get() ? "YES" : "NO").append('\n');
        }

        // A connection that presents a made-up credential instead of the probe's own,
        // set on the MAL consumer directly, as any App could
        byte[] forged = new byte[32];
        new java.security.SecureRandom().nextBytes(forged);
        NMFConsumer forger = new NMFConsumer(supervisorProvider);
        forger.init();
        org.ccsds.moims.mo.sm.packagemanagement.consumer.PackageManagementStub forgedStub = forger
                .getSMServices().getPackageManagementService().getPackageManagementStub();
        forgedStub.getConsumer().setAuthenticationId(new org.ccsds.moims.mo.mal.structures.Blob(forged));
        attempt("Forged.Supervisor.SM.PackageManagement", "findPackage", () -> forgedStub.findPackage(all));

        // The same CommandExecutor, with the Supervisor's host written as this computer's
        // name instead of its address: the same socket, so the same service, but the
        // probe presents its credential only to the address it was given
        URI executor = supervisor.getSMServices().getCommandExecutorService().getConnectionDetails()
                .getProviderURI();
        String hostName = java.net.InetAddress.getLocalHost().getHostName();
        URI renamed = new URI(executor.getValue().replaceFirst("^([a-z]+://)[^:/]+", "$1" + hostName));
        if (renamed.getValue().equals(executor.getValue())) {
            report.append("PROBE_ERROR=the host name of the Supervisor is already ").append(hostName).append('\n');
        } else {
            attempt("OtherHost.Supervisor.SM.CommandExecutor", "runCommand", () -> new esa.mo.sm.impl.consumer
                    .CommandExecutorConsumerServiceImpl(new org.ccsds.moims.mo.mal.helpertools.connections
                            .SingleConnectionDetails(renamed, null, domain), null)
                    .getCommandExecutorStub().runCommand(new Command("id", null, null)));
        }

        // The ground credential, which would give an App the rights of ground
        attempt("Supervisor.GroundCredential", "read", () -> Files.readAllBytes(Paths.get(
                System.getProperty(GROUND_CREDENTIAL_PROPERTY, DEFAULT_GROUND_CREDENTIAL))));
    }

    /**
     * Publishes an event on the Apps Launcher events of the Supervisor, through
     * its broker, as the Supervisor does when an App starts or stops.
     */
    private static void publishAppEvent(URI broker, IdentifierList domain) throws Exception {
        MALContext mal = MALContextFactory.newFactory().createMALContext(System.getProperties());
        MALProviderManager providers = mal.createProviderManager();
        try {
            MALProvider provider = providers.createProvider("permissions-probe-publisher", null,
                    AppsLauncherHelper.APPSLAUNCHER_SERVICE, new Blob(new byte[0]), new NoInteractions(),
                    new QoSLevel[]{QoSLevel.ASSURED}, new UInteger(1), System.getProperties(), true, broker, null);
            MALPublisher publisher = provider.createPublisher(AppsLauncherServiceInfo.MONITOREVENTS_OP, domain,
                    SessionType.LIVE, new Identifier("LIVE"), QoSLevel.BESTEFFORT, null, null);
            IdentifierList keyNames = new IdentifierList();
            keyNames.add(new Identifier("appName"));
            keyNames.add(new Identifier("appId"));
            AttributeTypeList keyTypes = new AttributeTypeList();
            keyTypes.add(AttributeType.IDENTIFIER);
            keyTypes.add(AttributeType.LONG);
            publisher.register(keyNames, keyTypes, new MALPublishInteractionListener() {
                @Override
                public void publishRegisterAckReceived(MALMessageHeader header, Map qosProperties) {
                }

                @Override
                public void publishRegisterErrorReceived(MALMessageHeader header, MALErrorBody error,
                        Map qosProperties) {
                }

                @Override
                public void publishErrorReceived(MALMessageHeader header, MALErrorBody error,
                        Map qosProperties) {
                }

                @Override
                public void publishDeregisterAckReceived(MALMessageHeader header, Map qosProperties) {
                }
            });
            AttributeList keyValues = new AttributeList();
            keyValues.add(new Identifier("no-such-app"));
            keyValues.add(new Union(-1L));
            publisher.publish(new UpdateHeader(new Identifier(provider.getURI().getValue()), domain,
                    keyValues.getAsNullableAttributeList()), AppEventType.STOP_REQUESTED, null, PUBLISHED_EVENT_INFO);
            publisher.deregister();
        } finally {
            providers.close();
            mal.close();
        }
    }

    /** A provider that offers no operations: the probe's publisher only publishes. */
    private static final class NoInteractions implements MALInteractionHandler {

        @Override
        public void malInitialize(MALProvider provider) {
        }

        @Override
        public void handleSend(MALInteraction interaction, MALMessageBody body) {
        }

        @Override
        public void handleSubmit(MALSubmit interaction, MALMessageBody body) {
        }

        @Override
        public void handleRequest(MALRequest interaction, MALMessageBody body) {
        }

        @Override
        public void handleInvoke(MALInvoke interaction, MALMessageBody body) {
        }

        @Override
        public void handleProgress(MALProgress interaction, MALMessageBody body) {
        }

        @Override
        public void malFinalize(MALProvider provider) {
        }
    }

    /** Makes a call that may never be answered, and gives up after {@link #TIMEOUT_S}. */
    private static void withTimeout(Attempt call) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        try {
            executor.submit(() -> {
                call.run();
                return null;
            }).get(TIMEOUT_S, TimeUnit.SECONDS);
        } catch (ExecutionException e) {
            throw (e.getCause() instanceof Exception) ? (Exception) e.getCause() : e;
        } catch (TimeoutException e) {
            throw new IllegalStateException("no answer within " + TIMEOUT_S + " s");
        } finally {
            executor.shutdownNow();
        }
    }

    /** Counts objects in the Supervisor's Archive; the answer arrives through callbacks. */
    private static void countInArchive(NMFConsumer supervisor, IdentifierList domain) throws Exception {
        CountDownLatch done = new CountDownLatch(1);
        AtomicReference<MOErrorException> error = new AtomicReference<>();
        supervisor.getCOMServices().getArchiveService().getArchiveStub().count(
                PackageManagementServiceInfo.PACKAGEINSTALLED_OBJECT_TYPE,
                new ArchiveQuery(domain, null, 0L, null, null, null, null), null,
                new ArchiveAdapter() {
            @Override
            public void countResponseReceived(MALMessageHeader msgHeader, Long count,
                    Map qosProperties) {
                done.countDown();
            }

            @Override
            public void countAckErrorReceived(MALMessageHeader msgHeader, MOErrorException e,
                    Map qosProperties) {
                error.set(e);
                done.countDown();
            }

            @Override
            public void countResponseErrorReceived(MALMessageHeader msgHeader, MOErrorException e,
                    Map qosProperties) {
                error.set(e);
                done.countDown();
            }
        });
        if (!done.await(TIMEOUT_S, TimeUnit.SECONDS)) {
            throw new IllegalStateException("no answer within " + TIMEOUT_S + " s");
        }
        if (error.get() != null) {
            throw error.get();
        }
    }

    /**
     * Makes one call and records its outcome: REFUSED for an authorisation or
     * authentication failure, ALLOWED if the service answered, even with an
     * ordinary error, and ERROR if the call could not be made at all.
     */
    private void attempt(String permission, String operation, Attempt call) {
        String outcome;
        try {
            try {
                call.run();
            } catch (MALInteractionException e) {
                throw e.getStandardError(); // The error a publisher receives from the broker
            }
            outcome = "ALLOWED";
        } catch (AuthorisationFailException | AuthenticationFailedException e) {
            outcome = "REFUSED (" + e.getClass().getSimpleName() + ": " + e.getExtraInformation() + ")";
        } catch (java.nio.file.NoSuchFileException e) {
            outcome = "ERROR (no such file: " + e.getMessage() + ")";
        } catch (java.nio.file.AccessDeniedException e) {
            outcome = "REFUSED (the file cannot be read: " + e.getMessage() + ")";
        } catch (MOErrorException e) {
            outcome = "ALLOWED (the service answered " + e.getClass().getSimpleName() + ")";
        } catch (Exception e) {
            outcome = "ERROR (" + e + ")";
        }
        report.append(permission).append('.').append(operation).append('=')
                .append(outcome.replace('\n', ' ')).append('\n');
    }

    private void writeReport() {
        String path = System.getProperty(REPORT_PROPERTY, DEFAULT_REPORT);
        try {
            Files.write(Paths.get(path), report.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("PERMISSIONS-PROBE: could not write " + path + " (" + e + ")");
        }
        System.out.print("PERMISSIONS-PROBE REPORT:\n" + report);
    }
}
