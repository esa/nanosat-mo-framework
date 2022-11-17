/*
 * M&C Services for CCSDS Mission Operations Framework
 * Copyright (C) 2021 Deutsches Zentrum für Luft- und Raumfahrt e.V. (DLR).
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
package esa.mo.mc.impl.util;

import org.ccsds.moims.mo.mal.structures.BooleanList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;

/**
 * This is a helper model-class, for the retrieval of group-instances by a
 * Service-Operation. It is mainly used in the implementation of the
 * enableGeneration-operations by all services. It contains the information
 * where unknown/invalid errors occured and which instances should be
 * enabled/disabled (information contained in the "values" parameter) after
 * getting all instances recursively referenced in the group.
 *
 * @author Vorwerg
 */
public class GroupRetrieval {

    public GroupRetrieval(UIntegerList unkIndexList, UIntegerList invIndexList, LongList objIdToBeEnabled,
        BooleanList valueToBeEnabled) {
        this.unkIndexList = unkIndexList;
        this.invIndexList = invIndexList;
        this.objIdToBeEnabled = objIdToBeEnabled;
        this.valueToBeEnabled = valueToBeEnabled;
    }

    UIntegerList unkIndexList;
    UIntegerList invIndexList;
    LongList objIdToBeEnabled;
    BooleanList valueToBeEnabled;

    public UIntegerList getUnkIndexList() {
        return unkIndexList;
    }

    /**
     * adds the index to the list, ONLY IF it is not contained in the list yet.
     *
     * @param index
     */
    public void addUnkIndex(UInteger index) {
        if (!this.unkIndexList.contains(index)) {
            this.unkIndexList.add(index);
        }
    }

    public void setUnkIndexList(UIntegerList unkIndexList) {
        this.unkIndexList = unkIndexList;
    }

    public UIntegerList getInvIndexList() {
        return invIndexList;
    }

    public void setInvIndexList(UIntegerList invIndexList) {
        this.invIndexList = invIndexList;
    }

    /**
     * adds the index to the list, ONLY IF it is not contained in the list yet.
     *
     * @param index
     */
    public void addInvIndex(UInteger index) {
        if (!this.invIndexList.contains(index)) {
            this.invIndexList.add(index);
        }
    }

    public LongList getObjIdToBeEnabled() {
        return objIdToBeEnabled;
    }

    public void addObjIdToBeEnabled(Long objIdToBeEnabled) {
        this.objIdToBeEnabled.add(objIdToBeEnabled);
    }

    public void setObjIdToBeEnabled(LongList objIdToBeEnabled) {
        this.objIdToBeEnabled = objIdToBeEnabled;
    }

    public BooleanList getValueToBeEnabled() {
        return valueToBeEnabled;
    }

    public void addValueToBeEnabled(Boolean valueToBeEnabled) {
        this.valueToBeEnabled.add(valueToBeEnabled);
    }

    public void setValueToBeEnabled(BooleanList valueToBeEnabled) {
        this.valueToBeEnabled = valueToBeEnabled;
    }

}
