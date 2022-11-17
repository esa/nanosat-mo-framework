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
package esa.mo.mp.impl.com;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.ccsds.moims.mo.mp.structures.ObjectIdPairList;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.helpers.HelperMisc;

/**
 * Front-end to COM Archive. Delegates to ArchiveProviderServiceImpl.
 * <p>
 * CRUD operations of COM Objects to COM Archive.
 * <p>
 * Automatically manages "inverse related links" of COM model, e.g. Identity -> Definition, as part of Create/Update operations.
 *
 * @see COMConfiguration
 */
public class COMArchiveManager<IdentityT extends Element, IdentityListT extends ElementList, DefinitionT extends Element, DefinitionListT extends ElementList, InstanceT extends Element, InstanceListT extends ElementList, StatusT extends Element, StatusListT extends ElementList> {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(COMArchiveManager.class.getName());

    private final ArchiveProviderServiceImpl archiveService;

    private COMConfiguration configuration;

    private static final Identifier wildcardIdentity = new Identifier("*");
    private static final Long WILDCARD_ID = 0L;

    protected COMArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        this.archiveService = comServices.getArchiveService();
        this.configuration = configuration;
    }

    /**
     * {@link #addCOMObjects(IdentifierList, ElementList, ObjectId, MALInteraction)} operation with a single element
     * @param identity The identity to be added
     * @param object The COM Object for corresponding identity to be added
     * @param source The source of the COM object
     * @param interaction The interaction of the operation
     * @return The id pair of added COM Object
     * @throws MALException
     * @throws MALInteractionException If input validation exception occurred
     */
    protected ObjectIdPair addCOMObject(Identifier identity, Element object, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        IdentifierList identities = new IdentifierList();
        identities.add(identity);
        ElementList objects = HelperMisc.element2elementList(object);
        objects.add(object);
        ObjectIdPairList pairs = addCOMObjects(identities, objects, source, interaction);
        return pairs.get(0);
    }

    /**
     * First checks for invalid and duplicate identities,
     * then stores Identity and COM Object to COM Archive
     * and finally stores configuration COM Object.
     * @param identities The list of identities to be added
     * @param comObjects The list of COM Objects for corresponding identities to be added
     * @param source The source of the COM Objects
     * @param interaction The interaction of the operation
     * @return The id pair list of added COM Objects
     * @throws MALException
     * @throws MALInteractionException If input validation exception occurred
     */
    protected ObjectIdPairList addCOMObjects(IdentifierList identities, ElementList comObjects, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        checkForInvalidIdentities(identities);

        ObjectType objectType = this.configuration.getObjectType((Element) comObjects.get(0));
        ObjectType identityType = this.configuration.getRelatedType(objectType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        checkForDuplicateIdentities(identities, identityType);

        // Store identity
        LongList identityInstanceIds = this.archiveService.store(true, identityType, domain, HelperArchive
            .generateArchiveDetailsList((Long) null, source, interaction), identities, interaction);

        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType, domain);

        // Store object
        ObjectIdList objectId = addCOMObjects(identityIds, comObjects, source, interaction);
        return COMObjectIdHelper.getObjectIdPairs(identityIds, objectId);
    }

    /**
     * {@link #addCOMObjects(ObjectIdList, ElementList, ObjectId, MALInteraction)} operation with a single element
     * @param relatedId The id of the related object
     * @param comObject The COM Object for corresponding related id to be added
     * @param source The source of the COM Object
     * @param interaction The interaction of the operation
     * @return The id of added COM Object
     * @throws MALException
     * @throws MALInteractionException If input validation exception occurred
     */
    protected ObjectId addCOMObject(ObjectId relatedId, Element comObject, ObjectId source, MALInteraction interaction)
        throws MALException, MALInteractionException {
        ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        ElementList objects = HelperMisc.element2elementList(comObject);
        objects.add(comObject);
        ObjectIdList idList = addCOMObjects(relatedIds, objects, source, interaction);
        return idList.get(0);
    }

    /**
     * First checks for invalid related ids,
     * then stores COM Object to COM Archive
     * and finally stores configuration COM Object.
     * @param relatedIds The list of ids of related objects
     * @param comObjects The list of COM Objects for corresponding related ids to be added
     * @param source The source of the COM Objects
     * @param interaction The interaction of the operation
     * @return The id list of added COM Objects
     * @throws MALException
     * @throws MALInteractionException If input validation exception occurred
     */
    protected ObjectIdList addCOMObjects(ObjectIdList relatedIds, ElementList comObjects, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        checkForInvalidIds(relatedIds);

        ObjectType objectType = this.configuration.getObjectType((Element) comObjects.get(0));
        ObjectType relatedType = this.configuration.getRelatedType(objectType);
        LongList relatedInstanceIds = COMObjectIdHelper.getInstanceIds(relatedIds);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        // Store object
        LongList objectInstanceIds = this.archiveService.store(true, objectType, domain, HelperArchive
            .generateArchiveDetailsList(relatedInstanceIds, source, interaction), comObjects, interaction);

        // Store configuration
        this.storeConfiguration(relatedInstanceIds, relatedType, objectInstanceIds, source, interaction);

        return COMObjectIdHelper.getObjectIds(objectInstanceIds, objectType, domain);
    }

    /**
     * Lists all stored ids of identities for given type
     * @param identityType The type of the identities
     * @return The list of ids for given identity type
     */
    protected ObjectIdList listAllIdentityIds(ObjectType identityType) {
        ObjectType configurationType = this.configuration.getConfigurationType(identityType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(configurationType, domain,
            getObjectIdsWildcard());

        ObjectIdList objectIds = new ObjectIdList();
        for (ArchivePersistenceObject configObject : configurationList) {
            objectIds.add((ObjectId) configObject.getObject());
        }
        return objectIds;
    }

    /**
     * Lists all stored ids of objects for given type
     * @param objectType The type of the objects
     * @return The list of ids for given object type
     */
    protected ObjectIdList listAllObjectIds(ObjectType objectType) {
        ObjectType relatedType = configuration.getRelatedType(objectType);
        ObjectType configurationType = configuration.getConfigurationType(relatedType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        List<ArchivePersistenceObject> objects = this.getObjectsFromArchive(configurationType, domain,
            getObjectIdsWildcard());
        ObjectIdList objectIds = new ObjectIdList();
        for (ArchivePersistenceObject object : objects) {
            Long objectInstanceId = object.getArchiveDetails().getDetails().getRelated();
            objectIds.add(COMObjectIdHelper.getObjectId(objectInstanceId, objectType));
        }
        return objectIds;
    }

    /**
     * {@link #getIdentityIds(IdentifierList, ObjectType)} operation with a single element
     * @param identity The identity
     * @param identityType The type of the identity
     * @return The id for given identity and type, or null when not found
     */
    protected ObjectId getIdentityId(Identifier identity, ObjectType identityType) {
        IdentifierList identities = new IdentifierList();
        identities.add(identity);
        ObjectIdList identityIds = getIdentityIds(identities, identityType);
        return !identityIds.isEmpty() ? identityIds.get(0) : null;
    }

    /**
     * Gets stored ids of identities for given identity and type
     * @param identities The list of identities
     * @param identityType The type of all identities
     * @return The list of ids for given identities and type
     */
    protected ObjectIdList getIdentityIds(IdentifierList identities, ObjectType identityType) {
        // Query all identities, find matching identity
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        boolean containsWildcard = identities.contains(wildcardIdentity);

        ObjectIdList allIdentityIds = this.listAllIdentityIds(identityType);
        LongList identityInstanceIds = COMObjectIdHelper.getInstanceIds(allIdentityIds);

        List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(identityType, domain,
            identityInstanceIds);

        ObjectIdList identityIds = new ObjectIdList();
        for (ArchivePersistenceObject identityObject : configurationList) {
            for (Identifier identity : identities) {
                if (!containsWildcard && !Objects.equals(identityObject.getObject(), identity))
                    continue;
                ObjectId identityId = COMObjectIdHelper.getObjectId(identityObject);
                identityIds.add(identityId);
            }
        }
        return identityIds;
    }

    /**
     * {@link #getObjects(IdentifierList, ObjectType)} operation with a single element
     * @param identity The identity of COM Object
     * @param identityType The type of the identity
     * @return The COM Object for given identity and identity type, or null when not found
     */
    protected Element getObject(Identifier identity, ObjectType identityType) {
        IdentifierList identityIds = new IdentifierList();
        identityIds.add(identity);
        ElementList objects = getObjects(identityIds, identityType);
        return objects != null && !objects.isEmpty() ? (Element) objects.get(0) : null;
    }

    /**
     * Gets the COM Objects for given identities and identity type
     * @param identities The list of identities of COM Objects
     * @param identityType The type of the identity
     * @return The list of COM Objects for given identities and identity type
     */
    protected ElementList getObjects(IdentifierList identities, ObjectType identityType) {
        IdentifierList identityDomain = ConfigurationProviderSingleton.getDomain();
        // Query all identities, find matching identity, or match all when wildcard is used
        boolean containsWildcard = identities.contains(wildcardIdentity);
        List<ArchivePersistenceObject> identityObjects = this.getObjectsFromArchive(identityType, identityDomain,
            getObjectIdsWildcard());
        ObjectIdList identityIds = new ObjectIdList();
        for (ArchivePersistenceObject identityObject : identityObjects) {
            for (Identifier identity : identities) {
                if (!containsWildcard && !Objects.equals(identityObject.getObject(), identity))
                    continue;
                ObjectId identityId = COMObjectIdHelper.getObjectId(identityObject);
                identityIds.add(identityId);
            }
        }
        return getObjectsByRelatedIds(identityIds);
    }

    /**
     * {@link #getObjectIdsByRelatedIds(ObjectIdList)} operation with a single element
     * @param relatedId The id of related object
     * @return The id of the object, or null when not found
     */
    protected ObjectId getObjectIdByRelatedId(ObjectId relatedId) {
        if (relatedId == null)
            return null;
        ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        ObjectIdList objectIds = getObjectIdsByRelatedIds(relatedIds);
        return !objectIds.isEmpty() ? objectIds.get(0) : null;
    }

    /**
     * Gets object ids by related object ids
     * @param relatedIds The list of ids of related objects
     * @return The ids of the objects
     */
    protected ObjectIdList getObjectIdsByRelatedIds(ObjectIdList relatedIds) {
        ObjectIdList objectIds = new ObjectIdList();
        if (relatedIds.isEmpty())
            return objectIds;

        // Query all configuration objects for given relatedType, find matching relatedId
        ObjectType relatedType = relatedIds.get(0).getType();
        ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(configurationType, domain,
            getObjectIdsWildcard());

        for (ArchivePersistenceObject configObject : configurationList) {
            for (ObjectId relatedId : relatedIds) {
                if (!Objects.equals(configObject.getObject(), relatedId))
                    continue;
                ObjectType objectType = this.configuration.getInverseRelatedType(relatedType);
                Long objectInstanceId = configObject.getArchiveDetails().getDetails().getRelated();
                ObjectId objectId = COMObjectIdHelper.getObjectId(objectInstanceId, objectType, configObject
                    .getDomain());
                objectIds.add(objectId);
            }
        }
        return objectIds;
    }

    /**
     * {@link #getObjectIdsByInverseRelatedIds(ObjectIdList)} operation with a single element
     * @param inverseRelatedId The id of inverse related object
     * @return The id of the object, or null when not found
     */
    protected ObjectId getObjectIdByInverseRelatedId(ObjectId inverseRelatedId) {
        if (inverseRelatedId == null)
            return null;
        ObjectIdList inverseRelatedIds = new ObjectIdList();
        inverseRelatedIds.add(inverseRelatedId);
        ObjectIdList objectIds = getObjectIdsByInverseRelatedIds(inverseRelatedIds);
        return !objectIds.isEmpty() ? objectIds.get(0) : null;
    }

    /**
     * Gets object ids by inverse related object ids
     * @param inverseRelatedIds The list of ids of inverse related objects
     * @return The ids of the objects
     */
    protected ObjectIdList getObjectIdsByInverseRelatedIds(ObjectIdList inverseRelatedIds) {
        ObjectType inverseRelatedType = inverseRelatedIds.get(0).getType();
        ObjectType relatedType = this.configuration.getRelatedType(inverseRelatedType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        LongList inverseRelatedInstanceIds = COMObjectIdHelper.getInstanceIds(inverseRelatedIds);

        List<ArchivePersistenceObject> inverseRelatedObjects = this.getObjectsFromArchive(inverseRelatedType, domain,
            inverseRelatedInstanceIds);
        ObjectIdList objectIds = new ObjectIdList();
        for (ArchivePersistenceObject inverseRelatedObject : inverseRelatedObjects) {
            Long objectInstanceId = inverseRelatedObject.getArchiveDetails().getDetails().getRelated();
            ObjectId objectId = COMObjectIdHelper.getObjectId(objectInstanceId, relatedType, inverseRelatedObject
                .getDomain());
            objectIds.add(objectId);
        }
        return objectIds;
    }

    public ObjectId getSourceId(ObjectId objectId) {
        if (objectId == null)
            return null;
        ObjectIdList objectIds = new ObjectIdList();
        objectIds.add(objectId);
        ObjectIdList sourceIds = getSourceIds(objectIds);
        return !sourceIds.isEmpty() ? sourceIds.get(0) : null;
    }

    public ObjectIdList getSourceIds(ObjectIdList objectIds) {
        ObjectType objectType = objectIds.get(0).getType();
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        LongList instanceIds = COMObjectIdHelper.getInstanceIds(objectIds);
        List<ArchivePersistenceObject> objects = this.getObjectsFromArchive(objectType, domain, instanceIds);

        ObjectIdList sourceIds = new ObjectIdList();
        for (ArchivePersistenceObject object : objects) {
            ObjectId sourceId = object.getArchiveDetails().getDetails().getSource();
            sourceIds.add(sourceId);
        }
        return sourceIds;
    }

    /**
     * {@link #getObjectsByRelatedIds(ObjectIdList)} operation with a single element
     * @param relatedId The id of the related object
     * @return The COM Object, or null when not found
     */
    protected Element getObjectByRelatedId(ObjectId relatedId) {
        if (relatedId == null)
            return null;
        ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        ElementList objects = getObjectsByRelatedIds(relatedIds);
        return objects != null && !objects.isEmpty() ? (Element) objects.get(0) : null;
    }

    /**
     * Gets COM Objects by related object ids
     * @param relatedIds The list of ids of the related objects
     * @return The COM Objects for given related object ids
     */
    protected ElementList getObjectsByRelatedIds(ObjectIdList relatedIds) {
        ObjectIdList objectIds = getObjectIdsByRelatedIds(relatedIds);
        return getObjects(objectIds);
    }

    /**
     * {@link #getObjects(ObjectIdList)} operation with a single element
     * @param objectId The id of the COM Object
     * @return The COM Object, or null when not found
     */
    protected Element getObject(ObjectId objectId) {
        if (objectId == null)
            return null;
        ObjectIdList objectIds = new ObjectIdList();
        objectIds.add(objectId);
        ElementList objects = getObjects(objectIds);
        return objects != null && !objects.isEmpty() ? (Element) objects.get(0) : null;
    }

    /**
     * Gets COM Objects by their ids
     * @param objectIds The list of ids of the objects
     * @return The COM Objects for given object ids
     */
    protected ElementList getObjects(ObjectIdList objectIds) {
        if (objectIds.isEmpty())
            return null;
        ObjectId objectId = objectIds.get(0);
        LongList instanceIds = COMObjectIdHelper.getInstanceIds(objectIds);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        return HelperArchive.getObjectBodyListFromArchive(this.archiveService, objectId.getType(), domain, instanceIds);
    }

    /**
     * {@link #updateCOMObjects(ObjectIdList, ElementList, ObjectId, MALInteraction)} operation with a single element
     * @param relatedId The id of the related object
     * @param comObject The COM Object for corresponding related id to be updated
     * @param source The source of the COM Object
     * @param interaction The interaction of the operation
     * @return The id of updated COM Object
     * @throws MALException
     * @throws MALInteractionException If input validation exception occurred
     */
    protected ObjectId updateCOMObject(ObjectId relatedId, Element comObject, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        ElementList objects = HelperMisc.element2elementList(comObject);
        objects.add(comObject);
        ObjectIdList pairs = updateCOMObjects(relatedIds, objects, source, interaction);
        return pairs.get(0);
    }

    /**
     * First checks for invalid related ids,
     * then stores updated COM Object to COM Archive
     * and finally updates configuration COM Object.
     * @param relatedIds The list of ids of related objects
     * @param comObjects The list of COM Objects for corresponding related ids to be updated
     * @param source The source of the COM Objects
     * @param interaction The interaction of the operation
     * @return The id list of updated COM Objects
     * @throws MALException
     * @throws MALInteractionException If input validation exception occurred
     */
    protected ObjectIdList updateCOMObjects(ObjectIdList relatedIds, ElementList comObjects, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        checkForInvalidIds(relatedIds);

        ObjectType relatedType = relatedIds.get(0).getType();
        ObjectType objectType = this.configuration.getInverseRelatedType(relatedType);
        LongList relatedInstanceIds = COMObjectIdHelper.getInstanceIds(relatedIds);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        checkForUnknownRelatedIds(relatedIds, relatedType);

        // Store updated object
        LongList updatedObjectInstanceIds = this.archiveService.store(true, objectType, domain, HelperArchive
            .generateArchiveDetailsList(relatedInstanceIds, source, interaction), comObjects, interaction);

        // Update configuration
        this.updateConfiguration(relatedInstanceIds, relatedType, updatedObjectInstanceIds, source, interaction);

        return COMObjectIdHelper.getObjectIds(updatedObjectInstanceIds, objectType, domain);
    }

    /**
     * {@link #removeObjects(ObjectIdList, MALInteraction)} operation with a single element
     * @param relatedId The id of the related object
     * @param interaction The interaction of the operation
     * @throws MALException
     * @throws MALInteractionException
     */
    protected void removeObject(ObjectId relatedId, MALInteraction interaction) throws MALException,
        MALInteractionException {
        ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        this.removeObjects(relatedIds, interaction);
    }

    /**
     * Removes configuration COM Object
     * @param relatedIds The list of ids of related objects
     * @param interaction The interaction of the operation
     * @throws MALException
     * @throws MALInteractionException
     */
    protected void removeObjects(ObjectIdList relatedIds, MALInteraction interaction) throws MALException,
        MALInteractionException {
        // Update configuration
        if (relatedIds.isEmpty())
            return;
        LongList removeIds = COMObjectIdHelper.getInstanceIds(relatedIds);
        ObjectType relatedType = relatedIds.get(0).getType();
        this.removeConfiguration(removeIds, relatedType, interaction);
    }

    private void storeConfiguration(LongList relatedIds, ObjectType relatedType, LongList objectIds, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        ObjectIdList configurationList = new ObjectIdList();

        for (Long relatedId : relatedIds) {
            ObjectId configurationBody = getConfigurationBody(relatedId, relatedType);
            configurationList.add(configurationBody);
        }

        this.archiveService.store(true, configurationType, domain, HelperArchive.generateArchiveDetailsList(objectIds,
            source, interaction), configurationList, interaction);
    }

    private void updateConfiguration(LongList relatedIds, ObjectType relatedType, LongList objectIds, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        List<ArchivePersistenceObject> storedConfigurationList = this.getObjectsFromArchive(configurationType, domain,
            getObjectIdsWildcard());

        LongList configurationIds = new LongList();
        for (ArchivePersistenceObject configObject : storedConfigurationList) {
            for (Long relatedId : relatedIds) {
                ObjectId configurationBody = getConfigurationBody(relatedId, relatedType);
                if (!Objects.equals(configObject.getObject(), configurationBody))
                    continue;
                configurationIds.add(configObject.getObjectId());
            }
        }

        ObjectIdList configurationList = new ObjectIdList();
        ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(objectIds, source,
            interaction);

        for (int index = 0; index < relatedIds.size(); index++) {
            Long relatedId = relatedIds.get(index);
            Long configurationId = configurationIds.get(index);

            ObjectId configurationBody = getConfigurationBody(relatedId, relatedType);
            configurationList.add(configurationBody);

            archiveDetailsList.get(index).setInstId(configurationId);
        }

        this.archiveService.update(configurationType, domain, archiveDetailsList, configurationList, interaction);
    }

    private void removeConfiguration(LongList relatedIds, ObjectType relatedType, MALInteraction interaction)
        throws MALException, MALInteractionException {
        ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        boolean containsWildcard = relatedIds.contains(WILDCARD_ID);

        ObjectIdList configurationBodies = new ObjectIdList();
        for (Long relatedId : relatedIds) {
            ObjectId configurationBody = getConfigurationBody(relatedId, relatedType);
            configurationBodies.add(configurationBody);
        }

        List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(configurationType, domain,
            getObjectIdsWildcard());
        LongList removeIds = new LongList();
        for (ArchivePersistenceObject configObject : configurationList) {
            for (ObjectId configurationBody : configurationBodies) {
                if (!containsWildcard && !Objects.equals(configObject.getObject(), configurationBody))
                    continue;
                removeIds.add(configObject.getObjectId());
            }
        }
        this.archiveService.delete(configurationType, domain, removeIds, interaction);
    }

    private void checkForInvalidIdentities(IdentifierList identities) throws MALInteractionException {
        UIntegerList invalidIndexList = new UIntegerList();

        Identifier emptyIdentity = new Identifier("");
        for (int index = 0; index < identities.size(); index++) {
            Identifier identity = identities.get(index);

            if (identity == null || identity.equals(emptyIdentity) || identity.equals(wildcardIdentity)) {
                invalidIndexList.add(new UInteger(index));
            }
        }

        if (!invalidIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invalidIndexList));
        }
    }

    private void checkForDuplicateIdentities(IdentifierList identities, ObjectType identityType)
        throws MALInteractionException {
        UIntegerList duplicateIndexList = new UIntegerList();
        for (int index = 0; index < identities.size(); index++) {
            Identifier identity = identities.get(index);

            if (getObject(identity, identityType) != null) {
                duplicateIndexList.add(new UInteger(index));
            }
        }

        if (!duplicateIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER,
                duplicateIndexList));
        }
    }

    private void checkForInvalidIds(ObjectIdList objectIds) throws MALInteractionException {
        UIntegerList invalidIndexList = new UIntegerList();

        if (objectIds.isEmpty()) {
            invalidIndexList.add(new UInteger(0));
        }

        for (int index = 0; index < objectIds.size(); index++) {
            ObjectId objectId = objectIds.get(index);
            Long instanceId = COMObjectIdHelper.getInstanceId(objectId);

            if (objectId == null || instanceId == null || instanceId == 0 || objectId.getType() == null) {
                invalidIndexList.add(new UInteger(index));
            }
        }

        if (!invalidIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invalidIndexList));
        }
    }

    private void checkForUnknownRelatedIds(ObjectIdList relatedIds, ObjectType relatedType)
        throws MALInteractionException {
        UIntegerList unknownIndexList = new UIntegerList();
        ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(configurationType, domain,
            getObjectIdsWildcard());

        for (int index = 0; index < relatedIds.size(); index++) {
            ObjectId relatedId = relatedIds.get(index);
            boolean found = false;
            for (ArchivePersistenceObject configObject : configurationList) {
                if (!Objects.equals(configObject.getObject(), relatedId))
                    continue;
                found = true;
            }
            if (!found) {
                unknownIndexList.add(new UInteger(index));
            }
        }

        if (!unknownIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unknownIndexList));
        }
    }

    private LongList getObjectIdsWildcard() {
        LongList wildcard = new LongList();
        wildcard.add(WILDCARD_ID);
        return wildcard;
    }

    private ObjectId getConfigurationBody(Long relatedInstanceId, ObjectType relatedType) {
        return COMObjectIdHelper.getObjectId(relatedInstanceId, relatedType);
    }

    private List<ArchivePersistenceObject> getObjectsFromArchive(ObjectType objectType, IdentifierList domain,
        LongList objectIds) {
        List<ArchivePersistenceObject> result = HelperArchive.getArchiveCOMObjectList(this.archiveService, objectType,
            domain, objectIds);
        if (result != null)
            return result;
        return new ArrayList<>();
    }
}
