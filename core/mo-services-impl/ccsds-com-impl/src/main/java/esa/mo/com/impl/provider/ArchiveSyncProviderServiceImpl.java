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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.archive.entities.COMObjectEntity;
import esa.mo.com.impl.consumer.ArchiveConsumerServiceImpl;
import esa.mo.com.impl.sync.Dictionary;
import esa.mo.com.impl.sync.EncodeDecode;
import esa.mo.com.impl.sync.ToDelete;
import esa.mo.com.impl.util.Quota;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.connections.SingleConnectionDetails;
import esa.mo.helpertools.helpers.HelperTime;
import esa.mo.helpertools.misc.Const;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.PaginationFilter;
import org.ccsds.moims.mo.com.archivesync.ArchiveSyncHelper;
import org.ccsds.moims.mo.com.archivesync.body.GetTimeResponse;
import org.ccsds.moims.mo.com.archivesync.provider.ArchiveSyncInheritanceSkeleton;
import org.ccsds.moims.mo.com.archivesync.provider.RetrieveRangeAgainInteraction;
import org.ccsds.moims.mo.com.archivesync.provider.RetrieveRangeInteraction;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.*;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.*;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Archive Sync service Provider.
 */
public class ArchiveSyncProviderServiceImpl extends ArchiveSyncInheritanceSkeleton {

    private static final Logger LOGGER = Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName());

    private static final long DISPATCHERS_CLEANUP_INTERVAL_IN_MILISECONDS = 600000L; //10 minutes

    private static long timerCounter = 0;

    private final ConnectionProvider connection = new ConnectionProvider();

    private final AtomicLong lastSync = new AtomicLong(0);

    private final Dictionary dictionary = new Dictionary();

    private final Map<Long, Dispatcher> dispatchers = Collections.synchronizedMap(new HashMap<>());

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private final Map<Long, TimerTask> timerTasks = Collections.synchronizedMap(new HashMap<>());

    private final Map<Long, Long> syncTimes = Collections.synchronizedMap(new HashMap<>());

    private final String timerName;

    private ArchiveManager manager;

    private MALProvider archiveSyncServiceProvider;

    private boolean initialiased = false;

    private ArchiveConsumerServiceImpl archive;

    private FineTime latestSync;

    private Quota stdQuota;

    private Timer dispatchersCleanupTimer;

    private int objectsLimit;

    public ArchiveSyncProviderServiceImpl(SingleConnectionDetails connectionToArchiveService) {
        this(connectionToArchiveService, null, null);
    }

    public ArchiveSyncProviderServiceImpl(SingleConnectionDetails connectionToArchiveService,
            Blob authenticationId, String localNamePrefix) {
        this.latestSync = new FineTime(0);

        try {
            this.archive
                    = new ArchiveConsumerServiceImpl(connectionToArchiveService, authenticationId, localNamePrefix);
        } catch (MALException | MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, "", ex);
        }

        timerName = getTimerName();
        dispatchersCleanupTimer = new Timer(timerName);
        LOGGER.log(Level.FINE, "Dispatchers cleanup timer created " + timerName);

        try {
            objectsLimit = Integer.parseInt(System.getProperty(Const.ARCHIVESYNC_OBJECTS_LIMIT_PROPERTY, Const.ARCHIVESYNC_OBJECTS_LIMIT_DEFAULT));
            LOGGER.log(Level.FINE, "The object limits is: " + objectsLimit);

            if (objectsLimit >= 90000) {
                String msg = "Using a large objects limit may cause the archive sync to fail due to too long data transfer. "
                        + "Consider changing the limit to a smaller amount";
                LOGGER.log(Level.WARNING, msg);
            }
        } catch (NumberFormatException ex) {
            objectsLimit = 30000;
            String msg = "Error when parsing " + Const.ARCHIVESYNC_OBJECTS_LIMIT_PROPERTY
                    + " property. Using the default value of 30000";
            LOGGER.log(Level.WARNING, msg);
        }
    }

    private static synchronized String getTimerName() {
        return "DispachersCleanupTimer_" + timerCounter++;
    }

    /**
     * Set the quota. Should only be used when STD limits are used for the
     * archive.
     *
     * @param quota The same Quota object that is passed to the
     * AppsLauncherProviderServiceImpl using its setStdPerApp method.
     */
    public void setStdQuota(Quota quota) {
        this.stdQuota = quota;
    }

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param manager the Archive Manager
     * @throws MALException if initialization error.
     */
    public synchronized void init(ArchiveManager manager) throws MALException {
        long timestamp = System.currentTimeMillis();
        if (!initialiased) {
            if (null == MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION)) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (null == MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION)) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION)
                    .getServiceByName(ArchiveSyncHelper.ARCHIVESYNC_SERVICE_NAME) == null) {
                ArchiveSyncHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
        }

        this.manager = manager;

        // shut down old service transport
        if (null != archiveSyncServiceProvider) {
            connection.closeAll();
        }

        archiveSyncServiceProvider = connection.startService(ArchiveSyncHelper.ARCHIVESYNC_SERVICE_NAME.toString(),
                ArchiveSyncHelper.ARCHIVESYNC_SERVICE, false, this);
        initialiased = true;
        timestamp = System.currentTimeMillis() - timestamp;
        LOGGER.info("ArchiveSync service: READY! (" + timestamp + " ms)");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            dispatchersCleanupTimer.cancel();
            dispatchersCleanupTimer = new Timer(timerName);

            final String msg = "Dispatchers cleanup timer re-created " + timerName;
            LOGGER.log(Level.FINE, msg);

            if (null != archiveSyncServiceProvider) {
                archiveSyncServiceProvider.close();
            }

            manager.close();
            connection.closeAll();
        } catch (MALException ex) {
            LOGGER.log(Level.WARNING, "Exception during close down of the provider!", ex);
        }
    }

    @Override
    public GetTimeResponse getTime(final MALInteraction interaction) throws MALInteractionException, MALException {
        final FineTime currentTime = HelperTime.getTimestamp();
        final FineTime lastSyncTime = new FineTime(lastSync.get());
        return new GetTimeResponse(currentTime, lastSyncTime);
    }

    @Override
    public void retrieveRange(FineTime from, FineTime until, ObjectTypeList objectTypes, Identifier compression,
            RetrieveRangeInteraction interaction) throws MALInteractionException, MALException {
        final Dispatcher dispatcher = new Dispatcher(interaction, archive);
        long interactionTicket = interaction.getInteraction().getMessageHeader().getTransactionId();
        dispatchers.put(interactionTicket, dispatcher);
        final TimerTask timerTask = new CleaningTimerTask(interactionTicket);
        timerTasks.put(interactionTicket, timerTask);
        dispatchersCleanupTimer.schedule(timerTask, DISPATCHERS_CLEANUP_INTERVAL_IN_MILISECONDS);

        LOGGER.log(Level.FINE, "Dispatcher cleaning task created and scheduled in timer for transaction"
                + interactionTicket + " , it will be triggered in "
                + DISPATCHERS_CLEANUP_INTERVAL_IN_MILISECONDS / 1000 + " seconds.");

        interaction.sendAcknowledgement(interactionTicket);

        ArchiveQuery archiveQuery = new ArchiveQuery();
        archiveQuery.setStartTime(from);
        archiveQuery.setEndTime(until);
        archiveQuery.setDomain(null);
        archiveQuery.setNetwork(null);
        archiveQuery.setProvider(null);
        archiveQuery.setRelated(0L);
        archiveQuery.setSource(null);
        archiveQuery.setSortFieldName(null);
        archiveQuery.setSortOrder(true);

        PaginationFilter filter = new PaginationFilter();
        filter.setLimit(new UInteger(objectsLimit));
        filter.setOffset(new UInteger(0));

        ArrayList<COMObjectEntity> perObjs = manager.queryCOMObjectEntity(objectTypes, archiveQuery, filter);
        latestSync = perObjs.isEmpty() ? latestSync : perObjs.get(perObjs.size() - 1).getTimestamp();

        dispatcher.addObjects(perObjs);
        LOGGER.log(Level.FINE, "Stage 1: " + perObjs.size() +
            " objects were queried and are now being sent back to the consumer!");

        syncTimes.put(interactionTicket, latestSync.getValue());
        executor.execute(dispatcher::flushData);
    }

    @Override
    public void retrieveRangeAgain(final Long transactionTicket, final UIntegerList missingIndexes,
            final RetrieveRangeAgainInteraction interaction)
            throws MALInteractionException, MALException {
        final Dispatcher dispatcher = dispatchers.get(transactionTicket);

        if (null == dispatcher) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        TimerTask timerTask = timerTasks.get(transactionTicket);

        if (null == timerTask) {
            LOGGER.log(Level.WARNING, "Dispatcher cleaning timer task not found for "
                    + "transaction " + transactionTicket + " ! Trying to continue...");
        } else {
            cleanTimerTask(transactionTicket, timerTask);
        }

        timerTask = new CleaningTimerTask(transactionTicket);
        timerTasks.put(transactionTicket, timerTask);
        dispatchersCleanupTimer.schedule(timerTask, DISPATCHERS_CLEANUP_INTERVAL_IN_MILISECONDS);

        LOGGER.log(Level.FINE, "Dispatcher cleaning task re-created and scheduled in "
                + "timer for transaction " + transactionTicket + ", it will be triggered in "
                + DISPATCHERS_CLEANUP_INTERVAL_IN_MILISECONDS / 1000 + " seconds.");

        interaction.sendAcknowledgement();

        if (missingIndexes.size() == 2 && missingIndexes.get(1).getValue() == 0) {
            // Special case! The condition means that we need to retransmit
            // everything since the value in missingIndexes.get(0)
            UInteger lastIndex = missingIndexes.get(0);
            try {
                int numberOfChunks = dispatcher.numberOfChunks();

                for (int i = (int) lastIndex.getValue(); i < numberOfChunks; i++) {
                    byte[] chunk = dispatcher.getFlushedChunk(i);
                    interaction.sendUpdate(new Blob(chunk), new UInteger(i));
                }
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "", ex);
            }
        } else {
            for (UInteger missingIndex : missingIndexes) {
                byte[] chunk = dispatcher.getFlushedChunk((short) missingIndex.getValue());
                interaction.sendUpdate(new Blob(chunk), missingIndex);
            }
        }

        interaction.sendResponse();
    }

    private void cleanTimerTask(Long transactionTicket, TimerTask timerTask) {
        timerTask.cancel();
        dispatchersCleanupTimer.purge();
        timerTasks.remove(transactionTicket);

        final String msg = "Dispatcher cleaning task for transaction: " + transactionTicket;
        LOGGER.log(Level.FINE, msg);
    }

    @Override
    public StringList getDictionary(IntegerList wordIds, MALInteraction interaction)
            throws MALInteractionException, MALException {
        StringList output = new StringList();

        for (Integer wordId : wordIds) {
            String word;
            try {
                word = dictionary.getWord(wordId);
            } catch (Exception ex) {
                word = null;
                LOGGER.log(Level.SEVERE, "The word was not found!", ex);
            }

            output.add(word);
        }

        return output;
    }

    @Override
    public void free(Long transactionTicket, MALInteraction interaction) throws MALInteractionException, MALException {
        final Dispatcher dispatcher = dispatchers.get(transactionTicket);

        if (null == dispatcher) {
            throw new MALInteractionException(
                    new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, "Can't find a dispatcher!"));
        }

        final TimerTask timerTask = timerTasks.get(transactionTicket);

        if (null != timerTask) {
            cleanTimerTask(transactionTicket, timerTask);
        }

        cleanDispatcher(transactionTicket, dispatcher);

        Long lastSyncTime = syncTimes.get(transactionTicket);

        if (null == lastSyncTime) {
            throw new MALInteractionException(
                    new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, "Can't find a last sync time!"));
        }

        lastSync.set(lastSyncTime);
        LOGGER.log(Level.FINE, "Last sync time is set. For transaction : " + transactionTicket);
    }

    private void cleanDispatcher(Long transactionTicket, Dispatcher dispatcher) {
        dispatcher.clear();
        dispatchers.remove(transactionTicket);
        LOGGER.log(Level.FINE, "Dispatcher removed for transaction: " + transactionTicket);
    }

    private class CleaningTimerTask extends TimerTask {

        private final Long transactionTicket;

        public CleaningTimerTask(Long transactionTicket) {
            this.transactionTicket = transactionTicket;
        }

        /**
         * The action to be performed by this timer task.
         */
        @Override
        public void run() {
            LOGGER.log(Level.FINE, "Dispatcher cleaning task started for transaction: " + transactionTicket);
            final Dispatcher dispatcher = dispatchers.get(this.transactionTicket);

            if (null != dispatcher) {
                cleanDispatcher(this.transactionTicket, dispatcher);
            }

            cleanTimerTask(this.transactionTicket, this);
            LOGGER.log(Level.FINE, "Dispatcher cleaning task ended for transaction: ", transactionTicket);
        }
    }

    private class Dispatcher {

        private final RetrieveRangeInteraction interaction;

        // These chunks are already compressed!
        private final ArrayList<byte[]> chunksFlushed = new ArrayList<>();

        private byte[] dataToFlush = null;

        private final ArchiveConsumerServiceImpl archive;

        private int chunkSize = 200;

        private int numberOfChunks = 0;

        private boolean purgeArchive;

        Dispatcher(final RetrieveRangeInteraction interaction, ArchiveConsumerServiceImpl archive) {
            this.interaction = interaction;
            this.archive = archive;

            String chunkSizeParam = System.getProperty(Const.ARCHIVESYNC_CHUNK_SIZE_PROPERTY,
                Const.ARCHIVESYNC_CHUNK_SIZE_DEFAULT);

            try {
                this.chunkSize = Integer.parseInt(chunkSizeParam);
            } catch (NumberFormatException e) {
                Logger.getLogger(Dispatcher.class.getName()).log(Level.WARNING,
                        "Unexpected NumberFormatException on " + Const.ARCHIVESYNC_CHUNK_SIZE_PROPERTY, e);
            }

            Logger.getLogger(Dispatcher.class.getName()).log(Level.FINE,
                    Const.ARCHIVESYNC_CHUNK_SIZE_PROPERTY + " = " + this.chunkSize);
            this.purgeArchive = Boolean.parseBoolean(System.getProperty(Const.ARCHIVESYNC_PURGE_ARCHIVE_PROPERTY,
                    Const.ARCHIVESYNC_PURGE_ARCHIVE_DEFAULT));
        }

        private void clear() {
            chunksFlushed.clear();
        }

        public byte[] getFlushedChunk(int index) {
            return chunksFlushed.get(index);
        }

        public int numberOfChunks() throws IOException {
            if (numberOfChunks == 0) {
                throw new IOException("The dispatcher still did not pushed everything to the consumer!");
            }

            return numberOfChunks;
        }

        public void addObjects(final List<COMObjectEntity> entities) {
            dataToFlush = EncodeDecode.encodeToCompressedByteArray(entities, manager, dictionary);
        }

        public void flushData() {
            numberOfChunks = dataToFlush.length / chunkSize + (dataToFlush.length % chunkSize != 0 ? 1 : 0);
            if (numberOfChunks > 0) {
                byte[] aChunk = new byte[chunkSize];

                for (int i = 0; i < numberOfChunks - (dataToFlush.length % chunkSize != 0 ? 1 : 0); ++i) {
                    System.arraycopy(dataToFlush, chunkSize * i, aChunk, 0, chunkSize);
                    sendUpdateToConsumer(i, aChunk);
                }

                // Flush the last byte array!
                if (dataToFlush.length % chunkSize != 0) {
                    byte[] lastChunk = new byte[dataToFlush.length - (numberOfChunks - 1) * chunkSize]; // We need to trim to fit!
                    System.arraycopy(dataToFlush, chunkSize * (numberOfChunks - 1), lastChunk, 0, lastChunk.length);
                    sendUpdateToConsumer(numberOfChunks - 1, lastChunk);
                }
            }

            try {
                interaction.sendResponse(new UInteger(numberOfChunks));
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.SEVERE, "Unexpected exception!", ex);
            }

            LOGGER.log(Level.INFO, "Objects were successfully flushed! "
                    + numberOfChunks + "{0} chunks in total!");

            // This block cleans up the archive after sync if the option is enabled
            if (purgeArchive) {
                ArchiveQuery archiveQuery = new ArchiveQuery(null, null, null, 0L, null, new FineTime(0), latestSync, null, null);
                // Iterate over constant set of types to purge until the latest synchronised object
                for (ToDelete type : ToDelete.values()) {
                    int removed = manager.deleteCOMObjectEntities(type.getType(), archiveQuery, null);
                    LOGGER.log(Level.FINE, "Removed {} entities of type {}", new Object[]{removed, type.toString()});
                }
            }
        }

        public void sendUpdateToConsumer(int index, byte[] aChunk) {
            chunksFlushed.add(index, aChunk);
            try {
                interaction.sendUpdate(new Blob(aChunk), new UInteger(index));
            } catch (MALInteractionException | MALException ex) {
                LOGGER.log(Level.SEVERE, "Unexpected exception!", ex);
            }
        }

        private class ObjectsReceivedAdapter extends ArchiveAdapter {

            private final ArchiveDetailsList queryResults = new ArchiveDetailsList();

            private final ArchiveConsumerServiceImpl archive;

            private final ObjectType type;

            public ObjectsReceivedAdapter(ArchiveConsumerServiceImpl archive, ObjectType type) {
                this.archive = archive;
                this.type = type;
            }

            @Override
            public void queryUpdateReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                    ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
                super.queryUpdateReceived(msgHeader, objType, domain, objDetails, objBodies, qosProperties);
                if (objDetails != null) {
                    queryResults.addAll(objDetails);
                    Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Received update");
                }
            }

            @Override
            public void queryResponseReceived(MALMessageHeader msgHeader, ObjectType objType, IdentifierList domain,
                    ArchiveDetailsList objDetails, ElementList objBodies, Map qosProperties) {
                super.queryResponseReceived(msgHeader, objType, domain, objDetails, objBodies, qosProperties);
                if (null == objType || null == domain || null == objDetails || null == objBodies) {
                    return;
                }

                HashSet<Long> clearedIds = new HashSet<>();

                queryResults.addAll(objDetails);
                Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Received response!");
                if (objType.equals(ToDelete.STDERR_VALUE.getType()) || objType.equals(ToDelete.STDOUT_VALUE.getType())) {
                    objDetails.stream().map(detail -> detail.getDetails().getSource().getKey().getInstId())
                            .forEach(x -> clearedIds.add(x));
                }

                List<Long> ids = queryResults.stream().map(detail -> detail.getInstId()).collect(Collectors.toList());
                LongList objInstIds = new LongList();
                objInstIds.addAll(ids);
                try {
                    Thread.sleep(1000);

                    archive.getArchiveStub().delete(type, domain, objInstIds);

                    if (stdQuota != null) {
                        stdQuota.clean(clearedIds);
                    }
                } catch (MALInteractionException | MALException ex) {
                    LOGGER.log(Level.SEVERE, "Unexpected exception!", ex);
                } catch (InterruptedException ex) {
                    LOGGER.log(Level.SEVERE, "Unexpected exception!", ex);
                    Thread.currentThread().interrupt();

                }
            }
        }
    }
}
