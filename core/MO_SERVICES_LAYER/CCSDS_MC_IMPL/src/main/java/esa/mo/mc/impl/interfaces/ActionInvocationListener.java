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
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 *
 * 
 */
public interface ActionInvocationListener extends EventListener {
    
    /**
     * The user must implement this interface in order to link a certain 
     * action Identifier to the method on the application
     * @param identifier Name of the Parameter
     * @param attributeValues
     * @param actionInstanceObjId
     * @param reportProgress Determines if it is necessary to report the execution
     * @param interaction The interaction object
     * progress of the action
     * @return Returns null if the Action was successful. If not null, then the 
     * returned value should hold the error number
     */
    public UInteger actionArrived (Identifier identifier, AttributeValueList attributeValues, Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction);

}
