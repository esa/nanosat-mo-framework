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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.ServicesConnectionDetails;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.CommonHelper;
import org.ccsds.moims.mo.common.directory.DirectoryHelper;
import org.ccsds.moims.mo.common.directory.provider.DirectoryInheritanceSkeleton;
import org.ccsds.moims.mo.common.directory.structures.AddressDetails;
import org.ccsds.moims.mo.common.directory.structures.AddressDetailsList;
import org.ccsds.moims.mo.common.directory.structures.ProviderDetails;
import org.ccsds.moims.mo.common.directory.structures.ProviderDetailsList;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummaryList;
import org.ccsds.moims.mo.common.directory.structures.PublishDetails;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapability;
import org.ccsds.moims.mo.common.directory.structures.ServiceCapabilityList;
import org.ccsds.moims.mo.common.directory.structures.ServiceFilter;
import org.ccsds.moims.mo.common.directory.structures.ServiceKey;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.QoSLevelList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 *
 */
public class DirectoryProviderServiceImpl extends DirectoryInheritanceSkeleton {

    private MALProvider directoryServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private Long uniqueObjIdKey = (long) 0;
    private final HashMap<Long, PublishDetails> providersAvailable = new HashMap<Long, PublishDetails>();
    private COMServicesProvider comServices;

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
                DirectoryHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
            }
        }

        this.comServices = comServices;

        // shut down old service transport
        if (null != directoryServiceProvider) {
            connection.close();
        }

        directoryServiceProvider = connection.startService(DirectoryHelper.DIRECTORY_SERVICE_NAME.toString(), DirectoryHelper.DIRECTORY_SERVICE, false, this);
        
        running = true;
        initialiased = true;
        Logger.getLogger(DirectoryProviderServiceImpl.class.getName()).info("Directory service READY");

    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != directoryServiceProvider) {
                directoryServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(DirectoryProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnection(){
        return this.connection;
    }
    
    @Override
    public ProviderSummaryList lookupProvider(ServiceFilter filter, MALInteraction interaction) throws MALInteractionException, MALException {

        if (null == filter) { // Is the input null?
            throw new IllegalArgumentException("filter argument must not be null");
        }

        IdentifierList inputDomain = filter.getDomain();

        // Check if the domain contains any wildcard that is not in the end, if so, throw error
        for (int i = 0; i < inputDomain.size(); i++) {
            Identifier domainPart = inputDomain.get(i);

            if (domainPart.toString().equals("*") && i != (inputDomain.size() - 1)) {
                throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
            }
        }

/*
        PublishDetailsList allProviders = new PublishDetailsList();
        allProviders.addAll(providersAvailable.values());
*/
        
        LongList keys = new LongList();
        keys.addAll(providersAvailable.keySet());

        // Initialize the final Provider Summary List
        ProviderSummaryList outputList = new ProviderSummaryList();

        // Filter...
//        for (PublishDetails provider : allProviders) { // Filter through all providers
        for (int i = 0; i < keys.size() ; i++) { // Filter through all providers

            PublishDetails provider = providersAvailable.get(keys.get(i));
            ProviderSummary providerOutput = new ProviderSummary();
            
            //Check service provider name
            if(!filter.getServiceProviderName().toString().equals("*")){ // If not a wildcard...
                if (!provider.getProviderName().toString().equals(filter.getServiceProviderName().toString())) {
                    continue;
                }
            }

            if (HelperCOM.domainContainsWildcard(filter.getDomain())) {  // Does it contain a wildcard in the filter?
                // Compare each object one by one...

                if (!HelperCOM.domainMatchesWildcardDomain(provider.getDomain(), inputDomain)) {
                    continue;
                }

            } else { // Direct match...
                if (!inputDomain.equals(provider.getDomain())) {
                    continue;
                }
            }

            // Check session type
            if (filter.getSessionType() != null) {
                if (!provider.getSessionType().equals(filter.getSessionType())) {
                    continue;
                }
            }

            // Check session name
            if (!filter.getSessionName().toString().equals("*")) {
                if (!provider.getSourceSessionName().toString().equals(filter.getSessionName().toString())) {
                    continue;
                }
            }

            // Set the Provider Details structure
            ProviderDetails outProvDetails = new ProviderDetails();
            outProvDetails.setProviderAddresses(provider.getProviderDetails().getProviderAddresses());
            
            ServiceCapabilityList outCap = new ServiceCapabilityList();
            
            // Check each service
            for (int j = 0; j < provider.getProviderDetails().getServiceCapabilities().size(); j++) { // Go through all the services

                ServiceCapability serviceCapability = provider.getProviderDetails().getServiceCapabilities().get(j);
//                AddressDetails providerAddress = provider.getProviderDetails().getProviderAddresses().get(j);

                // Check service key - area field
                if (filter.getServiceKey().getArea().getValue() != 0) {
                    if (!serviceCapability.getServiceKey().getArea().equals(filter.getServiceKey().getArea())) {
                        continue;
                    }
                }

                // Check service key - service field
                if (filter.getServiceKey().getService().getValue() != 0) {
                    if (!serviceCapability.getServiceKey().getService().equals(filter.getServiceKey().getService())) {
                        continue;
                    }
                }

                // Check service key - version field
                if (filter.getServiceKey().getVersion().getValue() != 0) {
                    if (!serviceCapability.getServiceKey().getVersion().equals(filter.getServiceKey().getVersion().getValue())) {
                        continue;
                    }
                }

                // Check service capabilities
                if (!filter.getRequiredCapabilities().isEmpty()) { // Not empty...
                    boolean capExists = false;

                    for (UInteger cap : filter.getRequiredCapabilities()) {
                        // cycle all the ones available in the provider
                        for (UInteger proCap : filter.getRequiredCapabilities()) {
                            if (cap.equals(proCap)) {
                                capExists = true;
                            }
                        }
                    }

                    if (!capExists) { // If the capability we want does not exist, then get out...
                        continue;
                    }
                }
                
                // Add the service to the list of matching services
                outCap.add(serviceCapability);
                
            }

            // It passed all the tests!
            ObjectKey objKey = new ObjectKey();
            objKey.setDomain(connection.getConnectionDetails().getDomain());
            objKey.setInstId(keys.get(i));
            providerOutput.setProviderKey(objKey);
            providerOutput.setProviderName(provider.getProviderName());
            
            outProvDetails.setServiceCapabilities(outCap);
            providerOutput.setProviderDetails(outProvDetails);

            outputList.add(providerOutput);
        }

        // Errors
        // The operation does not return any errors.
        return outputList;  // requirement: 3.4.9.2.d

    }

    @Override
    public Long publishProvider(PublishDetails newProviderDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        Identifier serviceProviderName = newProviderDetails.getProviderName();
        IdentifierList objBodies = new IdentifierList();
        objBodies.add(serviceProviderName);

        ArchiveDetailsList archDetails;

        if (interaction == null) {
            archDetails = HelperArchive.generateArchiveDetailsList(null, null, connection.getConnectionDetails());
        } else {
            archDetails = HelperArchive.generateArchiveDetailsList(null, null, interaction);
        }

        // Check if there are comServices...
        if (comServices == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        // Check if the archive is available...
        if (comServices.getArchiveService() == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        // Store in the Archive the ServiceProvider COM object and get an object instance identifier
        LongList returnedServProvObjIds = comServices.getArchiveService().store(
                true,
                DirectoryHelper.SERVICEPROVIDER_OBJECT_TYPE,
                connection.getConnectionDetails().getDomain(),
                archDetails,
                objBodies,
                null
        );

        Long servProvObjId = (long) 0;

        if (!returnedServProvObjIds.isEmpty()) {
            servProvObjId = returnedServProvObjIds.get(0);
        } else {  // Nothing was returned...
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        ArchiveDetailsList archDetails1;

        if (interaction == null) {  // related contains the objId of the ServiceProvider object
            archDetails1 = HelperArchive.generateArchiveDetailsList(servProvObjId, null, connection.getConnectionDetails());
        } else {
            archDetails1 = HelperArchive.generateArchiveDetailsList(servProvObjId, null, interaction);
        }

        ProviderDetailsList capabilities = new ProviderDetailsList();
        capabilities.add(newProviderDetails.getProviderDetails());

        // Store in the Archive the ProviderCapabilities COM object and get an object instance identifier
        LongList returnedIds = comServices.getArchiveService().store(
                true,
                DirectoryHelper.PROVIDERCAPABILITIES_OBJECT_TYPE,
                connection.getConnectionDetails().getDomain(),
                archDetails1,
                capabilities,
                null
        );

        this.providersAvailable.put(servProvObjId, newProviderDetails);

        return servProvObjId;

    }

    @Override
    public void withdrawProvider(Long providerObjectKey, MALInteraction interaction) throws MALInteractionException, MALException {

        PublishDetails details = providersAvailable.get(providerObjectKey);

        if (details == null) { // The requested provider does not exist
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        // Remove the provider...
        providersAvailable.remove(providerObjectKey);

    }

    public PublishDetails autoLoadURIsFile(String providerName) {
        ServicesConnectionDetails servicesOnFile = new ServicesConnectionDetails();

        try {
            servicesOnFile = servicesOnFile.loadURIFromFiles();
        } catch (MalformedURLException ex) {
            Logger.getLogger(DirectoryProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        HashMap<String, SingleConnectionDetails> map = servicesOnFile.getServices();
        Object[] a = map.keySet().toArray();

        ServiceCapabilityList capabilities = new ServiceCapabilityList();

        // Iterate all the services and make them available...
        for (int i = 0; i < a.length; i++) {
            ServiceCapability capability = new ServiceCapability();

            String servName = (String) a[i];
            SingleConnectionDetails conn = map.get(servName);

            ServiceKey key = new ServiceKey();
            key.setArea(new UShort(conn.getServiceKey().get(0)));
            key.setService(new UShort(conn.getServiceKey().get(1)));
            key.setVersion(new UOctet(conn.getServiceKey().get(2).shortValue()));

            QoSLevelList qos = new QoSLevelList();
            qos.add(QoSLevel.ASSURED);
            NamedValueList qosProperties = new NamedValueList();  // Nothing here for now...

            AddressDetails serviceAddress = new AddressDetails();
            serviceAddress.setSupportedLevels(qos);
            serviceAddress.setQoSproperties(qosProperties);
            serviceAddress.setPriorityLevels(new UInteger(1));  // hum?
            serviceAddress.setServiceURI(conn.getProviderURI());
            serviceAddress.setBrokerURI(conn.getBrokerURI());
            serviceAddress.setBrokerProviderObjInstId(null);

            AddressDetailsList serviceAddresses = new AddressDetailsList();
            serviceAddresses.add(serviceAddress);

            capability.setServiceKey(key);
            capability.setSupportedCapabilities(null); // "If NULL then all capabilities supported."
            capability.setServiceProperties(new NamedValueList());
            capability.setServiceAddresses(serviceAddresses);

            capabilities.add(capability);
        }

        ProviderDetails serviceDetails = new ProviderDetails();
        serviceDetails.setServiceCapabilities(capabilities);
        serviceDetails.setProviderAddresses(new AddressDetailsList());

        PublishDetails newProviderDetails = new PublishDetails();
        newProviderDetails.setProviderName(new Identifier(providerName));
        newProviderDetails.setDomain(connection.getConnectionDetails().getDomain());
        newProviderDetails.setSessionType(connection.getConnectionDetails().getConfiguration().getSession());
        newProviderDetails.setSourceSessionName(null);
        newProviderDetails.setNetwork(connection.getConnectionDetails().getConfiguration().getNetwork());
        newProviderDetails.setProviderDetails(serviceDetails);

        try {
            this.publishProvider(newProviderDetails, null);
            return newProviderDetails;
        } catch (MALInteractionException ex) {
            Logger.getLogger(DirectoryProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(DirectoryProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

}
