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
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.File;
import java.io.IOException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.structures.*;

/**
 *
 * @author Cesar Coelho
 */
public class MCAdapter extends MonitorAndControlNMFAdapter {

    private final NanoSatMOConnectorImpl connector;
    //    private static int NUMBER_OF_OBJS = 5000;
    private static final int NUMBER_OF_OBJS = 10000;
    private static final String PARAMETER_PERIODIC = "Periodic_Parameter";
    private static final String PARAMETER_ARCHIVE_SIZE = "COM_Archive.size";
    private static final String ACTION_STORE_AGGS = "StoreAggregations";
    private static final String ACTION_STORE_PARS = "StoreParameters";

    MCAdapter(NanoSatMOConnectorImpl connector) {
        this.connector = connector;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Parameters ------------------
        ParameterDefinitionList parDef = new ParameterDefinitionList();

        // Creates a periodic parameter
        parDef.add(new ParameterDefinition(
                new Identifier(PARAMETER_PERIODIC),
                "A periodic parameter with a double value.",
                AttributeType.DOUBLE, "unit", false, new Duration(1), null, null));

        // Creates a periodic parameter
        parDef.add(new ParameterDefinition(
                new Identifier(PARAMETER_ARCHIVE_SIZE),
                "The COM Archive size.", AttributeType.DOUBLE,
                "bytes", false, new Duration(0), null, null));

        registration.registerParameters(parDef);

        // ------------------ Actions ------------------
        ActionDefinitionList actionDefs = new ActionDefinitionList();

        ArgumentDefinitionList arguments1 = new ArgumentDefinitionList();
        {
            AttributeType rawType = AttributeType.INTEGER;
            String rawUnit = "-";

            arguments1.add(new ArgumentDefinition(new Identifier("1"), "", rawType, rawUnit));
        }

        actionDefs.add(new ActionDefinition(
                new Identifier(ACTION_STORE_AGGS),
                "Stores " + NUMBER_OF_OBJS + " aggregation definition objects in the COM Archive.",
                ActionCategory.DEFAULT, new UShort(0), arguments1));

        actionDefs.add(new ActionDefinition(
                new Identifier(ACTION_STORE_PARS),
                "Stores " + NUMBER_OF_OBJS + " parameter value objects in the COM Archive.",
                ActionCategory.DEFAULT, new UShort(0), arguments1));

        LongList actionObjIds = registration.registerActions(actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, AttributeType rawType) throws IOException {
        if (PARAMETER_PERIODIC.equals(identifier.getValue())) {
            return (Attribute) HelperAttributes.javaType2Attribute(123.456);
        }

        if (PARAMETER_ARCHIVE_SIZE.equals(identifier.getValue())) {
            File f = connector.getDatabaseLocationInUserDirectory();
            long size = f.length();
            return (Attribute) HelperAttributes.javaType2Attribute(size);
        }

        throw new IOException("The value could not be acquired!");
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UInteger actionArrived(Identifier name, AttributeValueList attributeValues,
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        if (ACTION_STORE_AGGS.equals(name.getValue())) {
            StoreAggregations.storeAggregations(NUMBER_OF_OBJS, connector);
        }

        if (ACTION_STORE_PARS.equals(name.getValue())) {
            StoreParameters.storeParameterValues(NUMBER_OF_OBJS, connector);
        }

        return null;  // Action service not integrated
    }

}
