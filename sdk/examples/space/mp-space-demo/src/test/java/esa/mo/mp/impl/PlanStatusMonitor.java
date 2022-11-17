package esa.mo.mp.impl;

import java.util.Map;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.plandistribution.consumer.PlanDistributionAdapter;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetailsList;

/**
 * Used as test assertion for monitorPlanStatus operation
 */
public class PlanStatusMonitor extends PlanDistributionAdapter {

    private PlanUpdateDetailsList receivedPlanStatuses = new PlanUpdateDetailsList();

    public PlanUpdateDetailsList getReceivedPlanStatuses() {
        return this.receivedPlanStatuses;
    }

    @Override
    public void monitorPlanStatusNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
        UpdateHeaderList headerList, ObjectIdList planVersionList, PlanUpdateDetailsList updateList,
        Map qosProperties) {
        this.receivedPlanStatuses.addAll(updateList);
        synchronized (this) {
            this.notifyAll();
        }
    }
}
