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
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestTemplateIdentityDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateIdentityDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.com.COMConfiguration;
import esa.mo.mp.impl.com.pattern.COMStaticItemArchiveManager;

/**
 * Front-end to COM Archive, typed by MP Request Template.
 * <p>
 * Extends COMStaticItemArchiveManager, adds methods that are specific to Request Template type
 *
 * @see COMStaticItemArchiveManager
 */
public class RequestTemplateArchiveManager extends
    COMStaticItemArchiveManager<RequestTemplateIdentityDetails, RequestTemplateIdentityDetailsList, RequestTemplateDetails, RequestTemplateDetailsList> {

    public RequestTemplateArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdList listAllIdentityIds() {
        return super.listAllIdentityIds(PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList listAllDefinitionIds() {
        return super.listAllDefinitionIds(PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);
    }

    public ObjectId getIdentityId(Identifier identity) {
        return super.getIdentityId(identity, PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList getIdentityIds(IdentifierList identities) {
        return super.getIdentityIds(identities, PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE);
    }

    public RequestTemplateDetails getDefinition(Identifier identity) {
        return super.getDefinition(identity, PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE);
    }

    public RequestTemplateDetailsList getDefinitions(IdentifierList identities) {
        return super.getDefinitions(identities, PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE);
    }
}
