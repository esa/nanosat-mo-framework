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

    public CallableDeleteQuery(TransactionsProcessor transactionsProcessor, IntegerList objTypeIds,
            ArchiveQuery archiveQuery, IntegerList domainIds, Integer providerURIId, Integer networkId,
            SourceLinkContainer sourceLink, QueryFilter filter) {
        super(transactionsProcessor, objTypeIds, archiveQuery, domainIds, providerURIId, networkId, sourceLink, filter);
    }

    @Override
    protected Integer innerCall(String queryString) {
        try {
            Connection c = this.transactionsProcessor.dbBackend.getConnection();
            Statement query = c.createStatement();
            Integer result = query.executeUpdate(queryString);
            return result;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    @Override
    protected String assembleQueryPrefix(String fieldsList) {
        return "DELETE FROM COMObjectEntity ";
    }

}
