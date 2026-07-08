/* ----------------------------------------------------------------------------
 * Copyright (C) 2026      European Space Agency
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
package esa.mo.nmf.mcadapters;

import esa.mo.mc.impl.interfaces.ActionNotFoundException;
import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.AttributeType;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ParameterDefinition;
import org.ccsds.moims.mo.mc.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.structures.ParameterValue;

/**
 * Aggregates several {@link MonitorAndControlNMFAdapter}s behind a single
 * adapter, so a Supervisor can expose a default MC set together with the
 * mission-specific one. The MC services see one listener; this composite
 * forwards each callback to its children.
 *
 * <p>
 * Callbacks are dispatched by simply forwarding to every child: parameter
 * reads return the first non-null answer, sets are offered to all children
 * (each handles the names it owns), and an action is offered to each child
 * until one accepts it — a child signals "not mine" with an
 * {@link ActionNotFoundException}, which is caught here so the next child is
 * tried.
 *
 * <p>
 * The child adapters must be name-based (override
 * {@link #onGetValue(Identifier, AttributeType)}), as the manual adapters are;
 * annotation-based (id-based) adapters cannot be composed.
 *
 * @author Cesar Coelho
 */
public class CompositeMCAdapter extends MonitorAndControlNMFAdapter {

    private final List<MonitorAndControlNMFAdapter> children;

    /**
     * Constructor.
     *
     * @param children The adapters to aggregate, in priority order.
     */
    public CompositeMCAdapter(List<MonitorAndControlNMFAdapter> children) {
        this.children = new ArrayList<>(children);
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        for (MonitorAndControlNMFAdapter child : children) {
            child.initialRegistrations(registration);
        }
    }

    @Override
    public void restoreParameterValuesFromArchive() {
        for (MonitorAndControlNMFAdapter child : children) {
            child.restoreParameterValuesFromArchive();
        }
    }

    @Override
    public Attribute onGetValue(Identifier identifier, AttributeType rawType) throws IOException {
        for (MonitorAndControlNMFAdapter child : children) {
            Attribute value = child.onGetValue(identifier, rawType);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        boolean anySet = false;
        for (MonitorAndControlNMFAdapter child : children) {
            anySet = Boolean.TRUE.equals(child.onSetValue(identifiers, values)) || anySet;
        }
        return anySet;
    }

    @Override
    public ParameterValue getValueWithCustomValidityState(Attribute rawValue, ParameterDefinition pDef) {
        for (MonitorAndControlNMFAdapter child : children) {
            ParameterValue value = child.getValueWithCustomValidityState(rawValue, pDef);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    @Override
    public void actionArrived(Identifier name, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction)
            throws ExecutionFailedException, ActionNotFoundException {
        for (MonitorAndControlNMFAdapter child : children) {
            try {
                child.actionArrived(name, attributeValues, executionId, interaction);
                return; // handled by this child
            } catch (ActionNotFoundException ex) {
                // Not this child's action; try the next one
            }
        }
        throw new ActionNotFoundException(name == null ? null : name.getValue());
    }
}
