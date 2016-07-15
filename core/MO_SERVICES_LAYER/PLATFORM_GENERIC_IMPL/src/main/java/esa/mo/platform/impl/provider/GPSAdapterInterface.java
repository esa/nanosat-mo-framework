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
package esa.mo.platform.impl.provider;

import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;


/**
 *
 * @author Cesar Coelho
 */
public interface GPSAdapterInterface {
    
    /**
     * Request a NMEA sentence from a GPS unit.
     *
     * @param identifier The NMEA Identifier
     * @return
     */
    public String getNMEASentence(final String identifier);
    
    /**
     * Request the current position from the GPS
     *
     * @return The Position
     */
    public Position getCurrentPosition();

    /**
     * Requests the information of the satellites in view.
     *
     * @return The list of Satellites Information
     */
    public SatelliteInfoList getSatelliteInfoList();
    
}
