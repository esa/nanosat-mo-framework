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
package esa.mo.nmf.testbed.simulator;

import esa.mo.nmf.mission.orekit.simulator.util.CommandDescriptor;
import esa.mo.nmf.mission.orekit.simulator.util.CommandResult;
import esa.mo.nmf.mission.orekit.simulator.util.PlatformMessage;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorData;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorHeader;
import java.util.LinkedList;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;

/**
 * What the simulator does with what a client sends it: a command returns its
 * result to the client that sent it, and a changed setting, header or time
 * factor is applied. The tests go through the simulator's own server and the
 * client of its GUI, and check what arrives, not how it is encoded.
 */
public class SimulatorCommandsTest {

    private static final long TIMEOUT_MS = 30_000;

    @Test
    public void aCommandReturnsItsResultToTheClientThatSentIt() throws Exception {
        try (TestClient client = new TestClient(Simulator.port());
                TestClient other = new TestClient(Simulator.port())) {
            LinkedList<?> list = client.await(LinkedList.class, Messages::isCommandList, TIMEOUT_MS);
            Assert.assertNotNull("The client must receive the list of commands", list);
            CommandDescriptor command = Messages.theLastKnownPositionCommand(list);
            Assert.assertNotNull("The list must hold the GPS command getLastKnownPosition", command);
            Assert.assertNotNull(other.await(SimulatorData.class, data -> true, TIMEOUT_MS));
            System.out.println("The client runs " + command);

            client.send(command);

            CommandResult result = client.await(CommandResult.class,
                    r -> command.getMethodBody().equals(r.getCommandDescriptor().getMethodBody()), TIMEOUT_MS);
            Assert.assertNotNull("The client must receive the result of " + command, result);
            System.out.println("The client received " + result.toExtString());
            Assert.assertFalse("The command must not fail: " + result.toExtString(), result.isCommandFailed());
            Assert.assertTrue("The command must return the position as a String: " + result.toExtString(),
                    result.getOutput() instanceof String && !((String) result.getOutput()).isEmpty());
            Assert.assertEquals("The result must go only to the client that sent the command", 0,
                    other.count(CommandResult.class, r -> true));
        }
    }

    @Test
    public void aChangedSettingIsApplied() throws Exception {
        try (TestClient client = new TestClient(Simulator.port())) {
            Assert.assertNotNull(client.await(SimulatorHeader.class, header -> true, TIMEOUT_MS));
            String value = "testbed-" + System.nanoTime();

            client.send(new PlatformMessage("testbed.setting", value));
            client.send(new Properties());

            Properties configuration = client.await(Properties.class,
                    p -> value.equals(p.getProperty("testbed.setting")), TIMEOUT_MS);
            Assert.assertNotNull("The platform configuration must hold the changed setting", configuration);
            Assert.assertEquals("The client must stay connected", 0, client.connectionsLost());
        }
    }

    @Test
    public void aChangedHeaderIsApplied() throws Exception {
        try (TestClient client = new TestClient(Simulator.port())) {
            SimulatorHeader header = client.await(SimulatorHeader.class, h -> true, TIMEOUT_MS);
            Assert.assertNotNull("The client must receive the header", header);
            String elements = "7000.0;0.0;97.0;340.0;0.0;0.0";
            header.setKeplerElements(elements);
            client.forget();

            client.send(header);

            Assert.assertNotNull("The simulator must send back the header with the changed orbit",
                    client.await(SimulatorHeader.class, h -> elements.equals(h.getKeplerElements()), TIMEOUT_MS));
            Assert.assertEquals("The client must stay connected", 0, client.connectionsLost());
        }
    }

    @Test
    public void aChangedTimeFactorIsApplied() throws Exception {
        try (TestClient client = new TestClient(Simulator.port())) {
            Assert.assertNotNull(client.await(SimulatorData.class, data -> true, TIMEOUT_MS));
            try {
                client.send("TimeFactor:7");
                Assert.assertNotNull("The simulator data must show the changed time factor",
                        client.await(SimulatorData.class, data -> data.getTimeFactor() == 7, TIMEOUT_MS));
            } finally {
                client.send("TimeFactor:1");
                client.await(SimulatorData.class, data -> data.getTimeFactor() == 1, TIMEOUT_MS);
            }
        }
    }
}
