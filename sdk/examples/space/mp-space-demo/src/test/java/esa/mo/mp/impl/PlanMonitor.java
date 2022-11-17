package esa.mo.mp.impl;

import java.util.Map;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.plandistribution.consumer.PlanDistributionAdapter;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetailsList;

/**
 * Used as test assertion for monitorPlan operation
 */
public class PlanMonitor extends PlanDistributionAdapter {

    private PlanVersionDetailsList receivedPlanVersions = new PlanVersionDetailsList();

    public PlanVersionDetailsList getReceivedPlanVersions() {
        return this.receivedPlanVersions;
    }

    @Override
    public void monitorPlanNotifyReceived(MALMessageHeader msgHeader, Identifier identifier,
        UpdateHeaderList headerList, PlanVersionDetailsList versionList, Map qosProperties) {
        this.receivedPlanVersions.addAll(versionList);
        synchronized (this) {
            this.notifyAll();
        }
    }
}
