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
package esa.mo.nmf;

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.mc.impl.provider.ActionProviderServiceImpl;
import esa.mo.mc.impl.provider.AggregationProviderServiceImpl;
import esa.mo.mc.impl.provider.AlertProviderServiceImpl;
import esa.mo.mc.impl.provider.ParameterProviderServiceImpl;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.*;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.conversion.ConversionServiceInfo;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * The MCRegistration class provides methods to be implemented by the
 * registration object in order to let the registration of Parameters, Actions,
 * Aggregations and Alerts.
 */
public class MCRegistration {

    private final static URI PROVIDER_URI = new URI("NMF_Registration");

    /**
     * The possible registration modes.
     */
    public enum RegistrationMode {
        UPDATE_IF_EXISTS, DONT_UPDATE_IF_EXISTS
    }

    private RegistrationMode mode = RegistrationMode.DONT_UPDATE_IF_EXISTS; // default mode

    public final COMServicesProvider comServices;
    public final ParameterProviderServiceImpl parameterService;
    public final AggregationProviderServiceImpl aggregationService;
    public final AlertProviderServiceImpl alertService;
    public final ActionProviderServiceImpl actionService;

    public MCRegistration(COMServicesProvider comServices,
            ParameterProviderServiceImpl parameterService,
            AggregationProviderServiceImpl aggregationService,
            AlertProviderServiceImpl alertService,
            ActionProviderServiceImpl actionService) {
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
     * in the Parameter service. This abstracts the NMF developer from the
     * low-level details of MO.
     *
     * @param definitions The parameter definitions
     * @return The parameter object instance identifiers of the
     * ParameterIdentity objects.
     */
    public LongList registerParameters(final ParameterDefinitionList definitions) {
        // Some validation
        if (definitions == null) {
            return null;
        }

        if (definitions.isEmpty()) {
            return null;
        }

        final IdentifierList names = new IdentifierList(definitions.size());
        for (ParameterDefinition def : definitions) {
            names.add(def.getName());
        }

        try {
            ObjectInstancePairList duplicateIds = new ObjectInstancePairList();
            ParameterDefinitionList duplicateDefs = new ParameterDefinitionList();
            duplicateDefs.addAll(definitions);

            try {
                duplicateIds = parameterService.listDefinition(names, null);
            } catch (MALException ex1) {
                Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (MALInteractionException ex1) {
                // There are some new ones!
                final UIntegerList extraInfo = (UIntegerList) ex1.getStandardError().getExtraInformation();

                //-------------New Definitions-------------
                ParameterDefinitionList newDefs = new ParameterDefinitionList();

                for (int i = 0; i < extraInfo.size(); i++) { // Which ones are new?
                    int index = (short) extraInfo.get(i).getValue();
                    newDefs.add(definitions.get(index));
                }

                parameterService.addParameter(newDefs, null);

                //-------------Duplicate Definitions-------------
                IdentifierList requestAgain = new IdentifierList();
                requestAgain.addAll(names);

                for (int i = extraInfo.size() - 1; i >= 0; i--) {
                    requestAgain.remove((int) extraInfo.get(i).getValue());
                    duplicateDefs.remove((int) extraInfo.get(i).getValue());
                }

                duplicateIds = parameterService.listDefinition(requestAgain, null);
            }

            LongList duplicateObjIds = new LongList(duplicateIds.size());

            for (int j = 0; j < duplicateIds.size(); j++) {
                duplicateObjIds.add(duplicateIds.get(j).getObjIdentityInstanceId());
            }

            if (mode == RegistrationMode.UPDATE_IF_EXISTS) {
                parameterService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }

            final ObjectInstancePairList newInstPairs = parameterService.listDefinition(names, null);
            final LongList outs = new LongList(newInstPairs.size());

            for (ObjectInstancePair newInstPair : newInstPairs) {
                outs.add(newInstPair.getObjIdentityInstanceId());
            }

            return outs;
        } catch (MALException | MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    /**
     * The registerAggregations operation registers a set of Aggregation
     * Definitions in the Aggregation service. This abstracts the NMF developer
     * from the low-level details of MO.
     *
     * @param definitions The aggregation definitions
     * @return The aggregation object instance identifiers of the
     * AggregationIdentity objects.
     */
    public LongList registerAggregations(final AggregationDefinitionList definitions) {
        // Some validation
        if (definitions == null) {
            return null;
        }

        if (definitions.isEmpty()) {
            return null;
        }

        final IdentifierList names = new IdentifierList(definitions.size());
        for (AggregationDefinition def : definitions) {
            names.add(def.getName());
        }

        try {
            ObjectInstancePairList duplicateIds = new ObjectInstancePairList();
            AggregationDefinitionList duplicateDefs = new AggregationDefinitionList();
            duplicateDefs.addAll(definitions);

            try {
                duplicateIds = aggregationService.listDefinition(names, null);
            } catch (MALException ex1) {
                Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (MALInteractionException ex1) {
                // There are some new ones!
                final UIntegerList extraInfo = (UIntegerList) ex1.getStandardError().getExtraInformation();

                //-------------New Definitions-------------
                AggregationDefinitionList newDefs = new AggregationDefinitionList();

                for (int i = 0; i < extraInfo.size(); i++) { // Which ones already exist?
                    int index = (short) extraInfo.get(i).getValue();
                    newDefs.add(definitions.get(index));
                }

                aggregationService.addAggregation(newDefs, null);

                //-------------Duplicate Definitions-------------
                IdentifierList requestAgain = new IdentifierList();
                requestAgain.addAll(names);

                for (int i = extraInfo.size() - 1; i >= 0; i--) {
                    requestAgain.remove((int) extraInfo.get(i).getValue());
                    duplicateDefs.remove((int) extraInfo.get(i).getValue());
                }

                duplicateIds = aggregationService.listDefinition(requestAgain, null);
            }

            LongList duplicateObjIds = new LongList(duplicateIds.size());

            for (int j = 0; j < duplicateIds.size(); j++) {
                duplicateObjIds.add(duplicateIds.get(j).getObjIdentityInstanceId());
            }

            if (mode == RegistrationMode.UPDATE_IF_EXISTS) {
                aggregationService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }

            final ObjectInstancePairList newInstPairs = aggregationService.listDefinition(names, null);
            final LongList outs = new LongList(newInstPairs.size());

            for (ObjectInstancePair newInstPair : newInstPairs) {
                outs.add(newInstPair.getObjIdentityInstanceId());
            }

            return outs;
        } catch (MALException | MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    /**
     * The registerAlerts operation registers a set of Alert Definitions in the
     * Alert service. This abstracts the NMF developer from the low-level
     * details of MO.
     *
     * @param definitions The alert definitions
     * @return The aggregation object instance identifiers of the AlertIdentity
     * objects.
     */
    public LongList registerAlerts(final AlertDefinitionList definitions) {
        // Some validation
        if (definitions == null) {
            return null;
        }

        if (definitions.isEmpty()) {
            return null;
        }

        final IdentifierList names = new IdentifierList(definitions.size());
        for (AlertDefinition def : definitions) {
            names.add(def.getName());
        }

        try {
            ObjectInstancePairList duplicateIds = new ObjectInstancePairList();
            AlertDefinitionList duplicateDefs = new AlertDefinitionList();
            duplicateDefs.addAll(definitions);

            try {
                duplicateIds = alertService.listDefinition(names, null);
            } catch (MALException ex1) {
                Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (MALInteractionException ex1) {
                // There are some new ones!
                final UIntegerList extraInfo = (UIntegerList) ex1.getStandardError().getExtraInformation();

                //-------------New Definitions-------------
                AlertDefinitionList newDefs = new AlertDefinitionList();

                for (int i = 0; i < extraInfo.size(); i++) { // Which ones already exist?
                    int index = (short) extraInfo.get(i).getValue();
                    newDefs.add(definitions.get(index));
                }

                alertService.addAlert(newDefs, null);

                //-------------Duplicate Definitions-------------
                IdentifierList requestAgain = new IdentifierList();
                requestAgain.addAll(names);

                for (int i = extraInfo.size() - 1; i >= 0; i--) {
                    requestAgain.remove((int) extraInfo.get(i).getValue());
                    duplicateDefs.remove((int) extraInfo.get(i).getValue());
                }

                duplicateIds = alertService.listDefinition(requestAgain, null);
            }

            LongList duplicateObjIds = new LongList(duplicateIds.size());

            for (int j = 0; j < duplicateIds.size(); j++) {
                duplicateObjIds.add(duplicateIds.get(j).getObjIdentityInstanceId());
            }

            if (mode == RegistrationMode.UPDATE_IF_EXISTS) {
                alertService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }

            final ObjectInstancePairList newInstPairs = alertService.listDefinition(names, null);
            final LongList outs = new LongList(newInstPairs.size());

            for (ObjectInstancePair newInstPair : newInstPairs) {
                outs.add(newInstPair.getObjIdentityInstanceId());
            }

            return outs;
        } catch (MALException | MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    /**
     * The registerActions operation registers a set of Actions Definitions in
     * the Action service. This abstracts the NMF developer from the low-level
     * details of MO.
     *
     * @param definitions The action definitions
     * @return The aggregation object instance identifiers of the ActionIdentity
     * objects.
     */
    public LongList registerActions(final ActionDefinitionList definitions) {
        // Some validation
        if (definitions == null) {
            return null;
        }

        if (definitions.isEmpty()) {
            return null;
        }

        final IdentifierList names = new IdentifierList(definitions.size());
        for (ActionDefinition def : definitions) {
            names.add(def.getName());
        }

        try {
            ObjectInstancePairList duplicateIds = new ObjectInstancePairList();
            ActionDefinitionList duplicateDefs = new ActionDefinitionList();
            duplicateDefs.addAll(definitions);

            try {
                duplicateIds = actionService.listDefinition(names, null);
            } catch (MALException ex1) {
                Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
            } catch (MALInteractionException ex1) {
                // There are some new ones!
                final UIntegerList extraInfo = (UIntegerList) ex1.getStandardError().getExtraInformation();

                //-------------New Definitions-------------
                ActionDefinitionList newDefs = new ActionDefinitionList();

                for (int i = 0; i < extraInfo.size(); i++) { // Which ones already exist?
                    int index = (short) extraInfo.get(i).getValue();
                    newDefs.add(definitions.get(index));
                }

                actionService.addAction(newDefs, null);

                //-------------Duplicate Definitions-------------
                IdentifierList requestAgain = new IdentifierList();
                requestAgain.addAll(names);

                for (int i = extraInfo.size() - 1; i >= 0; i--) {
                    requestAgain.remove((int) extraInfo.get(i).getValue());
                    duplicateDefs.remove((int) extraInfo.get(i).getValue());
                }

                duplicateIds = actionService.listDefinition(requestAgain, null);
            }

            LongList duplicateObjIds = new LongList(duplicateIds.size());

            for (int j = 0; j < duplicateIds.size(); j++) {
                duplicateObjIds.add(duplicateIds.get(j).getObjIdentityInstanceId());
            }

            if (mode == RegistrationMode.UPDATE_IF_EXISTS) {
                actionService.updateDefinition(duplicateObjIds, duplicateDefs, null);
            }

            final ObjectInstancePairList newInstPairs = actionService.listDefinition(names, null);
            final LongList outs = new LongList(newInstPairs.size());

            for (ObjectInstancePair newInstPair : newInstPairs) {
                outs.add(newInstPair.getObjIdentityInstanceId());
            }

            return outs;
        } catch (MALException | MALInteractionException ex1) {
            Logger.getLogger(MCRegistration.class.getName()).log(Level.SEVERE, null, ex1);
        }

        return null;
    }

    /**
     * The registerConversions operation registers a set of conversions.
     *
     * @param conversions The conversions
     * @return The list of ObjIds of the Identity objects of the conversions.
     * @throws esa.mo.nmf.NMFException if the registration was not possible.
     * @throws org.ccsds.moims.mo.mal.MALException if there is a MAL exception
     * @throws org.ccsds.moims.mo.mal.MALInteractionException if there is a
     * problem while storing the registrations in the COM Archive.
     */
    public ObjectIdList registerConversions(ElementList conversions)
            throws NMFException, MALException, MALInteractionException {
        if (conversions == null) {
            throw new NMFException("The conversions object cannot be null!");
        }

        // Discrete Conversion:
        if (conversions instanceof DiscreteConversionDetailsList) {
            return this.registerConversionsGen(conversions, ConversionServiceInfo.DISCRETECONVERSION_OBJECT_TYPE);
        }

        // Line Conversion:
        if (conversions instanceof LineConversionDetailsList) {
            return this.registerConversionsGen(conversions, ConversionServiceInfo.LINECONVERSION_OBJECT_TYPE);
        }

        // Polynomial Conversion:
        if (conversions instanceof PolyConversionDetailsList) {
            return this.registerConversionsGen(conversions, ConversionServiceInfo.POLYCONVERSION_OBJECT_TYPE);
        }

        // Range Conversion:
        if (conversions instanceof RangeConversionDetailsList) {
            return this.registerConversionsGen(conversions, ConversionServiceInfo.RANGECONVERSION_OBJECT_TYPE);
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
        final ArchiveDetailsList metadata = HelperArchive.generateArchiveDetailsList(
                null, null, PROVIDER_URI);
        final HeterogeneousList names = new HeterogeneousList();

        Random rand = new Random();

        for (Object conversion : conversions) {
            names.add(new Identifier("Conversion" + rand.nextInt()));
        }

        for (int i = 1; i < conversions.size(); i++) { // There's already 1 object in the list
            metadata.add(metadata.get(0));
        }

        final LongList conversionIdentityObjIds = comServices.getArchiveService().store(true,
                ConversionServiceInfo.CONVERSIONIDENTITY_OBJECT_TYPE,
                domain,
                metadata,
                names,
                null);

        /*
        for (int i = 0; i < metadata.size(); i++) {
            metadata.get(i).setDetails(new ObjectLinks(conversionIdentityObjIds.get(i), null));
        }
         */
        ArchiveDetailsList metadataConversions = new ArchiveDetailsList();

        HeterogeneousList myList = new HeterogeneousList();
        myList.addAll(conversions);

        for (int i = 0; i < myList.size(); i++) {
            ArchiveDetails det = new ArchiveDetails(
                    0L,
                    new ObjectLinks(conversionIdentityObjIds.get(i), null),
                    metadata.get(0).getNetwork(),
                    metadata.get(0).getTimestamp(),
                    metadata.get(0).getProvider()
            );
            metadataConversions.add(det);
        }

        comServices.getArchiveService().store(false,
                objType,
                domain,
                metadataConversions,
                myList,
                null);

        ObjectIdList output = new ObjectIdList();

        for (Long objId : conversionIdentityObjIds) {
            output.add(new ObjectId(objType, domain, objId));
        }

        return output;
    }

}
