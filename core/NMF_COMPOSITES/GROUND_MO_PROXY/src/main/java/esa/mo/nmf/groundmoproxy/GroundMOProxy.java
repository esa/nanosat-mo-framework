/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
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
package esa.mo.nmf.groundmoproxy;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.common.impl.proxy.DirectoryProxyServiceImpl;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.NMFConsumer;
import java.net.MalformedURLException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * The Ground MO Proxy class.
 *
 * @author Cesar Coelho
 */
public class GroundMOProxy {

    private final static long PERIOD = 10000; // 10 seconds
    private final AtomicBoolean nmsAliveStatus = new AtomicBoolean(false);
    protected final COMServicesProvider localCOMServices;
    private final DirectoryProxyServiceImpl localDirectoryService;
    private Timer timer;

    // Have a list of providers
//    private final HashMap<String, NMFConsumer> myProviders = new HashMap<String, NMFConsumer>();
    public GroundMOProxy() {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties

        // Initialize the Helpers for the APIs
        NMFConsumer.initHelpers();
        localCOMServices = new COMServicesProvider();
        localDirectoryService = new DirectoryProxyServiceImpl();
    }

    public void init(final URI centralDirectoryServiceURI, final URI routedURI) {
        try {
            localCOMServices.init();
            localDirectoryService.init(localCOMServices);
        } catch (MALException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Start the timer to publish the heartbeat
        timer = new Timer("Central_Directory_service_Periodic_Sync");
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!nmsAliveStatus.get()) {
                    try {
                        localDirectoryService.syncLocalDirectoryServiceWithCentral(centralDirectoryServiceURI, routedURI);
                        nmsAliveStatus.set(true);
                    } catch (MALException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MALInteractionException ex) {
                        Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }, 0, PERIOD);
    }

    public final DirectoryProxyServiceImpl getLocalDirectoryService() {
        return localDirectoryService;
    }

    /**
     *
     *
     * @param connection The connection details of the provider
     * @return
     * @throws NMFException
     */
    /*
    public NMFConsumer connectToProvider(ConnectionConsumer connection) throws NMFException {

        synchronized (myProviders) {
            final String key = "vvfjvfbjsdkvfbsksdfksdfkbvf"; // To be done
            NMFConsumer cons = myProviders.get(key);

            if (cons != null) {
                throw new NMFException("The proxy is already connected to this Provider!");
            }

            cons = new NMFConsumer(connection);
            myProviders.put(key, cons);
            return cons;
        }

    }
     */
    /**
     *
     *
     * @param providerDetails The Provider details. This object can be obtained
     * from the Directory service
     * @return
     * @throws NMFException
     */
    /*
    public NMFConsumer connectToProvider(ProviderSummary providerDetails) throws NMFException {

        // To be done...
        return null;
    }
     */
    /**
     * Adds the protocol bridge as a prefix to the serviceURI and brokerURI.
     *
     * @param providers List of providers
     * @param proxyURI The URI of the protocol bridge
     * @throws IllegalArgumentException if the providers object is null
     */
    public static void addProxyPrefix(final ProviderSummaryList providers,
            final String proxyURI) throws IllegalArgumentException {
        if (providers == null) {
            throw new IllegalArgumentException("The provider object cannot be null.");
        }

        for (ProviderSummary provider : providers) {
            final ServiceCapabilityList capabilities = provider.getProviderDetails().getServiceCapabilities();

            for (ServiceCapability capability : capabilities) {
                for (AddressDetails dets : capability.getServiceAddresses()) {
//                    dets.setServiceURI(new URI(dets.getServiceURI().getValue() + "@" + proxyURI));
                    dets.setServiceURI(new URI(proxyURI + "@" + dets.getServiceURI().getValue()));

                    if (dets.getBrokerURI() != null) {
//                        dets.setBrokerURI(new URI(dets.getBrokerURI().getValue() + "@" + proxyURI));
                        dets.setBrokerURI(new URI(proxyURI + "@" + dets.getBrokerURI().getValue()));
                    }
                }
            }
        }
    }

    public URI getDirectoryServiceURI() {
        return localDirectoryService.getConnection().getPrimaryConnectionDetails().getProviderURI();
    }

}
