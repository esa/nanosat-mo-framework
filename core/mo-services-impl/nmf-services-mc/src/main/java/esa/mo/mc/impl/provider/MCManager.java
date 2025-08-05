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

import esa.mo.com.impl.provider.ActivityTrackingProviderServiceImpl;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.provider.EventProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mc.impl.util.GroupRetrieval;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * Class that manages the local map of identities and its definitions.
 *
 */
public abstract class MCManager {

    private final HashMap<Long, Identifier> idToName;
    private final HashMap<Identifier, Long> nameToId;
    private final HashMap<Long, Element> objIdToDef;

    private final EventProviderServiceImpl eventService;
    private final ArchiveProviderServiceImpl archiveService;
    private final ActivityTrackingProviderServiceImpl activityTrackingService;
    private final COMServicesProvider comServices;
    private final GroupServiceImpl groupService = new GroupServiceImpl();

    protected MCManager(COMServicesProvider comServices) {
        this.nameToId = new HashMap<>();
        this.objIdToDef = new HashMap<>();
        this.idToName = new HashMap<>();

        if (comServices != null) {
            this.eventService = comServices.getEventService();
            this.archiveService = comServices.getArchiveService();
            this.activityTrackingService = comServices.getActivityTrackingService();
            this.comServices = comServices;
        } else {
            this.eventService = null;
            this.archiveService = null;
            this.activityTrackingService = null;
            this.comServices = null;
        }
        try {
            groupService.init(archiveService);
        } catch (MALException ex) {
            Logger.getLogger(MCManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public EventProviderServiceImpl getEventService() {
        return this.eventService;
    }

    //TODO: synchrinized added; does the problem, that the updateDefinition-operation 
    //sometimes doesnt find the definition, that was right before updated? -> problem
    public ArchiveProviderServiceImpl getArchiveService() {
        return this.archiveService;
    }

    public ActivityTrackingProviderServiceImpl getActivityTrackingService() {
        return this.activityTrackingService;
    }

    public COMServicesProvider getCOMServices() {
        return this.comServices;
    }

    /**
     * Checks if a certain object instance identifier definition exists.
     *
     * @param objId The object instance identifier.
     * @return True if exists. False otherwise.
     */
    public synchronized boolean existsDef(final Long objId) {
        return this.objIdToDef.containsKey(objId);
    }

    public synchronized Identifier getName(Long objId) {
        return this.idToName.get(objId);
    }

    /**
     * Gets the details of the definition with the given objId.
     *
     * @param objId The id of the definition.
     * @return The Definition or Null if not found.
     */
    public synchronized Element getDefinition(Long objId) {
        return this.objIdToDef.get(objId);
    }

    /**
     * Gets the details of the definition with the given the name.
     *
     * @param name The name of the definition.
     * @return The Definition or Null if not found.
     */
    public synchronized Element getDefinition(Identifier name) {
        Long id = this.nameToId.get(name);
        return this.objIdToDef.get(id);
    }

    public synchronized Long getId(Identifier name) {
        return this.nameToId.get(name);
    }

    /**
     * Lists all the defintion-ids available.
     *
     * @return The object instance identifiers of the identities.
     */
    public synchronized LongList listAllDefinitions() {
        LongList list = new LongList();
        list.addAll(this.objIdToDef.keySet());
        return list;
    }

    /**
     * Adds a definition to the manager
     *
     * @param name the name of the Definition.
     * @param id the id of the Definition.
     * @param defDetails the Definition.
     * @return True if successful. False otherwise.
     */
    protected synchronized Boolean addDefinitionLocally(
            final Identifier name, Long id, final Element defDetails) {
        this.idToName.put(id, name);
        this.nameToId.put(name, id);
        this.objIdToDef.put(id, defDetails);
        return true;
    }

    protected synchronized Boolean deleteDefinitionLocally(Long id) {
        Identifier name = this.getName(id);
        this.idToName.remove(id);
        this.nameToId.remove(name);
        this.objIdToDef.remove(id);
        return true;
    }

    /**
     * Updates a definition of an existing identity in the manager.
     *
     * @param id The id the definition.
     * @param newDefDetails The object body of the new definition
     * @return True if successful. False if the object instance identifier does
     * not exist in the manager, in this case, the definition is not added.
     */
    protected synchronized boolean updateDef(Long id, Element newDefDetails) {
        if (id == null) {
            return false;
        }

        // We are good! Remove the previous definition and add the new one...
        Identifier name = this.getName(id);
        deleteDefinitionLocally(id);
        addDefinitionLocally(name, id, newDefDetails);
        /*
        this.objIdToDef.remove(id);
        this.objIdToDef.put(id, newDefDetails);
        this.idToName.remove(id);
        this.idToName.put(id, newDefDetails);
        this.nameToId.remove(name);
        this.nameToId.put(id, newDefDetails);
        */
        return true;
    }

    /**
     * Provides the current set of definitions available in the Manager.
     *
     * @param objTypeDef The object type of the definitions.
     * @return The definitions set and the corresponding object instance
     * identifiers.
     */
    public synchronized ConfigurationObjectSetList getCurrentConfiguration(ObjectType objTypeDef) {
        LongList defObjIds = new LongList();

        for (Long id : this.nameToId.values()) {
            defObjIds.add(id);
        }

        LongList currentObjIds1 = new LongList();
        currentObjIds1.addAll(defObjIds);
        ConfigurationObjectSet defis = new ConfigurationObjectSet(objTypeDef,
                ConfigurationProviderSingleton.getDomain(), currentObjIds1);

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(defis);

        return list;
    }

    /**
     * Changes the current set of definitions available by the provided set.
     *
     * @param names the names of the parameters to be set
     * @param defIds the definitionsIds of the parameters to be set
     * @param definitions The object body of the definitions to be set
     * @return True if the configuration was successfully changed. False
     * otherwise.
     */
    public synchronized Boolean reconfigureDefinitions(
            final IdentifierList names, final LongList defIds, final ElementList definitions) {
        if (names == null || defIds == null || definitions == null) {
            return false;
        }

        if (defIds.size() != names.size()
                && definitions.size() != defIds.size()) {
            return false;
        }

        this.idToName.clear();
        this.nameToId.clear();
        this.objIdToDef.clear();

        for (int i = 0; i < defIds.size(); i++) {
            this.idToName.put(defIds.get(i), names.get(i));
            this.nameToId.put(names.get(i), defIds.get(i));
            this.objIdToDef.put(defIds.get(i), (Element) definitions.get(i));
        }

        return true;
    }

    /**
     * Stores the COM Operation Activity object in the Archive
     *
     * @param interaction The MALInteraction object for the operation
     * @return The link to the stored COM Operation Activity. Null if not
     * stored.
     */
    protected ObjectId storeCOMOperationActivity(final MALInteraction interaction) {
        if (getActivityTrackingService() == null) {
            return null;
        }

        return getActivityTrackingService().storeCOMOperationActivity(interaction, null);
    }

    /**
     * This method is getting all instances contained in the given groups. It
     * also does the checking for invalid or unknown identities or entries. Just
     * as defined the "enableGeneration" ServiceOperation definition. The
     * requirements in these operations defined in the different Services only
     * differ in the identities object type of the objects contained in the
     * group. So you have to give the ObjectType of the service object identity.
     *
     * TODO : put this method in some GroupHelper - class
     *
     * @param enableInstances is the list that is used for filling the
     * retrievalinformation object of the group
     * @param groupRetrievalInformation contains an empty object to be filled.
     * with the group-information about: unknown and invalid errors and objects
     * and values that will be enabled/disabled
     * @param identyObjectType the objectType of the identity-object of which
     * the objects contained in the groups are
     * @param domain the domain to look for identityIds in the archive.
     * @param allIdentities the list of all available identities, the groups may
     * contain. E.g. in the ParameterService´enableGeneration this parameter
     * contains all available ParameterIdentities.
     * @return The group retrieval.
     */
    public GroupRetrieval getGroupInstancesForServiceOperation(InstanceBooleanPairList enableInstances,
            GroupRetrieval groupRetrievalInformation, ObjectType identyObjectType,
            IdentifierList domain, LongList allIdentities) {
        //in the next for loop, ignore the other group identities, those will be checked in other iterations.
        LongList ignoreList = new LongList();
        for (InstanceBooleanPair instance : enableInstances) {
            ignoreList.add(instance.getId());
        }
        for (int index = 0; index < enableInstances.size(); index++) {
            //these are Group-Identity-ids req: 3.9.4.g,h
            InstanceBooleanPair enableInstance = enableInstances.get(index);
            final Long groupIdentityId = enableInstance.getId();
            GroupDetails group = groupService.retrieveGroupDetailsFromArchive(domain, groupIdentityId);

            if (group == null) { //group wasnt found
                groupRetrievalInformation.addUnkIndex(new UInteger(index)); // requirement: 3.3.10.2.g
                continue;
            }

            // If group was found, then get the instances of it and its groups
            ignoreList.remove(groupIdentityId);
            GroupServiceImpl.IdObjectTypeList idObjectTypes = groupService.getGroupObjectIdsFromGroup(
                    groupIdentityId, group, ignoreList);
            ignoreList.add(groupIdentityId);
            //checks if the given identityId is found in the internal Identity-list of the specific 
            //service, if not then the object doesnt belong to the service and is invalid
            for (GroupServiceImpl.IdObjectType idObjectType : idObjectTypes) {
                if (!idObjectType.getObjectType().equals(identyObjectType)) {
                    groupRetrievalInformation.addInvIndex(new UInteger(index)); // requirement: 3.3.10.2.h
                    break;
                }

                final Long identityId = idObjectType.getId();
                //checks if the identityId referenced in the group is known
                if (!allIdentities.contains(identityId)) {// requirement: 3.3.10.2.g
                    groupRetrievalInformation.addUnkIndex(new UInteger(index));
                }
                if (!groupRetrievalInformation.getObjIdToBeEnabled().contains(identityId)) {
                    groupRetrievalInformation.addObjIdToBeEnabled(identityId);
                    groupRetrievalInformation.addValueToBeEnabled(enableInstance.getValue());
                }
            }
        }
        return groupRetrievalInformation;
    }

    /**
     * This method is getting all instances contained in the given groups. The
     * only difference to the other method (check the see part) is: you don't
     * care about the values and just want to get the identities referenced by
     * the groups.
     *
     * @param groupIds The group ids.
     * @param groupRetrievalInformation The group retrieval information.
     * @param identyObjectType The identity object type.
     * @param domain The domain.
     * @param allIdentities The identities to be retrieved.
     * @return The group retrieval.
     */
    public GroupRetrieval getGroupInstancesForServiceOperation(LongList groupIds,
            GroupRetrieval groupRetrievalInformation, ObjectType identyObjectType,
            IdentifierList domain, LongList allIdentities) {
        //if you cont care about the values and just want to get the identities referenced by the groups:
        InstanceBooleanPairList instBoolPairList = new InstanceBooleanPairList(groupIds.size());
        for (Long enableInstance : groupIds) {
            instBoolPairList.add(new InstanceBooleanPair(enableInstance, Boolean.TRUE));
        }

        return getGroupInstancesForServiceOperation(instBoolPairList,
                groupRetrievalInformation, identyObjectType, domain, allIdentities);
    }

}
