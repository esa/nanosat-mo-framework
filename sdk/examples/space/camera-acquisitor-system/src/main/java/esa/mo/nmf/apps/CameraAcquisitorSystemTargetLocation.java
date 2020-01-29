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

import java.util.Date;
import org.hipparchus.ode.events.Action;
//import org.ccsds.moims.mo.com.event.provider.EventHandler;

import org.hipparchus.util.FastMath;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.FactoryManagedFrame;
import org.orekit.frames.TopocentricFrame;
import org.orekit.models.earth.EarthITU453AtmosphereRefraction;
import org.orekit.orbits.Orbit;
import org.orekit.propagation.Propagator;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
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
  //current value = secondsPerMinute * minutesPerHour * hoursPerDay * 6 = 6 days
  private double simulationRange = 60 * 60 * 24 * 6; //todo add parameter

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
      TimeModeEnum timeMode, Orbit currentOrbit, CameraAcquisitorSystemMCAdapter addapter) throws
      Exception
  {
    this.longitude = longitude;
    this.latitude = latitude;
    this.maxAngle = maxAngle;
    this.timeMode = timeMode;

    calculateTimeFrame(currentOrbit, addapter);
  }

  /**
   * calculates the next time frame in which this location can be photographed.
   *
   * @param currentOrbit the current Orbit
   * @param addapter     the addapter to be used
   */
  public void calculateTimeFrame(Orbit currentOrbit, CameraAcquisitorSystemMCAdapter addapter)
      throws Exception
  {

    GeodeticPoint targetLocation = new GeodeticPoint(this.latitude, this.longitude, 0);
    TopocentricFrame groundFrame = new TopocentricFrame(addapter.earth, targetLocation,
        "cameraTarget");

    // ------------------ create Detectors ------------------
    EventDetector overpassDetector = new ElevationDetector(groundFrame)
        .withConstantElevation(FastMath.toRadians(180.0 - this.maxAngle));

    if (timeMode != TimeModeEnum.ANY) {

      PVCoordinatesProvider sun = CelestialBodyFactory.getSun(); //create detector for nighttime

      EventDetector timeModeDetector = new GroundAtNightDetector(groundFrame, sun, latitude,
          new EarthITU453AtmosphereRefraction(0));

      //invert nightTime detector if photograph should be taken at daytime
      if (this.timeMode == TimeModeEnum.DAYTIME) {
        timeModeDetector = BooleanDetector.notCombine(timeModeDetector);
      }
      overpassDetector = BooleanDetector.andCombine(timeModeDetector, overpassDetector)
          .withHandler(new timedPassHandler(this));
    } else {
      overpassDetector = BooleanDetector.andCombine(overpassDetector)
          .withHandler(new timedPassHandler(this));
    }

    // ------------------ Setup Simulation------------------
    //TODO better propagator?
    Propagator kepler = new KeplerianPropagator(currentOrbit);
    kepler.addEventDetector(overpassDetector);
    AbsoluteDate startDate = currentOrbit.getDate();
    AbsoluteDate endDate = startDate.shiftedBy(simulationRange);

    // ------------------ Simulate Orbit ------------------
    SpacecraftState finalState = kepler.propagate(endDate);
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

    public timedPassHandler(CameraAcquisitorSystemTargetLocation parent)
    {
      this.parent = parent;
    }

    @Override
    public Action eventOccurred(SpacecraftState s, BooleanDetector detector, boolean increasing)
    {
      if (!increasing) {
        parent.endTime = s.getDate();
        double elepsedTime = parent.endTime.durationFrom(parent.startTime);
        parent.optimalTime = parent.startTime.shiftedBy(elepsedTime / 2);
        return Action.STOP;
      }
      return Action.CONTINUE;
    }
  }

  private static class overpassBeginHandler implements EventHandler<ElevationDetector>
  {

    private final CameraAcquisitorSystemTargetLocation parent;

    public overpassBeginHandler(CameraAcquisitorSystemTargetLocation parent)
    {
      this.parent = parent;
    }

    @Override
    public Action eventOccurred(SpacecraftState s, ElevationDetector detector, boolean increasing)
    {
      if (increasing) {
        parent.startTime = s.getDate();
      }
      return Action.CONTINUE;
    }
  }
}
