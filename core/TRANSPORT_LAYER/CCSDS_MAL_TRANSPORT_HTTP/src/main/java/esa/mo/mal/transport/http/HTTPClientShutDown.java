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

import esa.mo.mal.transport.http.api.AbstractPostClient;
import esa.mo.mal.transport.http.api.HttpApiImplException;

/**
 * Implementation of the Runnable interface.
 * Allows shutting down the HTTP client in a separate thread
 * in case non-blocking wait for the HTTP response is needed.
 */
public class HTTPClientShutDown implements Runnable
{
  protected final AbstractPostClient client;

  /**
   * Constructor.
   * 
   * @param client the AbstractPostClient
   */
  public HTTPClientShutDown(AbstractPostClient client)
  {
    this.client = client;
  }

  @Override
  public void run()
  {
    try
    {
      int statusCode = client.getStatusCode(); // should always be "204 No Content"
      client.shutDown();
    }
    catch (HttpApiImplException ex)
    {
      throw new RuntimeException("HTTPClientShutDown: HttpApiImplException at run()", ex);
    }
  }
}
