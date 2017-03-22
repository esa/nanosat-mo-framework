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
package esa.mo.reconfigurable.provider;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.reconfigurable.service.PersistLatestServiceConfigurationAdapter;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetailsList;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSet;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSetList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class PersistProviderConfiguration {

    private final ArchiveInheritanceSkeleton archiveService;
    private final ObjectId confId;
    private final ArrayList<ReconfigurableServiceImplInterface> reconfigurableServices;

    public PersistProviderConfiguration(final ReconfigurableProviderImplInterface provider,
            final ObjectId confId, final ArchiveInheritanceSkeleton archiveService) {
        try {
            ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // if it was already initialized, then.. cool bro!
        }

        this.archiveService = archiveService;
        this.confId = confId;
        this.reconfigurableServices = provider.getServices();
        
//    }

//    public void init() {
        final ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(
                archiveService,
                ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE,
                confId.getKey().getDomain(),
                confId.getKey().getInstId());

        // Does the providerConfiguration object exists?
        if (comObject != null) {
            return;
        }

        // It doesn't exist... create all the necessary objects...
        try {
            // Store the Default Service Configuration objects
            final LongList objIds = new LongList();

            for (int i = 0; i < reconfigurableServices.size(); i++) {
                ReconfigurableServiceImplInterface providerService = reconfigurableServices.get(i);
                final PersistLatestServiceConfigurationAdapter latest
                        = new PersistLatestServiceConfigurationAdapter(providerService, new Long(i + 1), this.archiveService);

                providerService.setConfigurationAdapter(latest);
                objIds.add(latest.getConfigurationObjectInstId());
            }

            // Store the provider configuration objects
            ConfigurationObjectDetailsList archObj = new ConfigurationObjectDetailsList();
            ConfigurationObjectDetails providerObjects = new ConfigurationObjectDetails();
            ConfigurationObjectSetList setList = new ConfigurationObjectSetList();
            ConfigurationObjectSet set = new ConfigurationObjectSet();
            set.setDomain(ConfigurationProviderSingleton.getDomain());
            set.setObjType(ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE);
            set.setObjInstIds(objIds);

            setList.add(set);
            providerObjects.setConfigObjects(setList);
            archObj.add(providerObjects);

            LongList objIds3 = this.archiveService.store(
                    true,
                    ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null, ConfigurationProviderSingleton.getNetwork(), new URI("")),
                    archObj,
                    null);

            // Store the provider configuration
            // Related points to the Provider's Configuration Object
            ArchiveDetailsList details = HelperArchive.generateArchiveDetailsList(objIds3.get(0),
                    null, ConfigurationProviderSingleton.getNetwork(), new URI(""));
            details.get(0).setInstId(confId.getKey().getInstId());
            IdentifierList providerNameList = new IdentifierList();
            providerNameList.add(provider.getProviderName());

            this.archiveService.store(
                    false,
                    ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    details,
                    providerNameList,
                    null);

        } catch (MALException ex) {
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadPreviousConfigurations() throws IOException {

        // Activate the previous configuration

        /*---------------------------------------------------*/
        // Create the adapter that stores the configurations "onChange"
//        final MCStoreLastConfigurationAdapter confAdapter = new MCStoreLastConfigurationAdapter(this, confId, new Identifier(this.providerName));

            for (int i = 0; i < reconfigurableServices.size(); i++) {
                ReconfigurableServiceImplInterface providerService = reconfigurableServices.get(i);
                this.reloadServiceConfiguration(providerService, new Long(i + 1));
                /*
                final PersistLatestServiceConfigurationAdapter latest
                        = new PersistLatestServiceConfigurationAdapter(providerService, new Long(i + 1), this.archiveService);

                providerService.setConfigurationAdapter(latest);
                objIds.add(latest.getConfigurationObjectInstId());
                */
            }

        // Reload the previous Configurations
        /*
        this.reloadServiceConfiguration(mcServices.getActionService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_ACTION_SERVICE);
        this.reloadServiceConfiguration(mcServices.getParameterService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_PARAMETER_SERVICE);
        this.reloadServiceConfiguration(mcServices.getAlertService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_ALERT_SERVICE);
        this.reloadServiceConfiguration(mcServices.getAggregationService(), MCStoreLastConfigurationAdapter.DEFAULT_OBJID_AGGREGATION_SERVICE);

        // Send the adapter into each service to save configuration changes when they happen
        mcServices.getActionService().setConfigurationAdapter(confAdapter);
        mcServices.getParameterService().setConfigurationAdapter(confAdapter);
        mcServices.getAlertService().setConfigurationAdapter(confAdapter);
        mcServices.getAggregationService().setConfigurationAdapter(confAdapter);
        */
    }

    private void reloadServiceConfiguration(final ReconfigurableServiceImplInterface service,
            final Long serviceObjId) throws IOException {
        // Retrieve the COM object of the service
        ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(archiveService,
                ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), serviceObjId);

        if (comObject == null) { // Could not be found, return
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE,
                    service.getCOMService().getName() + " service: "
                    + "The configuration object does not exist in the Archive.");
            return;
        }

        // Retrieve it from the Archive
        ConfigurationObjectDetails configurationObjectDetails = (ConfigurationObjectDetails) HelperArchive.getObjectBodyFromArchive(
                archiveService, ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), comObject.getArchiveDetails().getDetails().getRelated());

        if (configurationObjectDetails == null) { // Could not be found, throw error!
            // If the object above exists, this one should also!
            throw new IOException("An error happened while reloading the service configuration: "
                    + service.getCOMService().getName());
        }

        // Reload the previous Configuration
        service.reloadConfiguration(configurationObjectDetails);
    }

}
