/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
package esa.mo.nmf.ground.facebook;

import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.FacebookType;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.commonmoadapter.SimpleDataReceivedListener;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Demo application pushing data into Facebook
 *
 */
public class Push2Facebook {

    private static final Logger LOGGER = Logger.getLogger(Push2Facebook.class.getName());

    private static final String APP_PREFIX = "App: ";
    private static final String TOKEN_FILENAME = "token.properties";
    private final String ACCESS_TOKEN;

    public Push2Facebook(String directoryURI, String providerName) {

        try {
            registerDataListener(directoryURI, providerName);

            final java.util.Properties sysProps = System.getProperties();

            // Load the properties out of the file
            File file = new File(System.getProperty(TOKEN_FILENAME, TOKEN_FILENAME));
            if (file.exists()) {
                sysProps.putAll(HelperMisc.loadProperties(file.toURI().toURL(), TOKEN_FILENAME));
            }
        } catch (MalformedURLException | MALInteractionException | MALException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }

        ACCESS_TOKEN = System.getProperty("access_token", "null");
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Please give supervisor directory URI as a first argument and a provider name," +
                " which to connect as the second argument!");
            System.err.println("e.g. maltcp://123.123.123.123:1024/nanosat-mo-supervisor-Directory publish-clock");
            System.exit(1);
        }
        Push2Facebook demo = new Push2Facebook(args[0], args[1]);
    }

    /**
     * Registers the data listener to the given provider.
     *
     * @param directoryURI - directory URI
     * @param providerName - provider name which to connect to
     * @throws MalformedURLException
     * @throws MALInteractionException
     * @throws MALException
     */
    private void registerDataListener(String directoryURI, String providerName) throws MalformedURLException,
        MALInteractionException, MALException {

        ProviderSummaryList providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(new URI(directoryURI));

        GroundMOAdapterImpl gma = null;
        if (!providers.isEmpty()) {
            for (ProviderSummary provider : providers) {
                if (provider.getProviderId().toString().equals(APP_PREFIX + providerName)) {
                    gma = new GroundMOAdapterImpl(provider);
                    gma.addDataReceivedListener(new DataReceivedAdapter());
                    break;
                }
            }

            if (gma == null) {
                throw new RuntimeException("Failed to connect to the provider. No such provider found - " +
                    providerName);
            }
        }
    }

    public class DataReceivedAdapter extends SimpleDataReceivedListener {

        @Override
        public void onDataReceived(String parameterName, Serializable data) {

            LOGGER.log(Level.INFO, "\nPosting on facebook...\nParameter name: {0}" + "\nData content:\n{1}",
                new Object[]{parameterName, data.toString()});

            // Get the Token here: https://developers.facebook.com/tools/explorer/
            FacebookClient facebookClient = new DefaultFacebookClient(ACCESS_TOKEN, Version.VERSION_2_4);

            if (facebookClient == null) {
                LOGGER.log(Level.INFO, "The facebookClient is null! The access token might be incorrect...\n");
            } else {
                LOGGER.log(Level.INFO, "The facebookClient is connected!\n");
            }

            FacebookType publishMessageResponse = facebookClient.publish("me/feed", FacebookType.class, Parameter.with(
                "message", data.toString()));

            String str = "";

            if (publishMessageResponse.getId() != null) {
                str += publishMessageResponse.getId() + "\n";
            }

            if (publishMessageResponse.getMetadata() != null) {
                str += publishMessageResponse.getMetadata().toString() + "\n";
            }

            if (publishMessageResponse.getType() != null) {
                str += publishMessageResponse.getType() + "\n";
            }

            LOGGER.log(Level.INFO, str);
        }

    }

}
