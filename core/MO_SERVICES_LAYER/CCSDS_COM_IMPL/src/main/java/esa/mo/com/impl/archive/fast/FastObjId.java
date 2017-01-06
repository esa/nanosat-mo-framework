/* ----------------------------------------------------------------------------
 * Copyright (C) 2015      European Space Agency
 *                         European Space Operations Centre
 *                         Darmstadt
 *                         Germany
 * ----------------------------------------------------------------------------
 * System                : ESA NanoSat MO Framework
 * ----------------------------------------------------------------------------
 * Licensed under the European Space Agency Public License, Version 2.0
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
import java.util.HashMap;
import javax.persistence.Query;
import org.ccsds.moims.mo.com.structures.ObjectType;

/**
 *
 *
 */
public class FastObjId {

    private final static String QUERY_FIND_MAX = "SELECT MAX(PU.objId) FROM COMObjectEntity PU WHERE PU.objectTypeId=:objectTypeId AND PU.domainId=:domainId";
    private final static String FIELD_OBJTYPEID = "objectTypeId";
    private final static String FIELD_DOMAINID = "domainId";
    private final DatabaseBackend dbBackend;
    private HashMap<Key, Long> fastID;

    public FastObjId(final DatabaseBackend dbBackend) {
        this.dbBackend = dbBackend;
        this.fastID = new HashMap<Key, Long>();
    }

    public synchronized void resetFastIDs() {
        this.fastID = new HashMap<Key, Long>();
    }

    private Long newUniqueID(final ObjectType objectTypeId, final Integer domain) {
        Long objId = (this.getCurrentID(objectTypeId, domain));
        if (objId == null) {
            return null;
        }

        objId++;
        this.setUniqueID(objectTypeId, domain, objId);

        return objId;
    }
    
    private void setUniqueIdIfLatest(final ObjectType objectTypeId, final Integer domain, final Long objId) {
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

    private void setUniqueID(final ObjectType objectTypeId, final Integer domain, final Long objId) {
        final Key key = new Key(objectTypeId, domain);
        this.fastID.put(key, objId);
    }

    public synchronized void delete(final ObjectType objectTypeId, final Integer domain) {
        Key key = new Key(objectTypeId, domain);
        this.fastID.remove(key);
    }

    private Long getCurrentID(final ObjectType objectTypeId, final Integer domain) {
        final Key key = new Key(objectTypeId, domain);
        final Long objId = this.fastID.get(key);

        return (objId == null) ? null : objId;
    }
    
    private synchronized Long generateUniqueObjId(final ObjectType objectType, final Integer domain) {
        // Did we request this objType+domain combination before?! If so, return the next value
        Long objId = this.newUniqueID(objectType, domain);
        if (objId != null) {
            return objId;
        }

        // Well, if not then we must check if this combination already exists in the PU...
        dbBackend.createEntityManager();
        Query query = dbBackend.getEM().createQuery(QUERY_FIND_MAX);
        query.setParameter(FIELD_OBJTYPEID, HelperCOM.generateSubKey(objectType));
        query.setParameter(FIELD_DOMAINID, domain);
        Long maxValue = (Long) query.getSingleResult();
        dbBackend.closeEntityManager();

        // If the object does not exist in PU, set as 0
        long value = (maxValue == null) ? (long) 0 : maxValue;
        this.setUniqueID(objectType, domain, value);

        return this.newUniqueID(objectType, domain);
    }
    
    public synchronized Long getUniqueObjId(final ObjectType objType, final Integer domain, final Long objId) {
        if (objId == 0) { // requirement: 3.4.6.2.5
            return this.generateUniqueObjId(objType, domain);
        } else {
            this.setUniqueIdIfLatest(objType, domain, objId); // Check if it is not greater than the current "fast" objId
            return objId;
        }
    }

    private class Key {

        private final Long objectTypeId;
        private final Integer domainId;

        protected Key(final ObjectType objectTypeId, final Integer domain) {
            this.objectTypeId = HelperCOM.generateSubKey(objectTypeId);
            this.domainId = domain;
        }

        private Long getObjTypeId() {
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
