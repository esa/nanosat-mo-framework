package esa.mo.mp.impl.api.entity;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityIdentityDetails;
import org.ccsds.moims.mo.mp.structures.ActivityIdentityDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetailsList;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.com.COMConfiguration;
import esa.mo.mp.impl.com.pattern.COMInstanceArchiveManager;

/**
 * Front-end to COM Archive, typed by MP Activity.
 * <p>
 * Extends COMInstanceArchiveManager, adds methods that are specific to Activity type
 *
 * @see COMInstanceArchiveManager
 */
public class ActivityArchiveManager extends COMInstanceArchiveManager<ActivityIdentityDetails, ActivityIdentityDetailsList, ActivityDefinitionDetails, ActivityDefinitionDetailsList, ActivityInstanceDetails, ActivityInstanceDetailsList, ActivityUpdateDetails, ActivityUpdateDetailsList> {

    public ActivityArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdList listAllIdentityIds() {
        return super.listAllIdentityIds(PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList listAllDefinitionIds() {
        return super.listAllIdentityIds(PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE);
    }

    public ObjectIdList listAllInstanceIds() {
        return super.listAllInstanceIds(PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);
    }

    public ObjectIdList listAllStatusIds() {
        return super.listAllStatusIds(PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);
    }

    public ObjectId getIdentityId(Identifier identity) {
        return super.getIdentityId(identity, PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList getIdentityIds(IdentifierList identities) {
        return super.getIdentityIds(identities, PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE);
    }

    public ActivityDefinitionDetails getDefinition(Identifier identity) {
        return super.getDefinition(identity, PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE);
    }

    public ActivityDefinitionDetailsList getDefinitions(IdentifierList identities) {
        return super.getDefinitions(identities, PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE);
    }
}
