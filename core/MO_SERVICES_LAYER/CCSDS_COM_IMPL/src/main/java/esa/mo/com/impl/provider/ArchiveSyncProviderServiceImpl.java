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

import esa.mo.com.impl.archive.entities.COMObjectEntity;
import esa.mo.com.impl.sync.Dictionary;
import esa.mo.com.impl.sync.EncodeDecode;
import esa.mo.helpertools.connections.ConnectionProvider;
import esa.mo.helpertools.helpers.HelperTime;
import java.io.IOException;
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
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;

/**
 * Archive Sync service Provider.
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
    public void retrieveRange(FineTime from, FineTime until, ObjectTypeList objectTypes,
            Identifier compression, RetrieveRangeInteraction interaction) throws MALInteractionException, MALException {
        final Dispatcher dispatcher = new Dispatcher(interaction);
        long interactionTicket = interaction.getInteraction().getMessageHeader().getTransactionId();
        dispatchers.put(interactionTicket, dispatcher);
        interaction.sendAcknowledgement(interactionTicket);

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

        dispatcher.setQueriesAreDone(true);

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
                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            for (UInteger missingIndex : missingIndexes) {
                byte[] chunk = dispatcher.getFlushedChunk((short) missingIndex.getValue());
                interaction.sendUpdate(new Blob(chunk), missingIndex);
            }
        }

        interaction.sendResponse();
    }

    @Override
    public StringList getDictionary(IntegerList wordIds, MALInteraction interaction) throws MALInteractionException, MALException {
        StringList output = new StringList();

        for (Integer wordId : wordIds) {
            String word;
            try {
                word = dictionary.getWord(wordId);
            } catch (Exception ex) {
                word = null;
                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            output.add(word);
        }

        return output;
    }

    @Override
    public void free(Long transactionTicket, MALInteraction interaction) throws MALInteractionException, MALException {
        final Dispatcher dispatcher = dispatchers.get(transactionTicket);

        if (dispatcher == null) {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        dispatcher.clear();
        dispatchers.remove(transactionTicket);
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
        private int numberOfChunks = 0;

        Dispatcher(final RetrieveRangeInteraction interaction) {
            this.interaction = interaction;
        }

        private void clear() {
            tempQueue.clear();
            chunksFlushed.clear();
            dataToFlush.clear();
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
                    int counter = 0;
                    
                    try {
                        boolean exit = false;

                        while (!exit) {
                            boolean done = queriesAreDone;
                            COMObjectEntity entity = tempQueue.poll(1, TimeUnit.SECONDS);

                            if (entity != null) {
                                byte[] objAsByteArray = EncodeDecode.encodeToByteArray(entity, manager, dictionary);
                                // Compress here?
                                // compression algorithm can go here!
                                dataToFlush.add(objAsByteArray);
                                counter++;
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
                            "The objects were all successfully processed! " + 
                                    counter + " objects in total!");
                }
            };
        }

        public void sendUpdateToConsumer(int index, byte[] aChunk) {
            chunksFlushed.add(index, aChunk);
            try {
                interaction.sendUpdate(new Blob(aChunk), new UInteger(index));
            } catch (MALInteractionException ex) {
                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            } catch (MALException ex) {
                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
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

                        while (!exit) {
                            boolean done = processingIsDone;
                            byte[] data = dataToFlush.poll(1, TimeUnit.SECONDS);

                            if (data != null) {
                                for (int i = 0; i < data.length; i++) {
                                    aChunk[pos] = data[i];
                                    pos++;

                                    if (pos == CHUNK_SIZE) {
                                        sendUpdateToConsumer(index, aChunk);
                                        index++;
                                        pos = 0;
                                    }
                                }
                            } else {
                                if (done) {
                                    exit = true;

                                    // Flush the last byte array!
                                    byte[] lastChunk = new byte[pos]; // We need to trim to fit!
                                    System.arraycopy(aChunk, 0, lastChunk, 0, pos);

                                    sendUpdateToConsumer(index, lastChunk);
                                    index++;
                                }
                            }
                        }

                        try {
                            interaction.sendResponse(new UInteger(index));
                            numberOfChunks = index;
                        } catch (MALInteractionException ex) {
                            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MALException ex) {
                            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(
                            Level.INFO, "The objects were all successfully flushed!" + 
                                    numberOfChunks + " chunks in total!");
                }
            };
        }
    }
}
