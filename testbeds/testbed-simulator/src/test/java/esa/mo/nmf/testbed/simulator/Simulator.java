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

import esa.mo.nmf.mission.orekit.simulator.main.ESASimulator;
import esa.mo.nmf.mission.orekit.simulator.tcp.MultiThreadedSocketServer;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The simulator that every test of the testbed connects to. It is started
 * once, the first time a test asks for it, on a free port of the loopback,
 * and runs until the tests end: starting it takes several seconds.
 */
final class Simulator {

    static final String HOST = "127.0.0.1";

    private static final long START_TIMEOUT_MS = 120_000;

    private static int port = -1;

    private Simulator() {
    }

    /**
     * Returns the port of the simulator, starting it the first time.
     *
     * @return the port that the simulator listens on
     * @throws Exception if the simulator does not start
     */
    static synchronized int port() throws Exception {
        if (port < 0) {
            int free = freePort();
            System.setProperty(MultiThreadedSocketServer.PROPERTY_SOCKET_PORT, String.valueOf(free));
            new ESASimulator(HOST);
            awaitListening(free);
            port = free;
        }
        return port;
    }

    private static int freePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0, 1, InetAddress.getByName(HOST))) {
            return socket.getLocalPort();
        }
    }

    private static void awaitListening(int port) throws Exception {
        long deadline = System.currentTimeMillis() + START_TIMEOUT_MS;
        while (true) {
            try (Socket probe = new Socket(HOST, port)) {
                return;
            } catch (IOException notYet) {
                if (System.currentTimeMillis() > deadline) {
                    throw new IllegalStateException("The simulator did not listen on " + HOST + ":" + port, notYet);
                }
                Thread.sleep(200);
            }
        }
    }
}
