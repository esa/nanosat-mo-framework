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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.helpers.HelperTime;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetailsList;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Listens for a Configuration change COM Event from the Event serviceImpl
 *
 */
public class ConfigurationEventAdapter extends EventAdapter implements Serializable {

    private final COMServicesProvider comServices;
    private final ReconfigurableService serviceImpl;
    private final IdentifierList providerDomain;
    private final URI providerURI;

    public ConfigurationEventAdapter(COMServicesProvider comServices,
            ReconfigurableService serviceImpl,
            IdentifierList providerDomain,
            URI providerURI) {
        this.comServices = comServices;
        this.serviceImpl = serviceImpl;
        this.providerDomain = providerDomain;
        this.providerURI = providerURI;
    }

    @Override
    public void monitorEventNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
            UpdateHeaderList updateHeaderList, ObjectDetailsList objectDetailsList,
            ElementList objects, Map qosProperties) {
        // Notification received from the Configuration serviceImpl...
        for (int i = 0; i < objectDetailsList.size(); i++) {
            Identifier eventObjNumber = updateHeaderList.get(i).getKey().getFirstSubKey();

            // Check if it is a "Configuration switch Request" or a "Current Configuration Store"
            if (!eventObjNumber.toString().equals(ConfigurationHelper.CONFIGURATIONSWITCH_OBJECT_NUMBER.toString())
                    && !eventObjNumber.toString().equals(ConfigurationHelper.CONFIGURATIONSTORE_OBJECT_NUMBER.toString())) {
                return;
            }

            // If so... check if it is a "Configuration switch Request"
            if (eventObjNumber.toString().equals(ConfigurationHelper.CONFIGURATIONSWITCH_OBJECT_NUMBER.toString())) {
                if (objects == null) {
                    return;
                }

                if (objects.isEmpty()) {
                    return; // No objects... 
                }

                // Get the objId of the Configuration
                ObjectId obj = (ObjectId) objects.get(0);

                if (obj == null) {
                    return;
                }

                // Check if it is a Configuration event for this particular service (based on the service type, domain ?)
                if (obj.getType().getArea().equals(serviceImpl.getCOMService().getArea().getNumber())
                        && obj.getType().getNumber().equals(serviceImpl.getCOMService().getNumber())
                        && obj.getKey().getDomain().equals(providerDomain)) {

                    // Retrieve it from the Archive
                    ConfigurationObjectDetails configurationObj = (ConfigurationObjectDetails) 
                            HelperArchive.getObjectBodyFromArchive(
                            comServices.getArchiveService(), obj.getType(),
                            obj.getKey().getDomain(), obj.getKey().getInstId());

                    // Reload the retrieved configuration
                    Boolean confChanged = serviceImpl.reloadConfiguration(configurationObj);

                    if (confChanged) {
                        // Todo: Publish success
                    } else {
                        // Todo: Publish failure
                    }
                }
            }

            // -----------------------------------------------------------
            // Check if it is a "Current Configuration Store"
            if (eventObjNumber.toString().equals(ConfigurationHelper.CONFIGURATIONSTORE_OBJECT_NUMBER.toString())) {
                ConfigurationObjectDetails set = serviceImpl.getCurrentConfiguration();
                ConfigurationObjectDetailsList bodies = new ConfigurationObjectDetailsList();
                bodies.add(set);

                // For the ConfigurationObjects:
                ObjectType objType = ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE;

                ArchiveDetails archiveDetails = new ArchiveDetails();
                archiveDetails.setInstId(0L);
                archiveDetails.setDetails(new ObjectDetails(updateHeaderList.get(i).getKey().getThirdSubKey(), null));  // Event objId
                archiveDetails.setNetwork(msgHeader.getNetworkZone());
                archiveDetails.setTimestamp(HelperTime.getTimestamp());
                archiveDetails.setProvider(msgHeader.getURIFrom());

                ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
                archiveDetailsList.add(archiveDetails);

                try {
                    // Store the Configuration Object in the COM Archive
                    LongList objIds = comServices.getArchiveService().store(
                            true,
                            objType,
                            providerDomain,
                            archiveDetailsList,
                            bodies,
                            null);

                    Long objId = objIds.get(0);

                    // Publish event: Success with the objId of the Configuration stored
                    this.publishConfigurationStoredSuccess(objId, updateHeaderList.get(i).getKey().getThirdSubKey());
                } catch (MALException | MALInteractionException ex) {
                    // Publish event: Failure with the objId of the Configuration stored
                    this.publishConfigurationStoredFailure(updateHeaderList.get(i).getKey().getThirdSubKey());  // Event objId
                }
            }
        }
    }

    private void publishConfigurationStoredFailure(Long related) {
        // Publish event: Failure with the objId of the Configuration stored
        ObjectType objTypeEvent = ConfigurationHelper.CONFIGURATIONSTORED_OBJECT_TYPE;
        BooleanList bool = new BooleanList();
        bool.add(false);  // Failure
        ObjectId eventSource = null;  // It was not stored...

        try {
            comServices.getEventService().publishEvent(
                    providerURI,
                    null,
                    objTypeEvent,
                    related,
                    eventSource,
                    bool
            );
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationEventAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void publishConfigurationStoredSuccess(Long objId, Long related) {
        // Publish event: Success with the objId of the Configuration stored
        ObjectType objTypeEvent = ConfigurationHelper.CONFIGURATIONSTORED_OBJECT_TYPE;
        BooleanList bool = new BooleanList();
        bool.add(true);  // Success
        ObjectId eventSource = new ObjectId();
        eventSource.setType(ConfigurationHelper.CONFIGURATIONOBJECTS_OBJECT_TYPE);
        eventSource.setKey(new ObjectKey(providerDomain, objId));

        try {
            comServices.getEventService().publishEvent(
                    providerURI,
                    objId,
                    objTypeEvent,
                    related,
                    eventSource,
                    bool
            );
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationEventAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
