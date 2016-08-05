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
package esa.mo.com.impl.provider;

import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ccsds.moims.mo.com.structures.ObjectType;
import org.ccsds.moims.mo.mal.structures.IdentifierList;

/**
 *
 *
 */
public class ArchiveFastObjId {

    private final HashMap<Key, Long> fastID;
    private final Semaphore semaphore = new Semaphore(1);

    public ArchiveFastObjId() {
        this.fastID = new HashMap<Key, Long>();
    }

    protected void lock(){
        try {
            this.semaphore.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(ArchiveFastObjId.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    protected void unlock(){
            this.semaphore.release();
    }

    protected Long newUniqueID(final ObjectType objectTypeId, final IdentifierList domain) {

        Long objId = (this.getCurrentID(objectTypeId, domain));
        if (objId == null){
            return null;
        }
        
        objId++;
        this.setUniqueID(objectTypeId, domain, objId);

        return objId;
    }
    
    protected void setUniqueIdIfLatest(final ObjectType objectTypeId, final IdentifierList domain, final Long objId){
        final Key key = new Key(objectTypeId, domain);
        final Long currentObjId = this.getCurrentID(objectTypeId, domain);

        if (currentObjId == null){
            this.fastID.put(key, objId);
            return;
        }
        
        if (objId > currentObjId){
            this.fastID.put(key, objId);
        }
    }

    protected void setUniqueID(final ObjectType objectTypeId, final IdentifierList domain, final Long objId) {
        Key key = new Key(objectTypeId, domain);
        this.fastID.put(key, objId);
    }

    protected void delete(final ObjectType objectTypeId, final IdentifierList domain) {
        Key key = new Key(objectTypeId, domain);
        this.fastID.remove(key);
    }

    private Long getCurrentID(final ObjectType objectTypeId, final IdentifierList domain) {
        final Key key = new Key(objectTypeId, domain);
        final Long objId = this.fastID.get(key);
        
        if (objId == null)
            return null;

        return objId;
    }

    /*
    private boolean exists(final ObjectType objectTypeId, final IdentifierList domain) {
        Key key = new Key(objectTypeId, domain);
        return (this.fastID.get(key) != null);
    }
    */

    protected class Key {

        private final Long objectTypeId;
        private final String domainId;

        protected Key(final ObjectType objectTypeId, final IdentifierList domain) {
            this.objectTypeId = HelperCOM.generateSubKey(objectTypeId);
            this.domainId = HelperMisc.domain2domainId(domain);
        }

        private Long getObjTypeId() {
            return this.objectTypeId;
        }

        private String getDomainId() {
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
