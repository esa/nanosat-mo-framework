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
package esa.mo.nmf.groundmoproxy;

import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.proxy.DirectoryProxyServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.misc.Const;
import esa.mo.nmf.NMFConsumer;
import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import esa.mo.sm.impl.provider.AppsLauncherManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.directory.DirectoryServiceInfo;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.ServiceKey;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperMisc;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;

import org.ccsds.moims.mo.sm.heartbeat.HeartbeatHelper;

/**
 * The Ground MO Proxy class.
 *
 * @author Cesar Coelho
 */
public abstract class GroundMOProxy {

    private static final Logger LOGGER = Logger.getLogger(GroundMOProxy.class.getName());
    /** Period, in milliseconds, at which the connector task probes the spacecraft heartbeat. */
    protected final static long HEARTBEAT_PUBLISH_PERIOD = 10000;
    /** Period, in milliseconds, between two scans of the remote Central Directory service. */
    protected final static long DIRECTORY_SCAN_PERIOD = 10000; // 10 seconds
    private final AtomicBoolean nmsAliveStatus = new AtomicBoolean(false);
    /** The local COM services stack (Archive, Directory) exposed to ground consumers. */
    protected final COMServicesProvider localCOMServices;
    /** The local Directory service that mirrors the spacecraft providers to the ground. */
    protected final DirectoryProxyServiceImpl localDirectoryService;
    /** Timer driving the periodic connector and directory-scan tasks. */
    protected Timer timer;
    /** Adapter listening to the spacecraft heartbeat to track the link status. */
    protected GroundHeartbeatAdapter providerStatusAdapter;
    private SingleConnectionDetails cdRemoteArchive;
    /** Task that (re)establishes the connection to the spacecraft. */
    protected GroundProxyConnectorTask proxyConnectorTask;
    /** Task that periodically synchronizes the local Directory with the spacecraft's. */
    protected DirectoryScanTask directoryScanTask;

    /**
     * Creates a new Ground MO Proxy, loading the provider and transport properties
     * files and initialising the local COM and Directory service helpers.
     */
    public GroundMOProxy() {
        // Loads: provider.properties; transport.properties
        ConnectionProvider.resetURILinksFile();
        HelperMisc.loadPropertiesFile();
        NMFConsumer.initHelpers();

        // Initialize the Helpers for the APIs
        localCOMServices = new COMServicesProvider();
        localDirectoryService = new DirectoryProxyServiceImpl();
    }

    /**
     * Initialises the proxy: starts the local COM and Directory services and schedules
     * the periodic tasks that connect to the spacecraft and keep the local Directory in sync.
     *
     * @param centralDirectoryServiceURI the URI of the spacecraft's Central Directory service
     * @param routedURI the URI through which the spacecraft providers are reached (the routed link)
     */
    public void init(final URI centralDirectoryServiceURI, final URI routedURI) {
        try {
            localCOMServices.init();
            localDirectoryService.init(localCOMServices);
        } catch (MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        // Start the timer to check for the heartbeat and initialise connection
        timer = new Timer("MainProxyTimer");
        proxyConnectorTask = new GroundProxyConnectorTask(centralDirectoryServiceURI, routedURI);
        timer.schedule(proxyConnectorTask, 0, HEARTBEAT_PUBLISH_PERIOD);

        // Add the periodic check if new NMF Apps were started/stopped
        directoryScanTask = new DirectoryScanTask(centralDirectoryServiceURI, routedURI);
        timer.schedule(directoryScanTask, DIRECTORY_SCAN_PERIOD, DIRECTORY_SCAN_PERIOD);

    }

    private SingleConnectionDetails cdFromService(COMService service) {
        try {
            ServiceKey key = service.getserviceKey();
            ServiceId serviceId = new ServiceId(key.getAreaNumber(), key.getServiceNumber(), key.getAreaVersion());
            ProviderList list = getRemoteNMSProviderSpecificService(serviceId);
            if (list.isEmpty() || list.get(0).getServiceCapabilities().isEmpty()) {
                return null;
            }
            return AppsLauncherManager.getSingleConnectionDetailsFromProviderList(list);
        } catch (InvalidArgumentException | MALInteractionException | MALException | IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot produce Connection Details for the service", ex);
        }
        return null;
    }

    /**
     * Looks up the NanoSat MO Supervisor providers registered in the local Directory service,
     * across all services (wildcard service key).
     *
     * @return the list of matching Supervisor providers; empty if none is registered
     * @throws org.ccsds.moims.mo.com.InvalidArgumentException if the lookup filter is invalid
     * @throws MALInteractionException if the Directory service returns an error
     * @throws MALException if a communication error occurs
     */
    public ProviderList getRemoteNMSProvider() throws org.ccsds.moims.mo.com.InvalidArgumentException, MALInteractionException, MALException {
        return getRemoteNMSProviderSpecificService(
                new ServiceId(new UShort((short) 0), new UShort((short) 0), new UOctet((short) 0))
        );
    }

    /**
     * Looks up the NanoSat MO Supervisor providers registered in the local Directory service
     * that expose the given service.
     *
     * @param key the service identifier to filter by (area, service and version)
     * @return the list of matching Supervisor providers; empty if none is registered
     * @throws org.ccsds.moims.mo.com.InvalidArgumentException if the lookup filter is invalid
     * @throws MALInteractionException if the Directory service returns an error
     * @throws MALException if a communication error occurs
     */
    public ProviderList getRemoteNMSProviderSpecificService(ServiceId key)
            throws org.ccsds.moims.mo.com.InvalidArgumentException, MALInteractionException, MALException {
        IdentifierList wildcardList = new IdentifierList();
        wildcardList.add(new Identifier("*"));

        ServiceFilter filter = new ServiceFilter(
                new Identifier("*"),
                wildcardList,
                key, null);
        ProviderList list = localDirectoryService.lookup(filter, null);
        // Post-filter the list
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            Provider ps = (Provider) itr.next();
            if (!ps.getProviderName().getValue().startsWith(Const.NANOSAT_MO_SUPERVISOR_NAME)) {
                itr.remove();
            }
        }
        return list;
    }

    /**
     * Hook invoked whenever the link to the spacecraft is (re)established or the set of
     * remote providers changes. Mission-specific subclasses override it to perform any
     * additional wiring (for example, spawning per-app proxies).
     */
    public abstract void additionalHandling();

    /**
     * Returns the URI of the local (ground-side) Directory service that consumers connect to.
     *
     * @return the local Directory service URI
     */
    public URI getDirectoryServiceURI() {
        return localDirectoryService.getConnection().getPrimaryConnectionDetails().getProviderURI();
    }

    /**
     * Returns the URI of the spacecraft's Central Directory service this proxy connects to.
     *
     * @return the remote Central Directory service URI, or {@code not-initialized} if the
     * proxy has not been initialised yet
     */
    public URI getRemoteCentralDirectoryServiceURI() {
        if (directoryScanTask != null) {
            return directoryScanTask.getCentralDirectoryServiceURI();
        }
        return new URI("not-initialized");
    }

    /**
     * Returns the routed URI through which the spacecraft providers are reached.
     *
     * @return the routed URI, or {@code not-initialized} if the proxy has not been
     * initialised yet
     */
    public URI getRoutedURI() {
        if (directoryScanTask != null) {
            return directoryScanTask.getRoutedURI();
        }
        return new URI("not-initialized");
    }

    /**
     * Returns the URI of the local COM Archive service.
     *
     * @return the local COM Archive service URI
     */
    public URI getCOMArchiveServiceURI() {
        return localCOMServices.getArchiveService().getConnection().getPrimaryConnectionDetails().getProviderURI();
    }

    /**
     * Returns the local Directory service implementation mirroring the spacecraft providers.
     *
     * @return the local Directory service
     */
    public DirectoryProxyServiceImpl getLocalDirectoryService() {
        return localDirectoryService;
    }

    /**
     * Returns whether the spacecraft (NanoSat MO Supervisor) is currently reachable, as
     * tracked from the received heartbeat.
     *
     * @return {@code true} if the link to the spacecraft is alive
     */
    public Boolean getNmsAliveStatus() {
        return nmsAliveStatus.get();
    }

    /**
     * Sets the tracked alive status of the spacecraft link.
     *
     * @param nmsAliveStatus {@code true} if the link to the spacecraft is alive
     */
    public void setNmsAliveStatus(Boolean nmsAliveStatus) {
        this.nmsAliveStatus.set(nmsAliveStatus);
    }

    /**
     * Periodic task that synchronizes the local Directory service with the spacecraft's
     * Central Directory whenever the remote COM Archive reports new objects.
     */
    protected class DirectoryScanTask extends TimerTask {

        private final URI centralDirectoryServiceURI;
        private final URI routedURI;
        private boolean firstRun = true;
        private ArchiveConsumerServiceImpl archiveService;
        private Time lastTime = new Time(0);

        /**
         * Creates the directory-scan task.
         *
         * @param centralDirectoryServiceURI the URI of the spacecraft's Central Directory service
         * @param routedURI the routed URI through which the spacecraft providers are reached
         */
        public DirectoryScanTask(URI centralDirectoryServiceURI, URI routedURI) {
            this.centralDirectoryServiceURI = centralDirectoryServiceURI;
            this.routedURI = routedURI;
        }

        /**
         * Returns the URI of the spacecraft's Central Directory service.
         *
         * @return the Central Directory service URI
         */
        public URI getCentralDirectoryServiceURI() {
            return centralDirectoryServiceURI;
        }

        /**
         * Returns the routed URI through which the spacecraft providers are reached.
         *
         * @return the routed URI
         */
        public URI getRoutedURI() {
            return routedURI;
        }

        @Override
        public void run() {
            if (!getNmsAliveStatus() && cdRemoteArchive == null) {
                try {
                    localDirectoryService.syncLocalDirectoryServiceWithCentral(centralDirectoryServiceURI, routedURI);
                    cdRemoteArchive = cdFromService(ArchiveHelper.ARCHIVE_SERVICE);
                } catch (MALTransmitErrorException e) {
                    LOGGER.log(Level.WARNING,
                            "Failed to start directory service sync. Check the link to the spacecraft.");
                } catch (UnknownException | InvalidArgumentException | MALException | MalformedURLException | MALInteractionException e) {
                    LOGGER.log(Level.SEVERE, "Error when initialising link to the NMS.", e);
                }
            } else if (getNmsAliveStatus() && cdRemoteArchive != null) {
                // If alive and remote Archive connection details are initialised and heartbeat is received
                try {
                    if (firstRun) {
                        archiveService = new ArchiveConsumerServiceImpl(cdRemoteArchive);
                        firstRun = false;
                    }

                    // Check the remote COM Archive for new objects! Use On-Board Timestamp.
                    Time currentOBT = providerStatusAdapter.getLastBeatOBT();

                    if (currentOBT == null) {
                        return;
                    }
                    ArchiveQuery archiveQuery = new ArchiveQuery(archiveService.getConnectionDetails().getDomain(),
                            null, 0L, null, lastTime, currentOBT, false, null);

                    long[] count = {0L}; // workaround to access the variable in the lambda below.
                    ArchiveAdapter adapter = new ArchiveAdapter() {
                        @Override
                        public synchronized void countResponseReceived(MALMessageHeader msgHeader,
                                Long countResult, Map qosProperties) {
                            count[0] += countResult;
                        }
                    };

                    // Use the count operation from the Archive for Common.Directory.Provider
                    archiveService.getArchiveStub().count(
                            DirectoryServiceInfo.PROVIDER_OBJECT_TYPE,
                            archiveQuery,
                            null, adapter);

                    if (count[0] != 0L) {
                        LOGGER.log(Level.INFO, "A change in the Central Directory service was detected."
                                + " The list of providers will be synchronized...");
                        try {
                            // If there are new objects, then synchronize!
                            localDirectoryService.syncLocalDirectoryServiceWithCentral(
                                    centralDirectoryServiceURI, routedURI);
                            additionalHandling();
                        } catch (UnknownException | InvalidArgumentException | MALException | MALInteractionException | MalformedURLException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                        }
                    }

                    lastTime = currentOBT;
                } catch (MALInteractionException | MALException | IOException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Creates and registers the adapter that listens to the spacecraft heartbeat.
     *
     * @param heartbeat the heartbeat consumer service connected to the spacecraft
     * @throws MALException if a communication error occurs
     * @throws MALInteractionException if the heartbeat service returns an error
     */
    protected void createProviderStatusAdapter(HeartbeatConsumerServiceImpl heartbeat)
            throws MALException, MALInteractionException {
        providerStatusAdapter = new GroundHeartbeatAdapter(heartbeat, this);
    }

    /**
     * Periodic task that (re)connects to the spacecraft: it subscribes to the heartbeat
     * service once the remote COM Archive is reachable and resets the connection after
     * repeated heartbeat failures.
     */
    protected class GroundProxyConnectorTask extends TimerTask {

        private final URI centralDirectoryServiceURI;
        private final URI routedURI;
        private Subscription heartbeatSubscription;
        private HeartbeatConsumerServiceImpl heartbeatService;

        /**
         * Creates the proxy-connector task.
         *
         * @param centralDirectoryServiceURI the URI of the spacecraft's Central Directory service
         * @param routedURI the routed URI through which the spacecraft providers are reached
         */
        public GroundProxyConnectorTask(URI centralDirectoryServiceURI, URI routedURI) {
            this.centralDirectoryServiceURI = centralDirectoryServiceURI;
            this.routedURI = routedURI;
        }

        private boolean firstTime = true;
        private int failureCounter = 0;

        @Override
        public void run() {
            if (nmsAliveStatus.get()) {
                failureCounter = 0;
                return;
            }

            try {
                if (!firstTime) {
                    failureCounter++;
                    if (failureCounter >= 3) {
                        // Reset everything
                        heartbeatService.closeConnection();
                        providerStatusAdapter.stop();
                        firstTime = true;
                        failureCounter = 0;
                    }
                }
                if (firstTime) {
                    if (cdRemoteArchive == null) {
                        LOGGER.log(Level.WARNING,
                                "Failed to find the remote NMS Archive. Might be still initializing...");
                        return;
                    } else {
                        // If it is first time, then we need to connect to the
                        // heartbeat service and listen to the beat
                        SingleConnectionDetails connectionDetails = cdFromService(
                                HeartbeatHelper.HEARTBEAT_SERVICE);
                        heartbeatService = new HeartbeatConsumerServiceImpl(connectionDetails, null);
                        createProviderStatusAdapter(heartbeatService);
                        heartbeatSubscription = new Subscription(new Identifier("HBSUB"), null, null, null);

                        try {
                            firstTime = false;
                            heartbeatService.getHeartbeatStub().beatRegister(
                                    heartbeatSubscription, providerStatusAdapter);
                        } catch (MALInteractionException | MALException ex) {
                            LOGGER.log(Level.SEVERE, "Error when subscribing to the NMS heartbeat.", ex);
                        }
                    }
                    setNmsAliveStatus(true);
                    additionalHandling();
                    return;
                }
            } catch (MALTransmitErrorException ex) {
                LOGGER.log(Level.WARNING,
                        "Failed to start directory service sync. Check the link to the spacecraft.");
            } catch (MALException | MALInteractionException ex) {
                LOGGER.log(Level.SEVERE, "Error when initialising link to the NMS.", ex);
            }
        }

        /**
         * Returns the subscription used to register for the spacecraft heartbeat.
         *
         * @return the heartbeat subscription, or {@code null} if not yet subscribed
         */
        public Subscription getHeartbeatSubscription() {
            return heartbeatSubscription;
        }

        /**
         * Returns the heartbeat consumer service connected to the spacecraft.
         *
         * @return the heartbeat consumer service, or {@code null} if not yet connected
         */
        public HeartbeatConsumerServiceImpl getHeartbeatService() {
            return heartbeatService;
        }
    }

}
