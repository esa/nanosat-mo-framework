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

import esa.mo.mal.transport.http.api.AbstractPostClient;
import esa.mo.mal.transport.http.api.HttpApiImplException;
import esa.mo.mal.transport.http.api.SSLHelper;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * An implementation of the AbstractPostClient interface based on java.net.HttpURLConnection.
 * 
 * Logging property to see the HTTP messages being sent:
 * sun.net.www.protocol.http.HttpURLConnection.level=ALL
 */
public class JdkClient implements AbstractPostClient {

    protected HttpURLConnection connection;

    @Override
    public void initAndConnectClient(String remoteUrl, boolean useHttps, String keystoreFilename, String keystorePassword) throws HttpApiImplException {
        try {
            if (useHttps) {
                remoteUrl = remoteUrl.replaceAll("http://", "https://");
                URL url = new URL(remoteUrl);
                HttpsURLConnection httpsConnection = (HttpsURLConnection) url.openConnection();
                SSLContext sslContext = SSLHelper.createSSLContext(keystoreFilename, keystorePassword);
                httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                connection = httpsConnection;
            } else {
                remoteUrl = remoteUrl.replaceAll("https://", "http://");
                URL url = new URL(remoteUrl);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                connection = httpConnection;
            }
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
        } catch (MalformedURLException ex) {
            throw new HttpApiImplException("JdkClient: MalformedURLException at initAndConnectClient()", ex);
        } catch (IOException ex) {
            throw new HttpApiImplException("JdkClient: IOException at initAndConnectClient()", ex);
        }
    }

    @Override
    public void setRequestReferer(String referer) {
        setRequestHeader("Referer", referer);
    }

    @Override
    public void setRequestHeader(String headerName, String headerValue) {
        if (headerValue.equals("")) headerValue = EMPTY_STRING_PLACEHOLDER;
        connection.setRequestProperty(headerName, headerValue);
    }

    @Override
    public void writeFullRequestBody(byte[] data) throws HttpApiImplException {
        connection.setRequestProperty("Content-Length", Integer.toString(data.length));
        try {
            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.write(data);
            os.flush();
        } catch (IOException ex) {
            throw new HttpApiImplException("JdkClient: IOException at writeFullRequestBody()", ex);
        }
    }

    @Override
    public void sendRequest() throws HttpApiImplException {
        // do nothing
    }

    @Override
    public int getStatusCode() throws HttpApiImplException {
        try {
            return connection.getResponseCode();
        } catch (IOException ex) {
            throw new HttpApiImplException("JdkClient: IOException at getStatusCode()", ex);
        }
    }

    @Override
    public String getResponseReferer() {
        return getResponseHeader("Referer");
    }

    @Override
    public String getResponseHeader(String headerName) {
        String headerValue = connection.getHeaderField(headerName);
        if (headerValue.equals(EMPTY_STRING_PLACEHOLDER)) headerValue = "";
        return headerValue;
    }

    @Override
    public byte[] readFullResponseBody() throws HttpApiImplException {
        try {
            int packetSize = connection.getContentLength();
            if (packetSize < 0) {
                packetSize = 0;
            }
            DataInputStream is = new DataInputStream(connection.getInputStream());
            byte[] data = new byte[packetSize];
            is.readFully(data);
            return data;
        } catch (IOException ex) {
            throw new HttpApiImplException("JdkClient: IOException at readFullResponseBody()", ex);
        }
    }

    @Override
    public void shutDown() throws HttpApiImplException {
        connection.disconnect();
    }
}
