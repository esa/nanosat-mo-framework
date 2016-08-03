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
package esa.mo.mal.transport.http.api.impl.jdk;

import com.sun.net.httpserver.HttpExchange;
import esa.mo.mal.transport.http.api.AbstractHttpResponse;
import static esa.mo.mal.transport.http.api.AbstractPostClient.EMPTY_STRING_PLACEHOLDER;
import esa.mo.mal.transport.http.api.HttpApiImplException;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * An implementation of the AbstractHttpResponse interface based on com.sun.net.httpserver.HttpExchange.
 */
public class JdkHttpResponse implements AbstractHttpResponse {

    protected final HttpExchange httpExchange;

    protected int statusCode;
    protected byte[] data;

    /**
     * Constructor.
     * 
     * @param httpExchange the HttpExchange object for initialisation of the final field
     */
    public JdkHttpResponse(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    @Override
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void setReferer(String referer) {
        setHeader("Referer", referer);
    }

    @Override
    public void setHeader(String headerName, String headerValue) {
        if (headerValue.equals("")) headerValue = EMPTY_STRING_PLACEHOLDER;
        httpExchange.getResponseHeaders().set(headerName, headerValue);
    }

    @Override
    public void writeFullBody(byte[] data) throws HttpApiImplException {
        this.data = data;
    }

    @Override
    public void send() throws HttpApiImplException {
        try {
            if (data == null || data.length <= 0) {
                httpExchange.sendResponseHeaders(statusCode, -1);
            } else {
                httpExchange.sendResponseHeaders(statusCode, data.length);
                DataOutputStream os = new DataOutputStream(httpExchange.getResponseBody());
                os.write(data);
                os.flush();
            }
        } catch (IOException ex) {
            throw new HttpApiImplException("JdkHttpResponse: IOException at send()", ex);
        }
        httpExchange.close();
    }
}
