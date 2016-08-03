/* ----------------------------------------------------------------------------
 * Copyright (C) 2014      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : CCSDS MO HTTP Transport Framework
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
package esa.mo.mal.transport.http;

import esa.mo.mal.transport.gen.GENMessageHeader;
import esa.mo.mal.transport.http.HTTPIncomingHeaderAndBodyMessageDecoderFactory.HTTPIncomingHeaderAndBodyMessageDecoder;

/**
 * The HTTP message receiver.
 * Holds a reference to the transport instance that created it and defines a single method for the reception of an encoded message.
 */
public class HTTPMessageReceiver
{
  protected final HTTPTransport transport;
  protected final GENMessageHeader header;

  /**
   * Creates a new instance of HTTPMessageReceiver
   *
   * @param transport The transport instance to pass received messages to.
   * @param header The message header.
   */
  public HTTPMessageReceiver(HTTPTransport transport, GENMessageHeader header)
  {
    this.transport = transport;
    this.header = header;
  }

  /**
   * Used to pass an encoded message to a HTTP Transport instance.
   *
   * @param packet The encoded message.
   */
  public void receive(final byte[] packet)
  {
    transport.receive(null, new HTTPIncomingHeaderAndBodyMessageDecoder(transport, new HTTPHeaderAndBody(header, packet)));
  }
}
