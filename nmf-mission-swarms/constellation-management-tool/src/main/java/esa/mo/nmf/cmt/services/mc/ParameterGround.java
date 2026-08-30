/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
 *
 * Author: N Wiegand (https://github.com/Klabau)
 */

package esa.mo.nmf.cmt.services.mc;

import esa.mo.mc.impl.consumer.ParameterConsumerServiceImpl;
import esa.mo.nmf.cmt.ConstellationManagementTool;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.structures.*;
import org.ccsds.moims.mo.mc.structures.ParameterExpression;

public class ParameterGround {

    public static ParameterDefinition makeNewParameterDefinition(Identifier name, AttributeType rawType,
        String rawUnit, String description, boolean generationEnabled, float interval,
        ParameterExpression validityExpression, ParameterConversion conversion) {

        return new ParameterDefinition(
            name,
            description,
            rawType,
            rawUnit,
            generationEnabled,
            new Duration(interval),
            validityExpression,
            conversion,
            false);
    }

    public static void setValue(GroundMOAdapterImpl groundMOAdapter) {
        final ParameterConsumerServiceImpl serviceMCParameter = groundMOAdapter.getMCServices().getParameterService();
        Union attribute = (Union) HelperAttributes.javaType2Attribute("thisIsSomeAttribute");

        ParameterRawValue rawValue = new ParameterRawValue(4l, attribute);
        ParameterRawValueList rawValueList = new ParameterRawValueList();
        rawValueList.add(rawValue);

        try {
            serviceMCParameter.getParameterStub().setValue(rawValueList);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void addParameter(GroundMOAdapterImpl groundMOAdapter, ParameterRawValue value) {

        final ParameterConsumerServiceImpl serviceMCParameter = groundMOAdapter.getMCServices().getParameterService();
        String rawUnit = "GPS position CSV";
        String description = "GeoFence data for testApp";
        boolean generationEnabled = false;
        float interval = 10f;
        ParameterExpression parameterExpression = null;
        ParameterConversion parameterConversion = null;

        ParameterDefinition parDef;
        parDef = makeNewParameterDefinition(
                new Identifier("GeoFence.testApp"),
                AttributeType.STRING,
                rawUnit,
                description,
                generationEnabled,
                interval,
                parameterExpression,
                parameterConversion);

        ParameterDefinitionList requestList = new ParameterDefinitionList();
        requestList.add(parDef);

        /*
        try {
            serviceMCParameter.getParameterStub().addParameter(requestList);
            setValue(groundMOAdapter);
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(ConstellationManagementTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
    }
}
