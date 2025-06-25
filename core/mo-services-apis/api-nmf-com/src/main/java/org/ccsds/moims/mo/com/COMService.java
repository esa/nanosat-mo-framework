/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO COM Java API
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
package org.ccsds.moims.mo.com;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.ServiceInfo;
import org.ccsds.moims.mo.mal.ServiceKey;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * This class is deprecated. It is only here for backward compatibility with the
 * old MAL.
 */
public abstract class COMService extends ServiceInfo {

    private final Map<Integer, COMObject> objectsByNumber = new HashMap<>();

    public COMService(final ServiceKey serviceKey, final Identifier serviceName,
            final Element[] elements, final MALOperation[] operations) {
        this(serviceKey, serviceName, elements, operations, new COMObject[0]);
    }

    public COMService(final ServiceKey serviceKey, final Identifier serviceName,
            final Element[] elements, final MALOperation[] operations, final COMObject[] comObjects) {
        super(serviceKey, serviceName, elements, operations);

        for (COMObject comObject : comObjects) {
            objectsByNumber.put(comObject.getObjectType().getNumber().getValue(), comObject);
        }
    }

    /**
     * Adds a COM object to this service specification.
     *
     * @param object The new object to add.
     * @throws java.lang.IllegalArgumentException If the argument is null.
     */
    public void addCOMObject(COMObject object) throws java.lang.IllegalArgumentException {
        if (object == null) {
            throw new IllegalArgumentException("object must not be null.");
        }
        objectsByNumber.put(object.getObjectType().getNumber().getValue(), object);
    }

    /**
     * Return an object identified by its number.
     *
     * @param opNumber The number of the object.
     * @return The found operation or null.
     * @throws java.lang.IllegalArgumentException If opNumber == null.
     */
    public COMObject getObjectByNumber(final UShort opNumber) throws IllegalArgumentException {
        if (opNumber == null) {
            throw new IllegalArgumentException("opNumber must not be null.");
        }
        return objectsByNumber.get(opNumber.getValue());
    }

    /**
     * Returns the set of objects.
     *
     * @return The set of objects or an empty array if none defined.
     */
    public COMObject[] getObjects() {
        List<COMObject> lst = new ArrayList<>(objectsByNumber.values());
        COMObject[] result = new COMObject[lst.size()];
        lst.toArray(result);
        return result;
    }
}
