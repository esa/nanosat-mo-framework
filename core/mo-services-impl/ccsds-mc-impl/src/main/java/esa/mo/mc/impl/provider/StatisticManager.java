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
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.ConfigurationProviderSingleton;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.mc.impl.interfaces.ExternalStatisticFunctionsInterface;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.ccsds.moims.mo.mc.statistic.structures.StatisticCreationRequest;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticCreationRequestList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticEvaluationReport;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticFunctionDetails;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticFunctionDetailsList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticLinkDetailsList;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValue;
import org.ccsds.moims.mo.mc.statistic.structures.StatisticValueList;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;

/**
 *
 * @author Cesar Coelho
 */
public final class StatisticManager {

    private static final int SAVING_PERIOD = 20;  // Used to store the uniqueAValObjId only once every "SAVINGPERIOD" times
    private static final String STR_STAT_FUNC_NAME_MAXIMUM = "MAX";
    private static final String STR_STAT_FUNC_NAME_MINIMUM = "MIN";
    private static final String STR_STAT_FUNC_NAME_MEAN_AVERAGE = "MEAN";
    private static final String STR_STAT_FUNC_NAME_STD_DEVIATION = "SD";

    private Long uniqueObjIdLink; // Unique objId Link (different for every Definition)
    private Long uniqueObjIdLinkDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdAIns;

    private final HashMap<Long, StatisticFunctionDetails> statFunctions;
    private final HashMap<Long, StatisticCreationRequest> statLinks;
    private final HashMap<Long, StatisticEvaluationReport> statEvaluationReports;
    private final HashMap<Long, Long> statLinkDefIdsByStatLinkIds;
    private final DataSets dataSets;
    private final COMServicesProvider comServices;
    private final transient ParameterManager parameterManager;
    private final transient ExternalStatisticFunctionsInterface externalStatFunctions;

    public StatisticManager(COMServicesProvider comServices, ParameterManager parameterManager,
        ExternalStatisticFunctionsInterface externalStatFunctions) {
        this.comServices = comServices;
        this.parameterManager = parameterManager;
        this.externalStatFunctions = externalStatFunctions;

        this.statFunctions = new HashMap<>();
        this.statLinks = new HashMap<>();
        this.statEvaluationReports = new HashMap<>();
        this.statLinkDefIdsByStatLinkIds = new HashMap<>();
        this.dataSets = new DataSets();

        if (comServices != null) {  // Do we have COM services?
            if (comServices.getArchiveService() == null) {  // No Archive?
                this.uniqueObjIdLink = 0L; // The zeroth value will not be used (reserved for the wildcard)
                this.uniqueObjIdAIns = 0L; // The zeroth value will not be used (reserved for the wildcard)
                //            this.load(); // Load the file
            } else {

            }
        }

        // Insert the default statistic functions requirements: 3.6.4.a, b, c, d, e
        this.statFunctions.put(1L, new StatisticFunctionDetails(new Identifier(STR_STAT_FUNC_NAME_MAXIMUM), ""));
        this.statFunctions.put(2L, new StatisticFunctionDetails(new Identifier(STR_STAT_FUNC_NAME_MINIMUM), ""));
        this.statFunctions.put(3L, new StatisticFunctionDetails(new Identifier(STR_STAT_FUNC_NAME_MEAN_AVERAGE), ""));
        this.statFunctions.put(4L, new StatisticFunctionDetails(new Identifier(STR_STAT_FUNC_NAME_STD_DEVIATION), ""));

    }

    public COMServicesProvider getCOMservices() {
        return comServices;
    }

    public HashMap<Long, StatisticCreationRequest> getStatisticLinks() {
        return statLinks;
    }

    public DataSets getDataSets() {
        return dataSets;
    }

    public StatisticCreationRequest getStatisticLink(Long objIdLink) {
        return this.statLinks.get(objIdLink);
    }

    public Long getStatisticLinkDefinitionId(Long statLinkId) {
        return statLinkDefIdsByStatLinkIds.get(statLinkId);
    }

    public StatisticFunctionDetails getStatisticFunction(Long objIdFunc) {
        StatisticFunctionDetails fun = this.statFunctions.get(objIdFunc);

        if (fun == null && externalStatFunctions != null) {  // Dind't find it? Maybe it is a custom function... requirement: 3.6.4.f
            fun = externalStatFunctions.getCustomStatisticFunction(objIdFunc);
        }

        return fun;
    }

    public LongList getStatisticLinksForFunction(Long statFuncId) {
        LongList foundList = new LongList();
        for (Map.Entry<Long, StatisticCreationRequest> entry : statLinks.entrySet()) {
            if (entry.getValue().getStatFuncInstId().equals(statFuncId)) {
                foundList.add(entry.getKey());
            }
        }
        return foundList;
    }

    public ParameterDefinitionDetails getParameterDefinition(Long input) {
        return this.parameterManager.getParameterDefinition(input);
    }

    public ParameterValue getParameterValue(Long statLinkId) throws MALInteractionException {
        return this.parameterManager.getParameterValue(this.statLinks.get(statLinkId).getParameterId().getInstId());
    }

    protected LongList listAllLinks() {
        LongList list = new LongList();
        list.addAll(statLinks.keySet());
        return list;
    }

    public Long storeAndGenerateStatValueInsobjId(StatisticValue sVal, Long related,
        SingleConnectionDetails connectionDetails, ObjectId source) {
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
                LongList objIds = comServices.getArchiveService().store( //requirement: 3.6.6.c
                    true, StatisticHelper.STATISTICVALUEINSTANCE_OBJECT_TYPE, //requirement: 3.6.4.i
                    ConfigurationProviderSingleton.getDomain(), HelperArchive.generateArchiveDetailsList(related,
                        source, connectionDetails), //requirement: 3.6.4. , n, o 
                    sValList, null);

                if (objIds.size() == 1) {
                    return objIds.get(0);
                }

            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(StatisticManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }

    }

    protected boolean setReportingEnabled(Long objId, Boolean bool, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        StatisticCreationRequest link = this.getStatisticLink(objId);

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
            StatisticCreationRequest link = this.getStatisticLink(objId);
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

    public ObjectInstancePair add(StatisticCreationRequest statLink, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5

        Long newLinkId;
        Long newLinkDefId;

        if (comServices.getArchiveService() == null) {
            newLinkId = ++uniqueObjIdLink; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            newLinkDefId = ++uniqueObjIdLinkDef;
        } else {
            try {
                // store the StatisticLink object
                ObjectId source = new ObjectId(ParameterHelper.PARAMETERIDENTITY_OBJECT_TYPE, statLink
                    .getParameterId());
                LongList linkIds = comServices.getArchiveService().store( //requirement: 3.6.6.b, 3.6.13.2.i
                    true, StatisticHelper.STATISTICLINK_OBJECT_TYPE, //requirement: 3.6.4.g, h
                    ConfigurationProviderSingleton.getDomain(), HelperArchive.generateArchiveDetailsList(statLink
                        .getStatFuncInstId(), source, connectionDetails), //requirement: 3.6.4.j, k
                    null, null);

                newLinkId = linkIds.get(0);

                // store the StatisticLinkDefinition object
                StatisticLinkDetailsList linkDetails = new StatisticLinkDetailsList();
                linkDetails.add(statLink.getLinkDetails());

                LongList linkDefIds = comServices.getArchiveService().store( //requirement: 3.6.6.b, 3.6.13.2.i
                    true, StatisticHelper.STATISTICLINKDEFINITION_OBJECT_TYPE, //requirement: 3.6.4.g, h
                    ConfigurationProviderSingleton.getDomain(), HelperArchive.generateArchiveDetailsList(newLinkId,
                        null, connectionDetails), //requirement: 3.6.4.j, k
                    linkDetails, null);

                newLinkDefId = linkDefIds.get(0);

            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(StatisticManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        this.statLinks.put(newLinkId, statLink);
        this.statLinkDefIdsByStatLinkIds.put(newLinkId, newLinkDefId);
        return new ObjectInstancePair(newLinkId, newLinkDefId);

    }

    public Long update(Long statLinkId, StatisticCreationRequest statLink, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5

        Long newLinkDefId;
        if (comServices.getArchiveService() == null) {
            newLinkDefId = ++uniqueObjIdLinkDef; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
        } else {
            try {
                StatisticLinkDetailsList links = new StatisticLinkDetailsList();
                links.add(statLink.getLinkDetails());

                LongList linkDefIds = comServices.getArchiveService().store( //requirement: 3.6.15.2.i
                    true, StatisticHelper.STATISTICLINKDEFINITION_OBJECT_TYPE, ConfigurationProviderSingleton
                        .getDomain(), HelperArchive.generateArchiveDetailsList(statLinkId, null, connectionDetails),
                    links, null);

                newLinkDefId = linkDefIds.get(0);
            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(StatisticManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        // requirement: 3.7.2.13
        this.statLinks.put(statLinkId, statLink);
        this.statLinkDefIdsByStatLinkIds.put(statLinkId, newLinkDefId);

        return newLinkDefId;
    }

    public boolean delete(Long objId) { // requirement: 3.3.2.5
        if (this.statLinks.remove(objId) == null) {
            return false;
        }
        this.statLinkDefIdsByStatLinkIds.remove(objId);
        return true;
    }

    protected ObjectId storeCOMOperationActivity(final MALInteraction interaction) {
        if (comServices.getActivityTrackingService() != null) {
            return comServices.getActivityTrackingService().storeCOMOperationActivity(interaction, null);
        } else {
            return null;
        }
    }

    protected StatisticEvaluationReport getStatisticEvaluationReport(Long statLinkId) {
        return this.statEvaluationReports.get(statLinkId);
    }

    protected void addStatisticValueToStatisticEvaluationReport(Long input, StatisticValue statisticValue) {
        StatisticEvaluationReport report = this.statEvaluationReports.get(input);

        if (report == null) { // No report
            report = new StatisticEvaluationReport();
            report.setLinkId(input);
            this.statEvaluationReports.put(input, report);
        }

        report.setValue(statisticValue);
    }

    protected void resetStatisticEvaluationReport(Long statLinkId) {
        this.statEvaluationReports.remove(statLinkId);
    }

    protected StatisticValue generateStatisticValue(Long statFuncId, long paramIdentityId, TimeList times,
        AttributeValueList values) {
        StatisticFunctionDetails statFunction = this.getStatisticFunction(statFuncId);

        if (statFunction == null) {
            return null;
        }

        //requirment: 3.6.2.b, c
        if (statFunction.getName().toString().equals(STR_STAT_FUNC_NAME_MAXIMUM)) { // Maximum
            return this.generateStatisticValueMaximum(paramIdentityId, times, values);
        }

        if (statFunction.getName().toString().equals(STR_STAT_FUNC_NAME_MINIMUM)) { // Minimum
            return this.generateStatisticValueMinimum(paramIdentityId, times, values);
        }

        if (statFunction.getName().toString().equals(STR_STAT_FUNC_NAME_MEAN_AVERAGE)) { // Mean average
            return this.generateStatisticValueMeanAverage(paramIdentityId, times, values);
        }

        if (statFunction.getName().toString().equals(STR_STAT_FUNC_NAME_STD_DEVIATION)) { // Standard deviation
            return this.generateStatisticValueStandardDeviation(paramIdentityId, times, values);
        }

        // Generate Statistic Value from external
        if (externalStatFunctions != null) {
            return externalStatFunctions.generateCustomStatisticValue(statFuncId, times, values);
        }

        return null;
    }

    private StatisticValue generateStatisticValueMaximum(long paramIdentityId, TimeList times,
        AttributeValueList values) {
        StatisticValue statValue = this.newStatisticValue(paramIdentityId, times, values);

        if (statValue == null) {
            return null;
        }

        int maximum = 0;

        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                continue;
            }

            // Comparison must be done here
            //requirement: 3.6.3.h report first occurance of max value
            if (HelperCOM.evaluateExpression(values.get(i).getValue(), ExpressionOperator.GREATER, values.get(maximum)
                .getValue())) {
                maximum = i;
            }
        }

        statValue.setValue(values.get(maximum).getValue());
        statValue.setValueTime(times.get(maximum));

        return statValue;
    }

    private StatisticValue generateStatisticValueMinimum(long paramIdentityId, TimeList times,
        AttributeValueList values) {
        StatisticValue statValue = this.newStatisticValue(paramIdentityId, times, values);

        if (statValue == null) {
            return null;
        }

        int minimum = 0;

        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                continue;
            }

            // Comparison must be done here
            //requirement: 3.6.3.i report first occurance of min value
            if (HelperCOM.evaluateExpression(values.get(i).getValue(), ExpressionOperator.LESS, values.get(minimum)
                .getValue())) {
                minimum = i;
            }
        }

        statValue.setValue(values.get(minimum).getValue());
        statValue.setValueTime(times.get(minimum));

        return statValue;
    }

    private StatisticValue generateStatisticValueMeanAverage(long paramIdentityId, TimeList times,
        AttributeValueList values) {
        StatisticValue statValue = this.newStatisticValue(paramIdentityId, times, values);

        if (statValue == null) {
            return null;
        }

        //----------------------Mean Calculation--------------------------
        double mean = 0;

        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                continue;
            }

            mean += HelperAttributes.attribute2double(values.get(i).getValue());
        }

        mean = mean / ((double) values.size());
        //----------------------------------------------------------------

        //requirement: 3.6.3.j report double value
        statValue.setValue((Attribute) HelperAttributes.javaType2Attribute(mean));
        statValue.setValueTime(null); // StatisticValue structure: "Shall be NULL if not applicable for cases such as 'mean average'."

        return statValue;
    }

    private StatisticValue generateStatisticValueStandardDeviation(long paramIdentityId, TimeList times,
        AttributeValueList values) {
        StatisticValue statValue = this.newStatisticValue(paramIdentityId, times, values);

        if (statValue == null) {
            return null;
        }

        //----------------------Mean Calculation--------------------------
        double mean = 0;

        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i) == null) {
                continue;
            }

            mean += HelperAttributes.attribute2double(values.get(i).getValue());
        }

        mean = mean / ((double) values.size());
        //----------------------------------------------------------------

        double variance = 0;

        // Go through all the data sets and find the maximum value...
        for (int i = 0; i < values.size(); i++) {
            double tmp = HelperAttributes.attribute2double(values.get(i).getValue()) - mean;
            variance += Math.pow(tmp, 2);
        }

        variance = variance / ((double) (values.size() - 1));
        variance = Math.sqrt(variance);

        //requirement: 3.6.3.k report double value
        statValue.setValue((Attribute) HelperAttributes.javaType2Attribute(variance));
        statValue.setValueTime(null); // StatisticValue structure: "Shall be NULL if not applicable for cases such as 'mean average'."

        return statValue;
    }

    private StatisticValue newStatisticValue(long paramIdentityId, TimeList times, AttributeValueList values) {
        if (times == null || values == null) { // Nothing to do here...
            return null;
        }

        // Different list sizes or zero elements?
        if (times.size() != values.size() || values.size() == 0) {
            return null;
        }

        StatisticValue statValue = new StatisticValue();
        statValue.setStartTime(times.get(0));
        statValue.setEndTime(times.get(times.size() - 1)); // Last element
        statValue.setSampleCount(new UInteger(times.size()));
        long paramDefInstId = parameterManager.getDefinitionId(paramIdentityId);
        statValue.setParamDefInstId(paramDefInstId);

        return statValue;
    }

    public Boolean reconfigureLinks(LongList objIds, StatisticCreationRequestList statisticParameterDetailsList) {
        if (objIds.size() != statisticParameterDetailsList.size()) {
            return false;
        }

        statLinks.clear();
        for (int i = 0; i < objIds.size(); i++) {
            statLinks.put(objIds.get(i), statisticParameterDetailsList.get(i));
        }
        return true;
    }

    boolean existsParameterIdentity(Long instId) {
        return parameterManager.existsIdentity(instId);
    }

    public static class DataSets {

        private final HashMap<Long, AttributeValueList> dataSets = new HashMap<>();
        private final HashMap<Long, TimeList> timeSets = new HashMap<>();
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

        public void removeDataSet(Long objId) {
            this.dataSets.remove(objId);
            this.timeSets.remove(objId);
        }

        public Integer getOldestIndex(Long objId, double oldestTimeInMs) {
            // get index of oldestTimeInMs (or the next higher index, if oldestTimeInMs not in list)
            TimeList allTimes = timeSets.get(objId);
            if (allTimes == null) {
                return null;
            }
            int i = 0;
            for (; i < allTimes.size() && allTimes.get(i).getValue() < oldestTimeInMs; i++)
                ;
            return i;
        }

    }

}
