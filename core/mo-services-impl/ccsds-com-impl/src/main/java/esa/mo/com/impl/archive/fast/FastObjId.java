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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Allows fast generation of object instance identifiers and only check the
 * database when it is necessary.
 */
public class FastObjId {

    private final DatabaseBackend dbBackend;
    private HashMap<Key, Long> fastID;

    public FastObjId(final DatabaseBackend dbBackend) {
        this.dbBackend = dbBackend;
        this.fastID = new HashMap<>();
    }

    public synchronized void resetFastIDs() {
        this.fastID = new HashMap<>();
    }

    private Long newUniqueID(final Integer objectTypeId, final Integer domain) {
        Long objId = (this.getCurrentID(objectTypeId, domain));
        if (objId == null) {
            return null;
        }

        objId++;
        this.setUniqueID(objectTypeId, domain, objId);

        return objId;
    }

    private void setUniqueIdIfLatest(final Integer objectTypeId, final Integer domain, final Long objId) {
        final Key key = new Key(objectTypeId, domain);
        final Long currentObjId = this.getCurrentID(objectTypeId, domain);

        if (currentObjId == null) {
            this.fastID.put(key, objId);
            return;
        }

        if (objId > currentObjId) {
            this.fastID.put(key, objId);
        }
    }

    private void setUniqueID(final Integer objectTypeId, final Integer domain, final Long objId) {
        final Key key = new Key(objectTypeId, domain);
        this.fastID.put(key, objId);
    }

    public synchronized void delete(final Integer objectTypeId, final Integer domain) {
        Key key = new Key(objectTypeId, domain);
        this.fastID.remove(key);
    }

    private Long getCurrentID(final Integer objectTypeId, final Integer domain) {
        final Key key = new Key(objectTypeId, domain);
        final Long objId = this.fastID.get(key);

        return (objId == null) ? null : objId;
    }

    private synchronized Long generateUniqueObjId(final Integer objectTypeId, final Integer domain) {
        // Did we request this objType+domain combination before?! If so, return the next value
        Long objId = this.newUniqueID(objectTypeId, domain);
        if (objId != null) {
            return objId;
        }

        // Well, if not then we must check if this combination already exists in the PU...
        try {
            dbBackend.getAvailability().acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            PreparedStatement select = dbBackend.getPreparedStatements().getSelectMaxObjId();
            select.setObject(1, objectTypeId);
            select.setObject(2, domain);
            ResultSet rs = select.executeQuery();

            while (rs.next()) {
                Long maxValue = TransactionsProcessor.convert2Long(rs.getObject(1));
                long value = (maxValue == null) ? (long) 0 : maxValue;
                this.setUniqueID(objectTypeId, domain, value);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, null, ex);
        }

        dbBackend.getAvailability().release();
        return this.newUniqueID(objectTypeId, domain);
    }

    public synchronized Long getUniqueObjId(final Integer objTypeId, final Integer domain, final Long objId) {
        if (objId == 0) { // requirement: 3.4.6.2.5
            return this.generateUniqueObjId(objTypeId, domain);
        } else {
            // Check if it is not greater than the current "fast" objId
            this.setUniqueIdIfLatest(objTypeId, domain, objId);
            return objId;
        }
    }

    private static class Key {

        private final Integer objectTypeId;
        private final Integer domainId;

        protected Key(final Integer objectTypeId, final Integer domain) {
            this.objectTypeId = objectTypeId;
            this.domainId = domain;
        }

        private Integer getObjTypeId() {
            return this.objectTypeId;
        }

        private Integer getDomainId() {
            return this.domainId;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Key)) {
                return false;
            }
            Key input = (Key) other;
            if (!input.getObjTypeId().equals(objectTypeId)) {
                return false;
            }
            return input.getDomainId().equals(domainId);
        }

        @Override
        public int hashCode() {
            return objectTypeId.hashCode() ^ domainId.hashCode();
        }

    }

}
