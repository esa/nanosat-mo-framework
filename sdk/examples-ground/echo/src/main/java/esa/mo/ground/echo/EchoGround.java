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
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * Demo application using the Directory service
 * This demo should be used with the echo-space.
 *
 * @author Cesar Coelho
 */
public class EchoGround {

    private static final String ECHO_SPACE_PROVIDER = "App: echo-space";
    private static final Logger LOGGER = Logger.getLogger(EchoGround.class.getName());

    public EchoGround(String directoryURI) {
        try {
            GroundMOAdapterImpl gma = null;
            ProviderSummaryList providers = GroundMOAdapterImpl.retrieveProvidersFromDirectory(new URI(directoryURI));

            if (!providers.isEmpty()) {
                for (ProviderSummary provider : providers) {
                    if (provider.getProviderId().toString().equals(ECHO_SPACE_PROVIDER)) {
                        gma = new GroundMOAdapterImpl(provider);
                        gma.addDataReceivedListener(new CompleteDataReceivedAdapter());
                        break;
                    }
                }
            } else {
                LOGGER.log(Level.SEVERE, "The returned list of providers is empty!");
            }

            if (gma != null) {
                StringBuilder sb = new StringBuilder("A");
                for (int i = 0; i < 50; i++) {
                    gma.setParameter("Data", new Blob(sb.toString().getBytes()));
                    sb.append("A");
                    Thread.sleep(5000);
                }
                gma.setParameter("Data", new Blob("Hello".getBytes()));
                gma.setParameter("Data", new Blob("OPS-SAT".getBytes()));
            } else {
                LOGGER.log(Level.SEVERE, "Failed to connect to the provider. No such provider found - " +
                    ECHO_SPACE_PROVIDER);
            }
        } catch (MALException | MalformedURLException | MALInteractionException | InterruptedException ex) {
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

        EchoGround demo = new EchoGround(args[0]);
        return;
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
            LOGGER.log(Level.INFO, "\nParameter name: {0}" + "\n" + "Parameter Value: {1}\nSource: {2}", new Object[]{
                                                                                                                      parameterInstance
                                                                                                                          .getName(),
                                                                                                                      parameterInstance
                                                                                                                          .getParameterValue()
                                                                                                                          .getRawValue(),});
        }
    }
}
