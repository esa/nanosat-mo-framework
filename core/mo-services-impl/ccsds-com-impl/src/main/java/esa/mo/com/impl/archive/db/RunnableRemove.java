package esa.mo.com.impl.archive.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.structures.LongList;

final class RunnableRemove implements Runnable {
    /**
 *
 */
private final TransactionsProcessor transactionsProcessor;
    private final Runnable publishEvents;
    private final Integer objTypeId;
    private final Integer domainId;
    private final LongList objIds;

    RunnableRemove(TransactionsProcessor transactionsProcessor, Runnable publishEvents, Integer objTypeId, Integer domainId, LongList objIds) {
        this.transactionsProcessor = transactionsProcessor;
        this.publishEvents = publishEvents;
        this.objTypeId = objTypeId;
        this.domainId = domainId;
        this.objIds = objIds;
    }

    @Override
    public void run() {
        try {
            this.transactionsProcessor.dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            Connection c = this.transactionsProcessor.dbBackend.getConnection();
            c.setAutoCommit(false);

            PreparedStatement deleteStmt = transactionsProcessor.dbBackend.getPreparedStatements().getDeleteCOMObjects();

            // Generate the object Ids if needed and the persistence objects to be removed
            for (int i = 0; i < objIds.size(); i++) {
                deleteStmt.setInt(1, objTypeId);
                deleteStmt.setInt(2, domainId);
                deleteStmt.setLong(3, objIds.get(i));
                deleteStmt.addBatch();

                // Flush every 1k objects...
                if (i != 0) {
                    if ((i % 1000) == 0) {
                        TransactionsProcessor.LOGGER.log(Level.FINE,
                                "Flushing the data after 1000 serial stores...");

                        deleteStmt.executeBatch();
                        deleteStmt.clearBatch();
                    }
                }
            }

            deleteStmt.executeBatch();
            c.setAutoCommit(true);
        } catch (SQLException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }

        this.transactionsProcessor.dbBackend.getAvailability().release();
      if(publishEvents != null) {
        this.transactionsProcessor.generalExecutor.submit(publishEvents);
      }
        this.transactionsProcessor.vacuum();
    }
}