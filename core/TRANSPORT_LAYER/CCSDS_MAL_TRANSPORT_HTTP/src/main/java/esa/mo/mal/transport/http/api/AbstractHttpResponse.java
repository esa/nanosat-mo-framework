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
package esa.mo.mal.transport.http.api;

/**
 * A generic HTTP response object.
 * 
 * Provides standardised access to all necessary fields and data needed by hiding the usage of specific libraries.
 */
public interface AbstractHttpResponse {
    
    /**
     * Sets the HTTP status-code for the response message, e.g. "200 OK", "202 Accepted" or "204 No Content".
     * 
     * @param statusCode the 3-digit integer code describing the status
     */
    public void setStatusCode(int statusCode);
    
    /**
     * Sets the "referer" header field of the HTTP response.
     * 
     * @param referer the response referer
     */
    public void setReferer(String referer);
    
    /**
     * Sets the header field value of the given header field name.
     * 
     * Needs to consider AbstractPostClient.EMPTY_STRING_PLACEHOLDER.
     * 
     * @param headerName the header field name
     * @param headerValue the header field value
     */
    public void setHeader(String headerName, String headerValue);
    
    /**
     * Writes the (possibly empty) full HTTP message body.
     * 
     * @param data the optional data bytes to be transmitted
     * @throws HttpApiImplException in case an error occurs when writing the response body
     */
    public void writeFullBody(byte[] data) throws HttpApiImplException;
    
    /**
     * Performs all required operations needed to commit the HTTP response, e.g. flush buffers, resume suspended resources.
     * 
     * See notice at AbstractContextHandler's processResponse() method.
     * 
     * @throws HttpApiImplException in case an error occurs when committing the response
     */
    public void send() throws HttpApiImplException;
}
