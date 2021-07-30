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
package esa.mo.com.impl.util;

import esa.mo.com.impl.provider.ActivityTrackingProviderServiceImpl;
import esa.mo.com.impl.provider.ArchiveProviderServiceImpl;
import esa.mo.com.impl.provider.EventProviderServiceImpl;
import java.util.HashMap;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * The DefinitionsManager class is an abstract class to be extended by Managers
 * that intend to use COM Objects.
 *
 */
public abstract class DefinitionsManager {

    private final HashMap<Long, Element> defs;
    private final EventProviderServiceImpl eventService;
    private final ArchiveProviderServiceImpl archiveService;
    private final ActivityTrackingProviderServiceImpl activityTrackingService;
    private final COMServicesProvider comServices;

    public DefinitionsManager(COMServicesProvider comServices) {
        this.defs = new HashMap<>();

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
    }

    public EventProviderServiceImpl getEventService() {
        return this.eventService;
    }

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
     * Checks if a certain definition exists.
     *
     * @param input The object instance identifier of the definition
     * @return True if exists. False otherwise.
     */
    public synchronized boolean exists(Long input) {
        return defs.containsKey(input);
    }

    /**
     * Lists the object instance identifier for a parameter name identifier.
     *
     * @param input The name identifier of the Definition
     * @return The object instance identifier of the Definition. Null if not
     * found.
     */
    public synchronized Long list(Identifier input) {
        final LongList objIds = this.listAll();
        for (Long objId : objIds) {
            if (compareName(objId, input)) {
                return objId;
            }
        }
        return null; // Not found!
    }

    /**
     * Lists all the definitions available.
     *
     * @return The object instance identifiers of the Definitions.
     */
    public synchronized LongList listAll() {
        LongList list = new LongList();
        list.addAll(defs.keySet());
        return list;
    }

    /**
     * Get all the definition objects available.
     *
     * @return The Definitions.
     */
    public synchronized ElementList getAllDefs() {
        ElementList list = this.newDefinitionList();
        list.addAll(defs.values());
        return list;
    }

    /**
     * Get the definition object.
     *
     * @param objId The object instance identifier
     * @return The Definition
     */
    public synchronized Element getDef(Long objId) {
        return defs.get(objId);
    }

    /**
     * Adds a definition to the manager.
     *
     * @param objId The object instance identifier of the definition
     * @param definition The object body of the definition
     * @return True if successful.
     */
    public synchronized Boolean addDef(Long objId, Element definition) {
        defs.put(objId, definition);
        return true;
    }

    /**
     * Updates a definition in the manager.
     *
     * @param objId The object instance identifier of the definition
     * @param definition The object body of the definition
     * @return True if successful. False if the object instance identifier does
     * not exist in the manager, in this case, the definition is not added.
     */
    public synchronized boolean updateDef(Long objId, Element definition) {
        return (this.deleteDef(objId)) ? this.addDef(objId, definition) : false;
    }

    /**
     * Removes a definition in the manager.
     *
     * @param objId The object instance identifier of the definition
     * @return True if successful. False if the object instance identifier does
     * not exist in the manager.
     */
    public synchronized boolean deleteDef(Long objId) {
        return (defs.remove(objId) != null);
    }

    /**
     * Provides the current set of definitions available in the Manager.
     *
     * @return The definitions set and the corresponding object instance
     * identifiers.
     */
    public synchronized HashMap<Long, Element> getCurrentDefinitionsConfiguration() {
        return new HashMap(defs);
    }

    /**
     * Changes the current set of definitions available by the provided set.
     *
     * @param objIds Object instance identifiers
     * @param definitions The object body of the definitions
     * @return True if the configuration was successfully changed. False
     * otherwise.
     */
    public synchronized Boolean reconfigureDefinitions(LongList objIds, ElementList definitions) {
        if (objIds == null || definitions == null) {
            return false;
        }

        if (objIds.size() != definitions.size()) {
            return false;
        }

        defs.clear();
        for (int i = 0; i < objIds.size(); i++) {
            defs.put(objIds.get(i), (Element) definitions.get(i));
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
    public ObjectId storeCOMOperationActivity(final MALInteraction interaction) {
        if (getActivityTrackingService() != null) {
            return getActivityTrackingService().storeCOMOperationActivity(interaction, null);
        } else {
            return null;
        }
    }

    /**
     * Compares the name of a certain Definition (from the objId) with a name
     * Identifier
     *
     * @param objId Obj Instance Identifier of the Definition to be compared
     * @param name The identifier name to be compared
     * @return True if the objId contains the same name identifier, false
     * otherwise
     */
    public abstract Boolean compareName(Long objId, Identifier name);

    /**
     * Generates a list for a certain type of definitions
     *
     * @return The list of the same definition type
     */
    public abstract ElementList newDefinitionList();

}
