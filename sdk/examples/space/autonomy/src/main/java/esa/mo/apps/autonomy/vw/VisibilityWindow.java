package esa.mo.apps.autonomy.vw;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Time;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import esa.mo.nmf.apps.navigation.assets.Satellite;
import esa.mo.nmf.apps.navigation.events.Event;
import esa.mo.nmf.apps.navigation.events.VisibilityDetector;
import esa.mo.nmf.apps.navigation.orbit.AutoconfigureOrekit;

/**
 * VisibilityWindow is used to calculate visibility windows of a coordinate
 * It uses ops-sat-plan-generation navigation app dependency to do the calculations
 */
public class VisibilityWindow {

    private static final Logger LOGGER = Logger.getLogger(VisibilityWindow.class.getName());

    private static final String SPACECRAFT_NAME = "OPS-SAT";
    private static final double SPACECRAFT_MASS = 5.4D;

    private static final String DEFAULT_NAVIGATION_FOLDER = "orekit";

    private static TimeScale timeScale = null;

    private VisibilityWindow() {}

    /**
     * Reads configuration properties, initializes and configures Orekit
     */
    public static void init() {
        String navigationFolder = System.getProperty("orekit.navigation.folder", DEFAULT_NAVIGATION_FOLDER);
        File navigationRoot = new File(navigationFolder);

        try {
            DataProvidersManager manager = DataProvidersManager.getInstance();
            manager.addProvider(new DirectoryCrawler(navigationRoot));
            AutoconfigureOrekit.configureOrekit();
            TimeScalesFactory.addDefaultUTCTAIOffsetsLoaders();
        } catch (OrekitException e) {
            LOGGER.log(Level.SEVERE, null, e);
            System.exit(1);
        }
        timeScale = TimeScalesFactory.getUTC();
    }

    /**
     * Finds visibility windows of a given coordinate and time window
     * @param tle the two-line element of the satellite
     * @param latitude coordinate
     * @param longitude coordinate
     * @param maxAngle the maximum angle of the satellite
     * @param startTime the lower bound of the time window
     * @param endTime the upper bound of the time window
     * @return a list of visibility windows for given arguments. An empty list is returned when no visibility windows were found or exception occured.
     */
    public static List<VisibilityWindowItem> getWindows(TLE tle, Double latitude, Double longitude, Double maxAngle, Time startTime, Time endTime) {
        List<VisibilityWindowItem> visibilityWindows = new ArrayList<>();

        try {
            Satellite satellite = getSatellite(SPACECRAFT_NAME, SPACECRAFT_MASS, tle);
            VisibilityDetector visibilityDetector = new VisibilityDetector(satellite, latitude, longitude, maxAngle);

            AbsoluteDate fromDate = new AbsoluteDate(new Date(startTime.getValue()), timeScale);
            AbsoluteDate toDate = new AbsoluteDate(new Date(endTime.getValue()), timeScale);
            LOGGER.info(String.format("Finding (%s,%s) visibility windows from %s to %s", latitude, longitude, fromDate, toDate));

            List<Event> visibilityEvents = visibilityDetector.get(fromDate, toDate);

            if (visibilityEvents.isEmpty()) {
                LOGGER.info("No visiblity events found");
            }

            StringBuilder stringBuilder = new StringBuilder("Found visibility windows");
            stringBuilder.append(System.lineSeparator());
            for (Event event : visibilityEvents) {
                stringBuilder.append(event.toString() + System.lineSeparator());
                long start = event.getStart().getDate().toDate(timeScale).getTime();
                long end = event.getStop().getDate().toDate(timeScale).getTime();

                visibilityWindows.add(new VisibilityWindowItem(latitude, longitude, start, end));
            }

            LOGGER.info(stringBuilder.toString());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        return visibilityWindows;
    }

    private static Satellite getSatellite(String name, double initMass, TLE tle) throws Exception {
        Satellite satellite = new Satellite();
        satellite.name = name;
        satellite.tle = tle;

        if (satellite.initPropagator(initMass) != 0) {
            throw new Exception("could not initialise the propagator");
        }
        if (satellite.propagate(tle.getDate()) != 0) {
            throw new Exception("could not propagate TLE");
        }
        return satellite;
    }
}
