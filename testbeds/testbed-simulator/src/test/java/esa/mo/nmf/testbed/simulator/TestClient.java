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

import esa.mo.nmf.mission.orekit.simulator.tcp.SocketClient;
import esa.mo.nmf.mission.orekit.simulator.tcp.SocketClientOwner;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A client of the simulator without a window: the client of the simulator's
 * GUI, {@link SocketClient}, working for a test instead. It keeps everything
 * the simulator sends, and connects again when the connection is lost, as the
 * GUI does.
 */
final class TestClient implements SocketClientOwner, AutoCloseable {

    private static final Logger LOGGER = quiet();

    private final int port;
    private final ConcurrentLinkedQueue<Object> fromServer = new ConcurrentLinkedQueue<>();
    private final ConcurrentLinkedQueue<Object> toServer = new ConcurrentLinkedQueue<>();
    private final List<Object> received = new ArrayList<>();
    private final AtomicInteger connectionsLost = new AtomicInteger();
    private volatile SocketClient client;
    private volatile boolean closed;

    TestClient(int port) {
        this.port = port;
        this.client = new SocketClient(Simulator.HOST, port, this);
        this.client.start();
    }

    private static Logger quiet() {
        Logger logger = Logger.getLogger(TestClient.class.getName());
        logger.setLevel(Level.WARNING);
        return logger;
    }

    @Override
    public ConcurrentLinkedQueue<Object> getFromServerQueue() {
        return fromServer;
    }

    @Override
    public ConcurrentLinkedQueue<Object> getToServerQueue() {
        return toServer;
    }

    @Override
    public Logger getLogger() {
        return LOGGER;
    }

    @Override
    public void connectionLost() {
        connectionsLost.incrementAndGet();
    }

    @Override
    public void restartSocket() {
        if (!closed) {
            client = new SocketClient(Simulator.HOST, port, this);
            client.start();
        }
    }

    /** Sends something to the simulator, as the GUI does. */
    void send(Object data) {
        toServer.add(data);
    }

    /** How many times the connection was lost. */
    int connectionsLost() {
        return connectionsLost.get();
    }

    /** Closes the connection, as when the simulator closes it: the client then connects again. */
    void disconnect() {
        client.close();
    }

    /** Forgets everything received so far. */
    synchronized void forget() {
        drain();
        received.clear();
    }

    /**
     * Waits for something received that is of the given type and passes the
     * test, and returns it, or null if none came in time.
     */
    synchronized <T> T await(Class<T> type, Predicate<T> test, long timeoutMs) throws InterruptedException {
        long deadline = System.currentTimeMillis() + timeoutMs;
        int checked = 0;
        while (true) {
            drain();
            for (; checked < received.size(); checked++) {
                Object item = received.get(checked);
                if (type.isInstance(item) && test.test(type.cast(item))) {
                    return type.cast(item);
                }
            }
            if (System.currentTimeMillis() > deadline) {
                return null;
            }
            wait(50);
        }
    }

    /** How many of the things received so far are of the given type and pass the test. */
    synchronized <T> long count(Class<T> type, Predicate<T> test) {
        drain();
        return received.stream().filter(type::isInstance).map(type::cast).filter(test).count();
    }

    private void drain() {
        Object item;
        while ((item = fromServer.poll()) != null) {
            received.add(item);
        }
    }

    @Override
    public void close() {
        closed = true;
        client.close();
    }
}
