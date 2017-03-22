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
import esa.mo.mc.impl.interfaces.ExternalStatisticFunctionsInterface;
import esa.mo.reconfigurable.service.ConfigurationNotificationInterface;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import java.util.HashMap;
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
import org.ccsds.moims.mo.com.structures.ObjectKeyList;
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
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.TimeList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.statistic.StatisticHelper;
import org.ccsds.moims.mo.mc.statistic.provider.MonitorStatisticsPublisher;
import org.ccsds.moims.mo.mc.statistic.provider.StatisticInheritanceSkeleton;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticEvaluationReport;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticEvaluationReportList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticFunctionDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkDetailsList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticParameterDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticParameterDetailsList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValue;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 *
 */
public class StatisticProviderServiceImpl extends StatisticInheritanceSkeleton implements ReconfigurableServiceImplInterface {

    private MALProvider statisticServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private StatisticManager manager;
    private MonitorStatisticsPublisher publisher;
    private final ConnectionProvider connection = new ConnectionProvider();
    private PeriodicReportingManager periodicReportingManager;
    private PeriodicCollectionManager periodicCollectionManager;
    private PeriodicSamplingManager periodicSamplingManager;
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private ConfigurationNotificationInterface configurationAdapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param parameterManager
     * @param statisticFunctions
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices,
            ParameterManager parameterManager,
            ExternalStatisticFunctionsInterface statisticFunctions)
            throws MALException {

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
            } catch (MALException ex) { // nothing to be done..
            }

            try {
                StatisticHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
            }
        }

        publisher = createMonitorStatisticsPublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // Shut down old service transport
        if (null != statisticServiceProvider) {
            connection.closeAll();
        }

        statisticServiceProvider = connection.startService(StatisticHelper.STATISTIC_SERVICE_NAME.toString(), StatisticHelper.STATISTIC_SERVICE, this);

        running = true;
        manager = new StatisticManager(comServices, parameterManager, statisticFunctions);

        periodicReportingManager = new PeriodicReportingManager();
        periodicCollectionManager = new PeriodicCollectionManager();
        periodicSamplingManager = new PeriodicSamplingManager();
        periodicReportingManager.init(); // Initialize the Periodic Reporting Manager
        periodicCollectionManager.init(); // Initialize the Periodic Collection Manager
        periodicSamplingManager.init(); // Initialize the Periodic Sampling Manager
        
        initialiased = true;
        Logger.getLogger(StatisticProviderServiceImpl.class.getName()).info("Statistic service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != statisticServiceProvider) {
                statisticServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider(){
        return this.connection;
    }

    @Override
    public void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter){
        this.configurationAdapter = configurationAdapter;
    }

    private void publishStatisticsUpdate(final Long objIdLink, final StatisticValue sVal, final ObjectId source) {
        try {
            synchronized(lock){
                if (!isRegistered) {
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).log(Level.FINE,
                    "Generating Statistics update for the Statistic Link objId: {0}",
                    new Object[]{
                        objIdLink
                    });

            final Long sValObjId = manager.storeAndGenerateStatValueInsobjId(sVal, objIdLink, connection.getConnectionDetails());

            final StatisticParameterDetails statLink = manager.getStatisticLink(objIdLink);
            final Identifier funcName = new Identifier(manager.getStatisticFunction(statLink.getStatFuncInstId()).getName().toString());

            // requirements: 3.6.9.2.a , 3.6.9.2.b , 3.6.9.2.c , 3.6.9.2.d
            final EntityKey ekey = new EntityKey(funcName, objIdLink, statLink.getParameterId().getInstId(), sValObjId);
            final Time timestamp = HelperTime.getTimestampMillis(); //  requirement: 3.6.9.2.e

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectIdList sourceId = new ObjectIdList();

            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
            sourceId.add(source); // requirement: 3.6.9.2.f and 3.6.9.2.g

            StatisticValueList statisticValues = new StatisticValueList();
            statisticValues.add(sVal); // requirement: 3.6.9.2.h

            publisher.publish(hdrlst, sourceId, statisticValues);

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public StatisticEvaluationReportList getStatistics(LongList linkObjInstIds, Boolean isGroup, ObjectKeyList paramDefObjInstIds, MALInteraction interaction) throws MALInteractionException, MALException {

        LongList outLongLst = new LongList();
        StatisticEvaluationReportList outEvaluations = new StatisticEvaluationReportList();
        Long linkObjInstId;

        // Please check the following RID: https://github.com/SamCooper/MC_SPEC_RIDS/issues/108
        
        if (null == linkObjInstIds) { // Is the input null? requirement: 3.6.7.2.a
            throw new IllegalArgumentException("funcObjInstIds argument must not be null");
        }

        for (int index = 0; index < linkObjInstIds.size(); index++) {
            linkObjInstId = linkObjInstIds.get(index);

            if (linkObjInstId == 0) {  // Is it the wildcard '0'? requirement: 3.6.7.2.b
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAllLinks()); // ... add all in a row
                break;
            }

            if (isGroup) { // requirement: 3.6.7.2.c
                GroupDetails group = groupService.retrieveGroupDetailsFromArchive(connection.getConnectionDetails().getDomain(), linkObjInstId);

                if (group != null) {
                    LongList objIdsGroup = groupService.getGroupObjectIdsFromGroup(group, linkObjInstId);

                    if (objIdsGroup != null) {
                        for (Long objId : objIdsGroup) {
                            if (manager.statLinkExists(objId)) { // does it exist? 
                                outLongLst.add(objId); //yap
                            }else{
                                //unkIndexList.add(new UInteger(index)); // requirement: ?
                            }
                        }
                    }
                } else {
                    // invIndexList.add(new UInteger(index)); // requirement: ?
                }

            } else { // requirement: 3.6.12.2.a
                if (linkObjInstId != null) { // Does the statistic link statLinkExists?
                    outLongLst.add(linkObjInstId); //yap
                } else {
                    // No error is being returned here...
                }
            }

        }

        for (Long outLong : outLongLst) {
            outEvaluations.add(manager.getStatisticEvaluationReport(outLong)); // requirement: 3.6.7.2.f
        }

        return outEvaluations;
    }

    @Override
    public StatisticEvaluationReportList resetEvaluation(Boolean isStatLinkGroup, LongList objInstIds, Boolean returnCurrentEval, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        LongList outLongLst = new LongList();
        Long linkObjInstId;

        
        for (int index = 0; index < objInstIds.size(); index++) {
            linkObjInstId = objInstIds.get(index);

            if (linkObjInstId == 0) {  // Is it the wildcard '0'? // requirement: 3.6.8.2.c
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAllLinks()); // ... add all in a row
                break;
            }

            if (isStatLinkGroup) { // requirement: 3.6.8.2.a
                GroupDetails group = groupService.retrieveGroupDetailsFromArchive(connection.getConnectionDetails().getDomain(), linkObjInstId);

                if (group != null) {
                    LongList objIdsGroup = groupService.getGroupObjectIdsFromGroup(group, linkObjInstId);

                    if (objIdsGroup != null) {
                        for (Long objId : objIdsGroup) {
                            if (manager.statLinkExists(objId)) { // does it exist? 
                                outLongLst.add(objId); // requirement: 3.6.8.2.b
                            }else{
                                unkIndexList.add(new UInteger(index)); // requirement: 3.6.8.2.d
                            }
                        }
                    }
                } else {
                    invIndexList.add(new UInteger(index)); // requirement: 3.6.8.2.e
                }

            } else { // requirement: 3.6.8.2.a
                if (linkObjInstId != null) { // Does the statistic link statLinkExists?
                    outLongLst.add(linkObjInstId); // requirement: 3.6.8.2.b
                } else {
                    unkIndexList.add(new UInteger(index)); // requirement: 3.6.8.2.d
                }
            }

        }
        
        
        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.6.8.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.6.8.3.2
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.6.8.2.g
        
        if(returnCurrentEval){ // requirement: 3.6.8.2.f
            StatisticEvaluationReportList outEvaluations = new StatisticEvaluationReportList();
    
            for (Long outLong : outLongLst) {
                outEvaluations.add(manager.getStatisticEvaluationReport(outLong));
                manager.resetStatisticEvaluationReport(outLong);
            }

            return outEvaluations;
        }else{
            for (Long outLong : outLongLst) {
                manager.resetStatisticEvaluationReport(outLong);
            }

            return null;
        }

    }

    @Override
    public void enableService(Boolean enableService, MALInteraction interaction) throws MALInteractionException, MALException {

        if (enableService) {
            this.periodicReportingManager.start(); // requirement: 3.5.10.2.a
        } else {
            this.periodicReportingManager.pause(); // requirement: 3.5.10.2.b
        }

    }

    @Override
    public Boolean getServiceStatus(MALInteraction interaction) throws MALInteractionException, MALException {

        return this.periodicReportingManager.getStatus();

    }

    @Override
    public void enableGeneration(Boolean isGroupIds, InstanceBooleanPairList enableInstances, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        InstanceBooleanPair enableInstance;

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (null == isGroupIds || null == enableInstances) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        boolean foundWildcard = false;

        for (InstanceBooleanPair instance : enableInstances) { // requirement: 3.6.12.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.6.12.2.c
                manager.setReportingEnabledAll(instance.getValue(), connection.getConnectionDetails());
                foundWildcard = true;
                
                if (configurationAdapter != null){
                    configurationAdapter.configurationChanged(this);
                }
                
                break;
            }
        }

        if (!foundWildcard) {

            for (int index = 0; index < enableInstances.size(); index++) {   // requirement: 3.6.12.2.b
                enableInstance = enableInstances.get(index);

                if (isGroupIds) { // requirement: 3.6.12.2.a
                    GroupDetails group = groupService.retrieveGroupDetailsFromArchive(connection.getConnectionDetails().getDomain(), enableInstances.get(index).getId());

                    if (group == null) {
                        invIndexList.add(new UInteger(index)); // requirement: 3.6.12.2.h
                    } else {
                        LongList objIds = groupService.getGroupObjectIdsFromGroup(group, enableInstances.get(index).getId());

                        if (objIds != null) {
                            for (Long objId : objIds) {
                                objIdToBeEnabled.add(objId);
                                valueToBeEnabled.add(enableInstance.getValue());

                                if (!manager.statLinkExists(objId)) { // does it exist? 
                                    unkIndexList.add(new UInteger(index)); // requirement: 3.6.12.2.g
                                }
                            }
                        }
                    }

                } else { // requirement: 3.6.12.2.a
                    objIdToBeEnabled.add(enableInstance.getId());
                    valueToBeEnabled.add(enableInstance.getValue());

                    if (!manager.statLinkExists(enableInstance.getId())) { // does it exist? 
                        unkIndexList.add(new UInteger(index)); // requirement: 3.6.12.2.d
                    }
                }
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.6.12.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.6.12.3.2
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        if (!foundWildcard) {
            // requirement: 3.6.12.2.i (This part of the code is not reached if an error is thrown)
            for (int index = 0; index < objIdToBeEnabled.size(); index++) {
                // requirement: 3.6.12.2.e and 3.6.12.2.f and 3.6.12.2.j
                manager.setReportingEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), connection.getConnectionDetails());
            }
        }

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public LongList addParameterEvaluation(StatisticParameterDetailsList newDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        LongList outLongLst = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList unkIndexList = new UIntegerList();
        StatisticParameterDetails statDefDetail;

        if (null == newDetails) { // Is the input null?
            throw new IllegalArgumentException("newDetails argument must not be null");
        }

        for (int index = 0; index < newDetails.size(); index++) { // requirement: 3.4.10.2.f (incremental "for cycle" guarantees that)
            statDefDetail = newDetails.get(index);

            // Get the StatisticFunction
            final StatisticFunctionDetails statFunc = manager.getStatisticFunction(statDefDetail.getStatFuncInstId());  // requirement: 3.6.13.2.b

            if (statFunc == null) { // requirement: 3.6.13.2.c
                unkIndexList.add(new UInteger(index));
                continue;
            }

            // Check if the parameterId statLinkExists
            final ParameterDefinitionDetails parameterDef = manager.getParameterDefinition(statDefDetail.getParameterId().getInstId()); // requirement: 3.6.13.2.d

            if (parameterDef == null) { // requirement: 3.6.13.2.e
                unkIndexList.add(new UInteger(index));
                continue;
            }

            final double samplingInterval = statDefDetail.getLinkDetails().getSamplingInterval().getValue(); // requirement: 3.6.13.2.f

            if (samplingInterval < 0) { // requirement: 3.6.13.2.g
                invIndexList.add(new UInteger(index));
                continue;
            }

        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.6.13.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.6.13.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // If no errors, then add!  // requirement: 3.6.13.2.h
        for (StatisticParameterDetails statDetails : newDetails) {  // requirement: 3.6.13.2.i
            outLongLst.add(manager.add(statDetails, connection.getConnectionDetails())); // requirement: 3.6.13.2.k
        }

        // Start sampling the referenced parameter 
        this.periodicSamplingManager.refreshList(outLongLst); // requirement: 3.6.13.2.j
        this.periodicCollectionManager.refreshList(outLongLst);
        this.periodicReportingManager.refreshList(outLongLst);

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }

        return outLongLst; // requirement: 3.7.10.2.k

    }

    @Override
    public void updateParameterEvaluation(LongList objInstIds, StatisticLinkDetailsList newDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList invIndexList = new UIntegerList();
        UIntegerList unkIndexList = new UIntegerList();
        StatisticLinkDetails statLink;
        Long objId;

        if (null == newDetails || objInstIds == null) { // Is the input null?
            throw new IllegalArgumentException("newDetails argument must not be null");
        }

        for (int index = 0; index < newDetails.size(); index++) { // requirement: 3.6.14.2.c
            statLink = newDetails.get(index);
            objId = objInstIds.get(index);

            // Get the Statistic Link
            final StatisticParameterDetails oldLink = manager.getStatisticLink(objId);

            if (oldLink == null) { // requirement: 3.6.14.2.d
                unkIndexList.add(new UInteger(index));
                continue;
            }

            final double samplingInterval = statLink.getSamplingInterval().getValue();

            if (samplingInterval < 0) { // requirement: 3.6.14.2.e
                invIndexList.add(new UInteger(index));
                continue;
            }

        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.6.14.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.6.13.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, unkIndexList));
        }

        // If no errors, then update!  // requirement: 3.6.13.2.f
        for (int index = 0; index < newDetails.size(); index++) { // requirement: 3.6.14.2.c
            statLink = newDetails.get(index);
            objId = objInstIds.get(index);

            // Get the Statistic Link
            StatisticParameterDetails editLink = manager.getStatisticLink(objId);
            editLink.setLinkDetails(statLink);

            manager.update(objId, editLink, connection.getConnectionDetails());
            // Refresh the sampling
            this.periodicSamplingManager.refresh(objId); // requirement: 3.6.14.2.j
            this.periodicCollectionManager.refresh(objId);
            this.periodicReportingManager.refresh(objId);
        }

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public void removeParameterEvaluation(LongList objInstIds, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == objInstIds){ // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < objInstIds.size(); index++) {
            tempLong = objInstIds.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.6.15.2.b
                tempLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempLongLst.addAll(manager.listAllLinks()); // ... add all in a row
                break;
            }

            if (manager.statLinkExists(tempLong)) { // Does it match an existing definition?
                tempLongLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.6.15.2.c
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()){ // requirement: 3.6.15.3 (error: a, b)
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.6.15.3.e (Inserting the errors before this line guarantees that the requirement is met)
        for (Long toDelete : tempLongLst) {
            manager.delete(toDelete);
        }

        periodicReportingManager.refreshList(tempLongLst); // Refresh the Periodic Reporting Manager for the removed Definitions
        periodicCollectionManager.refreshList(tempLongLst);
        periodicSamplingManager.refreshList(tempLongLst);
        // COM archive is left untouched. requirement: 3.6.15.2.f

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }
    
    }

    @Override
    public COMService getCOMService() {
        return StatisticHelper.STATISTIC_SERVICE;
    }    
    
    private static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).warning("PublishInteractionListener::publishRegisterErrorReceived");
        }

    }

    private class PeriodicSamplingManager { // requirement: 3.7.2.1a

        private HashMap<Long, Timer> sampleTimerList; // Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicSamplingManager() {
            sampleTimerList = new HashMap<Long, Timer>();
        }

        public void refreshAll() {
            this.refreshList(manager.listAllLinks());
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
            if (sampleTimerList.containsKey(objId)) { // Does it exist in the PeriodicSamplingManager?
                this.removePeriodicSampling(objId);
            }

            if (manager.statLinkExists(objId)) { // Does it exist?
                this.addPeriodicSampling(objId);
            }

            manager.getDataSets().lock();
            manager.getDataSets().resetDataSet(objId); // Reset the Data Set with the samples
            manager.getDataSets().unlock();
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
            final StatisticParameterDetails statLink = manager.getStatisticLink(objId);

            if (!statLink.getLinkDetails().getReportingEnabled()) {
                return; // Periodic Sampling shall not occur if the generation is not enabled at the definition level
            }
            
            Duration sampleInterval = statLink.getLinkDetails().getSamplingInterval();

            if (sampleInterval.getValue() > manager.getStatisticLink(objId).getLinkDetails().getReportingInterval().getValue()) {
                sampleInterval = new Duration(0);
            }

                // Add to the Periodic Sampling Manager only if there's a sampleInterval selected for the parameterSet
                if (sampleInterval.getValue() != 0) {
                    Timer timer = new Timer();  // Take care of adding a new timer
                    sampleTimerList.put(objId, timer);
                    startTimer(objId, sampleInterval);
                }
                
        }

        private void removePeriodicSampling(Long objId) {
            this.stopTimer(objId);
            sampleTimerList.remove(objId);
        }

        private void startTimer(final Long objId, Duration interval) {  // requirement: 3.7.2.11

            sampleTimerList.get(objId).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() { // Periodic sampling
                    if (active) {
                        ParameterValue pVal; 
                        try {
                            pVal = manager.getParameterValue(objId); // Get the Parameter Value
                            // Add the value to the data set
                            manager.getDataSets().addAttributeToDataSet(objId, pVal.getRawValue(), HelperTime.getTimestampMillis());
                        } catch (MALInteractionException ex) {
                            manager.getDataSets().addAttributeToDataSet(objId, null, HelperTime.getTimestampMillis());
                        }
                        
                        
                    }
                } // the time has to be converted to milliseconds by multiplying by 1000
            }, 0, (int) (interval.getValue() * 1000)); // requirement: 3.6.2.g
        }

        private void stopTimer(Long objId) {
            sampleTimerList.get(objId).cancel();
        }

    }
    
    private class PeriodicCollectionManager {

        private HashMap<Long, Timer> collectionTimerList; // Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicCollectionManager() {
            collectionTimerList = new HashMap<Long, Timer>();
        }

        public void refreshAll() {
            this.refreshList(manager.listAllLinks());
        }

        public void refreshList(LongList objIds) {
            if (objIds == null) {
                return;
            }
            for (Long objId : objIds) {
                refresh(objId);
            }
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
            if (collectionTimerList.containsKey(objId)) { // Does it exist in the PeriodicSamplingManager?
                this.removePeriodicCollecting(objId);
            }

            if (manager.statLinkExists(objId)) { // Does it exist?
                this.addPeriodicCollecting(objId);
            }

            manager.resetStatisticEvaluationReport(objId); // Reset the Reports
        }

        private void addPeriodicCollecting(Long objId) {
            final StatisticParameterDetails statLink = manager.getStatisticLink(objId);

            if (!statLink.getLinkDetails().getReportingEnabled()) {
                return; // Periodic Sampling shall not occur if the generation is not enabled at the definition level
            }
            
            Duration collectionInterval = statLink.getLinkDetails().getCollectionInterval();

                // Add to the Periodic Collection Manager
                if (collectionInterval.getValue() != 0) {
                    Timer timer = new Timer();  // Take care of adding a new timer
                    collectionTimerList.put(objId, timer);
                    startTimer(objId, collectionInterval);
                }
                
        }

        private void removePeriodicCollecting(Long objId) {
            this.stopTimer(objId);
            collectionTimerList.remove(objId);
        }

        private void startTimer(final Long objId, Duration interval) {

            collectionTimerList.get(objId).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() { // Periodic sampling
                    if (active) {
                        // Retrieve the Statistic Link
                        StatisticParameterDetails link = manager.getStatisticLink(objId);

                        manager.getDataSets().lock(); // Lock the Sets

                        // Retrieve the corresponding data set
                        TimeList times = manager.getDataSets().getTimeSet(objId);
                        AttributeValueList values = manager.getDataSets().getDataSet(objId);
                        
                        // Generate the Statistic Report
                        StatisticValue statValue = manager.generateStatisticValue(link.getStatFuncInstId(), times, values);
                        manager.addStatisticValueToStatisticEvaluationReport(objId, statValue);
                        
                        // Reset the evaluations
                        if(link.getLinkDetails().getResetEveryCollection()){
                            manager.getDataSets().resetDataSet(objId); // requirement: 3.6.2.f
                        }

                        manager.getDataSets().unlock(); // Unlock the Sets
                    
                    }
                } // the time has to be converted to milliseconds by multiplying by 1000
            }, 0, (int) (interval.getValue() * 1000) );
        }

        private void stopTimer(Long objId) {
            collectionTimerList.get(objId).cancel();
        }

    }
    
    private class PeriodicReportingManager { // requirement: 3.7.2.1a

        private HashMap<Long, Timer> updateTimerList; // updateInterval Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicReportingManager() {
            updateTimerList = new HashMap<Long, Timer>();
        }

        public void refreshAll() {
            this.refreshList(manager.listAllLinks());
        }

        public boolean getStatus() {
            return active;
        }

        public void pause() {
            active = false;
        }

        public void start() {
            active = true;
        }

        public void init() {
            this.refreshAll(); // Refresh all the Statistic Links in the Manager
            this.start(); // set active flag to true
        }

        public void refresh(Long objId) {
            // getStatisticLink aggregation definition
            StatisticParameterDetails statLink = manager.getStatisticLink(objId);

            if (updateTimerList.containsKey(objId)) { // Does it exist in the Periodic Reporting Manager?
                this.removePeriodicReporting(objId);
            }

            if (statLink != null) { // Does it exist in the Statistic Links List?
                if (statLink.getLinkDetails().getReportingInterval().getValue() != 0
                        && statLink.getLinkDetails().getReportingEnabled()) { // Is the periodic reporting active?
                    this.addPeriodicReporting(objId);
                }
            }

//            manager.getDataSets().resetEvaluation(objId); // Reset the Evaluation
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
            this.startUpdatesTimer(objId, manager.getStatisticLink(objId).getLinkDetails().getReportingInterval());
        }

        private void removePeriodicReporting(Long objId) {
            this.stopUpdatesTimer(objId);
            updateTimerList.remove(objId);
        }

        private void startUpdatesTimer(final Long objId, final Duration interval) {
            updateTimerList.get(objId).scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {  // requirement: 3.7.2.3
                    if (active) {
                        StatisticParameterDetails statLink = manager.getStatisticLink(objId);
                        
                        if (statLink.getLinkDetails().getReportingEnabled()) {  // requirement 3.6.2.h
                            StatisticEvaluationReport report = manager.getStatisticEvaluationReport(objId);
                            
                            if (report != null){
                                publishStatisticsUpdate(objId, manager.getStatisticEvaluationReport(objId).getValue(), null);
                            }
                        }
                        
                        if(true){  // Reset at every update Interval
                            manager.resetStatisticEvaluationReport(objId);
                        }
                        
                    }
                } // the time is being converted to milliseconds by multiplying by 1000  (starting delay included)
            }, (int) (interval.getValue() * 1000), (int) (interval.getValue() * 1000)); // requirement: 3.7.2.3
        }

        private void stopUpdatesTimer(final Long objId) {
            updateTimerList.get(objId).cancel();
        }

    }
    

    
    @Override
    public Boolean reloadConfiguration(ConfigurationObjectDetails configurationObjectDetails) {
        // Check if it is not null
        if (configurationObjectDetails == null){
            return false;
        }

        if (configurationObjectDetails.getConfigObjects() == null){
            return false;
        }
        
        // The Configuration of the Statistic service is only composed by Statistic Links
        if (configurationObjectDetails.getConfigObjects().size() != 1){  
            return false;
        }

        if(configurationObjectDetails.getConfigObjects().get(0) == null){
            return false;
        }
        
        ConfigurationObjectSet confSet = configurationObjectDetails.getConfigObjects().get(0);

        // Confirm the objType
        if (!confSet.getObjType().equals(StatisticHelper.STATISTICLINK_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }
        
        // If the list is empty, reconfigure the service with nothing...
        if(confSet.getObjInstIds().isEmpty()){
            manager.reconfigureLinks(new LongList(), new StatisticParameterDetailsList());   // Reconfigures the Manager
            periodicSamplingManager.refreshAll();  // Refresh the reporting
            periodicCollectionManager.refreshAll();  // Refresh the reporting
            periodicReportingManager.refreshAll();  // Refresh the reporting
            return true;
        }
            
        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        StatisticParameterDetailsList links = (StatisticParameterDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getCOMservices().getArchiveService(),
                StatisticHelper.STATISTICLINK_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSet.getObjInstIds());

        manager.reconfigureLinks(confSet.getObjInstIds(), links);   // Reconfigures the Manager
        periodicSamplingManager.refreshAll();  // Refresh the reporting
        periodicCollectionManager.refreshAll();  // Refresh the reporting
        periodicReportingManager.refreshAll();  // Refresh the reporting

        return true;

    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        HashMap<Long, StatisticParameterDetails> defObjs = manager.getStatisticLinks();

        ConfigurationObjectSet objsSet = new ConfigurationObjectSet();
        objsSet.setDomain(ConfigurationProviderSingleton.getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(StatisticHelper.STATISTICLINK_OBJECT_TYPE);

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSet);

        // Needs the Common API here!
        ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(list);

        return set;
    }
    
}
