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

import esa.mo.com.impl.provider.EventProviderServiceImpl;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.reconfigurable.service.ReconfigurableService;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.DuplicateException;
import org.ccsds.moims.mo.com.InvalidException;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.common.structures.*;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.AlertServiceInfo;
import org.ccsds.moims.mo.mc.alert.provider.AlertInheritanceSkeleton;
import org.ccsds.moims.mo.mc.structures.*;

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
     * Creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param comServices The COM services.
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices) throws MALException {
        long timestamp = System.currentTimeMillis();
        // Shut down old service transport
        if (null != alertServiceProvider) {
            connection.closeAll();
        }

        alertServiceProvider = connection.startService(AlertServiceInfo.ALERT_SERVICE_NAME.toString(),
                AlertHelper.ALERT_SERVICE, false, this);

        running = true;
        manager = new AlertManager(comServices);
        groupService.init(manager.getArchiveService());

        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(AlertProviderServiceImpl.class.getName()).info(
                "Alert service READY! (" + timestamp + " ms)");
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
            Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.WARNING,
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
    public LongList enableGeneration(Boolean isGroupIds, InstanceBooleanPairList enableInstances,
            MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        InstanceBooleanPair enableInstance;

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (isGroupIds == null || enableInstances == null) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        boolean foundWildcard = false;

        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.4.4.h
        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.4.8.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.3.8.2.c
                manager.setGenerationEnabledAll(instance.getValue(), source,
                        connection.getConnectionDetails());
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

                    if (!manager.existsDef(enableInstance.getId())) { // does it exist? 
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
                    GroupDetails group = groupService.retrieveGroupDetailsFromArchive(
                            ConfigurationProviderSingleton.getDomain(), groupId);
                    if (group == null) { //group wasnt found
                        unkIndexList.add(new UInteger(index)); // requirement: 3.4.8.2.g
                    } else { //if group was found, then get the instances of it and its groups
                        ignoreList.remove(groupId);
                        GroupServiceImpl.IdObjectTypeList idObjectTypes = groupService.getGroupObjectIdsFromGroup(
                                groupId, group, ignoreList);
                        ignoreList.add(groupId);

                        // workaround for empty groups of the wrong type
                        if (idObjectTypes.isEmpty() && !group.getObjectType().equals(AlertServiceInfo.ALERTIDENTITY_OBJECT_TYPE)) {
                            invIndexList.add(new UInteger(index));
                        }

                        //checks if the given identityId is found in the internal Alert-list, if not its not a alert and invalid
                        for (GroupServiceImpl.IdObjectType idObjectType : idObjectTypes) {
                            if (idObjectType.getObjectType().equals(AlertServiceInfo.ALERTIDENTITY_OBJECT_TYPE)) {
                                final Long identityId = idObjectType.getId(); // requirement: 3.4.8.2.b
                                //checks if the alertId referenced in the group is known
                                if (!manager.existsDef(identityId)) {// requirement: 3.4.8.2.g
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
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }

        if (!invIndexList.isEmpty()) { // requirement: 3.4.8.3.2
            throw new MALInteractionException(new InvalidException(invIndexList));
        }

        LongList output = new LongList();

        // requirement: 3.4.8.2.i (This part of the code is only reached if no error was raised)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            // requirement: 3.4.8.e and 3.4.8.f and 3.4.8.j
            Long out = manager.setGenerationEnabled(objIdToBeEnabled.get(index),
                    valueToBeEnabled.get(index), source, connection.getConnectionDetails());
            output.add(out);
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return output;
    }

    @Override
    public LongList listDefinition(IdentifierList alertNames, MALInteraction interaction)
            throws MALInteractionException, MALException {
        LongList outPairLst = new LongList();

        if (alertNames == null) { // Is the input null?
            throw new IllegalArgumentException("alertNames argument must not be null");
        }

        boolean wildcardFound = false;
        for (Identifier alertName : alertNames) {  // requirement: 3.4.9.2.f
            // Check for the wildcard
            if (alertName.toString().equals("*")) {  // requirement: 3.4.9.2.b
                outPairLst.addAll(manager.listAllDefinitions()); // ... add all in a row; requirement: 3.4.9.2.e
                wildcardFound = true;
                break;
            }
        }

        if (!wildcardFound) {
            UIntegerList unkIndexList = new UIntegerList();
            for (int i = 0; i < alertNames.size(); i++) { //requirement: 3.4.9.2.f foreach-cycle steps through list in order
                Identifier alertName = alertNames.get(i);

                final Long idPair = manager.getId(alertName);
                if (idPair == null) {  //requirement: 3.4.9.2.d
                    unkIndexList.add(new UInteger(i));
                } else {
                    outPairLst.add(idPair);  // requirement: 3.4.9.2.a, 3.4.9.2.e
                }
            }

            // Errors
            if (!unkIndexList.isEmpty()) { // requirement: 3.4.9.3.1 (error: a and b)
                throw new MALInteractionException(new UnknownException(unkIndexList));
            }
        }

        return outPairLst;  // requirement: 3.4.9.2.d
    }

    public LongList addAlert(AlertDefinitionList alertDefs,
            MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();

        if (alertDefs == null) { // Is the input null?
            throw new IllegalArgumentException("alertDefs argument must not be null");
        }

        for (int index = 0; index < alertDefs.size(); index++) {
            //requirement: 3.4.10.2.a
            AlertDefinition def = alertDefs.get(index);
            Identifier alertName = def.getName();

            // Check if the name field of the AlertDefinition is invalid.
            if (alertName.equals(new Identifier("*"))
                    || alertName.equals(new Identifier(""))) { // requirement: 3.4.10.2.b
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (manager.getDefinition(alertName) != null) { // Is the supplied name already given? requirement: 3.4.10.2.c
                dupIndexList.add(new UInteger(index));
                continue;
            }
        }
        // Errors
        //requirement: 3.4.10.2.d -> returning errors before adding definitions assures that
        if (!invIndexList.isEmpty()) { // requirement: 3.4.10.3.2
            throw new MALInteractionException(new InvalidException(invIndexList));
        }
        if (!dupIndexList.isEmpty()) { // requirement: 3.4.10.3.1
            throw new MALInteractionException(new DuplicateException(dupIndexList));
        }

        LongList outPairLst = new LongList();
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.4.4.g,h
        //requirement: 3.4.10.2.h -> cycling with for loop through  the requests assures that
        for (AlertDefinition alertDef : alertDefs) {
            //requirement: 3.4.10.2.a
            outPairLst.add(manager.add(alertDef, source,
                    connection.getConnectionDetails())); //  requirement: 3.4.10.2.f
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return outPairLst; // requirement: 3.4.10.2.g
    }

    @Override
    public LongList updateDefinition(LongList alertObjInstIds, AlertDefinitionList newAlertDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (alertObjInstIds == null || newAlertDefDetails == null) { // Are the inputs null?
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
            if (!manager.existsDef(identityId)) {
                unkIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        //requirement: 3.4.11.2.g -> returning errors before adding definitions assures that
        if (!invIndexList.isEmpty()) { // requirement: 3.4.11.3.1
            throw new MALInteractionException(new InvalidException(invIndexList));
        }

        if (!unkIndexList.isEmpty()) { // requirement: 3.4.11.3.2
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }

        LongList outLst = new LongList();
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.3.4.g, h
        // requirement: 3.4.11.2.e, 3.4.11.2.j
        for (int index = 0; index < alertObjInstIds.size(); index++) {
            //requirement: 3.4.11.2.a, 3.4.11.2.d
            outLst.add(manager.update(alertObjInstIds.get(index), newAlertDefDetails.get(index),
                    source, connection.getConnectionDetails())); //requirement: 3.4.11.2.h Change in the manager/archive
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return outLst; //requirement: 3.4.11.2.i
    }

    public void removeAlert(LongList alertIdentityIds,
            MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        LongList removalLst = new LongList();

        if (alertIdentityIds == null) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < alertIdentityIds.size(); index++) {
            //requirement: 3.4.12.2.a
            Long identityId = alertIdentityIds.get(index);
            if (identityId == 0) {  // Is it the wildcard '0'? requirement: 3.4.12.2.b
                removalLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                removalLst.addAll(manager.listAllDefinitions()); // ... add all in a row
                unkIndexList.clear();
                break;
            }

            if (!manager.existsDef(identityId)) { // Does it match an existing definition? requirement: 3.4.12.2.c
                unkIndexList.add(new UInteger(index)); // requirement: 3.4.12.2.c
            } else {
                removalLst.add(identityId);
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.4.12.3.1
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }

        // requirement: 3.4.12.2.e (Inserting the errors before this line guarantees that the requirement is met)
        for (Long removalId : removalLst) {
            //requirement: 3.4.12.2.a
            manager.deleteDefinitionLocally(removalId);  // COM archive is left untouched. requirement: 3.4.12.2.d
        }

        if (configurationAdapter != null) {
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

        Long id = manager.getId(alertDefinitionName);

        if (id == null) {
            // It doesn't... let's automatically generate the Alert Definition
            generateAlertDefinition(argumentValues, alertDefinitionName, interaction, null);
            id = manager.getId(alertDefinitionName);
        }

        // Also, check if the Alert is enabled or not!
        AlertDefinition alertDef = manager.getAlertDefinitionFromDefId(id);

        if (alertDef == null) { // requirement: 3.4.12.2.g
            return null;
        }

        if (!alertDef.getGenerationEnabled()) {  // requirement: 3.4.3.a, 3.4.3.b
            return null;
        }

        // Check if the argumentIds match
        if (alertDef.getArguments() != null) {
            if (argumentIds != null) {
                for (int index = 0; index < alertDef.getArguments().size(); index++) {
                    if (!alertDef.getArguments().get(index).getArgId().getValue().equals(argumentIds.get(index).getValue())) {  // If it doesn't match?
                        return null;
                    }
                }
            }
        }

        AlertEventDetails alertEvent = new AlertEventDetails(argumentValues, argumentIds);

        // COM and Event usage 
        // requirement: 3.4.3.d
        // requirement: 3.4.7.b, 3.4.4.d, 3.4.4.f
        Long alertEventObjId = manager.getEventService().generateAndStoreEvent(
                AlertServiceInfo.ALERTEVENT_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                alertEvent, id, source, interaction);

        // requirement: 3.4.5.a and 3.4.5.b and 3.4.5.c
        AlertEventDetailsList alertEvents = new AlertEventDetailsList();
        alertEvents.add(alertEvent);
        final URI uri = EventProviderServiceImpl.convertMALInteractionToURI(interaction);

        try {
            manager.getEventService().publishEvent(uri, alertEventObjId,
                    AlertServiceInfo.ALERTEVENT_OBJECT_TYPE, id, source, alertEvents);
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
        ArgumentDefinitionList args = new ArgumentDefinitionList();

        if (argumentValues != null) {
            for (int i = 0; i < argumentValues.size(); i++) {  // Go one by one and figure it out what they are
                AttributeValue argumentValue = argumentValues.get(i);
                int rawType;

                if (argumentValue.getValue() == null) {  // Well, let's then consider it is a Double
                    rawType = AttributeType.DOUBLE_VALUE;
                } else {
                    // Check what is the type and stamp it
                    rawType = argumentValue.getValue().getTypeId().getSFP();
                }

                // Generate the Argument Definition
                ArgumentDefinition arg = new ArgumentDefinition(
                        new Identifier(String.valueOf(i)),
                        "",
                        new AttributeType(rawType),
                        null
                );

                args.add(arg);
            }
        }
        AlertDefinition alertDef = new AlertDefinition(
                alertDefinitionName,
                "This def. was auto-generated by the Alert service",
                Severity.INFORMATIONAL,
                true,
                args);

        //fill creation object and... add it to the provider
        AlertDefinitionList defs = new AlertDefinitionList(1);
        defs.add(alertDef);

        try {
            LongList returnedObjIds = this.addAlert(defs, interaction);
            identityId = returnedObjIds.get(0);

            // Enable the Alert reporting!
            //            InstanceBooleanPairList ids = new InstanceBooleanPairList();
            //            ids.add(new InstanceBooleanPair(identityId, true));
            //            this.enableGeneration(true, ids, interaction); // Enable the reporting for this Alert Definition
        } catch (MALInteractionException | MALException ex) {
            Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return identityId;
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

        // Is the size 1?
        if (configurationObjectDetails.getConfigObjects().size() != 1) {
            return false;
        }

        ConfigurationObjectSet confSetDefs = configurationObjectDetails.getConfigObjects().get(0);

        // Confirm the objTypes
        if (!confSetDefs.getObjType().equals(AlertServiceInfo.ALERTDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSetDefs.getDomain().equals(ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if (confSetDefs.getObjInstIds().isEmpty()) {
            // Reconfigures the Manager
            manager.reconfigureDefinitions(new IdentifierList(), new LongList(), new AlertDefinitionList());
            return true;
        }

        // ok, we're good to go...
        // Load the Definitions from this configuration...
        HeterogeneousList pDefs = (HeterogeneousList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                AlertServiceInfo.ALERTDEFINITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetDefs.getObjInstIds());

        IdentifierList names = new IdentifierList();
        for (Element element : pDefs) {
            names.add(((AlertDefinition) element).getName());
        }
        manager.reconfigureDefinitions(names, confSetDefs.getObjInstIds(), pDefs);   // Reconfigures the Manager

        return true;
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        // Create a Configuration Object with all the objs of the provider
        ConfigurationObjectSetList list = manager.getCurrentConfiguration(
                AlertServiceInfo.ALERTDEFINITION_OBJECT_TYPE
        );

        // Needs the Common API here!
        return new ConfigurationObjectDetails(list);
    }

    @Override
    public COMService getCOMService() {
        return AlertHelper.ALERT_SERVICE;
    }

}
