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

import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.platform.gps.body.GetLastKnownPositionResponse;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.structures.TwoLineElementSet;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.propagation.analytical.tle.TLE;

/**
 * Class handling communication with the navigation system (GPS)
 *
 * @author Kevin Otto <Kevin@KevinOtto.de>
 */
public class CameraAcquisitorSystemGPSHandler extends GPSAdapter
{

  private static final Logger LOGGER = Logger.getLogger(
      CameraAcquisitorSystemGPSHandler.class.getName());

  private final CameraAcquisitorSystemMCAdapter casMCAdapter;

  public CameraAcquisitorSystemGPSHandler(CameraAcquisitorSystemMCAdapter casMCAdapter)
  {
    this.casMCAdapter = casMCAdapter;
  }

  public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
      Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction)
  {
    return new UInteger(0); // error code 0 - unknown error
  }

  /**
   * gets the current geographical position of the Satellite (longitude, latitude, altitude)
   *
   * @return
   */
  public GeodeticPoint getCurrentPosition()
  {
    GetLastKnownPositionResponse pos;
    try {
      casMCAdapter.getConnector().getPlatformServices().getGPSService().getPosition(this);//todo make more elegant
      pos = casMCAdapter.getConnector().getPlatformServices().getGPSService().getLastKnownPosition();
      //get geographical position
      float latitude = pos.getBodyElement0().getLatitude();
      float longitude = pos.getBodyElement0().getLongitude();
      float altitude = pos.getBodyElement0().getAltitude();
      return new GeodeticPoint(latitude, longitude, altitude);

    } catch (NMFException | IOException | MALInteractionException | MALException ex) {
      Logger.getLogger(CameraAcquisitorSystemGPSHandler.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }
}
