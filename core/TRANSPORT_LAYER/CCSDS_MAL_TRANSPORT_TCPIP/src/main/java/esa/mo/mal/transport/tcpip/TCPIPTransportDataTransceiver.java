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

import esa.mo.mal.transport.gen.sending.GENMessageSender;
import esa.mo.mal.transport.gen.sending.GENOutgoingMessageHolder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.structures.URI;

import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;

/**
 * This class implements the low level data (MAL Message) transport protocol. The messages are encoded according to the
 * MAL TCPIP Transport Binding specification.
 * 
 * This class manages both the transmitting and receiving of messages.
 */
public class TCPIPTransportDataTransceiver implements esa.mo.mal.transport.gen.util.GENMessagePoller.GENMessageReceiver<TCPIPPacketInfoHolder>, GENMessageSender {

        private boolean closed = false;
        private final static int HEADER_SIZE = 23;
        protected final Socket socket;
	protected final DataOutputStream socketWriteIf;
	protected final DataInputStream socketReadIf;
        private final URI from;
        private final URI to;

	/**
	 * Constructor.
	 *
	 * @param socket the TCPIP socket.
         * @param localPort
	 * @throws IOException if there is an error.
	 */
	public TCPIPTransportDataTransceiver(Socket socket, int localPort) throws IOException {
		RLOGGER.log(Level.FINE, "Creating new Data Transceiver");
		this.socket = socket;
		socketWriteIf = new DataOutputStream(socket.getOutputStream());
		socketReadIf = new DataInputStream(socket.getInputStream());
                
		// get information
		String remoteHost = socket.getInetAddress().getHostAddress();
		int remotePort = socket.getPort();
		String localHost = socket.getLocalAddress().getHostAddress();
		this.from = new URI("maltcp://" + remoteHost + ":" + remotePort);
		this.to = new URI("maltcp://" + localHost + ":" + localPort); // We need to use this localPort in order to fool the MAL
	}

	/**
	 * Send an encoded message out over the socket
	 * @param packetData
	 * 				The encoded message to send
	 */
	@Override
	public void sendEncodedMessage(GENOutgoingMessageHolder packetData) throws IOException {

            /* Playing with strings before sending messages... No thank you!
		StringBuilder sb = new StringBuilder();
		sb.append("\nTCPIPTransportDataTransciever.sendEncodedMessage()");
		sb.append("\nWriting to socket:");
		sb.append("\n---------------------------------------");
		sb.append("\npacketData length: " + ((byte[])packetData.getEncodedMessage()).length + "\n");
		for (byte b2 : (byte[])packetData.getEncodedMessage()) {
			sb.append(Integer.toString(b2 & 0xFF, 10) + " ");
		}
		sb.append("\n---------------------------------------");
		RLOGGER.log(Level.FINEST, sb.toString());
*/		

                if(!closed){
                        socketWriteIf.write((byte[])packetData.getEncodedMessage());
        		socketWriteIf.flush();
                }
	}

	/**
	 * Read an encoded message from the socket. The message is read into a byte
	 * array. The encoded message header contains the length of the
	 * variable-size body of the message. Therefore, this information is already
	 * extracted. The resulting length is used to extract a body message from
	 * the socket stream.
	 * 
	 * If this TCPIP transport instance is also a provider, it has initiated a
	 * server socket with a configuration-defined port. The incoming socket
	 * cannot have the same port, as it is not the same socket. Hence, the port
	 * of the URI-to parameter in the message is modified such that it equals
	 * the port of the server socket after the message is received. This ensures
	 * that the communication supports several sockets, one for provider-side
	 * and one for client-side, while still being compliant with the MAL
	 * restriction that every client/provider has exactly one unique address.
         * @return 
         * @throws java.io.IOException
	 */
	@Override
	public TCPIPPacketInfoHolder readEncodedMessage() throws IOException {
		
                    // figure out length according to mal message mapping to determine byte arr length, then read the rest.		
                    byte[] rawHeader = new byte[HEADER_SIZE];

                    try{
                            this.readUntilComplete(rawHeader, 0, HEADER_SIZE);
                    } catch (SocketException socketExc) {
                            throw new java.io.EOFException(socketExc.getMessage());
                    } catch (NullPointerException headerReadNullPointer) {
                            RLOGGER.warning("NullpointerException occured while reading header! " + headerReadNullPointer.getMessage());
                    } catch (IndexOutOfBoundsException headerReadOutOfBounds) {
                            RLOGGER.warning("IndexOutOfBoundsException occured while reading header! " + headerReadOutOfBounds.getMessage());			
                    }

                    // Get the lenght of the body directly at the byte level
                    final int bodyLength = byteArrayToInt(Arrays.copyOfRange(rawHeader, 19, 23));

                    // Allocate memory for header and body
                    byte[] totalPacketData = new byte[HEADER_SIZE + bodyLength];
                    System.arraycopy(rawHeader, 0, totalPacketData, 0, HEADER_SIZE);
                    rawHeader = null; // Free

                    try {
                            // read body and copy the body part
                            this.readUntilComplete(totalPacketData, HEADER_SIZE, bodyLength);
                    } catch (SocketException socketExc) {
                            if (socket.isClosed()) {
                                    // socket has been closed to throw EOF exception higher
                                    throw new java.io.EOFException();
                            }

                            throw socketExc;
                    } catch (EOFException bodyReadEof) {
                            RLOGGER.warning("EOF reached for input stream! " + bodyReadEof.getMessage());
                            throw new IOException("EOF reached for input stream!");
                    } catch (IOException bodyReadIo) {
                            RLOGGER.warning("Socket connection closed while reading!");
                    }
                    
    
            /* Same here!
	    StringBuilder sb = new StringBuilder();
	    sb.append("\nTCPIPTransportDataTransciever.readEncodedMessage()");
	    sb.append("\nReading from socket:");
	    sb.append("\n---------------------------------------");
	    sb.append("\ntotalPacketData headerLength: " + rawHeader.length + ", BodyLength: " + bodyLength + ", length: " + totalPacketData.length + "\n");
		for (byte b2 : totalPacketData) {
			sb.append(Integer.toString(b2 & 0xFF, 10) + " ");
		}
	    sb.append("\n---------------------------------------");
		RLOGGER.log(Level.FINEST, sb.toString());
		*/
            
		// if this is also a provider, update the localport to equal the port of the server socket.
//		RLOGGER.log(Level.FINE, "Local addr: " + to.toString());
//		RLOGGER.log(Level.FINE, "Remote addr: " + from.toString());
		
		return new TCPIPPacketInfoHolder(totalPacketData, from, to);
	}

	/**
	 * Close the socket
	 */
	@Override
	public void close() {
                closed = true;
                
		RLOGGER.log(Level.FINE, "Closing client connection at port {0}", socket.getLocalPort());
		
		try {
			socket.close();
		} catch (IOException e) {
			RLOGGER.warning("An exception occured while trying to close the socket! "
					+ e.getMessage());
			e.printStackTrace();
		}
	}
  
	/**
	 * Convert a 4-byte byte array to an integer.
	 * 
	 * @param b
	 * 		The byte array to convert
	 * @return int
	 */
	public static int byteArrayToInt(byte[] b) {
		return b[3] & 0xFF | (b[2] & 0xFF) << 8 | (b[1] & 0xFF) << 16
				| (b[0] & 0xFF) << 24;
	}
        
        private int readUntilComplete(final byte[] b, final int off, final int completeLength) throws IOException {
            int n;
            int len = 0;
            do {
                    n = socketReadIf.read(b, off + len, completeLength - len);
                    if (n != -1) {
                            len += n;
                    }else{
                            throw new SocketException("The socket read -1 from the input stream.");
                    }
            } while (len < completeLength);
            return len;
        }
}
