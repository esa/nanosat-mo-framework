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

import esa.mo.nmf.mission.orekit.simulator.util.SimulatorData;
import esa.mo.nmf.mission.orekit.simulator.util.SimulatorHeader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;

/**
 * What a client of the simulator receives when it connects, while it stays
 * connected, and when it connects again, and that one client's bad input
 * leaves the others served. The tests go through the simulator's own server and
 * the client of its GUI, and check what arrives, not how it is encoded.
 */
public class SimulatorConnectionTest {

    private static final long TIMEOUT_MS = 30_000;

    @Test
    public void aClientReceivesTheHeaderAndTheListOfCommandsWhenItConnects() throws Exception {
        try (TestClient client = new TestClient(Simulator.port())) {
            Assert.assertNotNull("The client must receive the header of the simulator",
                    client.await(SimulatorHeader.class, header -> true, TIMEOUT_MS));
            Assert.assertNotNull("The client must receive the list of commands and devices",
                    client.await(LinkedList.class, Messages::isCommandList, TIMEOUT_MS));
        }
    }

    @Test
    public void aClientReceivesThePlatformConfigurationWhenItAsksForIt() throws Exception {
        try (TestClient client = new TestClient(Simulator.port())) {
            client.send(new Properties());
            Properties configuration = client.await(Properties.class, p -> true, TIMEOUT_MS);
            Assert.assertNotNull("The client must receive the platform configuration", configuration);
            Assert.assertNotNull("The platform configuration must hold the simulator's settings, such as "
                    + "camerasim.imagemode: " + configuration, configuration.getProperty("camerasim.imagemode"));
        }
    }

    @Test
    public void aClientReceivesTheSimulatorDataAndTheDeviceDataPeriodically() throws Exception {
        try (TestClient client = new TestClient(Simulator.port())) {
            Assert.assertNotNull(client.await(SimulatorData.class, data -> true, TIMEOUT_MS));
            client.forget();
            Thread.sleep(6_000);
            long simulatorData = client.count(SimulatorData.class, data -> true);
            long deviceData = client.count(LinkedList.class, Messages::isDeviceData);
            System.out.println("In 6 s, the client received " + simulatorData + " SimulatorData and "
                    + deviceData + " lists of device data");
            Assert.assertTrue("The simulator data comes about twice a second: " + simulatorData, simulatorData >= 6);
            Assert.assertTrue("The device data comes about once a second: " + deviceData, deviceData >= 3);
        }
    }

    @Test
    public void twoClientsBothReceiveTheData() throws Exception {
        try (TestClient first = new TestClient(Simulator.port());
                TestClient second = new TestClient(Simulator.port())) {
            Assert.assertNotNull("The first client must receive the simulator data",
                    first.await(SimulatorData.class, data -> true, TIMEOUT_MS));
            Assert.assertNotNull("The second client must receive the simulator data",
                    second.await(SimulatorData.class, data -> true, TIMEOUT_MS));
            Assert.assertNotNull("The first client must receive the device data",
                    first.await(LinkedList.class, Messages::isDeviceData, TIMEOUT_MS));
            Assert.assertNotNull("The second client must receive the device data",
                    second.await(LinkedList.class, Messages::isDeviceData, TIMEOUT_MS));
        }
    }

    @Test
    public void aClientThatConnectsAgainReceivesTheHeaderAndTheListAgain() throws Exception {
        try (TestClient client = new TestClient(Simulator.port())) {
            Assert.assertNotNull(client.await(LinkedList.class, Messages::isCommandList, TIMEOUT_MS));
            client.forget();

            client.disconnect();

            Assert.assertNotNull("Having connected again, the client must receive the header again",
                    client.await(SimulatorHeader.class, header -> true, TIMEOUT_MS));
            Assert.assertNotNull("Having connected again, the client must receive the list again",
                    client.await(LinkedList.class, Messages::isCommandList, TIMEOUT_MS));
            Assert.assertNotNull("Having connected again, the client must receive the simulator data",
                    client.await(SimulatorData.class, data -> true, TIMEOUT_MS));
            Assert.assertEquals("The connection was lost once", 1, client.connectionsLost());
        }
    }

    @Test
    public void badInputDropsOnlyThatClient() throws Exception {
        try (TestClient client = new TestClient(Simulator.port())) {
            Assert.assertNotNull(client.await(SimulatorData.class, data -> true, TIMEOUT_MS));

            try (Socket bad = new Socket(Simulator.HOST, Simulator.port())) {
                bad.setSoTimeout((int) TIMEOUT_MS);
                OutputStream out = bad.getOutputStream();
                out.write("this is not a message of the simulator\n".getBytes(StandardCharsets.US_ASCII));
                out.flush();
                Assert.assertTrue("The simulator must close the connection that sent bad input",
                        closedByTheSimulator(bad.getInputStream()));
            }

            client.forget();
            Assert.assertNotNull("The other client must still receive the simulator data",
                    client.await(SimulatorData.class, data -> true, TIMEOUT_MS));
            Assert.assertEquals("The other client must stay connected", 0, client.connectionsLost());
        }
    }

    /** Reads until the simulator closes the connection; what it sends before is ignored. */
    private static boolean closedByTheSimulator(InputStream in) {
        byte[] buffer = new byte[4096];
        try {
            while (in.read(buffer) >= 0) {
                // What the simulator sends before it closes the connection
            }
            return true;
        } catch (java.net.SocketTimeoutException stillOpen) {
            return false;
        } catch (java.io.IOException reset) {
            return true;
        }
    }
}
