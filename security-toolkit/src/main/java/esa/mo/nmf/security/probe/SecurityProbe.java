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
package esa.mo.nmf.security.probe;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Probe that exercises the spacecraft simulator's TCP transport from inside a
 * container, for the vulnerability recorded as NMF-2026-001.
 * <p>
 * It starts the simulator's headless server (the {@code MainServer} class named
 * in the first argument) in the same JVM, waits until it is listening, and then
 * runs two checks and prints their results to standard output:
 * <pre>
 *   RESULT BIND=LOOPBACK_ONLY | WILDCARD
 *   RESULT DESER=REJECTED | ACCEPTED
 *   RESULT PROBE_DONE
 * </pre>
 * The {@code BIND} check connects to a non-loopback address of the container; a
 * server that honours the configured loopback listen address refuses it, a
 * server that binds to the wildcard address accepts it. The {@code DESER} check
 * sends a {@link Canary}, an object that is not on the server's allow-list and
 * whose {@code readObject} only records that it ran. A server with
 * deserialization filtering rejects it before it is instantiated; a server
 * without filtering instantiates it. The canary runs no attacker code, so the
 * probe detects the weakness without exploiting it.
 * <p>
 * This class is only ever run inside a container, packaged together with one
 * version of the simulator. It is never run on a host.
 */
public final class SecurityProbe {

    private static final int PORT = 11111;
    private static final int STARTUP_TIMEOUT_SECONDS = 90;

    private SecurityProbe() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: SecurityProbe <MainServer class name>");
        }
        String mainServerClass = args[0];
        log("NMF-2026-001 probe: spacecraft simulator TCP transport");
        Class<?> serverClass = Class.forName(mainServerClass);
        String[] identity = simulatorIdentity(serverClass);
        log("simulator under test: " + identity[0] + " " + identity[1]);
        result("VERSION", identity[1]);
        log("starting the simulator server from " + mainServerClass);
        startServer(serverClass);
        waitUntilListening(InetAddress.getLoopbackAddress(), PORT, STARTUP_TIMEOUT_SECONDS);
        log("the server is listening on " + InetAddress.getLoopbackAddress().getHostAddress() + ":" + PORT);

        boolean accepted = deserializationAccepted();
        log("deserialization: the server " + (accepted
                ? "INSTANTIATED the canary, so it deserializes classes that are not on an allow-list"
                : "REJECTED the canary before instantiating it, so a deserialization filter is in place"));
        result("DESER", accepted ? "ACCEPTED" : "REJECTED");

        boolean reachable = reachableOffLoopback();
        log("bind: the server is " + (reachable
                ? "REACHABLE on a non-loopback address, so it bound to the wildcard address 0.0.0.0"
                : "NOT reachable off loopback, so it honoured the configured loopback listen address"));
        result("BIND", reachable ? "WILDCARD" : "LOOPBACK_ONLY");

        log("probe complete");
        result("PROBE_DONE", "");
        System.out.flush();
        // Do not exit: the server's non-daemon threads keep this JVM and the
        // container alive so the test can read the lines above, then stop us.
    }

    private static void log(String message) {
        System.out.println("SECPROBE " + message);
    }

    private static void result(String name, String value) {
        System.out.println("SECPROBE RESULT " + name + (value.isEmpty() ? "" : "=" + value));
    }

    private static void startServer(Class<?> serverClass) throws Exception {
        Method main = serverClass.getMethod("main", String[].class);
        main.invoke(null, (Object) new String[0]);
    }

    /**
     * Reads the title and version of the simulator from the manifest of the jar
     * that provides the server class, so that the report names the exact build
     * that was exercised.
     *
     * @return a two-element array: the implementation title and the version.
     */
    private static String[] simulatorIdentity(Class<?> serverClass) {
        String title = null;
        String version = null;
        Package pkg = serverClass.getPackage();
        if (pkg != null) {
            title = pkg.getImplementationTitle();
            version = pkg.getImplementationVersion();
        }
        if (version == null) {
            try {
                URL location = serverClass.getProtectionDomain().getCodeSource().getLocation();
                try (JarInputStream jar = new JarInputStream(location.openStream())) {
                    Manifest manifest = jar.getManifest();
                    if (manifest != null) {
                        Attributes main = manifest.getMainAttributes();
                        if (title == null) {
                            title = main.getValue("Implementation-Title");
                        }
                        version = main.getValue("Implementation-Version");
                    }
                }
            } catch (Exception cannotRead) {
                // Fall through to the defaults below.
            }
        }
        return new String[] {title == null ? "simulator" : title, version == null ? "unknown" : version};
    }

    private static void waitUntilListening(InetAddress address, int port, int seconds) throws Exception {
        long deadline = System.currentTimeMillis() + seconds * 1000L;
        while (System.currentTimeMillis() < deadline) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(address, port), 1000);
                return;
            } catch (Exception notListeningYet) {
                Thread.sleep(500);
            }
        }
        throw new IllegalStateException("The server did not start listening on " + address + ":" + port);
    }

    /**
     * Sends a {@link Canary} to the server over the loopback interface and
     * reports whether the server deserialized it.
     *
     * @return true if the canary was instantiated, false if it was rejected.
     */
    private static boolean deserializationAccepted() throws Exception {
        Canary.instantiated = false;
        log("deserialization: connecting to " + InetAddress.getLoopbackAddress().getHostAddress() + ":" + PORT);
        log("deserialization: sending a canary (" + Canary.class.getName()
                + "), which is not one of the simulator's message types");
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(InetAddress.getLoopbackAddress(), PORT), 2000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(new Canary());
            out.flush();
            // Give the server's receiver thread time to read and resolve the object.
            Thread.sleep(2000);
        } catch (Exception connectionError) {
            // A connection error on its own does not tell accepted from rejected;
            // the canary flag does.
            log("deserialization: the connection ended after the canary was sent ("
                    + connectionError.getClass().getSimpleName() + ")");
        }
        return Canary.instantiated;
    }

    /**
     * Tries to reach the server on a non-loopback address of this container.
     *
     * @return true if the server is reachable there (bound to the wildcard
     * address), false if the connection is refused (bound to loopback only).
     */
    private static boolean reachableOffLoopback() throws Exception {
        InetAddress offLoopback = firstNonLoopbackIpv4();
        if (offLoopback == null) {
            throw new IllegalStateException("No non-loopback address to test the bind against");
        }
        log("bind: connecting to the non-loopback address " + offLoopback.getHostAddress() + ":" + PORT);
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(offLoopback, PORT), 2000);
            return true;
        } catch (Exception refused) {
            return false;
        }
    }

    private static InetAddress firstNonLoopbackIpv4() throws Exception {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                continue;
            }
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                    return address;
                }
            }
        }
        return null;
    }

    /**
     * A harmless object that records only whether it was deserialized. It is not
     * on the simulator's allow-list, so a server with deserialization filtering
     * rejects it before {@code readObject} runs.
     */
    public static final class Canary implements Serializable {

        private static final long serialVersionUID = 1L;

        static volatile boolean instantiated = false;

        private void readObject(ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
            in.defaultReadObject();
            instantiated = true;
        }
    }
}
