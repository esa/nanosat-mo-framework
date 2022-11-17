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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.provider.ArchiveHandler;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * A Helper class for the COM Archive.
 */
public class HelperArchive {
    public static final Logger LOGGER = Logger.getLogger(HelperArchive.class.getName());

    private enum ToBeReturned {
        OBJECT_BODY, ARCHIVE_DETAILS, COM_OBJECT
    }

    /**
     * Checks if the archiveDetails structure contains a null value in any of the
     * following fields: network, timestamp or provider
     *
     * @param archiveDetails
     *            The archive details object to be checked.
     * @return The boolean value of the comparison
     */
    public static Boolean archiveDetailsContainsNull(ArchiveDetails archiveDetails) {
        // Check if null
        return archiveDetails.getNetwork().getValue() == null || archiveDetails.getTimestamp() == null || archiveDetails
            .getProvider().getValue() == null;
    }

    /**
     * Checks if the archiveDetails structure contains a wildcard in any of the
     * following fields: network, timestamp or provider Null, "*" and 0 are
     * considered wildcards
     *
     * @param archiveDetails
     *            The archive details object to be checked.
     * @return The boolean value of the comparison
     */
    public static Boolean archiveDetailsContainsWildcard(ArchiveDetails archiveDetails) {
        if (archiveDetails == null) {
            return false;
        }

        // Check for nulls
        if (archiveDetails.getNetwork() == null || archiveDetails.getTimestamp() == null || archiveDetails
            .getProvider() == null) {
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
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object. The
     * object instance identifier will be set as 0. The operation will use the
     * submitted related, source and interaction fields to fill-in the object.
     *
     * @param related
     *            Related field
     * @param source
     *            Source field
     * @param interaction
     *            Interaction
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related, final ObjectId source,
        final MALInteraction interaction) {
        return generateArchiveDetailsList(related, source, interaction.getMessageHeader().getNetworkZone(), interaction
            .getMessageHeader().getURIFrom());
    }

    /**
     * Generates a ArchiveDetailsList structure with multiple ArchiveDetails
     * objects. The object instance identifier will be set as 0. The operation will
     * use the submitted related, source and interaction fields to fill-in the
     * objects.
     *
     * @param related
     *            Related fields
     * @param source
     *            Source field
     * @param interaction
     *            Interaction
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(LongList relatedIds, ObjectId source,
        MALInteraction interaction) {
        final ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();

        FineTime timestamp = HelperTime.getTimestamp();
        for (Long relatedId : relatedIds) {
            final ArchiveDetails archiveDetails = new ArchiveDetails();
            archiveDetails.setInstId(0L);
            archiveDetails.setDetails(new ObjectDetails(relatedId, source));
            archiveDetails.setNetwork(interaction.getMessageHeader().getNetworkZone());
            archiveDetails.setTimestamp(timestamp);
            archiveDetails.setProvider(interaction.getMessageHeader().getURIFrom());
            archiveDetailsList.add(archiveDetails);
        }

        return archiveDetailsList;
    }

    /**
     * Please consider using the same method but with the provider URI directly as
     * argument instead of the connectionDetails object
     *
     * @param related
     *            Related field
     * @param source
     *            Source field
     * @param connectionDetails
     *            The details of the connection
     * @return The ArchiveDetailsList object
     */
    @Deprecated
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related, final ObjectId source,
        final SingleConnectionDetails connectionDetails) {
        return generateArchiveDetailsList(related, source, connectionDetails.getProviderURI());
    }

    /**
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object. The
     * object instance identifier will be set as 0. The operation will use the
     * submitted related, source and connectionDetails fields to fill-in the object.
     * It will use the provider's network to fill in the network's field.
     *
     * @param related
     *            Related field
     * @param source
     *            Source field
     * @param provider
     *            Provider URI field
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related, final ObjectId source,
        final URI uri) {
        return generateArchiveDetailsList(related, source, ConfigurationProviderSingleton.getNetwork(), uri);
    }

    /**
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object. The
     * object instance identifier will be set as 0. The operation will use the
     * submitted related, source, network and provider fields to fill-in the object.
     * The fields network and provider are not set.
     *
     * @param related
     *            Related field
     * @param source
     *            Source field
     * @param network
     *            Network field
     * @param provider
     *            Provider URI field
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related, final ObjectId source,
        final Identifier network, final URI provider) {
        return generateArchiveDetailsList(related, source, ConfigurationProviderSingleton.getNetwork(), provider,
            HelperTime.getTimestamp());
    }

    /**
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object. The
     * object instance identifier will be set as 0. The operation will use the
     * submitted related, source, network and provider fields to fill-in the object.
     * The fields network and provider are not set.
     *
     * @param related
     *            Related field
     * @param source
     *            Source field
     * @param network
     *            Network field
     * @param provider
     *            Provider URI field
     * @param timestamp
     *            Timestamp field
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related, final ObjectId source,
        final Identifier network, final URI provider, final FineTime timestamp) {
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
     * Generates a ArchiveDetailsList structure with one ArchiveDetails object. The
     * object instance identifier will be set as 0. The operation will use the
     * submitted related, source, network, provider and objId fields to fill-in the
     * object. The fields network and provider are not set.
     *
     * @param related
     *            Related field
     * @param source
     *            Source field
     * @param network
     *            Network field
     * @param provider
     *            Provider field
     * @param objId
     *            Object instance identifier field
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related, final ObjectId source,
        final Identifier network, final URI provider, final Long objId) {
        final ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(related, source, network,
            provider);

        archiveDetailsList.get(0).setInstId(objId);

        return archiveDetailsList;
    }

    /**
     * Get the object body of a retrieved COM object from the Archive
     * 
     * @param archiveService
     *            The Archive
     * @param objType
     *            The object Type of the COM object
     * @param domain
     *            The domain of the COM object
     * @param objId
     *            The object instance identifier of the COM object
     * @return The object body of the retrieved COM object or null if no object was
     *         returned
     */
    public static Element getObjectBodyFromArchive(final Object archiveService, final ObjectType objType,
        final IdentifierList domain, final Long objId) {
        final LongList objIds = new LongList();
        objIds.add(objId);
        final ElementList objs = (ElementList) getFromArchive(archiveService, objType, domain, objIds,
            ToBeReturned.OBJECT_BODY, true);

        return (objs != null) ? (Element) objs.get(0) : null;
    }

    /**
     * Get a List of object bodies of a retrieved List of COM objects from the
     * Archive.
     *
     * @deprecated This method is no longer recommended for use as it is rather
     *             CPU-intensive to ensure that the returned object ordering is
     *             identical with the requested ids. Use
     *             {@link getUnorderedArchiveCOMObjectList} instead.
     * 
     * @param archiveService
     *            The Archive
     * @param objType
     *            The object Type of the COM object
     * @param domain
     *            The domain of the COM object
     * @param objIds
     *            The List of object instance identifiers of the COM object
     * @return The List of object bodies of the retrieved COM objects or null if no
     *         object was returned
     */
    @Deprecated
    public static ElementList getObjectBodyListFromArchive(Object archiveService, final ObjectType objType,
        final IdentifierList domain, final LongList objIds) {
        return (ElementList) getFromArchive(archiveService, objType, domain, objIds, ToBeReturned.OBJECT_BODY, true);
    }

    /**
     * Get the ArchiveDetails object of a retrieved COM object from the Archive
     * 
     * @param archiveService
     *            The Archive
     * @param objType
     *            The object Type of the COM object
     * @param domain
     *            The domain of the COM object
     * @param objId
     *            The object instance identifier of the COM object
     * @return The ArchiveDetails object of the retrieved COM objects or null if no
     *         object was returned
     */
    public static ArchiveDetails getArchiveDetailsFromArchive(Object archiveService, final ObjectType objType,
        final IdentifierList domain, final Long objId) {
        final LongList objIds = new LongList();
        objIds.add(objId);
        final ArchiveDetailsList archiveDetailsList = (ArchiveDetailsList) getFromArchive(archiveService, objType,
            domain, objIds, ToBeReturned.ARCHIVE_DETAILS, false);
        if (archiveDetailsList == null || archiveDetailsList.size() < 1) {
            return null;
        }
        return archiveDetailsList.get(0);
    }

    /**
     * Get the ArchiveDetails object list of a retrieved COM object from the
     * Archive. Note that the returned objects ordering is *not* guaranteed.
     *
     * @param archiveService
     *            The Archive
     * @param objType
     *            The object Type of the COM object
     * @param domain
     *            The domain of the COM object
     * @param objIds
     *            The List of object instance identifiers of the COM objects
     * @return The list of ArchiveDetails objects of the retrieved COM objects or
     *         null if no object was returned
     */
    public static ArchiveDetailsList getUnorderedArchiveDetailsListFromArchive(Object archiveService,
        final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (ArchiveDetailsList) getFromArchive(archiveService, objType, domain, objIds,
            ToBeReturned.ARCHIVE_DETAILS, false);
    }

    /**
     * Get the ArchiveDetails object list of a retrieved COM object from the Archive
     *
     * @deprecated This method is no longer recommended for use as it is rather
     *             CPU-intensive to ensure that the returned object ordering is
     *             identical with the requested ids. Use
     *             {@link getUnorderedArchiveDetailsListFromArchive} instead.
     *
     * @param archiveService
     *            The Archive
     * @param objType
     *            The object Type of the COM object
     * @param domain
     *            The domain of the COM object
     * @param objIds
     *            The List of object instance identifiers of the COM objects
     * @return The list of ArchiveDetails objects of the retrieved COM objects or
     *         null if no object was returned
     */
    @Deprecated
    public static ArchiveDetailsList getArchiveDetailsListFromArchive(Object archiveService, final ObjectType objType,
        final IdentifierList domain, final LongList objIds) {
        return (ArchiveDetailsList) getFromArchive(archiveService, objType, domain, objIds,
            ToBeReturned.ARCHIVE_DETAILS, true);
    }

    /**
     * Retrieved a COM object from the Archive
     *
     * @param archiveService
     *            The Archive
     * @param objType
     *            The object Type of the COM object
     * @param domain
     *            The domain of the COM object
     * @param objId
     *            The object instance identifier of the COM object to be retrieved
     * @return The COM object or null if no object was returned
     */
    public static ArchivePersistenceObject getArchiveCOMObject(Object archiveService, final ObjectType objType,
        final IdentifierList domain, final Long objId) {
        LongList objIds = new LongList();
        objIds.add(objId);

        List<ArchivePersistenceObject> archiveCOMobjectList = (List<ArchivePersistenceObject>) getFromArchive(
            archiveService, objType, domain, objIds, ToBeReturned.COM_OBJECT, false);

        if (archiveCOMobjectList == null || archiveCOMobjectList.size() < 1) {
            return null;
        }

        return archiveCOMobjectList.get(0);
    }

    /**
     * Retrieve a list of complete COM objects from the Archive.
     *
     * @deprecated This method is no longer recommended for use as it is rather
     *             CPU-intensive to ensure that the returned object ordering is
     *             identical with the requested ids.
     * 
     * @param archiveService
     *            The Archive
     * @param objType
     *            The object Type of the COM object
     * @param domain
     *            The domain of the COM object
     * @param objIds
     *            The List of object instance identifiers of the COM objects to be
     *            retrieved
     * @return The list of COM objects or null if no object was returned
     */
    @Deprecated
    public static List<ArchivePersistenceObject> getArchiveCOMObjectList(Object archiveService,
        final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (List<ArchivePersistenceObject>) getFromArchive(archiveService, objType, domain, objIds,
            ToBeReturned.COM_OBJECT, true);
    }

    /**
     * Retrieve a list of complete COM objects from the Archive. Note that the
     * returned objects ordering is *not* guaranteed.
     * 
     * @param archiveService
     *            The Archive
     * @param objType
     *            The object Type of the COM object
     * @param domain
     *            The domain of the COM object
     * @param objIds
     *            The List of object instance identifiers of the COM objects to be
     *            retrieved
     * @return The list of COM objects or null if no object was returned
     */
    public static List<ArchivePersistenceObject> getUnorderedArchiveCOMObjectList(Object archiveService,
        final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (List<ArchivePersistenceObject>) getFromArchive(archiveService, objType, domain, objIds,
            ToBeReturned.COM_OBJECT, false);
    }

    /**
     * Inner helper function used to retrieve COM objects from the Archive either
     * via local or remote adapter.
     */
    private static Object getFromArchive(final Object archiveService, final ObjectType objType,
        final IdentifierList domain, final LongList objIds, final ToBeReturned toBeReturned,
        final boolean sortReturnList) {
        if (archiveService == null) { // If there's no archive...
            LOGGER.log(Level.INFO, "The Archive service provided contains a null pointer!");
            return null;
        }
        Object ret = null;
        HelperArchiveRetrieveAdapterInterface adapter;

        try {
            if (archiveService instanceof ArchiveHandler) {
                adapter = new HelperLocalArchiveRetrieveAdapter(null, objType, domain);
                ((ArchiveHandler) archiveService).retrieve(objType, domain, objIds,
                    (HelperLocalArchiveRetrieveAdapter) adapter);
            } else if (archiveService instanceof ArchiveStub) {
                adapter = new HelperRemoteArchiveRetrieveAdapter(objType, domain);
                ((ArchiveStub) archiveService).retrieve(objType, domain, objIds,
                    (HelperRemoteArchiveRetrieveAdapter) adapter);
            } else {
                LOGGER.log(Level.SEVERE, "The Archive service provided ({0}) is not a supported class!", archiveService
                    .getClass().toString());
                return null;
            }
        } catch (MALInteractionException ex) {
            LOGGER.log(Level.INFO,
                "(MALInteractionException) The object {0}, domain = {1}, objIds = {2} could not be retrieved from the Archive ({3})! A null will be returned!",
                new Object[]{objType.toString(), HelperMisc.domain2domainId(domain), objIds.toString(), archiveService
                    .getClass().getSimpleName()});
            return null;
        } catch (MALException ex) {
            LOGGER.log(Level.INFO,
                "(MALException) The object could not be retrieved from the Archive! A null will be returned! {0}", ex);
            return null;
        }

        adapter.waitUntilReady();

        if (toBeReturned == ToBeReturned.OBJECT_BODY) { // Is it the object or the details of the object?
            ret = adapter.getObjectBodyList();
        }

        if (toBeReturned == ToBeReturned.ARCHIVE_DETAILS) {
            ret = adapter.getArchiveDetailsList();
        }

        if (toBeReturned == ToBeReturned.COM_OBJECT) {
            ret = adapter.getPersistenceObjectList();
        }
        // Ensure there is no wildcard in the requested list
        if (sortReturnList && ret != null && !objIds.contains(0L)) {
            ret = returnListSort(objIds, ret, adapter);
        }
        return ret;
    }

    // Suppress warnings of casting Object to List and ArrayList
    @SuppressWarnings("unchecked")
    private static Object returnListSort(final LongList objIds, Object ret,
        HelperArchiveRetrieveAdapterInterface adapter) {
        try {
            ArrayList<Object> sortedList = (ArrayList<Object>) ret.getClass().getConstructor().newInstance();
            ArchiveDetailsList detailsList = adapter.getArchiveDetailsList();
            TreeMap<Long, Integer> returnInstIdToListIndexMap = new TreeMap<>();
            // First generate mapping of returned ids into returned array index
            for (int i = 0; i < detailsList.size(); ++i) {
                returnInstIdToListIndexMap.put(detailsList.get(i).getInstId(), i);
            }
            // Then iterate over requested object ids and insert results into the sorted
            // list in a matching order
            for (Long inputId : objIds) {
                sortedList.add(((List<Object>) ret).get(returnInstIdToListIndexMap.get(inputId)));
            }
            ret = sortedList;
        } catch (InstantiationException |
                 IllegalAccessException |
                 IllegalArgumentException |
                 InvocationTargetException |
                 NoSuchMethodException |
                 SecurityException e) {
            LOGGER.log(Level.SEVERE, "Failed to sort the return list", e);
            ret = null;
        }
        return ret;
    }
}
