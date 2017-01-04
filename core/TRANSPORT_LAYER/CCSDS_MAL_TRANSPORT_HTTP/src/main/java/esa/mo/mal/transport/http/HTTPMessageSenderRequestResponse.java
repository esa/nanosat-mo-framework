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

import esa.mo.mal.transport.gen.sending.GENOutgoingMessageHolder;
import esa.mo.mal.transport.http.api.AbstractHttpResponse;
import esa.mo.mal.transport.http.api.AbstractPostClient;
import esa.mo.mal.transport.http.api.HttpApiImplException;
import java.io.IOException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Extension of HTTPMessageSenderNoResponse.
 * Additionally adds support for the HTTP request/response paradigm.
 * I.e. potentially encodes and delivers the MAL message via a HTTP response,
 * or possibly processes the HTTP response as received message for the parent transport.
 */
public class HTTPMessageSenderRequestResponse extends HTTPMessageSenderNoResponse
{

  /**
   * Constructor.
   *
   * @param transport The parent HTTP transport.
   * @param abstractPostClientImpl AbstractPostClient interface implementation
   */
  public HTTPMessageSenderRequestResponse(HTTPTransport transport, String abstractPostClientImpl)
  {
    super(transport, abstractPostClientImpl);
  }

  @Override
  public void sendEncodedMessage(GENOutgoingMessageHolder<byte[]> packetData) throws IOException
  {
    if (HTTPTransport.messageIsEncodedHttpResponse(packetData.getOriginalMessage().getHeader()))
    {
      sendEncodedMessageViaHttpResponse(packetData);
    }
    else
    {
      sendEncodedMessageViaHttpClient(packetData);
    }
  }

  /**
   * Sends the encoded message via an open HTTP response.
   * 
   * @param packetData the MALMessage
   * @throws IOException in case the message cannot be sent to the client
   */
  public void sendEncodedMessageViaHttpResponse(GENOutgoingMessageHolder<byte[]> packetData) throws IOException
  {
    MALMessageHeader malMessageHeader = packetData.getOriginalMessage().getHeader();
    AbstractHttpResponse httpResponse = transport.retrieveOpenHttpResponse(malMessageHeader.getURIFrom().getValue(), malMessageHeader.getTransactionId());
    if (httpResponse == null)
    {
      throw new IOException("HTTPMessageSender: httpResponse is NULL at sendEncodedMessageViaHttpResponse()");
    }
    
    try
    {
      if (malMessageHeader.getInteractionType().getOrdinal() == InteractionType._INVOKE_INDEX && malMessageHeader.getInteractionStage().getValue() == MALInvokeOperation._INVOKE_ACK_STAGE)
      {
        httpResponse.setStatusCode(202);
      }
      else
      {
        httpResponse.setStatusCode(200);
      }
      
      httpResponse.setHeader("X-Client-Url", malMessageHeader.getURITo().getValue());
      httpResponse.setReferer(malMessageHeader.getURIFrom().getValue());
      httpResponse.setHeader("X-Version-Number", "001"); // according to 3.4.2 in recommended standard.
      httpResponse.setHeader("Content-Type", "text/xml; charset=utf-8"); // according to 3.4.3 in recommended standard.
      
      mapMALHeaderToHttp(malMessageHeader, httpResponse);
      
      if (packetData.getEncodedMessage().length > 0)
      {
        httpResponse.writeFullBody(packetData.getEncodedMessage());
      }
      
      httpResponse.send();
    }
    catch (HttpApiImplException ex)
    {
      throw new IOException("HTTPMessageSender: HttpApiImplException at sendEncodedMessageViaHttpResponse()", ex);
    }
  }

  /**
   * Maps the MAL header fields from the message to HTTP custom headers of the httpResponse
   * 
   * @param malMessageHeader the MALMessageHeader
   * @param httpResponse the AbstractHttpResponse
   * @throws IOException in case an internal error occurs
   */
  protected void mapMALHeaderToHttp(MALMessageHeader malMessageHeader, AbstractHttpResponse httpResponse) throws IOException
  {
    if (HTTPTransport.authenticationIdFlag)
    {
      try
      {
        httpResponse.setHeader("X-Authentication-Id", HTTPTransport.byteArrayToHexString(malMessageHeader.getAuthenticationId().getValue()));
      }
      catch (MALException ex)
      {
        throw new IOException("MALException", ex);
      }
    }
    if (HTTPTransport.timestampFlag)
    {
      httpResponse.setHeader("X-Timestamp", String.valueOf(malMessageHeader.getTimestamp().getValue()));
    }
    httpResponse.setHeader("X-QoSlevel", String.valueOf(malMessageHeader.getQoSlevel().getOrdinal()));
    if (HTTPTransport.priorityFlag)
    {
      httpResponse.setHeader("X-Priority", String.valueOf(malMessageHeader.getPriority().getValue()));
    }
    if (HTTPTransport.domainFlag)
    {
      StringBuilder domainIdentifiers = new StringBuilder();
      int i = 0;
      int amount = malMessageHeader.getDomain().size();
      for (Identifier id : malMessageHeader.getDomain())
      {
        i++;
        domainIdentifiers.append(id.getValue());
        if (i < amount)
        {
          domainIdentifiers.append(",");
        }
      }
      httpResponse.setHeader("X-Domain", domainIdentifiers.toString());
    }
    if (HTTPTransport.networkZoneFlag)
    {
      httpResponse.setHeader("X-Network-Zone", malMessageHeader.getNetworkZone().getValue());
    }
    httpResponse.setHeader("X-Session", String.valueOf(malMessageHeader.getSession().getOrdinal()));
    if (HTTPTransport.sessionNameFlag)
    {
      httpResponse.setHeader("X-Session-Name", malMessageHeader.getSessionName().getValue());
    }
    httpResponse.setHeader("X-Interaction-Type", String.valueOf(malMessageHeader.getInteractionType().getOrdinal()));
    httpResponse.setHeader("X-Interaction-Stage", String.valueOf(malMessageHeader.getInteractionStage().getValue()));
    httpResponse.setHeader("X-Transaction-Id", String.valueOf(malMessageHeader.getTransactionId()));
    httpResponse.setHeader("X-Service-Area", String.valueOf(malMessageHeader.getServiceArea().getValue()));
    httpResponse.setHeader("X-Service", String.valueOf(malMessageHeader.getService().getValue()));
    httpResponse.setHeader("X-Operation", String.valueOf(malMessageHeader.getOperation().getValue()));
    httpResponse.setHeader("X-Area-Version", String.valueOf(malMessageHeader.getAreaVersion().getValue()));
    httpResponse.setHeader("X-Is-Error-Message", String.valueOf(malMessageHeader.getIsErrorMessage()));
  }

  /**
   * Sends an encoded message via the HTTP request of a HTTP Client.
   * 
   * @param packetData the MALMessage
   * @throws IOException in case the message cannot be sent to the client
   */
  protected void sendEncodedMessageViaHttpClient(GENOutgoingMessageHolder<byte[]> packetData) throws IOException
  {
    MALMessageHeader malMessageHeader = packetData.getOriginalMessage().getHeader();
    
    String remoteUrl = malMessageHeader.getURITo().getValue();
    if (transport.useHttps())
    {
      remoteUrl = remoteUrl.replaceAll("malhttp://", "https://");
    }
    else
    {
      remoteUrl = remoteUrl.replaceAll("malhttp://", "http://");
    }
    
    try
    {
      AbstractPostClient client = createPostClient();
      client.initAndConnectClient(remoteUrl, transport.useHttps(), transport.getKeystoreFilename(), transport.getKeystorePassword());
      
      client.setRequestReferer(malMessageHeader.getURIFrom().getValue());
      client.setRequestHeader("X-Version-Number", "001"); // according to 3.4.2 in recommended standard.
      client.setRequestHeader("Content-Type", "text/xml; charset=utf-8"); // according to 3.4.3 in recommended standard.
      
      mapMALHeaderToHttp(malMessageHeader, client);
      
      client.writeFullRequestBody(packetData.getEncodedMessage());
      client.sendRequest();
      
      if (HTTPTransport.messageHasEmtpyHttpResponse(malMessageHeader))
      {
        transport.runAsynchronousTask(new HTTPClientShutDown(client));
      }
      if (HTTPTransport.messageExpectsHttpResponse(malMessageHeader))
      {
        transport.runAsynchronousTask(new HTTPClientProcessResponse(client, transport));
      }
      threadSleep(10);
    }
    catch (HttpApiImplException ex)
    {
      throw new IOException("HTTPMessageSender: HttpApiImplException at sendEncodedMessageViaHttpClient()", ex);
    }
  }
}
