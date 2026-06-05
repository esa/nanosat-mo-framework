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
import org.ccsds.moims.mo.mal.structures.*;
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
    private static final String ACTION_SHUTDOWN_GRACEFULLY = "shutdown.gracefully";
    private static final String ACTION_SHUTDOWN_EXIT_0 = "shutdown.system.exit.0";
    private static final String ACTION_SHUTDOWN_EXIT_X = "shutdown.system.exit.x";

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
                new UShort(0), arguments1));

        actionDefs.add(new ActionDefinition(
                new Identifier(ACTION_STORE_PARS),
                "Stores " + NUMBER_OF_OBJS + " parameter value objects in the COM Archive.",
                new UShort(0), arguments1));

        actionDefs.add(new ActionDefinition(
                new Identifier(ACTION_SHUTDOWN_GRACEFULLY),
                "Shuts down the app gracefully via the NMF connector.",
                new UShort(0), null));

        actionDefs.add(new ActionDefinition(
                new Identifier(ACTION_SHUTDOWN_EXIT_0),
                "Shuts down the app immediately with exit code 0.",
                new UShort(0), null));

        ArgumentDefinitionList exitCodeArgs = new ArgumentDefinitionList();
        exitCodeArgs.add(new ArgumentDefinition(new Identifier("exitCode"),
                "The exit code.", AttributeType.INTEGER, "-"));
        actionDefs.add(new ActionDefinition(
                new Identifier(ACTION_SHUTDOWN_EXIT_X),
                "Shuts down the app immediately with the specified exit code.",
                new UShort(0), exitCodeArgs));

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
            Long executionId, boolean reportProgress, MALInteraction interaction) {
        if (ACTION_STORE_AGGS.equals(name.getValue())) {
            StoreAggregations.storeAggregations(NUMBER_OF_OBJS, connector);
        }

        if (ACTION_STORE_PARS.equals(name.getValue())) {
            StoreParameters.storeParameterValues(NUMBER_OF_OBJS, connector);
        }

        if (ACTION_SHUTDOWN_GRACEFULLY.equals(name.getValue())) {
            new Thread(() -> connector.closeGracefully(null)).start();
        }

        if (ACTION_SHUTDOWN_EXIT_0.equals(name.getValue())) {
            new Thread(() -> System.exit(0)).start();
        }

        if (ACTION_SHUTDOWN_EXIT_X.equals(name.getValue())) {
            int exitCode = (Integer) HelperAttributes.attribute2JavaType(
                    attributeValues.get(0).getValue());
            new Thread(() -> System.exit(exitCode)).start();
        }

        return null;  // Action service not integrated
    }

}
