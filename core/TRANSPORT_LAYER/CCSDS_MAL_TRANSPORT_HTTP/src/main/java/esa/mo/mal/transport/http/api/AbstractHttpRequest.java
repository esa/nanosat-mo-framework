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
 * A generic HTTP request object.
 * 
 * Provides standardised access to all necessary fields and data needed by hiding the usage of specific libraries.
 */
public interface AbstractHttpRequest {
    
    /**
     * Gets the full HTTP request URL (i.e. the "host" header field plus the request-target of the request-line).
     * 
     * @return the request URL
     */
    public String getRequestUrl();
    
    /**
     * Gets the "referer" header field of the HTTP request.
     * 
     * @return the request referer
     */
    public String getReferer();
    
    /**
     * Gets the header field value of the given header field name.
     * 
     * Needs to consider AbstractPostClient.EMPTY_STRING_PLACEHOLDER.
     * 
     * @param headerName the header field name
     * @return the header field value
     */
    public String getHeader(String headerName);
    
    /**
     * Reads the (possibly empty) full HTTP message body.
     * 
     * @return the optional data bytes transmitted
     * @throws HttpApiImplException in case an error occurs when reading the request body
     */
    public byte[] readFullBody() throws HttpApiImplException;
}
