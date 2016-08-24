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
package esa.mo.nanosatmoframework.groundmoadapter.consumer;

import esa.mo.helpertools.connections.ConnectionConsumer;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.provider.ParameterInstance;
import esa.mo.nanosatmoframework.groundmoadapter.listeners.CompleteDataReceivedListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import esa.mo.nanosatmoframework.groundmoadapter.interfaces.DataReceivedListener;
import esa.mo.nanosatmoframework.groundmoadapter.interfaces.SimpleCommandingInterface;
import esa.mo.nanosatmoframework.groundmoadapter.listeners.SimpleDataReceivedListener;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
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
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetails;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.action.structures.ActionInstanceDetails;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetails;
import org.ccsds.moims.mo.mc.structures.ArgumentDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.Severity;

/**
 * A Consumer of MO services composed by COM, M&C and Platform services.
 * Implements the SimpleCommandingInterface that permits an external software
 * entity to send data (parameters or serialized objects) to the provider, add
 * a DataReceivedListener to receive data and send actions to the provider.
 * It extends the MOServicesConsumer class in order make available the pure MO 
 * interfaces.
 * 
 * @author Cesar Coelho
 */
public class GroundMOAdapter extends MOServicesConsumer implements SimpleCommandingInterface {

    private Subscription subscription = null;
    
    /**
     * The constructor of this class
     *
     * @param connection The connection details of the provider
     */
    public GroundMOAdapter(ConnectionConsumer connection) {
        super(connection);
    }

    /**
     * The constructor of this class
     *
     * @param providerDetails The Provider details. This object can be obtained 
     * from the Directory service
     */
    public GroundMOAdapter(ProviderSummary providerDetails) {
        super(providerDetails);
    }

    @Override
    public void setParameter(String parameterName, Serializable content) {

        // Check if the parameter exists
        IdentifierList parameters = new IdentifierList();
        parameters.add(new Identifier(parameterName));

        // If it is java type, then convert it to Attribute
        Object midValue = HelperAttributes.javaType2Attribute(content);
        Attribute rawValue = null;

        if (midValue instanceof Attribute) { // Is the parameter MAL type or something else?
            rawValue = (Attribute) midValue;
        } else {
            try {
                // Well, if it is something else, then it will have to serialize it and put it inside a Blob
                rawValue = HelperAttributes.serialObject2blobAttribute(content);
            } catch (IOException ex) {
                Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
        }

        try {
            LongList objIds = super.getMCServices().getParameterService().getParameterStub().listDefinition(parameters);

            if (objIds == null) {
                return;  // something went wrong... Connection problem?
            }

            Long objId = objIds.get(0);

            // If the definition does not exist, then create it automatically for the user
            if (objId == null) {
                // Well, then let's create a new Parameter Definition and add it on the provider...
                ParameterDefinitionDetails parameterDefinition = new ParameterDefinitionDetails();
                parameterDefinition.setName(new Identifier(parameterName));
                parameterDefinition.setDescription("This Definition was automatically generated by: " + GroundMOAdapter.class.getName());

                if (rawValue instanceof Attribute) { // Is the parameter MAL type or something else?
                    parameterDefinition.setRawType(((Attribute) midValue).getTypeShortForm().byteValue());
                } else {
                    parameterDefinition.setRawType(HelperAttributes.SERIAL_OBJECT_RAW_TYPE);
                }

                parameterDefinition.setRawUnit(null);
                parameterDefinition.setGenerationEnabled(false);
                parameterDefinition.setUpdateInterval(new Duration(0));
                parameterDefinition.setValidityExpression(null);
                parameterDefinition.setConversion(null);

                ParameterDefinitionDetailsList pDefs = new ParameterDefinitionDetailsList();
                pDefs.add(parameterDefinition);

                // Now, add the definition to the service provider
                objIds = super.getMCServices().getParameterService().getParameterStub().addDefinition(pDefs);

            }

            // Continues here...
            ParameterValue pVal = new ParameterValue();
            pVal.setValid(true);
            pVal.setInvalidSubState(new UOctet((short) 0));
            pVal.setConvertedValue(null);
            pVal.setRawValue(rawValue);

            ParameterValueList pVals = new ParameterValueList();
            pVals.add(pVal);

            // Ok, now, let's finally set the Value!
            super.getMCServices().getParameterService().getParameterStub().setValue(objIds, pVals);

        } catch (MALInteractionException ex) {
            Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void addDataReceivedListener(final DataReceivedListener dataReceivedListener) {

        // Make the parameter adapter to call the receiveDataListener when there's a new object available
        class DataReceivedParameterAdapter extends ParameterAdapter {

            @Override
            public void monitorValueNotifyReceived(final MALMessageHeader msgHeader,
                    final Identifier lIdentifier, final UpdateHeaderList lUpdateHeaderList,
                    final ObjectIdList lObjectIdList, final ParameterValueList lParameterValueList,
                    final Map qosp) {

                if (lParameterValueList.size() == lUpdateHeaderList.size()) {
                    for (int i = 0; i < lUpdateHeaderList.size(); i++) {
                        String parameterName = lUpdateHeaderList.get(i).getKey().getFirstSubKey().toString();
                        Attribute parameterValue = lParameterValueList.get(i).getRawValue();
                        Serializable object = null;

                        // Is it a Blob?
                        if (parameterValue instanceof Blob) {
                            // If so, try to unserialize it
                            try {
                                object = HelperAttributes.blobAttribute2serialObject((Blob) parameterValue);
                            } catch (IOException ex) {
                                // Didn't work? Well, maybe it is just a normal Blob...
                                object = (Serializable) HelperAttributes.attribute2JavaType(parameterValue);

//                                Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            // Not a Blob?
                            // Then make it a Java type if possible
                            object = (Serializable) HelperAttributes.attribute2JavaType(parameterValue);
                        }

                        // Push the data to the user interface
                        
                        // Simple interface
                        if (dataReceivedListener instanceof SimpleDataReceivedListener){
                            ((SimpleDataReceivedListener) dataReceivedListener).onDataReceived(parameterName, object);
                        }
                        
                        // Complete interface
                        if (dataReceivedListener instanceof CompleteDataReceivedListener){
                            ObjectId source = lObjectIdList.get(i);
                            Time timestamp = lUpdateHeaderList.get(i).getTimestamp();
                            
                            ParameterInstance parameterInstance = new ParameterInstance(new Identifier(parameterName), lParameterValueList.get(i), source, timestamp);
                            
                            ((CompleteDataReceivedListener) dataReceivedListener).onDataReceived(parameterInstance);
                        }
                        
                    }
                }
            }
        }

        // Subscribes to ALL Parameters
        this.subscription = ConnectionConsumer.subscriptionWildcardRandom();

        try {
            // Register for pub-sub of all parameters
            super.getMCServices().getParameterService().getParameterStub().monitorValueRegister(this.subscription, new DataReceivedParameterAdapter());

        } catch (MALInteractionException ex) {
            Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void invokeAction(String actionName, Serializable[] objects) {

        IdentifierList actionNames = new IdentifierList();
        actionNames.add(new Identifier(actionName));

        try {
            // Check if the action exists
            LongList objIds = super.getMCServices().getActionService().getActionStub().listDefinition(actionNames);

            if (objIds == null) {
                return;  // something went wrong...
            }

            Long objId = objIds.get(0);

            // If the definition does not exist, then create it automatically for the user
            if (objId == null) {
                // Well, then let's create a new Parameter Definition and add it on the provider...
                ActionDefinitionDetails actionDefinition = new ActionDefinitionDetails();
                actionDefinition.setName(new Identifier(actionName));
                actionDefinition.setDescription("This Definition was automatically generated by: " + GroundMOAdapter.class.getName());
                actionDefinition.setSeverity(Severity.INFORMATIONAL);
                actionDefinition.setProgressStepCount(new UShort((short) 0));

                ArgumentDefinitionDetailsList argList = new ArgumentDefinitionDetailsList();

                for (int i = 0; i < objects.length; i++) {
                    ArgumentDefinitionDetails argDef = new ArgumentDefinitionDetails();

                    // If it is java type, then convert it to Attribute
                    Object midValue = HelperAttributes.javaType2Attribute(objects[i]);
                    Attribute rawValue = null;

                    if (midValue instanceof Attribute) { // Is the parameter MAL type or something else?
                        argDef.setRawType(((Attribute) midValue).getTypeShortForm().byteValue());
                        rawValue = (Attribute) midValue;

                    } else {
                        try {
                            // Well, if it is something else, then it will have to serialize it and put it inside a Blob
                            rawValue = HelperAttributes.serialObject2blobAttribute(objects[i]);
//                            argDef.setRawType(rawValue.getTypeShortForm().byteValue());
                            argDef.setRawType(HelperAttributes.SERIAL_OBJECT_RAW_TYPE);

                        } catch (IOException ex) {
                            Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
                            return;
                        }
                    }

                    argDef.setRawUnit(null);
                    argDef.setConversionCondition(null);
                    argDef.setConvertedType(null);
                    argDef.setConvertedUnit(null);
                    argList.add(argDef);
                }

                actionDefinition.setArguments(argList); // Change this...
                actionDefinition.setArgumentIds(null);

                ActionDefinitionDetailsList actionDefinitions = new ActionDefinitionDetailsList();
                actionDefinitions.add(actionDefinition);
                objIds = super.getMCServices().getActionService().getActionStub().addDefinition(actionDefinitions);
                objId = objIds.get(0);

            }

            // Fill-in the Action Instance object
            ActionInstanceDetails action = new ActionInstanceDetails();

            action.setDefInstId(objId);
            action.setStageStartedRequired(false);
            action.setStageProgressRequired(false);
            action.setStageCompletedRequired(false);

            AttributeValueList argValues = new AttributeValueList();

            // Fill-in the argument values
            for (int i = 0; i < objects.length; i++) {
                AttributeValue argValue = new AttributeValue();

                // If it is java type, then convert it to Attribute
                Object midValue = HelperAttributes.javaType2Attribute(objects[i]);
                Attribute rawValue = null;

                if (midValue instanceof Attribute) { // Is the parameter MAL type or something else?
                    rawValue = (Attribute) midValue;
                    argValue.setValue(rawValue);
                } else {
                    try {
                        // Well, if it is something else, then it will have to serialize it and put it inside a Blob
                        rawValue = HelperAttributes.serialObject2blobAttribute(objects[i]);
                        argValue.setValue(rawValue);
                    } catch (IOException ex) {
                        Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
                        return;
                    }
                }

                argValues.add(argValue);
            }

            action.setArgumentValues(argValues);
            action.setArgumentIds(null);
            action.setIsRawValue(null);

            // Use action service to submit the action
            super.getMCServices().getActionService().getActionStub().submitAction(objId, action);

        } catch (MALInteractionException ex) {
            Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALException ex) {
            Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * Closes the service consumer connections
     *
     */
    public void closeConnections() {
        
        // Unregister the consumer from the broker
        if (this.subscription != null){
            IdentifierList idList = new IdentifierList();
            idList.add(this.subscription.getSubscriptionId());
            
            try {
                super.getMCServices().getParameterService().getParameterStub().monitorValueDeregister(idList);
                this.subscription = null;
            } catch (MALInteractionException ex) {
                Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(GroundMOAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(this.comServices != null){
            this.comServices.closeConnections();
        }
        
        if(this.mcServices != null){
            this.mcServices.closeConnections();
        }
        
        if(this.commonServices != null){
            this.commonServices.closeConnections();
        }
        
        if(this.platformServices != null){
            this.platformServices.closeConnections();
        }
                
        if(this.smServices != null){
            this.smServices.closeConnections();
        }
                
    }
    
    
}
