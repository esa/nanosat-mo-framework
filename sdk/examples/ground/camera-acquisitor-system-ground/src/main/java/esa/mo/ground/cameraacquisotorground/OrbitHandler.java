/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
package esa.mo.ground.cameraacquisotorground;

import esa.mo.ground.restservice.Pass;
import esa.mo.ground.restservice.PositionAndTime;

import java.util.LinkedList;

import org.hipparchus.util.FastMath;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.FactoryManagedFrame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.models.earth.EarthITU453AtmosphereRefraction;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.BooleanDetector;
import org.orekit.propagation.events.ElevationDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.GroundAtNightDetector;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinatesProvider;

/**
 * Class for calculating passes and propagating orbits.
 *
 * @author Kevin Otto
 */
public class OrbitHandler
{

  /**
   * Enum for pass time modes ANY: the photograph will be taken as soon as possible. DAYTIME: the
   * photograph will be taken at a daytime pass NIGHTTIME: the photograph will be taken at a
   * nighttime pass
   */
  public enum TimeModeEnum
  {
    ANY, DAYTIME, NIGHTTIME
  }

  private final FactoryManagedFrame earthFrame;
  private final OneAxisEllipsoid earth;
  private TLEPropagator propagator;
  private TLE initialTLE;

  /**
   * creates a new orbit handler
   *
   * @param tle the current TLE
   */
  public OrbitHandler(TLE tle)
  {

    earthFrame = FramesFactory.getITRF(IERSConventions.IERS_2010, true);//FramesFactory.getEME2000();
    earth = new OneAxisEllipsoid(
        Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
        Constants.WGS84_EARTH_FLATTENING, earthFrame);

    initialTLE = tle;
    propagator = TLEPropagator.selectExtrapolator(tle);
  }

  /**
   * updates the TLE that is used for orbit propagation
   *
   * @param tle the new TLE
   */
  public void updateTLE(TLE tle)
  {
    initialTLE = tle;
    propagator = TLEPropagator.selectExtrapolator(tle);
  }

  /**
   * resets the State of the propagator.
   */
  public void reset()
  {
    propagator = TLEPropagator.selectExtrapolator(initialTLE);
  }

  /**
   * calculates ground with given parameters.
   *
   * @param startDate       date of first entry in the ground track.
   * @param endDate         date of last entry in the ground track.
   * @param timeStepSeconds seconds between entries.
   * @return
   */
  public PositionAndTime[] getGroundTrack(AbsoluteDate startDate, AbsoluteDate endDate,
      double timeStepSeconds)
  {
    LinkedList<PositionAndTime> positionSeries = new LinkedList<>();

    // get position at every timestep
    for (AbsoluteDate currentDate = startDate;
        currentDate.compareTo(endDate) < 0;
        currentDate = currentDate.shiftedBy(timeStepSeconds)) {

      SpacecraftState currentState = this.propagator.propagate(startDate, currentDate);

      GeodeticPoint curreLocation = earth.transform(
          currentState.getPVCoordinates(earthFrame).getPosition(), earthFrame, currentDate);
      positionSeries.add(new PositionAndTime(currentDate, curreLocation));
    }
    return positionSeries.toArray(new PositionAndTime[0]);
  }

  public GeodeticPoint getPosition(AbsoluteDate endDate)
  {
    System.out.println(propagator.getInitialState().getDate());
    SpacecraftState finalState = propagator.propagate(endDate);
    System.out.println(propagator.getInitialState().getDate());
    return earth.transform(
        finalState.getPVCoordinates(earthFrame).getPosition(), earthFrame, finalState.getDate());
  }

  /**
   * returns the next time a pass happens with the given parameters
   *
   * @param longitude                    target longitude
   * @param latitude                     target latitude
   * @param maxAngle                     maximum angle between satellite and target
   * @param timeMode                     time at which the pass should be
   * @param notBeforeDate                the earliest time the pass can be
   * @param worstCaseRotationTimeSeconds the time needed to orient the satellite
   * @param simulationRange
   * @return the earliest pass that fits the given parameters
   */
  public Pass getPassTime(double longitude, double latitude, double maxAngle,
      TimeModeEnum timeMode, AbsoluteDate notBeforeDate, long worstCaseRotationTimeSeconds,
      long simulationRange)
  {

    GeodeticPoint targetLocation = new GeodeticPoint(latitude, longitude, 0);
    TopocentricFrame groundFrame = new TopocentricFrame(earth, targetLocation, "cameraTarget");

    // ------------------ create Detectors ------------------
    EventDetector overpassDetector = new ElevationDetector(groundFrame)
        .withConstantElevation(FastMath.toRadians(90.0 - maxAngle));

    Pass pass = new Pass(notBeforeDate, worstCaseRotationTimeSeconds);
    if (timeMode != TimeModeEnum.ANY) {
      PVCoordinatesProvider sun = CelestialBodyFactory.getSun(); //create detector for nighttime

      EventDetector timeModeDetector = new GroundAtNightDetector(groundFrame, sun, latitude,
          new EarthITU453AtmosphereRefraction(0));

      //invert nightTime detector if photograph should be taken at daytime
      if (timeMode == TimeModeEnum.DAYTIME) {
        timeModeDetector = BooleanDetector.notCombine(timeModeDetector);
      }
      overpassDetector = BooleanDetector.andCombine(timeModeDetector, overpassDetector)
          .withHandler(pass);
    } else {
      overpassDetector = BooleanDetector.orCombine(overpassDetector)
          .withHandler(pass);
    }

    propagator.addEventDetector(overpassDetector);

    AbsoluteDate endDate = notBeforeDate.shiftedBy(simulationRange);
    SpacecraftState finalState = propagator.propagate(endDate);

    propagator.clearEventsDetectors();

    return pass;
  }

}
