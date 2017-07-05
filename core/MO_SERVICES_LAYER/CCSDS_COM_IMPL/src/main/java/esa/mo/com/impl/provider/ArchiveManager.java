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

import esa.mo.com.impl.archive.db.TransactionsProcessor;
import esa.mo.com.impl.archive.fast.FastObjId;
import esa.mo.com.impl.archive.fast.FastDomain;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.com.impl.archive.db.DatabaseBackend;
import esa.mo.com.impl.archive.fast.FastNetwork;
import esa.mo.com.impl.archive.fast.FastProviderURI;
import esa.mo.com.impl.archive.db.SourceLinkContainer;
import esa.mo.com.impl.archive.entities.COMObjectEntity;
import esa.mo.com.impl.archive.fast.FastObjectType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.ArchiveHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilter;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterList;
import org.ccsds.moims.mo.com.archive.structures.CompositeFilterSet;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.archive.structures.QueryFilter;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Composite;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Enumeration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class ArchiveManager {

    private final DatabaseBackend dbBackend;
    private final TransactionsProcessor dbProcessor;

    private final FastDomain fastDomain;
    private final FastNetwork fastNetwork;
    private final FastProviderURI fastProviderURI;
    private final FastObjId fastObjId;
    private final FastObjectType fastObjectType;

    private EventProviderServiceImpl eventService;

    /**
     * Initializes the Archive manager
     *
     * @param eventService
     */
    public ArchiveManager(EventProviderServiceImpl eventService) {
        this.eventService = eventService;

        try {
            ArchiveHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
        }

        this.dbBackend = new DatabaseBackend();
        this.dbProcessor = new TransactionsProcessor(dbBackend);

        // Start the separate lists for the "fast" generation of objIds
        this.fastDomain = new FastDomain(dbBackend);
        this.fastNetwork = new FastNetwork(dbBackend);
        this.fastProviderURI = new FastProviderURI(dbBackend);
        this.fastObjId = new FastObjId(dbBackend);
        this.fastObjectType = new FastObjectType(dbBackend);
    }

    public void init() {
        this.dbBackend.startBackendDatabase();
        this.fastDomain.init();
        this.fastNetwork.init();
        this.fastProviderURI.init();
        this.fastObjectType.init();
    }

    protected void setEventService(EventProviderServiceImpl eventService) {
        this.eventService = eventService;
    }

    void close() {
        // Forces the code to wait until all the stores are flushed
        this.dbProcessor.stopInteractions(new Callable() {
            @Override
            public Integer call() {
                dbBackend.createEntityManager();
                dbBackend.closeEntityManager();
                return null;
            }
        });

        this.eventService = null; // Remove the pointer to avoid publishing more stuff
    }

    private class ResetMainTableRunnable implements Callable {

        @Override
        public Integer call() {
            dbBackend.createEntityManager();
            dbBackend.getEM().getTransaction().begin();
            dbBackend.getEM().createQuery("DELETE FROM COMObjectEntity").executeUpdate();
            dbBackend.getEM().getTransaction().commit();

            fastObjId.resetFastIDs();
            fastDomain.resetFastDomain();
            fastNetwork.resetFastNetwork();
            fastProviderURI.resetFastProviderURI();

            dbBackend.getEM().close();
            dbBackend.restartEMF();

            return null;
        }
    }

    /**
     * Needs to be synchronized with the insertEntries method because the fast
     * objects are being called simultaneously. The Testbeds don't pass without
     * the synchronization.
     *
     */
    protected synchronized void resetTable() {
        Logger.getLogger(ArchiveProviderServiceImpl.class.getName()).info("Reset table triggered!");
        this.dbProcessor.resetMainTable(new ResetMainTableRunnable());
    }

    protected synchronized ArchivePersistenceObject getPersistenceObject(final ObjectType objType,
            final IdentifierList domain, final Long objId) {
        final Integer domainId = this.fastDomain.getDomainId(domain);
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        COMObjectEntity comEntity = this.dbProcessor.getCOMObject(objTypeId, domainId, objId);

        if (comEntity == null) {
            return null;
        }

        return this.convert2ArchivePersistenceObject(comEntity, domain, objId);
    }

    private ArchivePersistenceObject convert2ArchivePersistenceObject(final COMObjectEntity comEntity,
            final IdentifierList domain, final Long objId) {
        Identifier network = null;
        URI providerURI = null;
        ObjectType objType = null;

        try {
            network = this.fastNetwork.getNetwork(comEntity.getNetwork());
            providerURI = this.fastProviderURI.getProviderURI(comEntity.getProviderURI());
            objType = this.fastObjectType.getObjectType(comEntity.getObjectTypeId());
        } catch (Exception ex) {
            Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        SourceLinkContainer sourceLink = comEntity.getSourceLink();
        ObjectId objectId = null;

        if (sourceLink.getObjectTypeId() != null
                || sourceLink.getDomainId() != null
                || sourceLink.getObjId() != null) {
            try {
                ObjectKey ok = new ObjectKey(this.fastDomain.getDomain(sourceLink.getDomainId()), sourceLink.getObjId());
                objectId = new ObjectId(this.fastObjectType.getObjectType(sourceLink.getObjectTypeId()), ok);
            } catch (Exception ex) {
                Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ArchiveDetails archiveDetails = new ArchiveDetails(
                comEntity.getObjectId(),
                new ObjectDetails(comEntity.getRelatedLink(), objectId),
                network,
                comEntity.getTimestamp(),
                providerURI);

        return new ArchivePersistenceObject(objType, domain, objId, archiveDetails, comEntity.getObject());
    }

    protected Object getObject(final ObjectType objType, final IdentifierList domain, final Long objId) {
        return this.getPersistenceObject(objType, domain, objId).getObject();
    }

    protected ArchiveDetails getArchiveDetails(final ObjectType objType, final IdentifierList domain, final Long objId) {
        return this.getPersistenceObject(objType, domain, objId).getArchiveDetails();
    }

    protected Boolean objIdExists(final ObjectType objType, final IdentifierList domain, final Long objId) {
        return (this.getPersistenceObject(objType, domain, objId) != null);
    }

    protected LongList getAllObjIds(final ObjectType objType, final IdentifierList domain) {
        return this.dbProcessor.getAllCOMObjects(this.fastObjectType.getObjectTypeId(objType), this.fastDomain.getDomainId(domain));
    }

    private SourceLinkContainer createSourceContainerFromObjectId(final ObjectId source) {
        Integer sourceDomainId = null;
        Integer sourceObjectTypeId = null;
        Long sourceObjId = null;

        if (source != null) {
            if (source.getKey().getDomain() != null) {
                sourceDomainId = this.fastDomain.getDomainId(source.getKey().getDomain());
            }

            if (source.getType() != null) {
                sourceObjectTypeId = this.fastObjectType.getObjectTypeId(source.getType());
            }

            sourceObjId = source.getKey().getInstId();
        }

        return new SourceLinkContainer(sourceObjectTypeId, sourceDomainId, sourceObjId);
    }

    protected synchronized LongList insertEntries(final ObjectType objType, final IdentifierList domain,
            ArchiveDetailsList lArchiveDetails, final ElementList objects, final MALInteraction interaction) {
        final LongList objIds = new LongList(lArchiveDetails.size());
        final ArrayList<COMObjectEntity> perObjsEntities = new ArrayList<COMObjectEntity>(lArchiveDetails.size());
        final int domainId = this.fastDomain.getDomainId(domain);
        final int objTypeId = this.fastObjectType.getObjectTypeId(objType);

        // Generate the object Ids if needed and the persistence objects to be stored
        for (int i = 0; i < lArchiveDetails.size(); i++) {
            final Long objId = this.fastObjId.getUniqueObjId(objTypeId, domainId, lArchiveDetails.get(i).getInstId());
            final int providerURIId = this.fastProviderURI.getProviderURIId(lArchiveDetails.get(i).getProvider());
            final int networkId = this.fastNetwork.getNetworkId(lArchiveDetails.get(i).getNetwork());
            final SourceLinkContainer sourceLink = this.createSourceContainerFromObjectId(lArchiveDetails.get(i).getDetails().getSource());

            // If there are no objects in the list, inject null...
            final Object objBody = (objects == null) ? null : ((objects.get(i) == null) ? null : objects.get(i));

            final COMObjectEntity perObjEntity = new COMObjectEntity(
                    objTypeId,
                    domainId,
                    objId,
                    lArchiveDetails.get(i).getTimestamp().getValue(),
                    providerURIId,
                    networkId,
                    sourceLink,
                    lArchiveDetails.get(i).getDetails().getRelated(),
                    objBody);

            perObjsEntities.add(perObjEntity);
            objIds.add(objId);
        }

        final Runnable publishEvents = this.generatePublishEventsThread(ArchiveHelper.OBJECTSTORED_OBJECT_TYPE,
                objType, domain, objIds, interaction);

        this.dbProcessor.insert(perObjsEntities, publishEvents);

        return objIds;
    }

    protected void updateEntries(final ObjectType objType, final IdentifierList domain,
            final ArchiveDetailsList lArchiveDetails, final ElementList objects, final MALInteraction interaction) {
        final int domainId = this.fastDomain.getDomainId(domain);
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        final ArrayList<COMObjectEntity> newObjs = new ArrayList<COMObjectEntity>();
        final LongList objIds = new LongList();

        // Generate the object Ids if needed and the persistence objects to be stored
        for (int i = 0; i < lArchiveDetails.size(); i++) {
            final Integer providerURIId = this.fastProviderURI.getProviderURIId(lArchiveDetails.get(i).getProvider());
            final Integer networkId = this.fastNetwork.getNetworkId(lArchiveDetails.get(i).getNetwork());

            // If there are no objects in the list, inject null...
            Object objBody = (objects == null) ? null : ((objects.get(i) == null) ? null : objects.get(i));

            SourceLinkContainer sourceLink = this.createSourceContainerFromObjectId(lArchiveDetails.get(i).getDetails().getSource());

            final COMObjectEntity newObj = new COMObjectEntity(
                    objTypeId,
                    domainId,
                    lArchiveDetails.get(i).getInstId(),
                    lArchiveDetails.get(i).getTimestamp().getValue(),
                    providerURIId,
                    networkId,
                    sourceLink,
                    lArchiveDetails.get(i).getDetails().getRelated(),
                    objBody); // 0.170 ms

            newObjs.add(newObj);
            objIds.add(lArchiveDetails.get(i).getInstId());
        }

        Runnable publishEvents = this.generatePublishEventsThread(ArchiveHelper.OBJECTUPDATED_OBJECT_TYPE,
                objType, domain, objIds, interaction);

        this.dbProcessor.update(newObjs, publishEvents);
    }

    protected LongList removeEntries(final ObjectType objType, final IdentifierList domain,
            final LongList objIds, final MALInteraction interaction) {
        final Integer objTypeId = this.fastObjectType.getObjectTypeId(objType);
        final int domainId = this.fastDomain.getDomainId(domain);

        Runnable publishEvents = this.generatePublishEventsThread(ArchiveHelper.OBJECTDELETED_OBJECT_TYPE,
                objType, domain, objIds, interaction);

        this.dbProcessor.remove(objTypeId, domainId, objIds, publishEvents);
        this.fastObjId.delete(objTypeId, domainId);

        return objIds;
    }

    protected ArrayList<ArchivePersistenceObject> query(final ObjectType objType, 
            final ArchiveQuery archiveQuery, final QueryFilter filter) {
        final Integer objTypeId = (ArchiveManager.objectTypeContainsWildcard(objType)) ? 0 : this.fastObjectType.getObjectTypeId(objType);
        boolean domainContainsWildcard = HelperCOM.domainContainsWildcard(archiveQuery.getDomain());
        Integer domainId = (!domainContainsWildcard) ? this.fastDomain.getDomainId(archiveQuery.getDomain()) : null;
        Integer providerURIId = (archiveQuery.getProvider() != null) ? this.fastProviderURI.getProviderURIId(archiveQuery.getProvider()) : null;
        Integer networkId = (archiveQuery.getNetwork() != null) ? this.fastNetwork.getNetworkId(archiveQuery.getNetwork()) : null;
        SourceLinkContainer sourceLink = this.createSourceContainerFromObjectId(archiveQuery.getSource());

        ArrayList<COMObjectEntity> perObjs = this.dbProcessor.query(objTypeId, 
                archiveQuery, domainId, providerURIId, networkId, sourceLink, filter);

        // Add domain filtering by subpart
        if (archiveQuery.getDomain() != null) {
            if (domainContainsWildcard) {  // It does contain a wildcard
                perObjs = this.filterByDomainSubpart(perObjs, archiveQuery.getDomain());
            }
        }

        // If objectType contains a wildcard then we have to filter them
        if (ArchiveManager.objectTypeContainsWildcard(objType)) {
            try {
                perObjs = this.filterByObjectIdMask(perObjs, objType, false);
            } catch (Exception ex) {
                Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Source field
        if (archiveQuery.getSource() != null) {
            if (ArchiveManager.objectTypeContainsWildcard(archiveQuery.getSource().getType())
                    || HelperCOM.domainContainsWildcard(archiveQuery.getSource().getKey().getDomain())
                    || archiveQuery.getSource().getKey().getInstId() == 0) { // Any Wildcards?
                // objectType filtering   (in the source link)
                if (ArchiveManager.objectTypeContainsWildcard(archiveQuery.getSource().getType())) {
                    try {
                        perObjs = this.filterByObjectIdMask(perObjs, archiveQuery.getSource().getType(), true);
                    } catch (Exception ex) {
                        Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                // Add domain filtering by subpart  (in the source link)
                if (HelperCOM.domainContainsWildcard(archiveQuery.getSource().getKey().getDomain())) {  // Does it contain a wildcard?
                    perObjs = this.filterByDomainSubpart(perObjs, archiveQuery.getSource().getKey().getDomain());
                }
            }
        }

        // Convert to ArchivePersistenceObject
        final ArrayList<ArchivePersistenceObject> outs = new ArrayList<ArchivePersistenceObject>(perObjs.size());
        IdentifierList domain;

        for (COMObjectEntity perObj : perObjs) {
            try {
                domain = this.fastDomain.getDomain(perObj.getDomainId());
                ArchivePersistenceObject out = this.convert2ArchivePersistenceObject(perObj, domain, perObj.getObjectId());
                outs.add(out);
            } catch (Exception ex) {
                Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return outs;
    }

    private ArrayList<COMObjectEntity> filterByObjectIdMask(
            final ArrayList<COMObjectEntity> perObjs,
            final ObjectType objectTypeMask,
            final boolean isSource) throws Exception {
        final long bitMask = ArchiveManager.objectType2Mask(objectTypeMask);
        final long objTypeId = HelperCOM.generateSubKey(objectTypeMask);

        final ArrayList<COMObjectEntity> tmpPerObjs = new ArrayList<COMObjectEntity>(perObjs.size());
        long tmpObjectTypeId;
        int temp;
        long objTypeANDed;
        for (COMObjectEntity perObj : perObjs) {
            temp = (isSource) ? perObj.getSourceLink().getObjectTypeId() : perObj.getObjectTypeId();
            tmpObjectTypeId = HelperCOM.generateSubKey(this.fastObjectType.getObjectType(temp));
            objTypeANDed = (tmpObjectTypeId & bitMask);
            if (objTypeANDed == objTypeId) { // Comparison
                tmpPerObjs.add(perObj);
            }
        }

        return tmpPerObjs;  // Assign new filtered list and discard old one
    }

    private ArrayList<COMObjectEntity> filterByDomainSubpart(
            final ArrayList<COMObjectEntity> perObjs, final IdentifierList wildcardDomain) {
        final ArrayList<COMObjectEntity> tmpPerObjs = new ArrayList<COMObjectEntity>(perObjs.size());
        IdentifierList tmpDomain;

        for (COMObjectEntity perObj : perObjs) {
            try {
                tmpDomain = this.fastDomain.getDomain(perObj.getDomainId());
                if (HelperCOM.domainMatchesWildcardDomain(tmpDomain, wildcardDomain)) {  // Does the domain matches the wildcard?
                    tmpPerObjs.add(perObj);
                }
            } catch (Exception ex) {
                Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return tmpPerObjs;  // Assign new filtered list and discard old one
    }

    protected static ArrayList<ArchivePersistenceObject> filterQuery(
            final ArrayList<ArchivePersistenceObject> perObjs,
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
            tmpPerObjs = new ArrayList<ArchivePersistenceObject>();

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

                Element leftHandSide = (Element) HelperAttributes.javaType2Attribute(obj);
                Boolean evaluation = HelperCOM.evaluateExpression(leftHandSide, compositeFilter.getType(), compositeFilter.getFieldValue());

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

    private static ObjectIdList generateSources(final ObjectType objType,
            final IdentifierList domain, final LongList objIds) {
        final ObjectIdList sourceList = new ObjectIdList(objIds.size());

        for (int i = 0; i < objIds.size(); i++) {
            final ObjectId source = new ObjectId(objType, new ObjectKey(domain, objIds.get(i)));

            // Is the COM Object an Event coming from the archive?
            if (source.getType().equals(HelperCOM.generateCOMObjectType(ArchiveHelper.ARCHIVE_SERVICE, source.getType().getNumber()))) {
                continue; // requirement: 3.4.2.5
            }

            sourceList.add(source);
        }

        return sourceList;
    }

    private void generateAndPublishEvents(final ObjectType objType,
            final ObjectIdList sourceList, final MALInteraction interaction) {
        if (eventService == null) {
            return;
        }

        if (sourceList.isEmpty()) { // Don't store anything if the list is empty...
            return;
        }

        /* Just use it for debugging
        Logger.getLogger(ArchiveManager.class.getName()).log(Level.FINE, "\nobjType: " + objType.toString()
                + "\nDomain: " + ConfigurationProviderSingleton.getDomain().toString() + "\nSourceList: " + sourceList.toString());
         */
        // requirement: 3.4.2.4
        final LongList eventObjIds = eventService.generateAndStoreEvents(objType,
                ConfigurationProviderSingleton.getDomain(), null, sourceList, interaction);

        /* Just use it for debugging
        Logger.getLogger(ArchiveManager.class.getName()).log(Level.FINE, "The eventObjIds are: " + eventObjIds.toString());
         */
        URI sourceURI = new URI("");

        if (interaction != null) {
            if (interaction.getMessageHeader() != null) {
                sourceURI = interaction.getMessageHeader().getURITo();
            }
        }

        try {
            eventService.publishEvents(sourceURI, eventObjIds, objType, null, sourceList, null);
        } catch (IOException ex) {
            Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected static ObjectId archivePerObj2source(final ArchivePersistenceObject obj) {
        return new ObjectId(obj.getObjectType(), new ObjectKey(obj.getDomain(), obj.getObjectId()));
    }

    public static Boolean objectTypeContainsWildcard(final ObjectType objType) {
        return (objType.getArea().getValue() == 0
                || objType.getService().getValue() == 0
                || objType.getVersion().getValue() == 0
                || objType.getNumber().getValue() == 0);
    }

    private static Long objectType2Mask(final ObjectType objType) {
        long areaVal = (objType.getArea().getValue() == 0) ? (long) 0 : (long) 0xFFFF;
        long serviceVal = (objType.getService().getValue() == 0) ? (long) 0 : (long) 0xFFFF;
        long versionVal = (objType.getVersion().getValue() == 0) ? (long) 0 : (long) 0xFF;
        long numberVal = (objType.getNumber().getValue() == 0) ? (long) 0 : (long) 0xFFFF;

        return (new Long(areaVal << 48)
                | new Long(serviceVal << 32)
                | new Long(versionVal << 24)
                | new Long(numberVal));
    }

    public static UIntegerList checkForDuplicates(ArchiveDetailsList archiveDetailsList) {
        UIntegerList dupList = new UIntegerList();

        for (int i = 0; i < archiveDetailsList.size() - 1; i++) {
            if (archiveDetailsList.get(i).getInstId().intValue() == 0) { // Wildcard? Then jump over it
                continue;
            }

            for (int j = i + 1; j < archiveDetailsList.size(); j++) {
                if (archiveDetailsList.get(i).getInstId().intValue() == archiveDetailsList.get(j).getInstId().intValue()) {
                    dupList.add(new UInteger(j));
                }
            }
        }

        return dupList;
    }

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
            if (compositeFilter.getFieldValue().getTypeShortForm() != 15) {  // Is it String?
                return false;
            }
        }

        return true;
    }

    private Runnable generatePublishEventsThread(final ObjectType comObject, final ObjectType objType,
            final IdentifierList domain, final LongList objIds, final MALInteraction interaction) {
        return new Runnable() {
            @Override
            public void run() {
                // Generate and Publish the Events - requirement: 3.4.2.1
                generateAndPublishEvents(comObject,
                        ArchiveManager.generateSources(objType, domain, objIds),
                        interaction);
            }
        };
    }

}
