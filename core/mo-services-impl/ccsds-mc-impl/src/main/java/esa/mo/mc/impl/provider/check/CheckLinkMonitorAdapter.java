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
package esa.mo.mc.impl.provider.check;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.event.consumer.EventAdapter;
import org.ccsds.moims.mo.com.structures.ObjectDetails;
import org.ccsds.moims.mo.com.structures.ObjectDetailsList;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 *
 * @author Vorwerg
 */
public class CheckLinkMonitorAdapter extends EventAdapter {

    CheckLinkMonitorManager manager;

    public CheckLinkMonitorAdapter(CheckLinkMonitorManager manager) {
        this.manager = manager;
    }

    @Override
    public void monitorEventRegisterAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
        //save the value of the register-time to test the periodic update later on
        Logger.getLogger(CheckLinkMonitorAdapter.class.getName()).log(Level.INFO,
            "successfully registered for monitorEvents");
        super.monitorEventRegisterAckReceived(msgHeader, qosProperties);
    }

    @Override
    public void monitorEventRegisterErrorReceived(MALMessageHeader msgHeader, MALStandardError error,
        Map qosProperties) {
        Logger.getLogger(CheckLinkMonitorAdapter.class.getName()).log(Level.SEVERE,
            "registration for monitorEvents failed with error {0}", error.getErrorName());
        super.monitorEventRegisterErrorReceived(msgHeader, error, qosProperties);
    }

    @Override
    public void monitorEventNotifyErrorReceived(MALMessageHeader msgHeader, MALStandardError error, Map qosProperties) {
        Logger.getLogger(CheckLinkMonitorAdapter.class.getName()).log(Level.SEVERE,
            "monitorEvents notification failed with error {0}", error.getErrorName());
        super.monitorEventNotifyErrorReceived(msgHeader, error, qosProperties);
    }

    @Override
    public void monitorEventNotifyReceived(MALMessageHeader msgHeader, Identifier _Identifier0,
        UpdateHeaderList _UpdateHeaderList1, ObjectDetailsList _ObjectDetailsList2, ElementList elementList,
        Map qosProperties) {
        Logger.getLogger(CheckLinkMonitorAdapter.class.getName()).log(Level.INFO, "monitorEvents-update received");
        for (ObjectDetails objectDetails : _ObjectDetailsList2) {
            manager.updatedCheckLinkEvaluation(objectDetails.getRelated(), msgHeader.getDomain());
        }

        super.monitorEventNotifyReceived(msgHeader, _Identifier0, _UpdateHeaderList1, _ObjectDetailsList2, elementList,
            qosProperties);
    }

    @Override
    public void monitorEventDeregisterAckReceived(MALMessageHeader msgHeader, Map qosProperties) {
        Logger.getLogger(CheckLinkMonitorAdapter.class.getName()).log(Level.INFO,
            "successfully deregistered monitorEvents");
        super.monitorEventDeregisterAckReceived(msgHeader, qosProperties); //To change body of generated methods, choose Tools | Templates.
    }
}
