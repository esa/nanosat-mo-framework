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

import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValue;

/**
 *
 * @author Phil Brabbin
 */
public class AggregationInstance {

    private final Identifier name;
    private final AggregationValue aggregationValue;
    private final ObjectId source;
    private final Time timestamp;

    public AggregationInstance(final Identifier name, final AggregationValue pValue, final ObjectId source,
        final Time timestamp) {
        this.name = name;
        this.aggregationValue = pValue;
        this.source = source;
        this.timestamp = timestamp;
    }

    public Identifier getName() {
        return this.name;
    }

    public AggregationValue getAggregationValue() {
        return this.aggregationValue;
    }

    public ObjectId getSource() {
        return this.source;
    }

    public Time getTimestamp() {
        return this.timestamp;
    }

}
