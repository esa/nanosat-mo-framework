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
package esa.mo.nmf.apps;

import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NanoSatMOFrameworkInterface;
import esa.mo.nmf.provider.NanoSatMOMonolithicSim;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalReferenceList;
import org.ccsds.moims.mo.mc.structures.Severity;

/**
 * A simple demo that reports 5 stages of an Action every 2 seconds
 *
 */
public class FiveStagesAction {

    private final NanoSatMOFrameworkInterface nanoSatMOFramework = new NanoSatMOMonolithicSim(new MCAdapter());
    private final static int TOTAL_N_OF_STAGES = 5; // 5 stages
    private final static int SLEEP_TIME = 2; // 2 seconds
    private final static String ACTION5STAGES = "Go";

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

    public class MCAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();

            ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
            {
                Byte rawType = Attribute._DURATION_TYPE_SHORT_FORM;
                String rawUnit = "seconds";
                ConditionalReferenceList conversionCondition = null;
                Byte convertedType = null;
                String convertedUnit = null;

                arguments1.add(new ArgumentDefinitionDetails(rawType, rawUnit, conversionCondition, convertedType, convertedUnit));
            }

            ActionDefinitionDetails actionDef1 = new ActionDefinitionDetails(
                    new Identifier(ACTION5STAGES),
                    "Example of an Action with 5 stages.",
                    Severity.INFORMATIONAL,
                    new UShort(0),
                    arguments1,
                    null
            );

            actionDefs.add(actionDef1);
            LongList actionObjIds = registrationObject.registerActions(actionDefs);
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

            if (ACTION5STAGES.equals(name.getValue())) { try {
                // action1 was called?
                fiveStepsAction(actionInstanceObjId);
                } catch (NMFException ex) {
                    Logger.getLogger(FiveStagesAction.class.getName()).log(Level.SEVERE, null, ex);
                    return new UInteger(0);
                }
            
                return null;
            }

            return new UInteger(1);  // Action service not integrated
        }
    }

    public void fiveStepsAction(Long actionId) throws NMFException {
        for (int stage = 1; stage < TOTAL_N_OF_STAGES + 1; stage++) {
            nanoSatMOFramework.reportActionExecutionProgress(true, 0, stage, TOTAL_N_OF_STAGES, actionId);

            try {
                Thread.sleep(SLEEP_TIME * 1000); //1000 is milliseconds multiplier.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
