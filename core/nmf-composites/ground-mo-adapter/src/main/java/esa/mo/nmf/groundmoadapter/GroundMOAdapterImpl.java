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
package esa.mo.nmf.groundmoadapter;

import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.ObjectInstanceIdGenerator;
import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperAttributes;
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
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.activitytracking.ActivityTrackingHelper;
import org.ccsds.moims.mo.com.activitytracking.structures.OperationActivity;
import org.ccsds.moims.mo.com.activitytracking.structures.OperationActivityList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.common.directory.structures.ProviderSummary;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.ActionHelper;
import org.ccsds.moims.mo.mc.action.consumer.ActionAdapter;
import org.ccsds.moims.mo.mc.action.consumer.ActionStub;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequestList;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetailsList;
import org.ccsds.moims.mo.mc.aggregation.consumer.AggregationAdapter;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationSetValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueList;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequest;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequestList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValueList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 * The implementation of the Ground MO Adapter.
 *
 * @author Cesar Coelho
 */
public class GroundMOAdapterImpl extends NMFConsumer implements SimpleCommandingInterface
{

  /* Logger */
  private static final Logger LOGGER = Logger.getLogger(GroundMOAdapterImpl.class.getName());

  private Subscription parameterSubscription = null;
  private Subscription aggregationSubscription = null;

  /**
   * The constructor of this class
   *
   * @param connection The connection details of the provider
   */
  public GroundMOAdapterImpl(final ConnectionConsumer connection)
  {
    super(connection);
    super.init();
  }

  /**
   * The constructor of this class
   *
   * @param providerDetails The Provider details. This object can be obtained from the Directory
   *                        service
   */
  public GroundMOAdapterImpl(final ProviderSummary providerDetails)
  {
    super(providerDetails);
    super.init();
  }

  @Override
  public void setParameter(final String parameterName, final Serializable content)
  {
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
      ObjectInstancePairList objIds = parameterService.listDefinition(parameters);

      if (objIds == null) {
        return;  // something went wrong... Connection problem?
      }

      ObjectInstancePair objId = objIds.get(0);

      // If the definition does not exist, then create it automatically for the user
      if (objId == null) {
        // Well, then let's create a new Parameter Definition and add it on the provider...
        ParameterDefinitionDetails parameterDefinition = new ParameterDefinitionDetails();
        parameterDefinition.setDescription(
            "This Definition was automatically generated by: " + GroundMOAdapterImpl.class.getName());

        if (rawValue instanceof Attribute) { // Is the parameter MAL type or something else?
          parameterDefinition.setRawType(((Attribute) midValue).getTypeShortForm().byteValue());
        } else {
          parameterDefinition.setRawType(HelperAttributes.SERIAL_OBJECT_RAW_TYPE);
        }

        parameterDefinition.setRawUnit(null);
        parameterDefinition.setGenerationEnabled(false);
        parameterDefinition.setReportInterval(new Duration(0));
        parameterDefinition.setValidityExpression(null);
        parameterDefinition.setConversion(null);

        ParameterCreationRequestList request = new ParameterCreationRequestList(1);
        request.add(new ParameterCreationRequest(new Identifier(parameterName), parameterDefinition));

        // Now, add the definition to the service provider
        objIds = parameterService.addParameter(request);
      }

      // Continues here...
      ParameterRawValueList raws = new ParameterRawValueList();
      ParameterRawValue raw = new ParameterRawValue(objIds.get(0).getObjIdentityInstanceId(),
          rawValue);
      raws.add(raw);

      // Ok, now, let's finally set the Value!
      parameterService.setValue(raws);
    } catch (MALInteractionException ex) {
      LOGGER.log(Level.SEVERE, "The parameter could not be set!", ex);
    } catch (MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
  }

  @Override
  public void addDataReceivedListener(final DataReceivedListener listener)
  {
    // Make the parameter adapter to call the receiveDataListener when there's a new object available
    class DataReceivedParameterAdapter extends ParameterAdapter
    {

      @Override
      public void monitorValueNotifyReceived(final MALMessageHeader msgHeader,
          final Identifier lIdentifier, final UpdateHeaderList lUpdateHeaderList,
          final ObjectIdList lObjectIdList, final ParameterValueList lParameterValueList,
          final Map qosp)
      {

        if (lParameterValueList.size() == lUpdateHeaderList.size()) {
          for (int i = 0; i < lUpdateHeaderList.size(); i++) {
            String parameterName = lUpdateHeaderList.get(i).getKey().getFirstSubKey().toString();
            Attribute parameterValue = lParameterValueList.get(i).getRawValue();
            Serializable object;

            // Is it a Blob?
            if (parameterValue instanceof Blob) {
              // If so, try to unserialize it
              try {
                object = HelperAttributes.blobAttribute2serialObject((Blob) parameterValue);
              } catch (IOException ex) {
                // Didn't work? Well, maybe it is just a normal Blob...
                object = (Serializable) HelperAttributes.attribute2JavaType(parameterValue);
              }
            } else {
              // Not a Blob?
              // Then make it a Java type if possible
              object = (Serializable) HelperAttributes.attribute2JavaType(parameterValue);
            }

            // Push the data to the user interface
            // Simple interface
            if (listener instanceof SimpleDataReceivedListener) {
              ((SimpleDataReceivedListener) listener).onDataReceived(parameterName, object);
            }

            // Complete interface
            if (listener instanceof CompleteDataReceivedListener) {
              ObjectId source = lObjectIdList.get(i);
              Time timestamp = lUpdateHeaderList.get(i).getTimestamp();

              ParameterInstance parameterInstance = new ParameterInstance(new Identifier(
                  parameterName),
                  lParameterValueList.get(i), source, timestamp);

              ((CompleteDataReceivedListener) listener).onDataReceived(parameterInstance);
            }
          }
        }
      }
    }

    // Make the aggregation adapter to call the receiveDataListener when there's a new object available
    class DataReceivedAggregationAdapter extends AggregationAdapter
    {

      @Override
      public void monitorValueNotifyReceived(final MALMessageHeader msgHeader,
          final Identifier lIdentifier, final UpdateHeaderList lUpdateHeaderList,
          final ObjectIdList lObjectIdList, final AggregationValueList lAggregationValueList,
          final Map qosp)
      {

        if (lAggregationValueList.size() == lUpdateHeaderList.size()) {
          for (int i = 0; i < lUpdateHeaderList.size(); i++) {

            if (listener instanceof SimpleAggregationReceivedListener) {
              List<ParameterInstance> parameterInstances = new LinkedList<ParameterInstance>();

              AggregationValue aggregationValue = lAggregationValueList.get(i);

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
              ObjectId source = lObjectIdList.get(i);
              Time timestamp = lUpdateHeaderList.get(i).getTimestamp();
              String aggregationName = lUpdateHeaderList.get(i).getKey().getFirstSubKey().toString();
              AggregationValue aggregationValue = lAggregationValueList.get(i);

              AggregationInstance aggregationInstance = new AggregationInstance(
                  new Identifier(aggregationName), aggregationValue, source, timestamp);

              ((CompleteAggregationReceivedListener) listener).onDataReceived(aggregationInstance);
            }
          }
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
      } catch (MALInteractionException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      } catch (MALException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      } catch (NullPointerException ex) {
        LOGGER.log(Level.SEVERE,
            "Null pointer exception when trying to access the Parameter service. "
            + "Check if the service consumer was initialized with a proper URI.",
            ex);
      }
    }
    if (listener instanceof CompleteAggregationReceivedListener || listener instanceof CompleteAggregationReceivedListener) {
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
  public Long invokeAction(final String actionName, final Serializable[] objects)
  {
    IdentifierList actionNames = new IdentifierList(1);
    actionNames.add(new Identifier(actionName));

    ActionStub actionService = super.getMCServices().getActionService().getActionStub();

    try {
      // Check if the action exists
      ObjectInstancePairList objIds = actionService.listDefinition(actionNames);

      if (objIds == null) {
        return null;  // something went wrong...
      }

      ObjectInstancePair objId = objIds.get(0);

      // If the definition does not exist, then create it automatically for the user
      if (objId == null) {
        // Well, then let's create a new Action Definition and add it on the provider...
        ActionDefinitionDetails actionDefinition = new ActionDefinitionDetails();
        actionDefinition.setDescription(
            "This Definition was automatically generated by: " + GroundMOAdapterImpl.class.getName());
        actionDefinition.setProgressStepCount(new UShort((short) 0));

        ArgumentDefinitionDetailsList argList = new ArgumentDefinitionDetailsList(objects.length);

        for (Serializable object : objects) {
          ArgumentDefinitionDetails argDef = new ArgumentDefinitionDetails();
          // If it is java type, then convert it to Attribute
          Object midValue = HelperAttributes.javaType2Attribute(object);
          Attribute rawValue;
          if (midValue instanceof Attribute) {
            // Is the parameter MAL type or something else?
            argDef.setRawType(((Attribute) midValue).getTypeShortForm().byteValue());
            rawValue = (Attribute) midValue;
          } else {
            try {
              // Well, if it is something else, then it will have to serialize it and put it inside a Blob
              rawValue = HelperAttributes.serialObject2blobAttribute(object);
              argDef.setRawType(HelperAttributes.SERIAL_OBJECT_RAW_TYPE);
            } catch (IOException ex) {
              LOGGER.log(Level.SEVERE, null, ex);
              return null;
            }
          }
          argDef.setRawUnit(null);
          argDef.setConditionalConversions(null);
          argDef.setConvertedType(null);
          argDef.setConvertedUnit(null);
          argDef.setRawType(null);
          argDef.setArgId(null);
          argList.add(argDef);
        }

        actionDefinition.setArguments(argList); // Change this...

        ActionCreationRequestList acrl = new ActionCreationRequestList();
        acrl.add(new ActionCreationRequest(new Identifier(actionName), actionDefinition));

        objIds = actionService.addAction(acrl);
        objId = objIds.get(0);
      }

      // Fill-in the Action Instance object
      ActionInstanceDetails action = new ActionInstanceDetails();

      action.setDefInstId(objId.getObjDefInstanceId());
      action.setStageStartedRequired(false);
      action.setStageProgressRequired(false);
      action.setStageCompletedRequired(false);

      AttributeValueList argValues = new AttributeValueList();

      // Fill-in the argument values
      for (Serializable object : objects) {
        AttributeValue argValue = new AttributeValue();
        // If it is java type, then convert it to Attribute
        Object midValue = HelperAttributes.javaType2Attribute(object);
        Attribute rawValue;
        if (midValue instanceof Attribute) {
          // Is the parameter MAL type or something else?
          rawValue = (Attribute) midValue;
          argValue.setValue(rawValue);
        } else {
          try {
            // Well, if it is something else, then it will have to
            // serialize it and put it inside a Blob
            rawValue = HelperAttributes.serialObject2blobAttribute(object);
            argValue.setValue(rawValue);
          } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
          }
        }
        argValues.add(argValue);
      }

      action.setArgumentValues(argValues);
      action.setArgumentIds(null);
      action.setIsRawValue(null);

      // Use action service to submit the action
      long actionID = ObjectInstanceIdGenerator.getInstance().generateObjectInstanceId();

      actionService.submitAction(actionID, action);

      return actionID;

    } catch (MALInteractionException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    } catch (MALException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }
    return null;
  }

  /**
   * Closes the service consumer connections
   *
   */
  public void closeConnections()
  {
    // Unregister the consumer from the broker
    if (this.parameterSubscription != null) {
      try {
        IdentifierList idList = new IdentifierList();
        idList.add(this.parameterSubscription.getSubscriptionId());

        super.getMCServices().getParameterService().getParameterStub().asyncMonitorValueDeregister(
            idList, new ParameterAdapter()
        {
          @Override
          public void monitorValueDeregisterAckReceived(MALMessageHeader msgHeader,
              Map qosProperties)
          {
            parameterSubscription = null;
          }

        }
        );
      } catch (MALInteractionException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      } catch (MALException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      }

    }

    // Unregister the consumer from the broker
    if (this.aggregationSubscription != null) {
      try {
        IdentifierList idList = new IdentifierList();
        idList.add(this.aggregationSubscription.getSubscriptionId());

        super.getMCServices().getAggregationService().getAggregationStub().asyncMonitorValueDeregister(
            idList, new AggregationAdapter()
        {
          @Override
          public void monitorValueDeregisterAckReceived(MALMessageHeader msgHeader,
              Map qosProperties)
          {
            aggregationSubscription = null;
          }
        }
        );
      } catch (MALInteractionException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      } catch (MALException ex) {
        LOGGER.log(Level.SEVERE, null, ex);
      }

    }

    if (this.comServices != null) {
      this.comServices.closeConnections();
    }

    if (this.mcServices != null) {
      this.mcServices.closeConnections();
    }

    if (this.commonServices != null) {
      this.commonServices.closeConnections();
    }

    if (this.platformServices != null) {
      this.platformServices.closeConnections();
    }

    if (this.smServices != null) {
      this.smServices.getHeartbeatService().stopListening();
      this.smServices.closeConnections();
    }

  }

  @Override
  public Long invokeAction(Long defInstId, AttributeValueList argumentValues) throws NMFException
  {
    Long instanceObjId = null;

    // Create an ActionInstanceDetails object for the invocation of this action
    Boolean stageStartedRequired = true;
    Boolean stageProgressRequired = true;
    Boolean stageCompletedRequired = true;
    IdentifierList argumentIds = null;

    try {
      ActionInstanceDetails instanceDetails =
          new ActionInstanceDetails(
              defInstId,
              stageStartedRequired,
              stageProgressRequired,
              stageCompletedRequired,
              argumentValues,
              argumentIds,
              null);

      // Store the Action Instance in the Archive and get an
      // object instance identifier to use during the submit
      Long related = defInstId;
      ObjectId source = null; // TBD - should be the OperationActivity object
      SingleConnectionDetails actionConnection =
          super.getMCServices().getActionService().getConnectionDetails();

      ArchiveDetailsList archiveDetailsListActionInstance =
          HelperArchive.generateArchiveDetailsList(
              related,
              source,
              actionConnection.getProviderURI());

      boolean returnObjInstIds = true;
      ActionInstanceDetailsList instanceDetailsList = new ActionInstanceDetailsList();
      instanceDetailsList.add(instanceDetails);

      LongList objIdActionInstances =
          super.getCOMServices().getArchiveService().getArchiveStub().store(
              returnObjInstIds,
              ActionHelper.ACTIONINSTANCE_OBJECT_TYPE,
              actionConnection.getDomain(),
              archiveDetailsListActionInstance,
              instanceDetailsList);

      if (objIdActionInstances.size() == 1) {
        instanceObjId = objIdActionInstances.get(0);
        archiveDetailsListActionInstance.get(0).setInstId(instanceObjId);
      } else {
        throw new NMFException("Failed to store new Action Instance in COM Archive");
      }

      // Submit the action instance
      // WORKAROUND: submit the action asynchronously so that we can find
      // out the Transaction ID of the MAL message and include it in the
      // corresponding OperationActivity
      // actionService.submitAction(instanceObjId, instanceDetails);
      MALMessage msg = super.getMCServices().getActionService().getActionStub().asyncSubmitAction(
          instanceObjId,
          instanceDetails,
          new ActionAdapter()
      {
      }
      );

      // Store the corresponding OperationActivity object instance in the
      // Archive and publish its release
      OperationActivityList opActivityList = new OperationActivityList();
      opActivityList.add(new OperationActivity(msg.getHeader().getInteractionType()));

      related = null;
      source = null;          // the object that caused this to be created

      ArchiveDetailsList archiveDetailsListOp = HelperArchive.generateArchiveDetailsList(
          related,
          source,
          actionConnection.getProviderURI());

      Long transId = msg.getHeader().getTransactionId();
      archiveDetailsListOp.get(0).setInstId(transId); // requirement: 3.5.2.4

      try {
        returnObjInstIds = false;
        super.getCOMServices().getArchiveService().getArchiveStub().store(
            returnObjInstIds,
            ActivityTrackingHelper.OPERATIONACTIVITY_OBJECT_TYPE,
            actionConnection.getDomain(),
            archiveDetailsListOp,
            opActivityList);
      } catch (MALInteractionException ex) {
        // A duplicate might happen if the consumer stored the Operation Activity object
        if (ex.getStandardError().getErrorNumber().getValue() != COMHelper.DUPLICATE_ERROR_NUMBER.getValue()) {
          throw new NMFException("The storing of the Operation Activity failed. (1)", ex);
        } else {
          // It's a Duplicate error, the object already exists...
          // Do nothing!
        }
      } catch (MALException ex) {
        throw new NMFException("The storing of the Operation Activity failed. (2)", ex);
      }

      ObjectId source2 = new ObjectId(ActivityTrackingHelper.OPERATIONACTIVITY_OBJECT_TYPE,
          new ObjectKey(actionConnection.getDomain(), transId));
      ObjectDetails details = new ObjectDetails(defInstId, source2);
      archiveDetailsListActionInstance.get(0).setDetails(details);

      super.getCOMServices().getArchiveService().getArchiveStub().update(
          ActionHelper.ACTIONINSTANCE_OBJECT_TYPE,
          actionConnection.getDomain(),
          archiveDetailsListActionInstance,
          instanceDetailsList);
    } catch (MALInteractionException ex) {
      throw new NMFException("Failed to execute Action " + defInstId, ex);
    } catch (MALException ex) {
      throw new NMFException("Failed to execute Action " + defInstId, ex);
    } catch (NMFException ex) {
      throw new NMFException("Failed to execute Action " + defInstId, ex);
    }

    return instanceObjId;
  }

}
