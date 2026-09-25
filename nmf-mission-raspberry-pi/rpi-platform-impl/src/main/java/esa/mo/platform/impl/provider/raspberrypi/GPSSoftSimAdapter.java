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
package esa.mo.platform.impl.provider.raspberrypi;

import esa.mo.platform.impl.provider.gen.GPSNMEAonlyAdapter;
import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import java.io.IOException;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.platform.structures.Position;
import org.ccsds.moims.mo.platform.structures.SatelliteInfoList;
import org.orekit.propagation.analytical.tle.TLE;

/**
 * A simple GPS Adapter for testing purposes.
 *
 * @author Cesar Coelho
 */
public class GPSSoftSimAdapter extends GPSNMEAonlyAdapter {

    private ESASimulator instrumentsSimulator = null;
    private PowerControlAdapterInterface pcAdapter = null;

    public GPSSoftSimAdapter() {

    }

    public GPSSoftSimAdapter(ESASimulator instrumentsSimulator, PowerControlAdapterInterface pcAdapter) {
        this.instrumentsSimulator = instrumentsSimulator;
        this.pcAdapter = pcAdapter;
    }

    @Override
    public synchronized String getNMEASentence(final String sentenceIdentifier) throws IOException {
        return "123 Test";
    }

    @Override
    public boolean isUnitAvailable() {
        return true;
    }

    @Override
    public Position getCurrentPosition() {
        return new Position((float) 1, (float) 2, (float) 3, null);
    }

    @Override
    public SatelliteInfoList getSatelliteInfoList() {
        return null;
    }

    public TLE getTLE() {
        TLE tle = this.instrumentsSimulator.getSimulatorNode().getTLE();
        return tle;
    }
}
