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

import esa.mo.com.impl.consumer.EventConsumerServiceImpl;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.mc.impl.provider.model.GroupRetrieval;
import esa.mo.mc.impl.util.MCServicesHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPair;
import org.ccsds.moims.mo.com.structures.InstanceBooleanPairList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.FineTimeList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALErrorBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.MCHelper;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.provider.MonitorValuePublisher;
import org.ccsds.moims.mo.mc.parameter.provider.ParameterInheritanceSkeleton;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequest;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequestList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 *
 */
public class ParameterProviderServiceImpl extends ParameterInheritanceSkeleton {

    private MALProvider parameterServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private MonitorValuePublisher publisher;
    private boolean isRegistered = false;
    private final Object lock = new Object();
    private final AtomicLong pValUniqueObjId = new AtomicLong(System.currentTimeMillis());
    private ParameterManager manager;
    private PeriodicReportingManager periodicReportingManager;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final GroupServiceImpl groupService = new GroupServiceImpl();
    private EventConsumerServiceImpl eventServiceConsumer;

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param parameterManager
     * @throws MALException On initialisation error.
     */
    public synchronized void init(ParameterManager parameterManager) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.deepInit(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MCHelper.MC_AREA_NAME, MCHelper.MC_AREA_VERSION) == null) {
                MCHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ParameterHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }

        }

        publisher = createMonitorValuePublisher(ConfigurationProviderSingleton.getDomain(),
                ConfigurationProviderSingleton.getNetwork(),
                SessionType.LIVE,
                ConfigurationProviderSingleton.getSourceSessionName(),
                QoSLevel.BESTEFFORT,
                null,
                new UInteger(0));

        // shut down old service transport
        if (null != parameterServiceProvider) {
            connection.closeAll();
        }

        parameterServiceProvider = connection.startService(ParameterHelper.PARAMETER_SERVICE_NAME.toString(),
                ParameterHelper.PARAMETER_SERVICE, this);

        running = true;
        manager = parameterManager;
        periodicReportingManager = new PeriodicReportingManager();
        periodicReportingManager.init(); // Initialize the Periodic Reporting Manager

        groupService.init(manager.getArchiveService());

        initialiased = true;
        Logger.getLogger(ParameterProviderServiceImpl.class.getName()).info("Parameter service READY");
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
    public ParameterValueDetailsList getValue(final LongList inIdentityIds, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement 3.3.6.2.1

        if (null == inIdentityIds) { // Is the input null?
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
                throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            }
        }

        ParameterValueDetailsList outList = new ParameterValueDetailsList();
        ParameterValueList outPValLst = manager.getParameterValues(inIdentityIds, false); // requirement: 3.3.8.2.e

        for (int i = 0; i < inIdentityIds.size(); i++) {
            Long inIdentityId = inIdentityIds.get(i);

            outList.add(new ParameterValueDetails(
                    inIdentityId,
                    manager.getDefinitionId(inIdentityId),
                    new Time(manager.getParameterValuesTimestamp(inIdentityId)),
                    outPValLst.get(i)
            ));
        }

        return outList;
    }

    @Override
    public void enableGeneration(final Boolean isGroupIds, final InstanceBooleanPairList enableInstances,
            final MALInteraction interaction) throws MALException, MALInteractionException {

        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        LongList objIdToBeEnabled = new LongList();
        BooleanList valueToBeEnabled = new BooleanList();

        if (null == isGroupIds || null == enableInstances) { // Are the inputs null?
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
                groupRetrievalInformation = new GroupRetrieval(unkIndexList, invIndexList, objIdToBeEnabled, valueToBeEnabled);
                //get the group instances
                groupRetrievalInformation = manager.getGroupInstancesForServiceOperation(enableInstances,
                        groupRetrievalInformation, ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE,
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
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!invIndexList.isEmpty()) { // requirement: 3.3.10.3.2
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        // requirement: 3.3.10.2.i (This part of the code is only reached if no error was raised)
        for (int index = 0; index < objIdToBeEnabled.size(); index++) {
            // requirement: 3.3.10.2.e, 3.3.10.2.f, 3.3.10.2.j and 3.3.10.2.k
            boolean changed = manager.setGenerationEnabled(objIdToBeEnabled.get(index), valueToBeEnabled.get(index), source, connection.getConnectionDetails());
            // requirement: 3.3.10.2.l
            if (changed) {
                periodicReportingManager.refresh(objIdToBeEnabled.get(index));
            }
        }

    }

    @Override
    public void setValue(final ParameterRawValueList rawValueList, final MALInteraction interaction) throws MALException, MALInteractionException {
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();
        UIntegerList readOnlyIndexList = new UIntegerList();

        if (null == rawValueList) // Is the input null?
        {
            throw new IllegalArgumentException("rawValueList inputs must not be null");
        }

        for (int index = 0; index < rawValueList.size(); index++) {
            Long identityId = rawValueList.get(index).getParamInstId();
            ParameterRawValue newValue = rawValueList.get(index);
            ParameterDefinitionDetails pDef = manager.getParameterDefinition(identityId);

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
            if (!newValue.getRawValue().getTypeShortForm().equals(new Integer(pDef.getRawType()))) {
                invIndexList.add(new UInteger(index));
                continue;
            }
        }
        // requirement: 3.3.9.2.g: before changes are made, possible errors are thrown
        // Errors
        if (!invIndexList.isEmpty()) // requirement: 3.3.9.3.2 
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }
        if (!unkIndexList.isEmpty()) // requirement: 3.3.9.3.1 (error: a and b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }
        if (!readOnlyIndexList.isEmpty()) // requirement: 3.3.9.3.3 
        {
            throw new MALInteractionException(new MALStandardError(MCHelper.READONLY_ERROR_NUMBER, readOnlyIndexList));
        }

        //requirement 3.3.4.i   
        ObjectId source = manager.storeCOMOperationActivity(interaction);
        //atomic behaviour while setting the values. So let all values have the same timestamp for creation
        FineTime timestamp = HelperTime.getTimestamp();
        //requirement: 3.3.9.2.e
        ParameterValueList newParamValues = manager.setValues(rawValueList, timestamp.getValue(), connection.getConnectionDetails());

        //requirement: 3.3.9.2.h, 3.3.9.2.i
        List<ParameterInstance> toPublishParamInstances = new ArrayList<ParameterInstance>();
        ParameterValueList noPublishParamValList = new ParameterValueList();
        LongList noPublishRelatedIds = new LongList();
        ObjectIdList noPublishSourceIds = new ObjectIdList();
        FineTimeList timestamps = new FineTimeList();
        for (int i = 0; i < newParamValues.size(); i++) {
            final Long identityId = rawValueList.get(i).getParamInstId();
            if (manager.getParameterDefinition(identityId).getGenerationEnabled()) {
                //for the parameters where values have to be published (generation is enabled)
                toPublishParamInstances.add(new ParameterInstance(manager.getName(identityId), newParamValues.get(i), source, null));
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
            pushMultipleParameterValues(toPublishParamInstances, true);
        }
        //for the parameters where values do not have to be published (generation is disabled)
        if (!noPublishParamValList.isEmpty()) {
            manager.storeAndGenerateMultiplePValobjId(noPublishParamValList, noPublishRelatedIds, noPublishSourceIds, connection.getConnectionDetails(), timestamps);
        }
    }

    @Override
    public ObjectInstancePairList listDefinition(final IdentifierList paramNames, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.3.11.2.a
        ObjectInstancePairList retDefinitions = new ObjectInstancePairList();

        if (null == paramNames) { // Is the input null?
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
                throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
            }
        }
        return retDefinitions;
    }

    @Override
    public ObjectInstancePairList addParameter(final ParameterCreationRequestList paramCreationReqList,
            final MALInteraction interaction) throws MALException, MALInteractionException {

        UIntegerList invIndexList = new UIntegerList();
        UIntegerList dupIndexList = new UIntegerList();

        if (null == paramCreationReqList) // Is the input null?
        {
            throw new IllegalArgumentException("ParameterCreationRequestList argument must not be null");
        }

        for (int index = 0; index < paramCreationReqList.size(); index++) {
            ParameterCreationRequest paramCreationReq = paramCreationReqList.get(index);
            Identifier paramName = paramCreationReq.getName();

            // Check if the name field of the ParameterDefinition is invalid.
            if (paramName == null
                    || paramName.equals(new Identifier("*"))
                    || paramName.equals(new Identifier(""))) { // requirement: 3.3.12.2.b
                invIndexList.add(new UInteger(index));
                continue;
            }

            final ParameterDefinitionDetails pDef = paramCreationReq.getParamDefDetails();
//            if (pDef.getReportInterval().getValue() != 0 && !manager.getProvidedIntervals().contains(pDef.getReportInterval())) { //requirement: 3.3.3.h, 3.3.12.2.c
            if (pDef.getReportInterval().getValue() != 0) { //requirement: 3.3.3.h, 3.3.12.2.c
                invIndexList.add(new UInteger(index));
                continue;
            }

            if (manager.getIdentity(paramName) != null) { // Is the supplied name already given? requirement: 3.3.12.2.d
                dupIndexList.add(new UInteger(index));
                continue;
            }
        }
        // Errors
        if (!invIndexList.isEmpty()) // requirement: 3.3.12.2.b
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }
        if (!dupIndexList.isEmpty()) // requirement: 3.3.12.2.c
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.DUPLICATE_ERROR_NUMBER, dupIndexList));
        }

        ObjectInstancePairList outPairLst = new ObjectInstancePairList();

        //requirement: 3.3.12.2.e: only if no error was raised, the new definitions should be stored 
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.3.4.g, h 
        for (ParameterCreationRequest tempParameterCreationRequest : paramCreationReqList) { // requirement: 3.3.12.2.i ( "for each cycle" guarantees that)
            Identifier paramName = tempParameterCreationRequest.getName();
            final ObjectInstancePair newParamIds = manager.add(paramName,
                    tempParameterCreationRequest.getParamDefDetails(),
                    source,
                    connection.getConnectionDetails());
            //store the objects
            outPairLst.add(newParamIds); //  requirement: 3.3.12.2.g
            // Refresh the Periodic Reporting Manager for the added Definitions
            periodicReportingManager.refresh(newParamIds.getObjIdentityInstanceId());
        }

        return outPairLst; // requirement: 3.3.12.2.h
    }

    @Override
    public LongList updateDefinition(LongList paramIdentityInstIds, ParameterDefinitionDetailsList paramDefDetails,
            MALInteraction interaction) throws MALInteractionException, MALException { // requirement: 3.3.13.2.a
        UIntegerList unkIndexList = new UIntegerList();
        UIntegerList invIndexList = new UIntegerList();

        if (null == paramIdentityInstIds || null == paramDefDetails) // Are the inputs null?
        {
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
            final ParameterDefinitionDetails pDef = paramDefDetails.get(index);
//            if (pDef.getReportInterval().getValue() != 0 && !manager.getProvidedIntervals().contains(pDef.getReportInterval())) {
            if (pDef.getReportInterval().getValue() != 0) {
                invIndexList.add(new UInteger(index));
                continue;
            }
        }

        // Errors
        if (!invIndexList.isEmpty()) // requirement: 3.3.13.3.1 (error: a)
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, invIndexList));
        }

        if (!unkIndexList.isEmpty()) // requirement: 3.3.13.3.2 (error: b)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        //requirment 3.3.13.2.g: parameters shall only be updated if no error was raised
        LongList newDefIds = new LongList();
        ObjectId source = manager.storeCOMOperationActivity(interaction); // requirement: 3.3.4.g, h
        for (int index = 0; index < paramIdentityInstIds.size(); index++) {  // requirement: 3.3.13.2.i, .k
            newDefIds.add(manager.update(paramIdentityInstIds.get(index), paramDefDetails.get(index), source, connection.getConnectionDetails()));  // Change in the manager, requirement 3.3.13.2.d, g
            periodicReportingManager.refresh(paramIdentityInstIds.get(index));// then, refresh the Periodic updates
        }

        //requirement: 3.3.13.2.j
        return newDefIds;

    }

    @Override
    public void removeParameter(final LongList identityIds, final MALInteraction interaction) throws MALException, MALInteractionException { // requirement: 3.3.11.2.1
        UIntegerList unkIndexList = new UIntegerList();
        LongList removalLst = new LongList();

        if (null == identityIds) { // Is the input null?
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
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, unkIndexList));
        }

        // requirement: 3.3.14.2.f (Inserting the errors before this line guarantees that the requirement is met)
        // Delete from internal list; COM archive is left untouched. requirement: 3.3.14.2.e
        for (Long removalId : removalLst) {
            manager.delete(removalId);
            //requirement: 3.3.14.2.g: dont publish anymore values 
            periodicReportingManager.refresh(removalId);
        }

    }

    public static final class PublishInteractionListener implements MALPublishInteractionListener {

        @Override
        public void publishDeregisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishDeregisterAckReceived");
        }

        @Override
        public void publishErrorReceived(final MALMessageHeader header, final MALErrorBody body, final Map qosProperties)
                throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishErrorReceived");
        }

        @Override
        public void publishRegisterAckReceived(final MALMessageHeader header, final Map qosProperties)
                throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterAckReceived");
        }

        @Override
        public void publishRegisterErrorReceived(final MALMessageHeader header,
                final MALErrorBody body,
                final Map qosProperties)
                throws MALException {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).fine("PublishInteractionListener::publishRegisterErrorReceived");
        }

    }

    private class PeriodicReportingManager { // requirement: 3.3.2.a.a

        private HashMap<Long, Timer> timerList; // Timers list
        boolean active = false; // Flag that determines if the Manager publishes or not

        public PeriodicReportingManager() {
            timerList = new HashMap<Long, Timer>();
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
         * @param objId the identityId of the Parameter
         *
         */
        public void refresh(Long identityId) {
            if (timerList.containsKey(identityId)) { // Does it exist in the Periodic Manager?
                this.removePeriodicReporting(identityId);
            }
            // get parameter definition
            ParameterDefinitionDetails pDef = manager.getParameterDefinition(identityId);
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
            Timer timer = new Timer();
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
            timerList.get(identityId).scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (active) {
                        if (identityId == -1) {
                            return;
                        }
                        if (manager.getParameterDefinition(identityId).getGenerationEnabled()) {
                            publishPeriodicParameterUpdate(identityId);
                        }
                    }
                }
            }, 0, (int) (interval.getValue() * 1000)); // the time has to be converted to milliseconds by multiplying by 1000
        }

        private void stopTimer(final Long identityId) {
            timerList.get(identityId).cancel();
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
        final ParameterValue parameterValue = new ParameterValue(new UOctet((short) 0), value, null);
        ParameterInstance instance = new ParameterInstance(name, parameterValue, source, timestamp);

        ArrayList<ParameterInstance> parameters = new ArrayList<ParameterInstance>();
        parameters.add(instance);

        return this.pushMultipleParameterValues(parameters);

//        final ParameterValue parameterValue = manager.createParameterValue(name, value);
//        return this.pushParameterValue(name, parameterValue, source, timestamp);
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
    public Boolean pushParameterValue(final Identifier name, final ParameterValue parameterValue, final ObjectId source, final Time timestamp) {
        ParameterInstance instance = new ParameterInstance(name, parameterValue, source, timestamp);
        ArrayList<ParameterInstance> parameters = new ArrayList<ParameterInstance>();
        parameters.add(instance);

        return this.pushMultipleParameterValues(parameters);

        /*
        try {

            Long identityId = manager.getIdentity(name);  // Does the submitted name exists in the manager?
            if (identityId == null) { //create a new definition

                //fill the parameter-definition-object
                final ParameterDefinitionDetails pDef = new ParameterDefinitionDetails();

                pDef.setDescription("This Parameter Definition was automatically generated by the Parameter service during the push of a parameter value.");

                if (parameterValue.getRawValue() == null) {  // Well, let's then consider that it is a Double
                    pDef.setRawType(Union.DOUBLE_TYPE_SHORT_FORM.byteValue());
                } else {
                    pDef.setRawType(parameterValue.getRawValue().getTypeShortForm().byteValue()); // Check what is the type and stamp it
                }

                pDef.setRawUnit(null);
                pDef.setGenerationEnabled(false);
                pDef.setReportInterval(new Duration(0));
                pDef.setValidityExpression(null);
                pDef.setConversion(null);

                //fill the parameter-creation-object
                final ParameterCreationRequestList pCreationReqL = new ParameterCreationRequestList();
                final ParameterCreationRequest pCreationReq = new ParameterCreationRequest(name, pDef);
                pCreationReqL.add(pCreationReq);
                try {
                    final ObjectInstancePairList returnedObjIds = this.addParameter(pCreationReqL, null); // Enable the reporting for this Definition
                    identityId = returnedObjIds.get(0).getObjIdentityInstanceId();
                    manager.storeAndGeneratePValobjId(identityId, parameterValue, source, connection.getConnectionDetails(), new FineTime(timestamp.getValue())); // requirement: 3.3.4.d

                } catch (MALInteractionException | MALException ex) {
                    Logger.getLogger(AlertProviderServiceImpl.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                //check if generation of updates is enabled
                final ParameterDefinitionDetails pDef = manager.getParameterDefinition(identityId);
                if (pDef.getGenerationEnabled()) {//publish an update
                    publishParameterUpdate(identityId, parameterValue, source, timestamp);
                } else { //just store it in the archive
                    manager.storeAndGeneratePValobjId(identityId, parameterValue, source, connection.getConnectionDetails(), (timestamp == null) ? null : new FineTime(timestamp.getValue())); // requirement: 3.3.4.d
                }
            }
        } catch (IllegalArgumentException | MALException | MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class
                    .getName()).log(Level.WARNING, "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        }
        return true;
         */
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
     * @param parameters
     * @return Returns true if the push was successful. False otherwise. Please
     * notice that if no consumers are registered on the broker, then the value
     * of true will be returned because no error happened.
     */
    public Boolean pushMultipleParameterValues(final List<ParameterInstance> parameters) {
        return this.pushMultipleParameterValues(parameters, false);
    }

    /**
     *
     * The pushParameterValue operation allows an external entity to push
     * Parameter values through the monitorValue operation of the Parameter
     * service. If there is no parameter definition with the submitted name, the
     * method shall automatically create the parameter definition in the
     * Parameter service
     *
     * @param parameters
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
                    final EntityKeyList lst = new EntityKeyList();
                    lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                    publisher.register(lst, new PublishInteractionListener());
                    isRegistered = true;
                }
            }

            final ObjectInstancePairList outIds = new ObjectInstancePairList(parameters.size());
            final ParameterValueList parameterValueList = new ParameterValueList(parameters.size());
            final List<ParameterInstance> parameterInstances = new ArrayList<ParameterInstance>(parameters.size());

            for (int i = 0; i < parameters.size(); i++) {
                ObjectInstancePair objId = manager.getIdentityDefinition(parameters.get(i).getName());  // Does the submitted name exists in the manager? 

                if (objId == null) { // If the definition is not in the manager, then create it
                    ParameterDefinitionDetails pDef = new ParameterDefinitionDetails();
                    pDef.setDescription("This def. was auto-generated by the Parameter service");

                    if (parameters.get(i).getParameterValue().getRawValue() == null) {  // Well, let's then consider that it is a Double
                        pDef.setRawType(Union.DOUBLE_TYPE_SHORT_FORM.byteValue());
                    } else {
                        pDef.setRawType(parameters.get(i).getParameterValue().getRawValue().getTypeShortForm().byteValue()); // Check what is the type and stamp it
                    }

                    pDef.setRawUnit(null);
                    pDef.setGenerationEnabled(false);
                    pDef.setReportInterval(new Duration(0));
                    pDef.setValidityExpression(null);
                    pDef.setConversion(null);

                    ParameterCreationRequestList pDefCreationReqs = new ParameterCreationRequestList(1);
                    ParameterCreationRequest pDefCreationReq = new ParameterCreationRequest(parameters.get(i).getName(), pDef);
                    pDefCreationReqs.add(pDefCreationReq);

                    try {
                        ObjectInstancePairList returnedObjIds = this.addParameter(pDefCreationReqs, null); // Enable the reporting for this Alert Definition 
                        objId = returnedObjIds.get(0);

                        // Auto enable the generation
                        InstanceBooleanPairList enableInstances = new InstanceBooleanPairList(1);
                        enableInstances.add(new InstanceBooleanPair(objId.getObjIdentityInstanceId(), true));
                        this.enableGeneration(false, enableInstances, null);
                    } catch (MALInteractionException ex) {
                        Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MALException ex) {
                        Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                if (((ParameterDefinitionDetails) manager.getDefinition(objId.getObjDefInstanceId())).getGenerationEnabled()) {
                    outIds.add(objId); // Don't push the PVals that are not enabled...
                    parameterValueList.add(parameters.get(i).getParameterValue());
                    parameterInstances.add(parameters.get(i));
                }
            }

            if (parameterValueList.isEmpty()) {
                return true; // No parameters values are going to be pushed
            }

            LongList relatedIds = new LongList(outIds.size());
            ObjectIdList sourceIds = new ObjectIdList(outIds.size());
            FineTimeList timestamps = new FineTimeList(outIds.size());

            for (int i = 0; i < outIds.size(); i++) {
                ObjectInstancePair objId = outIds.get(i);
                relatedIds.add(objId.getObjDefInstanceId());
                sourceIds.add(parameters.get(i).getSource());
            }

            final LongList pValObjIds;

            if (storeIt) {
                pValObjIds = manager.storeAndGenerateMultiplePValobjId(parameterValueList,
                        relatedIds, sourceIds, connection.getConnectionDetails(), timestamps);
            } else {
                // Well, if we don't store it, then we shall use the local unique variable
                pValObjIds = new LongList(parameterValueList.size());
                for (ParameterValue parameterVal : parameterValueList) {
                    pValObjIds.add(pValUniqueObjId.incrementAndGet());
                }
            }

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectIdList objectIdlst = new ObjectIdList();
            final ParameterValueList pVallst = new ParameterValueList();

            for (int i = 0; i < parameterInstances.size(); i++) {
                //  requirements: 3.3.7.2.a , 3.3.7.2.b , 3.3.7.2.c , 3.3.7.2.d 
                final EntityKey ekey = new EntityKey(new Identifier(manager.getName(outIds.get(i).getObjIdentityInstanceId()).toString()),
                        outIds.get(i).getObjIdentityInstanceId(), outIds.get(i).getObjDefInstanceId(), pValObjIds.get(i));

                Time time = parameterInstances.get(i).getTimestamp();
                time = (time == null) ? HelperTime.getTimestampMillis() : time; //  requirement: 3.3.5.2.5

                //requirement: 3.3.7.2.e : timestamp must be the same as for the creation of the ParameterValue
                hdrlst.add(new UpdateHeader(time, connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
                objectIdlst.add(parameterInstances.get(i).getSource()); // requirement: 3.3.7.2.g (3.3.5.2.f not necessary) 
                pVallst.add(parameterInstances.get(i).getParameterValue()); // requirement: 3.3.7.2.h 
            }

            publisher.publish(hdrlst, objectIdlst, pVallst);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        }

        return true;
        /*        
        
        try {
            if (!isRegistered) {
                final EntityKeyList lst = new EntityKeyList();
                lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
                publisher.register(lst, new PublishInteractionListener());

                isRegistered = true;
            }

            ObjectInstancePairList objIds = new ObjectInstancePairList();

            for (ParameterInstance parameter : parameters) {
                ObjectInstancePair objId = manager.getIdentityDefinition(parameter.name);  // Does the submitted name exists in the manager? 

                if (objId == null) {
                    ParameterDefinitionDetails pDef = new ParameterDefinitionDetails();

                    pDef.setDescription("This Parameter Definition was automatically generated by the Parameter service during the push of a parameter value.");

                    if (parameter.parameterValue.getRawValue() == null) {  // Well, let's then consider that it is a Double 
                        pDef.setRawType(Union.DOUBLE_TYPE_SHORT_FORM.byteValue());
                    } else {
                        pDef.setRawType(parameter.parameterValue.getRawValue().getTypeShortForm().byteValue()); // Check what is the type and stamp it 
                    }

                    pDef.setRawUnit(null);
                    pDef.setGenerationEnabled(false);
                    pDef.setReportInterval(new Duration(0));
                    pDef.setValidityExpression(null);
                    pDef.setConversion(null);

                    ParameterCreationRequestList pDefCreationReqs = new ParameterCreationRequestList();
                    ParameterCreationRequest pDefCreationReq = new ParameterCreationRequest(parameter.name, pDef);
                    pDefCreationReqs.add(pDefCreationReq);
                    try {
                        ObjectInstancePairList returnedObjIds = this.addParameter(pDefCreationReqs, null); // Enable the reporting for this Alert Definition 
                        objId = returnedObjIds.get(0);

                    } catch (MALInteractionException ex) {
                        Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (MALException ex) {
                        Logger.getLogger(AlertProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                objIds.add(objId);

            }

            ParameterValueList parameterValueList = new ParameterValueList();
            for (ParameterInstance parameter : parameters) {
                parameterValueList.add(parameter.parameterValue);
            }
            LongList relatedIds = new LongList();
			ObjectIdList sourceIds = new ObjectIdList();
            for (int i = 0; i < objIds.size(); i++) {
				ObjectInstancePair objId = objIds.get(i);
                relatedIds.add(objId.getObjDefInstanceId());
				sourceIds.add(parameters.get(i).source);
				
            }
            //requirement: 3.3.9.2.h, 3.3.7.2.e 
            if (timestamp == null) {
                timestamp = HelperTime.getTimestamp();
            }
            final LongList pValObjIds = manager.storeAndGenerateMultiplePValobjId(parameterValueList, 
                    relatedIds, sourceIds, connection.getConnectionDetails(), timestamp);

            final UpdateHeaderList hdrlst = new UpdateHeaderList();
            final ObjectIdList objectIdlst = new ObjectIdList();
            final ParameterValueList pVallst = new ParameterValueList();
            for (int i = 0; i < pValObjIds.size(); i++) {
                //  requirements: 3.3.7.2.a , 3.3.7.2.b , 3.3.7.2.c , 3.3.7.2.d 
                final EntityKey ekey = new EntityKey(new Identifier(manager.getName(objIds.get(i).getObjIdentityInstanceId()).toString()), 
                        objIds.get(i).getObjIdentityInstanceId(), objIds.get(i).getObjDefInstanceId(), pValObjIds.get(i));

                //requirement: 3.3.7.2.e : timestamp must be the same as for the creation of the ParameterValue
                hdrlst.add(new UpdateHeader(new Time(timestamp.getValue()), connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
                objectIdlst.add(parameters.get(i).source); // requirement: 3.3.7.2.g (3.3.5.2.f not necessary) 
                pVallst.add(parameters.get(i).parameterValue); // requirement: 3.3.7.2.h 
            }
            
            publisher.publish(hdrlst, objectIdlst, pVallst);
        } catch (IllegalArgumentException | MALException | MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING, "Pushed Parameter: Exception during publishing process on the provider {0}", ex);
            return false;
        }

        return true;
         */
    }

    /**
     * publishes a periodic parameter update for the given parameter
     *
     * @param identityId the id of the parameter identity
     */
    private void publishPeriodicParameterUpdate(final Long identityId) {
        try {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.INFO,
                    "start publishing periodic parameter update with identityId: {0} at {1}",
                    new Object[]{identityId, System.currentTimeMillis()});
            final ParameterValue parameterValue = manager.getParameterValue(identityId);
            //requirement: 3.3.4.j source object for periodic updates shall be null
            //requirement: 3.3.7.2.e the time of the publish update shall be the time of the parameter value creation
            Long timestamp = manager.getParameterValuesTimestamp(identityId);
            publishParameterUpdate(identityId, parameterValue, null, new Time(timestamp));
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.INFO,
                    "finished publishing periodic parameter update with identityId: {0} at {1}",
                    new Object[]{identityId, System.currentTimeMillis()});
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        } catch (MALException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during publishing process on the provider {0}", ex);
        }
    }

    /**
     * publishes a parameter update of an existing parameter-definition
     *
     * @param identityId id of the parameter-identity
     * @param name name of the parameter
     * @param parameterValue new value of the parameter
     * @param source the source that created the update. Null for periodic
     * updates
     * @param timestamp the timestamp that will be set as the creation-timestamp
     * of the in this method created ParameterValue-Object. Fruthermore this
     * timestamp will be taken as the timestamp for the publish-message
     * @throws MALException
     * @throws MALInteractionException
     * @throws IllegalArgumentException
     */
    private void publishParameterUpdate(final Long identityId, final ParameterValue parameterValue,
            final ObjectId source, final Time timestamp) throws MALException, MALInteractionException, IllegalArgumentException {
        if (!isRegistered) {
            final EntityKeyList lst = new EntityKeyList();
            lst.add(new EntityKey(new Identifier("*"), 0L, 0L, 0L));
            publisher.register(lst, new PublishInteractionListener());

            isRegistered = true;
        }
        FineTime time;
        if (timestamp == null) {
            time = HelperTime.getTimestamp();
        } else {
            time = new FineTime(timestamp.getValue());
        }

        //  requirement: 3.3.4.d, 3.3.7.2.e : publish and ParameterValue-Object-creation timestamp are the same
        final Long pValObjId = manager.storeAndGeneratePValobjId(identityId,
                parameterValue, source, connection.getConnectionDetails(), time);

        //  requirements: 3.3.7.2.a , 3.3.7.2.b , 3.3.7.2.c , 3.3.7.2.d
        final Identifier parameterName = manager.getName(identityId);
        final Long defId = manager.getDefinitionId(identityId);
        final EntityKey ekey = new EntityKey(parameterName, identityId, defId, pValObjId);

        final UpdateHeaderList hdrlst = new UpdateHeaderList();
        final ObjectIdList objectIdlst = new ObjectIdList();
        final ParameterValueList pVallst = new ParameterValueList();

        //  requirement: 3.3.7.2.e : publish and ParameterValue-Object-creation timestamp are the same
        hdrlst.add(new UpdateHeader(new Time(time.getValue()), connection.getConnectionDetails().getProviderURI(), UpdateType.UPDATE, ekey));
        objectIdlst.add(source); // requirement: 3.3.7.2.f (3.3.7.2.g not necessary)
        pVallst.add(parameterValue); // requirement: 3.3.7.2.h

        Logger.getLogger(ParameterProviderServiceImpl.class.getName()).log(Level.INFO,
                "Generated Parameter update for the Parameter IdentityId: {0}, DefinitionId: {1} (Name: {2}). New Value ID is: {3}",
                new Object[]{
                    identityId, defId, parameterName, pValObjId
                });
        publisher.publish(hdrlst, objectIdlst, pVallst);
    }

}
