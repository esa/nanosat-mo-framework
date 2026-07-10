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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Logger;
import opssat.simulator.util.CommandDescriptor;
import opssat.simulator.util.CommandResult;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests the deserialization allowlist installed on the simulator's TCP
 * transport: the message types the protocol uses must round-trip, while
 * classes outside the allowlist and resource-abusing graphs must be rejected.
 */
public class SimulatorSerialFilterTest {

    private static byte[] serialize(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    private static Object readFiltered(byte[] bytes) throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
        ois.setObjectInputFilter(SimulatorSerialFilter.get());
        return ois.readObject();
    }

    @Test
    public void testAllowedJdkTypesPass() throws Exception {
        Assert.assertEquals("hi", readFiltered(serialize("hi")));
        Assert.assertEquals(Integer.valueOf(7), readFiltered(serialize(7)));

        ArrayList<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        Assert.assertEquals(list, readFiltered(serialize(list)));

        Date now = new Date();
        Assert.assertEquals(now, readFiltered(serialize(now)));

        byte[] blob = {1, 2, 3};
        Assert.assertArrayEquals(blob, (byte[]) readFiltered(serialize(blob)));

        // Boxed primitives that CommandResult.output may carry
        Assert.assertEquals(Byte.valueOf((byte) 5), readFiltered(serialize((byte) 5)));
        Assert.assertEquals(Double.valueOf(1.5), readFiltered(serialize(1.5)));
        Assert.assertEquals(Boolean.TRUE, readFiltered(serialize(true)));
    }

    @Test
    public void testNonListedCollectionRejected() throws Exception {
        // HashMap is a collection but not one the protocol uses, so the tighter
        // allowlist (only ArrayList/LinkedList) must reject it.
        java.util.HashMap<String, String> map = new java.util.HashMap<>();
        map.put("k", "v");
        try {
            readFiltered(serialize(map));
            Assert.fail("A collection outside the allowlist must be rejected");
        } catch (InvalidClassException expected) {
            // Expected: HashMap is not on the allowlist
        }
    }

    @Test
    public void testSimulatorMessageTypesPass() throws Exception {
        // Every message type the protocol actually carries must round-trip
        // through the filter, so legitimate traffic is never rejected.
        Logger logger = Logger.getLogger("SimulatorSerialFilterTest");
        CommandDescriptor cd = new CommandDescriptor("intf", "body", "comment", 1, logger);

        // Client -> server: CommandDescriptor
        Assert.assertTrue(readFiltered(serialize(cd)) instanceof CommandDescriptor);

        // Server -> client: CommandResult (output holds a byte[], an allowed type)
        CommandResult result = new CommandResult(cd, new Date(), new Date());
        result.setOutput(new byte[]{1, 2, 3});
        Assert.assertTrue(readFiltered(serialize(result)) instanceof CommandResult);

        // Server -> client: the "List" response is a LinkedList of descriptors
        LinkedList<CommandDescriptor> descriptors = new LinkedList<>();
        descriptors.add(cd);
        Object list = readFiltered(serialize(descriptors));
        Assert.assertTrue(list instanceof LinkedList);
        Assert.assertTrue(((LinkedList<?>) list).get(0) instanceof CommandDescriptor);
    }

    @Test
    public void testDisallowedClassRejected() throws Exception {
        // java.io.File is Serializable but outside the allowlist
        byte[] bytes = serialize(new File("/etc/passwd"));
        try {
            readFiltered(bytes);
            Assert.fail("A class outside the allowlist must be rejected");
        } catch (InvalidClassException expected) {
            // Expected: the filter returns REJECTED
        }
    }

    @Test
    public void testOverDepthGraphRejected() throws Exception {
        // Nest allowed ArrayLists deeper than maxdepth (20) to trip the limit
        ArrayList<Object> root = new ArrayList<>();
        ArrayList<Object> current = root;
        for (int i = 0; i < 40; i++) {
            ArrayList<Object> next = new ArrayList<>();
            current.add(next);
            current = next;
        }
        try {
            readFiltered(serialize(root));
            Assert.fail("An over-depth object graph must be rejected");
        } catch (InvalidClassException expected) {
            // Expected: the filter returns REJECTED on exceeding maxdepth
        }
    }
}
