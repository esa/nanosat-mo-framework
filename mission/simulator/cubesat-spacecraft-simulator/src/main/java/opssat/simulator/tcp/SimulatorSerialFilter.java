/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2026      European Space Agency
 *                          European Space Operations Centre
 *                          Darmstadt
 *                          Germany
 *  ----------------------------------------------------------------------------
 *  System                : ESA NanoSat MO Framework
 *  ----------------------------------------------------------------------------
 *  Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 *  You may not use this file except in compliance with the License.
 *
 *  Except as expressly set forth in this License, the Software is provided to
 *  You on an "as is" basis and without warranties of any kind, including without
 *  limitation merchantability, fitness for a particular purpose, absence of
 *  defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  ----------------------------------------------------------------------------
 */
package opssat.simulator.tcp;

import java.io.ObjectInputFilter;

/**
 * A deserialization allowlist for the simulator's TCP transport.
 *
 * <p>
 * The simulator exchanges Java-serialized objects between the server
 * ({@link MultiThreadedSocketServer}) and the GUI client ({@link SocketClient}).
 * Calling {@code readObject()} on a raw socket stream without restriction is an
 * unsafe-deserialization risk (CWE-502): a hostile peer can drive resolution of
 * attacker-chosen classes (remote code execution if a gadget chain is on the
 * classpath) or exhaust resources with a hostile object graph. This filter is
 * installed on every {@link java.io.ObjectInputStream} used by the transport to
 * bound both risks.
 *
 * <p>
 * The filter is an allowlist of explicit classes (no package wildcards): the
 * four simulator message types actually sent over the wire
 * ({@code CommandDescriptor}, {@code CommandResult}, {@code ArgumentDescriptor},
 * {@code ArgumentTemplate}), the boxed-primitive types and {@code String}, and
 * the two collection types plus {@code Date} the protocol carries. Everything
 * else is rejected. It also caps stream depth, reference count, array length and
 * total byte size to bound denial-of-service.
 *
 * <p>
 * Because {@code CommandResult.output} (and {@code ArgumentDescriptor.type}) are
 * declared {@code Object}, the concrete type each actually carries must be on
 * this list; the common cases — boxed primitives, {@code String}, and primitive
 * arrays such as {@code byte[]} (always permitted by the JVM filter) — are
 * covered. If a new message or output type is introduced, add its exact class to
 * {@link #PATTERN}, otherwise it will be rejected at deserialization time.
 *
 * <p>
 * Note that deserialization resolves these class <em>names</em> against the
 * server's own classpath; it never defines classes from the incoming bytes, so
 * an allowed name can only ever resolve to a class already shipped with the
 * simulator, not to attacker-supplied code.
 *
 * @author Cesar Coelho
 */
public final class SimulatorSerialFilter {

    /**
     * The allowlist pattern, in {@link ObjectInputFilter.Config#createFilter}
     * syntax: resource limits, then the allowed types, then a trailing
     * {@code !*} that rejects anything not explicitly allowed. Primitive types
     * and primitive arrays are always permitted by the JVM filter and need no
     * token here.
     */
    private static final String PATTERN = String.join(";",
            // Resource limits (denial-of-service bounds). Kept generous so the
            // full command-catalog "List" response is not false-rejected.
            "maxdepth=20",
            "maxrefs=2000",
            "maxbytes=5000000",
            "maxarray=100000",
            // The simulator's own serializable message types (explicit, no
            // package wildcard: only these four travel on the wire)
            "opssat.simulator.util.CommandDescriptor",
            "opssat.simulator.util.CommandResult",
            "opssat.simulator.util.ArgumentDescriptor",
            "opssat.simulator.util.ArgumentTemplate",
            // The JDK scalar types the protocol carries (boxed primitives +
            // String; covers the Object-typed CommandResult.output values)
            "java.lang.String",
            "java.lang.Boolean",
            "java.lang.Character",
            "java.lang.Byte",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Float",
            "java.lang.Double",
            // The filter checks the whole serializable superclass chain, and the
            // boxed numeric types (Integer, Byte, ...) extend the serializable
            // java.lang.Number, so it must be allowed too.
            "java.lang.Number",
            // Required for ArrayList/LinkedList: their deserialization runs a
            // filter check on the backing Object[] array (component java.lang.
            // Object). Each element is still filtered individually, so allowing
            // Object does not widen what may actually be instantiated.
            "java.lang.Object",
            // The collection and date types the protocol carries
            "java.util.ArrayList",
            "java.util.LinkedList",
            "java.util.Date",
            // Reject everything else
            "!*");

    private static final ObjectInputFilter FILTER = ObjectInputFilter.Config.createFilter(PATTERN);

    private SimulatorSerialFilter() {
    }

    /**
     * Returns the shared deserialization filter for the simulator transport.
     * Apply it with {@code objectInputStream.setObjectInputFilter(get())}
     * immediately after constructing the stream.
     *
     * @return The allowlist {@link ObjectInputFilter}.
     */
    public static ObjectInputFilter get() {
        return FILTER;
    }
}
