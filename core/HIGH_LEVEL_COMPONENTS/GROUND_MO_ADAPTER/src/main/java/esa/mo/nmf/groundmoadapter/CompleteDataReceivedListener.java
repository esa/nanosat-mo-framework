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
package esa.mo.nmf.groundmoadapter;

import esa.mo.mc.impl.provider.ParameterInstance;

/**
 * An abstract class that pushes the received data from the Parameter service
 * coming via the monitorValue operation with the complete parameter data
 *
 * @author Cesar Coelho
 */
public abstract class CompleteDataReceivedListener implements DataReceivedListener {

    /**
     * This interface must be implemented in order to receive the parameter
     * content from the Parameter service coming via the monitorValue operation
     *
     * @param parameterInstance
     */
    public abstract void onDataReceived(ParameterInstance parameterInstance);

}
