/* 
 * M&C Services for CCSDS Mission Operations Framework
 * Copyright (C) 2021 Deutsches Zentrum fuer Luft- und Raumfahrt e.V. (DLR).
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
package esa.mo.mc.impl.provider.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mc.check.structures.CheckResult;

/**
 *
 * @author Vorwerg
 */
public class CheckLinkEvaluation {

    public CheckLinkEvaluation(CheckResult lastCheckResult) {
        this.evaluationResults = new ArrayList<>();
        this.lastCheckResult = lastCheckResult;
        this.lastCheckResultTime = new Time(System.currentTimeMillis());
    }

    private List<EvaluationResult> evaluationResults;
    private CheckResult lastCheckResult;
    private Time lastCheckResultTime;
    private int refValueCounter;
    private Attribute refParamValue;
    HashMap<Long, Attribute> sampleTimes = new HashMap<>();

    public Time getLastCheckResultTime() {
        return lastCheckResultTime;
    }

    public void setLastCheckResultTime(Time lastCheckResultTime) {
        this.lastCheckResultTime = lastCheckResultTime;
    }

    public CheckResult getLastCheckResult() {
        return lastCheckResult;
    }

    public void setLastCheckResult(CheckResult lastCheckResult) {
        this.lastCheckResult = lastCheckResult;
    }

    public List<EvaluationResult> getEvaluationResults() {
        return evaluationResults;
    }

    public void setEvaluationResults(List<EvaluationResult> evaluationResults) {
        this.evaluationResults = evaluationResults;
    }

    public void addEvaluationResult(EvaluationResult evaluationResult) {
        this.evaluationResults.add(evaluationResult);
    }

    public EvaluationResult getLastEvaluationResult() {
        return this.evaluationResults.get(evaluationResults.size() - 1);
    }

    public void setLastEvaluationResult(EvaluationResult evaluationResult) {
        this.evaluationResults.set(evaluationResults.size() - 1, evaluationResult);
    }

    public int getRefValueCounter() {
        return refValueCounter;
    }

    public void incRefValueCounter() {
        this.refValueCounter++;
    }

    public void setRefValueCounter(int refValueCounter) {
        this.refValueCounter = refValueCounter;
    }

    public Attribute getRefParamValue() {
        return refParamValue;
    }

    public void setRefParamValue(Attribute refParamValue) {
        this.refParamValue = refParamValue;
    }

    public HashMap<Long, Attribute> getSampleTimes() {
        return sampleTimes;
    }

    public void setSampleTimes(HashMap<Long, Attribute> sampleTimes) {
        this.sampleTimes = sampleTimes;
    }

}
