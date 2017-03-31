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
package esa.mo.mc.impl.interfaces;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.DurationList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterRawValue;

/**
 * this interface holds methods used for the test of the parameter-service. it
 * must be implemented by an application that holds test-datas.
 *
 * @author Vorwerg
 */
public interface ParameterTestStatusListener extends ParameterStatusListener {

    /**
     * Pushes a new value of a parameter. This creates a parameterValue-Update
     * that will be published and can be monitored via the
     * monitorValue-Operation
     *
     * @param name the name of the parameter the value should be updated from
     * @param value the new parameter-value
     * @throws org.ccsds.moims.mo.mal.MALException
     * @throws org.ccsds.moims.mo.mal.MALInteractionException
     */
    public void pushParameterValue(Identifier name, ParameterRawValue value) throws MALException, MALInteractionException;

    /**
     * Sets the evaluation options for creating a custom validity state of a
     * parameters-value. Custom means, on the one hand, using custom mechanisms
     * for evaluating the validity state and on the other hand using custom
     * validity-state values. Via the getCustomValidityState-Operation these
     * options will be considered.
     *
     * @param customMechanisms True, if implementation specific mechanisms
     * should be used for evaluating the validity-state. Meaning the
     * standard-ones defined in the functional requirements will not be used
     * instead you own implemented ones.
     * @param customValue True, if deployment specific validity state values
     * should be used. Meaning the standard-ones defined in the functional
     * requirements will not be used instead you own specific ones. These values
     * are greater than 127
     * @throws org.ccsds.moims.mo.mal.MALException
     * @throws org.ccsds.moims.mo.mal.MALInteractionException
     */
    public void setValidityStateOptions(Boolean customMechanisms, Boolean customValue) throws MALException, MALInteractionException;

    /**
     * sets a parameter with the given name to a readonly-parameter 
     * @param name the name of the parameter
     * @param value true if it should be set as a read-only parameter, false if it should be deleted from the read-only-list
     * @throws org.ccsds.moims.mo.mal.MALException
     * @throws org.ccsds.moims.mo.mal.MALInteractionException
     */
    public void setReadOnlyParameter(Identifier name, Boolean value)  throws MALException, MALInteractionException;
    
    /**
     * Sets a list of update-intervals, that will be provided by the provider. A parameters updateInterval, must be equal to one of these provided interval.
     * @param providedIntervals list of update-intervals, that will be provided by the provider
     * @throws org.ccsds.moims.mo.mal.MALException
     * @throws org.ccsds.moims.mo.mal.MALInteractionException
     */
    public void setProvidedIntervals(DurationList providedIntervals)  throws MALException, MALInteractionException;

}
