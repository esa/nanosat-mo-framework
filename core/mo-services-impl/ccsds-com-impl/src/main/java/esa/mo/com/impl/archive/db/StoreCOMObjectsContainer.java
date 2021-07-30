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
package esa.mo.com.impl.archive.db;

import esa.mo.com.impl.archive.entities.COMObjectEntity;
import java.util.ArrayList;

/**
 * This class holds a set of COM Objects before they are committed to the
 * database.
 */
public class StoreCOMObjectsContainer {

    private final ArrayList<COMObjectEntity> perObjs;
    private final boolean continuous;

    public StoreCOMObjectsContainer(final ArrayList<COMObjectEntity> perObjs,
            final boolean continuous) {
        this.perObjs = perObjs;
        this.continuous = continuous;
    }

    public ArrayList<COMObjectEntity> getPerObjs() {
        return perObjs;
    }

    public boolean isContinuous() {
        return continuous;
    }
}
