package esa.mo.nmf.apps.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planexecutioncontrol.consumer.PlanExecutionControlAdapter;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetailsList;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;

/**
 * ActivitiesMonitor is used by Controllers to monitor updates of Activities by registered Request Version ids
 */
public class ActivitiesMonitor extends PlanExecutionControlAdapter {

    private final HashMap<Long, ActivityUpdateCallback> registrations = new HashMap<>();
    private MPArchiveManager archiveManager = null;

    public ActivitiesMonitor(MPArchiveManager archiveManager) {
        this.archiveManager = archiveManager;
    }

    @Override
    public void monitorActivitiesNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, ActivityUpdateDetailsList updateList, Map qosProperties) {
        for (int index = 0; index < headerList.size(); index++) {
            UpdateHeader header = headerList.get(index);
            Identifier activityIdentity = header.getKey().getFirstSubKey();
            ObjectId activityInstanceId = COMObjectIdHelper.getObjectId(
                header.getKey().getThirdSubKey(),
                PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE,
                msgHeader.getDomain());
            ObjectId sourceId = archiveManager.ACTIVITY.getSourceId(activityInstanceId);

            if (sourceId == null) continue;

            // ActivityInstance source can be another ActivityInstance object (parent) or RequestVersion
            boolean isRequestSource = Objects.equals(sourceId.getType(), PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE);

            if (isRequestSource) {
                ActivityUpdateDetails activityUpdate = updateList.get(index);
                Long requestVersionInstanceId = sourceId.getKey().getInstId();
                if (registrations.containsKey(requestVersionInstanceId)) {
                    registrations.get(requestVersionInstanceId).onCallback(activityIdentity, activityInstanceId, activityUpdate);
                }
            }
        }
    }

    public void register(ObjectId requestVersionId, ActivityUpdateCallback callback) {
        Long requestVersionInstanceId = COMObjectIdHelper.getInstanceId(requestVersionId);
        registrations.put(requestVersionInstanceId, callback);
    }

    public void deregister(ObjectId requestVersionId) {
        Long requestVersionInstanceId = COMObjectIdHelper.getInstanceId(requestVersionId);
        registrations.remove(requestVersionInstanceId);
    }
}
