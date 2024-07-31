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
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

public class TimestampedParameterValue {

    private final String parameterValue;
    private final Long timestamp;

    public TimestampedParameterValue(ParameterValue value, FineTime timestamp) {
        this.parameterValue = String.valueOf(value.getRawValue());
        this.timestamp = timestamp.getValue();
    }

    public String getParameterValue() {
        return parameterValue;
    }

    public Long getTimestamp() {
        return timestamp;
    }
}
