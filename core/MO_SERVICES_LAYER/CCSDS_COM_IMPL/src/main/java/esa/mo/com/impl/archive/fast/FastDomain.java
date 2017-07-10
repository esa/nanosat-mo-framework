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
import esa.mo.com.impl.archive.entities.DomainHolderEntity;
import esa.mo.com.impl.util.HelperCOM;
import esa.mo.helpertools.helpers.HelperMisc;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.Query;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.IntegerList;

/**
 *
 *
 */
public class FastDomain {

    private final static String QUERY_DELETE_DOMAIN = "DELETE FROM DomainHolderEntity";
    private final static String QUERY_SELECT_DOMAIN = "SELECT PU FROM DomainHolderEntity PU";
    private final DatabaseBackend dbBackend;
    private AtomicInteger uniqueId = new AtomicInteger(0);
    private HashMap<IdentifierList, Integer> fastID;
    private HashMap<Integer, IdentifierList> fastIDreverse;

    public FastDomain(final DatabaseBackend dbBackend) {
        this.fastID = new HashMap<IdentifierList, Integer>();
        this.fastIDreverse = new HashMap<Integer, IdentifierList>();
        this.dbBackend = dbBackend;
    }

    public synchronized void resetFastDomain() {
        this.fastID = new HashMap<IdentifierList, Integer>();
        this.fastIDreverse = new HashMap<Integer, IdentifierList>();
        uniqueId = new AtomicInteger(0);

        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().createQuery(QUERY_DELETE_DOMAIN).executeUpdate();
        dbBackend.getEM().getTransaction().commit();
    }

    public synchronized void init() {
        // Retrieve all the ids and domains from the Database
        dbBackend.createEntityManager();

        // Get All the domains available
        Query query = dbBackend.getEM().createQuery(QUERY_SELECT_DOMAIN);
        List resultList = query.getResultList();
        ArrayList<DomainHolderEntity> domainHolderEntities = new ArrayList<DomainHolderEntity>();
        domainHolderEntities.addAll(resultList);
        dbBackend.closeEntityManager();

        int max = 0;

        // Populate the variables on this class
        for (int i = 0; i < domainHolderEntities.size(); i++) {
            final IdentifierList domain = HelperMisc.domainId2domain(domainHolderEntities.get(i).getDomainString());
            final Integer id = domainHolderEntities.get(i).getId();
            this.fastID.put(domain, id);
            this.fastIDreverse.put(id, domain);

            if (id > max) {
                max = id; // Get the maximum value
            }
        }

        uniqueId = new AtomicInteger(max);
    }

    public synchronized boolean exists(final IdentifierList domain) {
        return (this.fastID.get(domain) != null);
    }

    public synchronized boolean exists(final Integer domainId) {
        return (this.fastIDreverse.get(domainId) != null);
    }

    private Integer addNewDomain(final IdentifierList domain) {
        final int domainId = uniqueId.incrementAndGet();
        this.fastID.put(domain, domainId);
        this.fastIDreverse.put(domainId, domain);

        dbBackend.createEntityManager();

        // Create Entity
        final DomainHolderEntity domainEntity = new DomainHolderEntity(domainId, HelperMisc.domain2domainId(domain));

        // Add it to the table
        dbBackend.getEM().getTransaction().begin();
        dbBackend.getEM().persist(domainEntity);
        dbBackend.getEM().getTransaction().commit();
        dbBackend.closeEntityManager();

        return domainId;
    }

    public synchronized Integer getDomainId(final IdentifierList domain) {
        final Integer id = this.fastID.get(domain);
        return (id == null) ? this.addNewDomain(domain) : id;
    }

    public synchronized IntegerList getDomainIds(final IdentifierList inputDomain) {
        final IntegerList ids = new IntegerList();

        if(inputDomain == null){
            return ids;
        }
        
        if (HelperCOM.domainContainsWildcard(inputDomain)) {
            for (Map.Entry<IdentifierList, Integer> entry : this.fastID.entrySet()) {
                try {
                    if (HelperCOM.domainMatchesWildcardDomain(entry.getKey(), inputDomain)) {  // Does the domain matches the wildcard?
                        ids.add(entry.getValue());
                    }
                } catch (Exception ex) {
                    Logger.getLogger(FastDomain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            final Integer id = this.fastID.get(inputDomain);
            
            if (id == null) {
                ids.add(this.addNewDomain(inputDomain));
            } else {
                ids.add(id);
            }
        }

        return ids;
    }

    public synchronized IdentifierList getDomain(final Integer id) throws Exception {
        final IdentifierList domain = this.fastIDreverse.get(id);

        if (domain == null) {
            throw new Exception();
        }

        return domain;
    }

}
