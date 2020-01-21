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
import esa.mo.nmf.MCRegistration.RegistrationMode;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFInterface;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.FactoryManagedFrame;
import org.orekit.frames.FramesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

/**
 * Class for Interfacing with the Camera Acquisitor System. This class handles all Parameters and
 * forwards commands to the corresponding Classes that handle them.
 */
public class CameraAcquisitorSystemMCAdapter extends MonitorAndControlNMFAdapter
{

  private static final Logger LOGGER = Logger.getLogger(
      CameraAcquisitorSystemMCAdapter.class.getName());

  private NMFInterface connector;

  //create earth reference frame:
  public final FactoryManagedFrame earthFrame = FramesFactory.getITRF(
      IERSConventions.IERS_2010,
      true);
  public final OneAxisEllipsoid earth = new OneAxisEllipsoid(
      Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
      Constants.WGS84_EARTH_FLATTENING, this.earthFrame);
  ;

  private CameraAcquisitorSystemCameraTargetHandler cameraHandler;
  private CameraAcquisitorSystemGPSHandler gpsHandler;

  public CameraAcquisitorSystemCameraTargetHandler getCameraHandler()
  {
    return cameraHandler;
  }

  public CameraAcquisitorSystemGPSHandler getGpsHandler()
  {
    return gpsHandler;
  }

  public NMFInterface getConnector()
  {
    return connector;
  }

  public CameraAcquisitorSystemMCAdapter(final NMFInterface connector)
  {
    this.connector = connector;
    this.cameraHandler = new CameraAcquisitorSystemCameraTargetHandler(this);
    this.gpsHandler = new CameraAcquisitorSystemGPSHandler(this);
  }

  @Override
  public void initialRegistrations(MCRegistration registration)
  {
    // Prevent definition updates on consecutive application runs
    registration.setMode(RegistrationMode.DONT_UPDATE_IF_EXISTS);
    registerParameters(registration);
    CameraAcquisitorSystemCameraTargetHandler.registerActions(registration);
  }

  @Override
  public Attribute onGetValue(Identifier identifier, Byte rawType)
  {
    return null;
  }

  @Override
  public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values)
  {
    return false;
  }

  @Override
  public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
      Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction)
  {
    this.cameraHandler.actionArrived(name, attributeValues, actionInstanceObjId, reportProgress,
        interaction);
    return null;
  }

  private void registerParameters(MCRegistration registration)
  {
    ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
    IdentifierList paramNames = new IdentifierList();
    // TODO add parameters
    // exposure
    // gain
    // resolution
    // ...
    registration.registerParameters(paramNames, defs);
  }

}
