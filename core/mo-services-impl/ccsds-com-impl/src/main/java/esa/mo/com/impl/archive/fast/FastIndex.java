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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains the set of different key fields that the database contains in its
 * dedicated table and avoids constant checking on it which makes things go much
 * faster.
 */
public abstract class FastIndex<T> {

    protected static final Logger LOGGER = Logger.getLogger(FastIndex.class.getName());
    protected final String QUERY_DELETE;
    protected final String QUERY_SELECT;
    protected final String QUERY_INSERT;
    protected final String CREATE_TABLE;

    protected PreparedStatement insertStmt;

    protected final DatabaseBackend dbBackend;
    protected AtomicInteger uniqueId = new AtomicInteger(0);
    protected HashMap<T, Integer> fastID;
    protected HashMap<Integer, T> fastIDreverse;

    public FastIndex(final DatabaseBackend dbBackend, String table) {
        this.fastID = new HashMap<>();
        this.fastIDreverse = new HashMap<>();
        this.dbBackend = dbBackend;

        QUERY_DELETE = "DELETE FROM " + table;
        QUERY_SELECT = "SELECT id, value FROM " + table;
        QUERY_INSERT = "INSERT INTO " + table + " (id, value) VALUES (?, ?)";
        CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table +
            " (id INTEGER NOT NULL, value VARCHAR, PRIMARY KEY (id))";
    }

    public synchronized void init() {
        // Retrieve all the ids and providerURIs from the Database
        try {
            dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        int max = 0;
        try {
            Connection c = dbBackend.getConnection();
            c.createStatement().execute(CREATE_TABLE);
            insertStmt = c.prepareStatement(QUERY_INSERT);
            ResultSet rs = c.createStatement().executeQuery(QUERY_SELECT);

            while (rs.next()) {
                Integer id = rs.getInt(1);
                T value = (T) rs.getObject(2);
                this.fastID.put(value, id);
                this.fastIDreverse.put(id, value);

                max = (id > max) ? id : max;
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        uniqueId = new AtomicInteger(max);
        dbBackend.getAvailability().release();
    }

    public synchronized boolean exists(final T value) {
        return (this.fastID.get(value) != null);
    }

    public synchronized boolean exists(final Integer key) {
        return (this.fastIDreverse.get(key) != null);
    }

    public synchronized void resetTable() {
        this.fastID = new HashMap<>();
        this.fastIDreverse = new HashMap<>();
        uniqueId = new AtomicInteger(0);

        try {
            Connection c = dbBackend.getConnection();
            c.createStatement().execute(QUERY_DELETE);
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public synchronized T getValue(final Integer key) throws Exception {
        final T value = this.fastIDreverse.get(key);

        if (value == null) {
            throw new Exception();
        }

        return value;
    }

    public synchronized Integer getKey(final T value) throws Exception {
        final Integer key = this.fastID.get(value);

        if (key == null) {
            throw new Exception();
        }

        return key;
    }

    public Integer addNewEntry(final T value) {
        final int key = uniqueId.incrementAndGet();
        this.fastID.put(value, key);
        this.fastIDreverse.put(key, value);

        try {
            dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        try {
            insertStmt.setObject(1, key);
            insertStmt.setObject(2, value);
            insertStmt.execute();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        dbBackend.getAvailability().release();
        return key;
    }

}
