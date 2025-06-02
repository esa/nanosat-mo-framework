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
package esa.mo.ground.echo;

import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nmf.groundmoadapter.GroundMOAdapterImpl;
import esa.mo.nmf.commonmoadapter.CompleteDataReceivedListener;
import esa.mo.nmf.commonmoadapter.SimpleDataReceivedListener;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Demo application using the Directory service
 *
 * @author Cesar Coelho
 */
public class DemoGroundDirectory {

    private GroundMOAdapterImpl gma;
    private static final Logger LOGGER = Logger.getLogger(DemoGroundDirectory.class.getName());

    public DemoGroundDirectory(String directoryURI) {
        try {
            ProviderSummaryList providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(new URI(directoryURI));

            if (!providers.isEmpty()) {
                // Connect to provider on index 0
                gma = new GroundMOAdapterImpl(providers.get(0));
                gma.addDataReceivedListener(new CompleteDataReceivedAdapter());
            } else {
                LOGGER.log(Level.SEVERE, "The returned list of providers is empty!");
            }
        } catch (MALException | MalformedURLException | MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Main command line entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception If there is an error
     */
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Please give supervisor directory URI as an argument!");
            System.err.println("e.g. maltcp://123.123.123.123:1024/nanosat-mo-supervisor-Directory");
            System.exit(1);
        }

        DemoGroundDirectory demo = new DemoGroundDirectory(args[0]);
    }

    private static class SimpleDataReceivedAdapter extends SimpleDataReceivedListener {

        @Override
        public void onDataReceived(String parameterName, Serializable data) {
            LOGGER.log(Level.INFO, "\nParameter name: {0}" + "\n" + "Data content:\n{1}", new Object[]{parameterName,
                                                                                                       data.toString()});
        }
    }

    private static class CompleteDataReceivedAdapter extends CompleteDataReceivedListener {

        @Override
        public void onDataReceived(ParameterInstance parameterInstance) {
            LOGGER.log(Level.INFO, "\nParameter name: {0}" + "\n" + "Parameter Value: {1}", new Object[]{
                                                                                                         parameterInstance
                                                                                                             .getName(),
                                                                                                         parameterInstance
                                                                                                             .getParameterValue()
                                                                                                             .toString()});
        }
    }
}
