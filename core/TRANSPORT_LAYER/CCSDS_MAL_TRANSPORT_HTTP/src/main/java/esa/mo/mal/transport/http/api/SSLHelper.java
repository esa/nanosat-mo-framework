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

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;


/**
 * Helper class to add support for the usage of HTTPS.
 * 
 * If the communication should be secured via SSL/TLS, loads the required data structures to create the appropriate context.
 * 
 * The necessary Java KeyStores can be generated with the keytool command like follows:
 * keytool -genkey -alias provider -keyalg RSA -keysize 2048 -keypass iamtheprovider -storepass iamtheprovider -ext "san=ip:127.0.0.1,dns:localhost" -dname "CN=localhost" -keystore "C:\Users\Thomas Pignede\provider.jks"
 * keytool -genkey -alias consumer -keyalg RSA -keysize 2048 -keypass iamtheconsumer -storepass iamtheconsumer -ext "san=ip:127.0.0.1,dns:localhost" -dname "CN=localhost" -keystore "C:\Users\Thomas Pignede\consumer.jks"
 * keytool -export -alias provider -storepass iamtheprovider -keystore "C:\Users\Thomas Pignede\provider.jks" -file "C:\Users\Thomas Pignede\provider.cert"
 * keytool -export -alias consumer -storepass iamtheconsumer -keystore "C:\Users\Thomas Pignede\consumer.jks" -file "C:\Users\Thomas Pignede\consumer.cert"
 * keytool -import -v -trustcacerts -alias consumer -file "C:\Users\Thomas Pignede\consumer.cert" -storepass iamtheprovider -keystore "C:\Users\Thomas Pignede\provider.jks"
 * keytool -import -v -trustcacerts -alias provider -file "C:\Users\Thomas Pignede\provider.cert" -storepass iamtheconsumer -keystore "C:\Users\Thomas Pignede\consumer.jks"
 */
public class SSLHelper {
    // all these are Oracle Java's default parameter values
    private static final String KEYSTORE_TYPE = "JKS";
    private static final String KEY_MANAGER_ALG = "SunX509";
    private static final String TRUST_MANAGER_ALG = "PKIX";
    private static final String SSL_PROTOCOL = "TLS";
    
    /**
     * Factory method for the creation of the SSLContext according to the supplied Java KeyStore.
     * 
     * @param keystoreFilename the Java KeyStore filename
     * @param keystorePassword the Java KeyStore password
     * @return the corresponding SSLContext
     * @throws HttpApiImplException in case an error occurs when creating the context
     */
    public static SSLContext createSSLContext(String keystoreFilename, String keystorePassword) throws HttpApiImplException {
        // force the use of at least TLSv1 (i.e. disable SSLv3 in earlier Java 6 versions)
        System.setProperty("https.protocols","TLSv1"); // see http://www.oracle.com/technetwork/java/javase/documentation/cve-2014-3566-2342133.html
        // for SSL/TLS debugging:
        //System.setProperty("javax.net.debug","ssl");
        try {
            char[] password = keystorePassword.toCharArray();
            
            KeyStore keyStore = KeyStore.getInstance(KEYSTORE_TYPE);
            keyStore.load(new FileInputStream(keystoreFilename), password);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KEY_MANAGER_ALG);
            kmf.init(keyStore, password);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TRUST_MANAGER_ALG);
            tmf.init(keyStore);
            SSLContext sslContext = SSLContext.getInstance(SSL_PROTOCOL);
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            return sslContext;
        } catch (KeyStoreException ex) {
            throw new HttpApiImplException("SSLHelper: KeyStoreException at createSSLContext()", ex);
        } catch (IOException ex) {
            throw new HttpApiImplException("SSLHelper: IOException at createSSLContext()", ex);
        } catch (NoSuchAlgorithmException ex) {
            throw new HttpApiImplException("SSLHelper: NoSuchAlgorithmException at createSSLContext()", ex);
        } catch (CertificateException ex) {
            throw new HttpApiImplException("SSLHelper: CertificateException at createSSLContext()", ex);
        } catch (UnrecoverableKeyException ex) {
            throw new HttpApiImplException("SSLHelper: UnrecoverableKeyException at createSSLContext()", ex);
        } catch (KeyManagementException ex) {
            throw new HttpApiImplException("SSLHelper: KeyManagementException at createSSLContext()", ex);
        }
    }
}
