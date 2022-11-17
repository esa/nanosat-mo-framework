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
 * COMInstanceVersionArchiveManager follows COM Instance Version Object Pattern (Identity <=> Instance <=> Update)
 * <p>
 * Extends COMArchiveManager and exposes methods only available for the given pattern
 *
 * @see COMArchiveManager
 */
public class COMInstanceVersionArchiveManager<IdentityT extends Element, IdentityListT extends ElementList, InstanceT extends Element, InstanceListT extends ElementList, StatusT extends Element, StatusListT extends ElementList>
    extends
    COMArchiveManager<IdentityT, IdentityListT, Element, ElementList, InstanceT, InstanceListT, StatusT, StatusListT> {

    public COMInstanceVersionArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdPair addInstance(Identifier identity, InstanceT instance, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObject(identity, instance, source, interaction);
    }

    public ObjectIdPairList addInstances(IdentifierList identities, InstanceListT instances, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.addCOMObjects(identities, instances, source, interaction);
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

    protected ObjectIdList getIdentityIds(IdentifierList identities, ObjectType identityType) {
        return super.getIdentityIds(identities, identityType);
    }

    public ObjectId getIdentityIdByInstanceId(ObjectId instanceId) {
        return super.getObjectIdByInverseRelatedId(instanceId);
    }

    public ObjectIdList getIdentityIdsByInstanceIds(ObjectIdList instanceIds) {
        return super.getObjectIdsByInverseRelatedIds(instanceIds);
    }

    protected InstanceT getInstance(Identifier identity, ObjectType identityType) {
        return (InstanceT) super.getObject(identity, identityType);
    }

    protected InstanceListT getInstances(IdentifierList identities, ObjectType identityType) {
        return (InstanceListT) super.getObjects(identities, identityType);
    }

    public ObjectId getInstanceIdByIdentityId(ObjectId identityId) {
        return super.getObjectIdByRelatedId(identityId);
    }

    public ObjectIdList getInstanceIdsByIdentityIds(ObjectIdList identityIds) {
        return super.getObjectIdsByRelatedIds(identityIds);
    }

    public ObjectId getInstanceIdByStatusId(ObjectId statusId) {
        return super.getObjectIdByInverseRelatedId(statusId);
    }

    public ObjectIdList getInstanceIdsByStatusIds(ObjectIdList statusIds) {
        return super.getObjectIdsByInverseRelatedIds(statusIds);
    }

    public InstanceT getInstanceByIdentityId(ObjectId identityId) {
        return (InstanceT) super.getObjectByRelatedId(identityId);
    }

    public InstanceListT getInstancesByIdentityIds(ObjectIdList identityIds) {
        return (InstanceListT) super.getObjectsByRelatedIds(identityIds);
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

    public StatusT getStatus(ObjectId statusId) {
        return (StatusT) super.getObject(statusId);
    }

    public StatusListT getStatuses(ObjectIdList statusIds) {
        return (StatusListT) super.getObjects(statusIds);
    }

    public StatusT getStatusByInstanceId(ObjectId instanceId) {
        return (StatusT) super.getObjectByRelatedId(instanceId);
    }

    public StatusListT getStatusesByInstanceIds(ObjectIdList instanceIds) {
        return (StatusListT) super.getObjectsByRelatedIds(instanceIds);
    }

    public ObjectId updateInstance(ObjectId identityId, InstanceT instance, ObjectId source, MALInteraction interaction)
        throws MALException, MALInteractionException {
        return super.updateCOMObject(identityId, instance, source, interaction);
    }

    public ObjectIdList updateInstances(ObjectIdList identityIds, InstanceListT instances, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObjects(identityIds, instances, source, interaction);
    }

    public ObjectId updateInstanceByIdentityId(ObjectId identityId, InstanceT instance, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObject(identityId, instance, source, interaction);
    }

    public ObjectIdList updateInstancesByIdentityIds(ObjectIdList identityIds, InstanceListT instances, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObjects(identityIds, instances, source, interaction);
    }

    public ObjectId updateStatus(ObjectId instanceId, StatusT status, ObjectId source, MALInteraction interaction)
        throws MALException, MALInteractionException {
        return super.updateCOMObject(instanceId, status, source, interaction);
    }

    public ObjectIdList updateStatuses(ObjectIdList instanceIds, StatusListT statuses, ObjectId source,
        MALInteraction interaction) throws MALException, MALInteractionException {
        return super.updateCOMObjects(instanceIds, statuses, source, interaction);
    }

    public void removeInstance(ObjectId identityId, MALInteraction interaction) throws MALException,
        MALInteractionException {
        super.removeObject(identityId, interaction);
    }

    public void removeInstances(ObjectIdList identityIds, MALInteraction interaction) throws MALException,
        MALInteractionException {
        super.removeObjects(identityIds, interaction);
    }
}
