package esa.mo.nmf.apps;

import esa.mo.apps.autonomy.util.FileUtils;
import esa.mo.nmf.apps.controller.MPCameraController;
import esa.mo.nmf.apps.controller.MPScoutController;
import esa.mo.nmf.apps.monitoring.MPExperimentLogger;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;

/**
 * An NMF App that demonstrates autonomy using APSI and MP services
 */
public class MPAutonomyDemo {

    private final NanoSatMOConnectorImpl connector;

    public MPAutonomyDemo() {
        connector = new NanoSatMOConnectorImpl();

        MCAutonomyDemoAdapter mcAdapter = new MCAutonomyDemoAdapter();
        mcAdapter.setNMF(connector);

        MPAutonomyDemoAdapter mpAdapter = new MPAutonomyDemoAdapter();
        mpAdapter.setNMF(connector);

        connector.init(mcAdapter, mpAdapter);

        MPExperimentLogger.init(connector);
        MPCameraController.init(connector);
        MPScoutController.init(connector);
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        FileUtils.loadApplicationProperties("application.properties");
        new MPAutonomyDemo();
    }
}
