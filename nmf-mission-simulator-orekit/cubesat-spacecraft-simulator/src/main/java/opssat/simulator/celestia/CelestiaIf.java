/*
 *  ----------------------------------------------------------------------------
 *  Copyright (C) 2021      European Space Agency
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
package opssat.simulator.celestia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * *
 *
 * @author Tiago Nogueira
 *
 */
public class CelestiaIf implements Runnable {

    final boolean PRINT_EVENTS = true;

    final String PROTOCOL_VERSION = "1.1";
    ArrayList<String> SPACECRAFT_ID;
    final int portOpsSat = 5909;
    final int portNetSat = 5910;
    /** How long to wait for an acknowledgement before resending, in ms. */
    final int DURATION_ACK_RECOVER = 15000;

    /**
     * How long to wait for Celestia to answer the dial. Short: a Celestia which is
     * not running should cost a moment and a retry, not a thread parked in connect
     * until the operating system gives up on it.
     */
    final int DURATION_CONNECT = 2000;

    /**
     * How long to wait before dialling Celestia again after a failed attempt.
     */
    final int DURATION_RETRY = 3000;
    final String DEFAULT_MESSAGE = "connection_alive";
    final String HANDSHAKE_MESSAGE = "connection_successful";
    final String STOP_MESSAGE = "connection_stop";

    int port = 0;
    int retries = 0;

    /**
     * Whether the absence of Celestia has already been reported. Celestia is dialled every
     * few seconds for as long as the simulator runs, and it is not expected to be there:
     * reporting each refused connection produces one message per attempt for the whole
     * session. The first is reported and the rest are suppressed until a connection
     * succeeds, at which point the next absence is reported again.
     */
    private boolean absenceReported = false;

    String MISSION_ID;// = "OPS-SAT";

    ConcurrentLinkedQueue<Object> sendQueue;

    /**
     * Where Celestia is listening. Celestia is the server: it is the long-running
     * end, and a simulator may come and go several times while it runs. It is also
     * what lets more than one simulator show up in the same Celestia without it
     * having to be told where each of them lives.
     */
    private final String host;

    Socket connection = null;
    PrintWriter out;
    BufferedReader in;
    private Logger logger;

    public CelestiaIf(ConcurrentLinkedQueue<Object> sendQueue, String celestiaHost, int celestiaPort,
            String mission_ID, Logger logger) {
        this.logger = logger;
        this.sendQueue = sendQueue;
        this.host = celestiaHost;
        this.port = celestiaPort;
        this.MISSION_ID = mission_ID;
    }

    @Override
    public void run() {
        this.init();
        while (true) {
            if (!this.openConnection()) {
                try {
                    Thread.sleep(DURATION_RETRY);
                    try {
                        if (this.connection != null) {
                            this.connection.close();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(CelestiaIf.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    continue;
                } catch (InterruptedException ex) {
                    Logger.getLogger(CelestiaIf.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            String outMsg = "";
            String inMsg = "";
            try {
                do {
                    Object data = this.sendQueue.poll();
                    while (this.sendQueue.peek() != null) {
                        data = this.sendQueue.poll();
                    }
                    if (data instanceof CelestiaData) {
                        outMsg = this.buildMessage((CelestiaData) data);
                        if (!outMsg.isEmpty()) {
                            this.sendMessage(outMsg);

                            // Wait for the acknowledgement by reading, rather
                            // than by asking whether anything has arrived yet
                            // and sleeping if it has not. Reading returns the
                            // moment the answer does, and returns nothing at all
                            // the moment the other end closes, so a Celestia that
                            // has been shut lets go of the port straight away
                            // instead of after two timeouts. The socket carries
                            // the timeout, so nothing can wait for ever.
                            String reply;
                            try {
                                reply = in.readLine();
                            } catch (SocketTimeoutException ex) {
                                reply = null;
                            }

                            if (reply == null) {
                                if (this.connection.isClosed() || !this.connection.isConnected()) {
                                    break;
                                }
                                // Either nothing came within the timeout, or the
                                // other end has gone. One resend tells the two
                                // apart: a client that is still there answers it.
                                retries = retries + 1;
                                if (retries <= 1) {
                                    sendQueue.clear();
                                    logger.log(Level.WARNING, "CelestiaIf: No response within [" +
                                        DURATION_ACK_RECOVER + "] ms, resending data message!");
                                } else {
                                    logger.log(Level.INFO, "CelestiaIf: Celestia has gone; waiting "
                                        + "for it to come back.");
                                    break;
                                }
                            } else {
                                retries = 0;
                                inMsg = reply;
                            }
                        }
                    } else {
                        Thread.sleep(150);
                    }

                } while (!inMsg.equals(this.STOP_MESSAGE)); // keep connection until STOP_MESSAGE received from Celestia
            } catch (Exception e) {
                System.err.println(e.toString());
                outMsg = this.STOP_MESSAGE;
            } finally {

                try {
                    logger.log(Level.FINE, "Closing all connections...");
                    this.in.close();
                    this.out.close();
                    this.connection.close();
                    logger.log(Level.FINE, "All connections closed.");
                } catch (IOException ioException) {
                    System.err.println(ioException.toString());
                }
            }
        }

    }

    /**
     */
    private void init() {

        this.SPACECRAFT_ID = new ArrayList<>();

        if (MISSION_ID.equals("OPS-SAT")) {
            this.SPACECRAFT_ID.add("OPS-SAT");
        } else if (MISSION_ID.equals("NetSat")) {
            this.SPACECRAFT_ID.add("NETSAT1");
            this.SPACECRAFT_ID.add("NETSAT2");
            this.SPACECRAFT_ID.add("NETSAT3");
            this.SPACECRAFT_ID.add("NETSAT4");
        }

    }

    /**
     * @brief Establish TCP/IP connection with Celestia
     */
    private boolean openConnection() {
        try {

            //1. dial Celestia. Bounded, so that a Celestia which is not running
            //   costs a moment and a retry rather than a thread stuck in connect.
            logger.log(Level.FINE, "Connecting to Celestia at " + this.host + ":" + this.port);
            connection = new Socket();
            connection.connect(new InetSocketAddress(this.host, this.port), DURATION_CONNECT);
            connection.setTcpNoDelay(true);
            // Bounds the wait for an acknowledgement, which is now done by
            // reading rather than by polling.
            connection.setSoTimeout(DURATION_ACK_RECOVER);
            logger.log(Level.INFO, "Connected to Celestia at " + connection.getInetAddress().getHostAddress()
                + ":" + connection.getPort());

            //3. get Input and Output streams
            //output stream: MO Consumer -> Celestia
            logger.log(Level.FINE, "Getting output stream...");
            this.out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));
            this.out.flush();
            //input stream: Celestia -> MO Consumer
            logger.log(Level.FINE, "Getting input stream...");
            this.in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            //4. handshake
            logger.log(Level.FINE, "Handshake - SENT - " + this.HANDSHAKE_MESSAGE);
            sendMessage(this.HANDSHAKE_MESSAGE);

            // Read for the answer rather than poll for it, as above: a client
            // that connects and then goes away is noticed at once instead of
            // holding the connection until the timeout runs out.
            String message;
            try {
                message = in.readLine();
            } catch (SocketTimeoutException ex) {
                message = null;
            }
            if (message == null) {
                logger.log(Level.FINE, "No reply to handshake");
                return false;
            }
            logger.log(Level.FINE, "Handshake - RECEIVED - " + message);

            retries = 0;

        } catch (ConnectException e) {
            // Nothing is listening, which is the ordinary state of affairs when Celestia
            // has not been started. Reported once, then not again until it has been.
            if (!absenceReported) {
                logger.log(Level.INFO, "Celestia is not listening at " + this.host + ":"
                        + this.port + ". Retrying every " + (DURATION_RETRY / 1000)
                        + " seconds, and this will not be reported again until it answers.");
                absenceReported = true;
            }
            return false;
        } catch (IOException e) {
            // Anything else is not the expected absence and is reported as before.
            logger.log(Level.SEVERE, "Error establishing connection to Celestia at "
                    + this.host + ":" + this.port, e);
            return false;
        }

        absenceReported = false;
        return true;
    }

    /**
     * @brief Send message to client
     *
     * @param msg
     */
    private void sendMessage(String msg) {
        this.out.println(msg);
        this.out.flush();
    }

    public void putDataInBuffer(Object obj) {
        this.sendQueue.add(obj);
    }

    /**
     * @brief Get data from MO Consumer and build message
     *
     * @return The message
     */
    String buildMessage(CelestiaData data) {

        StringBuilder dataStringSc = new StringBuilder();
        StringBuilder dataStringParam = new StringBuilder();
        StringBuilder dataStringValue = new StringBuilder();
        StringBuilder dataStringUnit = new StringBuilder();
        String outMsg = "";
        String scId = "";

        float[] rv = data.getRv();//{-7000,0,0,4,5,6}; // [x, y, z, vx, vy, vz]
        //LocalDateTime dateTime = LocalDateTime.now();  

        /*
        LocalDateTime dateTime = LocalDateTime.parse(
                data.getYears()
                + "-" + String.format("%02d", data.getMonths())
                + "-" + String.format("%02d", data.getDays())
                + "T" + String.format("%02d", data.getHours())
                + ":" + String.format("%02d", data.getMinutes())
                + ":" + String.format("%02d", data.getSeconds()));
        */

        float[] q = {data.getQ()[0], data.getQ()[1], data.getQ()[2], data.getQ()[3]}; // [qs, q1, q2, q3]
        //        LocalDateTime anxTime = LocalDateTime.parse("2015-08-09T10:00:00");
        //        LocalDateTime dnxTime = LocalDateTime.parse("2015-08-09T10:45:33");
        //        LocalDateTime aosKirTime = LocalDateTime.parse("2015-08-09T11:49:00");
        //        LocalDateTime losKirTime = LocalDateTime.parse("2015-08-09T12:01:00");

        ListIterator<String> iter = this.SPACECRAFT_ID.listIterator();

        // build message
        while (iter.hasNext()) {

            scId = iter.next();

            // epoch        
            dataStringSc.append(" ").append(scId);
            dataStringParam.append(" ").append("SIM_EPOCH_TIME");
            /*
            dataStringValue = dataStringValue + " " + dateTime.getYear() + "/"
                    + dateTime.getMonthValue() + "/"
                    + dateTime.getDayOfMonth() + "-"
                    + dateTime.getHour() + ":"
                    + dateTime.getMinute() + ":"
                    + dateTime.getSecond();
            */
            dataStringValue.append(" ").append(data.getYears()).append("/").append(data.getMonths()).append("/").append(
                data.getDays()).append("-").append(data.getHours()).append(":").append(data.getMinutes()).append(":")
                .append(data.getSeconds());

            dataStringUnit.append(" ").append("UTC");

            // ICF position and velocity        
            dataStringSc.append(" ").append(scId).append(" ").append(scId).append(" ").append(scId).append(" ").append(
                scId).append(" ").append(scId).append(" ").append(scId);
            dataStringParam.append(" ").append("X_ICF Y_ICF Z_ICF VX_ICF VY_ICF VZ_ICF");
            dataStringValue.append(" ").append(rv[0]).append(" ").append(rv[1]).append(" ").append(rv[2]).append(" ")
                .append(rv[3]).append(" ").append(rv[4]).append(" ").append(rv[5]);
            dataStringUnit.append(" ").append("km km km km/s km/s km/s");

            // attitude        
            dataStringSc.append(" ").append(scId).append(" ").append(scId).append(" ").append(scId).append(" ").append(
                scId);
            dataStringParam.append(" ").append("QS_ICF QX_ICF QY_ICF QZ_ICF");
            dataStringValue.append(" ").append(q[0]).append(" " // scalar part  --> order to be checked with MO message
            ).append(q[1]).append(" ").append(q[2]).append(" ").append(q[3]);
            dataStringUnit.append(" ").append("- - - -");

            if (this.PRINT_EVENTS) {
                // INFO        
                dataStringSc.append(" ").append(scId);
                dataStringParam.append(" ").append("INFO");
                dataStringValue.append(" ").append(data.getInfo());
                dataStringUnit.append(" ").append("UTC");

                // ANX        
                dataStringSc.append(" ").append(scId);
                dataStringParam.append(" ").append("ANX");
                dataStringValue.append(" ").append(data.getAnx());
                dataStringUnit.append(" ").append("UTC");

                // DNX        
                dataStringSc.append(" ").append(scId);
                dataStringParam.append(" ").append("DNX");
                dataStringValue.append(" ").append(data.getDnx());
                dataStringUnit.append(" ").append("UTC");

                // AOS_KIRUNA
                dataStringSc.append(" ").append(scId);
                dataStringParam.append(" ").append("AOS_ESOC");
                dataStringValue.append(" ").append(data.getAos());
                dataStringUnit.append(" ").append("UTC");

                // LOS_KIRUNA
                dataStringSc.append(" ").append(scId);
                dataStringParam.append(" ").append("LOS_ESOC");
                dataStringValue.append(" ").append(data.getLos());
                dataStringUnit.append(" ").append("UTC");
            }

        }

        // put together the message to be transmitted                                   
        outMsg = "$DATA_START$ $PROTOCOL_VERSION_" + this.PROTOCOL_VERSION + "$" + dataStringSc + " //" +
            dataStringParam + " //" + dataStringValue + " //" + dataStringUnit + " $DATA_END$";

        return outMsg;
    }

}
