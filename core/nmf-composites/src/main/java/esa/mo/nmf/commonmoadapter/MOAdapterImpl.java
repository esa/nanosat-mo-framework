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
package esa.mo.nmf.commonmoadapter;

import esa.mo.mc.impl.provider.AggregationInstance;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nmf.NMFConsumer;
import esa.mo.nmf.NMFException;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConnectionConsumer;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.consumer.ActionAdapter;
import org.ccsds.moims.mo.mc.action.consumer.ActionStub;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationAdapter;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * The implementation of the base class of the MO Adapters.
 */
public class MOAdapterImpl extends NMFConsumer implements SimpleCommandingInterface {

    /* Logger */
    private static final Logger LOGGER = Logger.getLogger(MOAdapterImpl.class.getName());

    private Subscription parameterSubscription = null;
    private Subscription aggregationSubscription = null;

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     */
    public MOAdapterImpl(final ConnectionConsumer connection) {
        this(connection, null, null);
    }

    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public MOAdapterImpl(final ConnectionConsumer connection, final Blob authenticationId, final String localNamePrefix) {
        super(connection, authenticationId, localNamePrefix);
        super.init();
    }

    /**
     * The constructor of this class
     *
     * @param providerDetails The Provider details. This object can be obtained
     * from the Directory service.
     */
    public MOAdapterImpl(final ProviderSummary providerDetails) {
        this(providerDetails, null, null);
    }

    /**
     * The constructor of this class
     *
     * @param providerDetails The Provider details. This object can be obtained
     * from the Directory service.
     * @param authenticationId authenticationId of the logged in user
     * @param localNamePrefix the prefix for the local name of the consumer
     */
    public MOAdapterImpl(final ProviderSummary providerDetails,
            final Blob authenticationId, final String localNamePrefix) {
        super(providerDetails, authenticationId, localNamePrefix);
        super.init();
    }

    @Override
    public void setParameter(final String parameterName, final Serializable content) {
        // Check if the parameter exists
        IdentifierList parameters = new IdentifierList(1);
        parameters.add(new Identifier(parameterName));

        // If it is java type, then convert it to Attribute
        Object midValue = HelperAttributes.javaType2Attribute(content);
        Attribute rawValue;

        if (midValue instanceof Attribute) { // Is the parameter MAL type or something else?
            rawValue = (Attribute) midValue;
        } else {
            try {
                // Well, if it is something else, then it will have to serialize it and put it inside a Blob
                rawValue = HelperAttributes.serialObject2blobAttribute(content);
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
                return;
            }
        }

        ParameterStub parameterService = super.getMCServices().getParameterService().getParameterStub();

        try {
            LongList ids = parameterService.listDefinition(parameters);

            if (ids == null) {
                LOGGER.log(Level.SEVERE, "The Parameter was not found: " + parameterName);
                return;  // something went wrong... Connection problem?
            }

            Long id = ids.get(0);

            // If the definition does not exist, then create it automatically for the user
            if (id == null) {
                LOGGER.log(Level.SEVERE, "The Parameter was not found: " + parameterName);
                return;
                /*
                // Well, then let's create a new Parameter Definition and add it on the provider...
                Byte rawType = HelperAttributes.SERIAL_OBJECT_RAW_TYPE;

                if (rawValue instanceof Attribute) { // Is the parameter MAL type or something else?
                    rawType = ((Integer) ((Attribute) midValue).getTypeId().getSFP()).byteValue();
                }

                ParameterDefinition parameterDefinition = new ParameterDefinition(
                        "This Definition was automatically generated by: " + CommonMOAdapterImpl.class.getName(),
                        rawType,
                        null,
                        false,
                        new Duration(0),
                        null,
                        null);

                ParameterCreationRequestList request = new ParameterCreationRequestList(1);
                request.add(new ParameterCreationRequest(new Identifier(parameterName), parameterDefinition));

                // Now, add the definition to the service provider
                objIds = parameterService.addParameter(request);
                 */
            }

            // Continues here...
            ParameterRawValueList raws = new ParameterRawValueList();
            rawValue = (content == null) ? null : rawValue;
            raws.add(new ParameterRawValue(ids.get(0), rawValue));

            // Ok, now, let's finally set the Value!
            parameterService.setValue(raws);
        } catch (MALInteractionException ex) {
            LOGGER.log(Level.SEVERE, "The parameter could not be set!", ex);
        } catch (MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void addDataReceivedListener(final DataReceivedListener listener) {
        // Make the parameter adapter to call the receiveDataListener when there's a new object available
        class DataReceivedParameterAdapter extends ParameterAdapter {

            @Override
            public void monitorValueNotifyReceived(org.ccsds.moims.mo.mal.transport.MALMessageHeader msgHeader,
                    org.ccsds.moims.mo.mal.structures.Identifier subscriptionId,
                    org.ccsds.moims.mo.mal.structures.UpdateHeader updateHeader,
                    org.ccsds.moims.mo.com.structures.ObjectKey objKey,
                    org.ccsds.moims.mo.mc.structures.ParameterValue newValue,
                    java.util.Map qosProperties) {
                String parameterName = updateHeader.getKeyValues().get(0).getValue().toString();
                Attribute parameterValue = newValue.getRawValue();
                Serializable object;

                // Is it a Blob?
                if (parameterValue instanceof Blob) {
                    // If so, try to unserialize it
                    try {
                        object = HelperAttributes.blobAttribute2serialObject((Blob) parameterValue);
                    } catch (IOException ex) {
                        // Didn't work? Well, maybe it is just a normal Blob...
                        object = (Serializable) Attribute.attribute2JavaType(parameterValue);
                    }
                } else {
                    // Not a Blob?
                    // Then make it a Java type if possible
                    object = (Serializable) Attribute.attribute2JavaType(parameterValue);
                }

                // Push the data to the user interface
                // Simple interface
                if (listener instanceof SimpleDataReceivedListener) {
                    ((SimpleDataReceivedListener) listener).onDataReceived(parameterName, object);
                }

                // Complete interface
                if (listener instanceof CompleteDataReceivedListener) {
                    Time timestamp = Time.now();

                    ParameterInstance parameterInstance = new ParameterInstance(new Identifier(parameterName),
                            newValue, objKey, timestamp);

                    ((CompleteDataReceivedListener) listener).onDataReceived(parameterInstance);
                }
            }
        }

        // Make the aggregation adapter to call the receiveDataListener when there's a new object available
        class DataReceivedAggregationAdapter extends AggregationAdapter {

            @Override
            public void monitorValueNotifyReceived(MALMessageHeader msgHeader,
                    org.ccsds.moims.mo.mal.structures.Identifier subscriptionId,
                    org.ccsds.moims.mo.mal.structures.UpdateHeader updateHeader,
                    org.ccsds.moims.mo.com.structures.ObjectKey objKey,
                    org.ccsds.moims.mo.mc.structures.AggregationValue newValue,
                    java.util.Map qosProperties) {
                if (listener instanceof SimpleAggregationReceivedListener) {
                    List<ParameterInstance> parameterInstances = new LinkedList<>();

                    AggregationValue aggregationValue = newValue;

                    for (AggregationSetValue aggregationSetValue : aggregationValue.getParameterSetValues()) {
                        for (AggregationParameterValue aggregationParamValue : aggregationSetValue.getValues()) {
                            Long paramDefInstId = aggregationParamValue.getParamDefInstId();
                            Attribute parameterValue = aggregationParamValue.getValue().getRawValue();

                            // TBD, not sure what to do with this now...
                        }
                    }

                    ((SimpleAggregationReceivedListener) listener).onDataReceived(parameterInstances);
                }

                if (listener instanceof CompleteAggregationReceivedListener) {
                    ObjectKey source = objKey;
                    Time timestamp = Time.now();
                    String aggregationName = updateHeader.getKeyValues().get(0).getValue().toString();
                    AggregationValue aggregationValue = newValue;

                    AggregationInstance aggregationInstance = new AggregationInstance(new Identifier(
                            aggregationName), aggregationValue, source, timestamp);

                    ((CompleteAggregationReceivedListener) listener).onDataReceived(aggregationInstance);
                }
            }
        }

        if (listener instanceof SimpleDataReceivedListener || listener instanceof CompleteDataReceivedListener) {
            // Subscribes to ALL Parameters
            this.parameterSubscription = ConnectionConsumer.subscriptionWildcardRandom();

            try {
                // Register for pub-sub of all parameters
                super.getMCServices().getParameterService().getParameterStub().monitorValueRegister(
                        this.parameterSubscription, new DataReceivedParameterAdapter());
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                LOGGER.log(Level.SEVERE,
                        "Null pointer exception when trying to access the Parameter service. "
                        + "Check if the service consumer was initialized with a proper URI.",
                        ex);
            }
        }
        if (listener instanceof SimpleAggregationReceivedListener || listener instanceof CompleteAggregationReceivedListener) {
            // Subscribes to ALL Aggregations
            this.aggregationSubscription = ConnectionConsumer.subscriptionWildcardRandom();

            try {
                // Register for pub-sub of all aggregations
                super.getMCServices().getAggregationService().getAggregationStub().monitorValueRegister(
                        this.aggregationSubscription, new DataReceivedAggregationAdapter());
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            } catch (NullPointerException ex) {
                LOGGER.log(Level.SEVERE,
                        "Null pointer exception when trying to access the Aggregation service. "
                        + "Check if the service consumer was initialized with a proper URI.",
                        ex);
            }
        }
    }

    @Override
    public Long launchAction(final String actionName, final Serializable[] objects) {
        Long actionID = null;
        IdentifierList actionNames = new IdentifierList(1);
        actionNames.add(new Identifier(actionName));

        ActionStub actionService = super.getMCServices().getActionService().getActionStub();

        try {
            // Check if the action exists
            LongList ids = actionService.listDefinition(actionNames);

            if (ids == null) {
                return null;  // something went wrong...
            }

            Long id = ids.get(0);

            // If the definition does not exist, then create it automatically for the user
            if (id == null) {
                LOGGER.log(Level.SEVERE, "The Action was not found: " + actionName);
                return -1L;
                /*
                ArgumentDefinitionList argList = new ArgumentDefinitionList(objects.length);

                for (Serializable object : objects) {
                    // If it is java type, then convert it to Attribute
                    Object midValue = HelperAttributes.javaType2Attribute(object);
                    Byte rawType;
                    Attribute rawValue;
                    if (midValue instanceof Attribute) {
                        // Is the parameter MAL type or something else?
                        rawType = ((Integer) ((Attribute) midValue).getTypeId().getSFP()).byteValue();
                        rawValue = (Attribute) midValue;
                    } else {
                        try {
                            // Well, if it is something else, then it will have to serialize it and put it inside a Blob
                            rawValue = HelperAttributes.serialObject2blobAttribute(object);
                            rawType = HelperAttributes.SERIAL_OBJECT_RAW_TYPE;
                        } catch (IOException ex) {
                            LOGGER.log(Level.SEVERE, null, ex);
                            return null;
                        }
                    }

                    argList.add(new ArgumentDefinition(null, rawType));
                }

                // Well, then let's create a new Action Definition and add it on the provider...
                ActionDefinition actionDefinition = new ActionDefinition(
                        "This Definition was automatically generated by: " + CommonMOAdapterImpl.class.getName(),
                        null,
                        new UShort((short) 0),
                        argList);

                ActionCreationRequestList acrl = new ActionCreationRequestList();
                acrl.add(new ActionCreationRequest(new Identifier(actionName), actionDefinition));

                objIds = actionService.addAction(acrl);
                objId = objIds.get(0);
                 */
            }

            AttributeValueList argValues = new AttributeValueList();

            // Fill-in the argument values
            for (Serializable object : objects) {
                // If it is java type, then convert it to Attribute
                Object midValue = HelperAttributes.javaType2Attribute(object);
                Attribute rawValue;

                if (midValue instanceof Attribute) {
                    // Is the parameter MAL type or something else?
                    rawValue = (Attribute) midValue;
                } else {
                    try {
                        // Well, if it is something else, then it will have to
                        // serialize it and put it inside a Blob
                        rawValue = HelperAttributes.serialObject2blobAttribute(object);
                    } catch (IOException ex) {
                        LOGGER.log(Level.SEVERE, null, ex);
                        return null;
                    }
                }
                argValues.add(new AttributeValue(rawValue));
            }

            actionID = launchAction(id, argValues);
        } catch (MALInteractionException | NMFException | MALException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return actionID;
    }

    @Override
    public Long launchAction(Long definitionId, AttributeValueList argumentValues) throws NMFException {
        return launchAction(definitionId, argumentValues, new ActionAdapter() {
        });
    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        // Unregister the consumer from the broker
        if (this.parameterSubscription != null) {
            try {
                IdentifierList idList = new IdentifierList();
                idList.add(this.parameterSubscription.getSubscriptionId());

                super.getMCServices().getParameterService().getParameterStub().asyncMonitorValueDeregister(idList, new ParameterAdapter() {
                    @Override
                    public void monitorValueDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
                        parameterSubscription = null;
                    }
                });
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }

        // Unregister the consumer from the broker
        if (this.aggregationSubscription != null) {
            try {
                IdentifierList idList = new IdentifierList();
                idList.add(this.aggregationSubscription.getSubscriptionId());

                super.getMCServices().getAggregationService().getAggregationStub().asyncMonitorValueDeregister(
                        idList, new AggregationAdapter() {
                    @Override
                    public void monitorValueDeregisterAckReceived(MALMessageHeader msgHeader,
                            Map qosProperties) {
                        aggregationSubscription = null;
                    }
                }
                );
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

        }

        if (this.comServices != null) {
            this.comServices.closeConnections();
        }

        if (this.mcServices != null) {
            this.mcServices.closeConnections();
        }

        if (this.platformServices != null) {
            this.platformServices.closeConnections();
        }

        if (this.smServices != null) {
            if (this.smServices.getHeartbeatService() != null) {
                this.smServices.getHeartbeatService().stopListening();
            }
            this.smServices.closeConnections();
        }

    }

    @Override
    public Long launchAction(Long definitionId, AttributeValueList argumentValues,
            ActionAdapter actionAdapter) throws NMFException {
        ExecutionRequest execReq = new ExecutionRequest(
                definitionId, argumentValues, null);
        try {
            Long executionId = super.getMCServices().getActionService().getActionStub()
                    .executeAction(execReq);
            if (actionAdapter != null) {
                actionAdapter.executeActionResponseReceived(null, executionId, null);
            }
            return executionId;
        } catch (MALInteractionException ex) {
            if (actionAdapter != null) {
                actionAdapter.executeActionErrorReceived(null, ex.getStandardError(), null);
            }
            throw new NMFException("Failed to execute Action " + definitionId, ex);
        } catch (MALException ex) {
            throw new NMFException("Failed to execute Action " + definitionId, ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void toggleParametersGeneration(List<String> parameterNames,
            boolean enable) throws NMFException {
        // Nothing to do in those cases
        if (parameterNames == null) {
            LOGGER.log(Level.WARNING, "Provided parameterNames list is null");
            return;
        }
        if (parameterNames.size() <= 0) {
            LOGGER.log(Level.WARNING, "Provided parameterNames list is empty");
            return;
        }

        ParameterStub parameterService = super.getMCServices().getParameterService().getParameterStub();

        // Create identifier list and parameters names string for error reporting
        final StringBuilder paramNames = new StringBuilder();
        IdentifierList parameters = new IdentifierList(1);
        for (String parameterName : parameterNames) {
            parameters.add(new Identifier(parameterName));
            paramNames.append(parameterName).append(",");
        }

        try {
            // Query parameters IDs
            LongList ids = parameterService.listDefinition(parameters);

            if (ids == null) {
                throw new NMFException(String.format(
                        "Error while toggling parameters generation, couldn't get parameters instance IDs (unknown error) for parameters names: %s",
                        paramNames));
            }

            // Check how many we got back
            if (ids.size() < parameterNames.size()) {
                if (ids.size() <= 0) {
                    throw new NMFException(String.format(
                            "Error while toggling parameters generation, 0 parameters instance IDs found for parameters names: %s",
                            paramNames));
                } else {
                    LOGGER.log(Level.WARNING, String.format(
                            "Couldn't get some parameters instance IDs, for parameters names: %s",
                            paramNames));
                }
            }

            // toggle their generation
            parameterService.enableReporting(enable, ids);
        } catch (MALInteractionException | MALException e) {
            throw new NMFException("Error while toggling parameters generation", e);
        }
    }
}
