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
import esa.mo.mal.transport.http.api.AbstractHttpRequest;
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
 * Extension of HTTPContextHandlerNoEncoding.
 * Additionally decodes the MAL message header separately from the HTTP headers.
 */
public class HTTPContextHandlerNoResponse extends HTTPContextHandlerNoEncoding
{
  protected GENMessageHeader malMessageHeader;

  /**
   * Constructor.
   *
   * @param transport The parent HTTP transport.
   */
  public HTTPContextHandlerNoResponse(HTTPTransport transport)
  {
    super(transport);
  }

  @Override
  public void processRequest(AbstractHttpRequest request) throws HttpApiImplException {
    String requestUrl = request.getRequestUrl();
    if (transport.useHttps())
    {
      requestUrl = requestUrl.replaceAll("https://", "malhttp://");
    }
    else
    {
      requestUrl = requestUrl.replaceAll("http://", "malhttp://");
    }
    URI uriTo = new URI(requestUrl);
    URI uriFrom = new URI(request.getReferer());
    String versionNumber = request.getHeader("X-Version-Number"); // should be "001" according to 3.4.2 in recommended standard.
    String contentType = request.getHeader("Content-Type"); // should be "text/xml; charset=utf-8" according to 3.4.3 in recommended standard.
    
    malMessageHeader = createMALHeaderFromHttp(request, uriTo, uriFrom);
    
    data = request.readFullBody();
  }

  /**
   * Creates a MAL header from HTTP standard and custom requestHeader fields
   * 
   * @param request the AbstractHttpRequest
   * @param uriTo the URITo field
   * @param uriFrom the URIfrom field
   * @return the GENMessageHeader corresponding to the HTTP headers
   */
  protected GENMessageHeader createMALHeaderFromHttp(AbstractHttpRequest request, URI uriTo, URI uriFrom)
  {
    Blob authenticationId = null;
    if (HTTPTransport.authenticationIdFlag)
    {
      authenticationId = new Blob(HTTPTransport.hexStringToByteArray(request.getHeader("X-Authentication-Id")));
    }
    Time timestamp = null;
    if (HTTPTransport.timestampFlag)
    {
      timestamp = new Time(Long.parseLong(request.getHeader("X-Timestamp")));
    }
    QoSLevel qosLevel = QoSLevel.fromOrdinal(Integer.parseInt(request.getHeader("X-QoSlevel")));
    UInteger priority = null;
    if (HTTPTransport.priorityFlag)
    {
      priority = new UInteger(Long.parseLong(request.getHeader("X-Priority")));
    }
    IdentifierList domain = null;
    if (HTTPTransport.domainFlag)
    {
      String domainString = request.getHeader("X-Domain");
      domain = new IdentifierList();
      for (String id : domainString.split(","))
      {
        domain.add(new Identifier(id));
      }
    }
    Identifier networkZone = null;
    if (HTTPTransport.networkZoneFlag)
    {
      networkZone = new Identifier(request.getHeader("X-Network-Zone"));
    }
    SessionType session = SessionType.fromOrdinal(Integer.parseInt(request.getHeader("X-Session")));
    Identifier sessionName = null;
    if (HTTPTransport.sessionNameFlag)
    {
      sessionName = new Identifier(request.getHeader("X-Session-Name"));
    }
    InteractionType interactionType = InteractionType.fromOrdinal(Integer.parseInt(request.getHeader("X-Interaction-Type")));
    UOctet interactionStage = null;
    if (interactionType.getOrdinal() != InteractionType._SEND_INDEX)
    {
      interactionStage = new UOctet(Short.parseShort(request.getHeader("X-Interaction-Stage")));
    }
    Long transactionId = Long.parseLong(request.getHeader("X-Transaction-Id"));
    UShort serviceArea = new UShort(Integer.parseInt(request.getHeader("X-Service-Area")));
    UShort service = new UShort(Integer.parseInt(request.getHeader("X-Service")));
    UShort operation = new UShort(Integer.parseInt(request.getHeader("X-Operation")));
    UOctet areaVersion = new UOctet(Short.parseShort(request.getHeader("X-Area-Version")));
    Boolean isErrorMessage = request.getHeader("X-Is-Error-Message").equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE;

    return new GENMessageHeader(uriFrom, authenticationId, uriTo, timestamp, qosLevel, priority, domain, networkZone, session, sessionName, interactionType, interactionStage, transactionId, serviceArea, service, operation, areaVersion, isErrorMessage);
  }

  @Override
  public void finishHandling()
  {
    HTTPMessageReceiver receiver = new HTTPMessageReceiver(transport, malMessageHeader);
    receiver.receive(data);
  }
}
