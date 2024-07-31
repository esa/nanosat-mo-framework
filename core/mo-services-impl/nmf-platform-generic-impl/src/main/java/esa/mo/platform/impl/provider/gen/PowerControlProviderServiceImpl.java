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
package esa.mo.platform.impl.provider.gen;

import esa.mo.helpertools.connections.ConnectionProvider;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.platform.powercontrol.PowerControlHelper;
import org.ccsds.moims.mo.platform.powercontrol.provider.PowerControlInheritanceSkeleton;
import org.ccsds.moims.mo.platform.powercontrol.structures.Device;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceList;

/**
 *
 * @author Cesar Coelho
 */
public class PowerControlProviderServiceImpl extends PowerControlInheritanceSkeleton {

    private MALProvider powerControlServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private PowerControlAdapterInterface adapter;
    private static final Logger LOGGER = Logger.getLogger(
            PowerControlProviderServiceImpl.class.getName());

    /**
     * Initializes the Power Control service.
     *
     * @param adapter The Power Control adapter
     * @throws MALException On initialisation error.
     */
    public synchronized void init(PowerControlAdapterInterface adapter) throws MALException {
        long timestamp = System.currentTimeMillis();
        
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME,
                    PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME,
                    PlatformHelper.PLATFORM_AREA_VERSION)
                    .getServiceByName(PowerControlHelper.POWERCONTROL_SERVICE_NAME) == null) {
                PowerControlHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
        }

        // Shut down old service transport
        if (null != powerControlServiceProvider) {
            connection.closeAll();
        }

        this.adapter = adapter;
        powerControlServiceProvider = connection.startService(
                PowerControlHelper.POWERCONTROL_SERVICE_NAME.toString(),
                PowerControlHelper.POWERCONTROL_SERVICE, this);

        running = true;
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Power Control service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != powerControlServiceProvider) {
                powerControlServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider", ex);
        }
    }

    @Override
    public DeviceList listDevices(IdentifierList names, MALInteraction interaction) 
            throws MALInteractionException, MALException {
        if (names == null) {
            throw new MALException("IdentifierList cannot be empty.");
        }
        final Map<Identifier, Device> devices = adapter.getDeviceMap();
        UIntegerList unkIndexList = new UIntegerList();
        DeviceList ret = new DeviceList();
        for (int index = 0; index < names.size(); index++) {
            Identifier name = names.get(index);
            if (name.toString().equals("*")) {
                ret.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                ret.addAll(devices.values()); // ... add all in a row
                ret.clear();
                break;
            }
            Device matchedDevice = devices.get(name);
            if (matchedDevice != null) {
                ret.add(matchedDevice);
            }
        }
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(
                    MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        return ret;
    }

    @Override
    public void enableDevices(DeviceList devices, MALInteraction interaction)
            throws MALInteractionException, MALException {
        try {
            adapter.enableDevices(devices);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "adapter.enableDevices failed", ex);
            throw new MALInteractionException(new MALStandardError(
                    PlatformHelper.DEVICE_NOT_AVAILABLE_ERROR_NUMBER, null));
        }
    }

}
