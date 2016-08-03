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
import esa.mo.mal.transport.http.api.AbstractPostClient;
import esa.mo.mal.transport.http.api.HttpApiImplException;
import java.io.IOException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Extension of HTTPMessageSenderNoEncoding.
 * Additionally encodes the MAL message header separately over the HTTP headers.
 */
public class HTTPMessageSenderNoResponse extends HTTPMessageSenderNoEncoding
{

  /**
   * Constructor.
   *
   * @param transport The parent HTTP transport.
   * @param abstractPostClientImpl AbstractPostClient interface implementation
   */
  public HTTPMessageSenderNoResponse(HTTPTransport transport, String abstractPostClientImpl)
  {
    super(transport, abstractPostClientImpl);
  }

  @Override
  public void sendEncodedMessage(GENOutgoingMessageHolder packetData) throws IOException
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
      
      transport.runAsynchronousTask(new HTTPClientShutDown(client));
      threadSleep(10);
    }
    catch (HttpApiImplException ex)
    {
      throw new IOException("HTTPMessageSender: HttpApiImplException at sendEncodedMessageViaHttpClient()", ex);
    }
  }

  /**
   * Maps the MAL header fields from the message to HTTP custom headers of the client
   * 
   * @param malMessageHeader the MALMessageHeader
   * @param client the AbstractPostClient
   * @throws IOException in case an internal error occurs
   */
  protected void mapMALHeaderToHttp(MALMessageHeader malMessageHeader, AbstractPostClient client) throws IOException
  {
    if (HTTPTransport.authenticationIdFlag)
    {
      try
      {
        client.setRequestHeader("X-Authentication-Id", HTTPTransport.byteArrayToHexString(malMessageHeader.getAuthenticationId().getValue()));
      }
      catch (MALException ex)
      {
        throw new IOException("MALException", ex);
      }
    }
    if (HTTPTransport.timestampFlag)
    {
      client.setRequestHeader("X-Timestamp", String.valueOf(malMessageHeader.getTimestamp().getValue()));
    }
    client.setRequestHeader("X-QoSlevel", String.valueOf(malMessageHeader.getQoSlevel().getOrdinal()));
    if (HTTPTransport.priorityFlag)
    {
      client.setRequestHeader("X-Priority", String.valueOf(malMessageHeader.getPriority().getValue()));
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
      client.setRequestHeader("X-Domain", domainIdentifiers.toString());
    }
    if (HTTPTransport.networkZoneFlag)
    {
      client.setRequestHeader("X-Network-Zone", malMessageHeader.getNetworkZone().getValue());
    }
    client.setRequestHeader("X-Session", String.valueOf(malMessageHeader.getSession().getOrdinal()));
    if (HTTPTransport.sessionNameFlag)
    {
      client.setRequestHeader("X-Session-Name", malMessageHeader.getSessionName().getValue());
    }
    client.setRequestHeader("X-Interaction-Type", String.valueOf(malMessageHeader.getInteractionType().getOrdinal()));
    if (malMessageHeader.getInteractionType().getOrdinal() == InteractionType._SEND_INDEX)
    {
      client.setRequestHeader("X-Interaction-Stage", "");
    }
    else
    {
      client.setRequestHeader("X-Interaction-Stage", String.valueOf(malMessageHeader.getInteractionStage().getValue()));
    }
    client.setRequestHeader("X-Transaction-Id", String.valueOf(malMessageHeader.getTransactionId()));
    client.setRequestHeader("X-Service-Area", String.valueOf(malMessageHeader.getServiceArea().getValue()));
    client.setRequestHeader("X-Service", String.valueOf(malMessageHeader.getService().getValue()));
    client.setRequestHeader("X-Operation", String.valueOf(malMessageHeader.getOperation().getValue()));
    client.setRequestHeader("X-Area-Version", String.valueOf(malMessageHeader.getAreaVersion().getValue()));
    client.setRequestHeader("X-Is-Error-Message", String.valueOf(malMessageHeader.getIsErrorMessage()));
  }
}
