/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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

import esa.mo.helpertools.helpers.HelperAttributes;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * The SimpleMonitorAndControlAdapter extends the MonitorAndControlAdapter and
 * implements the SimpleMonitorAndControlListener to facilitate receiving
 * actions and set/get parameters from an external software entity in a
 * simplified way. This adapter allows the parameter values to be directly Java
 * types and also serializable objects. The adapter automatically serializes an
 * object and puts it inside a MAL Blob type (a byte container).
 *
 */
public abstract class SimpleMonitorAndControlAdapter extends MonitorAndControlNMFAdapter implements SimpleMonitorAndControlListener {

    @Override
    public void initialRegistrations(MCRegistration registrationObject) {
        // To be overwritten
    }

    @Override
    public UInteger actionArrived(Identifier identifier, AttributeValueList attributeValues, 
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        Serializable[] values = new Serializable[attributeValues.size()];

        for (int i = 0; i < attributeValues.size(); i++) {
            AttributeValue attributeValue = attributeValues.get(i);

            if (attributeValue.getValue() instanceof Blob) {
                try {
                    values[i] = HelperAttributes.blobAttribute2serialObject((Blob) attributeValue.getValue());
                } catch (IOException ex) {
                    values[i] = attributeValue; // It didn't work? Maybe it really just a Blob (not a serialized object)
                }
            } else {
                values[i] = attributeValue;
            }
        }

        final Boolean success = this.actionArrivedSimple(identifier.getValue(), values, actionInstanceObjId);

        if (success) {
            return new UInteger(0);
        } else {
            return new UInteger(1);
        }
    }

    @Override
    public Attribute onGetValue(Identifier identifier, Byte rawType) {

        Serializable ret = this.onGetValueSimple(identifier.getValue());

        if (ret instanceof Attribute) {
            return (Attribute) ret;
        } else {
            // First try to convert it into a MO type...
            Object moType = (Object) HelperAttributes.javaType2Attribute(ret);

            if (moType instanceof Attribute) { // Was it succcessfully converted from Java to Attribute?
                return (Attribute) moType;
            }

            try { // Thy to serialize the object and put it inside a Blob
                return (Attribute) HelperAttributes.serialObject2blobAttribute(ret);
            } catch (IOException ex) {
                Logger.getLogger(SimpleMonitorAndControlAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    @Override
    public Boolean onSetValue(Identifier identifier, Attribute value) {

        Serializable obj;

        if (value instanceof Blob) {
            // Try to unserialize it!
            try {
                obj = HelperAttributes.blobAttribute2serialObject((Blob) value);
                return this.onSetValueSimple(identifier.getValue(), obj); // Success!
            } catch (IOException ex) {
                obj = value; // It didn't work? Maybe it really just a Blob (not a serialized object)
            }
        }

        // Convert it to java type
        obj = (Serializable) HelperAttributes.attribute2JavaType(value);

        return this.onSetValueSimple(identifier.getValue(), obj);
    }

}
