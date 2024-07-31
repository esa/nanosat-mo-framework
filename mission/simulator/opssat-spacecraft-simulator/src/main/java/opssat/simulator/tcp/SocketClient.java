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

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import opssat.simulator.gui.GuiApp;

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
    private GuiApp parent;
    private Socket s;

    public SocketClient(String targetURL, int targetPort, GuiApp tcpClientNode) {
        this.targetURL = targetURL;
        this.targetPort = targetPort;
        this.parent = tcpClientNode;
    }

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
        private GuiApp parent;

        SocketClientThreadReceiver(Socket socket, GuiApp tcpClientNode) {
            this.socket = socket;
            this.parent = tcpClientNode;
            System.out.println("Created SocketClientThreadReceiver");
        }

        @Override
        public void run() {
            ObjectInputStream in = null;
            try {
                // Create the streams to send and receive information

                in = new ObjectInputStream(socket.getInputStream());

                while (true) {

                    try {
                        // Object received=
                        // Receive the reply.
                        Object received = in.readObject();
                        //System.out.println("Received something ["+received.getClass().getName()+"]"+received.toString());
                        parent.getFromServerQueue().offer(received);
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (in.available() == 0) {
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SocketClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (IOException ioe) {
                System.out.println("Receiver Exception during communication. Server probably closed connection.");
                this.parent.getGuiMainWindow().showConnectedInfo(false);
                this.parent.getFromServerQueue().offer(
                    "Local;Receiver;Exception during communication. Server probably closed connection." + ioe);

            } finally {
                try {
                    // Close the input and output streams

                    in.close();
                    // Close the socket before quitting
                    s.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.parent.restartSocket();
            }
        }

    }

    class SocketClientThreadSender extends Thread {

        private Socket socket;
        private GuiApp parent;

        SocketClientThreadSender(Socket socket, GuiApp tcpClientNode) {
            this.socket = socket;
            this.parent = tcpClientNode;
            System.out.println("Created SocketClientThreadSender");
        }

        @Override
        public void run() {
            ObjectOutputStream out = null;
            try {
                // Create the streams to send and receive information

                out = new ObjectOutputStream(this.socket.getOutputStream());

                while (true) {

                    Object toSend = this.parent.getToServerQueue().poll();
                    out.reset();
                    if (toSend != null) {
                        this.parent.getLogger().log(Level.FINE, "Sending something [" + toSend + "]");
                        out.writeObject(toSend);
                    } else {
                        out.writeObject(0);
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
                    out.close();
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
