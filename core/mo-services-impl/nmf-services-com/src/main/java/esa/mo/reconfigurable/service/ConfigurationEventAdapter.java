/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectLinks;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.configuration.ConfigurationServiceInfo;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.HeterogeneousList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.NullableAttributeList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
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

    public ConfigurationEventAdapter(COMServicesProvider comServices, ReconfigurableService serviceImpl,
        IdentifierList providerDomain, URI providerURI) {
        this.comServices = comServices;
        this.serviceImpl = serviceImpl;
        this.providerDomain = providerDomain;
        this.providerURI = providerURI;
    }

    @Override
    public void monitorEventNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
            UpdateHeader updateHeader, ObjectLinks objectLinks,
            Element object, Map qosProperties) {
        // Notification received from the Configuration serviceImpl...
        NullableAttributeList subkeys = updateHeader.getKeyValues();
        Identifier eventObjNumber = (Identifier) subkeys.get(0).getValue();

        // Check if it is a "Configuration switch Request" or a "Current Configuration Store"
        if (!eventObjNumber.toString().equals(ConfigurationServiceInfo.CONFIGURATIONSWITCH_OBJECT_NUMBER.toString())
                && !eventObjNumber.toString().equals(ConfigurationServiceInfo.CONFIGURATIONSTORE_OBJECT_NUMBER.toString())) {
            return;
        }

        // If so... check if it is a "Configuration switch Request"
        if (eventObjNumber.toString().equals(ConfigurationServiceInfo.CONFIGURATIONSWITCH_OBJECT_NUMBER.toString())) {
            if (object == null) {
                return;
            }

            // Get the objId of the Configuration
            ObjectId obj = (ObjectId) object;

            // Check if it is a Configuration event for this particular service (based on the service type, domain ?)
            if (obj.getType().getArea().equals(serviceImpl.getCOMService().getAreaNumber())
                    && obj.getType().getNumber().equals(serviceImpl.getCOMService().getServiceNumber())
                    && obj.getDomain().equals(providerDomain)) {

                // Retrieve it from the Archive
                ConfigurationObjectDetails configurationObj = (ConfigurationObjectDetails) HelperArchive.getObjectBodyFromArchive(
                        comServices.getArchiveService(), obj.getType(),
                        obj.getDomain(), obj.getInstId());

                // Reload the retrieved configuration
                Boolean confChanged = serviceImpl.reloadConfiguration(configurationObj);

                if (confChanged) {
                    // Todo: Publish success
                } else {
                    // Todo: Publish failure
                }
            }
        }

        // Long entityKey3 = (Long) HelperAttributes.attribute2JavaType(subkeys.get(2).getValue());
        Attribute nullAtt = subkeys.get(2).getValue();
        Long entityKey3 = (Long) Attribute.javaType2Attribute(nullAtt);

        // -----------------------------------------------------------
        // Check if it is a "Current Configuration Store"
        if (eventObjNumber.toString().equals(ConfigurationServiceInfo.CONFIGURATIONSTORE_OBJECT_NUMBER.toString())) {
            ConfigurationObjectDetails set = serviceImpl.getCurrentConfiguration();
            HeterogeneousList bodies = new HeterogeneousList();
            bodies.add(set);

            // For the ConfigurationObjects:
            ObjectType objType = ConfigurationServiceInfo.CONFIGURATIONOBJECTS_OBJECT_TYPE;

            ArchiveDetails archiveDetails = new ArchiveDetails(new Long(0),
                    new ObjectLinks(entityKey3, null),
                    null,
                    FineTime.now(),
                    msgHeader.getFromURI());

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
                this.publishConfigurationStoredSuccess(objId, entityKey3);
            } catch (MALException ex) {
                // Publish event: Failure with the objId of the Configuration stored
                this.publishConfigurationStoredFailure(entityKey3);  // Event objId
            } catch (MALInteractionException ex) {
                // Publish event: Failure with the objId of the Configuration stored
                this.publishConfigurationStoredFailure(entityKey3);  // Event objId
            }
        }
    }

    private void publishConfigurationStoredFailure(Long related) {
        // Publish event: Failure with the objId of the Configuration stored
        ObjectType objTypeEvent = ConfigurationServiceInfo.CONFIGURATIONSTORED_OBJECT_TYPE;
        BooleanList bool = new BooleanList();
        bool.add(false);  // Failure
        ObjectId eventSource = null;  // It was not stored...

        try {
            comServices.getEventService().publishEvent(providerURI, null, objTypeEvent, related, eventSource, bool);
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationEventAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void publishConfigurationStoredSuccess(Long objId, Long related) {
        // Publish event: Success with the objId of the Configuration stored
        ObjectType objTypeEvent = ConfigurationServiceInfo.CONFIGURATIONSTORED_OBJECT_TYPE;
        BooleanList bool = new BooleanList();
        bool.add(true);  // Success
        ObjectId eventSource = new ObjectId(
                ConfigurationServiceInfo.CONFIGURATIONOBJECTS_OBJECT_TYPE,
                providerDomain,
                objId
        );

        try {
            comServices.getEventService().publishEvent(providerURI, objId, objTypeEvent, related, eventSource, bool);
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationEventAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
