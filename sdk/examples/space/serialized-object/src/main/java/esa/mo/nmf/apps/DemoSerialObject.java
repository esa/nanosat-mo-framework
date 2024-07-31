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
import esa.mo.nmf.SimpleMonitorAndControlAdapter;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.Serializable;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;

/**
 * This class provides a demo cli provider for generating Serial objects
 *
 */
public class DemoSerialObject {

    private final NanoSatMOConnectorImpl connector = new NanoSatMOConnectorImpl();
    private final static String PARAMETER = "MyParameter";

    public DemoSerialObject() {
        connector.init(new MCAdapter());
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        DemoSerialObject demo = new DemoSerialObject();
    }

    public static class MCAdapter extends SimpleMonitorAndControlAdapter {

        @Override
        public void initialRegistrations(MCRegistration registrationObject) {
            registrationObject.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

            // ------------------ Parameters ------------------
            final ParameterDefinitionDetailsList defsOther = new ParameterDefinitionDetailsList();
            final IdentifierList names = new IdentifierList();

            defsOther.add(new ParameterDefinitionDetails("A single parameter.", Union.STRING_SHORT_FORM.byteValue(), "",
                true, new Duration(0), null, null));
            names.add(new Identifier(PARAMETER));

            registrationObject.registerParameters(names, defsOther);
        }

        @Override
        public boolean actionArrivedSimple(String name, Serializable[] srlzbls, Long l) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Serializable onGetValueSimple(String name) {
            AttributeValue aval = new AttributeValue();
            aval.setValue(new UInteger(1234));
            return aval;
        }

        @Override
        public boolean onSetValueSimple(String name, Serializable srlzbl) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
