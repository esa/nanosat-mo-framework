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
package esa.mo.nanosatmoframework;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.common.impl.provider.DirectoryProviderServiceImpl;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.mc.impl.util.MCServicesProvider;
import esa.mo.platform.impl.util.PlatformServicesConsumer;
import esa.mo.reconfigurable.provider.ReconfigurableProviderImplInterface;
import esa.mo.reconfigurable.service.ConfigurationNotificationInterface;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import esa.mo.sm.impl.provider.HeartbeatProviderServiceImpl;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
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
public abstract class NanoSatMOFrameworkProvider implements ReconfigurableProviderImplInterface, NanoSatMOFrameworkInterface {

    private final static String MC_SERVICES_NOT_INITIALIZED = "The M&C services were not initialized!";
    public final static String DYNAMIC_CHANGES_PROPERTY = "esa.mo.nanosatmoframework.provider.dynamicchanges";
    public final static String FILENAME_CENTRAL_DIRECTORY_SERVICE = "centralDirectoryService.uri";
    public final static String NANOSAT_MO_SUPERVISOR_NAME = "NanoSat_MO_Supervisor";
    public final static Long DEFAULT_PROVIDER_CONFIGURATION_OBJID = (long) 1;  // The objId of the configuration to be used by the provider
    public final ConfigurationProvider configuration = new ConfigurationProvider();
    public final COMServicesProvider comServices = new COMServicesProvider();
    public final HeartbeatProviderServiceImpl heartbeatService = new HeartbeatProviderServiceImpl();
    public final DirectoryProviderServiceImpl directoryService = new DirectoryProviderServiceImpl();
    public MCServicesProvider mcServices;
    public ParameterManager parameterManager;
    public PlatformServicesConsumer platformServices;
    public CloseAppListener closeAppAdapter = null;
    public ConfigurationNotificationInterface providerConfigurationAdapter = null;
    public String providerName;

    @Override
    public COMServicesProvider getCOMServices() {
        return comServices;
    }

    @Override
    public MCServicesProvider getMCServices() {
        return mcServices;
    }

    @Override
    public PlatformServicesConsumer getPlatformServices() {
        return platformServices;
    }

    @Override
    public void reportActionExecutionProgress(final boolean success, final int errorNumber,
            final int progressStage, final int totalNumberOfProgressStages, final long actionInstId) throws IOException {
        if (this.getMCServices() == null) {
            throw new IOException(MC_SERVICES_NOT_INITIALIZED);
        }

        this.getMCServices().getActionService().reportExecutionProgress(success, new UInteger(errorNumber), progressStage, totalNumberOfProgressStages, actionInstId);
    }

    @Override
    public Long publishAlertEvent(final String alertDefinitionName, final ArgumentValueList argumentValues) throws IOException {
        if (this.getMCServices() == null) {
            throw new IOException(MC_SERVICES_NOT_INITIALIZED);
        }

        return this.getMCServices().getAlertService().publishAlertEvent(null, new Identifier(alertDefinitionName), argumentValues, null, null);
    }

    @Override
    public Boolean pushParameterValue(final String name, final Serializable content) throws IOException {
        return this.pushParameterValue(name, content, true);
    }

    @Override
    public Boolean pushParameterValue(final String name, final Serializable content, final boolean storeIt) throws IOException {
        if (this.getMCServices() == null) {
            throw new IOException(MC_SERVICES_NOT_INITIALIZED);
        }

        Object obj = HelperAttributes.javaType2Attribute(content); // Convert to MAL type if possible

        // If it is not a MAL type, then try to convert it into a Blob container
        if (!(obj instanceof Attribute)) {
            try {
                obj = HelperAttributes.serialObject2blobAttribute((Serializable) obj);
            } catch (IOException ex) {
                Logger.getLogger(NanoSatMOFrameworkProvider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        final ParameterValue parameterValue = new ParameterValue();
        parameterValue.setRawValue((Attribute) obj);
        
        // To do: Add the Conversion service here and put the value in the convertedValue field
        parameterValue.setConvertedValue(null);
        parameterValue.setValid(true);
        parameterValue.setInvalidSubState(new UOctet((short) 0));
        
        ParameterInstance instance = new ParameterInstance(new Identifier(name), parameterValue, null, null);
        ArrayList<ParameterInstance> parameters = new ArrayList<ParameterInstance>();
        parameters.add(instance);
        
        return this.getMCServices().getParameterService().pushMultipleParameterValues(parameters, storeIt);
    }
    
    private void reloadServiceConfiguration(ReconfigurableServiceImplInterface service, Long serviceObjId) throws IOException {
        // Retrieve the COM object of the service
        ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(comServices.getArchiveService(),
                ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE, configuration.getDomain(), serviceObjId);

        if (comObject == null) { // Could not be found, return
            Logger.getLogger(NanoSatMOFrameworkProvider.class.getName()).log(Level.SEVERE,
                    service.getCOMService().getName() + " service: The configuration object does not exist in the Archive.");
            return;
        }

        // Retrieve it from the Archive
        ConfigurationObjectDetails configurationObjectDetails = (ConfigurationObjectDetails) HelperArchive.getObjectBodyFromArchive(
                comServices.getArchiveService(), ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                configuration.getDomain(), comObject.getArchiveDetails().getDetails().getRelated());

        if (configurationObjectDetails == null) { // Could not be found, throw error!
            // If the object above exists, this one should also!
            throw new IOException("An error happened while reloading the service configuration: " + service.getCOMService().getName());
        }
        
        // Reload the previous Configuration
        service.reloadConfiguration(configurationObjectDetails);
    }

    public abstract void initPlatformServices(COMServicesProvider comServices);

    public final void loadConfigurations() throws IOException {
        // Activate the previous configuration
        ObjectId confId = new ObjectId();  // Select the default configuration
        confId.setType(ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE);
        confId.setKey(new ObjectKey(configuration.getDomain(), DEFAULT_PROVIDER_CONFIGURATION_OBJID));

        /*---------------------------------------------------*/
        // Create the adapter that stores the configurations "onChange"
        MCStoreLastConfigurationAdapter confAdapter = new MCStoreLastConfigurationAdapter(this, confId, new Identifier(this.providerName));
        

        // Reload the previous Configurations
        this.reloadServiceConfiguration(mcServices.getActionService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_ACTION_SERVICE);
        this.reloadServiceConfiguration(mcServices.getParameterService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_PARAMETER_SERVICE);
        this.reloadServiceConfiguration(mcServices.getAlertService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_ALERT_SERVICE);
        this.reloadServiceConfiguration(mcServices.getAggregationService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_AGGREGATION_SERVICE);

        // Send the adapter into each service to save configuration changes when they happen
        mcServices.getActionService().setConfigurationAdapter(confAdapter);
        mcServices.getParameterService().setConfigurationAdapter(confAdapter);
        mcServices.getAlertService().setConfigurationAdapter(confAdapter);
        mcServices.getAggregationService().setConfigurationAdapter(confAdapter);
    }

    public final void startMCServices(MonitorAndControlNMFAdapter mcAdapter) throws MALException {
        if (mcAdapter != null) {
            mcServices = new MCServicesProvider();
            parameterManager = new ParameterManager(comServices, mcAdapter);

            mcServices.getParameterService().init(parameterManager);
            mcServices.getActionService().init(comServices, mcAdapter);
            mcServices.getAlertService().init(comServices);
            mcServices.getAggregationService().init(comServices, parameterManager);
        }
    }
    
    @Override
    public void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter) {
        this.providerConfigurationAdapter = configurationAdapter;
    }

    @Override
    public ArrayList<ReconfigurableServiceImplInterface> getServices() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        // To be done
//        you need a list of services here...
    }

    @Override
    public Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        // To be done
//        you also need to plug the current configuration here...
//        for our case, we have only a single configuration that never changes...
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        // To be done
//        you need to implement the getter for the current configuration
//        Also, you will also retrieve the same because we have a single one
    }

    @Override
    public Identifier getProviderName() {
        return new Identifier(this.providerName);
    }

    @Override
    public void addCloseAppListener(CloseAppListener closeAppAdapter) {
        this.closeAppAdapter = closeAppAdapter;
    }

    public CloseAppListener getCloseAppListener() {
        return this.closeAppAdapter;
    }

}
