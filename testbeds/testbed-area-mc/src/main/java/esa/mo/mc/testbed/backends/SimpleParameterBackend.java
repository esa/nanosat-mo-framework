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
package esa.mo.mc.testbed.backends;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ParameterRawValueList;

/**
 * A simple backend that returns a fixed integer value for every parameter, and
 * that accepts every value set, except for the parameters it was told to
 * refuse.
 */
public class SimpleParameterBackend extends Backend {

    private final int value;
    private final Set<String> refused = ConcurrentHashMap.newKeySet();

    public SimpleParameterBackend(int value) {
        this.value = value;
    }

    @Override
    public Attribute onGetValue(Identifier identifier) {
        return new Union(value);
    }

    /**
     * Makes the backend refuse the values set on a parameter.
     *
     * @param name The name of the parameter.
     */
    public void refuse(String name) {
        refused.add(name);
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        for (Identifier identifier : identifiers) {
            if (refused.contains(identifier.getValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void actionArrived(Identifier identifier, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction) throws ExecutionFailedException {
        // Test backend - actions always succeed
    }

}
