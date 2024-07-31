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

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.misc.TaskScheduler;
import esa.mo.mc.impl.provider.check.CheckLinkMonitorManager;
import esa.mo.mc.impl.provider.check.ParameterMonitoringManager;
import esa.mo.mc.impl.util.GroupRetrieval;
import esa.mo.mc.impl.util.MCServicesConsumer;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.check.CheckHelper;
import org.ccsds.moims.mo.mc.check.provider.CheckInheritanceSkeleton;
import org.ccsds.moims.mo.mc.check.provider.GetCurrentTransitionListInteraction;
import org.ccsds.moims.mo.mc.check.provider.GetSummaryReportInteraction;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetailsList;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetailsList;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkSummary;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkSummaryList;
import org.ccsds.moims.mo.mc.check.structures.CheckResultFilter;
import org.ccsds.moims.mo.mc.check.structures.CheckResultSummary;
import org.ccsds.moims.mo.mc.check.structures.CheckResultSummaryList;
import org.ccsds.moims.mo.mc.check.structures.CheckTypedInstance;
import org.ccsds.moims.mo.mc.check.structures.CheckTypedInstanceList;
import org.ccsds.moims.mo.mc.check.structures.CompoundCheckDefinition;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 * Check service Provider.
 */
public class CheckProviderServiceImpl extends CheckInheritanceSkeleton {

    private MALProvider checkServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private CheckManager manager;
    private ParameterMonitoringManager paramMonitorManager;
    protected CheckLinkMonitorManager checkLinkMonitorManager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private PeriodicReportingMaxManager periodicReportingMaxManager;
    private PeriodicCheckingManager periodicCheckingManager;
    private ParameterManager parameterManager;
    private EventConsumerServiceImpl eventServiceConsumer;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param mcServicesConsumer
     * @param parameterManager
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, MCServicesConsumer mcServicesConsumer,
            ParameterManager parameterManager) throws MALException {
        long timestamp = System.currentTimeMillis();

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

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION).getServiceByName(
                CheckHelper.CHECK_SERVICE_NAME) == null) {
                CheckHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
        }

        // Shut down old service transport
        if (null != checkServiceProvider) {
            connection.close();
        }

        checkServiceProvider = connection.startService(CheckHelper.CHECK_SERVICE_NAME.toString(),
            CheckHelper.CHECK_SERVICE, this);

        running = true;
        this.parameterManager = parameterManager;
        manager = new CheckManager(comServices, parameterManager);
        paramMonitorManager = new ParameterMonitoringManager(manager, mcServicesConsumer, parameterManager);

        try {  // Consumer of Events for the CompoundChecks
            if (manager.getEventService() != null) { // Do we have the Event service?
                eventServiceConsumer = new EventConsumerServiceImpl(manager.getEventService().getConnectionProvider()
                    .getConnectionDetails());
                checkLinkMonitorManager = new CheckLinkMonitorManager(eventServiceConsumer.getEventStub(), manager);
            }

        } catch (MALException | MalformedURLException | MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        periodicReportingMaxManager = new PeriodicReportingMaxManager();
        periodicCheckingManager = new PeriodicCheckingManager();
        periodicReportingMaxManager.init(); // Initialize the Periodic Reporting Manager
        periodicCheckingManager.init(); // Initialize the Periodic Sampling Manager

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).info(
                "Check service READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != checkServiceProvider) {
                checkServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.WARNING,
                "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public void getCurrentTransitionList(CheckResultFilter filter, GetCurrentTransitionListInteraction interaction)
        throws MALInteractionException, MALException {
        // extract all Checks and Parameters to filter on
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        LongList checksToFilterFor = new LongList();
        LongList parametersToFilterFor = new LongList();

        //requirement: 3.5.8.2.d, e, implicitly 3.5.8.2.q: check for wildcard in check-ids 
        boolean foundWildcardCheck = false;
        for (Long checkToFilterFor : filter.getCheckFilter()) {
            if (checkToFilterFor == 0) {  // Is it the wildcard '0'? 
                checksToFilterFor.addAll(manager.listAllIdentities());
                foundWildcardCheck = true;
                break;
            }
        }
        //requirement: 3.5.8.2.h, i, implicitly 3.5.8.2.q: check for wildcard in parameter-ids 
        boolean foundWildcardParam = false;
        for (Long paramToFilterFor : filter.getParameterFilter()) {
            if (paramToFilterFor == 0) {  // Is it the wildcard '0'? 
                parametersToFilterFor.addAll(parameterManager.listAllIdentities());
                foundWildcardParam = true;
                break;
            }
        }

        //retrieve all check-ids to filter for
        if (!foundWildcardCheck) { // requirement: 3.5.8.2.e
            //the Ids are check-identity-ids 3.5.8.2.b
            if (!filter.getCheckFilterViaGroups()) {
                for (int index = 0; index < filter.getCheckFilter().size(); index++) {
                    Long checkToFilterFor = filter.getCheckFilter().get(index);
                    if (!manager.existsIdentity(checkToFilterFor)) { // does it exist? //requirement: 3.5.8.2.c
                        unkIndexList.add(new UInteger(index)); // requirement: 3.5.8.2.l
                        continue;
                    }
                    checksToFilterFor.add(checkToFilterFor);
                }
            } else { //the ids are group-identity-ids, req: 3.5.8.2.b, 3.9.4.g,h
                GroupRetrieval groupRetrievalInformation;
                groupRetrievalInformation = new GroupRetrieval(unkIndexList, invIndexList, checksToFilterFor,
                    new BooleanList());
                //get the group instances
                // requirement: 3.5.8.2.j, k, l
                groupRetrievalInformation = manager.getGroupInstancesForServiceOperation(filter.getCheckFilter(),
                    groupRetrievalInformation, CheckHelper.CHECKIDENTITY_OBJECT_TYPE, ConfigurationProviderSingleton
                        .getDomain(), manager.listAllIdentities());

                //fill the existing lists with the generated lists
                unkIndexList = groupRetrievalInformation.getUnkIndexList();
                invIndexList = groupRetrievalInformation.getInvIndexList();
                checksToFilterFor = groupRetrievalInformation.getObjIdToBeEnabled();
            }
        }
        //retrieve all parameter-ids to filter for        
        if (!foundWildcardParam) { // requirement: 3.5.8.2.i
            //if the Ids are parameter-identity-ids 3.5.8.2.f
            if (!filter.getParameterFilterViaGroups()) {
                for (int index = 0; index < filter.getParameterFilter().size(); index++) {
                    Long paramToFilterFor = filter.getParameterFilter().get(index);
                    if (!parameterManager.existsIdentity(paramToFilterFor)) { // does it exist? //requirement: 3.5.8.2.i
                        unkIndexList.add(new UInteger(index)); // requirement: 3.5.8.2.m
                        continue;
                    }
                    parametersToFilterFor.add(paramToFilterFor);
                }
            } else { //the ids are group-identity-ids, req: 3.5.8.2.f, 3.9.4.g,h
                GroupRetrieval groupRetrievalInformation;
                groupRetrievalInformation = new GroupRetrieval(unkIndexList, invIndexList, parametersToFilterFor,
                    new BooleanList()); //the boolean values can be ignored here, as we only want to have all referenced identityids
                //get the group instances
                // requirement: 3.5.8.2.j, k, m
                groupRetrievalInformation = manager.getGroupInstancesForServiceOperation(filter.getParameterFilter(),
                    groupRetrievalInformation, ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(), parameterManager.listAllIdentities());

                //fill the existing lists with the generated lists
                unkIndexList = groupRetrievalInformation.getUnkIndexList();
                invIndexList = groupRetrievalInformation.getInvIndexList();
                parametersToFilterFor = groupRetrievalInformation.getObjIdToBeEnabled();
            }
        }
        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.5.12.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!invIndexList.isEmpty()) { // requirement: 3.5.12.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        //operation call received
        interaction.sendAcknowledgement();

        // Get all the available CheckResults
        CheckResultSummaryList checkSummaries = manager.getAllCheckSummaries();

        //requirement: 3.5.8.2.s
        //requirement: 3.5.8.2.t size of updates is implementation specific
        //cut into slices for the single update-messages
        final int summaryPartSize = 10;
        final int checkSummaryParts = (checkSummaries.size() % summaryPartSize == 0 ? checkSummaries.size() /
            summaryPartSize : checkSummaries.size() / summaryPartSize + 1);

        //apply filter and send the single parts of the summary-report as update-messages and a response-message
        for (int i = 0; i < checkSummaryParts; i++) {
            //requirement: 3.5.8.2.a, n, o, p, r
            final List<CheckResultSummary> oneUpdateSlice = checkSummaries.subList(i * summaryPartSize, ((i + 1) *
                summaryPartSize < checkSummaries.size() ? (i + 1) * summaryPartSize : checkSummaries.size()));
            CheckResultSummaryList sendCheckSummaries = manager.applyFilter(oneUpdateSlice, checksToFilterFor,
                parametersToFilterFor, filter.getStateFilter());

            if (i < checkSummaryParts - 1) {
                interaction.sendUpdate(sendCheckSummaries);
            } else {
                interaction.sendResponse(sendCheckSummaries);
            }
        }
    }

    @Override
    public void getSummaryReport(LongList checkIdentityIds, GetSummaryReportInteraction interaction)
        throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();

        //requirement: 3.5.9.2.b, c "wildcard"-handling
        boolean wildcardFound = false;
        for (int i = 0; i < checkIdentityIds.size(); i++) {
            if (checkIdentityIds.get(i) == 0L) {
                checkIdentityIds.clear();
                checkIdentityIds.addAll(manager.listAllIdentities());
                wildcardFound = true;
                break;
            }
        }
        if (!wildcardFound) {
            for (int i = 0; i < checkIdentityIds.size(); i++) {
                //requirement: 3.5.9.2. d
                if (!manager.existsIdentity(checkIdentityIds.get(i))) {
                    unkIndexList.add(new UInteger(i));
                }

            }
        }
        //ERROR
        //returning the error without sending any result, just send an ackknowledgement error and end.
        if (!unkIndexList.isEmpty()) {  //requirement: 3.5.9.3.1
            interaction.sendError(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            return;
        }
        interaction.sendAcknowledgement();

        //requirement: 3.5.9.2.d
        for (int i = 0; i < checkIdentityIds.size(); i++) {
            //requirement: 3.5.9.2.e, f, g
            if (i < checkIdentityIds.size() - 1) {
                interaction.sendUpdate(checkIdentityIds.get(i), manager.getCheckSummaries(checkIdentityIds.get(i)));
            } else {
                interaction.sendResponse(checkIdentityIds.get(i), manager.getCheckSummaries(checkIdentityIds.get(i)));
            }
        }
    }

    @Override
    public void enableService(Boolean enableService, MALInteraction interaction) throws MALInteractionException,
        MALException {
        //requirement: 3.5.3.a, 3.5.10.2.c
        if (manager.getCheckServiceEnabled() == enableService) {
            return;
        }

        //requirement: 3.5.10.2.a, b
        manager.setCheckServiceEnabled(enableService);
        if (!enableService) {
            //requirement: 3.5.3.b
            periodicReportingMaxManager.pause();
            periodicCheckingManager.pause();
            paramMonitorManager.pauseAll();
        } else {
            //requirement: 3.5.3.c
            periodicReportingMaxManager.start();
            periodicCheckingManager.start();
            paramMonitorManager.startAll();
        }
    }

    @Override
    public Boolean getServiceStatus(MALInteraction interaction) throws MALInteractionException, MALException {
        //requirement: 3.5.11.2
        return manager.getCheckServiceEnabled();
    }

    @Override
    public void enableCheck(Boolean isGroupIds, InstanceBooleanPairList enableInstances, MALInteraction interaction)
        throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        InstanceBooleanPair enableInstance;

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (null == isGroupIds || null == enableInstances) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        boolean foundWildcard = false;

        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.5.12.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.5.12.2.c
                objIdToBeEnabled.clear();
                objIdToBeEnabled.addAll(manager.listAllCheckLinkIds());
                valueToBeEnabled.clear();
                boolean valueToSet = instance.getValue();
                // set all values to this value
                for (int i = 0; i < objIdToBeEnabled.size(); i++) {
                    valueToBeEnabled.add(valueToSet);
                }
                foundWildcard = true;
                break;
            }
        }

        if (!foundWildcard) { // requirement: 3.5.12.2.d
            //the Ids are checkLink-identity-ids 3.5.12.2.a
            if (!isGroupIds) {
                for (int index = 0; index < enableInstances.size(); index++) {
                    enableInstance = enableInstances.get(index);
                    objIdToBeEnabled.add(enableInstance.getId());
                    valueToBeEnabled.add(enableInstance.getValue());

                    if (!manager.listAllCheckLinkIds().contains(enableInstance.getId())) { // does it exist? //requirement: 3.5.12.2.b
                        unkIndexList.add(new UInteger(index)); // requirement: 3.5.12.2.g
                    }
                }
            } else { //the ids are group-identity-ids, req: 3.5.12.2.a, 3.9.4.g,h
                GroupRetrieval groupRetrievalInformation;
                groupRetrievalInformation = new GroupRetrieval(unkIndexList, invIndexList, objIdToBeEnabled,
                    valueToBeEnabled);
                //get the group instances
                groupRetrievalInformation = manager.getGroupInstancesForServiceOperation(enableInstances,
                    groupRetrievalInformation, CheckHelper.CHECKLINK_OBJECT_TYPE, ConfigurationProviderSingleton
                        .getDomain(), manager.listAllCheckLinkIds());

                //fill the existing lists with the generated lists
                unkIndexList = groupRetrievalInformation.getUnkIndexList();
                invIndexList = groupRetrievalInformation.getInvIndexList();
                objIdToBeEnabled = groupRetrievalInformation.getObjIdToBeEnabled();
                valueToBeEnabled = groupRetrievalInformation.getValueToBeEnabled();
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.5.12.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!invIndexList.isEmpty()) { // requirement: 3.5.12.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        // requirement: 3.5.12.2.i (This part of the code is not reached if an error is thrown)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            // requirement: 3.5.12.2.e and 3.5.12.2.f and 3.5.12.2.j and 3.5.3.g
            manager.setCheckEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), connection
                .getConnectionDetails());
            periodicReportingMaxManager.refresh(objIdToBeEnabled.get(index));
            periodicCheckingManager.refresh(objIdToBeEnabled.get(index));
            if (valueToBeEnabled.get(index)) {
                //add the check links parameter to the moniored ones, to notice when the value changes
                paramMonitorManager.add(objIdToBeEnabled.get(index));
            } else {
                paramMonitorManager.remove(objIdToBeEnabled.get(index));
            }
        }

    }

    @Override
    public void triggerCheck(LongList checkIdentityIds, LongList checkLinkIds, MALInteraction interaction)
        throws MALInteractionException, MALException {
        //requirement: 3.5.10.2.b
        if (!manager.getCheckServiceEnabled()) {
            return;
        }

        UIntegerList unkIndexList = new UIntegerList();

        // Get the list of all the objIds of the checkLinks
        LongList linksToBeTriggered = new LongList();
        boolean wildcardFound = false;

        // get the checkLinks that reference the given identites
        for (int i = 0; i < checkIdentityIds.size(); i++) {
            Long checkIdentityId = checkIdentityIds.get(i);
            //requirement 3.5.13.2.c,d  wildcard found?
            if (checkIdentityId == 0L) {
                linksToBeTriggered.clear();
                linksToBeTriggered.addAll(manager.listAllCheckLinkIds());
                unkIndexList.clear();
                wildcardFound = true;
                break;
            }

            //requirement: 3.15.2.e
            if (!manager.existsIdentity(checkIdentityId)) {
                unkIndexList.add(new UInteger(i));
                continue;
            }

            //requirement 3.5.13.2.a
            linksToBeTriggered.addAll(manager.findLinksPointingToCheck(checkIdentityId));

        }

        // Check if the given checkLinks are already in the linksToBeTriggered-list or add if not
        if (!wildcardFound) {
            for (int i = 0; i < checkLinkIds.size(); i++) {
                long checkLinkId = checkLinkIds.get(i);
                //requirement 3.5.13.2.c, d wildcard found?
                if (checkLinkId == 0L) {
                    linksToBeTriggered.clear();
                    linksToBeTriggered.addAll(manager.listAllCheckLinkIds());
                    unkIndexList.clear();
                    break;
                }

                //if unknown, it must return an error -> issue
                if (!manager.existsCheckLink(checkLinkId)) {
                    unkIndexList.add(new UInteger(i));
                    continue;
                }
                //requirement 3.5.13.2.g : Check if the id are already going to be triggered then skip...
                if (linksToBeTriggered.contains(checkLinkId)) {
                    continue;
                }
                //...or add if not
                linksToBeTriggered.add(checkLinkId);
            }
        }

        // requirement: 3.5.13.2.f: if errors -> no evaluation
        if (!unkIndexList.isEmpty()) { // requirement: 3.5.13.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        //3.5.3.p trigger the check 
        if (!linksToBeTriggered.isEmpty()) {
            //requirement: 3.5.4.n -> Source Object = COMOperationActivity-Object
            ObjectId source = manager.storeCOMOperationActivity(interaction);
            for (Long checkLinkId : linksToBeTriggered) {
                //requirement: 3.5.13.2.h, i
                final ParameterValue currParameterValue = parameterManager.getParameterValue(manager.getCheckLinkLinks(
                    checkLinkId).getSource().getKey().getInstId());
                manager.executeCheck(checkLinkId, currParameterValue, true, false, source);
            }
        }
    }

    @Override
    public CheckTypedInstanceList listDefinition(IdentifierList names, MALInteraction interaction)
        throws MALInteractionException, MALException {
        LongList identityIds = new LongList();
        UIntegerList unkIndexList = new UIntegerList();
        CheckTypedInstanceList checkIdTypes = new CheckTypedInstanceList();

        if (null == names) { // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        // requirement: 3.5.14.2.f adding names while iterating through namelist ensures that
        for (int i = 0; i < names.size(); i++) {
            Identifier name = names.get(i);
            // requirement: 3.5.14.2.b, c Check for wildcards
            if (name.toString().equals("*")) {
                identityIds.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                identityIds.addAll(manager.listAllIdentities()); // ... add all in a row
                unkIndexList.clear();
                break;
            }

            final ObjectInstancePair idPair = manager.getIdentityDefinition(name);
            if (idPair == null) {  //requirement: 3.5.14.2.d
                unkIndexList.add(new UInteger(i));
                continue;
            }

            identityIds.add(manager.getIdentity(name));
        }
        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.3.14.3.1 (error: a and b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        for (Long identityId : identityIds) {
            //requirement: 3.5.14.2.e
            final ObjectType checkDefObjectType = CheckManager.generateCheckObjectType(manager.getActualCheckDefinition(
                identityId));
            checkIdTypes.add(new CheckTypedInstance(checkDefObjectType, new ObjectInstancePair(identityId, manager
                .getDefinitionId(identityId))));
        }

        return checkIdTypes;
    }

    @Override
    public ObjectInstancePairList addCheck(StringList names, CheckDefinitionDetailsList newDefinitions,
        MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();

        if (null == newDefinitions) { // Is the input null?
            throw new IllegalArgumentException("newDefinitions argument must not be null");
        }

        if (newDefinitions.size() != names.size()) { // requirement: 3.5.15.2.f
            int min = Math.min(newDefinitions.size(), names.size());
            int max = Math.max(newDefinitions.size(), names.size());
            for (; min < max; min++) {
                invIndexList.add(new UInteger(min));
            }
        } else {
            for (int i = 0; i < newDefinitions.size(); i++) { // requirement: 3.5.15.2.m
                CheckDefinitionDetails checkDef = (CheckDefinitionDetails) newDefinitions.get(i);
                final String name = names.get(i);

                // Check if the name field of the CheckDefinitionDetails is invalid.
                final Duration maxReportInterval = checkDef.getMaxReportingInterval();
                if (name == null || name.equals("*") || name.equals("") // requirement: 3.5.15.2.b
                //                        || (maxReportInterval.getValue() != 0 && !manager.getProvidedIntervals().contains(maxReportInterval)) //requirement: 3.5.3.n
                    || (maxReportInterval.getValue() != 0) //requirement: 3.5.3.n
                    || (checkDef.getNominalTime().getValue() == 0 && checkDef.getNominalCount().getValue() == 0) //requirement: .2.g, 3.5.3.r
                    || (checkDef.getViolationTime().getValue() == 0 && checkDef.getViolationCount().getValue() == 0) //requirement: .2.h, 3.5.3.s
                ) {
                    invIndexList.add(new UInteger(i));
                    continue;
                }

                if (manager.getIdentity(new Identifier(name)) != null) { // Is the supplied name unique? requirement: 3.5.15.2.c
                    dupIndexList.add(new UInteger(i));
                    continue;
                }
            }
        }
        // requirement: 3.5.15.2.i by checking errors first
        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.5.15.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!dupIndexList.isEmpty()) { // requirement: 3.5.15.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        // requirement: 3.5.15.2.e, m
        ObjectInstancePairList idPairs = new ObjectInstancePairList();
        if (!newDefinitions.isEmpty()) {
            ObjectId source = manager.storeCOMOperationActivity(interaction);
            for (int i = 0; i < newDefinitions.size(); i++) {
                idPairs.add(manager.add(names.get(i), (CheckDefinitionDetails) newDefinitions.get(i), source, connection
                    .getConnectionDetails())); //  requirement: 3.5.15.2.a, d, h
            }
        }

        return idPairs; // requirement: 3.5.15.2.l

    }

    @Override
    public LongList updateDefinition(LongList identityIds, CheckDefinitionDetailsList newDetails,
        MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList refErrorIndexList = new UIntegerList();

        if (null == identityIds || null == newDetails) { // Are the inputs null?
            throw new IllegalArgumentException("alertObjInstIds and alertDefDetails arguments must not be null");
        }

        if (identityIds.size() != newDetails.size()) {
            invIndexList = getInvalidIndexes(identityIds.size(), newDetails.size());
        } else {
            //requirement: 3.5.17.2.f
            for (int index = 0; index < identityIds.size(); index++) {
                CheckDefinitionDetails defDetails = (CheckDefinitionDetails) newDetails.get(index);
                final Duration maxReportInterval = defDetails.getMaxReportingInterval();
                if (identityIds.get(index) == null || identityIds.get(index) == 0
                //                        || (maxReportInterval.getValue() != 0 && !manager.getProvidedIntervals().contains(maxReportInterval)) //requirement: 3.5.3.n, 
                    || (maxReportInterval.getValue() != 0) //requirement: 3.5.3.n, 
                    || (defDetails.getNominalTime().getValue() == 0 && defDetails.getNominalCount().getValue() == 0) //requirement: 3.5.3.r, 3.5.17.2.h
                    || (defDetails.getViolationTime().getValue() == 0 && defDetails.getViolationCount().getValue() == 0) //requirement: 3.5.3.s, 3.5.17.2.i
                ) { // The object instance identifier is null or zero?
                    invIndexList.add(new UInteger(index)); // requirement: 3.5.17.2.b
                    continue;
                }

                if (manager.getActualCheckDefinition(identityIds.get(index)) == null) { // The object instance identifier could not be found?
                    unkIndexList.add(new UInteger(index)); // requirement: 3.5.17.2.c
                    continue;
                }
                // requirement: 3.5.17.2.d, 3.5.17.3.3
                if (manager.findLinksPointingToCheck(identityIds.get(index)).size() != 0) {
                    refErrorIndexList.add(new UInteger(index));
                    continue;
                }
            }
        }

        // requirement: 3.5.17.2.j by checking errors first
        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.5.17.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.5.17.3.2
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!refErrorIndexList.isEmpty()) { // requirement: 3.5.17.3.3
            throw new MALInteractionException(new MALStandardError(MCHelper.REFERENCED_ERROR_NUMBER,
                refErrorIndexList));
        }

        LongList newDefIds = new LongList();
        // requirement: 3.5.17.2.n
        for (int index = 0; index < newDetails.size(); index++) {
            // requirement: 3.5.17.2.e, k, l
            newDefIds.add(manager.update(identityIds.get(index), (CheckDefinitionDetails) newDetails.get(index),
                connection.getConnectionDetails()));  // requirement: 3.4.11.2.d, g
        }
        return newDefIds; // requirement: 3.5.17.2.m
    }

    /**
     * This method adds all numbers to the invIndexList, which have a higher
     * number than the number of the smaller of the two sizes
     *
     * @param firstList
     * @param secondList
     * @param invIndexList
     * @return the modified invIndexList
     */
    private UIntegerList getInvalidIndexes(int size1, int size2) {
        UIntegerList invIndexList = new UIntegerList();
        // requirement: 3.5.17.2.g
        int min = Math.min(size1, size2);
        int max = Math.max(size1, size2);
        for (; min < max; min++) {
            invIndexList.add(new UInteger(min));
        }
        return invIndexList;
    }

    @Override
    public void removeCheck(LongList identityIds, MALInteraction interaction) throws MALInteractionException,
        MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList refErrorIndexList = new UIntegerList();

        Long tempIdentityId;
        LongList tempIdentityIds = new LongList();

        if (null == identityIds) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < identityIds.size(); index++) {
            tempIdentityId = identityIds.get(index);

            if (tempIdentityId == 0) {  // Is it the wildcard '0'? requirement: 3.5.18.2.b, c
                tempIdentityIds.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempIdentityIds.addAll(manager.listAllIdentities()); // ... add all in a row
                // as of requirement: 3.5.18.2.c the wildcard value should be checked first, dont return errors.
                unkIndexList.clear();
                break;
            }

            if (manager.existsIdentity(tempIdentityId)) { // Does it match an existing definition?
                tempIdentityIds.add(tempIdentityId);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.5.18.2.d
            }
            // requirement: 3.5.18.2.e, 3.5.18.3.2
            if (manager.findLinksPointingToCheck(identityIds.get(index)).size() != 0) {
                refErrorIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.5.18.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!refErrorIndexList.isEmpty()) { // requirement: 3.5.18.3.2
            throw new MALInteractionException(new MALStandardError(MCHelper.REFERENCED_ERROR_NUMBER,
                refErrorIndexList));
        }

        // requirement: 3.5.18.2.g (Inserting the errors before this line guarantees that the requirement is met)
        for (Long identityId : tempIdentityIds) {
            manager.delete(identityId);  // COM archive is left untouched. requirement: 3.5.18.2.f // 3.5.18.2.h: its deleted from the list, so no new checklinks will find this CheckIdentity anymore
        }

    }

    @Override
    public ObjectInstancePairList addParameterCheck(CheckLinkDetailsList linkDetails, ObjectDetailsList linkRefs,
        MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList unkIndexList = new UIntegerList();

        if (null == linkDetails || null == linkRefs) { // Is the input null?
            throw new IllegalArgumentException("newDefinitions argument must not be null");
        }

        // requirement: 3.5.18.2.g
        if (linkDetails.size() != linkRefs.size()) {
            invIndexList = getInvalidIndexes(linkDetails.size(), linkRefs.size());
        } else {
            // requirement: 3.5.18.2.g by adding the ids while iterating through the details-list
            for (int i = 0; i < linkDetails.size(); i++) {
                // requirement: 3.5.18.2.e
                CheckLinkDetails linkDetail = linkDetails.get(i);

                final Duration checkInterval = linkDetail.getCheckInterval();
                //                if (checkInterval.getValue() != 0 && !manager.getProvidedIntervals().contains(checkInterval) //requirement: 3.5.3.n, 3.5.19.2.h: checkInterval must be provided
                if (checkInterval.getValue() != 0 //requirement: 3.5.3.n, 3.5.19.2.h: checkInterval must be provided
                    || checkInterval.getValue() != 0 && linkDetail.getCheckOnChange() //requirement: 3.5.19.2.i: 
                ) {
                    invIndexList.add(new UInteger(i));
                    continue;
                }
                //requirement 3.5.19.2.f 
                if (!manager.existsIdentity(linkRefs.get(i).getRelated()) || (linkRefs.get(i).getSource() != null &&
                    !parameterManager.existsIdentity(linkRefs.get(i).getSource().getKey().getInstId()))) {
                    unkIndexList.add(new UInteger(i));
                    continue;
                }
            }
        }
        //requirement: 3.5.3.n -> change rejected
        // Errors
        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }
        if (!unkIndexList.isEmpty()) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        ObjectInstancePairList outIdPairLst = new ObjectInstancePairList();
        // requirement: 3.5.18.2.l
        for (int i = 0; i < linkDetails.size(); i++) {
            CheckLinkDetails linkDetail = linkDetails.get(i);
            ObjectDetails linkRef = linkRefs.get(i);
            //requirement: 3.5.3.d
            final ObjectInstancePair checkLinkIds = manager.addCheckLink(linkDetail, linkRef, connection
                .getConnectionDetails());
            outIdPairLst.add(checkLinkIds); // requirement: 3.5.18.2.f
            final Long checkLinkId = checkLinkIds.getObjIdentityInstanceId();

            //if its a compound check, add its compounded checkLinks to be monitored
            final CheckDefinitionDetails actualCheckDef = manager.getActualCheckDefinitionFromCheckLinks(checkLinkId);
            if (actualCheckDef instanceof CompoundCheckDefinition) {
                checkLinkMonitorManager.add(checkLinkId, ((CompoundCheckDefinition) actualCheckDef).getCheckLinkIds());
            } else {
                paramMonitorManager.add(checkLinkId);
            }
            periodicReportingMaxManager.refresh(checkLinkId); // Refresh the Periodic Reporting Manager for the added checks
            periodicCheckingManager.refresh(checkLinkId); // Refresh the Periodic Checking Manager for the added checks
        }

        return outIdPairLst; // requirement: 3.5.18.2.f

    }

    @Override
    public void removeParameterCheck(LongList checkLinkIds, MALInteraction interaction) throws MALInteractionException,
        MALException {
        UIntegerList unkIndexList = new UIntegerList();

        LongList tempCheckLinkIds = new LongList();

        if (null == checkLinkIds) { // Is the input null?
            throw new IllegalArgumentException("objInstIds argument must not be null");
        }

        for (int index = 0; index < checkLinkIds.size(); index++) {
            Long checkLinkId = checkLinkIds.get(index);

            if (checkLinkId == 0) {  // Is it the wildcard '0'?  // requirement: 3.5.19.2.b, c
                tempCheckLinkIds.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempCheckLinkIds.addAll(manager.listAllCheckLinkIds()); // ... add all in a row
                unkIndexList.clear(); //as of requirement .2.c, the wildcard value should be checked for first, no errors should be returned
                break;
            }

            if (manager.listAllCheckLinkIds().contains(checkLinkId)) { // Does it match an existing definition?
                tempCheckLinkIds.add(checkLinkId);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.5.19.2.d
            }
        }

        // Errors 3.5.19.2.f
        if (!unkIndexList.isEmpty()) { // requirement: 3.5.19.3.1 (error: a, b)
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.5.19.2.f (Inserting the errors before this line guarantees that the requirement is met)
        for (Long checkLinkId : tempCheckLinkIds) {
            final CheckDefinitionDetails actCheckDef = manager.getActualCheckDefinitionFromCheckLinks(checkLinkId);
            if (actCheckDef instanceof CompoundCheckDefinition) {
                checkLinkMonitorManager.remove(checkLinkId, ((CompoundCheckDefinition) actCheckDef).getCheckLinkIds());
            } else {
                paramMonitorManager.remove(checkLinkId);
            }
            // requirement 3.5.19.2.g 
            manager.deleteCheckLink(checkLinkId); // COM archive is left untouched. requirement 3.5.19.2.e 
            periodicReportingMaxManager.refresh(checkLinkId); // Refresh the Periodic Reporting Manager for the removed Definitions
            periodicCheckingManager.refresh(checkLinkId); // Refresh the Periodic Checking Manager for the removed Definitions
        }

    }

    @Override
    public CheckLinkSummaryList listCheckLinks(LongList checkIdentityIds, MALInteraction interaction)
        throws MALInteractionException, MALException {
        LongList checkLinkIds = new LongList();
        UIntegerList unkIndexList = new UIntegerList();

        if (null == checkIdentityIds) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int i = 0; i < checkIdentityIds.size(); i++) {
            Long checkIdentityId = checkIdentityIds.get(i);
            // requirement: 3.5.15.2.b, c Check for wildcards
            if (checkIdentityId == 0L) {
                checkLinkIds.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                checkLinkIds.addAll(manager.listAllCheckLinkIds()); // ... add all in a row
                unkIndexList.clear();
                break;
            }

            if (!manager.existsIdentity(checkIdentityId)) {  //requirement: 3.5.15.2.d
                unkIndexList.add(new UInteger(i));
                continue;
            }

            checkLinkIds.addAll(manager.findLinksPointingToCheck(checkIdentityId));
        }
        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.3.15.3.1 (error: a and b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        CheckLinkSummaryList checkLinkSummaries = new CheckLinkSummaryList(checkLinkIds.size());
        for (Long checkLinkId : checkLinkIds) {
            //requirement: 3.5.14.2.e
            final Long checkId = manager.getCheckLinkLinks(checkLinkId).getRelated();
            final Long linkDefinitionId = manager.getCheckLinkDefId(checkLinkId);

            final boolean checkLinkEnabled = manager.getCheckLinkDetails(linkDefinitionId).getCheckEnabled();
            final ObjectKey ParamKey = manager.getCheckLinkLinks(checkLinkId).getSource().getKey();
            checkLinkSummaries.add(new CheckLinkSummary(checkId, checkLinkId, linkDefinitionId, checkLinkEnabled,
                ParamKey));
        }

        return checkLinkSummaries;
    }

    private class PeriodicCheckingManager { // requirement: 3.7.2.1a

        private HashMap<Long, TaskScheduler> sampleTimerList; // Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicCheckingManager() {
            sampleTimerList = new HashMap<>();

        }

        public void refreshAll() {
            this.refreshList(manager.listAllCheckLinkIds());
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

        public void refresh(Long checkLinkId) {
            if (sampleTimerList.containsKey(checkLinkId)) { // Does it exist in the PeriodicCheckingManager?
                this.removePeriodicChecking(checkLinkId);
            }
            final CheckLinkDetails checkLink = manager.getCheckLinkDetails(manager.getCheckLinkDefId(checkLinkId));
            if (checkLink != null // Does it exist?
                && manager.getCheckServiceEnabled() //checkService globally enabled //TODO: should newly added checkLinks, that were added while the service is disabled, be added or not? -> issue #24
                && checkLink.getCheckEnabled() //checkservice enabled on checkDefinition-layer
                && checkLink.getCheckInterval().getValue() != 0 //3.5.3.l
                && !checkLink.getCheckOnChange() //3.5.3.k: checkOnChange must be false for periodic checks //TODO: INVALID error if CHECKONCHANGE and CHECKINTERVAL? -> issue #162
            ) {
                this.addPeriodicChecking(checkLinkId, checkLink.getCheckInterval());
            }
        }

        public void refreshList(LongList checkLinkIds) {
            if (checkLinkIds == null) {
                return;
            }
            for (Long objId : checkLinkIds) {
                refresh(objId);
            }
        }

        private void addPeriodicChecking(Long checkLinkId, Duration checkInterval) {
            try {
                final ObjectId paramId = manager.getCheckLinkLinks(checkLinkId).getSource();
                //todo: the source link should be the ObjectId-of the parameterValue somehow
                manager.executeCheck(checkLinkId, paramId == null ? null : parameterManager.getParameterValue(paramId
                    .getKey().getInstId()), false, false, null);
            } catch (MALInteractionException ex) {
                Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            // Add to the Periodic Sampling Manager 
            TaskScheduler timer = new TaskScheduler(1);  // Take care of adding a new timer
            sampleTimerList.put(checkLinkId, timer);
            startTimer(checkLinkId, checkInterval);// requirement: 3.5.3.j
        }

        private void removePeriodicChecking(Long objId) {
            this.stopTimer(objId);
            sampleTimerList.remove(objId);
        }

        private void startTimer(final Long checkLinkId, Duration interval) {  // requirement: 3.7.2.11

            // the time has to be converted to milliseconds by multiplying by 1000
            sampleTimerList.get(checkLinkId).scheduleTask(new Thread(() -> { // Periodic Checking
                if (active) {
                    try {
                        final ObjectId paramId = manager.getCheckLinkLinks(checkLinkId).getSource();
                        //todo: the source link should be the ObjectId-of the parameterValue
                        manager.executeCheck(checkLinkId, paramId == null ? null : parameterManager.getParameterValue(
                            manager.getCheckLinkLinks(checkLinkId).getSource().getKey().getInstId()), false, false,
                            null);
                    } catch (MALInteractionException ex) {
                        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }), 0, (int) (interval.getValue() * 1000), TimeUnit.MILLISECONDS, true); // requirement: 3.6.2.g
        }

        private void stopTimer(Long objId) {
            sampleTimerList.get(objId).stopLast();
        }

    }

    private class PeriodicReportingMaxManager { // requirement: 3.7.2.1a

        private HashMap<Long, TaskScheduler> updateTimerList; // updateInterval Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicReportingMaxManager() {
            updateTimerList = new HashMap<>();
        }

        public void refreshAll() {
            this.refreshList(manager.listAllCheckLinkIds());
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
            this.refreshAll(); // Refresh all the Check Links in the Manager
            this.start(); // set active flag to true
        }

        public void refresh(Long checkLinkId) {

            if (updateTimerList.containsKey(checkLinkId)) { // Does it exist in the Periodic Reporting Manager?
                this.removePeriodicReportingMax(checkLinkId);
            }
            CheckLinkDetails checkLink = manager.getCheckLinkDetails(manager.getCheckLinkDefId(checkLinkId));
            if (checkLink != null) { // Does it exist in the CheckLinks List?
                CheckDefinitionDetails checkDef = manager.getActualCheckDefinitionFromCheckLinks(checkLinkId);
                if (checkDef.getMaxReportingInterval().getValue() != 0 // Is the periodic reporting active?
                    && manager.getCheckServiceEnabled() //checkService globally enabled //TODO: should newly added checkLinks, that were added while the service is disabled, be added or not? -> issue #24
                    && checkLink.getCheckEnabled() //enabled on the definition layer
                ) {
                    this.addPeriodicReportingMax(checkLinkId, checkDef.getMaxReportingInterval());
                }
            }
        }

        public void refreshList(LongList checkLinkIds) {
            if (checkLinkIds == null) {
                return;
            }
            for (Long checkLinkId : checkLinkIds) {
                this.refresh(checkLinkId);
            }
        }

        private void addPeriodicReportingMax(Long checkLinkId, Duration maxReportingInterval) {
            TaskScheduler timer = new TaskScheduler(1);
            updateTimerList.put(checkLinkId, timer);
            this.startUpdatesTimer(checkLinkId, maxReportingInterval);
        }

        private void removePeriodicReportingMax(Long objId) {
            this.stopUpdatesTimer(objId);
            updateTimerList.remove(objId);
        }

        private void startUpdatesTimer(final Long checkLinkId, final Duration interval) {
            // the time is being converted to milliseconds by multiplying by 1000  (starting delay included)
            updateTimerList.get(checkLinkId).scheduleTask(new Thread(() -> {
                try {
                    //paramId is null for compound check
                    final ObjectId paramSource = manager.getCheckLinkLinks(checkLinkId).getSource();
                    //requirement: 3.5.4.m -> Source Object = null
                    manager.executeCheck(checkLinkId, paramSource == null ? null : parameterManager.getParameterValue(
                        paramSource.getKey().getInstId()), false, true, null);
                } catch (MALInteractionException ex) {
                    Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }), (int) (interval.getValue() * 1000), (int) (interval.getValue() * 1000), TimeUnit.MILLISECONDS, true); // requirement: 3.5.3.ff
        }

        private void stopUpdatesTimer(final Long objId) {
            updateTimerList.get(objId).stopLast();
        }

    }

}
