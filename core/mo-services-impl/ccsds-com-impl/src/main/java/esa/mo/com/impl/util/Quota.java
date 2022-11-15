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
package esa.mo.com.impl.util;

import java.util.HashMap;
import java.util.HashSet;

/**
 * This class represents a simple quota mechanism. You can either right directly to the underlying
 * data structure or use the provided wrapper methods.
 *
 * @author yannick
 */
public class Quota extends HashMap<Long, Integer> {

    /**
     * This method resets the quota to 0 for all provided IDs.
     * @param ids The entities for which to reset the quotas.
     */
    public void clean(HashSet<Long> ids) {
        synchronized (this) {
            for (Long id : ids) {
                this.put(id, 0);
            }
        }
    }

    /**
     * Increases the used quota for the provided entity.
     * @param id The entity for which to increase the utilization.
     * @param increment The value by which the utilization should be incremented.
     */
    public void increase(Long id, int increment) {
        synchronized (this) {
            int current = retrieve(id);
            this.put(id, current + increment);
        }
    }

    /**
     * Retrieves the used quota for the provided ID or 0 if the ID is not used.
     * @param id The ID for which the quota shall be retrieved.
     * @return The quota iff id is already a key in the quota and 0 otherwise.
     */
    public int retrieve(Long id) {
        int ret = 0;
        synchronized (this) {
            if (this.containsKey(id)) {
                ret = this.get(id);
            }
        }
        return ret;
    }
}
