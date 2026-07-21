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

import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * The ActionInvocationListener interface allows the creation of listeners for
 * the Action service.
 */
public interface ActionInvocationListener {

    /**
     * The user must implement this interface in order to link a certain action
     * Identifier to the method on the application.
     *
     * @param identifier Name of the action
     * @param attributeValues The attribute values for the action arguments
     * @param executionId The unique id of the action execution
     * @param interaction The MAL interaction context
     *
     * @throws ExecutionFailedException if the action execution fails. The exception message is
     * automatically captured and used as the comment field in both ExecutionProgress
     * (sent to ground systems in real-time) and ExecutionStatus (stored in archive
     * for historical tracking)
     * @throws ActionNotFoundException if the listener does not recognise the action. A composite
     * listener uses this to forward the action to the next listener instead of failing.
     */
    void actionArrived(Identifier identifier, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction)
            throws ExecutionFailedException, ActionNotFoundException;

}
