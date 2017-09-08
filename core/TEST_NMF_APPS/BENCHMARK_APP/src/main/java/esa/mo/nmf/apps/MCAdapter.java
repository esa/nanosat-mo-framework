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
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import esa.mo.nmf.NMFInterface;
import esa.mo.nmf.nanosatmoconnector.NanoSatMOConnectorImpl;
import java.io.File;
import java.io.IOException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
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

/**
 *
 * @author Cesar Coelho
 */
public class MCAdapter extends MonitorAndControlNMFAdapter {

    private final NMFInterface connector;
//    private static int NUMBER_OF_OBJS = 5000;
    private static final int NUMBER_OF_OBJS = 10000;
    private static final String ACTION_STORE_AGGS = "StoreAggregations";
    private static final String ACTION_STORE_PARS = "StoreParameters";
    private static final String PARAMETER_PERIODIC = "Periodic_Parameter";
    private static final String PARAMETER_ARCHIVE_SIZE = "COM_Archive.size";

    MCAdapter(NanoSatMOConnectorImpl connector) {
        this.connector = connector;
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(MCRegistration.RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Parameters ------------------
        ParameterDefinitionDetailsList parDef = new ParameterDefinitionDetailsList();
        IdentifierList paramNames = new IdentifierList();

        // Creates a periodic parameter
        parDef.add(new ParameterDefinitionDetails(
                "A periodic parameter with a double value.",
                Union.DOUBLE_TYPE_SHORT_FORM.byteValue(),
                "unit",
                false,
                new Duration(1),
                null,
                null
        ));
        paramNames.add(new Identifier(PARAMETER_PERIODIC));

        // Creates a periodic parameter
        parDef.add(new ParameterDefinitionDetails(
                "The COM Archive size.",
                Union.LONG_TYPE_SHORT_FORM.byteValue(),
                "bytes",
                false,
                new Duration(0),
                null,
                null
        ));
        paramNames.add(new Identifier(PARAMETER_ARCHIVE_SIZE));
        
        registration.registerParameters(paramNames, parDef);

        // ------------------ Actions ------------------
        ActionDefinitionDetailsList actionDefs = new ActionDefinitionDetailsList();
        IdentifierList actionNames = new IdentifierList();

        ArgumentDefinitionDetailsList arguments1 = new ArgumentDefinitionDetailsList();
        {
            Byte rawType = Attribute._INTEGER_TYPE_SHORT_FORM;
            String rawUnit = "-";
            ConditionalConversionList conditionalConversions = null;
            Byte convertedType = null;
            String convertedUnit = null;

            arguments1.add(new ArgumentDefinitionDetails(rawType, rawUnit,
                    conditionalConversions, convertedType, convertedUnit));
        }

        actionDefs.add(new ActionDefinitionDetails(
                "Stores " + NUMBER_OF_OBJS + " aggregation definition objects in the COM Archive.",
                new UOctet((short) 0),
                new UShort(0),
                arguments1,
                null
        ));
        actionNames.add(new Identifier(ACTION_STORE_AGGS));

        actionDefs.add(new ActionDefinitionDetails(
                "Stores " + NUMBER_OF_OBJS + " parameter value objects in the COM Archive.",
                new UOctet((short) 0),
                new UShort(0),
                arguments1,
                null
        ));
        actionNames.add(new Identifier(ACTION_STORE_PARS));
        
        LongList actionObjIds = registration.registerActions(actionNames, actionDefs);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) throws IOException {
        if (PARAMETER_PERIODIC.equals(identifier.getValue())) {
            return (Attribute) HelperAttributes.javaType2Attribute(123.456);
        }
        
        if (PARAMETER_ARCHIVE_SIZE.equals(identifier.getValue())) {
            File f = new File("comArchive.db");
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
