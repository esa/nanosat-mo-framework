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

import esa.mo.mal.transport.gen.GENEndpoint;
import esa.mo.mal.transport.gen.GENTransport;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * An implementation of the end point interface for TCP/IP.
 */
public class TCPIPEndpoint extends GENEndpoint
{
  /**
   * Constructor.
   *
   * @param transport Parent transport.
   * @param localName Endpoint local MAL name.
   * @param routingName Endpoint local routing name.
   * @param uri The URI string for this end point.
   * @param wrapBodyParts True if the encoded body parts should be wrapped in BLOBs.
   */
  public TCPIPEndpoint(GENTransport transport, String localName, String routingName, String uri, boolean wrapBodyParts)
  {
    super(transport, localName, routingName, uri, wrapBodyParts);
  }

  @Override
  public TCPIPMessageHeader createMessageHeader(final URI uriFrom,
          final Blob authenticationId,
          final URI uriTo,
          final Time timestamp,
          final QoSLevel qosLevel,
          final UInteger priority,
          final IdentifierList domain,
          final Identifier networkZone,
          final SessionType session,
          final Identifier sessionName,
          final InteractionType interactionType,
          final UOctet interactionStage,
          final Long transactionId,
          final UShort serviceArea,
          final UShort service,
          final UShort operation,
          final UOctet serviceVersion,
          final Boolean isErrorMessage)
  {
    return new TCPIPMessageHeader(transport,
            uriFrom,
            authenticationId,
            uriTo,
            timestamp,
            qosLevel,
            priority,
            domain,
            networkZone,
            session,
            sessionName,
            interactionType,
            interactionStage,
            transactionId,
            serviceArea,
            service,
            operation,
            serviceVersion,
            isErrorMessage);
  }
}
