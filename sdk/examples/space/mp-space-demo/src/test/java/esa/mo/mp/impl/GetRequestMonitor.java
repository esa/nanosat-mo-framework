package esa.mo.mp.impl;

import java.util.Map;

import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.planningrequest.consumer.PlanningRequestAdapter;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetailsList;

/**
 * Used as test assertion for getRequest operation
 */
public class GetRequestMonitor extends PlanningRequestAdapter {

    private RequestVersionDetailsList receivedRequestVersions = new RequestVersionDetailsList();

    public RequestVersionDetailsList getReceivedRequestVersions() {
        return this.receivedRequestVersions;
    }

    @Override
    public void getRequestResponseReceived(MALMessageHeader msgHeader, LongList requestIdentityId,
        LongList requestInstanceId, RequestVersionDetailsList requestVersion, Map qosProperties) {
        this.receivedRequestVersions.addAll(requestVersion);
        super.getRequestResponseReceived(msgHeader, requestIdentityId, requestInstanceId, requestVersion,
            qosProperties);
        synchronized (this) {
            this.notifyAll();
        }
    }
}
