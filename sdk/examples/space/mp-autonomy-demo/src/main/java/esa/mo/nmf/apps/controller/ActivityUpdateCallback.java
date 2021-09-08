package esa.mo.nmf.apps.controller;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;

/**
 * ActivityUpdateCallback is called by ActivitiesMonitor when an Activity is updated
 */
public interface ActivityUpdateCallback {
    public void onCallback(Identifier activityIdentity, ObjectId activityInstanceId, ActivityUpdateDetails updateDetails);
}
