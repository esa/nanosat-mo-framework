/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */
package esa.mo.nmf.groundmoproxy;

import esa.mo.com.impl.consumer.ArchiveSyncConsumerServiceImpl;
import esa.mo.com.impl.util.COMObjectStructure;
import esa.mo.mc.impl.consumer.ActionConsumerServiceImpl;
import esa.mo.mc.impl.proxy.ActionProxyServiceImpl;
import esa.mo.sm.impl.provider.AppsLauncherManager;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.DuplicateException;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.com.archive.provider.QueryInteraction;
import org.ccsds.moims.mo.com.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.archivesync.ArchiveSyncHelper;
import org.ccsds.moims.mo.com.archivesync.body.GetTimeResponse;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.com.structures.ProviderList;
import org.ccsds.moims.mo.com.structures.ServiceFilter;
import org.ccsds.moims.mo.mal.structures.ServiceId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperTime;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UShortList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mc.action.ActionHelper;

/**
 * The Ground MO Proxy for OPS-SAT
 *
 * @author Cesar Coelho
 */
public class GroundMOProxySwarmsImpl extends GroundMOProxy {

    private final ProtocolBridgeSPP protocolBridgeSPP = new ProtocolBridgeSPP();
    private final HashMap<IdentifierList, URI> actionURIs = new HashMap<>();

    /**
     * Ground MO Proxy for OPS-SAT
     *
     */
    public GroundMOProxySwarmsImpl() {
        super();

        // Initialize the protocol bridge services and expose them using TCP/IP!
        final Map properties = System.getProperties();

        String protocol = System.getProperty("org.ccsds.moims.mo.mal.transport.default.protocol");

        // Default it to tcp if the property is not defined
        protocol = (protocol != null) ? protocol.split(":")[0] : "maltcp";

        // The range of APIDs below were formally requested 
        // And are uniquely assigned for the Ground MO Proxy of OPS-SAT
        properties.put(ProtocolBridgeSPP.PROPERTY_APID_RANGE_START, "1450");
        properties.put(ProtocolBridgeSPP.PROPERTY_APID_RANGE_END, "1499");

        // Initialize the SPP Protocol Bridge
        try {
            // TCP/IP is the selected transport binding for the bridge with SPP
            protocolBridgeSPP.init(protocol, properties);
            final URI routedURI = protocolBridgeSPP.getRoutingProtocol();

            // Initialize the pure protocol bridge for the services without extension
            final URI centralDirectoryServiceURI = new URI("malspp:247/100/5");
            super.init(centralDirectoryServiceURI, routedURI);

            final URI uri = super.getDirectoryServiceURI();
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(
                    Level.INFO, "Ground MO Proxy initialized! URI: " + uri + "\n");
        } catch (Exception ex) {
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(
                    Level.SEVERE, "The SPP Protocol Bridge could not be initialized!", ex);
        }
    }

    @Override
    public synchronized void additionalHandling() {
        IdentifierList domain = new IdentifierList();
        domain.add(new Identifier("*"));

        // Add the Action service rerouting stuff
        COMService serviceType = ActionHelper.ACTION_SERVICE;
        ServiceId serviceKey = new ServiceId(serviceType.getArea().getNumber(),
                serviceType.getServiceNumber(), serviceType.getArea().getVersion());
        ServiceFilter sf = new ServiceFilter(new Identifier("*"), domain, serviceKey, null);

        try {
            final ProviderList actionsCD = localDirectoryService.lookup(sf, null);

            // Cycle through the NMF Apps and sync them!
            for (int i = 0; i < actionsCD.size(); i++) {
                ProviderList psl = new ProviderList();
                psl.add(actionsCD.get(i));
                // Needs some work!

                try {
                    final SingleConnectionDetails connectionDetails = AppsLauncherManager.getSingleConnectionDetailsFromProviderList(psl);
                    try {
                        synchronized (actionURIs) {
                            URI localActionURI = actionURIs.get(connectionDetails.getDomain());

                            if (localActionURI == null) {
                                // This only needs to be done in case it still does not exist:
                                ActionConsumerServiceImpl actionConsumer = new ActionConsumerServiceImpl(connectionDetails, null);
                                ActionProxyServiceImpl actionProxyService = new ActionProxyServiceImpl();
                                actionProxyService.init(localCOMServices, actionConsumer);
                                localActionURI = actionProxyService.getConnectionProvider().getConnectionDetails().getProviderURI();
                                actionURIs.put(connectionDetails.getDomain(), localActionURI);
                            }

                            super.localDirectoryService.rerouteActionServiceURI(connectionDetails.getDomain(), localActionURI);
                        }
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    // The Action service does not exist on this provider...
                    // Do nothing!
                }
            }
        } catch (MALInteractionException | InvalidArgumentException ex) {
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // ---------------------
        // Sync the COM Archives
        // ---------------------
        serviceType = ArchiveSyncHelper.ARCHIVESYNC_SERVICE;
        serviceKey = new ServiceId(serviceType.getArea().getNumber(),
                serviceType.getServiceNumber(), serviceType.getArea().getVersion());
        sf = new ServiceFilter(new Identifier("*"), domain, serviceKey, null);

        try {
            final ProviderList archiveSyncsCD = localDirectoryService.lookup(sf, null);
            ArrayList<ArchiveSyncConsumerServiceImpl> archiveSyncs = new ArrayList<>();

            // Cycle through the NMF Apps and sync them!
            for (int i = 0; i < archiveSyncsCD.size(); i++) {
                ProviderList psl = new ProviderList();
                psl.add(archiveSyncsCD.get(i));

                try {
                    final SingleConnectionDetails connectionDetails = AppsLauncherManager.getSingleConnectionDetailsFromProviderList(psl);
                    try {
                        ArchiveSyncConsumerServiceImpl archSync = new ArchiveSyncConsumerServiceImpl(connectionDetails);
                        archiveSyncs.add(archSync);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (IOException ex) {
                    // The ArchiveSync service does not exist on this provider...
                    // Do nothing!
                }
            }

            this.syncRemoteArchiveWithLocalArchive(archiveSyncs);
        } catch (MALInteractionException | InvalidArgumentException ex) {
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public final void syncRemoteArchiveWithLocalArchive(ArrayList<ArchiveSyncConsumerServiceImpl> archiveSyncs)
            throws MALInteractionException, MALException {
        // Select Parameter Definitions by default
        ObjectTypeList objTypes = new ObjectTypeList();
        UShort shorty = new UShort((short) 0);
        objTypes.add(new ObjectType(shorty, shorty, new UOctet((short) 0), shorty));

        for (int i = 0; i < archiveSyncs.size(); i++) {
            ArchiveSyncConsumerServiceImpl archiveSync = archiveSyncs.get(i);

            GetTimeResponse response = archiveSync.getArchiveSyncStub().getTime();
            Time lastSyncTime = response.getLastSyncTime();

            if (lastSyncTime.getValue() == 0) {
                lastSyncTime = latestTimestampForProvider(archiveSync);
            }

            Time until = response.getCurrentTime();

            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(
                    Level.INFO,
                    "Synchronizing provider: {0}, From: {1}, Until: {2}",
                    new Object[]{archiveSync.getConnectionDetails().getDomain(), lastSyncTime, until});
            // This value should be obtained from the getCurrent timestamp!
            ArrayList<COMObjectStructure> comObjects = archiveSync.retrieveCOMObjects(lastSyncTime, until, objTypes);

            for (COMObjectStructure comObject : comObjects) {
                ArchiveDetailsList detailsList = new ArchiveDetailsList();
                detailsList.add(comObject.getArchiveDetails());

                try {
                    super.localCOMServices.getArchiveService().store(
                            false,
                            comObject.getObjType(),
                            comObject.getDomain(),
                            detailsList,
                            comObject.getObjectsHeterogeneousList(),
                            null
                    );
                } catch (MALException ex) {
                    Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(
                            Level.SEVERE, null, ex);
                } catch (DuplicateException ex) {
                    Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(
                            Level.SEVERE, "The object already exists!");
                } catch (MALInteractionException | InvalidArgumentException ex) {
                    Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(
                            Level.SEVERE, "Error!", ex);
                }
            }
            // Change the Archive URI to be the one of the local COM Archive service
            IdentifierList providerDomain = archiveSync.getConnectionDetails().getDomain();
            URI localCOMArchiveURI = super.getCOMArchiveServiceURI();
            //super.localDirectoryService.rerouteArchiveServiceURI(providerDomain, localCOMArchiveURI);
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(
                    Level.INFO,
                    "Synchronizing provider {0} completed",
                    new Object[]{archiveSync.getConnectionDetails().getDomain()});
        }
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        GroundMOProxySwarmsImpl proxy = new GroundMOProxySwarmsImpl();
    }

    private Time latestTimestampForProvider(ArchiveSyncConsumerServiceImpl archiveSync) {
        // We have to return the value as the most recent COM Object timestamp!

        // Do a query on the COM Objects for the latest one!
        Time timeInFarFuture = new Time(Long.MAX_VALUE);
        String text = HelperTime.time2readableString(timeInFarFuture);
        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.FINE,
                "The time in the future is: " + text);

        ArchiveQuery archiveQuery = new ArchiveQuery(
                archiveSync.getConnectionDetails().getDomain(),
                null,
                0L,
                null,
                null,
                timeInFarFuture,
                true,
                null
        );

        final Semaphore semaphore = new Semaphore(0);
        final AtomicReference<Time> latest = new AtomicReference<>();

        ObjectType objType = new ObjectType(new UShort(0), new UShort(0), new UOctet((short) 0), new UShort(0));

        try {
            super.localCOMServices.getArchiveService().query(false, objType,
                    archiveQuery, null, new QueryInteractionImpl(latest, semaphore));
        } catch (MALException ex) {
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException | InvalidArgumentException ex) {
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            semaphore.acquire();
            return latest.get();
        } catch (InterruptedException ex) {
            Logger.getLogger(GroundMOProxySwarmsImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new Time(0);
    }

    private static class QueryInteractionImpl extends QueryInteraction {

        private final AtomicReference<Time> latest;
        private final Semaphore semaphore;

        public QueryInteractionImpl(AtomicReference<Time> latest, Semaphore semaphore) {
            super(null);
            this.latest = latest;
            this.semaphore = semaphore;
        }

        @Override
        public MALMessage sendAcknowledgement() throws MALInteractionException, MALException {
            return null;
        }

        @Override
        public MALMessage sendUpdate(ObjectType objType, IdentifierList domain,
                ArchiveDetailsList objDetails, HeterogeneousList objBodies)
                throws MALInteractionException, MALException {
            // The query is sorted and asks for the latest object, so the first update
            // to arrive carries the timestamp being looked for.
            if (objDetails != null && !objDetails.isEmpty()) {
                latest.compareAndSet(null, objDetails.get(0).getTimestamp());
            }
            return null;
        }

        @Override
        public MALMessage sendResponse() throws MALInteractionException, MALException {
            latest.compareAndSet(null, new Time(0));
            semaphore.release();
            return null;
        }

        @Override
        public MALMessage sendError(MOErrorException error) throws MALInteractionException, MALException {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.INFO, "Error! (1)");
            semaphore.release();
            return null;
        }

        @Override
        public MALMessage sendUpdateError(MOErrorException error) throws MALInteractionException, MALException {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.INFO, "Error! (2)");
            semaphore.release();
            return null;
        }

    }
}
