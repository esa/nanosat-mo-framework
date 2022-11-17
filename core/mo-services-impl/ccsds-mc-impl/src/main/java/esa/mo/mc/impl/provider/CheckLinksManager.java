/* 
 * M&C Services for CCSDS Mission Operations Framework
 * Copyright (C) 2021 Deutsches Zentrum fuer Luft- und Raumfahrt e.V. (DLR).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package esa.mo.mc.impl.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetails;

/**
 * To manage the CheckLink and the CheckLinkDefinition Object this
 * CheckLinksManager-class is used. It extends the DefinitionsManger-class which
 * is used to manage the check identity and the actual check definition.
 *
 * @author Vorwerg
 */
class CheckLinksManager extends MCManager {

    //    private final HashMap<Long, Long> checkIdentityDefIds; //contains the CheckIdentity-Id and the CheckDefinition-Id
    private final HashMap<Long, Long> checkLinkIds; //contains the checkLinkId and the CheckLinkDefinitionId
    private final HashMap<Long, ObjectDetails> checkLinkLinks; //contains the checkLinkId and the ObjectDetails
    private final HashMap<Long, CheckLinkDetails> checkLinkDetails; //contains the checkLinkDefinitionId and the CheckLinkDetails

    //CheckLinkDefinitionId <-> CheckLinkDetails (CheckLinkDefinition) => checkLinkDetails
    public CheckLinksManager(COMServicesProvider comServices) {
        super(comServices);
        //        this.checkIdentityDefIds = new HashMap<Long, Long>();
        this.checkLinkDetails = new HashMap<>();
        this.checkLinkIds = new HashMap<>();
        this.checkLinkLinks = new HashMap<Long, ObjectDetails>();
    }

    //    /**
    //     * gets all the checkDefinitions (not actual definition) related to all
    //     * existing identity ids
    //     *
    //     * @return
    //     */
    //    public LongList getAllCheckDefinitionIds() {
    //        return (LongList) checkIdentityDefIds.values();
    //    }
    public LongList listAllCheckLinkIds() {
        LongList checkLinkIdList = new LongList();
        checkLinkIdList.addAll(checkLinkIds.keySet());
        return checkLinkIdList;
    }

    public List<ObjectDetails> getAllCheckLinkLinks() {
        return (ArrayList<ObjectDetails>) checkLinkLinks.values();
    }

    public List<CheckLinkDetails> getAllCheckLinkDetails() {
        return (ArrayList<CheckLinkDetails>) checkLinkDetails.values();
    }

    //    /**
    //     * gets the checkDefinition (not actual definition) related to the given
    //     * identity id
    //     *
    //     * @param identityId id of the identity
    //     * @return id of the checkDefiniton realted to the given id
    //     */
    //    public Long getCheckDefinitionId(Long identityId) {
    //        return checkIdentityDefIds.get(identityId);
    //    }
    //    /**
    //     * gets the CheckIdentity of a given CheckDefinition (not actual definition)
    //     * @param definitionId the id of the CheckDefinition (not actual definition)
    //     * @return the id of the CheckIdentity or NULL if not found
    //     */
    //    public Long getCheckIdentityId(Long definitionId){
    //        for (Long identityId : checkIdentityDefIds.keySet()) {
    //            if (checkIdentityDefIds.get(identityId).equals(definitionId))
    //                return identityId;
    //        }
    //        return null;
    //    }
    public Long getCheckLinkDefId(Long checkLinkId) {
        return this.checkLinkIds.get(checkLinkId);
    }

    /**
     * Checks if a checkLink-object with the given Id exists in the internal
     * lists.
     *
     * @param checkLinkId
     * @return
     */
    public boolean existsCheckLink(Long checkLinkId) {
        return this.checkLinkIds.containsKey(checkLinkId);
    }

    public Long getCheckLinkId(Long checkLinkDefId) {
        for (Map.Entry<Long, Long> entry : checkLinkIds.entrySet()) {
            if (entry.getValue() == null && checkLinkDefId == null) {
                return entry.getKey();
            }
            if (entry.getValue() != null && entry.getValue().equals(checkLinkDefId)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public ObjectDetails getCheckLinkLinks(Long checkLinkId) {
        return this.checkLinkLinks.get(checkLinkId);
    }

    public CheckLinkDetails getCheckLinkDetails(Long checkLinkDefId) {
        return this.checkLinkDetails.get(checkLinkDefId);
    }

    //    /**
    //     * sets the given definitionId (not actual Definition Id) to the existing
    //     * identityId in the internal list
    //     *
    //     * @param identityId
    //     * @param defId id of the definition (not actual Definition) to be added to
    //     * the identity
    //     */
    //    protected void updateCheckIdentityDefPair(Long identityId, Long defId) {
    //        this.checkIdentityDefIds.put(identityId, defId);
    //    }
    protected void addCheckLink(Long checkLinkId, ObjectDetails objDetails, Long checkLinkDefId,
        CheckLinkDetails checkLinkDetails) {
        this.checkLinkIds.put(checkLinkId, checkLinkDefId);
        this.checkLinkLinks.put(checkLinkId, objDetails);
        this.checkLinkDetails.put(checkLinkDefId, checkLinkDetails);
    }

    /**
     * updates the checkLinks-links (source, related)
     *
     * @param checkLinkId
     * @param objectDetails
     */
    protected void updateCheckLinkLinks(Long checkLinkId, ObjectDetails objectDetails) {
        this.checkLinkLinks.put(checkLinkId, objectDetails);
    }

    /**
     * updates the details of the checkLink
     *
     * @param checkLinkDefId the defId of the CheckLinkDefinition-object
     * @param checkLinkDetails The new details for the checkLink
     */
    protected void updateCheckLinkDetails(Long checkLinkDefId, CheckLinkDetails checkLinkDetails) {
        this.checkLinkDetails.put(checkLinkDefId, checkLinkDetails);
    }

    /**
     * Updates the id of the of the checkLinkDefinition in the internal lists,
     * as well as the details of the checkLink
     *
     * @param checkLinkId the checklinks id
     * @param newCheckLinkDefId the defId of the new CheckLinkDefinition-object
     * @param checkLinkDetails The new details for the checkLink
     */
    protected void updateCheckLink(Long checkLinkId, Long newCheckLinkDefId, CheckLinkDetails checkLinkDetails) {
        final Long oldCheckLinkDefid = checkLinkIds.get(checkLinkId);
        this.checkLinkIds.put(checkLinkId, newCheckLinkDefId);
        this.checkLinkDetails.remove(oldCheckLinkDefid);
        this.checkLinkDetails.put(newCheckLinkDefId, checkLinkDetails);
    }

    /**
     * updates the checkLinkIds. Which CheckLinks-Definition-Id belongs to which
     * checkLink-id
     *
     * @param checkLinkId
     * @param checkLinkDefId
     */
    protected void updateCheckLinkIds(Long checkLinkId, Long checkLinkDefId) {
        this.checkLinkIds.put(checkLinkId, checkLinkDefId);
    }

    //    /**
    //     * deletes the given identity id and its definition id (not actual
    //     * Definition Id) from the internal list
    //     *
    //     * @param identityId the identity if the check to be deleted
    //     */
    //    protected void deleteCheckIdentity(Long identityId) {
    //        this.checkIdentityDefIds.remove(identityId);
    //    }
    /**
     * deletes the ObjectDetails of the checkLink with the given id from the
     * internal list, as well as the CheckLinkDefinitionDetails and the
     * checkLink and checkLinkDefinition-id
     *
     * @param checkLinkId id of the checkLink to be deleted
     */
    protected void deleteCheckLink(Long checkLinkId) {
        Long defId = getCheckLinkDefId(checkLinkId);
        this.checkLinkDetails.remove(defId);
        this.checkLinkLinks.remove(checkLinkId);
        this.checkLinkIds.remove(checkLinkId);
    }

    //    /**
    //     * deletes all the identity ids and its definition id (not actual
    //     * Definition Id) from the internal list
    //     *
    //     */
    //    protected void deleteAllCheckIdentities() {
    //        this.checkIdentityDefIds.clear();
    //    }
    /**
     * deletes all the ObjectDetails from the internal list
     */
    protected void deleteAllCheckLinks() {
        this.checkLinkLinks.clear();
        this.checkLinkDetails.clear();
        this.checkLinkIds.clear();
    }

}
