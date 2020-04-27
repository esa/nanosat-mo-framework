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

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MCRegistration.RegistrationMode;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.sdk.OrekitResources;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.orekit.data.DataProvidersManager;
import org.orekit.errors.OrekitException;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

/**
 * Class for Interfacing with the Camera Acquisitor System. This class handles all Parameters and
 * forwards commands to the corresponding Classes that handle them.
 */
public class CameraAcquisitorSystemMCAdapter extends MonitorAndControlNMFAdapter
{

  private static final Logger LOGGER = Logger.getLogger(
      CameraAcquisitorSystemMCAdapter.class.getName());

  private NMFInterface connector;

  private final CameraAcquisitorSystemCameraTargetHandler cameraTargetHandler;
  private final CameraAcquisitorSystemCameraHandler cameraHandler;

  private final CameraAcquisitorSystemGPSHandler gpsHandler;

  void recoverLastState()
  {
    this.cameraTargetHandler.recoverLastState();
  }

  public static enum ExposureTypeModeEnum
  {
    CUSTOM, AUTOMATIC // maybe add hdr?
  }

  private float gainRed = 1.0f;
  private float gainGreen = 1.0f;
  private float gainBlue = 1.0f;
  private ExposureTypeModeEnum exposureType = ExposureTypeModeEnum.AUTOMATIC;
  private float exposureTime = 1.0f;
  private long worstCaseRotationTimeMS = 1000000;
  private long attitudeSaftyMarginMS = 20000;//TODO add parameter
  private int maxRetrys = 10;//TODO add parameter
  private int pictureWidth = 2048;
  private int pictureHeight = 1944;
  private PictureFormat pictureType = PictureFormat.JPG;

  // --------------- parameter strings ------------------
  private static final String GAIN_RED = "GainRed";
  private static final String GAIN_GREEN = "GainGreen";
  private static final String GAIN_BLUE = "GainBlue";
  private static final String EXPOSURE_TYPE = "ExposureType";
  private static final String CUSTOM_EXPOSURE_TIME = "CustomExposureTime";
  private static final String WORST_CASE_ROTATION_TIME_MS = "WorstCaseRotationTimeMS";
  private static final String ATTITUDE_SAFTY_MARGIN_MS = "AttitudeSaftyMarginMS";
  private static final String MAX_RETRYS = "MaxRetrys";
  private static final String PICTURE_WIDTH = "PictureWidth";
  private static final String PICTURE_HEIGHT = "PictureHeight";
  private static final String PICTURE_TYPE = "PictureType";

  // ----------------------------------------------------
  public PictureFormat getPictureType()
  {
    return pictureType;
  }

  public float getGainRed()
  {
    return gainRed;
  }

  public float getGainGreen()
  {
    return gainGreen;
  }

  public float getGainBlue()
  {
    return gainBlue;
  }

  public ExposureTypeModeEnum getExposureType()
  {
    return exposureType;
  }

  public float getExposureTime()
  {
    return exposureTime;
  }

  public int getPictureWidth()
  {
    return pictureWidth;
  }

  public int getPictureHeight()
  {
    return pictureHeight;
  }

  public int getMaxRetrys()
  {
    return maxRetrys;
  }

  public long getWorstCaseRotationTimeMS()
  {
    return worstCaseRotationTimeMS;
  }

  public long getWorstCaseRotationTimeSeconds()
  {
    return worstCaseRotationTimeMS / 1000;
  }

  public CameraAcquisitorSystemCameraHandler getCameraHandler()
  {
    return cameraHandler;
  }

  public CameraAcquisitorSystemCameraTargetHandler getCameraTargetHandler()
  {
    return cameraTargetHandler;
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
    try {
      //load orekit-data wich is required for many parts of orekit to work.
      LOGGER.log(Level.INFO, "Loading orekit data");
      DataProvidersManager manager = DataProvidersManager.getInstance();
      manager.addProvider(OrekitResources.getOrekitData());
    } catch (OrekitException e) {
      LOGGER.log(Level.SEVERE, "Failed to initialise Orekit:\n{0}", e.getMessage());
    }

    this.connector = connector;
    LOGGER.log(Level.INFO, "init cameraTargetHandler");
    this.cameraTargetHandler = new CameraAcquisitorSystemCameraTargetHandler(this);
    LOGGER.log(Level.INFO, "init gpsHandler");
    this.gpsHandler = new CameraAcquisitorSystemGPSHandler(this);
    LOGGER.log(Level.INFO, "init cameraHandler");
    this.cameraHandler = new CameraAcquisitorSystemCameraHandler(this);

  }

  @Override
  public void initialRegistrations(MCRegistration registration)
  {
    LOGGER.log(Level.INFO, "initial registratin");
    // Prevent definition updates on consecutive application runs
    registration.setMode(RegistrationMode.DONT_UPDATE_IF_EXISTS);
    LOGGER.log(Level.INFO, "register parameters");
    registerParameters(registration);
    LOGGER.log(Level.INFO, "register target actions");
    CameraAcquisitorSystemCameraTargetHandler.registerActions(registration);
    LOGGER.log(Level.INFO, "register camera actions");
    CameraAcquisitorSystemCameraHandler.registerActions(registration);
  }

  @Override
  public Attribute onGetValue(Identifier identifier, Byte rawType)
  {
    if (connector == null) {
      return null;
    }

    switch (identifier.getValue()) {
      case (GAIN_RED):
        return new Union(gainRed);
      case (GAIN_GREEN):
        return new Union(gainGreen);
      case (GAIN_BLUE):
        return new Union(gainBlue);
      case (EXPOSURE_TYPE):
        return new Union(exposureType.ordinal());
      case (CUSTOM_EXPOSURE_TIME):
        return new Union(exposureTime);
      case (WORST_CASE_ROTATION_TIME_MS):
        return new Union(worstCaseRotationTimeMS);
      case (ATTITUDE_SAFTY_MARGIN_MS):
        return new Union(attitudeSaftyMarginMS);
      case (MAX_RETRYS):
        return new Union(maxRetrys);
      case (PICTURE_WIDTH):
        return new Union(pictureWidth);
      case (PICTURE_HEIGHT):
        return new Union(pictureHeight);
      case (PICTURE_TYPE):
        return new Union(pictureType.getOrdinal());
    }

    return null;
  }

  @Override
  public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values
  )
  {
    if (connector == null) {
      return null;
    }

    boolean result = false;
    for (int i = 0; i < identifiers.size(); i++) {

      switch (identifiers.get(i).getValue()) {
        case (GAIN_RED):
          gainRed = (float) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
          result = true;
          break;
        case (GAIN_GREEN):
          gainGreen = (float) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
          result = true;
          break;
        case (GAIN_BLUE):
          gainBlue = (float) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
          result = true;
          break;
        case (EXPOSURE_TYPE):
          exposureType = ExposureTypeModeEnum.values()[(int) HelperAttributes.attribute2JavaType(
              values.get(i).getRawValue())];
          result = true;
          break;
        case (CUSTOM_EXPOSURE_TIME):
          exposureTime = (float) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
          result = true;
          break;
        case (WORST_CASE_ROTATION_TIME_MS):
          worstCaseRotationTimeMS = (long) HelperAttributes.attribute2JavaType(
              values.get(i).getRawValue());
          result = true;
          break;
        case (ATTITUDE_SAFTY_MARGIN_MS):
          attitudeSaftyMarginMS = (int) HelperAttributes.attribute2JavaType(
              values.get(i).getRawValue());
          result = true;
          break;
        case (MAX_RETRYS):
          maxRetrys = (int) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
          break;
        case (PICTURE_WIDTH):
          pictureWidth = (int) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
          result = true;
          break;
        case (PICTURE_HEIGHT):
          pictureHeight = (int) HelperAttributes.attribute2JavaType(values.get(i).getRawValue());
          result = true;
          break;
        case (PICTURE_TYPE):
          pictureType = PictureFormat.fromOrdinal((int) HelperAttributes.attribute2JavaType(
              values.get(i).getRawValue()));
          result = true;
          break;
      }
    }

    return result;
  }

  @Override
  public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
      Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction
  )
  {

    if (name.getValue() == null) {
      return new UInteger(0);
    }

    LOGGER.log(Level.INFO, "number of parameters: {0}", attributeValues.size());

    switch (name.getValue()) {
      case (CameraAcquisitorSystemCameraTargetHandler.ACTION_PHOTOGRAPH_LOCATION):
        return this.cameraTargetHandler.photographLocation(attributeValues,
            actionInstanceObjId, reportProgress, interaction);
      case (CameraAcquisitorSystemCameraHandler.ACTION_PHOTOGRAPH_NOW):
        return this.cameraHandler.photographNow(attributeValues, actionInstanceObjId,
            reportProgress, interaction);
      default:
        return new UInteger(0);
    }

  }

  private void registerParameters(MCRegistration registration)
  {
    ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
    IdentifierList paramNames = new IdentifierList();

    ParameterDefinitionDetails details_gainR = new ParameterDefinitionDetails(
        "The red channel gain", Union.FLOAT_TYPE_SHORT_FORM.byteValue(), "", false, new Duration(0),
        null,
        null);
    defs.add(details_gainR);

    ParameterDefinitionDetails details_gainG = new ParameterDefinitionDetails(
        "The green channel gain", Union.FLOAT_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null,
        null);
    defs.add(details_gainG);

    ParameterDefinitionDetails details_gainB = new ParameterDefinitionDetails(
        "The blue channel gain", Union.FLOAT_TYPE_SHORT_FORM.byteValue(), "", false, new Duration(0),
        null,
        null);
    defs.add(details_gainB);

    ParameterDefinitionDetails details_expType = new ParameterDefinitionDetails(
        "The camera's exposure Type (CUSTOM = 0, AUTOMATIC = 1)",
        Union.INTEGER_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null, null);
    defs.add(details_expType);

    ParameterDefinitionDetails details_expTime = new ParameterDefinitionDetails(
        "The camera's exposure time (only used if exposureType is CUSTOM)",
        Union.FLOAT_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null, null);
    defs.add(details_expTime);

    ParameterDefinitionDetails details_worstCaseRotationTimeMS = new ParameterDefinitionDetails(
        "The maximum time (in Milliseconds) the Satelite will take to rotated if its in the worst posible orientation",
        Union.LONG_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null, null);
    defs.add(details_worstCaseRotationTimeMS);

    ParameterDefinitionDetails details_attitudeSaftyMarginMS = new ParameterDefinitionDetails(
        "The maximum time (in Milliseconds) the Satelite will holde the orientation after initiating the photograph",
        Union.INTEGER_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null, null);
    defs.add(details_attitudeSaftyMarginMS);

    ParameterDefinitionDetails details_maxRetrys = new ParameterDefinitionDetails(
        "The maximum amount of recalulations if the sceduler finds a collision in the picture timeframe",
        Union.INTEGER_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null, null);
    defs.add(details_maxRetrys);

    ParameterDefinitionDetails details_pictureWidth = new ParameterDefinitionDetails(
        "The width (x resolution) of the picture taken by the camera",
        Union.INTEGER_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null, null);
    defs.add(details_pictureWidth);

    ParameterDefinitionDetails details_pictureHeight = new ParameterDefinitionDetails(
        "The height (y resolution) of the picture taken by the camera",
        Union.INTEGER_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null, null);
    defs.add(details_pictureHeight);

    ParameterDefinitionDetails details_pictureType = new ParameterDefinitionDetails(
        "The picture type to use (uses PictureFormat ENUM)",
        Union.INTEGER_TYPE_SHORT_FORM.byteValue(), "", false,
        new Duration(0),
        null, null);
    defs.add(details_pictureType);

    paramNames.add(new Identifier(GAIN_RED));
    paramNames.add(new Identifier(GAIN_GREEN));
    paramNames.add(new Identifier(GAIN_BLUE));
    paramNames.add(new Identifier(EXPOSURE_TYPE));
    paramNames.add(new Identifier(CUSTOM_EXPOSURE_TIME));
    paramNames.add(new Identifier(WORST_CASE_ROTATION_TIME_MS));
    paramNames.add(new Identifier(ATTITUDE_SAFTY_MARGIN_MS));
    paramNames.add(new Identifier(MAX_RETRYS));
    paramNames.add(new Identifier(PICTURE_WIDTH));
    paramNames.add(new Identifier(PICTURE_HEIGHT));
    paramNames.add(new Identifier(PICTURE_TYPE));

    registration.registerParameters(paramNames, defs);
  }

  /**
   * creates an AbsoluteDate object, which contains the current time in UTC
   *
   * @return AbsoluteDate with current time in UTC
   */
  public static AbsoluteDate getNow()
  {
    Instant instant = Instant.now();
    TimeScale utc = TimeScalesFactory.getUTC();
    LocalDateTime time = LocalDateTime.ofInstant(instant, ZoneId.of("UTC"));

    return new AbsoluteDate(time.getYear(), time.getMonthValue(), time.getDayOfMonth(),
        time.getHour(), time.getMinute(), time.getSecond(), utc);
  }

  double getAttitudeSaftyMarginSeconds()
  {
    return this.attitudeSaftyMarginMS / 1000;
  }
}
