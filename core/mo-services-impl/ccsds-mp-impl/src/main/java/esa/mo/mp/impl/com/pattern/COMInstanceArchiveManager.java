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
package esa.mo.mp.impl.com.pattern;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mp.structures.ObjectIdPair;
import org.ccsds.moims.mo.mp.structures.ObjectIdPairList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.com.COMArchiveManager;
import esa.mo.mp.impl.com.COMConfiguration;

/**
 * COMInstanceArchiveManager follows COM Instance Object Pattern (Identity <=> Definition <=> Instance <=> Update)
 * <p>
 * Extends COMArchiveManager and exposes methods only available for the given pattern
 *
 * @see COMArchiveManager
 */
public abstract class COMInstanceArchiveManager<IdentityT extends Element, IdentityListT extends ElementList, DefinitionT extends Element, DefinitionListT extends ElementList, InstanceT extends Element, InstanceListT extends ElementList, StatusT extends Element, StatusListT extends ElementList>
    extends
    COMArchiveManager<IdentityT, IdentityListT, DefinitionT, DefinitionListT, InstanceT, InstanceListT, StatusT, StatusListT> {

    public COMInstanceArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdPair addDefinition(Identifier identity, DefinitionT definition, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObject(identity, definition, source, interaction);
    }

    public ObjectIdPairList addDefinitions(IdentifierList identities, DefinitionListT definitions, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObjects(identities, definitions, source, interaction);
    }

    public ObjectId addInstance(ObjectId definitionId, InstanceT instance, ObjectId source, MALInteraction interaction)
        throws MALException, MALInteractionException {
        return super.addCOMObject(definitionId, instance, source, interaction);
    }

    public ObjectIdList addInstances(ObjectIdList definitionIds, InstanceListT instances, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObjects(definitionIds, instances, source, interaction);
    }

    public ObjectId addStatus(ObjectId instanceId, StatusT status, ObjectId source, MALInteraction interaction)
        throws MALException, MALInteractionException {
        return super.addCOMObject(instanceId, status, source, interaction);
    }

    public ObjectIdList addStatuses(ObjectIdList instanceIds, StatusListT statuses, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObjects(instanceIds, statuses, source, interaction);
    }

    protected ObjectIdList listAllIdentityIds(ObjectType identityType) {
        return super.listAllIdentityIds(identityType);
    }

    protected ObjectIdList listAllDefinitionIds(ObjectType definitionType) {
        return super.listAllObjectIds(definitionType);
    }

    protected ObjectIdList listAllInstanceIds(ObjectType instanceType) {
        return super.listAllObjectIds(instanceType);
    }

    protected ObjectIdList listAllStatusIds(ObjectType statusType) {
        return super.listAllObjectIds(statusType);
    }

    public Identifier getIdentity(ObjectId identityId) {
        return (Identifier) super.getObject(identityId);
    }

    protected ObjectId getIdentityId(Identifier identity, ObjectType identityType) {
        return super.getIdentityId(identity, identityType);
    }

    public ObjectIdList getIdentityIds(IdentifierList identities, ObjectType identityType) {
        return super.getIdentityIds(identities, identityType);
    }

    public ObjectId getIdentityIdByDefinitionId(ObjectId definitionId) {
        return super.getObjectIdByInverseRelatedId(definitionId);
    }

    public ObjectIdList getIdentityIdsByDefinitionIds(ObjectIdList definitionIds) {
        return super.getObjectIdsByInverseRelatedIds(definitionIds);
    }

    protected DefinitionT getDefinition(Identifier identity, ObjectType identityType) {
        return (DefinitionT) super.getObject(identity, identityType);
    }

    protected DefinitionListT getDefinitions(IdentifierList identities, ObjectType identityType) {
        return (DefinitionListT) super.getObjects(identities, identityType);
    }

    public ObjectId getDefinitionIdByIdentityId(ObjectId identityId) {
        return super.getObjectIdByRelatedId(identityId);
    }

    public ObjectIdList getDefinitionIdsByIdentityIds(ObjectIdList identityIds) {
        return super.getObjectIdsByRelatedIds(identityIds);
    }

    public ObjectId getDefinitionIdByInstanceId(ObjectId instanceId) {
        return super.getObjectIdByInverseRelatedId(instanceId);
    }

    public ObjectIdList getDefinitionIdsByInstanceIds(ObjectIdList instanceIds) {
        return super.getObjectIdsByInverseRelatedIds(instanceIds);
    }

    public DefinitionT getDefinitionByIdentityId(ObjectId identityId) {
        return (DefinitionT) super.getObjectByRelatedId(identityId);
    }

    public DefinitionListT getDefinitionsByIdentityIds(ObjectIdList identityIds) {
        return (DefinitionListT) super.getObjectsByRelatedIds(identityIds);
    }

    public DefinitionT getDefinition(ObjectId definitionId) {
        return (DefinitionT) super.getObject(definitionId);
    }

    public DefinitionListT getDefinitions(ObjectIdList definitionIds) {
        return (DefinitionListT) super.getObjects(definitionIds);
    }

    public ObjectId getInstanceIdByStatusId(ObjectId statusId) {
        return super.getObjectIdByInverseRelatedId(statusId);
    }

    public ObjectIdList getInstanceIdsByStatusIds(ObjectIdList statusIds) {
        return super.getObjectIdsByInverseRelatedIds(statusIds);
    }

    public InstanceT getInstance(ObjectId instanceId) {
        return (InstanceT) super.getObject(instanceId);
    }

    public InstanceListT getInstances(ObjectIdList instanceIds) {
        return (InstanceListT) super.getObjects(instanceIds);
    }

    public ObjectId getStatusIdByInstanceId(ObjectId instanceId) {
        return super.getObjectIdByRelatedId(instanceId);
    }

    public ObjectIdList getStatusIdsByInstanceIds(ObjectIdList instanceIds) {
        return super.getObjectIdsByRelatedIds(instanceIds);
    }

    public StatusT getStatusByInstanceId(ObjectId instanceId) {
        return (StatusT) super.getObjectByRelatedId(instanceId);
    }

    public StatusListT getStatusesByInstanceIds(ObjectIdList instanceIds) {
        return (StatusListT) super.getObjectsByRelatedIds(instanceIds);
    }

    public StatusT getStatus(ObjectId statusId) {
        return (StatusT) super.getObject(statusId);
    }

    public StatusListT getStatuses(ObjectIdList statusIds) {
        return (StatusListT) super.getObjects(statusIds);
    }

    public ObjectId updateDefinition(ObjectId identityId, DefinitionT definition, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObject(identityId, definition, source, interaction);
    }

    public ObjectIdList updateDefinitions(ObjectIdList identityIds, DefinitionListT definitions, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObjects(identityIds, definitions, source, interaction);
    }

    public ObjectId updateInstance(ObjectId definitionId, InstanceT instance, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObject(definitionId, instance, source, interaction);
    }

    public ObjectIdList updateInstances(ObjectIdList definitionIds, InstanceListT instances, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObjects(definitionIds, instances, source, interaction);
    }

    public ObjectId updateStatus(ObjectId instanceId, StatusT status, ObjectId source, MALInteraction interaction)
        throws MALException, MALInteractionException {
        return super.updateCOMObject(instanceId, status, source, interaction);
    }

    public ObjectIdList updateStatuses(ObjectIdList instanceIds, StatusListT statuses, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObjects(instanceIds, statuses, source, interaction);
    }

    public void removeDefinition(ObjectId identityId, MALInteraction interaction) throws MALException,
        MALInteractionException {
        super.removeObject(identityId, interaction);
    }

    public void removeDefinitions(ObjectIdList identityIds, MALInteraction interaction) throws MALException,
        MALInteractionException {
        super.removeObjects(identityIds, interaction);
    }
}
