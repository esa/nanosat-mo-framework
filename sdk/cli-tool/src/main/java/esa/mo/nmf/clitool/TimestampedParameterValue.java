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
import org.ccsds.moims.mo.mc.structures.ParameterValue;

/**
 * A parameter value paired with the timestamp at which it was recorded.
 */
public class TimestampedParameterValue {

    private final String parameterValue;
    private final Long timestamp;

    /**
     * Creates a timestamped parameter value from a {@link ParameterValue} and its timestamp.
     *
     * @param value the parameter value
     * @param timestamp the timestamp at which the value was recorded
     */
    public TimestampedParameterValue(ParameterValue value, Time timestamp) {
        this.parameterValue = String.valueOf(value.getRawValue());
        this.timestamp = timestamp.getValue();
    }

    /**
     * Returns the parameter value as a string.
     *
     * @return the parameter value
     */
    public String getParameterValue() {
        return parameterValue;
    }

    /**
     * Returns the timestamp at which the value was recorded.
     *
     * @return the timestamp, in milliseconds
     */
    public Long getTimestamp() {
        return timestamp;
    }
}
