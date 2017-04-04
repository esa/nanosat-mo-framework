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

import esa.mo.com.impl.provider.ActivityTrackingProviderServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.provider.EventProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.mc.impl.provider.model.GroupRetrieval;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSet;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSetList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 * Class that manages the local map of identities and its definitions.
 *
 */
public abstract class DefinitionsManager {

    //An Identity always has exactly one active Definition. Never 0 and never > 1.
    private final HashMap<Identity, Definition> identityDefinitions;
//    private final HashMap<Long, Identifier> identitiesMap;
//    private final HashMap<Long, Element> definitionsMap;
    private final EventProviderServiceImpl eventService;
    private final ArchiveProviderServiceImpl archiveService;
    private final ActivityTrackingProviderServiceImpl activityTrackingService;
    private final COMServicesProvider comServices;
    private final GroupServiceImpl groupService = new GroupServiceImpl();

    protected DefinitionsManager(COMServicesProvider comServices) {
        this.identityDefinitions = new HashMap<Identity, Definition>();

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
            Logger.getLogger(DefinitionsManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public EventProviderServiceImpl getEventService() {
        return this.eventService;
    }

    //TODO: synchrinized added; does the problem, that the updateDefinition-operation sometimes doesnt find the definition, that was right before updated? -> problem
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
     * instances of the identity-class will be persisted in the local provider
     * list
     */
    protected class Identity {

        public Identity() {
        }

        public Identity(Long id, Identifier name) {
            this.id = id;
            this.name = name;
        }

        private Long id;
        private Identifier name;

        public Long getId() {
            return id;
        }

        public Identifier getName() {
            return name;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setName(Identifier name) {
            this.name = name;
        }
    }

    /**
     * instances of the definitions-objects will be persisted in the local
     * provider list
     */
    protected class Definition {

        public Definition() {
        }

        public Definition(Long id, Element details) {
            this.id = id;
            this.details = details;
        }

        private Long id;
        private Element details;

        public Long getId() {
            return id;
        }

        public Element getDetails() {
            return details;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public void setDetails(Element details) {
            this.details = details;
        }

    }

    /**
     * Checks if a certain identity exists.
     *
     * @param identityId The object instance identifier of the identity
     * @return True if exists. False otherwise.
     */
    protected synchronized boolean existsIdentity(Long identityId) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identity.getId().equals(identityId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lists the object instance identifier for an identities name.
     *
     * @param name The name of the identity
     * @return The object instance identifier of the identity. Null if not
     * found.
     */
    public synchronized Long getIdentity(Identifier name) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identity.getName().equals(name)) {
                return identity.getId();
            }
        }
        return null; // Not found!
    }

    /**
     * Lists the object instance identifier for an identity that will be looked
     * for by the id of its current definition.
     *
     * @param defId the id of a current definition
     * @return The object instance identifier of the identity. Null if defId not
     * found.
     */
    public synchronized Long getIdentity(Long defId) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identityDefinitions.get(identity).getId().equals(defId)) {
                return identity.getId();
            }
        }
        return null; // Not found!
    }

    /**
     * Lists the object instance identifier of an identity and its definition
     * for an identities name.
     *
     * @param name The name of the identity
     * @return The object instance identifier of the identity. Null if not
     * found.
     */
    public synchronized ObjectInstancePair getIdentityDefinition(Identifier name) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identity.getName().equals(name)) {
                Definition def = identityDefinitions.get(identity);
                return new ObjectInstancePair(identity.getId(), def.getId());
            }
        }
        return null; // Not found!
    }

    /**
     * gets a map with all the current identity-objects with its
     * defintion-objects
     *
     * @return the map with all identity-defintions
     */
    public synchronized HashMap<Identity, Definition> getIdentityDefinitions() {
        return new HashMap(identityDefinitions);
    }

    /**
     * Gets the details of the definition with the given id.
     *
     * @param identityId the id of the identity you want the details from
     * @return the definition-details. Or Null if not found.
     */
    public synchronized Element getDefinition(Long identityId) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identity.getId().equals(identityId)) {
                Definition def = identityDefinitions.get(identity);
                return def.getDetails();
            }
        }
        return null; // Not found!
    }

    /**
     * Gets the details of the definition with the given id.
     *
     *
     * @param identityId the id of the identity you want the details from
     * @return the definition-details. Or Null if not found.
     */
    public synchronized Long getDefinitionId(Long identityId) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identity.getId().equals(identityId)) {
                Definition def = identityDefinitions.get(identity);
                return def.getId();
            }
        }
        return null; // Not found!
    }

    /**
     * returns the name of the definition
     *
     * @param identityId the id of the definitions identity
     * @return the name-field of the definitions identity
     */
    public synchronized Identifier getName(Long identityId) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identity.getId().equals(identityId)) {
                return identity.getName();
            }
        }
        return null; // Not found!
    }
	
	public synchronized Identifier getNameFromDefId(Long defId) {
		for (Identity identity : identityDefinitions.keySet()) {
			Definition definition = identityDefinitions.get(identity);
			if (definition.id.equals(defId)) {
				return identity.getName();
			}
		}
        return null; // Not found!
	}

    /**
     * Lists all the identities available.
     *
     * @return The object instance identifiers of the identities.
     */
    public synchronized ObjectInstancePairList listAllIdentityDefinitions() {
        ObjectInstancePairList list = new ObjectInstancePairList();
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            Definition def = identityDefinitions.get(identity);
            list.add(new ObjectInstancePair(identity.getId(), def.getId()));
        }
        return list;
    }

    /**
     * Lists all the identity-ids available.
     *
     * @return The object instance identifiers of the identities.
     */
    public synchronized LongList listAllIdentities() {
        LongList list = new LongList();
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            list.add(identity.getId());
        }
        return list;

    }

    /**
     * Lists all the defintion-ids available.
     *
     * @return The object instance identifiers of the identities.
     */
    public synchronized LongList listAllDefinitions() {
        LongList list = new LongList();
        final Collection<Definition> definitions = identityDefinitions.values();
        for (Definition definition : definitions) {
            list.add(definition.getId());
        }
        return list;
    }

    /**
     * Adds an identity-object and its definition-object to the manager
     *
     * @param identityId the object instance identifier of the Identity
     * @param name the name of the Identity
     * @param defId the object instance identifier of the definition
     * @param defDetails the definitionDetails
     * @return
     */
    protected synchronized Boolean addIdentityDefinition(Long identityId, Identifier name, Long defId, Element defDetails) {
        Identity identity = new Identity(identityId, name);
        Definition def = new Definition(defId, defDetails);
        identityDefinitions.put(identity, def);

        return true;
    }

    /**
     * Updates a definition of an existing identity in the manager.
     *
     * @param identityId the identity the definition should be updated of
     * @param newDefId The object instance identifier of the new definition
     * @param newDefDetails The object body of the new definition
     * @return True if successful. False if the object instance identifier does
     * not exist in the manager, in this case, the definition is not added.
     */
    protected synchronized boolean updateDef(Long identityId, Long newDefId, Element newDefDetails) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identity.getId().equals(identityId)) {
                final Definition def = new Definition(newDefId, newDefDetails);
                identityDefinitions.replace(identity, def);
                return true;
            }
        }
        return false;
    }

    /**
     * Removes a identity and its definition in the manager.
     *
     * @param identityId The object instance identifier of the identity
     * @return True if successful. False if the object instance identifier does
     * not exist in the manager.
     */
    protected synchronized boolean deleteIdentity(Long identityId) {
        final Set<Identity> identities = identityDefinitions.keySet();
        for (Identity identity : identities) {
            if (identity.getId().equals(identityId)) {
                identityDefinitions.remove(identity);
                return true;
            }
        }
        return false;
    }

    /**
     * Provides the current set of definitions available in the Manager.
     *
     * @return The definitions set and the corresponding object instance
     * identifiers.
     */
    @Deprecated
    public synchronized HashMap<Identity, Definition> getCurrentDefinitionsConfiguration() {
        return new HashMap(identityDefinitions);
    }

    /**
     * Provides the current set of definitions available in the Manager.
     *
     * @return The definitions set and the corresponding object instance
     * identifiers.
     */
    public synchronized ConfigurationObjectSetList getCurrentConfiguration() {
        Set<Identity> ids = identityDefinitions.keySet();
        Collection<Definition> defs = identityDefinitions.values();
        
        LongList idObjIds = new LongList();
        LongList defObjIds = new LongList();
        
        for(Identity id : ids){
            idObjIds.add(id.getId());
        }

        for(Definition def : defs){
            defObjIds.add(def.getId());
        }

        ConfigurationObjectSet idents = new ConfigurationObjectSet();
        idents.setDomain(ConfigurationProviderSingleton.getDomain());
        idents.setObjInstIds(idObjIds);

        LongList currentObjIds1 = new LongList();
        currentObjIds1.addAll(defObjIds);
        ConfigurationObjectSet defis = new ConfigurationObjectSet();
        defis.setDomain(ConfigurationProviderSingleton.getDomain());
        defis.setObjInstIds(currentObjIds1);
      
        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(idents);
        list.add(defis);
        
        return list;
    }

    /**
     * Changes the current set of definitions available by the provided set.
     *
     * @param identityIds the identityIds of the parameters to be set
     * @param names the names of the parameters to be set
     * @param defIds the definitionsIds of the parameters to be set
     * @param definitions The object body of the definitions to be set
     * @return True if the configuration was successfully changed. False
     * otherwise.
     */
    public synchronized Boolean reconfigureDefinitions(LongList identityIds, IdentifierList names, LongList defIds, ElementList definitions) {
        if (identityIds == null || names == null || defIds == null || definitions == null) {
            return false;
        }

        if (identityIds.size() != names.size() && defIds.size() != definitions.size() && identityIds.size() != defIds.size()) {
            return false;
        }

        identityDefinitions.clear();
        for (int i = 0; i < identityIds.size(); i++) {
            final Identity identity = new Identity(identityIds.get(i), names.get(i));
            final Definition def = new Definition(defIds.get(i), (Element) definitions.get(i));
            identityDefinitions.put(identity, def);
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
        if (getActivityTrackingService() != null) {
            return getActivityTrackingService().storeCOMOperationActivity(interaction, null);
        } else {
            return null;
        }
    }

    /**
     * Queries the archive for a Identity-Object with the given name.
     *
     * TODO: make the method more efficient, by actually querying the archive.
     * TODO: put the method in some archive-helper-class (after independent from
     * ArchiveServiceImplementation)
     *
     * @param domain the domain to look at.
     * @param name the name of the Identity to be looked for.
     * @param identitysObjectType the objectType of the identity, you want to
     * have the identityId from (e.g. ParameterIdentity-ObjectType
     * @return The id of the Identity-Object with the given name if it exists in
     * the archive. NULL otherwise.
     */
    protected Long retrieveIdentityIdByNameFromArchive(IdentifierList domain, Identifier name, ObjectType identitysObjectType) {
        final ArchiveProviderServiceImpl archive = getArchiveService();
        if (archive == null) { // If there's no archive...
            return null;
        }
        //get all identity-objects with the given objectType
        LongList identityIds = new LongList();
        identityIds.add(0L);
        final List<ArchivePersistenceObject> identityArchiveObjs = HelperArchive.getArchiveCOMObjectList(archive, identitysObjectType, domain, identityIds);
        if (identityArchiveObjs == null) {
            return null;
        }
        //get the  Identity with the given name
        for (ArchivePersistenceObject identityArchiveObj : identityArchiveObjs) {
            final Identifier objArchiveName = (Identifier) identityArchiveObj.getObject();
            if (objArchiveName.equals(name)) //return the id of the  Identity with the given name
            {
                return identityArchiveObj.getObjectId();
            }
        }
        return null;
    }

    /**
     * This method is getting all instances contained in the given groups. It
     * also does the checking for invalid or unknown identities or entries. Just
     * as defined the "enableGeneration"- ServiceOperation´s definition. The
     * requirements in these operations defined in the different Services only
     * differ in the identiy´s objecttype of the objects contained in the group.
     * So you have to give the ObjectType of the service-object´s identity.
     *
     * TODO : put this method in some GroupHelper - class
     *
     * @param enableInstances is the (groupid,boolean)-pair-list that is used
     * for filling the group-retrievalinformation-object
     * @param groupRetrievalInformation contains an empty object to be filled.
     * with the group-information about: unknown and invalid errors and objects
     * and values that will be enabled/disabled
     * @param identyObjectType the objectType of the identity-object of which
     * the objects contained in the groups are
     * @param domain the domain to look for identityIds in the archive.
     * @param allIdentities the list of all available identities, the groups may
     * contain. E.g. in the ParameterService´enableGeneration this parameter
     * contains all available ParameterIdentities.
     * @return
     */
    public GroupRetrieval getGroupInstancesForServiceOperation(InstanceBooleanPairList enableInstances, GroupRetrieval groupRetrievalInformation, ObjectType identyObjectType, IdentifierList domain, LongList allIdentities) {
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
            } else { //if group was found, then get the instances of it and its groups
                ignoreList.remove(groupIdentityId);
                GroupServiceImpl.IdObjectTypeList idObjectTypes = groupService.getGroupObjectIdsFromGroup(groupIdentityId, group, ignoreList);
                ignoreList.add(groupIdentityId);
                //checks if the given identityId is found in the internal Identity-list of the specific service, if not then the object doesnt belong to the service and is invalid
                for (GroupServiceImpl.IdObjectType idObjectType : idObjectTypes) {
                    if (idObjectType.getObjectType().equals(identyObjectType)) {
                        final Long identityId = idObjectType.getId();
                        //checks if the identityId referenced in the group is known
                        if (!allIdentities.contains(identityId)) {// requirement: 3.3.10.2.g
                            groupRetrievalInformation.addUnkIndex(new UInteger(index));
                        }
                        if (!groupRetrievalInformation.getObjIdToBeEnabled().contains(identityId)) {
                            groupRetrievalInformation.addObjIdToBeEnabled(identityId);
                            groupRetrievalInformation.addValueToBeEnabled(enableInstance.getValue());
                        }
                    } else {
                        groupRetrievalInformation.addInvIndex(new UInteger(index)); // requirement: 3.3.10.2.h
                        break;
                    }
                }
            }
        }
        return groupRetrievalInformation;
    }

    /**
     * This method is getting all instances contained in the given groups. The
     * only difference to the other method (see the "see also part") is: you
     * dont care about the values and just want to get the identities referenced
     * by the groups
     *
     * @see getGroupInstancesForServiceOperation(InstanceBooleanPairList
     * enableInstances, GroupRetrieval groupRetrievalInformation, ObjectType
     * identyObjectType, IdentifierList domain, LongList allIdentities)
     *
     * @param groupIds
     * @param groupRetrievalInformation
     * @param identyObjectType
     * @param domain
     * @param allIdentities
     * @return
     */
    public GroupRetrieval getGroupInstancesForServiceOperation(LongList groupIds, GroupRetrieval groupRetrievalInformation, ObjectType identyObjectType, IdentifierList domain, LongList allIdentities) {
        //if you cont care about the values and just want to get the identities referenced by the groups:
        InstanceBooleanPairList instBoolPairList = new InstanceBooleanPairList();
        for (Long enableInstance : groupIds) {
            instBoolPairList.add(new InstanceBooleanPair(enableInstance, Boolean.TRUE));
        }
        return getGroupInstancesForServiceOperation(instBoolPairList, groupRetrievalInformation, identyObjectType, domain, allIdentities);
    }

}
