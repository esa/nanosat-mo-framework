package esa.mo.apps.autonomy.planner.converter.tle;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.platform.gps.structures.TwoLineElementSet;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.DateComponents;
import org.orekit.time.TimeComponents;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import esa.mo.nmf.apps.navigation.orbit.AutoconfigureOrekit;

/**
 * TLEConverter converts between MP and Orekit TLE data structures
 */
public class TLEConverter {

    private static final Logger LOGGER = Logger.getLogger(TLEConverter.class.getName());

    private static final String DEFAULT_NAVIGATION_FOLDER = "orekit";

    private static TimeScale timeScale = null;

    private TLEConverter() {}

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

    public static TwoLineElementSet convert(String line1, String line2) {
        TLE tle = new TLE(line1, line2);

        return new TwoLineElementSet(
            tle.getSatelliteNumber(), "" + tle.getClassification(),
            tle.getLaunchYear(), tle.getLaunchNumber(), tle.getLaunchPiece(),
            tle.getDate().getComponents(0).getDate().getYear(),
            tle.getDate().getComponents(0).getDate().getDayOfYear(),
            tle.getDate().getComponents(0).getTime().getSecondsInUTCDay(),
            tle.getMeanMotionFirstDerivative(), tle.getMeanMotionSecondDerivative(),
            tle.getBStar(), tle.getElementNumber(), tle.getI(), tle.getRaan(), tle.getE(),
            tle.getPerigeeArgument(), tle.getMeanAnomaly(), tle.getMeanMotion(),
            tle.getRevolutionNumberAtEpoch()
        );
    }

    public static TLE convert(TwoLineElementSet tle) {
        int ephemerisType = TLE.DEFAULT;
        DateComponents date = new DateComponents(tle.getEpochYear(), tle.getEpochDay());
        TimeComponents time = new TimeComponents(tle.getEpochSecond());
        AbsoluteDate epoch = new AbsoluteDate(date, time, timeScale);
        return new TLE(
            tle.getCatalogNo(), tle.getClassification().charAt(0),
            tle.getIdYear(), tle.getIdLaunchNo(), tle.getIdPiece(),
            ephemerisType, tle.getId(), epoch,
            tle.getMeanMotion(), tle.getMeanMotionD1(), tle.getMeanMotionD2(),
            tle.getEccentricity(), tle.getInclination(), tle.getArgumentOfPerigee(),
            tle.getRaan(), tle.getMeanAnomaly(), tle.getNumRevolutions(), tle.getDragTerm()
        );
    }
}