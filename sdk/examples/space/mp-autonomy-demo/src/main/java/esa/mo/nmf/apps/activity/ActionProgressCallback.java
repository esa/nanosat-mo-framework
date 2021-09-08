package esa.mo.nmf.apps.activity;

import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecution;

/**
 * ActionProgressCallback is called by ActionsMonitor when an Action is progressed
 */
public interface ActionProgressCallback {
    public void onCallback(Long actionInstanceId, ActivityExecution activityExecution);
}
