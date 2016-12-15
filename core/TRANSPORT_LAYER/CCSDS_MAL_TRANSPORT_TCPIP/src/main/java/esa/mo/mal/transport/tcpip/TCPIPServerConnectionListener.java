/* ----------------------------------------------------------------------------
 * Copyright (C) 2014      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO TCP/IP Transport Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.mal.transport.tcpip;

import esa.mo.mal.encoder.tcpip.TCPIPMessageDecoderFactory;
import esa.mo.mal.transport.gen.util.GENMessagePoller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;

import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;

import java.util.ArrayList;
import java.util.List;

/**
 * Server Thread for the TCPIP transport.
 *
 * This thread listens for new connections to a predefined port and when new connections arrive it forwards the newly
 * created socket to a dedicated manager thread.
 *
 */
public class TCPIPServerConnectionListener extends Thread {
	private final TCPIPTransport transport;
	private final ServerSocket serverSocket;

	/**
	 * Holds the list of data poller threads
	 */
	private final List<Thread> pollerThreads = new ArrayList<Thread>();

	/**
	 * Constructor.
	 *
	 * @param transport
	 *            The parent TCPIP transport.
	 * @param serverSocket
	 *            The server TCPIP socket.
	 */
	public TCPIPServerConnectionListener(TCPIPTransport transport, ServerSocket serverSocket) {
		this.transport = transport;
		this.serverSocket = serverSocket;
		setName(getClass().getName() + " - Main Server Socket Thread");
	}

	/**
	 * Run and accept new incoming sockets. Each incoming socket is assigned a
	 * data transceiver, which submits outgoing messages and reads incoming
	 * messages to and from the socket respectively.
	 */
	@Override
	public void run() {
		try {
			serverSocket.setSoTimeout(1000);
		} catch (IOException e) {
			RLOGGER.log(Level.WARNING, "Error while setting connection timeout", e);
		}

		// setup socket and then listen for connections forever
		while (!interrupted()) {
			try {
				// wait for connection
				Socket socket = serverSocket.accept();
				
				RLOGGER.log(Level.INFO, "Socket accepted at port {0}", socket.getPort());

				// handle socket in separate thread
				TCPIPTransportDataTransceiver tc = transport.createDataTransceiver(socket);

				GENMessagePoller poller = new GENMessagePoller(
						transport, tc, tc, new TCPIPMessageDecoderFactory());
				pollerThreads.add(poller);
				poller.start();
			} catch (java.net.SocketTimeoutException ex) {
				// No socket accepted within timeout. Try again until we accept a socket.
			} catch (IOException e) {
				RLOGGER.log(Level.WARNING, "Error while accepting connection", e);
			}
		}

		for (Thread pollerThread : pollerThreads) {
			synchronized (pollerThread) {
				pollerThread.interrupt();
			}
		}

		pollerThreads.clear();
	}
	
	/**
	 * Close the server socket for this connection listener.
	 */
	public void close() {

		RLOGGER.info("Closing server socket...");

		try {
			serverSocket.close();
		} catch (IOException e) {
			RLOGGER.log(Level.WARNING, "Error while closing server socket", e);
		}
		
	}
}
