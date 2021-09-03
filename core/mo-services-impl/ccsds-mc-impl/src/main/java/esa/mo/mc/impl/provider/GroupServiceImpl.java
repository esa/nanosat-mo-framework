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
package esa.mo.mc.impl.provider;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.HelperArchive;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.conversion.provider.ConversionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.group.GroupHelper;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;

/**
 * Group service Provider.
 */
public class GroupServiceImpl extends ConversionInheritanceSkeleton {

    private ArchiveProviderServiceImpl archiveService;
    private boolean initialiased = false;

    /**
     *
     * @param archiveService
     * @throws org.ccsds.moims.mo.mal.MALException
     */
    protected synchronized void init(ArchiveProviderServiceImpl archiveService) throws MALException {
        this.archiveService = archiveService;

        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                GroupHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        initialiased = true;
    }

    /**
     * TODO: make the method more efficient, by querying the archive
     *
     * @param domain
     * @param groupIdentityId
     * @return
     */
    protected Long retrieveLatestGroupDefinitionIdForIdentityFromArchive(IdentifierList domain, Long groupIdentityId) {
        if (archiveService == null) { // If there's no archive...
            return null;
        }
        //get the latest group-definition, referencing the group-identity
        LongList groupDefIds = new LongList();
        groupDefIds.add(0L);
        final ArchiveDetailsList groupDetailsList = HelperArchive.getArchiveDetailsListFromArchive(archiveService, 
                GroupHelper.GROUPDEFINITION_OBJECT_TYPE, domain, groupDefIds);
        ArchiveDetailsList groupDefsReferencingGroupIdentity = new ArchiveDetailsList();
        //get ALL group-definitions, referencing the current group-identity
        for (ArchiveDetails groupDefDetails : groupDetailsList) {
            if (groupIdentityId.equals(groupDefDetails.getDetails().getRelated())) {
                groupDefsReferencingGroupIdentity.add(groupDefDetails);
            }
        }
        //any groupDefs found?
        if (groupDefsReferencingGroupIdentity.isEmpty()) {
            return null;
        }

        //get the LATEST group-definition
        long latestTimestamp = 0;
        int latestDefIndex = 0;
        for (int i = 0; i < groupDefsReferencingGroupIdentity.size(); i++) {
            ArchiveDetails groupDefDetails = groupDefsReferencingGroupIdentity.get(i);
            if (groupDefDetails.getTimestamp().getValue() > latestTimestamp) {
                latestDefIndex = i;
                latestTimestamp = groupDefDetails.getTimestamp().getValue();
            }
        }

        //return the latest group-definitions id
        return groupDefsReferencingGroupIdentity.get(latestDefIndex).getInstId();
    }

    /**
     * Retrieves the groupDetails of the latest group-definition of the given
     * group-identity
     *
     * @param domain
     * @param groupIdentityId the id of the group-identity
     * @return
     */
    protected GroupDetails retrieveGroupDetailsFromArchive(IdentifierList domain, Long groupIdentityId) {

        if (archiveService == null) { // If there's no archive...
            return null;
        }

        Long latestGroupDefId = retrieveLatestGroupDefinitionIdForIdentityFromArchive(domain, groupIdentityId);
        if (latestGroupDefId == null) {
            return null;
        }
        //get the group-definitions-body
        //requirement: 3.9.4.g instances of a group will be referenced by the id of the GroupDefinition-object
        return (GroupDetails) HelperArchive.getObjectBodyFromArchive(archiveService, 
                GroupHelper.GROUPDEFINITION_OBJECT_TYPE, domain, latestGroupDefId);
    }

    /**
     * Retrieves the groupDetails from the given GroupDefinition
     *
     * @param domain
     * @param groupDefId the id of the group-definition
     * @return
     */
    protected GroupDetails retrieveGroupDetailsOfDefinitionFromArchive(IdentifierList domain, Long groupDefId) {

        if (archiveService == null) { // If there's no archive...
            return null;
        }

        //get the group-definitions-body
        //requirement: 3.9.4.g instances of a group will be referenced by the id of the GroupDefinition-object
        return (GroupDetails) HelperArchive.getObjectBodyFromArchive(archiveService, 
                GroupHelper.GROUPDEFINITION_OBJECT_TYPE, domain, groupDefId);
    }

    /**
     * gets all the Object Instances of the group and the groups group and so on
     * that these are referencing. All the objects will be added. no matter if
     * they are from different object-types. the service should check for
     * itself, if they are different
     *
     * @param groupIdentityId the id of this group-identity
     * @param group the details of the group
     * @param previousGroupInstances groups that shouldnt be checked
     * @return the identity-ids of the other services objects (e.g.
     * parameter-identity-ids, action-identity-ids,...)
     */
    protected IdObjectTypeList getGroupObjectIdsFromGroup(Long groupIdentityId, GroupDetails group, LongList previousGroupInstances) {
        //dont check the parent group later again
        previousGroupInstances.add(groupIdentityId);
        //get all referenced instances
        return getGroupObjectIdsFromGroupRecursive(group, previousGroupInstances);
    }

    private IdObjectTypeList getGroupObjectIdsFromGroupRecursive(GroupDetails group, LongList previousGroupInstances) {
        if (archiveService == null || group == null) { // If there's no archive...
            return null;
        }
        //easiest case: group contains non-group-instances
        //requirement: 3.9.4.h
        if (!group.getObjectType().equals(GroupHelper.GROUPIDENTITY_OBJECT_TYPE)) {  // It is no group of groups?
            return new IdObjectTypeList(group.getInstanceIds(), group.getObjectType());
        } else { //solve a case with less complexity: get the instances of the groups groups
            final LongList groupsGroupIdentityIds = group.getInstanceIds();

            //the recursive method-calls should not check the instances that will be checked during the iterations in this method call
            LongList newPreviousGroupInstances = new LongList();
            newPreviousGroupInstances.addAll(previousGroupInstances);
            newPreviousGroupInstances.addAll(groupsGroupIdentityIds);

            //iterate through all new Groups and add all their instances to the final list
            IdObjectTypeList idObjectTypeList = new IdObjectTypeList();
            for (int i = 0; i < groupsGroupIdentityIds.size(); i++) {
                final Long groupInstance = groupsGroupIdentityIds.get(i);
                //this group wasnt checked yet?
                if (!previousGroupInstances.contains(groupInstance)) {
                    previousGroupInstances.add(groupInstance);
                    // Retrieve the groups group-ids from the archive
                    // requirement: 3.9.4.h
                    GroupDetails nextGroupInstance = retrieveGroupDetailsFromArchive(group.getDomain(), groupInstance);
                    idObjectTypeList.addAll(this.getGroupObjectIdsFromGroupRecursive(nextGroupInstance, newPreviousGroupInstances));
                }
            }
            return idObjectTypeList;

        }
    }

    /**
     * helper class, that allows to store many IdObjectType-objects (stores an
     * id and its object-type) in one list
     */
    protected class IdObjectTypeList extends java.util.ArrayList<IdObjectType> {

        public IdObjectTypeList() {
        }

        public IdObjectTypeList(LongList ids, ObjectType objectType) {
            for (Long id : ids) {
                this.add(new IdObjectType(id, objectType));
            }
        }
    }

    /**
     * helper class, that allows to store definition or identity-ids and its
     * object-type in one object
     */
    protected class IdObjectType {

        public IdObjectType(Long id, ObjectType objectType) {
            this.id = id;
            this.objectType = objectType;
        }

        private Long id;
        private ObjectType objectType;

        public Long getId() {
            return id;
        }

        public ObjectType getObjectType() {
            return objectType;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setObjectType(ObjectType objectType) {
            this.objectType = objectType;
        }
    }

}
