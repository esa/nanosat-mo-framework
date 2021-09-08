package esa.mo.nmf;

import java.util.logging.Logger;
import esa.mo.mp.impl.api.MPArchiveManager;

/**
 * The MissionPlanningNMFAdapter
 *
 */
public abstract class MissionPlanningNMFAdapter {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MissionPlanningNMFAdapter.class.getName());

    private MPArchiveManager archiveManager = null;

    public void setArchiveManager(MPArchiveManager archiveManager) {
        this.archiveManager = archiveManager;
    }

    public MPArchiveManager getArchiveManager() {
        return this.archiveManager;
    }

    public abstract void initialRegistrations(MPRegistration registration);
}
