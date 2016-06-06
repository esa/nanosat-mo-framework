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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.com.event.provider.EventInheritanceSkeleton;
import org.ccsds.moims.mo.com.event.provider.MonitorEventPublisher;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 *
 */
public class EventProviderServiceImpl extends EventInheritanceSkeleton {

    private MALProvider eventServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private MonitorEventPublisher publisher;
    private final ConfigurationProvider configuration = new ConfigurationProvider();
    private final ConnectionProvider connection = new ConnectionProvider();
    private ArchiveProviderServiceImpl archiveService;

    /**
     * Initializes the Event service provider
     *
     * @param archiveService
     * @throws MALException On initialization error.
     */
    public synchronized void init(ArchiveProviderServiceImpl archiveService) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                EventHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        this.archiveService = archiveService;

        publisher = createMonitorEventPublisher(configuration.getDomain(),
                configuration.getNetwork(),
                SessionType.LIVE,
                new Identifier("LIVE"),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        /*
        try {
            final EntityKeyList lst = new EntityKeyList();
            lst.add(new EntityKey(new Identifier("*"), (long) 0, (long) 0, (long) 0));
            publisher.register(lst, new PublishInteractionListener());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        
        // shut down old service transport
        if (null != eventServiceProvider) {
            connection.close();
        }

        eventServiceProvider = connection.startService(EventHelper.EVENT_SERVICE_NAME.toString(), EventHelper.EVENT_SERVICE, this);
        running = true;
        initialiased = true;
        Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.INFO, "Event service: READY");

    }

    public void setArchiveService(ArchiveProviderServiceImpl archiveService) {
        this.archiveService = archiveService;
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    /**
     * Generates the Event and stores it on the archive
     *
     * @param objType Object Type
     * @param domain Domain
     * @param eventObjBody Event Object Body
     * @param related Related field
     * @param source Source field
     * @param interaction Interaction object
     * @return Object instance identifier of the Event
     */
    public Long generateAndStoreEvent(final ObjectType objType, final IdentifierList domain, final Element eventObjBody,
            final Long related, final ObjectId source, final MALInteraction interaction) {
        return this.generateAndStoreEvent(objType, domain, eventObjBody, related, source, interaction, null, null);
    }

    /**
     * Generates the Event and stores it on the archive
     *
     * @param objType Object Type
     * @param domain Domain
     * @param eventObjBody Event Object Body
     * @param related Related link
     * @param source Source link
     * @param uri URI
     * @param network Network
     * @return Object instance identifier of the Event
     */
    public Long generateAndStoreEvent(final ObjectType objType, final IdentifierList domain, final Element eventObjBody,
            final Long related, final ObjectId source, final URI uri, final Identifier network) {
        return this.generateAndStoreEvent(objType, domain, eventObjBody, related, source, null, uri, network);
    }

    /**
     * Generates the Event and stores it on the archive
     *
     * @param objType Object Type
     * @param domain Domain
     * @param eventObjBody Event Object Body
     * @param related Related link
     * @param source Source link
     * @param interaction Interaction object
     * @return Object instance identifier of the Event
     */
    private Long generateAndStoreEvent(final ObjectType objType, final IdentifierList domain, final Element eventObjBody,
            final Long related, final ObjectId source, final MALInteraction interaction, final URI uri, final Identifier network) {

        ObjectDetailsList objectDetailsList = new ObjectDetailsList();
        objectDetailsList.add(new ObjectDetails(related, source));
        ElementList events = null;

        try {
            if (eventObjBody != null) {  // Do we have a null as input?
                // Is it a list already?
                if (eventObjBody instanceof java.util.ArrayList) {
                    events = (ElementList) eventObjBody;    // Then just cast it to ElementList
                } else {
                    events = HelperMisc.element2elementList(eventObjBody);  // Else, convert it to ElementList
                    events.add(eventObjBody);
                }
            }

        } catch (Exception ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Store it!!
        if (interaction != null) {
            return this.storeEventOnArchive(objectDetailsList, domain, objType, events, interaction);
        } else {
            return this.storeEventOnArchive(objectDetailsList, domain, objType, events, uri, network);
        }
    }

    /**
     * Publishes an Event through the Event service.
     *
     * @param interaction Interaction object
     * @param objId Object instance identifier
     * @param objType Object type
     * @param related Related link
     * @param source Source link
     * @param eventBodies Bodies of the event
     */
    public void publishEvent(final MALInteraction interaction, final Long objId, final ObjectType objType,
            final Long related, final ObjectId source, final ElementList eventBodies) {

        URI sourceURI = new URI("");

        if (interaction != null) {
            if (interaction.getMessageHeader() != null) {
                sourceURI = interaction.getMessageHeader().getURITo();
            }
        }

        // requirement: 3.3.2.1
        this.publishEvent(sourceURI, objId, objType, related, source, eventBodies);
    }

    /**
     * Publishes an Event through the Event service.
     *
     * @param sourceURI Source URI
     * @param objId Object instance identifier
     * @param objType Object type
     * @param related Related link
     * @param source Source link
     * @param eventBodies Bodies of the event
     */
    public void publishEvent(final URI sourceURI, final Long objId, final ObjectType objType,
            final Long related, final ObjectId source, ElementList eventBodies) {
        // 3.3.2.1 , 3.3.2.2 , 3.3.2.3 , 3.3.2.4 , 3.3.2.5
        try {
            if (!isRegistered) {
                final EntityKeyList lst = new EntityKeyList();
                lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                publisher.register(lst, new PublishInteractionListener());
                isRegistered = true;
            }

            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.FINE,
                    "Publishing Event for the Event objId: {0}; with Event Object Number: {1}",
                    new Object[]{objId, objType.getNumber()});

            // 0xFFFF FFFF FF00 0000
            final Long secondEntityKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(objType);

            final Long subKey = (source != null) ? HelperCOM.generateSubKey(source.getType()) : null;
            // requirements: 3.3.4.2.1 , 3.3.4.2.2 , 3.3.4.2.3 , 3.3.4.2.4
            final EntityKey ekey = new EntityKey(
                    new Identifier(objType.getNumber().toString()),
                    secondEntityKey,
                    objId,
                    subKey);

            final Time timestamp = HelperTime.getTimestampMillis(); //  requirement: 3.3.4.2.7

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectDetailsList objectDetailsList = new ObjectDetailsList();

            hdrlst.add(new UpdateHeader(timestamp, sourceURI, UpdateType.DELETION, ekey));
            objectDetailsList.add(new ObjectDetails(related, source)); // requirement: 3.3.4.2.5

            if (eventBodies != null) {
                if (eventBodies.isEmpty()) {
                    Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING, "The event bodies list is empty!");
                }
            }else{
                eventBodies = new UIntegerList(hdrlst.size());
                
                for (UpdateHeader hdrlst1 : hdrlst){
                    eventBodies.add(new UInteger());
                }
            }

            publisher.publish(hdrlst, objectDetailsList, eventBodies); // requirement: 3.7.2.15

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
    }

    /**
     * Publishes a set of Events through the Event service.
     *
     * @param sourceURI Source URI
     * @param objIds Object instance identifier
     * @param objType Object type
     * @param related Related link
     * @param sources Source link
     * @param eventBodies Bodies of the event
     */
    public void publishEvents(final URI sourceURI, final LongList objIds, final ObjectType objType,
            final Long related, final ObjectIdList sources, ElementList eventBodies) {
        // 3.3.2.1 , 3.3.2.2 , 3.3.2.3 , 3.3.2.4 , 3.3.2.5
        try {

            if (!isRegistered) {
                final EntityKeyList lst = new EntityKeyList();
                lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                publisher.register(lst, new PublishInteractionListener());
                isRegistered = true;
            }

//            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.FINE,
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.INFO,
                    "Publishing Event for the Event objIds: {0}; with Event Object Numbers: {1}",
                    new Object[]{objIds, objType.getNumber()});

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectDetailsList objectDetailsList = new ObjectDetailsList();

            for (int i = 0; i < objIds.size(); i++) {

                // 0xFFFF FFFF FF00 0000
                final Long secondEntityKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(objType);

                final Long subKey = (sources.get(i) != null) ? HelperCOM.generateSubKey(sources.get(i).getType()) : null;
                // requirements: 3.3.4.2.1 , 3.3.4.2.2 , 3.3.4.2.3 , 3.3.4.2.4
                final EntityKey ekey = new EntityKey(
                        new Identifier(objType.getNumber().toString()),
                        secondEntityKey,
                        objIds.get(i),
                        subKey);

                final Time timestamp = HelperTime.getTimestampMillis(); //  requirement: 3.3.4.2.7

                hdrlst.add(new UpdateHeader(timestamp, sourceURI, UpdateType.DELETION, ekey));
                objectDetailsList.add(new ObjectDetails(related, sources.get(i))); // requirement: 3.3.4.2.5
            }

            if (eventBodies != null) {
                if (eventBodies.isEmpty()) {
                    Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING, "The event bodies list is empty!");
                }
            }else{
                eventBodies = new UIntegerList(hdrlst.size());
                
                for (UpdateHeader hdrlst1 : hdrlst){
                    eventBodies.add(new UInteger());
                }
            }

            publisher.publish(hdrlst, objectDetailsList, eventBodies); // requirement: 3.7.2.15

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
    }

    public LongList generateAndStoreEvents(final ObjectType objType, final IdentifierList domain, 
            final LongList relateds, final ObjectIdList sourceList, final MALInteraction interaction) {

        ObjectDetailsList objectDetailsList = new ObjectDetailsList();

        for(int i = 0; i < sourceList.size(); i++){
            if(relateds != null){
                objectDetailsList.add(new ObjectDetails(relateds.get(i), sourceList.get(i)));
            }else{
                objectDetailsList.add(new ObjectDetails(null, sourceList.get(i)));
            }
        }

        ElementList events = null;
        Identifier network = null;
        URI uri = null;

        if (interaction != null) {
            network = interaction.getMessageHeader().getNetworkZone();
            uri = interaction.getMessageHeader().getURIFrom();
        }

        if (this.archiveService == null) {
            return null;
        }

        ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        
        for(int i = 0; i < objectDetailsList.size(); i++){
            ArchiveDetails archiveDetails = new ArchiveDetails();
            archiveDetails.setDetails(objectDetailsList.get(i));
            archiveDetails.setInstId(new Long(0)); // no need to worry about objIds
            archiveDetails.setTimestamp(HelperTime.getTimestamp());

            if (network != null) {
                archiveDetails.setNetwork(network);
            } else {
                archiveDetails.setNetwork(configuration.getNetwork());
            }

            if (uri != null) {
                archiveDetails.setProvider(uri);
            }else{
                archiveDetails.setProvider(connection.getConnectionDetails().getProviderURI());
            }

            archiveDetailsList.add(archiveDetails);
        }

        try {
            // requirement 3.3.4.2.8
            return this.archiveService.store(true, objType, domain, archiveDetailsList, events, null);
        } catch (MALException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    /**
     * Stores an Event in the Archive
     */
    private Long storeEventOnArchive(final ObjectDetailsList objectDetailsList, final IdentifierList domain,
            final ObjectType objType, final ElementList events, final MALInteraction interaction) {

        if (interaction != null) {
            return this.storeEventOnArchive(objectDetailsList, domain, objType,
                    events, interaction.getMessageHeader().getURIFrom(), interaction.getMessageHeader().getNetworkZone());
        } else {
            return this.storeEventOnArchive(objectDetailsList, domain, objType,
                    events, null, null);
        }

    }

    /**
     * Stores an Event in the Archive
     */
    private Long storeEventOnArchive(final ObjectDetailsList objectDetailsList, final IdentifierList domain,
            final ObjectType objType, final ElementList events, final URI uri, final Identifier network) {

        if (this.archiveService == null) {
            return null;
        }

        if (events == null) {
            // BUG: Do something.. or maybe not...
            // Currently being taken
        }

        ArchiveDetails archiveDetails = new ArchiveDetails();
        archiveDetails.setDetails(objectDetailsList.get(0));
        archiveDetails.setInstId(new Long(0)); // no need to worry about objIds

        if (network != null) {
            archiveDetails.setNetwork(network);
        } else {
            archiveDetails.setNetwork(configuration.getNetwork());
        }

        if (uri != null) {
            archiveDetails.setProvider(uri);
        } else {
            archiveDetails.setProvider(connection.getConnectionDetails().getProviderURI());
        }

        archiveDetails.setTimestamp(HelperTime.getTimestamp());

        ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(archiveDetails);

        try {
            // requirement 3.3.4.2.8
            return this.archiveService.store(true, objType, domain, archiveDetailsList, events, null).get(0);
        } catch (MALException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
