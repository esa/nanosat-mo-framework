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
package esa.mo.nanosatmoframework.ground.adapter;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ServicesConnectionDetails;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.common.impl.util.CommonServicesConsumer;
import esa.mo.mc.impl.util.MCServicesConsumer;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.directory.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.platform.PlatformHelper;

/**
 * MOServicesConsumer connects to the NanoSat MO Framework and provides the
 * available services in the provider: COM, MC, Platform.
 *
 * @author Cesar Coelho
 */
public class MOServicesConsumer {

    private final ConnectionConsumer connection;

    private final COMServicesConsumer comServices;
    private final MCServicesConsumer mcServices;
    private final PlatformServicesConsumer platformServices;
    private final CommonServicesConsumer commonServices;

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     */
    public MOServicesConsumer(ConnectionConsumer connection) {
        this.connection = connection;
        this.comServices = new COMServicesConsumer();
        this.mcServices = new MCServicesConsumer();
        this.platformServices = new PlatformServicesConsumer();
        this.commonServices = new CommonServicesConsumer();

        this.initHelpers();
        this.init();
    }

    /**
     * The constructor of this class
     *
     * @param provider The Provider details. This object can be obtained from
     * the Directory service
     */
    public MOServicesConsumer(ProviderSummary provider) {
        // Grab the provider variable and put it into a ConnectionConsumer

        this.connection = new ConnectionConsumer();
        this.comServices = new COMServicesConsumer();
        this.mcServices = new MCServicesConsumer();
        this.platformServices = new PlatformServicesConsumer();
        this.commonServices = new CommonServicesConsumer();

        this.initHelpers();

        ServicesConnectionDetails serviceDetails = new ServicesConnectionDetails();
        HashMap<String, SingleConnectionDetails> services = new HashMap<String, SingleConnectionDetails>();

        // Cycle all the services in the provider and put them in the serviceDetails object
        for (ServiceCapability serviceInfo : provider.getProviderDetails().getServiceCapabilities()) {  // Add all the tabs!

            ServiceKey key = serviceInfo.getServiceKey();
            AddressDetails addressDetails;

            if (!serviceInfo.getServiceAddresses().isEmpty()) {  // If there are no address info we cannot connect...
                addressDetails = serviceInfo.getServiceAddresses().get(0);
            } else {
                continue;
            }

            SingleConnectionDetails details = new SingleConnectionDetails();
            details.setBrokerURI(addressDetails.getBrokerURI());
            details.setProviderURI(addressDetails.getServiceURI());
            details.setDomain(provider.getProviderKey().getDomain());

            MALService malService = MALContextFactory.lookupArea(key.getArea(), key.getAreaVersion()).getServiceByNumber(key.getService());
            
            if(malService == null){
                Logger.getLogger(MOServicesConsumer.class.getName()).log(Level.WARNING, "The service could not be found in the MAL factory. Maybe the Helper for that service was not initialized. The service key is: " + key.toString());
                continue;
            }
            
            services.put(malService.getName().toString(), details);

        }

        serviceDetails.setServices(services);
        this.connection.setServicesDetails(serviceDetails);
        this.init();

    }

    private void init() {

        try {
            HelperMisc.loadConsumerProperties();
        } catch (MalformedURLException ex) {
            Logger.getLogger(MOServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }

        initCOMServices();
        initMCServices();
        initPlatformServices();
        initCommonServices();

    }

    private void initHelpers() {

        // Load the MAL factories for the supported services
        try {
            MALHelper.init(MALContextFactory.getElementFactoryRegistry());

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME, PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
                CommonHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

        } catch (MALException ex) {
            Logger.getLogger(MOServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void initCOMServices() {
        comServices.init(connection);
    }

    private void initMCServices() {
        mcServices.init(connection, comServices);
    }

    private void initPlatformServices() {
        platformServices.init(connection, comServices);
    }

    private void initCommonServices() {
        commonServices.init(connection, comServices);
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

    public ConnectionConsumer getConnectionConsumer() {
        return connection;
    }

    public static final ProviderSummaryList retrieveProvidersFromDirectory(final URI directoryURI) throws MALException, MalformedURLException, MALInteractionException {

        // Starting the directory service consumer from static method. The whole Common should be registered to avoid errors during the initHelpers
        if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
            CommonHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
        }
        
        try {
            HelperMisc.loadConsumerProperties();
        } catch (MalformedURLException ex) {
            Logger.getLogger(MOServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        }

        DirectoryConsumerServiceImpl directoryService = new DirectoryConsumerServiceImpl(directoryURI);

        IdentifierList wildcardList = new IdentifierList();
        wildcardList.add(new Identifier("*"));

        ServiceFilter filter = new ServiceFilter();
        filter.setDomain(wildcardList);
        filter.setNetwork(new Identifier("*"));
        filter.setSessionType(null);
        filter.setSessionName(new Identifier("*"));
        filter.setServiceKey(new ServiceKey(new UShort((short) 0), new UShort((short) 0), new UOctet((short) 0)));
        filter.setRequiredCapabilities(new UIntegerList());
        filter.setServiceProviderName(new Identifier("*"));

        // Do the lookup
        ProviderSummaryList summaryList = directoryService.getDirectoryStub().lookupProvider(filter);
        directoryService.closeConnection();  // close the connection

        return summaryList;

    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        if(this.comServices != null){
            this.comServices.closeConnections();
        }
        
        if(this.mcServices != null){
            this.mcServices.closeConnections();
        }
        
        if(this.commonServices != null){
            this.commonServices.closeConnections();
        }
        
        if(this.platformServices != null){
            this.platformServices.closeConnections();
        }
                
    }
    
    
}
