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
import esa.mo.com.impl.archive.db.TransactionsProcessor;
import esa.mo.com.impl.provider.ArchiveManager;
import esa.mo.com.impl.util.HelperCOM;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.IntegerList;

/**
 * Holds the set of object types that the database contains in its dedicated table
 * and avoids constant checking on it which makes things go much faster.
 */
public class FastObjectType extends FastIndex<Long> {

    private final static String TABLE_NAME = "FastObjectType";

    public FastObjectType(final DatabaseBackend dbBackend) {
        super(dbBackend, TABLE_NAME);
    }

    @Override
    public synchronized void init() {
        // Retrieve all the ids and objectTypes from the Database
        try {
            dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FastObjectType.class.getName()).log(Level.SEVERE, null, ex);
        }

        int max = 0;
        try {
            Connection c = dbBackend.getConnection();
            Statement query = c.createStatement();
            query.execute("CREATE TABLE IF NOT EXISTS " + TABLE_NAME +
                " (id INTEGER NOT NULL, value BIGINT, PRIMARY KEY (id))");
            insertStmt = c.prepareStatement(QUERY_INSERT);
            ResultSet rs = query.executeQuery(QUERY_SELECT);

            while (rs.next()) {
                Integer id = rs.getInt(1);
                Long objType = TransactionsProcessor.convert2Long(rs.getObject(2));
                this.fastID.put(objType, id);
                this.fastIDreverse.put(id, objType);

                max = (id > max) ? id : max;
            }
        } catch (SQLException ex) {
            Logger.getLogger(FastObjectType.class.getName()).log(Level.SEVERE, null, ex);
        }

        uniqueId = new AtomicInteger(max);
        dbBackend.getAvailability().release();
    }

    public synchronized Integer getObjectTypeId(final ObjectType objectType) {
        final Long longObjType = HelperCOM.generateSubKey(objectType);
        final Integer id = this.fastID.get(longObjType);
        return (id == null) ? this.addNewEntry(longObjType) : id;
    }

    public synchronized IntegerList getObjectTypeIds(final ObjectType objectType) {
        final IntegerList ids = new IntegerList();

        if (ArchiveManager.objectTypeContainsWildcard(objectType)) {
            final long bitMask = objectType2Mask(objectType);
            final long objTypeId = HelperCOM.generateSubKey(objectType);

            long tmpObjectTypeId;
            long objTypeANDed;
            for (Map.Entry<Long, Integer> entry : this.fastID.entrySet()) {
                try {
                    tmpObjectTypeId = HelperCOM.generateSubKey(this.getObjectType(entry.getValue()));
                    objTypeANDed = (tmpObjectTypeId & bitMask);
                    if (objTypeANDed == objTypeId) { // Comparison
                        ids.add(entry.getValue());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FastObjectType.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            final Long longObjType = HelperCOM.generateSubKey(objectType);
            final Integer id = this.fastID.get(longObjType);
            ids.add((id == null) ? this.addNewEntry(longObjType) : id);
        }

        return ids;
    }

    public synchronized ObjectType getObjectType(final Integer id) throws Exception {
        final Long objectType = this.fastIDreverse.get(id);

        if (objectType == null) {
            throw new Exception();
        }

        return HelperCOM.objectTypeId2objectType(objectType);
    }

    private static Long objectType2Mask(final ObjectType objType) {
        long areaVal = (objType.getArea().getValue() == 0) ? (long) 0 : (long) 0xFFFF;
        long serviceVal = (objType.getService().getValue() == 0) ? (long) 0 : (long) 0xFFFF;
        long versionVal = (objType.getVersion().getValue() == 0) ? (long) 0 : (long) 0xFF;
        long numberVal = (objType.getNumber().getValue() == 0) ? (long) 0 : (long) 0xFFFF;

        return (areaVal << 48 | serviceVal << 32 | versionVal << 24 | numberVal);
    }

}
