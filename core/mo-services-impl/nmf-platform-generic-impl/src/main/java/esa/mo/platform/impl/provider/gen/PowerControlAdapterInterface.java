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

import esa.mo.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.platform.powercontrol.structures.Device;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceList;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;

public interface PowerControlAdapterInterface {

    Map<Identifier, Device> getDeviceMap();

    void enableDevices(DeviceList devices) throws IOException;

    boolean isDeviceEnabled(DeviceType deviceType);

    void startStatusTracking(ConnectionConsumer connection);
}
