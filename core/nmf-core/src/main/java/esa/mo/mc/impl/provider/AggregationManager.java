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

import org.ccsds.moims.mo.mal.MOErrorException;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.DuplicateException;
import org.ccsds.moims.mo.com.InvalidArgumentException;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.UnknownException;
import org.ccsds.moims.mo.mal.helpertools.connections.ConfigurationProviderSingleton;
import org.ccsds.moims.mo.mal.helpertools.connections.SingleConnectionDetails;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mc.aggregation.AggregationServiceInfo;
import org.ccsds.moims.mo.mc.structures.*;

/**
 * Manages the aggregation definitions of the Aggregation service.
 *
 * @author Cesar Coelho
 */
public final class AggregationManager extends MCManager {

    private final HashMap<Long, Boolean> isFilterTriggered; // DefId, Boolean Value that determines if the filter was triggered
    private final HashMap<Long, AggregationValue> aggValuesLast; // DefId, Aggregation Value List per definition
    private final HashMap<Long, AggregationValue> aggValuesCurrent; // DefId, Aggregation Value List per definition

    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdAVal;
    private final ParameterManager parameterManager;

    /**
     * Creates a new {@code AggregationManager}.
     *
     * @param comServices the COM services
     * @param parameterManager the parameter manager
     */
    public AggregationManager(COMServicesProvider comServices, ParameterManager parameterManager) {
        super(comServices);
        this.parameterManager = parameterManager;

        this.isFilterTriggered = new HashMap<>();
        this.aggValuesLast = new HashMap<>();
        this.aggValuesCurrent = new HashMap<>();

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = 0L; // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdAVal = 0L; // The zeroth value will not be used (reserved for the wildcard)
        }
    }

    /**
     * Returns the aggregation definition.
     *
     * @param id the id
     * @return the aggregation definition
     */
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
            this.populateAggregationValues(defId);
        }
    }

    /**
     * resets periodicAggregationValuesLast, periodicAggregationValuesCurrent,
     * and the filterTriggered-variables.
     * this method will be called after an aggregation-definition was updated.
     *
     * @param defId The definition Id.
     */
    public void populateAggregationValues(final Long defId) {
        AggregationSetValueList aggregationSetValueListLast = new AggregationSetValueList();
        AggregationSetValueList aggregationSetValueListCurrent = new AggregationSetValueList();
        AggregationDefinition definition = this.getAggregationDefinition(defId);
        final int paramSetSize = definition.getParameterSets().size();
        // init the latest and current aggregation-values
        for (int j = 0; j < paramSetSize; j++) {
            aggregationSetValueListLast.add(j, new AggregationSetValue());
            aggregationSetValueListCurrent.add(j, new AggregationSetValue());
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
     * resets the filtertriggered-variable. this method will be called after each
     * aggregation-value-update was published
     *
     * @param defId The definition Id.
     * @return true if it was successful, false if identity not found
     */
    public Boolean resetAggregationSampleHelperVariables(Long defId) {
        if (!this.existsDef(defId)) {
            return false;
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
        isFilterTriggered.remove(defId);
    }

    /**
     * Stores the aggregation value in the archive and returns its generated object id.
     *
     * @param aVal the a val
     * @param related the related
     * @param uri the uri
     * @param timestamp the timestamp
     * @return the generated object id
     */
    public Long storeAndGenerateAValobjId(AggregationValue aVal, Long related, URI uri, Time timestamp) {
        if (super.getArchiveService() == null) {
            uniqueObjIdAVal++;
            return this.uniqueObjIdAVal;
        } else {
            HeterogeneousList aValList = new HeterogeneousList();
            aValList.add(aVal);

            try {
                //requirement: Aggregation#16
                LongList objIds = super.getArchiveService().store(
                        true,
                        AggregationServiceInfo.AGGREGATIONVALUE_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(related, uri),
                        aValList,
                        null);

                if (objIds.size() == 1) {
                    return objIds.get(0);
                }
            } catch (DuplicateException | InvalidArgumentException | MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }
    }

    /**
     *
     * @param paramIdentityId the identity id of the parameter to be sampled
     * @return ParameterValue of the parameter, or an empty ParameterValue with
     * INVALID state if parameter cannot be retrieved
     */
    private ParameterValue sampleParameter(Long paramIdentityId) {
        try {
            return parameterManager.getParameterValue(paramIdentityId);
        } catch (MOErrorException ex) {
            return new ParameterValue(paramIdentityId, ValidityState.INVALID_RAW, null, Time.now());
        }
    }

    /**
     * Samples the parameters of a parameter set.
     *
     * @param paramIds the id of the parameters to be sampled
     * @return The parameter values, in the order of paramIds.
     */
    private ParameterValueList sampleParameters(LongList paramIds) {
        ParameterValueList pValList = new ParameterValueList();

        for (Long id : paramIds) {
            pValList.add(sampleParameter(id));
        }

        return pValList;
    }

    private Boolean triggeredFilter(Attribute previousValue, Attribute currentValue, ThresholdFilter filter) {
        if (filter == null) {
            return false;  // If there's no filter, then it will never be ignored! 
        }
        final Double previousValueDouble = toDouble(previousValue);
        final Double currentValueDouble = toDouble(currentValue);
        final Double thresholdValue = toDouble(filter.getThresholdValue());

        // A delta or a percentage has no meaning for a value that is not a
        // number, such as a String or a Boolean: any change passes the filter
        if (previousValueDouble == null || currentValueDouble == null || thresholdValue == null) {
            return !previousValue.toString().equals(currentValue.toString());
        }

        if (filter.getThresholdType() == ThresholdType.DELTA) // requirement: Aggregation#10
        {
            return (Math.abs(previousValueDouble - currentValueDouble) > thresholdValue);
        }

        if (filter.getThresholdType() == ThresholdType.PERCENTAGE) // requirement: Aggregation#10
        {
            final double change = Math.abs(previousValueDouble - currentValueDouble);
            // Any change from zero exceeds every percentage
            if (previousValueDouble == 0) {
                return change > 0;
            }
            // Against the size of the previous value, so that negative values
            // are filtered as positive ones are
            return (change / Math.abs(previousValueDouble) * 100 > thresholdValue);
        }

        return false;
    }

    /**
     * Returns the value as a number, or null if it is not one.
     *
     * @param value The value.
     * @return The number, or null if the value is not a number.
     */
    private static Double toDouble(Attribute value) {
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException ex) {
            return null;
        }
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
        this.sampleParam(defId, indexOfparameterSet, null);
    }

    /**
     * creates a new ParmeterSample or uses the given samples and saves it to
     * the internal list. No filter will be checked.
     *
     * @param defId The definition Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at.
     * @param newAggrParameterValueSamples the new parameterSamples to be set.
     * if null new parameterSamples will be generated
     */
    public void sampleParam(Long defId, int indexOfparameterSet,
            ParameterValueList newAggrParameterValueSamples) {
        final AggregationDefinition aggrDef = this.getAggregationDefinition(defId);

        if (newAggrParameterValueSamples == null) {
            final LongList parameters = aggrDef.getParameterSets().get(indexOfparameterSet).getParameters();
            newAggrParameterValueSamples = this.sampleParameters(parameters);
        }
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
        this.sampleAndFilterParam(defId, indexOfparameterSet, null);
    }

    /**
     * check if the newParameterValueSamples trigger the filter and if its
     * triggered, saves these in the internal list. This method doesnt publish
     * the new values.
     *
     * @param defId The definition Id.
     * @param indexOfparameterSet the index in the aggregation to set the
     * newParameterValueSamples at
     * @param newParameterValueSamples the newParameterValueSamples to be set.
     * if null new parameterSamples will be generated
     */
    public void sampleAndFilterParam(Long defId, int indexOfparameterSet,
            ParameterValueList newParameterValueSamples) {
        final AggregationDefinition aggrDef = this.getAggregationDefinition(defId);
        final AggregationParameterSet aggrParamSet = aggrDef.getParameterSets().get(indexOfparameterSet);
        // Add another sample on the AggregationValue that will be returned later:
        final ParameterValueList currentUpdateValues = this.getCurrentUpdateValue(defId,
                indexOfparameterSet);
        if (newParameterValueSamples == null) {
            newParameterValueSamples = this.sampleParameters(aggrParamSet.getParameters());
        }

        //no samples saved yet
        if (currentUpdateValues == null) {
            //first sample, set internally and trigger filter
            this.setParameterSamplesInternally(defId, indexOfparameterSet, newParameterValueSamples);
            this.setFilterTriggered(defId, true);
            return;
        }
        //requirement: Aggregation#9
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
        for (int j = 0; j < parameterSets.size(); j++) {   //Cycle through the parameterSets (requirement: Aggregation#14)
            ParameterValueList sampleParameters = this.sampleParameters(
                    aggrDef.getParameterSets().get(j).getParameters());
            AggregationSetValue aggrSetValue = new AggregationSetValue(sampleParameters);
            parameterSetValues.add(aggrSetValue);
        }

        return new AggregationValue(GenerationMode.ADHOC, false, parameterSetValues);
    }

    /**
     * gets the current aggregation value from the internal list.
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
        for (int j = 0; j < parameterSets.size(); j++) {  //Cycle through the parameterSets (requirement: Aggregation#14)
            //requirement: Aggregation#13 leaves out the unchanged values, if they are not to be sent
            ParameterValueList val = evaluateSendUnchanged(aggrDef, defId, j);

            if (val != null) {
                parameterSetValues.add(new AggregationSetValue(val));
            }
        }
        //set the current parameterSetList as the current aggregation values
        return new AggregationValue(generationMode, isFilterTriggered(defId), parameterSetValues);

    }

    /**
     * Returns the values of a parameter set to be reported, leaving out the
     * values unchanged since the previous sample if the aggregation does not
     * send unchanged values.
     *
     * @param aggrDef the aggregation definition that will be checked if it
     * expects you to send unchanged values.
     * @param defId the id of the aggregation definition.
     * @param indexParameterSet the index of the parameter-set.
     * @return The list of aggregation parameter values.
     */
    private ParameterValueList evaluateSendUnchanged(AggregationDefinition aggrDef, Long defId,
            int indexParameterSet) { //requirement: Aggregation#13
        return evaluateSendUnchanged(aggrDef, defId, indexParameterSet, null);
    }

    /**
     * Returns the values of a parameter set to be reported, leaving out the
     * values unchanged since the previous sample if the aggregation does not
     * send unchanged values.
     *
     * @param aggrDef the aggregation definition that will be checked if it
     * expects you to send unchanged values.
     * @param defId the id of the aggregation definition.
     * @param indexParameterSet the index of the parameter-set.
     * @param currentParamValues the new paramValues the old param-values should
     * be compare with.
     * @return The list of aggregation parameter values.
     */
    private ParameterValueList evaluateSendUnchanged(AggregationDefinition aggrDef, Long defId,
            int indexParameterSet, ParameterValueList currentParamValues) { //requirement: Aggregation#13
        ParameterValueList retParamValues = new ParameterValueList();
        if (currentParamValues == null) {
            currentParamValues = this.aggValuesCurrent.get(defId).getParameterSetValues().get(
                    indexParameterSet).getValues();
        }
        final ParameterValueList lastParamValues = getLastUpdateValue(defId, indexParameterSet);
        if (!aggrDef.getSendUnchanged() && lastParamValues != null) {
            for (int k = 0; k < currentParamValues.size(); k++) { //requirement: Aggregation#14 keeps the order of the set
                //requirement: Aggregation#13: an unchanged value is left out
                if (!isUnchanged(lastParamValues.get(k), currentParamValues.get(k))) {
                    retParamValues.add(currentParamValues.get(k));
                }
            }
        } else {
            retParamValues = currentParamValues;
        }
        return retParamValues;
    }

    /**
     * Checks if a parameter value is unchanged since the previous report. The
     * sample time is not compared, as it is new for every sample.
     *
     * @param previous The value of the previous report.
     * @param current The current value.
     * @return true if the validity state and the raw value are the same.
     */
    private static boolean isUnchanged(ParameterValue previous, ParameterValue current) {
        return previous.getValidityState().equals(current.getValidityState())
                && Objects.equals(previous.getRawValue(), current.getRawValue());
    }

    /**
     * Checks if the filter is triggered, comparing the previous and
     * currentvalues.
     *
     * @param aggregationParameterSet
     * @param previousUpdateValue
     * @param currentParameterValue
     * @return if the filter is triggered, because the values of the parameters
     * have changed more than a given threshold. Also returns true, if there is
     * no periodic filter or it contains more than one parameter.
     */
    private boolean checkFilterIsTriggered(final AggregationParameterSet aggregationParameterSet,
            final ParameterValueList previousUpdateValue,
            final ParameterValueList currentParameterValue) {
        // Filter Comparison Process
        if (aggregationParameterSet.getReportFilter() == null
                || aggregationParameterSet.getParameters().size() != 1) { // requirement: Aggregation#11 
            //let the update being published
            return true;
        }

        // In theory all the list should be null with the exception of the Parameter Value we want
        // because size = 1 and the remaining Parameter Values inside get the null state
        // So we can crawl the list until we find the first non-null element and compare it with the previousParameterValue
        for (int i = 0; i < currentParameterValue.size(); i++) {
            ParameterValue current = currentParameterValue.get(i);
            ParameterValue previous = previousUpdateValue.get(i);

            if (current != null && previous != null) {
                // Compare the values:
                int currentValidityState = current.getValidityState().getValue();
                int previousValidityState = previous.getValidityState().getValue();
                if ((currentValidityState == 0 && previousValidityState == 0)
                        || (currentValidityState == 2 && previousValidityState == 2)) { // 2 stands for the INVALID_RAW state
                    return this.triggeredFilter(previous.getRawValue(),
                            current.getRawValue(), aggregationParameterSet.getReportFilter());
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
    private ParameterValueList getLastUpdateValue(Long defId, int indexOfparameterSet) {
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
    private ParameterValueList getCurrentUpdateValue(Long defId, int indexOfparameterSet) {
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
    private ParameterValueList setParameterSamplesInternally(Long defId,
            int indexOfparameterSet, ParameterValueList newParamSample) {
        final ParameterValueList currentParamValues
                = this.aggValuesCurrent.get(defId).getParameterSetValues().get(indexOfparameterSet).getValues();

        this.aggValuesLast.get(defId).getParameterSetValues().set(indexOfparameterSet,
                new AggregationSetValue(currentParamValues));
        this.aggValuesCurrent.get(defId).getParameterSetValues().set(indexOfparameterSet,
                new AggregationSetValue(newParamSample));

        return newParamSample;
    }

    /**
     * Adds an aggregation definition and returns its object id.
     *
     * @param name the name
     * @param definition the definition
     * @param connectionDetails the connection details
     * @return the assigned object id
     */
    public Long add(Identifier name, AggregationDefinition definition,
            SingleConnectionDetails connectionDetails) {

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
                        HelperArchive.generateArchiveDetailsList(null, connectionDetails.getProviderURI()),
                        defs,
                        null);

                //add to providers local list
                newId = defIds.get(0);
            } catch (DuplicateException | InvalidArgumentException | MALException ex) {
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
     * @param connectionDetails The connection details.
     */
    public void update(Long defId, AggregationDefinition definition,
            SingleConnectionDetails connectionDetails) {
        if (super.getArchiveService() == null) { //only update locally
            this.updateDef(defId, definition);
        } else {  // update in the COM Archive
            try {
                HeterogeneousList defs = new HeterogeneousList();
                defs.add(definition);

                //requirement: Aggregation#15
                super.getArchiveService().update(
                        AggregationServiceInfo.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, null, defId),
                        defs,
                        null);
            } catch (UnknownException | InvalidArgumentException | MALException ex) {
                Logger.getLogger(AggregationManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.updateDef(defId, definition);
        }
    }

    /**
     * Deletes the aggregation definition with the given id.
     *
     * @param defId the def id
     * @return {@code true} on success
     */
    public boolean delete(Long defId) {
        if (!this.deleteDefinitionLocally(defId)) {
            return false;
        }

        aggValuesLast.remove(defId);
        aggValuesCurrent.remove(defId);

        return true;
    }

    /**
     * Sets the reporting enabled.
     *
     * @param defId the def id
     * @param status the status
     * @param connectionDetails the connection details
     */
    public void setReportingEnabled(Long defId, Boolean status,
            SingleConnectionDetails connectionDetails) {
        AggregationDefinition def = this.getAggregationDefinition(defId);

        if (def == null) {
            return;
        }
        //requirement: Aggregation.enableReporting#5
        if (def.getReportingEnabled().booleanValue() == status) { // Is it set with the requested value already?
            return; // the value was not changed
        }

        AggregationDefinition newDef = new AggregationDefinition(def.getName(),
                def.getDescription(), def.getCategory(), def.getReportInterval(),
                def.getSendUnchanged(), def.getFilterEnabled(),
                def.getFilteredTimeout(), status, def.getParameterSets());

        //requirement: Aggregation.enableReporting#7
        this.update(defId, newDef, connectionDetails);
    }

    /**
     * Sets the reporting enabled all.
     *
     * @param bool the bool
     * @param connectionDetails the connection details
     */
    public void setReportingEnabledAll(Boolean bool, SingleConnectionDetails connectionDetails) {
        LongList defIds = this.listAllDefinitions();

        for (Long defId : defIds) {
            AggregationDefinition def = this.getAggregationDefinition(defId);
            if (def.getReportingEnabled().booleanValue() != bool) {
                AggregationDefinition newDef = new AggregationDefinition(def.getName(),
                        def.getDescription(), def.getCategory(), def.getReportInterval(),
                        def.getSendUnchanged(), def.getFilterEnabled(),
                        def.getFilteredTimeout(), bool, def.getParameterSets());

                this.update(defId, newDef, connectionDetails);
            }
        }
    }

    /**
     * Sets the value to set the filter enabled or not.
     *
     * @param defId The id of the identity the definition belongs to.
     * @param bool the value if the filter should be enabled
     * @param connectionDetails The connection details.
     * @return true if it was set successfully, false if it wasnt set.
     */
    public boolean setFilterEnabled(Long defId, Boolean bool, SingleConnectionDetails connectionDetails) {
        AggregationDefinition def = this.getAggregationDefinition(defId);

        if (def == null) {
            return false;
        }
        //requirement: Aggregation.enableFilter#5
        // Is it set with the requested value already?
        if (def.getFilterEnabled().booleanValue() == bool) {
            return false; // the value was not changed
        }

        AggregationDefinition newDef = new AggregationDefinition(def.getName(),
                def.getDescription(), def.getCategory(), def.getReportInterval(),
                def.getSendUnchanged(), bool,
                def.getFilteredTimeout(), def.getReportingEnabled(), def.getParameterSets());

        //requirement: Aggregation.enableFilter#7
        this.update(defId, newDef, connectionDetails);
        return true;
    }

    /**
     * Sets the filter enabled all.
     *
     * @param bool the bool
     * @param connectionDetails the connection details
     */
    public void setFilterEnabledAll(Boolean bool, SingleConnectionDetails connectionDetails) {
        LongList defIds = this.listAllDefinitions();

        for (Long defId : defIds) {
            AggregationDefinition def = this.getAggregationDefinition(defId);
            if (def.getFilterEnabled().booleanValue() != bool) {
                AggregationDefinition newDef = new AggregationDefinition(def.getName(),
                        def.getDescription(), def.getCategory(), def.getReportInterval(),
                        def.getSendUnchanged(), bool,
                        def.getFilteredTimeout(), def.getReportingEnabled(), def.getParameterSets());

                this.update(defId, newDef, connectionDetails);
            }
        }
    }

}
