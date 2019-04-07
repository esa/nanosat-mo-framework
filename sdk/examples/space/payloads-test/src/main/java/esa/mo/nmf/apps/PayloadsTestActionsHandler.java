/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esa.mo.nmf.apps;

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeMode;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeNadirPointing;
import org.ccsds.moims.mo.platform.autonomousadcs.structures.AttitudeModeSunPointing;
import org.ccsds.moims.mo.platform.camera.consumer.CameraAdapter;
import org.ccsds.moims.mo.platform.camera.structures.CameraSettings;
import org.ccsds.moims.mo.platform.camera.structures.PictureFormat;
import org.ccsds.moims.mo.platform.opticaldatareceiver.consumer.OpticalDataReceiverAdapter;
import org.ccsds.moims.mo.platform.powercontrol.structures.Device;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceList;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;

/**
 *
 * @author dmars
 */
public class PayloadsTestActionsHandler {

  private static final Logger LOGGER = Logger.getLogger(
          PayloadsTestActionsHandler.class.getName());
  private static final String ACTION_UNSET_ATTITUDE = "ADCS_UnsetAttitude";
  private static final String ACTION_NADIR_POINTING_MODE = "ADCS_NadirPointingMode";
  private static final String ACTION_SUN_POINTING_MODE = "ADCS_SunPointingMode";
  private static final String ACTION_POWER_ON_DEVICE = "PowerOnDevice";
  private static final String ACTION_RECORD_SDR = "RecordSDRData";
  private static final String ACTION_TAKE_PICTURE_RAW = "TakePicture.RAW";
  private static final String ACTION_TAKE_PICTURE_BMP = "TakePicture.BMP";
  private static final String ACTION_TAKE_PICTURE_JPG = "TakePicture.JPG";
  private static final String ACTION_POWER_OFF_DEVICE = "PowerOffDevice";
  private static final String ACTION_RECORD_OPTRX = "RecordOptRXData";
  public static final int TOTAL_STAGES = 3;

  static private UInteger executeAdcsModeAction(Duration duration,
          AttitudeMode attitudeMode, PayloadsTestMCAdapter payloadsTestMCAdapter) {
    if (duration != null) {
      // Negative Durations are not allowed!
      if (duration.getValue() < 0) {
        return new UInteger(1);
      }
      if (duration.getValue() == 0) {
        // Adhere to the ADCS Service interface
        duration = null;
      }
    }
    try {
      payloadsTestMCAdapter.nmf.getPlatformServices().getAutonomousADCSService().setDesiredAttitude(
              duration, attitudeMode);
    } catch (MALInteractionException | MALException | NMFException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(3);
    } catch (IOException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
      return new UInteger(4);
    }
    return null; // Success
  }

  static void registerActions(MCRegistration registration,
          PayloadsTestMCAdapter payloadsTestMCAdapter) {
    // ------------------ Actions ------------------
    ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
    IdentifierList actionNames = new IdentifierList();
    ArgumentDefinitionDetailsList argumentsSetAttitude = new ArgumentDefinitionDetailsList();
    {
      Byte rawType = Attribute._DURATION_TYPE_SHORT_FORM;
      String rawUnit = "seconds";
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;
      argumentsSetAttitude.add(new ArgumentDefinitionDetails(new Identifier(
              "modeHoldDuration"), null, rawType, rawUnit,
              conditionalConversions, convertedType, convertedUnit));
    }
    ActionDefinitionDetails actionDefSunPointing = new ActionDefinitionDetails(
            "Changes the spacecraft's attitude to sun pointing mode.",
            new UOctet((short) 0), new UShort(0), argumentsSetAttitude);
    actionNames.add(new Identifier(ACTION_SUN_POINTING_MODE));
    ActionDefinitionDetails actionDefNadirPointing = new ActionDefinitionDetails(
            "Changes the spacecraft's attitude to nadir pointing mode.",
            new UOctet((short) 0), new UShort(0), argumentsSetAttitude);
    actionNames.add(new Identifier(ACTION_NADIR_POINTING_MODE));
    ActionDefinitionDetails actionDefUnsetAttitude = new ActionDefinitionDetails(
            "Unsets the spacecraft's attitude.", new UOctet((short) 0),
            new UShort(0), null);
    actionNames.add(new Identifier(ACTION_UNSET_ATTITUDE));
    actionDefs.add(actionDefSunPointing);
    actionDefs.add(actionDefNadirPointing);
    actionDefs.add(actionDefUnsetAttitude);
    actionDefs.add(new ActionDefinitionDetails(
            "Uses the NMF Camera service to take a picture.", new UOctet(
                    (short) 0), new UShort(TOTAL_STAGES), null));
    actionNames.add(new Identifier(ACTION_TAKE_PICTURE_RAW));
    actionDefs.add(new ActionDefinitionDetails(
            "Uses the NMF Camera service to take a picture.", new UOctet(
                    (short) 0), new UShort(TOTAL_STAGES), null));
    actionNames.add(new Identifier(ACTION_TAKE_PICTURE_JPG));
    actionDefs.add(new ActionDefinitionDetails(
            "Uses the NMF Camera service to take a picture.", new UOctet(
                    (short) 0), new UShort(TOTAL_STAGES), null));
    actionNames.add(new Identifier(ACTION_TAKE_PICTURE_BMP));
    ArgumentDefinitionDetailsList argumentsPowerSwitch = new ArgumentDefinitionDetailsList();
    {
      Byte rawType = Attribute._INTEGER_TYPE_SHORT_FORM;
      String rawUnit = null;
      ConditionalConversionList conditionalConversions = null;
      Byte convertedType = null;
      String convertedUnit = null;
      argumentsPowerSwitch.add(new ArgumentDefinitionDetails(new Identifier(
              "DeviceType"), null, rawType, rawUnit, conditionalConversions,
              convertedType, convertedUnit));
    }
    actionDefs.add(new ActionDefinitionDetails(
            "Use NMF PowerControl to switch a device.", new UOctet((short) 0),
            new UShort(0), argumentsPowerSwitch));
    actionNames.add(new Identifier(ACTION_POWER_ON_DEVICE));
    actionDefs.add(new ActionDefinitionDetails(
            "Use NMF PowerControl to switch a device.", new UOctet((short) 0),
            new UShort(0), argumentsPowerSwitch));
    actionNames.add(new Identifier(ACTION_POWER_OFF_DEVICE));

    actionDefs.add(new ActionDefinitionDetails(
            "Record SDR samples.", new UOctet((short) 0),
            new UShort(0), null));
    actionNames.add(new Identifier(ACTION_RECORD_SDR));

    actionDefs.add(new ActionDefinitionDetails(
            "Record Optical RX samples.", new UOctet((short) 0),
            new UShort(0), null));
    actionNames.add(new Identifier(ACTION_RECORD_OPTRX));
    registration.registerActions(actionNames, actionDefs);
  }

  static UInteger handleActionArrived(Identifier name,
          AttributeValueList attributeValues, Long actionInstanceObjId,
          PayloadsTestMCAdapter payloadsTestMCAdapter) {
    if (payloadsTestMCAdapter.nmf == null) {
      return new UInteger(0);
    }
    LOGGER.log(Level.INFO, "Action {0} with parameters '{'{1}'}' arrived.",
            new Object[]{name.toString(), attributeValues.stream().map(
              HelperAttributes::attribute2string).collect(Collectors.joining(
                      ", "))});
    // Action dispatcher
    if (null != name.getValue()) {
      switch (name.getValue()) {
        case PayloadsTestActionsHandler.ACTION_SUN_POINTING_MODE:
          return PayloadsTestActionsHandler.executeAdcsModeAction(
                  (Duration) attributeValues.get(0).getValue(),
                  new AttitudeModeSunPointing(), payloadsTestMCAdapter);
        case PayloadsTestActionsHandler.ACTION_NADIR_POINTING_MODE:
          return PayloadsTestActionsHandler.executeAdcsModeAction(
                  (Duration) attributeValues.get(0).getValue(),
                  new AttitudeModeNadirPointing(), payloadsTestMCAdapter);
        case PayloadsTestActionsHandler.ACTION_UNSET_ATTITUDE:
          return PayloadsTestActionsHandler.executeAdcsModeAction(null, null,
                  payloadsTestMCAdapter);
        case PayloadsTestActionsHandler.ACTION_TAKE_PICTURE_RAW:
          try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getCameraService().takePicture(
                    new CameraSettings(
                            payloadsTestMCAdapter.defaultCameraResolution,
                            PictureFormat.RAW,
                            payloadsTestMCAdapter.cameraExposureTime,
                            payloadsTestMCAdapter.cameraGainR,
                            payloadsTestMCAdapter.cameraGainG,
                            payloadsTestMCAdapter.cameraGainB),
                    new PayloadsTestCameraDataHandler(actionInstanceObjId,
                            payloadsTestMCAdapter));
            return null; // Success!
          } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
          }
        case PayloadsTestActionsHandler.ACTION_TAKE_PICTURE_JPG:
          try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getCameraService().takePicture(
                    new CameraSettings(
                            payloadsTestMCAdapter.defaultCameraResolution,
                            PictureFormat.JPG,
                            payloadsTestMCAdapter.cameraExposureTime,
                            payloadsTestMCAdapter.cameraGainR,
                            payloadsTestMCAdapter.cameraGainG,
                            payloadsTestMCAdapter.cameraGainB),
                    new PayloadsTestCameraDataHandler(actionInstanceObjId,
                            payloadsTestMCAdapter));
            return null; // Success!
          } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
          }
        case PayloadsTestActionsHandler.ACTION_TAKE_PICTURE_BMP:
          try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getCameraService().takePicture(
                    new CameraSettings(
                            payloadsTestMCAdapter.defaultCameraResolution,
                            PictureFormat.BMP,
                            payloadsTestMCAdapter.cameraExposureTime,
                            payloadsTestMCAdapter.cameraGainR,
                            payloadsTestMCAdapter.cameraGainG,
                            payloadsTestMCAdapter.cameraGainB),
                    new PayloadsTestCameraDataHandler(
                            actionInstanceObjId, payloadsTestMCAdapter));
            return null; // Success!
          } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
          }
        case PayloadsTestActionsHandler.ACTION_POWER_ON_DEVICE:
          try {
            DeviceList deviceList = new DeviceList();
            deviceList.add(new Device(true, null, null,
                    DeviceType.fromNumericValue(
                            (UInteger) attributeValues.get(0).getValue())));
            payloadsTestMCAdapter.nmf.getPlatformServices().getPowerControlService().enableDevices(
                    deviceList);
            return null; // Success!
          } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
          }
        case PayloadsTestActionsHandler.ACTION_POWER_OFF_DEVICE:
          try {
            DeviceList deviceList = new DeviceList();
            deviceList.add(new Device(false, null, null,
                    DeviceType.fromNumericValue(
                            (UInteger) attributeValues.get(0).getValue())));
            payloadsTestMCAdapter.nmf.getPlatformServices().getPowerControlService().enableDevices(
                    deviceList);
            return null; // Success!
          } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
          }
        case PayloadsTestActionsHandler.ACTION_RECORD_OPTRX:
          try {
            payloadsTestMCAdapter.nmf.getPlatformServices().getOpticalDataReceiverService().recordSamples(
                    new Duration(5), new OpticalDataReceiverAdapterImpl());
            return null; // Success!
          } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
          }
        default:
          break;
      }
    }
    return new UInteger(0); // error code 0 - unknown error
  }

  private static class OpticalDataReceiverAdapterImpl extends OpticalDataReceiverAdapter {

    public OpticalDataReceiverAdapterImpl() {
    }

    public void recordSamplesResponseReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
            org.ccsds.moims.mo.mal.structures.Blob data, java.util.Map qosProperties) {
    }
  }

}
