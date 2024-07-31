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

import esa.mo.mc.impl.provider.CheckProviderServiceImpl;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.parameter.ParameterHelper;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValueList;

/**
 *
 * @author Vorwerg
 */
public class ParameterMonitorAdapter extends ParameterAdapter {

    ParameterMonitoringManager paramMonitorManager;

    /**
     * creates a ParameterMonitorAdapter that is monitoring the parameter that will be registered.
     * @param paramMonitorManager the manager, that should be notified, when a new parameterValue arrived.
     */
    public ParameterMonitorAdapter(ParameterMonitoringManager paramMonitorManager) {
        this.paramMonitorManager = paramMonitorManager;
    }

    @Override
    public void monitorValueRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
        //save the value of the register-time to test the periodic update later on
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.INFO,
            "successfully registered for monitorvalue");
        super.monitorValueRegisterAckReceived(msgHeader, qosProperties);
    }

    @Override
    public void monitorValueRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
        Map qosProperties) {
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.SEVERE,
            "registration for monitorvalue failed with error {0}", new Object[]{error.getErrorName()});
        super.monitorValueRegisterErrorReceived(msgHeader, error, qosProperties);
    }

    @Override
    public void monitorValueNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
        UpdateHeaderList _UpdateHeaderList1, ObjectIdList paramValueObjectIds, ParameterValueList parameterValueList,
        Map qosProperties) {

        final Long paramIdentityId = _UpdateHeaderList1.get(0).getKey().getSecondSubKey();
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.INFO,
            "monitorvalue-update for parameter with identity id: {0} received", new Object[]{paramIdentityId});
        final ParameterValue newParamValue = parameterValueList.get(0);
        final ObjectId paramValObjId = new ObjectId(ParameterHelper.PARAMETERVALUEINSTANCE_OBJECT_TYPE, new ObjectKey(
            msgHeader.getDomain(), _UpdateHeaderList1.get(0).getKey().getFourthSubKey()));

        //set as the current parameterValue
        paramMonitorManager.setParameterValue(paramIdentityId, newParamValue, paramValObjId);

        super.monitorValueNotifyReceived(msgHeader, _Identifier0, _UpdateHeaderList1, paramValueObjectIds,
            parameterValueList, qosProperties);
    }

    @Override
    public void monitorValueNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.SEVERE,
            "monitorvalue notification failed with error {0}", new Object[]{error.getErrorName()});
        super.monitorValueNotifyErrorReceived(msgHeader, error, qosProperties);
    }

    @Override
    public void monitorValueDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.INFO,
            "successfully deregistered monitorvalue");
        super.monitorValueDeregisterAckReceived(msgHeader, qosProperties); //To change body of generated methods, choose Tools | Templates.
    }
}
