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
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.reconfigurable.service.ConfigurationNotificationInterface;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
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
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;

/**
 *
 */
public class ActionProviderServiceImpl extends ActionInheritanceSkeleton implements ReconfigurableServiceImplInterface {

    private final static String IS_INTERMEDIATE_RELAY_PROPERTY = "esa.mo.mc.impl.provider.ActionProviderServiceImpl.isIntermediateRelay";
    private MALProvider actionServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private ActionManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private ConfigurationNotificationInterface configurationAdapter;


    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @param actions
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, 
            ActionInvocationListener actions) throws MALException {
        if (!initialiased) {

            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
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
                ActionHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

        }

        // Shut down old service transport
        if (null != actionServiceProvider) {
            connection.closeAll();
        }

        actionServiceProvider = connection.startService(ActionHelper.ACTION_SERVICE_NAME.toString(), ActionHelper.ACTION_SERVICE, false, this);

        running = true;
        manager = new ActionManager(comServices, actions);

        initialiased = true;
        Logger.getLogger(ActionProviderServiceImpl.class.getName()).info("Action service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != actionServiceProvider) {
                actionServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ActionProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }
    
    public ConnectionProvider getConnectionProvider(){
        return this.connection;
    }

    @Override
    public void setConfigurationAdapter(esa.mo.reconfigurable.service.ConfigurationNotificationInterface configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }
        
    @Override
    public void submitAction(Long actionInstId, ActionInstanceDetails actionDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList invIndexList = new UIntegerList();
        UIntegerList unkIndexList = new UIntegerList();

        if (manager.exists(actionDetails.getDefInstId())) {

        }

        if ("true".equals(System.getProperty(IS_INTERMEDIATE_RELAY_PROPERTY))) {
            // Forward
            manager.forward(actionInstId, actionDetails, interaction, connection.getConnectionDetails());
            return; // Do nothing else...
        }
        
        // Publish Acceptance event for submitAction operation
        try {
            ObjectId source = manager.getActivityTrackingService().storeCOMOperationActivity(interaction, null);  // requirement: 3.2.4.f  and 3.2.4.g
            manager.getActivityTrackingService().publishAcceptanceEventOperation(interaction, true, null, source);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Check the ActionInstanceDetails
        boolean accepted = manager.checkActionInstanceDetails(actionDetails, invIndexList);
        
        ObjectType type = ActionHelper.ACTIONINSTANCE_OBJECT_TYPE;
        ObjectKey key = new ObjectKey(ConfigurationProviderSingleton.getDomain(), actionInstId);
        ObjectId source2 = new ObjectId(type, key);  // requirement: 3.2.8.f

        // Publish the Acceptance event
        try {  // requirement: 3.2.8.g
            manager.getActivityTrackingService().publishAcceptanceEventOperation(interaction, accepted, null, source2);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        }


        if (!manager.exists(actionDetails.getDefInstId())) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.2.9.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.2.9.3.2
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // If it was accepted then execute the action!
        if (accepted) { // requirement: 3.2.9.d
            manager.execute(actionInstId, actionDetails, interaction, connection.getConnectionDetails()); // The execution events are generated within the execute method
        }
        
        
    }

    @Override
    public Boolean preCheckAction(ActionInstanceDetails actionDetails, MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList invIndexList = new UIntegerList();

        // 3.2.10.3.2
        if (!manager.exists(actionDetails.getDefInstId())) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        // 3.2.10.2.c
        boolean accepted = manager.checkActionInstanceDetails(actionDetails, invIndexList);

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.2.9.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }
        
        return accepted;
    }

    @Override
    public LongList listDefinition(final IdentifierList actionNames, final MALInteraction interaction) throws MALException, MALInteractionException {
        LongList actionDefInstIds = new LongList();

        if (null == actionNames){ // Is the input null?
            throw new IllegalArgumentException("actionNames argument must not be null");
        }

        for (Identifier actionName : actionNames) { // requirement: 3.2.11.2.c
            // Check for the wildcard
            if (actionName.toString().equals("*")) {  // requirement: 3.2.11.2.b
                actionDefInstIds.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                actionDefInstIds.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            actionDefInstIds.add(manager.list(actionName)); // requirement: 3.2.11.2.d
        }

        // Errors
        // The operation does not return any errors.
        return actionDefInstIds;
    }

    @Override
    public LongList addDefinition(ActionDefinitionDetailsList actionDefDetails, MALInteraction interaction) throws MALInteractionException, MALException {
        LongList newObjInstIds = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();
        ActionDefinitionDetails tempActionDefinitionDetails;

        if (null == actionDefDetails) // Is the input null?
        {
            throw new IllegalArgumentException("actionDefDetails argument must not be null");
        }

        for (int index = 0; index < actionDefDetails.size(); index++) { // requirement: 3.2.12.2.f (incremental "for cycle" guarantees that)
            tempActionDefinitionDetails = actionDefDetails.get(index);

            // Check if the name field of the ActionDefinition is invalid.
            if (tempActionDefinitionDetails.getName() == null
                    || tempActionDefinitionDetails.getName().equals(new Identifier("*"))
                    || tempActionDefinitionDetails.getName().equals(new Identifier(""))) { // requirement: 3.2.12.2.b
                invIndexList.add(new UInteger(index));
            }

            if (manager.list(tempActionDefinitionDetails.getName()) == null) { // Is the supplied name unique? requirement: 3.2.12.2.c
                ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.2.4.b
                newObjInstIds.add(manager.add(tempActionDefinitionDetails, source, connection.getConnectionDetails().getProviderURI())); //  requirement: 3.2.12.2.e

                if (configurationAdapter != null){
                    configurationAdapter.configurationChanged(this);
                }
            } else {
                dupIndexList.add(new UInteger(index));
            }
        }

        // Errors
        if (!invIndexList.isEmpty()){ // requirement: 3.2.12.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!dupIndexList.isEmpty()) // requirement: 3.2.12.3.3
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        // requirement: 3.2.12.2.d
        return newObjInstIds; // requirement: 3.2.12.2.e
    }

    @Override
    public void updateDefinition(LongList actionDefInstIds, ActionDefinitionDetailsList actionDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        ActionDefinitionDetails oldActionDefinitionDetails;
        LongList newActionDefInstIds = new LongList();
        ActionDefinitionDetailsList newActionDefDetails = new ActionDefinitionDetailsList();

        if (null == actionDefDetails || null == actionDefInstIds) { // Are the inputs null?
            throw new IllegalArgumentException("actionDefInstIds and actionDefDetails arguments must not be null");
        }

        for (int index = 0; index < actionDefInstIds.size(); index++) { // requirement: 3.2.13.2.e (incremental "for cycle" guarantees that)
            oldActionDefinitionDetails = manager.get(actionDefInstIds.get(index));

            if (oldActionDefinitionDetails == null) { // The object instance identifier could not be found? // requirement: 3.2.13.2.b
                unkIndexList.add(new UInteger(index));
                continue;
            }

            if (actionDefInstIds.get(index) == null) { // requirement: 3.2.13.2.c
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (actionDefInstIds.get(index).equals(0)) { // requirement: 3.2.13.2.c
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (manager.isActionDefinitionValid(oldActionDefinitionDetails, actionDefDetails.get(index))) { // Are the names equal? requirement: 3.2.13.2.e
                newActionDefInstIds.add(actionDefInstIds.get(index));
                newActionDefDetails.add(actionDefDetails.get(index));

                if (configurationAdapter != null){
                    configurationAdapter.configurationChanged(this);
                }
            } else {
                invIndexList.add(new UInteger(index));
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.2.13.2.1 (error: a)
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.2.13.2.2 (error: b)
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        for (int index = 0; index < newActionDefInstIds.size(); index++) {
            manager.update(newActionDefInstIds.get(index), newActionDefDetails.get(index), connection.getConnectionDetails());  // Change in the manager
        }

    }

    @Override
    public void removeDefinition(final LongList actionDefInstIds, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.7.12.2.1
        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == actionDefInstIds) // Is the input null?
        {
            throw new IllegalArgumentException("actionDefInstIds argument must not be null");
        }

        for (int index = 0; index < actionDefInstIds.size(); index++) {
            tempLong = actionDefInstIds.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.2.14.2.b
                tempLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            if (manager.exists(tempLong)) { // Does it match an existing definition? requirement: 3.2.14.2.c
                tempLongLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.2.14.2.c
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.2.14.3.1 (error: a, b)
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.2.14.2.f (Inserting the errors before this line guarantees that the requirement is met)
        for (Long tempLong2 : tempLongLst) {
            manager.delete(tempLong2);
        }

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }
        
        // COM archive must be left untouched. requirement: 3.2.14.2.e
    }

    /**
     * Reports the execution of the current progress stage
     *
     * @param success Flag for the successfulness of the stage
     * @param errorNumber Error number code, this value is software-specific. 
     * The interpretation of the value is defined by the consumer. If the success
     * flag is set to false, this field will not be used
     * @param progressStage The progress stage. The first stage would be 1.
     * @param totalNumberOfProgressStages The total number of stages.
     * @param actionInstId The actions instance identifier. This value allows the
     * consumer to know which action generated this report.
     */
    public void reportExecutionProgress(final boolean success, final UInteger errorNumber,
            final int progressStage, final int totalNumberOfProgressStages, final Long actionInstId) {
        // requirement: 3.2.8.h and 3.2.8.j
        manager.reportActivityExecutionEvent(success, errorNumber, 1 + progressStage, 2 + totalNumberOfProgressStages, actionInstId, null, connection.getConnectionDetails());
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
        if (!confSet.getObjType().equals(ActionHelper.ACTIONDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if(confSet.getObjInstIds().isEmpty()){
            manager.reconfigureDefinitions(new LongList(), new ActionDefinitionDetailsList());   // Reconfigures the Manager
            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        ActionDefinitionDetailsList pDefs = (ActionDefinitionDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                ActionHelper.ACTIONDEFINITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSet.getObjInstIds());

        manager.reconfigureDefinitions(confSet.getObjInstIds(), pDefs);   // Reconfigures the Manager

        return true;
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        HashMap<Long, Element> defObjs = manager.getCurrentDefinitionsConfiguration();

        ConfigurationObjectSet objsSet = new ConfigurationObjectSet();
        objsSet.setDomain(ConfigurationProviderSingleton.getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(ActionHelper.ACTIONDEFINITION_OBJECT_TYPE);

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSet);

        // Needs the Common API here!
        ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(list);

        return set;
    }

    @Override
    public COMService getCOMService() {
        return ActionHelper.ACTION_SERVICE;
    }


}
