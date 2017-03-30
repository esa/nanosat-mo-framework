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
package esa.mo.mc.impl.util;

import esa.mo.com.impl.provider.EventProviderServiceImpl;
import org.ccsds.moims.mo.com.activitytracking.consumer.ActivityTrackingStub;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveStub;
import org.ccsds.moims.mo.com.event.consumer.EventStub;

/**
 * 
 * TODO: not used at the moment. Remove?
 * 
 * Class containing all COM-Services. The archive may be used as a service by
 * another provider. The EventService and the ActivityTracking-Service must be
 * provided by our MC-Provider. Thats because the activityTracking cant be used
 * as a service, thus you need to implement it by yourself. And to implement it,
 * you have to provide the EventService as well. Because you are publishing
 * events. So you have to provide both services.
 *
 * @author Vorwerg
 */
public class COMConsumerServices {

    ArchiveStub archiveStub;
    EventStub eventStub;
    ActivityTrackingStub activityTracking;

    /**
     *
     * @param archiveStub
     * @param eventStub
     * @param activityTracking the activity tracking "service". Suppport class
     * publishing events belonging to the ActivityTrackingService
     */
    public COMConsumerServices(ArchiveStub archiveStub, EventStub eventStub, ActivityTrackingStub activityTracking) {
        
        this.archiveStub = archiveStub;
        this.eventStub = eventStub;
        this.activityTracking = activityTracking;
    }

    public ArchiveStub getArchiveStub() {
        return archiveStub;
    }

    public void setArchiveStub(ArchiveStub archiveStub) {
        this.archiveStub = archiveStub;
    }

    public EventStub getEventStub() {
        return eventStub;
    }

    public void setEventStub(EventStub eventStub) {
        this.eventStub = eventStub;
    }

    public ActivityTrackingStub getActivityTracking() {
        return activityTracking;
    }

    public void setActivityTracking(ActivityTrackingStub activityTracking) {
        this.activityTracking = activityTracking;
    }

}
