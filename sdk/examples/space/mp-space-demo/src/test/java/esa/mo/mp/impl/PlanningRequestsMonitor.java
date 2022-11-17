package esa.mo.mp.impl;

import java.util.Map;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetailsList;

/**
 * Used as test assertion for monitorRequests operation
 */
public class PlanningRequestsMonitor extends PlanningRequestAdapter {

    private RequestUpdateDetailsList receivedUpdateList = new RequestUpdateDetailsList();

    @Override
    public void monitorRequestsNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
        UpdateHeaderList headerList, RequestUpdateDetailsList updateList, Map qosProperties) {
        receivedUpdateList.addAll(updateList);
        synchronized (this) {
            this.notifyAll();
        }
    }

    public RequestUpdateDetailsList getReceivedUpdateList() {
        return this.receivedUpdateList;
    }
}
