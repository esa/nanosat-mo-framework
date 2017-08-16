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

import esa.mo.mal.encoder.tcpip.TCPIPFixedBinaryStreamFactory;
import static esa.mo.mal.transport.tcpip.TCPIPTransport.RLOGGER;
import esa.mo.mal.encoder.tcpip.TCPIPMessageDecoderFactory;
import esa.mo.mal.transport.gen.GENEndpoint;
import esa.mo.mal.transport.gen.GENMessage;
import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.gen.GENTransport;
import esa.mo.mal.transport.gen.sending.GENMessageSender;
import esa.mo.mal.transport.gen.sending.GENOutgoingMessageHolder;
import esa.mo.mal.transport.gen.util.GENMessagePoller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransportFactory;

/**
 * The TCPIP MAL Transport implementation.
 *
 * The following properties configure the transport:
 *
 * org.ccsds.moims.mo.mal.transport.tcpip.debug 
 * 		== debug mode , affects logging 
 * org.ccsds.moims.mo.mal.transport.tcpip.host 
 * 		== adapter (host / IP Address) that the transport will use for incoming connections. 
 * 		In case of a pure client (i.e. not offering any services) this property should be omitted. 
 * org.ccsds.moims.mo.mal.transport.tcpip.port 
 * 		== port that the transport listens to. In case this is a pure client, this property should be omitted.
 *
 * The general logic is the following: The transport at first initializes the server listen port (if this is a server,
 * offering services). On receiving a request to send a MAL Message the transport tries to find if it has allocated some recourses
 * associated with the target URI (has already the means to exchange data with it) and if not, it creates
 * -numconnections- connections to the target server. If a client has already opened a connection to a server the server
 * will re-use that communication channel to send back data to the client.
 *
 * On the server, each incoming connection is handled separately by a different thread which on the first message
 * reception associates the remote URI with the connection (socket). This has the consequence that if the server wants
 * to either use a service, or reply to the remote URI, it will use on of these already allocated communication
 * resources.
 *
 * In the case of malformed MAL messages or communication errors, all resources related to the remote URI are released
 * and need to be reestablished.
 *
 * URIs:
 *
 * The TCPIP Transport generates URIs in the form of: {@code maltcp://<host>:<port or client ID>/<service id>}
 *
 * If a MAL instance does not offer any services then all of its endpoints get a Client URI. If a MAL instance offers at
 * least one service then all of its endpoints get a Server URI. A service provider communicates with a service consumer
 * with the communication channel that the service consumer initiated (uses bidirectional TCP/IP communication).
 *
 */
public class TCPIPTransport extends GENTransport<byte[], byte[]> {
	
	/**
	 * Logger
	 */
	public static final java.util.logging.Logger RLOGGER = Logger.getLogger("org.ccsds.moims.mo.mal.transport.tcpip");

	/**
	 * Port delimiter
	 */
	private static final char PORT_DELIMITER = ':';
	
	/**
	 * The server port that the TCP transport listens for incoming connections
	 */
	private int serverPort;

        /**
	 * Server host, this can be one of the IP Addresses / hostnames of the host.
	 */
	private final String serverHost;

        /**
         * The client port that the TCP transport uses as unique identifier for the
         * client on its host
         */
        private final int clientPort;
    
        /**
         * Client host, this can be one of the IP Addresses / hostnames of the host
         */
        private final String clientHost;
        
	/**
	 * Holds the server connection listener
	 */
	private TCPIPServerConnectionListener serverConnectionListener = null;

        private boolean autohost = false;
        private ServerSocket serverSocket;
        
        private final Map<String, Integer> socketsList = new HashMap<String, Integer>();

        /**
	 * Holds the list of data poller threads
	 */
	private final List<GENMessagePoller> messagePollerThreadPool = new ArrayList<GENMessagePoller>();

	/**
	 * Constructor. Configures host/port and debug settings.
	 *
	 * @param protocol
	 *            The protocol string.
	 * @param serviceDelim
	 *            The delimiter to use for separating the URL
	 * @param supportsRouting
	 *            True if routing is supported by the naming convention
	 * @param factory
	 *            The factory that created us.
	 * @param properties
	 *            The QoS properties.
	 * @throws MALException
	 *             On error.
	 */
	@SuppressWarnings("rawtypes")
	public TCPIPTransport(final String protocol, final char serviceDelim,
			final boolean supportsRouting, final MALTransportFactory factory,
			final java.util.Map properties) throws MALException {
		super(protocol, serviceDelim, supportsRouting, false, factory, properties);

		RLOGGER.fine("TCPIPTransport (constructor)");

		// decode configuration
		if (properties != null) {
                        if (properties.containsKey("org.ccsds.moims.mo.mal.transport.tcpip.autohost")) {
                                if ("true".equals((String) properties.get("org.ccsds.moims.mo.mal.transport.tcpip.autohost"))) {
                                        // Get the local address...
                                        properties.put("org.ccsds.moims.mo.mal.transport.tcpip.host", getDefaultHost());
                                        autohost = true;
                                }
                        }
            
			// host / ip adress
			if (properties.containsKey("org.ccsds.moims.mo.mal.transport.tcpip.host")) {
                                //this is a server
                                String hostName = (String) properties.get("org.ccsds.moims.mo.mal.transport.tcpip.host");
                                try {
                                        this.serverHost = InetAddress.getByName(hostName).getHostAddress();
                                } catch (UnknownHostException ex) {
                                        RLOGGER.log(Level.WARNING, "Cannot convert server hostname from properties file to IP address", ex);
                                        throw new MALException("Cannot convert server hostname from properties file to IP address", ex);
                                }
                                this.clientHost = null;
			} else {
                                //this is a client
                                this.serverHost = null;
                                this.clientHost = getDefaultHost();
			}

			// port
                        if (serverHost != null) {
                                //this is a server
                                if (properties.containsKey("org.ccsds.moims.mo.mal.transport.tcpip.port")) {
                                        try {
                                                this.serverPort = Integer.parseInt((String) properties.get("org.ccsds.moims.mo.mal.transport.tcpip.port"));
                                                InetAddress serverHostAddr = InetAddress.getByName(serverHost);
                                                serverSocket = new ServerSocket(this.serverPort, 0, serverHostAddr);
                                        } catch (NumberFormatException ex) {
                                                RLOGGER.log(Level.WARNING, "Cannot parse server port number from properties file to Integer", ex);
                                                throw new MALException("Cannot parse server port number from properties file to Integer", ex);
                                        } catch (UnknownHostException ex) {
                                                Logger.getLogger(TCPIPTransport.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (IOException ex) {
                                                Logger.getLogger(TCPIPTransport.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                } else {
                                        try {
                                                //use default port
                                                InetAddress serverHostAddr = InetAddress.getByName(serverHost);
                                                int portNumber = 1024;  // Default it to 1024

                                                while (true) {
                                                        try {
                                                                serverSocket = new ServerSocket(portNumber, 0, serverHostAddr);
                                                                break;
                                                        } catch (Exception ex) {
                                                                if (autohost) {
                                                                        RLOGGER.log(Level.FINE, "Port " + portNumber + " already in use...");
                                                                        portNumber += 1;
                                                                } else {
                                                                        throw new MALException("Error initialising TCP Server", ex);
                                                                }
                                                        }
                                                }
                                                this.serverPort = portNumber;
                                        } catch (UnknownHostException ex) {
                                                Logger.getLogger(TCPIPTransport.class.getName()).log(Level.SEVERE, null, ex);
                                        }
                                }
                                this.clientPort = 0;
                        } else {
                                //this is a client
                                this.serverPort = 0;
                                this.clientPort = getRandomClientPort();
                        }
			
			// debug mode
			if (properties.containsKey("org.ccsds.moims.mo.mal.transport.tcpip.debug")) {
				Object level = properties.get("org.ccsds.moims.mo.mal.transport.tcpip.debug");
				
				
				if (level != null) {
					try {
						ConsoleHandler handler = new ConsoleHandler();
						Level parsedLevel = Level.parse(level.toString());
						RLOGGER.setLevel(parsedLevel);
						handler.setLevel(parsedLevel);
						RLOGGER.info("Setting logger to level " + RLOGGER.getLevel());
						RLOGGER.addHandler(handler);
					} catch(IllegalArgumentException ex) {
						RLOGGER.log(Level.WARNING, "The debug level supplied by the parameter"
								+ "org.ccsds.moims.mo.mal.transport.tcpip.debug does not exist!"
								+ "Please provide a java-logging compatible debug level.");
					}
				}
			}
						  
		} else {
                        // default values, this is a client
                        this.serverHost = null;
                        this.serverPort = 0;
                        this.clientHost = getDefaultHost();
                        this.clientPort = getRandomClientPort();
		}

		RLOGGER.log(Level.FINE, "TCPIP Wrapping body parts set to  : {0}", this.wrapBodyParts);
	}

	/**
	 * Initialize a server socket, if this is a provider
         * @throws org.ccsds.moims.mo.mal.MALException
	 */
	@Override
	public void init() throws MALException {
		super.init();
		RLOGGER.fine("TCPIPTransport.init()");

		// Is it a server?
		if (serverHost != null) {
			// start server socket on predefined port / interface
                        try {
                                // create thread that will listen for connections
                                synchronized (this) {
                                    serverConnectionListener = new TCPIPServerConnectionListener(this, serverSocket);
                                    serverConnectionListener.start();
                                }
                                // RLOGGER.log(Level.INFO, "Started TCP Server Transport on port {0}", serverPort);
                        } catch (Exception ex) {
                                throw new MALException("Error initialising TCP Server", ex);
                        }
                }

	}

	@Override
	public MALBrokerBinding createBroker(final String localName,
			final Blob authenticationId, final QoSLevel[] expectedQos,
			final UInteger priorityLevelNumber, final Map defaultQoSProperties)
			throws MALException {
		RLOGGER.fine("TCPIPTransport.createBroker()");
		// not support by TCPIP transport
		return null;
	}

	@Override
	public MALBrokerBinding createBroker(final MALEndpoint endpoint,
			final Blob authenticationId, final QoSLevel[] qosLevels,
			final UInteger priorities, final Map properties)
			throws MALException {
		RLOGGER.info("TCPIPTransport.createBroker() 2");
		// not support by TCPIP transport
		return null;
	}

	/**
	 * The MAL TCPIP binding supports SEND, SUBMIT, REQUEST, INVOKE and
	 * PROGRESS. PUBSUB is not supported. A MAL implementation layer has to
	 * support PUBSUB itself.
         * @param type
         * @return 
	 */
	@Override
	public boolean isSupportedInteractionType(final InteractionType type) {
		return InteractionType.PUBSUB.getOrdinal() != type.getOrdinal();
	}

	/**
	 * The MAL TCPIP binding supports all QoS levels.
         * @param qos
         * @return 
	 */
	@Override
	public boolean isSupportedQoSLevel(final QoSLevel qos) {
		return true;
	}

	/**
	 * Close all pollers and socket connections and then close the transport
	 * itself.
         * @throws org.ccsds.moims.mo.mal.MALException
	 */
	@Override
	public void close() throws MALException {
		RLOGGER.info("Closing TCPIPTransport...");
                
		synchronized (this) {
			TCPIPConnectionPoolManager.INSTANCE.close();
			
			for (GENMessagePoller entry : messagePollerThreadPool) {
				entry.close();
			}

			messagePollerThreadPool.clear();
		}

		super.close();

		synchronized (this) {
			if (null != serverConnectionListener) {
				serverConnectionListener.interrupt();

			}
		}
	}
	
	/**
	 * Internal method for encoding the message.
	 *
	 * @param destinationRootURI
	 *            The destination root URI.
	 * @param destinationURI
	 *            The complete destination URI.
	 * @param multiSendHandle
	 *            Handle for multi send messages.
	 * @param lastForHandle
	 *            true if last message in a multi send.
	 * @param targetURI
	 *            The target URI.
	 * @param msg
	 *            The message to send.
	 * @return The message holder for the outgoing message.
	 * @throws Exception
	 *             if an error.
	 */
	@Override
	protected GENOutgoingMessageHolder<byte[]> internalEncodeMessage(
			final String destinationRootURI, final String destinationURI,
			final Object multiSendHandle, final boolean lastForHandle,
			final String targetURI, final GENMessage msg) throws Exception {
		try {
			// try to encode the TCPIP Message
			final ByteArrayOutputStream lowLevelOutputStream = new ByteArrayOutputStream();
			((TCPIPMessage) msg).encodeMessage(getStreamFactory(), lowLevelOutputStream);
			byte[] data = lowLevelOutputStream.toByteArray();

			// message is encoded!
			LOGGER.log(Level.FINE, "GEN Sending data to {0} : {1}", new Object[] { targetURI, new PacketToString(data) });

			return new GENOutgoingMessageHolder<byte[]>(10, destinationRootURI, destinationURI, multiSendHandle, lastForHandle, msg, data);
		} catch (MALException ex) {
			LOGGER.log(Level.SEVERE, "GEN could not encode message!", ex);
			throw new MALTransmitErrorException(msg.getHeader(), new MALStandardError(MALHelper.BAD_ENCODING_ERROR_NUMBER, null), null);
		}
	}
  
	/**
	 * Create an endpoint. This endpoint has an url assigned, which is made of
	 * the base url plus the service identifier.
	 */
	@Override
	protected GENEndpoint internalCreateEndpoint(final String localName,
			final String routingName, final Map properties) throws MALException {
		RLOGGER.fine("TCPIPTransport.internalCreateEndpoint() with uri: " + uriBase);
		
		return new TCPIPEndpoint(this, localName, routingName, uriBase + routingName, wrapBodyParts);
	}

	/**
	 * Create a transport address.
	 * 
	 * If this is a pure client, create a new client socket. The local port that
	 * is automatically assigned to this socket (the ephemeral socket) will be
	 * used as the port number for this client.
	 * 
	 * If this is a provider, the host and port as defined in the configuration
	 * file are used.
         * @return 
         * @throws org.ccsds.moims.mo.mal.MALException
	 */
	@Override
	protected String createTransportAddress() throws MALException {
		String addr;
		if (serverHost == null) {
			addr = clientHost + PORT_DELIMITER + clientPort;
		} else {
			// this a server (and potentially a client)
			addr = serverHost + PORT_DELIMITER + serverPort;
		}

		RLOGGER.info("Transport address created is " + addr);
		
		return addr;
	}  

        @Override
        public GENMessage createMessage(byte[] packet) throws MALException {
            return new GENMessage(wrapBodyParts, true, new GENMessageHeader(), qosProperties, packet, getStreamFactory());
        }

        /**
	 * Called for received messages.
	 * 
	 * The URI from and URI to parameters for the message header are set according to the address
	 * information. Later, during decoding, the full URL is formed from the information in the message
	 * header.
	 * 
	 * The raw message data is split up in a packet, describing the header, and a packet for the body.
	 * These are each decoded separately; the header is decoded using an implementation that follows the
	 * MAL TCPIP Transport Binding specification. The body is decoded using whatever we have selected.
     * @param packetInfo
     * @return 
     * @throws org.ccsds.moims.mo.mal.MALException
	 */
	public GENMessage createMessage(final TCPIPPacketInfoHolder packetInfo) throws MALException {
                String serviceDelimStr = Character.toString(serviceDelim);
		String from = packetInfo.getUriFrom().getValue();
		if (!from.endsWith(serviceDelimStr)) {
			from += serviceDelimStr;
		}
		String to = packetInfo.getUriTo().getValue();
		if (!to.endsWith(serviceDelimStr)) {
			to += serviceDelimStr;
		}
		
		// preset header
		TCPIPMessageHeader header = new TCPIPMessageHeader(new URI(from), new URI(to));
		
		// msg with decoded header and empty body
		byte[] packetData = packetInfo.getPacketData();

                // Header must be always Fixed Binary
		TCPIPMessage msg = new TCPIPMessage(wrapBodyParts, header, qosProperties, packetData, new TCPIPFixedBinaryStreamFactory());
		
		int decodedHeaderBytes = ((TCPIPMessageHeader)msg.getHeader()).decodedHeaderBytes;
		int bodySize = ((TCPIPMessageHeader)msg.getHeader()).getBodyLength() + 23 - decodedHeaderBytes;
		
		// copy body to separate packet
		byte[] bodyPacketData = new byte[bodySize];
		System.arraycopy(packetData, decodedHeaderBytes, bodyPacketData, 0, bodySize);

		// debug information
                /*
		StringBuilder sb = new StringBuilder();
		sb.append("\nTCPIPTransport.createMessage() Header results:\n");
		sb.append(msg.getHeader().toString());
		sb.append("\nTCPIPTransport.createMessage() TRYING TO DECODE BODY");
		sb.append("\n---------------------------------------");
		sb.append("\nTCPIPTransport.createMessage() Total msg in bytes:\n");
		for (byte b2 : packetData) {
			sb.append(Integer.toString(b2 & 0xFF, 10) + " ");
		}
		sb.append("\nDecoded header bytes: " + decodedHeaderBytes);
		sb.append("\nBody: sz=" + bodyPacketData.length + " contents=\n");
		sb = new StringBuilder();
		for (byte b2 : packetData) {
			sb.append(Integer.toString(b2 & 0xFF, 10) + " ");
		}
		sb.append("\n---------------------------------------");
		RLOGGER.log(Level.FINEST, sb.toString());
		*/
                
		// decode the body
		TCPIPMessage messageWithBody = new TCPIPMessage(wrapBodyParts, (TCPIPMessageHeader)msg.getHeader(), qosProperties,
				bodyPacketData, getStreamFactory());
                
		return messageWithBody;
	}
        
        
	/**
	 * Create a message sender which will send out the message passed to this
	 * method. If no socket exists yet on the port defined by the destination
	 * port of the message, a new socket will be created and stored in the
	 * connection pool.
	 * 
	 * Create also a data reader thread for this socket in order to read
	 * messages from it no need to register this as it will automatically
	 * terminate when the underlying connection is terminated.
	 */
	@Override
	protected GENMessageSender<byte[]> createMessageSender(GENMessage msg,
			String remoteRootURI) throws MALException,
			MALTransmitErrorException {
		RLOGGER.fine("TCPIPTransport.createMessageSender()");
		try {
                        // create a message sender and receiver for the socket
                        Integer localPort = socketsList.get(remoteRootURI);
                        
                        if(localPort == null){ // Assign a different client port per remote location
                            localPort = this.getRandomClientPort();
                            socketsList.put(remoteRootURI, localPort);
                        }
                        
			ConnectionTuple toCt = getConnectionParts(remoteRootURI);
                        
			Socket s = TCPIPConnectionPoolManager.INSTANCE.get(localPort);
                        
                        try{
                                s.connect(new InetSocketAddress(toCt.host, toCt.port));
                        }catch(IOException exc){
                                RLOGGER.warning("A problem was detected! Assigning a new consumer port...");
                            
                                localPort = this.getRandomClientPort();
                                socketsList.put(remoteRootURI, localPort);
        			s = TCPIPConnectionPoolManager.INSTANCE.get(localPort);
                                s.connect(new InetSocketAddress(toCt.host, toCt.port));
                        }

                        TCPIPTransportDataTransceiver trans = createDataTransceiver(s);
		    
			GENMessagePoller messageReceiver = new GENMessagePoller(this, trans,
					trans, new TCPIPMessageDecoderFactory());
			messageReceiver.setRemoteURI(remoteRootURI);
			messageReceiver.start();

			messagePollerThreadPool.add(messageReceiver);

			return trans;
		} catch (NumberFormatException nfe) {
			LOGGER.log(Level.WARNING, "Have no means to communicate with client URI : {0}", remoteRootURI);
			throw new MALException("Have no means to communicate with client URI : " + remoteRootURI);
		} catch (UnknownHostException e) {
			LOGGER.log(Level.WARNING, "TCPIP could not find host :{0}", remoteRootURI);
			LOGGER.log(Level.FINE, "TCPIP could not find host  :" + remoteRootURI, e);
			throw new MALTransmitErrorException(msg.getHeader(),
					new MALStandardError(MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER, null), null);
		} catch (java.net.ConnectException e) {
			LOGGER.log(Level.WARNING, "TCPIP could not connect to : {0}", remoteRootURI);
			LOGGER.log(Level.FINE, "TCPIP could not connect to : " + remoteRootURI, e);
			throw new MALTransmitErrorException(
					msg.getHeader(),
					new MALStandardError(
							MALHelper.DESTINATION_TRANSIENT_ERROR_NUMBER, null), null);
		} catch (IOException e) {
			// there was a communication problem, we need to clean up the
			// objects we created in the meanwhile
			LOGGER.log(Level.WARNING, "TCPIP could not connect to : " + remoteRootURI, e);
			communicationError(remoteRootURI, null);

			// rethrow for higher MAL leyers
			throw new MALTransmitErrorException(msg.getHeader(), new MALStandardError(MALHelper.DELIVERY_FAILED_ERROR_NUMBER, null), null);
		}
	}

  /**
   * Allows transport derived from this, where the message encoding is changed for example, to easily replace the
   * message transceiver without worrying about the TCPIP connection
   *
   * @param socket the TCPIP socket
   * @return the new transceiver
   * @throws IOException if there is an error
   */
  protected TCPIPTransportDataTransceiver createDataTransceiver(Socket socket) throws IOException
  {
		RLOGGER.fine("TCPIPTransport.createDataTransceiver()");
		return new TCPIPTransportDataTransceiver(socket, ((clientPort == 0) ? serverPort : clientPort));
  }

  /**
   * Provide a default IP address for this host
   *
   * @return The transport specific address part.
   * @throws MALException On error
   */
	private String getDefaultHost() throws MALException {
		try {
			// Build url string
//			final InetAddress addr = Inet4Address.getLocalHost();
//                        String hAddress = addr.getHostAddress();
                        
                        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
                        
                        for (NetworkInterface netint : Collections.list(nets)){
                            if(!netint.isLoopback()){
                                for (InetAddress inetAddress : Collections.list(netint.getInetAddresses())) {
                                    if (inetAddress instanceof Inet6Address) {
                                        // Let's skip the support for IPv6 for the autohost mechanism!
                                        continue;
                                    }

                                    final StringBuilder hostAddress = new StringBuilder();
                                    hostAddress.append(inetAddress.getHostAddress());
                                    return hostAddress.toString();                                
                                }                
                            }
                        }

/*                        
			final StringBuilder hostAddress = new StringBuilder();
			if (addr instanceof Inet6Address) {
				RLOGGER.fine("TCPIP Address class is IPv6");
				hostAddress.append('[');
				hostAddress.append(hAddress);
				hostAddress.append(']');
			} else {
				hostAddress.append(hAddress);
			}

			return hostAddress.toString();
                        */
//		} catch (UnknownHostException ex) {
//			throw new MALException("Could not determine local host address", ex);
		} catch (SocketException ex) {
                        Logger.getLogger(TCPIPTransport.class.getName()).log(Level.SEVERE, null, ex);
                }

                throw new MALException("Could not determine local host address");
	}

	public char getServiceDelim() {
		return this.serviceDelim;
	}
	
	/**
	 * Get a tuple from a URI which contains the host and port information.
	 * 
	 * @param addr
	 * @return
	 * @throws MALException
	 */
	private ConnectionTuple getConnectionParts(String addr) throws MALException {
		
		// decode address
		String targetAddress = addr.replaceAll(protocol + protocolDelim, "");
		targetAddress = targetAddress.replaceAll(protocol, "");
		
		// remove service URI part, i.e. the part after the service delimiter
		int serviceIdx = targetAddress.indexOf(serviceDelim);
		if (serviceIdx >= 0) {
			targetAddress = targetAddress.substring(0, serviceIdx);
		}
		targetAddress = targetAddress.replaceAll(Character.toString(serviceDelim), "");

		if (!targetAddress.contains(":")) {
			// malformed URI
			throw new MALException("Malformed URI:" + addr);
		}

		String host = targetAddress.split(":")[0];
		int port = Integer.parseInt(targetAddress.split(":")[1]);
		return new ConnectionTuple(host, port);
	}

	/**
	 * A container class storing the host and port of some address.
         * @author Rian van Gijlswijk
	 *
	 */
	public static class ConnectionTuple {
		public String host;
		public int port;
		public ConnectionTuple(String h, int p) {
			this.host = h;
			this.port = p;
		}
	}
        
        /**
         * This method returns a random port number to be used for differentiating
         * different MAL instances in the same host.
         *
         * @return the random, host unique port number
         */
        private int getRandomClientPort() {
                // By default the server ports will start on the 1024 range, So we can
                // exclude the first 1000 range from being hit by nasty randomness
                int min = 2024;
                int max = 65536;
                return new Random().nextInt(max - min) + min;
        }
        
}
