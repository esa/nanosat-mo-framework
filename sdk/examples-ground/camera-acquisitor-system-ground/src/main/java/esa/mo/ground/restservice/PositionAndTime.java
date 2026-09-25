/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
 * Class for containing geographical data and a corresponding date (used for
 * ground track)
 *
 * @author Kevin Otto
 */
@JsonIgnoreProperties(value = {"orekitDate", "location"})
public class PositionAndTime {

    /** The orekit date. */
    public final AbsoluteDate orekitDate;
    /** The date. */
    public final String date;

    /** The location. */
    public final GeodeticPoint location;
    /** The latitude. */
    public final double latitude;
    /** The longitude. */
    public final double longitude;
    /** The altitude. */
    public final double altitude;

    /**
     * Creates a new {@code PositionAndTime}.
     *
     * @param orekitDate the orekit date
     * @param location the location
     */
    public PositionAndTime(AbsoluteDate orekitDate, GeodeticPoint location) {
        this.orekitDate = orekitDate;
        this.date = orekitDate.toString();

        this.location = location;
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
    }

    /**
     * Returns the orekit date.
     *
     * @return the orekit date
     */
    public AbsoluteDate getOrekitDate() {
        return orekitDate;
    }

    /**
     * Returns the date.
     *
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * Returns the location.
     *
     * @return the location
     */
    public GeodeticPoint getLocation() {
        return location;
    }

    /**
     * Returns the latitude.
     *
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude.
     *
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns the altitude.
     *
     * @return the altitude
     */
    public double getAltitude() {
        return altitude;
    }

}
