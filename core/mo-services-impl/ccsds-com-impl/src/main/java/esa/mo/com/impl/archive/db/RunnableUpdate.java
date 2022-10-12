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

import esa.mo.com.impl.archive.entities.COMObjectEntity;

final class RunnableUpdate implements Runnable {
    /**
    *
    */
    private final TransactionsProcessor transactionsProcessor;
    private final Runnable publishEvents;
    private final ArrayList<COMObjectEntity> newObjs;

    RunnableUpdate(TransactionsProcessor transactionsProcessor, Runnable publishEvents,
        ArrayList<COMObjectEntity> newObjs) {
        this.transactionsProcessor = transactionsProcessor;
        this.publishEvents = publishEvents;
        this.newObjs = newObjs;
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

            PreparedStatement update = transactionsProcessor.dbBackend.getPreparedStatements().getUpdateCOMObjects();

            // Generate the object Ids if needed and the persistence objects to be removed
            for (int i = 0; i < newObjs.size(); i++) {
                COMObjectEntity obj = newObjs.get(i);
                update.setObject(1, obj.getObjectTypeId());
                update.setObject(2, obj.getObjectId());
                update.setObject(3, obj.getDomainId());
                update.setObject(4, obj.getNetwork());
                update.setObject(5, obj.getObjectEncoded());
                update.setObject(6, obj.getProviderURI());
                update.setObject(7, obj.getRelatedLink());
                update.setObject(8, obj.getSourceLink().getDomainId());
                update.setObject(9, obj.getSourceLink().getObjId());
                update.setObject(10, obj.getSourceLink().getObjectTypeId());
                update.setObject(11, obj.getTimestamp().getValue());

                update.setObject(12, newObjs.get(i).getObjectTypeId());
                update.setObject(13, newObjs.get(i).getDomainId());
                update.setObject(14, newObjs.get(i).getObjectId());
                update.addBatch();

                // Flush every 1k objects...
                if (i != 0) {
                    if ((i % 1000) == 0) {
                        TransactionsProcessor.LOGGER.log(Level.FINE, "Flushing the data after 1000 serial stores...");

                        update.executeBatch();
                        update.clearBatch();
                    }
                }
            }

            update.executeBatch();
            c.setAutoCommit(true);
        } catch (SQLException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }
        this.transactionsProcessor.dbBackend.getAvailability().release();

        if (publishEvents != null) {
            this.transactionsProcessor.generalExecutor.submit(publishEvents);
        }
    }
}