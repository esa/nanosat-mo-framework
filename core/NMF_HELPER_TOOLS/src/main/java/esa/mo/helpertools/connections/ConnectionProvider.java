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
package esa.mo.helpertools.connections;

import esa.mo.helpertools.helpers.HelperMisc;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.provider.MALInteractionHandler;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALProviderManager;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class ConnectionProvider {

    private MALContextFactory malFactory;
    private MALContext mal;
    private MALProviderManager providerMgr;
    private static final String PROPERTY_SHARED_BROKER_URI = "esa.mo.helpertools.connections.SharedBrokerURI";
    private final SingleConnectionDetails connectionDetails = new SingleConnectionDetails();

    public SingleConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    /**
     * Closes any existing service providers and recreates them. Used to
     * initialize services.
     *
     * @param serviceName Name of the service
     * @param malService MAL service
     * @param handler The handler of the interaction
     * @return
     * @throws MALException On error.
     */
    public MALProvider startService(String serviceName, MALService malService,
            MALInteractionHandler handler) throws MALException {
        return startService(serviceName, malService, true, handler);
    }

    /**
     * Closes any existing service providers and recreates them. Used to
     * initialize services.
     *
     * @param serviceName Name of the service
     * @param malService MAL service
     * @param isPublisher Boolean flag to define if the service has PUB-SUB
     * @param handler The handler of the interaction
     * @return
     * @throws MALException On error.
     */
    public MALProvider startService(String serviceName, MALService malService,
            boolean isPublisher, MALInteractionHandler handler) throws MALException {
        
        try{
            malFactory = MALContextFactory.newFactory();
        } catch (MALException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.SEVERE, "Check if the MAL implementation is included in your project!! This error usually happens when the MAL layer is missing.", ex);
        }

        mal = malFactory.createMALContext(System.getProperties());
        providerMgr = mal.createProviderManager();

        URI sharedBrokerURI = null;

        if ((null != System.getProperty(PROPERTY_SHARED_BROKER_URI))) {
            sharedBrokerURI = new URI(System.getProperty(PROPERTY_SHARED_BROKER_URI));
        }

        final String moAppName = System.getProperty(ConfigurationProvider.MO_APP_NAME);
        final String uriName = (moAppName != null) ? moAppName + "-" + serviceName : serviceName;  // Create the uri string name

        MALProvider serviceProvider = providerMgr.createProvider(uriName,
                null,
                malService,
                new Blob("".getBytes()),
                handler,
                new QoSLevel[]{
                    QoSLevel.ASSURED
                },
                new UInteger(1),
                System.getProperties(),
                isPublisher,
                sharedBrokerURI);

        final ConfigurationProvider configuration = new ConfigurationProvider();

        connectionDetails.setProviderURI(serviceProvider.getURI());
        connectionDetails.setBrokerURI(serviceProvider.getBrokerURI());
        connectionDetails.setDomain(configuration.getDomain());
        IntegerList serviceKey = new IntegerList();
        serviceKey.add(malService.getArea().getNumber().getValue()); // Area
        serviceKey.add(malService.getNumber().getValue()); // Service
        serviceKey.add((int) malService.getArea().getVersion().getValue()); // Version

//        Logger.getLogger(ConnectionProvider.class.getName()).log(Level.INFO,
        Logger.getLogger(ConnectionProvider.class.getName()).log(Level.FINE,
                "\n" + serviceName + " Service URI        : {0}" +
                "\n" + serviceName + " Service broker URI : {1}" +
                "\n" + serviceName + " Service domain     : {2}" +
                "\n" + serviceName + " Service key        : {3}" ,
                new Object[]{connectionDetails.getProviderURI(),
                    connectionDetails.getBrokerURI(),
                    connectionDetails.getDomain(),
                    serviceKey});

        /*        
         Logger.getLogger(ConnectionProvider.class.getName()).log(Level.INFO, serviceName + " Service URI        : {0}", connectionDetails.getProviderURI());
         Logger.getLogger(ConnectionProvider.class.getName()).log(Level.INFO, serviceName + " Service broker URI : {0}", connectionDetails.getBrokerURI());
         Logger.getLogger(ConnectionProvider.class.getName()).log(Level.INFO, serviceName + " Service domain     : {0}", connectionDetails.getDomain());
         Logger.getLogger(ConnectionProvider.class.getName()).log(Level.INFO, serviceName + " Service key        : {0}", serviceKey);
         */

        // Write the URIs on a text file
        BufferedWriter wrt = null;
        try {
            wrt = new BufferedWriter(new FileWriter(HelperMisc.PROVIDER_URIS_PROPERTIES_FILENAME, true));
            wrt.append(serviceName + "URI=" + connectionDetails.getProviderURI());
            wrt.newLine();
            wrt.append(serviceName + "Broker=" + connectionDetails.getBrokerURI());
            wrt.newLine();
            wrt.append(serviceName + "Domain=" + HelperMisc.domain2domainId(connectionDetails.getDomain()));
            wrt.newLine();
            wrt.append(serviceName + "ServiceKey=" + serviceKey);
            wrt.newLine();
        } catch (IOException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING, "Unable to write URI information to properties file {0}", ex);
        } finally {
            if (wrt != null) {
                try {
                    wrt.close();
                } catch (IOException ex) {
                }
            }
        }

        return serviceProvider;
    }
    
    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != providerMgr) {
                providerMgr.close();
            }

            if (null != mal) {
                mal.close();
            }

        } catch (MALException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    /**
     * Clears the URI links file of the provider
     */
    public static void resetURILinksFile() {
        
        BufferedWriter wrt = null;
        try {
            wrt = new BufferedWriter(new FileWriter(HelperMisc.PROVIDER_URIS_PROPERTIES_FILENAME, false));
        } catch (IOException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING, "Unable to reset URI information from properties file {0}", ex);
        } finally {
            if (wrt != null) {
                try {
                    wrt.close();
                } catch (IOException ex) {
                }
            }
        }
        
    }

}
