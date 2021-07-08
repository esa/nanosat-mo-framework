/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.orekit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.TimerTask;
import opssat.simulator.threading.SimulatorNode;
import opssat.simulator.util.SimulatorHeader;
import org.hipparchus.geometry.euclidean.threed.Rotation;
import org.hipparchus.geometry.euclidean.threed.RotationConvention;
import org.hipparchus.geometry.euclidean.threed.RotationOrder;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.util.FastMath;
import org.orekit.attitudes.Attitude;
import org.orekit.attitudes.AttitudeProvider;
import org.orekit.attitudes.AttitudesSequence;
import org.orekit.attitudes.CelestialBodyPointed;
import org.orekit.attitudes.LofOffset;
import org.orekit.attitudes.NadirPointing;
import org.orekit.attitudes.SpinStabilized;
import org.orekit.attitudes.TargetPointing;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.CelestialBody;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.DataProvidersManager;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.LOFType;
import org.orekit.frames.LocalOrbitalFrame;
import org.orekit.frames.TopocentricFrame;
import org.orekit.frames.Transform;
import org.orekit.models.earth.GeoMagneticField;
import org.orekit.models.earth.GeoMagneticFieldFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.AbstractPropagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.AngularDerivativesFilter;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinatesProvider;

/**
 *
 * @author Cezar Suteu
 */
public class OrekitCore
{

  public enum ATTITUDE_MODE
  {
    SUN_POINTING
    {
      @Override
      public double getDoubleValue()
      {
        return 0;
      }
    },
    NADIR_POINTING
    {
      @Override
      public double getDoubleValue()
      {
        return 1;
      }
    },
    TARGET_TRACKING
    {
      @Override
      public double getDoubleValue()
      {
        return 2;
      }
    },
    LOF_TARGET
    {
      @Override
      public double getDoubleValue()
      {
        return 3;
      }
    },
    LOF_TARGET_SPIN
    {
      @Override
      public double getDoubleValue()
      {
        return 4;
      }
    },
    BDOT_DETUMBLE
    {
      @Override
      public double getDoubleValue()
      {
        return 5;
      }
    },
    SPIN_STABILIZED
    {
      @Override
      public double getDoubleValue()
      {
        return 6;
      }
    },
    VECTOR_POINTING
    {
      @Override
      public double getDoubleValue()
      {
        return 7;
      }
    };

    public abstract double getDoubleValue();
  }

  private AbsoluteDate gpsExtrapDate;
  private SpacecraftState gpsCurrentSCState;
  private boolean gpsOneShotDistances;

  private ATTITUDE_MODE attitudeMode;

  private final double mu = 3.986004415e+14;
  private final double DARMSTADT_LATITUDE = 49.871884;
  private final double DARMSTADT_LONGITUDE = 8.622481;
  private final double DARMSTADT_ALTITUDE = 0;
  private final double DARMSTADT_MIN_ELEVATION = 5;

  TLE initialTLE;
  // Keplerian orbits elements
  private double a, e, i, omega, raan, lM;
  KeplerianPropagator kepler;
  // Duration
  private double timeElapsed;// seconds
  AbsoluteDate initialDate, extrapDate;
  BodyShape earth;
  CelestialBody sun;
  private double[] sunVector;

  SpacecraftState spacecraftState;
  Frame inertialFrame;
  Frame celestialFrame;
  public Frame earthFrameITRF;
  LocalOrbitalFrame lof;
  AttitudeProvider dayObservationLaw;
  AttitudeProvider sunPointing;
  AttitudeProvider nadirPointing;
  AttitudeProvider bDotDetumble;
  AttitudeProviderWrapper targetTracking;
  AttitudeProviderWrapper lofTracking;
  VectorPointingSimulator vectorPointing;
  SpinStabilized spinStabilized;
  GeodeticPoint targetGeo;
  AttitudesSequence attitudesSequence;
  boolean hasAnx, hasDnx, hasAOS, hasLOS;// Ascending and descending nodes valid calculations exist
  AbsoluteDate nextAnx, nextDnx, nextAOS, nextLOS;
  GeodeticPoint lastPosition;
  boolean lastInView;
  TopocentricFrame staESOCFrame;
  GeodeticPoint stationESOC;
  private final boolean debug1 = false;
  Orbit initialOrbit;
  AbstractPropagator runningPropagator;
  private AttitudeStateProvider attitudeState;
  boolean isInitialized;
  private int tleNumber = 0;
  private Logger logger;

  // Magnetic field
  GeoMagneticField geoMagneticField;
  private final Object magneticFieldVectorLock = new Object();
  private double[] magneticFieldVector;

  // GPS constellation
  private boolean propagatingConstellation;
  private LinkedList<GPSSatellite> gpsConstellation;
  final LinkedList<GPSSatInView> gpsSatsInView;
  private final Object constellationPropagationCounterMutex = new Object();
  private int constellationPropagationCounter;
  private ExecutorService executor = Executors
      .newCachedThreadPool(new SimThreadFactory("SimProcessorOrekit"));

  private byte stateTarget = 0;
  private long delayPeriod = 2*60*1000; // 2 minutes delay expressed in milliseconds.

  private final java.util.Timer stateTargetTimer = new java.util.Timer();
  TimerTask stateTargetTask;

  public OrekitCore(double a, double e, double i, double omega, double raan, double lM,
      SimulatorHeader simulatorHeader, Logger logger, SimulatorNode simulatorNode)
      throws OrekitException
  {
    this.logger = logger;
    SimulatorNode.handleResourcePath("orekit-data.zip", logger, getClass().getClassLoader(), true);
    SimulatorNode.handleResourcePath("IGRF.zip", logger, getClass().getClassLoader(), true);
    SimulatorNode.handleResourcePath("WMM2020COF.zip", logger, getClass().getClassLoader(), true);

    StringBuffer pathBuffer = new StringBuffer();
    appendIfExists(pathBuffer, "orekit-data.zip");
    appendIfExists(pathBuffer, "IGRF.zip");
    appendIfExists(pathBuffer, "WMM2020COF.zip");
    System.setProperty(DataProvidersManager.OREKIT_DATA_PATH, pathBuffer.toString());

    // Initial date in UTC time scale
    TimeScale utc = TimeScalesFactory.getUTC();

    this.initialDate = new AbsoluteDate(simulatorHeader.getYearStartDate(),
        simulatorHeader.getMonthStartDate(), simulatorHeader.getDayStartDate(),
        simulatorHeader.getHourStartDate(), simulatorHeader.getMinuteStartDate(),
        simulatorHeader.getSecondStartDate(), utc);

    this.extrapDate = new AbsoluteDate(initialDate, 0);
    this.a = a;
    this.e = e;
    this.i = FastMath.toRadians(i);
    this.omega = FastMath.toRadians(omega);
    this.raan = FastMath.toRadians(raan);
    this.lM = FastMath.toRadians(lM);

    // Frames
    this.inertialFrame = FramesFactory.getEME2000();
    this.celestialFrame = FramesFactory.getGCRF();
    this.earthFrameITRF = FramesFactory.getITRF(IERSConventions.IERS_2010, true);

    // Earth
    this.earth = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
        Constants.WGS84_EARTH_FLATTENING, earthFrameITRF);

    // Orbit construction as Keplerian
    this.initialOrbit = new KeplerianOrbit(this.a, this.e, this.i, this.omega, this.raan, this.lM,
        PositionAngle.MEAN, inertialFrame, initialDate, mu);

    // Attitude providers
    this.sun = CelestialBodyFactory.getSun();
    this.sunPointing = new CelestialBodyPointed(celestialFrame, sun,
        Vector3D.PLUS_I, Vector3D.MINUS_J, Vector3D.PLUS_K);

    this.vectorPointing = new VectorPointingSimulator();

    this.nadirPointing = new NadirPointing(inertialFrame, earth);
    this.dayObservationLaw = new LofOffset(initialOrbit.getFrame(), LOFType.VVLH, RotationOrder.XYZ,
        FastMath.toRadians(20), FastMath.toRadians(40), 0);
    this.targetGeo = new GeodeticPoint(FastMath.toRadians(DARMSTADT_LATITUDE),
        FastMath.toRadians(DARMSTADT_LONGITUDE), 0);
    this.targetTracking = new AttitudeProviderWrapper(new TargetPointing(inertialFrame, targetGeo,
        earth));
    this.attitudesSequence = new AttitudesSequence();

    this.lofTracking = new AttitudeProviderWrapper(
        new LofOffset(initialOrbit.getFrame(), LOFType.LVLH));
    this.spinStabilized = new SpinStabilized(lofTracking, this.extrapDate, Vector3D.PLUS_I,
        FastMath.toRadians(0));
    this.bDotDetumble = new NadirPointing(inertialFrame, earth);

    attitudesSequence.resetActiveProvider(sunPointing);
    changeAttitude(ATTITUDE_MODE.SUN_POINTING);

    // Propagators
    boolean TLERequired = "tle".equals(simulatorHeader.getOrekitPropagator());
    if (TLE.isFormatOK(simulatorHeader.getOrekitTLE1(), simulatorHeader.getOrekitTLE2())) {
      this.initialTLE = new TLE(simulatorHeader.getOrekitTLE1(), simulatorHeader.getOrekitTLE2());
      if (TLERequired) {
        logger.log(Level.FINE, "TLE\n" + this.initialTLE.toString() + "\nformat ok.");
      }
    } else {
      if (TLERequired) {
        logger.log(Level.FINE,
            "TLE\n" + simulatorHeader.getOrekitTLE1() + "\n" + simulatorHeader.getOrekitTLE2()
            + "\nformat invalid. Reverting to keplerian provider.");
      }
      simulatorHeader.setOrekitPropagator("kepler");
    }
    // Simple extrapolation with Keplerian motion
    this.kepler = new KeplerianPropagator(initialOrbit, attitudesSequence);

    if ("kepler".equals(simulatorHeader.getOrekitPropagator())) {
      this.runningPropagator = this.kepler;
    } else if ("tle".equals(simulatorHeader.getOrekitPropagator())) {
      this.runningPropagator = TLEPropagator.selectExtrapolator(initialTLE, attitudesSequence,
            6.0);

    } else {
      this.runningPropagator = this.kepler;
    }

    AttitudeDetector sunDetector = new AttitudeDetector(0);
    AttitudeDetector nadirDetector = new AttitudeDetector(1);
    AttitudeDetector targetTrackingDetector = new AttitudeDetector(2);
    AttitudeDetector lofTargetDetector = new AttitudeDetector(3);
    AttitudeDetector lofTargetSpinDetector = new AttitudeDetector(4);
    AttitudeDetector bDotDetumbleDetector = new AttitudeDetector(5);
    AttitudeDetector spinStabilizedDetector = new AttitudeDetector(6);
    AttitudeDetector vectorPointingDetector = new AttitudeDetector(7);

    // Transitions to sunpointing
    attitudesSequence.addSwitchingCondition(nadirPointing, sunPointing, sunDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(targetTracking, sunPointing, sunDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(lofTracking, sunPointing, sunDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(bDotDetumble, sunPointing, sunDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(spinStabilized, sunPointing, sunDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(vectorPointing, sunPointing, sunDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);

    // Transitions to nadir
    attitudesSequence.addSwitchingCondition(sunPointing, nadirPointing, nadirDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(targetTracking, nadirPointing, nadirDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(lofTracking, nadirPointing, nadirDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(bDotDetumble, nadirPointing, nadirDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(spinStabilized, nadirPointing, nadirDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(vectorPointing, nadirPointing, nadirDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);

    // Transitions to target tracking
    attitudesSequence.addSwitchingCondition(sunPointing, targetTracking, targetTrackingDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(nadirPointing, targetTracking, targetTrackingDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(lofTracking, targetTracking, targetTrackingDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(bDotDetumble, targetTracking, targetTrackingDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(spinStabilized, targetTracking, targetTrackingDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(vectorPointing, targetTracking, targetTrackingDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);

    // Transitions to LOF tracking
    attitudesSequence.addSwitchingCondition(sunPointing, lofTracking, lofTargetDetector, true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(nadirPointing, lofTracking, lofTargetDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(targetTracking, lofTracking, lofTargetDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(bDotDetumble, lofTracking, lofTargetDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(spinStabilized, lofTracking, lofTargetDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(vectorPointing, lofTracking, lofTargetDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);

    // Transitions to B-Dot detumbling
    attitudesSequence.addSwitchingCondition(sunPointing, bDotDetumble, bDotDetumbleDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(nadirPointing, bDotDetumble, bDotDetumbleDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(targetTracking, bDotDetumble, bDotDetumbleDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(lofTracking, bDotDetumble, bDotDetumbleDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(spinStabilized, bDotDetumble, bDotDetumbleDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(vectorPointing, bDotDetumble, bDotDetumbleDetector, true,
        false,
        60, AngularDerivativesFilter.USE_RRA, null);

    // Transitions to Spin stabilized
    attitudesSequence.addSwitchingCondition(sunPointing, spinStabilized, spinStabilizedDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(nadirPointing, spinStabilized, spinStabilizedDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(targetTracking, spinStabilized, spinStabilizedDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(lofTracking, spinStabilized, spinStabilizedDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(bDotDetumble, spinStabilized, spinStabilizedDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(vectorPointing, spinStabilized, spinStabilizedDetector,
        true, false,
        60, AngularDerivativesFilter.USE_RRA, null);

    // Transitions to Vector Pointing
    // Transition time is 1 because VectorPointingSimulator is simulating the transition
    attitudesSequence.addSwitchingCondition(sunPointing, vectorPointing, vectorPointingDetector,
        true, false,
        1, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(nadirPointing, vectorPointing, vectorPointingDetector,
        true, false,
        1, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(targetTracking, vectorPointing, vectorPointingDetector,
        true, false,
        1, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(lofTracking, vectorPointing, vectorPointingDetector,
        true, false,
        1, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(bDotDetumble, vectorPointing, vectorPointingDetector,
        true, false,
        1, AngularDerivativesFilter.USE_RRA, null);
    attitudesSequence.addSwitchingCondition(spinStabilized, vectorPointing, vectorPointingDetector,
        true, false,
        1, AngularDerivativesFilter.USE_RRA, null);


    this.attitudesSequence.registerSwitchEvents(runningPropagator);
    this.attitudeState = new AttitudeStateProvider();
    this.runningPropagator.addAdditionalStateProvider(attitudeState);
    this.lof = new LocalOrbitalFrame(this.inertialFrame, LOFType.LVLH, this.runningPropagator,
        "LVLH");
    // this.geoMagneticField = GeoMagneticFieldFactory.getWMM(2016);

    double decimalYear = getDecimalYear(this.extrapDate);
    extrapDate.toDate(utc);
    logger.log(Level.FINE, "Decimal year is [" + decimalYear + "]");
    this.geoMagneticField = GeoMagneticFieldFactory.getIGRF(decimalYear);
    logger.log(Level.FINE, "Magnetic model loaded: [" + this.geoMagneticField.getModelName() + "]");
    magneticFieldVector = new double[3];

    logger.log(Level.INFO, "Orekit module created with start date [" + this.initialDate + "]");

    hasAnx = false;
    hasDnx = false;
    lastInView = false;
    hasAOS = false;
    hasLOS = false;

    // Station
    this.stationESOC = new GeodeticPoint(FastMath.toRadians(DARMSTADT_LATITUDE),
        FastMath.toRadians(DARMSTADT_LONGITUDE), DARMSTADT_ALTITUDE);
    this.staESOCFrame = new TopocentricFrame(this.earth, stationESOC, "ESOC");
    this.sunVector = new double[3];

    BufferedReader in;
    // GPS Constellation simulator
    if (!simulatorHeader.isUpdateInternet()) {
      // Handle gps file from resources
      SimulatorNode.handleResourcePath(simulatorNode.getGPSOpsFile().getName(), logger,
          getClass().getClassLoader(), false);
    } else {
      // First check if folder exists
      // Try to download latest NORAD TLEs
      final String celestrakURL = "https://www.celestrak.com/NORAD/elements/gps-ops.txt";
      logger.log(Level.INFO, "Connecting to [" + celestrakURL + "] to update TLEs");
      URL noradGpsOps = null;
      try {
        noradGpsOps = new URL(celestrakURL);
      } catch (MalformedURLException ex) {
        logger.log(Level.INFO, ex.toString());
      }
      BufferedReader readerURL = null;
      if (noradGpsOps != null) {
        try {
          readerURL = new BufferedReader(new InputStreamReader((noradGpsOps.openStream())));
        } catch (IOException ex) {
          logger.log(Level.WARNING, "Unable to update  norad satellites" + ex.toString());
        }
      }
      if (readerURL != null) {
        String line;
        BufferedWriter out = null;
        File gpsOps = simulatorNode.getGPSOpsFile();
        if (!gpsOps.getParentFile().exists()) {
          gpsOps.getParentFile().mkdir();
        }
        try {
          out = new BufferedWriter(new FileWriter(gpsOps.getAbsolutePath()));
          while ((line = readerURL.readLine()) != null) {
            out.write(line + "\n");
          }
          out.close();
        } catch (IOException ex) {
          logger.log(Level.SEVERE, ex.toString());
        }
        try {
          readerURL.close();
        } catch (IOException ex) {
          logger.log(Level.SEVERE, ex.toString());
        }
      }
    }
    gpsConstellation = new LinkedList<>();
    gpsSatsInView = new LinkedList<>();
    try {
      in = new BufferedReader(new FileReader(simulatorNode.getGPSOpsFile()));
      String line;
      try {
        String name = null, tle1 = null, tle2 = null;
        while ((line = in.readLine()) != null) {
          if (line.startsWith("1")) {
            tle1 = line;
          } else if (line.startsWith("2")) {
            tle2 = line;
          } else {
            name = line;
          }
          if (name != null && tle1 != null && tle2 != null) {
            if (TLE.isFormatOK(tle1, tle2)) {
              TLE newTLE = new TLE(tle1, tle2);
              TLEPropagator tempTLEPropagator = TLEPropagator.selectExtrapolator(newTLE);
              tempTLEPropagator.setSlaveMode();
              GPSSatellite newSat = new GPSSatellite(name, tempTLEPropagator);
              this.gpsConstellation.add(newSat);
            }
            name = null;
            tle1 = null;
            tle2 = null;
          }
        }
        in.close();
      } catch (IOException ex) {
        logger.log(Level.SEVERE, ex.toString());
      }
    } catch (FileNotFoundException ex) {
      logger.log(Level.SEVERE, ex.toString());
      // No internet connection available and no file saved
    }
    logger.log(Level.FINE,
        "GPS Constellation has [" + this.gpsConstellation.size() + "] satellites!");
  }

  public LinkedList<GPSSatellite> getGpsConstellation()
  {
    return gpsConstellation;
  }

  private double getDecimalYear(AbsoluteDate extrapDate)
  {

    try {
      return extrapDate.getComponents(0).getDate().getYear()
          + extrapDate.getComponents(0).getDate().getDayOfYear() / 365.0;
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }
    return 0.0;
  }

  public int getNumberSatsInView()
  {
    synchronized (this.gpsSatsInView) {
      return gpsSatsInView.size();
    }
  }

  public ATTITUDE_MODE getAttitudeMode()
  {
    return attitudeMode;
  }

  public int getConstellationPropagationCounter()
  {
    synchronized (this.constellationPropagationCounterMutex) {
      return constellationPropagationCounter;
    }
  }

  public void setConstellationPropagationCounter(int constellationPropagationCounter)
  {
    synchronized (this.constellationPropagationCounterMutex) {
      this.constellationPropagationCounter = constellationPropagationCounter;
    }
  }

  private String getETAFromAbsDate(AbsoluteDate date)
  {
    int seconds = -(int) this.extrapDate.durationFrom(date);
    if (seconds > 0) {
      int hours = seconds / 3600;
      seconds = seconds % 3600;
      int minutes = seconds / 60;
      seconds = seconds % 60;
      String result1 = date.toString();
      return result1.substring(5, result1.length() - 4) + "--ETA=>" + hours + "h"
          + minutes + "m" + seconds + "s";
    } else {
      return "Passed";
    }
  }

  public static boolean parseTLEFromStrings(byte[] date, String tle1, String tle2,
      StringBuilder exception)
  {
    boolean TLEOk = false;
    //logger.log(Level.INFO, "1Trying to parse [\n" + tle1 + "\n" + tle2 + "\n], size is ["
    //    + (tle1.length() + tle2.length()) + " bytes]");
    try {
      TLEOk = TLE.isFormatOK(tle1, tle2);
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
      exception.append(ex.toString());

    }
    if (TLEOk) {
      String together = tle1 + '\n' + tle2 + '\n';
      int index = 0;
      for (char c : together.toCharArray()) {
        byte b = (byte) c;
        // System.out.print(b);
        date[index++] = b;
      }
    } else {
      //logger.log(Level.INFO, "TLE is not ok!");
    }
    return TLEOk;
  }

  public static boolean parseTLEFromBytes(byte[] date, StringBuilder tle1, StringBuilder tle2)
  {
    //logger.log(Level.INFO,
    //    "Trying to parse [" + date.toString() + "], size is [" + date.length + "]");
    StringBuilder stringBuilder = new StringBuilder();
    String tl1 = null, tl2 = null;
    int index = 1;
    for (byte b : date) {
      char c = (char) b;
      stringBuilder.append(c);
      index++;
      if (index == 70) {
        tl1 = stringBuilder.toString();
      }
      if (index == 71) {
        stringBuilder = new StringBuilder();
      }
      if (index == 140) {
        tl2 = stringBuilder.toString();
      }
    }
    //logger.log(Level.INFO, "Result \n[" + tl1 + "]\n[" + tl2 + "]");
    tle1.append(tl1);
    tle2.append(tl2);
    try {
      return TLE.isFormatOK(tle1.toString(), tle2.toString());
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }
    return false;
  }

  public void setNewTLEs(SimulatorHeader simulatorHeader)
  {
    try {
      // logger.log(Level.INFO,simulatorHeader.getOrekitTLE1());
      // logger.log(Level.INFO,simulatorHeader.getOrekitTLE2());
      this.initialTLE = new TLE(simulatorHeader.getOrekitTLE1(), simulatorHeader.getOrekitTLE2());
      // logger.log(Level.INFO,this.initialTLE.toString());
      this.runningPropagator = TLEPropagator.selectExtrapolator(initialTLE, attitudesSequence,
            6.0);
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private AbstractPropagator getNewPropagator()
  {
    if (this.runningPropagator instanceof KeplerianPropagator) {
      return new KeplerianPropagator(this.initialOrbit);
    } else if (this.runningPropagator instanceof TLEPropagator) {
      try {
        return TLEPropagator.selectExtrapolator(this.initialTLE);
      } catch (OrekitException ex) {
        Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return null;
  }

  public String getOrekitInfo()
  {
    String result = "UnknownPropagator!";
    String attitudeInfo = this.attitudeMode.toString();
    if (this.runningPropagator instanceof KeplerianPropagator) {
      return "Kepler|" + attitudeInfo;
    } else if (this.runningPropagator instanceof TLEPropagator) {
      return "TLE|" + this.initialTLE.getSatelliteNumber() + "|" + attitudeInfo;// +";"+this.initialDate.hashCode();
    }
    return result;
  }

  public String getNextAnx()
  {
    if (nextAnx != null) {
      return getETAFromAbsDate(nextAnx);
    } else {
      return "N/A";
    }
  }

  public String getNextDnx()
  {
    if (nextDnx != null) {
      return getETAFromAbsDate(nextDnx);
    } else {
      return "N/A";
    }
  }

  public String getNextAOS()
  {
    if (nextAOS != null) {
      return getETAFromAbsDate(nextAOS);
    } else {
      return "N/A";
    }
  }

  public String getNextLOS()
  {
    if (nextLOS != null) {
      return getETAFromAbsDate(nextLOS);
    } else {
      return "N/A";
    }
  }

  public void changeAttitudeLof(double x, double y, double z, double rate)
  {
    double x_rad = FastMath.toRadians(x);
    double y_rad = FastMath.toRadians(y);
    double z_rad = FastMath.toRadians(z);

    logger.log(Level.INFO,
        "changeAttitudeLof x [" + x + "] y [" + y + "] z [" + z + "] rate [" + rate + "]");
    try {
      this.lofTracking.setProvider(
          new LofOffset(this.inertialFrame, LOFType.LVLH, RotationOrder.XYZ, x_rad, y_rad, z_rad));
      this.spinStabilized = new SpinStabilized(lofTracking, this.extrapDate, Vector3D.PLUS_K,
          FastMath.toRadians(rate));
      if (rate > 0) {
        this.attitudeMode = ATTITUDE_MODE.LOF_TARGET_SPIN;
      } else {
        this.attitudeMode = ATTITUDE_MODE.LOF_TARGET;
      }
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }
    //attitudesSequence.resetActiveProvider(spinStabilized);
    scheduleStateTargetTimer();
  }

  public void changeAttitudeTarget(double latitude, double longitude, double altitude)
  {
    logger.log(Level.INFO,
        "changeAttitudeTarget latitude [" + latitude + "] longitude [" + longitude + "]");
    try {
      this.targetGeo = new GeodeticPoint(FastMath.toRadians(latitude),
          FastMath.toRadians(longitude), altitude);
      this.targetTracking.setProvider(new TargetPointing(this.inertialFrame, this.targetGeo,
          this.earth));
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }
    //attitudesSequence.resetActiveProvider(targetTracking);
    this.attitudeMode = ATTITUDE_MODE.TARGET_TRACKING;
    scheduleStateTargetTimer();
  }

  public void changeAttitudeVectorTarget(float x, float y, float z, float margin)
  {
    this.vectorPointing.update(this.spacecraftState);
    logger.log(Level.INFO, "changeAttitudeVectorTarget vector: [{0}, {1}, {2}] margin [{3}]",
        new Object[]{x, y, z, margin});

    this.vectorPointing.start(x, y, z, margin);
    this.attitudeMode = ATTITUDE_MODE.VECTOR_POINTING;
    scheduleStateTargetTimer();
  }

  public void changeAttitude(ATTITUDE_MODE newAttitude)
  {
    boolean found = false;
    logger.log(Level.FINE, "Changing attitude to " + newAttitude);
    if (newAttitude == ATTITUDE_MODE.SUN_POINTING) {
      //attitudesSequence.resetActiveProvider(sunPointing);
      found = true;
    } else if (newAttitude == ATTITUDE_MODE.NADIR_POINTING) {
      // attitudesSequence.resetActiveProvider(nadirPointing);
      found = true;
    } else if (newAttitude == ATTITUDE_MODE.BDOT_DETUMBLE) {
      //attitudesSequence.resetActiveProvider(bDotDetumble);
      found = true;
    }
    if (!found) {
      logger.log(Level.SEVERE, "Attitude type lookup failed!");
    }
    this.attitudeMode = newAttitude;
    scheduleStateTargetTimer();
  }

  private void appendIfExists(final StringBuffer path, final String directory)
  {
    /*
     * try { ClassLoader classLoader; classLoader = getClass().getClassLoader();
     * final URL url = classLoader.getResource(directory); if (url != null) { if
     * (path.length() > 0) { path.append(System.getProperty("path.separator")); }
     * path.append(url.toURI().getPath()); } } catch (URISyntaxException use) { //
     * display an error message and simply ignore the path
     * System.err.println(use.getLocalizedMessage()); }
     */
    if (path.length() > 0) {
      path.append(System.getProperty("path.separator"));
    }
    path.append(SimulatorNode.getResourcesPath() + directory);
  }

  public double[] getMagneticField()
  {
    synchronized (magneticFieldVectorLock) {
      return this.magneticFieldVector;
    }
  }

  private Rotation getLofRotation()
  {
    return this.spacecraftState.getAttitude().getRotation();
  }

  public double[][] getAttitudeRotation()
  {
    synchronized (magneticFieldVectorLock) {
      return this.getLofRotation().getMatrix();
    }
  }

  public double[] getSunVector()
  {
    return this.sunVector;
  }

  public double[] getMagnetometer()
  {
    synchronized (magneticFieldVectorLock) {
      double[] magnetometerVector = new double[3];
      // FramesFactory.
      this.getLofRotation().applyInverseTo(this.magneticFieldVector, magnetometerVector);
      return magnetometerVector;
    }
  }

  public boolean isIsInitialized()
  {
    return isInitialized;
  }

  public void initAttitudeProviders()
  {

  }

  private boolean isInGSView(SpacecraftState s)
  {
    return getGSElevation(s) >= 0;
  }

  public double getCurrentGSElevation()
  {
    return getGSElevation(this.spacecraftState);
  }

  public double getCurrentGSAzimuth()
  {
    return getGSAzimuth(this.spacecraftState);
  }

  private double getGSAzimuth(SpacecraftState s)
  {
    try {
      final double azimuth = this.staESOCFrame.getAzimuth(s.getPVCoordinates().getPosition(),
          s.getFrame(), s.getDate());
      return FastMath.toDegrees(azimuth);
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }
    return 0;
  }

  private double getGSElevation(SpacecraftState s)
  {
    try {
      final double trueElevation = this.staESOCFrame
          .getElevation(s.getPVCoordinates().getPosition(), s.getFrame(), s.getDate());
      final double calculatedElevation;
      // final double azimuth =
      // this.staESOCFrame.getAzimuth(s.getPVCoordinates().getPosition(),
      // s.getFrame(), s.getDate());
      calculatedElevation = trueElevation;
      return FastMath.toDegrees(calculatedElevation) - DARMSTADT_MIN_ELEVATION;
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }

    return 0;
  }

  private AbsoluteDate findNextSignalEvent(boolean isAOS)
  {
    if (isAOS) {
      this.hasAOS = true;
    } else {
      this.hasLOS = true;
    }
    int timeStep = 1;
    String nodeDetectStr = (isAOS ? "AOS" : "LOS");
    if (debug1) {
      logger.log(Level.INFO, nodeDetectStr + ".. Timestep is [" + timeStep + "]");
    }
    int maxSteps = 10000;
    int speedFactor = 10;
    int i = 0;
    boolean localDebug = false;
    AbsoluteDate tempExtrapDate = new AbsoluteDate(extrapDate, 0);
    AbstractPropagator tempPropagator = getNewPropagator();
    SpacecraftState tempSpacecraftState = null;
    GeodeticPoint positionThen;
    boolean allowCrossingLock = false;
    while (++i < maxSteps) {
      tempExtrapDate = tempExtrapDate.shiftedBy(timeStep * speedFactor);
      tempSpacecraftState = tempPropagator.propagate(tempExtrapDate);

      positionThen = getGeodeticPoint(tempSpacecraftState);

      // logger.log(Level.INFO,"Step ["+i+"], time
      // ["+tempExtrapDate.toString()+"],"+positionThen.toString()+"
      // elevation["+getGSElevation(tempSpacecraftState)+"]");
      boolean tempInViewRange = isInGSView(tempSpacecraftState);
      if ((!tempInViewRange && isAOS) || (tempInViewRange && !isAOS)) {
        allowCrossingLock = true;
      }
      if (allowCrossingLock) {
        if ((tempInViewRange && isAOS) || (!tempInViewRange && !isAOS)) {
          if (debug1 || localDebug) {
            logger.log(Level.INFO,
                nodeDetectStr + " found @ [" + tempExtrapDate.toString() + "], step [" + i + "]");
          }
          return tempExtrapDate;
        }
      }
    }
    if (debug1) {
      logger.log(Level.INFO, "Failed to find " + nodeDetectStr + " after [" + i + "] steps!");
    }
    return null;
  }

  private AbsoluteDate findNextCrossing(boolean isAnx)
  {
    if (isAnx) {
      this.hasAnx = true;
    } else {
      this.hasDnx = true;
    }
    // Propagate until anx is found
    int timeStep = 1;
    String nodeDetectStr = (isAnx ? "ANX" : "DNX");
    // logger.log(Level.INFO,nodeDetectStr+".. Timestep is ["+timeStep+"]");
    int maxSteps = 100;
    int speedFactor = 100;
    int i = 0;
    AbsoluteDate tempExtrapDate = new AbsoluteDate(extrapDate, 0);
    SpacecraftState tempSpacecraftState = null;
    GeodeticPoint positionThen;
    boolean allowCrossingLock = false;
    AbstractPropagator tempPropagator = getNewPropagator();
    while (++i < maxSteps) {
      tempExtrapDate = tempExtrapDate.shiftedBy(timeStep * speedFactor);
      tempSpacecraftState = tempPropagator.propagate(tempExtrapDate);

      positionThen = getGeodeticPoint(tempSpacecraftState);
      // logger.log(Level.INFO,positionThen.toString());
      if ((positionThen.getLatitude() < 0 && isAnx) || (positionThen.getLatitude() > 0 && !isAnx)) {
        allowCrossingLock = true;
      }
      if (allowCrossingLock) {
        if ((positionThen.getLatitude() > 0 && isAnx)
            || (positionThen.getLatitude() < 0 && !isAnx)) {
          allowCrossingLock = true;
          if (debug1) {
            logger.log(Level.INFO,
                nodeDetectStr + " found @ [" + tempExtrapDate.toString() + "], step [" + i + "]");
          }
          return tempExtrapDate;
        }
      }
    }
    if (debug1) {
      logger.log(Level.INFO, "Failed to find " + nodeDetectStr + " after [" + i + "] steps!");
    }
    return null;
  }

  private void checkAOSLOS()
  {
    if (this.nextAOS == null) {
      this.hasAOS = false;
      if (debug1) {
        logger.log(Level.INFO, "Resetting AOS");
      }
    }
    if (this.nextLOS == null) {
      this.hasLOS = false;
      if (debug1) {
        logger.log(Level.INFO, "Resetting LOS");
      }
    }
  }

  private void processGPSConstellationPropagateStep()
  {
    this.propagatingConstellation = true;
    this.gpsExtrapDate = this.extrapDate;
    this.gpsCurrentSCState = this.spacecraftState;

    executor.submit(() -> {
      // logger.log(Level.INFO,"Propagating constellation..");

      GeodeticPoint opsSatGeoDPoint = getGeodeticPoint(gpsCurrentSCState);
      TopocentricFrame opsSatCurrentFrame = new TopocentricFrame(earth, opsSatGeoDPoint,
          "OPS-SAT");

      LinkedList<GPSSatInView> tempSatsInView = new LinkedList<>();

      for (GPSSatellite t : gpsConstellation) {
        t.setState(t.propagator.propagate(gpsExtrapDate));

        //
        double distance = Vector3D.distance(gpsCurrentSCState.getPVCoordinates().getPosition(),
            t.getState().getPVCoordinates().getPosition());

        GPSSatInView tempGPSSatInView = new GPSSatInView(t.name, distance);
        double elevation = 0, azimuth = 0;
        try {
          elevation = opsSatCurrentFrame.getElevation(
              t.getState().getPVCoordinates().getPosition(), t.getState().getFrame(),
              t.getState().getDate());
          azimuth = opsSatCurrentFrame.getAzimuth(t.getState().getPVCoordinates().getPosition(),
              t.getState().getFrame(), t.getState().getDate());
        } catch (OrekitException ex) {
          Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
        }
        tempGPSSatInView.setElevation(FastMath.toDegrees(elevation));
        tempGPSSatInView.setAzimuth(FastMath.toDegrees(azimuth));
        if (distance < 25000000.0) {
          tempSatsInView.add(tempGPSSatInView);
          if (gpsOneShotDistances) {
            System.out.println(tempGPSSatInView.toString());
          }
        }
      }

      synchronized (gpsSatsInView) {
        gpsSatsInView.clear();
        gpsSatsInView.addAll(tempSatsInView);
      }
      synchronized (constellationPropagationCounterMutex) {
        constellationPropagationCounter++;
      }
      gpsOneShotDistances = false;
      propagatingConstellation = false;
    });
  }

  public LinkedList<GPSSatInView> getSatsInViewAsList()
  {
    LinkedList<GPSSatInView> result;
    synchronized (gpsSatsInView) {
      result = new LinkedList<>(gpsSatsInView);
    }
    return result;
  }

  public String getSatsInView()
  {
    StringBuilder result = new StringBuilder();
    synchronized (gpsSatsInView) {
      result.append("[" + this.gpsSatsInView.size() + "] satellites\n");
      for (GPSSatInView a : this.gpsSatsInView) {
        result.append(a.toString());
        result.append("\n");
      }
    }
    return result.toString();
  }

  public GPSSatInViewScience getSatsInViewScience()
  {
    LinkedList<GPSSatInView> result;
    synchronized (gpsSatsInView) {
      result = new LinkedList<>(gpsSatsInView);
    }
    double minDistance = 0;
    double maxDistance = 0;
    double minElevation = 0;
    double maxElevation = 0;
    double avgDistance = 0;
    double avgElevation = 0;
    double stdDevDistance = 0;
    double stdDevElevation = 0;
    if (result.size() > 0) {
      minDistance = result.get(0).distance;
      maxDistance = result.get(0).distance;
      minElevation = result.get(0).getElevation();
      maxElevation = result.get(0).getElevation();
      avgDistance = 0;
      avgElevation = 0;
      stdDevDistance = 0;
      stdDevElevation = 0;
      for (GPSSatInView sat : result) {
        if (sat.distance > maxDistance) {
          maxDistance = sat.distance;
        }
        if (sat.distance < minDistance) {
          minDistance = sat.distance;
        }
        if (sat.getElevation() > maxElevation) {
          maxElevation = sat.getElevation();
        }
        if (sat.getElevation() < minElevation) {
          minElevation = sat.getElevation();
        }
        avgDistance += sat.distance;
        avgElevation += sat.getElevation();

      }
      avgDistance /= result.size();
      avgElevation /= result.size();
      // STD
      for (GPSSatInView sat : result) {
        stdDevDistance += FastMath.pow(sat.distance - avgDistance, 2);
        stdDevElevation += FastMath.pow(sat.getElevation() - avgElevation, 2);

      }
      stdDevDistance /= result.size();
      stdDevDistance = FastMath.sqrt(stdDevDistance);
      stdDevElevation /= result.size();
      stdDevElevation = FastMath.sqrt(stdDevElevation);
    }
    return new GPSSatInViewScience(minDistance, maxDistance, minElevation, maxElevation,
        avgDistance, avgElevation, stdDevDistance, stdDevElevation);

  }

  public int getSatsNoInView()
  {
    synchronized (gpsSatsInView) {
      return this.gpsSatsInView.size();
    }

  }

  public void processPropagateStep(double timeStep) throws OrekitException
  {
    this.timeElapsed = this.timeElapsed + timeStep;
    extrapDate = extrapDate.shiftedBy(timeStep);
    synchronized (magneticFieldVectorLock) {
      boolean[] active = {
        this.attitudeMode.equals(ATTITUDE_MODE.SUN_POINTING),
        this.attitudeMode.equals(ATTITUDE_MODE.NADIR_POINTING),
        this.attitudeMode.equals(ATTITUDE_MODE.TARGET_TRACKING),
        this.attitudeMode.equals(ATTITUDE_MODE.LOF_TARGET),
        this.attitudeMode.equals(ATTITUDE_MODE.LOF_TARGET_SPIN),
        this.attitudeMode.equals(ATTITUDE_MODE.BDOT_DETUMBLE),
        this.attitudeMode.equals(ATTITUDE_MODE.SPIN_STABILIZED),
        this.attitudeMode.equals(ATTITUDE_MODE.VECTOR_POINTING)
      };
      if (!this.attitudeMode.equals(ATTITUDE_MODE.VECTOR_POINTING)) {
        this.vectorPointing.stop();
      }
      this.attitudeState.setSwitched(active);
      this.spacecraftState = this.runningPropagator.propagate(extrapDate);
    }
    if (!propagatingConstellation) {
      processGPSConstellationPropagateStep();
    }
    Transform inertToSpacecraft = this.spacecraftState.toTransform();
    final PVCoordinatesProvider sunPVProvider = CelestialBodyFactory.getSun();
    Vector3D sunInert = sunPVProvider
        .getPVCoordinates(spacecraftState.getDate(), spacecraftState.getFrame()).getPosition();
    Vector3D sunSat = inertToSpacecraft.transformPosition(sunInert);
    double sunX = sunSat.getX() / sunSat.getNorm();
    double sunY = sunSat.getY() / sunSat.getNorm();
    double sunZ = sunSat.getZ() / sunSat.getNorm();
    this.sunVector[0] = sunX;
    this.sunVector[1] = sunY;
    this.sunVector[2] = sunZ;
    GeodeticPoint newPosition = this.getGeodeticPoint();
    synchronized (magneticFieldVectorLock) {
      this.geoMagneticField = GeoMagneticFieldFactory.getIGRF(getDecimalYear(extrapDate));
      this.magneticFieldVector = this.geoMagneticField
          .calculateField(FastMath.toDegrees(newPosition.getLatitude()),
              FastMath.toDegrees(newPosition.getLongitude()), newPosition.getAltitude() / 1000.0)
          .getFieldVector().toArray();
    }
    if (this.lastPosition != null) {
      boolean isInView = isInGSView(this.spacecraftState);
      if (this.lastPosition.getLatitude() < 0 && newPosition.getLatitude() >= 0) {
        this.hasAnx = false;
        checkAOSLOS();

      }
      if (this.lastPosition.getLatitude() > 0 && newPosition.getLatitude() <= 0) {
        this.hasDnx = false;
        checkAOSLOS();
      }
      boolean AOSPassed = false;
      boolean LOSPassed = false;
      if (this.nextAOS != null) {
        AOSPassed = this.nextAOS.compareTo(this.extrapDate) < 0;
      }
      if (this.nextLOS != null) {
        LOSPassed = this.nextLOS.compareTo(this.extrapDate) < 0;
      }
      if (AOSPassed) {
        this.nextAOS = null;
        this.hasAOS = false;
      }
      if (LOSPassed) {
        this.nextLOS = null;
        this.hasLOS = false;
      }
    }
    this.lastPosition = newPosition;

    if (!hasAnx) {
      this.nextAnx = findNextCrossing(true);
    }
    if (!hasDnx) {
      this.nextDnx = findNextCrossing(false);
    }

    // The code below was eating the CPU, it was commented because it it
    // unnecessary functionality for the NMF.
    /*
     * if (!hasAOS) { //this.hasAOS=true; new Thread() {
     * 
     * @Override public void run() { nextAOS = findNextSignalEvent(true); }
     * }.start(); } if (!hasLOS) { //this.hasLOS=true; new Thread() {
     * 
     * @Override public void run() { nextLOS = findNextSignalEvent(false); }
     * }.start(); }
     */
    this.isInitialized = true;

  }

  public Orbit getOrbit()
  {
    // double lat = Math.asin(this.r.z() / this.r.length());

    return this.spacecraftState.getOrbit();
  }

  /**
   * returns the current TLE
   *
   * WARNING:
   *
   * Using getTLE() with any other than the TLEPropagator results in incomplete and inaccurate
   * TLEMessages!
   *
   * Also When not using TLEPropagator, the following will apply:
   *
   * Catalog Number, launch year, launch number, ephemeris type, mean motion d1 and d2, number of
   * revolutions and BStart will be set to 0"
   *
   * launch Piece will be set to "N", classification will be set to 'U'
   *
   * Element number will start at 0 and count up on every call of getTLE()
   *
   * @return
   */
  public TLE getTLE()
  {

    if (this.runningPropagator instanceof TLEPropagator) {
      return ((TLEPropagator) this.runningPropagator).getTLE();
    } else { // in case we are using the keplerian propagator, the TLE can not be reconstructed completle!
      this.logger.log(Level.WARNING,
          "Using getTLE() with any other than the TLEPropagator results in incomplete and inaccurate TLE Messages!"
          + "\n"
          + "Catalog Number, launch year, launch number, ephemeris type, mean motion d1 and d2, number of revolutions and BStart will be set to 0"
          + "\n"
          + "launch Piece will be set to \"N\", classification will be set to 'U'"
          + "\n"
          + "Element number will start at 0 and count up on every call of getTLE()");

      KeplerianOrbit orbit = (KeplerianOrbit) OrbitType.KEPLERIAN.convertType(this.getOrbit());

      int SatelliteNumber = 0;
      char classification = 'U';
      int launchYear = 0;
      int launchNumber = 0;
      String launchPiece = "N";
      int ephemerisType = 0;
      int elementNumber = this.tleNumber;
      AbsoluteDate date = orbit.getDate();
      if (this.initialTLE != null) {
        SatelliteNumber = this.initialTLE.getSatelliteNumber();
        classification = this.initialTLE.getClassification();
        launchYear = this.initialTLE.getLaunchYear();
        launchNumber = this.initialTLE.getLaunchNumber();
        launchPiece = this.initialTLE.getLaunchPiece();
        ephemerisType = this.initialTLE.getEphemerisType();
        elementNumber = this.initialTLE.getElementNumber() + this.tleNumber;
        date = this.initialTLE.getDate();
      }
      this.tleNumber = +1;

      return new TLE(
          SatelliteNumber,
          classification,
          launchYear,
          launchNumber,
          launchPiece,
          ephemerisType,
          elementNumber,
          date,
          orbit.getKeplerianMeanMotion(),
          0.0,// mean motion D1
          0.0,// mean motion D2
          orbit.getE(),
          orbit.getI(),
          orbit.getPerigeeArgument(),
          orbit.getRightAscensionOfAscendingNode(),
          orbit.getMeanAnomaly(),
          0,// revolutions
          0); // bStart / drag coeficient

    }

  }

  public String getAbsoluteDate()
  {
    return this.initialDate.shiftedBy(timeElapsed).toString();
  }

  public Attitude getAttitude()
  {
    return this.spacecraftState.getAttitude();
  }

  public void putQuaternionsInVectorFromYPR(double yaw, double pitch, double roll, float[] q)
  {
    Rotation test = new Rotation(RotationOrder.YXZ, RotationConvention.FRAME_TRANSFORM,
        FastMath.toRadians(-yaw), FastMath.toRadians(pitch), FastMath.toRadians(-roll));
    q[0] = (float) test.getQ0();
    q[1] = (float) test.getQ1();
    q[2] = (float) test.getQ2();
    q[3] = (float) test.getQ3();
  }

  public void putQuaternionsInVector(float[] q)
  {

    Attitude attitude = this.spacecraftState.getAttitude();
    q[0] = (float) attitude.getRotation().getQ0();
    q[1] = (float) attitude.getRotation().getQ1();
    q[2] = (float) attitude.getRotation().getQ2();
    q[3] = (float) attitude.getRotation().getQ3();

  }

  public GeodeticPoint getGeodeticPoint(SpacecraftState state)
  {
    try {
      return this.earth.transform(state.getOrbit().getPVCoordinates().getPosition(),
              inertialFrame, this.initialDate.shiftedBy(timeElapsed));
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  public GeodeticPoint getGeodeticPoint()
  {
    try {
      // TimeStampedPVCoordinates coord=this.spacecraftState.getPVCoordinates(new
      // TopocentricFrame(this.earth,new GeodeticPoint(0, 0, 0),"earth")).
      // TopocentricFrame earthFrame=new TopocentricFrame(this.earth,new
      // GeodeticPoint(0, 0, 0),"earth");
      GeodeticPoint point = null;
      if (this.spacecraftState != null) {
        point = this.earth.transform(
            this.spacecraftState.getOrbit().getPVCoordinates().getPosition(), inertialFrame,
            this.initialDate.shiftedBy(timeElapsed));
      }
      return point;
    } catch (OrekitException ex) {
      Logger.getLogger(OrekitCore.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
  private void scheduleStateTargetTimer()
  {
    if (stateTargetTask != null) {
      this.stateTargetTask.cancel();
    }
    this.stateTarget = 0;
    stateTargetTask = new java.util.TimerTask()
    {
      @Override
      public void run() {
          stateTarget = 1;
      }
    };
    this.stateTargetTimer.schedule(stateTargetTask, delayPeriod);
  }

  public byte getStateTarget()
  {
    return stateTarget;
  }

  /**
   * The database backend thread factory
   */
  static class SimThreadFactory implements ThreadFactory
  {

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    SimThreadFactory(String prefix)
    {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      namePrefix = prefix + "-thread-";
    }

    @Override
    public Thread newThread(Runnable r)
    {
      Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
      if (t.isDaemon()) {
        t.setDaemon(false);
      }
      if (t.getPriority() != Thread.NORM_PRIORITY) {
        t.setPriority(Thread.NORM_PRIORITY);
      }
      return t;
    }
  }

}
