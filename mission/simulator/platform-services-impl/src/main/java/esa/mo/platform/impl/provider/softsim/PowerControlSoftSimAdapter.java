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
package esa.mo.platform.impl.provider.softsim;

import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.platform.powercontrol.structures.Device;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceList;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;

/**
 *
 * @author Cesar Coelho
 */
public class PowerControlSoftSimAdapter implements PowerControlAdapterInterface
{

  private final ESASimulator instrumentsSimulator;
  private final List<Device> devices;
  private final Map<Identifier, Device> deviceByName;
  private final Map<Long, Device> deviceByObjInstId;

  public PowerControlSoftSimAdapter(ESASimulator instrumentsSimulator)
  {
    this.instrumentsSimulator = instrumentsSimulator;
    devices = new ArrayList<>();
    deviceByName = new HashMap<>();
    deviceByObjInstId = new HashMap<>();
  }

  private void initDevices()
  {
    addDevice(new Device(true, new Long(0), new Identifier("Attitude Determination and Control System"), DeviceType.ADCS));
    addDevice(new Device(true, new Long(1), new Identifier("Satellite Experimental Processing Platform"), DeviceType.OBC));
    addDevice(new Device(false, new Long(2), new Identifier("S-Band Transceiver"), DeviceType.SBAND));
    addDevice(new Device(false, new Long(3), new Identifier("X-Band Transmitter"), DeviceType.XBAND));
    addDevice(new Device(false, new Long(4), new Identifier("Software Defined Radio"), DeviceType.SDR));
    addDevice(new Device(false, new Long(5), new Identifier("Optical Receiver"), DeviceType.OPTRX));
    addDevice(new Device(false, new Long(6), new Identifier("HD Camera"), DeviceType.CAMERA));
  }

  private void addDevice(Device device)
  {
    devices.add(device);
    deviceByName.put(device.getName(), device);
    deviceByObjInstId.put(device.getUnitObjInstId(), device);
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
      Device found;
      if (device.getUnitObjInstId() != null) {
        found = deviceByObjInstId.get(device.getUnitObjInstId());
      } else {
        found = findByType(device.getDeviceType());
      }
      if (found != null) {
        switchDevice(found, device.getEnabled());
      } else {
        throw new IOException("Cannot find the device.");
      }
    }
  }

  private void switchDevice(Device found, Boolean enabled) throws IOException
  {
    // TODO interact with the simulator core
    found.setEnabled(enabled);
  }
}
