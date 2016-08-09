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
package esa.mo.nanosatmoframework.apps;

import esa.mo.nanosatmoframework.adapters.MonitorAndControlAdapter;
import esa.mo.nanosatmoframework.interfaces.NanoSatMOFrameworkInterface;
import esa.mo.nanosatmoframework.provider.NanoSatMOMonolithicSim;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * A simple demo that reports 5 stages of an Action every 2 seconds
 *
 */
public class FiveStagesAction {

    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOMonolithicSim(new mcAdapter());
    private final static int TOTAL_N_OF_STAGES = 5; // 5 stages
    private final static int SLEEP_TIME = 2; // 2 seconds

    public FiveStagesAction() {
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        FiveStagesAction demo = new FiveStagesAction();
    }

    public class mcAdapter extends MonitorAndControlAdapter {

        @Override
        public void initialRegistrations() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {
            return null;
        }

        @Override
        public Boolean onSetValue(Identifier identifier, Attribute value) {
            return false;  // to confirm that no variable was set
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {

            if ("Go".equals(name.getValue())) { // action1 was called?
                fiveStepsAction(actionInstanceObjId);
                return null;
            }

            return new UInteger(1);  // Action service not integrated
        }
    }

    public void fiveStepsAction(Long actionId) {
        for (int stage = 1; stage < TOTAL_N_OF_STAGES + 1; stage++) {
            try {
                nanoSatMOFramework.reportActionExecutionProgress(true, 0, stage, TOTAL_N_OF_STAGES, actionId);
            } catch (IOException ex) {
                Logger.getLogger(FiveStagesAction.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {
                Thread.sleep(SLEEP_TIME * 1000); //1000 milliseconds multiplier.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
