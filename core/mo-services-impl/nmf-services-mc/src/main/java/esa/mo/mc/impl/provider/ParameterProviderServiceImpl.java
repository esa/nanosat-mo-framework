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
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.mc.impl.util.GroupRetrieval;
import esa.mo.reconfigurable.service.ConfigurationChangeListener;
import esa.mo.reconfigurable.service.ReconfigurableService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMService;
import org.ccsds.moims.mo.com.DuplicateException;
import org.ccsds.moims.mo.com.InvalidException;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.common.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionProvider;
import org.ccsds.moims.mo.mal.helpertools.misc.TaskScheduler;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.ReadonlyException;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.mc.parameter.provider.MonitorValuePublisher;
import org.ccsds.moims.mo.mc.parameter.provider.ParameterInheritanceSkeleton;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * Parameter service Provider.
 */
public class ParameterProviderServiceImpl extends ParameterInheritanceSkeleton implements ReconfigurableService {

    private final static double MIN_REPORTING_INTERVAL = 0.2;
    private MALProvider parameterServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private boolean storeParametersInCOMArchive = true;
    private MonitorValuePublisher publisher;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private final AtomicLong pValUniqueObjId = new AtomicLong(System.currentTimeMillis());
    protected ParameterManager manager;
    private PeriodicReportingManager periodicReportingManager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private EventConsumerServiceImpl eventServiceConsumer;
    private ConfigurationChangeListener configurationAdapter;

    /**
     * Creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread.
     *
     * @param parameterManager The Parameter Manager.
     * @throws MALException if there is an initialisation error.
     */
    public synchronized void init(ParameterManager parameterManager) throws MALException {
        long timestamp = System.currentTimeMillis();
        publisher = createMonitorValuePublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE, ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT, null, new UInteger(0));

        // shut down old service transport
        if (parameterServiceProvider != null) {
            connection.closeAll();
        }

        parameterServiceProvider = connection.startService(ParameterHelper.PARAMETER_SERVICE, true, this);

        running = true;
        manager = parameterManager;
        periodicReportingManager = new PeriodicReportingManager();
        periodicReportingManager.init(); // Initialize the Periodic Reporting Manager
        groupService.init(manager.getArchiveService());

        /*
        storeParametersInCOMArchive = Boolean.parseBoolean(System.getProperty(MCServicesHelper.STORE_IN_ARCHIVE_PROPERTY, "true"));
        String msg = MessageFormat.format("{0} = {1}", MCServicesHelper.STORE_IN_ARCHIVE_PROPERTY, storeParametersInCOMArchive);
        Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.FINE, msg);
         */
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        Logger.getLogger(ParameterProviderServiceImpl.class.getName()).info(
                "Parameter service READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != parameterServiceProvider) {
                parameterServiceProvider.close();
            }

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public ParameterValueDetailsList getValue(final LongList inIdentityIds,
            final MALInteraction interaction) throws MALException, MALInteractionException { // requirement 3.3.6.2.1
        if (inIdentityIds == null) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }
        // requirement: 3.3.8.2.c : check for wildcard first
        boolean wildcardFound = false;
        for (int index = 0; index < inIdentityIds.size(); index++) {
            if (inIdentityIds.get(index) == 0) {  // Is it the wildcard '0'? requirement: 3.3.8.2.b
                inIdentityIds.clear();
                inIdentityIds.addAll(manager.listAllIdentities()); // ... add all in a row
                wildcardFound = true;
                break;
            }
        }
        //check for errors
        if (!wildcardFound) {
            UIntegerList unkIndexList = new UIntegerList();

            for (int index = 0; index < inIdentityIds.size(); index++) {
                Long identityId = inIdentityIds.get(index);
                if (!manager.existsIdentity(identityId)) { // requirement 3.3.8.2.d: Does the ParameterIdentity exist? 
                    unkIndexList.add(new UInteger(index)); // add the index to the list of errors
                    continue;
                }
            }
            // Errors
            if (!unkIndexList.isEmpty()) { // requirement: 3.3.8.2.d
                throw new MALInteractionException(new UnknownException(unkIndexList));
            }
        }

        ParameterValueDetailsList outList = new ParameterValueDetailsList();
        ParameterValueList outPValLst = manager.getParameterValues(inIdentityIds, false); // requirement: 3.3.8.2.e

        for (int i = 0; i < inIdentityIds.size(); i++) {
            Long inIdentityId = inIdentityIds.get(i);

            outList.add(new ParameterValueDetails(inIdentityId, manager.getDefinitionId(inIdentityId),
                    Time.now(), outPValLst.get(i)));
        }

        return outList;
    }

    @Override
    public LongList enableGeneration(final Boolean isGroupIds, final InstanceBooleanPairList enableInstances,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (isGroupIds == null || enableInstances == null) { // Are the inputs null?
            throw new IllegalArgumentException("isGroupIds and enableInstances arguments must not be null");
        }

        boolean foundWildcard = false;

        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.3.4.h
        for (InstanceBooleanPair instance : enableInstances) {  // requirement: 3.3.10.2.d
            if (instance.getId() == 0) {  // Is it the wildcard '0'? requirement: 3.3.10.2.c
                manager.setGenerationEnabledAll(instance.getValue(), source, connection.getConnectionDetails());
                periodicReportingManager.refreshAll();
                foundWildcard = true;

                break;
            }
        }

        if (!foundWildcard) { // requirement: 3.3.10.2.d
            //the Ids are parameter-identity-ids 3.3.10.2.a
            if (!isGroupIds) {
                for (int index = 0; index < enableInstances.size(); index++) {
                    InstanceBooleanPair enableInstance = enableInstances.get(index);
                    objIdToBeEnabled.add(enableInstance.getId());
                    valueToBeEnabled.add(enableInstance.getValue());

                    if (!manager.existsIdentity(enableInstance.getId())) { // does it exist? 
                        unkIndexList.add(new UInteger(index)); // requirement: 3.3.10.2.g
                    }
                }
            } else { //the ids are group-identity-ids, req: 3.3.10.2.a, 3.9.4.g,h
                GroupRetrieval groupRetrievalInformation;
                groupRetrievalInformation = new GroupRetrieval(unkIndexList, invIndexList, objIdToBeEnabled,
                        valueToBeEnabled);
                //get the group instances
                groupRetrievalInformation = manager.getGroupInstancesForServiceOperation(enableInstances,
                        groupRetrievalInformation, ParameterServiceInfo.PARAMETERIDENTITY_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(), manager.listAllIdentities());

                //fill the existing lists with the generated lists
                unkIndexList = groupRetrievalInformation.getUnkIndexList();
                invIndexList = groupRetrievalInformation.getInvIndexList();
                objIdToBeEnabled = groupRetrievalInformation.getObjIdToBeEnabled();
                valueToBeEnabled = groupRetrievalInformation.getValueToBeEnabled();
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) { // requirement: 3.3.10.3.1
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }
        if (!invIndexList.isEmpty()) { // requirement: 3.3.10.3.2
            throw new MALInteractionException(new InvalidException(invIndexList));
        }

        LongList output = new LongList();

        // requirement: 3.3.10.2.i (This part of the code is only reached if no error was raised)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            // requirement: 3.3.10.2.e, 3.3.10.2.f, 3.3.10.2.j and 3.3.10.2.k
            Long out = manager.setGenerationEnabled(objIdToBeEnabled.get(index),
                    valueToBeEnabled.get(index), source,
                    connection.getConnectionDetails());
            output.add(out);

            // requirement: 3.3.10.2.l
            if (out.longValue() != objIdToBeEnabled.get(index)) {
                periodicReportingManager.refresh(objIdToBeEnabled.get(index));
            }
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        return output;
    }

    @Override
    public void setValue(final ParameterRawValueList rawValueList, final MALInteraction interaction)
            throws MALException, MALInteractionException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList readOnlyIndexList = new UIntegerList();

        if (rawValueList == null) {
            throw new IllegalArgumentException("rawValueList inputs must not be null");
        }

        for (int index = 0; index < rawValueList.size(); index++) {
            Long identityId = rawValueList.get(index).getParamInstId();
            ParameterRawValue newValue = rawValueList.get(index);
            ParameterDefinition pDef = manager.getParameterDefinition(identityId);

            //requirement 3.3.9.2.b
            if (identityId == 0) {
                invIndexList.add(new UInteger(index));
                continue;
            }

            //requirement 3.3.9.2.c
            if (!manager.existsIdentity(identityId)) {
                unkIndexList.add(new UInteger(index));
                continue;
            }

            //requriement 3.3.9.2.d: checks if a parameter is readOnly
            if (manager.isReadOnly(identityId)) {
                readOnlyIndexList.add(new UInteger(index));
                continue;
            }

            // requirement: 3.3.9.2.f the new rawValues type and its definitions rawType must be the same
            if (!((Integer) newValue.getRawValue().getTypeId().getSFP()).equals(pDef.getRawType().getValue())) {
                invIndexList.add(new UInteger(index));
                continue;
            }
        }
        // requirement: 3.3.9.2.g: before changes are made, possible errors are thrown
        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.3.9.3.2 
            throw new MALInteractionException(new InvalidException(invIndexList));
        }
        if (!unkIndexList.isEmpty()) { // requirement: 3.3.9.3.1 (error: a and b)
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }
        if (!readOnlyIndexList.isEmpty()) { // requirement: 3.3.9.3.3 
            throw new MALInteractionException(new ReadonlyException(readOnlyIndexList));
        }

        //requirement 3.3.4.i   
        ObjectId source = manager.storeCOMOperationActivity(interaction);
        //atomic behaviour while setting the values. So let all values have the same timestamp for creation
        //        FineTime timestamp = HelperTime.getTimestamp();
        //requirement: 3.3.9.2.e
        ParameterValueList newParamValues = manager.setValues(rawValueList);
        FineTime timestamp = FineTime.now();

        //requirement: 3.3.9.2.h, 3.3.9.2.i
        List<ParameterInstance> toPublishParamInstances = new ArrayList<>();
        HeterogeneousList noPublishParamValList = new HeterogeneousList();
        LongList noPublishRelatedIds = new LongList();
        ObjectIdList noPublishSourceIds = new ObjectIdList();
        FineTimeList timestamps = new FineTimeList();
        for (int i = 0; i < newParamValues.size(); i++) {
            final Long identityId = rawValueList.get(i).getParamInstId();
            if (manager.getParameterDefinition(identityId).getGenerationEnabled()) {
                //for the parameters where values have to be published (generation is enabled)
                toPublishParamInstances.add(new ParameterInstance(manager.getName(identityId), newParamValues.get(i),
                        source, null));
            } else {
                //for the parameters where values do not have to be published (generation is disabled)
                noPublishParamValList.add(newParamValues.get(i));
                noPublishRelatedIds.add(manager.getDefinitionId(identityId));
                noPublishSourceIds.add(source);
                timestamps.add(timestamp);
            }
        }

        //for the parameters where values have to be published (generation is enabled)
        if (!toPublishParamInstances.isEmpty()) {
            pushMultipleParameterValues(toPublishParamInstances, storeParametersInCOMArchive);
        }
        //for the parameters where values do not have to be published (generation is disabled)
        if (!noPublishParamValList.isEmpty()) {
            manager.storeAndGenerateMultiplePValobjId(noPublishParamValList, noPublishRelatedIds, noPublishSourceIds,
                    connection.getConnectionDetails(), timestamps);
        }
    }

    @Override
    public ObjectInstancePairList listDefinition(final IdentifierList paramNames, final MALInteraction interaction)
            throws MALException, MALInteractionException { // requirement: 3.3.11.2.a
        ObjectInstancePairList retDefinitions = new ObjectInstancePairList();

        if (paramNames == null) { // Is the input null?
            throw new IllegalArgumentException("IdentifierList argument must not be null");
        }

        boolean wildcardFound = false;
        // requirement: 3.3.11.2.c : check for wildcard first
        for (int i = 0; i < paramNames.size(); i++) { //requirement: 3.3.11.2.f foreach-cycle steps through list in order
            if (paramNames.get(i).toString().equals("*")) {  // requirement: 3.3.11.2.b
                retDefinitions.addAll(manager.listAllIdentityDefinitions()); // ... add all in a row
                wildcardFound = true;
                break;
            }
        }

        //check for errors
        if (!wildcardFound) {
            UIntegerList unkIndexList = new UIntegerList();
            for (int i = 0; i < paramNames.size(); i++) { //requirement: 3.3.11.2.f foreach-cycle steps through list in order
                Identifier name = paramNames.get(i);

                final ObjectInstancePair idPair = manager.getIdentityDefinition(name);
                if (idPair == null) {  //requirement: 3.3.11.2.d
                    unkIndexList.add(new UInteger(i));
                    continue;
                } else {
                    retDefinitions.add(idPair);  // requirement: 3.3.11.2.e
                }
            }

            // Errors
            if (!unkIndexList.isEmpty()) // requirement: 3.3.11.3.1 (error: a and b)
            {
                throw new MALInteractionException(new UnknownException(unkIndexList));
            }
        }
        return retDefinitions;
    }

    public ObjectInstancePairList addParameter(final ParameterDefinitionList defsList,
            final MALInteraction interaction) throws MALException, MALInteractionException {
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();

        if (defsList == null) { // Is the input null?
            throw new IllegalArgumentException("defsList argument must not be null");
        }

        for (int index = 0; index < defsList.size(); index++) {
            ParameterDefinition pDef = defsList.get(index);
            Identifier paramName = pDef.getName();

            // Check if the name field of the ParameterDefinition is invalid.
            if (paramName == null || paramName.equals(new Identifier("*"))
                    || paramName.equals(new Identifier(""))) { // requirement: 3.3.12.2.b
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (pDef.getReportInterval().getValue() != 0
                    && pDef.getReportInterval().getValue() < MIN_REPORTING_INTERVAL) { //requirement: 3.3.3.h, 3.3.12.2.c
                invIndexList.add(new UInteger(index));
                continue;
            }

            // Is the supplied name already given? requirement: 3.3.12.2.d
            if (manager.getIdentity(paramName) != null) {
                dupIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) { // requirement: 3.3.12.2.b
            throw new MALInteractionException(new InvalidException(invIndexList));
        }

        if (!dupIndexList.isEmpty()) { // requirement: 3.3.12.2.c
            throw new MALInteractionException(new DuplicateException(dupIndexList));
        }

        ObjectInstancePairList outPairLst = new ObjectInstancePairList();

        //requirement: 3.3.12.2.e: only if no error was raised, the new definitions should be stored 
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.3.4.g, h
        IdentifierList names = new IdentifierList();
        HeterogeneousList details = new HeterogeneousList();
        for (ParameterDefinition tempDef : defsList) { // requirement: 3.3.12.2.i ( "for each cycle" guarantees that)
            names.add(tempDef.getName());
            details.add(tempDef);
        }

        ObjectInstancePairList objectInstancePairs = manager.addMultiple(names,
                details,
                source,
                connection.getConnectionDetails());
        //store the objects
        outPairLst.addAll(objectInstancePairs); //  requirement: 3.3.12.2.g
        // Refresh the Periodic Reporting Manager for the added Definitions

        final ReconfigurableService t = this;

        Thread thread = new Thread(() -> {
            for (ObjectInstancePair newParamIds : objectInstancePairs) {
                periodicReportingManager.refresh(newParamIds.getObjIdentityInstanceId());
            }

            if (configurationAdapter != null) {
                configurationAdapter.onConfigurationChanged(t);
            }
        });

        thread.start();//To not block main thread parameter timers can be started in parallel thread

        return outPairLst; // requirement: 3.3.12.2.h
    }

    @Override
    public LongList updateDefinition(LongList paramIdentityInstIds, ParameterDefinitionList paramDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException { // requirement: 3.3.13.2.a
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (paramIdentityInstIds == null || paramDefDetails == null) { // Are the inputs null?
            throw new IllegalArgumentException("paramIdentityInstIds and paramDefDetails arguments must not be null");
        }

        for (int index = 0; index < paramIdentityInstIds.size(); index++) {
            final Long identityId = paramIdentityInstIds.get(index);

            //requirement: 3.3.13.2.c: id is Null or 0?
            if (identityId == null || identityId == 0) {
                invIndexList.add(new UInteger(index));
                continue;
            }
            //requirement: 3.3.13.2.b: The object instance identifier could not be found?
            if (!manager.existsIdentity(identityId)) {
                unkIndexList.add(new UInteger(index));
                continue;
            }
            //requirement: 3.3.3.h, 3.3.13.2.f
            final ParameterDefinition pDef = paramDefDetails.get(index);
            if (pDef.getReportInterval().getValue() != 0
                    && pDef.getReportInterval().getValue() < MIN_REPORTING_INTERVAL) {
                invIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) // requirement: 3.3.13.3.1 (error: a)
        {
            throw new MALInteractionException(new InvalidException(invIndexList));
        }

        if (!unkIndexList.isEmpty()) // requirement: 3.3.13.3.2 (error: b)
        {
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }

        //requirment 3.3.13.2.g: parameters shall only be updated if no error was raised
        LongList newDefIds = new LongList();
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.3.4.g, h
        for (int index = 0; index < paramIdentityInstIds.size(); index++) {  // requirement: 3.3.13.2.i, .k
            newDefIds.add(manager.update(paramIdentityInstIds.get(index), paramDefDetails.get(index), source, connection
                    .getConnectionDetails()));  // Change in the manager, requirement 3.3.13.2.d, g
            periodicReportingManager.refresh(paramIdentityInstIds.get(index));// then, refresh the Periodic updates
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }

        //requirement: 3.3.13.2.j
        return newDefIds;

    }

    public void removeParameter(final LongList identityIds, final MALInteraction interaction) throws MALException,
            MALInteractionException { // requirement: 3.3.11.2.1
        UIntegerList unkIndexList = new UIntegerList();
        LongList removalLst = new LongList();

        if (identityIds == null) { // Is the input null?
            throw new IllegalArgumentException("LongList argument must not be null");
        }

        for (int index = 0; index < identityIds.size(); index++) {
            Long identityId = identityIds.get(index);

            if (identityId == 0) {  // Is it the wildcard '0'? requirement: 3.3.14.2.b, .c
                removalLst.clear();  // if the wildcard is in the middle of the input list, we clear the output list and...
                removalLst.addAll(manager.listAllIdentities()); // ... add all in a row
                unkIndexList.clear();
                break;
            }

            if (!manager.existsIdentity(identityId)) { // Does it match an existing identity? requirement: 3.3.14.2.d
                unkIndexList.add(new UInteger(index));
            } else {
                removalLst.add(identityId);
            }
        }

        // Errors
        if (!unkIndexList.isEmpty()) // requirement: 3.3.14.3.1 (error: a, b)
        {
            throw new MALInteractionException(new UnknownException(unkIndexList));
        }

        // requirement: 3.3.14.2.f (Inserting the errors before this line guarantees that the requirement is met)
        // Delete from internal list; COM archive is left untouched. requirement: 3.3.14.2.e
        for (Long removalId : removalLst) {
            manager.delete(removalId);
            //requirement: 3.3.14.2.g: dont publish anymore values 
            periodicReportingManager.refresh(removalId);
        }

        if (configurationAdapter != null) {
            configurationAdapter.onConfigurationChanged(this);
        }
    }

    @Override
    public void setOnConfigurationChangeListener(ConfigurationChangeListener configurationAdapter) {
        this.configurationAdapter = configurationAdapter;
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
        if (!confSet0.getObjType().equals(ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE)
                && !confSet1.getObjType().equals(ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE)) {
            return false;
        }

        if (!confSet0.getObjType().equals(ParameterServiceInfo.PARAMETERIDENTITY_OBJECT_TYPE)
                && !confSet1.getObjType().equals(ParameterServiceInfo.PARAMETERIDENTITY_OBJECT_TYPE)) {
            return false;
        }

        // Confirm the domain
        if (!confSet0.getDomain().equals(ConfigurationProviderSingleton.getDomain()) || !confSet1.getDomain().equals(
                ConfigurationProviderSingleton.getDomain())) {
            return false;
        }

        // If the list is empty, reconfigure the service with nothing...
        if (confSet0.getObjInstIds().isEmpty() && confSet1.getObjInstIds().isEmpty()) {
            manager.reconfigureDefinitions(new LongList(), new IdentifierList(), new LongList(),
                    new ParameterDefinitionList());   // Reconfigures the Manager

            return true;
        }

        // ok, we're good to go...
        // Load the Parameter Definitions from this configuration...
        ConfigurationObjectSet confSetDefs = (confSet0.getObjType().equals(ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE)) ? confSet0 : confSet1;

        HeterogeneousList pDefs = (HeterogeneousList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetDefs.getObjInstIds());

        ConfigurationObjectSet confSetIdents = (confSet0.getObjType().equals(ParameterServiceInfo.PARAMETERIDENTITY_OBJECT_TYPE)) ? confSet0 : confSet1;

        HeterogeneousList retrievedIdents = (HeterogeneousList) HelperArchive.getObjectBodyListFromArchive(
                manager.getArchiveService(),
                ParameterServiceInfo.PARAMETERIDENTITY_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                confSetIdents.getObjInstIds());

        periodicReportingManager.pause();

        IdentifierList idents = new IdentifierList();
        for (Element element : retrievedIdents) {
            idents.add((Identifier) element);
        }
        manager.reconfigureDefinitions(confSetIdents.getObjInstIds(), idents,
                confSetDefs.getObjInstIds(), pDefs);   // Reconfigures the Manager

        periodicReportingManager.refreshAll();  // Refresh the reporting
        periodicReportingManager.start();

        return true;
    }

    @Override
    public ConfigurationObjectDetails getCurrentConfiguration() {
        ConfigurationObjectSetList list = manager.getCurrentConfiguration(
                ParameterServiceInfo.PARAMETERIDENTITY_OBJECT_TYPE,
                ParameterServiceInfo.PARAMETERDEFINITION_OBJECT_TYPE
        );

        // Needs the Common API here!
        return new ConfigurationObjectDetails(list);
    }

    @Override
    public COMService getCOMService() {
        return ParameterHelper.PARAMETER_SERVICE;
    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body,
                final Map qosProperties) throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header, final MALErrorBody body,
                final Map qosProperties) throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine(
                    "PublishInteractionListener::publishRegisterErrorReceived");
        }

    }

    private class PeriodicReportingManager { // requirement: 3.3.2.a.a

        private HashMap<Long, TaskScheduler> timerList; // Timers list
        boolean active = false; // Flag that determines if the Manager publishes or not

        public PeriodicReportingManager() {
            timerList = new HashMap<>();
        }

        public void start() {
            active = true;
        }

        public void pause() {
            active = false;
        }

        public void init() {   // Refresh all the Parameter Definitions on the Manager
            this.refreshList(manager.listAllIdentities());
            active = true; // set active flag to true
        }

        /**
         * refreshes the interval for periodic updates of the parameter with the
         * given identityId
         *
         * @param identityId the identityId of the Parameter
         *
         */
        public void refresh(Long identityId) {
            if (timerList.containsKey(identityId)) { // Does it exist in the Periodic Manager?
                this.removePeriodicReporting(identityId);
            }
            // get parameter definition
            ParameterDefinition pDef = manager.getParameterDefinition(identityId);
            if (pDef != null) { // Does it exist in the Parameter Definitions List?
                //requirement: 3.3.3.d
                if (pDef.getReportInterval().getValue() != 0 && pDef.getGenerationEnabled()) { // Is the periodic reporting active?
                    this.addPeriodicReporting(identityId);
                }
            }
        }

        public void refreshList(LongList identityIds) {
            if (identityIds == null) {
                return;
            }
            for (Long identityId : identityIds) {
                refresh(identityId);
            }
        }

        public void refreshAll() {
            this.refreshList(manager.listAllIdentities());
        }

        /**
         * publishes the first update of the parameter after setting the
         * generatinEneabled-Field to true and starts the timer for the further
         * periodic updates.
         *
         * @param identityId the identityId of the parameter to be updated
         * periodically
         */
        private void addPeriodicReporting(Long identityId) {
            TaskScheduler timer = new TaskScheduler(1);
            timerList.put(identityId, timer);
            publishPeriodicParameterUpdate(identityId);
            //requirement: 3.3.3.c
            startTimer(identityId, manager.getParameterDefinition(identityId).getReportInterval());
        }

        private void removePeriodicReporting(Long identityId) {
            this.stopTimer(identityId);
            timerList.remove(identityId);
        }

        /**
         *
         * @param identityId
         * @param interval
         */
        private void startTimer(final Long identityId, final Duration interval) {  // requirement: 3.3.3.c
            timerList.get(identityId).scheduleTask(new Thread(() -> {
                if (active) {
                    if (identityId == -1) {
                        return;
                    }
                    if (manager.getParameterDefinition(identityId).getGenerationEnabled()) {
                        publishPeriodicParameterUpdate(identityId);
                    }
                }
            }), 0, (int) (interval.getValue() * 1000), TimeUnit.MILLISECONDS, true);
            // the time has to be converted to milliseconds by multiplying by 1000
        }

        private void stopTimer(final Long identityId) {
            timerList.get(identityId).stopLast();
        }

    }

    /**
     *
     * The pushSingleParameterValueAttribute operation allows an external entity
     * to push Attribute values through the monitorValue operation of the
     * Parameter service. If there is no parameter definition with the submitted
     * name, the method shall automatically create the parameter definition in
     * the Parameter service.
     *
     * @param name The name of the Parameter as set in the parameter definition
     * @param value The value of the parameter to be pushed
     * @param source The source of the parameter. Can be null
     * @param timestamp The timestamp of the parameter. If null, the method will
     * automatically use the System's time
     * @return Returns true if the push was successful. False otherwise. Please
     * notice that if no consumers are registered on the broker, then the value
     * of true will be returned because not error happened.
     */
    public Boolean pushSingleParameterValueAttribute(final Identifier name,
            final Attribute value, final ObjectId source, final Time timestamp) {
        final ParameterValue parameterValue = new ParameterValue(ValidityState.VALID, value, null);
        ArrayList<ParameterInstance> parameters = new ArrayList<>(1);
        parameters.add(new ParameterInstance(name, parameterValue, source, timestamp));

        return this.pushMultipleParameterValues(parameters);
    }

    /**
     *
     * The pushParameterValue operation allows an external entity to push
     * Parameter values through the monitorValue operation of the Parameter
     * service. If there is no parameter definition with the submitted name, the
     * method shall automatically create the parameter definition in the
     * Parameter service
     *
     * @param name The name of the Parameter as set in the parameter definition
     * @param parameterValue The parameter value to be pushed
     * @param source The source of the parameter. Can be null
     * @param timestamp The timestamp of the parameter. If null, the method will
     * automatically use the System's time
     * @return Returns true if the push was successful. False otherwise. Please
     * notice that if no consumers are registered on the broker, then the value
     * of true will be returned because not error happened.
     */
    @Deprecated
    public Boolean pushParameterValue(final Identifier name,
            final ParameterValue parameterValue, final ObjectId source, final Time timestamp) {
        ParameterInstance instance = new ParameterInstance(name, parameterValue, source, timestamp);
        ArrayList<ParameterInstance> parameters = new ArrayList<>();
        parameters.add(instance);

        return this.pushMultipleParameterValues(parameters);
    }

    /**
     *
     * The pushParameterValue operation allows an external entity to push
     * Parameter values through the monitorValue operation of the Parameter
     * service. If there is no parameter definition with the submitted name, the
     * method shall automatically create the parameter definition in the
     * Parameter service. The parameter value will not be stored in the COM
     * Archive.
     *
     * @param parameters The list of parameter instances.
     * @return Returns true if the push was successful. False otherwise. Please
     * notice that if no consumers are registered on the broker, then the value
     * of true will be returned because no error happened.
     */
    public Boolean pushMultipleParameterValues(final List<ParameterInstance> parameters) {
        return this.pushMultipleParameterValues(parameters, storeParametersInCOMArchive);
    }

    /**
     *
     * The pushParameterValue operation allows an external entity to push
     * Parameter values through the monitorValue operation of the Parameter
     * service. If there is no parameter definition with the submitted name, the
     * method shall automatically create the parameter definition in the
     * Parameter service.
     *
     * @param parameters The list of parameter instances to be pushed.
     * @param storeIt A flag that defines if the Parameters are going to be
     * stored in the COM Archive
     * @return Returns true if the push was successful. False otherwise. Please
     * notice that if no consumers are registered on the broker, then the value
     * of true will be returned because not error happened.
     */
    public Boolean pushMultipleParameterValues(final List<ParameterInstance> parameters, final boolean storeIt) {
        try {
            synchronized (lock) {
                if (!isRegistered) {
                    IdentifierList keys = new IdentifierList();
                    keys.add(new Identifier("objIdentityInstanceName"));
                    keys.add(new Identifier("objIdentityInstanceId"));
                    keys.add(new Identifier("objDefInstanceId"));
                    keys.add(new Identifier("pValObjIds"));
                    AttributeTypeList keyTypes = new AttributeTypeList();
                    keyTypes.add(AttributeType.IDENTIFIER);
                    keyTypes.add(AttributeType.LONG);
                    keyTypes.add(AttributeType.LONG);
                    keyTypes.add(AttributeType.LONG);
                    publisher.register(keys, keyTypes, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            final ObjectInstancePairList outIds = new ObjectInstancePairList(parameters.size());
            final HeterogeneousList pVals = new HeterogeneousList();
            final List<ParameterInstance> parameterInstances = new ArrayList<>(parameters.size());

            for (int i = 0; i < parameters.size(); i++) {
                // Does the submitted name exists in the manager?
                ParameterInstance parameter = parameters.get(i);
                ObjectInstancePair objId = manager.getIdentityDefinition(parameter.getName());

                if (objId == null) { // If the definition is not in the manager, then create it
                    Attribute rawValue = parameter.getParameterValue().getRawValue();
                    AttributeType rawType = AttributeType.DOUBLE; // Default

                    if (rawValue != null) {
                        // Check what is the type and stamp it
                        rawType = new AttributeType((Integer) rawValue.getTypeId().getSFP());
                    }

                    ParameterDefinition pDef = new ParameterDefinition(
                            parameter.getName(),
                            "This def. was auto-generated by the Parameter service",
                            rawType,
                            null,
                            true, // Auto enable the generation
                            new Duration(0),
                            null,
                            null);

                    ParameterDefinitionList pDefs = new ParameterDefinitionList(1);
                    pDefs.add(pDef);

                    try {
                        // Enable the reporting for this Alert Definition
                        ObjectInstancePairList returnedObjIds = this.addParameter(pDefs, null);
                        objId = returnedObjIds.get(0);
                    } catch (MALInteractionException | MALException ex) {
                        Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                        return false;
                    }
                }

                Long id = objId.getObjIdentityInstanceId();
                ParameterDefinition pDef2 = (ParameterDefinition) manager.getDefinition(id);
                if (pDef2.getGenerationEnabled()) {
                    outIds.add(objId); // Don't push the PVals that are not enabled...
                    ParameterValue value = parameter.getParameterValue();
                    Attribute convertedValue = value.getConvertedValue();
                    ValidityState validityState = value.getValidityState();

                    // If the conversion value was not provided, we can try to generate it
                    if (convertedValue == null) {
                        ParameterValue newPVal = manager.generateNewParameterValue(
                                value.getRawValue(), pDef2, false);
                        convertedValue = newPVal.getConvertedValue();
                        validityState = newPVal.getValidityState();
                    }

                    pVals.add(new ParameterValue(validityState, value.getRawValue(), convertedValue));
                    parameterInstances.add(parameter);
                }
            }

            if (pVals.isEmpty()) {
                return true; // No parameters values are going to be pushed
            }

            LongList relatedIds = new LongList(outIds.size());
            ObjectIdList sourceIds = new ObjectIdList(outIds.size());
            FineTimeList timestamps = new FineTimeList(outIds.size());

            //requirement: 3.3.9.2.h all Parameter-Value objects shall have the same creation-time
            final Time defaultTimestamp2 = Time.now();
            final FineTime defaultTimestamp = defaultTimestamp2.toFineTime();

            for (int i = 0; i < outIds.size(); i++) {
                relatedIds.add(outIds.get(i).getObjDefInstanceId());
                ObjectId sourceId = parameters.get(i).getSource();
                sourceId = (sourceId != null) ? sourceId : new ObjectId();
                sourceIds.add(sourceId);
                final FineTime timestamp = (parameters.get(i).getTimestamp() != null)
                        ? parameters.get(i).getTimestamp().toFineTime()
                        : defaultTimestamp;
                timestamps.add(timestamp);
            }

            final LongList pValObjIds;

            if (storeIt) {
                pValObjIds = manager.storeAndGenerateMultiplePValobjId(pVals, relatedIds,
                        sourceIds, connection.getConnectionDetails(), timestamps);
            } else {
                // Well, if we don't store it, then we shall use the local unique variable
                pValObjIds = new LongList(pVals.size());
                for (Element parameterVal : pVals) {
                    pValObjIds.add(pValUniqueObjId.incrementAndGet());
                }
            }

            final UpdateHeaderList hdrlst = new UpdateHeaderList(parameters.size());
            final ObjectIdList objectIdlst = new ObjectIdList(parameters.size());
            final ParameterValueList pVallst = new ParameterValueList(parameters.size());

            for (int i = 0; i < parameterInstances.size(); i++) {
                //  requirements: 3.3.7.2.a , 3.3.7.2.b , 3.3.7.2.c , 3.3.7.2.d 
                AttributeList keys = new AttributeList();
                keys.add(new Identifier(manager.getName(outIds.get(i).getObjIdentityInstanceId()).toString()));
                keys.add(new Union(outIds.get(i).getObjIdentityInstanceId()));
                keys.add(new Union(outIds.get(i).getObjDefInstanceId()));
                keys.add(new Union(pValObjIds.get(i)));

                Time time = parameterInstances.get(i).getTimestamp();
                time = (time == null) ? defaultTimestamp2 : time; //  requirement: 3.3.5.2.5

                //requirement: 3.3.7.2.e : timestamp must be the same as for the creation of the ParameterValue
                URI source = connection.getConnectionDetails().getProviderURI();
                UpdateHeader updateHeader = new UpdateHeader(new Identifier(source.getValue()),
                        connection.getConnectionDetails().getDomain(), keys.getAsNullableAttributeList());

                // requirement: 3.3.7.2.g (3.3.5.2.f not necessary) 
                // requirement: 3.3.7.2.h 
                publisher.publish(updateHeader, parameterInstances.get(i).getSource(),
                        parameterInstances.get(i).getParameterValue());
            }
        } catch (IllegalArgumentException | MALInteractionException | MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        }

        return true;
    }

    /**
     * publishes a periodic parameter update for the given parameter
     *
     * @param identityId the id of the parameter identity
     */
    private void publishPeriodicParameterUpdate(final Long identityId) {
        publishPeriodicParameterUpdate(identityId, storeParametersInCOMArchive);
    }

    /**
     * publishes a periodic parameter update for the given parameter
     *
     * @param identityId the id of the parameter identity
     * @param storeInCOMArchive flag indicating whether or not the parameter
     * should be stored in the archive
     */
    private void publishPeriodicParameterUpdate(final Long identityId, boolean storeInCOMArchive) {
        try {
            /*
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.INFO,
                    "start publishing periodic parameter update with identityId: {0} at {1}",
                    new Object[]{identityId, System.currentTimeMillis()});
             */
            final ParameterValue parameterValue = manager.getParameterValue(identityId);
            final Identifier name = manager.getName(identityId);

            ArrayList<ParameterInstance> parameters = new ArrayList<>(1);
            parameters.add(new ParameterInstance(name, parameterValue, null, Time.now()));
            this.pushMultipleParameterValues(parameters, storeInCOMArchive);
        } catch (IllegalArgumentException | MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

}
