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
package esa.mo.ground.restservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.time.AbsoluteDate;

/**
 * Class for containing geographical data and a corresponding date (used for ground track)
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
@JsonIgnoreProperties(value = {"orekitDate", "location"})
public class PositionAndTime
{

  public final AbsoluteDate orekitDate;
  public final String date;

  public final GeodeticPoint location;
  public final double latitude;
  public final double longitude;
  public final double altitude;

  public PositionAndTime(AbsoluteDate orekitDate, GeodeticPoint location)
  {
    this.orekitDate = orekitDate;
    this.date = orekitDate.toString();

    this.location = location;
    latitude = location.getLatitude();
    longitude = location.getLongitude();
    altitude = location.getLongitude();
  }

  public AbsoluteDate getOrekitDate()
  {
    return orekitDate;
  }

  public String getDate()
  {
    return date;
  }

  public GeodeticPoint getLocation()
  {
    return location;
  }

  public double getLatitude()
  {
    return latitude;
  }

  public double getLongitude()
  {
    return longitude;
  }

  public double getAltitude()
  {
    return altitude;
  }

}
