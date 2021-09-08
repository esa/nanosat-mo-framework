package esa.mo.mp.impl.api.entity;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionHelper;
import org.ccsds.moims.mo.mp.structures.PlanIdentityDetails;
import org.ccsds.moims.mo.mp.structures.PlanIdentityDetailsList;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.com.COMConfiguration;
import esa.mo.mp.impl.com.pattern.COMInstanceVersionArchiveManager;

/**
 * Front-end to COM Archive, typed by MP Plan.
 * <p>
 * Extends COMInstanceVersionArchiveManager, adds methods that are specific to Plan type
 *
 * @see COMInstanceVersionArchiveManager
 */
public class PlanVersionArchiveManager extends COMInstanceVersionArchiveManager<PlanIdentityDetails, PlanIdentityDetailsList, PlanVersionDetails, PlanVersionDetailsList, PlanUpdateDetails, PlanUpdateDetailsList> {

    public PlanVersionArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdList listAllIdentityIds() {
        return super.listAllIdentityIds(PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList listAllInstanceIds() {
        return super.listAllInstanceIds(PlanDistributionHelper.PLANVERSION_OBJECT_TYPE);
    }

    public ObjectIdList listAllStatusIds() {
        return super.listAllStatusIds(PlanDistributionHelper.PLANUPDATE_OBJECT_TYPE);
    }

    public ObjectId getIdentityId(Identifier identity) {
        return super.getIdentityId(identity, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList getIdentityIds(IdentifierList identities) {
        return super.getIdentityIds(identities, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
    }

    public PlanVersionDetails getInstance(Identifier identity) {
        return super.getInstance(identity, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
    }

    public PlanVersionDetailsList getInstances(IdentifierList identities) {
        return super.getInstances(identities, PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE);
    }
}
