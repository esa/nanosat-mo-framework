/*
 * M&C Services for CCSDS Mission Operations Framework
 * Copyright (C) 2016 Deutsches Zentrum für Luft- und Raumfahrt e.V. (DLR).
 * 
 *  This library is free software; you can redistribute it and/or
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
package esa.mo.mc.impl.provider.check;

import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperAttributes;
import java.util.HashMap;
import java.util.List;
import org.ccsds.moims.mo.com.archive.structures.ExpressionOperator;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mc.check.structures.CheckState;
import org.ccsds.moims.mo.mc.check.structures.CompoundCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ConstantCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.DeltaCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.LimitCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceValue;
import org.ccsds.moims.mo.mc.structures.AttributeValue;

/**
 *
 * @author Vorwerg
 */
public class CheckEvaluation {

    /**
     *
     * @param checkDef
     * @param value
     * @param evaluationResult
     * @return if the evaluation was successfull then the
     * evaluationResult.result is null otherwise false
     */
    public static EvaluationResult evaluateConstantCheck(ConstantCheckDefinition checkDef, Attribute value, EvaluationResult evaluationResult) {
        //pass one of the evaluations req. 3.5.2.e.a 
        //requirement: 3.5.2.e.a
        Boolean result = false;
        for (AttributeValue possibleValue : checkDef.getValues()) {
            if (HelperCOM.evaluateExpression(possibleValue.getValue(), checkDef.getOperator(), value)) {
                //requirement: 3.5.3.cc: the parameter value passed the check against the evaluation: return successful!
                result = null;
                break;
            }
        }
        evaluationResult.setEvaluationResult(result);
        evaluationResult.setCheckedValue(value);
        return evaluationResult;
    }

    /**
     *
     * @param checkDef
     * @param value
     * @param evaluationResult
     * @return if the evaluation was successfull then the
     * evaluationResult.result is null, if upper limit is exeeced: true, if
     * lower limit exceeded: false; OR if violateInRange = true then if it
     * violates, it returns true
     */
    public static EvaluationResult evaluateLimitCheck(LimitCheckDefinition checkDef, Attribute value, EvaluationResult evaluationResult) {
        //NULL = successful, true is violating upper limit, false violating lower limit
        Boolean result = null;

        if (checkDef.getUpperLimit() != null
                && !HelperCOM.evaluateExpression(value, ExpressionOperator.LESS_OR_EQUAL, checkDef.getUpperLimit())) {
            result = true;
        }
        if (checkDef.getLowerLimit() != null
                && !HelperCOM.evaluateExpression(value, ExpressionOperator.GREATER_OR_EQUAL, checkDef.getLowerLimit())) {
            result = false;
        }
        //requirement: 3.5.3.dd 
        if (checkDef.getViolateInRange()) {
            //in range? -> result is NOT_OK
            result = (result == null ? true : null);
        }
        evaluationResult.setCheckedValue(value);
        evaluationResult.setEvaluationResult(result);
        return evaluationResult;
    }

    /**
     *
     * @param referenceCheckDefinition
     * @param value
     * @param currCheckLinkEvaluation
     * @param newEvaluationResult
     * @return if the evaluation was successful then the evaluationResult.result
     * is null otherwise: false;
     * @throws MALInteractionException
     */
    public static EvaluationResult evaluateReferenceCheck(ReferenceCheckDefinition referenceCheckDefinition, Attribute value, final CheckLinkEvaluation currCheckLinkEvaluation, EvaluationResult newEvaluationResult) throws MALInteractionException {
        //requirement: 3.5.3.x, y
        ReferenceValue reference = referenceCheckDefinition.getCheckReference();
        HashMap<Long, Attribute> sampleTimes = reference.getDeltaTime().getValue() == 0 ? null : currCheckLinkEvaluation.getSampleTimes();

//        manageBeforeRefCheck(referenceValue, checkLinkEvaluation, sampleTimes, useConverted);
        //evaluate param + refValue        
        Boolean eval = HelperCOM.evaluateExpression(value, referenceCheckDefinition.getOperator(), currCheckLinkEvaluation.getRefParamValue());
        if (reference.getParameterId() == null) {
            manageAfterRefCheck(reference, currCheckLinkEvaluation, sampleTimes, value);
        }

        newEvaluationResult.setCheckedValue(value);
        newEvaluationResult.setEvaluationResult(eval == true ? null : false);
        return newEvaluationResult;
    }

    /**
     *
     * @param deltaCheckDefinition
     * @param value
     * @param currCheckLinkEvaluation
     * @param newEvaluationResult
     * @return if the evaluation was successfull then the
     * evaluationResult.result is null, if upper limit is exeeced: true, if
     * lower limit exceeded: false; OR if violateInRange = true then if it
     * violates, it returns true
     * @throws MALInteractionException
     */
    public static EvaluationResult evaluateDeltaCheck(DeltaCheckDefinition deltaCheckDefinition, Attribute value, final CheckLinkEvaluation currCheckLinkEvaluation, EvaluationResult newEvaluationResult) throws MALInteractionException {
        //3.5.3.x, y
        ReferenceValue referenceValue = deltaCheckDefinition.getCheckReference();
        HashMap<Long, Attribute> sampleTimes = referenceValue.getDeltaTime().getValue() == 0 ? null : currCheckLinkEvaluation.getSampleTimes();

//        manageBeforeRefCheck(referenceValue, checkLinkEvaluation, sampleTimes, useConverted);
        Attribute refValue = currCheckLinkEvaluation.getRefParamValue();

        //requirement: 3.5.3.z
        Double delta = HelperAttributes.attribute2double(value) - HelperAttributes.attribute2double(refValue);

        Attribute upperThreshold;
        Attribute lowerThreshold;
        if (deltaCheckDefinition.getValueDelta()) { // "If true then thresholds contain value deltas"
            upperThreshold = deltaCheckDefinition.getUpperThreshold();
            lowerThreshold = deltaCheckDefinition.getLowerThreshold();
        } else { // "If false, they contain percentage deltas."

            //requirement: 3.5.3.bb
            if (HelperAttributes.attribute2double(refValue) == 0) {
                upperThreshold = new Union(Float.NEGATIVE_INFINITY);
                lowerThreshold = new Union(Float.POSITIVE_INFINITY);
            } else {
                //requirement: 3.5.3.aa
                upperThreshold = new Union(HelperAttributes.attribute2double(deltaCheckDefinition.getUpperThreshold()) * HelperAttributes.attribute2double(refValue));
                lowerThreshold = new Union(HelperAttributes.attribute2double(deltaCheckDefinition.getLowerThreshold()) * HelperAttributes.attribute2double(refValue));
            }
        }

        //requirement: 3.5.3.cc
        Boolean result = null;
        if (deltaCheckDefinition.getUpperThreshold() != null
                && !HelperCOM.evaluateExpression(new Union(delta), ExpressionOperator.LESS_OR_EQUAL, upperThreshold)) {
            result = true;
        } else {
            if (deltaCheckDefinition.getLowerThreshold() != null
                    && !HelperCOM.evaluateExpression(new Union(delta), ExpressionOperator.GREATER_OR_EQUAL, lowerThreshold)) {
                result = false;
            }
        }
        //requirement: 3.5.3.dd 
        if (deltaCheckDefinition.getViolateInRange()) {
            result = (result == null ? true : null);
        }
        if (referenceValue.getParameterId() == null) {
            manageAfterRefCheck(referenceValue, currCheckLinkEvaluation, sampleTimes, value);
        }

        newEvaluationResult.setCheckedValue(value);
        newEvaluationResult.setEvaluationResult(result);
        return newEvaluationResult;
    }

    /**
     *
     * @param compoundCheckDefinition
     * @param currCheckLinkEvaluations
     * @return if the evaluation was successful then the evaluationResult.result
     * is null otherwise: false;
     */
    public static EvaluationResult evaluateCompoundCheck(CompoundCheckDefinition compoundCheckDefinition, final List<CheckLinkEvaluation> currCheckLinkEvaluations, EvaluationResult newEvaluationResult) {
        final UInteger minChecksViolate = compoundCheckDefinition.getMinimumChecksInViolation();
        int countChecksViolate = 0;
        //count the checks that are currently in violation
        for (CheckLinkEvaluation currCheckLinkEvaluation : currCheckLinkEvaluations) {
            final CheckState checkState = currCheckLinkEvaluation.getLastCheckResult().getCurrentCheckState();
            if (checkState == CheckState.NOT_OK) {
                countChecksViolate++;
            }
        }
        Boolean result;
        //wildcard check
        if (minChecksViolate.getValue() == 0) //requirement: 3.5.3.ee - true if any check is not currently in violation
        {
            result = countChecksViolate < currCheckLinkEvaluations.size();
        } else //requirement: 3.5.3.dd - true if less checks violate than the minimum limit
        {
            result = countChecksViolate < minChecksViolate.getValue();
        }
        newEvaluationResult.setEvaluationResult(result == true ? null : false);
        //fill the checkedValue-field with the number of violating checks
        newEvaluationResult.setCheckedValue(new Union(countChecksViolate));
        return newEvaluationResult;
    }

    /**
     * used for self referencing parameters only, the not self referenced ones
     * will be managed by the ParameterMonitoringManager
     *
     * @param referenceValue
     * @param checkLinkEvaluation
     * @param sampleTimes
     * @param value
     */
    private static void manageAfterRefCheck(ReferenceValue referenceValue, final CheckLinkEvaluation checkLinkEvaluation, HashMap<Long, Attribute> sampleTimes, Attribute value) {
        if (referenceValue.getDeltaTime().getValue() == 0) {
            checkLinkEvaluation.incRefValueCounter();
            //referencing itslf and refCounter is hit
            if (checkLinkEvaluation.getRefValueCounter() == referenceValue.getValidCount().getValue()) {//set current value as new refValue
                //set the value the parameters own value as the reference value
                checkLinkEvaluation.setRefParamValue(value);
            }
        } else {
            Long now = (new Time()).getValue();
            removeExpiredSamples(now, referenceValue, sampleTimes);

            //set how many samples were checked in the deltaTime from now into the past (+1 for the current one set later)
            checkLinkEvaluation.setRefValueCounter(sampleTimes.size() + 1);
            //referencing itslf and refCounter is hit
            if (referenceValue.getParameterId() == null && checkLinkEvaluation.getRefValueCounter() == referenceValue.getValidCount().getValue()) {//get the last element
                sampleTimes.clear();
                checkLinkEvaluation.setRefParamValue(value);
            }
            //add the param with the current timestamp in a hashmap
            sampleTimes.put(now, value);
        }
    }

    private static Long removeExpiredSamples(Long now, ReferenceValue referenceValue, final HashMap<Long, Attribute> sampleTimes) {
        //check how many samples checked(= how many param exist in the hashmap) in the duration (now - deltaTime)

        //samples sampled before the deltaLimit are not important anymore
        Long deltaLimit = now - Math.round(referenceValue.getDeltaTime().getValue());
        for (Long sampleTime : sampleTimes.keySet()) {
            if (sampleTime < deltaLimit) {
                sampleTimes.remove(sampleTime);
            }
        }
        return now;
    }

}
