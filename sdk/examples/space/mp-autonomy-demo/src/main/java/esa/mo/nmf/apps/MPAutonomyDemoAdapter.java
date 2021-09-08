package esa.mo.nmf.apps;

import esa.mo.nmf.MPRegistration;
import esa.mo.nmf.MissionPlanningNMFAdapter;

import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import esa.mo.nmf.NMFInterface;

/**
 * The MP adapter for the MP Autonomy App
 */
public class MPAutonomyDemoAdapter extends MissionPlanningNMFAdapter {
    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MPAutonomyDemoAdapter.class.getName());

    private NMFInterface connector;

    private ObjectId planIdentityId = null;

    public void setNMF(NMFInterface connector) {
        this.connector = connector;
    }

    public ObjectId getPlanIdentityId() {
        return this.planIdentityId;
    }

    @Override
    public void initialRegistrations(MPRegistration registration) {
        registration.setMode(MPRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        LOGGER.info("Registering to MP operations");
    }
}
