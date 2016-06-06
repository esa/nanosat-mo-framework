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
package esa.mo.com.impl.util;

import esa.mo.com.impl.util.EventCOMObject;

/**
 * An interface to receive events from the Event service via the monitorEvent
 * operation
 *
 * @author Cesar Coelho
 */
public abstract class EventReceivedListener {

     /**
     * This interface must be implemented in order to receive the subscribed
     * events coming from the Event service monitorValue operation.
     *
     * @param eventCOMObject The COM Object of the received event
     */
    public abstract void onDataReceived (EventCOMObject eventCOMObject);
   
}
