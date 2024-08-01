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
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.event.EventHelper;
import org.ccsds.moims.mo.com.event.EventServiceInfo;
import org.ccsds.moims.mo.com.event.provider.EventInheritanceSkeleton;
import org.ccsds.moims.mo.com.event.provider.MonitorEventPublisher;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.AttributeList;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.NullableAttribute;
import org.ccsds.moims.mo.mal.structures.NullableAttributeList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
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
            ConfigurationProviderSingleton.getNetwork(), SessionType.LIVE, ConfigurationProviderSingleton
                .getSourceSessionName(), QoSLevel.BESTEFFORT, null, new UInteger(0));

        // shut down old service transport
        if (null != eventServiceProvider) {
            connection.closeAll();
        }

        eventServiceProvider = connection.startService(EventServiceInfo.EVENT_SERVICE_NAME.toString(), EventHelper.EVENT_SERVICE, this);
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

        ObjectDetailsList objectDetailsList = new ObjectDetailsList();
        objectDetailsList.add(new ObjectDetails(related, source));
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
            return this.storeEventOnArchive(objectDetailsList, domain, objType, events, interaction);
        } else {
            return this.storeEventOnArchive(objectDetailsList, domain, objType, events, uri, network);
        }
    }

    /**
     * This method is deprecated! The sourceURI should be explicitly stated. The
     * extraction of it from the interaction object should happen on the layers
     * above. The broker won't publish the event if the source is not correct.
     * Publishes an Event through the Event service.
     *
     * @param interaction Interaction object
     * @param objId Object instance identifier
     * @param objType Object type
     * @param related Related link
     * @param source Source link
     * @param eventBodies Bodies of the event
     * @throws java.io.IOException if it cannot publish the Event
     */
    @Deprecated
    public void publishEvent(final MALInteraction interaction, final Long objId, final ObjectType objType,
        final Long related, final ObjectId source, final ElementList eventBodies) throws IOException {
        URI sourceURI = new URI("");

        if (interaction != null) {
            if (interaction.getMessageHeader() != null) {
                sourceURI = interaction.getMessageHeader().getToURI();
            }
        }

        // requirement: 3.3.2.1
        this.publishEvent(sourceURI, objId, objType, related, source, eventBodies);
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
                    IdentifierList keyNames = new IdentifierList();
                    keyNames.add(new Identifier("K1"));
                    keyNames.add(new Identifier("K2"));
                    keyNames.add(new Identifier("K3"));
                    keyNames.add(new Identifier("K4"));
                    AttributeTypeList keyTypes = new AttributeTypeList();
                    keyTypes.add(AttributeType.IDENTIFIER);
                    keyTypes.add(AttributeType.LONG);
                    keyTypes.add(AttributeType.LONG);
                    keyTypes.add(AttributeType.LONG);
                    publisher.register(keyNames, keyTypes, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.FINE,
                "Publishing Event for the Event objId: {0}; with Event Object Number: {1}", new Object[]{objId, objType
                    .getNumber()});

            // 0xFFFF FFFF FF00 0000
            final Long secondEntityKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(objType);

            final Long subKey = (source != null) ? HelperCOM.generateSubKey(source.getType()) : null;
            // requirements: 3.3.4.2.1 , 3.3.4.2.2 , 3.3.4.2.3 , 3.3.4.2.4
            /*
            final EntityKey ekey = new EntityKey(
                    new Identifier(objType.getNumber().toString()),
                    secondEntityKey,
                    objId,
                    subKey);

            NamedValueList subkeys = new NamedValueList();
            subkeys.add(new NamedValue(new Identifier("key1"), new Identifier(objType.getNumber().toString())));
            subkeys.add(new NamedValue(new Identifier("key2"), new Union(secondEntityKey)));
            subkeys.add(new NamedValue(new Identifier("key3"), new Union(objId)));
            subkeys.add(new NamedValue(new Identifier("key4"), new Union(subKey)));
             */

            final NullableAttributeList keyValues = new NullableAttributeList();
            keyValues.add(new NullableAttribute(new Identifier(objType.getNumber().toString())));
            keyValues.add(new NullableAttribute(new Union(secondEntityKey)));
            keyValues.add(new NullableAttribute(new Union(objId)));
            keyValues.add(new NullableAttribute(new Union(subKey)));

            UpdateHeader updateHeader = new UpdateHeader(new Identifier(sourceURI.getValue()),
                    connection.getConnectionDetails().getDomain(), keyValues);
            ObjectDetails objectDetails = new ObjectDetails(related, source); // requirement: 3.3.4.2.5

            if (eventBody == null) {
                eventBody = new UInteger();
            }

            publisher.publish(updateHeader, objectDetails, eventBody); // requirement: 3.7.2.15
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
                    IdentifierList keyNames = new IdentifierList();
                    keyNames.add(new Identifier("K1"));
                    keyNames.add(new Identifier("K2"));
                    keyNames.add(new Identifier("K3"));
                    keyNames.add(new Identifier("K4"));
                    AttributeTypeList keyTypes = new AttributeTypeList();
                    keyTypes.add(AttributeType.IDENTIFIER);
                    keyTypes.add(AttributeType.LONG);
                    keyTypes.add(AttributeType.LONG);
                    keyTypes.add(AttributeType.LONG);
                    publisher.register(keyNames, keyTypes, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            /* Used only for debugging
            Logger.getLogger(EventProviderServiceImpl.class.getName()).log(Level.INFO,
                    "Publishing Event for the Event objIds: {0}; with Event Object Numbers: {1}",
                    new Object[]{objIds, objType.getNumber()});
             */
            for (int i = 0; i < objIds.size(); i++) {
                // 0xFFFF FFFF FF00 0000
                final Long secondEntityKey = 0xFFFFFFFFFF000000L & HelperCOM.generateSubKey(objType);

                final Long subKey = (sources.get(i) != null) ? HelperCOM.generateSubKey(sources.get(i).getType()) :
                    null;
                // requirements: 3.3.4.2.1 , 3.3.4.2.2 , 3.3.4.2.3 , 3.3.4.2.4
                AttributeList keys = new AttributeList();
                keys.add(new Identifier(objType.getNumber().toString()));
                keys.addAsJavaType(secondEntityKey);
                keys.addAsJavaType(objIds.get(i));
                keys.addAsJavaType(subKey);

                final Long related = (relateds == null) ? null : relateds.get(i);

                UpdateHeader updateHeader = new UpdateHeader(new Identifier(sourceURI.getValue()),
                        connection.getConnectionDetails().getDomain(), keys.getAsNullableAttributeList());
                ObjectDetails objectDetails = new ObjectDetails(related, sources.get(i)); // requirement: 3.3.4.2.5

                if (eventBody == null) {
                    eventBody = new UInteger();
                }

                publisher.publish(updateHeader, objectDetails, eventBody); // requirement: 3.7.2.15
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
        ObjectDetailsList objectDetailsList = new ObjectDetailsList(sourceList.size());

        for (int i = 0; i < sourceList.size(); i++) {
            Long related = (relateds != null) ? relateds.get(i) : null;
            objectDetailsList.add(new ObjectDetails(related, sourceList.get(i)));
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

        ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList(objectDetailsList.size());

        for (int i = 0; i < objectDetailsList.size(); i++) {
            ArchiveDetails archiveDetails = new ArchiveDetails(new Long(0),
            objectDetailsList.get(i),
            network,
            FineTime.now(),
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
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
            throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).fine(
                "PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body,
            final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).warning(
                "PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
            throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).fine(
                "PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body,
            final Map qosProperties) throws MALException {
            Logger.getLogger(EventProviderServiceImpl.class.getName()).warning(
                "PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    /**
     * Stores an Event in the Archive
     */
    private Long storeEventOnArchive(final ObjectDetailsList objectDetailsList, final IdentifierList domain,
            final ObjectType objType, final HeterogeneousList events, final MALInteraction interaction) {
        if (interaction != null) {
            return this.storeEventOnArchive(objectDetailsList, domain, objType,
                    events, interaction.getMessageHeader().getFromURI(), null);
        } else {
            return this.storeEventOnArchive(objectDetailsList, domain, objType, events, null, null);
        }
    }

    /**
     * Stores an Event in the Archive
     */
    private Long storeEventOnArchive(final ObjectDetailsList objectDetailsList, final IdentifierList domain,
            final ObjectType objType, final HeterogeneousList events, URI uri, Identifier network) {

        if (this.archiveService == null) {
            return null;
        }

        if (events == null) {
            // BUG: Do something.. or maybe not...
            // Currently being taken
        }

        if (network == null) {
            network = ConfigurationProviderSingleton.getNetwork();
        }

        if (uri == null) {
            uri = connection.getConnectionDetails().getProviderURI();
        }
        
        ArchiveDetails archiveDetails = new ArchiveDetails(new Long(0),
            objectDetailsList.get(0), network, FineTime.now(), uri);
        
        /*
        archiveDetails.setDetails(objectDetailsList.get(0));
        archiveDetails.setInstId(new Long(0)); // no need to worry about objIds
        archiveDetails.setTimestamp(HelperTime.getTimestamp());

        if (network != null) {
            archiveDetails.setNetwork(network);
        } else {
            archiveDetails.setNetwork(ConfigurationProviderSingleton.getNetwork());
        }
        if (uri != null) {
            archiveDetails.setProvider(uri);
        } else {
            archiveDetails.setProvider(connection.getConnectionDetails().getProviderURI());
        }
*/
        
        ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(archiveDetails);

        try {
            // requirement 3.3.4.2.8
            return this.archiveService.store(true, objType, domain, archiveDetailsList, events, null).get(0);
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
            Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).log(Level.WARNING,
                "Exception during close down of the provider.", ex);
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
