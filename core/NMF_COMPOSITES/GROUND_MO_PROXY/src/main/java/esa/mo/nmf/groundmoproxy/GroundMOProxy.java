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
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.nmf.NMFException;
import esa.mo.nmf.NMFConsumer;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * The Ground MO Proxy class.
 *
 * @author Cesar Coelho
 */
public class GroundMOProxy {

    private final COMServicesProvider localCOMServices = new COMServicesProvider();
    private final DirectoryProxyServiceImpl directoryService = new DirectoryProxyServiceImpl();

    // Have a list of providers
    private final HashMap<String, NMFConsumer> myProviders = new HashMap<String, NMFConsumer>();

    public GroundMOProxy() {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties

        // Initializae the Helpers for the APIs
        NMFConsumer.initHelpers();
    }

    public void init(final URI centralDirectoryServiceURI, final URI routedURI) {
        try {
            localCOMServices.init();
            directoryService.init(localCOMServices);
        } catch (MALException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            ProviderSummaryList providers = NMFConsumer.retrieveProvidersFromDirectory(true, centralDirectoryServiceURI);
            this.addProxyPrefix(providers, routedURI.getValue());

            for (ProviderSummary provider : providers) {
                PublishDetails pub = new PublishDetails();
                pub.setDomain(provider.getProviderKey().getDomain());
                pub.setNetwork(new Identifier("not_available"));
                pub.setProviderDetails(provider.getProviderDetails());
                pub.setProviderName(provider.getProviderName());
                pub.setServiceXML(null);
                pub.setSessionType(SessionType.LIVE);
                pub.setSourceSessionName(null);

                directoryService.publishProvider(pub, null);
            }
        } catch (MALException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(GroundMOProxy.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     *
     * @param connection The connection details of the provider
     * @return
     * @throws NMFException
     */
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

    /**
     *
     *
     * @param providerDetails The Provider details. This object can be obtained
     * from the Directory service
     * @return
     * @throws NMFException
     */
    public NMFConsumer connectToProvider(ProviderSummary providerDetails) throws NMFException {

        // To be done...
        return null;
    }

    public void addProxyPrefix(final ProviderSummaryList providers, final String proxyURI) throws IllegalArgumentException {
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
        return directoryService.getConnection().getPrimaryConnectionDetails().getProviderURI();
    }

}
