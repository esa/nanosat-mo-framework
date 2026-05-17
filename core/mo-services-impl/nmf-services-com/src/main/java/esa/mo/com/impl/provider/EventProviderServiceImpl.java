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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.util.HelperCOM;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.com.event.provider.EventInheritanceSkeleton;
import org.ccsds.moims.mo.com.event.provider.MonitorEventPublisher;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Event service Provider.
 */
public class EventProviderServiceImpl extends EventInheritanceSkeleton {

    private MALProvider eventServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final Object lock = new Object();
    private boolean isRegistered = false;
    private MonitorEventPublisher publisher;
    private final ConnectionProvider connection = new ConnectionProvider();
    private ArchiveProviderServiceImpl archiveService;

    /**
     * Initializes the Event service provider
     *
     * @param archiveService The COM Archive service provider.
     * @throws MALException On initialization error.
     */
    public synchronized void init(ArchiveProviderServiceImpl archiveService) throws MALException {
        long timestamp = System.currentTimeMillis();
        this.archiveService = archiveService;

        publisher = createMonitorEventPublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(), SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT, null, new UInteger(0));

        // shut down old service transport
        if (null != eventServiceProvider) {
            connection.closeAll();
        }

        eventServiceProvider = connection.startService(EventHelper.EVENT_SERVICE, true, this);
        running = true;
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.INFO,
                "Event service: READY! (" + timestamp + " ms)");
    }

    public void setArchiveService(ArchiveProviderServiceImpl archiveService) {
        this.archiveService = archiveService;
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    public MonitorEventPublisher getPublisher() {
        return publisher;
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
    private Long generateAndStoreEvent(final ObjectType objType, final IdentifierList domain,
            final Element eventObjBody, final Long related, final ObjectId source, final MALInteraction interaction,
            final URI uri, final Identifier network) {

        ObjectLinksList objectLinksList = new ObjectLinksList();
        objectLinksList.add(new ObjectLinks(related, source));
        HeterogeneousList events = new HeterogeneousList();

        if (eventObjBody != null) {  // Do we have a null as input?
            // Is it a list already?
            if (eventObjBody instanceof java.util.ArrayList) {
                events.addAll((ElementList) eventObjBody);    // Then just cast it to ElementList
            } else {
                // Else, convert it to ElementList
                events.add(eventObjBody);
            }
        }

        // Store it!!
        if (interaction != null) {
            return this.storeEventOnArchive(objectLinksList, domain, objType, events, interaction);
        } else {
            return this.storeEventOnArchive(objectLinksList, domain, objType, events, uri, network);
        }
    }

    /**
     * Publishes an Event through the Event service. The source URI must match
     * the source otherwise the event won't be published
     *
     * @param sourceURI Source URI
     * @param objId Object instance identifier
     * @param objType Object type
     * @param related Related link
     * @param source Source link
     * @param eventBody Body of the event
     * @throws java.io.IOException if it cannot publish the Event
     */
    public void publishEvent(final URI sourceURI, final Long objId, final ObjectType objType,
            final Long related, final ObjectId source, Element eventBody) throws IOException {
        // 3.3.2.1 , 3.3.2.2 , 3.3.2.3 , 3.3.2.4 , 3.3.2.5
        if (!running) {
            throw new IOException("The Event service is not running.");
        }

        try {
            synchronized (lock) {
                if (!isRegistered) {
                    publisher.registerWithDefaultKeys(new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.FINE,
                    "Publishing Event for the Event objId: {0}; with Event Object Number: {1}", new Object[]{objId, objType
                                .getNumber()});

            // area(16) | service(16) | version(8) | 0(24) — number carried separately in eventObjectNumber key
            final Long areaServiceVersionKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(objType);
            final Long sourceObjTypeKey = (source != null) ? HelperCOM.generateSubKey(source.getType()) : 0L;

            // requirements: 3.3.4.2.1 , 3.3.4.2.2 , 3.3.4.2.4
            /*
            final EntityKey ekey = new EntityKey(
                    new Identifier(objType.getNumber().toString()),
                    areaServiceVersionKey,
                    objId,
                    sourceObjTypeKey);

            NamedValueList subkeys = new NamedValueList();
            subkeys.add(new NamedValue(new Identifier("key1"), new Identifier(objType.getNumber().toString())));
            subkeys.add(new NamedValue(new Identifier("key2"), new Union(areaServiceVersionKey)));
            subkeys.add(new NamedValue(new Identifier("key3"), new Union(objId)));
            subkeys.add(new NamedValue(new Identifier("key4"), new Union(sourceObjTypeKey)));
             */
            final NullableAttributeList keyValues = new NullableAttributeList();
            keyValues.add(new NullableAttribute(new Union((long) objType.getNumber().getValue()))); // eventObjectNumber
            keyValues.add(new NullableAttribute(new Union(areaServiceVersionKey)));
            keyValues.add(new NullableAttribute(new Union(sourceObjTypeKey)));

            UpdateHeader updateHeader = new UpdateHeader(new Identifier(sourceURI.getValue()),
                    connection.getConnectionDetails().getDomain(), keyValues);
            ObjectLinks objectLinks = new ObjectLinks(related, source); // requirement: 3.3.4.2.5

            if (eventBody == null) {
                eventBody = new UInteger();
            }

            publisher.publish(updateHeader, objectLinks, objId, eventBody); // requirement: 3.7.2.15
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (0)", ex);
        } catch (MALException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (1)", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (2)", ex);
        }
    }

    /**
     * Publishes a set of Events through the Event service.
     *
     * @param sourceURI Source URI
     * @param objIds Object instance identifier
     * @param objType Object type
     * @param relateds Related links
     * @param sources Source links
     * @param eventBody Body of the event
     * @throws java.io.IOException if it cannot publish the Event
     */
    public void publishEvents(final URI sourceURI, final LongList objIds, final ObjectType objType,
            final LongList relateds, final ObjectIdList sources, Element eventBody) throws IOException {
        // 3.3.2.1 , 3.3.2.2 , 3.3.2.3 , 3.3.2.4 , 3.3.2.5
        if (!running) {
            throw new IOException("The Event service is not running.");
        }

        try {
            synchronized (lock) {
                if (!isRegistered) {
                    publisher.registerWithDefaultKeys(new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            /* Used only for debugging
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.INFO,
                    "Publishing Event for the Event objIds: {0}; with Event Object Numbers: {1}",
                    new Object[]{objIds, objType.getNumber()});
             */
            for (int i = 0; i < objIds.size(); i++) {
                // area(16) | service(16) | version(8) | 0(24) — number carried separately in eventObjectNumber key
                final Long areaServiceVersionKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(objType);
                final Long sourceObjTypeKey = (sources.get(i) != null)
                        ? HelperCOM.generateSubKey(sources.get(i).getType()) : 0L;

                // requirements: 3.3.4.2.1 , 3.3.4.2.2 , 3.3.4.2.4
                AttributeList keys = new AttributeList();
                keys.addAsJavaType((long) objType.getNumber().getValue()); // eventObjectNumber
                keys.addAsJavaType(areaServiceVersionKey);
                keys.addAsJavaType(sourceObjTypeKey);

                final Long related = (relateds == null) ? null : relateds.get(i);

                UpdateHeader updateHeader = new UpdateHeader(new Identifier(sourceURI.getValue()),
                        connection.getConnectionDetails().getDomain(), keys.getAsNullableAttributeList());
                ObjectLinks objectLinks = new ObjectLinks(related, sources.get(i)); // requirement: 3.3.4.2.5

                if (eventBody == null) {
                    eventBody = new UInteger();
                }

                publisher.publish(updateHeader, objectLinks, objIds.get(i), eventBody); // requirement: 3.7.2.15
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (0)", ex);
        } catch (MALException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (1)", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider (2)", ex);
        }
    }

    public LongList generateAndStoreEvents(final ObjectType objType, final IdentifierList domain,
            final LongList relateds, final ObjectIdList sourceList, final MALInteraction interaction) {
        ObjectLinksList objectLinksList = new ObjectLinksList(sourceList.size());

        for (int i = 0; i < sourceList.size(); i++) {
            Long related = (relateds != null) ? relateds.get(i) : null;
            objectLinksList.add(new ObjectLinks(related, sourceList.get(i)));
        }

        HeterogeneousList events = null;
        Identifier network = ConfigurationProviderSingleton.getNetwork();
        URI uri = null;

        if (interaction != null) {
            uri = interaction.getMessageHeader().getFromURI();
        }

        if (uri == null) {
            uri = connection.getConnectionDetails().getProviderURI();
        }

        if (this.archiveService == null) {
            return null;
        }

        ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList(objectLinksList.size());

        for (int i = 0; i < objectLinksList.size(); i++) {
            ArchiveDetails archiveDetails = new ArchiveDetails(new Long(0),
                    objectLinksList.get(i),
                    network,
                    Time.now(),
                    uri);

            archiveDetailsList.add(archiveDetails);
        }

        try {
            // requirement 3.3.4.2.8
            return this.archiveService.store(true, objType, domain, archiveDetailsList, events, null);
        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).warning(
                    "PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header,
                final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header,
                final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).warning(
                    "PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    /**
     * Stores an Event in the Archive
     */
    private Long storeEventOnArchive(final ObjectLinksList objectLinksList, final IdentifierList domain,
            final ObjectType objType, final HeterogeneousList events, final MALInteraction interaction) {
        if (interaction != null) {
            return this.storeEventOnArchive(objectLinksList, domain, objType,
                    events, interaction.getMessageHeader().getFromURI(), null);
        } else {
            return this.storeEventOnArchive(objectLinksList, domain, objType, events, null, null);
        }
    }

    /**
     * Stores an Event in the Archive
     */
    private Long storeEventOnArchive(final ObjectLinksList objectLinksList, final IdentifierList domain,
            final ObjectType objType, HeterogeneousList bodies, URI uri, Identifier network) {

        if (this.archiveService == null) {
            return null;
        }

        if (bodies != null && bodies.isEmpty()) {
            bodies = null;
        }

        network = (network != null) ? network : ConfigurationProviderSingleton.getNetwork();
        uri = (uri != null) ? uri : connection.getConnectionDetails().getProviderURI();

        ArchiveDetails details = new ArchiveDetails(0L,
                objectLinksList.get(0), network, Time.now(), uri);

        ArchiveDetailsList detailsList = new ArchiveDetailsList();
        detailsList.add(details);

        try {
            // requirement 3.3.4.2.8
            LongList objIds = this.archiveService.store(true, objType, domain, detailsList, bodies, null);
            return objIds.get(0);
        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != eventServiceProvider) {
                eventServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).log(
                    Level.WARNING, "Exception during close down of the provider.", ex);
        }
    }

    public static URI convertMALInteractionToURI(final MALInteraction interaction) {
        if (interaction != null) {
            if (interaction.getMessageHeader() != null) {
                return interaction.getMessageHeader().getToURI();
            }
        }

        return new URI("");
    }

}
