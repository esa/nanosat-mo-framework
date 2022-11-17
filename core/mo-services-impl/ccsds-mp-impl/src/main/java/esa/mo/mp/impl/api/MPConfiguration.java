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
package esa.mo.mp.impl.api;

import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionHelper;
import org.ccsds.moims.mo.mp.planedit.PlanEditHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestHelper;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityInstanceDetails;
import org.ccsds.moims.mo.mp.structures.ActivityUpdateDetails;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.EventInstanceDetails;
import org.ccsds.moims.mo.mp.structures.EventUpdateDetails;
import org.ccsds.moims.mo.mp.structures.FunctionDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.PlanUpdateDetails;
import org.ccsds.moims.mo.mp.structures.PlanVersionDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestUpdateDetails;
import org.ccsds.moims.mo.mp.structures.RequestVersionDetails;
import org.ccsds.moims.mo.mp.structures.ResourceUpdateDetails;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetails;
import esa.mo.mp.impl.util.BidirectionalMap;
import esa.mo.mp.impl.com.COMConfiguration;

/**
 * MPConfiguration has the knowledge on MP inverse related links. Examples:
 * <p>
 * EventIdentity -> EventDefinition, EventInstance -> EventStatus
 * <p>
 * RequestIdentity -> RequestVersion, RequestVersion -> RequestUpdate
 * <p>
 * RequestTemplateIdentity -> RequestTemplate
 *
 * @see COMConfiguration
 */
public class MPConfiguration implements COMConfiguration {

    private static BidirectionalMap<ObjectType, ObjectType> relatedTypes = new BidirectionalMap<>();
    private static BidirectionalMap<ObjectType, ObjectType> relatedConfigurationTypes = new BidirectionalMap<>();

    @Override
    public ObjectType getObjectType(Element element) {
        // Plan
        if (element instanceof PlanVersionDetails) {
            return PlanDistributionHelper.PLANVERSION_OBJECT_TYPE;
        }
        if (element instanceof PlanUpdateDetails) {
            return PlanDistributionHelper.PLANUPDATE_OBJECT_TYPE;
        }

        // Planning Request
        if (element instanceof RequestTemplateDetails) {
            return PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE;
        }
        if (element instanceof RequestVersionDetails) {
            return PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE;
        }
        if (element instanceof RequestUpdateDetails) {
            return PlanningRequestHelper.REQUESTSTATUSUPDATE_OBJECT_TYPE;
        }

        // Activity
        if (element instanceof ActivityDefinitionDetails) {
            return PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE;
        }
        if (element instanceof ActivityInstanceDetails) {
            return PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE;
        }
        if (element instanceof ActivityUpdateDetails) {
            return PlanEditHelper.ACTIVITYUPDATE_OBJECT_TYPE;
        }

        // Event
        if (element instanceof EventDefinitionDetails) {
            return PlanInformationManagementHelper.EVENTDEFINITION_OBJECT_TYPE;
        }
        if (element instanceof EventInstanceDetails) {
            return PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE;
        }
        if (element instanceof EventUpdateDetails) {
            return PlanEditHelper.EVENTUPDATE_OBJECT_TYPE;
        }

        // Resource
        if (element instanceof c_ResourceDefinitionDetails) {
            return PlanInformationManagementHelper.RESOURCEDEFINITION_OBJECT_TYPE;
        }
        if (element instanceof ResourceUpdateDetails) {
            return PlanEditHelper.RESOURCEUPDATE_OBJECT_TYPE;
        }

        // Function
        if (element instanceof FunctionDefinitionDetails) {
            return PlanInformationManagementHelper.FUNCTIONDEFINITION_OBJECT_TYPE;
        }

        return null;
    }

    @Override
    public ObjectType getConfigurationType(ObjectType relatedType) {
        return relatedConfigurationTypes.get(relatedType);
    }

    @Override
    public ObjectType getRelatedType(ObjectType objectType) {
        return relatedTypes.getKey(objectType);
    }

    @Override
    public ObjectType getInverseRelatedType(ObjectType objectType) {
        return relatedTypes.get(objectType);
    }

    static {
        // PlanIdentity <=> PlanVersion <=> PlanUpdate
        relatedTypes.put(PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE,
            PlanDistributionHelper.PLANVERSION_OBJECT_TYPE);

        relatedTypes.put(PlanDistributionHelper.PLANVERSION_OBJECT_TYPE, PlanDistributionHelper.PLANUPDATE_OBJECT_TYPE);

        // RequestTemplateIdentity <=> RequestTemplate
        relatedTypes.put(PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.REQUESTTEMPLATE_OBJECT_TYPE);

        // RequestIdentity <=> RequestVersion <=> RequestStatusUpdate
        relatedTypes.put(PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE,
            PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE);

        relatedTypes.put(PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE,
            PlanningRequestHelper.REQUESTSTATUSUPDATE_OBJECT_TYPE);

        // ActivityIdentity <=> ActivityDefinition <=> ActivityInstance <=> ActivityUpdate
        relatedTypes.put(PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE);

        relatedTypes.put(PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE,
            PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE);

        relatedTypes.put(PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE, PlanEditHelper.ACTIVITYUPDATE_OBJECT_TYPE);

        // EventIdentity <=> EventDefinition <=> EventInstance <=> EventUpdate
        relatedTypes.put(PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.EVENTDEFINITION_OBJECT_TYPE);

        relatedTypes.put(PlanInformationManagementHelper.EVENTDEFINITION_OBJECT_TYPE,
            PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE);

        relatedTypes.put(PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE, PlanEditHelper.EVENTUPDATE_OBJECT_TYPE);

        // ResourceIdentity <=> ResourceDefinition <=> ResourceUpdate
        relatedTypes.put(PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.RESOURCEDEFINITION_OBJECT_TYPE);

        relatedTypes.put(PlanInformationManagementHelper.RESOURCEDEFINITION_OBJECT_TYPE,
            PlanEditHelper.RESOURCEUPDATE_OBJECT_TYPE);

        // FunctionIdentity <=> FunctionDefinition
        relatedTypes.put(PlanInformationManagementHelper.FUNCTIONIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.FUNCTIONDEFINITION_OBJECT_TYPE);

        // Plan Identity
        relatedConfigurationTypes.put(PlanDistributionHelper.PLANIDENTITY_OBJECT_TYPE,
            PlanDistributionHelper.PLANIDENTITYTOPLANVERSION_OBJECT_TYPE);

        // Plan Version
        relatedConfigurationTypes.put(PlanDistributionHelper.PLANVERSION_OBJECT_TYPE,
            PlanDistributionHelper.PLANVERSIONTOPLANUPDATE_OBJECT_TYPE);

        // Request Template Identity
        relatedConfigurationTypes.put(PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITYTOREQUESTTEMPLATE_OBJECT_TYPE);

        // Request Identity
        relatedConfigurationTypes.put(PlanningRequestHelper.REQUESTIDENTITY_OBJECT_TYPE,
            PlanningRequestHelper.REQUESTIDENTITYTOREQUESTVERSION_OBJECT_TYPE);

        // Request Version
        relatedConfigurationTypes.put(PlanningRequestHelper.REQUESTVERSION_OBJECT_TYPE,
            PlanningRequestHelper.REQUESTVERSIONTOREQUESTSTATUSUPDATE_OBJECT_TYPE);

        // Activity Identity
        relatedConfigurationTypes.put(PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.ACTIVITYIDENTITYTOACTIVITYDEFINITION_OBJECT_TYPE);

        // Activity Definition
        relatedConfigurationTypes.put(PlanInformationManagementHelper.ACTIVITYDEFINITION_OBJECT_TYPE,
            PlanInformationManagementHelper.ACTIVITYDEFINITIONTOACTIVITYINSTANCE_OBJECT_TYPE);

        // Activity Instance
        relatedConfigurationTypes.put(PlanEditHelper.ACTIVITYINSTANCE_OBJECT_TYPE,
            PlanEditHelper.ACTIVITYINSTANCETOACTIVITYSTATUS_OBJECT_TYPE);

        // Event Identity
        relatedConfigurationTypes.put(PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.EVENTIDENTITYTOEVENTDEFINITION_OBJECT_TYPE);

        // Event Definition
        relatedConfigurationTypes.put(PlanInformationManagementHelper.EVENTDEFINITION_OBJECT_TYPE,
            PlanInformationManagementHelper.EVENTDEFINITIONTOEVENTINSTANCE_OBJECT_TYPE);

        // Event Instance
        relatedConfigurationTypes.put(PlanEditHelper.EVENTINSTANCE_OBJECT_TYPE,
            PlanEditHelper.EVENTINSTANCETOEVENTSTATUS_OBJECT_TYPE);

        // Resource Identity
        relatedConfigurationTypes.put(PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.RESOURCEIDENTITYTORESOURCEDEFINITION_OBJECT_TYPE);

        // Resource Definition
        relatedConfigurationTypes.put(PlanInformationManagementHelper.RESOURCEDEFINITION_OBJECT_TYPE,
            PlanInformationManagementHelper.RESOURCEIDENTITYTORESOURCEDEFINITION_OBJECT_TYPE // TODO
        );

        // Function Identity
        relatedConfigurationTypes.put(PlanInformationManagementHelper.FUNCTIONIDENTITY_OBJECT_TYPE,
            PlanInformationManagementHelper.FUNCTIONIDENTITYTOFUNCTIONDEFINITION_OBJECT_TYPE);
    }
}
