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

import esa.mo.com.impl.util.DefinitionsManager;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetailsList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;

/**
 *
 * @author Cesar Coelho
 */
public final class ActionManager extends DefinitionsManager {
    
    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdAIns;
    private final transient ActionInvocationListener actions;   // transient: marks members that won't be serialized.


    public ActionManager (COMServicesProvider comServices, ActionInvocationListener actions){
        super(comServices);
        this.actions = actions;
        
        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdAIns = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
//            this.load(); // Load the file
        }else{
            
        }

    }
    
    @Override
    public Boolean compareName(Long objId, Identifier name) {
        return this.get(objId).getName().equals(name);
    }

    @Override
    public ElementList newDefinitionList() {
        return new ActionDefinitionDetailsList();
    }

    public ActionDefinitionDetails get(Long input) {
        return (ActionDefinitionDetails) this.getDefs().get(input);
    }
    
    public Long storeAndGenerateAInsobjId(ActionInstanceDetails aIns, Long related, SingleConnectionDetails connectionDetails){ 
        if (super.getArchiveService() == null) {
            uniqueObjIdAIns++;
///            if (uniqueObjIdAIns % SAVING_PERIOD  == 0) // It is used to avoid constant saving every time we generate a new obj Inst identifier.
//                this.save();
            return this.uniqueObjIdAIns;
        }else{
            ActionInstanceDetailsList aValList = new ActionInstanceDetailsList();
            aValList.add(aIns);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        ActionHelper.ACTIONINSTANCE_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(related, null, connectionDetails),
                        aValList,
                        null);

                if (objIds.size() == 1) {
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }
        
    }

    public ActionDefinitionDetailsList getAll(){
        return (ActionDefinitionDetailsList) this.getAllDefs();
    }

    public Long add(ActionDefinitionDetails definition, ObjectId source, SingleConnectionDetails connectionDetails){ // requirement: 3.3.2.5

        if (super.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.addDef(uniqueObjIdDef, definition);

//            this.save();
            return uniqueObjIdDef;
        }else{
            ActionDefinitionDetailsList defs = new ActionDefinitionDetailsList();
            defs.add(definition);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        ActionHelper.ACTIONDEFINITION_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
                        defs,
                        null);

                if (objIds.size() == 1) {
                    this.addDef(objIds.get(0), definition);
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
      
    public boolean update(Long objId, ActionDefinitionDetails definition, SingleConnectionDetails connectionDetails){ // requirement: 3.3.2.5
        Boolean success = this.updateDef(objId, definition);  // requirement: 3.7.2.13

        if (super.getArchiveService() != null) {  // It should also update on the COM Archive
            try {
                ActionDefinitionDetailsList defs = new ActionDefinitionDetailsList();
                defs.add(definition);

                ArchiveDetails archiveDetails = HelperArchive.getArchiveDetailsFromArchive(super.getArchiveService(), 
                        ActionHelper.ACTIONDEFINITION_OBJECT_TYPE, connectionDetails.getDomain(), objId);

                ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
                archiveDetailsList.add(archiveDetails);

                super.getArchiveService().update(
                        ActionHelper.ACTIONDEFINITION_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        archiveDetailsList,
                        defs,
                        null);

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

//        this.save();
        return success;
    }

    public boolean delete(Long objId){ // requirement: 3.3.2.5

        if (!this.deleteDef(objId)) {
            return false;
        }
        
        return true;
    }
    
    protected boolean isActionDefinitionValid(ActionDefinitionDetails oldDef, ActionDefinitionDetails newDef) {
        
        if (!oldDef.getName().equals(newDef.getName())                             ||
            !oldDef.getSeverity().equals(newDef.getSeverity())                     ||
            !oldDef.getProgressStepCount().equals(newDef.getProgressStepCount())   ){
            return false;
        }

        final ArgumentDefinitionDetailsList oldArguments = oldDef.getArguments();
        final ArgumentDefinitionDetailsList newArguments = newDef.getArguments();

        if (oldArguments.size() != newArguments.size()){
            return false;
        }
        
        for (int index = 0; index < oldArguments.size(); index++){
            ArgumentDefinitionDetails oldArgument = oldArguments.get(index);
            ArgumentDefinitionDetails newArgument = newArguments.get(index);
            
            if (oldArgument == null || newArgument == null){  // cannot compare, check next
                continue;
            }
            
            if (oldArgument.getRawType() != null && newArgument.getRawType() != null){
                if (!oldArgument.getRawType().equals(newArgument.getRawType()) ){
                    return false;
                }
            }

            if (oldArgument.getConversionCondition() != null && newArgument.getConversionCondition() != null){
                if (!oldArgument.getConversionCondition().equals(newArgument.getConversionCondition()) ){
                    return false;
                }
            }

            if (oldArgument.getConvertedType() != null && newArgument.getConvertedType() != null){
                if ( !oldArgument.getConvertedType().equals(newArgument.getConvertedType()) ){
                    return false;
                }
            }
        }

        if (oldDef.getArgumentIds() == null && newDef.getArgumentIds() == null){  // If both are null then skip the rest of the code
            return true;
        }

        if (oldDef.getArgumentIds() == null || newDef.getArgumentIds() == null){ // But if only one of them is null, well, then we have an error here
            return false;
        }

        if (oldArguments.size() != newArguments.size()){  // Are the list sizes different?
            return false;
        }
        
        for (int index = 0; index < oldDef.getArgumentIds().size(); index++){
            if (oldDef.getArgumentIds().get(index) != null && newDef.getArgumentIds().get(index) != null){
            
                if (!oldDef.getArgumentIds().get(index).getValue().equals(newDef.getArgumentIds().get(index).getValue())  ){
                    return false;
                }
            }
        }
        
        return true;
    
    }

    public boolean checkActionInstanceDetails(ActionInstanceDetails actionDetails, UIntegerList errorList) {
        
        ActionDefinitionDetails actionDef = this.get(actionDetails.getDefInstId());
        
        if (errorList != null){
            errorList.clear();
        }else{  // just for safety reasons. It is expected from whom calls the method, to submit an already initialized List!
            errorList = new UIntegerList();
        }
        
        if (actionDef == null){  // The action definition is not in the Provider
            return false;
        }

        int sizeDef = 0;
        int sizeArgVal = 0;
        
        if (actionDef.getArguments() != null){
            sizeDef = actionDef.getArguments().size();
        }

        if (actionDetails.getArgumentValues() != null){
            sizeArgVal = actionDetails.getArgumentValues().size();
        }
        
        if (sizeDef == 0 && sizeArgVal == 0){
            return true; // Nothing to be compared
        }
        
        // So, first of all, are we even comparing things of the same size?
        if ( sizeDef != sizeArgVal ){
            return false;
        }
        
        // Do the argument ids are not null? (it is optional)
        if (actionDef.getArgumentIds() != null && actionDetails.getArgumentIds() != null){
            // Are the argumentIds the same?
            for (int index = 0; index < actionDef.getArgumentIds().size(); index++ ){
                if (!(actionDef.getArgumentIds().get(index).equals(actionDetails.getArgumentIds().get(index)))){
                    errorList.add(new UInteger(index));
                }
            }
        }
        
        // If no errors happened, then return true!
        return errorList.isEmpty();

    }

    protected void forward(final Long actionInstId, final ActionInstanceDetails actionDetails, final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {

        Thread t = new Thread() {
            
            @Override
            public void run() {
                try {
                    final ActionDefinitionDetails actionDefinition = get(actionDetails.getDefInstId());
                    ObjectKey key = new ObjectKey (connectionDetails.getDomain(), actionInstId);
                    
                    URI uriTo = interaction.getMessageHeader().getURITo();
                    URI uriNextDestination = null;
                    String[] nodes = uriTo.toString().split("@");
                    
                    if (nodes.length > 1){ // Remove the first characters until the '@'; +1 below for the '@'
                        uriNextDestination = new URI( uriTo.toString().substring(nodes[0].length() + 1) );
                    }
                    
                    // Reception
                    ObjectId sourceRec = new ObjectId(ActionHelper.ACTIONINSTANCE_OBJECT_TYPE, key);
                    getActivityTrackingService().publishReceptionEvent(new URI(nodes[0]), interaction.getMessageHeader().getNetworkZone(), true, null, uriNextDestination, sourceRec);
            
                    UInteger errorNumber;

                    // Call the Action
                    if(actions != null){
                        errorNumber = actions.actionArrived(actionDefinition.getName(), actionDetails.getArgumentValues(),
                                actionInstId, actionDetails.getStageProgressRequired(), interaction);
                    }else{
                        errorNumber = new UInteger(0);
                    }
                    
                    // Publish forward success
                    ObjectId sourceFor = new ObjectId(ActionHelper.ACTIONINSTANCE_OBJECT_TYPE, key);
                    getActivityTrackingService().publishForwardEvent(new URI(nodes[0]), interaction.getMessageHeader().getNetworkZone(), (errorNumber == null), null, uriNextDestination, sourceFor);
                    
                } catch (MALInteractionException ex) {
                    Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MALException ex) {
                    Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            }
        };
        
        t.start();
    }
    
    protected void execute(final Long actionInstId, final ActionInstanceDetails actionDetails, 
            final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {

        Thread t = new Thread() {
            
            @Override
            public void run() {
                final ActionDefinitionDetails actionDefinition = get(actionDetails.getDefInstId());
            
                // Publish Event stating that the execution was initialized
                if (actionDetails.getStageStartedRequired()){  // ActionInstanceDetails field requirement
                    reportExecutionStart(true, null, actionDefinition.getProgressStepCount().getValue(), actionInstId, interaction, connectionDetails);
                }

                UInteger errorNumber;
                
                // Call the Action
                if(actions != null){
                    errorNumber = actions.actionArrived(actionDefinition.getName(), actionDetails.getArgumentValues(), 
                        actionInstId, actionDetails.getStageProgressRequired(), interaction);
                }else{
                    errorNumber = new UInteger(0);
                }
                
                // Publish Event stating that the execution was finished
                if (actionDetails.getStageCompletedRequired()){  // ActionInstanceDetails field requirement
                    reportExecutionComplete( (errorNumber == null) , errorNumber, actionDefinition.getProgressStepCount().getValue(), actionInstId, interaction, connectionDetails);
                }
                
            }
        };
        
        t.start();
    }

    protected void reportActivityExecutionEvent(final boolean success, final UInteger errorNumber, 
            final int executionStage, final int stageCount, final Long actionInstId, 
            final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {

        ObjectKey key = new ObjectKey (connectionDetails.getDomain(), actionInstId);
        ObjectId source = new ObjectId(ActionHelper.ACTIONINSTANCE_OBJECT_TYPE, key);
        
        try {

            if (this.getActivityTrackingService() != null){
                ObjectId executionEventLink;
                    // requirement 3.2.5.a
                    executionEventLink = this.getActivityTrackingService().publishExecutionEventOperation(connectionDetails.getProviderURI(), connectionDetails.getConfiguration().getNetwork(), success, executionStage, stageCount, null, source);

                if (!success){ // requirement 3.2.5.c
                    this.publishActionFailureEvent(errorNumber, actionInstId, executionEventLink, interaction, connectionDetails);
                }
            }
        
        } catch (MALInteractionException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    private void publishActionFailureEvent(final UInteger errorNumber, final Long related, 
            final ObjectId source, final MALInteraction interaction, final SingleConnectionDetails connectionDetails){
        
        UIntegerList errorNumbers = new UIntegerList();
        errorNumbers.add(errorNumber);

        // requirement: 3.2.5.c and 3.2.5.d and 3.2.5.e
        if (this.getEventService() != null){
            Long objId = this.getEventService().generateAndStoreEvent(
                    ActionHelper.ACTIONFAILURE_OBJECT_TYPE,
                    connectionDetails.getDomain(), 
                    errorNumber, 
                    related, 
                    source, 
                    interaction);
        
            this.getEventService().publishEvent(new URI(""), objId, 
                ActionHelper.ACTIONFAILURE_OBJECT_TYPE, related, source, errorNumbers);
        }
        
    }
    
    private void reportExecutionStart(final boolean success, final UInteger errorNumber, 
            final int totalNumberOfProgressStages, final Long actionInstId, final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {
        // requirement: 3.2.8.h and 3.2.8.i
        reportActivityExecutionEvent(success, errorNumber, 1, 2 + totalNumberOfProgressStages, actionInstId, interaction, connectionDetails);
    }

    /**
     *
     * @param success
     * @param errorNumber
     * @param totalNumberOfProgressStages
     * @param actionInstId
     */
    private void reportExecutionComplete(final boolean success, final UInteger errorNumber, 
            final int totalNumberOfProgressStages, final Long actionInstId, final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {
        // requirement: 3.2.8.h and 3.2.8.k
        reportActivityExecutionEvent(success, errorNumber, 2 + totalNumberOfProgressStages, 2 + totalNumberOfProgressStages, actionInstId, interaction, connectionDetails);
    }
    
    
}
