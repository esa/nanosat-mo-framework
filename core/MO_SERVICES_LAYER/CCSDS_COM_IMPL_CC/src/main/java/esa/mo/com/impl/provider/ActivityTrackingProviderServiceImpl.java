/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.util.HelperArchive;
import esa.mo.helpertools.connections.ConfigurationProvider;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.activitytracking.ActivityTrackingHelper;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityAcceptance;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityAcceptanceList;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecution;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityExecutionList;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityTransfer;
import org.ccsds.moims.mo.com.activitytracking.structures.ActivityTransferList;
import org.ccsds.moims.mo.com.activitytracking.structures.OperationActivity;
import org.ccsds.moims.mo.com.activitytracking.structures.OperationActivityList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.structures.ObjectId;
import org.ccsds.moims.mo.com.structures.ObjectKey;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 * @author Cesar Coelho
 */
public class ActivityTrackingProviderServiceImpl {

    private boolean initialiased = false;
    private boolean running = false;
    private boolean isRegistered = false;
    private ArchiveProviderServiceImpl archiveService;
    private EventProviderServiceImpl eventService;
    private final ConfigurationProvider configuration = new ConfigurationProvider();

    /**
     * Initializes the service
     *
     * @param archiveService Archive service provider
     * @param eventService Event service provider
     * @throws MALException On initialization error.
     */
    public synchronized void init(ArchiveProviderServiceImpl archiveService,
            EventProviderServiceImpl eventService) throws MALException {

        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ActivityTrackingHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
            }
        }

        this.archiveService = archiveService;
        this.eventService = eventService;

        running = true;
        initialiased = true;
        Logger.getLogger(ActivityTrackingProviderServiceImpl.class.getName()).info("Activity Tracking service READY");

    }

//------------------------------------------------------------------------------
// With MALInteraction object
//------------------------------------------------------------------------------
    public void publishExecutionEventSubmitAck(MALInteraction interaction, boolean success, ObjectId source)
            throws MALInteractionException, MALException {
        publishExecutionEventOperation(interaction, success, 1, 1, null, source);
    }

    public void publishExecutionEventRequestResponse(MALInteraction interaction, boolean success, ObjectId source)
            throws MALInteractionException, MALException {
        publishExecutionEventOperation(interaction, success, 1, 1, null, source);
    }

    public void publishExecutionEventInvokeAck(MALInteraction interaction, boolean success, ObjectId source) throws MALInteractionException, MALException {
        publishExecutionEventOperation(interaction, success, 1, 2, null, source);
    }

    public void publishExecutionEventInvokeResponse(MALInteraction interaction, boolean success, ObjectId source) throws MALInteractionException, MALException {
        publishExecutionEventOperation(interaction, success, 2, 2, null, source);
    }

    public void publishReleaseEvent(MALInteraction interaction, boolean success, Duration nextDuration, URI nextDestination, ObjectId source) throws MALInteractionException, MALException {
        this.publishTransferEventOperation(interaction, ActivityTrackingHelper.RELEASE_OBJECT_TYPE, success, nextDuration, nextDestination, source);
    }

    public void publishReceptionEvent(MALInteraction interaction, boolean success, Duration nextDuration, URI nextDestination, ObjectId source) throws MALInteractionException, MALException {
        this.publishTransferEventOperation(interaction, ActivityTrackingHelper.RECEPTION_OBJECT_TYPE, success, nextDuration, nextDestination, source);
    }

    public void publishForwardEvent(MALInteraction interaction, boolean success, Duration nextDuration, URI nextDestination, ObjectId source) throws MALInteractionException, MALException {
        this.publishTransferEventOperation(interaction, ActivityTrackingHelper.FORWARD_OBJECT_TYPE, success, nextDuration, nextDestination, source);
    }
//------------------------------------------------------------------------------

//------------------------------------------------------------------------------
// With uri and network fields directly
//------------------------------------------------------------------------------
    public void publishExecutionEventSubmitAck(final URI uri, final Identifier network, boolean success, ObjectId source)
            throws MALInteractionException, MALException {
        publishExecutionEventOperation(uri, network, success, 1, 1, null, source);
    }

    public void publishExecutionEventRequestResponse(final URI uri, final Identifier network, boolean success, ObjectId source)
            throws MALInteractionException, MALException {
        publishExecutionEventOperation(uri, network, success, 1, 1, null, source);
    }

    public void publishExecutionEventInvokeAck(final URI uri, final Identifier network, boolean success, ObjectId source) throws MALInteractionException, MALException {
        publishExecutionEventOperation(uri, network, success, 1, 2, null, source);
    }

    public void publishExecutionEventInvokeResponse(final URI uri, final Identifier network, boolean success, ObjectId source) throws MALInteractionException, MALException {
        publishExecutionEventOperation(uri, network, success, 2, 2, null, source);
    }

    public void publishReleaseEvent(final URI uri, final Identifier network, boolean success, Duration nextDuration, URI nextDestination, ObjectId source) throws MALInteractionException, MALException {
        this.publishTransferEventOperation(uri, network, ActivityTrackingHelper.RELEASE_OBJECT_TYPE, success, nextDuration, nextDestination, source);
    }

    public void publishReceptionEvent(final URI uri, final Identifier network, boolean success, Duration nextDuration, URI nextDestination, ObjectId source) throws MALInteractionException, MALException {
        this.publishTransferEventOperation(uri, network, ActivityTrackingHelper.RECEPTION_OBJECT_TYPE, success, nextDuration, nextDestination, source);
    }

    public void publishForwardEvent(final URI uri, final Identifier network, boolean success, Duration nextDuration, URI nextDestination, ObjectId source) throws MALInteractionException, MALException {
        this.publishTransferEventOperation(uri, network, ActivityTrackingHelper.FORWARD_OBJECT_TYPE, success, nextDuration, nextDestination, source);
    }

//------------------------------------------------------------------------------
    public ObjectId publishExecutionEventOperation(MALInteraction interaction, boolean success,
            int currentStageCount, int totalStageCount, final Long related, ObjectId source) throws MALInteractionException, MALException {
        return this.publishExecutionEventOperation(interaction, null, null, success, currentStageCount, totalStageCount, related, source);
    }

    public ObjectId publishExecutionEventOperation(final URI uri, final Identifier network, boolean success,
            int currentStageCount, int totalStageCount, final Long related, ObjectId source) throws MALInteractionException, MALException {
        return this.publishExecutionEventOperation(null, uri, network, success, currentStageCount, totalStageCount, related, source);
    }

    private ObjectId publishExecutionEventOperation(MALInteraction interaction, final URI uri, final Identifier network,
            boolean success, int currentStageCount, int totalStageCount, final Long related, ObjectId source) throws MALInteractionException, MALException {

        // Produce ActivityExecutionList
        ActivityExecutionList ael = new ActivityExecutionList();
        ActivityExecution activityExecutionInstance = new ActivityExecution();
        activityExecutionInstance.setExecutionStage(new UInteger(currentStageCount)); // TBD
        activityExecutionInstance.setStageCount(new UInteger(totalStageCount));
        activityExecutionInstance.setSuccess(success);
        ael.add(activityExecutionInstance);
        final Long objId;

        if (interaction != null) {
            objId = eventService.generateAndStoreEvent(ActivityTrackingHelper.EXECUTION_OBJECT_TYPE, configuration.getDomain(), ael, related, source, interaction);
            eventService.publishEvent(interaction, objId, ActivityTrackingHelper.EXECUTION_OBJECT_TYPE, related, source, ael);
        } else {
            objId = eventService.generateAndStoreEvent(ActivityTrackingHelper.EXECUTION_OBJECT_TYPE, configuration.getDomain(), ael, related, source, uri, network);
            eventService.publishEvent(uri, objId, ActivityTrackingHelper.EXECUTION_OBJECT_TYPE, related, source, ael);
        }

        final ObjectKey key = new ObjectKey(configuration.getDomain(), objId);

        return new ObjectId(ActivityTrackingHelper.EXECUTION_OBJECT_TYPE, key);
    }

    public Long publishTransferEventOperation(MALInteraction interaction,
            final ObjectType objType, final boolean success, final Duration duration,
            final URI nextDestination, final ObjectId source) throws MALInteractionException, MALException {
        return this.publishTransferEventOperation(interaction, null, null, objType, success, duration, nextDestination, source);
    }

    public Long publishTransferEventOperation(final URI uri, final Identifier network,
            final ObjectType objType, final boolean success, final Duration duration,
            final URI nextDestination, final ObjectId source) throws MALInteractionException, MALException {
        return this.publishTransferEventOperation(null, uri, network, objType, success, duration, nextDestination, source);
    }

    private Long publishTransferEventOperation(MALInteraction interaction, final URI uri, final Identifier network,
            final ObjectType objType, final boolean success, final Duration duration,
            final URI nextDestination, final ObjectId source) throws MALInteractionException, MALException {

        // Produce ActivityTransferList
        ActivityTransferList atl = new ActivityTransferList();
        ActivityTransfer at = new ActivityTransfer();
        at.setSuccess(success);
        at.setEstimateDuration(duration);
        at.setNextDestination(nextDestination);
        atl.add(at);
        Long objId;

        if (interaction != null) {
            objId = eventService.generateAndStoreEvent(objType, configuration.getDomain(), atl, null, source, interaction);
            eventService.publishEvent(interaction, objId, objType, null, source, atl);
        } else {
            objId = eventService.generateAndStoreEvent(objType, configuration.getDomain(), atl, null, source, uri, network);
            eventService.publishEvent(uri, objId, objType, null, source, atl);
        }

        return objId;
    }

    public Long publishAcceptanceEventOperation(MALInteraction interaction, boolean success, final Long related, final ObjectId source)
            throws MALInteractionException, MALException {
        return this.publishAcceptanceEventOperation(interaction, null, null, success, related, source);
    }

    public Long publishAcceptanceEventOperation(final URI uri, final Identifier network, boolean success, final Long related, final ObjectId source)
            throws MALInteractionException, MALException {
        return this.publishAcceptanceEventOperation(null, uri, network, success, related, source);
    }

    public Long publishAcceptanceEventOperation(MALInteraction interaction, final URI uri, final Identifier network,
            boolean success, final Long related, final ObjectId source) throws MALInteractionException, MALException {

        // Produce ActivityAcceptanceList
        ActivityAcceptanceList aal = new ActivityAcceptanceList();
        ActivityAcceptance aa = new ActivityAcceptance();
        aa.setSuccess(success);
        aal.add(aa);
        Long objId;

        if (interaction != null) {
            objId = eventService.generateAndStoreEvent(ActivityTrackingHelper.ACCEPTANCE_OBJECT_TYPE, configuration.getDomain(), aal, related, source, interaction);
            eventService.publishEvent(interaction, objId, ActivityTrackingHelper.ACCEPTANCE_OBJECT_TYPE, null, source, aal);
        } else {
            objId = eventService.generateAndStoreEvent(ActivityTrackingHelper.ACCEPTANCE_OBJECT_TYPE, configuration.getDomain(), aal, related, source, uri, network);
            eventService.publishEvent(uri, objId, ActivityTrackingHelper.ACCEPTANCE_OBJECT_TYPE, null, source, aal);
        }

        return objId;
    }

    /**
     * Generates and stores the COM Operation Activity object in the Archive
     *
     * @param interaction The MALInteraction object for the operation
     * @param source The source link of the Operation Activity object. Can be
     * null
     * @return The link to the stored COM Operation Activity. Null if not
     * stored.
     */
    public ObjectId storeCOMOperationActivity(final MALInteraction interaction, final ObjectId source) {

        if (interaction == null || this.archiveService == null) {
            return null;
        }

        OperationActivityList opActivityList = new OperationActivityList();
        opActivityList.add(new OperationActivity(interaction.getMessageHeader().getInteractionType()));

        // requirement: 3.5.2.3
        ArchiveDetailsList archiveDetails = HelperArchive.generateArchiveDetailsList(null, source, interaction);
        archiveDetails.get(0).setInstId(interaction.getMessageHeader().getTransactionId()); // requirement: 3.5.2.4
        archiveDetails.get(0).setNetwork(interaction.getMessageHeader().getNetworkZone());  // RID raised to create this requirement!
        archiveDetails.get(0).setProvider(interaction.getMessageHeader().getURIFrom());     // RID raised to create this requirement!

        try {
            LongList objIds = this.archiveService.store(
                    true,
                    ActivityTrackingHelper.OPERATIONACTIVITY_OBJECT_TYPE,
                    interaction.getMessageHeader().getDomain(),
                    archiveDetails,
                    opActivityList,
                    interaction); // requirement: 3.5.2.3 & 3.5.2.5

            if (objIds.size() == 1) {
                ObjectKey key = new ObjectKey(interaction.getMessageHeader().getDomain(), objIds.get(0));
                return new ObjectId(ActivityTrackingHelper.OPERATIONACTIVITY_OBJECT_TYPE, key);
            }

        } catch (MALException ex) {
            Logger.getLogger(ActivityTrackingProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MALInteractionException ex) {
            Logger.getLogger(ActivityTrackingProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

}
