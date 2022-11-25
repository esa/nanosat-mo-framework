package esa.mo.com.impl.archive.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;

import org.ccsds.moims.mo.com.archive.structures.ArchiveQuery;
import org.ccsds.moims.mo.com.archive.structures.QueryFilter;
import org.ccsds.moims.mo.mal.structures.IntegerList;

import esa.mo.com.impl.archive.entities.COMObjectEntity;

public class CallableSelectQuery extends CallableGenericQuery<ArrayList<COMObjectEntity>> {

    public CallableSelectQuery(TransactionsProcessor transactionsProcessor, IntegerList objTypeIds,
        ArchiveQuery archiveQuery, IntegerList domainIds, Integer providerURIId, Integer networkId,
        SourceLinkContainer sourceLink, QueryFilter filter) {
        super(transactionsProcessor, objTypeIds, archiveQuery, domainIds, providerURIId, networkId, sourceLink, filter);
    }

    @Override
    protected ArrayList<COMObjectEntity> innerCall(String queryString) {
        ArrayList<COMObjectEntity> perObjs = new ArrayList<>();
        try {
            Connection c = this.transactionsProcessor.dbBackend.getConnection();
            Statement query = c.createStatement();
            ResultSet rs = query.executeQuery(queryString);

            while (rs.next()) {
                perObjs.add(new COMObjectEntity((Integer) rs.getObject(1), (Integer) rs.getObject(2),
                    TransactionsProcessor.convert2Long(rs.getObject(3)), TransactionsProcessor.convert2Long(rs
                        .getObject(4)), (Integer) rs.getObject(5), (Integer) rs.getObject(6), new SourceLinkContainer(
                            (Integer) rs.getObject(7), (Integer) rs.getObject(8), TransactionsProcessor.convert2Long(rs
                                .getObject(9))), TransactionsProcessor.convert2Long(rs.getObject(10)), (byte[]) rs
                                    .getObject(11)));
            }
        } catch (SQLException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }
        return perObjs;
    }

    @Override
    protected String assembleQueryPrefix(String fieldsList) {
        return "SELECT " + fieldsList + " FROM COMObjectEntity ";
    }

}
