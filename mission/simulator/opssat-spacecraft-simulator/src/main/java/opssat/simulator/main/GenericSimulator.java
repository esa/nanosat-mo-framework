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
package opssat.simulator.main;

import opssat.simulator.peripherals.PCamera;
import opssat.simulator.peripherals.PGPS;
import opssat.simulator.peripherals.PFineADCS;
import opssat.simulator.peripherals.POpticalReceiver;
import opssat.simulator.peripherals.PSDR;

/**
 *
 * @author Cezar Suteu
 */
public abstract class GenericSimulator {

    public PGPS getpGPS() {
        return pGPS;
    }

    public PFineADCS getpFineADCS() {
        return pFineADCS;
    }

    public void setpGPS(PGPS pGPS) {
        this.pGPS = pGPS;
    }

    public void setpFineADCS(PFineADCS pFineADCS) {
        this.pFineADCS = pFineADCS;
    }

    public PSDR getpSDR() {
        return pSDR;
    }

    public void setpSDR(PSDR pSDR) {
        this.pSDR = pSDR;
    }

    public PCamera getpCamera() {
        return pCamera;
    }

    public void setpCamera(PCamera pCamera) {
        this.pCamera = pCamera;
    }

    public POpticalReceiver getpOpticalReceiver() {
        return pOpticalReceiver;
    }

    public void setpOpticalReceiver(POpticalReceiver pOpticalReceiver) {
        this.pOpticalReceiver = pOpticalReceiver;
    }

    PGPS pGPS;
    PFineADCS pFineADCS;
    PSDR pSDR;
    PCamera pCamera;
    POpticalReceiver pOpticalReceiver;

}
