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
import org.orekit.orbits.Orbit;

/**
 * Class for handling orbit propagation for finding time frames for photographs
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class CameraAcquisitorSystemTargetLocation implements
    Comparable<CameraAcquisitorSystemTargetLocation>
{

  public static enum TimeModeEnum
  {
    ANY, DAYTIME, NIGHTTIME
  }

  private Date optimalTime;

  //timeframe in wich photograph is possible:
  private Date startTime;
  private Date endTime;

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
      TimeModeEnum timeMode, Orbit currentOrbit)
  {
    this.longitude = longitude;
    this.latitude = latitude;
    this.maxAngle = maxAngle;
    this.timeMode = timeMode;

    calculateTimeFrame(currentOrbit);
  }

  /**
   * calculates the next time frame in which this location can be photographed.
   */
  private void calculateTimeFrame(Orbit currentOrbit)
  {
    //TODO implement
  }

  public Date getOptimalTime()
  {
    return optimalTime;
  }

  public Date getStartTime()
  {
    return startTime;
  }

  public Date getEndTime()
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
}
