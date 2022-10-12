/* ----------------------------------------------------------------------------
 * Copyright (C) 2022      European Space Agency
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import esa.mo.com.impl.archive.entities.COMObjectEntity;

final class RunnableInsert implements Runnable {

    public static Logger LOGGER = Logger.getLogger(RunnableInsert.class.getName());

    private final TransactionsProcessor transactionsProcessor;
    private final Runnable publishEvents;

    RunnableInsert(TransactionsProcessor transactionsProcessor, Runnable publishEvents) {
        this.transactionsProcessor = transactionsProcessor;
        this.publishEvents = publishEvents;
    }

    @Override
    public void run() {
        StoreCOMObjectsContainer container = this.transactionsProcessor.storeQueue.poll();
        if (container != null) {
            try {
                this.transactionsProcessor.dbBackend.getAvailability().acquire();
            } catch (InterruptedException ex) {
                TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
            }

            ArrayList<COMObjectEntity> objs = new ArrayList<>();
            objs.addAll(container.getPerObjs());

            while (true) {
                container = this.transactionsProcessor.storeQueue.peek(); // get next if there is one available

                if (container == null || !container.isContinuous()) {
                    break;
                }

                objs.addAll(this.transactionsProcessor.storeQueue.poll().getPerObjs());
            }

            persistObjects(objs); // store
            this.transactionsProcessor.dbBackend.getAvailability().release();
        }

        if (publishEvents != null) {
            this.transactionsProcessor.generalExecutor.submit(publishEvents);
        }
    }

    void persistObjects(final ArrayList<COMObjectEntity> perObjs) {
        try {
            Connection c = this.transactionsProcessor.dbBackend.getConnection();
            c.setAutoCommit(false);
            PreparedStatement insertStmt = transactionsProcessor.dbBackend.getPreparedStatements()
                .getInsertCOMObjects();

            for (int i = 0; i < perObjs.size(); i++) { // 6.510 ms per cycle
                COMObjectEntity obj = perObjs.get(i);
                insertStmt.setObject(1, obj.getObjectTypeId());
                insertStmt.setObject(2, obj.getObjectId());
                insertStmt.setObject(3, obj.getDomainId());
                insertStmt.setObject(4, obj.getNetwork());
                insertStmt.setObject(5, obj.getObjectEncoded());
                insertStmt.setObject(6, obj.getProviderURI());
                insertStmt.setObject(7, obj.getRelatedLink());
                insertStmt.setObject(8, obj.getSourceLink().getDomainId());
                insertStmt.setObject(9, obj.getSourceLink().getObjId());
                insertStmt.setObject(10, obj.getSourceLink().getObjectTypeId());
                insertStmt.setObject(11, obj.getTimestamp().getValue());
                insertStmt.addBatch();

                // Flush every 1k objects...
                if (i != 0) {
                    if ((i % 1000) == 0) {
                        LOGGER.log(Level.FINE, "Flushing the data after 1000 serial stores...");

                        insertStmt.executeBatch();
                        insertStmt.clearBatch();
                    }
                }
            }

            insertStmt.executeBatch();
            c.setAutoCommit(true);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }
}
