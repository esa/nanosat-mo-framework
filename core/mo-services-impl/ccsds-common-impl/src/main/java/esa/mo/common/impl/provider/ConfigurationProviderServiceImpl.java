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
package esa.mo.common.impl.provider;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.ConfigurationServiceInfo;
import org.ccsds.moims.mo.common.configuration.provider.ActivateInteraction;
import org.ccsds.moims.mo.common.configuration.provider.ConfigurationInheritanceSkeleton;
import org.ccsds.moims.mo.common.configuration.provider.StoreCurrentInteraction;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationType;
import org.ccsds.moims.mo.common.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Configuration service Provider.
 */
public class ConfigurationProviderServiceImpl extends ConfigurationInheritanceSkeleton {

    protected static COMService service;

    private MALProvider configurationServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final HashMap<ObjectId, Element> configs = new HashMap<>();
    private final ConnectionProvider connection = new ConnectionProvider();
    private final Random random = new Random();
    private COMServicesProvider comServices;
    private final HashMap<ObjectId, ConfigurationObjectDetails> serviceConfigurations = new HashMap<>();
    private final HashMap<ObjectId, ConfigurationObjectDetails> providerConfigurations = new HashMap<>();
    private final HashMap<ObjectId, ConfigurationObjectDetails> compositeConfigurations = new HashMap<>();

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices) throws MALException {
        long timestamp = System.currentTimeMillis();

        // shut down old service transport
        if (null != configurationServiceProvider) {
            connection.closeAll();
        }

        service = ConfigurationHelper.CONFIGURATION_SERVICE;
        configurationServiceProvider = connection.startService(ConfigurationServiceInfo.CONFIGURATION_SERVICE_NAME.toString(), 
                ConfigurationHelper.CONFIGURATION_SERVICE, false, this);
        this.comServices = comServices;

        running = true;
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).info(
                "Configuration service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != configurationServiceProvider) {
                configurationServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.WARNING,
                "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public void activate(ObjectKey serviceProvider, ObjectId configObjId, ActivateInteraction interaction) throws MALInteractionException, MALException {
        ObjectIdList objBodies = new ObjectIdList();
        objBodies.add(configObjId);

        Long related = null;
        ObjectId source = this.comServices.getActivityTrackingService().storeCOMOperationActivity(interaction.getInteraction(), null);

        // Store Event in the Archive
        Long objId = this.comServices.getEventService().generateAndStoreEvent(
                ConfigurationServiceInfo.CONFIGURATIONSWITCH_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                objBodies,
                related,
                source,
                interaction.getInteraction());

        try {
            // Send Activation event
            this.comServices.getEventService().publishEvent(interaction.getInteraction(), objId,
                    ConfigurationServiceInfo.CONFIGURATIONSWITCH_OBJECT_TYPE, related, source, objBodies);
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        interaction.sendResponse(true, objBodies);
    }

    @Override
    public ObjectIdList getCurrent(ObjectKey serviceProvider, ServiceKey serviceKey, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(ObjectKey serviceProvider, ObjectIdList configObjIds, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void storeCurrent(ObjectKey serviceProvider, ServiceKey serviceKey, Boolean autoAdd, StoreCurrentInteraction interaction) throws MALInteractionException, MALException {
//    public void storeCurrent(ServiceProviderKey service, ConfigurationType type, Boolean autoAdd, 
//            StoreCurrentInteraction interaction) throws MALInteractionException, MALException {
        interaction.sendAcknowledgement();

        if (this.comServices.getEventService() == null) {  // If there is no event service then we can't really do anything...
            interaction.sendError(null);
        }

        Long related = null;
        ObjectId source = this.comServices.getActivityTrackingService().storeCOMOperationActivity(interaction
            .getInteraction(), null);

        // Store Event in the Archive
        Long objId = this.comServices.getEventService().generateAndStoreEvent(
                ConfigurationServiceInfo.CONFIGURATIONSTORE_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                null,
                related,
                source,
                interaction.getInteraction());

        // Create the Adapter which will wait for the callback of the service
        try {  // Consumer of Events for the configurations
            EventConsumerServiceImpl eventServiceConsumer = new EventConsumerServiceImpl(comServices.getEventService()
                .getConnectionProvider().getConnectionDetails());

            // Listen only to Configuration events
            ObjectType original = ConfigurationServiceInfo.CONFIGURATIONOBJECTS_OBJECT_TYPE;
            ObjectType objType = new ObjectType(original.getArea(),
                    original.getService(),
                    original.getVersion(),
                    new UShort((short) 0)); // Select "any" object from the Configuration service

            Long key2 = HelperCOM.generateSubKey(objType);
            Subscription subscriptionPartial = ConnectionConsumer.subscriptionKeys(new Identifier("*"), key2, 0L, 0L);
            Identifier subId = new Identifier("ConfigurationEvent" + random.nextInt());  // Add some randomness in the subscriptionId to avoid colisions

            Subscription subscription = new Subscription(subId, null, null, subscriptionPartial.getFilters());
            //subscription.setSubscriptionId(subId);
            EventConsumerConfigurationCallbackAdapter adapter = new EventConsumerConfigurationCallbackAdapter(objId);
            eventServiceConsumer.getEventStub().monitorEventRegister(subscription, adapter);

            try {
                // Send Store event
                this.comServices.getEventService().publishEvent(interaction.getInteraction(), objId,
                        ConfigurationServiceInfo.CONFIGURATIONSTORE_OBJECT_TYPE, related, source, null);
            } catch (IOException ex) {
                Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Wait for the reply...
            ObjectId response = adapter.getResponseFromService();
            interaction.sendResponse(response); // Send the response

            // Don't forget to deregister from the Event service! ;)
            IdentifierList subIds = new IdentifierList();
            subIds.add(subId);
            eventServiceConsumer.getEventStub().monitorEventDeregister(subIds);
        } catch (MALException | MalformedURLException | MALInteractionException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            interaction.sendError(null);
        }
    }

    @Override
    public org.ccsds.moims.mo.mal.structures.File exportXML(ObjectId confObjId, Boolean returnComplete,
        MALInteraction interaction) throws MALInteractionException, MALException {

        // Configuration COM object
        ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(comServices.getArchiveService(),
            confObjId.getType(), confObjId.getKey().getDomain(), confObjId.getKey().getInstId());
        Element objBody = (Element) comObject.getObject();

        FileOutputStream fos;
        try {
            // Create the file
            java.io.File file = new java.io.File("temporary_file.xml");
            fos = new FileOutputStream(file);
            /*
            esa.mo.mal.encoder.xml.XMLElementOutputStream xmlEOS = new esa.mo.mal.encoder.xml.XMLElementOutputStream(fos);
            xmlEOS.setIndentation(true);
            
            // Write the object in the xml file
            xmlEOS.writeCOMObject(comObject.getDomain().toString(), comObject.getObjectType().toString(), comObject.getObjectId().toString(), 
                    comObject.getArchiveDetails().getDetails().getRelated(), comObject.getArchiveDetails().getDetails().getSource(), 
                    comObject.getArchiveDetails().getNetwork(), comObject.getArchiveDetails().getTimestamp(), 
                    comObject.getArchiveDetails().getProvider(), comObject.getObject());
            
            xmlEOS.writeCOMObject(comObject.getDomain().toString(), comObject.getObjectType().toString(), comObject.getObjectId().toString(), 
                    comObject.getArchiveDetails().getDetails().getRelated(), comObject.getArchiveDetails().getDetails().getSource(), 
                    comObject.getArchiveDetails().getNetwork(), comObject.getArchiveDetails().getTimestamp(), 
                    comObject.getArchiveDetails().getProvider(), comObject.getObject());
            
            //            xmlEOS.writeElement(objBody, null);
            
//            ( (ConfigurationObjectDetails) objBody).getConfigObjects().get(0).getDomain().add(new Identifier("Test123"));
//            xmlEOS.writeElement(objBody, null);
            xmlEOS.flush();
            xmlEOS.close();
            */
            // Create a static method in the Helper Tools to cconvert rom a Java file to a MAL File and vice versa
            org.ccsds.moims.mo.mal.structures.File xmlFile = new org.ccsds.moims.mo.mal.structures.File();
//            xmlFile.setContent((Blob) HelperAttributes.javaType2Attribute(Files.readAllBytes(file.toPath())));
            //xmlFile.setName("MyFilename.file");
            return xmlFile;

        } catch (IOException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public ObjectId importXML(org.ccsds.moims.mo.mal.structures.File xmlFile, MALInteraction interaction)
        throws MALInteractionException, MALException {
        FileInputStream fis;

        try {
            // Create the file
            java.io.File file = new java.io.File("temporary_file.xml");
            fis = new FileInputStream(file);
            /*
            esa.mo.mal.encoder.xml.XMLElementInputStream xmlEIS = new esa.mo.mal.encoder.xml.XMLElementInputStream(fis);
            // Dcode the object from the xml file
            ConfigurationObjectDetails decodedElement1 = (ConfigurationObjectDetails) xmlEIS.readElement(new ConfigurationObjectDetails(), null);
            ConfigurationObjectDetails decodedElement2 = (ConfigurationObjectDetails) xmlEIS.readElement(new ConfigurationObjectDetails(), null);
            */
            //            ConfigurationObjectDetails decodedElement = (ConfigurationObjectDetails) xmlEIS.readElement(new ConfigurationObjectDetails(), null);
            fis.close();
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public ObjectIdList list(ConfigurationType type, IdentifierList domain, ServiceKey serviceKey,
            MALInteraction interaction) throws MALInteractionException, MALException {
        // Select the right type of Configuration
        HashMap<ObjectId, ConfigurationObjectDetails> configurations = null;

        if (type == ConfigurationType.SERVICE) {
            configurations = serviceConfigurations;
        }

        if (type == ConfigurationType.PROVIDER) {
            configurations = providerConfigurations;
        }

        /*
        if (type == ConfigurationType.COMPOSITE) {
            configurations = compositeConfigurations;
        }
        */

        ObjectIdList out = new ObjectIdList();

        if (configurations != null) {
            out.addAll(configurations.keySet());
        }

        // Do we have wildcards in the domain?
        // Yes
        // Get all the Composite, and check one by one
        // return the list of the valid ones
        // The domain has no wildcards...
        // Check if the service field has any wildcards in it...
        // Yes
        // Get all the configurations for the specific domain
        // Filter them according to the wildcard criteria
        // return the list of the configs
        // There are no wildcards neither in the domain nor in the service...
        // Then it is a direct match.. search the configurations available for that
        return out;

    }

    @Override
    public void remove(ObjectKey serviceProvider, ObjectIdList configObjIds, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static class EventConsumerConfigurationCallbackAdapter extends EventAdapter {

        private final Long originalObjId;
        private final int timeout = 15; // (seconds) Default timeout for the waiting of a response from the service
        private ObjectId objectId = null;
        private boolean available = false;

        private EventConsumerConfigurationCallbackAdapter(Long emittedEventObjId) {
            originalObjId = emittedEventObjId;
        }

        @Override
        public synchronized void monitorEventNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
                UpdateHeader updateHeader, ObjectDetails objectDetails,
                Element object, Map qosProperties) {
            if (objectDetails == null) {
                return;
            }

            // Does the objId received matches the one the we originally sent to the service?
            if (originalObjId.equals(objectDetails.getRelated())) {
                objectId = objectDetails.getSource();
                this.notify();
                available = true;
            }
        }

        public synchronized ObjectId getResponseFromService() {
            if (available) {
                return objectId;
            }

            try {  // Wait until it is available...
                this.wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            return objectId;
        }

    }

}
