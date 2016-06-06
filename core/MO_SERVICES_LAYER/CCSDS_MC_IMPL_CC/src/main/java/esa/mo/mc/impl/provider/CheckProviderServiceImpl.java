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

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mc.impl.interfaces.ConfigurationNotificationInterface;
import esa.mo.mc.impl.util.ReconfigurableServiceImplInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
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
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
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
import org.ccsds.moims.mo.mc.check.structures.CheckResult;
import org.ccsds.moims.mo.mc.check.structures.CheckResultFilter;
import org.ccsds.moims.mo.mc.check.structures.CheckResultList;
import org.ccsds.moims.mo.mc.check.structures.CheckState;
import org.ccsds.moims.mo.mc.check.structures.CheckSummary;
import org.ccsds.moims.mo.mc.check.structures.CheckSummaryList;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

/**
 *
 */
public class CheckProviderServiceImpl extends CheckInheritanceSkeleton implements ReconfigurableServiceImplInterface {

    private MALProvider checkServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private CheckManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final ConfigurationProvider configuration = new ConfigurationProvider();
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private PeriodicReportingMaxManager periodicReportingManager;
    private PeriodicCheckingManager periodicCheckingManager;
    private ConfigurationNotificationInterface configurationAdapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param parameterManager
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices,
            ParameterManager parameterManager) throws MALException {

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
                CheckHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {// nothing to be done..
            }
        }

        // Shut down old service transport
        if (null != checkServiceProvider) {
            connection.close();
        }

        checkServiceProvider = connection.startService(CheckHelper.CHECK_SERVICE_NAME.toString(), CheckHelper.CHECK_SERVICE, false, this);

        running = true;
        manager = new CheckManager(comServices, parameterManager);

        periodicReportingManager = new PeriodicReportingMaxManager();
        periodicCheckingManager = new PeriodicCheckingManager();
        periodicReportingManager.init(); // Initialize the Periodic Reporting Manager
        periodicCheckingManager.init(); // Initialize the Periodic Sampling Manager

        initialiased = true;
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).info("Check service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != checkServiceProvider) {
                checkServiceProvider.close();
            }

            connection.close();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    protected Long publishCheckTransitionEvent(final CheckResult checkResult, final Long related, final ObjectId source) {
        
        // Store the event in the Archive
        Long eventObjId = this.manager.getCOMservices().getEventService().generateAndStoreEvent(
                CheckHelper.CHECKTRANSITION_OBJECT_TYPE, 
                this.configuration.getDomain(), 
                checkResult, 
                related, 
                source, 
                null, 
                this.configuration.getNetwork());
        
        CheckResultList checkResults = new CheckResultList();
        checkResults.add(checkResult);

        // Publish the Event
        manager.getCOMservices().getEventService().publishEvent(
                this.connection.getConnectionDetails().getProviderURI(), 
                eventObjId, 
                CheckHelper.CHECKTRANSITION_OBJECT_TYPE, 
                related, 
                source, 
                checkResults);
                
        return eventObjId;
    }
    
    
    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override
    public void getCurrentTransitionList(CheckResultFilter filter, GetCurrentTransitionListInteraction interaction) throws MALInteractionException, MALException {

        
        filter.getParameterFilter();
        

        // Get all the available CheckResults
        

        CheckSummaryList list = new CheckSummaryList();

        for(int i = 0; i < filter.getParameterFilter().size(); i++){
            
            CheckSummary summary = new CheckSummary();

        }
        
        
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

    }

    @Override
    public void getSummaryReport(LongList objInstIds, GetSummaryReportInteraction interaction) throws MALInteractionException, MALException {
        
        
        
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        
        
        
    }

    @Override
    public void enableService(Boolean enableService, MALInteraction interaction) throws MALInteractionException, MALException {
        manager.setCheckServiceEnabled(enableService);
    }

    @Override
    public Boolean getServiceStatus(MALInteraction interaction) throws MALInteractionException, MALException {
        return manager.getCheckServiceEnabled();
    }

    @Override
    public void enableCheck(Boolean isGroupIds, InstanceBooleanPairList enableInstances, MALInteraction interaction) throws MALInteractionException, MALException {

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
                manager.setCheckEnabledAll(instance.getValue(), connection.getConnectionDetails());
                periodicReportingManager.refreshAll();
                periodicCheckingManager.refreshAll();
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

        // requirement: 3.4.8.i (This part of the code is not reached if an error is thrown)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            // requirement: 3.4.8.e and 3.4.8.f and 3.4.8.j
            manager.setCheckEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), connection.getConnectionDetails());
            periodicReportingManager.refresh(objIdToBeEnabled.get(index));
            periodicCheckingManager.refresh(objIdToBeEnabled.get(index));
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public void triggerCheck(LongList defObjInstIds, LongList linkObjInstIds, MALInteraction interaction) throws MALInteractionException, MALException {

        // Get the list of all the objIds of the checkLinks
        LongList linksToBeTriggered = new LongList();

        // Check if the Check Links requested exist...
        for(int i = 0 ; i < linkObjInstIds.size(); i++){
            CheckLinkDetails linkLinks = manager.getCheckLinkDetails(linkObjInstIds.get(i));

            if (linkLinks == null){
                // Raise error!
            }
            
            linksToBeTriggered.add(linkObjInstIds.get(i));
        }

        LongList linksPoitingDef = manager.findLinksPointingToDefinition(defObjInstIds);

        
        // Check if the links of the requested Definitions are already in the previous list
        for(int i = 0; i < linksPoitingDef.size() ; i++){
            
            boolean alreadyInList = false;
            
            // Check if they are already going to be triggered...
            for (int j = 0; j < linksToBeTriggered.size(); j++){
                if (linksToBeTriggered.get(j).equals(linksPoitingDef.get(i)) ){
                    alreadyInList = true;
                    break;
                }
            }
            
            if (!alreadyInList){  // If not in the list then add it...
                linksToBeTriggered.add(linksPoitingDef.get(i));
            }
            
        }


        // Trigger the Checking...

        // (To be finalized...)
        
        
        
    }

    @Override
    public LongList listDefinition(IdentifierList names, MALInteraction interaction) throws MALInteractionException, MALException {
        LongList outLongLst = new LongList();

        if (null == names) { // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        for (Identifier name : names) {
            // Check for wildcards
            if (name.toString().equals("*")) {
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            outLongLst.add(manager.list(name));
        }

        // Errors
        // The operation does not return any errors.
        return outLongLst;
    }

    @Override
    public LongList addDefinition(CheckDefinitionDetailsList newDefinitions, MALInteraction interaction) throws MALInteractionException, MALException {

        LongList outLongLst = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();
        CheckDefinitionDetails tempCheckDefinition;

        if (null == newDefinitions) { // Is the input null?
            throw new IllegalArgumentException("newDefinitions argument must not be null");
        }

        for (int index = 0; index < newDefinitions.size(); index++) { // requirement: 3.5.15.2.f
            tempCheckDefinition = (CheckDefinitionDetails) newDefinitions.get(index);

            // Check if the name field of the CheckDefinitionDetails is invalid.
            if (tempCheckDefinition.getName() == null
                    || tempCheckDefinition.getName().toString().equals("*")
                    || tempCheckDefinition.getName().toString().equals("")) { // requirement: 3.5.15.2.b
                invIndexList.add(new UInteger(index));
            }

            if (manager.list(tempCheckDefinition.getName()) != null) { // Is the supplied name unique? requirement: 3.5.15.2.c
                dupIndexList.add(new UInteger(index));
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.5.15.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!dupIndexList.isEmpty()) { // requirement: 3.5.15.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        // requirement: 3.5.15.2.d
        for (Object tempCheckDefinition1 : newDefinitions) {
            ObjectId source = manager.storeCOMOperationActivity(interaction);
            outLongLst.add(manager.add(((CheckDefinitionDetails) tempCheckDefinition1), source, connection.getConnectionDetails())); //  requirement: 3.5.15.2.d
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

        return outLongLst; // requirement: 3.5.15.2.e

    }

    @Override
    public void updateDefinition(LongList objInstIds, CheckDefinitionDetailsList newDetails, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        CheckDefinitionDetails oldCheckDefDetails;

        if (null == objInstIds || null == newDetails) { // Are the inputs null?
            throw new IllegalArgumentException("alertObjInstIds and alertDefDetails arguments must not be null");
        }

        for (int index = 0; index < objInstIds.size(); index++) {
            if (objInstIds.get(index) == null
                    || objInstIds.get(index) == 0) { // The object instance identifier is null or zero?
                invIndexList.add(new UInteger(index)); // requirement: 3.5.16.2.b
                continue;
            }

            oldCheckDefDetails = manager.getCheckDefs().get(objInstIds.get(index));

            if (oldCheckDefDetails == null) { // The object instance identifier could not be found?
                unkIndexList.add(new UInteger(index)); // requirement: 3.5.16.2.c
                continue;
            }

            if (!((CheckDefinitionDetails) newDetails.get(index)).getName().equals(oldCheckDefDetails.getName())) { // Are the names different?
                invIndexList.add(new UInteger(index)); // requirement: 3.5.16.2.b
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.4.11.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.4.11.3.2
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.4.11.2.f
        for (int index = 0; index < newDetails.size(); index++) {
            manager.update(objInstIds.get(index), (CheckDefinitionDetails) newDetails.get(index), connection.getConnectionDetails());  // Change in the manager
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public void removeDefinition(LongList objInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == objInstIds) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < objInstIds.size(); index++) {
            tempLong = objInstIds.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.5.17.2.b
                tempLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            if (manager.exists(tempLong)) { // Does it match an existing definition?
                tempLongLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.5.17.2.c
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.4.12.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.4.12.2.e (Inserting the errors before this line guarantees that the requirement is met)
        for (Long tempLong2 : tempLongLst) {
            manager.delete(tempLong2);  // COM archive is left untouched. requirement: 3.4.12.2.d
        }

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public LongList addParameterCheck(CheckLinkDetailsList linkDetails, ObjectDetailsList linkRefs, MALInteraction interaction) throws MALInteractionException, MALException {
        LongList outLongLst = new LongList();

        if (null == linkDetails || null == linkRefs) { // Is the input null?
            throw new IllegalArgumentException("newDefinitions argument must not be null");
        }

        if (linkDetails.size() != linkRefs.size()) {
            throw new IllegalArgumentException("linkDetails and linkRefs must have the same size.");
        }

        for (int i = 0; i < linkDetails.size(); i++) {
            CheckLinkDetails linkDetail = linkDetails.get(i);
            ObjectDetails linkRef = linkRefs.get(i);
            outLongLst.add(manager.addLink(linkDetail, linkRef, connection.getConnectionDetails())); // requirement: 3.5.18.2.f
        }

        periodicReportingManager.refreshList(outLongLst); // Refresh the Periodic Reporting Manager for the removed Definitions
        periodicCheckingManager.refreshList(outLongLst); // Refresh the Periodic Checking Manager for the removed Definitions

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

        return outLongLst; // requirement: 3.5.18.2.f

    }

    @Override
    public void removeParameterCheck(LongList objInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == objInstIds) { // Is the input null?
            throw new IllegalArgumentException("objInstIds argument must not be null");
        }

        for (int index = 0; index < objInstIds.size(); index++) {
            tempLong = objInstIds.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'?  // requirement: 3.5.19.2.b
                tempLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempLongLst.addAll(manager.listAllLinks()); // ... add all in a row
                break;
            }

            if (manager.exists(tempLong)) { // Does it match an existing definition? // requirement: 3.5.19.2.f
                tempLongLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.5.19.2.c
            }
        }

        // Errors 3.5.19.2.e
        if (!unkIndexList.isEmpty()) { // requirement: 3.5.19.3.1 (error: a, b)
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.5.19.2.f (Inserting the errors before this line guarantees that the requirement is met)
        for (Long tempLong2 : tempLongLst) {
            manager.deleteLink(tempLong2);
            // COM archive is left untouched. 3.5.19.2.d
        }

        periodicReportingManager.refreshList(tempLongLst); // Refresh the Periodic Reporting Manager for the removed Definitions
        periodicCheckingManager.refreshList(tempLongLst); // Refresh the Periodic Checking Manager for the removed Definitions

        if (configurationAdapter != null) {
            configurationAdapter.configurationChanged(this);
        }

    }
    
    private class PeriodicCheckingManager { // requirement: 3.7.2.1a

        private HashMap<Long, Timer> sampleTimerList; // Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicCheckingManager() {
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
            if (sampleTimerList.containsKey(objId)) { // Does it exist in the PeriodicCheckingManager?
                this.removePeriodicChecking(objId);
            }

            if (manager.checkLinkExists(objId)) { // Does it exist?
                this.addPeriodicChecking(objId);
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

        private void addPeriodicChecking(Long linkObjId) {

            final CheckLinkDetails checkLink = manager.getCheckLinkDetails(linkObjId);

            if (!checkLink.getCheckEnabled()) {
                return; // Periodic Sampling shall not occur if the generation is not enabled at the definition level
            }

            Duration checkInterval = checkLink.getCheckInterval(); // requirement: 3.5.3.j
            /*
             if (checkInterval.getValue() > manager.getStatisticLink(linkObjId).getLinkDetails().getReportingInterval().getValue()) {
             checkInterval = new Duration(0);
             }
             */
            // Add to the Periodic Sampling Manager only if there's a sampleInterval selected for the parameterSet
            if (checkInterval.getValue() != 0) {
                Timer timer = new Timer();  // Take care of adding a new timer
                sampleTimerList.put(linkObjId, timer);
                startTimer(linkObjId, checkInterval);
            }

        }

        private void removePeriodicChecking(Long objId) {
            this.stopTimer(objId);
            sampleTimerList.remove(objId);
        }

        private void startTimer(final Long linkObjId, Duration interval) {  // requirement: 3.7.2.11

            sampleTimerList.get(linkObjId).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() { // Periodic Checking
                    if (active) {
                        
                        CheckResult checkResult = new CheckResult();
                        CheckState state;

                        try {
                            // Fetch Value
                            ParameterValue parValue = manager.getParameterValue(linkObjId);
                            state = manager.evaluateCheckState(linkObjId, parValue);

                            Attribute value = (manager.getCheckLinkDetails(linkObjId).getUseConverted()) ? parValue.getConvertedValue() : parValue.getRawValue();
                            checkResult.setCheckedValue(value);

                        } catch (MALInteractionException ex) {
                            state = CheckState.INVALID; // The parameter does not exist
                        }
                        
                        checkResult.setPreviousCheckState(checkResult.getCurrentCheckState());
                        checkResult.setCurrentCheckState(state);

                        checkResult.setCurrentEvaluationResult(active);
                        
                        
                        
                        //  To be finalized...
                        
                        
                        ObjectId source = new ObjectId();
                        source.setType(ParameterHelper.PARAMETERVALUEINSTANCE_OBJECT_TYPE);
                        source.setKey(new ObjectKey());
                        
                        // If it is in transition then publish...
                        if (checkResult.getCurrentEvaluationResult()){
                            Long eventId = publishCheckTransitionEvent(checkResult, linkObjId, source);
                        }
                        
                        
//                        ParameterValue pVal = manager.ge.getParameterValue(objId); // Get the Parameter Value

                        // Add the value to the data set
//                        manager.getDataSets().addAttributeToDataSet(objId, pVal.getRawValue(), ConfigurationProvider.getTimestampMillis());
                    }
                } // the time has to be converted to milliseconds by multiplying by 1000
            }, 0, (int) (interval.getValue() * 1000)); // requirement: 3.6.2.g
        }

        private void stopTimer(Long objId) {
            sampleTimerList.get(objId).cancel();
        }

    }

    private class PeriodicReportingMaxManager { // requirement: 3.7.2.1a

        private HashMap<Long, Timer> updateTimerList; // updateInterval Timers list
        private boolean active = false; // Flag that determines if the Manager is on or off

        public PeriodicReportingMaxManager() {
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

            CheckLinkDetails checkLink = manager.getCheckLinkDetails(objId);
            CheckDefinitionDetails checkDef = manager.getCheckDefs().get(manager.getCheckLinkLinks(objId).getRelated());

            if (updateTimerList.containsKey(objId)) { // Does it exist in the Periodic Reporting Manager?
                this.removePeriodicReportingMax(objId);
            }

            if (checkDef != null) { // Does it exist in the Statistic Links List?
                if (checkDef.getMaxReportingInterval().getValue() != 0
                        && checkLink.getCheckEnabled()) { // Is the periodic reporting active?
                    this.addPeriodicReportingMax(objId);
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

        private void addPeriodicReportingMax(Long linkObjId) {

            Timer timer = new Timer();
            updateTimerList.put(linkObjId, timer);
            this.startUpdatesTimer(linkObjId, manager.getCheckDefs().get(manager.getCheckLinkLinks(linkObjId).getRelated()).getMaxReportingInterval());

        }

        private void removePeriodicReportingMax(Long objId) {
            this.stopUpdatesTimer(objId);
            updateTimerList.remove(objId);
        }

        private void startUpdatesTimer(final Long linkObjId, final Duration interval) {
            updateTimerList.get(linkObjId).scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
/*
                    if (manager.getCheckServiceEnabled() && active && manager.getCheckLinkDetails(linkObjId).getCheckEnabled()) {
                         StatisticParameterDetails statLink = manager.getStatisticLink(objId);

                         if (statLink.getLinkDetails().getReportingEnabled()) {  // requirement 3.6.2.h
                             StatisticEvaluationReport report = manager.getStatisticEvaluationReport(objId);

                             if (report != null) {
                                 publishStatisticsUpdate(objId, manager.getStatisticEvaluationReport(objId).getValue(), null);
                             }
                         }

                         if (true) {  // Reset at every update Interval
                             manager.resetStatisticEvaluationReport(objId);
                         }
                     }
*/                    
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
        if(configurationObjectDetails == null){
            return false;
        }

        if (configurationObjectDetails.getConfigObjects() == null) {
            return false;
        }

        // The Configuration of the Check service is composed by Check Links and Check Definitions
        if (configurationObjectDetails.getConfigObjects().size() != 2) {
            return false;
        }

        if (configurationObjectDetails.getConfigObjects().get(0) == null
                && configurationObjectDetails.getConfigObjects().get(1) == null) {
            return false;
        }

        ConfigurationObjectSet confSet0 = configurationObjectDetails.getConfigObjects().get(0);
        ConfigurationObjectSet confSet1 = configurationObjectDetails.getConfigObjects().get(1);

        // Confirm the objType
        if (!confSet0.getObjType().equals(CheckHelper.CHECKLINK_OBJECT_TYPE)
                && !confSet1.getObjType().equals(CheckHelper.CHECKLINK_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the objType
        if (!confSet1.getObjType().equals(CheckHelper.CHECKLINK_OBJECT_TYPE)
                && !confSet1.getObjType().equals(CheckHelper.CHECKLINK_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet0.getDomain().equals(configuration.getDomain())
                || !confSet1.getDomain().equals(configuration.getDomain())) {
            return false;
        }

        for (ConfigurationObjectSet set : configurationObjectDetails.getConfigObjects()) {

            if (set.getObjType().equals(CheckHelper.CHECKLINK_OBJECT_TYPE)) {
                CheckLinkDetailsList links = (CheckLinkDetailsList) HelperArchive.getObjectBodyListFromArchive(
                        manager.getCOMservices().getArchiveService(),
                        set.getObjType(),
                        configuration.getDomain(),
                        set.getObjInstIds());

                manager.reconfigureLinkDetails(set.getObjInstIds(), links);
                
                List<ArchivePersistenceObject> comObjects = HelperArchive.getArchiveCOMObjectList(
                        manager.getCOMservices().getArchiveService(),
                        set.getObjType(),
                        configuration.getDomain(),
                        set.getObjInstIds());
                
                ObjectDetailsList linklinks = new ObjectDetailsList();
                
                if(comObjects == null){
                    continue;
                }
                
                for (ArchivePersistenceObject comObject : comObjects){
                    linklinks.add(comObject.getArchiveDetails().getDetails());
                }
                
                manager.reconfigureLinkLinks(set.getObjInstIds(), linklinks);
            }

            if (set.getObjType().equals(CheckHelper.CHECKIDENTITY_OBJECT_TYPE)) {
                List<ArchivePersistenceObject> comObjects = HelperArchive.getArchiveCOMObjectList(
                        manager.getCOMservices().getArchiveService(),
                        set.getObjType(),
                        configuration.getDomain(),
                        set.getObjInstIds());

                LongList objIds = set.getObjInstIds();
                ArrayList<CheckDefinitionDetails> defs = new ArrayList<CheckDefinitionDetails>();
                
                for(Long objId : objIds){
                    CheckDefinitionDetails def = (CheckDefinitionDetails) HelperArchive.getObjectBodyFromArchive(
                        manager.getCOMservices().getArchiveService(),
                        comObjects.get(0).getArchiveDetails().getDetails().getSource().getType(),
                        comObjects.get(0).getArchiveDetails().getDetails().getSource().getKey().getDomain(),
                        objId);
                     
                     defs.add(def);
                }

                manager.reconfigureDefinitions(objIds, defs);
            }

        }

        /*        
         // If the list is empty, reconfigure the service with nothing...
         if(confSet.getObjInstIds().isEmpty()){
         manager.reconfigureLinkDetails(new LongList(), new StatisticParameterDetailsList());   // Reconfigures the Manager
         periodicCheckingManager.refreshAll();  // Refresh the reporting
         periodicCollectionManager.refreshAll();  // Refresh the reporting
         periodicReportingManager.refreshAll();  // Refresh the reporting
         return true;
         }
            
         // ok, we're good to go...
         // Load the Parameter Definitions from this configuration...
         StatisticParameterDetailsList links = (StatisticParameterDetailsList) HelperArchive.getObjectBodyListFromArchive(
         manager.getCOMservices().getArchiveService(),
         StatisticHelper.STATISTICLINK_OBJECT_TYPE,
         configuration.getDomain(),
         confSet.getObjInstIds());

         manager.reconfigureLinkDetails(confSet.getObjInstIds(), links);   // Reconfigures the Manager
         periodicCheckingManager.refreshAll();  // Refresh the reporting
         periodicCollectionManager.refreshAll();  // Refresh the reporting
         periodicReportingManager.refreshAll();  // Refresh the reporting
         */

         periodicCheckingManager.refreshAll();  // Refresh the reporting
         periodicReportingManager.refreshAll();  // Refresh the reporting
        
        return true;

    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {

        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider

        // Definitions
        ConfigurationObjectSet objsSet = new ConfigurationObjectSet();
        objsSet.setDomain(configuration.getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(manager.getCheckDefs().keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(CheckHelper.CHECKIDENTITY_OBJECT_TYPE);

        // Links
        ConfigurationObjectSet objsSet2 = new ConfigurationObjectSet();
        objsSet2.setDomain(configuration.getDomain());
        LongList currentObjIds2 = new LongList();
        currentObjIds2.addAll(manager.getCheckLinksLinks().keySet());
        objsSet2.setObjInstIds(currentObjIds2);
        objsSet2.setObjType(CheckHelper.CHECKLINK_OBJECT_TYPE);

        ConfigurationObjectSetList configObjs = new ConfigurationObjectSetList();
        configObjs.add(objsSet);
        configObjs.add(objsSet2);
        
        // Needs the Common API here!
        ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(configObjs);

        return set;
    }

}
