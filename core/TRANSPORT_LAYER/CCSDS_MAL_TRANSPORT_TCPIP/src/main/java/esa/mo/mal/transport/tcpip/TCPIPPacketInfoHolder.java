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

import org.ccsds.moims.mo.mal.structures.URI;

/**
 * A container class which keeps the raw packet data of an incoming message, as
 * well as the TCP/IP address of both the source and the destination.
 *
 * @author Rian van Gijlswijk
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
