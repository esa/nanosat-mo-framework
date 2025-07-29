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
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mc.structures.ActionDefinition;
import org.ccsds.moims.mo.mc.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * The ActionInvocationListener interface allows the creation of listeners for
 * the Action service.
 */
public interface ActionInvocationListener {

    /**
     * The user must implement this interface in order to link a certain action
     * Identifier to the method on the application
     *
     * @param identifier Name of the Parameter.
     * @param attributeValues The attribute values for the action.
     * @param actionInstanceObjId The action instance id.
     * @param reportProgress Determines if it is necessary to report the
     * execution.
     * @param interaction The interaction object progress of the action
     * @return Returns null if the Action was successful. If not null, then the
     * returned value should hold the error number
     */
    UInteger actionArrived(Identifier identifier, AttributeValueList attributeValues,
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction);

    /**
     * The user must implement this interface in order to pre-check actions.
     *
     * @param defDetails The Action definition.
     * @param instDetails The Action instance.
     * @param errorList The list of errors.
     * @return True if passes, false otherwise.
     */
    boolean preCheck(ActionDefinition defDetails, ActionInstanceDetails instDetails, UIntegerList errorList);

}
