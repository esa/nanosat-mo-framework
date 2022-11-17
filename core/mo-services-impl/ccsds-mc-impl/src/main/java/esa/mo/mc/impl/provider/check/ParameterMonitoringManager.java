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

import esa.mo.mc.impl.provider.CheckManager;
import esa.mo.mc.impl.provider.ParameterManager;
import esa.mo.mc.impl.util.MCServicesConsumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mc.check.structures.CheckDefinitionDetails;
import org.ccsds.moims.mo.mc.check.structures.CheckLinkDetails;
import org.ccsds.moims.mo.mc.check.structures.DeltaCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceCheckDefinition;
import org.ccsds.moims.mo.mc.check.structures.ReferenceValue;
import org.ccsds.moims.mo.mc.parameter.consumer.ParameterStub;
import org.ccsds.moims.mo.mc.parameter.structures.ParameterValue;

/**
 * This class manages all monitorings for parameter-updates. The new
 * parameter-values are needed for the evaluation of the CheckLinks. Each
 * CheckLinks parameter will be monitored here. 
 */
public class ParameterMonitoringManager {

    private final CheckManager manager;
    private final ParameterStub parameterStub;
    private final ParameterManager paramManager;
    /**
     * adapter that is monitoring all parameters
     */
    final ParameterMonitorAdapter adapter;

    /**
     * Current list of checks for one parameter Key: Parameter Identity Id that is
     * monitored; Value: List of CheckLinks ids that are checked changes.
     */
    private final HashMap<Long, List<Long>> parameterChecks = new HashMap<>();
    /**
     * Current list of parameterValues
     */
    private final HashMap<Long, List<ParameterValueEntry>> parameterValues = new HashMap<>();

    /**
     * key: the parameter-identity-Id; value: the checkLinkId
     */
    private final HashMap<Long, List<Long>> parameterReferences = new HashMap<>();
    /**
     * Key: Parameter Identity Id that is monitored; Value: List of CheckLinks ids
     * that will be notified (a check will be executed) if parameterValue
     * changes.
     */
    private final HashMap<Long, List<Long>> onChangeNotifierList = new HashMap<>();

    public ParameterMonitoringManager(CheckManager manager, MCServicesConsumer mcServicesConsumer,
        ParameterManager paramManager) {
        this.manager = manager;
        this.paramManager = paramManager;
        this.parameterStub = mcServicesConsumer.getParameterService().getParameterStub();
        this.adapter = new ParameterMonitorAdapter(this);
    }

    /**
     * adds the checkLinks parameters to the monitored ones. There are one, two
     * or three parameters added to the monitored parameters. The first one is
     * the parameter that is referenced in the CheckLinks-Source-Link. The
     * second one is the parameter that is referenced in the
     * CheckLinks-condition, which is optional. And the third one is the
     * parameter referenced in the ReferenceCheckDefinition or
     * DeltaCheckDefinition.
     *
     * @param checkLinkId the id of the checkLink of which the parameters should
     * be monitored for.
     * @throws MALException
     * @throws MALInteractionException
     */
    public void add(Long checkLinkId) throws MALException, MALInteractionException {
        final CheckLinkDetails checkLinkDetails = manager.getCheckLinkDetails(manager.getCheckLinkDefId(checkLinkId));
        //add linked parameter
        Long paramIdentityId = manager.getCheckLinkLinks(checkLinkId).getSource().getKey().getInstId();
        addParameterToMonitor(paramIdentityId, checkLinkDetails, checkLinkId);
        //add condition-parameter
        if (checkLinkDetails.getCondition() != null) {
            final Long condParamIdentityId = checkLinkDetails.getCondition().getParameterId().getInstId();
            addParameterToMonitor(condParamIdentityId, checkLinkDetails, checkLinkId);
        }
        //add referenced parameter (referenced in ReferenceCheck/DeltaCheck) 
        final CheckDefinitionDetails checkDefDetails = manager.getActualCheckDefinitionFromCheckLinks(checkLinkId);
        if (checkDefDetails instanceof ReferenceCheckDefinition || checkDefDetails instanceof DeltaCheckDefinition) {
            final ReferenceValue checkReference = checkDefDetails instanceof ReferenceCheckDefinition ?
                ((ReferenceCheckDefinition) checkDefDetails).getCheckReference() :
                ((DeltaCheckDefinition) checkDefDetails).getCheckReference();
            if (checkReference.getParameterId() != null) {
                final Long refParamIdentityId = checkReference.getParameterId().getInstId();
                addParameterReferenceToMonitor(refParamIdentityId, checkLinkId);
                //set the first Reference Value
                manager.getCheckLinkEvaluations().get(checkLinkId).setRefParamValue(parameterValues.get(
                    refParamIdentityId).get(0).getValue().getRawValue());
            }
        }

    }

    /**
     * removes the monitoring and its current values
     *
     * @param checkLinkId
     * @throws MALException
     * @throws MALInteractionException
     */
    public void remove(Long checkLinkId) throws MALException, MALInteractionException {
        //remove referenced parameter
        final Long paramIdentityId = manager.getCheckLinkLinks(checkLinkId).getSource().getKey().getInstId();
        removeParameterFromLists(paramIdentityId, checkLinkId);
        //remove condition-parameter
        final CheckLinkDetails checkLinkDetails = manager.getCheckLinkDetails(manager.getCheckLinkDefId(checkLinkId));
        if (checkLinkDetails.getCondition() != null) {
            final Long condParamIdentityId = checkLinkDetails.getCondition().getParameterId().getInstId();
            removeParameterFromLists(condParamIdentityId, checkLinkId);
        }
        final CheckDefinitionDetails checkDefDetails = manager.getActualCheckDefinitionFromCheckLinks(checkLinkId);
        if (checkDefDetails instanceof ReferenceCheckDefinition || checkDefDetails instanceof DeltaCheckDefinition) {
            final ReferenceValue checkReference = checkDefDetails instanceof ReferenceCheckDefinition ?
                ((ReferenceCheckDefinition) checkDefDetails).getCheckReference() :
                ((DeltaCheckDefinition) checkDefDetails).getCheckReference();
            if (checkReference.getParameterId() != null) {
                final Long refParamIdentityId = checkReference.getParameterId().getInstId();
                removeParameterFromLists(refParamIdentityId, checkLinkId);
            }
        }
    }

    /**
     * sets the new parameter in the internal lists, adds the checkLinkId, if an
     * OnChange is active and subscribes for this parameter
     * @param checkLinkDetails
     * @param checkLinkId
     * @throws MALException
     * @throws MALInteractionException
     */
    private void addParameterToMonitor(Long paramIdentityId, final CheckLinkDetails checkLinkDetails, Long checkLinkId)
        throws MALException, MALInteractionException {
        addToParameterCheckList(paramIdentityId, checkLinkId);
        //add to the OnChange-Notifier List, if necessary
        if (checkLinkDetails.getCheckOnChange()) {
            addToOnChangeNotifierList(paramIdentityId, checkLinkId);
        }
        //add to list of monitored parameterValues if necessary
        if (!parameterValues.containsKey(paramIdentityId)) {
            addToParameterValuesListAndRegister(paramIdentityId);
        }
    }

    private void addParameterReferenceToMonitor(Long paramIdentityId, Long checkLinkId) throws MALException,
        MALInteractionException {
        addToParameterCheckList(paramIdentityId, checkLinkId);

        final List<Long> currentRefCheckLinks = parameterReferences.get(paramIdentityId);
        if (currentRefCheckLinks == null) {
            final List<Long> newCheckLinks = new ArrayList<>();
            newCheckLinks.add(checkLinkId);
            parameterReferences.put(paramIdentityId, newCheckLinks);
        } else {
            if (!currentRefCheckLinks.contains(checkLinkId)) {
                currentRefCheckLinks.add(checkLinkId);
                parameterReferences.put(paramIdentityId, currentRefCheckLinks);
            }
        }
        //add to list of monitored parameterValues if necessary
        if (!parameterValues.containsKey(paramIdentityId)) {
            addToParameterValuesListAndRegister(paramIdentityId);
        }
    }

    private void addToParameterValuesListAndRegister(Long paramIdentityId) throws MALException,
        MALInteractionException {
        //add to list of monitored parameterValues
        List<ParameterValueEntry> values = new ArrayList<>();
        //get the first Values
        values.add(new ParameterValueEntry(paramManager.getParameterValue(paramIdentityId), new Time(System
            .currentTimeMillis())));
        parameterValues.put(paramIdentityId, values);
        //parameter will be registered at the adapter
        Subscription sub = subscriptionKeys(new Identifier("" + paramIdentityId), new Identifier("*"), paramIdentityId,
            0L, 0L);
        parameterStub.monitorValueRegister(sub, adapter);
    }

    /**
     * add CheckLinkId to List that should be notified, if the parameterValue
     * changed
     *
     * @param paramIdentityId
     * @param checkLinkId
     */
    private void addToOnChangeNotifierList(Long paramIdentityId, Long checkLinkId) {
        final List<Long> currentCheckLinks = onChangeNotifierList.get(paramIdentityId);
        if (currentCheckLinks == null) {
            final List<Long> newCheckLinks = new ArrayList<>();
            newCheckLinks.add(checkLinkId);
            onChangeNotifierList.put(paramIdentityId, newCheckLinks);
        } else {
            if (!currentCheckLinks.contains(checkLinkId)) {
                currentCheckLinks.add(checkLinkId);
                onChangeNotifierList.put(paramIdentityId, currentCheckLinks);
            }
        }
    }

    /**
     * add CheckLinkId to List that should be notified, if the parameterValue
     * changed
     *
     * @param checkLinkDetails
     * @param paramIdentityId
     * @param checkLinkId
     */
    private void addToParameterCheckList(Long paramIdentityId, Long checkLinkId) {
        final List<Long> currentCheckLinks = parameterChecks.get(paramIdentityId);
        if (currentCheckLinks == null) {
            final List<Long> newCheckLinks = new ArrayList<>();
            newCheckLinks.add(checkLinkId);
            parameterChecks.put(paramIdentityId, newCheckLinks);
        } else {
            if (!currentCheckLinks.contains(checkLinkId)) {
                currentCheckLinks.add(checkLinkId);
                parameterChecks.put(paramIdentityId, currentCheckLinks);
            }
        }
    }

    private void removeParameterFromLists(final Long paramIdentityId, Long checkLinkId) throws MALException,
        MALInteractionException {
        //remove form general lists of paramChecks
        final List<Long> checkLinks = parameterChecks.get(paramIdentityId);
        if (checkLinks != null) {
            checkLinks.remove(checkLinkId);
        }
        if (checkLinks == null || checkLinks.isEmpty()) {
            //parameter is not needed to be monitored anymore
            parameterChecks.remove(paramIdentityId);
            parameterValues.remove(paramIdentityId);
            onChangeNotifierList.remove(paramIdentityId);
            //parameter will be deregistered at the adapter
            IdentifierList subIdList = new IdentifierList();
            subIdList.add(new Identifier("" + paramIdentityId));
            parameterStub.monitorValueDeregister(subIdList);
        }
        //remove from the onChangeList, if it still exists
        final List<Long> onChangeCheckLinks = onChangeNotifierList.get(paramIdentityId);
        if (onChangeCheckLinks != null) {
            onChangeCheckLinks.remove(checkLinkId);
            if (onChangeCheckLinks.isEmpty()) {
                onChangeNotifierList.remove(paramIdentityId);
            }
        }
        //remove from referenceValueList
        final List<Long> refCheckLinks = parameterReferences.get(paramIdentityId);
        if (refCheckLinks != null) {
            refCheckLinks.remove(checkLinkId);
            if (refCheckLinks.isEmpty()) {
                parameterReferences.remove(paramIdentityId);
            }
        }
    }

    /**
     * continues all paused monitorings.
     *
     * @throws MALException
     * @throws MALInteractionException
     */
    public void startAll() throws MALException, MALInteractionException {
        for (Long paramIdentityId : parameterChecks.keySet()) {
            Subscription sub = subscriptionKeys(new Identifier("" + paramIdentityId), new Identifier("*"),
                paramIdentityId, 0L, 0L);
            parameterStub.monitorValueRegister(sub, adapter);
        }
    }

    /**
     * pauses all monitorings. The latest ParameterValue of each CheckLink will
     * be kept.
     *
     * @throws MALException
     * @throws MALInteractionException
     */
    public void pauseAll() throws MALException, MALInteractionException {
        IdentifierList subIdList = new IdentifierList();
        subIdList.add(new Identifier("*"));
        parameterStub.monitorValueDeregister(subIdList);
    }

    /**
     * sets the parameterValue and if the value changed, the checks should be
     * executed at the CheckLinks that have the CheckOnChange enabled.
     *
     * @param paramIdentityId the parameter that was updated
     * @param newParamValue the updated value
     * @param source the source id the event should have. in this case the
     * standard says: "The Obejct that caused the check evaluation to occur,
     * most liely the relevant ParamterValueinstance object."
     */
    public synchronized void setParameterValue(Long paramIdentityId, ParameterValue newParamValue, ObjectId source) {
        final List<ParameterValueEntry> paramValues = parameterValues.get(paramIdentityId);
        ParameterValue oldParamValue = paramValues.get(paramValues.size() - 1).getValue();
        paramValues.add(new ParameterValueEntry(newParamValue, new Time(System.currentTimeMillis())));
        //check the onChange-CheckLinks 
        if (!oldParamValue.equals(newParamValue)) {
            final List<Long> checkLinkIdsToNotify = onChangeNotifierList.get(paramIdentityId);
            if (checkLinkIdsToNotify != null)
                for (Long checkLinkId : checkLinkIdsToNotify) {
                    manager.executeCheck(checkLinkId, newParamValue, false, false, source);
                }
        }
        //the parameter is a reference-parameter
        final List<Long> refCheckLinks = parameterReferences.get(paramIdentityId);
        if (refCheckLinks != null) {
            //set the new value to the referenceValue if there are validCount samples in the last deltaTime seconds
            for (Long refCheckLink : refCheckLinks) {
                final CheckDefinitionDetails actCheckDef = manager.getActualCheckDefinitionFromCheckLinks(refCheckLink);
                ReferenceValue refValue = actCheckDef instanceof ReferenceCheckDefinition ?
                    ((ReferenceCheckDefinition) actCheckDef).getCheckReference() : ((DeltaCheckDefinition) actCheckDef)
                        .getCheckReference();
                List<ParameterValueEntry> values = parameterValues.get(paramIdentityId);
                //task: make it more effective and delete the expired ones
                long now = System.currentTimeMillis();
                int counter = 0;
                for (ParameterValueEntry value : values) {
                    if (now - value.getCreationTime().getValue() < Math.round(refValue.getDeltaTime().getValue() *
                        1000)) {
                        counter++;
                    }
                }
                if (counter >= refValue.getValidCount().getValue()) {
                    manager.getCheckLinkEvaluations().get(refCheckLink).setRefParamValue(newParamValue.getRawValue());
                    //                    values = values.subList(0, values.size()-2);
                    //                    parameterValues.put(paramIdentityId, values);
                }
            }
        }
    }

    /**
     *
     * Returns a subscription object with the entity keys field set as the
     * provided keys
     *
     * @param subId Identifier of the subscription
     * @param key1 First key - name
     * @param key2 Second key - identity-id
     * @param key3 Third key - definition-id
     * @param key4 Fourth key - value-id
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
