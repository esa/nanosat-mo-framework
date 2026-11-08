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
package esa.mo.mc.testbed.backends;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 * A backend whose integer parameter values the test can change. A parameter is
 * either fixed, with the value set by {@link #set}, or a counter, which
 * returns a new value on every read.
 */
public class MutableParameterBackend extends Backend {

    private static final int DEFAULT_VALUE = 42;

    private final Map<String, Integer> fixed = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    /**
     * Sets the value of a fixed parameter.
     *
     * @param name The name of the parameter.
     * @param value The value returned from now on.
     */
    public void set(String name, int value) {
        fixed.put(name, value);
    }

    /**
     * Makes a parameter a counter, which returns 1, 2, 3... on its reads.
     *
     * @param name The name of the parameter.
     */
    public void counter(String name) {
        counters.put(name, new AtomicInteger());
    }

    @Override
    public Attribute onGetValue(Identifier identifier) {
        String name = identifier.getValue();
        AtomicInteger counter = counters.get(name);
        if (counter != null) {
            return new Union(counter.incrementAndGet());
        }
        return new Union(fixed.getOrDefault(name, DEFAULT_VALUE));
    }

    @Override
    public void actionArrived(Identifier identifier, AttributeValueList attributeValues,
            Long executionId, MALInteraction interaction) {
        // Test backend - no actions
    }

}
