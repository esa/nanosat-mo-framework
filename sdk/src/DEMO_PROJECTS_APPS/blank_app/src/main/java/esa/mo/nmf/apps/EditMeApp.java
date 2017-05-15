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

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.NMFException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;
import esa.mo.nmf.NMFInterface;

/**
 * This class provides a blank template for starting the development of an app
 */
public class EditMeApp {

    private final NMFInterface nanoSatMOFramework = new NanoSatMOConnectorImpl(new MCAdapter());
    private String parameterX = "Hi!";

    public EditMeApp() {
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        EditMeApp demo = new EditMeApp();
    }

    public class MCAdapter extends MonitorAndControlNMFAdapter {

        private static final String PARAMETER_X = "parameterX";
        private static final String ACTION_1 = "action1";

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {

            registrationObject.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

            // ------------------ Parameters ------------------
            ParameterDefinitionDetailsList defsOther = new ParameterDefinitionDetailsList();
            IdentifierList paramNames = new IdentifierList();

            defsOther.add(new ParameterDefinitionDetails(
                    "The ADCS mode operation",
                    Union.STRING_SHORT_FORM.byteValue(),
                    "",
                    false,
                    new Duration(0),
                    null,
                    null
            ));
            paramNames.add(new Identifier(PARAMETER_X));

            registrationObject.registerParameters(paramNames, defsOther);

            // ------------------ Actions ------------------
            ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
            IdentifierList actionNames = new IdentifierList();

            ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
            {
                Byte rawType = Attribute._DURATION_TYPE_SHORT_FORM;
                String rawUnit = "seconds";
                ConditionalConversionList conditionalConversions = null;
                Byte convertedType = null;
                String convertedUnit = null;

                arguments1.add(new ArgumentDefinitionDetails(rawType, rawUnit, conditionalConversions, convertedType, convertedUnit));
            }

            ActionDefinitionDetails actionDef1 = new ActionDefinitionDetails(
                    "An action that reports 1 execution progress stage.",
                    new UOctet((short) 0),
                    new UShort(0),
                    arguments1,
                    null
            );
            actionNames.add(new Identifier(ACTION_1));

            actionDefs.add(actionDef1);
            registrationObject.registerActions(actionNames, actionDefs);
        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {

            if (PARAMETER_X.equals(identifier.toString())) { // parameterX was called?
                return (Attribute) HelperAttributes.javaType2Attribute(PARAMETER_X + " was called! The value is: " + parameterX);
            }

            return null;
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            if (PARAMETER_X.equals(identifiers.get(0).toString())) { // parameterX was called?
                parameterX = values.get(0).getRawValue().toString();
                return true;
            }

            return false;  // to confirm that the variable was set
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
                Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
            if (ACTION_1.equals(name.getValue())) {
                try {
                    nanoSatMOFramework.reportActionExecutionProgress(true, 0, 1, 1, actionInstanceObjId);
                } catch (NMFException ex) {
                    Logger.getLogger(EditMeApp.class.getName()).log(Level.SEVERE, null, ex);
                    return new UInteger(0);
                }
            }

            return null;  // Action service not integrated
        }
    }
}
