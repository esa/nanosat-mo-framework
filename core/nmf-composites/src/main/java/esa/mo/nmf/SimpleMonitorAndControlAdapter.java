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

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ParameterRawValueList;

/**
 * The SimpleMonitorAndControlAdapter extends the MonitorAndControlAdapter and
 * implements the SimpleMonitorAndControlListener to facilitate receiving
 * actions and set/get parameters from an external software entity in a
 * simplified way. The parameter and action values are the Java types that map
 * to the MO attribute types. Arbitrary serializable objects are not supported:
 * the adapter never serializes an object into, or deserializes one from, a MAL
 * Blob, so that untrusted bytes are never passed to Java deserialization.
 *
 */
public abstract class SimpleMonitorAndControlAdapter extends MonitorAndControlNMFAdapter implements SimpleMonitorAndControlListener {

    /**
     * Default constructor.
     */
    public SimpleMonitorAndControlAdapter() {
    }

    @Override
    public void actionArrived(Identifier identifier, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction) throws ExecutionFailedException {
        Serializable[] values = new Serializable[attributeValues.size()];

        for (int i = 0; i < attributeValues.size(); i++) {
            values[i] = attributeValues.get(i);
        }

        final boolean success = this.actionArrivedSimple(identifier.getValue(), values, executionId);
        if (!success) {
            throw new ExecutionFailedException("Action execution failed: " + identifier.getValue());
        }
    }

    @Override
    public Attribute onGetValue(Identifier identifier) {
        Serializable ret = this.onGetValueSimple(identifier.getValue());

        if (ret instanceof Attribute || ret == null) {
            return (Attribute) ret;
        }

        // Convert the Java type into its MO attribute type.
        Object moType = Attribute.javaType2Attribute(ret);
        if (moType instanceof Attribute) {
            return (Attribute) moType;
        }

        Logger.getLogger(SimpleMonitorAndControlAdapter.class.getName()).log(Level.SEVERE,
                "The value of parameter {0} is not an MO attribute type and cannot be returned. "
                + "Use an MO attribute type for the parameter value.", identifier.getValue());
        return null;
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        if (identifiers.isEmpty() || values.isEmpty()) { // Validation
            return false;
        }

        final Identifier identifier = identifiers.get(0);
        final Attribute value = values.get(0).getRawValue();

        // Convert the MO attribute type into its Java type.
        Serializable obj = (Serializable) Attribute.attribute2JavaType(value);

        return this.onSetValueSimple(identifier.getValue(), obj);
    }

}
