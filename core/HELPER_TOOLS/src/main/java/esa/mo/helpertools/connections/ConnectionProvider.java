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

import esa.mo.helpertools.helpers.HelperConnections;
import esa.mo.helpertools.helpers.HelperMisc;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;
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
    private MALProvider primaryMALServiceProvider = null;
    private MALProvider secondaryMALServiceProvider = null;
    private final SingleConnectionDetails primaryConnectionDetails = new SingleConnectionDetails();
    private SingleConnectionDetails secondaryConnectionDetails = null;

    @Deprecated
    public SingleConnectionDetails getConnectionDetails() {
        return primaryConnectionDetails;
    }

    public SingleConnectionDetails getPrimaryConnectionDetails() {
        return primaryConnectionDetails;
    }

    public SingleConnectionDetails getSecondaryConnectionDetails() {
        return secondaryConnectionDetails;
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
        try {
            malFactory = MALContextFactory.newFactory();
        } catch (MALException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.SEVERE,
                    "Check if the MAL implementation is included in your project!! "
                    + "This error usually happens when the MAL layer is missing.", ex);
        }

        mal = malFactory.createMALContext(System.getProperties());
        providerMgr = mal.createProviderManager();

        URI sharedBrokerURI = null;

        if ((null != System.getProperty(HelperMisc.PROPERTY_SHARED_BROKER_URI))) {
            sharedBrokerURI = new URI(System.getProperty(HelperMisc.PROPERTY_SHARED_BROKER_URI));
        }

        final String moAppName = System.getProperty(HelperMisc.MO_APP_NAME);
        final String uriName = (moAppName != null) ? moAppName + "-" + serviceName : serviceName;  // Create the uri string name

        Properties props = new Properties();
        props.putAll(System.getProperties());

        MALProvider serviceProvider = providerMgr.createProvider(uriName,
                null,
                malService,
                new Blob("".getBytes()),
                handler,
                new QoSLevel[]{
                    QoSLevel.ASSURED
                },
                new UInteger(1),
                props,
                isPublisher,
                sharedBrokerURI);

        IntegerList serviceKey = new IntegerList();
        serviceKey.add(malService.getArea().getNumber().getValue()); // Area
        serviceKey.add(malService.getNumber().getValue()); // Service
        serviceKey.add((int) malService.getArea().getVersion().getValue()); // Version

        primaryConnectionDetails.setProviderURI(serviceProvider.getURI());
        primaryConnectionDetails.setBrokerURI(serviceProvider.getBrokerURI());
        primaryConnectionDetails.setDomain(ConfigurationProviderSingleton.getDomain());
        primaryConnectionDetails.setServiceKey(serviceKey);

        Logger.getLogger(ConnectionProvider.class.getName()).log(Level.FINE,
                "\n" + serviceName + " Service URI        : {0}"
                + "\n" + serviceName + " Service broker URI : {1}"
                + "\n" + serviceName + " Service domain     : {2}"
                + "\n" + serviceName + " Service key        : {3}",
                new Object[]{
                    primaryConnectionDetails.getProviderURI(),
                    primaryConnectionDetails.getBrokerURI(),
                    primaryConnectionDetails.getDomain(),
                    serviceKey
                });

        this.writeURIsOnFile(primaryConnectionDetails,
                serviceName,
                HelperMisc.PROVIDER_URIS_PROPERTIES_FILENAME);

        primaryMALServiceProvider = serviceProvider;

        final String secondaryProtocol = System.getProperty(HelperMisc.SECONDARY_PROTOCOL);

        // Check if the secondary Transport is enabled
        if (secondaryProtocol != null) {
            secondaryConnectionDetails = new SingleConnectionDetails();

            MALProvider serviceProvider2 = providerMgr.createProvider(uriName,
                    secondaryProtocol,
                    malService,
                    new Blob("".getBytes()),
                    handler,
                    new QoSLevel[]{
                        QoSLevel.ASSURED
                    },
                    new UInteger(1),
                    props,
                    isPublisher,
                    sharedBrokerURI);

            secondaryConnectionDetails.setProviderURI(serviceProvider2.getURI());
            secondaryConnectionDetails.setBrokerURI(serviceProvider2.getBrokerURI());
            secondaryConnectionDetails.setDomain(ConfigurationProviderSingleton.getDomain());
            secondaryConnectionDetails.setServiceKey(serviceKey);

            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.FINE,
                    "\n" + serviceName + " Service URI        : {0}"
                    + "\n" + serviceName + " Service broker URI : {1}"
                    + "\n" + serviceName + " Service domain     : {2}"
                    + "\n" + serviceName + " Service key        : {3}",
                    new Object[]{
                        secondaryConnectionDetails.getProviderURI(),
                        secondaryConnectionDetails.getBrokerURI(),
                        secondaryConnectionDetails.getDomain(),
                        serviceKey
                    });

            this.writeURIsOnFile(secondaryConnectionDetails,
                    serviceName,
                    HelperMisc.PROVIDER_URIS_SECONDARY_PROPERTIES_FILENAME);

            secondaryMALServiceProvider = serviceProvider2;
        }

        return serviceProvider;
    }

    /**
     * Closes all running threads and releases the MAL resources. The method has
     * been deprecated and closeAll should be used instead.
     */
    @Deprecated
    public void close() {
        try {
            if (null != providerMgr) {
                providerMgr.close();
            }

            if (null != mal) {
                mal.close();
            }
        } catch (MALException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void closeAll() {
        try {
            if (null != primaryMALServiceProvider) {
                primaryMALServiceProvider.getClass();
                primaryMALServiceProvider.close();
            }

            if (null != secondaryMALServiceProvider) {
                secondaryMALServiceProvider.close();
            }
        } catch (MALException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }

        try {
            if (null != providerMgr) {
                providerMgr.close();
            }

            if (null != mal) {
                mal.close();
            }
        } catch (MALException ex) {
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
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
            Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING,
                    "Unable to reset URI information from properties file {0}", ex);
        } finally {
            if (wrt != null) {
                try {
                    wrt.close();
                } catch (IOException ex) {
                }
            }
        }

        if (System.getProperty(HelperMisc.SECONDARY_PROTOCOL) != null) {
            BufferedWriter wrt2 = null;
            try {
                wrt2 = new BufferedWriter(new FileWriter(HelperMisc.PROVIDER_URIS_SECONDARY_PROPERTIES_FILENAME, false));
            } catch (IOException ex) {
                Logger.getLogger(ConnectionProvider.class.getName()).log(Level.WARNING,
                        "Unable to reset URI information from properties file {0}", ex);
            } finally {
                if (wrt2 != null) {
                    try {
                        wrt2.close();
                    } catch (IOException ex) {
                    }
                }
            }
        }

    }

    /**
     * Writes the URIs on a text file
     */
    private void writeURIsOnFile(SingleConnectionDetails connectionDetails, String serviceName, String filename) {
        BufferedWriter wrt = null;
        try {
            wrt = new BufferedWriter(new FileWriter(filename, true));
            wrt.append(serviceName + HelperConnections.SUFFIX_URI + "=" + connectionDetails.getProviderURI());
            wrt.newLine();
            wrt.append(serviceName + HelperConnections.SUFFIX_BROKER + "=" + connectionDetails.getBrokerURI());
            wrt.newLine();
            wrt.append(serviceName + HelperConnections.SUFFIX_DOMAIN + "=" + HelperMisc.domain2domainId(connectionDetails.getDomain()));
            wrt.newLine();
            wrt.append(serviceName + HelperConnections.SUFFIX_SERVICE_KEY + "=" + connectionDetails.getServiceKey());
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
    }

}
