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

import esa.mo.mp.impl.com.COMConfiguration;
import esa.mo.mp.impl.util.BidirectionalMap;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mp.plandistribution.PlanDistributionServiceInfo;
import org.ccsds.moims.mo.mp.planedit.PlanEditServiceInfo;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementServiceInfo;
import org.ccsds.moims.mo.mp.planningrequest.PlanningRequestServiceInfo;
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
            return PlanDistributionServiceInfo.PLANVERSION_OBJECT_TYPE;
        }
        if (element instanceof PlanUpdateDetails) {
            return PlanDistributionServiceInfo.PLANUPDATE_OBJECT_TYPE;
        }

        // Planning Request
        if (element instanceof RequestTemplateDetails) {
            return PlanInformationManagementServiceInfo.REQUESTTEMPLATE_OBJECT_TYPE;
        }
        if (element instanceof RequestVersionDetails) {
            return PlanningRequestServiceInfo.REQUESTVERSION_OBJECT_TYPE;
        }
        if (element instanceof RequestUpdateDetails) {
            return PlanningRequestServiceInfo.REQUESTSTATUSUPDATE_OBJECT_TYPE;
        }

        // Activity
        if (element instanceof ActivityDefinitionDetails) {
            return PlanInformationManagementServiceInfo.ACTIVITYDEFINITION_OBJECT_TYPE;
        }
        if (element instanceof ActivityInstanceDetails) {
            return PlanEditServiceInfo.ACTIVITYINSTANCE_OBJECT_TYPE;
        }
        if (element instanceof ActivityUpdateDetails) {
            return PlanEditServiceInfo.ACTIVITYUPDATE_OBJECT_TYPE;
        }

        // Event
        if (element instanceof EventDefinitionDetails) {
            return PlanInformationManagementServiceInfo.EVENTDEFINITION_OBJECT_TYPE;
        }
        if (element instanceof EventInstanceDetails) {
            return PlanEditServiceInfo.EVENTINSTANCE_OBJECT_TYPE;
        }
        if (element instanceof EventUpdateDetails) {
            return PlanEditServiceInfo.EVENTUPDATE_OBJECT_TYPE;
        }

        // Resource
        if (element instanceof c_ResourceDefinitionDetails) {
            return PlanInformationManagementServiceInfo.RESOURCEDEFINITION_OBJECT_TYPE;
        }
        if (element instanceof ResourceUpdateDetails) {
            return PlanEditServiceInfo.RESOURCEUPDATE_OBJECT_TYPE;
        }

        // Function
        if (element instanceof FunctionDefinitionDetails) {
            return PlanInformationManagementServiceInfo.FUNCTIONDEFINITION_OBJECT_TYPE;
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
        relatedTypes.put(PlanDistributionServiceInfo.PLANIDENTITY_OBJECT_TYPE,
            PlanDistributionServiceInfo.PLANVERSION_OBJECT_TYPE);

        relatedTypes.put(PlanDistributionServiceInfo.PLANVERSION_OBJECT_TYPE, PlanDistributionServiceInfo.PLANUPDATE_OBJECT_TYPE);

        // RequestTemplateIdentity <=> RequestTemplate
        relatedTypes.put(PlanInformationManagementServiceInfo.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.REQUESTTEMPLATE_OBJECT_TYPE);

        // RequestIdentity <=> RequestVersion <=> RequestStatusUpdate
        relatedTypes.put(PlanningRequestServiceInfo.REQUESTIDENTITY_OBJECT_TYPE,
            PlanningRequestServiceInfo.REQUESTVERSION_OBJECT_TYPE);

        relatedTypes.put(PlanningRequestServiceInfo.REQUESTVERSION_OBJECT_TYPE,
            PlanningRequestServiceInfo.REQUESTSTATUSUPDATE_OBJECT_TYPE);

        // ActivityIdentity <=> ActivityDefinition <=> ActivityInstance <=> ActivityUpdate
        relatedTypes.put(PlanInformationManagementServiceInfo.ACTIVITYIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.ACTIVITYDEFINITION_OBJECT_TYPE);

        relatedTypes.put(PlanInformationManagementServiceInfo.ACTIVITYDEFINITION_OBJECT_TYPE,
            PlanEditServiceInfo.ACTIVITYINSTANCE_OBJECT_TYPE);

        relatedTypes.put(PlanEditServiceInfo.ACTIVITYINSTANCE_OBJECT_TYPE, PlanEditServiceInfo.ACTIVITYUPDATE_OBJECT_TYPE);

        // EventIdentity <=> EventDefinition <=> EventInstance <=> EventUpdate
        relatedTypes.put(PlanInformationManagementServiceInfo.EVENTIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.EVENTDEFINITION_OBJECT_TYPE);

        relatedTypes.put(PlanInformationManagementServiceInfo.EVENTDEFINITION_OBJECT_TYPE,
            PlanEditServiceInfo.EVENTINSTANCE_OBJECT_TYPE);

        relatedTypes.put(PlanEditServiceInfo.EVENTINSTANCE_OBJECT_TYPE, PlanEditServiceInfo.EVENTUPDATE_OBJECT_TYPE);

        // ResourceIdentity <=> ResourceDefinition <=> ResourceUpdate
        relatedTypes.put(PlanInformationManagementServiceInfo.RESOURCEIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.RESOURCEDEFINITION_OBJECT_TYPE);

        relatedTypes.put(PlanInformationManagementServiceInfo.RESOURCEDEFINITION_OBJECT_TYPE,
            PlanEditServiceInfo.RESOURCEUPDATE_OBJECT_TYPE);

        // FunctionIdentity <=> FunctionDefinition
        relatedTypes.put(PlanInformationManagementServiceInfo.FUNCTIONIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.FUNCTIONDEFINITION_OBJECT_TYPE);

        // Plan Identity
        relatedConfigurationTypes.put(PlanDistributionServiceInfo.PLANIDENTITY_OBJECT_TYPE,
            PlanDistributionServiceInfo.PLANIDENTITYTOPLANVERSION_OBJECT_TYPE);

        // Plan Version
        relatedConfigurationTypes.put(PlanDistributionServiceInfo.PLANVERSION_OBJECT_TYPE,
            PlanDistributionServiceInfo.PLANVERSIONTOPLANUPDATE_OBJECT_TYPE);

        // Request Template Identity
        relatedConfigurationTypes.put(PlanInformationManagementServiceInfo.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.REQUESTTEMPLATEIDENTITYTOREQUESTTEMPLATE_OBJECT_TYPE);

        // Request Identity
        relatedConfigurationTypes.put(PlanningRequestServiceInfo.REQUESTIDENTITY_OBJECT_TYPE,
            PlanningRequestServiceInfo.REQUESTIDENTITYTOREQUESTVERSION_OBJECT_TYPE);

        // Request Version
        relatedConfigurationTypes.put(PlanningRequestServiceInfo.REQUESTVERSION_OBJECT_TYPE,
            PlanningRequestServiceInfo.REQUESTVERSIONTOREQUESTSTATUSUPDATE_OBJECT_TYPE);

        // Activity Identity
        relatedConfigurationTypes.put(PlanInformationManagementServiceInfo.ACTIVITYIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.ACTIVITYIDENTITYTOACTIVITYDEFINITION_OBJECT_TYPE);

        // Activity Definition
        relatedConfigurationTypes.put(PlanInformationManagementServiceInfo.ACTIVITYDEFINITION_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.ACTIVITYDEFINITIONTOACTIVITYINSTANCE_OBJECT_TYPE);

        // Activity Instance
        relatedConfigurationTypes.put(PlanEditServiceInfo.ACTIVITYINSTANCE_OBJECT_TYPE,
            PlanEditServiceInfo.ACTIVITYINSTANCETOACTIVITYSTATUS_OBJECT_TYPE);

        // Event Identity
        relatedConfigurationTypes.put(PlanInformationManagementServiceInfo.EVENTIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.EVENTIDENTITYTOEVENTDEFINITION_OBJECT_TYPE);

        // Event Definition
        relatedConfigurationTypes.put(PlanInformationManagementServiceInfo.EVENTDEFINITION_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.EVENTDEFINITIONTOEVENTINSTANCE_OBJECT_TYPE);

        // Event Instance
        relatedConfigurationTypes.put(PlanEditServiceInfo.EVENTINSTANCE_OBJECT_TYPE,
            PlanEditServiceInfo.EVENTINSTANCETOEVENTSTATUS_OBJECT_TYPE);

        // Resource Identity
        relatedConfigurationTypes.put(PlanInformationManagementServiceInfo.RESOURCEIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.RESOURCEIDENTITYTORESOURCEDEFINITION_OBJECT_TYPE);

        // Resource Definition
        relatedConfigurationTypes.put(PlanInformationManagementServiceInfo.RESOURCEDEFINITION_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.RESOURCEIDENTITYTORESOURCEDEFINITION_OBJECT_TYPE // TODO
        );

        // Function Identity
        relatedConfigurationTypes.put(PlanInformationManagementServiceInfo.FUNCTIONIDENTITY_OBJECT_TYPE,
            PlanInformationManagementServiceInfo.FUNCTIONIDENTITYTOFUNCTIONDEFINITION_OBJECT_TYPE);
    }
}
