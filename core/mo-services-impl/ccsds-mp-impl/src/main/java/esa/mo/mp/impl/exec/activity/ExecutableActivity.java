package esa.mo.mp.impl.exec.activity;

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;

public interface ExecutableActivity {
    public void execute(ObjectId activityInstanceId) throws MALException, MALInteractionException;
    public void missed(ObjectId activityInstanceId) throws MALException, MALInteractionException;
}
