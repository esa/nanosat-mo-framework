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
package esa.mo.platform.impl.provider.gen;

import java.io.IOException;

import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;

/**
 * The GPS interface for adapters.
 *
 * @author Cesar Coelho
 */
public interface GPSAdapterInterface {

    /**
     * Checks if the device is present and accessible.
     *
     * @return true if the device is present and available for operations.
     */
    boolean isUnitAvailable();

    /**
     * Request a NMEA sentence from a GPS unit.
     *
     * @param identifier The NMEA Identifier
     * @return
     * @throws java.io.IOException
     */
    String getNMEASentence(final String identifier) throws IOException;

    /**
     * Request the current position from the GPS
     *
     * @return The Position or null in case of error
     */
    Position getCurrentPosition();

    /**
     * Requests the information of the satellites in view.
     *
     * @return The list of Satellites Information or null in case of error
     */
    SatelliteInfoList getSatelliteInfoList();

    /**
     * Requests the BESTXYZ NMEA sentence
     *
     * @return BESTXYZ response
     * @throws IOException if the request fails
     */
    String getBestXYZSentence() throws IOException;

    /**
     * Requests the TIMEA NMEA sentence
     *
     * @return TIMEA response
     * @throws IOException if the request fails
     */
    String getTIMEASentence() throws IOException;

}
