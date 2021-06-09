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
package esa.mo.mc.impl.provider;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.misc.TaskScheduler;
import esa.mo.mc.impl.interfaces.ExternalStatisticFunctionsInterface;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectKeyList;
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
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.statistic.StatisticHelper;
import org.ccsds.moims.mo.mc.statistic.provider.MonitorStatisticsPublisher;
import org.ccsds.moims.mo.mc.statistic.provider.StatisticInheritanceSkeleton;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticCreationRequest;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticCreationRequestList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticEvaluationReport;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticEvaluationReportList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticFunctionDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkDetailsList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkSummary;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkSummaryList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValue;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 * Statistic service Provider.
 */
public class StatisticProviderServiceImpl extends StatisticInheritanceSkeleton {

    private static final Set<Integer> TYPES_ALLOWED_FOR_STATISTIC_EVALUATION = new HashSet<>();
    private MALProvider statisticServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    protected StatisticManager manager;
    private final Object lock = new Object();
    private MonitorStatisticsPublisher publisher;
    private final ConnectionProvider connection = new ConnectionProvider();
    private PeriodicReportingManager periodicReportingManager;
    private PeriodicCollectionManager periodicCollectionManager;
    private PeriodicSamplingManager periodicSamplingManager;
    private final GroupServiceImpl groupService = new GroupServiceImpl();

    static {
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.DOUBLE_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.DURATION_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.FINETIME_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.FLOAT_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.INTEGER_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.LONG_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.OCTET_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.SHORT_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.TIME_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.UINTEGER_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.ULONG_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.UOCTET_TYPE_SHORT_FORM);
        TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.add(Attribute.USHORT_TYPE_SHORT_FORM);
    }

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
            connection.close();
        }

        statisticServiceProvider = connection.startService(StatisticHelper.STATISTIC_SERVICE_NAME.toString(), StatisticHelper.STATISTIC_SERVICE, this);

        running = true;
        manager = new StatisticManager(comServices, parameterManager, statisticFunctions);
        groupService.init(manager.getCOMservices().getArchiveService());

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

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    private void publishStatisticsUpdate(final Long objIdLink, final Long objIdLinkDef, final StatisticValue sVal, final ObjectId source) {
        try {
            synchronized (lock) {
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
            // objIdLink is id of StatisticLink
            // objIdLinkDef is id of StatisticLinkDefinition
            final Long sValObjId = manager.storeAndGenerateStatValueInsobjId(sVal, objIdLinkDef, connection.getConnectionDetails(), source);

            final StatisticCreationRequest statLink = manager.getStatisticLink(objIdLink);
            final Identifier funcName = manager.getStatisticFunction(statLink.getStatFuncInstId()).getName();

            // requirements: 3.6.9.2.a , 3.6.9.2.b , 3.6.9.2.c , 3.6.9.2.d
            final EntityKey ekey = new EntityKey(funcName, objIdLink, statLink.getParameterId().getInstId(), sValObjId);
            final Time timestamp = HelperTime.getTimestampMillis(); //  requirement: 3.6.9.2.e

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            LongList relatedId = new LongList();
            final ObjectIdList sourceId = new ObjectIdList();

            hdrlst.add(new UpdateHeader(timestamp, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
            sourceId.add(source); // requirement: 3.6.9.2.f and 3.6.9.2.g

            StatisticValueList statisticValues = new StatisticValueList();
            statisticValues.add(sVal); // requirement: 3.6.9.2.h

            relatedId.add(objIdLinkDef);

            publisher.publish(hdrlst, relatedId, sourceId, statisticValues);

        } catch (IllegalArgumentException | MALInteractionException | MALException ex) {
            Logger.getLogger(AggregationProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

    @Override
    public StatisticEvaluationReportList getStatistics(LongList funcObjInstIds, Boolean isGroupIds, ObjectKeyList reportInstances, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        LongList paramIdsToBeReported = new LongList();
        LongList linksToBeReported = new LongList();

        if (null == isGroupIds || null == reportInstances) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        //requirement: 3.6.7.2.c, 3.6.7.2.g -  handle the wildcards first 
        //check statistic function ids for wildcard 
        boolean foundStatFuncWildcard = false;
        for (Long funcObjInstId : funcObjInstIds) {
            if (funcObjInstId == 0) { //requirement: 3.6.7.2.b
                foundStatFuncWildcard = true;
                break;
            }
        }

        //check parameter/group identity ids for wildcard
        boolean foundParamWildcard = false;
        for (ObjectKey instance : reportInstances) {
            if (instance.getInstId() == 0) {  // Is it the wildcard '0'? requirement: 3.6.7.2.f
                foundParamWildcard = true;
                break;
            }
        }

        // check if statistic function is known
        if (!foundStatFuncWildcard) {
            for (int index = 0; index < funcObjInstIds.size(); index++) {
                if (manager.getStatisticFunction(funcObjInstIds.get(index)) == null) {
                    unkIndexList.add(new UInteger(index));
                }
            }
        }

        if (unkIndexList.isEmpty()) {
            if (!foundParamWildcard) {
                //the Ids are parameter-identity-ids requirement 3.6.7.2.e
                if (!isGroupIds) {
                    for (int index = 0; index < reportInstances.size(); index++) {
                        ObjectKey reportInstance = reportInstances.get(index);
                        paramIdsToBeReported.add(reportInstance.getInstId());

                        if (!manager.existsParameterIdentity(reportInstance.getInstId())) { // does it exist? 
                            unkIndexList.add(new UInteger(index)); // requirement 3.6.7.2.h
                            continue;
                        }
                    }
                } else {//the ids are group-definition-ids, requirement: 3.6.7.2.d
                    //TODO: sure? groupddefintion or identity-ids? -> issue #135, #179
                    //in the next for loop, ignore the other group definitions, they will be checked in other iterations.
                    LongList ignoreList = new LongList();
                    for (ObjectKey instance : reportInstances) {
                        //instances of different domains are not handled yet
                        ignoreList.add(instance.getInstId());
                    }
                    for (int index = 0; index < reportInstances.size(); index++) {
                        //these are Group-Definition-ids req: 3.9.4.g,h
                        ObjectKey reportInstance = reportInstances.get(index);
                        final Long groupId = reportInstance.getInstId();
                        GroupDetails group = groupService.retrieveGroupDetailsFromArchive(reportInstance.getDomain(), groupId);
                        if (group == null) { //group wasnt found
                            unkIndexList.add(new UInteger(index)); // requirement 3.6.7.2.h
                            continue;
                        } else { //if group was found, then get the instances of it and its groups
                            ignoreList.remove(groupId);
                            GroupServiceImpl.IdObjectTypeList idObjectTypes = groupService.getGroupObjectIdsFromGroup(groupId, group, ignoreList);
                            ignoreList.add(groupId);

                            //checks if the given identityId is found in the internal Parameter-list, if not its not a parameter and invalid
                            for (GroupServiceImpl.IdObjectType idObjectType : idObjectTypes) {
                                if (idObjectType.getObjectType().equals(ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE)) {
                                    final Long identityId = idObjectType.getId();
                                    //checks if the parameterId referenced in the group is known
                                    if (!manager.existsParameterIdentity(identityId)) {// requirement 3.6.7.2.h
                                        unkIndexList.add(new UInteger(index));
                                        break;
                                    }
                                    if (!paramIdsToBeReported.contains(identityId)) {
                                        paramIdsToBeReported.add(identityId);
                                    }
                                } else {
                                    invIndexList.add(new UInteger(index)); // requirement: 3.6.7.2.
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement 3.6.7.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!invIndexList.isEmpty()) {// requirement 3.6.7.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        //find the statLinkIds that are to be reported
        if (foundParamWildcard) {
            if (foundStatFuncWildcard) {
                linksToBeReported.addAll(manager.listAllLinks());
            } else {
                for (Long funcObjInstId : funcObjInstIds) {
                    linksToBeReported.addAll(manager.getStatisticLinksForFunction(funcObjInstId)); //requirement: 3.6.7.2.a
                }
            }
        } else {
            //iterate through each statLink and check if the the wanted combination of statFunc and Param exists
            //only add if it does
            for (Long statLinkId : manager.listAllLinks()) {
                final StatisticCreationRequest statLinkObj = manager.getStatisticLink(statLinkId);
                //statFuncWildCards found
                if (foundStatFuncWildcard) {
                    //add statLinks that use the given parameter
                    for (Long paramId : paramIdsToBeReported) {
                        if (statLinkObj.getParameterId().getInstId().equals(paramId)) {
                            linksToBeReported.add(statLinkId);
                            break;
                        }
                    }
                }
                //no wildcards
                if (!foundStatFuncWildcard) {
                    //using a labelled block here to be more efficient
                    PARAM_STATFUNC_LOOPS:
                    {
                        //add statLinks that use the given parameter and given statLinks
                        for (Long paramId : paramIdsToBeReported) {
                            for (Long statFuncId : funcObjInstIds) {
                                if (statLinkObj.getParameterId().getInstId().equals(paramId) && statLinkObj.getStatFuncInstId().equals(statFuncId)) { //requirement: 3.6.7.2.a, e
                                    linksToBeReported.add(statLinkId);
                                    break PARAM_STATFUNC_LOOPS;
                                }
                            }
                        }
                    }
                }
            }
        }

        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.6.4.o
        StatisticEvaluationReportList evaluationReports = new StatisticEvaluationReportList();

        //create a new EvaluationReport and return it 
        for (Long statLinkId : linksToBeReported) {
            StatisticCreationRequest link = manager.getStatisticLink(statLinkId);
            StatisticValue statValue = generateStatisticValue(statLinkId, link); // requirement: 3.6.7.2.j, l 
            Long statLinkDefId = manager.getStatisticLinkDefinitionId(statLinkId);
            manager.storeAndGenerateStatValueInsobjId(statValue, statLinkDefId, connection.getConnectionDetails(), source);

            if (statValue != null) { //requirement 3.6.7.2.k 
                //add to returned reports
                evaluationReports.add(new StatisticEvaluationReport(statLinkId, statValue)); // requirement: 3.6.7.2.i
            }
        }

        return evaluationReports; //requirement: 3.6.3.a
    }

    /**
     * generates a statistic value but is not saving it
     *
     * @param statLinkId
     * @param link
     * @return
     */
    private StatisticValue generateStatisticValue(Long statLinkId, StatisticCreationRequest link) {
        if (link == null) {
            return null;
        }
        // slice the times and values in order to only use those values gathered during the last collection period
        double oldestTime = System.currentTimeMillis() - link.getLinkDetails().getCollectionInterval().getValue() * 1000.0;
        Integer oldestIndex = manager.getDataSets().getOldestIndex(statLinkId, oldestTime);
        // Retrieve the corresponding data set
        TimeList times = new TimeList();
        AttributeValueList values = new AttributeValueList();
        TimeList allTimes = manager.getDataSets().getTimeSet(statLinkId);
        AttributeValueList allValues = manager.getDataSets().getDataSet(statLinkId);
        if (oldestIndex == null) {
            times = allTimes;
            values = allValues;
        } else {
            List<Time> timesArr = allTimes.subList(oldestIndex, allTimes.size());
            times.addAll(timesArr);
            List<AttributeValue> valuesArr = allValues.subList(oldestIndex, allValues.size());
            values.addAll(valuesArr);
        }
        // Generate the Statistic Report
        return manager.generateStatisticValue(link.getStatFuncInstId(), link.getParameterId().getInstId(), times, values);
    }

    @Override
    public StatisticEvaluationReportList resetEvaluation(Boolean isGroupIds, LongList resetInstances, Boolean returnCurrentEval, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        LongList objIdsToBeReset = new LongList();

        if (null == isGroupIds || null == resetInstances || returnCurrentEval == null) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        boolean foundWildcard = false;

        for (Long instId : resetInstances) {
            if (instId == 0) {  // Is it the wildcard '0'? requirement: 3.6.8.2.c
                objIdsToBeReset.addAll(resetInstances);
                foundWildcard = true;
                break;
            }
        }
        if (!foundWildcard) { // requirement: 3.6.8.2.d

            //the Ids are statistic-function-ids 3.6.8.2.a
            if (!isGroupIds) {
                for (int index = 0; index < resetInstances.size(); index++) {
                    Long statFuncId = resetInstances.get(index);
                    // does function exist? 
                    if (manager.getStatisticFunction(statFuncId) == null) {
                        unkIndexList.add(new UInteger(index)); // requirement: 3.6.8.2.d
                        continue;
                    }
                    //add the statisticLinks that reference to the StatisticFunction to the list of values to be enabled
                    objIdsToBeReset.addAll(manager.getStatisticLinksForFunction(statFuncId)); //requirement: 3.6.8.2.b
                }
            } else {//the ids are group-definition-ids, req: 3.6.8.2.a, 3.9.4.g,h
                //TODO: sure? groupddefintion or identity-ids? -> issue #135, #179
                //in the next for loop, ignore the other group definitions, they will be checked in other iterations.
                LongList ignoreList = new LongList();
                ignoreList.addAll(resetInstances);
                //add all instances referenced in all groups to list of values to be enabled
                for (int index = 0; index < resetInstances.size(); index++) {
                    //these are Group-Definition-ids req: 3.9.4.g,h
                    final Long groupId = resetInstances.get(index);
                    //is group known?
                    GroupDetails group = groupService.retrieveGroupDetailsFromArchive(ConfigurationProviderSingleton.getDomain(), groupId);
                    if (group == null) { //group wasnt found
                        unkIndexList.add(new UInteger(index)); // requirement: 3.6.8.2.d
                        continue;
                    } else { //if group was found, then get the instances of it and its groups
                        ignoreList.remove(groupId);
                        GroupServiceImpl.IdObjectTypeList idObjectTypes = groupService.getGroupObjectIdsFromGroup(groupId, group, ignoreList);
                        ignoreList.add(groupId);

                        //checks if the given identityId is found in the internal StatisticLink-list, if not its not a StatisticLink and invalid
                        for (GroupServiceImpl.IdObjectType idObjectTypePair : idObjectTypes) {
                            if (idObjectTypePair.getObjectType().equals(StatisticHelper.STATISTICLINK_OBJECT_TYPE)) {  //requirement: 3.6.8.2.b
                                final Long statLinkId = idObjectTypePair.getId();
                                //checks if the parameterId referenced in the group is known
                                if (manager.getStatisticLink(statLinkId) == null) {// requirement: 3.6.8.2.d
                                    unkIndexList.add(new UInteger(index));
                                    continue;
                                }
                                if (!objIdsToBeReset.contains(statLinkId)) {
                                    objIdsToBeReset.add(statLinkId);
                                }
                            } else {
                                invIndexList.add(new UInteger(index)); // requirement: 3.6.8.2.e
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.6.12.2.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!invIndexList.isEmpty()) { // requirement: 3.6.12.2.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        // requirement: 3.6.8.2.g (This part of the code is only reached if no error was raised)
        if (returnCurrentEval) { // requirement: 3.6.8.2.f

            ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.6.4.o
            StatisticEvaluationReportList outEvaluations = new StatisticEvaluationReportList();

            //create a new StatisticValue
            for (Long statLinkId : objIdsToBeReset) {
                StatisticCreationRequest link = manager.getStatisticLink(statLinkId);
                StatisticValue statValue = generateStatisticValue(statLinkId, link); // requirement: 3.6.7.2.j, l 
                Long statLinkDefId = manager.getStatisticLinkDefinitionId(statLinkId);
                manager.storeAndGenerateStatValueInsobjId(statValue, statLinkDefId, connection.getConnectionDetails(), source);

                //add to returned reports
                outEvaluations.add(new StatisticEvaluationReport(statLinkId, statValue));
                manager.resetStatisticEvaluationReport(statLinkId);
            }
            return outEvaluations;
        } else { // requirement: 3.6.8.2.f
            for (Long statLinkId : objIdsToBeReset) {
                manager.resetStatisticEvaluationReport(statLinkId);
            }
            return null;
        }

    }

    @Override
    public void enableService(Boolean enableService, MALInteraction interaction) throws MALInteractionException, MALException {
        if (enableService) {
            this.periodicSamplingManager.start();
            this.periodicReportingManager.start(); // requirement: 3.6.10.2.a
            this.periodicCollectionManager.start();
        } else {
            //evalutations and reportings of statistics shall be suspended, no need for further sampling and reporting. 
            this.periodicSamplingManager.pause();
            this.periodicReportingManager.pause(); // requirement: 3.6.10.2.b
            this.periodicCollectionManager.pause();
        }
    }

    @Override
    public Boolean getServiceStatus(MALInteraction interaction) throws MALInteractionException, MALException {
        return this.periodicReportingManager.getStatus();
    }

    @Override
    public void enableReporting(Boolean isGroupIds, InstanceBooleanPairList enableInstances, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (null == isGroupIds || null == enableInstances) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        boolean foundWildcard = false;

        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.6.12.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.6.12.2.c
                // requirement: 3.6.12.2.e and 3.6.12.2.f and 3.6.12.2.j
                manager.setReportingEnabledAll(instance.getValue(), connection.getConnectionDetails());
                //3.6.2.n.b when disabling the periodic statistics reporting, the service shall continue evaluating the parameter statistics -> continue sampling
                periodicReportingManager.refreshAll();
                periodicCollectionManager.refreshAll();
                foundWildcard = true;
                break;
            }
        }
        if (!foundWildcard) { // requirement: 3.6.12.2.d

            //the Ids are statistic-function-ids 3.6.12.2.a
            if (!isGroupIds) {
                for (int index = 0; index < enableInstances.size(); index++) {
                    InstanceBooleanPair enableInstance = enableInstances.get(index);
                    // does function exist? 
                    if (manager.getStatisticFunction(enableInstance.getId()) == null) {
                        unkIndexList.add(new UInteger(index)); // requirement: 3.6.12.2.g
                        continue;
                    }
                    //add the statisticLinks that reference to the StatisticFunction to the list of values to be enabled
                    final LongList statLinksForFunction = manager.getStatisticLinksForFunction(enableInstance.getId());  //requirement: 3.6.12.2.b
                    final Boolean value = enableInstance.getValue();
                    for (Long statLink : statLinksForFunction) {
                        objIdToBeEnabled.add(statLink);
                        valueToBeEnabled.add(value);
                    }
                }
            } else {//the ids are group-definition-ids, req: 3.4.6.12.2.a, 3.9.4.g,h
                //TODO: sure? groupddefintion or identity-ids? -> issue #135, #179
                //in the next for loop, ignore the other group definitions, they will be checked in other iterations.
                LongList ignoreList = new LongList();
                for (InstanceBooleanPair instance : enableInstances) {
                    ignoreList.add(instance.getId());
                }
                //add all instances referenced in all groups to list of values to be enabled
                for (int index = 0; index < enableInstances.size(); index++) {
                    //these are Group-Definition-ids req: 3.9.4.g,h
                    InstanceBooleanPair enableInstance = enableInstances.get(index);
                    final Long groupId = enableInstance.getId();
                    //is group known?
                    GroupDetails group = groupService.retrieveGroupDetailsFromArchive(ConfigurationProviderSingleton.getDomain(), groupId);
                    if (group == null) { //group wasnt found
                        unkIndexList.add(new UInteger(index)); // requirement: 3.3.10.2.g
                        continue;
                    } else { //if group was found, then get the instances of it and its groups
                        ignoreList.remove(groupId);
                        GroupServiceImpl.IdObjectTypeList idObjectTypes = groupService.getGroupObjectIdsFromGroup(groupId, group, ignoreList);
                        ignoreList.add(groupId);

                        //checks if the given identityId is found in the internal StatisticLink-list, if not its not a StatisticLink and invalid
                        for (GroupServiceImpl.IdObjectType idObjectTypePair : idObjectTypes) {
                            if (idObjectTypePair.getObjectType().equals(StatisticHelper.STATISTICLINK_OBJECT_TYPE)) {  //requirement: 3.6.12.2.b
                                final Long statLinkId = idObjectTypePair.getId();
                                //checks if the parameterId referenced in the group is known
                                if (manager.getStatisticLink(statLinkId) == null) {// requirement: 3.6.12.2.g
                                    unkIndexList.add(new UInteger(index));
                                    continue;
                                }
                                if (!objIdToBeEnabled.contains(statLinkId)) {
                                    objIdToBeEnabled.add(statLinkId);
                                    valueToBeEnabled.add(enableInstance.getValue());
                                }
                            } else {
                                invIndexList.add(new UInteger(index)); // requirement: 3.6.12.2.h
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.6.12.2.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!invIndexList.isEmpty()) { // requirement: 3.6.12.2.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        // requirement: 3.6.12.2.i (This part of the code is only reached if no error was raised)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            final Long changedId = objIdToBeEnabled.get(index);
            // requirement: 3.6.12.2.e and 3.6.12.2.f and 3.6.12.2.j
            manager.setReportingEnabled(changedId, valueToBeEnabled.get(index), connection.getConnectionDetails());
            //3.6.2.n.b when disabling the periodic statistics reporting, the service shall continue evaluating the parameter statistics -> continue sampling by not refreshing the SampleManager
            periodicReportingManager.refreshAndReport(changedId);
            periodicCollectionManager.refresh(changedId);
        }

    }

    @Override
    public ObjectInstancePairList addParameterEvaluation(StatisticCreationRequestList newDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList invIndexList = new UIntegerList();
        UIntegerList unkIndexList = new UIntegerList();

        if (null == newDetails) { // Is the input null?
            throw new IllegalArgumentException("newDetails argument must not be null");
        }

        for (int index = 0; index < newDetails.size(); index++) { // requirement: 3.6.14.2.n (incremental "for cycle" guarantees that)
            StatisticCreationRequest statDefDetail = newDetails.get(index); // requirement: 3.6.14.2.a

            // Get the StatisticFunction
            final StatisticFunctionDetails statFunc = manager.getStatisticFunction(statDefDetail.getStatFuncInstId());  // requirement: 3.6.14.2.b

            if (statFunc == null) { // requirement: 3.6.14.2.c
                unkIndexList.add(new UInteger(index));
                continue;
            }

            // Check if the parameterId statLinkExists
            final ParameterDefinitionDetails parameterDef = manager.getParameterDefinition(statDefDetail.getParameterId().getInstId()); // requirement: 3.6.14.2.d

            if (parameterDef == null) { // requirement: 3.6.14.2.e
                unkIndexList.add(new UInteger(index));
                continue;
            }

            // Check that statistical function can be applied to type of parameter
            int paramType;
            if (statDefDetail.getLinkDetails().getUseConverted()) {
                if (parameterDef.getConversion() == null) {
                    invIndexList.add(new UInteger(index));
                    continue;
                }
                paramType = parameterDef.getConversion().getConvertedType().intValue();
            } else {
                paramType = parameterDef.getRawType().intValue();
            }
            if (!TYPES_ALLOWED_FOR_STATISTIC_EVALUATION.contains(paramType)) { // requirement: 3.6.14.2.f
                invIndexList.add(new UInteger(index));
                continue;
            }

            final double samplingInterval = statDefDetail.getLinkDetails().getSamplingInterval().getValue(); // requirement: 3.6.14.2.g

            //requrirement: 3.6.3.h: TODO: create a method that allows to retrieve the allowed sampling intervals by a "sampleIntervals"-provider
            // hardcode invalid sample rates of 1000.0s and 50.1s - we need to finish this quickly!
            if (samplingInterval < 0 || samplingInterval == 1000.0 || samplingInterval == 50.1) { // requirement: 3.6.14.2.h
                invIndexList.add(new UInteger(index));
                continue;
            }

        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.6.14.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.6.14.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // If no errors, then add!  // requirement: 3.6.14.2.i
        ObjectInstancePairList oipl = new ObjectInstancePairList();
        LongList statLinkIds = new LongList();
        for (StatisticCreationRequest statDetails : newDetails) {  // requirement: 3.6.14.2.j, m
            ObjectInstancePair newLink = manager.add(statDetails, connection.getConnectionDetails());
            oipl.add(newLink); // requirement: 3.6.14.2.l, 3.6.14.2.a
            statLinkIds.add(newLink.getObjIdentityInstanceId());
        }

        this.periodicSamplingManager.refreshList(statLinkIds); // requirement: 3.6.14.2.k
        this.periodicReportingManager.refreshList(statLinkIds);
        this.periodicCollectionManager.refreshList(statLinkIds);

        return oipl; // requirement: 3.6.14.2.l

    }

    @Override
    public LongList updateParameterEvaluation(LongList objInstIds, StatisticLinkDetailsList newDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList invIndexList = new UIntegerList();
        UIntegerList unkIndexList = new UIntegerList();
        StatisticLinkDetails statLink;
        Long linkId;

        if (null == newDetails || objInstIds == null) { // Is the input null?
            throw new IllegalArgumentException("newDetails argument must not be null");
        }

        if (objInstIds.size() != newDetails.size()) { // requirement: 3.6.1.5.2.g
            int min = (objInstIds.size() < newDetails.size()) ? objInstIds.size() : newDetails.size();
            int max = (objInstIds.size() > newDetails.size()) ? objInstIds.size() : newDetails.size();

            for (int i = min; i < max; i++) {
                invIndexList.add(new UInteger(i));
            }
        } else {
            for (int index = 0; index < newDetails.size(); index++) { // requirement: 3.6.15.2.c
                statLink = newDetails.get(index); // requirement: 3.6.15.2.a
                linkId = objInstIds.get(index);

                final double samplingInterval = statLink.getSamplingInterval().getValue();

                // hardcode invalid sample rates of 1000.0s and 50.1s - we need to finish this quickly!
                if (linkId == 0 || linkId == null || samplingInterval < 0 || samplingInterval == 1000.0 || samplingInterval == 50.1) { // requirement: 3.6.15.2.b. 3.6.15.2.d
                    invIndexList.add(new UInteger(index));
                    continue;
                }
                // Get the Statistic Link
                final StatisticCreationRequest oldLink = manager.getStatisticLink(linkId);

                if (oldLink == null) { // requirement: 3.6.15.2.c 
                    unkIndexList.add(new UInteger(index));
                    continue;
                }
            }
        }
        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.6.15.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.6.15.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        LongList newDefIds = new LongList();
        // If no errors, then update!  // requirement: 3.6.15.2.h
        for (int index = 0; index < newDetails.size(); index++) { // requirement: 3.6.15.2.f, 3.6.15.2.l
            linkId = objInstIds.get(index);  // requirement: 3.6.15.2.a

            // Get the Statistic Link
            StatisticCreationRequest editLink = manager.getStatisticLink(linkId);
            editLink.setLinkDetails(newDetails.get(index)); // requirement: 3.6.15.2.e
            Long newLinkDefId = manager.update(linkId, editLink, connection.getConnectionDetails()); // requirement: 3.6.15.2.i

            // Refresh the sampling
            this.periodicSamplingManager.update(linkId); // requirement: 3.6.15.2.j
            this.periodicCollectionManager.refresh(linkId);
            this.periodicReportingManager.refresh(linkId);
            newDefIds.add(newLinkDefId);
        }

        return newDefIds;
    }

    @Override
    public void removeParameterEvaluation(LongList objInstIds, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempIdsToRemoveLst = new LongList();

        if (null == objInstIds) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < objInstIds.size(); index++) {
            tempLong = objInstIds.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.6.15.2.b
                tempIdsToRemoveLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempIdsToRemoveLst.addAll(manager.listAllLinks()); // ... add all in a row
                break;
            }

            if (manager.statLinkExists(tempLong)) { // Does it match an existing definition?
                tempIdsToRemoveLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.6.15.2.c
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.6.15.3 (error: a, b)
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.6.15.3.e (Inserting the errors before this line guarantees that the requirement is met)
        for (Long toDelete : tempIdsToRemoveLst) {
            manager.delete(toDelete); // requirement: 3.6.15.2.a, COM archive is left untouched: 3.6.15.2.d
        }

        //  requirement: 3.6.15.2.f
        periodicReportingManager.refreshList(tempIdsToRemoveLst); // Refresh the Periodic Reporting Manager for the removed Definitions
        periodicCollectionManager.refreshList(tempIdsToRemoveLst);
        periodicSamplingManager.refreshList(tempIdsToRemoveLst);

    }

    @Override
    public StatisticLinkSummaryList listParameterEvaluations(LongList statObjInstId, MALInteraction interaction) throws MALInteractionException, MALException {
        LongList statLinkIds = new LongList();
        UIntegerList unkIndexList = new UIntegerList();

        if (null == statObjInstId) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int i = 0; i < statObjInstId.size(); i++) {
            Long statFuncId = statObjInstId.get(i);
            // requirement: 3.6.13.2.b, c Check for wildcards
            if (statFuncId == 0L) {
                statLinkIds.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                statLinkIds.addAll(manager.listAllLinks()); // ... add all in a row
                unkIndexList.clear();
                break;
            }

            if (manager.getStatisticFunction(statFuncId) == null) {  //requirement: 3.6.13.2.d
                unkIndexList.add(new UInteger(i));
                continue;
            }

            statLinkIds.addAll(manager.getStatisticLinksForFunction(statFuncId));
        }
        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.6.13.3.1 (error: a and b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        StatisticLinkSummaryList statLinkSummaries = new StatisticLinkSummaryList(statLinkIds.size());
        for (Long statLinkId : statLinkIds) {
            //requirement: 3.6.13.2.e
            StatisticCreationRequest statLink = manager.getStatisticLink(statLinkId);
            final Long statFuncId = statLink.getStatFuncInstId();
            final Long statLinkDefId = manager.getStatisticLinkDefinitionId(statLinkId);
            final boolean reportingEnabled = statLink.getLinkDetails().getReportingEnabled();
            final ObjectKey paramKey = statLink.getParameterId();
            statLinkSummaries.add(new StatisticLinkSummary(statFuncId, statLinkId, statLinkDefId, reportingEnabled, paramKey));
        }

        return statLinkSummaries;
    }

    private static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties) throws MALException {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties) throws MALException {
            Logger.getLogger(StatisticProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterErrorReceived");
        }

    }

    private class PeriodicSamplingManager { // requirement: 3.7.2.1a

        /**
         * Key: the id of the ParameterIdentity, Value: the
         * sample-reporting-timer
         */
        private HashMap<Long, TaskScheduler> sampleTimerList; // Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicSamplingManager() {
            sampleTimerList = new HashMap<>();
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
            this.refreshAll(); // Refresh all the Statistic Definitions on the Manager
            this.start(); // set active flag to true
        }

        private void removePeriodicReporting(Long identityId) {
            this.stopTimer(identityId);
            sampleTimerList.remove(identityId);
            manager.getDataSets().removeDataSet(identityId);
        }

        /**
         * necessary if statisticLink is updated
         *
         * @param identityId
         */
        public void update(Long identityId) {
            if (sampleTimerList.containsKey(identityId)) { // Does it exist in the PeriodicSamplingManager?
                removePeriodicReporting(identityId);
            }
            if (manager.statLinkExists(identityId)) { // Does it exist anymore?
                this.addPeriodicSampling(identityId);
            }
        }

        /**
         * necessary for starting and removing the sampling
         *
         * @param identityId
         */
        public void refresh(Long identityId) {
            //do the sampling from the first enabling to the removal of the statisticLink
            if (sampleTimerList.containsKey(identityId)) { // Does it exist in the PeriodicSamplingManager?
                if (!manager.statLinkExists(identityId)) //Its in the list but doesnt exist anymore? -> remove 
                {
                    removePeriodicReporting(identityId);
                }
                return;
            }
            if (manager.statLinkExists(identityId)) { // Does it exist?
                this.addPeriodicSampling(identityId);
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
            final StatisticCreationRequest statLink = manager.getStatisticLink(identityId);

            //requirement 3.6.2.n.b: Periodic Sampling shall not occur if the generation was disabled. So if it was never enabled nothing happens. If is was already enabled it should not even reach this code as the refresh-method will not be called in the enabledGeneration-operation
            //if (!statLink.getLinkDetails().getReportingEnabled()) {
            //	return;
            //}
            // Add to the Periodic Sampling Manager only if there's a sampleInterval selected for the parameterSet
            // NOTE: The standard says its "perfectly possible" to set a sampleInterval greater than a reporting or collection interval so no other checks of the sampleinterval necessary here
            Duration sampleInterval = statLink.getLinkDetails().getSamplingInterval();
            if (sampleInterval.getValue() != 0) {
                TaskScheduler timer = new TaskScheduler(1, true);  // Take care of adding a new timer
                sampleTimerList.put(identityId, timer);
                startTimer(identityId, sampleInterval, statLink.getLinkDetails().getUseConverted());
            }

        }

        private void startTimer(final Long identityId, final Duration interval, final boolean useConverted) {  // requirement: 3.6.2.g
            // the time has to be converted to milliseconds by multiplying by 1000
            sampleTimerList.get(identityId).scheduleTask(new Thread(() -> { // Periodic sampling
                if (active) {
                    sampleParamValue(identityId, useConverted);
                }
            }), 0, (int) (interval.getValue() * 1000), TimeUnit.MILLISECONDS, true); // requirement: 3.6.2.g
        }

        private void sampleParamValue(final Long identityId, final boolean useConverted) {
            ParameterValue pVal;
            try {
                pVal = manager.getParameterValue(identityId); // Get the Parameter Value of the parameter referenced in the statLink
                // Add the value to the data set
                manager.getDataSets().addAttributeToDataSet(identityId, useConverted ? pVal.getConvertedValue() : pVal.getRawValue(), HelperTime.getTimestampMillis());
            } catch (MALInteractionException ex) {
                manager.getDataSets().addAttributeToDataSet(identityId, null, HelperTime.getTimestampMillis());
            }
            generateAndAddStatisticEvaluationReport(identityId);
        }

        private void generateAndAddStatisticEvaluationReport(final Long statLinkId) {
            // Retrieve the Statistic Link
            StatisticCreationRequest link = manager.getStatisticLink(statLinkId);

            // Retrieve the corresponding data set
            StatisticValue statValue = generateStatisticValue(statLinkId, link);
            manager.addStatisticValueToStatisticEvaluationReport(statLinkId, statValue);
        }

        private void stopTimer(Long objId) {
            sampleTimerList.get(objId).stopLast();
        }

    }

    private class PeriodicCollectionManager {

        private HashMap<Long, TaskScheduler> collectionTimerList; // Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicCollectionManager() {
            collectionTimerList = new HashMap<>();
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
            final StatisticCreationRequest statLink = manager.getStatisticLink(objId);

            if (!statLink.getLinkDetails().getReportingEnabled()) {
                return; // Periodic Sampling shall not occur if the generation is not enabled at the definition level
            }

            Duration collectionInterval = statLink.getLinkDetails().getCollectionInterval();

            // Add to the Periodic Collection Manager
            if (collectionInterval.getValue() != 0) {
                TaskScheduler timer = new TaskScheduler(1, true);  // Take care of adding a new timer
                collectionTimerList.put(objId, timer);
                startTimer(objId, collectionInterval);
            }

        }

        private void removePeriodicCollecting(Long objId) {
            this.stopTimer(objId);
            collectionTimerList.remove(objId);
        }

        private void startTimer(final Long statLinkId, Duration interval) {

            collectionTimerList.get(statLinkId).scheduleTask(new Thread() {
                @Override
                public void run() { // Periodic sampling
                    if (active) {
                        manager.getDataSets().lock(); // Lock the Sets
                        StatisticCreationRequest link = manager.getStatisticLink(statLinkId);

                        //publish if the intervals dont align
                        if (link.getLinkDetails().getCollectionInterval().getValue() % link.getLinkDetails().getReportingInterval().getValue() != 0) //requirement: 3.6.3.e
                        {
                            // Retrieve the Statistic Link
                            generateAndAddStatisticEvaluationReport(link);
                            Long statLinkDefId = manager.getStatisticLinkDefinitionId(statLinkId);
                            publishStatisticsUpdate(statLinkId, statLinkDefId, manager.getStatisticEvaluationReport(statLinkId).getValue(), null);
                        }

                        // Reset the evaluations
                        if (link.getLinkDetails().getResetEveryCollection()) {
                            manager.getDataSets().resetDataSet(statLinkId); // requirement: 3.6.2.f
                        }
                        manager.getDataSets().unlock(); // Unlock the Sets
                    }
                }

                private void generateAndAddStatisticEvaluationReport(StatisticCreationRequest link) {
                    // Retrieve the corresponding data set
                    StatisticValue statValue = generateStatisticValue(statLinkId, link);
                    manager.addStatisticValueToStatisticEvaluationReport(statLinkId, statValue);
                }

                // the time has to be converted to milliseconds by multiplying by 1000
            }, (int) (interval.getValue() * 1000), (int) (interval.getValue() * 1000), TimeUnit.MILLISECONDS, true);
        }

        private void stopTimer(Long objId) {
            collectionTimerList.get(objId).stopLast();
        }

    }

    private class PeriodicReportingManager { // requirement: 3.7.2.1a

        private HashMap<Long, TaskScheduler> updateTimerList; // updateInterval Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicReportingManager() {
            updateTimerList = new HashMap<>();
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

        public void refreshAndReport(Long objId) {
            refresh(objId, true);
        }

        public void refresh(Long objId) {
            refresh(objId, false);
        }

        private void refresh(Long objId, boolean immediateReport) {
            if (updateTimerList.containsKey(objId)) { // Does it exist in the Periodic Reporting Manager?
                this.removePeriodicReporting(objId);
            }
            // getStatisticLink aggregation definition
            StatisticCreationRequest statLink = manager.getStatisticLink(objId);
            if (statLink != null) { // Does it exist in the Statistic Links List?
                if (statLink.getLinkDetails().getReportingInterval().getValue() != 0 //requirement: 3.6.3.c
                        && statLink.getLinkDetails().getReportingEnabled()) { // Is the periodic reporting active?
                    this.addPeriodicReporting(objId, immediateReport);
                }
            }
//TODO: needed here?
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

        private void addPeriodicReporting(Long statLinkId, boolean immediateReport) {
            TaskScheduler timer = new TaskScheduler(1, true);
            updateTimerList.put(statLinkId, timer);
            this.startReportingTimer(statLinkId, manager.getStatisticLink(statLinkId).getLinkDetails().getReportingInterval(), immediateReport); //requirement: 3.6.2.h, 3.6.3.b
        }

        private void removePeriodicReporting(Long objId) {
            this.stopUpdatesTimer(objId);
            updateTimerList.remove(objId);
        }

        private void startReportingTimer(final Long statLinkId, final Duration interval, boolean immediateReport) {
            updateTimerList.get(statLinkId).scheduleTask(new Thread(() -> {  //requirement: 3.6.2.h, 3.6.3.b
                if (active) {
                    reportStatistic(statLinkId);
                }
            }), immediateReport ? 0 : (int) (interval.getValue() * 1000), (int) (interval.getValue() * 1000),
            TimeUnit.MILLISECONDS, true); //requirement: 3.6.2.h, 3.6.3.b
        }

        private void reportStatistic(final Long statLinkId) {
            StatisticEvaluationReport report = manager.getStatisticEvaluationReport(statLinkId);
            if (report != null) {
                Long statLinkDefId = manager.getStatisticLinkDefinitionId(statLinkId);
                publishStatisticsUpdate(statLinkId, statLinkDefId, manager.getStatisticEvaluationReport(statLinkId).getValue(), null);
            }
//            }
            //TODO: Reset at every reporting Interval, really? then we dont have to save it in the first place a couple of lines above
//            if (true) {  // Reset at every reporting Interval
//                manager.resetStatisticEvaluationReport(statLinkId);
//            }
        }

        private void stopUpdatesTimer(final Long objId) {
            updateTimerList.get(objId).stopLast();
        }

    }
}
