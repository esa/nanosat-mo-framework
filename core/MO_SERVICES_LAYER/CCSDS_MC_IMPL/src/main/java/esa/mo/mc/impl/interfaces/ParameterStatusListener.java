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

import java.util.EventListener;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;

/**
 *
 * 
 */
public interface ParameterStatusListener extends EventListener {
    
    /**
     * The user must implement this interface in order to acquire a certain 
     * parameter/rawType combination of a variable in the application
     * @param identifier Name of the Parameter
     * @param rawType Type of the requested parameter
     * @return The value of the parameter that was requested
     */
    public Attribute onGetValue (Identifier identifier, Byte rawType);
    
    /**
     * The user must implement this interface in order to set a certain 
     * parameter value to a variable in the application
     * @param identifier Name of the Parameter
     * @param value The value to be set on the parameter
     * @return True if the value was set successfully, false if not
     */
    public Boolean onSetValue (Identifier identifier, Attribute value);
    
}
