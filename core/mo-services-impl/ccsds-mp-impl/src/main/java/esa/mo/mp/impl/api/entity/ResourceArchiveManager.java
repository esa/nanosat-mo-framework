package esa.mo.mp.impl.api.entity;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.ResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ResourceDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.ResourceIdentityDetails;
import org.ccsds.moims.mo.mp.structures.ResourceIdentityDetailsList;
import org.ccsds.moims.mo.mp.structures.ResourceUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ResourceUpdateDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.com.COMConfiguration;
import esa.mo.mp.impl.com.pattern.COMStateArchiveManager;

/**
 * Front-end to COM Archive, typed by MP Resource.
 * <p>
 * Extends COMStateArchiveManager, adds methods that are specific to Resource type
 *
 * @see COMStateArchiveManager
 */
public class ResourceArchiveManager extends COMStateArchiveManager<ResourceIdentityDetails, ResourceIdentityDetailsList, ResourceDefinitionDetails, ResourceDefinitionDetailsList, ResourceUpdateDetails, ResourceUpdateDetailsList> {

    public ResourceArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdList listAllIdentityIds() {
        return super.listAllIdentityIds(PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList listAllDefinitionIds() {
        return super.listAllIdentityIds(PlanInformationManagementHelper.RESOURCEDEFINITION_OBJECT_TYPE);
    }

    public ObjectIdList listAllStatusIds() {
        return super.listAllStatusIds(PlanEditHelper.RESOURCEUPDATE_OBJECT_TYPE);
    }

    public ObjectId getIdentityId(Identifier identity) {
        return super.getIdentityId(identity, PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList getIdentityIds(IdentifierList identities) {
        return super.getIdentityIds(identities, PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE);
    }

    public ResourceDefinitionDetails getDefinition(Identifier identity) {
        return super.getDefinition(identity, PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE);
    }

    public ResourceDefinitionDetailsList getDefinitions(IdentifierList identities) {
        return super.getDefinitions(identities, PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE);
    }
}
