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

import java.io.IOException;
import java.util.Map;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.platform.structures.*;

/**
 * The PowerControlAdapterInterface is an interface to create adapters for the
 * Power Control service.
 */
public interface PowerControlAdapterInterface {

    /**
     * Returns the device map.
     * @return the device map
     */
    Map<Identifier, Device> getDeviceMap();

    /**
     * Enable devices.
     *
     * @param devices the devices
     * @throws IOException if the operation fails
     */
    void enableDevices(DeviceList devices) throws IOException;

    /**
     * Returns whether device enabled.
     *
     * @param deviceType the device type
     * @return {@code true} on success
     */
    boolean isDeviceEnabled(DeviceType deviceType);

    /**
     * Start status tracking.
     *
     * @param connection the NMF provider connector
     */
    void startStatusTracking(ConnectionConsumer connection);
}
