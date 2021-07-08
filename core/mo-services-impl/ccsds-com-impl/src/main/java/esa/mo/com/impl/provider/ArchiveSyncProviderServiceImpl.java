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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.com.COMHelper;
import org.ccsds.moims.mo.com.archive.consumer.ArchiveAdapter;
import org.ccsds.moims.mo.com.archive.structures.ArchiveDetailsList;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQueryList;
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
import org.ccsds.moims.mo.mal.structures.ElementList;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;
import org.ccsds.moims.mo.mal.structures.StringList;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UIntegerList;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * Archive Sync service Provider.
 */
public class ArchiveSyncProviderServiceImpl extends ArchiveSyncInheritanceSkeleton
{

    private final ConnectionProvider connection = new ConnectionProvider();

    private final AtomicLong lastSync = new AtomicLong(0);

    private final Dictionary dictionary = new Dictionary();

    private final HashMap<Long, Dispatcher> dispatchers = new HashMap<>();

    private final ExecutorService executor = Executors.newCachedThreadPool();

    private ArchiveManager manager;

    private MALProvider archiveSyncServiceProvider;

    private boolean initialiased = false;

    private boolean running = false;

    private ArchiveConsumerServiceImpl archive;

    private FineTime latestSync;

    private Quota stdQuota;

    public ArchiveSyncProviderServiceImpl(SingleConnectionDetails connectionToArchiveService)
    {
        this(connectionToArchiveService, null, null);
    }

    public ArchiveSyncProviderServiceImpl(SingleConnectionDetails connectionToArchiveService,
                                          Blob authenticationId,
                                          String localNamePrefix)
    {
        try
        {
            this.archive = new ArchiveConsumerServiceImpl(connectionToArchiveService,
                                                          authenticationId,
                                                          localNamePrefix);
        }
        catch (MALException | MalformedURLException ex)
        {
            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Set the quota. Should only be used when STD limits are used for the archive.
     *
     * @param quota The same Quota object that is passed to the AppsLauncherProviderServiceImpl using its setStdPerApp
     *              method.
     */
    public void setStdQuota(Quota quota)
    {
        this.stdQuota = quota;
    }

    /**
     * creates the MAL objects, the publisher used to create updates and starts the publishing thread
     *
     * @param manager the Archive Manager
     * @throws MALException if initialization error.
     */
    public synchronized void init(ArchiveManager manager) throws MALException
    {
        if (!initialiased)
        {
            if (MALContextFactory.lookupArea(MALHelper.MAL_AREA_NAME, MALHelper.MAL_AREA_VERSION) == null)
            {
                MALHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            if (MALContextFactory.lookupArea(COMHelper.COM_AREA_NAME, COMHelper.COM_AREA_VERSION) == null)
            {
                COMHelper.init(MALContextFactory.getElementFactoryRegistry());
            }

            try
            {
                ArchiveSyncHelper.init(MALContextFactory.getElementFactoryRegistry());
            }
            catch (MALException ex)
            {
            }
        }

        this.manager = manager;

        // shut down old service transport
        if (null != archiveSyncServiceProvider)
        {
            connection.closeAll();
        }

        archiveSyncServiceProvider = connection.startService(ArchiveSyncHelper.ARCHIVESYNC_SERVICE_NAME.toString(),
                                                             ArchiveSyncHelper.ARCHIVESYNC_SERVICE,
                                                             false,
                                                             this);
        running = true;
        initialiased = true;
        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).info("ArchiveSync service READY");
    }

    /**
     * Closes all running threads and releases the MAL resources.
     */
    public void close()
    {
        try
        {
            if (null != archiveSyncServiceProvider)
            {
                archiveSyncServiceProvider.close();
            }

            manager.close();

            connection.closeAll();
            running = false;
        }
        catch (MALException ex)
        {
            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName())
                    .log(Level.WARNING, "Exception during close down of the provider {0}", ex);
        }
    }

    @Override
    public GetTimeResponse getTime(final MALInteraction interaction) throws MALInteractionException, MALException
    {
        final FineTime currentTime = HelperTime.getTimestamp();
        final FineTime lastSyncTime = new FineTime(lastSync.get());
        return new GetTimeResponse(currentTime, lastSyncTime);
    }

    @Override
    public void retrieveRange(FineTime from,
                              FineTime until,
                              ObjectTypeList objectTypes,
                              Identifier compression,
                              RetrieveRangeInteraction interaction) throws MALInteractionException, MALException
    {
        final Dispatcher dispatcher = new Dispatcher(interaction, archive);
        long interactionTicket = interaction.getInteraction().getMessageHeader().getTransactionId();
        dispatchers.put(interactionTicket, dispatcher);
        lastSync.set(HelperTime.getTimestamp().getValue());
        interaction.sendAcknowledgement(interactionTicket);

        Runnable processQueriedObjs = dispatcher.getProcessingRunnable();
        Runnable flushProcessedObjs = dispatcher.getFlushingRunnable();

        executor.execute(processQueriedObjs);
        executor.execute(flushProcessedObjs);

        ArrayList<COMObjectEntity> perObjs;

        for (int i = 0; i < objectTypes.size(); i++)
        {
            ArchiveQuery archiveQuery = new ArchiveQuery();
            archiveQuery.setStartTime(from);
            archiveQuery.setEndTime(until);
            archiveQuery.setDomain(null);
            archiveQuery.setNetwork(null);
            archiveQuery.setProvider(null);
            archiveQuery.setRelated(0L);
            archiveQuery.setSource(null);
            archiveQuery.setSortFieldName(null);

            perObjs = manager.queryCOMObjectEntity(objectTypes.get(i), archiveQuery, null);
            latestSync = until;
            dispatcher.addObjects(perObjs);
        }

        dispatcher.setQueriesAreDone(true);

        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName())
                .log(Level.INFO, "Stage 1: The objects were queried and are now being sent back to the consumer!");
    }

    @Override
    public void retrieveRangeAgain(final Long transactionTicket,
                                   final UIntegerList missingIndexes,
                                   final RetrieveRangeAgainInteraction interaction) throws
                                                                                    MALInteractionException,
                                                                                    MALException
    {
        final Dispatcher dispatcher = dispatchers.get(transactionTicket);

        if (dispatcher == null)
        {
            throw new MALInteractionException(new MALStandardError(COMHelper.INVALID_ERROR_NUMBER, null));
        }

        interaction.sendAcknowledgement();

        if (missingIndexes.size() == 2 && missingIndexes.get(1).getValue() == 0)
        {
            // Special case! The condition means that we need to retransmit
            // everything since the value in missingIndexes.get(0)
            UInteger lastIndex = missingIndexes.get(0);
            try
            {
                int numberOfChunks = dispatcher.numberOfChunks();

                for (int i = (int) lastIndex.getValue(); i < numberOfChunks; i++)
                {
                    byte[] chunk = dispatcher.getFlushedChunk(i);
                    interaction.sendUpdate(new Blob(chunk), new UInteger(i));
                }
            }
            catch (IOException ex)
            {
                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        else
        {
            for (UInteger missingIndex : missingIndexes)
            {
                byte[] chunk = dispatcher.getFlushedChunk((short) missingIndex.getValue());
                interaction.sendUpdate(new Blob(chunk), missingIndex);
            }
        }

        interaction.sendResponse();
    }

    @Override
    public StringList getDictionary(IntegerList wordIds, MALInteraction interaction) throws
                                                                                     MALInteractionException,
                                                                                     MALException
    {
        StringList output = new StringList();

        for (Integer wordId : wordIds)
        {
            String word;
            try
            {
                word = dictionary.getWord(wordId);
            }
            catch (Exception ex)
            {
                word = null;
                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }

            output.add(word);
        }

        return output;
    }

    @Override
    public void free(Long transactionTicket, MALInteraction interaction) throws MALInteractionException, MALException
    {
        final Dispatcher dispatcher = dispatchers.get(transactionTicket);

        if (dispatcher == null)
        {
            throw new MALInteractionException(new MALStandardError(MALHelper.UNKNOWN_ERROR_NUMBER, null));
        }

        dispatcher.clear();
        dispatchers.remove(transactionTicket);
    }

    private class Dispatcher
    {

        // A temporary queue to hold the objects that were queried
        private final LinkedBlockingQueue<COMObjectEntity> tempQueue = new LinkedBlockingQueue<>();

        private final RetrieveRangeInteraction interaction;

        // These chunks are already compressed!
        private final ArrayList<byte[]> chunksFlushed = new ArrayList<>();

        private final LinkedBlockingQueue<byte[]> dataToFlush = new LinkedBlockingQueue<>();

        private int chunkSize = 200;

        private int pollTiemout = 1000;

        private boolean queriesAreDone = false;

        private boolean processingIsDone = false;

        private int numberOfChunks = 0;

        private ArchiveConsumerServiceImpl archive;

        Dispatcher(final RetrieveRangeInteraction interaction, ArchiveConsumerServiceImpl archive)
        {
            this.interaction = interaction;
            this.archive = archive;

            String chunkSizeParam = System.getProperty("esa.nmf.archive.sync.chunk.size");

            if (null != chunkSizeParam && !"".equals(chunkSizeParam))
            {

                try
                {
                    int chunkSizePramInt = Integer.parseInt(chunkSizeParam);

                    this.chunkSize = chunkSizePramInt;
                }
                catch (NumberFormatException e)
                {
                    Logger.getLogger(Dispatcher.class.getName()).log(Level.WARNING,
                                                                     "Unexpected NumberFormatException on esa.nmf.archive.sync.chunk.size ! "
                                                                     + e.getMessage(),
                                                                     e);
                }
            }

            Logger.getLogger(Dispatcher.class.getName())
                    .log(Level.INFO, "esa.nmf.archive.sync.chunk.size = " + this.chunkSize);

            String queuePollTimeout = System.getProperty("esa.nmf.archive.sync.queue.poll.timeout");

            if (null != queuePollTimeout && !"".equals(queuePollTimeout))
            {

                try
                {
                    int queuePollTimeoutInt = Integer.parseInt(queuePollTimeout);

                    this.pollTiemout = queuePollTimeoutInt;
                }
                catch (NumberFormatException e)
                {
                    Logger.getLogger(Dispatcher.class.getName()).log(Level.WARNING,
                                                                     "Unexpected NumberFormatException on esa.nmf.archive.sync.queue.poll.timeout ! "
                                                                     + e.getMessage(),
                                                                     e);
                }
            }

            Logger.getLogger(Dispatcher.class.getName())
                    .log(Level.INFO, "esa.nmf.archive.sync.queue.poll.timeout = " + this.pollTiemout);
        }

        private void clear()
        {
            tempQueue.clear();
            chunksFlushed.clear();
            dataToFlush.clear();
        }

        public byte[] getFlushedChunk(int index)
        {
            return chunksFlushed.get(index);
        }

        public int numberOfChunks() throws IOException
        {
            if (numberOfChunks == 0)
            {
                throw new IOException("The dispatcher still did not pushed everything to the consumer!");
            }

            return numberOfChunks;
        }

        public void addObjects(final ArrayList<COMObjectEntity> list)
        {
            // "addAll()" might not be thread-safe. "add" is for sure!
            tempQueue.addAll(list);
        }

        public void setQueriesAreDone(final boolean done)
        {
            this.queriesAreDone = done;
        }

        public Runnable getProcessingRunnable()
        {
            return () -> {
                int counter = 0;

                try
                {
                    boolean exit = false;

                    while (!exit)
                    {
                        boolean done = queriesAreDone;
                        COMObjectEntity entity = tempQueue.poll(pollTiemout, TimeUnit.MILLISECONDS);

                        if (entity != null)
                        {
                            byte[] objAsByteArray = EncodeDecode.encodeToByteArray(entity, manager, dictionary);
                            // Compress here?
                            // compression algorithm can go here!
                            dataToFlush.add(objAsByteArray);
                            counter++;
                        }
                        else
                        {
                            if (done)
                            {
                                exit = true;
                            }
                        }
                    }
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }

                processingIsDone = true;
                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.INFO,
                                                                                     "Stage 2: The objects were all successfully processed! "
                                                                                     + counter
                                                                                     + " objects in total!");
            };
        }

        public void sendUpdateToConsumer(int index, byte[] aChunk)
        {
            chunksFlushed.add(index, aChunk);
            try
            {
                interaction.sendUpdate(new Blob(aChunk), new UInteger(index));
            }
            catch (MALInteractionException | MALException ex)
            {
                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public Runnable getFlushingRunnable()
        {
            return () -> {
                try
                {
                    boolean exit = false;
                    int index = 0;
                    byte[] aChunk = new byte[chunkSize];
                    int pos = 0;

                    while (!exit)
                    {
                        boolean done = processingIsDone;
                        byte[] data = dataToFlush.poll(pollTiemout, TimeUnit.MILLISECONDS);

                        if (data != null)
                        {
                            for (int i = 0; i < data.length; i++)
                            {
                                aChunk[pos] = data[i];
                                pos++;

                                if (pos == chunkSize)
                                {
                                    sendUpdateToConsumer(index, aChunk);
                                    index++;
                                    pos = 0;
                                }
                            }
                        }
                        else
                        {
                            if (done)
                            {
                                exit = true;

                                // Flush the last byte array!
                                byte[] lastChunk = new byte[pos]; // We need to trim to fit!
                                System.arraycopy(aChunk, 0, lastChunk, 0, pos);

                                sendUpdateToConsumer(index, lastChunk);
                                index++;
                            }
                        }
                    }

                    try
                    {
                        interaction.sendResponse(new UInteger(index));
                        numberOfChunks = index;
                    }
                    catch (MALInteractionException ex)
                    {
                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                    catch (MALException ex)
                    {
                        Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }

                Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.INFO,
                                                                                     "Stage 3: The objects were all successfully flushed! "
                                                                                     + numberOfChunks
                                                                                     + " chunks in total!");
                boolean purge = Boolean.valueOf(System.getProperty(Const.ARCHIVESYNC_PURGE_ARCHIVE_PROPERTY,
                                                                   Const.ARCHIVESYNC_PURGE_ARCHIVE_DEFAULT));
                if (purge)
                { // This block cleans up the archive after sync if the option is enabled
                    ArchiveQueryList aql = new ArchiveQueryList();
                    aql.add(new ArchiveQuery(null, null, null, 0L, null, new FineTime(0), latestSync, null, null));

                    for (ToDelete type : ToDelete.values())
                    {
                        try
                        {
                            archive.getArchiveStub().query(false,
                                                           type.getType(),
                                                           aql,
                                                           null,
                                                           new ObjectsReceivedAdapter(archive, type.getType()));
                        }
                        catch (MALInteractionException ex)
                        {
                            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                        catch (MALException ex)
                        {
                            Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName())
                                    .log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };
        }

        private class ObjectsReceivedAdapter extends ArchiveAdapter
        {

            private ArchiveDetailsList queryResults = new ArchiveDetailsList();

            private ArchiveConsumerServiceImpl archive;

            private ObjectType type;

            public ObjectsReceivedAdapter(ArchiveConsumerServiceImpl archive, ObjectType type)
            {
                this.archive = archive;
                this.type = type;
            }

            @Override
            public void queryResponseReceived(MALMessageHeader msgHeader,
                                              ObjectType objType,
                                              IdentifierList domain,
                                              ArchiveDetailsList objDetails,
                                              ElementList objBodies,
                                              Map qosProperties)
            {
                super.queryResponseReceived(msgHeader, objType, domain, objDetails, objBodies, qosProperties);
                if (objType == null || domain == null || objDetails == null || objBodies == null)
                {
                    return;
                }
                HashSet<Long> clearedIds = new HashSet<>();
                if (objDetails != null)
                {
                    queryResults.addAll(objDetails);
                    Logger.getLogger(this.getClass().getName()).log(Level.FINE, "Received response!");
                    if (objType != null && (objType.equals(ToDelete.STDERR_VALUE.getType()) || objType
                            .equals(ToDelete.STDOUT_VALUE.getType())))
                    {
                        objDetails.stream().map(detail -> detail.getDetails().getSource().getKey().getInstId())
                                .forEach(x -> clearedIds.add(x));
                    }
                }

                List<Long> ids = queryResults.stream().map(detail -> detail.getInstId()).collect(Collectors.toList());
                LongList objInstIds = new LongList();
                objInstIds.addAll(ids);
                try
                {
                    Thread.sleep(1000);
                    LongList deleted = archive.getArchiveStub().delete(type, domain, objInstIds);
                    if (stdQuota != null)
                    {
                        stdQuota.clean(clearedIds);
                    }
                }
                catch (MALInteractionException | InterruptedException | MALException ex)
                {
                    Logger.getLogger(ArchiveSyncProviderServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void queryUpdateReceived(MALMessageHeader msgHeader,
                                            ObjectType objType,
                                            IdentifierList domain,
                                            ArchiveDetailsList objDetails,
                                            ElementList objBodies,
                                            Map qosProperties)
            {
                super.queryUpdateReceived(msgHeader, objType, domain, objDetails, objBodies, qosProperties);
                if (objDetails != null)
                {
                    queryResults.addAll(objDetails);
                    Logger.getLogger(this.getClass().getName()).log(Level.FINER, "Received update");
                }
            }
        }
    }
}
