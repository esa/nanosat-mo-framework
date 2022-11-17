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
package esa.mo.reconfigurable.service;

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.common.CommonHelper;
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
 * An adapter that implements the ConfigurationNotificationInterface and persists the configuration of the current
 * service state.
 *
 * @author Cesar Coelho
 */
public class PersistLatestServiceConfigurationAdapter implements ConfigurationChangeListener {

    private final ArchiveInheritanceSkeleton archiveService;

    private final Long serviceConfigObjId;

    private final ExecutorService executor;

    private Long configObjectsObjId = null;

    public PersistLatestServiceConfigurationAdapter(final ReconfigurableService service, final Long serviceConfigObjId,
        final ArchiveInheritanceSkeleton archiveService, final ExecutorService executor) {

        if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) != null &&
            MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION)
                .getServiceByName(ConfigurationHelper.CONFIGURATION_SERVICE_NAME) == null) {
            try {
                ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE,
                    "Unexpectedly ConfigurationHelper already initialized!?", ex);
            }
        }

        if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) != null &&
            MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION)
                .getServiceByName(DirectoryHelper.DIRECTORY_SERVICE_NAME) == null) {
            try {
                DirectoryHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE,
                    "Unexpectedly DirectoryHelper already initialized!?", ex);
            }
        }

        this.archiveService = archiveService;
        this.serviceConfigObjId = serviceConfigObjId;
        this.executor = executor;
    }

    public Long getConfigurationObjectInstId() {
        return this.serviceConfigObjId;
    }

    @Override
    public void onConfigurationChanged(final ReconfigurableService serviceImpl) {
        // Submit the task to update the configuration in the COM Archive
        executor.execute(() -> {
            if (configObjectsObjId == null) {
                // Retrieve the COM object of the service
                ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(archiveService,
                    ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE, ConfigurationProviderSingleton.getDomain(),
                    serviceConfigObjId);

                if (comObject == null) {
                    Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE,
                        serviceImpl.getCOMService().getName() +
                            " service: The service configuration object could not be found! objectId: " +
                            serviceConfigObjId);

                    // Todo: Maybe we can use storeDefaultServiceConfiguration() here!? To handle better the error...
                    return;
                }

                configObjectsObjId = comObject.getArchiveDetails().getDetails().getRelated();
            }

            // Stuff to feed the update operation from the Archive...
            ArchiveDetailsList details = HelperArchive.generateArchiveDetailsList(null, null,
                ConfigurationProviderSingleton.getNetwork(), new URI(""), configObjectsObjId);
            ConfigurationObjectDetailsList confObjsList = new ConfigurationObjectDetailsList();
            confObjsList.add(serviceImpl.getCurrentConfiguration());

            try {
                archiveService.update(ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(), details, confObjsList, null);
            } catch (MALException ex) {
                Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, serviceImpl
                    .getCOMService().getName() + " service: The configuration could not be updated! objectId: " +
                    serviceConfigObjId, ex);
            }
        });
    }

    public final void storeDefaultServiceConfiguration(final Long defaultObjId, final ReconfigurableService service) {
        try {
            // Store the Service Configuration objects
            ConfigurationObjectDetailsList archObj1 = new ConfigurationObjectDetailsList();
            archObj1.add(service.getCurrentConfiguration());

            LongList objIds1 = archiveService.store(true, ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), HelperArchive.generateArchiveDetailsList(null, null,
                    ConfigurationProviderSingleton.getNetwork(), new URI("")), archObj1, null);

            // Store the Service Configuration
            ServiceKeyList serviceKeyList = new ServiceKeyList();
            serviceKeyList.add(new ServiceKey(service.getCOMService().getArea().getNumber(), service.getCOMService()
                .getNumber(), service.getCOMService().getArea().getVersion()));

            archiveService.store(false, ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), HelperArchive.generateArchiveDetailsList(objIds1.get(0),
                    null, ConfigurationProviderSingleton.getNetwork(), new URI(""), defaultObjId), serviceKeyList,
                null);
        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
