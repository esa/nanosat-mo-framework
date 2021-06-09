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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.TimeList;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSet;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterValueList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationSetValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationSetValueList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueList;
import org.ccsds.moims.mo.mc.aggregation.structures.GenerationMode;
import org.ccsds.moims.mo.mc.aggregation.structures.ThresholdFilter;
import org.ccsds.moims.mo.mc.aggregation.structures.ThresholdType;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.parameter.structures.ValidityState;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;

/**
 *
 * @author Cesar Coelho
 */
public final class AggregationManager extends MCManager {

    private final HashMap<Long, Boolean> isFilterTriggered; // IdentityId, Boolean Value that determines if the filter was triggered
    private final HashMap<Long, AggregationValue> periodicAggregationValuesLast; // IdentityId, Aggregation Value List per definition
    private final HashMap<Long, AggregationValue> periodicAggregationValuesCurrent; // IdentityId, Aggregation Value List per definition
    private final HashMap<Long, TimeList> latestSampleTimeList; // IdentityId, Time of the first sample of each ParameterSetValue
    private final HashMap<Long, ArrayList<Integer>> sampleCountList; //IdentityId, Amount of samples per parameterSetValue

    private long uniqueObjIdIdentity; // Unique objId Identity (different for every Identity)
    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdAVal;
    private final ParameterManager parameterManager;

    public AggregationManager(COMServicesProvider comServices, ParameterManager parameterManager) {
        super(comServices);
        this.parameterManager = parameterManager;

        this.isFilterTriggered = new HashMap<>();
        this.periodicAggregationValuesLast = new HashMap<>();
        this.periodicAggregationValuesCurrent = new HashMap<>();
        this.latestSampleTimeList = new HashMap<>();
        this.sampleCountList = new HashMap<>();

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdIdentity = 0L; // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdDef = 0L; // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdAVal = 0L; // The zeroth value will not be used (reserved for the wildcard)
        }
    }

    public AggregationDefinitionDetails getAggregationDefinition(Long identityId) {
        return (AggregationDefinitionDetails) this.getDefinition(identityId);
    }

    /**
     * Initializes the variables in the manager belonging the updates and
     * samples of the given aggregation. this method will be called after the
     * creation of an aggregation-definition
     *
     * @param identityIdList The list of identity Ids
     */
    protected void createAggregationValuesList(LongList identityIdList) {
        for (Long identityId : identityIdList) {
            periodicAggregationValuesLast.put(identityId, new AggregationValue());
            periodicAggregationValuesCurrent.put(identityId, new AggregationValue());
            isFilterTriggered.put(identityId, false);
            //initialize timList with the amount of parameterSets
            //final int parameterSetsCount = getAggregationDefinition(identityId).getParameterSets().size();
            latestSampleTimeList.put(identityId, new TimeList());
            sampleCountList.put(identityId, new ArrayList<>());

            this.populateAggregationValues(identityId);
        }
    }

    /**
     * resets periodicAggregationValuesLast, periodicAggregationValuesCurrent,
     * sampleCounter, latestSampleTimeList and the filterTriggered-variables.
     * this method will be called after an aggregation-definition was updated.
     *
     * @param identityId The identity Id.
     */
    public void populateAggregationValues(final Long identityId) {
        AggregationSetValueList aggregationSetValueListLast = new AggregationSetValueList();
        AggregationSetValueList aggregationSetValueListCurrent = new AggregationSetValueList();
        AggregationDefinitionDetails definition = this.getAggregationDefinition(identityId);
        final int paramSetSize = definition.getParameterSets().size();
        //reset the latest sample-time and the sample-counter
        if (paramSetSize != 0) {
            latestSampleTimeList.get(identityId).clear();
            sampleCountList.get(identityId).clear();
        }
        //init the latest sample-time and the sample-counter, filtertriggered and latest and current aggregation-values
        for (int j = 0; j < paramSetSize; j++) {
            latestSampleTimeList.get(identityId).add(j, null);
            aggregationSetValueListLast.add(j, new AggregationSetValue());
            aggregationSetValueListCurrent.add(j, new AggregationSetValue());
            sampleCountList.get(identityId).add(j, 0);
        }
        periodicAggregationValuesLast.get(identityId).setParameterSetValues(aggregationSetValueListLast);
        periodicAggregationValuesCurrent.get(identityId).setParameterSetValues(aggregationSetValueListCurrent);
        this.setFilterTriggered(identityId, false);  // Reset the filter state
    }

    /**
     * resets samplecounter, latestsampletimelist and the
     * filtertriggered-variables. this method will be called after each
     * aggregation-value-update was published
     *
     * @param identityId The identity Id.
     * @return true if it was successful, false if identity not found
     */
    public Boolean resetAggregationSampleHelperVariables(Long identityId) {
        if (!this.existsIdentity(identityId)) {
            return false;
        }
        AggregationDefinitionDetails definition = this.getAggregationDefinition(identityId);
        final int paramSetSize = definition.getParameterSets().size();
        //reset the latest sample-time and the sample-counter
        if (paramSetSize != 0) {
            latestSampleTimeList.get(identityId).clear();
            sampleCountList.get(identityId).clear();
        }
        //init the latest sample-time and the sample-counter
        for (int j = 0; j < paramSetSize; j++) {
            latestSampleTimeList.get(identityId).add(j, null);
            sampleCountList.get(identityId).add(j, 0);
        }
        this.setFilterTriggered(identityId, false);  // Reset the filter state
        return true;
    }

    /**
     * removes the values of the aggregation with the given id from the internal
     * lists
     *
     * @param identityId The identity Id.
     */
    public void removeAggregationValues(Long identityId) {
        periodicAggregationValuesLast.remove(identityId);
        periodicAggregationValuesCurrent.remove(identityId);
        latestSampleTimeList.remove(identityId);
        isFilterTriggered.remove(identityId);
        sampleCountList.remove(identityId);
    }

    public Long storeAndGenerateAValobjId(AggregationValue aVal, Long related, ObjectId source, URI uri, FineTime timestamp) {
        if (super.getArchiveService() == null) {
            uniqueObjIdAVal++;
            return this.uniqueObjIdAVal;
        } else {
            AggregationValueList aValList = new AggregationValueList();
            aValList.add(aVal);

            try {
                //requirement: 3.7.4.d, 3.7.6.b
                LongList objIds = super.getArchiveService().store(
                        true,
                        AggregationHelper.AGGREGATIONVALUEINSTANCE_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(
                            related,
                            source,
                            ConfigurationProviderSingleton.getNetwork(),
                            uri,
                            timestamp), //requirement: 3.7.4.e
                        aValList,
                        null);

                if (objIds.size() == 1) {
                    return objIds.get(0);
                }
            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }

    }

    /**
     *
     * @param paramIdentityId the identity id of the parameter to be sampled
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter, is periodic and the update hasnt been received in
     * the aggregation-period. if true, the validity-state of the new parameter
     * will be expired.
     * @return ParameterValue of the parameter, or an empty ParameterValue with INVALID state if parameter cannot be retrieved
     */
    private ParameterValue sampleParameter(Long paramIdentityId, boolean aggrExpired) {
        try {
            return parameterManager.getParameterValue(paramIdentityId, aggrExpired);
        } catch (MALInteractionException ex) {
            return new ParameterValue(new UOctet((short)ValidityState.INVALID_RAW_NUM_VALUE.getValue()), null, null);
        }
    }

    /**
     * This method is created for the field:
     * AggregationDefinitionDetails.sendDefinitions. If
     * AggregationDefinitionDetails.sendDefinitions is TRUE, reports will
     * include the ParameterDefinition object instance identifier in the
     * AggregationParameterValue, if FALSE it will be set to NULL.
     *
     * @param paramIdentityIds the identity id of the parameters to be sampled
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter, is periodic and the update hasnt been received in
     * the aggregation-period. if true, the validity-state of the new parameter
     * will be expired.
     * @return
     */
    private AggregationParameterValueList sampleParameters(LongList paramIdentityIds, boolean aggrExpired, boolean sendDefinitions) {
        AggregationParameterValueList aggrPValList = new AggregationParameterValueList();
        for (Long identityId : paramIdentityIds) {
            final Long paramDefId = sendDefinitions ? parameterManager.getDefinitionId(identityId) : null;
            final ParameterValue paramValue = sampleParameter(identityId, aggrExpired);
            aggrPValList.add(new AggregationParameterValue(paramValue, paramDefId));
        }

        return aggrPValList;
    }

    private Boolean triggeredFilter(Attribute previousValue, Attribute currentValue, ThresholdFilter filter) {
        if (filter == null) {
            return false;  // If there's no filter, then it will never be ignored! 
        }
        final Double previousValueDouble = Double.parseDouble(previousValue.toString());
        final Double currentValueDouble = Double.parseDouble(currentValue.toString());
        final double thresholdValue = Double.parseDouble(filter.getThresholdValue().toString());

        if (filter.getThresholdType() == ThresholdType.DELTA) // requirement: 3.7.3.l
        {
            return (Math.abs(previousValueDouble - currentValueDouble) > thresholdValue);
        }

        if (filter.getThresholdType() == ThresholdType.PERCENTAGE) // requirement: 3.7.3.l
        {
            return (Math.abs(previousValueDouble - currentValueDouble) / previousValueDouble * 100 > thresholdValue);
        }

        return false;
    }

    /**
     * creates a new ParmeterSample and saves it to the internal list. No filter
     * will be checked.
     *
     * @param identityId The identity Id.
     * @param indexOfparameterSet
     */
    protected void sampleParam(Long identityId, int indexOfparameterSet) {
        this.sampleParam(identityId, indexOfparameterSet, false, null);
    }

    /**
     * creates a new ParmeterSample or uses the given samples and saves it to
     * the internal list. No filter will be checked.
     *
     * @param identityId The identity Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter is periodic and the aggregation period is up.
     * @param newAggrParameterValueSamples the new parameterSamples to be set.
     * if null new parameterSamples will be generated
     */
    public void sampleParam(Long identityId, int indexOfparameterSet, boolean aggrExpired,
            AggregationParameterValueList newAggrParameterValueSamples) {
        final AggregationDefinitionDetails aggrDef = this.getAggregationDefinition(identityId);

        if (newAggrParameterValueSamples == null) {
            final LongList parameters = aggrDef.getParameterSets().get(indexOfparameterSet).getParameters();
            //not using the aggrExpired value here but the "false" value, is because the actual check 
            //for expired value will be done a bit furhter down this code. Ther ParameterService should 
            //still hold the "interface" to manually check for the expired value

            //If sendDefinition is TRUE reports will include the ParameterDefinition object instance 
            //identifier in the AggregationParameterValue, if FALSE it will be set to NULL.
            newAggrParameterValueSamples = this.sampleParameters(parameters, aggrExpired, aggrDef.getSendDefinitions());
        }
//        //check for expired values if the aggregation-period was expired
        if (aggrExpired) {
            newAggrParameterValueSamples = checkForExpiredValues(identityId, indexOfparameterSet,
                    aggrDef.getParameterSets().get(indexOfparameterSet), newAggrParameterValueSamples);
        }
        //increment the number of samples for this ParameterSet
        this.sampleCountList.get(identityId).set(indexOfparameterSet, this.sampleCountList.get(identityId).get(indexOfparameterSet) + 1);
        this.setParameterSamplesInternally(identityId, indexOfparameterSet, newAggrParameterValueSamples);
    }

    /**
     * samples new values for the given parameterSet, check if these trigger the
     * filter and if its triggered, saves these in the internal list. This
     * method doesnt publish the new values.
     *
     * @param identityId The identity Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at
     */
    public void sampleAndFilterParam(Long identityId, int indexOfparameterSet) {
        this.sampleAndFilterParam(identityId, indexOfparameterSet, false, null);
    }

    /**
     * check if the newParameterValueSamples trigger the filter and if its
     * triggered, saves these in the internal list. This method doesnt publish
     * the new values.
     *
     * @param identityId The identity Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter is periodic and the aggregation period is up.
     * @param newParameterValueSamples the newParameterValueSamples to be set.
     * if null new parameterSamples will be generated
     */
    public void sampleAndFilterParam(Long identityId, int indexOfparameterSet, boolean aggrExpired, AggregationParameterValueList newParameterValueSamples) {
        final AggregationDefinitionDetails aggrDef = this.getAggregationDefinition(identityId);
        final AggregationParameterSet aggrParamSet = aggrDef.getParameterSets().get(indexOfparameterSet);
        // Add another sample on the AggregationValue that will be returned later:
        final AggregationParameterValueList currentUpdateValues = this.getCurrentUpdateValue(identityId, indexOfparameterSet);
        if (newParameterValueSamples == null) {
            //not using the aggrExpired value here but the "false" value, is because the actual 
            //check for expired value will be done a bit furhter down this code. Ther ParameterService 
            //should still hold the "interface" to manually check for the expired value
            newParameterValueSamples = this.sampleParameters(aggrParamSet.getParameters(), false, aggrDef.getSendDefinitions());
        }
        //increment the number of samples for this ParameterSet
        this.sampleCountList.get(identityId).set(indexOfparameterSet, this.sampleCountList.get(identityId).get(indexOfparameterSet) + 1);

//        //check for expired values if the aggregation-period was expired
        if (aggrExpired) {
            newParameterValueSamples = checkForExpiredValues(identityId, indexOfparameterSet, aggrParamSet, newParameterValueSamples);
        }
        //no samples saved yet
        if (currentUpdateValues == null) {
            //first sample, set internally and trigger filter
            this.setParameterSamplesInternally(identityId, indexOfparameterSet, newParameterValueSamples);
            this.setFilterTriggered(identityId, true);
            return;
        }
        //requirement: 3.7.3.k
        if (aggrDef.getFilterEnabled()) {
            boolean filterIsTriggered = this.checkFilterIsTriggered(aggrParamSet, currentUpdateValues, newParameterValueSamples);
            if (filterIsTriggered) {
                this.setParameterSamplesInternally(identityId, indexOfparameterSet, newParameterValueSamples);
                this.setFilterTriggered(identityId, filterIsTriggered);
            }
        } else {
            this.setParameterSamplesInternally(identityId, indexOfparameterSet, newParameterValueSamples);
            this.setFilterTriggered(identityId, false);
        }
    }

    /**
     * if the aggregation periodic expired, the parameters is periodic and the
     * value didnt change then the parameter that will be saved must be of the
     * validity-state: EXPIRED
     *
     * @param identityId The identity Id.
     * @param indexOfparameterSet
     * @param aggrParamSet
     * @param newParameterValueSamples
     * @return
     */
    private AggregationParameterValueList checkForExpiredValues(Long identityId, int indexOfparameterSet,
            AggregationParameterSet aggrParamSet, AggregationParameterValueList newParameterValueSamples) {
        final AggregationParameterValueList currentParamValues = this.periodicAggregationValuesCurrent.get(identityId).getParameterSetValues().get(indexOfparameterSet).getValues();
        //requirement: 3.3.3.i (ParameterService-requirement)
        //if sendUnchanged is true: replace validity-state with an EXPIRED state
        //todo: try to let the ParameterService set the EXPIRED state and not the AggregationService
        if (currentParamValues != null) {
            for (int k = 0; k < currentParamValues.size(); k++) {
                final ParameterDefinitionDetails paramDef = parameterManager.getParameterDefinition(aggrParamSet.getParameters().get(k));
                if (paramDef.getGenerationEnabled() && paramDef.getReportInterval().getValue() != 0) {
                    if (currentParamValues.get(k).getValue().getRawValue().equals(newParameterValueSamples.get(k).getValue().getRawValue())) {
                        newParameterValueSamples.get(k).getValue().setValidityState(new UOctet(new Short("" + ValidityState.EXPIRED_NUM_VALUE)));
                    }
                }
            }
        }
        return newParameterValueSamples;
    }

    /**
     * gets the current aggregation value from the internal list. if the update
     * is not periodically so adhoc or filtered, or the sampleInterval is greater or equal to the
     * updateinterval, then this method sets the time-intervals at the returned
     * samples.
     *
     * @param identityIds
     * @param generationMode the mode of the generation (PERIODIC, ADHOC,
     * FILTEREDTIMEOUT)
     * @return the most recent values
     */
    public AggregationValueList getAggregationValuesList(LongList identityIds, GenerationMode generationMode) {
        AggregationValueList aValList = new AggregationValueList();
        for (Long identityId : identityIds) {

            aValList.add(getAggregationValue(identityId, generationMode));
        }

        return aValList;
    }

    /**
     * creates new samples and returns its values. no filter, no sendUnchanged
     * will be considered and it does not interfere with other adhoc or periodic
     * updates.
     *
     * @param identityId
     * @return
     */
    public AggregationValue getValue(Long identityId) {
        AggregationValue aVal = new AggregationValue();
        AggregationDefinitionDetails aggrDef = this.getAggregationDefinition(identityId);
        AggregationParameterSetList parameterSets = aggrDef.getParameterSets();
        AggregationSetValueList parameterSetValues = new AggregationSetValueList();

        //fill AggregationSetValue-objects for each parameterSet
        for (int j = 0; j < parameterSets.size(); j++) {   //Cycle through the parameterSets (requirement: 3.7.3.n)
            AggregationParameterValueList sampleParameters = this.sampleParameters(aggrDef.getParameterSets().get(j).getParameters(), false, aggrDef.getSendDefinitions());
            AggregationSetValue aggrSetValue = new AggregationSetValue(null, null, sampleParameters);
            parameterSetValues.add(aggrSetValue);
        }

        aVal.setParameterSetValues(parameterSetValues);
        aVal.setGenerationMode(GenerationMode.ADHOC);
        aVal.setFiltered(false);
        return aVal;
    }

    /**
     * gets the current aggregation value from the internal list. if the update
     * is not periodically so adhoc or filtered, or the sampleInterval is greater or equal to the
     * updateinterval, then this method sets the time-intervals at the returned
     * samples.
     *
     * @param identityId
     * @param generationMode the mode of the generation (PERIODIC, ADHOC,
     * FILTEREDTIMEOUT)
     * @return the most recent values
     */
    public AggregationValue getAggregationValue(Long identityId, GenerationMode generationMode) {
        AggregationValue aVal = new AggregationValue();
        AggregationDefinitionDetails aggrDef = this.getAggregationDefinition(identityId);
        AggregationParameterSetList parameterSets = aggrDef.getParameterSets();
        AggregationSetValueList parameterSetValues = new AggregationSetValueList();

        //fill AggregationSetValue-objects for each parameterSet
        for (int j = 0; j < parameterSets.size(); j++) {  //Cycle through the parameterSets (requirement: 3.7.3.r)
            final Duration sampleInterval = parameterSets.get(j).getSampleInterval();
            final Duration updateInterval = aggrDef.getReportInterval();
            //calculate the the new aggregation-values interval times

            AggregationParameterValueList val = evaluateSendUnchanged(aggrDef, identityId, j);

            if (val != null) {
                AggregationSetValue parameterSetValue = calcAggrSetValueTimes(generationMode, sampleInterval, updateInterval, identityId, j);
                //requirement: 3.7.3.q if unchanged values should be sent with a value replaced by a null, then replace them 
                //set the values of the parameter-set that will be published
                parameterSetValue.setValues(val);
                //add the current parameterSet to the current parameterSetList
                parameterSetValues.add(parameterSetValue);
            }
        }
        //set the current parameterSetList as the current aggregation vakues
        aVal.setParameterSetValues(parameterSetValues);
        aVal.setGenerationMode(generationMode);
        aVal.setFiltered(isFilterTriggered(identityId));
        return aVal;
    }

    /**
     * sets the delta-time and the update time at the sampled value.
     *
     *
     * @param generationMode
     * @param sampleInterval
     * @param updateInterval
     * @param identityId
     * @param indexParameterSet
     * @param parameterSetValue
     * @return
     */
    private AggregationSetValue calcAggrSetValueTimes(GenerationMode generationMode,
            Duration sampleInterval, Duration updateInterval, Long identityId, int indexParameterSet) {
        //periodic updates should get the value from the last sampled value
        AggregationSetValue parameterSetValue = new AggregationSetValue();
        if (generationMode == GenerationMode.PERIODIC
                && sampleInterval.getValue() != 0
                && sampleInterval.getValue() < updateInterval.getValue()) {
            //calculate the intervals
            Time currentTime = HelperTime.getTimestampMillis();
//            Time AggTimeStamp = new Time(currentTime.getValue() - (long) updateInterval.getValue() * 1000);
//            Time firstSampleTime = new Time(this.latestSampleTimeList.get(identityId).get(indexParameterSet).getValue());
            Time previousSetTimeStamp;
            if (indexParameterSet == 0) { //if its the first Set, the reference-time is the start of this aggregation-update
                previousSetTimeStamp = new Time(currentTime.getValue() - (long) (updateInterval.getValue() * 1000));
            } else { //otherwise its the time of the last value of the previous set
                previousSetTimeStamp = new Time(this.latestSampleTimeList.get(identityId).get(indexParameterSet - 1).getValue());
            }

            Time firstSampleTime = new Time(this.latestSampleTimeList.get(identityId).get(indexParameterSet).getValue()
                    - (long) (sampleInterval.getValue() * 1000) * sampleCountList.get(identityId).get(indexParameterSet));

            // Delta-TIme =  firstSampleTime(Setx) - (firstSampleTime(Setx-1) + y*sampleInterval) | y = amount of updates.
            Duration deltaTime = new Duration(((float) (firstSampleTime.getValue() - previousSetTimeStamp.getValue())) / 1000);
            // Duration is in seconds but Time is in miliseconds
            parameterSetValue.setDeltaTime(deltaTime);
            parameterSetValue.setIntervalTime(sampleInterval);
        } else {  // a new sample should be generated (if the generationMode is ADHOC or FILTEREDTIMEOUT, or the sampleInterval is out of the updateInterval range)
            parameterSetValue.setDeltaTime(null);
            parameterSetValue.setIntervalTime(null);
        }
        return parameterSetValue;
    }

    /**
     * checks if the an aggregation expects to get unchanged values as the
     * actual value or as the null value, and replaces these with a null if
     * necessary.
     *
     * @param aggrDef the aggregation definition that will be checked if it
     * expects you to send unchanged values
     * @param identityId the id of the aggregation identity
     * @param indexParameterSet the index of the parameter-set
     * @return
     */
    private AggregationParameterValueList evaluateSendUnchanged(AggregationDefinitionDetails aggrDef,
            Long identityId, int indexParameterSet) { //requirement: 3.7.3.m
        return evaluateSendUnchanged(aggrDef, identityId, indexParameterSet, null);
    }

    /**
     * checks if the an aggregation expects to get unchanged values as the
     * actual value or as the null value, and replaces these with a null if
     * necessary.
     *
     * @param aggrDef the aggregation definition that will be checked if it
     * expects you to send unchanged values
     * @param identityId the id of the aggregation identity
     * @param indexParameterSet the index of the parameter-set
     * @param currentParamValues the new paramValues the old param-values should
     * be compare with
     * @return
     */
    private AggregationParameterValueList evaluateSendUnchanged(AggregationDefinitionDetails aggrDef,
            Long identityId, int indexParameterSet, AggregationParameterValueList currentParamValues) { //requirement: 3.7.3.m
        AggregationParameterValueList retParamValues = new AggregationParameterValueList();
        if (currentParamValues == null) {
            currentParamValues = this.periodicAggregationValuesCurrent.get(identityId).getParameterSetValues().get(indexParameterSet).getValues();
        }
        final AggregationParameterValueList lastParamValues = getLastUpdateValue(identityId, indexParameterSet);
        if (!aggrDef.getSendUnchanged() && lastParamValues != null) {
            for (int k = 0; k < currentParamValues.size(); k++) { //requirement 3.7.3.n cyclying through for loop ensures that
                //3.7.3.q if unchanged replace AggregationParameterValue-Object by a NULL
                if (currentParamValues.get(k).equals(lastParamValues.get(k))) {
                    retParamValues.add(k, null);
                } else {
                    retParamValues.add(k, currentParamValues.get(k));
                }
            }
        } else {
            retParamValues = currentParamValues;
        }
        return retParamValues;
    }

    /**
     * checks if the filter is triggered, comparing the previous and
     * currentvalues.
     *
     * @param aggregationDefinition
     * @param aggregationParameterSet
     * @param previousUpdateValue
     * @param currentParameterValue
     * @return if the filter is triggered, because the values of the parameters
     * have changed more than a given threshold. Also returns true, if there is
     * no periodic filter or it contains more than one parameter.
     */
    private boolean checkFilterIsTriggered(final AggregationParameterSet aggregationParameterSet,
            final AggregationParameterValueList previousUpdateValue, final AggregationParameterValueList currentParameterValue) {
        // Filter Comparison Process
        if (aggregationParameterSet.getReportFilter() == null
                || aggregationParameterSet.getParameters().size() != 1) { // requirement: 3.7.3.m (and 4.7.5: periodicFilter comment) 
            //let the update being published
            return true;
        }

        // In theory all the list should be null with the exception of the Parameter Value we want
        // because size = 1 and the remaining Parameter Values inside get the null state
        // So we can crawl the list until we find the first non-null element and compare it with the previousParameterValue
        for (int i = 0; i < currentParameterValue.size(); i++) {
            ParameterValue current = currentParameterValue.get(i).getValue();
            ParameterValue previous = previousUpdateValue.get(i).getValue();

            if (current != null && previous != null) {
                // Compare the values:
                if ((current.getValidityState().getValue() == 0 && previous.getValidityState().getValue() == 0)
                        || // Are the parameters valid?
                        (current.getValidityState().getValue() == 2 && previous.getValidityState().getValue() == 2)) { // 2 stands for the INVALID_RAW state

                    boolean filterisTriggered = false;
                    if (current.getValidityState().getValue() == 0 && previous.getValidityState().getValue() == 0
                            && current.getConvertedValue() != null && previous.getConvertedValue() != null) // requirement: 3.7.2.6
                    {
                        filterisTriggered = this.triggeredFilter(previous.getConvertedValue(), current.getConvertedValue(), aggregationParameterSet.getReportFilter());
                    }

                    if (current.getConvertedValue() == null && previous.getConvertedValue() == null) // requirement: 3.7.2.6
                    {
                        filterisTriggered = this.triggeredFilter(previous.getRawValue(), current.getRawValue(), aggregationParameterSet.getReportFilter());
                    }

                    return filterisTriggered;
                }

                break;
            }
        }
        return false;
    }

    /**
     * sets the value if the filter was triggered or not.
     *
     * @param identityId
     * @param bool the value if the filter was triggered
     * @return if a filter existed before
     */
    public Boolean setFilterTriggered(Long identityId, Boolean bool) {
//        this.isFilterTriggered.replace(objId, bool);
        boolean existed = (this.isFilterTriggered.remove(identityId) != null);

        if (existed) {
            this.isFilterTriggered.put(identityId, bool);
        }

        return existed;
    }

    /**
     * checks if the filter at the aggregation with the id was already triggered
     *
     * @param identityId
     * @return
     */
    public Boolean isFilterTriggered(Long identityId) {
        return this.isFilterTriggered.get(identityId);
    }

    /**
     * retrieves the values of an update that was published the time before the
     * last time.
     *
     * @param identityId
     * @param indexOfparameterSet
     * @return
     */
    private AggregationParameterValueList getLastUpdateValue(Long identityId, int indexOfparameterSet) {
        if (this.periodicAggregationValuesLast.get(identityId).getParameterSetValues() == null) // It was never sampled before?
        {
            return null;
        }

        if (this.periodicAggregationValuesLast.get(identityId).getParameterSetValues().get(indexOfparameterSet).getValues() == null) // It was never sampled before?
        {
            return null;
        }

        return this.periodicAggregationValuesLast.get(identityId).getParameterSetValues().get(indexOfparameterSet).getValues();

    }

    /**
     * retrieves the values of an update that was published the last time.
     *
     * @param identityId
     * @param indexOfparameterSet
     * @return
     */
    private AggregationParameterValueList getCurrentUpdateValue(Long identityId, int indexOfparameterSet) {
        if (this.periodicAggregationValuesCurrent.get(identityId).getParameterSetValues() == null) // It was never sampled before?
        {
            return null;
        }

        if (this.periodicAggregationValuesCurrent.get(identityId).getParameterSetValues().get(indexOfparameterSet).getValues() == null) // It was never sampled before?
        {
            return null;
        }

        return this.periodicAggregationValuesCurrent.get(identityId).getParameterSetValues().get(indexOfparameterSet).getValues();

    }

    /**
     * this method sets the new samples as the current values. If a Filter is
     * active, it must have checked already that the filter is triggered. if its
     * not triggered the samples must not be set.
     *
     * @param identityId the id of the aggregation
     * @param indexOfparameterSet the index of the parameterSet in an
     * aggregation
     * @param newParamSample the new values that should be set
     * @return
     */
    private AggregationParameterValueList setParameterSamplesInternally(Long identityId, int indexOfparameterSet, AggregationParameterValueList newParamSample) {

        final AggregationParameterValueList currentParamValues = this.periodicAggregationValuesCurrent.get(identityId).getParameterSetValues().get(indexOfparameterSet).getValues();
        this.periodicAggregationValuesLast.get(identityId).getParameterSetValues().get(indexOfparameterSet).setValues(currentParamValues);

        //set the new ones to the current ones
        this.periodicAggregationValuesCurrent.get(identityId).getParameterSetValues().get(indexOfparameterSet).setValues(newParamSample);
        //sets the timestamp of the latest value of the set. needed for the calculation of the delta-time
        this.latestSampleTimeList.get(identityId).set(indexOfparameterSet, HelperTime.getTimestampMillis());

        return newParamSample;
    }

    public ObjectInstancePair add(Identifier name, AggregationDefinitionDetails definition, ObjectId source, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5

        ObjectInstancePair newIdPair;
        if (super.getArchiveService() == null) {
            //add to providers local list
            uniqueObjIdIdentity++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newIdPair = new ObjectInstancePair(uniqueObjIdIdentity, uniqueObjIdDef);

        } else {
            try {
                //requirement: 3.7.10.2.f: if a AggregationName ever existed before, use the old AggregationIdentity-Object by retrieving it from the archive
                //check if the name existed before and retrieve id if found
                Long identityId = retrieveIdentityIdByNameFromArchive(ConfigurationProviderSingleton.getDomain(),
                        name, AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE);

                //in case the AggregationName never existed before, create a new identity
                if (identityId == null) {
                    IdentifierList names = new IdentifierList();
                    names.add(name);
                    //add to the archive
                    LongList identityIds = super.getArchiveService().store(true,
                            AggregationHelper.AGGREGATIONIDENTITY_OBJECT_TYPE,
                            ConfigurationProviderSingleton.getDomain(),
                            HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
                            names,
                            null);
                    identityId = identityIds.get(0);
                }

                //not matter if the Aggregation was created or loaded, a new definition will be created
                AggregationDefinitionDetailsList defs = new AggregationDefinitionDetailsList();
                defs.add(definition);
                LongList defIds = super.getArchiveService().store(true,
                        AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(identityId, source, connectionDetails),
                        defs,
                        null);

                //add to providers local list
                newIdPair = new ObjectInstancePair(identityId, defIds.get(0));

            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

        //add to internal lists
//        this.addIdentityDefinition(newIdPair.getObjIdentityInstanceId(), name, newIdPair.getObjDefInstanceId(), definition);
        this.addIdentityDefinition(name, newIdPair, definition);
        final LongList identities = new LongList();
        identities.add(newIdPair.getObjIdentityInstanceId());
        this.createAggregationValuesList(identities);

        return newIdPair;
    }

    /**
     * updates an existing aggregation-definition with the given
     * definition-details.
     *
     * @param identityId the id of the identity the definition belongs to
     * @param definition the new definition-details
     * @param source the ObjectId of the source-object that cause the update to
     * be created
     * @param connectionDetails
     * @return the id of the new definition
     */
    public Long update(Long identityId, AggregationDefinitionDetails definition, ObjectId source, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        Long newDefId = null;

        if (super.getArchiveService() == null) { //only update locally
            //add to providers local list
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newDefId = uniqueObjIdDef;

        } else {  // update in the COM Archive
            try {
                AggregationDefinitionDetailsList defs = new AggregationDefinitionDetailsList();
                defs.add(definition);

                //requirement 3.7.6.a
                LongList defIds = super.getArchiveService().store(true,
                        AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(identityId, source, connectionDetails), //requirement: 3.7.4.d, h
                        defs,
                        null);

                newDefId = defIds.get(0);
            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(AggregationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //update internal lists
        this.updateDef(identityId, newDefId, definition);

        return newDefId;
    }

    public boolean delete(Long identityId) {

        if (!this.deleteIdentity(identityId)) {
            return false;
        }

        periodicAggregationValuesLast.remove(identityId);
        periodicAggregationValuesCurrent.remove(identityId);
        latestSampleTimeList.remove(identityId);

        return true;
    }

    public Long setGenerationEnabled(Long identityId, Boolean bool, ObjectId source, SingleConnectionDetails connectionDetails) {
        AggregationDefinitionDetails def = this.getAggregationDefinition(identityId);

        if (def == null) {
            return null;
        }
        //requirement: 3.7.9.2.f    
        if (def.getGenerationEnabled().booleanValue() == bool) // Is it set with the requested value already?
        {
            return identityId; // the value was not changed
        }
        def.setGenerationEnabled(bool);
        //requirement: 3.7.9.2.j, k
        return this.update(identityId, def, source, connectionDetails);
    }

    public void setGenerationEnabledAll(Boolean bool, ObjectId source, SingleConnectionDetails connectionDetails) {
        LongList identityIds = this.listAllIdentities();

        for (Long identityId : identityIds) {
            AggregationDefinitionDetails def = this.getAggregationDefinition(identityId);
            if (def.getGenerationEnabled().booleanValue() != bool) {
                def.setGenerationEnabled(bool);
                this.update(identityId, def, source, connectionDetails);
            }
        }
    }

    /**
     * sets the value to set the filter enabled or not
     *
     * @param identityId
     * @param bool the value if the filter should be enabled
     * @param source
     * @param connectionDetails
     * @return true if it was set successfully, false if it wasnt set.
     */
    public boolean setFilterEnabled(Long identityId, Boolean bool, ObjectId source, SingleConnectionDetails connectionDetails) {

        AggregationDefinitionDetails def = this.getAggregationDefinition(identityId);

        if (def == null) {
            return false;
        }
        //requirement: 3.7.10.2.f    
        if (def.getFilterEnabled().booleanValue() == bool) // Is it set with the requested value already?
        {
            return false; // the value was not changed
        }
        def.setFilterEnabled(bool);

        //requirement: 3.7.10.2.j
        this.update(identityId, def, source, connectionDetails);
        return true;
    }

    public void setFilterEnabledAll(Boolean bool, ObjectId source, SingleConnectionDetails connectionDetails) {
        LongList identityIds = this.listAllIdentities();

        for (Long identityId : identityIds) {
            AggregationDefinitionDetails def = this.getAggregationDefinition(identityId);
            if (def.getFilterEnabled().booleanValue() != bool) {
                def.setFilterEnabled(bool);
                this.update(identityId, def, source, connectionDetails);
            }
        }
    }

    /*
    public DurationList getProvidedIntervals() {
        return parameterManager.getProvidedIntervals();
    }
     */
}
