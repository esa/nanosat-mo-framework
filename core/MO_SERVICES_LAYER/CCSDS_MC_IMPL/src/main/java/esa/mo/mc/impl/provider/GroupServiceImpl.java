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
package esa.mo.mc.impl.provider;

import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.util.HelperArchive;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.conversion.provider.ConversionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.group.GroupHelper;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.group.structures.GroupDetailsList;

/**
 *
 */
public class GroupServiceImpl extends ConversionInheritanceSkeleton {

    private ArchiveProviderServiceImpl archiveService;
    private static transient COMService service;
/*
    // Conversion Service Object Types
    protected static transient ObjectType OBJ_TYPE_GS_GROUP;
*/
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

        service = GroupHelper.GROUP_SERVICE;
/*
        // Conversion Service Object Types
        OBJ_TYPE_GS_GROUP = HelperCOM.generateCOMOjectType(service, new UShort(1));
*/
    }

    protected GroupDetails retrieveGroupDetailsFromArchive(IdentifierList domain, Long objId) {

        if (archiveService == null){ // If there's no archive...
            return null;
        }
        
        return  (GroupDetails) HelperArchive.getObjectBodyFromArchive(archiveService, GroupHelper.GROUPDEFINITION_OBJECT_TYPE, domain, objId);
    }

    
    protected LongList getGroupObjectIdsFromGroup(GroupDetails group, Long objId) {

        if (archiveService == null || group == null){ // If there's no archive...
            return null;
        }
        
        LongList objIds = new LongList();
        objIds.add(objId);
        
        if (group.getObjectType().equals(GroupHelper.GROUPDEFINITION_OBJECT_TYPE)) {  // Is it a group of groups?
            return this.getGroupObjectIdsFromGroup(group, objIds);
        }
        
        return group.getInstanceIds();

    }
    
    private LongList getGroupObjectIdsFromGroup(GroupDetails group, LongList previousGroupInstances) {

        if (archiveService == null){ // If there's no archive...
            return null;
        }
        
        if (previousGroupInstances == null){ // Is it the first round?
            previousGroupInstances = new LongList();
        }
        
        LongList outObjs = null;

        // Retrieve either (more groups) or (the objects of the Group) from the Archive
        GroupDetailsList newGroups = (GroupDetailsList) HelperArchive.getObjectBodyListFromArchive(archiveService, 
                group.getObjectType(), group.getDomain(), group.getInstanceIds());
        
        for (int index = 0; index < newGroups.size(); index++){
            GroupDetails newGroup = newGroups.get(index);
            
            if (newGroup.getObjectType().equals(GroupHelper.GROUPDEFINITION_OBJECT_TYPE)) {  // Is it a group of groups?

                for (Long objId : newGroup.getInstanceIds()){
                    if (previousGroupInstances.contains( objId )){ // To avoid getting stuck in a loop
                        continue; // It was already checked.. jump to the next one.
                    }

                    previousGroupInstances.addAll(newGroup.getInstanceIds());
                
                    if (outObjs == null || outObjs.size() == 0){  // If there are no elements, then use the created list from the method
                        outObjs = this.getGroupObjectIdsFromGroup(group, previousGroupInstances);
                    }else{ // If it already exists, then just add to the group
                        LongList obj = this.getGroupObjectIdsFromGroup(group, previousGroupInstances);

                        if(obj == null){
                            return null;
                        }
                    
                        if ( outObjs.get(0).getClass().equals(obj.get(0)) ){ // it is only valid if the type is the same, else return null
                            outObjs.addAll(obj);
                        }else{
                            return null;
                        }
                    }
                }

            }else{ // if it is not a group then just simply assign it and send...

                if (outObjs == null || outObjs.size() == 0){  // If there are no elements, then use the created list from the method
                    outObjs = new LongList();
                }
                
                outObjs.addAll(newGroup.getInstanceIds());
            }
        }
        
        return outObjs;
    }

    protected ElementList retrieveGroupObjectsFromArchive(GroupDetails group) {

        if (archiveService == null || group == null){ // If there's no archive...
            return null;
        }
        
        if (group.getObjectType().equals(GroupHelper.GROUPDEFINITION_OBJECT_TYPE)) {  // Is it a group of groups?
            return this.retrieveGroupObjectsFromArchive(group, null);
        }
        
        return HelperArchive.getObjectBodyListFromArchive(archiveService, group.getObjectType(), group.getDomain(), group.getInstanceIds());
    }

    private ElementList retrieveGroupObjectsFromArchive(GroupDetails group, LongList previousGroupInstances) {

        if (archiveService == null){ // If there's no archive...
            return null;
        }
        
        if (previousGroupInstances == null){ // Is it the first round?
            previousGroupInstances = new LongList();
        }
        
        ElementList outObjs = null;
        
        // Retrieve the new groups from the Archive
        GroupDetailsList newGroups = (GroupDetailsList) HelperArchive.getObjectBodyListFromArchive(archiveService, 
                group.getObjectType(), group.getDomain(), group.getInstanceIds());
        
        for (int index = 0; index < newGroups.size(); index++){
            GroupDetails newGroup = newGroups.get(index);
            
            if (newGroup.getObjectType().equals(GroupHelper.GROUPDEFINITION_OBJECT_TYPE)) {  // Is it a group of groups?

                for (Long objId : newGroup.getInstanceIds()){
                    if (previousGroupInstances.contains( objId )){ // To avoid getting stuck in a loop
                        continue; // It was already checked.. jump to the next one.
                    }
                }

                previousGroupInstances.addAll(newGroup.getInstanceIds());
                
                if (outObjs == null || outObjs.size() == 0){  // If there are no elements, then use the created list from the method
                    outObjs = this.retrieveGroupObjectsFromArchive(group, previousGroupInstances);
                }else{ // If it already exists, then just add to the group
                    ElementList obj = this.retrieveGroupObjectsFromArchive(group, previousGroupInstances);

                    if(obj == null){
                        return null;
                    }
                    
                    if ( outObjs.get(0).getClass().equals(obj.get(0)) ){ // it is only valid if the type is the same, else return null
                        outObjs.addAll(obj);
                    }else{
                        return null;
                    }
                }

            }else{ // if it is not a group then just simply add it...
                outObjs = HelperArchive.getObjectBodyListFromArchive(archiveService, newGroup.getObjectType(), newGroup.getDomain(), newGroup.getInstanceIds());
            }
        }
        
        return outObjs;
    }
    
}
