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
 * COMStaticItemManager follows COM Static Item Object Pattern (Identity <=> Definition)
 * <p>
 * Extends COMArchiveManager and exposes methods only available for the given pattern
 *
 * @see COMArchiveManager
 */
public class COMStaticItemArchiveManager<IdentityT extends Element, IdentityListT extends ElementList, DefinitionT extends Element, DefinitionListT extends ElementList>
        extends COMArchiveManager<IdentityT, IdentityListT, DefinitionT, DefinitionListT, Element, ElementList, Element, ElementList> {

    public COMStaticItemArchiveManager(final COMServicesProvider comServices, final COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdPair addDefinition(final Identifier identity, final DefinitionT definition, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObject(identity, definition, source, interaction);
    }

    public ObjectIdPairList addDefinitions(final IdentifierList identities, final DefinitionListT definitions, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObjects(identities, definitions, source, interaction);
    }

    protected ObjectIdList listAllIdentityIds(final ObjectType identityType) {
        return super.listAllIdentityIds(identityType);
    }

    protected ObjectIdList listAllDefinitionIds(final ObjectType definitionType) {
        return super.listAllObjectIds(definitionType);
    }

    public Identifier getIdentity(final ObjectId identityId) {
        return (Identifier) super.getObject(identityId);
    }

    protected ObjectId getIdentityId(final Identifier identity, final ObjectType identityType) {
        return super.getIdentityId(identity, identityType);
    }

    public ObjectIdList getIdentityIds(final IdentifierList identities, final ObjectType identityType) {
        return super.getIdentityIds(identities, identityType);
    }

    public ObjectId getIdentityIdByDefinitionId(final ObjectId definitionId) {
        return super.getObjectIdByInverseRelatedId(definitionId);
    }

    public ObjectIdList getIdentityIdsByDefinitionIds(final ObjectIdList definitionIds) {
        return super.getObjectIdsByInverseRelatedIds(definitionIds);
    }

    protected DefinitionT getDefinition(final Identifier identity, final ObjectType identityType) {
        return (DefinitionT) super.getObject(identity, identityType);
    }

    protected DefinitionListT getDefinitions(final IdentifierList identities, final ObjectType identityType) {
        return (DefinitionListT) super.getObjects(identities, identityType);
    }

    public ObjectId getDefinitionIdByIdentityId(final ObjectId identityId) {
        return super.getObjectIdByRelatedId(identityId);
    }

    public ObjectIdList getDefinitionIdsByIdentityIds(final ObjectIdList identityIds) {
        return super.getObjectIdsByRelatedIds(identityIds);
    }

    public DefinitionT getDefinitionByIdentityId(final ObjectId identityId) {
        return (DefinitionT) super.getObjectByRelatedId(identityId);
    }

    public DefinitionListT getDefinitionsByIdentityIds(final ObjectIdList identityIds) {
        return (DefinitionListT) super.getObjectsByRelatedIds(identityIds);
    }

    public DefinitionT getDefinition(final ObjectId definitionId) {
        return (DefinitionT) super.getObject(definitionId);
    }

    public DefinitionListT getDefinitions(final ObjectIdList definitionIds) {
        return (DefinitionListT) super.getObjects(definitionIds);
    }

    public ObjectId updateDefinition(final ObjectId identityId, final DefinitionT definition, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObject(identityId, definition, source, interaction);
    }

    public ObjectIdList updateDefinitions(final ObjectIdList identityIds, final DefinitionListT definitions, final ObjectId source, final MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObjects(identityIds, definitions, source, interaction);
    }

    public void removeDefinition(final ObjectId identityId, final MALInteraction interaction) throws MALException, MALInteractionException {
        super.removeObject(identityId, interaction);
    }

    public void removeDefinitions(final ObjectIdList identityIds, final MALInteraction interaction) throws MALException, MALInteractionException {
        super.removeObjects(identityIds, interaction);
    }
}
