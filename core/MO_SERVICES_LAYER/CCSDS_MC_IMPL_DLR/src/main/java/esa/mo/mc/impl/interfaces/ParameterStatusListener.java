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
package esa.mo.mc.impl.interfaces;

import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.DurationList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

/**
 *
 *
 */
public interface ParameterStatusListener extends ParameterTestStatusListener {

    /**
     * The user must implement this interface in order to acquire a certain
     * parameter/rawType combination of a variable in the application
     *
     * @param identifier Name of the Parameter
     * @param rawType Type of the requested parameter
     * @return The value of the parameter that was requested
     */
    public Attribute onGetValue(Identifier identifier, Byte rawType);
    /**
     * The user must implement this interface in order to acquire a certain
     * parameter values timestamp in the application.
     *
     * @param identifier Name of the Parameter
     * @return The timestamp when the parameters value was set
     */
    public Long onGetValueTimestamp(Identifier identifier);

    /**
     * The user must implement this interface in order to set a certain
     * parameter value to a variable in the application
     *
     * @param identifier Name of the Parameter
     * @param value The raw value to be set at the parameter
     * @param timestamp the timestamp when the value was set/created
     * @return True if the value was set successfully, false if not
     */
    public Boolean onSetValue(Identifier identifier, Attribute value, Long timestamp);

    /**
     * this method allows to use implementation specific mechanisms to determine
     * parameter validity. Also it supports the value to have
     * deployment-specific validityStates.
     *
     * @param rawValue the raw value to be set
     * @param pDef the definition of the parameter the value should be created
     * from
     * @return the validity state of the value. if it is deployment-specific
     * then the values must be greater than 127. if null then there are no
     * custom mechanisms and values, and the standard ones must be used
     */
    public ParameterValue getValueWithCustomValidityState(Attribute rawValue, ParameterDefinitionDetails pDef);

    /**
     * checks if a Parameter is read-only
     *
     * @param name the anme of the parameter
     * @return true, if it is readonly. false, if you can set it.
     */
    public boolean isReadOnly(Identifier name);

    /**
     * gets the intervals supported by the provider
     *
     * @return a list with the supported intervals as durations
     */
//    public DurationList getProvidedIntervals();

    /**
     * removes the given parameters with its values from the provider.
     * 
     * @param parameterNames the names of the parameters of which the values should be removed. The value "*" removes all of them.
     */
//    public void removeParameters(IdentifierList parameterNames);

}
