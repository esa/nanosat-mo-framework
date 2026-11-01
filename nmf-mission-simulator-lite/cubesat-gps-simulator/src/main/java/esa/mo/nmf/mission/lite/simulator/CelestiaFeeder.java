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
package esa.mo.nmf.mission.lite.simulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Feeds the lite simulator's position to Celestia, so that the spacecraft the
 * simulator is flying can be seen flying.
 * <p>
 * The Orekit mission does the same through its own {@code CelestiaIf}, which
 * reads a running engine and carries the attitude as well. This one is the
 * lighter half of that: the lite simulator has no engine and no attitude, so
 * this works the position out on demand, ten times a second, and sends the
 * position and the velocity alone. The quaternion is left out of the message,
 * which Celestia reads as "orientation unchanged", so the spacecraft holds a
 * fixed attitude while it orbits.
 * <p>
 * Celestia is the server: it listens, and this dials in. A Celestia that is not
 * running is the ordinary case at startup, so this retries rather than giving
 * up, and reports the absence once rather than once per attempt. The protocol
 * is Celestia's line protocol: a greeting that names the spacecraft, then one
 * message per sample, each answered before the next is sent.
 */
public class CelestiaFeeder implements Runnable {

    private static final String PROTOCOL_VERSION = "1.1";
    private static final String HANDSHAKE_MESSAGE = "connection_successful";
    private static final String STOP_MESSAGE = "connection_stop";

    /** The sample time on the wire: ISO 8601 to the millisecond, in UTC. */
    private static final String WIRE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /** How long to wait for Celestia to answer the dial, in ms. */
    private static final int DURATION_CONNECT = 2000;

    /** How long to wait for an acknowledgement before giving up on it, in ms. */
    private static final int DURATION_ACK = 15000;

    /** How long to wait before dialling again after a failed attempt, in ms. */
    private static final int DURATION_RETRY = 3000;

    /** How long between samples, in ms: ten a second, as the Orekit feed sends. */
    private static final int SAMPLE_INTERVAL = 100;

    private final Orbit orbit;
    private final String host;
    private final int port;
    private final String spacecraftName;
    private final Logger logger;

    private boolean absenceReported = false;
    private boolean refusalReported = false;

    /**
     * Constructor.
     *
     * @param orbit The orbit to read the position from.
     * @param host Where Celestia is listening.
     * @param port The port Celestia is listening on.
     * @param spacecraftName The name this spacecraft announces, so that a
     * Celestia drawing more than one can tell them apart. Any whitespace in it
     * is closed up, the message being read as a list of words.
     * @param logger The logger to report the connection on.
     */
    public CelestiaFeeder(Orbit orbit, String host, int port, String spacecraftName, Logger logger) {
        this.orbit = orbit;
        this.host = host;
        this.port = port;
        this.spacecraftName = spacecraftName.trim().replaceAll("\\s+", "-");
        this.logger = logger;
    }

    @Override
    public void run() {
        while (true) {
            Socket connection = null;
            try {
                connection = openConnection();
                if (connection == null) {
                    Thread.sleep(DURATION_RETRY);
                    continue;
                }
                feed(connection);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                return;
            } catch (IOException ex) {
                logger.log(Level.FINE, "CelestiaFeeder: the connection to Celestia ended.", ex);
            } finally {
                close(connection);
            }
        }
    }

    /**
     * Sends one sample after another until Celestia goes away or asks to stop,
     * each answered before the next is sent.
     */
    private void feed(Socket connection) throws IOException, InterruptedException {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        while (true) {
            send(out, buildMessage(orbit.getParametersForDate(new Date())));

            String reply;
            try {
                reply = in.readLine();
            } catch (SocketTimeoutException ex) {
                reply = null;
            }

            // Nothing within the timeout, or the other end has gone: either
            // way, let go and dial again.
            if (reply == null || STOP_MESSAGE.equals(reply)) {
                return;
            }
            Thread.sleep(SAMPLE_INTERVAL);
        }
    }

    /**
     * Dials Celestia and announces the spacecraft.
     *
     * @return The open connection, or null when Celestia is not there or has no
     * room, which this reports once and then leaves to the retry.
     */
    private Socket openConnection() {
        Socket connection = new Socket();
        try {
            connection.connect(new InetSocketAddress(host, port), DURATION_CONNECT);
            connection.setTcpNoDelay(true);
            connection.setSoTimeout(DURATION_ACK);

            PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            send(out, HANDSHAKE_MESSAGE + " " + spacecraftName);

            String reply;
            try {
                reply = in.readLine();
            } catch (SocketTimeoutException ex) {
                reply = null;
            }
            if (reply == null) {
                // Celestia let the connection be made and then let it go without
                // a word, which is what it does when it is already drawing as
                // many spacecraft as it has room for. Said once, or a spacecraft
                // that is one too many fills the log with it.
                if (!refusalReported) {
                    logger.log(Level.INFO, "Celestia at {0}:{1} did not answer the greeting of {2}, "
                            + "which it does when it already draws as many spacecraft as it has room "
                            + "for. Retrying every {3} s, and this will not be reported again until it "
                            + "answers.", new Object[]{host, port, spacecraftName, DURATION_RETRY / 1000});
                    refusalReported = true;
                }
                close(connection);
                return null;
            }

            logger.log(Level.INFO, "CelestiaFeeder: connected to Celestia at {0}:{1} as {2}.",
                    new Object[]{host, port, spacecraftName});
            absenceReported = false;
            refusalReported = false;
            return connection;
        } catch (ConnectException ex) {
            // Nothing is listening, which is the ordinary state of affairs when
            // Celestia has not been started. Reported once, then not again.
            if (!absenceReported) {
                logger.log(Level.INFO, "Celestia is not listening at {0}:{1}. Retrying every {2} s, "
                        + "and this will not be reported again until it answers.",
                        new Object[]{host, port, DURATION_RETRY / 1000});
                absenceReported = true;
            }
            close(connection);
            return null;
        } catch (IOException ex) {
            logger.log(Level.WARNING, "CelestiaFeeder: could not connect to Celestia at "
                    + host + ":" + port + ".", ex);
            close(connection);
            return null;
        }
    }

    /**
     * Builds one message in Celestia's line protocol from a position.
     * <p>
     * The four lists run in parallel, one entry per field: the spacecraft the
     * field is for, the field's name, its value, and its unit. Celestia reads
     * the names and the values and ignores the rest. The position and the
     * velocity are in the inertial frame, which is the frame Celestia places
     * the spacecraft in.
     */
    String buildMessage(OrbitParameters parameters) {
        Vector r = parameters.getPositionInertial();
        Vector v = parameters.getVelocity();
        String id = spacecraftName;

        StringBuilder ids = new StringBuilder();
        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        StringBuilder units = new StringBuilder();

        append(ids, names, values, units, id, "SIM_EPOCH_TIME", wireDate(parameters.getTime()), "UTC");
        append(ids, names, values, units, id, "X_ICF", Double.toString(r.x()), "km");
        append(ids, names, values, units, id, "Y_ICF", Double.toString(r.y()), "km");
        append(ids, names, values, units, id, "Z_ICF", Double.toString(r.z()), "km");
        append(ids, names, values, units, id, "VX_ICF", Double.toString(v.x()), "km/s");
        append(ids, names, values, units, id, "VY_ICF", Double.toString(v.y()), "km/s");
        append(ids, names, values, units, id, "VZ_ICF", Double.toString(v.z()), "km/s");

        return "$DATA_START$ $PROTOCOL_VERSION_" + PROTOCOL_VERSION + "$" + ids + " //"
                + names + " //" + values + " //" + units + " $DATA_END$";
    }

    private static void append(StringBuilder ids, StringBuilder names, StringBuilder values,
            StringBuilder units, String id, String name, String value, String unit) {
        ids.append(' ').append(id);
        names.append(' ').append(name);
        values.append(' ').append(value);
        units.append(' ').append(unit);
    }

    private static String wireDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(WIRE_FORMAT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    private static void send(PrintWriter out, String message) {
        out.println(message);
        out.flush();
    }

    private void close(Socket connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (IOException ex) {
                logger.log(Level.FINE, "CelestiaFeeder: closing the connection failed.", ex);
            }
        }
    }
}
