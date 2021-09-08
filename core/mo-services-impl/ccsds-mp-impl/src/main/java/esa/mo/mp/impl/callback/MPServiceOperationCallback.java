package esa.mo.mp.impl.callback;

import java.util.List;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.EventInstanceDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;

/**
 * MPServiceOperationCallback contains methods that can be overridden with custom implementation
 */
public abstract class MPServiceOperationCallback {

    public void validate(RequestVersionDetails requestVersion) {}

    public void validate(PlanVersionDetails planVersion, ActivityInstanceDetails activityInstance) {}

    public void validate(PlanVersionDetails planVersion, EventInstanceDetails eventInstance) {}

    public abstract void onCallback(List<MPServiceOperationArguments> arguments) throws MALException, MALInteractionException;
}
