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
package esa.mo.mp.impl.callback;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.EventInstanceDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;

/**
 * MPServiceOperationManager can be used to register for MP Service operations callbacks
 * by passing operation name and MPServiceOperationCallback object that receives the callback
 */

public class MPServiceOperationManager {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MPServiceOperationManager.class.getName());

    private final HashMap<MPServiceOperation, MPServiceOperationCallback> operations = new HashMap<>();

    public void register(MPServiceOperation operationName, MPServiceOperationCallback callback) {
        if (operations.get(operationName) != null) {
            LOGGER.warning(String.format("Operation %s is already registered. Ignoring previous registration.",
                operationName));
        }
        operations.put(operationName, callback);
    }

    public void notifyRequestValidation(MPServiceOperation operationName, RequestVersionDetails requestVersion) {
        if (operations.get(operationName) == null)
            return;
        operations.get(operationName).validate(requestVersion);
    }

    public void notifyActivityValidation(MPServiceOperation operationName, PlanVersionDetails planVersion,
        ActivityInstanceDetails activityInstance) {
        if (operations.get(operationName) == null)
            return;
        operations.get(operationName).validate(planVersion, activityInstance);
    }

    public void notifyEventValidation(MPServiceOperation operationName, PlanVersionDetails planVersion,
        EventInstanceDetails eventInstance) {
        if (operations.get(operationName) == null)
            return;
        operations.get(operationName).validate(planVersion, eventInstance);
    }

    public void notify(MPServiceOperation operationName, List<MPServiceOperationArguments> arguments)
        throws MALException, MALInteractionException {
        if (operations.get(operationName) == null)
            return;
        operations.get(operationName).onCallback(arguments);
    }
}