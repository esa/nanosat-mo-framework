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
package esa.mo.nmf.apps;

import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;

/**
 * A simple NMF App demo that reports 5 stages of an Action every 2 seconds
 *
 */
public class FiveStagesAction {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private final static int TOTAL_N_OF_STAGES = 5; // 5 stages
    private final static int SLEEP_TIME = 2; // 2 seconds
    private final static String ACTION5STAGES = "Go";

    public FiveStagesAction() {
        connector.init(new MCAdapter());
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        FiveStagesAction demo = new FiveStagesAction();
    }

    public class MCAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
            IdentifierList names = new IdentifierList();

            ArgumentDefinitionDetailsList argDef = new ArgumentDefinitionDetailsList();
            {
                Byte rawType = Attribute._DURATION_TYPE_SHORT_FORM;
                String rawUnit = "seconds";
                ConditionalConversionList conditionalConversions = null;
                Byte convertedType = null;
                String convertedUnit = null;

                argDef.add(new ArgumentDefinitionDetails(new Identifier("1"), null, rawType, rawUnit,
                    conditionalConversions, convertedType, convertedUnit));
            }

            ActionDefinitionDetails actionDef1 = new ActionDefinitionDetails("Example of an Action with 5 stages.",
                new UOctet((short) 0), new UShort(5), argDef);
            names.add(new Identifier(ACTION5STAGES));
            actionDefs.add(actionDef1);
            registrationObject.registerActions(names, actionDefs);
        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {
            return null;
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            return false;  // to confirm that no variable was set
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId,
            boolean reportProgress, MALInteraction interaction) {
            if (ACTION5STAGES.equals(name.getValue())) {
                try {
                    // action1 was called?
                    reportFiveStepsAction(actionInstanceObjId);
                } catch (NMFException ex) {
                    Logger.getLogger(FiveStagesAction.class.getName()).log(Level.SEVERE,
                        "The action could not report the five steps!", ex);
                    return new UInteger(0);
                }

                return null;
            }

            return new UInteger(1);  // Action service not integrated
        }
    }

    public void reportFiveStepsAction(Long actionId) throws NMFException {
        for (int stage = 1; stage < TOTAL_N_OF_STAGES + 1; stage++) {
            connector.reportActionExecutionProgress(true, 0, stage, TOTAL_N_OF_STAGES, actionId);

            try { // Quick and dirty, but enough for demo purposes!
                Thread.sleep(SLEEP_TIME * 1000); // 1000 is the ms multiplier.
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
