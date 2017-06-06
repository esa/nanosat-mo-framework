/* 
 * M&C Services for CCSDS Mission Operations Framework
 * Copyright (C) 2016 Deutsches Zentrum fuer Luft- und Raumfahrt e.V. (DLR).
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
/*
 * @author Vorwerg
 *
 */
package esa.mo.mc.impl.provider.check;

import esa.mo.mc.impl.provider.ParameterManager;
import java.util.HashMap;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckState;
import org.ccsds.moims.mo.mc.check.structures.CompoundCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ConstantCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.DeltaCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.LimitCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceCheckDefinition;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import esa.mo.mc.impl.provider.check.model.CheckLinkEvaluation;
import esa.mo.mc.impl.provider.check.model.EvaluationResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetails;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterDefinitionDetails;

public class CheckLinkEvaluationManager {

    private final HashMap<Long, CheckLinkEvaluation> checkLinkEvaluations = new HashMap<Long, CheckLinkEvaluation>(); //contains fields belonging the evaluations (CheckLinkEvaluation) of a checkLink (Long)
    private ParameterManager parameterManager;

    public CheckLinkEvaluationManager(ParameterManager parameterManager) {
        this.parameterManager = parameterManager;
    }

    public HashMap<Long, CheckLinkEvaluation> getCheckLinkEvaluations() {
        return checkLinkEvaluations;
    }

    public Set<Long> getAllCheckLinkEvaluationIds() {
        return checkLinkEvaluations.keySet();
    }

    public CheckLinkEvaluation get(Long checkLinkId) {
        return checkLinkEvaluations.get(checkLinkId);
    }

    public void addCheckLinkEvaluation(Long checkLinkId, CheckLinkEvaluation checkLinkEvaluation) {
        checkLinkEvaluations.put(checkLinkId, checkLinkEvaluation);
    }

    public void remove(Long checkLinkId) {
        checkLinkEvaluations.remove(checkLinkId);
    }

    /**
     * requirement: 3.5.3.o Checks if the Check is enabled, the CheckLinks
     * condition evaluates to true and the checked parameter is in a valid state
     *
     * @param checkLinkId
     * @param parValue
     * @param triggered if the check was iniitialized with the
     * TriggerCheck-operation. false if a check for the
     * successive-evaluation-results shall be done. true if only the current
     * evaluations result shall be checked.
     * @param checkDefDetails
     * @param details
     * @param checkLinkLink
     * @param checkServiceGloballyEnabled
     * @return the checkState of the check. OK if the expression evaluates to
     * true for the correct number of consecutive valid samples, NOT_OK if the
     * expression evaluates to false for the correct number of consecutive
     * invalid samples otherwise. Other possible States are DISABLED, UNCHECKED
     * or INVALID. NULL if no new CheckResult-Object has to be created because
     * the consecutive nominal/violation samples havent been reached yet
     */
    public EvaluationResult evaluateCheckResult(final Long checkLinkId, final ParameterValue parValue, boolean triggered, CheckDefinitionDetails checkDefDetails, CheckLinkDetails details, ObjectDetails checkLinkLink, boolean checkServiceGloballyEnabled) {
        EvaluationResult evaluationResult = new EvaluationResult();
        evaluationResult.setEvaluationTime(new Time(System.currentTimeMillis()));
//requirement: 3.5.3.o
//        CheckLinkDetails details = super.getCheckLinkDetails(checkLinkId);
//        ObjectDetails checkLinkLink = super.getCheckLinkLinks(checkLinkId);
        //requirement: 3.5.10.2.b no evaluation
        if (!checkServiceGloballyEnabled) { // Is the Check service globally disabled?
            evaluationResult.setEvaluationState(CheckState.DISABLED);
            return evaluationResult;
        }

        //requirement: 3.5.3.e (implicitly f)
        if (!details.getCheckEnabled()) { // Is the Check enabled?
            evaluationResult.setEvaluationState(CheckState.DISABLED);
            return evaluationResult;
        }

        if (details.getCondition() != null) {
            if (isUncheckedResult(details)) {
                evaluationResult.setEvaluationState(CheckState.UNCHECKED);
                return evaluationResult;
            }

        }
        Attribute value = null;
        if (!(checkDefDetails instanceof CompoundCheckDefinition)) {
            ParameterDefinitionDetails entityParDef = parameterManager.getParameterDefinition(checkLinkLink.getSource().getKey().getInstId());

            if (!isParameterValueValid(parValue, (entityParDef.getConversion() == null))) {
                evaluationResult.setEvaluationState(CheckState.INVALID);
                return evaluationResult;
            }
            final Boolean useConverted = details.getUseConverted();
            value = useConverted ? parValue.getConvertedValue() : parValue.getRawValue();
        }
        try {
            //Evaluate Check
            evaluationResult = this.evaluateCheckResult(checkLinkId, checkDefDetails, value, evaluationResult);

            return getCheckStateOK(triggered, evaluationResult, checkLinkId, checkDefDetails);

        } catch (MALInteractionException ex) {
            evaluationResult.setEvaluationState(CheckState.UNCHECKED);
            return evaluationResult; // The Evaluation could not be determined
        }
    }

    /**
     * 
     * @param triggered if triggered, the newly generated results wont be saved in the internal lists 
     * @param evaluationResult
     * @param checkLinkId
     * @param checkDefDetails
     * @return 
     */
    private EvaluationResult getCheckStateOK(boolean triggered, EvaluationResult evaluationResult, final Long checkLinkId, CheckDefinitionDetails checkDefDetails) {
        //requirement: 3.5.13.2.h, i: the triggerCheckOperation doesnt need a successiveCheck and wont be saved to any internal lists/variables
        if (triggered) {
            evaluationResult.setEvaluationState(evaluationResult.getEvaluationResult() == null ? CheckState.OK : CheckState.NOT_OK);
            return evaluationResult;
        }
        //add to the evaluation-results
        checkLinkEvaluations.get(checkLinkId).addEvaluationResult(evaluationResult);
        //requirement: 3.5.2.d.b, e.b, f.b, g.c
        //requirement: 3.5.3.t, 3.5.3.u, 3.5.3.v, 3.5.3.w
        //check if the samples have a successive result, set the state
        boolean calculateCheckResult = calculateCheckResult(checkLinkId, checkDefDetails);
        if (calculateCheckResult) {
            evaluationResult.setEvaluationState(evaluationResult.getEvaluationResult() == null ? CheckState.OK : CheckState.NOT_OK);
            checkLinkEvaluations.get(checkLinkId).setLastEvaluationResult(evaluationResult);
        }
        //evaluation state stays on null if no calcuation necessary
        return evaluationResult;
    }

    private boolean isUncheckedResult(CheckLinkDetails details) {
        // "UNCHECKED" iteration...
        // Fetch Value
        ParameterValue conditionParVal = null;
        final Long conParamIdentityId = details.getCondition().getParameterId().getInstId(); //parameterManager.getIdentity(details.getCondition().getParameterId().getInstId());
        try {
            conditionParVal = parameterManager.getParameterValue(conParamIdentityId);
        } catch (MALInteractionException ex) {
            Logger.getLogger(CheckLinkEvaluationManager.class.getName()).log(Level.SEVERE, "Conditional Parameter Value couldnt be retrieved", ex);
        }
        ParameterDefinitionDetails conditionParDef = parameterManager.getParameterDefinition(conParamIdentityId);
        if (!isParameterValueValid(conditionParVal, (conditionParDef.getConversion() == null))) {
            return true;
        }
        //TODO: contains the expression defintion or identity-id? -> issue #132, #179
        //until then, hack the identityId in the details
//        IdentifierList domain = details.getCondition().getParameterId().getDomain();
//        details.getCondition().setParameterId(new ObjectKey(domain, conParamIdentityId));
        //hack end
        Boolean conditionEvaluation = parameterManager.evaluateParameterExpression(details.getCondition());
        //hack start
//        details.getCondition().setParameterId(new ObjectKey(domain, parameterManager.getDefinitionId(conParamIdentityId)));
        //hack end
        if (!conditionEvaluation) {
            // Does the condition evaluate to TRUE?
            return true;
        }
        return false;
    }

    private boolean isParameterValueValid(ParameterValue pVal, boolean usingRaw) {
        // "Is VALID or using raw and INVALID_CONVERSION?"
        if (pVal.getValidityState().getValue() == 0) { // VALID
            return true;
        }

        if (usingRaw && pVal.getValidityState().getValue() == 3) {  // Using raw and INVALID_CONVERSION
            return true;
        }

        return false;

    }

    /**
     *
     * @param checkLinkId
     * @param actCheckDefinition
     * @param value
     * @param useConverted is needed for ReferenceCheck and DeltaCheck. says if
     * the ParameterValue should be compared to the converted ReferencedValue or
     * its RawValue
     * @return
     * @throws MALInteractionException
     */
    private EvaluationResult evaluateCheckResult(Long checkLinkId, CheckDefinitionDetails actCheckDefinition, Attribute value, EvaluationResult evaluationResult) throws MALInteractionException {
        if (actCheckDefinition instanceof ConstantCheckDefinition) {
            return CheckEvaluation.evaluateConstantCheck((ConstantCheckDefinition) actCheckDefinition, value, evaluationResult);
        }

        if (actCheckDefinition instanceof LimitCheckDefinition) {
            return CheckEvaluation.evaluateLimitCheck((LimitCheckDefinition) actCheckDefinition, value, evaluationResult);
        }

        if (actCheckDefinition instanceof ReferenceCheckDefinition) {
            return CheckEvaluation.evaluateReferenceCheck((ReferenceCheckDefinition) actCheckDefinition, value, checkLinkEvaluations.get(checkLinkId), evaluationResult);
        }

        if (actCheckDefinition instanceof DeltaCheckDefinition) {
            return CheckEvaluation.evaluateDeltaCheck((DeltaCheckDefinition) actCheckDefinition, value, checkLinkEvaluations.get(checkLinkId), evaluationResult);
        }
        if (actCheckDefinition instanceof CompoundCheckDefinition) {
            //get the last results of the referenced check links
            List<CheckLinkEvaluation> currCheckLinkEvaluations = new ArrayList<CheckLinkEvaluation>();
            final CompoundCheckDefinition compoundCheckDef = (CompoundCheckDefinition) actCheckDefinition;
            for (Long refCheckLinkId : compoundCheckDef.getCheckLinkIds()) {
                currCheckLinkEvaluations.add(this.get(refCheckLinkId));
            }
            return CheckEvaluation.evaluateCompoundCheck(compoundCheckDef, currCheckLinkEvaluations, evaluationResult);
        }
        throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, "There is a problem with the checkDefinition referenced by the checkLink"));
    }

    /**
     * The method checks the consecutive nominal or violation samples during the
     * last nominal/violation time and returns if a check result should be to be
     * created..
     *
     * @param checkLinkId the id of the checkLink you may create a CheckResult
     * for
     * @return TRUE if a new check result shall be calculated; FALSE otherwise;
     */
    private boolean calculateCheckResult(Long checkLinkId, CheckDefinitionDetails actCheckDef) {
        final CheckLinkEvaluation checkLinkEval = checkLinkEvaluations.get(checkLinkId);
//        final CheckDefinitionDetails actCheckDef = getActualCheckDefinitionFromCheckLinks(checkLinkId);
        final long now = System.currentTimeMillis();
        final long nominalTime = Math.round(actCheckDef.getNominalTime().getValue() * 1000);
        final long violationTime = Math.round(actCheckDef.getViolationTime().getValue() * 1000);
        int nominalCounter = 0;
        int violationCounter = 0;
        int maxNominalCount = 0;
        int maxViolationCount = 0;
        //check that there were a nominal-count amount of sucessive successful evaluations during the last nominal-time period (or violation count failed samples)
        final List<EvaluationResult> evaluationResults = checkLinkEval.getEvaluationResults();
        for (int i = 0; i < evaluationResults.size(); i++) {
            final long timeSinceEvaluation = now - evaluationResults.get(i).getEvaluationTime().getValue();
            boolean expired = true;
            //requirement: 3.5.3.t,u
            if (nominalTime == 0 || timeSinceEvaluation <= nominalTime) {
                //count successive passed sample
                expired = false;
                if (evaluationResults.get(i).getEvaluationResult() == null) {
                    maxViolationCount = maxViolationCount < violationCounter ? violationCounter : maxViolationCount;
                    violationCounter = 0;
                    nominalCounter++;
                    maxNominalCount = maxNominalCount < nominalCounter ? nominalCounter : maxNominalCount;
                }
            }
            //requirement: 3.5.3.v,w
            if (violationTime == 0 || timeSinceEvaluation <= violationTime) {
                //count successive failed samples
                expired = false;
                if (evaluationResults.get(i).getEvaluationResult() != null) {
                    maxNominalCount = maxNominalCount < nominalCounter ? nominalCounter : maxNominalCount;
                    nominalCounter = 0;
                    violationCounter++;
                    maxViolationCount = maxViolationCount < violationCounter ? violationCounter : maxViolationCount;
                }
            }
            if (expired) {
//                evaluationResults.remove(i);
//                //list is one element shorter -> decrement index
//                i--;
            }
        }

        //check if a new check result shall be calculated
        if (maxNominalCount >= actCheckDef.getNominalCount().getValue()
                || maxViolationCount >= actCheckDef.getViolationCount().getValue()) {
            return true;
        }
        return false;

    }

}
