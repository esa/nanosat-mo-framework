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
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.common.impl.proxy.DirectoryProxyServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
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
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.common.directory.DirectoryHelper;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.HeartbeatHelper;

/**
 * The Ground MO Proxy class.
 *
 * @author Cesar Coelho
 */
public abstract class GroundMOProxy {

    private static final Logger LOGGER = Logger.getLogger(GroundMOProxy.class.getName());
    protected final static long HEARTBEAT_PUBLISH_PERIOD = 10000;
    protected final static long DIRECTORY_SCAN_PERIOD = 10000; // 10 seconds
    private final AtomicBoolean nmsAliveStatus = new AtomicBoolean(false);
    protected final COMServicesProvider localCOMServices;
    protected final DirectoryProxyServiceImpl localDirectoryService;
    protected Timer timer;
    protected GroundHeartbeatAdapter providerStatusAdapter;
    private SingleConnectionDetails cdRemoteArchive;
    protected GroundProxyConnectorTask proxyConnectorTask;
    protected DirectoryScanTask directoryScanTask;

    public GroundMOProxy() {
        ConnectionProvider.resetURILinks();
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties

        // Initialize the Helpers for the APIs
        NMFConsumer.initHelpers();
        localCOMServices = new COMServicesProvider();
        localDirectoryService = new DirectoryProxyServiceImpl();
    }

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
        final ServiceKey serviceKey = new ServiceKey(service.getArea().getNumber(), service.getNumber(), service
            .getArea().getVersion());

        try {
            ProviderSummaryList list = getRemoteNMSProviderSpecificService(serviceKey);
            if (list.isEmpty() || list.get(0).getProviderDetails().getServiceCapabilities().isEmpty()) {
                return null;
            }
            return AppsLauncherManager.getSingleConnectionDetailsFromProviderSummaryList(list);
        } catch (MALInteractionException | MALException | IOException ex) {
            LOGGER.log(Level.SEVERE, "Cannot produce Connection Details for the service", ex);
        }
        return null;
    }

    public ProviderSummaryList getRemoteNMSProvider() throws MALInteractionException, MALException {
        return getRemoteNMSProviderSpecificService(new ServiceKey(new UShort((short) 0), new UShort((short) 0),
            new UOctet((short) 0)));
    }

    public ProviderSummaryList getRemoteNMSProviderSpecificService(ServiceKey key) throws MALInteractionException,
        MALException {
        IdentifierList wildcardList = new IdentifierList();
        wildcardList.add(new Identifier("*"));

        ServiceFilter filter = new ServiceFilter();
        filter.setDomain(wildcardList);
        filter.setNetwork(new Identifier("*"));
        filter.setSessionType(null);
        filter.setSessionName(new Identifier("*"));
        filter.setServiceKey(key);
        filter.setRequiredCapabilitySets(new UShortList());
        filter.setServiceProviderId(new Identifier("*"));
        ProviderSummaryList list = localDirectoryService.lookupProvider(filter, null);
        // Post-filter the list
        Iterator itr = list.iterator();
        while (itr.hasNext()) {
            ProviderSummary ps = (ProviderSummary) itr.next();
            if (!ps.getProviderId().getValue().startsWith(Const.NANOSAT_MO_SUPERVISOR_NAME)) {
                itr.remove();
            }
        }
        return list;
    }

    public abstract void additionalHandling();

    public URI getDirectoryServiceURI() {
        return localDirectoryService.getConnection().getPrimaryConnectionDetails().getProviderURI();
    }

    public URI getRemoteCentralDirectoryServiceURI() {
        if (directoryScanTask != null) {
            return directoryScanTask.getCentralDirectoryServiceURI();
        }
        return new URI("not-initialized");
    }

    public URI getRoutedURI() {
        if (directoryScanTask != null) {
            return directoryScanTask.getRoutedURI();
        }
        return new URI("not-initialized");
    }

    public URI getCOMArchiveServiceURI() {
        return localCOMServices.getArchiveService().getConnection().getPrimaryConnectionDetails().getProviderURI();
    }

    public DirectoryProxyServiceImpl getLocalDirectoryService() {
        return localDirectoryService;
    }

    /**
     * @return the nmsAliveStatus
     */
    public Boolean getNmsAliveStatus() {
        return nmsAliveStatus.get();
    }

    /**
     * @param nmsAliveStatus the nmsAliveStatus to set
     */
    public void setNmsAliveStatus(Boolean nmsAliveStatus) {
        this.nmsAliveStatus.set(nmsAliveStatus);
    }

    protected class DirectoryScanTask extends TimerTask {
        private final URI centralDirectoryServiceURI;
        private final URI routedURI;
        private boolean firstRun = true;
        private ArchiveConsumerServiceImpl archiveService;
        private FineTime lastTime = new FineTime(0);

        public DirectoryScanTask(URI centralDirectoryServiceURI, URI routedURI) {
            this.centralDirectoryServiceURI = centralDirectoryServiceURI;
            this.routedURI = routedURI;
        }

        public URI getCentralDirectoryServiceURI() {
            return centralDirectoryServiceURI;
        }

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
                } catch (MALException | MalformedURLException | MALInteractionException e) {
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
                    FineTime currentOBT = providerStatusAdapter.getLastBeatOBT();

                    if (currentOBT == null) {
                        return;
                    }
                    ArchiveQuery archiveQuery = new ArchiveQuery(archiveService.getConnectionDetails().getDomain(),
                        null, null, 0L, null, lastTime, currentOBT, false, null);

                    ArchiveQueryList archiveQueryList = new ArchiveQueryList();
                    archiveQueryList.add(archiveQuery);

                    long[] count = {0L}; // workaround to access the variable in the lambda below.
                    ArchiveAdapter adapter = new ArchiveAdapter() {
                        @Override
                        public synchronized void countResponseReceived(MALMessageHeader msgHeader, LongList countList,
                            Map qosProperties) {
                            count[0] += countList.get(0);
                        }
                    };

                    // Use the count operation from the Archive for Common.Directory.ServiceProvider
                    archiveService.getArchiveStub().count(DirectoryHelper.SERVICEPROVIDER_OBJECT_TYPE, archiveQueryList,
                        null, adapter);

                    // use the count operation from the Archive for SoftwareManagement.AppsLauncher.StopApp
                    archiveService.getArchiveStub().count(AppsLauncherHelper.STOPAPP_OBJECT_TYPE, archiveQueryList,
                        null, adapter);

                    if (count[0] != 0L) {
                        LOGGER.log(Level.INFO, "A change in the Central Directory service was detected." +
                            " The list of providers will be synchronized...");
                        try {
                            // If there are new objects, then synchronize!
                            localDirectoryService.syncLocalDirectoryServiceWithCentral(centralDirectoryServiceURI,
                                routedURI);
                            additionalHandling();

                        } catch (MALException | MALInteractionException | MalformedURLException ex) {
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

    protected void createProviderStatusAdapter(HeartbeatConsumerServiceImpl heartbeat) throws MALException,
        MALInteractionException {
        providerStatusAdapter = new GroundHeartbeatAdapter(heartbeat, esa.mo.nmf.groundmoproxy.GroundMOProxy.this);
    }

    protected class GroundProxyConnectorTask extends TimerTask {
        private final URI centralDirectoryServiceURI;
        private final URI routedURI;
        private Subscription heartbeatSubscription;
        private HeartbeatConsumerServiceImpl heartbeatService;

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
            } else {
                try {
                    if (!firstTime) {
                        failureCounter++;
                        if (failureCounter >= 3) {
                            // Reset everything
                            heartbeatService.close();
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
                            heartbeatSubscription = ConnectionConsumer.subscriptionWildcard(new Identifier("HBSUB"));

                            try {
                                firstTime = false;
                                heartbeatService.getHeartbeatStub().beatRegister(heartbeatSubscription,
                                    providerStatusAdapter);
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
                } catch (MALException | MalformedURLException | MALInteractionException ex) {
                    LOGGER.log(Level.SEVERE, "Error when initialising link to the NMS.", ex);
                }
            }
        }

        public Subscription getHeartbeatSubscription() {
            return heartbeatSubscription;
        }

        public HeartbeatConsumerServiceImpl getHeartbeatService() {
            return heartbeatService;
        }
    }

}
