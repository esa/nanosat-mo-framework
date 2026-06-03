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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.aggregation.AggregationServiceInfo;
import org.ccsds.moims.mo.mc.structures.*;

/**
 *
 * @author Cesar Coelho
 */
public final class AggregationManager extends MCManager {

    private final HashMap<Long, Boolean> isFilterTriggered; // DefId, Boolean Value that determines if the filter was triggered
    private final HashMap<Long, AggregationValue> aggValuesLast; // DefId, Aggregation Value List per definition
    private final HashMap<Long, AggregationValue> aggValuesCurrent; // DefId, Aggregation Value List per definition
    private final HashMap<Long, TimeList> latestSampleTimeList; // DefId, Time of the first sample of each ParameterSetValue
    private final HashMap<Long, ArrayList<Integer>> sampleCountList; // DefId, Amount of samples per parameterSetValue

    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdAVal;
    private final ParameterManager parameterManager;

    public AggregationManager(COMServicesProvider comServices, ParameterManager parameterManager) {
        super(comServices);
        this.parameterManager = parameterManager;

        this.isFilterTriggered = new HashMap<>();
        this.aggValuesLast = new HashMap<>();
        this.aggValuesCurrent = new HashMap<>();
        this.latestSampleTimeList = new HashMap<>();
        this.sampleCountList = new HashMap<>();

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = 0L; // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdAVal = 0L; // The zeroth value will not be used (reserved for the wildcard)
        }
    }

    public AggregationDefinition getAggregationDefinition(Long id) {
        return (AggregationDefinition) this.getDefinition(id);
    }

    /**
     * Initializes the variables in the manager belonging the updates and
     * samples of the given aggregation. this method will be called after the
     * creation of an aggregation-definition
     *
     * @param defIdList The list of definition Ids
     */
    protected void createAggregationValuesList(LongList defIdList) {
        for (Long defId : defIdList) {
            aggValuesLast.put(defId, new AggregationValue());
            aggValuesCurrent.put(defId, new AggregationValue());
            isFilterTriggered.put(defId, false);
            //initialize timList with the amount of parameterSets
            //final int parameterSetsCount = getAggregationDefinition(defId).getParameterSets().size();
            latestSampleTimeList.put(defId, new TimeList());
            sampleCountList.put(defId, new ArrayList<>());

            this.populateAggregationValues(defId);
        }
    }

    /**
     * resets periodicAggregationValuesLast, periodicAggregationValuesCurrent,
     * sampleCounter, latestSampleTimeList and the filterTriggered-variables.
     * this method will be called after an aggregation-definition was updated.
     *
     * @param defId The definition Id.
     */
    public void populateAggregationValues(final Long defId) {
        AggregationSetValueList aggregationSetValueListLast = new AggregationSetValueList();
        AggregationSetValueList aggregationSetValueListCurrent = new AggregationSetValueList();
        AggregationDefinition definition = this.getAggregationDefinition(defId);
        final int paramSetSize = definition.getParameterSets().size();
        //reset the latest sample-time and the sample-counter
        if (paramSetSize != 0) {
            latestSampleTimeList.get(defId).clear();
            sampleCountList.get(defId).clear();
        }
        //init the latest sample-time and the sample-counter, filter
        // triggered and latest and current aggregation-values
        for (int j = 0; j < paramSetSize; j++) {
            latestSampleTimeList.get(defId).add(j, null);
            aggregationSetValueListLast.add(j, new AggregationSetValue());
            aggregationSetValueListCurrent.add(j, new AggregationSetValue());
            sampleCountList.get(defId).add(j, 0);
        }

        AggregationValue last = aggValuesLast.get(defId);
        AggregationValue current = aggValuesCurrent.get(defId);

        aggValuesLast.put(defId,
                new AggregationValue(last.getGenerationMode(), last.getFiltered(), aggregationSetValueListLast));
        aggValuesCurrent.put(defId,
                new AggregationValue(current.getGenerationMode(), current.getFiltered(), aggregationSetValueListCurrent));

        //aggValuesLast.get(defId).setParameterSetValues(aggregationSetValueListLast);
        //aggValuesCurrent.get(defId).setParameterSetValues(aggregationSetValueListCurrent);
        this.setFilterTriggered(defId, false);  // Reset the filter state
    }

    /**
     * resets samplecounter, latestsampletimelist and the
     * filtertriggered-variables. this method will be called after each
     * aggregation-value-update was published
     *
     * @param defId The definition Id.
     * @return true if it was successful, false if identity not found
     */
    public Boolean resetAggregationSampleHelperVariables(Long defId) {
        if (!this.existsDef(defId)) {
            return false;
        }
        AggregationDefinition definition = this.getAggregationDefinition(defId);
        final int paramSetSize = definition.getParameterSets().size();
        //reset the latest sample-time and the sample-counter
        if (paramSetSize != 0) {
            latestSampleTimeList.get(defId).clear();
            sampleCountList.get(defId).clear();
        }
        //init the latest sample-time and the sample-counter
        for (int j = 0; j < paramSetSize; j++) {
            latestSampleTimeList.get(defId).add(j, null);
            sampleCountList.get(defId).add(j, 0);
        }
        this.setFilterTriggered(defId, false);  // Reset the filter state
        return true;
    }

    /**
     * removes the values of the aggregation with the given id from the internal
     * lists
     *
     * @param defId The definition Id.
     */
    public void removeAggregationValues(Long defId) {
        aggValuesLast.remove(defId);
        aggValuesCurrent.remove(defId);
        latestSampleTimeList.remove(defId);
        isFilterTriggered.remove(defId);
        sampleCountList.remove(defId);
    }

    public Long storeAndGenerateAValobjId(AggregationValue aVal, Long related,
            ObjectKey source, URI uri, Time timestamp) {
        if (super.getArchiveService() == null) {
            uniqueObjIdAVal++;
            return this.uniqueObjIdAVal;
        } else {
            HeterogeneousList aValList = new HeterogeneousList();
            aValList.add(aVal);

            try {
                //requirement: 3.7.4.d, 3.7.6.b
                LongList objIds = super.getArchiveService().store(
                        true,
                        AggregationServiceInfo.AGGREGATIONVALUE_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(related, source, uri),
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
     * @return ParameterValue of the parameter, or an empty ParameterValue with
     * INVALID state if parameter cannot be retrieved
     */
    private ParameterValue sampleParameter(Long paramIdentityId, boolean aggrExpired) {
        try {
            return parameterManager.getParameterValue(paramIdentityId, aggrExpired);
        } catch (MALInteractionException ex) {
            return new ParameterValue(ValidityState.INVALID_RAW, null, null);
        }
    }

    /**
     * This method is created for the field:
     * AggregationDefinition.sendDefinitions. If
     * AggregationDefinition.sendDefinitions is TRUE, reports will include the
     * ParameterDefinition object instance identifier in the
     * AggregationParameterValue, if FALSE it will be set to NULL.
     *
     * @param paramIds the id of the parameters to be sampled
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter, is periodic and the update hasnt been received in
     * the aggregation-period. if true, the validity-state of the new parameter
     * will be expired.
     * @return
     */
    private AggregationParameterValueList sampleParameters(LongList paramIds,
            boolean aggrExpired, boolean sendDefinitions) {
        AggregationParameterValueList aggrPValList = new AggregationParameterValueList();

        for (Long id : paramIds) {
            final Long paramDefId = sendDefinitions ? id : null;
            final ParameterValue paramValue = sampleParameter(id, aggrExpired);
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
     * @param defId The definition Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at.
     */
    protected void sampleParam(Long defId, int indexOfparameterSet) {
        this.sampleParam(defId, indexOfparameterSet, false, null);
    }

    /**
     * creates a new ParmeterSample or uses the given samples and saves it to
     * the internal list. No filter will be checked.
     *
     * @param defId The definition Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at.
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter is periodic and the aggregation period is up.
     * @param newAggrParameterValueSamples the new parameterSamples to be set.
     * if null new parameterSamples will be generated
     */
    public void sampleParam(Long defId, int indexOfparameterSet, boolean aggrExpired,
            AggregationParameterValueList newAggrParameterValueSamples) {
        final AggregationDefinition aggrDef = this.getAggregationDefinition(defId);

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
            newAggrParameterValueSamples = checkForExpiredValues(defId, indexOfparameterSet,
                    aggrDef.getParameterSets().get(indexOfparameterSet),
                    newAggrParameterValueSamples);
        }
        //increment the number of samples for this ParameterSet
        this.sampleCountList.get(defId).set(indexOfparameterSet, this.sampleCountList.get(defId).get(
                indexOfparameterSet) + 1);
        this.setParameterSamplesInternally(defId, indexOfparameterSet, newAggrParameterValueSamples);
    }

    /**
     * samples new values for the given parameterSet, check if these trigger the
     * filter and if its triggered, saves these in the internal list. This
     * method doesnt publish the new values.
     *
     * @param defId The definition Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at
     */
    public void sampleAndFilterParam(Long defId, int indexOfparameterSet) {
        this.sampleAndFilterParam(defId, indexOfparameterSet, false, null);
    }

    /**
     * check if the newParameterValueSamples trigger the filter and if its
     * triggered, saves these in the internal list. This method doesnt publish
     * the new values.
     *
     * @param defId The definition Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at
     * @param aggrExpired should be set to true, if the aggregation that is
     * sampling the parameter is periodic and the aggregation period is up.
     * @param newParameterValueSamples the newParameterValueSamples to be set.
     * if null new parameterSamples will be generated
     */
    public void sampleAndFilterParam(Long defId, int indexOfparameterSet, boolean aggrExpired,
            AggregationParameterValueList newParameterValueSamples) {
        final AggregationDefinition aggrDef = this.getAggregationDefinition(defId);
        final AggregationParameterSet aggrParamSet = aggrDef.getParameterSets().get(indexOfparameterSet);
        // Add another sample on the AggregationValue that will be returned later:
        final AggregationParameterValueList currentUpdateValues = this.getCurrentUpdateValue(defId,
                indexOfparameterSet);
        if (newParameterValueSamples == null) {
            //not using the aggrExpired value here but the "false" value, is because the actual 
            //check for expired value will be done a bit furhter down this code. Ther ParameterService 
            //should still hold the "interface" to manually check for the expired value
            newParameterValueSamples = this.sampleParameters(aggrParamSet.getParameters(),
                    false, aggrDef.getSendDefinitions());
        }
        //increment the number of samples for this ParameterSet
        this.sampleCountList.get(defId).set(indexOfparameterSet, this.sampleCountList.get(defId).get(
                indexOfparameterSet) + 1);

        //        //check for expired values if the aggregation-period was expired
        if (aggrExpired) {
            newParameterValueSamples = checkForExpiredValues(defId,
                    indexOfparameterSet, aggrParamSet, newParameterValueSamples);
        }
        //no samples saved yet
        if (currentUpdateValues == null) {
            //first sample, set internally and trigger filter
            this.setParameterSamplesInternally(defId, indexOfparameterSet, newParameterValueSamples);
            this.setFilterTriggered(defId, true);
            return;
        }
        //requirement: 3.7.3.k
        if (aggrDef.getFilterEnabled()) {
            boolean filterIsTriggered = this.checkFilterIsTriggered(aggrParamSet, currentUpdateValues,
                    newParameterValueSamples);
            if (filterIsTriggered) {
                this.setParameterSamplesInternally(defId, indexOfparameterSet, newParameterValueSamples);
                this.setFilterTriggered(defId, filterIsTriggered);
            }
        } else {
            this.setParameterSamplesInternally(defId, indexOfparameterSet, newParameterValueSamples);
            this.setFilterTriggered(defId, false);
        }
    }

    /**
     * if the aggregation periodic expired, the parameters is periodic and the
     * value didn't change then the parameter that will be saved must be of the
     * validity-state: EXPIRED
     *
     * @param defId The definition Id.
     * @param indexOfparameterSet The index in the aggregation to be checked.
     * @param aggrParamSet The aggregation parameter set.
     * @param newParameterValueSamples The new parameter value samples.
     * @return
     */
    private AggregationParameterValueList checkForExpiredValues(Long defId, int indexOfparameterSet,
            AggregationParameterSet aggrParamSet, AggregationParameterValueList newParameterValueSamples) {
        final AggregationParameterValueList currentParamValues =
                this.aggValuesCurrent.get(defId).getParameterSetValues().get(indexOfparameterSet).getValues();
        //requirement: 3.3.3.i (ParameterService-requirement)
        //if sendUnchanged is true: replace validity-state with an EXPIRED state
        //todo: try to let the ParameterService set the EXPIRED state and not the AggregationService
        if (currentParamValues == null) {
            return newParameterValueSamples;
        }

        for (int k = 0; k < currentParamValues.size(); k++) {
            Long id = aggrParamSet.getParameters().get(k);
            ParameterDefinition paramDef = parameterManager.getParameterDefinition(id);

            if (paramDef.getReportingEnabled() && paramDef.getReportInterval().getInSeconds() != 0) {
                AggregationParameterValue agg = newParameterValueSamples.get(k);
                ParameterValue pVal = agg.getValue();
                if (currentParamValues.get(k).getValue().getRawValue().equals(pVal.getRawValue())) {
                    ParameterValue newValue = new ParameterValue(ValidityState.EXPIRED, pVal.getRawValue(), pVal.getConvertedValue());
                    newParameterValueSamples.add(k, new AggregationParameterValue(newValue, agg.getParamDefinitionId()));
                }
            }
        }
        return newParameterValueSamples;
    }

    /**
     * Returns the current aggregation value from the internal list. if the
     * update is not periodically so adhoc or filtered, or the sampleInterval is
     * greater or equal to the updateinterval, then this method sets the
     * time-intervals at the returned samples.
     *
     * @param defIds The identity Ids.
     * @param generationMode the mode of the generation (PERIODIC, ADHOC,
     * FILTEREDTIMEOUT)
     * @return the most recent values
     */
    public AggregationValueList getAggregationValuesList(LongList defIds, GenerationMode generationMode) {
        AggregationValueList aValList = new AggregationValueList();
        for (Long defId : defIds) {

            aValList.add(getAggregationValue(defId, generationMode));
        }

        return aValList;
    }

    /**
     * creates new samples and returns its values. no filter, no sendUnchanged
     * will be considered and it does not interfere with other adhoc or periodic
     * updates.
     *
     * @param defId The definition Id.
     * @return The Aggregation Value.
     */
    public AggregationValue getValue(Long defId) {
        AggregationDefinition aggrDef = this.getAggregationDefinition(defId);
        AggregationParameterSetList parameterSets = aggrDef.getParameterSets();
        AggregationSetValueList parameterSetValues = new AggregationSetValueList();

        //fill AggregationSetValue-objects for each parameterSet
        for (int j = 0; j < parameterSets.size(); j++) {   //Cycle through the parameterSets (requirement: 3.7.3.n)
            AggregationParameterValueList sampleParameters = this.sampleParameters(
                    aggrDef.getParameterSets().get(j).getParameters(),
                    false, aggrDef.getSendDefinitions());
            AggregationSetValue aggrSetValue = new AggregationSetValue(null, null, sampleParameters);
            parameterSetValues.add(aggrSetValue);
        }

        return new AggregationValue(GenerationMode.ADHOC, false, parameterSetValues);
    }

    /**
     * gets the current aggregation value from the internal list. if the update
     * is not periodically so adhoc or filtered, or the sampleInterval is
     * greater or equal to the updateinterval, then this method sets the
     * time-intervals at the returned samples.
     *
     * @param defId The definition Id.
     * @param generationMode the mode of the generation (PERIODIC, ADHOC,
     * FILTEREDTIMEOUT)
     * @return the most recent values
     */
    public AggregationValue getAggregationValue(Long defId, GenerationMode generationMode) {
        AggregationDefinition aggrDef = this.getAggregationDefinition(defId);
        AggregationParameterSetList parameterSets = aggrDef.getParameterSets();
        AggregationSetValueList parameterSetValues = new AggregationSetValueList();

        //fill AggregationSetValue-objects for each parameterSet
        for (int j = 0; j < parameterSets.size(); j++) {  //Cycle through the parameterSets (requirement: 3.7.3.r)
            final Duration sampleInterval = parameterSets.get(j).getSampleInterval();
            final Duration updateInterval = aggrDef.getReportInterval();
            //calculate the the new aggregation-values interval times

            AggregationParameterValueList val = evaluateSendUnchanged(aggrDef, defId, j);

            if (val != null) {
                AggregationSetValue parameterSetValue = calcAggrSetValueTimes(generationMode,
                        sampleInterval, updateInterval, defId, j, val);
                //requirement: 3.7.3.q if unchanged values should be sent with a value replaced by a null, then replace them 
                //add the current parameterSet to the current parameterSetList
                parameterSetValues.add(parameterSetValue);
            }
        }
        //set the current parameterSetList as the current aggregation values
        return new AggregationValue(generationMode, isFilterTriggered(defId), parameterSetValues);

    }

    /**
     * Sets the delta-time and the update time at the sampled value.
     *
     *
     * @param generationMode the mode of the generation (PERIODIC, ADHOC,
     * FILTEREDTIMEOUT)
     * @param sampleInterval
     * @param updateInterval
     * @param defId The definition Id.
     * @param indexParameterSet
     * @param parameterSetValue
     * @return
     */
    private AggregationSetValue calcAggrSetValueTimes(GenerationMode generationMode,
            Duration sampleInterval, Duration updateInterval, Long defId,
            int indexParameterSet, AggregationParameterValueList val) {
        //periodic updates should get the value from the last sampled value
        if (generationMode == GenerationMode.PERIODIC
                && sampleInterval.getInSeconds() != 0
                && sampleInterval.getInSeconds() < updateInterval.getInSeconds()) {
            //calculate the intervals
            Time currentTime = Time.now();
            //            Time AggTimeStamp = new Time(currentTime.getValue() - (long) updateInterval.getValue() * 1000);
            //            Time firstSampleTime = new Time(this.latestSampleTimeList.get(defId).get(indexParameterSet).getValue());
            long previous;
            if (indexParameterSet == 0) { //if its the first Set, the reference-time is the start of this aggregation-update
                previous = currentTime.getValue() - (long) (updateInterval.getInSeconds() * 1000);
            } else { //otherwise its the time of the last value of the previous set
                previous = this.latestSampleTimeList.get(defId).get(indexParameterSet - 1).getValue();
            }

            Time previousSetTimeStamp = new Time(previous);
            Time firstSampleTime = new Time(this.latestSampleTimeList.get(defId).get(indexParameterSet).getValue()
                    - (long) (sampleInterval.getInSeconds() * 1000) * sampleCountList.get(defId).get(indexParameterSet));

            // Delta-TIme =  firstSampleTime(Setx) - (firstSampleTime(Setx-1) + y*sampleInterval) | y = amount of updates.
            Duration deltaTime = new Duration(((float) (firstSampleTime.getValue() - previousSetTimeStamp.getValue())) / 1000);
            // Duration is in seconds but Time is in miliseconds
            return new AggregationSetValue(deltaTime, sampleInterval, val);
        } else {  // a new sample should be generated (if the generationMode is ADHOC or FILTEREDTIMEOUT, or the sampleInterval is out of the updateInterval range)
            return new AggregationSetValue(null, null, val);
        }
    }

    /**
     * Checks if the an aggregation expects to get unchanged values as the
     * actual value or as the null value, and replaces these with a null if
     * necessary.
     *
     * @param aggrDef the aggregation definition that will be checked if it
     * expects you to send unchanged values.
     * @param defId the id of the aggregation definition.
     * @param indexParameterSet the index of the parameter-set.
     * @return The list of aggregation parameter values.
     */
    private AggregationParameterValueList evaluateSendUnchanged(AggregationDefinition aggrDef, Long defId,
            int indexParameterSet) { //requirement: 3.7.3.m
        return evaluateSendUnchanged(aggrDef, defId, indexParameterSet, null);
    }

    /**
     * Checks if the an aggregation expects to get unchanged values as the
     * actual value or as the null value, and replaces these with a null if
     * necessary.
     *
     * @param aggrDef the aggregation definition that will be checked if it
     * expects you to send unchanged values.
     * @param defId the id of the aggregation definition.
     * @param indexParameterSet the index of the parameter-set.
     * @param currentParamValues the new paramValues the old param-values should
     * be compare with.
     * @return The list of aggregation parameter values.
     */
    private AggregationParameterValueList evaluateSendUnchanged(AggregationDefinition aggrDef, Long defId,
            int indexParameterSet, AggregationParameterValueList currentParamValues) { //requirement: 3.7.3.m
        AggregationParameterValueList retParamValues = new AggregationParameterValueList();
        if (currentParamValues == null) {
            currentParamValues = this.aggValuesCurrent.get(defId).getParameterSetValues().get(
                    indexParameterSet).getValues();
        }
        final AggregationParameterValueList lastParamValues = getLastUpdateValue(defId, indexParameterSet);
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
     * Checks if the filter is triggered, comparing the previous and
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
            final AggregationParameterValueList previousUpdateValue,
            final AggregationParameterValueList currentParameterValue) {
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
                int currentValidityState = current.getValidityState().getValue();
                int previousValidityState = previous.getValidityState().getValue();
                if ((currentValidityState == 0 && previousValidityState == 0)
                        || (currentValidityState == 2 && previousValidityState == 2)) { // 2 stands for the INVALID_RAW state
                    boolean filterisTriggered = false;

                    if (currentValidityState == 0
                            && previousValidityState == 0
                            && current.getConvertedValue() != null
                            && previous.getConvertedValue() != null) { // requirement: 3.7.2.6
                        filterisTriggered = this.triggeredFilter(previous.getConvertedValue(),
                                current.getConvertedValue(), aggregationParameterSet.getReportFilter());
                    }

                    if (current.getConvertedValue() == null && previous.getConvertedValue() == null) { // requirement: 3.7.2.6
                        filterisTriggered = this.triggeredFilter(previous.getRawValue(),
                                current.getRawValue(), aggregationParameterSet.getReportFilter());
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
     * @param defId the id of the aggregation definition.
     * @param bool the value if the filter was triggered
     * @return if a filter existed before
     */
    public Boolean setFilterTriggered(Long defId, Boolean bool) {
        //        this.isFilterTriggered.replace(objId, bool);
        boolean existed = (this.isFilterTriggered.remove(defId) != null);

        if (existed) {
            this.isFilterTriggered.put(defId, bool);
        }

        return existed;
    }

    /**
     * checks if the filter at the aggregation with the id was already triggered
     *
     * @param defId the id of the aggregation definition.
     * @return True of the filter is triggered.
     */
    public Boolean isFilterTriggered(Long defId) {
        return this.isFilterTriggered.get(defId);
    }

    /**
     * retrieves the values of an update that was published the time before the
     * last time.
     *
     * @param defId the id of the aggregation definition.
     * @param indexOfparameterSet The index of the parameter set.
     * @return The aggregation parameter value list.
     */
    private AggregationParameterValueList getLastUpdateValue(Long defId, int indexOfparameterSet) {
        AggregationSetValueList set = this.aggValuesLast.get(defId).getParameterSetValues();

        if (set == null) { // It was never sampled before?
            return null;
        }

        if (set.get(indexOfparameterSet).getValues() == null) { // It was never sampled before?
            return null;
        }

        return set.get(indexOfparameterSet).getValues();
    }

    /**
     * retrieves the values of an update that was published the last time.
     *
     * @param defId
     * @param indexOfparameterSet
     * @return
     */
    private AggregationParameterValueList getCurrentUpdateValue(Long defId, int indexOfparameterSet) {
        AggregationSetValueList set = this.aggValuesCurrent.get(defId).getParameterSetValues();

        if (set == null) { // It was never sampled before?
            return null;
        }

        if (set.get(indexOfparameterSet).getValues() == null) { // It was never sampled before?
            return null;
        }

        return set.get(indexOfparameterSet).getValues();
    }

    /**
     * this method sets the new samples as the current values. If a Filter is
     * active, it must have checked already that the filter is triggered. if its
     * not triggered the samples must not be set.
     *
     * @param defId the id of the aggregation
     * @param indexOfparameterSet the index of the parameterSet in an
     * aggregation
     * @param newParamSample the new values that should be set
     * @return
     */
    private AggregationParameterValueList setParameterSamplesInternally(Long defId,
            int indexOfparameterSet, AggregationParameterValueList newParamSample) {
        final AggregationParameterValueList currentParamValues =
                this.aggValuesCurrent.get(defId).getParameterSetValues().get(indexOfparameterSet).getValues();

        AggregationSetValueList agg1 = this.aggValuesLast.get(defId).getParameterSetValues();
        AggregationSetValue set1 = agg1.get(indexOfparameterSet);
        agg1.add(indexOfparameterSet, new AggregationSetValue(set1.getDeltaTime(), set1.getIntervalTime(), currentParamValues));

        AggregationSetValueList agg2 = this.aggValuesCurrent.get(defId).getParameterSetValues();
        AggregationSetValue set2 = agg2.get(indexOfparameterSet);
        agg2.add(indexOfparameterSet, new AggregationSetValue(set2.getDeltaTime(), set2.getIntervalTime(), newParamSample));

        /* Previous Code
        final AggregationParameterValueList currentParamValues = this.aggValuesCurrent
                .get(defId).getParameterSetValues().get(indexOfparameterSet).getValues();
        
        this.aggValuesLast.get(defId).getParameterSetValues()
                .get(indexOfparameterSet).setValues(currentParamValues);

        //set the new ones to the current ones
        this.aggValuesCurrent.get(defId).getParameterSetValues()
                .get(indexOfparameterSet).setValues(newParamSample);
         */
        //sets the timestamp of the latest value of the set. needed for the calculation of the delta-time
        this.latestSampleTimeList.get(defId).set(indexOfparameterSet, Time.now());

        return newParamSample;
    }

    public Long add(Identifier name, AggregationDefinition definition, ObjectKey source,
            SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5

        Long newId;
        if (super.getArchiveService() == null) {
            //add to providers local list
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newId = uniqueObjIdDef;

        } else {
            try {
                //not matter if the Aggregation was created or loaded, a new definition will be created
                HeterogeneousList defs = new HeterogeneousList();
                defs.add(definition);
                LongList defIds = super.getArchiveService().store(true,
                        AggregationServiceInfo.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails.getProviderURI()),
                        defs,
                        null);

                //add to providers local list
                newId = defIds.get(0);
            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }

        //add to internal lists
        this.addDefinitionLocally(name, newId, definition);
        final LongList ids = new LongList();
        ids.add(newId);
        this.createAggregationValuesList(ids);

        return newId;
    }

    /**
     * Updates an existing aggregation-definition with the given
     * definition-details.
     *
     * @param defId The id of the identity the definition belongs to
     * @param definition The new definition-details
     * @param source The ObjectKey of the source-object that cause the update to
     * be created
     * @param connectionDetails The connection details.
     * @return The id of the new definition.
     */
    public void update(Long defId, AggregationDefinition definition, ObjectKey source,
            SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        if (super.getArchiveService() == null) { //only update locally
            this.updateDef(defId, definition);
        } else {  // update in the COM Archive
            try {
                HeterogeneousList defs = new HeterogeneousList();
                defs.add(definition);

                //requirement 3.7.6.a
                super.getArchiveService().update(
                        AggregationServiceInfo.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, null, defId),
                        defs,
                        null);
            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(AggregationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.updateDef(defId, definition);
        }
    }

    public boolean delete(Long defId) {
        if (!this.deleteDefinitionLocally(defId)) {
            return false;
        }

        aggValuesLast.remove(defId);
        aggValuesCurrent.remove(defId);
        latestSampleTimeList.remove(defId);

        return true;
    }

    public void setReportingEnabled(Long defId, Boolean status, ObjectKey source,
            SingleConnectionDetails connectionDetails) {
        AggregationDefinition def = this.getAggregationDefinition(defId);

        if (def == null) {
            return;
        }
        //requirement: 3.7.9.2.f
        if (def.getReportingEnabled().booleanValue() == status) { // Is it set with the requested value already?
            return; // the value was not changed
        }

        AggregationDefinition newDef = new AggregationDefinition(def.getName(),
                def.getDescription(), def.getCategory(), def.getReportInterval(),
                def.getSendUnchanged(), def.getSendDefinitions(), def.getFilterEnabled(),
                def.getFilteredTimeout(), status, def.getParameterSets());

        //requirement: 3.7.9.2.j, k
        this.update(defId, newDef, source, connectionDetails);
    }

    public void setReportingEnabledAll(Boolean bool, ObjectKey source, SingleConnectionDetails connectionDetails) {
        LongList defIds = this.listAllDefinitions();

        for (Long defId : defIds) {
            AggregationDefinition def = this.getAggregationDefinition(defId);
            if (def.getReportingEnabled().booleanValue() != bool) {
                AggregationDefinition newDef = new AggregationDefinition(def.getName(),
                        def.getDescription(), def.getCategory(), def.getReportInterval(),
                        def.getSendUnchanged(), def.getSendDefinitions(), def.getFilterEnabled(),
                        def.getFilteredTimeout(), bool, def.getParameterSets());

                this.update(defId, newDef, source, connectionDetails);
            }
        }
    }

    /**
     * Sets the value to set the filter enabled or not.
     *
     * @param defId The id of the identity the definition belongs to.
     * @param bool the value if the filter should be enabled
     * @param source The source of the update.
     * @param connectionDetails The connection details.
     * @return true if it was set successfully, false if it wasnt set.
     */
    public boolean setFilterEnabled(Long defId, Boolean bool,
            ObjectKey source, SingleConnectionDetails connectionDetails) {
        AggregationDefinition def = this.getAggregationDefinition(defId);

        if (def == null) {
            return false;
        }
        //requirement: 3.7.10.2.f
        // Is it set with the requested value already?
        if (def.getFilterEnabled().booleanValue() == bool) {
            return false; // the value was not changed
        }

        AggregationDefinition newDef = new AggregationDefinition(def.getName(),
                def.getDescription(), def.getCategory(), def.getReportInterval(),
                def.getSendUnchanged(), def.getSendDefinitions(), def.getFilterEnabled(),
                def.getFilteredTimeout(), bool, def.getParameterSets());

        //requirement: 3.7.10.2.j
        this.update(defId, newDef, source, connectionDetails);
        return true;
    }

    public void setFilterEnabledAll(Boolean bool, ObjectKey source, SingleConnectionDetails connectionDetails) {
        LongList defIds = this.listAllDefinitions();

        for (Long defId : defIds) {
            AggregationDefinition def = this.getAggregationDefinition(defId);
            if (def.getFilterEnabled().booleanValue() != bool) {
                AggregationDefinition newDef = new AggregationDefinition(def.getName(),
                        def.getDescription(), def.getCategory(), def.getReportInterval(),
                        def.getSendUnchanged(), def.getSendDefinitions(), def.getFilterEnabled(),
                        def.getFilteredTimeout(), bool, def.getParameterSets());

                this.update(defId, newDef, source, connectionDetails);
            }
        }
    }

}
