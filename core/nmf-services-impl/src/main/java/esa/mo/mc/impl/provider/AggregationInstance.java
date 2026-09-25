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
package esa.mo.mc.impl.provider;

import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mc.structures.AggregationValue;

/**
 * An aggregation instance holding a set of sampled parameter values.
 *
 * @author Phil Brabbin
 */
public class AggregationInstance {

    private final Identifier name;
    private final AggregationValue aggregationValue;
    private final ObjectKey source;
    private final Time timestamp;

    /**
     * Creates a new {@code AggregationInstance}.
     *
     * @param name the name
     * @param pValue the p value
     * @param source the source
     * @param timestamp the timestamp
     */
    public AggregationInstance(final Identifier name, final AggregationValue pValue,
            final ObjectKey source, final Time timestamp) {
        this.name = name;
        this.aggregationValue = pValue;
        this.source = source;
        this.timestamp = timestamp;
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public Identifier getName() {
        return this.name;
    }

    /**
     * Returns the aggregation value.
     *
     * @return the aggregation value
     */
    public AggregationValue getAggregationValue() {
        return this.aggregationValue;
    }

    /**
     * Returns the source.
     *
     * @return the source
     */
    public ObjectKey getSource() {
        return this.source;
    }

    /**
     * Returns the timestamp.
     *
     * @return the timestamp
     */
    public Time getTimestamp() {
        return this.timestamp;
    }

}
