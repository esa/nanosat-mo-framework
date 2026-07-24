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

import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mc.structures.AggregationValue;

/**
 * An aggregation value paired with the timestamp at which it was recorded.
 */
public class TimestampedAggregationValue {

    private final AggregationValue aggregationValue;
    private final Time timestamp;

    /**
     * Creates a timestamped aggregation value.
     *
     * @param value the aggregation value
     * @param timestamp the timestamp at which the value was recorded
     */
    public TimestampedAggregationValue(AggregationValue value, Time timestamp) {
        this.aggregationValue = value;
        this.timestamp = timestamp;
    }

    /**
     * Returns the aggregation value.
     *
     * @return the aggregation value
     */
    public AggregationValue getAggregationValue() {
        return aggregationValue;
    }

    /**
     * Returns the timestamp at which the value was recorded.
     *
     * @return the timestamp
     */
    public Time getTimestamp() {
        return timestamp;
    }
}
