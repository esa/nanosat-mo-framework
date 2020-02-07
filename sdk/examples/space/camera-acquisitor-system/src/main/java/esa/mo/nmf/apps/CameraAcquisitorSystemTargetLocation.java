/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.nmf.apps;

import java.time.Instant;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hipparchus.ode.events.Action;
import org.hipparchus.util.FastMath;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.TopocentricFrame;
import org.orekit.models.earth.EarthITU453AtmosphereRefraction;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.BooleanDetector;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.GroundAtNightDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * Class for handling orbit propagation for finding time frames for photographs
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class CameraAcquisitorSystemTargetLocation implements
    Comparable<CameraAcquisitorSystemTargetLocation>
{

  //how far to simulate into the future (in seconds)s
  //current value = secondsPerMinute * minutesPerHour * hoursPerDay * 365 = 365 days
  private double simulationRange = 60 * 60 * 24 * 365; //todo add parameter

  private static final Logger LOGGER = Logger.getLogger(
      CameraAcquisitorSystemTargetLocation.class.getName());

  public static enum TimeModeEnum
  {
    ANY, DAYTIME, NIGHTTIME
  }

  private AbsoluteDate optimalTime;

  //timeframe in wich photograph is possible:
  private AbsoluteDate startTime;
  private AbsoluteDate endTime;

  private double longitude;
  private double latitude;
  private double maxAngle;

  private TimeModeEnum timeMode;

  private TimerTask task;
  private final long ID;

  /**
   * creates an Instance with the given parameters and calculates the next possible time frame, a
   * photograph with the given constraints can be taken.
   *
   * @param longitude    The longitude that should be photographed
   * @param latitude     The latitude that should be photographed
   * @param maxAngle     The maximum angle between spacecraft and target location the photograph
   *                     should be taken at
   * @param timeMode     The time the photograph should be taken at. ANY will use the next possible
   *                     pass, DAYTIME will use the next pass that has the target location in
   *                     daylight, NIGHTTIME will use the next pass that has the target location in
   *                     shadow
   * @param currentOrbit The current orbit of the spacecraft (used for orbit propagation)
   */
  public CameraAcquisitorSystemTargetLocation(double longitude, double latitude, double maxAngle,
      TimeModeEnum timeMode, TLE tle, AbsoluteDate notBeforeDate,
      CameraAcquisitorSystemMCAdapter addapter) throws
      Exception
  {
    this(longitude, latitude, maxAngle, timeMode, tle, notBeforeDate,
        addapter.getWorstCaseRotationTimeSeconds(),
        addapter.earth);
  }

  public CameraAcquisitorSystemTargetLocation(double longitude, double latitude, double maxAngle,
      TimeModeEnum timeMode, TLE tle, CameraAcquisitorSystemMCAdapter addapter) throws
      Exception
  {
    this(longitude, latitude, maxAngle, timeMode, tle, addapter.getWorstCaseRotationTimeSeconds(),
        addapter.earth);
  }

  public CameraAcquisitorSystemTargetLocation(double longitude, double latitude, double maxAngle,
      TimeModeEnum timeMode, TLE tle, long worstCaseRotationTimeSeconds, BodyShape earth) throws
      Exception
  {
    this(longitude, latitude, maxAngle, timeMode, tle, tle.getDate(),
        worstCaseRotationTimeSeconds, earth);
  }

  public CameraAcquisitorSystemTargetLocation(double longitude, double latitude, double maxAngle,
      TimeModeEnum timeMode, TLE tle, AbsoluteDate notBeforeDate,
      long worstCaseRotationTimeSeconds, BodyShape earth) throws
      Exception
  {
    this.longitude = longitude;
    this.latitude = latitude;
    this.maxAngle = maxAngle;
    this.timeMode = timeMode;

    this.ID = Instant.now().toEpochMilli();

    calculateTimeFrame(tle, notBeforeDate, worstCaseRotationTimeSeconds, earth);
  }

  public final void calculateTimeFrame(TLE tle, long worstCaseRotationTimeSeconds,
      BodyShape earth) throws
      Exception
  {
    calculateTimeFrame(tle, tle.getDate(), worstCaseRotationTimeSeconds, earth);
  }

  public final void calculateTimeFrame(TLE tle,
      CameraAcquisitorSystemMCAdapter addapter)
      throws Exception
  {
    calculateTimeFrame(tle, tle.getDate(), addapter.getWorstCaseRotationTimeSeconds(),
        addapter.earth);
  }

  /**
   * calculates the next time frame in which this location can be photographed.
   *
   * @param tle     the current TLE
   * @param adapter the adapter to be used
   * @throws java.lang.Exception
   */
  public final void calculateTimeFrame(TLE tle, AbsoluteDate notBeforeDate,
      long worstCaseRotationTimeSeconds, BodyShape earth)
      throws Exception
  {
    LOGGER.log(Level.INFO, "Calculating timeframe for photograph target");
    GeodeticPoint targetLocation = new GeodeticPoint(this.latitude, this.longitude, 0);
    TopocentricFrame groundFrame = new TopocentricFrame(earth, targetLocation,
        "cameraTarget");

    // ------------------ create Detectors ------------------
    LOGGER.log(Level.INFO, "Creating propagation detectors");
    EventDetector overpassDetector = new ElevationDetector(groundFrame)
        .withConstantElevation(FastMath.toRadians(90.0 - this.maxAngle));

    if (timeMode != TimeModeEnum.ANY) {

      PVCoordinatesProvider sun = CelestialBodyFactory.getSun(); //create detector for nighttime

      EventDetector timeModeDetector = new GroundAtNightDetector(groundFrame, sun, latitude,
          new EarthITU453AtmosphereRefraction(0));

      //invert nightTime detector if photograph should be taken at daytime
      if (this.timeMode == TimeModeEnum.DAYTIME) {
        timeModeDetector = BooleanDetector.notCombine(timeModeDetector);
      }
      overpassDetector = BooleanDetector.andCombine(timeModeDetector, overpassDetector)
          .withHandler(new timedPassHandler(this, notBeforeDate, worstCaseRotationTimeSeconds));
    } else {
      overpassDetector = BooleanDetector.orCombine(overpassDetector)
          .withHandler(new timedPassHandler(this, notBeforeDate, worstCaseRotationTimeSeconds));
    }
    // ------------------ Setup Simulation------------------
    LOGGER.log(Level.INFO, "Setting up Propagator");

    TLEPropagator propagator = TLEPropagator.selectExtrapolator(tle);

    propagator.addEventDetector(overpassDetector);

    AbsoluteDate startDate = tle.getDate();
    AbsoluteDate endDate = startDate.shiftedBy(simulationRange);
    LOGGER.log(Level.INFO, "Start Date: {0}", startDate);
    LOGGER.log(Level.INFO, "EN Date: {0}", endDate);
    // ------------------ Simulate Orbit ------------------
    LOGGER.log(Level.INFO, "Simulating Orbit");
    SpacecraftState finalState = propagator.propagate(endDate);

    LOGGER.log(Level.INFO, "Final State: {0}", finalState);
    LOGGER.log(Level.INFO, "Final State Date: {0}", finalState.getDate());
    LOGGER.log(Level.INFO, "Optimal Time: {0}", this.optimalTime);

    if (finalState.getDate() == endDate) {
      throw new Exception("No possible Pass in set Timeframe");
    }
  }

  public AbsoluteDate getOptimalTime()
  {
    return optimalTime;
  }

  public AbsoluteDate getStartTime()
  {
    return startTime;
  }

  public AbsoluteDate getEndTime()
  {
    return endTime;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public double getMaxAngle()
  {
    return maxAngle;
  }

  public TimeModeEnum getTimeMode()
  {
    return timeMode;
  }

  @Override
  public int compareTo(CameraAcquisitorSystemTargetLocation other)
  {
    return this.optimalTime.compareTo(other.getOptimalTime());

  }

  private static class timedPassHandler implements EventHandler<BooleanDetector>
  {

    private final CameraAcquisitorSystemTargetLocation parent;
    private final AbsoluteDate notBeforeDate;
    private final long worstCaseRotationTimeSeconds;

    private boolean startIsSet = false;

    public timedPassHandler(CameraAcquisitorSystemTargetLocation parent, AbsoluteDate notBeforeDate,
        long worstCaseRotationTimeSeconds)
    {
      this.parent = parent;
      this.notBeforeDate = notBeforeDate;
      this.worstCaseRotationTimeSeconds = worstCaseRotationTimeSeconds;
    }

    @Override
    public Action eventOccurred(SpacecraftState s, BooleanDetector detector, boolean increasing)
    {
      if (increasing) {
        if (!startIsSet) {
          parent.startTime = s.getDate();
          startIsSet = true;
        }
      } else {
        parent.endTime = s.getDate();
        double elepsedTime = parent.endTime.durationFrom(parent.startTime);
        parent.optimalTime = parent.startTime.shiftedBy(elepsedTime / 2);
        LOGGER.log(Level.INFO, "evemt at: {0}", s.getDate());
        if (parent.optimalTime.durationFrom(CameraAcquisitorSystemMCAdapter.getNow()) <= worstCaseRotationTimeSeconds && parent.optimalTime.compareTo(
            this.notBeforeDate) > 0) {
          LOGGER.log(Level.INFO, "Time to close");
          startIsSet = false;
          return Action.CONTINUE;
        }
        LOGGER.log(Level.INFO, "Time found!");
        return Action.STOP;
      }
      return Action.CONTINUE;
    }
  }

  public TimerTask getTask()
  {
    return task;
  }

  public void setTask(TimerTask task)
  {
    this.task = task;
  }

}
