package esa.mo.nmf.apps.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mp.structures.ActivityStatus;
import org.ccsds.moims.mo.mp.structures.RequestStatus;
import esa.mo.mp.impl.com.COMObjectIdHelper;

/**
 * RequestActivitiesStatus is used by MPCameraController to determine status of a Request based on Activity statuses
 */
public class RequestActivitiesStatus {

    private HashMap<Long, ActivityStatus> activityStatuses = new HashMap<>();

    public void setActivityStatus(ObjectId activityInstanceId, ActivityStatus status) {
        Long instanceId = COMObjectIdHelper.getInstanceId(activityInstanceId);
        setActivityStatus(instanceId, status);
    }

    public void setActivityStatus(Long activityInstanceId, ActivityStatus status) {
        activityStatuses.put(activityInstanceId, status);
    }

    public RequestStatus getRequestStatus() {
        List<ActivityStatus> statuses = new ArrayList<>(activityStatuses.values());
        HashSet<ActivityStatus> differentStatuses = new HashSet<>(statuses);
        if (differentStatuses.size() == 0) {
            // No Activities
            return RequestStatus.COMPLETED;
        } else if (differentStatuses.size() == 1) {
            if (differentStatuses.contains(ActivityStatus.PLANNED)) {
                // All Activities PLANNED
                return RequestStatus.PLANNED;
            } else if (differentStatuses.contains(ActivityStatus.COMMITTED)) {
                // All Activities COMMITTED
                return RequestStatus.SCHEDULED;
            } else if (differentStatuses.contains(ActivityStatus.COMPLETED)) {
                // All Activities COMPLETED
                return RequestStatus.COMPLETED;
            }
        } else if (differentStatuses.size() == 2) {
            if (differentStatuses.contains(ActivityStatus.COMPLETED) && differentStatuses.contains(ActivityStatus.FAILED)) {
                // At least one Activity FAILED, other COMPLETED
                return RequestStatus.FAILED;
            }
        }
        return RequestStatus.EXECUTING;
    }
}
