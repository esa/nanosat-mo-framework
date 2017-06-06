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

import esa.mo.mc.impl.provider.CheckManager;
import esa.mo.mc.impl.util.MCServicesHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.event.consumer.EventStub;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mc.check.CheckHelper;

/**
 *
 * @author Vorwerg
 */
public class CheckLinkMonitorManager {

    private EventStub eventService;
    private final CheckLinkMonitorAdapter adapter;
    private final CheckManager checkManager;
    /**
     * key: CheckLinkId that is referenced in the CompoundCheck; value:
     * CheckLink Ids of the compounds that contain the CheckLink;
     */
    private final Map<Long, List<Long>> notifyList;

    public CheckLinkMonitorManager(EventStub eventService, CheckManager checkManager) {
        this.adapter = new CheckLinkMonitorAdapter(this);
        this.eventService = eventService;
        this.checkManager = checkManager;
        this.notifyList = new HashMap<Long, List<Long>>();
    }

    /**
     * adds the compounded checkLinks to the monitored ones and lets the
     * CompoundCheck automatically executed if a new event for one of these is
     * received.
     *
     * @param checkLinkId the id of the checkLink that references to a compound
     * check
     * @param compoundedCheckLinks the checkLinks referenced in the compound
     * check
     */
    public synchronized void add(Long checkLinkId, List<Long> compoundedCheckLinks) {
        for (Long compoundedCheckLink : compoundedCheckLinks) {
            List<Long> currentChecksToNotify = notifyList.get(compoundedCheckLink);
            if (currentChecksToNotify == null) {
                currentChecksToNotify = new ArrayList<Long>();
            }
            currentChecksToNotify.add(checkLinkId);
            notifyList.put(compoundedCheckLink, currentChecksToNotify);
        }
        registerForCheckTranisitionEvents();

    }

    /**
     * removes the compounded checkLinks from the monitored ones.
     *
     * @param checkLinkId the id of the checkLink that references to a compound
     * check
     * @param compoundedCheckLinks the checkLinks referenced in the compound
     */
    public synchronized void remove(Long checkLinkId, List<Long> compoundedCheckLinks) {
        deregisterForCheckTranisitionEvents();
        for (Long compoundedCheckLink : compoundedCheckLinks) {
            final List<Long> checksToNotify = notifyList.get(compoundedCheckLink);
            checksToNotify.remove(checkLinkId);
            if (checksToNotify.isEmpty()) {
                notifyList.remove(compoundedCheckLink);
            }
        }
    }

    /**
     * executes a compound check
     *
     * @param sourceCheckLinkId
     * @param domain
     */
    public synchronized void updatedCheckLinkEvaluation(Long sourceCheckLinkDefId, IdentifierList domain) {
		// sourceCheckLinkDefId was obtained from related link of CheckTransition
		// but we need the CheckLink id here, because that is what was put into notifyList
		Long sourceCheckLinkId = checkManager.getCheckLinkId(sourceCheckLinkDefId);
        final List<Long> checkLinksToNotify = notifyList.get(sourceCheckLinkId);
        if (checkLinksToNotify != null) {
            for (Long checkLinkToNotify : checkLinksToNotify) {
                checkManager.executeCheck(checkLinkToNotify, null, false, false, 
                        new ObjectId(CheckHelper.CHECKLINK_OBJECT_TYPE, new ObjectKey(domain, sourceCheckLinkId)));
            }
        }
    }

    /**
     * registers for all events that are published for CheckTransitions.
     */
    private synchronized void registerForCheckTranisitionEvents() {
		try {
			eventService.monitorEventRegister(subscriptionKeys(new Identifier("AllCheckTransitions"), new Identifier("4"), 0L, 0L, 0L), adapter);
		} catch (MALInteractionException ex) {
			Logger.getLogger(CheckLinkMonitorManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (MALException ex) {
			Logger.getLogger(CheckLinkMonitorManager.class.getName()).log(Level.SEVERE, null, ex);
		}
    }

    /**
     * deregisters for the events that are published for CheckTransitions.
     */
    private synchronized void deregisterForCheckTranisitionEvents() {
		try {
			IdentifierList subIdentifiers = new IdentifierList();
			subIdentifiers.add(new Identifier("AllCheckTransitions"));
			eventService.monitorEventDeregister(subIdentifiers);
		} catch (MALInteractionException ex) {
			Logger.getLogger(CheckLinkMonitorManager.class.getName()).log(Level.SEVERE, null, ex);
		} catch (MALException ex) {
			Logger.getLogger(CheckLinkMonitorManager.class.getName()).log(Level.SEVERE, null, ex);
		}
    }

    /**
     *
     * Returns a subscription object with the entity keys field set as the
     * provided keys
     *
     * @param subId Identifier of the subscription
     * @param key1 First key - event object number
     * @param key2 Second key - the area, service, and version ObjectType fields
     * as a MAL::Long
     * @param key3 Third key - the event object instance identifier.
     * @param key4 Fourth key - the area, service, version and number fields of
     * the event source ObjectType
     * @return The subscription object
     */
    private Subscription subscriptionKeys(Identifier subId, Identifier key1, Long key2, Long key3, Long key4) {
        final EntityKeyList entityKeys = new EntityKeyList();
        final EntityKey entitykey = new EntityKey(key1, key2, key3, key4);
        entityKeys.add(entitykey);

        final EntityRequest entity = new EntityRequest(null, false, false, false, false, entityKeys);
        final EntityRequestList entities = new EntityRequestList();
        entities.add(entity);

        return new Subscription(subId, entities);
    }

}
