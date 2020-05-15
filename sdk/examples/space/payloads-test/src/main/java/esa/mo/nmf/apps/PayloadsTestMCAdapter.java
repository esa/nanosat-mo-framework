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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.annotations.Action;
import esa.mo.nmf.annotations.ActionParameter;
import esa.mo.nmf.annotations.Aggregation;
import esa.mo.nmf.annotations.Parameter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Pair;
import org.ccsds.moims.mo.mal.structures.PairList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetails;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterConversion;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversion;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;
import org.ccsds.moims.mo.platform.autonomousadcs.consumer.AutonomousADCSAdapter;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.*;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.camera.structures.PixelResolution;
import org.ccsds.moims.mo.platform.gps.consumer.GPSAdapter;
import org.ccsds.moims.mo.platform.gps.structures.SatelliteInfoList;
import org.ccsds.moims.mo.platform.structures.VectorF3D;

/**
 * The adapter for the NMF App
 */
//add aggregations:
@Aggregation(
    id = "Magnetometer_Aggregation",
    description = "Aggregates Magnetometer components: X, Y, Z.",
    reportInterval = 10,
    sendUnchanged = true,
    sampleInterval = 3)
@Aggregation(
    id = "GPS_Aggregation",
    description = "Aggregates: GPS Latitude, GPS Longitude, GPS Altitude.",
    reportInterval = 10,
    sendUnchanged = true,
    sampleInterval = 3)
public class PayloadsTestMCAdapter extends MonitorAndControlNMFAdapter
{
  private static final String AGGREGATION_GPS = "GPS_Aggregation";
  private static final String AGGREGATION_MAG = "Magnetometer_Aggregation";
  private static final String PARAMETER_ADCS_MODE = "ADCS_ModeOperation";
  private static final String PARAMETER_ADCS_DURATION = "ADCS_RemainingControlDuration";
  private static final String PARAMETER_ANGULAR_VELOCITY_X = "AngularVelocity_X";
  private static final String PARAMETER_ANGULAR_VELOCITY_Y = "AngularVelocity_Y";
  private static final String PARAMETER_ANGULAR_VELOCITY_Z = "AngularVelocity_Z";
  private static final String PARAMETER_ATTITUDE_Q_A = "AttitudeQuaternion_a";
  private static final String PARAMETER_ATTITUDE_Q_B = "AttitudeQuaternion_b";
  private static final String PARAMETER_ATTITUDE_Q_C = "AttitudeQuaternion_c";
  private static final String PARAMETER_ATTITUDE_Q_D = "AttitudeQuaternion_d";
  private static final String PARAMETER_MAG_X = "MagneticField_X";
  private static final String PARAMETER_MAG_Y = "MagneticField_Y";
  private static final String PARAMETER_MAG_Z = "MagneticField_Z";
  private static final String PARAMETER_MTQ_X = "MagnetorquerMoment_X";
  private static final String PARAMETER_MTQ_Y = "MagnetorquerMoment_Y";
  private static final String PARAMETER_MTQ_Z = "MagnetorquerMoment_Z";
  private static final String PARAMETER_SUN_VECTOR_X = "SunVector_X";
  private static final String PARAMETER_SUN_VECTOR_Y = "SunVector_Y";
  private static final String PARAMETER_SUN_VECTOR_Z = "SunVector_Z";

  private static final Duration ATTITUDE_MONITORING_INTERVAL = new Duration(1.0);
  private static final Logger LOGGER = Logger.getLogger(PayloadsTestMCAdapter.class.getName());

  private static final float DEFAULT_CAMERA_GAIN = 1.0f;
  private static final float DEFAULT_CAMERA_EXPOSURE_TIME = 0.1f;
  public final NMFInterface nmf;

  public final AtomicInteger picturesTaken = new AtomicInteger(0);

  private final int defaultPictureWidth = 2048;
  private final int defaultPictureHeight = 1944;
  public final PixelResolution defaultCameraResolution;

  private final PayloadsTestActionsHandler actionsHandler;


  //----------------------------------- Camera Parameters -----------------------------------------
  @Parameter(
      description = "The number of pictures taken",
      generationEnabled = false,
      onGetFunction = "onGetPicturesTaken",
      readOnly = true,
      reportIntervalSeconds = 10)
  Integer NumberOfPicturesTaken = 0;

  @Parameter(
      description = "Camera red channel gain",
      generationEnabled = false,
      reportIntervalSeconds = 10)
  public float cameraGainR = DEFAULT_CAMERA_GAIN;

  @Parameter(
      description = "Camera green channel gain",
      generationEnabled = false,
      reportIntervalSeconds = 10)
  public float cameraGainG = DEFAULT_CAMERA_GAIN;

  @Parameter(
      description = "Camera blue channel gain",
      generationEnabled = false,
      reportIntervalSeconds = 10)
  public float cameraGainB = DEFAULT_CAMERA_GAIN;

  @Parameter(
      description = "Camera exposure time",
      generationEnabled = false,
      reportIntervalSeconds = 10)
  public float cameraExposureTime = DEFAULT_CAMERA_EXPOSURE_TIME;

  //-----------------------------------------------------------------------------------------------
  //-------------------------------------- GPS Parameters -----------------------------------------
  @Parameter(
      description = "The number of satellites in view of GPS receiver.",
      rawUnit = "sats",
      generationEnabled = false,
      onGetFunction = "onGPSSatsInView",
      readOnly = true)
  Integer GPS_NumberOfSatellitesInView = 0;

  @Parameter(
      description = "The GPS Latitude",
      rawUnit = "degrees",
      generationEnabled = false,
      onGetFunction = "onGetLatitude",
      readOnly = true,
      reportIntervalSeconds = 2,
      aggregations = {AGGREGATION_GPS})
  Float GPS_Latitude = 0.0f;

  @Parameter(
      description = "The GPS Longitude",
      rawUnit = "degrees",
      generationEnabled = false,
      onGetFunction = "onGetLongitude",
      readOnly = true,
      reportIntervalSeconds = 2,
      aggregations = {AGGREGATION_GPS})
  Float GPS_Longitude = 0.0f;

  @Parameter(
      description = "The GPS Altitude",
      rawUnit = "meters",
      generationEnabled = false,
      onGetFunction = "onGetAltitude",
      readOnly = true,
      reportIntervalSeconds = 2,
      aggregations = {AGGREGATION_GPS})
  Float GPS_Altitude = 0.0f;

  @Parameter(
      description = "The GPS elapsed Time",
      rawUnit = "seconds",
      generationEnabled = false,
      onGetFunction = "onGetGPSElapsedTime",
      readOnly = true)
  Duration GPS_ElapsedTime = new Duration();

  //-----------------------------------------------------------------------------------------------
  //------------------------------------ Magnetic Field Parameters---------------------------------
  @Parameter(
      description = "The Magnetometer X component",
      rawUnit = "microTesla",
      generationEnabled = false,
      onGetFunction = "onGetMagneticField_X",
      readOnly = true,
      reportIntervalSeconds = 2,
      aggregations = {AGGREGATION_MAG})
  Float MagneticField_X = 0.0f;

  @Parameter(
      description = "The Magnetometer Y component",
      rawUnit = "microTesla",
      generationEnabled = false,
      onGetFunction = "onGetMagneticField_Y",
      readOnly = true,
      reportIntervalSeconds = 2,
      aggregations = {AGGREGATION_MAG})
  Float MagneticField_Y = 0.0f;

  @Parameter(
      description = "The Magnetometer Z component",
      rawUnit = "microTesla",
      generationEnabled = false,
      onGetFunction = "onGetMagneticField_Z",
      readOnly = true,
      reportIntervalSeconds = 2,
      aggregations = {AGGREGATION_MAG})
  Float MagneticField_Z = 0.0f;

  //-----------------------------------------------------------------------------------------------
  public PayloadsTestMCAdapter(final NMFInterface nmfProvider)
  {
    this.defaultCameraResolution =
        new PixelResolution(new UInteger(defaultPictureWidth), new UInteger(
            defaultPictureHeight));
    actionsHandler = new PayloadsTestActionsHandler(this);
    this.nmf = nmfProvider;
  }

  private static enum AttitudeModeEnum
  {
    IDLE, BDOT, SUNPOINTING, SINGLESPINNING, TARGETTRACKING, NADIRPOINTING
  }

  private UOctet attitudeModeToParamValue(AttitudeMode attitude)
  {
    AttitudeModeEnum modeEnum;
    if (attitude == null) {
      modeEnum = AttitudeModeEnum.IDLE;
    } else if (attitude instanceof AttitudeModeBDot) {
      modeEnum = AttitudeModeEnum.BDOT;
    } else if (attitude instanceof AttitudeModeSunPointing) {
      modeEnum = AttitudeModeEnum.SUNPOINTING;
    } else if (attitude instanceof AttitudeModeSingleSpinning) {
      modeEnum = AttitudeModeEnum.SINGLESPINNING;
    } else if (attitude instanceof AttitudeModeTargetTracking) {
      modeEnum = AttitudeModeEnum.TARGETTRACKING;
    } else if (attitude instanceof AttitudeModeNadirPointing) {
      modeEnum = AttitudeModeEnum.NADIRPOINTING;
    } else {
      throw new IllegalArgumentException("Unrecognized attitude mode type!");
    }
    return new UOctet((short) modeEnum.ordinal());
  }

  @Override
  public void initialRegistrations(MCRegistration registration)
  {
    // call super for annotations to work!
    super.initialRegistrations(registration);

    // conversions are not supported by annotatinos yet, so these have to be done the old way
    registerParameters(registration);
  }

  private void registerParameters(MCRegistration registration) throws IllegalArgumentException
  {
    ParameterConversion paramConversion = registerAdcsModeConversion(registration);

    // ------------------ Parameters ------------------
    ParameterDefinitionDetailsList defsOther = new ParameterDefinitionDetailsList();
    IdentifierList paramOtherNames = new IdentifierList();

    defsOther.add(new ParameterDefinitionDetails(
        "The ADCS mode of operation",
        Union.UOCTET_SHORT_FORM.byteValue(),
        "",
        false,
        new Duration(0),
        null,
        paramConversion
    ));
    paramOtherNames.add(new Identifier(PARAMETER_ADCS_MODE));

    registration.registerParameters(paramOtherNames, defsOther);
  }

  private ParameterConversion registerAdcsModeConversion(MCRegistration registration) throws
      IllegalArgumentException
  {
    PairList mappings = new PairList();
    mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.IDLE.ordinal()), new Union("IDLE")));
    mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.BDOT.ordinal()), new Union("BDOT")));
    mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.SUNPOINTING.ordinal()), new Union(
        "SUNPOINTING")));
    mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.SINGLESPINNING.ordinal()), new Union(
        "SINGLESPINNING")));
    mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.TARGETTRACKING.ordinal()), new Union(
        "TARGETTRACKING")));
    mappings.add(new Pair(new UOctet((short) AttitudeModeEnum.NADIRPOINTING.ordinal()), new Union(
        "NADIRPOINTING")));
    DiscreteConversionDetailsList conversions = new DiscreteConversionDetailsList();
    conversions.add(new DiscreteConversionDetails(mappings));
    ParameterConversion paramConversion = null;
    try {
      ObjectIdList objIds = registration.registerConversions(conversions);

      if (objIds.size() == 1) {
        ObjectId objId = objIds.get(0);
        ParameterExpression paramExpr = null;

        ConditionalConversion condition = new ConditionalConversion(paramExpr, objId.getKey());
        ConditionalConversionList conditionalConversions = new ConditionalConversionList();
        conditionalConversions.add(condition);

        Byte convertedType = Attribute.STRING_TYPE_SHORT_FORM.byteValue();
        String convertedUnit = "n/a";

        paramConversion = new ParameterConversion(convertedType, convertedUnit,
            conditionalConversions);
      }
    } catch (NMFException | MALException | MALInteractionException ex) {
      LOGGER.log(Level.SEVERE,
          "Failed to register conversion.", ex);
    }
    return paramConversion;
  }

//-------------------------------------- onGet Functions----------------------------------------
  public void onGPSSatsInView()
  {
    final Semaphore sem = new Semaphore(0);
    class AdapterImpl extends GPSAdapter
    {

      @Override
      public void getSatellitesInfoResponseReceived(MALMessageHeader msgHeader,
          SatelliteInfoList gpsSatellitesInfo, java.util.Map qosProperties)
      {
        GPS_NumberOfSatellitesInView = gpsSatellitesInfo.size();
        sem.release();
      }
    }

    try {
      nmf.getPlatformServices().getGPSService().getSatellitesInfo(new AdapterImpl());
    } catch (IOException | MALInteractionException | MALException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      GPS_NumberOfSatellitesInView = null;
    }

    try {
      sem.acquire();
    } catch (InterruptedException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  public void onGetLatitude()
  {
    try {
      GPS_Latitude =
          nmf.getPlatformServices().getGPSService().getLastKnownPosition().getBodyElement0().getLatitude();
    } catch (NMFException | IOException | MALInteractionException | MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      GPS_Latitude = null;
    }
  }

  public void onGetLongitude()
  {
    try {
      GPS_Longitude =
          nmf.getPlatformServices().getGPSService().getLastKnownPosition().getBodyElement0().getLongitude();
    } catch (NMFException | IOException | MALInteractionException | MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      GPS_Longitude = null;
    }
  }

  public void onGetAltitude()
  {
    try {
      GPS_Altitude =
          nmf.getPlatformServices().getGPSService().getLastKnownPosition().getBodyElement0().getAltitude();
    } catch (NMFException | IOException | MALInteractionException | MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      GPS_Altitude = null;
    }
  }


  public void onGetGPSElapsedTime()
  {
    try {
      GPS_ElapsedTime =
          nmf.getPlatformServices().getGPSService().getLastKnownPosition().getBodyElement1();
    } catch (NMFException | IOException | MALInteractionException | MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      GPS_ElapsedTime = null;
    }
  }

  public void onGetMagneticField_X()
  {
    try {
      MagneticField_X =
          nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0().getMagneticField().getX();
    } catch (NMFException | IOException | MALInteractionException | MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      MagneticField_X = null;
    }
  }

  public void onGetMagneticField_Y()
  {
    try {
      MagneticField_Y =
          nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0().getMagneticField().getY();
    } catch (NMFException | IOException | MALInteractionException | MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      MagneticField_Y = null;
    }
  }

  public void onGetMagneticField_Z()
  {
    try {
      MagneticField_Z =
          nmf.getPlatformServices().getAutonomousADCSService().getStatus().getBodyElement0().getMagneticField().getZ();
    } catch (NMFException | IOException | MALInteractionException | MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      MagneticField_Z = null;
    }
  }

  public void onGetPicturesTaken()
  {
    NumberOfPicturesTaken = picturesTaken.get();
  }

  //-----------------------------------------------------------------------------------------------
  //----------------------------------- Actions ---------------------------------------------------

  @Action(description = "Changes the spacecraft's attitude to sun pointing mode.")
  public UInteger adcs_SunPointingMode(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction,
      @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration)
  {
    return actionsHandler.executeAdcsModeAction(holdDuration,
        new AttitudeModeSunPointing(), this);
  }

  @Action(description = "Changes the spacecraft's attitude to nadir pointing mode.")
  public UInteger adcs_NadirPointingMode(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction,
      @ActionParameter(name = "Hold Duration", rawUnit = "seconds") Duration holdDuration)
  {
    return actionsHandler.executeAdcsModeAction((Duration) holdDuration,
        new AttitudeModeNadirPointing(), this);
  }

  @Action(description = "Unsets the spacecraft's attitude.")
  public UInteger adcs_UnsetAttitude(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    return actionsHandler.executeAdcsModeAction(null, null, this);
  }

  @Action(description = "Uses the NMF Camera service to take a picture in RAW format.",
      stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
  public UInteger takePicture_RAW(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    return actionsHandler
        .takePicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.RAW);
  }

  @Action(description = "Uses the NMF Camera service to take a picture in JPG format.",
      stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
  public UInteger takePicture_JPG(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    return actionsHandler
        .takePicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.JPG);
  }

  @Action(description = "Uses the NMF Camera service to take a picture in BMP format.",
      stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
  public UInteger takePicture_BMP(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    return actionsHandler
        .takePicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.BMP);
  }

  @Action(description = "Uses the NMF Camera service to take a auto exposed picture in RAW format.",
      stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
  public UInteger takeAutoExposedPicture_RAW(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    return actionsHandler
        .takeAutoExposedPicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.RAW);
  }

  @Action(description = "Uses the NMF Camera service to take a auto exposed picture in JPG format.",
      stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
  public UInteger takeAutoExposedPicture_JPG(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    return actionsHandler
        .takeAutoExposedPicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.JPG);
  }


  @Action(description = "Uses the NMF Camera service to take a auto exposed picture in BMP format.",
      stepCount = PayloadsTestActionsHandler.TOTAL_STAGES)
  public UInteger takeAutoExposedPicture_BMP(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    return actionsHandler
        .takeAutoExposedPicture(actionInstanceObjId, reportProgress, interaction, PictureFormat.BMP);
  }

  @Action(description = "Use NMF PowerControl to switch a device On.")
  public UInteger powerOnDevice(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction,
      @ActionParameter(name = "DeviceType") UInteger deviceType)
  {
    return actionsHandler
        .setDeviceState(actionInstanceObjId, reportProgress, interaction, deviceType, true);
  }

  @Action(description = "Use NMF PowerControl to switch a device Off.")
  public UInteger powerOffDevice(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction,
      @ActionParameter(name = "DeviceType") UInteger deviceType)
  {
    return actionsHandler
        .setDeviceState(actionInstanceObjId, reportProgress, interaction, deviceType, false);
  }

  @Action(description = "Record Optical RX samples.")
  public UInteger recordOptRXData(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    try {
      nmf.getPlatformServices().getOpticalDataReceiverService().recordSamples(
          new Duration(5), new PayloadsTestOpticalDataHandler());
      return null; // Success!
    } catch (MALInteractionException | MALException | IOException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(1);
    }
  }

  @Action(description = "Record SDR samples.")
  public UInteger recordSDRData(
      Long actionInstanceObjId,
      boolean reportProgress,
      MALInteraction interaction)
  {
    return actionsHandler.recordSDRData(actionInstanceObjId, reportProgress, interaction);
  }

  //-----------------------------------------------------------------------------------------------

  public void startAdcsAttitudeMonitoring()
  {
    try {
      // Subscribe monitorAttitude
      nmf.getPlatformServices().getAutonomousADCSService().monitorAttitudeRegister(
          ConnectionConsumer.subscriptionWildcard(),
          new ADCSDataHandler()
      );
      nmf.getPlatformServices().getAutonomousADCSService().enableMonitoring(true,
          ATTITUDE_MONITORING_INTERVAL);
    } catch (IOException | MALInteractionException | MALException | NMFException ex) {
      LOGGER.log(Level.SEVERE, "Error when setting up attitude monitoring.", ex);
    }
  }

  public class ADCSDataHandler extends AutonomousADCSAdapter
  {
    @Override
    public void monitorAttitudeNotifyReceived(
        final MALMessageHeader msgHeader,
        final Identifier lIdentifier, final UpdateHeaderList lUpdateHeaderList,
        org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeTelemetryList attitudeTelemetryList,
        org.ccsds.moims.mo.platform.autonomousadcs.structures.ActuatorsTelemetryList actuatorsTelemetryList,
        org.ccsds.moims.mo.mal.structures.DurationList controlDurationList,
        org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeList attitudeModeList,
        final Map qosp)
    {
      LOGGER.log(Level.FINE, "Received monitorAttitude notify");
      for (AttitudeTelemetry attitudeTm : attitudeTelemetryList) {
        try {
          VectorF3D sunVector = attitudeTm.getSunVector();
          VectorF3D magneticField = attitudeTm.getMagneticField();
          VectorF3D angularVelocity = attitudeTm.getAngularVelocity();
          Quaternion attitude = attitudeTm.getAttitude();
          //attMode = AttitudeMode.NADIRPOINTING;
          nmf.pushParameterValue(PARAMETER_SUN_VECTOR_X, sunVector.getX());
          nmf.pushParameterValue(PARAMETER_SUN_VECTOR_Y, sunVector.getY());
          nmf.pushParameterValue(PARAMETER_SUN_VECTOR_Z, sunVector.getZ());

          nmf.pushParameterValue(PARAMETER_MAG_X, magneticField.getX());
          nmf.pushParameterValue(PARAMETER_MAG_Y, magneticField.getY());
          nmf.pushParameterValue(PARAMETER_MAG_Z, magneticField.getZ());

          nmf.pushParameterValue(PARAMETER_ANGULAR_VELOCITY_X, angularVelocity.getX());
          nmf.pushParameterValue(PARAMETER_ANGULAR_VELOCITY_Z, angularVelocity.getY());
          nmf.pushParameterValue(PARAMETER_ANGULAR_VELOCITY_Y, angularVelocity.getZ());

          nmf.pushParameterValue(PARAMETER_ATTITUDE_Q_A, attitude.getA());
          nmf.pushParameterValue(PARAMETER_ATTITUDE_Q_B, attitude.getB());
          nmf.pushParameterValue(PARAMETER_ATTITUDE_Q_C, attitude.getC());
          nmf.pushParameterValue(PARAMETER_ATTITUDE_Q_D, attitude.getD());
        } catch (NMFException ex) {
          LOGGER.log(Level.SEVERE, "Error when propagating Sensors TM", ex);
        }
      }
      for (ActuatorsTelemetry actuatorsTm : actuatorsTelemetryList) {
        try {
          VectorF3D mtqDipoleMoment = actuatorsTm.getMtqDipoleMoment();
          nmf.pushParameterValue(PARAMETER_MTQ_X, mtqDipoleMoment.getX());
          nmf.pushParameterValue(PARAMETER_MTQ_Y, mtqDipoleMoment.getY());
          nmf.pushParameterValue(PARAMETER_MTQ_Z, mtqDipoleMoment.getZ());
        } catch (NMFException ex) {
          LOGGER.log(Level.SEVERE, "Error when propagating Actuators TM", ex);
        }
      }
      for (Object activeAttitudeMode : attitudeModeList) {
        try {
          nmf.pushParameterValue(PARAMETER_ADCS_MODE, attitudeModeToParamValue(
              (AttitudeMode) activeAttitudeMode));
        } catch (NMFException ex) {
          LOGGER.log(Level.SEVERE, "Error when propagating active ADCS mode", ex);
        }
      }
      for (Duration remainingDuration : controlDurationList) {
        try {
          if (remainingDuration != null) {
            nmf.pushParameterValue(PARAMETER_ADCS_DURATION, remainingDuration);
          } else {
            nmf.pushParameterValue(PARAMETER_ADCS_DURATION, new Duration(0));
          }
        } catch (NMFException ex) {
          LOGGER.log(Level.SEVERE, "Error when propagating active ADCS mode duration", ex);
        }
      }
    }
  }
}
