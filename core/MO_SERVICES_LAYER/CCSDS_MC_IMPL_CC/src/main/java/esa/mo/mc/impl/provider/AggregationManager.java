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

import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperTime;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.TimeList;
import org.ccsds.moims.mo.mc.aggregation.AggregationHelper;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetails;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationDefinitionDetailsList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationParameterSetList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationSetValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationSetValueList;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValue;
import org.ccsds.moims.mo.mc.aggregation.structures.AggregationValueList;
import org.ccsds.moims.mo.mc.aggregation.structures.GenerationMode;
import org.ccsds.moims.mo.mc.aggregation.structures.ThresholdFilter;
import org.ccsds.moims.mo.mc.aggregation.structures.ThresholdType;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;

/**
 *
 * @author Cesar Coelho
 */
public final class AggregationManager extends DefinitionsManager {
    
    private static final transient int SAVING_PERIOD = 20;  // Used to store the uniqueAValObjId only once every "SAVINGPERIOD" times

    private final HashMap<Long, Boolean> isFilterTriggered; // Boolean Value that determines if the filter was triggered
    private final transient HashMap<Long, AggregationValue> periodicAggregationValues; // Aggregation Value List per definition
    private final transient HashMap<Long, TimeList> firstSampleTimeList; // Time of the first sample of the Aggregation value

    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdAVal;
    private final transient ParameterManager parameterManager;

    
    public AggregationManager (COMServicesProvider comServices, ParameterManager parameterManager){
        super(comServices);
        this.parameterManager = parameterManager;

        this.isFilterTriggered = new HashMap<Long, Boolean>();
        this.periodicAggregationValues = new HashMap<Long, AggregationValue>();
        this.firstSampleTimeList = new HashMap<Long, TimeList>();

        if (super.getArchiveService() == null) {  // No Archive?
            this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdAVal = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
//            this.load(); // Load the file
        }else{
            
        }

        LongList objIds = new LongList();
        objIds.addAll(this.getDefs().keySet());
        this.createAggregationValuesList(objIds);
        
    }

    @Override
    public Boolean compareName(Long objId, Identifier name) {
        return this.get(objId).getName().equals(name);
    }

    @Override
    public ElementList newDefinitionList() {
        return new AggregationDefinitionDetailsList();
    }

    public AggregationDefinitionDetails get(Long input) {
        return (AggregationDefinitionDetails) this.getDefs().get(input);
    }

    protected void createAggregationValuesList (LongList objIds){
        for (Long objId : objIds){
            periodicAggregationValues.put(objId, new AggregationValue());
            firstSampleTimeList.put(objId, new TimeList());
            isFilterTriggered.put(objId, false);

            this.populateAggregationValues(objId);
        }
    }
    
    private void populateAggregationValues (final Long objId){
        AggregationDefinitionDetails definition = this.get(objId);
        AggregationSetValueList aggregationSetValueList = new AggregationSetValueList();
        for (int j = 0; j < definition.getParameterSets().size(); j++){
            firstSampleTimeList.get(objId).add(j, new Time());
            aggregationSetValueList.add(j, new AggregationSetValue());
        }
        periodicAggregationValues.get(objId).setParameterSetValues(aggregationSetValueList);
    }
   
    public Boolean resetPeriodicAggregationValues(Long objId){
        if (!this.exists(objId)) return false;
        AggregationSetValueList aggregationSetValueList = periodicAggregationValues.get(objId).getParameterSetValues();
        AggregationDefinitionDetails definition = this.get(objId);
        for (int j = 0; j < definition.getParameterSets().size(); j++){
            firstSampleTimeList.get(objId).set(j, new Time());
            aggregationSetValueList.set(j, new AggregationSetValue());
        }
        periodicAggregationValues.get(objId).setParameterSetValues(aggregationSetValueList);

        this.setFilterTriggered(objId, false);  // Reset the filter state
        return true;
    }
    
    public Long storeAndGenerateAValobjId(AggregationValue aVal, Long related, SingleConnectionDetails connectionDetails){ 
        if (super.getArchiveService() == null) {
            uniqueObjIdAVal++;
//            if (uniqueObjIdAVal % SAVING_PERIOD  == 0) // It is used to avoid constant saving every time we generate a new obj Inst identifier.
//                this.save();
            return this.uniqueObjIdAVal;
        }else{
            AggregationValueList aValList = new AggregationValueList();
            aValList.add(aVal);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        AggregationHelper.AGGREGATIONVALUEINSTANCE_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(related, null, connectionDetails),
                        aValList,
                        null);

                if (objIds.size() == 1) {
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }
        
    }

    public ParameterValue sampleParameter(Long objId){
        try {
            return parameterManager.getParameterValue(objId);
        } catch (MALInteractionException ex) {
            return null;
        }
    }

    public ParameterValueList sampleParameters(LongList objIds){
        ParameterValueList pValList = new ParameterValueList();
        for (Long objId : objIds){
            pValList.add(sampleParameter(objId));
        }
        
        return pValList;
    }
    
    public Boolean triggeredFilter (Attribute previousValue, Attribute currentValue, ThresholdFilter filter ){
        if (filter == null) return false;  // If there's no filter, then it will never be ignored! // requirement: 3.7.2.7

        final Double previousValueDouble = Double.parseDouble(previousValue.toString());
        final Double currentValueDouble = Double.parseDouble(currentValue.toString());
        final Double thresholdValue = Double.parseDouble(filter.getThresholdValue().toString());
        
        if (filter.getThresholdType() == ThresholdType.DELTA)       // requirement: 3.7.2.8
            return ( Math.abs(previousValueDouble - currentValueDouble) > thresholdValue );
            
        if (filter.getThresholdType() == ThresholdType.PERCENTAGE)  // requirement: 3.7.2.8
            return ( Math.abs(previousValueDouble - currentValueDouble) / previousValueDouble * 100 > thresholdValue );

        return false;
    }

    public AggregationValueList getAggregationValuesList(LongList objIds, GenerationMode generationMode, Boolean filtered){
        AggregationValueList aValList = new AggregationValueList();
        for (Long objId : objIds) 
            aValList.add( getAggregationValue(objId, generationMode, filtered) );
        
        return aValList;
    }

    public AggregationValue getAggregationValue(Long objId, GenerationMode generationMode, Boolean filtered){
        AggregationValue aVal = new AggregationValue();
        AggregationDefinitionDetails aDef = this.get(objId);
        AggregationParameterSetList parameterSets = aDef.getParameterSets();
        AggregationSetValueList parameterSetValues = new AggregationSetValueList();
        
        aVal.setGenerationMode(generationMode);
        aVal.setFiltered(filtered);

        for (int j = 0; j < parameterSets.size(); j++){  //Cycle through the parameterSets (requirement: 3.7.2.15)
            AggregationSetValue parameterSetValue = new AggregationSetValue();
            Duration sampleInterval = parameterSets.get(j).getSampleInterval();

            if ( generationMode == GenerationMode.PERIODIC &&
            sampleInterval.getValue() != 0  &&
            sampleInterval.getValue() <  aDef.getUpdateInterval().getValue() ){
                Time currentTime = HelperTime.getTimestampMillis();
                Time AggTimeStamp = new Time( currentTime.getValue() - (long) aDef.getUpdateInterval().getValue()*1000) ;
                Time firstSampleTime = new Time(this.firstSampleTimeList.get(objId).get(j).getValue());
//                Duration deltaTime = new Duration( (int) ((float)(firstSampleTime.getValue() - AggTimeStamp.getValue()))/1000 );  // Duration is in seconds but Time is in miliseconds
                Duration deltaTime = new Duration( ((float)(firstSampleTime.getValue() - AggTimeStamp.getValue())) / 1000 );  // Duration is in seconds but Time is in miliseconds
                parameterSetValue.setDeltaTime( deltaTime );
                parameterSetValue.setIntervalTime(sampleInterval);
                parameterSetValue.setValues(periodicAggregationValues.get(objId).getParameterSetValues().get(j).getValues()); // Get all the samples stored
            }else{  // One sample only of each?
                parameterSetValue.setDeltaTime(null);
                parameterSetValue.setIntervalTime(null);
                parameterSetValue.setValues(this.sampleParameters(parameterSets.get(j).getParameters())); // Do just one sampling...
            }
            parameterSetValues.add(parameterSetValue);
        }
        
        aVal.setParameterSetValues(parameterSetValues);
        return aVal;
    }

    public AggregationDefinitionDetailsList getAll(){
        return (AggregationDefinitionDetailsList) this.getAllDefs();
    }
    
    public Boolean setFilterTriggered(Long objId, Boolean bool){
//        this.isFilterTriggered.replace(objId, bool);
        boolean existed = (this.isFilterTriggered.remove(objId) != null);
        
        if (existed){
            this.isFilterTriggered.put(objId, bool);
        }
        
        return existed;
    }

    public Boolean isFilterTriggered(Long objId){
        return this.isFilterTriggered.get(objId);
    }

    public ParameterValueList getLastParameterValue(Long objId, int indexOfparameterSet){
        ParameterValueList pValLst = new ParameterValueList();
        AggregationDefinitionDetails def = this.get(objId);

        if (this.periodicAggregationValues.get(objId).getParameterSetValues() == null)  // It was never sampled before?
            return null;

        if (this.periodicAggregationValues.get(objId).getParameterSetValues().get(indexOfparameterSet).getValues() == null)  // It was never sampled before?
            return null;

        ParameterValueList currentPValLst = this.periodicAggregationValues.get(objId).getParameterSetValues().get(indexOfparameterSet).getValues();
        int numberParametersPerSample = def.getParameterSets().get(indexOfparameterSet).getParameters().size();
        int totalNumberParametersSampled = currentPValLst.size();
        
        pValLst.addAll(currentPValLst.subList(totalNumberParametersSampled - numberParametersPerSample, totalNumberParametersSampled));
        return pValLst;

    }

    public ParameterValueList updateParameterValue(Long objId, int indexOfparameterSet){
        AggregationDefinitionDetails aggregationDefinition = this.get(objId);
        ParameterValueList newSample = this.sampleParameters(aggregationDefinition.getParameterSets().get(indexOfparameterSet).getParameters());

        if (this.periodicAggregationValues.get(objId).getParameterSetValues().get(indexOfparameterSet).getValues() == null){ // Is it the first sample?
            this.periodicAggregationValues.get(objId).getParameterSetValues().get(indexOfparameterSet).setValues(newSample);
            this.firstSampleTimeList.get(objId).set(indexOfparameterSet, HelperTime.getTimestampMillis()); 
        }else{
            ParameterValueList currentPValLst = this.periodicAggregationValues.get(objId).getParameterSetValues().get(indexOfparameterSet).getValues();
            currentPValLst.addAll(newSample);  // then add to the current list the new Sample
            this.periodicAggregationValues.get(objId).getParameterSetValues().get(indexOfparameterSet).setValues(currentPValLst);
        }

        return newSample;
    }

    public Long add(AggregationDefinitionDetails definition, ObjectId source, SingleConnectionDetails connectionDetails){ // requirement: 3.3.2.5
        definition.setGenerationEnabled(false);  // requirement: 3.3.2.7

        if (super.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.addDef(uniqueObjIdDef, definition);
            isFilterTriggered.put(uniqueObjIdDef, false);
            LongList longlist = new LongList();
            longlist.add(uniqueObjIdDef);
            this.createAggregationValuesList(longlist);

//            this.save();
            return uniqueObjIdDef;
        }else{
            AggregationDefinitionDetailsList defs = new AggregationDefinitionDetailsList();
            defs.add(definition);

            try {
                LongList objIds = super.getArchiveService().store(
                        true,
                        AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
                        defs,
                        null);

                if (objIds.size() == 1) {
                    this.addDef(objIds.get(0), definition);
                    isFilterTriggered.put(objIds.get(0), false);
                    this.createAggregationValuesList(objIds);
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }
      
    public boolean update(Long objId, AggregationDefinitionDetails definition, SingleConnectionDetails connectionDetails){ // requirement: 3.3.2.5
        Boolean success = this.updateDef(objId, definition);  // requirement: 3.7.2.13
        this.populateAggregationValues(objId);

        if (super.getArchiveService() != null) {  // It should also update on the COM Archive
            try {
                AggregationDefinitionDetailsList defs = new AggregationDefinitionDetailsList();
                defs.add(definition);
//                ArchiveDetailsList archiveDetailsList = HelperMisc.generateArchiveDetailsList(null, null);
//                archiveDetailsList.get(0).setInstId(objId);

                ArchiveDetails archiveDetails = HelperArchive.getArchiveDetailsFromArchive(super.getArchiveService(), 
                        AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE, connectionDetails.getDomain(), objId);

                ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
                archiveDetailsList.add(archiveDetails);
                
                super.getArchiveService().update(
                        AggregationHelper.AGGREGATIONDEFINITION_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        archiveDetailsList,
                        defs,
                        null);

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

//        this.save();
        return success;
    }

    public boolean delete(Long objId){ // requirement: 3.3.2.5

        if (!this.deleteDef(objId)) {
            return false;
        }
        
        periodicAggregationValues.remove(objId);
        firstSampleTimeList.remove(objId);

//        if (super.getArchiveService() == null)
//            this.save();
    
        return true;
    }
   
    public boolean setGenerationEnabled(Long objId, Boolean bool, SingleConnectionDetails connectionDetails){ // requirement: 3.3.2.5
        AggregationDefinitionDetails def = this.get(objId);

        if (def == null){
            return false;
        }
        
        if (def.getGenerationEnabled().booleanValue() == bool) // Is it set with the requested value already?
            return false; // the value was not changed

        def.setGenerationEnabled(bool);
        this.update(objId, def, connectionDetails);
        return true;
    }

    public void setGenerationEnabledAll(Boolean bool, SingleConnectionDetails connectionDetails){ 
        LongList objIds = new LongList(); 
        objIds.addAll(this.getDefs().keySet());
        
        for (Long objId : objIds) {
            AggregationDefinitionDetails def = this.get(objId);
            def.setGenerationEnabled(bool);
            this.update(objId, def, connectionDetails);
        }
    }
    
    public boolean setFilterEnabled(Long objId, Boolean bool){ // requirement: 3.3.2.5
        AggregationDefinitionDetails aDef = this.get(objId);
        if (aDef.getFilterEnabled().booleanValue() == bool) // Is it set with the requested value already?
            return false; // the value was not changed
            
        aDef.setFilterEnabled(bool);
//        this.save();
        return true;
   }

    public void setFilterEnabledAll(Boolean bool){
        AggregationDefinitionDetailsList aDefs = this.getAll();
        for (AggregationDefinitionDetails ADeflist1 : aDefs)
            ADeflist1.setFilterEnabled(bool);

//        this.save();
    }
/*
    @Override
    public boolean load() {
        try {
            AggregationManager loadedFile = (AggregationManager) FileDataSerialization.serializeDataIn(FILENAME_DB);
            this.uniqueObjIdDef = loadedFile.uniqueObjIdDef;
            this.uniqueObjIdAVal = loadedFile.uniqueObjIdAVal + SAVING_PERIOD; // Guarantees the obj inst id stays unique
            this.getDefs().putAll(loadedFile.getDefs());
            this.save();
            return true;        // returns true if the file was successfully loaded
        } catch (FileNotFoundException ex ) {
            return false;
        } catch (IOException | ClassNotFoundException ex) {
            return false;
        }
    }

    @Override
    public boolean save(){  // returns true if the file was successfully saved
        try {
            FileDataSerialization.serializeDataOut(FILENAME_DB, this);
            return true;
        } catch (IOException e){
            return false;
        }
    }
*/
}
