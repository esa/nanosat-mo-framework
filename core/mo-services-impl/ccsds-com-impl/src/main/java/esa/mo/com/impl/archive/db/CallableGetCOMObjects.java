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

import esa.mo.com.impl.archive.entities.COMObjectEntity;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.ccsds.moims.mo.mal.structures.LongList;

public final class CallableGetCOMObjects implements Callable<List<COMObjectEntity>> {

    // for sqlite. %s will be replaced with proper ids.
    private final static String SELECT_COM_OBJECTS = "SELECT objectTypeId, domainId, objId, " +
        "timestampArchiveDetails, providerURI, network, sourceLinkObjectTypeId, " +
        "sourceLinkDomainId, sourceLinkObjId, relatedLink, objBody " + "FROM COMObjectEntity " +
        "WHERE ((objectTypeId = %s) AND (domainId = %s) AND (objId in (%s)))";
    private final TransactionsProcessor transactionsProcessor;
    private final LongList ids;
    private final Integer domainId;
    private final Integer objTypeId;

    CallableGetCOMObjects(TransactionsProcessor transactionsProcessor, LongList ids, Integer domainId,
        Integer objTypeId) {
        this.transactionsProcessor = transactionsProcessor;
        this.ids = ids;
        this.domainId = domainId;
        this.objTypeId = objTypeId;
    }

    @Override
    public List<COMObjectEntity> call() {
        try {
            this.transactionsProcessor.dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            TransactionsProcessor.LOGGER.log(Level.SEVERE, null, ex);
        }

        List<COMObjectEntity> perObjs = new ArrayList<>();
        Connection c = this.transactionsProcessor.dbBackend.getConnection();

        try {
            ResultSet rs;
            if (this.transactionsProcessor.dbBackend.isPostgres) {
                // Array-bind is supported in Postgres
                PreparedStatement stmt = this.transactionsProcessor.dbBackend.getPreparedStatements()
                        .getSelectCOMObjects();
                Array idsArray = c.createArrayOf("BIGINT", ids.toArray());
                stmt.setInt(1, objTypeId);
                stmt.setInt(2, domainId);
                stmt.setArray(3, idsArray);
                rs = stmt.executeQuery();
            } else {
                String parameters = ids.stream().map(Object::toString).collect(Collectors.joining(", "));
                String query = String.format(SELECT_COM_OBJECTS, objTypeId.toString(), domainId.toString(), parameters);
                rs = c.createStatement().executeQuery(query);
            }

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

        this.transactionsProcessor.dbBackend.getAvailability().release();
        return perObjs;
    }
}
