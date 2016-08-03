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
import esa.mo.mal.transport.http.api.AbstractPostClient;
import esa.mo.mal.transport.http.api.HttpApiImplException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * Extension of HTTPClientShutDown.
 * Additionally reads the encoded MAL message from the HTTP response and forwards it to the HTTPMessageReceiver
 * before shutting down the HTTP client (again running in a separate thread).
 */
public class HTTPClientProcessResponse extends HTTPClientShutDown
{
  protected final HTTPTransport transport;

  /**
   * Constructor.
   * 
   * @param client the AbstractPostClient
   * @param transport The parent HTTP transport.
   */
  public HTTPClientProcessResponse(AbstractPostClient client, HTTPTransport transport)
  {
    super(client);
    this.transport = transport;
  }

  @Override
  public void run()
  {
    try {
      processHttpResponse(client);
      client.shutDown();
    }
    catch (HttpApiImplException ex)
    {
      throw new RuntimeException("HTTPClientShutDown: HttpApiImplException at run()", ex);
    }
  }

  /**
   * Processes the HTTP response after submitting the request
   * 
   * @param client the AbstractPostClient
   * @throws HttpApiImplException in case an error occurs when trying to read the response from the HTTP client
   */
  protected void processHttpResponse(AbstractPostClient client) throws HttpApiImplException
  {
    int statusCode = client.getStatusCode(); // should always be "200 OK", except for INVOKE_ACK ("202 Accepted")
    
    URI uriTo = new URI(client.getResponseHeader("X-Client-Url"));
    URI uriFrom = new URI(client.getResponseReferer());
    String versionNumber = client.getResponseHeader("X-Version-Number"); // should be "001" according to 3.4.2 in recommended standard.
    String contentType = client.getResponseHeader("Content-Type"); // should be "text/xml; charset=utf-8" according to 3.4.3 in recommended standard.
    
    GENMessageHeader malMessageHeader = createMALHeaderFromHttp(client, uriTo, uriFrom);
    
    byte[] responseData = client.readFullResponseBody();
    
    HTTPMessageReceiver receiver = new HTTPMessageReceiver(transport, malMessageHeader);
    receiver.receive(responseData);
  }

  /**
   * Creates a MAL header from HTTP standard and custom responseHeader fields
   * 
   * @param client the AbstractPostClient
   * @param uriTo the URITo field
   * @param uriFrom the URIfrom field
   * @return the GENMessageHeader corresponding to the HTTP headers
   */
  protected GENMessageHeader createMALHeaderFromHttp(AbstractPostClient client, URI uriTo, URI uriFrom)
  {
    Blob authenticationId = null;
    if (HTTPTransport.authenticationIdFlag)
    {
      authenticationId = new Blob(HTTPTransport.hexStringToByteArray(client.getResponseHeader("X-Authentication-Id")));
    }
    Time timestamp = null;
    if (HTTPTransport.timestampFlag)
    {
      timestamp = new Time(Long.parseLong(client.getResponseHeader("X-Timestamp")));
    }
    QoSLevel qosLevel = QoSLevel.fromOrdinal(Integer.parseInt(client.getResponseHeader("X-QoSlevel")));
    UInteger priority = null;
    if (HTTPTransport.priorityFlag)
    {
      priority = new UInteger(Long.parseLong(client.getResponseHeader("X-Priority")));
    }
    IdentifierList domain = null;
    if (HTTPTransport.domainFlag)
    {
      String domainString = client.getResponseHeader("X-Domain");
      domain = new IdentifierList();
      for (String id : domainString.split(","))
      {
        domain.add(new Identifier(id));
      }
    }
    Identifier networkZone = null;
    if (HTTPTransport.networkZoneFlag)
    {
      networkZone = new Identifier(client.getResponseHeader("X-Network-Zone"));
    }
    SessionType session = SessionType.fromOrdinal(Integer.parseInt(client.getResponseHeader("X-Session")));
    Identifier sessionName = null;
    if (HTTPTransport.sessionNameFlag)
    {
      sessionName = new Identifier(client.getResponseHeader("X-Session-Name"));
    }
    InteractionType interactionType = InteractionType.fromOrdinal(Integer.parseInt(client.getResponseHeader("X-Interaction-Type")));
    UOctet interactionStage = null;
    if (interactionType.getOrdinal() != InteractionType._SEND_INDEX)
    {
      interactionStage = new UOctet(Short.parseShort(client.getResponseHeader("X-Interaction-Stage")));
    }
    Long transactionId = Long.parseLong(client.getResponseHeader("X-Transaction-Id"));
    UShort serviceArea = new UShort(Integer.parseInt(client.getResponseHeader("X-Service-Area")));
    UShort service = new UShort(Integer.parseInt(client.getResponseHeader("X-Service")));
    UShort operation = new UShort(Integer.parseInt(client.getResponseHeader("X-Operation")));
    UOctet areaVersion = new UOctet(Short.parseShort(client.getResponseHeader("X-Area-Version")));
    Boolean isErrorMessage = client.getResponseHeader("X-Is-Error-Message").equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE;

    return new GENMessageHeader(uriFrom, authenticationId, uriTo, timestamp, qosLevel, priority, domain, networkZone, session, sessionName, interactionType, interactionStage, transactionId, serviceArea, service, operation, areaVersion, isErrorMessage);
  }
}
