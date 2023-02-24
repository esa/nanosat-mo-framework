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
package esa.mo.mp.impl.provider;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.structures.ObjectIdList;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mp.MPHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.PlanInformationManagementHelper;
import org.ccsds.moims.mo.mp.planinformationmanagement.provider.PlanInformationManagementInheritanceSkeleton;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.ActivityDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.EventDefinitionDetailsList;
import org.ccsds.moims.mo.mp.structures.ObjectIdPairList;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePair;
import org.ccsds.moims.mo.mp.structures.ObjectInstancePairList;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetails;
import org.ccsds.moims.mo.mp.structures.RequestTemplateDetailsList;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetails;
import org.ccsds.moims.mo.mp.structures.c_ResourceDefinitionDetailsList;
import esa.mo.com.impl.util.COMServicesProvider;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.mp.impl.api.MPArchiveManager;
import esa.mo.mp.impl.callback.MPServiceOperation;
import esa.mo.mp.impl.callback.MPServiceOperationArguments;
import esa.mo.mp.impl.callback.MPServiceOperationHelper;
import esa.mo.mp.impl.callback.MPServiceOperationManager;
import esa.mo.mp.impl.com.COMObjectIdHelper;
import esa.mo.mp.impl.com.pattern.COMInstanceArchiveManager;
import esa.mo.mp.impl.com.pattern.COMStateArchiveManager;
import esa.mo.mp.impl.com.pattern.COMStaticItemArchiveManager;

/**
 * Plan Information Management (PIM) Service provider implementation
 * Overridden methods contain the operation implementation.
 */
public class PlanInformationManagementProviderServiceImpl extends PlanInformationManagementInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(PlanInformationManagementProviderServiceImpl.class.getName());

    private MALProvider provider;
    private boolean initialised = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private MPServiceOperationManager operationCallbackManager;

    private MPArchiveManager archiveManager;

    /**
     * creates the MAL objects
     *
     * @param comServices
     * @throws MALException On initialisation error.
     */
    public synchronized void init(COMServicesProvider comServices, MPArchiveManager archiveManager, MPServiceOperationManager registration) throws MALException {
        long timestamp = System.currentTimeMillis();
        
        if (!this.initialised) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(MPHelper.MP_AREA_NAME, MPHelper.MP_AREA_VERSION) == null) {
                MPHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                PlanInformationManagementHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
                // nothing to be done..
            }
        }

        // Shut down old service transport
        if (this.provider != null) {
            this.connection.closeAll();
        }

        this.provider = this.connection.startService(
            PlanInformationManagementHelper.PLANINFORMATIONMANAGEMENT_SERVICE_NAME.toString(),
            PlanInformationManagementHelper.PLANINFORMATIONMANAGEMENT_SERVICE, false, this);

        this.archiveManager = archiveManager;
        this.operationCallbackManager = registration;

        this.initialised = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("Plan Information Management service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (this.provider != null) {
                this.provider.close();
            }

            this.connection.closeAll();
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    public ConnectionProvider getConnectionProvider() {
        return this.connection;
    }

    @Override
    public ObjectInstancePairList addRequestDef(IdentifierList identities, RequestTemplateDetailsList definitions,
        MALInteraction interaction) throws MALInteractionException, MALException {
        return this.addDef(MPServiceOperation.ADD_REQUEST_DEF, this.archiveManager.REQUEST_TEMPLATE, identities,
            definitions, interaction);
    }

    @Override
    public LongList updateRequestDef(LongList identityIds, RequestTemplateDetailsList definitions,
        MALInteraction interaction) throws MALInteractionException, MALException {
        return this.updateDef(MPServiceOperation.UPDATE_REQUEST_DEF, this.archiveManager.REQUEST_TEMPLATE, identityIds,
            PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE, definitions, interaction);
    }

    @Override
    public ObjectInstancePairList listRequestDefs(IdentifierList identities, MALInteraction interaction)
        throws MALInteractionException, MALException {
        return this.listDefs(this.archiveManager.REQUEST_TEMPLATE, identities,
            PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE, interaction);
    }

    @Override
    public RequestTemplateDetailsList getRequestDef(LongList identityIds, MALInteraction interaction)
        throws MALInteractionException, MALException {
        return (RequestTemplateDetailsList) this.getDef(this.archiveManager.REQUEST_TEMPLATE, identityIds,
            PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE, interaction,
            new RequestTemplateDetails());
    }

    @Override
    public void removeRequestDef(LongList identityIds, MALInteraction interaction) throws MALInteractionException,
        MALException {
        this.removeDef(MPServiceOperation.REMOVE_REQUEST_DEF, this.archiveManager.REQUEST_TEMPLATE, identityIds,
            PlanInformationManagementHelper.REQUESTTEMPLATEIDENTITY_OBJECT_TYPE, interaction);
    }

    @Override
    public ObjectInstancePairList addActivityDef(IdentifierList identities, ActivityDefinitionDetailsList definitions,
        MALInteraction interaction) throws MALInteractionException, MALException {
        return this.addDef(MPServiceOperation.ADD_ACTIVITY_DEF, this.archiveManager.ACTIVITY, identities, definitions,
            interaction);
    }

    @Override
    public LongList updateActivityDef(LongList identityIds, ActivityDefinitionDetailsList definitions,
        MALInteraction interaction) throws MALInteractionException, MALException {
        return this.updateDef(MPServiceOperation.UPDATE_ACTIVITY_DEF, this.archiveManager.ACTIVITY, identityIds,
            PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE, definitions, interaction);
    }

    @Override
    public ObjectInstancePairList listActivityDefs(IdentifierList identities, MALInteraction interaction)
        throws MALInteractionException, MALException {
        return this.listDefs(this.archiveManager.ACTIVITY, identities,
            PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE, interaction);
    }

    @Override
    public ActivityDefinitionDetailsList getActivityDef(LongList identityIds, MALInteraction interaction)
        throws MALInteractionException, MALException {
        return (ActivityDefinitionDetailsList) this.getDef(this.archiveManager.ACTIVITY, identityIds,
            PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE, interaction, new ActivityDefinitionDetails());
    }

    @Override
    public void removeActivityDef(LongList identityIds, MALInteraction interaction) throws MALInteractionException,
        MALException {
        this.removeDef(MPServiceOperation.REMOVE_ACTIVITY_DEF, this.archiveManager.ACTIVITY, identityIds,
            PlanInformationManagementHelper.ACTIVITYIDENTITY_OBJECT_TYPE, interaction);
    }

    @Override
    public ObjectInstancePairList addEventDef(IdentifierList identities, EventDefinitionDetailsList definitions,
        MALInteraction interaction) throws MALInteractionException, MALException {
        return this.addDef(MPServiceOperation.ADD_EVENT_DEF, this.archiveManager.EVENT, identities, definitions,
            interaction);
    }

    @Override
    public LongList updateEventDef(LongList identityIds, EventDefinitionDetailsList definitions,
        MALInteraction interaction) throws MALInteractionException, MALException {
        return this.updateDef(MPServiceOperation.UPDATE_EVENT_DEF, this.archiveManager.EVENT, identityIds,
            PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE, definitions, interaction);
    }

    @Override
    public ObjectInstancePairList listEventDefs(IdentifierList identities, MALInteraction interaction)
        throws MALInteractionException, MALException {
        return this.listDefs(this.archiveManager.EVENT, identities,
            PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE, interaction);
    }

    @Override
    public EventDefinitionDetailsList getEventDef(LongList identityIds, MALInteraction interaction)
        throws MALInteractionException, MALException {
        return (EventDefinitionDetailsList) this.getDef(this.archiveManager.EVENT, identityIds,
            PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE, interaction, new EventDefinitionDetails());
    }

    @Override
    public void removeEventDef(LongList identityIds, MALInteraction interaction) throws MALInteractionException,
        MALException {
        this.removeDef(MPServiceOperation.REMOVE_EVENT_DEF, this.archiveManager.EVENT, identityIds,
            PlanInformationManagementHelper.EVENTIDENTITY_OBJECT_TYPE, interaction);
    }

    @Override
    public ObjectInstancePairList addResourceDef(IdentifierList identities, c_ResourceDefinitionDetailsList definitions,
        MALInteraction interaction) throws MALInteractionException, MALException {
        return this.addDef(MPServiceOperation.ADD_RESOURCE_DEF, this.archiveManager.RESOURCE, identities, definitions,
            interaction);
    }

    @Override
    public LongList updateResourceDef(LongList identityIds, c_ResourceDefinitionDetailsList definitions,
        MALInteraction interaction) throws MALInteractionException, MALException {
        return this.updateDef(MPServiceOperation.UPDATE_RESOURCE_DEF, this.archiveManager.RESOURCE, identityIds,
            PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE, definitions, interaction);
    }

    @Override
    public ObjectInstancePairList listResourceDefs(IdentifierList identities, MALInteraction interaction)
        throws MALInteractionException, MALException {
        return this.listDefs(this.archiveManager.RESOURCE, identities,
            PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE, interaction);
    }

    @Override
    public c_ResourceDefinitionDetailsList getResourceDef(LongList identityIds, MALInteraction interaction)
        throws MALInteractionException, MALException {
        return (c_ResourceDefinitionDetailsList) this.getDef(this.archiveManager.RESOURCE, identityIds,
            PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE, interaction,
            new c_ResourceDefinitionDetails());
    }

    @Override
    public void removeResourceDef(LongList identityIds, MALInteraction interaction) throws MALInteractionException,
        MALException {
        this.removeDef(MPServiceOperation.REMOVE_RESOURCE_DEF, this.archiveManager.RESOURCE, identityIds,
            PlanInformationManagementHelper.RESOURCEIDENTITY_OBJECT_TYPE, interaction);
    }

    private ObjectInstancePairList addDef(MPServiceOperation operation, COMStaticItemArchiveManager archive,
        IdentifierList identities, ElementList definitions, MALInteraction interaction) throws MALInteractionException,
        MALException {
        ObjectIdPairList pairs = archive.addDefinitions(identities, definitions, null, interaction);
        return this.addDefResponse(operation, pairs, interaction);
    }

    private ObjectInstancePairList addDef(MPServiceOperation operation, COMStateArchiveManager archive,
        IdentifierList identities, ElementList definitions, MALInteraction interaction) throws MALInteractionException,
        MALException {
        ObjectIdPairList pairs = archive.addDefinitions(identities, definitions, null, interaction);
        return this.addDefResponse(operation, pairs, interaction);
    }

    private ObjectInstancePairList addDef(MPServiceOperation operation, COMInstanceArchiveManager archive,
        IdentifierList identities, ElementList definitions, MALInteraction interaction) throws MALInteractionException,
        MALException {
        ObjectIdPairList pairs = archive.addDefinitions(identities, definitions, null, interaction);
        return this.addDefResponse(operation, pairs, interaction);
    }

    private ObjectInstancePairList addDefResponse(MPServiceOperation operation, ObjectIdPairList pairs,
        MALInteraction interaction) throws MALInteractionException, MALException {
        // Operation callback
        List<MPServiceOperationArguments> arguments = MPServiceOperationHelper.asArgumentsList(pairs, null,
            interaction);
        this.operationCallbackManager.notify(operation, arguments);

        ObjectInstancePairList response = COMObjectIdHelper.getInstancePairIds(pairs);
        return response;
    }

    private LongList updateDef(MPServiceOperation operation, COMStaticItemArchiveManager archive,
        LongList identityInstanceIds, ObjectType identityType, ElementList definitions, MALInteraction interaction)
        throws MALInteractionException, MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        ObjectIdList objectIdList = archive.updateDefinitions(identityIds, definitions, null, interaction);
        return this.updateDefResponse(operation, identityIds, objectIdList, interaction);
    }

    private LongList updateDef(MPServiceOperation operation, COMStateArchiveManager archive,
        LongList identityInstanceIds, ObjectType identityType, ElementList definitions, MALInteraction interaction)
        throws MALInteractionException, MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        ObjectIdList objectIdList = archive.updateDefinitions(identityIds, definitions, null, interaction);
        return this.updateDefResponse(operation, identityIds, objectIdList, interaction);
    }

    private LongList updateDef(MPServiceOperation operation, COMInstanceArchiveManager archive,
        LongList identityInstanceIds, ObjectType identityType, ElementList definitions, MALInteraction interaction)
        throws MALInteractionException, MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        ObjectIdList objectIdList = archive.updateDefinitions(identityIds, definitions, null, interaction);
        return this.updateDefResponse(operation, identityIds, objectIdList, interaction);
    }

    private LongList updateDefResponse(MPServiceOperation operation, ObjectIdList identityIds,
        ObjectIdList objectIdList, MALInteraction interaction) throws MALInteractionException, MALException {
        // Operation callback
        List<MPServiceOperationArguments> arguments = MPServiceOperationHelper.asArgumentsList(identityIds,
            objectIdList, null, null, interaction, null);
        this.operationCallbackManager.notify(operation, arguments);

        LongList response = COMObjectIdHelper.getInstanceIds(objectIdList);
        return response;
    }

    private ObjectInstancePairList listDefs(COMStaticItemArchiveManager archive, IdentifierList identities,
        ObjectType identityType, MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectIdList identityIds = archive.getIdentityIds(identities, identityType);
        ObjectIdList definitionIds = archive.getDefinitionIdsByIdentityIds(identityIds);
        return this.listDefsResponse(identityIds, definitionIds, interaction);
    }

    private ObjectInstancePairList listDefs(COMStateArchiveManager archive, IdentifierList identities,
        ObjectType identityType, MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectIdList identityIds = archive.getIdentityIds(identities, identityType);
        ObjectIdList definitionIds = archive.getDefinitionIdsByIdentityIds(identityIds);
        return this.listDefsResponse(identityIds, definitionIds, interaction);
    }

    private ObjectInstancePairList listDefs(COMInstanceArchiveManager archive, IdentifierList identities,
        ObjectType identityType, MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectIdList identityIds = archive.getIdentityIds(identities, identityType);
        ObjectIdList definitionIds = archive.getDefinitionIdsByIdentityIds(identityIds);
        return this.listDefsResponse(identityIds, definitionIds, interaction);
    }

    private ObjectInstancePairList listDefsResponse(ObjectIdList identityIds, ObjectIdList definitionIds,
        MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectInstancePairList response = new ObjectInstancePairList();
        for (int index = 0; index < identityIds.size(); index++) {
            Long identityId = COMObjectIdHelper.getInstanceId(identityIds.get(index));
            Long definitionId = COMObjectIdHelper.getInstanceId(definitionIds.get(index));
            ObjectInstancePair pair = new ObjectInstancePair(identityId, definitionId);
            response.add(pair);
        }

        return response;
    }

    private ElementList getDef(COMStaticItemArchiveManager archive, LongList identityInstanceIds,
        ObjectType identityType, MALInteraction interaction, Element element) throws MALInteractionException,
        MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        ElementList definitions = archive.getDefinitionsByIdentityIds(identityIds);
        return definitions;
    }

    private ElementList getDef(COMStateArchiveManager archive, LongList identityInstanceIds, ObjectType identityType,
        MALInteraction interaction, Element element) throws MALInteractionException, MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        ElementList definitions = archive.getDefinitionsByIdentityIds(identityIds);
        return definitions;
    }

    private ElementList getDef(COMInstanceArchiveManager archive, LongList identityInstanceIds, ObjectType identityType,
        MALInteraction interaction, Element element) throws MALInteractionException, MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        ElementList definitions = archive.getDefinitionsByIdentityIds(identityIds);
        return definitions;
    }

    private void removeDef(MPServiceOperation operation, COMStaticItemArchiveManager archive,
        LongList identityInstanceIds, ObjectType identityType, MALInteraction interaction)
        throws MALInteractionException, MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        archive.removeDefinitions(identityIds, interaction);
        this.removeDefResponse(operation, identityIds, interaction);
    }

    private void removeDef(MPServiceOperation operation, COMStateArchiveManager archive, LongList identityInstanceIds,
        ObjectType identityType, MALInteraction interaction) throws MALInteractionException, MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        archive.removeDefinitions(identityIds, interaction);
        this.removeDefResponse(operation, identityIds, interaction);
    }

    private void removeDef(MPServiceOperation operation, COMInstanceArchiveManager archive,
        LongList identityInstanceIds, ObjectType identityType, MALInteraction interaction)
        throws MALInteractionException, MALException {
        ObjectIdList identityIds = COMObjectIdHelper.getObjectIds(identityInstanceIds, identityType);
        archive.removeDefinitions(identityIds, interaction);
        this.removeDefResponse(operation, identityIds, interaction);
    }

    private void removeDefResponse(MPServiceOperation operation, ObjectIdList identityIds, MALInteraction interaction)
        throws MALInteractionException, MALException {
        // Operation callback
        List<MPServiceOperationArguments> arguments = MPServiceOperationHelper.asArgumentsList(identityIds, null, null,
            null, interaction, null);
        this.operationCallbackManager.notify(operation, arguments);
    }
}
