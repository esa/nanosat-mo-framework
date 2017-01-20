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
package esa.mo.nanosatmoframework.groundmoadapter;

import esa.mo.com.impl.util.COMServicesConsumer;
import esa.mo.common.impl.consumer.DirectoryConsumerServiceImpl;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.common.impl.util.CommonServicesConsumer;
import esa.mo.common.impl.util.HelperCOMMON;
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
 * MOServicesConsumer connects to the NanoSat MO Framework and provides the
 * available services in the provider: COM, MC, Platform.
 *
 * @author Cesar Coelho
 */
public class MOServicesConsumer {

    private final ConnectionConsumer connection;

    protected final COMServicesConsumer comServices;
    protected final MCServicesConsumer mcServices;
    protected final PlatformServicesConsumer platformServices;
    protected final CommonServicesConsumer commonServices;
    protected final SMServicesConsumer smServices;

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
        this.smServices = new SMServicesConsumer();

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
        this.comServices = new COMServicesConsumer();
        this.mcServices = new MCServicesConsumer();
        this.platformServices = new PlatformServicesConsumer();
        this.commonServices = new CommonServicesConsumer();
        this.smServices = new SMServicesConsumer();

        this.initHelpers(); // The Helpers need to be initialized before conversion

        // Grab the provider variable and put it into a ConnectionConsumer
        this.connection = HelperCOMMON.providerSummaryToConnectionConsumer(provider);
        
        this.init();
    }

    private void init() {
        try {
            HelperMisc.loadConsumerProperties();
        } catch (MalformedURLException ex) {
            Logger.getLogger(MOServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MOServicesConsumer.class.getName()).log(Level.WARNING, "The file " + 
                    HelperMisc.CONSUMER_PROPERTIES_FILE + 
                    " could not be found! This error can happen if the user is trying to run the application from a different folder other than the one where the file is.", ex);
        }

        initCOMServices();
        initMCServices();
        initSMServices();
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

            if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
                CommonHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_NAME, SoftwareManagementHelper.SOFTWAREMANAGEMENT_AREA_VERSION) == null) {
                SoftwareManagementHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(PlatformHelper.PLATFORM_AREA_NAME, PlatformHelper.PLATFORM_AREA_VERSION) == null) {
                PlatformHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
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

    private void initSMServices() {
        smServices.init(connection, comServices);
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

    public ConnectionConsumer getConnectionConsumer() {
        return connection;
    }

    public static final ProviderSummaryList retrieveProvidersFromDirectory(final URI directoryURI)
            throws MALException, MalformedURLException, MALInteractionException {
        // Starting the directory service consumer from static method.
        // The whole Common area should be registered to avoid errors during the initHelpers
        if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
            CommonHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
        }
        
        try {
            HelperMisc.loadConsumerProperties();
        } catch (MalformedURLException ex) {
            Logger.getLogger(MOServicesConsumer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MOServicesConsumer.class.getName()).log(Level.WARNING, "The file " + 
                    HelperMisc.CONSUMER_PROPERTIES_FILE + 
                    " could not be found! This error can happen if the user is trying to run the application from a different folder other than the one where the file is.", ex);
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
  
}
