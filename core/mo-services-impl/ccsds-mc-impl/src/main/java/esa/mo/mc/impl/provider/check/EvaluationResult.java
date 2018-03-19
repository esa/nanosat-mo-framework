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
package esa.mo.mc.impl.provider.check;

import java.util.List;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mc.check.structures.CheckState;

/**
 *
 * @author Vorwerg
 */
public class EvaluationResult {

    private Time evaluationTime;
    private CheckState evaluationState;
    private Boolean evaluationResult;
    private Attribute checkedValue;

    public Time getEvaluationTime() {
        return evaluationTime;
    }

    public void setEvaluationTime(Time evaluationTime) {
        this.evaluationTime = evaluationTime;
    }

    public CheckState getEvaluationState() {
        return evaluationState;
    }

    public void setEvaluationState(CheckState evaluationState) {
        this.evaluationState = evaluationState;
    }

    public Boolean getEvaluationResult() {
        return evaluationResult;
    }

    public void setEvaluationResult(Boolean evaluationResult) {
        this.evaluationResult = evaluationResult;
    }

    public Attribute getCheckedValue() {
        return checkedValue;
    }

    public void setCheckedValue(Attribute checkedValue) {
        this.checkedValue = checkedValue;
    }


}
