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
import esa.mo.nmf.MCRegistration.RegistrationMode;
import esa.mo.nmf.SimpleMonitorAndControlAdapter;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinition;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinition;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionList;
import org.ccsds.moims.mo.mc.structures.ConditionalConversionList;

/**
 * This class provides a simple Hello World demo cli provider
 *
 */
public class DemoHelloWorldSimple {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private static final String PARAMETER_NAME = "A_Parameter";
    private static final String PARAMETER_DESCRIPTION = "My first parameter!";
    private String var = "Hello World!";
    private static final String ACTION_GO = "Go";

    public DemoHelloWorldSimple() {
        connector.init(new MCAdapterSimple());
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        DemoHelloWorldSimple demo = new DemoHelloWorldSimple();
    }

    public class MCAdapterSimple extends SimpleMonitorAndControlAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            registrationObject.setMode(RegistrationMode.DONT_UPDATE_IF_EXISTS);

            // ------------------ Parameters ------------------
            final ParameterDefinitionList defs = new ParameterDefinitionList();
            final IdentifierList names = new IdentifierList();

            defs.add(new ParameterDefinition(PARAMETER_DESCRIPTION, AttributeType.STRING, "",
                false, new Duration(3), null, null));
            names.add(new Identifier(PARAMETER_NAME));
            registrationObject.registerParameters(names, defs);

            // ------------------ Actions ------------------
            ActionDefinitionList actionDefs = new ActionDefinitionList();
            IdentifierList actionNames = new IdentifierList();

            ArgumentDefinitionList arguments1 = new ArgumentDefinitionList();
            {
                AttributeType rawType = AttributeType.DOUBLE;
                String rawUnit = "-";
                ConditionalConversionList conditionalConversions = null;
                AttributeType convertedType = null;
                String convertedUnit = null;

                arguments1.add(new ArgumentDefinition(new Identifier("1"), null, rawType, rawUnit,
                    conditionalConversions, convertedType, convertedUnit));
            }

            actionDefs.add(new ActionDefinition("Simple Go action with double value.", new UOctet((short) 0),
                new UShort(3), arguments1));
            actionNames.add(new Identifier(ACTION_GO));

            registrationObject.registerActions(actionNames, actionDefs);
        }

        @Override
        public Serializable onGetValueSimple(String name) {
            if (PARAMETER_NAME.equals(name)) {
                return var;
            }

            return null;
        }

        @Override
        public boolean onSetValueSimple(String name, Serializable value) {
            if (PARAMETER_NAME.equals(name)) {
                var = value.toString();
                return true;  // to confirm that the variable was set
            }

            return false;
        }

        @Override
        public boolean actionArrivedSimple(String name, Serializable[] values, Long actionInstanceObjId) {
            if (ACTION_GO.equals(name)) {
                Logger.getLogger(DemoHelloWorldSimple.class.getName()).log(Level.INFO,
                    "Action 'Go' activated. Success!");
                return true; // Success!
            }
            return false;
        }
    }
}
