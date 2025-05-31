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
package esa.mo.nmf.commonmoadapter;

import esa.mo.mc.impl.provider.AggregationInstance;

/**
 * An abstract class that passes the received data from the Aggregation service
 * coming via the monitorValue operation
 *
 * @author Phil Brabbin
 */
public interface CompleteAggregationReceivedListener extends DataReceivedListener {

    /**
     * This interface must be implemented in order to receive the parameter
     * content from the Aggregation service coming via the monitorValue operation
     *
     * @param aggregationInstance
     */
    void onDataReceived(AggregationInstance aggregationInstance);

}
