/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
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
package esa.mo.nmf;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.common.impl.util.CommonServicesConsumer;
import esa.mo.common.impl.util.HelperCommon;
import esa.mo.mc.impl.util.MCServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.sm.impl.util.SMServicesConsumer;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.platform.PlatformHelper;
import org.ccsds.moims.mo.softwaremanagement.SoftwareManagementHelper;

/**
 * NMFConsumer connects to an NMF Provider and exposes the available services in
 * the provider: COM, MC, Common, Platform and Software Management services.
 *
 * @author Cesar Coelho
 */
public class NMFConsumer {

    protected final COMServicesConsumer comServices = new COMServicesConsumer();
    protected final MCServicesConsumer mcServices = new MCServicesConsumer();
    protected final PlatformServicesConsumer platformServices = new PlatformServicesConsumer();
    protected final CommonServicesConsumer commonServices = new CommonServicesConsumer();
    protected final SMServicesConsumer smServices = new SMServicesConsumer();
    private final ConnectionConsumer connection;
    private final Blob authenticationId;
    private final String localNamePrefix;

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     */
    public NMFConsumer(final ConnectionConsumer connection) {
        this(connection, null, null);
    }

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public NMFConsumer(final ConnectionConsumer connection, final Blob authenticationId, final String localNamePrefix) {
        this.connection = connection;
        this.authenticationId = authenticationId;
        this.localNamePrefix = localNamePrefix;
        NMFConsumer.initHelpers();
    }

    /**
     * The constructor of this class
     *
     * @param provider The Provider details. This object can be obtained from
     * the Directory service
     */
    public NMFConsumer(final ProviderSummary provider) {
        this(provider, null, null);
    }

    /**
     * The constructor of this class
     *
     * @param provider The Provider details. This object can be obtained from
     * the Directory service
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public NMFConsumer(final ProviderSummary provider, final Blob authenticationId, final String localNamePrefix) {
        NMFConsumer.initHelpers(); // The Helpers need to be initialized before conversion
        // Grab the provider variable and put it into a ConnectionConsumer
        this.connection = HelperCommon.providerSummaryToConnectionConsumer(provider);
        this.authenticationId = authenticationId;
        this.localNamePrefix = localNamePrefix;
    }

    public void init() {
        try {
            HelperMisc.loadConsumerProperties();
        } catch (MalformedURLException ex) {
            Logger.getLogger(NMFConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NMFConsumer.class.getName()).log(Level.WARNING,
                    "The file " + HelperMisc.CONSUMER_PROPERTIES_FILE
                    + " could not be found! This error can happen if the user "
                    + "is trying to run the application from a different folder"
                    + " other than the one where the file is.", ex);
        }

        initCOMServices();
        initMCServices();
        initSMServices();
        initPlatformServices();
        initCommonServices();
    }

    private void initCOMServices() {
        comServices.init(connection, authenticationId, localNamePrefix);
    }

    private void initMCServices() {
        mcServices.init(connection, comServices, authenticationId, localNamePrefix);
    }

    private void initPlatformServices() {
        platformServices.init(connection, comServices, authenticationId, localNamePrefix);
    }

    private void initCommonServices() {
        commonServices.init(connection, comServices, authenticationId, localNamePrefix);
    }

    private void initSMServices() {
        smServices.init(connection, comServices, authenticationId, localNamePrefix);
    }

    /**
     * Requests the COM services.
     *
     * @return The COM services
     */
    public COMServicesConsumer getCOMServices() {
        return comServices;
    }

    /**
     * Requests the MC services.
     *
     * @return The MC services
     */
    public MCServicesConsumer getMCServices() {
        return mcServices;
    }

    /**
     * Requests the Platform services.
     *
     * @return The Platform services
     */
    public PlatformServicesConsumer getPlatformServices() {
        return platformServices;
    }

    /**
     * Requests the Common services.
     *
     * @return The Common services
     */
    public CommonServicesConsumer getCommonServices() {
        return commonServices;
    }

    /**
     * Requests the Software Management services.
     *
     * @return The Software Management services
     */
    public SMServicesConsumer getSMServices() {
        return smServices;
    }

    /**
     * Requests the Connection Consumer object.
     *
     * @return The Connection Consumer object
     */
    public ConnectionConsumer getConnectionConsumer() {
        return connection;
    }

    /**
     * Retrieves the complete list of Providers available on the Directory
     * service.
     *
     * @param directoryURI The Directory service URI
     * @return The list of providers
     * @throws org.ccsds.moims.mo.mal.MALException if there is a MAL exception.
     * @throws java.net.MalformedURLException if the URI is incorrect.
     * @throws org.ccsds.moims.mo.mal.MALInteractionException if it could not
     * reach the Directory service.
     */
    public static final ProviderSummaryList retrieveProvidersFromDirectory(final URI directoryURI)
            throws MALException, MalformedURLException, MALInteractionException {
        return NMFConsumer.retrieveProvidersFromDirectory(false, directoryURI);
    }

    /**
     * Retrieves the complete list of Providers available on the Directory
     * service.
     *
     * @param isS2G If true, then the method will only request for SPP
     * connections.
     * @param directoryURI The Directory service URI
     * @return The list of providers
     * @throws org.ccsds.moims.mo.mal.MALException if there is a MAL exception.
     * @throws java.net.MalformedURLException if the URI is incorrect.
     * @throws org.ccsds.moims.mo.mal.MALInteractionException if it could not
     * reach the Directory service.
     */
    public static final ProviderSummaryList retrieveProvidersFromDirectory(final boolean isS2G,
            final URI directoryURI) throws MALException, MalformedURLException, MALInteractionException {
        // Starting the directory service consumer from static method.
        // The whole Common area should be registered to avoid errors during the initHelpers
        if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
            CommonHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
        }

        try {
            HelperMisc.loadConsumerProperties();
        } catch (MalformedURLException ex) {
            Logger.getLogger(NMFConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NMFConsumer.class.getName()).log(Level.WARNING,
                    "The file " + HelperMisc.CONSUMER_PROPERTIES_FILE
                    + " could not be found! This error can happen if the user "
                    + "is trying to run the application from a different "
                    + "folder other than the one where the file is.", ex);
        }

        DirectoryConsumerServiceImpl directoryService = new DirectoryConsumerServiceImpl(directoryURI);

        IdentifierList wildcardList = new IdentifierList();
        wildcardList.add(new Identifier("*"));

        ServiceFilter filter = new ServiceFilter();
        filter.setDomain(wildcardList);
        filter.setNetwork(new Identifier("*"));
        filter.setSessionType(null);

        // Additional logic to save bandwidth in the Space2Ground link
        if (isS2G) {
            filter.setSessionName(new Identifier(DirectoryProviderServiceImpl.CHAR_S2G));
        } else {
            filter.setSessionName(new Identifier("*"));
        }

        filter.setServiceKey(new ServiceKey(new UShort((short) 0), new UShort((short) 0), new UOctet((short) 0)));
        filter.setRequiredCapabilities(new UIntegerList());
        filter.setServiceProviderName(new Identifier("*"));

        // Do the lookup
        final ProviderSummaryList summaryList = directoryService.getDirectoryStub().lookupProvider(filter);
        directoryService.closeConnection();  // close the connection

        return summaryList;
    }

    /**
     * Initializes the MAL Helpers for all the sets of services.
     */
    public static void initHelpers() {
        // Load the MAL factories for the supported services
        try {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
                CommonHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME,
                    SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) == null) {
                SoftwareManagementHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME, PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }
        } catch (MALException ex) {
            Logger.getLogger(NMFConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
