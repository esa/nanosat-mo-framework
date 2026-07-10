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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InvalidClassException;
import java.io.ObjectInputFilter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import opssat.simulator.util.CommandDescriptor;
import opssat.simulator.util.CommandResult;
import opssat.simulator.util.SimulatorData;
import opssat.simulator.util.SimulatorHeader;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the deserialization allowlists on the simulator's TCP transport. The
 * server filter (untrusted client input) is strict; the client filter (input
 * from a chosen server) allows the simulator's own DTO family. Both must reject
 * classes outside their allowlist and resource-abusing graphs.
 */
public class SimulatorSerialFilterTest {

    private static final Logger LOG = Logger.getLogger("SimulatorSerialFilterTest");

    private static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    private static Object read(Object obj, ObjectInputFilter filter) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(serialize(obj)));
        ois.setObjectInputFilter(filter);
        return ois.readObject();
    }

    private static void assertAllowed(Object obj, ObjectInputFilter filter) throws Exception {
        Assert.assertNotNull("must be allowed: " + obj.getClass().getName(), read(obj, filter));
    }

    private static void assertRejected(Object obj, ObjectInputFilter filter) throws Exception {
        try {
            read(obj, filter);
            Assert.fail("must be rejected: " + obj.getClass().getName());
        } catch (InvalidClassException expected) {
            // Expected: filter returns REJECTED
        }
    }

    private static CommandDescriptor descriptor() {
        return new CommandDescriptor("intf", "body", "comment", 1, LOG);
    }

    // Server filter — strict: only the command types a client may send

    @Test
    public void testServerFilterAllowsClientMessages() throws Exception {
        ObjectInputFilter f = SimulatorSerialFilter.serverFilter();
        assertAllowed("List", f);
        assertAllowed(Integer.valueOf(0), f);
        assertAllowed(descriptor(), f);
        Assert.assertEquals("hi", read("hi", f));
    }

    @Test
    public void testServerFilterRejectsTelemetryAndForeignTypes() throws Exception {
        ObjectInputFilter f = SimulatorSerialFilter.serverFilter();
        // Telemetry DTOs are server->client only; a client must not send them
        assertRejected(new SimulatorHeader(), f);
        assertRejected(new SimulatorData(0), f);
        // Classes outside the allowlist
        assertRejected(new File("/etc/passwd"), f);
        assertRejected(new HashMap<>(), f);
    }

    // Client filter — allows the simulator's own DTO family

    @Test
    public void testClientFilterAllowsSimulatorDtos() throws Exception {
        ObjectInputFilter f = SimulatorSerialFilter.clientFilter();
        assertAllowed(new SimulatorHeader(), f);
        assertAllowed(new SimulatorData(0), f);
        assertAllowed(descriptor(), f);

        CommandResult result = new CommandResult(descriptor(), new Date(), new Date());
        result.setOutput(new byte[]{1, 2, 3});
        assertAllowed(result, f);

        LinkedList<CommandDescriptor> list = new LinkedList<>();
        list.add(descriptor());
        assertAllowed(list, f);
    }

    @Test
    public void testClientFilterStillRejectsForeignClasses() throws Exception {
        ObjectInputFilter f = SimulatorSerialFilter.clientFilter();
        // A class outside the simulator's packages must still be rejected
        assertRejected(new File("/etc/passwd"), f);
    }

    // The platform configuration (Properties) flows in both directions

    @Test
    public void testPlatformPropertiesAllowedBothDirections() throws Exception {
        java.util.Properties config = new java.util.Properties();
        config.setProperty("platform.mode", "sim");
        config.setProperty("camerasim.imagemode", "Fixed");
        assertAllowed(config, SimulatorSerialFilter.serverFilter());
        assertAllowed(config, SimulatorSerialFilter.clientFilter());
    }

    // The client tolerates the large command catalog; the server does not

    @Test
    public void testHighReferenceCountAllowedByClientRejectedByServer() throws Exception {
        // Mirrors the "List" response: the catalog's CommandDescriptors share
        // objects, producing thousands of back-references (which is what the
        // maxrefs limit counts). A repeated element reproduces that here.
        LinkedList<Object> catalog = new LinkedList<>();
        String shared = "shared-command";
        for (int i = 0; i < 3000; i++) {
            catalog.add(shared);
        }
        assertAllowed(catalog, SimulatorSerialFilter.clientFilter());
        assertRejected(catalog, SimulatorSerialFilter.serverFilter());
    }

    // Resource limits apply to both filters

    @Test
    public void testOverDepthGraphRejected() throws Exception {
        ArrayList<Object> root = new ArrayList<>();
        ArrayList<Object> current = root;
        for (int i = 0; i < 40; i++) {
            ArrayList<Object> next = new ArrayList<>();
            current.add(next);
            current = next;
        }
        assertRejected(root, SimulatorSerialFilter.serverFilter());
        assertRejected(root, SimulatorSerialFilter.clientFilter());
    }
}
