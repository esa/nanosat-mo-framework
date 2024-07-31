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
 * COMStateArchiveManager follows COM State Object Pattern (Identity <=> Definition <=> Update)
 * <p>
 * Extends COMArchiveManager and exposes methods only available for the given pattern
 *
 * @see COMArchiveManager
 */
public class COMStateArchiveManager<IdentityT extends Element, IdentityListT extends ElementList, DefinitionT extends Element, DefinitionListT extends ElementList, StatusT extends Element, StatusListT extends ElementList>
    extends
    COMArchiveManager<IdentityT, IdentityListT, DefinitionT, DefinitionListT, Element, ElementList, StatusT, StatusListT> {

    public COMStateArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
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

    public ObjectId addStatus(ObjectId definitionId, StatusT status, ObjectId source, MALInteraction interaction)
        throws MALException, MALInteractionException {
        return super.addCOMObject(definitionId, status, source, interaction);
    }

    public ObjectIdList addStatuses(ObjectIdList definitionIds, StatusListT statuses, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObjects(definitionIds, statuses, source, interaction);
    }

    protected ObjectIdList listAllIdentityIds(ObjectType identityType) {
        return super.listAllIdentityIds(identityType);
    }

    protected ObjectIdList listAllDefinitionIds(ObjectType definitionType) {
        return super.listAllObjectIds(definitionType);
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

    public ObjectId getDefinitionIdByStatusId(ObjectId statusId) {
        return super.getObjectIdByInverseRelatedId(statusId);
    }

    public ObjectIdList getDefinitionIdsByStatusIds(ObjectIdList statusIds) {
        return super.getObjectIdsByInverseRelatedIds(statusIds);
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

    public ObjectId getStatusIdByDefinitionId(ObjectId definitionId) {
        return super.getObjectIdByRelatedId(definitionId);
    }

    public ObjectIdList getStatusIdsByDefinitionIds(ObjectIdList definitionIds) {
        return super.getObjectIdsByRelatedIds(definitionIds);
    }

    public StatusT getStatusByDefinitionId(ObjectId definitionId) {
        return (StatusT) super.getObjectByRelatedId(definitionId);
    }

    public StatusListT getStatusesByDefinitionIds(ObjectIdList definitionIds) {
        return (StatusListT) super.getObjectsByRelatedIds(definitionIds);
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

    public ObjectId updateStatus(ObjectId definitionId, StatusT status, ObjectId source, MALInteraction interaction)
        throws MALException, MALInteractionException {
        return super.updateCOMObject(definitionId, status, source, interaction);
    }

    public ObjectIdList updateStatuses(ObjectIdList definitionIds, StatusListT statuses, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObjects(definitionIds, statuses, source, interaction);
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
