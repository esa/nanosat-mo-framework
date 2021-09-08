package esa.mo.mp.impl.api.entity;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.structures.RequestIdentityDetails;
import org.ccsds.moims.mo.mp.structures.RequestIdentityDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.mp.impl.com.COMConfiguration;
import esa.mo.mp.impl.com.pattern.COMInstanceVersionArchiveManager;

/**
 * Front-end to COM Archive, typed by MP Request (Instance, not Definition).
 * <p>
 * Extends COMInstanceVersionArchiveManager, adds methods that are specific to Request type
 *
 * @see COMInstanceVersionArchiveManager
 */
public class RequestVersionArchiveManager extends COMInstanceVersionArchiveManager<RequestIdentityDetails, RequestIdentityDetailsList, RequestVersionDetails, RequestVersionDetailsList, RequestUpdateDetails, RequestUpdateDetailsList> {

    public RequestVersionArchiveManager(COMServicesProvider comServices, COMConfiguration configuration) {
        super(comServices, configuration);
    }

    public ObjectIdList listAllIdentityIds() {
        return super.listAllIdentityIds(PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList listAllInstanceIds() {
        return super.listAllInstanceIds(PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE);
    }

    public ObjectIdList listAllStatusIds() {
        return super.listAllStatusIds(PlanningRequestHelper.REQUESTSTATUSUPDATE_OBJECT_TYPE);
    }

    public ObjectId getIdentityId(Identifier identity) {
        return super.getIdentityId(identity, PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
    }

    public ObjectIdList getIdentityIds(IdentifierList identities) {
        return super.getIdentityIds(identities, PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
    }

    public RequestVersionDetails getInstance(Identifier identity) {
        return super.getInstance(identity, PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
    }

    public RequestVersionDetailsList getInstances(IdentifierList identities) {
        return super.getInstances(identities, PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE);
    }
}
