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

import esa.mo.mal.transport.gen.sending.GENMessageSender;
import esa.mo.mal.transport.gen.sending.GENOutgoingMessageHolder;
import esa.mo.mal.transport.http.api.AbstractPostClient;
import esa.mo.mal.transport.http.api.HttpApiImplException;
import java.io.IOException;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * The implementation of the GENMessageSender interface for HTTP transport.
 * Encodes and delivers the MAL message via a HTTP request.
 */
public class HTTPMessageSenderNoEncoding implements GENMessageSender<byte[]>
{
  protected final HTTPTransport transport;
  protected final String abstractPostClientImpl;

  /**
   * Constructor.
   *
   * @param transport The parent HTTP transport.
   * @param abstractPostClientImpl AbstractPostClient interface implementation
   */
  public HTTPMessageSenderNoEncoding(HTTPTransport transport, String abstractPostClientImpl)
  {
    this.transport = transport;
    this.abstractPostClientImpl = abstractPostClientImpl;
  }

  /**
   * Lets the current thread sleep during the specified number of milliseconds.
   * 
   * @param millis the duration of time to sleep in milliseconds
   */
  protected void threadSleep(int millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException ex)
    {
      // do nothing
    }
  }

  @Override
  public void sendEncodedMessage(GENOutgoingMessageHolder<byte[]> packetData) throws IOException
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
   * Creates an instance of the AbstractPostClient interface.
   * 
   * @return the AbstractPostClient implementation
   * @throws HttpApiImplException in case an error occurs when trying to instantiate the AbstractPostClient
   */
  protected AbstractPostClient createPostClient() throws HttpApiImplException
  {
    try
    {
      AbstractPostClient clientImpl = (AbstractPostClient) Class.forName(abstractPostClientImpl).newInstance();
      return clientImpl;
    }
    catch (ClassNotFoundException ex)
    {
      throw new HttpApiImplException("HTTPMessageSender: ClassNotFoundException at createPostClient()", ex);
    }
    catch (InstantiationException ex)
    {
      throw new HttpApiImplException("HTTPMessageSender: InstantiationException at createPostClient()", ex);
    }
    catch (IllegalAccessException ex)
    {
      throw new HttpApiImplException("HTTPMessageSender: IllegalAccessException at createPostClient()", ex);
    }
  }

  @Override
  public void close()
  {
    // nothing to close
  }
}
