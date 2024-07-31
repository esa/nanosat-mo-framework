/* ----------------------------------------------------------------------------
 * Copyright (C) 2021      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under European Space Agency Public License (ESA-PL) Weak Copyleft – v2.4
 * You may not use this file except in compliance with the License.
 *
 * Except as expressly set forth in this License, the Software is provided to
 * You on an "as is" basis and without warranties of any kind, including without
 * limitation merchantability, fitness for a particular purpose, absence of
 * defects or errors, accuracy or non-infringement of intellectual property rights.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ----------------------------------------------------------------------------
 */
package esa.mo.mp.impl.util;

import java.util.Arrays;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ActivityNode;
import org.ccsds.moims.mo.mp.structures.ActivityStatus;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.ConstraintNode;
import org.ccsds.moims.mo.mp.structures.DurationExpression;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.EventInstanceDetails;
import org.ccsds.moims.mo.mp.structures.NumericResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.PlanInformation;
import org.ccsds.moims.mo.mp.structures.PlanStatus;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.PlannedActivityList;
import org.ccsds.moims.mo.mp.structures.PlannedEventList;
import org.ccsds.moims.mo.mp.structures.PlannedItems;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.ResourceProfile;
import org.ccsds.moims.mo.mp.structures.c_ArgDefList;
import org.ccsds.moims.mo.mp.structures.ArgumentList;
import org.ccsds.moims.mo.mp.structures.c_ConstraintList;
import esa.mo.helpertools.helpers.HelperTime;

/**
 * MPFactory contains factory methods to create blank MP structures with default values
 */
public class MPFactory extends MOFactory {

    /**
     * Creates a blank Request Version instance
     */
    public static RequestVersionDetails createRequestVersion() {
        RequestVersionDetails requestVersion = new RequestVersionDetails();

        requestVersion.setArguments(new ArgumentList());
        requestVersion.setComments("");
        requestVersion.setDescription("");

        return requestVersion;
    }

    /**
     * Creates a blank Activity instance
     */
    public static ActivityInstanceDetails createActivityInstance() {
        ActivityInstanceDetails activityInstance = new ActivityInstanceDetails();

        activityInstance.setChildren(new LongList());

        activityInstance.setComments("");

        ConstraintNode constraints = new ConstraintNode();
        constraints.setConstraints(new c_ConstraintList());
        activityInstance.setConstraints(constraints);

        return activityInstance;
    }

    /**
     * Creates a blank Activity update with given status and current time
     */
    public static ActivityUpdateDetails createActivityUpdate(ActivityStatus activityStatus) {
        ActivityUpdateDetails activityUpdate = new ActivityUpdateDetails();

        activityUpdate.setStatus(activityStatus);
        activityUpdate.setTimestamp(HelperTime.getTimestampMillis());

        return activityUpdate;
    }

    /**
     * Creates a blank Event instance
     */
    public static EventInstanceDetails createEventInstance() {
        return createEventInstance(new Long[0]);
    }

    /**
     * Creates an Event instance with given events
     */
    public static EventInstanceDetails createEventInstance(Long... events) {
        EventInstanceDetails eventInstance = new EventInstanceDetails();

        LongList eventList = new LongList();
        eventList.addAll(Arrays.asList(events));
        eventInstance.setEvents(eventList);

        return eventInstance;
    }

    /**
     * Creates a blank Plan Version instance
     */
    public static PlanVersionDetails createPlanVersion() {
        PlanVersionDetails planVersion = new PlanVersionDetails();

        planVersion.setHasPrecursor(false);
        planVersion.setPatchPlan(false);

        PlanInformation planInformation = new PlanInformation();
        planInformation.setComments("");
        planInformation.setDescription("");
        planInformation.setProductionDate(HelperTime.getTimestampMillis());
        planVersion.setInformation(planInformation);

        PlannedItems plannedItems = new PlannedItems();
        plannedItems.setPlannedActivities(new PlannedActivityList());
        plannedItems.setPlannedEvents(new PlannedEventList());
        planVersion.setItems(plannedItems);

        return planVersion;
    }

    /**
     * Creates a plan update with given status and current time
     */

    public static PlanUpdateDetails createPlanUpdate(PlanStatus planStatus) {
        PlanUpdateDetails planUpdate = new PlanUpdateDetails();

        planUpdate.setStatus(planStatus);
        planUpdate.setAlternate(false);
        planUpdate.setTimestamp(HelperTime.getTimestampMillis());

        return planUpdate;
    }

    /**
     * Creates a blank Activity Definition
     */
    public static ActivityDefinitionDetails createActivityDefinition() {
        ActivityDefinitionDetails activityDefinition = new ActivityDefinitionDetails();

        activityDefinition.setArgDefs(new c_ArgDefList());
        activityDefinition.setChildren(new ActivityNode());
        activityDefinition.setConstraints(new ConstraintNode());
        activityDefinition.setDefaultTags(new StringList());
        activityDefinition.setDescription("");
        activityDefinition.setDisplayType("");
        activityDefinition.setDurationSpec(new DurationExpression());
        activityDefinition.setVersion("");

        return activityDefinition;
    }

    /**
     * Creates a blank Event Definition
     */
    public static EventDefinitionDetails createEventDefinition() {
        EventDefinitionDetails eventDefinition = new EventDefinitionDetails();

        eventDefinition.setArgDefs(new c_ArgDefList());
        eventDefinition.setDescription("");
        eventDefinition.setDisplayType("");
        eventDefinition.setEventDefs(new LongList());
        eventDefinition.setVersion("");

        return eventDefinition;
    }

    /**
     * Creates a blank Request Template
     */
    public static RequestTemplateDetails createRequestTemplate() {
        RequestTemplateDetails requestTemplate = new RequestTemplateDetails();

        requestTemplate.setActivities(new ActivityNode());
        requestTemplate.setArgDefs(new c_ArgDefList());
        requestTemplate.setConstraints(new ConstraintNode());
        requestTemplate.setDescription("");
        requestTemplate.setStandingOrder(false);
        requestTemplate.setVersion("");

        return requestTemplate;
    }

    /**
     * Creates a blank Numeric Resource Definition
     */
    public static NumericResourceDefinitionDetails createNumericResourceDefinition() {
        NumericResourceDefinitionDetails numericResourceDefinition = new NumericResourceDefinitionDetails();

        numericResourceDefinition.setDescription("");
        numericResourceDefinition.setMaximum(new ResourceProfile());
        numericResourceDefinition.setMinimum(new ResourceProfile());
        numericResourceDefinition.setVersion("");

        return numericResourceDefinition;
    }
}
