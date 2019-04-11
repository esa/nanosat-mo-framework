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
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
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
import org.ccsds.moims.mo.platform.softwaredefinedradio.consumer.SoftwareDefinedRadioAdapter;
import org.ccsds.moims.mo.platform.softwaredefinedradio.structures.SDRConfiguration;

/**
 *
 * @author dmars
 */
public class PayloadsTestActionsHandler
{

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

  private static final float SDR_SAMPLING_FREQUENCY = (float) 1.5;
  private static final float SDR_LPF_BW = (float) 0.75;
  private static final int SDR_RX_GAIN = 10;
  private static final float SDR_CENTER_FREQUENCY = (float) 443.0;
  private static final Duration SDR_REPORTING_INTERVAL = new Duration(0.2);
  private static final int SDR_RECORDING_DURATION = 2000;

  private boolean sdrRegistered = false;

  private PayloadsTestMCAdapter payloadsTestMCAdapter;

  public PayloadsTestActionsHandler(PayloadsTestMCAdapter payloadsTestMCAdapter)
  {
    this.payloadsTestMCAdapter = payloadsTestMCAdapter;
  }

  private UInteger executeAdcsModeAction(Duration duration,
      AttitudeMode attitudeMode, PayloadsTestMCAdapter payloadsTestMCAdapter)
  {
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
      PayloadsTestMCAdapter payloadsTestMCAdapter)
  {
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

  UInteger handleActionArrived(Identifier name,
      AttributeValueList attributeValues, Long actionInstanceObjId)
  {
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
          return executeAdcsModeAction(
              (Duration) attributeValues.get(0).getValue(),
              new AttitudeModeSunPointing(), payloadsTestMCAdapter);
        case PayloadsTestActionsHandler.ACTION_NADIR_POINTING_MODE:
          return executeAdcsModeAction(
              (Duration) attributeValues.get(0).getValue(),
              new AttitudeModeNadirPointing(), payloadsTestMCAdapter);
        case PayloadsTestActionsHandler.ACTION_UNSET_ATTITUDE:
          return executeAdcsModeAction(null, null,
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
                new Duration(5), new PayloadsTestOpticalDataHandler());
            return null; // Success!
          } catch (MALInteractionException | MALException | IOException | NMFException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return new UInteger(1);
          }
        case PayloadsTestActionsHandler.ACTION_RECORD_SDR:
          try {
            if (!sdrRegistered) {
              payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().
                  streamRadioRegister(ConnectionConsumer.subscriptionWildcardRandom(),
                      new PayloadsTestSDRDataHandler());
              sdrRegistered = true;
            }
            SDRConfiguration config = new SDRConfiguration(SDR_CENTER_FREQUENCY, SDR_RX_GAIN,
                SDR_LPF_BW, SDR_SAMPLING_FREQUENCY);
            payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().enableSDR(
                true, config, SDR_REPORTING_INTERVAL);
            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
              @Override
              public void run()
              {
                try {
                  payloadsTestMCAdapter.nmf.getPlatformServices().getSoftwareDefinedRadioService().enableSDR(
                      false, config, SDR_REPORTING_INTERVAL);
                } catch (MALInteractionException | MALException | IOException | NMFException ex) {
                  LOGGER.log(Level.SEVERE, "Failed to stop the SDR", ex);
                }
              }
            }, SDR_RECORDING_DURATION);
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

}
