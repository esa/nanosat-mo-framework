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

import esa.mo.com.impl.util.HelperArchive;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.provider.ArchiveInheritanceSkeleton;
import org.ccsds.moims.mo.com.configuration.ConfigurationServiceInfo;
import org.ccsds.moims.mo.com.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ConfigurationService;
import org.ccsds.moims.mo.com.structures.ObjectKeysList;
import org.ccsds.moims.mo.com.DuplicateException;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.structures.*;

/**
 * An adapter that implements the ConfigurationNotificationInterface and
 * persists the configuration of the current service state.
 *
 * @author Cesar Coelho
 */
public class PersistLatestServiceConfigurationAdapter implements ConfigurationChangeListener {

    private final ArchiveInheritanceSkeleton archiveService;

    private final Long serviceConfigObjId;

    private final ExecutorService executor;

    public PersistLatestServiceConfigurationAdapter(final ReconfigurableService service, final Long serviceConfigObjId,
            final ArchiveInheritanceSkeleton archiveService, final ExecutorService executor) {
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
            // Update the service configuration with embedded config objects
            HeterogeneousList serviceConfigList = new HeterogeneousList();
            ConfigurationService serviceConfig = new ConfigurationService(
                    new ServiceId(serviceImpl.getCOMService().getAreaNumber(),
                            serviceImpl.getCOMService().getServiceNumber(),
                            serviceImpl.getCOMService().getServiceVersion()),
                    serviceImpl.getCurrentConfiguration());
            serviceConfigList.add(serviceConfig);

            try {
                archiveService.update(ConfigurationServiceInfo.CONFIGURATIONSERVICE_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, null, new URI(""), serviceConfigObjId),
                        serviceConfigList, null);
            } catch (MALException ex) {
                Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnknownException | InvalidArgumentException ex) {
                Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(
                        Level.SEVERE,
                        serviceImpl.getCOMService().getName()
                        + " service: The configuration could not be updated! objectId: "
                        + serviceConfigObjId, ex);
            }
        });
    }

    public final void storeDefaultServiceConfiguration(final Long defaultObjId, final ReconfigurableService service) {
        try {
            // Store the Service Configuration with embedded config objects
            HeterogeneousList serviceConfigList = new HeterogeneousList();
            ConfigurationService serviceConfig = new ConfigurationService(
                    new ServiceId(service.getCOMService().getAreaNumber(),
                            service.getCOMService().getServiceNumber(),
                            service.getCOMService().getServiceVersion()),
                    service.getCurrentConfiguration());
            serviceConfigList.add(serviceConfig);

            archiveService.store(
                    false,
                    ConfigurationServiceInfo.CONFIGURATIONSERVICE_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null, new URI(""), defaultObjId),
                    serviceConfigList,
                    null);
        } catch (MALException ex) {
            Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (DuplicateException | InvalidArgumentException ex) {
            Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(PersistLatestServiceConfigurationAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
