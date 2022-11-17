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

import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MCRegistration.RegistrationMode;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * This class provides a simple Hello World demo cli provider
 *
 */
public class DemoHelloWorldFull {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private static final String PARAMETER_NAME = "A_Parameter";
    private static final String PARAMETER_DESCRIPTION = "My first parameter!";
    private String var = "Hello World!";

    public DemoHelloWorldFull() {
        connector.init(new MCAdapter());
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        DemoHelloWorldFull demo = new DemoHelloWorldFull();
    }

    public class MCAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            registrationObject.setMode(RegistrationMode.DONT_UPDATE_IF_EXISTS);

            // ------------------ Parameters ------------------
            final ParameterDefinitionDetailsList defs = new ParameterDefinitionDetailsList();
            final IdentifierList names = new IdentifierList();

            defs.add(new ParameterDefinitionDetails(PARAMETER_DESCRIPTION, Union.STRING_SHORT_FORM.byteValue(), "",
                false, new Duration(3), null, null));
            names.add(new Identifier(PARAMETER_NAME));
            registrationObject.registerParameters(names, defs);
        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {
            if (PARAMETER_NAME.equals(identifier.getValue())) {
                return (Attribute) HelperAttributes.javaType2Attribute(var);
            }

            return null;
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            if (PARAMETER_NAME.equals(identifiers.get(0).getValue())) {
                var = values.get(0).getRawValue().toString();
                return true;  // to confirm that the variable was set
            }

            return false;
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues, Long actionInstanceObjId,
            boolean reportProgress, MALInteraction interaction) {
            return null;  // Action service not integrated
        }
    }
}
