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
package esa.mo.reconfigurable.service;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetailsList;
import org.ccsds.moims.mo.common.directory.DirectoryHelper;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.common.structures.ServiceKeyList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class PersistLatestServiceConfigurationAdapter implements ConfigurationNotificationInterface {

    private final ArchiveInheritanceSkeleton archiveService;
    private final Long configObjId;
    private final ExecutorService executor;

    public PersistLatestServiceConfigurationAdapter(final ReconfigurableServiceImplInterface service,
            final Long configObjId, final ArchiveInheritanceSkeleton archiveService) {
        try {
            ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // if it was already initialized, then.. cool bro!
        }

        try {
            DirectoryHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // if it was already initialized, then.. cool bro!
        }

        this.archiveService = archiveService;
        this.configObjId = configObjId;
        this.executor = Executors.newSingleThreadExecutor(new NamedConfigurationThreadFactory(service.getCOMService().getName().toString())); // Guarantees sequential order

        // Store a service Configuration object in the Archive
        this.storeDefaultServiceConfiguration(configObjId, service);
    }

    public Long getConfigurationObjectInstId() {
        return this.configObjId;
    }

    @Override
    public void configurationChanged(final ReconfigurableServiceImplInterface serviceImpl) {
        // Submit the task to update the configuration in the COM Archive
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // Retrieve the COM object of the service
                ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(archiveService,
                        ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE, ConfigurationProviderSingleton.getDomain(), configObjId);

                if (comObject == null) {
                    Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE,
                            serviceImpl.getCOMService().getName()
                            + " service: The service configuration object could not be found! objectId: "
                            + configObjId);

                    // Maybe we can use storeDefaultServiceConfiguration() here!? To handle better the error...
                }

                // Stuff to feed the update operation from the Archive...
                ArchiveDetailsList details = HelperArchive.generateArchiveDetailsList(null, null,
                        ConfigurationProviderSingleton.getNetwork(), new URI(""),
                        comObject.getArchiveDetails().getDetails().getRelated());
                ConfigurationObjectDetailsList confObjsList = new ConfigurationObjectDetailsList();
                confObjsList.add(serviceImpl.getCurrentConfiguration());

                try {
                    archiveService.update(
                            ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                            ConfigurationProviderSingleton.getDomain(),
                            details,
                            confObjsList,
                            null);
                } catch (MALException ex) {
                    Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MALInteractionException ex) {
                    Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE,
                            serviceImpl.getCOMService().getName()
                            + " service: The configuration could not be updated! objectId: " + configObjId, ex);
                }
            }
        });
    }

    public final void storeDefaultServiceConfiguration(final Long defaultObjId, final ReconfigurableServiceImplInterface service) {
        try {
            // Store the Service Configuration objects
            ConfigurationObjectDetailsList archObj1 = new ConfigurationObjectDetailsList();
            archObj1.add(service.getCurrentConfiguration());

            LongList objIds1 = archiveService.store(
                    true,
                    ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null,
                            ConfigurationProviderSingleton.getNetwork(), new URI("")),
                    archObj1,
                    null);

            // Store the Service Configuration
            ServiceKeyList serviceKeyList = new ServiceKeyList();
            serviceKeyList.add(new ServiceKey(service.getCOMService().getArea().getNumber(),
                    service.getCOMService().getNumber(), service.getCOMService().getArea().getVersion()));

            archiveService.store(
                    false,
                    ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(objIds1.get(0), null,
                            ConfigurationProviderSingleton.getNetwork(), new URI(""), defaultObjId),
                    serviceKeyList,
                    null);
        } catch (MALException ex) {
            Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*
        try {
            // Store the Service Configuration objects
            ConfigurationObjectDetailsList archObj1 = new ConfigurationObjectDetailsList();
            archObj1.add(service.getCurrentConfiguration());

            LongList objIds1 = archiveService.store(
                    true,
                    ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null, ConfigurationProviderSingleton.getNetwork(), new URI("")),
                    archObj1,
                    null);

            // Store the Service Configuration
            ServiceKeyList serviceKeyList = new ServiceKeyList();
            serviceKeyList.add(new ServiceKey(service.getCOMService().getArea().getNumber(), 
                    service.getCOMService().getNumber(), service.getCOMService().getArea().getVersion()));

            LongList objIds2 = archiveService.store(
                    true,
                    ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(objIds1.get(0), null, ConfigurationProviderSingleton.getNetwork(), new URI(""), defaultObjId),
                    serviceKeyList,
                    null);

            return objIds2.get(0);

        } catch (MALException ex) {
            Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
         */
    }

    /**
     * The database backend thread factory
     */
    static class NamedConfigurationThreadFactory implements ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedConfigurationThreadFactory(String configurationName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup()
                    : Thread.currentThread().getThreadGroup();
            namePrefix = "ConfigurationUpdate" + "-" + configurationName + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
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
