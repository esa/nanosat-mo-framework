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

import esa.mo.com.impl.provider.ArchivePersistenceObject;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.com.impl.util.HelperArchive;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperAttributes;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.TimeList;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.check.CheckHelper;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetailsList;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetailsList;
import org.ccsds.moims.mo.mc.check.structures.CheckState;
import org.ccsds.moims.mo.mc.check.structures.CompoundCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ConstantCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.DeltaCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.LimitCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.AttributeValue;
import org.ccsds.moims.mo.mc.structures.AttributeValueList;

/**
 *
 * @author Cesar Coelho
 */
public final class CheckManager {

    private static final int SAVING_PERIOD = 20;  // Used to store the uniqueAValObjId only once every "SAVINGPERIOD" times
    private Long uniqueObjIdDef; // Unique objId Definition (different for every Definition)
    private Long uniqueObjIdLink;

    private final HashMap<Long, CheckDefinitionDetails> checkDefs;
    private final HashMap<Long, CheckLinkDetails> checkLinkDetails;
    private final HashMap<Long, ObjectDetails> checkLinkLinks;
    private final DataSets dataSets;
    private final COMServicesProvider comServices;
    private final ParameterManager parameterManager;
    private boolean checkServiceEnabled;
    private final ObjectType objTypeCheckDefinition;

    public CheckManager(COMServicesProvider comServices, ParameterManager parameterManager) {
        this.comServices = comServices;
        this.parameterManager = parameterManager;
        this.checkServiceEnabled = true;

        this.objTypeCheckDefinition = CheckHelper.CHECKIDENTITY_OBJECT_TYPE;

        this.checkDefs = new HashMap<Long, CheckDefinitionDetails>();
        this.checkLinkDetails = new HashMap<Long, CheckLinkDetails>();
        this.checkLinkLinks = new HashMap<Long, ObjectDetails>();
        this.dataSets = new DataSets();

        if (comServices != null) {  // Do we have COM services?
            if (comServices.getArchiveService() == null) {  // No Archive?
                this.uniqueObjIdDef = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
                this.uniqueObjIdLink = new Long(0); // The zeroth value will not be used (reserved for the wildcard)
            } else {

            }
        }
    }

    protected COMServicesProvider getCOMservices() {
        return comServices;
    }

    protected HashMap<Long, CheckDefinitionDetails> getCheckDefs() {
        return checkDefs;
    }

    protected HashMap<Long, ObjectDetails> getCheckLinksLinks() {
        return checkLinkLinks;
    }

    protected boolean exists(Long input) {
        return checkDefs.containsKey(input);
    }

    protected Long list(Identifier input) {
        final LongList objIds = this.listAll();
        for (Long objId : objIds) {
            if (compareName(objId, input)) {
                return objId;
            }
        }
        return null; // Not found!
    }

    protected LongList listAll() {
        LongList list = new LongList();
        list.addAll(checkDefs.keySet());
        return list;
    }

    protected Boolean compareName(Long objId, Identifier name) {
        return checkDefs.get(objId).getName().equals(name);
    }

    public DataSets getDataSets() {
        return dataSets;
    }

    public CheckLinkDetails getCheckLinkDetails(Long objIdLink) {
        return this.checkLinkDetails.get(objIdLink);
    }

    public ObjectDetails getCheckLinkLinks(Long objIdLink) {
        return this.checkLinkLinks.get(objIdLink);
    }

    public ParameterDefinitionDetails getParameterDefinition(Long input) {
        return this.parameterManager.get(input);
    }

    protected LongList listAllLinks() {
        LongList list = new LongList();
        list.addAll(checkLinkDetails.keySet());
        return list;
    }

    protected void setCheckServiceEnabled(boolean flag) {
        this.checkServiceEnabled = flag;
    }

    protected boolean getCheckServiceEnabled() {
        return checkServiceEnabled;
    }

    protected LongList findLinksPointingToDefinition(LongList objIdDefinitions) {
        LongList output = new LongList();

        // Let's go definition by definition
        for (int i = 0; i < objIdDefinitions.size(); i++) {
            HashMap<Long, ObjectDetails> allLinks = this.checkLinkLinks;
            ArrayList<ObjectDetails> details = new ArrayList<ObjectDetails>();
            ArrayList<Long> key = new ArrayList<Long>();
            details.addAll(allLinks.values());
            key.addAll(allLinks.keySet());

            // Cycle all the available links
            for (int k = 0; k < details.size(); k++) {
                if (details.get(k).getRelated().equals(objIdDefinitions.get(i))) {
                    // This check link is pointing to that definition...
                    output.add(key.get(k));
                }
            }
        }

        return output;
    }

    protected boolean setCheckEnabled(Long objIdLink, Boolean bool, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        CheckLinkDetails link = this.getCheckLinkDetails(objIdLink);

        if (link == null) {
            return false;
        }

        if (link.getCheckEnabled().booleanValue() == bool) { // Is it set with the requested value already?
            return false; // the value was not changed
        }

        link.setCheckEnabled(bool);
        this.updateLink(objIdLink, link, connectionDetails);
        return true;
    }

    protected void setCheckEnabledAll(Boolean bool, SingleConnectionDetails connectionDetails) {
        LongList objIds = new LongList();
        objIds.addAll(this.checkLinkDetails.keySet());

        for (Long objIdLink : objIds) {
            CheckLinkDetails link = this.getCheckLinkDetails(objIdLink);
            link.setCheckEnabled(bool);
            this.updateLink(objIdLink, link, connectionDetails);
        }

    }

    protected boolean checkLinkExists(Long input) {
        return checkLinkDetails.containsKey(input);
    }

    public Long add(CheckDefinitionDetails checkDef, ObjectId incomeSource, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5

        if (comServices.getArchiveService() == null) {
            uniqueObjIdDef++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.checkDefs.put(uniqueObjIdDef, checkDef);

//            this.save();
            return uniqueObjIdDef;
        } else {

            try {
                CheckDefinitionDetailsList defs = (CheckDefinitionDetailsList) HelperMisc.element2elementList(checkDef);
                defs.add(checkDef);
                ObjectType objType = CheckManager.generateCheckObjectType(checkDef);

                // Store the actual Definition
                LongList objIds = comServices.getArchiveService().store(
                        true,
                        objType,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, incomeSource, connectionDetails),
                        defs,
                        null);

                ObjectId source = new ObjectId(objType, new ObjectKey(connectionDetails.getDomain(), objIds.get(0)));
                IdentifierList nameList = new IdentifierList();
                nameList.add(checkDef.getName());

                // Store the Check Definition
                LongList objIds2 = comServices.getArchiveService().store(
                        true,
                        objTypeCheckDefinition,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, source, connectionDetails),
                        nameList,
                        null);

                if (objIds2.size() == 1) {
                    this.checkDefs.put(objIds2.get(0), checkDef);
                    return objIds.get(0);
                }

            } catch (MALException ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALInteractionException ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return null;
    }

    public boolean update(final Long objId, final CheckDefinitionDetails checkDef, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        // requirement: 3.7.2.13
        boolean success = (this.checkDefs.remove(objId) != null);

        if (success) {
            this.checkDefs.put(objId, checkDef);
        } else {
            return false;
        }

        if (comServices.getArchiveService() != null) {  // It should also update in the COM Archive
            try {
                CheckDefinitionDetailsList defs = (CheckDefinitionDetailsList) HelperMisc.element2elementList(checkDef);
                defs.add(checkDef);

                ObjectType objType = CheckManager.generateCheckObjectType(checkDef);

                LongList objIds2 = comServices.getArchiveService().store(
                        true,
                        objType,
                        connectionDetails.getDomain(),
                        HelperArchive.generateArchiveDetailsList(null, null, connectionDetails),
                        defs,
                        null);

                ArchivePersistenceObject comObjectOriginal = HelperArchive.getArchiveCOMObject(comServices.getArchiveService(),
                        objTypeCheckDefinition, connectionDetails.getDomain(), objId);

                IdentifierList nameList = new IdentifierList();
                nameList.add(checkDef.getName());
                ArchiveDetailsList archList = new ArchiveDetailsList();
                archList.add(comObjectOriginal.getArchiveDetails());

                comServices.getArchiveService().update(
                        comObjectOriginal.getObjectType(),
                        comObjectOriginal.getDomain(),
                        archList,
                        nameList,
                        null);

            } catch (MALException ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (MALInteractionException ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            } catch (Exception ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }
        }

        return success;
    }

    public boolean delete(Long objId) {
        return (this.checkDefs.remove(objId) != null);
    }

    protected ObjectId storeCOMOperationActivity(final MALInteraction interaction) {
        if (comServices.getActivityTrackingService() != null) {
            return comServices.getActivityTrackingService().storeCOMOperationActivity(interaction, null);
        } else {
            return null;
        }
    }

    protected Boolean reconfigureLinkDetails(LongList objIds, CheckLinkDetailsList checkLinkDetailsList) {
        if (objIds == null || checkLinkDetailsList == null) {
            return false;
        }

        if (objIds.size() != checkLinkDetailsList.size()) {
            return false;
        }

        checkLinkDetails.clear();
        for (int i = 0; i < objIds.size(); i++) {
            checkLinkDetails.put(objIds.get(i), checkLinkDetailsList.get(i));
        }
        return true;
    }

    public Boolean reconfigureLinkLinks(LongList objIds, ObjectDetailsList checkLinkLinksList) {
        if (objIds.size() != checkLinkLinksList.size()) {
            return false;
        }

        checkLinkLinks.clear();
        for (int i = 0; i < objIds.size(); i++) {
            checkLinkLinks.put(objIds.get(i), checkLinkLinksList.get(i));
        }
        return true;
    }

    private void updateLink(Long objIdLink, CheckLinkDetails link, SingleConnectionDetails connectionDetails) {

        if (this.checkLinkDetails.remove(objIdLink) != null) {
            this.checkLinkDetails.put(objIdLink, link);
        }

        if (comServices.getArchiveService() == null) {  // It should also update in the COM Archive
            return;
        }

        try {
            ArchivePersistenceObject comObjectOriginal = HelperArchive.getArchiveCOMObject(
                    comServices.getArchiveService(),
                    CheckHelper.CHECKLINK_OBJECT_TYPE,
                    connectionDetails.getDomain(),
                    objIdLink);

            CheckLinkDetailsList objs = new CheckLinkDetailsList();
            objs.add(link);
            ArchiveDetailsList arch = new ArchiveDetailsList();
            arch.add(comObjectOriginal.getArchiveDetails());

            comServices.getArchiveService().update(
                    comObjectOriginal.getObjectType(),
                    comObjectOriginal.getDomain(),
                    arch,
                    objs,
                    null);

        } catch (MALException ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    protected Long addLink(CheckLinkDetails linkDetail, ObjectDetails linkRef, SingleConnectionDetails connectionDetails) {

        if (comServices.getArchiveService() == null) {
            uniqueObjIdLink++; // This line as to go before any writing (because it's initialized as zero and that's the wildcard)
            this.checkLinkDetails.put(uniqueObjIdLink, linkDetail);
            this.checkLinkLinks.put(uniqueObjIdLink, linkRef);
            return uniqueObjIdLink;
        }

        try {
            CheckLinkDetailsList links = new CheckLinkDetailsList();
            links.add(linkDetail);

            // Store the actual Definition
            LongList objIds = comServices.getArchiveService().store(
                    true,
                    CheckHelper.CHECKLINK_OBJECT_TYPE,
                    connectionDetails.getDomain(),
                    HelperArchive.generateArchiveDetailsList(linkRef.getRelated(), linkRef.getSource(), connectionDetails),
                    links,
                    null);

            if (objIds.size() == 1) {
                this.checkLinkDetails.put(objIds.get(0), linkDetail);
                this.checkLinkLinks.put(objIds.get(0), linkRef);
                return objIds.get(0);
            }

        } catch (MALException ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    protected Boolean deleteLink(Long objId) {
        return (this.checkLinkDetails.remove(objId) != null && this.checkLinkLinks.remove(objId) != null);
    }

    protected Boolean reconfigureDefinitions(LongList objInstIds, ArrayList<CheckDefinitionDetails> defs) {

        if (objInstIds.size() != defs.size()) {
            return false;
        }

        checkDefs.clear();
        for (int i = 0; i < objInstIds.size(); i++) {
            checkDefs.put(objInstIds.get(i), defs.get(i));
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
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
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

    public static ObjectType generateCheckObjectType(CheckDefinitionDetails checkDef) {

        if (checkDef instanceof ConstantCheckDefinition) {
            return CheckHelper.CONSTANTCHECK_OBJECT_TYPE;
        }

        if (checkDef instanceof ReferenceCheckDefinition) {
            return CheckHelper.REFERENCECHECK_OBJECT_TYPE;
        }

        if (checkDef instanceof DeltaCheckDefinition) {
            return CheckHelper.DELTACHECK_OBJECT_TYPE;
        }

        if (checkDef instanceof LimitCheckDefinition) {
            return CheckHelper.LIMITCHECK_OBJECT_TYPE;
        }

        if (checkDef instanceof CompoundCheckDefinition) {
            return CheckHelper.COMPOUNDCHECK_OBJECT_TYPE;
        }

        return null;
    }

    protected CheckState evaluateCheckState(final Long linkObjId, final ParameterValue parValue) {

        CheckLinkDetails details = this.checkLinkDetails.get(linkObjId);
        ObjectDetails checkLinkLink = this.checkLinkLinks.get(linkObjId);

        if (!this.checkServiceEnabled) { // Is the Check service globally disabled?
            return CheckState.DISABLED;
        }

        if (!details.getCheckEnabled()) { // Is the Check enabled?
            return CheckState.DISABLED;
        }

        if (details.getCondition() != null) { // Unchecked iteration...
            // Fetch Value
            try {
                ParameterValue conditionParVal = parameterManager.getParameterValue(details.getCondition().getParameterId().getInstId());
                ParameterDefinitionDetails conditionParDef = parameterManager.get(details.getCondition().getParameterId().getInstId());

                if (!isParameterValueValid(conditionParVal, (conditionParDef.getConversion() == null))) {
                    return CheckState.UNCHECKED;
                }

                Boolean conditionEvaluation = parameterManager.evaluateParameterExpression(details.getCondition());

                if (!conditionEvaluation) { // Does the condition evaluate to TRUE?
                    return CheckState.UNCHECKED;
                }
            } catch (MALInteractionException ex) {  // The Parameter is not available in the manager
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        ParameterDefinitionDetails entityParDef = parameterManager.get(checkLinkLink.getSource().getKey().getInstId());

        if (!isParameterValueValid(parValue, (entityParDef.getConversion() == null))) {
            return CheckState.INVALID;
        }

        try {
            //Evaluate Check
            return (this.performCheckEvaluation(linkObjId, parValue)) ? CheckState.OK : CheckState.NOT_OK;
        } catch (MALInteractionException ex) {
            return CheckState.UNCHECKED;  // The Evaluation could not be determined
        }

    }

    protected ParameterValue getParameterValue(Long linkObjId) throws MALInteractionException {
        return parameterManager.getParameterValue(this.checkLinkLinks.get(linkObjId).getSource().getKey().getInstId());
    }

    private boolean isParameterValueValid(ParameterValue pVal, boolean usingRaw) {
        // "Is VALID or using raw and INVALID_CONVERSION?"
        if (pVal.getInvalidSubState().getValue() == 2) { // VALID
            return true;
        }

        if (usingRaw && pVal.getInvalidSubState().getValue() == 3) {  // Using raw and INVALID_CONVERSION
            return true;
        }

        return false;

    }

    private Boolean performCheckEvaluation(Long linkObjId, ParameterValue pVal) throws MALInteractionException {
        if (linkObjId == null || pVal == null) {
            return false;
        }

        CheckDefinitionDetails checkDefinition = this.getCheckDefs().get(checkLinkLinks.get(linkObjId).getRelated());
        Attribute value = (checkLinkDetails.get(linkObjId).getUseConverted()) ? pVal.getConvertedValue() : pVal.getRawValue();

        if (checkDefinition instanceof ConstantCheckDefinition) {
            return this.evaluateConstantCheck((ConstantCheckDefinition) checkDefinition, value);
        }

        if (checkDefinition instanceof LimitCheckDefinition) {
            return this.evaluateLimitCheck((LimitCheckDefinition) checkDefinition, value);
        }

        if (checkDefinition instanceof ReferenceCheckDefinition) {
            return this.evaluateReferenceCheck((ReferenceCheckDefinition) checkDefinition, value);
        }

        if (checkDefinition instanceof DeltaCheckDefinition) {
            return this.evaluateDeltaCheck((DeltaCheckDefinition) checkDefinition, value);
        }

        if (checkDefinition instanceof CompoundCheckDefinition) {
            return this.evaluateCompoundCheck((CompoundCheckDefinition) checkDefinition);
        }

        return false;
    }

    private boolean evaluateConstantCheck(ConstantCheckDefinition checkDef, Attribute value) {

        for (AttributeValue possibleValue : checkDef.getValues()) {
            if (HelperCOM.evaluateExpression(possibleValue, ExpressionOperator.EQUAL, value)) {
                return true;
            }
        }

        return false;
    }

    private boolean evaluateLimitCheck(LimitCheckDefinition checkDef, Attribute value) {

        Boolean withinLimits = null;

        if (checkDef.getUpperLimit() != null && checkDef.getLowerLimit() != null) {
            withinLimits = (HelperCOM.evaluateExpression(value, ExpressionOperator.LESS, checkDef.getUpperLimit())
                    && HelperCOM.evaluateExpression(value, ExpressionOperator.GREATER, checkDef.getLowerLimit()));
        }

        if (checkDef.getUpperLimit() != null && checkDef.getLowerLimit() == null) {
            withinLimits = (HelperCOM.evaluateExpression(value, ExpressionOperator.LESS, checkDef.getUpperLimit()));
        }

        if (checkDef.getUpperLimit() == null && checkDef.getLowerLimit() != null) {
            withinLimits = (HelperCOM.evaluateExpression(value, ExpressionOperator.GREATER, checkDef.getLowerLimit()));
        }

        if (checkDef.getUpperLimit() == null && checkDef.getLowerLimit() != null) {
            withinLimits = true;
        }

        if (withinLimits == null) {
            return false;
        }

        return (withinLimits ^ checkDef.getViolateInRange());
    }

    private boolean evaluateReferenceCheck(ReferenceCheckDefinition referenceCheckDefinition, Attribute value) {

        ReferenceValue referenceValue = referenceCheckDefinition.getCheckReference();

        try {
            ParameterValue pVal = parameterManager.getParameterValue(referenceValue.getParameterId().getInstId());
            Boolean eval = HelperCOM.evaluateExpression(value, referenceCheckDefinition.getOperator(), pVal.getRawValue());
            return eval;
        } catch (MALInteractionException ex) { // The parameter is not available in the manager
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    private boolean evaluateDeltaCheck(DeltaCheckDefinition deltaCheckDefinition, Attribute value) throws MALInteractionException {

        Boolean withinLimits = null;
        ReferenceValue referenceValue = deltaCheckDefinition.getCheckReference();

        ParameterValue pVal = parameterManager.getParameterValue(referenceValue.getParameterId().getInstId());
        Attribute refValue = pVal.getRawValue();

        Attribute upperThreshold = null;
        Attribute lowerThreshold = null;
        Double delta = Math.abs(HelperAttributes.attribute2double(refValue) - HelperAttributes.attribute2double(value));

        if (deltaCheckDefinition.getValueDelta()) { // "If true then thresholds contain value deltas"
            upperThreshold = deltaCheckDefinition.getUpperThreshold();
            lowerThreshold = deltaCheckDefinition.getLowerThreshold();
        } else { // "If false, they contain percentage deltas."
            upperThreshold = new Union(HelperAttributes.attribute2double(deltaCheckDefinition.getUpperThreshold()) * HelperAttributes.attribute2double(refValue));
            lowerThreshold = new Union(HelperAttributes.attribute2double(deltaCheckDefinition.getLowerThreshold()) * HelperAttributes.attribute2double(refValue));
            delta = delta / HelperAttributes.attribute2double(refValue);
        }

        if (deltaCheckDefinition.getUpperThreshold() != null && deltaCheckDefinition.getLowerThreshold() != null) {
            withinLimits = (HelperCOM.evaluateExpression(new Union(delta), ExpressionOperator.LESS, upperThreshold)
                    && HelperCOM.evaluateExpression(new Union(delta), ExpressionOperator.GREATER, lowerThreshold));
        }

        if (deltaCheckDefinition.getUpperThreshold() != null && deltaCheckDefinition.getLowerThreshold() == null) {
            withinLimits = (HelperCOM.evaluateExpression(new Union(delta), ExpressionOperator.LESS, upperThreshold));
        }

        if (deltaCheckDefinition.getUpperThreshold() == null && deltaCheckDefinition.getLowerThreshold() != null) {
            withinLimits = (HelperCOM.evaluateExpression(new Union(delta), ExpressionOperator.GREATER, lowerThreshold));
        }

        if (deltaCheckDefinition.getUpperThreshold() == null && deltaCheckDefinition.getLowerThreshold() != null) {
            withinLimits = true;
        }

        if (withinLimits == null) {
            return false;
        }

        return (withinLimits ^ deltaCheckDefinition.getViolateInRange());
    }

    private boolean evaluateCompoundCheck(CompoundCheckDefinition compoundCheckDefinition) throws MALInteractionException {

        int violationCounter = 0;

        for (int i = 0; i < compoundCheckDefinition.getCheckLinkIds().size(); i++) {

            Long chekLinkId = compoundCheckDefinition.getCheckLinkIds().get(i);
            ObjectDetails checkLinks = this.getCheckLinkLinks(chekLinkId);
            CheckDefinitionDetails checkDef = this.getCheckDefs().get(checkLinks.getRelated());

            if (checkDef instanceof CompoundCheckDefinition) { // Let's not do checks of checks of checks, otherwise we might get into an infinite loop
                throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, chekLinkId));
            }

            Boolean eval;

            try {
                ParameterValue parValue = parameterManager.getParameterValue(checkLinks.getSource().getKey().getInstId());
                eval = this.performCheckEvaluation(chekLinkId, parValue);
            } catch (MALInteractionException ex) {
                eval = this.performCheckEvaluation(chekLinkId, null);
            }

            if (!eval) { // Is the evaluation in violation?
                violationCounter++;
            }
        }

        if (compoundCheckDefinition.getMinimumChecksInViolation().getValue() == 0L) {
            if (violationCounter == compoundCheckDefinition.getCheckLinkIds().size()) {
                return true;
            }
        }

        if (violationCounter > compoundCheckDefinition.getMinimumChecksInViolation().getValue()) {
            return true;
        }

        return false;
    }

}
