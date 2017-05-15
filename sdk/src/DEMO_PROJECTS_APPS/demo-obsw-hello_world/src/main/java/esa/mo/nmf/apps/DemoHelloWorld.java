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

import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.SimpleMonitorAndControlAdapter;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.Serializable;
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
import esa.mo.nmf.NMFInterface;

/**
 * This class provides a simple Hello World demo cli provider
 *
 */
public class DemoHelloWorld {

    private final NMFInterface nanoSatMOFramework;
    private static final String PARAMETER_HELLO = "A_Parameter";
    private String str = "Hello World!";

    public DemoHelloWorld() {
        this.nanoSatMOFramework = new NanoSatMOConnectorImpl(new MCAdapterSimple());
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String args[]) throws Exception {
        DemoHelloWorld demo = new DemoHelloWorld();
    }

    public class MCAdapter extends MonitorAndControlNMFAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            registrationObject.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

            // ------------------ Parameters ------------------
            final ParameterDefinitionDetailsList defsOther = new ParameterDefinitionDetailsList();
            final IdentifierList names = new IdentifierList();

            defsOther.add(new ParameterDefinitionDetails(
                    "The ADCS mode operation",
                    Union.STRING_SHORT_FORM.byteValue(),
                    "",
                    false,
                    new Duration(3),
                    null,
                    null
            ));
            names.add(new Identifier(PARAMETER_HELLO));

            registrationObject.registerParameters(names, defsOther);
        }

        @Override
        public Attribute onGetValue(Identifier identifier, Byte rawType) {
            if (PARAMETER_HELLO.equals(identifier.getValue())) {
                return (Attribute) HelperAttributes.javaType2Attribute(str);
            }

            return null;
        }

        @Override
        public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
            if (PARAMETER_HELLO.equals(identifiers.get(0).getValue())) {
                str = values.get(0).getRawValue().toString(); // Let's set the str variable
                return true;  // to confirm that the variable was set                
            }

            return false;
        }

        @Override
        public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
                Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
            return null;  // Action service not integrated
        }

    }

    public class MCAdapterSimple extends SimpleMonitorAndControlAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            registrationObject.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

            // ------------------ Parameters ------------------
            final ParameterDefinitionDetailsList defsOther = new ParameterDefinitionDetailsList();
            final IdentifierList names = new IdentifierList();

            defsOther.add(new ParameterDefinitionDetails(
                    "My first parameter!",
                    Union.STRING_SHORT_FORM.byteValue(),
                    "",
                    false,
                    new Duration(3),
                    null,
                    null
            ));
            names.add(new Identifier(PARAMETER_HELLO));

            registrationObject.registerParameters(names, defsOther);
        }

        @Override
        public boolean actionArrivedSimple(String name, Serializable[] values, Long actionInstanceObjId) {
            return false;
        }

        @Override
        public Serializable onGetValueSimple(String name) {
            if (PARAMETER_HELLO.equals(name)) {
                return str;
            }

            return null;
        }

        @Override
        public boolean onSetValueSimple(String name, Serializable value) {
            if (PARAMETER_HELLO.equals(name)) {
                str = value.toString(); // Let's set the str variable
                return true;  // to confirm that the variable was set
            }

            return false;
        }
    }

}
