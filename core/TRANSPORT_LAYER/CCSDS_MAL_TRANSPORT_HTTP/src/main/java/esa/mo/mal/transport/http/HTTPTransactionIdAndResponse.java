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

import esa.mo.mal.transport.http.api.AbstractHttpResponse;


/**
 * Small class to hold the MAL message transaction id and open HTTP response.
 */
public class HTTPTransactionIdAndResponse
{
  protected final long transactionId;
  protected final AbstractHttpResponse openResponse;

  /**
   * Constructor
   * @param transactionId The transaction id
   * @param openResponse Open HTTP response
   */
  public HTTPTransactionIdAndResponse(long transactionId, AbstractHttpResponse openResponse)
  {
    this.transactionId = transactionId;
    this.openResponse = openResponse;
  }
}
