package esa.mo.mp.impl;

import java.util.Map;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mp.plandistribution.consumer.PlanDistributionAdapter;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetailsList;

/**
 * Used as test assertion for queryPlan operation
 */
public class QueryPlanMonitor extends PlanDistributionAdapter {

    private static final Logger LOGGER = Logger.getLogger(QueryPlanMonitor.class.getName());

    private PlanVersionDetailsList receivedPlanVersions = new PlanVersionDetailsList();

    public PlanVersionDetailsList getReceivedPlanVersions() {
        return this.receivedPlanVersions;
    }

    @Override
    public void queryPlanResponseReceived(MALMessageHeader msgHeader, LongList planVersionIds,
        PlanVersionDetailsList planVersions, Map qosProperties) {
        this.receivedPlanVersions.addAll(planVersions);
        synchronized (this) {
            this.notifyAll();
        }
    }
}
