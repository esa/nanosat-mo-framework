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
package esa.mo.mp.impl.api.entity;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.EventIdentityDetails;
import org.ccsds.moims.mo.mp.structures.EventIdentityDetailsList;
import org.ccsds.moims.mo.mp.structures.EventInstanceDetails;
import org.ccsds.moims.mo.mp.structures.EventUpdateDetails;
import org.ccsds.moims.mo.mp.structures.EventUpdateDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.com.COMConfiguration;
import esa.mo.mp.impl.com.pattern.COMInstanceArchiveManager;

/**
 * Front-end to COM Archive, typed by MP Event.
 * <p>
 * Extends COMInstanceArchiveManager, adds methods that are specific to Event type
 *
 * @see COMInstanceArchiveManager
 */
public class EventArchiveManager extends
    COMInstanceArchiveManager<EventIdentityDetails, EventIdentityDetailsList, EventDefinitionDetails, EventDefinitionDetailsList, EventInstanceDetails, EventDefinitionDetailsList, EventUpdateDetails, EventUpdateDetailsList> {

    public EventArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdList listAllIdentityIds() {
        return super.listAllIdentityIds(PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList listAllDefinitionIds() {
        return super.listAllIdentityIds(PlanInformationManagementHelper.EVENTDEFINITION_OBJECT_TYPE);
    }

    public ObjectIdList listAllInstanceIds() {
        return super.listAllInstanceIds(PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE);
    }

    public ObjectIdList listAllStatusIds() {
        return super.listAllStatusIds(PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE);
    }

    public ObjectId getIdentityId(Identifier identity) {
        return super.getIdentityId(identity, PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList getIdentityIds(IdentifierList identities) {
        return super.getIdentityIds(identities, PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE);
    }

    public EventDefinitionDetails getDefinition(Identifier identity) {
        return super.getDefinition(identity, PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE);
    }

    public EventDefinitionDetailsList getDefinitions(IdentifierList identities) {
        return super.getDefinitions(identities, PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE);
    }
}
