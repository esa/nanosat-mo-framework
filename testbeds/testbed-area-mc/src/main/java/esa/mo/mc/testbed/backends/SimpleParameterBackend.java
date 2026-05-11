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

import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.structures.ActionDefinition;
import org.ccsds.moims.mo.mc.structures.ActionInstance;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterValue;

/**
 * A simple backend that returns a fixed integer value for every parameter.
 */
public class SimpleParameterBackend extends Backend {

    private final int value;

    public SimpleParameterBackend(int value) {
        this.value = value;
    }

    @Override
    public Attribute onGetValue(Long parameterID) {
        return new Union(value);
    }

    @Override
    public ParameterValue getValueWithCustomValidityState(Attribute rawValue, ParameterDefinition pDef) {
        return null;
    }

    @Override
    public boolean isReadOnly(Identifier name) {
        return false;
    }

    @Override
    public boolean isReadOnly(Long parameterID) {
        return false;
    }

    @Override
    public UInteger actionArrived(Identifier identifier, AttributeValueList attributeValues,
            Long actionInstanceObjId, boolean reportProgress, MALInteraction interaction) {
        return null;
    }

    @Override
    public boolean preCheck(ActionDefinition defDetails, ActionInstance instDetails, UIntegerList errorList) {
        return true;
    }

}
