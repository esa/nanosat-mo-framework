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
package esa.mo.nmf.apps.broken;

import esa.mo.nmf.MCRegistration;
import esa.mo.nmf.MCRegistration.RegistrationMode;
import esa.mo.nmf.MonitorAndControlNMFAdapter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.ExecutionFailedException;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * The Monitor and Control adapter for the {@link BrokenApp}. Every "broken"
 * item fails deterministically on retrieval, and each is paired with a healthy
 * counterpart used as a control:
 * <ul>
 * <li>{@code Broken_Parameter} — {@code onGetValue} throws, so getValue returns
 * a ParameterValue with validity INVALID_RAW; {@code Healthy_Parameter} reads
 * fine.</li>
 * <li>{@code Broken_Action} — {@code actionArrived} throws
 * ExecutionFailedException, so the execution ends unsuccessfully;
 * {@code Healthy_Action} succeeds.</li>
 * <li>{@code Broken_Aggregation} — aggregates {@code Broken_Parameter}, so its
 * samples come back INVALID_RAW; {@code Healthy_Aggregation} aggregates
 * {@code Healthy_Parameter}.</li>
 * </ul>
 * The {@code shutdown.hang} action arms the app to block during its next
 * shutdown (see {@link BrokenApp}).
 */
public class BrokenMCAdapter extends MonitorAndControlNMFAdapter {

    private static final Logger LOGGER = Logger.getLogger(BrokenMCAdapter.class.getName());

    public static final String PARAM_HEALTHY = "Healthy_Parameter";
    public static final String PARAM_BROKEN = "Broken_Parameter";
    public static final String AGG_HEALTHY = "Healthy_Aggregation";
    public static final String AGG_BROKEN = "Broken_Aggregation";
    public static final String ACTION_HEALTHY = "Healthy_Action";
    public static final String ACTION_BROKEN = "Broken_Action";
    public static final String ACTION_SHUTDOWN_HANG = "shutdown.hang";
    public static final String ACTION_SHUTDOWN_DELAY = "shutdown.delay";

    /** Effectively indefinite: far longer than any stopApp grace period. */
    private static final long HANG_MS = 300_000;
    /** A finite, still-graceful delay used to prove a slow shutdown completes. */
    private static final long DELAY_MS = 8_000;

    // How long onClose() should block during the next shutdown. 0 = close immediately.
    private final AtomicLong shutdownDelayMs = new AtomicLong(0);

    /**
     * How long the app's {@code onClose()} should block during its next
     * shutdown, in milliseconds. Set by the {@code shutdown.hang} /
     * {@code shutdown.delay} actions; 0 means close immediately.
     *
     * @return the shutdown delay in milliseconds.
     */
    public long getShutdownDelayMs() {
        return shutdownDelayMs.get();
    }

    @Override
    public void initialRegistrations(MCRegistration registration) {
        registration.setMode(RegistrationMode.DONT_UPDATE_IF_EXISTS);

        // ------------------ Parameters ------------------
        ParameterDefinitionList healthyParams = new ParameterDefinitionList();
        healthyParams.add(new ParameterDefinition(new Identifier(PARAM_HEALTHY),
                "A parameter that always reads successfully.",
                AttributeType.DOUBLE, "unit", false, new Duration(2), null, null, false));
        LongList healthyParamIds = registration.registerParameters(healthyParams);

        ParameterDefinitionList brokenParams = new ParameterDefinitionList();
        brokenParams.add(new ParameterDefinition(new Identifier(PARAM_BROKEN),
                "A parameter whose read always throws, by design.",
                AttributeType.DOUBLE, "unit", false, new Duration(2), null, null, false));
        LongList brokenParamIds = registration.registerParameters(brokenParams);

        // ------------------ Aggregations ------------------
        AggregationDefinitionList aggs = new AggregationDefinitionList();

        AggregationDefinition healthyAgg = new AggregationDefinition(new Identifier(AGG_HEALTHY),
                "Aggregates the healthy parameter.", AggregationCategory.GENERAL,
                new Duration(0), true, false, false, new Duration(0), false,
                new AggregationParameterSetList());
        healthyAgg.getParameterSets().add(
                new AggregationParameterSet(null, healthyParamIds, new Duration(0), null));

        AggregationDefinition brokenAgg = new AggregationDefinition(new Identifier(AGG_BROKEN),
                "Aggregates the broken parameter, so its samples fail to read.", AggregationCategory.GENERAL,
                new Duration(0), true, false, false, new Duration(0), false,
                new AggregationParameterSetList());
        brokenAgg.getParameterSets().add(
                new AggregationParameterSet(null, brokenParamIds, new Duration(0), null));

        aggs.add(healthyAgg);
        aggs.add(brokenAgg);
        registration.registerAggregations(aggs);

        // ------------------ Actions ------------------
        ActionDefinitionList actions = new ActionDefinitionList();
        actions.add(new ActionDefinition(new Identifier(ACTION_HEALTHY),
                "An action that always succeeds.",
                new UShort(0), new ArgumentDefinitionList()));
        actions.add(new ActionDefinition(new Identifier(ACTION_BROKEN),
                "An action that always fails, by design.",
                new UShort(0), new ArgumentDefinitionList()));
        actions.add(new ActionDefinition(new Identifier(ACTION_SHUTDOWN_HANG),
                "Arms the app to block indefinitely during its next shutdown.",
                new UShort(0), new ArgumentDefinitionList()));
        actions.add(new ActionDefinition(new Identifier(ACTION_SHUTDOWN_DELAY),
                "Arms the app to take several seconds, but still close, during its next shutdown.",
                new UShort(0), new ArgumentDefinitionList()));
        registration.registerActions(actions);
    }

    @Override
    public Attribute onGetValue(Identifier identifier, AttributeType rawType) throws IOException {
        if (identifier == null || identifier.getValue() == null) {
            return null;
        }
        switch (identifier.getValue()) {
            case PARAM_HEALTHY:
                return (Attribute) HelperAttributes.javaType2Attribute(42.0);
            case PARAM_BROKEN:
                throw new IOException("Broken_Parameter always fails to read, by design.");
            default:
                return null;
        }
    }

    @Override
    public Boolean onSetValue(IdentifierList identifiers, ParameterRawValueList values) {
        return false; // This app does not support setting parameters.
    }

    @Override
    public void actionArrived(Identifier name, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction) throws ExecutionFailedException {
        if (name == null || name.getValue() == null) {
            throw new ExecutionFailedException("The action name is null.");
        }
        switch (name.getValue()) {
            case ACTION_HEALTHY:
                return; // success
            case ACTION_SHUTDOWN_HANG:
                shutdownDelayMs.set(HANG_MS);
                LOGGER.log(Level.WARNING, "shutdown.hang armed: this app will block indefinitely during its next shutdown.");
                return; // the hang is deferred to shutdown; the action itself succeeds
            case ACTION_SHUTDOWN_DELAY:
                shutdownDelayMs.set(DELAY_MS);
                LOGGER.log(Level.WARNING, "shutdown.delay armed: this app will take {0} ms, but still close, during its next shutdown.", DELAY_MS);
                return;
            case ACTION_BROKEN:
                throw new ExecutionFailedException("Broken_Action always fails, by design.");
            default:
                throw new ExecutionFailedException("Unknown action: " + name.getValue());
        }
    }

}
