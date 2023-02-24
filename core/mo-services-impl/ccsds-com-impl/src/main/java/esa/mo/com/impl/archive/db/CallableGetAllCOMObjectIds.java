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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.structures.LongList;

final class CallableGetAllCOMObjectIds implements Callable<LongList> {

    private final TransactionsProcessor transactionsProcessor;
    private final Integer domainId;
    private final Integer objTypeId;

    CallableGetAllCOMObjectIds(TransactionsProcessor transactionsProcessor, Integer domainId, Integer objTypeId) {
        this.transactionsProcessor = transactionsProcessor;
        this.domainId = domainId;
        this.objTypeId = objTypeId;
    }

    @Override
    public LongList call() {
        try {
            this.transactionsProcessor.dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }

        LongList objIds = new LongList();

        try {
            PreparedStatement stmt = this.transactionsProcessor.dbBackend.getPreparedStatements()
                .getSelectAllCOMObjects();
            stmt.setInt(1, objTypeId);
            stmt.setInt(2, domainId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                objIds.add(TransactionsProcessor.convert2Long(rs.getObject(1)));
            }
        } catch (SQLException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }

        this.transactionsProcessor.dbBackend.getAvailability().release();
        return objIds;
    }
}
