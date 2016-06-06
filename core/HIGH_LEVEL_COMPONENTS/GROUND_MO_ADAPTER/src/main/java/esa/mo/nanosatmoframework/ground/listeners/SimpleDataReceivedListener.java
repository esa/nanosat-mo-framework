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
package esa.mo.nanosatmoframework.ground.listeners;

import esa.mo.nanosatmoframework.ground.interfaces.DataReceivedListener;
import java.io.Serializable;

/**
 * An abstract class that pushes the received data from the Parameter service 
 * coming via the monitorValue operation with the basic parameter data: name of 
 * the parameter and the content
 *
 * @author Cesar Coelho
 */
public abstract class SimpleDataReceivedListener implements DataReceivedListener {

    /**
     * This interface must be implemented in order to receive the parameter
     * content from the Parameter service coming via the monitorValue operation
     *
     * @param parameterName Name of the Parameter
     * @param data The content of the data
     */
    public abstract void onDataReceived (String parameterName, Serializable data);
    
}
