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
package esa.mo.nmf.mission.orekit.simulator.tcp;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cezar Suteu
 */
// A client for our Multithreaded SocketServer. 
public class SocketClient extends Thread {

    private String targetURL;
    private int targetPort;

    public static boolean validIP(String ip) {
        if (ip == null || ip.length() < 7 || ip.length() > 15) {
            return false;
        }

        try {
            int x = 0;
            int y = ip.indexOf('.');

            if (y == -1 || ip.charAt(x) == '-' || Integer.parseInt(ip.substring(x, y)) > 255) {
                return false;
            }

            x = ip.indexOf('.', ++y);
            if (x == -1 || ip.charAt(y) == '-' || Integer.parseInt(ip.substring(y, x)) > 255) {
                return false;
            }

            y = ip.indexOf('.', ++x);
            return !(y == -1 || ip.charAt(x) == '-' || Integer.parseInt(ip.substring(x, y)) > 255 || ip.charAt(++y) ==
                '-' || Integer.parseInt(ip.substring(y)) > 255 || ip.charAt(ip.length() - 1) == '.');

        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void setTargetConnection(String targetURL, int targetPort) {
        if (validIP(targetURL)) {
            this.targetURL = targetURL;
            this.targetPort = targetPort;
        } else {
            String dnsLookup = "";
            try {
                dnsLookup = InetAddress.getByName(targetURL).getHostAddress();
            } catch (UnknownHostException ex) {
                Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (validIP(dnsLookup)) {
                this.targetURL = dnsLookup;
                this.targetPort = targetPort;
            }
        }
    }

    private boolean connectionEstablished;
    private SocketClientOwner parent;
    private volatile Socket s;

    public SocketClient(String targetURL, int targetPort, SocketClientOwner tcpClientNode) {
        this.targetURL = targetURL;
        this.targetPort = targetPort;
        this.parent = tcpClientNode;
    }

    /**
     * Closes the connection to the simulator. The owner is then told that the
     * connection was lost, as when the simulator closes it.
     */
    public void close() {
        Socket socket = this.s;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(SocketClient.class.getName()).log(Level.FINE, "The socket could not be closed", ex);
            }
        }
    }

    @Override
    public void run() {
        while (!connectionEstablished) {
            Socket s = null;

            // Create the socket connection to the MultiThreadedSocketServer port 11111
            try {
                s = new Socket(targetURL, targetPort);
                this.parent.getFromServerQueue().offer("Local;Connected to server @ " + targetURL + ":" + targetPort);
                parent.getToServerQueue().offer("List");
                // this.parent.getGuiMainWindow().showConnectedInfo(true);
            } catch (UnknownHostException uhe) {
                // Server Host unreachable
                System.out.println("Unknown Host :" + targetURL);
                s = null;
            } catch (IOException ioe) {
                // Cannot connect to port on given server host
                this.parent.getFromServerQueue().offer("Local;Cannot connect to server @ " + targetURL + ":" +
                    targetPort);
                s = null;
            }

            if (s == null) {
                try {
                    Thread.sleep(5000);
                    this.parent.getFromServerQueue().offer("Local;Trying to reconnect..");
                    continue;
                } catch (InterruptedException ex) {
                    Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
            this.s = s;
            connectionEstablished = true;
            SocketClientThreadReceiver socketClientThreadReceiver = new SocketClientThreadReceiver(s, parent);
            SocketClientThreadSender socketClientThreadSender = new SocketClientThreadSender(s, parent);
            socketClientThreadReceiver.start();
            socketClientThreadSender.start();
        }
    }

    class SocketClientThreadReceiver extends Thread {

        private Socket socket;
        private SocketClientOwner parent;

        SocketClientThreadReceiver(Socket socket, SocketClientOwner tcpClientNode) {
            this.socket = socket;
            this.parent = tcpClientNode;
            System.out.println("Created SocketClientThreadReceiver");
        }

        @Override
        public void run() {
            InputStream in = null;
            try {
                // The server is read as the simulator's own messages, one line
                // of JSON each, and nothing else, in case it is hostile or MitM'd.
                in = new BufferedInputStream(socket.getInputStream());

                Object received;
                while ((received = SimulatorMessages.read(in)) != null) {
                    parent.getFromServerQueue().offer(received);
                }
                throw new IOException("The server closed the connection");
            } catch (IOException ioe) {
                System.out.println("Receiver Exception during communication. Server probably closed connection.");
                this.parent.connectionLost();
                this.parent.getFromServerQueue().offer(
                    "Local;Receiver;Exception during communication. Server probably closed connection."
                    + ioe);

            } finally {
                try {
                    // Close the input and output streams
                    if (in != null) {
                        in.close();
                    }
                    // Close the socket before quitting
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.parent.restartSocket();
            }
        }

    }

    static class SocketClientThreadSender extends Thread {

        private Socket socket;
        private SocketClientOwner parent;

        SocketClientThreadSender(Socket socket, SocketClientOwner tcpClientNode) {
            this.socket = socket;
            this.parent = tcpClientNode;
            System.out.println("Created SocketClientThreadSender");
        }

        @Override
        public void run() {
            OutputStream out = null;
            try {
                // Create the streams to send and receive information

                out = new BufferedOutputStream(this.socket.getOutputStream());

                while (true) {

                    Object toSend = this.parent.getToServerQueue().poll();
                    if (toSend != null) {
                        this.parent.getLogger().log(Level.FINE, "Sending something [" + toSend + "]");
                        try {
                            SimulatorMessages.write(toSend, out);
                        } catch (SimulatorMessages.NotAMessageException ex) {
                            this.parent.getLogger().log(Level.WARNING, "Not sent to the server: " + ex.getMessage());
                        }
                    } else {
                        SimulatorMessages.write(0, out);
                    }

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ioe) {
                System.out.println("Sender Exception during communication. Server probably closed connection.");

                this.parent.getFromServerQueue().offer(
                    "Local;Sender;Exception during communication. Server probably closed connection." + ioe);

            } finally {
                try {
                    // Close the input and output streams
                    if (out != null) {
                        out.close();
                    }
                    // Close the socket before quitting
                    this.socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            // }
        }

    }
}
