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
 * This interface represents the relevant high-level behaviour a HTTP POST client offers.
 * 
 * Actual implementations shall hide any low-level details and simply supply the functionality specified by the required methods.
 */
public interface AbstractPostClient {
    
    /**
     * Placeholder to use for empty string header values instead, in order to be sure that the header field will be transmitted and not dropped.
     */
    public static final String EMPTY_STRING_PLACEHOLDER = "<![CDATA[EmptyStringPlaceholder]]>";
    
    /**
     * Initialises the actual HTTP client which will connect to the specified remoteUrl.
     * 
     * @param remoteUrl the remote URL the client connects to
     * @param useHttps whether the communication should be secured via SSL/TLS
     * @param keystoreFilename the Java KeyStore filename
     * @param keystorePassword the Java KeyStore password
     * @throws HttpApiImplException in case an error occurs when initialising or connecting the client
     */
    public void initAndConnectClient(String remoteUrl, boolean useHttps, String keystoreFilename, String keystorePassword) throws HttpApiImplException;
    
    /**
     * Sets the "referer" header field of the HTTP request.
     * 
     * @param referer the request referer
     */
    public void setRequestReferer(String referer);
    
    /**
     * Sets the header field value of the given header field name.
     * 
     * Needs to consider EMPTY_STRING_PLACEHOLDER.
     * 
     * @param headerName the header field name
     * @param headerValue the header field value
     */
    public void setRequestHeader(String headerName, String headerValue);
    
    /**
     * Writes the (possibly empty) full HTTP message body.
     * 
     * @param data the optional data bytes to be transmitted
     * @throws HttpApiImplException in case an error occurs when writing the request body
     */
    public void writeFullRequestBody(byte[] data) throws HttpApiImplException;
    
    /**
     * Performs all required operations needed to send the HTTP request, e.g. flush buffers, executing request builders.
     * 
     * @throws HttpApiImplException in case an error occurs when sending the request
     */
    public void sendRequest() throws HttpApiImplException;
    
    /**
     * Gets the HTTP status-code for the response message, e.g. "200 OK", "202 Accepted" or "204 No Content".
     * 
     * @return the 3-digit integer code describing the status
     * @throws HttpApiImplException in case an error occurs when reading the status-code
     */
    public int getStatusCode() throws HttpApiImplException;
    
    /**
     * Gets the "referer" header field of the HTTP response.
     * 
     * @return the response referer
     */
    public String getResponseReferer();
    
    /**
     * Gets the header field value of the given header field name.
     * 
     * Needs to consider EMPTY_STRING_PLACEHOLDER.
     * 
     * @param headerName the header field name
     * @return the header field value
     */
    public String getResponseHeader(String headerName);
    
    /**
     * Reads the (possibly empty) full HTTP message body.
     * 
     * @return the optional data bytes transmitted
     * @throws HttpApiImplException in case an error occurs when reading the response body
     */
    public byte[] readFullResponseBody() throws HttpApiImplException;
    
    /**
     * Disconnects the HTTP client and closes any open resources.
     * 
     * @throws HttpApiImplException in case an error occurs when shutting down the client
     */
    public void shutDown() throws HttpApiImplException;
}
