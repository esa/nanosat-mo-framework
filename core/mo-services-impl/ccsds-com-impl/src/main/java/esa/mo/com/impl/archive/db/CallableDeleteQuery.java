package esa.mo.com.impl.archive.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.QueryFilter;
import org.ccsds.moims.mo.mal.structures.IntegerList;

public class CallableDeleteQuery extends CallableGenericQuery<Integer> {
    public static Logger LOGGER = Logger.getLogger(CallableDeleteQuery.class.getName());

    public CallableDeleteQuery(final TransactionsProcessor transactionsProcessor, final IntegerList objTypeIds,
                               final ArchiveQuery archiveQuery, final IntegerList domainIds, final Integer providerURIId, final Integer networkId,
                               final SourceLinkContainer sourceLink, final QueryFilter filter) {
        super(transactionsProcessor, objTypeIds, archiveQuery, domainIds, providerURIId, networkId, sourceLink, filter);
    }

    @Override
    protected Integer innerCall(final String queryString) {
        try {
            final Connection c = this.transactionsProcessor.dbBackend.getConnection();
            final Statement query = c.createStatement();
            final Integer result = query.executeUpdate(queryString);
            return result;
        } catch (final SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    protected String assembleQueryPrefix(final String fieldsList) {
        return "DELETE FROM COMObjectEntity ";
    }

}
