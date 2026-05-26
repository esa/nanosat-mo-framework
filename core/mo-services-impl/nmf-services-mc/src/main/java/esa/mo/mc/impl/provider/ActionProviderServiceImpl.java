/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft - v2.4
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
import esa.mo.mc.impl.interfaces.ActionInvocationListener;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;
import esa.mo.reconfigurable.service.ReconfigurableService;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.DuplicateException;
import org.ccsds.moims.mo.com.InvalidException;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.ActionServiceInfo;
import org.ccsds.moims.mo.mc.action.provider.ActionInheritanceSkeleton;
import org.ccsds.moims.mo.mc.action.provider.MonitorExecutionPublisher;
import org.ccsds.moims.mo.mc.structures.*;

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
    private MonitorExecutionPublisher publisher;
    private boolean isRegistered = false;
    private final Object lock = new Object();

    /**
     * cCreates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices The COM services.
     * @param actionListener The action listener.
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices,
            ActionInvocationListener actionListener) throws MALException {
        long timestamp = System.currentTimeMillis();

        publisher = createMonitorExecutionPublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE, ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT, null, new UInteger(0));

        // Shut down old service transport
        if (null != actionServiceProvider) {
            connection.closeAll();
        }

        actionServiceProvider = connection.startService(ActionHelper.ACTION_SERVICE, true, this);

        running = true;
        manager = new ActionManager(comServices, actionListener);

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(ActionProviderServiceImpl.class.getName()).info(
                "Action service READY! (" + timestamp + " ms)");
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

    @Override
    public Long executeAction(ExecutionRequest executionRequest, MALInteraction interaction)
            throws MALInteractionException, MALException {
        UIntegerList invIndexList = new UIntegerList();

        if ("true".equals(System.getProperty(IS_INTERMEDIATE_RELAY_PROPERTY))) {
            Long executionId = manager.storeAndGenerateExecReqId(executionRequest, executionRequest.getDefinitionId(),
                    connection.getPrimaryConnectionDetails().getProviderURI());
            manager.forward(executionId, executionRequest, interaction, connection.getConnectionDetails());
            return executionId;
        }

        if (!manager.existsDef(executionRequest.getDefinitionId())) {
            throw new MALInteractionException(new UnknownException(null));
        }

        boolean accepted = manager.checkExecutionRequest(executionRequest, invIndexList);

        if (!invIndexList.isEmpty()) {
            throw new MALInteractionException(new InvalidException(invIndexList));
        }

        if (!accepted) {
            throw new MALInteractionException(new InvalidException(invIndexList));
        }

        Long executionId = manager.storeAndGenerateExecReqId(executionRequest,
                executionRequest.getDefinitionId(),
                connection.getPrimaryConnectionDetails().getProviderURI());

        manager.execute(executionId, executionRequest,
                interaction, this, connection.getConnectionDetails());

        return executionId;
    }

    @Override
    public LongList listDefinition(final IdentifierList actionNames,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        LongList outPairLst = new LongList();

        if (actionNames == null) { // Is the input null?
            throw new IllegalArgumentException("actionNames argument must not be null");
        }

        boolean wildcardFound = false;
        for (Identifier actionName : actionNames) {  // requirement: 3.2.11.2.f
            // Check for the wildcard
            if (actionName.toString().equals("*")) {  // requirement: 3.2.11.2.b
                outPairLst.addAll(manager.listAllDefinitions()); // ... add all in a row; requirement: 3.2.11.2.e
                wildcardFound = true;
                break;
            }
        }

        if (!wildcardFound) {
            UIntegerList unkIndexList = new UIntegerList();
            for (int i = 0; i < actionNames.size(); i++) { //requirement: 3.2.11.2.f foreach-cycle steps through list in order
                Identifier actionName = actionNames.get(i);

                final Long idPair = manager.getId(actionName);
                if (idPair == null) {  //requirement: 3.2.11.2.d
                    unkIndexList.add(new UInteger(i));
                } else {
                    outPairLst.add(idPair);  // requirement: 3.2.11.2.a, 3.2.11.2.e
                }
            }

            // Errors
            if (!unkIndexList.isEmpty()) { // requirement: 3.2.11.3.1 (error: a and b)
                throw new MALInteractionException(new UnknownException(unkIndexList));
            }
        }

        return outPairLst;  // requirement: 3.4.9.2.d
    }

    public LongList addAction(ActionDefinitionList defsList,
            MALInteraction interaction) throws MALInteractionException, MALException {
        LongList newObjInstIds = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();

        if (defsList == null) { // Is the input null?
            throw new IllegalArgumentException("actionDefDetails argument must not be null");
        }

        //do the checks
        for (int index = 0; index < defsList.size(); index++) {
            final Identifier name = defsList.get(index).getName();
            // Check if the name field of the ActionDefinition is invalid.
            if (name == null
                    || name.equals(new Identifier("*"))
                    || name.equals(new Identifier(""))) { // requirement: 3.2.12.2.b
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (manager.getDefinition(name) != null) { // Is the supplied name unique? requirement: 3.2.12.2.c
                dupIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        // returning errors before creating the object -> requirement: 3.2.12.2.d
        if (!invIndexList.isEmpty()) { // requirement: 3.2.12.3.1
            throw new MALInteractionException(new InvalidException(invIndexList));
        }
        if (!dupIndexList.isEmpty()) { // requirement: 3.2.12.3.2
            throw new MALInteractionException(new DuplicateException(dupIndexList));
        }

        //add the definition
        for (int index = 0; index < defsList.size(); index++) { // requirement: 3.2.12.2.f (incremental "for cycle" guarantees that)
            newObjInstIds.add(manager.add(defsList.get(index), null,
                    connection.getPrimaryConnectionDetails().getProviderURI())); //  requirement: 3.2.12.2.e, g
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return newObjInstIds; // requirement: 3.2.12.2.f
    }

    public LongList updateDefinition(LongList ids, ActionDefinitionList actionDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (actionDefDetails == null || ids == null) { // Are the inputs null?
            throw new IllegalArgumentException("actionDefInstIds and actionDefDetails arguments must not be null");
        }

        //do the checks
        for (int index = 0; index < ids.size(); index++) {
            final Long id = ids.get(index);

            if (id == null || id == 0 //requirement: 3.2.13.2.c: id is Null or 0?
                    || ids.size() != actionDefDetails.size()) { //requirement: 3.2.13.2.f
                invIndexList.add(new UInteger(index));
                continue;
            }
            ActionDefinition actionDefinition = manager.getActionDefinition(id);  // requirement: 3.2.13.2.a
            if (actionDefinition == null) { // The id could not be found? // requirement: 3.2.13.2.b
                unkIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        // returning errors before creating the object -> requirement: 3.2.13.2.g
        if (!invIndexList.isEmpty()) { // requirement: 3.2.13.2.1 (error: a)
            throw new MALInteractionException(new InvalidException(invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.2.13.2.2 (error: b)
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }
        LongList newDefIds = new LongList();
        for (int index = 0; index < ids.size(); index++) { // requirement: 3.2.13.2.e, k (incremental "for cycle" guarantees that)
            newDefIds.add(manager.update(ids.get(index),
                    actionDefDetails.get(index), null,
                    connection.getPrimaryConnectionDetails().getProviderURI()));  // Change in the manager; requirement: 3.2.13.2.d, g, h
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return newDefIds;
    }

    public void removeAction(final LongList definitionIds, final MALInteraction interaction)
            throws MALException, MALInteractionException { // requirement: 3.7.12.2.1
        UIntegerList unkIndexList = new UIntegerList();
        Long tempIdentity;
        LongList tempIdentityLst = new LongList();

        if (definitionIds == null) { // Is the input null?
            throw new IllegalArgumentException("actionDefInstIds argument must not be null");
        }

        for (int index = 0; index < definitionIds.size(); index++) {
            tempIdentity = definitionIds.get(index); // requirement: 3.2.14.2.a

            if (tempIdentity == 0) {  // Is it the wildcard '0'? requirement: 3.2.14.2.b
                tempIdentityLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempIdentityLst.addAll(manager.listAllDefinitions()); // ... add all in a row
                unkIndexList.clear();
                break;
            }

            if (!manager.existsDef(tempIdentity)) { // Does it match an existing definition? requirement: 3.2.14.2.c
                unkIndexList.add(new UInteger(index)); // requirement: 3.2.14.2.c
            } else {
                tempIdentityLst.add(tempIdentity);
            }
        }

        // Errors
        // returning errors before removing the object -> requirement: 3.2.14.2.g
        if (!unkIndexList.isEmpty()) { // requirement: 3.2.14.3.1 (error: a, b)
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }

        for (Long tempIdentity2 : tempIdentityLst) {
            manager.deleteDefinitionLocally(tempIdentity2); // COM archive must be left untouched. requirement: 3.2.14.2.e
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
     * @param executionId The id of the execution of an action. This value
     * allows the consumer to know which action generated this report.
     * @throws IOException if the definition has a totalNumberOfProgressStages
     * different from the on supplied
     */
    public void reportExecutionProgress(final boolean success, final UInteger errorNumber,
            final int progressStage, final int totalNumberOfProgressStages,
            final Long executionId) throws IOException {
        // Some validation
        if (progressStage < 1) {
            throw new IOException("The first progress stage must be 1.");
        }

        final ExecutionRequest execReq = manager.getExecutionRequest(executionId);

        if (execReq != null) {
            // Aditional validation can be performed!
            ActionDefinition actionDefinition = manager.getActionDefinition(execReq.getDefinitionId());

            if (actionDefinition == null) {
                throw new IOException("The submitted executionId could not be found.");
            }

            UShort totalSteps = actionDefinition.getProgressStepCount();

            if (totalNumberOfProgressStages != totalSteps.getValue()) {
                throw new IOException("The reported total number of progress stages "
                        + "does not match the number stated in the Action Definition: " + totalNumberOfProgressStages
                        + " vs. " + totalSteps.getValue());
            }

            if (totalSteps.getValue() == 0) {
                throw new IOException(
                        "The Action Definition includes 0 progress step count and so, it cannot be reported on it.");
            }
        }

        Long definitionId = execReq != null ? execReq.getDefinitionId() : null;
        publishExecutionProgress(definitionId, executionId,
                ExecutionStageType.PROGRESS, success, new UShort(progressStage),
                success ? null : "Error code: " + errorNumber);
    }

    /**
     * Publishes an execution progress update via the monitorExecution PUB-SUB
     * operation.
     *
     * @param definitionId The id of the ActionDefinition being executed.
     * @param executionId The id of the execution of an action.
     * @param stageType The lifecycle stage (START, PROGRESS, or END).
     * @param success Whether the execution stage completed successfully.
     * @param step The progress step number, or null for START and END stages.
     * @param comment An optional comment.
     */
    public void publishExecutionProgress(final Long definitionId, final Long executionId,
            final ExecutionStageType stageType,
            final boolean success, final UShort step, final String comment) {
        try {
            synchronized (lock) {
                if (!isRegistered) {
                    publisher.registerWithDefaultKeys(new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            AttributeList keys = new AttributeList();
            keys.add(new Union(definitionId));
            keys.add(new Union(executionId));

            URI source = connection.getConnectionDetails().getProviderURI();
            UpdateHeader updateHeader = new UpdateHeader(new Identifier(source.getValue()),
                    connection.getConnectionDetails().getDomain(), keys.getAsNullableAttributeList());

            publisher.publish(updateHeader, stageType, success, step, comment);
            manager.storeExecutionStatus(executionId, stageType, success, step, comment,
                    connection.getPrimaryConnectionDetails().getProviderURI());
        } catch (IllegalArgumentException | MALInteractionException | MALException ex) {
            Logger.getLogger(ActionProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing of execution progress {0}", ex);
        }
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(ActionProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body,
                final Map qosProperties) throws MALException {
            Logger.getLogger(ActionProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(ActionProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body,
                final Map qosProperties) throws MALException {
            Logger.getLogger(ActionProviderServiceImpl.class.getName()).warning(
                    "PublishInteractionListener::publishRegisterErrorReceived");
        }

    }

    @Override
    public Boolean reloadConfiguration(ConfigurationSet configurationObjectDetails) {
        // Validate the returned configuration...
        if (configurationObjectDetails == null) {
            return false;
        }

        if (configurationObjectDetails.getConfigObjects() == null) {
            return false;
        }

        // Is the size 1?
        if (configurationObjectDetails.getConfigObjects().size() != 1) {
            return false;
        }

        ObjectKeys confSetDefs = configurationObjectDetails.getConfigObjects().get(0);

        // Confirm the objTypes
        if (!confSetDefs.getObjType().equals(ActionServiceInfo.ACTIONDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSetDefs.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if (confSetDefs.getIds().isEmpty()) {
            manager.reconfigureDefinitions(new IdentifierList(), new LongList(),
                    new ActionDefinitionList());  // Reconfigures the Manager

            return true;
        }

        // ok, we're good to go...
        // Load the Definitions from this configuration...
        HeterogeneousList pDefs = (HeterogeneousList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                ActionServiceInfo.ACTIONDEFINITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetDefs.getIds());

        IdentifierList names = new IdentifierList();
        for (Element element : pDefs) {
            names.add(((ActionDefinition) element).getName());
        }
        // Reconfigures the Manager
        manager.reconfigureDefinitions(names, confSetDefs.getIds(), pDefs);

        return true;
    }

    @Override
    public ConfigurationSet getCurrentConfiguration() {
        // Get all the current objIds in the serviceImpl
        // Create a Configuration Object with all the objs of the provider
        ObjectKeysList list = manager.getCurrentConfiguration(
                ActionServiceInfo.ACTIONDEFINITION_OBJECT_TYPE);

        // Needs the Common API here!
        return new ConfigurationSet(list);
    }

    @Override
    public COMService getCOMService() {
        return ActionHelper.ACTION_SERVICE;
    }

}
