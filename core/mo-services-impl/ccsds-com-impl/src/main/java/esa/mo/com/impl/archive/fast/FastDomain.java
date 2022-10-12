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
package esa.mo.com.impl.archive.fast;

import esa.mo.com.impl.archive.db.DatabaseBackend;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperMisc;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;

/**
 * Holds the set of domains that the database contains in its dedicated table
 * and avoids constant checking on it which makes things go much faster.
 */
public class FastDomain extends FastIndex<IdentifierList> {

    private final static String TABLE_NAME = "FastDomain";

    public FastDomain(final DatabaseBackend dbBackend) {
        super(dbBackend, TABLE_NAME);
    }

    @Override
    public synchronized void init() {
        // Retrieve all the ids and domains from the Database
        try {
            dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, null, ex);
        }

        int max = 0;

        Connection c = dbBackend.getConnection();

        try {
            Statement query = c.createStatement();
            query.execute(CREATE_TABLE);
            insertStmt = c.prepareStatement(QUERY_INSERT);
            ResultSet rs = query.executeQuery(QUERY_SELECT);

            while (rs.next()) {
                Integer id = rs.getInt(1);
                IdentifierList domain = HelperMisc.domainId2domain(rs.getString(2));
                this.fastID.put(domain, id);
                this.fastIDreverse.put(id, domain);

                max = (id > max) ? id : max;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, null, ex);
        }

        uniqueId = new AtomicInteger(max);
        dbBackend.getAvailability().release();
    }

    private Integer addNewDomain(final IdentifierList domain) {
        final int domainId = uniqueId.incrementAndGet();
        this.fastID.put(domain, domainId);
        this.fastIDreverse.put(domainId, domain);

        try {
            dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            insertStmt.setObject(1, domainId);
            insertStmt.setObject(2, HelperMisc.domain2domainId(domain));
            insertStmt.execute();
        } catch (SQLException ex) {
            Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, null, ex);
        }

        dbBackend.getAvailability().release();
        return domainId;
    }

    public synchronized Integer getDomainId(final IdentifierList domain) {
        final Integer id = this.fastID.get(domain);
        return (id == null) ? this.addNewDomain(domain) : id;
    }

    public synchronized IntegerList getDomainIds(final IdentifierList inputDomain) {
        final IntegerList ids = new IntegerList();

        if (inputDomain == null) {
            return ids;
        }

        if (HelperCOM.domainContainsWildcard(inputDomain)) {
            for (Map.Entry<IdentifierList, Integer> entry : this.fastID.entrySet()) {
                try {
                    // Does the domain matches the wildcard?
                    if (HelperCOM.domainMatchesWildcardDomain(entry.getKey(), inputDomain)) {
                        ids.add(entry.getValue());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            final Integer id = this.fastID.get(inputDomain);
            ids.add((id == null) ? this.addNewDomain(inputDomain) : id);
        }

        return ids;
    }

    public synchronized IdentifierList getDomain(Integer key) throws Exception {
        return super.getValue(key);
    }

}
