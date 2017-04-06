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
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import java.io.IOException;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetailsList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;

/**
 *
 * @author Cesar Coelho
 */
public final class ActionManager extends DefinitionsManager {

    private Long uniqueObjIdIdentity;
    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdAIns;
    private final ActionInvocationListener actions;
    private final HashMap<Long, ActionInstanceDetails> actionInstances = new HashMap<Long, ActionInstanceDetails>();
    private final ExecutorService actionsExecutor = Executors.newCachedThreadPool(new ActionThreadFactory("ActionsExecutor"));

    public ActionManager(COMServicesProvider comServices, ActionInvocationListener actions) {
        super(comServices);
        this.actions = actions;

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdIdentity = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdAIns = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
//            this.load(); // Load the file
        } else {

        }

    }

    public ActionDefinitionDetails getActionDefinitionFromIdentityId(Long identityId) {
        return (ActionDefinitionDetails) this.getDefinition(identityId);
    }

    public ActionDefinitionDetails getActionDefinitionFromDefId(Long defId) {
        return (ActionDefinitionDetails) this.getDefinition(getIdentity(defId));
    }

    public Long storeAndGenerateAInsobjId(ActionInstanceDetails aIns, Long related, final URI uri) {
        if (super.getArchiveService() == null) {
            uniqueObjIdAIns++;
///            if (uniqueObjIdAIns % SAVING_PERIOD  == 0) // It is used to avoid constant saving every time we generate a new obj Inst identifier.
//                this.save();
            return this.uniqueObjIdAIns;
        } else {
            ActionInstanceDetailsList aValList = new ActionInstanceDetailsList(1);
            aValList.add(aIns);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        ActionHelper.ACTIONINSTANCE_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(related, null, uri),
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

//    public ActionDefinitionDetailsList getAll(){
//        return (ActionDefinitionDetailsList) this.listgetAllDefs();
//    }
    public ObjectInstancePair add(ActionCreationRequest creationRequest, ObjectId source, URI uri) { // requirement: 3.3.2.5
        ObjectInstancePair newIdPair = new ObjectInstancePair();
        final Identifier name = creationRequest.getName();
        final ActionDefinitionDetails actionDefDetails = creationRequest.getActionDefDetails();

        if (super.getArchiveService() == null) {
            //add to providers local list
            uniqueObjIdIdentity++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newIdPair = new ObjectInstancePair(uniqueObjIdIdentity, uniqueObjIdDef);
        } else {
            try {
                //requirement: 3.2.12.2.e: if an ActionName ever existed before, use the old ActionIdentity-Object by retrieving it from the archive
                //check if the name existed before and retrieve id if found
                Long identityId = retrieveIdentityIdByNameFromArchive(ConfigurationProviderSingleton.getDomain(), 
                        name, ActionHelper.ACTIONIDENTITY_OBJECT_TYPE);

                //in case the ActionName never existed before, create a new identity
                if (identityId == null) {
                    IdentifierList names = new IdentifierList(1);
                    //requirement: 3.2.4.b
                    names.add(name);
                    //add identity to the archive 3.2.7.a
                    LongList identityIds = super.getArchiveService().store(true,
                            ActionHelper.ACTIONIDENTITY_OBJECT_TYPE, //requirement: 3.2.4.a
                            ConfigurationProviderSingleton.getDomain(),
                            HelperArchive.generateArchiveDetailsList(null, source, uri), //requirement: 3.2.4.e
                            names,
                            null);

                    //there is only one identity created, so get the id and set it as the related id
                    identityId = identityIds.get(0);
                }
                ActionDefinitionDetailsList defs = new ActionDefinitionDetailsList();
                defs.add(actionDefDetails);
                //add definition to the archive requirement: 3.2.7.b
                LongList defIds = super.getArchiveService().store(true,
                        ActionHelper.ACTIONDEFINITION_OBJECT_TYPE, //requirement: 3.2.4.c
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(identityId, source, uri), //requirement: 3.2.4.d, f
                        defs,
                        null);

                newIdPair = new ObjectInstancePair(identityId, defIds.get(0));
            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //add to internal lists
        this.addIdentityDefinition(newIdPair.getObjIdentityInstanceId(), name, newIdPair.getObjDefInstanceId(), actionDefDetails);
        return newIdPair;
    }

    public Long update(Long identityId, ActionDefinitionDetails definition, ObjectId source, URI uri) { // requirement: 3.3.2.5
        Long newDefId = null;

        if (super.getArchiveService() == null) { //only update locally
            //add to providers local list
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newDefId = uniqueObjIdDef;

        } else {  // update in the COM Archive
            try {
                ActionDefinitionDetailsList defs = new ActionDefinitionDetailsList();
                defs.add(definition);

                //create a new ActionDefinition 
                LongList defIds = super.getArchiveService().store(true,
                        ActionHelper.ACTIONDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(identityId, source, uri),
                        defs,
                        null);

                newDefId = defIds.get(0);
            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //update internal list
        this.updateDef(identityId, newDefId, definition);

        return newDefId;
    }

    public boolean delete(Long objId) { // requirement: 3.2.14.2.e
        if (!this.deleteIdentity(objId)) {
            return false;
        }
        return true;
    }

    protected boolean isActionDefinitionValid(ActionDefinitionDetails oldDef, ActionDefinitionDetails newDef) {

        if (//!oldDef.getName().equals(newDef.getName()) ||
                !oldDef.getCategory().equals(newDef.getCategory())
                || !oldDef.getProgressStepCount().equals(newDef.getProgressStepCount())) {
            return false;
        }

        final ArgumentDefinitionDetailsList oldArguments = oldDef.getArguments();
        final ArgumentDefinitionDetailsList newArguments = newDef.getArguments();

        if (oldArguments.size() != newArguments.size()) {
            return false;
        }

        for (int index = 0; index < oldArguments.size(); index++) {
            ArgumentDefinitionDetails oldArgument = oldArguments.get(index);
            ArgumentDefinitionDetails newArgument = newArguments.get(index);

            if (oldArgument == null || newArgument == null) {  // cannot compare, check next
                continue;
            }

            if (oldArgument.getRawType() != null && newArgument.getRawType() != null) {
                if (!oldArgument.getRawType().equals(newArgument.getRawType())) {
                    return false;
                }
            }

            if (oldArgument.getConditionalConversions() != null && newArgument.getConditionalConversions() != null) {
                if (!oldArgument.getConditionalConversions().equals(newArgument.getConditionalConversions())) {
                    return false;
                }
            }

            if (oldArgument.getConvertedType() != null && newArgument.getConvertedType() != null) {
                if (!oldArgument.getConvertedType().equals(newArgument.getConvertedType())) {
                    return false;
                }
            }
        }

        if (oldDef.getArgumentIds() == null && newDef.getArgumentIds() == null) {  // If both are null then skip the rest of the code
            return true;
        }

        if (oldDef.getArgumentIds() == null || newDef.getArgumentIds() == null) { // But if only one of them is null, well, then we have an error here
            return false;
        }

        if (oldArguments.size() != newArguments.size()) {  // Are the list sizes different?
            return false;
        }

        for (int index = 0; index < oldDef.getArgumentIds().size(); index++) {
            if (oldDef.getArgumentIds().get(index) != null && newDef.getArgumentIds().get(index) != null) {

                if (!oldDef.getArgumentIds().get(index).getValue().equals(newDef.getArgumentIds().get(index).getValue())) {
                    return false;
                }
            }
        }

        return true;

    }

    public boolean checkActionInstanceDetails(ActionInstanceDetails actionInstance, UIntegerList errorList) {
        //TODO extend this method to support the external verification. create a new Interface -> actionservice
        ActionDefinitionDetails actionDef = this.getActionDefinitionFromDefId(actionInstance.getDefInstId());

        if (errorList != null) {
            errorList.clear();
        } else {  // just for safety reasons. It is expected from whom calls the method, to submit an already initialized List!
            errorList = new UIntegerList();
        }

        if (actionDef == null) {  // The action definition is not in the Provider
            return false;
        }

        int sizeDef = 0;
        int sizeArgVal = 0;

        if (actionDef.getArguments() != null) {
            sizeDef = actionDef.getArguments().size();
        }

        if (actionInstance.getArgumentValues() != null) {
            sizeArgVal = actionInstance.getArgumentValues().size();
        }

        // So, first of all, are we even comparing things of the same size?
        if (sizeArgVal != sizeDef) {
            for (int i = min(sizeDef, sizeArgVal); i < max(sizeDef, sizeArgVal); i++) {
                errorList.add(new UInteger(i));
            }
            return false;
        }

        // Do the argument ids are not null? (it is optional)
        if (actionDef.getArgumentIds() != null && actionInstance.getArgumentIds() != null) {
            // Ids must be of the same size as well
            int sizeDefArgIds = actionDef.getArgumentIds().size();
            int sizeInstArgIds = actionInstance.getArgumentIds().size();
            if (sizeDefArgIds != sizeInstArgIds) {
                for (int i = min(sizeDefArgIds, sizeInstArgIds); i < max(sizeDefArgIds, sizeInstArgIds); i++) {
                    errorList.add(new UInteger(i));
                }
                return false;
            }
            // Are the argumentIds the same?
            for (int index = 0; index < sizeDefArgIds; index++) {
                if (!(actionDef.getArgumentIds().get(index).equals(actionInstance.getArgumentIds().get(index)))) {
                    errorList.add(new UInteger(index));
                }
                if (!errorList.isEmpty()) {
                    return false;
                }
            }
            // Are the argument types the same?
            for (int index = 0; index < sizeDefArgIds; index++) {
                int defRawType = actionDef.getArguments().get(index).getRawType().intValue();
                int defConvType = actionDef.getArguments().get(index).getConvertedType().intValue();
                int instType = actionInstance.getArgumentValues().get(index).getValue().getTypeShortForm();
                boolean isRawValue = (actionInstance.getIsRawValue() == null)
                        || (actionInstance.getIsRawValue().get(index) == null)
                        || (actionInstance.getIsRawValue().get(index));
                if ((isRawValue && (defRawType != instType))
                        || (!isRawValue && (defConvType != instType))) {
                    errorList.add(new UInteger(index));
                }
                if (!errorList.isEmpty()) {
                    return false;
                }
            }
        }
        boolean preCheckResult = actions.preCheck(actionDef, actionInstance, errorList);
        if (!errorList.isEmpty()) {
            return false;
        }
        return preCheckResult;
    }

    protected void forward(final Long actionInstId, final ActionInstanceDetails actionDetails,
            final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {
        //TODO: after issue I expect to get the identity-id here -> issue #179
        final Identifier name = getName(actionDetails.getDefInstId());

        actionsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    final ActionDefinitionDetails actionDefinition = getActionDefinitionFromDefId(actionDetails.getDefInstId());

                    ObjectKey key = new ObjectKey(ConfigurationProviderSingleton.getDomain(), actionInstId);

                    URI uriTo = interaction.getMessageHeader().getURITo();
                    URI uriNextDestination = null;
                    String[] nodes = uriTo.toString().split("@");

                    if (nodes.length > 1) { // Remove the first characters until the '@'; +1 below for the '@'
                        uriNextDestination = new URI(uriTo.toString().substring(nodes[0].length() + 1));
                    }

                    // Reception
                    ObjectId sourceRec = new ObjectId(ActionHelper.ACTIONINSTANCE_OBJECT_TYPE, key);
                    getActivityTrackingService().publishReceptionEvent(new URI(nodes[0]),
                            interaction.getMessageHeader().getNetworkZone(), true, null, uriNextDestination, sourceRec);

                    UInteger errorNumber;

                    // Call the Action
                    if (actions != null) {
                        errorNumber = actions.actionArrived(name, actionDetails.getArgumentValues(),
                                actionInstId, actionDetails.getStageProgressRequired(), interaction);
                    } else {
                        errorNumber = new UInteger(0);
                    }

                    // Publish forward success
                    ObjectId sourceFor = new ObjectId(ActionHelper.ACTIONINSTANCE_OBJECT_TYPE, key);
                    getActivityTrackingService().publishForwardEvent(new URI(nodes[0]), interaction.getMessageHeader().getNetworkZone(),
                            (errorNumber == null), null, uriNextDestination, sourceFor);
                } catch (MALInteractionException ex) {
                    Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
                } catch (MALException ex) {
                    Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    protected void execute(final Long actionInstId, final ActionInstanceDetails actionDetails,
            final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {

        actionInstances.put(actionInstId, actionDetails);
        final Identifier name = this.getName(actionDetails.getDefInstId());

        actionsExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final ActionDefinitionDetails actionDefinition = getActionDefinitionFromDefId(actionDetails.getDefInstId());

                //from here on: requirement 3.2.8.b
                // Publish Event stating that the execution was initialized
                if (actionDetails.getStageStartedRequired()) {  // ActionInstanceDetails field requirement
                    reportExecutionStart(true, null, actionDefinition.getProgressStepCount().getValue(),
                            actionInstId, interaction, connectionDetails);
                }

                UInteger errorNumber;

                // Call the Action
                if (actions != null) {
                    //requirement: 3.2.8.j, 3.2.5.a -> actionArrived will send the progress-events
                    errorNumber = actions.actionArrived(name, actionDetails.getArgumentValues(),
                            actionInstId, actionDetails.getStageProgressRequired(), interaction);
                } else {
                    errorNumber = new UInteger(0);
                }

                // Publish Event stating that the execution was finished
                if (actionDetails.getStageCompletedRequired()) {  // ActionInstanceDetails field requirement
                    reportExecutionComplete((errorNumber == null), errorNumber,
                            actionDefinition.getProgressStepCount().getValue(),
                            actionInstId, interaction, connectionDetails);
                }

                actionInstances.remove(actionInstId);

//                //TODO: i think the failure was published in actionArrived method and only if it wasnt, the following completion event shall be published -> issue
//                // Publish Event stating that the execution was finished
//				success = actions.getFailureStage() != actionDefinition.getProgressStepCount().getValue() + 2;
//                if (actionDetails.getStageCompletedRequired()) {  // ActionInstanceDetails field requirement
//                    reportExecutionComplete(success, success ? null : actions.getFailureCode(), actionDefinition.getProgressStepCount().getValue(), actionInstId, interaction, connectionDetails);
//                }
            }
        });

    }

    protected ActionInstanceDetails getActionInstance(final Long id) {
        return actionInstances.get(id);
    }

    protected void reportActivityExecutionEvent(final boolean success, final UInteger errorNumber,
            final int executionStage, final int stageCount, final Long actionInstId,
            final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {
        ObjectKey key = new ObjectKey(ConfigurationProviderSingleton.getDomain(), actionInstId);
        ObjectId source = new ObjectId(ActionHelper.ACTIONINSTANCE_OBJECT_TYPE, key);

        try {
            if (this.getActivityTrackingService() != null) {
                ObjectId executionEventLink;
                try {
                    // requirement 3.2.5.a ,  3.2.7.d -> will be done in the "publishExecutionEventOperation"-method.
                    executionEventLink = this.getActivityTrackingService().publishExecutionEventOperation(connectionDetails.getProviderURI(),
                            ConfigurationProviderSingleton.getNetwork(), success, executionStage, stageCount, null, source);

                    if (!success) { // requirement 3.2.5.c
                        //TODO: requirement 3.2.5.c is the source really the completionEvent? -> issue #189 
                        this.publishActionFailureEvent(errorNumber, actionInstId, executionEventLink, interaction, connectionDetails);
                    }
                } catch (MALInteractionException ex) {
                    Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (MALException ex) {
            Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void publishActionFailureEvent(final UInteger errorNumber, final Long related,
            final ObjectId source, final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {
        // requirement: 3.2.5.f
        final UIntegerList errorNumbers = new UIntegerList(1);
        errorNumbers.add(errorNumber);

        // requirement: 3.2.5.c and 3.2.5.d and 3.2.5.e
        if (this.getEventService() != null) {
            Long objId = this.getEventService().generateAndStoreEvent(
                    ActionHelper.ACTIONFAILURE_OBJECT_TYPE,
                    ConfigurationProviderSingleton.getDomain(),
                    errorNumber,
                    related,
                    source,
                    interaction);

            try {
                this.getEventService().publishEvent(new URI(""), objId,
                        ActionHelper.ACTIONFAILURE_OBJECT_TYPE, related, source, errorNumbers);
            } catch (IOException ex) {
                Logger.getLogger(ActionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void reportExecutionStart(final boolean success, final UInteger errorNumber,
            final int totalNumberOfProgressStages, final Long actionInstId,
            final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {
        // requirement: 3.2.8.h and 3.2.8.i
        reportActivityExecutionEvent(success, errorNumber, 1, 2 + totalNumberOfProgressStages,
                actionInstId, interaction, connectionDetails);
    }

    /**
     *
     * @param success
     * @param errorNumber
     * @param totalNumberOfProgressStages
     * @param actionInstId
     */
    private void reportExecutionComplete(final boolean success, final UInteger errorNumber,
            final int totalNumberOfProgressStages, final Long actionInstId,
            final MALInteraction interaction, final SingleConnectionDetails connectionDetails) {
        // requirement: 3.2.8.h and 3.2.8.k
        reportActivityExecutionEvent(success, errorNumber, 2 + totalNumberOfProgressStages,
                2 + totalNumberOfProgressStages, actionInstId, interaction, connectionDetails);
    }

    /**
     * The database backend thread factory
     */
    static class ActionThreadFactory implements ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        ActionThreadFactory(String prefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup()
                    : Thread.currentThread().getThreadGroup();
            namePrefix = prefix + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

}
