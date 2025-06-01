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
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.Time;
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

/**
 * MPFactory contains factory methods to create blank MP structures with default values
 */
public class MPFactory extends MOFactory {

    /**
     * Creates a blank Request Version instance
     */
    public static RequestVersionDetails createRequestVersion() {
        return new RequestVersionDetails(null, new ArgumentList(), "", null,
                "", null, null, null, null,
                null, null, null, null, null);
    }

    /**
     * Creates a blank Activity instance
     */
    public static ActivityInstanceDetails createActivityInstance() {
        ConstraintNode constraints = new ConstraintNode(new c_ConstraintList(), null, null);
        return new ActivityInstanceDetails(new LongList(), "", constraints, null);
    }

    /**
     * Creates a blank Activity update with given status and current time
     */
    public static ActivityUpdateDetails createActivityUpdate(ActivityStatus activityStatus) {
        return new ActivityUpdateDetails(null, null, null, null, null,
                null, null, null, null, activityStatus, Time.now());
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
        LongList eventList = new LongList();
        eventList.addAll(Arrays.asList(events));
        return new EventInstanceDetails(eventList);
    }

    /**
     * Creates a blank Plan Version instance
     * @param description The description.
     * @return The plan version object.
     */
    public static PlanVersionDetails createPlanVersion(String description) {
        PlanInformation planInformation = new PlanInformation("", description, null,
                null, null, Time.now(), null, null, null);
        PlannedItems plannedItems = new PlannedItems(new PlannedActivityList(), new PlannedEventList());
        return new PlanVersionDetails(planInformation, plannedItems, null,
                null, false, false, null, null);
    }

    /**
     * Creates a plan update with given status and current time
     */

    public static PlanUpdateDetails createPlanUpdate(PlanStatus planStatus, String terminationInfo) {
        return  new PlanUpdateDetails(false, planStatus, terminationInfo, Time.now());
    }

    /**
     * Creates a blank Activity Definition
     */
    public static ActivityDefinitionDetails createActivityDefinition(String description, Identifier execDef) {
        return new ActivityDefinitionDetails(
                new c_ArgDefList(),
                new ActivityNode(),
                new ConstraintNode(),
                new StringList(),
                description,
                "",
                new DurationExpression(),
                execDef,
                "");
    }

    /**
     * Creates a blank Event Definition
     */
    public static EventDefinitionDetails createEventDefinition(String description) {
        return new EventDefinitionDetails(new c_ArgDefList(), description, "", new LongList(), "");
    }

    /**
     * Creates a blank Request Template
     */
    public static RequestTemplateDetails createRequestTemplate(String description) {
        return new RequestTemplateDetails(new ActivityNode(), new c_ArgDefList(),
                new ConstraintNode(), description, false, "");
    }

    /**
     * Creates a blank Numeric Resource Definition
     */
    public static NumericResourceDefinitionDetails createNumericResourceDefinition(String description) {
        return new NumericResourceDefinitionDetails(null, description,
                null, "", new ResourceProfile(), new ResourceProfile());
    }
}
