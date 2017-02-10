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
import esa.mo.reconfigurable.service.ConfigurationNotificationInterface;
import esa.mo.reconfigurable.service.ReconfigurableServiceImplInterface;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
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
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.alert.AlertHelper;
import org.ccsds.moims.mo.mc.alert.provider.AlertInheritanceSkeleton;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetailsList;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetails;
import org.ccsds.moims.mo.mc.alert.structures.AlertEventDetailsList;
import org.ccsds.moims.mo.mc.group.structures.GroupDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ArgumentValue;
import org.ccsds.moims.mo.mc.structures.ArgumentValueList;
import org.ccsds.moims.mo.mc.structures.Severity;

/**
 *
 */
public class AlertProviderServiceImpl extends AlertInheritanceSkeleton implements ReconfigurableServiceImplInterface {

    private MALProvider alertServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private AlertManager manager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private ConfigurationNotificationInterface configurationAdapter;

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
                ConfigurationHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) { // nothing to be done..
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

        manager = new AlertManager(comServices);

        alertServiceProvider = connection.startService(AlertHelper.ALERT_SERVICE_NAME.toString(), AlertHelper.ALERT_SERVICE, false, this);

        running = true;
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

    public ConnectionProvider getConnectionProvider(){
        return this.connection;
    }

    @Override
    public void setConfigurationAdapter(ConfigurationNotificationInterface configurationAdapter){
        this.configurationAdapter = configurationAdapter;
    }
    
    @Override
    public void enableGeneration(Boolean isGroupIds, InstanceBooleanPairList enableInstances,
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

        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.4.8.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.4.8.2.c
                manager.setGenerationEnabledAll(instance.getValue(), connection.getConnectionDetails());
                foundWildcard = true;
                
                if (configurationAdapter != null){
                    configurationAdapter.configurationChanged(this);
                }
                
                break;
            }
        }

        if (!foundWildcard) { // requirement: 3.4.8.2.d
            for (int index = 0; index < enableInstances.size(); index++) {
                enableInstance = enableInstances.get(index);

                if (isGroupIds) {
                    GroupDetails group = groupService.retrieveGroupDetailsFromArchive(
                            ConfigurationProviderSingleton.getDomain(), enableInstances.get(index).getId());

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

        if (!foundWildcard) { // requirement: 3.4.8.2.d
            // requirement: 3.4.8.i (This part of the code is not reached if an error is thrown)
            for (int index = 0; index < objIdToBeEnabled.size(); index++) {
                // requirement: 3.4.8.e and 3.4.8.f and 3.4.8.j
                manager.setGenerationEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), connection.getConnectionDetails());
            }
        }

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public LongList listDefinition(IdentifierList alertNames, MALInteraction interaction) throws MALInteractionException, MALException {
        LongList outLongLst = new LongList();

        if (null == alertNames) { // Is the input null?
            throw new IllegalArgumentException("alertNames argument must not be null");
        }

        for (Identifier alertName : alertNames) {  // requirement: 3.4.9.2.d
            // Check for the wildcard
            if (alertName.toString().equals("*")) {  // requirement: 3.4.9.2.b
                outLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                outLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            outLongLst.add(manager.list(alertName));  // requirement: 3.4.9.2.c
        }

        // Errors
        // The operation does not return any errors.
        return outLongLst;  // requirement: 3.4.9.2.d
    }

    @Override
    public LongList addDefinition(AlertDefinitionDetailsList alertDefDetails, MALInteraction interaction) throws MALInteractionException, MALException {
        LongList outLongLst = new LongList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();
        AlertDefinitionDetails alertDefDetail;

        if (null == alertDefDetails) { // Is the input null?
            throw new IllegalArgumentException("alertDefDetails argument must not be null");
        }

        for (int index = 0; index < alertDefDetails.size(); index++) { // requirement: 3.4.10.2.f (incremental "for cycle" guarantees that)
            alertDefDetail = alertDefDetails.get(index);

            // Check if the name field of the AggregationDefinition is invalid.
            if (alertDefDetail.getName() == null
                    || alertDefDetail.getName().equals(new Identifier("*"))
                    || alertDefDetail.getName().equals(new Identifier(""))) {
                invIndexList.add(new UInteger(index)); // requirement: 3.4.10.2.b
            }

            if (manager.list(alertDefDetail.getName()) == null) { // Is the supplied name unique? requirement: 3.4.10.2.c
                ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.4.4.b
                outLongLst.add(manager.add(alertDefDetail, source, connection.getConnectionDetails())); //  requirement: 3.4.10.2.e
            } else {
                dupIndexList.add(new UInteger(index)); //  requirement: 3.4.10.2.c
            }
        }

        // Errors
        if (!dupIndexList.isEmpty()) { // requirement: 3.4.10.3.1
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        if (!invIndexList.isEmpty()) { // requirement: 3.4.10.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }

        return outLongLst; // requirement: 3.7.10.2.d
    }

    @Override
    public void updateDefinition(LongList alertObjInstIds, AlertDefinitionDetailsList alertDefDetails, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        AlertDefinitionDetails oldAlertDefDetails;
        LongList newAlertDefInstIds = new LongList();
        AlertDefinitionDetailsList newAlertDefDetails = new AlertDefinitionDetailsList();

        if (null == alertObjInstIds || null == alertDefDetails) { // Are the inputs null?
            throw new IllegalArgumentException("alertObjInstIds and alertDefDetails arguments must not be null");
        }

        for (int index = 0; index < alertObjInstIds.size(); index++) { // requirement: 3.4.11.2.c
            oldAlertDefDetails = manager.get(alertObjInstIds.get(index));

            if (oldAlertDefDetails == null) { // The object instance identifier could not be found?
                unkIndexList.add(new UInteger(index)); // requirement: 3.4.11.2.d
                continue;
            }

            if (alertDefDetails.get(index).getName().equals(oldAlertDefDetails.getName())) { // Are the names equal?
                newAlertDefInstIds.add(alertObjInstIds.get(index));
                newAlertDefDetails.add(alertDefDetails.get(index));
            } else {
                invIndexList.add(new UInteger(index)); // requirement: 3.4.11.3.1.a
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
        for (int index = 0; index < newAlertDefInstIds.size(); index++) {
            manager.update(newAlertDefInstIds.get(index), newAlertDefDetails.get(index), connection.getConnectionDetails());  // Change in the manager
        }

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }

    }

    @Override
    public void removeDefinition(LongList alertDefInstIds, MALInteraction interaction) throws MALInteractionException, MALException {
        UIntegerList unkIndexList = new UIntegerList();
        Long tempLong;
        LongList tempLongLst = new LongList();

        if (null == alertDefInstIds) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < alertDefInstIds.size(); index++) {
            tempLong = alertDefInstIds.get(index);

            if (tempLong == 0) {  // Is it the wildcard '0'? requirement: 3.4.12.2.b
                tempLongLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                tempLongLst.addAll(manager.listAll()); // ... add all in a row
                break;
            }

            if (manager.exists(tempLong)) { // Does it match an existing definition? requirement: 3.4.12.2.c
                tempLongLst.add(tempLong);
            } else {
                unkIndexList.add(new UInteger(index)); // requirement: 3.4.12.2.c
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

        if (configurationAdapter != null){
            configurationAdapter.configurationChanged(this);
        }

    }

    /**
     * The publishAlertEvent operation allows an external software entity to
     * publish Alert events through the Alert service. The method will return
     * null if something goes wrong or if the generation of that event is disabled
     *
     * @param interaction The interaction object of the operation, can be null
     * @param alertDefinitionName The name of the Alert Definition
     * @param argumentValues The argument values to be published
     * @param argumentIds If null, no verification will take place
     * @param source The source of the alert
     * @return Returns the object instance identifier of the published event. 
     * Null if the event was not publish because of some error, or the generation
     * of events is disabled
     */
    public Long publishAlertEvent(final MALInteraction interaction, final Identifier alertDefinitionName,
            final ArgumentValueList argumentValues, final IdentifierList argumentIds, final ObjectId source) {

        // Add code to publish an Alert following the requirements and using the  Event service
        if (manager.getEventService() == null) {
            return null;
        }

        Long objIdDef = manager.list(alertDefinitionName); // Does it exist in the manager?

        if (objIdDef == null) {
            // It doesn't... let's automatically generate the Alert Definition

            AlertDefinitionDetails alertDef = new AlertDefinitionDetails();
            alertDef.setName(alertDefinitionName);
            alertDef.setDescription("This def. was auto-generated by the Alert service");
            alertDef.setSeverity(Severity.INFORMATIONAL);
            alertDef.setGenerationEnabled(true);

            ArgumentDefinitionDetailsList args = new ArgumentDefinitionDetailsList();

            ArgumentDefinitionDetails arg = new ArgumentDefinitionDetails(); // Generate the Argument Definition

            if (argumentValues == null) {
                arg = null;
            } else {
                for (int i = 0; i < argumentValues.size(); i++) {  // Go one by one and figure it out what they are
                    ArgumentValue argumentValue = argumentValues.get(i);

                    if (argumentValue.getValue() == null) {  // Well, let's then consider it is a Double
                        arg.setRawType(Union.DOUBLE_TYPE_SHORT_FORM.byteValue());
                    } else {
                        arg.setRawType(argumentValue.getValue().getTypeShortForm().byteValue()); // Check what is the type and stamp it
                    }

                    arg.setRawUnit(null);
                    arg.setConversionCondition(null);
                    arg.setConvertedType(null);
                    arg.setConvertedUnit(null);

                    args.add(arg);
                }
            }

            alertDef.setArguments(args);
            alertDef.setArgumentIds(null); // Not necessary to fill-in

            // And... add it to the provider
            AlertDefinitionDetailsList alertDefs = new AlertDefinitionDetailsList();
            alertDefs.add(alertDef);

            try {
                LongList returnedObjIds = this.addDefinition(alertDefs, interaction);
                objIdDef = returnedObjIds.get(0);

                // Enable the Alert reporting!
                InstanceBooleanPairList ids = new InstanceBooleanPairList();
                ids.add(new InstanceBooleanPair(objIdDef, true));
                this.enableGeneration(false, ids, interaction); // Enable the reporting for this Alert Definition

            } catch (MALInteractionException ex) {
                Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // Also, check if the Alert is enabled or not!
        AlertDefinitionDetails alertDef = manager.get(objIdDef);

        if (!alertDef.getGenerationEnabled()) {  // requirement: 3.4.3.a
            return null;
        }

        // Check if the argumentIds match
        if (alertDef.getArgumentIds() != null) {
            for (int index = 0; index < alertDef.getArgumentIds().size(); index++) {
                if (!alertDef.getArgumentIds().get(index).equals(argumentIds.get(index))) {  // If it doesn't match?
                    return null;
                }
            }
        }

        AlertEventDetails alertEvent = new AlertEventDetails();
        alertEvent.setArgumentValues(argumentValues);
        alertEvent.setArgumentIds(argumentIds);


        // COM usage
        // requirement: 3.4.7.b
        Long objId = manager.getEventService().generateAndStoreEvent(AlertHelper.ALERTEVENT_OBJECT_TYPE, 
                ConfigurationProviderSingleton.getDomain(), alertEvent, objIdDef, source, interaction);

        // requirement: 3.4.5.a and 3.4.5.b and 3.4.5.c
        AlertEventDetailsList alertEvents = new AlertEventDetailsList();
        alertEvents.add(alertEvent);
        manager.getEventService().publishEvent(interaction, objId, AlertHelper.ALERTEVENT_OBJECT_TYPE, objIdDef, source, alertEvents);

        return objId;

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
        if (!confSet.getObjType().equals(AlertHelper.ALERTDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet.getDomain().equals(connection.getConnectionDetails().getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if(confSet.getObjInstIds().isEmpty()){
            manager.reconfigureDefinitions(new LongList(), new AlertDefinitionDetailsList());   // Reconfigures the Manager
            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        AlertDefinitionDetailsList pDefs = (AlertDefinitionDetailsList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                AlertHelper.ALERTDEFINITION_OBJECT_TYPE,
                connection.getConnectionDetails().getDomain(),
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
        objsSet.setDomain(connection.getConnectionDetails().getDomain());
        LongList currentObjIds = new LongList();
        currentObjIds.addAll(defObjs.keySet());
        objsSet.setObjInstIds(currentObjIds);
        objsSet.setObjType(AlertHelper.ALERTDEFINITION_OBJECT_TYPE);

        ConfigurationObjectSetList list = new ConfigurationObjectSetList();
        list.add(objsSet);

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
