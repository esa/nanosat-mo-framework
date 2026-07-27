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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Deserialization allowlists for the simulator's TCP transport.
 *
 * <p>
 * The simulator exchanges Java-serialized objects between the server
 * ({@link MultiThreadedSocketServer}) and the GUI client ({@link SocketClient}).
 * Calling {@code readObject()} on a raw socket stream without restriction is an
 * unsafe-deserialization risk (CWE-502): a hostile peer can drive resolution of
 * attacker-chosen classes (remote code execution if a gadget chain is on the
 * classpath) or exhaust resources with a hostile object graph. A filter is
 * installed on every {@link java.io.ObjectInputStream} the transport uses.
 *
 * <p>
 * The two directions have different threat models, so they use different
 * allowlists:
 * <ul>
 * <li>{@link #serverFilter()} — applied to input from clients, which are
 * untrusted network peers. It is <strong>strict</strong>: only the message types
 * a client legitimately sends (the command types) plus the scalar and collection
 * JDK types they carry.</li>
 * <li>{@link #clientFilter()} — applied by the GUI to input from the server it
 * connected to. The server streams the simulator's whole telemetry/data DTO
 * family, so this allows the simulator's own packages
 * ({@code opssat.simulator.**}) plus the JDK types. Deserialization only resolves
 * these names against the client's own classpath — it never defines classes from
 * the incoming bytes — so an allowed name can only ever resolve to a class
 * already shipped with the simulator, never to attacker-supplied code; the
 * external packages where real gadget chains live remain rejected.</li>
 * </ul>
 *
 * <p>
 * Both filters also cap stream depth, reference count, array length and total
 * byte size to bound denial-of-service, and log the offending class (or exceeded
 * limit) on rejection so a blocked message is diagnosable.
 *
 * @author Cesar Coelho
 */
public final class SimulatorSerialFilter {

    private static final Logger LOGGER = Logger.getLogger(SimulatorSerialFilter.class.getName());

    /**
     * Resource limits for the strict server filter (denial-of-service bounds).
     * A client only sends small individual commands, so a low reference count
     * is ample.
     *
     * <p>
     * <strong>Note:</strong> the JDK enforces {@code maxrefs} and {@code maxbytes}
     * <em>cumulatively over the whole stream</em>, not per message. The server
     * keeps a single {@link java.io.ObjectInputStream} open for a client's entire
     * connection, so these counters grow with every message and will eventually
     * be reached on a long-lived connection.
     */
    private static final long SERVER_MAX_DEPTH = 20;
    private static final long SERVER_MAX_REFS = 2000;
    private static final long SERVER_MAX_BYTES = 5_000_000;
    private static final long SERVER_MAX_ARRAY = 100_000;
    private static final String SERVER_LIMITS = String.join(";",
            "maxdepth=" + SERVER_MAX_DEPTH,
            "maxrefs=" + SERVER_MAX_REFS,
            "maxbytes=" + SERVER_MAX_BYTES,
            "maxarray=" + SERVER_MAX_ARRAY);

    /**
     * Resource limits for the client filter. The server sends the whole command
     * catalog in a single "List" message — many small CommandDescriptors — which
     * legitimately holds thousands of object references, so {@code maxrefs} is
     * much higher than on the server. {@code maxbytes} still bounds the total
     * amount deserialized.
     */
    private static final long CLIENT_MAX_DEPTH = 20;
    private static final long CLIENT_MAX_REFS = 1_000_000;
    private static final long CLIENT_MAX_BYTES = 5_000_000;
    private static final long CLIENT_MAX_ARRAY = 100_000;
    private static final String CLIENT_LIMITS = String.join(";",
            "maxdepth=" + CLIENT_MAX_DEPTH,
            "maxrefs=" + CLIENT_MAX_REFS,
            "maxbytes=" + CLIENT_MAX_BYTES,
            "maxarray=" + CLIENT_MAX_ARRAY);

    /**
     * The JDK scalar types the protocol carries. {@code Number} is required
     * because the boxed numeric types extend it (the filter checks the whole
     * serializable superclass chain); {@code Object} is required because
     * {@code ArrayList}/{@code LinkedList} run a filter check on their backing
     * {@code Object[]} array. Each element is still filtered individually.
     */
    private static final String JDK_SCALARS = String.join(";",
            "java.lang.String",
            "java.lang.Boolean",
            "java.lang.Character",
            "java.lang.Byte",
            "java.lang.Short",
            "java.lang.Integer",
            "java.lang.Long",
            "java.lang.Float",
            "java.lang.Double",
            "java.lang.Number",
            "java.lang.Object");

    /**
     * Strict allowlist for input from untrusted clients: only the command
     * message types a client legitimately sends, plus the JDK scalar and
     * collection types they carry. No telemetry DTOs, no package wildcard.
     */
    private static final String SERVER_PATTERN = String.join(";",
            SERVER_LIMITS,
            "opssat.simulator.util.CommandDescriptor",
            "opssat.simulator.util.CommandResult",
            "opssat.simulator.util.ArgumentDescriptor",
            "opssat.simulator.util.ArgumentTemplate",
            JDK_SCALARS,
            "java.util.ArrayList",
            "java.util.LinkedList",
            "java.util.Date",
            // The GUI sends the platform configuration up as a Properties
            // (Hashtable is its serializable superclass); keys/values are Strings
            "java.util.Properties",
            "java.util.Hashtable",
            // Hashtable's deserialization runs an array check on its internal
            // Map.Entry[] (component java.util.Map$Entry), like ArrayList's Object[]
            "java.util.Map$Entry",
            "!*");

    /**
     * Allowlist for input the GUI receives from its chosen server: the
     * simulator's own DTO family (telemetry, scheduler, device and command
     * data live across {@code opssat.simulator.**}) plus the JDK types they
     * carry.
     */
    private static final String CLIENT_PATTERN = String.join(";",
            CLIENT_LIMITS,
            "opssat.simulator.**",
            JDK_SCALARS,
            "java.util.ArrayList",
            "java.util.LinkedList",
            "java.util.HashMap",
            "java.util.Date",
            // The server sends the platform configuration down as a Properties
            "java.util.Properties",
            "java.util.Hashtable",
            // Hashtable's deserialization runs an array check on its internal
            // Map.Entry[] (component java.util.Map$Entry), like ArrayList's Object[]
            "java.util.Map$Entry",
            "!*");

    private static final ObjectInputFilter SERVER_FILTER
            = logging(ObjectInputFilter.Config.createFilter(SERVER_PATTERN), "server",
                    SERVER_MAX_DEPTH, SERVER_MAX_REFS, SERVER_MAX_BYTES, SERVER_MAX_ARRAY);
    private static final ObjectInputFilter CLIENT_FILTER
            = logging(ObjectInputFilter.Config.createFilter(CLIENT_PATTERN), "client",
                    CLIENT_MAX_DEPTH, CLIENT_MAX_REFS, CLIENT_MAX_BYTES, CLIENT_MAX_ARRAY);

    /**
     * The description of the most recent rejection (class name, or the limit
     * exceeded). Best-effort diagnostic so a caller can report <em>why</em> a
     * stream was rejected; the JDK's {@code InvalidClassException} only says
     * "filter status: REJECTED".
     */
    private static volatile String lastRejection;

    private SimulatorSerialFilter() {
    }

    /**
     * Returns the strict filter for input from untrusted clients. Apply it with
     * {@code objectInputStream.setObjectInputFilter(serverFilter())} right after
     * constructing the stream.
     *
     * @return The server-side allowlist filter.
     */
    public static ObjectInputFilter serverFilter() {
        return SERVER_FILTER;
    }

    /**
     * Returns the filter the GUI applies to input from its server. Apply it with
     * {@code objectInputStream.setObjectInputFilter(clientFilter())} right after
     * constructing the stream.
     *
     * @return The client-side allowlist filter.
     */
    public static ObjectInputFilter clientFilter() {
        return CLIENT_FILTER;
    }

    /**
     * Returns a description of the most recent deserialization rejection, or
     * {@code null} if nothing has been rejected yet.
     *
     * @return The last rejected class name (or exceeded limit), best-effort.
     */
    public static String lastRejection() {
        return lastRejection;
    }

    /**
     * Wraps a filter so a rejection records and logs the true reason, instead of
     * a bare "filter status: REJECTED".
     *
     * @param delegate the underlying pattern filter.
     * @param side "server" or "client", for the message.
     * @param maxDepth the configured maxdepth limit.
     * @param maxRefs the configured maxrefs limit.
     * @param maxBytes the configured maxbytes limit.
     * @param maxArray the configured maxarray limit.
     * @return the wrapped, self-diagnosing filter.
     */
    private static ObjectInputFilter logging(ObjectInputFilter delegate, String side,
            long maxDepth, long maxRefs, long maxBytes, long maxArray) {
        return info -> {
            ObjectInputFilter.Status status = delegate.checkInput(info);
            if (status == ObjectInputFilter.Status.REJECTED) {
                String reason = describeRejection(info, maxDepth, maxRefs, maxBytes, maxArray);
                lastRejection = reason;
                LOGGER.log(Level.WARNING,
                        "Simulator {0} deserialization filter REJECTED a stream. Reason: {1}",
                        new Object[]{side, reason});
            }
            return status;
        };
    }

    /**
     * Works out why the filter rejected the stream and returns a human-readable
     * explanation.
     *
     * <p>
     * A resource-limit breach must be detected explicitly, <strong>not</strong>
     * inferred from {@code serialClass()}: when the JDK filter trips a limit it
     * returns REJECTED with {@code serialClass()} still set to whatever class was
     * being read at that moment. Trusting {@code serialClass()} therefore
     * misattributes a limit breach to an (often allowlisted) class — e.g.
     * reporting "rejected class java.lang.Integer" when the real cause is that
     * the cumulative {@code maxrefs}/{@code maxbytes} of a long-lived stream was
     * reached. The configured limits are compared against the reported counters
     * first, and only a genuine allowlist miss is reported as a class rejection.
     * The full counter snapshot is always included so the cause is diagnosable.
     */
    private static String describeRejection(ObjectInputFilter.FilterInfo info,
            long maxDepth, long maxRefs, long maxBytes, long maxArray) {
        StringBuilder limits = new StringBuilder();
        if (maxDepth > 0 && info.depth() > maxDepth) {
            appendReason(limits, "maxdepth exceeded (" + info.depth() + " > " + maxDepth + ")");
        }
        if (maxRefs > 0 && info.references() > maxRefs) {
            appendReason(limits, "maxrefs exceeded (" + info.references() + " > " + maxRefs + ")");
        }
        if (maxBytes > 0 && info.streamBytes() > maxBytes) {
            appendReason(limits, "maxbytes exceeded (" + info.streamBytes() + " > " + maxBytes + ")");
        }
        if (maxArray >= 0 && info.arrayLength() > maxArray) {
            appendReason(limits, "maxarray exceeded (" + info.arrayLength() + " > " + maxArray + ")");
        }

        final Class<?> clazz = info.serialClass();
        final String snapshot = " [depth=" + info.depth() + ", refs=" + info.references()
                + ", bytes=" + info.streamBytes() + ", arrayLength=" + info.arrayLength()
                + (clazz != null ? ", class=" + clazz.getName() : "") + "]";

        if (limits.length() != 0) {
            return "resource limit reached: " + limits + snapshot;
        }
        if (clazz != null) {
            return "class not in allowlist: " + clazz.getName()
                    + " (add it to SimulatorSerialFilter if it is a legitimate message type)" + snapshot;
        }
        return "no allowlist pattern matched" + snapshot;
    }

    private static void appendReason(StringBuilder sb, String reason) {
        if (sb.length() != 0) {
            sb.append("; ");
        }
        sb.append(reason);
    }
}
