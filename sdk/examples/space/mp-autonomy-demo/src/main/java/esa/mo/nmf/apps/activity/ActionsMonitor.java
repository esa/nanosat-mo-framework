package esa.mo.nmf.apps.activity;

import java.util.HashMap;
import java.util.Map;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecution;
import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * ActionsMonitor is used by Activities to monitor progress of Actions by registered Action instance ids
 */
public class ActionsMonitor extends EventAdapter {

    private final HashMap<Long, ActionProgressCallback> registrations = new HashMap<>();

    @Override
    public void monitorEventNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, ObjectDetailsList detailsList, ElementList elementList, Map qosProperties) {
        for (int index = 0; index < headerList.size(); index++) {
            ObjectDetails objectDetails = detailsList.get(index);
            Object element = elementList.get(index);
            if (element instanceof ActivityExecution) {
                ActivityExecution activityExecution = (ActivityExecution)element;
                Long actionInstanceId = objectDetails.getSource().getKey().getInstId();
                if (registrations.containsKey(actionInstanceId)) {
                    registrations.get(actionInstanceId).onCallback(actionInstanceId, activityExecution);
                }
            }
        }
    }

    public void register(Long actionInstanceId, ActionProgressCallback callback) {
        registrations.put(actionInstanceId, callback);
    }

    public void deregister(Long actionInstanceId) {
        registrations.remove(actionInstanceId);
    }
}
