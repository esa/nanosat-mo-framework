package esa.mo.nmf.apps.controller;

import java.util.Map;
import java.util.Objects;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.mp.structures.RequestStatus;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;

/**
 * RequestStatusMonitor is used by Controllers to monitor received Requests statuses by Request Identity
 */
public class RequestStatusMonitor extends PlanningRequestAdapter {

    private MPArchiveManager archiveManager = null;
    private RequestStatusCallback callback = null;
    private ObjectId monitoredRequestTemplateId = null;

    public RequestStatusMonitor(Identifier requestTemplateIdentity, MPArchiveManager archiveManager, RequestStatusCallback callback) {
        this.archiveManager = archiveManager;
        this.callback = callback;
        ObjectId requestTemplateIdentityId = archiveManager.REQUEST_TEMPLATE.getIdentityId(requestTemplateIdentity);
        this.monitoredRequestTemplateId = archiveManager.REQUEST_TEMPLATE.getDefinitionIdByIdentityId(requestTemplateIdentityId);
    }

    @Override
    public void monitorRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, RequestUpdateDetailsList updateList, Map qosProperties) {
        for (int index = 0; index < headerList.size(); index++) {
            UpdateHeader header = headerList.get(index);
            Long requestInstanceId = header.getKey().getThirdSubKey();
            ObjectId requestVersionId = COMObjectIdHelper.getObjectId(requestInstanceId, PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE);
            RequestUpdateDetails update = updateList.get(index);
            if (update.getStatus() == RequestStatus.REQUESTED) {
                RequestVersionDetails requestVersion = this.archiveManager.REQUEST_VERSION.getInstance(requestVersionId);
                ObjectId requestTemplateId = requestVersion.getTemplate();
                if (requestTemplateIdEquals(this.monitoredRequestTemplateId, requestTemplateId)) {
                    callback.requested(update);
                }
            }
        }
    }

    private boolean requestTemplateIdEquals(ObjectId requestTemplateId1, ObjectId requestTemplateId2) {
        // Does not check domain equality
        if (requestTemplateId1 == null && requestTemplateId2 == null) return true;
        if (requestTemplateId1 == null || requestTemplateId2 == null) return false;
        boolean typesEqual = Objects.equals(requestTemplateId1.getType(), requestTemplateId2.getType());
        ObjectKey objectKey1 = requestTemplateId1.getKey();
        ObjectKey objectKey2 = requestTemplateId2.getKey();
        if (objectKey1 == null && objectKey2 == null) return typesEqual;
        if (objectKey1 == null || objectKey2 == null) return false;
        return typesEqual && Objects.equals(objectKey1.getInstId(), objectKey2.getInstId());
    }
}
