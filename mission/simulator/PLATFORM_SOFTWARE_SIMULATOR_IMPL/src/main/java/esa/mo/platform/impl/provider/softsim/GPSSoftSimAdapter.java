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

import esa.mo.platform.impl.provider.gen.GPSNMEAonlyAdapter;
import java.io.IOException;
import opssat.simulator.main.ESASimulator;

/**
 *
 * @author Cesar Coelho
 */
public class GPSSoftSimAdapter extends GPSNMEAonlyAdapter {

    private final ESASimulator instrumentsSimulator;

    public GPSSoftSimAdapter(ESASimulator instrumentsSimulator) {
        this.instrumentsSimulator = instrumentsSimulator;
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
    public boolean isUnitAvailable() {
        return true;
    }

}
