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
package esa.mo.nmf;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import esa.mo.mc.impl.provider.AlertProviderServiceImpl;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequest;
import org.ccsds.moims.mo.mc.action.structures.ActionCreationRequestList;
import org.ccsds.moims.mo.mc.action.structures.ActionDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCreationRequest;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationCreationRequestList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequest;
import org.ccsds.moims.mo.mc.alert.structures.AlertCreationRequestList;
import org.ccsds.moims.mo.mc.alert.structures.AlertDefinitionDetailsList;
import org.ccsds.moims.mo.mc.conversion.ConversionHelper;
import org.ccsds.moims.mo.mc.conversion.structures.DiscreteConversionDetailsList;
import org.ccsds.moims.mo.mc.conversion.structures.LineConversionDetailsList;
import org.ccsds.moims.mo.mc.conversion.structures.PolyConversionDetailsList;
import org.ccsds.moims.mo.mc.conversion.structures.RangeConversionDetailsList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequest;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterCreationRequestList;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetailsList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePairList;

/**
 * The MCRegistration class provides methods to be implemented by the
 * registration object in order to let the registration of Parameters, Actions,
 * Aggregations and Alerts.
 *
 */
public class MCRegistration {
    
    private final static URI PROVIDER_URI = new URI("NMF_Registration");

    public enum RegistrationMode {
        UPDATE_IF_EXISTS, DONT_UPDATE_IF_EXISTS
    };
    private RegistrationMode mode = RegistrationMode.DONT_UPDATE_IF_EXISTS; // default mode

    public final COMServicesProvider comServices;
    public final ParameterProviderServiceImpl parameterService;
    public final AggregationProviderServiceImpl aggregationService;
    public final AlertProviderServiceImpl alertService;
    public final ActionProviderServiceImpl actionService;

    public MCRegistration(
            COMServicesProvider comServices,
            ParameterProviderServiceImpl parameterService,
            AggregationProviderServiceImpl aggregationService,
            AlertProviderServiceImpl alertService,
            ActionProviderServiceImpl actionService
    ) {
        this.comServices = comServices;
        this.parameterService = parameterService;
        this.aggregationService = aggregationService;
        this.alertService = alertService;
        this.actionService = actionService;
    }

    public void setMode(RegistrationMode mode) {
        this.mode = mode;
    }

    /**
     * The registerParameters operation registers a set of Parameter Definitions
     * in the M&C Parameter service. This abstracts the NMF developer from the
     * low-level details of MO.
     *
     * @param names The parameter name identifiers
     * @param definitions The parameter definitions
     * @return The parameter object instance identifiers of the ParameterIdentity
     * objects.
     */
    public LongList registerParameters(final IdentifierList names, final ParameterDefinitionDetailsList definitions) {
        // Some validation
        if(names == null || definitions == null){
            return null;
        }

        if(names.isEmpty() || definitions.isEmpty()){
            return null;
        }
        
        try {
            ObjectInstancePairList duplicateIds = new ObjectInstancePairList();
            ParameterDefinitionDetailsList duplicateDefs = new ParameterDefinitionDetailsList();
            duplicateDefs.addAll(definitions);
            
            try {
                duplicateIds = parameterService.listDefinition(names, null);
            } catch (MALException ex1) {
                Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (MALInteractionException ex1) {
                // There are some new ones!
                final UIntegerList extraInfo = (UIntegerList) ex1.getStandardError().getExtraInformation();

                //-------------New Definitions-------------
                ParameterCreationRequestList newDefs = new ParameterCreationRequestList();
                
                for (int i = 0; i < extraInfo.size(); i++) { // Which ones already exist?
                    int index = (short) extraInfo.get(i).getValue();
                    newDefs.add(new ParameterCreationRequest(names.get(index), definitions.get(index)));
                }

                parameterService.addParameter(newDefs, null);

                //-------------Duplicate Definitions-------------
                IdentifierList requestAgain = new IdentifierList();
                requestAgain.addAll(names);
                
                for(int i = extraInfo.size() - 1; i >= 0; i--){
                    requestAgain.remove((int) extraInfo.get(i).getValue());
                    duplicateDefs.remove((int) extraInfo.get(i).getValue());
                }
                
                duplicateIds = parameterService.listDefinition(requestAgain, null);
            }
            
            LongList duplicateObjIds = new LongList(duplicateIds.size());
            
            for(int j = 0 ; j < duplicateIds.size(); j++){
                duplicateObjIds.add(duplicateIds.get(j).getObjIdentityInstanceId());
            }
            
            if (mode == RegistrationMode.UPDATE_IF_EXISTS) {
                parameterService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }

            final ObjectInstancePairList newInstPairs = parameterService.listDefinition(names, null);
            final LongList outs = new LongList(newInstPairs.size());
            
            for(ObjectInstancePair newInstPair : newInstPairs){
                outs.add(newInstPair.getObjIdentityInstanceId());
            }
            
            return outs;
        } catch (MALException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    /**
     * The registerAggregations operation registers a set of Aggregation Definitions
     * in the M&C Aggregation service. This abstracts the NMF developer from the
     * low-level details of MO.
     *
     * @param names The aggregation name identifiers
     * @param definitions The aggregation definitions
     * @return The aggregation object instance identifiers of the AggregationIdentity
     * objects.
     */
    public LongList registerAggregations(final IdentifierList names, final AggregationDefinitionDetailsList definitions) {
        // Some validation
        if(names == null || definitions == null){
            return null;
        }

        if(names.isEmpty() || definitions.isEmpty()){
            return null;
        }

        try {
            ObjectInstancePairList duplicateIds = new ObjectInstancePairList();
            AggregationDefinitionDetailsList duplicateDefs = new AggregationDefinitionDetailsList();
            duplicateDefs.addAll(definitions);
            
            try {
                duplicateIds = aggregationService.listDefinition(names, null);
            } catch (MALException ex1) {
                Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (MALInteractionException ex1) {
                // There are some new ones!
                final UIntegerList extraInfo = (UIntegerList) ex1.getStandardError().getExtraInformation();

                //-------------New Definitions-------------
                AggregationCreationRequestList newDefs = new AggregationCreationRequestList();
                
                for (int i = 0; i < extraInfo.size(); i++) { // Which ones already exist?
                    int index = (short) extraInfo.get(i).getValue();
                    newDefs.add(new AggregationCreationRequest(names.get(index), definitions.get(index)));
                }

                aggregationService.addAggregation(newDefs, null);

                //-------------Duplicate Definitions-------------
                IdentifierList requestAgain = new IdentifierList();
                requestAgain.addAll(names);
                
                for(int i = extraInfo.size() - 1; i >= 0; i--){
                    requestAgain.remove((int) extraInfo.get(i).getValue());
                    duplicateDefs.remove((int) extraInfo.get(i).getValue());
                }
                
                duplicateIds = aggregationService.listDefinition(requestAgain, null);
            }
            
            LongList duplicateObjIds = new LongList(duplicateIds.size());
            
            for(int j = 0 ; j < duplicateIds.size(); j++){
                duplicateObjIds.add(duplicateIds.get(j).getObjIdentityInstanceId());
            }
            
            if (mode == RegistrationMode.UPDATE_IF_EXISTS) {
                aggregationService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }

            final ObjectInstancePairList newInstPairs = aggregationService.listDefinition(names, null);
            final LongList outs = new LongList(newInstPairs.size());
            
            for(ObjectInstancePair newInstPair : newInstPairs){
                outs.add(newInstPair.getObjIdentityInstanceId());
            }
            
            return outs;
        } catch (MALException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    /**
     * The registerAlerts operation registers a set of Alert Definitions
     * in the M&C Alert service. This abstracts the NMF developer from the
     * low-level details of MO.
     *
     * @param names The alert name identifiers
     * @param definitions The alert definitions
     * @return The aggregation object instance identifiers of the AlertIdentity
     * objects.
     */
    public LongList registerAlerts(final IdentifierList names, final AlertDefinitionDetailsList definitions) {
        // Some validation
        if(names == null || definitions == null){
            return null;
        }

        if(names.isEmpty() || definitions.isEmpty()){
            return null;
        }

        try {
            ObjectInstancePairList duplicateIds = new ObjectInstancePairList();
            AlertDefinitionDetailsList duplicateDefs = new AlertDefinitionDetailsList();
            duplicateDefs.addAll(definitions);
            
            try {
                duplicateIds = alertService.listDefinition(names, null);
            } catch (MALException ex1) {
                Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (MALInteractionException ex1) {
                // There are some new ones!
                final UIntegerList extraInfo = (UIntegerList) ex1.getStandardError().getExtraInformation();

                //-------------New Definitions-------------
                AlertCreationRequestList newDefs = new AlertCreationRequestList();
                
                for (int i = 0; i < extraInfo.size(); i++) { // Which ones already exist?
                    int index = (short) extraInfo.get(i).getValue();
                    newDefs.add(new AlertCreationRequest(names.get(index), definitions.get(index)));
                }

                alertService.addAlert(newDefs, null);

                //-------------Duplicate Definitions-------------
                IdentifierList requestAgain = new IdentifierList();
                requestAgain.addAll(names);
                
                for(int i = extraInfo.size() - 1; i >= 0; i--){
                    requestAgain.remove((int) extraInfo.get(i).getValue());
                    duplicateDefs.remove((int) extraInfo.get(i).getValue());
                }
                
                duplicateIds = alertService.listDefinition(requestAgain, null);
            }
            
            LongList duplicateObjIds = new LongList(duplicateIds.size());
            
            for(int j = 0 ; j < duplicateIds.size(); j++){
                duplicateObjIds.add(duplicateIds.get(j).getObjIdentityInstanceId());
            }
            
            if (mode == RegistrationMode.UPDATE_IF_EXISTS) {
                alertService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }

            final ObjectInstancePairList newInstPairs = alertService.listDefinition(names, null);
            final LongList outs = new LongList(newInstPairs.size());
            
            for(ObjectInstancePair newInstPair : newInstPairs){
                outs.add(newInstPair.getObjIdentityInstanceId());
            }
            
            return outs;
        } catch (MALException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    /**
     * The registerActions operation registers a set of Actions Definitions
     * in the M&C Action service. This abstracts the NMF developer from the
     * low-level details of MO.
     *
     * @param names The action name identifiers
     * @param definitions The action definitions
     * @return The aggregation object instance identifiers of the ActionIdentity
     * objects.
     */
    public LongList registerActions(final IdentifierList names, final ActionDefinitionDetailsList definitions) {
        // Some validation
        if(names == null || definitions == null){
            return null;
        }

        if(names.isEmpty() || definitions.isEmpty()){
            return null;
        }
        
        try {
            ObjectInstancePairList duplicateIds = new ObjectInstancePairList();
            ActionDefinitionDetailsList duplicateDefs = new ActionDefinitionDetailsList();
            duplicateDefs.addAll(definitions);
            
            try {
                duplicateIds = actionService.listDefinition(names, null);
            } catch (MALException ex1) {
                Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (MALInteractionException ex1) {
                // There are some new ones!
                final UIntegerList extraInfo = (UIntegerList) ex1.getStandardError().getExtraInformation();

                //-------------New Definitions-------------
                ActionCreationRequestList newDefs = new ActionCreationRequestList();
                
                for (int i = 0; i < extraInfo.size(); i++) { // Which ones already exist?
                    int index = (short) extraInfo.get(i).getValue();
                    newDefs.add(new ActionCreationRequest(names.get(index), definitions.get(index)));
                }

                actionService.addAction(newDefs, null);

                //-------------Duplicate Definitions-------------
                IdentifierList requestAgain = new IdentifierList();
                requestAgain.addAll(names);
                
                for(int i = extraInfo.size() - 1; i >= 0; i--){
                    requestAgain.remove((int) extraInfo.get(i).getValue());
                    duplicateDefs.remove((int) extraInfo.get(i).getValue());
                }
                
                duplicateIds = actionService.listDefinition(requestAgain, null);
            }
            
            LongList duplicateObjIds = new LongList(duplicateIds.size());
            
            for(int j = 0 ; j < duplicateIds.size(); j++){
                duplicateObjIds.add(duplicateIds.get(j).getObjIdentityInstanceId());
            }
            
            if (mode == RegistrationMode.UPDATE_IF_EXISTS) {
                actionService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }

            final ObjectInstancePairList newInstPairs = actionService.listDefinition(names, null);
            final LongList outs = new LongList(newInstPairs.size());
            
            for(ObjectInstancePair newInstPair : newInstPairs){
                outs.add(newInstPair.getObjIdentityInstanceId());
            }
            
            return outs;
        } catch (MALException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        } catch (MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    /**
     * The registerConversions operation registers a set of conversions.
     *
     * @param conversions The conversions
     * @return The list of ObjIds of the Identity objects of the conversions.
     * @throws esa.mo.nmf.NMFException
     * @throws org.ccsds.moims.mo.mal.MALException
     * @throws org.ccsds.moims.mo.mal.MALInteractionException
     */
    public ObjectIdList registerConversions(ElementList conversions) throws NMFException, MALException, MALInteractionException {
        if (conversions == null) {
            throw new NMFException("The conversions object cannot be null!");
        }

        // Discrete Conversion:
        if (conversions instanceof DiscreteConversionDetailsList) {
            return this.registerConversionsGen(conversions, ConversionHelper.DISCRETECONVERSION_OBJECT_TYPE);
        }
        
        // Line Conversion:
        if (conversions instanceof LineConversionDetailsList) {
            return this.registerConversionsGen(conversions, ConversionHelper.LINECONVERSION_OBJECT_TYPE);
        }

        // Polynomial Conversion:
        if (conversions instanceof PolyConversionDetailsList) {
            return this.registerConversionsGen(conversions, ConversionHelper.POLYCONVERSION_OBJECT_TYPE);
        }

        // Range Conversion:
        if (conversions instanceof RangeConversionDetailsList) {
            return this.registerConversionsGen(conversions, ConversionHelper.RANGECONVERSION_OBJECT_TYPE);
        }

        throw new NMFException("The conversion object didn't match any type of Conversion.");
    }

    /**
     * The registerConversionsGen operation registers a set conversions of a
     * specific Object Type.
     *
     * @param conversions The conversions
     * @param objType The Object Type of the conversions
     * @return The list of ObjIds of the Identity objects of the conversions.
     */
    private ObjectIdList registerConversionsGen(final ElementList conversions, 
            final ObjectType objType) throws MALException, MALInteractionException {
        final IdentifierList domain = ConfigurationProviderSingleton.getDomain();
        final ArchiveDetailsList archiveDetailsList = HelperArchive.generateArchiveDetailsList(null, null, PROVIDER_URI);
        final IdentifierList names = new IdentifierList();

        Random rand = new Random();
        
        for (Object conversion : conversions) {
            names.add(new Identifier("Conversion" + rand.nextInt()));
        }
        
        for (int i = 1; i < conversions.size(); i++) { // There's already 1 object in the list
            archiveDetailsList.add(archiveDetailsList.get(0));
        }

        final LongList conversionIdentityObjIds = comServices.getArchiveService().store(
                true,
                ConversionHelper.CONVERSIONIDENTITY_OBJECT_TYPE,
                domain,
                archiveDetailsList,
                names,
                null);

        for (int i = 0; i < archiveDetailsList.size(); i++) {
            archiveDetailsList.get(i).setDetails(new ObjectDetails(conversionIdentityObjIds.get(i), null));
        }

        /*
        final LongList objIds = comServices.getArchiveService().store(
                true,
                objType,
                domain,
                archiveDetailsList,
                conversions,
                null);
        */
        comServices.getArchiveService().store(
                false,
                objType,
                domain,
                archiveDetailsList,
                conversions,
                null);
        
        ObjectIdList output = new ObjectIdList();

        for (Long objId : conversionIdentityObjIds) {
            output.add(new ObjectId(objType, new ObjectKey(domain, objId)));
        }

        return output;
    }
    
}
