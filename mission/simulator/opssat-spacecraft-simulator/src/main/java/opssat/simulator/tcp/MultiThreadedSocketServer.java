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
package opssat.simulator.tcp;

/**
 *
 * @author Cezar Suteu
 */
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.threading.CentralNode;
import opssat.simulator.util.CommandDescriptor;
import opssat.simulator.util.CommandResult;

public class MultiThreadedSocketServer extends Thread {

    private static final int MAX_PORTS_OPEN = 10;
    public static final int DEFAULT_SOCKET_PORT = 11111;
    public static final int DEFAULT_CELESTIA_PORT = 5909;
    ServerSocket myServerSocket;
    boolean ServerOn = true;
    private String listenURL;
    private CentralNode parent;
    private LinkedList<ClientServiceThreadSender> clientSockets;
    /*
     * This is a hashmap which makes connection between sender and receiver threads
     * for determining whether a command result should be sent to a specific client
     * (in case of multi-client operation).
     */
    private HashMap<ClientServiceThreadSender, LinkedList<String>> hMapManualCommandsList;
    private int listenPort;
    private Logger logger;

    public Logger getLogger() {
        return logger;
    }

    public MultiThreadedSocketServer(String listenURL, CentralNode centralNode, int listenPort, Logger logger) {

        this.logger = logger;
        this.listenURL = listenURL;
        this.parent = centralNode;
        this.listenPort = listenPort;
        this.clientSockets = new LinkedList<>();
        this.hMapManualCommandsList = new HashMap<>();

    }

    public synchronized ConcurrentLinkedQueue<Object> getParentFromGUIQ() {
        return this.parent.getqFromGUI();
    }

    public void putDataOnForAllClients(Object data) {

        for (ClientServiceThreadSender s : clientSockets) {
            if (s.getMyClientSocket().isConnected()) {
                logger.log(Level.ALL, "Put data [" + data.getClass().getName() + "] for client [" + s.toString() + "]");
                if (data instanceof LinkedList) {
                    if (((LinkedList) data).size() > 0) {
                        logger.log(Level.ALL, "Put list of [" + ((LinkedList) data).get(0).getClass().getName() +
                            "] for client [" + s.toString() + "]");
                        /*
                         * if (!s.isListSent() && ((LinkedList) data).get(0) instanceof
                         * CommandDescriptor) { logger.log(Level.ALL, "Set list to true");
                         * s.setListSent(true); s.putDataToClient(data); } else {
                         * s.putDataToClient(data); }
                         */
                        s.putDataToClient(data);
                    }
                } else {
                    boolean doSendData = true;
                    if (data instanceof CommandResult) {
                        boolean hasBeenRequested = false;
                        LinkedList<String> tempList = this.hMapManualCommandsList.get(s);
                        String testString = ((CommandResult) data).getCommandDescriptor().getMethodBody();
                        hasBeenRequested = tempList.contains(testString);
                        if (hasBeenRequested)
                            tempList.remove(testString);
                        doSendData = hasBeenRequested;
                    }
                    if (doSendData)
                        s.putDataToClient(data);
                }
            } else {
                clientSockets.remove(s);
            }
        }
    }

    public void run() {
        Thread.currentThread().setName("sim-" + this.getClass().getSimpleName());
        int currentTries = 0;
        int targetPort = 0;
        while (currentTries < MAX_PORTS_OPEN) {
            boolean failed = false;
            try {
                InetAddress addr = InetAddress.getByName(listenURL);
                targetPort = this.listenPort + currentTries;
                logger.log(Level.FINE, "Create server socket on port [" + targetPort + "].");
                myServerSocket = new ServerSocket(targetPort, 10);
                logger.log(Level.INFO, "ServerSocket created on port [" + targetPort + "]");

            } catch (IOException ioe) {
                logger.log(Level.INFO, "Could not create server socket on port [" + targetPort + "].");
                currentTries++;
                failed = true;
            }
            if (!failed) {
                break;
            }
        }
        if (currentTries >= MAX_PORTS_OPEN) {
            logger.log(Level.SEVERE, "Could not create server socket from port [" + DEFAULT_SOCKET_PORT +
                "] up to port [" + targetPort + "]. Total tries [" + currentTries + "]. Quitting.");
            System.exit(-1);
        }
        Calendar now = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("E yyyy.MM.dd 'at' hh:mm:ss a zzz");

        // Successfully created Server Socket. Now wait for connections.
        while (ServerOn) {
            try {
                // Accept incoming connections.
                Socket clientSocket = myServerSocket.accept();

                // accept() will block until a client connects to the server.
                // If execution reaches this point, then it means that a client
                // socket has been accepted.
                // For each client, we will start a service thread to
                // service the client requests. This is to demonstrate a
                // Multi-Threaded server. Starting a thread also lets our
                // MultiThreadedSocketServer accept multiple connections simultaneously.
                // Start a Service thread
                ClientServiceThreadSender cliThread = new ClientServiceThreadSender(clientSocket, this);
                ClientServiceThreadReceiver cliThreadReceiver = new ClientServiceThreadReceiver(clientSocket, this);
                this.hMapManualCommandsList.put(cliThread, cliThreadReceiver.getCommandsList());
                cliThread.start();
                cliThreadReceiver.start();
                clientSockets.add(cliThread);

            } catch (IOException ioe) {
                logger.log(Level.SEVERE, "Exception encountered on accept. Ignoring. Stack Trace :");
                ioe.printStackTrace();
            }

        }

        try {
            myServerSocket.close();
            logger.log(Level.SEVERE, "Server Stopped");
        } catch (Exception ioe) {
            logger.log(Level.SEVERE, "Problem stopping server socket");
            System.exit(-1);
        }

    }

    class ClientServiceThreadReceiver extends Thread {

        private Socket myClientSocket;
        private boolean listSent;
        private LinkedList<String> listCommands;
        private Logger logger;

        public Socket getMyClientSocket() {
            return myClientSocket;
        }

        MultiThreadedSocketServer parent;
        boolean m_bRunThread = true;
        ConcurrentLinkedQueue<Object> toClient;

        public ClientServiceThreadReceiver() {
            super();
        }

        public boolean isListSent() {
            return listSent;
        }

        public void setListSent(boolean listSent) {
            this.listSent = listSent;
        }

        ClientServiceThreadReceiver(Socket s, MultiThreadedSocketServer parent) {
            myClientSocket = s;
            this.parent = parent;
            this.logger = parent.getLogger();
            logger.log(Level.FINE, "Created ClientServiceThreadReceiver");
            toClient = new ConcurrentLinkedQueue<>();
            listCommands = new LinkedList<>();
        }

        public void run() {
            Thread.currentThread().setName("sim-" + this.getClass().getSimpleName() + "-" + myClientSocket
                .getInetAddress().getHostName());

            // Obtain the input stream and the output stream for the socket
            // A good practice is to encapsulate them with a BufferedReader
            // and a PrintWriter as shown below.
            ObjectInputStream in = null;

            // Print out details of this connection
            logger.log(Level.INFO, "Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());

            try {

                in = new ObjectInputStream(myClientSocket.getInputStream());
                // At this point, we can read for input and reply with appropriate output.

                // Run in a loop until m_bRunThread is set to false
                while (m_bRunThread) {
                    Object clientCommand = null;
                    try {
                        // read incoming stream
                        clientCommand = in.readObject();
                    } catch (EOFException | SocketException ex) {
                        logger.log(Level.INFO, "Disconnected Client Address - " + myClientSocket.getInetAddress()
                            .getHostName());
                        m_bRunThread = false;
                    }
                    if (clientCommand != null) {
                        if (clientCommand instanceof Integer) {
                        } else {
                            logger.log(Level.ALL, "Received data");
                            if (clientCommand != null) {
                                this.parent.getParentFromGUIQ().add(clientCommand);
                                if (clientCommand.equals("refreshConfig")) {
                                    parent.parent.getParentSimulator().getSimulatorNode().updatePlatformConfig();
                                    continue;
                                }
                                this.parent.putDataOnForAllClients("OnServer;UserInput;" + CommandDescriptor
                                    .makeConsoleDescriptionForObj(clientCommand));
                                if (clientCommand instanceof CommandDescriptor) {

                                    this.listCommands.add(((CommandDescriptor) clientCommand).getMethodBody());
                                }
                            }
                        }
                    }

                    if (!ServerOn) {
                        // Special command. Quit this thread
                        logger.log(Level.SEVERE, "Server has already stopped");
                        m_bRunThread = false;

                    }
                    if (in.available() == 0) {
                        Thread.sleep(150);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Clean up
                try {
                    in.close();

                    myClientSocket.close();
                    logger.log(Level.FINE, "...Stopped");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

        private LinkedList<String> getCommandsList() {
            return this.listCommands;
        }

    }

    class ClientServiceThreadSender extends Thread {

        private Socket myClientSocket;
        private boolean listSent;

        private Logger logger;

        public Socket getMyClientSocket() {
            return myClientSocket;
        }

        MultiThreadedSocketServer parent;
        boolean m_bRunThread = true;
        ConcurrentLinkedQueue<Object> toClient;

        public ClientServiceThreadSender() {
            super();
        }

        public boolean isListSent() {
            return listSent;
        }

        public void setListSent(boolean listSent) {
            this.listSent = listSent;
        }

        public void putDataToClient(Object data) {
            boolean dataOk = false;
            dataOk = true;
            if (dataOk) {
                toClient.offer(data);
                while (toClient.size() > 5) {
                    toClient.poll();
                }
            }
        }

        ClientServiceThreadSender(Socket s, MultiThreadedSocketServer parent) {
            myClientSocket = s;
            this.parent = parent;
            this.logger = parent.getLogger();
            logger.log(Level.FINE, "Created ClientServiceThread");
            toClient = new ConcurrentLinkedQueue<>();
            toClient.add("PWD:" + System.getProperty("user.dir"));
        }

        public void run() {
            Thread.currentThread().setName("sim-" + this.getClass().getSimpleName() + "-" + myClientSocket
                .getInetAddress().getHostName());

            // Obtain the input stream and the output stream for the socket
            // A good practice is to encapsulate them with a BufferedReader
            // and a PrintWriter as shown below.

            ObjectOutputStream out = null;

            // Print out details of this connection
            logger.log(Level.FINE, "Sender Accepted Client Address - " + myClientSocket.getInetAddress().getHostName());

            try {
                out = new ObjectOutputStream(myClientSocket.getOutputStream());

                // At this point, we can read for input and reply with appropriate output.
                // Run in a loop until m_bRunThread is set to false
                while (m_bRunThread) {

                    Object toClientObj;
                    while ((toClientObj = this.toClient.poll()) != null) {
                        try {
                            out.reset();
                            out.writeObject(toClientObj);
                        } catch (SocketException ex) {
                            m_bRunThread = false;
                        }
                    }
                    if (!ServerOn) {
                        // Special command. Quit this thread
                        logger.log(Level.SEVERE, "Server has already stopped");
                        m_bRunThread = false;
                    }
                    Thread.sleep(150);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // Clean up
                try {
                    try {
                        out.close();
                    } catch (SocketException ex) {
                    }
                    myClientSocket.close();
                    logger.log(Level.FINE, "...Stopped");
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }

    }

}
