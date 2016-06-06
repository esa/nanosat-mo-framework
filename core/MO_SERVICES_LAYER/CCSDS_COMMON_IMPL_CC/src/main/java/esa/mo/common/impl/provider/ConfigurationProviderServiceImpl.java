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
package esa.mo.common.impl.provider;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.provider.ConfigurationInheritanceSkeleton;
import org.ccsds.moims.mo.common.configuration.provider.StoreCurrentInteraction;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationType;
import org.ccsds.moims.mo.common.configuration.structures.ServiceProviderKey;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 *
 */
public class ConfigurationProviderServiceImpl extends ConfigurationInheritanceSkeleton {

    protected static COMService service;

    private MALProvider configurationServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final HashMap<ObjectId, Element> configs = new HashMap<ObjectId, Element>();
    private final ConnectionProvider connection = new ConnectionProvider();
    private final Random random = new Random();
    private COMServicesProvider comServices;
    private final HashMap<ObjectId, ConfigurationObjectDetails> serviceConfigurations = new HashMap<ObjectId, ConfigurationObjectDetails>();
    private final HashMap<ObjectId, ConfigurationObjectDetails> providerConfigurations = new HashMap<ObjectId, ConfigurationObjectDetails>();
    private final HashMap<ObjectId, ConfigurationObjectDetails> compositeConfigurations = new HashMap<ObjectId, ConfigurationObjectDetails>();

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(CommonHelper.COMMON_AREA_NAME, CommonHelper.COMMON_AREA_VERSION) == null) {
                CommonHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

        }

        // shut down old service transport
        if (null != configurationServiceProvider) {
            connection.close();
        }

        service = ConfigurationHelper.CONFIGURATION_SERVICE;
        configurationServiceProvider = connection.startService(ConfigurationHelper.CONFIGURATION_SERVICE_NAME.toString(), ConfigurationHelper.CONFIGURATION_SERVICE, false, this);
        this.comServices = comServices;

        running = true;
        initialiased = true;
        Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).info("Configuration service READY");

    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != configurationServiceProvider) {
                configurationServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public void activate(ObjectId configObjId, MALInteraction interaction) throws MALInteractionException, MALException {

        ObjectIdList objBodies = new ObjectIdList();
        objBodies.add(configObjId);

        Long related = null;
        ObjectId source = this.comServices.getActivityTrackingService().storeCOMOperationActivity(interaction, null);

        // Store Event in the Archive
        Long objId = this.comServices.getEventService().generateAndStoreEvent(
                ConfigurationHelper.CONFIGURATIONSWITCH_OBJECT_TYPE,
                connection.getConnectionDetails().getDomain(),
                objBodies,
                related,
                source,
                interaction);

        // Send Activation event
        this.comServices.getEventService().publishEvent(interaction, objId,
                ConfigurationHelper.CONFIGURATIONSWITCH_OBJECT_TYPE, related, source, objBodies);

    }

    @Override
    public ObjectId getCurrent(ServiceProviderKey service, ConfigurationType type, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add(ObjectId configObj, ObjectIdList configObjIds, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void storeCurrent(ServiceProviderKey service, ConfigurationType type, Boolean autoAdd, StoreCurrentInteraction interaction) throws MALInteractionException, MALException {

        interaction.sendAcknowledgement();

        if (this.comServices.getEventService() == null) {  // If there is no event service then we can't really do anything...
            interaction.sendError(null);
        }

        Long related = null;
        ObjectId source = this.comServices.getActivityTrackingService().storeCOMOperationActivity(interaction.getInteraction(), null);

        // Store Event in the Archive
        Long objId = this.comServices.getEventService().generateAndStoreEvent(
                ConfigurationHelper.CONFIGURATIONSTORE_OBJECT_TYPE,
                connection.getConnectionDetails().getDomain(),
                null,
                related,
                source,
                interaction.getInteraction());

        // Create the Adapter which will wait for the callback of the service
        try {  // Consumer of Events for the configurations
            EventConsumerServiceImpl eventServiceConsumer = new EventConsumerServiceImpl(comServices.getEventService().getConnectionProvider().getConnectionDetails());

            // For the Configuration service: area=3 ; service=5; version=1
            ObjectType objType = HelperCOM.generateCOMObjectType(3, 5, 1, 0);  // Listen only to Configuration events
            Long key2 = HelperCOM.generateSubKey(objType);
            Subscription subscription = ConnectionConsumer.subscriptionKeys(new Identifier("*"), key2, 0L, 0L);
            Identifier subId = new Identifier("ConfigurationEvent" + random.nextInt());  // Add some randomness in the subscriptionId to avoid colisions

            subscription.setSubscriptionId(subId);
            EventConsumerConfigurationCallbackAdapter adapter = new EventConsumerConfigurationCallbackAdapter(objId);
            eventServiceConsumer.getEventStub().monitorEventRegister(subscription, adapter);

            // Send Store event
            this.comServices.getEventService().publishEvent(interaction.getInteraction(), objId,
                    ConfigurationHelper.CONFIGURATIONSTORE_OBJECT_TYPE, related, source, null);

            // Wait for the reply...
            ObjectId response = adapter.getResponseFromService();
            interaction.sendResponse(response); // Send the response

            // Don't forget to deregister from the Event service! ;)
            IdentifierList subIds = new IdentifierList();
            subIds.add(subId);
            eventServiceConsumer.getEventStub().monitorEventDeregister(subIds);

        } catch (MALException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            interaction.sendError(null);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            interaction.sendError(null);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            interaction.sendError(null);
        }

    }

    @Override
    public org.ccsds.moims.mo.mal.structures.File exportXML(ObjectId confObjId, Boolean getComplete, MALInteraction interaction) throws MALInteractionException, MALException {

        // Configuration COM object
        ArchivePersistenceObject comObject = HelperArchive.getArchiveCOMObject(comServices.getArchiveService(), confObjId.getType(), confObjId.getKey().getDomain(), confObjId.getKey().getInstId());
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
            
//            ( (ConfigurationObjectDetails) objBody).getConfigObjects().get(0).getDomain().add(new Identifier("César"));
//            xmlEOS.writeElement(objBody, null);
            xmlEOS.flush();
            xmlEOS.close();
*/
            // Create a static method in the Helper Tools to cconvert rom a Java file to a MAL File and vice versa
            org.ccsds.moims.mo.mal.structures.File xmlFile = new org.ccsds.moims.mo.mal.structures.File();
//            xmlFile.setContent((Blob) HelperAttributes.javaType2Attribute(Files.readAllBytes(file.toPath())));
            xmlFile.setName(new Identifier("bbgbgbdg"));

            return xmlFile;

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public ObjectId importXML(org.ccsds.moims.mo.mal.structures.File xmlFile, MALInteraction interaction) throws MALInteractionException, MALException {

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

        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfigurationProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public ObjectIdList list(ServiceProviderKey service, ConfigurationType type, MALInteraction interaction) throws MALInteractionException, MALException {

        // Select the right type of Configuration
        HashMap<ObjectId, ConfigurationObjectDetails> configurations = null;

        if (type == ConfigurationType.SERVICE) {
            configurations = serviceConfigurations;
        }

        if (type == ConfigurationType.PROVIDER) {
            configurations = providerConfigurations;
        }

        if (type == ConfigurationType.COMPOSITE) {
            configurations = compositeConfigurations;
        }

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
    public void remove(ObjectId configObj, MALInteraction interaction) throws MALInteractionException, MALException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public class EventConsumerConfigurationCallbackAdapter extends EventAdapter {

        private final Long originalObjId;
        private int timeout = 15; // (seconds) Default timeout for the waiting of a response from the service
        private ObjectId objectId = null;
        private boolean available = false;

        private EventConsumerConfigurationCallbackAdapter(Long emittedEventObjId) {
            originalObjId = emittedEventObjId;
        }

        @Override
        public synchronized void monitorEventNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
                UpdateHeaderList updateHeaderList, ObjectDetailsList objectDetailsList,
                ElementList objects, Map qosProperties) {

            if (objectDetailsList.size() != 1) {
                return;
            }

            // Does the objId received matches the one the we originally sent to the service?
            if (originalObjId.equals(objectDetailsList.get(0).getRelated())) {
                objectId = objectDetailsList.get(0).getSource();
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
