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
package esa.mo.nmf;

import java.util.logging.Logger;
import esa.mo.mp.impl.api.MPArchiveManager;

/**
 * The MissionPlanningNMFAdapter
 *
 */
public abstract class MissionPlanningNMFAdapter {

    private static final java.util.logging.Logger LOGGER = Logger.getLogger(MissionPlanningNMFAdapter.class.getName());

    private MPArchiveManager archiveManager = null;

    public void setArchiveManager(MPArchiveManager archiveManager) {
        this.archiveManager = archiveManager;
    }

    public MPArchiveManager getArchiveManager() {
        return this.archiveManager;
    }

    public abstract void initialRegistrations(MPRegistration registration);
}
