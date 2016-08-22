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
package esa.mo.mc.impl.provider;

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.reconfigurable.service.ConfigurationNotificationInterface;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.common.configuration.ConfigurationHelper;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectDetails;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSet;
import org.ccsds.moims.mo.common.configuration.structures.ConfigurationObjectSetList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.body.GetValueResponse;
import org.ccsds.moims.mo.mc.parameter.provider.MonitorValuePublisher;
import org.ccsds.moims.mo.mc.parameter.provider.ParameterInheritanceSkeleton;
import org.ccsds.moims.mo.mc.parameter.structures.*;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;

/**
 *
 */
public class ParameterProviderServiceImpl extends ParameterInheritanceSkeleton implements ReconfigurableServiceImplInterface {

    private MALProvider parameterServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private MonitorValuePublisher publisher;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private ParameterManager manager;
    private PeriodicReportingManager periodicReportingManager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final ConfigurationProvider configuration = new ConfigurationProvider();
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private EventConsumerServiceImpl eventServiceConsumer;
    private ConfigurationNotificationInterface configurationAdapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param parameterManager
     * @throws MALException On initialisation error.
     */
    public synchronized void init(ParameterManager parameterManager) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

            try {
                ParameterHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

        }

        publisher = createMonitorValuePublisher(configuration.getDomain(),
                configuration.getNetwork(),
                SessionType.LIVE,
                new Identifier("LIVE"),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // shut down old service transport
        if (null != parameterServiceProvider) {
            connection.close();
        }

        parameterServiceProvider = connection.startService(ParameterHelper.PARAMETER_SERVICE_NAME.toString(), ParameterHelper.PARAMETER_SERVICE, this);

        running = true;
        manager = parameterManager;
        periodicReportingManager = new PeriodicReportingManager();
        periodicReportingManager.init(); // Initialize the Periodic Reporting Manager

        manager.setGenerationEnabledAll(false, connection.getConnectionDetails());
        groupService.init(manager.getArchiveService());

        /*
        try {  // Consumer of Events for the configurations
            if (manager.getEventService() != null) { // Do we have the Event service?
                eventServiceConsumer = new EventConsumerServiceImpl(manager.getEventService().getConnectionProvider().getConnectionDetails());
                
                // For the Configuration service: area=3 ; service=5; version=1
                ObjectType objType = HelperCOM.generateCOMObjectType(3, 5, 1, 0);  // Listen only to Configuration events
                Long key2 = HelperCOM.generateSubKey(objType);
                Subscription subscription = ConnectionConsumer.subscriptionKeys(new Identifier("*"), key2, 0L, 0L);
                eventServiceConsumer.getEventStub().monitorEventRegister(subscription,
                        new ConfigurationEventAdapter(
                                parameterManager.getCOMServices(), this, this.connection.getConnectionDetails().getDomain(), 
                                this.connection.getConnectionDetails().getProviderURI(), ParameterHelper.PARAMETER_SERVICE_NUMBER)
                        );

            }

        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
        initialiased = true;
        Logger.getLogger(ParameterProviderServiceImpl.class.getName()).info("Parameter service READY");

    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != parameterServiceProvider) {
                parameterServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    private void publishParameterUpdate(final Long objId) {
        this.publishParameterUpdate(objId, true);
    }
    
    private void publishParameterUpdate(final Long objId, final boolean storeIt) {
        try {
            synchronized(lock){
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating Parameter update for the Parameter Definition: {0} (Identifier: {1})",
                    new Object[]{
                        objId, new Identifier(manager.get(objId).getName().toString())
                    });

            final ParameterValue parameterValue = manager.getParameterValue(objId);
            final Long pValObjId = (storeIt) ? 
                    manager.storeAndGeneratePValobjId(parameterValue, objId, connection.getConnectionDetails()) :
                    HelperTime.getTimestampMillis().getValue(); // requirement: 3.3.4.2

            //  requirements: 3.3.5.2.1 , 3.3.5.2.2 , 3.3.5.2.3 , 3.3.5.2.4
            final EntityKey ekey = new EntityKey(new Identifier(manager.get(objId).getName().toString()), objId, pValObjId, null);
            final Time timestamp = HelperTime.getTimestampMillis(); //  requirement: 3.3.5.2.5

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectIdList objectIdlst = new ObjectIdList();
            final ParameterValueList pVallst = new ParameterValueList();

            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
            objectIdlst.add(null); // requirement: 3.3.5.2.7 (3.3.5.2.6 not necessary. We will not use it for periodic updates)
            pVallst.add(parameterValue); // requirement: 3.3.5.2.8

            publisher.publish(hdrlst, objectIdlst, pVallst);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public GetValueResponse getValue(final LongList lLongList, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement 3.3.6.2.1

        LongList outLongLst = new LongList();
        UIntegerList unkIndexList = new UIntegerList();
        ParameterValueList outPValLst;
        ParameterDefinitionDetails tempPDef;
        Long tempLong;

        if (null == lLongList) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < lLongList.size(); index++) {
            tempLong = lLongList.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.3.6.2.2
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            tempPDef = manager.get(tempLong);

            if (tempPDef != null) { // Does the ParameterDefinition exist?
                outLongLst.add(tempLong); //yap
            } else {
                unkIndexList.add(new UInteger(index)); // add the index to the list of errors
            }
        }

        outPValLst = manager.getParameterValues(outLongLst); // requirement: 3.3.6.2.4

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.3.6.2.3
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        return new GetValueResponse(outLongLst, outPValLst);
    }

    @Override
    public void enableGeneration(final Boolean isGroupIds, final InstanceBooleanPairList enableInstances,
            final MALInteraction interaction) throws MALException, MALInteractionException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        InstanceBooleanPair enableInstance;

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (null == isGroupIds || null == enableInstances) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        boolean foundWildcard = false;

        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.4.8.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.4.8.2.c
                manager.setGenerationEnabledAll(instance.getValue(), connection.getConnectionDetails());
                periodicReportingManager.refreshAll();
                foundWildcard = true;

                if (configurationAdapter != null) {
                    configurationAdapter.configurationChanged(this);
                }

                break;
            }
        }

        if (!foundWildcard) { // requirement: 3.4.8.2.d
            for (int index = 0; index < enableInstances.size(); index++) {
                enableInstance = enableInstances.get(index);

                if (isGroupIds) {
                    GroupDetails group = groupService.retrieveGroupDetailsFromArchive(connection.getConnectionDetails().getDomain(), enableInstances.get(index).getId());

                    if (group == null) {
                        invIndexList.add(new UInteger(index)); // requirement: 3.4.8.h
                    } else {
                        LongList objIds = groupService.getGroupObjectIdsFromGroup(group, enableInstances.get(index).getId());

                        if (objIds != null) {
                            for (Long objId : objIds) {
                                objIdToBeEnabled.add(objId);
                                valueToBeEnabled.add(enableInstance.getValue());

                                if (!manager.exists(objId)) { // does it exist? 
                                    unkIndexList.add(new UInteger(index)); // requirement: 3.4.8.g
                                }
                            }
                        }
                    }

                } else {  // requirement: 3.4.8.g
                    objIdToBeEnabled.add(enableInstance.getId());
                    valueToBeEnabled.add(enableInstance.getValue());

                    if (!manager.exists(enableInstance.getId())) { // does it exist? 
                        unkIndexList.add(new UInteger(index)); // requirement: 3.4.8.g
                    }
                }
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.4.8.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.4.8.3.2
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!foundWildcard) { // requirement: 3.4.8.2.d
            // requirement: 3.4.8.i (This part of the code is not reached if an error is thrown)
            for (int index = 0; index < objIdToBeEnabled.size(); index++) {
                // requirement: 3.4.8.e and 3.4.8.f and 3.4.8.j
                manager.setGenerationEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), connection.getConnectionDetails());
                periodicReportingManager.refresh(objIdToBeEnabled.get(index));
            }
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public void setValue(final LongList paramDefInstId, final ParameterValueList newValues,
            final MALInteraction interaction) throws MALException, MALInteractionException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        Long objId;
        ParameterValue newValue;
        ParameterDefinitionDetails pDef;

        if (null == paramDefInstId || null == newValues) // Are the inputs null?
        {
            throw new IllegalArgumentException("paramDefInstId and newValues inputs must not be null");
        }

        for (int index = 0; index < paramDefInstId.size(); index++) {
            objId = paramDefInstId.get(index);

            if (!manager.exists(objId)) {
                unkIndexList.add(new UInteger(index));
                break;
            }

            newValue = newValues.get(index);
            pDef = manager.get(objId);

            if (pDef == null) { // The definition does not exist...
                unkIndexList.add(new UInteger(index));
            }

            /*
             if (pDef.getRawType() == newValue.getRawValue()) // requirement: 3.3.9.2.f
             invIndexList.add(new UInteger(index)); 
             */
        }

        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.3.9.3.1 (error: a and b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) // requirement: 3.3.9.3.2 
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        manager.setValues(paramDefInstId, newValues);

    }

    @Override
    public LongList listDefinition(final IdentifierList lIdentifier, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.3.8.2.1
        LongList outLongLst = new LongList();

        if (null == lIdentifier) { // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        for (Identifier tempIdentifier : lIdentifier) {
            // Check for wildcards
            if (tempIdentifier.toString().equals("*")) {  // requirement: 3.3.8.2.2
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            outLongLst.add(manager.list(tempIdentifier));  // requirement: 3.3.8.2.3
        }

        // Errors
        // The operation does not return any errors.
        return outLongLst;
    }

    @Override
    public LongList addDefinition(final ParameterDefinitionDetailsList lParameterDefinitionList,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        LongList outLongLst = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();
        ParameterDefinitionDetails tempParameterDefinition;

        if (null == lParameterDefinitionList) // Is the input null?
        {
            throw new IllegalArgumentException("ParameterDefinitionList argument must not be null");
        }

        for (int index = 0; index < lParameterDefinitionList.size(); index++) { // requirement: 3.3.9.2.5 (incremental "for cycle" guarantees that)
            tempParameterDefinition = lParameterDefinitionList.get(index);

            // Check if the name field of the ParameterDefinition is invalid.
            if (tempParameterDefinition.getName() == null
                    || tempParameterDefinition.getName().equals(new Identifier("*"))
                    || tempParameterDefinition.getName().equals(new Identifier(""))) { // requirement: 3.3.9.2.2
                invIndexList.add(new UInteger(index));
            }

            if (manager.list(tempParameterDefinition.getName()) == null) { // Is the supplied name unique? requirement: 3.3.9.2.3
                ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.3.4.d
                outLongLst.add(manager.add(tempParameterDefinition, source, connection.getConnectionDetails())); //  requirement: 3.3.9.2.4
            } else {
                dupIndexList.add(new UInteger(index));
            }
        }

        periodicReportingManager.refreshList(outLongLst); // Refresh the Periodic Reporting Manager for the added Definitions

        // Errors
        if (!invIndexList.isEmpty()) // requirement: 3.3.9.2.2
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!dupIndexList.isEmpty()) // requirement: 3.3.9.2.3
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

        return outLongLst; // requirement: 3.3.9.2.4
    }

    @Override
    public void updateDefinition(LongList paramDefInstIds, ParameterDefinitionDetailsList paramDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException { // requirement: 3.3.10.2.1
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        ParameterDefinitionDetails oldParameterDefinitionDetails;
        LongList newParameterDefInstIds = new LongList();
        ParameterDefinitionDetailsList newParameterDefDetails = new ParameterDefinitionDetailsList();

        if (null == paramDefInstIds || null == paramDefDetails) // Are the inputs null?
        {
            throw new IllegalArgumentException("paramDefInstIds and paramDefDetails arguments must not be null");
        }

        for (int index = 0; index < paramDefInstIds.size(); index++) {
            oldParameterDefinitionDetails = manager.get(paramDefInstIds.get(index));

            if (oldParameterDefinitionDetails == null) { // The object instance identifier could not be found? // requirement: 3.3.10.2.5
                unkIndexList.add(new UInteger(index));
                continue;
            }

            if (paramDefDetails.get(index).getName().equals(oldParameterDefinitionDetails.getName())) { // Are the names equal? requirement: 3.3.10.2.6
                newParameterDefInstIds.add(paramDefInstIds.get(index));
                newParameterDefDetails.add(paramDefDetails.get(index));
            } else {
                invIndexList.add(new UInteger(index));
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) // requirement: 3.3.10.2.6 (error: a)
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) // requirement: 3.3.10.2.5 (error: b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        for (int index = 0; index < newParameterDefInstIds.size(); index++) {
            manager.update(newParameterDefInstIds.get(index), newParameterDefDetails.get(index), connection.getConnectionDetails());  // Change in the manager
            periodicReportingManager.refresh(newParameterDefInstIds.get(index));// then, refresh the Periodic updates
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public void removeDefinition(final LongList lLongList, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.3.11.2.1
        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == lLongList) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < lLongList.size(); index++) {
            tempLong = lLongList.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.3.11.2.2
                tempLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            if (manager.exists(tempLong)) { // Does it match an existing definition? requirement: 3.3.11.2.3
                tempLongLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.3.11.2.3
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.3.11.2.3 (error: a, b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.3.11.2.5 (Inserting the errors before this line guarantees that the requirement is met)
        for (Long tempLong2 : tempLongLst) {
            manager.delete(tempLong2);
        }

        periodicReportingManager.refreshList(tempLongLst); // Refresh the Periodic Reporting Manager for the removed Definitions
        // COM archive is left untouched. requirement: 3.3.11.2.4

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public COMService getCOMService() {
        return ParameterHelper.PARAMETER_SERVICE;
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.INFO, "Registration Ack: {0}", header.toString());
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    private class PeriodicReportingManager { // requirement: 3.3.2.1a

        private final HashMap<Long, Timer> timerList; // Timers list
        boolean active = false; // Flag that determines if the Manager publishes or not

        public PeriodicReportingManager() {
            timerList = new HashMap<Long, Timer>();
        }

        public void start() {
            active = true;
        }

        public void pause() {
            active = false;
        }

        public void init() {   // Refresh all the Parameter Definitions on the Manager
            this.refreshList(manager.listAll());
            active = true; // set active flag to true
        }

        public void refresh(Long objId) {
            // get parameter definition
            ParameterDefinitionDetails PDef = manager.get(objId);

            if (timerList.containsKey(objId)) { // Does it exist in the Periodic Manager?
                this.removePeriodicReporting(objId);
            }

            if (PDef != null) { // Does it exist in the Aggregation Definitions List?
                if (PDef.getUpdateInterval().getValue() != 0 && PDef.getGenerationEnabled()) { // Is the periodic reporting active?
                    this.addPeriodicReporting(objId);
                }
            }
        }

        public void refreshList(LongList objIds) {
            if (objIds == null) {
                return;
            }
            for (Long objId : objIds) {
                refresh(objId);
            }
        }

        public void refreshAll() {
            this.refreshList(manager.listAll());
        }

        private void addPeriodicReporting(Long objId) {
            Timer timer = new Timer();
            timerList.put(objId, timer);
            startTimer(objId, manager.get(objId).getUpdateInterval());
        }

        private void removePeriodicReporting(Long objId) {
            this.stopTimer(objId);
            timerList.remove(objId);
        }

        private void startTimer(final Long objId, final Duration interval) {  // requirement: 3.3.2.11
            timerList.get(objId).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (active) {
                        if (objId == -1) {
                            return;
                        }
                        if (manager.get(objId).getGenerationEnabled()) {
                            // The Archive is getting swamped if we store all the pushed Parameters
//                            publishParameterUpdate(objId, false);
                            publishParameterUpdate(objId, true);
                        }
                    }
                }
            }, 0, (int) (interval.getValue() * 1000)); // the time has to be converted to milliseconds by multiplying by 1000
        }

        private void stopTimer(final Long objId) {
            timerList.get(objId).cancel();
        }

    }

    /**
     *
     * The pushSingleParameterValueAttribute operation allows an external entity
     * to push Attribute values through the monitorValue operation of the
     * Parameter service. If there is no parameter definition with the submitted
     * name, the method shall automatically create the parameter definition in
     * the Parameter service.
     *
     * @param name The name of the Parameter as set in the parameter definition
     * @param value The value of the parameter to be pushed
     * @param source The source of the parameter. Can be null
     * @param timestamp The timestamp of the parameter. If null, the method will
     * automatically use the System's time
     * @return Returns true if the push was successful. False otherwise. Please
     * notice that if no consumers are registered on the broker, then the value
     * of true will be returned because not error happened.
     */
    public Boolean pushSingleParameterValueAttribute(final Identifier name, final Attribute value, final ObjectId source, final Time timestamp) {

        final ParameterValue parameterValue = new ParameterValue();
        parameterValue.setRawValue(value);
        parameterValue.setConvertedValue(null);
        parameterValue.setValid(true);
        parameterValue.setInvalidSubState(new UOctet((short) 0));

        return this.pushParameterValue(name, parameterValue, source, timestamp);
    }

    /**
     *
     * The pushParameterValue operation allows an external entity to push
     * Parameter values through the monitorValue operation of the Parameter
     * service. If there is no parameter definition with the submitted name, the
     * method shall automatically create the parameter definition in the
     * Parameter service
     *
     * @param name The name of the Parameter as set in the parameter definition
     * @param parameterValue The parameter value to be pushed
     * @param source The source of the parameter. Can be null
     * @param timestamp The timestamp of the parameter. If null, the method will
     * automatically use the System's time
     * @return Returns true if the push was successful. False otherwise. Please
     * notice that if no consumers are registered on the broker, then the value
     * of true will be returned because not error happened.
     */
    public Boolean pushParameterValue(final Identifier name, final ParameterValue parameterValue, final ObjectId source, final Time timestamp) {

        try {
            synchronized(lock){
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Long objId = manager.list(name);  // Does the submitted name exists in the manager?

            if (objId == null) {

                ParameterDefinitionDetails pDef = new ParameterDefinitionDetails();

                pDef.setName(name);
                pDef.setDescription("This Parameter Definition was automatically generated by the Parameter service during the push of a parameter value.");

                if (parameterValue.getRawValue() == null) {  // Well, let's then consider that it is a Double
                    pDef.setRawType(Union.DOUBLE_TYPE_SHORT_FORM.byteValue());
                } else {
                    pDef.setRawType(parameterValue.getRawValue().getTypeShortForm().byteValue()); // Check what is the type and stamp it
                }

                pDef.setRawUnit(null);
                pDef.setGenerationEnabled(false);
                pDef.setUpdateInterval(new Duration(0));
                pDef.setValidityExpression(null);
                pDef.setConversion(null);

                ParameterDefinitionDetailsList pDefs = new ParameterDefinitionDetailsList();
                pDefs.add(pDef);

                try {
                    LongList returnedObjIds = this.addDefinition(pDefs, null);
                    objId = returnedObjIds.get(0);

                } catch (MALInteractionException ex) {
                    Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MALException ex) {
                    Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating Pushed Parameter update for the Parameter Definition: {0} (Identifier: {1})",
                    new Object[]{
                        objId, new Identifier(manager.get(objId).getName().toString())
                    });

            final Long pValObjId = manager.storeAndGeneratePValobjId(parameterValue, objId, connection.getConnectionDetails()); // requirement: 3.3.4.2

            //  requirements: 3.3.5.2.1 , 3.3.5.2.2 , 3.3.5.2.3 , 3.3.5.2.4
            final EntityKey ekey = new EntityKey(new Identifier(manager.get(objId).getName().toString()), objId, pValObjId, null);

            Time time = timestamp;

            if (time == null) {
                time = HelperTime.getTimestampMillis(); //  requirement: 3.3.5.2.5
            }

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectIdList objectIdlst = new ObjectIdList();
            final ParameterValueList pVallst = new ParameterValueList();

            hdrlst.add(new UpdateHeader(time, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
            objectIdlst.add(source); // requirement: 3.3.5.2.7 (3.3.5.2.6 not necessary)
            pVallst.add(parameterValue); // requirement: 3.3.5.2.8

            publisher.publish(hdrlst, objectIdlst, pVallst);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        }

        return true;
    }

    /**
     *
     * The pushParameterValue operation allows an external entity to push
     * Parameter values through the monitorValue operation of the Parameter
     * service. If there is no parameter definition with the submitted name, the
     * method shall automatically create the parameter definition in the
     * Parameter service
     *
     * @param parameters
     * @return Returns true if the push was successful. False otherwise. Please
     * notice that if no consumers are registered on the broker, then the value
     * of true will be returned because not error happened.
     */
    public Boolean pushMultipleParameterValues(final List<ParameterInstance> parameters) {

        try {
            if (!isRegistered) {
                final EntityKeyList lst = new EntityKeyList();
                lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                publisher.register(lst, new PublishInteractionListener());

                isRegistered = true;
            }

            LongList objIds = new LongList();

            for (ParameterInstance parameter : parameters) {
                Long objId = manager.list(parameter.getName());  // Does the submitted name exists in the manager?

                if (objId == null) {

                    ParameterDefinitionDetails pDef = new ParameterDefinitionDetails();

                    pDef.setName(parameter.getName());
                    pDef.setDescription("This Parameter Definition was automatically generated by the Parameter service during the push of a parameter value.");

                    if (parameter.getParameterValue().getRawValue() == null) {  // Well, let's then consider that it is a Double
                        pDef.setRawType(Union.DOUBLE_TYPE_SHORT_FORM.byteValue());
                    } else {
                        pDef.setRawType(parameter.getParameterValue().getRawValue().getTypeShortForm().byteValue()); // Check what is the type and stamp it
                    }

                    pDef.setRawUnit(null);
                    pDef.setGenerationEnabled(false);
                    pDef.setUpdateInterval(new Duration(0));
                    pDef.setValidityExpression(null);
                    pDef.setConversion(null);

                    ParameterDefinitionDetailsList pDefs = new ParameterDefinitionDetailsList();
                    pDefs.add(pDef);

                    try {
                        LongList returnedObjIds = this.addDefinition(pDefs, null); // Enable the reporting for this Alert Definition
                        objId = returnedObjIds.get(0);

                    } catch (MALInteractionException ex) {
                        Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MALException ex) {
                        Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                objIds.add(objId);

            }

            /*            
             Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.FINER,
             "Generating Pushed Parameter update for the Parameter Definition: {0} (Identifier: {1})",
             new Object[]{
             objId, new Identifier(manager.get(objId).getName().toString())
             });
             */
            ParameterValueList parameterValueList = new ParameterValueList();
            for (ParameterInstance parameter : parameters) {
                parameterValueList.add(parameter.getParameterValue());
            }

            final LongList pValObjIds = manager.storeAndGenerateMultiplePValobjId(parameterValueList, objIds, connection.getConnectionDetails()); // requirement: 3.3.4.2

            for (int i = 0; i < pValObjIds.size(); i++) {

                //  requirements: 3.3.5.2.1 , 3.3.5.2.2 , 3.3.5.2.3 , 3.3.5.2.4
                final EntityKey ekey = new EntityKey(new Identifier(manager.get(objIds.get(i)).getName().toString()), objIds.get(i), pValObjIds.get(i), null);

                Time time = parameters.get(i).getTimestamp();

                if (time == null) {
                    time = HelperTime.getTimestampMillis(); //  requirement: 3.3.5.2.5
                }

                final UpdateHeaderList hdrlst = new UpdateHeaderList();
                final ObjectIdList objectIdlst = new ObjectIdList();
                final ParameterValueList pVallst = new ParameterValueList();

                hdrlst.add(new UpdateHeader(time, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
                objectIdlst.add(parameters.get(i).getSource()); // requirement: 3.3.5.2.7 (3.3.5.2.6 not necessary)
                pVallst.add(parameters.get(i).getParameterValue()); // requirement: 3.3.5.2.8

                publisher.publish(hdrlst, objectIdlst, pVallst);

            }

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        }

        return true;

    }

    @Override
    public void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override
    public Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails) {
        // Validate the returned configuration...
        if (configurationObjectDetails == null) {
            return false;
        }

        if (configurationObjectDetails.getConfigObjects() == null) {
            return false;
        }

        // Is the size 1?
        if (configurationObjectDetails.getConfigObjects().size() != 1) {  // 1 because we just have ParameterDefinitions as configuration objects in this service
            return false;
        }

        ConfigurationObjectSet confSet = configurationObjectDetails.getConfigObjects().get(0);

        // Confirm the objType
        if (!confSet.getObjType().equals(ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet.getDomain().equals(configuration.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if (confSet.getObjInstIds().isEmpty()) {
            manager.reconfigureDefinitions(new LongList(), new ParameterDefinitionDetailsList());   // Reconfigures the Manager
            periodicReportingManager.refreshAll();  // Refresh the reporting
            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        ParameterDefinitionDetailsList pDefs = (ParameterDefinitionDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE,
                configuration.getDomain(),
                confSet.getObjInstIds());

        manager.reconfigureDefinitions(confSet.getObjInstIds(), pDefs);   // Reconfigures the Manager
        periodicReportingManager.refreshAll();  // Refresh the reporting

        return true;

    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        HashMap<Long, Element> defObjs = manager.getCurrentDefinitionsConfiguration();

        ConfigurationObjectSet objsSet = new ConfigurationObjectSet();
        objsSet.setDomain(configuration.getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE);

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSet);

        // Needs the Common API here!
        ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(list);

        return set;
    }

}
