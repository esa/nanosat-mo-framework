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
package esa.mo.platform.impl.provider.gen;

import esa.mo.platform.impl.util.HelperGPS;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.platform.gps.structures.Position;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;

/**
 *
 * @author Cesar Coelho
 */
public abstract class GPSNMEAonlyAdapter implements GPSAdapterInterface
{

  @Override
  public Position getCurrentPosition()
  {
    String gpggalong = "";
    try {
      gpggalong = this.getNMEASentence("GPGGALONG");
      Position position = HelperGPS.gpggalong2Position(gpggalong);
      return position;
    } catch (NumberFormatException ex1) {
      Logger.getLogger(GPSNMEAonlyAdapter.class.getName()).log(Level.SEVERE,
          "Number format exception! The gpggalong string is: " + gpggalong, ex1);
    } catch (IOException ex) {
      Logger.getLogger(GPSNMEAonlyAdapter.class.getName()).log(Level.SEVERE,
          "The current position could not be retrieved! Exception: {0}", ex.getMessage());
    }

    return null;
  }

  @Override
  public SatelliteInfoList getSatelliteInfoList()
  {
    try {
      String gpgsv = this.getNMEASentence("GPGSV");
      return HelperGPS.gpgsv2SatelliteInfoList(gpgsv);
    } catch (IOException ex) {
      Logger.getLogger(GPSNMEAonlyAdapter.class.getName()).log(Level.SEVERE, null, ex);
    }

    return null;
  }
}
