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
package esa.mo.com.impl.archive.db;

import esa.mo.com.impl.archive.entities.COMObjectEntity;
import esa.mo.com.impl.provider.ArchiveManager;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.QueryFilter;
import org.ccsds.moims.mo.mal.structures.IntegerList;
import org.ccsds.moims.mo.mal.structures.LongList;

/**
 * The Transactions Processor is responsible for executing the transactions with
 * the database. It has one general executor with 2 threads for flushing the
 * generated COM Events and a single thread executor for orderly executing the
 * interactions with the database. It includes an optimization for batch stores,
 * basically, if many stores are received sequentially, they will be
 * consolidated and executed in one single transaction.
 */
public class TransactionsProcessor {

    public static Logger LOGGER = Logger.getLogger(TransactionsProcessor.class.getName());
    public final DatabaseBackend dbBackend;

    // This executor is responsible for the interactions with the db
    // Guarantees sequential order
    private final ExecutorService dbTransactionsExecutor = Executors.newSingleThreadExecutor(new DBThreadFactory(
        "Archive_DBTransactionsProcessor"));

    // This executor is expecting "short-lived" runnables that generate Events.
    // 2 Threads minimum because we need to acquire the lock from 2 different tasks during startup
    final ExecutorService generalExecutor = Executors.newFixedThreadPool(2, new DBThreadFactory(
        "Archive_GeneralProcessor"));
    private final AtomicBoolean sequencialStoring;

    final LinkedBlockingQueue<StoreCOMObjectsContainer> storeQueue;

    public TransactionsProcessor(DatabaseBackend dbBackend) {
        this.dbBackend = dbBackend;
        this.storeQueue = new LinkedBlockingQueue<>();
        this.sequencialStoring = new AtomicBoolean(false);
    }

    public void submitExternalTaskGeneral(final Runnable task) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order
        generalExecutor.execute(task);
    }

    public void submitExternalTaskDBTransactions(final Runnable task) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order
        dbTransactionsExecutor.execute(task);
    }

    /**
     * Resizes the database file to match its contents.
     */
    public void vacuum() {
        try {
            Connection c = dbBackend.getConnection();
            LOGGER.log(Level.FINE, "Vacuuming database");
            Statement query = c.createStatement();
            query.executeUpdate("VACUUM");
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Failed to vacuum database", ex);
        }
    }

    public COMObjectEntity getCOMObject(final Integer objTypeId, final Integer domainId,
            final Long objId) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order

        LongList ids = new LongList();
        ids.add(objId);
        Future<List<COMObjectEntity>> future = dbTransactionsExecutor.submit(new CallableGetCOMObjects(this, ids,
            domainId, objTypeId));

        try {
            List<COMObjectEntity> ret = future.get();
            if (ret.size() == 1) {
                return ret.get(0);
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public boolean existsCOMObject(final Integer objTypeId, final Integer domain, final Long objId) {
        return (getCOMObject(objTypeId, domain, objId) != null);
    }

    public List<COMObjectEntity> getCOMObjects(final Integer objTypeId, final Integer domainId, final LongList ids) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order

        Future<List<COMObjectEntity>> future = dbTransactionsExecutor.submit(new CallableGetCOMObjects(this, ids,
            domainId, objTypeId));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public List<COMObjectEntity> getAllCOMObjects(final Integer objTypeId, final Integer domainId) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order

        IntegerList types = new IntegerList();
        types.add(objTypeId);
        IntegerList domains = new IntegerList();
        domains.add(domainId);
        ArchiveQuery archiveQuery = new ArchiveQuery(null, null, null, 0L, null, null, null, null, null);
        CallableSelectQuery query = new CallableSelectQuery(this, types, archiveQuery, domains, null, null, null, null);
        Future<ArrayList<COMObjectEntity>> future = dbTransactionsExecutor.submit(query);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public LongList getAllCOMObjectsIds(final Integer objTypeId, final Integer domainId) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order

        Future<LongList> future = dbTransactionsExecutor.submit(new CallableGetAllCOMObjectIds(this, domainId,
            objTypeId));

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public void insert(final ArrayList<COMObjectEntity> perObjs, final Runnable publishEvents) {
        final boolean isSequential = this.sequencialStoring.get();
        final StoreCOMObjectsContainer container = new StoreCOMObjectsContainer(perObjs, isSequential);

        this.sequencialStoring.set(true);

        try { // Insert into queue
            storeQueue.put(container);
        } catch (InterruptedException ex) {
            Logger.getLogger(ArchiveManager.class.getName()).log(Level.SEVERE, "Something went wrong...", ex);
        }

        dbTransactionsExecutor.execute(new RunnableInsert(this, publishEvents));
    }

    public void remove(final Integer objTypeId, final Integer domainId, final LongList objIds,
        final Runnable publishEvents) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order

        dbTransactionsExecutor.execute(new RunnableRemove(this, publishEvents, objTypeId, domainId, objIds));
    }

    public void update(final ArrayList<COMObjectEntity> newObjs, final Runnable publishEvents) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order

        dbTransactionsExecutor.execute(new RunnableUpdate(this, publishEvents, newObjs));
    }

    public ArrayList<COMObjectEntity> query(final IntegerList objTypeIds,
            final ArchiveQuery archiveQuery, final IntegerList domainIds,
            final Integer providerURIId, final Integer networkId,
            final SourceLinkContainer sourceLink, final QueryFilter filter) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order
        final CallableSelectQuery task = new CallableSelectQuery(this, objTypeIds, archiveQuery,
                domainIds, providerURIId, networkId, sourceLink, filter);

        Future<ArrayList<COMObjectEntity>> future = dbTransactionsExecutor.submit(task);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public int delete(final IntegerList objTypeIds,
            final ArchiveQuery archiveQuery, final IntegerList domainIds,
            final Integer providerURIId, final Integer networkId,
            final SourceLinkContainer sourceLink, final QueryFilter filter) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order
        final CallableDeleteQuery task = new CallableDeleteQuery(this, objTypeIds, archiveQuery,
                domainIds, providerURIId, networkId, sourceLink, filter);

        Future<Integer> future = dbTransactionsExecutor.submit(task);

        try {
            return future.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    public void resetMainTable(final Callable<?> task) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order
        Future<?> f = dbTransactionsExecutor.submit(task);
        LOGGER.info("Reset table submitted!");

        try {
            f.get();
            LOGGER.info("Reset table callback!");
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void stopInteractions(final Callable<?> task) {
        this.sequencialStoring.set(false); // Sequential stores can no longer happen otherwise we break order
        Future<?> future = dbTransactionsExecutor.submit(task);

        try {
            future.get();
        } catch (InterruptedException | ExecutionException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * The database backend thread factory
     */
    static class DBThreadFactory implements ThreadFactory {

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DBThreadFactory(String prefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup()
                    : Thread.currentThread().getThreadGroup();
            namePrefix = prefix + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    public static Long convert2Long(final Object obj) {
        if (obj == null) {
            return null;
        }

        if (obj instanceof Long) {
            return (Long) obj;
        }

        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }

        return null;
    }

}
