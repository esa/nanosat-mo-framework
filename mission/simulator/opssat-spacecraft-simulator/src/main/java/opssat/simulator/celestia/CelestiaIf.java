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
import java.net.ServerSocket;
import java.net.Socket;
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
    final int SLEEP_DURATION_ACK = 150;
    final int DURATION_ACK_RECOVER = 15000;//After 5 seconds of no confirmation resend the command
    final String DEFAULT_MESSAGE = "connection_alive";
    final String HANDSHAKE_MESSAGE = "connection_successful";
    final String STOP_MESSAGE = "connection_stop";

    int port = 0;
    int retries = 0;

    String MISSION_ID;// = "OPS-SAT";

    ConcurrentLinkedQueue<Object> sendQueue;

    ServerSocket socket;
    Socket connection = null;
    PrintWriter out;
    BufferedReader in;
    private Logger logger;

    public CelestiaIf(ConcurrentLinkedQueue<Object> sendQueue, int listenPort, String mission_ID, Logger logger) {
        this.logger = logger;
        this.sendQueue = sendQueue;
        this.port = listenPort;
        this.MISSION_ID = mission_ID;
    }

    @Override
    public void run() {
        this.init();
        while (true) {
            if (!this.openConnection()) {
                try {
                    Thread.sleep(3000);
                    try {
                        if (this.socket != null) {
                            this.socket.close();
                        }
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
                            // wait for acknowledgement from Celestia

                            int waitedTime = 0;
                            while (!in.ready() && waitedTime < DURATION_ACK_RECOVER) {
                                Thread.sleep(SLEEP_DURATION_ACK);
                                waitedTime = waitedTime + SLEEP_DURATION_ACK;
                            }
                            if (in.ready()) {
                                retries = 0;
                                inMsg = in.readLine();
                            } else {
                                //No reply
                                retries = retries + 1;
                                if (retries <= 1) {
                                    sendQueue.clear();
                                    logger.log(Level.WARNING, "CelestiaIf: No response within [" +
                                        DURATION_ACK_RECOVER + "] ms, resending data message!");
                                } else {
                                    break;
                                }

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
                    this.socket.close();
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
            this.SPACECRAFT_ID.add("OPSSAT");
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

            //1. create a socket
            this.socket = new ServerSocket(this.port, 10);
            logger.log(Level.INFO, "Created ServerSocket for Celestia on port [" + this.port + "] ");
            //this.socket.setReuseAddress(true); // set reusable socket address     
            //this.socket.setSoTimeout(60*1000);
            //this.socket.bind(new InetSocketAddress(), 10); // bind to address              

            //2. Wait for connection            
            logger.log(Level.FINE, "Waiting for connection...");
            connection = this.socket.accept();
            connection.setTcpNoDelay(true);
            logger.log(Level.INFO, "Connection received from " + connection.getInetAddress().getHostName() +
                " on port " + connection.getLocalPort());

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
            int waitedTime = 0;
            while (!in.ready() && waitedTime < DURATION_ACK_RECOVER) {
                try {
                    Thread.sleep(SLEEP_DURATION_ACK);
                } catch (InterruptedException ex) {
                    Logger.getLogger(CelestiaIf.class.getName()).log(Level.SEVERE, null, ex);
                }
                waitedTime = waitedTime + SLEEP_DURATION_ACK;
            }
            String message = null;
            if (in.ready()) {
                message = in.readLine();
            } else {
                //No reply to handshake
                logger.log(Level.FINE, "No reply to handshake");
                return false;
            }
            logger.log(Level.FINE, "Handshake - RECEIVED - " + message);

            retries = 0;

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error establishing connection1" + e.toString());
            return false;
        }
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
