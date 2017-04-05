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
package esa.mo.nmf;

import java.io.Serializable;
import java.util.EventListener;

/**
 * The SimpleMonitorAndControlListener interface provides a simple way to 
 * receive actions, set parameters and get  parameters.
 * 
 */
public abstract interface SimpleMonitorAndControlListener extends EventListener {
    
    /**
     * The user must implement this interface in order to link a certain 
     * action Identifier to the method on the application
     * @param name Name of the Parameter
     * @param values The parameters/objects sent by the consumer
     * @param actionInstanceObjId The object instance identifier of the action
     * @return Returns true if the action was successfully executed, false 
     * otherwise.
     */
    boolean actionArrivedSimple (String name, Serializable[] values, Long actionInstanceObjId);
    
    /**
     * The user must implement this interface in order to acquire a certain 
     * parameter value of a variable in the application
     * @param name Name of the Parameter
     * @return The value of the parameter that was requested
     */
    Serializable onGetValueSimple (String name);
    
    /**
     * The user must implement this interface in order to set a certain 
     * parameter value to a variable in the application
     * @param name Name of the Parameter
     * @param value The value to be set on the parameter
     * @return True if the value was set successfully, false otherwise.
     */
    boolean onSetValueSimple (String name, Serializable value);
    
}
