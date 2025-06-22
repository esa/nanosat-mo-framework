/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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
package esa.mo.nmf.clitool;

import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValue;

public class TimestampedAggregationValue {

    private final AggregationValue aggregationValue;
    private final FineTime timestamp;

    public TimestampedAggregationValue(AggregationValue value, FineTime timestamp) {
        this.aggregationValue = value;
        this.timestamp = timestamp;
    }

    public AggregationValue getAggregationValue() {
        return aggregationValue;
    }

    public FineTime getTimestamp() {
        return timestamp;
    }
}
