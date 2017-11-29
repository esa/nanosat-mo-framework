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

import esa.mo.com.impl.provider.EventProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
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
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.provider.AlertInheritanceSkeleton;
import org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequestList;
import org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetailsList;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetailsList;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mc.structures.Severity;
import esa.mo.reconfigurable.service.ReconfigurableService;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;

/**
 * Alert service Provider.
 */
public class AlertProviderServiceImpl extends AlertInheritanceSkeleton implements ReconfigurableService {

    private MALProvider alertServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    protected AlertManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private ConfigurationChangeListener configurationAdapter;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices) throws MALException {
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
                AlertHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {// nothing to be done..
            }
        }

        // Shut down old service transport
        if (null != alertServiceProvider) {
            connection.closeAll();
        }

        alertServiceProvider = connection.startService(AlertHelper.ALERT_SERVICE_NAME.toString(), AlertHelper.ALERT_SERVICE, false, this);

        running = true;
        manager = new AlertManager(comServices);
        groupService.init(manager.getArchiveService());

        initialiased = true;
        Logger.getLogger(AlertProviderServiceImpl.class.getName()).info("Alert service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != alertServiceProvider) {
                alertServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public void setOnConfigurationChangeListener(ConfigurationChangeListener configurationAdapter){
        this.configurationAdapter = configurationAdapter;
    }
        
    @Override
    public LongList enableGeneration(Boolean isGroupIds, InstanceBooleanPairList enableInstances,
            MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        InstanceBooleanPair enableInstance;

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (null == isGroupIds || null == enableInstances) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        boolean foundWildcard = false;

        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.4.4.h
        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.4.8.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.3.8.2.c
                manager.setGenerationEnabledAll(instance.getValue(), source, connection.getConnectionDetails());
                foundWildcard = true;
                break;
            }
        }

        if (!foundWildcard) { // requirement: 3.4.8.2.d

            //the Ids are alert-identity-ids 3.4.8.2.a
            if (!isGroupIds) {
                for (int index = 0; index < enableInstances.size(); index++) {
                    enableInstance = enableInstances.get(index);
                    objIdToBeEnabled.add(enableInstance.getId()); // requirement: 3.4.8.2.b
                    valueToBeEnabled.add(enableInstance.getValue());

                    if (!manager.existsIdentity(enableInstance.getId())) { // does it exist? 
                        unkIndexList.add(new UInteger(index)); // requirement: 3.4.8.2.g
                    }
                }
            } else {//the ids are group-definition-ids, req: 3.4.8.2.a, 3.9.4.g,h
                //TODO: sure? groupddefintion or identity-ids? -> issue #135, #179
                //in the next for loop, ignore the other group definitions, they will be checked in other iterations.
                LongList ignoreList = new LongList();
                for (InstanceBooleanPair instance : enableInstances) {
                    ignoreList.add(instance.getId());
                }
                for (int index = 0; index < enableInstances.size(); index++) {
                    //these are Group-Definition-ids req: 3.9.4.g,h
                    enableInstance = enableInstances.get(index);
                    final Long groupId = enableInstance.getId();
                    GroupDetails group = groupService.retrieveGroupDetailsFromArchive(ConfigurationProviderSingleton.getDomain(), groupId);
                    if (group == null) { //group wasnt found
                        unkIndexList.add(new UInteger(index)); // requirement: 3.4.8.2.g
                    } else { //if group was found, then get the instances of it and its groups
                        ignoreList.remove(groupId);
                        GroupServiceImpl.IdObjectTypeList idObjectTypes = groupService.getGroupObjectIdsFromGroup(groupId, group, ignoreList);
                        ignoreList.add(groupId);

                        // workaround for empty groups of the wrong type
                        if (idObjectTypes.isEmpty() && !group.getObjectType().equals(AlertHelper.ALERTIDENTITY_OBJECT_TYPE)) {
                            invIndexList.add(new UInteger(index));
                        }

                        //checks if the given identityId is found in the internal Alert-list, if not its not a alert and invalid
                        for (GroupServiceImpl.IdObjectType idObjectType : idObjectTypes) {
                            if (idObjectType.getObjectType().equals(AlertHelper.ALERTIDENTITY_OBJECT_TYPE)) {
                                final Long identityId = idObjectType.getId(); // requirement: 3.4.8.2.b
                                //checks if the alertId referenced in the group is known
                                if (!manager.existsIdentity(identityId)) {// requirement: 3.4.8.2.g
                                    unkIndexList.add(new UInteger(index));
                                }
                                if (!objIdToBeEnabled.contains(identityId)) {
                                    objIdToBeEnabled.add(identityId);
                                    valueToBeEnabled.add(enableInstance.getValue());
                                }
                            } else { // no/not only AlertIdentity-entries
                                invIndexList.add(new UInteger(index)); // requirement: 3.4.8.2.h
                                break;
                            }
                        }
                    }
                }
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.4.8.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        
        if (!invIndexList.isEmpty()) { // requirement: 3.4.8.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        LongList output = new LongList();

        // requirement: 3.4.8.2.i (This part of the code is only reached if no error was raised)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            // requirement: 3.4.8.e and 3.4.8.f and 3.4.8.j
            Long out = manager.setGenerationEnabled(objIdToBeEnabled.get(index), 
                    valueToBeEnabled.get(index), source, connection.getConnectionDetails());
            output.add(out);
        }

        if (configurationAdapter != null){
            configurationAdapter.onConfigurationChanged(this);
        }
        
        return output;
    }

    @Override
    public ObjectInstancePairList listDefinition(IdentifierList alertNames, MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectInstancePairList outPairLst = new ObjectInstancePairList();

        if (null == alertNames) { // Is the input null?
            throw new IllegalArgumentException("alertNames argument must not be null");
        }

        boolean wildcardFound = false;
        for (Identifier alertName : alertNames) {  // requirement: 3.4.9.2.f
            // Check for the wildcard
            if (alertName.toString().equals("*")) {  // requirement: 3.4.9.2.b
                outPairLst.addAll(manager.listAllIdentityDefinitions()); // ... add all in a row; requirement: 3.4.9.2.e
                wildcardFound = true;
                break;
            }
        }

        if (!wildcardFound) {
            UIntegerList unkIndexList = new UIntegerList();
            for (int i = 0; i < alertNames.size(); i++) { //requirement: 3.4.9.2.f foreach-cycle steps through list in order
                Identifier alertName = alertNames.get(i);

                final ObjectInstancePair idPair = manager.getIdentityDefinition(alertName);
                if (idPair == null) {  //requirement: 3.4.9.2.d
                    unkIndexList.add(new UInteger(i));
                } else {
                    outPairLst.add(idPair);  // requirement: 3.4.9.2.a, 3.4.9.2.e
                }
            }

            // Errors
            if (!unkIndexList.isEmpty()) // requirement: 3.4.9.3.1 (error: a and b)
            {
                throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            }
        }

        return outPairLst;  // requirement: 3.4.9.2.d
    }

    @Override
    public ObjectInstancePairList addAlert(AlertCreationRequestList alertCreationRequests, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();

        if (null == alertCreationRequests) // Is the input null?
        {
            throw new IllegalArgumentException("AlertCreationRequestList argument must not be null");
        }

        for (int index = 0; index < alertCreationRequests.size(); index++) {
            //requirement: 3.4.10.2.a
            AlertCreationRequest alertCreationReq = alertCreationRequests.get(index);
            Identifier alertName = alertCreationReq.getName();

            // Check if the name field of the AlertDefinition is invalid.
            if (alertName.equals(new Identifier("*"))
                    || alertName.equals(new Identifier(""))) { // requirement: 3.4.10.2.b
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (manager.getIdentity(alertName) != null) { // Is the supplied name already given? requirement: 3.4.10.2.c
                dupIndexList.add(new UInteger(index));
                continue;
            }
        }
        // Errors
        //requirement: 3.4.10.2.d -> returning errors before adding definitions assures that
        if (!invIndexList.isEmpty()) // requirement: 3.4.10.3.2
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }
        if (!dupIndexList.isEmpty()) // requirement: 3.4.10.3.1
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        ObjectInstancePairList outPairLst = new ObjectInstancePairList();
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.4.4.g,h
        //requirement: 3.4.10.2.h -> cycling with for loop through  the requests assures that
        for (AlertCreationRequest alertCreationRequest : alertCreationRequests) {
            //requirement: 3.4.10.2.a
            outPairLst.add(manager.add(alertCreationRequest.getName(), alertCreationRequest.getAlertDefDetails(), 
                    source, connection.getConnectionDetails())); //  requirement: 3.4.10.2.f
        }

        if (configurationAdapter != null){
            configurationAdapter.onConfigurationChanged(this);
        }

        return outPairLst; // requirement: 3.4.10.2.g
    }

    @Override
    public LongList updateDefinition(LongList alertObjInstIds, AlertDefinitionDetailsList newAlertDefDetails, 
            MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == alertObjInstIds || null == newAlertDefDetails) { // Are the inputs null?
            throw new IllegalArgumentException("alertObjInstIds and alertDefDetails arguments must not be null");
        }
        for (int index = 0; index < alertObjInstIds.size(); index++) {
            //requirement: 3.4.11.2.a
            final Long identityId = alertObjInstIds.get(index);
            if (identityId == null || identityId == 0 //requirement: 3.4.11.2.c: id is Null or 0?
                    || alertObjInstIds.size() != newAlertDefDetails.size()) { //requirement: 3.4.11.2.f
                invIndexList.add(new UInteger(index));
                continue;
            }
            //requirement: 3.4.11.2.b: The object instance identifier could not be found?
            if (!manager.existsIdentity(identityId)) {
                unkIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        //requirement: 3.4.11.2.g -> returning errors before adding definitions assures that
        if (!invIndexList.isEmpty()) { // requirement: 3.4.11.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.4.11.3.2
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        LongList outLst = new LongList();
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.3.4.g, h
        // requirement: 3.4.11.2.e, 3.4.11.2.j
        for (int index = 0; index < alertObjInstIds.size(); index++) {
            //requirement: 3.4.11.2.a, 3.4.11.2.d
            outLst.add(manager.update(alertObjInstIds.get(index), newAlertDefDetails.get(index), 
                    source, connection.getConnectionDetails())); //requirement: 3.4.11.2.h Change in the manager/archive
        }

        if (configurationAdapter != null){
            configurationAdapter.onConfigurationChanged(this);
        }

        return outLst; //requirement: 3.4.11.2.i
    }

    @Override
    public void removeAlert(LongList alertIdentityIds, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        LongList removalLst = new LongList();

        if (null == alertIdentityIds) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < alertIdentityIds.size(); index++) {
            //requirement: 3.4.12.2.a
            Long identityId = alertIdentityIds.get(index);
            if (identityId == 0) {  // Is it the wildcard '0'? requirement: 3.4.12.2.b
                removalLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                removalLst.addAll(manager.listAllIdentities()); // ... add all in a row
                unkIndexList.clear();
                break;
            }

            if (!manager.existsIdentity(identityId)) { // Does it match an existing definition? requirement: 3.4.12.2.c
                unkIndexList.add(new UInteger(index)); // requirement: 3.4.12.2.c
            } else {
                removalLst.add(identityId);
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.4.12.3.1
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.4.12.2.e (Inserting the errors before this line guarantees that the requirement is met)
        for (Long removalId : removalLst) {
            //requirement: 3.4.12.2.a
            manager.delete(removalId);  // COM archive is left untouched. requirement: 3.4.12.2.d
        }

        if (configurationAdapter != null){
            configurationAdapter.onConfigurationChanged(this);
        }
    }

    /**
     * The publishAlertEvent operation allows an external software entity to
     * publish Alert events through the Alert service. The method will return
     * null if something goes wrong or if the generation of that event is
     * disabled
     *
     * @param interaction The interaction object of the operation, can be null
     * @param alertDefinitionName The name of the Alert Definition
     * @param argumentValues The argument values to be published
     * @param argumentIds If null, no verification will take place
     * @param source The source of the alert
     * @return Returns the object instance identifier of the published event.
     * Null if the event was not publish because of some error, or the
     * generation of events is disabled
     */
    public Long publishAlertEvent(final MALInteraction interaction, final Identifier alertDefinitionName,
            final AttributeValueList argumentValues, final IdentifierList argumentIds, final ObjectId source) {

        // Add code to publish an Alert following the requirements and using the  Event service
        if (manager.getEventService() == null) {
            return null;
        }

        ObjectInstancePair pair  = manager.getIdentityDefinition(alertDefinitionName);

        if  (pair == null) {
            // It doesn't... let's automatically generate the Alert Definition
            generateAlertDefinition(argumentValues, alertDefinitionName, interaction, null);
            pair  = manager.getIdentityDefinition(alertDefinitionName);
        }

        final Long defId = pair.getObjDefInstanceId();
        
        // Also, check if the Alert is enabled or not!
        AlertDefinitionDetails alertDef = manager.getAlertDefinitionFromDefId(defId);

        if (alertDef == null) { // requirement: 3.4.12.2.g
            return null;
        }

        if (!alertDef.getGenerationEnabled()) {  // requirement: 3.4.3.a, 3.4.3.b
            return null;
        }

        // Check if the argumentIds match
        if (alertDef.getArguments() != null) {
            if(argumentIds != null){
                for (int index = 0; index < alertDef.getArguments().size(); index++) {
                    if (!alertDef.getArguments().get(index).getArgId().getValue().equals(argumentIds.get(index).getValue())) {  // If it doesn't match?
                        return null;
                    }
                }
            }
        }

        AlertEventDetails alertEvent = new AlertEventDetails();
        alertEvent.setArgumentValues(argumentValues);
        alertEvent.setArgumentIds(argumentIds);

        // COM and Event usage 
        // requirement: 3.4.3.d
        // requirement: 3.4.7.b, 3.4.4.d, 3.4.4.f
        Long alertEventObjId = manager.getEventService().generateAndStoreEvent(AlertHelper.ALERTEVENT_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(), alertEvent, defId, source, interaction);

        // requirement: 3.4.5.a and 3.4.5.b and 3.4.5.c
        AlertEventDetailsList alertEvents = new AlertEventDetailsList();
        alertEvents.add(alertEvent);
        final URI uri = EventProviderServiceImpl.convertMALInteractionToURI(interaction);

        try {
            manager.getEventService().publishEvent(uri, alertEventObjId, AlertHelper.ALERTEVENT_OBJECT_TYPE, defId, source, alertEvents);
        } catch (IOException ex) {
            Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return alertEventObjId;
    }

    /**
     * adds an alertDefinition witht the given values
     *
     * @param argumentValues
     * @param alertDefinitionName
     * @param interaction
     * @param identityId
     * @return
     */
    private Long generateAlertDefinition(final AttributeValueList argumentValues, 
            final Identifier alertDefinitionName, final MALInteraction interaction, Long identityId) {
        AlertCreationRequest alertCreationDef = new AlertCreationRequest();
        AlertDefinitionDetails alertDef = new AlertDefinitionDetails();
        alertDef.setDescription("This def. was auto-generated by the Alert service");
        alertDef.setSeverity(Severity.INFORMATIONAL);
        alertDef.setGenerationEnabled(true);
        
        ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();
        ArgumentDefinitionDetails arg = new ArgumentDefinitionDetails(); // Generate the Argument Definition
        
        if (argumentValues == null) {
            arg = null;
        } else {
            for (int i = 0; i < argumentValues.size(); i++) {  // Go one by one and figure it out what they are
                AttributeValue argumentValue = argumentValues.get(i);

                if (argumentValue.getValue() == null) {  // Well, let's then consider it is a Double
                    arg.setRawType(Union.DOUBLE_TYPE_SHORT_FORM.byteValue());
                } else {
                    arg.setRawType(argumentValue.getValue().getTypeShortForm().byteValue()); // Check what is the type and stamp it
                }
                arg.setRawUnit(null);
                arg.setConditionalConversions(null);
                arg.setConvertedType(null);
                arg.setConvertedUnit(null);
                arg.setArgId(new Identifier(String.valueOf(i)));

                args.add(arg);
            }
        }
        alertDef.setArguments(args);
        //fill creation object
        alertCreationDef.setName(alertDefinitionName);
        alertCreationDef.setAlertDefDetails(alertDef);
        // And... add it to the provider
        AlertCreationRequestList alertCreationDefs = new AlertCreationRequestList(1);
        alertCreationDefs.add(alertCreationDef);
        
        try {
            ObjectInstancePairList returnedObjIds = this.addAlert(alertCreationDefs, interaction);
            identityId = returnedObjIds.get(0).getObjIdentityInstanceId();

            // Enable the Alert reporting!
//            InstanceBooleanPairList ids = new InstanceBooleanPairList();
//            ids.add(new InstanceBooleanPair(identityId, true));
//            this.enableGeneration(true, ids, interaction); // Enable the reporting for this Alert Definition
        } catch (MALInteractionException ex) {
            Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return identityId;
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

        // Is the size 2?
        if (configurationObjectDetails.getConfigObjects().size() != 2) {
            return false;
        }

        ConfigurationObjectSet confSet0 = configurationObjectDetails.getConfigObjects().get(0);
        ConfigurationObjectSet confSet1 = configurationObjectDetails.getConfigObjects().get(1);

        // Confirm the objTypes
        if (!confSet0.getObjType().equals(AlertHelper.ALERTDEFINITION_OBJECT_TYPE) &&
                !confSet1.getObjType().equals(AlertHelper.ALERTDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        if (!confSet0.getObjType().equals(AlertHelper.ALERTIDENTITY_OBJECT_TYPE) &&
                !confSet1.getObjType().equals(AlertHelper.ALERTIDENTITY_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet0.getDomain().equals(ConfigurationProviderSingleton.getDomain()) ||
                !confSet1.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if(confSet0.getObjInstIds().isEmpty() && confSet1.getObjInstIds().isEmpty()){
            manager.reconfigureDefinitions(new LongList(), new IdentifierList(), 
                    new LongList(), new AlertDefinitionDetailsList());   // Reconfigures the Manager

            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        ConfigurationObjectSet confSetDefs = (confSet0.getObjType().equals(AlertHelper.ALERTDEFINITION_OBJECT_TYPE)) ? confSet0 : confSet1;
        
        AlertDefinitionDetailsList pDefs = (AlertDefinitionDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                AlertHelper.ALERTDEFINITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetDefs.getObjInstIds());

        ConfigurationObjectSet confSetIdents = (confSet0.getObjType().equals(AlertHelper.ALERTIDENTITY_OBJECT_TYPE)) ? confSet0 : confSet1;
        
        IdentifierList idents = (IdentifierList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                AlertHelper.ALERTIDENTITY_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetIdents.getObjInstIds());
        
            manager.reconfigureDefinitions(confSetIdents.getObjInstIds(), idents, 
                    confSetDefs.getObjInstIds(), pDefs);   // Reconfigures the Manager

        return true;
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Create a Configuration Object with all the objs of the provider
        ConfigurationObjectSetList list = manager.getCurrentConfiguration();
        list.get(0).setObjType(AlertHelper.ALERTIDENTITY_OBJECT_TYPE);
        list.get(1).setObjType(AlertHelper.ALERTDEFINITION_OBJECT_TYPE);
        
        // Needs the Common API here!
        ConfigurationObjectDetails set = new ConfigurationObjectDetails();
        set.setConfigObjects(list);

        return set;
    }
    
    @Override
    public COMService getCOMService() {
        return AlertHelper.ALERT_SERVICE;
    }        
    
}
