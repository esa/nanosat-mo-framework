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
package esa.mo.mc.impl.interfaces;

import java.io.IOException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

/**
 *
 *
 */
public interface ParameterStatusListener {

    /**
     * Called by the NMF core whenever a parameter value needs to be propagated to the consumer,
     * either through Aggregation or Parameter service.
     *
     * The user must implement this interface in order to acquire a certain parameter/rawType
     * combination of a variable in the application.
     *
     * @param identifier Name of the Parameter
     * @param rawType    Type of the requested parameter
     * @return The value of the parameter that was requested
     * @throws java.io.IOException if the parameter value could not be acquired
     */
    @Deprecated
    default Attribute onGetValue(Identifier identifier, Byte rawType) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Called by the NMF core whenever a parameter value needs to be propagated to the consumer,
     * either through Aggregation or Parameter service.
     *
     * The user must implement this interface in order to acquire a certain parameter/rawType
     * combination of a variable in the application.
     *
     * @param parameterID the ID of the parameter
     * @return The value of the parameter that was requested
     * @throws java.io.IOException if the parameter value could not be acquired
     */
    default Attribute onGetValue(Long parameterID) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * The user must implement this interface in order to set a collection of parameter values to a
     * set variables in the application logic.
     *
     * @param identifiers Name of the Parameters
     * @param values      The raw values to be set at the parameter
     * @return True if the value was set successfully, false if not
     */
    @Deprecated
    default Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * The user must implement this interface in order to set a collection of parameter values to a
     * set variables in the application logic.
     *
     * @param newRawValues The new values to be set at the parameter
     * @return True if the value was set successfully, false if not
     */
    default Boolean onSetValue(ParameterRawValueList newRawValues) {// This function needs to be implement here to ensure backwards compatibility
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This method allows to use implementation specific mechanisms to determine parameter validity.
     * Also it supports the value to have deployment-specific validityStates.
     *
     * @param rawValue The raw value to be set
     * @param pDef     The parameter definition details of the parameter
     *
     * @return the validity state of the value. if it is deployment-specific then the values must be
     * greater than 127. if null then there are no custom mechanisms and values, and the standard ones
     * must be used
     */
    ParameterValue getValueWithCustomValidityState(Attribute rawValue, ParameterDefinitionDetails pDef);

    /**
     * Checks if a Parameter is read-only
     *
     * @param name The name of the parameter
     * @return True, if it is read-only. False, if you can set it.
     */
    @Deprecated
    boolean isReadOnly(Identifier name);

    /**
     * Checks if a Parameter is read-only
     *
     * @param parameterID id of the parameter
     * @return True, if it is read-only. False, if you can set it.
     */
    default boolean isReadOnly(Long parameterID) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Gets the intervals supported by the provider
     *
     * @return a list with the supported intervals as durations
     */
    //    public DurationList getProvidedIntervals();
    /**
     * The user must implement this interface in order to acquire a certain parameter values timestamp
     * in the application.
     *
     * @param identifier Name of the Parameter
     * @return The timestamp when the parameters value was set
     */
    //    public Long onGetValueTimestamp(Identifier identifier);
}
