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
import com.sun.net.httpserver.HttpsExchange;
import esa.mo.mal.transport.http.api.AbstractHttpRequest;
import static esa.mo.mal.transport.http.api.AbstractPostClient.EMPTY_STRING_PLACEHOLDER;
import esa.mo.mal.transport.http.api.HttpApiImplException;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * An implementation of the AbstractHttpRequest interface based on com.sun.net.httpserver.HttpExchange.
 */
public class JdkHttpRequest implements AbstractHttpRequest {

    protected final HttpExchange httpExchange;

    /**
     * Constructor.
     * 
     * @param httpExchange the HttpExchange object for initialisation of the final field
     */
    public JdkHttpRequest(HttpExchange httpExchange) {
        this.httpExchange = httpExchange;
    }

    @Override
    public String getRequestUrl() {
        StringBuilder sb = new StringBuilder();
        if (httpExchange instanceof HttpsExchange) {
            sb.append("https://");
        } else {
            sb.append("http://");
        }
        sb.append(getHeader("Host"));
        sb.append(httpExchange.getRequestURI().getPath());
        return sb.toString();
    }

    @Override
    public String getReferer() {
        return getHeader("Referer");
    }

    @Override
    public String getHeader(String headerName) {
        String headerValue = httpExchange.getRequestHeaders().getFirst(headerName);
        if (headerValue.equals(EMPTY_STRING_PLACEHOLDER)) headerValue = "";
        return headerValue;
    }

    @Override
    public byte[] readFullBody() throws HttpApiImplException {
        try {
            int packetSize = 0;
            String contentLength = httpExchange.getRequestHeaders().getFirst("Content-Length");
            if (contentLength != null){
                packetSize = Integer.parseInt(contentLength);
            }
            DataInputStream is = new DataInputStream(httpExchange.getRequestBody());
            byte[] data = new byte[packetSize];
            is.readFully(data);
            return data;
        } catch (IOException ex) {
            throw new HttpApiImplException("JdkHttpRequest: IOException at readFullBody()", ex);
        }
    }
}
