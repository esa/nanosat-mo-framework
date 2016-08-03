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

import java.net.InetSocketAddress;

/**
 * This interface represents the relevant high-level behaviour a HTTP server provides.
 * 
 * Actual implementations shall offer the necessary functionality specified by the required methods
 * but be transparent to any low-level details of the specific technology.
 */
public interface AbstractHttpServer {
    
    /**
     * Initialises the actual HTTP server which will bind to the specified InetSocketAddress.
     * 
     * @param serverSocket the InetSocketAddress (IP address and port number) the server binds to
     * @param useHttps whether the communication should be secured via SSL/TLS
     * @param keystoreFilename the Java KeyStore filename
     * @param keystorePassword the Java KeyStore password
     * @throws HttpApiImplException in case an error occurs when initialising the server
     */
    public void initServer(InetSocketAddress serverSocket, boolean useHttps, String keystoreFilename, String keystorePassword) throws HttpApiImplException;
    
    /**
     * Adds the specified AbstractContextHandler to the HTTP server.
     * 
     * @param contextHandler the AbstractContextHandler to invoke for incoming requests
     */
    public void addContextHandler(AbstractContextHandler contextHandler);
    
    /**
     * Starts the HTTP server to listen for incoming connections and to delegate processing to the context handler.
     * 
     * @throws HttpApiImplException in case an error occurs when starting the server
     */
    public void startServer() throws HttpApiImplException;
    
    /**
     * Stops the HTTP server listening for connections and closes any open resources.
     * 
     * @throws HttpApiImplException in case an error occurs when stopping the server
     */
    public void stopServer() throws HttpApiImplException;
}
