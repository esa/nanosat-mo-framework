/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
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
import esa.mo.reconfigurable.service.PersistLatestServiceConfigurationAdapter;
import esa.mo.reconfigurable.service.ReconfigurableService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.common.configuration.ConfigurationServiceInfo;
import org.ccsds.moims.mo.common.structures.*;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 * This class is responsible for storing the configuration of a provider in the
 * COM Archive every time there is a change on the configuration of one of its
 * reconfigurable services.
 */
public class PersistProviderConfiguration {

    private final ArchiveInheritanceSkeleton archiveService;

    private final ObjectId confId;

    private final ArrayList<ReconfigurableService> reconfigurableServices;

    private final ExecutorService executor;

    private LongList objIds;

    public PersistProviderConfiguration(final ReconfigurableProvider provider,
            final ObjectId confId, final ArchiveInheritanceSkeleton archiveService) {
        this.archiveService = archiveService;
        this.confId = confId;
        this.reconfigurableServices = provider.getServices();
        this.executor
                = Executors.newSingleThreadExecutor(new ConfigurationThreadFactory()); // Guarantees sequential order

        final ArchivePersistenceObject comObjectProvider = HelperArchive.getArchiveCOMObject(
                archiveService,
                ConfigurationServiceInfo.PROVIDERCONFIGURATION_OBJECT_TYPE,
                confId.getDomain(),
                confId.getInstId());

        // Does the providerConfiguration object exists?
        if (comObjectProvider != null) {
            final ArchivePersistenceObject comObjectConfs = HelperArchive.getArchiveCOMObject(
                    archiveService,
                    ConfigurationServiceInfo.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                    confId.getDomain(),
                    comObjectProvider.getArchiveDetails().getLinks().getRelated());
            objIds = ((ConfigurationObjectDetails) comObjectConfs.getObject()).getConfigObjects().get(0).getObjInstIds();
            return;
        }

        // It doesn't exist... create all the necessary objects...
        try {
            // Store the Default Service Configuration objects
            objIds = new LongList(reconfigurableServices.size());

            for (int i = 0; i < reconfigurableServices.size(); i++) {
                Long confObjId = (long) (i + 1);
                final PersistLatestServiceConfigurationAdapter persistLatestConfig
                        = new PersistLatestServiceConfigurationAdapter(
                                reconfigurableServices.get(i), confObjId,
                                this.archiveService, this.executor);

                persistLatestConfig.storeDefaultServiceConfiguration(confObjId, reconfigurableServices.get(i));
                objIds.add(confObjId); // Will work for now
            }

            // Store the provider configuration objects
            HeterogeneousList archObj = new HeterogeneousList();
            ConfigurationObjectSetList setList = new ConfigurationObjectSetList(1);
            ConfigurationObjectSet set = new ConfigurationObjectSet(
                    ConfigurationServiceInfo.SERVICECONFIGURATION_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    objIds);

            setList.add(set);
            ConfigurationObjectDetails providerObjects = new ConfigurationObjectDetails(setList);
            archObj.add(providerObjects);

            LongList objIds3 = this.archiveService.store(
                    true,
                    ConfigurationServiceInfo.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null, ConfigurationProviderSingleton.getNetwork(), new URI("")),
                    archObj,
                    null);

            // Store the provider configuration
            // Related points to the Provider's Configuration Object
            ArchiveDetailsList details = HelperArchive.generateArchiveDetailsList(objIds3.get(0),
                    null, ConfigurationProviderSingleton.getNetwork(),
                    new URI(""), FineTime.now(), confId.getInstId());

            HeterogeneousList providerNameList = new HeterogeneousList();
            providerNameList.add(provider.getProviderName());

            this.archiveService.store(
                    false,
                    ConfigurationServiceInfo.PROVIDERCONFIGURATION_OBJECT_TYPE,
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

    public ObjectId getConfId() {
        return confId;
    }

    public void loadPreviousConfigurations() throws IOException {
        // Activate the previous configuration
        this.reloadServiceConfigurations(reconfigurableServices, objIds);

        Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.FINER,
                "The size of the objIds list is: "
                + objIds.size()
                + " and the size of the reconfigurableServices is: "
                + reconfigurableServices.size());

        for (int i = 0; i < objIds.size(); i++) {
            final ReconfigurableService providerService = reconfigurableServices.get(i);
            final PersistLatestServiceConfigurationAdapter persistLatestConfig
                    = new PersistLatestServiceConfigurationAdapter(providerService,
                            objIds.get(i), this.archiveService, this.executor);
            providerService.setOnConfigurationChangeListener(persistLatestConfig);
        }
    }

    private void reloadServiceConfigurations(final ArrayList<ReconfigurableService> services,
            final LongList objIds) throws IOException {
        // Retrieve the COM object of the service
        List<ArchivePersistenceObject> comObjects = HelperArchive.getArchiveCOMObjectList(archiveService,
                ConfigurationServiceInfo.SERVICECONFIGURATION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), objIds);

        if (comObjects == null) { // Could not be found, return
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.INFO,
                    "The service configuration object of one of the services does not exist in the Archive.");
            return;
        }

        final LongList relateds = new LongList();

        // Get all of the related links to retrieve from the archive
        for (ArchivePersistenceObject comObject : comObjects) {
            relateds.add(comObject.getArchiveDetails().getLinks().getRelated());
        }

        // Retrieve it from the Archive
        List<ArchivePersistenceObject> confObjs = HelperArchive.getArchiveCOMObjectList(
                archiveService, ConfigurationServiceInfo.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), relateds);

        for (int i = 0; i < confObjs.size(); i++) {
            ConfigurationObjectDetails configurationObjectDetails
                    = (ConfigurationObjectDetails) confObjs.get(i).getObject();

            if (configurationObjectDetails == null) { // Could not be found, throw error!
                // If the object above exists, this one should also!
                throw new IOException("An error happened while reloading the service "
                        + "configuration: " + services.get(i).getCOMService().getName());
            }

            // Reload the previous Configuration
            services.get(i).reloadConfiguration(configurationObjectDetails);
        }
    }

    /**
     * The Configuration Updates thread factory
     */
    static class ConfigurationThreadFactory implements ThreadFactory {

        private final ThreadGroup group;

        private final AtomicInteger threadNumber = new AtomicInteger(1);

        private final String namePrefix;

        ConfigurationThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            namePrefix = "ConfigurationUpdate-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

}
