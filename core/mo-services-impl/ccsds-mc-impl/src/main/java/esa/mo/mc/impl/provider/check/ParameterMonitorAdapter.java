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
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MOErrorException;
import org.ccsds.moims.mo.mal.helpertools.helpers.HelperAttributes;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.NullableAttributeList;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mc.parameter.ParameterServiceInfo;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterAdapter;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

/**
 *
 * @author Vorwerg
 */
public class ParameterMonitorAdapter extends ParameterAdapter {

    ParameterMonitoringManager paramMonitorManager;

    /**
     * creates a ParameterMonitorAdapter that is monitoring the parameter that
     * will be registered.
     *
     * @param paramMonitorManager the manager, that should be notified, when a
     * new parameterValue arrived.
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
    public void monitorValueRegisterErrorReceived(MALMessageHeader msgHeader, MOErrorException error, Map qosProperties) {
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.SEVERE,
                "registration for monitorvalue failed with error", error);
        super.monitorValueRegisterErrorReceived(msgHeader, error, qosProperties);
    }

    @Override
    public void monitorValueNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
            UpdateHeader updateHeader, ObjectId paramValueObjectId, ParameterValue parameterValue, Map qosProperties) {
        NullableAttributeList keyValues = updateHeader.getKeyValues();

        //final Long paramIdentityId = _UpdateHeaderList1.get(0).getKey().getSecondSubKey();
        final Long paramIdentityId = (Long) HelperAttributes.attribute2JavaType(keyValues.get(1).getValue());
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.INFO,
                "monitorvalue-update for parameter with identity id: {0} received", new Object[]{paramIdentityId});
        final ParameterValue newParamValue = parameterValue;
        final Long key4 = (Long) HelperAttributes.attribute2JavaType(keyValues.get(3).getValue());
        final ObjectId paramValObjId = new ObjectId(ParameterServiceInfo.PARAMETERVALUEINSTANCE_OBJECT_TYPE,
                new ObjectKey(null, key4));

        //set as the current parameterValue
        paramMonitorManager.setParameterValue(paramIdentityId, newParamValue, paramValObjId);

        super.monitorValueNotifyReceived(msgHeader, _Identifier0, updateHeader,
                paramValueObjectId, parameterValue, qosProperties);
    }

    @Override
    public void monitorValueNotifyErrorReceived(MALMessageHeader msgHeader,
            MOErrorException error, Map qosProperties) {
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.SEVERE,
                "monitorvalue notification failed with error", error);
        super.monitorValueNotifyErrorReceived(msgHeader, error, qosProperties);
    }

    @Override
    public void monitorValueDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
        Logger.getLogger(CheckProviderServiceImpl.class.getName()).log(Level.INFO,
                "successfully deregistered monitorvalue");
        super.monitorValueDeregisterAckReceived(msgHeader, qosProperties);
    }
}
