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
package esa.mo.nmf.clitool.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Map;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.ActionHelper;

/**
 * Archive adapter that retrieves available action names and their values.
 *
 * @author marcel.mikolajko
 */
public class ArchiveToActionsAdapter extends ArchiveAdapter implements QueryStatusProvider {

    private static final Logger LOGGER = Logger.getLogger(ArchiveToActionsAdapter.class.getName());

    /**
     * True if the query is over (response or any error received)
     */
    private boolean isQueryOver = false;

    private final ObjectType actionIdentityType = ActionHelper.ACTIONIDENTITY_OBJECT_TYPE;
    private final ObjectType actionDefinitionType = ActionHelper.ACTIONDEFINITION_OBJECT_TYPE;

    private final Map<IdentifierList, List<Identifier>> actionIdentities = new HashMap<>();
    private final Map<IdentifierList, Map<Long, Identifier>> identitiesMap = new HashMap<>();
    private final Map<IdentifierList, Map<Long, Long>> definitionsMap = new HashMap<>();

    @Override
    public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
            ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        if (objDetails == null) {
            setIsQueryOver(true);
            return;
        }
        processObjects(objType, objDetails, objBodies, domain);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
            ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
        processObjects(objType, objDetails, objBodies, domain);
    }

    /**
     * Fills the maps based on the type of the object
     *
     * @param type Type of the objects to be processed
     * @param detailsList Archive details of the objects
     * @param bodiesList Bodies of the objects
     */
    private void processObjects(ObjectType type, ArchiveDetailsList detailsList,
            ElementList bodiesList, IdentifierList domain) {
        if (!actionIdentities.containsKey(domain)) {
            actionIdentities.put(domain, new ArrayList<>());
        }

        if (!identitiesMap.containsKey(domain)) {
            identitiesMap.put(domain, new HashMap<>());
        }

        if (!definitionsMap.containsKey(domain)) {
            definitionsMap.put(domain, new HashMap<>());
        }

        if (type == null || type.equals(actionIdentityType)) {
            for (int i = 0; i < detailsList.size(); ++i) {
                identitiesMap.get(domain).put(detailsList.get(i).getInstId(), (Identifier) bodiesList.get(i));
                actionIdentities.get(domain).add((Identifier) bodiesList.get(i));
            }
        } else if (type.equals(actionDefinitionType)) {
            for (ArchiveDetails archiveDetails : detailsList) {
                definitionsMap.get(domain).put(archiveDetails.getInstId(), archiveDetails.getDetails().getRelated());
            }
        }
    }

    @Override
    public void queryAckErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryAckErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryUpdateErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryUpdateErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public void queryResponseErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        LOGGER.log(Level.SEVERE, "queryResponseErrorReceived", error);
        setIsQueryOver(true);
    }

    @Override
    public boolean isQueryOver() {
        return isQueryOver;
    }

    private synchronized void setIsQueryOver(boolean isQueryOver) {
        this.isQueryOver = isQueryOver;
    }

    public Map<IdentifierList, List<Identifier>> getActionIdentities() {
        return actionIdentities;
    }

    public Map<IdentifierList, Map<Long, Identifier>> getIdentitiesMap() {
        return identitiesMap;
    }
}
