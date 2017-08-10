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

import esa.mo.com.impl.archive.db.TransactionsProcessor;
import esa.mo.com.impl.archive.encoding.BinaryEncoder;
import esa.mo.com.impl.archive.entities.COMObjectEntity;
import esa.mo.com.impl.sync.Dictionary;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archivesync.ArchiveSyncHelper;
import org.ccsds.moims.mo.com.archivesync.body.GetTimeResponse;
import org.ccsds.moims.mo.com.archivesync.provider.ArchiveSyncInheritanceSkeleton;
import org.ccsds.moims.mo.com.archivesync.provider.RetrieveRangeAgainInteraction;
import org.ccsds.moims.mo.com.archivesync.provider.RetrieveRangeInteraction;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.com.structures.ObjectTypeList;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.structures.URI;

/**
 *
 */
public class ArchiveSyncProviderServiceImpl extends ArchiveSyncInheritanceSkeleton {

    private ArchiveManager manager;
    private MALProvider archiveSyncServiceProvider;
    private boolean initialiased = false;
    private boolean running = false;
    private final ConnectionProvider connection = new ConnectionProvider();
    private final AtomicLong lastSync = new AtomicLong(0);
    private final Dictionary dictionary = new Dictionary();
    private final HashMap<Long, Dispatcher> dispatchers = new HashMap<Long, Dispatcher>();
    private final ExecutorService executor = Executors.newCachedThreadPool();

    /**
     * creates the MAL objects, the publisher used to create updates and starts
     * the publishing thread
     *
     * @param manager the Archive Manager
     * @throws MALException if initialization error.
     */
    public synchronized void init(ArchiveManager manager) throws MALException {
        if (!initialiased) {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null) {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null) {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try {
                ArchiveSyncHelper.init(MALContextFactory.getElementFactoryRegistry());
            } catch (MALException ex) {
            }
        }

        this.manager = manager;

        // shut down old service transport
        if (null != archiveSyncServiceProvider) {
            connection.closeAll();
        }

        archiveSyncServiceProvider = connection.startService(ArchiveSyncHelper.ARCHIVESYNC_SERVICE_NAME.toString(), ArchiveSyncHelper.ARCHIVESYNC_SERVICE, false, this);
        running = true;
        initialiased = true;
        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).info("ArchiveSync service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close() {
        try {
            if (null != archiveSyncServiceProvider) {
                archiveSyncServiceProvider.close();
            }

            manager.close();

            connection.closeAll();
            running = false;
        } catch (MALException ex) {
            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.WARNING,
                    "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public GetTimeResponse getTime(final MALInteraction interaction) throws MALInteractionException, MALException {
        final FineTime currentTime = HelperTime.getTimestamp();
        final FineTime lastSyncTime = new FineTime(lastSync.get());
        return new GetTimeResponse(currentTime, lastSyncTime);
    }

    @Override
    public void retrieveRange(final FineTime from, final FineTime until, final ObjectTypeList objectTypes,
            final RetrieveRangeInteraction interaction) throws MALInteractionException, MALException {
        final Dispatcher dispatcher = new Dispatcher(interaction);
        long interactionTicket = interaction.getInteraction().getMessageHeader().getTransactionId();
        dispatchers.put(interactionTicket, dispatcher);
        interaction.sendAcknowledgement(interactionTicket, new UInteger(0)); // 0 for now...

        Runnable processQueriedObjs = dispatcher.getProcessingRunnable();
        Runnable flushProcessedObjs = dispatcher.getFlushingRunnable();
        executor.execute(processQueriedObjs);
        executor.execute(flushProcessedObjs);

        ArrayList<COMObjectEntity> perObjs;

        for (int i = 0; i < objectTypes.size(); i++) {
            ArchiveQuery archiveQuery = new ArchiveQuery();
            archiveQuery.setStartTime(from);
            archiveQuery.setEndTime(until);
            archiveQuery.setDomain(null);
            archiveQuery.setNetwork(null);
            archiveQuery.setProvider(null);
            archiveQuery.setRelated(new Long(0));
            archiveQuery.setSource(null);
            archiveQuery.setSortFieldName(null);

            perObjs = manager.queryCOMObjectEntity(objectTypes.get(i), archiveQuery, null);
            dispatcher.addObjects(perObjs);
        }

        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.INFO,
                "The objects were queried and are now being sent back to the consumer!");
    }

    @Override
    public void retrieveRangeAgain(final Long transactionTicket, final UIntegerList missingIndexes,
            final RetrieveRangeAgainInteraction interaction) throws MALInteractionException, MALException {
        final Dispatcher dispatcher = dispatchers.get(transactionTicket);

        if (dispatcher == null) {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        interaction.sendAcknowledgement();

        for (UInteger missingIndex : missingIndexes) {
            byte[] chunk = dispatcher.getFlushedChunk((short) missingIndex.getValue());

            interaction.sendUpdate(new Blob(chunk), missingIndex);
        }

        interaction.sendResponse();
    }

    private class Dispatcher {

        private final static int CHUNK_SIZE = 512;

        // A temporary queue to hold the objects that were queried
        private final LinkedBlockingQueue<COMObjectEntity> tempQueue = new LinkedBlockingQueue<COMObjectEntity>();

        private final RetrieveRangeInteraction interaction;

        // These chunks are already compressed!
        private final ArrayList<byte[]> chunksFlushed = new ArrayList<byte[]>();

        private final LinkedBlockingQueue<byte[]> dataToFlush = new LinkedBlockingQueue<byte[]>();

        private boolean queriesAreDone = false;
        private boolean processingIsDone = false;

        Dispatcher(final RetrieveRangeInteraction interaction) {
            this.interaction = interaction;
        }

        public byte[] getFlushedChunk(int index) {
            return chunksFlushed.get(index);
        }

        public void addObjects(final ArrayList<COMObjectEntity> list) {
            // "addAll()" might not be thread-safe. "add" is for sure!
            for (COMObjectEntity entity : list) {
                tempQueue.add(entity);
            }
        }

        public void setQueriesAreDone(final boolean done) {
            this.queriesAreDone = done;
        }

        public Runnable getProcessingRunnable() {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean exit = false;

                        while (exit) {
                            boolean done = queriesAreDone;
                            COMObjectEntity entity = tempQueue.poll(5, TimeUnit.SECONDS);

                            if (entity != null) {
                                byte[] objAsByteArray = convertToByteArray(entity);
                                // Compress here?
                                // compression algorithm
                                dataToFlush.add(objAsByteArray);
                            } else {
                                if (done) {
                                    exit = true;
                                }
                            }
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    processingIsDone = true;
                    Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.INFO,
                            "The objects were all successfully processed!");
                }
            };
        }

        public Runnable getFlushingRunnable() {
            return new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean exit = false;
                        int index = 0;
                        byte[] aChunk = new byte[CHUNK_SIZE];
                        int pos = 0;

                        while (exit) {
                            boolean done = processingIsDone;
                            byte[] data = dataToFlush.poll(5, TimeUnit.SECONDS);

                            if (data != null) {
                                boolean chunkIsFull = (data.length + pos > CHUNK_SIZE);
                                int length = chunkIsFull ? CHUNK_SIZE - pos : data.length;
                                System.arraycopy(data, 0, aChunk, pos, length);

                                if (chunkIsFull) {
                                    // Send it to the consumer!
                                    chunksFlushed.add(index, aChunk);
                                    try {
                                        interaction.sendUpdate(new Blob(aChunk), new UInteger(index));
                                    } catch (MALInteractionException ex) {
                                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (MALException ex) {
                                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    index++;

                                    // Clean the aChunk and copy the rest of the data!
                                    aChunk = new byte[CHUNK_SIZE];
                                    System.arraycopy(data, length, aChunk, pos, length);
                                    pos = length;
                                } else {
                                    pos += length;
                                }
                            } else {
                                if (done) {
                                    exit = true;

                                    // Flush the last byte array!
                                    byte[] lastChunk = new byte[pos]; // We need to trim to fit!
                                    System.arraycopy(aChunk, 0, lastChunk, 0, pos);

                                    chunksFlushed.add(index, lastChunk);
                                    try {
                                        interaction.sendUpdate(new Blob(aChunk), new UInteger(index));
                                    } catch (MALInteractionException ex) {
                                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                                    } catch (MALException ex) {
                                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    index++;
                                }
                            }
                        }

                    } catch (InterruptedException ex) {
                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.INFO,
                            "The objects were all successfully flushed!");
                }

            };
        }

    }

    private byte[] convertToByteArray(final COMObjectEntity entity) {

        try {
            final ByteArrayOutputStream bodyBaos = new ByteArrayOutputStream();
            final BinaryEncoder be = new BinaryEncoder(bodyBaos);

            Identifier network = manager.getFastNetwork().getNetwork(entity.getNetwork());
            Integer wordId1 = dictionary.getWordId(network.getValue());
            be.encodeShort(wordId1.shortValue());

            URI providerURI = manager.getFastProviderURI().getProviderURI(entity.getProviderURI());
            Integer wordId2 = dictionary.getWordId(providerURI.getValue());
            be.encodeShort(wordId2.shortValue());

            ObjectType objType = manager.getFastObjectType().getObjectType(entity.getObjectTypeId());
            be.encodeElement(objType);

            // --- Source Link ---
            IdentifierList sourceDomain = manager.getFastDomain().getDomain(entity.getSourceLink().getDomainId());
            Integer wordId3 = dictionary.getWordId(sourceDomain.toString());
            be.encodeShort(wordId3.shortValue());

            ObjectType sourceObjType = manager.getFastObjectType().getObjectType(entity.getSourceLink().getObjectTypeId());
            be.encodeElement(sourceObjType);

            Long sourceObjId = entity.getSourceLink().getObjId();
            be.encodeNullableLong(sourceObjId);
            // --- Source Link ---

            Long relatedLink = entity.getRelatedLink();
            be.encodeNullableLong(relatedLink);

            byte[] array = entity.getObjectEncoded();
            Blob value = (array == null) ? null : new Blob(array);
            be.encodeNullableBlob(value);

            be.encodeLong(entity.getObjectId());
            be.encodeFineTime(entity.getTimestamp());

            byte[] output = bodyBaos.toByteArray();
            be.close();

            return output;
        } catch (Exception ex) {
            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return new byte[0]; // Return an empty byte array
    }

}
