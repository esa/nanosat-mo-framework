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
package esa.mo.com.impl.util;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.helpertools.helpers.HelperTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.provider.ArchiveHandler;
import org.ccsds.moims.mo.com.archive.provider.RetrieveInteraction;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALInvoke;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * A Helper class for the COM Archive.
 */
public class HelperArchive {

    private enum ToBeReturned {
        OBJECT_BODY, ARCHIVE_DETAILS, COM_OBJECT
    }

    /**
     * Checks if the archiveDetails structure contains a null value in any of
     * the following fields: network, timestamp or provider
     *
     * @param archiveDetails The archive details object to be checked.
     * @return The boolean value of the comparison
     */
    public static Boolean archiveDetailsContainsNull(ArchiveDetails archiveDetails) {
        // Check if null
        return archiveDetails.getNetwork().getValue() == null
                || archiveDetails.getTimestamp() == null
                || archiveDetails.getProvider().getValue() == null;
    }

    /**
     * Checks if the archiveDetails structure contains a wildcard in any of the
     * following fields: network, timestamp or provider Null, "*" and 0 are
     * considered wildcards
     *
     * @param archiveDetails The archive details object to be checked.
     * @return The boolean value of the comparison
     */
    public static Boolean archiveDetailsContainsWildcard(ArchiveDetails archiveDetails) {
        if (archiveDetails == null) {
            return false;
        }

        // Check for nulls
        if (archiveDetails.getNetwork() == null
                || archiveDetails.getTimestamp() == null
                || archiveDetails.getProvider() == null) {
            return true;
        }

        // Check if any of them have a wildcard
        if (archiveDetails.getNetwork().getValue().equals("*")) {
            return true;
        }

        if (archiveDetails.getTimestamp().getValue() == 0) {
            return true;
        }

        if (archiveDetails.getProvider().getValue().equals("*")) {
            return true;
        }

        return false;
    }

    /**
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object.
     * The object instance identifier will be set as 0. The operation will use
     * the submitted related, source and interaction fields to fill-in the
     * object.
     *
     * @param related Related field
     * @param source Source field
     * @param interaction Interaction
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related,
            final ObjectId source, final MALInteraction interaction) {
        return generateArchiveDetailsList(
            related,
            source,
            interaction.getMessageHeader().getNetworkZone(),
            interaction.getMessageHeader().getURIFrom());
    }

    /**
     * Please consider using the same method but with the provider URI directly
     * as argument instead of the connectionDetails object
     *
     * @param related Related field
     * @param source Source field
     * @param connectionDetails The details of the connection
     * @return The ArchiveDetailsList object
     */
    @Deprecated
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related,
            final ObjectId source, final SingleConnectionDetails connectionDetails) {
        return generateArchiveDetailsList(related, source,
            connectionDetails.getProviderURI());
    }

    /**
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object.
     * The object instance identifier will be set as 0. The operation will use
     * the submitted related, source and connectionDetails fields to fill-in the
     * object. It will use the provider's network to fill in the network's
     * field.
     *
     * @param related Related field
     * @param source Source field
     * @param provider Provider URI field
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related,
            final ObjectId source, final URI uri) {
        return generateArchiveDetailsList(
            related,
            source,
            ConfigurationProviderSingleton.getNetwork(),
            uri);
    }

    /**
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object.
     * The object instance identifier will be set as 0. The operation will use
     * the submitted related, source, network and provider fields to fill-in the
     * object. The fields network and provider are not set.
     *
     * @param related Related field
     * @param source Source field
     * @param network Network field
     * @param provider Provider URI field
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related,
            final ObjectId source, final Identifier network, final URI provider) {
        return generateArchiveDetailsList(
            related,
            source,
            ConfigurationProviderSingleton.getNetwork(),
            provider,
            HelperTime.getTimestamp());
    }

    /**
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object.
     * The object instance identifier will be set as 0. The operation will use
     * the submitted related, source, network and provider fields to fill-in the
     * object. The fields network and provider are not set.
     *
     * @param related Related field
     * @param source Source field
     * @param network Network field
     * @param provider Provider URI field
     * @param timestamp Timestamp field
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related,
            final ObjectId source, final Identifier network, final URI provider, final FineTime timestamp) {
        final ArchiveDetails archiveDetails = new ArchiveDetails();
        archiveDetails.setInstId(0L);
        archiveDetails.setDetails(new ObjectDetails(related, source));
        archiveDetails.setNetwork(network);
        archiveDetails.setTimestamp(timestamp);
        archiveDetails.setProvider(provider);

        final ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(archiveDetails);
        return archiveDetailsList;
    }

    /**
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object.
     * The object instance identifier will be set as 0. The operation will use
     * the submitted related, source, network, provider and objId fields to
     * fill-in the object. The fields network and provider are not set.
     *
     * @param related Related field
     * @param source Source field
     * @param network Network field
     * @param provider Provider field
     * @param objId Object instance identifier field
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related,
            final ObjectId source, final Identifier network, final URI provider, final Long objId) {
        final ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(
                related,
                source,
                network,
                provider
        );

        archiveDetailsList.get(0).setInstId(objId);

        return archiveDetailsList;
    }

    /**
     * Get the object body of a retrieved COM object from the Archive
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objId The object instance identifier of the COM object
     * @return The object body of the retrieved COM object or null if no object was returned
     */
    public static Element getObjectBodyFromArchive(final Object archiveService,
            final ObjectType objType, final IdentifierList domain, final Long objId) {
        final LongList objIds = new LongList();
        objIds.add(objId);
        final ElementList objs = (ElementList) getFromArchive(
                archiveService,
                objType,
                domain,
                objIds,
                ToBeReturned.OBJECT_BODY
        );

        return (objs != null) ? (Element) objs.get(0) : null;
    }

    /**
     * Get a List of object bodies of a retrieved List of COM objects from the
     * Archive
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objIds The List of object instance identifiers of the COM object
     * @return The List of object bodies of the retrieved COM objects or null if no object was returned
     */
    public static ElementList getObjectBodyListFromArchive(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (ElementList) getFromArchive(archiveService, objType, domain, objIds, ToBeReturned.OBJECT_BODY);
    }

    /**
     * Get the ArchiveDetails object of a retrieved COM object from the Archive
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objId The object instance identifier of the COM object
     * @return The ArchiveDetails object of the retrieved COM objects or null if no object was returned
     */
    public static ArchiveDetails getArchiveDetailsFromArchive(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final Long objId) {
        final LongList objIds = new LongList();
        objIds.add(objId);
        final ArchiveDetailsList archiveDetailsList = (ArchiveDetailsList) getFromArchive(archiveService, objType, domain, objIds, ToBeReturned.ARCHIVE_DETAILS);
        if (archiveDetailsList == null) {
            return null;
        }
        return archiveDetailsList.get(0);
    }

    /**
     * Get the ArchiveDetails object of a retrieved COM object from the Archive
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objIds The List of object instance identifiers of the COM objects
     * @return The list of ArchiveDetails objects of the retrieved COM objects or null if no object was returned
     */
    public static ArchiveDetailsList getArchiveDetailsListFromArchive(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (ArchiveDetailsList) getFromArchive(
                archiveService,
                objType,
                domain,
                objIds,
                ToBeReturned.ARCHIVE_DETAILS);
    }

    /**
     * Retrieved a COM object from the Archive
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objId The object instance identifier of the COM object to be
     * retrieved
     * @return The COM object or null if no object was returned
     */
    public static ArchivePersistenceObject getArchiveCOMObject(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final Long objId) {
        LongList objIds = new LongList();
        objIds.add(objId);

        List<ArchivePersistenceObject> archiveCOMobjectList = (List<ArchivePersistenceObject>) getFromArchive(
                archiveService,
                objType,
                domain,
                objIds,
                ToBeReturned.COM_OBJECT
        );

        if (archiveCOMobjectList == null) {
            return null;
        }

        return archiveCOMobjectList.get(0);
    }

    /**
     * Retrieve a list of COM objects from the Archive
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objIds The List of object instance identifiers of the COM objects
     * to be retrieved
     * @return The list of COM objects or null if no object was returned
     */
    public static List<ArchivePersistenceObject> getArchiveCOMObjectList(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (List<ArchivePersistenceObject>) getFromArchive(
                archiveService,
                objType,
                domain,
                objIds,
                ToBeReturned.COM_OBJECT
        );
    }

    private static Object getFromArchive(final Object archiveService, final ObjectType objType,
            final IdentifierList domain, final LongList objIds, final ToBeReturned toBeReturned) {
        if (archiveService == null) { // If there's no archive...
            Logger.getLogger(HelperArchive.class.getName()).log(Level.INFO,
                    "The Archive service provided contains a null pointer!");
            return null;
        }

        // This class will receive the retrieve object
        class ArchiveRetrieveAdapter1 extends RetrieveInteraction {

            private ElementList obj;
            private ElementList objDetails;

            public ArchiveRetrieveAdapter1(MALInvoke interaction) {
                super(interaction);
            }

            @Override
            public synchronized MALMessage sendResponse(ArchiveDetailsList objDetails,
                    ElementList objBodies) throws MALInteractionException, MALException {
                if (objBodies != null) {
                    if (!objBodies.isEmpty()) {
                        this.obj = objBodies;
                    }
                } else {
                    this.obj = null;
                }

                if (objDetails != null) {
                    if (!objDetails.isEmpty()) {
                        this.objDetails = (ElementList) objDetails;
                    }
                } else {
                    this.objDetails = null;
                }

                this.notify();  // Notify that it is over
                return null;
            }

            @Override
            public MALMessage sendAcknowledgement() throws MALInteractionException, MALException {
                return null;
            }

            public ElementList getObject() {
                return this.obj;
            }

            public ElementList getObjectDetails() {
                return this.objDetails;
            }

            public List<ArchivePersistenceObject> getCOMobject() {
                if (objDetails == null) {
                    return null;
                }

                List<ArchivePersistenceObject> all = new ArrayList<>();

                for (int i = 0; i < objDetails.size(); i++) {
                    ArchivePersistenceObject tmp = new ArchivePersistenceObject(
                            objType,
                            domain,
                            ((ArchiveDetails) objDetails.get(i)).getInstId(),
                            (ArchiveDetails) objDetails.get(i),
                            (Element) obj.get(i)
                    );

                    all.add(tmp);
                }

                return all;
            }

        }

        class ArchiveRetrieveAdapter2 extends ArchiveAdapter {

            private ElementList obj;
            private ElementList objDetails;
            private final Semaphore semaphore = new Semaphore(0);

            ArchiveRetrieveAdapter2() {
                super();
            }

            @Override
            public void retrieveAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
            }

            @Override
            public void retrieveAckErrorReceived(MALMessageHeader msgHeader,
                    MALStandardError error, Map qosProperties) {
                Logger.getLogger(HelperArchive.class.getName()).log(Level.SEVERE,
                        "The Archive returned the following error: {0}", error.toString());
                semaphore.release();
            }

            @Override
            public void retrieveResponseReceived(MALMessageHeader msgHeader,
                    ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {

                if (objBodies != null) {
                    if (!objBodies.isEmpty()) {
                        this.obj = objBodies;
                    }
                } else {
                    this.obj = null;
                }

                if (objDetails != null) {
                    if (!objDetails.isEmpty()) {
                        this.objDetails = (ElementList) objDetails;
                    }
                } else {
                    this.objDetails = null;
                }

                semaphore.release();
            }

            @Override
            public void retrieveResponseErrorReceived(MALMessageHeader msgHeader,
                    MALStandardError error, Map qosProperties) {
                Logger.getLogger(HelperArchive.class.getName()).log(Level.SEVERE,
                        "The Archive returned the following error: {0}", error.toString());

                semaphore.release();
            }

            public ElementList getObject() {
                return this.obj;
            }

            public ElementList getObjectDetails() {
                return this.objDetails;
            }

            public List<ArchivePersistenceObject> getCOMobject() {
                if (objDetails == null) {
                    return null;
                }

                List<ArchivePersistenceObject> all = new ArrayList<>();

                for (int i = 0; i < objDetails.size(); i++) {
                    Element objBody = null;

                    if (obj != null) {
                        objBody = (Element) obj.get(i);
                    }

                    ArchivePersistenceObject tmp = new ArchivePersistenceObject(
                            objType,
                            domain,
                            ((ArchiveDetails) objDetails.get(i)).getInstId(),
                            (ArchiveDetails) objDetails.get(i),
                            objBody
                    );

                    all.add(tmp);
                }

                return all;
            }

            private void waitUntilReady() {
                try {
                    semaphore.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(HelperArchive.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }

        // The submitted Archive is a provider instance...
        if (archiveService instanceof ArchiveHandler) {

            ArchiveRetrieveAdapter1 adapter = new ArchiveRetrieveAdapter1(null);

            Object obj = null;
            // Retrieve the object
            synchronized (adapter) {
                try {
                    ((ArchiveHandler) archiveService).retrieve(objType, domain, objIds, adapter);
                } catch (MALInteractionException ex2) {
                    Logger.getLogger(HelperArchive.class.getName()).log(Level.INFO,
                            "(MALInteractionException) The object {0}, domain = {1}, objIds = {2} could not be retrieved from the Archive! A null will be returned!",
                            new Object[]{objType.toString(), HelperMisc.domain2domainId(domain),
                                objIds.toString()});
                    return null;
                } catch (MALException ex2) {
                    Logger.getLogger(HelperArchive.class.getName()).log(Level.INFO,
                            "(MALException) The object could not be retrieved from the Archive! A null will be returned! {0}", ex2);
                    return null;
                }

                if (toBeReturned == ToBeReturned.OBJECT_BODY) {  // Is it the object or the details of the object?
                    obj = adapter.getObject();
                }

                if (toBeReturned == ToBeReturned.ARCHIVE_DETAILS) {
                    obj = adapter.getObjectDetails();
                }

                if (toBeReturned == ToBeReturned.COM_OBJECT) {
                    obj = adapter.getCOMobject();
                }
            }

            return obj;
        }

        // The submitted Archive is a consumer instance...
        else if (archiveService instanceof ArchiveStub) {
            ArchiveRetrieveAdapter2 adapter = new ArchiveRetrieveAdapter2();
            Object obj = null;

            try {
                ((ArchiveStub) archiveService).retrieve(objType, domain, objIds, adapter);
            } catch (MALInteractionException ex2) {
                Logger.getLogger(HelperArchive.class.getName()).log(Level.INFO,
                        "(debug code: 03) The object could not be retrieved from the Archive! A null will be returned! "
                        + "This problem usually occurs when there are problems on the layers below, either MAL or Transport Binding Layer.");
                return null;
            } catch (MALException ex2) {
                Logger.getLogger(HelperArchive.class.getName()).log(Level.INFO,
                        "(debug code: 04) The object could not be retrieved from the Archive! A null will be returned! {0}", ex2);
                return null;
            }

            adapter.waitUntilReady();

            if (toBeReturned == ToBeReturned.OBJECT_BODY) {  // Is it the object or the details of the object?
                obj = adapter.getObject();
            }

            if (toBeReturned == ToBeReturned.ARCHIVE_DETAILS) {
                obj = adapter.getObjectDetails();
            }

            if (toBeReturned == ToBeReturned.COM_OBJECT) {
                obj = adapter.getCOMobject();
            }

            return obj;
        }
        else
        {
            Logger.getLogger(HelperArchive.class.getName()).log(Level.SEVERE,
                    "The Archive service provided ({0}) is not a supported class!",
                    archiveService.getClass().toString());
        }

        return null;
    }

}
