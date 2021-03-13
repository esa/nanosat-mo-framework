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
package esa.mo.platform.impl.provider.softsim;

import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.platform.powercontrol.structures.Device;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceList;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;

/**
 *
 * @author Cesar Coelho
 */
public class PowerControlSoftSimAdapter implements PowerControlAdapterInterface
{

  enum SimPayloadDevice
  {
    FineADCS,
    SEPP1,
    SEPP2,
    SBandTRX,
    XBandTRX,
    SDR,
    OpticalRX,
    HDCamera,
  }
  private final List<Device> devices;
  private final Map<Identifier, Device> deviceByName;
  private final Map<Long, Device> deviceByObjInstId;
  private final Map<Long, SimPayloadDevice> payloadIdByObjInstId;
  private static final Logger LOGGER = Logger.getLogger(PowerControlSoftSimAdapter.class.getName());
  private final ESASimulator instrumentsSimulator;

  public PowerControlSoftSimAdapter(ESASimulator instrumentsSimulator)
  {
    LOGGER.log(Level.INFO, "Initialisation");
    this.instrumentsSimulator = instrumentsSimulator;
    devices = new ArrayList<>();
    deviceByName = new HashMap<>();
    deviceByObjInstId = new HashMap<>();
    payloadIdByObjInstId = new HashMap<>();
    initDevices();
  }

  private void initDevices()
  {
    addDevice(new Device(true, 0L, new Identifier(
        "Attitude Determination and Control System"), DeviceType.ADCS), SimPayloadDevice.FineADCS);
    addDevice(new Device(true, 10L, new Identifier(
        "Satellite Experimental Processing Platform 1"), DeviceType.OBC), SimPayloadDevice.SEPP1);
    addDevice(new Device(true, 11L, new Identifier(
        "Satellite Experimental Processing Platform 2"), DeviceType.OBC), SimPayloadDevice.SEPP2);
    addDevice(new Device(false, 2L, new Identifier("S-Band Transceiver"), DeviceType.SBAND),
        SimPayloadDevice.SBandTRX);
    addDevice(new Device(false, 3L, new Identifier("X-Band Transmitter"), DeviceType.XBAND),
        SimPayloadDevice.XBandTRX);
    addDevice(new Device(false, 4L, new Identifier("Software Defined Radio"),
        DeviceType.SDR), SimPayloadDevice.SDR);
    addDevice(new Device(false, 5L, new Identifier("Optical Receiver"), DeviceType.OPTRX),
        SimPayloadDevice.OpticalRX);
    addDevice(new Device(false, 6L, new Identifier("HD Camera"), DeviceType.CAMERA),
        SimPayloadDevice.HDCamera);
  }

  private void addDevice(Device device, SimPayloadDevice payloadId)
  {
    devices.add(device);
    deviceByName.put(device.getName(), device);
    deviceByObjInstId.put(device.getUnitObjInstId(), device);
    payloadIdByObjInstId.put(device.getUnitObjInstId(), payloadId);
  }

  @Override
  public Map<Identifier, Device> getDeviceMap()
  {
    return Collections.unmodifiableMap(deviceByName);
  }

  private Device findByType(DeviceType type)
  {
    for (Device device : devices) {
      if (device.getDeviceType() == type) {
        return device;
      }
    }
    return null;
  }

  @Override
  public void enableDevices(DeviceList inputList) throws IOException
  {
    for (Device device : inputList) {
      LOGGER.log(Level.INFO, "Looking up Device {0}", new Object[]{device});
      SimPayloadDevice payloadId = payloadIdByObjInstId.get(device.getUnitObjInstId());
      if (device.getUnitObjInstId() != null) {
        payloadId = payloadIdByObjInstId.get(device.getUnitObjInstId());
      } else {
        Device found = findByType(device.getDeviceType());
        if (found != null) {
          payloadId = payloadIdByObjInstId.get(found.getUnitObjInstId());
        } else {
          throw new IOException("Cannot find the device.");
        }
      }
      if (payloadId != null) {
        switchDevice(payloadId, device.getEnabled());
      } else {
        throw new IOException("Cannot find the payload id.");
      }
    }
  }

  private void switchDevice(SimPayloadDevice device, Boolean enabled) throws IOException
  {
    LOGGER.log(Level.INFO, "Switching device {0} to enabled: {1}", new Object[]{device, enabled});
    // TODO interact with simulator core and track the device status locally
  }
}
