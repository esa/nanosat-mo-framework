package esa.mo.mp.impl;

import java.util.Map;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.plandistribution.consumer.PlanDistributionAdapter;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
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
            UpdateHeader header, ObjectId planVersion, PlanUpdateDetails update,
            Map qosProperties) {
        this.receivedPlanStatuses.add(update);
        synchronized (this) {
            this.notifyAll();
        }
    }
}
