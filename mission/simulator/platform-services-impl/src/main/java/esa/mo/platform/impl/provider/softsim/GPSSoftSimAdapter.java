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

import java.io.IOException;

import esa.mo.platform.impl.provider.gen.GPSNMEAonlyAdapter;
import esa.mo.platform.impl.provider.gen.PowerControlAdapterInterface;
import opssat.simulator.main.ESASimulator;
import org.ccsds.moims.mo.platform.powercontrol.structures.DeviceType;
import org.orekit.propagation.analytical.tle.TLE;

/**
 *
 * @author Cesar Coelho
 */
public class GPSSoftSimAdapter extends GPSNMEAonlyAdapter implements SimulatorAdapter {

  private final ESASimulator instrumentsSimulator;
  private final PowerControlAdapterInterface pcAdapter;

    public GPSSoftSimAdapter(ESASimulator instrumentsSimulator, PowerControlAdapterInterface pcAdapter) {
        this.instrumentsSimulator = instrumentsSimulator;
        this.pcAdapter = pcAdapter;
    }

    @Override
    public synchronized String getNMEASentence(final String sentenceIdentifier) throws IOException {
        final String nmeaSentence = instrumentsSimulator.getpGPS().getNMEASentence(sentenceIdentifier);

        if (nmeaSentence == null) {
            throw new IOException("The Simulator returned a null object!");
        }

        return nmeaSentence;
    }

    @Override
    public synchronized String getBestXYZSentence() throws IOException {
        String sentence = instrumentsSimulator.getpGPS().getBestXYZSentence();

        if (sentence == null) {
            throw new IOException("The simulator returned a null object!");
        }

        return sentence;
    }

    @Override
    public synchronized String getTIMEASentence() throws IOException {
        String sentence = instrumentsSimulator.getpGPS().getTIMEASentence();

        if (sentence == null) {
            throw new IOException("The simulator returned a null object!");
        }

        return sentence;
    }

    @Override
    public boolean isUnitAvailable() {
        return pcAdapter.isDeviceEnabled(DeviceType.GNSS);
    }

  public TLE getTLE()
  {
    TLE tle = this.instrumentsSimulator.getSimulatorNode().getTLE();

        return tle;
    }

}
