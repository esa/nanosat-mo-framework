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

    protected COMArchiveManager(final COMServicesProvider comServices, final COMConfiguration configuration) {
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
    protected ObjectIdPair addCOMObject(final Identifier identity, final Element object, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        final IdentifierList identities = new IdentifierList();
        identities.add(identity);
        final ElementList objects = HelperMisc.element2elementList(object);
        objects.add(object);
        final ObjectIdPairList pairs = addCOMObjects(identities, objects, source, interaction);
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
    protected ObjectIdPairList addCOMObjects(final IdentifierList identities, final ElementList comObjects, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        checkForInvalidIdentities(identities);

        final ObjectType objectType = this.configuration.getObjectType((Element) comObjects.get(0));
        final ObjectType identityType = this.configuration.getRelatedType(objectType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        checkForDuplicateIdentities(identities, identityType);

        // Store identity
        final LongList identityInstanceIds = this.archiveService.store(true, identityType, domain,
                HelperArchive.generateArchiveDetailsList((Long) null, source, interaction), identities, interaction);

        final ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType, domain);

        // Store object
        final ObjectIdList objectId = addCOMObjects(identityIds, comObjects, source, interaction);
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
    protected ObjectId addCOMObject(final ObjectId relatedId, final Element comObject, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        final ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        final ElementList objects = HelperMisc.element2elementList(comObject);
        objects.add(comObject);
        final ObjectIdList idList = addCOMObjects(relatedIds, objects, source, interaction);
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
    protected ObjectIdList addCOMObjects(final ObjectIdList relatedIds, final ElementList comObjects, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        checkForInvalidIds(relatedIds);

        final ObjectType objectType = this.configuration.getObjectType((Element) comObjects.get(0));
        final ObjectType relatedType = this.configuration.getRelatedType(objectType);
        final LongList relatedInstanceIds = COMObjectIdHelper.getInstanceIds(relatedIds);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        // Store object
        final LongList objectInstanceIds = this.archiveService.store(true, objectType, domain,
            HelperArchive.generateArchiveDetailsList(relatedInstanceIds, source, interaction), comObjects, interaction);

        // Store configuration
        this.storeConfiguration(relatedInstanceIds, relatedType, objectInstanceIds, source, interaction);

        return COMObjectIdHelper.getObjectIds(objectInstanceIds, objectType, domain);
    }

    /**
     * Lists all stored ids of identities for given type
     * @param identityType The type of the identities
     * @return The list of ids for given identity type
     */
    protected ObjectIdList listAllIdentityIds(final ObjectType identityType) {
        final ObjectType configurationType = this.configuration.getConfigurationType(identityType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        final List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(
            configurationType,
            domain,
            getObjectIdsWildcard()
        );

        final ObjectIdList objectIds = new ObjectIdList();
        for (final ArchivePersistenceObject configObject : configurationList) {
            objectIds.add((ObjectId) configObject.getObject());
        }
        return objectIds;
    }

    /**
     * Lists all stored ids of objects for given type
     * @param objectType The type of the objects
     * @return The list of ids for given object type
     */
    protected ObjectIdList listAllObjectIds(final ObjectType objectType) {
        final ObjectType relatedType = configuration.getRelatedType(objectType);
        final ObjectType configurationType = configuration.getConfigurationType(relatedType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        final List<ArchivePersistenceObject> objects = this.getObjectsFromArchive(
            configurationType,
            domain,
            getObjectIdsWildcard()
        );
        final ObjectIdList objectIds = new ObjectIdList();
        for (final ArchivePersistenceObject object : objects) {
            final Long objectInstanceId = object.getArchiveDetails().getDetails().getRelated();
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
    protected ObjectId getIdentityId(final Identifier identity, final ObjectType identityType) {
        final IdentifierList identities = new IdentifierList();
        identities.add(identity);
        final ObjectIdList identityIds = getIdentityIds(identities, identityType);
        return !identityIds.isEmpty() ? identityIds.get(0) : null;
    }

    /**
     * Gets stored ids of identities for given identity and type
     * @param identities The list of identities
     * @param identityType The type of all identities
     * @return The list of ids for given identities and type
     */
    protected ObjectIdList getIdentityIds(final IdentifierList identities, final ObjectType identityType) {
        // Query all identities, find matching identity
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        final boolean containsWildcard = identities.contains(wildcardIdentity);

        final ObjectIdList allIdentityIds = this.listAllIdentityIds(identityType);
        final LongList identityInstanceIds = COMObjectIdHelper.getInstanceIds(allIdentityIds);

        final List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(
            identityType,
            domain,
            identityInstanceIds
        );

        final ObjectIdList identityIds = new ObjectIdList();
        for (final ArchivePersistenceObject identityObject : configurationList) {
            for (final Identifier identity : identities) {
                if (!containsWildcard && !Objects.equals(identityObject.getObject(), identity)) continue;
                final ObjectId identityId = COMObjectIdHelper.getObjectId(identityObject);
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
    protected Element getObject(final Identifier identity, final ObjectType identityType) {
        final IdentifierList identityIds = new IdentifierList();
        identityIds.add(identity);
        final ElementList objects = getObjects(identityIds, identityType);
        return objects != null && !objects.isEmpty() ? (Element) objects.get(0) : null;
    }

    /**
     * Gets the COM Objects for given identities and identity type
     * @param identities The list of identities of COM Objects
     * @param identityType The type of the identity
     * @return The list of COM Objects for given identities and identity type
     */
    protected ElementList getObjects(final IdentifierList identities, final ObjectType identityType) {
        final IdentifierList identityDomain = ConfigurationProviderSingleton.getDomain();
        // Query all identities, find matching identity, or match all when wildcard is used
        final boolean containsWildcard = identities.contains(wildcardIdentity);
        final List<ArchivePersistenceObject> identityObjects = this.getObjectsFromArchive(
            identityType,
            identityDomain,
            getObjectIdsWildcard()
        );
        final ObjectIdList identityIds = new ObjectIdList();
        for (final ArchivePersistenceObject identityObject : identityObjects) {
            for (final Identifier identity : identities) {
                if (!containsWildcard && !Objects.equals(identityObject.getObject(), identity)) continue;
                final ObjectId identityId = COMObjectIdHelper.getObjectId(identityObject);
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
    protected ObjectId getObjectIdByRelatedId(final ObjectId relatedId) {
        if (relatedId == null) return null;
        final ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        final ObjectIdList objectIds = getObjectIdsByRelatedIds(relatedIds);
        return !objectIds.isEmpty() ? objectIds.get(0) : null;
    }

    /**
     * Gets object ids by related object ids
     * @param relatedIds The list of ids of related objects
     * @return The ids of the objects
     */
    protected ObjectIdList getObjectIdsByRelatedIds(final ObjectIdList relatedIds) {
        final ObjectIdList objectIds = new ObjectIdList();
        if (relatedIds.isEmpty()) return objectIds;

        // Query all configuration objects for given relatedType, find matching relatedId
        final ObjectType relatedType = relatedIds.get(0).getType();
        final ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        final List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(
            configurationType,
            domain,
            getObjectIdsWildcard()
        );

        for (final ArchivePersistenceObject configObject : configurationList) {
            for (final ObjectId relatedId : relatedIds) {
                if (!Objects.equals(configObject.getObject(), relatedId)) continue;
                final ObjectType objectType = this.configuration.getInverseRelatedType(relatedType);
                final Long objectInstanceId = configObject.getArchiveDetails().getDetails().getRelated();
                final ObjectId objectId = COMObjectIdHelper.getObjectId(objectInstanceId, objectType, configObject.getDomain());
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
    protected ObjectId getObjectIdByInverseRelatedId(final ObjectId inverseRelatedId) {
        if (inverseRelatedId == null) return null;
        final ObjectIdList inverseRelatedIds = new ObjectIdList();
        inverseRelatedIds.add(inverseRelatedId);
        final ObjectIdList objectIds = getObjectIdsByInverseRelatedIds(inverseRelatedIds);
        return !objectIds.isEmpty() ? objectIds.get(0) : null;
    }

    /**
     * Gets object ids by inverse related object ids
     * @param inverseRelatedIds The list of ids of inverse related objects
     * @return The ids of the objects
     */
    protected ObjectIdList getObjectIdsByInverseRelatedIds(final ObjectIdList inverseRelatedIds) {
        final ObjectType inverseRelatedType = inverseRelatedIds.get(0).getType();
        final ObjectType relatedType = this.configuration.getRelatedType(inverseRelatedType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        final LongList inverseRelatedInstanceIds = COMObjectIdHelper.getInstanceIds(inverseRelatedIds);

        final List<ArchivePersistenceObject> inverseRelatedObjects = this.getObjectsFromArchive(
            inverseRelatedType,
            domain,
            inverseRelatedInstanceIds
        );
        final ObjectIdList objectIds = new ObjectIdList();
        for (final ArchivePersistenceObject inverseRelatedObject : inverseRelatedObjects) {
            final Long objectInstanceId = inverseRelatedObject.getArchiveDetails().getDetails().getRelated();
            final ObjectId objectId = COMObjectIdHelper.getObjectId(objectInstanceId, relatedType, inverseRelatedObject.getDomain());
            objectIds.add(objectId);
        }
        return objectIds;
    }

    public ObjectId getSourceId(final ObjectId objectId) {
        if (objectId == null) return null;
        final ObjectIdList objectIds = new ObjectIdList();
        objectIds.add(objectId);
        final ObjectIdList sourceIds = getSourceIds(objectIds);
        return !sourceIds.isEmpty() ? sourceIds.get(0) : null;
    }

    public ObjectIdList getSourceIds(final ObjectIdList objectIds) {
        final ObjectType objectType = objectIds.get(0).getType();
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        final LongList instanceIds = COMObjectIdHelper.getInstanceIds(objectIds);
        final List<ArchivePersistenceObject> objects = this.getObjectsFromArchive(
            objectType,
            domain,
            instanceIds
        );

        final ObjectIdList sourceIds = new ObjectIdList();
        for (final ArchivePersistenceObject object : objects) {
            final ObjectId sourceId = object.getArchiveDetails().getDetails().getSource();
            sourceIds.add(sourceId);
        }
        return sourceIds;
    }

    /**
     * {@link #getObjectsByRelatedIds(ObjectIdList)} operation with a single element
     * @param relatedId The id of the related object
     * @return The COM Object, or null when not found
     */
    protected Element getObjectByRelatedId(final ObjectId relatedId) {
        if (relatedId == null) return null;
        final ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        final ElementList objects = getObjectsByRelatedIds(relatedIds);
        return objects != null && !objects.isEmpty() ? (Element) objects.get(0) : null;
    }

    /**
     * Gets COM Objects by related object ids
     * @param relatedIds The list of ids of the related objects
     * @return The COM Objects for given related object ids
     */
    protected ElementList getObjectsByRelatedIds(final ObjectIdList relatedIds) {
        final ObjectIdList objectIds = getObjectIdsByRelatedIds(relatedIds);
        return getObjects(objectIds);
    }

    /**
     * {@link #getObjects(ObjectIdList)} operation with a single element
     * @param objectId The id of the COM Object
     * @return The COM Object, or null when not found
     */
    protected Element getObject(final ObjectId objectId) {
        if (objectId == null) return null;
        final ObjectIdList objectIds = new ObjectIdList();
        objectIds.add(objectId);
        final ElementList objects = getObjects(objectIds);
        return objects != null && !objects.isEmpty() ? (Element) objects.get(0) : null;
    }

    /**
     * Gets COM Objects by their ids
     * @param objectIds The list of ids of the objects
     * @return The COM Objects for given object ids
     */
    protected ElementList getObjects(final ObjectIdList objectIds) {
        if (objectIds.isEmpty()) return null;
        final ObjectId objectId = objectIds.get(0);
        final LongList instanceIds = COMObjectIdHelper.getInstanceIds(objectIds);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        return HelperArchive.getObjectBodyListFromArchive(
            this.archiveService,
            objectId.getType(),
            domain,
            instanceIds
        );
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
    protected ObjectId updateCOMObject(final ObjectId relatedId, final Element comObject, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        final ObjectIdList relatedIds = new ObjectIdList();
        relatedIds.add(relatedId);
        final ElementList objects = HelperMisc.element2elementList(comObject);
        objects.add(comObject);
        final ObjectIdList pairs = updateCOMObjects(relatedIds, objects, source, interaction);
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
    protected ObjectIdList updateCOMObjects(final ObjectIdList relatedIds, final ElementList comObjects, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        checkForInvalidIds(relatedIds);

        final ObjectType relatedType = relatedIds.get(0).getType();
        final ObjectType objectType = this.configuration.getInverseRelatedType(relatedType);
        final LongList relatedInstanceIds = COMObjectIdHelper.getInstanceIds(relatedIds);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        checkForUnknownRelatedIds(relatedIds, relatedType);

        // Store updated object
        final LongList updatedObjectInstanceIds = this.archiveService.store(true, objectType, domain,
            HelperArchive.generateArchiveDetailsList(relatedInstanceIds, source, interaction), comObjects, interaction);

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
    protected void removeObject(final ObjectId relatedId, final MALInteraction interaction) throws MALException, MALInteractionException {
        final ObjectIdList relatedIds = new ObjectIdList();
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
    protected void removeObjects(final ObjectIdList relatedIds, final MALInteraction interaction) throws MALException, MALInteractionException {
        // Update configuration
        if (relatedIds.isEmpty()) return;
        final LongList removeIds = COMObjectIdHelper.getInstanceIds(relatedIds);
        final ObjectType relatedType = relatedIds.get(0).getType();
        this.removeConfiguration(removeIds, relatedType, interaction);
    }

    private void storeConfiguration(final LongList relatedIds, final ObjectType relatedType, final LongList objectIds, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        final ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        final ObjectIdList configurationList = new ObjectIdList();

        for (final Long relatedId : relatedIds) {
            final ObjectId configurationBody = getConfigurationBody(relatedId, relatedType);
            configurationList.add(configurationBody);
        }

        this.archiveService.store(true, configurationType, domain,
            HelperArchive.generateArchiveDetailsList(objectIds, source, interaction), configurationList, interaction);
    }

    private void updateConfiguration(final LongList relatedIds, final ObjectType relatedType, final LongList objectIds, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        final ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        final List<ArchivePersistenceObject> storedConfigurationList = this.getObjectsFromArchive(
            configurationType,
            domain,
            getObjectIdsWildcard()
        );

        final LongList configurationIds = new LongList();
        for (final ArchivePersistenceObject configObject : storedConfigurationList) {
            for (final Long relatedId : relatedIds) {
                final ObjectId configurationBody = getConfigurationBody(relatedId, relatedType);
                if (!Objects.equals(configObject.getObject(), configurationBody)) continue;
                configurationIds.add(configObject.getObjectId());
            }
        }

        final ObjectIdList configurationList = new ObjectIdList();
        final ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(objectIds, source, interaction);

        for (int index = 0; index < relatedIds.size(); index++) {
            final Long relatedId = relatedIds.get(index);
            final Long configurationId = configurationIds.get(index);

            final ObjectId configurationBody = getConfigurationBody(relatedId, relatedType);
            configurationList.add(configurationBody);

            archiveDetailsList.get(index).setInstId(configurationId);
        }

        this.archiveService.update(configurationType, domain, archiveDetailsList, configurationList, interaction);
    }

    private void removeConfiguration(final LongList relatedIds, final ObjectType relatedType, final MALInteraction interaction) throws MALException, MALInteractionException {
        final ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        final boolean containsWildcard = relatedIds.contains(WILDCARD_ID);

        final ObjectIdList configurationBodies = new ObjectIdList();
        for (final Long relatedId : relatedIds) {
            final ObjectId configurationBody = getConfigurationBody(relatedId, relatedType);
            configurationBodies.add(configurationBody);
        }

        final List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(
            configurationType,
            domain,
            getObjectIdsWildcard()
        );
        final LongList removeIds = new LongList();
        for (final ArchivePersistenceObject configObject : configurationList) {
            for (final ObjectId configurationBody : configurationBodies) {
                if (!containsWildcard && !Objects.equals(configObject.getObject(), configurationBody)) continue;
                removeIds.add(configObject.getObjectId());
            }
        }
        this.archiveService.delete(configurationType, domain, removeIds, interaction);
    }

    private void checkForInvalidIdentities(final IdentifierList identities) throws MALInteractionException {
        final UIntegerList invalidIndexList = new UIntegerList();

        final Identifier emptyIdentity = new Identifier("");
        for (int index = 0; index < identities.size(); index++) {
            final Identifier identity = identities.get(index);

            if (identity == null || identity.equals(emptyIdentity) || identity.equals(wildcardIdentity)) {
                invalidIndexList.add(new UInteger(index));
            }
        }

        if (!invalidIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invalidIndexList));
        }
    }

    private void checkForDuplicateIdentities(final IdentifierList identities, final ObjectType identityType) throws MALInteractionException {
        final UIntegerList duplicateIndexList = new UIntegerList();
        for (int index = 0; index < identities.size(); index++) {
            final Identifier identity = identities.get(index);

            if (getObject(identity, identityType) != null) {
                duplicateIndexList.add(new UInteger(index));
            }
        }

        if (!duplicateIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, duplicateIndexList));
        }
    }

    private void checkForInvalidIds(final ObjectIdList objectIds) throws MALInteractionException {
        final UIntegerList invalidIndexList = new UIntegerList();

        if (objectIds.isEmpty()) {
            invalidIndexList.add(new UInteger(0));
        }

        for (int index = 0; index < objectIds.size(); index++) {
            final ObjectId objectId = objectIds.get(index);
            final Long instanceId = COMObjectIdHelper.getInstanceId(objectId);

            if (objectId == null || instanceId == null || instanceId == 0 || objectId.getType() == null) {
                invalidIndexList.add(new UInteger(index));
            }
        }

        if (!invalidIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invalidIndexList));
        }
    }

    private void checkForUnknownRelatedIds(final ObjectIdList relatedIds, final ObjectType relatedType) throws MALInteractionException {
        final UIntegerList unknownIndexList = new UIntegerList();
        final ObjectType configurationType = this.configuration.getConfigurationType(relatedType);
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();

        final List<ArchivePersistenceObject> configurationList = this.getObjectsFromArchive(
            configurationType,
            domain,
            getObjectIdsWildcard()
        );

        for (int index = 0; index < relatedIds.size(); index++) {
            final ObjectId relatedId = relatedIds.get(index);
            boolean found = false;
            for (final ArchivePersistenceObject configObject : configurationList) {
                if (!Objects.equals(configObject.getObject(), relatedId)) continue;
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
        final LongList wildcard = new LongList();
        wildcard.add(WILDCARD_ID);
        return wildcard;
    }

    private ObjectId getConfigurationBody(final Long relatedInstanceId, final ObjectType relatedType) {
        return COMObjectIdHelper.getObjectId(relatedInstanceId, relatedType);
    }

    private List<ArchivePersistenceObject> getObjectsFromArchive(final ObjectType objectType, final IdentifierList domain, final LongList objectIds) {
        final List<ArchivePersistenceObject> result = HelperArchive.getArchiveCOMObjectList(
            this.archiveService,
            objectType,
            domain,
            objectIds
        );
        if (result != null) return result;
        return new ArrayList<>();
    }
}
