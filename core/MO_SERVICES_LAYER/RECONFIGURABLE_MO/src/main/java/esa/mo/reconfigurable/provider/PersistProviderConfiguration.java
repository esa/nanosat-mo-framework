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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetailsList;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSet;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSetList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class PersistProviderConfiguration {

    private final ArchiveInheritanceSkeleton archiveService;
//    private final ObjectId confId;
    private final ArrayList<ReconfigurableServiceImplInterface> reconfigurableServices;
//    private final ArrayList<PersistLatestServiceConfigurationAdapter> serviceAdapters = new ArrayList<PersistLatestServiceConfigurationAdapter>();
    private LongList objIds;

    public PersistProviderConfiguration(final ReconfigurableProviderImplInterface provider,
            final ObjectId confId, final ArchiveInheritanceSkeleton archiveService) {
        try {
            ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // if it was already initialized, then.. cool bro!
        }

        this.archiveService = archiveService;
//        this.confId = confId;
        this.reconfigurableServices = provider.getServices();

        final ArchivePersistenceObject comObjectProvider = HelperArchive.getArchiveCOMObject(
                archiveService,
                ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE,
                confId.getKey().getDomain(),
                confId.getKey().getInstId());

        // Does the providerConfiguration object exists?
        if (comObjectProvider != null) {
            final ArchivePersistenceObject comObjectConfs = HelperArchive.getArchiveCOMObject(
                    archiveService,
                    ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                    confId.getKey().getDomain(),
                    comObjectProvider.getArchiveDetails().getDetails().getRelated());
            objIds = ((ConfigurationObjectDetails) comObjectConfs.getObject()).getConfigObjects().get(0).getObjInstIds();
            return;
        }

        // It doesn't exist... create all the necessary objects...
        try {
            // Store the Default Service Configuration objects
            objIds = new LongList(reconfigurableServices.size());

            for (int i = 0; i < reconfigurableServices.size(); i++) {
                Long confObjId = new Long(i + 1);
                final PersistLatestServiceConfigurationAdapter persistLatestConfig
                        = new PersistLatestServiceConfigurationAdapter(reconfigurableServices.get(i), confObjId, this.archiveService);

                persistLatestConfig.storeDefaultServiceConfiguration(confObjId, reconfigurableServices.get(i));
//                serviceAdapters.add(i, persistLatestConfig);
                objIds.add(confObjId); // Will work for now
            }

            // Store the provider configuration objects
            ConfigurationObjectDetailsList archObj = new ConfigurationObjectDetailsList(1);
            ConfigurationObjectDetails providerObjects = new ConfigurationObjectDetails();
            ConfigurationObjectSetList setList = new ConfigurationObjectSetList(1);
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
            IdentifierList providerNameList = new IdentifierList(1);
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
 /*
        for (int i = 0; i < reconfigurableServices.size(); i++) {
            ReconfigurableServiceImplInterface providerService = reconfigurableServices.get(i);
            this.reloadServiceConfiguration(providerService, new Long(i + 1));

            final PersistLatestServiceConfigurationAdapter persistLatestConfig;
            
            if(serviceAdapters.isEmpty()){
                persistLatestConfig = new PersistLatestServiceConfigurationAdapter(reconfigurableServices.get(i), new Long(i + 1), this.archiveService);
                serviceAdapters.add(persistLatestConfig);
            }else{
                persistLatestConfig = serviceAdapters.get(i);
            }

            providerService.setConfigurationAdapter(persistLatestConfig);
        }
         */
        this.reloadServiceConfigurations(reconfigurableServices, objIds);

        Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.FINER,
                    "The size of the objIds list is: " + objIds.size()
                    + " and the size of the reconfigurableServices is: " + reconfigurableServices.size());

        for (int i = 0; i < objIds.size(); i++) {
            final ReconfigurableServiceImplInterface providerService = reconfigurableServices.get(i);
            final PersistLatestServiceConfigurationAdapter persistLatestConfig
                    = new PersistLatestServiceConfigurationAdapter(providerService, objIds.get(i), this.archiveService);
            providerService.setConfigurationAdapter(persistLatestConfig);
        }

    }

    /*
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
    */

    private void reloadServiceConfigurations(final ArrayList<ReconfigurableServiceImplInterface> services,
            final LongList objIds) throws IOException {
        // Retrieve the COM object of the service
        List<ArchivePersistenceObject> comObjects = HelperArchive.getArchiveCOMObjectList(archiveService,
                ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), objIds);

        if (comObjects == null) { // Could not be found, return
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE,
                    "The configuration object of one of the services does not exist in the Archive.");
            return;
        }

        final LongList relateds = new LongList();

        // Get all of the related links to retrieve from the archive
        for (ArchivePersistenceObject comObject : comObjects) {
            relateds.add(comObject.getArchiveDetails().getDetails().getRelated());
        }

        // Retrieve it from the Archive
        List<ArchivePersistenceObject> confObjs = HelperArchive.getArchiveCOMObjectList(
                archiveService, ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), relateds);

        for (int i = 0; i < confObjs.size(); i++) {
            ConfigurationObjectDetails configurationObjectDetails = (ConfigurationObjectDetails) confObjs.get(i).getObject();

            if (configurationObjectDetails == null) { // Could not be found, throw error!
                // If the object above exists, this one should also!
                throw new IOException("An error happened while reloading the service configuration: "
                        + services.get(i).getCOMService().getName());
            }

            // Reload the previous Configuration
            services.get(i).reloadConfiguration(configurationObjectDetails);
        }
    }

}
