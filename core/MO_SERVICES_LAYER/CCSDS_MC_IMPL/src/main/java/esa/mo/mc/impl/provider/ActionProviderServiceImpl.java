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
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
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
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequestList;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import esa.mo.reconfigurable.service.ReconfigurableService;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;

/**
 * Action service Provider.
 */
public class ActionProviderServiceImpl extends ActionInheritanceSkeleton implements ReconfigurableService {

    private final static String IS_INTERMEDIATE_RELAY_PROPERTY = "esa.mo.mc.impl.provider.ActionProviderServiceImpl.isIntermediateRelay";
    private MALProvider actionServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    protected ActionManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private ConfigurationChangeListener configurationAdapter;

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
            Logger.getLogger(ActionProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public void setOnConfigurationChangeListener(ConfigurationChangeListener configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
    }

    @Override //requirement: 3.2.3
    public void submitAction(Long actionInstId, ActionInstanceDetails actionDetails,
            MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList invIndexList = new UIntegerList();
        boolean unknown = false;

        //from here on: requirement 3.2.8.a, c
        if ("true".equals(System.getProperty(IS_INTERMEDIATE_RELAY_PROPERTY))) {
            // Forward requirement: 3.2.8.c in conjunction with requirement in standard: "MISSION OPERATIONS COMMON OBJECT MODEL" 3.5.3.3, 4
            manager.forward(actionInstId, actionDetails, interaction, connection.getConnectionDetails());
            return; // Do nothing else...
        }

        // Publish first Acceptance event for submitAction operation
        // source for submitAction ACCEPTANCE event is the OperationActivity instance id, which is the transaction id of this submitAction operation
        ObjectId saSource = manager.getActivityTrackingService().storeCOMOperationActivity(interaction, null);  // requirement: 3.2.4.f  and 3.2.4.g

        try {
            //body of AcceptanceEvent is true? -> issue #187
            // requirement: 3.2.8.e
            manager.getActivityTrackingService().publishAcceptanceEventOperation(interaction, true, null, saSource); // requirement: f, g
        } catch (MALInteractionException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // requirement: 3.2.8.e
        boolean accepted;
//        if (!manager.existsIdentity(manager.getIdentity(actionDetails.getDefInstId()))) {
        if (!manager.existsDef(actionDetails.getDefInstId())) {
            accepted = false;
            unknown = true;
        } else {
            // Check the ActionInstanceDetails
            accepted = manager.checkActionInstanceDetails(actionDetails, invIndexList); // requirement: 3.2.9.2.b
        }

        // Publish the second Acceptance event
        try {
            // source for ActionInstance ACCEPTANCE event is the ActionInstance object id
            ObjectId source = new ObjectId(ActionHelper.ACTIONINSTANCE_OBJECT_TYPE,
                    new ObjectKey(ConfigurationProviderSingleton.getDomain(), actionInstId)); // requirement: 3.2.8.f  
            //body of AcceptanceEvent is value of "accepted"? -> issue #187
            manager.getActivityTrackingService().publishAcceptanceEventOperation(interaction, accepted, null, source); // requirement: 3.2.8.e, f, g  
        } catch (MALInteractionException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.2.9.3.1
            manager.getActivityTrackingService().publishExecutionEventSubmitAck(interaction, false, saSource); // requirement: c
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (unknown) { // requirement: 3.2.9.3.2
            manager.getActivityTrackingService().publishExecutionEventSubmitAck(interaction, false, saSource); // requirement: c
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        // If it was accepted then execute the action!
        if (accepted) { // requirement: 3.2.9.2c is met because the "execute"-method is started asynchronously. so an ACK will be send straight away
            manager.execute(actionInstId, actionDetails, interaction, connection.getConnectionDetails()); // The execution events are generated within the execute method; requirement: 3.2.9.2.b
        }
        manager.getActivityTrackingService().publishExecutionEventSubmitAck(interaction, accepted, saSource); // requirement: c
    }

    @Override
    public Boolean preCheckAction(ActionInstanceDetails actionDetails, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList invIndexList = new UIntegerList();

        // requirement: 3.2.10.3.2
        if (!manager.existsDef(actionDetails.getDefInstId())) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        // requirement: 3.2.10.2.a, 3.2.10.2.b
        //TODO: check the checkActionInstanceDetails-mehod after spec-update -> issue #99
        boolean accepted = manager.checkActionInstanceDetails(actionDetails, invIndexList);

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.2.10.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        return accepted;
    }

    @Override
    public ObjectInstancePairList listDefinition(final IdentifierList actionNames,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        ObjectInstancePairList outPairLst = new ObjectInstancePairList();

        if (null == actionNames) { // Is the input null?
            throw new IllegalArgumentException("actionNames argument must not be null");
        }

        boolean wildcardFound = false;
        for (Identifier actionName : actionNames) {  // requirement: 3.2.11.2.f
            // Check for the wildcard
            if (actionName.toString().equals("*")) {  // requirement: 3.2.11.2.b
                outPairLst.addAll(manager.listAllIdentityDefinitions()); // ... add all in a row; requirement: 3.2.11.2.e
                wildcardFound = true;
                break;
            }
        }

        if (!wildcardFound) {
            UIntegerList unkIndexList = new UIntegerList();
            for (int i = 0; i < actionNames.size(); i++) { //requirement: 3.2.11.2.f foreach-cycle steps through list in order
                Identifier actionName = actionNames.get(i);

                final ObjectInstancePair idPair = manager.getIdentityDefinition(actionName);
                if (idPair == null) {  //requirement: 3.2.11.2.d
                    unkIndexList.add(new UInteger(i));
                } else {
                    outPairLst.add(idPair);  // requirement: 3.2.11.2.a, 3.2.11.2.e
                }
            }

            // Errors
            if (!unkIndexList.isEmpty()) // requirement: 3.2.11.3.1 (error: a and b)
            {
                throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            }
        }

        return outPairLst;  // requirement: 3.4.9.2.d
    }

    @Override
    public ObjectInstancePairList addAction(ActionCreationRequestList actionCreationRequestList,
            MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectInstancePairList newObjInstIds = new ObjectInstancePairList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();

        if (null == actionCreationRequestList) // Is the input null?
        {
            throw new IllegalArgumentException("actionDefDetails argument must not be null");
        }

        //do the checks
        for (int index = 0; index < actionCreationRequestList.size(); index++) {
            ActionCreationRequest tempActionCreationRequest = actionCreationRequestList.get(index);
            // Check if the name field of the ActionDefinition is invalid.
            if (tempActionCreationRequest.getName() == null
                    || tempActionCreationRequest.getName().equals(new Identifier("*"))
                    || tempActionCreationRequest.getName().equals(new Identifier(""))) { // requirement: 3.2.12.2.b
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (manager.getIdentity(tempActionCreationRequest.getName()) != null) { // Is the supplied name unique? requirement: 3.2.12.2.c
                dupIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        // returning errors before creating the object -> requirement: 3.2.12.2.d
        if (!invIndexList.isEmpty()) { // requirement: 3.2.12.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }
        if (!dupIndexList.isEmpty()) { // requirement: 3.2.12.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        //add the definition
        for (int index = 0; index < actionCreationRequestList.size(); index++) { // requirement: 3.2.12.2.f (incremental "for cycle" guarantees that)
            ObjectId source;
            source = manager.storeCOMOperationActivity(interaction); // requirement: 3.2.4.e
            newObjInstIds.add(manager.add(actionCreationRequestList.get(index), source,
                    connection.getPrimaryConnectionDetails().getProviderURI())); //  requirement: 3.2.12.2.e, g
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return newObjInstIds; // requirement: 3.2.12.2.f
    }

    @Override
    public LongList updateDefinition(LongList actionDefInstIds, ActionDefinitionDetailsList actionDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == actionDefDetails || null == actionDefInstIds) { // Are the inputs null?
            throw new IllegalArgumentException("actionDefInstIds and actionDefDetails arguments must not be null");
        }

        //do the checks
        for (int index = 0; index < actionDefInstIds.size(); index++) {
            final Long identityId = actionDefInstIds.get(index);

            if (identityId == null || identityId == 0 //requirement: 3.2.13.2.c: id is Null or 0?
                    || actionDefInstIds.size() != actionDefDetails.size()) { //requirement: 3.2.13.2.f
                invIndexList.add(new UInteger(index));
                continue;
            }
            ActionDefinitionDetails actionDefinitionDetails = manager.getActionDefinitionFromIdentityId(identityId);  // requirement: 3.2.13.2.a
            if (actionDefinitionDetails == null) { // The object instance identifier could not be found? // requirement: 3.2.13.2.b
                unkIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        // returning errors before creating the object -> requirement: 3.2.13.2.g
        if (!invIndexList.isEmpty()) { // requirement: 3.2.13.2.1 (error: a)
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.2.13.2.2 (error: b)
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        LongList newDefIds = new LongList();
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.2.4.e
        for (int index = 0; index < actionDefInstIds.size(); index++) { // requirement: 3.2.13.2.e, k (incremental "for cycle" guarantees that)
            newDefIds.add(manager.update(actionDefInstIds.get(index), actionDefDetails.get(index),
                    source, connection.getPrimaryConnectionDetails().getProviderURI()));  // Change in the manager; requirement: 3.2.13.2.d, g, h
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return newDefIds;
    }

    @Override
    public void removeAction(final LongList actionInstIds, final MALInteraction interaction)
            throws MALException, MALInteractionException { // requirement: 3.7.12.2.1
        UIntegerList unkIndexList = new UIntegerList();
        Long tempIdentity;
        LongList tempIdentityLst = new LongList();

        if (null == actionInstIds) { // Is the input null?
            throw new IllegalArgumentException("actionDefInstIds argument must not be null");
        }

        for (int index = 0; index < actionInstIds.size(); index++) {
            tempIdentity = actionInstIds.get(index); // requirement: 3.2.14.2.a

            if (tempIdentity == 0) {  // Is it the wildcard '0'? requirement: 3.2.14.2.b
                tempIdentityLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempIdentityLst.addAll(manager.listAllIdentities()); // ... add all in a row
                unkIndexList.clear();
                break;
            }

            if (!manager.existsIdentity(tempIdentity)) { // Does it match an existing definition? requirement: 3.2.14.2.c
                unkIndexList.add(new UInteger(index)); // requirement: 3.2.14.2.c
            } else {
                tempIdentityLst.add(tempIdentity);
            }
        }

        // Errors
        // returning errors before removing the object -> requirement: 3.2.14.2.g
        if (!unkIndexList.isEmpty()) { // requirement: 3.2.14.3.1 (error: a, b)
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        for (Long tempIdentity2 : tempIdentityLst) {
            manager.delete(tempIdentity2); // COM archive must be left untouched. requirement: 3.2.14.2.e
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }
    }

    /**
     * Reports the execution of the current progress stage
     *
     * @param success Flag for the successfulness of the stage
     * @param errorNumber Error number code, this value is software-specific.
     * The interpretation of the value is defined by the consumer. If the
     * success flag is set to false, this field will not be used
     * @param progressStage The progress stage. The first stage would be 1.
     * @param totalNumberOfProgressStages The total number of stages.
     * @param actionInstId The actions instance identifier. This value allows
     * the consumer to know which action generated this report.
     * @throws IOException if the definition has a totalNumberOfProgressStages
     * different from the on supplied
     */
    public void reportExecutionProgress(final boolean success, final UInteger errorNumber,
            final int progressStage, final int totalNumberOfProgressStages, 
            final Long actionInstId) throws IOException {
        // Some validation
        if (progressStage < 1) {
            throw new IOException("The first progress stage must be 1.");
        }

        final ActionInstanceDetails actionInstance = manager.getActionInstance(actionInstId);

        if (actionInstance != null) {
            // Aditional validation can be performed!
            final ActionDefinitionDetails actionDefinition = manager.getActionDefinitionFromDefId(actionInstance.getDefInstId());

            if (actionDefinition == null) {
                throw new IOException("The submitted actionInstId could not be found.");
            }

            UShort totalSteps = actionDefinition.getProgressStepCount();

            if (totalSteps.getValue() == 0) {
                throw new IOException("The Action Definition includes 0 progress step count and so, it cannot be reported on it.");
            }
        }

        // requirement: 3.2.8.h and 3.2.8.j
        manager.reportActivityExecutionEvent(success, errorNumber, 1 + progressStage,
                2 + totalNumberOfProgressStages, actionInstId, null, connection.getConnectionDetails());
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
        if (!confSet0.getObjType().equals(ActionHelper.ACTIONDEFINITION_OBJECT_TYPE)
                && !confSet1.getObjType().equals(ActionHelper.ACTIONDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        if (!confSet0.getObjType().equals(ActionHelper.ACTIONIDENTITY_OBJECT_TYPE)
                && !confSet1.getObjType().equals(ActionHelper.ACTIONIDENTITY_OBJECT_TYPE)) {
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
                    new LongList(), new ActionDefinitionDetailsList());  // Reconfigures the Manager

            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        ConfigurationObjectSet confSetDefs = (confSet0.getObjType().equals(ActionHelper.ACTIONDEFINITION_OBJECT_TYPE)) ? confSet0 : confSet1;

        ActionDefinitionDetailsList pDefs = (ActionDefinitionDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                ActionHelper.ACTIONDEFINITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetDefs.getObjInstIds());

        ConfigurationObjectSet confSetIdents = (confSet0.getObjType().equals(ActionHelper.ACTIONIDENTITY_OBJECT_TYPE)) ? confSet0 : confSet1;

        IdentifierList idents = (IdentifierList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                ActionHelper.ACTIONIDENTITY_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetIdents.getObjInstIds());

        manager.reconfigureDefinitions(confSetIdents.getObjInstIds(), idents,
                confSetDefs.getObjInstIds(), pDefs);   // Reconfigures the Manager

        return true;
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        /*
        HashMap<Identity, Definition> defObjs = manager.getCurrentDefinitionsConfiguration();

        ConfigurationObjectSet objsSet = new ConfigurationObjectSet();
        objsSet.setDomain(ConfigurationProviderSingleton.getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(ActionHelper.ACTIONDEFINITION_OBJECT_TYPE);

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSet);
         */
        ConfigurationObjectSetList list = manager.getCurrentConfiguration();
        list.get(0).setObjType(ActionHelper.ACTIONIDENTITY_OBJECT_TYPE);
        list.get(1).setObjType(ActionHelper.ACTIONDEFINITION_OBJECT_TYPE);

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
