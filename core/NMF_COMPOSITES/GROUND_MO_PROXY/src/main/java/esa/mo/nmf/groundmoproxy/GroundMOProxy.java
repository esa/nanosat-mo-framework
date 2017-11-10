/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.misc.HelperNMF;
import esa.mo.nmf.NMFConsumer;
import esa.mo.sm.impl.consumer.HeartbeatConsumerServiceImpl;
import esa.mo.sm.impl.provider.AppsLauncherManager;
import java.io.IOException;
import java.net.MalformedURLException;
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
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.softwaremanagement.appslauncher.AppsLauncherHelper;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.HeartbeatHelper;
import org.ccsds.moims.mo.softwaremanagement.heartbeat.consumer.HeartbeatAdapter;

/**
 * The Ground MO Proxy class.
 *
 * @author Cesar Coelho
 */
public abstract class GroundMOProxy {

    private final static long PERIOD = 10000; // 10 seconds
    private final AtomicBoolean nmsAliveStatus = new AtomicBoolean(false);
    protected final COMServicesProvider localCOMServices;
    protected final DirectoryProxyServiceImpl localDirectoryService;
    private Timer timer;
    private ProviderStatusAdapter providerStatusAdapter;

    public GroundMOProxy() {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
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
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Start the timer to publish the heartbeat
        timer = new Timer("Central_Directory_service_Periodic_Sync");
        timer.scheduleAtFixedRate(new TimerTask() {
            private boolean firstTime = true;

            @Override
            public void run() {
                if (!nmsAliveStatus.get()) {
                    try {
                        if (firstTime) {
                            // If it is first time, then we need to conect to the
                            // heartbeat service and listen to the beat
                            SingleConnectionDetails connectionDetails = cdFromService(HeartbeatHelper.HEARTBEAT_SERVICE);
                            HeartbeatConsumerServiceImpl heartbeat = new HeartbeatConsumerServiceImpl(connectionDetails, null);
                            providerStatusAdapter = new ProviderStatusAdapter(heartbeat);
                            Subscription heartbeatSubscription = ConnectionConsumer.subscriptionWildcardRandom();

                            try {
                                heartbeat.getHeartbeatStub().beatRegister(heartbeatSubscription, providerStatusAdapter);
                                firstTime = false;
                            } catch (MALInteractionException ex) {
                                Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (MALException ex) {
                                Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }

                        localDirectoryService.syncLocalDirectoryServiceWithCentral(centralDirectoryServiceURI, routedURI);
                        nmsAliveStatus.set(true);
                        additionalHandling();
                    } catch (MALException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MALInteractionException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }, 0, PERIOD);

        // Add the periodic check if new NMF Apps were started/stopped
        timer.scheduleAtFixedRate(new TimerTask() {
            private boolean firstRun = true;
            private ArchiveConsumerServiceImpl archiveService;
            private FineTime lastTime = new FineTime(0);

            @Override
            public void run() {
                // If alive
                if (nmsAliveStatus.get()) {
                    try {
                        if (firstRun) {
                            SingleConnectionDetails connectionDetails = cdFromService(ArchiveHelper.ARCHIVE_SERVICE);
                            archiveService = new ArchiveConsumerServiceImpl(connectionDetails);
                            firstRun = false;
                        }

                        // Check the remote COM Archive for new objects!
                        FineTime currentTime = providerStatusAdapter.getLastBeat();

                        ArchiveQuery archiveQuery = new ArchiveQuery(
                                archiveService.getConnectionDetails().getDomain(),
                                null,
                                null,
                                null,
                                null,
                                lastTime,
                                currentTime,
                                false,
                                null
                        );

                        ArchiveQueryList archiveQueryList = new ArchiveQueryList();
                        archiveQueryList.add(archiveQuery);

                        ArchiveAdapter adapter = new ArchiveAdapter() {
                            @Override
                            public synchronized void countResponseReceived(MALMessageHeader msgHeader,
                                    LongList countList, Map qosProperties) {
                                long count = countList.get(0);

                                if (count != 0) {
                                    Logger.getLogger(GroundMOProxy.class.getName()).log(Level.INFO,
                                            "The count is not zero. It returned: " + count);
                                    try {
                                        // If there are new objects, then synchronize!
                                        localDirectoryService.syncLocalDirectoryServiceWithCentral(centralDirectoryServiceURI, routedURI);
                                    } catch (MALException ex) {
                                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (MalformedURLException ex) {
                                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (MALInteractionException ex) {
                                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        };

                        // Use the cout operation from the Archive for Common.Directory.ServiceProvider
                        archiveService.getArchiveStub().count(DirectoryHelper.SERVICEPROVIDER_OBJECT_TYPE,
                                archiveQueryList, null, adapter);

                        // use the cout operation from the Archive for SoftwareManagement.AppsLauncher.StopApp
                        archiveService.getArchiveStub().count(AppsLauncherHelper.STOPAPP_OBJECT_TYPE,
                                archiveQueryList, null, adapter);

                        lastTime = currentTime;
                    } catch (MALInteractionException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MALException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

        }, 0, PERIOD);

    }

    private SingleConnectionDetails cdFromService(COMService service) {
        final ServiceKey serviceKey = new ServiceKey(
                service.getArea().getNumber(),
                service.getNumber(),
                service.getArea().getVersion()
        );

        try {
            ProviderSummaryList list = getRemoteNMSProviderSpecificService(serviceKey);
            return AppsLauncherManager.getSingleConnectionDetailsFromProviderSummaryList(list);
        } catch (MALInteractionException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public ProviderSummaryList getRemoteNMSProvider() throws MALInteractionException, MALException {
        IdentifierList wildcardList = new IdentifierList();
        wildcardList.add(new Identifier("*"));

        ServiceFilter filter = new ServiceFilter();
        filter.setDomain(wildcardList);
        filter.setNetwork(new Identifier("*"));
        filter.setSessionType(null);
        filter.setSessionName(new Identifier("*"));

        filter.setServiceKey(new ServiceKey(new UShort((short) 0), new UShort((short) 0), new UOctet((short) 0)));
        filter.setRequiredCapabilities(new UIntegerList());
        filter.setServiceProviderName(new Identifier(HelperNMF.NMF_NMS_NAME));

        return localDirectoryService.lookupProvider(filter, null);
    }

    public ProviderSummaryList getRemoteNMSProviderSpecificService(ServiceKey key) throws MALInteractionException, MALException {
        IdentifierList wildcardList = new IdentifierList();
        wildcardList.add(new Identifier("*"));

        ServiceFilter filter = new ServiceFilter();
        filter.setDomain(wildcardList);
        filter.setNetwork(new Identifier("*"));
        filter.setSessionType(null);
        filter.setSessionName(new Identifier("*"));

        filter.setServiceKey(key);
        filter.setRequiredCapabilities(new UIntegerList());
        filter.setServiceProviderName(new Identifier(HelperNMF.NMF_NMS_NAME));

        return localDirectoryService.lookupProvider(filter, null);
    }

    public abstract void additionalHandling();

    public URI getDirectoryServiceURI() {
        return localDirectoryService.getConnection().getPrimaryConnectionDetails().getProviderURI();
    }

    public URI getCOMArchiveServiceURI() {
        return localCOMServices.getArchiveService().getConnection().getPrimaryConnectionDetails().getProviderURI();
    }

    private class ProviderStatusAdapter extends HeartbeatAdapter {

        private static final long DELTA_ERROR = 2 * 1000; // 2 seconds = 2000 milliseconds
        private final long period; // In seconds
        private long lag; // In milliseconds
        private final Timer timer;
        private Time lastBeatAt = HelperTime.getTimestampMillis();

        public ProviderStatusAdapter(final HeartbeatConsumerServiceImpl heartbeat) throws MALInteractionException, MALException {
            long timestamp = System.currentTimeMillis();
            double value = heartbeat.getHeartbeatStub().getPeriod().getValue();
            lag = System.currentTimeMillis() - timestamp;
            period = (long) (value * 1000);

            Logger.getLogger(ProviderStatusAdapter.class.getName()).log(Level.INFO,
                    "The provider is reachable! Beat period: " + value + " seconds");
            nmsAliveStatus.set(true);

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                int tryNumber = 0;

                @Override
                public void run() {
                    synchronized (timer) {
                        final Time currentTime = HelperTime.getTimestampMillis();

                        // If the current time has passed the last beat + the beat period + a delta error
                        long threshold = lastBeatAt.getValue() + period + DELTA_ERROR;

                        if (currentTime.getValue() > threshold) {
                            // Then the provider is unresponsive
                            nmsAliveStatus.set(false);
                        } else {
                            if (tryNumber == 3) { // Every third try...
                                try {
                                    long timestamp = System.currentTimeMillis();
                                    heartbeat.getHeartbeatStub().getPeriod();
                                    lag = System.currentTimeMillis() - timestamp; // Calculate the lag
                                } catch (MALInteractionException ex) {
                                    Logger.getLogger(ProviderStatusAdapter.class.getName()).log(Level.SEVERE, null, ex);
                                } catch (MALException ex) {
                                    Logger.getLogger(ProviderStatusAdapter.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                tryNumber = 0;
                            }
                        }

                        tryNumber++;

                    }
                }
            }, period, period);
        }

        @Override
        public synchronized void beatNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                org.ccsds.moims.mo.mal.structures.Identifier _Identifier0,
                org.ccsds.moims.mo.mal.structures.UpdateHeaderList _UpdateHeaderList1,
                java.util.Map qosProperties) {
            synchronized (timer) {
                lastBeatAt = HelperTime.getTimestampMillis();
                final Time onboardTime = msgHeader.getTimestamp();
                final long iDiff = lastBeatAt.getValue() - onboardTime.getValue();

                Logger.getLogger(ProviderStatusAdapter.class.getName()).log(Level.INFO,
                        "(Clocks diff: " + iDiff + " ms"
                        + " | Round-Trip Delay time: " + lag + " ms"
                        + " | Last beat received at: " + HelperTime.time2readableString(lastBeatAt)
                        + ")"
                );

                nmsAliveStatus.set(true);
            }
        }

        public FineTime getLastBeat() {
            return HelperTime.timeToFineTime(lastBeatAt);
        }
    }

}
