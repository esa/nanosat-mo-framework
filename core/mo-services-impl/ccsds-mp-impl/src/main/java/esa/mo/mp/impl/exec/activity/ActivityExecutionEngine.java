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
package esa.mo.mp.impl.exec.activity;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;

/**
 * ActivityExecutionEngine calls registered ExecutableActivities by execDef
 */
public class ActivityExecutionEngine {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(ActivityExecutionEngine.class.getName());

    private final HashMap<String, ExecutableActivity> registrations = new HashMap<>();

    public void register(String execDef, ExecutableActivity activity) {
        registrations.put(execDef, activity);
    }

    public void executeActivity(String execDef, ObjectId activityInstanceId) {
        if (registrations.containsKey(execDef)) {
            try {
                registrations.get(execDef).execute(activityInstanceId);
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        } else {
            LOGGER.warning("ExecDef " + execDef + " not found during execution");
        }
    }

    public void missedActivity(String execDef, ObjectId activityInstanceId) {
        if (registrations.containsKey(execDef)) {
            try {
                registrations.get(execDef).missed(activityInstanceId);
            } catch (MALException | MALInteractionException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
        } else {
            LOGGER.warning("ExecDef " + execDef + " not found");
        }
    }
}
