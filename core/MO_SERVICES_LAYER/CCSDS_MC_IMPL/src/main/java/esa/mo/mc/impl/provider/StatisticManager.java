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
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.interfaces.ExternalStatisticFunctionsInterface;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetails;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.TimeList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.statistic.StatisticHelper;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticEvaluationReport;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticFunctionDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticFunctionDetailsList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkDetailsList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticParameterDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticParameterDetailsList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValue;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 *
 * @author Cesar Coelho
 */
public final class StatisticManager {

    private static final int SAVING_PERIOD = 20;  // Used to store the uniqueAValObjId only once every "SAVINGPERIOD" times
    private static final String STR_STAT_FUNC_NAME_MAXIMUM = "Maximum";
    private static final String STR_STAT_FUNC_NAME_MINIMUM = "Minimum";
    private static final String STR_STAT_FUNC_NAME_MEAN_AVERAGE = "Mean average";
    private static final String STR_STAT_FUNC_NAME_STD_DEVIATION = "Standard deviation";

    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdAIns;

    private final HashMap<Long, StatisticFunctionDetails> statFunctions;
    private final HashMap<Long, StatisticParameterDetails> statLinks;
    private final HashMap<Long, StatisticEvaluationReport> statEvaluationReports;
    private final DataSets dataSets;
    private final COMServicesProvider comServices;
    private final transient ParameterManager parameterManager;
    private final transient ExternalStatisticFunctionsInterface externalStatFunctions;

    public StatisticManager(COMServicesProvider comServices, ParameterManager parameterManager, ExternalStatisticFunctionsInterface externalStatFunctions) {
        this.comServices = comServices;
        this.parameterManager = parameterManager;
        this.externalStatFunctions = externalStatFunctions;

        this.statFunctions = new HashMap<Long, StatisticFunctionDetails>();
        this.statLinks = new HashMap<Long, StatisticParameterDetails>();
        this.statEvaluationReports = new HashMap<Long, StatisticEvaluationReport>();
        this.dataSets = new DataSets();

        if (comServices != null) {  // Do we have COM services?
            if (comServices.getArchiveService() == null) {  // No Archive?
                this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
                this.uniqueObjIdAIns = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
//            this.load(); // Load the file
            } else {

            }
        }

        // Insert the default statistic functions
        this.statFunctions.put(new Long(1), new StatisticFunctionDetails(new Identifier(STR_STAT_FUNC_NAME_MAXIMUM), ""));
        this.statFunctions.put(new Long(2), new StatisticFunctionDetails(new Identifier(STR_STAT_FUNC_NAME_MINIMUM), ""));
        this.statFunctions.put(new Long(3), new StatisticFunctionDetails(new Identifier(STR_STAT_FUNC_NAME_MEAN_AVERAGE), ""));
        this.statFunctions.put(new Long(4), new StatisticFunctionDetails(new Identifier(STR_STAT_FUNC_NAME_STD_DEVIATION), ""));

    }
    
    public COMServicesProvider getCOMservices(){
        return comServices;
    }

    public HashMap<Long, StatisticParameterDetails> getStatisticLinks(){
        return statLinks;
    }
    
    public DataSets getDataSets() {
        return dataSets;
    }

    public StatisticParameterDetails getStatisticLink(Long objIdLink) {
        return this.statLinks.get(objIdLink);
    }

    public StatisticFunctionDetails getStatisticFunction(Long objIdFunc) {
        StatisticFunctionDetails fun = this.statFunctions.get(objIdFunc);

        if (fun == null && externalStatFunctions != null) {  // Dind't find it? Maybe it is a custom function...
            fun = externalStatFunctions.getCustomStatisticFunction(objIdFunc);
        }

        return fun;
    }

    public ParameterDefinitionDetails getParameterDefinition(Long input) {
        return this.parameterManager.get(input);
    }

    public ParameterValue getParameterValue(Long input) throws MALInteractionException {
        return this.parameterManager.getParameterValue(this.statLinks.get(input).getParameterId().getInstId());
    }

    protected LongList listAllLinks() {
        LongList list = new LongList();
        list.addAll(statLinks.keySet());
        return list;
    }

    public Long storeAndGenerateStatValueInsobjId(StatisticValue sVal, Long related, SingleConnectionDetails connectionDetails) {
        if (comServices.getArchiveService() == null) {
            uniqueObjIdAIns++;
            if (uniqueObjIdAIns % SAVING_PERIOD == 0) // It is used to avoid constant saving every time we generate a new obj Inst identifier.
            {
//                this.save();
            }
            return this.uniqueObjIdAIns;
        } else {
            StatisticValueList sValList = new StatisticValueList();
            sValList.add(sVal);

            try {
                LongList objIds = comServices.getArchiveService().store(
                        true,
                        StatisticHelper.STATISTICVALUEINSTANCE_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(related, null, connectionDetails),
                        sValList,
                        null);

                if (objIds.size() == 1) {
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(StatisticManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(StatisticManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }

    }

    protected boolean setReportingEnabled(Long objId, Boolean bool, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        StatisticParameterDetails link = this.getStatisticLink(objId);

        if (link == null) {
            return false;
        }

        if (link.getLinkDetails().getReportingEnabled().booleanValue() == bool) // Is it set with the requested value already?
        {
            return false; // the value was not changed
        }
        link.getLinkDetails().setReportingEnabled(bool);
        this.update(objId, link, connectionDetails);
        return true;
    }

    protected void setReportingEnabledAll(Boolean bool, SingleConnectionDetails connectionDetails) {
        LongList objIds = new LongList();
        objIds.addAll(statLinks.keySet());

        for (Long objId : objIds) {
            StatisticParameterDetails link = this.getStatisticLink(objId);
            link.getLinkDetails().setReportingEnabled(bool);
            this.update(objId, link, connectionDetails);
        }
    }

    protected boolean statLinkExists(Long input) {
        return statLinks.containsKey(input);
    }
/*
    public Boolean resetEvaluation(Long objId) {
        if (!this.statLinkExists(objId)) {
            return false;
        }

        StatisticValue statValue = new StatisticValue();
        statValue.setStartTime(ConfigurationProvider.getTimestampMillis());
        statValue.setSampleCount(new UInteger(0));

        StatisticEvaluationReport statEval = new StatisticEvaluationReport(); // Let's add a new one...
        statEval.setLinkId(objId);
        statEval.setValue(statValue);

        this.statEvaluations.put(objId, statEval);  // The old value is replaced

        return true;
    }
*/
    public StatisticFunctionDetailsList getAll() {
        StatisticFunctionDetailsList list = new StatisticFunctionDetailsList();
        list.addAll(this.statFunctions.values());
        return list;
    }

    public Long add(StatisticParameterDetails statLink, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5

        if (comServices.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.statLinks.put(uniqueObjIdDef, statLink);

//            this.save();
            return uniqueObjIdDef;
        } else {
            StatisticLinkDetailsList links = new StatisticLinkDetailsList();
            links.add(statLink.getLinkDetails());
            ObjectId source = new ObjectId(ParameterHelper.PARAMETERDEFINITION_OBJECT_TYPE,
                    statLink.getParameterId());

            try {
                LongList objIds = comServices.getArchiveService().store(
                        true,
                        StatisticHelper.STATISTICLINK_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(statLink.getStatFuncInstId(), source, connectionDetails),
                        links,
                        null);

                if (objIds.size() == 1) {
                    this.statLinks.put(objIds.get(0), statLink);
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

    public boolean update(Long objIdLink, StatisticParameterDetails statLink, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        // requirement: 3.7.2.13
        boolean success = (this.statLinks.remove(objIdLink) != null);

        if (success) {
            this.statLinks.put(objIdLink, statLink);
        } else {
            return false;
        }

        if (comServices.getArchiveService() != null) {  // It should also update in the COM Archive
            try {
                StatisticLinkDetailsList links = new StatisticLinkDetailsList();
                links.add(statLink.getLinkDetails());

                ArchiveDetails archiveDetails = HelperArchive.getArchiveDetailsFromArchive(comServices.getArchiveService(),
                        StatisticHelper.STATISTICLINK_OBJECT_TYPE, connectionDetails.getDomain(), objIdLink);

                ArchiveDetailsList archiveDetailsList = new ArchiveDetailsList();
                archiveDetailsList.add(archiveDetails);

                comServices.getArchiveService().update(
                        StatisticHelper.STATISTICLINK_OBJECT_TYPE,
                        connectionDetails.getDomain(),
                        archiveDetailsList,
                        links,
                        null);

            } catch (MALException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (MALInteractionException ex) {
                Logger.getLogger(ParameterManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        return success;
    }

    public boolean delete(Long objId) { // requirement: 3.3.2.5

        if (this.statLinks.remove(objId) == null) {
            return false;
        }

        if (comServices.getArchiveService() == null) {
//            this.save();
        }

        return true;
    }

    protected ObjectId storeCOMOperationActivity(final MALInteraction interaction) {
        if (comServices.getActivityTrackingService() != null) {
            return comServices.getActivityTrackingService().storeCOMOperationActivity(interaction, null);
        } else {
            return null;
        }
    }
    
    protected StatisticEvaluationReport getStatisticEvaluationReport(Long input) {
        return this.statEvaluationReports.get(input);
    }

    protected void addStatisticValueToStatisticEvaluationReport(Long input, StatisticValue statisticValue) {
        StatisticEvaluationReport report = this.statEvaluationReports.get(input);
        
        if (report == null){ // No report
            report = new StatisticEvaluationReport();
            report.setLinkId(input);
            this.statEvaluationReports.put(input, report);
        }
        
        report.setValue(statisticValue);
    }
    
    protected void resetStatisticEvaluationReport(Long input) {
        this.statEvaluationReports.remove(input);
    }
    
    protected StatisticValue generateStatisticValue(Long statFuncId, TimeList times, AttributeValueList values) {
        StatisticFunctionDetails statFunction = this.getStatisticFunction(statFuncId);
        
        if (statFunction == null) {
            return null;
        }

        if (statFunction.getName().toString().equals(STR_STAT_FUNC_NAME_MAXIMUM)) { // Maximum
            return this.generateStatisticValueMaximum(times, values);
        }

        if (statFunction.getName().toString().equals(STR_STAT_FUNC_NAME_MINIMUM)) { // Minimum
            return this.generateStatisticValueMinimum(times, values);
        }

        if (statFunction.getName().toString().equals(STR_STAT_FUNC_NAME_MEAN_AVERAGE)) { // Mean average
            return this.generateStatisticValueMeanAverage(times, values);
        }

        if (statFunction.getName().toString().equals(STR_STAT_FUNC_NAME_STD_DEVIATION)) { // Standard deviation
            return this.generateStatisticValueStandardDeviation(times, values);
        }
        
        // Generate Statistic Value from external
        if (externalStatFunctions != null){
            return externalStatFunctions.generateCustomStatisticValue(statFuncId, times, values);
        }
        
        return null;
    }
    
    private StatisticValue generateStatisticValueMaximum(TimeList times, AttributeValueList values) {
        StatisticValue statValue = this.newStatisticValue(times, values);
        
        if (statValue == null){
            return null;
        }
        
        int maximum = 0;
        
        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size() ; i++){
            if (values.get(i) == null){
                continue;
            }

            // Comparison must be done here
            if ( HelperCOM.evaluateExpression(values.get(i).getValue(), ExpressionOperator.GREATER, values.get(maximum).getValue()) ){
                maximum = i;
            }
        }
        
        statValue.setValue(values.get(maximum).getValue());
        statValue.setValueTime(times.get(maximum));
        
        
        return statValue;
    }

    private StatisticValue generateStatisticValueMinimum(TimeList times, AttributeValueList values) {
        StatisticValue statValue = this.newStatisticValue(times, values);
        
        if (statValue == null){
            return null;
        }
        
        int minimum = 0;
        
        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size() ; i++){
            if (values.get(i) == null){
                continue;
            }

            // Comparison must be done here
            if ( HelperCOM.evaluateExpression(values.get(i).getValue(), ExpressionOperator.LESS, values.get(minimum).getValue()) ){
                minimum = i;
            }
        }
        
        statValue.setValue(values.get(minimum).getValue());
        statValue.setValueTime(times.get(minimum));
        
        return statValue;
    }

    private StatisticValue generateStatisticValueMeanAverage(TimeList times, AttributeValueList values) {
        StatisticValue statValue = this.newStatisticValue(times, values);
        
        if (statValue == null){
            return null;
        }
        
        //----------------------Mean Calculation--------------------------
        double mean = 0;
        
        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size() ; i++){
            if (values.get(i) == null){
                continue;
            }

            mean += HelperAttributes.attribute2double(values.get(i).getValue());
        }
        
        mean = mean / ((double) values.size());
        //----------------------------------------------------------------

        statValue.setValue( (Attribute) HelperAttributes.javaType2Attribute(new Double (mean)) );
        statValue.setValueTime(null); // StatisticValue structure: "Shall be NULL if not applicable for cases such as 'mean average'."
        
        return statValue;
    }
    
    private StatisticValue generateStatisticValueStandardDeviation(TimeList times, AttributeValueList values) {
        StatisticValue statValue = this.newStatisticValue(times, values);
        
        if (statValue == null){
            return null;
        }
        
        //----------------------Mean Calculation--------------------------
        double mean = 0;
        
        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size() ; i++){
            if (values.get(i) == null){
                continue;
            }

            mean += HelperAttributes.attribute2double(values.get(i).getValue());
        }
        
        mean = mean / ((double) values.size());
        //----------------------------------------------------------------

        double variance = 0;
        
        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size() ; i++){
            double tmp = HelperAttributes.attribute2double(values.get(i).getValue()) - mean;
            variance += Math.pow(tmp, 2);
        }
        
        variance = variance / ( (double) (values.size() - 1) );
        variance = Math.sqrt(variance);
        
        statValue.setValue( (Attribute) HelperAttributes.javaType2Attribute(new Double (variance)) );
        statValue.setValueTime(null); // StatisticValue structure: "Shall be NULL if not applicable for cases such as 'mean average'."
        
        return statValue;
    }
    
    private StatisticValue newStatisticValue(TimeList times, AttributeValueList values){
        if (times == null || values == null){ // Nothing to do here...
            return null;
        }

        // Different list sizes or zero elements?
        if (times.size() != values.size()  || values.size() == 0){
            return null;
        }
        
        StatisticValue statValue = new StatisticValue();
        statValue.setStartTime(times.get(0));
        statValue.setEndTime(times.get(times.size() - 1)); // Last element
        statValue.setSampleCount(new UInteger(times.size()));
        
        return statValue;
    }

    public Boolean reconfigureLinks(LongList objIds, StatisticParameterDetailsList statisticParameterDetailsList) {
        if (objIds.size() != statisticParameterDetailsList.size()){
            return false;
        }

        statLinks.clear();
        for (int i = 0; i < objIds.size(); i++){
            statLinks.put(objIds.get(i), statisticParameterDetailsList.get(i));
        }
        return true;
    }
    
    public class DataSets {

        private final HashMap<Long, AttributeValueList> dataSets = new HashMap<Long, AttributeValueList>();
        private final HashMap<Long, TimeList> timeSets = new HashMap<Long, TimeList>();
        private final Semaphore semaphore = new Semaphore(1);

        public void lock() {
            try {
                this.semaphore.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(StatisticManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void unlock() {
            this.semaphore.release();
        }

        public TimeList getTimeSet(Long objId) {
            return this.timeSets.get(objId);
        }

        public AttributeValueList getDataSet(Long objId) {
            return this.dataSets.get(objId);
        }

        public void resetDataSet(Long objId) {
            this.dataSets.put(objId, new AttributeValueList());
            this.timeSets.put(objId, new TimeList());
        }

        public void addAttributeToDataSet(Long objId, Attribute attribute, Time time) {
            this.lock();

            if (this.dataSets.get(objId) == null) {
                this.dataSets.put(objId, new AttributeValueList());
                this.timeSets.put(objId, new TimeList());
            }

            if (attribute == null) {
                this.dataSets.get(objId).add(null);
                this.timeSets.get(objId).add(time);
            } else {
                AttributeValue value = new AttributeValue();
                value.setValue(attribute);
                this.dataSets.get(objId).add(value);
                this.timeSets.get(objId).add(time);
            }

            this.unlock();
        }

    }

}
