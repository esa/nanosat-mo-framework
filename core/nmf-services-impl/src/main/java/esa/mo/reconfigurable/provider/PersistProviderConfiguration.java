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
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.configuration.ConfigurationServiceInfo;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
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

    private final ObjectKey confId;

    private final ArrayList<ReconfigurableService> reconfigurableServices;

    private final ExecutorService executor;

    private LongList objIds;

    /**
     * Creates a new {@code PersistProviderConfiguration}.
     *
     * @param provider the provider
     * @param confId the conf id
     * @param archiveService the archive service
     */
    public PersistProviderConfiguration(final ReconfigurableProvider provider,
            final ObjectKey confId, final ArchiveInheritanceSkeleton archiveService) {
        this.archiveService = archiveService;
        this.confId = confId;
        this.reconfigurableServices = provider.getServices();
        this.executor
                = Executors.newSingleThreadExecutor(new ConfigurationThreadFactory()); // Guarantees sequential order

        final ArchivePersistenceObject comObjectProvider = HelperArchive.getArchiveCOMObject(
                archiveService,
                ConfigurationServiceInfo.CONFIGURATIONPROVIDER_OBJECT_TYPE,
                confId.getDomain(),
                confId.getId());

        // Does the providerConfiguration object exists?
        if (comObjectProvider != null) {
            final ConfigurationProvider providerConfig = (ConfigurationProvider) comObjectProvider.getObject();
            objIds = providerConfig.getConfigObjects().get(0).getIds();
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

            // Store the provider configuration
            ObjectKeysList setList = new ObjectKeysList(1);
            ObjectKeys set = new ObjectKeys(
                    ConfigurationServiceInfo.CONFIGURATIONSERVICE_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    objIds);
            setList.add(set);

            HeterogeneousList providerConfigList = new HeterogeneousList();
            ConfigurationProvider providerConfig = new ConfigurationProvider(
                    provider.getProviderName(), setList);
            providerConfigList.add(providerConfig);

            this.archiveService.store(
                    false,
                    ConfigurationServiceInfo.CONFIGURATIONPROVIDER_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null, new URI(""), confId.getId()),
                    providerConfigList,
                    null);
        } catch (MALException ex) {
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (org.ccsds.moims.mo.com.DuplicateException | org.ccsds.moims.mo.com.InvalidArgumentException ex) {
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns the conf id.
     *
     * @return the conf id
     */
    public ObjectKey getConfId() {
        return confId;
    }

    /**
     * Loads the previously persisted provider configuration.
     *
     * @throws IOException if the operation fails
     */
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
        // Retrieve the COM objects of the services (now with embedded configuration data)
        List<ArchivePersistenceObject> comObjects = HelperArchive.getArchiveCOMObjectList(archiveService,
                ConfigurationServiceInfo.CONFIGURATIONSERVICE_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), objIds);

        if (comObjects == null) { // Could not be found, return
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.INFO,
                    "The service configuration object of one of the services does not exist in the Archive.");
            return;
        }

        for (int i = 0; i < comObjects.size(); i++) {
            final ConfigurationService serviceConfig = (ConfigurationService) comObjects.get(i).getObject();

            if (serviceConfig == null) { // Could not be found, throw error!
                throw new IOException("An error happened while reloading the service "
                        + "configuration: " + services.get(i).getCOMService().getName());
            }

            // Reload the previous configuration (now directly from the service configuration object)
            services.get(i).reloadConfiguration(serviceConfig.getConfigObjects());
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
            group = Thread.currentThread().getThreadGroup();
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
