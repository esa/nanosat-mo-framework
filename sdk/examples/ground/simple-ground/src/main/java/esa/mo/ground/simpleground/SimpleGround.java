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
package esa.mo.ground.simpleground;

import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.commonmoadapter.SimpleDataReceivedListener;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Ground consumer: Demo Simple Ground
 * Takes in the directoryURI and app name to which to connect, and logs the received parameters data.
 */
public class SimpleGround {

    private static final String APP_PREFIX = "App: ";
    private final Logger LOGGER = Logger.getLogger(SimpleGround.class.getName());

    public SimpleGround(String directoryURI, String providerName) {
        try {
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
            }

            if (gma == null) {
                LOGGER.log(Level.SEVERE, "Failed to connect to the provider. No such provider found - " + providerName);
            }
        } catch (MalformedURLException | MALException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "Failed to connect to the provider.", ex);
        }

    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Please give supervisor directory URI as a first argument and a provider name," +
                " which to connect as the second argument!");
            System.err.println("e.g. maltcp://123.123.123.123:1024/nanosat-mo-supervisor-Directory publish-clock");
            System.exit(1);
        }
        SimpleGround demo = new SimpleGround(args[0], args[1]);
    }

    class DataReceivedAdapter extends SimpleDataReceivedListener {

        @Override
        public void onDataReceived(String parameterName, Serializable data) {
            LOGGER.log(Level.INFO, "\nParameter name: {0}" + "\n" + "Data content:\n{1}", new Object[]{parameterName,
                                                                                                       data.toString()});
        }
    }
}
