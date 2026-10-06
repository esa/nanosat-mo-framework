/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.DuplicateException;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.ArchiveServiceInfo;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.archive.provider.CountInteraction;
import org.ccsds.moims.mo.com.archive.provider.QueryInteraction;
import org.ccsds.moims.mo.com.archive.provider.RetrieveInteraction;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.UnknownException;

/**
 * Archive service Provider.
 */
public class ArchiveProviderServiceImpl extends ArchiveInheritanceSkeleton {

    private final ConnectionProvider connection = new ConnectionProvider();
    private final ArchiveManager manager = new ArchiveManager();
    private MALProvider archiveServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;

    /**
     * Default constructor.
     */
    public ArchiveProviderServiceImpl() {
    }

    /**
     * Creates the MAL objects and starts the Archive service provider.
     *
     * @throws MALException On initialization error.
     */
    public synchronized void init() throws MALException {
        long timestamp = System.currentTimeMillis();

        manager.init();

        // shut down old service transport
        if (null != archiveServiceProvider) {
            connection.closeAll();
        }

        archiveServiceProvider = connection.startService(ArchiveServiceInfo.ARCHIVE_SERVICE_NAME.toString(),
                ArchiveHelper.ARCHIVE_SERVICE, false, this);
        running = true;
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).info(
                "Archive service: READY! (" + timestamp + " ms)");
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

    /**
     * Wipes the entire archive clean. Used mainly by the tests.
     */
    public void wipe() {
        manager.wipe();
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
            throws InvalidArgumentException, UnknownException, MALException {
        interaction.sendAcknowledgement();  // "ok, it was received.."
        UIntegerList unkIndexList = new UIntegerList();
        LongList longList = new LongList();

        if (inObjectType == null) {
            throw new InvalidArgumentException(null);
        }
        if (inDomain == null) {
            throw new InvalidArgumentException(null);
        }
        if (inObjIds == null) {
            throw new InvalidArgumentException(null);
        }
        if (ArchiveManager.objectTypeContainsWildcard(inObjectType)) {
//            interaction.sendError(new InvalidArgumentException(null));
            throw new InvalidArgumentException(null);
        }

        if (HelperCOM.domainContainsWildcard(inDomain)) {
//            interaction.sendError(new InvalidArgumentException(null));
            throw new InvalidArgumentException(null);
        }

        boolean wildcardFound = false;
        for (Long tempObjId : inObjIds) {
            if (tempObjId == 0) {  // Is it the wildcard 0? requirement: Archive.retrieve#1
                longList.clear();  // if the wildcard is in the middle of the input list, we clear the list...
                wildcardFound = true;
                break;
            }
            longList.add(tempObjId);
        }

        ArchiveDetailsList outArchiveDetailsList = new ArchiveDetailsList();
        HeterogeneousList outMatchedObjects = null;

        List<ArchivePersistenceObject> perObjs;
        if (wildcardFound) {
            perObjs = manager.getAllPersistenceObjects(inObjectType, inDomain);
        } else {
            perObjs = manager.getPersistenceObjects(inObjectType, inDomain, longList);
        }

        for (int index = 0; index < perObjs.size(); index++) {  // Let's go one by one in the list

            ArchivePersistenceObject perObj = perObjs.get(index);

            if (perObj == null) {  // COM object not found
                unkIndexList.add(new UInteger(index));
                outArchiveDetailsList.add(new ArchiveDetails());

                if (outMatchedObjects != null) {
                    outMatchedObjects.add(new UInteger());
                }

                continue;
            }

            outArchiveDetailsList.add(perObj.getArchiveDetails()); // requirement: Archive.retrieve#4

            if (outMatchedObjects == null) {  // Initialize the elementList object
                try {
                    outMatchedObjects = new HeterogeneousList();

                    for (int j = 0; j < index; j++) { // Insert the missing elements in the list
                        outMatchedObjects.add(new UInteger());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    interaction.sendError(new UnknownException(
                            "The List of the objects could not be generated!"));
                    interaction.sendResponse(null, null);
                    return;
                }
            }

            if (outMatchedObjects != null) {
                outMatchedObjects.add((Element) perObj.getObject()); // requirement: Archive.retrieve#3
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) {
//            interaction.sendError(new MOErrorException(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            throw new UnknownException(unkIndexList);
        }

        // requirement: Archive.retrieve#6: ordering of objects is not specified
        if (outArchiveDetailsList.isEmpty()) {
            interaction.sendResponse(null, null);  // requirement: Archive.retrieve#5
        } else {
            interaction.sendResponse(outArchiveDetailsList, outMatchedObjects); // requirement: Archive.retrieve#2
        }
    }

    @Override
    public void query(Boolean returnObjBody, final ObjectType lObjectType, final ArchiveQuery lArchiveQuery,
            final QueryFilter queryFilter, final QueryInteraction interaction) throws InvalidArgumentException,
            MALException {
        interaction.sendAcknowledgement();

        if (returnObjBody == null) {
            returnObjBody = false;
        }

        ArrayList<ArchivePersistenceObject> perObjs = manager.query(lObjectType, lArchiveQuery, queryFilter);

        if (queryFilter instanceof CompositeFilterSet) {
            try {
                perObjs = ArchiveManager.filterQuery(perObjs, (CompositeFilterSet) queryFilter);
            } catch (SecurityException | IllegalArgumentException ex) {
                throw new InvalidArgumentException(null);
            }
        }

        // End time but no start time: return only the single object closest to (but not past) the end time
        if (lArchiveQuery != null && lArchiveQuery.getEndTime() != null
                && lArchiveQuery.getStartTime() == null && !perObjs.isEmpty()) {
            ArchivePersistenceObject latestPerObj = perObjs.get(0);
            for (ArchivePersistenceObject perObj : perObjs) {
                long value1 = latestPerObj.getArchiveDetails().getTimestamp().getValue();
                long value2 = perObj.getArchiveDetails().getTimestamp().getValue();
                if (value1 < value2) {
                    latestPerObj = perObj;
                }
            }
            perObjs = new ArrayList<>();
            perObjs.add(latestPerObj);
        }

        if (lArchiveQuery != null && lArchiveQuery.getSortOrder() != null) {
            try {
                perObjs = SortByField.sortPersistenceObjects(perObjs,
                        lArchiveQuery.getSortFieldName(),
                        lArchiveQuery.getSortOrder());
            } catch (NoSuchFieldException ex) {
                throw new InvalidArgumentException(null);
            }
        }

        if (!perObjs.isEmpty()) {
            if (ArchiveManager.objectTypeContainsWildcard(lObjectType)
                    || (lArchiveQuery != null && HelperCOM.domainContainsWildcard(lArchiveQuery.getDomain()))) {
                // Wildcard in ObjectType or domain: send one UPDATE per object so each carries its own type/domain
                for (ArchivePersistenceObject perObj : perObjs) {
                    ArchiveDetailsList outArchDetLst = new ArchiveDetailsList();
                    outArchDetLst.add(perObj.getArchiveDetails());
                    HeterogeneousList outObjectList = new HeterogeneousList();

                    if (returnObjBody) {
                        Element el = (Element) Attribute.javaType2Attribute(perObj.getObject());
                        outObjectList.add(el);
                    }

                    ObjectType objType = ArchiveManager.objectTypeContainsWildcard(lObjectType)
                            ? perObj.getObjectType() : null;
                    interaction.sendUpdate(objType, perObj.getDomain(), outArchDetLst, outObjectList);
                }
            } else {
                // All objects share the same ObjectType and domain: send a single UPDATE
                ArchiveDetailsList outArchiveDetailsList = new ArchiveDetailsList();
                HeterogeneousList outObjectList = returnObjBody ? new HeterogeneousList() : null;

                for (ArchivePersistenceObject perObj : perObjs) {
                    outArchiveDetailsList.add(perObj.getArchiveDetails());
                    if (outObjectList != null) {
                        outObjectList.add((Element) perObj.getObject());
                    }
                }

                IdentifierList domain = (lArchiveQuery != null) ? lArchiveQuery.getDomain() : null;
                interaction.sendUpdate(null, domain, outArchiveDetailsList, outObjectList);
            }
        }

        interaction.sendResponse();
    }

    @Override
    public void count(final ObjectType objType, final ArchiveQuery lArchiveQuery,
            final QueryFilter filter, final CountInteraction interaction) throws InvalidArgumentException,
            MALException {
        interaction.sendAcknowledgement();

        ArrayList<ArchivePersistenceObject> perObjs = manager.query(objType, lArchiveQuery, filter);

        if (filter instanceof CompositeFilterSet) {
            try {
                perObjs = ArchiveManager.filterQuery(perObjs, (CompositeFilterSet) filter);
            } catch (SecurityException | IllegalArgumentException ex) {
                throw new InvalidArgumentException(null);
            }
        }

        // End time but no start time: count only the single object closest to (but not past) the end time
        if (lArchiveQuery != null && lArchiveQuery.getEndTime() != null
                && lArchiveQuery.getStartTime() == null && !perObjs.isEmpty()) {
            ArchivePersistenceObject latestPerObj = perObjs.get(0);
            for (ArchivePersistenceObject perObj : perObjs) {
                if (latestPerObj.getArchiveDetails().getTimestamp().getValue()
                        < perObj.getArchiveDetails().getTimestamp().getValue()) {
                    latestPerObj = perObj;
                }
            }
            perObjs = new ArrayList<>();
            perObjs.add(latestPerObj);
        }

        if (lArchiveQuery != null && lArchiveQuery.getSortOrder() != null) {
            try {
                perObjs = SortByField.sortPersistenceObjects(perObjs, lArchiveQuery.getSortFieldName(),
                        lArchiveQuery.getSortOrder());
            } catch (NoSuchFieldException ex) {
                throw new InvalidArgumentException(null);
            }
        }

        interaction.sendResponse((long) perObjs.size());
    }

    @Override
    public LongList store(final Boolean returnObjId, final ObjectType objType,
            final IdentifierList domain, final ArchiveDetailsList details,
            final HeterogeneousList bodies, final MALInteraction interaction)
            throws DuplicateException, InvalidArgumentException, MALException {
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList;

        // What if the list is null?
        if (details == null) {
            throw new InvalidArgumentException(null);
        }

        if (bodies != null) {
            if (details.size() != bodies.size()) { // requirement: Archive.store#2
                UIntegerList error = new UIntegerList();
                int size1 = (details.size() < bodies.size()) ? details.size() : bodies.size();
                int size2 = (details.size() > bodies.size()) ? details.size() : bodies.size();

                for (int i = size1; i < size2; i++) { // make a list with the invalid indexes
                    error.add(new UInteger(i));
                }

                throw new InvalidArgumentException(error);
            }

        }

        if (ArchiveManager.objectTypeContainsWildcard(objType)) {
            throw new InvalidArgumentException(null);
        }

        if (HelperCOM.domainContainsWildcard(domain)) {
            throw new InvalidArgumentException(null);
        }

        // Do we have Duplicates in the objId array?
        dupIndexList = ArchiveManager.checkForDuplicates(details);

        if (!dupIndexList.isEmpty()) {
            throw new DuplicateException(dupIndexList);
        }

        synchronized (manager) {
            for (int index = 0; index < details.size(); index++) { // Validation of ArchiveDetails object
                if (details.get(index).getId() == 0) { // requirement: Archive.store#1
                    // Shall be taken care in the manager & per inserted entry
                } else { // Does it exist already?
                    if (manager.objIdExists(objType, domain, details.get(index).getId())) {
                        dupIndexList.add(new UInteger(index));
                        continue;
                    }
                }

                if (HelperArchive.archiveDetailsContainsWildcard(details.get(index))) {
                    invIndexList.add(new UInteger(index));
                    //                continue;
                }

                // requirement: Archive.store#3 is not implemented yet.
                // Can only be made after the JAVA API supports COM features: https://github.com/SamCooper/JAVA_SPEC_RIDS/issues/2
                /*
                if (lElementList != null) {
                    if (!manager.isObjectTypeLikeDeclaredServiceType(lObjectType, (Element) lElementList.get(index))
                            && lElementList.get(index) != null) { // requirement: Archive.store#3
                        invIndexList.add(new UInteger(index));
                        continue;
                    }
                }
                 */
            }

            // Errors
            if (!invIndexList.isEmpty()) {
                throw new InvalidArgumentException(invIndexList);
            }

            if (!dupIndexList.isEmpty()) {
                throw new DuplicateException(dupIndexList);
            }

            // The errors have to be before the store operation to fulfil requirement: Archive.store#4
            if (returnObjId) { // requirement: Archive.store#5
                // Execute the store operation (objType, domain, archiveDetails, objs)
                // requirement: Archive.store#7 (the operation returns the objIds with the same order)
                return manager.insertEntries(objType, domain, details, bodies, interaction);
            } else {
                // Cannot be Threaded because is does not lock the access to the db and out of order will happen
                manager.insertEntriesFast(objType, domain, details, bodies, interaction); // requirement: Archive.store#6
                return null;
            }
        }
    }

    @Override
    public void update(final ObjectType objType, final IdentifierList domain,
            final ArchiveDetailsList details, final HeterogeneousList objBodies,
            final MALInteraction interaction) throws UnknownException,
            InvalidArgumentException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (objType == null) {
            return;
        }
        if (domain == null) {
            return;
        }
        if (details == null) {
            return;
        }
        if (ArchiveManager.objectTypeContainsWildcard(objType)
                || HelperCOM.domainContainsWildcard(domain)) {
            throw new InvalidArgumentException(null);
        }

        if (null != objBodies && details.size() != objBodies.size()) {
            UIntegerList error = new UIntegerList();
            int size1 = (details.size() < objBodies.size())
                    ? details.size() : objBodies.size();
            int size2 = (details.size() > objBodies.size())
                    ? details.size() : objBodies.size();

            for (int i = size1; i < size2; i++) { // make a list with the invalid indexes
                error.add(new UInteger(i));
            }

            throw new InvalidArgumentException(error);
        }

        synchronized (manager) {
            for (int index = 0; index < details.size(); index++) {
                ArchiveDetails tmpArchiveDetails = details.get(index);

                if (tmpArchiveDetails.getId() == 0) {
                    invIndexList.add(new UInteger(index));
                    continue;
                }

                if (!manager.objIdExists(objType, domain, tmpArchiveDetails.getId())) { // requirement: Archive.update#1
                    unkIndexList.add(new UInteger(index));
                }
            }

            // Errors
            if (!unkIndexList.isEmpty()) {
                throw new UnknownException(unkIndexList);
            }

            if (!invIndexList.isEmpty()) {
                throw new InvalidArgumentException(invIndexList);
            }

            // The errors are raised before the update, so that nothing is updated if one occurs
            manager.updateEntries(objType, domain, details, objBodies, interaction); // requirement: Archive.update#2
        }
    }

    @Override
    public LongList delete(final ObjectType objType, final IdentifierList domain,
            final LongList lLongList, final MALInteraction interaction) throws UnknownException,
            InvalidArgumentException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        LongList toBeDeleted = new LongList();

        if (ArchiveManager.objectTypeContainsWildcard(objType)) {
            throw new InvalidArgumentException(null);
        }

        if (HelperCOM.domainContainsWildcard(domain)) {
            throw new InvalidArgumentException(null);
        }

        synchronized (manager) {
            for (int index = 0; index < lLongList.size(); index++) {
                Long tempObjId = lLongList.get(index);
                if (tempObjId == 0) {  // Is it the wildcard 0? requirement: Archive.delete#1
                    toBeDeleted.clear();  // if the wildcard is in the middle of the input list, we clear the list...
                    toBeDeleted.addAll(manager.getAllObjIds(objType, domain)); // ... add all
                    break;
                }
                if (!manager.objIdExists(objType, domain, tempObjId)) {
                    unkIndexList.add(new UInteger(index));
                    continue;
                }

                toBeDeleted.add(tempObjId);
            }

            // Errors
            if (!unkIndexList.isEmpty()) {
                throw new UnknownException(unkIndexList);
            }

            if (!invIndexList.isEmpty()) {
                throw new InvalidArgumentException(invIndexList);
            }

            return manager.removeEntries(objType, domain, toBeDeleted, interaction); // requirement: Archive.delete#2, Archive.delete#3
        }
    }

    public ConnectionProvider getConnection() {
        return this.connection;
    }
}
