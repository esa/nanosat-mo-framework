package esa.mo.mp.impl;

import java.util.Map;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planexecutioncontrol.consumer.PlanExecutionControlAdapter;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetailsList;

/**
 * ActivitiesMonitor is used as test assertion for monitorActivities operation
 */
public class ActivitiesMonitor extends PlanExecutionControlAdapter {

    private UpdateHeaderList receivedHeaderList = new UpdateHeaderList();
    private ActivityUpdateDetailsList receivedUpdateList = new ActivityUpdateDetailsList();

    @Override
    public void monitorActivitiesNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, ActivityUpdateDetailsList updateList, Map qosProperties) {
        this.receivedHeaderList.addAll(headerList);
        this.receivedUpdateList.addAll(updateList);
        synchronized(this) {
            this.notifyAll();
        }
    }

    public ActivityUpdateDetailsList getReceivedUpdateList() {
        return this.receivedUpdateList;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Received Activity Statuses" + System.lineSeparator());
        for (int index = 0; index < receivedHeaderList.size(); index++) {
            UpdateHeader updateHeader = receivedHeaderList.get(index);
            ActivityUpdateDetails activityUpdate = receivedUpdateList.get(index);
            stringBuilder.append(
                activityUpdate.getTimestamp() + " " +
                updateHeader.getKey().getFirstSubKey() + " " +
                "(id: " + updateHeader.getKey().getThirdSubKey() + ") " +
                activityUpdate.getStatus() + System.lineSeparator());

        }
        return stringBuilder.toString();
    }
}
