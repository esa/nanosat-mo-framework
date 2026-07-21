/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 * 
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *  
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.util;

/**
 *
 * @author Cezar Suteu
 */
public class SimulatorTimer {

    String name;
    long duration;
    long setDuration;

    public void timeElapsed(long timeElapsed) {
        if (duration < 0) {
            duration = setDuration;
        }
        duration -= timeElapsed;
    }

    public boolean isElapsed() {
        return duration < 0;
    }

    public SimulatorTimer(String name, long duration) {
        this.name = name;
        this.setDuration = duration;
    }

    public void setDuration(long duration) {
        this.setDuration = duration;
    }
}
