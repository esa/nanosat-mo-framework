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

import esa.mo.com.impl.archive.db.SortByField;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.archive.provider.CountInteraction;
import org.ccsds.moims.mo.com.archive.provider.QueryInteraction;
import org.ccsds.moims.mo.com.archive.provider.RetrieveInteraction;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterSet;
import org.ccsds.moims.mo.com.archive.structures.QueryFilter;
import org.ccsds.moims.mo.com.archive.structures.QueryFilterList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;

/**
 * Archive service Provider.
 */
public class ArchiveProviderServiceImpl extends ArchiveInheritanceSkeleton {

    private final ArchiveManager manager = new ArchiveManager(null);
    private MALProvider archiveServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param eventService The Event service provider.
     * @throws MALException On initialization error.
     */
    public synchronized void init(EventProviderServiceImpl eventService) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ArchiveHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
            }
        }

        manager.setEventService(eventService);
        manager.init();

        // shut down old service transport
        if (null != archiveServiceProvider) {
            connection.closeAll();
        }

        archiveServiceProvider = connection.startService(ArchiveHelper.ARCHIVE_SERVICE_NAME.toString(), ArchiveHelper.ARCHIVE_SERVICE, false, this);
        running = true;
        initialiased = true;
        Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).info("Archive service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != archiveServiceProvider) {
                archiveServiceProvider.close();
            }

            manager.close();

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).log(Level.WARNING, 
                    "Exception during close down of the provider {0}", ex);
        }
    }

    public void setEventService(EventProviderServiceImpl eventService) {
        manager.setEventService(eventService);
    }

    /**
     * Reset operation
     */
    public void reset() {
        manager.resetTable();
    }

    /**
     * This method should only be used by the Archive Sync service. Any other
     * calls should go through the COM Archive interface and not this one.
     *
     * @return The Archive Manager.
     */
    public ArchiveManager getArchiveManager() {
        return manager;
    }

    @Override
    public void retrieve(final ObjectType inObjectType, final IdentifierList inDomain,
            final LongList inObjIds, final RetrieveInteraction interaction)
            throws MALInteractionException, MALException {
        interaction.sendAcknowledgement();  // "ok, it was received.."
        UIntegerList unkIndexList = new UIntegerList();
        LongList longList = new LongList();

        if (inObjectType == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null)); // requirement 3.4.3.2.1
        }
        if (inDomain == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null)); // requirement: 3.4.3.2.3
        }
        if (inObjIds == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }
        if (ArchiveManager.objectTypeContainsWildcard(inObjectType)) {   // requirement: 3.4.3.2.2
//            interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        if (HelperCOM.domainContainsWildcard(inDomain)) {   // requirement: 3.4.3.2.4
//            interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        for (Long tempObjId : inObjIds) { // requirement: 3.4.3.2.5
            if (tempObjId == 0) {  // Is it the wildcard 0? requirement: 3.4.3.2.6
                longList.clear();  // if the wildcard is in the middle of the input list, we clear the list...
                longList.addAll(manager.getAllObjIds(inObjectType, inDomain)); // ... add all
                break;
            }
            longList.add(tempObjId);
        }

        ArchiveDetailsList outArchiveDetailsList = new ArchiveDetailsList();
        ElementList outMatchedObjects = null;

        for (int index = 0; index < longList.size(); index++) {  // Let's go one by one in the list
            Long objId = longList.get(index);

            ArchivePersistenceObject perObj = manager.getPersistenceObject(inObjectType, inDomain, objId);

            if (perObj == null) {  // COM object not found
                unkIndexList.add(new UInteger(index)); // requirement: 3.4.3.2.7
                outArchiveDetailsList.add(null); // requirement: 3.4.3.2.12

                if (outMatchedObjects != null) {
                    outMatchedObjects.add(null);
                }

                continue;
            }

            outArchiveDetailsList.add(perObj.getArchiveDetails()); // requirement: 3.4.3.2.9

            if (outMatchedObjects == null) {  // Initialize the elementList object
                try {
                    outMatchedObjects = HelperMisc.element2elementList(perObj.getObject());

                    if (outMatchedObjects != null) { // Was it created?
                        for (int j = 0; j < index; j++) { // Insert the missing elements in the list
                            outMatchedObjects.add(null);
                        }
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    interaction.sendError(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, "The List of the objects could not be generated!"));
                    interaction.sendResponse(null, null);
                    return;
                }
            }

            if (outMatchedObjects != null) {
                outMatchedObjects.add(perObj.getObject()); // requirement: 3.4.3.2.10 and 3.4.3.2.11
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.4.3.3 (error: a)
//            interaction.sendError(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.4.3.2.13 and requirement: 3.4.3.2.14: ordering of objects is not specified
        if (outArchiveDetailsList.isEmpty()) {
            interaction.sendResponse(null, null);  // requirement: 3.4.3.2.12
        } else {
            interaction.sendResponse(outArchiveDetailsList, outMatchedObjects); // requirement: 3.4.3.2.8
        }
    }

    @Override
    public void query(Boolean returnObjBody, final ObjectType lObjectType,
            final ArchiveQueryList lArchiveQueryList, final QueryFilterList queryFilterList,
            final QueryInteraction interaction) throws MALException, MALInteractionException {
        UIntegerList invIndexList = new UIntegerList();

        interaction.sendAcknowledgement();  // "ok, it was received.."

        // Is the list empty?
        if (lArchiveQueryList.isEmpty()) {
            interaction.sendResponse(null, null, null, null);  // requirement: 3.4.4.2.29
            return;
        }

        if (returnObjBody == null) {  // Handle it as a false
            returnObjBody = false;  // requirement: 3.4.4.2.25
//            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }
        if (lObjectType == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));  // requirement: 3.4.4.2.2 and 3.4.4.2.3
        }
        if (lArchiveQueryList == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));  // requirement: 3.4.4.2.4 and 3.4.4.2.5
        }

        if (queryFilterList != null) { // requirement: 3.4.4.2.8
            if (lArchiveQueryList.size() != queryFilterList.size()) { // requirement: 3.4.4.2.9
//                interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
                throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
            }
        }

        ArchiveQuery tmpArchiveQuery;
        QueryFilter tmpQueryFilter = null;
        final int sizeArchiveQueryList = lArchiveQueryList.size();

        // Go through all the archiveQueries, one by one
        for (int index = 0; index < sizeArchiveQueryList; index++) { // requirement: 3.4.4.2.6
            tmpArchiveQuery = lArchiveQueryList.get(index);
            ArrayList<ArchivePersistenceObject> perObjs;

            if (queryFilterList != null) {
                tmpQueryFilter = (QueryFilter) queryFilterList.get(index);
            }

            // Query the objects
            // requirement: 3.4.4.2.11 (taken care internally)
            perObjs = manager.query(lObjectType, tmpArchiveQuery, tmpQueryFilter); // requirement: 3.4.4.2.10
            // requirement: 3.4.4.2.15

            if (queryFilterList != null) { // requirement: 3.4.4.2.8
                if (tmpQueryFilter instanceof CompositeFilterSet) {
                    try {
                        if (tmpQueryFilter != null) { // requirement: 3.4.4.2.7
                            perObjs = ArchiveManager.filterQuery(perObjs, (CompositeFilterSet) tmpQueryFilter);  // requirement: 3.4.4.2.10
                        }
                    } catch (SecurityException | IllegalArgumentException ex) {
                        invIndexList.add(new UInteger(index));
                    }
                }
            }

            // requirement: 3.4.4.2.12  ("Gimme only the latest!")
            if (tmpArchiveQuery.getEndTime() != null && tmpArchiveQuery.getStartTime() == null && perObjs.size() > 0) {
                ArchivePersistenceObject latestPerObj = perObjs.get(0);

                for (ArchivePersistenceObject perObj : perObjs) {  // Cycle the perObjs to find the latest
                    if (latestPerObj.getArchiveDetails().getTimestamp().getValue() < perObj.getArchiveDetails().getTimestamp().getValue()) {
                        latestPerObj = perObj; // It is newer than the current
                    }
                }
                perObjs = new ArrayList<>();
                perObjs.add(latestPerObj);
            }

            // Sort the objects
            if (tmpArchiveQuery.getSortOrder() != null) {
                try { // requirement: 3.4.4.2.26
                    perObjs = SortByField.sortPersistenceObjects(perObjs, tmpArchiveQuery.getSortFieldName(), tmpArchiveQuery.getSortOrder());
                } catch (NoSuchFieldException ex) {
                    // requirement: 3.4.4.2.14
                    throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
//                    throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, new UInteger(index)));
//                    interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
                }
            }

            // Errors
            if (!invIndexList.isEmpty()) { // requirement: 3.4.4.3 (error: a, b)
//                interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
                if (index == (sizeArchiveQueryList - 1)) { // Is it the last query?
                    throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
                } else {
                    continue;
                }

            }

            // Is the list empty? and it is the last query?
            if (perObjs.isEmpty() && index == (sizeArchiveQueryList - 1)) {
                interaction.sendResponse(null, null, null, null);  // requirement: 3.4.4.2.29
                return;
            }

            // requirement: 3.4.4.2.18 and requirement 3.4.4.2.21
            if (ArchiveManager.objectTypeContainsWildcard(lObjectType)
                    || HelperCOM.domainContainsWildcard(lArchiveQueryList.get(index).getDomain())) {  // Any wilcards? if so, then send the updates separately

                // Then we need to send data sequentially... object by object
                for (int j = 0; j < perObjs.size(); j++) {
                    // requirement: 3.4.4.2.21
                    ElementList outObjectList = null;

                    ArchiveDetailsList outArchDetLst = new ArchiveDetailsList();
                    outArchDetLst.add(perObjs.get(j).getArchiveDetails());

                    if (returnObjBody) {
                        // requirement: 3.4.4.2.1
                        try {  // Let's try to generate the list...
                            outObjectList = HelperMisc.element2elementList(perObjs.get(j).getObject());
                        } catch (Exception ex) { // The list could not be generated
                            Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).log(Level.SEVERE, "The outObjectList could not be generated!", ex);
                            continue;
                        }

                        if (outObjectList != null) {
                            outObjectList.add(perObjs.get(j).getObject()); // requirement: 3.4.4.2.24
                        }
                    }

                    // requirement: 3.4.4.2.19
                    ObjectType objType = (ArchiveManager.objectTypeContainsWildcard(lObjectType)) ? perObjs.get(j).getObjectType() : null;
//                    ObjectType objType = (ArchiveManager.objectTypeContainsWildcard(lObjectType)) ? perObjs.get(j).getObjectType() : lObjectType;

                    if (j != (perObjs.size() - 1) || index != (sizeArchiveQueryList - 1)) {
                        // requirement: 3.4.4.2.18
                        interaction.sendUpdate(objType, perObjs.get(j).getDomain(),
                                outArchDetLst, outObjectList); // requirement: 3.4.4.2.17 and 3.4.4.2.23
                    } else {
                        interaction.sendResponse(objType, perObjs.get(j).getDomain(),
                                outArchDetLst, outObjectList); // requirement: 3.4.4.2.17 and 3.4.4.2.23
                    }
                }

            } else {
                // Here: Same Domain and Object Type for all objects
                // Find the objIds based on the query
                // requirement: 3.4.4.2.10 and 3.4.4.2.11 and 3.4.4.2.12 and 3.4.4.2.13 and 3.4.4.2.15 and 3.4.4.2.16
                ArchiveDetailsList outArchiveDetailsList = new ArchiveDetailsList();
                ElementList outObjectList = null;

                if (returnObjBody && !perObjs.isEmpty()) { // requirement: 3.4.4.2.24
                    try {
                        outObjectList = HelperMisc.element2elementList(perObjs.get(0).getObject());
                    } catch (Exception ex) {
                        Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                for (int j = 0; j < perObjs.size(); j++) {  // Make the output lists
                    outArchiveDetailsList.add(perObjs.get(j).getArchiveDetails());

                    if (outObjectList != null) {
                        outObjectList.add(perObjs.get(j).getObject());
                    }
                }

                if (index != (sizeArchiveQueryList - 1)) {
                    // requirement: 3.4.4.2.19
                    interaction.sendUpdate(null, lArchiveQueryList.get(index).getDomain(), outArchiveDetailsList, outObjectList); // requirement: 3.4.4.2.17
                } else {
                    // requirement: 3.4.4.2.19
                    interaction.sendResponse(null, lArchiveQueryList.get(index).getDomain(), outArchiveDetailsList, outObjectList); // requirement: 3.4.4.2.17
                }
            }
        }
    }

    @Override
    public void count(final ObjectType lObjectType, final ArchiveQueryList lArchiveQueryList,
            final QueryFilterList queryFilterList, final CountInteraction interaction)
            throws MALException, MALInteractionException { // requirement: 3.4.5.2.1
        UIntegerList invIndexList = new UIntegerList();
        LongList outLong = new LongList();
        interaction.sendAcknowledgement();  // "ok, it was received.."

        // Is the list empty?
        if (lArchiveQueryList.isEmpty()) {
            interaction.sendResponse(null);  // requirement: 3.4.4.2.29
            return;
        }

        if (lObjectType == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null)); // requirement: 3.4.5.2.1
        }

        if (lArchiveQueryList == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null)); // requirement: 3.4.5.2.1
        }

        if (queryFilterList != null) { // requirement: 3.4.5.2.1

            if (lArchiveQueryList.size() != queryFilterList.size()) { // requirement: 3.4.5.2.1
//                interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
                throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
            }
        }

        ArchiveQuery tmpArchiveQuery;
        QueryFilter tmpQueryFilter = null;
        final int sizeArchiveQueryList = lArchiveQueryList.size();

        for (int index = 0; index < sizeArchiveQueryList; index++) { // requirement: 3.4.5.2.3 and 3.4.5.2.4
            tmpArchiveQuery = lArchiveQueryList.get(index);
            ArrayList<ArchivePersistenceObject> perObjs;

            if (queryFilterList != null) {
                tmpQueryFilter = (QueryFilter) queryFilterList.get(index);
            }

            // Query the objects
            perObjs = manager.query(lObjectType, tmpArchiveQuery, tmpQueryFilter);

            if (queryFilterList != null) {
                if (tmpQueryFilter instanceof CompositeFilterSet) {
                    try {
                        if (tmpQueryFilter != null) { // requirement: 3.4.4.2.7
                            perObjs = ArchiveManager.filterQuery(perObjs, (CompositeFilterSet) tmpQueryFilter);  // requirement: 3.4.4.2.10
                        }
                    } catch (SecurityException | IllegalArgumentException ex) {
                        invIndexList.add(new UInteger(index));
                    }
                }
            }

            // requirement: 3.4.4.2.12  ("Gimme only the latest!")
            if (tmpArchiveQuery.getEndTime() != null && tmpArchiveQuery.getStartTime() == null && perObjs.size() > 0) {
                ArchivePersistenceObject latestPerObj = perObjs.get(0);

                for (ArchivePersistenceObject perObj : perObjs) {  // Cycle the perObjs to find the latest
                    if (latestPerObj.getArchiveDetails().getTimestamp().getValue() < perObj.getArchiveDetails().getTimestamp().getValue()) {
                        latestPerObj = perObj; // It is newer than the current
                    }
                }
                perObjs = new ArrayList<>();
                perObjs.add(latestPerObj);
            }

            // Sort the objects  (just to be sure there are no errors...
            if (tmpArchiveQuery.getSortOrder() != null) {
                try { // requirement: 3.4.4.2.26
                    perObjs = SortByField.sortPersistenceObjects(perObjs, tmpArchiveQuery.getSortFieldName(), tmpArchiveQuery.getSortOrder());
                } catch (NoSuchFieldException ex) {
                    // requirement: 3.4.4.2.14
                    throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
                }
            }

            outLong.add((long) perObjs.size()); // requirement: 3.4.5.2.2
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.4.4.3 (error: a, b)
//            interaction.sendError(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        interaction.sendResponse(outLong);
    }

    @Override
    public LongList store(final Boolean returnObjId, final ObjectType objType,
            final IdentifierList domain, final ArchiveDetailsList lArchiveDetailsList,
            final ElementList lElementList, final MALInteraction interaction)
            throws MALException, MALInteractionException {
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList;

        // What if the list is null?
        if (lArchiveDetailsList == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        if (lElementList != null) {

            if (lArchiveDetailsList.size() != lElementList.size()) { // requirement: 3.4.6.2.8
                UIntegerList error = new UIntegerList();
                int size1 = (lArchiveDetailsList.size() < lElementList.size()) ? lArchiveDetailsList.size() : lElementList.size();
                int size2 = (lArchiveDetailsList.size() > lElementList.size()) ? lArchiveDetailsList.size() : lElementList.size();

                for (int i = size1; i < size2; i++) { // make a list with the invalid indexes
                    error.add(new UInteger(i));
                }

                throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, error));
            }

        }

        if (ArchiveManager.objectTypeContainsWildcard(objType)) { // requirement: 3.4.6.2.9
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        if (HelperCOM.domainContainsWildcard(domain)) { // requirement: 3.4.6.2.10
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        // Do we have Duplicates in the objId array?
        dupIndexList = ArchiveManager.checkForDuplicates(lArchiveDetailsList);

        if (!dupIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        synchronized (manager) {
            for (int index = 0; index < lArchiveDetailsList.size(); index++) { // Validation of ArchiveDetails object
                if (lArchiveDetailsList.get(index).getInstId() == 0) { // requirement: 3.4.6.2.5
                    // Shall be taken care in the manager & per inserted entry
                } else { // Does it exist already?  // requirement: 3.4.6.2.6
                    if (manager.objIdExists(objType, domain, lArchiveDetailsList.get(index).getInstId())) {
                        dupIndexList.add(new UInteger(index));
                        continue;
                    }
                }

                if (HelperArchive.archiveDetailsContainsWildcard(lArchiveDetailsList.get(index))) { // requirement: 3.4.6.2.11
                    invIndexList.add(new UInteger(index));
                    //                continue;
                }

                // There's a requirement missing: 3.4.6.2.12
                // Can only be made after the JAVA API supports COM features: https://github.com/SamCooper/JAVA_SPEC_RIDS/issues/2
                /*
                if (lElementList != null) {
                    if (!manager.isObjectTypeLikeDeclaredServiceType(lObjectType, (Element) lElementList.get(index))
                            && lElementList.get(index) != null) { // requirement: 3.4.6.2.12
                        invIndexList.add(new UInteger(index));
                        continue;
                    }
                }
                 */
            }

            // Errors
            if (!invIndexList.isEmpty()) { // requirement: 3.4.6.3 (error: a)
                throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
            }

            if (!dupIndexList.isEmpty()) { // requirement: 3.4.6.3 (error: b)
                throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
            }

            // The errors have to be before the store operation to fulfil requirement: 3.4.6.2.13
            if (returnObjId) { // requirement: 3.4.6.2.1 and 3.4.6.2.14
                // Execute the store operation (objType, domain, archiveDetails, objs)
                // requirement: 3.4.6.2.15 (the operation returns the objIds with the same order)
                return manager.insertEntries(objType, domain, lArchiveDetailsList, lElementList, interaction);
            } else {
                // Cannot be Threaded because is does not lock the access to the db and out of order will happen
                manager.insertEntriesFast(objType, domain, lArchiveDetailsList, lElementList, interaction); // requirement: 3.4.6.2.15
                return null;
            }
        }
    }

    @Override
    public void update(final ObjectType lObjectType,
            final IdentifierList domain,
            final ArchiveDetailsList lArchiveDetailsList,
            final ElementList lElementList,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (lObjectType == null) {
            return; // requirement: 3.4.4.2.1
        }
        if (domain == null) {
            return; // requirement: 3.4.4.2.2
        }
        if (lArchiveDetailsList == null) {
            return; // requirement: 3.4.4.2.3
        }
        if (ArchiveManager.objectTypeContainsWildcard(lObjectType)
                || HelperCOM.domainContainsWildcard(domain)) {   // requirement: 3.4.7.2.8 (first part)
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        if (lArchiveDetailsList.size() != lElementList.size()) { // requirement: ------ (proposed, does not exist yet)
            UIntegerList error = new UIntegerList();
            int size1 = (lArchiveDetailsList.size() < lElementList.size()) ? lArchiveDetailsList.size() : lElementList.size();
            int size2 = (lArchiveDetailsList.size() > lElementList.size()) ? lArchiveDetailsList.size() : lElementList.size();

            for (int i = size1; i < size2; i++) { // make a list with the invalid indexes
                error.add(new UInteger(i));
            }

            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, error));
        }

        synchronized (manager) {
            for (int index = 0; index < lArchiveDetailsList.size(); index++) {
                ArchiveDetails tmpArchiveDetails = lArchiveDetailsList.get(index);

                if (tmpArchiveDetails.getInstId() == 0) { // requirement: 3.4.7.2.8 (second part)
                    invIndexList.add(new UInteger(index));
                    continue;
                }

                if (!manager.objIdExists(lObjectType, domain, tmpArchiveDetails.getInstId())) { // requirement: 3.4.7.2.4
                    unkIndexList.add(new UInteger(index)); // requirement: 3.4.7.2.5
                }
            }

            // Errors
            if (!unkIndexList.isEmpty()) { // requirement: 3.4.7.3 (error: a)
                throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            }

            if (!invIndexList.isEmpty()) { // requirement: 3.4.7.3 (error: b)
                throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
            }

            // The errors have to be before the update operation to fulfil requirement: 3.4.7.2.5 and 3.4.7.2.8 ("nothing will be updated")
            manager.updateEntries(lObjectType, domain, lArchiveDetailsList, lElementList, interaction); // requirement: 3.4.7.2.6 and 3.4.7.2.7
        }
    }

    @Override
    public LongList delete(final ObjectType lObjectType,
            final IdentifierList lIdentifierList,
            final LongList lLongList,
            final MALInteraction interaction)
            throws MALException, MALInteractionException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        LongList toBeDeleted = new LongList();

        if (ArchiveManager.objectTypeContainsWildcard(lObjectType)) { // requirement: 3.4.8.2.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null)); // requirement: 3.4.8.2.3
        }

        if (HelperCOM.domainContainsWildcard(lIdentifierList)) { // requirement: 3.4.8.2.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null)); // requirement: 3.4.8.2.3
        }

        synchronized (manager) {
            for (int index = 0; index < lLongList.size(); index++) {
                Long tempObjId = lLongList.get(index);
                if (tempObjId == 0) {  // Is it the wildcard 0? requirement: 3.4.8.2.5
                    toBeDeleted.clear();  // if the wildcard is in the middle of the input list, we clear the list...
                    toBeDeleted.addAll(manager.getAllObjIds(lObjectType, lIdentifierList)); // ... add all
                    break;
                }
                if (!manager.objIdExists(lObjectType, lIdentifierList, tempObjId)) {
                    unkIndexList.add(new UInteger(index)); // requirement: 3.4.8.2.6
                    continue;
                }

                toBeDeleted.add(tempObjId);
            }

            // Errors
            if (!unkIndexList.isEmpty()) { // requirement: 3.4.8.3 (error: a)
                throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            }

            if (!invIndexList.isEmpty()) { // requirement: 3.4.8.3 (error: b)
                throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
            }

            // requirement: 3.4.8.2.4 and 3.4.8.2.7
            return manager.removeEntries(lObjectType, lIdentifierList, toBeDeleted, interaction); // requirement: 3.4.8.2.8
        }
    }

    public ConnectionProvider getConnection() {
        return this.connection;
    }
}
