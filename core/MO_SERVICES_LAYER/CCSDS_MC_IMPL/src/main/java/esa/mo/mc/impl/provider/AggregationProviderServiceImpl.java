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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.reconfigurable.service.ConfigurationNotificationInterface;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import java.util.ArrayList;
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
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.body.GetValueResponse;
import org.ccsds.moims.mo.mc.aggregation.provider.AggregationInheritanceSkeleton;
import org.ccsds.moims.mo.mc.aggregation.provider.MonitorValuePublisher;
import org.ccsds.moims.mo.mc.aggregation.structures.*;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueList;
import org.ccsds.moims.mo.mc.aggregation.structures.GenerationMode;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;

/**
 *
 */
public class AggregationProviderServiceImpl extends AggregationInheritanceSkeleton implements ReconfigurableServiceImplInterface {

    private MALProvider aggregationServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private MonitorValuePublisher publisher;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private AggregationManager manager;
    private PeriodicReportingManager periodicReportingManager;
    private PeriodicSamplingManager periodicSamplingManager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private ConfigurationNotificationInterface configurationAdapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param parameterManager
     * @throws MALException On initialisation error.
     */
    public synchronized void init (COMServicesProvider comServices, ParameterManager parameterManager) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

            try {
                AggregationHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        publisher = createMonitorValuePublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                new Identifier("LIVE"),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != aggregationServiceProvider) {
            connection.closeAll();
        }

        aggregationServiceProvider = connection.startService(AggregationHelper.AGGREGATION_SERVICE_NAME.toString(), AggregationHelper.AGGREGATION_SERVICE, this);

        running = true;
        manager = new AggregationManager(comServices, parameterManager);
        periodicReportingManager = new PeriodicReportingManager();
        periodicSamplingManager = new PeriodicSamplingManager();
        periodicReportingManager.init(); // Initialize the Periodic Reporting Manager
        periodicSamplingManager.init(); // Initialize the Periodic Sampling Manager

        manager.setGenerationEnabledAll(false, connection.getConnectionDetails());
        initialiased = true;
        Logger.getLogger(AggregationProviderServiceImpl.class.getName()).info("Aggregation service READY");
    }

    public ConnectionProvider getConnectionProvider(){
        return this.connection;
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != aggregationServiceProvider) {
                aggregationServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter){
        this.configurationAdapter = configurationAdapter;
    }

    private void publishAggregationUpdate(final Long objId, final AggregationValue aVal) {
        try {
            synchronized(lock){
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating Aggregation update for the Aggregation Definition objId: {0} (Identifier: {1})",
                    new Object[]{
                        objId, new Identifier(manager.get(objId).getName().toString())
                    });

            final Long aValObjId = manager.storeAndGenerateAValobjId(aVal, objId, connection.getConnectionDetails());

            // requirements: 3.7.5.2.1 , 3.7.5.2.2 , 3.7.5.2.3 , 3.7.5.2.4
            final EntityKey ekey = new EntityKey(new Identifier(manager.get(objId).getName().toString()), objId, aValObjId, null);
            final Time timestamp = HelperTime.getTimestampMillis(); //  requirement: 3.7.5.2.5

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectIdList objectIdlst = new ObjectIdList();
            final AggregationValueList aValLst = new AggregationValueList();

            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
            objectIdlst.add(null); // requirement: 3.7.5.2.7 (3.7.5.2.6 not necessary)
            aValLst.add(aVal);

            publisher.publish(hdrlst, objectIdlst, aValLst); // requirement: 3.7.2.15

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public GetValueResponse getValue(final LongList lLongList, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement 3.7.6.2.1

        LongList outLongLst = new LongList();
        UIntegerList unkIndexList = new UIntegerList();
        AggregationValueList outAValLst = new AggregationValueList();
        AggregationDefinitionDetails tempPDef;
        Long tempLong;

        if (null == lLongList){ // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < lLongList.size(); index++) {
            tempLong = lLongList.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.7.6.2.2
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            tempPDef = manager.get(tempLong);

            if (tempPDef != null) { // Does the AggregationDefinition exist?
                outLongLst.add(tempLong); //yap
            } else {  // The requested aggregation is unknown
                unkIndexList.add(new UInteger(index)); // requirement: 3.7.6.2.3
            }
        }

        outAValLst.addAll(manager.getAggregationValuesList(outLongLst, GenerationMode.ADHOC, false));  // requirement: 3.7.6.2.4

        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.7.6.2.3 (error: a, b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        return new GetValueResponse(outLongLst, outAValLst);
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
                periodicSamplingManager.refreshAll();
                foundWildcard = true;

                if (configurationAdapter != null){
                    configurationAdapter.configurationChanged(this);
                }                
                
                break;
            }
        }

        if (!foundWildcard) { // requirement: 3.4.8.2.d
            for (int index = 0; index < enableInstances.size(); index++) {
                enableInstance = enableInstances.get(index);

                if (isGroupIds) {
                    GroupDetails group = groupService.retrieveGroupDetailsFromArchive(
                            ConfigurationProviderSingleton.getDomain(), enableInstances.get(index).getId());

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

        if (!foundWildcard) {
            // requirement: 3.4.8.i (This part of the code is not reached if an error is thrown)
            for (int index = 0; index < objIdToBeEnabled.size() ; index++){
             // requirement: 3.4.8.e and 3.4.8.f and 3.4.8.j
                manager.setGenerationEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), connection.getConnectionDetails());
                periodicReportingManager.refresh(objIdToBeEnabled.get(index));
                periodicSamplingManager.refresh(objIdToBeEnabled.get(index));
            }
        }
        
        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }
        
    }

    @Override
    public void enableFilter(final Boolean lBoolean, final InstanceBooleanPairList lInstanceBooleanPairList,
            final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.7.8.2.1
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        InstanceBooleanPair tempBPair;

        if (null == lBoolean || null == lInstanceBooleanPairList) // Are the inputs null?
        {
            throw new IllegalArgumentException("Boolean and InstanceBooleanPairList arguments must not be null");
        }

        if (lBoolean) // Are the objId group identifiers?
        {
            throw new IllegalArgumentException("The MO M&C Group Service was not implemented. Group object instance identifiers cannot be used!");
        }

        for (int index = 0; index < lInstanceBooleanPairList.size(); index++) {
            tempBPair = lInstanceBooleanPairList.get(index);

            if (tempBPair.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.7.8.2.4
                manager.setFilterEnabledAll(tempBPair.getValue());
                periodicReportingManager.refreshAll();
                periodicSamplingManager.refreshAll();

                if (configurationAdapter != null){
                    configurationAdapter.configurationChanged(this);
                }
                
                break;
            }

            if (lBoolean) {
            // Insert code here to make the Group service work with the enableGeneration operation
                //    if (!groupService.exists(tempBPair.getId()))  // does it exist? 
                invIndexList.add(new UInteger(index)); // requirement: 3.7.8.2.7 (incomplete: group service not available)

            } else { // requirement: 3.7.8.2.8 (is respected because no error is generated if it is already enabled)
                manager.setFilterEnabled(tempBPair.getId(), tempBPair.getValue()); // requirement: 3.7.8.2.5
                periodicReportingManager.refresh(tempBPair.getId());
                periodicSamplingManager.refresh(tempBPair.getId());

                if (!manager.exists(tempBPair.getId())) // does it exist? 
                {
                    unkIndexList.add(new UInteger(index)); // requirement: 3.7.8.2.6 (incomplete: group service not available)
                }
            }
        }
        
        // The Aggregation Definition is not updated on the COM archive (should requirement). // requirement: 3.7.8.2.9
        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.7.8.2.6 (error: a)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) // requirement: 3.7.8.2.7(incomplete: group service not available) (error: b)
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }


        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }
    }

    @Override
    public LongList listDefinition(final IdentifierList lIdentifier, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.7.9.2.1
        LongList outLongLst = new LongList();
        Long tempLong;

        if (null == lIdentifier){ // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        for (Identifier tempIdentifier : lIdentifier) {
            // Check for the wildcard
            if (tempIdentifier.toString().equals("*")) {  // requirement: 3.7.9.2.2
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }
            
            outLongLst.add(manager.list(tempIdentifier));  // requirement: 3.7.9.2.3
        }

    // Errors
        // The operation does not return any errors.
        return outLongLst;
    }

    @Override
    public LongList addDefinition(final AggregationDefinitionDetailsList lAggregationDefinitionDetailsList,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        LongList outLongLst = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();
        AggregationDefinitionDetails tempAggregationDefinitionDetails;

        if (null == lAggregationDefinitionDetailsList) // Is the input null?
        {
            throw new IllegalArgumentException("AggregationDefinitionList argument must not be null");
        }

        for (int index = 0; index < lAggregationDefinitionDetailsList.size(); index++) { // requirement: 3.7.10.2.5 (incremental "for cycle" guarantees that)
            tempAggregationDefinitionDetails = lAggregationDefinitionDetailsList.get(index);

            // Check if the name field of the AggregationDefinition is invalid.
            if (tempAggregationDefinitionDetails.getName() == null
                    || tempAggregationDefinitionDetails.getName().equals(new Identifier("*"))
                    || tempAggregationDefinitionDetails.getName().equals(new Identifier(""))) { // requirement: 3.7.10.2.2
                invIndexList.add(new UInteger(index));
            }

            if (manager.list(tempAggregationDefinitionDetails.getName()) == null) { // Is the supplied name unique? requirement: 3.7.10.2.3
                ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.7.4.d
                outLongLst.add(manager.add(tempAggregationDefinitionDetails, source, connection.getConnectionDetails())); //  requirement: 3.7.10.2.4
            } else {
                dupIndexList.add(new UInteger(index));
            }
        }

        periodicReportingManager.refreshList(outLongLst); // Refresh the Periodic Reporting Manager for the added Definitions
        periodicSamplingManager.refreshList(outLongLst); // Refresh the Periodic Sampling Manager for the added Definitions


        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }
        
        // Errors
        if (!invIndexList.isEmpty()) // requirement: 3.7.10.2.2
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!dupIndexList.isEmpty()) // requirement: 3.7.10.2.3
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        return outLongLst; // requirement: 3.7.10.2.4
    }

    @Override
    public void updateDefinition(LongList aggDefInstIds, AggregationDefinitionDetailsList aggDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException { // requirement: 3.7.11.2.1, 3.7.11.2.2, 3.7.11.2.3
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        AggregationDefinitionDetails oldAggregationDefinitionDetails;
        LongList newAggregationDefInstIds = new LongList();
        AggregationDefinitionDetailsList newAggregationDefDetails = new AggregationDefinitionDetailsList();

        if (null == aggDefDetails || null == aggDefInstIds) // Are the inputs null?
        {
            throw new IllegalArgumentException("aggDefInstIds and aggDefDetails arguments must not be null");
        }

        for (int index = 0; index < aggDefInstIds.size(); index++) {
            oldAggregationDefinitionDetails = manager.get(aggDefInstIds.get(index));

            if (oldAggregationDefinitionDetails == null) { // The object instance identifier could not be found? // requirement: 3.7.11.2.5
                unkIndexList.add(new UInteger(index));
                continue;
            }

            if (aggDefDetails.get(index).getName().equals(oldAggregationDefinitionDetails.getName())) { // Are the names equal? requirement: 3.7.11.2.6
                newAggregationDefInstIds.add(aggDefInstIds.get(index));
                newAggregationDefDetails.add(aggDefDetails.get(index));
            } else {
                invIndexList.add(new UInteger(index));
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.7.11.2.5 (error: a)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) // requirement: 3.7.11.2.6 (error: b)
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        for (int index = 0; index < newAggregationDefInstIds.size(); index++) {
            manager.update(newAggregationDefInstIds.get(index), newAggregationDefDetails.get(index), connection.getConnectionDetails());  // Change in the manager
            periodicReportingManager.refresh(newAggregationDefInstIds.get(index));// then, refresh the Periodic updates and samplings
            periodicSamplingManager.refresh(newAggregationDefInstIds.get(index));
        }

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }
    }

    @Override
    public void removeDefinition(final LongList lLongList, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.7.12.2.1
        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == lLongList) // Is the input null?
        {
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < lLongList.size(); index++) {
            tempLong = lLongList.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.7.12.2.2
                tempLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            if (manager.exists(tempLong)) { // Does it match an existing definition? requirement: 3.7.12.2.3
                tempLongLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.7.12.2.3
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.7.12.2.3 (error: a, b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.7.12.2.5 (Inserting the errors before this line guarantees that the requirement is met)
        for (Long tempLong2 : tempLongLst) {
            manager.delete(tempLong2);
        }

        periodicReportingManager.refreshList(tempLongLst); // Refresh the Periodic Reporting Manager for the removed Definitions
        periodicSamplingManager.refreshList(tempLongLst); // Refresh the Periodic Sampling Manager for the removed Definitions
        // COM archive is left untouched. requirement: 3.7.12.2.4


        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }
        
    }
    
    /**
     *
     * The pushAggregationSetValue operation allows an external
     * entity to push Aggregation values through the monitorValue operation of
     * the Aggregation service. If there is no aggregation definition with the
     * submitted name, the method shall automatically create the aggregation
     * definition in the Aggregation service service.
     *
     * @param objId The name of the Aggregation as set in the aggregation definition
     * @param aSetVal The list of aggregation set values to be pushed
     * @param source The source of the aggregation. Can be null
     * @param timestamp The timestamp of the aggregation. If null, the method
     * will automatically use the System's time
     * @return Returns true if the push was successful. False otherwise.
     */
    public Boolean pushAggregationSetValue(final Long objId, final AggregationSetValueList aSetVal, final ObjectId source, final Time timestamp) {
        AggregationValue aVal = new AggregationValue();
        aVal.setGenerationMode(GenerationMode.ADHOC);
        aVal.setFiltered(false);
        aVal.setParameterSetValues(aSetVal);
        
        return this.pushAggregationValue(objId, aVal, source, timestamp);
    }
    
    /**
     *
     * The pushAggregationValue operation allows an external entity to push
     * Aggregation values through the monitorValue operation of the Aggregation
     * service. 
     *
     * @param objId The name of the Aggregation as set in the aggregation definition
     * @param aVal The aggregation value to be pushed
     * @param source The source of the aggregation. Can be null
     * @param timestamp The timestamp of the aggregation. If null, the method
     * will automatically use the System's time
     * @return Returns true if the push was successful. False otherwise.
     */
    public Boolean pushAggregationValue(final Long objId, final AggregationValue aVal, final ObjectId source, final Time timestamp) {
        try {
            if (!isRegistered) {
                final EntityKeyList lst = new EntityKeyList();
                lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                publisher.register(lst, new PublishInteractionListener());
                isRegistered = true;
            }

            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating Aggregation update for the Aggregation Definition objId: {0} (Identifier: {1})",
                    new Object[]{
                        objId, new Identifier(manager.get(objId).getName().toString())
                    });

            final Long aValObjId = manager.storeAndGenerateAValobjId(aVal, objId, connection.getConnectionDetails());

            // requirements: 3.7.5.2.1 , 3.7.5.2.2 , 3.7.5.2.3 , 3.7.5.2.4
            final EntityKey ekey = new EntityKey(new Identifier(manager.get(objId).getName().toString()), objId, aValObjId, null);
            Time time = timestamp;
            
            if (time == null){
                time = HelperTime.getTimestampMillis(); //  requirement: 3.3.5.2.5
            }
            
            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectIdList objectIdlst = new ObjectIdList();
            final AggregationValueList aValLst = new AggregationValueList();

            hdrlst.add(new UpdateHeader(time, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
            objectIdlst.add(source); // requirement: 3.7.5.2.7 (3.7.5.2.6 not necessary)
            aValLst.add(aVal);
            
            publisher.publish(hdrlst, objectIdlst, aValLst); // requirement: 3.7.2.15
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
            return false;
        } catch (MALException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
            return false;
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
            return false;
        }

        return true;
    }

    @Override
    public COMService getCOMService() {
        return AggregationHelper.AGGREGATION_SERVICE;
    }
    
    private static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterErrorReceived");
        }
    }

    private class PeriodicReportingManager { // requirement: 3.7.2.1a

        private final HashMap<Long, Timer> updateTimerList; // updateInterval Timers list
        private final HashMap<Long, Timer> filterTimeoutTimerList; // filterTimeout Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicReportingManager() {
            updateTimerList = new HashMap<Long, Timer>();
            filterTimeoutTimerList = new HashMap<Long, Timer>();
        }

        public void refreshAll() {
            this.refreshList(manager.listAll());
        }

        public void pause() {
            active = false;
        }

        public void start() {
            active = true;
        }

        public void init() {
            this.refreshAll(); // Refresh all the Aggregation Definitions on the Manager
            this.start(); // set active flag to true
        }

        public void refresh(Long objId) {
            // get Aggregation Definition
            AggregationDefinitionDetails ADef = manager.get(objId);

            if (updateTimerList.containsKey(objId)){ // Does it exist in the Periodic Reporting Manager?
                this.removePeriodicReporting(objId);
            }

            if (ADef != null) { // Does it exist in the Aggregation Definitions List?
                if (ADef.getUpdateInterval().getValue() != 0 && ADef.getGenerationEnabled()){ // Is the periodic reporting active? (requirement 3.7.2.12)
                    this.addPeriodicReporting(objId);  // requirement: 3.7.2.10
                }
            }

            manager.resetPeriodicAggregationValues(objId); // Reset the Sampling Values
        }

        public void refreshList(LongList objIds) {
            if (objIds == null) {
                return;
            }
            for (Long objId : objIds) {
                this.refresh(objId);
            }
        }

        private void addPeriodicReporting(Long objId) {
            Timer timer = new Timer();
            updateTimerList.put(objId, timer);
            this.startUpdatesTimer(objId, manager.get(objId).getUpdateInterval());  // requirement 3.7.2.11

            // Is the filter enabled? If so, do we have a filter Timeout set?
            if (manager.get(objId).getFilterEnabled() && manager.get(objId).getFilteredTimeout().getValue() != 0) { // requirement 3.7.2.12
                Timer timer2 = new Timer();
                filterTimeoutTimerList.put(objId, timer2);
                this.startFilterTimeoutTimer(objId, manager.get(objId).getFilteredTimeout());
            } else {
                filterTimeoutTimerList.put(objId, null);
            }
        }

        private void removePeriodicReporting(Long objId) {
            this.stopUpdatesTimer(objId);
            this.stopFilterTimeoutTimer(objId);
            updateTimerList.remove(objId);
            filterTimeoutTimerList.remove(objId);

        }

        private void startUpdatesTimer(final Long objId, final Duration interval) {
            updateTimerList.get(objId).scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {  // requirement: 3.7.2.3
                    if (active) {
                        AggregationDefinitionDetails def = manager.get(objId);
                        if (def.getGenerationEnabled()) {  // requirement 3.7.2.9

                            if (!def.getFilterEnabled()) // The Filter is not enabled?
                            {
                                publishAggregationUpdate(objId, manager.getAggregationValue(objId, GenerationMode.PERIODIC, false));
                            }

                            if (def.getFilterEnabled() && manager.isFilterTriggered(objId) == true) { // The Filter is on and triggered? requirement: 3.7.2.6
                                publishAggregationUpdate(objId, manager.getAggregationValue(objId, GenerationMode.PERIODIC, true)); // requirement: 3.7.5.2.8
                                manager.setFilterTriggered(objId, false); // Reset the trigger
                                resetFilterTimeoutTimer(objId);        // Reset the timer
                            }

                            manager.resetPeriodicAggregationValues(objId); // Reset the Sampled Values

                        }
                    }
                } // the time is being converted to milliseconds by multiplying by 1000  (starting delay included)
            }, (int) (interval.getValue() * 1000), (int) (interval.getValue() * 1000)); // requirement: 3.7.2.3
        }

        private void stopUpdatesTimer(final Long objId) {
            updateTimerList.get(objId).cancel();
        }

        private void resetFilterTimeoutTimer(Long objId) {
            if (!updateTimerList.containsKey(objId)) {
                return;  // Get out if it didn't find the objId
            }
            if (filterTimeoutTimerList.get(objId) == null) {
                return;  // Get out if the timer was not set
            }
            this.stopFilterTimeoutTimer(objId);
            Timer timer2 = new Timer();
            filterTimeoutTimerList.put(objId, timer2);
            this.startFilterTimeoutTimer(objId, manager.get(objId).getFilteredTimeout());
        }

        private void startFilterTimeoutTimer(final Long objId, final Duration interval) {
            filterTimeoutTimerList.get(objId).scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {  // requirement: 3.7.2.5
                    if (active) {
                        if (manager.get(objId).getFilterEnabled() && manager.get(objId).getGenerationEnabled()){ // requirement 3.7.2.9
                            publishAggregationUpdate(objId, manager.getAggregationValue(objId, GenerationMode.FILTERED_TIMEOUT, true)); // requirement: 3.7.2.6
                        }
                    }
                } // the time is being converted to milliseconds by multiplying by 1000
            }, (int) (interval.getValue() * 1000), (int) (interval.getValue() * 1000));
        }

        private void stopFilterTimeoutTimer(final Long objId) {
            if (filterTimeoutTimerList.get(objId) != null){ // Does it exist?
                filterTimeoutTimerList.get(objId).cancel();
            }
        }

    }

    private class PeriodicSamplingManager { // requirement: 3.7.2.1a

        private List<Timer> sampleTimerList; // Timers list
        private LongList aggregationObjId; // Corresponding object instance identifier of the above aggregation reference 
        private List<Integer> parameterSetIndex; // Corresponding Parameter set Index
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicSamplingManager() {
            sampleTimerList = new ArrayList<Timer>();
            aggregationObjId = new LongList();
            parameterSetIndex = new ArrayList<Integer>();
        }

        public void refreshAll() {
            this.refreshList(manager.listAll());
        }

        public void pause() {
            active = false;
        }

        public void start() {
            active = true;
        }

        public void init() {
            this.refreshAll(); // Refresh all the Aggregation Definitions on the Manager
            this.start(); // set active flag to true
        }

        public void refresh(Long objId) {
            final int index = aggregationObjId.indexOf(objId);

            if (index != -1){ // Does it exist in the PeriodicSamplingManager?
                this.removePeriodicSampling(objId);
            }

            if (manager.exists(objId)){ // Does it exist in the Aggregation Definitions List?
                this.addPeriodicSampling(objId);
            }

            manager.resetPeriodicAggregationValues(objId); // Reset the Sampled Values
        }

        public void refreshList(LongList objIds) {
            if (objIds == null) {
                return;
            }
            
            for (Long objId : objIds) {
                refresh(objId);
            }
        }

        private void addPeriodicSampling(Long objId) {
            if (!manager.get(objId).getGenerationEnabled()) {
                return; // Periodic Sampling shall not occur if the generation is not enabled at the definition level
            }
            final int parameterSetsTotal = manager.get(objId).getParameterSets().size();
            int index = sampleTimerList.size();

            for (int indexOfParameterSet = 0; indexOfParameterSet < parameterSetsTotal; indexOfParameterSet++) {
                Duration sampleInterval = manager.get(objId).getParameterSets().get(indexOfParameterSet).getSampleInterval();

                if (sampleInterval.getValue() > manager.get(objId).getUpdateInterval().getValue()) {
                    sampleInterval = new Duration(0);
                }

                // Add to the Periodic Sampling Manager only if there's a sampleInterval selected for the parameterSet
                if (sampleInterval.getValue() != 0) {
                    aggregationObjId.add(index, objId);
                    parameterSetIndex.add(index, indexOfParameterSet);
                    Timer timer = new Timer();  // Take care of adding a new timer
                    sampleTimerList.add(index, timer);
                    startTimer(index, sampleInterval);
                    index++;
                }
            }
        }

        private void removePeriodicSampling(Long objId) {
            for (int index = aggregationObjId.indexOf(objId); index != -1; index = aggregationObjId.indexOf(objId)) {
                this.stopTimer(index);
                aggregationObjId.remove(index);
                sampleTimerList.remove(index);
                parameterSetIndex.remove(index);
            }
        }

        private void startTimer(final int index, Duration interval) {  // requirement: 3.7.2.11
            final Long objId = aggregationObjId.get(index);
            final int IndexOfparameterSet = parameterSetIndex.get(index);

            sampleTimerList.get(index).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (active) {
                        final AggregationDefinitionDetails aggregationDefinition = manager.get(objId);
                        final AggregationParameterSet aggregationParameterSet = aggregationDefinition.getParameterSets().get(IndexOfparameterSet);

                        // Add another sample on the AggregationValue that will be returned later:
                        final ParameterValueList previousParameterValue = manager.getLastParameterValue(objId, IndexOfparameterSet);
                        final ParameterValueList currentParameterValue = manager.updateParameterValue(objId, IndexOfparameterSet);

                        if (previousParameterValue == null) // Is it the first value? 
                        {
                            return;   // Then, there's no need to check the filter
                        }
                        // Filter Comparison Process
                        if (aggregationDefinition.getFilterEnabled() && // Is the filter enabled? 
                                aggregationParameterSet.getPeriodicFilter() != null && // requirement: 3.7.2.7 (and 4.7.6: periodicFilter comment)
                                aggregationParameterSet.getParameters().size() == 1) {  // Are we sampling one parameter? requirement: 3.7.2.8

                        // In theory all the list should be null with the exception of the Parameter Value we want
                            // because size = 1 and the remaining Parameter Values inside get the null state
                            // So we can crawl the list until we find the first non-null element and compare it with the previousParameterValue
                            for (int i = 0; i < currentParameterValue.size(); i++) {
                                ParameterValue current = currentParameterValue.get(i);
                                ParameterValue previous = previousParameterValue.get(i);

                                if (current != null && previous != null) {
                                    // Compare the values:
                                    if ((current.getIsValid() && previous.getIsValid()) || // Are the parameters valid?
                                            (current.getInvalidSubState().getValue() == 2 && previous.getInvalidSubState().getValue() == 2)) { // 2 stands for the INVALID_RAW state

                                        Boolean filterisTriggered = false;
                                        if (current.getIsValid() && previous.getIsValid()
                                                && current.getConvertedValue() != null && previous.getConvertedValue() != null) // requirement: 3.7.2.6
                                        {
                                            filterisTriggered = manager.triggeredFilter(current.getConvertedValue(), previous.getConvertedValue(), aggregationParameterSet.getPeriodicFilter());
                                        }

                                        if (current.getConvertedValue() == null && previous.getConvertedValue() == null) // requirement: 3.7.2.6
                                        {
                                            filterisTriggered = manager.triggeredFilter(current.getRawValue(), previous.getRawValue(), aggregationParameterSet.getPeriodicFilter());
                                        }

                                        if (filterisTriggered) {
                                            manager.setFilterTriggered(objId, true);
                                        }
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }
            }, 0, (int) (interval.getValue() * 1000)); // the time has to be converted to milliseconds by multiplying by 1000
        }

        private void stopTimer(int index) {
            sampleTimerList.get(index).cancel();
        }

    }

    @Override
    public Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails) {
        // Validate the returned configuration...
        if(configurationObjectDetails == null){
            return false;
        }

        if(configurationObjectDetails.getConfigObjects() == null){
            return false;
        }

        // Is the size 1?
        if (configurationObjectDetails.getConfigObjects().size() != 1) {  // 1 because we just have ParameterDefinitions as configuration objects in this service
            return false;
        }

        ConfigurationObjectSet confSet = configurationObjectDetails.getConfigObjects().get(0);

        // Confirm the objType
        if (!confSet.getObjType().equals(AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet.getDomain().equals(connection.getConnectionDetails().getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if(confSet.getObjInstIds().isEmpty()){
            manager.reconfigureDefinitions(new LongList(), new AggregationDefinitionDetailsList());   // Reconfigures the Manager
            periodicReportingManager.refreshAll();  // Refresh the reporting
            periodicSamplingManager.refreshAll();
            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        AggregationDefinitionDetailsList pDefs = (AggregationDefinitionDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                connection.getConnectionDetails().getDomain(),
                confSet.getObjInstIds());

        manager.reconfigureDefinitions(confSet.getObjInstIds(), pDefs);   // Reconfigures the Manager
        manager.createAggregationValuesList(confSet.getObjInstIds());
        periodicReportingManager.refreshAll();  // Refresh the reporting
        periodicSamplingManager.refreshAll();

        return true;

    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        HashMap<Long, Element> defObjs = manager.getCurrentDefinitionsConfiguration();

        ConfigurationObjectSet objsSet = new ConfigurationObjectSet();
        objsSet.setDomain(connection.getConnectionDetails().getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE);

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSet);

        // Needs the Common API here!
        ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(list);

        return set;
    }
    
}
