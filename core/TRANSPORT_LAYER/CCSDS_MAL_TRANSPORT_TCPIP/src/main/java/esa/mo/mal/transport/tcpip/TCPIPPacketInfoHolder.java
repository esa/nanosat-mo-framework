package esa.mo.mal.transport.tcpip;

import org.ccsds.moims.mo.mal.structures.URI;

/**
 * A container class which keeps the raw packet data of an incoming message, as
 * well as the TCP/IP address of both the source and the destination.
 * 
 * @author Rian van Gijlswijk <r.vangijlswijk@telespazio-vega.de>
 *
 */
public class TCPIPPacketInfoHolder {
	
	/**
	 * The raw packet data of an incoming message
	 */
	private byte[] packetData;
	
	/**
	 * The TCP/IP address of the source
	 */
	private URI tcpipFrom;
	
	/**
	 * The TCP/IP address of the destination
	 */
	private URI tcpipTo;
	
	public TCPIPPacketInfoHolder(byte[] packetData, URI from, URI to) {
		this.packetData = packetData;
		this.tcpipFrom = from;
		this.tcpipTo = to;
	}

	public byte[] getPacketData() {
		return packetData;
	}

	public void setPacketData(byte[] packetData) {
		this.packetData = packetData;
	}

	public URI getUriFrom() {
		return tcpipFrom;
	}

	public void setUriFrom(URI uriFrom) {
		this.tcpipFrom = uriFrom;
	}

	public URI getUriTo() {
		return tcpipTo;
	}

	public void setUriTo(URI uriTo) {
		this.tcpipTo = uriTo;
	}

}
