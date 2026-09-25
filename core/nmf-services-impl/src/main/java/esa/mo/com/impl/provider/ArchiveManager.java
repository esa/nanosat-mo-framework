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

import esa.mo.com.impl.archive.db.DatabaseBackend;
import esa.mo.com.impl.archive.db.SourceLinkContainer;
import esa.mo.com.impl.archive.db.TransactionsProcessor;
import esa.mo.com.impl.archive.db.COMObjectEntity;
import esa.mo.com.impl.archive.fast.FastDomain;
import esa.mo.com.impl.archive.fast.FastObjId;
import esa.mo.com.impl.archive.fast.FastObjectType;
import esa.mo.com.impl.archive.fast.FastProviderURI;
import esa.mo.com.impl.util.HelperCOM;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * Manages the storage and retrieval of COM objects in the Archive.
 *
 * @author Cesar Coelho
 */
public class ArchiveManager {

    /** The logger. */
    public static final Logger LOGGER = Logger.getLogger(ArchiveManager.class.getName());

    private final DatabaseBackend dbBackend;
    private final TransactionsProcessor dbProcessor;

    private final FastDomain fastDomain;
    private final FastProviderURI fastProviderURI;
    private final FastObjId fastObjId;
    private final FastObjectType fastObjectType;

    /**
     * Creates a new {@code ArchiveManager}.
     */
    public ArchiveManager() {
        this.dbBackend = new DatabaseBackend();
        this.dbProcessor = new TransactionsProcessor(dbBackend);

        // Start the separate lists for the "fast" generation of objIds
        this.fastDomain = new FastDomain(dbBackend);
        this.fastProviderURI = new FastProviderURI(dbBackend);
        this.fastObjId = new FastObjId(dbBackend);
        this.fastObjectType = new FastObjectType(dbBackend);
    }

    /**
     * Initializes the archive manager and its fast indexes.
     */
    public synchronized void init() {
        final ArchiveManager manager = this;

        this.dbProcessor.submitExternalTaskDBTransactions(() -> {
            synchronized (manager) {
                long timestamp = System.currentTimeMillis();
                Logger.getLogger(ArchiveManager.class.getName()).log(
                        Level.INFO, "Starting Archive Backend in a dedicated thread...");
                this.dbBackend.startBackendDatabase(this.dbProcessor);
                timestamp = System.currentTimeMillis() - timestamp;
                Logger.getLogger(ArchiveManager.class.getName()).log(Level.INFO,
                        "Archive Backend started in " + timestamp + " ms! "
                        + "Initializing Fast classes...");

                long timestamp2 = System.currentTimeMillis();
                fastDomain.init();
                fastObjectType.init();
                fastProviderURI.init();
                timestamp2 = System.currentTimeMillis() - timestamp2;
                Logger.getLogger(ArchiveManager.class.getName()).log(Level.INFO,
                        "The Fast classes were initialized in " + timestamp2 + " ms");
                dbBackend.getAvailability().release();
            }
        });
    }

    void close() {
        // Forces the code to wait until all the stores are flushed
        this.dbProcessor.stopInteractions(new Callable<Void>() {
            @Override
            public Void call() {
                try {
                    dbBackend.getAvailability().acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ArchiveManager.class.getName()).log(
                            Level.SEVERE, "The thread was interrupted!", ex);
                }
                dbBackend.getAvailability().release();
                return null;
            }
        });
    }

    /**
     * Wipes the entire archive clean. Used mainly by the tests.
     *
     * Needs to be synchronized with the insertEntries method because the fast
     * objects are being called simultaneously. The Testbeds don't pass without
     * the synchronization.
     *
     */
    public synchronized void wipe() {
        LOGGER.info("(0) Reset table requested!");

        this.dbProcessor.resetMainTable(() -> {
            Logger.getLogger(ArchiveManager.class.getName()).log(
                    Level.INFO, "(1) Starting to reset the table...");
            try {
                dbBackend.getAvailability().acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(ArchiveManager.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

            try {
                Connection c = dbBackend.getConnection();
                c.createStatement().execute("DELETE FROM COMObjectEntity");
            } catch (SQLException ex) {
                Logger.getLogger(ArchiveManager.class.getName()).log(
                        Level.SEVERE, null, ex);
            }

            Logger.getLogger(ArchiveManager.class.getName()).log(Level.INFO,
                    "(2) Reset done for the COM table! Reseting Fast classes...");

            fastObjId.resetFastIDs();
            fastDomain.resetTable();
            fastProviderURI.resetTable();
            dbBackend.getAvailability().release();

            Logger.getLogger(ArchiveManager.class.getName()).log(
                    Level.INFO, "(3) Reset done for all Fast classes!");

            return null;
        });
    }

    /**
     * Returns the persistence object.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param objId the object id
     * @return the persistence object
     */
    public synchronized ArchivePersistenceObject getPersistenceObject(
            final ObjectType objType, final IdentifierList domain, final Long objId) {
        final Integer domainId = this.fastDomain.getDomainId(domain);
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        COMObjectEntity comEntity = this.dbProcessor.getCOMObject(objTypeId, domainId, objId);

        if (comEntity == null) {
            return null;
        }

        return this.convert2ArchivePersistenceObject(comEntity, domain, objId);
    }

    /**
     * Returns the persistence objects.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param objIds the object ids
     * @return the persistence objects
     */
    public synchronized List<ArchivePersistenceObject> getPersistenceObjects(
            final ObjectType objType, final IdentifierList domain, final LongList objIds) {
        final Integer domainId = this.fastDomain.getDomainId(domain);
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        List<COMObjectEntity> comEntities = this.dbProcessor.getCOMObjects(objTypeId, domainId, objIds);
        return convert2ArchivePersistenceObjects(comEntities, domain);
    }

    /**
     * Returns the all persistence objects.
     *
     * @param objType the obj type
     * @param domain the domain
     * @return all the persistence objects
     */
    public synchronized List<ArchivePersistenceObject> getAllPersistenceObjects(
            final ObjectType objType, final IdentifierList domain) {
        final Integer domainId = this.fastDomain.getDomainId(domain);
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        List<COMObjectEntity> comEntities = this.dbProcessor.getAllCOMObjects(objTypeId, domainId);
        return convert2ArchivePersistenceObjects(comEntities, domain);
    }

    private List<ArchivePersistenceObject> convert2ArchivePersistenceObjects(
            final List<COMObjectEntity> comEntities, final IdentifierList domain) {
        if (comEntities == null) {
            return null;
        }

        return comEntities.stream()
                .map(entity -> entity == null ? null : convert2ArchivePersistenceObject(entity, domain, entity.getObjectId()))
                .collect(Collectors.toList());
    }

    private ArchivePersistenceObject convert2ArchivePersistenceObject(
            final COMObjectEntity comEntity, final IdentifierList domain, final Long objId) {
        URI providerURI = null;
        ObjectType objType = null;

        try {
            providerURI = this.fastProviderURI.getProviderURI(comEntity.getProviderURI());
            objType = this.fastObjectType.getObjectType(comEntity.getObjectTypeId());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        SourceLinkContainer sourceLink = comEntity.getSourceLink();
        ObjectKey objectKey = null;

        if (sourceLink.getObjectTypeId() != null
                || sourceLink.getDomainId() != null
                || sourceLink.getObjId() != null) {
            try {
                IdentifierList sDomain = this.fastDomain.getDomain(sourceLink.getDomainId());
                objectKey = new ObjectKey(
                        this.fastObjectType.getObjectType(sourceLink.getObjectTypeId()),
                        sDomain, sourceLink.getObjId());
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        ArchiveDetails archiveDetails = new ArchiveDetails(comEntity.getObjectId(),
                new ObjectLinks(comEntity.getRelatedLink(), objectKey),
                new Time(comEntity.getTimestamp().getValue()), providerURI);

        return new ArchivePersistenceObject(objType, domain, objId, archiveDetails, comEntity.getObject());
    }

    /**
     * Returns the object.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param objId the object id
     * @return the object
     */
    public Object getObject(final ObjectType objType, final IdentifierList domain, final Long objId) {
        return this.getPersistenceObject(objType, domain, objId).getObject();
    }

    /**
     * Returns the archive details.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param objId the object id
     * @return the archive details
     */
    public ArchiveDetails getArchiveDetails(final ObjectType objType, final IdentifierList domain, final Long objId) {
        return this.getPersistenceObject(objType, domain, objId).getArchiveDetails();
    }

    /**
     * Returns whether an object with the given type, domain and id exists in the archive.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param objId the object id
     * @return {@code true} if it exists
     */
    public Boolean objIdExists(final ObjectType objType, final IdentifierList domain, final Long objId) {
        final Integer domainId = this.fastDomain.getDomainId(domain);
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        return this.dbProcessor.existsCOMObject(objTypeId, domainId, objId);
    }

    /**
     * Returns the all obj ids.
     *
     * @param objType the obj type
     * @param domain the domain
     * @return all the object ids
     */
    public LongList getAllObjIds(final ObjectType objType, final IdentifierList domain) {
        return this.dbProcessor.getAllCOMObjectsIds(
                this.fastObjectType.getObjectTypeId(objType),
                this.fastDomain.getDomainId(domain));
    }

    private SourceLinkContainer createSourceContainerFromObjectId(final ObjectKey source) {
        Integer sourceDomainId = null;
        Integer sourceObjectTypeId = null;
        Long sourceObjId = null;

        if (source != null) {
            if (source.getDomain() != null) {
                sourceDomainId = this.fastDomain.getDomainId(source.getDomain());
            }

            if (source.getType() != null) {
                sourceObjectTypeId = this.fastObjectType.getObjectTypeId(source.getType());
            }

            if (source.getId() != null) {
                sourceObjId = source.getId();
            }
        }

        return new SourceLinkContainer(sourceObjectTypeId, sourceDomainId, sourceObjId);
    }

    /**
     * Inserts the given entries using the fast index, without generating new ids.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param lArchiveDetails the l archive details
     * @param objects the objects
     * @param interaction the MAL interaction context
     */
    public void insertEntriesFast(final ObjectType objType, final IdentifierList domain,
            final ArchiveDetailsList lArchiveDetails, final ElementList objects, final MALInteraction interaction) {
        // It is quite hard to improve this method...
        insertEntries(objType, domain, lArchiveDetails, objects, interaction);
    }

    /**
     * Inserts the given entries and returns their assigned object ids.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param lArchiveDetails the l archive details
     * @param objects the objects
     * @param interaction the MAL interaction context
     * @return the assigned object ids
     */
    public synchronized LongList insertEntries(final ObjectType objType, final IdentifierList domain,
            final ArchiveDetailsList lArchiveDetails, final ElementList objects, final MALInteraction interaction) {
        final LongList objIds = new LongList(lArchiveDetails.size());
        final ArrayList<COMObjectEntity> perObjsEntities = new ArrayList<>(lArchiveDetails.size());
        final int domainId = this.fastDomain.getDomainId(domain);
        final int objTypeId = this.fastObjectType.getObjectTypeId(objType);

        for (int i = 0; i < lArchiveDetails.size(); i++) {
            ArchiveDetails details = lArchiveDetails.get(i);
            final int providerURIId = this.fastProviderURI.getProviderURIId(details.getProvider());
            final SourceLinkContainer sourceLink = this.createSourceContainerFromObjectId(details.getLinks().getSource());
            final Long objId = this.fastObjId.getUniqueObjId(objTypeId, domainId, details.getId());

            // If there are no objects in the list, inject null...
            final Object objBody = (objects == null) ? null : ((objects.get(i) == null) ? null : objects.get(i));

            perObjsEntities.add(new COMObjectEntity(objTypeId, domainId, objId,
                    details.getTimestamp().getValue(), providerURIId,
                    sourceLink, details.getLinks().getRelated(),
                    objBody));
            objIds.add(objId);
        }

        this.dbProcessor.insert(perObjsEntities, null);
        return objIds;
    }

    /**
     * Updates the given archive entries.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param lArchiveDetails the l archive details
     * @param objects the objects
     * @param interaction the MAL interaction context
     */
    public synchronized void updateEntries(final ObjectType objType, final IdentifierList domain,
            final ArchiveDetailsList lArchiveDetails, final ElementList objects, final MALInteraction interaction) {
        final int domainId = this.fastDomain.getDomainId(domain);
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        final ArrayList<COMObjectEntity> newObjs = new ArrayList<>();

        for (int i = 0; i < lArchiveDetails.size(); i++) {
            final URI provider = lArchiveDetails.get(i).getProvider();
            final Integer providerURIId = this.fastProviderURI.getProviderURIId(provider);

            // If there are no objects in the list, inject null...
            Object objBody = (objects == null) ? null : ((objects.get(i) == null) ? null : objects.get(i));

            SourceLinkContainer sourceLink = this.createSourceContainerFromObjectId(
                    lArchiveDetails.get(i).getLinks().getSource());

            final COMObjectEntity newObj = new COMObjectEntity(objTypeId,
                    domainId, lArchiveDetails.get(i).getId(),
                    lArchiveDetails.get(i).getTimestamp().getValue(),
                    providerURIId, sourceLink,
                    lArchiveDetails.get(i).getLinks().getRelated(), objBody);

            newObjs.add(newObj);
        }

        this.dbProcessor.update(newObjs, null);
    }

    /**
     * Removes the entries with the given ids from the archive.
     *
     * @param objType the obj type
     * @param domain the domain
     * @param objIds the object ids
     * @param interaction the MAL interaction context
     * @return the removed object ids
     */
    public LongList removeEntries(final ObjectType objType, final IdentifierList domain,
            final LongList objIds, final MALInteraction interaction) {
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        final int domainId = this.fastDomain.getDomainId(domain);
        this.dbProcessor.remove(objTypeId, domainId, objIds, null);
        this.fastObjId.delete(objTypeId, domainId);
        return objIds;
    }

    /**
     * Queries the archive for the objects matching the given type and query.
     *
     * @param objType the obj type
     * @param archiveQuery the archive query
     * @param filter the filter
     * @return the matching objects
     */
    public ArrayList<ArchivePersistenceObject> query(final ObjectType objType,
            final ArchiveQuery archiveQuery, final QueryFilter filter) {
        final ArrayList<COMObjectEntity> perObjs = this.queryCOMObjectEntity(objType, archiveQuery, filter);

        // Convert COMObjectEntity to ArchivePersistenceObject
        final ArrayList<ArchivePersistenceObject> outs = new ArrayList<>(perObjs.size());
        IdentifierList domain;

        for (COMObjectEntity perObj : perObjs) {
            try {
                domain = this.fastDomain.getDomain(perObj.getDomainId());
                outs.add(this.convert2ArchivePersistenceObject(perObj, domain, perObj.getObjectId()));
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        return outs;
    }

    /**
     * Deletes the COM object entities matching the given query and returns the number deleted.
     *
     * @param objType the obj type
     * @param archiveQuery the archive query
     * @param filter the filter
     * @return the number of affected objects
     */
    public int deleteCOMObjectEntities(final ObjectType objType,
            final ArchiveQuery archiveQuery, final QueryFilter filter) {
        final IntegerList objTypeIds = this.fastObjectType.getObjectTypeIds(objType);

        if (objTypeIds == null || objTypeIds.isEmpty()) {
            return 0;
        }

        final IntegerList domainIds = this.fastDomain.getDomainIds(archiveQuery.getDomain());
        final Integer providerURIId = (archiveQuery.getProvider() != null)
                ? this.fastProviderURI.getProviderURIId(archiveQuery.getProvider()) : null;
        final ObjectKey sId = archiveQuery.getSource();
        final SourceLinkContainer sourceLink = this.createSourceContainerFromObjectId(sId);

        if (sId != null) {
            if (sId.getDomain() != null) {
                sourceLink.setDomainIds(this.fastDomain.getDomainIds(sId.getDomain()));
            }

            if (sId.getType() != null) {
                sourceLink.setObjectTypeIds(this.fastObjectType.getObjectTypeIds(sId.getType()));
            }
        }

        return this.dbProcessor.delete(objTypeIds, archiveQuery,
                domainIds, providerURIId, sourceLink, filter);
    }

    /**
     * Queries the archive for the COM object entities matching the given type and query.
     *
     * @param objType the obj type
     * @param archiveQuery the archive query
     * @param filter the filter
     * @return the matching objects
     */
    public ArrayList<COMObjectEntity> queryCOMObjectEntity(final ObjectType objType,
            final ArchiveQuery archiveQuery, final QueryFilter filter) {
        final IntegerList objTypeIds = this.fastObjectType.getObjectTypeIds(objType);

        if (null != objTypeIds && !objTypeIds.isEmpty()) {
            final IntegerList domainIds = this.fastDomain.getDomainIds(archiveQuery.getDomain());
            final Integer providerURIId = (archiveQuery.getProvider() != null) ?
                    this.fastProviderURI.getProviderURIId(archiveQuery.getProvider()) : null;
            final SourceLinkContainer sourceLink = this.createSourceContainerFromObjectId(archiveQuery.getSource());

            if (archiveQuery.getSource() != null) {
                if (archiveQuery.getSource().getDomain() != null) {
                    sourceLink.setDomainIds(this.fastDomain.getDomainIds(
                            archiveQuery.getSource().getDomain()));
                }

                if (archiveQuery.getSource().getType() != null) {
                    sourceLink.setObjectTypeIds(this.fastObjectType.getObjectTypeIds(
                            archiveQuery.getSource().getType()));
                }
            }

            return this.dbProcessor.query(objTypeIds, archiveQuery, domainIds,
                    providerURIId, sourceLink, filter);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Queries the archive for the COM object entities matching the given types and query.
     *
     * @param objTypes the obj types
     * @param archiveQuery the archive query
     * @param filter the filter
     * @return the matching objects
     */
    public ArrayList<COMObjectEntity> queryCOMObjectEntity(final ObjectTypeList objTypes,
            final ArchiveQuery archiveQuery, final QueryFilter filter) {
        final IntegerList objTypeIds = new IntegerList();
        for (ObjectType objType : objTypes) {
            objTypeIds.addAll(this.fastObjectType.getObjectTypeIds(objType));
        }

        if (objTypeIds.isEmpty()) {
            return new ArrayList<>();
        }

        final IntegerList domainIds = this.fastDomain.getDomainIds(archiveQuery.getDomain());
        final Integer providerURIId = (archiveQuery.getProvider() != null)
                ? this.fastProviderURI.getProviderURIId(archiveQuery.getProvider()) : null;
        final SourceLinkContainer sourceLink = this.createSourceContainerFromObjectId(archiveQuery.getSource());

        if (archiveQuery.getSource() != null) {
            if (archiveQuery.getSource().getDomain() != null) {
                sourceLink.setDomainIds(this.fastDomain.getDomainIds(archiveQuery.getSource().getDomain()));
            }

            if (archiveQuery.getSource().getType() != null) {
                sourceLink.setObjectTypeIds(this.fastObjectType.getObjectTypeIds(archiveQuery.getSource().getType()));
            }
        }

        return this.dbProcessor.query(objTypeIds, archiveQuery, domainIds,
                providerURIId, sourceLink, filter);
    }

    /**
     * Filters the given persistence objects against the archive query.
     *
     * @param perObjs the per objs
     * @param filterSet the filter set
     * @return the filter query
     * @throws MALInteractionException if the operation fails
     */
    public static ArrayList<ArchivePersistenceObject> filterQuery(final ArrayList<ArchivePersistenceObject> perObjs,
            final CompositeFilterSet filterSet) throws MALInteractionException {
        if (filterSet == null) {
            return perObjs;
        }

        final CompositeFilterList compositeFilterList = filterSet.getFilters();
        ArrayList<ArchivePersistenceObject> outPerObjs = perObjs;
        ArrayList<ArchivePersistenceObject> tmpPerObjs;
        Object obj;

        // Cycle the Filters
        for (CompositeFilter compositeFilter : compositeFilterList) {
            tmpPerObjs = new ArrayList<>();

            if (compositeFilter == null) {
                continue;
            }

            // Cycle the objects
            for (ArchivePersistenceObject outPerObj : outPerObjs) {
                obj = outPerObj.getObject();

                // Check if Composite Filter is valid
                if (!ArchiveManager.isCompositeFilterValid(compositeFilter, obj)) {
                    throw new IllegalArgumentException();
                }

                // Requirement from the Composite filter: page 57:
                // For the dots: "If a field is nested, it can use the dot to separate"
                try {
                    obj = HelperCOM.getNestedObject(obj, compositeFilter.getFieldName());
                } catch (NoSuchFieldException ex) {
                    // requirement from the Composite filter: page 57
                    // "If the field does not exist in the Composite then the filter shall evaluate to false."
                    continue;
                }

                Attribute leftHandSide = (Attribute) Attribute.javaType2Attribute(obj);
                Boolean evaluation = HelperCOM.evaluateExpression(leftHandSide, compositeFilter.getType(),
                        compositeFilter.getFieldValue());

                if (evaluation == null) {
                    continue;
                }

                if (evaluation) {
                    tmpPerObjs.add(outPerObj);
                }
            }

            outPerObjs = tmpPerObjs;
        }

        return outPerObjs;
    }

    /**
     * Builds the source {@link org.ccsds.moims.mo.com.structures.ObjectKey} of the given persistence object.
     *
     * @param obj the obj
     * @return the archive per obj2source
     */
    public static ObjectKey archivePerObj2source(final ArchivePersistenceObject obj) {
        return new ObjectKey(obj.getObjectType(), obj.getDomain(), obj.getObjectId());
    }

    /**
     * Returns whether the given object type contains a wildcard field.
     *
     * @param objType the obj type
     * @return the object type contains wildcard
     */
    public static Boolean objectTypeContainsWildcard(final ObjectType objType) {
        return (objType.getArea().getValue() == 0
                || objType.getService().getValue() == 0
                || objType.getVersion().getValue() == 0
                || objType.getNumber().getValue() == 0);
    }

    /**
     * Returns the indexes of the duplicate ids in the given details list.
     *
     * @param details the details
     * @return the check for duplicates
     */
    public static UIntegerList checkForDuplicates(ArchiveDetailsList details) {
        UIntegerList dupList = new UIntegerList();

        for (int i = 0; i < details.size() - 1; i++) {
            Long instId = details.get(i).getId();

            if (instId.intValue() == 0) { // Wildcard? Then jump over it
                continue;
            }

            for (int j = i + 1; j < details.size(); j++) {
                if (instId.intValue() == details.get(j).getId().intValue()) {
                    dupList.add(new UInteger(j));
                }
            }
        }

        return dupList;
    }

    /**
     * Returns whether composite filter valid.
     *
     * @param compositeFilter the composite filter
     * @param obj the obj
     * @return the is composite filter valid
     */
    public static boolean isCompositeFilterValid(CompositeFilter compositeFilter, Object obj) {
        if (compositeFilter.getFieldName().contains("\\.")) {  // Looking into a nested field?
            if (!(obj instanceof Composite)) {
                return false;  // If it is not a composite, we can not check fields inside...
            } else {
                try { // Does the Field asked for, exists?
                    HelperCOM.getNestedObject(obj, compositeFilter.getFieldName());
                } catch (NoSuchFieldException ex) {
                    return false;
                }
            }
        }

        ExpressionOperator expressionOperator = compositeFilter.getType();

        if (compositeFilter.getFieldValue() == null) {
            if (expressionOperator.equals(ExpressionOperator.CONTAINS)
                    || expressionOperator.equals(ExpressionOperator.ICONTAINS)
                    || expressionOperator.equals(ExpressionOperator.GREATER)
                    || expressionOperator.equals(ExpressionOperator.GREATER_OR_EQUAL)
                    || expressionOperator.equals(ExpressionOperator.LESS)
                    || expressionOperator.equals(ExpressionOperator.LESS_OR_EQUAL)) {
                return false;
            }
        }

        if (obj instanceof Enumeration) {
            Attribute fieldValue = compositeFilter.getFieldValue();
            //            if (!(fieldValue instanceof UInteger) || !(fieldValue.getTypeShortForm() == 11) ) {
            if (!(fieldValue instanceof UInteger)) {
                return false;
            }
        }

        if (obj instanceof Blob) {
            if (!(expressionOperator.equals(ExpressionOperator.EQUAL))
                    && !(expressionOperator.equals(ExpressionOperator.DIFFER))) {
                return false;
            }
        }

        if (expressionOperator.equals(ExpressionOperator.CONTAINS)
                || expressionOperator.equals(ExpressionOperator.ICONTAINS)) {
            if (compositeFilter.getFieldValue().getTypeId().getSFP() != 15) {  // Is it String?
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the fast domain.
     *
     * @return the fast domain
     */
    public FastDomain getFastDomain() {
        return fastDomain;
    }

    /**
     * Returns the fast provider uri.
     *
     * @return the fast provider uri
     */
    public FastProviderURI getFastProviderURI() {
        return fastProviderURI;
    }

    /**
     * Returns the fast obj id.
     *
     * @return the fast obj id
     */
    public FastObjId getFastObjId() {
        return fastObjId;
    }

    /**
     * Returns the fast object type.
     *
     * @return the fast object type
     */
    public FastObjectType getFastObjectType() {
        return fastObjectType;
    }

    /**
     * Returns the database backend.
     *
     * @return the database backend
     */
    public DatabaseBackend getDbBackend() {
        return dbBackend;
    }

    /**
     * Returns the transactions processor.
     *
     * @return the transactions processor
     */
    public TransactionsProcessor getTransactionsProcessor() {
        return dbProcessor;
    }
}
