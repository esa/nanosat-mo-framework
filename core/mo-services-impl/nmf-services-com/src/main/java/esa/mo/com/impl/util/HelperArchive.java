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
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.archive.provider.ArchiveHandler;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperDomain;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * A Helper class for the COM Archive.
 */
public class HelperArchive {

    public static final Logger LOGGER = Logger.getLogger(HelperArchive.class.getName());

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
     * Generates an ArchiveDetailsList with one ArchiveDetails entry for a new
     * COM object. The object instance identifier is set to 0 (auto-assigned by
     * the archive), the network is taken from ConfigurationProviderSingleton,
     * and the timestamp is set to now.
     *
     * @param related Related field (id of the related object, or null)
     * @param source Source field (id of the object that caused creation, or null)
     * @param uri Provider URI
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related,
            final ObjectKey source, final URI uri) {
        return generateArchiveDetailsList(related, source, uri, 0L);
    }

    /**
     * Generates an ArchiveDetailsList with one ArchiveDetails entry. Use this
     * variant when updating an existing COM object and the object instance
     * identifier must be set explicitly.
     *
     * @param related Related field (id of the related object, or null)
     * @param source Source field (id of the object that caused creation, or null)
     * @param uri Provider URI
     * @param objId Object instance identifier
     * @return The ArchiveDetailsList object
     */
    public static ArchiveDetailsList generateArchiveDetailsList(final Long related,
            final ObjectKey source, final URI uri, final Long objId) {
        final Identifier network = ConfigurationProviderSingleton.getNetwork();
        final ArchiveDetails archiveDetails = new ArchiveDetails(objId,
                new ObjectLinks(related, source),
                network != null ? network : new Identifier(""),
                Time.now(),
                uri);
        final ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
        archiveDetailsList.add(archiveDetails);
        return archiveDetailsList;
    }

    /**
     * Get the object body of a retrieved COM object from the Archive
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objId The object instance identifier of the COM object
     * @return The object body of the retrieved COM object or null if no object
     * was returned
     */
    public static Element getObjectBodyFromArchive(final Object archiveService,
            final ObjectType objType, final IdentifierList domain, final Long objId) {
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
     * CPU-intensive to ensure that the returned object ordering is identical
     * with the requested ids. Use {@link getUnorderedArchiveCOMObjectList}
     * instead.
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objIds The List of object instance identifiers of the COM object
     * @return The List of object bodies of the retrieved COM objects or null if
     * no object was returned
     */
    @Deprecated
    public static HeterogeneousList getObjectBodyListFromArchive(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (HeterogeneousList) getFromArchive(archiveService, objType, domain, objIds, ToBeReturned.OBJECT_BODY, true);
    }

    /**
     * Get the ArchiveDetails object of a retrieved COM object from the Archive
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objId The object instance identifier of the COM object
     * @return The ArchiveDetails object of the retrieved COM objects or null if
     * no object was returned
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
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objIds The List of object instance identifiers of the COM objects
     * @return The list of ArchiveDetails objects of the retrieved COM objects
     * or null if no object was returned
     */
    public static ArchiveDetailsList getUnorderedArchiveDetailsListFromArchive(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (ArchiveDetailsList) getFromArchive(archiveService, objType, domain, objIds,
                ToBeReturned.ARCHIVE_DETAILS, false);
    }

    /**
     * Get the ArchiveDetails object list of a retrieved COM object from the
     * Archive
     *
     * @deprecated This method is no longer recommended for use as it is rather
     * CPU-intensive to ensure that the returned object ordering is identical
     * with the requested ids. Use
     * {@link getUnorderedArchiveDetailsListFromArchive} instead.
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objIds The List of object instance identifiers of the COM objects
     * @return The list of ArchiveDetails objects of the retrieved COM objects
     * or null if no object was returned
     */
    @Deprecated
    public static ArchiveDetailsList getArchiveDetailsListFromArchive(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (ArchiveDetailsList) getFromArchive(archiveService, objType, domain, objIds,
                ToBeReturned.ARCHIVE_DETAILS, true);
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
     * CPU-intensive to ensure that the returned object ordering is identical
     * with the requested ids.
     *
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objIds The List of object instance identifiers of the COM objects
     * to be retrieved
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
     * @param archiveService The Archive
     * @param objType The object Type of the COM object
     * @param domain The domain of the COM object
     * @param objIds The List of object instance identifiers of the COM objects
     * to be retrieved
     * @return The list of COM objects or null if no object was returned
     */
    public static List<ArchivePersistenceObject> getUnorderedArchiveCOMObjectList(Object archiveService,
            final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        return (List<ArchivePersistenceObject>) getFromArchive(archiveService, objType, domain, objIds,
                ToBeReturned.COM_OBJECT, false);
    }

    /**
     * Inner helper function used to retrieve COM objects from the Archive
     * either via local or remote adapter.
     */
    private static Object getFromArchive(final Object archiveService, final ObjectType objType,
            final IdentifierList domain, final LongList objIds, final ToBeReturned toBeReturned,
            final boolean sortReturnList) {
        if (archiveService == null) { // If there's no archive...
            LOGGER.log(Level.INFO, "The Archive service provided contains a null pointer!");
            return null;
        }
        if (objType == null) {
            LOGGER.log(Level.WARNING, "The objType is null!");
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
                LOGGER.log(Level.SEVERE,
                        "The Archive service provided ({0}) is not a supported class!",
                        archiveService.getClass().toString());
                return null;
            }
        } catch (MALInteractionException ex) {
            LOGGER.log(Level.INFO,
                    "(MALInteractionException) The object {0}, domain = {1}, objIds = {2} "
                    + "could not be retrieved from the Archive ({3})! A null will be returned!",
                    new Object[]{objType.toString(), HelperDomain.domain2domainId(domain),
                        objIds.toString(), archiveService.getClass().getSimpleName()});
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
                returnInstIdToListIndexMap.put(detailsList.get(i).getId(), i);
            }
            // Then iterate over requested object ids and insert results into the sorted
            // list in a matching order
            for (Long inputId : objIds) {
                sortedList.add(((List<Object>) ret).get(returnInstIdToListIndexMap.get(inputId)));
            }
            ret = sortedList;
        } catch (InstantiationException
                | IllegalAccessException
                | IllegalArgumentException
                | InvocationTargetException
                | NoSuchMethodException
                | SecurityException e) {
            LOGGER.log(Level.SEVERE, "Failed to sort the return list", e);
            ret = null;
        }
        return ret;
    }
}
