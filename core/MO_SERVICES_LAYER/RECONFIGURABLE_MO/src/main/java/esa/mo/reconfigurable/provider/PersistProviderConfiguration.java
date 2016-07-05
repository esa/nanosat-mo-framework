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
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.reconfigurable.service.PersistLatestServiceConfigurationAdapter;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
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

    private final ConfigurationProvider configuration = new ConfigurationProvider();
    private final ArchiveInheritanceSkeleton archiveService;

    public PersistProviderConfiguration(ReconfigurableProviderImplInterface provider, ObjectId confId, ArchiveInheritanceSkeleton archiveService) {

        try {
            ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
        } catch (MALException ex) {
            // if it was already initialized, then.. cool bro!
        }

        this.archiveService = archiveService;

        ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(
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
            ReconfigurableServiceImplInterface[] providerServices = (ReconfigurableServiceImplInterface[]) provider.getServices().toArray();
            LongList objIds = new LongList();
            
            for (ReconfigurableServiceImplInterface providerService : providerServices) {
                PersistLatestServiceConfigurationAdapter latest = new PersistLatestServiceConfigurationAdapter(providerService, new Long(0), this.archiveService);
                objIds.add(latest.getConfigurationObjectInstId());
            }

            // Store the provider configuration objects
            ConfigurationObjectDetailsList archObj = new ConfigurationObjectDetailsList();
            ConfigurationObjectDetails providerObjects = new ConfigurationObjectDetails();
            ConfigurationObjectSetList setList = new ConfigurationObjectSetList();
            ConfigurationObjectSet set = new ConfigurationObjectSet();
            set.setDomain(configuration.getDomain());
            set.setObjType(ConfigurationHelper.SERVICECONFIGURATION_OBJECT_TYPE);
            set.setObjInstIds(objIds);

            setList.add(set);
            providerObjects.setConfigObjects(setList);
            archObj.add(providerObjects);

            LongList objIds3 = this.archiveService.store(
                    true,
                    ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                    configuration.getDomain(),
                    HelperArchive.generateArchiveDetailsList(null, null, configuration.getNetwork(), new URI("")),
                    archObj,
                    null);

            // Store the provider configuration
            // Related points to the Provider's Configuration Object
            ArchiveDetailsList details = HelperArchive.generateArchiveDetailsList(objIds3.get(0), null, configuration.getNetwork(), new URI(""));
            details.get(0).setInstId(confId.getKey().getInstId());
            IdentifierList providerNameList = new IdentifierList();
            providerNameList.add(provider.getProviderName());

            this.archiveService.store(
                    false,
                    ConfigurationHelper.PROVIDERCONFIGURATION_OBJECT_TYPE,
                    configuration.getDomain(),
                    details,
                    providerNameList,
                    null);

        } catch (MALException ex) {
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(PersistProviderConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
