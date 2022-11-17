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

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.platform.powercontrol.structures.Device;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceList;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;

/**
 *
 * @author Cesar Coelho
 */
public class PowerControlSoftSimAdapter implements PowerControlAdapterInterface, SimulatorAdapter {

    enum SimPayloadDevice {
        FineADCS("Attitude Determination and Control System"), SEPP1("Satellite Experimental Processing Platform 1"),
        SEPP2("Satellite Experimental Processing Platform 2"), SBandTRX("S-Band Transceiver"), XBandTRX(
            "X-Band Transmitter"), SDR("Software Defined Radio"), OpticalRX("Optical Receiver"), HDCamera("HD Camera"),
        GPS("GPS");

        private Identifier identifier;

        SimPayloadDevice(String name) {
            identifier = new Identifier(name);
        }
    }

    private final ConcurrentHashMap<SimPayloadDevice, Device> deviceByType;
    private final Map<Long, SimPayloadDevice> payloadIdByObjInstId;
    private static final Logger LOGGER = Logger.getLogger(PowerControlSoftSimAdapter.class.getName());

    public PowerControlSoftSimAdapter() {
        LOGGER.log(Level.INFO, "Initialisation");
        deviceByType = new ConcurrentHashMap<>();
        payloadIdByObjInstId = new HashMap<>();
        initDevices();
    }

    private void initDevices() {
        addDevice(new Device(true, 0L, SimPayloadDevice.FineADCS.identifier, DeviceType.ADCS),
            SimPayloadDevice.FineADCS);
        addDevice(new Device(true, 10L, SimPayloadDevice.SEPP1.identifier, DeviceType.OBC), SimPayloadDevice.SEPP1);
        addDevice(new Device(true, 11L, SimPayloadDevice.SEPP2.identifier, DeviceType.OBC), SimPayloadDevice.SEPP2);
        addDevice(new Device(true, 2L, SimPayloadDevice.SBandTRX.identifier, DeviceType.SBAND),
            SimPayloadDevice.SBandTRX);
        addDevice(new Device(true, 3L, SimPayloadDevice.XBandTRX.identifier, DeviceType.XBAND),
            SimPayloadDevice.XBandTRX);
        addDevice(new Device(true, 4L, SimPayloadDevice.SDR.identifier, DeviceType.SDR), SimPayloadDevice.SDR);
        addDevice(new Device(true, 5L, SimPayloadDevice.OpticalRX.identifier, DeviceType.OPTRX),
            SimPayloadDevice.OpticalRX);
        addDevice(new Device(true, 6L, SimPayloadDevice.HDCamera.identifier, DeviceType.CAMERA),
            SimPayloadDevice.HDCamera);
        addDevice(new Device(true, 7L, SimPayloadDevice.GPS.identifier, DeviceType.GNSS), SimPayloadDevice.GPS);
    }

    private void addDevice(Device device, SimPayloadDevice payload) {
        deviceByType.put(payload, device);
        payloadIdByObjInstId.put(device.getUnitObjInstId(), payload);
    }

    @Override
    public Map<Identifier, Device> getDeviceMap() {
        Map<Identifier, Device> mapByName = new HashMap<>();
        deviceByType.forEach((k, device) -> mapByName.put(device.getName(), device));
        return mapByName;
    }

    private Device findByType(DeviceType type) {
        for (Device device : deviceByType.values()) {
            if (device.getDeviceType() == type) {
                return device;
            }
        }
        return null;
    }

    @Override
    public void enableDevices(DeviceList inputList) throws IOException {
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

    @Override
    public boolean isDeviceEnabled(DeviceType deviceType) {

        Device device = findByType(deviceType);
        return device == null ? false : device.getEnabled();
    }

    @Override
    public void startStatusTracking(ConnectionConsumer connection) {
        // In simulator we do nothing here. Otherwise subscribe to the status parameter.
    }

    private void switchDevice(SimPayloadDevice device, Boolean enabled) throws IOException {
        LOGGER.log(Level.INFO, "Switching device {0} to enabled: {1}", new Object[]{device, enabled});
        deviceByType.get(device).setEnabled(enabled);
    }

}
