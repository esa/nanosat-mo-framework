package esa.mo.com.impl.archive.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.logging.Level;

import org.ccsds.moims.mo.mal.structures.LongList;

final class CallableGetAllCOMObjectIds implements Callable<LongList> {
    /**
 *
 */
private final TransactionsProcessor transactionsProcessor;
    private final Integer domainId;
    private final Integer objTypeId;

    CallableGetAllCOMObjectIds(final TransactionsProcessor transactionsProcessor, final Integer domainId, final Integer objTypeId) {
        this.transactionsProcessor = transactionsProcessor;
        this.domainId = domainId;
        this.objTypeId = objTypeId;
    }

    @Override
    public LongList call() {
        try {
            this.transactionsProcessor.dbBackend.getAvailability().acquire();
        } catch (final InterruptedException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }

        final LongList objIds = new LongList();

        try {
            final PreparedStatement stmt = this.transactionsProcessor.dbBackend.getPreparedStatements().getSelectAllCOMObjects();
            stmt.setInt(1, objTypeId);
            stmt.setInt(2, domainId);
            final ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                objIds.add(TransactionsProcessor.convert2Long(rs.getObject(1)));
            }
        } catch (final SQLException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }

        this.transactionsProcessor.dbBackend.getAvailability().release();
        return objIds;
    }
}