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
package esa.mo.nanosatmoframework.provider;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.mc.impl.interfaces.ParameterStatusListener;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.mc.impl.util.MCServicesProvider;
import esa.mo.mc.impl.util.ReconfigurableServiceImplInterface;
import esa.mo.nanosatmoframework.adapters.MonitorAndControlAdapter;
import esa.mo.nanosatmoframework.adapters.MCStoreLastConfigurationAdapter;
import esa.mo.nanosatmoframework.interfaces.CloseAppListener;
import esa.mo.nanosatmoframework.interfaces.NanoSatMOFrameworkInterface;
import esa.mo.platform.impl.util.PlatformServicesProviderInterface;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.structures.ArgumentValueList;

/**
 * A Provider of MO services composed by COM, M&C and Platform services. Selects
 * the transport layer based on the selected values of the properties file and
 * initializes all services automatically. Provides configuration persistence,
 * therefore the last state of the configuration of the MO services will be kept
 * upon restart. Additionally, the NanoSat MO Framework implements an
 * abstraction layer over the Back-End of some MO services to facilitate the
 * monitoring of the business logic of the app using the NanoSat MO Framework.
 *
 * @author Cesar Coelho
 */
public abstract class NanoSatMOMonolithic implements NanoSatMOFrameworkInterface {

    private final static String DYNAMIC_CHANGES_PROPERTY = "esa.mo.nanosatmoframework.provider.dynamicchanges";
    private final static String PROVIDER_SUFFIX_NAME = " over NanoSat MO Monolithic";
    private final static Long DEFAULT_PROVIDER_CONFIGURATION_OBJID = (long) 1;  // The objId of the configuration to be used by the provider
    private final ConfigurationProvider configuration = new ConfigurationProvider();
    private final COMServicesProvider comServices = new COMServicesProvider();
    private final MCServicesProvider mcServices = new MCServicesProvider();
    private ParameterManager parameterManager;
    private PlatformServicesProviderInterface platformServices;
    private final DirectoryProviderServiceImpl directoryService = new DirectoryProviderServiceImpl();
    private final String providerName;

    /**
     * To initialize the NanoSat MO Framework with this method, it is necessary
     * to implement the ActionInvocationListener interface and the
     * ParameterStatusListener interface.
     *
     * @param actionAdapter The adapter to connect the actions to the
     * corresponding method of a specific entity.
     * @param parameterAdapter The adapter to connect the parameters to the
     * corresponding variable of a specific entity.
     * @param platformServices
     */
    public NanoSatMOMonolithic(ActionInvocationListener actionAdapter,
            ParameterStatusListener parameterAdapter, 
            PlatformServicesProviderInterface platformServices) {
        ConnectionProvider.resetURILinksFile(); // Resets the providerURIs.properties file
        HelperMisc.loadPropertiesFile(); // Loads: provider.properties; settings.properties; transport.properties

        this.platformServices = platformServices;
        this.providerName = System.getProperty(ConfigurationProvider.MO_APP_NAME) + PROVIDER_SUFFIX_NAME;

        try {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.FINE, "Initializing services...");
            this.servicesInit(actionAdapter, parameterAdapter);
        } catch (MALException ex) {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE,
                    "The services could not be initialized. Perhaps there's something wrong with the selected Transport Layer.", ex);
            return;
        }

        // Populate the Directory service with the entries from the URIs File
        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "Populating Directory service...");
        directoryService.autoLoadURIsFile(this.providerName);

        // Are the dynamic changes enabled?
        if ("true".equals(System.getProperty(DYNAMIC_CHANGES_PROPERTY))) {
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "Loading NanoSat MO Framework previous configurations...");
            this.loadConfigurations();
        }

        final String uri = directoryService.getConnection().getConnectionDetails().getProviderURI().toString();
        Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.INFO, "NanoSat MO Framework initialized! URI: " + uri + "\n");

    }

    /**
     * To initialize the NanoSat MO Framework with this method, it is necessary
     * to extend the MonitorAndControlAdapter adapter class. The
     * SimpleMonitorAndControlAdapter class contains a simpler interface which
     * allows sending directly parameters of the most common java types and it
     * also allows the possibility to send serializable objects.
     *
     * @param mcAdapter The adapter to connect the actions and parameters to the
     * corresponding methods and variables of a specific entity.
     * @param platformServices Platform Services
     */
    public NanoSatMOMonolithic(MonitorAndControlAdapter mcAdapter, 
            PlatformServicesProviderInterface platformServices) {
        this(mcAdapter, mcAdapter, platformServices);
    }

    @Override
    public COMServicesProvider getCOMServices() {
        return comServices;
    }

    @Override
    public MCServicesProvider getMCServices() {
        return mcServices;
    }

    @Override
    public PlatformServicesProviderInterface getPlatformServices() {
        return platformServices;
    }

    @Override
    public void reportActionExecutionProgress(final boolean success, final int errorNumber,
            final int progressStage, final int totalNumberOfProgressStages, final long actionInstId) throws IOException {
        if (this.getMCServices().getActionService() == null){  // BUG: the object exists but the init was not done!!! The comparison cannot be with null!
            throw new IOException("The Action service was not initialized!");
        }

        this.getMCServices().getActionService().reportExecutionProgress(success, new UInteger(errorNumber), progressStage, totalNumberOfProgressStages, actionInstId);
    }

    @Override
    public Long publishAlertEvent(final String alertDefinitionName, final ArgumentValueList argumentValues) throws IOException {
        if (this.getMCServices().getAlertService() == null){  // BUG: the object exists but the init was not done!!! The comparison cannot be with null!
            throw new IOException("The Alert service was not initialized!");
        }
        
        return this.getMCServices().getAlertService().publishAlertEvent(null, new Identifier(alertDefinitionName), argumentValues, null, null);
    }

    @Override
    public Boolean pushParameterValue(final String name, final Serializable content) throws IOException {
        if (this.getMCServices().getParameterService() == null){  // BUG: the object exists but the init was not done!!! The comparison cannot be with null!
            throw new IOException("The Parameter service was not initialized!");
        }

        // Convert to MAL type if possible
        Object obj = HelperAttributes.javaType2Attribute(content);

        // If it is not a MAL type, then try to convert it into a Blob container
        if (!(obj instanceof Attribute)) {
            try {
                obj = HelperAttributes.serialObject2blobAttribute((Serializable) obj);
            } catch (IOException ex) {
                Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return this.getMCServices().getParameterService().pushSingleParameterValueAttribute(new Identifier(name), (Attribute) obj, null, null);
    }

    private void reloadServiceConfiguration(ReconfigurableServiceImplInterface service, Long serviceObjId) {
        // Retrieve the COM object of the service
        ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(comServices.getArchiveService(),
                ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE, configuration.getDomain(), serviceObjId);

        if (comObject == null) { // Could not be found, return
            Logger.getLogger(NanoSatMOMonolithic.class.getName()).log(Level.SEVERE,
                    service.toString() + " service: The configuration object does not exist on the Archive.");
            return;
        }

        // Retrieve it from the Archive
        ConfigurationObjectDetails configurationObjectDetails = (ConfigurationObjectDetails) HelperArchive.getObjectBodyFromArchive(
                comServices.getArchiveService(), ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                configuration.getDomain(), comObject.getArchiveDetails().getDetails().getRelated());

        // Reload the previous Configuration
        service.reloadConfiguration(configurationObjectDetails);
    }

    private void servicesInit(ActionInvocationListener actionAdapter,
            ParameterStatusListener parameterAdapter) throws MALException {
        comServices.init();
        
        /*
            mcServices.init(
                    comServices,
                    actionAdapter,
                    parameterAdapter,
                    null
            );
         */
        parameterManager = new ParameterManager(comServices, parameterAdapter);
        mcServices.getParameterService().init(parameterManager);

        mcServices.getActionService().init(comServices, actionAdapter);
        mcServices.getAlertService().init(comServices);
        mcServices.getAggregationService().init(comServices, parameterManager);

        this.initPlatformServices();
        directoryService.init(comServices);

    }
    
    public abstract void initPlatformServices();

    private void loadConfigurations() {
            // Activate the previous configuration
            ObjectId confId = new ObjectId();  // Select the default configuration
            confId.setKey(new ObjectKey(configuration.getDomain(), DEFAULT_PROVIDER_CONFIGURATION_OBJID));
            confId.setType(ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE);

            /*---------------------------------------------------*/
            // Create the adapter that stores the configurations "onChange"
            MCStoreLastConfigurationAdapter confAdapter = new MCStoreLastConfigurationAdapter(this, confId, new Identifier(this.providerName));

            // Reload the previous Configurations
            this.reloadServiceConfiguration(mcServices.getActionService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_ACTION_SERVICE);
            this.reloadServiceConfiguration(mcServices.getParameterService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_PARAMETER_SERVICE);
            this.reloadServiceConfiguration(mcServices.getAlertService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_ALERT_SERVICE);
//            this.reloadServiceConfiguration(mcServices.getCheckService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_CHECK_SERVICE);
//            this.reloadServiceConfiguration(mcServices.getStatisticService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_STATISTIC_SERVICE);
            this.reloadServiceConfiguration(mcServices.getAggregationService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_AGGREGATION_SERVICE);

            // Send the adapter into each service to save configuration changes when they happen
            mcServices.getActionService().setConfigurationAdapter(confAdapter);
            mcServices.getParameterService().setConfigurationAdapter(confAdapter);
            mcServices.getAlertService().setConfigurationAdapter(confAdapter);
//            mcServices.getCheckService().setConfigurationAdapter(confAdapter);
//            mcServices.getStatisticService().setConfigurationAdapter(confAdapter);
            mcServices.getAggregationService().setConfigurationAdapter(confAdapter);

    }
    
    @Override
    public void addCloseAppListener(CloseAppListener closeAppAdapter) {
        // To be done...
    }

}
