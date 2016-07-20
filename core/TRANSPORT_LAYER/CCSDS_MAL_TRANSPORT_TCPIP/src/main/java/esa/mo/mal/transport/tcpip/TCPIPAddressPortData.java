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

import java.net.InetAddress;

/**
 * Small class to hold the TCP/IP address information.
 */
public class TCPIPAddressPortData
{
  protected final InetAddress fromAddr;
  protected final int fromPort;
  protected final byte[] encodedPacketData;

  /**
   * Constructor
   * @param fromAddr From InetAddress
   * @param fromPort From port
   * @param encodedPacketData Encoded message
   */
  public TCPIPAddressPortData(InetAddress fromAddr, int fromPort, byte[] encodedPacketData)
  {
    this.fromAddr = fromAddr;
    this.fromPort = fromPort;
    this.encodedPacketData = encodedPacketData;
  }
}
