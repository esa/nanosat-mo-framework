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
import esa.mo.mc.impl.provider.model.GroupRetrieval;
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
import org.ccsds.moims.mo.mc.aggregation.provider.AggregationInheritanceSkeleton;
import org.ccsds.moims.mo.mc.aggregation.provider.MonitorValuePublisher;
import org.ccsds.moims.mo.mc.aggregation.structures.*;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueList;
import org.ccsds.moims.mo.mc.aggregation.structures.GenerationMode;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 *
 */
public class AggregationProviderServiceImpl extends AggregationInheritanceSkeleton implements ReconfigurableServiceImplInterface {

    private final static double MIN_REPORTING_INTERVAL = 0.2;
    private MALProvider aggregationServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private MonitorValuePublisher publisher;
    private final Object lock = new Object();
    private boolean isRegistered = false;
    private AggregationManager manager;
    private PeriodicReportingManager periodicReportingManager;
    private PeriodicSamplingManager periodicSamplingManager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private ConfigurationNotificationInterface configurationAdapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param parameterManager
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, ParameterManager parameterManager) throws MALException {
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
                ConfigurationProviderSingleton.getSourceSessionName(),
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

        initialiased = true;
        Logger.getLogger(AggregationProviderServiceImpl.class.getName()).info("Aggregation service READY");
    }

    public ConnectionProvider getConnectionProvider() {
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
    public void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    /**
     * publishes, a periodic update, meaning without a sourceId and with a newly
     * created timestamp
     *
     * @param objId the identityId of the aggregation
     * @param aVal the value to be published
     * @return true if it was successfully published, false otherwise
     */
    private boolean publishPeriodicAggregationUpdate(final Long objId, final AggregationValue aVal) {
        return publishAggregationUpdate(objId, aVal, null, null); //requirement: 3.7.4.j
    }

    /**
     * publishes, an update with the given value, sourceId and timestamp
     *
     * @param identityId the identityId of the aggregation
     * @param aVal the value to be published
     * @param source the id of the object that caused the update to be generated
     * @param timestamp the timestamp of the update. if null a new timestamp
     * will be generated
     * @return true if it was successfully published, false otherwise
     */
    private boolean publishAggregationUpdate(final Long identityId, final AggregationValue aVal, final ObjectId source, final Time timestamp) {
        try {
            synchronized (lock) {
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.FINER,
                    "Generating Aggregation update for the Aggregation Identity objId: {0} (Identifier: {1})",
                    new Object[]{
                        identityId, new Identifier(manager.getName(identityId).toString())
                    });

            final Long definitionId = manager.getDefinitionId(identityId);
            //requirement 3.7.6.b
            final Long aValObjId = manager.storeAndGenerateAValobjId(aVal, definitionId, source, connection.getPrimaryConnectionDetails().getProviderURI());

            // requirements: 3.7.7.2.a , 3.7.7.2.b , 3.7.7.2.c , 3.7.7.2.d
            final EntityKey ekey = new EntityKey(new Identifier(manager.getName(identityId).toString()), identityId, definitionId, aValObjId);
            Time time = timestamp;

            if (time == null) {
                time = HelperTime.getTimestampMillis(); //  requirement: 3.7.7.2.e
            }

            final UpdateHeaderList hdrlst = new UpdateHeaderList(1);
            final ObjectIdList objectIdlst = new ObjectIdList(1);
            final AggregationValueList aValLst = new AggregationValueList(1);

            hdrlst.add(new UpdateHeader(time, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
            objectIdlst.add(source); // requirement: 3.7.7.2.f,g 
            aValLst.add(aVal); //requirement 3.7.7.2.h

            publisher.publish(hdrlst, objectIdlst, aValLst);
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

    /**
     * creates and publishes an aggregation-update. all parameter-sets of the
     * aggregation will be sampled for the first time, saved in the internal
     * list and published. As it is the first update-value, no filter must be
     * checked.
     *
     * @param identityId the id of the aggregations identity
     * @return
     */
    private void publishImmediatePeriodicUpdate(Long identityId) {
        //get the parameter-values of each parameter-set
        final AggregationDefinitionDetails aggrDefinition = manager.getAggregationDefinition(identityId);
        final AggregationParameterSetList parameterSets = aggrDefinition.getParameterSets();
        for (int i = 0; i < parameterSets.size(); i++) {
            manager.sampleParam(identityId, i);
        }
        //publish the parameter-values as an updated 
        publishPeriodicAggregationUpdate(identityId, manager.getAggregationValue(identityId, GenerationMode.PERIODIC));
        manager.resetAggregationSampleHelperVariables(identityId);
    }

    @Override
    public AggregationValueDetailsList getValue(final LongList inIdentityIds, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement 3.7.6.2.1
        UIntegerList unkIndexList = new UIntegerList();

        if (null == inIdentityIds) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < inIdentityIds.size(); index++) {
            Long tempIdentityId = inIdentityIds.get(index);

            if (tempIdentityId == 0) {  // Is it the wildcard '0'? requirement: 3.7.8.2.b
                inIdentityIds.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                inIdentityIds.addAll(manager.listAllIdentities()); // ... add all in a row
                //as it should be checked for a wildcard first, dont return found errors; requirement: 3.7.8.2.c
                unkIndexList.clear();
                break;
            }

            if (!manager.existsIdentity(tempIdentityId)) { // Is the requested aggregation unknown?
                unkIndexList.add(new UInteger(index)); // requirement: 3.7.8.2.d
                continue;
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.7.8.3.1 a, b
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.7.8.2.e
        AggregationValueDetailsList outList = new AggregationValueDetailsList(inIdentityIds.size());

        for (Long inIdentityId : inIdentityIds) {
            outList.add(new AggregationValueDetails(
                    inIdentityId,
                    manager.getDefinitionId(inIdentityId),
                    HelperTime.getTimestampMillis(),
                    manager.getValue(inIdentityId))
            );
        }

        return outList;
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

        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.7.4.h
        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.7.9.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.7.9.2.c
                manager.setGenerationEnabledAll(instance.getValue(), source, connection.getConnectionDetails());
                periodicReportingManager.refreshAll();
                periodicSamplingManager.refreshAll();
                foundWildcard = true;

                break;
            }
        }

        if (!foundWildcard) { // requirement: 3.7.9.2.d
            if (!isGroupIds) { //the Ids are aggregation-identity-ids requirement: 3.7.9.2.a 
                for (int index = 0; index < enableInstances.size(); index++) {
                    enableInstance = enableInstances.get(index);
                    objIdToBeEnabled.add(enableInstance.getId()); //requirement: 3.7.9.2.b
                    valueToBeEnabled.add(enableInstance.getValue());

                    if (!manager.existsIdentity(enableInstance.getId())) { // does it exist? 
                        unkIndexList.add(new UInteger(index)); // requirement: 3.7.9.2.g
                    }
                }
            } else { //the ids are group-identity-ids, req: 3.3.10.2.a, 3.9.4.g,h 
                GroupRetrieval groupRetrievalInformation;
                groupRetrievalInformation = new GroupRetrieval(unkIndexList, invIndexList, objIdToBeEnabled, valueToBeEnabled);
                //get the group instances requirements: 3.7.9.2.g, h
                groupRetrievalInformation = manager.getGroupInstancesForServiceOperation(enableInstances,
                        groupRetrievalInformation, AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(), manager.listAllIdentities());

                //fill the existing lists with the modified lists
                unkIndexList = groupRetrievalInformation.getUnkIndexList();
                invIndexList = groupRetrievalInformation.getInvIndexList();
                objIdToBeEnabled = groupRetrievalInformation.getObjIdToBeEnabled();
                valueToBeEnabled = groupRetrievalInformation.getValueToBeEnabled();
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.7.9.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!invIndexList.isEmpty()) { // requirement: 3.7.9.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        // requirement: 3.7.9.2.i (This part of the code is not reached if an error is thrown)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            // requirement: 3.7.3.c, 3.7.9.2.f and 3.7.9.2.j, k
            boolean changed = manager.setGenerationEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), source, connection.getConnectionDetails());
            //requirement: 3.7.9.2.e, l
            if (changed) {
                periodicReportingManager.refresh(objIdToBeEnabled.get(index));
                periodicSamplingManager.refresh(objIdToBeEnabled.get(index));
            }
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }
    }

    @Override
    public void enableFilter(final Boolean isGroupIds, final InstanceBooleanPairList enableInstances,
            final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.7.10.2.a
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        InstanceBooleanPair enableInstance;

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (null == isGroupIds || null == enableInstances) // Are the inputs null?
        {
            throw new IllegalArgumentException("Boolean and InstanceBooleanPairList arguments must not be null");
        }

        boolean foundWildcard = false;

        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.7.4.h
        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.7.10.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.7.10.2.c
                manager.setFilterEnabledAll(instance.getValue(), source, connection.getConnectionDetails());
                periodicReportingManager.refreshAll();
                periodicSamplingManager.refreshAll();
                foundWildcard = true;
                break;
            }
        }

        if (!foundWildcard) { // requirement: 3.7.10.2.d
            if (!isGroupIds) { //the Ids are aggregation-identity-ids requirement: 3.7.10.2.a 
                for (int index = 0; index < enableInstances.size(); index++) {
                    enableInstance = enableInstances.get(index);
                    objIdToBeEnabled.add(enableInstance.getId()); //requirement: 3.7.10.2.b
                    valueToBeEnabled.add(enableInstance.getValue());

                    if (!manager.existsIdentity(enableInstance.getId())) { // does it exist? 
                        unkIndexList.add(new UInteger(index)); // requirement: 3.7.10.2.g
                    }
                }
            } else {//the ids are group-definition-ids, req: 3.7.10.2.a, 3.9.4.g,h
                GroupRetrieval groupRetrievalInformation = new GroupRetrieval(unkIndexList,
                        invIndexList, objIdToBeEnabled, valueToBeEnabled);

                //get the group instances requirements: 3.7.10.2.g, h
                groupRetrievalInformation = manager.getGroupInstancesForServiceOperation(enableInstances,
                        groupRetrievalInformation, AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(), manager.listAllIdentities());

                //fill the existing lists with the modified lists
                unkIndexList = groupRetrievalInformation.getUnkIndexList();
                invIndexList = groupRetrievalInformation.getInvIndexList();
                objIdToBeEnabled = groupRetrievalInformation.getObjIdToBeEnabled();
                valueToBeEnabled = groupRetrievalInformation.getValueToBeEnabled();
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.7.10.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!invIndexList.isEmpty()) { // requirement: 3.7.10.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        // requirement: 3.7.10.2.i (This part of the code is not reached if an error is thrown)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            // requirement: 3.7.3.d, e, f; 3.7.10.2.f and 3.7.1.2.j, k
            boolean changed = manager.setFilterEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), source, connection.getConnectionDetails());
            //requirement: 3.7.10.2.e //periodic managers must be refreshed, as the change of the filterEnabled-value creates a new Definition object
            if (changed) {
                periodicReportingManager.refresh(objIdToBeEnabled.get(index));
                periodicSamplingManager.refresh(objIdToBeEnabled.get(index));
            }
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }
    }

    @Override
    public ObjectInstancePairList listDefinition(final IdentifierList nameList, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.7.9.2.1
        ObjectInstancePairList outLongLst = new ObjectInstancePairList();
        UIntegerList unkIndexList = new UIntegerList();

        if (null == nameList) { // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        for (int index = 0; index < nameList.size(); index++) { // requirement: 3.7.11.2.f  
            Identifier name = nameList.get(index);
            // Check for the wildcard: requirement: 3.7.11.2.c
            if (name.toString().equals("*")) {
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAllIdentityDefinitions()); // ... add all in a row
                //as it should be checked for wildcards first, ignore unknown names: requirement: 3.7.11.2.b,
                unkIndexList.clear();
                break;
            }
            //check if the given name exists
            final ObjectInstancePair idPair = manager.getIdentityDefinition(name);
            if (idPair == null) { // the id is unknown; requirement: 3.7.11.2.d  
                unkIndexList.add(new UInteger(index));
                continue;
            } else {
                outLongLst.add(idPair);  // requirement: 3.7.11.2.e  
            }
        }

        // Errors
        //if there is one name unknown, fail with an unknown error and dont return the found entries.
        if (!unkIndexList.isEmpty()) // requirement: 3.7.11.3.1
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        return outLongLst;
    }

    @Override
    public ObjectInstancePairList addAggregation(final AggregationCreationRequestList aggrCreationReqList,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        ObjectInstancePairList outPairLst = new ObjectInstancePairList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();

        if (null == aggrCreationReqList) // Is the input null?
        {
            throw new IllegalArgumentException("AggregationDefinitionList argument must not be null");
        }

        for (int index = 0; index < aggrCreationReqList.size(); index++) { // requirement: 3.7.10.2.5 (incremental "for cycle" guarantees that)
            AggregationCreationRequest aggrCreationReq = aggrCreationReqList.get(index);
            final Identifier aggrName = aggrCreationReq.getName();

            // Check if the name field of the AggregationDefinition is invalid.
            if (aggrName.equals(new Identifier("*"))
                    || aggrName.equals(new Identifier(""))) { // requirement: 3.7.10.2.2
                invIndexList.add(new UInteger(index));
                continue;
            }

            final AggregationDefinitionDetails aDef = aggrCreationReq.getAggDefDetails();
            final AggregationParameterSetList parameterSets = aDef.getParameterSets();

            //requirement: 3.7.10.2.c, 3.7.3.p requested intervals must be provided
            //updateInterval must be provided
            if (aDef.getReportInterval().getValue() != 0 && aDef.getReportInterval().getValue() < MIN_REPORTING_INTERVAL) {
                invIndexList.add(new UInteger(index));
                continue;
            }
            //requirement: 3.7.10.2.c, 3.7.3.p
            //sample-interval must be provided
            for (AggregationParameterSet parameterSet : parameterSets) {
                if (parameterSet.getSampleInterval().getValue() != 0 && parameterSet.getSampleInterval().getValue() < MIN_REPORTING_INTERVAL) {
                    invIndexList.add(new UInteger(index));
                    break;
                }
            }
            //requirement: 3.7.3.p
            //filteredTimeout-interval must be provided
            if (aDef.getFilteredTimeout().getValue() != 0 && aDef.getFilteredTimeout().getValue() < MIN_REPORTING_INTERVAL) {
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (manager.getIdentity(aggrName) != null) { // Is the supplied name already given? requirement: 3.7.12.2.d
                dupIndexList.add(new UInteger(index));
                continue;
            }
        }

        // requirement: 3.7.10.2.e is met because the errors will be thrown before something changes
        // Errors
        if (!invIndexList.isEmpty()) // requirement: 3.7.10.2.2
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!dupIndexList.isEmpty()) // requirement: 3.7.10.2.3
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        ObjectId source = manager.storeCOMOperationActivity(interaction); //requirement: 3.7.4.g, h
        for (AggregationCreationRequest aggrCreationRequest : aggrCreationReqList) { // requirement: 3.7.12.2.i ( "for each cycle" guarantees that)
            Identifier aggrName = aggrCreationRequest.getName();
            //requriement: 3.7.12.2.g , store the objects 
            outPairLst.add(manager.add(aggrName,
                    aggrCreationRequest.getAggDefDetails(),
                    source,
                    connection.getConnectionDetails())); //  requirement: 3.3.12.2.e
            periodicReportingManager.refresh(outPairLst.get(0).getObjIdentityInstanceId()); // Refresh the Periodic Reporting Manager for the added Identities
            periodicSamplingManager.refresh(outPairLst.get(0).getObjIdentityInstanceId()); // Refresh the Periodic Sampling Manager for the added Identities
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

        return outPairLst; // requirement: 3.7.12.2.h
    }

    @Override
    public LongList updateDefinition(LongList identityIds, AggregationDefinitionDetailsList aDefs,
            MALInteraction interaction) throws MALInteractionException, MALException { // requirement: 3.7.13.2.a, 3.7.13.2.d
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == aDefs || null == identityIds) // Are the inputs null?
        {
            throw new IllegalArgumentException("identityIds and aggDefDetails arguments must not be null");
        }

        for (int index = 0; index < identityIds.size(); index++) {
            final Long identity = identityIds.get(index);

            if (identity == null || identity == 0) { // requirement: 3.7.13.2.c
                invIndexList.add(new UInteger(index));
                continue;
            }
            if (!manager.existsIdentity(identity)) { // requirement: 3.7.13.2.b
                unkIndexList.add(new UInteger(index));
                continue;
            }

            final AggregationDefinitionDetails aDef = aDefs.get(index);
            final AggregationParameterSetList parameterSets = aDef.getParameterSets();
            //requirement: 3.7.3.p, 3.7.13.2.f
            //TODO: check the updateInterval, filteredTimeout and sampleIntervals? -> issue #152
            //updateInterval must be provided
            if (aDef.getReportInterval().getValue() != 0 && aDef.getReportInterval().getValue() < MIN_REPORTING_INTERVAL) {
                invIndexList.add(new UInteger(index));
                continue;
            }
            //requirement: 3.7.3.p, 3.7.13.2.f
            //sample-interval must be provided
            for (AggregationParameterSet parameterSet : parameterSets) {
                if (parameterSet.getSampleInterval().getValue() != 0 && parameterSet.getSampleInterval().getValue() < MIN_REPORTING_INTERVAL) {
                    invIndexList.add(new UInteger(index));
                    break;
                }
            }
            //requirement: 3.7.3.p
            //filteredTimeout-interval must be provided
            if (aDef.getFilteredTimeout().getValue() != 0 && aDef.getFilteredTimeout().getValue() < MIN_REPORTING_INTERVAL) {
                invIndexList.add(new UInteger(index));
                continue;
            }
        }

        // requirement: 3.7.13.2.g is met because errors will be thrown before changes are made
        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.7.13.3.1
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!invIndexList.isEmpty()) // requirement: 3.7.13.3.2
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        LongList newDefIds = new LongList();
        ObjectId source = manager.storeCOMOperationActivity(interaction); //requirement: 3.7.4.h

        for (int index = 0; index < identityIds.size(); index++) { // requirement: 3.7.13.2.e, k (implicitly by cycling through list)
            final Long identityId = identityIds.get(index);
            // requirement: 3.7.3.o, 3.7.13.2.d, h, i, k
            newDefIds.add(manager.update(identityId, aDefs.get(index), source, connection.getConnectionDetails()));  // update and return new id
            periodicReportingManager.refresh(identityId);// then, refresh the Periodic updates and samplings //requirement: 3.7.3.k
            periodicSamplingManager.refresh(identityId);//requirement: 3.7.3.k
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

        // requirement: 3.7.13.2.j
        return newDefIds;
    }

    @Override
    public void removeAggregation(final LongList identityIds,
            final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.7.12.2.1
        UIntegerList unkIndexList = new UIntegerList();
        Long identityId;
        LongList removalLst = new LongList();

        if (null == identityIds) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < identityIds.size(); index++) {
            identityId = identityIds.get(index);

            if (identityId == 0) {  // Is it the wildcard '0'? requirement: 3.3.14.2.b, c
                removalLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                removalLst.addAll(manager.listAllIdentities()); // ... add all in a row
                //as of requirement 3.3.14.2.c, the wildcards should be checked "first", no error will be returned then
                unkIndexList.clear();
                break;
            }

            if (!manager.existsIdentity(identityId)) { // Does it match an existing identity? requirement: 3.3.14.2.d
                unkIndexList.add(new UInteger(index));
            } else {
                removalLst.add(identityId);
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.3.14.3.1 (error: a, b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        // requirement: 3.7.14.2.f (Inserting the errors before this line guarantees that the requirement is met)
        for (Long removalItem : removalLst) {
            manager.delete(removalItem); // requirement: 3.7.14.2.e
        }
        //requirement: 3.7.14.2.g
        periodicReportingManager.refreshList(removalLst); // Refresh the Periodic Reporting Manager for the removed identities
        periodicSamplingManager.refreshList(removalLst); // Refresh the Periodic Sampling Manager for the removed identities
        // COM archive is left untouched. requirement: 3.7.14.2.e

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }
    }

    /**
     *
     * The pushAggregationAdhocUpdate operation allows an external entity to
     * trigger an adhoc aggregation-update.
     *
     * @param identityId The id of the Aggregation as set in the aggregation
     * definition
     * @param source The source of the aggregation. Can be null
     * @param timestamp The timestamp of the aggregation. If null, the method
     * will automatically use the System's time
     * @return Returns true if the push was successful. False otherwise.
     */
    public Boolean pushAggregationAdhocUpdate(Identifier name, final ObjectId source, final Time timestamp) { //requirement: 3.7.2.b.b, 3.7.4.i
        final Long identityId = manager.getIdentity(name);

        //check the filter and sample
        if (!checkFilterAndSampleParam(identityId, false)) {
            //filter didnt trigger
            return false;
        }
        //publish! requirement: 3.7.3.j
        if (!publishAggregationUpdate(identityId, manager.getAggregationValue(identityId, GenerationMode.ADHOC), source, timestamp)) {
            return false;
        }

        //if the push value was published during a periodic update reset the timers //not standarized. impl. specific
        if (manager.getAggregationDefinition(identityId).getReportInterval().getValue() != 0) {
            periodicReportingManager.refresh(identityId);// then, refresh the Periodic updates and samplings
            periodicSamplingManager.refresh(identityId);
        } else {
            manager.resetAggregationSampleHelperVariables(identityId);
        }
        return true;
    }

    /**
     *
     * The pushSingleParameterValueAttribute operation allows an external entity
     * to push Attribute values through the monitorValue operation of the
     * Aggregation service.
     *
     * @param identityId The id of the Aggregation as set in the aggregation
     * definition
     * @param aSetVal The list of aggregation set values to be pushed
     * @param source The source of the aggregation. Can be null
     * @param timestamp The timestamp of the aggregation. If null, the method
     * will automatically use the System's time
     * @return Returns true if the push was successful. False otherwise.
     */
    public Boolean pushAggregationSetValue(final Long identityId, final AggregationSetValueList aSetVal,
            final ObjectId source, final Time timestamp) { //requirement: 3.7.4.i

        //check that the given aggregationSetValueList has the right amount of entries
        final AggregationDefinitionDetails aggrDef = manager.getAggregationDefinition(identityId);
        if (aSetVal.size() != aggrDef.getParameterSets().size()) {
            return false;
        }
        //check that the generation of updates is enabled
        if (!aggrDef.getGenerationEnabled()) { //requirement 3.7.3.a, 3.7.3.b
            return false;
        }
        //check the filter and sample
        if (!checkFilterAndSampleParams(identityId, false, aSetVal)) {
            return false;
        }
        //publish! requirement: 3.7.3.j
        if (!publishAggregationUpdate(identityId, manager.getAggregationValue(identityId, GenerationMode.ADHOC), source, timestamp)) {
            return false;
        }

        //if the push value was published during a periodic update reset the timers //not standarized, impl. specific
        if (manager.getAggregationDefinition(identityId).getReportInterval().getValue() != 0) {
            periodicReportingManager.refresh(identityId);// then, refresh the Periodic updates and samplings
            periodicSamplingManager.refresh(identityId);
        } else {
            manager.resetAggregationSampleHelperVariables(identityId);
        }
        return true;
    }

    /**
     * checks if the filter is triggered and samples new parametervalues and
     * sets them to the internal list if it was triggered or if no filter is
     * enabled
     *
     * @param identityId
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter is periodic and the aggregation period is up.
     * @return true if the values were successfully saved to the internal list.
     * false if the filter is enabled but wasnt triggered with the new values.
     */
    private boolean checkFilterAndSampleParam(final Long identityId, boolean aggrExpired) {
        return checkFilterAndSampleParams(identityId, aggrExpired, null);
    }

    /**
     * gets new parameterValues, checks if the filter is triggered and sets
     * these to the internal list if the filter was triggered or if no filter is
     * enabled
     *
     * @param identityId
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter is periodic and the aggregation period is up.
     * @param aSetVal the new parameter values to be set
     * @return true if the values were successfully saved to the internal list.
     * false if the filter is enabled but wasnt triggered with the new values.
     */
    private boolean checkFilterAndSampleParams(final Long identityId, boolean aggrExpired, final AggregationSetValueList aSetVal) {
        //check if filter enabled
        AggregationDefinitionDetails aggrDef = manager.getAggregationDefinition(identityId);
        final AggregationParameterSetList parameterSets = aggrDef.getParameterSets();
        //requirement: 3.7.3.d, e
        if (aggrDef.getFilterEnabled()) {
            //create new parameter-samples
            for (int i = 0; i < parameterSets.size(); i++) {
                final AggregationParameterValueList newParaVals = (aSetVal == null ? null : aSetVal.get(i).getValues());
                manager.sampleAndFilterParam(identityId, i, aggrExpired, newParaVals);
            }
            //check the filter is triggered
            if (!manager.isFilterTriggered(identityId)) {
                return false;
            }
        } else {
            //no filtering enabled
            //create new parameter-samples
            for (int i = 0; i < parameterSets.size(); i++) {
                final AggregationParameterValueList newParaVals = (aSetVal == null ? null : aSetVal.get(i).getValues());
                manager.sampleParam(identityId, i, aggrExpired, newParaVals);
            }
        }
        return true;
    }

    /**
     * checks if the sampleInterval is 0 or greater than the updateInterval of
     * the given definition. if it is, it samples new parameters and check if
     * the filter was triggered. if it was triggerd, or no filter was enabled,
     * the values will be set to the internal list
     *
     * @param identityId the identity id of the aggregation-definito to be
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter is periodic and the aggregation period is up.
     */
    private void checkSampleIntervalAndSampleParam(Long identityId, boolean aggrExpired) {
        final AggregationDefinitionDetails def = manager.getAggregationDefinition(identityId);
        //check the sampleInterval
        final AggregationParameterSetList parameterSets = def.getParameterSets();
        for (int i = 0; i < parameterSets.size(); i++) {
            final double sampleInterval = parameterSets.get(i).getSampleInterval().getValue();
            //If '0' or if its greater than the updateInterval then just a single sample of the parameters is required per aggregation update
            if (sampleInterval == 0 || (sampleInterval > def.getReportInterval().getValue())) {
                checkFilterAndSampleParam(identityId, aggrExpired);
            }
        }
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

        private HashMap<Long, Timer> updateTimerList; // updateInterval Timers list
        private HashMap<Long, Timer> filterTimeoutTimerList; // filterTimeout Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicReportingManager() {
            updateTimerList = new HashMap<Long, Timer>();
            filterTimeoutTimerList = new HashMap<Long, Timer>();
        }

        public void refreshAll() {
            this.refreshList(manager.listAllIdentities());
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

        public void refresh(Long identityId) {
            // get Aggregation Definition
            AggregationDefinitionDetails aDef = manager.getAggregationDefinition(identityId);

            if (updateTimerList.containsKey(identityId)) { // Does it exist in the Periodic Reporting Manager?
                this.removePeriodicReporting(identityId);
            }
            if (filterTimeoutTimerList.containsKey(identityId)) { // Does it exist in the filter-timeout timerlist already?
                this.removeFilteredTimeoutReporting(identityId);
            }

            if (aDef != null) { // Does it exist in the Aggregation Definitions List?
                if (aDef.getGenerationEnabled()) { //requirement 3.7.3.a, 3.7.3.b
                    manager.populateAggregationValues(identityId); // Reset the Sampling Values
                    if (aDef.getReportInterval().getValue() != 0) { // Is the periodic reporting active? (requirement: 3.7.3.i, 3.7.9.2.k)
                        this.addPeriodicReporting(identityId);
                    }
                    //AD-HOC aggregations can also have a filter and must be added to the filtered-timeout-list in this case
                    if (aDef.getFilterEnabled() && aDef.getFilteredTimeout().getValue() != 0) { // requirement 3.7.3.j
                        this.addFilteredTimeoutReporting(identityId);
                    }
                }
            } else { //aggregation was removed
                manager.removeAggregationValues(identityId);
            }

        }

        public void refreshList(LongList identityIds) {
            if (identityIds == null) {
                return;
            }
            for (Long identityId : identityIds) {
                this.refresh(identityId);
            }
        }

        /**
         * starts the periodic-update-timer and if enabled the
         * filter-timeout-timer.
         *
         * @param identityId the id of the aggregation which timers should be
         * started
         */
        private void addPeriodicReporting(Long identityId) {
            //requirement: 3.7.9.2.k
            publishImmediatePeriodicUpdate(identityId);
            Timer timer = new Timer();
            updateTimerList.put(identityId, timer);

            final AggregationDefinitionDetails aggrDef = manager.getAggregationDefinition(identityId);
            this.startUpdatesTimer(identityId, aggrDef.getReportInterval());  // requirement 3.7.3.c

        }

        /**
         * starts the filtered timeout-timer, if enabled
         *
         * @param identityId
         */
        private void addFilteredTimeoutReporting(Long identityId) {
            final AggregationDefinitionDetails aggrDef = manager.getAggregationDefinition(identityId);
            // Is the filter enabled? If so, do we have a filter Timeout set?
//            if (aggrDef.getFilterEnabled() && aggrDef.getFilteredTimeout().getValue() != 0) { // requirement 3.7.2.12
            Timer timer2 = new Timer();
            filterTimeoutTimerList.put(identityId, timer2);
            this.startFilterTimeoutTimer(identityId, aggrDef.getFilteredTimeout());
//            } else {
//                filterTimeoutTimerList.put(identityId, null);
//            }

        }

        private void removePeriodicReporting(Long objId) {
            this.stopUpdatesTimer(objId);
            updateTimerList.remove(objId);
        }

        private void removeFilteredTimeoutReporting(Long identityId) {
            this.stopFilterTimeoutTimer(identityId);
            filterTimeoutTimerList.remove(identityId);
        }

        private void startUpdatesTimer(final Long identityId, final Duration interval) {
            updateTimerList.get(identityId).scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {  // requirement: 3.7.3.c
                    if (active) {
                        AggregationDefinitionDetails def = manager.getAggregationDefinition(identityId);
                        checkSampleIntervalAndSampleParam(identityId, true);
                        if (!def.getFilterEnabled()) { // The Filter is not enabled? // requirement: 3.7.2.a.a, 
                            publishPeriodicAggregationUpdate(identityId, 
                                    manager.getAggregationValue(identityId, GenerationMode.PERIODIC)); //requirement: 3.7.3.h
                            manager.resetAggregationSampleHelperVariables(identityId);
                        } else {  // requirement: 3.7.2.a.c, 
                            if (manager.isFilterTriggered(identityId) == true) { // The Filter is on and triggered? requirement: 3.7.2.6
                                publishPeriodicAggregationUpdate(identityId, 
                                        manager.getAggregationValue(identityId, GenerationMode.PERIODIC)); //requirement: 3.7.3.h
                                manager.resetAggregationSampleHelperVariables(identityId);
                                resetFilterTimeoutTimer(identityId);        // Reset the timer
                            }
                        }
                    }
                } // the time is being converted to milliseconds by multiplying by 1000 
            }, (int) (interval.getValue() * 1000), (int) (interval.getValue() * 1000)); // requirement: 3.7.3.g
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
            this.startFilterTimeoutTimer(objId, manager.getAggregationDefinition(objId).getFilteredTimeout());
        }

        private void startFilterTimeoutTimer(final Long identityId, final Duration interval) {
            filterTimeoutTimerList.get(identityId).scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {  // requirement: 3.7.2.a.c, 3.7.3.n
                    if (active) {
                        manager.setFilterTriggered(identityId, true);
                        //get the new samples and update the aggregation in the internal list
                        for (int index = 0; index < manager.getAggregationDefinition(identityId).getParameterSets().size(); index++) {
                            manager.sampleParam(identityId, index);
                        }
                        //publish the values in the internal list
                        publishPeriodicAggregationUpdate(identityId, manager.getAggregationValue(identityId, GenerationMode.FILTERED_TIMEOUT));
                        manager.resetAggregationSampleHelperVariables(identityId);
                    }
                } // the time is being converted to milliseconds by multiplying by 1000
            }, 0, (int) (interval.getValue() * 1000));
        }

        private void stopFilterTimeoutTimer(final Long objId) {
            if (filterTimeoutTimerList.get(objId) != null) { // Does it exist?
                filterTimeoutTimerList.get(objId).cancel();
            }
        }

    }

    /**
     * this manager samples the parameter-values of the aggregations
     * parameterSet after the given sampleInterval. It saves the values to an
     * internal list and checks at the same time, if the filter would be
     * triggered with this value. But it doesnt publish the value yet. The
     * generation of the update will be done with the periodic or filtered
     * generation mode. This modes use the sample-values generated here as the
     * update-value when the updateInterval or filter-timeout-interval is
     * expired.
     *
     * Hasnt been tested yet!
     *
     */
    private class PeriodicSamplingManager { // requirement: 3.7.2.1a

        private List<Timer> sampleTimerList; // Timer List. One timer for each parameterSet of each aggregation that needs to be sampled
        private LongList aggregationObjIdList; // ids of the aggregations whiches parameterSet started the timer above. first index here belongs to the first timer abode. 
        private List<Integer> parameterSetIndexList; // index of the parameter set in the aggregation above, that belongs to the timer. first index here belngs to the first  aggregation id above and belongs to the first timer above.
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicSamplingManager() {
            sampleTimerList = new ArrayList<Timer>();
            aggregationObjIdList = new LongList();
            parameterSetIndexList = new ArrayList<Integer>();
        }

        public void refreshAll() {
            this.refreshList(manager.listAllIdentities());
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

        public void refresh(Long identityId) {
            final int index = aggregationObjIdList.indexOf(identityId);

            if (index != -1) { // Does it exist in the PeriodicSamplingManager?
                this.removePeriodicSampling(identityId);
            }

            if (manager.existsIdentity(identityId)) { // Does it exist in the Aggregation Definitions List?
                manager.populateAggregationValues(identityId); // Reset the Sampled Values
                this.addPeriodicSampling(identityId);
            } else { //aggregation was removed
                manager.removeAggregationValues(identityId);
            }

        }

        public void refreshList(LongList identityIds) {
            if (identityIds == null) {
                return;
            }

            for (Long identityId : identityIds) {
                refresh(identityId);
            }
        }

        private void addPeriodicSampling(Long identityId) {
            final AggregationDefinitionDetails aggrDef = manager.getAggregationDefinition(identityId);
            if (!aggrDef.getGenerationEnabled()) {
                return; // Periodic Sampling shall not occur if the generation is not enabled at the definition level
            }
            final int parameterSetsTotal = aggrDef.getParameterSets().size();
            int index = sampleTimerList.size();

            for (int indexOfParameterSet = 0; indexOfParameterSet < parameterSetsTotal; indexOfParameterSet++) {
                Duration sampleInterval = aggrDef.getParameterSets().get(indexOfParameterSet).getSampleInterval();

                //means ad hoc value, dont add to sample timer
                if (sampleInterval.getValue() > aggrDef.getReportInterval().getValue()) {
                    sampleInterval = new Duration(0);
                }
                // Add to the Periodic Sampling Manager only if there's a sampleInterval selected for the parameterSet
                if (sampleInterval.getValue() != 0) {
                    aggregationObjIdList.add(index, identityId);
                    parameterSetIndexList.add(index, indexOfParameterSet);
                    Timer timer = new Timer();  // Take care of adding a new timer
                    sampleTimerList.add(index, timer);
                    startTimer(index, sampleInterval);
                    index++;
                }
            }
        }

        private void removePeriodicSampling(Long objId) {
            for (int index = aggregationObjIdList.indexOf(objId); index != -1; index = aggregationObjIdList.indexOf(objId)) {
                this.stopTimer(index);
                aggregationObjIdList.remove(index);
                sampleTimerList.remove(index);
                parameterSetIndexList.remove(index);
            }
        }

        private void startTimer(final int index, Duration interval) {  // requirement: 3.7.2.11
            final Long identityId = aggregationObjIdList.get(index);
            final int indexOfparameterSet = parameterSetIndexList.get(index);

            sampleTimerList.get(index).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (active) {
                        //create the new paraemtersamples and set them if filter triggered or not enabled
                        manager.sampleAndFilterParam(identityId, indexOfparameterSet);

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
        if (configurationObjectDetails == null) {
            return false;
        }

        if (configurationObjectDetails.getConfigObjects() == null) {
            return false;
        }

        // Is the size 2?
        if (configurationObjectDetails.getConfigObjects().size() != 2) {
            return false;
        }

        ConfigurationObjectSet confSet0 = configurationObjectDetails.getConfigObjects().get(0);
        ConfigurationObjectSet confSet1 = configurationObjectDetails.getConfigObjects().get(1);

        // Confirm the objTypes
        if (!confSet0.getObjType().equals(AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE)
                && !confSet1.getObjType().equals(AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        if (!confSet0.getObjType().equals(AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE)
                && !confSet1.getObjType().equals(AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet0.getDomain().equals(ConfigurationProviderSingleton.getDomain())
                || !confSet1.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if (confSet0.getObjInstIds().isEmpty() && confSet1.getObjInstIds().isEmpty()) {
            manager.reconfigureDefinitions(new LongList(), new IdentifierList(),
                    new LongList(), new AggregationDefinitionDetailsList());   // Reconfigures the Manager

            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        ConfigurationObjectSet confSetDefs = (confSet0.getObjType().equals(AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE)) ? confSet0 : confSet1;

        AggregationDefinitionDetailsList pDefs = (AggregationDefinitionDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetDefs.getObjInstIds());

        ConfigurationObjectSet confSetIdents = (confSet0.getObjType().equals(AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE)) ? confSet0 : confSet1;

        IdentifierList idents = (IdentifierList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetIdents.getObjInstIds());

        manager.reconfigureDefinitions(confSetIdents.getObjInstIds(), idents,
                confSetDefs.getObjInstIds(), pDefs);   // Reconfigures the Manager

        // The periodic reporting and sampling need to be refreshed
        manager.createAggregationValuesList(confSetIdents.getObjInstIds());
        periodicReportingManager.refreshAll();  // Refresh the reporting
        periodicSamplingManager.refreshAll();
        
        return true;
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Needs the Common API here!
        ConfigurationObjectSetList list = manager.getCurrentConfiguration();
        list.get(0).setObjType(AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE);
        list.get(1).setObjType(AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE);

        // Needs the Common API here!
        ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(list);

        return set;
    }

    @Override
    public COMService getCOMService() {
        return AggregationHelper.AGGREGATION_SERVICE;
    }

}
