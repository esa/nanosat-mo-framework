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
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperMisc;
import esa.mo.mc.impl.provider.check.CheckLinkEvaluationManager;
import esa.mo.mc.impl.provider.check.CheckLinkEvaluation;
import esa.mo.mc.impl.provider.check.EvaluationResult;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mc.check.CheckHelper;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetailsList;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetailsList;
import org.ccsds.moims.mo.mc.check.structures.CheckResult;
import org.ccsds.moims.mo.mc.check.structures.CheckResultList;
import org.ccsds.moims.mo.mc.check.structures.CheckResultSummary;
import org.ccsds.moims.mo.mc.check.structures.CheckResultSummaryList;
import org.ccsds.moims.mo.mc.check.structures.CheckState;
import org.ccsds.moims.mo.mc.check.structures.CheckStateList;
import org.ccsds.moims.mo.mc.check.structures.CompoundCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ConstantCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.DeltaCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.LimitCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceCheckDefinition;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.structures.ObjectInstancePair;

/**
 * The definitionsManger is used to manage the check identity and the actual
 * check definition. To manage the CheckDefinition or the CheckLink Object this
 * CheckManager-class is used.
 *
 * @author Cesar Coelho, Nicolas Vorwerg
 *
 */
public final class CheckManager extends CheckLinksManager {

    private Long uniqueObjIdIdentity;
    private Long uniqueObjIdActDef;
    private Long uniqueObjIdLink;
    private Long uniqueObjIdLinkDef;

    //private final HashMap<Long, CheckResult> lastCheckResult; // list of latest checkResults by its checkLink-ids
    private final ParameterManager parameterManager;
    private boolean checkServiceEnabled;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final CheckLinkEvaluationManager checkLinkEvalManager;

    public CheckManager(COMServicesProvider comServices, ParameterManager parameterManager) {
        super(comServices);
        this.parameterManager = parameterManager;
        this.checkLinkEvalManager = new CheckLinkEvaluationManager(parameterManager);
        this.checkServiceEnabled = true;
        if (super.getArchiveService() == null) {  // No Archive?
            // The zeroth value will not be used (reserved for the wildcard)
            this.uniqueObjIdIdentity = 0L;
            this.uniqueObjIdActDef = 0L;
            this.uniqueObjIdLink = 0L;
            this.uniqueObjIdLinkDef = 0L;
        } else {

        }

    }

    public HashMap<Long, CheckLinkEvaluation> getCheckLinkEvaluations() {
        return checkLinkEvalManager.getCheckLinkEvaluations();
    }

    public CheckDefinitionDetails getActualCheckDefinition(Long identityId) {
        return (CheckDefinitionDetails) super.getDefinition(identityId);
    }

    /**
     * returns the current checkSummaries (inclusive the lastCheckResult) of the
     * checks that are linked to the given checkIdentity
     *
     * @param identityId the id of the check-identity the check is linked with.
     * @return
     */
    protected CheckResultSummaryList getCheckSummaries(Long identityId) {
        CheckResultSummaryList checkSummaries = new CheckResultSummaryList();
        final LongList checkLinkIds = findLinksPointingToCheck(identityId);
        for (Long checkLinkId : checkLinkIds) {
            CheckLinkEvaluation checkLinkEvaluation = checkLinkEvalManager.get(checkLinkId);
            // if compound check no parameterId is available, leave the data as null then
            final ObjectId paramObjId = super.getCheckLinkLinks(checkLinkId).getSource();
            ObjectKey paramKey = null;
            if (paramObjId != null) {
                paramKey = paramObjId.getKey();
            }
            checkSummaries.add(new CheckResultSummary(checkLinkId,
                    super.getCheckLinkDetails(super.getCheckLinkDefId(checkLinkId)).getCheckEnabled(),
                    paramKey, checkLinkEvaluation.getLastCheckResultTime(), checkLinkEvaluation.getLastCheckResult()));
        }
        return checkSummaries;
    }

    /**
     * gets the latest CheckResult for a given CheckLink
     *
     * @param checkLinkId the id of the checkLink the lastCheckResult should be
     * returned
     * @return the latest CheckResult
     */
    protected CheckResult getCheckResultObject(Long checkLinkId) {
        return checkLinkEvalManager.get(checkLinkId).getLastCheckResult();
    }

    protected CheckResultSummaryList getAllCheckSummaries() {
        CheckResultSummaryList checkSummeries = new CheckResultSummaryList();
        final Set<Long> checkLinkids = checkLinkEvalManager.getAllCheckLinkEvaluationIds();
        for (Long checkLinkId : checkLinkids) {
            final CheckLinkEvaluation checkLinkEval = checkLinkEvalManager.get(checkLinkId);
            // if compound check no parameterId is available, leave the data as null then
            final ObjectId paramObjId = super.getCheckLinkLinks(checkLinkId).getSource();
            ObjectKey paramKey = null;
            if (paramObjId != null) {
                paramKey = paramObjId.getKey();
            }
            checkSummeries.add(new CheckResultSummary(checkLinkId,
                    super.getCheckLinkDetails(super.getCheckLinkDefId(checkLinkId)).getCheckEnabled(),
                    paramKey, checkLinkEval.getLastCheckResultTime(), checkLinkEval.getLastCheckResult()));
        }
        return checkSummeries;
    }

    protected ParameterDefinitionDetails getParameterDefinition(Long input) {
        return this.parameterManager.getParameterDefinition(input);
    }

    protected void setCheckServiceEnabled(boolean flag) {
        this.checkServiceEnabled = flag;
    }

    protected boolean getCheckServiceEnabled() {
        return checkServiceEnabled;
    }

    /**
     * Returns all checkLinks that point to the given check. The given check
     * must be the same as the related link of the CheckLink, to add it to the
     * returned list of checkLinks.
     *
     * @param identityId the given identity id of the check
     * @return checkLinks that point to the given check
     */
    protected LongList findLinksPointingToCheck(Long identityId) {
        LongList output = new LongList();
        for (Long checkLinkId : listAllCheckLinkIds()) {
            if (getCheckLinkLinks(checkLinkId).getRelated().equals(identityId)) {
                output.add(checkLinkId);
            }
        }
        return output;
    }

    protected void setCheckEnabled(Long checkLinkId, Boolean bool, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        CheckLinkDetails checkLinkDetails = this.getCheckLinkDetails(super.getCheckLinkDefId(checkLinkId));

        if (checkLinkDetails == null) {
            return;
        }

        if (checkLinkDetails.getCheckEnabled().booleanValue() == bool) { // Is it set with the requested value already?
            return; // the value was not changed
        }

        checkLinkDetails.setCheckEnabled(bool);
        //create new object
        Long newCheckLinkDefId = this.updateCheckLinkObject(checkLinkId, checkLinkDetails, connectionDetails);
        //update internal lists
        super.updateCheckLink(checkLinkId, newCheckLinkDefId, checkLinkDetails);
    }

    protected void setCheckEnabledAll(Boolean bool, SingleConnectionDetails connectionDetails) {
        LongList objIds = new LongList();
        objIds.addAll(listAllCheckLinkIds());

        for (Long objIdLink : objIds) {
            setCheckEnabled(objIdLink, bool, connectionDetails);
        }
    }

    public ObjectInstancePair add(String name, CheckDefinitionDetails checkDef,
            ObjectId incomeSource, SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5

        ObjectInstancePair newIdPair;

        if (super.getArchiveService() == null) {
            // This lines have to go before any writing (because it's initialized as zero and that's the wildcard)
            uniqueObjIdIdentity++;
            uniqueObjIdActDef++;
            newIdPair = new ObjectInstancePair(uniqueObjIdIdentity, uniqueObjIdActDef);
//            this.save();
        } else {
            try {
                //requirement: 3.5.16.2.j: if a ParameterName ever existed before, use the old ParameterIdentity-Object by retrieving it from the archive
                //check if the name existed before and retrieve id if found
                Long identityId = retrieveIdentityIdByNameFromArchive(ConfigurationProviderSingleton.getDomain(),
                        new Identifier(name), CheckHelper.CHECKIDENTITY_OBJECT_TYPE);
                //in case the ParameterName never existed before, create a new identity
                if (identityId == null) {
                    //  requirement: 3.5.16.2.k
                    //Store Identity Object
                    IdentifierList names = new IdentifierList();
                    names.add(new Identifier(name));
                    LongList identityIds = super.getArchiveService().store( //requirement: 3.5.7.a
                            true,
                            CheckHelper.CHECKIDENTITY_OBJECT_TYPE, //requirement: 3.5.4.a
                            ConfigurationProviderSingleton.getDomain(),
                            HelperArchive.generateArchiveDetailsList(null, incomeSource, connectionDetails),
                            names, //requirement 3.5.4.b
                            null);
                    identityId = identityIds.get(0);
                }
                //requirement: 3.5.4.c
                // Store the actual Definition
                CheckDefinitionDetailsList defs = (CheckDefinitionDetailsList) HelperMisc.element2elementList(checkDef);
                defs.add(checkDef);
                ObjectType objType = CheckManager.generateCheckObjectType(checkDef); //requirement 3.5.4.c
                LongList actDefIds = super.getArchiveService().store( //requirement: 3.5.7.b
                        true,
                        objType,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(identityId, null, connectionDetails), //requirement 3.5.4.d
                        defs,
                        null);
                newIdPair = new ObjectInstancePair(identityId, actDefIds.get(0));

            } catch (MALInteractionException ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            } catch (Exception ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
                return null;
            }
        }
        //save ids and objects internally
//        super.addIdentityDefinition(newIdPair.getObjIdentityInstanceId(), new Identifier(name), newIdPair.getObjDefInstanceId(), checkDef);
        this.addIdentityDefinition(new Identifier(name), newIdPair, checkDef);

        //return object
        return newIdPair;
    }

    public Long update(final Long identityId, final CheckDefinitionDetails checkDef,
            SingleConnectionDetails connectionDetails) { // requirement: 3.3.2.5
        // requirement: 3.5.16.2.g
        Long actDefId = 0L;

        if (super.getArchiveService() == null) {
            actDefId = ++uniqueObjIdActDef;
        } else {  // store new objects in the COM Archive
            try {
                // Store the actual Definition
                CheckDefinitionDetailsList defs = (CheckDefinitionDetailsList) HelperMisc.element2elementList(checkDef);
                defs.add(checkDef);
                ObjectType objType = CheckManager.generateCheckObjectType(checkDef); //requirement 3.5.4.c
                LongList actDefIds = super.getArchiveService().store(
                        true,
                        objType,
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(identityId, null, connectionDetails), //requirement 3.5.4.d
                        defs,
                        null);
                actDefId = actDefIds.get(0);
            } catch (MALInteractionException ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //update internal list of check-definitions
        super.updateDef(identityId, actDefId, checkDef);

        //create returned object
        return actDefId;
    }

    public void delete(Long identityId) {
        super.deleteIdentity(identityId);
    }

    private Long updateCheckLinkObject(Long checkLinkId, CheckLinkDetails checkLinkDetails, SingleConnectionDetails connectionDetails) {
        final Long oldCheckLinkDefId = this.getCheckLinkDefId(checkLinkId);
        super.updateCheckLinkDetails(oldCheckLinkDefId, checkLinkDetails);
        Long newCheckLinkDefId = 0L;

        if (super.getArchiveService() == null) {  // It should also update in the COM Archive
            newCheckLinkDefId = ++uniqueObjIdLinkDef;
        }
        try {
            //Store the new CheckLinkDefinition-Object
            CheckLinkDetailsList linkDetails = new CheckLinkDetailsList();
            linkDetails.add(checkLinkDetails);
            LongList checkLinkDefIds = super.getArchiveService().store( //requirement: 3.5.7.d
                    true,
                    CheckHelper.CHECKLINKDEFINITION_OBJECT_TYPE, //requirement: 3.5.4.h
                    ConfigurationProviderSingleton.getDomain(),
                    HelperArchive.generateArchiveDetailsList(checkLinkId, null, connectionDetails), //requirement: 3.5.18.2.b, 3.5.4.i
                    linkDetails, //requirement: 3.5.18.2.a
                    null);
            newCheckLinkDefId = checkLinkDefIds.get(0);

        } catch (MALException | MALInteractionException ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return newCheckLinkDefId;
    }

    protected ObjectInstancePair addCheckLink(CheckLinkDetails linkDetail,
            ObjectDetails linkRef, SingleConnectionDetails connectionDetails) {
        //id of the new checklink
        Long checkLinkId = 0L;
        Long checkLinkDefId = 0L;
        if (super.getArchiveService() == null) {
            checkLinkId = ++uniqueObjIdLink;
            checkLinkDefId = ++uniqueObjIdLinkDef;
        } else {
            try {

                // Store the CheckLink-Object
                LongList checkLinkIds = super.getArchiveService().store( //requirement: 3.5.7.c
                        true,
                        CheckHelper.CHECKLINK_OBJECT_TYPE,//requirement: 3.5.4.e
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(linkRef.getRelated(),
                                linkRef.getSource(), connectionDetails), //requirement: 3.5.18.2.b, 3.5.4.f, 3.5.4.g
                        null, //requirement: 3.5.18.2.a
                        null);
                checkLinkId = checkLinkIds.get(0);

                //Store the CheckLinkDefinition-Object
                CheckLinkDetailsList linkDetails = new CheckLinkDetailsList();
                linkDetails.add(linkDetail);
                LongList checkLinkDefIds = super.getArchiveService().store( //requirement: 3.5.7.d
                        true,
                        CheckHelper.CHECKLINKDEFINITION_OBJECT_TYPE,//requirement: 3.5.4.h
                        ConfigurationProviderSingleton.getDomain(),
                        HelperArchive.generateArchiveDetailsList(checkLinkId, null, connectionDetails), //requirement: 3.5.18.2.b, 3.5.4.i
                        linkDetails, //requirement: 3.5.18.2.a
                        null);
                checkLinkDefId = checkLinkDefIds.get(0);

            } catch (MALException | MALInteractionException ex) {
                Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //set CheckLink internally 
        super.addCheckLink(checkLinkId, linkRef, checkLinkDefId, linkDetail);
        //init the evaluation object of the new checkLink
        CheckLinkEvaluation initCheckLinkEval = new CheckLinkEvaluation(getUnevaluatedCheckResult());
        checkLinkEvalManager.addCheckLinkEvaluation(checkLinkId, initCheckLinkEval);

        return new ObjectInstancePair(checkLinkId, checkLinkDefId);
    }

    /**
     * //requirement: 3.5.3.q Checkstates are set to UNCHECKED and the chacked
     * value to null.
     *
     * @return
     */
    private CheckResult getUnevaluatedCheckResult() {

        return new CheckResult(CheckState.UNCHECKED, CheckState.UNCHECKED, null, null);
    }

    /**
     * removes the checklink from the evaluation-list and from the internal list
     * of checkLinks
     *
     * @param checkLinkId
     */
    @Override
    protected void deleteCheckLink(Long checkLinkId) {
        checkLinkEvalManager.remove(checkLinkId);
        super.deleteCheckLink(checkLinkId);
    }

    protected Boolean reconfigureDefinitions(LongList identityIds, IdentifierList names,
            LongList defIds, LongList actDefIds, ArrayList<CheckDefinitionDetails> defs) {
//        if (identityIds == null || defIds == null) {
//            return false;
//        }
//        if (identityIds.size() != defIds.size()) {
//            return false;
//        }
//        super.reconfigureDefinitions(identityIds, names, actDefIds, (ElementList) defs);
//
//        super.deleteAllCheckIdentities();
//        for (int i = 0; i < identityIds.size(); i++) {
//            super.addCheckIdentityDefPair(identityIds.get(i), defIds.get(i));
//        }
//        return true;
        return false;

    }

    /**
     * TODO: contains the expression definition or identity-id? linked too issue
     * number 132, number 179 returns the definition id of a parameter by its
     * identity-id
     *
     * @param parameterId the id of the identity of the parameter
     * @return the id of the defintion
     */
    protected Long getParamDefId(Long parameterId) {
        return parameterManager.getDefinitionId(parameterId);
    }

//    protected DurationList getProvidedIntervals() {
//        return parameterManager.getProvidedIntervals();
//    }
    /**
     * Generates the Check Object Type.
     *
     * @param checkDef The check definition details.
     * @return The Object Type.
     */
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

    /**
     * Gets the ActualCheckDefintion that is used by the given CheckLink
     *
     * @param checkLinkId id of the checkLink you need the ActCheckDefintion
     * from
     * @return the actualCheckDefintion the checkLink references to. Null if the
     * checkDefintion could not be obtained.
     */
    public CheckDefinitionDetails getActualCheckDefinitionFromCheckLinks(Long checkLinkId) {
        //requirement: 3.5.17.2.f
        //get the details from the internal list
        final ObjectDetails checkLinkLinks = super.getCheckLinkLinks(checkLinkId);
        if (checkLinkLinks != null) {
            final Long identityId = checkLinkLinks.getRelated();
            return this.getActualCheckDefinition(identityId);
        }
        //for continuing using the checklink after the Definition had been removed, the COM-Archive must be used instead of the internal lists
        //TODO: is this still necessary? after we always get reference-errors if we still reference the Check?
//        if (getArchiveService() != null) {
//            //get the checkLinks-links referencing definitionId
//            final ArchivePersistenceObject checkLinkArchiveObj = HelperArchive.getArchiveCOMObject(getArchiveService(), MCServicesHelper.getCheckLinkObjectType(), this.configuration.getDomain(), checkLinkId);
//            final Long defId = checkLinkArchiveObj.getArchiveDetails().getDetails().getRelated();
//            //get the ChechDefintions actualDefintionsId
//            final ArchivePersistenceObject defIdArchiveObj = HelperArchive.getArchiveCOMObject(getArchiveService(), MCServicesHelper.getCheckDefinitionObjectType(), this.configuration.getDomain(), defId);
//            final Long actDefId = defIdArchiveObj.getArchiveDetails().getDetails().getSource().getKey().getInstId();
//            //get the actDefintions-details
//            final CheckDefinitionDetails actCheckDefinition = (CheckDefinitionDetails) HelperArchive.getObjectBodyFromArchive(getArchiveService(), MCServicesHelper.getCheckDefinitionObjectType(), this.configuration.getDomain(), actDefId);
//            return actCheckDefinition;
//        }
        //it was deleted and there is no archive, just return null
        return null;
    }

    /**
     * saves the CheckResult of the check
     *
     * @param checkId identityId of the compoundCheck
     * @param checkResult the CheckResult-Object that should be stored.
     */
    private void saveCheckResultObject(Long checkLinkId, CheckResult checkResult) {
        //save the new lastCheckResult as the current one
        final CheckLinkEvaluation checkLinkEvaluation = checkLinkEvalManager.get(checkLinkId);
        checkLinkEvaluation.setLastCheckResultTime(new Time(System.currentTimeMillis()));
        //get the current parameter-definition-id if available and set the object
        final ObjectId sourceParam = super.getCheckLinkLinks(checkLinkId).getSource();
        checkResult.setParamDefInstId(sourceParam == null ? null : parameterManager.getDefinitionId(sourceParam.getKey().getInstId()));

        checkLinkEvaluation.setLastCheckResult(checkResult);
    }

    private CheckResult createNewCheckResultObject(CheckResult prevResult, EvaluationResult evalResult) {
        //create and fill the CheckResult-Object
        CheckResult checkResult = new CheckResult();
        //set "previous" fields
        checkResult = evaluatePreviousCheckResultFields(prevResult, checkResult);
        //set "current" fields and value
        checkResult.setCheckedValue(evalResult.getCheckedValue());
        checkResult.setCurrentCheckState(evalResult.getEvaluationState());
//        checkResult.setCurrentEvaluationResult(evalResult.getEvaluationResult());

        return checkResult;
    }

    /**
     * applies the given filter to the given checkSummary-List. If a
     * checkSummary-entry matches an entry in the filter, this checksummary will
     * be added to the returned list
     *
     * @param checkSummaries the checkSummaries to be filtered
     * @param checksToFilterFor the checkLink must reference to one of these
     * checks in order to return its result
     * @param paramsToFilterFor the checkLink must reference to one of these
     * parameters in order to return its result
     * @param statesToFilterFor the checkLink result must be of one of these
     * states in order to return the result
     * @return the checkSummaries that matched the filter.
     */
    protected CheckResultSummaryList applyFilter(List<CheckResultSummary> checkSummaries,
            LongList checksToFilterFor, LongList paramsToFilterFor, CheckStateList statesToFilterFor) {

        CheckResultSummaryList sendCheckSummaries = new CheckResultSummaryList();
        for (CheckResultSummary checkSummary : checkSummaries) {
            //requirement: 3.5.8.2.a, 3.5.8.2.b
            final Long resultsCheckId = getCheckLinkLinks(checkSummary.getLinkId()).getRelated();
            final Long resultsParamId = checkSummary.getParameterId() == null ? null : checkSummary.getParameterId().getInstId();
            final CheckState resultsCheckState = checkSummary.getResult().getCurrentCheckState();
            //requirement: 3.5.8.2.o: the filters must be AND´d in order to return the result
            //requirement: 3.5.8.2.q: wildcard values (empty statesToFilterFor list means match all)
            if (statesToFilterFor.isEmpty() || statesToFilterFor.contains(resultsCheckState)) {
                if (checksToFilterFor.contains(resultsCheckId) && (resultsParamId == null || paramsToFilterFor.contains(resultsParamId))) {
                    //requirement: 3.5.8.2.c, 3.5.8.2.e
                    sendCheckSummaries.add(checkSummary);
                }
            }
        }
        //requirement: 3.5.8.2.f
        return sendCheckSummaries;
    }

    private CheckResult evaluatePreviousCheckResultFields(CheckResult prevResult,
            CheckResult newCheckResult) {
        //evaluate the previous fields
        if (prevResult != null) {
            final CheckState prevCheckState = prevResult.getCurrentCheckState();
            newCheckResult.setPreviousCheckState(prevCheckState);
            //When the previous state of the check is NOT_OK, this shall hold the previous check evaluation result. NULL if previous state is not NOT_OK.
//            if (prevCheckState == CheckState.NOT_OK) {
//                newCheckResult.setPreviousEvaluationResult(prevResult.getCurrentEvaluationResult());
//            }// else PreviousEvaluationResult = null;
        } else { //UNCHECKED for the first transition of a check. 
            newCheckResult.setPreviousCheckState(CheckState.UNCHECKED);
        }
        return newCheckResult;
    }

    protected Long publishCheckTransitionEvent(final CheckResult checkResult,
            final Long related, final ObjectId source) {
        // Store the event in the Archive
        Long eventObjId = getEventService().generateAndStoreEvent( //requirement: 3.5.7.e
                CheckHelper.CHECKTRANSITION_OBJECT_TYPE,
                ConfigurationProviderSingleton.getDomain(),
                checkResult,
                related,
                source,
                null,
                ConfigurationProviderSingleton.getNetwork());

        CheckResultList checkResults = new CheckResultList(1);
        checkResults.add(checkResult);

        try {
            // Publish the Event
            getEventService().publishEvent(
                    connection.getPrimaryConnectionDetails().getProviderURI(),
                    eventObjId,
                    CheckHelper.CHECKTRANSITION_OBJECT_TYPE,
                    related,
                    source,
                    checkResults);
        } catch (IOException ex) {
            Logger.getLogger(CheckManager.class.getName()).log(Level.SEVERE, null, ex);
        }

        return eventObjId;
    }

    /**
     * Executes Delta, Limit, Reference and Constant-Checks. For Compound-Checks
     * use executeCompoundCheck-method. Checks the given value and creates,
     * saves and publishes the CheckResult if necessary.
     *
     * @param checkLinkId id of the checkLink
     * @param newParamValue ParameterValue to be checked
     * @param triggered true, if check was triggered by the
     * triggerCheck-Operation.
     * @param maxReportingIntervalExpired true if the executeCheck was initiated
     * because the maxReportingInterval is expired
     * @param source source that caused the check to be executed. most likely
     * the ParameterValueInstance-Object
     */
    public void executeCheck(Long checkLinkId, final ParameterValue newParamValue,
            boolean triggered, boolean maxReportingIntervalExpired, ObjectId source) {
        Logger.getLogger(CheckManager.class.getName()).log(Level.INFO,
                "Executing check for CheckLink with Id {0} that references to: "
                + "Check-Identity with id: {1} and Parameter-Identity with id: {2}. "
                + "The ParamValue is: {3}",
                new Object[]{checkLinkId, getCheckLinkLinks(checkLinkId).getRelated(),
                    getCheckLinkLinks(checkLinkId).getSource() == null ? "null"
                    : getCheckLinkLinks(checkLinkId).getSource().getKey().getInstId(), newParamValue});
        if (!super.getCheckLinkDetails(super.getCheckLinkDefId(checkLinkId)).getCheckEnabled()) {
            return;
        }
        //evalutate new result
        final EvaluationResult evalResult = checkLinkEvalManager.evaluateCheckResult(checkLinkId,
                newParamValue, triggered, getActualCheckDefinitionFromCheckLinks(checkLinkId),
                super.getCheckLinkDetails(super.getCheckLinkDefId(checkLinkId)),
                getCheckLinkLinks(checkLinkId), checkServiceEnabled);
        //publish if necessary
        if (!maxReportingIntervalExpired) {
            //3.5.3.p
            if (triggered) {//the check must violate to create new result and event.
                if (evalResult.getEvaluationState() != CheckState.NOT_OK) {
                    return;
                }
            } else {
                //if the state is NULL, no new CheckResult-object should be created and published
                if (evalResult.getEvaluationState() == null) {
                    return;
                }
            }
        }
        //create new CheckResult-Object, but its not saved yet
        CheckResult newCheckResult = createNewCheckResultObject(checkLinkEvalManager.get(checkLinkId).getLastCheckResult(), evalResult);
        if (!triggered) {
            //requriement: requirement: 3.5.4.j, 3.5.5.a, 3.5.5.b 3.5.3.hh - dont publish if it didnt change (only if the maximum reporting interval expired (3.5.3.ff)
            if (!maxReportingIntervalExpired && newCheckResult.getPreviousCheckState() == newCheckResult.getCurrentCheckState()) {
                return;
            }
            //now it can be saved
            saveCheckResultObject(checkLinkId, newCheckResult);
        }
        //requriement: 3.5.4.l
        //requirement: 3.5.4.k -> Related = checkLinkDefId
        publishCheckTransitionEvent(newCheckResult, super.getCheckLinkDefId(checkLinkId), source);
    }

}
