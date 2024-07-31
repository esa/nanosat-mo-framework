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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cezar Suteu
 */
public class TCPServerReceiveOnly extends Thread {

    private int port;
    private Object dataMutex = new Object();
    private String data;
    private boolean shouldClose;
    private Logger logger;

    public void setShouldClose(boolean shouldClose) {
        this.shouldClose = shouldClose;
    }

    public String getData() {
        synchronized (dataMutex) {
            return data;
        }

    }

    @Override
    public void run() {
        Thread.currentThread().setName("sim-" + this.getClass().getSimpleName() + "-" + port);
        String clientSentence = null;
        ServerSocket welcomeSocket = null;
        while (!shouldClose) {
            try {
                welcomeSocket = new ServerSocket(port);
                this.logger.log(Level.INFO, "Created ServerSocket on port [" + port + "]");

                Socket connectionSocket;
                try {
                    connectionSocket = welcomeSocket.accept();
                } catch (IOException ex) {
                    Logger.getLogger(TCPServerReceiveOnly.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }
                this.logger.log(Level.INFO, "Accepted Socket from [" + connectionSocket.getInetAddress() + "]");
                BufferedReader inFromClient;
                try {
                    inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    while (!shouldClose) {
                        try {
                            clientSentence = inFromClient.readLine();
                            if (clientSentence == null) {
                                connectionSocket.close();
                                welcomeSocket.close();
                                break;

                            }
                        } catch (IOException ex) {
                            Logger.getLogger(TCPServerReceiveOnly.class.getName()).log(Level.SEVERE, null, ex);
                            connectionSocket.close();
                            welcomeSocket.close();
                            break;
                        }
                        //System.out.println("Received: " + clientSentence);
                        synchronized (dataMutex) {
                            data = clientSentence;
                        }
                    }
                } catch (IOException ex) {
                    welcomeSocket.close();
                    Logger.getLogger(TCPServerReceiveOnly.class.getName()).log(Level.SEVERE, null, ex);
                    break;
                }

            } catch (IOException ex) {
                if (welcomeSocket != null) {
                    try {
                        welcomeSocket.close();
                    } catch (IOException ex1) {
                        Logger.getLogger(TCPServerReceiveOnly.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
                Logger.getLogger(TCPServerReceiveOnly.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        try {
            welcomeSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(TCPServerReceiveOnly.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public TCPServerReceiveOnly(int port, Logger logger) {
        this.logger = logger;
        this.port = port;

    }

    public static void main(String[] argv) {
        TCPServerReceiveOnly tcpServer = new TCPServerReceiveOnly(10500, Logger.getLogger(Logger.GLOBAL_LOGGER_NAME));
        tcpServer.start();
    }
}
