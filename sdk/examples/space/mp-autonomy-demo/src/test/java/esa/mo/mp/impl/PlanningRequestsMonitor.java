package esa.mo.mp.impl;

import java.util.Map;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;

/**
 * PlanningRequestsMonitor is used as test assertion for monitorRequests operation
 */
public class PlanningRequestsMonitor extends PlanningRequestAdapter {

    private UpdateHeaderList receivedHeaderList = new UpdateHeaderList();
    private RequestUpdateDetailsList receivedUpdateList = new RequestUpdateDetailsList();

    @Override
    public void monitorRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier identifier, UpdateHeaderList headerList, RequestUpdateDetailsList updateList, Map qosProperties) {
        this.receivedHeaderList.addAll(headerList);
        this.receivedUpdateList.addAll(updateList);
        synchronized(this) {
            this.notifyAll();
        }
    }

    public RequestUpdateDetailsList getReceivedUpdateList() {
        return this.receivedUpdateList;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Received Request Statuses" + System.lineSeparator());
        for (int index = 0; index < receivedHeaderList.size(); index++) {
            UpdateHeader updateHeader = receivedHeaderList.get(index);
            RequestUpdateDetails requestUpdate = receivedUpdateList.get(index);
            stringBuilder.append(
                requestUpdate.getTimestamp() + " " +
                updateHeader.getKey().getFirstSubKey() + " " +
                "(id: " + updateHeader.getKey().getThirdSubKey() + ") " +
                requestUpdate.getStatus() + System.lineSeparator());

        }
        return stringBuilder.toString();
    }
}
